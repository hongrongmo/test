package org.ei.dataloading.bd.loadtime;


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

public class BaseTableDriver
{
    private static BaseTableWriter baseWriter;
    private int loadNumber;
    private String databaseName;
    private String action;
    private static String startRootElement ="<?xml version=\"1.0\" standalone=\"no\"?><!DOCTYPE bibdataset SYSTEM \"ani512.dtd\"><bibdataset xmlns:ce=\"http://www.elsevier.com/xml/common/dtd\" xmlns:xoe=\"http://www.elsevier.com/xml/xoe/dtd\" xmlns:ait=\"http://www.elsevier.com/xml/ait/dtd\">";
    private static String endRootElement   ="</bibdataset>";
    private static Connection con;
    private static String infile;
    private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String username = "ba_loading";
    private static String password = "ny5av";
    private static List<String> blockedIssnList;
    //HH 02/2016 for Cafe Processing
    BufferedReader bfReader = null;
    boolean cafe = false;
    public String s3FileLoc = "";

   
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
            c.setBlockedIssnList(con);
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
    
    public void setBlockedIssnList(Connection con)
    {
    	Statement stmt = null;
        ResultSet rs = null;
        List<String> issnList = new ArrayList<>();
        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery("select sn from blocked_issn");
            while (rs.next())
            {
                String issn = rs.getString("sn");
                if(issn != null)
                {
                	issnList.add(issn);
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
        this.blockedIssnList=issnList;
       
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
    
    //HH 04/05/2016 for Cafe converting
    public BaseTableDriver(int loadN,String databaseName, String action, String fileLocation)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
        this.action = "normal";
        this.s3FileLoc = fileLocation;
    }
    
    
    //HH 02/2016 for Cafe COnverting
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
    
    
    //HH 04/30/2016 for cafe Converting, send reference to GetANIFileFromCafeS3Bucket to access bufferedreader itself instead of copy it
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
            else if (this.cafe)   //HH 02/2016 for Cafe
            {
              System.out.println("IS CAFE FILE");
              //HH 04/30/2016 sending copy of Bufferedreader from GetANIFromS3Bucket 
              /*in = this.bfReader;
              writeRecs(in, con);*/
              writeRecs(GetANIFileFromCafeS3Bucket.getBufferedReader(), con); 
              
            }
            else
            {
                System.out.println("this application only handle xml and zip file");
            }

            /* remove these to avoid clog log file
             * 
            List nullAccessNumberRecordList = baseWriter.getNullAccessNumberRecord();          
            if(nullAccessNumberRecordList.size()>0)
            {
            	System.out.println("there are "+baseWriter.getNullAccessNumberRecord().size()+" records with null accessnumber");
            	System.out.println("the PUI for these records are");
            	for(int i=0;i<nullAccessNumberRecordList.size();i++)
            	{
            		String pui = (String)nullAccessNumberRecordList.get(i);
            		//System.out.print(pui);
            		if(i<nullAccessNumberRecordList.size()-1)
            		{
            			System.out.print(",");
            		}
            		else
            		{
            			System.out.print(".\n");
            		}
            	}
            }
            */
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
    	File outFile = new File(this.infile+"_not_loaded_record.txt");
    	if(outFile.exists())
    	{
    		outFile.setReadable(true);
    		outFile.setWritable(true);
    	}
        FileWriter out = new FileWriter(outFile);
        RecordReader r;   //HH 04/05/2016 for cafe
        try
        {
            //RecordReader r = new RecordReader(loadNumber,databaseName);
            
        	//HH 04/05/2016 for Cafe S3_file_location
        	if(s3FileLoc !=null && s3FileLoc.length() >0)
        	{
        		 r = new RecordReader(loadNumber,databaseName,s3FileLoc);
        	}
        	else
        	{
        		r = new RecordReader(loadNumber,databaseName);
        	}
        	
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
        
      //HH 04/05/2016 for Cafe
        String s3FileLoc = "";

        RecordReader(int loadNumber,String databaseName)
        {
            this.loadNumber = loadNumber;
            this.databaseName = databaseName;
        }
        
        //HH 04/05/2016 fro Cafe S3_File_Loc
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
            r.setWeekNumber(Integer.toString(loadNumber));
            r.setDatabaseName(databaseName);
            //r.setAction(action);
            r.setCafeS3Loc(s3FileLoc);   //HH 04/05/2016
            boolean start = false;
           
            while((line=xmlReader.readLine())!=null)
            {
            	
            	if(line.indexOf("<xoe:document-id>")>-1)
            	{
            		document_eid = line;                     	
            	}
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
	                        sBuffer.append(endRootElement);
	                        //System.out.println(sBuffer.toString());
	                        r.parseRecord(new StringReader(sBuffer.toString()));
                    	}
                    	catch(Exception e)
                    	{
                    		System.out.println(sBuffer.toString());
                    		e.printStackTrace();
                    	}
	                        sBuffer = new StringBuffer();
                    }

                  //pre frank request, block certain issns just for cpx and pch 8/3/2015
                    if(r.getRecordTable()!=null)
                    {
                    	Hashtable ht=r.getRecordTable();
                    	//System.out.println("ACCESSNUMBER:"+ht.get("ACCESSNUMBER"));
                    	if((ht.get("DATABASE")!=null && (((String)ht.get("DATABASE")).equals("cpx") || ((String)ht.get("DATABASE")).equals("pch"))))
                		{
                    		boolean issnFlag=false;
                    		
                    		if(blockedIssnList!=null)
                    		{
		                    	for(int i=0;i<blockedIssnList.size();i++)
		                    	{
		                    		  String blockedIssn = blockedIssnList.get(i);
		                    		  
		                    		  if(blockedIssn.indexOf("EISSN")>-1)
		                    		  {
		                    			 
		                    			  blockedIssn = blockedIssn.substring(blockedIssn.indexOf(":")+1);
		                    			  if(ht.get("EISSN")!=null)
		                    			  {
			                    			  String eissn = (String)ht.get("EISSN");
			                    			  eissn=eissn.replaceAll("-", "");
			                    			  if (eissn.equals(blockedIssn))
					                		   {			       
					                			   System.out.println("block record "+ht.get("ACCESSNUMBER")+" for eissn="+blockedIssn+" from BaseTableDriver");
					                			   issnFlag=true;
					                			   break;
					                		   }
		                    			  }
		                    		  }
		                    		  else if(blockedIssn.indexOf("ISSN")>-1)
		                    		  {
		                    			  blockedIssn = blockedIssn.substring(blockedIssn.indexOf(":")+1);
		                    			  if (ht.get("ISSN")!=null)
		                    			  {	                   
		                    				  String issn = (String)ht.get("ISSN");
		                    				  issn=issn.replaceAll("-", "");
					                		  if (issn.equals(blockedIssn))
					                		  {			                			   
					                			   System.out.println("block record "+ht.get("ACCESSNUMBER")+" for issn="+blockedIssn+" from BaseTableDriver");
					                			   issnFlag=true;
					                			   break;
					                		  }
		                    			  }
		                    		  }	
		                    		  else
		                    		  {
		                    			  System.out.println("incorrect issn format, check blocked_issn table");
		                    		  }
		                    	  }
                    		}
                    		
	                    	if(issnFlag==true)
							{
	                    		continue;
							}
                		}
                    	
                        return r.getRecordTable();
                    }
                   
                }
               
            }
            

            return null;
        }
       
    }


    //HH 02/2016 for Cafe
    // original was protected

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
