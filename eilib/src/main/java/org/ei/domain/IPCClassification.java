package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

public class IPCClassification
			implements XMLSerializable
{
	String code;
	String title = "";
	boolean labels;
	protected Key key;
	String cid;

	public IPCClassification()
	{
	}

	public IPCClassification(String cid)
	{
		this.key = Keys.INTERNATCL_CODE;
		this.cid = cid;
	}

	public IPCClassification(Key key,
						  	 String cid)
	{
		this.key = key;
		this.cid = cid;
	}

	public void setCode(String cid)
	{
		this.cid = cid;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getCode()
	{
		return this.cid;
	}

	public String getTitle()
	{
		return this.title;
	}

	public Key getKey() { return this.key; }

	public void toXML(Writer out)
	        throws IOException
	{

	        out.write("<PID>");
	        out.write("<CID><![CDATA[");
	        out.write(notNull(cid));
	        out.write("]]></CID>");
	        out.write("<CTI><![CDATA[");
	        out.write(notNull(getTitle()));
	        out.write("]]></CTI>");
			out.write("</PID>");
    }

	private String notNull(String s)
	{
		if(s == null)
		{
			return " ";
		}

		return s;
    }

}
