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
import java.util.StringTokenizer;

import org.ei.util.StringUtil;

public class ExtractPcEpt {
    private static final String setURL = "jdbc:oracle:thin:@stage.ei.org:1521:apl2";
    private static final String setUserName = "gen_search";
    private static final String setPassword = "";

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerAuthor = null;
        Hashtable<String, String> pcHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerAuthor = new PrintWriter(new FileWriter("ept_pc.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select pc from ept_master where (pc is not null) ");
                // System.out.println("\n\nQuery: " + " select pc from ept_master where (pc is not null) ");
            } else {
                pstmt1 = con.prepareStatement(" select pc from ept_master where (pc is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                // System.out.println("\n\nQuery: " + " select pc from ept_master where (pc is not null) and load_number >= " + load_number_begin +
                // " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String pc = rs1.getString("pc");
                pc = StringUtil.replaceNonAscii(pc);

                if (pc != null) {
                    String cy = null;
                    StringTokenizer tokens = new StringTokenizer(pc, ";", false);

                    while (tokens.hasMoreTokens()) {

                        cy = tokens.nextToken().trim().toUpperCase();

                    }
                    if (!pcHash.containsKey(cy)) {
                        pcHash.put(cy, cy);

                    }
                }
            }

            Iterator<String> itrTest = pcHash.keySet().iterator();

            for (int i = 0; itrTest.hasNext(); i++) {
                String display_name = (String) itrTest.next();
                // System.out.println("Display Name=" + display_name);
                writerAuthor.println(display_name + "\t" + "ept");
            }

            pcHash.clear();

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

        ExtractPcEpt test = new ExtractPcEpt();

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
