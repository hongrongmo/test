package org.ei.data.chem.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.common.AuthorStream;
import org.ei.common.DataCleaner;
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
 * of docids come from CHEMSearchControl and
 *
 */
public class ChemDocBuilder implements DocumentBuilder {
    public static String CHEM_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String CHEM_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER = "";
    public static String PROVIDER_TEXT = "";

    private static final Key CHEM_COUNTRY = new Key(Keys.COUNTRY, "Country of origin");
    private static final Key CHEM_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Controlled terms");
    private static final Key CHEM_CLASS_CODES = new Key(Keys.CLASS_CODES, "Numeric Class. Code");

    public static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.PROVIDER, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.SERIAL_TITLE, Keys.PUBLICATION_YEAR,
        Keys.TITLE, Keys.AUTHORS, Keys.AUTHOR_AFFS, Keys.SOURCE, Keys.ISSN, Keys.VOLISSUE, Keys.p_PAGE_RANGE, Keys.DOI };
    public static final Key[] ABSTRACT_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.PROVIDER, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.SERIAL_TITLE, Keys.TITLE, Keys.AUTHORS,
        Keys.AUTHOR_AFFS, Keys.SOURCE, Keys.VOLISSUE, Keys.VOLUME, Keys.ISSUE, Keys.p_PAGE_RANGE, Keys.NO_SO, Keys.PUBLICATION_YEAR, Keys.ISSN, Keys.CODEN,
        Keys.DOI, Keys.NUMBER_OF_REFERENCES, Keys.ABSTRACT, Keys.CAS_REGISTRY_CODES, Keys.CONTROLLED_TERMS };
    public static final Key[] DETAILED_KEYS = { Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.AUTHORS, Keys.AUTHOR_AFFS, Keys.SERIAL_TITLE, Keys.VOLUME, Keys.ISSUE,
        Keys.PUBLICATION_YEAR, Keys.PAGE_RANGE, Keys.LANGUAGE, Keys.ISSN, Keys.CODEN, Keys.DOC_TYPE, Keys.ABSTRACT, Keys.NUMBER_OF_REFERENCES,
        Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.CAS_REGISTRY_CODES, Keys.CLASS_CODES, Keys.DOI, Keys.DOCID, Keys.COPYRIGHT, Keys.PROVIDER,
        Keys.COPYRIGHT_TEXT };
    private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_N1, Keys.RIS_TI, Keys.RIS_AUS, Keys.RIS_AD, Keys.RIS_VL, Keys.RIS_IS, Keys.RIS_PY,
        Keys.RIS_AN, Keys.RIS_SP, Keys.RIS_SN, Keys.RIS_N2, Keys.RIS_DO, Keys.RIS_CVS, Keys.RIS_FLS };

    private static final Key[] XML_KEYS = { Keys.ISSN, Keys.NO_SO, Keys.PUBLICATION_YEAR, Keys.AUTHORS, Keys.DOCID, Keys.SOURCE, Keys.VOLUME, Keys.AUTHOR_AFFS,
        Keys.PROVIDER, Keys.COPYRIGHT_TEXT, Keys.DOI, Keys.TITLE, Keys.LANGUAGE, Keys.PAGE_RANGE, Keys.COPYRIGHT, Keys.ISSUE, Keys.ACCESSION_NUMBER,
        Keys.CONTROLLED_TERMS };

    // document builder interface methods which are called by EIDoc classes
    // for building detailed XML views of documents

    private Perl5Util perl = new Perl5Util();
    private Database database;

    private static String queryCitation = "select M_ID,TIE,TIF,AUT,ADR,STI,VIP,PYR,ISN,ISS,PAG,VOL,COD,LNA,DOI,LOAD_NUMBER from chm_master where M_ID IN ";

    private static String queryAbstracts = "select M_ID,TIE,TIF,AUT,ADR,STI,VIP,PYR,ISN,ISS,PAG,VOL,COD,REF,LNA,ABS,CRD,CTM,DOI from chm_master where M_ID IN ";

    private static String queryDetailed = "select M_ID,CHN,TIE,TIF,AUT,ADR,STI,VIP,PYR,ISN,ISS,PAG,VOL,ITD,COD,REF,LNA,STD,ABS,CRD,CTM,CFL,TRC,DOI from chm_master where M_ID IN ";
    private static String queryXMLCitation = "select M_ID,CHN,TIE,TIF,AUT,ADR,STI,VIP,PYR,ISN,ISS,PAG,VOL,ITD,COD,REF,LNA,STD,ABS,CRD,CTM,CFL,TRC,DOI,LOAD_NUMBER from chm_master where M_ID IN ";
    private static String queryPreview = "select M_ID, ABS from chm_master where M_ID IN ";

    // jam 12/30/2002
    // New Index - field change from AN to EX
    // private static String queryXMLCitation =
    // "select M_ID,ABN,ATL,OTL,FJL,VOL,ISS,PBD,PAG,PBR,PAD,AVL,ISN,IBN,CDN,LAN,DOC,SCO,ABS,SRC,REG,EBT,SCT,SCC,CIN,CYM,SIC,GIC,GID from cbn_master where M_ID IN ";

    public DocumentBuilder newInstance(Database database) {
        return new ChemDocBuilder(database);
    }

    public ChemDocBuilder() {
    }

    public ChemDocBuilder(Database database) {
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
            rset = stmt.executeQuery(queryPreview + INString);
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
        int count = 0;
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

                ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, ChemDocBuilder.CHEM_TEXT_COPYRIGHT));

                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("TIE") != null) && (rset.getString("TIF") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIE"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TIF"))).concat(")");
                } else if (rset.getString("TIE") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIE")).concat(strTitle);
                } else if (rset.getString("TIF") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIF")).concat(strTitle);
                }
                ht.put(Keys.RIS_TI, new XMLWrapper(Keys.RIS_TI, strTitle));

                String strAf = StringUtil.EMPTY_STRING;
                if (rset.getString("AUT") != null) {
                    Contributors authors = new Contributors(Keys.RIS_AUS, getContributors(rset.getString("AUT"), Keys.RIS_AUS));
                    ht.put(Keys.RIS_AUS, authors);

                    if (rset.getString("ADR") != null) {
                        Affiliation aff = new Affiliation(Keys.RIS_AD, StringUtil.replaceNullWithEmptyString(rset.getString("ADR")));
                        ht.put(Keys.RIS_AD, new Affiliations(Keys.RIS_AD, aff));
                    }
                }

                if (rset.getString("VOL") != null) {
                    String strVol = replaceVolumeNullWithEmptyString(rset.getString("VOL"));
                    ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, strVol));
                }
                if (rset.getString("ISS") != null) {
                    String strIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                    ht.put(Keys.RIS_IS, new Issue(strIss, perl));
                }
                if (rset.getString("PYR") != null) {
                    ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, rset.getString("PYR"), perl));
                }
                if (rset.getString("PAG") != null) {
                    String page = StringUtil.replaceNullWithEmptyString(rset.getString("PAG"));
                    ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, page));
                }
                if (rset.getString("ISN") != null) {
                    ht.put(Keys.RIS_SN, new ISSN(rset.getString("ISN")));
                }
                if (rset.getString("CTM") != null) {
                    ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(rset.getString("CTM"))));
                }
                if (rset.getString("CFL") != null) {
                    ht.put(Keys.RIS_FLS, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(rset.getString("CFL"))));
                }

                if (rset.getClob("ABS") != null) {
                    String abs = null;
                    abs = checkAbstract(StringUtil.getStringFromClob(rset.getClob("ABS")));
                    if (!((abs).equals("")) && abs != null) {
                        ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, abs));
                    }
                }

                if (rset.getString("STD") != null) {

                    String std = rset.getString("STD");

                    if (std.equalsIgnoreCase("Proceeding"))
                        std = "CONF";
                    else if (std.equalsIgnoreCase("Journal"))
                        std = "JOUR";
                    else if (std.equalsIgnoreCase("Book"))
                        std = "BOOK";

                    ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, std));
                }

                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.RIS_DO, new XMLWrapper(Keys.RIS_DO, rset.getString("DOI")));
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

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, ChemDocBuilder.CHEM_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, ChemDocBuilder.CHEM_TEXT_COPYRIGHT));

                if (rset.getString("STI") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("STI")));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("TIE") != null) && (rset.getString("TIF") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIE"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TIF"))).concat(")");
                } else if (rset.getString("TIE") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIE")).concat(strTitle);
                } else if (rset.getString("TIF") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIF")).concat(strTitle);
                }

                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));
                strTitle = null;

                // AUT
                if (rset.getString("AUT") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUT"), Keys.AUTHORS));

                    if (rset.getString("ADR") != null) {
                        Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, rset.getString("ADR"));
                        authors.setFirstAffiliation(affil);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }

                    ht.put(Keys.AUTHORS, authors);

                }

                if (rset.getString("STI") != null) {

                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("STI"))));

                    if (rset.getString("PYR") != null) {
                        ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                    }

                    if ((rset.getString("ISS") != null) && (rset.getString("VOL") != null)) {
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, rset.getString("VOL") + ", " + rset.getString("ISS")));

                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, StringUtil.replaceNullWithEmptyString(rset.getString("VOL")), perl));
                        ht.put(Keys.ISSUE, new Issue(StringUtil.replaceNullWithEmptyString(rset.getString("ISS")), perl));
                    }

                    if (rset.getString("PAG") != null) {
                        ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("PAG"))));
                    }

                }

                else {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                if (rset.getString("STI") == null && rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                }

                if ((rset.getString("LNA") != null) && (!rset.getString("LNA").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LNA"))));
                }

                if (rset.getString("ISN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("ISN"))));
                }

                if (rset.getString("PAG") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(rset.getString("PAG")), perl));
                }

                if (rset.getString("COD") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("COD"))));
                }

                // Number of ref
                if (rset.getString("REF") != null) {
                    ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, StringUtil.replaceNullWithEmptyString(rset.getString("REF"))));
                }

                // AB
                if (rset.getClob("ABS") != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, checkAbstract(StringUtil.getStringFromClob(rset.getClob("ABS")))));
                }

                if (rset.getString("CRD") != null) {
                    String strCRD = StringUtil.replaceNullWithEmptyString(rset.getString("CRD"));
                    strCRD = perl.substitute("s/%/ and /g", strCRD);
                    ht.put(Keys.CAS_REGISTRY_CODES, new XMLMultiWrapper(Keys.CAS_REGISTRY_CODES, setElementData(strCRD)));
                }

                if (rset.getString("CTM") != null) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(CHEM_CONTROLLED_TERMS, setCVS(rset.getString("CTM"))));
                }
                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
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

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, ChemDocBuilder.CHEM_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, ChemDocBuilder.CHEM_TEXT_COPYRIGHT));

                // TS 02/10/03 the following elements added to ht for xml document mapping

                if (rset.getString("CHN") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("CHN")));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("TIE") != null) && (rset.getString("TIF") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIF"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TIE"))).concat(")");
                } else if (rset.getString("TIE") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIE")).concat(strTitle);
                } else if (rset.getString("TIF") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIF")).concat(strTitle);
                }

                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.replaceNullWithEmptyString(strTitle)));

                strTitle = null;

                if (rset.getString("AUT") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUT"), Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);
                }

                if (rset.getString("ADR") != null) {
                    Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, rset.getString("ADR"));
                    ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                }

                if (rset.getString("STI") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("STI")));
                }

                String strVip = StringUtil.EMPTY_STRING;

                if (rset.getString("ISS") != null) {
                    String strIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                    ht.put(Keys.ISSUE, new Issue(strIss, perl));
                }

                if (rset.getString("VOL") != null) {
                    String strVol = replaceVolumeNullWithEmptyString(rset.getString("VOL"));
                    ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, strVol, perl));
                }

                if (rset.getString("PAG") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(rset.getString("PAG")), perl));
                }

                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                }

                if (rset.getString("LNA") != null) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, rset.getString("LNA")));
                }

                if (rset.getString("ISN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("ISN"))));
                }

                if (rset.getString("COD") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("COD"))));
                }

                String strDoc = StringUtil.EMPTY_STRING;
                if (rset.getString("STD") != null) {
                    strDoc = StringUtil.replaceNullWithEmptyString(rset.getString("STD"));
                }
                if ((rset.getString("ITD") != null) && (!rset.getString("ITD").equalsIgnoreCase(strDoc))) {
                    if (strDoc != null) {
                        strDoc = strDoc.concat(", ").concat(StringUtil.replaceNullWithEmptyString(rset.getString("ITD")));
                    } else {
                        strDoc = StringUtil.replaceNullWithEmptyString(rset.getString("ITD"));
                    }

                }
                ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, StringUtil.replaceNullWithEmptyString(strDoc)));

                // AB
                if (rset.getClob("ABS") != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, checkAbstract(StringUtil.getStringFromClob(rset.getClob("ABS")))));
                }

                if (rset.getString("REF") != null) {
                    ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, StringUtil.replaceNullWithEmptyString(rset.getString("REF"))));
                }

                if (rset.getString("CRD") != null) {
                    String strCRD = StringUtil.replaceNullWithEmptyString(rset.getString("CRD"));
                    strCRD = perl.substitute("s/%/ and /g", strCRD);
                    ht.put(Keys.CAS_REGISTRY_CODES, new XMLMultiWrapper(Keys.CAS_REGISTRY_CODES, setElementData(strCRD)));
                }

                if (rset.getString("CTM") != null) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(CHEM_CONTROLLED_TERMS, setCVS(rset.getString("CTM"))));
                }

                if (rset.getString("CFL") != null) {
                    ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(rset.getString("CFL"))));
                }

                if (rset.getString("TRC") != null) {
                    System.out.println("Class code: " + rset.getString("TRC"));
                    ht.put(Keys.CLASS_CODES, new Classifications(CHEM_CLASS_CODES, setElementData(rset.getString("TRC")), this.database));
                }
                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
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

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, ChemDocBuilder.CHEM_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, ChemDocBuilder.CHEM_TEXT_COPYRIGHT));

                if (rset.getString("STI") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("STI")));
                }
                if (rset.getString("ISN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("ISN"))));
                }

                if (rset.getString("COD") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("COD"))));
                }
                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                }
                if (rset.getString("ISS") != null) {
                    String strIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                    ht.put(Keys.ISSUE, new Issue(strIss, perl));
                }

                if (rset.getString("VOL") != null) {
                    String strVol = replaceVolumeNullWithEmptyString(rset.getString("VOL"));
                    ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, strVol, perl));
                }

                if (rset.getString("PAG") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(rset.getString("PAG")), perl));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("TIE") != null) && (rset.getString("TIF") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIF"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TIE"))).concat(")");
                }
                if (rset.getString("TIE") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIE")).concat(strTitle);
                } else if (rset.getString("TIF") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIF")).concat(strTitle);
                }

                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));
                strTitle = null;

                // AUT
                String strAf = StringUtil.EMPTY_STRING;
                if (rset.getString("AUT") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUT"), Keys.AUTHORS));

                    if (rset.getString("ADR") != null) {
                        Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, rset.getString("ADR"));
                        authors.setFirstAffiliation(affil);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }

                    ht.put(Keys.AUTHORS, authors);

                }

                if (rset.getString("STI") != null) {

                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("STI"))));

                    if ((rset.getString("ISS") != null) && (rset.getString("VOL") != null)) {

                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, rset.getString("VOL") + ", " + rset.getString("ISS")));
                    }

                    if (rset.getString("PAG") != null) {
                        ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("PAG"))));
                    }
                }

                if ((rset.getString("LNA") != null) && (!rset.getString("LNA").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LNA"))));
                }
                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
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

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, ChemDocBuilder.CHEM_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, ChemDocBuilder.CHEM_TEXT_COPYRIGHT));

                if (rset.getString("STI") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("STI")));
                }

                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("TIE") != null) && (rset.getString("TIF") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIF"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TIE"))).concat(")");
                }
                if (rset.getString("TIE") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIE")).concat(strTitle);
                } else if (rset.getString("TIF") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TIF")).concat(strTitle);
                }

                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));
                strTitle = null;
                // AN
                if (rset.getString("CHN") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("CHN")));
                }

                if (rset.getString("STI") != null) {

                    if ((rset.getString("ISS") != null) && (rset.getString("VOL") != null)) {
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, rset.getString("VOL") + ", " + rset.getString("ISS")));

                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, StringUtil.replaceNullWithEmptyString(rset.getString("VOL")), perl));
                        ht.put(Keys.ISSUE, new Issue(StringUtil.replaceNullWithEmptyString(rset.getString("ISS")), perl));
                    }

                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("STI"))));

                    if (rset.getString("PAG") != null) {
                        ht.put(Keys.p_PAGE_RANGE, new XMLWrapper(Keys.p_PAGE_RANGE, StringUtil.replaceNullWithEmptyString(rset.getString("PAG"))));
                    }
                } else {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                String strAf = StringUtil.EMPTY_STRING;
                if (rset.getString("AUT") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUT"), Keys.AUTHORS));

                    if (rset.getString("ADR") != null) {
                        Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, rset.getString("ADR"));
                        authors.setFirstAffiliation(affil);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }

                    ht.put(Keys.AUTHORS, authors);

                }

                if (rset.getString("PYR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PYR"), perl));
                }
                if (rset.getString("PAG") != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(rset.getString("PAG")), perl));
                }

                if (rset.getString("CTM") != null) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(CHEM_CONTROLLED_TERMS, setCVS(rset.getString("CTM"))));
                }

                // ISN
                if (rset.getString("ISN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("ISN"))));
                }

                if ((rset.getString("LNA") != null) && (!rset.getString("LNA").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LNA"))));
                }

                if (rset.getString("COD") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("COD"))));
                }
                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
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

    /*
     * private String replaceDTNullWithEmptyString(String str) { if(str==null || str.equals("QQ")) { str=StringUtil.EMPTY_STRING; }
     *
     * if( !str.equals(StringUtil.EMPTY_STRING)) { if (str.equals("JA")){str = "Journal article (JA)";} else if (str.equals("CA")){str =
     * "Conference article (CA)";} else if (str.equals("CP")){str = "Conference proceeding (CP)";} else if (str.equals("MC")){str = "Monograph chapter (MC)";}
     * else if (str.equals("MR")){str = "Monograph review (MR)";} else if (str.equals("RC")){str = "Report chapter (RC)";} else if (str.equals("RR")){str =
     * "Report review (RR)";} else if (str.equals("DS")){str = "Dissertation (DS)";} else if (str.equals("UP")){str = "Unpublished paper (UP)";} } return str; }
     */

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
