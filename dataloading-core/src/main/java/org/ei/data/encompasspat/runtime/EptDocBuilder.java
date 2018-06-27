package org.ei.data.encompasspat.runtime;

/*
 * Created on Jun 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author Tsolovye
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.common.AuthorStream;
import org.ei.common.CVSTermBuilder;
import org.ei.common.DataCleaner;
import org.ei.common.upt.AssigneeFilter;
import org.ei.common.upt.IPCClassNormalizer;
import org.ei.domain.Abstract;
import org.ei.domain.Citation;
import org.ei.domain.ClassNodeManager;
import org.ei.domain.Classification;
import org.ei.domain.ClassificationID;
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
import org.ei.domain.KeyValuePair;
import org.ei.domain.Keys;
import org.ei.domain.LinkedTerm;
import org.ei.domain.LinkedTermDetail;
import org.ei.domain.RIS;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLMultiWrapper2;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.exception.InfrastructureException;
import org.ei.util.StringUtil;

/**
 * This class is the implementation of DocumentBuilder Basically this class is responsible for building a List of EIDocs from a List of DocIds.The input ie list
 * of docids come from CPXSearchControl and
 *
 */
public class EptDocBuilder implements DocumentBuilder, Keys {
    public static String PAT_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String PAT_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER = "EnCompassPAT";
    private static final Key EPT_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Controlled terms");
    private static final Key LINKED_TERMS_HOLDER = new Key(Keys.LINKED_TERMS_HOLDER, "Linked terms");
    private static final Key EPT_MAJOR_TERMS = new Key(Keys.MAJOR_TERMS, "Major terms");
    private static final Key EPT_CLASS_CODES = new Key(Keys.CLASS_CODES_MULTI, "Classification codes");
    private static final Key EPT_UPAT_PUBDATE = new Key(Keys.UPAT_PUBDATE, "Publication year");

    public static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.PATENT_INFORMATION, Keys.PRIORITY_INFORMATION, Keys.TITLE, Keys.AUTHORS, Keys.PATASSIGN,
        Keys.AUTH_CODE, Keys.UPAT_PUBDATE, Keys.PROVIDER, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.LANGUAGE, Keys.NO_SO };
    public static final Key[] ABSTRACT_KEYS = { Keys.DOCID, Keys.DERWENT_NO, Keys.TITLE, Keys.AUTHORS, Keys.PATEPTASSIGN, Keys.PUBLICATION_YEAR,
        EPT_UPAT_PUBDATE, Keys.LANGUAGE, Keys.ABSTRACT, Keys.PATAPP_INFO, Keys.PATENT_INFORMATION, Keys.PRIORITY_INFORMATION, Keys.CAS_REGISTRY_CODES,
        Keys.INTERNATCL_CODE_EPT, EPT_MAJOR_TERMS, EPT_CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.NO_SO, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT,
        Keys.PROVIDER };
    public static final Key[] LINKED_TERM_KEYS = { Keys.LINKED_TERMS };

    public static final Key[] DETAILED_KEYS = { Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.INVENTORS, Keys.PATASSIGN, Keys.AUTH_CODE, Keys.PATENT_NUMBER,
        Keys.PATNUM, Keys.UPAT_PUBDATE, Keys.PUBLICATION_YEAR, Keys.LANGUAGE, Keys.PATAPP_INFO, Keys.PRIORITY_INFORMATION, Keys.INTERNATCL_CODE_EPT,
        Keys.DERWENT_NO, Keys.ABSTRACT, Keys.DESIGNATED_STATES, EPT_CLASS_CODES, Keys.DOC_TYPE, EPT_MAJOR_TERMS, EPT_CONTROLLED_TERMS, Keys.CAS_REGISTRY_CODES,
        Keys.UNCONTROLLED_TERMS, Keys.INDEXING_TEMPLATE, Keys.MANUAL_LINKED_TERMS, Keys.LINKED_TERMS, LINKED_TERMS_HOLDER,
        // Keys.INTERNATCL_CODE,

        Keys.DOCID, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.PROVIDER };
    public static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_M1, Keys.RIS_LA, Keys.RIS_TI, RIS_U2, Keys.RIS_AUS, Keys.RIS_EDS, Keys.RIS_PY, Keys.RIS_U1,
        Keys.RIS_N2, Keys.RIS_N1, Keys.RIS_AD, Keys.RIS_CVS };

    public static final Key[] XML_KEYS = { Keys.PATASSIGN, Keys.NO_SO, Keys.PATENT_ISSUE_DATE, Keys.AUTHORS, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT,
        Keys.PATCOUNTRY, Keys.PATNUM, Keys.PATFILDATE, Keys.TITLE, Keys.DOCID };

    private Perl5Util perl = new Perl5Util();
    private Database database;
    private static String queryCitation = "select ID,M_ID,DN,PAT_IN,PRI,CS,TI,AJ,AC,AD,AP,PC,PD,PN,PAT,LA,PY,PC,LOAD_NUMBER from ept_master where M_ID IN ";

    private static String queryAbstracts = "select ID,M_ID,DN,PAT_IN,CS,TI,TI2,PRI,AJ,AC,AD,AP,PC,PD,PN,PAT,LA,PY,DS,IC,LL,EY,CC,DT,CR,LTM,ATM,ATM1,ALC,AMS,APC,ANC,AT_API,CT,CRN,LT,UT,AB,LOAD_NUMBER from ept_master where M_ID    IN ";

    private static String queryDetailed = "select   "
        + "ID,M_ID,DN,PAT_IN,CS,TI,TI2,PRI,AJ,AC,AD,AP,PC,PD,PN,PAT,LA,PY,DS,IC,LL,EY,CC,DT,CR,LTM,ATM,ATM1,ALC,AMS,APC,ANC,AT_API,CT,CRN,LT,UT,AB,LOAD_NUMBER from ept_master where M_ID  IN ";

    private static String queryLinkedTerms = "select M_ID,LT,LOAD_NUMBER from ept_master where M_ID in ";

    private static String queryPreview = "select M_ID, AB from ept_master where M_ID in ";

    private static String DELIM;

    static {
        char d[] = { (char) 30 };
        DELIM = new String(d);
    }

    public DocumentBuilder newInstance(Database database) {
        return new EptDocBuilder(database);
    }

    public EptDocBuilder() {
    }

    public EptDocBuilder(Database database) {
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
        } else if (dataFormat.equals(LinkedTermDetail.LINKEDTERM_FORMAT)) {
            l = loadLinkedTerms(listOfDocIDs);
        } else if (dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT)) {
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
                String abs = hasAbstract(rset.getClob("AB"));

                if (StringUtils.isNotBlank(abs)) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(abs)));
                }
                list.add(eiDoc);

            }
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException("database connection pool problem", e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException("database connection pool avilable problem", e);
        } catch (SQLException e) {
            throw new DocumentBuilderException("database fetch problem", e);
        } catch (Exception e) {
            throw new DocumentBuilderException("database fetch problem", e);
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

    private List<EIDoc> loadRIS(List<DocID> listOfDocIDs) throws DocumentBuilderException {
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

            DatabaseConfig dconfig = DatabaseConfig.getInstance();

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                did.setDatabase(dconfig.getDatabase("ept"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, EptDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, EptDocBuilder.PAT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, EptDocBuilder.PAT_TEXT_COPYRIGHT));

                // Title
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.substituteChars(rset.getString("TI"))));
                }

                // Inventors
                if ((rset.getString("PAT_IN") != null) && (!rset.getString("PAT_IN").equals(""))) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.substituteChars(rset.getString("PAT_IN")), Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);

                }

                if (rset.getString("CS") != null) {
                    String aaf = StringUtil.substituteChars(StringUtil.replaceNullWithEmptyString(rset.getString("CS")));
                    List<String> lstAsgs = new ArrayList<String>();
                    perl.split(lstAsgs, "/;/", aaf);
                    ht.put(Keys.RIS_AD, new XMLMultiWrapper(Keys.RIS_AD, setAssignees(lstAsgs)));
                }
                // get accession number
                if (rset.getString("DN") != null) {
                    ht.put(Keys.RIS_U1, new XMLWrapper(Keys.RIS_U1, StringUtil.substituteChars(rset.getString("TI"))));
                }

                if (rset.getString("PN") != null) {

                    StringBuffer pubNum = new StringBuffer();

                    if (rset.getString("PC") != null) {
                        pubNum.append(rset.getString("PC"));
                    }

                    pubNum.append(rset.getString("PN"));

                    ht.put(Keys.RIS_U2, new XMLWrapper(Keys.RIS_U2, pubNum.toString()));

                }

                if (rset.getString("ct") != null) {

                    ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(StringUtil.substituteChars(rset.getString("ct")))));

                }

                if (rset.getString("UT") != null) {
                    ht.put(Keys.RIS_FLS, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(StringUtil.substituteChars(rset.getString("UT")))));
                }

                String abs = null;

                if ((abs = hasAbstract(rset.getClob("AB"))) != null) {
                    ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, StringUtil.substituteChars(abs)));
                }
                if (rset.getString("PY") != null) {
                    ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, rset.getString("PY"), perl));
                }

                ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, "PAT"));

                ht.put(Keys.RIS_M1, new XMLWrapper(Keys.RIS_M1, "EnCompassPAT"));

                EIDoc eiDoc = new EIDoc(did, ht, RIS.RIS_FORMAT);

                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
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
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }

    private String[] setAssignees(List<String> a) {
        HashMap<String, String> dupMap = new HashMap<String, String>();
        ArrayList<String> assignees = new ArrayList<String>();
        for (int i = 0; i < a.size(); i++) {

            String assignee = (String) a.get(i);
            String upAs = assignee.toUpperCase().trim();
            if (!dupMap.containsKey(upAs)) {
                assignees.add(assignee);
                dupMap.put(upAs, "");
            }

        }

        return (String[]) assignees.toArray(new String[assignees.size()]);
    }

    public String replaceSemiWithComma(String s) {
        byte[] sbytes = s.getBytes();
        char[] chars = new char[sbytes.length];
        boolean inEntity = false;

        for (int i = 0; i < sbytes.length; i++) {
            char c = (char) sbytes[i];
            if (!inEntity) {
                if (c == '&') {
                    inEntity = true;
                }

                if (c == ';') {
                    c = ',';
                }
            } else {
                if (c == ';' || c == ' ') {
                    inEntity = false;
                }
            }
            chars[i] = c;
        }

        return new String(chars);
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

            DatabaseConfig dconfig = DatabaseConfig.getInstance();

            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                did.setDatabase(dconfig.getDatabase("ept"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, EptDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, EptDocBuilder.PAT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, EptDocBuilder.PAT_TEXT_COPYRIGHT));

                // AccessNum Derwent Number
                if (rset.getString("DN") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("DN")));
                }
                // Title
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.substituteChars(rset.getString("TI"))));
                }
                // Inventors
                List<String> lstAsg = null;
                List<String> lstInv = null;

                if (rset.getString("PAT_IN") != null) {
                    lstInv = convertString2List(StringUtil.substituteChars(rset.getString("PAT_IN")));
                }

                if (rset.getString("CS") != null) {
                    lstAsg = convertString2List(StringUtil.substituteChars(rset.getString("CS")));
                }

                if (rset.getString("PC") != null) {
                    rset.getString("PC");
                }

                // if (lstAsg != null && lstInv != null)
                // lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);

                // IN Inventor
                if (lstInv != null && lstInv.size() > 0) {
                    Contributors authors = new Contributors(Keys.AUTHORS, setContributors(lstInv, Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);
                }

                if (lstAsg != null && lstAsg.size() > 0) {
                    ht.put(Keys.PATEPTASSIGN, new XMLMultiWrapper(Keys.PATEPTASSIGN, setAssignees(lstAsg)));
                }

                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                if (rset.getString("PY") != null) {
                    // specs 11/07 - no formating for date
                    // String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("PD")));
                    ht.put(Keys.UPAT_PUBDATE, new XMLWrapper(EPT_UPAT_PUBDATE, rset.getString("PY")));
                }

                // Language
                if ((rset.getString("LA") != null)) {
                    String language = rset.getString("LA");
                    if (language.length() > 1)
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.substituteChars(rset.getString("LA"))));
                }

                StringBuffer pubNum = new StringBuffer();

                // Publication Number
                if (rset.getString("PC") != null) {
                    pubNum.append(rset.getString("PC"));
                }

                if (rset.getString("PN") != null) {
                    pubNum.append(removeLeadingZeros(rset.getString("PN")));
                }
                // Specs 11/07 PATENT_NUMBER remove from abstact
                // if (pubNum.length() > 0)
                // ht.put(Keys.PATENT_NUMBER, new XMLWrapper(Keys.PATENT_NUMBER, pubNum.toString()));

                if (rset.getString("PN") != null) {
                    ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, removeLeadingZeros(rset.getString("PN"))));
                }

                // Application Information
                String appinfo = rset.getString("LL");
                if ((appinfo != null)) {
                    ht.put(Keys.PATAPP_INFO, new XMLWrapper(Keys.PATAPP_INFO, formatPriorityInfo(removeSemiColon(rset.getString("LL")))));
                }

                // add patent info
                if (rset.getString("PAT") != null) {
                    ht.put(Keys.PATENT_INFORMATION, new XMLWrapper(Keys.PATENT_INFORMATION, formatPriorityInfo(removeSemiColon(rset.getString("PAT")))));
                }

                // add pat priority info
                if (rset.getString("PRI") != null) {
                    ht.put(Keys.PRIORITY_INFORMATION,
                        new XMLWrapper(Keys.PRIORITY_INFORMATION, removeSemiColon(formatPriorityInfo(rset.getString("PRI")), ";", "|")));
                }
                // AB
                String abs = null;
                if ((abs = hasAbstract(rset.getClob("AB"))) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(abs)));
                }
                // IC
                if (rset.getString("IC") != null) {
                    String[] arrIpcs = IPCClassNormalizer.trimLeadingZeroFromSubClass(rset.getString("IC"));
                    ht.put(Keys.INTERNATCL_CODE_EPT, new Classifications(Keys.INTERNATCL_CODE_EPT, getIPCClssificationsDT(arrIpcs)));
                }

                // CRN

                if (rset.getString("CRN") != null) {
                    ht.put(Keys.CAS_REGISTRY_CODES,
                        new XMLMultiWrapper2(Keys.CAS_REGISTRY_CODES, setCRC(StringUtil.substituteChars(removePoundSign(rset.getString("CRN"))))));
                }
                // CT
                CVSTermBuilder termBuilder = new CVSTermBuilder();
                if (rset.getString("CT") != null) {
                    String ct = StringUtil.replaceNullWithEmptyString(rset.getString("CT"));
                    String cv = termBuilder.getNonMajorTerms(ct);
                    String mh = termBuilder.getMajorTerms(ct);
                    StringBuffer cvBuffer = new StringBuffer();

                    String expandedMajorTerms = termBuilder.expandMajorTerms(mh);

                    String expandedMH = termBuilder.getMajorTerms(expandedMajorTerms);

                    String expandedCV1 = termBuilder.expandNonMajorTerms(cv);
                    String expandedCV2 = termBuilder.getNonMajorTerms(expandedMajorTerms);

                    if (!expandedCV1.equals(""))
                        cvBuffer.append(expandedCV1);
                    if (!expandedCV2.equals(""))
                        cvBuffer.append(";").append(expandedCV2);

                    String parsedCV = CVSTermBuilder.formatCT(cvBuffer.toString());
                    parsedCV = StringUtil.replaceNonAscii(parsedCV);
                    String parsedMH = CVSTermBuilder.formatCT(expandedMH);
                    parsedMH = StringUtil.replaceNonAscii(parsedMH);

                    StringBuffer nonMajorTerms = new StringBuffer();

                    nonMajorTerms.append(termBuilder.getStandardTerms(parsedCV));
                    nonMajorTerms.append(";").append(termBuilder.getNoRoleTerms(parsedCV));
                    nonMajorTerms.append(";").append(termBuilder.getReagentTerms(parsedCV));
                    nonMajorTerms.append(";").append(termBuilder.getProductTerms(parsedCV));

                    ht.put(EPT_CONTROLLED_TERMS,
                        new XMLMultiWrapper2(EPT_CONTROLLED_TERMS, setCVS(StringUtil.substituteChars(formatCV(nonMajorTerms.toString())))));

                    StringBuffer majorTerms = new StringBuffer();

                    majorTerms.append(termBuilder.removeRoleTerms(parsedMH));
                    majorTerms.append(";").append(termBuilder.getMajorNoRoleTerms(parsedMH));
                    majorTerms.append(";").append(termBuilder.getMajorReagentTerms(parsedMH));
                    majorTerms.append(";").append(termBuilder.getMajorProductTerms(parsedMH));

                    ht.put(EPT_MAJOR_TERMS, new XMLMultiWrapper2(EPT_MAJOR_TERMS, setCVS(StringUtil.substituteChars(formatCV(majorTerms.toString())))));

                }

                // UT
                if (rset.getString("UT") != null) {
                    ht.put(Keys.UNCONTROLLED_TERMS,
                        new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(StringUtil.substituteChars(removePoundSign(rset.getString("UT"))))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Abstract.ABSTRACT_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(true);
                eiDoc.setOutputKeys(ABSTRACT_KEYS);
                list.add(eiDoc);
            }

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (IOException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } catch (InfrastructureException e) {
            throw new DocumentBuilderException(e.getErrorCode(), e);
        } finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }

    public static String removePoundSign(String ct) {

        Perl5Util perl = new Perl5Util();

        if (ct == null)
            return "";

        String parsedCT = perl.substitute("s/\\#//g", ct);

        return parsedCT;

    }

    private String formatPriorityInfo(String priorityInfo) {

        StringBuffer pi = new StringBuffer();
        StringTokenizer st = new StringTokenizer(priorityInfo, ";", false);

        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (!token.equals(""))
                pi.append(token).append(";");
        }

        priorityInfo = perl.substitute("s/;$//g", priorityInfo);

        return priorityInfo;
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
                    list.add(strToken);
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

    public Classification[] getIPCClssifications(String[] classcodes) throws InfrastructureException, IOException  {
        Classification[] cls = new Classification[classcodes.length];
        ClassNodeManager mn = ClassNodeManager.getInstance();

        for (int i = 0; i < classcodes.length; i++) {
            ClassificationID cid = new ClassificationID(classcodes[i], this.database);
            Classification cl = new Classification(Keys.INTERNATCL_CODE, cid);
            String code = IPCClassNormalizer.normalize(classcodes[i]);
            String title = mn.seekIPC(code);
            if (title == null) {
                title = "";
            }
            cl.setClassTitle(title);
            cls[i] = cl;
        }

        return cls;
    }

    public Classification[] getIPCClssificationsDT(String[] classcodes) throws InfrastructureException, IOException  {
        Classification[] cls = new Classification[classcodes.length];
        ClassNodeManager mn = ClassNodeManager.getInstance();

        for (int i = 0; i < classcodes.length; i++) {
            ClassificationID cid = new ClassificationID(classcodes[i], this.database);
            Classification cl = new Classification(Keys.INTERNATCL_CODE_EPT, cid);
            String code = IPCClassNormalizer.normalize(classcodes[i]);
            String title = mn.seekIPC(code);
            if (title == null) {
                title = "";
            }
            cl.setClassTitle(title);
            cls[i] = cl;
        }

        return cls;
    }

    private String hasAbstract(Clob clob) throws SQLException  {
        String abs = null;

        if (clob != null) {
            abs = StringUtil.getStringFromClob(clob);
        }

        if (abs == null || abs.equals("QQ")) {
            return null;
        } else {
            abs = perl.substitute("s/^<p>//i", abs);
            abs = perl.substitute("s/<\\/p>$//i", abs);
            abs = perl.substitute("s/&amp;/&/ig", abs);

            if (abs.equals(""))
                return null;
            else
                return abs;
        }
    }

    private String formatPIDate(String pi) {
        Character ch = new Character((char) 31);
        String nestedDelim = ch.toString();
        StringBuffer result = new StringBuffer();
        StringTokenizer st = new StringTokenizer(pi, nestedDelim);

        if (st.countTokens() > 2) {
            result.append(st.nextToken()).append(" ");
            result.append(st.nextToken()).append(" ");
            String lasttoken = st.nextToken().trim();
            if ((lasttoken.length() >= 6) && (perl.match("/^[0-9]{6,}/", lasttoken))) {
                result.append(formatDate(lasttoken)).append(" ");
            } else {
                pi = perl.substitute("s/" + nestedDelim + "/ /g", pi);
                return pi;
            }
            while (st.hasMoreTokens()) {
                result.append(st.nextToken()).append(" ");
            }
        }

        if (result.length() > 0) {
            return result.toString();
        } else {
            pi = perl.substitute("s/" + nestedDelim + "/ /g", pi);
            return pi;
        }
    }

    public String formatDate(String date) {
        StringBuffer result = null;
        if ((date != null) && (date.length() > 5)) {
            result = new StringBuffer();
            result.append(date.substring(4, 6));
            result.append("/");
            result.append(date.substring(6));
            result.append("/");
            result.append(date.substring(0, 4));
            return result.toString();
        } else {
            return date;
        }

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

            DatabaseConfig dconfig = DatabaseConfig.getInstance();

            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                did.setDatabase(dconfig.getDatabase("ept"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, EptDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, EptDocBuilder.PAT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, EptDocBuilder.PAT_TEXT_COPYRIGHT));

                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                // Title
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.substituteChars(rset.getString("TI"))));
                }
                // Inventors
                List<String> lstAsg = null;
                List<String> lstInv = null;

                if (rset.getString("PAT_IN") != null) {
                    lstInv = convertString2List(StringUtil.substituteChars(rset.getString("PAT_IN")));
                }

                if (rset.getString("CS") != null) {
                    lstAsg = convertString2List(StringUtil.substituteChars(rset.getString("CS")));
                }

                if (rset.getString("PC") != null) {
                    rset.getString("PC");
                }

                // if (lstAsg != null && lstInv != null)
                // {
                // lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);
                // }

                // IN Inventor

                if (lstInv != null && lstInv.size() > 0) {
                    Contributors authors = new Contributors(Keys.INVENTORS, setContributors(lstInv, Keys.INVENTORS));
                    ht.put(Keys.INVENTORS, authors);

                }
                // AF- CS

                if (lstAsg != null && lstAsg.size() > 0) {
                    ht.put(new Key(Keys.PATASSIGN, "Patent assignee"), new XMLMultiWrapper(new Key(Keys.PATASSIGN, "Patent assignee"), setAssignees(lstAsg)));
                }

                if (rset.getString("ATM") != null) {

                    if (rset.getClob("LTM") != null) {

                        String ltm = StringUtil.getLTStringFromClob(rset.getClob("LTM"));

                        if (!ltm.equalsIgnoreCase("QQ")) {
                            // ht.put(Keys.MANUAL_LINKED_TERMS, new LinkedTerms(Keys.MANUAL_LINKED_TERMS, getLinkedTerms(StringUtil.substituteChars(ltm),
                            // Keys.LINKED_SUB_TERM)));
                            ht.put(Keys.MANUAL_LINKED_TERMS, new XMLWrapper(Keys.MANUAL_LINKED_TERMS, StringUtil.substituteChars(ltm)));
                        }

                    }
                }

                // DS
                if (rset.getString("DS") != null) {
                    ht.put(Keys.DESIGNATED_STATES,
                        new XMLMultiWrapper(Keys.DESIGNATED_STATES, setElementData(StringUtil.substituteChars(rset.getString("DS")))));
                }

                // PY
                if ((rset.getString("PY") != null)) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PY"), perl));
                }
                // Language
                if ((rset.getString("LA") != null)) {
                    String language = rset.getString("LA");
                    if (language.length() > 1)
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.substituteChars(rset.getString("LA"))));
                }

                // CAS
                if (rset.getString("CRN") != null) {
                    ht.put(Keys.CAS_REGISTRY_CODES,
                        new XMLMultiWrapper(Keys.CAS_REGISTRY_CODES, setElementData(StringUtil.substituteChars(removePoundSign(rset.getString("CRN"))))));

                }
                // AccessNum DN
                if (rset.getString("DN") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("DN")));

                }

                // AB
                String abs = null;
                if ((abs = hasAbstract(rset.getClob("AB"))) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(abs)));
                }
                // AJ
                if (rset.getString("AJ") != null) {
                    ht.put(Keys.DERWENT_NO, new XMLWrapper(Keys.DERWENT_NO, formatDerwentNo(rset.getString("AJ"))));
                }
                // PC
                if (rset.getString("PC") != null) {
                    ht.put(new Key(Keys.AUTH_CODE, "Patent country"), new XMLWrapper(new Key(Keys.AUTH_CODE, "Patent country"), rset.getString("PC")));

                }
                // PD
                if (rset.getString("PD") != null) {
                    // specs 11/07 - no formating for date
                    // String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("PD")));
                    ht.put(Keys.UPAT_PUBDATE, new XMLWrapper(Keys.UPAT_PUBDATE, rset.getString("PD")));

                }

                StringBuffer pubNum = new StringBuffer();
                // Publication Number
                if (rset.getString("PC") != null) {
                    pubNum.append(rset.getString("PC"));
                }

                if (rset.getString("PN") != null) {
                    pubNum.append(removeLeadingZeros(rset.getString("PN")));
                }

                if (pubNum.length() > 0)
                    ht.put(Keys.PATENT_NUMBER, new XMLWrapper(Keys.PATENT_NUMBER, pubNum.toString()));

                if (rset.getString("PN") != null) {
                    ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, removeLeadingZeros(rset.getString("PN"))));
                }
                // Publication Year
                if ((rset.getString("PY") != null)) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PY"), perl));
                }
                // IC
                if (rset.getString("IC") != null) {

                    String[] arrIpcs = IPCClassNormalizer.trimLeadingZeroFromSubClass(rset.getString("IC"));
                    ht.put(new Key(Keys.INTERNATCL_CODE_EPT, "Int. patent classification"), new Classifications(new Key(Keys.INTERNATCL_CODE_EPT,
                        "Int. patent classification"), getIPCClssificationsDT(arrIpcs)));

                }

                // IC
                // if (rset.getString("IC") != null) {
                // String[] arrIpcs = IPCClassNormalizer.trimLeadingZeroFromSubClass(rset.getString("IC"));
                // ht.put(Keys.INTERNATCL_CODE, new Classifications(Keys.INTERNATCL_CODE, getIPCClssifications(arrIpcs)));
                //
                // }

                // APPLICATION INFO
                if ((rset.getString("LL") != null)) {
                    ht.put(Keys.PATAPP_INFO, new XMLMultiWrapper(Keys.PATAPP_INFO, setElementData(formatPriorityInfo(removeSemiColon(rset.getString("LL"))))));
                }
                // add app priority info
                if (rset.getString("PRI") != null) {
                    ht.put(Keys.PRIORITY_INFORMATION,
                        new XMLMultiWrapper(Keys.PRIORITY_INFORMATION, setElementData(((formatPriorityInfo(removeSemiColon(rset.getString("PRI"))))))));

                }
                // CC
                if (rset.getString("CC") != null) {
                    ht.put(Keys.CLASS_CODES_MULTI, new XMLMultiWrapper(EPT_CLASS_CODES, setElementData(StringUtil.substituteChars(rset.getString("CC")))));
                }

                // if (rset.getString("CC") != null)
                // {
                // ht.put(Keys.CLASS_CODES,
                // new Classifications(EPT_CLASS_CODES, setElementData(rset.getString("CC")), this.database));
                // }

                // Language
                if ((rset.getString("LA") != null)) {
                    String language = rset.getString("LA");
                    if (language.length() > 1)
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.substituteChars(rset.getString("LA"))));
                }

                // CT
                CVSTermBuilder termBuilder = new CVSTermBuilder();
                // CT
                if (rset.getString("CT") != null) {
                    String ct = StringUtil.replaceNullWithEmptyString(rset.getString("CT"));
                    String cv = termBuilder.getNonMajorTerms(ct);
                    String mh = termBuilder.getMajorTerms(ct);
                    StringBuffer cvBuffer = new StringBuffer();

                    String expandedMajorTerms = termBuilder.expandMajorTerms(mh);

                    String expandedMH = termBuilder.getMajorTerms(expandedMajorTerms);

                    String expandedCV1 = termBuilder.expandNonMajorTerms(cv);
                    String expandedCV2 = termBuilder.getNonMajorTerms(expandedMajorTerms);

                    if (!expandedCV1.equals(""))
                        cvBuffer.append(expandedCV1);
                    if (!expandedCV2.equals(""))
                        cvBuffer.append(";").append(expandedCV2);

                    String parsedCV = CVSTermBuilder.formatCT(cvBuffer.toString());
                    parsedCV = StringUtil.replaceNonAscii(parsedCV);
                    String parsedMH = CVSTermBuilder.formatCT(expandedMH);
                    parsedMH = StringUtil.replaceNonAscii(parsedMH);

                    StringBuffer nonMajorTerms = new StringBuffer();

                    nonMajorTerms.append(termBuilder.getStandardTerms(parsedCV));
                    nonMajorTerms.append(";").append(termBuilder.getNoRoleTerms(parsedCV));
                    nonMajorTerms.append(";").append(termBuilder.getReagentTerms(parsedCV));
                    nonMajorTerms.append(";").append(termBuilder.getProductTerms(parsedCV));

                    ht.put(EPT_CONTROLLED_TERMS,
                        new XMLMultiWrapper2(EPT_CONTROLLED_TERMS, setCVS(StringUtil.substituteChars(formatCV(nonMajorTerms.toString())))));

                    StringBuffer majorTerms = new StringBuffer();

                    majorTerms.append(termBuilder.removeRoleTerms(parsedMH));
                    majorTerms.append(";").append(termBuilder.getMajorNoRoleTerms(parsedMH));
                    majorTerms.append(";").append(termBuilder.getMajorReagentTerms(parsedMH));
                    majorTerms.append(";").append(termBuilder.getMajorProductTerms(parsedMH));

                    ht.put(EPT_MAJOR_TERMS, new XMLMultiWrapper2(EPT_MAJOR_TERMS, setCVS(StringUtil.substituteChars(formatCV(majorTerms.toString())))));

                }
                // if (rset.getString("ct") != null) {
                //
                // StringBuffer cvs = new StringBuffer();
                // StringBuffer cvm = new StringBuffer();
                //
                // if (rset.getString("cvs") != null) {
                // cvs.append(rset.getString("cvs"));
                // }
                // if (rset.getString("cvm") != null) {
                // cvm.append(rset.getString("cvm"));
                // }
                // if (rset.getString("cva") != null) {
                // if (cvs.length() > 0)
                // cvs.append(";").append(rset.getString("cva"));
                // else
                // cvs.append(rset.getString("cva"));
                // }
                // if (rset.getString("cvp") != null) {
                // if (cvs.length() > 0)
                // cvs.append(";").append(rset.getString("cvp"));
                // else
                // cvs.append(rset.getString("cvp"));
                // }
                // if (rset.getString("cvn") != null) {
                // if (cvs.length() > 0)
                // cvs.append(";").append(rset.getString("cvn"));
                // else
                // cvs.append(rset.getString("cvn"));
                // }
                // if (rset.getString("cvma") != null) {
                // if (cvm.length() > 0)
                // cvm.append(";").append(rset.getString("cvma"));
                // else
                // cvm.append(rset.getString("cvma"));
                // }
                // if (rset.getString("cvmp") != null) {
                // if (cvm.length() > 0)
                // cvm.append(";").append(rset.getString("cvmp"));
                // else
                // cvm.append(rset.getString("cvmp"));
                // }
                // if (rset.getString("cvmn") != null) {
                // if (cvm.length() > 0)
                // cvm.append(";").append(rset.getString("cvmn"));
                // else
                // cvm.append(rset.getString("cvmn"));
                // }
                //
                // if (cvs.length() > 0)
                // ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(EPT_CONTROLLED_TERMS,
                // setCVS(StringUtil.substituteChars(CVSTermBuilder.formatCT(cvs.toString())))));
                //
                // if (cvm.length() > 0)
                // ht.put(Keys.MAJOR_TERMS, new XMLMultiWrapper2(EPT_MAJOR_TERMS, setCVS(StringUtil.substituteChars(CVSTermBuilder.formatCT(cvm.toString())))));
                // }

                // UT
                if (rset.getString("UT") != null) {
                    ht.put(Keys.UNCONTROLLED_TERMS,
                        new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(StringUtil.substituteChars(removePoundSign(rset.getString("UT"))))));
                }
                // ATM

                if (rset.getString("ATM") != null) {
                    StringBuffer tempterms = new StringBuffer(rset.getString("ATM"));
                    if (rset.getString("ATM1") != null) {
                        tempterms.append(rset.getString("ATM1"));
                    }

                    ht.put(Keys.INDEXING_TEMPLATE,
                        new XMLWrapper(Keys.INDEXING_TEMPLATE, StringUtil.substituteChars(replaceBar(formatATM(tempterms.toString())))));

                    if (rset.getClob("LT") != null) {
                        String label = getLTLink();
                        ht.put(LINKED_TERMS_HOLDER, new XMLWrapper(LINKED_TERMS_HOLDER, label));
                    }
                } else {
                    if (rset.getClob("LT") != null) {
                        String linkedTerms = StringUtil.replaceNullWithEmptyString(StringUtil.getLTStringFromClob(rset.getClob("LT")));

                        if (!linkedTerms.equals("") && !linkedTerms.equalsIgnoreCase("QQ")) {
                            ht.put(Keys.LINKED_TERMS, new XMLWrapper(Keys.LINKED_TERMS, StringUtil.substituteChars(removePoundSign(linkedTerms))));
                        }
                    }
                }

                EIDoc eiDoc = new EIDoc(did, ht, Detail.FULLDOC_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(true);
                eiDoc.setOutputKeys(DETAILED_KEYS);
                list.add(eiDoc);
            }

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (IOException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } catch (InfrastructureException e) {
            throw new DocumentBuilderException(e.getErrorCode(), e);
        } finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }

    public List<LinkedTerm> getLinkedTerms(String lterms, Key key) {

        new StringBuffer();
        StringTokenizer ltgTokens = null;
        List<LinkedTerm> terms = new ArrayList<LinkedTerm>();

        ltgTokens = new StringTokenizer(lterms, "|", false);
        while (ltgTokens.hasMoreTokens()) {
            String ltSubset = ltgTokens.nextToken();

            LinkedTerm linkedTerm = new LinkedTerm();
            linkedTerm.setKey(key);
            linkedTerm.setTerm(ltSubset);
            terms.add(linkedTerm);
        }

        return terms;
    }

    private KeyValuePair[] setCVS(String cvs) {
        ArrayList<KeyValuePair> list = new ArrayList<KeyValuePair>();
        StringTokenizer st = new StringTokenizer(cvs, ";", false);
        String strToken = null;

        while (st.hasMoreTokens()) {
            strToken = st.nextToken().trim();
            if (strToken.length() > 0 && !strToken.equals("")) {
                KeyValuePair k = new KeyValuePair(getTermField(strToken), strToken);
                list.add(k);
            }

        }

        return (KeyValuePair[]) list.toArray(new KeyValuePair[list.size()]);

    }

    private KeyValuePair[] setCLS(String cls) {
        ArrayList<KeyValuePair> list = new ArrayList<KeyValuePair>();
        StringTokenizer st = new StringTokenizer(cls, ";", false);
        String strToken = null;

        while (st.hasMoreTokens()) {
            strToken = st.nextToken().trim();
            if (strToken.length() > 0) {
                KeyValuePair k = new KeyValuePair(Keys.ELT_CLASS_CODE, strToken);
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
        } else {
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

    private KeyValuePair[] setCRC(String cvs) {
        ArrayList<KeyValuePair> list = new ArrayList<KeyValuePair>();
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

    public String formatDerwentNo(String accession) {

        if (accession == null)
            return "";

        String newAccession = perl.substitute("s/DERWENT//ig", accession);

        return newAccession;

    }

    private String getLTLink() {

        StringBuffer ltlink = new StringBuffer();

        ltlink.append("Please click here to view all linked terms");

        return ltlink.toString();
    }

    public List<String> convertString2List(String sList) {

        if (sList == null)
            return new ArrayList<String>();

        List<String> lstVals = new ArrayList<String>();

        perl.split(lstVals, "/;/", sList);

        return lstVals;
    }

    private List<EIDoc> loadLinkedTerms(List<DocID> listOfDocIDs) throws DocumentBuilderException  {

        List<EIDoc> list = new ArrayList<EIDoc>();
        String INString = buildINString(listOfDocIDs);
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryLinkedTerms + INString);
            DatabaseConfig dconfig = DatabaseConfig.getInstance();
            new CVSTermBuilder();
            if (rset.next()) {
                // Common Fields
                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                String mid = rset.getString("M_ID");

                DocID did = new DocID(mid, dconfig.getDatabase("ept"));

                if (rset.getClob("LT") != null) {
                    String linkedTerms = CVSTermBuilder.formatCT(StringUtil.replaceNonAscii(StringUtil.getLTStringFromClob(rset.getClob("LT"))));

                    if (linkedTerms != null && !linkedTerms.equalsIgnoreCase("QQ")) {
                        ht.put(Keys.LINKED_TERMS, new XMLWrapper(Keys.LINKED_TERMS, linkedTerms));
                    }
                }

                EIDoc eiDoc = new EIDoc(did, ht, LinkedTermDetail.LINKEDTERM_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(LINKED_TERM_KEYS);
                list.add(eiDoc);
            }
        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {
            close(rset);
            close(stmt);
            close(con, broker);
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
            DatabaseConfig dconfig = DatabaseConfig.getInstance();
            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();
                // Common Fields

                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                did.setDatabase(dconfig.getDatabase("ept"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, EptDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, EptDocBuilder.PAT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, EptDocBuilder.PAT_TEXT_COPYRIGHT));

                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.substituteChars(rset.getString("TI"))));
                }

                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                List<String> lstAsg = null;
                List<String> lstInv = null;

                if (rset.getString("PAT_IN") != null) {
                    lstInv = convertString2List(StringUtil.substituteChars(rset.getString("PAT_IN")));
                }

                if (rset.getString("CS") != null) {
                    lstAsg = convertString2List(StringUtil.substituteChars(rset.getString("CS")));
                }

                if (rset.getString("PC") != null) {
                    rset.getString("PC");
                }

                // if (lstAsg != null && lstInv != null)
                // lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);

                // IN Inventor
                if (lstInv != null && lstInv.size() > 0) {
                    Contributors authors = new Contributors(Keys.AUTHORS, setContributors(lstInv, Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);
                }
                // AF- CS
                if (lstAsg != null && lstAsg.size() > 0) {
                    ht.put(Keys.PATEPTASSIGN, new XMLMultiWrapper(Keys.PATEPTASSIGN, setAssignees(lstAsg)));
                }

                if (rset.getString("PC") != null) {
                    ht.put(Keys.AUTH_CODE, new XMLWrapper(Keys.AUTH_CODE, rset.getString("PC")));

                }

                // add patent info
                if (rset.getString("PAT") != null) {
                    ht.put(Keys.PATENT_INFORMATION, new XMLWrapper(Keys.PATENT_INFORMATION, formatPriorityInfo(removeSemiColon(rset.getString("PAT")))));
                }

                // PY

                if (rset.getString("PY") != null) {
                    // 11/07 specs on pubdate
                    // String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("PD")));
                    ht.put(Keys.UPAT_PUBDATE, new XMLWrapper(Keys.UPAT_PUBDATE, rset.getString("PY")));
                }
                StringBuffer pubNum = new StringBuffer();
                // Publication Number
                if (rset.getString("PC") != null) {
                    pubNum.append(rset.getString("PC"));
                }

                if (rset.getString("PN") != null) {
                    pubNum.append(removeLeadingZeros(rset.getString("PN")));
                }

                if (pubNum.length() > 0)
                    ht.put(Keys.PATENT_NUMBER, new XMLWrapper(Keys.PATENT_NUMBER, pubNum.toString()));

                if (rset.getString("PN") != null) {
                    ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, removeLeadingZeros(rset.getString("PN"))));
                }
                // LA
                if ((rset.getString("LA") != null)) {
                    String language = rset.getString("LA");
                    if (language.length() > 1)
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.substituteChars(rset.getString("LA"))));
                }

                ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, "Patent"));

                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(false);
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
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }

    public List<Contributor> setContributors(List<String> contribs, Key key) {
        List<Contributor> list = new ArrayList<Contributor>();
        for (int i = 0; i < contribs.size(); i++) {
            String inventor = (String) contribs.get(i);

            list.add(new Contributor(key, inventor));
        }

        return list;
    }

    public String removeLeadingZeros(String sVal) {

        if (sVal == null) {
            return "";
        }

        char[] schars = sVal.toCharArray();
        int index = 0;
        for (; index < sVal.length(); index++) {

            if (schars[index] != '0') {
                break;
            }
        }

        return (index == 0) ? sVal : sVal.substring(index);
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

    private Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }

        return h;
    }

    public String removeSemiColon(String str) {

        String result = perl.substitute("s/,//g", str);

        return result;
    }

    public String removeSemiColon(String str, String ch, String rplCh) {

        String result = perl.substitute("s/" + ch + "/" + rplCh + "/g", str);

        return result;
    }

    public String cleanAbstr(String abstr) {
        String result = perl.substitute("s/(\\.\\|)/\\. /g", abstr);
        return result;
    }

    public String cleanBar(String str) {
        String result = perl.substitute("s/\\|/;/g", str);
        return result;
    }

    public String replaceBar(String str) {
        String result = perl.substitute("s/\\|/<\\/br>/g", str);

        return result;
    }

    public String formatCV(String cvs) {

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/;/", cvs);

        StringBuffer termBuffer = new StringBuffer();

        for (Iterator<String> iter = lstTokens.iterator(); iter.hasNext();) {

            String cv = (String) iter.next();

            if (cv != null && !cv.equals(""))
                termBuffer.append(cv);

            if (iter.hasNext() && !cv.equals(""))
                termBuffer.append(";");
        }

        return termBuffer.toString();
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

        List<String> lstTokens = new ArrayList<String>();

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
                        sbTemplate.append("</br>").append(term);
                } else {

                    if (!perl.match("/NUMBER OF TEMPLATE-GENERATED LINK TERMS:/", term) && !perl.match("/V[0-9]/i", term) && !term.startsWith(">")
                        && !perl.match("/S[0-9]/i", term)) {
                        // sbTemplate.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(term);
                        sbTemplate.append("</br>").append(term);

                    } else {
                        sbTemplate.append(term);
                    }

                }
                if (i > 0)
                    sbTemplate.append("|");
            }

            return sbTemplate.toString();
        } else {
            return template;
        }
    }

    /**
     * @return
     */
    private void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<EIDoc> loadXMLCitations(List<DocID> listOfDocIDs) throws DocumentBuilderException {
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

            DatabaseConfig dconfig = DatabaseConfig.getInstance();

            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                did.setDatabase(dconfig.getDatabase("ept"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, EptDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, EptDocBuilder.PAT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, EptDocBuilder.PAT_TEXT_COPYRIGHT));

                // AccessNum Derwent Number
                if (rset.getString("DN") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("DN")));
                }
                // Title
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.substituteChars(rset.getString("TI"))));
                }
                // Inventors
                List<String> lstAsg = null;
                List<String> lstInv = null;

                if (rset.getString("PAT_IN") != null) {
                    lstInv = convertString2List(StringUtil.substituteChars(rset.getString("PAT_IN")));
                }

                if (rset.getString("CS") != null) {
                    lstAsg = convertString2List(StringUtil.substituteChars(rset.getString("CS")));
                }

                if (rset.getString("PC") != null) {
                    rset.getString("PC");
                }

                if (lstAsg != null && lstInv != null)
                    lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);

                // IN Inventor
                if (lstInv != null && lstInv.size() > 0) {
                    Contributors authors = new Contributors(Keys.AUTHORS, setContributors(lstInv, Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);
                }

                if (lstAsg != null && lstAsg.size() > 0) {
                    ht.put(Keys.PATEPTASSIGN, new XMLMultiWrapper(Keys.PATEPTASSIGN, setAssignees(lstAsg)));
                }

                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                if (rset.getString("PD") != null) {
                    // 11/07 specs on pubdate - no foramting
                    // String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("PD")));
                    ht.put(Keys.UPAT_PUBDATE, new XMLWrapper(Keys.UPAT_PUBDATE, rset.getString("PD")));

                }

                // Language
                if ((rset.getString("LA") != null)) {
                    String language = rset.getString("LA");
                    if (language.length() > 1)
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.substituteChars(rset.getString("LA"))));
                }

                StringBuffer pubNum = new StringBuffer();

                // Publication Number
                if (rset.getString("PC") != null) {
                    pubNum.append(rset.getString("PC"));
                }

                if (rset.getString("PN") != null) {
                    pubNum.append(removeLeadingZeros(rset.getString("PN")));
                }

                if (pubNum.length() > 0)
                    ht.put(Keys.PATENT_NUMBER, new XMLWrapper(Keys.PATENT_NUMBER, pubNum.toString()));

                if (rset.getString("PN") != null) {
                    ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, removeLeadingZeros(rset.getString("PN"))));
                }

                // Application Information
                String appinfo = rset.getString("LL");
                if ((appinfo != null)) {
                    ht.put(Keys.PATAPP_INFO, new XMLWrapper(Keys.PATAPP_INFO, formatPriorityInfo(removeSemiColon(rset.getString("LL")))));
                }

                // add patent info
                if (rset.getString("PAT") != null) {
                    ht.put(Keys.PATENT_INFORMATION, new XMLWrapper(Keys.PATENT_INFORMATION, formatPriorityInfo(removeSemiColon(rset.getString("PAT")))));
                }

                // add pat priority info
                if (rset.getString("PRI") != null) {
                    ht.put(Keys.PRIORITY_INFORMATION,
                        new XMLWrapper(Keys.PRIORITY_INFORMATION, removeSemiColon(formatPriorityInfo(rset.getString("PRI")), ";", "|")));
                }
                // AB
                String abs = null;
                if ((abs = hasAbstract(rset.getClob("AB"))) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.substituteChars(abs)));
                }
                // IC
                if (rset.getString("IC") != null) {
                    String[] arrIpcs = IPCClassNormalizer.trimLeadingZeroFromSubClass(rset.getString("IC"));
                    ht.put(Keys.INTERNATCL_CODE, new Classifications(Keys.INTERNATCL_CODE, getIPCClssifications(arrIpcs)));

                }

                // CRN

                if (rset.getString("CRN") != null) {
                    ht.put(Keys.CAS_REGISTRY_CODES,
                        new XMLMultiWrapper2(Keys.CAS_REGISTRY_CODES, setCRC(StringUtil.substituteChars(removePoundSign(rset.getString("CRN"))))));
                }
                // CT
                CVSTermBuilder termBuilder = new CVSTermBuilder();
                if (rset.getString("CT") != null) {
                    String ct = StringUtil.replaceNullWithEmptyString(rset.getString("CT"));
                    String cv = termBuilder.getNonMajorTerms(ct);
                    String mh = termBuilder.getMajorTerms(ct);
                    StringBuffer cvBuffer = new StringBuffer();

                    String expandedMajorTerms = termBuilder.expandMajorTerms(mh);

                    String expandedMH = termBuilder.getMajorTerms(expandedMajorTerms);

                    String expandedCV1 = termBuilder.expandNonMajorTerms(cv);
                    String expandedCV2 = termBuilder.getNonMajorTerms(expandedMajorTerms);

                    if (!expandedCV1.equals(""))
                        cvBuffer.append(expandedCV1);
                    if (!expandedCV2.equals(""))
                        cvBuffer.append(";").append(expandedCV2);

                    String parsedCV = CVSTermBuilder.formatCT(cvBuffer.toString());
                    parsedCV = StringUtil.replaceNonAscii(parsedCV);
                    String parsedMH = CVSTermBuilder.formatCT(expandedMH);
                    parsedMH = StringUtil.replaceNonAscii(parsedMH);

                    StringBuffer nonMajorTerms = new StringBuffer();

                    nonMajorTerms.append(termBuilder.getStandardTerms(parsedCV));
                    nonMajorTerms.append(";").append(termBuilder.getNoRoleTerms(parsedCV));
                    nonMajorTerms.append(";").append(termBuilder.getReagentTerms(parsedCV));
                    nonMajorTerms.append(";").append(termBuilder.getProductTerms(parsedCV));

                    ht.put(EPT_CONTROLLED_TERMS,
                        new XMLMultiWrapper2(EPT_CONTROLLED_TERMS, setCVS(StringUtil.substituteChars(formatCV(nonMajorTerms.toString())))));

                    StringBuffer majorTerms = new StringBuffer();

                    majorTerms.append(termBuilder.removeRoleTerms(parsedMH));
                    majorTerms.append(";").append(termBuilder.getMajorNoRoleTerms(parsedMH));
                    majorTerms.append(";").append(termBuilder.getMajorReagentTerms(parsedMH));
                    majorTerms.append(";").append(termBuilder.getMajorProductTerms(parsedMH));

                    ht.put(EPT_MAJOR_TERMS, new XMLMultiWrapper2(EPT_MAJOR_TERMS, setCVS(StringUtil.substituteChars(formatCV(majorTerms.toString())))));

                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.XMLCITATION_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(true);
                eiDoc.setOutputKeys(ABSTRACT_KEYS);
                list.add(eiDoc);
            }

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (IOException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } catch (InfrastructureException e) {
            throw new DocumentBuilderException(e.getErrorCode(), e);
        } finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
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
