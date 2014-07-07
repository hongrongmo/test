package org.ei.data.insback.runtime;

import org.ei.domain.*;
import java.util.*;
import java.io.*;


public class OCVS
	implements ElementData
{


	private List elementData = new ArrayList();
	private Key key;
	private boolean labels = false;

	public OCVS(Key key,
				String raw)
	{
		this.key = key;
		parse(raw);
	}

	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public void setElementData(String[] edata)

	{
		for(int i=0; i<edata.length; i++)
		{
			List l = new ArrayList();
			String[] parts = edata[i].split(" QDelimQ ");
			for(int z=0; z<parts.length; z++)
			{
				l.add(parts[z]);
			}

			elementData.set(i, l);
		}
	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public String[] getElementData()
	{
		String[] edata = new String[elementData.size()];
		for(int i=0; i<elementData.size(); i++)
		{
			List l = (List)elementData.get(i);
			StringBuffer buf = new StringBuffer();
			for(int z=0;z<l.size();z++)
			{
				String s = (String)l.get(z);
				if(z>0)
				{
					buf.append(" QDelimQ ");
				}
				buf.append(s);
			}
			edata[i] = buf.toString();
		}
		return edata;
	}

	public void toXML(Writer out)
		throws IOException
	{
		if(elementData.size() > 0)
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
			for(int i=0; i<elementData.size(); i++)
			{
				out.write("<ORGC>");
				List l = (List)elementData.get(i);
				for(int z=0;z<l.size();z++)
				{
					String s = (String)l.get(z);
					out.write("<CV><![CDATA[");
					out.write(s);
					out.write("]]></CV>");
				}
				out.write("</ORGC>");
			}
			out.write("</");
			out.write(this.key.getKey());
			out.write(">");
		}
	}

	private void parse(String raw)
	{
		HashSet unique = new HashSet();
		String[] cvs =raw.split("~ ");
		for(int i=0;i<cvs.length;i++)
		{
			ArrayList l = new ArrayList();

			String[] term=cvs[i].split("\\|");
			if(term.length>0)
			{
				if(!unique.contains(term[0]))
				{
					if(term.length>=1)
					{
						if (!term[0].equals(""))
						{
							l.add(term[0]);
							unique.add(term[0]);
						}
					}
					if(term.length>=2)
					{
						if (!term[1].equals(""))
						{
							l.add(term[1]);
						}
					}
					if(term.length>=3)
					{
						if (!term[2].equals(""))
						{
							l.add(term[2]);
						}
					}
				}

				if(l.size() > 0)
				{
					elementData.add(l);
				}
			}
		}
	}
}