package org.ei.dataloading.cafe.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.ei.dataloading.cafe.AmazonS3Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class TestGetS3ObjectVersionid {

	static String bucketName="sc-ani-xml-prod";
	static String key="0000334321";
	
	public static void main(String[] args) throws AmazonClientException,AmazonServiceException, InterruptedException{

		try
		{
			AmazonS3 s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			
			GetObjectRequest request = new GetObjectRequest(bucketName, key);
			Date dt = request.getModifiedSinceConstraint();
			
			// works well for one single file from S3 bucket
			//S3Object object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			S3Object object = s3Client.getObject(request);
			
			InputStream objectData = object.getObjectContent();
			
			
			
			
			objectData.close();
			
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
