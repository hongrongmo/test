package org.ei.dataloading.xmlDataLoading;

public class Tocentry extends BaseElement
{

	Sectiontitle sectiontitle;
	Sectionauthor sectionauthor;
	String sectionauthortext;
	Pagerange pagerange;
	String pages;
	Formatinfo formatinfo;
	Linkinfo linkinfo;

	public void setSectiontitle(Sectiontitle sectiontitle)
	{
		this.sectiontitle = sectiontitle;
	}

	public Sectiontitle getSectiontitle()
	{
		return this.sectiontitle;
	}

	public void setSectionauthor(Sectionauthor sectionauthor)
	{
		this.sectionauthor = sectionauthor;
	}

	public Sectionauthor getSectionauthor()
	{
		return this.sectionauthor;
	}

	public void setSectionauthortext(String sectionauthortext)
	{
		this.sectionauthortext = sectionauthortext;
	}

	public String getSectionauthortext()
	{
		return this.sectionauthortext;
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

	public void setFormatinfo(Formatinfo formatinfo)
	{
		this.formatinfo = formatinfo;
	}

	public Formatinfo getFormatinfo()
	{
		return this.formatinfo;
	}

	public void setLinkinfo(Linkinfo linkinfo)
	{
		this.linkinfo = linkinfo;
	}

	public Linkinfo getLinkinfo()
	{
		return this.linkinfo;
	}

}
