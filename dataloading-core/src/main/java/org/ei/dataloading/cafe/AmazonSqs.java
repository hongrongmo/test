package org.ei.dataloading.cafe;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.print.attribute.HashAttributeSet;

import org.ei.dataloading.awss3.AmazonS3Service;

import antlr.collections.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/*
 * Create amazon SQS and pull messages. since it is text messages, so print its contents
 * Author: TELEBH
 * Date: 01/28/2016
 */
public class AmazonSqs {


	public AmazonSqs(){}
	
	private String bucketName="sc-ani-xml-prod";
	private String key="xyz";
	
	private static ReceiveAmazonSQSMessage sqsMessage;
	private static AmazonSQS sqs;
	private static String myQueueUrl;
	
	
	public static void main(String[] args) throws Exception {
		int k=0;
		StringBuffer messageBody = new StringBuffer();
		int year = 0;
		
		
		ArrayList<HashMap<String,Date>> s3KeyInfo = new ArrayList<HashMap<String,Date>>();
		HashMap<String,Date> row = new HashMap<String,Date>();
		AmazonSqs sqsInstance = new AmazonSqs();
		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (~/.aws/credentials).
		 */
		AWSCredentialsProvider credentials = null;
		try {
			credentials = new EnvironmentVariableCredentialsProvider();
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
			CreateQueueRequest createQueueRequest = new CreateQueueRequest("EVCAFE");
			myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

			// List queues
			System.out.println("Listing all queues in your account.\n");
			for (String queueUrl : sqs.listQueues().getQueueUrls()) {
				System.out.println("  QueueUrl: " + queueUrl);
			}
			System.out.println();
			
			// create object of ReceiveAmazonSQSMessage to parse file
			sqsMessage = new ReceiveAmazonSQSMessage();
			
			// create object of GetANIFileFromCafeS3Bucket to convert the CPX record
			GetANIFileFromCafeS3Bucket objectFromS3 = GetANIFileFromCafeS3Bucket.getInstance(sqsInstance.bucketName, sqsInstance.key);


   
			// Receive messages
			System.out.println("Receiving messages from EVCAFE.\n");
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
			for(int i=0;i<3;i++)
			{
				
				// Wait for the visibility time out, so that unacknowledged messages reappear in the queue
		        System.out.println("Waiting for visibility timeout...");
		        Thread.sleep(TimeUnit.SECONDS.toMillis(10));
		        
		        receiveMessageRequest.setWaitTimeSeconds(10);
		        
				ArrayList<Message> messages = (ArrayList<Message>)sqs.receiveMessage(receiveMessageRequest).getMessages();
				for (Message message : messages) {
					//System.out.println("  Message");
					//System.out.println("    MessageId:     " + message.getMessageId());
					//System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
					//System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
					//System.out.println("    Body:          " + message.getBody());

					// Delete a message if it is APR or IPR, only keep ANI (abstract)
					if(!(message.getBody().contains("-ani-")))
					{
						k++;
						sqsInstance.deleteMessageFromQueue(message);
						continue;
						
					}
					else
					{
						// keep message & print it's contents
						System.out.println("Keep Message it is ANI one");
						
						// convert the CPX Abstract Record
						sqsMessage.ParseSQSMessage(message.getBody());
						System.out.println();
						if(objectFromS3.checkDBCollection(sqsMessage.getMessageField("bucket"), sqsMessage.getMessageField("key")))
						{
							objectFromS3.getFile(sqsMessage.getMessageField("bucket"), sqsMessage.getMessageField("key"));
						}
						else
						{
							//delete the message from the queue
							sqsInstance.deleteMessageFromQueue(message);
						}
						
						
					}

				}
				System.out.println();
			}
			System.out.println("Totale # of deleted messages + " + k);



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
	}

private ArrayList<HashMap<String,Date>> getFileNameFromS3()
{
	ArrayList<HashMap<String,Date>> s3KeyInfo = new ArrayList<HashMap<String,Date>>();
	HashMap <String,Date> row = new HashMap<String,Date>();
	try
	{
		AmazonS3 s3client = AmazonS3Service.getInstance().getAmazonS3Service();
		
		//List Keys in S3 bucket
		ListObjectsRequest listRequest = new ListObjectsRequest().withBucketName(bucketName);
		
		ObjectListing objectListing;
		int k =0;
		
			objectListing = s3client.listObjects(listRequest);
			for(S3ObjectSummary objectSummary: objectListing.getObjectSummaries())
			{
				if(k>=2)
					break;
				
				row.put(objectSummary.getKey(), objectSummary.getLastModified());
				
				System.out.println(" - " + objectSummary.getKey()
						+ "  Last Modified: "+ objectSummary.getLastModified());
				
				k++;
				s3KeyInfo.add(row);
			}
			
			listRequest.setMarker(objectListing.getNextMarker());

		
	}
	catch(IOException ex)
	{
		ex.printStackTrace();
	}
	catch(AmazonServiceException ase)
	{
		System.out.println("Caught an AmazonServiceException, which " +"means your request made it " +
							"to Amazon S3, but was rejected with an error response" +
							" for some reason.");
        System.out.println("Error Message:    " + ase.getMessage());
        System.out.println("HTTP Status Code: " + ase.getStatusCode());
        System.out.println("AWS Error Code:   " + ase.getErrorCode());
        System.out.println("Error Type:       " + ase.getErrorType());
        System.out.println("Request ID:       " + ase.getRequestId());
	}
	catch(AmazonClientException ace)
	{
		System.out.println("Caught an AmazonClientException, which " +
        		"means the client encountered " +
                "an internal error while trying to " +
                "communicate with S3, " +
                "such as not being able to access the network.");
        System.out.println("Error Message: " + ace.getMessage());
	}
	
	return s3KeyInfo;
}


private void deleteMessageFromQueue(Message message)
{
	if(message !=null)
	{
		System.out.println("Deleting a message: " + message.getReceiptHandle());
		String messageReceiptHandle = message.getReceiptHandle();
		sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));
	}
}

	/*public static void main(String [] args)
	{
		AWSCredentialsProvider credentialsProvider = null;
		try
		{
			credentialsProvider = new EnvironmentVariableCredentialsProvider();

		}
		catch(Exception e)
		{
			throw new AmazonClientException("cannot load credentials from the enverinment variables" +
					"please make sure that the Environemnet variables AWS_ACCESS_KEY and AWS_SECRET_KEY are set" + 
					e.getMessage(), e);

		}

		AmazonSQSClient sqs = new AmazonSQSClient(credentialsProvider);
		Region useast = Region.getRegion(Regions.US_EAST_1);
		sqs.setRegion(useast);

		System.out.println("Start with Amazon SQS....");

		//check if queue exists
		CreateQueueRequest request = new CreateQueueRequest("test");
		String url = sqs.createQueue(request).getQueueUrl();

		if(url !=null && url.contains("test"))
		{
			System.out.println("Yes Queue exist with url:  " + url);

			//Receive messages

			ReceiveMessageRequest msgRequest = new ReceiveMessageRequest();

			//short pulling (10 messages a time), seems it only returns one single MSG???

			ArrayList<Message> messages = (ArrayList<Message>) sqs.receiveMessage(msgRequest).getMessages();

			for(Message message: messages)
			{
				System.out.println("Message");
				System.out.println("MessageId: " + message.getMessageId());
				System.out.println("RecieptHandle: " + message.getReceiptHandle());
				System.out.println("MD5: " + message.getMD5OfBody());
				System.out.println("Body: " + message.getBody());
			}


		}
	}*/


}

