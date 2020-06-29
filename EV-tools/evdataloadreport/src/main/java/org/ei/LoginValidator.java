package org.ei;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginValidator {

	static Connection con;
	Statement stmt = null;
	ResultSet rs = null;
	String role = null;
	
	
	public boolean validate(String userName, String password) throws Exception
	{
		boolean status = false;
		
		// DB connections
		String url = null;
		//String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";   // for deployment
		//String url = "jdbc:oracle:thin:@localhost:15212:eid";   //works for localhost
		String driver = "oracle.jdbc.driver.OracleDriver";
		String dbUserName = "ap_report";
		String dbPassword = "";
		
		String query = "select * from report_user_profile where username='" +userName+"' and password='"
				 +password+"'";
		 
		try
		{	
			url = RdsMapping.singleRdsMapping();
			
			
			con = getConnection(url,driver,dbUserName,dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			if(rs !=null)
			{
				status = rs.next();
				role = rs.getString("ROLE");
				
			}
			
		}
		catch(SQLException sqlex)
		{
			System.out.println(sqlex.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			if(con !=null)
			{
				try
				{
					con.close();
				}
				catch(SQLException ex)
				{
					ex.printStackTrace();
				}
			}
			
			if(stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (SQLException ex)
				{
					ex.printStackTrace();
				}
			}
			
			if(rs !=null)
			{
				try
				{
					rs.close();
				}
				catch(SQLException ex)
				{
					ex.printStackTrace();
				}
			}
		}
		return status;
	}
	

	protected Connection getConnection(String connectionURL, String driver, String username, String password)
			throws Exception
	{
		Class.forName(driver);
		
		Connection con = DriverManager.getConnection(connectionURL, username, password);
		
		return con;
	}
	
	public String getUserRole()
	{
		return role;
	}
}
