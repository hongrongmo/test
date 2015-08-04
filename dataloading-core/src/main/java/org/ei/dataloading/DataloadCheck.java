package org.ei.dataloading;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import org.apache.oro.text.perl.Perl5Util;
//import org.ei.domain.Database;

/*
 * Author: HT
 * Date: 03/27/2015
 * Desc: get the weekly data loaded INFO (i.e. load#, count, #rejected,#filename,....) for preparing a dataloading report
 */
public class DataloadCheck {

	private static Connection con = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;
	
	static String database;
	static String fileName;
	static int loadNumber = 0; 
	static int updateNumber = 0;
	static String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction";
	static String password = "ei3it";
	static String operation;
	static String tableName;
	static String sqlldrlogFileName;
	static String fastExtractLogFileName;
	
	static Hashtable <String,String> record = null;
	
	private static Perl5Util perl = new Perl5Util();
	
	public static final String[] logTableFields= {"DATABASE","OPERATION","LOADNUMBER","UPDATENUMBER","SOURCEFILENAME","SOURCEFILECOUNT","CONVERTEDFILECOUNT","TEMPTABLECOUNT","SQLLDRLOG","MASTERTABLECOUNT","SRC_MASTER_DIFF","SRC_TEMP_DIFF","FASTEXTRACTFILENAME","FASTEXTRACTCOUNT", "MASTER_FAST_DIFF" , "ACCESSNUMBER" , "ERRORMESSAGE" , "ERRORMESSAGECOUNT" , "DB_EXCEPTION_COUNT"};

	static String fast_count;
	static int src_temp_diff = 0;
	static int src_master_diff = 0;
	static int cpxDbExceptionCount = 0;
	static int grfDbExceptionCount = 0;
	
	static StringBuffer accessnumbers = new StringBuffer();
	static String errorMessage = null;
	
	
	//List of other errors & their count that in log file (i.e. records not converted due to having "null" accessnumber,... (for S300)
	static Map<String,Integer> otherErrors = new HashMap<String, Integer>();
	static String recordsIdentifier = null;
	
	
	
	static StringBuffer sqlldrErrorMessage = new StringBuffer(); 
	static StringBuffer sqlldrErrorMessageCount = new StringBuffer(); 
	
	
	static ArrayList<String> sourceFilenameList = new ArrayList<String>();
	static ArrayList<Integer> sourcefileCountList = new ArrayList<Integer>();
	static ArrayList<Integer> tempTableCountList = new ArrayList<Integer>();
	static ArrayList<Integer> srcTempDiffCountList = new ArrayList<Integer>();
	static ArrayList<Integer> masterTableCountList = new ArrayList<Integer>();
	static ArrayList<Integer> srcMasterDiffCountList = new ArrayList<Integer>();
	static ArrayList<String> fastExtractFileNameList = new ArrayList<String>();
	static ArrayList<Integer> fastExtractCountList = new ArrayList<Integer>();
	static ArrayList<String> sqlldrInfoList = new ArrayList<String>();
	static ArrayList<String> sqlldrFileNameList = new ArrayList<String>();
	static ArrayList<Integer> errorTableCountList = new ArrayList<Integer>();
	static ArrayList<String> sqlErrorMessageList = new ArrayList<String>();
	static ArrayList<String> sqlErrorMessageCountList = new ArrayList<String>();
	static ArrayList<String> otherErrorsAccessnumberPuiList = new ArrayList<>();
	
	static ArrayList<Hashtable<String, String>> records = new ArrayList<Hashtable<String,String>>();
	
	
	public static void main(String[] args) throws Exception{
		
		record = new Hashtable<String,String>();
		
		
			if(args[0]!=null)
			{
				database = args[0];
			}
			
			if(args[1]!=null)
			{
				fileName = args[1];
			}
			
			if(args[2]!=null)
			{
				loadNumber = Integer.parseInt(args[2]);
			}
			
			if(args[3]!=null && args[3].length()>0)
            {
                Pattern pattern = Pattern.compile("^\\d*$");
                Matcher matcher = pattern.matcher(args[3]);
                if (matcher.find())
                {
                    updateNumber = Integer.parseInt(args[3]);
                    
                    System.out.println("Updatenumber is : " + updateNumber);
                    
                }
                else
                {
                    System.out.println("did not find updateNumber or updateNumber has wrong format");
                    System.exit(1);
                }
            }
			
		if (args.length >4)
		{
			if(args[4]!=null)
			{
				connectionURL = args[4];
			}
			
			if(args[5]!=null)
			{
				driver = args[5];
			}
			
			if(args[6]!=null)
			{
				username =  args[6];
			}
			
			if(args[7]!=null)
			{
				password =  args[7];
			}
			
			if(args[8]!=null)
			{
				operation = args[8];
				
				//look for slqldr file only when operation is update/delete
				if (operation !=null && !(operation.equalsIgnoreCase("new")) && !(operation.equalsIgnoreCase("ip")))
				{
					if(args[9]!=null)
					{
						sqlldrlogFileName = args[9];
					}
				}
				
				//look for fast extract log file only when operation is new
				if (operation !=null && operation.equalsIgnoreCase("new"))
				{
					if(args[9]!=null)
					{
						fastExtractLogFileName = args[9];
					}
				}
			}
			
			 
		}
		
		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}
		
		//Temp till conv program generate converted file count, converted file count
		record.put("CONVERTEDFILECOUNT", "");
		
		ReadLogFile();
		
		//printList();
		getRecords();
		//Print Info
		writeRecs();
	}
		

	public static void ReadLogFile() throws Exception{
		String updatenum=null;
		//String Count=null;
		int Count=0;
		int firstmatch = 0;
		String line = null;
		String sqlldrError = null;
		
		try
		{
			File f = new File(fileName);
			if(!f.exists())
			{
				System.out.println("File Not found");
				System.exit(1);
			}
			FileInputStream fis = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);
			
			if(database!=null && database.equalsIgnoreCase("cpx") && (operation!=null && (operation.equalsIgnoreCase("update")|| operation.equalsIgnoreCase("delete"))))
			{
				tableName = "BD_CORRECTION_ERROR";
				int srcFileCount = 0;
				int tempTableCount = 0;
				
				String updNumCount="";
				
				//INFO from Log file
				while (dis.available() !=0)
				{
					line = dis.readLine();

					//Source FileName
					if(line.contains("No errors detected in compressed data of"))
					{
						StringBuffer str = new StringBuffer("No errors detected in compressed data of");
						if(!(sourceFilenameList.contains(line.substring(str.length(),line.length()).trim())))
						{
							sourceFilenameList.add(line.substring(str.length(),line.length()).trim());
						}
						
					}

					//Source File Count
					if(line.contains("Total counts for this file is"))
					{
						srcFileCount = Integer.parseInt(dis.readLine().trim());
						sourcefileCountList.add(srcFileCount);
						
					}

					//TEMP TABLE COUNT
					if(line.contains("records was loaded into the temp table"))
					{ 
						tempTableCount = Integer.parseInt(line.substring(0,line.indexOf("records")).trim());
						tempTableCountList.add(tempTableCount);
						
						// SRC Temp Diff Count 
						
						src_temp_diff = srcFileCount - tempTableCount;
						srcTempDiffCountList.add(src_temp_diff);
						
					}

					//UpdateNumber , and updated/deleted Master Table Count
					
					if(line.contains("new") && line.contains("select updatenumber, count(*) from BD_MASTER_ORIG where updatenumber")
							&& line.contains("group by updatenumber"))
					{
						for(int i=0;i<4;i++)
						{
							updNumCount = dis.readLine();
						}
						
						updNumCount = perl.substitute("s/\t/;/g",updNumCount);
						updatenum = updNumCount.substring(0,updNumCount.indexOf(';')).trim();
						Count = Integer.parseInt(updNumCount.substring(updNumCount.indexOf(';')+1,updNumCount.length()).trim());
						
						//Src_Master_diff count
						
						 /*if more than one file master count is for first file, second master count is count of both files,
						 so to get 2nd file count , subtract second master count - first file count 
						 */
						
						if (masterTableCountList !=null && masterTableCountList.size()>0)
						{
							for(int i=0;i<masterTableCountList.size();i++)
							{
								Count = Count -  masterTableCountList.get(i);
							}
							
						}	
						
						masterTableCountList.add(Count);
						
						//SRC_Master_Diff Count 
						
						src_master_diff = srcFileCount -Count;
						srcMasterDiffCountList.add(src_master_diff);
						
					}


					//Fast Extract File Name, Fast Extract Count
					if(line.contains("fast/batch") && line.contains("is a valid zip"))
					{
						fast_count = line.substring(line.indexOf(":") +1, line.length()).trim();

						fastExtractFileNameList.add(line.substring(0, line.indexOf("is a valid zip file with")).trim());
							
						if(fast_count.contains("records"))
						{
							fastExtractCountList.add(Integer.parseInt(fast_count.substring(0, fast_count.indexOf(" records"))));
						}
						else 
						{
							fastExtractCountList.add(Integer.parseInt(fast_count.trim()));
						}
						
					}
					

					//SQLLDR Log Info From Main Log File (.txt)
					if(line.contains("/corrections/logs") && line.contains("out.log") && !(line.contains("Reference_")))
					{
						sqlldrInfoList.add(line.trim());
					}
				}
				
				//DB EXCEPTION COUNT FROM ERROR TABLE 
				
				cpxDbExceptionCount = getErrorTableCount(tableName);
				errorTableCountList.add(cpxDbExceptionCount);
				
				// Accessnumber(s) rejected or having other issue & error message from bd_correction_error
				//getErrorTableMessage (tableName);
				
				
				//SQLLDR ErrorMessage INFO from Sqlldr Log File
				System.out.println("sqlldrfilename is " +  sqlldrlogFileName);
				
				if(sqlldrlogFileName.contains(";"))
				{
					sqlldrFileNameFormat(sqlldrlogFileName);
				}
				else
				{
					sqlldrFileNameList.add(sqlldrlogFileName);
					getSqlldrErrorMessage(sqlldrlogFileName);
				}
			
			}
			
			
			else if(database!=null && database.equalsIgnoreCase("cpx") && (operation !=null && operation.equalsIgnoreCase("s300")))
			{
				tableName = "BD_AIP_ERROR";
				username = "ba_s300";
				password = "ei7it";
				
				int srcFileCount = 0;
				int tempTableCount = 0;
				
				
				
				while (dis.available() !=0)
				{
					line = dis.readLine();
					
					//Source FileName
					if(line.contains("No errors detected in compressed data of") && !(line.contains("upload_to_fast")))
					{
						StringBuffer str = new StringBuffer("No errors detected in compressed data of");
						if(!(sourceFilenameList.contains(line.substring(str.length(),line.length()).trim())))
						{
							sourceFilenameList.add(line.substring(str.length(),line.length()).trim());
						}
						
						
					}
					
					//Source File Count
					if(line.contains("Total counts of combined file is"))
					{
						srcFileCount = Integer.parseInt(line.substring(line.indexOf(':')+1,line.length()).trim());
						if(!(sourcefileCountList.contains(srcFileCount)))
						{
							sourcefileCountList.add(srcFileCount);
						}
						
					}
					
					//TEMP TABLE COUNT
					if(line.contains("records was loaded into the temp table"))
					{ 
						tempTableCount = Integer.parseInt(line.substring(0,line.indexOf("records")).trim());
						
						if(!(tempTableCountList.contains(tempTableCount)))
						{
							tempTableCountList.add(tempTableCount);
							
							// SRC_TEMP_DIFF
							
							src_temp_diff = srcFileCount - tempTableCount;
							srcTempDiffCountList.add(src_temp_diff);
						}
						
					}
					
					
					
					//Master TABLE COUNT & diff
					if((line.contains("ba_s300@EID> ba_s300@EID> ba_s300@EID>") || line.contains("ba_s300@EIA> ba_s300@EIA> ba_s300@EIA>")) 
							&& (dis.readLine().contains("COUNT(*)")))
					{
						dis.readLine();
						Count = Integer.parseInt(dis.readLine().trim());

						masterTableCountList.add(Count);

						//Src_Master_diff count
						src_master_diff = srcFileCount - Count;
						srcMasterDiffCountList.add(src_master_diff);

					}
					
					//UpdateNumber 
					if(line.contains("updateNumber="))
					{
						updatenum = line.substring(line.indexOf('=')+1, line.indexOf("fileName=")).trim();						
						updateNumber = Integer.parseInt(updatenum);
					}
					
					
					//Fast Extract File Name, Fast Extract Count
					if(line.contains("Checking extract files for correction update number") && firstmatch==0)
					{	
						line = dis.readLine();
						
						if(line.length() >0)
						{
							fast_count = line.substring(line.indexOf(":") +1, line.length()).trim();
							
							fastExtractFileNameList.add(line.substring(0, line.indexOf("is a valid zip file with")).trim());
							fastExtractCountList.add(Integer.parseInt(fast_count.substring(0, fast_count.indexOf(" records"))));
							firstmatch = 1;
						}
						
					}
					
					//sqlldr log file Info
					if(line.contains("Check Export result") )
					{						
						line = dis.readLine();
						
						// if log file name the first one
						if(line !=null && line.contains("log"))
						{
							sqlldrInfoList.add(line.substring(line.indexOf("/data"), line.length()));
						}
						
						else if (line !=null)
						{
							line = dis.readLine();
							
							if(line !=null && line.contains("log"))
							{
								sqlldrInfoList.add(line.substring(line.indexOf("/data"), line.length()));
							}
						}
						// sometimes the log file is the second one not, the 
					}
					
					// Other Errors to append to sqlldr errormessage, count
					
					if(line !=null && line.contains("records with null accessnumber"))
					{
						int count = Integer.parseInt(line.substring(line.indexOf("are")+3, line.indexOf("records")).trim());
						otherErrors.put("records with null accessnumber", count);
						
						// get idenitifier of these records
						line = dis.readLine();
						if(line.length() >0 && line.contains("the PUI for these records are"))
						{
							line = dis.readLine();
							if(line.length() >0)
							{
								recordsIdentifier = line.trim();
							}
							
						}
					}
					
				}
				
				//DB EXCEPTION COUNT
				cpxDbExceptionCount = getErrorTableCount(tableName);
				
				
				errorTableCountList.add(cpxDbExceptionCount);
				
				
				// Accessnumber(s) rejected or having other issue & error message 
				//getErrorTableMessage (tableName);
				if(recordsIdentifier !=null && recordsIdentifier.length() >0)
				{
					otherErrorsAccessnumberPuiList.add(recordsIdentifier);
				}
				
				
				
				
				//Sqlldr ErrorMessage INFO from sqlldr Log file
				sqlldrFileNameList.add(sqlldrlogFileName);
				getSqlldrErrorMessage(sqlldrlogFileName);
			}
			
			
			
			else if(database!=null && (database.equalsIgnoreCase("grf") || database.equalsIgnoreCase("elt")) && 
					(operation !=null && operation.equalsIgnoreCase("update")))
			{
				if(database.equalsIgnoreCase("grf"))
				{
					tableName = "GEOREF_CORRECTION_ERROR";
				}
				else if (database.equalsIgnoreCase("elt"))
				{
					tableName = "BD_CORRECTION_ERROR";
				}
				
				int srcFileCount = 0;
				int tempTableCount = 0;
				
				
				while (dis.available() !=0)
				{
					line = dis.readLine();
					
					//Source FileName
					if(line.contains("Datafile is:"))
					{
						if(!(sourceFilenameList.contains(line.substring(line.indexOf(":")+1,line.indexOf("and")).trim())))
						{
							//record.put("SOURCEFILENAME", line.substring(line.indexOf(":")+1,line.indexOf("and")).trim());
							sourceFilenameList.add(line.substring(line.indexOf(":")+1,line.indexOf("and")).trim());
						}

					}
					
					//Source File Count
					if(line.contains("Total counts of"))
					{
							srcFileCount = Integer.parseInt(line.substring(line.indexOf(':')+1,line.length()).trim());
							sourcefileCountList.add(srcFileCount);
					}
					
					
					//TEMP TABLE COUNT
					if(line.contains("records was loaded into the temp table"))
					{ 
							tempTableCount = Integer.parseInt(line.substring(0,line.indexOf("records")).trim());
							tempTableCountList.add(tempTableCount);
					}
					
					//Fast Extract File Name, Fast Extract Count
					if(line.contains("Checking extract files for correction update number"))
					{	
						if(fastExtractFileNameList.size()==0)
						{
							line = dis.readLine();
							
							if(line.contains("fast/batch") && line.contains("record"))
							{
								fast_count = line.substring(line.indexOf(":") +1, line.length()).trim();
								
								fastExtractFileNameList.add(line.substring(0, line.indexOf("is a valid zip file with")).trim());
								fastExtractCountList.add(Integer.parseInt(fast_count.substring(0, fast_count.indexOf(" record"))));
							}
						}
					}
					
					//sqlldr log file Info
					if(line.contains("Check Export result") )
					{	
						if(sqlldrInfoList.size()==0)
						{
							line = dis.readLine();
							sqlldrInfoList.add(line.substring(line.indexOf("/data"), line.length()));
						}
					}


				}
				
				//SRC_Temp_DIFF
				
				src_temp_diff = srcFileCount - tempTableCount;
				srcTempDiffCountList.add(src_temp_diff);
				
				//Master TABLE COUNT & diff with src file count
				/*HH 04/06/2015 i added a sql stmt to easily get the total GRF_Master count (update/delete) - to be used after WK: 201516
				 * get from DB for now, till data becomes available in log file 
				 */
				if(masterTableCountList.size()==0)
				{
					Count = getMasterTableCount("ck_counts");
					masterTableCountList.add(Count);
					
					//Src_Master_diff count					
						src_master_diff = srcFileCount - Count;
						srcMasterDiffCountList.add(src_master_diff);
				}
				
				//DB EXCEPTION COUNT
				grfDbExceptionCount = getErrorTableCount(tableName);
				errorTableCountList.add(grfDbExceptionCount);
				
				
				// Accessnumber(s) rejected or having other issue & error message 
				//getErrorTableMessage (tableName);
				
				//Sqlldr ErrorMessage INFO from sqlldr Log file
				sqlldrFileNameList.add(sqlldrlogFileName);
				getSqlldrErrorMessage(sqlldrlogFileName);
			}
			
			
			else if ((database!=null) && (database.equalsIgnoreCase("cpx") || database.equalsIgnoreCase("aip") || database.equalsIgnoreCase("chm")
					|| database.equalsIgnoreCase("pch") || database.equalsIgnoreCase("elt") || database.equalsIgnoreCase("geo")
					|| database.equalsIgnoreCase("nti") || database.equalsIgnoreCase("cbn") || database.equalsIgnoreCase("ept")
					|| database.equalsIgnoreCase("grf") || database.equalsIgnoreCase("ins") || database.equalsIgnoreCase("upt")
					) 
					&& (operation !=null && (operation.equalsIgnoreCase("new") || operation.equalsIgnoreCase("ip"))))
			{
				tableName = "BD_MASTER";
				int i = 0;
				//ArrayList<String> errorMessageList = new ArrayList<String>();
				HashMap<String,Integer> errorMessageList = new HashMap<String,Integer>();
				String errorMessage = "";
				int loadedRecordCount = 0;
				int rejectedRecordCount = 0;
				StringBuffer sqlErrorMessage=new StringBuffer();
				
				
				
				System.out.println("Operation is " +  operation);
				while (dis.available() !=0)
				{
					line = dis.readLine();
					
					//Source FileName & Sqlldr file name
					
					if(line.contains("Data File:"))
					{
							if(database.equalsIgnoreCase("cbn"))
							{
								//record.put("SOURCEFILENAME", line.substring(line.indexOf("/")+1,line.indexOf("txt")+3).trim());
								
								if(line.contains(".txt"))
								{
									sourceFilenameList.add(line.substring(line.indexOf("/")+1,line.indexOf("txt")+3).trim());
								}
								else if (line.contains("zip"))
								{
									sourceFilenameList.add(line.substring(line.indexOf("/")+1,line.indexOf("zip")+3).trim());
								}
							}
							else if (database.equalsIgnoreCase("ept"))
							{
								//record.put("SOURCEFILENAME", line.substring(line.indexOf("/")+1,line.indexOf("pat")+3).trim());
								if(!(line.contains("/")))
								{
									sourceFilenameList.add(line.substring(line.indexOf(":")+1,line.indexOf("pat")+3).trim());
								}
								else
								{
									sourceFilenameList.add(line.substring(line.indexOf("/")+1,line.indexOf("pat")+3).trim());
								}
								
							}
							else if(database.equalsIgnoreCase("ins"))
							{
								//record.put("SOURCEFILENAME", line.substring(line.indexOf("/")+1,line.indexOf("zip")+3).trim());
								sourceFilenameList.add(line.substring(line.indexOf("/")+1,line.indexOf("zip")+3).trim());
							}
							else if(database.equalsIgnoreCase("nti"))
							{
								//record.put("SOURCEFILENAME", line.substring(line.indexOf("/")+1,line.indexOf("xml")+3).trim());
								sourceFilenameList.add(line.substring(line.indexOf("/")+1,line.indexOf("xml")+3).trim());
							}
							else if(database.equalsIgnoreCase("upt"))
							{
								//record.put("SOURCEFILENAME", line.substring(line.indexOf("/")+1,line.length()).trim());
								sourceFilenameList.add(line.substring(line.indexOf("/")+1,line.length()).trim());
							}
							else if (database.equalsIgnoreCase("grf") && operation.equalsIgnoreCase("ip"))
							{
								//record.put("SOURCEFILENAME", line.substring(line.indexOf(":")+1,line.indexOf("out")-1).trim());
								sourceFilenameList.add(line.substring(line.indexOf(":")+1,line.indexOf("out")-1).trim());
							}
							else
							{
								//record.put("SOURCEFILENAME", line.substring(line.indexOf(":")+1,line.indexOf("xml")+3).trim());
								sourceFilenameList.add(line.substring(line.indexOf(":")+1,line.indexOf("xml")+3).trim());
							}
					}
					
					//SqlLdr ERROR Messages
					if(line.contains("Rejected -"))
					{
						//if(database.equalsIgnoreCase("ins"))
						if(line.contains("column"))
						{
							errorMessage = line.substring(line.indexOf(":")+1, line.length()).trim();
							
							line = dis.readLine();

							if(line !=null)
							{
								errorMessage = errorMessage + " " + line;
							}

						}
						else
						{
							line = dis.readLine();

							if(line !=null && line.contains("ORA-0"))
							{
								errorMessage = line.substring(line.indexOf(":")+1, line.length()).trim();
							}

						}
						if(errorMessage!=null && errorMessageList.containsKey(errorMessage.toString()))
						{
							errorMessageList.put(errorMessage.toString(), errorMessageList.get(errorMessage.toString())+1);
						}
						else
						{
							errorMessageList.put(errorMessage.toString(),1);
						}

					}


					
					// Total number of successfully Loaded records
					if(line.contains("Rows successfully loaded") || line.contains("Row successfully loaded"))
					{
						if(line.contains("Rows successfully loaded"))
						{
							loadedRecordCount = Integer.parseInt(line.substring(0,line.indexOf("Rows")).trim());
						}
						
						else if(line.contains("Row successfully loaded"))
						{
							loadedRecordCount = Integer.parseInt(line.substring(0,line.indexOf("Row")).trim());
						}
						System.out.println("Total Loaded records: " + loadedRecordCount);
					}
					
					// Total number of rejected records
					if(line.contains("Rows not loaded due to data errors") || line.contains("Row not loaded due to data errors"))
					{
						if(line.contains("Rows not loaded due to data errors"))
						{
							rejectedRecordCount = Integer.parseInt(line.substring(0, line.indexOf("Rows")).trim());
						}
						else if(line.contains("Row not loaded due to data errors"))
						{
							rejectedRecordCount = Integer.parseInt(line.substring(0, line.indexOf("Row")).trim());
						}
						
						System.out.println("Total rejected records: " + rejectedRecordCount);
					}
					/*notInGRFMasterCount = getErrorTableCount(tableName);
					record.put("ERRORTABLECOUNT", Integer.toString(notInGRFMasterCount));*/
					
					
					
				}
				
				//Source File Count
				if(loadedRecordCount >=0 && rejectedRecordCount >=0)
				{
					
					//record.put("SOURCEFILECOUNT", Integer.toString(loadedRecordCount + rejectedRecordCount));
					System.out.println("SRC File Count " + (loadedRecordCount + rejectedRecordCount));
					sourcefileCountList.add(loadedRecordCount + rejectedRecordCount);
				}
				
				
			
				// Get distinct Error message, count from errorMessageList to load to log table
				
				if(errorMessageList.size()>0)
				{
					for (String key : errorMessageList.keySet())
					{
						if(sqlldrErrorMessage.length()>0)
						{
							sqlldrErrorMessage.append(" / ");
						}
						
						if(sqlldrErrorMessageCount.length()>0)
						{
							sqlldrErrorMessageCount.append(" / ");
						}
						sqlldrErrorMessage.append(key);
						sqlldrErrorMessageCount.append(errorMessageList.get(key));
					}
					
					sqlErrorMessageList.add(sqlldrErrorMessage.toString());
					sqlErrorMessageCountList.add(sqlldrErrorMessageCount.toString());
				}
				
				
				System.out.println("Sqlldr Error Message: " + sqlldrErrorMessage);
				System.out.println("Sqlldr Error Message Count: " + sqlldrErrorMessageCount);
				
				
				//TEMP TABLE COUNT, add "0" to avoid having "null"
				
				tempTableCountList.add(0);
				
				
				//SRC_TEMP_DIFF, Rejected Records Count from sqlldr file, temp count for new data load is 0
				srcTempDiffCountList.add((rejectedRecordCount + loadedRecordCount)-0);
					
					
				//Master TABLE COUNT 
				System.out.println("Total Count in Master table is : " + loadedRecordCount);
				masterTableCountList.add(loadedRecordCount);
				
				
				//SRC_Master_DIFF 
				src_master_diff = (loadedRecordCount + rejectedRecordCount) - loadedRecordCount;
				srcMasterDiffCountList.add(src_master_diff);
				System.out.println("Src_master count diff is : " + src_master_diff);
				
				
				//sqlldr log file Info
				if(fileName !=null)
				{	
					sqlldrInfoList.add(fileName);
				}
				
				//Fast Extract File Name, Fast Extract Count
				if(!(operation.equalsIgnoreCase("ip")))
				{
					readFastExtractLog(fastExtractLogFileName,database);
				}
				
				
			}
			else
			{
				System.out.println("Invalid Database");
				System.exit(1);
			}
		}
			
		catch (FileNotFoundException ex)
		{
			//System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		

	}
	
	private static int getErrorTableCount(String tableName)
	{
		int count = 0;
		String query = "select count(*) from "+ tableName +" WHERE UPDATE_NUMBER=" + updateNumber + " and lower(source) like '%exception%'";
		try
		{
		con = getConnection(connectionURL,driver,username,password);
		stmt = con.createStatement();
		rs = stmt.executeQuery(query);
		
		if(rs.next())
		{
			count = rs.getInt(1);
		}
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
		return count;
	}
	
	
	private static int getMasterTableCount(String tableName)
	{
		int count = 0;
		String query = null;
		
		try
		{
			
			//if(record.containsKey("UPDATENUMBER") && record.containsKey("OPERATION"))
			if(operation != null)
			{
				query = "select master_count from "+ tableName +" WHERE DB='" + database + "' and updatenumber=" + updateNumber
						+ " and action='"+ operation + "'";
			
			
				con = getConnection(connectionURL,driver,username,password);
				stmt = con.createStatement();
				rs = stmt.executeQuery(query);

				while(rs.next())
				{
					count = rs.getInt(1);

				}
			}
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
		return count;
	}

	
	
	private static void getErrorTableMessage (String tableName)
	{
		String query = "select distinct EX,message from "+ tableName +" WHERE UPDATE_NUMBER=" + updateNumber;
		int count = 0;

		try
		{
			con = getConnection(connectionURL,driver,username,password);
			stmt = con.createStatement();
			rs= stmt.executeQuery(query);

			while(rs.next())
			{
				//System.out.println("Accessnumber:  " + rs.getString(1));
				if(accessnumbers.length()>0)
				{
					accessnumbers.append(",");
				}
				accessnumbers.append(rs.getString(1));
				
				if(count ==0)
				{
					errorMessage = rs.getString(2);
					count = 1;
				}

			}
			
			if(errorMessage !=null && errorMessage.length()>0)
			{
				errorMessage = errorMessage.substring(0, errorMessage.indexOf("record") + 6) + " " + errorMessage.substring(errorMessage.indexOf("not"));
			}
			
			if(accessnumbers !=null && errorMessage !=null && accessnumbers.length()>0)
			{
				record.put("ACCESSNUMBER", accessnumbers.toString());
				record.put("ERRORMESSAGE", errorMessage); 
			}
			
			
			
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
	
// get the error message from sqlldr file 	
	private static void getSqlldrErrorMessage (String sqlldrFileName)
	{
		HashMap<String, Integer> sqlldrErrorMessageList = new HashMap<String,Integer>();
		

		try
		{
			File sqlldrfile = new File(sqlldrFileName);
			if(!sqlldrfile.exists())
			{
				System.out.println("Sqlldr File Not found");
				System.exit(1);
			}
			FileInputStream sqlldrfis = new FileInputStream(sqlldrfile);
			BufferedInputStream sqlldrbis = new BufferedInputStream(sqlldrfis);
			DataInputStream sqlldrdis = new DataInputStream(sqlldrbis);

			String line = null;
			while (sqlldrdis.available() !=0)
			{
				line = sqlldrdis.readLine();


				//ERROR Messages
				if(line.contains("Rejected -"))
				{
					line = sqlldrdis.readLine();
					if(line !=null && line.contains("ORA-0"))
					{
						if(sqlldrErrorMessageList.containsKey(line))
						{
							sqlldrErrorMessageList.put(line, sqlldrErrorMessageList.get(line)+1);
						}
						else
						{
							sqlldrErrorMessageList.put(line, 1);
						}
					}
				}
			}
			
			// add other errors as well to same field
			
			if(otherErrors != null && otherErrors.size() >0)
			{
				for(String key: otherErrors.keySet())
				{
					sqlldrErrorMessageList.put(key, otherErrors.get(key));
				}
			}
		}
		catch(FileNotFoundException ex)
		{
			ex.getCause();
			ex.printStackTrace();
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}


		// Get distinct Error message from errorMessageList, and corresponding Count  to load to log table 
		if(sqlldrErrorMessageList != null && sqlldrErrorMessageList.size() >0)
		{
			for(String key: sqlldrErrorMessageList.keySet() )
			{
				if(sqlldrErrorMessage.length()>0)
				{
					sqlldrErrorMessage.append(" / ");
				}
				
				if(sqlldrErrorMessageCount.length()>0)
				{
					sqlldrErrorMessageCount.append(" / ");
				}
				sqlldrErrorMessage.append(key);
				sqlldrErrorMessageCount.append(sqlldrErrorMessageList.get(key));
			}
		}

		sqlErrorMessageList.add(sqlldrErrorMessage.toString());
		sqlErrorMessageCountList.add(sqlldrErrorMessageCount.toString());
		
		System.out.println("Sqlldr Error Message: " + sqlldrErrorMessage);
		System.out.println("Sqlldr Error Count: " + sqlldrErrorMessageCount);
		
			
	}
		
	
	// get the error message from sqlldr file 	
		private static void readFastExtractLog (String fastExtractLogFileName, String dataset)
		{
			StringBuffer fastExtractFileName = new StringBuffer();
			int totalFastExtractCount = 0;
			String extractRecordsCount = null;

			try
			{
				File fastextractlogfile = new File(fastExtractLogFileName);
				if(!fastextractlogfile.exists())
				{
					System.out.println("Fast Extract Log File Not found");
					System.exit(1);
				}
				FileInputStream fastExtractLogFis = new FileInputStream(fastextractlogfile);
				BufferedInputStream fastExtractLogBis = new BufferedInputStream(fastExtractLogFis);
				DataInputStream fastExtractLogDis = new DataInputStream(fastExtractLogBis);

				String line = null;
				while (fastExtractLogDis.available() !=0)
				{
					line = fastExtractLogDis.readLine();


					// "nti" extracted to fast as "ntis", so need to map 
					
					if(dataset !=null && dataset.equalsIgnoreCase("nti"))
					{
						dataset = "ntis";
					}
					//Fast Extract File
					if(line.contains("zip is a valid zip file with:") && line.contains("/EIDATA/") && line.contains(dataset+"/fast/batch"))
					{
						if(fastExtractFileName !=null && fastExtractFileName.length()>0)
						{
							
							fastExtractFileName.append(",");
						}
						fastExtractFileName.append(line.substring(0, line.indexOf("zip")+3).trim());
						
						System.out.println("Fast Extract FileName " + fastExtractFileName);
						
						extractRecordsCount = line.substring(line.indexOf(":") +1,line.indexOf("records")).trim();
						System.out.println("fast extract records count for "+ dataset +" "+ extractRecordsCount);
						
						totalFastExtractCount = totalFastExtractCount + Integer.parseInt(extractRecordsCount);
					}
					else if (line.contains("zip is a valid zip file with:") && line.contains("/EIDATA/") && line.contains("_upt_add_") && dataset.equalsIgnoreCase("upt"))
					{
						if(fastExtractFileName !=null && fastExtractFileName.length()>0)
						{
							
							fastExtractFileName.append(",");
						}
						fastExtractFileName.append(line.substring(0, line.indexOf("zip")+3).trim());
						extractRecordsCount = line.substring(line.indexOf(":") +1,line.indexOf("records")).trim();
						totalFastExtractCount = totalFastExtractCount + Integer.parseInt(extractRecordsCount);
					}
					
			}

				
				fastExtractFileNameList.add(fastExtractFileName.toString());
				fastExtractCountList.add(totalFastExtractCount);
				
				
		}
			catch(FileNotFoundException ex)
			{
				ex.getCause();
				ex.printStackTrace();
			}

			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
			

private static void getRecords()
{
	Hashtable<String, String> record;
	
	if(sourceFilenameList !=null && sourceFilenameList.size()>0)
	{
		for (int i=0; i<sourceFilenameList.size();i++)
		{
			record =  new Hashtable<String,String>();

			//DataBase
			record.put("DATABASE",database);
			
			//LoadNumber
			record.put("LOADNUMBER",Integer.toString(loadNumber));
			
			//UpdateNumber
			 record.put("UPDATENUMBER", Integer.toString(updateNumber));
			
			//Temp till conv program generate converted file count, converted file count
				record.put("CONVERTEDFILECOUNT", "");
				
			//Operation
			record.put("OPERATION",operation);
				
				
			//Source FileName
			if(sourceFilenameList.size() >0)
			{
				record.put("SOURCEFILENAME", sourceFilenameList.get(i));
			}	
				
			//Source File Count
			if(sourcefileCountList.size() >0)
			{
				record.put("SOURCEFILECOUNT", sourcefileCountList.get(i).toString());
			}
				
			//TEMP TABLE COUNT	
			if(tempTableCountList.size() >0)
			{
				record.put("TEMPTABLECOUNT", tempTableCountList.get(i).toString());
			}

			// SRC TEMP Diff Count
			if(srcTempDiffCountList.size() >0)
			{
				record.put("SRC_TEMP_DIFF", srcTempDiffCountList.get(i).toString());
			}
						
			// Master Table Count
			if(masterTableCountList.size() >0)
			{
				record.put("MASTERTABLECOUNT", masterTableCountList.get(i).toString());
			}
				
			// SRC Mater Diff Count	
			if(srcMasterDiffCountList.size() >0)
			{
				record.put("SRC_MASTER_DIFF", srcMasterDiffCountList.get(i).toString());
			}
				
			//Fast Extract File Name, it is only once
			if(fastExtractFileNameList.size() >0)
			{
				record.put("FASTEXTRACTFILENAME", fastExtractFileNameList.get(0));
			}
					
			//Fast Extract Count
			if(fastExtractCountList.size() >0)
			{
				record.put("FASTEXTRACTCOUNT",fastExtractCountList.get(0).toString());
				
				//Master_Fast DIFF 
				if(masterTableCountList.size() >0 && masterTableCountList.get(i) != null)
				{
					record.put("MASTER_FAST_DIFF", Integer.toString(masterTableCountList.get(i) -  fastExtractCountList.get(0)));
				}
				
			}
	
			// Sqlldr Info 
			if(sqlldrInfoList.size() >0)
			{
				record.put("SQLLDRLOG", sqlldrInfoList.get(i));
			}

			//Sqlldr Log File Error Message & Count
			if( sqlErrorMessageList.size()>0 && sqlErrorMessageCountList.size() >0)
			{			
				record.put("ERRORMESSAGE", sqlErrorMessageList.get(i));
				record.put("ERRORMESSAGECOUNT", sqlErrorMessageCountList.get(i));
				
			}
			
			
			// OtherErrors Identifier (AN/PUI) in txt log file 
			if(otherErrorsAccessnumberPuiList.size() >0)
			{
				record.put("ACCESSNUMBER", otherErrorsAccessnumberPuiList.get(i));
			}
			
			
			
			//Error Table Exception Count, only once 
			if(errorTableCountList.size() >0)
			{
				record.put("DB_EXCEPTION_COUNT",errorTableCountList.get(0).toString());
			}
		
			
			
			records.add(record);
				
		}
	}

}

// when there is more than one file for same operation (i.e. toc update) when it has more than one file
private static void sqlldrFileNameFormat(String sqlldrFileName)
{
	int sqlldrFileCount = 0;
	if(sqlldrFileName !=null && sqlldrFileName.contains(";"))
	{
		StringTokenizer sqlldrFileTokenizer = new StringTokenizer(sqlldrFileName, ";");
		String sqlldrFilePath = null;
		String sqlldrFile = null;

		while(sqlldrFileTokenizer.hasMoreTokens())
		{
			sqlldrFile = sqlldrFileTokenizer.nextToken().toString().trim();
			if(sqlldrFileCount==0)
			{
				System.out.println("sqlldr FIle : " + sqlldrFile);
				if(sqlldrFile !=null && sqlldrFile.length()>0)
				{
					sqlldrFilePath = sqlldrFile.substring(0, sqlldrFile.lastIndexOf("/")+1);

					System.out.println("sqlldr FIle Path : " + sqlldrFilePath);
				}
			}

			// append path to second file in case second file does not include the whole path
			/*else if (sqlldrFileCount>0 && sqlldrFilePath !=null)
			{
				sqlldrFile =  sqlldrFilePath + sqlldrFile;
			}*/
			sqlldrFileCount++;
		}
		sqlldrFileNameList.add(sqlldrFile);
		getSqlldrErrorMessage(sqlldrFile);

	}
}


	private static Connection getConnection(String connectionURL,String driver, String username, String password)
	throws Exception
	{
		Class.forName(driver);
		
		Connection con = DriverManager.getConnection(connectionURL, username, password);
		
		return con;
	}
	
	private static void writeRecs () throws Exception
	{
		String key;
		String value;
		Set<String> keySet = record.keySet();

		StringBuffer recordBuf = new  StringBuffer();

		Hashtable<String, String> rec = null;

		try
		{

			if(records == null)
			{
				System.out.println("records is null");
				System.exit(0);
			}

			File file = new File(loadNumber+".log.out");
			if (!file.exists()) 
			{
				//file.createNewFile();
				System.out.println("Out FIle name is : " + file.getName());
			}

			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file.getName(),true)));

			if(sourceFilenameList !=null && sourceFilenameList.size()>0)
			{
				for(int i = 0 ;i<sourceFilenameList.size();i++)
				{
					rec = records.get(i);

					if(rec !=null)
					{
						for (int j =0; j<logTableFields.length;++j)
						{
							String bf = logTableFields[j];
							//String val = record.get(bf);
							String val = rec.get(bf);

							if(j > 0)
							{
								out.print("	");
							}
							if(val!=null)
							{
								out.print(val);
							}
						}

						out.println("");
						
					}
				}
			}
			
			out.flush();
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
