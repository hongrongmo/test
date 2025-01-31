package org.ei.dataloading;

/**
 * @author TELEBH
 * Sample of Query to pass
 * Query: "(st:""Proquest Dissertation And Theses Database"" AND db:""cpx"")"
 * NumOfCount: 3000
 * 
 * Sample of Query to pass #2:
 * Query: (((all:composite)) AND (((NTER:1873.24) OR (NTER:[1873.240000001;])))) AND (yr:[1884;2020]) AND (((db:cpx OR db:c84)) for Rec Count 3000
 * NumOfCount: 2257
 * 
 * sample query for all AFID in STID for Email alerts of ESP
 * 
 * "((afid:60022195 OR afid:60006320 OR afid:60024592 OR afid:60002243 OR afid:60014228 OR afid:60022717 OR afid:60003561 OR afid:60015009 OR afid:60100145 OR afid:60097091)) AND (wk:201944) AND (((db:cpx OR db:c84)) OR ((db:c84))) "
200

 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.ei.domain.FastClient;
import org.ei.exception.SearchException;

public class FetchMidFromFast {
	static String query = "";
    private static Connection con;
    static ResultSet rs = null;
    static Statement stmt = null;
    static String url = "jdbc:oracle:thin:@localhost:1521:eid";
    static String driver = "oracle.jdbc.driver.OracleDriver";
    static String username = "db_xml";
    static String password = "";
    //static String fastUrl = "http://ei-main.nda.fastsearch.net:15100";
    //static String fastUrl = "http://evprod14.cloudapp.net:15100";
    static String fastUrl = "http://evdr09.cloudapp.net:15100";

	/*
	 * static String fastQuery=""; static int pageRecCount = 25;
	 */
    
    
	public static void main(String[] args) throws Exception {
        
		 String fastQuery="";
		 int pageRecCount = 25;
		    
        if(args[0] != null)
        {
        	fastQuery = args[0].toString();
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
        		username = args[3];
        	}

        	if( args[4] != null)
        	{
        		password = args[4];
        	}

        	if(args[5] !=null)
        	{
        		fastUrl = args[5];
        	}

        }
        new FetchMidFromFast().queryFast(fastQuery, pageRecCount);
       
    }

	public void queryFast(String fastQuery, int pageRecCount)
	{
		BufferedReader in = null;
        FileWriter out = null;
		 System.out.println("Fetch accessnumber of query " + fastQuery + " for Rec Count " +  pageRecCount);
	        try {

	           // in = new BufferedReader(new FileReader("fastdocs.txt"));
	        	File file = new File("fastdocs.txt");
	        	if(!file.exists())
	        	{
	        		file.createNewFile();
	        	}
	        	out = new FileWriter(file);
	        	
	            FastClient client = new FastClient();
	            //client.setBaseURL("http://ei-main-p1.nda.fastsearch.net:15100");   //original
	            client.setBaseURL(fastUrl);
	            client.setResultView("ei");
	            client.setOffSet(0);
	            //client.setPageSize(25);   //Default
	            client.setPageSize(pageRecCount);
	            //client.setQueryString("((ti:A) (ti:12-) (ti:W) (ti:to) (ti:1.1-mW))) AND (yr:[1896;2015]) AND (((db:ins OR db:ibf)))");  //HH run sample query
	            //client.setQueryString("(ti:water AND db:cpx)");    //HH another query 
	            //client.setQueryString("(ti:water AND db:cpx AND wk:[201518;201520])");
	            client.setQueryString(fastQuery);
	            client.setDoCatCount(true);
	            client.setDoNavigators(true);
	            //client.setPrimarySort("ausort");  //original
	            client.setPrimarySort("rank");
	            client.setPrimarySortDirection("+");
	            client.search();

	            int hit_count = client.getHitCount();
	            System.out.println("HitCount: " + hit_count);
	            List<String[]> l = client.getDocIDs();
	            for (int i = 0; i < l.size(); i++) {
	                String[] docID = (String[]) l.get(i);
	                			out.write(docID[0]+"\n");
	                			//out.write("\r");
	                
	            }
	            
	           

	           
	        } catch (IOException | SearchException e) {
				e.printStackTrace();
			} finally {
	            if (in != null) {
	                try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	            
	            if(con !=null)
	            {
	            	try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
	            }
	            
	            if(rs !=null)
	            {
	            	try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
	            }
	            
	            if(out !=null)
	            {
	            	try {
						out.flush();
						out.close();
					} catch (IOException e) {
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
