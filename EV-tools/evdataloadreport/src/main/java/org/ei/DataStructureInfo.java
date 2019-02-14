package org.ei;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.io.*;


/*
 * Desc: retrive data structure for master tales , 
 *  i.e. tablestructure
 *  indexes
 *  constraints
 *  
 *  for column name "COLUMN_EXPRESSION" it raise "java.sql.SQLException: Stream has already been closed"
 *  if used rs.getstring(columnindex), because it the column type other than the Long or Long Raw column before reading and closing the stream that the Oracle JDBC driver creates for the Long or Long Raw column data. 
 *  This can be done using the getBinaryStream or getAsciiStream methods on the ResultSet instead of getString(columnindex) 
 * */

public class DataStructureInfo {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Connection con;
	Statement stmtStructure =null;
	Statement stmtIndex =null;
	Statement stmtConstraint =null;
	
	
	ResultSet structureRs =null;
	ResultSet indexesRs =null;
	ResultSet constraintsRs =null;
	
	String url = "";
	String driver = "oracle.jdbc.driver.OracleDriver";
	String password = "";
	
	StringBuffer column_Expression;
	StringBuffer search_condition;
	
	 //Map<String,String> tableStructure = new HashMap <String,String>();
	ArrayList <Map<String,String>> tableStructure = new ArrayList<Map<String,String>>();
	 ArrayList <Map<String,String>> tableIndexes = new ArrayList<Map<String,String>>();
	 ArrayList <Map<String,String>> tableConstraints = new ArrayList<Map<String,String>>();
	
	public DataStructureInfo()
	{
		
	}
	
	public DataStructureInfo(int database, String schema, String tableName)
	{
		fetchTableStructure(database,schema,tableName);
	}
	public void fetchTableStructure(int rds, String schema, String tableName)
	{
		/*mapUrl(rds);*/
		url = RdsMapping.mapUrl(rds);
		
		if(schema !=null && tableName !=null)
		{
			/*String structureQuery = "select * from " + tableName + " where rownum=1";*/
			String structureQuery = " select COLUMN_ID, COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE, DATA_DEFAULT from "+
									" user_TAB_COLUMNS where table_name ='"+tableName.toUpperCase()+"' order by COLUMN_ID";
			
			/*String indexesQuery = "select a.table_owner,a.index_name,a.uniqueness,a.status,a.index_type,a.temporary,a.partitioned,a.funcidx_status, a.join_index,"+
									" b.column_name, c.column_expression "+
									"from user_indexes a, USER_IND_COLUMNS b,USER_IND_EXPRESSIONS c  where "+
									" a.INDEX_NAME = b.INDEX_NAME and a.INDEX_NAME = c.INDEX_NAME (+) and a.table_name='"+tableName.toUpperCase()+"' order by a.index_name desc";
			*/
			
			String indexesQuery = "select a.table_owner,a.index_name,a.uniqueness,a.status,a.index_type,a.partitioned,a.funcidx_status, a.join_index,"+
								  "b.column_name, c.column_expression "+
								  "from user_indexes a, "+
								  "(select index_name,listagg(COLUMN_NAME||',') WITHIN GROUP (ORDER BY column_position) column_name "+
								  "from USER_IND_COLUMNS "+
								  "where table_name='"+tableName.toUpperCase()+"' group by index_name) b,USER_IND_EXPRESSIONS c  where "+
								  "a.INDEX_NAME = b.INDEX_NAME and a.INDEX_NAME = c.INDEX_NAME (+) and a.table_name='"+tableName.toUpperCase()+"' order by a.index_name desc";
			
			String constraintsQuery = "select owner,constraint_name,constraint_type,search_condition,status,generated,"+
										"last_change,index_name,index_owner from user_constraints where table_name='"+tableName.toUpperCase()+"' order by constraint_name";
			

			if(schema.toLowerCase().contains("ap_"))
			{
				password = "ei3it";
			}
			else if(schema.toLowerCase().contains("ba_s300"))
			{
				password = "ei7it";
			}
			else if(schema.toLowerCase().contains("db") || schema.toLowerCase().contains("ba_loading"))
			{
				password = "ny5av";
			}
		
			try
			{
				// get password based on schema name
				
				
				con = getConnection(url, driver, schema,password);
				
				// desc table
				stmtStructure = con.createStatement();
				structureRs = stmtStructure.executeQuery(structureQuery);
				ResultSetMetaData rsmdstructure = structureRs.getMetaData();
				
				// tables indexes 
				stmtIndex = con.createStatement();
				indexesRs = stmtIndex.executeQuery(indexesQuery);
				ResultSetMetaData rsmdIndexes = indexesRs.getMetaData();
				
				
				// table constraints
				stmtConstraint = con.createStatement();
				constraintsRs = stmtConstraint.executeQuery(constraintsQuery);
				ResultSetMetaData rsmdConstraints = constraintsRs.getMetaData();
				
				while(structureRs.next())
				{	
					Map <String,String>row = new HashMap<String,String>();
					
					for(int i=1;i<=rsmdstructure.getColumnCount();i++)
					{	
						row.put(rsmdstructure.getColumnLabel(i).trim(),structureRs.getString(i));
						
						//tableStructure.put(rsmdstructure.getColumnLabel(i).trim(),rsmdstructure.getColumnTypeName(i));
					}
					
					tableStructure.add(row);

				}
				
				
				
				
				while(indexesRs.next())
				{
					Map <String,String>row = new HashMap<String,String>();
					
					for(int i=1;i<=rsmdIndexes.getColumnCount();i++)
					{	
						
						
						if(rsmdIndexes.getColumnLabel(i).equalsIgnoreCase("COLUMN_EXPRESSION"))
						{
							column_Expression = new StringBuffer();
							if((InputStream)indexesRs.getBinaryStream(i) !=null)
							{
								InputStream stream = (InputStream)indexesRs.getBinaryStream(i);
								int k = stream.read();
								while(k>0)
								{
									column_Expression.append((char)k);
									k = stream.read();
								}
								stream.close();

							}
							else
							{
								column_Expression = new StringBuffer();
								column_Expression.append("(null)");
							}
							row.put(rsmdIndexes.getColumnLabel(i).trim(),column_Expression.toString());
							
						}
						else if (rsmdIndexes.getColumnLabel(i).equalsIgnoreCase("COLUMN_NAME") && indexesRs.getString(i) !=null
								&& indexesRs.getString(i).contains(","))
						{
							row.put(rsmdIndexes.getColumnLabel(i).trim(),indexesRs.getString(i).substring(0, indexesRs.getString(i).lastIndexOf(",")));
						}
						else
						{
							row.put(rsmdIndexes.getColumnLabel(i).trim(),indexesRs.getString(i));
						}
						
					}
					
					
					tableIndexes.add(row);

				}
				
				
				
				while(constraintsRs.next())
				{
					Map <String,String>row = new HashMap<String,String>();
					
					for(int i=1;i<=rsmdConstraints.getColumnCount();i++)
					{	
						if(rsmdConstraints.getColumnLabel(i).equalsIgnoreCase("SEARCH_CONDITION"))
						{
							search_condition = new StringBuffer();
							
							if((InputStream)constraintsRs.getBinaryStream(i) !=null)
							{
								InputStream stream = (InputStream)constraintsRs.getBinaryStream(i);
								int k = stream.read();
								while(k>0)
								{
									search_condition.append((char)k);
									k = stream.read();
								}
								
								stream.close();
							}
							else 
							{
								search_condition = new StringBuffer();
								search_condition.append("(null)");
							}
							
							row.put(rsmdConstraints.getColumnLabel(i).trim(),search_condition.toString());
							
						}
						
						else
						{
							row.put(rsmdConstraints.getColumnLabel(i).trim(),constraintsRs.getString(i));
						}
							
					}

					tableConstraints.add(row);
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
				
				if(stmtStructure !=null)
				{
					try
					{
						stmtStructure.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				if(stmtIndex !=null)
				{
					try
					{
						stmtIndex.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				if(stmtConstraint !=null)
				{
					try
					{
						stmtConstraint.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
				
				if(structureRs !=null)
				{
					try
					{
						structureRs.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				
				if(indexesRs !=null)
				{
					try
					{
						indexesRs.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				
				if(constraintsRs !=null)
				{
					try
					{
						constraintsRs.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				
			}
			
		}
		
		else
		{
			System.out.println("some info are missing, please check");
		}
	}
	
	/*public String mapUrl(int rds)
	{
		if(rds >0 )
		{
			switch (rds) {
			case 1:
				url = "jdbc:oracle:thin:@eia.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eia";  // for deplyment
				//url = "jdbc:oracle:thin:@localhost:15210:eia";   // for localhost testing
				break;
			case 2:
				url = "jdbc:oracle:thin:@eib.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eib";
				//url = "jdbc:oracle:thin:@localhost:15211:eib";   // for localhost testing
				break;
			case 3:
				url="jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
				//url = "jdbc:oracle:thin:@localhost:15212:eid";   // for localhost testing
				break;

			default:
				url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
				//url = "jdbc:oracle:thin:@localhost:15212:eid";   // for localhost testing
				break;
			}
		}
		
		return url;
	}
	
	public String mapPassword(int rds)
	{
		if(rds >0)
		{
			switch(rds) {
			case 1:
				password = "Awseia123";
				break;
			case 2: 
				password = "Awseib123";
				break;
			case 3:
				password = "Awseid123";
				break;
			default:
				password = " Awseid123";
				break;
			}
		
			}
		return password;
	}
	*/
	public Connection getConnection(String connectionURL, String driver, String username, String password)	
			throws Exception
			{
				Class.forName(driver);
				con = DriverManager.getConnection(connectionURL, username, password);
				return con;
			}
			
		public ArrayList<Map<String,String>> getTableStructure() throws Exception
			{
				return tableStructure;
				
			}
		
		public ArrayList<Map<String,String>> getTableindexes() throws Exception
		{
			return tableIndexes;
			
		}
		
		public ArrayList<Map<String,String>> getTableConstraints() throws Exception
		{
			return tableConstraints;
			
		}
		
		
		
}
