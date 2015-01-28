package org.ei.data.inspec.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.data.AuthorStream;
import org.ei.data.CITEDBY;
import org.ei.data.bd.BdAffiliation;
import org.ei.data.bd.BdAuthor;
import org.ei.data.bd.loadtime.BdParser;
import org.ei.domain.Abstract;
import org.ei.domain.Affiliation;
import org.ei.domain.Affiliations;
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
import org.ei.domain.IPCClassifications;
import org.ei.domain.ISBN;
import org.ei.domain.ISSN;
import org.ei.domain.Issue;
import org.ei.domain.Key;
import org.ei.domain.KeyValuePair;
import org.ei.domain.Keys;
import org.ei.domain.PageRange;
import org.ei.domain.RIS;
import org.ei.domain.Treatments;
import org.ei.domain.Volume;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLMultiWrapper2;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.util.StringUtil;

public class InspecDocBuilder implements DocumentBuilder {
    public static String INS_TEXT_COPYRIGHT = "Copyright 2014, IEE";
    public static String INS_HTML_COPYRIGHT = "Copyright 2014, IEE";
    public static String PROVIDER_TEXT = "Inspec";
    public static String TRANS_SEE_DETAILED = "For translation info., see Detailed Record / Links";
    private static final Key INS_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Inspec controlled terms");
    private static final Key INS_CLASS_CODES = new Key(Keys.CLASS_CODES, "Inspec classification codes");
    public static final Key[] CITATION_KEYS = { Keys.ACCESSION_NUMBER, Keys.CITEDBY, Keys.DOCID, Keys.DOC_TYPE, Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.INVENTORS,
        Keys.EDITOR_AFFS, Keys.REPORT_NUMBER, Keys.PUBLISHER, Keys.PATPUBDATE, Keys.PATFILDATE, Keys.PATASSIGN, Keys.PATAPPNUM, Keys.PATNUM, Keys.PART_NUMBER,
        Keys.PATCOUNTRY, Keys.NO_SO, Keys.SOURCE, Keys.TRANSLATION_SERIAL_TITLE, Keys.AUTHOR_AFFS, Keys.p_PAGE_RANGE, Keys.PAGE_RANGE_pp, Keys.VOLISSUE,
        Keys.SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.ISSUE_DATE, Keys.LANGUAGE, Keys.ISBN, Keys.DOI, Keys.CODEN, Keys.ISSN, Keys.COPYRIGHT,
        Keys.COPYRIGHT_TEXT, Keys.NUMBER_OF_REFERENCES, Keys.REFCNT };
    public static final Key[] ABSTRACT_KEYS = { Keys.CITEDBY, Keys.DOCID, Keys.DOC_TYPE, Keys.TITLE, Keys.INVENTORS, Keys.EDITORS, Keys.AUTHORS, Keys.EDITOR_AFFS,
        Keys.AUTHOR_AFFS, Keys.VOLISSUE, Keys.NO_SO, Keys.SOURCE, Keys.SERIAL_TITLE, Keys.TRANSLATION_SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.ISSUE_DATE,
        Keys.LANGUAGE, Keys.REPORT_NUMBER, Keys.PATFILDATE, Keys.PATASSIGN, Keys.PATAPPNUM, Keys.PATNUM, Keys.PATCOUNTRY, Keys.PATPUBDATE, Keys.p_PAGE_RANGE,
        Keys.PAGE_RANGE_pp, Keys.COUNTRY_OF_PUB, Keys.ISBN, Keys.ISSN, Keys.CONFERENCE_NAME, Keys.CONF_DATE, Keys.MEETING_LOCATION, Keys.SPONSOR,
        Keys.PART_NUMBER, Keys.PART_NUMBER, Keys.PUBLISHER, Keys.I_PUBLISHER, Keys.PAGE_COUNT, Keys.PUB_PLACE, Keys.DOI, Keys.PROVIDER, INS_CONTROLLED_TERMS,
        Keys.UNCONTROLLED_TERMS, Keys.ABSTRACT, Keys.NUMBER_OF_REFERENCES, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.CLASS_CODES, Keys.TREATMENTS,
        Keys.INTERNATCL_CODE, Keys.CITATION, Keys.REFCNT };
    public static final Key[] DETAILED_KEYS = { Keys.CITEDBY, Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.TITLE_TRANSLATION, Keys.AUTHORS, Keys.EDITORS,
        Keys.INVENTORS, Keys.PATASSIGN, Keys.PATAPPNUM, Keys.PATNUM, Keys.PATFILDATE, Keys.AUTHOR_AFFS, Keys.EDITOR_AFFS, Keys.SOURCE, Keys.SERIAL_TITLE,
        Keys.ABBRV_SERIAL_TITLE, Keys.VOLUME, Keys.ISSUE, Keys.ISSUING_ORG, Keys.REPORT_NUMBER, Keys.ISSUE_DATE, Keys.PUBLICATION_DATE,
        Keys.PART_NUMBER, Keys.PAGE_RANGE, Keys.LANGUAGE, Keys.ISSN, Keys.CODEN, Keys.ISBN, Keys.ISBN13, Keys.DOC_TYPE, Keys.PATCOUNTRY, Keys.CONFERENCE_NAME,
        Keys.CONF_DATE, Keys.MEETING_LOCATION, Keys.CONF_CODE, Keys.SPONSOR, Keys.PUBLISHER, Keys.PUB_PLACE, Keys.PUB_LOCATION, Keys.TRANSLATION_SERIAL_TITLE,
        Keys.TRANSLATION_ABBREVIATED_SERIAL_TITLE, Keys.TRANSLATION_VOLUME, Keys.TRANSLATION_ISSUE, Keys.TRANSLATION_PUBLICATION_DATE, Keys.TRANSLATION_PAGES,
        Keys.TRANSLATION_ISSN, Keys.TRANSLATION_CODEN, Keys.TRANSLATION_COUNTRY_OF_PUB, Keys.MATERIAL_ID, Keys.ABSTRACT, Keys.ABSTRACT_TYPE,
        Keys.NUMBER_OF_REFERENCES, Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.CLASS_CODES, Keys.NUMERICAL_DATA_INDEXING,
        Keys.ASTRONOMICAL_OBJECT_INDEXING, Keys.CHEMICAL_DATA_INDEXING, Keys.TREATMENTS, Keys.DISCIPLINES, Keys.DOI, Keys.DOCID, Keys.INTERNATCL_CODE,
        Keys.COPYRIGHT, Keys.PROVIDER, Keys.COPYRIGHT_TEXT, Keys.CITATION, Keys.REFCNT };

    private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_LA, Keys.RIS_M1, Keys.RIS_N1, Keys.RIS_Y1, Keys.RIS_Y2, Keys.RIS_A2, Keys.RIS_SE,
        Keys.RIS_TI, Keys.RIS_T1, Keys.RIS_T2, Keys.RIS_AD, Keys.RIS_BT, Keys.RIS_JO, Keys.RIS_T3, Keys.RIS_AUS, Keys.RIS_EDS, Keys.RIS_VL, Keys.RIS_IS,
        Keys.RIS_PY, Keys.RIS_AN, Keys.RIS_SP, Keys.RIS_EP, Keys.RIS_SN, Keys.RIS_BN, Keys.RIS_S1, Keys.RIS_MD, Keys.RIS_CY, Keys.RIS_UR, Keys.RIS_PB,
        Keys.RIS_N2, Keys.RIS_KW, Keys.RIS_CVS, Keys.RIS_FLS, Keys.RIS_DO, Keys.BIB_TY };

    private static final Key[] XML_KEYS = { Keys.DOCID, Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.INVENTORS, Keys.EDITOR_AFFS,
        Keys.REPORT_NUMBER, Keys.PUBLISHER, Keys.PATPUBDATE, Keys.PATFILDATE, Keys.PATASSIGN, Keys.PATAPPNUM, Keys.PATNUM, Keys.PART_NUMBER, Keys.PATCOUNTRY,
        Keys.PAGE_COUNT, Keys.NO_SO, Keys.SOURCE, Keys.TRANSLATION_SERIAL_TITLE, Keys.AUTHOR_AFFS, Keys.PAGE_RANGE, Keys.p_PAGE_RANGE, Keys.PAGE_RANGE_pp,
        Keys.VOLUME, Keys.ISSUE, Keys.VOLISSUE, Keys.SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.ISSUE_DATE, Keys.LANGUAGE, Keys.ISBN, Keys.DOI, Keys.CODEN,
        Keys.CONTROLLED_TERMS, Keys.I_PUBLISHER, Keys.ISSN, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    public static final String AUDELIMITER = new String(new char[] { 30 });
    public static final String IDDELIMITER = new String(new char[] { 31 });
    public static final String GROUPDELIMITER = new String(new char[] { 29 });

    private Database database;
    private Perl5Util perl = new Perl5Util();

    private static String queryCitation = "select SOPDATE, SSPDATE, CDATE, SU, SBN, ANUM, PCDN, M_ID, NRTYPE,RTYPE,LA,PIPN,NPL1,NPL2,PARTNO,PCPUB,BPPUB,PPUB,EAFF,EDS,AUS,AUS2,AAFF,AFC,TI,TC,IORG,RNUM,POPDATE,PFJT,PAJT,PVOLISS,PVOL,PISS,SVOL,SISS,SFJT,SAJT,PAS,OPAN,FDATE,OFDATE,PNUM,PSN,CPR,LOAD_NUMBER,COPA,PDOI,PUBTI,PYR,AAFFMULTI1, AAFFMULTI2, EAFFMULTI1, EAFFMULTI2, EFC, CITATION, XREFNO  from new_ins_master where M_ID IN ";
    private static String queryREFCitation = "select SOPDATE, SSPDATE, CDATE, SU, SBN, ANUM, PCDN, M_ID, NRTYPE,RTYPE,LA,PIPN,NPL1,NPL2,PARTNO,PCPUB,BPPUB,PPUB,EAFF,EDS,AUS,AUS2,AAFF,AFC,TI,TC,IORG,RNUM,POPDATE,PFJT,PAJT,PVOLISS,PVOL,PISS,SVOL,SISS,SFJT,SAJT,PAS,OPAN,FDATE,OFDATE,PNUM,PSN,CPR,LOAD_NUMBER,COPA,PDOI,PUBTI,PYR,AAFFMULTI1, AAFFMULTI2, EAFFMULTI1, EAFFMULTI2, EFC, CITATION, XREFNO  from new_ins_master where ANUM IN ";
    private static String queryXMLCitation = "select  SOPDATE,M_ID,NRTYPE,RTYPE,ANUM,LA,PIPN,NPL1,NPL2,PARTNO,PCPUB,PPUB,BPPUB,EAFF,EDS,AUS,AUS2,AAFF,AFC,TI,IORG,RNUM,POPDATE,PFJT,PAJT,SFJT,SAJT,PVOLISS,PVOL,PISS,SVOL,SISS,PAS,OPAN,FDATE,OFDATE,PNUM,PSN,CPR,LOAD_NUMBER,COPA,PDOI,CVS,SBN,PUBTI,PYR,AAFFMULTI1, AAFFMULTI2, EAFFMULTI1, EAFFMULTI2, EFC, CITATION  from new_ins_master where M_ID IN ";
    private static String queryAbstracts = "select SOPDATE, SSPDATE, CDATE, SU, M_ID,ANUM,NRTYPE,RTYPE,TI,AUS,AUS2,AAFF,AFC,PFJT,SFJT,PAJT,SAJT,PVOLISS,PVOL,PISS,SVOL,SISS,POPDATE,PARTNO,PIPN,NPL1,NPL2,LA,PSN,PCDN,TC,CODATE,CLOC,SORG,BPPUB,PPUB,PCPUB,AB,XREFNO,CVS,FLS,EDS,EAFF,SBN,IORG,RNUM,PAS,OPAN,COPA,PNUM,FDATE,OFDATE,CPR,PDOI,PUBTI,PYR,LOAD_NUMBER,CLS, AAFFMULTI1, AAFFMULTI2, EAFFMULTI1, EAFFMULTI2, EFC, TRMC, IPC, CITATION  from new_ins_master where M_ID IN ";
    private static String queryDetailed = "select SOPDATE,SSPDATE, CDATE, SU, M_ID,NRTYPE,RTYPE,ANUM,TI,AUS,AUS2,EDS,AAFF,AFC,EAFF,PFJT,SFJT,PAJT,SAJT,PVOLISS,PVOL,PISS,SVOLISS,SVOL,SISS,POPDATE,PIPN,NPL1,NPL2,LA,PSN,PCDN,TC,CODATE,CLOC,SORG,COPA,BPPUB,PPUB,PCPUB,SVOLISS,SOPDATE,SIPN,SSN,SCDN,SCPUB,MATID,PARTNO,RNUM,SBN,IORG,FDATE,OFDATE,PYR,PAS,OPAN,PNUM,AB,XREFNO,CVS,FLS,CLS,NDI,CHI,AOI,TRMC,CPR,PURL,PUM,PDOI,PUBTI,LOAD_NUMBER, AAFFMULTI1, AAFFMULTI2, EAFFMULTI1, EAFFMULTI2, EFC, IPC, CITATION  from new_ins_master where M_ID IN ";
    private static String queryPreview = "select M_ID, AB from new_ins_master where M_ID IN ";

    public DocumentBuilder newInstance(Database database) {

        return new InspecDocBuilder(database);
    }

    public InspecDocBuilder() {
    }

    public InspecDocBuilder(Database database) {
        this.database = database;
    }

    /**
     * This method takes a list of DocID objects and dataFormat and returns a List of EIDoc Objects based on a particular dataformat @ param listOfDocIDs @
     * param dataFormat @ return List --list of EIDoc's @ exception DocumentBuilderException
     */
    public List<?> buildPage(List<DocID> listOfDocIDs, String dataFormat) throws DocumentBuilderException {
        List<?> l = null;
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
        } else if (dataFormat.equalsIgnoreCase(Citation.CITATION_FORMAT_REF)) {
            l = loadREFCitations(listOfDocIDs);
        } else if (Citation.CITATION_PREVIEW.equals(dataFormat)) {
            l = loadPreviewData(listOfDocIDs);
        }

        return l;
    }

    private List<EIDoc> loadPreviewData(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        // Perl5Util perl = new Perl5Util();
        Map<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

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
        String dataFormat = Abstract.ABSTRACT_FORMAT;
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

                if (rset.getString("PFJT") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("PFJT")));
                }

                if (rset.getString("PAJT") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("PAJT")));
                } else if (rset.getString("PUBTI") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("PUBTI")));
                }

                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                } else {
                    String spyear = selectPubYear(rset.getString("sspdate"), rset.getString("fdate"), rset.getString("cdate"), rset.getString("su"));
                    if (spyear != null) {
                        ht.put(Keys.PUBLICATION_YEAR, new Year(spyear, perl));
                    }
                }

                if (rset.getString("NPL1") != null) {
                    String pageCount = rset.getString("NPL1").replaceAll("p", " ");
                    ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, pageCount));
                }

                if (rset.getString("PIPN") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(rset.getString("PIPN"), perl));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if (rset.getString("TI") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI"));
                }
                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));

                String strRTYPE = rset.getString("RTYPE");
                ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, replaceDTNullWithEmptyString(rset.getString("NRTYPE"))));

                if (strRTYPE.equals("08")) {
                    if (rset.getString("AUS") != null) {

                        Contributors inventors = new Contributors(Keys.INVENTORS, getContributors(Keys.INVENTORS, rset.getString("AUS"),
                            rset.getString("AUS2"), dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {

                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));
                        }
                        ht.put(Keys.INVENTORS, inventors);
                    }
                } else {
                    if (rset.getString("AUS") != null) {
                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(Keys.AUTHORS, rset.getString("AUS"), rset.getString("AUS2"),
                            dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {

                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));
                        }

                        ht.put(Keys.AUTHORS, authors);

                    } else {
                        if (rset.getString("EDS") != null) {
                            String strED = StringUtil.replaceNullWithEmptyString(rset.getString("EDS"));
                            if (perl.match("/(Ed[.]\\s*)/", strED)) {
                                strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                            }

                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(Keys.EDITORS, strED, null, dataFormat,
                                rset.getString("EAFFMULTI1"), rset.getString("EDS")));
                            ht.put(Keys.EDITORS, editors);

                            if (rset.getString("EAFF") != null) {

                                ht.put(
                                    Keys.EDITOR_AFFS,
                                    getAuthorsAffiliation(Keys.EDITOR_AFFS, rset.getString("EAFF"), rset.getString("EFC"), rset.getString("EAFFMULTI1"),
                                        rset.getString("EAFFMULTI2"), dataFormat));
                            }
                        }
                    }
                }
                String strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("PFJT"));
                String strAJT = StringUtil.replaceNullWithEmptyString(rset.getString("PUBTI"));

                if ((strRTYPE.equals("02")) || (strRTYPE.equals("05") && (!strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (!strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strFJT));

                    if (rset.getString("PVOLISS") != null) {

                        String volumeIssue = rset.getString("PVOLISS");
                        volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                        volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, volumeIssue));

                        if (perl.match("/,/", volumeIssue)) {
                            ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, perl.preMatch(), perl));
                            ht.put(Keys.ISSUE, new Issue(perl.postMatch(), perl));
                        } else {
                            volumeIssue = rset.getString("PVOLISS");
                            if (perl.match("/vol/i", volumeIssue)) {
                                volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                                ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, volumeIssue, perl));
                            } else if (perl.match("/no/i", volumeIssue)) {
                                volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                                ht.put(Keys.ISSUE, new Issue(volumeIssue, perl));
                            }
                        }

                    }

                    // VO
                    if (rset.getString("PVOL") != null) {
                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, rset.getString("PVOL"), perl));
                    }
                    // ISS
                    if (rset.getString("PISS") != null) {
                        ht.put(Keys.ISSUE, new Issue(rset.getString("PISS"), perl));
                    }

                    if (rset.getString("POPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("POPDATE"))));
                    } else if (rset.getString("SOPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("SOPDATE"))));

                    }

                    if (rset.getString("SFJT") != null) {
                        ht.put(Keys.TRANSLATION_SERIAL_TITLE, new XMLWrapper(Keys.TRANSLATION_SERIAL_TITLE, TRANS_SEE_DETAILED));
                    }

                } else if ((strRTYPE.equals("03")) || (strRTYPE.equals("04")) || (strRTYPE.equals("05") && (strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    if ((strAJT != null) && !(strAJT.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));
                    }

                    if (strAJT.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PPUB"))));
                    }
                    if (rset.getString("POPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("POPDATE"))));
                    }

                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {
                    if ((strAJT != null) && !(strAJT.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));
                    }

                    if (rset.getString("IORG") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("IORG"))));
                    } else {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PPUB"))));
                    }

                    if (rset.getString("POPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("POPDATE"))));
                    } else if (rset.getString("OFDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("OFDATE"))));
                    }

                    if (rset.getString("RNUM") != null) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("RNUM"))));
                    }

                } else if (strRTYPE.equals("08")) {

                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    // RTYPE='08'
                    if ((rset.getString("PAS") != null) && !(rset.getString("PAS").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, rset.getString("PAS")));
                    }
                    if ((rset.getString("OPAN") != null) && !(rset.getString("OPAN").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATAPPNUM, new XMLWrapper(Keys.PATAPPNUM, rset.getString("OPAN")));
                    }
                    if ((rset.getString("PNUM") != null) && !(rset.getString("PNUM").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, rset.getString("PNUM")));
                    }
                    if ((rset.getString("OFDATE") != null) && !(rset.getString("OFDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, rset.getString("OFDATE")));
                    }
                    if ((rset.getString("COPA") != null) && !(rset.getString("COPA").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, rset.getString("COPA")));
                    }
                    if ((rset.getString("POPDATE") != null) && !(rset.getString("POPDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATPUBDATE, new XMLWrapper(Keys.PATPUBDATE, rset.getString("POPDATE")));
                    }
                    if ((rset.getString("PCPUB") != null) && !(rset.getString("PCPUB").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.COUNTRY_OF_PUB, new XMLWrapper(Keys.COUNTRY_OF_PUB, rset.getString("PCPUB")));
                    }
                }

                if (rset.getString("PSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("PSN"))));
                }
                if (rset.getString("PCDN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("PCDN"))));
                }
                if (rset.getString("SBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("SBN"))));
                }

                if (rset.getString("PARTNO") != null) {
                    String PartNumber = getPartNumber(rset.getString("PARTNO"));
                    String partNo = "pt. ".concat(StringUtil.replaceNullWithEmptyString(PartNumber));
                    ht.put(Keys.PART_NUMBER, new XMLWrapper(Keys.PART_NUMBER, partNo));

                }

                String strPages = StringUtil.EMPTY_STRING;

                if (rset.getString("PIPN") != null) {
                    ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("PIPN"))));
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("PIPN"));
                } else if (rset.getString("NPL1") != null) {
                    ht.put(Keys.PAGE_RANGE_pp, new XMLWrapper(Keys.PAGE_RANGE_pp, StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"))));
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"));
                } else if (rset.getString("NPL2") != null) {
                    ht.put(Keys.PAGE_RANGE_pp, new XMLWrapper(Keys.PAGE_RANGE_pp, StringUtil.replaceNullWithEmptyString(rset.getString("NPL2"))));
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("NPL2"));
                }

                if ((rset.getString("LA") != null) && (!rset.getString("LA").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, replaceInvisibleChar(StringUtil.replaceNullWithEmptyString(rset.getString("LA")))));

                }

                if (rset.getString("TC") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("TC"))));
                } else {

                    if (strRTYPE.equals("06")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("PUBTI"))));
                    } else if (strRTYPE.equals("05")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                    }
                }

                // TR
                if (rset.getString("TRMC") != null) {
                    ht.put(Keys.TREATMENTS, new Treatments(setTreatments(rset.getString("TRMC")), this.database));
                }

                if (rset.getString("CODATE") != null) {
                    ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("CODATE"))));
                }
                if (rset.getString("CLOC") != null) {
                    ht.put(Keys.MEETING_LOCATION, new XMLWrapper(Keys.MEETING_LOCATION, StringUtil.replaceNullWithEmptyString(rset.getString("CLOC"))));
                }
                if (rset.getString("SORG") != null) {
                    ht.put(Keys.SPONSOR, new XMLWrapper(Keys.SPONSOR, replaceInvisibleChar(StringUtil.replaceNullWithEmptyString(rset.getString("SORG")))));
                }
                if (rset.getString("PPUB") != null) {
                    List<String> lstPub = new ArrayList<String>();
                    lstPub.add(rset.getString("PPUB"));

                    if (rset.getString("BPPUB") != null) {
                        lstPub.add(rset.getString("BPPUB"));
                    } else if (rset.getString("PCPUB") != null) {
                        lstPub.add(rset.getString("PCPUB"));
                    }
                    ht.put(Keys.I_PUBLISHER, new XMLWrapper(Keys.I_PUBLISHER, StringUtil.join(lstPub, ", ")));
                    lstPub.clear();
                    lstPub = null;
                } else if (rset.getString("PCPUB") != null) {
                    ht.put(Keys.COUNTRY_OF_PUB, new XMLWrapper(Keys.COUNTRY_OF_PUB, rset.getString("PCPUB")));
                } else if (rset.getString("BPPUB") != null) {
                    ht.put(Keys.PUB_PLACE, new XMLWrapper(Keys.PUB_PLACE, rset.getString("BPPUB")));
                }

                // DOI
                if (rset.getString("PDOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("PDOI")));
                }

                // CLS
                if (rset.getString("CLS") != null) {
                    ht.put(Keys.CLASS_CODES, new Classifications(INS_CLASS_CODES, setElementData(rset.getString("CLS")), this.database));
                }

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                if (rset.getString("XREFNO") != null) {
                    ht.put(Keys.NUMBER_OF_REFERENCES,
                        new XMLWrapper(Keys.NUMBER_OF_REFERENCES, StringUtil.replaceNullWithEmptyString(rset.getString("XREFNO"))));
                }
                if (rset.getString("CVS") != null) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(INS_CONTROLLED_TERMS, setCVS(rset.getString("CVS"))));
                }

                // FLS - added to ABSTRACT view in Baja build
                if (rset.getString("FLS") != null) {
                    ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(rset.getString("FLS"))));
                }

                if (rset.getString("IPC") != null) {
                    ht.put(Keys.INTERNATCL_CODE, new IPCClassifications(Keys.INTERNATCL_CODE, setElementData(rset.getString("IPC")), this.database));
                }

                // added for CITEDBY

                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ANUM")));
                }

                CITEDBY citedby = new CITEDBY();
                citedby.setKey(Keys.CITEDBY);

                if (rset.getString("PDOI") != null) {
                    citedby.setDoi(URLEncoder.encode((rset.getString("PDOI")).trim()));
                }

                if (rset.getString("PSN") != null) {
                    citedby.setIssn((rset.getString("PSN")).trim());
                }
                
                if (rset.getString("SBN") != null) {
                    citedby.setIsbn((rset.getString("SBN")).trim());
                }

                if (rset.getString("PIPN") != null) {
                    citedby.setPage((rset.getString("PIPN")).trim());
                }

                if (rset.getString("ANUM") != null) {
                    citedby.setAccessionNumber((rset.getString("ANUM")).trim());
                }

                // System.out.println("**ABSTRACT** REFERENCE-COUNT= "+rset.getString("XREFNO")+" CITATION= "+getCitationCount(rset.getClob("CITATION")));
                if (rset.getString("XREFNO") != null && getCitationCount(rset.getClob("CITATION")) > 0) {

                    // ht.put(Keys.REFCNT, new XMLWrapper(Keys.REFCNT, rset.getString("XREFNO")));
                }

                
                if (rset.getString("PVOLISS") != null) {

                    String volumeIssue = rset.getString("PVOLISS");
                    volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                    volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);

                    if (perl.match("/,/", volumeIssue)) {
                        citedby.setVolume((perl.preMatch()).trim());
                        citedby.setIssue((perl.postMatch()).trim());
                    } else {
                        volumeIssue = rset.getString("PVOLISS");
                        if (perl.match("/vol/i", volumeIssue)) {
                            volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                            citedby.setVolume(volumeIssue.trim());
                        } else if (perl.match("/no/i", volumeIssue)) {
                            volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                            citedby.setIssue(volumeIssue.trim());
                        }
                    }
                }

                ht.put(Keys.CITEDBY, citedby);

                // end citedby

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
        String dataFormat = FullDoc.FULLDOC_FORMAT;
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
                } else {
                    String spyear = selectPubYear(rset.getString("sspdate"), rset.getString("fdate"), rset.getString("cdate"), rset.getString("su"));
                    if (spyear != null) {
                        ht.put(Keys.PUBLICATION_YEAR, new Year(spyear, perl));
                    }
                }

                if (rset.getString("PIPN") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(rset.getString("PIPN"), perl));
                }

                String strRTYPE = rset.getString("RTYPE");
                ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, replaceDTNullWithEmptyString(rset.getString("NRTYPE"))));

                // ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, replaceTYwithRIScode(rset.getString("NRTYPE"))));

                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ANUM")));
                }
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("TI")));
                }

                if (strRTYPE.equals("08")) {
                    if (rset.getString("AUS") != null) {

                        Contributors inventors = new Contributors(Keys.INVENTORS, getContributors(Keys.INVENTORS, rset.getString("AUS"),
                            rset.getString("AUS2"), dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {
                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));
                        }
                        ht.put(Keys.INVENTORS, inventors);
                    }
                } else {
                    if (rset.getString("AUS") != null) {
                        String aus = rset.getString("AUS");
                        if (rset.getString("AUS2") != null) {
                            aus = aus.concat(rset.getString("AUS2"));
                        }

                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(Keys.AUTHORS, rset.getString("AUS"), rset.getString("AUS2"),
                            dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        ht.put(Keys.AUTHORS, authors);

                        if (rset.getString("AAFF") != null) {
                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));
                        }

                    } else {
                        if (rset.getString("EDS") != null) {
                            String strED = StringUtil.replaceNullWithEmptyString(rset.getString("EDS"));
                            if (perl.match("/(Ed[.]\\s*)/", strED)) {
                                strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                            }

                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(Keys.EDITORS, strED, null, dataFormat,
                                rset.getString("EAFFMULTI1"), rset.getString("EDS")));
                            ht.put(Keys.EDITORS, editors);

                            if (rset.getString("EAFF") != null) {
                                ht.put(
                                    Keys.EDITOR_AFFS,
                                    getAuthorsAffiliation(Keys.EDITOR_AFFS, rset.getString("EAFF"), rset.getString("EFC"), rset.getString("EAFFMULTI1"),
                                        rset.getString("EAFFMULTI2"), dataFormat));
                            }

                        }
                    }
                }

                String strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("PFJT"));
                String strAJT = StringUtil.replaceNullWithEmptyString(rset.getString("PUBTI"));
                String strABRJT = StringUtil.replaceNullWithEmptyString(rset.getString("PAJT"));

                if (rset.getString("TRMC") != null) {
                    ht.put(Keys.TREATMENTS, new Treatments(setTreatments(rset.getString("TRMC")), this.database));
                }

                if ((strRTYPE.equals("02")) || (strRTYPE.equals("05") && (!strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (!strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("PFJT"))));

                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("PAJT"))));

                    if (rset.getString("PPUB") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PPUB"))));
                    }

                    // VO
                    if (rset.getString("PVOL") != null) {
                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, rset.getString("PVOL"), perl));
                    }
                    // ISS
                    if (rset.getString("PISS") != null) {
                        ht.put(Keys.ISSUE, new Issue(rset.getString("PISS"), perl));
                    }
                    if (rset.getString("SFJT") != null) {
                        ht.put(Keys.TRANSLATION_SERIAL_TITLE,
                            new XMLWrapper(Keys.TRANSLATION_SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("SFJT"))));
                    }
                    if (rset.getString("SAJT") != null) {
                        ht.put(Keys.TRANSLATION_ABBREVIATED_SERIAL_TITLE,
                            new XMLWrapper(Keys.TRANSLATION_ABBREVIATED_SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("SAJT"))));
                    }

                    if (rset.getString("SVOL") != null) {
                        ht.put(Keys.TRANSLATION_VOLUME, new XMLWrapper(Keys.TRANSLATION_VOLUME, StringUtil.replaceNullWithEmptyString(rset.getString("SVOL"))));
                    }
                    if (rset.getString("SISS") != null) {
                        ht.put(Keys.TRANSLATION_ISSUE, new XMLWrapper(Keys.TRANSLATION_ISSUE, StringUtil.replaceNullWithEmptyString(rset.getString("SISS"))));
                    }
                    if (rset.getString("SOPDATE") != null) {
                        ht.put(Keys.TRANSLATION_PUBLICATION_DATE,
                            new XMLWrapper(Keys.PUBLICATION_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("SOPDATE"))));
                    }
                    if (rset.getString("SIPN") != null) {
                        ht.put(Keys.TRANSLATION_PAGES, new XMLWrapper(Keys.TRANSLATION_PAGES, StringUtil.replaceNullWithEmptyString(rset.getString("SIPN"))));
                    }
                    if (rset.getString("SSN") != null) {
                        ht.put(Keys.TRANSLATION_ISSN, new XMLWrapper(Keys.TRANSLATION_ISSN, StringUtil.replaceNullWithEmptyString(rset.getString("SSN"))));
                    }
                    if (rset.getString("SCDN") != null) {
                        ht.put(Keys.TRANSLATION_CODEN, new XMLWrapper(Keys.TRANSLATION_CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("SCDN"))));
                    }
                    if (rset.getString("SCPUB") != null) {
                        ht.put(Keys.TRANSLATION_COUNTRY_OF_PUB,
                            new XMLWrapper(Keys.TRANSLATION_COUNTRY_OF_PUB, StringUtil.replaceNullWithEmptyString(rset.getString("SCPUB"))));
                    }

                } else if ((strRTYPE.equals("03")) || (strRTYPE.equals("04")) || (strRTYPE.equals("05") && (strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    if (strAJT != null && !(strAJT.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));
                    }

                    if (rset.getString("PPUB") != null && !(rset.getString("PPUB").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PPUB"))));
                    }

                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {
                    if ((strAJT != null) && !(strAJT.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));
                    }

                    if (rset.getString("IORG") != null && !(rset.getString("IORG").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.ISSUING_ORG, new XMLWrapper(Keys.ISSUING_ORG, replaceInvisibleChar(rset.getString("IORG"))));
                    }
                    // jam 5/12/2003 Thes release testing - rule incorrectly copied from Citation
                    // IORG and PUB both show in detailed!!!
                    if (rset.getString("PPUB") != null && !(rset.getString("PPUB").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PPUB")));
                    }
                    if (rset.getString("RNUM") != null && !(rset.getString("RNUM").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, rset.getString("RNUM")));
                    }

                } else if (strRTYPE.equals("08")) {
                    if ((rset.getString("OFDATE") != null) && !(rset.getString("OFDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, rset.getString("OFDATE")));
                    }
                    if ((rset.getString("PAS") != null) && !(rset.getString("PAS").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, rset.getString("PAS")));
                    }
                    if ((rset.getString("OPAN") != null) && !(rset.getString("OPAN").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATAPPNUM, new XMLWrapper(Keys.PATAPPNUM, rset.getString("OPAN")));
                    }
                    if ((rset.getString("PNUM") != null) && !(rset.getString("PNUM").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, rset.getString("PNUM")));
                    }
                    if ((rset.getString("COPA") != null) && !(rset.getString("COPA").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, rset.getString("COPA")));
                    }
                }
                if (rset.getString("POPDATE") != null && !(rset.getString("POPDATE").equals(StringUtil.EMPTY_STRING))) {
                    ht.put(Keys.PUBLICATION_DATE, new XMLWrapper(Keys.PUBLICATION_DATE, rset.getString("POPDATE")));
                }

                String strPages = StringUtil.EMPTY_STRING;
                if (rset.getString("PIPN") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("PIPN"));
                } else if (rset.getString("NPL1") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"));
                } else if (rset.getString("NPL2") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("NPL2"));
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
                if (rset.getString("LA") != null && !(rset.getString("LA").equals(StringUtil.EMPTY_STRING))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, replaceInvisibleChar(StringUtil.replaceNullWithEmptyString(rset.getString("LA")))));
                }
                // PSN
                if (rset.getString("PSN") != null && !(rset.getString("PSN").equals(StringUtil.EMPTY_STRING))) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("PSN")));
                }
                // PCDN
                if (rset.getString("PCDN") != null && !(rset.getString("PCDN").equals(StringUtil.EMPTY_STRING))) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, rset.getString("PCDN")));
                }
                // SBN
                if (rset.getString("SBN") != null && !(rset.getString("SBN").equals(StringUtil.EMPTY_STRING))) {
                    ht.put(Keys.ISBN, new ISBN(rset.getString("SBN")));
                }

                if (rset.getString("TC") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("TC")));
                } else {

                    if (strRTYPE.equals("06")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("PUBTI")));
                    } else if (strRTYPE.equals("05")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("TI")));
                    }
                }
                if (rset.getString("CODATE") != null) {
                    ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, rset.getString("CODATE")));
                }
                if (rset.getString("CLOC") != null) {
                    ht.put(Keys.MEETING_LOCATION, new XMLWrapper(Keys.MEETING_LOCATION, rset.getString("CLOC")));
                }
                if (rset.getString("SORG") != null) {
                    ht.put(Keys.SPONSOR, new XMLWrapper(Keys.SPONSOR, replaceInvisibleChar(StringUtil.replaceNullWithEmptyString(rset.getString("SORG")))));
                }

                if (rset.getString("BPPUB") != null) {
                    ht.put(Keys.PUB_PLACE, new XMLWrapper(Keys.PUB_PLACE, rset.getString("BPPUB")));
                } else {
                    ht.put(Keys.PUB_LOCATION, new XMLWrapper(Keys.PUB_LOCATION, rset.getString("PCPUB")));
                }

                if (!strRTYPE.equals("08")) {
                    String strPartNo = StringUtil.replaceNullWithEmptyString(rset.getString("PARTNO"));
                    if (!strPartNo.equals(StringUtil.EMPTY_STRING)) {
                        if (perl.match("/(\\d+)/", strPartNo)) {
                            ht.put(Keys.PART_NUMBER, new XMLWrapper(Keys.PART_NUMBER, perl.group(0).toString()));
                        }
                    }
                }

                if (rset.getString("MATID") != null) {
                    String matid = null;
                    if (rset.getString("MATID").lastIndexOf("-") > 0) {
                        matid = rset.getString("MATID").substring(0, rset.getString("MATID").lastIndexOf("-"));
                    }
                    if (!((matid).equals("")) && matid != null) {
                        ht.put(Keys.MATERIAL_ID, new XMLWrapper(Keys.MATERIAL_ID, matid));
                    }
                }

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                if (rset.getString("XREFNO") != null) {
                    ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, rset.getString("XREFNO")));
                }
                // CVS
                if ((rset.getString("CVS") != null)) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(INS_CONTROLLED_TERMS, setElementData(rset.getString("CVS"))));
                }
                // FLS
                if (rset.getString("FLS") != null) {
                    ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(rset.getString("FLS"))));
                }
                // TR
                if (rset.getString("TRMC") != null) {
                    ht.put(Keys.TREATMENTS, new Treatments(setTreatments(rset.getString("TRMC")), this.database));
                }
                // CLS
                if (rset.getString("CLS") != null) {
                    ht.put(Keys.CLASS_CODES, new Classifications(INS_CLASS_CODES, setElementData(rset.getString("CLS")), this.database));
                }
                // NDI
                if (rset.getString("NDI") != null) {
                    ht.put(Keys.NUMERICAL_DATA_INDEXING,
                        new XMLWrapper(Keys.NUMERICAL_DATA_INDEXING, replaceIndextermsDelim(StringUtil.replaceNullWithEmptyString(rset.getString("NDI")))));
                }
                // CHI
                if (rset.getString("CHI") != null) {
                    ht.put(Keys.CHEMICAL_DATA_INDEXING,
                        new XMLWrapper(Keys.CHEMICAL_DATA_INDEXING, replaceIndextermsDelim(StringUtil.replaceNullWithEmptyString(rset.getString("CHI")))));
                }
                // AOI
                if (rset.getString("AOI") != null) {
                    ht.put(Keys.ASTRONOMICAL_OBJECT_INDEXING,
                        new XMLWrapper(Keys.ASTRONOMICAL_OBJECT_INDEXING, replaceIndextermsDelim(StringUtil.replaceNullWithEmptyString(rset.getString("AOI")))));
                }
                // CLS
                if ((rset.getString("CLS") != null)) {
                    ht.put(Keys.DISCIPLINES, new XMLMultiWrapper(Keys.DISCIPLINES, setDisciplineElementData(rset.getString("CLS"))));
                }

                // Used in RIS format ONLY
                if (rset.getString("PURL") != null) {
                    ht.put(Keys.DOC_URL, new XMLWrapper(Keys.DOC_URL, rset.getString("PURL")));
                }

                // DOI
                if (rset.getString("PDOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("PDOI")));
                }

                // IPC
                if (rset.getString("IPC") != null) {
                    ht.put(Keys.INTERNATCL_CODE, new IPCClassifications(Keys.INTERNATCL_CODE, setElementData(rset.getString("IPC")), this.database));
                }

                if (rset.getString("CPR") != null) {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, StringUtil.replaceNullWithEmptyString(rset.getString("CPR"))));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, StringUtil.replaceNullWithEmptyString(rset.getString("CPR"))));
                } else {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, InspecDocBuilder.INS_TEXT_COPYRIGHT));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, InspecDocBuilder.INS_TEXT_COPYRIGHT));

                }

                // added for CITEDBY

                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ANUM")));
                }

                CITEDBY citedby = new CITEDBY();
                citedby.setKey(Keys.CITEDBY);

                if (rset.getString("PDOI") != null) {
                    citedby.setDoi(URLEncoder.encode((rset.getString("PDOI")).trim()));
                }
                
                if (rset.getString("PSN") != null) {
                    citedby.setIssn((rset.getString("PSN")).trim());
                }
                
                if (rset.getString("SBN") != null) {
                    citedby.setIsbn((rset.getString("SBN")).trim());
                }

                if (rset.getString("PIPN") != null) {
                    citedby.setPage((rset.getString("PIPN")).trim());
                }

                if (rset.getString("ANUM") != null) {
                    citedby.setAccessionNumber(rset.getString("ANUM"));
                }

                if (rset.getString("XREFNO") != null && getCitationCount(rset.getClob("CITATION")) > 0) {

                    // ht.put(Keys.REFCNT, new XMLWrapper(Keys.REFCNT, rset.getString("XREFNO")));
                }

                if (rset.getString("PVOLISS") != null) {

                    String volumeIssue = rset.getString("PVOLISS");
                    volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                    volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);

                    if (perl.match("/,/", volumeIssue)) {
                        citedby.setVolume((perl.preMatch()).trim());
                        citedby.setIssue((perl.postMatch()).trim());
                    } else {
                        volumeIssue = rset.getString("PVOLISS");
                        if (perl.match("/vol/i", volumeIssue)) {
                            volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                            citedby.setVolume(volumeIssue.trim());
                        } else if (perl.match("/no/i", volumeIssue)) {
                            volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                            citedby.setIssue(volumeIssue.trim());
                        }
                    }
                }

                ht.put(Keys.CITEDBY, citedby);

                // end citedby

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
        String dataFormat = "RIS";
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
                    ht.put(Keys.RIS_M1, new XMLWrapper(Keys.RIS_M1, StringUtil.replaceNullWithEmptyString(rset.getString("CPR"))));
                } else {
                    ht.put(Keys.RIS_M1, new XMLWrapper(Keys.RIS_M1, InspecDocBuilder.INS_TEXT_COPYRIGHT));
                }
                ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, replaceTYwithRIScode(rset.getString("NRTYPE"))));
                ht.put(Keys.BIB_TY, new XMLWrapper(Keys.BIB_TY, replaceTYwithBIBcode(rset.getString("NRTYPE"))));

                if (rset.getString("LA") != null) {
                    ht.put(Keys.RIS_LA, new XMLWrapper(Keys.RIS_LA, replaceInvisibleChar(rset.getString("LA"))));
                }

                String strRTYPE = rset.getString("RTYPE");

                if (rset.getString("TI") != null) {
                    ht.put(Keys.RIS_TI, new XMLWrapper(Keys.RIS_TI, rset.getString("TI")));
                }

                if (strRTYPE.equals("08")) {
                    if (rset.getString("AUS") != null) {
                        Contributors inventors = new Contributors(Keys.INVENTORS, getContributors(Keys.INVENTORS, rset.getString("AUS"),
                            rset.getString("AUS2"), dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));
                        inventors.nullAffilID();
                        if (rset.getString("AAFF") != null) {
                            ht.put(
                                Keys.RIS_AD,
                                getAuthorsAffiliation(Keys.RIS_AD, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));

                        }
                        ht.put(Keys.RIS_AUS, inventors);
                    }

                } else {
                    if (rset.getString("AUS") != null) {

                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(Keys.AUTHORS, rset.getString("AUS"), rset.getString("AUS2"),
                            dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        authors.nullAffilID();
                        if (rset.getString("AAFF") != null) {
                            ht.put(
                                Keys.RIS_AD,
                                getAuthorsAffiliation(Keys.RIS_AD, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));
                        }
                        ht.put(Keys.RIS_AUS, authors);
                    } else {
                        if (rset.getString("EDS") != null) {
                            String strED = StringUtil.replaceNullWithEmptyString(rset.getString("EDS"));
                            if (perl.match("/(Ed[.]\\s*)/", strED)) {
                                strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                            }

                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(Keys.EDITORS, rset.getString("EDS"), null, dataFormat,
                                rset.getString("EAFFMULTI1"), rset.getString("EDS")));

                            editors.nullAffilID();
                            ht.put(Keys.RIS_EDS, editors);

                            if (rset.getString("EAFF") != null) {

                                ht.put(Keys.RIS_AD, getAuthorsAffiliation(Keys.RIS_AD, rset.getString("EAFF"), null, null, null, dataFormat));
                            }

                        }
                    }
                }

                String strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("PFJT"));
                String strAJT = StringUtil.EMPTY_STRING;
                if (rset.getString("PAJT") != null) {
                    strAJT = StringUtil.replaceNullWithEmptyString(rset.getString("PAJT"));
                } else if (rset.getString("PUBTI") != null) {
                    strAJT = StringUtil.replaceNullWithEmptyString(rset.getString("PUBTI"));
                }

                if (strAJT != null && (!strAJT.equals(StringUtil.EMPTY_STRING))) {
                    if (strRTYPE.equals("04") || strRTYPE.equals("12")) {
                        ht.put(Keys.RIS_T2, new XMLWrapper(Keys.RIS_T2, strAJT));
                    } else {
                        ht.put(Keys.RIS_T3, new XMLWrapper(Keys.RIS_T3, strAJT));
                    }
                }

                if ((strRTYPE.equals("02")) || (strRTYPE.equals("05") && (!strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (!strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    if (strFJT != null && (!strFJT.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.RIS_JO, new XMLWrapper(Keys.RIS_JO, strFJT));
                    }

                    if (rset.getString("PPUB") != null) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, rset.getString("PPUB")));
                    }

                    if (rset.getString("PVOL") != null) {
                        String strVol = replaceVolumeNullWithEmptyString(rset.getString("PVOL"));
                        strVol = perl.substitute("s/[vol\\.]//gi", strVol);
                        ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, strVol));
                    }

                    if (rset.getString("PISS") != null) {
                        String strIss = replaceIssueNullWithEmptyString(rset.getString("PISS"));
                        strIss = perl.substitute("s/[no\\.]//gi", strIss);
                        ht.put(Keys.RIS_IS, new Issue(strIss, perl));
                    }

                    // Error - overwriting T1 - FTTJ not defined for RIS
                    // ht.put("T1",StringUtil.replaceNullWithEmptyString(rset.getString("FTTJ")));

                } else if ((strRTYPE.equals("03")) || (strRTYPE.equals("04")) || (strRTYPE.equals("05") && (strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    if (rset.getString("PPUB") != null) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, rset.getString("PPUB")));
                    }
                    if (rset.getString("PARTNO") != null) {
                        ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, rset.getString("PARTNO")));
                    }

                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {

                    if (rset.getString("IORG") != null) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, replaceInvisibleChar(rset.getString("IORG"))));
                    } else if (rset.getString("PPUB") != null) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, rset.getString("PPUB")));
                    }
                    if (rset.getString("RNUM") != null) {
                        ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, rset.getString("RNUM")));
                    }

                } else if (strRTYPE.equals("08")) {

                    ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, rset.getString("PNUM")));
                    if ((rset.getString("OFDATE") != null) && !(rset.getString("OFDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.RIS_Y2, new XMLWrapper(Keys.RIS_Y2, rset.getString("OFDATE")));
                    }
                    if ((rset.getString("PAS") != null) && !(rset.getString("PAS").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.RIS_Y1, new XMLWrapper(Keys.RIS_Y1, rset.getString("PAS")));
                    }
                    if ((rset.getString("PAS") != null) && !(rset.getString("PAS").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.RIS_A2, new XMLWrapper(Keys.RIS_A2, rset.getString("PAS")));
                    }
                    if ((rset.getString("PNUM") != null) && !(rset.getString("PNUM").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, rset.getString("PNUM")));
                    }
                    if ((rset.getString("COPA") != null) && !(rset.getString("COPA").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, rset.getString("COPA")));
                    }

                }

                // This code takes a date in a variety of different formats.
                // Parses it into a Date object and then outputs it in the RIS format
                // Known input Formats:
                // 1 Mar 2003
                // Mar.-Apr 2002
                // March 3, 2002
                // Mar. 2002
                // 2002
                // If parsing the date fails, the raw field from the DB is used
                String strDate = StringUtil.replaceNullWithEmptyString(rset.getString("POPDATE"));

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
                    strDate = StringUtil.replaceNullWithEmptyString(rset.getString("POPDATE"));
                }
                ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, strDate, perl));

                String strPages = StringUtil.EMPTY_STRING;
                if (rset.getString("PIPN") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("PIPN"));
                } else if (rset.getString("NPL1") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"));
                } else if (rset.getString("NPL2") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("NPL2"));
                }
                if ((strPages != null) && !(strPages.equals(StringUtil.EMPTY_STRING))) {
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

                if (rset.getString("PSN") != null) {
                    ht.put(Keys.RIS_SN, new ISSN(rset.getString("PSN")));
                }
                if (rset.getString("SBN") != null) {
                    ht.put(Keys.RIS_S1, new ISBN(rset.getString("SBN")));
                }

                if (rset.getString("TC") != null) {
                    ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, rset.getString("TC")));
                } else {
                    if (strRTYPE.equals("06")) {
                        ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, rset.getString("PUBTI")));
                    } else if (strRTYPE.equals("05")) {
                        ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, rset.getString("TI")));
                    }
                }
                if (rset.getString("CODATE") != null) {
                    ht.put(Keys.RIS_MD, new XMLWrapper(Keys.RIS_MD, rset.getString("CODATE")));
                }

                /*
                 * JM 10/22/2008 Stop putting Conference Location in CY field. CY should be City of publication. Code was left since we may find another RIS
                 * field to put it in
                 */
                /*
                 * /* if(!strRTYPE.equals("08") && rset.getString("CLOC") != null) { ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, rset.getString("CLOC"))); }
                 */
                /* JM 10/23/2008 Added Publisher address fields for CY */
                if (rset.getString("BPPUB") != null) {
                    ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, rset.getString("BPPUB")));
                } else if (rset.getString("PCPUB") != null) {
                    ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, rset.getString("PCPUB")));
                }

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, abs));
                }

                if (rset.getString("PURL") != null) {
                    ht.put(Keys.RIS_UR, new XMLWrapper(Keys.RIS_UR, rset.getString("PURL")));
                }
                if (rset.getString("PUM") != null) {
                    ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, rset.getString("PUM")));
                }

                // CVS/CV
                if (rset.getString("CVS") != null) {
                    ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(rset.getString("CVS"))));
                }

                // FLS
                if (rset.getString("FLS") != null) {
                    ht.put(Keys.RIS_FLS, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(rset.getString("FLS"))));
                }
                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.RIS_AN, new XMLWrapper(Keys.RIS_AN, rset.getString("ANUM")));
                }

                // DO
                if (rset.getString("PDOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("PDOI")));
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
        String dataFormat = Citation.CITATION_FORMAT;

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

                // Needed for IVIP
                if (rset.getString("PSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("PSN")));
                }

                if (rset.getString("PIPN") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(rset.getString("PIPN"), perl));
                }

                String strRTYPE = StringUtil.replaceNullWithEmptyString(rset.getString("RTYPE"));

                // TI
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                }
                if (strRTYPE.equals("08")) {
                    if (rset.getString("AUS") != null) {

                        Contributors inventors = new Contributors(Keys.INVENTORS, getContributors(Keys.INVENTORS, rset.getString("AUS"),
                            rset.getString("AUS2"), dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {

                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));

                        }
                        ht.put(Keys.INVENTORS, inventors);
                    }

                } else {
                    if (rset.getString("AUS") != null) {
                        Contributors inventors = new Contributors(Keys.AUTHORS, getContributors(Keys.AUTHORS, rset.getString("AUS"), rset.getString("AUS2"),
                            dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {
                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));
                        }
                        ht.put(Keys.AUTHORS, inventors);

                    } else {
                        if (rset.getString("EDS") != null) {
                            String strED = StringUtil.replaceNullWithEmptyString(rset.getString("EDS"));
                            if (perl.match("/(Ed[.]\\s*)/", strED)) {
                                strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                            }

                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(Keys.EDITORS, strED, null, dataFormat,
                                rset.getString("EAFFMULTI1"), rset.getString("EDS")));

                            ht.put(Keys.EDITORS, editors);

                            if (rset.getString("EAFF") != null) {
                                ht.put(
                                    Keys.EDITOR_AFFS,
                                    getAuthorsAffiliation(Keys.EDITOR_AFFS, rset.getString("EAFF"), rset.getString("EFC"), rset.getString("EAFFMULTI1"),
                                        rset.getString("EAFFMULTI2"), dataFormat));
                            }
                        }
                    }
                }
                String strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("PFJT"));
                String strAJT = StringUtil.replaceNullWithEmptyString(rset.getString("PUBTI"));

                if ((strRTYPE.equals("02")) || (strRTYPE.equals("05") && (!strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (!strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strFJT));

                    if (rset.getString("SFJT") != null) {
                        ht.put(Keys.TRANSLATION_SERIAL_TITLE, new XMLWrapper(Keys.TRANSLATION_SERIAL_TITLE, TRANS_SEE_DETAILED));
                    }

                    if (rset.getString("PVOLISS") != null) {

                        String volumeIssue = rset.getString("PVOLISS");
                        volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                        volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, volumeIssue));

                        if (perl.match("/,/", volumeIssue)) {
                            ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, perl.preMatch(), perl));
                            ht.put(Keys.ISSUE, new Issue(perl.postMatch(), perl));
                        } else {
                            volumeIssue = rset.getString("PVOLISS");
                            if (perl.match("/vol/i", volumeIssue)) {
                                volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                                ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, volumeIssue, perl));
                            } else if (perl.match("/no/i", volumeIssue)) {
                                volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                                ht.put(Keys.ISSUE, new Issue(volumeIssue, perl));
                            }
                        }

                    }
                } else if ((strRTYPE.equals("03")) || (strRTYPE.equals("04")) || (strRTYPE.equals("05") && (strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));
                    if (strAJT.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PPUB"))));
                    }
                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {

                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));

                    if (rset.getString("IORG") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("IORG"))));
                    } else {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PPUB"))));
                    }

                    if (rset.getString("POPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("POPDATE"))));
                    } else if (rset.getString("OFDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("OFDATE"))));
                    }
                    if (rset.getString("RNUM") != null) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("RNUM"))));
                    }
                } else if (strRTYPE.equals("08")) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    // RTYPE='08'
                    if ((rset.getString("OFDATE") != null) && !(rset.getString("OFDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, rset.getString("OFDATE")));
                    }
                    if ((rset.getString("PAS") != null) && !(rset.getString("PAS").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, rset.getString("PAS")));
                    }
                    if ((rset.getString("OPAN") != null) && !(rset.getString("OPAN").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATAPPNUM, new XMLWrapper(Keys.PATAPPNUM, rset.getString("OPAN")));
                    }
                    if ((rset.getString("PNUM") != null) && !(rset.getString("PNUM").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, rset.getString("PNUM")));
                    }
                    if ((rset.getString("COPA") != null) && !(rset.getString("COPA").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, rset.getString("COPA")));
                    }
                    if ((rset.getString("POPDATE") != null) && !(rset.getString("POPDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATPUBDATE, new XMLWrapper(Keys.PATPUBDATE, rset.getString("POPDATE")));
                    }

                }

                // SD
                if (!strRTYPE.equals("08")) {
                    if (rset.getString("POPDATE") != null) {
                        String strYR = rset.getString("POPDATE");
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strYR));
                    } else if (rset.getString("OFDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("OFDATE")));
                    } else if (rset.getString("SOPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("SOPDATE")));
                    }
                }

                if (rset.getString("PARTNO") != null) {
                    String PartNumber = getPartNumber(rset.getString("PARTNO"));
                    String partNo = "pt. ".concat(StringUtil.replaceNullWithEmptyString(PartNumber));
                    ht.put(Keys.PART_NUMBER, new XMLWrapper(Keys.PART_NUMBER, partNo));
                }

                // PP
                if (rset.getString("PIPN") != null) {
                    ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("PIPN"))));
                } else {
                    if (rset.getString("NPL1") != null) {
                        ht.put(Keys.PAGE_RANGE_pp, new XMLWrapper(Keys.PAGE_RANGE_pp, StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"))));
                    } else {
                        if (rset.getString("NPL2") != null) {
                            ht.put(Keys.PAGE_RANGE_pp, new XMLWrapper(Keys.PAGE_RANGE_pp, StringUtil.replaceNullWithEmptyString(rset.getString("NPL2"))));
                        }
                    }
                }

                // LA
                if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, replaceInvisibleChar(rset.getString("LA"))));
                }

                // DOI
                if (rset.getString("PDOI") != null) {

                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, StringUtil.replaceNullWithEmptyString(rset.getString("PDOI"))));
                }

                // jam - Itegrated changes in jamaica build with
                // changes made in Inspec Backfile EV2

                // SBN
                if (rset.getString("SBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("SBN"))));
                }
                // ANUM
                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("ANUM"))));
                }
                // CN
                if (rset.getString("PCDN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("PCDN"))));
                }
                // FJT
                if (rset.getString("PFJT") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("PFJT")));
                }

                // AJT
                if (rset.getString("PAJT") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("PAJT")));
                }

                // PDATE
                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                } else {
                    String spyear = selectPubYear(rset.getString("sspdate"), rset.getString("fdate"), rset.getString("cdate"), rset.getString("su"));
                    if (spyear != null) {
                        ht.put(Keys.PUBLICATION_YEAR, new Year(spyear, perl));
                    }
                }

                if (rset.getString("TC") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("TC")));
                } else {

                    if (strRTYPE.equals("06")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("PUBTI")));
                    } else if (strRTYPE.equals("05")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("TI")));
                    }
                }

                // added for CITEDBY

                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ANUM")));
                }

                CITEDBY citedby = new CITEDBY();
                citedby.setKey(Keys.CITEDBY);

                if (rset.getString("PDOI") != null) {
                    citedby.setDoi(URLEncoder.encode((rset.getString("PDOI")).trim()));
                }
                
                if (rset.getString("PSN") != null) {
                    citedby.setIssn((rset.getString("PSN")).trim());
                }
                
                if (rset.getString("SBN") != null) {
                    citedby.setIsbn((rset.getString("SBN")).trim());
                }

                if (rset.getString("PIPN") != null) {
                    citedby.setPage((rset.getString("PIPN")).trim());
                }

                if (rset.getString("ANUM") != null) {
                    citedby.setAccessionNumber(rset.getString("ANUM"));
                }

                if (rset.getString("PVOLISS") != null) {

                    String volumeIssue = rset.getString("PVOLISS");
                    volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                    volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);

                    if (perl.match("/,/", volumeIssue)) {
                        citedby.setVolume((perl.preMatch()).trim());
                        citedby.setIssue((perl.postMatch()).trim());
                    } else {
                        volumeIssue = rset.getString("PVOLISS");
                        if (perl.match("/vol/i", volumeIssue)) {
                            volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                            citedby.setVolume(volumeIssue.trim());
                        } else if (perl.match("/no/i", volumeIssue)) {
                            volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                            citedby.setIssue(volumeIssue.trim());
                        }
                    }
                }

                ht.put(Keys.CITEDBY, citedby);

                // end citedby

                // reference count

                if (rset.getString("XREFNO") != null && getCitationCount(rset.getClob("CITATION")) > 0) {

                    // ht.put(Keys.NUMBER_OF_REFERENCES,new XMLWrapper(Keys.NUMBER_OF_REFERENCES,
                    // StringUtil.replaceNullWithEmptyString(rset.getString("XREFNO"))));
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

    private Hashtable<String, InspecWrapper> loadREFCitationsWithAccessnumber(List<String> listOfAcceesionNumber) throws DocumentBuilderException {

        Perl5Util perl = new Perl5Util();

        Hashtable<String, InspecWrapper> docTable = new Hashtable<String, InspecWrapper>();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildAccessNumberINString(listOfAcceesionNumber);
        String dataFormat = Citation.CITATION_FORMAT;

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryREFCitation + INString);
            while (rset.next()) {
                String accessNumber = null;
                ElementDataMap ht = new ElementDataMap();
                String m_id = rset.getString("M_ID");
                DocID did = new DocID(rset.getString("M_ID"), database);
                ht.put(Keys.DOCID, did);
                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));
                if (rset.getString("CPR") != null) {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, rset.getString("CPR")));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, rset.getString("CPR")));
                } else {
                    ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, INS_HTML_COPYRIGHT));
                    ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, INS_TEXT_COPYRIGHT));
                }

                // Needed for IVIP
                if (rset.getString("PSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("PSN")));
                }

                if (rset.getString("PIPN") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(rset.getString("PIPN"), perl));
                }

                String strRTYPE = StringUtil.replaceNullWithEmptyString(rset.getString("RTYPE"));

                // TI
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                }
                if (strRTYPE.equals("08")) {
                    if (rset.getString("AUS") != null) {

                        Contributors inventors = new Contributors(Keys.INVENTORS, getContributors(Keys.INVENTORS, rset.getString("AUS"),
                            rset.getString("AUS2"), dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {

                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));

                        }
                        ht.put(Keys.INVENTORS, inventors);
                    }

                } else {
                    if (rset.getString("AUS") != null) {
                        Contributors inventors = new Contributors(Keys.AUTHORS, getContributors(Keys.AUTHORS, rset.getString("AUS"), rset.getString("AUS2"),
                            dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {
                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));
                        }
                        ht.put(Keys.AUTHORS, inventors);

                    } else {
                        if (rset.getString("EDS") != null) {
                            String strED = StringUtil.replaceNullWithEmptyString(rset.getString("EDS"));
                            if (perl.match("/(Ed[.]\\s*)/", strED)) {
                                strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                            }

                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(Keys.EDITORS, strED, null, dataFormat,
                                rset.getString("EAFFMULTI1"), rset.getString("EDS")));

                            ht.put(Keys.EDITORS, editors);

                            if (rset.getString("EAFF") != null) {
                                ht.put(
                                    Keys.EDITOR_AFFS,
                                    getAuthorsAffiliation(Keys.EDITOR_AFFS, rset.getString("EAFF"), rset.getString("EFC"), rset.getString("EAFFMULTI1"),
                                        rset.getString("EAFFMULTI2"), dataFormat));
                            }
                        }
                    }
                }
                String strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("PFJT"));
                String strAJT = StringUtil.replaceNullWithEmptyString(rset.getString("PUBTI"));

                if ((strRTYPE.equals("02")) || (strRTYPE.equals("05") && (!strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (!strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strFJT));

                    if (rset.getString("SFJT") != null) {
                        ht.put(Keys.TRANSLATION_SERIAL_TITLE, new XMLWrapper(Keys.TRANSLATION_SERIAL_TITLE, TRANS_SEE_DETAILED));
                    }

                    if (rset.getString("PVOLISS") != null) {

                        String volumeIssue = rset.getString("PVOLISS");
                        volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                        volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, volumeIssue));

                        if (perl.match("/,/", volumeIssue)) {
                            ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, perl.preMatch(), perl));
                            ht.put(Keys.ISSUE, new Issue(perl.postMatch(), perl));
                        } else {
                            volumeIssue = rset.getString("PVOLISS");
                            if (perl.match("/vol/i", volumeIssue)) {
                                volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                                ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, volumeIssue, perl));
                            } else if (perl.match("/no/i", volumeIssue)) {
                                volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                                ht.put(Keys.ISSUE, new Issue(volumeIssue, perl));
                            }
                        }

                    }
                } else if ((strRTYPE.equals("03")) || (strRTYPE.equals("04")) || (strRTYPE.equals("05") && (strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));
                    if (strAJT.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PPUB"))));
                    }
                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {

                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));

                    if (rset.getString("IORG") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("IORG"))));
                    } else {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PPUB"))));
                    }

                    if (rset.getString("POPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("POPDATE"))));
                    } else if (rset.getString("OFDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("OFDATE"))));
                    }
                    if (rset.getString("RNUM") != null) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("RNUM"))));
                    }
                } else if (strRTYPE.equals("08")) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    // RTYPE='08'
                    if ((rset.getString("OFDATE") != null) && !(rset.getString("OFDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, rset.getString("OFDATE")));
                    }
                    if ((rset.getString("PAS") != null) && !(rset.getString("PAS").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, rset.getString("PAS")));
                    }
                    if ((rset.getString("OPAN") != null) && !(rset.getString("OPAN").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATAPPNUM, new XMLWrapper(Keys.PATAPPNUM, rset.getString("OPAN")));
                    }
                    if ((rset.getString("PNUM") != null) && !(rset.getString("PNUM").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, rset.getString("PNUM")));
                    }
                    if ((rset.getString("COPA") != null) && !(rset.getString("COPA").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, rset.getString("COPA")));
                    }
                    if ((rset.getString("POPDATE") != null) && !(rset.getString("POPDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATPUBDATE, new XMLWrapper(Keys.PATPUBDATE, rset.getString("POPDATE")));
                    }

                }

                // SD
                if (!strRTYPE.equals("08")) {
                    if (rset.getString("POPDATE") != null) {
                        String strYR = rset.getString("POPDATE");
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strYR));
                    } else if (rset.getString("OFDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("OFDATE")));
                    } else if (rset.getString("SOPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("SOPDATE")));
                    }
                }

                if (rset.getString("PARTNO") != null) {
                    String PartNumber = getPartNumber(rset.getString("PARTNO"));
                    String partNo = "pt. ".concat(StringUtil.replaceNullWithEmptyString(PartNumber));
                    ht.put(Keys.PART_NUMBER, new XMLWrapper(Keys.PART_NUMBER, partNo));
                }

                // PP
                if (rset.getString("PIPN") != null) {
                    ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("PIPN"))));
                } else {
                    if (rset.getString("NPL1") != null) {
                        ht.put(Keys.PAGE_RANGE_pp, new XMLWrapper(Keys.PAGE_RANGE_pp, StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"))));
                    } else {
                        if (rset.getString("NPL2") != null) {
                            ht.put(Keys.PAGE_RANGE_pp, new XMLWrapper(Keys.PAGE_RANGE_pp, StringUtil.replaceNullWithEmptyString(rset.getString("NPL2"))));
                        }
                    }
                }

                // LA
                if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, replaceInvisibleChar(rset.getString("LA"))));
                }

                // DOI
                if (rset.getString("PDOI") != null) {

                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, StringUtil.replaceNullWithEmptyString(rset.getString("PDOI"))));
                }

                // jam - Itegrated changes in jamaica build with
                // changes made in Inspec Backfile EV2

                // SBN
                if (rset.getString("SBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("SBN"))));
                }
                // ANUM
                if (rset.getString("ANUM") != null) {
                    accessNumber = rset.getString("ANUM");
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("ANUM"))));
                }
                // CN
                if (rset.getString("PCDN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("PCDN"))));
                }
                // FJT
                if (rset.getString("PFJT") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("PFJT")));
                }

                // AJT
                if (rset.getString("PAJT") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("PAJT")));
                }

                // PDATE
                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                } else {
                    String spyear = selectPubYear(rset.getString("sspdate"), rset.getString("fdate"), rset.getString("cdate"), rset.getString("su"));
                    if (spyear != null) {
                        ht.put(Keys.PUBLICATION_YEAR, new Year(spyear, perl));
                    }
                }

                if (rset.getString("TC") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("TC")));
                } else {

                    if (strRTYPE.equals("06")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("PUBTI")));
                    } else if (strRTYPE.equals("05")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("TI")));
                    }
                }

                // added for CITEDBY

                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ANUM")));
                }

                CITEDBY citedby = new CITEDBY();
                citedby.setKey(Keys.CITEDBY);

                if (rset.getString("PDOI") != null) {
                    citedby.setDoi(URLEncoder.encode((rset.getString("PDOI")).trim()));
                }
                
                if (rset.getString("PSN") != null) {
                    citedby.setIssn((rset.getString("PSN")).trim());
                }
                
                if (rset.getString("SBN") != null) {
                    citedby.setIsbn((rset.getString("SBN")).trim());
                }

                if (rset.getString("PIPN") != null) {
                    citedby.setPage((rset.getString("PIPN")).trim());
                }

                if (rset.getString("ANUM") != null) {
                    citedby.setAccessionNumber(rset.getString("ANUM"));
                }

                if (rset.getString("PVOLISS") != null) {

                    String volumeIssue = rset.getString("PVOLISS");
                    volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                    volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);

                    if (perl.match("/,/", volumeIssue)) {
                        citedby.setVolume((perl.preMatch()).trim());
                        citedby.setIssue((perl.postMatch()).trim());
                    } else {
                        volumeIssue = rset.getString("PVOLISS");
                        if (perl.match("/vol/i", volumeIssue)) {
                            volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                            citedby.setVolume(volumeIssue.trim());
                        } else if (perl.match("/no/i", volumeIssue)) {
                            volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                            citedby.setIssue(volumeIssue.trim());
                        }
                    }

                   
                }

                ht.put(Keys.CITEDBY, citedby);

                // end citedby

                // reference count

                String refCount = rset.getString("XREFNO");
                if (refCount != null && rset.getClob("CITATION") != null && StringUtil.getStringFromClob(rset.getClob("CITATION")).length() > 1) {

                    ht.put(Keys.REFCNT, new XMLWrapper(Keys.REFCNT, refCount));

                    ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, StringUtil.replaceNullWithEmptyString(refCount)));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(CITATION_KEYS);
                InspecWrapper w = new InspecWrapper();
                w.eiDoc = eiDoc;
                w.mid = m_id;
                docTable.put(accessNumber, w);
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

        return docTable;

    } // loadREFCitationsWithAccessnumber

    private List<InspecWrapper> loadREFCitations(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();

        List<InspecWrapper> list = new ArrayList<InspecWrapper>();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;

        String INString = buildINString(listOfDocIDs);
        String dataFormat = Citation.CITATION_FORMAT;

        String refCount = "0";
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryCitation + INString);
            String citationString = null;
            String accessNumber = null;
            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();
                String m_id = rset.getString("M_ID");
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

                // Needed for IVIP
                if (rset.getString("PSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("PSN")));
                }

                if (rset.getString("PIPN") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(rset.getString("PIPN"), perl));
                }

                String strRTYPE = StringUtil.replaceNullWithEmptyString(rset.getString("RTYPE"));

                // TI
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                }
                if (strRTYPE.equals("08")) {
                    if (rset.getString("AUS") != null) {

                        Contributors inventors = new Contributors(Keys.INVENTORS, getContributors(Keys.INVENTORS, rset.getString("AUS"),
                            rset.getString("AUS2"), dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {

                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));

                        }
                        ht.put(Keys.INVENTORS, inventors);
                    }

                } else {
                    if (rset.getString("AUS") != null) {
                        Contributors inventors = new Contributors(Keys.AUTHORS, getContributors(Keys.AUTHORS, rset.getString("AUS"), rset.getString("AUS2"),
                            dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {
                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));
                        }
                        ht.put(Keys.AUTHORS, inventors);

                    } else {
                        if (rset.getString("EDS") != null) {
                            String strED = StringUtil.replaceNullWithEmptyString(rset.getString("EDS"));
                            if (perl.match("/(Ed[.]\\s*)/", strED)) {
                                strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                            }

                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(Keys.EDITORS, strED, null, dataFormat,
                                rset.getString("EAFFMULTI1"), rset.getString("EDS")));

                            ht.put(Keys.EDITORS, editors);

                            if (rset.getString("EAFF") != null) {
                                ht.put(
                                    Keys.EDITOR_AFFS,
                                    getAuthorsAffiliation(Keys.EDITOR_AFFS, rset.getString("EAFF"), rset.getString("EFC"), rset.getString("EAFFMULTI1"),
                                        rset.getString("EAFFMULTI2"), dataFormat));
                            }
                        }
                    }
                }
                String strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("PFJT"));
                String strAJT = StringUtil.replaceNullWithEmptyString(rset.getString("PUBTI"));

                if ((strRTYPE.equals("02")) || (strRTYPE.equals("05") && (!strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (!strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strFJT));

                    if (rset.getString("SFJT") != null) {
                        ht.put(Keys.TRANSLATION_SERIAL_TITLE, new XMLWrapper(Keys.TRANSLATION_SERIAL_TITLE, TRANS_SEE_DETAILED));
                    }

                    if (rset.getString("PVOLISS") != null) {

                        String volumeIssue = rset.getString("PVOLISS");
                        volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                        volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, volumeIssue));

                        if (perl.match("/,/", volumeIssue)) {
                            ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, perl.preMatch(), perl));
                            ht.put(Keys.ISSUE, new Issue(perl.postMatch(), perl));
                        } else {
                            volumeIssue = rset.getString("PVOLISS");
                            if (perl.match("/vol/i", volumeIssue)) {
                                volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                                ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, volumeIssue, perl));
                            } else if (perl.match("/no/i", volumeIssue)) {
                                volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                                ht.put(Keys.ISSUE, new Issue(volumeIssue, perl));
                            }
                        }

                    }
                } else if ((strRTYPE.equals("03")) || (strRTYPE.equals("04")) || (strRTYPE.equals("05") && (strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));
                    if (strAJT.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PPUB"))));
                    }
                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {

                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));

                    if (rset.getString("IORG") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("IORG"))));
                    } else {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PPUB"))));
                    }

                    if (rset.getString("POPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("POPDATE"))));
                    } else if (rset.getString("OFDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("OFDATE"))));
                    }
                    if (rset.getString("RNUM") != null) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("RNUM"))));
                    }
                } else if (strRTYPE.equals("08")) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    // RTYPE='08'
                    if ((rset.getString("OFDATE") != null) && !(rset.getString("OFDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, rset.getString("OFDATE")));
                    }
                    if ((rset.getString("PAS") != null) && !(rset.getString("PAS").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, rset.getString("PAS")));
                    }
                    if ((rset.getString("OPAN") != null) && !(rset.getString("OPAN").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATAPPNUM, new XMLWrapper(Keys.PATAPPNUM, rset.getString("OPAN")));
                    }
                    if ((rset.getString("PNUM") != null) && !(rset.getString("PNUM").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, rset.getString("PNUM")));
                    }
                    if ((rset.getString("COPA") != null) && !(rset.getString("COPA").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, rset.getString("COPA")));
                    }
                    if ((rset.getString("POPDATE") != null) && !(rset.getString("POPDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATPUBDATE, new XMLWrapper(Keys.PATPUBDATE, rset.getString("POPDATE")));
                    }

                }

                // SD
                if (!strRTYPE.equals("08")) {
                    if (rset.getString("POPDATE") != null) {
                        String strYR = rset.getString("POPDATE");
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strYR));
                    } else if (rset.getString("OFDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("OFDATE")));
                    } else if (rset.getString("SOPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("SOPDATE")));
                    }
                }

                if (rset.getString("PARTNO") != null) {
                    String PartNumber = getPartNumber(rset.getString("PARTNO"));
                    String partNo = "pt. ".concat(StringUtil.replaceNullWithEmptyString(PartNumber));
                    ht.put(Keys.PART_NUMBER, new XMLWrapper(Keys.PART_NUMBER, partNo));
                }

                // PP
                if (rset.getString("PIPN") != null) {
                    ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("PIPN"))));
                } else {
                    if (rset.getString("NPL1") != null) {
                        ht.put(Keys.PAGE_RANGE_pp, new XMLWrapper(Keys.PAGE_RANGE_pp, StringUtil.replaceNullWithEmptyString(rset.getString("NPL1"))));
                    } else {
                        if (rset.getString("NPL2") != null) {
                            ht.put(Keys.PAGE_RANGE_pp, new XMLWrapper(Keys.PAGE_RANGE_pp, StringUtil.replaceNullWithEmptyString(rset.getString("NPL2"))));
                        }
                    }
                }

                // LA
                if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, replaceInvisibleChar(rset.getString("LA"))));
                }

                // DOI
                if (rset.getString("PDOI") != null) {

                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, StringUtil.replaceNullWithEmptyString(rset.getString("PDOI"))));
                }

                // jam - Itegrated changes in jamaica build with
                // changes made in Inspec Backfile EV2

                // SBN
                if (rset.getString("SBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("SBN"))));
                }

                // ANUM
                if (rset.getString("ANUM") != null) {
                    accessNumber = rset.getString("ANUM");
                }

                // CN
                if (rset.getString("PCDN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("PCDN"))));
                }
                // FJT
                if (rset.getString("PFJT") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("PFJT")));
                }

                // AJT
                if (rset.getString("PAJT") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("PAJT")));
                }

                // PDATE
                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                } else {
                    String spyear = selectPubYear(rset.getString("sspdate"), rset.getString("fdate"), rset.getString("cdate"), rset.getString("su"));
                    if (spyear != null) {
                        ht.put(Keys.PUBLICATION_YEAR, new Year(spyear, perl));
                    }
                }

                if (rset.getString("TC") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("TC")));
                } else {

                    if (strRTYPE.equals("06")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("PUBTI")));
                    } else if (strRTYPE.equals("05")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("TI")));
                    }
                }

                // added for CITEDBY

                if (rset.getString("ANUM") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ANUM")));
                }

                CITEDBY citedby = new CITEDBY();
                citedby.setKey(Keys.CITEDBY);

                if (rset.getString("PDOI") != null) {
                    citedby.setDoi(URLEncoder.encode((rset.getString("PDOI")).trim()));
                }

                if (rset.getString("PSN") != null) {
                    citedby.setIssn((rset.getString("PSN")).trim());
                }
                
                if (rset.getString("SBN") != null) {
                    citedby.setIsbn((rset.getString("SBN")).trim());
                }

                if (rset.getString("PIPN") != null) {
                    citedby.setPage((rset.getString("PIPN")).trim());
                }

                if (rset.getString("ANUM") != null) {
                    citedby.setAccessionNumber(rset.getString("ANUM"));
                }
                
                if (rset.getString("PVOLISS") != null) {

                    String volumeIssue = rset.getString("PVOLISS");
                    volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                    volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);

                    if (perl.match("/,/", volumeIssue)) {
                        citedby.setVolume((perl.preMatch()).trim());
                        citedby.setIssue((perl.postMatch()).trim());
                    } else {
                        volumeIssue = rset.getString("PVOLISS");
                        if (perl.match("/vol/i", volumeIssue)) {
                            volumeIssue = perl.substitute("s/vol\\./v /i", volumeIssue);
                            citedby.setVolume(volumeIssue.trim());
                        } else if (perl.match("/no/i", volumeIssue)) {
                            volumeIssue = perl.substitute("s/no\\./n /i", volumeIssue);
                            citedby.setIssue(volumeIssue.trim());
                        }
                    }
                }

                ht.put(Keys.CITEDBY, citedby);

                // end citedby

                // reference count

                refCount = rset.getString("XREFNO");
                if (refCount != null && rset.getClob("CITATION") != null && StringUtil.getStringFromClob(rset.getClob("CITATION")).length() > 1) {

                    ht.put(Keys.REFCNT, new XMLWrapper(Keys.REFCNT, refCount));
                    citationString = StringUtil.getStringFromClob(rset.getClob("CITATION"));

                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(CITATION_KEYS);
                InspecWrapper w = new InspecWrapper();
                w.eiDoc = eiDoc;
                w.mid = m_id;
                list.add(w);
                count++;
            } // while

            if (citationString != null) {
                List<InspecWrapper> citationList = parseCitationContent(citationString, accessNumber, refCount);
                Collections.sort(citationList, new MIDComparator());
                list.addAll(citationList);
            }

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

    } // loadREFCitation

    public List<?> getRefPager(String parentID) throws DocumentBuilderException {

        List<DocID> list = new ArrayList<DocID>();
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        Database ins = databaseConfig.getDatabase("ins");
        list.add(new DocID(parentID, ins));
        List<?> eiDoc = buildPage(list, Citation.CITATION_FORMAT_REF);
        return eiDoc;
    }

    class MIDComparator implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            InspecWrapper w1 = (InspecWrapper) o1;
            InspecWrapper w2 = (InspecWrapper) o2;

            return w1.mid.compareTo(w2.mid);
        }
    }

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
        String dataFormat = Citation.XMLCITATION_FORMAT;
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
                if (rset.getString("PSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("PSN")));
                }
                String strRTYPE = StringUtil.replaceNullWithEmptyString(rset.getString("RTYPE"));

                // TI
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("TI")));
                }

                if (strRTYPE.equals("08")) {
                    if (rset.getString("AUS") != null) {
                        Contributors inventors = new Contributors(Keys.INVENTORS, getContributors(Keys.INVENTORS, rset.getString("AUS"),
                            rset.getString("AUS2"), dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {
                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));
                        }
                        ht.put(Keys.INVENTORS, inventors);
                    }
                } else {
                    if (rset.getString("AUS") != null) {
                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(Keys.AUTHORS, rset.getString("AUS"), rset.getString("AUS2"),
                            dataFormat, rset.getString("AAFFMULTI1"), rset.getString("AAFF")));

                        if (rset.getString("AAFF") != null) {

                            ht.put(
                                Keys.AUTHOR_AFFS,
                                getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AAFF"), rset.getString("AFC"), rset.getString("AAFFMULTI1"),
                                    rset.getString("AAFFMULTI2"), dataFormat));
                        }
                        ht.put(Keys.AUTHORS, authors);

                    } else {
                        if (rset.getString("EDS") != null) {
                            String strED = StringUtil.replaceNullWithEmptyString(rset.getString("EDS"));
                            if (perl.match("/(Ed[.]\\s*)/", strED)) {
                                strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                            }

                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(Keys.EDITORS, strED, null, dataFormat,
                                rset.getString("EAFFMULTI1"), rset.getString("EDS")));

                            ht.put(Keys.EDITORS, editors);

                            if (rset.getString("EAFF") != null) {
                                ht.put(
                                    Keys.EDITOR_AFFS,
                                    getAuthorsAffiliation(Keys.EDITOR_AFFS, rset.getString("EAFF"), rset.getString("EFC"), rset.getString("EAFFMULTI1"),
                                        rset.getString("EAFFMULTI2"), dataFormat));
                            }
                        }
                    }
                }

                String strFJT = StringUtil.replaceNullWithEmptyString(rset.getString("PFJT"));
                String strAJT = StringUtil.replaceNullWithEmptyString(rset.getString("PUBTI"));

                if ((strRTYPE.equals("02")) || (strRTYPE.equals("05") && (!strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (!strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    if (!strFJT.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strFJT));
                    }

                    if (rset.getString("SFJT") != null) {
                        ht.put(Keys.TRANSLATION_SERIAL_TITLE, new XMLWrapper(Keys.TRANSLATION_SERIAL_TITLE, TRANS_SEE_DETAILED));
                    }

                    String strVolIss = StringUtil.EMPTY_STRING;

                    if (rset.getString("PVOL") != null) {
                        strVolIss = strVolIss.concat(rset.getString("PVOL"));
                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, rset.getString("PVOL"), perl));
                    }

                    if (rset.getString("PISS") != null) {
                        ht.put(Keys.ISSUE, new Issue(rset.getString("PISS"), perl));

                        if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                            strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(rset.getString("PISS")));
                        } else {
                            strVolIss = replaceIssueNullWithEmptyString(rset.getString("PISS"));
                        }
                    }

                    ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));

                } else if ((strRTYPE.equals("03")) || (strRTYPE.equals("04")) || (strRTYPE.equals("05") && (strFJT.equals(StringUtil.EMPTY_STRING)))
                    || (strRTYPE.equals("06") && (strFJT.equals(StringUtil.EMPTY_STRING)))) {
                    if (!strAJT.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));
                    }
                    if (strAJT.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PPUB")));
                    }
                } else if ((strRTYPE.equals("10")) || (strRTYPE.equals("11")) || (strRTYPE.equals("12"))) {
                    if (!strAJT.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, strAJT));
                    }

                    if (rset.getString("IORG") != null) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, replaceInvisibleChar(rset.getString("IORG"))));
                    } else {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PPUB")));
                    }

                    if (rset.getString("POPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("POPDATE")));
                    } else if (rset.getString("OFDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("OFDATE")));
                    }
                    ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, rset.getString("RNUM")));

                } else if (strRTYPE.equals("08")) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                    // RTYPE='08'
                    if ((rset.getString("OFDATE") != null) && !(rset.getString("OFDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, rset.getString("OFDATE")));
                    }
                    if ((rset.getString("PAS") != null) && !(rset.getString("PAS").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, rset.getString("PAS")));
                    }
                    if ((rset.getString("OPAN") != null) && !(rset.getString("OPAN").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATAPPNUM, new XMLWrapper(Keys.PATAPPNUM, rset.getString("OPAN")));
                    }
                    if ((rset.getString("PNUM") != null) && !(rset.getString("PNUM").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, rset.getString("PNUM")));
                    }
                    if ((rset.getString("COPA") != null) && !(rset.getString("COPA").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, rset.getString("COPA")));
                    }
                    if ((rset.getString("POPDATE") != null) && !(rset.getString("POPDATE").equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PATPUBDATE, new XMLWrapper(Keys.PATPUBDATE, rset.getString("POPDATE")));
                    }

                }

                // SD
                if (!strRTYPE.equals("08")) {
                    if (rset.getString("POPDATE") != null) {
                        String strYR = rset.getString("POPDATE");
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strYR));
                    } else if (rset.getString("OFDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("OFDATE")));
                    } else if (rset.getString("SOPDATE") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("SOPDATE")));
                    }
                }

                if (rset.getString("PARTNO") != null) {
                    String PartNumber = getPartNumber(rset.getString("PARTNO"));
                    String partNo = "pt. ".concat(StringUtil.replaceNullWithEmptyString(PartNumber));
                    ht.put(Keys.PART_NUMBER, new XMLWrapper(Keys.PART_NUMBER, partNo));
                }

                // PP
                if (rset.getString("PIPN") != null) {
                    String pageRange = rset.getString("PIPN").replaceAll("p", " ");
                    ht.put(Keys.PAGE_RANGE, new PageRange(pageRange, perl));
                    // ht.put(Keys.PAGE_RANGE,new XMLWrapper(Keys.PAGE_RANGE , pageRange));
                } else if (rset.getString("NPL1") != null) {
                    String pageCount = rset.getString("NPL1").replaceAll("p", " ");
                    ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, pageCount));
                } else if (rset.getString("NPL2") != null) {
                    String pageCount = rset.getString("NPL2").replaceAll("p", " ");
                    ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, pageCount));
                }

                // LA
                if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, replaceInvisibleChar(rset.getString("LA"))));
                }

                // DOI
                if (rset.getString("PDOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("PDOI")));
                }

                // CVS
                if (rset.getString("CVS") != null) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(INS_CONTROLLED_TERMS, setCVS(rset.getString("CVS"))));
                }

                // ISBN
                if (rset.getString("SBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("SBN"))));
                }

                // PN
                if (rset.getString("PPUB") != null) {
                    List<String> lstPub = new ArrayList<String>();
                    lstPub.add(rset.getString("PPUB"));

                    if (rset.getString("BPPUB") != null) {
                        lstPub.add(rset.getString("BPPUB"));
                    } else if (rset.getString("PCPUB") != null) {
                        lstPub.add(rset.getString("PCPUB"));
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

    private List<InspecWrapper> parseCitationContent(String citationString, String anum, String refCount) throws DocumentBuilderException {
        String[] citationArray = null;
        Hashtable<String, InspecWrapper> docTable = new Hashtable<String, InspecWrapper>();

        List<String> docidList = new ArrayList<String>();
        String accessNumber = "0";
        if (citationString != null) {
            citationArray = citationString.split(AUDELIMITER, -1);
        }

        for (int i = 0; i < citationArray.length; i++) {
            ElementDataMap ht = new ElementDataMap();
            String citationRecordString = citationArray[i];
            if (citationRecordString != null) {
                String[] citationRecordArray = citationRecordString.split(IDDELIMITER, -1);
                StringBuffer sourceString = new StringBuffer();
                String serialTitle = null;
                String AbbrSerialTitle = null;
                String volumeString = null;
                String issueString = null;
                String pageString = null;
                String pageCountString = null;
                String yearString = null;
                String dateString = null;
                DocID did = null;
                String m_id = null;
                String doi = null;
                String rawString = null;
                // System.out.println("********************************");
                for (int j = 0; j < citationRecordArray.length; j++) {

                    String field = citationRecordArray[j];
                    String fieldName = "";
                    String fieldValue = "";
                    if (field.indexOf("::") > -1) {
                        fieldName = field.substring(0, field.indexOf("::"));
                        fieldValue = field.substring(field.indexOf("::") + 2);
                    }

                    if (fieldValue.length() > 0)
                        // System.out.println(i+" fieldName= "+fieldName+" fieldValue= "+fieldValue);

                        if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("DOI")) {
                            doi = fieldValue;
                            ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, doi));
                            // System.out.println("ANUM= "+accessNumber+" DOI= "+fieldValue);

                        }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("ACCESSION_NUMBER")) {
                        ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, fieldValue));
                        accessNumber = fieldValue;
                        docidList.add(accessNumber);
                        m_id = "ref_ins_" + accessNumber;
                        did = new DocID(i + 1, m_id, database);

                    } else if (fieldName.equalsIgnoreCase("ACCESSION_NUMBER")) {
                        accessNumber = "ref_ins_" + anum + "_" + i;
                        ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, accessNumber));
                        did = new DocID(i + 1, accessNumber, database);
                        m_id = accessNumber;
                    }
                    // System.out.println("DATABASE= "+database.getID());

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("AUTHOR")) {
                        Contributors authors = new Contributors(Keys.AUTHORS, getRefContributors(Keys.AUTHORS, fieldValue));
                        ht.put(Keys.AUTHORS, authors);
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("YEAR")) {
                        yearString = fieldValue;
                        ht.put(Keys.PUBLICATION_YEAR, new XMLWrapper(Keys.PUBLICATION_YEAR, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("PUBLICATION_DATE")) {
                        dateString = getPublicationData(fieldValue);
                        ht.put(Keys.PUBLICATION_YEAR, new XMLWrapper(Keys.PUBLICATION_DATE, dateString));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("JOURNAL_TITLE")) {
                        serialTitle = fieldValue;
                        ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("PUBLCATION_TITLE")) {
                        serialTitle = fieldValue;
                        ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("SERIES_TITLE")) {
                        serialTitle = fieldValue;
                        ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("ABBR_TITLE")) {
                        AbbrSerialTitle = fieldValue;
                        ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("ABBR_JOURNAL_TITLE")) {
                        AbbrSerialTitle = fieldValue;
                        ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("ISSN")) {
                        ht.put(Keys.ISSN, new ISSN(fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("VOLUME_ISSUE")) {

                        String volumeIssue = fieldValue;
                        if (volumeIssue.indexOf(GROUPDELIMITER) > -1) {
                            String[] volumeIssueArray = volumeIssue.split(GROUPDELIMITER, -1);
                            if (volumeIssueArray.length > 3) {
                                if (volumeIssueArray[0].length() > 0) {
                                    ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, volumeIssueArray[0]));
                                    volumeString = volumeIssueArray[0];
                                }
                                if (volumeIssueArray[1].length() > 0) {
                                    ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, volumeIssueArray[1], perl));
                                    volumeString = volumeIssueArray[1];
                                }
                                if (volumeIssueArray[2].length() > 0) {
                                    issueString = volumeIssueArray[2];
                                    ht.put(Keys.ISSUE, new Issue(Keys.ISSUE, volumeIssueArray[2], perl));
                                }
                            }
                        }
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("PAGE")) {
                        pageCountString = fieldValue;
                        ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("FIRST_PAGE")) {
                        pageString = fieldValue;
                        ht.put(Keys.START_PAGE, new XMLWrapper(Keys.START_PAGE, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("TITLE")) {
                        ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("PUBLISHER")) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("EDITOR")) {
                        // ht.put(Keys.EDITORS, new XMLWrapper(Keys.EDITORS, fieldValue));
                        Contributors editors = new Contributors(Keys.EDITORS, getRefContributors(Keys.EDITORS, fieldValue));
                        ht.put(Keys.EDITORS, editors);
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("COUNTRY")) {
                        ht.put(Keys.COUNTRY_OF_PUB, new XMLWrapper(Keys.COUNTRY_OF_PUB, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("ISBN")) {
                        ht.put(Keys.ISBN, new XMLWrapper(Keys.ISBN, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("CONFERENCE")) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("REPORT_NUMBER")) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("PATENT_DETAIL")) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, fieldValue));
                    }

                    if (fieldValue.length() > 0 && fieldName.equalsIgnoreCase("RAW")) {
                        rawString = fieldValue;
                    }

                }

                if (serialTitle != null) {
                    sourceString.append(serialTitle + ", ");
                } else if (AbbrSerialTitle != null) {
                    sourceString.append(AbbrSerialTitle + ", ");
                }

                if (volumeString != null) {
                    sourceString.append("v " + volumeString + ", ");
                }

                if (issueString != null) {
                    sourceString.append("n " + issueString + ", ");
                }

                if (pageCountString != null) {
                    sourceString.append("p " + pageCountString + ", ");
                } else if (pageString != null) {
                    sourceString.append("p " + pageString + ", ");
                }

                // System.out.println("dateString= "+dateString);
                if (dateString != null) {
                    sourceString.append(dateString + ", ");
                } else if (yearString != null) {
                    sourceString.append(yearString + ", ");
                }

                if (sourceString.length() > 2 && sourceString.charAt(sourceString.length() - 2) == ',') {
                    sourceString.deleteCharAt(sourceString.length() - 2);
                } else if (sourceString.length() < 1 && rawString != null) {
                    sourceString.append(rawString);
                } else {
                    sourceString.append("Information is not available yet");
                }

                if (refCount == null || refCount.length() == 0) {
                    refCount = "0";
                }
                ht.put(Keys.REFCNT, new XMLWrapper(Keys.REFCNT, refCount));

                if (sourceString.length() > 0) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, sourceString.toString()));
                }
                // System.out.println("index= "+(i+1)+" sourceString= "+sourceString);
                if (doi != null) {
                    did.setDoi(doi);
                }
                ht.put(Keys.DOCID, did);
                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(201199);
                eiDoc.setOutputKeys(CITATION_KEYS);
                InspecWrapper w = new InspecWrapper();
                w.eiDoc = eiDoc;
                w.mid = m_id;
                // docTable.add(eiDoc);
                docTable.put(accessNumber, w);
            }

        }
        Hashtable<String, InspecWrapper> docWithAccessnumber = null;
        if (docidList != null && docidList.size() > 0) {
            docWithAccessnumber = loadREFCitationsWithAccessnumber(docidList);
        }
        // if(docWithAccessnumber != null)
        // {
        // docTable.addAll(docWithAccessnumber);
        // }
        // return docList;

        return updateDoc(docTable, docWithAccessnumber);

    }// parseCitationContent

    private String getPublicationData(String date) {
        String day = null;
        String month = null;
        String year = null;
        StringBuffer dateBuffer = new StringBuffer();
        HashMap<String, String> monthConversionTable = new HashMap<String, String>();
        monthConversionTable.put("1", "January");
        monthConversionTable.put("2", "February");
        monthConversionTable.put("3", "March");
        monthConversionTable.put("4", "April");
        monthConversionTable.put("5", "may");
        monthConversionTable.put("6", "June");
        monthConversionTable.put("7", "July");
        monthConversionTable.put("8", "August");
        monthConversionTable.put("9", "September");
        monthConversionTable.put("10", "October");
        monthConversionTable.put("11", "November");
        monthConversionTable.put("12", "December");
        if (date.indexOf(GROUPDELIMITER) > -1) {
            String[] whichDate = date.split(GROUPDELIMITER, -1);
            for (int i = 0; i < whichDate.length; i++) {
                String thisDate = whichDate[i];
                if (thisDate != null && thisDate.length() > 0) {
                    if (thisDate.indexOf("-") > -1) {
                        String[] dateArray = whichDate[i].split("-", -1);
                        if (dateArray.length == 3) {
                            day = dateArray[0];
                            month = dateArray[1];
                            year = dateArray[2];

                            if (day != null && day.length() > 0) {
                                dateBuffer.append(day + " ");
                            }

                            if (month != null && month.length() > 0) {
                                if (monthConversionTable.get(month) != null) {
                                    dateBuffer.append(monthConversionTable.get(month) + " ");
                                } else {
                                    dateBuffer.append(month + " ");
                                }
                            }

                            if (year != null && year.length() > 0) {
                                dateBuffer.append(year);
                            }
                        }

                    } else {
                        dateBuffer.append(thisDate);
                    }
                    break;
                }
            }
        }

        return dateBuffer.toString();
    }

    private List<InspecWrapper> updateDoc(Hashtable<String, InspecWrapper> docTable, Hashtable<String, InspecWrapper> docWithAccessnumber) throws DocumentBuilderException {
        StringBuffer docidBuffer = new StringBuffer();
        List<InspecWrapper> newDocList = new ArrayList<InspecWrapper>();
        // System.out.println("SIZE= "+docTable.size());
        /*
         * if(docTable != null) { Enumeration key = docTable.keys(); while(key.hasMoreElements()) { String aNumber = (String)key.nextElement(); InspecWrapper
         * insDoc = (InspecWrapper)docTable.get(aNumber); EIDoc doc = insDoc.getEiDoc(); DocID docID = doc.getDocID(); int hitIndex = docID.getHitIndex();
         * System.out.println("oldHitIndex= "+hitIndex); } } System.out.println("SIZE1= "+docWithAccessnumber.size());
         */
        try {
            if (docWithAccessnumber != null) {
                Enumeration<String> keyEnum = docWithAccessnumber.keys();
                while (keyEnum.hasMoreElements()) {
                    String aNumber = (String) keyEnum.nextElement();
                    InspecWrapper insOldDoc = (InspecWrapper) docTable.get(aNumber);
                    EIDoc oldDoc = insOldDoc.eiDoc;
                    DocID oldDocID = oldDoc.getDocID();
                    int hitIndex = oldDocID.getHitIndex();
                    InspecWrapper insNewDoc = (InspecWrapper) docWithAccessnumber.get(aNumber);
                    EIDoc newDoc = insNewDoc.eiDoc;
                    DocID newDocID = newDoc.getDocID();
                    String m_id = newDocID.getDocID();
                    newDocID.setHitIndex(hitIndex);
                    // newDoc.setDocID(newDocID);
                    InspecWrapper w = new InspecWrapper();
                    w.eiDoc = newDoc;
                    w.mid = m_id;
                    docTable.put(aNumber, w);
                    // System.out.println("hitIndex= "+hitIndex);
                }
            }

            Collection<InspecWrapper> c = docTable.values();
            newDocList = new ArrayList<InspecWrapper>(c);

        } catch (Exception e) {
            e.printStackTrace();
        } // try/finally

        return newDocList;

    }// updateDoc

    // new method for multy authors

    private Affiliations getAuthorsAffiliation(Key key, String affFirstString, String country, String affString, String affString1, String dataFormat)
        throws DocumentBuilderException {

        StringBuffer affBuffer = new StringBuffer();
        if (affString == null || affString.trim().equals("")) {
            if (affFirstString != null && !affFirstString.trim().equals("")) {
                if (country != null) {
                    affFirstString = affFirstString.concat(BdParser.IDDELIMITER).concat(country);
                }
                affBuffer.append(affFirstString);
            }
        }
        if (affString != null) {
            affBuffer.append(affString);
        }

        if (affString1 != null) {
            affBuffer.append(affString1);
        }

        if (affBuffer.length() > 0) {
            List<Affiliation> affList = new ArrayList<Affiliation>();
            InspecAffiliations aff = new InspecAffiliations(affBuffer.toString());
            List<BdAffiliation> aList = aff.getAffiliations();
            for (int i = 0; i < aList.size(); i++) {
                BdAffiliation bdaff = (BdAffiliation) aList.get(i);
                String strAffDisplay = bdaff.getDisplayValue();

                if (i == 0 && dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT)) {
                    affList.add(new Affiliation(key, strAffDisplay));
                    return (new Affiliations(Keys.RIS_AD, affList));
                } else if (dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT) && i == 0) {
                    affList.add(new Affiliation(key, strAffDisplay));
                    return (new Affiliations(Keys.AUTHOR_AFFS, affList));
                } else {
                    affList.add(new Affiliation(key, strAffDisplay, bdaff.getIdDislpayValue()));
                }
            }
            return (new Affiliations(key, affList));
        }
        return null;
    }

    private List<Contributor> getRefContributors(Key key, String auString)

    throws DocumentBuilderException {

        if (auString != null && auString.length() > 0) {
            List<Contributor> authorNames = new ArrayList<Contributor>();
            InspecAuthors authors = new InspecAuthors(auString, "ref");

            List<BdAuthor> authorList = authors.getInspecAuthors();
            for (int i = 0; i < authorList.size(); i++) {
                BdAuthor author = (BdAuthor) authorList.get(i);
                String email = author.getInitials();// inspect use initials as email address holder
                String auDisplayName = author.getDisplayName();

                Contributor persons = new Contributor(key, auDisplayName);
                if (email != null && !email.equals("")) {
                    persons.setEmail(email);
                }
                authorNames.add(persons);
            }
            return authorNames;
        }
        return null;
    }

    private List<Contributor> getContributors(Key key, String authorString, String authorString1, String dataFormat, String affiliations,
        String FirstAuAffiliations) throws DocumentBuilderException {
        StringBuffer auString = new StringBuffer();

        if (authorString != null) {
            auString.append(authorString);
        }
        if (authorString1 != null) {
            auString.append(authorString1);
        }

        if (auString != null && auString.length() > 0) {
            List<Contributor> authorNames = new ArrayList<Contributor>();
            InspecAuthors authors = new InspecAuthors(auString.toString());

            List<BdAuthor> authorList = authors.getInspecAuthors();
            for (int i = 0; i < authorList.size(); i++) {
                BdAuthor author = (BdAuthor) authorList.get(i);
                String email = author.getInitials();// inspect use initials as email address holder
                String auDisplayName = author.getDisplayName();
                if (dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT) || dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT)) {
                    authorNames.add(new Contributor(key, auDisplayName));
                } else if ((affiliations != null && !affiliations.trim().equals("")) || (FirstAuAffiliations != null && !FirstAuAffiliations.trim().equals(""))) {
                    Contributor persons = new Contributor(key, auDisplayName, author.getAffIdList());
                    if (email != null && !email.equals("")) {
                        persons.setEmail(email);
                    }
                    authorNames.add(persons);
                } else {
                    Contributor persons = new Contributor(key, auDisplayName);
                    if (email != null && !email.equals("")) {
                        persons.setEmail(email);
                    }
                    authorNames.add(persons);

                }
            }
            return authorNames;
        }
        return null;
    }

    /*
     * This method builds the IN String from list of docId objects. The select query will get the result set in a reverse way So in order to get in correct
     * order we are doing a reverse example of return String--(23,22,1,12...so on);
     *
     * @param listOfDocIDs
     *
     * @return String
     */
    private String buildINString(List<DocID> listOfDocIDs) {
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

    /*
     * This method builds the IN String from list of docId objects. The select query will get the result set in a reverse way So in order to get in correct
     * order we are doing a reverse example of return String--(23,22,1,12...so on);
     *
     * @param listOfDocIDs
     *
     * @return String
     */
    private String buildAccessNumberINString(List<String> listOfAccessNumber) {
        StringBuffer sQuery = new StringBuffer();
        sQuery.append("(");
        for (int k = listOfAccessNumber.size(); k > 0; k--) {
            String accessnumber = (String) listOfAccessNumber.get(k - 1);

            if ((k - 1) == 0) {
                sQuery.append("'" + accessnumber + "'");
            } else {
                sQuery.append("'" + accessnumber + "'").append(",");
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

    private String hasAbstract(ResultSet rs) throws SQLException  {
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
        StringTokenizer st = new StringTokenizer(cvs, AUDELIMITER);
        String strToken = null;

        while (st.hasMoreTokens()) {
            strToken = st.nextToken().trim();
            if (strToken.length() > 0) {
                KeyValuePair k = new KeyValuePair(Keys.CONTROLLED_TERM, strToken);
                list.add(k);
            }
        }
        return (KeyValuePair[]) list.toArray(new KeyValuePair[list.size()]);

    }

    private String replaceDisciplinesWithDescriptions(String strDISCIPLINES) {

        StringTokenizer st = new StringTokenizer(strDISCIPLINES, ";");
        String strToken;
        StringBuffer strResult = new StringBuffer();
        List<String> lstUsedDisciplines = new ArrayList<String>();

        while (st.hasMoreTokens()) {
            strToken = st.nextToken();

            if (!lstUsedDisciplines.contains(strToken.substring(0, 1))) {

                lstUsedDisciplines.add(strToken.substring(0, 1));

                strResult.append("<");
                strResult.append(EIDoc.DISCIPLINE);
                strResult.append(">");

                strResult.append("<![CDATA[");

                if (strToken.toUpperCase().startsWith("A")) {
                    strResult.append("Physics (A)");
                } else if (strToken.toUpperCase().startsWith("B")) {
                    strResult.append("Electrical/Electronic engineering (B)");
                } else if (strToken.toUpperCase().startsWith("C")) {
                    strResult.append("Computers/Control engineering (C)");
                } else if (strToken.toUpperCase().startsWith("D")) {
                    strResult.append("Information technology (D)");
                } else if (strToken.toUpperCase().startsWith("E")) {
                    strResult.append("Manufacturing and production engineering (E)");
                }

                strResult.append("]]>");

                strResult.append("</");
                strResult.append(EIDoc.DISCIPLINE);
                strResult.append(">");
            }
        }

        return strResult.toString();
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

    private String replaceTYwithBIBcode(String str) {

        if (!str.equals(StringUtil.EMPTY_STRING)) {
            int type = Integer.parseInt(str);

            switch (type) {
            case 10:
                str = "article";
                break;

            case 11:
                str = "article";
                break;

            case 12:
                str = "article";
                break;

            case 21:
                str = "article";
                break;

            case 22:
                str = "article";
                break;

            case 23:
                str = "article";
                break;

            case 30:
                str = "book";
                break;

            case 40:
                str = "inbook";
                break;

            case 50:
                str = "proceedings";
                break;
            case 51:
                str = "proceedings";
                break;

            case 52:
                str = "proceedings";
                break;

            case 53:
                str = "proceedings";
                break;

            case 60:
                str = "inproceedings";
                break;
            case 61:
                str = "inproceedings";
                break;

            case 62:
                str = "inproceedings";
                break;

            case 63:
                str = "inproceedings";
                break;

            case 80:
                str = "article";
                break;

            default:
                str = "article";
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

    private Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }

        return h;
    }

    public String[] setElementData(String elementVal) {
        ArrayList<String> list = new ArrayList<String>();
        String s = null;
        if (elementVal != null) {
            StringTokenizer st = new StringTokenizer(elementVal, AUDELIMITER);
            while (st.hasMoreTokens()) {
                s = st.nextToken().trim();
                if (s.length() > 0) {
                    list.add(s.trim());
                }
            }
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    public String[] setDisciplineElementData(String elementVal) {
        ArrayList<String> list = new ArrayList<String>();
        if (elementVal != null) {
            StringTokenizer st = new StringTokenizer(elementVal, AUDELIMITER);
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
                        list.add("Information technology for Business (D)");
                    } else if (strToken.toUpperCase().startsWith("E")) {
                        list.add("Manufacturing and production engineering (E)");
                    }

                }

            }
        }
        return (String[]) list.toArray(new String[list.size()]);

    }

    public String[] setTreatments(String treatments) {
        String[] trarray = treatments.split(AUDELIMITER);
        ArrayList<String> result = new ArrayList<String>();

        if (trarray.length >= 0 && trarray.length < 10 && trarray.length > 0) {
            for (int i = 0; i < trarray.length; i++) {
                if (!trarray[i].equals("") && !trarray[i].equals("QQ") && !trarray[i].equals(";")) {
                    result.add(trarray[i]);
                }
            }
        }

        return (String[]) result.toArray(new String[1]);
    }

    private KeyValuePair[] setCVS(String mainHeading, String cvs) {
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

    private int getCitationCount(Clob citationClob) throws SQLException  {
        String citationString = null;
        int citationCount = 0;
        if (citationClob != null) {
            citationString = StringUtil.getStringFromClob(citationClob);
            if (citationString != null && citationString.length() > 1) {
                String[] citationArray = citationString.split(AUDELIMITER, -1);
                citationCount = citationArray.length;
            }
        }

        return citationCount;

    }

    private String selectPubYear(String sspdate, String fdate, String cdate, String su) {
        String strYear = null;

        if (sspdate != null && (sspdate = getPubYear(sspdate)) != null) {
            strYear = sspdate;
        } else if (fdate != null && (fdate = getPubYear(fdate)) != null) {
            strYear = fdate;
        } else if (cdate != null && (cdate = getPubYear(cdate)) != null) {
            strYear = cdate;
        } else if (su != null && (su = getPubYear(su)) != null) {
            strYear = su;
        }

        return strYear;
    }

    private String getPubYear(String y) {
        String year = null;
        try {
            String regularExpression = "((19\\d|200)\\d)\\w?";
            Pattern p = Pattern.compile(regularExpression);
            Matcher m = p.matcher(y);
            if (m.find()) {
                year = m.group(1).trim();
            }
        } catch (Exception e) {
            return null;
        }

        return year;
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
