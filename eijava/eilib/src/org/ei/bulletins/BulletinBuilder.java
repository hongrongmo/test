package org.ei.bulletins;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.domain.DatabaseConfig;

import java.sql.*;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import java.util.*;

public class BulletinBuilder {

    private Perl5Util perl = new Perl5Util();

    /**
     * @param id
     * @return
     */
    public Bulletin buildBulletinDetail(String id) throws Exception {

        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BulletinPage page = new BulletinPage();
        Bulletin bulletin = null;

        try {

            String sql = "select db,cy,yr,fl,zp,wk from bulletins where id = ?";
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                String ctgy = rs.getString("cy");
                String db = rs.getString("db");
                String yr = rs.getString("yr");
                String fl = rs.getString("fl");
                String zpName = rs.getString("zp");
                String wk = rs.getString("wk");

                bulletin = new Bulletin();
                bulletin.setCategory(ctgy);
                bulletin.setDatabase(db);
                bulletin.setYear(yr);
                bulletin.setFileName(fl);
                bulletin.setZipFileName(zpName);
				bulletin.setWeek(wk);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;

        }
        finally {

            close(rs);
            close(pstmt);
            close(con, broker);

        }

        return bulletin;

    }
    /**
     * @param db
     * @param year
     * @param category
     * @return
     */
    public BulletinPage buildBulletinResults(String db, String year, String category) throws Exception {

        ConnectionBroker broker = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BulletinPage page = new BulletinPage();

        try {
            String sql = "select id,cy,pd,fl,zp,db,wk from bulletins where db = ? and cy = ? and yr = ? order by wk desc";
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, db);
            pstmt.setString(2, replaceCategory(db, category));
            pstmt.setInt(3, Integer.valueOf(year).intValue());

            rs = pstmt.executeQuery();

            while (rs.next()) {

                String pubdt = rs.getString("pd");
                String currDb = rs.getString("db");
                String id = rs.getString("id");
                String fileName = rs.getString("fl");
                String zipFileName = rs.getString("zp");
                String cy = rs.getString("cy");
				String wk = rs.getString("wk");

                Bulletin bulletin = new Bulletin();

                bulletin.setId(id);
                bulletin.setDatabase(currDb);
                bulletin.setFileName(fileName);
                bulletin.setPublishedDt(pubdt);
                bulletin.setZipFileName(zipFileName);
                bulletin.setCategory(cy);
                bulletin.setYear(year);
                bulletin.setWeek(wk);

                if (db.equals("1")) //Lit
                    bulletin.setFormat(Bulletin.FORMAT_LIT_RESULTS);
                else //PAT
                    bulletin.setFormat(Bulletin.FORMAT_PAT_RESULTS);

                page.add(bulletin);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;

        }
        finally {

            close(rs);
            close(pstmt);
            close(con, broker);

        }

        return page;

    }
    /**
     * @return
     */
    public BulletinPage buildLITRecent(BulletinPage page, Connection con) throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "select id,db,pd,cy,fl from bulletins where db = '1' and yr = ? order by wk desc";

            pstmt = con.prepareStatement(sql);


            int maxYear = getMaxYear(con);

            pstmt.setInt(1, maxYear);

            rs = pstmt.executeQuery();

            Hashtable htBulletins = new Hashtable();

            while (rs.next()) {

                String db = rs.getString("db");
                String name = rs.getString("fl");
                String id = rs.getString("id");
                String pubdt = rs.getString("pd");
                String category = rs.getString("cy");

                Bulletin bulletin = new Bulletin();

                bulletin.setFormat(Bulletin.FORMAT_LIT_RECENT);
                bulletin.setDatabase(db);
                bulletin.setCategory(category);
                bulletin.setPublishedDt(pubdt);
                bulletin.setId(id);
                bulletin.setFileName(name);
                bulletin.setYear(Integer.toString(maxYear));

                if (!htBulletins.containsKey(category))
                    htBulletins.put(category, bulletin);

                if (htBulletins.size() > 10)
                    break;

            }

            List lstBulletins = new Vector();

            Enumeration keys = htBulletins.keys();

            while (keys.hasMoreElements()) {

                String key = (String) keys.nextElement();

                lstBulletins.add((Bulletin) htBulletins.get(key));
            }

            Collections.sort(lstBulletins, new BulletinComparator());

            for (Iterator iter = lstBulletins.iterator(); iter.hasNext();) {
                Bulletin element = (Bulletin) iter.next();

                page.add(element);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            close(rs);
            close(pstmt);
        }

        return page;

    }
    public BulletinPage buildPATRecent(BulletinPage page, Connection con) throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "select id,db,pd,cy,fl,zp from bulletins where db = '2' and yr = ? order by wk desc";

            pstmt = con.prepareStatement(sql);

            int maxYear = getMaxYear(con);

            pstmt.setInt(1, maxYear);

            rs = pstmt.executeQuery();

            Hashtable htBulletins = new Hashtable();

            while (rs.next()) {

                String db = rs.getString("db");
                String name = rs.getString("fl");
                String id = rs.getString("id");
                String pubdt = rs.getString("pd");
                String category = rs.getString("cy");
                String zipFileName = rs.getString("zp");
                Bulletin bulletin = new Bulletin();

                bulletin.setFormat(Bulletin.FORMAT_PAT_RECENT);
                bulletin.setDatabase(db);
                bulletin.setCategory(category);
                bulletin.setPublishedDt(pubdt);
                bulletin.setId(id);
                bulletin.setFileName(name);
                bulletin.setZipFileName(zipFileName);
                bulletin.setYear(Integer.toString(maxYear));

                if (!htBulletins.containsKey(category))
                    htBulletins.put(category, bulletin);

                if (htBulletins.size() > 10)
                    break;

            }

            List lstBulletins = new Vector();

            Enumeration keys = htBulletins.keys();

            while (keys.hasMoreElements()) {

                String key = (String) keys.nextElement();

                lstBulletins.add((Bulletin) htBulletins.get(key));
            }

            Collections.sort(lstBulletins, new BulletinComparator());

            for (Iterator iter = lstBulletins.iterator(); iter.hasNext();) {
                Bulletin element = (Bulletin) iter.next();

                page.add(element);

            }

        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            close(rs);
            close(pstmt);
        }

        return page;

    }
    class BulletinComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Bulletin a1 = (Bulletin) o1;
            Bulletin a2 = (Bulletin) o2;

            return a1.getCategory().compareTo(a2.getCategory());
        }
    }
    public BulletinPage buildRecentBulletins() throws Exception {

        BulletinPage page = new BulletinPage();

        ConnectionBroker broker = null;
        Connection conn = null;

        try {

            broker = ConnectionBroker.getInstance();
            conn = broker.getConnection(DatabaseConfig.SEARCH_POOL);

            buildLITRecent(page, conn);
            buildPATRecent(page, conn);

        }
        catch (ConnectionPoolException e) {
            e.printStackTrace();
            throw e;
        }
        catch (NoConnectionAvailableException e) {
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            close(conn, broker);
        }

        return page;

    }
    /**
     * @return
     */
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
    /**
     * @return
     */
    private void close(Statement stmt) {

        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @return
     */
    private void close(Connection conn, ConnectionBroker broker) {

        try {
            if (conn != null) {
                broker.replaceConnection(conn, DatabaseConfig.SEARCH_POOL);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getMaxYear(Connection conn) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;


        int maxYear = -1;

        try {
            String sql = "select max(yr) from bulletins";
            stmt = conn.createStatement();

            rs = stmt.executeQuery(sql);

            while (rs.next()) {

                maxYear = rs.getInt(1);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        finally {
            close(rs);
            close(stmt);
        }

        return maxYear;

    }
    private String replaceCategory(String db, String category) {

        List lstTokens = new ArrayList();
        String newCy = "";

        perl.split(lstTokens, "/:/", category);

        if (lstTokens.size() == 2) {

            if (db.equals("1")) //LIT
                newCy = (String) lstTokens.get(0);

            else if (db.equals("2")) //PAT
                newCy = (String) lstTokens.get(1);

        }
        else {
            newCy = category;
        }

        return newCy;
    }

}