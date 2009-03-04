package org.ei.data.bd.loadtime;

import java.util.*;

public class BaseTableRecord
{
    String name;
    Integer columnLenght;
    String dbs;

	private static LinkedHashSet  bdTableColumns = new LinkedHashSet();

	public static LinkedHashSet getBdColumns()
	{
	    return bdTableColumns;
	}

	public String getName()
	{
	    return this.name;
	}
	public Integer getColumnLength()
	{
	    return this.columnLenght;
	}
	public String getDatabases()
	{
	    return this.dbs;
	}
	private BaseTableRecord(String aname, Integer cLength, String dbs)
	{
	    this.name = aname;
	    this.columnLenght= cLength;
	    this.dbs = dbs;
	}

	public static final BaseTableRecord M_ID = new BaseTableRecord("M_ID", new Integer(100), "cpx");
	public static final BaseTableRecord ACCESSNUMBER = new BaseTableRecord("ACCESSNUMBER", new Integer(15), "cpx");
   	public static final BaseTableRecord DATESORT = new BaseTableRecord("DATESORT", new Integer(500), "cpx");
   	public static final BaseTableRecord AUTHOR = new BaseTableRecord("AUTHOR", new Integer(4000), "cpx");
   	public static final BaseTableRecord AUTHOR_1 = new BaseTableRecord("AUTHOR_1", new Integer(4000), "cpx");
   	public static final BaseTableRecord AFFILIATION = new BaseTableRecord("AFFILIATION", new Integer(4000), "cpx");
   	public static final BaseTableRecord AFFILIATION_1 = new BaseTableRecord("AFFILIATION_1", new Integer(4000), "cpx");
   	public static final BaseTableRecord CODEN = new BaseTableRecord("CODEN", new Integer(6), "cpx");
   	public static final BaseTableRecord ISSUE = new BaseTableRecord("ISSUE", new Integer(128), "cpx");
   	public static final BaseTableRecord TITLELANGUAGE= new BaseTableRecord("TITLELANGUAGE", new Integer(32), "cpx");
   	public static final BaseTableRecord CLASSIFICATIONCODE = new BaseTableRecord("CLASSIFICATIONCODE", new Integer(768), "cpx");
   	public static final BaseTableRecord CONTROLLEDTERM = new BaseTableRecord("CONTROLLEDTERM", new Integer(4000), "cpx");
   	public static final BaseTableRecord UNCONTROLLEDTERM = new BaseTableRecord("UNCONTROLLEDTERM", new Integer(4000), "cpx");
   	public static final BaseTableRecord MAINHEADING = new BaseTableRecord("MAINHEADING", new Integer(4000), "cpx");
   	public static final BaseTableRecord TREATMENTCODE = new BaseTableRecord("TREATMENTCODE", new Integer(16), "cpx");
   	public static final BaseTableRecord SPECIESTERM = new BaseTableRecord("SPECIESTERM", new Integer(4000), "geo");
   	public static final BaseTableRecord REGIONALTERM = new BaseTableRecord("REGIONALTERM", new Integer(4000), "geo");
   	public static final BaseTableRecord LOADNUMBER = new BaseTableRecord("LOADNUMBER", new Integer(10000), "cpx");
   	public static final BaseTableRecord SOURCETYPE = new BaseTableRecord("SOURCETYPE", new Integer(32), "cpx");
   	public static final BaseTableRecord SOURCECOUNTRY = new BaseTableRecord("SOURCECOUNTRY", new Integer(32), "cpx");
   	public static final BaseTableRecord SOURCEID = new BaseTableRecord("SOURCEID", new Integer(32), "cpx");
   	public static final BaseTableRecord SOURCETITLE = new BaseTableRecord("SOURCETITLE", new Integer(2560), "cpx");
   	public static final BaseTableRecord SOURCETITLEABBREV = new BaseTableRecord("SOURCETITLEABBREV", new Integer(1280), "cpx");
   	public static final BaseTableRecord ISSUETITLE  = new BaseTableRecord("ISSUETITLE", new Integer(2560), "cpx");
   	public static final BaseTableRecord ISSN= new BaseTableRecord("ISSN", new Integer(9), "cpx");
   	public static final BaseTableRecord EISSN = new BaseTableRecord("EISSN", new Integer(13), "cpx");
   	public static final BaseTableRecord ISBN= new BaseTableRecord("ISBN", new Integer(1000), "cpx");
   	public static final BaseTableRecord VOLUME= new BaseTableRecord("VOLUME", new Integer(128), "cpx");
   	public static final BaseTableRecord PAGE= new BaseTableRecord("PAGE", new Integer(120), "cpx");
   	public static final BaseTableRecord PAGECOUNT= new BaseTableRecord("PAGECOUNT", new Integer(32), "cpx");
   	public static final BaseTableRecord ARTICLENUMBER  = new BaseTableRecord("ARTICLENUMBER", new Integer(64), "cpx");
   	public static final BaseTableRecord PUBLICATIONYEAR= new BaseTableRecord("PUBLICATIONYEAR", new Integer(9), "cpx");
   	public static final BaseTableRecord PUBLICATIONDATE= new BaseTableRecord("PUBLICATIONDATE", new Integer(32), "cpx");
   	public static final BaseTableRecord EDITORS = new BaseTableRecord("EDITORS", new Integer(4000), "cpx");
   	public static final BaseTableRecord PUBLISHERNAME  = new BaseTableRecord("PUBLISHERNAME", new Integer(2400), "cpx");
   	public static final BaseTableRecord PUBLISHERADDRESS = new BaseTableRecord("PUBLISHERADDRESS", new Integer(3200), "cpx");
   	public static final BaseTableRecord PUBLISHERELECTRONICADDRESS = new BaseTableRecord("PUBLISHERELECTRONICADDRESS", new Integer(2400), "cpx");
   	public static final BaseTableRecord REPORTNUMBER= new BaseTableRecord("REPORTNUMBER", new Integer(128), "cpx");
   	public static final BaseTableRecord CHEMICALTERMS = new BaseTableRecord("CHEMICAL_TERMS", new Integer(128), "chm");
   	public static final BaseTableRecord CONFNAME = new BaseTableRecord("CONFNAME", new Integer(1200), "cpx");
   	public static final BaseTableRecord CONFCATNUMBER  = new BaseTableRecord("CONFCATNUMBER", new Integer(32), "cpx");
   	public static final BaseTableRecord CONFCODE = new BaseTableRecord("CONFCODE", new Integer(32), "cpx");
   	public static final BaseTableRecord CONFLOCATION= new BaseTableRecord("CONFLOCATION", new Integer(1200), "cpx");
   	public static final BaseTableRecord CONFDATE = new BaseTableRecord("CONFDATE", new Integer(526), "cpx");
   	public static final BaseTableRecord CONFSPONSORS= new BaseTableRecord("CONFSPONSORS", new Integer(2400), "cpx");
   	public static final BaseTableRecord CONFERENCEPARTNUMBER= new BaseTableRecord("CONFERENCEPARTNUMBER", new Integer(32), "cpx");
   	public static final BaseTableRecord CONFERENCEPAGERANGE = new BaseTableRecord("CONFERENCEPAGERANGE", new Integer(32), "cpx");
   	public static final BaseTableRecord CONFERENCEPAGECOUNT = new BaseTableRecord("CONFERENCEPAGECOUNT", new Integer(32), "cpx");
   	public static final BaseTableRecord CONFERENCEEDITOR = new BaseTableRecord("CONFERENCEEDITOR", new Integer(2400), "cpx");
   	public static final BaseTableRecord CONFERENCEORGANIZATION = new BaseTableRecord("CONFERENCEORGANIZATION", new Integer(1200), "cpx");
   	public static final BaseTableRecord CONFERENCEEDITORADDRESS= new BaseTableRecord("CONFERENCEEDITORADDRESS", new Integer(1200), "cpx");
   	public static final BaseTableRecord TRANSLATEDSOURCETITLE  = new BaseTableRecord("TRANSLATEDSOURCETITLE", new Integer(1280), "cpx");
   	public static final BaseTableRecord VOLUMETITLE = new BaseTableRecord("VOLUMETITLE", new Integer(1280), "cpx");
   	public static final BaseTableRecord CONTRIBUTOR = new BaseTableRecord("CONTRIBUTOR", new Integer(4000), "cpx");
   	public static final BaseTableRecord CONTRIBUTORAFFILIATION = new BaseTableRecord("CONTRIBUTORAFFILIATION", new Integer(4000), "cpx");
   	public static final BaseTableRecord COPYRIGHT= new BaseTableRecord("COPYRIGHT", new Integer(1000), "cpx");
   	public static final BaseTableRecord DOI = new BaseTableRecord("DOI", new Integer(128), "cpx");
   	public static final BaseTableRecord PII = new BaseTableRecord("PII", new Integer(200), "cpx");
   	public static final BaseTableRecord PUI = new BaseTableRecord("PUI", new Integer(200), "cpx");
   	public static final BaseTableRecord ABSTRACTORIGINAL = new BaseTableRecord("ABSTRACTORIGINAL", new Integer(20), "cpx");
   	public static final BaseTableRecord ABSTRACTPERSPECTIVE = new BaseTableRecord("ABSTRACTPERSPECTIVE", new Integer(512), "cpx");
   	public static final BaseTableRecord ABSTRACTDATA= new BaseTableRecord("ABSTRACTDATA", new Integer(20000), "cpx");
   	public static final BaseTableRecord CITTYPE  = new BaseTableRecord("CITTYPE", new Integer(5), "cpx");
   	public static final BaseTableRecord CORRESPONDENCENAME  = new BaseTableRecord("CORRESPONDENCENAME", new Integer(4000), "cpx");
   	public static final BaseTableRecord CORRESPONDENCEAFFILIATION = new BaseTableRecord("CORRESPONDENCEAFFILIATION", new Integer(4000), "cpx");
   	public static final BaseTableRecord CORRESPONDENCEEADDRESS = new BaseTableRecord("CORRESPONDENCEEADDRESS", new Integer(2000), "cpx");
   	public static final BaseTableRecord CITATIONTITLE  = new BaseTableRecord("CITATIONTITLE", new Integer(1000), "cpx");
   	public static final BaseTableRecord CITATIONTITLEORIG= new BaseTableRecord("CITATIONTITLEORIG", new Integer(5), "cpx");
	public static final BaseTableRecord CITATIONLANGUAGE= new BaseTableRecord("CITATIONLANGUAGE", new Integer(20), "cpx");
	public static final BaseTableRecord DATABASE= new BaseTableRecord("DATABASE", new Integer(3), "cpx");
	public static final BaseTableRecord AUTHORKEYWORD= new BaseTableRecord("AUTHORKEYWORD", new Integer(4000), "pch");
	public static final BaseTableRecord REFCOUNT= new BaseTableRecord("REFCOUNT", new Integer(4), "cpx");
	public static final BaseTableRecord CASREGISTRYNUMBER= new BaseTableRecord("CASREGISTRYNUMBER", new Integer(4000), "chm");
	public static final BaseTableRecord CHEMICALTERM= new BaseTableRecord("CHEMICALTERM", new Integer(4000), "chm");
	public static final BaseTableRecord SEQUENCEBANKS= new BaseTableRecord("SEQUENCEBANKS", new Integer(4000), "chm");
	public static final BaseTableRecord TRADENAME= new BaseTableRecord("TRADENAME", new Integer(4000), "chm");
	public static final BaseTableRecord MANUFACTURER= new BaseTableRecord("MANUFACTURER", new Integer(4000), "chm");
	public static final BaseTableRecord MEDIA= new BaseTableRecord("MEDIA", new Integer(26), "pch");
	public static final BaseTableRecord CSESS= new BaseTableRecord("CSESS", new Integer(140), "pch");
	public static final BaseTableRecord PATNO= new BaseTableRecord("PATNO", new Integer(22), "pch");
	public static final BaseTableRecord PLING= new BaseTableRecord("PLING", new Integer(64), "pch");
	public static final BaseTableRecord APPLN= new BaseTableRecord("APPLN", new Integer(128), "pch");
	public static final BaseTableRecord PRIOR_NUM= new BaseTableRecord("PRIOR_NUM", new Integer(1200), "pch");
	public static final BaseTableRecord ASSIG= new BaseTableRecord("ASSIG", new Integer(300), "pch");
	public static final BaseTableRecord PCODE= new BaseTableRecord("PCODE", new Integer(22), "pch");
	public static final BaseTableRecord CLAIM= new BaseTableRecord("CLAIM", new Integer(8), "pch");

	static
	{
	    bdTableColumns.add(M_ID);
	    bdTableColumns.add(ACCESSNUMBER);
	    bdTableColumns.add(DATESORT);
	    bdTableColumns.add(AUTHOR);
	    bdTableColumns.add(AUTHOR_1);
	    bdTableColumns.add(AFFILIATION);
	    bdTableColumns.add(AFFILIATION_1);
	    bdTableColumns.add(CODEN);
	    bdTableColumns.add(ISSUE);
	    bdTableColumns.add(CLASSIFICATIONCODE);
	    bdTableColumns.add(CONTROLLEDTERM);
	    bdTableColumns.add(UNCONTROLLEDTERM);
	    bdTableColumns.add(MAINHEADING);
	    bdTableColumns.add(SPECIESTERM);
	    bdTableColumns.add(REGIONALTERM);
	    bdTableColumns.add(TREATMENTCODE);
	    bdTableColumns.add(LOADNUMBER);
	    bdTableColumns.add(SOURCETYPE);
	    bdTableColumns.add(SOURCECOUNTRY);
	    bdTableColumns.add(SOURCEID);
	    bdTableColumns.add(SOURCETITLE);
	    bdTableColumns.add(SOURCETITLEABBREV);
	    bdTableColumns.add(ISSUETITLE);
	    bdTableColumns.add(ISSN);
	    bdTableColumns.add(EISSN);
	    bdTableColumns.add(ISBN);
	    bdTableColumns.add(VOLUME);
	    bdTableColumns.add(PAGE);
	    bdTableColumns.add(PAGECOUNT);
	    bdTableColumns.add(ARTICLENUMBER);
	    bdTableColumns.add(PUBLICATIONYEAR);
	    bdTableColumns.add(PUBLICATIONDATE);
	    bdTableColumns.add(EDITORS);
	    bdTableColumns.add(PUBLISHERNAME);
	    bdTableColumns.add(PUBLISHERADDRESS);
	    bdTableColumns.add(PUBLISHERELECTRONICADDRESS);
	    bdTableColumns.add(REPORTNUMBER);
	    bdTableColumns.add(CONFNAME);
	    bdTableColumns.add(CONFCATNUMBER);
	    bdTableColumns.add(CONFCODE);
	    bdTableColumns.add(CONFLOCATION);
	    bdTableColumns.add(CONFDATE);
	    bdTableColumns.add(CONFSPONSORS);
	    bdTableColumns.add(CONFERENCEPARTNUMBER);
	    bdTableColumns.add(CONFERENCEPAGERANGE);
	    bdTableColumns.add(CONFERENCEPAGECOUNT);
	    bdTableColumns.add(CONFERENCEEDITOR);
	    bdTableColumns.add(CONFERENCEORGANIZATION);
	    bdTableColumns.add(CONFERENCEEDITORADDRESS);
	    bdTableColumns.add(TRANSLATEDSOURCETITLE);
	    bdTableColumns.add(VOLUMETITLE);
	    bdTableColumns.add(CONTRIBUTOR);
	    bdTableColumns.add(CONTRIBUTORAFFILIATION);
	    bdTableColumns.add(COPYRIGHT);
	    bdTableColumns.add(DOI);
	    bdTableColumns.add(PII);
	    bdTableColumns.add(PUI);
	    bdTableColumns.add(ABSTRACTORIGINAL);
	    bdTableColumns.add(ABSTRACTPERSPECTIVE);
	    bdTableColumns.add(ABSTRACTDATA);
	    bdTableColumns.add(CITTYPE);
	    bdTableColumns.add(CORRESPONDENCENAME);
	    bdTableColumns.add(CORRESPONDENCEAFFILIATION);
	    bdTableColumns.add(CORRESPONDENCEEADDRESS);
	    bdTableColumns.add(CITATIONTITLE);
	   	bdTableColumns.add(CITATIONLANGUAGE);
	   	bdTableColumns.add(AUTHORKEYWORD);
		bdTableColumns.add(REFCOUNT);
		bdTableColumns.add(CHEMICALTERM);
		bdTableColumns.add(CHEMICALTERMS);
		bdTableColumns.add(CASREGISTRYNUMBER);
		bdTableColumns.add(SEQUENCEBANKS);
		bdTableColumns.add(TRADENAME);
		bdTableColumns.add(MANUFACTURER);
	    bdTableColumns.add(DATABASE);
		bdTableColumns.add(MEDIA);
		bdTableColumns.add(CSESS);
		bdTableColumns.add(PATNO);
		bdTableColumns.add(PLING);
		bdTableColumns.add(APPLN);
		bdTableColumns.add(PRIOR_NUM);
		bdTableColumns.add(ASSIG);
		bdTableColumns.add(PCODE);
		bdTableColumns.add(CLAIM);

	}
}