package org.ei.dataloading;

/**
 * @author TELEBH
 * @Date: 04/08/2020
 * @Description : Fech Hitcount from fast for comparison with ES
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.ei.domain.FastClient;

public class FetchHitCountFromFast {
	
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

static String database;
static String loadNumberFile; 
static String fastQuery="";
static int pageRecCount = 25;
static String doc_type;
static int numOfThreads = 50;
static String tableName;
static String columnName;
static String fastField = "wk";
static int counter = 0;

BufferedReader in = null;


static List<String> idList = new LinkedList<String>();


public static void main(String[] args) throws Exception 
{
	if(args.length<5)
	{
		System.out.println("Not ENough Parameters!!!!");
		System.exit(1);
	}

	else
	{
		if(args[0] !=null)
		{
			database= args[0];
			System.out.println("Database: " + database);
		}
		if(args[1] !=null)
		{
			loadNumberFile = args[1];
			System.out.println("LoadNumberFileName: " + loadNumberFile);
		}

		if(args[2] !=null)
		{
			numOfThreads = Integer.parseInt(args[2]);
			System.out.println("Number of Threads : " +  numOfThreads);
		}
		if(args[3] != null)
		{
			pageRecCount = Integer.parseInt(args[3]);
		}
		if(args[4] != null)
		{
			fastField = args[4];
		}
	}
	
	
	fetchLoadBumbers();
		
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

			FastHitCountThreads thread = new FastHitCountThreads("Thread" + i, latch, start, last, client, doc_type, fastField, database);
			thread.start();
			
			// set start & last indexes for later threads
			synchronized (thread) 
			{
				start = last + 1;
				if(i<(numOfThreads -2))
					last = start + (subListSize);
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

public static void fetchLoadBumbers()
{
	try
	{
		BufferedReader rd = new BufferedReader(new FileReader(loadNumberFile));
		String loadNumber;
		while((loadNumber = rd.readLine()) != null)
		{
			idList.add(loadNumber);
		}
		System.out.println("Total LoadNumberList Size: " + idList.size());
	}
	catch (FileNotFoundException e) {
		System.out.println("File Not Exist!!!");
		e.printStackTrace();
	} catch (IOException e) {
		System.out.println("Problem reading file!!!");
		e.printStackTrace();
	}
	
}

protected static Connection getConnection(String connectionURL, String driver, String username, String password) throws Exception
{
	Class.forName(driver);
	Connection con = DriverManager.getConnection(connectionURL, username, password);
	return con;
}
}


class FastHitCountThreads implements Runnable
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
String database;

FileWriter out = null;

public FastHitCountThreads(String name, CountDownLatch latch, int start, int last, FastClient client, String doc_type, String fastField, String database)
{
	threadName = name;
	this.latch = latch;
	startIndex = start;
	lastIndex = last;

	fastClient = client;
	this.doc_type = doc_type;
	this.fastField = fastField;
	this.database = database;

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
		File file = new File("fastcount/" + database + "_fastdocs_" + threadName + ".txt");
		if(!file.exists())
		{
			file.createNewFile();
		}
		out = new FileWriter(file);

		for(int i=startIndex; i<=lastIndex;i++)
		{

			//fastQuery = fastField + ":" + FetchHitCountFromFast.idList.get(i) + " AND db:\"" + database + "\"";  // loadnumber query
			fastQuery = fastField + ":" + FetchHitCountFromFast.idList.get(i) + " AND db:\"" + database + "\" AND yr:2013 AND dt:\"ca\"";  // loadnumber and doc-type query
			//fastQuery = fastField + ":" + FetchHitCountFromFast.idList.get(i) + " AND db:\"" + database + "\" AND dt:\"pa\" AND yr:1999";  // loadnumber and doc-type and year query
					
			//System.out.println("fastQuery: " + fastQuery);   // only for debugging
			fastClient.setQueryString(fastQuery);
			fastClient.getBaseURL();
			fastClient.search();

			int hit_count = fastClient.getHitCount();
			out.write(FetchHitCountFromFast.idList.get(i) + "\t" + hit_count + "\n");

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
