package org.ei.dataloading.cafe;

/**
 * @author TELEBH
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.ei.dataloading.awss3.AmazonS3Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;

public class AdhocDownloadFilesFromANIBucket {

	static String bucketName = "datafabrication-reports";
	static String key = "2015/reports.css";
	static String uploadFileName = "reports.css";
	
	static AmazonS3 s3Client = null;
	static TransferManager tm = null;
	
	static S3Object object;
	static Download downloadFile = null;
	static InputStream objectData = null;
	
	BufferedReader reader = null;
	static File file = null;
	
	
	static int i=0;
	
	static String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";
    static String driver="oracle.jdbc.driver.OracleDriver";
    static String username="db_xml";
    static String password="ny5av";
	int count = 0;
    
    private static Connection con = null;
    ResultSet rs = null;
    java.sql.Statement stmt = null;
	
   
    String itemInfoStart = "";
	String itemInfo = "";
	String cpxIDInfo = "";
	String accessNumber = "";
	
	
	public AdhocDownloadFilesFromANIBucket()
	{
		
	}
	
	public AdhocDownloadFilesFromANIBucket(String s3BucketName, String key)
	{
		this.bucketName = s3BucketName;
		this.key = key;
	}
	
public static void main(String[] args) throws Exception {
		
		String bucketName = "sc-ani-xml-prod";
		String key = "56015952900";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		
		Date s3FileLmd = null;
		
		if(args !=null && args.length >0)
		{
			if(args[0] !=null)
			{
				connectionURL = args[0];
			}
		}
	
		try
		{
			Date lastModified = sdf.parse("2016-02");
			
			Calendar cal = new GregorianCalendar();
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.YEAR, 2016);
			Date referenceDate = sdf.parse(cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH));
			
			
			AdhocDownloadFilesFromANIBucket objectFromS3 = new AdhocDownloadFilesFromANIBucket();
			
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			tm = new TransferManager(s3Client);
			
			//get DB connection to check of accessnumber in BD, if so download file
			
			con = objectFromS3.getConnection(connectionURL,driver,username,password);
			
			
			//List objects
			
			ListObjectsRequest listRequest = new ListObjectsRequest().withBucketName(bucketName);
			ObjectListing objectListing;
			do
			{
				objectListing = s3Client.listObjects(listRequest);
				for(S3ObjectSummary summary: objectListing.getObjectSummaries())
				{
					s3FileLmd = summary.getLastModified();
					key = summary.getKey();
					object = s3Client.getObject(new GetObjectRequest (bucketName, key));
					
					objectData = object.getObjectContent();
					//Process the objectData stream;
					if(objectFromS3.parseObjectData(objectData, key))
					{
							//Download the file from ANI bucket 
							objectFromS3.getFileFromS3(bucketName, key, object);
						
					}
					objectData.close();					
				}
				
				listRequest.setMarker(objectListing.getNextMarker());
			}
			while (objectListing.isTruncated());
			
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
	            if (con != null)
	            {
	                try
	                {
	                    con.close();
	                }
	                catch (Exception e)
	                {
	                    e.printStackTrace();
	                }
	            }
	            
	            if(tm !=null)
	            {
	            	//after download is complete, call shutdownNow to release resources
					tm.shutdownNow();	
	            }

	        }

	    
	}

	public static void getFileFromS3(String s3BucketName, String fileKey, S3Object object) throws AmazonClientException,AmazonServiceException, InterruptedException
	{	
		try
		{
			i++;
			System.out.println("downloading CAFE FILE:"+fileKey+" from S3 bucket: "+s3BucketName);
			System.out.println("S3FilePathe: " + "s3://"+s3BucketName+"/"+fileKey);
			
			
			System.out.println("etag "+object.getObjectMetadata());
			
			// download the file
			file = new File(fileKey);
			//tm = new TransferManager(s3Client);
			downloadFile = tm.download(s3BucketName, fileKey, file);
			
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
			
			System.out.println("downloaded CAFE file #:  " + i);
			
			/*if(i >200)
			{
				System.out.println("Total: " + (i-1) + " have been downloaded file");
				System.exit(1000);
				
			}
			*/

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

	public boolean parseObjectData (InputStream s3FileContent, String key) throws IOException
	{
		 boolean bdcollection = false;
		reader = new BufferedReader(new InputStreamReader(s3FileContent));
		
		
		while (true)
		{
			String line = reader.readLine();
			if(line == null)
				break;
			if(line.contains("<item-info>"))
			{
				itemInfoStart = line.substring(line.indexOf("<item-info>") , line.length());
				if(itemInfoStart.length() >0 && itemInfoStart.contains("</item-info>"))
				{
					itemInfo = itemInfoStart.substring(0,itemInfoStart.indexOf("</item-info>") +12);
				}
					
				if(itemInfo.length() >0 && itemInfo.contains("<itemid idtype=\"CPX\">"))
				{
					cpxIDInfo = itemInfo.substring(itemInfo.indexOf("<itemid idtype=\"CPX\">"),itemInfo.length());
					accessNumber = cpxIDInfo.substring(cpxIDInfo.indexOf(">")+1,cpxIDInfo.indexOf("</itemid>"));
					
					bdcollection = checkDB(accessNumber);
					
					//bdcollection = true;
					System.out.println("CPX file: " + key);
				}
			}
				
			}
		return bdcollection;
		}
	
	private boolean checkDB(String an)
	{
		boolean anMatch = false;

		try
		{
			stmt = con.createStatement();
			rs = stmt.executeQuery("select count(accessnumber) count from bd_master where accessnumber='"+an+"' and database='cpx' and loadnumber >=201501 and loadnumber<=20160621");
			
			while(rs.next())
			{
				count = rs.getInt("count");
				if(count >0)
					anMatch = true;
				
				System.out.println("Matched AN record:  " + anMatch);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		finally
		{
			if(rs !=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if(stmt !=null)
			{
				try
				{
					stmt.close();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		
		return anMatch;
		
	}
	protected Connection getConnection(String connectionURL,
			String driver,
			String username,
			String password)
					throws Exception
	{
		System.out.println("connectionURL= "+connectionURL);
		System.out.println("driver= "+driver);
		System.out.println("username= "+username);
		System.out.println("password= "+password);

		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,
				username,
				password);
		return con;
	}
	
}
