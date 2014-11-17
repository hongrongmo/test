package org.ei.thesaurus;

import java.io.IOException;
import java.io.Writer;

import org.ei.domain.XMLSerializable;

public class ThesSearchNavigator
	implements XMLSerializable
{
	private int pageSize;
	private int numberOfPages;
	private int docCount;
	private int currPageNumber;

	public ThesSearchNavigator(int pageSize, int docCount, int currPageNumber)
	{
		this.pageSize 		= pageSize;
		this.docCount 		= docCount;
		this.currPageNumber = currPageNumber;

		int remainder =1;

		if(docCount > 0)
		{
			remainder = docCount % pageSize;
			if(remainder == 0)
			{
				this.numberOfPages = docCount/pageSize;
			}
			else
			{
				this.numberOfPages = (docCount/pageSize)+1;
			}
		}

	}

	public int getPageSize()
	{
		return this.pageSize;
	}
	public int getDocCount()
	{
		return this.docCount;
	}
	public int getCurrPageNumber()
	{
		return this.currPageNumber;
	}

	public void toXML(Writer out)
		throws IOException
	{

		out.write("<NPAGES PAGES='"+numberOfPages+"'>");

		if((currPageNumber > 1))
		{
			out.write("<NPAGE CURR='PREV'>");
				out.write("<PNUM>");
					out.write(String.valueOf(currPageNumber - 1));
				out.write("</PNUM>");
			out.write("</NPAGE>");
		}

		for(int i = 1; i < numberOfPages+1; i++)
		{
			if(i == currPageNumber)
			{
				out.write("<NPAGE CURR='CURR'>");
					out.write("<PNUM>");
						out.write(String.valueOf(i));
					out.write("</PNUM>");
				out.write("</NPAGE>");
			}
			else
			{
				out.write("<NPAGE CURR='NOTCURR'>");
					out.write("<PNUM>");
						out.write(String.valueOf(i));
					out.write("</PNUM>");
				out.write("</NPAGE>");
			}
		}

		if((numberOfPages - currPageNumber) > 0)
		{
			out.write("<NPAGE CURR='NEXT'>");
				out.write("<PNUM>");
					out.write(String.valueOf(currPageNumber + 1));
				out.write("</PNUM>");
			out.write("</NPAGE>");
		}

		out.write("</NPAGES>");
	}

}