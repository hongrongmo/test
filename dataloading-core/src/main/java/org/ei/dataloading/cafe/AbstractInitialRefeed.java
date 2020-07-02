package org.ei.dataloading.cafe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ei.dataloading.bd.loadtime.BaseTableDriver;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

/**
 * 
 * @author TELEBH
 * @Date 04/21/2016
 * @Description: Cafe initial Refeed process for all historical CPX ABstract (ANI) data
 * @Modification Date: 05/02/2016 around 12:22 PM
 * @New Description: cafe initial Refeed process for all historical CPX Abstract (ANI) data, buy getting zip file of bulk of downloaded S3 files from FileSystem
 */

public class AbstractInitialRefeed {

	private static Connection con = null;

	static String url="jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_correction1";
	static String password="";
	static String database ="cpx";
	static String action = "normal";
	static String s3FileLoc = "sc-ani-xml-prod";
	//static String action;
	static int loadNumber=0;
	static String infile;     //combined zip file for bulk of Cafe files (xml)
	static String sqlldrFileName="cafeANIFileLoader.sh";
	static int id_start;
	static int id_end;

	private GetANIFileFromCafeS3Bucket objectFromS3;
	private AmazonS3 s3Client;



	public static void main(String[] args) throws Exception
	{

		 long startTime = System.currentTimeMillis();
		 
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
			if(args[0] !=null)
			{
				infile = args[0];
				System.out.println("converting datafile: " + infile);
			}
			if(args[1]!=null && args[1].length()>0)
			{
				Pattern pattern = Pattern.compile("^\\d*$");
				Matcher matcher = pattern.matcher(args[1]);
				if (matcher.find())
				{
					loadNumber = Integer.parseInt(args[1]);
				}
				else
				{
					System.out.println("did not find loadNumber or loadNumber has wrong format");
					System.exit(1);
				}
			}
			if(args[2]!=null)
			{
				url = args[2];
			}
			if(args[3]!=null)
			{
				driver = args[3];
			}
			if(args[4]!=null)
			{
				username = args[4];
				System.out.println("username= "+username);
			}
			if(args[5]!=null)
			{
				password = args[5];
				System.out.println("password= "+password);
			}
			if(args[6] !=null)
			{
				database = args[6];
			}
		}


		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}


		try
		{

			
			DateFormat dateFormat = new SimpleDateFormat("E, MM/dd/yyyy-hh:mm:ss a");		 
			
			System.out.println("Current date and time Before Process Start: "+ dateFormat.format(new Date()));
			
			BaseTableDriver c = new BaseTableDriver(loadNumber,database,action,s3FileLoc);
			con = c.getConnection(url,driver,username,password);
			c.writeBaseTableFile(infile,con);
			String dataFile=infile+"."+loadNumber+".out";
			File f = new File(dataFile);
			if(!f.exists())
			{
				System.out.println("datafile: "+dataFile+" does not exists");
				System.exit(1);
			}
			
			System.out.println("Current date and time After Process Finish: "+ dateFormat.format(new Date()));
			
			 Runtime r = Runtime.getRuntime();
             Process p = r.exec("./"+sqlldrFileName+" "+dataFile);
             int t = p.waitFor();
             

             System.out.println("Current date and time After sqlldr Finish: "+ dateFormat.format(new Date()));
             
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
			System.out.println("total process time "+(System.currentTimeMillis()-startTime)/1000.0+" seconds");
		}

	}


}
