package org.ei.data;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CreatingStoredPackage {
    public static void main(String[] args) {
        String url = null;
        String username = null;
        String password = null;
        String driver = null;
        String fileName = null;
        fileName = "systemPackageHeader.sql|systemPackageBody.sql|sessionPackageHeader.sql|sessionPackageBody.sql|thesaurusHeader.sql|thesaurusBody.sql|personalizationHeader.sql|personalizationBody.sql|domainHeader.sql|domainBody.sql";

        if (args.length >= 4) {
            url = args[0];
            username = args[1];
            password = args[2];
            driver = args[3];
            if (args.length >= 5) {
                fileName = args[4];
            }
        }
        System.out.println("URL= " + url);
        System.out.println("USERNAME= " + username);
        System.out.println("PASSWORD= " + password);
        System.out.println("DRIVER= " + driver);
        CreatingStoredPackage cs = new CreatingStoredPackage();
        cs.entryPoint(url, username, password, driver, fileName);
        System.exit(10000);

    }

    public void entryPoint(String url, String username, String password, String driver, String fileName) {
        Connection con = null;
        List<String> headerList = new ArrayList<String>();
        List<String> bodyList = new ArrayList<String>();
        String name = null;
        try {
            con = getDbCoonection(url, username, password, driver);
            createInitialPocedure(con);
            StringTokenizer t = new StringTokenizer(fileName, "|");
            while (t.hasMoreTokens()) {
                name = t.nextToken();
                if (name.indexOf("Header") > 0) {
                    headerList.add(name);
                } else {
                    bodyList.add(name);
                }
            }
            if (headerList.size() != bodyList.size()) {
                throw new Exception("Size of the header files should be equal to size of the body file");
            }
            for (int i = 0; i < headerList.size(); i++) {
                runQuery(con, (String) headerList.get(i));
            }

            for (int i = 0; i < bodyList.size(); i++) {
                runQuery(con, (String) bodyList.get(i));
            }

        } catch (Exception e) {
            System.out.println("exception from main " + e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    System.out.println("Problem with closing connection " + e);
                }
            }
        }
    }

    public void runQuery(Connection con, String fileName) {
        System.out.println("creating package " + fileName);
        PreparedStatement pstmt = null;
        String pString = null;
        pString = readData(fileName);
        try {
            pstmt = (CallableStatement) con.prepareCall("{ call CREATEPACKAGE(?)}");
            pstmt.setString(1, pString);
            pstmt.execute();
        } catch (Exception sqle) {
            System.out.println("Exception = " + sqle);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception sqle) {
                }
            }

        }
    }

    public String readData(String fileName) {

        String line = null;
        StringBuffer buffer = new StringBuffer();
        DataInputStream in = null;

        try {
            in = new DataInputStream(new FileInputStream("C:/jamaica/eijava/database/" + fileName));

            while ((line = in.readLine()) != null) {
                buffer.append(line + " ");
            }

        } catch (IOException e) {
            System.out.println("Exception from CreatingStoredPackage.readData " + e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception sqle) {
                }
            }

        }
        return buffer.toString();
    }

    public void createInitialPocedure(Connection con) {
        System.out.println("Runing CreatingStoredPackage.createInitialPocedure");
        PreparedStatement pstmt = null;
        StringBuffer sqlString = new StringBuffer();
        sqlString.append(" create or replace procedure createPackage(passingQuery varchar2) as");
        sqlString.append(" begin	");
        sqlString.append(" execute immediate passingQuery;");
        sqlString.append(" commit;");
        sqlString.append(" EXCEPTION");
        sqlString.append(" WHEN OTHERS THEN");
        sqlString.append(" ROLLBACK;");
        sqlString.append(" RAISE_APPLICATION_ERROR(-20101, 'Error in procedure CreatingStoredPackage.createInitialPocedure, error='||sqlerrm);");
        sqlString.append(" end;");
        try {
            pstmt = con.prepareStatement(sqlString.toString());
            pstmt.execute();
        } catch (Exception sqle) {
            System.out.println("Exception = " + sqle);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Exception sqle) {
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
