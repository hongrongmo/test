package org.ei.dataloading.paper.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;

public class ExtractStPch {

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerSt = null;
        Hashtable<String, String> stHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        String source_title = null;
        String issn = null;

        try {

            writerSt = new PrintWriter(new FileWriter("pch_st.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select st,sn from paper_master where (st is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select st,sn from paper_master where (st is not null) and load_number = " + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select st,sn from paper_master where (st is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select st,sn from paper_master where (st is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                source_title = rs1.getString("st");
                issn = rs1.getString("sn");

                if (source_title != null) {
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

            for (int i = 0; itrTest.hasNext(); i++) {
                source_title = (String) itrTest.next();
                issn = (String) stHash.get(source_title);

                writerSt.println(issn.trim() + "\t" + source_title + "\tpch");
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
