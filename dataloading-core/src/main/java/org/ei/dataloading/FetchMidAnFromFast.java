package org.ei.dataloading;

/*
 * Date: 05/06/2015
 * Desc: get accessnumber list for list of M_ID returned from fast for certain query, this functionality is used more often
 * for our fast investigation (i.e M_id in fast and not in DB and vice versa)
 * Author: HT
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

    
	public static void main(String[] args) throws Exception {
        BufferedReader in = null;
        FileWriter out = null;
        
        
        try {

           // in = new BufferedReader(new FileReader("fastdocs.txt"));
        	File file = new File("fastdocs.txt");
        	if(!file.exists())
        	{
        		file.createNewFile();
        	}
        	out = new FileWriter(file);
        	
            FastClient client = new FastClient();
            client.setBaseURL("http://ei-main-p1.nda.fastsearch.net:15100");
            client.setResultView("ei");
            client.setOffSet(0);
            client.setPageSize(25);
            //client.setQueryString("((ti:A) (ti:12-) (ti:W) (ti:to) (ti:1.1-mW))) AND (yr:[1896;2015]) AND (((db:ins OR db:ibf)))");
            client.setQueryString("(ti:water AND db:cpx)");
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
                System.out.println("DOC ID: " +  docID[0]);
                
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
