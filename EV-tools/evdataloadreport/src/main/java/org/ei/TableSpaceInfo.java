package org.ei;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TableSpaceInfo {

	Connection con;
	Statement stmt =null;
	ResultSet rs =null;
	
	Statement stmt2 =null;
	ResultSet rs2 =null;
	
	
	Statement stmt3 =null;
	ResultSet rs3 =null;
	
	
	
	ArrayList <Map<String,String>> ts = new ArrayList<Map<String,String>>();
	ArrayList <Map<String,String>> ts2 = new ArrayList<Map<String,String>>();
	String totalSum = null;
	
	public TableSpaceInfo(){
	}
		
	public TableSpaceInfo(int rdsId){
		tableSpace(rdsId);
	}
	
	public void tableSpace(int rdsId)
	{
		
		String url = null;
		String dbpassword = null;

		String[] rdsInfo = RdsMapping.multipleRdsMapping(rdsId);
		/*switch(rdsId)
		{
			case 1: 
				//url = "jdbc:oracle:thin:@localhost:15210:eia";  // for localhost testing
				url = "jdbc:oracle:thin:@eia.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eia";
				dbpassword = "";
				break;
			case 2:
				//url = "jdbc:oracle:thin:@localhost:15211:eib";  // for localhost
				url = "jdbc:oracle:thin:@eib.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eib";
				dbpassword = "";
				break;
			case 3: 
				url = "jdbc:oracle:thin:@localhost:15212:eid";   // for localhost
				//url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
				dbpassword = "";
				break;
			default:
				//url = "jdbc:oracle:thin:@localhost:15212:eid";   // for localhost
				url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
				break;
		}
		*/
		
		if(rdsInfo !=null && rdsInfo.length ==2)
		{
			url = rdsInfo[0];
			dbpassword = rdsInfo[1];
		}
		
		String driver = "oracle.jdbc.driver.OracleDriver";
		String dbuserName = "awseid";
		
		String query="select dd.tablespace_name,("+
		        "sum(distinct(dd.bytes||'.'||lpad(dd.file_id,10,'0'))) -"+
		        "sum(distinct('.'||lpad(dd.file_id,10,'0')))"+
		        ")/1024/1024/1024  \"Free_GB\","+
		        "round(sum(nvl(df.bytes,0))/1024/1024/1024) \"Free_GB_1\","+
		        "round( max(nvl(df.bytes,0))/1024/1024) \"Max_Free_MB\","+
		        "trunc(10000*sum(nvl(df.bytes,0)) /"+
		        "(sum(distinct(dd.bytes||'.'||lpad(dd.file_id,10,'0'))) -"+
		         "sum(distinct('.'||lpad(dd.file_id,10,'0')))))/100 \"Free_Percentage\" "+
		         "from "+
		         "dba_free_space df, "+
		         "dba_data_files dd "+
		         "where dd.tablespace_name = df.tablespace_name (+) "+
		         "and dd.tablespace_name not in ('SYSTEM','SYSAUX','UNDO_T1','USERS', 'RDSADMIN') "+
		         "and dd.file_id = df.file_id (+) "+
		         "group by dd.tablespace_name order by \"Free_Percentage\" desc";
		
		
		String query2 = "select dd.tablespace_name,("+
						"sum(distinct(dd.bytes||'.'||lpad(dd.file_id,10,'0'))) -"+
						"sum(distinct('.'||lpad(dd.file_id,10,'0')))"+
						")/1024/1024  \"Free_MB\","+
						"round(sum(nvl(df.bytes,0))/1024/1024) \"Free_MB_1\","+
						"round( max(nvl(df.bytes,0))/1024/1024) \"Max_Free_MB\","+
						"trunc(10000*sum(nvl(df.bytes,0)) /"+
						"(sum(distinct(dd.bytes||'.'||lpad(dd.file_id,10,'0'))) -"+
						"sum(distinct('.'||lpad(dd.file_id,10,'0')))))/100 \"Free_Percentage\" "+
						"from "+
						"dba_free_space df,"+
						"dba_data_files dd "+
						"where dd.tablespace_name = df.tablespace_name (+) "+
						"and dd.tablespace_name in ('SYSTEM','SYSAUX','UNDO_T1','USERS', 'RDSADMIN','TEMP') "+
						"and dd.file_id = df.file_id (+)"+
						"group by dd.tablespace_name order by \"Free_Percentage\" desc";
		
		//String query3 = "select sum(bytes)/1024/1024/1024 from dba_data_files";  // original free space, not including temp tablespace
		
		String query3 = "select (select sum(bytes)/1024/1024/1024 from dba_data_files) + (select BYTES/1024/1024/1024 from dba_temp_files) from dual";
		
		
		
		try
		{
			con = getConnection(url, driver, dbuserName,dbpassword);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			
			// 2nd query of TS 
			stmt2 = con.createStatement();
			rs2 = stmt2.executeQuery(query2);
			ResultSetMetaData rsmd2 = rs2.getMetaData();
			
			// 3rd query of TS
			stmt3 = con.createStatement();
			rs3 = stmt3.executeQuery(query3);
			
			
			while(rs.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd.getColumnCount();i++)
				{	
					row.put(rsmd.getColumnLabel(i).trim(),rs.getString(i));
				}
				
				
				ts.add(row);

			}
			
			
			while(rs2.next())
			{
				Map <String,String>row = new HashMap<String,String>();
				
				for(int i=1;i<=rsmd2.getColumnCount();i++)
				{	
					row.put(rsmd2.getColumnLabel(i).trim(),rs2.getString(i));
				}
				
				
				ts2.add(row);
				
			}
			
			
			while(rs3.next())
			{
				totalSum = rs3.getString(1);
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
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if(stmt2 !=null)
			{
				try
				{
					stmt2.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if(rs2 !=null)
			{
				try
				{
					rs2.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if(stmt3 !=null)
			{
				try
				{
					stmt3.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if(rs3 !=null)
			{
				try
				{
					rs3.close();
				}
				catch (Exception e)
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
	
	public ArrayList<Map<String,String>> getTableSpace() throws Exception
	{
		return ts;
		
	}
	
	public ArrayList<Map<String,String>> getTableSpace2() throws Exception
	{
		return ts2;
		
	}
	
	public String getTableSpace3() throws Exception
	{
		return totalSum;
		
	}
	
	
			
}
