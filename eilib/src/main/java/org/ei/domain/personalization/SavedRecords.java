package org.ei.domain.personalization;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.EIDoc;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.util.GUID;

/**
 * Saved Records.java implements the functionality of saving records to the folder of an User for later retrieval.It has the functionality realated to Folder
 * creation, access and deletion and records added,deleted to the folder.We can view records in a folder.
 */

public class SavedRecords {
    private final static Logger log4j = Logger.getLogger(SavedRecords.class);

    private String userID;
    private Hashtable<String, FolderEntry> folderTable;

    // jam 9/26/2002
    // storing tablenames in strings
    private static String SAVED_RECORDS_SQLTABLENAME = "SAVED_RECORDS";
    private static String FOLDER_SQLTABLENAME = "FOLDER";

    /************ Constructors required to created savedrecords *********************/

    /** Generic constructor without user ID */
    public SavedRecords() throws SavedRecordsException {
    }

    /** Single Argument constructor which creates a savedRecord component for a user */
    public SavedRecords(String aUserID) throws SavedRecordsException {
        if (aUserID == null)
            throw new SavedRecordsException(new Exception("UserID cannot be null"));

        this.userID = aUserID;
    }

    /**
     * Build a folder given the folderid
     *
     * @param folderid
     * @return
     * @throws SavedRecordsException
     */
    public Folder getFolder(String folderid) throws SavedRecordsException {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String folderId;
        String folderName = "";
        Folder folder = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            stmt = conn.createStatement();
            StringBuffer selectBuffer = new StringBuffer("SELECT FOLDER_ID,FOLDER_NAME FROM ").append(FOLDER_SQLTABLENAME).append(" WHERE FOLDER_ID='")
                .append(folderid + "'");

            rs = stmt.executeQuery(selectBuffer.toString());
            if (rs.next()) {
                folderId = rs.getString("FOLDER_ID");
                folderName = rs.getString("FOLDER_NAME");
                folder = new Folder(folderId, folderName);
                folder.setFolderSize(getSizeOfFolder(folder));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SavedRecordsException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception se) {
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                }
            }
        }

        return folder;

    }

    /**
     * Getting the number of records for this folder
     *
     * @param aFolder
     * @return
     * @throws SavedRecordsException
     */
    public int getRecordCount(String folderid) throws SavedRecordsException {
        if (GenericValidator.isBlankOrNull(folderid)) {
            throw new SavedRecordsException(new Exception("folderid cannot be null"));
        }
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int recordCount = 0;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            stmt = conn.createStatement();
            StringBuffer selectBuffer = new StringBuffer();
            selectBuffer.append("SELECT COUNT(RECORD_ID) FROM ").append(SAVED_RECORDS_SQLTABLENAME).append(" WHERE FOLDER_ID='").append(folderid).append("'");
            rs = stmt.executeQuery(selectBuffer.toString());
            if (rs.next()) {
                recordCount = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SavedRecordsException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                }
            }
        }
        return recordCount;
    }

    /**
     * Returning the folderName for the folder for the given folderId
     *
     * @param folderId
     * @return
     * @throws SavedRecordsException
     */
    public String getFolderName(String folderId) throws SavedRecordsException {
        if (GenericValidator.isBlankOrNull(this.userID)) {
            throw new SavedRecordsException("User ID must be passed in constructor!");
        }
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String folderName = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            stmt = conn.createStatement();
            StringBuffer selectBuffer = new StringBuffer();
            selectBuffer.append("SELECT  FOLDER_NAME FROM ").append(FOLDER_SQLTABLENAME).append(" WHERE USER_ID='").append(userID).append("' AND FOLDER_ID='")
                .append(folderId).append("'");

            rs = stmt.executeQuery(selectBuffer.toString());
            if (rs.next()) {
                folderName = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SavedRecordsException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception se) {
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }

        return folderName;
    }

    /******* Folder releated functionality:add,delete,rename,view ******/

    /** To add a Folder */

    public boolean addFolder(Folder aFolder) throws SavedRecordsException {
        if (GenericValidator.isBlankOrNull(this.userID)) {
            throw new SavedRecordsException("User ID must be passed in constructor!");
        }

        if (aFolder == null) {
            throw new SavedRecordsException("Folder is null");
        }

        Connection conn = null;
        CallableStatement proc = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            String accessDate = this.getFormattedDate();
            String folderName = aFolder.getFolderName();
            proc = conn.prepareCall("{ call SavedRecord_addFolder(?,?,?,?,?)}");
            proc.setString(1, new GUID().toString());
            proc.setString(2, userID);
            proc.setString(3, folderName);
            proc.setString(4, accessDate);
            proc.setString(5, accessDate);
            proc.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SavedRecordsException(e);
        } finally {

            if (proc != null) {
                try {
                    proc.close();
                } catch (Exception se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }

        return true;

    }

    /** To delete a folder */

    public boolean removeFolder(Folder aFolder) throws SavedRecordsException {
        if (GenericValidator.isBlankOrNull(this.userID)) {
            throw new SavedRecordsException("User ID must be passed in constructor!");
        }
        if (aFolder == null) {
            throw new SavedRecordsException("Folder is null");
        }

        Connection conn = null;
        CallableStatement proc = null;
        try {
            // remove All Saved records from Saved_records folder when a folder is deleted.
            this.removeAllInFolder(aFolder);
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            proc = conn.prepareCall("{ call SavedRecord_removeFolder(?,?)}");
            proc.setString(1, aFolder.getFolderID());
            proc.setString(2, userID);
            proc.executeUpdate();
        } catch (Exception e) {
            throw new SavedRecordsException(e);
        } finally {
            if (proc != null) {
                try {
                    proc.close();
                } catch (Exception se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }

        return true;
    }

    /*** To rename a folder Name */
    public boolean renameFolder(Folder oldFolder, Folder newFolder) throws SavedRecordsException {
        log4j.info("Accessing SavedRecords.renameFolder");
        if (GenericValidator.isBlankOrNull(this.userID)) {
            throw new SavedRecordsException("User ID must be passed in constructor!");
        }
        if (oldFolder.getFolderID() == null)
            throw new SavedRecordsException("oldFolder Id cannot be null");
        if (newFolder.getFolderName() == null)
            throw new SavedRecordsException("new Folder name cannot be null");

        Connection conn = null;
        CallableStatement proc = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            String accessDate = getFormattedDate();
            proc = conn.prepareCall("{ call SavedRecord_renameFolder(?,?,?,?)}");
            proc.setString(1, oldFolder.getFolderID());
            proc.setString(2, userID);
            proc.setString(3, newFolder.getFolderName());
            proc.setString(4, accessDate);
            proc.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SavedRecordsException(e);
        } finally {
            if (proc != null) {
                try {
                    proc.close();
                } catch (Exception se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }

        return true;
    }

    /**
     * To View the list of Folders for a particular user
     */
    public List<Folder> viewListOfFolders() throws SavedRecordsException {
        if (GenericValidator.isBlankOrNull(this.userID)) {
            throw new SavedRecordsException("User ID must be passed in constructor!");
        }
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Folder> folderList = new ArrayList<Folder>();
        String folderId;
        String folderName = "";
        Folder folder = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            stmt = conn.createStatement();
            // String accessDate=getFormattedDate();
            StringBuffer selectBuffer = new StringBuffer("SELECT FOLDER_ID,FOLDER_NAME FROM ").append(FOLDER_SQLTABLENAME).append(" WHERE USER_ID='");
            selectBuffer.append(userID + "'").append(" ORDER BY FOLDER_ID");

            rs = stmt.executeQuery(selectBuffer.toString());
            while (rs.next()) {
                folderId = rs.getString("FOLDER_ID");
                folderName = rs.getString("FOLDER_NAME");
                folder = new Folder(folderId, folderName);
                folder.setFolderSize(getSizeOfFolder(folder));
                folderList.add(folder);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new SavedRecordsException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception se) {
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }

        return folderList;
    }

    /** Getting the number of folders for this user **/
    public int getFolderCount() throws SavedRecordsException {
        if (GenericValidator.isBlankOrNull(this.userID)) {
            throw new SavedRecordsException("User ID must be passed in constructor!");
        }
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        int folderCount = 0;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            stmt = conn.createStatement();
            StringBuffer selectBuffer = new StringBuffer();
            selectBuffer.append("SELECT COUNT(FOLDER_ID) FROM ").append(FOLDER_SQLTABLENAME).append(" WHERE USER_ID='").append(userID + "'");
            rs = stmt.executeQuery(selectBuffer.toString());
            if (rs.next()) {
                folderCount = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SavedRecordsException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }
        return folderCount;
    }

    /** Returning the folderId generated when the folder is added by sending the name */

    public String getFolderId(String folderName) throws SavedRecordsException {
        if (GenericValidator.isBlankOrNull(this.userID)) {
            throw new SavedRecordsException("User ID must be passed in constructor!");
        }
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String folderId = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            stmt = conn.createStatement();
            StringBuffer selectBuffer = new StringBuffer();
            selectBuffer.append("SELECT  FOLDER_ID FROM ").append(FOLDER_SQLTABLENAME).append(" WHERE USER_ID='").append(userID).append("' AND FOLDER_NAME='")
                .append(folderName).append("'");
            rs = stmt.executeQuery(selectBuffer.toString());
            if (rs.next()) {
                folderId = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SavedRecordsException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }
        return folderId;
    }

    /*********** Record level functionality:add,delete,view ***************/

    // jam 9/25/2002
    // changed addRecord to create list and call AddRecords to lessen
    // size of code !!

    /**
     * Adding a single record to the folder
     */
    public boolean addRecord(FolderEntry aFolderEntry, Folder aFolder) throws SavedRecordsException {
        ArrayList<FolderEntry> alstFolderEntry = new ArrayList<FolderEntry>();
        alstFolderEntry.add(aFolderEntry);

        return addSelectedRecords(alstFolderEntry, aFolder);
    }

    /**
     * Adding a set of Records to the Folder
     */
    public boolean addSelectedRecords(List<FolderEntry> aFolderEntryList, Folder aFolder) throws SavedRecordsException {
        if (aFolderEntryList == null)
            throw new SavedRecordsException(new Exception("folderEntryList cannot be null"));
        if (aFolder == null)
            throw new SavedRecordsException(new Exception("aFolder cannot be null"));

        Connection conn = null;
        CallableStatement proc = null;
        boolean blnReturnFlag = false;

        try {
            int folderSize = getSizeOfFolder(aFolder);
            int maxFolderSize = aFolder.getMaxfolderSize();
            if (maxFolderSize == 0) {
                maxFolderSize = getMaxFolderSize();
            }

            int listsize = aFolderEntryList.size();

            // check to make sure all files will fit
            if ((folderSize + listsize) <= maxFolderSize) {
                // Remove any duplicates first
                removeSelected(aFolderEntryList, aFolder);

                conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
                proc = conn.prepareCall("{ call SavedRecord_addSelectedRecord(?,?,?,?,?,?,?)}");
                Iterator<FolderEntry> itor = aFolderEntryList.listIterator();

                while (itor.hasNext()) {
                    FolderEntry folderEntry = (FolderEntry) itor.next();
                    proc.setString(1, new GUID().toString());
                    proc.setString(2, aFolder.getFolderID());
                    proc.setString(3, folderEntry.getDocID().getDatabase().getID());
                    proc.setString(4, folderEntry.getDocID().getDocID());
                    proc.setString(5, getFormattedDate());
                    proc.setString(6, getFormattedDate());
                    proc.setString(7, ""); // This is the old useless clob.
                    proc.executeUpdate();
                }
                blnReturnFlag = true;
            } else {
                String strMessage = "<DISPLAY>You have tried to save " + listsize + " records to a folder that can only accept " + (maxFolderSize - folderSize)
                    + " more records (the maximum number of records per folder is " + maxFolderSize
                    + "). Try saving fewer records, or save records to another folder.</DISPLAY>";
                throw new SavedRecordsException(new Exception(strMessage));
            }

        } catch (Exception e) {
            throw new SavedRecordsException(e);
        } finally {
            if (proc != null) {
                try {
                    proc.close();
                } catch (Exception se) {
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }

        return blnReturnFlag;
    }

    /** removing records */
    public boolean removeSelected(List<FolderEntry> aFolderEntryList, Folder aFolder) throws SavedRecordsException {
        if (aFolderEntryList == null)
            throw new SavedRecordsException(new Exception("folderEntryList cannot be null"));
        if (aFolder == null)
            throw new SavedRecordsException(new Exception("aFolder cannot be null"));

        Connection conn = null;
        CallableStatement proc = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            String folderID = aFolder.getFolderID();
            int listSize = aFolderEntryList.size();
            proc = conn.prepareCall("{ call SavedRecord_removeSelected(?,?)}");
            for (int k = listSize; k > 0; k--) {
                FolderEntry folderEntry = (FolderEntry) aFolderEntryList.get(k - 1);
                if (folderEntry == null) {
                    throw new Exception("folderEntry cannot be null");
                }
                proc.setString(1, folderID);
                proc.setString(2, folderEntry.getDocID().getDocID());
                proc.executeUpdate();
            }
        } catch (Exception e) {
            throw new SavedRecordsException(e);
        } finally {
            if (proc != null) {
                try {
                    proc.close();
                } catch (Exception se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }
        return true;
    }

    /** removing all records of a particular folder */
    public boolean removeAllInFolder(Folder aFolder) throws SavedRecordsException {
        System.out.println("Accessing SavedRecord.removeAllInFolder");
        if (aFolder == null)
            throw new SavedRecordsException(new Exception("aFolder cannot be null"));
        Connection conn = null;
        CallableStatement proc = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            proc = conn.prepareCall("{ call SavedRecord_removeAllInFolder(?)}");
            proc.setString(1, aFolder.getFolderID());
            proc.executeUpdate();
        } catch (Exception e) {
            throw new SavedRecordsException(e);
        } finally {

            if (proc != null) {
                try {
                    proc.close();
                } catch (Exception se) {
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }
        return true;

    }

    /* Returns size of Folder ie no of records this folder conatins */

    public int getSizeOfFolder(Folder aFolder) throws SavedRecordsException {
        if (aFolder == null)
            throw new SavedRecordsException(new Exception("aFolder cannot be null"));
        int folderSize = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            stmt = conn.createStatement();
            StringBuffer selectBuffer = new StringBuffer();
            selectBuffer.append("select COUNT(DISTINCT GUID)  from ").append(SAVED_RECORDS_SQLTABLENAME).append(" where FOLDER_ID='").append(aFolder.getFolderID())
                .append("'");
            rs = stmt.executeQuery(selectBuffer.toString());
            if (rs.next() == true) {
                folderSize = rs.getInt(1);
            }
        } catch (Exception e) {
            throw new SavedRecordsException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }

        return folderSize;
    }

    /**
     * To View Records in a Folder.returns a Page of Records which contains all the records of Folder
     */

    public FolderPage viewRecordsInFolder(String folderid, String dataFormat) throws SavedRecordsException {
        if (GenericValidator.isBlankOrNull(folderid))
            throw new SavedRecordsException(new Exception("Folder ID must be present"));

        FolderPage folderPage = new FolderPage();
        try {
            List<FolderEntry> folderEntries = new ArrayList<FolderEntry>();
            folderEntries.addAll(getEIDocumentsOfFolder(folderid, dataFormat));
            for (int i = 0; i < folderEntries.size(); i++) {
                FolderEntry pageFolderEntry = (FolderEntry) folderEntries.get(i);
                folderPage.add(pageFolderEntry);
            }
        } catch (Exception e) {
            throw new SavedRecordsException(e);
        }

        return folderPage;
    }

    /********* private methods *******************************************/

    /** Getting date in a format required */
    private String getFormattedDate() {
        Calendar cal = Calendar.getInstance();
        java.util.Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yy");
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    /** Getting Max Folder Size */
    private int getMaxFolderSize() {
        ApplicationProperties eiProps = ApplicationProperties.getInstance();
        int docsPerFolder = 0;
        docsPerFolder = Integer.parseInt(eiProps.getProperty(ApplicationProperties.MAX_FOLDERSIZE));
        return docsPerFolder;

    }

    /**
     * This gets the EIDocuments by building the documents sending it to recspective builders and ordering them in the sequence they were added to the database
     */

    private Set<FolderEntry> getEIDocumentsOfFolder(String folderid, String dataFormat) throws Exception {
        TreeSet<FolderEntry> folderSet = new TreeSet<FolderEntry>();
        List<DocID> folderDocIDs = buildFolderDocIDs(folderid);
        MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
        List<EIDoc> docs = builder.buildPage(folderDocIDs, dataFormat);
        Iterator<EIDoc> docItor = docs.iterator();
        while (docItor.hasNext()) {
            Object obj = docItor.next();
            EIDoc eiDoc = (EIDoc) obj;
            DocID compareDocID = eiDoc.getDocID();
            String compDocIDHashCode = Integer.toString(compareDocID.hashCode());
            FolderEntry fEntry = (FolderEntry) folderTable.get(compDocIDHashCode);
            fEntry.setEIDoc(eiDoc);
            folderSet.add(fEntry);
        }

        return folderSet;
    }

    /**
     * Building the DocID objects by getting data from the SavedRecrds table
     */
    private List<DocID> buildFolderDocIDs(String folderid) throws SavedRecordsException {

        folderTable = new Hashtable<String, FolderEntry>();
        List<DocID> folderDocIDs = new ArrayList<DocID>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
            conn = ConnectionBroker.getInstance().getConnection(DatabaseConfig.SESSION_POOL);
            stmt = conn.createStatement();
            String docId = "";
            String dataBase = "";
            DocID docID = null;
            StringBuffer getBuffer = new StringBuffer();
            getBuffer.append("select RECORD_ID,GUID,DATABASE_ID from ").append(SAVED_RECORDS_SQLTABLENAME).append(" where FOLDER_ID='").append(folderid)
                .append("'").append("ORDER BY SAVE_TIMESTAMP,RECORD_ID ");

            rs = stmt.executeQuery(getBuffer.toString());
            int i = 1;
            while (rs.next()) {
                rs.getString("RECORD_ID");
                docId = rs.getString("GUID");
                dataBase = rs.getString("DATABASE_ID");
                docID = new DocID(docId, databaseConfig.getDatabase(dataBase.toLowerCase()));
                FolderEntry fEntry = new FolderEntry();
                fEntry.setFolderEntrytHitIndex(Integer.toString(i));
                fEntry.setDocID(docID);
                folderTable.put(Integer.toString(docID.hashCode()), fEntry);
                folderDocIDs.add(docID);
                i++;
            }
        } catch (Exception e) {
            throw new SavedRecordsException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception sq) {
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception se) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception cpe) {
                }
            }
        }

        return folderDocIDs;
    }

}// End of SavedRecords.java

