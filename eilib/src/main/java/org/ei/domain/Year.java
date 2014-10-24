package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;


public class Year
	implements ElementData
{
	private String year;
	private Perl5Util perl;
	private boolean labels;
	private Key key;

	public Year(String year,
		      	Perl5Util perl)
	{
		this.key = Keys.PUBLICATION_YEAR;
		this.year = year;
		this.perl = perl;
	}

	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public Year(Key key,
				String year,
				Perl5Util perl)
	{
		this.key = key;
		this.year = year;
		this.perl = perl;
	}


	public String getYYYY()
	{
		if(perl.match("/\\d\\d\\d\\d/", year))
		{
			MatchResult mResult = perl.getMatch();
//			System.out.println("mResult::"+mResult.toString());
			return mResult.toString();
		}

		return null;
	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public String[] getElementData()
	{
		String[] s = {this.year};
		return s;
	}

	public void setElementData(String[] elementData)
	{
		this.year = elementData[0];
	}

	public String getYear()
	{
		return this.year;
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
		out.write(this.year);
		out.write("]]></");
		out.write(this.key.getKey());
		out.write(">");
	}




}