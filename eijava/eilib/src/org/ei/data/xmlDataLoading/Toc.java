package org.ei.data.xmlDataLoading;

public class Toc extends BaseElement
{
	Tocentry tocentry;

	public void setTocentry(Tocentry tocentry)
	{
		this.tocentry = tocentry;
	}

	public Tocentry getTocentry()
	{
		return tocentry;
	}
}