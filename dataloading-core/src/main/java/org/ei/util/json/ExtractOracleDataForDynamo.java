package org.ei.util.json;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ei.util.db.DbConnection;

/**
 * 
 * @author TELEBH
 * @Date: 02/04/2021
 * @Description: Extract Existing Oracle DB data/columns into JSON files for BatchWrite into DynamoDB
 * for the POC for migration out of Oracle by storing XML files from source vendor into S3 bucket &
 * Using DynamoDB for Metadata
 */

public class ExtractOracleDataForDynamo {
	String url = "jdbc:oracle:thin:@localhost:1521:eid";
	String driver = "oracle.jdbc.driver.OracleDriver";
	String userName;
	String password;
	String database;
	String loadNumber;
	
	
	Connection con = null;
	
	public static void main(String[] args)
	{
		if(args != null && args.length >0)
		{
			ExtractOracleDataForDynamo obj = new ExtractOracleDataForDynamo();
			obj.run(args);
		}
	}
	
	public void run(String[] args)
	{
		if(args.length >2)
		{
			if(args[0] != null && !args[0].isBlank())
			{
				database = args[0];
				System.out.println("Database: " + database);
			}
			if(args[1] != null && !args[1].isBlank())
			{
				userName = args[1];
				System.out.println("UserName: " + userName);
			}
			if(args[2] != null && !args[2].isBlank())
			{
				password = args[2];
			}
			
		}
		if(args.length >4)
		{
			if(args[3] != null && !args[3].isBlank())
			{
				url = args[2];
				System.out.println("URL: " + url);
			}
			if(args[4] != null && !args[4].isBlank())
			{
				driver = args[3];
				System.out.println(driver);
			}
		}
		if(args.length >5)
		{
			if(args[5] != null && !args[5].isBlank())
			{
				if(args[5].matches("[0-9]+"))
				{
					loadNumber = args[5];
					System.out.println("LoadNumber: " + loadNumber);
				}
			}
		}
		else
		{
			System.out.println("Not enough parameters!!!!!");
			System.out.println("Rerun the process with userName, password and optional url & driver");
			System.exit(1);
		}
		
		ReadDataFromOracle();
	}
	
	/** 
	 * Initializer
	 */
	public void init()
	{
		try
		{
			con = DbConnection.getConnection(url, driver, userName, password);
		}
		catch(Exception ex)
		{
			System.err.println("SQL Connection Exception!!!!");
			System.err.println(ex.getMessage());
			ex.printStackTrace();
		}
		
	}
	
	public void ReadDataFromOracle()
	{
		ResultSet rs = null;
		PreparedStatement stmt = null;
		String query;
		try
		{
			if(con != null)
			{
				if(database != null && !database.isBlank())
				{
					if(loadNumber != null)
					{
						if(database.equalsIgnoreCase("cpx") || database.equalsIgnoreCase("geo")
								|| database.equalsIgnoreCase("pch") || database.equalsIgnoreCase("elt")
								|| database.equalsIgnoreCase("chm"))
						{
							
								query = "select m_id, pui, accessNumber, loadnumber, doi from bd_master where database=? and loadnumber=?";
								stmt = con.prepareStatement(query);
								stmt.setString(0, database);
								stmt.setString(0, loadNumber);
						}
							
						else if(database.equalsIgnoreCase("ins"))
						{
							query = "select m_id, pui, anum, load_number, doi from ins_master where load_number=?";
							stmt = con.prepareStatement(query);
							stmt.setString(0, loadNumber);
						}
						else if(database.equalsIgnoreCase("ibf"))
						{
							query = "select m_id, pui, accessNumber, load_number, doi from ins_master where load_number=?";
							stmt = con.prepareStatement(query);
							stmt.setString(0, loadNumber);
						}
					}
					
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Exception Reading Data from oracle!!!!");
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
}
