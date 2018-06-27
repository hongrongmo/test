package org.ei.dataloading.xmlDataLoading;

public class BaseElement
{

	StringBuffer text = new StringBuffer();

	public void setText( String s)
	{
		text.append(s);
	}

	public String getText()
	{
		return text.toString();
	}

	public void setAttributeValue(String name, String value)
	{
		throw new Error( getClass()+":No attributes allowed");
	}
}
