package org.ei.data.bd.loadtime;import java.io.BufferedReader;import java.io.FileInputStream;import java.io.FileReader;import java.io.StringReader;import java.io.IOException;import java.io.InputStreamReader;import java.util.zip.ZipEntry;import java.util.zip.ZipFile;import java.util.Hashtable;import java.util.Enumeration;import org.ei.data.LoadNumber;import org.ei.data.bd.*;public class BaseTableDriver{    private static BaseTableWriter baseWriter;    private int loadNumber;    private String databaseName;    private static String startRootElement ="<?xml version=\"1.0\" standalone=\"no\"?><!DOCTYPE bibdataset SYSTEM \"ani512.dtd\"><bibdataset xmlns:ce=\"http://www.elsevier.com/xml/common/dtd\" xmlns:ait=\"http://www.elsevier.com/xml/ait/dtd\">";	private static String endRootElement   ="</bibdataset>";    public static void main(String args[])        throws Exception    {        int loadN=0;        if(args.length<3)        {			System.out.println("please enter three parameters as \" weeknumber filename databaseName \"");			System.exit(1);		}        loadN = Integer.parseInt(args[0]);        String infile = args[1];        String databaseName = args[2];        BaseTableDriver c = new BaseTableDriver(loadN,databaseName);        c.writeBaseTableFile(infile);    }    public BaseTableDriver(int loadN,String databaseName)    {        this.loadNumber = loadN;		this.databaseName = databaseName;    }    public void writeBaseTableFile(String infile)        throws Exception    {        BufferedReader in = null;        try        {            baseWriter = new BaseTableWriter(infile+"."+loadNumber+".out");            baseWriter.begin();            if(infile.toLowerCase().endsWith(".zip"))            {				ZipFile zipFile = new ZipFile(infile);				Enumeration entries = zipFile.entries();				while (entries.hasMoreElements())				{					ZipEntry entry = (ZipEntry)entries.nextElement();					in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));					writeRecs(in);				}			}            else if(infile.toLowerCase().endsWith(".xml"))            {                in = new BufferedReader(new FileReader(infile));				writeRecs(in);			}			else			{				System.out.println("this application only handle xml and zip file");			}            baseWriter.end();        }        catch (IOException e)        {            System.err.println(e);            System.exit(1);        }        finally        {            if(in != null)            {                in.close();            }        }    }    private void writeRecs(BufferedReader xmlReader) throws Exception	{        	RecordReader r = new RecordReader(loadNumber,databaseName);    		while(xmlReader!=null)    		{    		    Hashtable h = r.readRecord(xmlReader);    		    if (h != null)    		    {    		        baseWriter.writeRec(h);    		    }    		    else    		    {    		      break;    		    }    		}    }    class RecordReader    {        BdParser r;        int loadNumber;		String databaseName;        RecordReader(int loadNumber,String databaseName)        {            this.loadNumber = loadNumber;            this.databaseName = databaseName;        }        Hashtable readRecord(BufferedReader xmlReader) throws Exception        {    		String line = null;    		r = new BdParser();    		StringBuffer sBuffer = new StringBuffer();    		r.setWeekNumber(Integer.toString(loadNumber));    		r.setDatabaseName(databaseName);    		boolean start = false;    		while((line=xmlReader.readLine())!=null)    		{    			if(start)    			{    				sBuffer.append(line);    			}    			if(line.indexOf("<item>")>-1)    			{    				start = true;    				sBuffer = new StringBuffer();    				sBuffer.append(line);    			}    			if(line.indexOf("</item>")>-1)    			{    				start = false;    			}    			if(!start)    			{    				if(sBuffer!=null && sBuffer.length()>0)    				{    					sBuffer.insert(0,startRootElement);    					sBuffer.append(endRootElement);    					r.parseRecord(new StringReader(sBuffer.toString()));    					sBuffer = new StringBuffer();    				}    				if(r.getRecordTable()!=null)    				{    				    return r.getRecordTable();    				}    			}    		}    		return null;        }    }}