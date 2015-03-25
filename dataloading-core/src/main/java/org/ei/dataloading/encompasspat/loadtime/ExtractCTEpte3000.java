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

import org.apache.oro.text.perl.Perl5Util;
import org.ei.common.CVSTermBuilder;
import org.ei.util.StringUtil;

public class ExtractCTEpte3000 {
    // private static final String setURL = "jdbc:oracle:thin:@stage.ei.org:1521:apl2";
    // private static final String setUserName = "gen_search";
    // private static final String setPassword = "team";

    private static final String setURL = "jdbc:oracle:thin:@e3000.ei.org:1521:apli";
    private static final String setUserName = "encompass";
    private static final String setPassword = "team";

    private Perl5Util perl = new Perl5Util();

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerCT = null;
        Hashtable<String, String> ctHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerCT = new PrintWriter(new FileWriter("ept_ct.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select ct,py from apipat_base_new where (ct is not null)");
                // System.out.println("\n\nQuery: " + " select apict from apipat_base_new where (ct is not null)");
            } else {
                pstmt1 = con.prepareStatement(" select ct from apipat_base_new where (ct is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                // System.out.println(
                // "\n\nQuery: "
                // + " select apict from apipat_base_new where (ct is not null) and load_number >= "
                // + load_number_begin
                // + " and load_number <= "
                // + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            CVSTermBuilder termBuilder = new CVSTermBuilder();
            while (rs1.next()) {
                String ct = rs1.getString("ct");
                if (validYear(rs1.getString("py"))) {
                    if (ct != null) {
                        StringBuffer cvs = new StringBuffer();
                        String cv = termBuilder.getNonMajorTerms(ct);
                        String mh = termBuilder.getMajorTerms(ct);
                        StringBuffer cvBuffer = new StringBuffer();

                        String expandedMajorTerms = termBuilder.expandMajorTerms(mh);
                        String expandedMH = termBuilder.getMajorTerms(expandedMajorTerms);
                        String expandedCV1 = termBuilder.expandNonMajorTerms(cv);
                        String expandedCV2 = termBuilder.getNonMajorTerms(expandedMajorTerms);

                        if (!expandedCV1.equals(""))
                            cvBuffer.append(expandedCV1);
                        if (!expandedCV2.equals(""))
                            cvBuffer.append(";").append(expandedCV2);

                        String parsedCV = CVSTermBuilder.formatCT(cvBuffer.toString());
                        parsedCV = StringUtil.replaceNonAscii(parsedCV);
                        // String parsedMH = termBuilder.formatCT(expandedMH);
                        // parsedMH = StringUtil.replaceNonAscii(parsedMH);

                        cvs.append(termBuilder.getStandardTerms(parsedCV));

                        StringTokenizer st1 = new StringTokenizer(cvs.toString(), ";", false);
                        int countTokens1 = st1.countTokens();
                        if (countTokens1 > 0) {
                            while (st1.hasMoreTokens()) {
                                String display_name = CVSTermBuilder.formatCT(StringUtil.replaceNonAscii(st1.nextToken().trim().toUpperCase()));
                                if (!ctHash.containsKey(display_name)) {
                                    ctHash.put(display_name, display_name);
                                }
                            }
                        }
                    }
                }
            }

            Iterator<String> itrTest = ctHash.keySet().iterator();
            for (int i = 0; itrTest.hasNext(); i++) {
                String display_name = (String) itrTest.next();
                // System.out.println("Display Name=" + display_name);
                writerCT.println(display_name + "\t" + "ept");
            }

            ctHash.clear();

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
            if (writerCT != null) {
                try {
                    writerCT.close();
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

        ExtractCTEpte3000 test = new ExtractCTEpte3000();

        try {
            // System.out.println("ExtractCTEpte3000 started");
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            Connection con = DriverManager.getConnection(setURL, setUserName, setPassword);
            test.extract(200437, 0, con);
            // System.out.println("ExtractCTEpte3000 ended");
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
