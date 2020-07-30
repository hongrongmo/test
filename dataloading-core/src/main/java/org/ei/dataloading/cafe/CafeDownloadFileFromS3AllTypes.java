package org.ei.dataloading.cafe;

/*import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Delete;
import io.searchbox.core.BulkResult.BulkResultItem;*/

/*
 * @telebh: Friday 07/26/2019, wk: [201931] due restricted Mapping type removal in ES 6.7, I have to modify all classes taking off the combined/multi type mapping index "cafe" and replace it
 * with the 2 new indices "author" , "affiliation" 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.ei.common.georef.DocumentView.MultiValueLookupValueDecorator;
import org.ei.dataloading.awss3.AmazonS3Service;
import org.ei.dataloading.upt.loadtime.vtw.ArchiveVTWPatentAsset;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

public class CafeDownloadFileFromS3AllTypes {

	private static Connection con = null;


	static String url="jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_correction1";
	static String password="";
	static String database ="cpx";
	//static String action;
	static String doc_type;
	static String archive_date;
	static Boolean isArchive_date_range = true;
	static int recsPerZipFile;
	static String tableToBeTruncated = "cafe_inventory_temp";
	static String sqlldrFileName = "cafeInventoryFileLoader.sh";
	static int updateNumber;
	static String ani_DeletionTempTable = "cafe_weekly_deletion";
	static String deletionSqlldrFileName = "cafeANIDeletionFileLoader.sh";
	static int numOfThreads = 1;
	
	static int recsPerEsbulk;
	static String esDomain = "search-evcafe-prod-h7xqbezrvqkb5ult6o4sn6nsae.us-east-1.es.amazonaws.com";
	static String esIndexName;		// added 05/10/2018 as ES 6.2 and up split types in separate indices
	static String operationType="normal";		//HH added 07/29/2020 to give option of adhoc download of previously processed cafe files
	


	static int curRecNum = 0;
	static int zipFileID = 1;

	File S3dir;    // to hold downloaded bulk of s3 keys/files
	private static String currDir;   // to hold current working dir to use fro zip downloaded cafe keys

	//private GetANIFileFromCafeS3Bucket objectFromS3;  // 05/19/2017 moved to multithreading class instead
	private AmazonS3 s3Client;

	private long epoch;
	
	// for naming zip files with both start and end dates
	private String archive_date_end;
	
	

	// for parsing archive_date
	SimpleDateFormat archiveDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//05/24/2016 hold cafe_inventory key/epoch List
	Map <String,Long> cafeInventory_List = new HashMap<String,Long>();
	private PrintWriter out;

	/* 05/16/2017 use multithreading instead to be able to download large # of files 
	 * instead of one single thread that crashed for APR "APR-06-17" that has total 4M+ to download
	 * APR download for archive "APR-06-17" failed after downloaded 2M+ out of total 4M+ due to SQLRecoverableException. so need to implement multithreading
	 */
	private List<LinkedHashMap<String, String>> keys_to_be_downloaded = new ArrayList<LinkedHashMap<String,String>>();


	//08/01/2016 combine Keys List for action "d" for direct deletion from ES & DB without dowbload from S3 bucket
	private HashMap<String,String> keys_to_be_deleted = new HashMap<String,String>();
	private List<String> keys_MID_to_be_deleted = new ArrayList<String>();

	
	public static final char FIELDDELIM = '\t';

	
	//04/17/2018 added for accumulating archive_dates instead of single value for each iteration
	StringBuffer archiveDateList = new StringBuffer();
	
	

	public static void main(String[] args)
	{

		if(args.length >6)
		{
			if(args[0]!=null)
			{
				url = args[0];
			}
			if(args[1]!=null)
			{
				driver = args[1];
			}
			if(args[2]!=null)
			{
				username = args[2];
				System.out.println("username= "+username);
			}
			if(args[3]!=null)
			{
				password = args[3];
				System.out.println("password= "+password);
			}   

			if(args[4] !=null)
			{
				doc_type = args[4];
				System.out.println("doc_Type: " +  doc_type);
			}

			if(args[5] !=null)
			{
				// date to select which archived messages to process
				System.out.println("archive_date= " +args[5]);
				if(isValidDate(args[5]))
				{
					archive_date = args[5];
					System.out.println("Process Keys of Archive Date:  " + archive_date);
				}
				else
				{
					System.out.println("Invalid Date Format, please re-enter as 'MMM-DD-YY'");
					System.exit(1);
				}
			}
		}

		if(args.length >16)
		{
			// # of downloaded cafe key files to include in one zip file for later process/convert
			if(args[6] !=null)
			{
				recsPerZipFile = Integer.parseInt(args[6]);
				System.out.println("Number of Keys per ZipFile: " + recsPerZipFile);
			}

			if(args[7] !=null)
			{
				tableToBeTruncated = args[7];
			}

			if(args[8] !=null)
			{
				sqlldrFileName = args[8];
				System.out.println("using sqlloaderfile " + sqlldrFileName);
			}
			if(args[9] !=null)
			{
				Pattern pattern = Pattern.compile("^\\d*$");
				Matcher match = pattern.matcher(args[9]);
				if(match.find())
				{
					updateNumber = Integer.parseInt(args[9]);
				}
				else
				{
					System.out.println("did not find updateNumber or updateNumber has wrong format!!!");
					System.exit(1);
				}
			}
			if(args[10] !=null)
			{
				ani_DeletionTempTable = args[10];
				if(doc_type.equalsIgnoreCase("ani"))
					System.out.println("cafe ANI Weekly deletion temp table: " + ani_DeletionTempTable);
			}
			if(args[11] !=null)
			{
				deletionSqlldrFileName = args[11];
				if(doc_type.equalsIgnoreCase("ani"))
					System.out.println("ANI deletion sqlldrfile: " + deletionSqlldrFileName);
			}
			if(args[12] !=null)
			{
				Pattern pattern = Pattern.compile("^\\d*$");
				Matcher match = pattern.matcher(args[12]);
				if(match.find())
					numOfThreads = Integer.parseInt(args[12]);
				else {
					System.out.println("did not find numOfThreads or numOfThreads has wrong format!!!");
					System.exit(1);
				}
			}
			if(args[13] !=null)
			{
				Pattern pattern = Pattern.compile("^\\d*$");
				Matcher match = pattern.matcher(args[13]);
				if(match.find())
				{
					recsPerEsbulk = Integer.parseInt(args[13]);
					System.out.println("RecordsPerBulk:  " + recsPerEsbulk);
				}
				else
				{
					System.out.println("recsPerEsbulk has wrong format!!!");
					System.exit(1);
				}
			}
			if(args[14] !=null)
			{
				esDomain = args[14];
				System.out.println("ES Domain: " + esDomain);
			}
			
			if(args[15] !=null)
			{
				// if archive_date range is true, get keys list fit within the date range that starts from archive_date till one day before today
				isArchive_date_range = Boolean.valueOf(args[15]);
			}
			//Added 06/14/2018 for specifying indexname as per ES 6.2
			if(args[16] !=null)
			{
				esIndexName = args[16];
				if(esIndexName.equalsIgnoreCase("author") || esIndexName.equalsIgnoreCase("affiliation") || esIndexName.equalsIgnoreCase("cafe"))

					System.out.println("ES Index Name: " + esIndexName);
				else
				{
					System.out.println("Invalid ES Index Name for AU/AF profile deletion from ES, re-try with ESIndexName author/affiliation");
					System.exit(1);
				}
			}
		}
		if(args.length >17)
		{
			operationType = args[17];
			System.out.println("OperationType: " + operationType);
		}


		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}


		CafeDownloadFileFromS3AllTypes downloadFile = new CafeDownloadFileFromS3AllTypes();
		if(operationType.equalsIgnoreCase("normal"))
			downloadFile.startProcess();
		else if(operationType.equalsIgnoreCase("adhoc"))
			downloadFile.startAdhocProcess();
		else
		{
			System.out.println("Invalid operation type, re-run with option normal/adhoc");
			System.exit(1);
		}

		
	}
	
	public void startProcess()
	{
		try {
			init();
			getCafeInv_FilesInfo();
			getSnsArch_FilesInfo();
			end();

			downloadFiles();
			

		} 
		catch (Exception e) 
		{

			e.printStackTrace();
		}

	}
	public void startAdhocProcess()
	{
		try {
			init();
			getSnsArch_FilesInfo();
			end();

			downloadFiles();
			

		} 
		catch (Exception e) 
		{

			e.printStackTrace();
		}

	}
	
	public void downloadFiles()
	{
		try {
			
			// download files from s3 bucket using thread (s)
			if(keys_to_be_downloaded.size() >0)
			{
				double listSize = keys_to_be_downloaded.size()/numOfThreads;
				int subListSize = (int)listSize;
				int start = 0;
				int last = (subListSize -1);
				CountDownLatch latch = new CountDownLatch(numOfThreads);
				
				
				System.out.println("list size to download: " + keys_to_be_downloaded.size());
				if(numOfThreads ==1)
					last = subListSize -1;
				
				
				System.out.println("STARTING................." + new Date().getTime());
				for(int i=0;i<numOfThreads;i++)
				{
					GetANIFileFromCafeS3Bucket objectFromS3 = new GetANIFileFromCafeS3Bucket(s3Client,database,url,driver,username,password,S3dir);
					
					s3FileDownload thread = new s3FileDownload("Thread " + i,latch, start,last, objectFromS3);
					thread.start();
					
					//Thread.sleep(1000);
					synchronized (thread) {
						start = last + 1;
						if(i<(numOfThreads-2))
							last = start + (subListSize -1);
						else
							last = keys_to_be_downloaded.size() -1;
						
						System.out.println("***********************");	
					}
				}
				
				latch.await();
				
				System.out.println("In Main thread after completion of " + numOfThreads + " threads");
				System.out.println("FINISHED................." + new Date().getTime());
			}
			


			// zip downloaded cafe keys/files
			zipDownloads();

			// update cafe_inventory table
			if(operationType.equalsIgnoreCase("normal"))
				updateCafeInventory();  //comment for localhost only, uncomment in prod, added condition of OPT 07/29/2020 

			// close connection
			flush();

			//get the list of keys to be deleted & delete from ES & DB
			if(keys_to_be_deleted.size() >0)
			{
				if(doc_type !=null && (doc_type.equalsIgnoreCase("apr") || doc_type.equalsIgnoreCase("ipr")))
				{
					PrepareDeletion();
				}
				else if (doc_type !=null && doc_type.equalsIgnoreCase("ani"))
				{
					deleteANIData();
				}
			}

		} 
		catch (Exception e) 
		{

			e.printStackTrace();
		}

	}
	// get db connection
	public void init() 
	{
		try
		{
			con = getConnection(url,driver,username,password);
			// connect to AmazonS3
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();

			
			// if archive_date_range is on, get date list start from archive_date till one day before current day to reduce file download from cafe s3
			if(isArchive_date_range)
				getArchiveDates_List();
						
						
			// get time in epoch formate to be able to distingush which file to send to converting, in case there are multiple files to convert
			DateFormat dateFormat = new SimpleDateFormat("E, MM/dd/yyyy-hh:mm:ss a");
			Date date = dateFormat.parse(dateFormat.format(new Date()));
			epoch = date.getTime();

			//05/02/2016 create dir to download s3 files into xml for later zip and convert, on 04/18/2018 add archive_date range in filename

			S3dir=new File(doc_type+"_s3Files_"+epoch+"_"+archive_date+"-"+archive_date_end);
			if(!S3dir.exists())
			{
				S3dir.mkdir();
			}

			// for parsing ANI/Abstract CPX File
			//objectFromS3 = new GetANIFileFromCafeS3Bucket(s3Client,database,url,driver,username,password,S3dir);  // 05/19/2017 moved to multithreadig class

			/* for writing newly archived sqs message info from sns_archive (unique, most recent epoch) including ones that has correspondence in cafe_inventory
			 * with sns_archive.epoch > cafe_inventory.epoch
			 * this file is later used to update cafe_archive table
			 */
			String filename = doc_type + "_cafe_inventory_" + epoch + "_" + archive_date + "_" + archive_date_end + ".out";
			try {
				out = new PrintWriter(new FileWriter(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Output Filename "+filename);

		} 
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		catch(AmazonServiceException ase)
		{
			System.out.println("Caught an AmazonServiceException, which " +"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch(AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
/* 04/17/2018
 * @Description: reduce cafe downloading files by accumulating archive_dates instead of daiy bases download
 * reason is cafe keep sending very frequent updates for same records i.e. send update for 
 * record 123 today, then another one tomorrow and again in 2 days so we download and process the 
 * record 3 times, if we accumulate these 3 times and download the file only once
 * and so process the record only once. since cafe s3 bucket will be having latest version anyway
*/
	public void getArchiveDates_List()
	{
		
		SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yy");
		SimpleDateFormat endDateFormat = new SimpleDateFormat("MMM-dd");
		
		try
		{
			
			//Start Date
			Date start_date = format.parse(archive_date);
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(start_date);
			
			
			// END date
			final Calendar calEnd = Calendar.getInstance();
			calEnd.add(Calendar.DATE, -1);
			
			
			while(calStart.before(calEnd))
			{
				if(archiveDateList.length() >0)
				{
					archiveDateList.append(",");
				}
				archiveDateList.append("'"+ format.format(calStart.getTime()).toUpperCase() + "'");
				calStart.add(Calendar.DATE, 1);  //increment cal by one day
			}
			
			archive_date_end = endDateFormat.format(calEnd.getTime()).toUpperCase();
			System.out.println("ENd date: " + archive_date_end);
			

		}
		catch(ParseException ex)
		{
			System.out.println("Invalid date format, re-try with format MON-DD-YY");
			System.exit(1);
		}
		catch(Exception ex)
		{
			System.out.println("something else went wrong for archive_dates accumulation!!!!");
			ex.printStackTrace();
		}
	}
	
	
	public void getCafeInv_FilesInfo() throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;


		try
		{
			stmt = con.createStatement();

			String cafeInventory_list;
			
			
			//HH added 04/17/2018 for accumulating archive_dates to reduce # of files to download from cafe s3 bucket
			if (isArchive_date_range)
				
				cafeInventory_list = "select key,epoch from CAFE_INVENTORY where doc_type='" + doc_type + "' and key in "+
						"(select distinct key from sns_archive where doc_type='" + doc_type + "' and TO_CHAR(archive_date, 'MON-DD-RR') in (" + archiveDateList + "))";

			else
				cafeInventory_list = "select key,epoch from CAFE_INVENTORY where doc_type='" + doc_type + "' and key in "+
					"(select distinct key from sns_archive where doc_type='" + doc_type + "' and TO_CHAR(archive_date, 'MON-DD-RR')='" + archive_date + "')";

			rs = stmt.executeQuery(cafeInventory_list);

			process_CafeInventory(rs);
			//downloadFiles(rs);   //used for initial refeed download/process



		}
		finally
		{

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	public void getSnsArch_FilesInfo() throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = con.createStatement();
			String snsArchive_UniqueRecentEpoch_List;
			
			if(isArchive_date_range)
			
			snsArchive_UniqueRecentEpoch_List = "SELECT t.KEY,t.EPOCH,t.PUI,t.ACTION,t.BUCKET,t.DOC_TYPE,TO_CHAR(t.ARCHIVE_DATE ,'YYYY-MM-DD HH24:MI:SS') ARCHIVE_DATE FROM("+
					"SELECT key,max(to_number(epoch)) AS epoch "+
					"FROM sns_archive where doc_type='" + doc_type + "' and TO_CHAR(archive_date, 'MON-DD-RR') in (" + archiveDateList + ") "+
					"GROUP BY key) x "+
					"JOIN sns_archive t ON x.key =t.key "+
					"AND x.epoch = t.epoch "+
					"where doc_type='" + doc_type + "' and TO_CHAR(archive_date, 'MON-DD-RR') in (" + archiveDateList + ") ";
			//"order by x.key"; commented out for performance 05/08/2017

			else
				snsArchive_UniqueRecentEpoch_List = "SELECT t.KEY,t.EPOCH,t.PUI,t.ACTION,t.BUCKET,t.DOC_TYPE,TO_CHAR(t.ARCHIVE_DATE ,'YYYY-MM-DD HH24:MI:SS') ARCHIVE_DATE FROM("+
						"SELECT key,max(to_number(epoch)) AS epoch "+
						"FROM sns_archive where doc_type='" + doc_type + "' and TO_CHAR(archive_date, 'MON-DD-RR')='" + archive_date + "' "+
						"GROUP BY key) x "+
						"JOIN sns_archive t ON x.key =t.key "+
						"AND x.epoch = t.epoch "+
						"where doc_type='" + doc_type + "' and TO_CHAR(archive_date, 'MON-DD-RR')='" + archive_date + "' ";
			

			System.out.println("execute query: " + snsArchive_UniqueRecentEpoch_List);
			rs = stmt.executeQuery(snsArchive_UniqueRecentEpoch_List);

			getSnsArchiveListToDownload(rs);

			//downloadFiles(rs);   //used for initial refeed download/process



		}
		finally
		{

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}

	}



	private void end()
	{
		if(out !=null)
		{
			try
			{
				out.flush();
				out.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

	}

	private void flush()
	{
		if(con != null)
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


	public void process_CafeInventory(ResultSet rs)
	{
		String key = null;
		long epoch = 0;

		try 
		{
			while (rs.next())
			{
				if(rs.getString("KEY") !=null)
				{
					key = rs.getString("KEY");
				}
				if(rs.getString("EPOCH") !=null)
				{
					epoch = Long.parseLong(rs.getString("EPOCH"));
				}


				//get the s3file content & download as key.xml 
				if(key !=null && epoch !=0)
				{
					cafeInventory_List.put(key, epoch);

				}

			}

			System.out.println("Total Cafe_inventory matching keys to snsArchive: " + cafeInventory_List.size());
			System.out.println("*********************");

		} 

		// for resultSet
		catch (SQLException e) 
		{
			System.out.println("Error Occurred reading from Cafe_Inventory ResultSet: " + e.getMessage());
			e.printStackTrace();
		}

	}
	public void getSnsArchiveListToDownload (ResultSet rs2) throws InterruptedException
	{
		String key = null;
		String pui = null;
		long epoch = 0;
		String bucket = null;

		StringBuffer strBuf = null;

		boolean isRecent_epoch = false;
		String errorCode = null;   // only write keys that were successfully downloaded from s3 bucket (exist in s3 bucket)

		StringBuffer keys = new StringBuffer();

		LinkedHashMap<String, String> keyBucketPair;
		try 
		{
			while (rs2.next())
			{
				strBuf = new StringBuffer();
				keyBucketPair = new LinkedHashMap<String,String>();
				
				if(rs2.getString("KEY") !=null)
				{
					key = rs2.getString("KEY");
					strBuf.append(key);
				}
				strBuf.append(FIELDDELIM);


				if(rs2.getString("EPOCH") !=null)
				{
					strBuf.append(rs2.getString("EPOCH"));

					epoch = Long.parseLong(rs2.getString("EPOCH"));
				}
				strBuf.append(FIELDDELIM);


				if(rs2.getString("PUI") !=null)
				{
					strBuf.append(rs2.getString("PUI"));
					pui = rs2.getString("PUI");
				}
				strBuf.append(FIELDDELIM);


				if(rs2.getString("action") !=null)
				{
					strBuf.append(rs2.getString("action"));
				}
				strBuf.append(FIELDDELIM);

				if(rs2.getString("BUCKET") !=null)
				{
					bucket = rs2.getString("BUCKET");
					strBuf.append(bucket);
				}
				strBuf.append(FIELDDELIM);

				if(rs2.getString("DOC_TYPE") !=null)
				{
					strBuf.append(rs2.getString("DOC_TYPE"));
				}
				strBuf.append(FIELDDELIM);

				//if(rs2.getString("ARCHIVE_DATE") !=null)
				if(rs2.getTimestamp("ARCHIVE_DATE") !=null)
				{
					try
					{
						strBuf.append(archiveDateFormat.format(archiveDateFormat.parse(rs2.getString("ARCHIVE_DATE"))));
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
				strBuf.append(FIELDDELIM);


				//get the s3file content & download as key.xml 
				if(key !=null && epoch >0)
				{
					if(cafeInventory_List.containsKey(key))
					{
						isRecent_epoch = CheckMsgObjectEpoch (epoch,cafeInventory_List.get(key));

						if(isRecent_epoch)
						{
							System.out.println("Get file... " +  bucket+"/"+key + " for action: " +  rs2.getString("action"));

							//Only download Keys from S3 having action "a/u", exluding deletion
							if(rs2.getString("action").equalsIgnoreCase("d"))
							{
								keys_to_be_deleted.put(key, pui);
							}
							//05/17/2017 moved to multithreading class
							else
							{
								//errorCode = objectFromS3.getFile(bucket, key);
								
								/*
								 * originally added 05/17/2017 to download cafe files from s3 bucket later in multi-threads instead 
								 * modified 06/07/2017 by placing it hear instead of outside epoch check so now count in out file should match physically downloaded files 
								 */
								keyBucketPair.put(key,bucket);
								keys_to_be_downloaded.add(keyBucketPair);
							}

							//write Keys info for later load to cafe_inventory 
							//if(errorCode == null)
							//{
							// write the message to out file
							out.println(strBuf.toString().trim());
							//}	
						}
						else
						{
							System.out.println(key + " sns.epoch < inventory.epoch, skip the key");
						}
					}
					else
					{
						System.out.println(key + ": is a new Key");
						System.out.println("Get file... " +  bucket+"/"+key + " for action: " +  rs2.getString("action"));

						//Only download Keys from S3 having action "a/u", exluding deletion
						if(rs2.getString("action").equalsIgnoreCase("d"))
						{
							keys_to_be_deleted.put(key,pui);
						}
						
						//05/17/2017 moved to multithreading class
						else
						{
							//errorCode = objectFromS3.getFile(bucket, key);
							
							/*
							 * originally added 05/17/2017 to download cafe files from s3 bucket later in multi-threads instead 
							 * modified 06/07/2017 by placing it hear instead of outside epoch check so now count in out file should match physically downloaded files 
							 */ 
							keyBucketPair.put(key,bucket);
							keys_to_be_downloaded.add(keyBucketPair);
						}

						//write Keys info for later load to cafe_inventory 
						//if(errorCode == null)
						//{
						// write the message to out file
						out.println(strBuf.toString().trim());
						//}	

					}

				}	
			}

			for(String aniKey: keys_to_be_deleted.keySet())
			{
				if(keys.length() >0)
					keys.append(",");
				keys.append(aniKey);
			}
			System.out.println("Total Keys of action 'd' [" + keys + " ]");

		} 

		// for resultSet
		catch (SQLException e) 
		{
			System.out.println("Error Occurred reading from ResultSet: " + e.getMessage());
			e.printStackTrace();
		}

	}
	public void downloadFiles(ResultSet rs) throws InterruptedException
	{
		String bucket = null;
		String key = null;
		String action = null;   //SQS Msg Action

		try 
		{
			while (rs.next())
			{
				if(rs.getString("BUCKET") !=null)
				{
					bucket = rs.getString("BUCKET");
				}
				if(rs.getString("KEY") !=null)
				{
					key = rs.getString("KEY");
				}
				if(rs.getString("action") !=null)
				{
					action = rs.getString("action");
				}


				//get the s3file content & download as key.xml 
				if(bucket !=null && key !=null)
				{
					System.out.println("Get file... " +  bucket+"/"+key + " for action: " +  action);
					//objectFromS3.getFile(bucket, key);  // 05/19/2017 temp comment out because i moved objectFromS3 to multithreading class. when needed return it back
				}

			}


		} 

		// for resultSet
		catch (SQLException e) 
		{
			System.out.println("Error Occurred reading from ResultSet: " + e.getMessage());
			e.printStackTrace();
		}

		catch(AmazonServiceException ase)
		{
			System.out.println("Caught an AmazonServiceException, which " +"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch(AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}


	}


	//05/24/2016 parse archive_date to specify which range of messages to process from sns_archive
	static boolean isValidDate(String input) 
	{

		SimpleDateFormat format = new SimpleDateFormat("MMM-dd-yy");
		try {
			format.parse(input);
			return true;
		}
		catch(ParseException e){
			return false;
		}
	}


	private boolean CheckMsgObjectEpoch(long msgEpoch, long invEpoch)
	{
		boolean updatable = false;

		//if the action is “update”, the user only processes the message if “epoch” in the incoming message is later than “epoch” from the object’s UserMetadata (cafe_inventory)

		if (msgEpoch > invEpoch) 
		{
			updatable = true;
		}	

		return updatable;
	}


	// 05/25/2016: Zip Cafe keys downloaded from S3 bucket for later passing to converting program
	public void zipDownloads() throws Exception
	{
		currDir = System.getProperty("user.dir");
		File zipsDir = new File(currDir+"/zips");
		if(!(zipsDir.exists()))
		{
			zipsDir.mkdir();
		}


		File downDir = new File(currDir + "/"+this.S3dir.getName());

		String[] xmlFiles = downDir.list();
		File[] xmlFilesToDelete = downDir.listFiles();
		byte[] buf = new byte[1024];


		System.out.println("zip starting....");
		// create zip files if any files were downloaded, otherwise no zip file should be created
		if(xmlFiles.length >0)
		{
			String zipFileName = zipsDir + "/" + this.S3dir.getName() + "_" + this.zipFileID + ".zip";
			ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileName));

			for(int i=0; i<xmlFiles.length; i++)
			{
				// limit each single zip file to hold recsPerZipfile, otherwise split to multiple zip files
				if(curRecNum >= recsPerZipFile)
				{
					curRecNum = 0;
					outZip.close();

					this.zipFileID++;
					zipFileName = zipsDir + "/" + this.S3dir.getName() + "_" + this.zipFileID + ".zip";
					outZip = new ZipOutputStream(new FileOutputStream(zipFileName));	
				}
				FileInputStream in = new FileInputStream(downDir + "/" + xmlFiles[i]);
				outZip.putNextEntry(new ZipEntry(xmlFiles[i]));

				int length;
				while((length = in.read(buf)) >0)
				{
					outZip.write(buf,0,length);
				}
				outZip.closeEntry();
				in.close();
				xmlFilesToDelete[i].delete();  // delete original xml file to save space

				++curRecNum;
			}
			outZip.close();
			downDir.delete();
		}
	}




	public void updateCafeInventory()
	{
		// update cafe_invetory table by replacing matching keys, with their most recent epoch, or add new keys info
		String dataFile = doc_type + "_cafe_inventory_" + epoch + "_" + archive_date + "_" + archive_date_end + ".out";
		CallableStatement stmt = null;

		try
		{
			File cafeInventoryOutFile = new File(dataFile);
			if(!(cafeInventoryOutFile.exists()))
			{
				System.out.println("Cafe Inventory Output File: " + dataFile + " not exist");
			}
			else
			{
				/**********delete all data from temp table *************/

				System.out.println("about to truncate table "+tableToBeTruncated);
				cleanUp();

				/************** load data into temp table ****************/
				System.out.println("about to load data file "+dataFile);

				Runtime r = Runtime.getRuntime();
				Process p = r.exec("./"+ sqlldrFileName + " " + dataFile);
				int t = p.waitFor();

				//the value 0 indicates normal termination.
				System.out.println("Sqlldr process complete with exit status: " + t);

				int tempTableCount = getTempTableCount(tableToBeTruncated);
				System.out.println(tempTableCount+" records was loaded into the temp table");

				if(tempTableCount >0)
				{
					System.out.println("begin to execute stored procedure update_cafe_inventory_master_table");

					stmt = con.prepareCall("{ call update_inventory_master_table(?)}");
					stmt.setString(1,doc_type);
					stmt.executeUpdate();

				}
				else
				{
					System.out.println("no record was loaded into the temp table");
				}


			}

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
		}

	}

	private void cleanUp()
	{
		Statement stmt = null;

		try
		{
			stmt = con.createStatement();
			stmt.executeUpdate("truncate table " + tableToBeTruncated);
			System.out.println("truncate temp table " + tableToBeTruncated);
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
		}
	}


	private int getTempTableCount(String tempTable)
	{
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		try
		{
			stmt = con.createStatement();

			rs = stmt.executeQuery("select count(*) count from " + tempTable + "");
			if(rs.next())
			{
				count = rs.getInt("count");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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
			if(stmt !=null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return count;

	}

	// get the list of M_ID to be deleted from ES & DB based on SQS "Key" field from cafe_inventory.
	/*
	 * as per team discussion whenever receive AU/AF profile deletion, simply delet it from ES & from database.
	 */

	public boolean PrepareDeletion()
	{
		StringBuffer keys = new StringBuffer();
		String profileTable = "";
		String backupTable = "";
		String columnName = "";
		int curRec = 0; 
		int status = 0;

		
		System.out.println("Total Keys to be deleted: " + keys_to_be_deleted.size());

		if(doc_type.equalsIgnoreCase("ipr"))
		{
			profileTable = "institute_profile";
			backupTable = "institute_profile_deleted";
			columnName = "AFFID";

		}

		else if(doc_type.equalsIgnoreCase("apr"))
		{
			profileTable = "author_profile";
			backupTable = "author_profile_deleted";
			columnName = "AUTHORID";

		}

		else
		{
			System.out.println("Invalide Doc_type");
			return false;
		}

		try
		{
			con = getConnection(url,driver,username,password);
			AusAffESIndex esIndexObj = new AusAffESIndex(recsPerEsbulk, esDomain, "delete", esIndexName);
			
			for(String key: keys_to_be_deleted.keySet())
			{
				if(curRec>999)
				{
					//get M_ID list from DB for ES deletion & later delete from DB
					getMidToBeDeleted(keys.toString(), profileTable, columnName, doc_type);

					if(keys_MID_to_be_deleted.size() >0)
					{
						//delete from ES
						//AuAfESIndex esIndexObj = new AuAfESIndex(doc_type);
						//AusAffESIndex esIndexObj = new AusAffESIndex(recsPerEsbulk, esDomain, "delete"); // moved to outside for loop
						status = esIndexObj.createBulkDelete(doc_type, keys_MID_to_be_deleted);

						//delete from DB
						if(status!=0 && (status == 200 || status == 201 || status == 404))
						{
							DbBulkDelete(profileTable,backupTable);  // temp comment during testing, NEED TO UNCOMMENT WHEN MOVE TO PROD
						}
						else
						{
							System.out.println("Error Occurred during ES Deletion, so no DB deletion");
						}

					}

					curRec = 0;
					keys = new StringBuffer();
					keys_MID_to_be_deleted.clear();
				}

				if(keys.length() >0)
					keys.append(",");
				keys.append("'" + key + "'");
				curRec++;
			}

			//get M_ID list from DB for ES deletion & later delete from DB
			getMidToBeDeleted(keys.toString(), profileTable, columnName, doc_type);

			if(keys_MID_to_be_deleted.size() >0)
			{
				//delete from ES
				//AuAfESIndex esIndexObj = new AuAfESIndex(doc_type);
				//AusAffESIndex esIndexObj = new AusAffESIndex(recsPerEsbulk, esDomain, "delete");  // moved to outside for loop
				status = esIndexObj.createBulkDelete(doc_type, keys_MID_to_be_deleted);
				esIndexObj.end();


				//delete from DB
				if(status!=0 && (status == 200 || status == 201 || status == 404))
				{
					DbBulkDelete(profileTable,backupTable);  // temp comment during testing, NEED TO UNCOMMENT WHEN MOVE TO PROD
				}
				else
				{
					System.out.println("Error Occurred during ES Deletion, so no DB deletion");
				}

			}


		}

		catch(SQLException ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return true;

	}
	public boolean getMidToBeDeleted(String keys, String tableName, String columnName, String document_type)
	{
		Statement stmt = null;
		ResultSet rs = null;

		String query = "";

		try
		{
			if(document_type !=null && (document_type.equalsIgnoreCase("apr") || document_type.equalsIgnoreCase("ipr")))
			{
				query = "select M_ID from " + tableName + " where  " + columnName + " in ("+ keys + ")";
			}

			else if(document_type !=null && document_type.equalsIgnoreCase("ani"))
			{
				query = "select M_ID from " + tableName + " where  substr(" + columnName + ",INSTR(" + columnName + ",'-', 1, 2)+1,length(" + columnName + ")) in ("+ keys + ")";
			}
			else
			{
				System.out.println("Invalid doc type!!!");
				System.exit(1);
			}

			System.out.println("Running query...." + query);

			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			while(rs.next())
			{
				if(rs.getString("M_ID") !=null)
				{
					keys_MID_to_be_deleted.add(rs.getString("M_ID"));
				}
			}

			System.out.println("Only :" +  keys_MID_to_be_deleted.size() + " out of " + keys_to_be_deleted.size() + " exist in DB");


		}
		catch(SQLException ex)
		{
			System.out.println("Error to get M_ID list for the Keys to be deleted: " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return true;

	}

	public void DbBulkBackup(String tableName,String backupTable,String keys) throws IOException
	{ 
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";

		try
		{
			//HH 05/03/2017 backup records to backup table before deletion for later QA, then later on need to comment it out to save space
			//1. backup records
			query = "insert into " + backupTable + " select * from " + tableName + " where M_ID in ("+ keys.toString() + ")";

			System.out.println("Running query...." + query);

			con.setAutoCommit(false);
			stmt = con.createStatement();
			int count = stmt.executeUpdate(query);
			con.commit();
			System.out.println("Total Keys backed up before deletion from: " + tableName + " :- " + count);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}



	}
	public void DbBulkDelete(String tableName, String backupTable) throws IOException
	{	
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";

		StringBuffer keys = new StringBuffer();

		try
		{
			for(String key : keys_MID_to_be_deleted)
			{
				if(keys.length() >0)
					keys.append(",");

				keys.append("'" + key +"'");
			}

			//1. backup records
			DbBulkBackup(tableName,backupTable,keys.toString());


			//2. delete records from DB
			query = "delete from " + tableName + " where M_ID in ("+ keys.toString() + ")";

			System.out.println("Running query...." + query);

			con.setAutoCommit(false);
			stmt = con.createStatement();
			int count = stmt.executeUpdate(query);
			con.commit();


			System.out.println("Total Keys Deleted from: " + tableName + " :- " + count);

		}
		catch(SQLException ex)
		{
			System.out.println("Error to delete M_ID list from DB table: " + tableName + " error message: " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}


	}

	/***
 	added 11/22/2016 for deleting CAFE ANI records directly from Fast & DB using "EID" 
	instead of downloading files & process them as deletion (as we do now for BD deletion)

	 * cafe ani deletion procedures: added 05/02/2017
	 * - load pui and key to CAFE_weekly_DELETION table
	 * - backup records from cafe_master into cafe_deleted (similar to BD)
	 * -  modify "status" in lookup tables (AU/AF) to "deleted" for later ES updated during freezing window

	 */
	private void deleteANIData()
	{
		PrintWriter ani_del_out = null;
		String fileName = doc_type + "_deletion_" + epoch + "_" + archive_date + "_" + archive_date_end + ".out";

		try {

			if(doc_type !=null && doc_type.equalsIgnoreCase("ani"))
			{
				ani_del_out = new PrintWriter(new FileWriter(fileName));
				for(String key: keys_to_be_deleted.keySet())
				{
					ani_del_out.println(key + FIELDDELIM + keys_to_be_deleted.get(key));
				} 


				/************** load data into temp table ****************/
				System.out.println("about to load data file "+fileName);

				Runtime r = Runtime.getRuntime();
				Process p = r.exec("./"+ deletionSqlldrFileName + " " + fileName);
				int t = p.waitFor();

				//the value 0 indicates normal termination.
				System.out.println("ani deletion Sqlldr process complete with exit status: " + t);

				int tempTableCount = getTempTableCount(ani_DeletionTempTable);
				System.out.println(tempTableCount+" records was loaded into the " + ani_DeletionTempTable + "  table");


				/* 
				 * backup records, delete from cafe master and update lookup tables's status column to "deleted"
				 * this will be done in a separte process during the freezing window based on "CAFE_weekly_DELETION" table
				 */

			}
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("cafe ani deletion file error message " + ex.getMessage());
			ex.printStackTrace();
		}
		catch (IOException e) 
		{
			System.out.println("Error occurred to load ANI deletion records to temp table!!!");
			System.out.println("Cause Reason: " + e.getMessage());
			e.printStackTrace();
		}

		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(ani_del_out !=null)
				{
					ani_del_out.flush();
					ani_del_out.close();

				}
			}
			catch(Exception e)
			{
				System.out.println("Failed to close " + doc_type + " deletion file");
				e.printStackTrace();
			}
		}
	}

	// create fast deletion file "delete.txt"

	private void creatDeleteFile()
	{
		String batchID = "0001";

		File file=new File("fast");
		FileWriter out= null;

		try
		{
			if(!file.exists())
			{
				file.mkdir();
			}

			String batchPath = "fast/batch_" + updateNumber+"_"+batchID;

			file=new File(batchPath);
			if(!file.exists())
			{
				file.mkdir();
			}
			String root = batchPath +"/EIDATA/tmp";
			file=new File(root);

			if(!file.exists())
			{
				file.mkdir();
			}

			file = new File(root+"/delete.txt");

			if(!file.exists())
			{
				file.createNewFile();
			}
			out = new FileWriter(file);

			for(String m_id: keys_MID_to_be_deleted)
			{
				if(m_id != null)
				{
					out.write(m_id+"\n");
				}
			}
			out.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(out !=null)
			{
				try
				{
					out.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}

	}


	protected Connection getConnection(String connectionURL,
			String driver,
			String username,
			String password)
					throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,
				username,
				password);
		return con;
	}



	//added 05/17/2017 to download cafe s3 files using multithreads
	public class s3FileDownload extends Thread  
	{
		private Thread th;
		private String threadName = null;
		private CountDownLatch latch;
		
		int startIndex;
		int lastIndex;

		GetANIFileFromCafeS3Bucket objectFromS3;
		
		
		public s3FileDownload (String name, CountDownLatch latch, int start, int last, GetANIFileFromCafeS3Bucket obj)
		{
			threadName = name;
			this.latch = latch;
			
			
			startIndex = start;
			lastIndex = last;

			objectFromS3 = obj;
			
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
					th = new Thread(this, threadName);
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
			String errorCode = null;
			
			System.out.println("Running Thread: " + threadName);
			
			System.out.println("starting download files for " + threadName + ": start: " + startIndex + " last: " + lastIndex);
			
						
			try 
			{
				
				for(int j=startIndex; j<=lastIndex;j++)
				{
					for(String key : keys_to_be_downloaded.get(j).keySet())
						errorCode = objectFromS3.getFile(keys_to_be_downloaded.get(j).get(key),key);
				}
				
				System.out.println("finished download files for " + threadName );
				
				latch.countDown();
			}

			catch(AmazonServiceException ase)
			{
				System.out.println("Caught an AmazonServiceException, which " +"means your request made it " +
						"to Amazon S3, but was rejected with an error response" +
						" for some reason.");
				System.out.println("Error Message:    " + ase.getMessage());
				System.out.println("HTTP Status Code: " + ase.getStatusCode());
				System.out.println("AWS Error Code:   " + ase.getErrorCode());
				System.out.println("Error Type:       " + ase.getErrorType());
				System.out.println("Request ID:       " + ase.getRequestId());
			}
			catch(AmazonClientException ace)
			{
				System.out.println("Caught an AmazonClientException, which " +
						"means the client encountered " +
						"an internal error while trying to " +
						"communicate with S3, " +
						"such as not being able to access the network.");
				System.out.println("Error Message: " + ace.getMessage());
			}
			catch (InterruptedException e) 
			{
				System.out.println("caught an InterruptedException for cafe download file from s3 bucket!!!");
				System.out.println("Reason: " + e.getMessage()); 
				e.printStackTrace();
			}

			catch (Exception e) 
			{
				System.out.println("Exception for thread: " + threadName + " !!!");
				System.out.println("Reason: " + e.getMessage()); 
				e.printStackTrace();
			}

		}
	} 

}
