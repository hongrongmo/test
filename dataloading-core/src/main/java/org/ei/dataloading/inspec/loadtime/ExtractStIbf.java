package org.ei.dataloading.inspec.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.ei.xml.Entity;

public class ExtractStIbf {

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerSt = null;
        Hashtable<String, String> stHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        String fjt = null;
        String source_title = null;
        String issn = null;
        try {
            writerSt = new PrintWriter(new FileWriter("ibf_st.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select fjt from ibf_master where (fjt is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select fjt,sn from ibf_master where (fjt is not null) and load_number = " + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select fjt from ibf_master where (fjt is not null)  and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select fjt from ibf_master where (fjt is not null)  and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                fjt = rs1.getString("fjt");
                // thlp = rs1.getString("thlp");
                // issn = rs1.getString("sn");

                if (fjt != null) {
                    source_title = fjt.trim();
                }
                // else if(thlp != null)
                // {
                // source_title = thlp.trim();
                // }

                if (source_title != null) {
                    source_title = Entity.prepareString(source_title);
                    source_title = source_title.trim().toUpperCase();

                    if (!stHash.containsKey(source_title)) {
                        if (issn == null) {
                            issn = "";
                        }
                        stHash.put(source_title, issn);
                    }
                }
            }

            Iterator<String> itrTest = stHash.keySet().iterator();

            for (; itrTest.hasNext();) {
                source_title = (String) itrTest.next();
                issn = (String) stHash.get(source_title);

                writerSt.println(issn.trim() + "\t" + source_title + "\tibf\t");
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

}
