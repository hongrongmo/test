package org.ei.data;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class FieldAnalyzer {

    private static TreeMap mfields = new TreeMap();

    public static void main(String args[]) throws Exception {

        String dbtable = args[0];
        String fieldname = args[1];
        String distinct = args[2];
        String outfile = args[3];
        String driver = args[4];
        String url = args[5];
        String user = args[6];
        String pass = args[7];

        FieldAnalyzer fa = new FieldAnalyzer();
        StringTokenizer toks = new StringTokenizer(fieldname, ";");

        for (int a = 0; a < toks.countTokens(); a++) {
            Field f = fa.new Field(dbtable, toks.nextToken(), distinct);
            getFieldsCounts(f, driver, url, user, pass);
        }
        writeFile(outfile);

    }

    public static void writeFile(String fileout) throws Exception {
        FileWriter out = new FileWriter(fileout);
        Iterator itr = mfields.keySet().iterator();

        while (itr.hasNext()) {
            String tmp = (String) itr.next();
            out.write("\n" + tmp + "\t" + mfields.get(tmp));
        }

        out.close();
    }

    public static void getFieldsCounts(Field f, String driver, String url, String user, String pass) throws Exception {
        PreparedStatement pstmt = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url, user, pass);
            pstmt = con.prepareStatement("select " + f.getName() + " from " + f.dbtable + " where " + f.getName() + " is not null");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String fl = rs.getString(f.getName());
                if (fl != null) {
                    StringTokenizer toks = new StringTokenizer(fl, "|");
                    for (int j = 0; j < toks.countTokens(); j++) {
                        String toc = toks.nextToken().trim();
                        if (mfields.containsKey(toc)) {
                            Integer i = (Integer) mfields.get(toc);
                            int t = i.intValue();
                            mfields.put(toc, new Integer(++t));
                        } else {
                            mfields.put(toc, new Integer(1));
                        }
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                con.close();

            } catch (Exception cpe) {
                cpe.printStackTrace();
            }
        }
    }

    private class Field {
        private String dbtable;
        private String name;
        boolean isDistinct;

        Field(String dbtable, String name, String distinct) {
            this.dbtable = dbtable;
            this.name = name;
            this.isDistinct = new Boolean(distinct).booleanValue();
        }

        private String getDbtable() {
            return dbtable;
        }

        private String getName() {
            return name;
        }

        private boolean isDistinct() {
            return isDistinct;
        }
    }
}