package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

public class XMLMultiWrapper2
	implements ElementData
{

	private Key key;
	private KeyValuePair[] keyValuePairs;
	private boolean labels = false;


	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public String[] getElementData()
	{
		String[] s = new String[keyValuePairs.length];
		for(int i=0; i<keyValuePairs.length; i++)
		{
			s[i] = keyValuePairs[i].getValue();
		}

		return s;
	}

	public void setElementData(String[] elementData)
	{
		for(int i=0; i<elementData.length; i++)
		{
			keyValuePairs[i].setValue(elementData[i]);
		}
	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}


	public XMLMultiWrapper2(Key key,
			  		  	    KeyValuePair[] keyValuePairs)
	{
		this.key = key;
		this.keyValuePairs = keyValuePairs;
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

		for(int i=0; i<keyValuePairs.length; i++)
		{
			keyValuePairs[i].toXML(out);
		}

		out.write("</");
		out.write(this.key.getKey());
		out.write(">");
	}

}