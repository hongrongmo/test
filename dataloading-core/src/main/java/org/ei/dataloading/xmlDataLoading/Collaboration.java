
package org.ei.dataloading.xmlDataLoading;

public class Collaboration extends BaseElement
{
	String indexed_name;
	String text;
	String seq;

	public void setIndexed_Name(String indexed_name)
	{
		this.indexed_name = indexed_name;
	}

	public String getIndexed_Name()
	{
		return this.indexed_name;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return this.text;
	}

	public void setCollaboration_seq(String seq)
	{
		this.seq = seq;
	}

	public String getCollaboration_seq()
	{
		return this.seq;
	}

}
