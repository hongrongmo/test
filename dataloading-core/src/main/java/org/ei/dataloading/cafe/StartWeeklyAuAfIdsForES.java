package org.ei.dataloading.cafe;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;
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
	//static String sharedSearchurl = "https://shared-search-service-api.cert.scopussearch.net/document/v1/query/result";		//Kong url
	static String sharedSearchurl = "https://shared-search-service-api.cert.scopussearch.net/sharedsearch/document/facets";
	static String[] searchFields;
	static String[] tempTables;
	static String[] sqlldrFiles;
	
	Connection con;
	Logger logger = Logger.getLogger(StartWeeklyAuAfIdsForES.class);
	static String midReturn = "none";
	
	static String[] fileNames = null;

	public static void main(String[] args)
	{
		if(args.length >9)
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
				if(args[6] .contains(","))
				{
					tempTables = args[6].split(",");
					System.out.println("Tables to be truncated: " + args[6]);
				}
				else
				{
					System.out.println("Incomplete tempTables List, exit");
					System.exit(1);
				}
				
			}
			if(args.length >7)
			{
				if(args[7].contains(","))
				{
					sqlldrFiles = args[7].split(",");
					System.out.println("sqlldr fileName: " +  args[7]);
				}
				else
				{
					System.out.println("Incomplete sqlldrFiles List, exit");
					System.exit(1);
				}
					
			}
			
			if(args.length >8)
			{
				if(args[8].contains(","))
				{
					searchFields = args[8].split(",");
					System.out.println("searchField: " + args[8]);
				}
				else
				{
					System.out.println("Incomplete serchField List, exit");
					System.exit(1);
				}
				
			}
			if(args.length >9)
			{
				midReturn = args[9];
				System.out.println("midReturn? " + midReturn);
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
		
		/* try with resource, con is a closable object */
		try (Connection con = getConnection())
		{
			FetchWeeklyAuAfIdsForES obj = new FetchWeeklyAuAfIdsForES(con, weekNumber);
			
			fileNames = obj.init();
			obj.fetchWeeklyIds();
			/* Call SharedSearch to check doc_count for identified auids/afids */
			
			
			SharedSearchSearchEntry auentry = new SharedSearchSearchEntry(fileNames[0], searchFields[0], sharedSearchurl, "cpx", con, tempTables[0], sqlldrFiles[0], logger, midReturn);
			auentry.startProcess();
			
			System.out.println("Sleep 20 sec before calling sharedSearch for AffId");
			Thread.sleep(20000);
		
			SharedSearchSearchEntry afentry = new SharedSearchSearchEntry(fileNames[1], searchFields[1], sharedSearchurl, "cpx", con, tempTables[1], sqlldrFiles[1], logger, midReturn);
			afentry.startProcess();
			
				 
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
