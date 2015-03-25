package org.ei.dataloading.ntis.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.oro.text.perl.Perl5Util;

public class NTISBaseTableWriter
{

	private Perl5Util perl = new Perl5Util();
	private int recsPerFile = -1;
	private int curRecNum = 0;
	private String filename;
	private PrintWriter out;
	private String filepath;
	private int loadnumber;
	private int filenumber = 0;
	private boolean open = false;
	private static Properties props;
	private String[] baseTableFields = NTISBaseTableRecord.baseTableFields;



	public NTISBaseTableWriter(int recsPerFile,
							   String filename)
	{
		this.recsPerFile = recsPerFile;
		this.filename = filename;
	}


	public void begin()
			throws Exception
	{
		++filenumber;
		out = new PrintWriter(new FileWriter(filename+"."+filenumber));
		open = true;
		curRecNum = 0;
	}

	public void writeRec(Hashtable record)
			throws Exception
	{

			if(curRecNum >= recsPerFile)
			{
				if(open)
				{
					end();
				}
				begin();
			}

			StringBuffer recordBuf = new StringBuffer();
			for(int i=0; i<baseTableFields.length; ++i)
			{
				String bf = baseTableFields[i];
				if(record == null)
				{
					System.out.println("Record was null");
				}

				String value = (String)record.get(bf);

				if(value != null)
				{
					value = perl.substitute("s/\\t/     /g", value);
				}


				if(i > 0)
				{
					recordBuf.append("	");
				}


				if(value != null)
				{
					recordBuf.append(value);
				}

			}

			out.println(recordBuf.toString());

			++curRecNum;
	}


	public void end()
			throws Exception
	{
		if(open)
		{
			out.close();
			open = false;
		}
	}
}
