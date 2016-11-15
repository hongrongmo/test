package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ei.dataloading.awss3.AmazonS3Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;


import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;


public class DownloadloadFileFromS3 {

	
	
	static String bucketName = "datafabrication-reports";
	static String key = "2015/reports.css";
	static String uploadFileName = "reports.css";
	
	public DownloadloadFileFromS3()
	{
		
	}
	
	public DownloadloadFileFromS3(String s3BucketName, String key)
	{
		this.bucketName = s3BucketName;
		this.key = key;
	}
	public static void getFileFromS3(String s3BucketName, String fileKey) throws AmazonClientException,AmazonServiceException, InterruptedException
	{	
		
		try
		{
			AmazonS3 s3client = AmazonS3Service.getInstance().getAmazonS3Service();
			
			
			System.out.println("downloading CAFE FILE:"+fileKey+" from S3 bucket: "+s3BucketName);
			System.out.println("S3FilePathe: " + "s3://"+s3BucketName+"/"+fileKey);
			
			
			S3Object request = s3client.getObject(new GetObjectRequest(s3BucketName, fileKey));
			
			System.out.println("etag "+request.getObjectMetadata());
			
			// download the file
			File file = new File(fileKey+".xml");
			TransferManager tm = new TransferManager(s3client);
			Download downloadFile = tm.download(s3BucketName, fileKey, file);
			
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
	
	

}
