package org.ei.dataloading.compendex.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

public class ExtractCvsCpx {

    public void extract(int load_number_begin, int load_number_end, Connection con, String dbname) throws Exception {
        PrintWriter writerCvs = null;
        Hashtable<String, String> cvsHash = new Hashtable<String, String>();
        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;
        int idx1 = 1;

        try {
            writerCvs = new PrintWriter(new FileWriter(dbname + "_cvs.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select cvs from " + dbname + "_master where cvs is not null and load_number = ?");
                System.out.println(" select cvs from " + dbname + "_master where cvs is not null and load_number = ?");
                pstmt1.setInt(idx1++, load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select cvs from " + dbname + "_master where cvs is not null and load_number >= ? and load_number <= ?");
                System.out.println(" select cvs from " + dbname + "_master where cvs is not null and load_number >= ? and load_number <= ?");
                pstmt1.setInt(idx1++, load_number_begin);
                pstmt1.setInt(idx1++, load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String cvs = rs1.getString("cvs");

                if (cvs != null) {
                    StringTokenizer st1 = new StringTokenizer(cvs, ";", false);
                    int countTokens1 = st1.countTokens();

                    if (countTokens1 > 0) {
                        while (st1.hasMoreTokens()) {
                            String index_term = st1.nextToken().trim().toUpperCase();

                            if (!cvsHash.containsKey(index_term)) {
                                cvsHash.put(index_term, index_term);
                            }
                        }
                    }
                }
            }

            Iterator<String> itrTest = cvsHash.keySet().iterator();

            for (int i = 0; itrTest.hasNext(); i++) {
                String index_term = (String) itrTest.next();

                writerCvs.println(index_term + "\t" + dbname.toLowerCase());
            }

            cvsHash.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        finally {
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
            if (writerCvs != null) {
                try {
                    writerCvs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
