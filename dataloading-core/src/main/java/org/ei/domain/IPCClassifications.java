package org.ei.domain;

import java.io.IOException;
import java.io.Writer;
import org.ei.common.Constants;

public class IPCClassifications
			implements ElementData
{
	protected IPCClassification[] iPCclassifications;
	protected Key key;
	protected Database database;
	protected boolean labels = false;
	protected String code;
	protected String[] title;

	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public IPCClassifications ()
	{
	}

	public IPCClassifications (Key key,
		    String[] codes,
		    Database database)
	{
	    this.key = key;
	    this.database = database;
	    iPCclassifications = loadIPCClassifications(key,codes);
	}


	protected IPCClassification[] loadIPCClassifications(Key key,
												   String[] codes)
	{
		IPCClassification[] iPCs = new IPCClassification[codes.length];

		for(int i=0;i<codes.length;i++)
		{
			IPCClassification ipc = new IPCClassification();
			if(codes[i] != null)
			{
				String[] id = codes[i].split(Constants.IDDELIMITER,-1);
				if(id.length ==1)
				{
					ipc.setCode(id[0]);
				}
				else
				{
					ipc.setCode(id[0]);
					ipc.setTitle(id[1]);
				}
				//System.out.println("code="+id[0]+" title="+id[1]);
			}

			iPCs[i]= ipc;
	    }
		return iPCs;
	}

    public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public void setElementData(String[] sarray)
	{
		this.title = sarray;
	}

	public String[] getElementData()
	{
		return title;
	}


	public void toXML(Writer out)
		throws IOException
	{

       	out.write("<");
       	out.write(this.key.getKey());
		if(this.labels && (this.key.getLabel() != null))
		{
			out.write(" label=\"");
			out.write(this.key.getLabel());
			out.write("\"");
		}
       	out.write(">");
		for (int i = 0; i< iPCclassifications.length; i++)
		{
			iPCclassifications[i].toXML(out);
		}
		out.write("</");
		out.write(this.key.getKey());
    	out.write(">");
	}
}
