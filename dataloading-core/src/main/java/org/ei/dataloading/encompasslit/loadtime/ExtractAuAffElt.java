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
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.common.EltAusFormatter;
import org.ei.util.StringUtil;

public class ExtractAuAffElt {

    private Perl5Util perl = new Perl5Util();
    private static final String setURL = "jdbc:oracle:thin:@stage.ei.org:1521:apl2";
    private static final String setUserName = "gen_search";
    private static final String setPassword = "team";

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerAuAff = null;
        Hashtable<String, String> afsHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerAuAff = new PrintWriter(new FileWriter("elt_af.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select aaf from elt_master where (aaf is not null)");
                // System.out.println("\n\nQuery: " + " select aaf from elt_master where (aaf is not null) ");

            } else {
                pstmt1 = con.prepareStatement(" select aaf from elt_master where (aaf is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                // System.out.println("\n\nQuery: " + " select aaf from elt_master where (aaf is not null) and load_number >= " + load_number_begin +
                // " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String institute_name = rs1.getString("aaf");

                if (validYear(rs1.getString("pyr"))) {
                    if (institute_name != null) {
                        String aaf = StringUtil.replaceNonAscii(EltAusFormatter.formatAffiliation(institute_name));

                        StringTokenizer st1 = new StringTokenizer(aaf, ";", false);
                        int countTokens1 = st1.countTokens();

                        if (countTokens1 > 0) {
                            while (st1.hasMoreTokens()) {
                                institute_name = st1.nextToken().trim().toUpperCase();
                                if (!afsHash.containsKey(institute_name)) {
                                    afsHash.put(institute_name, institute_name);
                                }
                            }
                        }
                    }
                }
            }

            Iterator<String> itrTest = afsHash.keySet().iterator();

            for (int i = 0; itrTest.hasNext(); i++) {
                String institute_name = (String) itrTest.next();

                writerAuAff.println(institute_name + "\t" + "elt");
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
            if (writerAuAff != null) {
                try {
                    writerAuAff.close();
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

        ExtractAuAffElt test = new ExtractAuAffElt();

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
