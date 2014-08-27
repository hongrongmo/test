package org.ei.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;

/**
 * Session event listener. See web.xml file for usage.
 *
 * @author harovetm
 *
 */
public class EVSessionListener implements HttpSessionListener {

    public static final Logger log4j = Logger.getLogger(EVSessionListener.class);
    public static ConnectionBroker m_broker;
    static {
        try {
            m_broker = ConnectionBroker.getInstance();
        } catch (Throwable t) {
            log4j.error("Unable to create static broker!");
        }
    }

    private static int activeSessions = 0;

    public static int getActiveSessions() {
        return activeSessions;
    }

    /**
     * Session created event handler
     */
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        activeSessions++;
    }

    /**
     * Session destroyed event handler
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        if (activeSessions > 0) {
            activeSessions--;
        }

        // Clear the document basket
        String sessionid = event.getSession().getId();
        try {
            log4j.info("Removing session-based data for session id: '" + sessionid + "'");
            cleanSessionTables(sessionid);
        } catch (Exception e) {
            log4j.error("Unable to remove session items!  Session id: '" + sessionid + "'", e);
        }
    }

    public static void cleanSessionTables(String sessionid) throws Exception {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String strTableArray[] = { "BASKET", "BASKET_STATE", "PAGE_CACHE", "THESAURUS_PATH", "SESSION_PROPS", "LOOKUP_SKIPPER", "NAVIGATOR_CACHE", "SEARCHES",
            "USER_SESSION" };

        try {
            con = m_broker.getConnection(DatabaseConfig.SESSION_POOL);
            for (String table : strTableArray) {
                try {
                    log4j.info("DELETE FROM " + table + " WHERE SESSION_ID=? " + sessionid);
                    pstmt = con.prepareStatement("DELETE FROM " + table + " WHERE SESSION_ID=?");
                    pstmt.setString(1, sessionid);
                    int count = pstmt.executeUpdate();
                    log4j.info("DELETE COUNT = " + count);
                } catch (Throwable t) {
                    log4j.error("Unable to execute:  'DELETE FROM " + table + " WHERE SESSION_ID=?", t);
                } finally {
                    if (pstmt != null) {
                        try {
                            pstmt.close();
                        } catch (Exception sqle) {
                        }
                    }
                }
            }
        } catch (Exception se) {
            throw new Exception(se.getMessage());
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                }
            }

            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception sqle) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception cpe) {
                }
            }
        }
    }

}
