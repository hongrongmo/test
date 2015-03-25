package org.ei.dataloading.inspec.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.ei.xml.Entity;

public class ExtractPubIns {

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerPub = null;

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerPub = new PrintWriter(new FileWriter("ins_pn.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select ppub from new_ins_master where (ppub is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select ppub from new_ins_master where (ppub is not null) and load_number = " + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select ppub from new_ins_master where (ppub is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select ppub from new_ins_master where (ppub is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String publisher_name = rs1.getString("ppub");
                publisher_name = Entity.prepareString(publisher_name).trim().toUpperCase();

                if (publisher_name != null) {
                    writerPub.println(publisher_name + "\tins");
                }

            }

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
