package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;


/**
 * 
 * @author TELEBH
 * @Date: 07/20/2016
 * @Description: Upload Extracted ElasticSearch Author/Institution Profiles to S3 bucket for index in Elastic Search
 * Each S3 Put/delete operation will trigger Lambda Function index/delete document 
 */
public class UploadAuAfESToS3 {

	private static AmazonS3 s3Client;
	private static String s3BucketName = "evcafe";
	private static String key = "";

	public UploadAuAfESToS3(){}

	public static void UploadFileToS3(String esdir_name, String bucket_name, String operation)
	{
		TransferManager tm = null;
		
		if(esdir_name !=null)
		{
			if(esdir_name.contains("ipr"))
			{
				key = "affiliation";
			}
			else if (esdir_name.contains("apr"))
			{
				key = "author";
			}
			try
			{

				// connect to AmazonS3
				s3Client = AmazonS3Service.getInstance().getAmazonS3Service();

				// upload ES dir to S3

				tm = new TransferManager(s3Client);
				MultipleFileUpload uploadFile = tm.uploadDirectory(s3BucketName, key, new File(esdir_name), true);
				
				// check transfer's status to check its progress
				if(uploadFile.isDone() ==false)
				{
					System.out.println("Transfer: " + uploadFile.getDescription());
					System.out.println(" - State: " + uploadFile.getState());
					System.out.println(" - Progress: " + uploadFile.getProgress().getBytesTransferred());
				}
				
				// block the current thread and wait for the transfer to complete. if transfer fails; this method will throw 
				//AmazonClientException or AmazonServiceException 
				uploadFile.waitForCompletion();

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
				System.out.println("HTTP Status Code: " + ace.getCause());
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			finally
			{
				if(tm !=null)
				{
					//after download is complete, call shutdownNow to release resources
					tm.shutdownNow();  
				}
			}
		}
	}
}

