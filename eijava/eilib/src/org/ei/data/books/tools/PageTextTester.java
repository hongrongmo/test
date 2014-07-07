package org.ei.data.books.tools;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PageTextTester {

    public static void main(String args[]) throws Exception {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI"; // args[1];
        String username = "AP_PRO1"; // args[2];
        String password = "ei3it"; // args[3];
        Map badIsbns = new HashMap<String,Integer>();
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
            username = "AP_PRO1"; // args[2];
            password = "ei3it"; // args[3];
            con = DriverManager.getConnection(url, username, password);

            stmt = con.createStatement();
            rs = stmt.executeQuery("select BN13, PAGE_TXT, PAGE_NUM FROM BOOK_PAGES_S300 WHERE PAGE_NUM>20 AND PAGE_NUM<30 ORDER BY BN13,PAGE_NUM");
            while (rs.next()) {
                String term = (new PageTextTester()).getStringFromClob(rs.getClob("PAGE_TXT"));
                if(term == null) {
                    String bn13 = rs.getString("BN13");
                    String pg = rs.getString("PAGE_NUM");
                    System.out.println(bn13 + " on Page:" + pg);
                    if(!badIsbns.containsKey(bn13)) {
                        badIsbns.put(bn13,new Integer(1));
                    }
                    else {
                        Integer newcount = (Integer) badIsbns.get(bn13);
                        badIsbns.put(bn13,new Integer(newcount.intValue() + 1));
                    }
                }
            }
            Iterator k = badIsbns.keySet().iterator();
            while(k.hasNext()) {
                String bn13 = (String) k.next();
                Integer count = (Integer) badIsbns.get(bn13);
                if(count > 1) {
                    System.out.println(bn13 + ", " + count);
                }
            }
            
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Exception");
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

        System.out.println("done..");
    }

    public String getStringFromClob(Clob clob) {
        StringBuffer stringbuf = new StringBuffer();
        Reader clobreader = null;
        boolean badchar = false; 
        char[] cbuf = new char[1024];
        if (clob != null) {
            try {
                clobreader = clob.getCharacterStream();
                if (clobreader != null) {
                    int readx = 0;
                    while (readx != -1) {
                        readx = clobreader.read(cbuf, 0, 1024);
                        if (readx != -1) {
                            stringbuf.append(cbuf, 0, readx);
                            int badcount = 0;
                            for (int d = 0; d < readx; d++) {
                                if(((int)cbuf[d] < 32) || ((int)cbuf[d] > 127)) { 
                                    badcount++;
                                }
                                badchar = (badcount > 10);
                            }
                             
                        }
                    }
                }

            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (clobreader != null) {
                        clobreader.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        if(badchar) {
            System.out.println("=======================================================");
            System.out.println(stringbuf.toString());
            System.out.println("=======================================================");
            return null;
        } else {
            return stringbuf.toString();
        }
    }
}