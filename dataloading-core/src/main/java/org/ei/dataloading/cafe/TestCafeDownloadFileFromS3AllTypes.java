package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

public class TestCafeDownloadFileFromS3AllTypes {
	
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
					System.out.println("Invalid Date Format, please re-enter as 'DD-MMM-YY'");
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
		}
		

		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}

		
		TestCafeDownloadFileFromS3AllTypes downloadFile = new TestCafeDownloadFileFromS3AllTypes();

		try {
			downloadFile.init();
			downloadFile.getCafeInv_FilesInfo();
			downloadFile.getSnsArch_FilesInfo();
			downloadFile.end();
			
			// zip downloaded cafe keys/files
			downloadFile.zipDownloads();
			
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

			S3dir=new File(doc_type+"_s3Files_"+epoch);
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
				String filename = doc_type + "_cafe_inventory_" + epoch +".out";
				try {
					out = new PrintWriter(new FileWriter(filename));
				} catch (IOException e) {
					e.printStackTrace();
				}

				System.out.println("Output Filename "+filename);

			//
			

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
										"(select key from sns_archive where doc_type='" + doc_type + "' and TO_CHAR(archive_date, 'DD-MON-RR')='" + archive_date + "')";
			
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
													   "where doc_type='" + doc_type + "' and TO_CHAR(archive_date, 'DD-MON-RR')='" + archive_date + "' "+
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

	}


	
	public void end()
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
							errorCode = objectFromS3.getFile(bucket, key);
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
						errorCode = objectFromS3.getFile(bucket, key);
						//write Keys info for later load to cafe_inventory 
						//if(errorCode == null)
						//{
							// write the message to out file
							out.println(strBuf.toString().trim());
						//}	
						
					}
	
				}	
			}
			
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
		
		SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy");
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
