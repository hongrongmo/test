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

public class archiveEVCafeRefeed {


	private static final long TIME_OUT_SECONDS = 30;
	DateFormat dateFormat;
	DateFormat sentTimestampdateFormat;

	private static int updateNumber = 0;
	private static String url="jdbc:oracle:thin:@localhost:1521:eid";
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String username="ap_correction1";
	private String password="ei3it";
	private static String sqlldrFileName = null;
	private String action = null;
	private long msgEpoch;



	static int numberOfRuns=0;

	private static AmazonSQS sqs;
	private static String myQueueUrl;

	private PrintWriter out;
	private static Map<String,String> msgAttributes = new HashMap<String,String>();

	static String messageBody;
	final static String queueName = "EVCafeRefeed";

	private static GetANIFileFromCafeS3Bucket objectFromS3 = null;
	private static AmazonS3 s3Client = null; 
	private ReceiveAmazonSQSMessage obj = null;

	private static SQSConnection connection = null;

	private static Set<String> messageIds= new HashSet<String>();
	private static HashMap<String, String> queueArgs = new HashMap<String,String>();


	public void archiveEVCafeRefeed()
	{

	}
	public static void main(String[] args) throws Exception {

		archiveEVCafeRefeed archiveEVCafeRefeed = new archiveEVCafeRefeed();
		if(args[0] !=null)
			numberOfRuns = Integer.parseInt(args[0]);
		if(args[1]!=null && args[1].length()>0)
		{
			Pattern pattern = Pattern.compile("^\\d*$");
			Matcher matcher = pattern.matcher(args[1]);
			if (matcher.find())
			{
				updateNumber = Integer.parseInt(args[1]);
			}
			else
			{
				System.out.println("did not find updateNumber or updateNumber has wrong format");
				System.exit(1);
			}
		}

		if(args[2] !=null)
		{
			url=args[2];
		}
		if(args.length>3)
		{
			if(args[3] !=null)
			{
				sqlldrFileName = args[3];
			}
		}


		// works only on my local machine	
		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (~/.aws/credentials).
		 */
		/*AWSCredentialsProvider credentials = null;
		try {
			//credentials = new EnvironmentVariableCredentialsProvider();   //for localhost
			//credentials = new InstanceProfileCredentialsProvider();		// for dataloading ec2

		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (~/.aws/credentials), and is in valid format.",
							e);
		}






		sqs = new AmazonSQSClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_EAST_1);
		sqs.setRegion(usWest2);

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon SQS");
		System.out.println("===========================================\n");

		try {
			// get current queue
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
			myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

			// List queues
			System.out.println("Listing all queues in your account.\n");
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				System.out.println("  QueueUrl: " + queueUrl);
			}
			System.out.println();

		 */


		try
		{
			archiveEVCafeRefeed.begin();
			archiveEVCafeRefeed.receiveMessage();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			archiveEVCafeRefeed.end();


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

		/*} catch (AmazonServiceException ase) {
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
		}*/

	}

	public void receiveMessage()
	{
		// Receive messages
		System.out.println("Receiving messages from "+queueName+".\n");
		ReceiveMessageRequest receiveMessageRequest;

		sentTimestampdateFormat = new SimpleDateFormat("yyyy_MM_dd");
		Date date;

		obj = new ReceiveAmazonSQSMessage(); 
		ChangeMessageVisibilityRequest msgVisibilityReq;
		String msgReciptHandle = null;


		try
		{

			//while(true)
			for(int i = 0; i<numberOfRuns;i++)
			{				
				receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl)
				.withWaitTimeSeconds(10).withMaxNumberOfMessages(10)
				.withAttributeNames("All");

				ArrayList<Message> messages = (ArrayList<Message>)sqs.receiveMessage(receiveMessageRequest).getMessages();

				//msgAttributes = receiveMessageRequest.getAttributeNames();

				if(messages.size() >0)
				{
					for(Message message:messages)
					{
						if (message == null) {
							System.out.println("Queue is empty!");
							break;
						} else {
							messageBody = message.getBody();
							//System.out.println("SQS Message: " +  messageBody);

							msgAttributes = message.getAttributes();

							if(!(messageIds.contains(message.getMessageId())))
							{
								out.println(messageBody);
								messageIds.add(message.getMessageId());

								// Convert Key if it is for dbcollcodes "CPX"
								//parse SQS Message Fields& determine whether it is "ANI" message
								if(obj.ParseSQSMessage(message.getBody()))   //for Prod
								{
									// change message visibility timeout
									msgVisibilityReq = new ChangeMessageVisibilityRequest(myQueueUrl, msgReciptHandle, 1200);
									System.out.println("Message Visibility Timeout is: " + msgVisibilityReq.getVisibilityTimeout());


									action = obj.getMessageField("action");
									msgEpoch = Long.parseLong(obj.getMessageField("epoch"));


									if(action !=null)
									{

										objectFromS3.getFile(obj.getMessageField("bucket"), obj.getMessageField("key"),action,msgEpoch);

									}

								}

							}

							else
							{
								System.out.println("Message was previously received and archived");
							}



							//receiveMessageRequest Attributes

							for(String key: msgAttributes.keySet())
							{
								// convert SentTimestamp from default Epoch format into date time format
								if(key.equalsIgnoreCase("SentTimestamp"))
								{
									date = new Date(Long.parseLong(msgAttributes.get(key)));

									String SentTimestamp = sentTimestampdateFormat.format(date);
									System.out.println("formated sentTimeStamp: " + SentTimestamp);
								}
								System.out.println(key +"#"+ msgAttributes.get(key));
							}
						}
					}
				}
				else
				{
					System.out.println("No Messages were recived at Iteration #: " + i + " Wait for for few seconds, Skip to Next iteration");
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
	public void writeMessage()
	{

	}
	public void begin()
	{
		queueArgs.put("--queue", "EVCafeRefeed");
		queueArgs.put("--region","us-east-1");


		dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss");
		Date date = new Date();

		String filename = queueName+"-"+dateFormat.format(date)+".txt";
		try {
			out = new PrintWriter(new FileWriter(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Output Filename "+filename);

		// Create the configuration for the example
		SQSConfiguration config = SQSConfiguration.parseConfig("archiveEVCafeRefeed", queueArgs);

		// create object of GetANIFileFromCafeS3Bucket to convert the CPX record
		s3Client = config.getAmazonS3Cleint();
		//objectFromS3 = GetANIFileFromCafeS3Bucket.getInstance(s3Client);
		objectFromS3 = new GetANIFileFromCafeS3Bucket(s3Client,updateNumber,"cpx",url,driver,username,password, sqlldrFileName);


		// Create the connection factory based on the config
		SQSConnectionFactory connectionFactory =SQSConnectionFactory.builder()
				.withRegion(config.getRegion())
				.withAWSCredentialsProvider(config.getCredentialsProvider())
				.build();

		// Create the connection
		try {
			connection = connectionFactory.createConnection();

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
			//MessageConsumer consumer = session.createConsumer(session.createQueue(queueName));

			// Open the connection

			connection.start();
		} catch (JMSException e) {
			e.printStackTrace();
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

}
