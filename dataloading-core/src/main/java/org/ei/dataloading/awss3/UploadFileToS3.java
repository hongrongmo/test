package org.ei.dataloading.awss3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

/**
 * 
 * @author TELEBH
 * @Date: 09/21/2016
 * @Description: Upload file(s)/directory to S3 bucket
 * 
 */
public class UploadFileToS3 {

	static String bucketName = null;
	static String key = null;
	static int loadNumber;

	public UploadFileToS3()
	{

	}
	public UploadFileToS3(String s3Bucket, String s3Key)
	{
		bucketName = s3Bucket;
		key = s3Key;
	}

	public static void main(String[] args) throws AmazonClientException,AmazonServiceException, InterruptedException
	{
		if(args.length >3)
		{
			if(args[0] !=null)
			{
				bucketName = args[0];
				System.out.println("S3Bucket: " + bucketName);
			}
			if(args[1] !=null)
			{
				key = args[1];
				System.out.println("Key: " +  key);
			}
			if(args[2] !=null)
			{
				if(Pattern.matches("^\\d*$", args[2]))
				{
					loadNumber = Integer.parseInt(args[2]);
				}
				else
				{
					System.out.println("loadNumber has wrong format");
					System.exit(1);
				}
			}
		}
		else
		{
			System.out.println("not enough parameters!");
			System.exit(1);
		}

	}

	public static void uploadFileToS3Bucket() throws AmazonClientException,AmazonServiceException
	{
		try
		{
			AmazonS3 s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			TransferManager tmanager = new TransferManager(s3Client);
			Upload upload = tmanager.upload(bucketName, key, new File(Integer.toString(loadNumber)));
			
			// poll transfer's status to check it's progress
			if(upload.isDone() ==false)
			{
				System.out.println("Transer: " + upload.getDescription());
				System.out.println(" -State: " + upload.getState());
				System.out.println(" -Progress: " + upload.getProgress().getBytesTransferred());
			}
			
			/*block the current thread and wait for the transfer to complete.
			// if the transfer fails, this method will throw an AmazonClientException or AMazonServiceException 
			 detailing the reason.
			 */
			upload.waitForCompletion();
				
			// After the upload is complete, call shutDown to release the resources.
			tmanager.shutdownNow();

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
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		catch(FileNotFoundException ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}


}
