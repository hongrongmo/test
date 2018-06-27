package org.ei.dataloading.compendex.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.ei.util.StringUtil;

public class ExtractAuthorsCpx {

    public void extract(int load_number_begin, int load_number_end, Connection con, String dbname) throws Exception {
        PrintWriter writerAuthor = null;
        Hashtable<String, String> ausHash = new Hashtable<String, String>();

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerAuthor = new PrintWriter(new FileWriter(dbname + "_aus.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select aus from " + dbname + "_master where (aus is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select aus from " + dbname + "_master where (aus is not null) and load_number = " + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select aus from " + dbname + "_master where (aus is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select aus from " + dbname + "_master where (aus is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            while (rs1.next()) {
                String aus = rs1.getString("aus");

                if (aus != null) {
                    StringTokenizer st1 = new StringTokenizer(aus, ";", false);
                    int countTokens1 = st1.countTokens();

                    if (countTokens1 > 0) {
                        while (st1.hasMoreTokens()) {
                            String display_name = st1.nextToken().trim().toUpperCase();

                            if (!ausHash.containsKey(display_name)) {
                                ausHash.put(display_name, display_name);
                            }
                        }
                    }
                }
            }

            Iterator<String> itrTest = ausHash.keySet().iterator();

            for (int i = 0; itrTest.hasNext(); i++) {
                String display_name = (String) itrTest.next();
                // String au_index_name = getIndexedAuthor(display_name);
                // writerAuthor.println(display_name+"\t"+au_index_name+"\tcpx");
                writerAuthor.println(display_name + "\t" + dbname.toLowerCase());
            }

            ausHash.clear();

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

    private String getIndexedAuthor(String author) {
        String Iauthor = new String();
        StringUtil stringUtil = new StringUtil();

        // ` ~ ! @ # $ % ^ & * ( ) - _ = + [ { ] } \ | ; : ' " , < . > / ?
        // ~ ! @ # % ^ + = ` : | , . < > [ ] - '
        Iauthor = author.replace('~', ' ');
        Iauthor = Iauthor.replace('!', ' ');
        Iauthor = Iauthor.replace('@', ' ');
        Iauthor = Iauthor.replace('#', ' ');
        Iauthor = Iauthor.replace('$', ' ');
        Iauthor = Iauthor.replace('%', ' ');
        Iauthor = Iauthor.replace('^', ' ');
        Iauthor = Iauthor.replace('&', ' ');
        Iauthor = Iauthor.replace('*', ' ');
        Iauthor = Iauthor.replace('+', ' ');
        Iauthor = Iauthor.replace('`', ' ');
        Iauthor = Iauthor.replace(':', ' ');
        Iauthor = Iauthor.replace('|', ' ');
        Iauthor = Iauthor.replace('<', ' ');
        Iauthor = Iauthor.replace('>', ' ');
        Iauthor = Iauthor.replace('[', ' ');
        Iauthor = Iauthor.replace(']', ' ');
        Iauthor = Iauthor.replace('\'', ' ');

        Iauthor = stringUtil.replace(Iauthor, ",", " ", 1, 4);

        Iauthor = Iauthor.replace('.', ' ');
        Iauthor = Iauthor.replace('-', ' ');

        Iauthor = Iauthor.trim();

        while (Iauthor.indexOf("  ") > -1) {
            Iauthor = stringUtil.replace(Iauthor, "  ", " ", 1, 4);
        }

        Iauthor = Iauthor.replace(' ', '9');

        return Iauthor;

    }

}
