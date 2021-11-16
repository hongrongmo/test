package awslambda;

/**
 * 
 * @author TELEBH
 * @date: 02/18/2016
 * @version: 1.0
 * @description: a testing class for Invoking AWS Lambda Function (event-driven) from s3 Event source (i.e. upload file to S3)
 * The following is example Java code that reads incoming Amazon S3 events and upload to another S3 bucket
 * @Note: this class implements the RequestHandler interface provided in the aws-lambda-java-core library. 
 * @input: The S3Event type that the handler uses as the input type is one of the predefined classes in the aws-lambda-java-events
 * 		   The Context type that the handler uses as the second input parameter
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;



import org.apache.log4j.Logger;



import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

public class TestInvokeLambdaFunctionFromS3 implements RequestHandler <S3Event,String>{

	//Initialize the log4j logger 
	static final Logger log = Logger.getLogger(TestInvokeLambdaFunctionFromS3.class);
	InputStream objectData = null;
	
	
	public String handleRequest(S3Event s3event, Context context)
	{
		try
		{
		S3EventNotificationRecord record = s3event.getRecords().get(0);
		
		//Src S3 Bucket
		String srcBucketName = record.getS3().getBucket().getName();
		//Object Key may have spaces or unicod non-ASCII characters, so need to map it to UTF-8 unicod
		String srcKey = record.getS3().getObject().getKey().replace("+", "");
		srcKey = URLDecoder.decode(srcKey);
		
		//PrintInfo
		//log.debug("SourceBucket: " + srcBucketName + " and SrcKey: " + srcKey);
		System.out.println("BucketName: " + srcBucketName + " Key: " + srcKey);
		
		//Dest S3 Bucket
		String destBucketName = "ev-deploy-nyc";
		String destKey = "dataloadreport/"+srcKey;
		
		// validate that srcBucketName & destBucketName are not the same
		if(srcBucketName.equalsIgnoreCase(destBucketName))
		{
			log.error("Destination bucket must be different from Source Bucket");
			return "NOT OK";
		}
		
		//download the file from Source Bucket into a stream
		AmazonS3 s3Cleint = AmazonS3Service.getInstance().getAmazonS3Service();
		S3Object s3Object = s3Cleint.getObject(new GetObjectRequest(srcBucketName, srcKey));
		objectData = s3Object.getObjectContent();
		
		ObjectMetadata meta = s3Object.getObjectMetadata();
		
		
		//uploading to S3 destination bucket
		//log.debug("Upload S3 Object: " + srcKey + " To Dest S3 Bucket:  " + srcBucketName);
		System.out.println("Upload S3 Object: " + destKey + " To Dest S3 Bucket:  " + destBucketName);
		PutObjectRequest putRequest = new PutObjectRequest(destBucketName, destKey, objectData, meta);
		PutObjectResult putResult = s3Cleint.putObject(putRequest);
		//log.debug(putResult.getETag());
		System.out.println(putResult.getETag());
		
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
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
		
		finally
		{
			if(objectData !=null)
			{
				try
				{
					objectData.close();
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
		
		
		return "OK";
	}
	public static void main(String[] args) {
		

	}

}
