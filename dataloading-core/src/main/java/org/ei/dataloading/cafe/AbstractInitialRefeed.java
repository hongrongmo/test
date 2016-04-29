package org.ei.dataloading.cafe;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;







import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

/**
 * 
 * @author TELEBH
 * @Date 04/21/2016
 * @Description: Cafe initial Refeed process for all historical CPX ABstract (ANI) data
 */

public class AbstractInitialRefeed {

	private static Connection con = null;

	static String url="jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_correction1";
	static String password="ei3it";
	static String database ="cpx";
	//static String action;
	static int loadNumber=0;
	static String sqlldrFileName="cafeANIFileLoader.sh";
	static int id_start;
	static int id_end;
	
	private GetANIFileFromCafeS3Bucket objectFromS3;
	private AmazonS3 s3Client;

	
	
	public static void main(String[] args)
	{

		if(args.length >7)
		{
			if(args[7]!=null)
			{
				sqlldrFileName = args[7];
				System.out.println("using sqlloaderfile "+sqlldrFileName);
			}
		}
		if(args.length >6)
		{
			if(args[0]!=null && args[0].length()>0)
			{
				Pattern pattern = Pattern.compile("^\\d*$");
				Matcher matcher = pattern.matcher(args[0]);
				if (matcher.find())
				{
					loadNumber = Integer.parseInt(args[0]);
				}
				else
				{
					System.out.println("did not find loadNumber or loadNumber has wrong format");
					System.exit(1);
				}
			}
			if(args[1]!=null)
			{
				url = args[1];
			}
			if(args[2]!=null)
			{
				driver = args[2];
			}
			if(args[3]!=null)
			{
				username = args[3];
				System.out.println("username= "+username);
			}
			if(args[4]!=null)
			{
				password = args[4];
				System.out.println("password= "+password);
			}   
			
			if(args[5] !=null)
			{
				id_start = Integer.parseInt(args[5]);
			}
			
			if(args[6] !=null)
			{
				id_end = Integer.parseInt(args[6]);
			}
			
			System.out.println("Start Converting from ID:  " +  id_start + " to ID: " + id_end);
		}
		

		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}

		
		AbstractInitialRefeed cpxFeed = new AbstractInitialRefeed();
		
		
		try {
			cpxFeed.init();
			cpxFeed.processAbstract();
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

			// for parsing ANI/Abstract CPX File
			objectFromS3 = new GetANIFileFromCafeS3Bucket(s3Client,loadNumber,database,url,driver,username,password, sqlldrFileName);
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
	public void processAbstract() throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = con.createStatement();
			String sqlString = "select * from CAFE_INVENTORY where ID >=" +  id_start + " and ID<=" +  id_end;
			
			rs = stmt.executeQuery(sqlString);
			convertRecs(rs);
			
		
			
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

	public void convertRecs(ResultSet rs)
	{
		String bucket = null;
		String key = null;
		String action = null;   //SQS Msg Action
		
		
		// check free memory space
		Runtime rt = Runtime.getRuntime();
		System.err.println(String.format("Memory CHeck before Chain source contents: Free: %d bytes, Total: %d bytes, Max: %d bytes",
		rt.freeMemory(), rt.totalMemory(), rt.maxMemory()));
		
		
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

				//get the s3file content & convert using our cpx converting prog
				if(bucket !=null && key !=null)
				{
					System.out.println("Get file... " +  bucket+"/"+key + " for action: " +  action);
					//objectFromS3.getFile(bucket, key,action);  // for each single Cafe Key/File
					objectFromS3.getFileContent(bucket, key,action);    // for multiple cafe Key/File (combined together)
				}
				
			}
			
			//HH 04/28/2016
			//Combine Key's Contents and convert the bulk
			//objectFromS3.chainInputstreams(id_start,id_end);
			
			// check free memory space
			rt = Runtime.getRuntime();
			System.err.println(String.format("Memory CHeck After Chain source contents: Free: %d bytes, Total: %d bytes, Max: %d bytes",
			rt.freeMemory(), rt.totalMemory(), rt.maxMemory()));

			
			objectFromS3.parseS3Files(id_start,id_end);
		} 
		
		// for resultSet
		catch (SQLException e) 
		{
			System.out.println("Error Occurred reading from ResultSet: " + e.getMessage());
			e.printStackTrace();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
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
