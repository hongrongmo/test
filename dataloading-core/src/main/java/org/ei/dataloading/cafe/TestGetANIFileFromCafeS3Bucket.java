package org.ei.dataloading.cafe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;

import org.ei.dataloading.bd.loadtime.BaseTableDriver;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;

public class TestGetANIFileFromCafeS3Bucket {

	static String bucketName = "sc-ani-xml-prod";
	
	//0000014153,0000001226
	static String key = "0000001226";
	boolean cafe = true;
	
	BufferedReader reader = null;
	String substr = "";
	String documentType = "";
	

	public TestGetANIFileFromCafeS3Bucket(){}
	public static void main(String[] args) throws AmazonClientException,AmazonServiceException, InterruptedException{

		try
		{
			AmazonS3 s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			
			
			// works well for one single file from S3 bucket
			S3Object object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			
			InputStream objectData = object.getObjectContent();
			
			//Process the objectData stream;
			TestGetANIFileFromCafeS3Bucket objectFromS3 = new TestGetANIFileFromCafeS3Bucket();
			//objectFromS3.parseObjectData(objectData);   // that's the one for creating file of content then send the file
			objectFromS3.parseS3File (objectData);  // parse based on content of s3
			
			/*// download the file
			File file = new File(key+".xml");
			TransferManager tm = new TransferManager(s3Client);
			Download downloadFile = tm.download(bucketName, key, file);
			
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
			
			objectFromS3.parseS3File (file);
			
			
			//after download is complete, call shutdownNow to release resources
			tm.shutdownNow();*/
			
			
			
			
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

	public void parseObjectData (InputStream s3FileContent) throws IOException
	{
		reader = new BufferedReader(new InputStreamReader(s3FileContent));
		reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(reader.readLine().replaceAll("><", ">\n<").getBytes())));
		int updateNumber = 20160301;
		String database="cpx";
		Connection con = null;
		
		String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";
		String driver = "oracle.jdbc.driver.OracleDriver";
		String username = "ap_correction1";
		String password = "ei3it";
		
		//System.out.println("S3Content: " +  reader.readLine());
		
		// temp comment out as reader is null after this loop

		File file = new File(key+".xml");
		if (!file.exists()) 
		{
			//file.createNewFile();
			System.out.println("Out FIle name is : " + file.getName());
		}
		
		String line = null;
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file.getName(),true)));
		while ((line = reader.readLine()) !=null)
		{
			out.println(line);
			//String line = reader.readLine().replaceAll("><", ">\n<");
			//if(line == null)
				//break;			
				//System.out.println(line);

			// for fetching file-type
			/*if(line.contains("content-type"))
			{
				System.out.println(line);
				
				substr = line.substring(line.indexOf("content-type") +14, line.length());
				documentType = substr.substring(0, substr.indexOf("\"")).trim();
				System.out.println("DocumentType: " +  documentType);
			}*/
		}
		//System.out.println();
		out.close();
		
		
		try {
			 BaseTableDriver c = new BaseTableDriver(updateNumber,database);
			 con = c.getConnection(connectionURL, driver, username, password);
             c.setBlockedIssnList(con);
             c.writeBaseTableFile(file.getName(),con);
             String dataFile=file.getName()+"."+updateNumber+".out";
             File f = new File(dataFile);
             if(!f.exists())
             {
                 System.out.println("datafile: "+dataFile+" does not exists");
                 System.exit(1);
             }
             
				/*BaseTableDriver c = new BaseTableDriver(updateNumber,database);
				con = c.getConnection(connectionURL, driver, username, password);
				c.writeRecs(reader, con);*/
			} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		

	}
	
	public void parseS3File (InputStream s3FileContent) throws IOException
	{
		int updateNumber = 20160301;
		String database="cpx";
		Connection con = null;
		
		String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";
		String driver = "oracle.jdbc.driver.OracleDriver";
		String username = "ap_correction1";
		String password = "ei3it";
		
		
		reader = new BufferedReader(new InputStreamReader(s3FileContent));
		reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(reader.readLine().replaceAll("><", ">\n<").getBytes())));
		
		
		try {
			 BaseTableDriver c = new BaseTableDriver(updateNumber,database);
			 con = c.getConnection(connectionURL, driver, username, password);
             c.setBlockedIssnList(con);
             c.writeBaseTableFile(key,con,reader,cafe);
             String dataFile=key+"."+updateNumber+".out";
             File f = new File(dataFile);
             if(!f.exists())
             {
                 System.out.println("datafile: "+dataFile+" does not exists");
                 System.exit(1);
             }
             
				/*BaseTableDriver c = new BaseTableDriver(updateNumber,database);
				con = c.getConnection(connectionURL, driver, username, password);
				c.writeRecs(reader, con);*/
			} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		

	}
	
}
