package org.ei.data.ntis.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.data.AuthorStream;
import org.ei.data.DataCleaner;
import org.ei.data.ntis.loadtime.NTISAuthor;
import org.ei.data.ntis.loadtime.NTISData;
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
import org.ei.domain.Key;
import org.ei.domain.Keys;
import org.ei.domain.PageRange;
import org.ei.domain.RIS;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.util.StringUtil;
import org.ei.data.bd.loadtime.BdParser;

/**
 * This class is the implementation of DocumentBuilder Basically this class is responsible for building a List of EIDocs from a List of DocIds.The input is list
 * of docids come from NTISSearchControl
 *
 */
public class NTISDocBuilder implements DocumentBuilder {
    public static String NTIS_TEXT_COPYRIGHT = "Compiled and Distributed by the NTIS, U.S. Department of Commerce.  It contains copyrighted material.  All rights reserved. 2013";
    public static String NTIS_HTML_COPYRIGHT = NTIS_TEXT_COPYRIGHT;
    public static final Key NTIS_PRICES = new Key(Keys.CONTROLLED_TERMS, "NTIS price code");
    public static final Key NTIS_COUNTRY = new Key(Keys.COUNTRY, "Country of origin");
    public static final Key NTIS_CORPORATE_SOURCE_CODES = new Key(Keys.CORPORATE_SOURCE_CODES, "Author affiliation codes");
    public static String PROVIDER = "NTIS";

    private static final Key NTIS_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "NTIS controlled terms");
    private static final Key NTIS_CLASS_CODES = new Key(Keys.CLASS_CODES, "NTIS classification codes");
    private static final Key NTIS_MAIN_HEADING = new Key(Keys.MAIN_HEADING, "NTIS main heading");
    public static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.TITLE, Keys.AUTHORS, Keys.PERFORMER, Keys.NO_SO, Keys.RSRCH_SPONSOR, Keys.PAGE_RANGE,
        Keys.PUBLICATION_YEAR, Keys.LANGUAGE, Keys.RN_LABEL, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    public static final Key[] ABSTRACT_KEYS = { Keys.DOCID, Keys.TITLE, Keys.AUTHORS, Keys.PERFORMER, Keys.NO_SO, Keys.AUTHOR_AFFS, Keys.RSRCH_SPONSOR,
        Keys.PUBLICATION_YEAR, Keys.PATFILDATE, Keys.PAGE_RANGE, Keys.PUBLISHER, Keys.PROVIDER, Keys.LANGUAGE, Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS,
        Keys.ABSTRACT, Keys.RN_LABEL, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    public static final Key[] DETAILED_KEYS = { Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.TITLE_ANNOTATION, Keys.AUTHORS, Keys.PERFORMER,
        Keys.CORPORATE_SOURCE_CODES, Keys.RSRCH_SPONSOR, Keys.RN_LABEL, Keys.PATFILDATE, Keys.PATENT_ISSUE_DATE, Keys.PUBLICATION_DATE, Keys.PUBLICATION_YEAR,
        Keys.PAGE_RANGE, Keys.LANGUAGE, Keys.COUNTRY, Keys.DOC_TYPE, Keys.NOTES, Keys.ABSTRACT, Keys.AVAILABILITY, Keys.PRICES, Keys.CONTRACT_NUMBERS,
        Keys.PROJECT_NUMBER, Keys.TASK_NUMBER, Keys.MONITORING_AGENCIES, Keys.JOURNAL_ANNOUNCEMENT, Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS,
        Keys.CLASS_CODES, Keys.DOCID, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    private static final Key[] RIS_KEYS = { Keys.RIS_AD, Keys.RIS_AN, Keys.RIS_AUS, Keys.RIS_AV, Keys.RIS_CVS, Keys.RIS_CY, Keys.RIS_FLS, Keys.RIS_IS,
        Keys.RIS_LA, Keys.RIS_N1, Keys.RIS_N2, Keys.RIS_PB, Keys.RIS_PY, Keys.RIS_SP, Keys.RIS_T1, Keys.RIS_T2, Keys.RIS_TY, Keys.RIS_U1, Keys.RIS_U3,
        Keys.RIS_U4, Keys.RIS_U5, Keys.BIB_TY };
    private static final Key[] XML_KEYS = { Keys.DOCID, Keys.PROVIDER, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.TITLE, Keys.ACCESSION_NUMBER, Keys.NO_SO,
        Keys.AUTHORS, Keys.PERFORMER, Keys.RSRCH_SPONSOR, Keys.PUBLICATION_YEAR, Keys.PAGE_COUNT, Keys.RN_LABEL, Keys.ACCESSION_NUMBER, Keys.LANGUAGE,
        Keys.CONTROLLED_TERMS };

    /*
     *
     * static { m_labels.put("AN","Accession number"); m_labels.put("TI","Title"); m_labels.put("TA","Title annotation"); m_labels.put("AUS","Authors"); ?????
     * m_labels.put("PF","Author affiliation"); m_labels.put("CAC","Author affiliation codes"); m_labels.put("RSP","Sponsor");
     * m_labels.put("RN","Report number"); m_labels.put("PFD","Filing date"); m_labels.put("PID","Patent issue date"); m_labels.put("PD_YR","Publication date");
     * m_labels.put("PP","Pages"); m_labels.put("LA","Language"); m_labels.put("CO","Country of origin"); m_labels.put("DT","Document type");
     * m_labels.put("SU","Notes"); m_labels.put("AB","Abstract"); m_labels.put("AV","Availability"); m_labels.put("PS",PROVIDER+" price code");
     * m_labels.put("CTS","Contract number"); m_labels.put("PNUM","Project number"); m_labels.put("TNUM","Task number");
     * m_labels.put("AGS","Monitoring agency"); m_labels.put("VI","Journal announcement"); m_labels.put("CVS",PROVIDER+" controlled terms");
     * m_labels.put("FLS","Uncontrolled terms"); m_labels.put("CLS",PROVIDER+" classification codes"); m_labels.put("DB","Database");
     *
     * } static { m_order.add("AN"); m_order.add("TI"); m_order.add("TA"); m_order.add("AUS"); m_order.add("PF"); m_order.add("CAC"); m_order.add("RSP");
     * m_order.add("RN"); m_order.add("PFD"); m_order.add("PID"); m_order.add("PD_YR"); m_order.add("PP"); m_order.add("LA"); m_order.add("CO");
     * m_order.add("DT"); m_order.add("SU"); m_order.add("AB"); m_order.add("AV"); m_order.add("PS"); m_order.add("CTS"); m_order.add("PNUM");
     * m_order.add("TNUM"); m_order.add("AGS"); m_order.add("VI"); m_order.add("CVS"); m_order.add("FLS"); m_order.add("CLS"); m_order.add("DB");
     * m_order.add("CPR"); }
     */

    // document builder interface methods which are called by EIDoc classes
    // for building detailed XML views of documents

    private Database database;
    private Perl5Util perl = new Perl5Util();

    // fields to be displayed in citation format
    private static String queryCitation = "select M_ID,TI,PA1,PA2,PA3,PA4,PA5,HN,SO,RD,XP,RN,AN,load_number,IC  from NTIS_MASTER where M_ID IN ";
    private static String queryXMLCitation = "select M_ID,TI,PA1,PA2,PA3,PA4,PA5,HN,SO,RD,XP,RN,AN,load_number,IC,DES  from NTIS_MASTER where M_ID IN ";
    // fields to be displayed in abstract format
    private static String queryAbstracts = "select M_ID,TI,PA1,PA2,PA3,PA4,PA5,HN,SO,RD,XP,RN,MAA1,MAN1,MAA2,MAN2,AN,IC,AB,DES  from NTIS_MASTER where  M_ID IN ";

    // fields to be displayed in detailed format
    private static String queryDetailed = "select M_ID,AN,TI,PA1,PA2,PA3,PA4,PA5,HN,AV,PR,SO,CAC,RD,XP,VI,SU,TA,AB,IC,TN,DES,IDE,CAT,RN,CT,PN,TNUM,MAA1,MAA2  from NTIS_MASTER where M_ID IN ";

    private static String queryPreview = "select M_ID, AB from NTIS_MASTER where M_ID IN ";

    public DocumentBuilder newInstance(Database database) {
        return new NTISDocBuilder(database);
    }

    public NTISDocBuilder() {
    }

    public NTISDocBuilder(Database database) {
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
                String abs = rset.getString("AB");

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
                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);
                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, NTISDocBuilder.NTIS_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, NTISDocBuilder.NTIS_TEXT_COPYRIGHT));
                // get title
                if (rset.getString("TI") != null) {
                    String title = NTISData.formatTitle(rset.getString("TI"));
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, title));
                }

                // accession number for openXML
                if (rset.getString("AN") != null) {
                    String accessionNumber = NTISData.formatTitle(rset.getString("AN")).trim();
                    if ((accessionNumber != null) && !(accessionNumber.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, accessionNumber));
                    }

                }

                // we do not want the 'Source:' label appearing - this is needed to suppress it
                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                // AUS
                // get authors
                if (rset.getString("PA1") != null) {
                    String aus = NTISAuthor.formatAuthors(rset.getString("PA1"), rset.getString("PA2"), rset.getString("PA3"), rset.getString("PA4"),
                        rset.getString("PA5"), rset.getString("HN"));
                    if (aus != null) {
                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(aus, Keys.AUTHORS));
                        ht.put(Keys.AUTHORS, authors);
                    }
                }
                // get author affiliation: performer and sponsor
                Map<Key, String> pAndS = NTISData.authorAffiliationAndSponsor(rset.getString("SO"));

                if (pAndS.containsKey(Keys.PERFORMER)) {
                    ht.put(Keys.PERFORMER, new XMLWrapper(Keys.PERFORMER, (String) pAndS.get(Keys.PERFORMER)));
                }

                if (pAndS.containsKey(Keys.RSRCH_SPONSOR)) {
                    ht.put(Keys.RSRCH_SPONSOR, new XMLWrapper(Keys.RSRCH_SPONSOR, (String) pAndS.get(Keys.RSRCH_SPONSOR)));
                }

                // get publication date, if null, get
                String pubDate = null;
                if (rset.getString("RD") != null) {
                    pubDate = NTISData.formatDate(rset.getString("RD")).trim();
                    if ((pubDate != null) && !(pubDate.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PUBLICATION_YEAR, new Year(pubDate, perl));
                    }
                }
                // get pages

                if (rset.getString("XP") != null) {
                    String page = NTISData.formatPage(rset.getString("XP")).replaceAll("p", " ").trim();
                    if ((page != null) && !(page.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, page));
                    }
                }

                // get report number
                if (rset.getString("RN") != null) {
                    String reportNum = NTISData.formatRN(rset.getString("RN")).trim();
                    if ((reportNum != null) && !(reportNum.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.RN_LABEL, new XMLWrapper(Keys.RN_LABEL, reportNum));
                    }
                }

                // get accession number, strip out "/", "/XAB", "-", " "
                if (rset.getString("AN") != null) {
                    String accNum = NTISData.formatAN(rset.getString("AN")).trim();
                    if ((accNum != null) && !(accNum.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, accNum));
                    }

                }

                // get language
                if (rset.getString("IC") != null) {
                    String lang = NTISData.formatLA(rset.getString("IC"));
                    if (!("ENGLISH").equalsIgnoreCase(lang)) {
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, lang));
                    }
                }

                if (rset.getString("DES") != null) {
                    String descriptors = NTISData.formatDES(rset.getString("DES"));
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(NTIS_CONTROLLED_TERMS, setElementData(descriptors)));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.XMLCITATION_FORMAT);
                eiDoc.exportLabels(false);
                // eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(XML_KEYS);
                list.add(eiDoc);
                count++;
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
                    rset = null;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
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
        }
        return list;
    }// loadXMLCitation

    /**
     * This method basically takes list Of DocIDs as Parameter This list of Docids use buildINString() method to build the required IN clause String.This is
     * appended to sql String The resultSet so obtained by executing the sql,is iterated, to build EIDoc objects,which are then added to EIdocumentList
     *
     * @param listOfDocIDs
     * @return EIDocumentList
     * @exception Exception
     */
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

                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);
                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, NTIS_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, NTIS_TEXT_COPYRIGHT));
                // DB
                // ht.put("DM", Integer.toString(did.getDatabase().getMask()));
                // get title
                if (rset.getString("TI") != null) {
                    String title = NTISData.formatTitle(rset.getString("TI"));
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, title));
                }

                // we do not want the 'Source:' label appearing - this is needed to suppress it
                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                if (rset.getString("PA1") != null) {
                    String aus = NTISAuthor.formatAuthors(rset.getString("PA1"), rset.getString("PA2"), rset.getString("PA3"), rset.getString("PA4"),
                        rset.getString("PA5"), rset.getString("HN"));
                    if (aus != null) {
                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(aus, Keys.AUTHORS));
                        ht.put(Keys.AUTHORS, authors);
                    }
                }
                // get author affiliation: performer and sponsor
                Map<Key, String> pAndS = NTISData.authorAffiliationAndSponsor(rset.getString("SO"));

                if (pAndS.containsKey(Keys.PERFORMER)) {
                    ht.put(Keys.PERFORMER, new XMLWrapper(Keys.PERFORMER, (String) pAndS.get(Keys.PERFORMER)));
                }

                if (pAndS.containsKey(Keys.RSRCH_SPONSOR)) {
                    ht.put(Keys.RSRCH_SPONSOR, new XMLWrapper(Keys.RSRCH_SPONSOR, (String) pAndS.get(Keys.RSRCH_SPONSOR)));
                }
                // get publication date, if null, get
                String pubDate = null;
                if (rset.getString("RD") != null) {
                    pubDate = NTISData.formatDate(rset.getString("RD")).trim();
                    if ((pubDate != null) && !(pubDate.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PUBLICATION_YEAR, new Year(pubDate, perl));
                    }
                }
                // get pages
                if (rset.getString("XP") != null) {
                    String page = NTISData.formatPage(rset.getString("XP")).trim();
                    if ((page != null) && !(page.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PAGE_RANGE, new PageRange(page, perl));
                    }
                }

                // get report number
                if (rset.getString("RN") != null) {
                    String reportNum = NTISData.formatRN(rset.getString("RN")).trim();
                    if ((reportNum != null) && !(reportNum.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.RN_LABEL, new XMLWrapper(Keys.RN_LABEL, reportNum));
                    }
                }

                // get accession number, strip out "/", "/XAB", "-", " "
                if (rset.getString("AN") != null) {
                    String accNum = NTISData.formatAN(rset.getString("AN")).trim();
                    if ((accNum != null) && !(accNum.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, accNum));
                    }
                }
                // get language
                if (rset.getString("IC") != null) {
                    String lang = NTISData.formatLA(rset.getString("IC"));
                    if (!("ENGLISH").equalsIgnoreCase(lang)) {
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, lang));
                    }
                }
                // AN
                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.exportLabels(false);
                // eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(CITATION_KEYS);
                list.add(eiDoc);
                count++;
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
                    rset = null;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
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

                // ABSTRACT IDENTITCAL TO CITATION EXCEPT FOR
                // TWO FIELDS - ABSTRACT AND CONTROLLED TERMS

                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);
                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, NTIS_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, NTIS_TEXT_COPYRIGHT));

                // get title
                if (rset.getString("TI") != null) {
                    String title = NTISData.formatTitle(rset.getString("TI"));
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, title));
                }

                // we do not want the 'Source:' label appearing - this is needed to suppress it
                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                // get authors
                if (rset.getString("PA1") != null) {
                    String aus = NTISAuthor.formatAuthors(rset.getString("PA1"), rset.getString("PA2"), rset.getString("PA3"), rset.getString("PA4"),
                        rset.getString("PA5"), rset.getString("HN"));

                    if (aus != null) {
                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(aus, Keys.AUTHORS));
                        ht.put(Keys.AUTHORS, authors);
                    }

                }
                // get author affiliation: performer and sponsor
                Map<Key, String> pAndS = NTISData.authorAffiliationAndSponsor(rset.getString("SO"));

                if (pAndS.containsKey(Keys.PERFORMER)) {
                    ht.put(Keys.PERFORMER, new XMLWrapper(Keys.PERFORMER, (String) pAndS.get(Keys.PERFORMER)));
                }

                if (pAndS.containsKey(Keys.RSRCH_SPONSOR)) {
                    ht.put(Keys.RSRCH_SPONSOR, new XMLWrapper(Keys.RSRCH_SPONSOR, (String) pAndS.get(Keys.RSRCH_SPONSOR)));
                }

                // get publication date, if null
                String pubDate = null;
                if (rset.getString("RD") != null) {
                    pubDate = NTISData.formatDate(rset.getString("RD")).trim();
                    if ((pubDate != null) && !(pubDate.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PUBLICATION_YEAR, new Year(pubDate, perl));
                    }
                }
                // get pages
                if (rset.getString("XP") != null) {
                    String page = NTISData.formatPage(rset.getString("XP")).trim();
                    if ((page != null) && !(page.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PAGE_RANGE, new PageRange(page, perl));
                    }
                }

                // get report number
                if (rset.getString("RN") != null) {
                    String reportNum = NTISData.formatRN(rset.getString("RN")).trim();
                    if ((reportNum != null) && !(reportNum.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.RN_LABEL, new XMLWrapper(Keys.RN_LABEL, reportNum));
                    }
                }
                // get accession number, strip out "/", "/XAB", "-", " "
                if (rset.getString("AN") != null) {
                    String accNum = NTISData.formatAN(rset.getString("AN")).trim();
                    if ((accNum != null) && !(accNum.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, accNum));
                    }
                }

                // get language
                if (rset.getString("IC") != null) {
                    String lang = NTISData.formatLA(rset.getString("IC"));
                    if (!("ENGLISH").equalsIgnoreCase(lang)) {
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, lang));
                    }
                }

                // ABSTRACT IDENTITCAL TO CITATION EXCEPT FOR
                // TWO FIELDS - ABSTRACT AND CONTROLLED TERMS
                // ADDITIONAL FIELDS FOR ABSTRACT NTIS
                // get abstract

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }
                // get controlled terms
                if (rset.getString("DES") != null) {
                    String descriptors = NTISData.formatDES(rset.getString("DES"));
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(NTIS_CONTROLLED_TERMS, setElementData(descriptors)));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Abstract.ABSTRACT_FORMAT);
                // eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(true);
                eiDoc.setOutputKeys(ABSTRACT_KEYS);
                list.add(eiDoc);
                count++;
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
        }
        return list;
    }

    /**
     * This method basically takes list Of DocIDs as Parameter This list of Docids use buildINString() method to build the required IN clause String.This is
     * appended to sql String The resultSet so obtained by executing the sql,is iterated, to build Detailed EIDoc objects,which are then added to EIdocumentList
     *
     * @param listOfDocIDs
     * @return EIDocumentList
     * @exception Exception
     */
    private List<EIDoc> loadDetailed(List<DocID> listOfDocIDs) throws DocumentBuilderException {
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
            rset = stmt.executeQuery(queryDetailed + INString);

            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();

                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);
                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, NTIS_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, NTIS_TEXT_COPYRIGHT));

                if (rset.getString("AN") != null) {
                    String accNum = NTISData.formatAN(rset.getString("AN"));
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, accNum));
                }

                // get report number
                if (rset.getString("RN") != null) {
                    String reportNum = NTISData.formatRN(rset.getString("RN"));
                    if ((reportNum != null) && !(reportNum.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.RN_LABEL, new XMLWrapper(Keys.RN_LABEL, reportNum));
                    }
                }

                // get title
                if (rset.getString("TI") != null) {
                    String title = NTISData.formatTitle(rset.getString("TI"));
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, title));
                }

                if (rset.getString("PA1") != null) {
                    String aus = NTISAuthor.formatAuthors(rset.getString("PA1"), rset.getString("PA2"), rset.getString("PA3"), rset.getString("PA4"),
                        rset.getString("PA5"), rset.getString("HN"));

                    if (aus != null) {
                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(aus, Keys.AUTHORS));
                        ht.put(Keys.AUTHORS, authors);
                    }

                }
                // get author affiliation: performer and sponsor
                Map<Key, String> pAndS = NTISData.authorAffiliationAndSponsor(rset.getString("SO"));

                if (pAndS.containsKey(Keys.PERFORMER)) {
                    ht.put(Keys.PERFORMER, new XMLWrapper(Keys.PERFORMER, (String) pAndS.get(Keys.PERFORMER)));
                }

                if (pAndS.containsKey(Keys.RSRCH_SPONSOR)) {
                    ht.put(Keys.RSRCH_SPONSOR, new XMLWrapper(Keys.RSRCH_SPONSOR, (String) pAndS.get(Keys.RSRCH_SPONSOR)));
                }

                // get availability
                if (rset.getString("AV") != null) {
                    ht.put(Keys.AVAILABILITY, new XMLWrapper(Keys.AVAILABILITY, rset.getString("AV")));
                }

                // get NTIS prices
                if (rset.getString("PR") != null) {
                    String strPrices = StringUtil.replaceNullWithEmptyString(rset.getString("PR"));
                    if (!perl.match("/not available NTIS$/i", strPrices)) {
                        if (perl.match("/^NTIS Prices:/i", strPrices)) {
                            strPrices = perl.substitute("s/^NTIS Prices://i", strPrices);
                        }
                        ht.put(Keys.PRICES, new XMLWrapper(NTIS_PRICES, strPrices));
                    }
                }

                // get corporate source codes, ignore dummy codes
                if (rset.getString("CAC") != null) {

                    String strCAC = rset.getString("CAC");
                    if (perl.match("#([8|9]{2,})\\b#", strCAC)) {
                        strCAC = perl.substitute("s#([8|9]{2,})\\b##g", strCAC).trim();
                    }
                    if ((strCAC.trim() != null) && !(strCAC.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.CORPORATE_SOURCE_CODES, new XMLWrapper(NTIS_CORPORATE_SOURCE_CODES, strCAC));
                    }
                }

                // get patent filing date, patent issue date and publication date
                if (rset.getString("RD") != null) {
                    String date = rset.getString("RD");
                    String strPatApp = StringUtil.EMPTY_STRING;
                    String strPatIss = StringUtil.EMPTY_STRING;
                    if (perl.match("#filed([^,]*),#i", date)) {
                        strPatApp = perl.group(1);
                        ht.put(Keys.PATFILDATE, new Year(NTISData.formatDate(strPatApp), perl));
                    }

                    date = rset.getString("RD");
                    if (perl.match("#patented([^,]*),#i", date)) {
                        strPatIss = perl.group(1);
                        ht.put(Keys.PATENT_ISSUE_DATE, new Year(NTISData.formatDate(strPatIss), perl));
                    } else if (perl.match("#reissue([^,]*),#i", date)) {
                        strPatIss = perl.group(1);
                        ht.put(Keys.PATENT_ISSUE_DATE, new Year(NTISData.formatDate(strPatIss), perl));
                    }

                    if (StringUtil.EMPTY_STRING.equals(strPatApp) && StringUtil.EMPTY_STRING.equals(strPatIss)) {
                        String pdate = NTISData.formatDate(rset.getString("RD"));
                        ht.put(Keys.PUBLICATION_DATE, new Year(pdate, perl));
                    }

                    String pubDate = NTISData.formatDate(rset.getString("RD"));
                    ht.put(Keys.PUBLICATION_YEAR, new Year(pubDate, perl));
                }

                // get pages
                if (rset.getString("XP") != null) {
                    String page = NTISData.formatPage(rset.getString("XP")).trim();
                    if ((page != null) && !(page.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.PAGE_RANGE, new PageRange(page, perl));
                    }
                }

                // get journal announcement
                if (rset.getString("VI") != null) {
                    String vi = NTISData.formatVI(rset.getString("VI")).trim();
                    if ((vi != null) && !(vi.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.JOURNAL_ANNOUNCEMENT, new XMLWrapper(Keys.JOURNAL_ANNOUNCEMENT, vi));
                    }
                }

                // get notes
                if (rset.getString("SU") != null) {
                    ht.put(Keys.NOTES, new XMLWrapper(Keys.NOTES, rset.getString("SU")));
                }

                // get title annotation
                if (rset.getString("TA") != null) {
                    ht.put(Keys.TITLE_ANNOTATION, new XMLWrapper(Keys.TITLE_ANNOTATION, rset.getString("TA")));
                }

                // get abstract
                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }
                // get language

                if (rset.getString("IC") != null) {
                    String lang = NTISData.formatLA(rset.getString("IC")).trim();
                    if ((lang != null) && !(lang.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, lang));
                    }
                }
                // get country
                if (rset.getString("SO") != null) {
                    String country = NTISData.formatCountry(rset.getString("SO"), rset.getString("IC")).trim();
                    if ((country != null) && !(country.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.COUNTRY, new XMLWrapper(NTIS_COUNTRY, country));
                    }
                }

                // get document type
                if (rset.getString("TN") != null) {
                    String strTN = perl.substitute("s#\\.$##", rset.getString("TN"));
                    ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, strTN));
                }

                // get controlled terms
                if (rset.getString("DES") != null) {
                    String des = NTISData.formatDES(rset.getString("DES")).trim();
                    if ((des != null) && !(des.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(NTIS_CONTROLLED_TERMS, setElementData(des)));
                    }
                }
                // get uncontrolled terms
                if (rset.getString("IDE") != null) {
                    String ide = NTISData.formatIDE(rset.getString("IDE")).trim();
                    if ((ide != null) && !(ide.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(ide)));
                    }
                }
                // get classification codes
                if (rset.getString("CAT") != null) {
                    String cat = NTISData.formatCAT(rset.getString("CAT")).trim();
                    if ((cat != null) && !(cat.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.CLASS_CODES, new Classifications(NTIS_CLASS_CODES, setElementData(cat), this.database));
                    }

                }

                // get report number
                if (rset.getString("RN") != null) {
                    String rn = NTISData.formatRN(rset.getString("RN")).trim();
                    if ((rn != null) && !(rn.equals(StringUtil.EMPTY_STRING))) {
                        ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, rn));
                    }
                }

                // get contract number
                if (rset.getString("CT") != null) {
                    String ct = NTISData.stripOutBracket(rset.getString("CT"));

                    ct = formatOrderNumbers(ct);
                    ct = perl.substitute("s#contract(s*)##i", ct).trim();

                    if (!StringUtil.EMPTY_STRING.equals(ct.trim())) {
                        ht.put(Keys.CONTRACT_NUMBERS, new XMLWrapper(Keys.CONTRACT_NUMBERS, ct));
                    }
                }

                // get project number
                if (rset.getString("PN") != null) {
                    String pnum = NTISData.stripOutBracket(rset.getString("PN"));
                    pnum = perl.substitute("s#Proj\\.##i", pnum);
                    if (!StringUtil.EMPTY_STRING.equals(pnum.trim())) {
                        ht.put(Keys.PROJECT_NUMBER, new XMLWrapper(Keys.PROJECT_NUMBER, pnum));
                    }
                }

                // get task number
                if (rset.getString("TNUM") != null) {
                    String tnum = NTISData.stripOutBracket(rset.getString("TNUM"));
                    if (!StringUtil.EMPTY_STRING.equals(tnum.trim())) {
                        ht.put(Keys.TASK_NUMBER, new XMLWrapper(Keys.TASK_NUMBER, tnum));
                    }
                }

                // get agency
                String strAGs = null;
                if (rset.getString("MAA1") != null) {
                    strAGs = rset.getString("MAA1");
                }
                if (rset.getString("MAA2") != null) {
                    if (strAGs != null) {
                        strAGs = strAGs.concat("; ").concat(rset.getString("MAA2"));
                    } else {
                        strAGs = rset.getString("MAA2");
                    }
                }
                if (strAGs != null) {
                    ht.put(Keys.MONITORING_AGENCIES, new XMLWrapper(Keys.MONITORING_AGENCIES, strAGs));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Detail.FULLDOC_FORMAT);
                eiDoc.exportLabels(true);
                // eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(DETAILED_KEYS);
                list.add(eiDoc);
                count++;
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
        }

        return list;
    }

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
            String bibDocType = "article";

            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();

                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));

                // document type is fixed as RPRT (Report)
                ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, replaceTYwithRIScode(rset.getString("TN"))));
                // always article
                ht.put(Keys.BIB_TY, new XMLWrapper(Keys.BIB_TY, bibDocType));

                if (rset.getString("IC") != null) {
                    String la = NTISData.formatLA(rset.getString("IC"));
                    ht.put(Keys.RIS_LA, new XMLWrapper(Keys.RIS_LA, la));
                }

                if (rset.getString("AN") != null) {
                    String accNum = NTISData.formatAN(rset.getString("AN"));
                    if (!StringUtil.EMPTY_STRING.equals(accNum)) {
                        ht.put(Keys.RIS_U1, new XMLWrapper(Keys.RIS_U1, accNum));
                    }
                }

                // get uncontrolled terms
                if (rset.getString("IDE") != null) {
                    String ide = NTISData.formatIDE(rset.getString("IDE"));
                    ht.put(Keys.RIS_FLS, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(ide)));
                }

                // get report number
                if (rset.getString("RN") != null) {
                    String rn = NTISData.formatRN(rset.getString("RN"));
                    if (!StringUtil.EMPTY_STRING.equals(rn)) {
                        ht.put(Keys.RIS_U3, new XMLWrapper(Keys.RIS_U3, rn));
                    }
                }

                List<String> lst_CT_PN_TNUM = new ArrayList<String>();
                // get contract number
                if (rset.getString("CT") != null) {
                    String ct = NTISData.stripOutBracket(rset.getString("CT"));
                    ct = formatOrderNumbers(ct);
                    if (!StringUtil.EMPTY_STRING.equals(ct)) {
                        lst_CT_PN_TNUM.add(ct);
                    }
                }
                // get project number
                if (rset.getString("PN") != null) {
                    String pnum = NTISData.stripOutBracket(rset.getString("PN"));
                    if (!StringUtil.EMPTY_STRING.equals(pnum)) {
                        lst_CT_PN_TNUM.add(pnum);
                    }
                }
                // get task number
                if (rset.getString("TNUM") != null) {
                    String tnum = NTISData.stripOutBracket(rset.getString("TNUM"));
                    if (!StringUtil.EMPTY_STRING.equals(tnum)) {
                        lst_CT_PN_TNUM.add(tnum);
                    }
                }
                if (lst_CT_PN_TNUM.size() > 0) {
                    ht.put(Keys.RIS_U4, new XMLWrapper(Keys.RIS_U4, StringUtil.join(lst_CT_PN_TNUM, ", ")));
                }
                lst_CT_PN_TNUM = null;

                // get notes
                if (rset.getString("SU") != null) {
                    ht.put(Keys.RIS_U5, new XMLWrapper(Keys.RIS_U5, rset.getString("SU")));
                }

                ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, NTIS_TEXT_COPYRIGHT));

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, abs));
                }

                if (rset.getString("TI") != null) {
                    ht.put(Keys.RIS_T1, new XMLWrapper(Keys.RIS_T1, rset.getString("TI")));
                }

                if (rset.getString("TA") != null) {
                    ht.put(Keys.RIS_T2, new XMLWrapper(Keys.RIS_T2, rset.getString("TA")));
                }

                if (rset.getString("XP") != null) {
                    String page = NTISData.formatPage(rset.getString("XP"));
                    ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, page));
                }

                // get authors
                if (rset.getString("PA1") != null) {
                    String aus = NTISAuthor.formatAuthors(rset.getString("PA1"), rset.getString("PA2"), rset.getString("PA3"), rset.getString("PA4"),
                        rset.getString("PA5"), rset.getString("HN"));

                    if (aus != null) {
                        Contributors author = new Contributors(Keys.RIS_AUS, getContributors(aus, Keys.RIS_AUS));

                        ht.put(Keys.RIS_AUS, author);
                    }

                }

                // get author affiliation: performer and sponsor
                Map<Key, String> pAndS = NTISData.authorAffiliationAndSponsor(rset.getString("SO"));
                if (pAndS.containsKey(Keys.PERFORMER)) {
                    ht.put(Keys.RIS_AD, new XMLWrapper(Keys.RIS_AD, (String) pAndS.get(Keys.PERFORMER)));
                }
                if (pAndS.containsKey(Keys.SPONSOR)) {
                    ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, (String) pAndS.get(Keys.SPONSOR)));
                }
                // get country
                if (rset.getString("SO") != null) {
                    String country = NTISData.formatCountry(rset.getString("SO"), rset.getString("IC"));
                    ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, country));
                }

                // get patent filing date, patent issue date and publication date
                if (rset.getString("RD") != null) {
                    String date = rset.getString("RD");
                    String strPatApp = StringUtil.EMPTY_STRING;
                    String strPatIss = StringUtil.EMPTY_STRING;
                    if (perl.match("#filed([^,]*),#i", date)) {
                        strPatApp = perl.group(1);
                        date = StringUtil.replaceNullWithEmptyString(NTISData.formatDate(strPatApp));
                        ht.put(Keys.RIS_PY, new XMLWrapper(Keys.RIS_PY, formatDateRIS(date)));
                    }

                    date = rset.getString("RD");
                    if (perl.match("#patented([^,]*),#i", date)) {
                        strPatIss = perl.group(1);
                        date = StringUtil.replaceNullWithEmptyString(NTISData.formatDate(strPatIss));
                        ht.put(Keys.RIS_IS, new XMLWrapper(Keys.RIS_IS, formatDateRIS(date)));
                    } else if (perl.match("#reissue([^,]*),#i", date)) {
                        strPatIss = perl.group(1);
                        date = StringUtil.replaceNullWithEmptyString(NTISData.formatDate(strPatIss));
                        ht.put(Keys.RIS_IS, new XMLWrapper(Keys.RIS_IS, formatDateRIS(date)));
                    }

                    if (StringUtil.EMPTY_STRING.equals(strPatApp) && StringUtil.EMPTY_STRING.equals(strPatIss)) {
                        date = rset.getString("RD");
                        ht.put(Keys.RIS_PY, new XMLWrapper(Keys.RIS_PY, formatDateRIS(date)));
                    }
                }
                String str_AV_PS = StringUtil.EMPTY_STRING;

                // get availability
                if (rset.getString("AV") != null) {
                    str_AV_PS = StringUtil.replaceNullWithEmptyString(rset.getString("AV"));
                }
                // get NTIS prices
                if (rset.getString("PR") != null) {
                    String strPrices = StringUtil.replaceNullWithEmptyString(rset.getString("PR"));
                    if (!perl.match("/not available NTIS$/i", strPrices)) {
                        if (perl.match("/^NTIS Prices:/i", strPrices)) {
                            strPrices = perl.substitute("s/^NTIS Prices://i", strPrices);
                        }
                        str_AV_PS = str_AV_PS.concat(strPrices);
                    }
                }
                if (!str_AV_PS.equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.RIS_AV, new XMLWrapper(Keys.RIS_AV, str_AV_PS));
                }

                // get controlled terms
                if (rset.getString("DES") != null) {

                    String descriptors = NTISData.formatDES(rset.getString("DES"));

                    ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(descriptors)));
                }

                EIDoc eiDoc = new EIDoc(did, ht, RIS.RIS_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(RIS_KEYS);
                list.add(eiDoc);
                count++;
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
        }

        return list;
    } // loadRIS

    public String formatDateRIS(String dateRIS) {
        String date = dateRIS;
        String strYear = StringUtil.EMPTY_STRING;
        SimpleDateFormat formatter = new SimpleDateFormat("MMM");
        SimpleDateFormat formatter1 = new SimpleDateFormat("yy");
        java.util.Date currentTime = null;
        java.util.Date currentTime1 = null;
        if (perl.match("/(\\w+)\\W+(\\d+)/", date)) {
            // use the Java SimpleDateFormat to convert January to 01, etc. (MMM to MM)
            // @see http://java.sun.com/j2se/1.3/docs/api/java/text/SimpleDateFormat.html
            String strDate = StringUtil.EMPTY_STRING;
            try {
                currentTime = formatter.parse(perl.group(1));
                formatter = new SimpleDateFormat("MM");
                strDate = formatter.format(currentTime);
                currentTime1 = formatter1.parse(perl.group(2));
                formatter1 = new SimpleDateFormat("yyyy");
                strYear = formatter1.format(currentTime1);
            } catch (ParseException pe) {
                // just in case we failed miserably, use the raw data from the DB
                strDate = perl.group(1);
                strYear = perl.group(2);
            }
            date = (strYear).concat("/").concat(strDate).concat("/");
        } else if (perl.match("/(\\d+)/", date)) {
            try {
                currentTime1 = formatter1.parse(perl.group(1));
                formatter1 = new SimpleDateFormat("yyyy");
                strYear = formatter1.format(currentTime1);
            } catch (ParseException pe) {
                strYear = perl.group(1);
            }
            date = (strYear).concat("/").concat("/");
        }
        return date;
    }

    /*
     * PRIVATE UTILITY METHODS /
     *
     * /* This method builds the IN String from list of docId objects. The select query will get the result set in a reverse way So in order to get in correct
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
                sQuery.append("'").append(docID).append("'");
            } else {
                sQuery.append("'").append(docID).append("'").append(",");
            }
        }
        sQuery.append(")");
        return sQuery.toString();
    }

    private Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }
        return h;
    }

    // jam XML document mapping, conversion to TY values
    // for RIS format - only called from loadDetailed
    private String replaceTYwithRIScode(String str) {
        if (str == null || str.equals("QQ")) {
            str = StringUtil.EMPTY_STRING;
        }

        str = str.toLowerCase();
        if (str.indexOf("report") > -1) {
            str = "RPRT";
        } else if (str.indexOf("abstract") > -1) {
            str = "ABST";
        } else if (str.indexOf("conference") > -1) {
            str = "CONF";
        } else if (str.indexOf("thesis") > -1) {
            str = "THES";
        } else if (str.indexOf("dissertation") > -1) {
            str = "THES";
        } else if (str.indexOf("book chapter") > -1) {
            str = "CHAP";
        } else if (str.indexOf("book") > -1) {
            str = "BOOK";
        } else if (str.indexOf("journal") > -1) {
            str = "JOUR";
        } else if (str.indexOf("patent") > -1) {
            str = "PAT";
        } else if (str.indexOf("pamphlet") > -1) {
            str = "PAMP";
        } else if (str.indexOf("hearing") > -1) {
            str = "HEAR";
        } else if (str.indexOf("data file") > -1) {
            str = "DATA";
        } else if (str.indexOf("computer program") > -1) {
            str = "COMP";
        } else {
            str = "RPRT";
        }

        return str;
    }

    private String hasAbstract(ResultSet rs) throws SQLException  {
        String abs = rs.getString("AB");
        if (abs == null || abs.length() < 100) {
            return null;
        } else {
            return abs;
        }
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

    private String formatOrderNumbers(String num1)
    {
		if (num1 == null || num1.length() == 0)
		{
			return num1;
		}
		else
		{
			num1 = num1.trim();
		}

		//Added for xml format data
		if(num1.indexOf(BdParser.AUDELIMITER)>-1)
		{
			String[] num1Array = num1.split(BdParser.AUDELIMITER);
			String num11="";
			String num12="";

			//added for XML format
			for(int i=0;i<num1Array.length;i++)
			{
				String singleNum1 = num1Array[i];
				if(singleNum1.indexOf("Contract")>-1)
				{
					num11= singleNum1;
				}
				else
				{
					num12 = num12+";"+singleNum1;
				}
			}
			num1=num11+";"+num12;
		}
		num1 = num1.replace("{"," ");

		return num1;
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

// End Of NTISDocBuilder
