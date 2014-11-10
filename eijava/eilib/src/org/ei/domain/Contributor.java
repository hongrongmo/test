package org.ei.domain;

import java.io.*;
import java.util.*;

public class Contributor
	implements XMLSerializable
{
	protected String name;
	protected String[] affilID;
	protected Affiliation affiliation;
	protected String country;
	protected String email;
	protected Key key;

	public Contributor(Key key,
					   String name)
	{
		this.key = key;
		this.name = name;
	}

	public Contributor(Key key,
					   String name,
					   String[] affilID)
	{
		this.key = key;
		this.name = name;
		this.affilID = affilID;
	}
	

	public Contributor(Key key,
			   String name,
			   String country)
	{
	    this.key = key;
	    this.name = name;
	    this.country = country;
	}


	public String getFirstName()
	{
		StringTokenizer st = null;
		if(this.name.indexOf(",") > -1)
		{
			st = new StringTokenizer(this.name,",",false);
		}
		else
		{
			st = new StringTokenizer(this.name," ",false);
		}

		if (st.countTokens() > 1)
		{
			String tmpStr = st.nextToken();
			return st.nextToken();
		}

		return null;
	}

	public String getName()
	{
		return this.name;
	}

	public String getCountry()
	{
		return this.country;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setAffilID(String[] affilID)
	{
		this.affilID = affilID;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getEmail()
	{
		return this.email;
	}


	public String getLastName()
	{
		StringTokenizer st = null;
		if(this.name.indexOf(",") > -1)
		{
			st = new StringTokenizer(this.name,",",false);
		}
		else
		{
			st = new StringTokenizer(this.name," ",false);
		}

		if (st.countTokens() > 0)
		{
			return st.nextToken();
		}
		return null;
	}

	public void setAffiliation(Affiliation affiliation)
	{
	    this.affiliation = affiliation;
	}

	public void toXML(Writer out) throws IOException
	{
		out.write("<");
		out.write(this.key.getSubKey());
		if(affilID != null)
		{
			//Only have one affilition not for multiple
			out.write(" id=\"");
			out.write(String.valueOf(affilID[0]));
			out.write("\">");
		}
		else
		{
			out.write(">");
		}


		    out.write("<![CDATA[");
		    out.write(name);
		    out.write("]]>");


		if(this.affiliation != null)
		{
			this.affiliation.toXML(out);
		}

		if(affilID != null)
		{
			out.write("<AFS>");
			for(int i=0; i< this.affilID.length; i++)
			{
				out.write("<AFID>");
				out.write(String.valueOf(affilID[i]));
				out.write("</AFID>");
			}
			out.write("</AFS>");
		}

		if(this.country != null)
		{
			out.write("<CO><![CDATA[");
			out.write(this.country);
			out.write("]]></CO>");
		}

		if(this.email != null)
		{
			out.write("<EMAIL><![CDATA[");
			out.write(this.email);
			out.write("]]></EMAIL>");
		}

		out.write("</");
		out.write(this.key.getSubKey());
		out.write(">");
	}

}