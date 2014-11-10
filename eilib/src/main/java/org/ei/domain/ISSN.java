package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

public class ISSN
	implements ElementData
{
	protected String issn;
	protected Key key;
	protected boolean labels;


	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public ISSN(String issn)
	{
		this.key = Keys.ISSN;
		this.issn = issn;
	}

	public ISSN(Key key,
				String issn)
	{
		this.key = key;
		this.issn = issn;
	}

	public String getIssn()
	{
		return this.issn;
	}

	public String withDash()
	{
		if (issn.length() == 9)
		{
			return issn;
		}
		else if(issn.length() == 8)
		{
			return issn.substring(0,4)+"-"+issn.substring(4,8);
		}

        return null;
	}

	public String withoutDash()
	{

		if (issn.length() == 9)
		{
			return issn.substring(0,4)+issn.substring(5,9);
		}
		else if(issn.length() == 8)
		{
			return issn;
		}

		return null;
	}


	public String[] getElementData()
	{
		String s[] = {this.issn};
		return s;
	}

	public void setElementData(String[] s)
	{
		this.issn = s[0];
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
		out.write("><![CDATA[");
		out.write(this.issn);
		out.write("]]></");
		out.write(this.key.getKey());
		out.write(">");
	}


}