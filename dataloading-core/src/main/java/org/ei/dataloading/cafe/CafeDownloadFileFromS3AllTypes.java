package org.ei.dataloading.cafe;

import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Delete;
import io.searchbox.core.BulkResult.BulkResultItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

public class CafeDownloadFileFromS3AllTypes {

	private static Connection con = null;


	static String url="jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver="oracle.jdbc.driver.OracleDriver";
	static String username="ap_correction1";
	static String password="ei3it";
	static String database ="cpx";
	//static String action;
	static String doc_type;
	static String archive_date;
	static int recsPerZipFile;
	static String tableToBeTruncated = "cafe_inventory_temp";
	static String sqlldrFileName = "cafeInventoryFileLoader.sh";

	int curRecNum = 0;
	int zipFileID = 1;

	File S3dir;    // to hold downloaded bulk of s3 keys/files
	String currDir;   // to hold current working dir to use fro zip downloaded cafe keys

	private GetANIFileFromCafeS3Bucket objectFromS3;
	private AmazonS3 s3Client;

	private long epoch;

	// for parsing archive_date
	SimpleDateFormat archiveDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//05/24/2016 hold cafe_inventory key/epoch List
	Map <String,Long> cafeInventory_List = new HashMap<String,Long>();
	private PrintWriter out;

	//08/01/2016 combine Keys List for action "d" for direct deletion from ES & DB without dowbload from S3 bucket
	private List<String> keys_to_be_deleted = new ArrayList<String>();
	private List<String> keys_MID_to_be_deleted = new ArrayList<String>();

	public static final char FIELDDELIM = '\t';


	public static void main(String[] args)
	{

		if(args.length >5)
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

		if(args.length >6)
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
		}


		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}


		CafeDownloadFileFromS3AllTypes downloadFile = new CafeDownloadFileFromS3AllTypes();

		try {
			downloadFile.init();
			downloadFile.getCafeInv_FilesInfo();
			downloadFile.getSnsArch_FilesInfo();
			downloadFile.end();


			// zip downloaded cafe keys/files
			downloadFile.zipDownloads();

			// update cafe_inventory table
			downloadFile.updateCafeInventory();  //comment for localhost only, uncomment in prod

			// close connection
			downloadFile.flush();

			//get the list of keys to be deleted & delete from ES & DB
			if(downloadFile.keys_to_be_deleted.size() >0)
			{
				downloadFile.PrepareDeletion();
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

			// get time in epoch formate to be able to distingush which file to send to converting, in case there are multiple files to convert
			DateFormat dateFormat = new SimpleDateFormat("E, MM/dd/yyyy-hh:mm:ss a");
			Date date = dateFormat.parse(dateFormat.format(new Date()));
			epoch = date.getTime();

			//05/02/2016 create dir to download s3 files into xml for later zip and convert

			S3dir=new File(doc_type+"_s3Files_"+epoch+"_"+archive_date);
			if(!S3dir.exists())
			{
				S3dir.mkdir();
			}

			// for parsing ANI/Abstract CPX File
			objectFromS3 = new GetANIFileFromCafeS3Bucket(s3Client,database,url,driver,username,password,S3dir);

			/* for writing newly archived sqs message info from sns_archive (unique, most recent epoch) including ones that has correspondence in cafe_inventory
			 * with sns_archive.epoch > cafe_inventory.epoch
			 * this file is later used to update cafe_archive table
			 */
			String filename = doc_type + "_cafe_inventory_" + epoch + "_" + archive_date + ".out";
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
	public void getCafeInv_FilesInfo() throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;


		try
		{
			stmt = con.createStatement();

			String cafeInventory_list = "select key,epoch from CAFE_INVENTORY where key in "+
					"(select key from sns_archive where doc_type='" + doc_type + "' and TO_CHAR(archive_date, 'MON-DD-RR')='" + archive_date + "') and doc_type='" + doc_type + "'";

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
			String snsArchive_UniqueRecentEpoch_List = "SELECT t.KEY,t.EPOCH,t.PUI,t.ACTION,t.BUCKET,t.DOC_TYPE,TO_CHAR(t.ARCHIVE_DATE ,'YYYY-MM-DD HH24:MI:SS') ARCHIVE_DATE FROM("+
					"SELECT key,max(to_number(epoch)) AS epoch "+
					"FROM sns_archive "+
					"GROUP BY key) x "+
					"JOIN sns_archive t ON x.key =t.key "+
					"AND x.epoch = t.epoch "+
					"where doc_type='" + doc_type + "' and TO_CHAR(archive_date, 'MON-DD-RR')='" + archive_date + "' "+
					"order by x.key";


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
		long epoch = 0;
		String bucket = null;

		StringBuffer strBuf = null;

		boolean isRecent_epoch = false;
		String errorCode = null;   // only write keys that were successfully downloaded from s3 bucket (exist in s3 bucket)

		try 
		{
			while (rs2.next())
			{
				strBuf = new StringBuffer();

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
								keys_to_be_deleted.add(key);
							}
							else
							{
								errorCode = objectFromS3.getFile(bucket, key);
							}

							//write Keys info for later load to cafe_inventory 
							//if(errorCode == null)
							//{
							// write the message to out file
							out.println(strBuf.toString().trim());
							//}	
						}
					}
					else
					{
						System.out.println(key + ": is a new Key");
						System.out.println("Get file... " +  bucket+"/"+key + " for action: " +  rs2.getString("action"));

						//Only download Keys from S3 having action "a/u", exluding deletion
						if(rs2.getString("action").equalsIgnoreCase("d"))
						{
							keys_to_be_deleted.add(key);
						}
						else
						{
							errorCode = objectFromS3.getFile(bucket, key);
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
			
			System.out.println("Total Keys of action 'd'" + keys_to_be_deleted);
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
					objectFromS3.getFile(bucket, key);
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
		String dataFile = doc_type + "_cafe_inventory_" + epoch + "_" + archive_date + ".out";
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

				int tempTableCount = getTempTableCount();
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


	private int getTempTableCount()
	{
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		try
		{
			stmt = con.createStatement();

			rs = stmt.executeQuery("select count(*) count from " + tableToBeTruncated + "");
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

	// get the list of M_ID to be deleted from ES & DB based on SQS "Key" field from cafe_inventory

	public boolean PrepareDeletion()
	{
		StringBuffer keys = new StringBuffer();
		String profileTable = "";
		String columnName = "";
		int curRec = 0; 

		System.out.println("Total Keys to be deleted: " + keys_to_be_deleted.size());

		if(doc_type.equalsIgnoreCase("ipr"))
		{
			profileTable = "institute_profile";
			columnName = "AFFID";

		}
		else if (doc_type.equalsIgnoreCase("apr"))
		{
			profileTable = "author_profile";
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
			for(int i=0;i<keys_to_be_deleted.size();i++)
			{
				if(curRec>999)
				{
						//get M_ID list from DB for ES deletion & later delete from DB
						getMidToBeDeleted(keys.toString(), profileTable, columnName);

						if(keys_MID_to_be_deleted.size() >0)
						{
							//delete from ES
							AuAfESIndex esIndexObj = new AuAfESIndex(doc_type);
							esIndexObj.EsBulkDelete(keys_MID_to_be_deleted);

							//delete from DB
							DbBulkDelete(profileTable);  // temp comment during testing, NEED TO UNCOMMENT WHEN MOVE TO PROD
						}

					curRec = 0;
					keys = new StringBuffer();
					keys_MID_to_be_deleted.clear();
				}
				
				if(keys.length() >0)
					keys.append(",");
				keys.append("'" + keys_to_be_deleted.get(i) + "'");
				curRec++;
			}

			//get M_ID list from DB for ES deletion & later delete from DB
			getMidToBeDeleted(keys.toString(), profileTable, columnName);

			if(keys_MID_to_be_deleted.size() >0)
			{
				//delete from ES
				AuAfESIndex esIndexObj = new AuAfESIndex(doc_type);
				esIndexObj.EsBulkDelete(keys_MID_to_be_deleted);

				//delete from DB
				DbBulkDelete(profileTable);  // temp comment during testing, NEED TO UNCOMMENT WHEN MOVE TO PROD
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
	public boolean getMidToBeDeleted(String keys, String tableName, String columnName)
	{
		Statement stmt = null;
		ResultSet rs = null;

		String query = "";

		try
		{
			query = "select M_ID from " + tableName + " where  " + columnName + " in ("+ keys + ")";

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


	public void DbBulkDelete(String tableName) throws IOException
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

}
