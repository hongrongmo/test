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

public class ExtractAuthorsGrf {
    public static final String AUDELIMITER = new String(new char[] { 30 });
    public static final String IDDELIMITER = new String(new char[] { 31 });
    public static final String GROUPDELIMITER = new String(new char[] { 02 });

    public static void main(String[] args) throws Exception {
        Connection con = null;
        ExtractAuthorsGrf eag = new ExtractAuthorsGrf();
        con = getDbCoonection("jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_EV_SEARCH", "ei3it", "oracle.jdbc.driver.OracleDriver");
        eag.extract(0, 0, con, "georef");
    }

    public void extract(int load_number_begin, int load_number_end, Connection con, String dbname) throws Exception {
        PrintWriter writerAuthor = null;

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        try {

            writerAuthor = new PrintWriter(new FileWriter(dbname + "_aus.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select person_analytic from " + dbname + "_master where (person_analytic is not null) and load_number = "
                    + load_number_begin);
                System.out.println("\n\nQuery: " + " select person_analytic from " + dbname + "_master where (person_analytic is not null) and load_number = "
                    + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select person_analytic from " + dbname + "_master where (person_analytic is not null) and load_number >= "
                    + load_number_begin + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select person_analytic from " + dbname + "_master where (person_analytic is not null) and load_number >= "
                    + load_number_begin + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            String[] authorGroupArray = null;
            while (rs1.next()) {
                String authors = rs1.getString("person_analytic");

                if (authors != null) {
                    if (authors.indexOf(IDDELIMITER) > -1) {
                        authorGroupArray = authors.split(IDDELIMITER);
                    } else {
                        authorGroupArray = new String[1];
                        authorGroupArray[0] = authors;
                    }
                    for (int i = 0; i < authorGroupArray.length; i++) {
                        String authorGroupString = authorGroupArray[i];
                        StringTokenizer st1 = new StringTokenizer(authorGroupString, AUDELIMITER, false);
                        int countTokens1 = st1.countTokens();

                        if (countTokens1 > 0) {
                            while (st1.hasMoreTokens()) {
                                String display_name = st1.nextToken().trim().toUpperCase();
                                display_name = Entity.prepareString(display_name);
                                display_name = LoadLookup.removeSpecialCharacter(display_name);
                                // System.out.println(display_name+"\t"+"grf"+"\t");
                                writerAuthor.println(display_name + "\t" + "grf" + "\t");
                                // writerAuthor.println(display_name+"\t"+dbname.toLowerCase()+"\t");
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

            if (writerAuthor != null) {
                try {
                    writerAuthor.close();
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
