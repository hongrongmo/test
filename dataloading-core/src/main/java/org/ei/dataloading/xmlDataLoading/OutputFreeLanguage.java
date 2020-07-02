package org.ei.dataloading.xmlDataLoading;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OutputFreeLanguage {
    public static String driver = "oracle.jdbc.driver.OracleDriver";
    public static String url = "jdbc:oracle:thin:@206.137.75.51:1521:EI";
    public static String user = "AP_EV_SEARCH";
    public static String password = "";

    public static void main(String[] args) throws Exception {
        String format = "";
        String year = "2006";
        String[] yearArray = null;
        FileWriter outputFile = null;

        if (args.length > 0) {
            format = args[0];
        }

        if (args.length > 1) {
            year = args[1];
        }

        OutputFreeLanguage OFL = new OutputFreeLanguage();

        if (year.indexOf(";") > 0) {
            yearArray = year.split(";");
        } else {
            yearArray = new String[1];
            yearArray[0] = year;
        }

        for (int i = 0; i < yearArray.length; i++) {
            String filename = "Year" + yearArray[i] + "OrderBy" + format + ".txt";
            outputFile = new FileWriter(filename);
            OFL.getResults(outputFile, yearArray[i], format);
        }
        outputFile.flush();
        outputFile.close();

        System.exit(1);
    }

    public void writingFile(CPXFreeLanguageObject cflo, FileWriter outputFile) {
        try {
            outputFile.write(cflo.getFreeLanguage().trim() + "|");
            outputFile.write(cflo.getCount() + "|");
            outputFile.write(cflo.getAccessionNumber() + "|");
            outputFile.write(cflo.getYear() + "|");
            outputFile.write(cflo.getClassificationCode() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception= " + e);
        }
    }

    public void getResults(FileWriter outputFile, String year, String format) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlString = "select b.count COUNT, a.* from CPXPARSEDFREELANGUAGE a, (select upper(fls) fls,count(upper(fls)) count from CPXPARSEDFREELANGUAGE group by upper(fls)) b where upper(a.fls)=upper(b.fls) and a.yr='"
            + year + "' ";
        if (format.equals("FLS")) {
            sqlString = sqlString + " order by upper(a.fls)";
        } else if (format.equals("COUNT")) {
            sqlString = sqlString + " order by count";
        }

        System.out.println("sqlString= " + sqlString);
        try {
            CPXFreeLanguageObject cflo = null;
            con = getConnection();
            pstmt = con.prepareStatement(sqlString);
            rs = pstmt.executeQuery();

            int i = 1;
            while (rs.next()) {
                if (year.equals(rs.getString("YR"))) {
                    cflo = new CPXFreeLanguageObject();
                    cflo.setYear(rs.getString("YR"));
                    cflo.setCount(rs.getInt("COUNT"));
                    cflo.setRowID(rs.getRow());
                    cflo.setAccessionNumber(rs.getString("EX") == null ? "" : rs.getString("EX"));
                    cflo.setFreeLanguage(rs.getString("FLS") == null ? "" : rs.getString("FLS"));
                    cflo.setClassificationCode(rs.getString("CLS") == null ? "" : rs.getString("CLS"));
                    writingFile(cflo, outputFile);
                    if (i > 1000) {
                        outputFile.flush();
                        i = 0;
                    }
                    i++;
                }
            }
            outputFile.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                    pstmt = null;
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    con.close();
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }

            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }

            }
        }
    }

    private Connection getConnection() throws Exception {
        Class.forName(OutputFreeLanguage.driver);
        Connection con = DriverManager.getConnection(OutputFreeLanguage.url, OutputFreeLanguage.user, OutputFreeLanguage.password);
        return con;
    }

}
