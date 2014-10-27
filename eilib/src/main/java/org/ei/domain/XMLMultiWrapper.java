package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

public class XMLMultiWrapper
	implements ElementData
{

	private Key key;
	private String [] elementData;
	private boolean labels = false;

	public void setElementData(String[] elementData)
	{
		this.elementData = elementData;
	}

	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public String[] getElementData()
	{
		return elementData;
	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}


	public XMLMultiWrapper(Key key,
			  		  	   String []elementVal)
	{
		this.key = key;
		this.elementData = elementVal;
	}


	public void toXML(Writer out)
		throws IOException
	{
		out.write("<");
		out.write(this.key.getKey());
		if(labels && (this.key.getLabel() != null))
		{
			out.write(" label=\"");
			out.write(this.key.getLabel());
			out.write("\"");
		}
		out.write(">");

		for(int i=0; i<elementData.length; i++)
		{
			out.write("<");
			out.write(this.key.getSubKey());
			out.write("><![CDATA[");
			out.write(this.elementData[i]);
			out.write("]]></");
			out.write(this.key.getSubKey());
			out.write(">");

		}

		out.write("</");
		out.write(this.key.getKey());
		out.write(">");
	}

}