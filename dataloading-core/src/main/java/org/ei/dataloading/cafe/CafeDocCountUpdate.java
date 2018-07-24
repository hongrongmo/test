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
 * 		5. for those ids that were in prod and not in temp count table (deleted) need to delete from ES
 * 		6. continue regular weekly AU/AF ES index
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
	static String action = "delete";
	static int recsPerEsbulk;
	static String esDomain = "search-evcafe5-ucqg6c7jnb4qbvppj2nee4muwi.us-east-1.es.amazonaws.com";
	static String esIndexName;		// added 05/10/2018 as ES 6.2 and up split types in separate indices
	


	static String[] tables;		// make sure temp table first then prod table
	static String profileTable;
	
	AusAffESIndex esIndexObj = null;
	
	String tableName;
	String column_name;
	Connection con;
	Statement stmt;
	ResultSet rs;
	List<String> profileid_List = new ArrayList<String>();
	List<String> deleted_Profileid_List = new ArrayList<String>();
	List<String> MID_deletion_list;
	
	

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
			if(!(temp_table.toLowerCase().contains("temp")))
			{
				System.out.println("Invalid counts temp table!");
				System.exit(1);
			}
		}
		if(args[2] != null)
		{
			count_table = args[2];
			System.out.println("counts table: " + count_table);
		}

		if(args.length >10)
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
			
			if(args[7] !=null)
			{
				action = args[7];
				System.out.println("ES Action: " + action);
			}
			if(args[8] !=null)
			{
				try
				{
					recsPerEsbulk = Integer.parseInt(args[8]);

					System.out.println("ES Documents per Bulk: " + recsPerEsbulk);
				}
				catch(NumberFormatException ex)
				{
					recsPerEsbulk = 10;
				}
			}
			if(args[9] !=null)
			{
				esDomain = args[9];

				System.out.println("ES Domain name: " + esDomain);
			}
			
			if(args[10] !=null)
			{
				esIndexName = args[10];
				if(esIndexName.equalsIgnoreCase("author") || esIndexName.equalsIgnoreCase("affiliation") || esIndexName.equalsIgnoreCase("cafe"))

					System.out.println("ES Index Name: " + esIndexName);
				else
				{
					System.out.println("Invalid ES Index Name for AU/AF profile deletion from ES, re-try with ESIndexName cafe");
					System.exit(1);
				}
			}

		}
		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}

		CafeDocCountUpdate obj = new CafeDocCountUpdate();
		obj.esIndexObj = new AusAffESIndex(recsPerEsbulk, esDomain, action, esIndexName);
		
		
		obj.init();
		obj.getProfileIds();
		
		// delete ids that were in Prod and not in temp from ES
		if(obj.deleted_Profileid_List.size() >0)
		obj.deleteProfile();
				
		// update es_status of affected profile ids to null to be re-indexed to ES with new count
		obj.updateProfileESStatus(obj.profileid_List);

	}

	public void init()
	{
		if(doc_type.equalsIgnoreCase("apr"))
		{
			column_name = "AUTHORID";
			tableName = "author_profile";
		}
		else if(doc_type.equalsIgnoreCase("ipr"))
		{
			column_name = "AFFID";
			tableName = "institute_profile";
		}

	}

	public void getProfileIds()
	{
		String query;
		String[] id_count_pair = new String[2];

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
						id_count_pair = rs.getString("ID_COUNT").split(",");
						if(id_count_pair.length == 1)
							deleted_Profileid_List.add(id_count_pair[0]);
						profileid_List.add(id_count_pair[0]);
					}
				}
			}

			System.out.println("Total IDS without doc_count count: " + deleted_Profileid_List.size());
			System.out.println("Total IDS with updated count:  " + profileid_List.size());

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

	}
	
	public void getMidToBeDeleted(String profileIds)
	{
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		MID_deletion_list =  new ArrayList<String>();

		try
		{


			System.out.println("Running the query...");

			query = "select M_ID from " + tableName + " where " + column_name + " in (" + profileIds + " )";

			//System.out.println(query);
			stmt = con.createStatement();
			stmt.setFetchSize(200);

			rs = stmt.executeQuery(query);

			while(rs.next())
			{
				MID_deletion_list.add(rs.getString("M_ID"));
			}

		}

		catch(SQLException ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
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
		}
	}

	public boolean deleteProfile()
	{
		StringBuffer profileIds = new StringBuffer();
		int curRec = 0;
		int status;

		try
		{
			for(int i=0;i<deleted_Profileid_List.size();i++)
			{
				if(curRec>999)
				{
					//get M_ID list from DB for ES deletion & then delete from DB
					getMidToBeDeleted(profileIds.toString());

					if(MID_deletion_list.size() >0)
					{
						// delete from ES	
						status = esIndexObj.createBulkDelete(doc_type, MID_deletion_list);
					}

					curRec = 0;
					profileIds = new StringBuffer();
					MID_deletion_list.clear();
				}


				if(profileIds.length() >0)
					profileIds.append(",");			
				profileIds.append("'" + deleted_Profileid_List.get(i) + "'");

				curRec ++;
			}

			//get M_ID list from DB for ES deletion & then delete from DB
			getMidToBeDeleted(profileIds.toString());

			if(MID_deletion_list.size() >0)
			{
				// delete from ES
				status = esIndexObj.createBulkDelete(doc_type, MID_deletion_list);
			}
		}
		catch(Exception e)
		{
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
		return true;

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

						sql = "update " + tableName + " set es_status = null where authorid in ( " + keys + ")";
						//System.out.println("Query: " + sql);
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
				//System.out.println("Query: " + sql);
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
