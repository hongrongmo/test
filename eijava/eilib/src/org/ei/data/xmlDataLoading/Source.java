package org.ei.data.xmlDataLoading;
import java.util.*;

public class Source extends BaseElement
{

	Sourcetitle sourcetitle;
	String sourcetitle_abbrev;
	List translated_sourcetitles = new ArrayList();
	String source_type;
	String source_country;
	String volumetitle;
	String issuetitle;
	List issns = new ArrayList();
	List isbns = new ArrayList();
	String codencode;
	Volisspag volisspag;
	Publicationyear publicationyear;
	Publicationdate  publicationdate;
	List websites = new ArrayList();
	List contributor_groups = new ArrayList();
	Editors editors;
	List publishers = new ArrayList();
	Additional_srcinfo additional_srcinfo;
	String bib_text;
	String source_srcid;
	String article_number;

	public void setArticle_number(String article_number)
	{
		this.article_number = article_number;
	}

	public String getArticle_number()
	{
		return this.article_number;
	}

	public void setSource_srcid(String source_srcid)
	{
		this.source_srcid = source_srcid;
	}

	public String getSource_srcid()
	{
		return this.source_srcid;
	}

	public void setSource_type(String source_type)
	{
		this.source_type = source_type;
	}

	public String getSource_type()
	{
		return this.source_type;
	}

	public void setSource_country(String source_country)
	{
		this.source_country = source_country;
	}

	public String getSource_country()
	{
		return this.source_country;
	}

	public void setSourcetitle(Sourcetitle sourcetitle)
	{
		this.sourcetitle = sourcetitle;
	}

	public Sourcetitle getSourcetitle()
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

	public void setTranslated_sourcetitle(List translated_sourcetitles)
	{
		this.translated_sourcetitles = translated_sourcetitles;
	}

	public void addTranslated_sourcetitle(Translated_sourcetitle translated_sourcetitle)
	{
		translated_sourcetitles.add(translated_sourcetitle);
	}

	public List getTranslated_sourcetitle()
	{
		return this.translated_sourcetitles;
	}


	public void setVolumetitle(String volumetitle)
	{
		this.volumetitle = volumetitle;
	}

	public String getVolumetitle()
	{
		return this.volumetitle;
	}

	public void setIssuetitle(String issuetitle)
	{
		this.issuetitle = issuetitle;
	}

	public String getIssuetitle()
	{
		return this.issuetitle;
	}

	public void setIssns(List issns)
	{
		this.issns = issns;
	}

	public List getIssns()
	{
		return this.issns;
	}

	public void addIssn(Issn issn)
	{
		issns.add(issn);
	}

	public void setIsbns(List isbns)
	{
		this.isbns = isbns;
	}

	public List getIsbns()
	{
		return this.isbns;
	}

	public void addIsbn(Isbn isbn)
	{
		isbns.add(isbn);
	}

	public void setCodencode(String codencode)
	{
		this.codencode = codencode;
	}

	public String getCodencode()
	{
		return this.codencode;
	}

	public void setVolisspag(Volisspag volisspag)
	{
		this.volisspag = volisspag;
	}

	public Volisspag getVolisspag()
	{
		return this.volisspag;
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

	public void setWebsites(List websites)
	{
		this.websites = websites;
	}

	public void addWebsite(Website website)
	{
		websites.add(website);
	}

	public List getWebsites()
	{
		return this.websites;
	}

	public void setContributor_groups(List contributor_groups)
	{
		this.contributor_groups = contributor_groups;
	}

	public void addContributor_group(Contributor_group contributor_group)
	{
		contributor_groups.add(contributor_group);
	}

	public List getContributor_groups()
	{
		return this.contributor_groups;
	}

	public void setEditors(Editors editors)
	{
		this.editors = editors;
	}

	public Editors getEditors()
	{
		return this.editors;
	}

	public void setPublishers(List publishers)
	{
		this.publishers = publishers;
	}

	public void addPublisher(Publisher publisher)
	{
		publishers.add(publisher);
	}

	public List getPublishers()
	{
		return this.publishers;
	}

	public void setAdditional_srcinfo(Additional_srcinfo additional_srcinfo)
	{
		this.additional_srcinfo = additional_srcinfo;
	}

	public Additional_srcinfo getAdditional_srcinfo()
	{
		return this.additional_srcinfo;
	}

	public void setBib_text(String bib_text)
	{
		this.bib_text = bib_text;
	}

	public String getBib_text()
	{
		return this.bib_text;
	}


}