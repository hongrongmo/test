package cafe;

import jar.diff_match_patch;
import jar.diff_match_patch.Diff;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;

public class CafeBdDoiUpdate {

	String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";
	String driver = "oracle.jdbc.driver.OracleDriver";
	String username = "db_cafe";
	String password = "";
	
	
	public static void main(String[] args) 
	{
		CafeBdDoiUpdate obj = new CafeBdDoiUpdate();
		try
		{
			obj.getDoi();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		

	}

	public void getDoi() throws Exception
	{
		Connection con;
		Statement stmt = null;
		ResultSet rs = null;

		try
		{

			con = getConnection(connectionURL, driver, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery("select an,cafe_doi,bd_doi from HH_DOI_QUOTE");
			processRecs(rs,con);

		}
		finally
		{

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (stmt != null)
			{
				try
				{
					stmt.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}
	
	
	public void processRecs(ResultSet rs, Connection con)
	{
		System.out.println("do nothing for now");

		diff_match_patch dmp = new diff_match_patch();
		PrintWriter out = null;
		LinkedList<Diff> DiffList;

		String an = null;
		String cafe_doi = null;
		String bd_doi = null;
		
		boolean res = false;
		
		int lengthDiff = 0;
		try
		{
			out = new PrintWriter(new File("cafe_bd_doi.txt"));
			
			String updateTable = "update HH_DOI_QUOTE set cafe_doi = ?, bd_doi = ? where an = ?";
			PreparedStatement prepareStmt = getConnection(connectionURL, driver, username, password)
					.prepareStatement(updateTable);
			
			
			while(rs.next())
			{
				if(rs.getString("AN") !=null && (rs.getString("CAFE_DOI") !=null || rs.getString("BD_DOI") !=null))
				{
					an = rs.getString("AN");
					cafe_doi = rs.getString("CAFE_DOI").trim();
					bd_doi = rs.getString("BD_DOI").trim();
					
					
					prepareStmt.setString(1, cafe_doi);
					prepareStmt.setString(2, bd_doi);
					prepareStmt.setString(3, an);
					
					res = prepareStmt.execute();
					
					System.out.println("update statement : " + res);
					System.out.println("execution count: " + prepareStmt.getUpdateCount());
					
					
					System.out.println(cafe_doi);
					System.out.println(bd_doi);
					out.println("");
				}
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
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}

	
	
	public Connection getConnection(String connectionURL,String driver,String username,String password)	throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,username,password);
		return con;
	}
	
}
