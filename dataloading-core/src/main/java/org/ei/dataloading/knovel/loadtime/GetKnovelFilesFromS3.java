package org.ei.dataloading.knovel.loadtime;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.ei.dataloading.cafe.AmazonS3Service;
import org.ei.dataloading.cafe.GetANIFileFromCafeS3Bucket;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;

public class GetKnovelFilesFromS3 {
	
	private String bucketName = "";
	private String key="";
	
	AmazonS3 s3Client = null;
	private static GetKnovelFilesFromS3 instance = null;
	InputStream objectData = null;
	
	
	ArrayList<InputStream> fileContents = null;

	public GetKnovelFilesFromS3 () 
	{
		
	}

	public GetKnovelFilesFromS3 (String bucket, String key) 
	{
		this.bucketName = bucket;
		this.key = key;
		System.out.println("BucketName is: " +  this.bucketName);
		System.out.println("Key is: " +  this.key);
	}
	
	public static GetKnovelFilesFromS3 getInstance()
	{
		synchronized (GetKnovelFilesFromS3.class){


			if(instance == null)
			{
				instance =  new GetKnovelFilesFromS3();

			}
			return instance;	
		}

	}
	
	//HH 03/18/2016 download first group.xml file name from AmazonS3 bucket

	public void downloadGroupFileFromS3 (String fileName) throws AmazonClientException,AmazonServiceException, InterruptedException
	{
		//key="archive/KNOVEL/";
		try
		{
			System.out.println("downloading Group Knovel file: "+fileName+" from S3 bucket " + this.bucketName + ": ");
			System.out.println("download group file: " +  key+fileName);

			AmazonS3 s3Client = new AmazonS3Client(new PropertiesCredentials(new File("AwsCredentials.properties")));  // for local testing
			
			//AmazonS3 s3Client = new AmazonS3Client(new InstanceProfileCredentialsProvider());   // for ec2 testing
			S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key+fileName));


			System.out.println("etag "+object.getObjectMetadata());

			// download the file
			File file = new File(fileName);
			TransferManager tm = new TransferManager(s3Client);
			Download downloadFile = tm.download(bucketName, key+fileName, file);

			// check transfer's status to check its progress
			if(downloadFile.isDone() ==false)
			{
				System.out.println("Transfer: " + downloadFile.getDescription());
				System.out.println(" - State: " + downloadFile.getState());
				System.out.println(" - Progress: " + downloadFile.getProgress().getBytesTransferred());
			}

			// block the current thread and wait for the transfer to complete. if transfer fails; this method will throw 
			//AmazonClientException or AmazonServiceException 
			downloadFile.waitForCompletion();

			//after download is complete, call shutdownNow to release resources
			tm.shutdownNow();

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
		
	}

	public void getFileContentFromS3 (String fileName) throws AmazonClientException,AmazonServiceException, InterruptedException
	{
		//key="archive/KNOVEL/";
		try
		{	
			if(s3Client == null)
			{
				s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			}
		
			System.out.println("get File Content for file: "+fileName+" from S3 bucket "+ this.bucketName + ": ");
			
			S3Object object = s3Client.getObject(new GetObjectRequest (this.bucketName, key+fileName));
			objectData = object.getObjectContent();
			
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
	
	}
	
	public static void main(String[] args)
	{
		System.out.println("in GetKnovelFilesFromS3");
		String filename = args[0];
		
		GetKnovelFilesFromS3 obj = new GetKnovelFilesFromS3();
		
		try 
		{
			obj.bucketName = "datafabrication-reports";
			obj.downloadGroupFileFromS3(filename);
		} 
		catch (AmazonClientException | InterruptedException e) 
		{	
			e.printStackTrace();
		}
	}

	
}
