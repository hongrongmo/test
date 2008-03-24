package org.ei.domain;

import java.io.*;


import java.io.IOException;
import java.io.Writer;

public class Treatments
	implements ElementData
{
	protected Treatment[] treatments;
	protected Key key;
	protected boolean labels = false;

	public void setKey(Key akey)
	{
	  this.key = akey;
	}

	public Treatments(String[] codes,
					  Database database)
	{
		this.key = Keys.TREATMENTS;
		treatments = loadTreatments(this.key,
									codes,
									database);
	}

	public Treatments(Key key,
					  String[] codes,
					  Database database)
	{
		this.key = key;
		treatments = loadTreatments(key,
								   codes,
								   database);
	}

	protected Treatment[] loadTreatments(Key key,
										 String codes[],
										 Database database)
	{
		DataDictionary dataDictionary = database.getDataDictionary();
		Treatment[] trs = new Treatment[codes.length];
		for (int i = 0; i< codes.length; i++)
		{

		    //System.out.println("TR::"+codes[i]);
			String title = (String)(dataDictionary.getTreatments()).get(codes[i]);
			Treatment tr = new Treatment(key,
										 codes[i],
										 title);
			trs[i] = tr;
		}

		return trs;
	}

	public String[] getElementData()
	{
		return null;
	}

	public void setElementData(String[] s)
	{

	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
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
		for (int i = 0; i< treatments.length; i++)
		{
			treatments[i].toXML(out);
		}
		out.write("</");
		out.write(this.key.getKey());
    	out.write(">");
	}

}



