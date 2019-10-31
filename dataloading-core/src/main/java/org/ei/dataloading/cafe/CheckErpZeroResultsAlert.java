package org.ei.dataloading.cafe;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.ei.util.db.DbConnection;

/**
 * 
 * @author TELEBH
 * @date: 10/29/2019
 * @Description: As per Sally's request to check top 5 Engineering Research Profile's Email Alerts 
 */
public class CheckErpZeroResultsAlert {
	
	// Institutions List as finals
	
	private static final int MASSACHUSETTS_INST_TECH = 508111;
	private static final int STANFORD_UNIV = 508219;
	private static final int MICHIGAN_STATE_UNIV = 508122;
	private static final int HARVARD_UNIV = 508076;
	private static final int PRINCETON_UNIV = 508191;
	private static final int OHIO_STATE_UNIV = 508179;
	private static final int CAMBRIDGE_UNIV = 315068;
	
	
	// DB connection parameters
	private static String connectionURL;
	private static String driver;
	private static String userName;
	private static String password;
	private static String updateNumber;
	
	
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;
			
	Map<Integer,HashSet<Integer>> instAndAffIds = new TreeMap<>();		// sort INSTIDS
	
	public void getAffIds()
	{
		String query = "select INSTITUTION_ID,AFFILIATION_ID from HH_DEANDASHBOARD_2019 where INSTITUTION_ID in (" + MASSACHUSETTS_INST_TECH + "," + STANFORD_UNIV + "," 
				+ MICHIGAN_STATE_UNIV + "," + HARVARD_UNIV + "," + PRINCETON_UNIV + "," + OHIO_STATE_UNIV + "," + CAMBRIDGE_UNIV + ")";
		try 
		{
			con = DbConnection.getConnection(connectionURL, driver, userName, password);
			System.out.println("Running query: " + query);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			int key;
			
			while(rs.next())
			{
				key = rs.getInt("INSTITUTION_ID");
				if(instAndAffIds.containsKey(key))
				{
					instAndAffIds.get(key).add(rs.getInt("AFFILIATION_ID"));
				}
				else
				{
					HashSet<Integer> value = new HashSet<>();
					value.add(rs.getInt("AFFILIATION_ID"));
					instAndAffIds.put(key, value);
				}
			}
			
			System.out.println(instAndAffIds);
			
		} 
		catch (Exception e) 
		{
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
	
	
	// loop through INSTIDs to find # of BD-CPX abstract records for the current week containing these AFFIDS

	private void checkErpZeroAlert()
	{
		String query = null;
		int docCount = 0;
		try
		{
			con = DbConnection.getConnection(connectionURL, driver, userName, password);
			stmt = con.createStatement();
			
			for(Map.Entry<Integer, HashSet<Integer>> entry: instAndAffIds.entrySet())
			{
				HashSet<Integer> values = entry.getValue();
				String affIdsQuery = buildAffiliationIdsSubQuery(values);
				
				query = "select count(*) as doc_count from db_xml.bd_master where updatenumber='" + updateNumber + "' and database='cpx' and pui in "
						+ "(select puisecondary from cafe_pui_list_master where pui in (select pui from cafe_master where " + affIdsQuery + "))";
				
				rs = stmt.executeQuery(query);
				while(rs.next())
				{
					docCount = rs.getInt("doc_count");
					System.out.println("INSTID: " + entry.getKey() + "has " + docCount + " docs for Email Alerts this week");
				}
			}
		}
		catch(Exception e)
		{
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
			if(con !=null)
			{
				DbConnection.closeConnection();
			}
			
		}
	}
	
	
	// build AffiliationIds subQuery
	private String buildAffiliationIdsSubQuery(HashSet<Integer> values)
	{

		StringBuilder affIdsBuilder = new StringBuilder();
		for(Integer affId: values)
		{
			if(affIdsBuilder.length() >0)
				affIdsBuilder.append("or ");
			affIdsBuilder.append("affiliation like '%" + affId + "%' ");
		}
		
		System.out.println("concatenated AffiliatonIDS Query: " + affIdsBuilder.toString());
		return affIdsBuilder.toString();
	}
	
	
	
	public static void main(String[] args)
	{
		if(args.length <5)
		{
			System.out.println("Not Enough Parameters!");
			System.exit(1);
		}
		else
		{
			if(args[0] !=null && !(args[0].isEmpty()))
			{
				connectionURL = args[0];
				System.out.println("Connection URL: " + connectionURL);
			}
			if(args[1] !=null && !(args[1].isEmpty()))
			{
				driver = args[1];
				System.out.println("Driver: " + driver);
			}
			if(args[2] !=null && !(args[2].isEmpty()))
			{
				userName = args[2];
				System.out.println("userName: " + userName);
			}
			if(args[3] !=null && !(args[3].isEmpty()))
			{
				password = args[3];
			}
			if(args[4] !=null && !(args[4].isEmpty()))
			{
				updateNumber = args[4];
				System.out.println("updateNumber: " + updateNumber);
			}
			
		}
		
		try
		{
			CheckErpZeroResultsAlert obj = new CheckErpZeroResultsAlert();
			
			obj.getAffIds();
			obj.checkErpZeroAlert();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
