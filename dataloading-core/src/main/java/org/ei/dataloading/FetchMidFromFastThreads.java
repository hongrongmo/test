package org.ei.dataloading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.ei.domain.FastClient;

/**
 * 
 * @author TELEBH
 * @Date: 09/14/2020
 * @Description: To fetch m_id/doc_id associated to each individual auid (to
 *               identify missing 5M authors from ES than fast using
 *               multithreads
 */
public class FetchMidFromFastThreads {

	static String query = "";
	private static Connection con;
	static ResultSet rs = null;
	static Statement stmt = null;
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "db_xml";
	static String password = "";
	// static String fastUrl = "http://ei-main.nda.fastsearch.net:15100";
	// static String fastUrl = "http://evprod14.cloudapp.net:15100"; //PROD
	static String fastUrl = "http://evdr09.cloudapp.net:15100"; // DR

	static String fastQuery = "";
	static int pageRecCount = 25;
	static String fastField;
	static int numOfThreads = 50;
	static String tableName;
	static String columnName;
	static String esField;
	static int counter = 0;

	BufferedReader in = null;

	static List<String> idList = new ArrayList<String>();

	public static void main(String[] args) {

		String fileName = null;

		if (args.length < 4) {
			System.out.println("Not Enough Arguments!!!");
			System.exit(1);
		}
		if (args[0] != null) {
			fastField = args[0];
			if (fastField.equalsIgnoreCase("auid") || fastField.equalsIgnoreCase("afid")) {
				System.out.println("Query Fast for: " + fastField);
			}

		}
		if (args[1] != null) {
			numOfThreads = Integer.parseInt(args[1]);
			System.out.println("Number of Threads : " + numOfThreads);
		}
		if (args[2] != null) {
			pageRecCount = Integer.parseInt(args[2]);
		}
		if (args[3] != null) {
			fileName = args[3];
			System.out.println("Ids Input FileName: " + fileName);
		}

		readInput(fileName);
		if (idList.size() > 0) {
			double subList = idList.size() / numOfThreads;
			int subListSize = (int) subList;
			System.out.println("each of the " + numOfThreads + " will check " + subListSize + " ids");
			int start = 0;
			int last = (subListSize - 1);
			CountDownLatch latch = new CountDownLatch(numOfThreads);
			if (numOfThreads == 1)
				last = subListSize - 1;

			try {
				System.out.println("STARTING............." + new Date().getTime());
				for (int i = 0; i < numOfThreads; i++) {

					FastClient client = new FastClient();
					client.setBaseURL(fastUrl);
					client.setResultView("ei");
					client.setOffSet(0);
					client.setPageSize(pageRecCount);
					client.setDoCatCount(true);
					client.setDoNavigators(false);
					client.setPrimarySort("rank");
					client.setPrimarySortDirection("+");

					FastDocIdsThreads thread = new FastDocIdsThreads("Thread" + i, latch, start, last, client,
							fastField);
					thread.start();

					// set start & last indexes for later threads
					synchronized (thread) {
						start = last + 1;
						if (i < (numOfThreads - 2))
							last = start + (subListSize - 1);
						else
							last = idList.size() - 1;
						System.out.println("***********************");
					}
				}
				latch.await();
				System.out.println("In Main thread after completion of " + numOfThreads + " threads");
				System.out.println("FINISHED................." + new Date().getTime());

			}

			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void readInput(String fileName) {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
			String auid;
			while ((auid = br.readLine()) != null) {
				if (!(auid.isEmpty())) {
					idList.add(auid.trim());

				}
			}
			System.out.println("Total # of AUIDS to check in fast: " + idList.size());

		} catch (IOException ex) {
			System.out.println("Exception reading from file!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Exception to run FindMissingAuidsRecordsInFast!!!!");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

}

class FastDocIdsThreads implements Runnable {
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

	public FastDocIdsThreads(String name, CountDownLatch latch, int start, int last, FastClient client,
			String fastField) {
		threadName = name;
		this.latch = latch;
		startIndex = start;
		lastIndex = last;

		fastClient = client;
		this.fastField = fastField;

		if (startIndex < -1 || lastIndex < -1) {
			System.out.println("invalid startIndex: " + startIndex + " or lastIndex: " + lastIndex + "!! exit...");
			System.exit(1);
		}
	}

	public void start() {
		if (th == null) {
			try {
				th = new Thread(this, threadName);
				th.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		try {

			// in = new BufferedReader(new FileReader("fastdocs.txt"));
			File file = new File(fastField + "_fastdocs_" + threadName + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileWriter(file);

		
			for (int i = startIndex; i <= lastIndex; i++) {
				fastQuery = "((meta.collection:ei) AND " + fastField + ":\"" + FetchMidFromFastThreads.idList.get(i)
						+ "\")";
				fastClient.setQueryString(fastQuery);
				fastClient.getBaseURL();
				fastClient.search();
				
					int hit_count = fastClient.getHitCount();					
					  //System.out.println("HitCount: " + hit_count);
					 
						  List<String[]> l = fastClient.getDocIDs();
						  synchronized (l) {
						  for (int j = 0; j < l.size(); j++) { 
							  String[] docID = (String[]) l.get(j);
							  	out.write(docID[0]+"\n"); 
						  }
						  l.clear();			// clear list before next iteration, otherwise accumulates to it
					  }
					 

			}
			System.out.println("finished checking fast doc_count for " + threadName);
			latch.countDown();

		}

		catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)

				{
					out.flush();
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
