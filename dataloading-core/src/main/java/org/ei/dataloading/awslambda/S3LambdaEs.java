package org.ei.dataloading.awslambda;

import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.ei.dataloading.awss3.AmazonS3Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.lambda.runtime.events.S3Event;

/**
 * 
 * @author TELEBH
 * @Date: 07/27/2016
 * @Description: this class used for Lambda Function which is triggered with each S3 Event (Put/Delete Operation). 
 * based on S3 Event, Lambda will Index/Delete the Au/Af profile document to/from ES
 */
public class S3LambdaEs implements RequestHandler<S3Event, String>{
	
	private static final String FILE_TYPE = "json";
	private static AmazonS3 s3Client;
	private static JestClient jclient;
	
	S3Object object;
	private InputStream objectData;
	
	@Override
	public String handleRequest(S3Event s3event, Context context) {
		String bucketName= null;
		String key = null;
		try
		{
			S3EventNotificationRecord record = s3event.getRecords().get(0);
			bucketName = record.getS3().getBucket().getName();
			key = record.getS3().getObject().getKey().replace('+', ' ');
			key = URLDecoder.decode(key, "UTF-8");
			
			String eventName = record.getEventName();
			String doc_type = key.substring(0,key.indexOf("/")).trim();
			String doc_id = key.substring(key.lastIndexOf("/")+1,key.indexOf(".")).trim();
			System.out.println("BucketName: " + bucketName);
			System.out.println("Key name: " + key);
			System.out.println("Document Type: " + doc_type + "doc ID: " + doc_id);
			System.out.println("S3 EventName: " + eventName);
			
			// verify the file type
			Matcher matcher = Pattern.compile(".*\\.([^\\.]*)").matcher(key);
			if(!matcher.matches())
			{
				System.out.println("Unable to find the file type for key: " +  key);
				return "";
			}
			String fileType = matcher.group(1);
			if(!(FILE_TYPE.equals(fileType)))
			{
				System.out.println("Skipping non-json file: " + key);
			}
			
			// get Jest & AMazon S3 clients  
			jclient = AmazonEsService.getInstance().getAmazonEsService();
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			
			//get the Key's contents
			object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			objectData = object.getObjectContent();
			String profile_content = IOUtils.toString(objectData,"UTF-8");
			IOUtils.closeQuietly(objectData);
			//index document in ES index
			
			Map<String,Object> docSource = new HashMap<String, Object>();
			docSource.put(profile_content, new Date());
			
			Index index = new Index.Builder(profile_content).index("cafe").type(doc_type).id(doc_id).build();
			
					DocumentResult result = jclient.execute(index);
					if(result.isSucceeded())
					{
						System.out.println("Document: " +  result.getId() + "was successfully Indexed in ES, Response Code:  " +  result.getResponseCode());
						
						System.out.println(result.getJsonObject() + "Error Message: " + result.getErrorMessage());
						
					}
					else
					{
						System.out.println("Document: " + doc_id + "  Not indexed in ES");
						System.out.println(result.getErrorMessage() + result.getJsonString());
						
						System.out.println("Profile Content is: \n" + profile_content);
					}
					
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
			
			System.out.println("Profile file" +  bucketName+"/"+key + "not Exist in S3");
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
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(objectData !=null)
			{
				try
				{
					objectData.close();
				}
				catch(IOException ioex)
				{
					ioex.printStackTrace();
				}
			}
			
			try
			{
				if(object !=null)
				{
					object.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			if(jclient !=null)
			{
				try
				{
					jclient.shutdownClient();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			

		}
		
		return null;
	}
}
