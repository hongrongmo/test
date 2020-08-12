package org.dataloading.sharedsearch;

/**
 * 
 * @author TELEBH
 * @Date: 06/17/2020
 * @Description: Search ENTRY for Pulling AUIDS and AFIDS from ES via Shared Search Service API to compare IDS with 
 * Run concurrency threading for each AUID read from file to submit to shared search
 * 
**/

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.ei.dataloading.cafe.FetchWeeklyAuAfIdsForES;

import java.lang.Runtime;
import java.util.Set;
import java.util.LinkedHashSet;

public class SharedSearchSearchEntry {

	
	static String fileName;
	static String searchField;
	static String sharedSearchurl;
	static String database;
	static String url;
	static String driver;
	static String username;
	static String password;
	static String tableToBeTruncated;
	static String esAuthorCountTempTable = "es_au_count";
	static String esAffiliationCountTable = "es_af_count";
	static String sqlldrFileName;
	Logger logger;
	static String midReturn = "none";		//allowed values none or mid
	static boolean isFacet = true;
	
	private Connection con;
	String outFileName = "";
	Thread thread;
	
	int counter = 0;
	Set<String> idPrefixSet;
	
	
	public static void main(String[] args)
	{
		if(args.length <12)
		{
			System.out.println("Not enough arguments!!! Re-try with searchField and db parameters");
			System.exit(1);
		}
		if(args.length >0)
		{
			fileName = args[0];
			System.out.println("Read Data from file: " + fileName);
		}
		
		if(args.length >1)
		{
			searchField = args[1];
			System.out.println("SearchField: " + searchField);
		}
		if(args.length >2)
		{
			sharedSearchurl = args[2];
			System.out.println("SharedSearch URL: " + sharedSearchurl);
		}
		if(args.length >3)
		{
			database= args[3];
			System.out.println("Database: " + database);
		}
		if(args.length >4)
		{
			url = args[4];
		}
		if(args.length >5)
		{
			driver = args[5];
		}
		if(args.length >6)
		{
			username = args[6];
		}
		if(args.length >7)
		{
			password = args[7];
		}
		if(args.length > 8)
		{
			tableToBeTruncated = args[8];
			System.out.println("Tables to be truncated: " + tableToBeTruncated);
		}
		if(args.length >9)
		{
			sqlldrFileName = args[9];
			System.out.println("sqlldr fileName: " +  sqlldrFileName);
		}
		if(args.length > 10)
		{
			midReturn = args[10];
			System.out.println("mid return? " + midReturn);
		}
		if(args.length > 11)
		{
			if(args[11].matches("[0-1]"))
			{
				if(Integer.parseInt(args[11]) == 1)
					isFacet = true;
				else
					isFacet = false;
			}
				
			else
			{
				System.out.println("Invalid boolean value for isFacet? re-tru with true or false");
				System.exit(1);
			}
		}
		SharedSearchSearchEntry entry = new SharedSearchSearchEntry();
		if(isFacet)
			entry.startFacetProcess();
		else
			entry.startProcess();    //for standalone processing ONLY
	}
	
	public SharedSearchSearchEntry() {
		
		// configure log
		String log4jFile = System.getProperty("user.dir") + File.separator + "src" + File.separator + "resources"
				+ File.separator + "log4j.properties";
		PropertyConfigurator.configure(log4jFile);

	}
	/* to support calling from other Weekly CPX IDS classes */
	public SharedSearchSearchEntry(String inFileName, String searchField, String sharedSearchurl, String database, Connection con, 
			String tableToBeTruncated, String sqlldrFileName, Logger logger, String midReturn)
	{
		this.fileName = inFileName;
		this.searchField = searchField;
		this.sharedSearchurl = sharedSearchurl;
		this.database = database;
		this.con = con;
		this.tableToBeTruncated = tableToBeTruncated;
		this.sqlldrFileName = sqlldrFileName;
		this.logger = logger;
		this.midReturn = midReturn;
	}
	/*if searching composit Facet*/
	
	public void startFacetProcess() {
		
		SearchFields fields = new SearchFields();
		searchField = fields.getSearchField(searchField) ;
		idPrefixSet = new LinkedHashSet<>();
		
		
		long startTime = System.currentTimeMillis();
		long finishTime = System.currentTimeMillis();
		String after = "0";
		logger = Logger.getLogger(SharedSearchSearchEntry.class);
		
		outFileName = startTime + "_" + searchField + "_es-auid-count.txt";
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName)))
		{
			SharedSearchSearch sharedSearch = new SharedSearchSearch(sharedSearchurl, database, logger);
			
			runProcess(after, "-1", sharedSearch, bw);
			
			
			 System.out.println("quering SharedsSearch for : " + searchField + " are now complete with total# of iterations: " + counter);
			 bw.flush();
			 bw.close(); 
			 
			 finishTime = System.currentTimeMillis();
			 System.out.println("Finish.... " + finishTime);
		
			 System.out.println("Total Time to fetch all : " + searchField + " " + Long.valueOf((finishTime - startTime)/1000) + " seconds");
		 
			 // creating database connection
			 init();
			 //cleanup data to temp table
			 cleanUpTempTables();
			 loadData();
		 
		}
		catch(IOException ex)
		{
			System.out.println("Exception reading from file!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception ex)
		{
			System.out.println("Exception to run sharedSearch!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	/* iterative call, can be updated for recusrive*/
	public void runProcess(String after, String prev, SharedSearchSearch sharedSearch, BufferedWriter bw)
	{
		/* base case*/
		/*
		 * if(after == null || after.isEmpty() || after.equalsIgnoreCase(prev)) return;
		 * /*
		 * if(after != null && !after.isEmpty())
				runProcess(after, prev, sharedSearch, bw);
		 * else return;
		 */
		
		calculateIDPrefixes();
		
		for(String prefix: idPrefixSet)
		{
			while(after != null && !(after.isBlank()))
			{
				prev = after;
				logger.info(++counter);
				String query = sharedSearch.buildESQueryFacet(after,searchField, prefix);
				logger.info(query);
				after = sharedSearch.runESQuery("", query, bw);
				logger.info("after: " + after);
				
			}
		}
		
		
		
	}
	
	/*calculate prefixes*/
	public Set<String> calculateIDPrefixes()
	{
		
		for(int i=0; i<10; i++)
			for(int j=0; j<10; j++)
			{
				String str = i +""+ j;
				idPrefixSet.add(str);
			}
		System.out.println("Id Prefix List Size: " + idPrefixSet.size());
		return idPrefixSet;
		
	}
	/* starting point, same as main*/
	public void startProcess() {
		
		SearchFields fields = new SearchFields();
		searchField = fields.getSearchField(searchField) ;
		logger = Logger.getLogger(SharedSearchSearchEntry.class);
		
		try
		{
			readDataFromFile(searchField);
			
			/* close DB connection when running as standalone */
			//entry.end();
		}
		catch(Exception ex)
		{
			System.out.println("File: "+ fileName + " not exist");
			ex.printStackTrace();
		}
	}
	private void init() {
		
		try
		{
			if(this.con == null)
			{
				this.con = getConnection(url, driver, username, password);
			}
			else
			{
				System.out.println("Connection already set, so skip initialization....");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	public void end()
	{
		System.out.println("close db connection");
		try
		{
			if(con != null)
				con.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void readDataFromFile(String searchField) {
		int i=0;
		List<Thread> threads = new ArrayList<>();
		SharedSearchSearch sharedSearch = new SharedSearchSearch(sharedSearchurl, database, logger);
		
		long startTime = System.currentTimeMillis();
		long finishTime = System.currentTimeMillis();
		
		outFileName = startTime + "_" + searchField + "_es-auid-count.txt";
		
		
		System.out.println("Start.... " + startTime);
		
		try(BufferedReader br = new BufferedReader(new FileReader(new File(fileName))); BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName)))
		{
			String line;
			while((line = br.readLine()) != null)
			{
				if(!(line.isEmpty()))
				{
					//this.thread.sleep(1000);
					
					ConcurrentSharedSearch concurrentSearch = new ConcurrentSharedSearch("thread" + i, line.trim(), searchField, sharedSearch,bw, logger, midReturn);
					thread = new Thread(concurrentSearch);
					threads.add(thread);
					thread.start();
					
				}
				i++;
			}
			
			// close writer after all threads are complete
			for(Thread thread: threads)
				thread.join();  
			System.out.println("Total # of threads started: " + threads.size());
			
			 if(thread != null && !(thread.isAlive())) 
			 { 
				 System.out.println("All Threads quering SharedsSearch for : " + searchField + " are now complete");
				 bw.flush();
				 bw.close(); 
				 
				 finishTime = System.currentTimeMillis();
				 System.out.println("Finish.... " + finishTime);
			 }
			 System.out.println("Total Time to fetch all : " + searchField + " " + Long.valueOf((finishTime - startTime)/1000) + " seconds");
			 
			 // creating database connection
			 init();
			 //cleanup data to temp table
			 cleanUpTempTables();
			 loadData();
			
		}
		catch(IOException ex)
		{
			System.out.println("Exception reading from file!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception ex)
		{
			System.out.println("Exception to run sharedSearch!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		
	}

	/* load data to temp tables using sqlldr */
	private void loadData() {
		
		try
		{
			Runtime runTime = Runtime.getRuntime();
			Process process = runTime.exec("./" + sqlldrFileName + " " + outFileName);
			int status = process.waitFor();			// wait till sqlldr is complete
			System.out.println("Sqlldr completion status: " + status);
			if(status == 0)
			{
				getTempTableCount();
			}
			
		}
		catch(Exception e)
		{
			System.out.println("Exception loading data!!!");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private void getTempTableCount() {
		
		Statement stmt = null;
		ResultSet rs;
		try
		{
			String tableName = tableToBeTruncated;			// just to give it meaningful name in sql stmt
			
			if(con != null)
			{
				stmt = con.createStatement();
				rs = stmt.executeQuery("select count(*) as count from " + tableName);
				if(rs.next())
					System.out.println("tempTable Count: "+ rs.getInt("count"));
			}
		}
		catch(Exception e)
		{
			System.out.println("Exceptin getting tempTableCount!!!");
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(stmt != null)
					stmt.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
	}

	private void cleanUpTempTables() {
		Statement stmt = null;
		try
		{
			if(con != null)
			{
				stmt = con.createStatement();
				
				if(tableToBeTruncated != null && !(tableToBeTruncated.isEmpty()))
				{
					System.out.println("About to truncate: " + tableToBeTruncated);
					stmt.execute("truncate table " + tableToBeTruncated);
					System.out.println(tableToBeTruncated + " truncated");										
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		finally
		{
			try
			{
				if(stmt != null)
					stmt.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("finally")
	private Connection getConnection(String url, String driver, String username, String password) {
		Connection con = null;
		try {
			System.out.println("connectionUrl= " + url);
			System.out.println("driver= " + driver);
			System.out.println("username= " + username);
			
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
		}
		catch(SQLException ex)
		{
			System.out.println("Error creating Oracle Connection!!!");
			System.out.println(ex.getMessage());
			System.out.println("Exit....");
			
			/* since everything after fetching data from shared search API depends on oracle connection, so if exception happened exit */
			System.exit(1);
		}
		catch (ClassNotFoundException e) {
			System.out.println("OracleDriver class not found!!!");
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return con;
		}
		
		
	}

	
}
