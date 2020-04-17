package org.ei.dataloading.cafe.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import org.ei.dataloading.awss3.AmazonS3Service;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * 
 * @author TELEBH
 *
 */
// Test download Object from S3 bucket using InputStream
public class TestGetObjectFromS3Bucket {
	
	BufferedReader reader = null;
	String substr = "";
	String documentType = "";
	

	public TestGetObjectFromS3Bucket(){}
	public static void main(String[] args) {
		
		String bucketName = "sc-ani-xml-prod";
		String key = "56015952900";
		
		TestGetObjectFromS3Bucket objectFromS3 = new TestGetObjectFromS3Bucket();
		
		try
		{
			AmazonS3 s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			
			//List objects
			
			ListObjectsRequest listRequest = new ListObjectsRequest().withBucketName(bucketName);
			ObjectListing objectListing;
			do
			{
				objectListing = s3Client.listObjects(listRequest);
				for(S3ObjectSummary summary: objectListing.getObjectSummaries())
				{
					//System.out.println("ObjectSummary: " +  summary.getKey());
					
					S3Object object = s3Client.getObject(new GetObjectRequest (bucketName, summary.getKey()));
					
					InputStream objectData = object.getObjectContent();
					
					//Process the objectData stream;
					objectFromS3.parseObjectData(objectData);
					
					objectData.close();
					
				}
			}
			while (objectListing.isTruncated());
						
			
			// works well for one single file from S3 bucket
			/*S3Object object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			
			InputStream objectData = object.getObjectContent();
			
			//Process the objectData stream;
			TestGetObjectFromS3Bucket objectFromS3 = new TestGetObjectFromS3Bucket();
			objectFromS3.parseObjectData(objectData);
			
			objectData.close();
			*/
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

	public void parseObjectData (InputStream s3FileContent) throws IOException
	{
		reader = new BufferedReader(new InputStreamReader(s3FileContent));
		/*BufferedReader reader = new BufferedReader(new InputStreamReader(s3FileContent));
		int updateNumber = 20160301;
		String database="cpx";
		Connection con = null;
		
		String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";
		String driver = "oracle.jdbc.driver.OracleDriver";
		String username = "ap_correction1";
		String password = "ei3it";
		*/
		
		
		while (true)
		{
			String line = reader.readLine();
			if(line == null)
				break;
			if(line.contains("content-type"))
			{
				//System.out.println(line);
				
				substr = line.substring(line.indexOf("content-type") +14, line.length());
				documentType = substr.substring(0, substr.indexOf("\"")).trim();
				System.out.println("DocumentType: " +  documentType);
			}
		}
		//System.out.println();
		
	/*	try {
				BaseTableDriver c = new BaseTableDriver(updateNumber,database);
				con = c.getConnection(connectionURL, driver, username, password);
				c.writeRecs(reader, con);
			} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		*/

	}
}
