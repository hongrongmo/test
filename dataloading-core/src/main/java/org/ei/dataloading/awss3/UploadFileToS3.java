package org.ei.dataloading.awss3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
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
	static String loadNumber;

	static TransferManager tmanager;

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
		if(args.length >2)
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

				loadNumber = args[2];

			}
		}
		else
		{
			System.out.println("not enough parameters!");
			System.exit(1);
		}

		uploadDirToS3Bucket();


	}

	public static void uploadDirToS3Bucket() throws AmazonClientException,AmazonServiceException
	{
		try
		{
			AmazonS3 s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			tmanager = new TransferManager(s3Client);

			String currDir = System.getProperty("user.dir");
			File zipsDir = new File(currDir+"/zips/" + loadNumber);


			if(!(zipsDir.exists()))
			{
				throw new FileNotFoundException();
			}

			//Check if Dir isNotEmpty (contains file), otherwise skip upload to S3
			if(!(isEmptyDir(zipsDir)))
			{
				System.out.println("Uploading Dir: " + zipsDir.getAbsolutePath() + " to S3 Bucket");

				// upload vtw zip dir to S3
				MultipleFileUpload uploadFile = tmanager.uploadDirectory(bucketName, key, zipsDir, true);

				// check transfer's status to check its progress
				/*if(uploadFile.isDone() ==false)
				{
					System.out.println("Transfer: " + uploadFile.getDescription());
					System.out.println(" - State: " + uploadFile.getState());
					System.out.println(" - Progress: " + uploadFile.getProgress().getBytesTransferred());
				}*/
				uploadFile.addProgressListener(new ProgressListener()
				{
					@Override
					public void progressChanged(ProgressEvent progressEvent)
					{
						System.out.println("Transfer: " + progressEvent.getBytesTransferred());
					}
				}	);
				
				// block the current thread and wait for the transfer to complete. if transfer fails; this method will throw 
				//AmazonClientException or AmazonServiceException 
				uploadFile.waitForCompletion();
				

			}
			else
			{
				System.out.println("Dir is Empty!");
				System.exit(1);
			}


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
		catch(InterruptedException ex)
		{
			ex.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally
		{
			if(tmanager !=null)
			{
				//after download is complete, call shutdownNow to release resources
				tmanager.shutdownNow(false);  
			}

		}
	}


	public static boolean isEmptyDir(File dir)
	{
		boolean empty = true;

		String[] fileNames = dir.list();
		try
		{
			if(fileNames !=null && fileNames.length >0)
			{
				System.out.println("Total zip files to upload to S3: " +  fileNames.length);
				empty = false;
			}
		}
		catch(SecurityException ex)
		{
			ex.printStackTrace();
		}
		

		return empty;
	}

}