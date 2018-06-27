package org.ei.data.georef.runtime;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.domain.Abstract;
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
import org.ei.domain.RIS;
import org.ei.domain.XMLWrapper;
import org.ei.util.StringUtil;
import org.ei.common.georef.*;
import org.ei.common.Constants;

public class GRFDocBuilder implements DocumentBuilder {
    //public static final int MINIMUM_ABSTRACT_LENGTH = 100;

    public static final String AUDELIMITER = new String(new char[] { 30 });
    public static final String IDDELIMITER = new String(new char[] { 31 });
    public static final String GROUPDELIMITER = new String(new char[] { 02 });
    //public static String GRF_TEXT_COPYRIGHT = "GeoRef, Copyright 2014, American Geological Institute.";
    //public static String GRF_HTML_COPYRIGHT = "GeoRef, Copyright &copy; 2014, American Geological Institute.";
    //public static String PROVIDER_TEXT = "American Geological Institute";

    private static String queryPreview = "SELECT M_ID, ABSTRACT FROM GEOREF_MASTER WHERE M_ID IN ";

    public DocumentBuilder newInstance(Database database) {
        return new GRFDocBuilder(database);
    }

    public GRFDocBuilder() {
    }

    public GRFDocBuilder(Database database) {
    }

    /**
     * This method takes a list of DocID objects and dataFormat and returns a List of EIDoc Objects based on a particular dataformat @ param listOfDocIDs @
     * param dataFormat @ return List --list of EIDoc's @ exception DocumentBuilderException
     */
    public List<EIDoc> buildPage(List<DocID> listOfDocIDs, String dataFormat) throws DocumentBuilderException {
        List<EIDoc> l = null;
        if (dataFormat != null) {
            DocumentView format = null;
            if (dataFormat.equalsIgnoreCase(Citation.CITATION_FORMAT)) {
                format = new CitationView();
            } else if (dataFormat.equalsIgnoreCase(Abstract.ABSTRACT_FORMAT)) {
                format = new AbstractView();
            } else if (dataFormat.equalsIgnoreCase(FullDoc.FULLDOC_FORMAT)) {
                format = new DetailedView();
            } else if (dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT)) {
                format = new RISView();
            } else if (dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT)) {
                format = new XMLCitationView();
            } else if (Citation.CITATION_PREVIEW.equals(dataFormat)) {
                l = loadPreviewData(listOfDocIDs);
            }

            if (format != null) {
                l = loadDocument(listOfDocIDs, format);
            }
        }

        return l;
    }

    private List<EIDoc> loadPreviewData(List<?> listOfDocIDs) throws DocumentBuilderException {
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
                ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.getStringFromClob(rset.getClob("ABSTRACT"))));

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
                        e1.printStackTrace();;
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
        }
        return list;
    }

    /*
    *
    */
    private List<EIDoc> loadDocument(List<?> listOfDocIDs, DocumentView viewformat) throws DocumentBuilderException {
        List<EIDoc> list = new ArrayList<EIDoc>();

        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;

        Map<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        String INString = buildINString(listOfDocIDs);

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(viewformat.getQuery() + INString);

            while (rset.next()) {
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                list.add(viewformat.buildDocument(rset, did));
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

    /*
     * This method builds the IN String from list of docId objects. The select query will get the result set in a reverse way So in order to get in correct
     * order we are doing a reverse example of return String--(23,22,1,12...so on);
     *
     * @param listOfDocIDs
     *
     * @return String
     */
    private String buildINString(List<?> listOfDocIDs) {
        StringBuffer sQuery = new StringBuffer("(");
        Collections.reverse(listOfDocIDs);
        Iterator<?> itrdocids = listOfDocIDs.iterator();
        while (itrdocids.hasNext()) {
            DocID doc = (DocID) itrdocids.next();
            sQuery.append("'").append(doc.getDocID()).append("'");
            if (itrdocids.hasNext()) {
                sQuery.append(",");
            }
        }
        sQuery.append(")");
        return sQuery.toString();
    }

    private Map<String, DocID> getDocIDTable(List<?> listOfDocIDs) {
        Map<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }
        return h;
    }

    @Override
    public Key[] getCitationKeys() {
        return (new CitationView()).getKeys();
    }

    @Override
    public Key[] getAbstractKeys() {
        return (new AbstractView()).getKeys();
    }

    @Override
    public Key[] getDetailedKeys() {
        return (new DetailedView()).getKeys();
    }

}
