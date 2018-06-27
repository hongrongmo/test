package org.ei.domain;

import java.io.IOException;
import java.io.Writer;

import org.apache.oro.text.perl.Perl5Util;

public class Volume
	implements ElementData
{
	protected String volume;
	protected Perl5Util perl;
	protected boolean labels;
	protected Key key;

	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

	public Volume(Key key,
				  String volume,
				  Perl5Util perl)
	{
		this.key = key;
		this.volume = volume;
		this.perl = perl;
	}

	public Volume(String volume,
				  Perl5Util perl)
	{
		this.key = Keys.VOLUME;
		this.volume = volume;
		this.perl = perl;
	}

	public void exportLabels(boolean labels)
	{
		this.labels = labels;
	}

	public String getVolumeNumber()
	{
		if(perl.match("/(\\d+)/", volume))
		{
			return (String) (perl.group(0).toString());
		}

		return null;
	}

	public String[] getElementData()
	{
		String s[] = {this.volume};
		return s;
	}

	public void setElementData(String[] elementData)
	{
		this.volume = elementData[0];
	}

	public String getVolume()
	{
		return this.volume;
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
		out.write("<![CDATA[");
		out.write(this.volume);
		out.write("]]></");
		out.write(this.key.getKey());
		out.write(">");
	}
}