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
import java.util.HashMap;

import org.ei.dataloading.bd.loadtime.BaseTableDriver;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;

public class GetANIFileFromCafeS3Bucket {

	String bucketName = "";
	String key = "";
	boolean cafe = true;
	String action = "normal";
	String snsAction="";  // Action in SQS' SNS Metadata
	String s3FileLoc = "";  //in format of Bucket/Key for later tracing loaded files
	
	
	BufferedReader reader = null;
	
	BufferedReader S3Filecontents = null;
	
	String singleLine = null;
	String itemInfoStart = "";
	String itemInfo = "";
	String cpxIDInfo = "";
	String accessNumber = null;
	String epoch = "";
	String eid = "";
	
	private InputStream objectData;
	S3Object object;
	AmazonS3 s3Client = null;
	
	int updateNumber = 0;
	String database="cpx";
	Connection con = null;
	String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";  // for localhost 
	String driver = "oracle.jdbc.driver.OracleDriver";
	String username = "ap_correction1";
	String password = "ei3it";
	String sqlldrFileName ="";
	
	
	
	HashMap<String, String> objectMetadata = new HashMap<String,String>();
	
	
	private static GetANIFileFromCafeS3Bucket instance = null;

	public GetANIFileFromCafeS3Bucket(){}
	
	public GetANIFileFromCafeS3Bucket (String s3BucketName, String key)
	{
		this.bucketName = s3BucketName;
		this.key = key;
	}
	
	public GetANIFileFromCafeS3Bucket (AmazonS3 s3Client)
	{
		
		this.s3Client = s3Client;
	}
	
	
	public static GetANIFileFromCafeS3Bucket getInstance(String bucketName, String key)
	{
		synchronized (GetANIFileFromCafeS3Bucket.class){


			if(instance == null)
			{
				instance =  new GetANIFileFromCafeS3Bucket(bucketName, key);
			}
			return instance;	
		}

	}
	
	public static GetANIFileFromCafeS3Bucket getInstance(AmazonS3 s3Cleint)
	{
		synchronized (GetANIFileFromCafeS3Bucket.class){


			if(instance == null)
			{
				instance =  new GetANIFileFromCafeS3Bucket(s3Cleint);
			}
			return instance;	
		}

	}
	
	public void getFile(String bucketName, String key) throws AmazonClientException,AmazonServiceException, InterruptedException{

		this.bucketName = bucketName;
		this.key = key;
		try
		{
			//temp comment for now, as i moved them to DBCollectionCheck
			/*AmazonS3 s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			
			
			// works well for one single file from S3 bucket
			object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			
			objectData = object.getObjectContent();*/
			
			//objectFromS3.saveContentToFile(objectData);   // that's the one for creating file of content then send the file for parsing/converting
			/*GetANIFileFromCafeS3Bucket objectFromS3 = getInstance(bucketName,key);
			objectFromS3.parseS3File (objectData);  // parse based on content of s3*/	
			
			object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			
			objectData = object.getObjectContent();
			parseS3File(objectData);
			
	
			//objectData.close();
			
		}
		
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		/*
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
		}*/
		

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
		}
		
	}

	
	public void getFile(String bucketName, String key, int updatenumber
							,String database, String connectionURL, String driver,
							String username, String password,String sqlldrFileName) {

		this.bucketName = bucketName;
		this.key = key;
		
		this.updateNumber = updatenumber;
		this.database=database;
		
		this.connectionURL =  connectionURL;  // for localhost 
		this.driver = driver;
		this.username = username;
		this.password = password;
		this.sqlldrFileName = sqlldrFileName;
		
		try
		{
			
			/*if(object == null)
			{*/
				object = s3Client.getObject(new GetObjectRequest (bucketName, key));
				objectData = object.getObjectContent();
			/*}*/
			/*parseS3File (objectData, updateNumber,
					database, connectionURL, driver,
					username, password,sqlldrFileName);*/
				
				parseS3File (updateNumber,
						database, connectionURL, driver,
						username, password,sqlldrFileName);
				
			
		}
		
		catch(IOException ex)
		{
			ex.printStackTrace();
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
		}
		
	}

	
	public void saveContentToFile (InputStream s3FileContent) throws IOException
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
             
			} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		

	}
	
	/** works well for the AmazonSqs.java that i first used to run whole scenario of read msg from queue, parse its content, decide wether it is CPX record, then convert the file
	// this was basically called from java class "AmazonSqs.java"
	// Temp comment for now, as i updated the progtam to use TestSyncMultiMessageReceiverAknowledge to simulate whole process of get msg from queue,
	//parse it, decide whether it is CPX then convert record as "AmazonSqs.java" is doing but using JMS instead of AmazonSqs API
	 * **/
	 
	public boolean checkDBCollection(String bucketName, String key) throws AmazonClientException,AmazonServiceException, InterruptedException
	{
		boolean cpxCollection = false;
		String dbCollection="";
		String substr="";
		
		try
		{
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			
			object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			objectData = object.getObjectContent();
			
			
			// only parse "CPX" Records, skip any other dbcollection
			S3Filecontents = new BufferedReader(new InputStreamReader(objectData));
			while ((singleLine = S3Filecontents.readLine()) !=null)
			{
				if(singleLine.contains("<dbcollection>CPX</dbcollection>"))
				{
					System.out.println("Processing CPX record");
					cpxCollection = true;
					break;
				}
				else if(singleLine.contains("<dbcollection>"))
				{
					substr = singleLine.substring(singleLine.indexOf("<dbcollection>") +14, singleLine.length());
					dbCollection = substr.substring(0, substr.indexOf("<")).trim();
					System.out.println("CollectionType: " +  dbCollection);
					
					System.out.println("Skip this Key as it belongs to db collection: " +  dbCollection);
				}
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
		
		return cpxCollection;
		
	}
	
	
	public boolean checkDBCollection(String bucketName, String key , AmazonS3 s3Cleint) throws AmazonClientException,AmazonServiceException, InterruptedException
	{
		boolean cpxCollection = false;
		String dbCollection="";
		String substr="";

		try
		{

			object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			if(object !=null)
			{
				objectData = object.getObjectContent();


				// only parse "CPX" Records, skip any other dbcollection
				S3Filecontents = new BufferedReader(new InputStreamReader(objectData));
				while ((singleLine = S3Filecontents.readLine()) !=null)
				{

					if(singleLine.contains("<item-info>"))
					{
						itemInfoStart = singleLine.substring(singleLine.indexOf("<item-info>") , singleLine.length());
						if(itemInfoStart.length() >0 && itemInfoStart.contains("</item-info>"))
						{
							itemInfo = itemInfoStart.substring(0,itemInfoStart.indexOf("</item-info>") +12);


							if(itemInfo.length() >0 && itemInfo.contains("<itemid idtype=\"CPX\">") && itemInfo.contains("<dbcollection>CPX</dbcollection>"))
							{
								cpxIDInfo = itemInfo.substring(itemInfo.indexOf("<itemid idtype=\"CPX\">"),itemInfo.length());
								accessNumber = cpxIDInfo.substring(cpxIDInfo.indexOf(">")+1,cpxIDInfo.indexOf("</itemid>"));


								cpxCollection = true;
								System.out.println("CPX file: " + key);
							}
							else if(itemInfo.length() >0 && itemInfo.contains("<dbcollection>"))
							{
								dbCollection = itemInfo.substring(itemInfo.indexOf("<dbcollection>"), itemInfo.indexOf("</item-info>"));
								System.out.println("CollectionType: " +  dbCollection);

								System.out.println("Skip this Key as it belongs to db collection: " +  dbCollection);
							}
							
							//get the Object's epoch Metadata
							if(singleLine.contains("<xocs:indexeddate epoch"))
							{
								substr = singleLine.substring(singleLine.indexOf("<xocs:indexeddate epoch="), singleLine.length());
								epoch = substr.substring(substr.indexOf("=")+1, substr.indexOf(">")).trim().replace("\"", "");
								System.out.println("epoch: " + epoch);
							}
							
							//get the object's EID metadata
							
							if(singleLine.contains("<xocs:eid>"))
							{
								eid = singleLine.substring(singleLine.indexOf("<xocs:eid>")+10, singleLine.indexOf("</xocs:eid>"));
								System.out.println("EID: " + eid);
							}
							
							// save Record's MetadaData in CafeRecordMetaData
							CafeRecordMetaData.SetKeyValue("EPOCH", epoch);
							CafeRecordMetaData.SetKeyValue("EID", eid);
						}
					}
				}
			}
			else
			{
				System.out.println("Key: " + key + " Not exist in specified bucket: " +  bucketName);
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
		}

		return cpxCollection;

	}

	
	public void fileContentMetadata(String bucketName, String key , AmazonS3 s3Cleint) throws AmazonClientException,AmazonServiceException, InterruptedException
	{
		String substr="";

		try
		{
			if(object ==null)
			{
				object = s3Client.getObject(new GetObjectRequest (bucketName, key));
				objectData = object.getObjectContent();

				if(object !=null)
				{
					// get get the Object's some major Metdata from s3FIleContents for later check (i.e. EID, epoch,..)
					S3Filecontents = new BufferedReader(new InputStreamReader(objectData));
					while ((singleLine = S3Filecontents.readLine()) !=null)
					{
						//get the Object's epoch Metadata
						if(singleLine.contains("<xocs:indexeddate epoch"))
						{
							substr = singleLine.substring(singleLine.indexOf("<xocs:indexeddate epoch="), singleLine.length());
							epoch = substr.substring(substr.indexOf("=")+1, substr.indexOf(">")).trim().replace("\"", "");
							System.out.println("epoch: " + epoch);
						}

						//get the object's EID metadata

						if(singleLine.contains("<xocs:eid>"))
						{
							eid = singleLine.substring(singleLine.indexOf("<xocs:eid>")+10, singleLine.indexOf("</xocs:eid>"));
							System.out.println("EID: " + eid);
						}

						// save Record's MetadaData in CafeRecordMetaData
						CafeRecordMetaData.SetKeyValue("EPOCH", epoch);
						CafeRecordMetaData.SetKeyValue("EID", eid);
					}
				}
				else
				{
					System.out.println("Key: " + key + " Not exist in specified bucket: " +  bucketName);
				}
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
	}


}
	
	
	public void parseS3File (InputStream s3FileContent) throws IOException
	{
		int updateNumber = 20160301;
		String database="cpx";
		Connection con = null;
		
		//String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";  // for localhost
		String connectionURL = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid"; 
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
	
	
	
	/*public void parseS3File (InputStream s3FileContent, int updateNumber,
							String database, String connectionURL, String driver,
							String username, String password, String sqlldrFileName) throws IOException*/
	public void parseS3File (int updateNumber,
			String database, String connectionURL, String driver,
			String username, String password, String sqlldrFileName) throws IOException
	{

		reader = new BufferedReader(new InputStreamReader(objectData));
		reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(reader.readLine().replaceAll("><", ">\n<").getBytes())));
		
		s3FileLoc = this.bucketName+"/"+this.key;
		System.out.println("Key S3 Location: " + s3FileLoc);
		
		try {
			 BaseTableDriver c = new BaseTableDriver(updateNumber,database,action,s3FileLoc);
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
             
            
                 System.out.println("sql loader file "+dataFile+" created;");
                 System.out.println("about to load data file "+dataFile);
                 System.out.println("press enter to continue");
                 //System.in.read();
                 Thread.currentThread().sleep(1000);
             
             Runtime r = Runtime.getRuntime();

             Process p = r.exec("./"+sqlldrFileName+" "+dataFile);
             int t = p.waitFor();
             
             
             
			} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		

	}
	
	
}
