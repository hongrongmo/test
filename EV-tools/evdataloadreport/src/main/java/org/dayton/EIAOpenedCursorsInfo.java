package org.dayton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.ei.RdsMapping;

public class EIAOpenedCursorsInfo {


	String url = null;
	String driver = "oracle.jdbc.driver.OracleDriver";
	String dbUserName = "dayton_tm";
	String dbPassword = "tm1only";

	String operation = null;
	String sessionName = null;
	String osUser = null;


	String parameterName = null;

	Connection con;
 	PreparedStatement stmt =null;
	ResultSet rs = null;
	
	
	String query = null;
	String infoType = "";
	int sid = 0;
	
	
	ArrayList <Map<String,String>> cursorInfo = new ArrayList<Map<String,String>>();
	LinkedList<String> columnNames = new LinkedList<String>();

	
	public EIAOpenedCursorsInfo(String infoType)
	{
		this.infoType = infoType;
		fetchOpenedCursors();
		
	}
	
	public EIAOpenedCursorsInfo(String infoType, int sid)
	{
		this.infoType = infoType;
		this.sid = sid;
		fetchOpenedCursors();
		
	}
	
	/*public EIAOpenedCursorsInfo(String infoType, String query)
	{
		this.infoType = infoType;
		this.query = query;
		fetchOpenedCursors();
		
	}
	*/
	
	public void fetchOpenedCursors()
	{	
		try
		{

			if(infoType.equalsIgnoreCase("summary"))
			{
				query = "select s.username, a.value count, s.sid from v$sesstat a, v$statname b, v$session s"+
						" where s.username like 'AP%' and s.username !='APPQOSSYS' and a.statistic# = b.statistic# and s.sid=a.sid"+
						" and b.name = 'opened cursors current' order by s.username,a.value desc";


			}
			else if(infoType.equalsIgnoreCase("detailed"))
			{
				query = "select s.username, a.value count, s.sid, s.serial# as serial_num, s.OSUSER, s.PROCESS, s.MACHINE, to_char(s.LOGON_TIME, 'mm-dd-yyyy HH24:MI:SS') as datetime "+ 
						"from v$sesstat a, v$statname b, v$session s "+
						"where s.username not in ('SYSTEM','APPQOSSYS','DBSNMP','OUTLN','SYS','CTXSYS', 'RDSADMIN', 'SYSMAN') "+
						"and a.statistic# = b.statistic#  and s.sid=a.sid "+
						"and b.name = 'opened cursors current' order by s.username desc";

			}
			
			else if(infoType.equalsIgnoreCase("custom") && sid != 0)
			{
				query = "select  sid ,sql_text,  USER_NAME from v$open_cursor where sid=" + sid + " order by USER_NAME";
			}
			
			
			url = RdsMapping.mapUrl(1);
			System.out.println("URL Mapping value is: " + url);

			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.prepareStatement(query);
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();

			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();

				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}

				cursorInfo.add(row);
			}
			
			System.out.println("Columns COunt is : " + rsmd.getColumnCount());
			for(int j=1;j<=rsmd.getColumnCount();j++)
			{	
				columnNames.add(rsmd.getColumnName(j));
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
		}

	}


	protected Connection getConnection(String connectionURL, String driver, String username, String password)
			throws Exception
	{
		Class.forName(driver);

		Connection con = DriverManager.getConnection(connectionURL, username, password);

		return con;
	}

	public ArrayList <Map<String,String>> getCursorInfo()
	{
		return cursorInfo;
	}	
	
	public LinkedList<String> getColumnNames()
	{
		return columnNames;
	}

}
