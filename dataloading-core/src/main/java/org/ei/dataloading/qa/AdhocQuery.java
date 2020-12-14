package org.ei.dataloading.qa;

/**
 * 
 * @author TELEBH
 * @Date: 11/13/2020
 * @Description: Judy/TM want to receive regular Cafe-BD /BD-Cafe "8% gap" 
 * checking , i.e. records in Cafe not in BD or in BD not in Cafe, Plus New OpenAccess Values in Cafe
 * Check the info and send SNS/email with info 
 * 
 * queryTpe is one of 2 options, oa, cafegap
 */

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.dataloading.sharedsearch.PublishSES;
import org.dataloading.sharedsearch.PublishSNS;
import org.dataloading.sharedsearch.SharedSearchSearchEntry;
import org.ei.dataloading.db.DBConnection;

/**
 * 
 * @author TELEBH
 * @Date: 11/13/2020
 * @Description: Judy has created a jira ticket to tackl CAFE-BD Gap which was initially set to 8% as reported by Jum Slaton at the beginning of cafe feed project
 * In order to help Judy and content team identify the Gap we need to provide stats of current gap and 1k records for further checking by Judy's team
 * In addition to this, as per Judy request, tackle if any new openAccess values Cafe deliver other than 0,1,2
 * 
 */
public class AdhocQuery {
	
	String queryType;
	String url="jdbc:oracle:thin:@localhost:1521:eid";
	String driver="oracle.jdbc.driver.OracleDriver";
	String username="db_cafe";
	String password="";
	String database = "cpx";
	Logger logger;
	
	private String topic_arn = "arn:aws:sns:us-east-1:230521890328:EVDataLoading";
	private String subject = "ES Weekly QA Report";
	
	String cafe_bd_gap_file, bd_cafe_gap_file = null;
	
	Connection con = null;
	public static void main(String[] args)
	{
		if(args != null && args.length <5)
		{
			System.out.println("Not enough parameters!");
			System.exit(1);
		}
		else
		{
			AdhocQuery obj = new AdhocQuery();
			obj.run(args);
		}
		
	}
	public void run(String[] args)
	{
		if(args[0] != null && !args[0].isEmpty())
		{
			queryType = args[0];
			System.out.println("QA Type: " + queryType);
		}
		if(args[1] != null && !args[1].isEmpty())
			url = args[1];
		
		if(args[2] != null && !args[2].isEmpty())
			driver = args[2];
		
		if(args[3] != null && !args[3].isEmpty())
			username = args[3];
		
		if(args[4] != null && !args[4].isEmpty())
			password = args[4];
		
		if(args[5] != null && !args[5].isEmpty())
			database = args[5];
		if(args.length >6)
		{
			if(args[6] != null && !args[6].isEmpty())
			{
				topic_arn = args[6];
				System.out.println("Sending QA report to Topic: " + topic_arn);
			}		
		
		}
		
		try
		{
			// get Database Connection
			con = new DBConnection().getConnection(url, driver, username, password);
			logger = Logger.getLogger(AdhocQuery.class);
			// run appropriate query based on queryType
			if(queryType != null && ! queryType.isEmpty() && queryType.equalsIgnoreCase("oa"))
				runOACheck();
			else if((queryType != null && ! queryType.isEmpty() && queryType.equalsIgnoreCase("cafegap")))
			{
				runCafeGapCheck();
				// Send SES email with Cafe-BD Gap attachment
				new PublishSES().publishRawSESMessageWithAttachment(cafe_bd_gap_file);
			}
				
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void runOACheck()
	{
		StringBuilder sb = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			subject = "Cafe OpenAccess Check";
			String query = "select  NVL(ISOPENACESS,'NONE') as oa, count(*) as count from cafe_master group by NVL(ISOPENACESS,'NONE')";
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null)
			{
				while(rs.next())
				{
					if(rs.getString("OA") != null && rs.getString("COUNT") != null)
					{
						sb.append(rs.getString("OA")).append(" -> ").append(rs.getString("COUNT"));
						sb.append("\n");
					}
				}
				// send SNS message with final contents
				new PublishSNS(topic_arn,subject).publishSNSMessage(new Date(System.currentTimeMillis()) + " \n" + sb.toString());
			}
		}
		catch(SQLException ex)
		{
			logger.error("SQL Exception happened");
			logger.error("Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		
		finally
		{
			try
			{
				if(stmt != null)
					stmt.close();
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			try 
			{
				if (rs != null) 
				{
					rs.close();
				}
			}

			catch (Exception e) 
			{
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
	public void runCafeGapCheck()
	{
		StringBuilder sb = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		int count = 1;
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		String date = df.format(new Date());
		
		cafe_bd_gap_file = "Cafe-BD-Gap-" + date + ".csv";
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(cafe_bd_gap_file)))
		{
			String query = "Select a.pui as pui,a.accessnumber as accessnumber,a.publicationyear, a.citationtitle as citationtitle,count(*) over() as total from bd_master a"
					+ " where a.database='cpx' and (a.pui not in (select b.puisecondary from cafe_pui_list_master b)"
					+ " or a.accessnumber not in (select accessnumber from cafe_pui_list_master))";
			
			long startTime = System.currentTimeMillis();
			
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery(query);
			rs.setFetchSize(200);
			
			long midTime = System.currentTimeMillis();
			System.out.println("Total time it took to run query: " + (midTime - startTime)/1000 + " seconds");
			
			if(rs != null)
			{
				//Write Total CAFE-BD Gap count before adding details
				if(rs.next())
					if(rs.getNString("TOTAL") != null)
					{
						bw.write("Total Count: " + rs.getNString("TOTAL"));
						bw.write("\n");
					}
						
				//Write headers line
				bw.write("PUI, AccessionNumber,  PublicationYear, CitationTitle");
				bw.write("\n");
				// Reset the RS cursor back to before first row
				rs.beforeFirst();
				while(rs.next())
				{
					if(count<1001)
					{
						if(rs.getString("pui") != null)
						{
							bw.write(rs.getString("pui") + ",");
						}
						if(rs.getString("accessnumber") != null)
						{
							bw.write(rs.getString("accessnumber") + ",");
						}
						if(rs.getString("publicationyear") != null)
						{
							bw.write(rs.getString("publicationyear") + ",");
						}
						if(rs.getString("citationtitle") != null)
						{
							bw.write(rs.getString("citationtitle") + ",");
						}
						bw.write("\n");
						count++;
					}
					
				}
				long endTime = System.currentTimeMillis();
				System.out.println("Total time to write 1k records: " + (endTime - midTime)/1000 + " seconds");
				
			}
		}
		catch(IOException ex)
		{
			logger.error("IO Exception happened");
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
		catch(SQLException ex)
		{
			logger.error("SQL Exception happened");
			logger.error("Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		
		finally
		{
			try
			{
				if(stmt != null)
					stmt.close();
			}
			catch(Exception e)
			{
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			try 
			{
				if (rs != null) 
				{
					rs.close();
				}
			}

			catch (Exception e) 
			{
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}