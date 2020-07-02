package org.ei.dataloading.encompasspat.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;

import org.ei.util.StringUtil;

public class ExtractLaEpt {
    private static final String setURL = "jdbc:oracle:thin:@stage.ei.org:1521:apl2";
    private static final String setUserName = "gen_search";
    private static final String setPassword = "";

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerAuthor = null;
        Hashtable<String, String> laHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerAuthor = new PrintWriter(new FileWriter("ept_la.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select la from ept_master where (la is not null) ");
                // System.out.println("\n\nQuery: " + " select la from ept_master where (la is not null) ");
            } else {
                pstmt1 = con.prepareStatement(" select la from ept_master where (la is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                // System.out.println("\n\nQuery: " + " select la from ept_master where (la is not null) and load_number >= " + load_number_begin +
                // " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String la = rs1.getString("la");
                la = StringUtil.replaceNonAscii(la);

                if (la != null) {

                    la = la.trim().toUpperCase();

                    if (!laHash.containsKey(la)) {
                        laHash.put(la, la);

                    }
                }
            }

            Iterator<String> itrTest = laHash.keySet().iterator();

            for (int i = 0; itrTest.hasNext(); i++) {
                String display_name = (String) itrTest.next();
                // System.out.println("Display Name=" + display_name);
                writerAuthor.println(display_name + "\t" + "ept");
            }

            laHash.clear();

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
            if (con != null)
                try {
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }

    }

    public static void main(String[] args) {

        ExtractLaEpt test = new ExtractLaEpt();

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            Connection con = DriverManager.getConnection(setURL, setUserName, setPassword);
            test.extract(1965, 0, con);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
