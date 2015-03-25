package org.ei.dataloading.inspec.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.ei.xml.Entity;

public class ExtractPubIbf {

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerPub = null;
        Hashtable<String, String> pnHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        System.currentTimeMillis();

        try {

            writerPub = new PrintWriter(new FileWriter("ibf_pn.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select pub from ibf_master where (pub is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select pub from ibf_master where (pub is not null) and load_number = " + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select pub from ibf_master where (pub is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select pub from ibf_master where (pub is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String publisher_name = rs1.getString("pub");
                publisher_name = Entity.prepareString(publisher_name);

                if (publisher_name != null) {
                    publisher_name = publisher_name.toUpperCase().trim();

                    if (!pnHash.containsKey(publisher_name)) {
                        pnHash.put(publisher_name, publisher_name);
                    }
                }
            }

            Iterator<String> itrTest = pnHash.keySet().iterator();

            for (; itrTest.hasNext();) {
                String publisher_name = (String) itrTest.next();

                writerPub.println(publisher_name + "\tibf\t");
            }

            pnHash.clear();

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

}
