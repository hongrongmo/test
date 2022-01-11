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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.lang.Runtime;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class SharedSearchSearchEntry {

	private enum DBTables
	{
		BD_MASTER,
		INS_MASTER,
		UPT_MASTER,
		CBN_MASTER,
		EPT_MASTER,
		KNOVEL_MASTER,
		NTIS_MASTER
	}
	
	static String fileName;
	static String searchField;
	static String searchValue;
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
	static String facetField;
	Logger logger;
	static String midReturn = null;		//allowed values none or mid
	static String esEnvs;

	
	private Connection con;
	String outFileName = "";
	Thread thread;
	
	int counter = 0;
	Set<String> idPrefixSet;
	
	List<String> sharedSearchurls = new LinkedList<>();
	
	public static void main(String[] args)
	{
		System.out.println("args length: " + args.length);
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
		if(args.length > 2)
		{
			searchValue = args[2];
			System.out.println("SearchValue: " + searchValue);
		}
		if(args.length >3)
		{
			sharedSearchurl = args[3];
			System.out.println("SharedSearch URL: " + sharedSearchurl);
		}
		if(args.length >4)
		{
			database= args[4];
			System.out.println("Database: " + database);
		}
		if(args.length >5)
		{
			url = args[5];
		}
		if(args.length >6)
		{
			driver = args[6];
		}
		if(args.length >7)
		{
			username = args[7];
		}
		if(args.length >8)
		{
			password = args[8];
		}
		if(args.length > 9)
		{
			tableToBeTruncated = args[9];
			System.out.println("Tables to be truncated: " + tableToBeTruncated);
		}
		if(args.length >10)
		{
			sqlldrFileName = args[10];
			System.out.println("sqlldr fileName: " +  sqlldrFileName);
		}
		if(args.length > 11)
		{
			facetField = args[11];	
			System.out.println("FacetField: " + facetField);
		}
		
		if(args.length > 12)
		{
			midReturn = args[12];
			System.out.println("mid return? " + midReturn);
		}
		if(args.length > 13)
		{
			esEnvs = args[13];
		}
		
		SharedSearchSearchEntry entry = new SharedSearchSearchEntry();
		if(facetField != null)
			if(facetField.equalsIgnoreCase("auid") || facetField.equalsIgnoreCase("afid"))
				entry.startAuAfFacetProcess();
			else if(esEnvs == null || esEnvs.isEmpty())
					entry.startFacetProcess();
			else if(facetField.equalsIgnoreCase("db") && esEnvs != null && !esEnvs.isEmpty())
				entry.startQAProcess();
		else
			entry.startProcess();    //for standalone processing ONLY, when needed to read AUIDS list from file and for each ID check ES
		
		/* QA query for batchinfo and processInfo*/
		//entry.startQAProcess();
	}
	
	public SharedSearchSearchEntry() {
		
		// configure log
		
		// For localhost
		
		/*String log4jFile = System.getProperty("user.dir") + File.separator + "src" + File.separator + "resources"
				+ File.separator + "log4j.properties";
		
		*/
		//For EC2/Prod
		
		String log4jFile = "/data/loading/shared" + File.separator + "resources" + File.separator + "log4j.properties";
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
	
	/*to support lookup check*/
	public SharedSearchSearchEntry(String sharedSearchURL ) {
		this();
		sharedSearchurl = sharedSearchURL;
		logger = Logger.getLogger(SharedSearchSearchEntry.class);
	}

	
	/*if searching composit Facet*/
	
	public void startFacetProcess() {

		long startTime = System.currentTimeMillis();
		long finishTime = System.currentTimeMillis();
		String after = "0";
		logger = Logger.getLogger(SharedSearchSearchEntry.class);
			
		//0. Since final contents will be sent as SNS message anyway, so no point to load data to oracle
		 
		/*
		 * try { // creating database connection init(); //cleanup data to temp table
		 * cleanUpTempTables(); // verify the table is physically truncated
		 * getTempTableCount(); } catch(Exception ex) { logger.
		 * error("Oracle connection or Cleaning temp tables Exception occurred, exit!!"
		 * ); logger.error(ex.getMessage()); //System.exit(1); //UnComment in PROD }
		 */
		  
		getFacetField();
		for(String prefix: idPrefixSet)
		{
			outFileName = startTime + "_" + searchField + "_" + prefix + "_count.txt";
			if(facetField.equalsIgnoreCase("database"))
			{
				outFileName = startTime + "_" + searchField + "_" + searchValue + "_count.txt";
			}
				
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName)))
			{
				System.out.println("Start polling IDS with prefix: " + prefix);
				SharedSearchSearch sharedSearch = new SharedSearchSearch(sharedSearchurl, database, logger);
				
				String queryString = buildQueryString(prefix);
				
				runProcess(after, sharedSearch, bw, queryString, prefix);
				
				
				 System.out.println("quering SharedsSearch for : " + searchField + " are now complete with total# of iterations: " + counter);
				 bw.flush();
				 bw.close(); 
				 
				 finishTime = System.currentTimeMillis();
				 System.out.println("Finish.... " + finishTime);
			
				 System.out.println("Total Time to fetch all : " + searchField + " " + Long.valueOf((finishTime - startTime)/1000) + " seconds");
			 
				 /*** ONLY TEMP COMMENTED for local testing, NEED TO UNCOMMENT IN PROD **/
				 
				 // load data to table(s) 
				 // loadData(0);  ONLY when need to load data to oracle, when section 0 above uncommented 
				 
				 
				 /*Find physical hit count by running search for each individual batchInfo, after discussion with team we can't count on count info in processInfo as it is static count and would change for later updates/deletes
				  * will get it anyway and load to table for our records, this will be only special case if searchField is updateTime
				  */
				if (facetField.equalsIgnoreCase("batchinfo")) {

					startTime = System.currentTimeMillis();
					sharedSearchurl = sharedSearchurl.replace("facets", "result");
					System.out.println("Now start finding actual processinfo for each individual batchInfo:");
					fileName = outFileName;
					readDataFromFileSequential(facetField);
					finishTime = System.currentTimeMillis();

					System.out.println("Total Time to fetch all : " + searchField + " "
							+ Long.valueOf((finishTime - startTime) / 1000) + " seconds");
					//loadData(1);				ONLY when need to load data to oracle, when section 0 above uncommented 

				} 
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
			/* reset counter*/
			counter = 0;
		}
		// send SNS message with final contents
		new PublishSNS(searchField, searchValue, facetField, outFileName)
		.dispatchMessage();
	}
	
	public void startAuAfFacetProcess()
	{
		
		long startTime = System.currentTimeMillis();
		long finishTime = System.currentTimeMillis();
		logger = Logger.getLogger(SharedSearchSearchEntry.class);
			
		 
		try
		{
			 // creating database connection
			  init(); 
			  //cleanup data to temp table 
			  cleanUpTempTables();
			  // verify the table is physically truncated
			  getTempTableCount();
		}
		catch(Exception ex)
		{
			logger.error("Oracle connection or Cleaning temp tables Exception occurred, exit!!");
			logger.error(ex.getMessage());
			//System.exit(1);			//UnComment in PROD
		}
		
		List<Thread> threads = new ArrayList<>();
		List<String> outFilesNames = new ArrayList<>();
		
		int i = 0;
		getFacetField();
		SharedSearchSearch sharedSearch = new SharedSearchSearch(sharedSearchurl, database, logger);
		for(String prefix: idPrefixSet)
		{
			outFileName = startTime + "_" + searchField + "_" + prefix + "_count.txt";
			try{
				System.out.println("Start polling IDS with prefix: " + prefix);

				String queryString = buildQueryString(prefix);
				ConcurrentSharedSearch concurrentSearch = new ConcurrentSharedSearch("thread" + i, prefix,
						searchField, sharedSearch, outFileName, logger, null, queryString);
				thread = new Thread(concurrentSearch);
				threads.add(thread);
				outFilesNames.add(outFileName);
				thread.start();

				i++;

			} catch (Exception ex) {
				System.out.println("Exception to run sharedSearch!!!!");
				System.out.println(ex.getMessage());
				ex.printStackTrace();
			}
		}

		for (Thread thread : threads)
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		System.out.println("Total # of threads started: " + threads.size());

		/* load out files to DB in sequence after all threads started and completed */

		if (thread != null && !(thread.isAlive())) {
			for (String outFile : outFilesNames) {
				outFileName = outFile;

				/*** ONLY TEMP COMMENTED for local testing, NEED TO UNCOMMENT IN PROD **/
				// load data to table(s)
				loadData(0);

			}

			finishTime = System.currentTimeMillis();
			System.out.println("Finish.... " + finishTime);
			System.out.println("Total Time to fetch all : " + searchField + " "
					+ Long.valueOf((finishTime - startTime) / 1000) + " seconds");

		}
		
	}
	
	/* map searchField to Sharedsearch searchField and same for facetField*/
	
	public void getFacetField() 
	{
		SearchFields fields = new SearchFields();

		searchField = fields.getSearchField(searchField.toLowerCase());
		facetField = fields.getFacetField(facetField.toLowerCase());
		
		idPrefixSet = new LinkedHashSet<>();
		
		if(facetField.equalsIgnoreCase("authorId") || facetField.equalsIgnoreCase("affiliationId"))
			calculateIDPrefixes();
			
		else
			idPrefixSet.add("");
		
		

	}
	public String buildQueryString(String prefix)
	{
		String queryString = null;
		
		/* Pulling auid and afid*/
		if(facetField != null && (facetField.equalsIgnoreCase("authorId") || facetField.equalsIgnoreCase("affiliationId")))
			queryString = "database:" + database + " AND " + searchField + ":" + prefix + "*";
		/*Pulling batchInfo for time-range using updateTime*/
		else if (searchField.equalsIgnoreCase("updatetime") && searchValue.contains("-") && searchValue.toLowerCase().contains("to") &&  isValidDate(searchValue))
			queryString = searchField + ":" + searchValue;
		/* get facet count based on any other field search, i.e. loadNumber: 202037, though in such case need to exclude database */
		else
			queryString = searchField + ":" + searchValue;
		return queryString;
	}
	
	/* support Facet search*/
	public void runProcess(String after, SharedSearchSearch sharedSearch, BufferedWriter bw, String queryString, String prefix)
	{
		
			while(after != null && !(after.isBlank()))
			{
				logger.info(++counter);
				
				String query = sharedSearch.buildESQueryFacet(after,facetField, queryString);
				logger.info(query);
				after = sharedSearch.runESQuery("", query, bw, prefix);
				logger.info("after: " + after);
				
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
	
	/* To support BD Correction Lookup hitcount check so can filter the ones to be deleted */
	public List<String> runESLookupCheck(Map<String, String>LookupMap, String lookupField, String database)
	{
		String lookupESQuery = null;

		  List<String> outputList = new ArrayList<>();
		try
		{
			SharedSearchSearch sharedSearch = new SharedSearchSearch(sharedSearchurl, logger);
			SearchFields fields = new SearchFields();

			String esSearchField = fields.getSearchField(lookupField.toLowerCase());

			for (String lookupItem : LookupMap.keySet()) 
			{
				if(!(lookupItem.trim().isBlank())) {
					lookupESQuery = sharedSearch.buildLookupESQuery(esSearchField + ":" + "\"" + lookupItem + "\" database:" + database); 
					//System.out.println(lookupESQuery);			// only for local debugging
					String esHitCount = sharedSearch.runESQuery(lookupItem, lookupESQuery, null, "");
					String indexCount = String.valueOf(LookupMap.get(lookupItem));
					if(esHitCount != null && !esHitCount.isEmpty())
					{
						if (indexCount != null && indexCount != ""
								&& Integer.parseInt(indexCount) >= Integer.parseInt(esHitCount)) {
							outputList.add(lookupItem);
						}
						
						//Commented out 10/30/2020 to keep log file readable, only uncomment for debugging
						/*
						 * else { System.out.println(lookupItem +
						 * " db count < esHitCount, so no action " + indexCount + " < " + esHitCount); }
						 */
					}
					else
					{
						System.out.println("No esHitCount!");
					}
				}
				
				
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception to run sharedSearch for ES Lookup check of hitcount to identify lookups to be deleted!!!!");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return outputList;
	}
	
	
	/* Check data format range for QA */
	//parse updateTime range for limiting batchinfo to this specified time Range
		static boolean isValidDate(String input) 
		{
			// input validation on range value
			boolean isValid = false;
			if(isValidRange(input))
			{
				if(input.toLowerCase().contains("to"))
				{
					String [] dateRanges = input.toLowerCase().split(" ");
					for(String date: dateRanges)
					{
						if(date.contains("-"))
						{
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							try {
								format.parse(input);
								isValid = true;
							}
							catch(ParseException e){
								isValid = false;
							}
						}
						else if(date.contains("*"))
							isValid = true;
						else
							isValid = false;
					}
				}
			}
			else
			{
				System.out.println("Invalid range: " + input + " Re-run with invalid range i.e. [2020-08-27 TO *}, Exit");
				System.exit(1);
			}
			
			
			return isValid;
			
		}

		public static boolean isValidRange(String range)
		{
			boolean isRange = false;
			
			if(range != null && !range.isBlank())
			{
				if(range.startsWith("[") || range.startsWith("{"))
					if(range.endsWith("]") || range.endsWith("}"))
						isRange= true;
					else if(range.startsWith(">") || range.startsWith(">=") || range.startsWith("<") || range.startsWith("<="))
						isRange = true;
			}
			return isRange;
		}
		
	/* QA Process where can check more than one ES ENV all together*/
	public void startQAProcess()
	{
		long startTime = System.currentTimeMillis();
		long finishTime = System.currentTimeMillis();
		String after = "0";
		logger = Logger.getLogger(SharedSearchSearchEntry.class);
		
		getFacetField();	
		
		if(facetField.equalsIgnoreCase("database"))
		{
			outFileName = startTime + "_" + searchField + "_" + searchValue + "_count.txt";
			
		}
		String[] allEsEnvs = esEnvs.split(",");
		sharedSearchurls.add(sharedSearchurl);
		for(String esEnv: allEsEnvs)
		{
			sharedSearchurls.add(sharedSearchurl.replace("prod", esEnv.trim()));
		}
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName)))
		{
			for (String sharedSearchurl : sharedSearchurls) {
				System.out.println("Start Weekly DB QA for loadNumber: " + searchField);
				
				bw.write("\nES " + sharedSearchurl.substring(sharedSearchurl.indexOf(".") + 1, sharedSearchurl.indexOf(".") +5) + " ------- : \n");
				
				SharedSearchSearch sharedSearch = new SharedSearchSearch(sharedSearchurl, database, logger);

				String queryString = buildQueryString("");

				runProcess(after, sharedSearch, bw, queryString, "");

				System.out.println("quering SharedsSearch for : " + searchField
						+ " are now complete with total# of iterations: " + counter);
				

				finishTime = System.currentTimeMillis();
				System.out.println("Finish.... " + finishTime);

				System.out.println("Total Time to fetch all : " + searchField + " "
						+ Long.valueOf((finishTime - startTime) / 1000) + " seconds");
				
				bw.write("\nEND");
				
			}
			
			bw.flush();
			bw.close();
			// send SNS message with final contents
			new PublishSNS(searchField, searchValue, facetField, outFileName)
			.dispatchMessage();
			
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
		System.out.println("shredsearchurl: " + sharedSearchurl);
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
					// ONLY comment temp for troubleshooting, make sure to uncomment in prod
					
					String[] lines = line.split("\t");
					
					
					 ConcurrentSharedSearch concurrentSearch =  new ConcurrentSharedSearch("thread" + i, lines[0].trim(), searchField,sharedSearch,bw, logger, midReturn, ""); 
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
			 loadData(0);
			
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
	
	private void readDataFromFileSequential(String searchField) {
		System.out.println("shredsearchurl: " + sharedSearchurl);
		SharedSearchSearch sharedSearch = new SharedSearchSearch(sharedSearchurl, database, logger);

		long startTime = System.currentTimeMillis();
		long finishTime = System.currentTimeMillis();

		outFileName = startTime + "_" + searchField + "_es_count.txt";

		System.out.println("Start.... " + startTime);
		

		try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
				BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName))) {
			String line;
			SequentialSharedSearch seqSearch = new SequentialSharedSearch(searchField,sharedSearch, bw, logger, midReturn, "");
			
			while ((line = br.readLine()) != null) {
				if (!(line.isEmpty())) {
					// ONLY comment temp for troubleshooting, make sure to uncomment in prod

					String[] lines = line.split("\t");
					seqSearch.start(lines[0].trim());

				}

			}

			// close writer after Ids have been checked in ES

			System.out.println(
					"All Ids Have been checked against SharedsSearch for : " + searchField + " are now complete");
			bw.flush();
			bw.close();

			finishTime = System.currentTimeMillis();
			System.out.println("Finish.... " + finishTime);

			System.out.println("Total Time to fetch all : " + searchField + " "
					+ Long.valueOf((finishTime - startTime) / 1000) + " seconds");

			/*
			 * // creating database connection init(); // cleanup data to temp table
			 * cleanUpTempTables(); loadData(0);
			 */

		} catch (IOException ex) {
			System.out.println("Exception reading from file!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Exception to run sharedSearch!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

	}
	
	/* load data to temp tables using sqlldr */
	private void loadData(int index) {
		
		String[] sqlldrs = sqlldrFileName.split(",");
	
		try
		{
			Runtime runTime = Runtime.getRuntime();
			Process process = runTime.exec("./" + sqlldrs[index] + " " + outFileName);
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
			String[] tableNames = tableToBeTruncated.split(",");			// just to give it meaningful name in sql stmt
			
			if(con != null)
			{
				for(String tableName: tableNames)
				{
					stmt = con.createStatement();
					rs = stmt.executeQuery("select count(*) as count from " + tableName);
					if(rs.next())
						System.out.println("tempTable Count: "+ rs.getInt("count"));
				}
				
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
					String[] tableNames = tableToBeTruncated.split(",");
					for(String tableName: tableNames)
					{
						System.out.println("About to truncate: " + tableName);
						stmt.execute("truncate table " + tableName);
						System.out.println(tableName + " truncated");	
					}
					
														
				}
			}
			
		}
		catch(SQLSyntaxErrorException ex)
		{
			logger.error(ex.getCause());
			logger.error(ex.getMessage());
			ex.printStackTrace();
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

	/**
	 * @author TELEBH
	 * @Date: 01/20/2021
	 * @Description: Find weekly QA DB Counts to compare with ES weekly Counts, so all counts listed in one SNS message, So Frank can only check one email message
	 */
	public void CompareWeeklyQADBCounts()
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String query;
		Map<String,Integer> weeklyDBQACounts = new HashMap<>();
		try
		{
			//Initialize DB connection
			init();
			for(DBTables dbTable: DBTables.values())
			{
				if(dbTable.equals(DBTables.BD_MASTER) || dbTable.equals(DBTables.UPT_MASTER) || dbTable.equals(DBTables.KNOVEL_MASTER))
					query = "select database,count(*) as totalCount from " + dbTable + " group by database order by totalCount asc";
				else
					query = "select count(*) as totalCount from " + dbTable;
				
				stmt = con.prepareStatement(query);
				rs = stmt.executeQuery();
				while(rs.next())
				{
					if(rs.getString("DATABASE") != null)
						weeklyDBQACounts.put(rs.getString("DATABASe"), 0);
				}
			}
			
		}
		/*catch(SQLException ex)
		{
			logger.error("weekly QA DB Counts exception: ");
			logger.error("Cause: " + ex.getCause());
			logger.error("Error: " + ex.getMessage());
			ex.printStackTrace();
		}*/
		catch(Exception ex)
		{
			logger.error("wCompareWeeklyQADBCounts exception: ");
			logger.error("Cause: " + ex.getCause());
			logger.error("Error: " + ex.getMessage());
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
