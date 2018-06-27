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

public class ExtractAuAffGrf {
    public static final String AUDELIMITER = new String(new char[] { 30 });
    public static final String IDDELIMITER = new String(new char[] { 31 });
    public static final String GROUPDELIMITER = new String(new char[] { 02 });

    public static void main(String[] args) throws Exception {
        Connection con = null;
        ExtractAuAffGrf eaf = new ExtractAuAffGrf();
        con = getDbCoonection("jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_EV_SEARCH", "ei3it", "oracle.jdbc.driver.OracleDriver");
        eaf.extract(0, 0, con, "georef");
    }

    public void extract(int load_number_begin, int load_number_end, Connection con, String dbname) throws Exception {
        PrintWriter writerAuf = null;

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerAuf = new PrintWriter(new FileWriter(dbname + "_af.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select author_affiliation,affiliation_secondary from " + dbname
                    + "_master where (author_affiliation is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select author_affiliation,affiliation_secondary from " + dbname
                    + "_master where (author_affiliation is not null) and load_number = " + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select author_affiliation,affiliation_secondary from " + dbname
                    + "_master where (author_affiliation is not null) and load_number >= " + load_number_begin + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select author_affiliation,affiliation_secondary from " + dbname
                    + "_master where (author_affiliation is not null) and load_number >= " + load_number_begin + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            String[] aufGroupArray = null;

            while (rs1.next()) {
                String aufs = rs1.getString("author_affiliation");
                String aufs2 = rs1.getString("affiliation_secondary");

                if (aufs != null) {
                    if (aufs.indexOf(AUDELIMITER) > -1) {
                        aufGroupArray = aufs.split(AUDELIMITER);
                    } else {
                        aufGroupArray = new String[1];
                        aufGroupArray[0] = aufs;
                    }
                    for (int i = 0; i < aufGroupArray.length; i++) {
                        String aufGroupString = aufGroupArray[i];
                        if (aufGroupString.indexOf(IDDELIMITER) > -1) {
                            aufGroupString = aufGroupString.substring(aufGroupString.indexOf(IDDELIMITER) + 1);
                        }
                        StringTokenizer st1 = new StringTokenizer(aufGroupString, AUDELIMITER, false);
                        int countTokens1 = st1.countTokens();

                        if (countTokens1 > 0) {
                            while (st1.hasMoreTokens()) {
                                String display_name = st1.nextToken().trim().toUpperCase();
                                display_name = Entity.prepareString(display_name);
                                display_name = LoadLookup.removeSpecialCharacter(display_name);

                                writerAuf.println(display_name + "\t" + "grf" + "\t");
                            }
                        }
                    }

                }

                if (aufs2 != null) {
                    int curCount = 0;

                    if (aufs2.indexOf(IDDELIMITER) > -1) {
                        aufGroupArray = aufs2.split(IDDELIMITER);

                    } else {
                        aufGroupArray = new String[1];
                        aufGroupArray[0] = aufs2;
                    }
                    for (int i = 0; i < aufGroupArray.length; i++) {
                        String aufGroupString = aufGroupArray[i];

                        StringTokenizer st1 = new StringTokenizer(aufGroupString, AUDELIMITER, false);
                        int countTokens1 = st1.countTokens();

                        if (curCount == 0 && countTokens1 > 0) {
                            while (st1.hasMoreTokens()) {
                                String display_name = st1.nextToken().trim().toUpperCase();
                                display_name = Entity.prepareString(display_name);
                                display_name = LoadLookup.removeSpecialCharacter(display_name);

                                writerAuf.println(display_name + "\t" + "grf" + "\t");
                                curCount++;
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
            if (writerAuf != null) {
                try {
                    writerAuf.close();
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
