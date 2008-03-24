package org.ei.domain;
import java.util.*;
import java.io.*;



public class Contributors
	implements ElementData
{

	public void setKey(Key akey)
	{
	  this.key = akey;
	}

	protected List contributors = new ArrayList();
	protected Key key;
	protected boolean labels = false;

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public String[] getElementData()
	{
		String[] edata = new String[contributors.size()];

		for(int i=0;i<contributors.size();i++)
		{
			Contributor c = (Contributor)contributors.get(i);
			edata[i] = c.getName();
		}

		return edata;
	}

	public void setElementData(String[] elementData)
	{
		for(int i=0; i<elementData.length; i++)
		{
			Contributor c = (Contributor)contributors.get(i);
			c.setName(elementData[i]);
		}
	}

	public Contributors(Key key,
				       List list)
	{
		this.key = key;
	    this.contributors = list;
	}

	public void setChildren(List contributors)
	{
		this.contributors = contributors;
	}

	public Contributor getFirstContributor()
	{
		if(this.contributors.size() > 0)
		{
			return (Contributor)contributors.get(0);
		}

		return null;
	}

	public void setFirstAffiliation(Affiliation affil)
	{
		Contributor c = (Contributor)this.contributors.get(0);
		c.setAffiliation(affil);
	}

	public void setEmails(List emails)
	{
		for(int i=0;i<emails.size();i++)
		{
			Contributor c = (Contributor)this.contributors.get(i);
			c.setEmail((String)emails.get(i));
		}
	}


	public List getChildren()
	{
		return this.contributors;
	}

	public void toXML(Writer out)
		throws IOException

	{
		out.write("<");
		out.write(key.getKey());
		if(labels && (this.key.getLabel() != null))
		{
			out.write(" label=\"");
			out.write(this.key.getLabel());
			out.write("\"");
		}
		out.write(">");

		for(int i=0; i<contributors.size(); i++)
		{
			Contributor co = (Contributor)contributors.get(i);
			co.toXML(out);
		}
		out.write("</");
		out.write(key.getKey());
		out.write(">");

	}
}

