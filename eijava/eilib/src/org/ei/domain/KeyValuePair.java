package org.ei.domain;

import java.io.*;

public class KeyValuePair
{
	private Key key;
	private String value;

	public KeyValuePair(Key key,
					String value)
	{
		this.key = key;
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public void toXML(Writer out)
		throws IOException
	{
		out.write("<");
		out.write(this.key.getKey());
		out.write("><![CDATA[");
		out.write(this.value);
		out.write("]]></");
		out.write(this.key.getKey());
		out.write(">");
	}


}