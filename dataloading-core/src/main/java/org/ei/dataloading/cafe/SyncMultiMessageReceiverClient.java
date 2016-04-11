package org.ei.dataloading.cafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

public class SyncMultiMessageReceiverClient {

	/**
	 * An example class to demonstrate the behavior of CLIENT_ACKNOWLEDGE mode for received messages. 
	 *
	 * First, a session, a message producer, and a message consumer are created. Then, two messages are sent. Next, two messages
	 * are received but only the second one is acknowledged. After waiting for the visibility time out period, an attempt to
	 * receive another message is made. It is shown that no message is returned for this attempt since in CLIENT_ACKNOWLEDGE mode,
	 * as expected, all the messages prior to the acknowledged messages are also acknowledged.
	 */

	public SyncMultiMessageReceiverClient(){}

	static int numberOfRuns = 0;
	
	
	static String url="jdbc:oracle:thin:@localhost:1521:eid";
    static String driver="oracle.jdbc.driver.OracleDriver";
    static String username="ap_correction1";
    static String password="ei3it";
    static String database;
    static int updateNumber=0;
    static String sqlldrFileName = null;
    

	// Visibility time-out for the queue. It must match to the one set for the queue for this example to work.
	private static final long TIME_OUT_SECONDS = 30;
	private static final int MESSAGE_VISIBILITY_TIME_OUT_SECONDS = 600;
	private static final int NUM_OF_MESSAGES_TO_FETCH = 10;


	private String queueName = "";
	private static AmazonSQS sqs = null;
	private static String myQueueUrl = null;
	private static GetANIFileFromCafeS3Bucket objectFromS3 = null;
	private static AmazonS3 s3Client = null; 
	private DeleteMessageRequest deleteRequest = null;
	private ReceiveMessageResult receiveMessageResult = null;
	
	private ReceiveAmazonSQSMessage obj = null;

	private String action = null;
	private long msgEpoch;
	

	//Configuration&Settings
	SQSConnection connection = null;

	private static HashMap<String, String> queueArgs = new HashMap<String,String>();


	public static void main(String args[]) throws JMSException, InterruptedException {

		/* queueArgs[0] = "--queue EVCAFE";
	 	     queueArgs[1] = "--region US_EAST_1";*/

		queueArgs.put("--queue", "EVCAFE");
		queueArgs.put("--region","us-east-1");

		if(args !=null)
		{
			if(args[0] !=null)
				numberOfRuns = Integer.parseInt(args[0]);
			
			if(args.length >2)
			{
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
					  database = args[2];
				  }
				  if(args[3] !=null)
				  {
					  url = args[3];
				  }
				  if(args[4] !=null)
				  {
					  driver = args[4];
				  }
				  if(args[5] !=null)
				  {
					  username = args[5];
				  }
				  if(args[6] !=null)
				  {
					  password = args[6];
				  }
			}
			
			if(args.length >7)
			{
				if(args[7] !=null)
					sqlldrFileName = args[7];
				
			}
		}
		// create Class instance 

		SyncMultiMessageReceiverClient msgReceiveClientAknowledge = new SyncMultiMessageReceiverClient();

		msgReceiveClientAknowledge.SQSCreationAndSetting();


	}

	public void SQSCreationAndSetting() throws JMSException, InterruptedException
	{

		try {

			// Create the configuration for the example
			SQSConfiguration config = SQSConfiguration.parseConfig("SyncMessageReceiverClientAcknowledge", queueArgs);

			// create object of GetANIFileFromCafeS3Bucket to convert the CPX record
			s3Client = config.getAmazonS3Cleint();
			objectFromS3 = GetANIFileFromCafeS3Bucket.getInstance(s3Client);


			// Setup logging for the example
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

	/**
	 * Receives a message through the consumer synchronously with the default timeout (TIME_OUT_SECONDS).
	 * If a message is received, the message is printed if it is ANI & CPX Record. If no message is received, "Queue is empty!" is
	 * printed.
	 *
	 * @param consumer Message consumer
	 * @param acknowledge If true and a message is received, the received message is acknowledged.
	 * @throws JMSException
	 */
	private void receiveMessage(MessageConsumer consumer, boolean acknowledge) throws JMSException,InterruptedException {

		obj = new ReceiveAmazonSQSMessage(); 
		ChangeMessageVisibilityRequest msgVisibilityReq;
		String msgReciptHandle = null;
		
		ReceiveMessageRequest request = new ReceiveMessageRequest(myQueueUrl)
										.withVisibilityTimeout(MESSAGE_VISIBILITY_TIME_OUT_SECONDS)
										.withWaitTimeSeconds(10).withMaxNumberOfMessages(NUM_OF_MESSAGES_TO_FETCH);


		try
		{

			//while(true)
			for(int j = 0; j<numberOfRuns;j++)
			{				
				// Receive message(s); default 10 messages (short poll)  that should match SQS settings
				/*receiveMessageResult = sqs.receiveMessage(
						new ReceiveMessageRequest(myQueueUrl)
						.withVisibilityTimeout(MESSAGE_VISIBILITY_TIME_OUT_SECONDS)
						.withWaitTimeSeconds(10).withMaxNumberOfMessages(NUM_OF_MESSAGES_TO_FETCH));*/
				
				receiveMessageResult = sqs.receiveMessage(request);
				
				
				ArrayList<Message> messages = (ArrayList<Message>)receiveMessageResult.getMessages();
				System.out.println("MessagesList size: " + messages.size());
				
				
				if(messages.size() >0)
				{
					//for(int i=1; i<=NUM_OF_MESSAGES_TO_FETCH;i++)
					for(Message message: messages)
					{
						if (message == null) {
							System.out.println("Queue is empty!");
							break;
						} else {
							
							msgReciptHandle = message.getReceiptHandle();  // for deleting the message 

							//parse SQS Message Fields& determine whether it is "ANI" message
							if(obj.ParseSQSMessage(message.getBody()))   //for Prod
							{
								// change message visibility timeout
								msgVisibilityReq = new ChangeMessageVisibilityRequest(myQueueUrl, msgReciptHandle, MESSAGE_VISIBILITY_TIME_OUT_SECONDS);
								System.out.println("Message Visibility Timeout is: " + msgVisibilityReq.getVisibilityTimeout());
								
								
								action = obj.getMessageField("action");
								msgEpoch = Long.parseLong(obj.getMessageField("epoch"));
								
								
								// give time for each of the Converted CPX content be written in out file
								//Thread.currentThread().sleep(100);


								//check if S3 File, contains CPX record, and SQS action is add/update (msgEpoch > objEpoch)/delete, then convert it
								//if(objectFromS3.checkDBCollection(obj.getMessageField("bucket"),obj.getMessageField("key"),s3Client))  // determine cpx from file content
								/**
								 * That was the original way to check whether the file is CPX record by checking file contents 
								 * check <<itemid idtype=\"CPX\">>  under <item-info> section to determine whether the file is CPX record or not
								 * Since 03/30/2016 Mike reported "adding dbcollection codes to Scopus Abstract CAFÉ SNS messages"
								 * so Now, can Determine whether the record is CPX or from SNS Message itself without going to file contents
								 */
								
								if(checkCpxDBCollection())
								{
									if(action !=null)
									{
										//if(action.equalsIgnoreCase("a") || (action.equalsIgnoreCase("u") && CheckMsgObjectEpoch()) || action.equalsIgnoreCase("d"))
										if(action.equalsIgnoreCase("a") || action.equalsIgnoreCase("d"))
										{
											//get the s3file content & convert using our cpx converting prog
											//objectFromS3.getFile(obj.getMessageField("bucket"), obj.getMessageField("key"));  //was working fine, for static url/schema,..

											objectFromS3.getFile(obj.getMessageField("bucket"), obj.getMessageField("key"),
													updateNumber,database,url,driver,username,password, sqlldrFileName);
										}
										else if (action.equalsIgnoreCase("u"))
										{
											objectFromS3.fileContentMetadata(obj.getMessageField("bucket"),obj.getMessageField("key"),s3Client);
											if(CheckMsgObjectEpoch())
											{
												objectFromS3.getFile(obj.getMessageField("bucket"), obj.getMessageField("key"),
														updateNumber,database,url,driver,username,password, sqlldrFileName);
											}
											
										}
									}
								}
								// Acknowledge the message if asked
								//if (acknowledge) ((javax.jms.Message)message).acknowledge();
							}

							// delete the message
							deleteMessage(msgReciptHandle);
						}

					}
				}
				
				else
				{
					System.out.println("No Messages were recived at Iteration #: " + j + " Wait for for few seconds, Skip to Next iteration");
					// Wait for for few seconds before next run
					System.out.println("Waiting for visibility timeout...");
					Thread.sleep(TimeUnit.SECONDS.toMillis(TIME_OUT_SECONDS));
					
				}
			}
		}
		//catch(JMSException jmsex)   // for JMS
		catch(Exception sqsex)
		{
			/*System.err.println("Error receiving from SQS: " + jmsex.getMessage());
			jmsex.printStackTrace();*/

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
	
	private boolean CheckMsgObjectEpoch()
	{
		boolean updatable = false;
		
		//if the action is “update”, the user only processes the message if “epoch” in the incoming message is later than “epoch” from the object’s UserMetadata 
		long objEpoch = Long.parseLong(CafeRecordMetaData.getValue("EPOCH"));
		
		if (msgEpoch > objEpoch) 
		{
			updatable = true;
		}	
			
		return updatable;
	}
	
	//03/30/2016 check dbcollection code from SNS message to determin whether it is CPX record
	
	private boolean checkCpxDBCollection()
	{
		boolean cpxCollection = false;
		String dbcollcodes = obj.getMessageField("dbcollcodes");
		String dbcode = null;
		
		
		if(dbcollcodes !=null && dbcollcodes.length() >0)
		{
			if(dbcollcodes.contains("|"))
			{
				StringTokenizer dbcodes = new StringTokenizer(dbcollcodes, "|");
				while(dbcodes.hasMoreTokens())
				{
					dbcode=dbcodes.nextToken().trim();
					
					if(dbcode != null && dbcode.length() >0 && dbcode.equalsIgnoreCase("CPX"))
					{
						cpxCollection =true;
						System.out.println("CPX file");
						return cpxCollection;
					}
				}
			}
			
			else if (dbcollcodes.equalsIgnoreCase("CPX"))
			{
				cpxCollection =true;
				System.out.println("CPX file");
			}
			else
			{
				System.out.println("Skip this Key as it belongs to db collection: " +  dbcollcodes);
			}
		}
		
		return cpxCollection;
	}

}
