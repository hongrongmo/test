package org.ei.dataloading.upt.loadtime.vtw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.ei.dataloading.cafe.ReceiveAmazonSQSMessage;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;




/**
 * @author TELEBH
 * @Date: 09/16/2016
 * @Description: Backup Messages in VTW_Patents-dev:  
 * read VTW Message, parse it, archive it in Oracle table for later process in case 
 * there is a large flow of messages to read for later process
 * 
 * NOTE:
 * 	Each SQS message should be processed once, Amazon SQS distributed nature may result in Message deuplication, so the our application should handle this
 *  Each SQS message contains a unique message "@id", so need to set unique index on this field in oracle table to avoid duplication. 
 */

public class ArchiveVTWPatentRefeed {


	// Visibility time-out for the queue. It must match to the one set for the queue for this example to work.
	private static final long TIME_OUT_SECONDS = 30;
	private static final int MESSAGE_VISIBILITY_TIME_OUT_SECONDS = 1200;   // 20 minutes to give time upload the Asset/File to S3 bucket
	private static final int NUM_OF_MESSAGES_TO_FETCH = 10;

	private final static Logger logger = Logger.getLogger(ArchiveVTWPatentRefeed.class);

	private static int numberOfRuns=0;
	private static String queueName = "acc-contributor-event-queue-EV";
	private static String sqlldrFileName = null;
	static int loadNumber = 0;
	static int recsPerZipFile = 20000;

	DateFormat dateFormat,msgSentDateFormat;
	private String filename;
	private PrintWriter out;
	public static final char FIELDDELIM = '\t';
	StringBuffer recordBuf = null;

	private static AmazonSQS sqs;
	private static String myQueueUrl;



	private ReceiveAmazonSQSMessage obj = null;   // for parsing/checking Message Metadata
	private ReceiveMessageResult receiveMessageResult = null;
	private DeleteMessageRequest deleteRequest = null;
	private String messageBody;
	private Map<String,String> msgAttributes= new HashMap<String,String>();


	// get the list of Patent-Ids; with their signedAssetURL if any;  to download 
	private static Map<String,String> patentIds = new LinkedHashMap<String,String>();


	public static void main(String[] args) throws Exception {

		if(args[0] !=null)
		{
			numberOfRuns = Integer.parseInt(args[0]);
		}
		if(args[1] !=null)
		{
			queueName = args[1];
		}
		if(args.length >4)
		{
			if(args[2] !=null)
			{
				sqlldrFileName = args[2];
			}

			if(args[3] !=null)
			{
				if(Pattern.matches("^\\d*$", args[3]))
				{
					loadNumber = Integer.parseInt(args[3]);
				}
				else
				{
					System.out.println("loadNumber has wrong format");
					System.exit(1);
				}
			}
			if(args[4] !=null)
			{
				recsPerZipFile = Integer.parseInt(args[4]);
				System.out.println("Number of Keys per ZipFile: " + recsPerZipFile);
			}
		}
		else
		{
			System.out.println("not enough parameters!");
			System.exit(1);
		}

		ArchiveVTWPatentRefeed archiveVtwPatentRefeed = new ArchiveVTWPatentRefeed();

		// access VTW QUEUE 

		archiveVtwPatentRefeed.begin();
		archiveVtwPatentRefeed.SQSCreationAndSetting();
		archiveVtwPatentRefeed.end();

		// archive SQS messages to DB  
		archiveVtwPatentRefeed.loadToDB();		// comment out only in testing, UnComment when in Prod

		if(!(patentIds.isEmpty()))
		{
			VTWSearchAPI vtwSearchAPI = new VTWSearchAPI(loadNumber);
			vtwSearchAPI.downloadPatentMetadata(patentIds);
		}


		//Zip downloaded files (each in it's corresponding dir)
		archiveVtwPatentRefeed.zipDownloads();
	}

	// create text file to hold original message & partial parsed fields
	public void begin()
	{
		dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");		 
		Date date = new Date();

		String currDir = System.getProperty("user.dir");
		String root= currDir+"/sqsarchive";
		File sqsArchiveDir = new File (root);
		if(!sqsArchiveDir.exists())
		{
			sqsArchiveDir.mkdir();
		}


		filename = sqsArchiveDir+"/"+queueName+"_feed-"+dateFormat.format(date)+".txt";
		//filename = sqsArchiveDir+"/UAT_feed-"+dateFormat.format(date)+".txt";
		try {
			out = new PrintWriter(new FileWriter(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Output Filename "+filename);

	}

	public void SQSCreationAndSetting() throws JMSException, InterruptedException
	{

		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (~/.aws/credentials).
		 */
		AWSCredentialsProvider credentials = null;
		try {
			//credentials = new EnvironmentVariableCredentialsProvider();   // for localhost
			credentials = new InstanceProfileCredentialsProvider();        // for dataloading EC2
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (~/.aws/credentials), and is in valid format.",
							e);
		}

		try {

			sqs = new AmazonSQSClient(credentials);
			Region euWest2 = Region.getRegion(Regions.EU_WEST_1);
			sqs.setRegion(euWest2);



			// AMazonSQS queue
			System.out.println("===========================================");
			System.out.println("Getting Started with Amazon SQS");
			System.out.println("===========================================\n");

			GetQueueUrlRequest request = new GetQueueUrlRequest().withQueueName(queueName)
					.withQueueOwnerAWSAccountId("790640479873");

			GetQueueUrlResult result = sqs.getQueueUrl(request);


			System.out.println("  QueueUrl: " + result.getQueueUrl());
			System.out.println();


			myQueueUrl = result.getQueueUrl();

			// Receive message & parse it 
			receiveMessage();


		}

		catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it " +
					"to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());

			//exit
			//System.exit(1);
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered " +
					"a serious internal problem while trying to communicate with SQS, such as not " +
					"being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
			//System.exit(1);
		}

	}


	private void receiveMessage() throws InterruptedException 
	{
		ChangeMessageVisibilityRequest msgVisibilityReq;
		String msgReciptHandle = null;

		obj = new ReceiveAmazonSQSMessage(loadNumber);
		msgSentDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl)
		.withVisibilityTimeout(MESSAGE_VISIBILITY_TIME_OUT_SECONDS)
		.withWaitTimeSeconds(10).withMaxNumberOfMessages(NUM_OF_MESSAGES_TO_FETCH)
		.withAttributeNames("SentTimestamp");



		// Receive messages
		System.out.println("Receiving messages from "+queueName+".\n");

		try
		{

			//while(true)
			for(int i = 0; i<numberOfRuns;i++)
			{				

				receiveMessageResult = sqs.receiveMessage(receiveMessageRequest);


				ArrayList<Message> messages = (ArrayList<Message>)receiveMessageResult.getMessages();
				System.out.println("MessagesList size: " + messages.size());


				if(messages.size() >0)
				{
					for(Message message:messages)
					{
						if (message == null) 
						{
							System.out.println("Message is empty!");
							break;
						} 
						else 
						{
							msgReciptHandle = message.getReceiptHandle();  // for deleting the message 
							messageBody = message.getBody();
							msgAttributes = message.getAttributes();

							recordBuf = new StringBuffer();


							// change message visibility timeout
							msgVisibilityReq = new ChangeMessageVisibilityRequest(myQueueUrl, msgReciptHandle, MESSAGE_VISIBILITY_TIME_OUT_SECONDS);
							

							//System.out.println("SQS Message: " +  messageBody);

							//parse SQS Message Fields& determine whether it is sent to "E-Village" message
							if(obj.ParseJsonSQSMessage(messageBody))   
							{
								
								//Patent XML FileName (Patent ID, i.e. EP1412238A1)
								if(obj.getMessageField("patentid") !=null)
								{
									recordBuf.append(obj.getMessageField("patentid"));
									
									// only download Patent of type EP/US
									
									if(obj.getMessageField("patentid").substring(0, 2).equalsIgnoreCase("US") || 
											obj.getMessageField("patentid").substring(0, 2).equalsIgnoreCase("EP"))
									patentIds.put(obj.getMessageField("patentid"), "");
									
									else
										System.out.println("Skip downloading Patent : "+ obj.getMessageField("patentid") + " of type: " + obj.getMessageField("patentid").substring(0, 2));
								}
								recordBuf.append(FIELDDELIM);


								//SignedAssetURL
								if(obj.getMessageField("signedAssetUrl") !=null)
								{
									recordBuf.append(obj.getMessageField("signedAssetUrl"));
									if(patentIds.containsKey(obj.getMessageField("patentid")))
									{
										patentIds.put(obj.getMessageField("patentid"), obj.getMessageField("signedAssetUrl"));
									}
								}

								recordBuf.append(FIELDDELIM);


								//signed Asset URL's Expiration Date
								if(obj.getMessageField("urlExpirationDate") !=null)
								{
									recordBuf.append(convertMillisecondsToFormattedDate(obj.getMessageField("urlExpirationDate")));
								}
								recordBuf.append(FIELDDELIM);
								
								
								/*
								//Message ID 
								if(obj.getMessageField("message_id") !=null)
								{
									recordBuf.append(obj.getMessageField("message_id"));
								}
								recordBuf.append(FIELDDELIM);

								//message Type
								if(obj.getMessageField("message_type") !=null)
								{
									recordBuf.append(obj.getMessageField("message_type"));
								}
								recordBuf.append(FIELDDELIM);*/

								//message To
								if(obj.getMessageField("message_to") !=null)
								{
									recordBuf.append(obj.getMessageField("message_to"));
								}
								recordBuf.append(FIELDDELIM);

								/*//eventNotification ID
								if(obj.getMessageField("event_id") !=null)
								{
									recordBuf.append(obj.getMessageField("event_id"));
								}

								else
								{
									//ServiceCall ID
									if(obj.getMessageField("service_id") !=null)
									{
										recordBuf.append(obj.getMessageField("service_id"));
									}
								}

								recordBuf.append(FIELDDELIM);
								 */
								//Resource (URL containing Patent ID  and Generation# [i.e. http://acc.vtw.elsevier.com/content/pat/EP1412238A1/10] )
								if(obj.getMessageField("resource") !=null)
								{
									recordBuf.append(obj.getMessageField("resource"));
								}
								recordBuf.append(FIELDDELIM);



								//Status of download (succeed or failed)
								if(obj.getMessageField("status") !=null)
								{
									recordBuf.append(obj.getMessageField("status"));
								}
								recordBuf.append(FIELDDELIM);


								//Archive_Date, Date when message was read from SQS & archived
								recordBuf.append(msgSentDateFormat.format(new Date()));
								recordBuf.append(FIELDDELIM);


								//Body
								recordBuf.append(messageBody);
								recordBuf.append(FIELDDELIM);


								//Sent (the time when the message was sent to the queue )
								if(msgAttributes !=null && ! (msgAttributes.isEmpty()))
								{	
									recordBuf.append(msgSentDateFormat.format(new Date(Long.parseLong(msgAttributes.get("SentTimestamp")))).toString());
								}



								// write the message to out file
								out.println(recordBuf.toString().trim());
							}

							// delete the message
							deleteMessage(msgReciptHandle);
						}
					}
				}
				else
				{
					System.out.println("Queue is empty!");
					System.out.println("No Messages were recived at Iteration #: " + i + " Wait for " + TIME_OUT_SECONDS + " seconds, Skip to Next iteration");
					// Wait for for few seconds before next run
					System.out.println("Waiting for visibility timeout...");
					Thread.sleep(TimeUnit.SECONDS.toMillis(TIME_OUT_SECONDS));

				}
			}
		}
		catch(Exception sqsex)
		{
			System.err.println("Error receiving from SQS: " + sqsex.getMessage());
			sqsex.printStackTrace();
		}

	}

	private void deleteMessage(String messageHandle)
	{
		if(messageHandle !=null && messageHandle.length() >0)
		{
			System.out.println("Deleting a message: " + messageHandle);
			deleteRequest = new DeleteMessageRequest(myQueueUrl, messageHandle);
			sqs.deleteMessage(deleteRequest);
		}
	}


	public void end()
	{
		if(out !=null)
		{
			try
			{
				out.flush();
				out.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	//HH 09/20/2016 Convert Milliseconds to formatted date
	public String convertMillisecondsToFormattedDate(String milliSeconds)
	{
		Long epoch = Long.parseLong(milliSeconds) * 1000;
		Calendar calendar = new java.util.GregorianCalendar(java.util.TimeZone.getTimeZone("US/Central"));
		calendar.setTimeInMillis(epoch);
		return msgSentDateFormat.format(calendar.getTime());
	}

	public void loadToDB()
	{
		try {

			File f = new File(filename);
			if(!f.exists())
			{
				System.out.println("datafile: "+filename+" does not exists");
				System.exit(1);
			}


			System.out.println("sql loader file "+filename+" created;");
			System.out.println("about to load data file "+filename);
			System.out.println("press enter to continue");
			//System.in.read();
			Thread.currentThread().sleep(1000);

			Runtime r = Runtime.getRuntime();

			Process p = r.exec("./"+sqlldrFileName+" "+filename);
			int t = p.waitFor();
			System.out.println("Waited: " + t + " seconds for the sqlldr to complete");

		} 
		catch (Exception e) {

			e.printStackTrace();
		}

	}


	// 09/21/2016: Zip VTW XML files downloaded from VTW S3 bucket for later passing to converting program
	/**
	 * 
	 * @param recsPerZipFile
	 * @throws Exception
	 * hierarchy of downloaded VTW XML files (CurrDir -> loadnumberDir -> PatentID -> xml file) (i.e. /data/loading/ipdd -> 201639 -> EP2042829B1 -> AU2010281317A1.xml)
	 **/
	public void zipDownloads() throws Exception
	{
		int zipFileID = 1;
	    int curRecNum = 0;
		
		// get date&time in epoch format to be able to distinguish which file to send to converting, in case there are multiple files to convert
		DateFormat dateFormat = new SimpleDateFormat("E, MM/dd/yyyy-hh:mm:ss a");
		Date date = dateFormat.parse(dateFormat.format(new Date()));
		long epoch = date.getTime();
		
		
		SequenceGenerator seqNum = new SequenceGenerator();
		
		String currDir = System.getProperty("user.dir");
		File zipsDir = new File(currDir+"/zips");
		if(!(zipsDir.exists()))
		{
			zipsDir.mkdir();
		}

		zipsDir = new File(zipsDir + "/vtw");
		if(!(zipsDir.exists()))
		{
			zipsDir.mkdir();
		}

		zipsDir = new File(zipsDir+"/" +loadNumber);
		if(!(zipsDir.exists()))
		{
			zipsDir.mkdir();
		}


		File downDir = new File(currDir + "/raw_data/"+loadNumber);

		String[] xmlFiles = downDir.list();
		File[] xmlFilesToDelete = downDir.listFiles();
		byte[] buf = new byte[1024];


		// create zip files if any files were downloaded, otherwise no zip file should be created
		if(xmlFiles.length >0)
		{
			//String zipFileName = zipsDir + "/" + epoch + "_" + zipFileID + ".zip";
			String zipFileName = zipsDir + "/" + seqNum.nextloadNum() + seqNum.nextNum()+ ".zip";
			ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileName));

			for(int i=0; i<xmlFiles.length; i++)
			{
				// limit each single zip file to hold recsPerZipfile, otherwise split to multiple zip files
				if(curRecNum >= recsPerZipFile)
				{
					curRecNum = 0;
					outZip.close();

					zipFileID++;
					
					date = dateFormat.parse(dateFormat.format(new Date()));
					epoch = date.getTime();
					
					//zipFileName = zipsDir + "/" + epoch + "_" + zipFileID + ".zip";
					zipFileName = zipsDir + "/" + seqNum.nextloadNum() + seqNum.nextNum() + ".zip";
					outZip = new ZipOutputStream(new FileOutputStream(zipFileName));	
				}
				FileInputStream in = new FileInputStream(downDir + "/" + xmlFiles[i]);
				outZip.putNextEntry(new ZipEntry(xmlFiles[i]));

				int length;
				while((length = in.read(buf)) >0)
				{
					outZip.write(buf,0,length);
				}
				outZip.closeEntry();
				in.close();
				xmlFilesToDelete[i].delete();  // delete original xml file to save space

				++curRecNum;
			}
			outZip.close();
			downDir.delete();
		}
	}

	
	// generate unique ID for the zip filename (zip file should be a sequence number that can not be duplicate)
	/* for example in multithreading running of download VTW, each instance should create unique zip file name that does not override the other instance
	 * Note: With current patent app, the name of zip file must be a number and must be less than 2147483647, a max int value
	 */
	
	public class SequenceGenerator
	{
		volatile AtomicInteger sequenceLoadNum = new AtomicInteger();
		volatile AtomicInteger sequenceID = new AtomicInteger();
		public synchronized int nextloadNum()
		{
			int nextVal = sequenceLoadNum.incrementAndGet();
			System.out.println("Next LoadNumber Value is: " + nextVal);
			return nextVal;
			
		}
		
		public synchronized int nextNum()
		{
			int nextVal = sequenceID.incrementAndGet();
			System.out.println("Next Value is: " + nextVal);
			return nextVal;
			
		}
		
	}

}
