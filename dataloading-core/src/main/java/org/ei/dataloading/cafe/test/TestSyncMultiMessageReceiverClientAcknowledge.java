package org.ei.dataloading.cafe.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.ei.dataloading.cafe.GetANIFileFromCafeS3Bucket;
import org.ei.dataloading.cafe.ReceiveAmazonSQSMessage;
import org.ei.dataloading.cafe.SQSConfiguration;
import org.ei.dataloading.cafe.SQSExistenceCheck;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.amazon.sqs.javamessaging.message.SQSMessage;
import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

public class TestSyncMultiMessageReceiverClientAcknowledge {

	/**
	 * @author TELEBH
	 * An example class to demonstrate the behavior of CLIENT_ACKNOWLEDGE mode for received messages. 
	 *
	 * First, a session, a message producer, and a message consumer are created. Then, two messages are sent. Next, two messages
	 * are received but only the second one is acknowledged. After waiting for the visibility time out period, an attempt to
	 * receive another message is made. It is shown that no message is returned for this attempt since in CLIENT_ACKNOWLEDGE mode,
	 * as expected, all the messages prior to the acknowledged messages are also acknowledged.
	 */

	public TestSyncMultiMessageReceiverClientAcknowledge(){}
	
	static int numberOfRuns = 0;

	// Visibility time-out for the queue. It must match to the one set for the queue for this example to work.
	private static final long TIME_OUT_SECONDS = 10;
	private static final int MESSAGE_VISIBILITY_TIME_OUT_SECONDS = 300;
	private static final int NUM_OF_MESSAGES_TO_FETCH = 10;
	
	
	
	private static String bucketName = "";
	private static AmazonSQS sqs = null;
	private static Message amazonSqsMessage = null;
	private static String myQueueUrl = null;
	private static GetANIFileFromCafeS3Bucket objectFromS3 = null;
	private static AmazonS3 s3Client = null; 
	private DeleteMessageRequest deleteRequest = null;
	private ReceiveMessageResult receiveMessageResult = null;


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
		}
			// create Class instance 

			TestSyncMultiMessageReceiverClientAcknowledge msgReceiveClientAknowledge = new TestSyncMultiMessageReceiverClientAcknowledge();

			msgReceiveClientAknowledge.SQSCreationAndSetting();

			/*
			// AMazonSQS queue, that was working fine till i find i can get amazonSQSClient created by JMA Wrapper to amazonSQS

			// create AmazonSQS for later message deletion

			sqs = new AmazonSQSClient(config.getCredentialsProvider());
			sqs.setRegion(config.getRegion());

			System.out.println("===========================================");
			System.out.println("Getting Started with Amazon SQS");
			System.out.println("===========================================\n");


			// get current queue
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(config.getQueueName());
			myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

			// List queues
			System.out.println("Listing all queues in your account.\n");
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				System.out.println("  QueueUrl: " + queueUrl);
			}
			System.out.println();

			// END of AmazonSQS queue*/


	}

	public void SQSCreationAndSetting() throws JMSException, InterruptedException
	{
		
		try {

		// Create the configuration for the example
		SQSConfiguration config = SQSConfiguration.parseConfig("TestSyncMessageReceiverClientAcknowledge", queueArgs);

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
			
		// AMazonSQS queue
		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon SQS");
		System.out.println("===========================================\n");
		
			//clone JMSwrapped amazonSQSClient to AMazonSQS queue
			sqs = connection.getWrappedAmazonSQSClient().getAmazonSQSClient();
			// get current queue
					myQueueUrl = sqs.getQueueUrl(config.getQueueName()).getQueueUrl();

					// List queues
					System.out.println("Listing all queues in your account.\n");
					for (String queueUrl : sqs.listQueues().getQueueUrls()) {
						System.out.println("  QueueUrl: " + queueUrl);
					}
					System.out.println();
		// END of AmazonSQS queue		

		// Create the queue if needed
		SQSExistenceCheck.ensureQueueExists(connection, config.getQueueName());

		// Create the session  with client acknowledge mode
		Session session = connection.createSession(false, SQSSession.UNORDERED_ACKNOWLEDGE);

		// Create the consumer 
		MessageConsumer consumer = session.createConsumer(session.createQueue(config.getQueueName()));

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
	 * If a message is received, the message is printed. If no message is received, "Queue is empty!" is
	 * printed.
	 *
	 * @param consumer Message consumer
	 * @param acknowledge If true and a message is received, the received message is acknowledged.
	 * @throws JMSException
	 */
	private void receiveMessage(MessageConsumer consumer, boolean acknowledge) throws JMSException,InterruptedException {

		ReceiveAmazonSQSMessage obj = new ReceiveAmazonSQSMessage(); 

		String msgReciptHandle = null;

		try
		{
			
			//while(true)
			for(int j = 0; j<numberOfRuns;j++)
			{
				// Wait for for few seconds before next run
				System.out.println("Waiting for visibility timeout...");
				Thread.sleep(TimeUnit.SECONDS.toMillis(TIME_OUT_SECONDS));
				
				// Receive message(s)
				/*receiveMessageResult = connection.getAmazonSQSClient().receiveMessage(
						new ReceiveMessageRequest(myQueueUrl)
						.withVisibilityTimeout(MESSAGE_VISIBILITY_TIME_OUT_SECONDS)
						.withWaitTimeSeconds(10).withMaxNumberOfMessages(NUM_OF_MESSAGES_TO_FETCH));*/   //works well, but at sometimes give exception that "SQS: null"
				
				receiveMessageResult = sqs.receiveMessage(
						new ReceiveMessageRequest(myQueueUrl)
						.withVisibilityTimeout(MESSAGE_VISIBILITY_TIME_OUT_SECONDS)
						.withWaitTimeSeconds(10).withMaxNumberOfMessages(NUM_OF_MESSAGES_TO_FETCH));
				
			ArrayList<Message> messages = (ArrayList<Message>)receiveMessageResult.getMessages();
				System.out.println("MessagesList size: " + messages.size());
				
				
			//for(int i=1; i<=NUM_OF_MESSAGES_TO_FETCH;i++)
			for(Message message: messages)
			{
				/*// Receive a message
				javax.jms.Message message = consumer.receive(TimeUnit.SECONDS.toMillis(TIME_OUT_SECONDS));*/
				
				if (message == null) {
					System.out.println("Queue is empty!");
					break;
				} else {
					// Since this queue has only text messages, cast the message object and print the text
					//System.out.println("Received: " + ((TextMessage) message).getText());
					
					
					// set message visibility timeout
					
					/*connection.getAmazonSQSClient().changeMessageVisibility(new ChangeMessageVisibilityRequest(connection.getAmazonSQSClient().getQueueUrl("EVCAFE").getQueueUrl(),((SQSMessage)message).getReceiptHandle(),
							MESSAGE_VISIBILITY_TIME_OUT_SECONDS));*/
					
					
					//parse SQS Message Fields& determine whether it is "ANI" message
					//if(obj.ParseSQSMessage(((TextMessage)message).getText()))   //for JMS message
					if(obj.ParseSQSMessage(message.getBody()))
					{

						//check if S3 File, contains CPX record, then convert it
						if(objectFromS3.checkDBCollection(obj.getMessageField("bucket"),obj.getMessageField("key"),s3Client))
						{
							//get the s3file content & convert using our cpx converting prog
							objectFromS3.getFile(obj.getMessageField("bucket"), obj.getMessageField("key"));
						}
						// Acknowledge the message if asked
						//if (acknowledge) message.acknowledge();
					}

					// delete the message
					//msgReciptHandle = ((SQSTextMessage)message).getReceiptHandle();
					msgReciptHandle = message.getReceiptHandle();
					deleteMessage(msgReciptHandle);
				}


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
			//connection.getAmazonSQSClient().deleteMessage(deleteRequest);  // works well 
			sqs.deleteMessage(deleteRequest);
			
			//sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageHandle));
		}
	}

}
