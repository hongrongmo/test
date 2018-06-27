package org.ei.domain;

import java.io.*;
import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;
import java.util.*;


public class PageRange
	implements ElementData
{

	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	protected Perl5Util perl;
	protected String pageRange;
	protected String numberPattern = "/[A-Z]?[0-9][0-9]*/";
	protected Key key;
	protected boolean labels = false;

	public PageRange(Key key,
					 String pageRange,
					 Perl5Util perl)
	{
		this.key = key;
		this.pageRange = pageRange;
		this.perl = perl;
	}

	public PageRange(String pageRange,
					 Perl5Util perl)
	{
		this.key = Keys.PAGE_RANGE;
		this.pageRange = pageRange;
		this.perl = perl;
	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public String[] getElementData()
	{
		String s[] = {this.pageRange};
		return s;
	}

	public void setElementData(String[] elementData)
	{
		this.pageRange = elementData[0];
	}

	public String getStartPage()
	{
		String firstPage = null;
		StringTokenizer tmpPage = new StringTokenizer(this.pageRange,"-");

		if(tmpPage.countTokens()>0)
		{
			firstPage = tmpPage.nextToken();
		}
		else
		{
			firstPage = this.pageRange;
		}

		if(firstPage != null)
		{
			if(perl.match(numberPattern, firstPage))
			{
				MatchResult mResult = perl.getMatch();
				return mResult.toString();
			}
		}

		return null;
	}

	public String getPageRange()
	{
		return perl.substitute("s/[^\\d-,]//g", this.pageRange);
	}

	public String getEndPage()
	{
		String lastPage = null;
		StringTokenizer tmpPage = new StringTokenizer(this.pageRange,"-");

		if(tmpPage.countTokens()>1)
		{
			tmpPage.nextToken();
			lastPage = tmpPage.nextToken();
		}
		else
		{
			lastPage = this.pageRange;
		}

		if(lastPage != null)
		{

			if(perl.match(numberPattern, lastPage))
			{
				MatchResult mResult = perl.getMatch();
				return mResult.toString();
			}
		}

		return null;
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
		out.write(this.pageRange);
		out.write("]]></");
		out.write(this.key.getKey());
		out.write(">");
	}


}