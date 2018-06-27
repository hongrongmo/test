package org.ei.data;

import java.io.*;
import java.sql.*;

public class CRNLookupWriter {

    PrintWriter out = null;

    public CRNLookupWriter() {
        init();
    }

    public void init() {
        try {
            out = new PrintWriter(new FileWriter("c:\\CRNLookup.java"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void createLookup() {
        Connection con = null;
        Statement stmnt = null;
        ResultSet rs = null;

        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            con = DriverManager.getConnection("jdbc:odbc:crn-list", "", "");
            stmnt = con.createStatement();
            String query = "select term,crn from [codes$];";
            rs = stmnt.executeQuery(query);

            writeClassName();
            writeAttributes();

            while (rs.next()) {
                String term = rs.getString("term");
                String crn = rs.getString("crn");

                if (term != null)
                    term = term.trim();

                if (crn != null)
                    crn = crn.trim();

                writeCRN(term, crn);

            }
            out.println("} ");
            out.println("");
            out.println("public String getCrnName(String crn) { ");
            out.println("");
            out.println("String sVal =  (String)htCrns.get(crn);");
            out.println("");
            out.println("if(sVal == null)");
            out.println("sVal = \"\";");
            out.println("return sVal;");
            out.println("} ");

            out.println("} ");
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmnt != null)
                    stmnt.close();
                if (con != null)
                    con.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    private void writeClassName() {

        out.println("import java.util.*;");
        out.println("");
        out.println("public class CRNLookup { ");
        out.println("");

    }
    private void writeAttributes() {

        out.println("static Hashtable htCrns = new Hashtable();");
        out.println("");
        out.println("static {");

    }
    private void writeCRN(String term, String crn) {
        out.println("htCrns.put(\"" + crn + "\",\"" + term + "\");");
    }
    public static void main(String[] args) {

        CRNLookupWriter writer = new CRNLookupWriter();
        writer.createLookup();
    }

}