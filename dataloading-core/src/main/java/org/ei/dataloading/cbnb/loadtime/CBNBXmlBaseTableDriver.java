package org.ei.dataloading.cbnb.loadtime;


import java.io.BufferedReader;
import java.io.File;
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
import java.util.List;
import java.util.ArrayList;
//import org.ei.data.LoadNumber;
//import org.ei.dataloading.bd.*;
import java.sql.*;

import org.ei.dataloading.cafe.GetANIFileFromCafeS3Bucket;
import org.ei.dataloading.bd.loadtime.BdParser;

public class CBNBXmlBaseTableDriver
{
    private static CBNBBaseTableWriter baseWriter;
    private int loadNumber;
    private String databaseName;
    private String action;
    private static String startRootElement ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><bibdataset xmlns:xocs=\"http://www.elsevier.com/xml/xocs/dtd\" xmlns:ce=\"http://www.elsevier.com/xml/common/dtd\" xmlns:xoe=\"http://www.elsevier.com/xml/xoe/dtd\" xmlns:ait=\"http://www.elsevier.com/xml/ait/dtd\">";
    private static String endRootElement   ="</bibdataset>";
    private static Connection con;
    private static String infile;
    private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String username = "ap_ev_search";
    private static String password = "ei3it";
    private static List<String> blockedCpxIssnList;
    private static List<String> blockedPchIssnList;
    private static List<String> blockedGeoIssnList;

    BufferedReader bfReader = null;
    boolean cafe = false;
    public String s3FileLoc = "";

   
    public static void main(String args[])
        throws Exception

    {
        int loadN=0;
        long startTime = System.currentTimeMillis();
        if(args.length<3)
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
        CBNBXmlBaseTableDriver c;

        try
        {

            if(action!=null)
            {
                c = new CBNBXmlBaseTableDriver(loadN,databaseName,action);
                System.out.println("action="+action);
            }
            else
            {
                c = new CBNBXmlBaseTableDriver(loadN,databaseName);
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
    
    
    public CBNBXmlBaseTableDriver(int loadN,String databaseName)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
        this.action = "normal";
    }

    public CBNBXmlBaseTableDriver(int loadN,String databaseName,String action)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
        this.action = action;
    }
    
    public CBNBXmlBaseTableDriver(int loadN,String databaseName, String action, String fileLocation)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
        this.action = "normal";
        this.s3FileLoc = fileLocation;
    }
    
   
    public void writeBaseTableFile(String infile, Connection con, BufferedReader reader, boolean cafe)
    {
      try
      {
        this.bfReader = reader;
        this.cafe = cafe;
        writeBaseTableFile(infile, con);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    
    

    public void writeBaseTableFile(String infile, Connection con, boolean cafe)
    {
      try
      {
        this.cafe = cafe;
        writeBaseTableFile(infile, con);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    

    public void writeBaseTableFile(String infile, Connection con)
        throws Exception
    {
        BufferedReader in = null;
        this.infile = infile;
        try
        {
            baseWriter = new CBNBBaseTableWriter(infile+"."+loadNumber+".out");
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
                //in = new BufferedReader(new FileReader(infile));//new InputStreamReader(is, "UTF-8"));
                File file = new File(infile);
                in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
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

    private void writeRecs(BufferedReader xmlReader, Connection con) throws Exception
    {
    	File outFile = new File(this.infile+"_not_loaded_record.txt");
    	if(outFile.exists())
    	{
    		outFile.setReadable(true);
    		outFile.setWritable(true);
    	}
        FileWriter out = new FileWriter(outFile);
        RecordReader r;   
        try
        {          
        	if(s3FileLoc !=null && s3FileLoc.length() >0)
        	{
        		 r = new RecordReader(loadNumber,databaseName,s3FileLoc);
        	}
        	else
        	{
        		r = new RecordReader(loadNumber,databaseName);
        	}
        	int recordCount = 0;
            while(xmlReader!=null)
            {
            	recordCount++;
                Hashtable h = r.readRecord(xmlReader);
            
                if (h != null)
                {                   
                	baseWriter.writeXmlRec(h);                   
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
           e.printStackTrace();
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
     
        String s3FileLoc = "";

        RecordReader(int loadNumber,String databaseName)
        {
            this.loadNumber = loadNumber;
            this.databaseName = databaseName;
        }
        
        RecordReader(int loadNumber,String databaseName, String fileLocation)
        {
            this.loadNumber = loadNumber;
            this.databaseName = databaseName;
            this.s3FileLoc = fileLocation;
        }
        
        Hashtable readRecord(BufferedReader xmlReader) throws Exception
        {
            String line = null;
            String document_eid = null;
            r = new BdParser();
            StringBuffer sBuffer = new StringBuffer();
            StringBuffer fundBuffer = new StringBuffer();
           
            //added on 6/8/2018 for limiting loadnumber to six digits
            String loadnumber = Integer.toString(loadNumber);
            //added for getting updatenumber by hmo on 5/15/2019
            r.setUpdateNumber(loadnumber);
            //System.out.println("loadnumber="+loadnumber);
            if(loadnumber.length()>0)
            {
            	loadnumber = loadnumber.substring(0,6);
            }
            //System.out.println("loadnumber1="+loadnumber);
            r.setWeekNumber(loadnumber);
            r.setDatabaseName(databaseName);
            //r.setAction(action);
            r.setCafeS3Loc(s3FileLoc);   //HH 04/05/2016
            boolean start = false;
            boolean fundingStart = false;
            boolean openaccessStart = false;
           
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
                    if(document_eid!=null)
                    {
                    	sBuffer.append(document_eid);                   	
                    }
                   
                }

                if(line.indexOf("</item>")>-1)
                {
                    start = false; 
                    document_eid = null;
                }

                if(!start)
                {               	
                    if(sBuffer!=null && sBuffer.length()>0)
                    {
                    	try{
	                        sBuffer.insert(0,startRootElement);
	                        
	                        if(fundBuffer.length()>0)
	                        {
	                        	sBuffer.append(fundBuffer.toString());
	                        }
	                        
	                        sBuffer.append(endRootElement);
	                        //System.out.println("CONTENT="+sBuffer.toString());
	                        r.parseRecord(new StringReader(sBuffer.toString()));
                    	}
                    	catch(Exception e)
                    	{
                    		System.out.println("ERROR="+sBuffer.toString());
                    		e.printStackTrace();
                    	}
	                    sBuffer = new StringBuffer();
                    }//if
       
                    if(r.getRecordTable()!=null)
                    {
                    	//System.out.println("got records");
                    	return r.getRecordTable();
                    }//if
                   
                }//if             
               
            }
            
            
            return null;
        }
        
    }
    

     public Connection getConnection(String connectionURL,
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