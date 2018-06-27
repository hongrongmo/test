package org.ei.dataloading.geobase.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import org.ei.dataloading.LoadLookup;
import org.ei.xml.Entity;

public class ExtractAuthorsGeo {
    public static final String AUDELIMITER = new String(new char[] { 30 });
    public static final String IDDELIMITER = new String(new char[] { 29 });
    public static final String GROUPDELIMITER = new String(new char[] { 02 });

    public void extract(int load_number_begin, int load_number_end, Connection con, String dbname) throws Exception {
        PrintWriter writerAuthor = null;

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerAuthor = new PrintWriter(new FileWriter(load_number_begin + dbname + "_aus.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select authors from " + dbname + "_master where (authors is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select authors from " + dbname + "_master where (authors is not null) and load_number = "
                    + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select authors from " + dbname + "_master where (authors is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select authors from " + dbname + "_master where (authors is not null) and load_number >= "
                    + load_number_begin + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            String[] authorGroupArray = null;
            String[] authorArray = null;

            while (rs1.next()) {
                String authors = rs1.getString("authors");

                if (authors != null) {
                    if (authors.indexOf(GROUPDELIMITER) > -1) {
                        authorGroupArray = authors.split(GROUPDELIMITER);
                    } else {
                        authorGroupArray = new String[1];
                        authorGroupArray[0] = authors;
                    }
                    for (int i = 0; i < authorGroupArray.length; i++) {
                        String authorGroupString = authorGroupArray[i];
                        if (authorGroupString.indexOf(IDDELIMITER) > -1) {
                            authorGroupString = authorGroupString.substring(authorGroupString.indexOf(IDDELIMITER) + 1);
                        }
                        StringTokenizer st1 = new StringTokenizer(authorGroupString, AUDELIMITER, false);
                        int countTokens1 = st1.countTokens();

                        if (countTokens1 > 0) {
                            while (st1.hasMoreTokens()) {
                                String display_name = st1.nextToken().trim().toUpperCase();
                                display_name = Entity.prepareString(display_name);
                                display_name = LoadLookup.removeSpecialCharacter(display_name);
                                writerAuthor.println(display_name + "\t" + dbname.toLowerCase() + "\t");
                            }
                        }
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
            if (writerAuthor != null) {
                try {
                    writerAuthor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
