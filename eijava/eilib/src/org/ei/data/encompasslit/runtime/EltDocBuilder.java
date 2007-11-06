package org.ei.data.encompasslit.runtime;

import java.util.*;
import java.sql.*;

import org.ei.connectionpool.*;
import org.ei.domain.*;
import org.ei.util.StringUtil;
import org.ei.data.*;
import org.ei.xml.*;
import java.io.*;

import org.apache.oro.text.perl.*;

/** This class is the implementation of DocumentBuilder
  * Basically this class is responsible for building
  * a List of EIDocs from a List of DocIds.The input ie
  * list of docids come from APILITSearchControl and
  *
  */
public class EltDocBuilder implements DocumentBuilder, Keys {
    private static final String LT_MSG = "Please click here to view all linked terms";
    public static String ELT_TEXT_COPYRIGHT = "Compilation and indexing terms, Copyright 2007 Elsevier Engineering Information, Inc.";
    public static String ELT_HTML_COPYRIGHT = "Compilation and indexing terms, &copy; 2007 Elsevier Engineering Information, Inc.";
    public static String PROVIDER = "EnCompassLIT";
    private static final Key ELT_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "EnCompassLIT controlled terms");
    private static final Key ELT_CLASS_CODES = new Key(Keys.CLASS_CODES_MULTI, "EnCompassLIT class codes");
    private static final Key ELT_MAJOR_TERMS = new Key(Keys.MAJOR_TERMS, "EnCompassLIT major terms");
    private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_LA, Keys.RIS_TI, Keys.RIS_T3, Keys.RIS_VL, Keys.RIS_IS, Keys.RIS_SP, Keys.RIS_AUS, RIS_AD, Keys.RIS_EDS, Keys.RIS_PY, Keys.RIS_U1, Keys.RIS_N2, Keys.RIS_N1, Keys.RIS_AD, Keys.RIS_CVS };
    private static final Key[] LINKED_TERM_KEYS = { Keys.LINKED_TERMS };
    private static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.TITLE, Keys.AUTHORS, Keys.SOURCE, Keys.PUBLICATION_YEAR, Keys.PUBLISHER, Keys.ISSN, Keys.LANGUAGE, Keys.NO_SO, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    private static final Key[] ABSTRACT_KEYS = { Keys.TITLE, Keys.AUTHORS, Keys.LANGUAGE, Keys.ISSN, Keys.PUBLISHER, Keys.ABSTRACT, ELT_MAJOR_TERMS, ELT_CONTROLLED_TERMS, Keys.CAS_REGISTRY_CODES, Keys.SOURCE, Keys.PROVIDER, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.DOCID };
    private static final Key[] DETAILED_KEYS =
        {
            Keys.ACCESSION_NUMBER,
            Keys.TITLE,
            Keys.TITLE_TRANSLATION,
            Keys.AUTHORS,
            Keys.AUTHOR_AFFS,
            Keys.CORRESPONDING_AUTHORS,
            Keys.CORRESPONDING_AUTHORS_AFF,
            Keys.CORRESPONDING_EMAIL,
            Keys.SOURCE,
            Keys.SERIAL_TITLE,
            Keys.ABBRV_SERIAL_TITLE,
            Keys.ISSN,
            Keys.VOLUME,
            Keys.ISSUE,
            Keys.PAGE_RANGE,
            Keys.ISBN,
            Keys.EDITORS,
            Keys.EDITOR_AFFS,
            Keys.PUBLICATION_DATE,
            Keys.PUBLICATION_YEAR,
            Keys.REPORT_NUMBER,
            Keys.PUBLISHER,
            Keys.DOC_TYPE,
            Keys.LANGUAGE,
            Keys.SECONDARY_SOURCE,
            Keys.CONFERENCE_NAME,
            Keys.CONF_DATE,
            Keys.MEETING_LOCATION,
            Keys.CONF_EDITORS,
            Keys.CONF_EDITORS_AF,
            Keys.CONF_CODE,
            Keys.SPONSOR,
            Keys.ABSTRACT,
            Keys.NUMBER_OF_REFERENCES,
            ELT_CLASS_CODES,
            ELT_MAJOR_TERMS,
            ELT_CONTROLLED_TERMS,
            Keys.UNCONTROLLED_TERMS,
            Keys.CAS_REGISTRY_CODES,
            Keys.INDEXING_TEMPLATE,
            Keys.MANUAL_LINKED_TERMS,
            Keys.LINKED_TERMS,
            Keys.LINKED_TERMS_HOLDER,
            Keys.DOI,
            Keys.DOCID,
            Keys.COPYRIGHT,
            Keys.COPYRIGHT_TEXT,
            Keys.PROVIDER };
    private static Hashtable MAPDOCTYPES = new Hashtable();
    static
    {
    	MAPDOCTYPES.put("K_CP","CA");
    	MAPDOCTYPES.put("P_AB","CA");
    	MAPDOCTYPES.put("P_AR","CA");
    	MAPDOCTYPES.put("P_CP","CA");
    	MAPDOCTYPES.put("P_ED","CA");
    	MAPDOCTYPES.put("P_LE","CA");
    	MAPDOCTYPES.put("P_NO","CA");
    	MAPDOCTYPES.put("P_RE","CA");
    	MAPDOCTYPES.put("J_CP","CA");
    	MAPDOCTYPES.put("D_CP","CA");
    	MAPDOCTYPES.put("J_AB","JA");
    	MAPDOCTYPES.put("J_AR","JA");
    	MAPDOCTYPES.put("J_BZ","JA");
    	MAPDOCTYPES.put("J_ED","JA");
    	MAPDOCTYPES.put("J_ER","JA");
    	MAPDOCTYPES.put("J_LE","JA");
    	MAPDOCTYPES.put("J_NO","JA");
    	MAPDOCTYPES.put("J_RE","JA");
    	MAPDOCTYPES.put("J_SH","JA");
    	MAPDOCTYPES.put("D_AB","JA");
    	MAPDOCTYPES.put("D_AR","JA");
    	MAPDOCTYPES.put("D_BZ","JA");
    	MAPDOCTYPES.put("D_ER","JA");
    	MAPDOCTYPES.put("D_LE","JA");
    	MAPDOCTYPES.put("D_NO","JA");
    	MAPDOCTYPES.put("D_RE","JA");
    	MAPDOCTYPES.put("K_AR","MC");
    	MAPDOCTYPES.put("B_ER","MC");
    	MAPDOCTYPES.put("R_AB","RC");
    	MAPDOCTYPES.put("R_RE","RR");
    	MAPDOCTYPES.put("AB","AB");
    	MAPDOCTYPES.put("P","CA");
    	MAPDOCTYPES.put("DI","DS");
    	MAPDOCTYPES.put("Other","RC");
    	MAPDOCTYPES.put("R","RC");
    	MAPDOCTYPES.put("RE","RC");	   	
    }

    private Perl5Util perl = new Perl5Util();
    private Database database;

    private static String queryCitation = "select M_ID,DOI,CORG,TIE,TIF,PUB,PYR,AUT,AAF,STI,VLN,ISN,PAG,SPD,LNA,LOAD_NUMBER,SO,CF,ISSN,SECSTI,SECVLN,SECISN,SECPYR,CNFNAM,CNFSTD,CNFEND,CNFCTY,SPD,SO,SECIST,DT,STY,SEC,STA,SAN,PUB from elt_master where M_ID IN ";

    private static String queryAbstracts = "select M_ID,DOI,CORG,TIE,TIF,PUB,PYR,AUT,AAF,STI,VLN,ISN,PAG,SPD,CNFNAM,ISBN,ISSN,LNA,CNFNAM,CNFSTD,CNFEND,CNFVEN,CNFCTY,CNFCNY,SECSTI,SECVLN,SECISN,SECPYR,APICT,SO,ABS,SEC,CF,APICRN,SECIST,OAB,APICC,STA,SAN,LOAD_NUMBER,CVS,CVM,CVA,CVP,CVN,CVMA,CVMP,CVMN,APICRN from elt_master where M_ID IN ";

    // jam 12/30/2002
    // New Index - field change from AN to EX
    private static String queryDetailed =
        "select M_ID,DOI,CIP,TIE,TIF,PUB,PYR,AUT,AAF,STI,STA,RNR,VLN,ISN,PAG,SPD,CNFNAM,CNFSTD,CNFEND,CNFCTY,CNFCNY,CNFEDN,CNFEDO,CNFCOD,CNFSPO,CPRS,CORG,EML,SECSTI,SECVLN,SECISN,SECPYR,ISBN,ISSN,LNA,STY,ITY,REF,APICT,APICRN,APIUT,APICC,APILTM,APILT,APIATM,SO,ABS,DT,SEC,CF,OAB,SECIST,LOAD_NUMBER,SAN,CVS,CVM,CVA,CVP,CVN,CVMA,CVMP,CVMN from elt_master where M_ID IN ";

    private static String queryLinkedTerms = "select M_ID,APILT,LOAD_NUMBER from elt_master where M_ID in ";
    EltAusFormatter ausFormatter = new EltAusFormatter();
    public DocumentBuilder newInstance(Database database) {
        return new EltDocBuilder(database);
    }

    public EltDocBuilder() {
    }

    public EltDocBuilder(Database database) {
        this.database = database;
    }

    /** This method takes a list of DocID objects and dataFormat
     *  and returns a List of EIDoc Objects based on a particular
     *  dataformat
     *  @ param listOfDocIDs
     *  @ param dataFormat
     *  @ return List --list of EIDoc's
     *  @ exception DocumentBuilderException
     */
    public List buildPage(List listOfDocIDs, String dataFormat) throws DocumentBuilderException {
        List l = null;
        try {
            if (dataFormat.equals(Citation.CITATION_FORMAT)) {
                l = loadCitations(listOfDocIDs);
            }
            else if (dataFormat.equals(Abstract.ABSTRACT_FORMAT)) {
                l = loadAbstracts(listOfDocIDs);
            }
            else if (dataFormat.equals(FullDoc.FULLDOC_FORMAT)) {
                l = loadDetailed(listOfDocIDs);
            }
            else if (dataFormat.equals(LinkedTermDetail.LINKEDTERM_FORMAT)) 
            {
                l = loadLinkedTerms(listOfDocIDs);
            }
            else if (dataFormat.equalsIgnoreCase("RIS")) {
                l = loadRIS(listOfDocIDs);
            }

        }
        catch (Exception e) {
            throw new DocumentBuilderException(e);
        }

        return l;
    }
    private List loadRIS(List listOfDocIDs) throws Exception {
        Hashtable oidTable = getDocIDTable(listOfDocIDs);
        List list = new ArrayList();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildINString(listOfDocIDs);

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();

            rset = stmt.executeQuery(queryDetailed + INString);

            DatabaseConfig dconfig = DatabaseConfig.getInstance();

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                did.setDatabase(dconfig.getDatabase("elt"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, EltDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, EltDocBuilder.ELT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, EltDocBuilder.ELT_TEXT_COPYRIGHT));

                if (rset.getString("TIE") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.substituteChars(rset.getString("TIE"))));
                }
                else {
                    if (rset.getString("STI") != null) {
                        ht.put(Keys.RIS_T3, new XMLWrapper(Keys.RIS_T3, StringUtil.substituteChars(rset.getString("STI"))));
                    }
                }
                if (rset.getString("AUT") != null) {

                    Contributors authors = null;

                    int loadNumber = rset.getInt("LOAD_NUMBER");
                    if (loadNumber >= 200000)
                        authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.substituteChars(ausFormatter.formatAuthors(rset.getString("AUT"))), Keys.AUTHORS));

                    else
                        authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.substituteChars(rset.getString("AUT")), Keys.AUTHORS));

                    ht.put(Keys.RIS_AUS, authors);
                }
                if (rset.getString("AAF") != null) {
                    Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, StringUtil.substituteChars((EltAafFormatter.formatAffiliation(rset.getString("AAF")))));
                    ht.put(Keys.RIS_AD, new Affiliations(Keys.RIS_AD, affil));

                }

                if (rset.getString("VLN") != null) {
                    String strVol = replaceVolumeNullWithEmptyString(rset.getString("VLN"));
                    strVol = perl.substitute("s/[vol\\.]//gi", strVol);
                    ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, strVol));

                }
                if (rset.getString("ISN") != null) {
                    ht.put(Keys.RIS_IS, new ISSN(rset.getString("ISN")));
                }
                if (rset.getString("PYR") != null) {
                    ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, rset.getString("PYR"), perl));

                }

                if (rset.getString("PAG") != null) {
                    String pageRange = formatPAGE(rset.getString("PAG"));

                    if (pageRange != null) {

                        if (perl.match("/(\\d+)[^\\d](\\d+)/", pageRange)) {
                            if (perl.match("/(\\d+)/", pageRange)) {
                                ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, perl.group(0).toString()));
                                if (perl.match("/(\\d+)/", perl.postMatch())) {
                                    ht.put(Keys.RIS_EP, new XMLWrapper(Keys.RIS_EP, perl.group(0).toString()));
                                }
                            }
                        }
                        else {
                            pageRange = perl.substitute("s/[^\\d-,]//g", pageRange);
                            ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, pageRange));
                        }
                    }
                }

                if (rset.getString("ISSN") != null) {
                    ht.put(Keys.RIS_SN, new ISSN(rset.getString("ISSN")));
                }
                if (rset.getString("ISBN") != null) {
                    ht.put(Keys.RIS_S1, new ISBN(rset.getString("ISBN")));
                }

                if (rset.getString("CNFNAM") != null) {
                    ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, StringUtil.substituteChars(rset.getString("CNFNAM"))));

                    StringBuffer cnfLocation = new StringBuffer();
                    if (rset.getString("CNFCTY") != null) {
                        cnfLocation.append(StringUtil.substituteChars(rset.getString("CNFCTY")));
                    }
                    if (rset.getString("CNFCNY") != null) {
                        cnfLocation.append(", ").append(StringUtil.substituteChars(rset.getString("CNFCNY")));
                    }

                    ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, StringUtil.substituteChars(cnfLocation.toString())));

                }

                if (rset.getString("apict") != null) {

                    StringBuffer cvs = new StringBuffer();

                    if (rset.getString("cvs") != null) {
                        cvs.append(rset.getString("cvs"));
                    }
                    if (rset.getString("cva") != null) {
                        if (cvs.length() > 0)
                            cvs.append(";").append(rset.getString("cva"));
                        else
                            cvs.append(rset.getString("cva"));
                    }

                    if (rset.getString("cvp") != null) {
                        if (cvs.length() > 0)
                            cvs.append(";").append(rset.getString("cvp"));
                        else
                            cvs.append(rset.getString("cvp"));
                    }
                    if (rset.getString("cvn") != null) {
                        if (cvs.length() > 0)
                            cvs.append(";").append(rset.getString("cvn"));
                        else
                            cvs.append(rset.getString("cvn"));
                    }

                    if (cvs.length() > 0)
                        ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(StringUtil.substituteChars(cvs.toString()))));

                }
                //FLS
                if (rset.getString("APIUT") != null)
                    ht.put(Keys.RIS_FLS, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(StringUtil.substituteChars(rset.getString("APIUT")))));

                String cc = StringUtil.substituteChars(rset.getString("APICC"));
                String secondarySrc = StringUtil.substituteChars(rset.getString("SEC"));
                String secondaryTitle = StringUtil.substituteChars(rset.getString("SECSTI"));
                String abs = null;

                if (!perl.match("/Chemical Abstracts/i", secondarySrc) && !perl.match("/Chemical Abstracts/i", secondaryTitle)) {

                    if (((abs = hasAbstract(rset.getClob("ABS"))) != null))
                        ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, StringUtil.substituteChars(abs)));
                    else {

                        if (rset.getString("OAB") != null)
                            ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, StringUtil.substituteChars(rset.getString("OAB"))));
                    }
                }
                else {

                    if (perl.match("/Oil Field Chemicals/i", cc)) {

                        if (((abs = hasAbstract(rset.getClob("ABS"))) != null))
                            ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, StringUtil.substituteChars(abs)));
                        else {

                            if (rset.getString("OAB") != null)
                                ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, StringUtil.substituteChars(rset.getString("OAB"))));

                        }
                    }
                }

                if (rset.getString("DT") != null) {
                    String docType = rset.getString("DT");

                    if (docType.startsWith("B_"))
                        docType = "BOOK";
                    else if (docType.startsWith("D_") || docType.startsWith("J_"))
                        docType = "JOUR";
                    else if (docType.startsWith("P_"))
                        docType = "CONF";
                    else if (docType.startsWith("R"))
                        docType = "RPRT";
                    else {
                        docType = "GEN";
                    }

                    ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, docType));
                }

                EIDoc eiDoc = new EIDoc(did, ht, RIS.RIS_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(RIS_KEYS);
                list.add(eiDoc);
                count++;
            }
        }
        finally {

            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }
    private String replaceVolumeNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = "";
        }

        if (!str.equals("") && ((str.indexOf("v", 0) < 0) && (str.indexOf("V", 0) < 0))) {
            str = "v ".concat(str);
        }
        return str;
    }
    private List loadAbstracts(List listOfDocIDs) throws Exception {
        Hashtable oidTable = getDocIDTable(listOfDocIDs);
        List list = new ArrayList();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildINString(listOfDocIDs);

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();

            rset = stmt.executeQuery(queryAbstracts + INString);

            DatabaseConfig dconfig = DatabaseConfig.getInstance();

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                did.setDatabase(dconfig.getDatabase("elt"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, EltDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, EltDocBuilder.ELT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, EltDocBuilder.ELT_TEXT_COPYRIGHT));

                String cc = StringUtil.substituteChars(rset.getString("APICC"));

                String secondarySrc = StringUtil.substituteChars(rset.getString("SEC"));
                String secondaryTitle = StringUtil.substituteChars(rset.getString("SECSTI"));
                String abs = null;

                if (!perl.match("/Chemical Abstracts/i", secondarySrc) && !perl.match("/Chemical Abstracts/i", secondaryTitle)) {

                    if ((abs = hasAbstract(rset.getClob("ABS"))) != null) {
                        ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(abs)));
                    }
                    else {

                        if (rset.getString("OAB") != null)
                            ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(rset.getString("OAB"))));
                    }

                }
                else {

                    if (perl.match("/Oil Field Chemicals/i", cc)) {

                        if ((abs = hasAbstract(rset.getClob("ABS"))) != null) {
                            ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(abs)));
                        }
                        else {

                            if (rset.getString("OAB") != null)
                                ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(rset.getString("OAB"))));
                        }
                    }
                }

                //DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
                }

                //CAS
                if (rset.getString("APICRN") != null) {
                    ht.put(Keys.CAS_REGISTRY_CODES, new XMLMultiWrapper2(Keys.CAS_REGISTRY_CODES, setCRC(StringUtil.substituteChars(removePoundSign(rset.getString("APICRN"))))));

                }
                if (rset.getString("TIE") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.substituteChars(rset.getString("TIE"))));
                }

                if (rset.getString("TIF") != null) {
                    ht.put(Keys.TITLE_TRANSLATION, new XMLWrapper(Keys.TITLE_TRANSLATION, StringUtil.substituteChars(rset.getString("TIF"))));
                }

                if (rset.getString("AUT") != null) {

                    Contributors authors = null;

                    int loadNumber = rset.getInt("LOAD_NUMBER");
                    if (loadNumber >= 200000)
                        authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.substituteChars(ausFormatter.formatAuthors(rset.getString("AUT"))), Keys.AUTHORS));

                    else
                        authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.substituteChars(rset.getString("AUT")), Keys.AUTHORS));

                    ht.put(Keys.AUTHORS, authors);
                }

                if (rset.getString("SO") != null) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.substituteChars(rset.getString("SO"))));
                }
                else {
                    StringBuffer source = new StringBuffer();

                    if (rset.getString("STI") != null) {

                        source.append(formatTitle(StringUtil.substituteChars(rset.getString("STI"))));
                    }
                    else {
                        if (rset.getString("STA") != null) {
                            source.append(formatTitle(StringUtil.substituteChars(rset.getString("STA"))));
                        }

                    } // VO
                    if (rset.getString("VLN") != null) {
                        source.append("&nbsp;&nbsp;").append(formatVOLUME(rset.getString("VLN")));
                    }
                    if (rset.getString("ISN") != null) {
                        if (rset.getString("VLN") == null)
                            source.append(" ");
                        source.append("(").append(formatISSUE(rset.getString("ISN"))).append(")");
                    }

                    if (rset.getString("SPD") != null) {
                        source.append(" ").append(getYear(rset.getString("SPD")));
                    }
                    else {
                        if (rset.getString("PYR") != null) {
                            source.append(" ").append(rset.getString("PYR"));
                        }
                    }

                    if (rset.getString("PAG") != null) {
                        source.append(" p. ").append(formatPAGE(rset.getString("PAG")));
                    }

                    if (rset.getString("CNFNAM") != null) {
                        source.append(", ").append(StringUtil.substituteChars(rset.getString("CNFNAM")));
                    }

                    if ((rset.getString("CNFSTD") != null)) {
                        source.append(", ").append(getYear(rset.getString("CNFSTD")));
                    }

                    if (rset.getString("CNFCTY") != null) {
                        source.append(", ").append(StringUtil.substituteChars(rset.getString("CNFCTY")));
                    }

                    if (rset.getString("SECSTI") != null) {
                        source.append(", ").append(StringUtil.substituteChars(rset.getString("SECSTI")));
                    }
                    if (rset.getString("SECVLN") != null) {
                        source.append(" ").append(formatVOLUME(rset.getString("SECVLN")));
                    }
                    if (rset.getString("SECISN") != null) {
                        source.append("(").append(formatISSUE(rset.getString("SECISN"))).append(")");
                    }
                    if (rset.getString("SAN") != null) {
                        source.append(" ").append(formatSAN(rset.getString("SAN")));
                    }
                    if (rset.getString("SECPYR") != null) {
                        source.append(" ").append(formatSECPYR(rset.getString("SECPYR")));
                    }

                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.substituteChars(source.toString())));

                }

                //PUB
                if (rset.getString("PUB") != null) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.substituteChars(rset.getString("PUB"))));
                }

                //IBN
                if (rset.getString("ISBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(rset.getString("ISBN")));
                }

                //ISN
                if (rset.getString("ISSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(formatISSN(rset.getString("ISSN"))));
                }

                if ((rset.getString("LNA") != null) && (!rset.getString("LNA").equalsIgnoreCase("ENGLISH"))) {
                    String la = rset.getString("LNA");
                    if (la.length() > 1)
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, formatLanguage(StringUtil.substituteChars(rset.getString("LNA")))));
                }
                if (rset.getString("apict") != null) {

                    StringBuffer cvs = new StringBuffer();
                    StringBuffer cvm = new StringBuffer();

                    if (rset.getString("cvs") != null) {
                        cvs.append(rset.getString("cvs"));
                    }
                    if (rset.getString("cvm") != null) {
                        cvm.append(rset.getString("cvm"));
                    }
                    if (rset.getString("cva") != null) {
                        if (cvs.length() > 0)
                            cvs.append(";").append(rset.getString("cva"));
                        else
                            cvs.append(rset.getString("cva"));
                    }
                    if (rset.getString("cvp") != null) {
                        if (cvs.length() > 0)
                            cvs.append(";").append(rset.getString("cvp"));
                        else
                            cvs.append(rset.getString("cvp"));
                    }
                    if (rset.getString("cvn") != null) {
                        if (cvs.length() > 0)
                            cvs.append(";").append(rset.getString("cvn"));
                        else
                            cvs.append(rset.getString("cvn"));
                    }
                    if (rset.getString("cvma") != null) {
                        if (cvm.length() > 0)
                            cvm.append(";").append(rset.getString("cvma"));
                        else
                            cvm.append(rset.getString("cvma"));
                    }
                    if (rset.getString("cvmp") != null) {
                        if (cvm.length() > 0)
                            cvm.append(";").append(rset.getString("cvmp"));
                        else
                            cvm.append(rset.getString("cvmp"));
                    }
                    if (rset.getString("cvmn") != null) {
                        if (cvm.length() > 0)
                            cvm.append(";").append(rset.getString("cvmn"));
                        else
                            cvm.append(rset.getString("cvmn"));
                    }

                    if (cvs.length() > 0)
                        ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(ELT_CONTROLLED_TERMS, setCVS(StringUtil.substituteChars(CVSTermBuilder.formatCT(cvs.toString())))));

                    if (cvm.length() > 0)
                        ht.put(Keys.MAJOR_TERMS, new XMLMultiWrapper2(ELT_MAJOR_TERMS, setCVS(StringUtil.substituteChars(CVSTermBuilder.formatCT(cvm.toString())))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Abstract.ABSTRACT_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(ABSTRACT_KEYS);
                list.add(eiDoc);
                count++;
            }

        }
        finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }

    private KeyValuePair[] setCLS(String cvs) {
        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer(cvs, ";", false);
        String strToken = null;

        while (st.hasMoreTokens()) {
            strToken = st.nextToken().trim();
            if (strToken.length() > 0) {
                KeyValuePair k = new KeyValuePair(Keys.CLASS_CODES_MULTI, strToken);

                list.add(k);
            }

        }

        return (KeyValuePair[]) list.toArray(new KeyValuePair[list.size()]);

    }
    private KeyValuePair[] setCVS(String cvs) {
        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer(cvs, ";", false);
        String strToken = null;

        while (st.hasMoreTokens()) {
            strToken = st.nextToken().trim();
            strToken = strToken.replace('-', ' ');
            if (strToken.length() > 0) {
                KeyValuePair k = new KeyValuePair(getTermField(strToken), strToken);
                list.add(k);
            }

        }

        return (KeyValuePair[]) list.toArray(new KeyValuePair[list.size()]);

    }
    

    public Key getTermField(String cv) {

        Key field = null;

        if (cv.startsWith("*")) {
            if (cv.endsWith("-A"))
                field = Keys.MAJOR_REAGENT_TERM;
            else if (cv.endsWith("-P"))
                field = Keys.MAJOR_PRODUCT_TERM;
            else if (cv.endsWith("-N"))
                field = Keys.MAJOR_NOROLE_TERM;
            else
                field = Keys.MAJOR_TERM;
        }
        else {
            if (cv.endsWith("-A"))
                field = Keys.REAGENT_TERM;
            else if (cv.endsWith("-P"))
                field = Keys.PRODUCT_TERM;
            else if (cv.endsWith("-N"))
                field = Keys.NOROLE_TERM;
            else
                field = Keys.CONTROLLED_TERM;
        }

        return field;

    }
    public String[] setElementData(String elementVal) {
        ArrayList list = new ArrayList();
        AuthorStream aStream = null;
        String strToken = null;
        try {
            if (elementVal != null) {
                aStream = new AuthorStream(new ByteArrayInputStream(elementVal.getBytes()));
                strToken = null;
                while ((strToken = aStream.readAuthor()) != null) {
                    list.add(strToken);
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            if (aStream != null) {
                try {
                    aStream.close();
                    aStream = null;
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }
    private String hasAbstract(Clob clob) throws Exception {
        String abs = null;

        if (clob != null) {
            abs = StringUtil.getStringFromClob(clob);
        }

        if (abs == null || abs.equals("QQ")) {
            return null;
        }
        else {
            abs = perl.substitute("s/^<p>//i", abs);
            abs = perl.substitute("s/<\\/p>$//i", abs);
            abs = perl.substitute("s/&amp;/&/ig", abs);

            if (abs.equals(""))
                return null;
            else
                return abs;
        }
    }
    public String buildFirstIssn(String issn) {

        if (issn != null) {

            if (issn.length() == 9) {
                return issn;
            }
            else if (issn.length() == 8) {
                return issn.substring(0, 4) + "-" + issn.substring(4, 8);
            }
        }

        return null;
    }
    private String buildFirstVolume(String volume) {
        StringBuffer retStr = new StringBuffer();

        if (volume != null) {
            if (perl.match("/(\\d+)/", volume)) {
                retStr.append(perl.group(0).toString());
            }
        }

        return retStr.toString();
    }
    private String buildFirstIssue(String issue) {
        StringBuffer retStr = new StringBuffer();

        if (issue != null) {
            if (perl.match("/(\\d+)/", issue)) {
                retStr.append(perl.group(0).toString());
            }
        }

        return retStr.toString();
    }
    public static String removePoundSign(String ct) {

        Perl5Util perl = new Perl5Util();

        if (ct == null)
            return "";

        String parsedCT = perl.substitute("s/\\#//g", ct);

        return parsedCT;

    }
    private String buildFirstPage(String pageRange) {
        StringBuffer retStr = new StringBuffer();

        String firstPage = null;
        if (pageRange != null) {
            StringTokenizer tmpPage = new StringTokenizer(pageRange, "-");
            if (tmpPage.countTokens() > 0) {
                firstPage = tmpPage.nextToken();
            }
            else {
                firstPage = pageRange;
            }

            if (firstPage != null) {
                if (perl.match("/(\\d+)/", firstPage)) {
                    retStr.append(perl.group(0).toString());
                }
            }

        }

        return retStr.toString();

    }
    /**
                    *   This method basically takes list Of DocIDs as Parameter
                    *   This list of Docids use buildINString() method to build
                    *   the required IN clause String.This is appended to sql String
                    *   The resultSet so obtained by executing the sql,is iterated,
                    *   to build Detailed EIDoc objects,which are then added to EIdocumentList
                    *   @param listOfDocIDs
                    *   @return EIDocumentList
                    *   @exception Exception
                    */
    private List loadDetailed(List listOfDocIDs) throws Exception {
        Hashtable oidTable = getDocIDTable(listOfDocIDs);
        List list = new ArrayList();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildINString(listOfDocIDs);
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryDetailed + INString);
            DatabaseConfig dconfig = DatabaseConfig.getInstance();

            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                did.setDatabase(dconfig.getDatabase("elt"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, EltDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, EltDocBuilder.ELT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, EltDocBuilder.ELT_TEXT_COPYRIGHT));

                String cc = StringUtil.substituteChars(rset.getString("APICC"));
                String secondarySrc = StringUtil.substituteChars(rset.getString("SEC"));
                String secondaryTitle = StringUtil.substituteChars(rset.getString("SECSTI"));
                String abs = null;

                if (!perl.match("/Chemical Abstracts/i", secondarySrc) && !perl.match("/Chemical Abstracts/i", secondaryTitle)) {

                    if ((abs = hasAbstract(rset.getClob("ABS"))) != null) {
                        ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(abs)));
                    }
                    else {

                        if (rset.getString("OAB") != null)
                            ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(rset.getString("OAB"))));
                    }

                }
                else {

                    if (perl.match("/Oil Field Chemicals/i", cc)) {

                        if ((abs = hasAbstract(rset.getClob("ABS"))) != null) {
                            ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(abs)));
                        }
                        else {

                            if (rset.getString("OAB") != null)
                                ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(rset.getString("OAB"))));
                        }
                    }
                }

                //DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
                }
                // TS 02/10/03  the following elements added to ht for xml document mapping
                if (rset.getString("APIATM") != null) {

                    if (rset.getString("APILTM") != null) {

                        String ltm = rset.getString("APILTM");
                        if (!ltm.equalsIgnoreCase("QQ"))
                            ht.put(Keys.MANUAL_LINKED_TERMS, new LinkedTerms(Keys.MANUAL_LINKED_TERMS, getLinkedTerms(StringUtil.substituteChars(ltm), Keys.LINKED_SUB_TERM)));

                    }
                } // AN

                if (rset.getString("CIP") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("CIP")));
                }

                if (rset.getString("TIE") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.substituteChars(rset.getString("TIE"))));
                }

                if (rset.getString("TIF") != null) {
                    ht.put(Keys.TITLE_TRANSLATION, new XMLWrapper(Keys.TITLE_TRANSLATION, StringUtil.substituteChars(rset.getString("TIF"))));
                }

                if (rset.getString("AUT") != null) {

                    Contributors authors = null;

                    int loadNumber = rset.getInt("LOAD_NUMBER");
                    if (loadNumber >= 200000)
                        authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.substituteChars(ausFormatter.formatAuthors(rset.getString("AUT"))), Keys.AUTHORS));

                    else
                        authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.substituteChars(rset.getString("AUT")), Keys.AUTHORS));

                    ht.put(Keys.AUTHORS, authors);
                }

                if (rset.getString("AAF") != null) {
                    Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, StringUtil.substituteChars((EltAafFormatter.formatAffiliation(rset.getString("AAF")))));
                    ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));

                }

                if (rset.getString("CPRS") != null) {
                    ht.put(Keys.CORRESPONDING_AUTHORS, new XMLWrapper(Keys.CORRESPONDING_AUTHORS, StringUtil.substituteChars(rset.getString("CPRS"))));
                }

                if (rset.getString("CORG") != null) {
                    ht.put(Keys.CORRESPONDING_AUTHORS_AFF, new XMLWrapper(Keys.CORRESPONDING_AUTHORS_AFF, StringUtil.substituteChars(rset.getString("CORG"))));

                }

                if (rset.getString("EML") != null) {
                    ht.put(Keys.CORRESPONDING_EMAIL, new XMLWrapper(Keys.CORRESPONDING_EMAIL, StringUtil.substituteChars(StringUtil.substituteChars(rset.getString("EML")))));
                }

                if (rset.getString("STI") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, StringUtil.substituteChars(rset.getString("STI"))));
                }
                if (rset.getString("SO") != null) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.substituteChars(rset.getString("SO"))));
                }
                if (rset.getString("VLN") != null) {

                    String strVol = StringUtil.substituteChars(rset.getString("VLN"));
                    ht.put(Keys.VOLUME, new Volume(strVol, perl));
                }
                if (rset.getString("ISN") != null) {

                    String strIss = formatISSUE(StringUtil.substituteChars((rset.getString("ISN"))));
                    ht.put(Keys.ISSUE, new Issue(strIss, perl));
                }
                if (rset.getString("PAG") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.substituteChars(rset.getString("PAG")), perl));
                }

                if (rset.getString("STA") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, StringUtil.substituteChars(rset.getString("STA"))));
                }

                if (rset.getString("RNR") != null) {
                    ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, StringUtil.substituteChars(rset.getString("RNR"))));
                }

                if (rset.getString("SPD") != null) {
                    ht.put(Keys.PUBLICATION_DATE, new Year(rset.getString("SPD"), perl));
                }
                else {
                    if (rset.getString("PYR") != null) {
                        ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                    }
                }

                if ((rset.getString("LNA") != null) && (!rset.getString("LNA").equalsIgnoreCase("ENGLISH"))) {
                    String la = rset.getString("LNA");
                    if (la.length() > 1)
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, formatLanguage(StringUtil.substituteChars(rset.getString("LNA")))));
                }

                if (rset.getString("SEC") == null && rset.getString("SECSTI") == null) { //do nothing
                    if (rset.getString("PUB") != null)
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.substituteChars(rset.getString("PUB"))));
                }

                if (rset.getString("CNFNAM") != null) {

                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.substituteChars(rset.getString("CNFNAM"))));

                    if ((rset.getString("CNFSTD") != null) && (rset.getString("CNFEND") != null)) {

                        StringBuffer confDt = new StringBuffer();
                        confDt.append(rset.getString("CNFSTD")).append(" - ").append(rset.getString("CNFEND"));
                        ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, confDt.toString()));

                    }
                    if ((rset.getString("CNFCTY") != null) || (rset.getString("CNFCNY") != null)) {

                        StringBuffer confLocation = new StringBuffer();
                        if (rset.getString("CNFCTY") != null) {
                            confLocation.append(rset.getString("CNFCTY"));
                        }
                        if (rset.getString("CNFCNY") != null) {
                            if (confLocation.length() > 0) {
                                confLocation.append(", ").append(rset.getString("CNFCNY"));
                            }
                            else {
                                confLocation.append(rset.getString("CNFCNY"));
                            }

                        }
                        ht.put(Keys.MEETING_LOCATION, new XMLWrapper(Keys.MEETING_LOCATION, StringUtil.substituteChars(confLocation.toString())));
                    }

                    if (rset.getString("CNFEDN") != null) {
                        ht.put(Keys.CONF_EDITORS, new XMLWrapper(Keys.CONF_EDITORS, formatCONFEDN(StringUtil.substituteChars(rset.getString("CNFEDN")))));
                    }
                    if (rset.getString("CNFEDO") != null) {
                        ht.put(Keys.CONF_EDITORS_AF, new XMLWrapper(Keys.CONF_EDITORS_AF, formatCONFEDN(StringUtil.substituteChars(rset.getString("CNFEDO")))));
                    }

                    if (rset.getString("CNFCOD") != null) {
                        ht.put(Keys.CONF_CODE, new XMLWrapper(Keys.CONF_CODE, StringUtil.substituteChars(rset.getString("CNFCOD"))));
                    }
                    if (rset.getString("CNFSPO") != null) {
                        ht.put(Keys.SPONSOR, new XMLWrapper(Keys.SPONSOR, StringUtil.substituteChars(rset.getString("CNFSPO"))));
                    }
                }
                else {
                    if (rset.getString("CF") != null)
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.substituteChars(rset.getString("CF"))));
                }

                if (rset.getString("ISBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(rset.getString("ISBN")));
                } //ISN
                if (rset.getString("ISSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(formatISSN(rset.getString("ISSN"))));
                }

                StringBuffer strSecSour = new StringBuffer();

                if (rset.getString("SECSTI") != null) {
                    strSecSour.append(rset.getString("SECSTI"));
                    if (rset.getString("SECVLN") != null) {
                        strSecSour.append(" ").append(formatVOLUME(rset.getString("SECVLN")));
                    }
                    if (rset.getString("SECISN") != null) {
                        strSecSour.append("(").append(formatISSUE(rset.getString("SECISN"))).append(")");
                    }
                    if (rset.getString("SAN") != null) {
                        strSecSour.append(" ").append(formatSAN(rset.getString("SAN")));
                    }
                    if (rset.getString("SECPYR") != null) {
                        strSecSour.append(" (").append(rset.getString("SECPYR")).append(")");
                    }
                    ht.put(Keys.SECONDARY_SOURCE, new XMLWrapper(Keys.SECONDARY_SOURCE, StringUtil.substituteChars(strSecSour.toString())));
                }
                else {

                    if (rset.getString("SEC") != null)
                        ht.put(Keys.SECONDARY_SOURCE, new XMLWrapper(Keys.SECONDARY_SOURCE, StringUtil.substituteChars(rset.getString("SEC"))));

                } //ATM
                //	DT
                if ((rset.getString("DT") != null)) {
                    ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, formatDT(rset.getString("DT"))));
                }

                if (rset.getString("REF") != null) {
                    ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, rset.getString("REF")));
                }

                if (rset.getString("APICRN") != null) {
                    ht.put(Keys.CAS_REGISTRY_CODES, new XMLMultiWrapper2(Keys.CAS_REGISTRY_CODES, setCRC(StringUtil.substituteChars(rset.getString("APICRN")))));
                }

                if (rset.getString("apict") != null) {

                    StringBuffer cvs = new StringBuffer();
                    StringBuffer cvm = new StringBuffer();

                    if (rset.getString("cvs") != null) {
                        cvs.append(rset.getString("cvs"));
                    }
                    if (rset.getString("cvm") != null) {
                        cvm.append(rset.getString("cvm"));
                    }
                    if (rset.getString("cva") != null) {
                        if (cvs.length() > 0)
                            cvs.append(";").append(rset.getString("cva"));
                        else
                            cvs.append(rset.getString("cva"));
                    }
                    if (rset.getString("cvp") != null) {
                        if (cvs.length() > 0)
                            cvs.append(";").append(rset.getString("cvp"));
                        else
                            cvs.append(rset.getString("cvp"));
                    }
                    if (rset.getString("cvn") != null) {
                        if (cvs.length() > 0)
                            cvs.append(";").append(rset.getString("cvn"));
                        else
                            cvs.append(rset.getString("cvn"));
                    }
                    if (rset.getString("cvma") != null) {
                        if (cvm.length() > 0)
                            cvm.append(";").append(rset.getString("cvma"));
                        else
                            cvm.append(rset.getString("cvma"));
                    }
                    if (rset.getString("cvmp") != null) {
                        if (cvm.length() > 0)
                            cvm.append(";").append(rset.getString("cvmp"));
                        else
                            cvm.append(rset.getString("cvmp"));
                    }
                    if (rset.getString("cvmn") != null) {
                        if (cvm.length() > 0)
                            cvm.append(";").append(rset.getString("cvmn"));
                        else
                            cvm.append(rset.getString("cvmn"));
                    }

                    if (cvs.length() > 0)
                        ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(ELT_CONTROLLED_TERMS, setCVS(StringUtil.substituteChars(CVSTermBuilder.formatCT(cvs.toString())))));

                    if (cvm.length() > 0)
                        ht.put(Keys.MAJOR_TERMS, new XMLMultiWrapper2(ELT_MAJOR_TERMS, setCVS(StringUtil.substituteChars(CVSTermBuilder.formatCT(cvm.toString())))));
                }

//                //ATM
//                if (rset.getString("APIUT") != null) {
//                    ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(StringUtil.substituteChars(removePoundSign(rset.getString("APIUT"))))));
//                }
                
                // use free language fields for qualifiers fasets
                
                

                if (rset.getString("APICC") != null) {
                    ht.put(Keys.CLASS_CODES_MULTI, new XMLMultiWrapper(ELT_CLASS_CODES, setElementData(StringUtil.substituteChars(rset.getString("APICC")))));
                }

                if (rset.getString("APIATM") != null) {
                    ht.put(Keys.INDEXING_TEMPLATE, new XMLWrapper(Keys.INDEXING_TEMPLATE, replaceBar(formatATM(StringUtil.substituteChars(rset.getString("APIATM"))))));

                    if (rset.getClob("APILT") != null) {

                        ht.put(Keys.LINKED_TERMS_HOLDER, new XMLWrapper(Keys.LINKED_TERMS_HOLDER, LT_MSG));
                    }
                }
                else {
                    String linkedTerms = StringUtil.getStringFromClob(rset.getClob("APILT"));

                    if (rset.getClob("APILT") != null) {
                        if (!linkedTerms.equals("") && !linkedTerms.equalsIgnoreCase("QQ"))
                            ht.put(Keys.LINKED_TERMS, new LinkedTerms(Keys.LINKED_TERMS, getLinkedTerms(StringUtil.substituteChars(removePoundSign(linkedTerms)), Keys.LINKED_SUB_TERM)));

                    }
                }

                EIDoc eiDoc = new EIDoc(did, ht, Detail.FULLDOC_FORMAT);
                eiDoc.exportLabels(true);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(DETAILED_KEYS);
                list.add(eiDoc);
                count++;
            }

        }
        finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }
    private KeyValuePair[] setCRC(String cvs) {
        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer(cvs, ";", false);
        String strToken = null;

        while (st.hasMoreTokens()) {
            strToken = st.nextToken().trim();
            if (strToken.length() > 0) {
                KeyValuePair k = new KeyValuePair(Keys.CAS_REGISTRY_CODE, strToken);
                list.add(k);
            }

        }

        return (KeyValuePair[]) list.toArray(new KeyValuePair[list.size()]);

    }
    public List getLinkedTerms(String lterms, Key key) {

        StringBuffer strXML = new StringBuffer();
        String termToken = null;
        StringTokenizer ltgTokens = null;
        StringTokenizer ltSubsetTerms = null;
        List terms = new ArrayList();

        ltgTokens = new StringTokenizer(lterms, "|", false);
        termToken = null;
        LinkedTerms linkedTerms = null;

        while (ltgTokens.hasMoreTokens()) {
            String ltSubset = ltgTokens.nextToken();

            LinkedTerm linkedTerm = new LinkedTerm();
            linkedTerm.setKey(key);
            linkedTerm.setTerm(ltSubset);
            terms.add(linkedTerm);

        }

        return terms;
    }

    public String formatCONFEDN(String edn) {

        List parms = new ArrayList();
        StringBuffer bufEdn = new StringBuffer();
        perl.split(parms, "/;/", edn);
        for (int i = 0; i < parms.size(); i++) {
            String editor = ((String) parms.get(i)).trim();
            if (editor != null && !editor.equals(""))
                bufEdn.append(editor);
            if (i > 0 && !editor.equals(""))
                bufEdn.append(";");
        }

        return bufEdn.toString();
    }

    public String replaceBar(String str) {
        String result = perl.substitute("s/\\|/<\\/br>/g", str);
        return result;
    }

    public String formatATM(String str) {

        if (str == null)
            return "";

        String template = perl.substitute("s/A\\$\\$/TEMPLATE TITLE: /g", str);
        template = perl.substitute("s/P\\$\\$/TEMPLATE TITLE: /g", template);
        template = perl.substitute("s/\\$\\$/TEMPLATE TITLE: /g", template);
        template = perl.substitute("s/##/NUMBER OF TEMPLATE-GENERATED LINK TERMS: /g", template);
        template = perl.substitute("s/0//g", template);
        StringBuffer sbTemplate = new StringBuffer();

        List lstTokens = new ArrayList();

        perl.split(lstTokens, "/\\|/", template);

        int numOfTplts = 0;

        for (int i = 0; i < lstTokens.size(); i++) {
            String term = (String) lstTokens.get(i);

            if (perl.match("/TEMPLATE TITLE:/i", term))
                ++numOfTplts;
        }

        if (numOfTplts >= 1) {
            for (int i = 0; i < lstTokens.size(); i++) {
                String term = (String) lstTokens.get(i);

                if (perl.match("/TEMPLATE TITLE:/i", term)) {

                    if (i == 0)
                        sbTemplate.append(term);
                    else
                        sbTemplate.append("<br/>").append(term);

                }
                else {

                    if (!perl.match("/NUMBER OF TEMPLATE-GENERATED LINK TERMS:/", term) && !perl.match("/V[0-9]/i", term) && !term.startsWith(">") && !perl.match("/S[0-9]/i", term)) {
                       // sbTemplate.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").
                        sbTemplate.append(term);

                    }
                    else {
                        sbTemplate.append(term);
                    }

                }
                if (i > 0)
                    sbTemplate.append("|");
            }

            String result = sbTemplate.toString();
            result =result.replaceAll("</br>", "<br>");
            result =result.replaceAll("<br/>", "<br>");
            System.out.println(result);
            return result;
        }
        else {
        	template =template.replaceAll("</br>", "<br>");
        	template =template.replaceAll("<br/>", "<br>");
        	System.out.println(template);
            return template;
        }
    }
       
    private List loadLinkedTerms(List listOfDocIDs) throws Exception {

        List list = new ArrayList();
        String INString = buildINString(listOfDocIDs);
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;

        try {
        	
        	System.out.println("loadLinkedTerms");
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryLinkedTerms + INString);
            String results = null;

            DatabaseConfig dconfig = DatabaseConfig.getInstance();
            CVSTermBuilder termBuilder = new CVSTermBuilder();
            if (rset.next()) {

                // Common Fields
                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                String mid = rset.getString("M_ID");

                DocID did = new DocID(mid, dconfig.getDatabase("elt"));

                if (rset.getClob("APILT") != null) {
                    String linkedTerms = StringUtil.getStringFromClob(rset.getClob("APILT"));

//                    if (!linkedTerms.equals("") && !linkedTerms.equalsIgnoreCase("QQ"))
//                    {
//                    	ht.put(EIDoc.LINKED_TERMS, new LinkedTerms(Keys.LINKED_TERMS,termBuilder.formatCT(StringUtil.replaceNonAscii(linkedTerms), Keys.LINKED_SUB_TERM)));
//                    }
                    
                    results =  termBuilder.formatCT(StringUtil.replaceNonAscii(linkedTerms));
                   if (results != null && !results.equalsIgnoreCase("QQ")) 
                   {
       //                 ht.put(Keys.LINKED_TERMS, new LinkedTerms(Keys.LINKED_TERMS, getLinkedTerms(StringUtil.substituteChars(removePoundSign(linkedTerms)), Keys.LINKED_SUB_TERM)));
                       ht.put(Keys.LINKED_TERMS, new XMLWrapper(Keys.LINKED_TERMS, results));
                	  // ht.put(Keys.LINKED_TERMS,results);
                   }
                        

                }

                
                System.out.println("--loadLinkedTerms results::"+results);
                
                
                EIDoc eiDoc = new EIDoc(did, ht, LinkedTermDetail.LINKEDTERM_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(LINKED_TERM_KEYS);
                list.add(eiDoc);
                count++;
            }

        }
        finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }
    /**    This method basically takes list Of DocIDs as Parameter
                                                                                                                                                                                                                                                                                               */
    private List loadCitations(List listOfDocIDs) throws Exception {
        Hashtable oidTable = getDocIDTable(listOfDocIDs);
        List list = new ArrayList();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildINString(listOfDocIDs);
        try {

            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryCitation + INString);
            DatabaseConfig dconfig = DatabaseConfig.getInstance();

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                did.setDatabase(dconfig.getDatabase("elt"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, EltDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, EltDocBuilder.ELT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, EltDocBuilder.ELT_TEXT_COPYRIGHT));

                if (rset.getString("TIE") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.substituteChars(rset.getString("TIE"))));
                }
                else {
                    if (rset.getString("TIF") != null) {
                        ht.put(Keys.TITLE_TRANSLATION, new XMLWrapper(Keys.TITLE_TRANSLATION, StringUtil.substituteChars(rset.getString("TIF"))));
                    }
                }

                if (rset.getString("AUT") != null) {

                    Contributors authors = null;

                    int loadNumber = rset.getInt("LOAD_NUMBER");
                    if (loadNumber >= 200000)
                        authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.substituteChars(ausFormatter.formatAuthors(rset.getString("AUT"))), Keys.AUTHORS));

                    else
                        authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.substituteChars(rset.getString("AUT")), Keys.AUTHORS));

                    ht.put(Keys.AUTHORS, authors);
                }

                //DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
                }

                if (rset.getString("PUB") != null) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.substituteChars(rset.getString("PUB"))));
                }
                if (rset.getString("SO") != null) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.substituteChars(rset.getString("SO"))));
                }
                else {
                    StringBuffer source = new StringBuffer();

                    if (rset.getString("STI") != null) {
                        source.append(formatTitle(StringUtil.substituteChars(rset.getString("STI"))));
                    }
                    else {

                        if (rset.getString("STA") != null) {
                            source.append(formatTitle(StringUtil.substituteChars(rset.getString("STA"))));
                        }

                    } // VO
                    if (rset.getString("VLN") != null) {
                        source.append("  ").append(formatVOLUME(rset.getString("VLN")));
                    }
                    if (rset.getString("ISN") != null) {
                        if (rset.getString("VLN") == null)
                            source.append(" ");
                        source.append("(").append(formatISSUE(rset.getString("ISN"))).append(")");
                    }

                    if (rset.getString("SPD") != null) {
                        source.append(" ").append(getYear(rset.getString("SPD")));
                    }
                    else {
                        if (rset.getString("PYR") != null) {
                            source.append(" ").append(rset.getString("PYR"));
                        }
                    }

                    if (rset.getString("PAG") != null) {
                        source.append(" p. ").append(formatPAGE(rset.getString("PAG")));
                    }

                    if (rset.getString("CNFNAM") != null) {
                        source.append(", ").append(StringUtil.substituteChars(rset.getString("CNFNAM")));
                    }

                    if ((rset.getString("CNFSTD") != null)) {
                        source.append(", ").append(getYear(rset.getString("CNFSTD")));
                    }

                    if (rset.getString("CNFCTY") != null) {
                        source.append(", ").append(StringUtil.substituteChars(rset.getString("CNFCTY")));
                    }

                    if (rset.getString("SECSTI") != null) {
                        source.append(", ").append(StringUtil.substituteChars(rset.getString("SECSTI")));
                    }
                    if (rset.getString("SECVLN") != null) {
                        source.append(" ").append(formatVOLUME(rset.getString("SECVLN")));
                    }
                    if (rset.getString("SECISN") != null) {
                        source.append("(").append(formatISSUE(rset.getString("SECISN"))).append(")");
                    }
                    if (rset.getString("SAN") != null) {
                        source.append(" ").append(formatSAN(rset.getString("SAN")));
                    }
                    if (rset.getString("SECPYR") != null) {
                        source.append(" ").append(formatSECPYR(rset.getString("SECPYR")));
                    }

                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.substituteChars(source.toString())));

                }

                if ((rset.getString("LNA") != null) && (!rset.getString("LNA").equalsIgnoreCase("ENGLISH"))) {
                    String la = rset.getString("LNA");
                    if (la.length() > 1)
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, formatLanguage(StringUtil.substituteChars(rset.getString("LNA")))));
                }

                //ISN
                if (rset.getString("ISSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(formatISSN(rset.getString("ISSN"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(CITATION_KEYS);
                list.add(eiDoc);
                count++;
            }

        }
        finally {
            close(rset);
            close(stmt);
            close(con, broker);

        }

        return list;
    }
    public List getContributors(String strAuthors, Key key) {

        List list = new ArrayList();
        AuthorStream aStream = null;
        DataCleaner dataCleaner = new DataCleaner();
        strAuthors = dataCleaner.cleanEntitiesForDisplay(strAuthors);
        try {
            aStream = new AuthorStream(new ByteArrayInputStream(strAuthors.getBytes()));
            String strToken = null;
            while ((strToken = aStream.readAuthor()) != null) {
                list.add(new Contributor(key, strToken));
            }
        }
        catch (IOException ioe) {
            System.out.println("IOE " + ioe.getMessage());
        }
        finally {
            if (aStream != null)
                try {
                    aStream.close();
                    aStream = null;
                }
                catch (IOException ioe) {
                }
        }

        return list;
    }

    /*
                                     * PRIVATE UTILITY METHODS
                                     * /
                                    
                                     /* This method builds the IN String
                                     * from list of docId objects.
                                     * The select query will get the result set in a reverse way
                                     * So in order to get in correct order we are doing a reverse
                                     * example of return String--(23,22,1,12...so on);
                                     * @param listOfDocIDs
                                     * @return String
                                     * */
    private String buildINString(List listOfDocIDs) {
        StringBuffer sQuery = new StringBuffer();
        sQuery.append("(");
        for (int k = listOfDocIDs.size(); k > 0; k--) {
            DocID doc = (DocID) listOfDocIDs.get(k - 1);
            String docID = doc.getDocID();
            if ((k - 1) == 0) {
                sQuery.append("'" + docID + "'");
            }
            else {
                sQuery.append("'" + docID + "'").append(",");
            }
        } //end of for
        sQuery.append(")");
        return sQuery.toString();
    }
    private String formatSECPYR(String str) {

        if (str == null)
            return str;
        StringBuffer secYr = new StringBuffer();
        secYr.append("(").append(str).append(")");
        return secYr.toString();
    }
    public String getYear(String cnfStd) {

        if (cnfStd == null)
            return "";
        String yr = "";
        if (cnfStd.length() == 8) {

            if (perl.match("/[0-9][0-9][0-9][0-9]/", cnfStd))
                yr = perl.getMatch().toString();
        }
        else {
            yr = cnfStd;
        }

        return yr;
    }
    public String formatSAN(String sVal) {

        StringBuffer sbSan = new StringBuffer();
        sVal = perl.substitute("s/;//g", sVal);
        if (sVal.indexOf(":") > -1) {

            List parms = new ArrayList();
            perl.split(parms, "/:/", sVal);
            if (parms.size() == 2)
                sVal = (String) parms.get(1);
        }

        sbSan.append("abstract no. ").append(sVal);
        return sbSan.toString();
    } /*TS if volume  is null and str is not null print n  */
    private String formatVOLUME(String str) {
        String volume = "";
        if (str == null) {
            return "";
        }

        volume = perl.substitute("s/[v,V,\\.]//g", str);
        volume = perl.substitute("s/\\^/-/g", volume);
        return volume;
    } /*TS if number  is null and str is not null print n   */
    private String formatISSUE(String str) {
        String issue = new String();
        if (str == null) {
            return "";
        }

        issue = perl.substitute("s/[n,N,\\.]//g", str);
        return issue.toString();
    }
    private String formatLanguage(String str) {

        if (str == null)
            return "";
        String la = "";
        StringTokenizer tokens = new StringTokenizer(str, ";", false);
        while (tokens.hasMoreTokens()) {
            la = tokens.nextToken();
            break;
        }
        return la;
    }
    private String formatPAGE(String str) {
        String issue = "";
        if (str == null) {
            return "";
        }

        String s1 = perl.substitute("s/[p,P,\\.]//g", str);
        String s2 = perl.substitute("s/,/ /g", s1);
        String s3 = perl.substitute("s/\\./ /g", s2);
        return s3;
    } /* TS , end  */
    private String checkAbstract(String s) {
        if (s == null) {
            return "";
        }

        if (s.length() < 100) {
            return "";
        }

        return s;
    }
    public String formatTitle(String title) {

        StringBuffer sbTitle = new StringBuffer();
        sbTitle.append("<i>").append(title).append("</i>");
        return sbTitle.toString();
    }

    private String formatDT(String docType) {

    	String mapdt = null;
        if (docType != null && !docType.equals("")) 
        {        	
        	mapdt =(String) MAPDOCTYPES.get(docType.toUpperCase().trim());
        }

        return mapdt;
    }
    public String formatISSN(String issn) {

        String sVal = null;
        if (issn == null)
            return "";
        sVal = perl.substitute("s/-//g", issn);
        return sVal;
    }

    private Hashtable getDocIDTable(List listOfDocIDs) {
        Hashtable h = new Hashtable();
        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }

        return h;
    }
    /** 
    	* @return
    	*/
    private void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    /** 
    	* @return
    	*/
    private void close(Statement stmt) {

        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** 
    	* @return
    	*/
    private void close(Connection conn, ConnectionBroker broker) {

        try {
            if (conn != null) {
                broker.replaceConnection(conn, DatabaseConfig.SEARCH_POOL);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    


} //End Of EltDocBuilder
