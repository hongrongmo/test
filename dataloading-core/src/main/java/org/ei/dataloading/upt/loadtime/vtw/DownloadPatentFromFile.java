package org.ei.dataloading.upt.loadtime.vtw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 
 * @author TELEBH
 * @Date: 02/09/2017
 * @Description: if we ever need to re-download Patents from SQS Archive text
 *               file (i.e. re-download files that failed to download when got
 *               from SQS message, for other reason, for example when 1st
 *               downloaded files with Byte Order Marker, and our process failed
 *               to convert, we had to modify download program taking off these
 *               BOM, then we need to download these files again, since the
 *               message were deleted from the QUEUE, so to get these Patents
 *               IDS again to re-download we can get them form archive file, in
 *               this case we use this program to do so
 * 
 *               this class is to read SQS archive text file, get the PatentID,
 *               keep it in a list, pass it to VTWAssetAPI for download
 * 
 *               NOTE: this class logic same as "ReadPatentIdFromFile.java"
 *               except that this class if for download file using AssetAPI (not
 *               SearchAPI), while "ReadPatentIdFromFile.java" is for SearchAPI
 * 
 *               this class should be run via one single thread. so set default
 *               threadname as "Thread1"
 */
public class DownloadPatentFromFile {

	public static final String FIELDDELIM = "\t";

	private static String fileName = null;
	private static int loadNumber = 0;
	private static int recsPerZipFile = 2000;
	private static int recsPerSingleConnection = 2000;

	private static String threadName = "Thread1";
	private static String type = "forward";

	static CloseableHttpClient client = null;

	// hold the list of Patent-Ids; with their signedAssetURL if any; to download
	private static Map<String, String> patentIds = new LinkedHashMap<String, String>();

	private static long startTime = System.currentTimeMillis();
	private static long endTime = System.currentTimeMillis();
	private static long midTime = System.currentTimeMillis();

	public DownloadPatentFromFile() {

	}

	public static void main(String[] args) {
		if (args.length > 0) {
			if (args[0] != null) {
				fileName = args[0];

				System.out.println("read PatentIDs from file: " + fileName);
			}
		}

		if (args.length > 4) {
			if (args[1] != null) {
				if (Pattern.matches("^\\d*$", args[1])) {
					loadNumber = Integer.parseInt(args[1]);
				} else {
					System.out.println("loadNumber has wrong format");
					System.exit(1);
				}
			}
			if (args[2] != null) {
				recsPerZipFile = Integer.parseInt(args[2]);
				System.out.println("Number of Keys per ZipFile: " + recsPerZipFile);
			}
			if (args[3] != null) {
				recsPerSingleConnection = Integer.parseInt(args[3]);
				System.out.println("Number of keys per one HttpConnection: " + recsPerSingleConnection);
			}
			if (args[4] != null) {
				type = args[4];
				System.out.println("Message type: " + type);
			}
		}

		else {
			System.out.println("not enough parameters!");
			System.exit(1);
		}

		midTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();
		System.out.println("Time before reading archive file " + (endTime - startTime) / 1000.0 + " seconds");
		System.out.println("total Time used " + (endTime - startTime) / 1000.0 + " seconds");

		DownloadPatentFromFile obj = new DownloadPatentFromFile();
		obj.readFile(fileName);

		midTime = System.currentTimeMillis();
		endTime = System.currentTimeMillis();
		System.out.println("Time after reading archive file " + (endTime - startTime) / 1000.0 + " seconds");
		System.out.println("total Time used " + (endTime - startTime) / 1000.0 + " seconds");

		// get PatentID's metadata

		if (!(patentIds.isEmpty())) {
			try {

				// Zip downloaded files (each in it's corresponding dir)
				ArchiveVTWPatentAsset archivePatent = new ArchiveVTWPatentAsset();
				// archivePatent.readZipFileNameFromFile(loadNumber);

				// to make each instance running of this class does not conflict with each other
				// (not using same downloads dir,so can be deleted once zip process is complete)
				DateFormat dateFormat = new SimpleDateFormat("E, MM/dd/yyyy-hh:mm:ss a");
				Date date = dateFormat.parse(dateFormat.format(new Date()));
				long epoch = date.getTime();

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println(
						threadName + " :time before downloading files " + (endTime - midTime) / 1000.0 + " seconds");
				System.out.println(threadName + " :total time used " + (endTime - startTime) / 1000.0 + " seconds");

				VTWAssetAPI vtwAssetAPI = new VTWAssetAPI(Long.toString(epoch), recsPerSingleConnection, threadName);
				vtwAssetAPI.downloadPatent(patentIds, vtwAssetAPI.getInstance(), Long.toString(epoch), threadName,
						type);

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println(
						threadName + " :time after downloading files " + (endTime - midTime) / 1000.0 + " seconds");
				System.out.println(threadName + " :total time used " + (endTime - startTime) / 1000.0 + " seconds");

				// Zip downloaded files (each in it's corresponding dir)

				// 02/22/2018 temp comment out for WO backfill/forward, uncomment for US/EUP,
				// uncommented 07/29/2019 to download failed files
				archivePatent.zipDownloads(loadNumber, Long.toString(epoch), type); // temp comment out for downloading
																					// backword records generation>10,
																					// uncomment Back after finish

				midTime = endTime;
				endTime = System.currentTimeMillis();
				System.out.println(
						threadName + " :time after zip downloaded files " + (endTime - midTime) / 1000.0 + " seconds");
				System.out.println(threadName + " :total time used " + (endTime - startTime) / 1000.0 + " seconds");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void readFile(String filename) {
		String patentID = null, signedAssetUrl = "", generation = null;
		String fields[] = null;
		if (filename != null) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(filename));
				for (String line; (line = br.readLine()) != null;) {
					 patentID = line;
					
					//HH commented out 07/29/2019 as file only contains patentID wzout delimiter
					/*
					 * fields = line.split(FIELDDELIM); if(fields.length >2) { patentID = fields[0];
					 * signedAssetUrl = fields[2];
					 * 
					 * if(fields[4] !=null && fields[4].contains("content/pat")) generation =
					 * fields[4].substring(fields[4].lastIndexOf("/")+1, fields[4].length()); else
					 * if(fields[3] !=null && fields[3].contains("content/pat")) generation =
					 * fields[3].substring(fields[3].lastIndexOf("/")+1, fields[3].length()); //
					 * that was used for backword when out file format was diff else
					 * System.out.println("something wrong!!!"); }
					 */

					// 02/22/2018 download Prod WO backfil 1st bulk of 02/21/2018 no limit to
					// generation, imply download all
					if (patentID != null
							&& (patentID.substring(0, 2).equalsIgnoreCase("US")
									|| patentID.substring(0, 2).equalsIgnoreCase("EP"))
							|| patentID.substring(0, 2).equalsIgnoreCase("WO"))

					{
						patentIds.put(patentID, signedAssetUrl);

					} else {
						System.out.println("Skip downloading Patent : " + patentID + " of generation: " + generation
								+ " and type: " + patentID.substring(0, 2));
					}

				}

			} catch (IOException e) {
				System.out.println("What is Wrong with the file!");
				e.printStackTrace();
				e.printStackTrace();
			}

		}
	}

}
