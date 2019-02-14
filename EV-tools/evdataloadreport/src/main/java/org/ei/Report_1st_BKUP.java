package org.ei;

import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.text.StrBuilder;

public class Report_1st_BKUP {

	Connection con;
	Statement stmt =null;
	ResultSet rs =null;
	
	//ArrayList <ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
	ArrayList <Map<String,String>> rows = new ArrayList<Map<String,String>>();
	Map<String,Integer> totalCount = new HashMap<String, Integer>();
	
	
	Map <String,String>errorMessageMappingList = new HashMap<String,String>();
	
	
	
	
	public Report_1st_BKUP()
	{
		
	}
	
	public Report_1st_BKUP(int loadNumber)
	{
		try
		{
			errorMessageMappingList.put("unique constraint", "Most of the rejections were due to duplicated deliveries");
			errorMessageMappingList.put("Field in data file exceeds maximum length", "Some record(s) has extra tab and caused field shifting");
			errorMessageMappingList.put("cannot insert NULL into", "Record(s) missing accessnumber");
			errorMessageMappingList.put("Records blocked for ISSN", "Some record(s) blocked on purpose");
			errorMessageMappingList.put("Records blocked for E-ISSN", "Some record(s) blocked on purpose");
			errorMessageMappingList.put("other","Other");
			
			
			dataLoadReport(loadNumber);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void dataLoadReport(int loadNumber) throws Exception
	{
		//String url = "jdbc:oracle:thin:@localhost:15212:eid";   // for local testing
		String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";  //for deployment
		String driver = "oracle.jdbc.driver.OracleDriver";
		String dbuserName = "ap_correction1";
		String dbpassword = "ei3it";
		
		String query= "select dataset , operation , loadnumber , sourcefilename  , sourcefilecount , MASTERTABLECOUNT , SRC_MASTER_DIFF , ERRORMESSAGECOUNT , "
				+ "ERRORMESSAGE from dataload_report where loadnumber="+loadNumber + " order by dataset";
		
		try
		{
			con = getConnection(url, driver, dbuserName,dbpassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				StringBuffer errorMessage= new StringBuffer();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{
					// Map ErrorMessage
					if(rsmd.getColumnLabel(i).equalsIgnoreCase("ERRORMESSAGE"))
					{
						int id = 1;
						if(rs.getString(i) !=null && rs.getString(i).length() >0 && rs.getString(i).contains("/"))
						{
							StringTokenizer strtoken = new StringTokenizer(rs.getString(i), "/");
							
							while(strtoken.hasMoreTokens())
							{
								String str = strtoken.nextToken();
								
								if(errorMessage.length() >0)
								{
									errorMessage.append("\n");
								}
								if(str.contains("unique constraint"))
								{
									errorMessage.append("("+id+") "+ errorMessageMappingList.get("unique constraint"));
								}
								else if(str.contains("Field in data file exceeds maximum length"))
								{
									errorMessage.append("("+id+") "+errorMessageMappingList.get("Field in data file exceeds maximum length"));
								}
								else if(str.contains("cannot insert NULL into"))
								{
									errorMessage.append("("+id+") "+errorMessageMappingList.get("cannot insert NULL into"));
								}
								else if(str.contains("Records blocked for ISSN") ||  str.contains("Records blocked for E-ISSN"))
								{
									errorMessage.append("("+id+") "+ errorMessageMappingList.get("Records blocked for ISSN"));
								}
								else if(str.contains("other"))
								{
									errorMessage.append("("+id+") Other");
									
								}
								
								id++;
								
							}
						}
						else if (rs.getString(i) !=null && rs.getString(i).length() >0 && !(rs.getString(i).contains("/")))
						{
							if(errorMessage.length() >0)
							{
								errorMessage.append("\n");
							}
							if(rs.getString(i).contains("unique constraint"))
							{
								errorMessage.append(errorMessageMappingList.get("unique constraint"));
							}
							else if(rs.getString(i).contains("Field in data file exceeds maximum length"))
							{
								errorMessage.append(errorMessageMappingList.get("Field in data file exceeds maximum length"));
							}
							else if(rs.getString(i).contains("cannot insert NULL into"))
							{
								errorMessage.append(errorMessageMappingList.get("cannot insert NULL into"));
							}
							else if(rs.getString(i).contains("Records blocked for ISSN") ||  rs.getString(i).contains("Records blocked for E-ISSN"))
							{
								errorMessage.append(errorMessageMappingList.get("Records blocked for ISSN"));
							}
							else if(rs.getString(i).contains("other"))
							{
								errorMessage.append("Other");
								
							}
							
						}
						
						/*if(rs.getString(i) !=null && rs.getString(i).length() >0 && rs.getString(i).contains("unique constraint"))
						{
							if(errorMessage.length() >0)
							{
								errorMessage.append(",");
							}
							errorMessage.append("Some of the rejections were due to duplicated deliveries");
						}
						
						if(rs.getString(i) !=null && rs.getString(i).length() >0 && rs.getString(i).contains("Field in data file exceeds maximum length"))
						{
							if(errorMessage.length() >0)
							{
								errorMessage.append(",");
							}
							errorMessage.append("Some record(s) has extra tab and caused field shifting");
						}
						if(rs.getString(i) !=null && rs.getString(i).length() >0 && rs.getString(i).contains("cannot insert NULL into"))
						{
							if(errorMessage.length() >0)
							{
								errorMessage.append(",");
							}
							errorMessage.append("Some record(s) missing accessnumber");
						}
						if(rs.getString(i) !=null && rs.getString(i).length() >0 && (rs.getString(i).contains("Records blocked for ISSN") || 
								rs.getString(i).contains("Records blocked for E-ISSN")))
						{
							if(errorMessage.length() >0)
							{
								errorMessage.append(",");
							}
							errorMessage.append("Some record(s) blocked on purpose");
						}
						
						if(rs.getString(i) !=null && rs.getString(i).length() >0 && rs.getString(i).contains("other"))
						{
							if(errorMessage.length() >0)
							{
								errorMessage.append(",");
							}
							errorMessage.append("Other");
						}
						*/
						
						
						row.put(rsmd.getColumnLabel(i),errorMessage.toString());
					}
					
					else
					{
						row.put(rsmd.getColumnLabel(i),rs.getString(i));
					}

				}
				
				
				rows.add(row);

			}
			
			// get sum of sourcefilecount,MASTERTABLECOUNT,SRC_MASTER_DIFF
			countSum();
		}
		
		catch(SQLException sqlex)
		{
			System.out.println(sqlex.getMessage());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		finally
		{
			if(con !=null)
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
			
			if(rs !=null)
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
		}
	}
	
	public Connection getConnection(String connectionURL, String driver, String username, String password)	
	throws Exception
	{
		Class.forName(driver);
		con = DriverManager.getConnection(connectionURL, username, password);
		return con;
	}
	
	
	public ArrayList<Map<String,String>> getReport() throws Exception
	{
		return rows;
		
	}
	
	
	public void countSum()
	{
		int srcCountSum = 0;
		int masterCountSum = 0;
		int rejCountSum = 0;
		
		if(rows !=null && rows.size()>0)
		{
			for(Map<String,String> item: rows)
			{
				// Src file count sum 
				if(item.get("SOURCEFILECOUNT") !=null)
				{
					srcCountSum = srcCountSum + Integer.parseInt(item.get("SOURCEFILECOUNT"));
				}
				
				// Master table (loaded) count sum
				if(item.get("MASTERTABLECOUNT") !=null)
				{
					masterCountSum = masterCountSum + Integer.parseInt(item.get("MASTERTABLECOUNT"));
				}
				
				// Rejection Count Sum 
				if(item.get("SRC_MASTER_DIFF") !=null)
				{
					rejCountSum = rejCountSum + Integer.parseInt(item.get("SRC_MASTER_DIFF"));
				}
			}
			
			totalCount.put("TOTALSRCCOUT", srcCountSum);
			totalCount.put("TOTALLOADEDCOUNT",masterCountSum);
			totalCount.put("TOTALREJECTEDCOUNT",rejCountSum);
		}
		
	}
	
	public Map<String,Integer> getTotalCount()
	{
		return totalCount;
	}
	
}
