package org.ei.dataloading.georef.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.ei.dataloading.LoadLookup;
import org.ei.data.georef.runtime.GRFDataDictionary;
import org.ei.xml.Entity;

public class ExtractLangGrf {
    public static final String AUDELIMITER = new String(new char[] { 30 });
    public static final String IDDELIMITER = new String(new char[] { 31 });
    public static final String GROUPDELIMITER = new String(new char[] { 02 });

    public static void main(String[] args) throws Exception {
        Connection con = null;
        ExtractLangGrf ela = new ExtractLangGrf();
        con = getDbCoonection("jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_EV_SEARCH", "ei3it", "oracle.jdbc.driver.OracleDriver");
        ela.extract(0, 0, con, "georef");
    }

    public void extract(int load_number_begin, int load_number_end, Connection con, String dbname) throws Exception {
        PrintWriter writerLang = null;
        GRFDataDictionary grfdd = GRFDataDictionary.getInstance();
        HashMap<String, String> mapLang = new HashMap<String, String>(grfdd.getLanguages());
        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;

        try {

            writerLang = new PrintWriter(new FileWriter(dbname + "_la.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement(" select language_text from " + dbname + "_master where (language_text is not null) and load_number = "
                    + load_number_begin);
                System.out.println("\n\nQuery: " + " select language_text from " + dbname + "_master where (language_text is not null) and load_number = "
                    + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement(" select language_text from " + dbname + "_master where (language_text is not null) and load_number >= "
                    + load_number_begin + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select language_text from " + dbname + "_master where (language_text is not null) and load_number >= "
                    + load_number_begin + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();

            String[] langGroupArray = null;

            while (rs1.next()) {
                String langs = rs1.getString("language_text");

                if (langs != null) {
                    if (langs.indexOf(AUDELIMITER) > -1) {
                        langGroupArray = langs.split(AUDELIMITER);
                    } else {
                        langGroupArray = new String[1];
                        langGroupArray[0] = langs;
                    }
                    for (int i = 0; i < langGroupArray.length; i++) {
                        String langGroupString = langGroupArray[i];
                        if (langGroupString.indexOf(IDDELIMITER) > -1) {
                            langGroupString = langGroupString.substring(langGroupString.indexOf(IDDELIMITER) + 1);
                        }
                        StringTokenizer st1 = new StringTokenizer(langGroupString, AUDELIMITER, false);
                        int countTokens1 = st1.countTokens();

                        if (countTokens1 > 0) {
                            while (st1.hasMoreTokens()) {
                                String display_name = st1.nextToken().trim().toUpperCase();
                                display_name = Entity.prepareString(display_name);
                                display_name = LoadLookup.removeSpecialCharacter(display_name);
                                if (mapLang.get(display_name) != null)
                                    writerLang.println(mapLang.get(display_name) + "\t" + "grf" + "\t");
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
            if (writerLang != null) {
                try {
                    writerLang.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Connection getDbCoonection(String url, String username, String password, String driver) throws Exception {
        Class.forName(driver).newInstance();
        Connection con = DriverManager.getConnection(url, username, password);
        return con;
    }

}
