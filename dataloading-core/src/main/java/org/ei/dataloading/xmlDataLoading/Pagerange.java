package org.ei.dataloading.xmlDataLoading;

public class Pagerange extends BaseElement

{
	String pagerange;
	String last;
	String first;


	public void setPagerange(String pagerange)
	{
		this.pagerange = pagerange;
	}

	public String getPagerange()
	{
		return this.pagerange;
	}

	public void setPagerange_first(String first)
	{
		this.first = first;
	}

	public String getPagerange_first()
	{
		return this.first;
	}

	public void setPagerange_last(String last)
	{
		this.last = last;
	}

	public String getPagerange_last()
	{
		return this.last;
	}

}
