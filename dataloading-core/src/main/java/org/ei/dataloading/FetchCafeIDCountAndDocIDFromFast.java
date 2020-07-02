package org.ei.dataloading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import org.ei.domain.*;

/**
 * 
 * @author TELEBH
 * @Date: 10/29/2018
 * @Description: to get the list of all AUID/AFID, HIT_COUNT and DOC_IDs in fast
 *               DR. this class was majorly created to help in the doc_count
 *               discrepancy by clean-up of cafe DUP AN, to find thier
 *               associated AUIDS/AFIDS and delete them from ES, but 1st need to
 *               only delete the ones that only matched those DUP AN ANIS, but
 *               leave the IDS that belong to other BD's ANI that not in the dup
 *               cafe AN list so can delete them from ES as a phase of the
 *               clean-up to increase QPS as it is low compared to PROD. and
 *               that casing Delay in checking all AUIDS in fast DR, as
 *               suggested by Harold if increase QPS from current ~25 in DR to
 *               ~93 as in PROD that should increase performance in other words
 *               DR performance itself to process request once received it is
 *               very good the problem is latency from sending request from our
 *               machine to fast DR instance and this should be enhanced by
 *               increasing # of queries submitted to DR /second
 * 
 */
public class FetchCafeIDCountAndDocIDFromFast {

	static String query = "";
	private static Connection con;
	static ResultSet rs = null;
	static Statement stmt = null;
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "db_xml";
	static String password = "";
	// static String fastUrl = "http://ei-main.nda.fastsearch.net:15100";
	// static String fastUrl = "http://evprod14.cloudapp.net:15100"; //PROD
	static String fastUrl = "http://evdr09.cloudapp.net:15100"; // DR

	static String fastQuery = "";
	static int pageRecCount = 25;
	static String doc_type;
	static String tableName;
	static String columnName;
	static String esField;
	static int counter = 0;

	BufferedReader in = null;

	static List<String> idList = new ArrayList<String>();

	public static void main(String[] args) throws Exception {

		if (args[0] != null) {
			doc_type = args[0];
			if (doc_type.equalsIgnoreCase("apr")) {
				tableName = "HH_AUID_TO_DELETE_DUP33AN";
				columnName = "authorid";
				esField = "auid";
			}

			else if (doc_type.equalsIgnoreCase("ipr")) {
				tableName = "HH_AFID_TO_DELETE_DUP33AN";
				columnName = "affid";
				esField = "afid";
			}

		}

		if (args[1] != null) {
			pageRecCount = Integer.parseInt(args[1]);
		}

		if (args.length > 2) {
			if (args[2] != null) {
				url = args[2];
			}

			if (args[3] != null) {
				driver = args[3];
			}
			if (args[4] != null) {
				username = args[4];
			}

			if (args[5] != null) {
				password = args[5];
			}

			if (args[6] != null) {
				fastUrl = args[6];
			}

		}

		getIdsFromDB();
		fastCheck();

	}

	public static void getIdsFromDB() {
		Statement stmt = null;
		ResultSet rs = null;

		/*String query = "select "
				+ columnName
				+ " from "
				+ tableName
				+ " where authorid in (56285443100,57190209546,55561679100,56259840100,"
				+ "55953346600,7101639530,7402835601)";*/
		
		String query = "select "
				+ columnName
				+ " from "
				+ tableName;
		
		try {
			con = getConnection(url, driver, username, password);
			System.out.println(query);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			System.out.println("Got Records....");
			while (rs.next()) {
				if (rs.getString(1) != null) {
					idList.add(rs.getString(1));
				}
			}
		} catch (SQLException ex) {
			System.out.println("error in sql query!! " + ex.getMessage());
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	public static void fastCheck() {
		String ID = null;
		FileWriter out = null;

		try {

			// in = new BufferedReader(new FileReader("fastdocs.txt"));
			File file = new File(FetchCafeIDCountAndDocIDFromFast.doc_type
					+ "_fastdocs.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			out = new FileWriter(file);

			if (idList.size() > 0) {
				FastClient client = new FastClient();
				client.setBaseURL(fastUrl);
				client.setResultView("ei");
				client.setOffSet(0);
				client.setPageSize(pageRecCount);
				client.setDoCatCount(true);
				client.setDoNavigators(false);
				client.setPrimarySort("rank");
				client.setPrimarySortDirection("+");

				for (int i = 0; i < idList.size(); i++) {

					ID = idList.get(i);
					fastQuery = "(" + esField + ":\"" + ID + "\")";
					client.setQueryString(fastQuery);
					client.getBaseURL();
					client.search();

					int hit_count = client.getHitCount();

					List<String[]> l = client.getDocIDs();
					if (l.size() > 0) {
						for (int j = 0; j < l.size(); j++) 
						{

							// keep this block to add additonal conditions on
							// docID tests
							String[] docID = (String[]) l.get(j);
							out.write(ID + "\t" + docID[0] + "\t" + hit_count+ "\n");
						}
						
						//reset client's docIDS so does not concatenate with previous AUID/AFIDS docIDs, uncomment when needed & modify fastClient class as well
						
						//client.clearDocIDs();
						}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)

				{
					out.flush();
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	protected static Connection getConnection(String connectionURL,
			String driver, String username, String password) throws Exception {
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL, username,
				password);
		return con;
	}

}
