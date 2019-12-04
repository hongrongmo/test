package org.ei.dataloading;

/*
 * Date: 05/06/2015
 * Desc: get accessnumber list for list of M_ID returned from fast for certain query, this functionality is used more often
 * for our fast investigation (i.e M_id in fast and not in DB and vice versa)
 * Parameters: query & # of records  to fetch from fast & so from DB 
 * Author: TELEBH
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.ei.domain.FastClient;


public class FetchMidAnFromFast {

	static String query = "";
    private static Connection con;
    static ResultSet rs = null;
    static Statement stmt = null;
    static String url = "jdbc:oracle:thin:@localhost:1521:eid";
    static String driver = "oracle.jdbc.driver.OracleDriver";
    static String username = "db_xml";
    static String password = "ny5av";
    static String fastUrl = "http://evprod14.cloudapp.net:15100";

    static String fastQuery="";
    static int pageRecCount = 25;
    
    
	public static void main(String[] args) throws Exception {
        BufferedReader in = null;
        FileWriter out = null;
        
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
            client.setPrimarySort("ausort");
            client.setPrimarySortDirection("+");
            client.search();

            con = getConnection(url,driver,username,password);
            stmt = con.createStatement();
            
            
            List<String[]> l = client.getDocIDs();
            for (int i = 0; i < l.size(); i++) {
                String[] docID = (String[]) l.get(i);
               
                query = "select accessnumber from bd_master where m_id= '" + docID[0]+"'";
                rs = stmt.executeQuery(query);
                
                if(rs!=null)
                {
                	while(rs.next())
                	{
                		if(rs.getString("accessnumber") !=null)
                		{
                			out.write(rs.getString("accessnumber"));
                			out.write("\r\n");
                		}
                	}
                }
                
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

	protected static Connection getConnection(String connectionURL, String driver, String username, String password) throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL, username, password);
		return con;
	}
}
