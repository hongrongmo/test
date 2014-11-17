
package org.ei.domain;

import java.io.*;

import org.apache.oro.text.perl.*;

public class Issue
	implements ElementData
{
	protected String issue;
	protected Perl5Util perl;
	protected boolean labels;
	protected Key key;

	public Issue(Key key,
				 String issue,
				 Perl5Util perl)
	{
		this.key = key;
		this.issue = issue;
		this.perl = perl;
	}

	public Issue(String issue,
				 Perl5Util perl)
	{
		this.key = Keys.ISSUE;
		this.issue = issue;
		this.perl = perl;
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

	public String[] getElementData()
	{
		String[] s = {this.issue};
		return s;
	}

	public String getIssueNumber()
	{
		if(perl.match("/(\\d+)/", issue))
		{
			return (String) (perl.group(0).toString());
		}

		return null;
	}

	public void setElementData(String[] elementData)
	{
		this.issue = elementData[0];
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
		out.write("><![CDATA[");
		out.write(this.issue);
		out.write("]]></");
		out.write(this.key.getKey());
		out.write(">");
	}


	public String getIssue()
	{
		return this.issue;
	}

}