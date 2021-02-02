package org.ei.dataloading.awss3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;

/**
 * @author TELEBH
 * @Date: 02/01/201
 * @Description: Thread Class to upload File/Dir to S3 Bucket
 */
public class UploadFileToS3Thread extends Thread{
	
	String threadName = null;
	AmazonS3 s3Client = null;
	String bucketName = null;
	String key = null;
	String filePath = null;
	int loadNumber;


	public UploadFileToS3Thread(String threadName)
	{
		super();
		this.threadName = threadName;
	}
	public UploadFileToS3Thread(String threadName, AmazonS3 s3Client, String s3Bucket, String fileName, String path, int loadNumber)
	{
		this(threadName);
		this.s3Client = s3Client;
		bucketName = s3Bucket;
		key = "cpx/" + Integer.toString(loadNumber) + "/" + fileName;
		filePath = path;
		this.loadNumber = loadNumber;
	}

	public UploadFileToS3Thread(String threadName, AmazonS3 s3Client, String bucket, String file, int loadNumber)
	{
		this(threadName);
		this.s3Client = s3Client;
		this.bucketName = bucket;
		this.key = Integer.toString(loadNumber);
	}
	
	public void start() {
		if (threadName == null) {
			try {
				Thread th = new Thread(this, threadName);
				th.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void run()
	{
		PutObjectResult putObjResult = null;
		System.out.println("Running Thread: " + threadName);
		
		System.out.println("starting download files for " + threadName);
		
					
		try {
			
			//upload a file as a new object with Content Type & some metadata
			PutObjectRequest request = new PutObjectRequest(bucketName, key, new File(filePath));
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("text/xml");
			metadata.addUserMetadata("m_id", key);
			metadata.addUserMetadata("loadNumber", Integer.toString(loadNumber));
			request.setMetadata(metadata);
			
			//System.out.println("Key: " + request.getKey());
			putObjResult = s3Client.putObject(request);
			if(putObjResult.getETag().isEmpty())
				System.out.println("Failed to upload file: " + request.getKey());
			
		}

		catch(AmazonServiceException ase)
		{
			System.out.println("The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch (SdkClientException e) {
			System.out.println("Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.");
            e.printStackTrace();
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
		
		catch (Exception e) 
		{
			System.out.println("Exception for thread: " + threadName + " !!!");
			System.out.println("Reason: " + e.getMessage()); 
			e.printStackTrace();
		}

	}
	public void run1()
	{
		PutObjectResult putObjResult = null;
		System.out.println("Running Thread: " + threadName);
		
		System.out.println("starting download files for " + threadName);
		
					
		try {

			//long startTime = System.currentTimeMillis();
			//System.out.println("startTime: "+ startTime);
			
			//upload a file as a new object with Content Type & some metadata
			PutObjectRequest request = new PutObjectRequest(bucketName, key, new File(filePath));
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("text/xml");
			metadata.addUserMetadata("m_id", key);
			metadata.addUserMetadata("loadNumber", Integer.toString(loadNumber));
			request.setMetadata(metadata);
			
			//System.out.println("Key: " + request.getKey());
			putObjResult = s3Client.putObject(request);
			if(putObjResult.getETag().isEmpty())
				System.out.println("Failed to upload file: " + request.getKey());
			/*
			else
				System.out.println("Upload S3 Key " + filePath + File.separator + key + 
				 " uploaded to S3: " + bucketName + " with Etag: " + putObjResult.getETag());
*/
			
			// ONLY for debugging, uncomment when needed
			//long endTime = System.currentTimeMillis();
			//System.out.println("endTime: "+ endTime);
			
			//System.out.println("Total Time to upload xml file to S3 buket: " + (endTime - startTime)/1000 + " seconds....");
			//
		}

		catch(AmazonServiceException ase)
		{
			System.out.println("The call was transmitted successfully, but Amazon S3 couldn't process it, so it returned an error response.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch (SdkClientException e) {
			System.out.println("Amazon S3 couldn't be contacted for a response, or the client couldn't parse the response from Amazon S3.");
            e.printStackTrace();
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
		
		catch (Exception e) 
		{
			System.out.println("Exception for thread: " + threadName + " !!!");
			System.out.println("Reason: " + e.getMessage()); 
			e.printStackTrace();
		}

	}
}
