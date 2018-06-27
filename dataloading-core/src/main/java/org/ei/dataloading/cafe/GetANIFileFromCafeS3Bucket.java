package org.ei.dataloading.cafe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.ei.dataloading.awss3.AmazonS3Service;
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
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;


public class GetANIFileFromCafeS3Bucket {

	String bucketName = "";
	String key = "";
	boolean cafe = true;
	String action = "normal";
	String msgAction="";  // Action in SQS' SNS Metadata
	long msgEpoch;
	String s3FileLoc = "";  //in format of Bucket/Key for later tracing loaded files

	//HH 04/28/2016 id_start and id_end for out filename
	int id_start = 0;
	int id_end = 0;

	//HH 05/02/2016 to download Cafe Keys from S3 bucket to filesystem
	File s3Dir = null;
	File file = null;
	//PrintWriter out = null;  // 06/01/2017 temp comment out in multithreading env, return it back when no multithreading
	
	
	static BufferedReader reader = null; 
	BufferedReader S3Filecontents = null;


	String singleLine = null;
	String itemInfoStart = "";
	String itemInfo = "";
	String cpxIDInfo = "";
	String accessNumber = null;
	String epoch = "";
	String eid = "";

	byte[] bytes= null;
	private InputStream objectData;
	S3Object object;
	AmazonS3 s3Client = null;

	int loadNumber = 0;
	String database="cpx";
	Connection con = null;
	String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";  // for localhost 
	String driver = "oracle.jdbc.driver.OracleDriver";
	String username = "ap_correction1";
	String password = "ei3it";
	String sqlldrFileName ="";
	String filename="";

	
	
	
	StringBuffer strb = new StringBuffer();   //04/29/2016 to combine multiple key's content before converting

	Runtime rt;


	HashMap<String, String> objectMetadata = new HashMap<String,String>();


	private static GetANIFileFromCafeS3Bucket instance = null;

	public GetANIFileFromCafeS3Bucket(){}

	public GetANIFileFromCafeS3Bucket (String s3BucketName, String key)
	{
		this.bucketName = s3BucketName;
		this.key = key;
	}

	public GetANIFileFromCafeS3Bucket (AmazonS3 s3Client,int loadnumber,String database,String url,String driver,String username,String password,String sqlldrFileName)
	{

		this.s3Client = s3Client;

		this.loadNumber = loadnumber;
		this.database=database;

		this.connectionURL =  url;  // for localhost 
		this.driver = driver;
		this.username = username;
		this.password = password;
		this.sqlldrFileName = sqlldrFileName;

	}
	
	//05/02/2016 to download Cafe files from S3 bucket
	public GetANIFileFromCafeS3Bucket (AmazonS3 s3Client,String database,String url,String driver,String username,String password, File s3dir)
	{

		this.s3Client = s3Client;

		this.database=database;

		this.connectionURL =  url;  // for localhost 
		this.driver = driver;
		this.username = username;
		this.password = password;
		this.s3Dir = s3dir;

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
	

	public synchronized String getFile(String bucketName, String key) throws AmazonClientException,AmazonServiceException, InterruptedException{

		this.bucketName = bucketName;
		this.key = key;
		
		//HH 05/24/2016 to write only keys that were downloaded successfully (exist in S3)
		String errorCode = null;
		
		S3Object s3object = null;
		InputStream s3objectData = null;
		
		
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

			
			s3object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			s3objectData = s3object.getObjectContent();
			//parseS3File(objectData);  // for single file testing
			
			if(s3objectData !=null)
			{
				saveContentToFile(s3objectData);
			}
					
			//objectData.close();

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
			
			System.out.println("Cafe file " +  bucketName+"/"+key + " not Exist in S3");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
			
			errorCode = ase.getErrorCode();
		}
		catch(AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
			errorCode = ace.getMessage();
		}


		finally
		{
			if(s3objectData !=null)
			{
				try
				{
					s3objectData.close();
				}
				catch(IOException ioex)
				{
					ioex.printStackTrace();
				}
			}
			
			try
			{
				if(s3object !=null)
				{
					s3object.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

		return errorCode;
	}


	public void getFile(String bucketName, String key, String msgAction) {


		this.bucketName = bucketName;
		this.key = key;
		this.msgAction = msgAction;


		try
		{
			object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			objectData = object.getObjectContent();


			if(object !=null && objectData !=null)
			{

				/*parseS3File (objectData,updateNumber,
								database, connectionURL, driver,
								username, password,sqlldrFileName);
				 */
				parseS3File ();

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
			if(reader !=null)
			{
				try
				{
					reader.close();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}


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


	public void getFileContent(String bucketName, String key, String msgAction) {


		this.bucketName = bucketName;
		this.key = key;
		this.msgAction = msgAction;


		try
		{
			object = s3Client.getObject(new GetObjectRequest (bucketName, key));
			objectData = object.getObjectContent();

			BufferedReader rd = new BufferedReader(new InputStreamReader(objectData));
			String str="";


			if(object !=null && objectData !=null)
			{

				try 
				{
					while((str=rd.readLine()) !=null)
					{
						strb.append(str);
					}
				} 
				catch (IOException e) {

					e.printStackTrace();
				}
				
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
		}
		finally
		{
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
		}
	}



	// temp comment out on 05/19/2017, need to uncomment when needed
/*	public void saveContentToFile (InputStream s3FileContent) throws IOException
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


	}*/
	
	
	//HH 05/02/2016 as per NYC meeting, download each 20k from S3 bucket, zip them for Converting process
	
	public void saveContentToFile (InputStream objectData) throws IOException
	{
		 BufferedReader breader = null;
		 PrintWriter out = null;
		try
		{
			breader = new BufferedReader(new InputStreamReader(objectData));
			breader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(breader.readLine().replaceAll("><", ">\n<").getBytes())));
		
		File file = new File(s3Dir.getName()+"/"+key+".xml");
		if (!file.exists()) 
		{
			System.out.println("Downloaded: "+file.getName());
			
		}
		else
		{
			System.out.println("file:" +  file.getName() + "already exist");
		}

		String line = null;
		out = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(),true)));
		while ((line = breader.readLine()) !=null)
		{
			out.println(line);

		}
		
		}
		catch (IOException e) {

			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			try
			{
				if(breader !=null)
				{
					breader.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			try
			{
				if(out !=null)
				{
					out.flush();
					out.close();
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
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


	//public void fileContentMetadata(String bucketName, String key , AmazonS3 s3Cleint) throws AmazonClientException,AmazonServiceException, InterruptedException
	public void fileContentMetadata(String bucketName, String key) throws AmazonClientException,AmazonServiceException, InterruptedException
	{
		String substr="";

		try
		{
			/*if(object ==null)
			{
				object = s3Client.getObject(new GetObjectRequest (bucketName, key));
				objectData = object.getObjectContent();*/

			//if(object !=null)
			if(objectData !=null)
			{
				// get get the Object's some major Metdata from s3FIleContents for later check (i.e. EID, epoch,..)
				//S3Filecontents = new BufferedReader(new InputStreamReader(objectData));
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
			//}
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

		/*finally
	{
		if(s3FileContentCopy !=null)
		{
			try
			{
				s3FileContentCopy.close();
			}
			catch(IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
	}*/


	}

	private boolean CheckMsgObjectEpoch()
	{
		boolean updatable = false;

		//if the action is “update”, the user only processes the message if “epoch” in the incoming message is later than “epoch” from the object’s UserMetadata 
		long objEpoch = Long.parseLong(CafeRecordMetaData.getValue("EPOCH"));

		if (this.msgEpoch > objEpoch) 
		{
			updatable = true;
		}	

		return updatable;
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


		} 
		catch (Exception e) {

			e.printStackTrace();
		}


	}



	/*public void parseS3File (InputStream s3FileContent, int updateNumber,
							String database, String connectionURL, String driver,
							String username, String password, String sqlldrFileName) throws IOException*/
	public void parseS3File () throws IOException
	{

		reader = new BufferedReader(new InputStreamReader(objectData));
		reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(reader.readLine().replaceAll("><", ">\n<").getBytes())));



		s3FileLoc = this.bucketName+"/"+this.key;
		System.out.println("Key S3 Location: " + s3FileLoc);


		try {
			BaseTableDriver c = new BaseTableDriver(loadNumber,database,action,s3FileLoc);
			con = c.getConnection(connectionURL, driver, username, password);
			//HH 04/21/2016 similar as correction, check block ISSN/EISSN only for Add/Update
			//HH 04/25/2016 as per Frank request, temporarirly disable ISSN/E-ISSN for Cafe Initial Refeed
			/* if(msgAction !=null && !(msgAction.equalsIgnoreCase("d")))
			 {
	             c.setBlockedIssnList(con);
			 }*/
			c.writeBaseTableFile(key,con,reader,cafe);
			String dataFile=key+"."+loadNumber+".out";
			File f = new File(dataFile);
			if(!f.exists())
			{
				System.out.println("datafile: "+dataFile+" does not exists");
				System.exit(1);
			}


			// only for testing
			/*  System.out.println("sql loader file "+dataFile+" created;");
                 System.out.println("about to load data file "+dataFile);
                 System.out.println("press enter to continue");
                 //System.in.read();
                 Thread.currentThread().sleep(1000);

             Runtime r = Runtime.getRuntime();

             Process p = r.exec("./"+sqlldrFileName+" "+dataFile);
             int t = p.waitFor();*/


		} 
		catch (Exception e) {

			e.printStackTrace();
		}


	}


	// HH 04/28/2016
	//Parse combined Key's content's
	public void parseS3Files (int start, int end) throws IOException
	{
		this.id_start = start;
		this.id_end = end;
		
		InputStream in = new ByteArrayInputStream(strb.toString().getBytes());

		reader = new BufferedReader(new InputStreamReader(in));
		reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(reader.readLine().replaceAll("><", ">\n<").getBytes())));

		// clear stringbuffer
		strb.setLength(0);

		//s3FileLoc = this.bucketName+"/"+this.key;  // Temp comment out till find a way to add s3 file location for each single key in combinedcontents

		//HH 04/28/2016 for combined contents
		s3FileLoc = this.bucketName;
		System.out.println("Key S3 Location for last Key: " + s3FileLoc);

		// check free memory space
		rt = Runtime.getRuntime();
		System.err.println(String.format("Memory CHeck Before Converting: Free: %d bytes, Total: %d bytes, Max: %d bytes",
		rt.freeMemory(), rt.totalMemory(), rt.maxMemory()));


		try {
			if(id_start >0 && id_end>0)
			{
			filename = "cafe_inventory_"+Integer.toString(id_start) +"_to_" + Integer.toString(id_end);
			BaseTableDriver c = new BaseTableDriver(loadNumber,database,action,s3FileLoc);
			con = c.getConnection(connectionURL, driver, username, password);
			//c.writeBaseTableFile(filename,con,reader,cafe);
			c.writeBaseTableFile(filename,con,cafe);
			String dataFile=filename+"."+loadNumber+".out";
			File f = new File(dataFile);
			if(!f.exists())
			{
				System.out.println("datafile: "+dataFile+" does not exists");
				System.exit(1);
			}

			}

			// check free memory space
			rt = Runtime.getRuntime();
			System.err.println(String.format("Memory CHeck After Converting is complete: Free: %d bytes, Total: %d bytes, Max: %d bytes",
			rt.freeMemory(), rt.totalMemory(), rt.maxMemory()));



		} 
		catch (Exception e) {

			e.printStackTrace();
		}
		
		finally
		{
			try
			{
				if(reader !=null)
				{
					reader.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	public static BufferedReader getBufferedReader()
	{
		return reader;
	}
	
}
