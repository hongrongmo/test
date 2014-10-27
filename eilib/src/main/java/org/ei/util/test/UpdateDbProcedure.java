package org.ei.util.test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.ei.util.GUID;

public class UpdateDbProcedure {

    private String filter = "TEST";
    private String strUser = "ZHUN";

    private String sessid = null;
    private String docId = null;
    private String searchid = null;
    private int basketCount = 0;

    public UpdateDbProcedure() throws Exception {

        docId = filter.concat(new GUID().toString());
        sessid = filter.concat(new GUID().toString());
        searchid = filter.concat(new GUID().toString());
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocId() {
        return this.docId;
    }

    public void setSessId(String sessid) {
        this.sessid = sessid;
    }

    public String getSessId() {
        return this.sessid;
    }

    public void setSearchId(String searchid) {
        this.searchid = searchid;
    }

    public String getSearchId() {
        return this.searchid;
    }

    public void runAll() {
        addAll();
        removeAll();
        removeSelected();
        updateClearOnNewSearch();
    }

    public void addAll() {
        new UpdateDbProcedure_addAll().run();
    }

    public void removeAll() {
        new UpdateDbProcedure_removeAll().run();
    }

    public void updateClearOnNewSearch() {
        new UpdateDbProcedure_updateClearOnNewSearch().run();
    }

    public void removeSelected() {
        new UpdateDbProcedure_removeSelected().run();
    }

    class UpdateDbProcedure_addAll extends BaseStoredProcedure {
        public CallableStatement getStatement(Connection con) {
            CallableStatement pstmt = null;
            int idx = 1;

            try {
                pstmt = con.prepareCall("{ call DB_addAll(?,?,?,?,?,?,?,?)}");
                pstmt.setString(idx++, getSessId());
                pstmt.setString(idx++, getSearchId());
                pstmt.setInt(idx++, ++basketCount);
                pstmt.setString(idx++, getDocId());
                pstmt.setString(idx++, "NA");
                pstmt.setString(idx++, "1");
                pstmt.setString(idx++, "test query");
                pstmt.setString(idx++, "999");

            } catch (SQLException sqle) {
                log.error("UpdateDbProcedure_addAll - SQLException ", sqle);
            } finally {
            }
            return pstmt;
        }
    }

    class UpdateDbProcedure_updateClearOnNewSearch extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement pstmt = null;
            int idx = 1;

            try {
                pstmt = con.prepareCall("{ call DB_updateClearOnNewSearch(?,?,?)}");
                pstmt.setString(idx++, getSessId());
                pstmt.setInt(idx++, 1);
                pstmt.setLong(3, System.currentTimeMillis());
            } catch (SQLException sqle) {
                log.error("UpdateDbProcedure_updateClearOnNewSearch - SQLException ", sqle);
            } finally {
            }

            return pstmt;
        }
    }

    class UpdateDbProcedure_removeSelected extends BaseStoredProcedure {
        public CallableStatement getStatement(Connection con) {
            CallableStatement pstmt = null;
            int idx = 1;

            try {
                pstmt = con.prepareCall("{ call DB_removeSelected(?,?,?)}");
                pstmt.setString(idx++, getSessId());
                pstmt.setString(idx++, getDocId());
                pstmt.setString(idx++, "1");

                basketCount--;
            } catch (SQLException sqle) {
                log.error("UpdateDbProcedure_removeSelected - SQLException ", sqle);
            } finally {
            }

            return pstmt;
        }
    }

    class UpdateDbProcedure_removeAll extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement pstmt = null;
            int idx = 1;

            try {
                pstmt = con.prepareCall("{ call DB_removeAll(?)}");
                pstmt.setString(idx++, getSessId());
                basketCount = 0;
            } catch (SQLException sqle) {
                log.error("UpdateDbProcedure_removeAll - SQLException ", sqle);
            } finally {
            }

            return pstmt;
        }
    }

}
