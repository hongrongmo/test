/*
 * Created on Oct 28, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.dataloading.upt.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author KFokuo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UPTRefCounter {


    int count = 0;
    private static final String setURL = "jdbc:oracle:thin:@neptune.elsevier.com:1521:EI";
    private static final String setUserName = "ap_pro1";
    private static final String setPassword = "ei3it";

    public void generateCounts(String dir) {

        ResultSet rs = null;
        Statement stmt = null;

        Connection con = null;
        PrintWriter countsOut = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            countsOut = new PrintWriter(new FileWriter(dir), true);

            con = DriverManager.getConnection(setURL, setUserName, setPassword);
            stmt = con.createStatement();

            String sql = "select m_id from upt_master";
            stmt.execute(sql);
            rs = stmt.getResultSet();

            while (rs.next()) {
                String m_id = rs.getString("m_id");

                writeCounts(m_id, countsOut);

            }

        }
        catch (Exception sqle) {
            sqle.printStackTrace();
        }
        finally {
            close(rs);
            close(stmt);
            close(con);
            close(countsOut);
        }
    }
    public void writeCounts(String mid, PrintWriter countsOut) {

        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        Connection con = null;

        try {

            con = DriverManager.getConnection(setURL, setUserName, setPassword);
            pstmt = con.prepareStatement("select count(*) as count from patent_refs_new where prt_mid = ?");
            pstmt.setString(1, mid);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();

            if (rs.next()) {

                String refCnt = rs.getString("count");
                if (refCnt != null && !refCnt.equals("0")) {
                    ++count;
                    countsOut.println("update upt_master set ref_cnt = " + refCnt + " where m_id = '"+mid+"';");
                    if (count % 500 == 0)
                        countsOut.println("commit;");
                }

            }
            pstmt1 = con.prepareStatement("select count(*) as count from patent_refs_new where cit_mid = ?");
            pstmt1.setString(1, mid);
            pstmt1.executeQuery();
            ResultSet rs2 = pstmt1.getResultSet();

            if (rs2.next()) {

                String citCnt = rs2.getString("count");
                if (citCnt != null && !citCnt.equals("0")) {
                    ++count;
                    countsOut.println("update upt_master set cit_cnt = " + citCnt + " where m_id = '"+mid+"';");
                    if (count % 500 == 0)
                        countsOut.println("commit;");
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            close(rs);
            close(rs1);
            close(pstmt);
            close(pstmt1);
            close(con);
        }
    }

    public void close(ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public void close(PrintWriter countsOut) {

        if (countsOut != null) {
            try {
                countsOut.close();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public void close(Statement stmt) {

        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public void close(Connection conn) {

        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        new UPTRefCounter().generateCounts(args[0]);
    }

}
