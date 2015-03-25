package org.ei.dataloading.chem.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;

public class ExtractAuAffChm {

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerAuAff = null;
        Hashtable<String, String> afsHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        try {

            writerAuAff = new PrintWriter(new FileWriter("chm_af.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select adr from chm_master where (adr is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select adr from chm_master where (adr is not null) and load_number = " + load_number_begin);

            } else {
                pstmt1 = con.prepareStatement(" select adr from chm_master where (adr is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select adr from chm_master where (adr is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String institute_name = rs1.getString("adr");

                if (institute_name != null && institute_name.indexOf(", ") != -1) {
                    institute_name = institute_name.substring(institute_name.indexOf(", ") + 2).trim().toUpperCase();

                    if (!afsHash.containsKey(institute_name)) {
                        afsHash.put(institute_name, institute_name);
                    }
                }
            }

            Iterator<String> itrTest = afsHash.keySet().iterator();

            for (int i = 0; itrTest.hasNext(); i++) {
                String institute_name = (String) itrTest.next();

                writerAuAff.println(institute_name + "\tchm");
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

}
