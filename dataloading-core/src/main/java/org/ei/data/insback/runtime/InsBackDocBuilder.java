package org.ei.data.insback.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.common.AuthorStream;
import org.ei.common.DataCleaner;
import org.ei.domain.Abstract;
import org.ei.domain.Citation;
import org.ei.domain.Classifications;
import org.ei.domain.Contributor;
import org.ei.domain.Contributors;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Detail;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.DocumentBuilderException;
import org.ei.domain.EIDoc;
import org.ei.domain.ElementDataMap;
import org.ei.domain.FullDoc;
import org.ei.domain.Issue;
import org.ei.domain.Key;
import org.ei.domain.KeyValuePair;
import org.ei.domain.Keys;
import org.ei.domain.PageRange;
import org.ei.domain.RIS;
import org.ei.domain.Volume;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLMultiWrapper2;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.util.StringUtil;

public class InsBackDocBuilder implements DocumentBuilder {
    public static String INS_TEXT_COPYRIGHT = "Copyright 2007, IEE";
    public static String INS_HTML_COPYRIGHT = "Copyright 2007, IEE";
    public static String PROVIDER_TEXT = "Inspec";
    public static String TRANS_SEE_DETAILED = "For translation info., see Detailed Record / Links";
    private static final Key INS_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Inspec controlled terms");
    private static final Key INS_CLASS_CODES = new Key(Keys.CLASS_CODES, "Inspec classification codes");
    private static final Key ORIGINAL_INS_CONTROLLED_TERMS = new Key(Keys.ORIGINAL_CONTROLLED_TERMS, "Inspec original controlled terms");
    private static final Key ORIGINAL_INS_CLASS_CODES = new Key(Keys.ORIGINAL_CLASS_CODES, "Inspec original classification codes");
    public static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.INVENTORS, Keys.REPORT_NUMBER, Keys.NO_SO,
        Keys.PUBLISHER, Keys.I_PUBLISHER, Keys.SOURCE, Keys.PATASSIGN, Keys.PATAPPNUM, Keys.PATNUM, Keys.PATCOUNTRY, Keys.PAPER_NUMBER, Keys.p_PAGE_RANGE,
        Keys.PAGE_RANGE_pp, Keys.VOLISSUE, Keys.TRANSLATION_SERIAL_TITLE, Keys.SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.ISSUE_DATE, Keys.LANGUAGE, Keys.DOI,
        Keys.CODEN, Keys.ISSN, Keys.IMAGES, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    public static final Key[] ABSTRACT_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.INVENTORS, Keys.EDITOR_AFFS, Keys.AUTHOR_AFFS,
        Keys.VOLISSUE, Keys.NO_SO, Keys.SOURCE, Keys.REPORT_NUMBER, Keys.PATASSIGN, Keys.PATNUM, Keys.PATCOUNTRY, Keys.SERIAL_TITLE,
        Keys.TRANSLATION_SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.ISSUE_DATE, Keys.LANGUAGE, Keys.p_PAGE_RANGE, Keys.PAGE_RANGE_pp, Keys.PUBLISHER,
        Keys.I_PUBLISHER, Keys.PROVIDER, INS_CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.CONFERENCE_NAME, Keys.CONF_DATE, Keys.MEETING_LOCATION,
        Keys.SPONSOR, Keys.PAPER_NUMBER, Keys.PAGE_COUNT, Keys.PUB_PLACE, Keys.CONF_CODE, Keys.COUNTRY_OF_PUB, Keys.DOI, Keys.ABSTRACT2, Keys.IMAGES,
        Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    public static final Key[] DETAILED_KEYS = { Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.TITLE_TRANSLATION, Keys.AUTHORS, Keys.INVENTORS, Keys.EDITORS,
        Keys.PATASSIGN, Keys.PATNUM, Keys.NO_SO, Keys.ISSUING_ORG, Keys.REPORT_NUMBER, Keys.AUTHOR_AFFS, Keys.EDITOR_AFFS, Keys.SOURCE, Keys.SERIAL_TITLE,
        Keys.ABBRV_SERIAL_TITLE, Keys.VOLUME, Keys.ISSUE, Keys.PUBLICATION_DATE, Keys.ISSUE_DATE, Keys.PART_NUMBER, Keys.PAGE_RANGE, Keys.LANGUAGE,
        Keys.ASOURCES, Keys.TRANSLATION_SERIAL_TITLE, Keys.TRANSLATION_ABBREVIATED_SERIAL_TITLE, Keys.TRANSLATION_PUBLICATION_DATE, Keys.TRANSLATION_PAGES,
        Keys.TRANSLATION_COUNTRY_OF_PUB, Keys.TSOURCES, Keys.DOC_TYPE, Keys.PATCOUNTRY, Keys.COUNTRY_OF_PUB, Keys.CONFERENCE_NAME, Keys.CONF_DATE,
        Keys.MEETING_LOCATION, Keys.CONF_CODE, Keys.SPONSOR, Keys.PUB_PLACE, Keys.PUBLISHER, Keys.PUB_LOCATION, Keys.ABSTRACT2, Keys.OTHER_INFO, Keys.IMAGES,
        Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.CLASS_CODES, Keys.ORIGINAL_CONTROLLED_TERMS, Keys.ORIGINAL_CLASS_CODES, Keys.TREATMENTS,
        Keys.DISCIPLINES, Keys.DOI, Keys.DOCID, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.PROVIDER };

    private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_LA, Keys.RIS_M1, Keys.RIS_N1, Keys.RIS_Y1, Keys.RIS_Y2, Keys.RIS_AUS, Keys.RIS_A2,
        Keys.RIS_SE, Keys.RIS_T1, Keys.RIS_T2, Keys.RIS_AD, Keys.RIS_BT, Keys.RIS_JO, Keys.RIS_EDS, Keys.RIS_VL, Keys.RIS_IS, Keys.RIS_PY, Keys.RIS_AN,
        Keys.RIS_SP, Keys.RIS_EP, Keys.RIS_MD, Keys.RIS_CY, Keys.RIS_PB, Keys.RIS_AB2, Keys.RIS_CVS, Keys.RIS_FLS, Keys.RIS_DO };

    private static final Key[] XML_KEYS = { Keys.DOCID, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.EDITORS, Keys.AUTHORS,
        Keys.INVENTORS, Keys.REPORT_NUMBER, Keys.NO_SO, Keys.PUBLISHER, Keys.SOURCE, Keys.PATASSIGN, Keys.PATAPPNUM, Keys.PATNUM, Keys.PATPUBDATE,
        Keys.PAPER_NUMBER, Keys.PAGE_RANGE, Keys.p_PAGE_RANGE, Keys.PAGE_RANGE_pp, Keys.PAGE_COUNT, Keys.VOLISSUE, Keys.TRANSLATION_SERIAL_TITLE,
        Keys.SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.ISSUE_DATE, Keys.LANGUAGE, Keys.CONTROLLED_TERMS, Keys.ISBN, Keys.I_PUBLISHER, Keys.DOI, Keys.CODEN,
        Keys.ISSN, Keys.IMAGES };
    private static final String AUDELIMITER = new String(new char[] { 30 });
    private static final String IDDELIMITER = new String(new char[] { 31 });

    private Database database;
    private Perl5Util perl = new Perl5Util();

    private static String queryCitation = "select M_ID,RTYPE,CPR,ANUM,DOI,LA,IPN,NPL1,PARTNO,CPUB,PPUB,PUB,THLP,EDS,AUS,TI,TC,IORG,RNUM,PDATE,FJT,AJT,OJT,VOL,ISS,VOLISS,FTTJ,PAS,FDATE,PNUM,FIG,LOAD_NUMBER,DOI,CPAT,PYR from ibf_master where M_ID IN ";
    private static String queryXMLCitation = "select M_ID,RTYPE,CPR,ANUM,LA,IPN,NPL1,PARTNO,CPUB,PPUB,PUB,THLP,EDS,AUS,TI,IORG,RNUM,PDATE,FJT,AJT,OJT,VOL,ISS,VOLISS,FTTJ,PAS,FDATE,PNUM, LOAD_NUMBER,DOI,CVS,CPAT,PYR from ibf_master where M_ID IN ";
    private static String queryAbstracts = "select M_ID,RTYPE,CPR,ANUM,TI,AUS,AJT,FJT,OJT,VOL,ISS,VOLISS,PDATE,PARTNO,IPN,NPL1,LA,TC,CDATE,CEDATE,CLOC,SORG,PUB,PPUB,CPUB,AB,FIG,FTTJ,CPUBT,CVS,FLS,EDS,THLP,IORG,RNUM,PAS,CPAT,PNUM,FDATE,DOI,LOAD_NUMBER,PYR from ibf_master where M_ID IN ";
    private static String queryDetailed = "select M_ID,RTYPE,CPR,ANUM,TI,AUS,EDS,AJT,OJT,FJT,THLP,VOL,ISS,VOLISS,PDATE,IPN,NPL1,LA,TC,CDATE,CEDATE,CLOC,SORG,PUB,PPUB,CPUB,FTTJ,TTJ,VOLISST,TDATE,IPNT,CPUBT,PARTNO,RNUM,IORG,FDATE,PAS,CPAT,PNUM,AB,OINFO,FIG,CVS,OCVS,FLS,CLS,OCLS,UDC,THLP,DOI,SOURCE,TSOURCE,LOAD_NUMBER,PYR from ibf_master where M_ID IN ";
    private static String queryPreview = "select M_ID, AB from ibf_master where M_ID IN ";

    public DocumentBuilder newInstance(Database database) {
        return new InsBackDocBuilder(database);
    }

    public InsBackDocBuilder() {
    }

    public InsBackDocBuilder(Database database) {
        this.database = database;
    }

    /**
     * This method takes a list of DocID objects and dataFormat and returns a List of EIDoc Objects based on a particular dataformat @ param listOfDocIDs @
     * param dataFormat @ return List --list of EIDoc's @ exception DocumentBuilderException
     */
    public List<EIDoc> buildPage(List<DocID> listOfDocIDs, String dataFormat) throws DocumentBuilderException {
        List<EIDoc> l = null;
        if (dataFormat.equals(Citation.CITATION_FORMAT)) {
            l = loadCitations(listOfDocIDs);
        } else if (dataFormat.equals(Abstract.ABSTRACT_FORMAT)) {
            l = loadAbstracts(listOfDocIDs);
        } else if (dataFormat.equals(FullDoc.FULLDOC_FORMAT)) {
            l = loadDetailed(listOfDocIDs);
        } else if (dataFormat.equalsIgnoreCase("RIS")) {
            l = loadRIS(listOfDocIDs);
        } else if (dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT)) {
            l = loadXMLCitations(listOfDocIDs);
        } else if (Citation.CITATION_PREVIEW.equals(dataFormat)) {
            l = loadPreviewData(listOfDocIDs);
        }

        return l;
    }

    private List<EIDoc> loadPreviewData(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        // Perl5Util perl = new Perl5Util();
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

        List<EIDoc> list = new ArrayList<EIDoc>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;

        try {
            String INString = buildINString(listOfDocIDs);
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryPreview + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_PREVIEW);

                ht.put(Keys.DOCID, (DocID) oidTable.get(rset.getString("M_ID")));
                String abs = hasAbstract(rset);

                if (StringUtils.isNotBlank(abs)) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                list.add(eiDoc);

            }
        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {

            if (rset != null) {
                if (rset != null) {
                    try {
                        rset.close();
                    } catch (Exception e1) {
                        throw new DocumentBuilderException("database connection pool problem", e1);
                    }
                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (Exception sqle) {
                        throw new DocumentBuilderException("database connection pool problem", sqle);
                    }
                }

                if (con != null) {
                    try {
                        broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                    } catch (Exception cpe) {
                        throw new DocumentBuilderException("database connection pool problem", cpe);
                    }
                }
            }
        }
        return list;
    }

    private List<EIDoc> loadAbstracts(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Perl5Util perl = new Perl5Util();
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

        List<EIDoc> list = new ArrayList<EIDoc>();
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

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();

                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));

                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                if (rset.getString("CPR") != null) {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, rset.getString("CPR")));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, rset.getString("CPR")));
                } else {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, INS_HTML_COPYRIGHT));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, INS_TEXT_COPYRIGHT));
                }

                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ANUM")));
                }

                if (rset.getString("FJT") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("FJT")));
                }

                if (rset.getString("AJT") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("AJT")));
                }

                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                }

                if (rset.getString("NPL1") != null) {
                    String pageCount = rset.getString("NPL1").replaceAll("p", " ");
                    ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, pageCount));
                }

                if (rset.getString("IPN") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(rset.getString("IPN"), perl));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if (rset.getString("TI") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI"));
                }
                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));

                String strRTYPE = rset.getString("RTYPE");
                ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, replaceDTNullWithEmptyString(rset.getString("RTYPE"))));

                if (strRTYPE.equals("80")) {
                    if (rset.getString("AUS") != null) {
                        Contributors inventors = new Contributors(Keys.INVENTORS, getContributors(rset.getString("AUS"), Keys.INVENTORS));
                        ht.put(Keys.INVENTORS, inventors);
                    }
                } else {
                    if (rset.getString("AUS") != null) {

                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUS"), Keys.AUTHORS));
                        ht.put(Keys.AUTHORS, authors);
                    } else {
                        if (rset.getString("EDS") != null) {
                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(rset.getString("EDS"), Keys.EDITORS));
                            ht.put(Keys.EDITORS, editors);

                        }
                    }
                }
                String strFJT;

                if (rset.getString("FJT") != null) {
                    strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("FJT"));
                } else {
                    strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("OJT"));
                }
                String strTHLP = StringUtil.replaceNullWithEmptyString(rset.getString("THLP"));

                if ((strRTYPE.equals("21") || strRTYPE.equals("22") || strRTYPE.equals("23"))
                    || (strRTYPE.equals("51") || strRTYPE.equals("52") || strRTYPE.equals("53"))
                    || (strRTYPE.equals("61") || strRTYPE.equals("62") || strRTYPE.equals("63"))) {
                    if (strFJT != null && (!strFJT.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strFJT));
                    }

                    if (rset.getString("VOL") != null || rset.getString("ISS") != null) {
                        String strVolIss = StringUtil.EMPTY_STRING;
                        if (rset.getString("VOL") != null) {
                            ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, rset.getString("VOL"), perl));
                        }
                        if (rset.getString("ISS") != null) {
                            ht.put(Keys.ISSUE, new Issue(rset.getString("ISS"), perl));
                        }
                        if (rset.getString("VOL") != null)
                            strVolIss = "v " + StringUtil.replaceNullWithEmptyString(rset.getString("VOL"));
                        if (rset.getString("ISS") != null) {
                            if (strVolIss.equals(StringUtil.EMPTY_STRING))
                                strVolIss = strVolIss + "n " + StringUtil.replaceNullWithEmptyString(rset.getString("ISS"));
                            else
                                strVolIss = strVolIss + ", n " + StringUtil.replaceNullWithEmptyString(rset.getString("ISS"));
                        }

                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                    }

                    if (rset.getString("PDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PDATE"))));
                    }

                    if (rset.getString("FTTJ") != null) {
                        ht.put(Keys.TRANSLATION_SERIAL_TITLE, new XMLWrapper(Keys.TRANSLATION_SERIAL_TITLE, TRANS_SEE_DETAILED));
                    }

                } else if ((strRTYPE.equals("30")) || (strRTYPE.equals("40")) || (strRTYPE.equals("50"))
                    || (strRTYPE.equals("60") && (strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    if (strTHLP != null && (!strTHLP.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strTHLP));
                    }

                    if (strTHLP.equals(StringUtil.EMPTY_STRING) && rset.getString("PUB") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PUB"))));
                    }
                    if (rset.getString("PDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PDATE"))));
                    }

                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {
                    if (strTHLP != null && (!strTHLP.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strTHLP));
                    }

                    if (rset.getString("IORG") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("IORG"))));
                    } else if (rset.getString("PUB") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PUB"))));
                    }

                    if (rset.getString("PDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PDATE"))));
                    } else if (rset.getString("FDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("FDATE"))));
                    }

                    if (rset.getString("RNUM") != null) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("RNUM"))));
                    }

                } else if (strRTYPE.equals("80")) {

                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                    // RTYPE='08'
                    if (rset.getString("PAS") != null) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, StringUtil.replaceNullWithEmptyString(rset.getString("PAS"))));
                    }
                    if (rset.getString("PNUM") != null) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, StringUtil.replaceNullWithEmptyString(rset.getString("PNUM"))));
                    }
                    if (rset.getString("CPAT") != null) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, StringUtil.replaceNullWithEmptyString(rset.getString("CPAT"))));
                    }
                    if (rset.getString("CPUB") != null) {
                        ht.put(Keys.COUNTRY_OF_PUB, new XMLWrapper(Keys.COUNTRY_OF_PUB, StringUtil.replaceNullWithEmptyString(rset.getString("CPUB"))));
                    }
                }

                if (rset.getString("PARTNO") != null) {
                    String PartNumber = getPartNumber(rset.getString("PARTNO"));
                    String partNo = "pt. ".concat(StringUtil.replaceNullWithEmptyString(PartNumber));
                    ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, partNo));
                }

                String strPages = StringUtil.EMPTY_STRING;

                if (rset.getString("IPN") != null) {
                    ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("IPN"))));
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("IPN"));
                } else if (rset.getString("NPL1") != null) {
                    ht.put(Keys.PAGE_RANGE_pp, new XMLWrapper(Keys.PAGE_RANGE_pp, StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"))));
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"));
                }

                if ((rset.getString("LA") != null) && (!rset.getString("LA").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, (StringUtil.replaceNullWithEmptyString(rset.getString("LA")))));

                }

                if (rset.getString("TC") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("TC"))));
                } else {

                    if (strRTYPE.equals("60") || strRTYPE.equals("61") || strRTYPE.equals("62") || strRTYPE.equals("63") && rset.getString("THLP") != null) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("THLP"))));
                    } else if (strRTYPE.equals("50") || strRTYPE.equals("51") || strRTYPE.equals("52") || strRTYPE.equals("53") && rset.getString("TI") != null) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                    }
                }

                if (rset.getString("CDATE") != null) {
                    ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("CDATE"))));
                }
                if (rset.getString("CDATE") != null && rset.getString("CEDATE") != null) {
                    ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, rset.getString("CDATE") + " - " + rset.getString("CEDATE")));
                }

                if (rset.getString("CLOC") != null) {
                    ht.put(Keys.MEETING_LOCATION, new XMLWrapper(Keys.MEETING_LOCATION, StringUtil.replaceNullWithEmptyString(rset.getString("CLOC"))));
                }
                if (rset.getString("SORG") != null) {
                    ht.put(Keys.SPONSOR, new XMLWrapper(Keys.SPONSOR, (StringUtil.replaceNullWithEmptyString(rset.getString("SORG")))));
                }
                if (rset.getString("PUB") != null) {
                    List<String> lstPub = new ArrayList<String>();
                    lstPub.add(rset.getString("PUB"));

                    if (rset.getString("PPUB") != null) {
                        lstPub.add(rset.getString("PPUB"));
                    } else if (rset.getString("CPUB") != null) {
                        lstPub.add(rset.getString("CPUB"));
                    }
                    ht.put(Keys.I_PUBLISHER, new XMLWrapper(Keys.I_PUBLISHER, StringUtil.join(lstPub, ", ")));
                    lstPub.clear();
                    lstPub = null;
                } else if (rset.getString("PPUB") != null) {
                    ht.put(Keys.PUB_PLACE, new XMLWrapper(Keys.PUB_PLACE, rset.getString("PPUB")));
                } else if (rset.getString("CPUB") != null) {
                    ht.put(Keys.COUNTRY_OF_PUB, new XMLWrapper(Keys.COUNTRY_OF_PUB, rset.getString("CPUB")));
                }

                // DOI
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
                }

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT2, new AB2(Keys.ABSTRACT2, abs));
                }

                if (rset.getString("FIG") != null) {

                    ht.put(Keys.IMAGES, new Images(Keys.IMAGES, rset.getString("FIG")));
                }

                if (rset.getString("CVS") != null) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(INS_CONTROLLED_TERMS, setCVS(rset.getString("CVS"))));
                }

                // FLS - added to ABSTRACT view in Baja build
                if (rset.getString("FLS") != null) {
                    ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(rset.getString("FLS"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Abstract.ABSTRACT_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(true);
                eiDoc.setOutputKeys(ABSTRACT_KEYS);
                list.add(eiDoc);
                count++;
            } // while

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception ine) {
                    ine.printStackTrace();
                }
            }
        } // try/finally

        return list;

    } // loadAbstract

    /**
     * This method basically takes list Of DocIDs as Parameter This list of Docids use buildINString() method to build the required IN clause String.This is
     * appended to sql String The resultSet so obtained by executing the sql,is iterated, to build Detailed EIDoc objects,which are then added to EIdocumentList
     *
     * @param listOfDocIDs
     * @return EIDocumentList
     * @exception Exception
     */
    private List<EIDoc> loadDetailed(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();

        List<EIDoc> list = new ArrayList<EIDoc>();
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

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();

                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                }

                if (rset.getString("IPN") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(rset.getString("IPN"), perl));
                }

                if (rset.getString("CPR") != null) {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, rset.getString("CPR")));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, rset.getString("CPR")));
                } else {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, INS_HTML_COPYRIGHT));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, INS_TEXT_COPYRIGHT));
                }

                String strRTYPE = rset.getString("RTYPE");
                ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, replaceDTNullWithEmptyString(rset.getString("RTYPE"))));

                // ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, replaceTYwithRIScode(rset.getString("NRTYPE"))));

                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ANUM")));
                }
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("TI")));
                }

                if (rset.getString("FJT") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("FJT")));
                }

                if (rset.getString("AJT") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("AJT")));
                }

                if (rset.getString("PUB") != null) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PUB")));
                }

                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUS"), Keys.AUTHORS));

                    ht.put(Keys.AUTHORS, authors);
                } else {
                    if (rset.getString("EDS") != null) {
                        Contributors editors = new Contributors(Keys.EDITORS, getContributors(rset.getString("EDS"), Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);
                    }
                }

                String strFJT;
                if (rset.getString("FJT") != null) {
                    strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("FJT"));
                } else {
                    strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("OJT"));
                }

                String strAJT = StringUtil.replaceNullWithEmptyString(rset.getString("AJT"));
                String strTHLP = StringUtil.replaceNullWithEmptyString(rset.getString("THLP"));

                if ((strRTYPE.equals("21") || strRTYPE.equals("22") || strRTYPE.equals("23"))
                    || (strRTYPE.equals("51") || strRTYPE.equals("52") || strRTYPE.equals("53"))
                    || (strRTYPE.equals("61") || strRTYPE.equals("62") || strRTYPE.equals("63"))) {
                    if (strFJT != null && (!strFJT.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(strFJT)));
                    }
                    if (strAJT != null && (!strAJT.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(strAJT)));
                    }

                    if (rset.getString("PUB") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PUB"))));
                    }

                    // VO
                    if (rset.getString("VOL") != null) {
                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, rset.getString("VOL"), perl));
                    }
                    // ISS
                    if (rset.getString("ISS") != null) {
                        ht.put(Keys.ISSUE, new Issue(rset.getString("ISS"), perl));
                    }
                    if (rset.getString("FTTJ") != null) {
                        ht.put(Keys.TRANSLATION_SERIAL_TITLE,
                            new XMLWrapper(Keys.TRANSLATION_SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("FTTJ"))));
                    }
                    if (rset.getString("TTJ") != null) {
                        ht.put(Keys.TRANSLATION_ABBREVIATED_SERIAL_TITLE,
                            new XMLWrapper(Keys.TRANSLATION_ABBREVIATED_SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("TTJ"))));
                    }

                    if (rset.getString("TDATE") != null) {
                        ht.put(Keys.TRANSLATION_PUBLICATION_DATE,
                            new XMLWrapper(Keys.TRANSLATION_PUBLICATION_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("TDATE"))));
                    }
                    if (rset.getString("IPNT") != null) {
                        ht.put(Keys.TRANSLATION_PAGES, new PageRange(Keys.TRANSLATION_PAGES, rset.getString("IPNT"), perl));
                    }
                    if (rset.getString("CPUBT") != null) {
                        ht.put(Keys.TRANSLATION_COUNTRY_OF_PUB,
                            new XMLWrapper(Keys.TRANSLATION_COUNTRY_OF_PUB, StringUtil.replaceNullWithEmptyString(rset.getString("CPUBT"))));
                    }

                } else if ((strRTYPE.equals("30")) || (strRTYPE.equals("40")) || (strRTYPE.equals("50")) || (strRTYPE.equals("60"))) {
                    if (strTHLP != null && (!strTHLP.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strTHLP));
                    }
                    if (rset.getString("PUB") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PUB"))));
                    }

                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {
                    if (strAJT != null && (!strAJT.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));
                    }

                    if (rset.getString("IORG") != null) {

                        ht.put(Keys.ISSUING_ORG, new XMLWrapper(Keys.ISSUING_ORG, StringUtil.replaceNullWithEmptyString(rset.getString("IORG"))));
                    }
                    // jam 5/12/2003 Thes release testing - rule incorrectly copied from Citation
                    // IORG and PUB both show in detailed!!!
                    if (rset.getString("PUB") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PUB")));
                    }
                    if (rset.getString("RNUM") != null) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, rset.getString("RNUM")));
                    }

                } else if (strRTYPE.equals("08")) {
                    if (rset.getString("PAS") != null) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, StringUtil.replaceNullWithEmptyString(rset.getString("PAS"))));
                    }
                    if (rset.getString("PNUM") != null) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, StringUtil.replaceNullWithEmptyString(rset.getString("PNUM"))));
                    }
                    if (rset.getString("CPAT") != null) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, StringUtil.replaceNullWithEmptyString(rset.getString("CPAT"))));
                    }
                    if (rset.getString("CPUB") != null) {
                        ht.put(Keys.COUNTRY_OF_PUB, new XMLWrapper(Keys.COUNTRY_OF_PUB, StringUtil.replaceNullWithEmptyString(rset.getString("CPUB"))));
                    }
                }
                if (rset.getString("PDATE") != null) {
                    ht.put(Keys.PUBLICATION_DATE, new XMLWrapper(Keys.PUBLICATION_DATE, rset.getString("PDATE")));
                }

                String strPages = StringUtil.EMPTY_STRING;
                if (rset.getString("IPN") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("IPN"));
                } else if (rset.getString("NPL1") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"));
                }
                if ((strPages != null) && !(strPages.equals(StringUtil.EMPTY_STRING))) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));

                    // Strip out and store the start page and end page
                    if (perl.match("/(\\d+)/", strPages)) {
                        ht.put(Keys.START_PAGE, new XMLWrapper(Keys.START_PAGE, perl.group(0).toString()));
                        if (perl.match("/(\\d+)/", perl.postMatch())) {
                            ht.put(Keys.END_PAGE, new XMLWrapper(Keys.END_PAGE, perl.group(0).toString()));
                        }
                    }
                }

                // LA
                if (rset.getString("LA") != null) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, replaceInvisibleChar(StringUtil.replaceNullWithEmptyString(rset.getString("LA")))));
                }

                if (rset.getString("TC") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("TC")));
                } else {

                    if (strRTYPE.equals("60") || strRTYPE.equals("61") || strRTYPE.equals("62") || strRTYPE.equals("63")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("THLP")));
                    } else if (strRTYPE.equals("50") || strRTYPE.equals("51") || strRTYPE.equals("52") || strRTYPE.equals("53")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("TI")));
                    }
                }
                if (rset.getString("CDATE") != null) {
                    ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("CDATE"))));
                }
                if (rset.getString("CDATE") != null && rset.getString("CEDATE") != null) {
                    ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, rset.getString("CDATE") + " - " + rset.getString("CEDATE")));
                }
                if (rset.getString("CLOC") != null) {
                    ht.put(Keys.MEETING_LOCATION, new XMLWrapper(Keys.MEETING_LOCATION, StringUtil.replaceNullWithEmptyString(rset.getString("CLOC"))));
                }
                if (rset.getString("SORG") != null) {
                    ht.put(Keys.SPONSOR,
                        new XMLWrapper(Keys.SPONSOR, StringUtil.replaceNullWithEmptyString(StringUtil.replaceNullWithEmptyString(rset.getString("SORG")))));
                }

                if (rset.getString("PPUB") != null) {
                    ht.put(Keys.PUB_PLACE, new XMLWrapper(Keys.PUB_PLACE, rset.getString("PPUB")));
                } else if (rset.getString("CPUB") != null) {
                    ht.put(Keys.PUB_LOCATION, new XMLWrapper(Keys.PUB_LOCATION, rset.getString("CPUB")));
                }

                if (!strRTYPE.equals("08")) {
                    String strPartNo = StringUtil.replaceNullWithEmptyString(rset.getString("PARTNO"));
                    if (!strPartNo.equals(StringUtil.EMPTY_STRING)) {
                        if (perl.match("/(\\d+)/", strPartNo)) {
                            ht.put(Keys.PART_NUMBER, new XMLWrapper(Keys.PART_NUMBER, perl.group(0).toString()));
                        }
                    }
                }

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT2, new AB2(Keys.ABSTRACT2, abs));
                }

                if (rset.getString("OINFO") != null) {
                    ht.put(Keys.OTHER_INFO, new XMLWrapper(Keys.OTHER_INFO, StringUtil.replaceNullWithEmptyString(rset.getString("OINFO"))));
                }

                if (rset.getString("FIG") != null) {
                    ht.put(Keys.IMAGES, new Images(Keys.IMAGES, rset.getString("FIG")));
                }

                // CVS
                if (rset.getString("CVS") != null) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(INS_CONTROLLED_TERMS, setElementData(rset.getString("CVS"))));
                }
                // FLS
                if (rset.getString("FLS") != null) {
                    ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(rset.getString("FLS"))));
                }

                // CLS
                if (rset.getString("CLS") != null) {
                    ht.put(Keys.CLASS_CODES, new Classifications(INS_CLASS_CODES, setElementData(rset.getString("CLS")), this.database));
                }

                // OCVS
                if (rset.getString("OCVS") != null) {
                    ht.put(Keys.ORIGINAL_CONTROLLED_TERMS, new OCVS(ORIGINAL_INS_CONTROLLED_TERMS, rset.getString("OCVS")));
                }

                // OCLS
                if (rset.getString("OCLS") != null) {
                    ht.put(Keys.ORIGINAL_CLASS_CODES, new OCLS(ORIGINAL_INS_CLASS_CODES, rset.getString("OCLS")));
                }

                // CLS
                if (rset.getString("CLS") != null) {
                    ht.put(Keys.DISCIPLINES, new XMLMultiWrapper(Keys.DISCIPLINES, setDisciplineElementData(rset.getString("CLS"))));
                }

                if (rset.getString("SOURCE") != null) {
                    ht.put(Keys.ASOURCES, new ASources(Keys.ASOURCES, rset.getString("SOURCE")));
                }
                if (rset.getString("TSOURCE") != null) {
                    ht.put(Keys.TSOURCES, new XMLWrapper(Keys.TSOURCES, StringUtil.replaceNullWithEmptyString(rset.getString("TSOURCE"))));
                }

                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, StringUtil.replaceNullWithEmptyString(rset.getString("DOI"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Detail.FULLDOC_FORMAT);
                eiDoc.exportLabels(true);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(DETAILED_KEYS);
                list.add(eiDoc);
                count++;
            } // while

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        } // try/finally

        return list;

    } // loadDetailed

    private List<EIDoc> loadRIS(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();

        List<EIDoc> list = new ArrayList<EIDoc>();
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

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();

                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                // ht.put(Keys.DOCID, did);

                // ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                if (rset.getString("CPR") != null) {
                    ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, StringUtil.replaceNullWithEmptyString(rset.getString("CPR"))));
                } else {
                    ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, InsBackDocBuilder.INS_TEXT_COPYRIGHT));
                }

                if (rset.getString("LA") != null) {
                    ht.put(Keys.RIS_LA, new XMLWrapper(Keys.RIS_LA, rset.getString("LA")));
                }

                String strRTYPE = rset.getString("RTYPE");

                if (rset.getString("TI") != null) {
                    ht.put(Keys.RIS_T1, new XMLWrapper(Keys.RIS_T1, rset.getString("TI")));
                }

                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUS"), Keys.AUTHORS));

                    ht.put(Keys.RIS_AUS, authors);
                } else {
                    if (rset.getString("EDS") != null) {
                        Contributors editors = new Contributors(Keys.EDITORS, getContributors(rset.getString("EDS"), Keys.EDITORS));

                        ht.put(Keys.RIS_EDS, editors);
                    }
                }

                String strFJT;
                if (rset.getString("FJT") != null) {
                    strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("FJT"));
                } else {
                    strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("OJT"));
                }

                String strAJT = StringUtil.replaceNullWithEmptyString(rset.getString("AJT"));
                String strTHLP = StringUtil.replaceNullWithEmptyString(rset.getString("THLP"));

                ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, replaceTYwithRIScode(rset.getString("RTYPE"))));

                if (strRTYPE.equals("40") || strRTYPE.equals("12")) {
                    ht.put(Keys.RIS_T2, new XMLWrapper(Keys.RIS_T2, strTHLP));
                }

                if ((strRTYPE.equals("21") || strRTYPE.equals("22") || strRTYPE.equals("23"))
                    || (strRTYPE.equals("51") || strRTYPE.equals("52") || strRTYPE.equals("53"))
                    || (strRTYPE.equals("61") || strRTYPE.equals("62") || strRTYPE.equals("63"))) {
                    if (strFJT != null) {
                        ht.put(Keys.RIS_JO, new XMLWrapper(Keys.RIS_JO, strFJT));
                    }

                    if (rset.getString("VOL") != null) {
                        ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, StringUtil.replaceNullWithEmptyString(rset.getString("VOL"))));
                    }
                    if (rset.getString("ISS") != null) {
                        ht.put(Keys.RIS_IS, new Issue(StringUtil.replaceNullWithEmptyString(rset.getString("ISS")), perl));
                    }

                    // Error - overwriting T1 - FTTJ not defined for RIS
                    // ht.put("T1",StringUtil.replaceNullWithEmptyString(rset.getString("FTTJ")));

                } else if ((strRTYPE.equals("30")) || (strRTYPE.equals("40")) || (strRTYPE.equals("50")) || (strRTYPE.equals("60"))) {
                    if (rset.getString("PUB") != null) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, StringUtil.replaceNullWithEmptyString(rset.getString("PUB"))));
                    }
                    if (rset.getString("PARTNO") != null) {
                        ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, StringUtil.replaceNullWithEmptyString(rset.getString("PARTNO"))));
                    }

                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {

                    if (rset.getString("IORG") != null) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, replaceInvisibleChar(rset.getString("IORG"))));
                    } else if (rset.getString("PUB") != null) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, StringUtil.replaceNullWithEmptyString(rset.getString("PUB"))));
                    }
                    if (rset.getString("RNUM") != null) {
                        ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, StringUtil.replaceNullWithEmptyString(rset.getString("RNUM"))));
                    }

                } else if (strRTYPE.equals("80")) {
                    if (rset.getString("PNUM") != null) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, StringUtil.replaceNullWithEmptyString(rset.getString("PNUM"))));
                    }
                    if (rset.getString("PAS") != null) {
                        ht.put(Keys.RIS_A2, new XMLWrapper(Keys.RIS_A2, StringUtil.replaceNullWithEmptyString(rset.getString("PAS"))));
                    }
                    if (rset.getString("PAS") != null) {
                        ht.put(Keys.RIS_Y1, new XMLWrapper(Keys.RIS_Y1, StringUtil.replaceNullWithEmptyString(rset.getString("PAS"))));
                    }
                    if (rset.getString("FDATE") != null) {
                        ht.put(Keys.RIS_Y2, new XMLWrapper(Keys.RIS_Y2, StringUtil.replaceNullWithEmptyString(rset.getString("FDATE"))));
                    }
                }
                if (rset.getString("PDATE") != null) {
                    ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, getRISDate(StringUtil.replaceNullWithEmptyString(rset.getString("PDATE"))), perl));
                }

                String strPages = StringUtil.EMPTY_STRING;
                if (rset.getString("IPN") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("IPN"));
                } else if (rset.getString("NPL1") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"));
                }
                if (strPages != null && (!strPages.equals(StringUtil.EMPTY_STRING))) {
                    // Strip out and store the start page and end page
                    if (perl.match("/(\\d+)[^\\d](\\d+)/", strPages)) {
                        if (perl.match("/(\\d+)/", strPages)) {
                            ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, perl.group(0).toString()));
                            if (perl.match("/(\\d+)/", perl.postMatch())) {
                                ht.put(Keys.RIS_EP, new XMLWrapper(Keys.RIS_EP, perl.group(0).toString()));

                            }
                        }
                    } else {
                        ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, strPages));
                    }
                }

                if (rset.getString("TC") != null) {
                    ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, StringUtil.replaceNullWithEmptyString(rset.getString("TC"))));
                } else {
                    if (strRTYPE.equals("60") && rset.getString("THLP") != null) {
                        ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, StringUtil.replaceNullWithEmptyString(rset.getString("THLP"))));
                    } else if (strRTYPE.equals("50") && rset.getString("TI") != null) {
                        ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                    }
                }
                if (rset.getString("CDATE") != null) {
                    ht.put(Keys.RIS_MD, new XMLWrapper(Keys.RIS_MD, StringUtil.replaceNullWithEmptyString(rset.getString("CDATE"))));
                }

                /*
                 * JM 10/22/2008 Stop putting Conference Location in CY field. CY should be City of publication. Code was left since we may find another RIS
                 * field to put it in
                 */
                /*
                 * if(!strRTYPE.equals("80") && rset.getString("CLOC") != null) { ht.put(Keys.RIS_CY, new
                 * XMLWrapper(Keys.RIS_CY,StringUtil.replaceNullWithEmptyString(rset.getString("CLOC")))); }
                 */

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.RIS_AB2, new XMLWrapper(Keys.RIS_AB2, abs));
                }

                if (rset.getString("PUB") != null) {
                    ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, StringUtil.replaceNullWithEmptyString(rset.getString("PUB"))));
                }

                // CVS/CV
                if (rset.getString("CVS") != null) {
                    ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("CVS")))));
                }

                // FLS
                if (rset.getString("FLS") != null) {
                    ht.put(Keys.RIS_FLS, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("FLS")))));
                }
                // ANUM
                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.RIS_AN, new XMLWrapper(Keys.RIS_AN, StringUtil.replaceNullWithEmptyString(rset.getString("ANUM"))));
                }

                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, StringUtil.replaceNullWithEmptyString(rset.getString("DOI"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, RIS.RIS_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(RIS_KEYS);
                list.add(eiDoc);
                count++;
            } // while

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        } // try/finally

        return list;

    } // loadRIS

    private List<EIDoc> loadCitations(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();

        List<EIDoc> list = new ArrayList<EIDoc>();
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
            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);
                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));
                if (rset.getString("CPR") != null) {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, rset.getString("CPR")));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, rset.getString("CPR")));
                } else {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, INS_HTML_COPYRIGHT));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, INS_TEXT_COPYRIGHT));
                }

                if (rset.getString("IPN") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(rset.getString("IPN"), perl));
                }

                // ANUM
                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("ANUM"))));
                }

                // FJT
                if (rset.getString("FJT") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("FJT")));
                }

                // AJT
                if (rset.getString("AJT") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("AJT")));
                }

                // PDATE
                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                }

                String strRTYPE = StringUtil.replaceNullWithEmptyString(rset.getString("RTYPE"));

                // TI
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                }

                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUS"), Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);

                } else if (rset.getString("EDS") != null) {
                    Contributors editors = new Contributors(Keys.EDITORS, getContributors(rset.getString("EDS"), Keys.EDITORS));
                    ht.put(Keys.EDITORS, editors);

                }

                String strFJT;
                if (rset.getString("FJT") != null) {
                    strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("FJT"));
                } else {
                    strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("OJT"));
                }
                String strTHLP = StringUtil.replaceNullWithEmptyString(rset.getString("THLP"));

                if ((strRTYPE.equals("21") || strRTYPE.equals("22") || strRTYPE.equals("23"))
                    || (strRTYPE.equals("51") || strRTYPE.equals("52") || strRTYPE.equals("53"))
                    || (strRTYPE.equals("61") || strRTYPE.equals("62") || strRTYPE.equals("63"))) {
                    if (strFJT != null && (!strFJT.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strFJT));
                    }

                    if (rset.getString("FTTJ") != null) {
                        ht.put(Keys.TRANSLATION_SERIAL_TITLE, new XMLWrapper(Keys.TRANSLATION_SERIAL_TITLE, TRANS_SEE_DETAILED));
                    }

                    if (rset.getString("VOL") != null || rset.getString("ISS") != null) {
                        String strVolIss = StringUtil.EMPTY_STRING;
                        if (rset.getString("VOL") != null) {
                            ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, rset.getString("VOL"), perl));
                        }
                        if (rset.getString("ISS") != null) {
                            ht.put(Keys.ISSUE, new Issue(rset.getString("ISS"), perl));
                        }
                        if (rset.getString("VOL") != null)
                            strVolIss = "v " + StringUtil.replaceNullWithEmptyString(rset.getString("VOL"));
                        if (rset.getString("ISS") != null) {
                            if (strVolIss.equals(StringUtil.EMPTY_STRING))
                                strVolIss = strVolIss + "n " + StringUtil.replaceNullWithEmptyString(rset.getString("ISS"));
                            else
                                strVolIss = strVolIss + ", n " + StringUtil.replaceNullWithEmptyString(rset.getString("ISS"));
                        }

                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                    }

                } else if ((strRTYPE.equals("30")) || (strRTYPE.equals("40")) || (strRTYPE.equals("50") && (strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("60") && (strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    if (strTHLP != null && (!strTHLP.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strTHLP));
                    }

                    if (strTHLP.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PUB"))));
                    }
                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {
                    if (strTHLP != null && (!strTHLP.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strTHLP));
                    }

                    if (rset.getString("IORG") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("IORG"))));
                    } else if (rset.getString("PUB") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PUB"))));
                    }

                    if (rset.getString("PDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PDATE"))));
                    } else if (rset.getString("FDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("FDATE"))));
                    }

                    if (rset.getString("RNUM") != null) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("RNUM"))));
                    }

                } else if (strRTYPE.equals("80")) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    if (rset.getString("PAS") != null) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, StringUtil.replaceNullWithEmptyString(rset.getString("PAS"))));
                    }
                    if (rset.getString("PNUM") != null) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, StringUtil.replaceNullWithEmptyString(rset.getString("PNUM"))));
                    }
                    if (rset.getString("CPAT") != null) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, StringUtil.replaceNullWithEmptyString(rset.getString("CPAT"))));
                    }
                    if (rset.getString("CPUB") != null) {
                        ht.put(Keys.COUNTRY_OF_PUB, new XMLWrapper(Keys.COUNTRY_OF_PUB, StringUtil.replaceNullWithEmptyString(rset.getString("CPUB"))));
                    }
                }

                // SD
                if (!strRTYPE.equals("80")) {
                    if (rset.getString("PDATE") != null) {
                        String strYR = rset.getString("PDATE");
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strYR));
                    } else if (rset.getString("FDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("FDATE"))));
                    }
                }

                if (rset.getString("PARTNO") != null) {
                    String PartNumber = getPartNumber(rset.getString("PARTNO"));
                    String partNo = "pt. ".concat(StringUtil.replaceNullWithEmptyString(PartNumber));
                    ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, partNo));
                }

                // PP
                if (rset.getString("IPN") != null) {
                    ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("IPN"))));
                } else {
                    if (rset.getString("NPL1") != null) {
                        ht.put(Keys.PAGE_RANGE_pp, new XMLWrapper(Keys.PAGE_RANGE_pp, StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"))));
                    }
                }

                if (rset.getString("TC") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("TC"))));
                } else {

                    if (strRTYPE.equals("60") || strRTYPE.equals("61") || strRTYPE.equals("62") || strRTYPE.equals("63") && rset.getString("THLP") != null) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("THLP"))));
                    } else if (strRTYPE.equals("50") || strRTYPE.equals("51") || strRTYPE.equals("52") || strRTYPE.equals("53") && rset.getString("TI") != null) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                    }
                }

                // LA
                if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LA"))));
                }

                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, StringUtil.replaceNullWithEmptyString(rset.getString("DOI"))));
                }

                if (rset.getString("FIG") != null) {
                    ht.put(Keys.NUMBER_OF_FIGURES, new XMLWrapper(Keys.NUMBER_OF_FIGURES, StringUtil.replaceNullWithEmptyString(rset.getString("FIG"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(CITATION_KEYS);
                list.add(eiDoc);
                count++;
            } // while

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        } // try/finally

        return list;

    } // loadCitation

    /**
     * This method basically takes list Of DocIDs as Parameter This list of Docids use buildINString() method to build the required IN clause String.This is
     * appended to sql String The resultSet so obtained by executing the sql,is iterated, to build EIDoc objects,which are then added to EIdocumentList
     *
     * @param listOfDocIDs
     * @return EIDocumentList
     * @exception Exception
     */
    private List<EIDoc> loadXMLCitations(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();
        List<EIDoc> list = new ArrayList<EIDoc>();
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
            rset = stmt.executeQuery(queryXMLCitation + INString);
            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);

                if (rset.getString("CPR") != null) {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, rset.getString("CPR")));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, rset.getString("CPR")));
                } else {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, INS_HTML_COPYRIGHT));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, INS_TEXT_COPYRIGHT));
                }

                // Accession_number, needed for openXML.
                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ANUM")));
                }

                // Needed for IVIP
                /*
                 * JM 10/24/2008 Column "PSN" Does not exist - this is throwing exception ISSN does not appear in any other docbuilder methods in this file.
                 * if(rset.getString("PSN") != null) { ht.put(Keys.ISSN,new ISSN(rset.getString("PSN"))); }
                 */
                String strRTYPE = StringUtil.replaceNullWithEmptyString(rset.getString("RTYPE"));

                // TI
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("TI")));
                }

                if (strRTYPE.equals("08")) {
                    Contributors inventors = new Contributors(Keys.INVENTORS, getContributors(rset.getString("AUS"), Keys.INVENTORS));
                    ht.put(Keys.INVENTORS, inventors);

                } else {
                    if (rset.getString("AUS") != null) {
                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUS"), Keys.AUTHORS));
                        ht.put(Keys.AUTHORS, authors);

                    } else {
                        if (rset.getString("EDS") != null) {
                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(rset.getString("EDS"), Keys.EDITORS));
                            ht.put(Keys.EDITORS, editors);
                        }
                    }
                }

                String strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("FJT"));
                String strTHLP = StringUtil.replaceNullWithEmptyString(rset.getString("THLP"));

                if ((strRTYPE.equals("21") || strRTYPE.equals("22") || strRTYPE.equals("23"))
                    || (strRTYPE.equals("51") || strRTYPE.equals("52") || strRTYPE.equals("53"))
                    || (strRTYPE.equals("61") || strRTYPE.equals("62") || strRTYPE.equals("63"))) {
                    if (!strFJT.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strFJT));
                    }

                    if (rset.getString("FTTJ") != null) {
                        ht.put(Keys.TRANSLATION_SERIAL_TITLE, new XMLWrapper(Keys.TRANSLATION_SERIAL_TITLE, TRANS_SEE_DETAILED));
                    }

                    if (rset.getString("VOL") != null || rset.getString("ISS") != null) {
                        String strVolIss = StringUtil.EMPTY_STRING;
                        if (rset.getString("VOL") != null) {
                            ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, rset.getString("VOL"), perl));
                        }
                        if (rset.getString("ISS") != null) {
                            ht.put(Keys.ISSUE, new Issue(rset.getString("ISS"), perl));
                        }
                        if (rset.getString("VOL") != null)
                            strVolIss = "v " + StringUtil.replaceNullWithEmptyString(rset.getString("VOL"));
                        if (rset.getString("ISS") != null) {
                            if (strVolIss.equals(StringUtil.EMPTY_STRING))
                                strVolIss = strVolIss + "n " + StringUtil.replaceNullWithEmptyString(rset.getString("ISS"));
                            else
                                strVolIss = strVolIss + ", n " + StringUtil.replaceNullWithEmptyString(rset.getString("ISS"));
                        }

                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                    }

                } else if ((strRTYPE.equals("30")) || (strRTYPE.equals("40")) || (strRTYPE.equals("50")) || (strRTYPE.equals("60"))) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strTHLP));
                    if (strTHLP.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PUB")));
                    }
                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {

                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strTHLP));

                    if (rset.getString("IORG") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, replaceInvisibleChar(rset.getString("IORG"))));
                    } else {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PUB")));
                    }

                    if (rset.getString("PDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("PDATE")));
                    } else if (rset.getString("FDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("FDATE")));
                    }
                    if (rset.getString("RNUM") != null) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, rset.getString("RNUM")));
                    }

                } else if (strRTYPE.equals("80")) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    if (rset.getString("PAS") != null) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, StringUtil.replaceNullWithEmptyString(rset.getString("PAS"))));
                    }
                    if (rset.getString("PNUM") != null) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, StringUtil.replaceNullWithEmptyString(rset.getString("PNUM"))));
                    }
                    if (rset.getString("PDATE") != null) {
                        ht.put(Keys.PATPUBDATE, new XMLWrapper(Keys.PATPUBDATE, StringUtil.replaceNullWithEmptyString(rset.getString("PDATE"))));
                    }

                }

                // SD
                if (!strRTYPE.equals("80")) {
                    if (rset.getString("PDATE") != null) {
                        String strYR = rset.getString("PDATE");
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strYR));
                    } else if (rset.getString("FDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("FDATE")));
                    }
                }

                if (rset.getString("PARTNO") != null) {
                    String PartNumber = getPartNumber(rset.getString("PARTNO"));
                    String partNo = "pt. ".concat(StringUtil.replaceNullWithEmptyString(PartNumber));
                    ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, partNo));
                }

                // PP
                if (rset.getString("IPN") != null) {
                    String pageRange = rset.getString("IPN").replaceAll("p", " ");
                    ht.put(Keys.PAGE_RANGE, new XMLWrapper(Keys.PAGE_RANGE, pageRange));
                } else if (rset.getString("NPL1") != null) {
                    String pageCount = rset.getString("NPL1").replaceAll("p", " ");
                    ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, pageCount));
                }

                // LA
                if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, rset.getString("LA")));
                }

                // DOI
                ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));

                // CVS
                if (rset.getString("CVS") != null) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(INS_CONTROLLED_TERMS, setCVS(rset.getString("CVS"))));
                }

                // ISBN
                // ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("SBN"))));

                // PN
                if (rset.getString("PUB") != null) {
                    List<String> lstPub = new ArrayList<String>();
                    lstPub.add(rset.getString("PUB"));

                    if (rset.getString("PPUB") != null) {
                        lstPub.add(rset.getString("PPUB"));
                    } else if (rset.getString("CPUB") != null) {
                        lstPub.add(rset.getString("CPUB"));
                    }
                    ht.put(Keys.I_PUBLISHER, new XMLWrapper(Keys.I_PUBLISHER, StringUtil.join(lstPub, ", ")));
                    lstPub.clear();
                    lstPub = null;
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.XMLCITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(XML_KEYS);
                list.add(eiDoc);
                count++;
            } // while

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        } // try/finally

        return list;

    } // loadXMLCitation

    private int getFigureCount(String fig) {
        String[] temp = fig.split("~");
        return temp.length;

    }

    public List<Contributor> getContributors(String strAuthors, Key key) {

        List<Contributor> list = new ArrayList<Contributor>();
        AuthorStream aStream = null;
        DataCleaner dataCleaner = new DataCleaner();
        strAuthors = dataCleaner.cleanEntitiesForDisplay(strAuthors);
        try {
            aStream = new AuthorStream(new ByteArrayInputStream(strAuthors.getBytes()));
            String strToken = null;
            while ((strToken = aStream.readAuthor()) != null) {
                list.add(new Contributor(key, strToken));
            }
        } catch (IOException ioe) {
            System.out.println("IOE " + ioe.getMessage());
        } finally {
            if (aStream != null)
                try {
                    aStream.close();
                    aStream = null;
                } catch (IOException ioe) {
                }
        }
        return list;
    }

    /*
     * This method builds the IN String from list of docId objects. The select query will get the result set in a reverse way So in order to get in correct
     * order we are doing a reverse example of return String--(23,22,1,12...so on);
     *
     * @param listOfDocIDs
     *
     * @return String
     */
    protected String buildINString(List<DocID> listOfDocIDs) {
        StringBuffer sQuery = new StringBuffer();
        sQuery.append("(");
        for (int k = listOfDocIDs.size(); k > 0; k--) {
            DocID doc = (DocID) listOfDocIDs.get(k - 1);
            String docID = doc.getDocID();
            if ((k - 1) == 0) {
                sQuery.append("'" + docID + "'");
            } else {
                sQuery.append("'" + docID + "'").append(",");
            }
        }// end of for
        sQuery.append(")");
        return sQuery.toString();
    }

    /* TS if volume is null and str is not null print n */
    private String replaceVolumeNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = "";
        }

        if (!str.equals("") && ((str.indexOf("v", 0) < 0) && (str.indexOf("V", 0) < 0))) {
            str = "v ".concat(str);
        }
        return str;
    }

    /* TS if number is null and str is not null print n */
    private String replaceIssueNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = "";
        }

        if (!str.equals("") && ((str.indexOf("n") < 0) && (str.indexOf("N") < 0))) {
            str = "n ".concat(str);
        }
        return str;
    }

    private String hasAbstract(ResultSet rs) throws SQLException {
        String abs = null;
        Clob clob = rs.getClob("AB");
        if (clob != null) {
            abs = StringUtil.getStringFromClob(clob);
        }

        if (abs == null || abs.equals("NOABSTRACT")) {
            return null;
        } else {
            return abs;
        }
    }

    private KeyValuePair[] setCVS(String cvs) {
        ArrayList<KeyValuePair> list = new ArrayList<KeyValuePair>();

        AuthorStream aStream = null;
        String strToken = null;
        try {
            if (cvs != null) {
                aStream = new AuthorStream(new ByteArrayInputStream(cvs.getBytes()));
                strToken = null;
                while ((strToken = aStream.readAuthor()) != null) {
                    KeyValuePair k = new KeyValuePair(Keys.CONTROLLED_TERM, strToken);
                    list.add(k);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (aStream != null) {
                try {
                    aStream.close();
                    aStream = null;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return (KeyValuePair[]) list.toArray(new KeyValuePair[list.size()]);

    }

    public String[] setElementData(String elementVal) {
        ArrayList<String> list = new ArrayList<String>();
        AuthorStream aStream = null;
        String strToken = null;
        try {
            if (elementVal != null) {
                aStream = new AuthorStream(new ByteArrayInputStream(elementVal.getBytes()));
                strToken = null;
                while ((strToken = aStream.readAuthor()) != null) {
                    list.add(strToken.trim());
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (aStream != null) {
                try {
                    aStream.close();
                    aStream = null;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public String[] setDisciplineElementData(String elementVal) {
        ArrayList<String> list = new ArrayList<String>();
        if (elementVal != null) {
            StringTokenizer st = new StringTokenizer(elementVal, ";");
            String strToken;
            List<String> lstUsedDisciplines = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                strToken = st.nextToken().trim();
                if (!lstUsedDisciplines.contains(strToken.substring(0, 1))) {

                    lstUsedDisciplines.add(strToken.substring(0, 1));
                    if (strToken.toUpperCase().startsWith("A")) {
                        list.add("Physics (A)");
                    } else if (strToken.toUpperCase().startsWith("B")) {
                        list.add("Electrical/Electronic engineering (B)");
                    } else if (strToken.toUpperCase().startsWith("C")) {
                        list.add("Computers/Control engineering (C)");
                    } else if (strToken.toUpperCase().startsWith("D")) {
                        list.add("Information technology (D)");
                    } else if (strToken.toUpperCase().startsWith("E")) {
                        list.add("Manufacturing and production engineering (E)");
                    }

                }

            }
        }
        return (String[]) list.toArray(new String[list.size()]);

    }

    private String[] getMultipleSources(String strSource) {
        String[] strToken = strSource.split("~ ");
        String[] source;
        ArrayList<String> list = new ArrayList<String>();

        StringBuffer strResult = new StringBuffer();
        // StringBuffer strResult1 = new StringBuffer();
        for (int i = 0; i < strToken.length; i++) {
            source = strToken[i].split("\\|");
            if (strToken.length == 1)
                list.add("<SOURCE ");
            else
                strResult.append("<SOURCE NO=\"").append(i + 1).append("\"");
            list.add(strResult.toString());
            if (source.length == 12)
                strResult.append(" DOI=\"").append(source[11]).append("\"");
            list.add(strResult.toString());
            list.add(" >");

            list.add("<![CDATA[");
            if (source.length >= 1) {
                if (!source[0].equals("")) {
                    list.add(source[0]);
                } else if (source.length >= 3) {
                    if (!source[2].equals(""))
                        list.add(source[2]);
                }
            }

            if (source.length >= 4) {
                if (!source[3].equals(""))
                    strResult.append(", v ").append(source[3]);
                list.add(strResult.toString());
            }
            if (source.length >= 5) {
                if (!source[4].equals(""))
                    strResult.append(", n ").append(source[4]);
                list.add(strResult.toString());
            }
            if (source.length >= 8) {
                if (!source[7].equals(""))
                    strResult.append(", ").append(source[7]);
                list.add(strResult.toString());
            }
            if (source.length >= 7) {
                if (!source[6].equals(""))
                    strResult.append(", p ").append(source[6]);
                list.add(strResult.toString());
            }
            list.add("]]>");
            list.add("</SOURCE>");

        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    private String replaceInvisibleChar(String str) {
        if (str.indexOf(AUDELIMITER) > -1) {
            str = perl.substitute("s/" + AUDELIMITER + "/;/g", str);
        }

        return str;
    }

    private String replaceIndextermsDelim(String str) {
        str = perl.substitute("s/" + IDDELIMITER + "/ /g", str);

        if (str.indexOf(AUDELIMITER) > -1) {
            str = perl.substitute("s/" + AUDELIMITER + "/;/g", str);
        }

        return str;
    }

    /*
     * This method strips out 'vol' from the PARTNO field.
     */
    private String getPartNumber(String partNumber) {
        String str = null;
        if (partNumber != null) {
            StringTokenizer stoken = new StringTokenizer(partNumber, ".");
            if (stoken.hasMoreTokens()) {
                String tmp = stoken.nextToken();
                if (tmp.trim().equalsIgnoreCase("vol")) {
                    str = stoken.nextToken();
                }
            }
        }
        return str;
    }

    private String replaceTYwithRIScode(String str) {

        if (!str.equals(StringUtil.EMPTY_STRING)) {
            int type = Integer.parseInt(str);

            switch (type) {
            case 10:
                str = "THES";
                break;

            case 11:
                str = "RPRT";
                break;

            case 12:
                str = "RPRT";
                break;

            case 21:
                str = "JOUR";
                break;

            case 22:
                str = "JOUR";
                break;

            case 23:
                str = "JOUR";
                break;

            case 30:
                str = "BOOK";
                break;

            case 40:
                str = "CHAP";
                break;

            case 50:
                str = "CONF";
                break;
            case 51:
                str = "CONF";
                break;

            case 52:
                str = "CONF";
                break;

            case 53:
                str = "CONF";
                break;

            case 60:
                str = "CONF";
                break;
            case 61:
                str = "CONF";
                break;

            case 62:
                str = "CONF";
                break;

            case 63:
                str = "CONF";
                break;

            case 80:
                str = "PAT";
                break;

            default:
                str = "JOUR";
            }
            ;
        }

        return str;
    }

    // TS XML document mapping, conversion to dt values 02/10/03

    private String replaceDTNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = "";
        }

        if (!str.equals("")) {
            if (str.equals("21")) {
                str = "Journal article (JA)";
            } else if (str.equals("22")) {
                str = "Journal article (JA)";
            } else if (str.equals("23")) {
                str = "Journal article (JA)";
            } else if (str.equals("30")) {
                str = "Monograph review (MR)";
            } else if (str.equals("40")) {
                str = "Monograph chapter (MC)";
            } else if (str.equals("50")) {
                str = "Conference proceeding (CP)";
            } else if (str.equals("51")) {
                str = "Conference proceeding (CP)";
            } else if (str.equals("52")) {
                str = "Conference proceeding (CP)";
            } else if (str.equals("53")) {
                str = "Conference proceeding (CP)";
            } else if (str.equals("60")) {
                str = "Conference article (CA)";
            } else if (str.equals("61")) {
                str = "Conference article (CA)";
            } else if (str.equals("62")) {
                str = "Conference article (CA)";
            } else if (str.equals("63")) {
                str = "Conference article (CA)";
            } else if (str.equals("80")) {
                str = "Patent (PA)";
            } else if (str.equals("10")) {
                str = "Dissertation (DS)";
            } else if (str.equals("11")) {
                str = "Report review (RR)";
            } else if (str.equals("12")) {
                str = "Report chapter (RC)";
            }
        }
        return str;
    }

    protected Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }

        return h;
    }

    private String getRISDate(String date) {

        // This code takes a date in a variety of different formats.
        // Parses it into a Date object and then outputs it in the RIS format
        // Known input Formats:
        // 1 Mar 2003
        // Mar.-Apr 2002
        // March 3, 2002
        // Mar. 2002
        // 2002
        // If parsing the date fails, the raw field from the DB is used
        String strDate = date;

        final String DEFAULT_RIS_FORMAT = "yyyy/MM/dd";
        final String DEFAULT_PDATE_FORMAT = "yyyy/MMM/dd";

        String strOutputPattern = DEFAULT_RIS_FORMAT;
        String strInputPattern = DEFAULT_PDATE_FORMAT;

        if (perl.match("/(\\w+)\\W+(\\d+)\\W+(\\d+)/", strDate)) {
            strInputPattern = DEFAULT_PDATE_FORMAT;
            strOutputPattern = DEFAULT_RIS_FORMAT;
            strDate = (perl.group(3).toString()).concat("/").concat(perl.group(1).toString()).concat("/").concat(perl.group(2).toString());
        } else if (perl.match("/(\\d+)\\W+(\\w+)\\W+(\\d+)/", strDate)) {
            strInputPattern = DEFAULT_PDATE_FORMAT;
            strOutputPattern = DEFAULT_RIS_FORMAT;
            strDate = (perl.group(3).toString()).concat("/").concat(perl.group(2).toString()).concat("/").concat(perl.group(1).toString());
        } else if (perl.match("/(\\w+)\\W+(\\w+)\\W+(\\d+)/", strDate)) {
            strInputPattern = "yyyy/MMM/";
            strOutputPattern = "yyyy/MM/";
            strDate = (perl.group(3).toString()).concat("/").concat(perl.group(1).toString()).concat("/");
        } else if (perl.match("/(\\w+)\\W+(\\d+)/", strDate)) {
            strInputPattern = "yyyy/MMM/";
            strOutputPattern = "yyyy/MM/";
            strDate = (perl.group(2).toString()).concat("/").concat(perl.group(1).toString()).concat("/");
        } else if (perl.match("/(\\d+)/", strDate)) {
            strInputPattern = "yyyy//";
            strOutputPattern = "yyyy//";
            strDate = (perl.group(1).toString()).concat("//");
        }

        SimpleDateFormat formatter = new SimpleDateFormat(strInputPattern);

        // Parse the resulting string into a Date object
        // MMM is used to parse the input (Month name, this will include long, short and abbreviated month names)
        // MM is used to format the output (Month number)
        // Format the Date object back into a String
        try {
            java.util.Date currentTime = formatter.parse((String) strDate);
            formatter = new SimpleDateFormat(strOutputPattern);
            strDate = formatter.format(currentTime);
        } catch (ParseException pe) {
            // just in case we failed miserably, use the raw data from the DB
            strDate = StringUtil.replaceNullWithEmptyString(date);
        }

        return strDate;
    }

    private String checkOCVS(String str) {

        StringBuffer buf = new StringBuffer();
        String[] cvs = str.split("~ ");
        for (int i = 0; i < cvs.length; i++) {
            if (!cvs[i].startsWith("|||")) {
                if (buf.length() == 0)
                    buf.append(cvs[i]);
                else
                    buf.append("~ ").append(cvs[i]);

            }

        }

        return buf.toString();

    }

    private String checkOCLS(String str) {

        StringBuffer buf = new StringBuffer();
        String[] cls = str.split("~ ");
        for (int i = 0; i < cls.length; i++) {
            if (!cls[i].replaceFirst("prime\\|", "\\|").startsWith("|||")) {
                if (buf.length() == 0)
                    buf.append(cls[i]);
                else
                    buf.append("~ ").append(cls[i]);

            }
        }
        return buf.toString();

    }

    public String[] setTreatments(String treatments) {
        ArrayList<String> result = new ArrayList<String>();
        int len = 0;
        int trlen = treatments.length();
        String ch = "";
        if (trlen >= 0 && trlen < 10 && trlen > 0) {
            for (int i = 0; i < trlen; i++) {
                if (len < trlen) {
                    ch = treatments.substring(len, len + 1);
                } else {
                    ch = treatments.substring(len);
                }

                if (!ch.equals("") && ch != null && !ch.equals("QQ") && !ch.equals(";")) {
                    result.add(ch);
                }
                len++;
                ch = "";
            }
        }

        return (String[]) result.toArray(new String[1]);
    }

    @Override
    public Key[] getCitationKeys() {
        return CITATION_KEYS;
    }

    @Override
    public Key[] getAbstractKeys() {
        return ABSTRACT_KEYS;
    }

    @Override
    public Key[] getDetailedKeys() {
        return DETAILED_KEYS;
    }

}
