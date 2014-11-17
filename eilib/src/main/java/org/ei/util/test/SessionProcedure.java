package org.ei.util.test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.ei.util.GUID;

public class SessionProcedure {
    private int intVersion = 1;
    private int intContractid = 923358;

    private String filter = "TEST";
    private String strUser = "JOHN";

    private String sessid = null;

    public SessionProcedure() throws Exception {
        sessid = filter.concat(new GUID().toString());
    }

    public void runAll() {
        createSession();
        touchSession();
        updateSession();
    }

    public void createSession() {
        new SessionProcedure_createSession().run();
    }

    public void touchSession() {
        new SessionProcedure_touch().run();
    }

    public void updateSession() {
        new SessionProcedure_updateSession().run();
    }

    class SessionProcedure_createSession extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement pstmt = null;
            int idx = 1;

            try {
                pstmt = (CallableStatement) con.prepareCall("{ call SessionBroker_createSession(?,?,?,?,?)}");
                pstmt.setString(idx++, sessid);
                pstmt.setInt(idx++, intVersion++);
                pstmt.setInt(idx++, intContractid);
                pstmt.setLong(idx++, System.currentTimeMillis());
                pstmt.setString(idx++, strUser);

            } catch (SQLException sqle) {
                log.error("SessionProcedure_createSession - SQLException ", sqle);
            } finally {
            }

            return pstmt;
        }
    }

    class SessionProcedure_touch extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement pstmt = null;
            int idx = 1;

            try {
                pstmt = (CallableStatement) con.prepareCall("{ call SessionBroker_touch(?,?)}");
                pstmt.setString(idx++, sessid);
                pstmt.setLong(idx++, System.currentTimeMillis());
            } catch (SQLException sqle) {
                log.error("SessionProcedure_touch - SQLException ", sqle);
            } finally {
            }

            return pstmt;
        }
    }

    class SessionProcedure_updateSession extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement pstmt = null;
            int idx = 1;

            try {
                pstmt = (CallableStatement) con.prepareCall("{ call SessionBroker_updateSession(?,?,?,?,?)}");
                pstmt.setString(idx++, sessid);
                pstmt.setInt(idx++, intVersion++);
                pstmt.setInt(idx++, intContractid);
                pstmt.setLong(idx++, System.currentTimeMillis());
                pstmt.setString(idx++, strUser);

            } catch (SQLException sqle) {
                log.error("SessionProcedure_updateSession - SQLException ", sqle);
            } finally {
            }

            return pstmt;
        }
    }

}
