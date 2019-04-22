package cafe;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//import org.ei.common.Constants;  // not working at jar build


/*
 * @Date: 04/17/2019
 * @Author: telebh
 * @Description: this week [201917] on Tuesday 04/17/2019. TM reported that some authors in author search shows "1" record more than in expert search result documents
 * 
 * For Example: AUID: 7601458357, in ES result page shows "259", but in EV Expert search page "258"
 * 
 * Harold did investigation and found the following
 * 
 * [Harold]
 * I found it!

Document id:  cpx_535b58137b8d54a75M4fcd2061377553
    <context name="bconauid"><![CDATA[ ǂ  ǂ  7601458357  ǂ  36646127900  ǂ  56165116300  ǂ  35085535100  ǂ  7601458357  ǂ  16437099300  ǂ  56124535700  ǂ  ǂ ]]></context>

This record has 2 instances of 7601458357 in the auid field. I believe each instance is counted, which results in the count mismatch below, that you reported. Refeed this doc with a single instance to (hopefully ;-) fix the issue.
[/Harold]

* in order to find all similar cases we have now, need to pull all authorIDS from cafe_master and for each record indetify if there any duplication
 */
public class CheckCafeMasterDupAuAfIDs 
{
	public static final String AUDELIMITER = new String(new char[] {30});
	public static final String IDDELIMITER = new String(new char[] {31});
	public static final String GROUPDELIMITER = new String(new char[] {29});

	
	static String doc_type = "apr";

	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction1";
	static String password = "ei3it";
	
	Map<String,Integer> idFrequencyList = new HashMap<String,Integer>();
	
	
	public static void main(String [] args)
	{
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		PrintWriter out =  null;
		
		StringBuffer dbValue = new StringBuffer();
		int max = 0;
		
		
		if(args.length > 4)
		{
			if(args[0] != null)
				doc_type = args[0];
			System.out.println("Find dup Profile IDS for: " + doc_type);
			
			if(args[1] != null)
				url = args[1];
			System.out.println("URL: " + url);
			
			
			if(args[2] != null)
				driver = args[2];
			System.out.println("Driver: " + driver);
			
			if(args[3] != null)
				username = args[3];
			System.out.println("userName: " + username);
			
			if(args[4] != null)
				password = args[4];
		}
		
		else
		{
			System.out.println("Not Enough Parameters!");
			System.exit(1);
		}
		
		
		try
		{
			CheckCafeMasterDupAuAfIDs obj = new CheckCafeMasterDupAuAfIDs();
			out = new PrintWriter(new File("dup_" + doc_type + "_ID_cafe_master.out"));
			
			if(doc_type.equalsIgnoreCase("apr"))
				//query = "select m_id,author||' ' || author_1 as author from cafe_master where rownum<700";
				query = "select m_id,author,author_1 from cafe_master";
			else if (doc_type.equalsIgnoreCase("ipr"))
				query = "select m_id,affiliation,affiliation_1 as affiliation from cafe_master and rownum<11";
			
			System.out.println("Query: " + query);
			
			con = getConnection(url, driver, username, password);
			
			stmt = con.createStatement();
			stmt.setFetchSize(100);
			rs = stmt.executeQuery(query);
			while(rs.next())
			{
				if(rs.getString(2) != null && !(rs.getString(2).isBlank()))
					dbValue.append(rs.getString(2));
				if(rs.getString(3) !=null && !(rs.getString(3).isBlank()))
					dbValue.append(AUDELIMITER).append(rs.getString(3));
				
				if(dbValue.length() >0)
				{
					obj.getProfileIds(dbValue.toString());
					if(obj.idFrequencyList.size() >0)
					{
						max = Collections.max(obj.idFrequencyList.values());
						if(max >1)
						{
							for(String key: obj.idFrequencyList.keySet())
							{
								if(obj.idFrequencyList.get(key) > 1)
									out.print(rs.getString(1) + "\t" + key + "\n");
									
							}
						}
						/*
						 * else System.out.println("This record contains no duplicate ids");
						 */
					}
					
				}
				
				dbValue.delete(0, dbValue.length());   // clear content for next iteration
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(stmt != null)
				{
					stmt.close();
				}
			}
			catch(Exception e)
			{
				System.out.println("Failed to close SQL stmt");
				e.printStackTrace();
			}
			try
			{
				if(rs != null)
				{
					rs.close();
				}
			}
			catch(Exception e)
			{
				System.out.println("Failed to close ResultSet");
				e.printStackTrace();
			}
			try
			{
				if(con != null)
				{
					con.close();
				}
			}
			catch(Exception e)
			{
				System.out.println("Failed to close Connection");
				e.printStackTrace();
			}
			try
			{
				if(out != null)
				{
					out.flush();
					out.close();
				}
			}
			catch(Exception e)
			{
				System.out.println("Failed to close Connection");
				e.printStackTrace();
			}
		}
				
	}
	
	
	protected void getProfileIds(String dbColumnValue)
	{
		String auid = null;
		idFrequencyList.clear();
		 String [] authors = dbColumnValue.split(AUDELIMITER, -1);
		 for(int i=0; i< authors.length; i++)
		 {
			 if(!(authors[i].trim().isBlank()))
			 {
				 String [] auelements = authors[i].trim().split(IDDELIMITER);
				 
				 if(auelements.length > 0)
				 {
					if(!(auelements[1].trim().isEmpty()) && auelements[1].trim().contains(","))
					{
						auid = auelements[1].trim().split(",")[1];
						//System.out.println("AUID: " + auid);
					 
						if(idFrequencyList.containsKey(auid))
						{
							idFrequencyList.put(auid, idFrequencyList.get(auid) + 1);
						}
						else
							idFrequencyList.put(auid,1);
					}
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
