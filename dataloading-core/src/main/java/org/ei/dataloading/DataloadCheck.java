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
	
	static Hashtable <String,String> record = null;
	
	private static Perl5Util perl = new Perl5Util();
	
	public static final String[] logTableFields= {"DATABASE","OPERATION","LOADNUMBER","UPDATENUMBER","SOURCEFILENAME","SOURCEFILECOUNT","CONVERTEDFILECOUNT","TEMPTABLECOUNT","SQLLDRLOG","MASTERTABLECOUNT","SRC_MASTER_DIFF","ERRORTABLECOUNT","FASTEXTRACTFILENAME","FASTEXTRACTCOUNT"};

	static String fast_count;
	static int src_master_diff=0;
	static int notInBDMasterCount=0;
	
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
				record.put("OPERATION",operation);
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
		String Count=null;
		int firstmatch = 0;
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
					String line = dis.readLine();

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
							Count = updNumCount.substring(updNumCount.indexOf(';')+1,updNumCount.length()).trim();
							firstmatch = 1;
							
							//Src_Master_diff count & diff
							src_master_diff = Integer.parseInt(record.get("SOURCEFILECOUNT")) - Integer.parseInt(Count);
							record.put("SRC_MASTER_DIFF", Integer.toString(src_master_diff));
						}
						else
						{
							System.out.println("Why updNumCount does not have data?????");
						}
						record.put("UPDATENUMBER", updatenum);
						record.put("MASTERTABLECOUNT", Count);
					}

					//Fast Extract File Name, Fast Extract Count
					if(line.contains("fast/batch"))
					{
						fast_count = line.substring(line.indexOf(":") +1, line.length()).trim();
						record.put("FASTEXTRACTFILENAME", line.substring(0, line.indexOf("is a valid zip file with")).trim());
						//record.put("FASTEXTRACTCOUNT",line.substring(line.indexOf(":") +1, line.length()).trim());	 
						record.put("FASTEXTRACTCOUNT",fast_count.substring(0, fast_count.indexOf(" records")));
					}

					//sqlldr log file Info
					if(line.contains("/corrections/logs") && line.contains("out.log"))
					{
						record.put("SQLLDRLOG", line.trim());
					}

				}
				
				//ERROR TABLE COUNT
				
				notInBDMasterCount = getErrorTableCount(tableName);
				record.put("ErrorTableCount", Integer.toString(notInBDMasterCount));

			}
			
			
			else if(database!=null && database.equalsIgnoreCase("cpx") && (operation !=null && operation.equalsIgnoreCase("s300")))
			{
				tableName = "BD_aip_ERROR";
				username = "ba_s300";
				password = "ei7it";
				
				while (dis.available() !=0)
				{
					String line = dis.readLine();
					
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
						Count = dis.readLine().trim();
						record.put("MASTERTABLECOUNT", Count);
						
						//Src_Master_diff count
						if(record.get("SOURCEFILECOUNT")!=null)
						{
							src_master_diff = Integer.parseInt(record.get("SOURCEFILECOUNT")) - Integer.parseInt(Count);
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

				}
				
				//ERROR TABLE COUNT
				notInBDMasterCount = getErrorTableCount(tableName);
				record.put("ErrorTableCount", Integer.toString(notInBDMasterCount));
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
		String query = "select count(*) from "+ tableName +" WHERE UPDATE_NUMBER=" + updateNumber;
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
