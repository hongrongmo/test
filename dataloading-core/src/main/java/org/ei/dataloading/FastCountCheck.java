package org.ei.dataloading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ei.domain.FastClient;

/*
 * Date: 10/21/2015
 * Desc: Check Fast Total Hit Count for weekly dataloading, to verify with Weekly QA Count. if fast count match 
 * QA, send SNS for later process EmailAlert
 * Parameters: Current WeekNumber (i.e. 201544)
 * Author: HT
 */
public class FastCountCheck {

	
	static String query = "";
    private static Connection con;
    static ResultSet rs = null;
    static Statement stmt = null;
    static String url = "jdbc:oracle:thin:@localhost:1521:eid";
    static String driver = "oracle.jdbc.driver.OracleDriver";
    static String username = "db_xml";
    static String password = "ny5av";
    //static String fastUrl = "http://ei-main.nda.fastsearch.net:15100";
    static String fastUrl = "http://evprod14.cloudapp.net:15100";

    static String [] fastQuery= null;
    static int weekNumber = 0;
    
    static int hitCount = 0;
    
    
	public static void main(String[] args) throws Exception {
        BufferedReader in = null;
        FileWriter out = null;
        
       /* if(args[0] != null)
        {
        	fastQuery = args[0].toString();
        }*/
        
        if(args[0] != null)
        {
        	Pattern pattern = Pattern.compile("^\\d*$");
        	Matcher matcher = pattern.matcher(args[0]);
        	if(matcher.find())
        	{
        		weekNumber = Integer.parseInt(args[0]);
        	}
        	
        }
        
        if(weekNumber !=0)
        {
        	fastQuery = new String[] {("(wk:"+weekNumber+")"), ("(wk:"+weekNumber+" AND db:\"cpx\")"), ("(wk:"+weekNumber+" AND db:\"ins\")"),
        			("(wk:"+weekNumber+" AND db:\"ntis\")"), ("(wk:"+weekNumber+" AND db:\"pch\")"), ("(wk:"+weekNumber+" AND db:\"chm\")"),
        			("(wk:"+weekNumber+" AND db:\"cbn\")"), ("(wk:"+weekNumber+" AND db:\"elt\")"), ("(wk:"+weekNumber+" AND db:\"ept\")"),
        			("(wk:"+weekNumber+" AND db:\"geo\")"), ("(wk:"+weekNumber+" AND db:\"grf\")"), ("(wk:"+weekNumber+" AND db:\"upa\")"),
        			("(wk:"+weekNumber+" AND db:\"eup\")")};
        }
        
        if(args.length > 1)
        {
        	if(args[1] !=null)
        	{
        		url = args[1];
        	}

        	if(args[2] !=null)
        	{
        		username = args[2];
        	}

        	if( args[3] != null)
        	{
        		password = args[3];
        	}

        	if(args[4] !=null)
        	{
        		fastUrl = args[4];
        	}

        }

        System.out.println("Check Fast Count for week " + weekNumber);
        try {

        	File file = new File("fastcounts.txt");
        	if(!file.exists())
        	{
        		file.createNewFile();
        	}
        	
            FastClient client = new FastClient();
            client.setBaseURL(fastUrl);
            client.setResultView("ei");
            client.setOffSet(0);
            client.setPageSize(25);  
            client.setDoCatCount(true);
            client.setDoNavigators(true);
            //client.setPrimarySort("ausort");  //original
            client.setPrimarySort("rank");
            client.setPrimarySortDirection("+");
            
            for(int i = 0;i<fastQuery.length;i++)
            {
            	 client.setQueryString(fastQuery[i]);
            	 
            	 System.out.println("Query: " +  fastQuery[i]);
            	 client.search();

                 hitCount = client.getHitCount();
                 
                System.out.println("Total Fast HitCount is : " + hitCount);
                     
            }
           
           
           
        } finally {
            
            if(con !=null)
            {
            	con.close();
            }
            
            if(rs !=null)
            {
            	rs.close();
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
