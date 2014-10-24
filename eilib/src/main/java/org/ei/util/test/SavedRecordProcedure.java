package org.ei.util.test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.ei.util.GUID;
import org.ei.util.StringUtil;

public class SavedRecordProcedure {
    private String filter = "TEST";

    private String folderID = null;
    private String userProfileID = null;
    private String docID = null;

    private String strTodaysDate = StringUtil.getFormattedDate("MM-dd-yyyy");

    public void setUserProfileID(String userid) {
        userProfileID = userid;
    }

    public String getUserProfileID() {
        return userProfileID;
    }

    public void setDocID(String docid) {
        docID = docid;
    }

    public String getDocID() {
        return docID;
    }

    public void setFolderID(String folderid) {
        folderID = folderid;
    }

    public String getFolderID() {
        return folderID;
    }

    public SavedRecordProcedure() throws Exception {
        setUserProfileID(filter.concat(new GUID().toString()));
        setFolderID(filter.concat(new GUID().toString()));
        setDocID(new GUID().toString());
    }

    public void runAll() {
        addFolder();
        renameFolder();
        removeAllInFolder();
        addSelectedRecord();
        removeSelected();

        removeFolder();

    }

    public void addFolder() {
        new SavedRecord_addFolder().run();
    }

    public void renameFolder() {
        new SavedRecord_renameFolder().run();
    }

    public void removeFolder() {
        new SavedRecord_removeFolder().run();
    }

    public void removeAllInFolder() {
        new SavedRecord_removeAllInFolder().run();
    }

    public void addSelectedRecord() {
        new SavedRecord_addSelectedRecord().run();
    }

    public void removeSelected() {
        new SavedRecord_removeSelected().run();
    }

    /*
     * SavedRecord_viewListOfFolder(userID varchar2,accessDate varchar2) is SavedRecord_viewRecordInFolder(folderID varchar2,accessDate varchar2) is
     */

    class SavedRecord_addFolder extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement proc = null;
            int idx = 1;

            try {
                proc = con.prepareCall("{ call SavedRecord_addFolder(?,?,?,?,?)}");
                proc.setString(idx++, getFolderID());
                proc.setString(idx++, getUserProfileID());
                proc.setString(idx++, "TEST_" + (new GUID().toString()).substring(0, 10));
                proc.setString(idx++, strTodaysDate);
                proc.setString(idx++, strTodaysDate);
            } catch (SQLException sqle) {
                log.error("SavedRecord_addFolder - SQLException ", sqle);
            } catch (Exception e) {
                log.error("SavedRecord_addFolder - Exception ", e);
            } finally {
            }

            return proc;
        }
    }

    class SavedRecord_renameFolder extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement proc = null;
            int idx = 1;

            try {
                proc = con.prepareCall("{ call SavedRecord_renameFolder(?,?,?,?)}");
                proc.setString(idx++, getFolderID());
                proc.setString(idx++, getUserProfileID());
                proc.setString(idx++, "TEST_" + (new GUID().toString()).substring(0, 10));
                proc.setString(idx++, strTodaysDate);
            } catch (SQLException sqle) {
                log.error("SavedRecord_renameFolder - SQLException ", sqle);
            } catch (Exception e) {
                log.error("SavedRecord_renameFolder - Exception ", e);
            } finally {
            }

            return proc;
        }
    }

    class SavedRecord_removeFolder extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement proc = null;
            int idx = 1;

            try {
                proc = con.prepareCall("{ call SavedRecord_removeFolder(?,?)}");
                proc.setString(idx++, getFolderID());
                proc.setString(idx++, getUserProfileID());
            } catch (SQLException sqle) {
                log.error("SavedRecord_removeFolder - SQLException ", sqle);
            } finally {
            }

            return proc;
        }
    }

    class SavedRecord_removeAllInFolder extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement proc = null;
            int idx = 1;

            try {
                proc = con.prepareCall("{ call SavedRecord_removeAllInFolder(?)}");
                proc.setString(idx++, getFolderID());
            } catch (SQLException sqle) {
                log.error("SavedRecord_removeAllInFolder - SQLException ", sqle);
            } finally {
            }

            return proc;
        }
    }

    class SavedRecord_addSelectedRecord extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement proc = null;
            int idx = 1;

            try {
                proc = con.prepareCall("{ call SavedRecord_addSelectedRecord(?,?,?,?,?,?,?)}");
                proc.setString(idx++, filter.concat(new GUID().toString()));
                proc.setString(idx++, getFolderID());
                proc.setString(idx++, "1");
                proc.setString(idx++, getDocID());
                proc.setString(idx++, strTodaysDate);
                proc.setString(idx++, strTodaysDate);
                proc.setString(idx++, "CLOB");
            } catch (SQLException sqle) {
                log.error("SavedRecord_addSelectedRecord - SQLException ", sqle);
            } catch (Exception e) {
                log.error("SavedRecord_addSelectedRecord - Exception ", e);
            } finally {
            }

            return proc;
        }
    }

    class SavedRecord_removeSelected extends BaseStoredProcedure {

        public CallableStatement getStatement(Connection con) {
            CallableStatement proc = null;
            int idx = 1;

            try {
                proc = con.prepareCall("{ call SavedRecord_removeSelected(?,?)}");
                proc.setString(idx++, getFolderID());
                proc.setString(idx++, getDocID());
            } catch (SQLException sqle) {
                log.error("SavedRecord_removeSelected - SQLException ", sqle);
            } finally {
            }

            return proc;
        }
    }

}
