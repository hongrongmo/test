package org.ei.dataloading.xmlDataLoading;

public class Refd_itemcitation extends BaseElement
{

	String pii;
	String doi;
	Citation_title citation_title;
	Author_group author_group;
	String sourcetitle;
	String sourcetitle_abbrev;
	Issn issn;
	Isbn isbn;
	String codencode;
	String publicationyear;
	Volisspag volisspag;
	Website website;
	String ref_text;
	String refd_itemcitation_type;

	public void setPii(String pii)
	{
		this.pii = pii;
	}

	public String getPii()
	{
		return this.pii;
	}

	public void setDoi(String doi)
	{
		this.doi = doi;
	}

	public String getDoi()
	{
		return this.doi;
	}

	public void setCitation_title(Citation_title citation_title)
	{
		this.citation_title = citation_title;
	}

	public Citation_title getCitation_title()
	{
		return this.citation_title;
	}

	public void setAuthor_group(Author_group author_group)
	{
		this.author_group = author_group;
	}

	public Author_group getAuthor_group()
	{
		return this.author_group;
	}

	public void setSourcetitle(String sourcetitle)
	{
		this.sourcetitle = sourcetitle;
	}

	public String getSourcetitle()
	{
		return this.sourcetitle;
	}

	public void setSourcetitle_abbrev(String sourcetitle_abbrev)
	{
		this.sourcetitle_abbrev = sourcetitle_abbrev;
	}

	public String getSourcetitle_abbrev()
	{
		return this.sourcetitle_abbrev;
	}

	public void setIssn(Issn issn)
	{
		this.issn = issn;
	}

	public Issn getIssn()
	{
		return this.issn;
	}

	public void setIsbn(Isbn isbn)
	{
		this.isbn = isbn;
	}

	public Isbn getIsbn()
	{
		return this.isbn;
	}

	public void setCodencode(String codencode)
	{
		this.codencode = codencode;
	}

	public String getCodencode()
	{
		return this.codencode;
	}

	public void setPublicationyear(String publicationyear)
	{
		this.publicationyear = publicationyear;
	}

	public String getPublicationyear()
	{
		return this.publicationyear;
	}

	public void setVolisspag(Volisspag volisspag)
	{
		this.volisspag = volisspag;
	}

	public Volisspag getVolisspag()
	{
		return this.volisspag;
	}

	public void setWebsite(Website website)
	{
		this.website = website;
	}

	public Website getWebsite()
	{
		return this.website;
	}

	public void setRefd_itemcitation_type(String refd_itemcitation_type)
	{
		this.refd_itemcitation_type = refd_itemcitation_type;
	}

	public String getRefd_itemcitation_type()
	{
		return this.refd_itemcitation_type;
	}

	public void setRef_text(String ref_text)
	{
		this.ref_text = ref_text;
	}

	public String getRef_text()
	{
		return this.ref_text;
	}

}
