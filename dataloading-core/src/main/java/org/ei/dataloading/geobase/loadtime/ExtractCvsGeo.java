package org.ei.dataloading.geobase.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.ei.dataloading.LoadLookup;
import org.ei.util.StringUtil;
import org.ei.xml.Entity;

public class ExtractCvsGeo {
    public static final String AUDELIMITER = new String(new char[] { 30 });
    public static final String IDDELIMITER = new String(new char[] { 29 });
    public static final String GROUPDELIMITER = new String(new char[] { 02 });

    public void extract(int load_number_begin, int load_number_end, Connection con, String dbname) throws Exception {
        PrintWriter writerCvs = null;

        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        long begin = System.currentTimeMillis();

        try {

            writerCvs = new PrintWriter(new FileWriter(dbname + "_cvs.lkp"));

            String yearString = " and source_publicationyear>1995";
            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select descriptor_mainterm_gde from " + dbname
                    + "_master where (descriptor_mainterm_gde is not null) and load_number = " + load_number_begin + yearString);
                System.out.println("\n\nQuery: " + " select descriptor_mainterm_gde from " + dbname
                    + "_master where (descriptor_mainterm_gde is not null) and load_number = " + load_number_begin + yearString);
            } else {
                pstmt1 = con.prepareStatement(" select descriptor_mainterm_gde from " + dbname
                    + "_master where (descriptor_mainterm_gde is not null) and load_number >= " + load_number_begin + " and load_number <= " + load_number_end
                    + yearString);
                System.out.println("\n\nQuery: " + " select descriptor_mainterm_gde from " + dbname
                    + "_master where (descriptor_mainterm_gde is not null) and load_number >= " + load_number_begin + " and load_number <= " + load_number_end
                    + yearString);
            }

            rs1 = pstmt1.executeQuery();

            String[] cvsArray = null;

            while (rs1.next()) {
                String cvsString = rs1.getString("descriptor_mainterm_gde");

                if (cvsString != null) {
                    if (cvsString.indexOf(AUDELIMITER) > -1) {
                        cvsArray = cvsString.split(AUDELIMITER);
                    } else {
                        cvsArray = new String[1];
                        cvsArray[0] = cvsString;
                    }

                    for (int i = 0; i < cvsArray.length; i++) {
                        String singleCvsString = cvsArray[i];

                        String display_name = singleCvsString.trim().toUpperCase();
                        display_name = Entity.prepareString(display_name);
                        display_name = LoadLookup.removeSpecialCharacter(display_name);
                        writerCvs.println(display_name + "\t" + dbname.toLowerCase() + "\t");

                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            if (writerCvs != null) {
                try {
                    writerCvs.close();
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
