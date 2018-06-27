/*
 * Created on Jan 17, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.dataloading.upt.loadtime;

import java.sql.*;
import java.io.*;

/**
 * @author KFokuo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PatentRecordUpdater {

    String input;
    String log;
    String setURL;
    String setUserName;
    String setPassword;

    public void executeSql(String input, String log) throws Exception {
        int count = 0;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String mid = null;
        PrintWriter newOut = null;
        int status = 0;
        BufferedReader in = null;
        PrintWriter logWriter = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            conn = DriverManager.getConnection(setURL, setUserName, setPassword);
            stmt = conn.createStatement();

            in = new BufferedReader(new FileReader(input));
            logWriter = new PrintWriter(new FileWriter(log), true);

            String line = null;
            while ((line = in.readLine()) != null) {

                status = stmt.executeUpdate(line);

                logWriter.println("Status = " + status + "\t" + line);
            }

        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
        finally {

            close(in);
            close(logWriter);
            close(rs);
            close(stmt);
            close(conn);
        }

    }
    private void close(PrintWriter log) {

        if (log != null) {
            try {
                log.close();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void close(BufferedReader in) {

        if (in != null) {
            try {
                in.close();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void close(Statement stmt) {

        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void close(ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void close(Connection conn) {

        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        String input = args[0];
        String log = args[1];
        String url = args[2];
        String usr = args[3];
        String pwd = args[4];

        PatentRecordUpdater updater = new PatentRecordUpdater();
        updater.setInput(input);
        updater.setLog(log);
        updater.setURL(url);
        updater.setUserName(usr);
        updater.setPassword(pwd);

        try {
            updater.executeSql(input, log);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public String getInput() {
        return input;
    }

    /**
     * @return
     */
    public String getLog() {
        return log;
    }

    /**
     * @return
     */
    public String getPassword() {
        return setPassword;
    }

    /**
     * @return
     */
    public String getURL() {
        return setURL;
    }

    /**
     * @return
     */
    public String getUserName() {
        return setUserName;
    }

    /**
     * @param string
     */
    public void setInput(String string) {
        input = string;
    }

    /**
     * @param string
     */
    public void setLog(String string) {
        log = string;
    }

    /**
     * @param string
     */
    public void setPassword(String string) {
        setPassword = string;
    }

    /**
     * @param string
     */
    public void setURL(String string) {
        setURL = string;
    }

    /**
     * @param string
     */
    public void setUserName(String string) {
        setUserName = string;
    }

}
