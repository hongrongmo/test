package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;
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

	public static void UploadFileToS3(String esdir_name, String bucket_name)
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
	
	
	
	
	public static void DeleteFilesFromS3(LinkedHashSet<String> documentIds, String bucket_name , String profile_type)
	{
		List<KeyVersion> keys = new ArrayList<KeyVersion>();
		
		if(documentIds !=null && documentIds.size() >0 && profile_type !=null)
		{
			if(profile_type.contains("ipr"))
			{
				key = "affiliation";
			}
			else if (profile_type.contains("apr"))
			{
				key = "author";
			}
			try
			{

				// connect to AmazonS3
				s3Client = AmazonS3Service.getInstance().getAmazonS3Service();

				DeleteObjectsRequest multiObjectDeleteRequest = new DeleteObjectsRequest(bucket_name);
				
				for(String key: documentIds)
				{
					keys.add(new KeyVersion(key));
				}
				
				multiObjectDeleteRequest.setKeys(keys);
				
				
				// Delete from S3 which will trigger Lambda Function to delete from ES

				DeleteObjectsResult delObjResult = s3Client.deleteObjects(multiObjectDeleteRequest);
				System.out.format("Successfully deleted all the %s items.\n", delObjResult.getDeletedObjects().size());

			} 
			catch(MultiObjectDeleteException  ex)
			{
				System.out.format("%s \n", ex.getMessage());
				System.out.format("No. of objects successfully deleted = %s\n", ex.getDeletedObjects().size());
				System.out.format("No. of objects failed to delete = %s\n",  ex.getErrors().size());
				System.out.format("Printing error data........\n");
				for(DeleteError delete_error : ex.getErrors())
				{
					System.out.format("Object Key: %s\t%s\t%s\t%s\n" , delete_error.getKey() , delete_error.getCode() , delete_error.getMessage() , delete_error.getVersionId());
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
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
}

