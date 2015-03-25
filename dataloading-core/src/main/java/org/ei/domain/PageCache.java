package org.ei.domain;

// general imports
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * This class builds DocID List for the requested page and the no of document to display on the page . In this class we query the database table(CHEM_PI_INFO)
 * based on sessionid and pageno we get a xmlString (XML format).Then this xmlstring is parsed to get DocID objects.
 **/

// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class PageCache {

    private String sessionID;
    // private static int timeHolder = 1;

    /**
     * Constructor which takes session id as argument.
     */

    public PageCache(String sSessionID) {
        sessionID = sSessionID;
    }

    /**
     * This method basically queries the database table(CHEM_PI_INFO) based on sessionid and pageNo to return the clob(xml format) The xml so returned is parsed
     * by buildDocIdsFromXMLString() to get the DocID list.
     * 
     * @param : int(page no)
     * @return : List of DocID's in that page.
     */

    public List<DocID> getPage(int pageNo, String searchID) throws InfrastructureException {

        List<DocID> docIDList = null;
        ConnectionBroker broker = null;
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        StringBuffer queryString = new StringBuffer();
        String lString = null;
        try {

            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            String line = "";
            stmt = con.createStatement();
            queryString.append(" SEARCH_ID='").append(searchID).append("' and PAGE_NO=").append(pageNo);
            String query = "select PI_PAGE_DATA from PAGE_CACHE where " + queryString;
            rs = stmt.executeQuery(query);

            if (rs.next()) {

                Clob clob = rs.getClob("PI_PAGE_DATA");
                if (clob != null) {
                    lString = clob.getSubString(1, (int) clob.length());
                }
                docIDList = buildDocIDsFromString(lString);
            }
        } catch (ConnectionPoolException e) {
            throw new InfrastructureException(SystemErrorCodes.PAGE_CACHE_ERROR, e);
        } catch (NoConnectionAvailableException e) {
            throw new InfrastructureException(SystemErrorCodes.PAGE_CACHE_ERROR, e);
        } catch (SQLException e) {
            throw new InfrastructureException(SystemErrorCodes.PAGE_CACHE_ERROR, e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception sqle) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception sqle) {
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                } catch (ConnectionPoolException cpe) {
                }
            }
        }

        return docIDList;

    }

    private List<DocID> buildDocIDsFromString(String s)  {
        ArrayList<DocID> docIDList = new ArrayList<DocID>();
        StringTokenizer docs = new StringTokenizer(s, "*");
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        while (docs.hasMoreTokens()) {
            StringTokenizer doc = new StringTokenizer(docs.nextToken(), "|");
            DocID docObj = new DocID(Integer.parseInt(doc.nextToken()), doc.nextToken(), databaseConfig.getDatabase(doc.nextToken()));
            docIDList.add(docObj);
        }

        return docIDList;
    }

    public void cachePage(int pageNumber, List<DocID> docIDList, String searchID) throws PageCacheException {
        try {
            StringBuffer pageBuffer = new StringBuffer();
            for (int k = 0; k < docIDList.size(); k++) {
                if (k > 0) {
                    pageBuffer.append("*");
                }
                DocID doc = (DocID) docIDList.get(k);
                pageBuffer.append(Integer.toString(doc.getHitIndex()));
                pageBuffer.append("|");
                pageBuffer.append(doc.getDocID());
                pageBuffer.append("|");
                pageBuffer.append(doc.getDatabase().getID());
            }

            writePage(pageNumber, searchID, pageBuffer.toString());
        } catch (Exception e) {
            throw new PageCacheException(e);
        }
    }

    private boolean writePage(int pageNo, String searchID, String lString) throws Exception {
        ConnectionBroker broker = ConnectionBroker.getInstance();
        Connection con = null;
        PreparedStatement proc = null;

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            List<DocID> doclist = getPage(pageNo, searchID);
            // proc = con.prepareCall("{ call PageCache_writePage(?,?,?,?,?)}");
            if (null != doclist && doclist.size() > 0) {
                proc = con.prepareStatement("UPDATE PAGE_CACHE SET PI_PAGE_DATA= ? WHERE SEARCH_ID = ? AND PAGE_NO = ?");
                proc.setString(1, lString);
                proc.setString(2, searchID);
                proc.setInt(3, pageNo);
                proc.executeUpdate();
            } else {
                proc = con.prepareStatement("INSERT INTO PAGE_CACHE VALUES(?,?,?,?)");
                proc.setString(1, sessionID);
                proc.setString(2, searchID);
                proc.setInt(3, pageNo);
                // this is not used in insert - variable was ignored in PLSQL
                // proc.setInt(4,timeHolder);
                proc.setString(4, lString);
                proc.executeUpdate();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (proc != null) {
                try {
                    proc.close();
                    proc = null;
                } catch (Exception e) {
                }
            }

            if (con != null) {

                try {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                } catch (Exception e1) {
                }
            }
        }

        return true;
    }
}
