package vtw.threads.vtw;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;



public class InitiatVtwThreads 
{

	private static int numberOfRuns=0;
	private static String queueName = "acc-contributor-event-queue-EV";   // 1st UAT queue
	private static int numOfThreads = 1;
	private static String sqlldrFileName = null;
	static int loadNumber = 0;
	static int recsPerZipFile = 20000;
	static int recsPerSingleConnection = 1000;
	static String type = "forward";

	// epoch name list for individual dir for each thread in "raw_data" to zip
	static List<String> raw_Dir_Names;


	public static void main(String[] args) throws Exception {

		if (args.length > 3) {
			if (args[0] != null) {
				if (Pattern.matches("^\\d*$", args[0])) {
					numberOfRuns = Integer.parseInt(args[0]);
				} else {
					System.out.println("NumOfRuns has wrong format");
					System.exit(1);
				}

			}
			if (args[1] != null) {
				queueName = args[1];
			}
			if (args[2] != null) {
				if (Pattern.matches("^\\d*$", args[2])) {
					numOfThreads = Integer.parseInt(args[2]);
				} else {
					System.out.println("NumOfThreads has wrong format");
					System.exit(1);
				}
			}
		}

		if (args.length > 7) {
			if (args[3] != null) {
				sqlldrFileName = args[3];
			}

			if (args[4] != null) {
				if (Pattern.matches("^\\d*$", args[4])) {
					loadNumber = Integer.parseInt(args[4]);
				} else {
					System.out.println("loadNumber has wrong format");
					System.exit(1);
				}
			}
			if (args[5] != null) {
				recsPerZipFile = Integer.parseInt(args[5]);
				System.out.println("Number of Keys per ZipFile: "
						+ recsPerZipFile);
			}
			if (args[6] != null) {
				recsPerSingleConnection = Integer.parseInt(args[6]);
				System.out.println("Number of keys per one HttpConnection: "
						+ recsPerSingleConnection);
			}
			if (args[7] != null) {
				type = args[7];
				System.out.println("Message type: " + type);
			}
		} else {
			System.out.println("not enough parameters!");
			System.exit(1);
		}

		try
		{	
			// original

			/*ArchiveVTWPatentAsset thread1 = new ArchiveVTWPatentAsset(numberOfRuns,queueName,sqlldrFileName,
						loadNumber,recsPerZipFile,recsPerSingleConnection,type,
						"Thread1");

				thread1.start();*/

			DateFormat dateFormat = new SimpleDateFormat(
					"E, MM/dd/yyyy-hh:mm:ss a");
			Date date;
			long epoch;

			CountDownLatch latch = new CountDownLatch(numOfThreads);

			String currDir = System.getProperty("user.dir");
			File downDir = null;
			File wo_downDir = null;
			String[] xmlFiles;

			/*
			 * added on Wed 04/05/2017 to fix issue of zipfile check, by zipping
			 * downloaded files after all threads finish downloadiong instead of
			 * executing zipDownload within each thread
			 */

			raw_Dir_Names = new ArrayList<String>();

			// create & start Threads

			for (int i = 1; i <= numOfThreads; i++) 
			{
				date = dateFormat.parse(dateFormat.format(new Date()));
				epoch = date.getTime();

				System.out.println("Thread" + i + " epoch: " + epoch);

				raw_Dir_Names.add(Long.toString(epoch));

				ArchiveVTWPatentAsset thread = new ArchiveVTWPatentAsset(
						numberOfRuns, queueName, sqlldrFileName, loadNumber,
						recsPerZipFile, recsPerSingleConnection, type, epoch,
						"Thread" + i, latch);
				thread.start();
			}


			// wait after all threads finish downloading
			latch.await();
			System.out.println("In Main thread after completion of "
					+ numOfThreads + " threads");
			System.out.println("FINISHED................."
					+ new Date().getTime());

			// Zip downloaded files (each in it's corresponding dir)

			System.out.println("all " + numOfThreads
					+ " complete, start to zip downloaded files");



			ArchiveVTWPatentAsset obj = new ArchiveVTWPatentAsset(loadNumber,
					recsPerZipFile, type);
/*
			for (int j = 0; j < raw_Dir_Names.size(); j++) {
				if (!(raw_Dir_Names.get(j).isEmpty())
						&& raw_Dir_Names.get(j) != null) 
				{
					// HH 01/30/2018 added to resolve null pointer exception due
					// to empty directories

					if(type!=null && type.equalsIgnoreCase("forward"))
					{
						downDir = new File(currDir + "/raw_data/" + type + "_"
								+ raw_Dir_Names.get(j));
					}
					else if(type!=null && type.equalsIgnoreCase("backfill"))
					{
						downDir = new File(currDir + "/raw_data/" + type + raw_Dir_Names.get(j));
					}


					xmlFiles = downDir.list();
					if (xmlFiles !=null && xmlFiles.length > 0) {
						obj.zipDownloads(loadNumber, raw_Dir_Names.get(j), type);
					}

					else {
						System.out.println("Download dir: "
								+ downDir.getAbsolutePath()
								+ " is Empty so nothing to zip, delete the dir");

						downDir.delete();
					}
				}

			}*/
		}
		catch (Exception e) {
			e.printStackTrace();
		}


	}





}
