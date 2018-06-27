package org.ei.data.upt.runtime;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.common.AuthorStream;
import org.ei.common.upt.AssigneeFilter;
import org.ei.common.upt.ECLAClassNormalizer;
import org.ei.common.upt.IPC8Classification;
import org.ei.common.upt.IPC8Classifications;
import org.ei.common.upt.IPCClassNormalizer;
import org.ei.common.upt.KindCodeLookup;
import org.ei.common.upt.USPTOClassNormalizer;
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
import org.ei.domain.DocID;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.DocumentBuilderException;
import org.ei.domain.EIDoc;
import org.ei.domain.ElementDataMap;
import org.ei.domain.FullDoc;
import org.ei.domain.Key;
import org.ei.domain.Keys;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.domain.RIS;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.exception.InfrastructureException;
import org.ei.util.StringUtil;

public class UPTDocBuilder implements DocumentBuilder, Keys {
    public static String UPT_TEXT_COPYRIGHT = "Compilation and indexing terms, 2014 LexisNexis Univentio B.V.";
    public static String UPT_HTML_COPYRIGHT = "Compilation and indexing terms, &copy; 2014 LexisNexis Univentio B.V.";

    public static String PROVIDER = "Ei";
    public static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.KIND_DESCRIPTION, Keys.PATENT_NUMBER, Keys.TITLE, Keys.PUBLICATION_YEAR,
        Keys.NO_SO, Keys.PATASSIGN, Keys.AUTHORS, Keys.UPAT_PUBDATE, Keys.PROVIDER, Keys.KIND_CODE, Keys.REFCNT, Keys.CITCNT, Keys.AUTH_CODE, Keys.NPREFCNT,
        Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    public static final Key[] ABSTRACT_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.KIND_DESCRIPTION, Keys.PATENT_NUMBER, Keys.TITLE, Keys.PUBLICATION_YEAR,
        Keys.NO_SO, Keys.PATASSIGN, Keys.AUTHORS, Keys.UPAT_PUBDATE, Keys.PROVIDER, Keys.KIND_CODE, Keys.ABSTRACT, Keys.REFCNT, Keys.CITCNT, Keys.AUTH_CODE,
        Keys.NPREFCNT, Keys.INTERNATCL_CODE, Keys.INTERNATCL_CODE8, Keys.EUROPCL_CODE, Keys.USCL_CODE, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS, Keys.COPYRIGHT,
        Keys.COPYRIGHT_TEXT };

    public static final Key[] DETAILED_KEYS = { Keys.PATENT_NUMBER, Keys.PATNUM,
        Keys.AUTH_CODE,
        Keys.KIND_CODE,
        Keys.TITLE,
        // Keys.PUBLICATION_NUMBER,
        Keys.KIND_DESCRIPTION, Keys.INVENTORS, Keys.PATASSIGN, Keys.PRIMARY_EXAMINER, Keys.PAT_ATTORNEY, Keys.ASSISTANT_EXAMINER, Keys.UPAT_PUBDATE,
        Keys.PUBLICATION_YEAR, Keys.LANGUAGE, Keys.PATAPPNUM,

        Keys.PATAPPDATEX, Keys.PATAPPDATE, Keys.PATAPPNUMS, Keys.PATAPPNUMUS, Keys.PATFILDATE, Keys.PATAPPCOUNTRY, Keys.FURTHER_INT_PATCLASS,
        Keys.FURTHER_EUR_PATCLASS, Keys.ABSTRACT, Keys.INT_PATCLASSES, Keys.EUR_PATCLASSES, Keys.DESIGNATED_STATES, Keys.FIELD_OF_SEARCH, Keys.DOC_TYPE,
        Keys.PRIORITY_INFORMATION, Keys.INTERNATCL_CODE, Keys.INTERNATCL_CODE8, Keys.EUROPCL_CODE, Keys.USCL_CODE, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS,
        Keys.DOCID, Keys.DOI, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.REFCNT, Keys.CITCNT, Keys.NPREFCNT };
    private static final Key[] DETAILED_KEYS_APPLICATION = {
        // Keys.PATNUM,
        Keys.PATENT_NUMBER, Keys.PATAPPDATEX, Keys.PATAPPDATE, Keys.PATAPPNUM, Keys.PATAPPNUMS, Keys.PATAPPNUMUS,
        Keys.PATAPPCOUNTRY,
        Keys.AUTH_CODE,
        Keys.KIND_CODE,
        Keys.TITLE,
        // Keys.PUBLICATION_NUMBER,
        Keys.KIND_DESCRIPTION, Keys.INVENTORS, Keys.PATASSIGN, Keys.PRIMARY_EXAMINER, Keys.PAT_ATTORNEY, Keys.ASSISTANT_EXAMINER, Keys.UPAT_PUBDATE,
        Keys.PUBLICATION_YEAR, Keys.PATFILDATE, Keys.LANGUAGE, Keys.PATAPPNUM, Keys.FURTHER_INT_PATCLASS, Keys.FURTHER_EUR_PATCLASS, Keys.ABSTRACT,
        Keys.INT_PATCLASSES, Keys.EUR_PATCLASSES, Keys.DESIGNATED_STATES, Keys.FIELD_OF_SEARCH, Keys.DOC_TYPE, Keys.PRIORITY_INFORMATION, Keys.INTERNATCL_CODE,
        Keys.INTERNATCL_CODE8, Keys.EUROPCL_CODE, Keys.USCL_CODE, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS, Keys.DOCID, Keys.DOI, Keys.COPYRIGHT,
        Keys.COPYRIGHT_TEXT, Keys.REFCNT, Keys.CITCNT, Keys.NPREFCNT };

    private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_A1, Keys.RIS_AUS, Keys.RIS_T1, Keys.RIS_Y1, Keys.RIS_PY, Keys.RIS_VL, Keys.RIS_A2,
        Keys.RIS_IS, Keys.RIS_CY, Keys.RIS_PB, Keys.RIS_KW, Keys.RIS_Y2, Keys.RIS_M1, Keys.RIS_M2, Keys.RIS_M3, Keys.RIS_U3, Keys.RIS_U4, Keys.RIS_N2,
        Keys.RIS_N1, Keys.BIB_TY };

    private static final Key[] XML_KEYS = { Keys.PATASSIGN, Keys.NO_SO, Keys.PATENT_ISSUE_DATE, Keys.AUTHORS, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT,
        Keys.PATCOUNTRY, Keys.PATNUM, Keys.PATFILDATE, Keys.TITLE, Keys.DOCID };

    public static String US_CY = "US";
    public static String EP_CY = "EP";
    public static String US_PATENT_TEXT = "U.S. Patents";
    public static String EP_PATENT_TEXT = "European Patents";

    private static String DELIM;

    static {
        char d[] = { (char) 30 };
        DELIM = new String(d);
    }

    private Perl5Util perl = new Perl5Util();
    private Database database;

    private static String queryCitation = "select  UPT_MASTER.M_ID,REF_CNT, CIT_CNT,UPT_MASTER.DT,AB,AC,AE,AIC,AID,AIK,AIN,ASG,ASG_CST,ATY,ATY_CTRY,DAN,DS,ECC,ECL,ESC,FD,FEC,FIC,FS,ICC,INV,INV_CTRY,IPC,ISC,IPC8,IPC8_2, KC,KD,LA,LOAD_NUMBER,OAB,PD,PE,PI,PN,PN,PY,TI,UCL,XPB_DT , NP_CNT from UPT_MASTER where M_ID IN ";
    private static String queryXMLCitation = "select  UPT_MASTER.M_ID,UPT_MASTER.DT,AB,AC,AE,AIC,AID,AIK,AIN,ASG,ASG_CST,ATY,ATY_CTRY,DAN,DS,ECC,ECL,ESC,FD,FEC,FIC,FS,ICC,INV,INV_CTRY,IPC,ISC,IPC8,IPC8_2,KC,KD,LA,LOAD_NUMBER,OAB,PD,PE,PI,PN,PN,PY,TI,UCL,XPB_DT, NP_CNT  from UPT_MASTER where M_ID IN ";
    private static String queryAbstracts = "select  UPT_MASTER.M_ID,REF_CNT,CIT_CNT,UPT_MASTER.DT,AB,AC,AE,AIC,AID,AIK,AIN,ASG,ASG_CST,ATY,ATY_CTRY,DAN,DS,ECC,ECL,ESC,FD,FEC,FIC,FS,ICC,INV,INV_CTRY,IPC,ISC,IPC8,IPC8_2,KC,KD,LA,LOAD_NUMBER,OAB,PD,PE,PI,PN,PN,PY,TI,UCL,XPB_DT, NP_CNT  from UPT_MASTER where M_ID IN ";
    private static String queryDetailed = "select  UPT_MASTER.M_ID,REF_CNT,CIT_CNT,UPT_MASTER.DT,AB,AC,AE,AIC,AID,AIK,AIN,ASG,ASG_CST,ATY,ATY_CTRY,DAN,DS,ECC,ECL,ESC,FD,FEC,FIC,FS,ICC,INV,INV_CTRY,IPC,ISC,IPC8,IPC8_2, KC,KD,LA,LOAD_NUMBER,OAB,PD,PE,PI,PN,PN,PY,TI,UCL,XPB_DT, DUN, NP_CNT  from UPT_MASTER where M_ID IN ";
    private static String queryPreview = "select M_ID, AB from UPT_MASTER where M_ID IN ";

    public DocumentBuilder newInstance(Database database) {
        return new UPTDocBuilder(database);
    }

    public UPTDocBuilder() {
    }

    public UPTDocBuilder(Database database) {
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
                String abs = hasAbstract(rset.getClob("AB"));

                if (StringUtils.isNotBlank(abs)) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                list.add(eiDoc);

            }
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } catch (SQLException e) {
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
                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, UPTDocBuilder.PROVIDER));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, UPTDocBuilder.UPT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, UPTDocBuilder.UPT_TEXT_COPYRIGHT));

                String docType = rset.getString("DT");
                String patentNumber = rset.getString("PN");

                String authCode = rset.getString("AC");
                String kindCode = rset.getString("KC");

                if (docType.trim().equalsIgnoreCase("UA") || docType.trim().equalsIgnoreCase("EA")) {
                }

                StringBuffer sic = new StringBuffer();
                if (authCode != null) {
                    sic.append(authCode);
                }

                sic.append(removeLeadingZeros(patentNumber));

                ht.put(Keys.PATENT_NUMBER, new XMLWrapper(Keys.PATENT_NUMBER, sic.toString()));

                String dString = null;

                if (rset.getString("AC") != null) {
                    DatabaseConfig dconfig = DatabaseConfig.getInstance();
                    dString = rset.getString("AC");
                    if (dString.equalsIgnoreCase(US_CY)) {
                        did.setDatabase(dconfig.getDatabase("upa"));
                    } else if (dString.equalsIgnoreCase(EP_CY)) {
                        did.setDatabase(dconfig.getDatabase("eup"));
                    }

                    ht.put(Keys.AUTH_CODE, new XMLWrapper(Keys.AUTH_CODE, rset.getString("AC")));
                }

                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("TI")));
                } else if (rset.getString("KD") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("KD")));
                }

                if (rset.getString("KC") != null) {
                    ht.put(Keys.KIND_CODE, new XMLWrapper(Keys.KIND_CODE, replaceDelim(kindCode)));
                }

                if (rset.getString("KD") != null) {
                    ht.put(Keys.KIND_DESCRIPTION, new XMLWrapper(Keys.KIND_DESCRIPTION, replaceDelim(rset.getString("KD"))));
                } else {
                    String kd = KindCodeLookup.getTitle(rset.getString("AC"), rset.getString("KC"));
                    if (kd != null) {
                        ht.put(Keys.KIND_DESCRIPTION, new XMLWrapper(Keys.KIND_DESCRIPTION, kd));
                    }
                }

                if (rset.getString("PI") != null) {
                    ht.put(Keys.PRIORITY_INFORMATION,
                        new XMLWrapper(Keys.PRIORITY_INFORMATION, formatPriorityInfo(StringUtil.replaceNullWithEmptyString(rset.getString("PI")))));
                }

                List<String> lstAsg = null;
                List<String> lstInv = null;

                if (rset.getString("INV") != null) {
                    lstInv = convertString2List(rset.getString("INV"));
                }

                if (rset.getString("ASG") != null) {
                    lstAsg = convertString2List(rset.getString("ASG"));
                }

                /*
                 * Apply filters to remove Inventors from Assignees in US data and Assignees from Inventors in patent data.
                 */

                if (dString.equals(US_CY) && lstInv != null && lstAsg != null) {

                    lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);

                } else if (dString.equals(EP_CY) && lstInv != null && lstAsg != null) {
                    lstInv = AssigneeFilter.filterInventors(lstAsg, lstInv, false);
                }

                if (lstInv != null && lstInv.size() > 0) {
                    rset.getString("INV");
                    Contributors contribs = new Contributors(Keys.AUTHORS, setContributors(lstInv, Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, contribs);
                }

                if (lstAsg != null && lstAsg.size() > 0) {
                    ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, setAssignee(lstAsg)));
                }

                if ((rset.getString("PN") != null)) {
                    ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, rset.getString("PN")));

                }

                if (rset.getString("REF_CNT") != null) {
                    String refc = rset.getString("REF_CNT");
                    ht.put(Keys.REFCNT, new XMLWrapper(Keys.REFCNT, refc));
                }

                if (rset.getString("CIT_CNT") != null) {
                    String sCitCnt = rset.getString("CIT_CNT");
                    ht.put(Keys.CITCNT, new XMLWrapper(Keys.CITCNT, sCitCnt));
                }

                if ((rset.getInt("NP_CNT") != 0)) {
                    String mpCnt = String.valueOf(rset.getInt("NP_CNT"));
                    ht.put(Keys.NPREFCNT, new XMLWrapper(Keys.NPREFCNT, mpCnt));
                }

                if (rset.getString("PD") != null) {
                    String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("PD")));
                    if (strYR != null) {
                        ht.put(Keys.UPAT_PUBDATE, new XMLWrapper(Keys.UPAT_PUBDATE, strYR));
                    }
                }

                // LA
                if ((rset.getString("LA") != null) && (!rset.getString("LA").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, rset.getString("LA")));
                }

                // US patent classification - UCL

                if (rset.getString("UCL") != null) {
                    ht.put(Keys.USCL_CODE, new Classifications(Keys.USCL_CODE, getUSClssifications(setElementData(replaceDelim(rset.getString("UCL"))))));
                }
                char c = (char) 30;
                if (rset.getString("IPC8") != null) {
                    StringBuffer strIPC8 = new StringBuffer(rset.getString("IPC8"));
                    if (rset.getString("IPC8_2") != null) {
                        strIPC8.append(rset.getString("IPC8_2"));
                    }
                    ht.put(Keys.INTERNATCL_CODE8,
                        new IPC8Classifications(Keys.INTERNATCL_CODE8, getIPC8Clssifications(IPC8Classification.build(strIPC8.toString()))));
                } else {

                    StringBuffer ipc = new StringBuffer();
                    if (rset.getString("IPC") != null) {
                        ipc.append(rset.getString("IPC")).append(c);
                    }
                    if (rset.getString("FIC") != null) {
                        ipc.append(rset.getString("FIC"));
                    }

                    String ipcSubs[] = null;

                    if (rset.getString("ISC") != null) {
                        String isc = rset.getString("ISC");
                        ipcSubs = isc.split(DELIM);
                    }

                    if (ipc != null && ipc.length() > 0) {
                        ht.put(Keys.INTERNATCL_CODE,
                            new Classifications(Keys.INTERNATCL_CODE, getIPCClssifications(ipcSubs, setElementData(replaceDelim(ipc.toString())))));

                    }
                }

                StringBuffer ecl = new StringBuffer();
                if (rset.getString("ECL") != null) {
                    ecl.append(rset.getString("ECL")).append(c);
                }
                if (rset.getString("FEC") != null) {
                    ecl.append(rset.getString("FEC"));
                }
                if (ecl != null && ecl.length() > 0) {
                    ht.put(Keys.EUROPCL_CODE, new Classifications(Keys.EUROPCL_CODE,
                        getECLClssifications(setElementData(removeDup(replaceDelim(ecl.toString()))))));

                }

                String abs = null;
                if ((abs = hasAbstract(rset.getClob("AB"))) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
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

    public List<String> convertString2List(String sList) {

        if (sList == null)
            return new ArrayList<String>();

        List<String> lstVals = new ArrayList<String>();

        perl.split(lstVals, "/" + DELIM + "/", sList);

        return lstVals;
    }

    public String checkAbstract(String abs) {

        if (abs == null)
            return "";
        else {
            if (abs.equalsIgnoreCase("QQ"))
                return "";
        }
        abs = perl.substitute("s/^<p>//i", abs);
        abs = perl.substitute("s/<\\/p>//i", abs);
        return abs;
    }

    /**
     * This method basically takes list Of DocIDs as Parameter This list of Docids use buildINString() method to build the required IN clause String.This is
     * appended to sql String The resultSet so obtained by executing the sql,is iterated, to build Detailed EIDoc objects,which are then added to EIdocumentList
     *
     * @param listOfDocIDs
     * @return EIDocumentList
     * @throws InfrastructureException
     * @exception Exception
     */

    private List<EIDoc> loadDetailed(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

        List<EIDoc> list = new ArrayList<EIDoc>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        boolean isApplication = false;
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
                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, UPTDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, UPTDocBuilder.UPT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, UPTDocBuilder.UPT_TEXT_COPYRIGHT));

                String patentNumber = rset.getString("PN");

                String kindCode = rset.getString("KC");

                String authCode = rset.getString("AC");

                String dString = null;

                if (authCode != null) {
                    DatabaseConfig dconfig = DatabaseConfig.getInstance();

                    dString = authCode;
                    if (dString.equalsIgnoreCase(US_CY)) {
                        did.setDatabase(dconfig.getDatabase("upa"));
                    } else if (dString.equalsIgnoreCase(EP_CY)) {
                        did.setDatabase(dconfig.getDatabase("eup"));
                    }

                    ht.put(Keys.AUTH_CODE, new XMLWrapper(Keys.AUTH_CODE, replaceDelimSP(rset.getString("AC"))));
                }

                // Application fields

                if (rset.getString("XPB_DT") != null) {
                    ht.put(Keys.PATAPPDATEX, new XMLWrapper(Keys.PATAPPDATEX, rset.getString("XPB_DT")));
                }
                /*
                 * if (rset.getString("AID") != null) { ht.put(Keys.PATAPPDATE, new XMLWrapper(Keys.PATAPPDATE, formatDate(rset.getString("AID")))); }
                 */
                if (rset.getString("DAN") != null) {
                    ht.put(Keys.PATAPPNUMS, new XMLWrapper(Keys.PATAPPNUMS, rset.getString("DAN")));
                }
                if (rset.getString("DUN") != null) {
                    ht.put(Keys.PATAPPNUMUS, new XMLWrapper(Keys.PATAPPNUMUS, rset.getString("DUN")));
                }
                /*
                 * if (rset.getString("AIC") != null) { ht.put(Keys.PATAPPCOUNTRY, new XMLWrapper(Keys.PATAPPCOUNTRY, rset.getString("AIC"))); } if
                 * (rset.getString("AIN") != null) { ht.put(Keys.PATAPPNUM, new XMLWrapper(Keys.PATAPPNUM, rset.getString("AIN"))); }
                 */

                // suppress 'Source:' label
                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NOCONTROLLED_TERMS, ""));

                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, replaceAmp(replaceDelim(rset.getString("TI")))));
                } else if (rset.getString("KD") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, replaceAmp(replaceDelim(rset.getString("KD")))));
                }

                // kind code
                if (rset.getString("KC") != null) {
                    ht.put(Keys.KIND_CODE, new XMLWrapper(Keys.KIND_CODE, replaceDelim(kindCode)));
                }

                // patent number - publication number

                if (patentNumber != null) {
                    StringBuffer sic = new StringBuffer();
                    if (authCode != null) {
                        sic.append(authCode);
                    }
                    sic.append(removeLeadingZeros(patentNumber));

                    ht.put(Keys.PATENT_NUMBER, new XMLWrapper(Keys.PATENT_NUMBER, sic.toString()));
                }

                if (rset.getString("KD") != null) {
                    ht.put(Keys.KIND_DESCRIPTION, new XMLWrapper(Keys.KIND_DESCRIPTION, replaceDelim(rset.getString("KD"))));
                } else {
                    String kd = KindCodeLookup.getTitle(rset.getString("AC"), rset.getString("KC"));
                    if (kd != null) {
                        ht.put(Keys.KIND_DESCRIPTION, new XMLWrapper(Keys.KIND_DESCRIPTION, kd));
                    }
                }

                if (rset.getString("PI") != null) {
                    ht.put(
                        Keys.PRIORITY_INFORMATION,
                        new XMLMultiWrapper(Keys.PRIORITY_INFORMATION, setElementData(replaceDelim(formatPriorityInfo(StringUtil
                            .replaceNullWithEmptyString(rset.getString("PI")))))));
                }

                if (rset.getString("DT") != null) {

                    String docType = rset.getString("DT");

                    if (docType.equalsIgnoreCase("UA")) {
                        docType = "US Application";
                        isApplication = true;
                    } else if (docType.equalsIgnoreCase("UG")) {
                        docType = "US Grant";
                    } else if (docType.equalsIgnoreCase("EA")) {
                        docType = "European Application";
                        isApplication = true;
                    } else if (docType.equalsIgnoreCase("EG")) {
                        docType = "European Grant";
                    }

                    ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, docType));
                }

                if (rset.getString("PE") != null) {
                    Contributors contribs = new Contributors(Keys.PRIMARY_EXAMINER, setContributors(convertString2List(rset.getString("PE")),
                        Keys.PRIMARY_EXAMINER));
                    ht.put(Keys.PRIMARY_EXAMINER, contribs);
                }

                // Attorney - ATY
                if (rset.getString("ATY") != null) {
                    ht.put(Keys.PAT_ATTORNEY, new XMLWrapper(Keys.PAT_ATTORNEY, replaceDelim(replaceAmp(rset.getString("ATY")))));
                }

                List<String> lstAsg = null;
                List<String> lstInv = null;

                if (rset.getString("INV") != null) {
                    lstInv = convertString2List(rset.getString("INV"));
                }

                if (rset.getString("ASG") != null) {
                    lstAsg = convertString2List(rset.getString("ASG"));
                }

                /*
                 * Apply filters to remove Inventors from Assignees in US data and Assignees from Inventors in patent data.
                 */

                if (dString.equals(US_CY) && lstInv != null && lstAsg != null) {
                    lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);
                } else if (dString.equals(EP_CY) && lstInv != null && lstAsg != null) {
                    lstInv = AssigneeFilter.filterInventors(lstAsg, lstInv, false);
                }

                if (lstInv != null && lstInv.size() > 0) {
                    rset.getString("INV");
                    Contributors contribs = new Contributors(Keys.AUTHORS, setContributors(lstInv, Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, contribs);
                }

                if (lstAsg != null && lstAsg.size() > 0) {
                    ht.put(Keys.PATASSIGN, new XMLMultiWrapper(Keys.PATASSIGN, setAssignees(lstAsg)));
                }

                if (lstInv != null) {

                    if (rset.getString("INV_CTRY") != null) {
                        String invcountry = rset.getString("INV_CTRY");
                        String invs = rset.getString("INV");
                        Map<String, String> cmap = buildCountryMap(invs, invcountry);

                        Contributors contribs = new Contributors(Keys.INVENTORS, setContributors(lstInv, Keys.INVENTORS, cmap));
                        ht.put(Keys.INVENTORS, contribs);
                    } else {
                        Contributors contribs = new Contributors(Keys.INVENTORS, setContributors(lstInv, Keys.INVENTORS));
                        ht.put(Keys.INVENTORS, contribs);
                    }

                }

                // ae
                if (rset.getString("AE") != null) {
                    ht.put(Keys.ASSISTANT_EXAMINER, new XMLWrapper(Keys.ASSISTANT_EXAMINER, replaceAmp(replaceDelim(rset.getString("AE")))));
                }

                // PN
                if ((rset.getString("PN") != null)) {
                    ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, rset.getString("PN")));
                }

                if (rset.getString("REF_CNT") != null) {
                    String refc = rset.getString("REF_CNT");
                    ht.put(Keys.REFCNT, new XMLWrapper(Keys.REFCNT, refc));

                }

                if (rset.getString("CIT_CNT") != null) {
                    String sCitCnt = rset.getString("CIT_CNT");
                    ht.put(Keys.CITCNT, new XMLWrapper(Keys.CITCNT, sCitCnt));
                }

                if (rset.getInt("NP_CNT") != 0) {
                    String mpCnt = String.valueOf(rset.getInt("NP_CNT"));
                    ht.put(Keys.NPREFCNT, new XMLWrapper(Keys.NPREFCNT, mpCnt));
                }

                if (rset.getString("PD") != null) {
                    String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("PD")));
                    if (strYR != null) {
                        ht.put(Keys.UPAT_PUBDATE, new XMLWrapper(Keys.UPAT_PUBDATE, strYR));
                    }
                }

                // py
                if ((rset.getString("PY") != null)) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("PY"), perl));
                }
                // LA
                if ((rset.getString("LA") != null) && (!rset.getString("LA").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, replaceDelim(rset.getString("LA"))));
                }

                // US patent classification - UCL

                if (rset.getString("UCL") != null) {
                    ht.put(Keys.USCL_CODE, new Classifications(Keys.USCL_CODE, getUSClssifications(setElementData(replaceDelim(rset.getString("UCL"))))));
                }
                char c = (char) 30;
                if (rset.getString("IPC8") != null) {
                    StringBuffer strIPC8 = new StringBuffer(rset.getString("IPC8"));
                    if (rset.getString("IPC8_2") != null) {
                        strIPC8.append(rset.getString("IPC8_2"));
                    }
                    ht.put(Keys.INTERNATCL_CODE8,
                        new IPC8Classifications(Keys.INTERNATCL_CODE8, getIPC8Clssifications(IPC8Classification.build(strIPC8.toString()))));
                } else {
                    StringBuffer ipc = new StringBuffer();

                    if (rset.getString("IPC") != null) {
                        ipc.append(rset.getString("IPC")).append(c);
                    }
                    if (rset.getString("FIC") != null) {
                        ipc.append(rset.getString("FIC"));
                    }

                    String ipcSubs[] = null;

                    if (rset.getString("ISC") != null) {
                        String isc = rset.getString("ISC");
                        ipcSubs = isc.split(DELIM);
                    }

                    if (ipc != null && ipc.length() > 0) {
                        ht.put(Keys.INTERNATCL_CODE,
                            new Classifications(Keys.INTERNATCL_CODE, getIPCClssifications(ipcSubs, setElementData(replaceDelim(ipc.toString())))));

                    }
                }

                StringBuffer ecl = new StringBuffer();

                if (rset.getString("ECL") != null) {
                    ecl.append(rset.getString("ECL")).append(c);
                }
                if (rset.getString("FEC") != null) {
                    ecl.append(rset.getString("FEC"));
                }

                if (ecl != null && ecl.length() > 0) {
                    ht.put(Keys.EUROPCL_CODE, new Classifications(Keys.EUROPCL_CODE,
                        getECLClssifications(setElementData(removeDup(replaceDelim(ecl.toString()))))));

                }

                String abs = null;
                if ((abs = hasAbstract(rset.getClob("AB"))) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                if ((rset.getString("DS") != null)) {
                    ht.put(Keys.DESIGNATED_STATES, new XMLMultiWrapper(Keys.DESIGNATED_STATES, setElementData(replaceDelim(rset.getString("DS")))));
                }

                if ((rset.getString("FS") != null)) {
                    ht.put(Keys.FIELD_OF_SEARCH, new XMLMultiWrapper(Keys.FIELD_OF_SEARCH, setElementData(replaceDelim(rset.getString("FS")))));
                }

                // Domestic applicaton num
                if (rset.getString("DAN") != null) {
                    ht.put(Keys.DOMESTIC_APPNUM, new XMLWrapper(Keys.DOMESTIC_APPNUM, replaceDelim(rset.getString("DAN"))));
                }
                // Contracting states - ASG_CST
                if (rset.getString("ASG_CST") != null) {
                    ht.put(Keys.CONTRACTIN_STATES, new XMLWrapper(Keys.CONTRACTIN_STATES, replaceDelim(rset.getString("ASG_CST"))));
                }

                if ((rset.getString("FD") != null)) {
                    String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("FD")));
                    if (strYR != null) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, strYR));
                    }
                }

                if (rset.getString("PD") != null) {
                    String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("PD")));
                    if (strYR != null) {
                        ht.put(Keys.UPAT_PUBDATE, new XMLWrapper(Keys.UPAT_PUBDATE, strYR));
                    }
                }

                EIDoc eiDoc = new EIDoc(did, ht, Abstract.ABSTRACT_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(true);
                if (isApplication) {
                    eiDoc.setOutputKeys(DETAILED_KEYS_APPLICATION);
                } else {
                    eiDoc.setOutputKeys(DETAILED_KEYS);
                }
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
            String bibDocType = "article";
            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                // DT
                ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, "PAT"));
                // always article
                ht.put(Keys.BIB_TY, new XMLWrapper(Keys.BIB_TY, bibDocType));

                if (rset.getString("KD") != null) {
                    ht.put(Keys.RIS_M3, new XMLWrapper(Keys.RIS_M3, rset.getString("KD")));
                } else if (rset.getString("DT") != null) {

                    String docType = rset.getString("DT");

                    if (docType.equalsIgnoreCase("UA")) {
                        docType = "US Application";
                    } else if (docType.equalsIgnoreCase("UG")) {
                        docType = "US Grant";
                    } else if (docType.equalsIgnoreCase("EA")) {
                        docType = "European Application";
                    } else if (docType.equalsIgnoreCase("EG")) {
                        docType = "European Grant";
                    }
                    ht.put(Keys.RIS_M3, new XMLWrapper(Keys.RIS_M3, docType));
                }

                /*
                 * //LA
                 *
                 * if (rset.getString("LA") != null) { ht.put(Keys.RIS_LA, new XMLWrapper(Keys.RIS_LA,rset.getString("LA"))); }
                 */
                // TI

                ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, UPTDocBuilder.UPT_TEXT_COPYRIGHT));

                if (rset.getString("TI") != null) {
                    ht.put(Keys.RIS_T1, new XMLWrapper(Keys.RIS_T1, rset.getString("TI")));
                }

                List<String> lstAsg = null;
                List<String> lstInv = null;

                if (rset.getString("INV") != null) {
                    lstInv = convertString2List(rset.getString("INV"));
                }

                if (rset.getString("ASG") != null) {
                    lstAsg = convertString2List(rset.getString("ASG"));
                }

                /*
                 * Apply filters to remove Inventors from Assignees in US data and Assignees from Inventors in patent data.
                 */

                String dString = null;
                String authCode = rset.getString("AC");

                if (authCode != null) {
                    ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, authCode));
                    DatabaseConfig dconfig = DatabaseConfig.getInstance();

                    dString = authCode;
                    if (dString.equalsIgnoreCase(US_CY)) {
                        did.setDatabase(dconfig.getDatabase("upa"));
                    } else if (dString.equalsIgnoreCase(EP_CY)) {
                        did.setDatabase(dconfig.getDatabase("eup"));
                    }

                }

                if (dString.equals(US_CY) && lstInv != null && lstAsg != null) {
                    lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg);
                } else if (dString.equals(EP_CY) && lstInv != null && lstAsg != null) {
                    lstInv = AssigneeFilter.filterInventors(lstAsg, lstInv);
                }

                if (lstAsg != null && lstAsg.size() > 0) {
                    ht.put(Keys.RIS_A2, new XMLMultiWrapper(Keys.RIS_A2, setAssignees(lstAsg)));
                }

                if (lstInv != null) {

                    if (rset.getString("INV_CTRY") != null) {
                        String invcountry = rset.getString("INV_CTRY");
                        String invs = rset.getString("INV");
                        Map<String, String> cmap = buildCountryMap(invs, invcountry);

                        Contributors contribs = new Contributors(Keys.RIS_AUS, setContributors(lstInv, Keys.RIS_AUS, cmap));
                        ht.put(Keys.RIS_AUS, contribs);
                    } else {
                        Contributors contribs = new Contributors(Keys.RIS_AUS, setContributors(lstInv, Keys.RIS_AUS));
                        ht.put(Keys.RIS_AUS, contribs);
                    }

                }
                if (rset.getString("DAN") != null) {
                    ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, rset.getString("DAN")));
                }
                /*
                 * if (rset.getString("PE") != null) { ht.put(Keys.RIS_EDS, new Contributors(Keys.RIS_EDS ,setContributors(rset.getString("PE"),
                 * Keys.RIS_EDS))); }
                 */
                // U1 - pat number

                if (rset.getString("PN") != null) {
                    ht.put(Keys.RIS_IS, new XMLWrapper(Keys.RIS_IS, removeLeadingZeros(rset.getString("PN"))));
                }

                String abs = null;
                if ((abs = hasAbstract(rset.getClob("AB"))) != null) {
                    ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, abs));
                }

                if (rset.getString("PD") != null) {
                    String strYR = formatDateRIS(StringUtil.replaceNullWithEmptyString(rset.getString("PD")));
                    if (strYR != null) {
                        ht.put(Keys.RIS_Y1, new XMLWrapper(Keys.RIS_Y1, strYR));
                    }
                }

                if (rset.getString("FD") != null) {
                    String strYR = formatDateRIS(StringUtil.replaceNullWithEmptyString(rset.getString("FD")));
                    if (strYR != null) {
                        ht.put(Keys.RIS_Y2, new XMLWrapper(Keys.RIS_Y2, strYR));
                    }
                }

                // US patent classification - UCL

                if (rset.getString("UCL") != null) {
                    ht.put(Keys.RIS_M2, new XMLWrapper(Keys.RIS_M2, replaceDelim(rset.getString("UCL"))));
                }
                char c = (char) 30;
                if (rset.getString("IPC8") != null) {
                    StringBuffer strIPC8 = new StringBuffer(rset.getString("IPC8"));
                    if (rset.getString("IPC8_2") != null) {
                        strIPC8.append(rset.getString("IPC8_2"));
                    }
                    ht.put(Keys.RIS_M1, new XMLWrapper(Keys.RIS_M1, getIPC8Codes(IPC8Classification.build(strIPC8.toString()))));
                } else {
                    StringBuffer ipc = new StringBuffer();

                    if (rset.getString("IPC") != null) {
                        ipc.append(rset.getString("IPC")).append(c);
                    }
                    if (rset.getString("FIC") != null) {
                        ipc.append(rset.getString("FIC"));
                    }

                    if (ipc != null && ipc.length() > 0) {
                        ht.put(Keys.RIS_M1, new XMLWrapper(Keys.RIS_M1, replaceDelim(ipc.toString())));

                    }
                }

                StringBuffer ecl = new StringBuffer();

                if (rset.getString("ECL") != null) {
                    ecl.append(rset.getString("ECL")).append(c);
                }
                if (rset.getString("FEC") != null) {
                    ecl.append(rset.getString("FEC"));
                }

                if (ecl != null && ecl.length() > 0) {
                    ht.put(Keys.RIS_U3, new XMLWrapper(Keys.RIS_U3, (removeDup(replaceDelim(ecl.toString())))));
                }

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

                if (rset.getString("DT") != null) {

                    ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, rset.getString("DT")));
                }

                String dString = null;

                if (rset.getString("AC") != null) {
                    DatabaseConfig dconfig = DatabaseConfig.getInstance();

                    dString = rset.getString("AC");
                    if (dString.equalsIgnoreCase(US_CY)) {
                        did.setDatabase(dconfig.getDatabase("upa"));
                    } else if (dString.equalsIgnoreCase(EP_CY)) {
                        did.setDatabase(dconfig.getDatabase("eup"));
                    }

                    ht.put(Keys.AUTH_CODE, new XMLWrapper(Keys.AUTH_CODE, rset.getString("AC")));
                }

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, UPTDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, UPTDocBuilder.UPT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, UPTDocBuilder.UPT_TEXT_COPYRIGHT));

                String patentNumber = rset.getString("PN");
                String authCode = rset.getString("AC");
                String kindCode = rset.getString("KC");
                rset.getString("DT");

                StringBuffer sic = new StringBuffer();
                if (authCode != null) {
                    sic.append(authCode);
                }
                sic.append(removeLeadingZeros(patentNumber));

                ht.put(Keys.PATENT_NUMBER, new XMLWrapper(Keys.PATENT_NUMBER, sic.toString()));

                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, replaceAmp(rset.getString("TI"))));
                } else if (rset.getString("KD") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, replaceAmp(rset.getString("KD"))));
                }

                List<String> lstAsg = null;
                List<String> lstInv = null;

                if (rset.getString("INV") != null) {
                    lstInv = convertString2List(rset.getString("INV"));
                }

                if (rset.getString("ASG") != null) {
                    lstAsg = convertString2List(rset.getString("ASG"));
                }

                /*
                 * Apply filters to remove Inventors from Assignees in US data and Assignees from Inventors in patent data.
                 */

                if (dString.equals(US_CY) && lstInv != null && lstAsg != null) {
                    lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);
                } else if (dString.equals(EP_CY) && lstInv != null && lstAsg != null) {
                    lstInv = AssigneeFilter.filterInventors(lstAsg, lstInv, false);
                }

                if (lstInv != null && lstInv.size() > 0) {
                    rset.getString("INV");
                    Contributors contribs = new Contributors(Keys.AUTHORS, setContributors(lstInv, Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, contribs);
                }

                if (lstAsg != null && lstAsg.size() > 0) {
                    ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, setAssignee(lstAsg)));
                }

                if (rset.getString("REF_CNT") != null) {
                    String refc = rset.getString("REF_CNT");
                    ht.put(Keys.REFCNT, new XMLWrapper(Keys.REFCNT, refc));

                }

                if (rset.getString("CIT_CNT") != null) {
                    String sCitCnt = rset.getString("CIT_CNT");
                    ht.put(Keys.CITCNT, new XMLWrapper(Keys.CITCNT, sCitCnt));
                }

                if (rset.getInt("NP_CNT") != 0) {
                    String mpCnt = String.valueOf(rset.getInt("NP_CNT"));
                    ht.put(Keys.NPREFCNT, new XMLWrapper(Keys.NPREFCNT, mpCnt));
                }

                if ((rset.getString("PN") != null)) {
                    String sPnum = rset.getString("PN");
                    sPnum = removeLeadingZeros(sPnum);
                    ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, sPnum));
                }

                if (rset.getString("KC") != null) {
                    ht.put(Keys.KIND_CODE, new XMLWrapper(Keys.KIND_CODE, replaceDelim(kindCode)));
                }

                if (rset.getString("KD") != null) {
                    ht.put(Keys.KIND_DESCRIPTION, new XMLWrapper(Keys.KIND_DESCRIPTION, replaceDelim(rset.getString("KD"))));
                } else {
                    String kd = KindCodeLookup.getTitle(rset.getString("AC"), rset.getString("KC"));
                    if (kd != null) {
                        ht.put(Keys.KIND_DESCRIPTION, new XMLWrapper(Keys.KIND_DESCRIPTION, kd));
                    }
                }

                if (rset.getString("PD") != null) {
                    String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("PD")));
                    if (strYR != null) {
                        ht.put(Keys.UPAT_PUBDATE, new XMLWrapper(Keys.UPAT_PUBDATE, strYR));
                    }
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(CITATION_KEYS);
                list.add(eiDoc);

            }
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }

    public void close(ResultSet rset) {

        if (rset != null) {
            try {
                rset.close();
                rset = null;
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void close(Statement stmt) {

        if (stmt != null) {
            try {
                stmt.close();
                stmt = null;
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void close(Connection con, ConnectionBroker broker) {

        if (con != null) {
            try {
                broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
            } catch (ConnectionPoolException cpe) {
                cpe.printStackTrace();
            }
        }

    }

    /**
     * This method basically takes list Of DocIDs as Parameter This list of Docids use buildINString() method to build the required IN clause String.This is
     * appended to sql String The resultSet so obtained by executing the sql,is iterated, to build EIDoc objects,which are then added to EIdocumentList
     *
     * @param listOfDocIDs
     * @return EIDocumentList
     * @throws DocumentBuilderException
     * @exception Exception
     */

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
            rset = stmt.executeQuery(queryXMLCitation + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, UPTDocBuilder.UPT_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, UPTDocBuilder.UPT_TEXT_COPYRIGHT));

                // suppress 'Source:' label
                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("TI")));
                } else if (rset.getString("KD") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("KD")));
                }

                String dString = null;

                if (rset.getString("AC") != null) {
                    DatabaseConfig dconfig = DatabaseConfig.getInstance();

                    dString = rset.getString("AC");
                    if (dString.equalsIgnoreCase(US_CY)) {
                        did.setDatabase(dconfig.getDatabase("upa"));
                    } else if (dString.equalsIgnoreCase(EP_CY)) {
                        did.setDatabase(dconfig.getDatabase("eup"));
                    }
                }

                List<String> lstAsg = null;
                List<String> lstInv = null;

                if (rset.getString("INV") != null) {
                    lstInv = convertString2List(rset.getString("INV"));
                }

                if (rset.getString("ASG") != null) {
                    lstAsg = convertString2List(rset.getString("ASG"));
                }

                /*
                 * Apply filters to remove Inventors from Assignees in US data and Assignees from Inventors in patent data.
                 */

                if (dString.equals(US_CY) && lstInv != null && lstAsg != null) {

                    lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);

                } else if (dString.equals(EP_CY) && lstInv != null && lstAsg != null) {
                    lstInv = AssigneeFilter.filterInventors(lstAsg, lstInv, false);
                }

                if (lstInv != null && lstInv.size() > 0) {
                    rset.getString("INV");
                    Contributors contribs = new Contributors(Keys.AUTHORS, setContributors(lstInv, Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, contribs);
                }

                if (lstAsg != null) {
                    ht.put(Keys.PATASSIGN, new XMLMultiWrapper(Keys.PATASSIGN, setAssignees(lstAsg)));
                }

                if (lstInv != null) {

                    if (rset.getString("INV_CTRY") != null) {
                        String invcountry = rset.getString("INV_CTRY");
                        String invs = rset.getString("INV");
                        Map<String, String> cmap = buildCountryMap(invs, invcountry);

                        Contributors contribs = new Contributors(Keys.AUTHORS, setContributors(lstInv, Keys.AUTHORS, cmap));
                        ht.put(Keys.AUTHORS, contribs);
                    } else {
                        Contributors contribs = new Contributors(Keys.AUTHORS, setContributors(lstInv, Keys.AUTHORS));
                        ht.put(Keys.AUTHORS, contribs);
                    }

                }

                if ((rset.getString("PN") != null)) {
                    ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, rset.getString("PN")));
                }

                if ((rset.getString("FD") != null)) {
                    ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, rset.getString("FD")));
                }

                if (rset.getString("PD") != null) {
                    String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("PD")));
                    if (strYR != null) {
                        ht.put(Keys.PATENT_ISSUE_DATE, new XMLWrapper(Keys.PATENT_ISSUE_DATE, strYR));
                    }
                }
                /*
                 * if((rset.getString("PU") != null)) { ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY,
                 * StringUtil.replaceNullWithEmptyString(rset.getString("PU")))); }
                 */
                EIDoc eiDoc = new EIDoc(did, ht, Abstract.ABSTRACT_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(XML_KEYS);
                list.add(eiDoc);
            }

        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                    rset = null;
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
                } catch (SQLException sqle) {
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

    private String hasAbstract(Clob clob) throws SQLException {
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
            return abs;
        }
    }

    public String[] setArray(String s) {
        return s.split(DELIM);

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

    public String removeLeadingZeros(String sVal) {

        if (sVal == null) {
            return sVal;
        } else {
            return sVal.replaceFirst("^(D|PP|RE|T|H|X|RX|AI)?[0]+", "$1");
        }

        /*
         *
         * char[] schars = sVal.toCharArray(); int index = 0; for (; index < sVal.length(); index++) {
         *
         * if (schars[index] != '0') { break; } }
         *
         * return (index == 0) ? sVal : sVal.substring(index);
         */

    }

    private Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }

        return h;
    }

    public String formatAuthor(String s) {
        if (s == null)
            return "";
        s = perl.substitute("s/;/,/g", s);
        s = perl.substitute("s/&amp;/&/ig", s);
        return s;
    }

    protected DocumentBuilder getBuilder() {
        return new MultiDatabaseDocBuilder();
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

    public String replaceDelim(String sVal) {
        char c = (char) 30;
        int n = 0x1E;
        char x = (char) n;
        sVal = sVal.replace(c, ';');
        sVal = sVal.replace(x, ' ');
        return sVal;
    }

    private String formatPriorityInfo(String priorityInfo) {

        Character ch = new Character((char) 30);
        String delim = ch.toString();
        StringBuffer pi = new StringBuffer();
        StringTokenizer st = new StringTokenizer(priorityInfo, delim);

        while (st.hasMoreTokens()) {
            pi.append(formatPIDate(st.nextToken())).append(";");
        }

        return pi.toString();
    }

    private Map<String, String> buildCountryMap(String inv, String con) {
        HashMap<String, String> m = new HashMap<String, String>();
        String[] invs = inv.split(DELIM);
        String[] cons = con.split(DELIM);

        for (int i = 0; i < invs.length; i++) {
            if (cons.length < i + 1) {
                break;
            }

            m.put(invs[i], cons[i]);
        }

        return m;
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

    public String replaceDelimSP(String sVal) {
        char c = (char) 30;
        int n = 0x1E;
        char x = (char) n;
        sVal = sVal.replace(c, ' ');
        sVal = sVal.replace(x, ' ');
        return sVal;
    }

    public String replaceSC(String sVal) {

        if (perl.match("/;/i", sVal)) {
            sVal = perl.substitute("s/;/ /ig", sVal);
        }

        return sVal;
    }

    public String replaceAmp(String sVal) {

        if (perl.match("/\\&amp\\;/i", sVal)) {
            sVal = perl.substitute("s/&amp;/&/ig", sVal);
        }

        return sVal;
    }

    public List<Contributor> setContributors(List<String> contribs, Key key) {
        List<Contributor> list = new ArrayList<Contributor>();
        for (int i = 0; i < contribs.size(); i++) {
            String inventor = (String) contribs.get(i);
            if (perl.match("/&amp;/i", inventor)) {
                inventor = perl.substitute("s/&amp;/&/ig", inventor);
            }

            inventor = replaceSemiWithComma(inventor);
            list.add(new Contributor(key, inventor));
        }

        return list;
    }

    private List<Contributor> setContributors(List<String> inventors, Key key, Map<String, String> cmap) {
        List<Contributor> list = new ArrayList<Contributor>();

        for (int i = 0; i < inventors.size(); i++) {
            String inventor = (String) inventors.get(i);
            String country = (String) cmap.get(inventor);
            if (perl.match("/&amp;/i", inventor)) {
                inventor = perl.substitute("s/&amp;/&/ig", inventor);
            }

            inventor = replaceSemiWithComma(inventor);

            if (country != null) {
                list.add(new Contributor(key, inventor, country));
            } else {
                list.add(new Contributor(key, inventor));
            }
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
                assignee = replaceAmp(assignee);
                assignee = replaceSemiWithComma(assignee);
                assignees.add(assignee);
                dupMap.put(upAs, "");
            }

        }

        return (String[]) assignees.toArray(new String[assignees.size()]);
    }

    private String setAssignee(List<String> a) {
        StringBuffer buf = new StringBuffer();
        HashMap<String, String> dupMap = new HashMap<String, String>();

        for (int i = 0; i < a.size(); i++) {
            String assignee = (String) a.get(i);
            String upAs = assignee.toUpperCase().trim();
            if (!dupMap.containsKey(upAs)) {
                assignee = replaceAmp(assignee);
                assignee = replaceSemiWithComma(assignee);
                if (i > 0) {
                    buf.append("; ");
                }
                buf.append(assignee);
                dupMap.put(upAs, "");
            }
        }

        return buf.toString();
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

    public String removeDup(String codes) {
        StringTokenizer tok = new StringTokenizer(codes, ";");
        StringBuffer result = new StringBuffer();
        Map.Entry entry = null;

        LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();

        while (tok.hasMoreTokens()) {
            lhm.put(tok.nextToken(), "");
        }

        Iterator<?> itr = lhm.entrySet().iterator();

        while (itr.hasNext()) {
            entry = (Map.Entry) itr.next();
            result.append(entry.getKey()).append(";");
        }

        return result.toString();
    }

    public Classification[] getIPCClssifications(String[] subs, String[] classcodes) throws InfrastructureException, IOException {
        Classification[] cls = new Classification[classcodes.length];
        ClassNodeManager mn = ClassNodeManager.getInstance();

        for (int i = 0; i < classcodes.length; i++) {
            classcodes[i] = IPCClassNormalizer.trimLeadingZeroFromSubClass(classcodes[i], subs);
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

    public List<IPC8Classification> getIPC8Clssifications(List<IPC8Classification> classcodes) throws InfrastructureException, IOException {

        ClassNodeManager mn = ClassNodeManager.getInstance();

        for (int i = 0; i < classcodes.size(); i++) {

            String code = IPCClassNormalizer.normalize(((IPC8Classification) classcodes.get(i)).getCode());
            String title = mn.seekIPC(code);
            if (title == null) {
                title = "";
            }
            ((IPC8Classification) classcodes.get(i)).setClassTitle(title);

        }

        return classcodes;
    }

    public String getIPC8Codes(List<?> classcodes) {

        StringBuffer sbCodes = new StringBuffer();
        for (int i = 0; i < classcodes.size(); i++) {

            sbCodes.append(((IPC8Classification) classcodes.get(i)).getCode());
            if (!(i == (classcodes.size() - 1)))
                sbCodes.append(";");
        }

        return sbCodes.toString();
    }

    public Classification[] getECLClssifications(String[] classcodes) throws InfrastructureException, IOException {
        Classification[] cls = new Classification[classcodes.length];
        ClassNodeManager mn = ClassNodeManager.getInstance();
        ECLAClassNormalizer enorm = new ECLAClassNormalizer();

        for (int i = 0; i < classcodes.length; i++) {
            String code = ECLAClassNormalizer.normalize(classcodes[i]);
            ClassificationID cid = new ClassificationID(code, this.database);
            Classification cl = new Classification(Keys.EUROPCL_CODE, cid);
            String title = mn.seekECLA(code);
            if (title == null) {
                title = "";
            }
            cl.setClassTitle(title);
            cls[i] = cl;
        }

        return cls;
    }

    public Classification[] getUSClssifications(String[] classcodes) throws IOException, InfrastructureException {

        Classification[] cls = new Classification[classcodes.length];
        ClassNodeManager mn = ClassNodeManager.getInstance();

        for (int i = 0; i < classcodes.length; i++) {
            String code = USPTOClassNormalizer.normalize(classcodes[i]);
            ClassificationID cid = new ClassificationID(code, this.database);
            Classification cl = new Classification(Keys.USCL_CODE, cid);
            String title = mn.seekUS(code);
            if (title == null) {
                title = "";
            }
            cl.setClassTitle(title);
            cls[i] = cl;
        }

        return cls;
    }

    public String formatDateRIS(String date) {
        StringBuffer result = null;
        if ((date != null) && (date.length() > 5)) {
            result = new StringBuffer();
            result.append(date.substring(0, 4));
            result.append("/");
            result.append(date.substring(4, 6));
            result.append("/");
            result.append(date.substring(6));

            return result.toString();
        } else {
            return date;
        }
    }

    public String formatDate(String date) {
        StringBuffer result = null;

        try {
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
        } catch (Exception e) {
            return date;
        }
    }

    // public String trimZeros(String sVal) {
    //
    //
    // if (sVal == null)
    // {
    // return sVal;
    // }
    //
    // char[] schars = sVal.toCharArray();
    // int index = 0;
    // for (; index < sVal.length(); index++)
    // {
    //
    // if (schars[index] != '0')
    // {
    // break;
    // }
    // }
    //
    // return (index == 0) ? sVal : sVal.substring(index);
    // }

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

    public Key[] getDetailedKeysApplication() {
        return DETAILED_KEYS_APPLICATION;
    }
}
