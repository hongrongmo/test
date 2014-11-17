package org.ei.data.test;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class BdDataMids {
    String tableName = "bd_master";
    String old_IVIPTableName = "enter table name here";
    String new_IVIPTableName = "enter table name here";

    public static Hashtable<String, Object> htproblems = new Hashtable<String, Object>();

    private String findCpxMid(BdData bd, HashMap<String, BdData> cpxMap) {
        // iterate cpxMap
        // if more than one result - to write to the file
        // if only one result - add to the map return cpxMap value - cpsMid;

        int counter = 0;
        String cpxMid = null;
        Iterator<String> itr = cpxMap.keySet().iterator();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            BdData cpx = (BdData) cpxMap.get(key);
            if (bd.check(cpx)) {
                cpxMid = key;
                bd.changeUpdateFlag(cpx);
                counter++;
            }

        }
        if (counter == 1)  // found match
        {
            // remove from cpxmap
            cpxMap.remove(cpxMid);
            return cpxMid;
        } else if (counter > 1) {
            // more than one match
            // add it to problems table
            htproblems.put(cpxMid, cpxMid);
        } else {
            // no match
            return null;
        }

        return null;

    }

    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@206.137.75.51:1521:EI";
        String userName = "ap_pro1";
        String password = "ei3it";

        Connection con = null;

        String tableCpxRecords = "cpx_subset";
        Hashtable<String, Object> htUpdateMid = new Hashtable<String, Object>();
        HashMap<String, BdData> cpxMap = new HashMap<String, BdData>();
        String sqlCpx = "select * from " + tableCpxRecords;

        try {
            StringBuffer sqlCpxMap = new StringBuffer();
            sqlCpxMap.append(sqlCpx);
            if (args.length > 0) {
                int yearsCount = args.length;
                sqlCpxMap.append(" where yr in (");

                for (int m = 0; m < yearsCount - 1; m++) {
                    sqlCpxMap.append("'" + args[m] + "', ");
                }
                sqlCpxMap.append(" '" + args[yearsCount - 1] + "' ) ");
            }

            System.out.println("Sql cpx stmt::" + sqlCpxMap.toString());
            BdDataMids bd = new BdDataMids();
            con = bd.getConnection(url, userName, password);

            // get record from CPXtable search with Bdtable ,
            // if fields are equal, midBD midCPX to htUpdateMid hashtable
            // print htUpdateMid to the file for later run updates using sqllader

            // from CPX saved_record create HashMap citationTitle, m_id
            // get rs of 2 500 000 from bd_master subset
            // for each bd record search for match in CPX hashmap
            bd.setCpxHashmap(con, cpxMap, tableCpxRecords, sqlCpxMap.toString());

            System.out.println("cpxMap size::" + cpxMap.size());
            String tableBdRecords = "nonmatchan";
            if (cpxMap.size() != 0) {

                bd.getMids(con, tableBdRecords, htUpdateMid, cpxMap);
            }

            // this is temp printout

            // printResult(htUpdateMid);
            StringBuffer years = new StringBuffer();
            if (args.length > 0) {
                years.append(args[0] + "_" + args[args.length - 1]);
                bd.writeSqlLoaderFile(htUpdateMid, years.toString());
                years.append("_problems");
                bd.writeSqlLoaderFile(htproblems, years.toString());
            } else
                System.out.println("NO YEAR(S) PASSED IN ARGS");

            // write to bulk update bd_master table file with cpx_mids

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

    public static void printResult(Hashtable<?, ?> htUpdateMid) {
        // temp part to test result
        System.out.println("result set size::" + htUpdateMid.size());
        Iterator<?> itrtmp = htUpdateMid.keySet().iterator();
        String updateString = null;
        while (itrtmp.hasNext()) {
            String cpxMid = (String) itrtmp.next();
            BdData bd = (BdData) htUpdateMid.get(cpxMid);
            updateString = "update nomatchAN set m_id='" + cpxMid + "',updateFlag='" + bd.getUpdateFlag() + "' where m_id='" + bd.getMid() + "';";

            System.out.println(updateString);
        }
    }

    public void setCpxHashmap(Connection con, HashMap<String, BdData> cpxMap, String tableCpxRecords, String sqlCpxMap) {
        ResultSet rsCpx = null;
        PreparedStatement stmtCpx = null;
        try {
            stmtCpx = con.prepareStatement(sqlCpxMap);
            rsCpx = stmtCpx.executeQuery();
            while (rsCpx.next()) {
                if (rsCpx.getString("ti") != null) {
                    BdData cpx = new BdData();

                    // fields to test for now ti and issn

                    cpx.setCitationTitle(rsCpx.getString("ti"));
                    cpx.setIssn(rsCpx.getString("sn"));
                    cpx.setMid("m_id");
                    cpx.setVolume(rsCpx.getString("vo"));
                    cpx.setIssue(rsCpx.getString("iss"));
                    cpx.setPage(rsCpx.getString("xp"));
                    cpx.setPublicationYear(rsCpx.getString("yr"));
                    cpxMap.put(rsCpx.getString("m_id"), cpx);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rsCpx != null) {
                    rsCpx.close();
                }
                if (stmtCpx != null) {
                    stmtCpx.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getMids(Connection con, String tableBdRecords, Hashtable<String, Object> htUpdateMid, HashMap<String, BdData> cpxMap) {
        String sqlBd = "select * from " + tableBdRecords;

        ResultSet rsBd = null;

        PreparedStatement stmtBd = null;

        try {
            stmtBd = con.prepareStatement(sqlBd);
            rsBd = stmtBd.executeQuery();

            while (rsBd.next()) {
                BdData bd = new BdData();
                if (rsBd.getString("citationtitle") != null) {
                    setBdData(bd, rsBd);

                    String midCpx = findCpxMid(bd, cpxMap);

                    if (midCpx != null) {
                        htUpdateMid.put(midCpx, bd);
                    }

                    // System.out.println(cittitleCpx + "::cittitleCpx");
                    // BdCitationTitle bct = new BdCitationTitle(rsBd.getString("ti"));
                    // String cittitleBd = ((BdCitationTitle) bct.getCitationTitle().get(0)).getTitle();
                    // System.out.println("BD cit title::"+ cittitleBd);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rsBd != null) {
                    rsBd.close();
                }

                if (stmtBd != null) {
                    stmtBd.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setBdData(BdData bd, ResultSet rs) {
        try {
            bd.setCitationTitle(rs.getString("CITATIONTITLE"));
            bd.setIssn(rs.getString("issn"));
            bd.setMid(rs.getString("m_id"));
            bd.setVolume(rs.getString("volume"));
            bd.setIssue(rs.getString("issue"));
            bd.setPage(rs.getString("page"));
            bd.setPublicationYear(rs.getString("PublicationYear"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // bdo.setDoi(rs.getString("doi"));
        // bdo.setPii(rs.getString("pii"));

        // bdo.setAuthor(rs.getString("author"));
        // bdo.setAuthor1(rs.getString("author_1"));
        // bdo.setIssn(rs.getString("issn"));
        // bdo.setIsbn(rs.getString("isbn"));
        // bdo.setVolume(rs.getString("volume"));
        // bdo.setIssue(rs.getString("issue"));
        // bdo.setPage(rs.getString("page"));

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

    public void writeSqlLoaderFile(Hashtable<String, Object> htUpdateMid, String year) {
        try {
            int i = 0;
            Iterator<String> itrtmp = htUpdateMid.keySet().iterator();
            FileWriter out = null;
            out = new FileWriter("./temp/midUpdates_" + year + ".sql");
            while (itrtmp.hasNext()) {
                String cpxMid = (String) itrtmp.next();
                if (htUpdateMid.get(cpxMid) != null) {
                    Object o = (Object) htUpdateMid.get(cpxMid);
                    if (o.getClass().getName().equalsIgnoreCase("org.ei.data.test.BdData")) {
                        BdData bd = (BdData) htUpdateMid.get(cpxMid);
                        out.write("update nomatchAN set m_id='" + cpxMid + "',updateFlag='" + bd.getUpdateFlag() + "' where m_id='" + bd.getMid() + "';\n");
                        i++;
                        if (i > 20) {
                            out.write("commit;\n");
                            i = 0;
                        }
                    } else {
                        out.write("Redundant citation titles");
                        if (o.getClass() != null && o.getClass().getName() != null) {
                            out.write("m_id='" + cpxMid + "';\n");
                        }
                    }
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}