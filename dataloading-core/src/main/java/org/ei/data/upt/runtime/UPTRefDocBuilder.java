package org.ei.data.upt.runtime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.domain.Citation;
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
import org.ei.domain.XMLWrapper;
import org.ei.xml.Entity;
//import org.ei.ev.driver.upt.UPTDatabase;

/**
 * @author Tsolovye
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * This class is the implementation of DocumentBuilder Basically this class is responsible for building a List of EIDocs from a List of DocIds.The input ie list
 * of docids come from CPXSearchControl and
 * 
 */
public class UPTRefDocBuilder implements DocumentBuilder {
    public static String UPTRef_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String UPTRef_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER = "Ei";

    public static String US_CY = "US";
    public static String EP_CY = "EP";
    public static String US_PATENT_TEXT = "U.S. Patents";
    public static String EP_PATENT_TEXT = "European Patents";

    private static final Key[] CITATION_KEYS = { Keys.PATENT_NUMBER1, Keys.DOCID, Keys.NO_SO };
    private static final Key[] DETAILED_KEYS = { Keys.PATENT_NUMBER1, Keys.DOCID };

    private Perl5Util perl = new Perl5Util();
    private Database database;

    private static String qryPatentRefsLinked = "select R.REF_MID, R.CIT_MID as CID, R.CIT_PN as PN, R.CIT_PK, R.CIT_CY CY, U.PD as PDATE FROM PATENT_REFS R, UPT_MASTER U WHERE R.PRT_MID = ? AND R.CIT_MID is not NULL and R.CIT_MID = U.M_ID";
    private static String qryPatentRefsUnLinked = "select REF_MID, CIT_MID, CIT_PN, CIT_PK, CIT_CY FROM PATENT_REFS WHERE PRT_MID = ? AND CIT_MID is null";

    private static String qryCitations = "select REF_MID, CIT_MID, CIT_PN, CIT_PK, CIT_CY FROM PATENT_REFS WHERE REF_MID IN ";
    private static String qryNonPatentRefs = "select M_ID,REF_RAW,CPX,INS FROM NON_PAT_REFS WHERE PRT_MID = ?";

    public DocumentBuilder newInstance(Database database) {
        return new UPTRefDocBuilder(database);
    }

    public UPTRefDocBuilder(Database database) {
        this.database = database;
    }

    /**
     * This method takes a list of DocID objects and dataFormat and returns a List of EIDoc Objects based on a particular dataformat @ param listOfDocIDs @
     * param dataFormat @ return List --list of EIDoc's @ exception DocumentBuilderException
     */
    public List<EIDoc> buildPage(List<DocID> listOfDocIDs, String dataFormat) throws DocumentBuilderException {
        List<EIDoc> l = null;

        try {
            l = loadCitations(listOfDocIDs, dataFormat);
        } catch (Exception e) {
            throw new DocumentBuilderException(e);
        }

        return l;
    }

    public List<NonPatentRef> buildNonPatentRefs(String parentID) throws DocumentBuilderException {
        List<NonPatentRef> list = null;

        try {
            list = loadNonPatentRefs(parentID);
        } catch (Exception e) {
            throw new DocumentBuilderException(e);
        }

        return list;
    }

    private List<NonPatentRef> loadNonPatentRefs(String parentID) throws Exception {

        List<NonPatentRef> list = new ArrayList<NonPatentRef>();
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.prepareStatement(qryNonPatentRefs);

            stmt.setString(1, parentID);
            rset = stmt.executeQuery();
            int index = 0;

            while (rset.next()) {
                ++index;
                String source = rset.getString("ref_raw");
                String cpxDocId = rset.getString("cpx");
                String insDocId = rset.getString("ins");

                NonPatentRef npRef = new NonPatentRef();

                npRef.setIndex(index);

                npRef.setSource(replaceAmpChar(Entity.prepareString(replaceAmpEntity(source))));

                if (cpxDocId != null)
                    npRef.setCpxDocId(cpxDocId);

                if (insDocId != null)
                    npRef.setInsDocId(insDocId);

                list.add(npRef);
            }
        } finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }

    public String replaceAmpEntity(String sVal) {
        if (sVal == null)
            return "";

        if (perl.match("/&amp;/i", sVal)) {
            sVal = perl.substitute("s/&amp;/&/ig", sVal);
        }

        return sVal;
    }

    public String replaceAmpChar(String sVal) {
        if (sVal == null)
            return "";

        if (perl.match("/&/i", sVal)) {
            sVal = perl.substitute("s/&/&amp;/ig", sVal);
        }

        return sVal;
    }

    private List<EIDoc> loadCitations(List<DocID> lstOfDocIDs, String dataFormat) throws Exception {

        List<EIDoc> list = new ArrayList<EIDoc>();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            String inList = buildINString(lstOfDocIDs);
            stmt = con.createStatement();
            rset = stmt.executeQuery(qryCitations + inList);

            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();
                String refMid = rset.getString("ref_mid");

                DocID did = new DocID(refMid, this.database);
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, UPTRefDocBuilder.PROVIDER));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, UPTRefDocBuilder.UPTRef_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, UPTDocBuilder.UPT_TEXT_COPYRIGHT));
                ht.put(Keys.NOCONTROLLED_TERMS, new XMLWrapper(Keys.NOCONTROLLED_TERMS, ""));
                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NOCONTROLLED_TERMS, ""));

                if ((rset.getString("CIT_PN") != null)) {
                    StringBuffer sbPnum = new StringBuffer();
                    String sPnum = rset.getString("CIT_PN");
                    String authCode = "";

                    if (rset.getString("CIT_CY") != null) {
                        authCode = rset.getString("CIT_CY");
                    }

                    sbPnum.append(authCode).append(sPnum);

                    ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, sbPnum.toString()));

                    ht.put(Keys.PATENT_NUMBER1, new XMLWrapper(Keys.PATENT_NUMBER1, sbPnum.toString()));

                    ht.put(Keys.PATENT_NUMBER, new XMLWrapper(Keys.PATENT_NUMBER, sPnum));
                }

                if ((rset.getString("CIT_CY") != null))
                    ht.put(Keys.AUTH_CODE, new XMLWrapper(Keys.AUTH_CODE, rset.getString("CIT_CY")));
                if ((rset.getString("CIT_PK") != null)) {
                    ht.put(Keys.KIND_CODE, new XMLWrapper(Keys.KIND_CODE, rset.getString("CIT_PK")));
                }

                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.exportLabels(true);
                System.out.println(dataFormat);
                if (dataFormat.equals(FullDoc.FULLDOC_FORMAT)) {
                    eiDoc.setOutputKeys(DETAILED_KEYS);

                } else {
                    eiDoc.setOutputKeys(CITATION_KEYS);
                }

                list.add(eiDoc);
            }
        } finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }

    /*
     * public List loadPatentRefs(List docIDs) throws Exception { ArrayList buildFromBaseTable = new ArrayList(); ArrayList buildFromRefsTable = new
     * ArrayList();
     * 
     * for(int i=0; i<docIDs.size(); i++) {
     * 
     * DocID docID = (DocID)docIDs.get(i); String did = docID.getDocID();
     * 
     * ElementDataMap ht = new ElementDataMap();
     * 
     * if (did.indexOf("upt") == 0) { buildFromBaseTable.add(docID); } else if(did.indexOf("ref") == 0) { buildFromRefsTable.add(docID); } }
     * 
     * ArrayList finishedList = new ArrayList();
     * 
     * if(buildFromBaseTable.size() > 0) { List fromBaseTable = buildFromBaseTable(buildFromBaseTable); finishedList.addAll(fromBaseTable); }
     * 
     * if(buildFromRefsTable.size() > 0) { List fromRefsTable = buildFromRefsTable(buildFromRefsTable); finishedList.addAll(fromRefsTable); }
     * 
     * Collections.sort(finishedList, new EIDocComparator());
     * 
     * return finishedList; }
     * 
     * public List buildFromBaseTable(List docIDs) throws Exception { DatabaseConfig config = DatabaseConfig.getInstance(); Database d =
     * config.getDatabase("upt"); DocumentBuilder patentBuilder = d.newBuilderInstance(); return patentBuilder.buildPage(docIDs, Citation.CITATION_FORMAT); }
     * 
     * private List buildFromRefsTable(List docIDs) throws Exception { return loadCitations(docIDs); }
     * 
     * class EIDocComparator implements Comparator { public int compare(Object o1, Object o2) { EIDoc a1 = (EIDoc) o1; EIDoc a2 = (EIDoc) o2; int index1 =
     * a1.getDocID().getHitIndex(); int index2 = a2.getDocID().getHitIndex();
     * 
     * if (index1 > index2) return 1; else if (index1 < index2) return -1; else return 0;
     * 
     * } }
     */

    public RefPager getRefPager(String parentID, int pageSize, String sessionID) throws Exception {

        PatRefQuery refQuery = new PatRefQuery();
        List<PatWrapper> linked = getLinkedRefIDs(parentID, refQuery);
        List<PatWrapper> unlinked = getUnLinkedRefIDs(parentID);
        linked.addAll(unlinked);
        Collections.sort(linked, new PubDateComparator());
        RefPager refPager = new RefPager(linked, pageSize, refQuery, sessionID);
        return refPager;
    }

    class PubDateComparator implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            PatWrapper w1 = (PatWrapper) o1;
            PatWrapper w2 = (PatWrapper) o2;

            if (w1.pdate == w2.pdate) {
                return 0;
            } else if (w1.pdate < w2.pdate) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private List<PatWrapper> getUnLinkedRefIDs(String parentID) throws Exception {
        List<PatWrapper> list = new ArrayList<PatWrapper>();
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.prepareStatement(qryPatentRefsUnLinked);

            stmt.setString(1, parentID);
            rset = stmt.executeQuery();
            while (rset.next()) {
                String refMid = rset.getString("ref_mid");
                String citMid = rset.getString("cit_mid");
                DocID docId = new DocID(++count, refMid, this.database);
                PatWrapper w = new PatWrapper();
                w.did = docId;
                w.pdate = -1;
                list.add(w);
            }
        } finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return list;
    }

    private List<PatWrapper> getLinkedRefIDs(String parentID, PatRefQuery query) throws Exception {
        List<PatWrapper> list = new ArrayList<PatWrapper>();
        int count = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        Database dbase = dbConfig.getDatabase("upt");
        List<PatWrapper> citMids = new ArrayList<PatWrapper>();

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.prepareStatement(qryPatentRefsLinked);
            stmt.setString(1, parentID);
            rset = stmt.executeQuery();
            while (rset.next()) {
                String citMid = rset.getString("cid");
                String pdate = rset.getString("pdate");
                String cy = rset.getString("cy");
                String pn = rset.getString("pn");
                query.addPat(cy, pn);
                int ipdate = -1;
                try {
                    ipdate = Integer.parseInt(pdate);
                } catch (Exception e) {
                    ipdate = -1;
                }

                DocID cDocId = new DocID(++count, citMid, dbase);
                PatWrapper w = new PatWrapper();
                w.did = cDocId;
                w.pdate = ipdate;
                citMids.add(w);
            }
        } finally {
            close(rset);
            close(stmt);
            close(con, broker);
        }

        return citMids;
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

    private Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }

        return h;
    }

    protected DocumentBuilder getBuilder() {
        return new MultiDatabaseDocBuilder();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List getPrevNextDocIds(String parentID, int index) throws Exception {

        PatRefQuery refQuery = new PatRefQuery();
        List linked = getLinkedRefIDs(parentID, refQuery);
        List unlinked = getUnLinkedRefIDs(parentID);
        linked.addAll(unlinked);
        Collections.sort(linked, new PubDateComparator());

        List retLinked = new ArrayList(2);
        boolean prevIndex = false;
        boolean nextIndex = false;

        for (int i = 0; i < linked.size(); i++) {
            // System.out.println("herere--> "+i);
            PatWrapper w1 = (PatWrapper) linked.get(i);
            // System.out.println("getting data from wrapper for --> "+i);
            DocID d = (DocID) w1.did;
            // System.out.println("herere56456456");
            if (linked.size() > 2) {
                // System.out.println("herere fgfdgfdgdf");
                if (index == 0) {
                    // System.out.println("herergfdgdfgdfg5466666666666e");
                    retLinked.add(-1 + "");
                } else {
                    // System.out.println("herere 99999999999999");
                    if (d.getHitIndex() == index - 1 && !prevIndex) {
                        // System.out.println("herere0000000000000000000");
                        retLinked.add(d.getDocID());
                        prevIndex = true;
                    }

                }

                if (index == linked.size()) {
                    // System.out.println("herere 5555555555555");
                    retLinked.add(-1 + "");
                } else {
                    // System.out.println("herere 3333333333333");
                    if (d.getHitIndex() == index + 1 && !nextIndex) {
                        // System.out.println("herere 2222222222222222");
                        retLinked.add(d.getDocID());
                        nextIndex = true;
                    }

                }
            } else {
                // System.out.println("herere 00000000000000003333333333");
                retLinked.add(-1 + "");
                retLinked.add(-1 + "");

            }
            if (!prevIndex) {
                // System.out.println("herere 88888888888888");
                retLinked.add(-1 + "");
            }
            if (!nextIndex) {
                // System.out.println("herere 1111111111111111");
                retLinked.add(-1 + "");
            }
        }
        // System.out.println("before end");
        // System.out.println("end-->"+retLinked.size());
        return retLinked;
    }

    @Override
    public Key[] getCitationKeys() {
        return CITATION_KEYS;
    }

    @Override
    public Key[] getAbstractKeys() {
        return DETAILED_KEYS;
    }

    @Override
    public Key[] getDetailedKeys() {
        return DETAILED_KEYS;
    }

}
