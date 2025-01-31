package org.ei.dataloading.bd.loadtime;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.HashSet;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.dataloading.bd.loadtime.BdNumericalIndexMapping;
import org.ei.util.kafka.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.ei.dataloading.*;
import org.ei.dataloading.georef.loadtime.*;
import org.ei.dataloading.lookup.LookupEntry;
import org.ei.common.bd.*;
import org.ei.common.*;
import org.ei.util.GUID;
import org.ei.util.StringUtil;
import org.json.simple.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.text.*;
//import java.math.*;

public class XmlCombiner extends CombinerTimestamp {

	public String[] EVCombinedRecKeys = { EVCombinedRec.AUTHOR, EVCombinedRec.AUTHOR_AFFILIATION,
	EVCombinedRec.AFFILIATION_LOCATION, EVCombinedRec.COUNTRY, EVCombinedRec.EDITOR, EVCombinedRec.TITLE,
	EVCombinedRec.TRANSLATED_TITLE, EVCombinedRec.VOLUME_TITLE, EVCombinedRec.ABSTRACT,
	EVCombinedRec.CONTROLLED_TERMS, EVCombinedRec.UNCONTROLLED_TERMS, EVCombinedRec.CHEMICALTERMS,
	EVCombinedRec.INT_PATENT_CLASSIFICATION, EVCombinedRec.ISSN, EVCombinedRec.CODEN, EVCombinedRec.ISBN,
	EVCombinedRec.SERIAL_TITLE, EVCombinedRec.MAIN_HEADING, EVCombinedRec.PUBLISHER_NAME,
	EVCombinedRec.TREATMENT_CODE, EVCombinedRec.LANGUAGE, EVCombinedRec.DOCTYPE,
	EVCombinedRec.CLASSIFICATION_CODE, EVCombinedRec.CONFERENCE_CODE, EVCombinedRec.CONFERENCE_NAME,
	EVCombinedRec.CONFERENCE_LOCATION, EVCombinedRec.MEETING_DATE, EVCombinedRec.SPONSOR_NAME,
	EVCombinedRec.CONFERENCEAFFILIATIONS, EVCombinedRec.CONFERENCEEDITORS, EVCombinedRec.CONFERENCEPARTNUMBER,
	EVCombinedRec.CONFERENCEPAGERANGE, EVCombinedRec.CONFERENCENUMBERPAGES, EVCombinedRec.MONOGRAPH_TITLE,
	EVCombinedRec.DATABASE, EVCombinedRec.LOAD_NUMBER, EVCombinedRec.PUB_YEAR, EVCombinedRec.DEDUPKEY,
	EVCombinedRec.VOLUME, EVCombinedRec.ISSUE, EVCombinedRec.STARTPAGE, EVCombinedRec.ACCESSION_NUMBER,
	EVCombinedRec.REPORTNUMBER, EVCombinedRec.DOI, EVCombinedRec.COPYRIGHT, EVCombinedRec.PII,
	EVCombinedRec.PUI, EVCombinedRec.COMPANIES, EVCombinedRec.CASREGISTRYNUMBER, EVCombinedRec.PUB_SORT };

	Perl5Util perl = new Perl5Util();

	private static String tablename;

	private static String currentDb;

	private static HashMap issnARFix = new HashMap();
	private List auid;
	private static String puiGlobal;
	private static String propertyFileName;
	private static List cittypeList;
	private static String url;
	private static String driver;
	private static String username;
	private static String password;

	/* HT added 09/21/2020 for lookup extraction to ES */
	/* change the default action to ""*/
	private String action = "";
	private LookupEntry lookupObj = null;
	private Connection con = null;

	static { // ISSNs with AR field problem
		issnARFix.put("00913286", "");
		issnARFix.put("10833668", "");
		issnARFix.put("10179909", "");
		issnARFix.put("15393755", "");
		issnARFix.put("00319007", "");
		issnARFix.put("10502947", "");
		issnARFix.put("00036951", "");
		issnARFix.put("00218979", "");
		issnARFix.put("00219606", "");
		issnARFix.put("00346748", "");
		issnARFix.put("1070664X", "");
		issnARFix.put("10706631", "");
		issnARFix.put("00948276", "");
		issnARFix.put("00431397", "");
	}

	public static void main(String args[]) throws Exception {
		// url=jdbc:oracle:thin:@jupiter.elsevier.com:1521:EIDB1
		// driver = oracle.jdbc.driver.OracleDriver
		// username = ap_ev_search
		// password =

		url = args[0];
		driver = args[1];
		username = args[2];
		password = args[3];
		int loadNumber = 0;
		int recsPerbatch = 50000;
		try {
			loadNumber = Integer.parseInt(args[4]);
		} catch (NumberFormatException e) {
			loadNumber = 0;
		}
		try {
			recsPerbatch = Integer.parseInt(args[5]);
		} catch (NumberFormatException e) {
			recsPerbatch = 50000;
		}
		String operation = args[6];
		tablename = args[7];
		String currentDb = args[8].toLowerCase();
		long timestamp = 0;

		if (args.length > 9) {
			propertyFileName = args[9];
			System.out.println("propertyFileName=" + propertyFileName);
		}

		Combiner.TABLENAME = tablename;
		Combiner.CURRENTDB = currentDb;
		System.out.println(Combiner.TABLENAME);

		String dbname = currentDb;
		if (timestamp > 0)
			dbname = dbname + "cor";
		CombinedWriter writer = new CombinedXMLWriter(recsPerbatch, loadNumber, dbname, "dev");

		writer.setOperation(operation);

		XmlCombiner c = new XmlCombiner(writer);

		/* TH added 09/21/2020 for ES lookup generation */
		for (String str : args) {
			if (str.equalsIgnoreCase("lookup")) {
				c.setAction("lookup");
			}

		}
		System.out.println("Lookup Action? " + c.getAction());

		/* HT added 09/21/2020, this condition needed when we run only Whole DB Lookup extraction for ES*/
		if (c.getAction() != null && (c.getAction() != null && c.getAction().equalsIgnoreCase("lookup"))) {
			c.writeLookupByWeekHook(loadNumber);
		}
		/*initialize ES lookup dirs For weekly lookup extraction*/
		c.writeLookupByWeekHook(loadNumber);
		
		
		
		if (timestamp == 0 && (loadNumber > 3000 || loadNumber < 1000) && (loadNumber > 1)) {
			c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
		} else if (timestamp > 0) {
			c.writeCombinedByTimestamp(url, driver, username, password, timestamp);
		} else if (loadNumber == 1) {
			c.writeCombinedByTable(url, driver, username, password);
		} else if (loadNumber < 3000 || loadNumber > 1000) {
			c.writeCombinedByYear(url, driver, username, password, loadNumber);
		} else if (loadNumber == 0) {
			for (int yearIndex = 1904; yearIndex <= 2012; yearIndex++) {
				c = new XmlCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex, dbname, "dev"));
				c.writeCombinedByYear(url, driver, username, password, yearIndex);
			}
			if (Combiner.CURRENTDB.equalsIgnoreCase("chm")) {
				c = new XmlCombiner(new CombinedXMLWriter(recsPerbatch, 9999, dbname, "dev"));
				c.writeCombinedByYear(url, driver, username, password, 9999);
			}
			if (Combiner.CURRENTDB.equalsIgnoreCase("geo")) {
				c = new XmlCombiner(new CombinedXMLWriter(recsPerbatch, 9999, dbname, "dev"));
				c.writeCombinedByYear(url, driver, username, password, 9999);
			}
			if (Combiner.CURRENTDB.equalsIgnoreCase("cpx")) {
				c = new XmlCombiner(new CombinedXMLWriter(recsPerbatch, 9999, dbname, "dev"));
				c.writeCombinedByYear(url, driver, username, password, 9999);
			}
		} else {
			c.writeCombinedByYear(url, driver, username, password, loadNumber);
		}
	}

	public void setAction(String str) {
		action = str;
	}

	public String getAction() {
		return action;
	}

	public XmlCombiner(CombinedWriter writer) {
		super(writer);
	}

	public XmlCombiner(CombinedWriter writer, String propertyFileName) {
		super(writer);
		this.propertyFileName = propertyFileName;

	}
	
	/*HT added 09/21/2020 to support ES Lookup*/
	public XmlCombiner(CombinedWriter writer, String propertyFileName, String database) {
		super(writer);
		this.propertyFileName = propertyFileName;
		Combiner.CURRENTDB = database;
	}
	public void writeCombinedByYearHook(Connection con, int year) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		try {

			// stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			// ResultSet.CONCUR_READ_ONLY);
			stmt = con.createStatement();
			System.out.println("Running the query...");
			if (Combiner.CURRENTDB.equalsIgnoreCase("chm") && (year == 9999)) {
				// modify for author/affiliation project by hmo at 4/5/2017
				rs = stmt.executeQuery(
						"select UPDATENUMBER,CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APICT, APICT1,APILT, APILT1,CLASSIFICATIONDESC,APIAMS, SEQ_NUM,GRANTLIST,ISOPENACESS,CSXTERM,null as cafe_author,b.null as cafe_author1,null as cafe_affiliation,null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,CONFSERIESID, CONFSERIESTITLE, CONFSERIESACRONYM, SOURCECOUNTRY,count(*) over() totalCount from "
								+ Combiner.TABLENAME
								+ " where  PUBLICATIONYEAR in ('1940','1981-1982','1982-1983','1983-1984','1984-1985','1987-1988','1988-1989','1989-1989','1989-1990','1990-1991','1991-1991','1993-1994','1994-1995','1995-1996','1996-1997','1997-1998','1999-2000','2000-2001','2001-1993','2001-2002','2002-2003','2003-2004','2004-2005','2005-2006','2006-2007,'2007-2008','2008-2009','2009-2010', '2010-2011', '2011-2012') AND loadnumber != 0 and loadnumber < 100000000 and database='"
								+ Combiner.CURRENTDB + "'");

			} else if (Combiner.CURRENTDB.equalsIgnoreCase("geo") && (year == 9999)) {
				// modify for author/affiliation project by hmo at 4/5/2017
				rs = stmt.executeQuery(
						"select UPDATENUMBER,CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APICT, APICT1,APILT, APILT1,CLASSIFICATIONDESC,APIAMS,SEQ_NUM,GRANTLIST,ISOPENACESS,CSXTERM,null as cafe_author,b.null as cafe_author1,null as cafe_affiliation,null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid, CONFSERIESID, CONFSERIESTITLE, CONFSERIESACRONYM, SOURCECOUNTRY, count(*) over() totalCount from "
								+ Combiner.TABLENAME
								+ " where  PUBLICATIONYEAR in ('1944-1995','1952-1985','1960-1976','1973-1982','1974-1975','1974-1978','1975-1976','1975-1978','1976-1977','1976-1981','1976-1987','1977-1978','1977-1979','1977-1980','1978-1979','1978-1980','1979-1980','1979-1981','1979-1982','1979-1983','1979-1994','1980-1981','1980-1982','1980-1983','1980-1984','1981-1982','1981-1983','1981-1984','1981-1985','1981-1986','1981-1992','1982-1983','1982-1984','1982-1985','1982-1986','1982-1987','1982-1988','1982-1989','1982-1999','1983-1984','1983-1985','1983-1986','1983-1987','1983-1988','1983-1989','1984-1985','1984-1986','1984-1987','1984-1988','1984-1989','1985-1986','1985-1987','1985-1988','1985-1989','1985-1990','1986-1987','1986-1988','1986-1989','1987-1988','1987-1989','1987-1990','1987-1991','1988-1988','1988-1989','1988-1990','1988-1991','1989-1990','1989-1991','1989-1992','1989-1993','1990-1991','1990-1992','1990-1993','1991-1992','1991-1993','1991-1994','1992-1992','1992-1993','1992-1994','1992-1995','1993-1994','1993-1995','1994-1995','1994-1996','1995-1996','1995-1997','1996-1997','1997-1998','1997-1999','1998-1999','1999-2000','2000-2001','2000-2002','2001-2002','2002-2003','2003-2004','2004-2005','2005-2006','2005-2007','2006-2007','2007-2008','2008-2009', '2009-2010', '2010-2011', '2011-2012') AND loadnumber != 0 and loadnumber < 100000000 and database='"
								+ Combiner.CURRENTDB + "'");

			} else if (Combiner.CURRENTDB.equalsIgnoreCase("cpx") && (year == 9999)) {
				
				sqlQuery = "select a.UPDATENUMBER,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS,a.CSXTERM, a.CONFSERIESID, a.CONFSERIESTITLE, a.CONFSERIESACRONYM, a.SOURCECOUNTRY,count(*) over() totalCount from "
						+ Combiner.TABLENAME
						+ " a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and  a.PUBLICATIONYEAR in ('1798','1885','1964-1980','1977-1978','1977-1980','1978-1979','1979-1980','1979-1981','1979-1989','1980-1981','1980-1982','1981-1982','1981-1983','1982-1983','1983-1984','1984-1985','1985-1986','1986-1987','1987-1988','1988-1989','1989-190','1989-1989','1989-1990','1989-1993','1990-1991','1991-1991','1991-1992','1992-1993','1992-1994','1993-1994','1993-1995','1993-94','1994-1995','1995-1996','1995-96','1996-1997','1997-1998','1997-2003','1997-98','1998-1999','1998-99','1999-2000','2000-2001','2001-1993','2001-2002','2002-2003','2003-2004','2004-2005','2005-2006','2006-2007','2007-2008','2008-2009','2009-2010','2010-2011','2011-2012','2008;2008') AND a.loadnumber != 0 and a.loadnumber < 100000000";
				System.out.println("SQLQUERY=" + sqlQuery);
				rs = stmt.executeQuery(sqlQuery);
			} else if (Combiner.CURRENTDB.equalsIgnoreCase("chm") || Combiner.CURRENTDB.equalsIgnoreCase("pch")
					|| Combiner.CURRENTDB.equalsIgnoreCase("geo") || Combiner.CURRENTDB.equalsIgnoreCase("elt")) {
				// modify for author/affiliation project by hmo at 4/5/2017
				sqlQuery = "select UPDATENUMBER,CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APICT, APICT1,APILT, APILT1,CLASSIFICATIONDESC,APIAMS, SEQ_NUM,GRANTLIST,ISOPENACESS,CSXTERM,null as cafe_author,b.null as cafe_author1,null as cafe_affiliation,null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid, CONFSERIESID, CONFSERIESTITLE, CONFSERIESACRONYM, SOURCECOUNTRY, count(*) over() totalCount from "
						+ Combiner.TABLENAME + " where  PPUBLICATIONYEAR='" + year + "' and database='"
						+ Combiner.CURRENTDB + "'";
				System.out.println("SQLQUERY=" + sqlQuery);
				rs = stmt.executeQuery(sqlQuery);
			} else if (Combiner.CURRENTDB.equalsIgnoreCase("cpx")) {
				
				sqlQuery = "select a.UPDATENUMBER, a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS,a.CSXTERM,a.CONFSERIESID, a.CONFSERIESTITLE, a.CONFSERIESACRONYM, a.SOURCECOUNTRY, count(*) over() totalCount from "
						+ Combiner.TABLENAME
						+ " a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and  a.PUBLICATIONYEAR='"
						+ year + "'";
				System.out.println("SQLQUERY=" + sqlQuery);
				rs = stmt.executeQuery(sqlQuery);

			} else {
				sqlQuery = "select UPDATENUMBER,CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APICT, APICT1,APILT, APILT1,CLASSIFICATIONDESC,APIAMS,SEQ_NUM,GRANTLIST,ISOPENACESS,null as cafe_author,b.null as cafe_author1,null as cafe_affiliation,null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,SOURCEBIBTEXT,STANDARDID,STANDARDDESIGNATION,NORMSTANDARDID,GRANTTEXT,CSXTERM, CONFSERIESID, CONFSERIESTITLE, CONFSERIESACRONYM, SOURCECOUNTRY, count(*) over() totalCount from "
						+ Combiner.TABLENAME + " where  PUBLICATIONYEAR='" + year + "' AND database='"
						+ Combiner.CURRENTDB + "'";
				rs = stmt.executeQuery(sqlQuery);			
			}
			System.out.println("Got records ...from table::" + Combiner.TABLENAME);
			writeRecs(rs, con, year, Combiner.TABLENAME, Combiner.CURRENTDB);
			System.out.println("Wrote records.");
			this.writer.end();
			this.writer.flush();

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setCittypeList(List cittypeList)
	{
		this.cittypeList=cittypeList;
	}

	public void writeCombinedByTableHook(Connection con) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		try {

			// stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			// ResultSet.CONCUR_READ_ONLY);
			stmt = con.createStatement();
			System.out.println("Running the query...");
		
			String sqlQuery = "select UPDATENUMBER,CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM,GRANTLIST,null as cafe_author,null as cafe_author1,null as cafe_affiliation, null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,SOURCEBIBTEXT,STANDARDID,STANDARDDESIGNATION,NORMSTANDARDID,GRANTTEXT,ISOPENACESS,eid,CSXTERM, CONFSERIESID, CONFSERIESTITLE, CONFSERIESACRONYM, SOURCECOUNTRY, count(*) over() totalCount from "
					+ Combiner.TABLENAME + " where database='" + Combiner.CURRENTDB + "'";
					
			String sqlCpxQuery = "select a.UPDATENUMBER,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS,a.CSXTERM,a.CONFSERIESID, A.CONFSERIESTITLE, A.CONFSERIESACRONYM, a.SOURCECOUNTRY, count(*) over() totalCount from "
					+ Combiner.TABLENAME
					+ " a left outer join db_cafe.CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join db_cafe.cafe_master b on b.pui=c.pui where a.database='cpx'";
			if (Combiner.CURRENTDB.equals("cpx")) {
				System.out.println(sqlCpxQuery);
				rs = stmt.executeQuery(sqlCpxQuery);
			} else {
				System.out.println(sqlQuery);
				rs = stmt.executeQuery(sqlQuery);
			}

			System.out.println("Got records ...from table::" + Combiner.TABLENAME);
			// writeRecs(rs,con,1,Combiner.TABLENAME,Combiner.CURRENTDB);
			writeRecs(rs, con, 1, "extractByLoadnumber--" + Combiner.TABLENAME, Combiner.CURRENTDB);
			System.out.println("Wrote records.");
			this.writer.end();
			this.writer.flush();

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static int getResultSetSize(Connection con, int week, String action, String database) {
		int size = -1;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;

		try {
			stmt = con.createStatement();

			if (database.equalsIgnoreCase("cpx") && action.equalsIgnoreCase("extractupdate")) {
				if (week == 1) {
					query = "select count(*) count from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='"
							+ database + "'";
				} else {
					query = "select count(*) count from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='"
							+ database + "' and a.updateNumber='" + week + "'";
				}
			} else if (!database.equalsIgnoreCase("cpx") && action.equalsIgnoreCase("extractupdate")) {
				query = "select count(*) count from bd_master_orig where database='" + database + "' and updateNumber='"
						+ week + "'";
			} else if (database.equals("cpx") && action.equalsIgnoreCase("extractcafe")) {
				query = "select count(*) count from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and b.updateNumber='"
						+ week + "'";
			} else if (database.equals("cpx") && action.equalsIgnoreCase("extractcafeloadnumber")) {
				query = "select count(*) count from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and b.loadnumber='"
						+ week + "'";
			} else if (database.equals("cpx") && action.equalsIgnoreCase("extractallupdate")) {
				query = "select count(*) count from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and a.updateNumber in ('"
						+ week + "','" + week + "01','" + week + "02','" + week + "05','" + week + "07','" + week
						+ "20')";
			} else if (database.equals("cpx") && action.equalsIgnoreCase("extractcafedelete")) {
				query = "select count(*) count from bd_master_orig a where a.database='cpx' and a.cafe_pui in(select b.pui from CAFE_PUI_LIST_MASTER b, CAFE_weekly_DELETION c where b.puisecondary=c.pui)";
			} else if (database.equals("cpx") && action.equalsIgnoreCase("extractcafemapping")) {
				query = "select count(*) count from bd_master_orig a,cafe_master b,bd_cafe_mapping c where a.database='cpx' and a.updateNumber='"
						+ week + "' and a.pui=c.bd_pui and b.pui=c.cafe_pui";
			} else if (database.equals("cpx") && action.equalsIgnoreCase("extractnumerical")) {
				query = "select count(*) count from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and a.m_id in (select distinct mid from bd_master_numerical where mid is not null and loadnumber='"
						+ week + "')";
			} else if (database.equals("cpx") && action.equalsIgnoreCase("extractauthorlookupindex")) {
				query = "select count(*) count from bd_master_orig a,cafe_au_lookup b,cafe_af_lookup c where (b.loadnumber="
						+ week + " or c.loadnumber=" + week
						+ ") and a.pui=b.pui and a.pui=c.pui group by a.pui) b where a.pui=b.pui";
			} else if (action.indexOf("extractByLoadnumber") > -1) {
				String tableName = action.replaceAll("extractByLoadnumber--", "");
				if (database.equals("cpx")) {
					if (week == 1) {
						query = "select count(*) count from " + tableName
								+ " a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx'";
					} else {
						query = "select count(*) count from " + tableName
								+ " a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.LOADNUMBER="
								+ week + " AND a.database='cpx' and a.loadnumber != 0";
					}
				} else {
					if (week == 1) {
						query = "SELECT count(*) count FROM " + tableName + " where database='" + database + "'";
					} else if (week > 1000) {
						query = "SELECT count(*) count FROM " + tableName + " WHERE  database='" + database
								+ "' and loadnumber=" + week;

					}
				}
			}
			System.out.println("Running the query..." + query);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				size = rs.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return size;
		}
}
	
	 public List getCittype(Connection con,String database)
	 {
    	Statement stmt = null;
        ResultSet rs = null;
        List<String> cittypeList = new ArrayList<>();
        String cittype = null;
       
        try
        {
            stmt = con.createStatement();
            String sqlquery="select trim(cittype) cittype,database from bd_master_cittype where database='"+database+"'";
            //System.out.println("get cittype "+sqlquery);
            rs = stmt.executeQuery(sqlquery);
            while (rs.next())
            {
                cittype = rs.getString("cittype");
                database = rs.getString("database");
                if(cittype != null)
                {
                	cittypeList.add(cittype.toLowerCase().trim());
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {

            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
       return cittypeList;
       
    }
	
public void writeRecs(ResultSet rs, Connection con, int week, String tableName, String database)
    throws Exception
{
    int i = 0;
    int problemRecordCount = 0;
    EVCombinedRec recSecondBox = null;
    EVCombinedRec[] recArray = null;
    boolean isGeoBase = false;
    boolean isChimica = false;
    boolean isCpx = false;
    String accessNumber = "";
    String pui = "";
    KafkaService kafka=null;
    Map<String,String> batchData = new ConcurrentHashMap<String,String>();   
    Map<String,String> missedData = new ConcurrentHashMap<String,String>();
    int batchSize = 0;
    int counter = 0;
    
    MessageSender sendMessage=null;
    Thread thread = null; 
    long processTime = System.currentTimeMillis();
    
    int totalCount =0;
    //int MAX_THREAD = 150; 
    //ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD);  
    

    try
    {
    	
    	totalCount = getResultSetSize(con,week,tableName,database);  
    	if(this.propertyFileName != null && (getAction() == null || !(getAction().equalsIgnoreCase("lookup")))) // HT only create Kafka instance when it is // not lookup extraction
    	{
    		kafka = new KafkaService(processTime+"_"+database+"_"+week, this.propertyFileName); //use it for ES extraction
    	}
    	
        while (rs.next())
        {       
          
          String firstGUID = "";        
          int numCoords = 1;
          int coordCount = 0;  
          accessNumber = rs.getString("ACCESSNUMBER");//move this one to the top of the loop to avoid missed placement of the accessnumber at 3/30/2022 by hmo
          if(rs.getString("DATABASE") != null)
          {
              if(rs.getString("DATABASE").equals("geo"))
              {
                    isGeoBase = true;
              }
              if(rs.getString("DATABASE").equals("chm"))
              {
                    isChimica = true;
              }
              if(rs.getString("DATABASE").equals("cpx"))
              {
                  isCpx = true;
              }
          }
          
          Vector recVector = new Vector();
          try
          {
        	  if(this.cittypeList==null)
              {
        		  this.cittypeList = getCittype(con,database);
        		  System.out.println("get cittype");
              }
        	  
              for(int currentCoord = 0; currentCoord < numCoords; currentCoord++)
              {
                String[] coords = null;
                String[] secondBoxCoords= null;
                EVCombinedRec rec = new EVCombinedRec();
                pui = rs.getString("PUI");
                puiGlobal = pui;
                String cittype = rs.getString("CITTYPE"); 
                
                //added by hmo to block records with new cittype to index @10/14/2020
                if(cittype!=null && !this.cittypeList.contains(cittype.toLowerCase().trim()))
                {
                	System.out.println("BLOCK records "+pui+" with unknown cittype \""+cittype+"\"");
                	continue;
                }
                
                if(validYear(rs.getString("PUBLICATIONYEAR")))
                { 
                	++i;
                	//totalCount = rs.getInt("TOTALCOUNT");
                	String processInfo = processTime+"-"+totalCount+"-"+i+"-"+rs.getString("DATABASE")+"-"+rs.getString("LOADNUMBER")+"-"+rs.getString("UPDATENUMBER")+"-"+week;
                	rec.put(EVCombinedRec.PROCESS_INFO, processInfo);	          
                	
                	
                	if(isCpx && rs.getString("authorid")!=null)
                	{
                		String authorid = rs.getString("authorid");
                		String[] aid = authorid.split(",");
                		rec.put(EVCombinedRec.AUTHORID, aid);
                	}
                	
                	if(isCpx && rs.getString("affid")!=null)
                	{
                		String affid = rs.getString("affid");
                		String[] affids = affid.split(",");
                		rec.put(EVCombinedRec.AFFILIATIONID, affids);
                	}
                	
                	//move this block of code to here to include Affiliation ID while BD affiliation is null
                	if(rs.getString("affid")==null && rs.getString("CAFE_AFFILIATION")!=null)
                    {
                    	String cafeAffString = rs.getString("CAFE_AFFILIATION");
                    	if(rs.getString("CAFE_AFFILIATION1")!=null)
                    	{
                    		cafeAffString = cafeAffString+rs.getString("CAFE_AFFILIATION1");
                    	}
           
                    	BdAffiliations caff = new BdAffiliations(cafeAffString);
                    	caff.getSearchValue();     
                    	
                    	rec.put(EVCombinedRec.AFFILIATIONID, caff.getAffiliationId());
                    	rec.put(EVCombinedRec.DEPARTMENTID,  caff.getDepartmentId());
                    	
                    }
                	//end of the block
                	
                    if(rs.getString("AUTHOR") != null)
                    {
                        String authorString = rs.getString("AUTHOR");
                        if(rs.getString("AUTHOR_1") !=null)
                        {
                            authorString=authorString+rs.getString("AUTHOR_1");
                        }
                        
                        rec.put(EVCombinedRec.AUTHOR, prepareBdAuthor(authorString));
                        //use this to deal with author alias
                        
                        //added for first author  EVOPS-1193 @8/31/2021
                        rec.put(EVCombinedRec.FIRST_AUTHOR, getFirstAuthor(authorString));
                        
                        rec.put(EVCombinedRec.TRANSLATOR, prepareBdAuthorWithAlias(authorString));
                        
                        //if(rs.getString("authorid")==null && rs.getString("cafe_author")!=null)
                        if(rs.getString("cafe_author")!=null)
                        {
                        	String cafeAuthorString = rs.getString("cafe_author");
                        	if(rs.getString("cafe_author1")!=null)
                        	{
                        		cafeAuthorString = cafeAuthorString+rs.getString("cafe_author1");
                        	}
                        	String[] cafeAuthor = prepareBdAuthor(cafeAuthorString);
	                        String[] cafe_aid = getAuID();	                        
	                        rec.put(EVCombinedRec.AUTHORID, cafe_aid);
                        }
                       // else
                        //{
                        //	System.out.println("No authorid for record "+rs.getString("ACCESSNUMBER"));
                        //}
                        
                        String affiliation = null;
                        
                        if (rs.getString("AFFILIATION") != null)
                        {
                            affiliation = rs.getString("AFFILIATION");
                            if(rs.getString("AFFILIATION_1")!=null)
                            {
                                affiliation = affiliation+rs.getString("AFFILIATION_1");
                            }
                            
                         
                            BdAffiliations aff = new BdAffiliations(affiliation);
                            rec.put(EVCombinedRec.AUTHOR_AFFILIATION,  aff.getSearchValue());
                            rec.put(EVCombinedRec.AFFILIATION_LOCATION,  aff.getLocationsSearchValue());
                            rec.put(EVCombinedRec.COUNTRY,  aff.getCountriesSearchValue());                            
                          
                        }
                        else if(rs.getString("AFFILIATION_1")!=null)
                        {
                            affiliation = rs.getString("AFFILIATION_1");
                            BdAffiliations aff = new BdAffiliations(affiliation);
                            rec.put(EVCombinedRec.AUTHOR_AFFILIATION,  aff.getSearchValue());
                            rec.put(EVCombinedRec.AFFILIATION_LOCATION,  aff.getLocationsSearchValue());
                            rec.put(EVCombinedRec.COUNTRY,  aff.getCountriesSearchValue());
                            rec.put(EVCombinedRec.AFFILIATIONID,  aff.getAffiliationId());
                            rec.put(EVCombinedRec.DEPARTMENTID,  aff.getDepartmentId());
                            if(rs.getString("affid")==null && rs.getString("CAFE_AFFILIATION1")!=null)
                            {
                            	String cafeAffString = rs.getString("CAFE_AFFILIATION1");                           	
                            	BdAffiliations caff = new BdAffiliations(cafeAffString);
                            	caff.getSearchValue();   
                            	rec.put(EVCombinedRec.AFFILIATIONID,  caff.getAffiliationId());
                            	rec.put(EVCombinedRec.DEPARTMENTID,  caff.getDepartmentId());
                            }

                        }
                        else if(rs.getString("CORRESPONDENCEAFFILIATION") != null)
                        {
                            affiliation = rs.getString("CORRESPONDENCEAFFILIATION");
                            BdCorrespAffiliations aff = new BdCorrespAffiliations(affiliation);
                            rec.put(EVCombinedRec.AUTHOR_AFFILIATION,  aff.getSearchValue());
                            rec.put(EVCombinedRec.AFFILIATION_LOCATION,  aff.getLocationsSearchValue());
                            rec.put(EVCombinedRec.COUNTRY,  aff.getCountriesSearchValue());
                            rec.put(EVCombinedRec.AFFILIATIONID,  aff.getAffiliationId());
                            rec.put(EVCombinedRec.DEPARTMENTID,  aff.getDepartmentId());
                            if(rs.getString("CAFE_CORRESPONDENCEAFFILIATION")!=null)
                            {
                            	String cafeAffString = rs.getString("CAFE_CORRESPONDENCEAFFILIATION");                           	
                            	BdAffiliations caff = new BdAffiliations(cafeAffString);
                            	caff.getSearchValue();   
                            	rec.put(EVCombinedRec.AFFILIATIONID,  caff.getAffiliationId());
                            	rec.put(EVCombinedRec.DEPARTMENTID,  caff.getDepartmentId());
                            }
                        }
                    }

                    if (rs.getString("EDITORS") != null)
                    {                        
                    	String[] editors = prepareEditor(rs.getString("EDITORS"));
 	                    rec.put(EVCombinedRec.EDITOR, editors);
	                    if(rec.getString(EVCombinedRec.FIRST_AUTHOR)==null)
						{
							rec.put(EVCombinedRec.FIRST_AUTHOR,editors[0]);
						}
                    }

                    if (rs.getString("CITATIONTITLE") != null)
                    {
                       rec.put(EVCombinedRec.TITLE, prepareCitationTitle(rs.getString("CITATIONTITLE")));
                    }

                    if (rs.getString("CITATIONTITLE") != null)
                    {                   
                       rec.put(EVCombinedRec.TRANSLATED_TITLE, prepareTranslatedCitationTitle(rs.getString("CITATIONTITLE")));
                    }

                    //****put translated source title into SERIAL_TITLE_TRANSLATION instead of TRANSLATED_TITLE by hongrong based on Frank request at 8/14/2014
                    if (rs.getString("TRANSLATEDSOURCETITLE") != null)
                    {
                        rec.put(EVCombinedRec.SERIAL_TITLE_TRANSLATION, prepareMulti(rs.getString("TRANSLATEDSOURCETITLE")));
                    }
                    
                    //added by hmo at 8/9/2023 for EVOPS-1497
                    if (rs.getString("SOURCECOUNTRY") != null) {
						rec.put(EVCombinedRec.SOURCE_COUNTRY, CountryFormatter.formatCountry(rs.getString("SOURCECOUNTRY")));
					}

                    if (rs.getString("VOLUMETITLE") != null)
                    {
                        rec.put(EVCombinedRec.VOLUME_TITLE, rs.getString("VOLUMETITLE"));
                    }

                    String abString = getStringFromClob(rs.getClob("ABSTRACTDATA"));
                    if (abString != null)
                    {
                    	abString = getAbstract(abString);
                        rec.put(EVCombinedRec.ABSTRACT, abString);
                    }

                    if (rs.getString("CSXTERM") != null)
                    {
                        rec.put(EVCombinedRec.CSX_TERMS, prepareMulti(rs.getString("CSXTERM")));
                    }

                    if(!isChimica)
                    {
                        if (rs.getString("CONTROLLEDTERM") != null)
                        {
                            rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("CONTROLLEDTERM")));
                        }
                    }
                    else
                    {
                        if (rs.getString("CHEMICALTERM") != null)
                        {
                            rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("CHEMICALTERM")));
                        }
                    }


                    if (rs.getString("UNCONTROLLEDTERM") != null)
                    {
                        rec.put(EVCombinedRec.UNCONTROLLED_TERMS, prepareMulti(rs.getString("UNCONTROLLEDTERM")));
                    }
                    else if(rs.getString("SPECIESTERM") != null)
                    {
                         rec.put(EVCombinedRec.UNCONTROLLED_TERMS, prepareMulti(rs.getString("SPECIESTERM")));
                    }

                    if (rs.getString("REGIONALTERM") != null)
                    {
                         String regionalterm = rs.getString("REGIONALTERM");
                         String[] geobasemaintermsrgi = regionalterm.split(Constants.AUDELIMITER);
                         rec.put(EVCombinedRec.CHEMICALTERMS, prepareMulti(regionalterm));
                         List navigatorterms = null;
                         if(isGeoBase)
                         {
                             navigatorterms = new ArrayList();
                             GeobaseToGeorefMap lookup = GeobaseToGeorefMap.getInstance();

                             for(int j = 0; j < geobasemaintermsrgi.length; j++)
                             {
                                 String georefterm = lookup.lookupGeobaseTerm(geobasemaintermsrgi[j]);
                                 if(georefterm != null)
                                 {
                                     navigatorterms.add(georefterm);

                                     GeoRefBoxMap coordLookup = GeoRefBoxMap.getInstance();
                                     String coordString = coordLookup.lookupGeoRefTermCoordinates(georefterm.trim());
                                     if(coordString != null)
                                     {
                                       coords = parseCoordinates(coordString);

                                       if(coords != null &&  coords[4].indexOf("-") == -1 && coords[3].indexOf("-") != -1)
                                       {
                                         coords[3] = "180";
                                       }
                                       if(j == currentCoord)
                                       {
                                         rec.put(EVCombinedRec.LAT_SE, coords[1]);
                                         rec.put(EVCombinedRec.LAT_NW, coords[2]);
                                         rec.put(EVCombinedRec.LNG_SE, coords[3]);
                                         rec.put(EVCombinedRec.LNG_NW, coords[4]);
                                         rec.put(EVCombinedRec.LAT_NE, coords[2]);
                                         rec.put(EVCombinedRec.LNG_NE, coords[3]);
                                         rec.put(EVCombinedRec.LAT_SW, coords[1]);
                                         rec.put(EVCombinedRec.LNG_SW, coords[4]);
                                       }
                                     }
                                 }
                             }
                         }
                         if(navigatorterms !=null && !navigatorterms.isEmpty())
                         {
                            rec.putIfNotNull(EVCombinedRec.INT_PATENT_CLASSIFICATION, (String[])navigatorterms.toArray(new String[]{}));
                         }
                    }

                    if(!isChimica && rs.getString("CHEMICALTERM") != null)
                    {
                        rec.put(EVCombinedRec.CHEMICALTERMS, prepareMulti(rs.getString("CHEMICALTERM")));
                    }


                    String[] issnArray = null;
                    if (rs.getString("ISSN") != null && rs.getString("EISSN") != null)
                    {
                        issnArray = new String[2];
                        issnArray[0] = rs.getString("ISSN");
                        issnArray[1] = rs.getString("EISSN");
                    }
                    else if (rs.getString("EISSN") != null)
                    {
                        issnArray = new String[1];
                        issnArray[0] = rs.getString("EISSN");
                    }
                    else if(rs.getString("ISSN") != null)
                    {
                        issnArray = new String[1];
                        issnArray[0] = rs.getString("ISSN");
                    }

                    if(issnArray != null)
                    {
                        rec.put(EVCombinedRec.ISSN, issnArray);
                    }

                    if (rs.getString("CODEN") != null)
                    {
                        rec.put(EVCombinedRec.CODEN, BdCoden.convert(rs.getString("CODEN")));
                    }

                    String isbnString = rs.getString("ISBN");
                    if (isbnString!= null)
                    {
                        rec.put(EVCombinedRec.ISBN, prepareISBN(isbnString));
                    }

                    String st = rs.getString("SOURCETITLE");
                    String sta = rs.getString("SOURCETITLEABBREV");
                    if (st == null)
                    {
                        st = sta;
                    }

                    if (st != null)
                    {
                        rec.put(EVCombinedRec.SERIAL_TITLE, st);
                    }

                    if(sta != null)
                    {
                         rec.put(EVCombinedRec.ABBRV_SRC_TITLE, sta);
                    }
                    
                    //added by hmo at 8/9/2023 for EVOPS-1497
                    if (rs.getString("SOURCECOUNTRY") != null) {
						rec.put(EVCombinedRec.SOURCE_COUNTRY, CountryFormatter.formatCountry(rs.getString("SOURCECOUNTRY")));
					}

                    if (rs.getString("PUBLISHERNAME") != null)
                    {
                        rec.put(EVCombinedRec.PUBLISHER_NAME, preparePublisherName(rs.getString("PUBLISHERNAME")));
                    }

                    if (rs.getString("TREATMENTCODE") != null)
                    {
                        rec.put(EVCombinedRec.TREATMENT_CODE,
                                prepareMulti(getTreatmentCode(rs.getString("TREATMENTCODE"))));
                    }

                    String la = rs.getString("CITATIONLANGUAGE");

                    if (la != null)
                    {
                        rec.put(EVCombinedRec.LANGUAGE,prepareLanguage(la));
                    }

                    String docType = rs.getString("CITTYPE");
                    String sourceType = rs.getString("SOURCETYPE");
                    
                    //added by hmo at 1/15/2020 based on EVOPS-884
                    //System.out.println("SOURCETYPE= "+sourceType);
                    if(sourceType!=null)
                    {
                    	 rec.put(EVCombinedRec.SOURCE_TYPE, sourceType);
                    }
                    
                    if(docType != null)
                    {
                        boolean confCodeFlag = false;
                        if(rs.getString("CONFCODE") != null)
                        {
                            confCodeFlag = true;
                        }

                        String ct = null;
                        if(sourceType!=null && sourceType.equalsIgnoreCase("b") &&
                           (docType.equalsIgnoreCase("bk") || docType.equalsIgnoreCase("ch") || docType.equalsIgnoreCase("mr") || docType.equalsIgnoreCase("mc") ||
                        	docType.equalsIgnoreCase("ed") || docType.equalsIgnoreCase("sh") || docType.equalsIgnoreCase("le") || docType.equalsIgnoreCase("no")))
                        	
                        {
                        	if(docType.equalsIgnoreCase("ch") || docType.equalsIgnoreCase("mc"))
                        	{
                        		rec.put(EVCombinedRec.DOCTYPE,"ch");
                        	}
                        	else if(docType.equalsIgnoreCase("ed") )
                        	{
                        		rec.put(EVCombinedRec.DOCTYPE,"ed");
                        	}
                        	else if(docType.equalsIgnoreCase("sh") )
                        	{
                        		rec.put(EVCombinedRec.DOCTYPE,"sh");
                        	}
                        	else if(docType.equalsIgnoreCase("le") )
                        	{
                        		rec.put(EVCombinedRec.DOCTYPE,"le");
                        	}
                        	else if(docType.equalsIgnoreCase("no") )
                        	{
                        		rec.put(EVCombinedRec.DOCTYPE,"no");
                        	}
                        	else
                        	{
                        		rec.put(EVCombinedRec.DOCTYPE,"bk");
                        	}
                        }
                        else if((ct = getCitationType(docType,confCodeFlag)) != null)
                        {
                        	/* remove CORE pre T.M. instruction on 4/14/2020
                            if((isCpx)&&((rs.getString("MAINHEADING") != null)|| rec.containsKey(EVCombinedRec.CONTROLLED_TERMS)))
                            {
                                ct = ct + " CORE";
                            }
                            */
                            rec.put(EVCombinedRec.DOCTYPE,ct);
                        }
                    }
                    else if(isCpx)
                    {
                    	/* remove CORE pre T.M. instruction on 4/14/2020
                        docType = "";
                        if ((rs.getString("MAINHEADING") != null)|| rec.containsKey(EVCombinedRec.CONTROLLED_TERMS)){
                            docType = docType + " CORE";
                        }
                        */
                        rec.put(EVCombinedRec.DOCTYPE, docType);
                    }
                    
                    //overwrite the doctype with "ST" when standardid is available
                    if(isCpx && sourceType!=null && sourceType.equalsIgnoreCase("b") && rs.getString("STANDARDID") != null)
            		{
                    	 rec.put(EVCombinedRec.DOCTYPE, "ST");
            		}

                    if (rs.getString("CLASSIFICATIONCODE") != null &&
                            !rs.getString("DATABASE").equalsIgnoreCase("elt"))
                    {
                        rec.put(EVCombinedRec.CLASSIFICATION_CODE,
                                prepareMulti(formatClassCodes(rs.getString("CLASSIFICATIONCODE"))));
                    }
                    if (rs.getString("CLASSIFICATIONDESC") != null &&
                            rs.getString("DATABASE").equalsIgnoreCase("elt"))
                    {
                        rec.put(EVCombinedRec.CLASSIFICATION_CODE,
                                prepareMulti(rs.getString("CLASSIFICATIONDESC")));
                    }
                    if (rs.getString("CONFCODE") != null)
                    {
                        rec.put(EVCombinedRec.CONFERENCE_CODE, rs.getString("CONFCODE"));
                    }

                    if (rs.getString("CONFNAME") != null)
                    {
                        rec.put(EVCombinedRec.CONFERENCE_NAME, rs.getString("CONFNAME"));
                    }

                    String cl = rs.getString("CONFLOCATION");

                    if (cl !=null && cl.length() > 2)
                    {
                        rec.put(EVCombinedRec.CONFERENCE_LOCATION,prepareConfLocation(cl) );
                    }

                    if (rs.getString("CONFDATE") != null)
                    {
                        rec.put(EVCombinedRec.MEETING_DATE, rs.getString("CONFDATE"));
                    }

                    if (rs.getString("CONFSPONSORS") != null)
                    {
                        rec.put(EVCombinedRec.SPONSOR_NAME, prepareMulti(rs.getString("CONFSPONSORS")));
                    }

                    if (rs.getString("CONFERENCEORGANIZATION") != null)
                    {
                        rec.put(EVCombinedRec.CONFERENCEAFFILIATIONS, prepareMulti(rs.getString("CONFERENCEORGANIZATION")));
                    }

                    if (rs.getString("CONFERENCEEDITOR") != null)
                    {
                        rec.put(EVCombinedRec.CONFERENCEEDITORS, prepareEditor(rs.getString("CONFERENCEEDITOR")));
                    }

                    if (rs.getString("CONFERENCEPARTNUMBER") != null)
                    {
                        rec.put(EVCombinedRec.CONFERENCEPARTNUMBER,rs.getString("CONFERENCEPARTNUMBER"));
                    }

                    if (rs.getString("CONFERENCEPAGERANGE") != null)
                    {
                        rec.put(EVCombinedRec.CONFERENCEPAGERANGE,rs.getString("CONFERENCEPAGERANGE"));
                    }

                    if (rs.getString("CONFERENCEPAGECOUNT") != null)
                    {
                        rec.put(EVCombinedRec.CONFERENCENUMBERPAGES,rs.getString("CONFERENCEPAGECOUNT"));
                    }

                    //============ Add for confseries by hmo @8/4/2023=================== 
                    
                    if (rs.getString("CONFSERIESID") != null)
                    {
                        rec.put(EVCombinedRec.CONFSERIESID,rs.getString("CONFSERIESID"));
                    }                                    
                    
                    if (rs.getString("CONFNAME") != null)
                    {
                        rec.put(EVCombinedRec.CONFSERIALNAME,rs.getString("CONFNAME"));
                    }                              
                                                                              
                    if (rs.getString("CONFSERIESACRONYM") != null)
                    {
                        rec.put(EVCombinedRec.CONFSERIALACRONYM,rs.getString("CONFSERIESACRONYM"));
                    }
                    
                    //========================================================================
                    
                    if (rs.getString("ISSUETITLE") != null)
                    {
                        rec.put(EVCombinedRec.MONOGRAPH_TITLE, rs.getString("ISSUETITLE"));
                    }

                    rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));

                    rec.put(EVCombinedRec.DATABASE, rs.getString("DATABASE"));
                    
                    //only output the first six digits of the loadnumber  by hmo@8/23/2017
                    if(rs.getString("LOADNUMBER").length()>=6)
                    {
                    	rec.put(EVCombinedRec.LOAD_NUMBER, (rs.getString("LOADNUMBER")).substring(0,6));
                    }
                    else
                    {
                    	rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOADNUMBER"));
                    }
                    
                    if(rs.getString("UPDATENUMBER") !=null)
                    {
                    	rec.put(EVCombinedRec.UPDATE_NUMBER, rs.getString("UPDATENUMBER"));
                    }

                    if (rs.getString("PUBLICATIONYEAR") != null && rs.getString("PUBLICATIONYEAR").length()>3)
                    {
                        rec.put(EVCombinedRec.PUB_YEAR, rs.getString("PUBLICATIONYEAR").substring(0,4));
                        //System.out.println("YEAR="+rs.getString("PUBLICATIONYEAR").substring(0,4));
                    }

                    rec.put(EVCombinedRec.DEDUPKEY,
                            getDedupKey(rec.get(EVCombinedRec.ISSN),
                                        rec.get(EVCombinedRec.CODEN),
                                        rs.getString("VOLUME"),
                                        rs.getString("ISSUE"),
                                        getPage(rs.getString("PAGE"), rs.getString("ARTICLENUMBER"))));

                    rec.put(EVCombinedRec.VOLUME, rs.getString("VOLUME"));
                    rec.put(EVCombinedRec.ISSUE, rs.getString("ISSUE"));
                    //rec.put(EVCombinedRec.STARTPAGE, getFirstPage(getPage(rs.getString("PAGE"), rs.getString("ARTICLENUMBER"), rs.getString("ISSN"))));
                    rec.put(EVCombinedRec.STARTPAGE, getPage(getFirstPage(rs.getString("PAGE")),rs.getString("ARTICLENUMBER")));
                    //accessNumber = rs.getString("ACCESSNUMBER");

                    rec.put(EVCombinedRec.ACCESSION_NUMBER,
                            rs.getString("ACCESSNUMBER"));

                    if (rs.getString("REPORTNUMBER") != null)
                    {
                        rec.put(EVCombinedRec.REPORTNUMBER,rs.getString("REPORTNUMBER"));
                    }

                    if(rs.getString("DOI")!=null)
                    {
                        rec.put(EVCombinedRec.DOI,rs.getString("DOI"));
                    }

                    if(rs.getString("COPYRIGHT") != null)
                    {
                        rec.put(EVCombinedRec.COPYRIGHT,rs.getString("COPYRIGHT"));
                    }

                    if(rs.getString("PII") != null)
                    {
                        rec.put(EVCombinedRec.PII,rs.getString("PII"));
                    }

                    if(rs.getString("PUI") != null)
                    {
                    	pui = rs.getString("PUI");
                    	//System.out.println("PUI="+pui);
                        rec.put(EVCombinedRec.PUI,rs.getString("PUI"));
                    }

                    if (rs.getString("ASSIG") != null)
                    {
                        rec.put(EVCombinedRec.COMPANIES, prepareMulti(rs.getString("ASSIG")));
                    }

                    if (rs.getString("CASREGISTRYNUMBER") != null && rs.getString("DATABASE") != null)
                    {
                        rec.put(EVCombinedRec.CASREGISTRYNUMBER, prepareCASRegistry(rs.getString("CASREGISTRYNUMBER"), rs.getString("DATABASE")));
                    }

                    //if (rs.getString("DATABASE").equals("cpx")) //remove this line to give datesort to all database by Hongrong @10/24/2023
                    {
                        rec.put(EVCombinedRec.DATESORT, prepareDateSort(rs.getString("DATESORT"),rs.getString("PUBLICATIONYEAR")));
                    }

                    if (rs.getString("SEQ_NUM") != null)
                    {
                        rec.put(EVCombinedRec.PARENT_ID, rs.getString("SEQ_NUM"));
                    }

                    rec.put(EVCombinedRec.PUB_SORT, Integer.toString(i));

                    if(currentCoord == 0)
                    {
                        firstGUID = rs.getString("M_ID");
                    }
                    if(numCoords == 1)
                    {
                        rec.putIfNotNull(EVCombinedRec.DOCID, firstGUID);
                    }
                    else
                    {
                        if(!isGeoBase || (isGeoBase && rec.get(EVCombinedRec.LAT_SE) != null))
                        {
                            coordCount++;
                        }

                        if(coordCount == 0)
                        {
                            coordCount++;
                        }

                        rec.putIfNotNull(EVCombinedRec.DOCID, firstGUID + "_" + (coordCount));
                    }
                    CVTerms cvterms = null;
                    String apict = replaceNull(rs.getString("apict"));
                    String apict1 = replaceNull(rs.getString("apict1"));

                    if(apict != null && !apict.trim().equals(""))
                    {
                        if(apict1 != null &&
                                !apict1.trim().equals(""))
                        {
                            apict = apict.concat(apict1);
                        }
                        cvterms = new CVTerms(apict);
                        cvterms.parse();

                    }

                    if (rs.getString("MAINHEADING") != null &&
                            !rs.getString("DATABASE").equalsIgnoreCase("elt"))
                    {
                        rec.put(EVCombinedRec.MAIN_HEADING, prepareMulti(rs.getString("MAINHEADING")));
                    }


                    if(cvterms != null)
                    {
                        QualifierFacet qfacet = new QualifierFacet();

                        String[] cvt = getCvs(cvterms.getCvt());

                        if(cvt != null)
                        {

                            rec.put(EVCombinedRec.CONTROLLED_TERMS, cvt);
                        }

                        String[] cvtm = getCvs(cvterms.getCvm());

                        if(cvtm != null &&
                                rs.getString("DATABASE").equalsIgnoreCase("elt"))
                        {
                             rec.put(EVCombinedRec.MAIN_HEADING, cvtm);
                             //this field is added to generate navigators for Major terms
                             rec.put(rec.ECLA_CODES, cvtm);
                        }

                        String[] norole = getCvs(cvterms.getCvn());

                        if(norole != null)
                        {
                             qfacet.setNorole(norole.toString());
                             rec.put(EVCombinedRec.NOROLE_TERMS, norole);
                        }

                        String[] reagent = getCvs(cvterms.getCva());

                        if(reagent != null)
                        {
                            qfacet.setReagent(reagent.toString());
                            rec.put(EVCombinedRec.REAGENT_TERMS, reagent);
                        }

                        String[] product = getCvs(cvterms.getCvp());

                        if(product != null)
                        {
                            qfacet.setProduct(product.toString());
                            rec.put(EVCombinedRec.PRODUCT_TERMS, product);
                        }

                        String[] mnorole = getCvs(cvterms.getCvmn());

                        if(mnorole != null)
                        {
                            qfacet.setNorole(mnorole.toString());
                            rec.put(EVCombinedRec.MAJORNOROLE_TERMS, mnorole);
                        }

                        String[] mreagent = getCvs(cvterms.getCvma());

                        if(mreagent != null)
                        {
                            qfacet.setReagent(mreagent.toString());
                            rec.put(EVCombinedRec.MAJORREAGENT_TERMS, mreagent);
                        }

                        String[] mproduct = getCvs(cvterms.getCvmp());

                        if(mproduct != null)
                        {
                            qfacet.setProduct(mproduct.toString());
                            rec.put(EVCombinedRec.MAJORPRODUCT_TERMS, mproduct);
                        }
                        rec.put(rec.USPTOCODE, prepareMulti(qfacet.getValue()));
                    }

                    CVTerms ltterms = null;
                    String apilt = replaceNull(rs.getString("apilt"));
                    String apilt1 = replaceNull(rs.getString("apilt1"));

                    if(apilt != null && !apilt.trim().equals(""))
                    {
                        if(apilt1 != null &&
                                !apilt1.trim().equals(""))
                        {
                            apilt = apilt.concat(apilt1);
                        }
                        ltterms = new CVTerms(apilt);
                        ltterms.parse();
                    }

                    if(ltterms != null)
                    {
                        String ltstr = ltterms.getCvexpandstr();

                        if(ltstr != null)
                        {
                            rec.put(EVCombinedRec.LINKED_TERMS, prepareELTCV(ltstr));
                        }
                    }

                    if(rs.getString("apiams") != null)
                    {
                        rec.put(EVCombinedRec.MAIN_TERM, prepareMulti(rs.getString("apiams")));
                    }
                    
                    //REMOVE FOR FAST CHANGE
                    
                    if(rs.getString("GRANTLIST") != null)
                    {
                        rec.put(EVCombinedRec.GRANTID, prepareMulti(getGrantID(rs.getString("GRANTLIST"))));
                        rec.put(EVCombinedRec.GRANTAGENCY, prepareMulti(getGrantAgency(rs.getString("GRANTLIST"))));
                    }
                    
                    if(rs.getString("GRANTTEXT") != null)
                    {
                        rec.put(EVCombinedRec.GRANTTEXT, prepareMulti(rs.getString("GRANTTEXT")));                       
                    }
                    
                    //new business rule from EVOPS-554 at1/25/2018
                    
                    if(rs.getString("SOURCEBIBTEXT") != null && rs.getString("CITTYPE").equalsIgnoreCase("st") && 
                    		sourceType!=null && sourceType.equalsIgnoreCase("b"))
                    {      
                        rec.put(EVCombinedRec.SOURCEBIBTEXT, getStatus(rs.getString("SOURCEBIBTEXT")));                       
                    }
                    
                    if(rs.getString("STANDARDID") != null)
                    {
                        rec.put(EVCombinedRec.STANDARDID, rs.getString("STANDARDID"));
                        
                    }
                    
                    if(rs.getString("STANDARDDESIGNATION") != null)
                    {
                       // rec.put(EVCombinedRec.STANDARDDESIGNATION, formatStandardCodes(rs.getString("STANDARDDESIGNATION")));
                        rec.put(EVCombinedRec.STANDARDDESIGNATION, rs.getString("STANDARDDESIGNATION"));
                        //rec.put(EVCombinedRec.STANDARDDESIGNATION, prepareStandardDesignation(rs.getString("STANDARDDESIGNATION")));
                        
                    }
                    
                    if(rs.getString("NORMSTANDARDID") != null)
                    {
                        rec.put(EVCombinedRec.NORMSTANDARDID, rs.getString("NORMSTANDARDID"));
                        //rec.put(EVCombinedRec.NORMSTANDARDID, formatStandardCodes(rs.getString("NORMSTANDARDID")));
                    }
                    
                    //ISOPENACESS
                    //blocked ISOPENACESS 10/24/2018
                    
                    if(rs.getString("ISOPENACESS") != null)
                    {
                        rec.put(EVCombinedRec.ISOPENACESS, rs.getString("ISOPENACESS"));                        
                    }                 
                                                           
                    //due to bd_master table structure change, we have to handle the missed column exception
                    try
                    {
                    	if(rs.getString("eid") != null)
                    	{
                    		rec.put(EVCombinedRec.EID, rs.getString("eid"));
                    		//System.out.println("EID= "+rs.getString("eid"));
                    	}
                    }
                    catch(Exception e)
                    {
                    	//System.out.println("missing eid column");
                    	//do nothing
                    }
                  
                   //*
                   // * use for numerical index
                   if(pui!=null && pui.length()>0 && isCpx)
                   {
                    	populateNumericalIndex(pui,rec,con);                 
                   }
                   // */
                    
                    try
                    {
                      if(!isGeoBase || (isGeoBase && rec.get(EVCombinedRec.LAT_SE) != null))
                      {
                          //coordCount++;
                          recVector.add(rec);
                          if(recSecondBox != null)
                          {

                            if(secondBoxCoords != null)
                            {
                                coordCount++;
                                for(int b = 0; b < EVCombinedRecKeys.length; b++)
                                {
                                    Object recTemp = rec.get(EVCombinedRecKeys[b]);
                                    if(recTemp != null)
                                    {
                                        recSecondBox.put(EVCombinedRecKeys[b],rec.get(EVCombinedRecKeys[b]));
                                    }
                                }
                                secondBoxCoords[4] = "-180";
                                recSecondBox.put(EVCombinedRec.LAT_SE, secondBoxCoords[1]);
                                recSecondBox.put(EVCombinedRec.LAT_NW, secondBoxCoords[2]);
                                recSecondBox.put(EVCombinedRec.LNG_SE, secondBoxCoords[3]);
                                recSecondBox.put(EVCombinedRec.LNG_NW, secondBoxCoords[4]);
                                recSecondBox.put(EVCombinedRec.LAT_NE, secondBoxCoords[2]);
                                recSecondBox.put(EVCombinedRec.LNG_NE, secondBoxCoords[3]);
                                recSecondBox.put(EVCombinedRec.LAT_SW, secondBoxCoords[1]);
                                recSecondBox.put(EVCombinedRec.LNG_SW, secondBoxCoords[4]);
                                if(coordCount == 0)
                                {
                                    coordCount++;
                                }
                                recSecondBox.putIfNotNull(EVCombinedRec.DOCID, firstGUID + "_" + (coordCount));
                                recVector.add(recSecondBox);
                            }
                          }
                      }
                      else
                      {
                          recVector.add(rec);
                      }
                    }
                    catch(Exception e)
                    {
                      e.printStackTrace();
                    }
                }
                else if(rs.getString("CITTYPE")!=null && (rs.getString("CITTYPE").equals("mm") || rs.getString("CITTYPE").equals("pp")))
                {
                	System.out.println("FOUND DOCTYPE MM OR PP");
                }

            }
           
            recArray = (EVCombinedRec[])recVector.toArray(new EVCombinedRec[0]);
            if(this.propertyFileName == null && (getAction() != null && !(getAction().equalsIgnoreCase("lookup"))))
            {
            	this.writer.writeRec(recArray);//use this line for fast extraction
          
            }
            /*HT added 09/21/2020 for ES lookup*/
			else if(getAction() != null && getAction().equalsIgnoreCase("lookup"))
            {
            	this.lookupObj.writeLookupRecs(recArray);
            }
            else
            {
	            /**********************************************************/
	            //following code used to test kafka by hmo@2020/01/30
	            //this.writer.writeRec(recArray,kafka);
	            /*********************************************************/
	            
	            //use this block of code for sending data to kafka
	            
	            this.writer.writeRec(recArray,kafka, batchData, missedData);
	            this.lookupObj.writeLookupRecs(recArray);						//HT added later for weekly lookup extraction for ES
	            if(counter<batchSize)
	            {            	
	            	counter++;
	            }
	            else
	            { 	            	 
                	 /*
	            	 thread = new Thread(sendMessage);
	            	 sendMessage= new MessageSender(kafka,batchData,missedData);	  
	            	 thread.start(); 
	            	 */
                	 kafka.runBatch(batchData,missedData);
	            	 batchData = new ConcurrentHashMap<String,String>();
	            	 counter=0;
	            	 
	            }
	            	              
            }                                    
            
         }
         catch(Exception e)
         {
            System.out.println("**** ERROR Found on access number "+accessNumber+" *****");
            e.printStackTrace();
            problemRecordCount++;
         }
         
     
        }
      
        System.out.println("epoch="+processTime+" database="+database+" totalCount="+totalCount);
    	System.out.println("Total record is "+ i);
        if(i!=totalCount)
	    {
	    	System.out.println("**Got "+i+" records instead of "+totalCount+" for week of "+week );
	    }
        
    	System.out.println(problemRecordCount + " of them has parsing problem");
    }
    catch(Exception e)
    {
    	e.printStackTrace();
    }
    finally
    { 
    	if(this.propertyFileName != null && (getAction() != null && !(getAction().equalsIgnoreCase("lookup"))))
    	{
	    	try
	    	{
	    		 kafka.runBatch(batchData,missedData);
	    		 /*
	    		 thread = new Thread(sendMessage);
	        	 sendMessage= new MessageSender(kafka,batchData,missedData);
	        	 thread.start(); 
	        	 */
	    	}
	    	catch(Exception ex) 
	    	{
	    		ex.printStackTrace();
	    	}   
    	}
    	
    	if(kafka!=null)
        {
    		try 
        	{
    			kafka.close(missedData);
        	}
        	catch(Exception ex) 
        	{
        		ex.printStackTrace();
        	}        	       
        }
	}//try
    
}
	private String getAbstract(String abstractData) throws Exception
	{
		String abstractContent=null;
		if(abstractData.indexOf("<div")<0)
		{
			return abstractData;
		}
		abstractData=abstractData.replaceAll("&", "&amp;");		
		abstractData=abstractData.replaceAll("<","&lt;").replaceAll(">", "&gt;");
		abstractData=abstractData.replaceAll("&lt;div","<div").replaceAll("abstract\"&gt;","abstract\">");
		abstractData=abstractData.replaceAll("&lt;/div&gt;","</div>");
		abstractData="<abstract>"+abstractData+"</abstract>";
		//System.out.println("abstractData2="+abstractData);
		InputStream inputStream = new ByteArrayInputStream(abstractData.getBytes());
		SAXBuilder builder = new SAXBuilder();
		builder.setExpandEntities(false);
		builder.setFeature( "http://xml.org/sax/features/namespaces", true );
		Document doc = builder.build(inputStream);
		Element abstractRoot = doc.getRootElement();
		List lt = abstractRoot.getChildren("div");
		boolean gotAbstract = false;
		for (int i = 0; i < lt.size(); i++)
		{
			Element oe = (Element) lt.get(i);
			String abstractLanguage = oe.getAttributeValue("data-language");
			//System.out.println("ELEMENT NAME="+oe.getName());
			//System.out.println("ELEMENT Value="+oe.getTextTrim());
			//System.out.println("ELEMENT LANGUAGE="+abstractLanguage);
			if(!gotAbstract)
			{
				abstractContent=oe.getTextTrim();
				if(abstractLanguage.equalsIgnoreCase("eng"))
				{
					gotAbstract=true;
					continue;
				}
			}
			
		}
		//System.out.println("ABSTRACT CONTENT="+abstractContent);
		return abstractContent;
	}
	
	private String prepareStandardDesignation(String input) {
		String output = null;
		if (input != null) {
			// not sure what to do yet
		}
		return output;
	}

	private List getCafeAffID(String pui, Connection con) {
		Statement stmt = null;
		ResultSet rs = null;
		List<String> affid = new ArrayList<String>();

		try {
			// stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			// ResultSet.CONCUR_READ_ONLY);
			stmt = con.createStatement();
			// System.out.println("Running the Numerical query...");
			String sqlQuery = "select institute_id from CAFE_AF_LOOKUP where PUI='" + pui + "'";
			// System.out.println(sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			// System.out.println("Got records ...from table BD_MASTER_NUMERICAL");
			while (rs.next()) {
				affid.add(rs.getString("institute_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return affid;

	}

	private List getCafeAuthorID(String pui, Connection con) {
		Statement stmt = null;
		ResultSet rs = null;
		List<String> aid = new ArrayList<String>();

		try {

			// stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			// ResultSet.CONCUR_READ_ONLY);
			stmt = con.createStatement();
			// System.out.println("Running the Numerical query...");
			String sqlQuery = "select author_id from CAFE_AU_LOOKUP where PUI='" + pui + "'";
			// System.out.println(sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			// System.out.println("Got records ...from table BD_MASTER_NUMERICAL");
			while (rs.next()) {
				aid.add(rs.getString("author_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return aid;

	}

	private String getStatus(String sourcebibtext) {

		if (sourcebibtext.indexOf("modifier:") > -1) {

			sourcebibtext = sourcebibtext.substring(0, sourcebibtext.indexOf("modifier:"));
			sourcebibtext = sourcebibtext.replaceAll("status:", "");
			sourcebibtext = sourcebibtext.replaceAll(";", "");

		}

		return sourcebibtext.trim();
	}

	private String getGrantID(String grant) {
		StringBuffer grantidBuffer = new StringBuffer();

		if (grant != null) {
			String[] grantA = grant.split(Constants.AUDELIMITER);
			for (int i = 0; i < grantA.length; i++) {
				if (grantA[i] != null) {
					String[] grantS = grantA[i].split(Constants.IDDELIMITER, -1);
					// System.out.println("GrantList "+grantS.length);
					if (grantS.length > 0) {
						String grant0 = grantS[0];
						if (grant0 != null) {
							if (grant0.indexOf(";") > -1) {
								String[] grant01 = grant0.split(";");
								for (int j = 0; j < grant01.length; j++) {
									grantidBuffer.append(grant01[j]);
									grantidBuffer.append(Constants.AUDELIMITER);
								}

							} else {
								grantidBuffer.append(grant0);
								grantidBuffer.append(Constants.AUDELIMITER);
							}
						}
					}

					if (grantS.length > 1) {
						String grant1 = grantS[1];
						if (grant1 != null) {
							if (grant1.indexOf(";") > -1) {
								String[] grant11 = grant1.split(";");
								for (int j = 0; j < grant11.length; j++) {
									grantidBuffer.append(grant11[j]);
									grantidBuffer.append(Constants.AUDELIMITER);
								}

							} else {
								grantidBuffer.append(grant1);
								grantidBuffer.append(Constants.AUDELIMITER);
							}
						}

					}

				}

			}
		}

		return grantidBuffer.toString();
	}

	private String getGrantAgency(String grant) {

		StringBuffer grantAgencyBuffer = new StringBuffer();

		if (grant != null) {
			String[] grantA = grant.split(Constants.AUDELIMITER, -1);
			for (int i = 0; i < grantA.length; i++) {
				if (grantA[i] != null && grantA[i].length() > 0) {
					String[] grantS = grantA[i].split(Constants.IDDELIMITER, -1);
					if (grantS.length > 2) {

						if (grantS[2] != null) {
							grantAgencyBuffer.append(grantS[2]);
							grantAgencyBuffer.append(Constants.AUDELIMITER);
						}

					} else if (grantS.length > 1) {
						System.out.println("GRANT-1 " + grantS[1] + " ** " + grantS[0]);
						System.out.println("PUI=" + puiGlobal + " GrantList has wrong format");
					} else if (grantS.length > 0) {
						System.out.println("GRANT-0 " + grantS[0]);
						System.out.println("PUI=" + puiGlobal + " GrantList has wrong format");
					}
				}

			}
		}

		return grantAgencyBuffer.toString();

	}

	// *
	// ******** use this method for full numericalIndex ***********
	// *
	private void populateNumericalIndex(String pui, EVCombinedRec rec, Connection con) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		BdNumericalIndexMapping bdni = new BdNumericalIndexMapping();
		NumberFormat formatter = new DecimalFormat();
		HashSet<String> unitSet = new HashSet<String>();

		try {
			if (pui != null && pui.length() > 0) {
				// stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				// ResultSet.CONCUR_READ_ONLY);
				stmt = con.createStatement();
				// System.out.println("Running the Numerical query...");
				String sqlQuery = "select * from BD_MASTER_NUMERICAL where PUI='" + pui + "' order by unit";
				// System.out.println(sqlQuery);
				rs = stmt.executeQuery(sqlQuery);
				// System.out.println("Got records ...from table BD_MASTER_NUMERICAL");
				while (rs.next()) {
					String unit = null;
					String maximum = null;
					String minimum = null;
					StringBuffer niBuffer = new StringBuffer();
					StringBuffer rangesBuffer = new StringBuffer();
					if (rs.getString("UNIT") != null) {
						unit = rs.getString("UNIT").toLowerCase();
						//take care of space between numerical index term by hmo at 3/17/2022
						unit = unit.replaceAll(" ", "_");
						unitSet.add(unit);
					} else {
						System.out.println("no unit found for pui " + pui);
						return;
					}

					if (rs.getString("MAXIMUM") != null && rs.getString("MINIMUM") != null) {
						maximum = rs.getString("MAXIMUM");
						minimum = rs.getString("MINIMUM");
						String niKey = bdni.get(unit + "_ranges");
						niBuffer.append(maximum.replace("-", "minus").replace("+", "plus") + Constants.AUDELIMITER);
						niBuffer.append(minimum.replace("-", "minus").replace("+", "plus") + Constants.AUDELIMITER);

						/*
						 * //to test if we need to add/minus a small value at 11/14/2016 by hmo Double
						 * doubleMaximum = Double.parseDouble(maximum); doubleMaximum =
						 * doubleMaximum+doubleMaximum/1000.0; Double doubleMinimum =
						 * Double.parseDouble(minimum); doubleMinimum = doubleMinimum -
						 * doubleMinimum/1000.0; String ranges = doubleMinimum+" "+doubleMaximum;
						 */

						String ranges = minimum + " " + maximum;

						if (niKey != null) {
							String oldRanges = rec.getString(niKey);
							if (oldRanges != null) {
								rec.put(niKey, oldRanges + ranges + " | ");
							} else {
								rec.put(niKey, " | " + ranges + " | ");
							}
						}

						niKey = bdni.get(unit + "_text");
						if (niKey != null) {
							String[] oldText = rec.getStrings(niKey);
							if (oldText != null && oldText.length > 0) {
								String[] temp = prepareMulti(niBuffer.toString());
								rec.put(niKey, uniqueConcat(oldText, temp));

							} else {
								rec.put(niKey, prepareMulti(niBuffer.toString()));

							}

						} else {
							//System.out.println("no mapping found for " + unit + "_text"); // HH 12/23/2021 commented to make log file readable
						}

					}

				}
				rec.put(EVCombinedRec.NUMERICALUNITS, unitSet.toArray(new String[unitSet.size()]));

			}

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// */
	public String[] concat(String[] a, String[] b) {
		int aLen = a.length;
		int bLen = b.length;
		String[] c = new String[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

	public String[] uniqueConcat(String[] a, String[] b) {
		HashSet<String> tSet = new HashSet<String>();
		for (int i = 0; i < a.length; i++) {
			tSet.add(a[i]);
		}
		for (int i = 0; i < b.length; i++) {
			tSet.add(b[i]);
		}

		return tSet.toArray(new String[tSet.size()]);
	}

	/*
	 * private String formatStandardCodes(String c) { String r = ""; if (c == null)
	 * { return null; } else { r = c.replaceAll("\\.","DQD"); //use DQD for dot(.) r
	 * = r.replaceAll("-","QMINUSQ"); //use QMINUSQ for minus sign r =
	 * r.replaceAll("\\/","QSLASHQ"); //use QSLASHQ for slash(/) r =
	 * r.replaceAll(" ","QSPACEQ"); //use QSPACEQ for space r =
	 * r.replaceAll(",","QCOMMAQ"); //use QCOMMAQ for comma r =
	 * r.replaceAll(":","QCOLONQ"); //use QCOLONQ for colon r =
	 * r.replaceAll(";","QSEMICOLONQ"); //use QSEMICOLONQ for semicolon r =
	 * r.replaceAll("\\(","QOPENINGBRACKETQ"); //opening parenthesis r =
	 * r.replaceAll("\\)","QCLOSINGBRACKETQ"); //closing parenthesis }
	 * 
	 * return r;
	 * 
	 * }
	 */

	private String formatClassCodes(String c) {
		if (c == null) {
			return null;
		}

		return c.replaceAll("\\.", "DQD");

	}

	private void setAuID(List auid) {
		this.auid = auid;
	}

	public String[] getAuID() {
		// System.out.println("ORIGINAL= "+this.auid.toString());
		HashSet uniqueAuid = new HashSet(this.auid);
		// System.out.println("UNIQUE= "+uniqueAuid.toString());
		return (String[]) uniqueAuid.toArray(new String[0]);
	}

	public String[] prepareBdAuthor(String bdAuthor) {
		if (bdAuthor != null && !bdAuthor.trim().equals("")) {
			BdAuthors aus = new BdAuthors(bdAuthor);
			String[] ausArray = aus.getSearchValue();
			setAuID(aus.getAuID());
			return ausArray;
		}
		return null;
	}

	public String[] prepareBdAuthorWithAlias(String bdAuthor) {
		if (bdAuthor != null && !bdAuthor.trim().equals("")) {
			BdAuthors aus = new BdAuthors(bdAuthor);
			String[] ausArray = aus.getSearchValueWithAlias();
			// setAuID(aus.getAuID());
			return ausArray;
		}
		return null;
	}

	private String prepareConfLocation(String coAff) {
		if (coAff != null && !coAff.trim().equals("")) {
			BdConfLocations aff = new BdConfLocations(coAff);
			return aff.getSearchValue();
		}
		return null;

	}

	private String[] prepareISBN(String isbnString) throws Exception {
		List list = new ArrayList();
		if (isbnString != null) {
			BdIsbn isbn = new BdIsbn(isbnString);
			List isbnList = isbn.getISBN();
			for (int i = 0; i < isbnList.size(); i++) {
				BdIsbn isbnObject = (BdIsbn) isbnList.get(i);

				if (isbnObject.getValue() != null) {
					list.add(isbnObject.getValue());
				}
			}
		}

		return (String[]) list.toArray(new String[1]);
	}
	
	public String getFirstAuthor(String bdAuthor) throws Exception
	{
		String firstAuthor=null;
		if (bdAuthor != null && !bdAuthor.trim().equals("")) {
			BdAuthors aus = new BdAuthors(bdAuthor);
			firstAuthor = aus.getFirstAuthor();		
		}		
		return firstAuthor;
	}

	private String[] prepareCitationTitle(String citationTitle) throws Exception {
		List list = new ArrayList();
		if (citationTitle != null) {
			BdCitationTitle ct = new BdCitationTitle(citationTitle);
			List ctList = ct.getCitationTitle();

			for (int i = 0; i < ctList.size(); i++) {
				BdCitationTitle ctObject = (BdCitationTitle) ctList.get(i);

				if (ctObject.getTitle() != null) {
					list.add(ctObject.getTitle());
				}

			}
		}

		return (String[]) list.toArray(new String[1]);
	}

	private String[] prepareTranslatedCitationTitle(String citationTitle) throws Exception {
		List list = new ArrayList();
		if (citationTitle != null) {
			BdCitationTitle ct = new BdCitationTitle(citationTitle);
			List ctList = ct.getTranslatedCitationTitle();

			for (int i = 0; i < ctList.size(); i++) {
				BdCitationTitle ctObject = (BdCitationTitle) ctList.get(i);

				if (ctObject.getTitle() != null) {
					list.add(ctObject.getTitle());
				}

			}
		}
		return (String[]) list.toArray(new String[1]);
	}

	private String[] prepareEditor(String bdEditor) {
		if (bdEditor != null && !bdEditor.trim().equals("")) {
			BdEditors ed = new BdEditors(bdEditor);
			return ed.getSearchValue();
		}
		return null;
	}

	public String[] prepareMulti(String multiString) throws Exception {
		String[] multiStringArray = multiString.split(Constants.AUDELIMITER, -1);
		return multiStringArray;

	}

	private String[] prepareCASRegistry(String multiString, String database) throws Exception {
		ArrayList list = new ArrayList();
		String[] multiStringArray = multiString.split(Constants.IDDELIMITER, -1);
		if (multiString.length() > 0 && database.equalsIgnoreCase("elt")) {
			for (int i = 0; i < multiStringArray.length; i++) {
				String[] multiStringArray2 = multiStringArray[i].split(Constants.AUDELIMITER, -1);
				if (multiStringArray2.length == 3) {
					if (multiStringArray2[2].indexOf(multiStringArray2[1]) == -1) {
						if (multiStringArray2[0] != null && multiStringArray2[0].equalsIgnoreCase("b")) {
							list.add(stripAsterics(multiStringArray2[2]).concat("-").concat("(BT)").concat("-")
									.concat(stripAsterics(multiStringArray2[1])));
						} else {
							list.add(stripAsterics(multiStringArray2[2]).concat("-")
									.concat(stripAsterics(multiStringArray2[1])));
						}
					} else {
						if (multiStringArray2[0] != null && multiStringArray2[0].equalsIgnoreCase("b")) {
							list.add(stripAsterics(multiStringArray2[2]).concat("-").concat("(BT)"));
						} else {
							list.add(stripAsterics(multiStringArray2[2]));
						}
					}
				}
			}
		} else {
			for (int i = 0; i < multiStringArray.length; i++) {
				String[] multiStringArray2 = multiStringArray[i].split(Constants.GROUPDELIMITER, -1);
				for (int j = 0; j < multiStringArray2.length; j++) {
					list.add(stripAsterics(multiStringArray2[j]));
				}
			}
		}

		return (String[]) list.toArray(new String[0]);
	}

	private String[] prepareLanguage(String multiString) throws Exception {
		String[] languages = multiString.split(Constants.IDDELIMITER);
		for (int i = 0; i < languages.length; i++) {
			languages[i] = Language.getIso639Language(languages[i]);
		}
		return languages;
	}

	private String prepareDateSort(String datesort, String publicationyear) throws Exception {
		String ds = null;

		if (datesort != null) {
			String[] dt = datesort.split(Constants.IDDELIMITER);
			if (dt.length == 3) {
				ds = dt[0] + dt[1] + dt[2];
			} else if (dt.length == 2) {
				ds = dt[0] + dt[1] + "01";
			} else {
				ds = dt[0] + "0101";
			}

		} else {
			if (publicationyear.length() > 3) {
				publicationyear = publicationyear.substring(0, 4);
			}
			ds = publicationyear + "0101";
		}

		return ds;
	}

	public String preparePublisherName(String pn) throws Exception {
		String[] pnames = pn.split(Constants.IDDELIMITER);
		String[] pnames2 = pnames[0].split(Constants.AUDELIMITER);
		return pnames2[0];
	}

	private String getFirstNumber(String v) {

		MatchResult mResult = null;
		if (v == null) {
			return null;
		}

		if (perl.match("/[1-9][0-9]*/", v)) {
			mResult = perl.getMatch();
		} else {
			return null;
		}

		return mResult.toString();
	}

	public String getPage(String xp, String ar) {
		String strPage = null;

		if (ar != null) {
			strPage = ar;
		} else {
			strPage = xp;
		}

		return strPage;
	}

	private String getFirstPage(String v) {
		BdPage pages = new BdPage(v);
		return pages.getStartPage();
	}

	public String getDedupKey(String issn, String coden, String volume, String issue, String page) throws Exception {
		String firstVolume = getFirstNumber(volume);
		String firstIssue = getFirstNumber(issue);
		String firstPage = getFirstPage(page);

		if ((issn == null && coden == null) || firstVolume == null || firstIssue == null || firstPage == null) {
			return (new GUID()).toString();
		}

		StringBuffer buf = new StringBuffer();

		if (issn != null) {
			buf.append(perl.substitute("s/-//g", issn));
		} else {
			buf.append(BdCoden.convert(coden));
		}

		buf.append("vol" + firstVolume);
		buf.append("is" + firstIssue);
		buf.append("pa" + firstPage);

		return buf.toString().toLowerCase();

	}

	private String getTreatmentCode(String tc) {
		StringBuffer tbuff = new StringBuffer();

		for (int i = 0; i < tc.length(); ++i) {
			char c = tc.charAt(i);

			if (c == 'A') {
				tbuff.append("APP");
			} else if (c == 'B') {
				tbuff.append("BIO");
			} else if (c == 'E') {
				tbuff.append("ECO");
			} else if (c == 'X') {
				tbuff.append("EXP");
			} else if (c == 'G') {
				tbuff.append("GEN");
			} else if (c == 'H') {
				tbuff.append("HIS");
			} else if (c == 'L') {
				tbuff.append("LIT");
			} else if (c == 'M') {
				tbuff.append("MAN");
			} else if (c == 'N') {
				tbuff.append("NUM");
			} else if (c == 'T') {
				tbuff.append("THR");
			} else if (c < 32) {
				tbuff.append(c);
			} else {
				tbuff.append("NA");
			}
		}

		return tbuff.toString();
	}

	private String getCitationType(String ct, boolean flag) {
		return BdDocumentType.getDocType(ct, flag);
	}

	private boolean validYear(String year) {
		if (year == null) {
			return false;
		}

		if (year.length() > 3) {
			year = year.substring(0, 4);
		} else {
			return false;

		}

		return perl.match("/[1-9][0-9][0-9][0-9]/", year);
	}

	private String formatConferenceLoc(String ms, String mc, String mv, String my) {
		StringBuffer b = new StringBuffer(" ");
		if (ms != null) {
			b.append(ms + ", ");
		}

		if (mc != null) {
			b.append(mc + ", ");
		}

		if (mv != null) {
			b.append(mv + ", ");
		}

		if (my != null) {
			b.append(my);
		}

		return b.toString();

	}

	private String getStringFromClob(Clob clob) throws Exception {
		String temp = null;
		if (clob != null) {
			temp = clob.getSubString(1, (int) clob.length());
		}

		return temp;
	}

	private String[] parseCoordinates(String cs) throws Exception {
		cs = cs.replaceAll("[^a-zA-Z0-9]", "");
		String coordString = cs.trim().replaceAll("([NEWS])", "-$1");

		String[] coords = coordString.split("-");
		for (int i = 1; i < 5; i++) {
			if (coords[i].length() < 7) {
				int padCount = 8 - coords[i].length();
				for (int p = 0; p < padCount; p++)
					coords[i] += "0";
			}

			coords[i] = coords[i].replaceAll("[NE]", "+").substring(0, coords[i].length() - 4).replaceAll("\\+", "");
			coords[i] = coords[i].replaceAll("[WS]", "-");

			if (coords[i].substring(0, 1).indexOf("-") != -1)
				coords[i] = coords[i].replaceAll("^(-)0{1,2}(.*?)", "$1$2");
			else
				coords[i] = coords[i].replaceAll("^0{1,2}(.*?)", "$1");

			int m = Integer.parseInt(coords[i]);
			if (i < 3) {
				if (m > 90)
					coords[i] = coords[i].substring(0, 2);
				if (m < -90)
					coords[i] = coords[i].substring(0, 3);
			} else {
				if (m > 180)
					coords[i] = coords[i].substring(0, 2);
				if (m < -180)
					coords[i] = coords[i].substring(0, 3);
			}
		}

		return coords;
	}

	public void writeCombinedByTimestampHook(Connection con, long timestamp) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;

		try {

			// stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
			// ResultSet.CONCUR_READ_ONLY);
			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"select UPDATENUMBER,CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER,PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,CLASSIFICATIONDESC,APIAMS,SEQ_NUM,CSXTERM, CONFSERIESID, CONFSERIESTITLE, CONFSERIESACRONYM, SOURCECOUNTRY, count(*) over() totalCount from "
							+ Combiner.TABLENAME + " where loadnumber != 0 and loadnumber < 100000000 and database='"
							+ Combiner.CURRENTDB + "'");
			writeRecs(rs, con, (int) timestamp, Combiner.TABLENAME, Combiner.CURRENTDB);
			this.writer.end();
			this.writer.flush();

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public String[] prepareELTCV(String multiString) throws Exception {
		if (multiString != null && multiString.length() > 0) {
			multiString = stripAsterics(multiString);

			String[] multiStringArray = null;
			if (multiString.indexOf(";") > 0) {
				multiStringArray = multiString.split(";", -1);
				return multiStringArray;
			} else {
				String[] multiStringArray1 = { multiString };
				return multiStringArray1;
			}
		}

		return null;

	}

	public String replaceNull(String sVal) {

		if (sVal == null)
			sVal = "";

		return sVal;
	}

	public String[] getCvs(List list) {

		if (list != null && list.size() > 0) {
			return (String[]) list.toArray(new String[0]);

		}
		return null;
	}

	private String stripAsterics(String line) {
		line = perl.substitute("s/\\*+//gi", line);
		return line;
	}

	public void writeCombinedByWeekHook(Connection con, int weekNumber) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;

		try {

			stmt = con.createStatement();

			// change by hmo at 4/6/2017 for author/affiliation project
			// String sqlQuery = "select
			// CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER,
			// substr(PUBLICATIONYEAR,1,4) as
			// PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME,CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER,CONFERENCEPAGERANGE,CONFERENCEPAGECOUNT,CONFERENCEEDITOR,CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM,GRANTLIST,null
			// as cafe_author,null as cafe_author1,null as cafe_affiliation, null as
			// cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as
			// authorid,null as
			// affid,SOURCEBIBTEXT,STANDARDID,STANDARDDESIGNATION,NORMSTANDARDID,GRANTTEXT
			// from " + Combiner.TABLENAME +" where database='" + Combiner.CURRENTDB + "'";
			// String sqlQuery = "select UPDATENUMBER,CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME,CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER,CONFERENCEPAGERANGE,CONFERENCEPAGECOUNT,CONFERENCEEDITOR,CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM,GRANTLIST,null as cafe_author,null as cafe_author1,null as cafe_affiliation, null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,SOURCEBIBTEXT,STANDARDID,STANDARDDESIGNATION,NORMSTANDARDID,GRANTTEXT,ISOPENACESS,eid,CSXTERM, count(*) over() totalCount from "
			String sqlQuery = "select UPDATENUMBER,CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME,CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER,CONFERENCEPAGERANGE,CONFERENCEPAGECOUNT,CONFERENCEEDITOR,CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM,GRANTLIST,null as cafe_author,null as cafe_author1,null as cafe_affiliation, null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,SOURCEBIBTEXT,STANDARDID,STANDARDDESIGNATION,NORMSTANDARDID,GRANTTEXT,ISOPENACESS,eid,CSXTERM, CONFSERIESID, CONFSERIESTITLE, CONFSERIESACRONYM, SOURCECOUNTRY from "
					+ Combiner.TABLENAME + " where LOADNUMBER='" + weekNumber + "' AND loadnumber != 0 and database='"
					+ Combiner.CURRENTDB + "'";

			// USING PUISECONDARY to join table 04/03/2019
			String sqlCpxQuery = "select a.UPDATENUMBER,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.EID,a.ISOPENACESS,a.CSXTERM,a.CONFSERIESID, a.CONFSERIESTITLE, a.CONFSERIESACRONYM,a.SOURCECOUNTRY  from "
					+ Combiner.TABLENAME
					+ " a left outer join db_cafe.CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join db_cafe.cafe_master b on b.pui=c.pui where a.LOADNUMBER="
					+ weekNumber + " AND a.database='cpx' and a.loadnumber != 0";
			
			System.out.println("DATABASE=" + Combiner.CURRENTDB);
			if ((Combiner.CURRENTDB).equalsIgnoreCase("cpx")) {
				System.out.println(sqlCpxQuery);
				rs = stmt.executeQuery(sqlCpxQuery);
			} else if ((Combiner.CURRENTDB).equalsIgnoreCase("chm") || ((Combiner.CURRENTDB).equalsIgnoreCase("pch"))
					|| ((Combiner.CURRENTDB).equalsIgnoreCase("geo"))
					|| ((Combiner.CURRENTDB).equalsIgnoreCase("elt"))) {
				System.out.println(sqlQuery);
				rs = stmt.executeQuery(sqlQuery);
			} else {
				System.out.println("database not found");
				System.exit(1);
			}

			writeRecs(rs, con, weekNumber, "extractByLoadnumber--" + Combiner.TABLENAME, Combiner.CURRENTDB);
			//this.writer.end();
			this.writer.flush();

		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	@Override
	/* HT added 09/21/2020 wk: [202040] for Lookup extraction for ES */
	public void writeLookupByWeekHook(int weekNumber) throws Exception {
		System.out.println("Extract Lookup");
		String database = Combiner.CURRENTDB;
		lookupObj = new LookupEntry(database, weekNumber);
		lookupObj.init();
	}


	/*HT added 09/21/2020, Get Lookup list from temp tables based on correction action, this method to support bdCorrectio, not for ES lookup extraction*/
	  public Map<String,List<String>> getESLookupData(int weekNumber, String actionType, String[] tableNames, Connection sqlcon, String database) throws Exception {
	  Statement stmt = null; 
	  ResultSet rs = null; 
	  String sqlQuery = null, cpxSqlQuery = null;
	  Map<String,List<String>> recs = null;
	  
	  try { 
			
	  con = sqlcon;
	  stmt = con.createStatement();
	  System.out.println("database: " + database);
	  
	  String tableName = null;
	  Combiner.CURRENTDB = database;
	  
		if(actionType.equalsIgnoreCase("update")||actionType.equalsIgnoreCase("aip"))
        {
            tableName = tableNames[0];
        }
        else if(actionType.equalsIgnoreCase("lookupIndex") && weekNumber != 0 && database != null)
        {
            tableName = tableNames[1];
        }
        else
        {
            tableName = tableNames[2];
        }
  
	  if(database.equals("cpx"))          
      {
		  if(actionType.equalsIgnoreCase("lookupIndex"))
			  cpxSqlQuery =
        			  "select a.UPDATENUMBER,a.CHEMICALTERM,a.DATABASE,a.PUI,a.accessnumber,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CONTROLLEDTERM,a.SOURCETITLE,a.PUBLISHERNAME,b.author as cafe_author,"
        			  +
        			  "b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,"
        			  +
        			  "a.REGIONALTERM,a.SOURCETITLEABBREV,a.UPDATENUMBER,a.m_id,a.apict,a.apict1,a.PUBLICATIONYEAR,a.LOADNUMBER,a.CORRESPONDENCEAFFILIATION, a.CSXTERM, a.SOURCECOUNTRY, count(*) over() totalCount from "
        			  + tableName +
        			  " a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.LOADNUMBER="
        			  + weekNumber + " AND a.database='cpx' and a.loadnumber != 0";
		  else
			  
			  cpxSqlQuery =
			  "select a.UPDATENUMBER,a.CHEMICALTERM,a.DATABASE,a.PUI,a.accessnumber,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CONTROLLEDTERM,a.SOURCETITLE,a.PUBLISHERNAME,b.author as cafe_author,"
			  +
			  "b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,"
			  +
			  "a.REGIONALTERM,a.SOURCETITLEABBREV,a.UPDATENUMBER,a.m_id,a.apict,a.apict1,a.PUBLICATIONYEAR,a.LOADNUMBER,a.CORRESPONDENCEAFFILIATION, a.CSXTERM,a.SOURCECOUNTRY, count(*) over() totalCount from "
			  + tableName + " a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and a.loadnumber != 0";
          }
	  else
	  {
		  if(actionType.equalsIgnoreCase("lookupIndex"))
			  sqlQuery =
				  "select ACCESSNUMBER,PUI,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE,PUI,REGIONALTERM,SOURCETITLEABBREV,UPDATENUMBER, null as affid, null as cafe_affiliation,"
				  + "null as cafe_author, null as cafe_author1, m_id, apict, apict1, PUBLICATIONYEAR, LOADNUMBER,CORRESPONDENCEAFFILIATION,null as CAFE_CORRESPONDENCEAFFILIATION,CSXTERM,SOURCECOUNTRY FROM "
				  + tableName + " where LOADNUMBER='" + weekNumber +
				  "' AND loadnumber != 0 and database='" + database + "'";
		  else
			  sqlQuery =
			  "select ACCESSNUMBER,PUI,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE,PUI,REGIONALTERM,SOURCETITLEABBREV,UPDATENUMBER,null as affid, null as cafe_affiliation, "
			  + "null as cafe_author, null as cafe_author1, m_id, apict, apict1, PUBLICATIONYEAR, LOADNUMBER,CORRESPONDENCEAFFILIATION,null as CAFE_CORRESPONDENCEAFFILIATION,CSXTERM,SOURCECOUNTRY FROM "
			  + tableName + " where loadnumber != 0 and database='" + database + "'"; 
	  }
	  
			System.out.println("DATABASE=" + Combiner.CURRENTDB);
			if (database.equalsIgnoreCase("cpx")) 
			{
				System.out.println(cpxSqlQuery);
				rs = stmt.executeQuery(cpxSqlQuery);
			} 
			else if (database.equalsIgnoreCase("chm") || (database.equalsIgnoreCase("pch"))
					|| (database.equalsIgnoreCase("geo")) || (database.equalsIgnoreCase("elt"))) 
			{
				System.out.println(sqlQuery);
				rs = stmt.executeQuery(sqlQuery);
			}

			else {
				System.out.println("No loadnumber or database provided, exit!!!");
				System.exit(1);
			}

			 recs = prepareLookupRecs(rs, weekNumber);
		} 
	  finally 
	  {

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/*
			 * if (con != null) { try { con.close(); } catch (Exception e) {
			 * e.printStackTrace(); } }
			 */
			 
		}
	  return recs;
	  
	  }
	  
	  //HT added 09/21/2020 for ES lookup 
	public Map<String,List<String>> prepareLookupRecs(ResultSet rs, int weekNumber) throws Exception {
		int i = 0;
		EVCombinedRec[] recArray = null;
		boolean isChimica = false;
		boolean isCpx = false;
		boolean isGeo = false;  //added to block CV term for geo database by hmo at 5/18/2022
		boolean isElt = false;  //added to block CV term for elt database by hmo at 5/18/2022
		String accessNumber = "";
		String pui = "";

		 Map<String,List<String>> recs = new HashMap<>();
	        List<String> authorList = new ArrayList<>();
	        List<String> affiliationList = new ArrayList<>();
	        List<String> serialTitleList = new ArrayList<>();
	        List<String> controltermList = new ArrayList<>();
	        List<String> publishernameList = new ArrayList<>();
	        List<String> ipcList = new ArrayList<>();
	        List<String> icsList = new ArrayList<>(); //added by hmo at 04/18/2023
	        
		try {

			while (rs.next()) {

				String firstGUID = "";

				if (rs.getString("DATABASE") != null) {
					if (rs.getString("DATABASE").equals("chm")) {
						isChimica = true;
					}
					if (rs.getString("DATABASE").equals("cpx")) {
						isCpx = true;
					}
					//added to block CV term for geo database by hmo at 5/18/2022
					if (rs.getString("DATABASE").equals("geo")) {
						isGeo = true;
					}
					
					//added to block CV term for elt database by hmo at 5/18/2022
					if (rs.getString("DATABASE").equals("elt")) {
						isElt = true;
					}
				}

				Vector<EVCombinedRec> recVector = new Vector<>();
				try {

					EVCombinedRec rec = new EVCombinedRec();
					pui = rs.getString("PUI");
					puiGlobal = pui;

					if (validYear(rs.getString("PUBLICATIONYEAR"))) {
						++i;

						if (isCpx && rs.getString("authorid") != null) {
							String authorid = rs.getString("authorid");
							String[] aid = authorid.split(",");
							rec.put(EVCombinedRec.AUTHORID, aid);
						}

						if (isCpx && rs.getString("affid") != null) {
							String affid = rs.getString("affid");
							String[] affids = affid.split(",");
							rec.put(EVCombinedRec.AFFILIATIONID, affids);
						}

						// move this block of code to here to include Affiliation ID while BD
						// affiliation is null
						if (rs.getString("affid") == null && rs.getString("CAFE_AFFILIATION") != null) {
							String cafeAffString = rs.getString("CAFE_AFFILIATION");
							if (rs.getString("CAFE_AFFILIATION1") != null) {
								cafeAffString = cafeAffString + rs.getString("CAFE_AFFILIATION1");
							}

							BdAffiliations caff = new BdAffiliations(cafeAffString);
							caff.getSearchValue();

							rec.put(EVCombinedRec.AFFILIATIONID, caff.getAffiliationId());
							rec.put(EVCombinedRec.DEPARTMENTID, caff.getDepartmentId());

						}
						// end of the block

						if (rs.getString("AUTHOR") != null) {
							String authorString = rs.getString("AUTHOR");
							if (rs.getString("AUTHOR_1") != null) {
								authorString = authorString + rs.getString("AUTHOR_1");
							}

							rec.put(EVCombinedRec.AUTHOR, prepareBdAuthor(authorString));
							rec.put(EVCombinedRec.FIRST_AUTHOR, getFirstAuthor(authorString));
							
							// use this to deal with author alias
							rec.put(EVCombinedRec.TRANSLATOR, prepareBdAuthorWithAlias(authorString));

							// if(rs.getString("authorid")==null && rs.getString("cafe_author")!=null)
							if (rs.getString("cafe_author") != null) {
								String cafeAuthorString = rs.getString("cafe_author");
								if (rs.getString("cafe_author1") != null) {
									cafeAuthorString = cafeAuthorString + rs.getString("cafe_author1");
								}
								String[] cafeAuthor = prepareBdAuthor(cafeAuthorString);
								String[] cafe_aid = getAuID();
								rec.put(EVCombinedRec.AUTHORID, cafe_aid);
							}

							String affiliation = null;

							if (rs.getString("AFFILIATION") != null) {
								affiliation = rs.getString("AFFILIATION");
								if (rs.getString("AFFILIATION_1") != null) {
									affiliation = affiliation + rs.getString("AFFILIATION_1");
								}

								BdAffiliations aff = new BdAffiliations(affiliation);
								rec.put(EVCombinedRec.AUTHOR_AFFILIATION, aff.getSearchValue());
								rec.put(EVCombinedRec.AFFILIATION_LOCATION, aff.getLocationsSearchValue());
								rec.put(EVCombinedRec.COUNTRY, aff.getCountriesSearchValue());

							} else if (rs.getString("AFFILIATION_1") != null) {
								affiliation = rs.getString("AFFILIATION_1");
								BdAffiliations aff = new BdAffiliations(affiliation);
								rec.put(EVCombinedRec.AUTHOR_AFFILIATION, aff.getSearchValue());
								rec.put(EVCombinedRec.AFFILIATION_LOCATION, aff.getLocationsSearchValue());
								rec.put(EVCombinedRec.COUNTRY, aff.getCountriesSearchValue());
								rec.put(EVCombinedRec.AFFILIATIONID, aff.getAffiliationId());
								rec.put(EVCombinedRec.DEPARTMENTID, aff.getDepartmentId());
								if (rs.getString("affid") == null && rs.getString("CAFE_AFFILIATION1") != null) {
									String cafeAffString = rs.getString("CAFE_AFFILIATION1");
									BdAffiliations caff = new BdAffiliations(cafeAffString);
									caff.getSearchValue();
									rec.put(EVCombinedRec.AFFILIATIONID, caff.getAffiliationId());
									rec.put(EVCombinedRec.DEPARTMENTID, caff.getDepartmentId());
								}

							} else if (rs.getString("CORRESPONDENCEAFFILIATION") != null) {
								affiliation = rs.getString("CORRESPONDENCEAFFILIATION");
								BdCorrespAffiliations aff = new BdCorrespAffiliations(affiliation);
								rec.put(EVCombinedRec.AUTHOR_AFFILIATION, aff.getSearchValue());
								rec.put(EVCombinedRec.AFFILIATION_LOCATION, aff.getLocationsSearchValue());
								rec.put(EVCombinedRec.COUNTRY, aff.getCountriesSearchValue());
								rec.put(EVCombinedRec.AFFILIATIONID, aff.getAffiliationId());
								rec.put(EVCombinedRec.DEPARTMENTID, aff.getDepartmentId());
								if (rs.getString("CAFE_CORRESPONDENCEAFFILIATION") != null) {
									String cafeAffString = rs.getString("CAFE_CORRESPONDENCEAFFILIATION");
									BdAffiliations caff = new BdAffiliations(cafeAffString);
									caff.getSearchValue();
									rec.put(EVCombinedRec.AFFILIATIONID, caff.getAffiliationId());
									rec.put(EVCombinedRec.DEPARTMENTID, caff.getDepartmentId());
								}
							}
						}

						if (!isChimica) {
							if(!isCpx && !isGeo && !isElt) {  //added to block CV term for geo, elt and cpx database by hmo at 5/18/2022
								if (rs.getString("CONTROLLEDTERM") != null) {
									rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("CONTROLLEDTERM")));
								}
							}
						} else {
							if (rs.getString("CHEMICALTERM") != null) {
								rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("CHEMICALTERM")));
							}
						}

						String st = rs.getString("SOURCETITLE");
						String sta = rs.getString("SOURCETITLEABBREV");
						if (st == null) {
							st = sta;
						}

						if (st != null) {
							rec.put(EVCombinedRec.SERIAL_TITLE, st);
						}
						
						//added by hmo at 8/9/2023 for EVOPS-1497
						if (rs.getString("SOURCECOUNTRY") != null) {
							rec.put(EVCombinedRec.SOURCE_COUNTRY, CountryFormatter.formatCountry(rs.getString("SOURCECOUNTRY")));
						}

						if (rs.getString("PUBLISHERNAME") != null) {
							rec.put(EVCombinedRec.PUBLISHER_NAME, preparePublisherName(rs.getString("PUBLISHERNAME")));
						}

						rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));

						rec.put(EVCombinedRec.DATABASE, rs.getString("DATABASE"));

						// only output the first six digits of the loadnumber by hmo@8/23/2017
						if (rs.getString("LOADNUMBER").length() >= 6) {
							rec.put(EVCombinedRec.LOAD_NUMBER, (rs.getString("LOADNUMBER")).substring(0, 6));
						} else {
							rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOADNUMBER"));
						}

						if (rs.getString("UPDATENUMBER") != null) {
							rec.put(EVCombinedRec.UPDATE_NUMBER, rs.getString("UPDATENUMBER"));
						}

						if (rs.getString("PUBLICATIONYEAR") != null && rs.getString("PUBLICATIONYEAR").length() > 3) {
							rec.put(EVCombinedRec.PUB_YEAR, rs.getString("PUBLICATIONYEAR").substring(0, 4));
							// System.out.println("YEAR="+rs.getString("PUBLICATIONYEAR").substring(0,4));
						}

						rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("ACCESSNUMBER"));

						if (rs.getString("PUI") != null) {
							pui = rs.getString("PUI");
							rec.put(EVCombinedRec.PUI, rs.getString("PUI"));
						}

						firstGUID = rs.getString("M_ID");

						rec.putIfNotNull(EVCombinedRec.DOCID, firstGUID);

						CVTerms cvterms = null;
						String apict = replaceNull(rs.getString("apict"));
						String apict1 = replaceNull(rs.getString("apict1"));

						if (apict != null && !apict.trim().equals("")) {
							if (apict1 != null && !apict1.trim().equals("")) {
								apict = apict.concat(apict1);
							}
							cvterms = new CVTerms(apict);
							cvterms.parse();

						}

						if (cvterms != null) {

							String[] cvt = getCvs(cvterms.getCvt());

							if (cvt != null) {

								rec.put(EVCombinedRec.CONTROLLED_TERMS, cvt);
							}
						}
						
						//added by hmo at 04/18/2023 for EVOPS_1386
						if (rs.getString("CSXTERM") != null) {
							rec.put(EVCombinedRec.CSX_TERMS, prepareMulti(rs.getString("CSXTERM")));						
						}
						
						recVector.add(rec);
						recArray = (EVCombinedRec[]) recVector.toArray(new EVCombinedRec[0]);

						this.lookupObj.setLookupRecs(recArray, authorList, affiliationList, serialTitleList, controltermList, publishernameList, ipcList);
					}

				} catch (Exception e) {
					System.out.println("**** ERROR Found on preparinglookuprec for access number " + accessNumber + " *****");
					System.out.println(e.getMessage());
					e.printStackTrace();
				}

			}
			
			recs.put("AUTHOR",authorList);
            recs.put("AFFILIATION",affiliationList);
            recs.put("CONTROLLEDTERM",controltermList);
            recs.put("PUBLISHERNAME",publishernameList);
            recs.put("SERIALTITLE",serialTitleList);
            recs.put("INTERNATONALPATENTCLASSIFICATION",ipcList);
            recs.put("INTERNATIONALCLASSIFICATIONFORSTANDARD",icsList); //added by hmo at 4/18/2023
            
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recs;

	}
	 

}

