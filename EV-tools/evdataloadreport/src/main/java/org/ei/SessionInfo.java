package org.ei;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SessionInfo {
	
	String url = null;
	String driver = "oracle.jdbc.driver.OracleDriver";
	String dbUserName = "awseid";
	String dbPassword = null;
	
	String operation = null;
	String sessionName = null;
	String osUser = null;

	
	String parameterName = null;

	Connection con;
	Statement stmt =null;
	ResultSet rs = null;
	
	ArrayList <Map<String,String>> sessionInfo = new ArrayList<Map<String,String>>();
	ArrayList <Map<String,String>> sessionCount = new ArrayList<Map<String,String>>();
	
	boolean result = false;
	
	public SessionInfo()
	{
		
	}
	
	public SessionInfo(String url, String password, String op, String oracleUserName, String osUserName, String paramName)
	{
		this.url = url;
		this.dbPassword = password;
		this.operation = op;
		this.sessionName = oracleUserName;
		this.osUser = osUserName;

		
		this.parameterName = paramName;
		
		if(op.equalsIgnoreCase("all"))
		{
			fetchAllSessionInfo();
		}
		else if(op.equalsIgnoreCase("allcount"))
		{
			fetchAllSessionCountByUser();
		}
		
		else if(op.equalsIgnoreCase("usersession") && sessionName !=null && sessionName.length() >0)
		{
			fetchSessionByOracleUserName ();
		}
		
		else if(op.equalsIgnoreCase("oousersession") && sessionName !=null && sessionName.length() >0)
		{
			fetchSessionByOracleUserNameAndOsuser ();
		}
		
		else if (op.equalsIgnoreCase("openedcursor"))
		{
			fetchOpenedCursors();
		}
		else if(op.equalsIgnoreCase("showallparam"))
		{
			fetchAllParameters();
		}
		else if(op.equalsIgnoreCase("showparam") && parameterName !=null && parameterName.length() >0)
		{
			fetchParameterByParamName();
		}
		else if(op.equalsIgnoreCase("ctlfile"))
		{
			fetchCtlFile();
		}
		else if(op.equalsIgnoreCase("logfile"))
		{
			fetchLogFile();
		}
		else if(op.equalsIgnoreCase("rollbackseg"))
		{
			fetchRollbackSegment();
		}
		
		
	}

	
	
	public void fetchAllSessionInfo()
	{
		try
		{
			String query = "select sid,serial# as serial_num,username,osuser,program, machine, terminal, to_char(logon_time, 'mm-dd-yyyy HH24:MI:SS')  from v$session "+
							"order by sid";
			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
					
					//tableStructure.put(rsmdstructure.getColumnLabel(i).trim(),rsmdstructure.getColumnTypeName(i));
				}
				
				sessionInfo.add(row);
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
	
	
	public void fetchAllSessionCountByUser()
	{
		try
		{
			String query = "select nvl(username, 'Background Processes') as username, count(*) as count from v$session group by username order by username";
			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}
				
				sessionInfo.add(row);
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
	
	
	public void fetchSessionByOracleUserName()
	{
		try
		{
			
			String query = "select sid,serial# as serial_num,username,osuser,program, machine, terminal, to_char(logon_time, 'mm-dd-yyyy HH24:MI:SS') as datetime "+
							"from v$session where username='"+sessionName.toUpperCase()+"'";
			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}
				
				sessionInfo.add(row);
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
	
	

	public void fetchSessionByOracleUserNameAndOsuser()
	{
		try
		{
			
			String query = "select sid,serial# as serial_num,username,osuser,program, machine, terminal, to_char(logon_time, 'mm-dd-yyyy HH24:MI:SS') as datetime "+
							"from v$session where username='"+sessionName.toUpperCase()+"' and upper(osuser)='"+osUser.toUpperCase()+"'";
			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}
				
				sessionInfo.add(row);
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
	

	
	public void fetchOpenedCursors()
	{
		try
		{
			
			String query = "select s.username, a.value, s.sid, s.serial# as serial_num, s.OSUSER, s.PROCESS, s.MACHINE, to_char(s.LOGON_TIME, 'mm-dd-yyyy HH24:MI:SS') as datetime "+ 
							"from v$sesstat a, v$statname b, v$session s "+
							"where s.username not in ('SYSTEM','APPQOSSYS','DBSNMP','OUTLN','SYS','CTXSYS', 'RDSADMIN', 'SYSMAN') "+
							"and a.statistic# = b.statistic#  and s.sid=a.sid "+
							"and b.name = 'opened cursors current' order by s.username, a.value desc";

			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}
				
				sessionInfo.add(row);
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
	
	
	public void fetchAllParameters()
	{
		try
		{
			
			String query = "select NUM, NAME,  VALUE , description, display_value, TYPE,ISSES_MODIFIABLE from v$parameter order by NAME";

			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}
				
				sessionInfo.add(row);
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
	
	
	public void fetchParameterByParamName()
	{
		try
		{
			
			String query = "select NUM, NAME,  VALUE , description, display_value, TYPE,ISSES_MODIFIABLE from v$parameter "
					+ "where lower(NAME) like'%"+this.parameterName.toLowerCase()+"%'";

			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}
				
				sessionInfo.add(row);
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
	
	
	public void fetchCtlFile()
	{
		try
		{
			
			String query = "select name,block_size,file_size_blks,status,is_recovery_dest_file from v$controlfile";

			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}
				
				sessionInfo.add(row);
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
	
	
	public void fetchLogFile()
	{
		try
		{
			
			String query = "select member,group# as group_num,type,status,is_recovery_dest_file from v$logfile";

			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}
				
				sessionInfo.add(row);
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
	
	
	public void fetchRollbackSegment()
	{
		try
		{
			
			String query = "select d.segment_name, d.tablespace_name, s.waits, s.shrinks, s.wraps, s.status "+
							"from v$rollstat s, dba_rollback_segs d "+
							"where s.usn = d.segment_id "+
							"order by 1";

			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}
				
				sessionInfo.add(row);
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
	
	
	
	public Connection getConnection(String connectionURL, String driver, String username, String password)	
			throws Exception
			{
				Class.forName(driver);
				con = DriverManager.getConnection(connectionURL, username, password);
				return con;
			}
	
	public ArrayList <Map<String,String>> getSessionInfo()
	{
		return sessionInfo;
	}
	
	
	public boolean getKillsessionResult()
	{
		return result;
	}
}
