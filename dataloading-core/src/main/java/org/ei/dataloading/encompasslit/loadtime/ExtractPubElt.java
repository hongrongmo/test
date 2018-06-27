package org.ei.dataloading.encompasslit.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.util.StringUtil;

public class ExtractPubElt {

    private static final String setURL = "jdbc:oracle:thin:@stage.ei.org:1521:apl2";
    private static final String setUserName = "gen_search";
    private static final String setPassword = "team";
    private Perl5Util perl = new Perl5Util();

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerPub = null;
        Hashtable<String, String> afsHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerPub = new PrintWriter(new FileWriter("elt_pn.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select pub from elt_master where pub is not null ");
                // System.out.println("\n\nQuery: " + " select pub from elt_master where pub is not null ");

            } else {
                pstmt1 = con.prepareStatement(" select pub from elt_master where pub is not null and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                // System.out.println("\n\nQuery: " + " select pub from elt_master where pub is not null and load_number >= " + load_number_begin +
                // " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String publisher = rs1.getString("pub");
                if (validYear(rs1.getString("pyr"))) {
                    if (publisher != null) {
                        publisher = StringUtil.replaceNonAscii(publisher.trim().toUpperCase());

                        if (!afsHash.containsKey(publisher)) {
                            afsHash.put(publisher, publisher);
                        }
                    }
                }
            }

            Iterator<String> itrTest = afsHash.keySet().iterator();

            for (int i = 0; itrTest.hasNext(); i++) {
                String publisher = (String) itrTest.next();

                writerPub.println(publisher + "\t" + "elt");
            }

            afsHash.clear();

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

    private boolean validYear(String year) {
        if (year == null) {
            return false;
        }

        if (year.length() != 4) {
            return false;
        }

        return perl.match("/[1-9][0-9][0-9][0-9]/", year);
    }

    public static void main(String[] args) {

        ExtractPubElt test = new ExtractPubElt();

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            Connection con = DriverManager.getConnection(setURL, setUserName, setPassword);
            test.extract(0, 0, con);
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
