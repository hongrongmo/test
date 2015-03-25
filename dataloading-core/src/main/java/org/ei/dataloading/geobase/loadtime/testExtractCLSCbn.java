package org.ei.dataloading.geobase.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

public class testExtractCLSCbn {
    public static final String AUDELIMITER = new String(new char[] { 30 });

    public void extract(Connection con) throws Exception {
        PrintWriter writerSt = null;
        Hashtable stHash = new Hashtable();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();
        String source_code = null;
        String source_title = null;
        String issn = null;

        try {

            writerSt = new PrintWriter(new FileWriter("loadClassification.txt"));

            pstmt1 = con
                .prepareStatement("select CLASSIFICATION,CLASSIFICATION_DESCRIPTION from geo_master where (classification is not null) and (CLASSIFICATION_DESCRIPTION is not null)");
            System.out
                .println("\n\nQuery: "
                    + " select classification,classification_description from geo_master where (classification is not null) and (classification_description is not null)");

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                source_code = rs1.getString("CLASSIFICATION");
                source_title = rs1.getString("CLASSIFICATION_DESCRIPTION");

                if (source_code != null) {
                    StringTokenizer st1 = new StringTokenizer(source_code, AUDELIMITER, false);
                    StringTokenizer st2 = new StringTokenizer(source_title, AUDELIMITER, false);

                    int countTokens1 = st1.countTokens();
                    int countTokens2 = st2.countTokens();

                    if (countTokens1 > 0) {
                        while (st1.hasMoreTokens() && st2.hasMoreTokens()) {
                            String display_code = st1.nextToken().trim();
                            String display_title = st2.nextToken().trim();

                            if (!stHash.containsKey(display_code)) {
                                stHash.put(display_code, display_title);

                            }
                        }
                    }
                }
            }

            Iterator itrTest = stHash.keySet().iterator();

            for (int i = 0; itrTest.hasNext(); i++) {
                String display_clcode = (String) itrTest.next();
                String display_cltitle = (String) stHash.get(display_clcode);

                writerSt.println("classCodes.put(\"" + display_clcode.trim() + "\",\t\"" + display_cltitle + "\");");

                System.out.println(display_clcode + "\tcbn");
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
