package org.ei.dataloading.cafe;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dataloading.sharedsearch.SharedSearchSearchEntry;

/**
 * 
 * @author TELEBH
 * @Date: 07/17/2020
 * @Description: In order to dynamically call SHaredSearchEntry for weekly fetched auids/afids has to call from here instead of FetchWeeklyAuAfIdsForES
 * in other words this is the starting point for running 
 * 		a. FetchWeeklyAuAfIdsForES
 * 		b. SharedSearchSearchEntry
 */
public class StartWeeklyAuAfIdsForES extends Thread{
	
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "db_xml";
	static String password = "";
	/* ONLY to know current weeknumber */
	static int weekNumber;
	static String sharedSearchurl = "https://shared-search-service-api.cert.scopussearch.net/document/v1/query/result";
	static String tableToBeTruncated;
	static String sqlldrFileName;
	static String searchField;
	
	Connection con;
	
	static String[] fileNames = null;

	public static void main(String[] args)
	{
		if(args.length >8)
		{
			if(args[0] != null)
				url = args[0];
			if(args[1] != null)
				driver = args[1];
			if(args[2] != null)
			{
				username = args[2];
				System.out.println("Username: " + username);
			}
			if(args[3] != null)
				password = args[3];
				
			if(args[4] != null)
			{
				weekNumber = Integer.parseInt(args[4]);
				System.out.println("WeekNumber: " + weekNumber);
			}
			if(args[5] != null)
			{
				sharedSearchurl = args[5];
				System.out.println("SharedSearch URL: " + sharedSearchurl);
			}
			if(args.length > 6)
			{
				tableToBeTruncated = args[6];
				System.out.println("Tables to be truncated: " + tableToBeTruncated);
			}
			if(args.length >7)
			{
				sqlldrFileName = args[7];
				System.out.println("sqlldr fileName: " +  sqlldrFileName);
			}
			if(args.length >8)
			{
				searchField = args[8];
				System.out.println("searchField: " + searchField);
			}
				
		}
		else
		{
			System.out.println("not enough paraneters!!!");
			System.exit(1);
		}
		
		StartWeeklyAuAfIdsForES obj1 = new StartWeeklyAuAfIdsForES();
		obj1.startWeeklyProcess();
		
		
	}
	
	
	private void startWeeklyProcess() {
		
		try (Connection con = getConnection())
		{
			FetchWeeklyAuAfIdsForES obj = new FetchWeeklyAuAfIdsForES(con, weekNumber);
			
			fileNames = obj.init();
			obj.fetchWeeklyIds();
			/* Call SharedSearch to check doc_count for identified auids/afids */
			 SharedSearchSearchEntry entry = new SharedSearchSearchEntry(fileNames[0], searchField, sharedSearchurl, "cpx", con, tableToBeTruncated, sqlldrFileName);
		 		entry.startProcess();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}


	/* create db connection */
	private Connection getConnection()
					throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(url,
				username,
				password);
		return con;
	}
	
	
}
