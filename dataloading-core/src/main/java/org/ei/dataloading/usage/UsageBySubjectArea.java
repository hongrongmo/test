package org.ei.dataloading.usage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 
 * @author TELEBH
 * @Date: 11/05/2015
 * @Description:  a report that summarizes usage of Compendex by "Subject Area"
 *to help generate the report the Dayton team completed a project where they 
 *look up the top 2-level terms from the Compendex thesaurus and record these in the Usage logging event 
 */
public class UsageBySubjectArea {

	static String connectionURL = "jdbc:oracle:thin:@localhost:1523:eia";
	String driver = "oracle.jdbc.driver.OracleDriver";
	String username = "db_log";
	String password = "ny5av";

	static String startDate = "";
	static String endDate = "";

	Connection con = null;
	PreparedStatement  stmt = null;
	ResultSet rs = null;


	//HashMap<String, Integer> subjectAreasList = new HashMap<String,Integer>();

	HashMap<String, Integer> subjectAreasList;
	HashMap<String,HashMap<String,Integer>> CustSubjectAreasList = new HashMap<String,HashMap<String,Integer>>();

	String timestamp = null;

	public UsageBySubjectArea(){}

	public static void main(String [] args)
	{
		if(args !=null && args.length>2)
		{
			connectionURL = args[0];
			startDate = args[1];
			endDate = args[2];
	
			System.out.println("Pull Usage Terms start from : " +  startDate + " to " + endDate);
		}
		UsageBySubjectArea usage = new UsageBySubjectArea();
		usage.fetchUsageData();
		usage.writetList();
	}


	private void fetchUsageData()
	{
		/*String query = "select TMSTP udate,APPDATA data from ALS_VILLAGE_LOG where to_date(TMSTP)>='01-OCT-15' and  to_date(TMSTP)<'01-NOV-15' "+
					"and APPDATA like '%criteria=%'";*/
		/*String query = "select TMSTP udate,APPDATA data from ALS_VILLAGE_LOG where to_date(TMSTP)>='29-OCT-15' and  to_date(TMSTP)<='30-OCT-15' "+
			"and APPDATA like '%criteria=%' and APPDATA like '%criteria=\"A%'and rownum<2";*/    //for testing a single record

		/*String query = "select to_char(TMSTP) udate,APPDATA data from ALS_VILLAGE_LOG where to_date(TMSTP)>='29-OCT-15' and  to_date(TMSTP)<='30-OCT-15' "+
				"and APPDATA like '%criteria=%' and (APPDATA like '%cust_id=\"C000062946\"%' or  APPDATA like '%cust_id=\"1021001533\"%')";*/

		/*String query = "select TMSTP udate,APPDATA data from ALS_VILLAGE_LOG where to_date(TMSTP)>='29-OCT-15' and  to_date(TMSTP)<='30-OCT-15' "+
			"and APPDATA like '%criteria=%'";
		 */

		/*String query = "select to_char(TMSTP) udate,APPDATA data from ALS_VILLAGE_LOG where to_date(TMSTP)>='"+startDate+"' and  to_date(TMSTP)<='"+endDate+"' "+
				"and APPDATA like '%criteria=%' and (APPDATA like '%cust_id=\"C000062946\"%' or  APPDATA like '%cust_id=\"1021001533\"%')";   // final testing
		 */

		/*String query = "select to_char(TMSTP) udate,APPDATA data from ALS_VILLAGE_LOG where to_date(TMSTP)>=? and  to_date(TMSTP)<=? "+
		"and APPDATA like '%criteria=%' and (APPDATA like '%cust_id=\"C000062946\"%' or  APPDATA like '%cust_id=\"1021001533\"%')";   // final testing
		 */
		String query = "select to_char(TMSTP) udate,APPDATA data from ALS_VILLAGE_LOG where to_date(TMSTP)>='"+startDate+"' and  to_date(TMSTP)<'"+endDate+"' "+
				"and APPDATA like '%criteria=\"%'";   



		try
		{
			System.out.println("Start Time: "+ System.currentTimeMillis()/1000.0);
			con = getConnection(connectionURL, driver, username, password);
			stmt = con.prepareStatement(query);


			System.out.println("Srunning Query: "+query + " ....");
			rs = stmt.executeQuery();

			while (rs.next())
			{
				timestamp = rs.getString("udate");
				parseData(rs.getString("udate"),rs.getString("data"));
			}
			System.out.println("End Time: "+ System.currentTimeMillis()/1000.0);
		}
		catch(SQLException sql)
		{
			System.out.println("SQL Exception: " +  sql.getMessage());
			sql.printStackTrace();
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
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void parseData(String usageDate, String usageData)
	{
		String customerID = "";
		String criteria = "";

		String tempCustomerID = "";
		String tempCriteria = "";

		if(usageDate != null && usageData !=null && usageData.length() >0)
		{
			if(usageData.contains("cust_id="))
			{
				tempCustomerID = usageData.substring(usageData.indexOf("cust_id"), usageData.length());
				customerID = tempCustomerID.substring(tempCustomerID.indexOf("cust_id")+8, tempCustomerID.indexOf(";")).replace("\"", "").trim();

				tempCriteria = usageData.substring(usageData.indexOf("criteria="), usageData.length());
				criteria = tempCriteria.substring(tempCriteria.indexOf("criteria=")+9,tempCriteria.indexOf(";")).replace("\"", "").trim();

				if(criteria.length() >0 && criteria.contains(","))
				{
					parseCriteria(customerID,criteria);
				}

			}
		}
	}

	private void parseCriteria(String customerID, String criteria)
	{
		StringTokenizer strtoken = new StringTokenizer(criteria,",");

		String subjectArea = null;

		if(CustSubjectAreasList.containsKey(customerID))
		{
			subjectAreasList = CustSubjectAreasList.get(customerID);
		}
		else
		{
			subjectAreasList = new HashMap<String,Integer>();
		}

		while(strtoken.hasMoreTokens())
		{
			subjectArea = strtoken.nextToken().toLowerCase();

			if(subjectAreasList.containsKey(subjectArea))
			{
				subjectAreasList.put(subjectArea, subjectAreasList.get(subjectArea) +1);
			}
			else
			{
				subjectAreasList.put(subjectArea, 1);
			}
		}

		CustSubjectAreasList.put(customerID, subjectAreasList);


	}

	private void writetList()
	{
		HashMap<String,Integer> row = new HashMap<String,Integer>();

		String date = timestamp.substring(timestamp.indexOf("-")+1, timestamp.length());

		PrintWriter out = null;

		try
		{
			
				if(CustSubjectAreasList.size() >0)
				{
					File file = new File("UsageSubjectArea_" + startDate + ".csv");
					if (!file.exists()) 
					{
						System.out.println("Out FIle name is : " + file.getName());
					}
					out = new PrintWriter(new BufferedWriter(new FileWriter(file.getName(),true)));

					out.print("Account " + "," + "Term" + "," + "Frequency" + "," + "Month");
					out.println("");

					for(String Key: CustSubjectAreasList.keySet())
					{
							row = (HashMap<String,Integer>)CustSubjectAreasList.get(Key);

							if(row.size() >0)
							{
								for(String subjet: row.keySet())
								{
										if(row.get(subjet) !=null)
										{
											out.println(Key + "," + subjet + "," + row.get(subjet) + "," + date);
										}
									//System.out.println(Key+ " : " + subjet+" : " + row.get(subjet));
								}
							}
						}

					}
			else
			{
				System.out.println("No Data to write");
			}

			
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
					out.flush();
					out.close();
				}
				catch(Exception ex)
				{
					System.out.println(ex.getMessage());
					ex.printStackTrace();
				}
			}
		}

	}


	private  Connection getConnection(String connectionURL,String driver, String username, String password)
			throws Exception
	{
		Class.forName(driver);

		Connection con = DriverManager.getConnection(connectionURL, username, password);

		return con;
	}


}
