package org.ei.dataloading.compendex.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;

public class ExtractAuAffCpx {

    public void extract(int load_number_begin, int load_number_end, Connection con, String dbname) throws Exception {
        PrintWriter writerAuAff = null;
        Hashtable<String, String> afsHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        try {

            writerAuAff = new PrintWriter(new FileWriter(dbname + "_af.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select af from " + dbname + "_master where (af is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select af from " + dbname + "_master where (af is not null) and load_number = " + load_number_begin);

            } else {
                pstmt1 = con.prepareStatement(" select af from " + dbname + "_master where (af is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select af from " + dbname + "_master where (af is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String institute_name = rs1.getString("af");

                if (institute_name != null) {
                    institute_name = institute_name.trim().toUpperCase();

                    if (institute_name.endsWith(";")) {
                        institute_name = institute_name.substring(0, institute_name.lastIndexOf(";"));
                    }

                    if (!afsHash.containsKey(institute_name)) {
                        afsHash.put(institute_name, institute_name);
                    }
                }
            }

            Iterator<String> itrTest = afsHash.keySet().iterator();

            for (int i = 0; itrTest.hasNext(); i++) {
                String institute_name = (String) itrTest.next();

                writerAuAff.println(institute_name + "\t" + dbname.toLowerCase());
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
