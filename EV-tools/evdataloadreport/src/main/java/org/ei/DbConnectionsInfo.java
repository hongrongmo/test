package org.ei;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DbConnectionsInfo {
	
	String url = "";
	String driver = "oracle.jdbc.driver.OracleDriver";
	String dbUserName = "awseid";
	String password = "";
	
	Connection con;
	Statement stmt =null;
	ResultSet rs =null;
	
	// con count by username
	Statement stmt2 =null;
	ResultSet rs2 =null;
	
	ArrayList <Map<String,String>> connectionsByUserList = new ArrayList<Map<String,String>>();
	

	public DbConnectionsInfo()
	{
		
	}
	
	public DbConnectionsInfo(String url, String dbPassword)
	{
		this.url = url;
		this.password = dbPassword;
	}
	
	public int fetchTotalDbConnections()
	{
		int totalConnections = 0;
		
		String query = "SELECT count(*) as total FROM v$session WHERE username IS NOT NULL and username not in ('AWSEID','DBSNMP','SYSMAN','RDSADMIN')";
		
		String query2 = "SELECT username,count(*) as total FROM v$session WHERE username IS NOT NULL and username not in ('AWSEID','DBSNMP','SYSMAN','RDSADMIN') "+
				"group by username ORDER BY username ASC";
		
		try
		{
			con = getConnection(url,driver,dbUserName,password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			while(rs.next())
			{
				totalConnections = rs.getInt("total");
			}
			
			
			stmt2 = con.createStatement();
			rs2 = stmt2.executeQuery(query2);
			
			while(rs2.next())
			{	
				Map <String,String>row = new HashMap<String,String>();
				
					row.put("USERNAME",rs2.getString("username"));
					row.put("TOTAL",rs2.getString("total"));
					connectionsByUserList.add(row);
			}
			
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
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if(stmt2 !=null)
			{
				try
				{
					stmt2.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if(rs2 !=null)
			{
				try
				{
					rs2.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			
		}
		return totalConnections;
	}

	public Connection getConnection(String connectionURL, String driver, String username, String password)	
			throws Exception
	{
		Class.forName(driver);
		con = DriverManager.getConnection(connectionURL, username, password);
		return con;
	}
	
	public ArrayList <Map<String,String>> getConnectionCountByUsername()
	{
		return connectionsByUserList;
	}
	
}
