package org.ei.domain;
import java.io.IOException;
import java.io.Writer;

public class XMLWrapper
	implements ElementData

{
	private Key key;
	private String label;
	private String elementData;
	private boolean labels = false;

	public String[] getElementData()
	{
		String[] edata = {elementData};
		return edata;
	}

	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public void setElementData(String[] edata)
	{
		elementData = edata[0];
	}

	public XMLWrapper(Key key,
				      String elementData)
	{
			this.key = key;
			this.elementData = elementData;
	}

	public XMLWrapper(Key key,
					  String label,
					  String elementData)
	{
		this.key = key;
		this.elementData = elementData;
		this.label = label;
	}

	public void toXML(Writer out)
		throws IOException
	{
		out.write("<");
		out.write(this.key.getKey());

		if(labels)
		{
			String lbl = null;
			if(this.label != null)
			{
				lbl = this.label;
			}
			else
			{
				if(this.key != null)
				{
					lbl = this.key.getLabel();
				}
			}
			if(lbl != null)
			{
				out.write(" label=\"");
				out.write(lbl);
				out.write("\"");
			}
		}
		out.write("><![CDATA[");
		if(this.elementData != null) {
  		out.write(this.elementData);
  	}
		out.write("]]></");
		out.write(this.key.getKey());
		out.write(">");
	}

}