package org.ei;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

public class UploadFileToS3 {

	
	/*static String bucketName = "ev-deploy-nyc";
	static String key = "DataLoadReport/201533.html";
	static String uploadFileName = "201533.html";*/
	static String bucketName = "datafabrication-reports";
	static String key = "2015/reports.css";
	static String uploadFileName = "reports.css";
	

	public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException, IOException {

		   AmazonS3 s3client = new AmazonS3Client(new PropertiesCredentials(new File("credentials.properties")));
			//AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
		        try {
		            System.out.println("Uploading a new object to S3 from a file\n");
		            File file = new File(uploadFileName);
		            s3client.putObject(new PutObjectRequest(bucketName, key, file));

		         } catch (AmazonServiceException ase) {
		            System.out.println("Caught an AmazonServiceException, which " +
		            		"means your request made it " +
		                    "to Amazon S3, but was rejected with an error response" +
		                    " for some reason.");
		            System.out.println("Error Message:    " + ase.getMessage());
		            System.out.println("HTTP Status Code: " + ase.getStatusCode());
		            System.out.println("AWS Error Code:   " + ase.getErrorCode());
		            System.out.println("Error Type:       " + ase.getErrorType());
		            System.out.println("Request ID:       " + ase.getRequestId());
		        } catch (AmazonClientException ace) {
		            System.out.println("Caught an AmazonClientException, which " +
		            		"means the client encountered " +
		                    "an internal error while trying to " +
		                    "communicate with S3, " +
		                    "such as not being able to access the network.");
		            System.out.println("Error Message: " + ace.getMessage());
		        }
		    }

}
