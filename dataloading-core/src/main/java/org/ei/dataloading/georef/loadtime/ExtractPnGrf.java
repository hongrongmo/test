package org.ei.dataloading.georef.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import org.ei.dataloading.LoadLookup;
import org.ei.xml.Entity;
import org.ei.common.*;

public class ExtractPnGrf {
    //public static final String AUDELIMITER = new String(new char[] { 30 });
    //public static final String IDDELIMITER = new String(new char[] { 31 });
    //public static final String GROUPDELIMITER = new String(new char[] { 02 });

    public static void main(String[] args) throws Exception {
        Connection con = null;
        ExtractPnGrf epn = new ExtractPnGrf();
        con = getDbCoonection("jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_EV_SEARCH", "ei3it", "oracle.jdbc.driver.OracleDriver");
        epn.extract(0, 0, con, "georef");
    }

    public void extract(int load_number_begin, int load_number_end, Connection con, String dbname) throws Exception {
        PrintWriter writerPub = null;

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        try {

            writerPub = new PrintWriter(new FileWriter(dbname + "_pn.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select publisher from " + dbname + "_master where (publisher is not null) and load_number = "
                    + load_number_begin);
                System.out.println("\n\nQuery: " + " select publisher from " + dbname + "_master where (publisher is not null) and load_number = "
                    + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select publisher from " + dbname + "_master where (publisher is not null) and load_number >= "
                    + load_number_begin + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select publisher from " + dbname + "_master where (publisher is not null) and load_number >= "
                    + load_number_begin + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            String[] pubGroupArray = null;

            while (rs1.next()) {
                String pubs = rs1.getString("publisher");

                if (pubs != null) {
                    if (pubs.indexOf(Constants.AUDELIMITER) > -1) {
                        pubGroupArray = pubs.split(Constants.AUDELIMITER);
                    } else {
                        pubGroupArray = new String[1];
                        pubGroupArray[0] = pubs;
                    }
                    for (int i = 0; i < pubGroupArray.length; i++) {
                        String pubGroupString = pubGroupArray[i];
                        if (pubGroupString.indexOf(Constants.IDDELIMITER) > -1) {
                            pubGroupString = pubGroupString.substring(pubGroupString.indexOf(Constants.IDDELIMITER) + 1);
                        }
                        StringTokenizer st1 = new StringTokenizer(pubGroupString, Constants.AUDELIMITER, false);
                        int countTokens1 = st1.countTokens();

                        if (countTokens1 > 0) {
                            while (st1.hasMoreTokens()) {
                                String display_name = st1.nextToken().trim().toUpperCase();
                                display_name = Entity.prepareString(display_name);
                                display_name = LoadLookup.removeSpecialCharacter(display_name);
                                writerPub.println(display_name + "\t" + "grf" + "\t");
                            }
                        }
                    }

                }
            }

        } finally {
            if (rs1 != null) {
                try {
                    rs1.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (pstmt1 != null) {
                try {
                    pstmt1.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (writerPub != null) {
                try {
                    writerPub.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static Connection getDbCoonection(String url, String username, String password, String driver) throws Exception {
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url, username, password);
        return con;
    }

}
