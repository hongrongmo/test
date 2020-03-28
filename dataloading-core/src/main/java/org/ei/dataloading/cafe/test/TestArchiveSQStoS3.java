package org.ei.dataloading.cafe.test;

/**
 * Author: Hanan 
 * Date: 04/14/2016
 * Description: Get large messages from source SQS (i.e. EVCAFE) and save it to S3 bucket, then send to a target SQS
 * NOTE: this Example ONLY WORKS for LARGE Messages that's of size of up to 2 GB. IS Message is of small Size, it will not be saved in S3 Bucket, but still
 * Sent to Target SQS
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;


public class TestArchiveSQStoS3 {
	/*private static final String s3BucketName = UUID.randomUUID() + "-"
			+ DateTimeFormat.forPattern("yyMMdd-hhmmss").print(new DateTime());*/
	
	private static final String s3BucketName = "sqs-archive-test";
	private static boolean bucketExist = false;

	private static AmazonSQS sqsExtended = null;
	private static AWSCredentials credentials = null;
	private static String targetQueueUrl;

	private AmazonSQS sqs;
	private String srcQueueUrl;
	
	private ArrayList<Message> messages;
	SendMessageRequest myMessageRequest;

	public TestArchiveSQStoS3()
	{
		
	}
	public static void main(String[] args) {



		try {
			credentials = new PropertiesCredentials(new File("AwsCredentials.properties"));
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the AWS credentials from the expected AWS credential profiles file. "
							+ "Make sure that your credentials file is at the correct "
							+ "location (/home/$USER/.aws/credentials) and is in a valid format.", e);
		}

		AmazonS3 s3 = new AmazonS3Client(credentials);
		Region s3Region = Region.getRegion(Regions.US_WEST_2);
		s3.setRegion(s3Region);

		// Set the Amazon S3 bucket name, and set a lifecycle rule on the bucket to
		//   permanently delete objects a certain number of days after
		//   each object's creation date.
		//   Then create the bucket, and enable message objects to be stored in the bucket.
		BucketLifecycleConfiguration.Rule expirationRule = new BucketLifecycleConfiguration.Rule();
		expirationRule.withExpirationInDays(14).withStatus("Enabled");
		BucketLifecycleConfiguration lifecycleConfig = new BucketLifecycleConfiguration().withRules(expirationRule);

		List<Bucket> allS3Buckets = s3.listBuckets();
		for(int i=0; i<allS3Buckets.size();i++)
		{
			Bucket s3Bucket = allS3Buckets.get(i);
			if (s3Bucket.getName().equalsIgnoreCase(s3BucketName))
			{
				bucketExist = true;
			}
		}
		
		if(! bucketExist)
		{
			s3.createBucket(s3BucketName);
			s3.setBucketLifecycleConfiguration(s3BucketName, lifecycleConfig);
			System.out.println("Bucket "+ s3BucketName + " created and configured.");
		}
		

		// Set the SQS extended client configuration with large payload support enabled.
		ExtendedClientConfiguration extendedClientConfig = new ExtendedClientConfiguration()
		.withLargePayloadSupportEnabled(s3, s3BucketName);

		sqsExtended = new AmazonSQSExtendedClient(new AmazonSQSClient(credentials), extendedClientConfig);
		Region sqsRegion = Region.getRegion(Regions.US_EAST_1);
		sqsExtended.setRegion(sqsRegion);

		// Create a message queue for this example.
		String QueueName = "QueueName" + UUID.randomUUID().toString();
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(QueueName);
		targetQueueUrl = sqsExtended.createQueue(createQueueRequest).getQueueUrl();
		System.out.println("Queue " + QueueName + " created.");


		// Get Messages from Source Queue
		TestArchiveSQStoS3 archiveSQS = new TestArchiveSQStoS3();
		archiveSQS.getMessagesFromSourceQueue();
		
		
		/*  // Delete the message, the queue, and the bucket.
		    String messageReceiptHandle = messages.get(0).getReceiptHandle();
		    sqsExtended.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));
		    System.out.println("Deleted the message.");

		    sqsExtended.deleteQueue(new DeleteQueueRequest(myQueueUrl));
		    System.out.println("Deleted the queue.");

		    deleteBucketAndAllContents(s3);
		    System.out.println("Deleted the bucket.");*/

	}

	private static void deleteBucketAndAllContents(AmazonS3 client) {

		ObjectListing objectListing = client.listObjects(s3BucketName);

		while (true) {
			for (Iterator<?> iterator = objectListing.getObjectSummaries().iterator(); iterator.hasNext(); ) {
				S3ObjectSummary objectSummary = (S3ObjectSummary) iterator.next();
				client.deleteObject(s3BucketName, objectSummary.getKey());
			}

			if (objectListing.isTruncated()) {
				objectListing = client.listNextBatchOfObjects(objectListing);
			} else {
				break;
			}
		};

		VersionListing list = client.listVersions(new ListVersionsRequest().withBucketName(s3BucketName));

		for (Iterator<?> iterator = list.getVersionSummaries().iterator(); iterator.hasNext(); ) {
			S3VersionSummary s = (S3VersionSummary) iterator.next();
			client.deleteVersion(s3BucketName, s.getKey(), s.getVersionId());
		}

		client.deleteBucket(s3BucketName);

	}

	public void getMessagesFromSourceQueue()
	{

		sqs = new AmazonSQSClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_EAST_1);
		sqs.setRegion(usWest2);

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon SQS");
		System.out.println("===========================================\n");

		try {
			// get current queue
			CreateQueueRequest createQueueRequest = new CreateQueueRequest("EVCAFE");
			srcQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();

			
			// Receive messages
			System.out.println("Receiving messages from EVCAFE.\n");
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(srcQueueUrl);

			// Wait for the visibility time out, so that unacknowledged messages reappear in the queue
			//System.out.println("Waiting for visibility timeout...");
			//Thread.sleep(TimeUnit.SECONDS.toMillis(10));

			receiveMessageRequest.setWaitTimeSeconds(10);

			messages = (ArrayList<Message>)sqs.receiveMessage(receiveMessageRequest).getMessages();

			SaveMessagesToTargetQueue();

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
		} /*catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}


	public void SaveMessagesToTargetQueue()
	{
		// get message object to be stored in the bucket.

		// Create a long string of characters for the message object to be stored in the bucket.
	    int stringLength = 300000;
	    char[] chars = new char[stringLength];
	    char[] msgchars = null;
	    
	    /*Arrays.fill(chars, 'x');
	    String myLongString = new String(chars);*/   // this is a large Message, so it is saved in S3 and sent to Target Queue 
	    
	    
		for (Message message : messages) {    // Message from src Queue still of small size that it can not be saved to S3 bucket, but can only be sent to Target SQS
			// Send the message.
			msgchars = message.getBody().toCharArray();
			System.arraycopy(msgchars, 0, chars, 0, msgchars.length);
			System.out.println("Chars Size is : " +  chars.length);
			String myLongString = new String(chars);
			System.out.println("myLongString" + myLongString);
			myMessageRequest = new SendMessageRequest(targetQueueUrl, myLongString);
			sqsExtended.sendMessage(myMessageRequest);
			System.out.println("Sent the message.");
		}


	}

}