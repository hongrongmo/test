package org.ei.dataloading;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckUTFChar {

	private static Connection con = null;
	private static Statement stmt = null;
	private static ResultSet rs = null;
	
	
	static String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction";
	static String password = "ei3it";
	
	static String query;
	static String tableName;
	static int loadNumber=0;
	static int updateNumber=0;
	static String database;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if(args[0]!=null)
		{
			database=args[0];
		}
		
		if(args[1]!=null)
		{
			loadNumber= loadNumber;
		}
		CheckDBUTFChar();
	}
	


	public static void CheckDBUTFChar()  
	{
		
		try
		{
			query = "select * from " + tableName + " where loadnumber=" + loadNumber + " and database=" + database;
			con = getConnection(connectionURL,driver,username,password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	private static Connection getConnection(String connectionURL,String driver, String username, String password)
			throws Exception
			{
				Class.forName(driver);
				
				Connection con = DriverManager.getConnection(connectionURL, username, password);
				
				return con;
			}
			

}
