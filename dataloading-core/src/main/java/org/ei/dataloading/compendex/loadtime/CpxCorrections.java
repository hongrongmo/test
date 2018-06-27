package org.ei.dataloading.compendex.loadtime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class CpxCorrections {

    static int updateCounter = 0;
    static int deleteCounter = 0;
    static int totalCounter = 0;
    static int rejectCounter = 0;
    static int addCounter = 0;
    static int notUpdatedCounter = 0;
    static int delNotInDBCounter = 0;
    static boolean updatePnFlag = false;

    static PrintWriter out;
    static PrintWriter log;
    CpxDBBroker cpxDB;
    int updateNum;
    static CpxAtomicReader in;
    private static final String KEY = "EX";

    public static void main(String[] args) throws Exception {

        if (args.length < 10) {
            System.out
                .println("Usage: org.ei.data.compendex.CpxCorrections [url(jdbc:oracle:thin:@SERVER:PORT:SID)] [Driver (oracle.jdbc.driver.OracleDriver)] [user name] [password] [source table] [update table]  [properties file] [Correction file]  [Log file] [Update Number] [type (QA|BACKUP)] [ADD file]");
            System.exit(1);
        }
        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
        String masterTable = args[4];
        String backupTable = args[5];
        String propFile = args[6];
        String inFile = args[7];
        String logFile = args[8];
        int updateNum = Integer.parseInt(args[9]);
        String qa = null;
        String outFile = null;
        if (args.length >= 11) {
            qa = args[10];
        }

        if (args.length >= 12) {
            outFile = args[11];
        }

        if (args.length >= 13 && args[12].equalsIgnoreCase("PN")) {
            updatePnFlag = true;
        }

        if (outFile != null) {
            out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
        }
        log = new PrintWriter(new FileWriter(new File(logFile)));

        String updateTable = null;
        boolean backupFlag = false;
        boolean qaFlag = false;

        if (qa != null) {
            qaFlag = qa.equalsIgnoreCase("QA");
            backupFlag = qa.equalsIgnoreCase("backup");
        }

        if (qaFlag) {
            updateTable = backupTable;
            backupFlag = true;
            System.out.println("QA mode!");
        } else {
            updateTable = masterTable;
        }

        System.out.println("File:" + inFile);
        System.out.println("Add File:" + outFile);
        System.out.println("Log File:" + logFile);
        System.out.println("Property File:" + propFile);
        System.out.println("Update Number:" + updateNum);
        System.out.println("username " + username);
        System.out.println("password " + password);
        System.out.println("driver " + driver);
        System.out.println("Connecting to " + url);
        System.out.println("Updating Table:" + updateTable);
        System.out.println("updatePnFlag= " + updatePnFlag);

        log.println("File:" + inFile);
        log.println("Add File:" + outFile);
        log.println("Log File:" + logFile);
        log.println("Update Number:" + updateNum);
        log.println("############################################################\n\n");

        log.println("Connecting to " + url);
        log.println("Updating Table:" + updateTable);
        log.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n\n");

        CpxCorrections cor = new CpxCorrections(url, driver, username, password, masterTable, updateTable, backupTable, propFile, updateNum, qaFlag);

        in = new CpxAtomicReader(new FileReader(new File(inFile)));
        cor.readRecords(backupFlag);

        log.println("Updated Records:" + updateCounter);
        log.println("Added Records:" + addCounter);
        log.println("Not Updated Records:" + notUpdatedCounter);
        log.println("Rejected Records:" + rejectCounter);
        log.println("Deleted Records:" + deleteCounter);
        log.println("Del not in DB Record:" + delNotInDBCounter++);
        log.println("Total Records:" + totalCounter);
        log.flush();
        System.exit(1);

    }

    public CpxCorrections(String url, String driver, String username, String password, String masterTable, String updateTable, String backupTable,
        String mapFile, int updateNum, boolean qaFlag) throws Exception {
        this.cpxDB = new CpxDBBroker(url, driver, username, password, masterTable, updateTable, backupTable, getFieldMapping(mapFile + ".properties"), KEY,
            updateNum);
        this.updateNum = updateNum;
        this.cpxDB.setUpdatePnFlag(CpxCorrections.updatePnFlag);

    }

    public void readRecords(boolean backup) throws IOException {
        Hashtable rec = null;

        // Gets a record from a Reader

        while ((rec = in.readRecord()) != null) {
            doCorrection(rec, backup);
        }

    }

    public boolean doCorrection(Hashtable htRecord, boolean backup) throws IOException {

        totalCounter++;
        char type = 'U';
        ResultSet rec = null;
        boolean update = true;

        if (htRecord.containsKey("HS")) {
            type = (htRecord.get("HS").toString()).charAt(0);
        }

        String accession = htRecord.get(KEY).toString();
        log.println(accession + " " + htRecord.get("HS").toString());
        // System.out.println("accession "+accession);
        int acc = Integer.parseInt(accession.substring(4));
        if (acc < 6500000) {
            log.println("PRE-BD CPX Record: " + accession);
            rejectCounter++;
            return update;
        }

        try {
            rec = cpxDB.getRecord(accession);
            ResultSet rs = null;
            if (rec.next()) {
                if (type == 'D') {
                    if (backup) {
                        cpxDB.doBackup(accession);
                    }
                    rs = cpxDB.getRecordForUpdate(accession);
                    if (rs.next()) {
                        cpxDB.deleteRecord(rs, htRecord);
                        deleteCounter++;
                        // log.println("Deleting Record");
                    }
                    close(rs);
                } else if (type == 'B') {
                    if (cpxDB.isUpdateable(rec, htRecord)) {
                        // if in QA mode backup the record to a backup table
                        if (backup) {

                            cpxDB.doBackup(accession);
                        }

                        rs = cpxDB.getRecordForUpdate(accession);

                        if (rs.next()) {
                            String updatedResult = cpxDB.updateRecord(rs, htRecord);
                            log.println(updatedResult);
                            if (updatedResult.indexOf("NOTUPDATED") > -1) {
                                notUpdatedCounter++;
                            } else {
                                updateCounter++;
                            }
                        }
                        close(rs);
                    } else // Nothing to update
                    {
                        log.println("NOTUPDATED");
                        notUpdatedCounter++;
                    }
                }
            } else  // Record not in the Database
            {
                if (type != 'D') {
                    log.println("Insert");
                    if (out != null) {
                        writeAtomic(htRecord);
                    }
                    update = false;
                    addCounter++;
                } else // Deleted Status not in the database
                {
                    delNotInDBCounter++;
                    log.println("Record not in database");
                }
            }
            log.println("------------------------------------------------------------\n");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        finally {
            close(rec);
        }
        return update;
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

    public void writeAtomic(Hashtable ht) {

        ht.put("UN", String.valueOf(updateNum));
        Set keys = ht.keySet();
        Iterator it = keys.iterator();
        out.println("*");
        while (it.hasNext()) {
            String tag = (String) it.next();
            out.println(tag + " " + ht.get(tag).toString());
        }
        out.flush();
    }

    private static Properties getFieldMapping(String filename) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(filename));
        return props;
    }

}
