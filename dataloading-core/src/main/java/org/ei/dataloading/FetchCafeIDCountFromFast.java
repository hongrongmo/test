package org.ei.dataloading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.ei.domain.FastClient;

/**
 * 
 * @author TELEBH
 * @Date: 07/17/2018
 * @Description: to get the list of all AUID/AFID and HIT_COUNT in fast to check discrepancy with doc_count in DB/ES
 */
public class FetchCafeIDCountFromFast {

	static String query = "";
	private static Connection con;
	static ResultSet rs = null;
	static Statement stmt = null;
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "db_xml";
	static String password = "ny5av";
	//static String fastUrl = "http://ei-main.nda.fastsearch.net:15100";
	//static String fastUrl = "http://evprod14.cloudapp.net:15100";		//PROD
	static String fastUrl = "http://evdr09.cloudapp.net:15100";			//DR

	static String fastQuery="";
	static int pageRecCount = 25;
	static String doc_type;
	static String tableName;
	static String columnName;
	static String esField;
	static int counter = 0;

	static List<String> idList = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		BufferedReader in = null;
		FileWriter out = null;

		if(args[0] !=null)
		{
			doc_type = args[0];
			if(doc_type.equalsIgnoreCase("apr"))
			{
				tableName = "author_count";
				columnName = "authorid";
				esField = "auid";
			}

			else if(doc_type.equalsIgnoreCase("ipr"))
			{
				tableName = "HH_AFID_TO_DELETE";
				columnName = "affid";
				esField = "afid";
			}

		}
		if(args[1] != null)
		{
			pageRecCount = Integer.parseInt(args[1]);
		}
		

		if(args.length > 2)
		{
			if(args[2] !=null)
			{
				url = args[2];
			}

			if(args[3] !=null)
			{
				driver = args[3];
			}
			if(args[4] !=null)
			{
				username = args[4];
			}

			if( args[5] != null)
			{
				password = args[5];
			}

			if(args[6] !=null)
			{
				fastUrl = args[6];
			}

		}


		System.out.println("Fetch accessnumber of query " + fastQuery + " for Rec Count " +  pageRecCount);
		try {

			// in = new BufferedReader(new FileReader("fastdocs.txt"));
			File file = new File(doc_type + "_fastdocs.txt");
			if(!file.exists())
			{
				file.createNewFile();
			}
			out = new FileWriter(file);

			FastClient client = new FastClient();
			client.setBaseURL(fastUrl);
			client.setResultView("ei");
			client.setOffSet(0);
			client.setPageSize(pageRecCount);

			//client.setQueryString(fastQuery);  //original
			client.setDoCatCount(true);
			client.setDoNavigators(false);
			//client.setPrimarySort("ausort");  //original
			client.setPrimarySort("rank");
			client.setPrimarySortDirection("+");
			

			// for testing
			/*String profileId = fastQuery.substring(fastQuery.indexOf(":") + 1, fastQuery.indexOf(")"));
					System.out.println("ProfileID: " + profileId);*/

			// get IDS list
			getIdsFromDB();
			
			
			for(int i=0; i<idList.size();i++)
			{
				/**
				 *  commented out for DR as per Harld advice to 
					submit more queries for DR/second bc current QPS is much less in DR than PROD
				if(counter >=4000)
				{
					System.out.println("Sleep 30 seconds before next 4000 bulk");
					//Thread.sleep(30000);   // sleep for 30 seconds before getting next 4000 IDS
					counter = 0;
				}
				**/

				fastQuery = "(" + esField + ":\"" + idList.get(i) + "\")";
				client.setQueryString(fastQuery);
				client.getBaseURL();
				client.search();

				int hit_count = client.getHitCount();
				out.write(idList.get(i) + "\t" + hit_count + "\n");

			}





		} finally {
			if (in != null) {
				in.close();
			}

			if(con !=null)
			{
				con.close();
			}

			if(rs !=null)
			{
				rs.close();
			}

			if(out !=null)
			{
				out.flush();
				out.close();
			}
		}
	}

	public static void getIdsFromDB()
	{
		Statement stmt = null;
		ResultSet rs = null;

		String query = "select " + columnName + " from " + tableName;
		try
		{
			con = getConnection(url, driver, username, password);
			System.out.println(query);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			System.out.println("Got Records....");
			while(rs.next())
			{
				if(rs.getString(1) !=null)
				{
					idList.add(rs.getString(1));
				}
			}
		}
		catch(SQLException ex)
		{
			System.out.println("error in sql query!! " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
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
		}
	}

	protected static Connection getConnection(String connectionURL, String driver, String username, String password) throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL, username, password);
		return con;
	}

}
