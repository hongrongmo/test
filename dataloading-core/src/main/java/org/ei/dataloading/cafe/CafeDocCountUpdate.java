package org.ei.dataloading.cafe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
 * @Date: 07/09/2018
 * @Author: TELEBH
 * @Description: AU & AF profile's cpx doc count is updated/deleted every week after BD's CPX & Cafe ANI is finished. as some doc count count be increased/decreased,
 * som doc_count are very bew (new auid + doc_count or new afid + doc_count) added up. also there could be a chance that some AU/AF ids are deleted 
 * so that we need to delete them from ES too (thier doc_count = 0), in order to do so we need to do the following
 * 
 * 		1. create author_count_temp, institute_count_temp tables that re-calculate count for all ids in lookup tables
 * 		2. compare count temp tables above with Prod count tables (both ways: temp minus prod union prod minus temp) with this comparsion will get
 * 			new/update counts in temp than prod and what was in prod and no longer in temp (deleted)
 * 		4. for ids list, update profile tables (author_profile, institute_profile) set "es_status" column to null
 * 		5. continue regular weekly AU/AF ES index
 */
public class CafeDocCountUpdate 
{

	static String url = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "db_cafe";
	static String password = "ny5av";
	static String doc_type;
	static String temp_table;
	static String count_table;


	static String[] tables;		// make sure temp table first then prod table

	String tableName;
	String column_name;
	Connection con;
	Statement stmt;
	ResultSet rs;

	public static void main(String[] args) 
	{
		if(args[0] !=null)
		{
			doc_type = args[0];
			if(doc_type.equalsIgnoreCase("apr") || doc_type.equalsIgnoreCase("ipr"))
				System.out.println("doc_Type: " + doc_type);
			else
			{
				System.out.println("Invalid Doc Type: " + doc_type + " re-run with apr or ipr");
				System.exit(1);
			}
		}
		if(args[1] !=null)
		{
			temp_table = args[1];
			System.out.println("counts temp table: " + temp_table);
		}
		if(args[2] != null)
		{
			count_table = args[2];
			System.out.println("counts table: " + count_table);
		}

		if(args.length >6)
		{
			if(args[3] != null)
			{
				url = args[3];
				System.out.println(url);
			}
			if(args[4] != null)
			{
				driver = args[4];
			}
			if(args[5] != null)
			{
				username = args[5];
				System.out.println("username= " + username);
			}
			if(args[6] != null)
			{
				password = args[6];
				System.out.println("password= " + password);
			}
		}
		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}

		CafeDocCountUpdate obj = new CafeDocCountUpdate();
		obj.init();
		obj.getProfileIds();

	}

	public void init()
	{
		if(doc_type.equalsIgnoreCase("apr"))
		{
			column_name = "AUTHOR_ID";
			tableName = "author_profile";
		}
		else if(doc_type.equalsIgnoreCase("ipr"))
		{
			column_name = "INSTITUTE_ID";
			tableName = "institute_profile";
		}

	}

	public void getProfileIds()
	{
		String query;
		List<String> profileid_List = new ArrayList<String>();

		try
		{
			
			// ONLY for testing
			/*query = "select * from (select " + column_name + "||','||doc_count as id_count from " + temp_table + " minus " +
					"select " + column_name + "||','||doc_count as id_count from " + count_table + " union all " +
					"select " + column_name + "||',' as id_count from " + count_table + " where " + column_name + 
					" not in (select " + column_name + " from " + temp_table + ")) where rownum<7";
			*/
			
			query = "select " + column_name + "||','||doc_count as id_count from " + temp_table + " minus " +
					"select " + column_name + "||','||doc_count as id_count from " + count_table + " union all " +
					"select " + column_name + "||',' as id_count from " + count_table + " where " + column_name + 
					" not in (select " + column_name + " from " + temp_table + ")";

			System.out.println(query);
			con = getConnection(url, driver, username, password);
			stmt = con.createStatement();
			stmt.setFetchSize(200);
			rs = stmt.executeQuery(query);

			System.out.println("Got records... from table: " + count_table);

			while(rs.next())
			{
				if(rs.getString("ID_COUNT") !=null)
				{
					if(rs.getString("ID_COUNT").indexOf(",") > -1)
					{
						profileid_List.add(rs.getString("ID_COUNT").split(",")[0]);
					}
				}
			}

			System.out.println("Total IDS with updated/deleted count:  " + profileid_List.size());

		}
		catch(SQLException ex)
		{
			System.out.println("Error occured in select stmt! Error: " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("Error occurred at finding profile ids with changed count Error: " + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			if(rs != null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					System.out.println("Failed to close RS!");
					e.printStackTrace();
				}
			}
			if(stmt != null)
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
		}
		// update es_status of affected profile ids to null to be re-indexed to ES with new count
		updateProfileESStatus(profileid_List);

	}

	public void updateProfileESStatus(List<String> ids)
	{
		StringBuffer keys = new StringBuffer();
		PreparedStatement stmt = null;
		String sql;

		int count =0;
		int result;

		if(con != null)
		{


			for(int i=0; i<ids.size();i++)
			{
				if(count<999)
				{
					if(keys.length() >0)
						keys.append(",");
					keys.append("'" + ids.get(i) + "'");
					count ++;
				}
				else
				{

					try
					{

						sql = "update " + tableName + "set es_status = null where authorid in ( " + keys + ")";
						System.out.println("Query: " + sql);
						stmt = con.prepareStatement(sql);
						result = stmt.executeUpdate();
						System.out.println("updated batch of " + result + " " + doc_type + " profiles");
						count = 0;
						keys.delete(0, keys.length());

					}
					catch(SQLException ex)
					{
						System.out.println("Error updating profile's es_status!! " + ex.getMessage());
						ex.printStackTrace();
					}
					catch(Exception e)
					{
						System.out.println("Error updating profile's es_status!! " + e.getMessage());
						e.printStackTrace();
					}
				}
			}

			try
			{

				// update what is left of all ids to update es_status
				sql = "update " + tableName + " set es_status = null where authorid in ( " + keys + ")";
				System.out.println("Query: " + sql);
				stmt = con.prepareStatement(sql);
				result = stmt.executeUpdate();
				System.out.println("updated batch of " + result + " " + doc_type + " profiles");
			}
			catch(SQLException ex)
			{
				System.out.println("Error updating profile's es_status!! " + ex.getMessage());
				ex.printStackTrace();
			}
			catch(Exception e)
			{
				System.out.println("Error updating profile's es_status!! " + e.getMessage());
				e.printStackTrace();
			}

			finally
			{
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
				if(con != null)
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

	}

	public Connection getConnection(String connectionURL,String driver,String username,String password)	throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,username,password);
		con.setAutoCommit(true);
		return con;
	}

}
