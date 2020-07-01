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

public class Report {

	Connection con;
	Statement stmt =null;
	ResultSet rs =null;
	
	//ArrayList <ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
	ArrayList <Map<String,String>> rows = new ArrayList<Map<String,String>>();
	Map<String,Integer> totalCount = new HashMap<String, Integer>();
	
	
	Map <String,String>errorMessageMappingList = new HashMap<String,String>();
	
	
	
	
	public Report()
	{
		
	}
	
	public Report(int loadNumber)
	{
		try
		{
			errorMessageMappingList.put("unique constraint", "Most of the rejections were due to duplicated deliveries");
			errorMessageMappingList.put("Field in data file exceeds maximum length", "Some record(s) has extra tab and caused field shifting");  
			errorMessageMappingList.put("Field in data file exceeds maximum length2", "Field in data file exceeds maximum length"); // for records really rejected because DB column length limit, not for shifting
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
		String url = null;
		//String url = "jdbc:oracle:thin:@localhost:15212:eid";   // for local testing
		//String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";  //for deployment
		String driver = "oracle.jdbc.driver.OracleDriver";
		//String dbuserName = "ap_correction1";
		String dbuserName = "ap_report";
		String dbpassword = "";
		
		int loadnum = 201547;
		int totalCount = 0;
		int update_count=0;
		
		String database = null;
		
		String query= "select dataset , operation , loadnumber , sourcefilename  , sourcefilecount , CONVERTEDFILECOUNT, MASTERTABLECOUNT , SRC_MASTER_DIFF , ERRORMESSAGECOUNT , "
				+ "ERRORMESSAGE from dataload_report where loadnumber="+loadNumber + " order by dataset";
		
		try
		{
			url = RdsMapping.singleRdsMapping();
			
			
			con = getConnection(url, driver, dbuserName,dbpassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				database = rs.getString(1);
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{
					loadnum = Integer.parseInt(rs.getString("loadnumber"));
					// Map ErrorMessage
					if(rsmd.getColumnLabel(i).equalsIgnoreCase("ERRORMESSAGE"))
					{
						row.put(rsmd.getColumnLabel(i),errorMessageMapping(database,rs.getString(i)));						
						//						row.put(rsmd.getColumnLabel(i),errorMessage.toString());
					}
					
					// beginning of week 201547, started to load new cpx/s200(aip) as s300 corrections, so need to distinguish updated count from new count
					// that's due to duplicates within set of files within same week. so to apply change only start from loadnum#: 201547
					
					else if (rsmd.getColumnLabel(i).equalsIgnoreCase("SRC_MASTER_DIFF") && (rs.getString("dataset").equalsIgnoreCase("CPX")
							|| rs.getString("dataset").equalsIgnoreCase("AIP EEI") || rs.getString("dataset").equalsIgnoreCase("AIP TOC"))
							&& rs.getString("operation").equalsIgnoreCase("new")
							&& Integer.parseInt(rs.getString("loadnumber")) >=loadnum)
					{
						update_count = Integer.parseInt(rs.getString("CONVERTEDFILECOUNT")) - Integer.parseInt(rs.getString("MASTERTABLECOUNT"));
						
						if(rs.getString("ERRORMESSAGE") ==null)
						{
							
							row.put("UPDATE",Integer.toString(update_count));
							//row.put("UPDATE",rs.getString(i));
							row.put(rsmd.getColumnLabel(i),"0");
						}
						else
						{
							// set UPDATE COUNT as SRC_MASTER_DIFF, while discrepancy as sum of counts in ERRORMESSAGECOUNT
							if(rs.getString("ERRORMESSAGECOUNT") !=null)
							{
								/*if(rs.getString("ERRORMESSAGECOUNT").contains("/"))
									totalCount = aggregateErrorCount(rs.getString("ERRORMESSAGECOUNT").toString());
								else
									totalCount = Integer.parseInt(rs.getString("ERRORMESSAGECOUNT"));
								*/
							}
							//row.put("UPDATE",Integer.toString(totalCount));
							row.put("UPDATE",Integer.toString(update_count));
							row.put(rsmd.getColumnLabel(i),rs.getString(i));
						}

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
	
	
	
	public int aggregateErrorCount(String errorMsgCounts)
	{
		int totalErrorsCount = 0;
		StringTokenizer token = new StringTokenizer(errorMsgCounts, "/");
		while(token.hasMoreTokens())
		{
			if(token !=null)
			{
				totalErrorsCount = totalErrorsCount + Integer.parseInt(token.nextToken().trim());
			}
			
		}
		return totalErrorsCount;
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
		int convCountSum = 0;
		int masterCountSum = 0;
		int rejCountSum = 0;
		int updatedSum = 0;
		
		if(rows !=null && rows.size()>0)
		{
			for(Map<String,String> item: rows)
			{
				// Src file count sum 
				if(item.get("SOURCEFILECOUNT") !=null)
				{
					srcCountSum = srcCountSum + Integer.parseInt(item.get("SOURCEFILECOUNT"));
				}
				
				//Converted file count sum 
				if(item.get("CONVERTEDFILECOUNT") !=null)
				{
					convCountSum = convCountSum + Integer.parseInt(item.get("CONVERTEDFILECOUNT"));
				}
				// Master table (loaded) count sum
				if(item.get("MASTERTABLECOUNT") !=null)
				{
					masterCountSum = masterCountSum + Integer.parseInt(item.get("MASTERTABLECOUNT"));
				}
				
				//Updated (only new cpx and s200) Count sum
				if(item.get("UPDATE") !=null)
				{
					updatedSum = updatedSum + Integer.parseInt(item.get("UPDATE"));
				}
				
				// Rejection Count Sum 
				if(item.get("SRC_MASTER_DIFF") !=null)
				{
					rejCountSum = rejCountSum + Integer.parseInt(item.get("SRC_MASTER_DIFF"));
				}
			}
			
			totalCount.put("TOTALSRCCOUT", srcCountSum);
			totalCount.put("TOTALCONVERTEDCOUNT", convCountSum);
			totalCount.put("TOTALLOADEDCOUNT",masterCountSum);
			totalCount.put("TOTALUPDATEDCOUNT",updatedSum);
			totalCount.put("TOTALREJECTEDCOUNT",rejCountSum);
		}
		
	}
	
	public Map<String,Integer> getTotalCount()
	{
		return totalCount;
	}
	
	
	public String errorMessageMapping(String dataset, String error)
	{
		StringBuffer errorMessage= new StringBuffer();
		int id = 1;
		
		if(error !=null && error.length() >0 && error.contains("/"))
		{
			StringTokenizer strtoken = new StringTokenizer(error, "/");
			
			while(strtoken.hasMoreTokens())
			{
				String str = strtoken.nextToken();
				
				if(errorMessage.length() >0)
				{
					errorMessage.append("</br>");
				}
				if(str.contains("unique constraint"))
				{
					errorMessage.append("("+id+") "+ errorMessageMappingList.get("unique constraint"));
				}
				else if(str.contains("Field in data file exceeds maximum length") && (dataset!=null && dataset.equalsIgnoreCase("cpx")))
				{
					errorMessage.append("("+id+") "+errorMessageMappingList.get("Field in data file exceeds maximum length"));
				}
				else if(str.contains("Field in data file exceeds maximum length") && (dataset!=null && !(dataset.equalsIgnoreCase("cpx"))))  // other dataset, exceed max length is for real long data
				{
					errorMessage.append("("+id+") "+errorMessageMappingList.get("Field in data file exceeds maximum length2"));
				}
				else if(str.contains("cannot insert NULL into"))
				{
					errorMessage.append("("+id+") "+errorMessageMappingList.get("cannot insert NULL into"));
				}
				else if((str.contains("Records blocked for ISSN") ||  str.contains("Records blocked for E-ISSN"))
						&& (!(errorMessage.toString().contains(errorMessageMappingList.get("Records blocked for ISSN")))))
				{
					errorMessage.append("("+id+") "+ errorMessageMappingList.get("Records blocked for ISSN"));
				}
				else if(str.contains("other"))
				{
					errorMessage.append("("+id+") Other");
					
				}
				
				id++;
				
			}

			/* due to blocked records for issn/E-Issn should have one single message, so take off (1) in case there is only 2 reasons for rejection 
			/(ISSn/E-Issn) , take off (1)*/
			
			if(errorMessage.toString().contains("(1)") && !(errorMessage.toString().contains("(2)")))
			{
				errorMessage.delete(0, 3);
				errorMessage.delete(errorMessage.toString().indexOf("<"), errorMessage.toString().indexOf(">")+1);
			}
					
		}
		else if (error !=null && error.length() >0 && !(error.contains("/")))
		{
			if(errorMessage.length() >0)
			{
				errorMessage.append("</br>");
			}
			if(error.contains("unique constraint"))
			{
				errorMessage.append(errorMessageMappingList.get("unique constraint"));
			}
			else if(error.contains("Field in data file exceeds maximum length")  && (dataset!=null && dataset.equalsIgnoreCase("cpx")))
			{
				errorMessage.append(errorMessageMappingList.get("Field in data file exceeds maximum length"));
			}
			else if(error.contains("Field in data file exceeds maximum length") && (dataset!=null && !(dataset.equalsIgnoreCase("cpx"))))  // other dataset, exceed max length is for real long data
			{
				errorMessage.append(errorMessageMappingList.get("Field in data file exceeds maximum length2"));
			}
			else if(error.contains("cannot insert NULL into"))
			{
				errorMessage.append(errorMessageMappingList.get("cannot insert NULL into"));
			}
			else if(error.contains("Records blocked for ISSN") ||  error.contains("Records blocked for E-ISSN"))
			{
				errorMessage.append(errorMessageMappingList.get("Records blocked for ISSN"));
			}
			else if(error.contains("other"))
			{
				errorMessage.append("Other");
				
			}
			
		}
		
		return errorMessage.toString().trim();
	}
}
