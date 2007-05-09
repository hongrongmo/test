package org.ei.domain;

import java.io.*;


public class ISBN
	implements ElementData
{

	protected String isbn;
	protected Key key;
	protected boolean labels = false;

	public ISBN(String isbn)
	{
		this.key = Keys.ISBN;
		this.isbn = isbn;
	}

	public ISBN(Key key, String isbn)
	{
		this.key = Keys.ISBN;
		this.isbn = isbn;
	}


	public String withoutDash()
	{
		return this.isbn.replaceAll("[-|\\s]","");
	}

	public String withDash()
	{
		return this.isbn.replaceAll("\\s","-");
	}

	public String[] getElementData()
	{
		String s[] = {this.isbn};
		return s;
	}

	public void setElementData(String[] s)
	{
		this.isbn = s[0];
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
		out.write(this.isbn);
		out.write("]]></");
		out.write(this.key.getKey());
		out.write(">");
	}

}

