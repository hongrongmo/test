package org.ei;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;



public class SchemaTableInfo {
	
	String driver = "oracle.jdbc.driver.OracleDriver";
	String dbUserName = "awseid";
	
	
	Connection con;
	Statement stmtSchema =null;
	Statement stmtSchemaTable =null;
	
	ResultSet schemaRs = null;
	ResultSet schemaTableRs = null;


	ArrayList<String> schemaList = new ArrayList<String>();
	ArrayList<Map<String,String>> schemaTableList = new ArrayList<Map<String,String>>();
	
	HashMap<String,String> combinedSchemaTablesList = new HashMap<String, String>();
	
	
	public SchemaTableInfo()
	{
		
	}

	public SchemaTableInfo(String url, String password)
	{
		fetchSchemaTable(url,password);
	}
	
	public void fetchSchemaTable(String url, String password)
	{
		if(url !=null && password !=null)
		{
			
			String schemasQuery = "select distinct owner from dba_tables where owner NOT IN "+
								  "('SYSTEM', 'SYS', 'SYSMAN', 'RDSADMIN', 'OUTLN','CTXSYS', 'APPQOSSYS','AWSEID','DBSNMP','AP_GV_SEARCH') "+
								  "order by owner";
			
			String schemaAndTablesQuery = "select owner, TABLE_NAME from dba_tables where owner NOT IN "+
										  "('SYSTEM', 'SYS', 'SYSMAN', 'RDSADMIN', 'OUTLN','CTXSYS', 'APPQOSSYS','AWSEID','DBSNMP','AP_GV_SEARCH') "+ 
										  "and upper(TABLE_NAME) not like 'W%' "+
										  "group by owner,table_name "+
										  "order by owner, TABLE_NAME";
			
			StringBuffer tables = new StringBuffer();
			
			try
			{
				con = getConnection(url, driver, dbUserName,password);
				
				
				// get the Schema List
				stmtSchema = con.createStatement();
				schemaRs = stmtSchema.executeQuery(schemasQuery);
				
				
				//get the schema list with their tables
				stmtSchemaTable = con.createStatement();
				schemaTableRs = stmtSchemaTable.executeQuery(schemaAndTablesQuery);
				
				
				
				if(schemaRs !=null)
				{
					while(schemaRs.next())
					{
						schemaList.add(schemaRs.getString(1));
					}
					
				}
				
				if(schemaTableRs != null)
				{
					while(schemaTableRs.next())
					{
						Map<String,String> row = new HashMap<String,String>();
						row.put(schemaTableRs.getString(1), schemaTableRs.getString(2));
						schemaTableList.add(row);
					}
				}
						
				
				// combine tables belong to one schema
			
				for(int i = 0; i<schemaList.size();i++)
				{
					for(int j =0 ;j<schemaTableList.size();j++)
					{
						Map<String,String> row = schemaTableList.get(j);
						
						String key = (String)row.keySet().toString();
						String keyValue = key.substring(1,key.length()-1);
						
						
						if(keyValue.equalsIgnoreCase(schemaList.get(i)))
						{
							if(tables.length() > 0)
							{
								tables.append(",");
							}
							tables.append(schemaTableList.get(j).get(keyValue));
						}
					}
					//System.out.println(schemaList.get(i)+": " + tables.toString());
					combinedSchemaTablesList.put(schemaList.get(i), tables.toString());
					
					//reset stringbuffer
					tables.setLength(0);
					
				}
			}  // end of try
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
				
				if(stmtSchema !=null)
				{
					try
					{
						stmtSchema.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				if(stmtSchemaTable !=null)
				{
					try
					{
						stmtSchemaTable.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				
				if(schemaRs !=null)
				{
					try
					{
						schemaRs.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				
				if(schemaTableRs !=null)
				{
					try
					{
						schemaTableRs.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
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
	
	public HashMap<String, String> getSchemaTables()
	{
		return combinedSchemaTablesList;
	}
	
}
