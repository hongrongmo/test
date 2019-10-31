package org.ei.util.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * 
 * @author TELEBH
 * @Date: 10/29/2019
 * @Description: Singleton Util class to return a db DB connection instance based on parameters provided
 */
public class DbConnection {
	
	private static Connection con = null;
	
	private DbConnection()
	{
		
	}
	public static Connection getConnection(String connectionURL, String driver, String userName, String password)
	{
		
		if(con ==null)
		{	
			try
			{
				//1. register JDBC driver
				Class.forName(driver);
				
				//2. open a connection
				con = DriverManager.getConnection(connectionURL, userName, password);
				System.out.println("Create DB connection for DB: " + connectionURL + ", username: " + userName);
			}
			catch (ClassNotFoundException e1) 
			{
				e1.printStackTrace();
			}
			catch(Exception e)
			{
				System.out.println("Failed to create DB connection!");
				System.out.println("DB Connection exception message: " + e.getMessage());
				e.printStackTrace();
			}		
					
		}
		
		return con;
	}
	
	public static void closeConnection()
	{
		try
		{
			if(con !=null)
				con.close();
			System.out.println("Connection Closed");
		}
		catch(Exception e)
		{
			System.out.println("Failed to close DB connection!");
			System.out.println("DB connection closing exception message: " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(con !=null)
					con.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}


}
