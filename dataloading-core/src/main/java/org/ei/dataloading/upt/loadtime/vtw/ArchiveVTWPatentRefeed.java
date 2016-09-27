package org.ei.dataloading.upt.loadtime.vtw;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.ei.dataloading.cafe.CafeDownloadFileFromS3AllTypes;
import org.ei.dataloading.cafe.ReceiveAmazonSQSMessage;
import org.ei.dataloading.cafe.SQSConfiguration;
import org.ei.dataloading.cafe.SQSExistenceCheck;








import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
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
	private static String queueName;
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
	private static SQSConnection connection = null;
	private static HashMap<String, String> queueArgs = new HashMap<String,String>();
	
	private ReceiveAmazonSQSMessage obj = null;   // for parsing/checking Message Metadata
	private ReceiveMessageResult receiveMessageResult = null;
	private DeleteMessageRequest deleteRequest = null;
	private String messageBody;
	private Map<String,String> msgAttributes= new HashMap<String,String>();


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
		
		//Zip downloaded files (each in it's corresponding dir)
		CafeDownloadFileFromS3AllTypes.zipDownloads(recsPerZipFile, loadNumber);
		
		archiveVtwPatentRefeed.loadToDB();
		
		

	}

	// create text file to hold original message & partial parsed fields
	public void begin()
	{
		dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");		 
		Date date = new Date();

		String currDir = System.getProperty("user.dir");
		//ESdir=new File(currDir+"/es/" + this.doc_type + "/" +  this.loadNumber);
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

		queueArgs.put("--queue", queueName);
		//queueArgs.put("--region","us-east-1");
		queueArgs.put("--region","eu-west-1");
		//queueArgs.put(--a, value)

		try {

			// Create the configuration for the example
			SQSConfiguration config = SQSConfiguration.parseConfig("ArchiveVTWPatentRefeed", queueArgs);


			// Check if Queue Exist
			SQSExistenceCheck.setupLogging();

			// Create the connection factory based on the config
			SQSConnectionFactory connectionFactory =SQSConnectionFactory.builder()
					.withRegion(config.getRegion())
					.withAWSCredentialsProvider(config.getCredentialsProvider())
					.withNumberOfMessagesToPrefetch(NUM_OF_MESSAGES_TO_FETCH)
					.build();

			// Create the connection
			connection = connectionFactory.createConnection();

			queueName = config.getQueueName();
			// Create the queue if needed
			SQSExistenceCheck.ensureQueueExists(connection, queueName);


			// AMazonSQS queue
			System.out.println("===========================================");
			System.out.println("Getting Started with Amazon SQS");
			System.out.println("===========================================\n");

			//clone JMSwrapped amazonSQSClient to AMazonSQS queue
			sqs = connection.getWrappedAmazonSQSClient().getAmazonSQSClient();
			
			//queu request
			GetQueueUrlRequest request = new GetQueueUrlRequest().withQueueName(queueName).withQueueOwnerAWSAccountId("790640479873");
			
			GetQueueUrlResult result = sqs.getQueueUrl(request);
			
			// get current queue
			//myQueueUrl = sqs.getQueueUrl(queueName).getQueueUrl();
			myQueueUrl = result.getQueueUrl();

			// List queues
			System.out.println("Listing all queues in your account.\n");
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				System.out.println("  QueueUrl: " + queueUrl);
			}
			System.out.println();
			// END of AmazonSQS queue		


			// Create the session  with client acknowledge mode
			Session session = connection.createSession(false, SQSSession.UNORDERED_ACKNOWLEDGE);

			// Create the consumer 
			MessageConsumer consumer = session.createConsumer(session.createQueue(queueName));

			// Open the connection
			connection.start();

			// Receive a message parse it
			receiveMessage(consumer, true);


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
		finally
		{
			if(connection !=null)
			{
				try
				{
					// Close the connection. This will close the session automatically
					connection.close();
					System.out.println("Connection closed.");
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}


	private void receiveMessage(MessageConsumer consumer, boolean acknowledge) throws InterruptedException 
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
								recordBuf.append(FIELDDELIM);

								//message To
								if(obj.getMessageField("message_to") !=null)
								{
									recordBuf.append(obj.getMessageField("message_to"));
								}
								recordBuf.append(FIELDDELIM);

								//eventNotification ID
								if(obj.getMessageField("evt_svc_id") !=null)
								{
									recordBuf.append(obj.getMessageField("event_id"));
								}
								recordBuf.append(FIELDDELIM);


								//ServiceCall ID
								if(obj.getMessageField("evt_svc_id") !=null)
								{
									recordBuf.append(obj.getMessageField("service_id"));
								}
								recordBuf.append(FIELDDELIM);
								
								//Resource (Patent ID part)
								if(obj.getMessageField("resource") !=null)
								{
									recordBuf.append(obj.getMessageField("resource"));
								}
								recordBuf.append(FIELDDELIM);
								


								//Patent XML FileName
								if(obj.getMessageField("xmlFileName") !=null)
								{
									recordBuf.append(obj.getMessageField("xmlFileName"));
								}
								recordBuf.append(FIELDDELIM);
								
								//signed Asset URL's Expiration Date
								if(obj.getMessageField("urlExpirationDate") !=null)
								{
									recordBuf.append(convertMillisecondsToFormattedDate(obj.getMessageField("urlExpirationDate")));
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
		

}
