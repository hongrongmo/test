package vtw;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.print.attribute.HashAttributeSet;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.AmazonWebServiceClient;
/*
 * Create amazon SQS and pull messages from vtw queue. since it is text messages, so print its contents
 * Author: HT
 * Date: 09/29/2016
 */


public class Amazonsqs {

	public Amazonsqs(){}

	private String bucketName="sc-ani-xml-prod";
	private String key="xyz";


	private static AmazonSQS sqs;
	private static String myQueueUrl;


	static List<String> attributes = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception {

		PrintWriter outWriter = null;
		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (~/.aws/credentials).
		 */
		AWSCredentialsProvider credentials = null;
		//BasicAWSCredentials credentials = null;		// for permission getting VTW queue properties (i.e. ApproximateNumberOfMessages)
		try {
			credentials = new EnvironmentVariableCredentialsProvider();   // works good for VTW queues
			
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (~/.aws/credentials), and is in valid format.",
							e);
		}

		sqs = new AmazonSQSClient(credentials);
		Region euWest2 = Region.getRegion(Regions.EU_WEST_1);    // for VTW
		Region usEast1 = Region.getRegion(Regions.US_EAST_1);	// for cafe
		sqs.setRegion(usEast1);

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon SQS");
		System.out.println("===========================================\n");

		try {

			File out = new File ("ev_cafe_after_initial_refeed.txt");
			outWriter = new PrintWriter(out);
			// PROD Queue: prod-contributor-event-queue-EV
			// UAT Queue:  acc-contributor-event-queue-EV
			// UAT BO Backfill: "acc-backfill-queue-EV";  
			// WO Prod Backfill Qeueue: prod-backfill-queue-EV
			//UAT Backfill Queueu for WO: acc-backfill-queue-EV
			
			//Recent UAT QUEUE 12/11/2017: arn:aws:sqs:eu-west-1:461549540087:acc-backfill-queue-EV
			
			//String queueName = "acc-contributor-event-queue-EV"; 
			//String queueName = "acc-contributor-event-queue-EV";    //UAT BO/US/EUP Forward
			//String accountID = "461549540087";
			
			
			/*
			 * String queueName = "prod-backfill-queue-EV"; // WO Prod Qeueue String
			 * accountID = "790640479873";
			 */
			
			
			// HH 03/25/2019 Added to get the new cafe subscription messages for APR/IPR new SNS topics
			/*"arn:aws:sns:us-east-1:814132467461:SCContentIPRFeedTopic-prod",
            "arn:aws:sns:us-east-1:814132467461:SCContentAPRFeedTopic-prod"*/
			
			
			String queueName = "EVCafeIprRefeed";
			String accountID = "230521890328";
			
		
	
			// UAT Backfill Queueu for WO: acc-backfill-queue-EV
			/*GetQueueUrlRequest request = new GetQueueUrlRequest().withQueueName("prod-contributor-event-queue-EV")
					.withQueueOwnerAWSAccountId("790640479873");*/
			
			GetQueueUrlRequest request = new GetQueueUrlRequest().withQueueName(queueName)
					.withQueueOwnerAWSAccountId(accountID);

			
			GetQueueUrlResult result = sqs.getQueueUrl(request);


			System.out.println("  QueueUrl: " + result.getQueueUrl());
			System.out.println();

			
			//queue attributes
			GetQueueAttributesRequest queue_attribute_request = new GetQueueAttributesRequest(result.getQueueUrl());

			attributes.add("All");
			queue_attribute_request.setAttributeNames(attributes);
			
			GetQueueAttributesResult queue_attribute_result = sqs.getQueueAttributes(queue_attribute_request);
			
	
			for(String attr: ((Map<String,String>)queue_attribute_result.getAttributes()).keySet())
			{
				System.out.println("attr: " +  attr + " value: " + ((Map<String,String>)queue_attribute_result.getAttributes()).get(attr));
			}

			

			// Receive messages
			System.out.println("Receiving messages from VTW SQS.\n");

			myQueueUrl = result.getQueueUrl();

			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl)
			.withVisibilityTimeout(1200)
			.withWaitTimeSeconds(10).withMaxNumberOfMessages(10)
			.withAttributeNames("SentTimestamp");


			//while(true)
			for(int i = 0; i<9000;i++)
			{				

				ReceiveMessageResult receiveMessageResult = sqs.receiveMessage(receiveMessageRequest);


				List<Message> messages = receiveMessageResult.getMessages();
				//System.out.println("MessagesList size: " + messages.size());


				int flag = 0; 		// added 03/25/2019 to identify cafe subscription message for the 2 new sns topics for IPR & APR
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
							String msgReciptHandle = message.getReceiptHandle();  // for deleting the message 
							String messageBody = message.getBody();
							Map<String,String> msgAttributes = message.getAttributes();


							// change message visibility timeout
							ChangeMessageVisibilityRequest msgVisibilityReq = new ChangeMessageVisibilityRequest(myQueueUrl, msgReciptHandle, 1200);
							//System.out.println("Message VisibilityTimeOut: " + msgVisibilityReq.getVisibilityTimeout());
							
							//System.out.println("SQS Message: " +  messageBody);

							//parse SQS Message Fields& determine whether it is sent to "E-Village" message
							//System.out.println("Message Body: " +  messageBody);
							outWriter.write(messageBody + "\n");
							if(messageBody.contains("\\\"prefix\\\""))
							{
								System.out.println(messageBody);
								deleteMessage(msgReciptHandle);
							}
									
							
							// only for debugging, uncomment when needed
							/*
							 * for(String key: msgAttributes.keySet()) { System.out.println("key: " + key +
							 * " , value: " + msgAttributes.get(key)); }
							 */

							// delete the message
							
								//deleteMessage(msgReciptHandle);
						}
					}
				}
				else
				{
					System.out.println("Queue is empty!");
					System.out.println("No Messages were recived at Iteration #: " + i + " Wait for 30" + " seconds, Skip to Next iteration");
					// Wait for for few seconds before next run
					System.out.println("Waiting for visibility timeout...");
					Thread.sleep(TimeUnit.SECONDS.toMillis(30));

				}
			}



		} catch (AmazonServiceException ase) {
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
			if(outWriter !=null)
			{
				try
				{
					outWriter.flush();
					outWriter.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private static void deleteMessage(String messageHandle)
	{
		if(messageHandle !=null && messageHandle.length() >0)
		{
			//System.out.println("Deleting a message: " + messageHandle);
			DeleteMessageRequest deleteRequest = new DeleteMessageRequest(myQueueUrl, messageHandle);
			sqs.deleteMessage(deleteRequest);
		}
	}
	


}
