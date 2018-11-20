package vtw.threads.vtw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jms.JMSException;

import org.apache.log4j.Logger;

import vtw.threads.vtw.ArchiveVTWPatentRefeed.SequenceGenerator;
import vtw.VTWAssetAPI;

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
 * @Date: 03/31/2016
 * @Description: Read Messages in VTW_Patents-dev Queue & download files:  
 * read VTW Message, parse it, archive it in Oracle table for later process in case 
 * there is a large flow of messages to read for later process
 * 
 * NOTE:
 * This Class is exact same as "ArchiveVTWPatentRefeed.java" except it uses "AssetAPI" instead of "SearchAPI"
 *
 * 	Each SQS message should be processed once, Amazon SQS distributed nature may result in Message deuplication, so the our application should handle this
 *  Each SQS message contains a unique message "@id", so need to set unique index on this field in oracle table to avoid duplication. 
 */

public class ArchiveVTWPatentAsset implements Runnable{

	// Visibility time-out for the queue. It must match to the one set for the queue for this example to work.
	private static final long TIME_OUT_SECONDS = 30;
	private static final int MESSAGE_VISIBILITY_TIME_OUT_SECONDS = 1200;   // 20 minutes to give time upload the Asset/File to S3 bucket
	private static final int NUM_OF_MESSAGES_TO_FETCH = 10;

	private final static Logger logger = Logger.getLogger(ArchiveVTWPatentRefeed.class);

	private static int numberOfRuns=0;
	private static String queueName = "acc-contributor-event-queue-EV";		// old one "acc-contributor-event-queue-EV";
	private static String sqlldrFileName = null;
	static int loadNumber = 0;
	static int recsPerZipFile = 20000;
	static int recsPerSingleConnection = 1000;
	static String type = "forward";
	long epoch;

	private CountDownLatch latch;
	
	String threadName;
	Thread th;
	
	// epoch name list for individual dir for each thread in "raw_data" to zip to fix issue that unzip -tq raise soem errors for some zip files
	LinkedHashMap<String, Long> raw_Dir_Names = new LinkedHashMap<String,Long>();;


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


	private String zipFileName = "";

	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long midTime = System.currentTimeMillis();



	public ArchiveVTWPatentAsset()
	{

	}
	public ArchiveVTWPatentAsset (int loadnum, int numOfRecsPerZip, String msgType)
	{
		loadNumber = loadnum;
		recsPerZipFile = numOfRecsPerZip;
		type = msgType;
	}
	

	public ArchiveVTWPatentAsset(int numOfRuns, String qName, String sqlldr_fileName, int loadnum, int numOfRecsPerZip, int numOfRecsPerCon, String msgType,
			long rawDir, String thread_name, CountDownLatch latch)
	{

		numberOfRuns = numOfRuns;
		queueName = qName;
		sqlldrFileName = sqlldr_fileName;
		loadNumber = loadnum;
		recsPerZipFile = numOfRecsPerZip;
		recsPerSingleConnection = numOfRecsPerCon;
		type = msgType;
		epoch = rawDir;
		threadName = thread_name;
		System.out.println("Creating Thread: " + threadName);
		
		this.latch = latch;
		raw_Dir_Names.put(thread_name, rawDir);
	}


	// start point for running the thread (calls run () method)
	public void start()
	{
		System.out.println("Starting thread: " +  threadName);
		if(th ==null)
		{
			th = new Thread(this,threadName);
			th.start();
		}
	}

	// main logic of thread/ function to do
	public void run() {

		try
		{
			midTime = System.currentTimeMillis();
			endTime = System.currentTimeMillis();
			System.out.println("Time for finish reading input parameter & initialization "+(endTime-startTime)/1000.0+" seconds");
			System.out.println("total Time used "+(endTime-startTime)/1000.0+" seconds");


			// access VTW QUEUE 
			begin();
			SQSCreationAndSetting();
			end();

			// archive SQS messages to DB  
			//archiveVtwPatentRefeed.loadToDB();		// comment out only in testing, UnComment when in Prod

			if(!(patentIds.isEmpty()))
			{
				DateFormat dateFormat = new SimpleDateFormat("E, MM/dd/yyyy-hh:mm:ss a");
				Date date = dateFormat.parse(dateFormat.format(new Date()));
				long epoch = date.getTime();		// for US/EUP
				long wo_epoch = dateFormat.parse(dateFormat.format(new Date())).getTime();  // for WO


				
				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time before downloading files "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");


				/*VTWAssetAPI vtwAssetAPI = new VTWAssetAPI(Long.toString(epoch),recsPerSingleConnection);
				vtwAssetAPI.downloadPatent(patentIds,type);*/
				
				VTWAssetAPI vtwAssetAPI = new VTWAssetAPI(Long.toString(raw_Dir_Names.get(th.getName())),recsPerSingleConnection, th.getName());
				vtwAssetAPI.downloadPatent(patentIds, vtwAssetAPI.getInstance(), Long.toString(raw_Dir_Names.get(th.getName())), th.getName(), type);
				
				


				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time after downloading files "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");


				//HH 10/31/2018 move to initVtwThreads to be same as Prod

				/*//Zip downloaded files (each in it's corresponding dir)
				zipDownloads(loadNumber, Long.toString(epoch),type);*/


				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println("time after zip downloaded files "+(endTime-midTime)/1000.0+" seconds");
				System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
				
				latch.countDown();
				System.out.println("latch value now: " + latch);
			}
			else
			{
				System.out.println("PatentIds List is Empty, nothing to download");
			}


			Thread.sleep(1000);  // sleep for 1 second
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		} catch (Exception e)
		{

			e.printStackTrace();
		}

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
			credentials = new EnvironmentVariableCredentialsProvider();   // for localhost
			//credentials = new InstanceProfileCredentialsProvider();        // for dataloading EC2
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


			//String accountID="790640479873";		// UAT testing forward, but seems no longer exist
			//02/01/2018 use same new acctID of backfill as account ID "790640479873" seems no longer exists
			String accountID = "461549540087";		// UAT backfill 

			// AMazonSQS queue
			System.out.println("===========================================");
			System.out.println("Getting Started with Amazon SQS");
			System.out.println("===========================================\n");

			GetQueueUrlRequest request = new GetQueueUrlRequest().withQueueName(queueName)
					.withQueueOwnerAWSAccountId(accountID);

			GetQueueUrlResult result = sqs.getQueueUrl(request);


			System.out.println("  QueueUrl: " + result.getQueueUrl());
			System.out.println();


			myQueueUrl = result.getQueueUrl();


			midTime = endTime;
			endTime = System.currentTimeMillis();
			System.out.println("time for connecting to SQS Queue "+(endTime-midTime)/1000.0+" seconds");
			System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");



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
		int exitWaitingID = 0;

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
								//if(obj.ParseJsonSQSMessage(messageBody) && obj.getMessageField("patentid").contains("WO"))  //temp to only get WO messages
							{

								//Patent XML FileName (Patent ID, i.e. EP1412238A1)
								if(obj.getMessageField("patentid") !=null)
								{
									recordBuf.append(obj.getMessageField("patentid"));

									// on Monday 08/20/2018, start to download "CN, JP and KR" in addition to Patent of type EP/US/WO

									if(obj.getMessageField("patentid").substring(0, 2).equalsIgnoreCase("US") || 
											obj.getMessageField("patentid").substring(0, 2).equalsIgnoreCase("EP") ||
											obj.getMessageField("patentid").substring(0, 2).equalsIgnoreCase("WO")||
											obj.getMessageField("patentid").substring(0, 2).equalsIgnoreCase("CN")||
											obj.getMessageField("patentid").substring(0, 2).equalsIgnoreCase("JP")||
											obj.getMessageField("patentid").substring(0, 2).equalsIgnoreCase("KR"))
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

								//message To
								if(obj.getMessageField("message_to") !=null)
								{
									recordBuf.append(obj.getMessageField("message_to"));
								}
								recordBuf.append(FIELDDELIM);


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

					// exit after 2 attempts to get messages if queue is still empty

					if(exitWaitingID >2)
					{
						System.out.println("no Messages after " + (exitWaitingID+1) + " attempts, exit loop to onctinue rest of process...");
						break;
					}
					exitWaitingID ++;

				}
			}


			midTime = endTime;
			endTime = System.currentTimeMillis();
			System.out.println("time for reading, parsing and archiving" + numberOfRuns*10 + " messages from Queue " +(endTime-midTime)/1000.0+" seconds");
			System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");


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
	public synchronized void zipDownloads(int loadnumber, String downloadDirName, String msgType) throws Exception
	{
		int zipFileID = 1;
		int curRecNum = 0;
		String prefix="";
		File[] xmlFilesToDelete = null;

		ArrayList<String[]> allFilesList = new ArrayList<String[]>();

		// get date&time in epoch format to be able to distinguish which file to send to converting, in case there are multiple files to convert
		// as per Hongrong reported filename need to be a number & of certain length as it could not be processed if name > certain number, so used sequence number instead


		System.out.println("Zip downloaded files.....");

		// read latest zipfilename from zipFileNames file as the start point for Sequence generation
		readZipFileNameFromFile(loadNumber);

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



		File downDir = new File(currDir + "/raw_data/"+downloadDirName);


		// Filter files
		String[] files = downDir.list(new FilenameFilter() {


			public boolean accept(File dir, String name) {

				return (name.toLowerCase().startsWith("us") || name.toLowerCase().startsWith("ep") || name.toLowerCase().startsWith("wo"));
			}
		});

		System.out.println("Total US/EP/WO downloaded: " + files.length);
		allFilesList.add(files);

		
		// move WO to US & UP because it will be realeased & processed the same
/*		files = downDir.list(new FilenameFilter() {


			public boolean accept(File dir, String name) {

				return name.toLowerCase().startsWith("wo");
			}
		});

		System.out.println("Total WO FIles downloaded: " + files.length);
		allFilesList.add(files);*/

		//Monday 08/20/2018 add 3 more Patents CN, JP and KR


		files = downDir.list(new FilenameFilter() {


			public boolean accept(File dir, String name) {

				return name.toLowerCase().startsWith("cn");
			}
		});
		System.out.println("Total Canadian FIles downloaded: " + files.length);
		allFilesList.add(files);
		
		files = downDir.list(new FilenameFilter() {


			public boolean accept(File dir, String name) {

				return name.toLowerCase().startsWith("jp");
			}
		});
		System.out.println("Total Japaneese FIles downloaded: " + files.length);
		allFilesList.add(files);
		
		files = downDir.list(new FilenameFilter() {


			public boolean accept(File dir, String name) {

				return name.toLowerCase().startsWith("kr");
			}
		});
		
		System.out.println("Total Korean FIles downloaded: " + files.length);
		allFilesList.add(files);
		

		/*String[] xmlFiles = downDir.list();  // original when was for US/EP ONLY, replaced by filenameFiltering above for zip WO to a separate zipfile
			File[] xmlFilesToDelete = downDir.listFiles();*/
		byte[] buf = new byte[1024];


		for(String[] xmlFiles: allFilesList)
		{

			// create zip files if any files were downloaded, otherwise no zip file should be created
			if(xmlFiles.length >0)
			{
				// before release forward WO to be processed with US/EUP
				/*if(xmlFiles[0].toLowerCase().startsWith("wo"))
				{
					prefix="WO";

					xmlFilesToDelete = downDir.listFiles(new FilenameFilter() {


						public boolean accept(File dir, String name) {

							return name.toLowerCase().startsWith("wo");
						}
					});

				}*/
				if(xmlFiles[0].toLowerCase().startsWith("cn"))
				{
					prefix="CN";

					xmlFilesToDelete = downDir.listFiles(new FilenameFilter() {


						public boolean accept(File dir, String name) {

							return name.toLowerCase().startsWith("cn");
						}
					});

				}
				if(xmlFiles[0].toLowerCase().startsWith("jp"))
				{
					prefix="JP";

					xmlFilesToDelete = downDir.listFiles(new FilenameFilter() {


						public boolean accept(File dir, String name) {

							return name.toLowerCase().startsWith("jp");
						}
					});

				}
				if(xmlFiles[0].toLowerCase().startsWith("kr"))
				{
					prefix="KR";

					xmlFilesToDelete = downDir.listFiles(new FilenameFilter() {


						public boolean accept(File dir, String name) {

							return name.toLowerCase().startsWith("kr");
						}
					});

				}
				else if((xmlFiles[0].toLowerCase().startsWith("us")) || (xmlFiles[0].toLowerCase().startsWith("ep")) || (xmlFiles[0].toLowerCase().startsWith("wo")))
				{
					prefix="US/EP/WO";

					xmlFilesToDelete = downDir.listFiles(new FilenameFilter() {


						public boolean accept(File dir, String name) {

							return (name.toLowerCase().startsWith("us") || name.toLowerCase().startsWith("ep") || name.toLowerCase().startsWith("wo"));
						}
					});
				}




				// 02/01/2018 Separate the forward "WO" zip files from Forward "US/EUP" zip files

				if(msgType !=null && msgType.equalsIgnoreCase("forward"))
				{
					// before release of WO forward to be processed with US/EUP
					/*if(prefix.equalsIgnoreCase("wo"))
						zipsDir = new File(zipsDir+"/wo_forward_tmp");*/
					if (prefix.equalsIgnoreCase("cn"))
						zipsDir = new File(zipsDir + "/cn_forward_tmp");
					else if (prefix.equalsIgnoreCase("jp"))
						zipsDir = new File(zipsDir + "/jp_forward_tmp");
					else if(prefix.equalsIgnoreCase("kr"))
						zipsDir = new File(zipsDir + "/kr_forward_tmp");
					else
						zipsDir = new File(zipsDir+"/tmp");
				}

				else if (msgType !=null && msgType.equalsIgnoreCase("backfill"))
					zipsDir = new File(zipsDir+"/back_tmp");
				if(!(zipsDir.exists()))
				{
					zipsDir.mkdir();
				}





				//String zipFileName = zipsDir + "/" + epoch + "_" + zipFileID + ".zip";
				//String zipFileName = zipsDir + "/" + seqNum.nextloadNum() + seqNum.nextNum()+ ".zip";  // worked well before reading zipfilename from file
				String zipFileName = zipsDir + "/" + seqNum.nextNum()+ ".zip";
				ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileName));

				for(int i=0; i<xmlFiles.length; i++)
				{
					// limit each single zip file to hold recsPerZipfile, otherwise split to multiple zip files
					if(curRecNum >= recsPerZipFile)
					{
						curRecNum = 0;
						outZip.close();

						zipFileID++;

						zipFileName = zipsDir + "/" + seqNum.nextNum() + ".zip";
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

		downDir.delete();
	}




	// generate unique ID for the zip filename (zip file should be a sequence number that can not be duplicate)
	/* for example in multithreading running of download VTW, each instance should create unique zip file name that does not override the other instance
	 * Note: With current patent app, the name of zip file must be a number and must be less than 2147483647, a max int value
	 */

	public class SequenceGenerator
	{
		volatile AtomicInteger sequenceLoadNum = new AtomicInteger(getRecentZipFileName());
		volatile AtomicInteger sequenceID = new AtomicInteger(getRecentZipFileName());
		public synchronized int nextloadNum()
		{
			int nextVal = sequenceLoadNum.incrementAndGet();
			System.out.println("Next LoadNumber Value is: " + nextVal);
			return nextVal;

		}

		public synchronized int nextNum()
		{
			int nextVal = sequenceID.incrementAndGet();
			System.out.println("Zip fileName is: " + nextVal);

			// write the nextvalue to the file
			writeZipFileNameToFile(Integer.toString(nextVal));

			return nextVal;
		}
	}


	/*** Read/write from/to zipfileNames File ***/

	// reads text file containg zipfile name and get the most recent one for coming new zipfiles to be created, and save it to the file (thread-safe/multithreading)
	public synchronized void readZipFileNameFromFile(int loadnumber)
	{
		FileInputStream in = null;
		String lastLine = "";
		File zipFile = null;

		try {
			String currDir = System.getProperty("user.dir");
			if(type !=null && type.equalsIgnoreCase("forward"))
				zipFile = new File(currDir+"/zipFileNames.txt");
			else if(type !=null && type.equalsIgnoreCase("backfill"))
				zipFile = new File(currDir+"/backfill_zipFileNames.txt");
			if(!(zipFile.exists()))
			{
				zipFile.createNewFile();
			} 

			// read last line in the file

			in = new FileInputStream(zipFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));


			String temp = null;

			if(zipFile.length() >0)
			{
				for(String line; (line = br.readLine()) !=null; )
				{
					temp = line;
				}
				lastLine = temp;
				System.out.println("Last Line in the zipFileNames file: " + lastLine);

			}
			else
			{
				System.out.println("zipFileNames file is empty, set current weeknumber as default zip file name");
				lastLine = loadnumber + "00";
				System.out.println("first zipFileName to use in this new file : " + lastLine);
			}

			// set zipfilename for later access
			setRecentZipFileName(lastLine);


		}
		catch (IOException e) 
		{		
			e.printStackTrace();
		}
		finally
		{
			if(in !=null)
			{
				try
				{
					in.close();
				}
				catch(Exception e)
				{
					System.out.println("Failed to close zipFileNames inputstream");
					e.printStackTrace();
				}
			}
		}

	}


	public synchronized void writeZipFileNameToFile(String zipfileName)
	{
		PrintWriter pw = null;
		File zipFile = null;

		try {
			String currDir = System.getProperty("user.dir");
			if(type !=null && type.equalsIgnoreCase("forward"))
				zipFile = new File(currDir+"/zipFileNames.txt");
			else if(type !=null && type.equalsIgnoreCase("backfill"))
				zipFile = new File(currDir+"/backfill_zipFileNames.txt");
			if(!(zipFile.exists()))
			{
				zipFile.createNewFile();
			} 
			// write/append to the file 


			pw = new PrintWriter(new BufferedWriter(new FileWriter(zipFile, true)));   //append the new zipfilename
			pw.println(zipfileName);

		}
		catch (IOException e) 
		{		
			e.printStackTrace();
		}
		finally
		{
			if(pw !=null)
			{
				try
				{
					pw.flush();
					pw.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}



	// setters/getters

	public void setRecentZipFileName(String zipfileName)
	{
		zipFileName = zipfileName;
	}

	public int getRecentZipFileName()
	{
		return Integer.parseInt(zipFileName);
	}
}
