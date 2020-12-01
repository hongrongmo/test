package org.ei.dataloading.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author TELEBH
 * @Date: 11/0/2020
 * @Description: DB connection class to be shared with multiple classes, instead of create dbconnection to each individual class
 */
public class DBConnection {

	@SuppressWarnings("finally")
	public Connection getConnection(String url, String driver, String username, String password) {
		Connection con = null;
		try {
			System.out.println("connectionUrl= " + url);
			System.out.println("driver= " + driver);
			System.out.println("username= " + username);
			
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
		}
		catch(SQLException ex)
		{
			System.out.println("Error creating Oracle Connection!!!");
			System.out.println(ex.getMessage());
			System.out.println("Exit....");
			
			/* since everything after fetching data from shared search API depends on oracle connection, so if exception happened exit */
			System.exit(1);
		}
		catch (ClassNotFoundException e) {
			System.out.println("OracleDriver class not found!!!");
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			return con;
		}
		
		
	}


}
