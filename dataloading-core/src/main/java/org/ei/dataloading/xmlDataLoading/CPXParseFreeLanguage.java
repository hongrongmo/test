package org.ei.dataloading.xmlDataLoading;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;

public class CPXParseFreeLanguage {
    public static String driver = "oracle.jdbc.driver.OracleDriver";
    public static String url = "jdbc:oracle:thin:@206.137.75.51:1521:EI";
    public static String user = "AP_EV_SEARCH";
    public static String password = "ei3it";

    public static void main(String[] args) throws Exception {
        String year = "";
        int size = 0;

        if (args.length > 0) {
            year = args[0];
        }

        CPXParseFreeLanguage cpxPFL = new CPXParseFreeLanguage();
        String[] yearArray = null;
        if (year.indexOf(";") > 0) {
            yearArray = year.split(";");
        } else {
            yearArray = new String[1];
            yearArray[0] = year;
        }
        for (int i = 0; i < yearArray.length; i++) {
            String fileName = yearArray[i] + "Data.txt";
            FileWriter outputFile = new FileWriter(fileName);
            int count = cpxPFL.getCount(yearArray[i]);
            if (count > 0) {
                cpxPFL.getResults(yearArray[i], outputFile);
            }
        }

        System.exit(1);

    }

    public void writingFile(List<?> resultList) {

        String accessionNumber = "";
        String year = "";
        int rowID = 0;
        String freeLanguage = "";
        String classificationCode = "";
        CPXFreeLanguageObject cflo = new CPXFreeLanguageObject();
        String[] flArray = null;
        Perl5Util perl = new Perl5Util();

        try {
            FileWriter outputFile = new FileWriter("freeLanguage.txt", true);
            // System.out.println("Size= "+resultList.size());
            for (int i = 0; i < resultList.size(); i++) {
                cflo = (CPXFreeLanguageObject) resultList.get(i);
                accessionNumber = cflo.getAccessionNumber();
                year = cflo.getYear();
                rowID = cflo.getRowID();
                freeLanguage = cflo.getFreeLanguage();

                if (freeLanguage.indexOf("&") > -1) {
                    System.out.println(freeLanguage);
                    freeLanguage = perl.substitute("s/&#{0,1}[a-z0-9]*;/&#{0,1}[a-z0-9]*:/gi", freeLanguage);
                    System.out.println("*** " + freeLanguage + " ***");
                }

                classificationCode = cflo.getClassificationCode();
                if (freeLanguage.indexOf(";") > -1) {
                    flArray = freeLanguage.split(";");
                } else {
                    flArray = new String[1];
                    flArray[0] = freeLanguage;
                }

                for (int j = 0; j < flArray.length; j++) {
                    outputFile.write(rowID + "|");
                    outputFile.write(accessionNumber + "|");
                    outputFile.write(year + "|");
                    outputFile.write(flArray[j].trim() + "|");
                    outputFile.write(classificationCode + "\n");
                }
                outputFile.flush();
            }

            outputFile.close();
            outputFile = null;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception= " + e);
        }
    }

    public void processResult(List<?> resultList) {
        Connection con = null;
        PreparedStatement pstmt = null;
        String accessionNumber = "";
        String year = "";
        int rowID = 0;
        String freeLanguage = "";
        String classificationCode = "";
        CPXFreeLanguageObject cflo = new CPXFreeLanguageObject();
        String[] flArray = null;

        try {
            con = getConnection();
            String sqlString = "INSERT INTO CPXPARSEDFREELANGUAGE(rID,ex,yr,fls,cls) VALUES(?,?,?,?,?)";
            pstmt = con.prepareStatement(sqlString);
            for (int i = 0; i < resultList.size(); i++) {
                cflo = (CPXFreeLanguageObject) resultList.get(i);
                accessionNumber = cflo.getAccessionNumber();
                year = cflo.getYear();
                rowID = cflo.getRowID();
                freeLanguage = cflo.getFreeLanguage();
                classificationCode = cflo.getClassificationCode();
                if (freeLanguage.indexOf(";") > -1) {
                    flArray = freeLanguage.split(";");
                } else {
                    flArray = new String[1];
                    flArray[0] = freeLanguage;
                }

                for (int j = 0; j < flArray.length; j++) {
                    pstmt.setInt(1, rowID);
                    pstmt.setString(2, accessionNumber);
                    pstmt.setString(3, year);
                    pstmt.setString(4, flArray[j].trim());
                    pstmt.setString(5, classificationCode);
                    pstmt.executeUpdate();

                }
                // pstmt.executeUpdate();
            }

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
        }

    }

    public int getCount(String year) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int resultCount = 0;
        String sqlString = "select count(*) count from CPX_FREE_LANGUAGE where FLS is not null and yr='" + year + "'";
        System.out.println("Query: " + sqlString);

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sqlString);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                resultCount = rs.getInt("count");
            }

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

            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    con.close();
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        }

        return resultCount;
    }

    public List<CPXFreeLanguageObject> getResults(int start, int size) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<CPXFreeLanguageObject> resultList = new ArrayList<CPXFreeLanguageObject>();
        String sqlString = "select * from CPX_FREE_LANGUAGE where FLS is not null";

        try {
            CPXFreeLanguageObject cflo = null;
            con = getConnection();
            /*
             * pstmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
             */
            pstmt = con.prepareStatement(sqlString);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                if (rs.getRow() > start) {
                    if (rs.getRow() > (start + size)) {
                        // System.out.println("id= "+(start+size));
                        break;
                    }
                    cflo = new CPXFreeLanguageObject();
                    cflo.setYear(rs.getString("YR"));
                    cflo.setRowID(rs.getRow());
                    cflo.setAccessionNumber(rs.getString("EX") == null ? "" : rs.getString("EX"));
                    cflo.setFreeLanguage(rs.getString("FLS") == null ? "" : rs.getString("FLS"));
                    cflo.setClassificationCode(rs.getString("CLS") == null ? "" : rs.getString("CLS"));
                    resultList.add(cflo);
                    // System.out.println(rs.getRow()+" id= "+(start+size)+" start= "+start);
                }

            }

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

        return resultList;
    }

    public void getResults(String year, FileWriter outputFile) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlString = "select * from CPX_FREE_LANGUAGE where FLS is not null and yr='" + year + "'";
        System.out.println("sqlString= " + sqlString);
        try {
            CPXFreeLanguageObject cflo = null;
            con = getConnection();
            pstmt = con.prepareStatement(sqlString);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                cflo = new CPXFreeLanguageObject();
                cflo.setYear(rs.getString("YR"));
                cflo.setRowID(rs.getRow());
                cflo.setAccessionNumber(rs.getString("EX") == null ? "" : rs.getString("EX"));
                cflo.setFreeLanguage(rs.getString("FLS") == null ? "" : rs.getString("FLS"));
                cflo.setClassificationCode(rs.getString("CLS") == null ? "" : rs.getString("CLS"));
                writingFile(cflo, outputFile);
            }

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

    public void writingFile(CPXFreeLanguageObject cflo, FileWriter outputFile) {
        String accessionNumber = "";
        String year = "";
        int rowID = 0;
        String freeLanguage = "";
        String classificationCode = "";
        String[] flArray = null;
        Perl5Util perl = new Perl5Util();
        Hashtable<String, String> matches = new Hashtable<String, String>();

        try {
            if (cflo != null) {
                accessionNumber = cflo.getAccessionNumber();
                year = cflo.getYear();
                rowID = cflo.getRowID();
                freeLanguage = cflo.getFreeLanguage();
                if (freeLanguage.indexOf("&") > -1) {
                    // System.out.println(freeLanguage);

                    int i = 0;

                    while (perl.match("/&#{0,1}[a-z0-9]*;/i", freeLanguage)) {
                        MatchResult result = perl.getMatch();
                        matches.put("QQ" + i, result.toString());
                        freeLanguage = freeLanguage.replaceAll(result.toString(), "QQ" + i);
                        // System.out.println("--group--= "+result.toString());
                        i++;
                    }

                    // freeLanguage =perl.substitute("s/&#{0,1}[a-z0-9]*;/&#{0,1}[a-z0-9]*:/gi", freeLanguage);
                    // System.out.println("*** "+freeLanguage+" ***");
                }
                classificationCode = cflo.getClassificationCode();
                if (freeLanguage.indexOf(";") > -1) {
                    flArray = freeLanguage.split(";");
                } else {
                    flArray = new String[1];
                    flArray[0] = freeLanguage;
                }

                for (int j = 0; j < flArray.length; j++) {
                    Enumeration<String> keys = matches.keys();
                    String freeLanguageString = flArray[j];
                    while (keys.hasMoreElements()) {
                        String match = (String) keys.nextElement();
                        freeLanguageString = (freeLanguageString.replaceAll(match, (String) matches.get(match))).trim();
                        // System.out.println("match= "+match+" *** "+freeLanguageString+" ***");
                    }

                    outputFile.write(rowID + "|");
                    outputFile.write(accessionNumber + "|");
                    outputFile.write(year + "|");
                    outputFile.write(freeLanguageString.trim() + "|");
                    outputFile.write(classificationCode + "\n");
                }
                outputFile.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception= " + e);
        }
    }

    public void getResults(FileWriter outputFile) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sqlString = "select * from CPX_FREE_LANGUAGE where FLS is not null";

        try {
            CPXFreeLanguageObject cflo = null;
            con = getConnection();
            pstmt = con.prepareStatement(sqlString);
            rs = pstmt.executeQuery();

            while (rs.next()) {

                cflo = new CPXFreeLanguageObject();
                cflo.setYear(rs.getString("YR"));
                cflo.setRowID(rs.getRow());
                cflo.setAccessionNumber(rs.getString("EX") == null ? "" : rs.getString("EX"));
                cflo.setFreeLanguage(rs.getString("FLS") == null ? "" : rs.getString("FLS"));
                cflo.setClassificationCode(rs.getString("CLS") == null ? "" : rs.getString("CLS"));
                writingFile(cflo, outputFile);
            }

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

        Class.forName(CPXParseFreeLanguage.driver);
        Connection con = DriverManager.getConnection(CPXParseFreeLanguage.url, CPXParseFreeLanguage.user, CPXParseFreeLanguage.password);
        return con;
    }

}
