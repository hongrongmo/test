package org.ei.data.test;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;

public class BDDataTest {
    String tableName = "bd_master";
    String old_IVIPTableName = "enter table name here";
    String new_IVIPTableName = "enter table name here";
    private String[] numberPatterns = { "/[1-9][0-9]*/" };
    private Perl5Util perl = new Perl5Util();

    public static void main(String[] args) {
        String year = "2007";
        String url = "jdbc:oracle:thin:@jupiter.elsevier.com:1521:EIDB1";
        String userName = "ap_pro1";
        String password = "ei3it";
        Connection con = null;
        try {
            if (args.length > 0) {
                year = args[0];
            }

            if (args.length > 3) {
                url = args[1];
                userName = args[2];
                password = args[3];
            }
            BDDataTest bd = new BDDataTest();
            con = bd.getConnection(url, userName, password);
            // bd.checkData(con,year);
            bd.updateMID1(con, year);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void updateMID(Connection con, String year) {
        String sql = "select m_id,accessnumber,doi from " + tableName + " where publicationyear='" + year + "'";
        String sql2 = "select m_id,ex,do from cpx_doi where do=?";
        String sql3 = "update " + tableName + " set m_id =? where accessnumber=? or doi=?";
        new StringBuffer("update " + tableName + " set m_id ='");
        FileWriter out = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        ResultSet rs = null;
        try {
            out = new FileWriter("/temp/missingRecords_" + year + ".txt");
            stmt = con.prepareStatement(sql);
            stmt.execute();
            rs = stmt.getResultSet();
            int i = 0;
            String accessionNumber = null;
            String doi = null;
            String mid = null;
            stmt2 = con.prepareStatement(sql3);
            stmt1 = con.prepareStatement(sql2);
            while (rs.next()) {
                accessionNumber = rs.getString("accessnumber");
                if (accessionNumber != null && accessionNumber.length() > 12) {
                    accessionNumber = accessionNumber.substring(2);
                }
                doi = rs.getString("doi");

                mid = getData(stmt1, accessionNumber);

                if (mid == null && doi != null) {
                    // System.out.println("DOI "+doi);

                    mid = getData(stmt1, doi);
                }

                if (mid != null) {
                    // while(cpxData.next())
                    {
                        stmt2.setString(1, mid);
                        stmt2.setString(2, rs.getString("accessnumber"));
                        stmt2.setString(3, doi);
                        stmt2.addBatch();

                    }
                } else {
                    // System.out.println("accessionNumber "+accessionNumber);
                    out.write(rs.getString("accessnumber") + "\n");
                }
                if (i > 20) {
                    stmt2.executeBatch();
                    i = 0;
                }
                i++;

            }
            stmt2.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (stmt1 != null) {
                    stmt1.close();
                }
                if (stmt2 != null) {
                    stmt2.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateMID1(Connection con, String year) {
        StringBuffer sql = new StringBuffer("select a.ex oldAccessNumber,a.do oldDoi,");
        sql.append("a.m_id oldMID,b.accessNumber newAccessNumber,b.doi newDoi ");
        sql.append(" from cpx_master a,bd_master b ");
        sql.append(" where a.ex = substr(b.accessNumber,3) and b.publicationYear='" + year + "'");

        StringBuffer updateBuffer = null;
        FileWriter out = null;
        PreparedStatement stmt = null;

        ResultSet rs = null;
        try {
            out = new FileWriter("/temp/updateRecords_" + year + ".txt");
            stmt = con.prepareStatement(sql.toString());
            stmt.execute();
            rs = stmt.getResultSet();
            int i = 0;
            String newAccessionNumber = null;
            String oldDoi = null;
            String oldMid = null;
            String newDoi = null;

            while (rs.next()) {
                newAccessionNumber = rs.getString("newAccessNumber");
                newDoi = rs.getString("newDoi");
                oldDoi = rs.getString("oldDoi");
                oldMid = rs.getString("oldMID");
                updateBuffer = new StringBuffer("update " + tableName + " set m_id ='" + oldMid + "'");

                if (newDoi == null && oldDoi != null) {
                    updateBuffer.append(" ,doi='" + oldDoi + "'");
                }

                updateBuffer.append(" where accessNumber='" + newAccessionNumber + "';");
                out.write(updateBuffer.toString() + "\n");
                if (i > 20) {
                    out.write("commit;\n");
                    i = 0;
                }
                i++;
            }
            out.write("commit;\n");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateMIDWithIVIP(Connection con, String year) {
        String sql = "select m_id,ex,do,vo,iss,pp,sn from " + old_IVIPTableName;
        String sql3 = null;
        ResultSet cpxData = null;
        FileWriter out = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        ResultSet rs = null;
        try {
            out = new FileWriter("/temp/missingRecords_" + year + ".txt");
            stmt = con.prepareStatement(sql);
            stmt.execute();
            rs = stmt.getResultSet();
            int i = 0;
            String mid = null;

            // CompendexDOIFinder df = new CompendexDOIFinder();
            while (rs.next()) {
                rs.getString("ex");
                rs.getString("do");
                mid = rs.getString("m_id");
                String issn = getIssn(rs.getString("issn"));
                String volume = getFirstVolume(rs.getString("volume"));
                String issue = getFirstIssue(rs.getString("issue"));
                String page = getFirstPage(rs.getString("page"));
                sql3 = getUpdateStatement(issn, volume, issue, page, mid);
                System.out.println("UPDATE " + sql3);
                stmt2 = con.prepareStatement(sql3);

                if (cpxData != null) {
                    stmt2.addBatch();
                    i++;
                } else {
                    // out.write(accessionNumber+"\n");
                }
                if (i > 20) {
                    stmt2.executeBatch();
                    i = 0;
                }

            }
            stmt2.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (stmt1 != null) {
                    stmt1.close();
                }
                if (stmt2 != null) {
                    stmt2.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getUpdateStatement(String issn, String volume, String issue, String page, String m_id) {
        StringBuffer sqlString = new StringBuffer("update " + new_IVIPTableName + " set m_id='" + m_id + "' where ");
        sqlString.append(" issn='" + issn + "'");
        sqlString.append(" and subStr(volumn,1," + volume.length() + ")='" + volume + "'");
        sqlString.append(" and subStr(issue,1," + issue.length() + ")='" + issue + "'");
        sqlString.append(" and subStr(page,1," + page.length() + ")='" + page + "'");
        return sqlString.toString();
    }

    public void checkData(Connection con, String year) {
        String sql = "select * from " + tableName + " where loadnumber='" + year + "00'";
        System.out.println("SQL " + sql);
        String sql2 = "select * from cpx_master where ex=?";

        ResultSet cpxData = null;

        FileWriter out = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt1 = null;
        ResultSet rs = null;
        try {
            out = new FileWriter("/temp/bddatatest_" + year + ".txt");
            stmt = con.prepareStatement(sql);
            stmt.execute();
            rs = stmt.getResultSet();
            int i = 0;
            while (rs.next()) {

                String accessionNumber = rs.getString("accessnumber");
                out.write("************ " + accessionNumber + " ************\n");
                if (accessionNumber != null && accessionNumber.length() > 12) {
                    accessionNumber = accessionNumber.substring(2);
                }
                String doi = rs.getString("doi");
                rs.getString("pii");
                String title = rs.getString("citationtitle");
                String author = rs.getString("author");
                String author1 = rs.getString("author_1");
                if (author1 != null) {
                    author = author + author1;
                }
                rs.getString("issn");
                rs.getString("isbn");
                rs.getString("volume");
                rs.getString("issue");
                rs.getString("page");
                stmt1 = con.prepareStatement(sql2);

                cpxData = getData(stmt1, accessionNumber, doi);

                if (cpxData != null) {
                    while (cpxData.next()) {
                        checkAccessionNumber(out, accessionNumber, cpxData.getString("ex"));
                        checkDoi(out, doi, cpxData.getString("do"));
                        // checkPii(out,pii,cpxData.getString("pi"));
                        checkTitle(out, title, cpxData.getString("ti"), accessionNumber);
                        // checkAuthor(out,author,cpxData.getString("aus"));
                        // checkIssn(out,issn,cpxData.getString("sn"));
                        // checkIsbn(out,isbn,cpxData.getString("bn"));
                        // checkVolume(out,volume,cpxData.getString("vo"));
                        // checkIssue(out,issue,cpxData.getString("iss"));
                        // checkPage(out,page,cpxData.getString("pp"));
                    }

                } else {
                    out.write("record not exist " + accessionNumber + " \n");
                }
                if (stmt1 != null) {
                    stmt1.close();
                }
                if (cpxData != null) {
                    cpxData.close();
                }
                i++;
                if (i > 500000) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (stmt1 != null) {
                    stmt1.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (out != null) {
                    out.close();
                }

                if (cpxData != null) {
                    cpxData.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkAccessionNumber(FileWriter out, String newAccessionNumber, String oldAccessionNumber) throws Exception {
        if (!newAccessionNumber.equalsIgnoreCase(oldAccessionNumber)) {
            out.write("ACCESSNUMBER " + newAccessionNumber + " cpx_master " + oldAccessionNumber + "\n");
        }
    }

    private void checkDoi(FileWriter out, String newDoi, String oldDoi) throws Exception {
        if (newDoi != null && oldDoi != null && !newDoi.equalsIgnoreCase(oldDoi)) {
            out.write("Doi  bd_master- " + newDoi + " cpx_master- " + oldDoi + "\n");
        } else if (newDoi == null || oldDoi == null) {
            out.write("Doi  bd_master- " + newDoi + " cpx_master- " + oldDoi + "\n");
        }
    }

    @SuppressWarnings("unused")
    private void checkPii(FileWriter out, String newPii, String oldPii) throws Exception {
        if (newPii != null && oldPii != null && !newPii.equalsIgnoreCase(oldPii)) {
            out.write("Pii  bd_master- " + newPii + " cpx_master- " + oldPii + "\n");
        } else if (newPii == null || oldPii == null) {
            out.write("Pii  bd_master- " + newPii + " cpx_master- " + oldPii + "\n");
        }
    }

    private void checkTitle(FileWriter out, String newTitle, String oldTitle, String accessionNumber) throws Exception {

        if (newTitle != null && !newTitle.equalsIgnoreCase(oldTitle)) {

            out.write("Title  " + accessionNumber + " new title " + newTitle + " cpx_master- " + oldTitle + "\n");
        } else if (newTitle == null) {

            out.write("Title  " + accessionNumber + " bd_master- is null cpx_master- " + oldTitle + "\n");
        }
    }

    @SuppressWarnings("unused")
    private void checkAuthor(FileWriter out, String newAuthor, String oldAuthor) throws Exception {

    }

    @SuppressWarnings("unused")
    private void checkIssn(FileWriter out, String newIssn, String oldIssn) throws Exception {
        if (newIssn != null && oldIssn != null && !newIssn.equalsIgnoreCase(oldIssn)) {
            out.write("Issn  bd_master- " + newIssn + " cpx_master- " + oldIssn + "\n");
        } else if (newIssn == null || oldIssn == null) {
            out.write("Issn  bd_master- " + newIssn + " cpx_master- " + oldIssn + "\n");
        }
    }

    @SuppressWarnings("unused")
    private void checkIsbn(FileWriter out, String newIsbn, String oldIsbn) throws Exception {
        out.write("Isbn  bd_master- " + newIsbn + " cpx_master- " + oldIsbn + "\n");
    }

    @SuppressWarnings("unused")
    private void checkVolume(FileWriter out, String newVolume, String oldVolume) throws Exception {
        out.write("Volume  bd_master- " + newVolume + " cpx_master- " + oldVolume + "\n");
    }

    @SuppressWarnings("unused")
    private void checkIssue(FileWriter out, String newIssue, String oldIssue) throws Exception {
        out.write("Issue  bd_master- " + newIssue + " cpx_master- " + oldIssue + "\n");
    }

    @SuppressWarnings("unused")
    private void checkPage(FileWriter out, String newPage, String oldPage) throws Exception {
        out.write("Page  bd_master- " + newPage + " cpx_master- " + oldPage + "\n");
    }

    @SuppressWarnings("unused")
    private void updateRecord(Connection con, String mid, String accessnumber) throws Exception {
        PreparedStatement stmt = con.prepareStatement("update " + tableName + " set m_id ='" + mid + "' where accessnumber='" + accessnumber + "'");
        try {
            stmt.execute();
        } catch (Exception sqle) {
            sqle.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private String getData(PreparedStatement stmt, String key) throws Exception {
        ResultSet rs = null;
        String mid = null;
        try {
            stmt.setString(1, key);
            stmt.execute();
            rs = stmt.getResultSet();
            while (rs.next()) {
                mid = rs.getString("m_id");
            }

        } catch (Exception sqle) {
            sqle.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }

        return mid;
    }

    private ResultSet getData(PreparedStatement stmt, String key1, String key2) throws Exception {
        ResultSet rs = null;
        try {
            stmt.setString(1, key1);
            // stmt.setString(2, key2);
            stmt.execute();
            rs = stmt.getResultSet();
        } catch (Exception sqle) {
            sqle.printStackTrace();
        }

        if (!rs.next()) {
            return null;
        }

        return rs;
    }

    protected String getIssn(String issn) {
        if (issn == null) {
            return null;
        }

        if (issn.length() == 9) {
            return issn.substring(0, 4) + issn.substring(5, 9);
        } else if (issn.length() == 8) {
            return issn;
        }

        return issn;
    }

    protected String getFirstIssue(String issue) {

        if (issue != null) {
            if (perl.match("/(\\d+)/", issue)) {
                return (String) (perl.group(0).toString());
            }
        }

        return null;
    }

    protected String getFirstVolume(String volume) {

        if (volume != null) {
            if (perl.match("/(\\d+)/", volume)) {
                return (String) (perl.group(0).toString());
            }
        }

        return null;
    }

    protected String getFirstPage(String pages) {
        StringBuffer retStr = new StringBuffer();
        String firstPage = null;
        if (pages != null) {
            StringTokenizer tmpPage = new StringTokenizer(pages, "-");
            if (tmpPage.countTokens() > 0) {
                firstPage = tmpPage.nextToken();
            } else {
                firstPage = pages;
            }

            for (int x = 0; x < numberPatterns.length; ++x) {
                String pattern = numberPatterns[x];
                if (perl.match(pattern, firstPage)) {
                    MatchResult mResult = perl.getMatch();
                    retStr.append(mResult.toString());
                    break;
                }
            }
            return retStr.toString();
        }

        return null;

    }

    public Connection getConnection(String url, String userName, String password) {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            con = DriverManager.getConnection(url, userName, password);

        } catch (Exception sqle) {
            sqle.printStackTrace();
        }
        return con;

    }

}