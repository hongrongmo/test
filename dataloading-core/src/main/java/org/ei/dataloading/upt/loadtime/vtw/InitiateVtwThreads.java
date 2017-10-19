package org.ei.dataloading.upt.loadtime.vtw;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.ei.dataloading.upt.loadtime.vtw.VTWAssetAPI.MyHttpResponseHandler;

/**
 * 
 * @author TELEBH
 * @Date: 02/09/2017
 * @Description: as per Bart request is to download VTW patents using multhithreads.
 * we will need to use "10" threads (Max 20), each Thread will download "2000" Patents, and no need
 * for waiting time (sleep time) using AssetAPI (instead of Search API)
 * 
 * # of threads: 10
 * # of Patents in each Thread: 2k
 */


public class InitiateVtwThreads {

	private static HttpConnectionManager connectionManager = null;
	private static CloseableHttpClient client = null;


	private static int numberOfRuns = 0;
	private static String queueName = "acc-contributor-event-queue-EV";
	private static int numOfThreads = 3;
	private static String sqlldrFileName = null;
	static int loadNumber = 0;
	static int recsPerZipFile = 2000;
	static int recsPerSingleConnection = 2000;
	static String type = "forward";


	// epoch name list for individual dir for each thread in "raw_data" to zip
	static List<String> raw_Dir_Names;
	
	
	public static void main(String[] args) throws Exception {

		if(args.length >3)
		{
			if(args[0] !=null)
			{
				if(Pattern.matches("^\\d*$", args[0]))
				{
					numberOfRuns = Integer.parseInt(args[0]);
				}
				else
				{
					System.out.println("NumOfRuns has wrong format");
					System.exit(1);
				}


			}
			if(args[1] !=null)
			{
				queueName = args[1];
			}
			if(args [2] !=null)
			{
				if(Pattern.matches("^\\d*$", args[2]))
				{
					numOfThreads = Integer.parseInt(args[2]);
				}
				else
				{
					System.out.println("NumOfThreads has wrong format");
					System.exit(1);
				}
			}
		}

		if(args.length >7)
		{
			if(args[3] !=null)
			{
				sqlldrFileName = args[3];
			}

			if(args[4] !=null)
			{
				if(Pattern.matches("^\\d*$", args[4]))
				{
					loadNumber = Integer.parseInt(args[4]);
				}
				else
				{
					System.out.println("loadNumber has wrong format");
					System.exit(1);
				}
			}
			if(args[5] !=null)
			{
				recsPerZipFile = Integer.parseInt(args[5]);
				System.out.println("Number of Keys per ZipFile: " + recsPerZipFile);
			}
			if(args[6] !=null)
			{
				recsPerSingleConnection = Integer.parseInt(args[6]);
				System.out.println("Number of keys per one HttpConnection: " + recsPerSingleConnection);
			}
			if(args[7] !=null)
			{
				type = args[7];
				System.out.println("Message type: " + type);
			}
		}
		else
		{
			System.out.println("not enough parameters!");
			System.exit(1);
		}

		try
		{

			DateFormat dateFormat = new SimpleDateFormat("E, MM/dd/yyyy-hh:mm:ss a");
			Date date;
			long epoch;

			CountDownLatch latch = new CountDownLatch(numOfThreads);


			/*
			 * added on Wed 04/05/2017 to fix issue of zipfile check, by zipping downloaded files after all threads finish downloadiong instead of
			 * executing zipDownload within each thread
			 */

			raw_Dir_Names = new ArrayList<String>();
			 

			// create & start Threads

			for(int i=1;i<=numOfThreads;i++)
			{
				date = dateFormat.parse(dateFormat.format(new Date()));
				epoch = date.getTime();		

				System.out.println("Thread" + i + " epoch: " + epoch);

				if(type !=null && type.equalsIgnoreCase("forward"))
					raw_Dir_Names.add(Long.toString(epoch));
				else if(type !=null && type.equalsIgnoreCase("backfill"))
					raw_Dir_Names.add("back_" + Long.toString(epoch));
				ArchiveVTWPatentAsset thread = new ArchiveVTWPatentAsset(numberOfRuns,queueName,sqlldrFileName,
						loadNumber,recsPerZipFile,recsPerSingleConnection,type,
						epoch,"Thread" + i, latch);
				thread.start();

				// to get unique epoch timestamp which used for naming raw_dir
				Thread.sleep(1000);
				
				//obj = thread;

			}
			
			
			// wait after all threads finish downloading
			latch.await();
			System.out.println("In Main thread after completion of " + numOfThreads + " threads");
			System.out.println("FINISHED................." + new Date().getTime());
			
			
			//Zip downloaded files (each in it's corresponding dir)
			
			System.out.println("all " + numOfThreads + " complete, start to zip downloaded files");
			
			ArchiveVTWPatentAsset obj = new ArchiveVTWPatentAsset(loadNumber, recsPerZipFile);
			
			for(int j=0;j<raw_Dir_Names.size();j++)
			{
				obj.zipDownloads(loadNumber, raw_Dir_Names.get(j),type);
			}
			

		}

		catch(Exception e)
		{
			e.printStackTrace();
		}



	}


}
