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

public class ExtractSerialTitleGrf {
    public static final String AUDELIMITER = new String(new char[] { 30 });
    public static final String IDDELIMITER = new String(new char[] { 31 });
    public static final String GROUPDELIMITER = new String(new char[] { 02 });

    public static void main(String[] args) throws Exception {
        Connection con = null;
        ExtractSerialTitleGrf est = new ExtractSerialTitleGrf();
        con = getDbCoonection("jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_EV_SEARCH", "ei3it", "oracle.jdbc.driver.OracleDriver");
        est.extract(0, 0, con, "georef");
    }

    public void extract(int load_number_begin, int load_number_end, Connection con, String dbname) throws Exception {
        PrintWriter writerSerialTitle = null;

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        try {

            writerSerialTitle = new PrintWriter(new FileWriter(dbname + "_st.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select title_of_serial from " + dbname + "_master where (title_of_serial is not null) and load_number = "
                    + load_number_begin);
                System.out.println("\n\nQuery: " + " select title_of_serial from " + dbname + "_master where (title_of_serial is not null) and load_number = "
                    + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select title_of_serial from " + dbname + "_master where (title_of_serial is not null) and load_number >= "
                    + load_number_begin + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select title_of_serial from " + dbname + "_master where (title_of_serial is not null) and load_number >= "
                    + load_number_begin + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            String[] stitleGroupArray = null;

            while (rs1.next()) {
                String stitles = rs1.getString("title_of_serial");

                if (stitles != null) {
                    if (stitles.indexOf(IDDELIMITER) > -1) {
                        stitleGroupArray = stitles.split(IDDELIMITER);
                    } else {
                        stitleGroupArray = new String[1];
                        stitleGroupArray[0] = stitles;
                    }
                    for (int i = 0; i < stitleGroupArray.length; i++) {
                        String stitleGroupString = stitleGroupArray[i];
                        StringTokenizer st1 = new StringTokenizer(stitleGroupString, AUDELIMITER, false);
                        int countTokens1 = st1.countTokens();

                        if (countTokens1 > 0) {
                            while (st1.hasMoreTokens()) {
                                String display_name = st1.nextToken().trim().toUpperCase();
                                display_name = Entity.prepareString(display_name);
                                display_name = LoadLookup.removeSpecialCharacter(display_name);
                                writerSerialTitle.println("\t" + display_name + "\t" + "grf" + "\t");
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
            if (writerSerialTitle != null) {
                try {
                    writerSerialTitle.close();
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
