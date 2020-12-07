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
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.dataloading.sharedsearch.PublishSNS;
import org.dataloading.sharedsearch.SharedSearchSearchEntry;
import org.ei.dataloading.db.DBConnection;

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
		if(args.length >5)
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
				runCafeGapCheck();
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
		String fileName = "Cafe-BD-Gap-";
		
		//String
		try
		{
			
			String query = "Select a.pui,a.accessnumber,a.citationtitle,count(*) over as count from bd_master a, cafe_pui_list_master b"
					+ "where a.pui=b.puisecondary and b.puisecondary in (select pui from cafe_master)";
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

}
