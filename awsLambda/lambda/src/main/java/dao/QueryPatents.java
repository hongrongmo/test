package dao;

import org.apache.log4j.Logger;
import org.ei.dataloading.db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @author TELEBH
 * @Date: 03/22/2022
 * @Description: DAO class to query data from database
 */
public class QueryPatents {
	
	Logger logger;
	
	Connection con = null;
	PreparedStatement stmt;
	ResultSet rs;
	
	public QueryPatents()
	{
		
	}
	public void init(String url, String driver, String username, String password)
	{
		logger= Logger.getLogger(QueryPatents.class);
		con = DBConnection.getInstance(url, driver, username, password).getDbConnection();
	}
	public ResultSet fetchPatents(int loadNumber)
	{
		try 
		{
			System.out.println("Time before query UPT_MASTER: " + System.currentTimeMillis());
			String query = "select AC||PN||KC from upt_master where load_number=?";
			stmt = con.prepareStatement(query);
			stmt.setInt(1, loadNumber);
			rs = stmt.executeQuery();
			System.out.println("Time after query UPT_MASTER: " + System.currentTimeMillis());
		} 
		catch (SQLException e) 
		{
			logger.error("Something went wrong in querying patents in DAO class!!!!");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return rs;
		
	}
	
	public void closeResultSet()
	{
		try
		{
			if(rs != null)
			{
				rs.close();
			}
		}
		catch(SQLException e)
		{
			logger.error("Something went wrong in closing ResultSet in DAO class!!!!");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e)
		{
			logger.error("Something went wrong in closing ResultSet in DAO class!!!!");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
