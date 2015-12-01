package org.ei.dataloading.bd.loadtime;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
//import org.ei.dataloading.QualifierFacet;
//import org.ei.dataloading.CombinerTimestamp;
//import org.ei.dataloading.CombinedWriter;
//import org.ei.dataloading.EVCombinedRec;
//import org.ei.dataloading.Combiner;
//import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.*;
import org.ei.dataloading.georef.*;
import org.ei.dataloading.georef.loadtime.*;
import org.ei.common.bd.*;
import org.ei.common.*;
import org.ei.util.GUID;
import org.ei.util.StringUtil;

import java.text.*;
//import java.math.*;

public class XmlCombiner
    extends CombinerTimestamp
{

    public String[] EVCombinedRecKeys = {EVCombinedRec.AUTHOR, EVCombinedRec.AUTHOR_AFFILIATION, EVCombinedRec.AFFILIATION_LOCATION, EVCombinedRec.COUNTRY, EVCombinedRec.EDITOR, EVCombinedRec.TITLE, EVCombinedRec.TRANSLATED_TITLE, EVCombinedRec.VOLUME_TITLE, EVCombinedRec.ABSTRACT, EVCombinedRec.CONTROLLED_TERMS, EVCombinedRec.UNCONTROLLED_TERMS, EVCombinedRec.CHEMICALTERMS, EVCombinedRec.INT_PATENT_CLASSIFICATION, EVCombinedRec.ISSN, EVCombinedRec.CODEN, EVCombinedRec.ISBN, EVCombinedRec.SERIAL_TITLE, EVCombinedRec.MAIN_HEADING, EVCombinedRec.PUBLISHER_NAME, EVCombinedRec.TREATMENT_CODE, EVCombinedRec.LANGUAGE, EVCombinedRec.DOCTYPE, EVCombinedRec.CLASSIFICATION_CODE, EVCombinedRec.CONFERENCE_CODE, EVCombinedRec.CONFERENCE_NAME, EVCombinedRec.CONFERENCE_LOCATION, EVCombinedRec.MEETING_DATE, EVCombinedRec.SPONSOR_NAME, EVCombinedRec.CONFERENCEAFFILIATIONS, EVCombinedRec.CONFERENCEEDITORS, EVCombinedRec.CONFERENCEPARTNUMBER, EVCombinedRec.CONFERENCEPAGERANGE, EVCombinedRec.CONFERENCENUMBERPAGES, EVCombinedRec.MONOGRAPH_TITLE, EVCombinedRec.DATABASE, EVCombinedRec.LOAD_NUMBER, EVCombinedRec.PUB_YEAR, EVCombinedRec.DEDUPKEY, EVCombinedRec.VOLUME, EVCombinedRec.ISSUE, EVCombinedRec.STARTPAGE, EVCombinedRec.ACCESSION_NUMBER, EVCombinedRec.REPORTNUMBER, EVCombinedRec.DOI, EVCombinedRec.COPYRIGHT, EVCombinedRec.PII, EVCombinedRec.PUI, EVCombinedRec.COMPANIES, EVCombinedRec.CASREGISTRYNUMBER, EVCombinedRec.PUB_SORT};

    Perl5Util perl = new Perl5Util();

    private static String tablename;

    private static String currentDb;

    private static HashMap issnARFix = new HashMap();
    private List auid;

    static
    {  //ISSNs with AR field problem
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

    public static void main(String args[])
        throws Exception
    {
        // url=jdbc:oracle:thin:@jupiter.elsevier.com:1521:EIDB1
        // driver = oracle.jdbc.driver.OracleDriver
        // username = ap_ev_search
        // password = ei3it

        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
        int loadNumber = 0;
        int recsPerbatch = 0;
        try {
            loadNumber = Integer.parseInt(args[4]);
        }
        catch(NumberFormatException e) {
            loadNumber = 0;
        }
        try {
            recsPerbatch = Integer.parseInt(args[5]);
        }
        catch(NumberFormatException e) {
            recsPerbatch = 50000;
        }
        String operation = args[6];
        tablename = args[7];
        String currentDb = args[8].toLowerCase();
        long timestamp=0;

        Combiner.TABLENAME = tablename;
        Combiner.CURRENTDB = currentDb;
        System.out.println(Combiner.TABLENAME);

        String dbname = currentDb;
        if (timestamp > 0)
            dbname=dbname+"cor";
        CombinedWriter writer = new CombinedXMLWriter(recsPerbatch,
                                                      loadNumber,
                                                      dbname, "dev");

        writer.setOperation(operation);

        XmlCombiner c = new XmlCombiner(writer);
        if (timestamp==0 && (loadNumber > 3000 || loadNumber < 1000) && (loadNumber >1))
        {
           c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
        }
        else if(timestamp > 0)
        {
           c.writeCombinedByTimestamp(url, driver, username, password, timestamp);
        }
        else if(loadNumber == 1)
        {
        	c.writeCombinedByTable(url, driver, username, password);
        }
        else if(loadNumber == 0)
        {
            for(int yearIndex = 1904; yearIndex <= 2012; yearIndex++)
            {
              c = new XmlCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex,dbname, "dev"));
              c.writeCombinedByYear(url, driver, username, password, yearIndex);
            }
            if(Combiner.CURRENTDB.equalsIgnoreCase("chm"))
            {
                c = new XmlCombiner(new CombinedXMLWriter(recsPerbatch, 9999,dbname, "dev"));
                c.writeCombinedByYear(url, driver, username, password, 9999);
            }
            if(Combiner.CURRENTDB.equalsIgnoreCase("geo"))
            {
                c = new XmlCombiner(new CombinedXMLWriter(recsPerbatch, 9999,dbname, "dev"));
                c.writeCombinedByYear(url, driver, username, password, 9999);
            }
            if(Combiner.CURRENTDB.equalsIgnoreCase("cpx"))
            {
                c = new XmlCombiner(new CombinedXMLWriter(recsPerbatch, 9999,dbname, "dev"));
                c.writeCombinedByYear(url, driver, username, password, 9999);
            }
        }
        else
        {
           c.writeCombinedByYear(url, driver, username, password, loadNumber);
        }
    }

    public XmlCombiner(CombinedWriter writer)
    {
        super(writer);
    }

    public void writeCombinedByYearHook(Connection con,
                                        int year)
        throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;
        try
        {

            stmt = con.createStatement();
            System.out.println("Running the query...");
            if(Combiner.CURRENTDB.equalsIgnoreCase("chm")&&(year==9999))
            {
                rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APICT, APICT1,APILT, APILT1,CLASSIFICATIONDESC,APIAMS, SEQ_NUM from " + Combiner.TABLENAME + " where  PUBLICATIONYEAR in ('1940','1981-1982','1982-1983','1983-1984','1984-1985','1987-1988','1988-1989','1989-1989','1989-1990','1990-1991','1991-1991','1993-1994','1994-1995','1995-1996','1996-1997','1997-1998','1999-2000','2000-2001','2001-1993','2001-2002','2002-2003','2003-2004','2004-2005','2005-2006','2006-2007,'2007-2008','2008-2009','2009-2010', '2010-2011', '2011-2012') AND loadnumber != 0 and loadnumber < 100000000 and database='" + Combiner.CURRENTDB + "'");
            }
            else if(Combiner.CURRENTDB.equalsIgnoreCase("geo")&&(year==9999))
            {
                rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APICT, APICT1,APILT, APILT1,CLASSIFICATIONDESC,APIAMS,SEQ_NUM from " + Combiner.TABLENAME + " where  PUBLICATIONYEAR in ('1944-1995','1952-1985','1960-1976','1973-1982','1974-1975','1974-1978','1975-1976','1975-1978','1976-1977','1976-1981','1976-1987','1977-1978','1977-1979','1977-1980','1978-1979','1978-1980','1979-1980','1979-1981','1979-1982','1979-1983','1979-1994','1980-1981','1980-1982','1980-1983','1980-1984','1981-1982','1981-1983','1981-1984','1981-1985','1981-1986','1981-1992','1982-1983','1982-1984','1982-1985','1982-1986','1982-1987','1982-1988','1982-1989','1982-1999','1983-1984','1983-1985','1983-1986','1983-1987','1983-1988','1983-1989','1984-1985','1984-1986','1984-1987','1984-1988','1984-1989','1985-1986','1985-1987','1985-1988','1985-1989','1985-1990','1986-1987','1986-1988','1986-1989','1987-1988','1987-1989','1987-1990','1987-1991','1988-1988','1988-1989','1988-1990','1988-1991','1989-1990','1989-1991','1989-1992','1989-1993','1990-1991','1990-1992','1990-1993','1991-1992','1991-1993','1991-1994','1992-1992','1992-1993','1992-1994','1992-1995','1993-1994','1993-1995','1994-1995','1994-1996','1995-1996','1995-1997','1996-1997','1997-1998','1997-1999','1998-1999','1999-2000','2000-2001','2000-2002','2001-2002','2002-2003','2003-2004','2004-2005','2005-2006','2005-2007','2006-2007','2007-2008','2008-2009', '2009-2010', '2010-2011', '2011-2012') AND loadnumber != 0 and loadnumber < 100000000 and database='" + Combiner.CURRENTDB + "'");
            }
            else if(Combiner.CURRENTDB.equalsIgnoreCase("cpx")&&(year==9999))
            {
                rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APICT, APICT1,APILT, APILT1,CLASSIFICATIONDESC,APIAMS,SEQ_NUM from " + Combiner.TABLENAME + " where  PUBLICATIONYEAR in ('1798','1885','1964-1980','1977-1978','1977-1980','1978-1979','1979-1980','1979-1981','1979-1989','1980-1981','1980-1982','1981-1982','1981-1983','1982-1983','1983-1984','1984-1985','1985-1986','1986-1987','1987-1988','1988-1989','1989-190','1989-1989','1989-1990','1989-1993','1990-1991','1991-1991','1991-1992','1992-1993','1992-1994','1993-1994','1993-1995','1993-94','1994-1995','1995-1996','1995-96','1996-1997','1997-1998','1997-2003','1997-98','1998-1999','1998-99','1999-2000','2000-2001','2001-1993','2001-2002','2002-2003','2003-2004','2004-2005','2005-2006','2006-2007','2007-2008','2008-2009','2009-2010','2010-2011','2011-2012','2008;2008') AND loadnumber != 0 and loadnumber < 100000000 and database='" + Combiner.CURRENTDB + "'");
            }
            else
            {
                rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER,PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APICT, APICT1,APILT, APILT1,CLASSIFICATIONDESC,APIAMS,SEQ_NUM from " + Combiner.TABLENAME + " where  PUBLICATIONYEAR='" + year + "' AND loadnumber != 0 and loadnumber < 100000000 and database='" + Combiner.CURRENTDB + "'");
            }
            System.out.println("Got records ...from table::"+Combiner.TABLENAME);
            writeRecs(rs,con);
            System.out.println("Wrote records.");
            this.writer.end();
            this.writer.flush();

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
    }
    
public void writeCombinedByTableHook(Connection con)
throws Exception
{
	Statement stmt = null;
	ResultSet rs = null;
	try
	{
	
		stmt = con.createStatement();
		System.out.println("Running the query...");
		String sqlQuery = "select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APICT, APICT1,APILT, APILT1,CLASSIFICATIONDESC,APIAMS,SEQ_NUM from " + Combiner.TABLENAME +" where database='" + Combiner.CURRENTDB + "'";
		System.out.println(sqlQuery);
		rs = stmt.executeQuery(sqlQuery);
		
		System.out.println("Got records ...from table::"+Combiner.TABLENAME);
		writeRecs(rs,con);
		System.out.println("Wrote records.");
		this.writer.end();
		this.writer.flush();
	
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
}


    public void writeRecs(ResultSet rs, Connection con)
        throws Exception
    {
        int i = 0;
        EVCombinedRec recSecondBox = null;
        EVCombinedRec[] recArray = null;
        boolean isGeoBase = false;
        boolean isChimica = false;
        boolean isCpx = false;
        String accessNumber = "";
        String pui = "";

        while (rs.next())
        {
          ++i;
          String firstGUID = "";
          int numCoords = 1;
          int coordCount = 0;
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
          //System.out.println("ACCESSNUMBER= "+rs.getString("ACCESSNUMBER"));

          Vector recVector = new Vector();
          try
          {
              for(int currentCoord = 0; currentCoord < numCoords; currentCoord++)
              {
                String[] coords = null;
                String[] secondBoxCoords= null;
                EVCombinedRec rec = new EVCombinedRec();
                if (validYear(rs.getString("PUBLICATIONYEAR")))
                {

                    if(rs.getString("AUTHOR") != null)
                    {
                        String authorString = rs.getString("AUTHOR");
                        if(rs.getString("AUTHOR_1") !=null)
                        {
                            authorString=authorString+rs.getString("AUTHOR_1");
                        }
                        rec.put(EVCombinedRec.AUTHOR, prepareBdAuthor(authorString));
                        
                        rec.put(EVCombinedRec.AUTHORID, getAuID());
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

                        }
                        else if(rs.getString("CORRESPONDENCEAFFILIATION") != null)
                        {
                            affiliation = rs.getString("CORRESPONDENCEAFFILIATION");
                            BdCorrespAffiliations aff = new BdCorrespAffiliations(affiliation);
                            rec.put(EVCombinedRec.AUTHOR_AFFILIATION,  aff.getSearchValue());
                            rec.put(EVCombinedRec.AFFILIATION_LOCATION,  aff.getLocationsSearchValue());
                            rec.put(EVCombinedRec.COUNTRY,  aff.getCountriesSearchValue());
                        }
                    }

                    if (rs.getString("EDITORS") != null)
                    {
                        rec.put(EVCombinedRec.EDITOR, prepareEditor(rs.getString("EDITORS")));
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

                    if (rs.getString("VOLUMETITLE") != null)
                    {
                        rec.put(EVCombinedRec.VOLUME_TITLE, rs.getString("VOLUMETITLE"));
                    }

                    String abString = getStringFromClob(rs.getClob("ABSTRACTDATA"));
                    if (abString != null)
                    {
                        rec.put(EVCombinedRec.ABSTRACT, abString);
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
                         if(!navigatorterms.isEmpty())
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
                    if(docType != null)
                    {
                        boolean confCodeFlag = false;
                        if(rs.getString("CONFCODE") != null)
                        {
                            confCodeFlag = true;
                        }

                        String ct = null;
                        if((ct = getCitationType(docType,confCodeFlag)) != null)
                        {
                            if((isCpx)&&((rs.getString("MAINHEADING") != null)|| rec.containsKey(EVCombinedRec.CONTROLLED_TERMS)))
                            {
                                ct = ct + " CORE";
                            }
                            rec.put(EVCombinedRec.DOCTYPE,ct);
                        }
                    }
                    else if(isCpx)
                    {
                        docType = "";
                        if ((rs.getString("MAINHEADING") != null)|| rec.containsKey(EVCombinedRec.CONTROLLED_TERMS)){
                            docType = docType + " CORE";
                        }
                        rec.put(EVCombinedRec.DOCTYPE, docType);
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

                    if (rs.getString("ISSUETITLE") != null)
                    {
                        rec.put(EVCombinedRec.MONOGRAPH_TITLE, rs.getString("ISSUETITLE"));
                    }

                    rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));

                    rec.put(EVCombinedRec.DATABASE, rs.getString("DATABASE"));


                    rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOADNUMBER"));

                    if (rs.getString("PUBLICATIONYEAR") != null)
                    {
                        rec.put(EVCombinedRec.PUB_YEAR, rs.getString("PUBLICATIONYEAR"));
                    }

                    //System.out.println("ACCESSNUMBER= "+rs.getString("ACCESSNUMBER")+" PAGE "+rs.getString("PAGE")+" ** "+rs.getString("ARTICLENUMBER"));

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
                    accessNumber = rs.getString("ACCESSNUMBER");

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

                    if (rs.getString("DATABASE").equals("cpx"))
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
                    
                   //*
                   // * use for numerical index
                    if(pui!=null && pui.length()>0)
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

            }
           
            recArray = (EVCombinedRec[])recVector.toArray(new EVCombinedRec[0]);
            this.writer.writeRec(recArray);
          }
          catch(Exception e)
         {
            System.out.println("**** ERROR Found on access number "+accessNumber+" *****");
            e.printStackTrace();
         }
        }
    }

    /*
    private void populateNumericalIndex(String pui,EVCombinedRec rec,Connection con) throws Exception
    {
    	Statement stmt = null;
    	ResultSet rs = null;
    	StringBuffer niBuffer = new StringBuffer();
    	NumberFormat formatter = new DecimalFormat();
    	try
    	{
    		if(pui!=null && pui.length()>0)
    		{
	    		stmt = con.createStatement();
	    		//System.out.println("Running the Numerical query...");
	    		String sqlQuery = "select * from BD_MASTER_NUMERICAL where PUI='"+pui+"'";
	    		//System.out.println(sqlQuery);
	    		rs = stmt.executeQuery(sqlQuery);
	    		//System.out.println("Got records ...from table BD_MASTER_NUMERICAL");
	    		while (rs.next())
	    	    {	    	         	    	         
	    	          if(rs.getString("MAXIMUM") != null)
	    	          {
		    	          if(rs.getString("UNIT") != null)
		    	          {
		    	        	  niBuffer.append(rs.getString("UNIT"));
		    	          }
		    	          //
		    	          // temperary change it to String from double for text search
		    	          // double maximum = 	rs.getDouble("MAXIMUM");
		    	          // formatter = new DecimalFormat("0.#####E0");
		    	          // niBuffer.append(" "+formatter.format(maximum).toString());
		    	          //
		    	          String maximum = 	rs.getString("MAXIMUM");		    	       
		    	          niBuffer.append(" "+maximum);
		    	          if(rs.getString("SYMBOL") != null)
		    	          {
		    	        	  niBuffer.append(" "+rs.getString("SYMBOL"));
		    	          }
		    	          		    	         
		    	          niBuffer.append(Constants.AUDELIMITER);		    	          
	    	          }
	    	          
	    	          if(rs.getString("MINIMUM") != null)
	    	          {
	    	        	  if(rs.getString("UNIT") != null)
		    	          {
		    	        	  niBuffer.append(rs.getString("UNIT"));
		    	          }
	    	        	
	    	        	  //
	    	        	  // temperary change it to String from double for text search
	    	        	  // double minimum = 	rs.getDouble("MINIMUM");
		    	          // formatter = new DecimalFormat("0.#####E0");
		    	          // niBuffer.append(" "+formatter.format(minimum).toString());
		    	          //
	    	        	  
	    	        	  String minimum = 	rs.getString("MINIMUM");
		    	          niBuffer.append(" "+minimum);
	    	        	  if(rs.getString("SYMBOL") != null)
		    	          {
		    	        	  niBuffer.append(" "+rs.getString("SYMBOL"));
		    	          }
	    	        	  	    	        	 
	    	        	  niBuffer.append(Constants.AUDELIMITER);		    	          
	    	          }
	    	          
	    	    }	 
	    		//System.out.println("Wrote records.");
    		}
    		if(niBuffer.length()>0)
    		{
    			rec.put(EVCombinedRec.NUMERICAL_INDEXING, prepareMulti(niBuffer.toString()));
    		}
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
    }
*/
    
    //*
    // ********  use this method for full numericalIndex	***********
    // *  
    private void populateNumericalIndex(String pui,EVCombinedRec rec,Connection con) throws Exception
    {
    	Statement stmt = null;
    	ResultSet rs = null;
    	
    	NumberFormat formatter = new DecimalFormat();
    	
    	try
    	{
    		if(pui!=null && pui.length()>0)
    		{
	    		stmt = con.createStatement();
	    		//System.out.println("Running the Numerical query...");
	    		String sqlQuery = "select * from BD_MASTER_NUMERICAL where PUI='"+pui+"' order by unit";
	    		//System.out.println(sqlQuery);
	    		rs = stmt.executeQuery(sqlQuery);
	    		//System.out.println("Got records ...from table BD_MASTER_NUMERICAL");
	    		while (rs.next())
	    	    {	
	    	        String unit = null;
    				String maximum = null;
    				String minimum = null;
    				StringBuffer niBuffer = new StringBuffer();
    				if(rs.getString("UNIT") != null)
					{
						unit = rs.getString("UNIT").toLowerCase();
					}
					else
					{
						System.out.println("no unit found for pui "+pui);
						return;
					}
					
	    	        if(rs.getString("MAXIMUM") != null)
	    	        {
		    	          maximum = rs.getString("MAXIMUM");	
		    	          if(BdNumericalIndexMapping.get(unit+"_maximum")!=null)
		    	          {
		    	          	  String niKey = BdNumericalIndexMapping.get(unit+"_maximum");
		    	          	  String oldMaximum = rec.get(niKey);
		    	          	  if(oldMaximum!=null)
		    	          	  {
			    	          	  if(maximum != null && oldMaximum != null)
			    	          	  {
			    	          	  	if(Double.parseDouble(maximum)>Double.parseDouble(oldMaximum))
			    	          	  	{
			    	          	  		rec.put(niKey,maximum);
			    	          	  	}
			    	          	  }
			    	          }
			    	          else
			    	          {
			    	          		rec.put(niKey,maximum);
			    	          }
			    	          niBuffer.append(maximum+Constants.AUDELIMITER);			    	          		    	         			    	        
			    	     }	    	          
	    	          }
	    	          
	    	          if(rs.getString("MINIMUM") != null)
	    	          {
	    	        	  minimum = rs.getString("MINIMUM");	
		    	          if(BdNumericalIndexMapping.get(unit+"_minimum")!=null)
		    	          {
		    	          	  String niKey = BdNumericalIndexMapping.get(unit+"_minimum");
		    	          	  String oldMinimum = rec.get(niKey);
		    	          	  if(oldMinimum!=null)
		    	          	  {
			    	          	  if(minimum != null && oldMinimum != null)
			    	          	  {
			    	          	  	if(Double.parseDouble(minimum)<Double.parseDouble(oldMinimum))
			    	          	  	{
			    	          	  		rec.put(niKey,minimum);
			    	          	  	}
			    	          	  }
			    	          }
			    	          else
			    	          {
			    	          	rec.put(niKey,minimum);
			    	          }
		    	          	  if(!minimum.equals(maximum))
		    	          	  {
		    	          		  niBuffer.append(minimum+Constants.AUDELIMITER);
		    	          	  }
	    	          }
	    	          if(BdNumericalIndexMapping.get(unit+"_text")!=null)
	    	          {
	    	          	String niKey = BdNumericalIndexMapping.get(unit+"_text");
	    	          	String oldText = rec.get(niKey);
	    	          	if(oldText!=null)
	    	          	{
	    	          		rec.put(niKey,oldText+niBuffer.toString());
	    	          	}
	    	          	else
	    	          	{
	    	          		rec.put(niKey,niBuffer.toString());
	    	          	}
	    	          }
	    	          
	    	    }	 
	    		//System.out.println("Wrote records.");
    		}
    
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
    }
    // */
    
    private String formatClassCodes(String c)
    {
        if (c == null)
        {
            return null;
        }

        return c.replaceAll("\\.","DQD");

    }
    
    private void setAuID(List auid)
    {
    	this.auid=auid;
    }
    
    public String[] getAuID()
    {    	
    	return  (String[]) this.auid.toArray(new String[1]);
    }


    public String[] prepareBdAuthor(String bdAuthor)
    {
        if(bdAuthor != null && !bdAuthor.trim().equals(""))
        {
            BdAuthors aus = new BdAuthors(bdAuthor);
            String[] ausArray = aus.getSearchValue();         
            setAuID(aus.getAuID());
            return ausArray;
        }
        return null;
    }
    private String prepareConfLocation(String coAff)
    {
        if(coAff != null && !coAff.trim().equals(""))
        {
            BdConfLocations aff = new BdConfLocations(coAff);
            return aff.getSearchValue();
        }
        return null;

    }
    private String[] prepareISBN(String isbnString) throws Exception
    {
        List list = new ArrayList();
        if(isbnString != null)
        {
            BdIsbn isbn = new BdIsbn(isbnString);
            List isbnList = isbn.getISBN();
            for(int i=0;i<isbnList.size();i++)
            {
                BdIsbn isbnObject = (BdIsbn)isbnList.get(i);

                if(isbnObject.getValue() !=null)
                {
                    list.add(isbnObject.getValue());
                }
            }
        }

        return (String[])list.toArray(new String[1]);
    }

    private String[] prepareCitationTitle(String citationTitle) throws Exception
    {
        List list = new ArrayList();
        if(citationTitle != null)
        {
            BdCitationTitle ct = new BdCitationTitle(citationTitle);
            List ctList = ct.getCitationTitle();

            for(int i=0;i<ctList.size();i++)
            {
                BdCitationTitle ctObject = (BdCitationTitle)ctList.get(i);

                if(ctObject.getTitle() !=null)
                {
                    list.add(ctObject.getTitle());
                }

            }
        }


        return (String[])list.toArray(new String[1]);
    }

    private String[] prepareTranslatedCitationTitle(String citationTitle) throws Exception
    {
        List list = new ArrayList();
        if(citationTitle != null)
        {
            BdCitationTitle ct = new BdCitationTitle(citationTitle);
            List ctList = ct.getTranslatedCitationTitle();

            for(int i=0;i<ctList.size();i++)
            {
                BdCitationTitle ctObject = (BdCitationTitle)ctList.get(i);

                if(ctObject.getTitle() !=null)
                {
                    list.add(ctObject.getTitle());
                }

            }
        }
            return (String[])list.toArray(new String[1]);
    }

    private String[] prepareEditor(String bdEditor)
    {
        if(bdEditor != null && !bdEditor.trim().equals(""))
        {
            BdEditors ed = new BdEditors(bdEditor);
            return ed.getSearchValue();
        }
        return null;
    }


    public String[] prepareMulti(String multiString)
            throws Exception
    {
        String[] multiStringArray = multiString.split(Constants.AUDELIMITER,-1);
        return multiStringArray;

    }

    private String[] prepareCASRegistry(String multiString, String database)
        throws Exception
    {
        ArrayList list = new ArrayList();
        String[] multiStringArray =  multiString.split(Constants.IDDELIMITER,-1);
        if(multiString.length()>0 && database.equalsIgnoreCase("elt"))
        {
            for(int i = 0; i < multiStringArray.length; i++)
            {
                String[] multiStringArray2 = multiStringArray[i].split(Constants.AUDELIMITER,-1);
                if(multiStringArray2.length == 3)
                {
                    if(multiStringArray2[2].indexOf(multiStringArray2[1])== -1)
                    {
                        if(multiStringArray2[0] != null && multiStringArray2[0].equalsIgnoreCase("b"))
                        {
                            list.add(stripAsterics(multiStringArray2[2]).concat("-").concat("(BT)").concat("-").concat(stripAsterics(multiStringArray2[1])));
                        }
                        else
                        {
                            list.add(stripAsterics(multiStringArray2[2]).concat("-").concat(stripAsterics(multiStringArray2[1])));
                        }
                    }
                    else
                    {
                        if(multiStringArray2[0] != null && multiStringArray2[0].equalsIgnoreCase("b"))
                        {
                            list.add(stripAsterics(multiStringArray2[2]).concat("-").concat("(BT)"));
                        }
                        else
                        {
                            list.add(stripAsterics(multiStringArray2[2]));
                        }
                    }
                }
            }
        }
        else
        {
            for(int i = 0; i < multiStringArray.length; i++)
            {
                String[] multiStringArray2 = multiStringArray[i].split(Constants.GROUPDELIMITER,-1);
                for(int j = 0; j < multiStringArray2.length; j++)
                {
                    list.add(stripAsterics(multiStringArray2[j]));
                }
            }
        }

        return (String[]) list.toArray(new String[0]);
    }

    private String[] prepareLanguage(String multiString)
        throws Exception
    {
        String[] languages = multiString.split(Constants.IDDELIMITER);
        for(int i = 0; i < languages.length; i++)
        {
            languages[i]=Language.getIso639Language(languages[i]);
        }
        return languages;
    }

/*
    private String prepareLoadNumber(String datesort,String loadNumber, String publicationyear)
        throws Exception
    {
        String ln = null;

        if(datesort != null)
        {
          String[] dt = datesort.split(Constants.IDDELIMITER);
          String weekOfYear = BdLoadNumberHash.getWeekNumber(dt[1]);

          ln = dt[0] + weekOfYear;
        }
        else
        {
          if(loadNumber.length() == 6 && !loadNumber.endsWith("00"))
          {
            ln = loadNumber;
          }
          else
          {
            ln = publicationyear + "01";
          }
        }

        return ln;
    }
*/


    private String prepareDateSort(String datesort,String publicationyear)
        throws Exception
    {
        String ds = null;

        if(datesort != null)
        {
          String[] dt = datesort.split(Constants.IDDELIMITER);
          if(dt.length == 3)
          {
            ds = dt[0]+ dt[1] + dt[2];
          }
          else if(dt.length == 2)
          {
            ds = dt[0] + dt[1] + "01";
          }
          else
          {
            ds = dt[0] + "0101";
          }

        }
        else
        {
          ds = publicationyear + "0101";
        }

        return ds;
    }

    public String preparePublisherName(String pn) throws Exception
    {
        String[] pnames = pn.split(Constants.IDDELIMITER);
        String[] pnames2 = pnames[0].split(Constants.AUDELIMITER);
        return pnames2[0];
    }
    private String getFirstNumber(String v)
    {

        MatchResult mResult = null;
        if (v == null)
        {
            return null;
        }

        if (perl.match("/[1-9][0-9]*/", v))
        {
            mResult = perl.getMatch();
        }
        else
        {
            return null;
        }

        return mResult.toString();
    }

    public String getPage(String xp,
                          String ar)
    {
        String strPage = null;

        if(ar != null)
        {
            strPage = ar;
        }
        else
        {
          strPage = xp;
        }

        return strPage;
    }


    private String getFirstPage(String v)
    {
        BdPage pages = new BdPage(v);
        return pages.getStartPage();
    }

    private String getDedupKey(String issn,
                               String coden,
                               String volume,
                               String issue,
                               String page)
        throws Exception
    {
        String firstVolume = getFirstNumber(volume);
        String firstIssue = getFirstNumber(issue);
        String firstPage = getFirstPage(page);

        if ((issn == null && coden == null) ||
                firstVolume == null ||
                firstIssue == null ||
                firstPage == null)
        {
            return (new GUID()).toString();
        }

        StringBuffer buf = new StringBuffer();

        if (issn != null)
        {
            buf.append(perl.substitute("s/-//g", issn));
        }
        else
        {
            buf.append(BdCoden.convert(coden));
        }

        buf.append("vol" + firstVolume);
        buf.append("is" + firstIssue);
        buf.append("pa" + firstPage);

        return buf.toString().toLowerCase();

    }

    private String getTreatmentCode(String tc)
    {
        StringBuffer tbuff = new StringBuffer();

        for (int i = 0; i < tc.length(); ++i)
        {
            char c = tc.charAt(i);

            if (c == 'A')
            {
                tbuff.append("APP");
            }
            else if (c == 'B')
            {
                tbuff.append("BIO");
            }
            else if (c == 'E')
            {
                tbuff.append("ECO");
            }
            else if (c == 'X')
            {
                tbuff.append("EXP");
            }
            else if (c == 'G')
            {
                tbuff.append("GEN");
            }
            else if (c == 'H')
            {
                tbuff.append("HIS");
            }
            else if (c == 'L')
            {
                tbuff.append("LIT");
            }
            else if (c == 'M')
            {
                tbuff.append("MAN");
            }
            else if (c == 'N')
            {
                tbuff.append("NUM");
            }
            else if (c == 'T')
            {
                tbuff.append("THR");
            }
            else if(c < 32)
            {
                tbuff.append(c);
            }
            else
            {
                tbuff.append("NA");
            }
        }

        return tbuff.toString();
    }

    private String getCitationType(String ct,boolean flag)
    {
        return BdDocumentType.getDocType(ct,flag);
    }



    private boolean validYear(String year)
    {
        if (year == null)
        {
            return false;
        }

        if (year.length() != 4)
        {
            return false;
        }

        return perl.match("/[1-9][0-9][0-9][0-9]/", year);
    }

    private String formatConferenceLoc(String ms,
                                       String mc,
                                       String mv,
                                       String my)
    {
        StringBuffer b = new StringBuffer(" ");
        if (ms != null)
        {
            b.append(ms + ", ");
        }

        if (mc != null)
        {
            b.append(mc + ", ");
        }

        if (mv != null)
        {
            b.append(mv + ", ");
        }

        if (my != null)
        {
            b.append(my);
        }

        return b.toString();

    }

    private String getStringFromClob(Clob clob) throws Exception
    {
        String temp = null;
        if (clob != null)
        {
            temp = clob.getSubString(1, (int) clob.length());
        }

        return temp;
    }

    private String[] parseCoordinates(String cs) throws Exception
    {
        cs = cs.replaceAll("[^a-zA-Z0-9]", "");
        String coordString = cs.trim().replaceAll("([NEWS])","-$1");

        String[] coords = coordString.split("-");
        for(int i=1;i< 5;i++)
        {
            if(coords[i].length() < 7)
            {
                int padCount = 8 - coords[i].length();
                for(int p=0;p < padCount;p++)
                    coords[i] += "0";
            }

            coords[i] = coords[i].replaceAll("[NE]","+").substring(0,coords[i].length()-4).replaceAll("\\+","");
            coords[i] = coords[i].replaceAll("[WS]","-");

            if(coords[i].substring(0,1).indexOf("-") != -1)
                coords[i] = coords[i].replaceAll("^(-)0{1,2}(.*?)","$1$2");
            else
                coords[i] = coords[i].replaceAll("^0{1,2}(.*?)","$1");

            int m = Integer.parseInt(coords[i]);
            if(i < 3)
            {
              if(m > 90)
                coords[i] = coords[i].substring(0,2);
              if(m < -90)
                coords[i] = coords[i].substring(0,3);
            }
            else
            {
                if(m > 180)
                  coords[i] = coords[i].substring(0,2);
                if(m < -180)
                  coords[i] = coords[i].substring(0,3);
            }
        }

        return coords;
    }

    public void writeCombinedByTimestampHook(Connection con, long timestamp) throws Exception
    {
            Statement stmt = null;
            ResultSet rs = null;

            try
            {

                stmt = con.createStatement();
                rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER,PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,CLASSIFICATIONDESC,APIAMS,SEQ_NUM from " + Combiner.TABLENAME + " where loadnumber != 0 and loadnumber < 100000000 and database='" + Combiner.CURRENTDB + "'");
                writeRecs(rs,con);
                this.writer.end();
                this.writer.flush();

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

    }

    public String[] prepareELTCV(String multiString)
    throws Exception
    {
        if(multiString != null && multiString.length() >0)
        {
            multiString = stripAsterics(multiString);

            String[] multiStringArray = null;
            if(multiString.indexOf(";") >0)
            {
                multiStringArray = multiString.split(";",-1);
                return multiStringArray;
            }
            else
            {
                String[] multiStringArray1 = {multiString};
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


    public String[] getCvs(List list)
    {

        if(list != null && list.size() > 0)
        {
            return (String[]) list.toArray(new String[0]);

        }
        return null;
    }

    private String stripAsterics(String line)
    {
        line = perl.substitute("s/\\*+//gi", line);
        return line;
    }


    public void writeCombinedByWeekHook(Connection con,
                                        int weekNumber)
        throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            stmt = con.createStatement();
            String sqlString = "select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER,substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APICT, APICT1,APILT, APILT1,CLASSIFICATIONDESC,APIAMS,SEQ_NUM from " + Combiner.TABLENAME + " where  LOADNUMBER='" + weekNumber + "' AND loadnumber != 0 and loadnumber < 100000000 and database='" + Combiner.CURRENTDB + "'";
            //String sqlString = "select * from bd_master where accessnumber='20143218020259'";
            rs = stmt.executeQuery(sqlString);
            writeRecs(rs,con);
            this.writer.end();
            this.writer.flush();

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

    }

}
