package org.ei.dataloading.ntis.loadtime;

import java.io.FileReader;
import java.util.Hashtable;
import org.ei.util.GUID;


public class NTISXMLBaseTableDriver

{



	private static NTISBaseTableWriter baseWriter;



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
		c.writeBaseTableFile(infile);
	}

	public NTISXMLBaseTableDriver(int loadN)
	{
		this.loadNumber = loadN;
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
}
