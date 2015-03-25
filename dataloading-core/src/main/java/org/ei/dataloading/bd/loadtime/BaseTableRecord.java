package org.ei.dataloading.bd.loadtime;

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
	public static final BaseTableRecord CLASSIFICATIONDESC= new BaseTableRecord("CLASSIFICATIONDESC", new Integer(1100), "cpx");
	public static final BaseTableRecord APICT= new BaseTableRecord("APICT", new Integer(4000), "elt");
	public static final BaseTableRecord APICT1= new BaseTableRecord("APICT1", new Integer(4000), "elt");
	public static final BaseTableRecord SOURC = new BaseTableRecord("SOURC", new Integer(768), "elt");
	public static final BaseTableRecord SECSTI = new BaseTableRecord("SECSTI", new Integer(76), "elt");
	public static final BaseTableRecord SECISS = new BaseTableRecord("SECISS", new Integer(8), "elt");
	public static final BaseTableRecord SEC = new BaseTableRecord("SEC", new Integer(100), "elt");
	public static final BaseTableRecord SECPUBDATE = new BaseTableRecord("SECPUBDATE", new Integer(250), "elt");
	public static final BaseTableRecord FIG = new BaseTableRecord("FIG", new Integer(250), "elt");
	public static final BaseTableRecord SECVOLUME = new BaseTableRecord("SECVOLUME", new Integer(250), "elt");
	public static final BaseTableRecord SECISSUE = new BaseTableRecord("SECISSUE", new Integer(250), "elt");
	public static final BaseTableRecord APILTM = new BaseTableRecord("APILTM", new Integer(4000), "elt");
	public static final BaseTableRecord APILT = new BaseTableRecord("APILT", new Integer(4000), "elt");
	public static final BaseTableRecord APILT1 = new BaseTableRecord("APILT1", new Integer(4000), "elt");
	public static final BaseTableRecord APIALC = new BaseTableRecord("APIALC", new Integer(500), "elt");
	public static final BaseTableRecord APIAMS = new BaseTableRecord("APIAMS", new Integer(200), "elt");
	public static final BaseTableRecord APIAPC = new BaseTableRecord("APIAPC", new Integer(600), "elt");
	public static final BaseTableRecord APIATM = new BaseTableRecord("APIATM", new Integer(4000), "elt");
	public static final BaseTableRecord APIAT = new BaseTableRecord("APIAT", new Integer(4000), "elt");
	public static final BaseTableRecord SEQ_NUM = new BaseTableRecord("SEQ_NUM", new Integer(4000), "elt");

	public static final BaseTableRecord REFERENCE= new BaseTableRecord("REFERENCE", new Integer(500000), "cpx");
	public static final BaseTableRecord REFERENCETITLE= new BaseTableRecord("REFERENCETITLE", new Integer(4000), "cpx");
	public static final BaseTableRecord REFERENCEAUTHOR= new BaseTableRecord("REFERENCEAUTHOR", new Integer(4000), "cpx");
	public static final BaseTableRecord REFERENCESOURCETITLE= new BaseTableRecord("REFERENCESOURCETITLE", new Integer(4000), "cpx");
	public static final BaseTableRecord REFERENCEPUBLICATIONYEAR= new BaseTableRecord("REFERENCEPUBLICATIONYEAR", new Integer(9), "cpx");
	public static final BaseTableRecord REFERENCEVOLUME= new BaseTableRecord("REFERENCEVOLUME", new Integer(128), "cpx");
	public static final BaseTableRecord REFERENCEISSUE= new BaseTableRecord("REFERENCEISSUE", new Integer(128), "cpx");
	public static final BaseTableRecord REFERENCEPAGES= new BaseTableRecord("REFERENCEPAGES", new Integer(1020), "cpx");
	public static final BaseTableRecord REFERENCEFULLTEXT= new BaseTableRecord("REFERENCEFULLTEXT", new Integer(4000), "cpx");
	public static final BaseTableRecord REFERENCETEXT= new BaseTableRecord("REFERENCETEXT", new Integer(4000), "cpx");
	public static final BaseTableRecord REFERENCEWEBSITE= new BaseTableRecord("REFERENCEWEBSITE", new Integer(2000), "cpx");
	public static final BaseTableRecord REFERENCEITEMID= new BaseTableRecord("REFERENCEITEMID", new Integer(64), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONPII= new BaseTableRecord("REFERENCEITEMCITATIONPII", new Integer(128), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONDOI= new BaseTableRecord("REFERENCEITEMCITATIONDOI", new Integer(128), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONTITLE = new BaseTableRecord("REFERENCEITEMCITATIONTITLE", new Integer(4000), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONAUTHOR= new BaseTableRecord("REFERENCEITEMCITATIONAUTHOR", new Integer(4000), "cpx");
	public static final BaseTableRecord REFITEMCITATIONSOURCETITLE= new BaseTableRecord("REFITEMCITATIONSOURCETITLE", new Integer(1000), "cpx");
	public static final BaseTableRecord REFCITATIONSOURCETITLEABBREV= new BaseTableRecord("REFCITATIONSOURCETITLEABBREV", new Integer(4000), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONISSN= new BaseTableRecord("REFERENCEITEMCITATIONISSN", new Integer(1000), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONISBN= new BaseTableRecord("REFERENCEITEMCITATIONISBN", new Integer(1000), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONCODEN= new BaseTableRecord("REFAERENCEITEMCITATIONCODEN", new Integer(500000), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONPART= new BaseTableRecord("REFERENCEITEMCITATIONPART", new Integer(128), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONPUBLICATIONYEAR= new BaseTableRecord("REFERENCEITEMCITATIONPUBLICATIONYEAR", new Integer(9), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONVOLUME= new BaseTableRecord("REFERENCEITEMCITATIONVOLUME", new Integer(128), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONISSUE= new BaseTableRecord("REFERENCEITEMCITATIONISSUE", new Integer(128), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONPAGE= new BaseTableRecord("REFERENCEITEMCITATIONPAGE", new Integer(64), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONARTICLENUMBER= new BaseTableRecord("REFERENCEITEMCITATIONARTICLENUMBER", new Integer(64), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONWEBSITE= new BaseTableRecord("REFERENCEITEMCITATIONWEBSITE", new Integer(2000), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONEADDRESS= new BaseTableRecord("REFERENCEITEMCITATIONEADDRESS", new Integer(1000), "cpx");
	public static final BaseTableRecord REFERENCEITEMCITATIONREFTEXT= new BaseTableRecord("REFERENCEITEMCITATIONREFTEXT", new Integer(4000), "cpx");


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
	    bdTableColumns.add(CLASSIFICATIONDESC);
	    bdTableColumns.add(APICT);
	    bdTableColumns.add(APICT1);
	    bdTableColumns.add(SOURC);
	    bdTableColumns.add(SECSTI);
	    bdTableColumns.add(SECISS);
	    bdTableColumns.add(SEC);
	    bdTableColumns.add(SECPUBDATE);
	    bdTableColumns.add(FIG);
	    bdTableColumns.add(SECVOLUME);
	    bdTableColumns.add(SECISSUE);
	    bdTableColumns.add(APILT);
	    bdTableColumns.add(APILT1);
	    bdTableColumns.add(APILTM);
	    bdTableColumns.add(APIALC);
	    bdTableColumns.add(APIAMS);
	    bdTableColumns.add(APIAPC);
	    bdTableColumns.add(APIATM);
	    bdTableColumns.add(APIAT);
	    bdTableColumns.add(SEQ_NUM);

		//ADDED FOR REFERENCE

		bdTableColumns.add(REFERENCETITLE);
		bdTableColumns.add(REFERENCEAUTHOR);
		bdTableColumns.add(REFERENCESOURCETITLE);
		bdTableColumns.add(REFERENCEPUBLICATIONYEAR);
		bdTableColumns.add(REFERENCEVOLUME);
		bdTableColumns.add(REFERENCEISSUE);
		bdTableColumns.add(REFERENCEPAGES);
		bdTableColumns.add(REFERENCEFULLTEXT);
		bdTableColumns.add(REFERENCETEXT);
		bdTableColumns.add(REFERENCEWEBSITE);
		bdTableColumns.add(REFERENCEITEMID);
		bdTableColumns.add(REFERENCEITEMCITATIONPII);
		bdTableColumns.add(REFERENCEITEMCITATIONDOI);
		bdTableColumns.add(REFERENCEITEMCITATIONTITLE);
		bdTableColumns.add(REFERENCEITEMCITATIONAUTHOR);
		bdTableColumns.add(REFITEMCITATIONSOURCETITLE);
		bdTableColumns.add(REFCITATIONSOURCETITLEABBREV);
		bdTableColumns.add(REFERENCEITEMCITATIONISSN);
		bdTableColumns.add(REFERENCEITEMCITATIONISBN);
		bdTableColumns.add(REFERENCEITEMCITATIONCODEN);
		bdTableColumns.add(REFERENCEITEMCITATIONPART);
		bdTableColumns.add(REFERENCEITEMCITATIONPUBLICATIONYEAR);
		bdTableColumns.add(REFERENCEITEMCITATIONVOLUME);
		bdTableColumns.add(REFERENCEITEMCITATIONISSUE);
		bdTableColumns.add(REFERENCEITEMCITATIONPAGE);
		bdTableColumns.add(REFERENCEITEMCITATIONARTICLENUMBER);
		bdTableColumns.add(REFERENCEITEMCITATIONWEBSITE);
		bdTableColumns.add(REFERENCEITEMCITATIONEADDRESS);
	    bdTableColumns.add(REFERENCEITEMCITATIONREFTEXT);
		bdTableColumns.add(REFERENCE);


	}
}

