package org.ei.dataloading.encompasslit.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.util.StringUtil;

public class ExtractStElt {

    private static final String setURL = "jdbc:oracle:thin:@stage.ei.org:1521:apl2";
    private static final String setUserName = "gen_search";
    private static final String setPassword = "";
    private Perl5Util perl = new Perl5Util();

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerSt = null;
        Hashtable<String, String> stHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;
        //
        // String source_title = null;
        // String abr_title = null;
        // String issn = null;
        // String tmpissn = null;

        long begin = System.currentTimeMillis();

        try {

            writerSt = new PrintWriter(new FileWriter("elt_st.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select sti, issn from elt_master where sti is not null");
                // System.out.println("\n\nQuery: " + " select sti, issn from elt_master where sti is not null");

            } else {
                pstmt1 = con.prepareStatement(" select sti,issn from elt_master where sti is not null or sta is not null and load_number >= "
                    + load_number_begin + " and load_number <= " + load_number_end);
                // System.out.println("\n\nQuery: " + " select sti,sta, issn from elt_master where sti is not null or sta is not null and load_number >= " +
                // load_number_begin + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String source_title = null;
                if (rs1.getString("sti") != null) {
                    source_title = StringUtil.replaceNonAscii(rs1.getString("sti").trim().toUpperCase());
                }

                String issn = formatISSN(rs1.getString("issn"));
                if (source_title != null && !stHash.containsKey(source_title)) {
                    if (issn == null) {
                        issn = "";
                    }
                    stHash.put(source_title, issn.trim());
                }

            }
            Iterator<String> itrTest = stHash.keySet().iterator();
            for (int i = 0; itrTest.hasNext(); i++) {
                String source_title = (String) itrTest.next();
                String issn = (String) stHash.get(source_title);
                writerSt.println(issn + "\t" + source_title + "\t" + "elt");
            }
            stHash.clear();
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
            if (writerSt != null) {
                try {
                    writerSt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String formatISSN(String issn) {

        String sVal = null;
        if (issn == null)
            return "";
        sVal = perl.substitute("s/--/-/g", issn);

        return sVal;
    }

    public static void main(String[] args) {

        ExtractStElt test = new ExtractStElt();

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
