package org.ei.data.cbnb.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.common.AuthorStream;
import org.ei.domain.Abstract;
import org.ei.domain.Citation;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Detail;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.DocumentBuilderException;
import org.ei.domain.EIDoc;
import org.ei.domain.ElementDataMap;
import org.ei.domain.FullDoc;
import org.ei.domain.ISBN;
import org.ei.domain.ISSN;
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

/**
 * This class is the implementation of DocumentBuilder Basically this class is responsible for building a List of EIDocs from a List of DocIds.The input ie list
 * of docids come from CBNBSearchControl and
 *
 */
public class CBNBDocBuilder implements DocumentBuilder {
    public static String CBNB_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String CBNB_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER_TEXT = "";

    public static final Key CBNB_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Controlled terms");
    public static final Key CBNB_COUNTRY = new Key(Keys.MULTIPLE_COUNTRY, "Country of origin");
    public static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.PROVIDER, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.SERIAL_TITLE, Keys.TITLE,
        Keys.VOLISSUE, Keys.ISSUE_DATE, Keys.SOURCE, Keys.p_PAGE_RANGE, Keys.NO_SO, Keys.PUBLICATION_DATE, Keys.ISBN, Keys.ISSN, Keys.AVAILABILITY,
        Keys.LANGUAGE, Keys.CODEN };
    public static final Key[] ABSTRACT_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.PROVIDER, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.DOC_TYPE,
        Keys.SERIAL_TITLE, Keys.TITLE, Keys.VOLISSUE, Keys.ISSUE_DATE, Keys.SOURCE, Keys.p_PAGE_RANGE, Keys.NO_SO, Keys.PUBLICATION_DATE, Keys.AVAILABILITY,
        Keys.ISBN, Keys.ISSN, Keys.LANGUAGE, Keys.CODEN, Keys.SCOPE, Keys.ABSTRACT, Keys.COMPANIES, Keys.CAS_REGISTRY_CODES, CBNB_CONTROLLED_TERMS,
        Keys.COUNTRY, Keys.CHEMICALS };
    public static final Key[] DETAILED_KEYS = { Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.TITLE_TRANSLATION, Keys.SERIAL_TITLE, Keys.VOLUME, Keys.ISSUE,
        Keys.PUBLICATION_DATE, Keys.PAGE_RANGE, Keys.LANGUAGE, Keys.ISBN, Keys.ISSN, Keys.CODEN, Keys.DOC_TYPE, Keys.AVAILABILITY, Keys.SCOPE, Keys.ABSTRACT,
        CBNB_CONTROLLED_TERMS, Keys.COMPANIES, Keys.CAS_REGISTRY_CODES, Keys.CHEMICALS, Keys.CHEMICAL_ACRONS, Keys.SIC_CODES, Keys.COUNTRY,
        Keys.MULTIPLE_COUNTRY, Keys.COUNTRY_CODES, Keys.INDUSTRIAL_SEC_CODES, Keys.INDUSTRIAL_SECTORS, Keys.DOCID, Keys.COPYRIGHT, Keys.PROVIDER,
        Keys.COPYRIGHT_TEXT };
    public static final Key[] RIS_KEYS = { Keys.RIS_AN, Keys.RIS_TY, Keys.RIS_N1, Keys.RIS_TI, Keys.RIS_JO, Keys.RIS_T3, Keys.RIS_VL, Keys.RIS_IS, Keys.RIS_PY,
        Keys.RIS_SP, Keys.RIS_EP, Keys.RIS_SN, Keys.RIS_N2, Keys.RIS_CVS };
    public static final Key[] XML_KEYS = { Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.SERIAL_TITLE, Keys.PUBLISHER, Keys.VOLUME, Keys.ISSUE, Keys.SOURCE,
        Keys.PUBLICATION_DATE, Keys.PAGE_RANGE, Keys.AVAILABILITY, Keys.ISBN, Keys.ISSN, Keys.LANGUAGE, Keys.CODEN, CBNB_CONTROLLED_TERMS, Keys.DOCID,
        Keys.COPYRIGHT, Keys.PROVIDER, Keys.COPYRIGHT_TEXT };

    // document builder interface methods which are called by EIDoc classes
    // for building detailed XML views of documents

    private Perl5Util perl = new Perl5Util();
    private static String queryCitation = "select M_ID,ATL,OTL,FJL,VOL,ISS,PBD,PAG,PBR,PAD,AVL,ISN,IBN,CDN,LAN,LOAD_NUMBER from cbn_master    where M_ID IN ";

    private static String queryAbstracts = "select M_ID,ATL,OTL,FJL,VOL,ISS,PBD,PAG,PBR,PAD,AVL,ISN,IBN,CDN,LAN,DOC,SCO,ABS,SRC,REG,EBT,SCT,CIN from cbn_master where M_ID IN ";

    // jam 12/30/2002
    // New Index - field change from AN to EX
    private static String queryXMLCitation = "select M_ID,ABN,ATL,OTL,FJL,VOL,ISS,PBD,PAG,PBR,PAD,AVL,ISN,IBN,CDN,LAN,DOC,SCO,ABS,SRC,REG,EBT,SCT,SCC,CIN,CYM,SIC,GIC,GID,LOAD_NUMBER from cbn_master where M_ID IN ";
    private static String queryDetailed = "select M_ID,ABN,ATL,OTL,FJL,VOL,ISS,PBD,PAG,PBR,PAD,AVL,ISN,IBN,CDN,LAN,DOC,SCO,ABS,SRC,REG,EBT,SCT,SCC,CIN,CYM,SIC,GIC,GID from cbn_master where M_ID IN ";

    private static String querypreview = "select M_ID, ABS from cbn_master where M_ID IN ";

    public DocumentBuilder newInstance(Database database) {
        return new CBNBDocBuilder(database);
    }

    public CBNBDocBuilder() {
    }

    public CBNBDocBuilder(Database database) {
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
        } else if (Citation.CITATION_PREVIEW.equalsIgnoreCase(dataFormat)) {
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
            rset = stmt.executeQuery(querypreview + INString);
            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_PREVIEW);

                ht.put(Keys.DOCID, (DocID) oidTable.get(rset.getString("M_ID")));
                ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.getStringFromClob(rset.getClob("ABS"))));
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
        return list;
    }

    private List<EIDoc> loadRIS(List<DocID> listOfDocIDs) throws DocumentBuilderException {

        List<EIDoc> list = new ArrayList<EIDoc>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

        String INString = buildINString(listOfDocIDs);

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();

            rset = stmt.executeQuery(queryDetailed + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();

                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));

                ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, CBNBDocBuilder.CBNB_TEXT_COPYRIGHT));

                if (rset.getString("ABN") != null) {
                    ht.put(Keys.RIS_AN, new XMLWrapper(Keys.RIS_AN, rset.getString("ABN")));
                }
                String strTitle = StringUtil.EMPTY_STRING;

                if ((rset.getString("ATL") != null) && (rset.getString("OTL") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("ATL"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("OTL"))).concat(" )");
                } else if (rset.getString("ATL") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("ATL")).concat(strTitle);
                } else if (rset.getString("OTL") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("OTL")).concat(strTitle);
                }

                ht.put(Keys.RIS_TI, new XMLWrapper(Keys.RIS_TI, strTitle));

                if (rset.getString("VOL") != null) {

                    String strVol = replaceVolumeNullWithEmptyString(rset.getString("VOL"));
                    ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, strVol));
                }
                if (rset.getString("ISS") != null) {
                    ht.put(Keys.RIS_IS, new Issue(rset.getString("ISS"), perl));
                }

                if (rset.getString("PBD") != null) {
                    ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, rset.getString("PBD"), perl));
                }

                String strPages = StringUtil.EMPTY_STRING;
                if (rset.getString("PAG") != null) {
                    strPages = StringUtil.replaceNullWithEmptyString(rset.getString("PAG"));
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

                if (rset.getString("ISN") != null) {
                    ht.put(Keys.RIS_SN, new ISSN(rset.getString("ISN")));
                }
                if (rset.getString("EBT") != null) {
                    ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(rset.getString("EBT"))));
                }

                if (rset.getClob("ABS") != null) {
                    String abs = null;
                    abs = checkAbstract(StringUtil.getStringFromClob(rset.getClob("ABS")));
                    if (!((abs).equals("")) && abs != null) {
                        ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, abs));
                    }
                }
                String docType = "";

                if (rset.getString("DOC") != null) {
                    docType = rset.getString("DOC");

                    if (docType.equalsIgnoreCase("Journal"))
                        docType = "JOUR";
                    ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, StringUtil.replaceNullWithEmptyString(docType)));
                }

                if (rset.getString("FJL") != null) {
                    ht.put(Keys.RIS_JO, new XMLWrapper(Keys.RIS_JO, StringUtil.replaceNullWithEmptyString(rset.getString("FJL"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, RIS.RIS_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(RIS_KEYS);
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
                } catch (ConnectionPoolException cpe) {
                    cpe.printStackTrace();
                }
            }
        }

        return list;
    }

    private List<EIDoc> loadAbstracts(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

        List<EIDoc> list = new ArrayList<EIDoc>();
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

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, CBNBDocBuilder.CBNB_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, CBNBDocBuilder.CBNB_TEXT_COPYRIGHT));

                if (rset.getString("FJL") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("FJL")));
                }

                if (rset.getString("PBR") != null) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PBR"))));
                }

                if (rset.getString("PBD") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PBD"), perl));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("ATL") != null) && (rset.getString("OTL") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("ATL"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("OTL"))).concat(" )");
                } else if (rset.getString("ATL") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("ATL")).concat(strTitle);
                } else if (rset.getString("OTL") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("OTL")).concat(strTitle);
                }

                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));

                strTitle = null;

                if (rset.getString("FJL") != null) {
                    String strVolIss = StringUtil.EMPTY_STRING;
                    // VO
                    if (rset.getString("VOL") != null) {
                        strVolIss = strVolIss.concat(replaceVolumeNullWithEmptyString(rset.getString("VOL")));
                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, StringUtil.replaceNullWithEmptyString(rset.getString("VOL")), perl));
                    }
                    if (rset.getString("ISS") != null) {
                        ht.put(Keys.ISSUE, new Issue(StringUtil.replaceNullWithEmptyString(rset.getString("ISS")), perl));
                        if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                            strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(rset.getString("ISS")));
                        } else {
                            strVolIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                        }
                    }
                    if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                    }

                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("FJL"))));

                    if (rset.getString("PBD") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PBD"))));
                    }
                    if (rset.getString("PAG") != null) {
                        ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("PAG"))));
                    }

                } else {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                if (rset.getString("FJL") == null && rset.getString("PBD") != null) {
                    ht.put(Keys.PUBLICATION_DATE, new XMLWrapper(Keys.PUBLICATION_DATE, rset.getString("PBD")));
                }

                // AVL
                String availability = getAvailability(rset.getString("PBR"), rset.getString("PAD"), rset.getString("AVL"));
                if (availability != null) {
                    ht.put(Keys.AVAILABILITY, new XMLWrapper(Keys.AVAILABILITY, availability));
                }

                // IBN
                if (rset.getString("IBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("IBN"))));
                }

                // ISN
                if (rset.getString("ISN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("ISN"))));
                }

                if ((rset.getString("LAN") != null) && (!rset.getString("LAN").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LAN"))));
                }

                if (rset.getString("CDN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("CDN"))));
                }

                if (rset.getString("DOC") != null) {
                    ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, StringUtil.replaceNullWithEmptyString(rset.getString("DOC"))));
                }

                if (rset.getString("SCO") != null) {
                    ht.put(Keys.SCOPE, new XMLWrapper(Keys.SCOPE, StringUtil.replaceNullWithEmptyString(rset.getString("SCO"))));
                }
                // AB
                if (rset.getClob("ABS") != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, checkAbstract(StringUtil.getStringFromClob(rset.getClob("ABS")))));
                }

                if (rset.getString("SRC") != null) {
                    ht.put(Keys.COMPANIES, new XMLMultiWrapper(Keys.COMPANIES, setElementData(rset.getString("SRC"))));
                }

                if (rset.getString("REG") != null) {
                    ht.put(Keys.CAS_REGISTRY_CODES, new XMLMultiWrapper(Keys.CAS_REGISTRY_CODES, setElementData(rset.getString("REG"))));
                }

                if (rset.getString("EBT") != null) {
                    ht.put(CBNB_CONTROLLED_TERMS, new XMLMultiWrapper2(CBNB_CONTROLLED_TERMS, setCVS(rset.getString("EBT"))));
                }

                if (rset.getString("SCT") != null) {
                    ht.put(Keys.COUNTRY, new XMLWrapper(CBNB_COUNTRY, StringUtil.replaceNullWithEmptyString(rset.getString("SCT"))));
                }

                if (rset.getString("CIN") != null) {
                    ht.put(Keys.CHEMICALS, new XMLMultiWrapper(Keys.CHEMICALS, setElementData(rset.getString("CIN"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Abstract.ABSTRACT_FORMAT);
                // eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(true);
                eiDoc.setOutputKeys(ABSTRACT_KEYS);
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
                } catch (ConnectionPoolException cpe) {
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
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

        List<EIDoc> list = new ArrayList<EIDoc>();
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
                // ht.put(EIDoc.HANDLE, Integer.toString(did.getHitIndex()));
                // ht.put(EIDoc.DATABASE_ID, did.getDatabase().getID());
                // ht.put("DM", Integer.toString(did.getDatabase().getMask()));
                // ht.put(EIDoc.DATABASE, "Chemical Business NewsBase");
                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, CBNBDocBuilder.CBNB_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, CBNBDocBuilder.CBNB_TEXT_COPYRIGHT));

                // TS 02/10/03 the following elements added to ht for xml document mapping
                if (rset.getString("FJL") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("FJL")));
                }

                if (rset.getString("PBR") != null) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PBR"))));
                }

                if (rset.getString("PBD") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PBD"), perl));
                }

                // AN
                if (rset.getString("ABN") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ABN")));
                }

                if (rset.getString("ATL") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("ATL"))));
                }
                if (rset.getString("OTL") != null) {
                    ht.put(Keys.TITLE_TRANSLATION, new XMLWrapper(Keys.TITLE_TRANSLATION, StringUtil.replaceNullWithEmptyString(rset.getString("OTL"))));
                }

                if (rset.getString("VOL") != null) {
                    String strVol = replaceVolumeNullWithEmptyString(rset.getString("VOL"));
                    ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, strVol, perl));
                }

                if (rset.getString("ISS") != null) {
                    String strIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                    ht.put(Keys.ISSUE, new Issue(strIss, perl));
                }

                if (rset.getString("PBD") != null && !(rset.getString("PBD").equals(StringUtil.EMPTY_STRING))) {
                    ht.put(Keys.PUBLICATION_DATE, new XMLWrapper(Keys.PUBLICATION_DATE, rset.getString("PBD")));
                }

                String strPg = StringUtil.EMPTY_STRING;
                if (rset.getString("PAG") != null) {
                    strPg = strPg.concat("p ").concat(StringUtil.replaceNullWithEmptyString(rset.getString("PAG")));
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPg, perl));
                }

                // AVL
                String availability = getAvailability(rset.getString("PBR"), rset.getString("PAD"), rset.getString("AVL"));
                if (availability != null) {
                    ht.put(Keys.AVAILABILITY, new XMLWrapper(Keys.AVAILABILITY, availability));
                }

                // IBN
                if (rset.getString("IBN") != null) {
                    ht.put(Keys.ISBN, new XMLWrapper(Keys.ISBN, rset.getString("IBN")));
                }
                // ISN
                if (rset.getString("ISN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("ISN"))));
                }

                if (rset.getString("LAN") != null) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, rset.getString("LAN")));
                }

                if (rset.getString("CDN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("CDN"))));
                }
                if (rset.getString("DOC") != null) {
                    ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, StringUtil.replaceNullWithEmptyString(rset.getString("DOC"))));
                }

                if (rset.getString("SCO") != null) {
                    ht.put(Keys.SCOPE, new XMLWrapper(Keys.SCOPE, StringUtil.replaceNullWithEmptyString(rset.getString("SCO"))));
                }

                // AB
                if (rset.getClob("ABS") != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, checkAbstract(StringUtil.getStringFromClob(rset.getClob("ABS")))));
                }

                if (rset.getString("SRC") != null) {
                    ht.put(Keys.COMPANIES, new XMLMultiWrapper(Keys.COMPANIES, setElementData(rset.getString("SRC"))));
                }

                if (rset.getString("REG") != null) {
                    ht.put(Keys.CAS_REGISTRY_CODES, new XMLMultiWrapper(Keys.CAS_REGISTRY_CODES, setElementData(rset.getString("REG"))));
                }

                if (rset.getString("EBT") != null) {
                    ht.put(CBNB_CONTROLLED_TERMS, new XMLMultiWrapper2(CBNB_CONTROLLED_TERMS, setCVS(rset.getString("EBT"))));
                }

                if (rset.getString("SCT") != null) {
                    ht.put(Keys.COUNTRY, new XMLWrapper(CBNB_COUNTRY, StringUtil.replaceNullWithEmptyString(rset.getString("SCT"))));
                }

                if (rset.getString("SCC") != null) {
                    ht.put(Keys.COUNTRY_CODES, new XMLMultiWrapper(Keys.COUNTRY_CODES, setElementData(rset.getString("SCC"))));
                }

                if (rset.getString("CIN") != null) {
                    ht.put(Keys.CHEMICALS, new XMLMultiWrapper(Keys.CHEMICALS, setElementData(rset.getString("CIN"))));
                }

                if (rset.getString("CYM") != null) {
                    ht.put(Keys.CHEMICAL_ACRONS, new XMLMultiWrapper(Keys.CHEMICAL_ACRONS, setElementData(rset.getString("CYM"))));
                }

                if (rset.getString("SIC") != null) {
                    ht.put(Keys.SIC_CODES, new XMLMultiWrapper(Keys.SIC_CODES, setElementData(rset.getString("SIC"))));
                }

                if (rset.getString("GIC") != null) {
                    ht.put(Keys.INDUSTRIAL_SEC_CODES, new XMLMultiWrapper(Keys.INDUSTRIAL_SEC_CODES, setElementData(rset.getString("GIC"))));
                }

                if (rset.getString("GID") != null) {
                    ht.put(Keys.INDUSTRIAL_SECTORS, new XMLMultiWrapper(Keys.INDUSTRIAL_SECTORS, setElementData(rset.getString("GID"))));

                }
                EIDoc eiDoc = new EIDoc(did, ht, Detail.FULLDOC_FORMAT);
                eiDoc.exportLabels(true);
                // eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(DETAILED_KEYS);
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
                } catch (ConnectionPoolException cpe) {
                    cpe.printStackTrace();
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
    private List<EIDoc> loadCitations(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

        List<EIDoc> list = new ArrayList<EIDoc>();
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

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, CBNBDocBuilder.CBNB_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, CBNBDocBuilder.CBNB_TEXT_COPYRIGHT));

                if (rset.getString("FJL") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("FJL")));
                }

                if (rset.getString("PBR") != null) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PBR"))));
                }

                if (rset.getString("PBD") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PBD"), perl));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("ATL") != null) && (rset.getString("OTL") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("ATL"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("OTL"))).concat(" )");
                } else if (rset.getString("ATL") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("ATL")).concat(strTitle);
                } else if (rset.getString("OTL") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("OTL")).concat(strTitle);
                }

                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));

                strTitle = null;
                if (rset.getString("FJL") != null) {
                    String strVolIss = StringUtil.EMPTY_STRING;
                    // VO
                    if (rset.getString("VOL") != null) {
                        strVolIss = strVolIss.concat(replaceVolumeNullWithEmptyString(rset.getString("VOL")));
                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, StringUtil.replaceNullWithEmptyString(rset.getString("VOL")), perl));
                    }
                    if (rset.getString("ISS") != null) {
                        ht.put(Keys.ISSUE, new Issue(StringUtil.replaceNullWithEmptyString(rset.getString("ISS")), perl));
                        if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                            strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(rset.getString("ISS")));
                        } else {
                            strVolIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                        }
                    }
                    if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                    }
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("FJL"))));

                    if (rset.getString("PBD") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PBD"))));
                    }
                    if (rset.getString("PAG") != null) {
                        ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("PAG"))));
                    }
                } else {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                if (rset.getString("FJL") == null && rset.getString("PBD") != null) {
                    ht.put(Keys.PUBLICATION_DATE, new XMLWrapper(Keys.PUBLICATION_DATE, rset.getString("PBD")));
                }

                // AVL
                if (rset.getString("PBR") != null) {
                    ht.put(Keys.AVAILABILITY, new XMLWrapper(Keys.AVAILABILITY, rset.getString("PBR")));
                } else {
                    if (rset.getString("AVL") != null) {
                        ht.put(Keys.AVAILABILITY, new XMLWrapper(Keys.AVAILABILITY, rset.getString("AVL")));
                    }
                }

                // IBN
                if (rset.getString("IBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("IBN"))));
                }

                // ISN
                if (rset.getString("ISN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("ISN"))));
                }

                if ((rset.getString("LAN") != null) && (!rset.getString("LAN").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LAN"))));
                }

                if (rset.getString("CDN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("CDN"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(CITATION_KEYS);
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
                } catch (ConnectionPoolException cpe) {
                    cpe.printStackTrace();
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

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, CBNBDocBuilder.CBNB_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, CBNBDocBuilder.CBNB_TEXT_COPYRIGHT));

                if (rset.getString("FJL") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("FJL")));
                }

                if (rset.getString("PBR") != null) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PBR"))));
                }

                if (rset.getString("PBD") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PBD"), perl));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("ATL") != null) && (rset.getString("OTL") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("ATL"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("OTL"))).concat(" )");
                } else if (rset.getString("ATL") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("ATL")).concat(strTitle);
                } else if (rset.getString("OTL") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("OTL")).concat(strTitle);
                }

                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));

                // AN
                if (rset.getString("ABN") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ABN")));
                }

                strTitle = null;
                if (rset.getString("FJL") != null) {
                    String strVolIss = StringUtil.EMPTY_STRING;
                    // VO
                    if (rset.getString("VOL") != null) {
                        strVolIss = strVolIss.concat(replaceVolumeNullWithEmptyString(rset.getString("VOL")));
                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, StringUtil.replaceNullWithEmptyString(rset.getString("VOL")), perl));
                    }
                    if (rset.getString("ISS") != null) {
                        ht.put(Keys.ISSUE, new Issue(StringUtil.replaceNullWithEmptyString(rset.getString("ISS")), perl));
                        if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                            strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(rset.getString("ISS")));
                        } else {
                            strVolIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                        }
                    }
                    if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                    }
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("FJL"))));

                    if (rset.getString("PBD") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PBD"))));
                    }
                    if (rset.getString("PAG") != null) {
                        ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("PAG"))));
                    }

                } else {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                if (rset.getString("FJL") == null && rset.getString("PBD") != null) {
                    ht.put(Keys.PUBLICATION_DATE, new XMLWrapper(Keys.PUBLICATION_DATE, rset.getString("PBD")));
                }

                String strPg = StringUtil.EMPTY_STRING;
                if (rset.getString("PAG") != null) {
                    strPg = strPg.concat("p ").concat(StringUtil.replaceNullWithEmptyString(rset.getString("PAG")));
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPg, perl));
                }

                if (rset.getString("EBT") != null) {
                    ht.put(CBNB_CONTROLLED_TERMS, new XMLMultiWrapper2(CBNB_CONTROLLED_TERMS, setCVS(rset.getString("EBT"))));
                }

                // AVL
                if (rset.getString("AVL") != null) {
                    ht.put(Keys.AVAILABILITY, new XMLWrapper(Keys.AVAILABILITY, rset.getString("AVL")));
                }

                // IBN
                if (rset.getString("IBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("IBN"))));
                }

                // ISN
                if (rset.getString("ISN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("ISN"))));
                }

                if ((rset.getString("LAN") != null) && (!rset.getString("LAN").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LAN"))));
                }

                if (rset.getString("CDN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("CDN"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.XMLCITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(XML_KEYS);
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
                } catch (ConnectionPoolException cpe) {
                    cpe.printStackTrace();
                }
            }
        }

        return list;
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
                sQuery.append("'" + docID + "'");
            } else {
                sQuery.append("'" + docID + "'").append(",");
            }
        } // end of for
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

    /* TS , end */
    private String checkAbstract(String s) {
        if (s == null) {
            return "";
        }

        if (s.length() < 100) {
            return "";
        }

        return s;
    }

    private Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }

        return h;
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

    private String getAvailability(String pbr, String pad, String avl) {
        String availability = null;

        if ((pbr == null) && (pad == null) && (avl == null)) {
            return null;
        }

        if (pbr != null) {
            availability = StringUtil.replaceNullWithEmptyString(pbr);
        }
        if (pad != null) {
            if (availability != null) {
                availability = availability.concat(StringUtil.replaceNullWithEmptyString(pad));
            } else {
                availability = StringUtil.replaceNullWithEmptyString(pad);
            }
        }
        if (avl != null) {
            if (availability != null) {
                availability = availability.concat(StringUtil.replaceNullWithEmptyString(avl));
            } else {
                availability = StringUtil.replaceNullWithEmptyString(avl);
            }
        }
        StringBuffer sb = null;
        if (availability != null) {
            Matcher m = org.ei.util.URLPlucker.UrlRegex.matcher(availability);
            sb = new StringBuffer();
            while (m.find()) {
                /*
                 * for(int g = 0; g <= m.groupCount(); g++) { System.out.println("Group " + g + ": " + m.group(g)); }
                 */
                if (m.group(0).indexOf("@") > 0) {
                    m.appendReplacement(sb, "<a class=\"SpLink\" title=\"External Link\" href=\"mailto:" + m.group() + "\">" + m.group() + "</a>");
                } else {
                    m.appendReplacement(sb, "<a target=\"_cbnb\" class=\"SpLink\" title=\"External Link\" href=\"" + m.group() + "\">" + m.group() + "</a>");
                }
            }
            m.appendTail(sb);
        }
        return (sb == null) ? null : sb.toString();
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
// End Of CBNBDocBuilder
