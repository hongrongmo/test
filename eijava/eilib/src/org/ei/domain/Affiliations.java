package org.ei.domain;

import java.util.*;
import java.io.*;
import org.ei.xml.*;

public class Affiliations
	implements ElementData
{
	protected List affiliations;
	protected Key key;
	protected boolean labels;


	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public Affiliations(Key key,
					    List affiliations)
	{
		this.key = key;
		this.affiliations = affiliations;
	}

	public Affiliations(Key key,
						Affiliation affiliation)
	{
		this.key = key;
		this.affiliations = new ArrayList();
		affiliations.add(affiliation);
	}


	public String[] getElementData()
	{
		String[] edata = new String[affiliations.size()];

		for(int i=0; i<affiliations.size(); i++)
		{
			Affiliation affil = (Affiliation) affiliations.get(i);
			edata[i] = affil.getAffiliation();
		}

		return edata;
	}

	public void setElementData(String[] elementData)
	{
		for(int i=0; i<elementData.length; i++)
		{
			Affiliation affil = (Affiliation) affiliations.get(i);
			affil.setAffiliation(elementData[i]);
		}
	}

	public List getAffiliations()
	{
		return this.affiliations;
	}

	public void toXML(Writer out)
		throws IOException
	{
		out.write("<");
		out.write(this.key.getKey());
		if(labels && (this.key.getLabel() != null))
		{
			out.write(" label=\"");
			out.write(this.key.getLabel());
			out.write("\"");
		}
		out.write(">");

		for(int i=0; i < affiliations.size(); i++)
		{
		    Affiliation af = (Affiliation)affiliations.get(i);
			af.toXML(out);
		}
		out.write("</");
		out.write(this.key.getKey());
		out.write(">");
	}

}