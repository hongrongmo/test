package org.ei.dataloading.cafe;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.ei.dataloading.bd.loadtime.BaseTableDriver;
import org.ei.dataloading.bd.loadtime.BaseTableWriter;

public class CafeLoading
{
	private static BaseTableWriter baseWriter;
    private int loadNumber;
    private String databaseName;
    private String action;
    private static String startRootElement ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><bibdataset xmlns:xocs=\"http://www.elsevier.com/xml/xocs/dtd\" xmlns:ce=\"http://www.elsevier.com/xml/common/dtd\" xmlns:xoe=\"http://www.elsevier.com/xml/xoe/dtd\" xmlns:aii=\"http://www.elsevier.com/xml/ani/internal\" xmlns:mml=\"http://www.w3.org/1998/Math/MathML\" xmlns:ait=\"http://www.elsevier.com/xml/ait/dtd\">";
    private static String endRootElement   ="</bibdataset>";
    private static Connection con;
    private static String infile;
    private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String username = "ba_loading";
    private static String password = "";
    private static List<String> blockedCpxIssnList;
    private static List<String> blockedPchIssnList;
    private static List<String> blockedGeoIssnList;
    private static List<String> cittypeList;
    //HH 02/2016 for Cafe Processing
    BufferedReader bfReader = null;
    boolean cafe = false;
    public String s3FileLoc = "";

   
    public static void main(String args[])
        throws Exception

    {
        String archivedDate="0";
        long startTime = System.currentTimeMillis();
        if(args.length<3)
        {
            System.out.println("please enter three parameters as \" weeknumber filename databaseName action url driver username password\"");
            System.exit(1);
        }
        
        archivedDate = args[0];

        infile = args[1];
        String databaseName = args[2];
        String action = null;
        if(args.length>3)
        {
            url = args[4];
            driver = args[5];
            username = args[6];
            password = args[7];
            action = args[3];
        }
        else
        {
            System.out.println("USING DEFAULT DATABASE SETTING");
            System.out.println("DATABASE URL= "+url);
            System.out.println("DATABASE USERNAME= "+username);
            System.out.println("DATABASE PASSWORD= "+password);
        }
        CafeLoading c;

        try
        {

            if(action!=null)
            {
                c = new CafeLoading(archivedDate,databaseName,action);
                System.out.println("action="+action);
            }
            else
            {
                c = new CafeLoading(archivedDate,databaseName);
            }

            con = c.getConnection(url,driver,username,password);
            c.setBlockedIssnList(con);
            List cafekeyList = c.getCafeKeys(archivedDate,con);
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
            System.out.println("total process time "+(System.currentTimeMillis()-startTime)/1000.0+" seconds");
        }
    }
    
    private List getCafeKeys(String archivedDate,Connection con)
    {
    	List cafeDataKeyList = new ArrayList();
    	return cafeDataKeyList;
    }
    
    public CafeLoading(int loadN,String databaseName)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
        this.action = "normal";
    }

    public CafeLoading(int loadN,String databaseName,String action)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
        this.action = action;
    }
    
    public CafeLoading(int loadN,String databaseName, String action, String fileLocation)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
        this.action = "normal";
        this.s3FileLoc = fileLocation;
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
    
    public void setBlockedIssnList(Connection con)
    {
    	Statement stmt = null;
        ResultSet rs = null;
        List<String> cpxIssnList = new ArrayList<>();
        List<String> pchIssnList = new ArrayList<>();
        List<String> geoIssnList = new ArrayList<>();
        String issn = null;
        String database = null;
        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery("select sn,database from blocked_issn");
            while (rs.next())
            {
                issn = rs.getString("sn");
                database = rs.getString("database");
                if(issn != null)
                {
                	if(database.equals("cpx"))
                	{
                		cpxIssnList.add(issn);
                	}
                	else if(database.equals("pch"))
                	{
                		pchIssnList.add(issn);
                	}
                	else if(database.equals("geo"))
                	{
                		geoIssnList.add(issn);
                	}
                }
            }

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
        this.blockedCpxIssnList=cpxIssnList;
        this.blockedPchIssnList=pchIssnList;
        this.blockedGeoIssnList=geoIssnList;
       
    }
}