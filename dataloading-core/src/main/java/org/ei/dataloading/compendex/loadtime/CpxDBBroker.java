/*
 * Created on Dec 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.dataloading.compendex.loadtime;

import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

//import oracle.jdbc.driver.OracleResultSet;  // works for ojdbc14.jar (for 11g RDS)

import oracle.sql.CLOB;

public class CpxDBBroker {
    private String key;
    public static final int OLD_VALUE = 0;
    public static final int NEW_VALUE = 1;
    private boolean updatePnFlag = false;

    private String masterTable;
    private String updateTable;
    private String backupTable;

    private int updateNumber;

    private Connection conn;

    PreparedStatement pstUpdate;
    PreparedStatement pstSave;
    PreparedStatement pstSelect;

    Properties fieldMapping = null;

    private static final String VERSION = "VERSION";

    public CpxDBBroker(String connectionURL, String driver, String username, String password, String masterTable, String updateTable, String backupTable,
        Properties fieldMapping, String key, int updateNum) throws Exception {
        conn = getConnection(connectionURL, driver, username, password);
        this.masterTable = masterTable;
        this.updateTable = updateTable;
        this.backupTable = backupTable;
        this.fieldMapping = fieldMapping;
        this.key = key;
        this.updateNumber = updateNum;
    }

    public void setMatchKey(String key) {
        this.key = key;
    }

    public void setUpdatePnFlag(boolean updatePnFlag) {
        this.updatePnFlag = updatePnFlag;
    }

    public void end() {
        try {
            conn.commit();
            close(pstSave);
            close(pstSelect);
            close(pstUpdate);
            close(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(Statement stmt) {

        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close(ResultSet rs) {

        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close(Connection conn) {

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection(String connectionURL, String driver, String username, String password) throws Exception {
        Class.forName(driver);
        Connection con = DriverManager.getConnection(connectionURL, username, password);
        con.setAutoCommit(false);
        return con;
    }

    public ResultSet getRecordForUpdate(String accession) {
        ResultSet rec = null;
        try {
            if (pstUpdate == null) {
                String sqlUpdate = "select " + updateTable + ".* from " + updateTable + " where " + key + " = ? ";// for update";
                pstUpdate = conn.prepareStatement(sqlUpdate, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            }
            pstUpdate.setString(1, accession);
            rec = pstUpdate.executeQuery();
        } catch (Exception ex) {

            ex.printStackTrace();

        }
        return rec;
    }

    public ResultSet getRecord(String accession) {
        ResultSet rec = null;
        try {
            if (pstSelect == null) {
                String sqlSelect = "select " + masterTable + ".* from " + masterTable + " where " + key + " = ? ";
                pstSelect = conn.prepareStatement(sqlSelect);
                // System.out.println(sqlSelect+"accession="+accession);
            }
            pstSelect.setString(1, accession);
            rec = pstSelect.executeQuery();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return rec;
    }

    public void doBackup(String an) throws SQLException {
        //
        try {
            if (pstSave == null) {
                String save = "insert into " + backupTable + " select " + masterTable + ".* from " + masterTable + " where " + key + " = ? ";
                pstSave = conn.prepareStatement(save);
            }
            pstSave.setString(1, an);
            pstSave.executeUpdate();
            conn.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void truncBackup() throws Exception {
        if (!backupTable.toLowerCase().endsWith("master")) // Not to truncate a real master table.
        {
            Statement stmTrunc = conn.createStatement();
            stmTrunc.execute("truncate table " + backupTable);
        } else {
            System.err.println("Can not Trucate table that ends with the word master");
        }
    }

    public String updateRecord(ResultSet rec, Hashtable<?, ?> htRecord) throws SQLException {
        StringBuffer strLog = new StringBuffer();
        try {
            Enumeration<?> enumeration = fieldMapping.propertyNames();
            boolean updated = false;
            StringBuffer updatedFields = new StringBuffer("[");

            while (enumeration.hasMoreElements()) {
                String field = (String) enumeration.nextElement();
                String tag = fieldMapping.getProperty(field);
                String correctionValue = null;
                if (field != null) {
                    if (htRecord.containsKey(tag)) {
                        correctionValue = htRecord.get(tag).toString();
                        if (field.equals("TR") && correctionValue.indexOf(";") > 0) {
                            correctionValue = correctionValue.replaceAll(";", "");
                        }
                    } else {
                        correctionValue = "";
                    }

                    if (field.equals("XP") && (rec.getString("UP") != null && rec.getString("UP").indexOf("APS Page Fix") > -1)) {
                        continue;
                    }

                    if (updatePnFlag == false && (field.equals("PN") || field.equals("PC") || field.equals("PS") || field.equals("PY"))) {
                        continue;
                    }

                    if (field.equals("AB")) {
                       //CLOB ab = ((OracleResultSet) rec).getCLOB(field);  //HH: 08/09/2016 works for ojdbc14 (11g RDS)
                    	 CLOB ab = (CLOB) ((ResultSet) rec).getClob(field);  //HH: 08/09/2016 added to work for ojdbc6 (12c RDS)

                        if (ab == null || !(ab.getSubString(1, (int) ab.length()).equals(correctionValue))) {
                            if (correctionValue.length() > 0) {
                                strLog.append("Old:" + field + "|" + ab.getSubString(1, (int) ab.length()) + "\n");
                                ab = getCLOB(correctionValue, conn);
                                //((OracleResultSet) rec).updateCLOB(field, ab);   //HH: 08/09/2016 works for ojdbc14 (11g RDS)
                                ((ResultSet) rec).updateClob(field, ab);	  //HH: 08/09/2016 added to work for ojdbc6 (12c RDS)
                                strLog.append("New:" + field + "|" + correctionValue + "\n");
                                updated = true;
                                updatedFields.append(field + ",");
                            }
                        }
                    } else if (!field.equals("M_ID")) {
                        String recValue = rec.getString(field);
                        if ((recValue == null || (!recValue.replaceAll("; ", ";").equals(correctionValue.replaceAll("; ", ";"))))) {
                            if (correctionValue.length() == 0) {
                                if ((recValue != null)
                                    && !(tag.equalsIgnoreCase("AN") || tag.equalsIgnoreCase("DO") || tag.equalsIgnoreCase("EX") || tag.equalsIgnoreCase("YR"))) {
                                    if (field.equalsIgnoreCase("CL") || field.equalsIgnoreCase("CLS") || field.equalsIgnoreCase("CVS")
                                        || field.equalsIgnoreCase("FLS") || field.equalsIgnoreCase("MH")) {
                                        strLog.append("****** Alert ******\n");
                                    }
                                    if (!field.equals("UR") && !field.equals("UP")) {
                                        strLog.append("Old:" + field + "|" + recValue + "\n");
                                        strLog.append("New:" + field + "|" + correctionValue + "\n");
                                        rec.updateString(field, null);
                                        updated = true;
                                    }
                                }
                            } else {
                                /*
                                 * if(field.equalsIgnoreCase("CL") || field.equalsIgnoreCase("CLS") || field.equalsIgnoreCase("CVS") ||
                                 * field.equalsIgnoreCase("FLS") || field.equalsIgnoreCase("MH")) { strLog.append("****** Alert ******\n"); }
                                 */
                                strLog.append("Old:" + field + "|" + recValue + "\n");
                                strLog.append("New:" + field + "|" + correctionValue + "\n");
                                rec.updateString(field, correctionValue);
                                updated = true;
                            }
                            updatedFields.append(field + ",");
                        }
                    }
                }
            }

            if (updated) {
                rec.updateTimestamp("OD", new java.sql.Timestamp(System.currentTimeMillis()));
                rec.updateInt("UPDATE_NUMBER", updateNumber);
                updatedFields.append("]");
                if (rec.getString("UR") != null) {
                    rec.updateString("UR", updatedFields.toString() + "; " + rec.getString("UR"));
                } else {
                    rec.updateString("UR", updatedFields.toString());
                }
                if (htRecord.containsKey("HS")) {
                    String hsString = htRecord.get("HS").toString();
                    if (hsString.length() > 32) {
                        hsString = hsString.substring(0, 32);
                    }
                    rec.updateString("UP", hsString);
                }
                rec.updateRow();
                strLog.append("update done!\n");
                conn.commit();
            } else {
                strLog.append("NOTUPDATED");
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.err.println(htRecord.toString());
            e.printStackTrace();
        }
        return strLog.toString();
    }

    public boolean isUpdateable(ResultSet rec, Hashtable<?, ?> htRecord) throws SQLException {
        Enumeration<?> enumeration = fieldMapping.propertyNames();
        boolean updated = false;

        while (enumeration.hasMoreElements()) {
            String field = (String) enumeration.nextElement();
            String tag = fieldMapping.getProperty(field);
            String abStr = null;
            if (htRecord.containsKey(tag)) {
                abStr = htRecord.get(tag).toString();
            } else {
                abStr = "";
            }
            if (field != null) {
                if (field.equals("AB")) {
                    //CLOB ab = ((OracleResultSet) rec).getCLOB(field);   //HH: 08/09/2016 works for ojdbc14 (11g RDS)
                    CLOB ab = (CLOB) ((ResultSet) rec).getClob(field);	//HH: 08/09/2016 added to work for ojdbc6 (12c RDS)

                    if (ab == null || !(ab.getSubString(1, (int) ab.length()).equals(abStr))) {
                        updated = true;
                        break;
                    }
                } else if (!field.equals("M_ID")) {
                    if (field.equals("XP") && (rec.getString(field) != null) && (rec.getString(field).replaceAll("/", "").equals(abStr))) {
                        continue;
                    }
                    if (rec.getString(field) == null && abStr.length() == 0) {
                        continue;
                    }
                    if ((rec.getString(field) == null || (!rec.getString(field).replaceAll("; ", ";").equals(abStr.replaceAll("; ", ";"))))) {
                        updated = true;
                        break;
                    }
                }
            }
        }
        return updated;
    }

    public void deleteRecord(ResultSet rec, Hashtable<?, ?> htRecord) throws SQLException {
        try {
            rec.updateInt("UPDATE_NUMBER", updateNumber);
            rec.updateTimestamp("OD", new java.sql.Timestamp(System.currentTimeMillis()));
            rec.updateString("UP", htRecord.get("HS").toString());
            rec.updateString("UR", "[DELETED]");
            rec.updateInt("LOAD_NUMBER", 0);
            rec.updateRow();
            conn.commit();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static CLOB getCLOB(String strData, Connection conn) throws SQLException {
        CLOB tempClob = null;
        try {

            // If the temporary CLOB has not yet been created, create new
            tempClob = CLOB.createTemporary(conn, true, CLOB.DURATION_SESSION);

            /* Open the temporary CLOB in readwrite mode to enable writing this does not seem to work using JDBC defaultConnection */
            tempClob.open(CLOB.MODE_READWRITE);
            // Get the output stream to write
            Writer tempClobWriter = tempClob.getCharacterOutputStream();

            // Write the data into the temporary CLOB
            tempClobWriter.write(strData);

            // Flush and close the stream
            tempClobWriter.flush();
            tempClobWriter.close();

            // Close the temporary CLOB
            tempClob.close();
        } catch (SQLException sqlexp) {
            tempClob.freeTemporary();
            sqlexp.printStackTrace();
        } catch (Exception exp) {
            tempClob.freeTemporary();
            exp.printStackTrace();
        }
        return tempClob;
    }
}
