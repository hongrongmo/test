package org.ei.dataloading.cafe;

/**
 * Author: HH
 * Date: 04/14/2016
 * Description: Backup Messages in EVCafeRefeed
 * NOTE: messages sent by Cafe does not have attributes, so can not get "senttimestamp" the time when messages was received by the Queue
 * i.e. sample message received from EVCAFE
 * {MessageId: e12536cb-64af-4cf8-9f79-9653951be58b,ReceiptHandle: AQEB6jZ89gXyv67RPA/YMshyW+X12/FDamp6GKCRkndNaxjoqdSXXfroZiU5iPoFQ3ckgpuhM6Dq+0LC5s14DJ0ku+8IVb1aoHtZSFuzldvZZbHIjs+jWVpGhsTaxqCcbsp8We5LxPnl79pmQa3qAx8d5Oa5u5TxmbAIKxqkqPIXt67CTcvsgWwIfOM3eKOfq2uc4Y/6sGX4tVYFt6lJYiKhxHE3igvTWDYWDb4JBcMohC3qoM8t+41UvN4ISBwQIl0oyQfecX48/fSQOpQi6pM+5usfzFSq9q/ubFzuuWCWNZEOlUJt42xzV6ttdKQ6bj2Dw7EZl4ynkVhPlJQg10BcDCw3GtjeZUbkJjCUwSQ0GQnJfZL5B6Xxmdm6FD2Gtghz,MD5OfBody: 220b266b908d153ef22a412660c14c6b,Body: { "bucket" : "sc-ani-xml-prod", "entries" : [{ "key" : "84961802044", "modification" : "CONTENT", "issn" : "10526188", "document-type" : "core", "prefix" : "2-s2.0", "epoch" : "1460263384774", "action" : "a", "pui" : "609158794", "xocs-timestamp" : "2016-04-10T04:21:56.117359Z", "sort-year" : "2016", "dbcollcodes" : "CPX|REAXYSCAR|Scopusbase", "eid" : "2-s2.0-84961802044", "doi" : "10.3103/S1052618815060151", "load-unit-id" : "swd_nC43700445915.dat", "version" : "2016-04-10T03:11:17.000017+01:00" } ] },Attributes: {},MessageAttributes: {}}
 * 
 * AWS Doc:
 * For each message returned, the response includes the following:

	Message body
	MD5 digest of the message body. For information about MD5, go to http://www.faqs.org/rfcs/rfc1321.html.
	Message ID you received when you sent the message to the queue.
	Receipt handle.
	Message attributes.
	MD5 digest of the message attributes.
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import java.util.*;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.ei.dataloading.bd.loadtime.BaseTableDriver;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class archiveEVCafeRefeed {



	// Visibility time-out for the queue. It must match to the one set for the queue for this example to work.
	private static final long TIME_OUT_SECONDS = 30;
	private static final int MESSAGE_VISIBILITY_TIME_OUT_SECONDS = 300;
	private static final int NUM_OF_MESSAGES_TO_FETCH = 10;


	DateFormat dateFormat;
	DateFormat msgSentDateFormat;
	

	private static int updateNumber = 0;
	private static String url="jdbc:oracle:thin:@localhost:1521:eid";
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String username="ap_correction1";
	private String password="ei3it";
	private static String sqlldrFileName = null;


	private String filename;

	static int numberOfRuns=0;

	private static AmazonSQS sqs;
	private static String myQueueUrl;
	private static String queueName;

	private PrintWriter out;
	public static final char FIELDDELIM = '\t';
	StringBuffer recordBuf = null;
	String document_type = "";

	
	

	private String messageBody;
	private ReceiveMessageResult receiveMessageResult = null;
	private DeleteMessageRequest deleteRequest = null;
	private ReceiveAmazonSQSMessage obj = null;   // for parsing/checking Message Metadata
	private Map<String,String> msgAttributes= new HashMap<String,String>();



	private static SQSConnection connection = null;


	private static HashMap<String, String> queueArgs = new HashMap<String,String>();


	public archiveEVCafeRefeed()
	{

	}
	public static void main(String[] args) throws Exception {

		archiveEVCafeRefeed archiveEVCafeRefeed = new archiveEVCafeRefeed();
		if(args[0] !=null)
		{
			numberOfRuns = Integer.parseInt(args[0]);
		}
		if(args[1] !=null)
		{
			queueName = args[1];
		}
		if(args.length >2)
		{
			if(args[2] !=null)
			{
				sqlldrFileName = args[2];
			}
		}
		

			archiveEVCafeRefeed.begin();
			archiveEVCafeRefeed.SQSCreationAndSetting();
			archiveEVCafeRefeed.end();
			archiveEVCafeRefeed.loadToDB();
			
		
	}

	private void receiveMessage(MessageConsumer consumer, boolean acknowledge) throws InterruptedException 
	{
		String bucketName = "";
		int exitWaitingID = 0;
		
		ChangeMessageVisibilityRequest msgVisibilityReq;
		String msgReciptHandle = null;

		obj = new ReceiveAmazonSQSMessage();
		//msgSentDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // complained in in sqlldr
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
							
							//parse SQS Message Fields& determine whether it is "ANI" message & belongs to dbcollcodes *|CPX|*
							if(obj.ParseSQSMessage(messageBody))   
							{
								bucketName = obj.getMessageField("bucket");
								if(!(bucketName.isEmpty()) && bucketName.contains("ani"))
								{
									document_type = "ani";
								}
								else if (!(bucketName.isEmpty()) && bucketName.contains("apr"))
								{
									document_type = "apr";
								}
								else if (!(bucketName.isEmpty()) && bucketName.contains("ipr"))
								{
									document_type = "ipr";
								}
								
								//Key
								if(obj.getMessageField("key") !=null)
								{
									recordBuf.append(obj.getMessageField("key"));
								}
								recordBuf.append(FIELDDELIM);
								
								//epoch
								if(obj.getMessageField("epoch") !=null)
								{
									recordBuf.append(obj.getMessageField("epoch"));
								}
								recordBuf.append(FIELDDELIM);
								
								//pui
								if(obj.getMessageField("pui") !=null)
								{
									recordBuf.append(obj.getMessageField("pui"));
								}
								recordBuf.append(FIELDDELIM);
								
								//action
								if(obj.getMessageField("action") !=null)
								{
									recordBuf.append(obj.getMessageField("action"));
								}
								recordBuf.append(FIELDDELIM);
								
								//Bucket Name
								if(obj.getMessageField("bucket") !=null)
								{
									recordBuf.append(obj.getMessageField("bucket"));
								}
								recordBuf.append(FIELDDELIM);
								
								
								//Document_type
								
								if(!(document_type.isEmpty()))
								{
									recordBuf.append(document_type);
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
					System.out.println("No Messages were recived at Iteration #: " + i + " Wait for for few seconds, Skip to Next iteration");
					// Wait for for few seconds before next run
					System.out.println("Waiting for visibility timeout...");
					Thread.sleep(TimeUnit.SECONDS.toMillis(TIME_OUT_SECONDS));
					
					// HH 01/11/2017 exit after 2 attempts to get messages if queue is still empty

					if(exitWaitingID >2)
					{
						System.out.println("no Messages after " + exitWaitingID + " attempts, exit loop to continue rest of process...");
						break;
					}
					exitWaitingID ++;
					

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
	
	public void begin()
	{
		dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");		 
		Date date = new Date();

		filename = queueName+"_after_initial-"+dateFormat.format(date)+".txt";
		try {
			out = new PrintWriter(new FileWriter(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Output Filename "+filename);

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


	public void SQSCreationAndSetting() throws JMSException, InterruptedException
	{

		queueArgs.put("--queue", queueName);
		queueArgs.put("--region","us-east-1");

		try {

			// Create the configuration for the example
			SQSConfiguration config = SQSConfiguration.parseConfig("archiveEVCafeRefeed", queueArgs);


			// Check if Queue Exist
			SQSExistenceCheck.setupLogging();

			// Create the connection factory based on the config
			SQSConnectionFactory connectionFactory =SQSConnectionFactory.builder()
					.withRegion(config.getRegion())
					.withAWSCredentialsProvider(config.getCredentialsProvider())
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
			// get current queue
			myQueueUrl = sqs.getQueueUrl(queueName).getQueueUrl();

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
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered " +
					"a serious internal problem while trying to communicate with SQS, such as not " +
					"being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
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

			} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}


}
