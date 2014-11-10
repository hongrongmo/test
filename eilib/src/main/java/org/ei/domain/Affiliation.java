package org.ei.domain;

import java.io.*;



public class Affiliation
	implements XMLSerializable
{

	private String affiliation;
	private String id;
	private Key key;

	public Affiliation(Key key,
					   String affiliation)
	{
		this.key = key;
		this.affiliation = affiliation;
	}

	public Affiliation(Key key,
					   String affiliation,
					   String id)
	{
		this.key = key;
		this.affiliation = affiliation;
		this.id = id;
	}

    public String getAffiliation()
    {
        return this.affiliation;
    }

	public void setID(String id)
	{
		this.id = id;
	}

	public String getID()
	{
		return this.id;
	}

	public void setAffiliation(String affiliation)
	{
		this.affiliation = affiliation;
	}


	public String getAffilation()
	{
		return this.affiliation;
	}

	public void toXML(Writer out)
					throws IOException
	{
		out.write("<");
		out.write(this.key.getSubKey());
		if(this.getID() != null)
		{
			out.write(" id=\"");
			out.write(this.getID());
			out.write("\">");
		}
		else
		{
			out.write(">");
		}
		out.write("<![CDATA[");
		out.write(affiliation);
		out.write("]]>");
		out.write("</");
		out.write(this.key.getSubKey());
		out.write(">");
	}

}