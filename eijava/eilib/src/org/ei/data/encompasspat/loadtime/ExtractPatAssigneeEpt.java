/*
 * Created on Jun 25, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.data.encompasspat;

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
import org.ei.util.StringUtil;

/**
 * @author Tsolovye
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExtractPatAssigneeEpt {

    private static final String setURL = "jdbc:oracle:thin:@stage.ei.org:1521:apl2";
    private static final String setUserName = "gen_search";
    private static final String setPassword = "team";
    private Perl5Util perl = new Perl5Util();

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerAuthor = null;
        Hashtable ausHash = new Hashtable();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerAuthor = new PrintWriter(new FileWriter("ept_cs.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select cs,py from ept_master where (cs is not null) ");
    //            System.out.println("\n\nQuery: " + " select cs from ept_master where (cs is not null) ");
            } else {
                pstmt1 = con.prepareStatement(" select cs from ept_master where (cs is not null) and load_number >= " + load_number_begin + " and load_number <= " + load_number_end);
   //             System.out.println("\n\nQuery: " + " select cs from ept_master where (cs is not null) and load_number >= " + load_number_begin + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String aus = rs1.getString("cs");
                aus = StringUtil.replaceNonAscii(aus);
                if (validYear(rs1.getString("py"))) {
                    if (aus != null) {
                        StringTokenizer st1 = new StringTokenizer(aus, ";", false);
                        int countTokens1 = st1.countTokens();

                        if (countTokens1 > 0) {
                            while (st1.hasMoreTokens()) {
                                String display_name = st1.nextToken().trim().toUpperCase();

                                if (!ausHash.containsKey(display_name)) {
                                    ausHash.put(display_name, display_name);
                                }
                            }
                        }
                    }
                }
            }

            Iterator itrTest = ausHash.keySet().iterator();

            for (int i = 0; itrTest.hasNext(); i++) {
                String display_name = (String) itrTest.next();
     //           System.out.println("Display Name=" + display_name);
                writerAuthor.println(display_name + "\t" + "ept");
            }

            ausHash.clear();

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

        ExtractPatAssigneeEpt test = new ExtractPatAssigneeEpt();

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
