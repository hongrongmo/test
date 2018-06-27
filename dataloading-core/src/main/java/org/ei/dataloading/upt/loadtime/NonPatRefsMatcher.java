package org.ei.dataloading.upt.loadtime;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.ei.domain.FastClient;
import org.ei.xml.Entity;

public class NonPatRefsMatcher {

    public static void main(String[] args) throws Exception {
        loadN = Integer.parseInt(args[3]);
        // String
        // sql="SELECT  rtrim(trim(REPLACE(REPLACE(ref_raw,'&amp;#x201c;','\"'),'&amp;#x201d;','\"')),',') as ref_raw,doi,cpx,ins,cpx as c84,ins as ibf  FROM NON_PAT_REFS WHERE ref_raw  LIKE '% et al., &amp;#x201c;%' AND  ref_raw like '%IEEE%' and load_number IN(71,72)";
        // String
        // sql="SELECT ref_raw, doi,cpx,ins,c84,ibf From non_pat_refs_new where load_number=72 and  ref_raw LIKE '%&amp;#x201c%' and  LOWER(ref_raw) not  LIKE '%brochure%' and LOWER(ref_raw) not LIKE '%patent%' and LOWER(ref_raw) not LIKE '%www.%'and LOWER(ref_raw) not LIKE 'u.s. appl. no.%' and cpx is null and ins is null and ibf is null and c84 is null";
        String sql = "SELECT m_id,ref_raw, doi,cpx,ins,c84,ibf from non_pat_refs where load_number=" + loadN;

        NonPatRefsMatcher m = new NonPatRefsMatcher();
        pwMatch = new PrintWriter(new FileOutputStream("NonPatRef_match_" + loadN + ".log"), true);
        pwLog = new PrintWriter(new FileOutputStream("NonPatRef_" + loadN + ".log"));
        m.getRecords(NonPatRefsMatcher.getConnection(args[0], args[1], args[2]), sql);

    }

    static int loadN = 0;
    static PrintWriter pwMatch = null;
    static PrintWriter pwLog = null;

    private static Connection getConnection(String connectionURL, String username, String password) throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
        Connection con = DriverManager.getConnection(connectionURL, username, password);
        con.setAutoCommit(true);
        return con;
    }

    public void getRecords(Connection conn, String sql) throws IOException {
        ResultSet rec = null;
        try {

            int count = 0;
            Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            rec = stmt.executeQuery(sql);
            pwLog.println("Got Records ...");
            while (rec.next()) {
                pwLog.println("----------------------------------------------------------\n");

                // System.out.println(rec.getString("REF_RAW"));
                String ref = rec.getString("REF_RAW");
                NonPatRef r = new NonPatRef(ref);
                if (r.hasTitleKey() || r.hasIVIP()) {
                    Hashtable<String, String> m = matchFast(buildFastQuery(r));
                    pwLog.println(r);

                    if (m != null) {
                        count++;
                        Enumeration<String> e = m.keys();
                        StringBuffer sbUpdate = new StringBuffer("update non_pat_refs set ");
                        while (e.hasMoreElements()) {
                            String field = (String) e.nextElement();
                            rec.updateString(field.toUpperCase(), (String) m.get(field));
                            sbUpdate.append(field.toUpperCase()).append("='").append((String) m.get(field)).append("', ");
                        }
                        sbUpdate.deleteCharAt(sbUpdate.lastIndexOf(",")).append(" where m_id='").append(rec.getString("M_ID")).append("';");
                        pwMatch.println(sbUpdate.toString());
                        rec.updateRow();
                        if (count % 500 == 0) {
                            conn.commit();
                            pwMatch.println("commit;");
                        }
                    }
                } else
                    pwLog.println("Not Matched*******:" + r.getRef());

            }
        } catch (Exception ex) {

            ex.printStackTrace();

        }

    }

    public Hashtable<String, String> matchFast(String fastQuery) throws Exception {

        FastClient client = new FastClient();
        client.setBaseURL("http://rei.bos3.fastsearch.net:15100");
        client.setResultView("ei");
        client.setOffSet(0);
        client.setPageSize(25);
        client.setQueryString(fastQuery);
        client.setDoCatCount(false);
        client.setDoNavigators(false);
        client.setPrimarySort("ausort");
        client.setPrimarySortDirection("+");
        client.search();
        List<?> l = client.getDocIDs();
        pwLog.println("Search size:[" + l.size() + "]" + fastQuery);
        Hashtable<String, String> t = null;
        if (l.size() == 1 || l.size() == 2) {
            t = new Hashtable<String, String>();
            for (int i = 0; i < l.size(); i++) {
                String[] docID = (String[]) l.get(i);

                pwLog.println(docID[0] + "|" + docID[1] + "|" + docID[2]);

                if (!t.containsKey("doi") && docID[2] != null)
                    t.put("doi", docID[2]);
                if (!t.containsKey(docID[0].substring(0, 3)))
                    t.put(docID[0].substring(0, 3), docID[0]);

            }
        }

        return t;
    }

    public String buildFastQuery(NonPatRef ref) {  // (au:"Smith" AND ti:water AND vo:2 AND su:6 AND yr:2004 AND sp:935) AND (((db:cpx OR db:c84))) -->
        StringBuffer fastQuery = new StringBuffer();

        if (ref.hasTitleKey()) {

            fastQuery.append("(ti:\"").append(Entity.prepareString(ref.getTitle())).append("\")");
            fastQuery.append(" AND (yr:").append(ref.getYear()).append(") ");
            fastQuery.append(" AND (au:").append(ref.getFirstAuthor()).append(") ");
            fastQuery.append(" AND (db:cpx OR db:ins OR db:ibf OR db:c84)");

        } else if (ref.hasIVIP()) {
            fastQuery.append(" (sn:").append(ref.getISSN()).append(" or bn:").append(ref.getISBN()).append(") ");
            fastQuery.append(" AND (vo:").append(ref.getVolume()).append(") ");
            fastQuery.append(" AND (sp:").append(ref.getStartPage()).append(") ");
            fastQuery.append(" AND (db:cpx OR db:ins OR db:ibf OR db:c84)");
        }

        return fastQuery.toString();
    }

    //HH 12/22/2014 from eijava part
    private void close(Statement stmt)
    {
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void close(ResultSet rs) {

        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close(Connection conn) {

        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


}
