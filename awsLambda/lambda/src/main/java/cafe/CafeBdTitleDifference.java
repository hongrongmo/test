package cafe;

/**
 * @author TELEBH
 * @Date: 01/03/2017
 * @Description: as per Frank request need to compare Title for Cafe & BD that match based on AN. as eventhough records match
 * on AN, but their title still different, so we need to find that difference
 * 
 * this class compare strings using Google "Diff, Match and Patch" API 
 *  
 */


import jar.diff_match_patch;
import jar.diff_match_patch.Diff;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;


public class CafeBdTitleDifference 
{
	diff_match_patch.Operation DELETE = diff_match_patch.Operation.DELETE;
	diff_match_patch.Operation EQUAL = diff_match_patch.Operation.EQUAL;
	diff_match_patch.Operation INSERT = diff_match_patch.Operation.INSERT;

	public static final String IDDELIMITER = new String(new char[] {31});


	String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";
	String driver = "oracle.jdbc.driver.OracleDriver";
	String username = "db_cafe";
	String password = "ny5av";


	public static void main(String[] args)
	{
		diff_match_patch dmp = new diff_match_patch();

		// for testing
		String a = "&laquo;Living&raquo; chain radical polymerisationyeng";
		String b = "<<Living>> chain radical polymerisationyRussian";


		LinkedList<Diff> DiffList = dmp.diff_main(a, b, false);
		for(int i=0;i<DiffList.size();i++)
		{
			System.out.println("diff #" + i + " : " + DiffList.get(i));
		}
		
		System.out.println("-------END of test------------");
		
		try
		{
			CafeBdTitleDifference obj = new CafeBdTitleDifference();
			obj.getCitationTitle();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}


	// Private function for quickly building lists of diffs.
	private static LinkedList<Diff> diffList(Diff... diffs) {
		LinkedList<Diff> myDiffList = new LinkedList<Diff>();
		for (Diff myDiff : diffs) {
			myDiffList.add(myDiff);
		}
		return myDiffList;
	}

	public void getCitationTitle() throws Exception
	{
		Connection con;
		Statement stmt = null;
		ResultSet rs = null;

		try
		{

			con = getConnection(connectionURL, driver, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery("select accessnumber,cafe_pui,bd_pui,cafe_title,bd_title from HH_CAFE_BD_SAME_TI");
			processRecs(rs,con);

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

	public void processRecs(ResultSet rs, Connection con)
	{
		System.out.println("do nothing for now");

		diff_match_patch dmp = new diff_match_patch();
		PrintWriter out = null;
		LinkedList<Diff> DiffList;

		String cafe_title = null;
		String bd_title = null;
		
		int lengthDiff = 0;
		try
		{
			out = new PrintWriter(new File("cafe_bd_ti_diff.txt"));
			while(rs.next())
			{
				if(rs.getString("CAFE_TITLE") !=null || rs.getString("BD_TITLE") !=null)
				{
					cafe_title = rs.getString("CAFE_TITLE").trim().toLowerCase();
					bd_title = rs.getString("BD_TITLE").trim().toLowerCase();
					String[] cafe_TitleParts = cafe_title.split(IDDELIMITER);
					String[] bd_TitleParts = bd_title.split(IDDELIMITER);

					if(cafe_TitleParts.length >1)
					{
						if(cafe_TitleParts[1] !=null && bd_TitleParts[1] !=null)
						{
							out.print(rs.getString("ACCESSNUMBER") + "\t");
							
							// uncomment when use Google API part below
							/*out.print(rs.getString("CAFE_TITLE") + "\t");
							out.print(rs.getString("BD_TITLE") + "\t");*/

							//length
							out.print(rs.getString("CAFE_TITLE").trim().length() + "\t");
							out.print(rs.getString("BD_TITLE").trim().length() + "\t");
							
							//difference
							if(rs.getString("CAFE_TITLE").trim().length() >= rs.getString("BD_TITLE").trim().length())
								
							{
								lengthDiff = rs.getString("CAFE_TITLE").trim().length() - rs.getString("BD_TITLE").trim().length();
							}
							else
							{
								lengthDiff = rs.getString("BD_TITLE").trim().length() - rs.getString("CAFE_TITLE").trim().length();
							}
								
							out.print(lengthDiff + "\t");
							
							// to easily identify records with bigg diff i.e. length_diff>100
							
							if(lengthDiff >50)
							{
								out.print("yes");
							}
							
							// google API, prints exact string diff
							/*DiffList = dmp.diff_main(cafe_TitleParts[1], bd_TitleParts[1], false);
							for(int i=0;i<DiffList.size();i++)
							{
								out.println("diff #" + i + " : " + DiffList.get(i));
							}

							out.println("***************");
							*/
						}
					}
				}
				out.println("");

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			if(out !=null)
			{
				try
				{
					out.flush();
					out.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}


	public Connection getConnection(String connectionURL,String driver,String username,String password)	throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL,username,password);
		return con;
	}

}

