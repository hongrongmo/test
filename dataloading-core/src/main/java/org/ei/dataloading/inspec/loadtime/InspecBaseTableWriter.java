package org.ei.dataloading.inspec.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;
import org.ei.common.Constants;

public class InspecBaseTableWriter
{

	private int recsPerFile = -1;
	private int curRecNum = 0;
	private String filename;
	private PrintWriter out;
	private String filepath;
	private int loadnumber;
	private int filenumber = 0;
	private boolean open = false;
	private static Properties props;
	private String[] baseTableFields;
	public final int AUS_MAXSIZE = 4000;
    public final String NOABSTRACT = "NOABSTRACT";

	public InspecBaseTableWriter(int recsPerFile,
									String filename,
									String type)
	{
		this.recsPerFile = recsPerFile;
		this.filename = filename;

		if(type.equalsIgnoreCase("XML"))
		{
			this.baseTableFields= InspecBaseTableRecord.insxmlbaseTableFields;
		}
		else
		{
			this.baseTableFields= InspecBaseTableRecord.insbaseTableFields;
		}
	}

	public InspecBaseTableWriter(String filename,String type)
	{
		this.filename = filename;

		if(type.equalsIgnoreCase("XML"))
		{
			this.baseTableFields= InspecBaseTableRecord.insxmlbaseTableFields;
		}
		else
		{
			this.baseTableFields= InspecBaseTableRecord.insbaseTableFields;
		}
	}

	public void begin() throws Exception
	{
		++filenumber;
		out = new PrintWriter(new FileWriter(filename+"."+filenumber));
		open = true;
		curRecNum = 0;
	}

	public void writeRec(Hashtable record)
		throws Exception
	{

        if(record == null)
        {
        	System.out.println("Record was null");
        	return;
        }

		for(int i=0; i<baseTableFields.length; ++i)
		{
			String bf = baseTableFields[i];
			//System.out.println("bf= "+bf);
			StringBuffer value = (StringBuffer) record.get(bf);
			//System.out.println("Value= "+value);
			String valueS = null;
			if(value != null)
			{
				valueS = value.toString();
				if(valueS.length() == 0 && bf.equalsIgnoreCase("AB"))
				{
					valueS = NOABSTRACT;
				}
				valueS = valueS.replaceAll("\\t","\\s{5}");
				if(bf.equalsIgnoreCase("AUS"))
				{
                    if(valueS.length() > AUS_MAXSIZE)
                    {
                        // store the AUS_MAXSIZE+ substring in Hashtable for later processing
                        record.put("AUS2", new StringBuffer(valueS.substring(AUS_MAXSIZE)));
                        // truncate first author string to AUS_MAXSIZE - we don't care if we truncate
                        // an author name since AUS + AUS2 fields will be rejoined by concatenation
    					valueS = valueS.substring(0, AUS_MAXSIZE);
                    }
				}
				if(bf.equalsIgnoreCase("AUS2"))
				{
				    // fix last author in string to make sure it isn't a partial author name
				    valueS = fixAuthors(valueS);
                }
			}
			else
			{
				if(bf.equalsIgnoreCase("AB"))
				{
					valueS = NOABSTRACT;
				}
			}

			if(i > 0)
			{
				out.print("	");
			}

			if(valueS != null)
			{
				out.print(valueS);
			}

		}
		out.println("");
		out.flush();
		++curRecNum;
	}

	private String fixAuthors(String authors)
	{
		if(authors != null && authors.length() > AUS_MAXSIZE)
		{
			authors = authors.substring(0,AUS_MAXSIZE-1);
			int i = authors.lastIndexOf(Constants.AUDELIMITER);
            if(i != -1)
            {
			    authors = authors.substring(0,i);
            }
		}
		return authors;
	}


	public void end() throws Exception
	{
		if(open)
		{
			out.close();
			open = false;
		}
	}
}
