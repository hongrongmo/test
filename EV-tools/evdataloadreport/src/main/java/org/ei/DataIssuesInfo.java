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

public class DataIssuesInfo {

	String category = null;
	String issue_Title = null;
	
	String url = null;
	//String url = "jdbc:oracle:thin:@localhost:15212:eid";   // for local testing
	//String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";   // for deployment
	String driver = "oracle.jdbc.driver.OracleDriver";
	//String dbUserName = "ap_correction1";
	String dbUserName = "ap_report";
	String dbPassword = "";
	

	Connection con;
	Statement stmt =null;
	ResultSet rs = null;
	
		
	Statement addStmt =null;

	
	
	ArrayList <Map<String,String>> issuesInfo = new ArrayList<Map<String,String>>();
	Map<String,String> updateInfoList = new  HashMap<String, String>();
	Map<String,String> addInfoList = new  HashMap<String, String>();
	
	
	
	public DataIssuesInfo()
	{
		
	}
	
	public DataIssuesInfo(String category, String issueTitle)
	{
		this.category = category.toLowerCase().trim();
		this.issue_Title = issueTitle.toLowerCase().trim();
		
		if(category !=null && issueTitle !=null)
		{
			fetchDataIssueInfo();
		}
	}
	public DataIssuesInfo(Map<String,String> updateList, String category, String issueTitle)
	{
		this.updateInfoList = updateList;
		this.category = category.toLowerCase().trim();
		this.issue_Title = issueTitle.toLowerCase().trim();
		
	}
	
	public DataIssuesInfo(Map<String,String> addList)
	{
		this.addInfoList= addList;
		
	}
	
	
	
public void fetchDataIssueInfo()
{
	try
	{
		String query = "select * from dataload_issues where lower(category) = '"+ this.category+"' and "+
						"lower(title) = '" + this.issue_Title+"'";
		
			
		url = RdsMapping.singleRdsMapping();
		
		
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
			
			issuesInfo.add(row);
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

public int updateIssue()
{
	int status = -1;
	
	if(this.updateInfoList !=null && this.updateInfoList.size() >0 && (this.category !=null && this.issue_Title !=null))
	{
		try
		{
			String update = "update dataload_issues set category='" + updateInfoList.get("CATEGORY")+"' , title='" + updateInfoList.get("TITLE")+"' ,"+
					"type='"+updateInfoList.get("TYPE")+"' , priority='" + updateInfoList.get("PRIORITY")+"' , labels='"+updateInfoList.get("LABELS")+"' , "+
					"epic_name='" + updateInfoList.get("EPIC_NAME")+"' , sprint='" + updateInfoList.get("SPRINT")+"' , status='"+updateInfoList.get("STATUS")+"' , "+
					"resolution='"+updateInfoList.get("RESOLUTION")+"' , assignee='"+updateInfoList.get("ASSIGNEE")+"' , reporter='"+updateInfoList.get("REPORTER")+"' , "+
					"update_date=sysdate , description='"+updateInfoList.get("DESCRIPTION").trim()+"' , added_comment='"+updateInfoList.get("COMMENT").trim()+"' "
							+ "where lower(category)='"+this.category+"' and lower(title)='"+this.issue_Title+"'";

			url = RdsMapping.singleRdsMapping();
			
			
			con = getConnection(url, driver, dbUserName, dbPassword);
			stmt = con.createStatement();
			status = stmt.executeUpdate(update);
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
	return status;
}

public int addIssue()
{
	int status = -1;
	
	if(this.addInfoList !=null && this.addInfoList.size() >0 
			&& (this.addInfoList.get("CATEGORY") !=null) && (this.addInfoList.get("TITLE") !=null))
	{
		try
		{
			String insert = "insert into dataload_issues (ID,CATEGORY,TITLE,TYPE,PRIORITY,LABELS,EPIC_NAME,SPRINT,STATUS,RESOLUTION,ASSIGNEE,REPORTER,CREATION_DATE,UPDATE_DATE,DESCRIPTION) "+
							"values(seq_issue_id.nextval,'"+addInfoList.get("CATEGORY").trim().toLowerCase()+"','"+addInfoList.get("TITLE").trim()+"','"+addInfoList.get("TYPE").trim().toLowerCase()+
							"','"+addInfoList.get("PRIORITY").trim()+"','"+addInfoList.get("LABELS").trim()+"','"+addInfoList.get("EPIC_NAME").trim()+"','"+addInfoList.get("SPRINT").trim()+
							"','"+addInfoList.get("STATUS").trim()+"','"+addInfoList.get("RESOLUTION").trim()+"','"+addInfoList.get("ASSIGNEE").trim()+"','"+addInfoList.get("REPORTER").trim()+
							"',sysdate,sysdate,'"+addInfoList.get("DESCRIPTION").trim()+"')";

			url = RdsMapping.singleRdsMapping();
			
			
			con = getConnection(url, driver, dbUserName, dbPassword);
			addStmt = con.createStatement();
			status = addStmt.executeUpdate(insert);
			
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
				if(addStmt !=null)
				{
					try
					{
						addStmt.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}

			}


		}
	return status;
}


public Connection getConnection(String connectionURL, String driver, String username, String password)	
		throws Exception
		{
			Class.forName(driver);
			con = DriverManager.getConnection(connectionURL, username, password);
			return con;
		}


public ArrayList <Map<String,String>> getIssuesInfo()
{
	return issuesInfo;
}


}
