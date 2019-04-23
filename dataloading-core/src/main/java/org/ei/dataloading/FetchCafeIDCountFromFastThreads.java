package org.ei.dataloading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.ei.domain.FastClient;

/**
 * 
 * @author TELEBH
 * @Date: 07/30/2018
 * @Description: to get the list of all AUID/AFID and HIT_COUNT in fast DR using multithreads
 * to increase QPS as it is low compared to PROD. and that casing Delay in checking all AUIDS in fast DR, as suggested by Harold
 * if increase QPS from current ~25 in DR to ~93 as in PROD that should increase performance
 * in other words DR performance itself to process request once received it is very good
 * the problem is latency from sending request from our machine to fast DR instance
 *  and this should be enhanced by increasing # of queries submitted to DR /second by using multithreading
 *  
 */

public class FetchCafeIDCountFromFastThreads 
{
	static String query = "";
	private static Connection con;
	static ResultSet rs = null;
	static Statement stmt = null;
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "db_xml";
	static String password = "ny5av";
	//static String fastUrl = "http://ei-main.nda.fastsearch.net:15100";
	//static String fastUrl = "http://evprod14.cloudapp.net:15100";		//PROD
	static String fastUrl = "http://evdr09.cloudapp.net:15100";			//DR

	static String fastQuery="";
	static int pageRecCount = 25;
	static String doc_type;
	static int numOfThreads = 50;
	static String tableName;
	static String columnName;
	static String esField;
	static int counter = 0;

	BufferedReader in = null;


	static List<String> idList = new ArrayList<String>();

	public static void main(String[] args) throws Exception 
	{


		if(args[0] !=null)
		{
			doc_type = args[0];
			if(doc_type.equalsIgnoreCase("apr"))
			{
				tableName = "author_count";
				columnName = "authorid";
				esField = "auid";
			}

			else if(doc_type.equalsIgnoreCase("ipr"))
			{
				tableName = "institute_count";
				columnName = "affid";
				esField = "afid";
			}

		}
		if(args[1] !=null)
		{
			numOfThreads = Integer.parseInt(args[1]);
			System.out.println("Number of Threads : " +  numOfThreads);
		}
		if(args[2] != null)
		{
			pageRecCount = Integer.parseInt(args[2]);
		}


		if(args.length > 3)
		{
			if(args[3] !=null)
			{
				url = args[3];
			}

			if(args[4] !=null)
			{
				driver = args[4];
			}
			if(args[5] !=null)
			{
				username = args[5];
			}

			if( args[6] != null)
			{
				password = args[6];
			}

			if(args[7] !=null)
			{
				fastUrl = args[7];
			}

		}

		getIdsFromDB();
		if(idList.size() >0)
		{
			double subList = idList.size()/numOfThreads;
			int subListSize = (int)subList;
			System.out.println("each of the " + numOfThreads + " will check " +  subListSize + "ids");
			int start = 0;
			int last = (subListSize -1);
			CountDownLatch latch = new CountDownLatch(numOfThreads);
			if(numOfThreads ==1)
				last = subListSize - 1;

			System.out.println("STARTING............." + new Date().getTime());
			for(int i=0; i<numOfThreads;i++)
			{


				FastClient client = new FastClient();
				client.setBaseURL(fastUrl);
				client.setResultView("ei");
				client.setOffSet(0);
				client.setPageSize(pageRecCount);
				client.setDoCatCount(true);
				client.setDoNavigators(false);
				client.setPrimarySort("rank");
				client.setPrimarySortDirection("+");

				FastDocCountThreads thread = new FastDocCountThreads("Thread" + i, latch, start, last, client, doc_type, esField);
				thread.start();
				
				// set start & last indexes for later threads
				synchronized (thread) 
				{
					start = last + 1;
					if(i<(numOfThreads -2))
						last = start + (subListSize -1);
					else
						last = idList.size() -1;
					System.out.println("***********************");
				}
			}
			latch.await();
			System.out.println("In Main thread after completion of " + numOfThreads + " threads");
			System.out.println("FINISHED................." + new Date().getTime());
		
			
		}

	}

	public static void getIdsFromDB()
	{
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;

		//HH 02/23/2019 Added since we started to oull auid doc_count from fast so to get most recent version of all cpx afid, get from lkup because institute_count still has the old version
		if(doc_type !=null && doc_type.equalsIgnoreCase("ipr"))
			//query = "select distinct institute_id from cmb_af_lookup where status='matched'";   //lookup table missing some records with updated status, so use following query for more secur
			// added 04/23/2019 for robust match
			query = "select distinct INSTITUTE_ID from cmb_af_lookup where pui in (select cafe_pui from db_xml.bd_master where database='cpx')";
		else if(doc_type !=null && doc_type.equalsIgnoreCase("apr"))
			query = "select " + columnName + " from " + tableName;
		
		System.out.println("Running queery: " + query);
		
		try
		{
			con = getConnection(url, driver, username, password);
			System.out.println(query);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			System.out.println("Got Records....");
			while(rs.next())
			{
				if(rs.getString(1) !=null)
				{
					idList.add(rs.getString(1));
				}
			}
		}
		catch(SQLException ex)
		{
			System.out.println("error in sql query!! " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(stmt !=null)
			{
				try
				{
					stmt.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
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
			if(con !=null)
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

	protected static Connection getConnection(String connectionURL, String driver, String username, String password) throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL, username, password);
		return con;
	}
}


class FastDocCountThreads implements Runnable
{
	private Thread th;
	private String threadName = null;
	private CountDownLatch latch;

	int startIndex;
	int lastIndex;

	FastClient fastClient;
	String doc_type;
	String fastQuery = null;
	String fastField;

	FileWriter out = null;
	
	public FastDocCountThreads(String name, CountDownLatch latch, int start, int last, FastClient client, String doc_type, String fastField)
	{
		threadName = name;
		this.latch = latch;
		startIndex = start;
		lastIndex = last;

		fastClient = client;
		this.doc_type = doc_type;
		this.fastField = fastField;

		if(startIndex <-1 || lastIndex <-1)
		{
			System.out.println("invalid startIndex: " + startIndex + " or lastIndex: " + lastIndex + "!! exit...");
			System.exit(1);
		}
	}
	public void start()
	{
		if(th ==null)
		{
			try
			{
				th = new Thread(this,threadName);
				th.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	public void run()
	{
		try {

			// in = new BufferedReader(new FileReader("fastdocs.txt"));
			File file = new File(doc_type + "_fastdocs_" + threadName + ".txt");
			if(!file.exists())
			{
				file.createNewFile();
			}
			out = new FileWriter(file);

			for(int i=startIndex; i<lastIndex;i++)
			{

				fastQuery = "(" + fastField + ":\"" + FetchCafeIDCountFromFastThreads.idList.get(i) + "\")";
				fastClient.setQueryString(fastQuery);
				fastClient.getBaseURL();
				fastClient.search();

				int hit_count = fastClient.getHitCount();
				out.write(FetchCafeIDCountFromFastThreads.idList.get(i) + "\t" + hit_count + "\n");

			}
			System.out.println("finished checking fast doc_count for " + threadName );
			latch.countDown();
			
		}


		catch(IOException ex)
		{
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
				if(out !=null)
					
				{
					out.flush();
					out.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

	}

}
