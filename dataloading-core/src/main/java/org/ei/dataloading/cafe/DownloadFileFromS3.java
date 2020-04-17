package org.ei.dataloading.cafe;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ei.dataloading.awss3.AmazonS3Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

/** 
 * 
 * @author TELEBH
 * @Date: 05/02/2016
 * Description: Download Cafe S3 files of Initial Refeed to Filesystem, for later zip each bulk and sending to converting program 
 * Update: 05/23/2016
 * Update for also downloading AU/AFF profiles in addition to Abstract
 */
public class DownloadFileFromS3 {

	
	private static Connection con = null;

	static String url="jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_correction1";
	static String password="ei3it";
	static String database ="cpx";
	//static String action;
	static int id_start;
	static int id_end;
	
	File S3dir;    // to hold downloaded bulk of s3 keys/files
	
	private GetANIFileFromCafeS3Bucket objectFromS3;
	private AmazonS3 s3Client;

	
	
	public static void main(String[] args)
	{
		if(args.length >5)
		{
			if(args[0]!=null)
			{
				url = args[0];
			}
			if(args[1]!=null)
			{
				driver = args[1];
			}
			if(args[2]!=null)
			{
				username = args[2];
				System.out.println("username= "+username);
			}
			if(args[3]!=null)
			{
				password = args[3];
				System.out.println("password= "+password);
			}   
			
			if(args[4] !=null)
			{
				id_start = Integer.parseInt(args[4]);
			}
			
			if(args[5] !=null)
			{
				id_end = Integer.parseInt(args[5]);
			}
			
		}
		

		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}

		
		DownloadFileFromS3 downloadFile = new DownloadFileFromS3();
		
		
		try {
			downloadFile.init();
			downloadFile.getFilesInfo();
		} 
		catch (Exception e) 
		{
			
			e.printStackTrace();
		}

	}
	// get db connection
	public void init() 
	{
		try
		{
			con = getConnection(url,driver,username,password);
			// connect to AmazonS3
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();

			//05/02/2016 create dir to download s3 files into xml for later zip and convert

			S3dir=new File("s3Files_" + id_start+"_" + id_end);
			if(!S3dir.exists())
			{
				S3dir.mkdir();
			}
			
			// for parsing ANI/Abstract CPX File
			objectFromS3 = new GetANIFileFromCafeS3Bucket(s3Client,database,url,driver,username,password,S3dir);

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
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




	}
	public void getFilesInfo() throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = con.createStatement();
			String sqlString = "select * from CAFE_INVENTORY where ID >=" +  id_start + " and ID<=" +  id_end;
			//String sqlString = "select key from cafe_pyr_2016_keys";
			
			rs = stmt.executeQuery(sqlString);
			downloadFiles(rs);
			
		
			
		}
		finally
		{

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
				if(con != null)
				{
					try
					{
						con.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
		}

	}

	public void downloadFiles(ResultSet rs) throws InterruptedException
	{
		String bucket = null;
		String key = null;
		String action = null;   //SQS Msg Action
	
		try 
		{
			while (rs.next())
			{
				if(rs.getString("BUCKET") !=null)
				{
					bucket = rs.getString("BUCKET");
				}
				if(rs.getString("KEY") !=null)
				{
					key = rs.getString("KEY");
				}
				if(rs.getString("action") !=null)
				{
					action = rs.getString("action");
				}

				
				//get the s3file content & download as key.xml 
				if(bucket !=null && key !=null)
				{
					System.out.println("Get file... " +  bucket+"/"+key + " for action: " +  action);
					objectFromS3.getFile(bucket, key);
				}
				
			}
			
			
		} 
		
		// for resultSet
		catch (SQLException e) 
		{
			System.out.println("Error Occurred reading from ResultSet: " + e.getMessage());
			e.printStackTrace();
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
	
	
	
	
	protected Connection getConnection(String connectionURL,
			String driver,
			String username,
			String password)
					throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,
				username,
				password);
		return con;
	}
}
