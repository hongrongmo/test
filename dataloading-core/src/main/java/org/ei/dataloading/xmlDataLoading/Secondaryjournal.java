package org.ei.dataloading.xmlDataLoading;

public class Secondaryjournal extends BaseElement
{
	String sourcetitle;
	Issn issn;
	Publicationyear publicationyear;
	Publicationdate publicationdate;
	Voliss voliss;
	String sourcetitle_abbrev;


	public void setSourcetitle_abbrev(String sourcetitle_abbrev)
	{
		this.sourcetitle_abbrev = sourcetitle_abbrev;
	}

	public String getSourcetitle_abbrev()
	{
		return this.sourcetitle_abbrev;
	}

	public void setSourcetitle(String sourcetitle)
	{
		this.sourcetitle = sourcetitle;
	}

	public String getSourcetitle()
	{
		return this.sourcetitle;
	}

	public void setIssn(Issn issn)
	{
		this.issn = issn;
	}

	public Issn getIssn()
	{
		return this.issn;
	}

	public void setPublicationyear(Publicationyear publicationyear)
	{
		this.publicationyear = publicationyear;
	}

	public Publicationyear getPublicationyear()
	{
		return this.publicationyear;
	}

	public void setPublicationdate(Publicationdate publicationdate)
	{
		this.publicationdate = publicationdate;
	}

	public Publicationdate getPublicationdate()
	{
		return this.publicationdate;
	}

	public void setVoliss(Voliss voliss)
	{
		this.voliss = voliss;
	}

	public Voliss getVoliss()
	{
		return this.voliss;
	}

}

