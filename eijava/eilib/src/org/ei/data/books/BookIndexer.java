package org.ei.data.books;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.oro.text.perl.Perl5Util;
//import org.ei.query.base.PorterStemmer;
//import org.ei.data.books.tools.OutputPageData;
//import org.ei.data.books.tools.PDF_FileInfo;
import org.ei.util.StringUtil;

public class BookIndexer {

    // private static PorterStemmer stemmer = new PorterStemmer();
    // private static TextFileFilter filter = new TextFileFilter();
    private static Perl5Util perl = new Perl5Util();

    private static Map stopWords = new HashMap();
    // private static int numfiles = 0;
    // private static int numbooks = 0;

    static {
        stopWords.put("Engineering", "n");
        stopWords.put("Engineers", "n");
        stopWords.put("pH", "n");
    }

    public static void main(String args[])
    // throws Exception
    {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI"; // args[1];
        String username = "AP_PRO1"; // args[2];
        String password = "ei3it"; // args[3];

        List terms = new ArrayList();
        String strDefault = "WOBL";
        if (args != null) {
            if (args.length >= 1 && args[0] != null) {
                strDefault = args[0];
            }
        }
        strDefault = strDefault.toUpperCase();
        System.out.println("Using table BOOK_PAGES_" + strDefault);
        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI"; // args[1];
            username = "AP_EV_SEARCH"; // args[2];
            password = "ei3it"; // args[3];
            con = DriverManager.getConnection(url, username, password);

            stmt = con.createStatement();
            rs = stmt
                    .executeQuery("select MAIN_TERM_SEARCH from CPX_THESAURUS WHERE STATUS='C'");
            while (rs.next()) {
                String term = rs.getString("MAIN_TERM_SEARCH");
                terms.add(term);
                // System.out.println(term);
            }

            if (rs != null) {
                close(rs);
            }
            if (stmt != null) {
                close(stmt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        try {
            // "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_EV_SEARCH",
            // "ei3it"

            url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI"; // args[1];
            username = "AP_PRO1"; // args[2];
            password = "ei3it"; // args[3];
            con = DriverManager.getConnection(url, username, password);
            Map books = new HashMap();

            stmt = con.createStatement();
            rs = stmt.executeQuery("select bn13, ti from BOOKS_ALL"); // where
                                                                        // BN13='9781555582739'");

            while (rs.next()) {
                String bn = rs.getString("bn13");
                String ti = rs.getString("ti");
                books.put(bn, ti);
                // System.out.println(bn + " ==> " + ti);
            }

            if (rs != null) {
                close(rs);
            }
            if (stmt != null) {
                close(stmt);
            }

            Iterator booksIt = (books.keySet()).iterator();

            PreparedStatement pstmt1 = null;

            pstmt1 = con
                    .prepareStatement("select docid,page_txt,vo from BOOK_PAGES_"
                            + strDefault + " where bn13 = ?");

            while (booksIt.hasNext()) {
                String isbn13 = (String) booksIt.next();
                String bookTitle = (String) books.get(isbn13);
                long commitCount = 0;
                pstmt1.setString(1, isbn13);
                rs = pstmt1.executeQuery();

                while (rs.next()) {
                    String id = rs.getString("docid");
                    String col = rs.getString("vo");

                    String keywords = null;
                    Clob clob = rs.getClob("page_txt");
                    if (clob != null) {
                        String abs = null; // getPageTextFromFile(id,isbn13);
                        try {
                            if (abs == null) {
                                abs = StringUtil.getStringFromClob(clob);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (abs != null) {
                            abs = abs.replaceFirst(bookTitle, "");
                            // abs = abs.replaceFirst("This page intentionally
                            // left blank", "");
                            keywords = getKeywords(terms, abs);
                            if (keywords != null) {
                                System.out.println("update BOOK_PAGES_"
                                        + strDefault + " set page_keywords = '"
                                        + keywords.replaceAll("'", "''")
                                        + "' where docid = '" + id + "';");
                                commitCount++;
                            } else {
                                System.out.println("/* No Keyword matches for "
                                        + id + ", " + col + " */");
                            }
                        } else {
                            System.out.println("/* NULL Abstract for " + id
                                    + " */");
                        }
                    }
                    System.out.println(commitCount);
                    if (commitCount >= 10000) {
                        System.out.println("commit;");
                        commitCount = 0;
                    }

                }
            }
            System.out.println("commit;");

            if (rs != null) {
                close(rs);
            }
            if (pstmt1 != null) {
                close(pstmt1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        System.out.println("quit;");
    }

    /*
     * private static String getPageTextFromFile(String id, String isbn13) {
     * String[] idtokens = id.split("_"); StringBuffer allPageTxt = new
     * StringBuffer(); File page_txt = new File(OutputPageData.PATH_PREFIX +
     * System.getProperty("file.separator") + "BurstAndExtracted" +
     * System.getProperty("file.separator") + isbn13 +
     * System.getProperty("file.separator") + "pg_" +
     * PDF_FileInfo.formatPageNumber(Long.parseLong(idtokens[2])) + ".txt");
     * BufferedReader rdr = null; try { rdr = new BufferedReader(new
     * FileReader(page_txt)); while (rdr.ready()) {
     * allPageTxt.append(rdr.readLine()); // TODO Auto-generated method stub } }
     * catch (FileNotFoundException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
     * catch block e.printStackTrace(); } finally {
     *  } return allPageTxt.toString(); }
     */
    private static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getKeywords(List terms, String text) {
        StringBuffer termbuf = new StringBuffer();
        String spage = stem(text);
        for (int j = 0; j < terms.size(); j++) {
            String term = (String) terms.get(j);
            term = term.trim();
            if (term.length() > 0 && !stopWords.containsKey(term)) {
                String stemmedTerm = stem(term);
                String regex = "m/\\b" + stemmedTerm + "\\b/";
                if (perl.match(regex, spage)) {
                    //System.out.println("Hit:"+term);
                    if (termbuf.length() > 0) {
                        termbuf.append("|");
                    }
                    termbuf.append(perl.substitute("s/'/''/g", term));
                }
            }
        }

        if (termbuf.length() > 0) {
            return termbuf.toString();
        }

        return null;
    }

    private static String stem(String words) {
        String lwords = words.toLowerCase();
        String[] splitwords = lwords.split("\\W+");
        StringBuffer stemmedBuffer = new StringBuffer();

        for (int i = 0; i < splitwords.length; i++) {
            if (stemmedBuffer.length() > 0) {
                stemmedBuffer.append(" ");
            }

            stemmedBuffer.append(splitwords[i]);
        }

        return stemmedBuffer.toString();
    }
}