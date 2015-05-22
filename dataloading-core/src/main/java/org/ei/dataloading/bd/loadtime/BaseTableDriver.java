package org.ei.dataloading.bd.loadtime;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Hashtable;
import java.util.Enumeration;
//import org.ei.data.LoadNumber;
//import org.ei.data.bd.*;
import java.sql.*;

public class BaseTableDriver
{
    private static BaseTableWriter baseWriter;
    private int loadNumber;
    private String databaseName;
    private String action;
    private static String startRootElement ="<?xml version=\"1.0\" standalone=\"no\"?><!DOCTYPE bibdataset SYSTEM \"ani512.dtd\"><bibdataset xmlns:ce=\"http://www.elsevier.com/xml/common/dtd\" xmlns:ait=\"http://www.elsevier.com/xml/ait/dtd\">";
    private static String endRootElement   ="</bibdataset>";
    private static Connection con;
    private static String infile;
    private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String username = "ba_loading";
    private static String password = "ny5av";

    public static void main(String args[])
        throws Exception

    {
        int loadN=0;
        long startTime = System.currentTimeMillis();
        if(args.length<8)
        {
            System.out.println("please enter three parameters as \" weeknumber filename databaseName action url driver username password\"");
            System.exit(1);
        }

        loadN = Integer.parseInt(args[0]);

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
        BaseTableDriver c;

        try
        {

            if(action!=null)
            {
                c = new BaseTableDriver(loadN,databaseName,action);
                System.out.println("action="+action);
            }
            else
            {
                c = new BaseTableDriver(loadN,databaseName);
            }

            con = c.getConnection(url,driver,username,password);
            c.writeBaseTableFile(infile,con);
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

    public BaseTableDriver(int loadN,String databaseName)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
        this.action = "normal";
    }

    public BaseTableDriver(int loadN,String databaseName,String action)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
        this.action = action;
    }

    public void writeBaseTableFile(String infile, Connection con)
        throws Exception
    {
        BufferedReader in = null;
        this.infile = infile;
        try
        {
            baseWriter = new BaseTableWriter(infile+"."+loadNumber+".out");
            baseWriter.begin();
            if(infile.toLowerCase().endsWith(".zip"))
            {
                System.out.println("IS ZIP FILE");
                ZipFile zipFile = new ZipFile(infile);
                Enumeration entries = zipFile.entries();
                while (entries.hasMoreElements())
                {
                    ZipEntry entry = (ZipEntry)entries.nextElement();
                    in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
                    writeRecs(in,con);
                }
            }
            else if(infile.toLowerCase().endsWith(".xml"))
            {
                System.out.println("IS XML FILE");
                in = new BufferedReader(new FileReader(infile));
                writeRecs(in,con);
            }
            else
            {
                System.out.println("this application only handle xml and zip file");
            }

            baseWriter.end();

        }
        catch (IOException e)
        {
            System.err.println(e);
            System.exit(1);
        }
        finally
        {
            if(in != null)
            {
                in.close();
            }
        }
    }

    private boolean checkPUI(String pui, Connection con)
    {
        Statement stmt = null;
        ResultSet rs = null;
        int count = 0;
        boolean checkResult = false;
        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery("select count(m_id) count from bd_master_orig where pui='"+pui+"'");
            while (rs.next())
            {
                count = rs.getInt("count");
            }

            if(count>0)
            {
                checkResult=true;
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
        return checkResult;
    }

    private void writeRecs(BufferedReader xmlReader, Connection con) throws Exception
    {
        FileWriter out = new FileWriter(this.infile+"_not_loaded_record.txt");
        try
        {
            RecordReader r = new RecordReader(loadNumber,databaseName);

            while(xmlReader!=null)
            {
                Hashtable h = r.readRecord(xmlReader);
                if (h != null)
                {
                    if(action.equals("aip"))
                    {
                        if(!checkPUI((String)h.get("PUI"),con))
                        {
                            baseWriter.writeRec(h);
                        }
                        else
                        {
                            out.write((String)h.get("PUI")+"\n");
                        }
                    }
                    else
                    {
                        baseWriter.writeRec(h);
                    }
                }
                else
                {
                  break;
                }
            }
            out.flush();
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    class RecordReader
    {
        BdParser r;
        int loadNumber;
        String databaseName;
        //String action;

        RecordReader(int loadNumber,String databaseName)
        {
            this.loadNumber = loadNumber;
            this.databaseName = databaseName;
        }
        Hashtable readRecord(BufferedReader xmlReader) throws Exception
        {
            String line = null;
            r = new BdParser();
            StringBuffer sBuffer = new StringBuffer();
            r.setWeekNumber(Integer.toString(loadNumber));
            r.setDatabaseName(databaseName);
            //r.setAction(action);
            boolean start = false;
            while((line=xmlReader.readLine())!=null)
            {
                if(start)
                {
                    sBuffer.append(line);
                }

                if(line.indexOf("<item>")>-1)
                {
                    start = true;
                    sBuffer = new StringBuffer();
                    sBuffer.append(line);
                }

                if(line.indexOf("</item>")>-1)
                {
                    start = false;

                }

                if(!start)
                {
                    if(sBuffer!=null && sBuffer.length()>0)
                    {
                    	try{
	                        sBuffer.insert(0,startRootElement);
	                        sBuffer.append(endRootElement);
	                        r.parseRecord(new StringReader(sBuffer.toString()));
                    	}
                    	catch(Exception e)
                    	{
                    		System.out.println(sBuffer.toString());
                    		e.printStackTrace();
                    	}
	                        sBuffer = new StringBuffer();
                    }

                    if(r.getRecordTable()!=null)
                    {
                        return r.getRecordTable();
                    }
                }
            }

            return null;
        }
    }

     protected Connection getConnection(String connectionURL,
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
}
