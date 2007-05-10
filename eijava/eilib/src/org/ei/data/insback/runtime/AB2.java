package org.ei.ev.driver.insback;

import org.ei.domain.*;
import java.util.*;
import java.io.*;

public class AB2
	implements ElementData
{

	private Key key;
	private String ab2;
	private boolean labels;

	public AB2(Key key,
			   String ab2)
	{
		this.key = key;
		this.ab2 = ab2;
	}


	public void setElementData(String[] edata)
	{
		this.ab2 = edata[0];
	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public String[] getElementData()
	{
		String[] edata = {ab2};
		return edata;
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
		out.write(">");
		out.write("<ABS>");
		out.write("<![CDATA[");
		out.write(this.ab2);
		out.write("]]>");
		out.write("</ABS>");
		out.write("</");
		out.write(this.key.getKey());
		out.write(">");
	}
}