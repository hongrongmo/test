package org.ei.dataloading.cafe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author TELEBH
 * @Date: 06/27/2017
 * @Description:  after processing of daily ANI/APR/IPR corrections, at somepoint in the week
 * ; during freezing window, we will need to delete AU/AF profiles from ES that corresponded 
 * to ANI records deleted (using pui in "weekly_deleted" table) as follows
 * 
 *  	- every day we run ANI correction, if there any deletion we save their "PUI, EID" in weekly_deleted before delete from cafe_master table
 *  	- if same record re-loaded in later days, its entry will be deleted from weekly_deleted table (during converting program)
 *  	- during freezing window, after updating lookup tables setting "statua=deleted" for those records matched weekly_deleted
 *  		- check distinct status for all authors associated to every PUI in weekly_deleted table
 *  				- if all distinct status is "deleted", then delete the entry from ES
 *  				- if distinct status contains at least "matched", do not delete any thing from ES
 *  				- if all distinct status does not contain any "matched", then delete the profile from ES 
 */
public class AusAffDeletion {

	static String doc_type; 
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction1";
	static String password = "ei3it";
	static String deletionTable = "cafe_weekly_deletion";
	static String action = "delete";
	static int recsPerEsbulk;
	static String esDomain = "search-evcafe5-ucqg6c7jnb4qbvppj2nee4muwi.us-east-1.es.amazonaws.com";
	
	

	AusAffESIndex esIndexObj = null;
	
	Connection con = null;
	String lookupTable = null;
	String lookupTable_columnName = null;
	String profileTable;
	String profileColumnName;

	Map<String,String> id_status_List = new Hashtable<String,String>();
	List<String> id_List = new ArrayList<String>();
	List<String> Id_deletion_list;
	List<String> MID_deletion_list;



	public static void main(String[] args)
	{
		if(args.length >8)
		{
			if(args[0] !=null)
			{
				doc_type = args[0];
			}

			if(args[1] !=null)
			{
				url = args[1];
			}
			if(args[2] !=null)
			{
				driver = args[2];
			}
			if(args[3] !=null)
			{
				username = args[3];

				System.out.println("Schema: " + username);
			}
			if(args[4] !=null)
			{
				password = args[4]; 
			}
			if(args[5] !=null)
			{
				deletionTable = args[5];
				System.out.println("Temp table: " + deletionTable);
			}
			if(args[6] !=null)
			{
				action = args[6];
			}
			if(args[7] !=null)
			{
				try
				{
					recsPerEsbulk = Integer.parseInt(args[7]);

					System.out.println("ES Documents per Bulk: " + recsPerEsbulk);
				}
				catch(NumberFormatException ex)
				{
					recsPerEsbulk = 10;
				}
			}
			if(args[8] !=null)
			{
				esDomain = args[8];

				System.out.println("ES Domain name: " + esDomain);
			}

		}
		else
		{
			System.out.println("Not Enough Parameters");
			System.exit(1);
		}

		try
		{
			AusAffDeletion c = new AusAffDeletion();
			
			c.esIndexObj = new AusAffESIndex(recsPerEsbulk, esDomain, action);
			
			c.con = c.getConnection(url, driver, username, password);

			c.getProfilesToBeDeleted();
			if(c.id_status_List.size() >0)
			{
				c.getProfileIdsToDelete();
				c.deleteProfile();
			}
			else
				System.out.println("nothing to be deleted, do nothing!");


		}

		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}


	public void getProfilesToBeDeleted()
	{
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;

		String profileId= "";
		String status;

		try
		{
			if(doc_type !=null && doc_type.equalsIgnoreCase("apr"))
			{
				lookupTable = "cmb_au_lookup";   //prod
				//lookupTable = "hh_test_au_lookup"; // for testing
				lookupTable_columnName = "AUTHOR_ID";
				
				profileTable = "author_profile";
				profileColumnName = "AUTHORID";
				
			}

			else if (doc_type !=null && doc_type.equalsIgnoreCase("ipr"))
			{
				lookupTable = "cmb_af_lookup";
				lookupTable_columnName = "INSTITUTE_ID";
				
				profileTable = "institute_profile";
				profileColumnName = "AFFID";
			}


			else
			{
				System.out.println("Invalid doc type!!! Re-try with apr or ipr");
				System.exit(1);
			}

			stmt = con.createStatement();
			System.out.println("Running the query...");
			query = "select " + lookupTable_columnName + ", status from " + lookupTable + " where pui in (select pui from " + deletionTable + ") " +
					"group by " + lookupTable_columnName + ",status order by " + lookupTable_columnName;

			System.out.println(query);
			stmt = con.createStatement();
			stmt.setFetchSize(200);
			rs = stmt.executeQuery(query);

			while(rs.next())
			{
				if(rs.getString(1) !=null)
				{
					profileId = rs.getString(1);
					status = rs.getString(2);
					if(status.equalsIgnoreCase("matched"))
						status="$"+status+"$";
					if(id_status_List.containsKey(profileId))
					{
						id_status_List.put(profileId, id_status_List.get(profileId) + "," + status);
					}
					else
					{
						id_status_List.put(profileId, status);
					}
				}
			}

			System.out.println("Total Records matched weekly_deletion table: " + id_status_List.size());

		}
		catch(SQLException ex)
		{
			System.out.println("Failed to read from rs!!!");
			System.out.println("Reason: " + ex.getMessage());
			ex.printStackTrace();
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

	public void getProfileIdsToDelete()
	{	
		for(String key: id_status_List.keySet())
		{
			if(id_status_List.get(key) !=null && !(id_status_List.get(key).contains("$matched$")) && id_status_List.get(key).contains("deleted"))
			{
				id_List.add(key);		
			}
			else
			{
				System.out.println("ID: " + key + " contains at least one 'matched' status, do nothing!");
			}
		}

		System.out.println("Total " + doc_type + " Ids to be deleted: " + id_List.size());

	}


	public boolean deleteProfile()
	{
		StringBuffer profileIds = new StringBuffer();
		int curRec = 0;
		int status;

		try
		{
			for(int i=0;i<id_List.size();i++)
			{
				if(curRec>999)
				{
					//get M_ID list from DB for ES deletion & then delete from DB
					getMidToBeDeleted(profileIds.toString());

					if(MID_deletion_list.size() >0)
					{
						// delete from ES	
						status = esIndexObj.createBulkDelete(doc_type, MID_deletion_list);

						// update ES_STATUS in profile table
						if(status !=0 && (status ==200 || status == 201 || status ==404))
						{
							updateProfileEsStatus(profileIds.toString());  // temp comment during testing, NEED TO UNCOMMENT WHEN MOVE TO PROD
						}
						else
						{
							System.out.println("Error Occurred during ES Deletion, so no DB deletion");
						}
					}

					curRec = 0;
					profileIds = new StringBuffer();
					MID_deletion_list.clear();
				}

				if(profileIds.length() >0)
					profileIds.append(",");			
				profileIds.append("'" + id_List.get(i) + "'");
			}

			//get M_ID list from DB for ES deletion & then delete from DB
			getMidToBeDeleted(profileIds.toString());

			if(MID_deletion_list.size() >0)
			{
				// delete from ES
				status = esIndexObj.createBulkDelete(doc_type, MID_deletion_list);


				//updates ES_STATUS in profile table & delete from DB LKUP table
				if(status!=0 && (status == 200 || status == 201 || status == 404))
				{
					updateProfileEsStatus(profileIds.toString());  // temp comment during testing, NEED TO UNCOMMENT WHEN MOVE TO PROD
					DbBulkDelete();                                // temp comment during testing, NEED TO UNCOMMENT WHEN MOVE TO PROD
				}
				else
				{
					System.out.println("Error Occurred during ES Deletion, so no DB deletion");
				}

			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return true;

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

			query = "select M_ID from " + profileTable + " where " + profileColumnName + " in (" + profileIds + " )";

			System.out.println(query);
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


	public void updateProfileEsStatus(String profileIds)
	{
		
		Statement stmt= null;
		ResultSet rs = null;
		String query = "";


		try
		{
			
			//1. updates "es_status" column to "null" in profile table that matched a deleted Cafe ANI PUI record, where profile distinct status in lkup is deleted  
			query = "update " + profileTable + " set es_status=null where " + profileColumnName + " in (" +profileIds + ")";

			System.out.println("Running query...." + query);

			stmt = con.createStatement();
			int count = stmt.executeUpdate(query);
			con.commit();


			System.out.println("Total Keys updated in " + profileTable + " : " + count);

		}
		catch(SQLException ex)
		{
			System.out.println("Error to update es_statusin DB table: " + profileTable + " error message: " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
	
	public void DbBulkDelete()
	{
		
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";


		try
		{
			//2. delete records match cafe ANI PUI from lookup tables
			query = "delete from " + lookupTable + " where pui in (select pui from " + deletionTable + ")" ;

			System.out.println("Running query...." + query);

			stmt = con.createStatement();
			int count = stmt.executeUpdate(query);
			con.commit();


			System.out.println("Total Keys Deleted from: " + lookupTable + " :- " + count);

		}
		catch(SQLException ex)
		{
			System.out.println("Error to delete M_ID list from DB table: " + lookupTable + " error message: " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
	private Connection getConnection(String connectionURL,
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
