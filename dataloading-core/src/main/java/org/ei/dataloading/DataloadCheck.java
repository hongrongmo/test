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
	
	public static final String[] logTableFields= {"DATABASE","OPERATION","LOADNUMBER","UPDATENUMBER","SOURCEFILENAME","SOURCEFILECOUNT","CONVERTEDFILECOUNT","TEMPTABLECOUNT","SQLLDRLOG","MASTERTABLECOUNT","SRC_MASTER_DIFF","SRC_TEMP_DIFF","FASTEXTRACTFILENAME","FASTEXTRACTCOUNT", "ACCESSNUMBER" , "ERRORMESSAGE" , "DB_EXCEPTION_COUNT"};

	static String fast_count;
	static int src_master_diff = 0;
	static int cpxDbExceptionCount = 0;
	static int grfDbExceptionCount = 0;
	
	static StringBuffer accessnumbers = new StringBuffer();
	static String errorMessage = null;
	
	
	public static void main(String[] args) throws Exception{
		
		record = new Hashtable<String,String>();
		
		
			if(args[0]!=null)
			{
				database = args[0];
				record.put("DATABASE",database);
			}
			
			if(args[1]!=null)
			{
				fileName = args[1];
			}
			
			if(args[2]!=null)
			{
				loadNumber = Integer.parseInt(args[2]);
				record.put("LOADNUMBER",Integer.toString(loadNumber));
			}
			
			if(args[3]!=null && args[3].length()>0)
            {
                Pattern pattern = Pattern.compile("^\\d*$");
                Matcher matcher = pattern.matcher(args[3]);
                if (matcher.find())
                {
                    updateNumber = Integer.parseInt(args[3]);
                    record.put("UPDATENUMBER", Integer.toString(updateNumber));
                    
                    System.out.println("Updatenumber is : " + record.get("UPDATENUMBER"));
                    
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
				//record.put("OPERATION",operation);
				
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

		//Print Info
		writeRec();
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
				//INFO from Log file
				while (dis.available() !=0)
				{
					line = dis.readLine();

					//Source FileName
					if(line.contains("No errors detected in compressed data of"))
					{
						StringBuffer str = new StringBuffer("No errors detected in compressed data of");
						record.put("SOURCEFILENAME", line.substring(str.length(),line.length()).trim());
					}

					//Source File Count
					if(line.contains("Total counts for this file is"))
					{
						record.put("SOURCEFILECOUNT", dis.readLine().trim());
					}

					//TEMP TABLE COUNT
					if(line.contains("records was loaded into the temp table"))
					{ 
						record.put("TEMPTABLECOUNT", line.substring(0,line.indexOf("records")).trim());
					}

					//UpdateNumber , and updated/deleted Master Table Count
					if(line.contains("UPDATENUMB   COUNT(*)"))
					{
						dis.readLine();
						String updNumCount = dis.readLine();
						updNumCount = perl.substitute("s/\t/;/g",updNumCount);
						if(updNumCount!=null && updNumCount.length()>0 && firstmatch==0)
						{
							System.out.println("updNumCount: " +updNumCount);
							updatenum = updNumCount.substring(0,updNumCount.indexOf(';')).trim();
							Count = Integer.parseInt(updNumCount.substring(updNumCount.indexOf(';')+1,updNumCount.length()).trim());
							firstmatch = 1;
							
							//Src_Master_diff count & diff
							src_master_diff = Integer.parseInt(record.get("SOURCEFILECOUNT")) -Count;
							record.put("SRC_MASTER_DIFF", Integer.toString(src_master_diff));
						}
						else
						{
							System.out.println("Why updNumCount does not have data?????");
						}
						//record.put("UPDATENUMBER", updatenum);  since updatenumber is sent as param, so use it instead of getting from log file
						record.put("MASTERTABLECOUNT", Integer.toString(Count));
					}

					//Fast Extract File Name, Fast Extract Count
					if(line.contains("fast/batch") && line.contains("is a valid zip"))
					{
						fast_count = line.substring(line.indexOf(":") +1, line.length()).trim();
						record.put("FASTEXTRACTFILENAME", line.substring(0, line.indexOf("is a valid zip file with")).trim());
						//record.put("FASTEXTRACTCOUNT",line.substring(line.indexOf(":") +1, line.length()).trim());	
						if(fast_count.contains("records"))
						{
							record.put("FASTEXTRACTCOUNT",fast_count.substring(0, fast_count.indexOf(" records")));
						}
						else 
						{
							record.put("FASTEXTRACTCOUNT",fast_count.trim());
						}
						
					}
					
					//Operation
					
					if(!(record.containsKey("OPERATION")))
					{
						record.put("OPERATION",operation);
					}

					//sqlldr log file Name from original log file
					if(line.contains("/corrections/logs") && line.contains("out.log") && !(line.contains("Reference_")))
					{
						record.put("SQLLDRLOG", line.trim());
					}

				}
				
				//DB EXCEPTION COUNT
				
				cpxDbExceptionCount = getErrorTableCount(tableName);
				record.put("DB_EXCEPTION_COUNT", Integer.toString(cpxDbExceptionCount));
				
				
				// Accessnumber(s) rejected or having other issue & error message from bd_correction_error
				//getErrorTableMessage (tableName);
				
				
				//Sqlldr ErrorMessage INFO from sqlldr Log file
				getSqlldrErrorMessage (sqlldrlogFileName);
			
			}
			
			
			else if(database!=null && database.equalsIgnoreCase("cpx") && (operation !=null && operation.equalsIgnoreCase("s300")))
			{
				tableName = "BD_AIP_ERROR";
				username = "ba_s300";
				password = "ei7it";
				
				while (dis.available() !=0)
				{
					line = dis.readLine();
					
					//Source FileName
					if(line.contains("No errors detected in compressed data of") && !(line.contains("upload_to_fast")))
					{
						StringBuffer str = new StringBuffer("No errors detected in compressed data of");
						record.put("SOURCEFILENAME", line.substring(str.length(),line.length()).trim());
						
					}
					
					//Source File Count
					if(line.contains("Total counts of combined file is"))
					{
						record.put("SOURCEFILECOUNT", line.substring(line.indexOf(':')+1,line.length()).trim());
					}
					
					//TEMP TABLE COUNT
					if(line.contains("records was loaded into the temp table"))
					{ 
						record.put("TEMPTABLECOUNT", line.substring(0,line.indexOf("records")).trim());
					}
					
					
					//Master TABLE COUNT & diff
					if(line.contains("ba_s300@EID> ba_s300@EID> ba_s300@EID>") && (dis.readLine().contains("COUNT(*)")))
					{
						dis.readLine();
						Count = Integer.parseInt(dis.readLine().trim());
						record.put("MASTERTABLECOUNT", Integer.toString(Count));
						
						//Src_Master_diff count
						if(record.get("SOURCEFILECOUNT")!=null)
						{
							src_master_diff = Integer.parseInt(record.get("SOURCEFILECOUNT")) - Count;
							record.put("SRC_MASTER_DIFF", Integer.toString(src_master_diff));
	
						}
					}
					
					//UpdateNumber 
					if(line.contains("updateNumber="))
					{
						updatenum = line.substring(line.indexOf('=')+1, line.indexOf("fileName=")).trim();
						record.put("UPDATENUMBER", updatenum);

					}
					
					
					//Fast Extract File Name, Fast Extract Count
					if(line.contains("Checking extract files for correction update number") && firstmatch==0)
					{	
						line = dis.readLine();
						
						fast_count = line.substring(line.indexOf(":") +1, line.length()).trim();
						
						record.put("FASTEXTRACTFILENAME", line.substring(0, line.indexOf("is a valid zip file with")).trim());	 
						record.put("FASTEXTRACTCOUNT",fast_count.substring(0, fast_count.indexOf(" records")));
						firstmatch = 1;
					}
					
					//sqlldr log file Info
					if(line.contains("Check Export result") )
					{						
						line = dis.readLine();
						record.put("SQLLDRLOG", line.substring(line.indexOf("/data"), line.length()));						
					}
					
					if(!(record.containsKey("OPERATION")))
					{
						record.put("OPERATION",operation);
					}


				}
				
				//DB EXCEPTION COUNT
				cpxDbExceptionCount = getErrorTableCount(tableName);
				record.put("DB_EXCEPTION_COUNT", Integer.toString(cpxDbExceptionCount));
				
				
				// Accessnumber(s) rejected or having other issue & error message 
				//getErrorTableMessage (tableName);
				
				
				//Sqlldr ErrorMessage INFO from sqlldr Log file
				getSqlldrErrorMessage (sqlldrlogFileName);
			}
			
			
			
			else if(database!=null && (database.equalsIgnoreCase("grf") || database.equalsIgnoreCase("elt"))  && (operation !=null && operation.equalsIgnoreCase("update")))
			{
				if(database.equalsIgnoreCase("grf"))
				{
					tableName = "GEOREF_CORRECTION_ERROR";
				}
				
				else if (database.equalsIgnoreCase("elt"))
				{
					tableName = "BD_CORRECTION_ERROR";
				}
				while (dis.available() !=0)
				{
					line = dis.readLine();
					
					//Source FileName
					if(line.contains("Datafile is:"))
					{
						if(!(record.containsKey("SOURCEFILENAME")))
						{
							record.put("SOURCEFILENAME", line.substring(line.indexOf(":")+1,line.indexOf("and")).trim());
						}

						//Operation

						if(line.contains("action is:"))
						{
							if(!(record.containsKey("OPERATION")))
							{
								record.put("OPERATION", line.substring(line.lastIndexOf(":")+1,line.length()).trim());
							}

						}

					}
					
					//Source File Count
					if(line.contains("Total counts of"))
					{
						if(!(record.containsKey("SOURCEFILECOUNT")))
						{
							record.put("SOURCEFILECOUNT", line.substring(line.indexOf(':')+1,line.length()).trim());
						}
					}
					
					
					 
					//UpdateNumber
					if(line.contains("UPDATENUMBER:"))
					{
						if(!(record.containsKey("UPDATENUMBER")))
						{
							record.put("UPDATENUMBER", line.substring(line.indexOf(":")+1,line.length()).trim());
						}
					}
					
					//TEMP TABLE COUNT
					if(line.contains("records was loaded into the temp table"))
					{ 
						if(!(record.containsKey("TEMPTABLECOUNT")))
						{
							record.put("TEMPTABLECOUNT", line.substring(0,line.indexOf("records")).trim());
						}
						
					}
					
					//Fast Extract File Name, Fast Extract Count
					if(line.contains("Checking extract files for correction update number"))
					{	
						if(!(record.containsKey("FASTEXTRACTFILENAME")) && !(record.containsKey("FASTEXTRACTCOUNT")))
						{
							line = dis.readLine();
							
							if(line.contains("fast/batch") && line.contains("record"))
							{
								fast_count = line.substring(line.indexOf(":") +1, line.length()).trim();

								record.put("FASTEXTRACTFILENAME", line.substring(0, line.indexOf("is a valid zip file with")).trim());	
								record.put("FASTEXTRACTCOUNT",fast_count.substring(0, fast_count.indexOf(" record")));
							}

							
						}
					}
					
					//sqlldr log file Info
					if(line.contains("Check Export result") )
					{	
						if(!(record.containsKey("SQLLDRLOG")))
						{
							line = dis.readLine();
							record.put("SQLLDRLOG", line.substring(line.indexOf("/data"), line.length()));	
						}
					}


				}
				//Master TABLE COUNT & diff with src file count
				/*HH 04/06/2015 i added a sql stmt to easily get the total GRF_Master count (update/delete) - to be used after WK: 201516
				 * get from DB for now, till data becomes available in log file 
				 */
				if(!(record.containsKey("MASTERTABLECOUNT")))
				{
					
					Count = getMasterTableCount("ck_counts");
					record.put("MASTERTABLECOUNT", Integer.toString(Count));

					
					//Src_Master_diff count
					if(record.get("SOURCEFILECOUNT")!=null)
					{
						src_master_diff = Integer.parseInt(record.get("SOURCEFILECOUNT")) - Count;
						record.put("SRC_MASTER_DIFF", Integer.toString(src_master_diff));
					}
				}
				
				//DB EXCEPTION COUNT
				grfDbExceptionCount = getErrorTableCount(tableName);
				record.put("DB_EXCEPTION_COUNT", Integer.toString(grfDbExceptionCount));
				
				
				// Accessnumber(s) rejected or having other issue & error message 
				//getErrorTableMessage (tableName);
				
				//Sqlldr ErrorMessage INFO from sqlldr Log file
				getSqlldrErrorMessage (sqlldrlogFileName);
				
	
				
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
				ArrayList<String> errorMessageList = new ArrayList<String>();
				ArrayList<String> uniqueErrorMessageList = new ArrayList<String>();
				StringBuffer errorMessage = new StringBuffer();
				int loadedRecordCount = 0;
				int rejectedRecordCount = 0;
				StringBuffer sqlErrorMessage=new StringBuffer();
				String error="";
				
				System.out.println("Operation is " +  operation);
				while (dis.available() !=0)
				{
					line = dis.readLine();
					
					//Source FileName & Sqlldr file name
					
					if(line.contains("Data File:"))
					{
						if(!(record.containsKey("SOURCEFILENAME")))
						{
							if(database.equalsIgnoreCase("cbn"))
							{
								record.put("SOURCEFILENAME", line.substring(line.indexOf("/")+1,line.indexOf("txt")+3).trim());
							}
							else if (database.equalsIgnoreCase("ept"))
							{
								record.put("SOURCEFILENAME", line.substring(line.indexOf("/")+1,line.indexOf("pat")+3).trim());
							}
							else if(database.equalsIgnoreCase("ins"))
							{
								record.put("SOURCEFILENAME", line.substring(line.indexOf("/")+1,line.indexOf("zip")+3).trim());
							}
							else if(database.equalsIgnoreCase("nti"))
							{
								record.put("SOURCEFILENAME", line.substring(line.indexOf("/")+1,line.indexOf("xml")+3).trim());
							}
							else if(database.equalsIgnoreCase("upt"))
							{
								record.put("SOURCEFILENAME", line.substring(line.indexOf("/")+1,line.length()).trim());
							}
							else if (database.equalsIgnoreCase("grf") && operation.equalsIgnoreCase("ip"))
							{
								record.put("SOURCEFILENAME", line.substring(line.indexOf(":")+1,line.indexOf("out")-1).trim());
							}
							else
							{
								record.put("SOURCEFILENAME", line.substring(line.indexOf(":")+1,line.indexOf("xml")+3).trim());
							}
							
						}
					}
					
					//ERROR Messages
					if(line.contains("Rejected -"))
					{
						if(database.equalsIgnoreCase("ins"))
						{
							sqlErrorMessage.append(line.substring(line.indexOf(":")+1, line.length()).trim());
						}
						line = dis.readLine();
						
						if(line !=null && line.contains("ORA-0"))
						{
							//sqlErrorMessage.append(" ");
							//sqlErrorMessage.append(line);
							//errorMessageList.add(sqlErrorMessage.toString());
							errorMessageList.add(line.substring(line.lastIndexOf(":")+1, line.length()).trim());
							i++;
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
					
						record.put("SOURCEFILECOUNT", Integer.toString(loadedRecordCount + rejectedRecordCount));
					System.out.println("SRC File Count " + record.get("SOURCEFILECOUNT"));
				}
				
				
			
				// Get distinct Error message from errorMessageList to load to log table
				/*if(errorMessageList != null && errorMessageList.size() >0)
				{
					for(int j=0; j<errorMessageList.size();j++)
					{
						if(j ==0)
						{
							errorMessage.append(errorMessageList.get(j));
						}
						
						else if (j >=0)
						{
							if(!(errorMessageList.get(j).equalsIgnoreCase(errorMessageList.get(j).toString())))
							{
								errorMessage.append(",");
								errorMessage.append(errorMessageList.get(j));
							}
						}
					}
					
					record.put("ERRORMESSAGE", errorMessage.toString());
					System.out.println("Errormessage is :" + errorMessage.toString());
				}*/
				
				
				if(errorMessageList != null && errorMessageList.size() >0)
				{
					for(int j=0; j<errorMessageList.size();j++)
					{
						
						if (j >0)
						{
							if(!(uniqueErrorMessageList.contains(errorMessageList.get(j).toString())))
							{
								uniqueErrorMessageList.add(errorMessageList.get(j));
							}
						}
						else if (j==0)
						{
							uniqueErrorMessageList.add(errorMessageList.get(0));							
						}
					}
				
					
					// Get Unique Error Message 
					
					for(int k=0; k<uniqueErrorMessageList.size();k++)
					{
						if(errorMessage.length()>0)
						{
							errorMessage.append(",");
						}
						errorMessage.append(uniqueErrorMessageList.get(k));
					}

				record.put("ERRORMESSAGE", errorMessage.toString());
				System.out.println("Errormessage is :" + errorMessage.toString());
			
			}
				
				
				//TEMP TABLE COUNT, add "0" to avoid having "null"
				
					if(!(record.containsKey("TEMPTABLECOUNT")))
					{
						record.put("TEMPTABLECOUNT", "0");
					}
				
				
				//Rejected Records Count from sqlldr file, temp count for new data load is 0
				record.put("SRC_TEMP_DIFF", (Integer.toString((rejectedRecordCount + loadedRecordCount)-0)));
					
					
				//Master TABLE COUNT 
				record.put("MASTERTABLECOUNT", Integer.toString(loadedRecordCount));
				System.out.println("Total Count in Master table is : " + loadedRecordCount);
				
				
				//Master TABLE COUNT & SRC File Count diff
				src_master_diff = Integer.parseInt(record.get("SOURCEFILECOUNT")) - loadedRecordCount;
				record.put("SRC_MASTER_DIFF", Integer.toString(src_master_diff));
				
				System.out.println("Src_master count diff is : " + src_master_diff);
				
				
				//sqlldr log file Info
				if(fileName !=null)
				{	
					record.put("SQLLDRLOG", fileName);
				}
				
				//Operation
				
				if(!(record.containsKey("OPERATION")))
				{
					record.put("OPERATION",operation);
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
			
			if(record.containsKey("UPDATENUMBER") && record.containsKey("OPERATION"))
			{
				query = "select master_count from "+ tableName +" WHERE DB='" + database + "' and updatenumber=" + Integer.parseInt(record.get("UPDATENUMBER"))
						+ " and action='"+record.get("OPERATION") + "'";
			
			
				con = getConnection(connectionURL,driver,username,password);
				stmt = con.createStatement();
				rs = stmt.executeQuery(query);

				if(rs.next())
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
		ArrayList<String> sqlldrErrorMessageList = new ArrayList<String>();
		StringBuffer errorMessage = new StringBuffer(); 
		

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
						sqlldrErrorMessageList.add(line);

					}

				}
				
				/*// Total number of rejected records from sqlldr
				if(line.contains("Rows not loaded due to data errors") || line.contains("Row not loaded due to data errors"))
				{
					//ERROR TABLE COUNT
					record.put("SRC_TEMP_DIFF", (line.substring(0, line.indexOf("Row")).trim()));
				}*/
				

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


		// Get distinct Error message from errorMessageList to load to log table
		if(sqlldrErrorMessageList != null && sqlldrErrorMessageList.size() >0)
		{
			for(int j=0; j<sqlldrErrorMessageList.size();j++)
			{
				if(j ==0)
				{
					errorMessage.append(sqlldrErrorMessageList.get(j));
				}

				else if (j >=0)
				{
					if(!(sqlldrErrorMessageList.get(j).equalsIgnoreCase(errorMessage.toString())))
					{
						errorMessage.append(",");
						errorMessage.append(sqlldrErrorMessageList.get(j));
					}
				}
			}
		}

		//System.out.println("Sqlldr Error Message: " + errorMessage);
		
		if(!record.containsKey("ERRORMESSAGE"))
		{
			record.put("ERRORMESSAGE", errorMessage.toString());
		}
			
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
				
				record.put("FASTEXTRACTFILENAME", fastExtractFileName.toString());	 
				record.put("FASTEXTRACTCOUNT",Integer.toString(totalFastExtractCount));
				
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
			
	
	private static Connection getConnection(String connectionURL,String driver, String username, String password)
	throws Exception
	{
		Class.forName(driver);
		
		Connection con = DriverManager.getConnection(connectionURL, username, password);
		
		return con;
	}
	
private static void writeRec () throws Exception
{
	String key;
	String value;
	Set<String> keySet = record.keySet();
	
	StringBuffer recordBuf = new  StringBuffer();
	
	try
	{
		
	if(record == null)
	{
		System.out.println("record is null");
		System.exit(0);
	}
	
	File file = new File(loadNumber+".log.out");
	if (!file.exists()) 
	{
		//file.createNewFile();
		System.out.println("Out FIle name is : " + file.getName());
	}

	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file.getName(),true)));

	/*for(Iterator<String> itr = keySet.iterator();itr.hasNext();)
	{
		key = itr.next();
		System.out.println(key + ":" + record.get(key));
	}*/
	
	
	for (int j =0; j<logTableFields.length;++j)
	{
		String bf = logTableFields[j];
		String val = record.get(bf);
		
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
	out.flush();
	out.close();
	
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	
}

}
