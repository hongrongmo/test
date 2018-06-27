package org.ei.dataloading.inspec.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.ei.xml.Entity;
import org.ei.common.*;

public class ExtractAuAffIns {

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerAuAff = null;
        String[] institute_names = new String[1];
        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        try {

            writerAuAff = new PrintWriter(new FileWriter("ins_af.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select aaff from new_ins_master where (aaff is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select aaff from new_ins_master where (aaff is not null) and load_number = " + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select aaff from new_ins_master where (aaff is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select aaff from new_ins_master where (aaff is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String aaff = rs1.getString("aaff");

                institute_names = prepareAuthor(aaff);
                // String institute_name = rs1.getString("aaff");
                for (int i = 0; i < institute_names.length; ++i) {
                    String institute_name = institute_names[i];
                    if (institute_name != null) {
                        institute_name = Entity.prepareString(institute_name).trim().toUpperCase();
                        if (institute_name.endsWith(";")) {
                            institute_name = institute_name.substring(0, institute_name.lastIndexOf(";"));
                        }

                        writerAuAff.println(institute_name + "\tins");

                    }

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
            if (writerAuAff != null) {
                try {
                    writerAuAff.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String[] prepareAuthor(String aString) throws Exception {

        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(aString, Constants.AUDELIMITER);
        String s;

        while (st.hasMoreTokens()) {
            s = st.nextToken().trim();
            if (s.length() > 0) {
                if (s.indexOf(Constants.IDDELIMITER) > -1) {
                    int i = s.indexOf(Constants.IDDELIMITER);
                    s = s.substring(0, i);
                }
                s = s.trim();
                list.add(s);
            }

        }

        return (String[]) list.toArray(new String[1]);

    }

}
