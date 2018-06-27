package org.ei.dataloading.ntis.loadtime;

import java.io.*;
import java.util.*;
import org.ei.util.GUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class NTISBaseTableDriver

{

	private static NTISBaseTableWriter baseWriter;
	private static NTISXMLBaseTableWriter baseXMLWriter;
	private static int exitNumber;
	private int counter = 0;
	private int loadNumber;


	public static void main(String args[])
		throws Exception

	{
		int loadN = Integer.parseInt(args[0]);
		String infile = args[1];
		if(args.length == 3)
		{
			exitNumber = Integer.parseInt(args[2]);
		}
		else
		{
			exitNumber = 0;
		}

		NTISBaseTableDriver c = new NTISBaseTableDriver(loadN);
		if(infile!=null && (infile.toLowerCase().endsWith(".xml") ||infile.toLowerCase().endsWith(".zip")))
		{
			c.writeXMLBaseTableFile(infile);
		}
		else
		{
			c.writeBaseTableFile(infile);
		}
		System.exit(1);
	}

	public NTISBaseTableDriver(int loadN)
	{
		this.loadNumber = loadN;
	}

	public void writeXMLBaseTableFile(String infile)
			throws Exception
	{


		BufferedReader in = null;
		//this.infile = infile;
		try
		{
			baseXMLWriter = new NTISXMLBaseTableWriter(infile+"."+loadNumber+".out");
			baseXMLWriter.begin();
			if(infile.toLowerCase().endsWith(".zip"))
			{
				System.out.println("IS ZIP FILE");
				ZipFile zipFile = new ZipFile(infile);
				Enumeration entries = zipFile.entries();
				while (entries.hasMoreElements())
				{
					ZipEntry entry = (ZipEntry)entries.nextElement();
					in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
					writeXMLRecs(in);
				}
			}
			else if(infile.toLowerCase().endsWith(".xml"))
			{
				System.out.println("IS XML FILE");
				in = new BufferedReader(new FileReader(infile));
				writeXMLRecs(in);
			}


			baseXMLWriter.end();

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

	private void writeXMLRecs(BufferedReader in)
			throws Exception
	{
		try{

			RecordReader r = new RecordReader(loadNumber,"ntis");


				List l = r.readRecord(in);
				System.out.println("total record counts= "+l.size());


				if(l != null)
				{
					for(int i=0;i<l.size();i++)
					{
						Hashtable h=(Hashtable)l.get(i);
						baseXMLWriter.writeXMLRec(h);
					}
				}


		}
		catch(Exception e)
		{
			throw new Exception(e);
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}


	public void writeBaseTableFile(String infile)
		throws Exception
	{
		NTISLogicalRecordReader in = null;

		try
		{
			baseWriter = new NTISBaseTableWriter(30000000, infile+".out");
			in = new NTISLogicalRecordReader(new FileReader(infile));
			baseWriter.begin();
			try
			{
				writeRecs(in);
			}
			catch(Exception e)
			{
				System.out.println("Exception:"+e.getMessage());
			}

			baseWriter.end();
		}
		finally
		{
			if(in != null)
			{
				in.close();
			}
		}
	}

	private void writeRecs(NTISLogicalRecordReader in)
		throws Exception
	{
		Hashtable record = null;
		while((record = in.readLogicalRecord()) != null)
		{
			if(record != null)
			{
				record.put("M_ID", new String("ntis_"+new GUID().toString()));
				record.put("LOAD_NUMBER",new String(Integer.toString(loadNumber)));
				baseWriter.writeRec(record);
			}
			counter++;
			if(exitNumber != 0 &&
			   counter == exitNumber)
			{
				break;
			}
		}


	}

	 class RecordReader
	 {
	        NTISParser r;
	        int loadNumber;
			String databaseName;
			private String startRootElement ="<?xml version=\"1.0\" encoding=\"UTF-8\"?><Records>";
			private String endRootElement   ="</Records>";
			//String action;

	        RecordReader(int loadNumber,String databaseName)
	        {
	            this.loadNumber = loadNumber;
	            this.databaseName = databaseName;
	        }
	        List readRecord(BufferedReader xmlReader) throws Exception
	        {
	    		String line = null;
	    		List resultList = new ArrayList();
	    		r = new NTISParser();
	    		StringBuffer sBuffer = new StringBuffer();
	    		r.setWeekNumber(Integer.toString(loadNumber));
	    		r.setDatabaseName(databaseName);
	    		//r.setAction(action);
	    		boolean start = false;
	    		while((line=xmlReader.readLine())!=null && !line.trim().equals("</Records>"))
	    		{
	    			if(start)
	    			{
	    				sBuffer.append(line);
	    			}

	    			if(line.indexOf("<Record>")>-1)
	    			{
	    				start = true;
	    				sBuffer = new StringBuffer();
	    				sBuffer.append(line);
	    			}

	    			if(line.indexOf("</Record>")>-1)
	    			{
	    				start = false;

	    			}

	    			if(!start)
	    			{
	    				if(sBuffer!=null && sBuffer.length()>0)
	    				{
	    					sBuffer.insert(0,startRootElement);
	    					sBuffer.append(endRootElement);
	    					r.parseRecord(new StringReader(sBuffer.toString()));
	    					sBuffer = new StringBuffer();
	    				}

	    				if(r.getRecordTable()!=null)
	    				{
	    				    resultList.add(r.getRecordTable());
	    				}
	    			}
	    		}
	    		return resultList;

	        }
    }
}
