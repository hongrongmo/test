package org.ei.dataloading.upt.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.xml.Entity;
import org.ei.common.upt.AssigneeFilter;

public class ExtractInventorsUpt {
    Perl5Util perl = new Perl5Util();
    public static final String DELIM = new String(new char[] { 30 });
    public static final int YEAR = 1959;
    public static String US_CY = "US";
    public static String EP_CY = "EP";

    public void extract(int load_number_begin, int load_number_end, Connection con) throws Exception {
        PrintWriter writerInventor = null;
        String[] inventors = new String[1];
        PreparedStatement pstmt1 = null;
        ResultSet rs1 = null;
        String[] asg = new String[1];
        String dbname = null;
        try {
            writerInventor = new PrintWriter(new FileWriter("upt_aus.lkp"));

            if (load_number_end == 0) {
                pstmt1 = con.prepareStatement("select ac,asg,inv,py from upt_master where (inv is not null) and load_number = " + load_number_begin);
                System.out.println("\n\nQuery: " + " select ac,asg,inv,py from upt_master where (inv is not null) and load_number = " + load_number_begin);
            } else {
                pstmt1 = con.prepareStatement("select ac,asg,inv,py from upt_master where (inv is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
                System.out.println("\n\nQuery: " + " select ac,asg,inv,py from upt_master where (inv is not null) and load_number >= " + load_number_begin
                    + " and load_number <= " + load_number_end);
            }

            rs1 = pstmt1.executeQuery();
            while (rs1.next()) {
                int yr = 0;
                if (rs1.getString("py") != null && !rs1.getString("py").equals("")) {
                    try {
                        yr = Integer.parseInt(rs1.getString("py"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        ;
                    }
                }
                if (yr >= YEAR) {

                    if (rs1.getString("ac") != null) {
                        if (rs1.getString("ac").equals(EP_CY)) {
                            dbname = "EUP";
                        } else if (rs1.getString("ac").equals(US_CY)) {
                            dbname = "UPA";
                        }
                    }

                    if (rs1.getString("asg") != null) {

                        List<String> lstAsg = convertString2List(rs1.getString("asg"));
                        List<String> lstInv = convertString2List(rs1.getString("inv"));
                        String authCode = Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs1.getString("ac"))));

                        if (authCode.equals(EP_CY) && lstInv.size() > 0 && lstAsg.size() > 0) {
                            lstInv = AssigneeFilter.filterInventors(lstAsg, lstInv, false);
                        }

                        String[] arrVals = (String[]) lstInv.toArray(new String[1]);

                        arrVals[0] = replaceNull(arrVals[0]);

                        for (int j = 0; j < arrVals.length; j++) {
                            arrVals[j] = formatAuthor(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(arrVals[j]))));
                        }

                        if (arrVals != null)
                            asg = arrVals;
                    } else {
                        asg = convert2Array(formatAuthor(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs1.getString("inv"))))));
                    }

                    for (int j = 0; j < asg.length; ++j) {
                        inventors = prepareInventor(asg[j]);

                        for (int i = 0; i < inventors.length; ++i) {
                            String inventor = inventors[i];
                            if (inventor != null) {
                                inventor = inventor.trim().toUpperCase();
                                writerInventor.println(inventor + "\t" + dbname.toLowerCase());
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ;
        } finally {
            if (rs1 != null) {
                try {
                    rs1.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (pstmt1 != null) {
                try {
                    pstmt1.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (writerInventor != null) {
                try {
                    writerInventor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private String[] prepareInventor(String aString) throws Exception {

        ArrayList<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(aString, DELIM);
        String s;

        while (st.hasMoreTokens()) {
            s = st.nextToken().trim();
            if (s.length() > 0) {
                s = s.trim();
                list.add(s);
            }

        }

        return (String[]) list.toArray(new String[1]);
    }

    public String replaceAmpersand(String sVal) {

        if (sVal == null)
            return "";

        if (perl.match("/&amp;/i", sVal)) {
            sVal = perl.substitute("s/&amp;/&/ig", sVal);
        }

        return sVal;
    }

    public String formatAuthor(String s) {

        if (s == null)
            return "";

        s = perl.substitute("s/;/,/g", s);

        return s;
    }

    public List<String> convertString2List(String sList) {

        if (sList == null)
            return new ArrayList<String>();

        List<String> lstVals = new ArrayList<String>();

        perl.split(lstVals, "/" + DELIM + "/", sList);

        return lstVals;
    }

    public String[] convert2Array(String sVal) {

        List<String> values = new ArrayList<String>();

        perl.split(values, "/" + DELIM + "/", sVal);

        String[] arrVals = (String[]) values.toArray(new String[1]);

        arrVals[0] = replaceNull(arrVals[0]);

        return arrVals;
    }

    public String replaceNull(String sVal) {

        if (sVal == null)
            sVal = "";

        return sVal;
    }

}
