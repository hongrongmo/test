package org.ei.dataloading.xmlDataLoading;

public class Volisspag extends BaseElement

{
	Voliss volissObject;
	Pagerange pagerange;
	String pages;
	Pagecount pagecount;
	String volisspag;

	public void setVolisspag(String volisspag)
	{
		this.volisspag = volisspag;
	}

	public String getVolisspag()
	{
		return this.volisspag;
	}

	public void setVoliss(Voliss volissObject)
	{
		this.volissObject = volissObject;
	}

	public Voliss getVoliss()
	{
		return this.volissObject;
	}


	public void setPagerange(Pagerange pagerange)
	{
		this.pagerange = pagerange;
	}

	public Pagerange getPagerange()
	{
		return this.pagerange;
	}

	public void setPages(String pages)
	{
		this.pages = pages;
	}

	public String getPages()
	{
		return this.pages;
	}

	public void setPagecount(Pagecount pagecount)
	{
		this.pagecount = pagecount;
	}

	public Pagecount getPagecount()
	{
		return this.pagecount;
	}

}
