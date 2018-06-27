package org.ei.dataloading.knovel.loadtime;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * 
 * @author TELEBH
 * @date: 06/30/2016
 * @description: get the unique list of SubjectArea of Knovel Master to verify with Knovel Team
 */
public class KnovelUniqueSubjArea {

	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction1";
	static String password = "ei3it";
	static int loadNumber = 0;
	static String tableName="knovel_master";
	
	public static final char AUDELIMITER = (char) 30;
	
	HashSet<String> subjectAreas = new HashSet<String>();
	
	
	public static void main(String[] args) throws Exception{

		if(args.length >5)
		{
			if(args[0] !=null)
			{
				url = args[0];
				System.out.println("url: " + url);
			}
			if(args[1] !=null)
			{
				driver = args[1];
				System.out.println("driver: " + driver);
			}
			if(args[2] !=null)
			{
				username = args[2];
				System.out.println("username: " + username);
			}
			if(args[3] !=null)
			{
				password = args[3];
			}
			if(args[4] !=null && args[4].length() >0)
			{
				if(Pattern.matches("^\\d*$", args[4]))
				{
					loadNumber = Integer.parseInt(args[4]);
					System.out.println("loadnumber: " +  loadNumber); 
				}
				else
				{
					System.out.println("loadNumber has wrong format");
					System.exit(1);
				}

			}
			if(args[5] !=null)
			{
				tableName = args[5];
				System.out.println("tablename: " + tableName);
			}
			
		}
		else
		{
			System.out.println("not enough paremetrs");
			System.exit(1);
		}
		KnovelUniqueSubjArea c = new KnovelUniqueSubjArea();
		Connection con = c.getConnection(url,driver,username,password);
		
		if(loadNumber ==1)
		{
			c.writeCombinedByTable(con);
		}
		else
		{
			c.writeCombinedByWeekNumber(con);
		}

	}
	
	public void writeCombinedByTable(Connection con) throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");
			String query = "select accessnumber,subject from " +  tableName + " where database='knc'";
			System.out.println(query);

			rs = stmt.executeQuery(query);

			System.out.println("Got records... from table: " + tableName);
			getSubjectArea(rs,con);
			System.out.println("Wrote records.");
		}

		finally
		{
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
		}
	}


	public void writeCombinedByWeekNumber(Connection con) throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");
			String query = "select accessnumber,subject from " +  tableName + " where loadnumber=" + loadNumber + " and database='knc'";
			System.out.println(query);

			rs = stmt.executeQuery(query);

			System.out.println("Got records... from table: " + tableName);
			getSubjectArea(rs,con);
			System.out.println("Wrote records.");
		}

		finally
		{
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
		}

	}


	public void getSubjectArea(ResultSet rs, Connection con) throws Exception
	{	
		String subjArea;
		StringTokenizer tokens;
		while(rs.next())
		{
			try
			{
				if(rs.getString("SUBJECT") !=null)
				{
					tokens = new StringTokenizer(rs.getString("SUBJECT"), String.valueOf(AUDELIMITER));
					while(tokens.hasMoreTokens())
					{
						subjArea = tokens.nextToken();
						
						if(!(subjectAreas.contains(subjArea)))
						{
							subjectAreas.add(subjArea);
						}
					}
				}
			}
			catch(SQLException ex)
			{
				System.out.println("Error Occurred reading from ResultSet for Accessnumber: " + rs.getString("ACCESSNUMBER") + " ... " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		
		System.out.println("Total Unique SubjectAreas Count: " +  subjectAreas.size());
		
		writeSubjectAreas();
		
	}
	
	public void writeSubjectAreas()
	{
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new File("knovel_subjectareas_" + loadNumber+".txt"));
			if(subjectAreas.size() >0)
			{
				for(String subject: subjectAreas)
				{
					out.println(subject);
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
	protected Connection getConnection(String connectionURL,
			String driver,
			String username,
			String password)
					throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,
				username,
				password);
		return con;
	}


}
