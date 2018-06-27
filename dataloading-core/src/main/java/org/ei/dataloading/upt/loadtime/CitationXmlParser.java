package org.ei.dataloading.upt.loadtime;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.ei.dataloading.DataValidator;
import org.ei.util.DiskMap;
import org.ei.util.GUID;
import org.ei.xml.Entity;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.sql.*;

public class CitationXmlParser {

    private static final String PCIT = "PCIT";
    private static final String NCIT = "NCIT";
    StringBuffer ncit = new StringBuffer();
    StringBuffer pcit = null;
    Hashtable htRecord = new Hashtable();
    static PrintWriter patentsOut = null;
    static PrintWriter nonPatentsOut = null;
    PrintWriter countOut = null;
    StringBuffer pcitMID = new StringBuffer();
    StringBuffer pcitKind = new StringBuffer();
    private static final String BWD_CIT = new String("1");
    private static final String FWD_CIT = new String("2");
    private static final String NCIT_CIT = new String("3");
    private static final String REF_TYPE = "REF_TYPE";
    private static final String PCIT_NUM = "PCIT_NUM";
    private static final String PATENT_NUM = "PATENT_NUM";
    private static final String PCIT_AUTH_CODE = "PCIT_AUTH_CODE";
    private static final String PATENT_KIND = "PATENT_KIND";
    private static final String AUTH_CODE = "AUTH_CODE";
    private static final String NCIT_SRC = "NCIT_SRC";
    private static final String PCIT_KIND = "PCIT_KIND";
    Connection conn = null;
    boolean startB561 = false;
    boolean startB561A = false;
    boolean startB563 = false;
    private static String setURL = "jdbc:oracle:thin:@jupiter.elsevier.com:1521:EI";
    private static final String setUserName = "ba_loading";
    private static final String setPassword = "";
    static OracleMap dbMap;
    static String weekNum;
    private Perl5Util perl = new Perl5Util();

    private static final String MID = "MID";
    private static final String KIND = "KIND";
    private static final String REF_MID = "REF_MID";
    private static final String PRT_MID = "PRT_MID";
    private static final String SN = "SN";
    private static final String VOL = "VOL";
    private static final String SU = "SU";
    private static final String PG = "PG";
    private static final String PY = "PY";
    private static final String SOURCE = "SOURCE";
    private static final String CIT_PN = "CIT_PN";
    private static final String CIT_CY = "CIT_CY";
    private static final String CIT_PK = "CIT_PK";
    private static final String PRT_PN = "PRT_PN";
    private static final String PRT_CY = "PRT_CY";
    private static final String PRT_PK = "PRT_PK";
    private static final String CIT_MID = "CIT_MID";
    private static final String AUS = "AUS";
    private static final String BN = "BN";
    private static final String TITLE = "TITLE";
    private static final String UPT_LINK = "UPT_LINK";
    private static final String PRT_LINK = "PRT_LINK";
    private static final String LOAD_NUMBER = "LOAD_NUMBER";
    private static boolean init = false;
    static DiskMap readMap = new DiskMap();
    private static final String[] htPatColumns = { REF_MID, PRT_MID, PRT_PN, PRT_CY, PRT_PK, CIT_PN, CIT_CY, CIT_PK, CIT_MID, UPT_LINK, PRT_LINK, LOAD_NUMBER };
    private static final String[] htNonPatColumns = { REF_MID, PRT_MID, PRT_PN, PRT_CY, PRT_PK, SOURCE, LOAD_NUMBER };
    static int count = 0;
    char DELIM = (char) 30;
    String indexDir = null;
    static long npCount = 0;
    String patentNum = null;
    StringBuffer sbPcitKind = new StringBuffer();
    StringBuffer sbPcitNum = new StringBuffer();
    StringBuffer sbPcitAuthCd = new StringBuffer();
    String loadNumber;
    static String rootFileName;
    public static final String WRITE_PCT = "WRITE_PCT";
    public static final String WRITE_NPCT = "WRITE_NPCT";
    private static HashSet hsFiles = new HashSet();

    public CitationXmlParser(String loadNumber, String patentOut, String nonPatentOut, String indexDir) {

        initializeFields();
        this.indexDir = indexDir;
        this.loadNumber = loadNumber;

        if (!init) {
            init = true;

            try {
                //readMap.openRead(indexDir, false);
                Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();

                System.out.println("Open.............");
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            init(patentOut, nonPatentOut);
        }
    }

    private void init(String patentOut, String nonPatentOut) {

        try {
            patentsOut = new PrintWriter(new FileWriter(patentOut), true);
            nonPatentsOut = new PrintWriter(new FileWriter(nonPatentOut), true);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public String replaceUTF8Chars(String sVal) {

        StringBuffer buffer = new StringBuffer();

        char[] sChar = sVal.toCharArray();

        int i = 0;

        for (int j = 0; j < sChar.length; j++) {
            i = (int) sChar[j];

            if (i >= 32 && i <= 126) {
                buffer.append(sChar[j]);
            }
            else {
                buffer.append(" ");
            }
        }

        return buffer.toString();
    }
    public void textOfB562(String text) {

        ncit.append(text);

    }
    /**
                    * End ncit
                    */
    public void endB562() {
        try {

            Hashtable htNcit = (Hashtable) htRecord.get(NCIT);

            if (ncit.length() > 0) {

                String sNcit = ncit.substring(0, ncit.length());

                StringBuffer ncitMID = new StringBuffer();
                Hashtable htFields = new Hashtable();
                ncitMID.append("ref_");
                ncitMID.append(new GUID().toString());
                htFields.put(NCIT_SRC, sNcit);

                String mid = ncitMID.substring(0, ncitMID.length());

                htNcit.put(mid, htFields);

                ncit.setLength(0);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
            * Start backward patent citations
            */
    public void startB561() {

        startB561 = true;
        try {
            pcitMID = new StringBuffer("ref_" + new GUID().toString());

            Hashtable htPcit = (Hashtable) htRecord.get(PCIT);

            htPcit.put(pcitMID, new Hashtable());
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void startB561A() {
        startB561A = true;
    }
    public void endB561A() {

        startB561A = false;

        Hashtable htPcit = (Hashtable) htRecord.get(PCIT);

        Hashtable htFields = (Hashtable) htPcit.get(pcitMID);

        htFields.put(PCIT_KIND, sbPcitKind.substring(0, sbPcitKind.length()));

        sbPcitKind.setLength(0);
    }
    public void textOfB561A(String text) {

        try {
            if (startB561A) {

                sbPcitKind.append(text);

                Hashtable htPcit = (Hashtable) htRecord.get(PCIT);

                Hashtable htFields = (Hashtable) htPcit.get(pcitMID);

                if (htFields == null) {
                    pcitMID = new StringBuffer("ref_" + new GUID().toString());

                    htFields = new Hashtable();
                    htFields.put(PCIT_NUM, "QQ");

                    htPcit.put(pcitMID, htFields);
                }
            }
            else {
                sbPcitKind.append(text);

            }

        }
        catch (Exception e) {
            // TODO Auto-generated catch block

            e.printStackTrace();

        }
    }
    /**
                * Get text for backward patent citation number
                */
    public void textOfB561(String text) {

        try {

            sbPcitNum.append(text);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void endB561() {
        startB561 = false;

        Hashtable htPcit = (Hashtable) htRecord.get(PCIT);

        Hashtable htFields = (Hashtable) htPcit.get(pcitMID);

        htFields.put(PCIT_NUM, sbPcitNum.substring(0, sbPcitNum.length()));

        sbPcitNum.setLength(0);
    }
    public void startB563() {
        startB563 = true;

    }
    public void endB563() {

        startB563 = false;

        Hashtable htPcit = (Hashtable) htRecord.get(PCIT);

        Hashtable htFields = (Hashtable) htPcit.get(pcitMID);

        htFields.put(PCIT_AUTH_CODE, sbPcitAuthCd.substring(0, sbPcitAuthCd.length()));

        sbPcitAuthCd.setLength(0);
    }
    /**
                   * Get patent citation auth code
                   */
    public void textOfB563(String text) {

        sbPcitAuthCd.append(text);

    }
    /**
           * Get Patent Number
           */
    public void textOfB110(String text) {

        StringBuffer sbPatentNum = (StringBuffer) htRecord.get(PATENT_NUM);
        sbPatentNum.append(text);
    }
    /**
             * Get Kind
             */
    public void textOfB130(String text) {

        StringBuffer sbKind = (StringBuffer) htRecord.get(PATENT_KIND);

        sbKind.append(text);
    }
    /**
                    * Get Authority Code
                    */
    public void textOfB190(String text) {

        StringBuffer sbAuthCode = (StringBuffer) htRecord.get(AUTH_CODE);

        sbAuthCode.append(text);
    }
    public void writeCitations() throws Exception {

        Hashtable htPcit = (Hashtable) htRecord.get(PCIT);

        Enumeration anenum = htPcit.keys();
        if(htRecord.get(AUTH_CODE) == null || ((StringBuffer)htRecord.get(AUTH_CODE)).length()!=2)
        {
            if(rootFileName != null && rootFileName.length() > 2)
            {
                if(rootFileName.substring(0,2).equalsIgnoreCase("EP")){
                    StringBuffer sbAuthCode = (StringBuffer) htRecord.get(AUTH_CODE);
                    sbAuthCode.append("EP");
                }
                if(rootFileName.substring(0,2).equalsIgnoreCase("US")){
                    StringBuffer sbAuthCode = (StringBuffer) htRecord.get(AUTH_CODE);
                    sbAuthCode.append("US");
                }
            }

        }
        if((htRecord.get(AUTH_CODE) == null || ((StringBuffer)htRecord.get(AUTH_CODE)).length()!=2)||
           (htRecord.get(PATENT_NUM) == null || ((StringBuffer)htRecord.get(PATENT_NUM)).length()==0) ||
           (htRecord.get(PATENT_KIND) == null || ((StringBuffer)htRecord.get(PATENT_KIND)).length()==0))
        {
            System.err.println("\n\n******************************************************************************");
            System.err.println("Missing Patent Keys!\nStopping parsing ...\nPlease Check the "+rootFileName+" file!");
            System.err.println("******************************************************************************");
            System.exit(10);
            }
        StringBuffer pnum = (StringBuffer) htRecord.get(PATENT_NUM);
        StringBuffer pkind = (StringBuffer) htRecord.get(PATENT_KIND);
        StringBuffer pauthcode = (StringBuffer) htRecord.get(AUTH_CODE);

        String sPnum = pnum.toString();
        String sKind = pkind.toString();
        String sAuthCode = pauthcode.toString();

        sPnum = removeLeadingZeros(sPnum);

        // String mid = getMID(sPnum, pkind.toString(), pauthcode.toString());
        Hashtable htPrt = getPrtDBRec(sPnum, sKind, sAuthCode);
        String mid = (String) htPrt.get(MID);


        Hashtable row = null;

        if (mid != null && !mid.equals("")) {

            while (anenum.hasMoreElements()) {

                StringBuffer refId = ((StringBuffer) anenum.nextElement());
                Enumeration htKeys = htPcit.keys();

                Hashtable htFields = (Hashtable) htPcit.get(refId);
                //Get Backward Citation
                String pcitNum = (String) htFields.get(PCIT_NUM);
                pcitNum = removeLeadingZeros(pcitNum);
                String pcitAuthCode = (String) htFields.get(PCIT_AUTH_CODE);

                String pcitKind = (String) htFields.get(PCIT_KIND);

                String bwdMid = null;
                Hashtable htVals = null;

                if (pcitAuthCode.equalsIgnoreCase("US")||pcitAuthCode.equalsIgnoreCase("EP")){
                    htVals = getCitDBRec(pcitNum, pcitKind , pcitAuthCode);
                    bwdMid = (String) htVals.get(MID);
                }

                if (bwdMid == null)
                    bwdMid = "";
                else {
                    if (pcitAuthCode.equalsIgnoreCase("EP") && !bwdMid.equals("")) {
                        pcitKind = (String) htVals.get(KIND);
                    }
                }

                if (pcitKind == null)
                    pcitKind = "";

                row = new Hashtable();

                row.put(REF_MID, refId.toString());
                row.put(PRT_MID, mid);
                row.put(PRT_PN, sPnum);
                row.put(PRT_CY, sAuthCode);
                row.put(PRT_PK, sKind);
                row.put(CIT_PN, pcitNum);
                row.put(CIT_CY, pcitAuthCode);
                row.put(CIT_PK, pcitKind);
                row.put(CIT_MID, bwdMid);
                row.put(LOAD_NUMBER, loadNumber);

                if (!bwdMid.equals(""))
                    row.put(UPT_LINK, "Y");
                else
                    row.put(UPT_LINK, "N");

                row.put(PRT_LINK, "Y");

                writePatents(row);

            }

            Hashtable htNcit = (Hashtable) htRecord.get(NCIT);

            anenum = htNcit.keys();

            while (anenum.hasMoreElements()) {

                String refId = (String) anenum.nextElement();

                Hashtable htFields = (Hashtable) htNcit.get(refId);
                String source = (String) htFields.get(NCIT_SRC);

                row = new Hashtable();

                row.put(REF_MID, refId);
                row.put(PRT_MID, mid);
                row.put(PRT_PN, sPnum);
                row.put(PRT_CY, sAuthCode);
                row.put(PRT_PK, sKind);
                row.put(SOURCE, source.toString());
                row.put(LOAD_NUMBER, loadNumber);

                writeNonPatents(row);

            }
        }
        else {
            System.err.println("Citation not found in UPT_MASTER table for: Patent Num:" + pnum + " AuthCode=" + pauthcode + " Kind=" + pkind);
        }
    }
    public String getDBMid(String pNum, String kind, String authCode) {

        ResultSet rs = null;
        PreparedStatement stmt = null;

        Connection con = null;
        PrintWriter countsOut = null;
        String mid = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();

            con = DriverManager.getConnection(setURL, setUserName, setPassword);
            String sql = "select m_id from upt_master where pn = ? and kc = ? and ac = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, pNum);
            stmt.setString(2, kind);
            stmt.setString(3, authCode);

            stmt.execute();

            rs = stmt.getResultSet();

            if (rs.next()) {
                mid = rs.getString("m_id");
            }

        }
        catch (Exception sqle) {
            sqle.printStackTrace();
        }
        finally {
            close(rs);
            close(stmt);
            close(con);
        }

        return mid;
    }
    public String getDBUsMid(String pNum, String authCode) {

        ResultSet rs = null;
        PreparedStatement stmt = null;

        Connection con = null;
        PrintWriter countsOut = null;
        String mid = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();

            con = DriverManager.getConnection(setURL, setUserName, setPassword);
            String sql = "select m_id from upt_master where pn = ? and ac = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, pNum);
            stmt.setString(2, authCode);

            stmt.execute();

            rs = stmt.getResultSet();

            if (rs.next()) {
                mid = rs.getString("m_id");
            }

        }
        catch (Exception sqle) {
            sqle.printStackTrace();
        }
        finally {
            close(rs);
            close(stmt);
            close(con);
        }

        return mid;
    }
    public boolean getPrtMid(String prtMid) {

        if (prtMid == null)
            return false;

        ResultSet rs = null;
        PreparedStatement stmt = null;

        Connection con = null;
        PrintWriter countsOut = null;
        String mid = null;
        boolean found = false;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();

            con = DriverManager.getConnection(setURL, setUserName, setPassword);
            String sql = "select prt_mid from patent_refs where prt_mid = ?";

            stmt = con.prepareStatement(sql);
            stmt.setString(1, prtMid);

            stmt.execute();

            rs = stmt.getResultSet();

            if (rs.next()) {
                found = true;
            }
        }
        catch (Exception sqle) {
            sqle.printStackTrace();
        }
        finally {
            close(rs);
            close(stmt);
            close(con);
        }

        return found;
    }
    public Hashtable getDBEpMid(String patentNum) {

        ResultSet rs = null;
        PreparedStatement stmt = null;

        Connection con = null;
        PrintWriter countsOut = null;
        String mid = null;
        Hashtable htVals = new Hashtable();

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();

            con = DriverManager.getConnection(setURL, setUserName, setPassword);
            String sql = "select m_id, kc from upt_master where pn = ? and (kc = 'A1' or kc = 'A2') and ac = 'EP'";

            stmt = con.prepareStatement(sql);

            stmt.setString(1, patentNum);

            stmt.execute();

            rs = stmt.getResultSet();

            if (rs.next()) {
                mid = rs.getString("mid");
                String kc = rs.getString("kc");
                htVals.put(CIT_MID, mid);
                htVals.put(CIT_PK, kc);
            }

        }
        catch (Exception sqle) {
            sqle.printStackTrace();
        }
        finally {
            close(rs);
            close(stmt);
            close(con);
        }

        return htVals;
    }
    public void writeNonPatents(Hashtable htRow) {

        try {

            StringBuffer sqlRec = new StringBuffer();

            for (int i = 0; i < htNonPatColumns.length; i++) {

                String key = htNonPatColumns[i];

                String value = (String) htRow.get(key);

                if (value != null && value.length() > 0) {
                    value = value.toString();
                    value = replaceUTF8Chars(value);
                    value = perl.substitute("s/\\s+/ /", value);
                }
                else {
                    value = "";
                }

                sqlRec.append(value);

                if (i != htNonPatColumns.length - 1)
                    sqlRec.append("\t");
            }

            nonPatentsOut.println(sqlRec);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void writePatents(Hashtable htRow) {

        try {

            StringBuffer sqlRec = new StringBuffer();

            for (int i = 0; i < htPatColumns.length; i++) {

                String key = htPatColumns[i];

                String value = (String) htRow.get(key);

                if (value != null && value.length() > 0) {
                    value = value.toString();
                    value = replaceUTF8Chars(value);
                    value = perl.substitute("s/\\s+/ /", value);
                }
                else {
                    value = "";
                }

                sqlRec.append(value);

                if (i != htPatColumns.length - 1)
                    sqlRec.append("\t");
            }

            patentsOut.println(sqlRec);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String replaceNull(String sVal) {

        if (sVal == null)
            sVal = "";

        return sVal;
    }

    public String getIssn(String text) {

        String issn = "";

        if (perl.match("/\\sISSN\\:?\\;?\\s?[0-9]*-?[0-9]*X?/i", text)) {
            issn = perl.getMatch().toString();

        }
        else if (perl.match("/ISSN\\:?\\;?\\s?[0-9]*-?[0-9]*X?/i", text)) {
            issn = perl.getMatch().toString();

        }
        else if (perl.match("/ISSN\\s[0-9]*-?[0-9]*X?/i", text)) {
            issn = perl.getMatch().toString();

        }

        if (perl.match("/[0-9]+-?[0-9]+X?/i", issn)) {
            issn = perl.getMatch().toString();
        }
        else {
            issn = "";
        }

        return issn;

    }
    public String getIsbn(String text) {

        String isbn = "";

        if (perl.match("/\\sISBN:?\\s?[0-9]*-[0-9]*-[0-9]*-[0-9]*X?/i", text)) {
            isbn = perl.getMatch().toString();

        }
        else if (perl.match("/ISBN:?\\s?[0-9]*-[0-9]*-[0-9]*-[0-9]*X?/i", text)) {
            isbn = perl.getMatch().toString();

        }

        if (perl.match("/[0-9]+-[0-9]+-[0-9]+-[0-9]+X?/i", isbn)) {
            isbn = perl.getMatch().toString();
        }
        else {
            isbn = "";
        }

        return isbn;
    }

    public String getIssue(String text) {

        String issue = "";

        if (perl.match("/,?\\s?no\\.\\s?[0-9]+/i", text)) {
            issue = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?Issue:\\s?[0-9]+/i", text)) {
            issue = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?Nr\\.\\s?[0-9]+/i", text)) {
            issue = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?Nr\\.\\s?Part\\s[0-9]+/i", text)) {
            issue = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?Nr\\.\\s?Suppl\\s[0-9]+/i", text)) {
            issue = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?n[0-9]+,/i", text)) {
            issue = perl.getMatch().toString();

        }

        if (perl.match("/[1-9][0-9]*/i", issue)) {
            issue = perl.getMatch().toString();
        }
        else {
            issue = "";
        }

        return issue;

    }
    public String getPage(String text) {

        String pages = "";

        if (perl.match("/,?\\s?pp\\.\\s?[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?p\\.\\s?[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?pages\\s[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?Seiten\\s?[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?PAGE\\(S\\)\\s?[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?pp\\s?[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/p\\.:\\s[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/,\\s?p\\s?[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/,\\s?pages\\s[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/\\spage\\s[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/pages,?\\s?[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/Seite\\s?[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/Seiten\\s?[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();

        }
        else if (perl.match("/,\\s?p\\.?\\s?[A-Z]?[0-9]+/i", text)) {
            pages = perl.getMatch().toString();
        }

        if (perl.match("/[A-Z]?[1-9][0-9]*/i", pages)) {
            pages = perl.getMatch().toString();
        }
        else {
            pages = "";
        }

        return pages;

    }
    public String removeLeadingZeros(String sVal) {

        if (sVal == null) {
            return sVal;
        }
        else {
            return sVal.replaceFirst("^(D|PP|RE|T|H|X|RX|AI)?[0]+","$1");
        }

        /*
        char[] schars = sVal.toCharArray();
        int index = 0;
        for (; index < sVal.length(); index++) {

            if (schars[index] != '0') {
                break;
            }
        }

        return (index == 0) ? sVal : sVal.substring(index);
        */
    }
    public String getTitle(String text) {

        text = perl.substitute("s/\\./::dot::/g", text);
        text = perl.substitute("s/-/::dash::/g", text);

        Perl5Matcher matcher = new Perl5Matcher();
        Perl5Compiler compiler = new Perl5Compiler();

        Pattern pattern = null;
        String title = "";
        String regularExpression = "\"([^\"]*)\"";

        try {

            pattern = compiler.compile(regularExpression);
        }
        catch (MalformedPatternException e) {
            System.out.println("Bad pattern.");
            System.out.println(e.getMessage());
        }

        if (matcher.contains(text, pattern)) {

            MatchResult result = matcher.getMatch();

            title = result.group(0);
            title = perl.substitute("s/\"//g", title);
            title = title.trim();
        }

        if (title == null)
            title = "";

        if (title.equals("")) {
            regularExpression = "\'([^\']*)\'";

            try {
                pattern = compiler.compile(regularExpression);
            }
            catch (MalformedPatternException e) {
                System.out.println("Bad pattern.");
                System.out.println(e.getMessage());
            }

            if (matcher.contains(text, pattern)) {

                MatchResult result = matcher.getMatch();

                title = result.group(0);
                title = perl.substitute("s/\"//g", title);
                title = perl.substitute("s/'//g", title);
                title = title.trim();
            }

        }

        title = perl.substitute("s/::dot::/\\./g", title);
        title = perl.substitute("s/::dash::/-/g", title);

        return title;

    }
    public String getVolume(String text) {

        String vol = "";

        if (perl.match("/,?\\s?vol\\.\\s[0-9]+/i", text)) {
            vol = perl.getMatch().toString();
        }
        else if (perl.match("/,?\\s?vol\\.:\\s[0-9]+/i", text)) {
            vol = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?bd\\.\\s[0-9]+/i", text)) {
            vol = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?band\\s?[0-9]+/i", text)) {
            vol = perl.getMatch().toString();

        }
        else if (perl.match("/vol\\s[0-9]+/i", text)) {
            vol = perl.getMatch().toString();

        }
        else if (perl.match("/vol\\.[0-9]+/i", text)) {
            vol = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?volume\\s[0-9]+/i", text)) {
            vol = perl.getMatch().toString();

        }
        else if (perl.match("/,?\\s?v[0-9]+/i", text)) {
            vol = perl.getMatch().toString();
        }

        if (perl.match("/[1-9][0-9]*/i", vol)) {
            vol = perl.getMatch().toString();
        }
        else {
            vol = "";
        }

        return vol;

    }
    public String getMID(String pnum, String kind, String cy) throws Exception {

        StringBuffer keyBuf = new StringBuffer();

        String mid = null;

        if (cy.equalsIgnoreCase("ep")) {
            keyBuf.append(cy).append(pnum).append(kind);

            String str = keyBuf.substring(0, keyBuf.length());

            // System.out.println(str);
            mid = readMap.get(str);

            //  long start = System.currentTimeMillis();

            //                                long end = System.currentTimeMillis();
            //                                long duration = end - start;
            //                                System.out.println("Duration = " + duration);

        }
        else if (cy.equalsIgnoreCase("us")) {
            keyBuf.append(cy).append(pnum);
            //      long start = System.currentTimeMillis();
            mid = readMap.get(keyBuf.toString());
            //                        long end = System.currentTimeMillis();
            //                        long duration = end - start;
            //                        System.out.println("Duration = " + duration);
        }

        return mid;

    }
    public Hashtable getEPMID(String pnum, String kind, String cy) throws Exception {

        StringBuffer keyBuf = new StringBuffer();

        String mid = null;

        Hashtable htVals = new Hashtable();

        if (cy.equalsIgnoreCase("ep")) {
            keyBuf.append(cy).append(pnum).append("A1");
            String str = keyBuf.substring(0, keyBuf.length());
            mid = readMap.get(str);

            if (mid == null) {
                keyBuf.setLength(0);
                keyBuf.append(cy).append(pnum).append("A2");
                //  long start = System.currentTimeMillis();
                mid = readMap.get(keyBuf.toString());
                //                                long end = System.currentTimeMillis();
                //                                long duration = end - start;
                //                                System.out.println("Duration = " + duration);
            }
        }
        else if (cy.equalsIgnoreCase("us")) {
            keyBuf.append(cy).append(pnum);
            //      long start = System.currentTimeMillis();
            mid = readMap.get(keyBuf.toString());
            //                        long end = System.currentTimeMillis();
            //                        long duration = end - start;
            //                        System.out.println("Duration = " + duration);
        }

        if (mid == null)
            mid = "";

        htVals.put(CIT_MID, mid);
        htVals.put(CIT_PK, keyBuf.toString());

        return htVals;

    }

    public String getEPMidByKind(String pnum) throws Exception {

        String sql = "select m_id from upt_master where pn = ? and (kc = 'A1' or kc = 'A2') and ac = 'EP'";

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String mid = null;

        try {
            conn = DriverManager.getConnection(setURL, setUserName, setPassword);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, pnum);

            rs = stmt.executeQuery();

            if (rs.next()) {
                mid = rs.getString(1);
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
        finally {
            close(rs);
            close(stmt);
            close(conn);
        }

        return mid;

    }
    public void buildIndex(String indexDir) throws Exception {

        DiskMap writeMap = new DiskMap();

        String sql = "select m_id,pn,kc,ac from upt_master";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        int count = 0;

        try {

            writeMap.openWrite(indexDir);

            conn = DriverManager.getConnection(setURL, setUserName, setPassword);
            stmt = conn.prepareStatement(sql);

            rs = stmt.executeQuery();

            StringBuffer keyBuf = null;

            while (rs.next()) {
                count++;
                String mid = rs.getString(1);
                String pn = rs.getString(2);
                String pk = rs.getString(3);
                String ac = rs.getString(4);
                keyBuf = new StringBuffer();

                if (ac.equalsIgnoreCase("EP"))
                    keyBuf.append(ac).append(removeLeadingZeros(pn)).append(pk);
                else if (ac.equalsIgnoreCase("US"))
                    keyBuf.append(ac).append(removeLeadingZeros(pn));

                String str = keyBuf.substring(0, keyBuf.length());

                writeMap.put(str, mid);

                keyBuf.setLength(0);

                if (count % 100 == 0) {
                    System.out.println(count);
                }

            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
        finally {
            close(rs);
            close(stmt);
            close(conn);
            writeMap.optimize();
            writeMap.close();
        }

    }
    public String getEPKind(String pnum) throws Exception {

        String sql = "select kc from upt_master where pn = ? and (kc = 'A1' or kc = 'A2') and ac = 'EP'";

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String mid = null;
        String kindCode = null;

        try {
            conn = DriverManager.getConnection(setURL, setUserName, setPassword);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, pnum);

            rs = stmt.executeQuery();

            if (rs.next()) {
                kindCode = rs.getString(1);
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("in ep catch.......");
            e.printStackTrace();
            throw e;
        }
        finally {
            close(rs);
            close(stmt);
            close(conn);
        }

        return kindCode;

    }
    public String getUSMid(String pnum) throws Exception {

        String sql = "select m_id from upt_master where pn = ? and ac = 'US'";

        PreparedStatement stmt = null;
        ResultSet rs = null;
        String mid = null;

        try {
            conn = DriverManager.getConnection(setURL, setUserName, setPassword);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, pnum);

            rs = stmt.executeQuery();

            if (rs.next()) {
                mid = rs.getString(1);
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("in us catch.......");
            e.printStackTrace();
            throw e;
        }
        finally {
            close(rs);
            close(stmt);
            close(conn);
        }

        return mid;
    }
    public  Hashtable getCitDBRec(String pNum, String kind, String authCode) {

        ResultSet rs = null;

        Hashtable htVals = new Hashtable(2);
        try {
            /*
            rs = dbMap.get(authCode+pNum);
            while(rs != null && rs.next()) {
                String mid = rs.getString("m_id");
                String kc = rs.getString("kc");
                if(authCode.equalsIgnoreCase("EP") && kc.startsWith("A")){
                    htVals.put(MID, mid);
                    htVals.put(KIND, kc);
                    break;
                }
                else {
                    htVals.put(MID, mid);
                    htVals.put(KIND, kc);
                }
            }
            */


        }
        catch (Exception sqle) {
            sqle.printStackTrace();
        }
        finally {
            close(rs);

        }

        return htVals;
    }
    public  Hashtable getPrtDBRec(String pNum, String kind, String authCode) {

            ResultSet rs = null;

            Hashtable htVals = new Hashtable(2);
            try {
            /*
                rs = dbMap.get(authCode+pNum);
                while(rs != null && rs.next()) {
                    String mid = rs.getString("m_id");
                    String kc = rs.getString("kc");
                    String ln = rs.getString("load_number");
                    if(authCode.equalsIgnoreCase("EP") && kc.equalsIgnoreCase(kind) && ln.equals(loadNumber)){
                        htVals.put(MID, mid);
                        htVals.put(KIND, kc);

                    }
                    else if(authCode.equalsIgnoreCase("US") && ln.equals(loadNumber))
                    {
                        htVals.put(MID, mid);
                        htVals.put(KIND, kc);
                    }

                }
            */

            }
            catch (Exception sqle) {
                sqle.printStackTrace();
            }
            finally {
                close(rs);

            }

            return htVals;
    }

    public void endPATDOC() {

        StringBuffer patentNum = null;
        StringBuffer patentKind = null;
        StringBuffer authCode = null;

        try {

            patentNum = (StringBuffer) htRecord.get(PATENT_NUM);
            patentKind = (StringBuffer) htRecord.get(PATENT_KIND);
            authCode = (StringBuffer) htRecord.get(AUTH_CODE);

            writeCitations();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex);
            System.out.println("Patent Num =" + patentNum);
            System.out.println("Patent Kind =" + patentKind);
            System.out.println("Patent AuthCode=" + authCode);

        }

    }
    public void initializeFields() {

        htRecord.put(PCIT, new Hashtable());
        htRecord.put(NCIT, new Hashtable());
        htRecord.put(PATENT_NUM, new StringBuffer());
        htRecord.put(PATENT_KIND, new StringBuffer());
        htRecord.put(AUTH_CODE, new StringBuffer());
    }

    private void close(Statement stmt) {

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
    private void close(ResultSet rs) {

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
    private void close(Connection conn) {

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

    public void parseXml(String loadNumber, File root, String patentOut, String nonPatentOut, StringBuffer xml, String indexDir) {

        BufferedReader in = null;
        Perl5Util perl = new Perl5Util();
        ZipInputStream zin = null;
        ZipEntry entry = null;

        try {

            long beginTime = System.currentTimeMillis();

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            SAXParser parser = saxParserFactory.newSAXParser();
             String[] dirlist;
            if (weekNum != null || weekNum.equals(""))
            {
                dirlist = root.list(new WeekFileFilter());

            }
            else
            {
                dirlist = root.list();
            }
            String path1 = null;
            File current_file = null;

            String path = null;

            int count = 0;
            int fileCount =0;
            if (dirlist == null && root.isFile()){
                fileCount = 1;
            }
            else {
                fileCount = dirlist.length;
            }

            for (int i = 0; i < fileCount; i++) {
                count++;
                StringBuffer sbPath = new StringBuffer();
                if(root.isDirectory()) {
                    sbPath.append(root.getPath()).append(File.separator).append(dirlist[i]);
                }
                else if(root.isFile()) {
                    sbPath.append(root.getPath());
                }

                path = sbPath.substring(0, sbPath.length());
                System.out.println(path);
                int index = path.indexOf("api");

                current_file = new File(path);

                if (current_file.isDirectory()) {
                    if (!path.endsWith(".xml")) {
                        System.out.println(path);
                    }
                    parseXml(loadNumber, current_file, patentOut, nonPatentOut, xml, indexDir);
                }
                else if (current_file.isFile() && path.endsWith(".zip")) {
                    rootFileName=current_file.getName();
                    System.out.println("File Root=" + current_file.getName());
                    zin = new ZipInputStream(new FileInputStream(current_file));
                    while ((entry = zin.getNextEntry()) != null) {
                        if (entry.getName().toUpperCase().endsWith(".XML")) {
                            if (!hsFiles.contains(entry.getName().toUpperCase())) {
                                hsFiles.add(entry.getName().toUpperCase());
                                in = new BufferedReader(new InputStreamReader(zin, "UTF-8"));

                                String line = null;

                                while ((line = in.readLine()) != null) {
                                    if (line.startsWith("</patdoc>")) {

                                        xml.append(line).append("\n");
                                        CitationXmlParser patentParser = new CitationXmlParser(loadNumber, patentOut, nonPatentOut, indexDir);

                                        SaxBaseHandler saxParser = new SaxBaseHandler(patentParser);
                                        //  String xmlFile = Entity.prepareString(substituteChars(xml.toString()));
                                        //xmlFile = perl.substitute("s/\\s+/ /g", xmlFile);
                                        String str = xml.substring(0, xml.length());

                                        //                            System.out.println(str);
                                        //
                                        //                            System.out.println("====================");
                                        str = wrapCDATA(str);

                                        if (str.indexOf("<b561>") != -1 || str.indexOf("<b562>") != -1) {
                                            InputSource insrc = new InputSource(new StringReader(new String(str)));
                                            parser.parse(insrc, saxParser);
                                        }

                                        //  patentParser.parseXml(xml.substring(0, xml.length()));
                                        //                            System.out.println(xml);
                                        //                            System.out.println("=====================");
                                        xml.setLength(0);
                                        xml.append("<patdoc>").append("\n");
                                    }
                                    else {
                                        //                            char arrText[] = line.toCharArray();
                                        //
                                        //                            int iChar = (int) arrText[0];
                                        //
                                        //                            //Ignore character
                                        //                            if (iChar == 65279)
                                        //                                line = line.substring(1, line.length());
                                        //
                                        if (validTag(line))
                                            xml.append(line).append("\n");
                                    }
                                }
                            }
                            else {
                                System.err.println("Duplicate file found:"+entry.getName());
                            }

                        }
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (zin != null) {
                        zin.close();
                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public boolean validTag(String line) {

        boolean goodTag = false;

        String goodTags[] = { "<b561>", "<b562>", "<b561a>", "<b110>", "<b130>", "<b190>", "<b563>" };

        for (int i = 0; i < goodTags.length; i++) {
            if (line.indexOf(goodTags[i]) != -1) {
                goodTag = true;
                break;
            }

        }

        return goodTag;
    }
    class XmlFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            if (name.endsWith(".xml"))
                return true;
            else
                return false;
        }
    }
    class WeekFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {

              return name.toUpperCase().matches("(USA|USB|EPA|EPB)_\\d\\d\\d\\d_"+weekNum+".ZIP");
        }
    }
    public String wrapCDATA(String str) {

        if (str.indexOf("<b562>") != -1) {
            str=substituteChars(str);
            str = perl.substitute("s/<b562>/<b562><![CDATA[/ig", str);
            str = perl.substitute("s/<\\/b562>/]]><\\/b562>/ig", str);
        }

        return str;

    }
    private static String substituteChars(String xml) {

        Perl5Util perl = new Perl5Util();


        int len = xml.length();
        StringBuffer sb = new StringBuffer();
        char c;

        for (int i = 0; i < len; i++)
        {
            c = xml.charAt(i);
            if ((int)c >= 32 && (int)c <= 126)
            {
                sb.append(c);
            }
            else if(((int)c >= 128 && (int)c <= 159)||((int)c==8364))
            {

                switch ((int)c) { //Fix for Extended ASCII not in Unicode
                    case 128: sb.append("&Ccedil;"); break;
                    case 129: sb.append("&uuml;"); break;
                    case 130: sb.append("&eacute;"); break;
                    case 131: sb.append("&acirc;"); break;
                    case 132: sb.append("&auml;"); break;
                    case 133: sb.append("&agrave;"); break;
                    case 134: sb.append("&aring;"); break;
                    case 135: sb.append("&ccedil;"); break;
                    case 136: sb.append("&ecirc;"); break;
                    case 137: sb.append("&euml;"); break;
                    case 138: sb.append("&egrave;"); break;
                    case 139: sb.append("&iuml;"); break;
                    case 140: sb.append("&icirc;"); break;
                    case 141: sb.append("&igrave;"); break;
                    case 142: sb.append("&Auml;"); break;
                    case 143: sb.append("&Aring;"); break;
                    case 144: sb.append("&Eacute;"); break;
                    case 145: sb.append("&aelig;"); break;
                    case 146: sb.append("&AElig;"); break;
                    case 147: sb.append("&ocirc;"); break;
                    case 148: sb.append("&ouml;"); break;
                    case 149: sb.append("&ograve;"); break;
                    case 150: sb.append("&ucirc;"); break;
                    case 151: sb.append("&ugrave;"); break;
                    case 152: sb.append("&yuml;"); break;
                    case 153: sb.append("&Ouml;"); break;
                    case 154: sb.append("&Uuml;"); break;
                    case 156: sb.append("&pound;"); break;
                    case 157: sb.append("&yen;"); break;
                    case 159: sb.append("&#131;"); break;
                    case 8364: sb.append("&Ccedil;"); break;
                    default:
                        sb.append(' ');
                    break;
                }
            }
            else
                switch (c) {
                    /*
                    case '\u0021': sb.append("&excl;"); break;  //EXCLAMATION MARK
                    case '\u0023': sb.append("&num;"); break;  //NUMBER SIGN
                    case '\u0024': sb.append("&dollar;"); break;  //DOLLAR SIGN
                    case '\u0025': sb.append("&percnt;"); break;  //PERCENT SIGN
                    case '\u0026': sb.append("&amp;"); break;  //AMPERSAND
                    case '\u0028': sb.append("&lpar;"); break;  //OPENING PARENTHESIS
                    case '\u0029': sb.append("&rpar;"); break;  //CLOSING PARENTHESIS
                    case '\u002A': sb.append("&ast;"); break;  //ASTERISK
                    case '\u002B': sb.append("&plus;"); break;  //PLUS SIGN
                    case '\u002C': sb.append("&comma;"); break;  //COMMA
                    case '\u002D': sb.append("&hyphen;"); break;  //HYPHEN-MINUS
                    case '\u002E': sb.append("&period;"); break;  //PERIOD
                    case '\u002F': sb.append("&sol;"); break;  //SLASH
                    case '\u003A': sb.append("&colon;"); break;  //COLON
                    case '\u003B': sb.append("&semi;"); break;  //SEMICOLON
                    case '\u003C': sb.append("&lt;"); break;  //LESS-THAN SIGN
                    case '\u003D': sb.append("&equals;"); break;  //EQUALS SIGN
                    case '\u003E': sb.append("&gt;"); break;  //GREATER-THAN SIGN
                    case '\u003F': sb.append("&quest;"); break;  //QUESTION MARK
                    case '\u0040': sb.append("&commat;"); break;  //COMMERCIAL AT
                    case '\u005B': sb.append("&lsqb;"); break;  //OPENING SQUARE BRACKET
                    case '\u005C': sb.append("&bsol;"); break;  //BACKSLASH
                    case '\u005D': sb.append("&rsqb;"); break;  //CLOSING SQUARE BRACKET
                    case '\u005E': sb.append("&circ;"); break;  //SPACING CIRCUMFLEX
                    case '\u005F': sb.append("&lowbar;"); break;  //SPACING UNDERSCORE
                    case '\u0060': sb.append("&grave;"); break;  //SPACING GRAVE
                    case '\u007B': sb.append("&lcub;"); break;  //OPENING CURLY BRACKET
                    case '\u007C': sb.append("&verbar;"); break;  //VERTICAL BAR
                    case '\u007D': sb.append("&rcub;"); break;  //CLOSING CURLY BRACKET
                    case '\u007E': sb.append("&tilde;"); break;  //TILDE
                    */
                    case '\u00A0': sb.append("&nbsp;"); break;  //NON-BREAKING SPACE
                    case '\u00A1': sb.append("&iexcl;"); break;  //INVERTED EXCLAMATION MARK
                    case '\u00A2': sb.append("&cent;"); break;  //CENT SIGN
                    case '\u00A3': sb.append("&pound;"); break;  //POUND SIGN
                    case '\u00A4': sb.append("&curren;"); break;  //CURRENCY SIGN
                    case '\u00A5': sb.append("&yen;"); break;  //YEN SIGN
                    case '\u00A6': sb.append("&brvbar;"); break;  //BROKEN VERTICAL BAR
                    case '\u00A7': sb.append("&sect;"); break;  //SECTION SIGN
                    case '\u00A8': sb.append("&die;"); break;  //SPACING DIAERESIS
                    case '\u00A9': sb.append("&copy;"); break;  //COPYRIGHT SIGN
                    case '\u00AA': sb.append("&ordf;"); break;  //FEMININE ORDINAL INDICATOR
                    case '\u00AB': sb.append("&laquo;"); break;  //LEFT POINTING GUILLEMET
                    case '\u00AC': sb.append("&not;"); break;  //NOT SIGN
                    case '\u00AD': sb.append("&shy;"); break;  //SOFT HYPHEN
                    case '\u00AE': sb.append("&reg;"); break;  //REGISTERED TRADE MARK SIGN
                    case '\u00AF': sb.append("&macr;"); break;  //SPACING MACRON
                    case '\u00B0': sb.append("&deg;"); break;  //DEGREE SIGN
                    case '\u00B1': sb.append("&plusmn;"); break;  //PLUS-OR-MINUS SIGN
                    case '\u00B2': sb.append("&sup2;"); break;  //SUPERSCRIPT DIGIT TWO
                    case '\u00B3': sb.append("&sup3;"); break;  //SUPERSCRIPT DIGIT THREE
                    case '\u00B4': sb.append("&acute;"); break;  //SPACING ACUTE
                    case '\u00B5': sb.append("&micro;"); break;  //MICRO SIGN
                    case '\u00B6': sb.append("&para;"); break;  //PARAGRAPH SIGN
                    case '\u00B7': sb.append("&middot;"); break;  //MIDDLE DOT
                    case '\u00B8': sb.append("&cedil;"); break;  //SPACING CEDILLA
                    case '\u00B9': sb.append("&sup1;"); break;  //SUPERSCRIPT DIGIT ONE
                    case '\u00BA': sb.append("&ordm;"); break;  //MASCULINE ORDINAL INDICATOR
                    case '\u00BB': sb.append("&raquo;"); break;  //RIGHT POINTING GUILLEMET
                    case '\u00BC': sb.append("&frac14;"); break;  //FRACTION ONE QUARTER
                    case '\u00BD': sb.append("&frac12;"); break;  //FRACTION ONE HALF
                    case '\u00BE': sb.append("&frac34;"); break;  //FRACTION THREE QUARTERS
                    case '\u00BF': sb.append("&iquest;"); break;  //INVERTED QUESTION MARK
                    case '\u00C0': sb.append("&Agrave;"); break;  //LATIN CAPITAL LETTER A GRAVE
                    case '\u00C1': sb.append("&Aacute;"); break;  //LATIN CAPITAL LETTER A ACUTE
                    case '\u00C2': sb.append("&Acirc;"); break;  //LATIN CAPITAL LETTER A CIRCUMFLEX
                    case '\u00C3': sb.append("&Atilde;"); break;  //LATIN CAPITAL LETTER A TILDE
                    case '\u00C4': sb.append("&Auml;"); break;  //LATIN CAPITAL LETTER A DIAERESIS
                    case '\u00C5': sb.append("&Aring;"); break;  //LATIN CAPITAL LETTER A RING
                    case '\u00C6': sb.append("&AElig;"); break;  //LATIN CAPITAL LETTER A E
                    case '\u00C7': sb.append("&Ccedil;"); break;  //LATIN CAPITAL LETTER C CEDILLA
                    case '\u00C8': sb.append("&Egrave;"); break;  //LATIN CAPITAL LETTER E GRAVE
                    case '\u00C9': sb.append("&Eacute;"); break;  //LATIN CAPITAL LETTER E ACUTE
                    case '\u00CA': sb.append("&Ecirc;"); break;  //LATIN CAPITAL LETTER E CIRCUMFLEX
                    case '\u00CB': sb.append("&Euml;"); break;  //LATIN CAPITAL LETTER E DIAERESIS
                    case '\u00CC': sb.append("&Igrave;"); break;  //LATIN CAPITAL LETTER I GRAVE
                    case '\u00CD': sb.append("&Iacute;"); break;  //LATIN CAPITAL LETTER I ACUTE
                    case '\u00CE': sb.append("&Icirc;"); break;  //LATIN CAPITAL LETTER I CIRCUMFLEX
                    case '\u00CF': sb.append("&Iuml;"); break;  //LATIN CAPITAL LETTER I DIAERESIS
                    case '\u00D0': sb.append("&ETH;"); break;  //LATIN CAPITAL LETTER ETH
                    case '\u00D1': sb.append("&Ntilde;"); break;  //LATIN CAPITAL LETTER N TILDE
                    case '\u00D2': sb.append("&Ograve;"); break;  //LATIN CAPITAL LETTER O GRAVE
                    case '\u00D3': sb.append("&Oacute;"); break;  //LATIN CAPITAL LETTER O ACUTE
                    case '\u00D4': sb.append("&Ocirc;"); break;  //LATIN CAPITAL LETTER O CIRCUMFLEX
                    case '\u00D5': sb.append("&Otilde;"); break;  //LATIN CAPITAL LETTER O TILDE
                    case '\u00D6': sb.append("&Ouml;"); break;  //LATIN CAPITAL LETTER O DIAERESIS
                    case '\u00D7': sb.append("&times;"); break;  //MULTIPLICATION SIGN
                    case '\u00D8': sb.append("&Oslash;"); break;  //LATIN CAPITAL LETTER O SLASH
                    case '\u00D9': sb.append("&Ugrave;"); break;  //LATIN CAPITAL LETTER U GRAVE
                    case '\u00DA': sb.append("&Uacute;"); break;  //LATIN CAPITAL LETTER U ACUTE
                    case '\u00DB': sb.append("&Ucirc;"); break;  //LATIN CAPITAL LETTER U CIRCUMFLEX
                    case '\u00DC': sb.append("&Uuml;"); break;  //LATIN CAPITAL LETTER U DIAERESIS
                    case '\u00DD': sb.append("&Yacute;"); break;  //LATIN CAPITAL LETTER Y ACUTE
                    case '\u00DE': sb.append("&THORN;"); break;  //LATIN CAPITAL LETTER THORN
                    case '\u00DF': sb.append("&szlig;"); break;  //LATIN SMALL LETTER SHARP S
                    case '\u00E0': sb.append("&agrave;"); break;  //LATIN SMALL LETTER A GRAVE
                    case '\u00E1': sb.append("&aacute;"); break;  //LATIN SMALL LETTER A ACUTE
                    case '\u00E2': sb.append("&acirc;"); break;  //LATIN SMALL LETTER A CIRCUMFLEX
                    case '\u00E3': sb.append("&atilde;"); break;  //LATIN SMALL LETTER A TILDE
                    case '\u00E4': sb.append("&auml;"); break;  //LATIN SMALL LETTER A DIAERESIS
                    case '\u00E5': sb.append("&aring;"); break;  //LATIN SMALL LETTER A RING
                    case '\u00E6': sb.append("&aelig;"); break;  //LATIN SMALL LETTER A E
                    case '\u00E7': sb.append("&ccedil;"); break;  //LATIN SMALL LETTER C CEDILLA
                    case '\u00E8': sb.append("&egrave;"); break;  //LATIN SMALL LETTER E GRAVE
                    case '\u00E9': sb.append("&eacute;"); break;  //LATIN SMALL LETTER E ACUTE
                    case '\u00EA': sb.append("&ecirc;"); break;  //LATIN SMALL LETTER E CIRCUMFLEX
                    case '\u00EB': sb.append("&euml;"); break;  //LATIN SMALL LETTER E DIAERESIS
                    case '\u00EC': sb.append("&igrave;"); break;  //LATIN SMALL LETTER I GRAVE
                    case '\u00ED': sb.append("&iacute;"); break;  //LATIN SMALL LETTER I ACUTE
                    case '\u00EE': sb.append("&icirc;"); break;  //LATIN SMALL LETTER I CIRCUMFLEX
                    case '\u00EF': sb.append("&iuml;"); break;  //LATIN SMALL LETTER I DIAERESIS
                    case '\u00F0': sb.append("&eth;"); break;  //LATIN SMALL LETTER ETH
                    case '\u00F1': sb.append("&ntilde;"); break;  //LATIN SMALL LETTER N TILDE
                    case '\u00F2': sb.append("&ograve;"); break;  //LATIN SMALL LETTER O GRAVE
                    case '\u00F3': sb.append("&oacute;"); break;  //LATIN SMALL LETTER O ACUTE
                    case '\u00F4': sb.append("&ocirc;"); break;  //LATIN SMALL LETTER O CIRCUMFLEX
                    case '\u00F5': sb.append("&otilde;"); break;  //LATIN SMALL LETTER O TILDE
                    case '\u00F6': sb.append("&ouml;"); break;  //LATIN SMALL LETTER O DIAERESIS
                    case '\u00F7': sb.append("&divide;"); break;  //DIVISION SIGN
                    case '\u00F8': sb.append("&oslash;"); break;  //LATIN SMALL LETTER O SLASH
                    case '\u00F9': sb.append("&ugrave;"); break;  //LATIN SMALL LETTER U GRAVE
                    case '\u00FA': sb.append("&uacute;"); break;  //LATIN SMALL LETTER U ACUTE
                    case '\u00FB': sb.append("&ucirc;"); break;  //LATIN SMALL LETTER U CIRCUMFLEX
                    case '\u00FC': sb.append("&uuml;"); break;  //LATIN SMALL LETTER U DIAERESIS
                    case '\u00FD': sb.append("&yacute;"); break;  //LATIN SMALL LETTER Y ACUTE
                    case '\u00FE': sb.append("&thorn;"); break;  //LATIN SMALL LETTER THORN
                    case '\u00FF': sb.append("&yuml;"); break;  //LATIN SMALL LETTER Y DIAERESIS
                    case '\u0100': sb.append("&Amacr;"); break;  //LATIN CAPITAL LETTER A MACRON
                    case '\u0101': sb.append("&amacr;"); break;  //LATIN SMALL LETTER A MACRON
                    case '\u0102': sb.append("&Abreve;"); break;  //LATIN CAPITAL LETTER A BREVE
                    case '\u0103': sb.append("&abreve;"); break;  //LATIN SMALL LETTER A BREVE
                    case '\u0104': sb.append("&Aogon;"); break;  //LATIN CAPITAL LETTER A OGONEK
                    case '\u0105': sb.append("&aogon;"); break;  //LATIN SMALL LETTER A OGONEK
                    case '\u0106': sb.append("&Cacute;"); break;  //LATIN CAPITAL LETTER C ACUTE
                    case '\u0107': sb.append("&cacute;"); break;  //LATIN SMALL LETTER C ACUTE
                    case '\u0108': sb.append("&Ccirc;"); break;  //LATIN CAPITAL LETTER C CIRCUMFLEX
                    case '\u0109': sb.append("&ccirc;"); break;  //LATIN SMALL LETTER C CIRCUMFLEX
                    case '\u010A': sb.append("&Cdot;"); break;  //LATIN CAPITAL LETTER C DOT
                    case '\u010B': sb.append("&cdot;"); break;  //LATIN SMALL LETTER C DOT
                    case '\u010C': sb.append("&Ccaron;"); break;  //LATIN CAPITAL LETTER C HACEK
                    case '\u010D': sb.append("&ccaron;"); break;  //LATIN SMALL LETTER C HACEK
                    case '\u010E': sb.append("&Dcaron;"); break;  //LATIN CAPITAL LETTER D HACEK
                    case '\u010F': sb.append("&dcaron;"); break;  //LATIN SMALL LETTER D HACEK
                    case '\u0110': sb.append("&Dstrok;"); break;  //LATIN CAPITAL LETTER D BAR
                    case '\u0111': sb.append("&dstrok;"); break;  //LATIN SMALL LETTER D BAR
                    case '\u0112': sb.append("&Emacr;"); break;  //LATIN CAPITAL LETTER E MACRON
                    case '\u0113': sb.append("&emacr;"); break;  //LATIN SMALL LETTER E MACRON
                    case '\u0116': sb.append("&Edot;"); break;  //LATIN CAPITAL LETTER E DOT
                    case '\u0117': sb.append("&edot;"); break;  //LATIN SMALL LETTER E DOT
                    case '\u0118': sb.append("&Eogon;"); break;  //LATIN CAPITAL LETTER E OGONEK
                    case '\u0119': sb.append("&eogon;"); break;  //LATIN SMALL LETTER E OGONEK
                    case '\u011A': sb.append("&Ecaron;"); break;  //LATIN CAPITAL LETTER E HACEK
                    case '\u011B': sb.append("&ecaron;"); break;  //LATIN SMALL LETTER E HACEK
                    case '\u011C': sb.append("&Gcirc;"); break;  //LATIN CAPITAL LETTER G CIRCUMFLEX
                    case '\u011D': sb.append("&gcirc;"); break;  //LATIN SMALL LETTER G CIRCUMFLEX
                    case '\u011E': sb.append("&Gbreve;"); break;  //LATIN CAPITAL LETTER G BREVE
                    case '\u011F': sb.append("&gbreve;"); break;  //LATIN SMALL LETTER G BREVE
                    case '\u0120': sb.append("&Gdot;"); break;  //LATIN CAPITAL LETTER G DOT
                    case '\u0121': sb.append("&gdot;"); break;  //LATIN SMALL LETTER G DOT
                    case '\u0122': sb.append("&Gcedil;"); break;  //LATIN CAPITAL LETTER G CEDILLA
                    case '\u0123': sb.append("&gcedil;"); break;  //LATIN SMALL LETTER G CEDILLA
                    case '\u0124': sb.append("&Hcirc;"); break;  //LATIN CAPITAL LETTER H CIRCUMFLEX
                    case '\u0125': sb.append("&hcirc;"); break;  //LATIN SMALL LETTER H CIRCUMFLEX
                    case '\u0126': sb.append("&Hstrok;"); break;  //LATIN CAPITAL LETTER H BAR
                    case '\u0127': sb.append("&hstrok;"); break;  //LATIN SMALL LETTER H BAR
                    case '\u0128': sb.append("&Itilde;"); break;  //LATIN CAPITAL LETTER I TILDE
                    case '\u0129': sb.append("&itilde;"); break;  //LATIN SMALL LETTER I TILDE
                    case '\u012A': sb.append("&Imacr;"); break;  //LATIN CAPITAL LETTER I MACRON
                    case '\u012B': sb.append("&imacr;"); break;  //LATIN SMALL LETTER I MACRON
                    case '\u012E': sb.append("&Iogon;"); break;  //LATIN CAPITAL LETTER I OGONEK
                    case '\u012F': sb.append("&iogon;"); break;  //LATIN SMALL LETTER I OGONEK
                    case '\u0130': sb.append("&Idot;"); break;  //LATIN CAPITAL LETTER I DOT
                    case '\u0131': sb.append("&inodot;"); break;  //LATIN SMALL LETTER DOTLESS I
                    case '\u0132': sb.append("&IJlig;"); break;  //LATIN CAPITAL LETTER I J
                    case '\u0133': sb.append("&ijlig;"); break;  //LATIN SMALL LETTER I J
                    case '\u0134': sb.append("&Jcirc;"); break;  //LATIN CAPITAL LETTER J CIRCUMFLEX
                    case '\u0135': sb.append("&jcirc;"); break;  //LATIN SMALL LETTER J CIRCUMFLEX
                    case '\u0136': sb.append("&Kcedil;"); break;  //LATIN CAPITAL LETTER K CEDILLA
                    case '\u0137': sb.append("&kcedil;"); break;  //LATIN SMALL LETTER K CEDILLA
                    case '\u0138': sb.append("&kgreen;"); break;  //LATIN SMALL LETTER KRA
                    case '\u0139': sb.append("&Lacute;"); break;  //LATIN CAPITAL LETTER L ACUTE
                    case '\u013A': sb.append("&lacute;"); break;  //LATIN SMALL LETTER L ACUTE
                    case '\u013B': sb.append("&Lcedil;"); break;  //LATIN CAPITAL LETTER L CEDILLA
                    case '\u013C': sb.append("&lcedil;"); break;  //LATIN SMALL LETTER L CEDILLA
                    case '\u013D': sb.append("&Lcaron;"); break;  //LATIN CAPITAL LETTER L HACEK
                    case '\u013E': sb.append("&lcaron;"); break;  //LATIN SMALL LETTER L HACEK
                    case '\u013F': sb.append("&Lmidot;"); break;  //LATIN CAPITAL LETTER L WITH MIDDLE DOT
                    case '\u0140': sb.append("&lmidot;"); break;  //LATIN SMALL LETTER L WITH MIDDLE DOT
                    case '\u0141': sb.append("&Lstrok;"); break;  //LATIN CAPITAL LETTER L SLASH
                    case '\u0142': sb.append("&lstrok;"); break;  //LATIN SMALL LETTER L SLASH
                    case '\u0143': sb.append("&Nacute;"); break;  //LATIN CAPITAL LETTER N ACUTE
                    case '\u0144': sb.append("&nacute;"); break;  //LATIN SMALL LETTER N ACUTE
                    case '\u0145': sb.append("&Ncedil;"); break;  //LATIN CAPITAL LETTER N CEDILLA
                    case '\u0146': sb.append("&ncedil;"); break;  //LATIN SMALL LETTER N CEDILLA
                    case '\u0147': sb.append("&Ncaron;"); break;  //LATIN CAPITAL LETTER N HACEK
                    case '\u0148': sb.append("&ncaron;"); break;  //LATIN SMALL LETTER N HACEK
                    case '\u0149': sb.append("&napos;"); break;  //LATIN SMALL LETTER APOSTROPHE N
                    case '\u014A': sb.append("&ENG;"); break;  //LATIN CAPITAL LETTER ENG
                    case '\u014B': sb.append("&eng;"); break;  //LATIN SMALL LETTER ENG
                    case '\u014C': sb.append("&Omacr;"); break;  //LATIN CAPITAL LETTER O MACRON
                    case '\u014D': sb.append("&omacr;"); break;  //LATIN SMALL LETTER O MACRON
                    case '\u0150': sb.append("&Odblac;"); break;  //LATIN CAPITAL LETTER O DOUBLE ACUTE
                    case '\u0151': sb.append("&odblac;"); break;  //LATIN SMALL LETTER O DOUBLE ACUTE
                    case '\u0152': sb.append("&OElig;"); break;  //LATIN CAPITAL LETTER O E
                    case '\u0153': sb.append("&oelig;"); break;  //LATIN SMALL LETTER O E
                    case '\u0154': sb.append("&Racute;"); break;  //LATIN CAPITAL LETTER R ACUTE
                    case '\u0155': sb.append("&racute;"); break;  //LATIN SMALL LETTER R ACUTE
                    case '\u0156': sb.append("&Rcedil;"); break;  //LATIN CAPITAL LETTER R CEDILLA
                    case '\u0157': sb.append("&rcedil;"); break;  //LATIN SMALL LETTER R CEDILLA
                    case '\u0158': sb.append("&Rcaron;"); break;  //LATIN CAPITAL LETTER R HACEK
                    case '\u0159': sb.append("&rcaron;"); break;  //LATIN SMALL LETTER R HACEK
                    case '\u015A': sb.append("&Sacute;"); break;  //LATIN CAPITAL LETTER S ACUTE
                    case '\u015B': sb.append("&sacute;"); break;  //LATIN SMALL LETTER S ACUTE
                    case '\u015C': sb.append("&Scirc;"); break;  //LATIN CAPITAL LETTER S CIRCUMFLEX
                    case '\u015D': sb.append("&scirc;"); break;  //LATIN SMALL LETTER S CIRCUMFLEX
                    case '\u015E': sb.append("&Scedil;"); break;  //LATIN CAPITAL LETTER S CEDILLA
                    case '\u015F': sb.append("&scedil;"); break;  //LATIN SMALL LETTER S CEDILLA
                    case '\u0160': sb.append("&Scaron;"); break;  //LATIN CAPITAL LETTER S HACEK
                    case '\u0161': sb.append("&scaron;"); break;  //LATIN SMALL LETTER S HACEK
                    case '\u0162': sb.append("&Tcedil;"); break;  //LATIN CAPITAL LETTER T CEDILLA
                    case '\u0163': sb.append("&tcedil;"); break;  //LATIN SMALL LETTER T CEDILLA
                    case '\u0164': sb.append("&Tcaron;"); break;  //LATIN CAPITAL LETTER T HACEK
                    case '\u0165': sb.append("&tcaron;"); break;  //LATIN SMALL LETTER T HACEK
                    case '\u0166': sb.append("&Tstrok;"); break;  //LATIN CAPITAL LETTER T BAR
                    case '\u0167': sb.append("&tstrok;"); break;  //LATIN SMALL LETTER T BAR
                    case '\u0168': sb.append("&Utilde;"); break;  //LATIN CAPITAL LETTER U TILDE
                    case '\u0169': sb.append("&utilde;"); break;  //LATIN SMALL LETTER U TILDE
                    case '\u016A': sb.append("&Umacr;"); break;  //LATIN CAPITAL LETTER U MACRON
                    case '\u016B': sb.append("&umacr;"); break;  //LATIN SMALL LETTER U MACRON
                    case '\u016C': sb.append("&Ubreve;"); break;  //LATIN CAPITAL LETTER U BREVE
                    case '\u016D': sb.append("&ubreve;"); break;  //LATIN SMALL LETTER U BREVE
                    case '\u016E': sb.append("&Uring;"); break;  //LATIN CAPITAL LETTER U RING
                    case '\u016F': sb.append("&uring;"); break;  //LATIN SMALL LETTER U RING
                    case '\u0170': sb.append("&Udblac;"); break;  //LATIN CAPITAL LETTER U DOUBLE ACUTE
                    case '\u0171': sb.append("&udblac;"); break;  //LATIN SMALL LETTER U DOUBLE ACUTE
                    case '\u0172': sb.append("&Uogon;"); break;  //LATIN CAPITAL LETTER U OGONEK
                    case '\u0173': sb.append("&uogon;"); break;  //LATIN SMALL LETTER U OGONEK
                    case '\u0174': sb.append("&Wcirc;"); break;  //LATIN CAPITAL LETTER W CIRCUMFLEX
                    case '\u0175': sb.append("&wcirc;"); break;  //LATIN SMALL LETTER W CIRCUMFLEX
                    case '\u0176': sb.append("&Ycirc;"); break;  //LATIN CAPITAL LETTER Y CIRCUMFLEX
                    case '\u0177': sb.append("&ycirc;"); break;  //LATIN SMALL LETTER Y CIRCUMFLEX
                    case '\u0178': sb.append("&Yuml;"); break;  //LATIN CAPITAL LETTER Y DIAERESIS
                    case '\u0179': sb.append("&Zacute;"); break;  //LATIN CAPITAL LETTER Z ACUTE
                    case '\u017A': sb.append("&zacute;"); break;  //LATIN SMALL LETTER Z ACUTE
                    case '\u017B': sb.append("&Zdot;"); break;  //LATIN CAPITAL LETTER Z DOT
                    case '\u017C': sb.append("&zdot;"); break;  //LATIN SMALL LETTER Z DOT
                    case '\u017D': sb.append("&Zcaron;"); break;  //LATIN CAPITAL LETTER Z HACEK
                    case '\u017E': sb.append("&zcaron;"); break;  //LATIN SMALL LETTER Z HACEK
                    case '\u0192': sb.append("&fnof;"); break;  //LATIN SMALL LETTER SCRIPT F
                    case '\u02BC': sb.append("&apos;"); break;  //MODIFIER LETTER APOSTROPHE
                    case '\u02C7': sb.append("&caron;"); break;  //MODIFIER LETTER HACEK
                    case '\u02D8': sb.append("&breve;"); break;  //SPACING BREVE
                    case '\u02D9': sb.append("&dot;"); break;  //SPACING DOT ABOVE
                    case '\u02DA': sb.append("&ring;"); break;  //SPACING RING ABOVE
                    case '\u02DB': sb.append("&ogon;"); break;  //SPACING OGONEK
                    case '\u02DC': sb.append("&tilde;"); break;  //SPACING TILDE
                    case '\u02DD': sb.append("&dblac;"); break;  //SPACING DOUBLE ACUTE
                    case '\u0386': sb.append("&Aacgr;"); break;  //GREEK CAPITAL LETTER ALPHA TONOS
                    case '\u0388': sb.append("&Eacgr;"); break;  //GREEK CAPITAL LETTER EPSILON TONOS
                    case '\u0389': sb.append("&EEacgr;"); break;  //GREEK CAPITAL LETTER ETA TONOS
                    case '\u038A': sb.append("&Iacgr;"); break;  //GREEK CAPITAL LETTER IOTA TONOS
                    case '\u038C': sb.append("&Oacgr;"); break;  //GREEK CAPITAL LETTER OMICRON TONOS
                    case '\u038E': sb.append("&Uacgr;"); break;  //GREEK CAPITAL LETTER UPSILON TONOS
                    case '\u038F': sb.append("&OHacgr;"); break;  //GREEK CAPITAL LETTER OMEGA TONOS
                    case '\u0390': sb.append("&idiagr;"); break;  //GREEK SMALL LETTER IOTA DIAERESIS TONOS
                    case '\u0391': sb.append("&Agr;"); break;  //GREEK CAPITAL LETTER ALPHA
                    case '\u0392': sb.append("&Bgr;"); break;  //GREEK CAPITAL LETTER BETA
                    case '\u0393': sb.append("&Gamma;"); break;  //GREEK CAPITAL LETTER GAMMA
                    case '\u0394': sb.append("&Delta;"); break;  //GREEK CAPITAL LETTER DELTA
                    case '\u0395': sb.append("&Egr;"); break;  //GREEK CAPITAL LETTER EPSILON
                    case '\u0396': sb.append("&Zgr;"); break;  //GREEK CAPITAL LETTER ZETA
                    case '\u0397': sb.append("&EEgr;"); break;  //GREEK CAPITAL LETTER ETA
                    case '\u0398': sb.append("&Theta;"); break;  //GREEK CAPITAL LETTER THETA
                    case '\u0399': sb.append("&Igr;"); break;  //GREEK CAPITAL LETTER IOTA
                    case '\u039A': sb.append("&Kgr;"); break;  //GREEK CAPITAL LETTER KAPPA
                    case '\u039B': sb.append("&Lambda;"); break;  //GREEK CAPITAL LETTER LAMBDA
                    case '\u039C': sb.append("&Mgr;"); break;  //GREEK CAPITAL LETTER MU
                    case '\u039D': sb.append("&Ngr;"); break;  //GREEK CAPITAL LETTER NU
                    case '\u039E': sb.append("&Xi;"); break;  //GREEK CAPITAL LETTER XI
                    case '\u039F': sb.append("&Ogr;"); break;  //GREEK CAPITAL LETTER OMICRON
                    case '\u03A0': sb.append("&Pi;"); break;  //GREEK CAPITAL LETTER PI
                    case '\u03A1': sb.append("&Rgr;"); break;  //GREEK CAPITAL LETTER RHO
                    case '\u03A3': sb.append("&Sigma;"); break;  //GREEK CAPITAL LETTER SIGMA
                    case '\u03A4': sb.append("&Tgr;"); break;  //GREEK CAPITAL LETTER TAU
                    case '\u03A5': sb.append("&Upsi;"); break;  //GREEK CAPITAL LETTER UPSILON
                    case '\u03A6': sb.append("&Phi;"); break;  //GREEK CAPITAL LETTER PHI
                    case '\u03A7': sb.append("&KHgr;"); break;  //GREEK CAPITAL LETTER CHI
                    case '\u03A8': sb.append("&Psi;"); break;  //GREEK CAPITAL LETTER PSI
                    case '\u03A9': sb.append("&Omega;"); break;  //GREEK CAPITAL LETTER OMEGA
                    case '\u03AA': sb.append("&Idigr;"); break;  //GREEK CAPITAL LETTER IOTA DIAERESIS
                    case '\u03AB': sb.append("&Udigr;"); break;  //GREEK CAPITAL LETTER UPSILON DIAERESIS
                    case '\u03AC': sb.append("&aacgr;"); break;  //GREEK SMALL LETTER ALPHA TONOS
                    case '\u03AD': sb.append("&eacgr;"); break;  //GREEK SMALL LETTER EPSILON TONOS
                    case '\u03AE': sb.append("&eeacgr;"); break;  //GREEK SMALL LETTER ETA TONOS
                    case '\u03AF': sb.append("&iacgr;"); break;  //GREEK SMALL LETTER IOTA TONOS
                    case '\u03B0': sb.append("&udiagr;"); break;  //GREEK SMALL LETTER UPSILON DIAERESIS TONOS
                    case '\u03B1': sb.append("&alpha;"); break;  //GREEK SMALL LETTER ALPHA
                    case '\u03B2': sb.append("&beta;"); break;  //GREEK SMALL LETTER BETA
                    case '\u03B3': sb.append("&gamma;"); break;  //GREEK SMALL LETTER GAMMA
                    case '\u03B4': sb.append("&delta;"); break;  //GREEK SMALL LETTER DELTA
                    case '\u03B5': sb.append("&epsi;"); break;  //GREEK SMALL LETTER EPSILON
                    case '\u03B6': sb.append("&zeta;"); break;  //GREEK SMALL LETTER ZETA
                    case '\u03B7': sb.append("&eta;"); break;  //GREEK SMALL LETTER ETA
                    case '\u03B8': sb.append("&thetas;"); break;  //GREEK SMALL LETTER THETA
                    case '\u03B9': sb.append("&iota;"); break;  //GREEK SMALL LETTER IOTA
                    case '\u03BA': sb.append("&kappa;"); break;  //GREEK SMALL LETTER KAPPA
                    case '\u03BB': sb.append("&lambda;"); break;  //GREEK SMALL LETTER LAMBDA
                    case '\u03BC': sb.append("&mu;"); break;  //GREEK SMALL LETTER MU
                    case '\u03BD': sb.append("&nu;"); break;  //GREEK SMALL LETTER NU
                    case '\u03BE': sb.append("&xi;"); break;  //GREEK SMALL LETTER XI
                    case '\u03BF': sb.append("&ogr;"); break;  //GREEK SMALL LETTER OMICRON
                    case '\u03C0': sb.append("&pi;"); break;  //GREEK SMALL LETTER PI
                    case '\u03C1': sb.append("&rho;"); break;  //GREEK SMALL LETTER RHO
                    case '\u03C2': sb.append("&sigmav;"); break;  //GREEK SMALL LETTER FINAL SIGMA
                    case '\u03C3': sb.append("&sigma;"); break;  //GREEK SMALL LETTER SIGMA
                    case '\u03C4': sb.append("&tau;"); break;  //GREEK SMALL LETTER TAU
                    case '\u03C5': sb.append("&upsi;"); break;  //GREEK SMALL LETTER UPSILON
                    case '\u03C6': sb.append("&phis;"); break;  //GREEK SMALL LETTER PHI
                    case '\u03C7': sb.append("&chi;"); break;  //GREEK SMALL LETTER CHI
                    case '\u03C8': sb.append("&psi;"); break;  //GREEK SMALL LETTER PSI
                    case '\u03C9': sb.append("&omega;"); break;  //GREEK SMALL LETTER OMEGA
                    case '\u03CA': sb.append("&idigr;"); break;  //GREEK SMALL LETTER IOTA DIAERESIS
                    case '\u03CB': sb.append("&udigr;"); break;  //GREEK SMALL LETTER UPSILON DIAERESIS
                    case '\u03CC': sb.append("&oacgr;"); break;  //GREEK SMALL LETTER OMICRON TONOS
                    case '\u03CD': sb.append("&uacgr;"); break;  //GREEK SMALL LETTER UPSILON TONOS
                    case '\u03CE': sb.append("&ohacgr;"); break;  //GREEK SMALL LETTER OMEGA TONOS
                    case '\u03D1': sb.append("&thetav;"); break;  //GREEK SMALL LETTER SCRIPT THETA
                    case '\u03D5': sb.append("&phiv;"); break;  //GREEK SMALL LETTER SCRIPT PHI
                    case '\u03D6': sb.append("&piv;"); break;  //GREEK SMALL LETTER OMEGA PI
                    case '\u03DD': sb.append("&gammad;"); break;  //GREEK SMALL LETTER DIGAMMA
                    case '\u03F0': sb.append("&kappav;"); break;  //GREEK SMALL LETTER SCRIPT KAPPA
                    case '\u03F1': sb.append("&rhov;"); break;  //GREEK SMALL LETTER TAILED RHO
                    case '\u0401': sb.append("&IOcy;"); break;  //CYRILLIC CAPITAL LETTER IO
                    case '\u0402': sb.append("&DJcy;"); break;  //CYRILLIC CAPITAL LETTER DJE
                    case '\u0403': sb.append("&GJcy;"); break;  //CYRILLIC CAPITAL LETTER GJE
                    case '\u0404': sb.append("&Jukcy;"); break;  //CYRILLIC CAPITAL LETTER E
                    case '\u0405': sb.append("&DScy;"); break;  //CYRILLIC CAPITAL LETTER DZE
                    case '\u0406': sb.append("&Iukcy;"); break;  //CYRILLIC CAPITAL LETTER I
                    case '\u0407': sb.append("&YIcy;"); break;  //CYRILLIC CAPITAL LETTER YI
                    case '\u0408': sb.append("&Jsercy;"); break;  //CYRILLIC CAPITAL LETTER JE
                    case '\u0409': sb.append("&LJcy;"); break;  //CYRILLIC CAPITAL LETTER LJE
                    case '\u040A': sb.append("&NJcy;"); break;  //CYRILLIC CAPITAL LETTER NJE
                    case '\u040B': sb.append("&TSHcy;"); break;  //CYRILLIC CAPITAL LETTER TSHE
                    case '\u040C': sb.append("&KJcy;"); break;  //CYRILLIC CAPITAL LETTER KJE
                    case '\u040E': sb.append("&Ubrcy;"); break;  //CYRILLIC CAPITAL LETTER SHORT U
                    case '\u040F': sb.append("&DZcy;"); break;  //CYRILLIC CAPITAL LETTER DZHE
                    case '\u0410': sb.append("&Acy;"); break;  //CYRILLIC CAPITAL LETTER A
                    case '\u0411': sb.append("&Bcy;"); break;  //CYRILLIC CAPITAL LETTER BE
                    case '\u0412': sb.append("&Vcy;"); break;  //CYRILLIC CAPITAL LETTER VE
                    case '\u0413': sb.append("&Gcy;"); break;  //CYRILLIC CAPITAL LETTER GE
                    case '\u0414': sb.append("&dcy;"); break;  //CYRILLIC CAPITAL LETTER DE
                    case '\u0415': sb.append("&IEcy;"); break;  //CYRILLIC CAPITAL LETTER IE
                    case '\u0416': sb.append("&ZHcy;"); break;  //CYRILLIC CAPITAL LETTER ZHE
                    case '\u0417': sb.append("&Zcy;"); break;  //CYRILLIC CAPITAL LETTER ZE
                    case '\u0418': sb.append("&Icy;"); break;  //CYRILLIC CAPITAL LETTER II
                    case '\u0419': sb.append("&Jcy;"); break;  //CYRILLIC CAPITAL LETTER SHORT II
                    case '\u041A': sb.append("&Kcy;"); break;  //CYRILLIC CAPITAL LETTER KA
                    case '\u041B': sb.append("&Lcy;"); break;  //CYRILLIC CAPITAL LETTER EL
                    case '\u041C': sb.append("&Mcy;"); break;  //CYRILLIC CAPITAL LETTER EM
                    case '\u041D': sb.append("&Ncy;"); break;  //CYRILLIC CAPITAL LETTER EN
                    case '\u041E': sb.append("&Ocy;"); break;  //CYRILLIC CAPITAL LETTER O
                    case '\u041F': sb.append("&Pcy;"); break;  //CYRILLIC CAPITAL LETTER PE
                    case '\u0420': sb.append("&Rcy;"); break;  //CYRILLIC CAPITAL LETTER ER
                    case '\u0421': sb.append("&Scy;"); break;  //CYRILLIC CAPITAL LETTER ES
                    case '\u0422': sb.append("&Tcy;"); break;  //CYRILLIC CAPITAL LETTER TE
                    case '\u0423': sb.append("&Ucy;"); break;  //CYRILLIC CAPITAL LETTER U
                    case '\u0424': sb.append("&Fcy;"); break;  //CYRILLIC CAPITAL LETTER EF
                    case '\u0425': sb.append("&KHcy;"); break;  //CYRILLIC CAPITAL LETTER KHA
                    case '\u0426': sb.append("&TScy;"); break;  //CYRILLIC CAPITAL LETTER TSE
                    case '\u0427': sb.append("&CHcy;"); break;  //CYRILLIC CAPITAL LETTER CHE
                    case '\u0428': sb.append("&SHcy;"); break;  //CYRILLIC CAPITAL LETTER SHA
                    case '\u0429': sb.append("&SHCHcy;"); break;  //CYRILLIC CAPITAL LETTER SHCHA
                    case '\u042A': sb.append("&HARDcy;"); break;  //CYRILLIC CAPITAL LETTER HARD SIGN
                    case '\u042B': sb.append("&Ycy;"); break;  //CYRILLIC CAPITAL LETTER YERI
                    case '\u042C': sb.append("&SOFTcy;"); break;  //CYRILLIC CAPITAL LETTER SOFT SIGN
                    case '\u042D': sb.append("&Ecy;"); break;  //CYRILLIC CAPITAL LETTER REVERSED E
                    case '\u042E': sb.append("&YUcy;"); break;  //CYRILLIC CAPITAL LETTER IU
                    case '\u042F': sb.append("&YAcy;"); break;  //CYRILLIC CAPITAL LETTER IA
                    case '\u0430': sb.append("&acy;"); break;  //CYRILLIC SMALL LETTER A
                    case '\u0431': sb.append("&bcy;"); break;  //CYRILLIC SMALL LETTER BE
                    case '\u0432': sb.append("&vcy;"); break;  //CYRILLIC SMALL LETTER VE
                    case '\u0433': sb.append("&gcy;"); break;  //CYRILLIC SMALL LETTER GE
                    case '\u0434': sb.append("&dcy;"); break;  //CYRILLIC SMALL LETTER DE
                    case '\u0435': sb.append("&iecy;"); break;  //CYRILLIC SMALL LETTER IE
                    case '\u0436': sb.append("&zhcy;"); break;  //CYRILLIC SMALL LETTER ZHE
                    case '\u0437': sb.append("&zcy;"); break;  //CYRILLIC SMALL LETTER ZE
                    case '\u0438': sb.append("&icy;"); break;  //CYRILLIC SMALL LETTER II
                    case '\u0439': sb.append("&jcy;"); break;  //CYRILLIC SMALL LETTER SHORT II
                    case '\u043A': sb.append("&kcy;"); break;  //CYRILLIC SMALL LETTER KA
                    case '\u043B': sb.append("&lcy;"); break;  //CYRILLIC SMALL LETTER EL
                    case '\u043C': sb.append("&mcy;"); break;  //CYRILLIC SMALL LETTER EM
                    case '\u043D': sb.append("&ncy;"); break;  //CYRILLIC SMALL LETTER EN
                    case '\u043E': sb.append("&ocy;"); break;  //CYRILLIC SMALL LETTER O
                    case '\u043F': sb.append("&pcy;"); break;  //CYRILLIC SMALL LETTER PE
                    case '\u0440': sb.append("&rcy;"); break;  //CYRILLIC SMALL LETTER ER
                    case '\u0441': sb.append("&scy;"); break;  //CYRILLIC SMALL LETTER ES
                    case '\u0442': sb.append("&tcy;"); break;  //CYRILLIC SMALL LETTER TE
                    case '\u0443': sb.append("&ucy;"); break;  //CYRILLIC SMALL LETTER U
                    case '\u0444': sb.append("&fcy;"); break;  //CYRILLIC SMALL LETTER EF
                    case '\u0445': sb.append("&khcy;"); break;  //CYRILLIC SMALL LETTER KHA
                    case '\u0446': sb.append("&tscy;"); break;  //CYRILLIC SMALL LETTER TSE
                    case '\u0447': sb.append("&chcy;"); break;  //CYRILLIC SMALL LETTER CHE
                    case '\u0448': sb.append("&shcy;"); break;  //CYRILLIC SMALL LETTER SHA
                    case '\u0449': sb.append("&shchcy;"); break;  //CYRILLIC SMALL LETTER SHCHA
                    case '\u044A': sb.append("&hardcy;"); break;  //CYRILLIC SMALL LETTER HARD SIGN
                    case '\u044B': sb.append("&ycy;"); break;  //CYRILLIC SMALL LETTER YERI
                    case '\u044C': sb.append("&softcy;"); break;  //CYRILLIC SMALL LETTER SOFT SIGN
                    case '\u044D': sb.append("&ecy;"); break;  //CYRILLIC SMALL LETTER REVERSED E
                    case '\u044E': sb.append("&yucy;"); break;  //CYRILLIC SMALL LETTER IU
                    case '\u044F': sb.append("&yacy;"); break;  //CYRILLIC SMALL LETTER IA
                    case '\u0451': sb.append("&iocy;"); break;  //CYRILLIC SMALL LETTER IO
                    case '\u0452': sb.append("&djcy;"); break;  //CYRILLIC SMALL LETTER DJE
                    case '\u0453': sb.append("&gjcy;"); break;  //CYRILLIC SMALL LETTER GJE
                    case '\u0454': sb.append("&jukcy;"); break;  //CYRILLIC SMALL LETTER E
                    case '\u0455': sb.append("&dscy;"); break;  //CYRILLIC SMALL LETTER DZE
                    case '\u0456': sb.append("&iukcy;"); break;  //CYRILLIC SMALL LETTER I
                    case '\u0457': sb.append("&yicy;"); break;  //CYRILLIC SMALL LETTER YI
                    case '\u0458': sb.append("&jsercy;"); break;  //CYRILLIC SMALL LETTER JE
                    case '\u0459': sb.append("&ljcy;"); break;  //CYRILLIC SMALL LETTER LJE
                    case '\u045A': sb.append("&njcy;"); break;  //CYRILLIC SMALL LETTER NJE
                    case '\u045B': sb.append("&tshcy;"); break;  //CYRILLIC SMALL LETTER TSHE
                    case '\u045C': sb.append("&kjcy;"); break;  //CYRILLIC SMALL LETTER KJE
                    case '\u045E': sb.append("&ubrcy;"); break;  //CYRILLIC SMALL LETTER SHORT U
                    case '\u045F': sb.append("&dzcy;"); break;  //CYRILLIC SMALL LETTER DZHE
                    case '\u2002': sb.append("&ensp;"); break;  //EN SPACE
                    case '\u2003': sb.append("&emsp;"); break;  //EM SPACE
                    case '\u2004': sb.append("&emsp13;"); break;  //THREE-PER-EM SPACE
                    case '\u2005': sb.append("&emsp14;"); break;  //FOUR-PER-EM SPACE
                    case '\u2007': sb.append("&numsp;"); break;  //FIGURE SPACE
                    case '\u2008': sb.append("&puncsp;"); break;  //PUNCTUATION SPACE
                    case '\u2009': sb.append("&thinsp;"); break;  //THIN SPACE
                    case '\u200A': sb.append("&hairsp;"); break;  //HAIR SPACE
                    case '\u2010': sb.append("&dash;"); break;  //HYPHEN
                    case '\u2013': sb.append("&ndash;"); break;  //EN DASH
                    case '\u2014': sb.append("&mdash;"); break;  //EM DASH
                    case '\u2015': sb.append("&horbar;"); break;  //QUOTATION DASH
                    case '\u2016': sb.append("&Verbar;"); break;  //DOUBLE VERTICAL BAR
                    case '\u2018': sb.append("&lsquo;"); break;  //SINGLE TURNED COMMA QUOTATION MARK
                    case '\u2019': sb.append("&rsquo;"); break;  //SINGLE COMMA QUOTATION MARK
                    case '\u201A': sb.append("&lsquor;"); break;  //LOW SINGLE COMMA QUOTATION MARK
                    case '\u201C': sb.append("&ldquo;"); break;  //DOUBLE TURNED COMMA QUOTATION MARK
                    case '\u201D': sb.append("&rdquo;"); break;  //DOUBLE COMMA QUOTATION MARK
                    case '\u201E': sb.append("&ldquor;"); break;  //LOW DOUBLE COMMA QUOTATION MARK
                    case '\u2020': sb.append("&dagger;"); break;  //DAGGER
                    case '\u2021': sb.append("&Dagger;"); break;  //DOUBLE DAGGER
                    case '\u2022': sb.append("&bull;"); break;  //BULLET
                    case '\u2025': sb.append("&nldr;"); break;  //TWO DOT LEADER
                    case '\u2026': sb.append("&mldr;"); break;  //HORIZONTAL ELLIPSIS
                    case '\u2030': sb.append("&permil;"); break;  //PER MILLE SIGN
                    case '\u2032': sb.append("&prime;"); break;  //PRIME
                    case '\u2033': sb.append("&Prime;"); break;  //DOUBLE PRIME
                    case '\u2034': sb.append("&tprime;"); break;  //TRIPLE PRIME
                    case '\u2035': sb.append("&bprime;"); break;  //REVERSED PRIME
                    case '\u2041': sb.append("&caret;"); break;  //CARET INSERTION POINT
                    case '\u2043': sb.append("&hybull;"); break;  //HYPHEN BULLET
                    case '\u20DB': sb.append("&tdot;"); break;  //NON-SPACING THREE DOTS ABOVE
                    case '\u20DC': sb.append("&DotDot;"); break;  //NON-SPACING FOUR DOTS ABOVE
                    case '\u2105': sb.append("&incare;"); break;  //CARE OF
                    case '\u210B': sb.append("&hamilt;"); break;  //SCRIPT H
                    case '\u210F': sb.append("&planck;"); break;  //PLANCK CONSTANT OVER 2 PI
                    case '\u2111': sb.append("&image;"); break;  //BLACK-LETTER I
                    case '\u2112': sb.append("&lagran;"); break;  //SCRIPT L
                    case '\u2113': sb.append("&ell;"); break;  //SCRIPT SMALL L
                    case '\u2116': sb.append("&numero;"); break;  //NUMERO
                    case '\u2117': sb.append("&copysr;"); break;  //SOUND RECORDING COPYRIGHT
                    case '\u2118': sb.append("&weierp;"); break;  //SCRIPT P
                    case '\u211C': sb.append("&real;"); break;  //BLACK-LETTER R
                    case '\u211E': sb.append("&rx;"); break;  //PRESCRIPTION TAKE
                    case '\u2122': sb.append("&trade;"); break;  //TRADEMARK
                    case '\u2126': sb.append("&ohm;"); break;  //OHM
                    case '\u212B': sb.append("&angst;"); break;  //ANGSTROM UNIT
                    case '\u212C': sb.append("&bernou;"); break;  //SCRIPT B
                    case '\u2133': sb.append("&phmmat;"); break;  //SCRIPT M
                    case '\u2134': sb.append("&order;"); break;  //SCRIPT SMALL O
                    case '\u2135': sb.append("&aleph;"); break;  //FIRST TRANSFINITE CARDINAL
                    case '\u2136': sb.append("&beth;"); break;  //SECOND TRANSFINITE CARDINAL
                    case '\u2137': sb.append("&gimel;"); break;  //THIRD TRANSFINITE CARDINAL
                    case '\u2138': sb.append("&daleth;"); break;  //FOURTH TRANSFINITE CARDINAL
                    case '\u2153': sb.append("&frac13;"); break;  //FRACTION ONE THIRD
                    case '\u2154': sb.append("&frac23;"); break;  //FRACTION TWO THIRDS
                    case '\u2155': sb.append("&frac15;"); break;  //FRACTION ONE FIFTH
                    case '\u2156': sb.append("&frac25;"); break;  //FRACTION TWO FIFTHS
                    case '\u2157': sb.append("&frac35;"); break;  //FRACTION THREE FIFTHS
                    case '\u2158': sb.append("&frac45;"); break;  //FRACTION FOUR FIFTHS
                    case '\u2159': sb.append("&frac16;"); break;  //FRACTION ONE SIXTH
                    case '\u215A': sb.append("&frac56;"); break;  //FRACTION FIVE SIXTHS
                    case '\u215B': sb.append("&frac18;"); break;  //FRACTION ONE EIGHTH
                    case '\u215C': sb.append("&frac38;"); break;  //FRACTION THREE EIGHTHS
                    case '\u215D': sb.append("&frac58;"); break;  //FRACTION FIVE EIGHTHS
                    case '\u215E': sb.append("&frac78;"); break;  //FRACTION SEVEN EIGHTHS
                    case '\u2190': sb.append("&larr;"); break;  //LEFT ARROW
                    case '\u2191': sb.append("&uarr;"); break;  //UP ARROW
                    case '\u2192': sb.append("&rarr;"); break;  //RIGHT ARROW
                    case '\u2193': sb.append("&darr;"); break;  //DOWN ARROW
                    case '\u2194': sb.append("&harr;"); break;  //LEFT RIGHT ARROW
                    case '\u2195': sb.append("&varr;"); break;  //UP DOWN ARROW
                    case '\u2196': sb.append("&nwarr;"); break;  //UPPER LEFT ARROW
                    case '\u2197': sb.append("&nearr;"); break;  //UPPER RIGHT ARROW
                    case '\u2198': sb.append("&drarr;"); break;  //LOWER RIGHT ARROW
                    case '\u2199': sb.append("&dlarr;"); break;  //LOWER LEFT ARROW
                    case '\u219A': sb.append("&nlarr;"); break;  //LEFT ARROW WITH STROKE
                    case '\u219B': sb.append("&nrarr;"); break;  //RIGHT ARROW WITH STROKE
                    case '\u219D': sb.append("&rarrw;"); break;  //RIGHT WAVE ARROW
                    case '\u219E': sb.append("&Larr;"); break;  //LEFT TWO HEADED ARROW
                    case '\u21A0': sb.append("&Rarr;"); break;  //RIGHT TWO HEADED ARROW
                    case '\u21A2': sb.append("&larrtl;"); break;  //LEFT ARROW WITH TAIL
                    case '\u21A3': sb.append("&rarrtl;"); break;  //RIGHT ARROW WITH TAIL
                    case '\u21A6': sb.append("&map;"); break;  //RIGHT ARROW FROM BAR
                    case '\u21A9': sb.append("&larrhk;"); break;  //LEFT ARROW WITH HOOK
                    case '\u21AA': sb.append("&rarrhk;"); break;  //RIGHT ARROW WITH HOOK
                    case '\u21AB': sb.append("&larrlp;"); break;  //LEFT ARROW WITH LOOP
                    case '\u21AC': sb.append("&rarrlp;"); break;  //RIGHT ARROW WITH LOOP
                    case '\u21AD': sb.append("&harrw;"); break;  //LEFT RIGHT WAVE ARROW
                    case '\u21AE': sb.append("&nharr;"); break;  //LEFT RIGHT ARROW WITH STROKE
                    case '\u21B0': sb.append("&lsh;"); break;  //UP ARROW WITH TIP LEFT
                    case '\u21B1': sb.append("&rsh;"); break;  //UP ARROW WITH TIP RIGHT
                    case '\u21B6': sb.append("&cularr;"); break;  //ANTICLOCKWISE TOP SEMICIRCLE ARROW
                    case '\u21B7': sb.append("&curarr;"); break;  //CLOCKWISE TOP SEMICIRCLE ARROW
                    case '\u21BA': sb.append("&olarr;"); break;  //ANTICLOCKWISE OPEN CIRCLE ARROW
                    case '\u21BB': sb.append("&orarr;"); break;  //CLOCKWISE OPEN CIRCLE ARROW
                    case '\u21BC': sb.append("&lharu;"); break;  //LEFT HARPOON WITH BARB UP
                    case '\u21BD': sb.append("&lhard;"); break;  //LEFT HARPOON WITH BARB DOWN
                    case '\u21BE': sb.append("&uharr;"); break;  //UP HARPOON WITH BARB RIGHT
                    case '\u21BF': sb.append("&uharl;"); break;  //UP HARPOON WITH BARB LEFT
                    case '\u21C0': sb.append("&rharu;"); break;  //RIGHT HARPOON WITH BARB UP
                    case '\u21C1': sb.append("&rhard;"); break;  //RIGHT HARPOON WITH BARB DOWN
                    case '\u21C2': sb.append("&dharr;"); break;  //DOWN HARPOON WITH BARB RIGHT
                    case '\u21C3': sb.append("&dharl;"); break;  //DOWN HARPOON WITH BARB LEFT
                    case '\u21C4': sb.append("&rlarr2;"); break;  //RIGHT ARROW OVER LEFT ARROW
                    case '\u21C6': sb.append("&lrarr2;"); break;  //LEFT ARROW OVER RIGHT ARROW
                    case '\u21C7': sb.append("&larr2;"); break;  //LEFT PAIRED ARROWS
                    case '\u21C8': sb.append("&uarr2;"); break;  //UP PAIRED ARROWS
                    case '\u21C9': sb.append("&rarr2;"); break;  //RIGHT PAIRED ARROWS
                    case '\u21CA': sb.append("&darr2;"); break;  //DOWN PAIRED ARROWS
                    case '\u21CB': sb.append("&lrhar2;"); break;  //LEFT HARPOON OVER RIGHT HARPOON
                    case '\u21CC': sb.append("&rlhar2;"); break;  //RIGHT HARPOON OVER LEFT HARPOON
                    case '\u21CD': sb.append("&nlArr;"); break;  //LEFT DOUBLE ARROW WITH STROKE
                    case '\u21CE': sb.append("&nhArr;"); break;  //LEFT RIGHT DOUBLE ARROW WITH STROKE
                    case '\u21CF': sb.append("&nrArr;"); break;  //RIGHT DOUBLE ARROW WITH STROKE
                    case '\u21D0': sb.append("&lArr;"); break;  //LEFT DOUBLE ARROW
                    case '\u21D1': sb.append("&uArr;"); break;  //UP DOUBLE ARROW
                    case '\u21D2': sb.append("&rArr;"); break;  //RIGHT DOUBLE ARROW
                    case '\u21D3': sb.append("&dArr;"); break;  //DOWN DOUBLE ARROW
                    case '\u21D4': sb.append("&hArr;"); break;  //LEFT RIGHT DOUBLE ARROW
                    case '\u21D5': sb.append("&vArr;"); break;  //UP DOWN DOUBLE ARROW
                    case '\u21DA': sb.append("&lAarr;"); break;  //LEFT TRIPLE ARROW
                    case '\u21DB': sb.append("&rAarr;"); break;  //RIGHT TRIPLE ARROW
                    case '\u21DD': sb.append("&rarrw;"); break;  //RIGHT SQUIGGLE ARROW
                    case '\u2200': sb.append("&forall;"); break;  //FOR ALL
                    case '\u2201': sb.append("&comp;"); break;  //COMPLEMENT
                    case '\u2202': sb.append("&part;"); break;  //PARTIAL DIFFERENTIAL
                    case '\u2203': sb.append("&exist;"); break;  //THERE EXISTS
                    case '\u2204': sb.append("&nexist;"); break;  //THERE DOES NOT EXIST
                    case '\u2205': sb.append("&empty;"); break;  //EMPTY SET
                    case '\u2207': sb.append("&nabla;"); break;  //NABLA
                    case '\u2208': sb.append("&isin;"); break;  //ELEMENT OF
                    case '\u2209': sb.append("&notin;"); break;  //NOT AN ELEMENT OF
                    case '\u220A': sb.append("&epsis;"); break;  //SMALL ELEMENT OF
                    case '\u220B': sb.append("&ni;"); break;  //CONTAINS AS MEMBER
                    case '\u220D': sb.append("&bepsi;"); break;  //SMALL CONTAINS AS MEMBER
                    case '\u220F': sb.append("&prod;"); break;  //N-ARY PRODUCT
                    case '\u2210': sb.append("&amalg;"); break;  //N-ARY COPRODUCT
                    case '\u2211': sb.append("&sum;"); break;  //N-ARY SUMMATION
                    case '\u2212': sb.append("&minus;"); break;  //MINUS SIGN
                    case '\u2213': sb.append("&mnplus;"); break;  //MINUS-OR-PLUS SIGN
                    case '\u2214': sb.append("&plusdo;"); break;  //DOT PLUS
                    case '\u2216': sb.append("&setmn;"); break;  //SET MINUS
                    case '\u2218': sb.append("&compfn;"); break;  //RING OPERATOR
                    case '\u221A': sb.append("&radic;"); break;  //SQUARE ROOT
                    case '\u221D': sb.append("&prop;"); break;  //PROPORTIONAL TO
                    case '\u221E': sb.append("&infin;"); break;  //INFINITY
                    case '\u221F': sb.append("&ang90;"); break;  //RIGHT ANGLE
                    case '\u2220': sb.append("&ang;"); break;  //ANGLE
                    case '\u2221': sb.append("&angmsd;"); break;  //MEASURED ANGLE
                    case '\u2222': sb.append("&angsph;"); break;  //SPHERICAL ANGLE
                    case '\u2223': sb.append("&mid;"); break;  //DIVIDES
                    case '\u2224': sb.append("&nmid;"); break;  //DOES NOT DIVIDE
                    case '\u2225': sb.append("&par;"); break;  //PARALLEL TO
                    case '\u2226': sb.append("&npar;"); break;  //NOT PARALLEL TO
                    case '\u2227': sb.append("&and;"); break;  //LOGICAL AND
                    case '\u2228': sb.append("&or;"); break;  //LOGICAL OR
                    case '\u2229': sb.append("&cap;"); break;  //INTERSECTION
                    case '\u222A': sb.append("&cup;"); break;  //UNION
                    case '\u222B': sb.append("&int;"); break;  //INTEGRAL
                    case '\u222E': sb.append("&conint;"); break;  //CONTOUR INTEGRAL
                    case '\u2234': sb.append("&there4;"); break;  //THEREFORE
                    case '\u2235': sb.append("&becaus;"); break;  //BECAUSE
                    case '\u223C': sb.append("&sim;"); break;  //TILDE OPERATOR
                    case '\u223D': sb.append("&bsim;"); break;  //REVERSED TILDE
                    case '\u2240': sb.append("&wreath;"); break;  //WREATH PRODUCT
                    case '\u2241': sb.append("&nsim;"); break;  //NOT TILDE
                    case '\u2243': sb.append("&sime;"); break;  //ASYMPTOTICALLY EQUAL TO
                    case '\u2244': sb.append("&nsime;"); break;  //NOT ASYMPTOTICALLY EQUAL TO
                    case '\u2245': sb.append("&cong;"); break;  //APPROXIMATELY EQUAL TO
                    case '\u2247': sb.append("&ncong;"); break;  //NEITHER APPROXIMATELY NOR ACTUALLY EQUAL TO
                    case '\u2248': sb.append("&ap;"); break;  //ALMOST EQUAL TO
                    case '\u2249': sb.append("&nap;"); break;  //NOT ALMOST EQUAL TO
                    case '\u224A': sb.append("&ape;"); break;  //ALMOST EQUAL OR EQUAL TO
                    case '\u224C': sb.append("&bcong;"); break;  //ALL EQUAL TO
                    case '\u224D': sb.append("&asymp;"); break;  //EQUIVALENT TO
                    case '\u224E': sb.append("&bump;"); break;  //GEOMETRICALLY EQUIVALENT TO
                    case '\u224F': sb.append("&bumpe;"); break;  //DIFFERENCE BETWEEN
                    case '\u2250': sb.append("&esdot;"); break;  //APPROACHES THE LIMIT
                    case '\u2251': sb.append("&eDot;"); break;  //GEOMETRICALLY EQUAL TO
                    case '\u2252': sb.append("&efDot;"); break;  //APPROXIMATELY EQUAL TO OR THE IMAGE OF
                    case '\u2253': sb.append("&erDot;"); break;  //IMAGE OF OR APPROXIMATELY EQUAL TO
                    case '\u2254': sb.append("&colone;"); break;  //COLON EQUAL
                    case '\u2255': sb.append("&ecolon;"); break;  //EQUAL COLON
                    case '\u2256': sb.append("&ecir;"); break;  //RING IN EQUAL TO
                    case '\u2257': sb.append("&cire;"); break;  //RING EQUAL TO
                    case '\u2259': sb.append("&wedgeq;"); break;  //ESTIMATES
                    case '\u225C': sb.append("&trie;"); break;  //DELTA EQUAL TO
                    case '\u2260': sb.append("&ne;"); break;  //NOT EQUAL TO
                    case '\u2261': sb.append("&equiv;"); break;  //IDENTICAL TO
                    case '\u2262': sb.append("&nequiv;"); break;  //NOT IDENTICAL TO
                    case '\u2264': sb.append("&le;"); break;  //LESS THAN OR EQUAL TO
                    case '\u2265': sb.append("&ge;"); break;  //GREATER THAN OR EQUAL TO
                    case '\u2266': sb.append("&lE;"); break;  //LESS THAN OVER EQUAL TO
                    case '\u2267': sb.append("&gE;"); break;  //GREATER THAN OVER EQUAL TO
                    case '\u2268': sb.append("&lnE;"); break;  //LESS THAN BUT NOT EQUAL TO
                    case '\u2269': sb.append("&gnE;"); break;  //GREATER THAN BUT NOT EQUAL TO
                    case '\u226A': sb.append("&Lt;"); break;  //MUCH LESS THAN
                    case '\u226B': sb.append("&Gt;"); break;  //MUCH GREATER THAN
                    case '\u226C': sb.append("&twixt;"); break;  //BETWEEN
                    case '\u226E': sb.append("&nlt;"); break;  //NOT LESS THAN
                    case '\u226F': sb.append("&ngt;"); break;  //NOT GREATER THAN
                    case '\u2270': sb.append("&nle;"); break;  //NEITHER LESS THAN NOR EQUAL TO
                    case '\u2271': sb.append("&nge;"); break;  //NEITHER GREATER THAN NOR EQUAL TO
                    case '\u2272': sb.append("&lsim;"); break;  //LESS THAN OR EQUIVALENT TO
                    case '\u2273': sb.append("&gsim;"); break;  //GREATER THAN OR EQUIVALENT TO
                    case '\u2276': sb.append("&lg;"); break;  //LESS THAN OR GREATER THAN
                    case '\u2277': sb.append("&gl;"); break;  //GREATER THAN OR LESS THAN
                    case '\u227A': sb.append("&pr;"); break;  //PRECEDES
                    case '\u227B': sb.append("&sc;"); break;  //SUCCEEDS
                    case '\u227C': sb.append("&cupre;"); break;  //PRECEDES OR EQUAL TO
                    case '\u227D': sb.append("&sccue;"); break;  //SUCCEEDS OR EQUAL TO
                    case '\u227E': sb.append("&prsim;"); break;  //PRECEDES OR EQUIVALENT TO
                    case '\u227F': sb.append("&scsim;"); break;  //SUCCEEDS OR EQUIVALENT TO
                    case '\u2280': sb.append("&npr;"); break;  //DOES NOT PRECEDE
                    case '\u2281': sb.append("&nsc;"); break;  //DOES NOT SUCCEED
                    case '\u2282': sb.append("&sub;"); break;  //SUBSET OF
                    case '\u2283': sb.append("&sup;"); break;  //SUPERSET OF
                    case '\u2284': sb.append("&nsub;"); break;  //NOT A SUBSET OF
                    case '\u2285': sb.append("&nsup;"); break;  //NOT A SUPERSET OF
                    case '\u2286': sb.append("&sube;"); break;  //SUBSET OF OR EQUAL TO
                    case '\u2287': sb.append("&supe;"); break;  //SUPERSET OF OR EQUAL TO
                    case '\u2288': sb.append("&nsube;"); break;  //NEITHER A SUBSET OF NOR EQUAL TO
                    case '\u2289': sb.append("&nsupe;"); break;  //NEITHER A SUPERSET OF NOR EQUAL TO
                    case '\u228A': sb.append("&subnE;"); break;  //SUBSET OF OR NOT EQUAL TO
                    case '\u228B': sb.append("&supnE;"); break;  //SUPERSET OF OR NOT EQUAL TO
                    case '\u228E': sb.append("&uplus;"); break;  //MULTISET UNION
                    case '\u228F': sb.append("&sqsub;"); break;  //SQUARE IMAGE OF
                    case '\u2290': sb.append("&sqsup;"); break;  //SQUARE ORIGINAL OF
                    case '\u2291': sb.append("&sqsube;"); break;  //SQUARE IMAGE OF OR EQUAL TO
                    case '\u2292': sb.append("&sqsupe;"); break;  //SQUARE ORIGINAL OF OR EQUAL TO
                    case '\u2293': sb.append("&sqcap;"); break;  //SQUARE CAP
                    case '\u2294': sb.append("&sqcup;"); break;  //SQUARE CUP
                    case '\u2295': sb.append("&oplus;"); break;  //CIRCLED PLUS
                    case '\u2296': sb.append("&ominus;"); break;  //CIRCLED MINUS
                    case '\u2297': sb.append("&otimes;"); break;  //CIRCLED TIMES
                    case '\u2298': sb.append("&osol;"); break;  //CIRCLED DIVISION SLASH
                    case '\u2299': sb.append("&odot;"); break;  //CIRCLED DOT OPERATOR
                    case '\u229A': sb.append("&ocir;"); break;  //CIRCLED RING OPERATOR
                    case '\u229B': sb.append("&oast;"); break;  //CIRCLED ASTERISK OPERATOR
                    case '\u229D': sb.append("&odash;"); break;  //CIRCLED DASH
                    case '\u229E': sb.append("&plusb;"); break;  //SQUARED PLUS
                    case '\u229F': sb.append("&minusb;"); break;  //SQUARED MINUS
                    case '\u22A0': sb.append("&timesb;"); break;  //SQUARED TIMES
                    case '\u22A1': sb.append("&sdotb;"); break;  //SQUARED DOT OPERATOR
                    case '\u22A2': sb.append("&vdash;"); break;  //RIGHT TACK
                    case '\u22A3': sb.append("&dashv;"); break;  //LEFT TACK
                    case '\u22A4': sb.append("&top;"); break;  //DOWN TACK
                    case '\u22A5': sb.append("&bottom;"); break;  //UP TACK
                    case '\u22A7': sb.append("&models;"); break;  //MODELS
                    case '\u22A8': sb.append("&vDash;"); break;  //TRUE
                    case '\u22A9': sb.append("&Vdash;"); break;  //FORCES
                    case '\u22AA': sb.append("&Vvdash;"); break;  //TRIPLE VERTICAL BAR RIGHT TURNSTILE
                    case '\u22AC': sb.append("&nvdash;"); break;  //DOES NOT PROVE
                    case '\u22AD': sb.append("&nvDash;"); break;  //NOT TRUE
                    case '\u22AE': sb.append("&nVdash;"); break;  //DOES NOT FORCE
                    case '\u22AF': sb.append("&nVDash;"); break;  //NEGATED DOUBLE VERTICAL BAR DOUBLE RIGHT TURNSTILE
                    case '\u22B2': sb.append("&vltri;"); break;  //NORMAL SUBGROUP OF
                    case '\u22B3': sb.append("&vrtri;"); break;  //CONTAINS AS NORMAL SUBGROUP
                    case '\u22B4': sb.append("&ltrie;"); break;  //NORMAL SUBGROUP OF OR EQUAL TO
                    case '\u22B5': sb.append("&rtrie;"); break;  //CONTAINS AS NORMAL SUBGROUP OR EQUAL TO
                    case '\u22B8': sb.append("&mumap;"); break;  //MULTIMAP
                    case '\u22BA': sb.append("&intcal;"); break;  //INTERCALATE
                    case '\u22BB': sb.append("&veebar;"); break;  //XOR
                    case '\u22BC': sb.append("&barwed;"); break;  //NAND
                    case '\u22C4': sb.append("&diam;"); break;  //DIAMOND OPERATOR
                    case '\u22C5': sb.append("&sdot;"); break;  //DOT OPERATOR
                    case '\u22C6': sb.append("&sstarf;"); break;  //STAR OPERATOR
                    case '\u22C7': sb.append("&divonx;"); break;  //DIVISION TIMES
                    case '\u22C8': sb.append("&bowtie;"); break;  //BOWTIE
                    case '\u22C9': sb.append("&ltimes;"); break;  //LEFT NORMAL FACTOR SEMIDIRECT PRODUCT
                    case '\u22CA': sb.append("&rtimes;"); break;  //RIGHT NORMAL FACTOR SEMIDIRECT PRODUCT
                    case '\u22CB': sb.append("&lthree;"); break;  //LEFT SEMIDIRECT PRODUCT
                    case '\u22CC': sb.append("&rthree;"); break;  //RIGHT SEMIDIRECT PRODUCT
                    case '\u22CD': sb.append("&bsime;"); break;  //REVERSED TILDE EQUALS
                    case '\u22CE': sb.append("&cuvee;"); break;  //CURLY LOGICAL OR
                    case '\u22CF': sb.append("&cuwed;"); break;  //CURLY LOGICAL AND
                    case '\u22D0': sb.append("&Sub;"); break;  //DOUBLE SUBSET
                    case '\u22D1': sb.append("&Sup;"); break;  //DOUBLE SUPERSET
                    case '\u22D2': sb.append("&Cap;"); break;  //DOUBLE INTERSECTION
                    case '\u22D3': sb.append("&Cup;"); break;  //DOUBLE UNION
                    case '\u22D4': sb.append("&fork;"); break;  //PITCHFORK
                    case '\u22D6': sb.append("&ldot;"); break;  //LESS THAN WITH DOT
                    case '\u22D7': sb.append("&gsdot;"); break;  //GREATER THAN WITH DOT
                    case '\u22D8': sb.append("&Ll;"); break;  //VERY MUCH LESS THAN
                    case '\u22D9': sb.append("&Gg;"); break;  //VERY MUCH GREATER THAN
                    case '\u22DA': sb.append("&leg;"); break;  //LESS THAN EQUAL TO OR GREATER THAN
                    case '\u22DB': sb.append("&gel;"); break;  //GREATER THAN EQUAL TO OR LESS THAN
                    case '\u22DC': sb.append("&els;"); break;  //EQUAL TO OR LESS THAN
                    case '\u22DD': sb.append("&egs;"); break;  //EQUAL TO OR GREATER THAN
                    case '\u22DE': sb.append("&cuepr;"); break;  //EQUAL TO OR PRECEDES
                    case '\u22DF': sb.append("&cuesc;"); break;  //EQUAL TO OR SUCCEEDS
                    case '\u22E0': sb.append("&npre;"); break;  //DOES NOT PRECEDE OR EQUAL
                    case '\u22E1': sb.append("&nsce;"); break;  //DOES NOT SUCCEED OR EQUAL
                    case '\u22E6': sb.append("&lnsim;"); break;  //LESS THAN BUT NOT EQUIVALENT TO
                    case '\u22E7': sb.append("&gnsim;"); break;  //GREATER THAN BUT NOT EQUIVALENT TO
                    case '\u22E8': sb.append("&prnsim;"); break;  //PRECEDES BUT NOT EQUIVALENT TO
                    case '\u22E9': sb.append("&scnsim;"); break;  //SUCCEEDS BUT NOT EQUIVALENT TO
                    case '\u22EA': sb.append("&nltri;"); break;  //NOT NORMAL SUBGROUP OF
                    case '\u22EB': sb.append("&nrtri;"); break;  //DOES NOT CONTAIN AS NORMAL SUBGROUP
                    case '\u22EC': sb.append("&nltrie;"); break;  //NOT NORMAL SUBGROUP OF OR EQUAL TO
                    case '\u22ED': sb.append("&nrtrie;"); break;  //DOES NOT CONTAIN AS NORMAL SUBGROUP OR EQUAL
                    case '\u22EE': sb.append("&vellip;"); break;  //VERTICAL ELLIPSIS
                    case '\u2306': sb.append("&Barwed;"); break;  //PERSPECTIVE
                    case '\u2308': sb.append("&lceil;"); break;  //LEFT CEILING
                    case '\u2309': sb.append("&rceil;"); break;  //RIGHT CEILING
                    case '\u230A': sb.append("&lfloor;"); break;  //LEFT FLOOR
                    case '\u230B': sb.append("&rfloor;"); break;  //RIGHT FLOOR
                    case '\u230C': sb.append("&drcrop;"); break;  //BOTTOM RIGHT CROP
                    case '\u230D': sb.append("&dlcrop;"); break;  //BOTTOM LEFT CROP
                    case '\u230E': sb.append("&urcrop;"); break;  //TOP RIGHT CROP
                    case '\u230F': sb.append("&ulcrop;"); break;  //TOP LEFT CROP
                    case '\u2315': sb.append("&telrec;"); break;  //TELEPHONE RECORDER
                    case '\u2316': sb.append("&target;"); break;  //POSITION INDICATOR
                    case '\u231C': sb.append("&ulcorn;"); break;  //TOP LEFT CORNER
                    case '\u231D': sb.append("&urcorn;"); break;  //TOP RIGHT CORNER
                    case '\u231E': sb.append("&dlcorn;"); break;  //BOTTOM LEFT CORNER
                    case '\u231F': sb.append("&drcorn;"); break;  //BOTTOM RIGHT CORNER
                    case '\u2322': sb.append("&frown;"); break;  //FROWN
                    case '\u2323': sb.append("&smile;"); break;  //SMILE
                    case '\u2329': sb.append("&lang;"); break;  //BRA
                    case '\u232A': sb.append("&rang;"); break;  //KET
                    case '\u2423': sb.append("&blank;"); break;  //OPEN BOX
                    case '\u24C8': sb.append("&oS;"); break;  //CIRCLED LATIN CAPITAL LETTER S
                    case '\u2500': sb.append("&boxh;"); break;  //FORMS LIGHT HORIZONTAL
                    case '\u2502': sb.append("&boxv;"); break;  //FORMS LIGHT VERTICAL
                    case '\u250C': sb.append("&boxdr;"); break;  //FORMS LIGHT DOWN AND RIGHT
                    case '\u2510': sb.append("&boxdl;"); break;  //FORMS LIGHT DOWN AND LEFT
                    case '\u2514': sb.append("&boxur;"); break;  //FORMS LIGHT UP AND RIGHT
                    case '\u2518': sb.append("&boxul;"); break;  //FORMS LIGHT UP AND LEFT
                    case '\u251C': sb.append("&boxvr;"); break;  //FORMS LIGHT VERTICAL AND RIGHT
                    case '\u2524': sb.append("&boxvl;"); break;  //FORMS LIGHT VERTICAL AND LEFT
                    case '\u252C': sb.append("&boxhd;"); break;  //FORMS LIGHT DOWN AND HORIZONTAL
                    case '\u2534': sb.append("&boxhu;"); break;  //FORMS LIGHT UP AND HORIZONTAL
                    case '\u253C': sb.append("&boxvh;"); break;  //FORMS LIGHT VERTICAL AND HORIZONTAL
                    case '\u2550': sb.append("&boxH;"); break;  //FORMS DOUBLE HORIZONTAL
                    case '\u2551': sb.append("&boxV;"); break;  //FORMS DOUBLE VERTICAL
                    case '\u2552': sb.append("&boxdR;"); break;  //FORMS DOWN SINGLE AND RIGHT DOUBLE
                    case '\u2553': sb.append("&boxDr;"); break;  //FORMS DOWN DOUBLE AND RIGHT SINGLE
                    case '\u2554': sb.append("&boxDR;"); break;  //FORMS DOUBLE DOWN AND RIGHT
                    case '\u2555': sb.append("&boxdL;"); break;  //FORMS DOWN SINGLE AND LEFT DOUBLE
                    case '\u2556': sb.append("&boxDl;"); break;  //FORMS DOWN DOUBLE AND LEFT SINGLE
                    case '\u2557': sb.append("&boxDL;"); break;  //FORMS DOUBLE DOWN AND LEFT
                    case '\u2558': sb.append("&boxuR;"); break;  //FORMS UP SINGLE AND RIGHT DOUBLE
                    case '\u2559': sb.append("&boxUr;"); break;  //FORMS UP DOUBLE AND RIGHT SINGLE
                    case '\u255A': sb.append("&boxUR;"); break;  //FORMS DOUBLE UP AND RIGHT
                    case '\u255B': sb.append("&boxuL;"); break;  //FORMS UP SINGLE AND LEFT DOUBLE
                    case '\u255C': sb.append("&boxUl;"); break;  //FORMS UP DOUBLE AND LEFT SINGLE
                    case '\u255D': sb.append("&boxUL;"); break;  //FORMS DOUBLE UP AND LEFT
                    case '\u255E': sb.append("&boxvR;"); break;  //FORMS VERTICAL SINGLE AND RIGHT DOUBLE
                    case '\u255F': sb.append("&boxVr;"); break;  //FORMS VERTICAL DOUBLE AND RIGHT SINGLE
                    case '\u2560': sb.append("&boxVR;"); break;  //FORMS DOUBLE VERTICAL AND RIGHT
                    case '\u2561': sb.append("&boxvL;"); break;  //FORMS VERTICAL SINGLE AND LEFT DOUBLE
                    case '\u2562': sb.append("&boxVl;"); break;  //FORMS VERTICAL DOUBLE AND LEFT SINGLE
                    case '\u2563': sb.append("&boxVL;"); break;  //FORMS DOUBLE VERTICAL AND LEFT
                    case '\u2564': sb.append("&boxHd;"); break;  //FORMS DOWN SINGLE AND HORIZONTAL DOUBLE
                    case '\u2565': sb.append("&boxhD;"); break;  //FORMS DOWN DOUBLE AND HORIZONTAL SINGLE
                    case '\u2566': sb.append("&boxHD;"); break;  //FORMS DOUBLE DOWN AND HORIZONTAL
                    case '\u2567': sb.append("&boxHu;"); break;  //FORMS UP SINGLE AND HORIZONTAL DOUBLE
                    case '\u2568': sb.append("&boxhU;"); break;  //FORMS UP DOUBLE AND HORIZONTAL SINGLE
                    case '\u2569': sb.append("&boxHU;"); break;  //FORMS DOUBLE UP AND HORIZONTAL
                    case '\u256A': sb.append("&boxvH;"); break;  //FORMS VERTICAL SINGLE AND HORIZONTAL DOUBLE
                    case '\u256B': sb.append("&boxVh;"); break;  //FORMS VERTICAL DOUBLE AND HORIZONTAL SINGLE
                    case '\u256C': sb.append("&boxVH;"); break;  //FORMS DOUBLE VERTICAL AND HORIZONTAL
                    case '\u2580': sb.append("&uhblk;"); break;  //UPPER HALF BLOCK
                    case '\u2584': sb.append("&lhblk;"); break;  //LOWER HALF BLOCK
                    case '\u2588': sb.append("&block;"); break;  //FULL BLOCK
                    case '\u2591': sb.append("&blk14;"); break;  //LIGHT SHADE
                    case '\u2592': sb.append("&blk12;"); break;  //MEDIUM SHADE
                    case '\u2593': sb.append("&blk34;"); break;  //DARK SHADE
                    case '\u25A1': sb.append("&squ;"); break;  //WHITE SQUARE
                    case '\u25AA': sb.append("&squf;"); break;  //BLACK SMALL SQUARE
                    case '\u25AD': sb.append("&rect;"); break;  //WHITE RECTANGLE
                    case '\u25AE': sb.append("&marker;"); break;  //BLACK VERTICAL RECTANGLE
                    case '\u25B3': sb.append("&xutri;"); break;  //WHITE UP POINTING TRIANGLE
                    case '\u25B4': sb.append("&utrif;"); break;  //BLACK UP POINTING SMALL TRIANGLE
                    case '\u25B5': sb.append("&utri;"); break;  //WHITE UP POINTING SMALL TRIANGLE
                    case '\u25B8': sb.append("&rtrif;"); break;  //BLACK RIGHT POINTING SMALL TRIANGLE
                    case '\u25B9': sb.append("&rtri;"); break;  //WHITE RIGHT POINTING SMALL TRIANGLE
                    case '\u25BD': sb.append("&xdtri;"); break;  //WHITE DOWN POINTING TRIANGLE
                    case '\u25BE': sb.append("&dtrif;"); break;  //BLACK DOWN POINTING SMALL TRIANGLE
                    case '\u25BF': sb.append("&dtri;"); break;  //WHITE DOWN POINTING SMALL TRIANGLE
                    case '\u25C2': sb.append("&ltrif;"); break;  //BLACK LEFT POINTING SMALL TRIANGLE
                    case '\u25C3': sb.append("&ltri;"); break;  //WHITE LEFT POINTING SMALL TRIANGLE
                    case '\u25CB': sb.append("&cir;"); break;  //WHITE CIRCLE
                    case '\u2605': sb.append("&starf;"); break;  //BLACK STAR
                    case '\u2606': sb.append("&star;"); break;  //WHITE STAR
                    case '\u260E': sb.append("&phone;"); break;  //BLACK TELEPHONE
                    case '\u2640': sb.append("&female;"); break;  //FEMALE SIGN
                    case '\u2642': sb.append("&male;"); break;  //MALE SIGN
                    case '\u2660': sb.append("&spades;"); break;  //BLACK SPADE SUIT
                    case '\u2661': sb.append("&hearts;"); break;  //WHITE HEART SUIT
                    case '\u2662': sb.append("&diams;"); break;  //WHITE DIAMOND SUIT
                    case '\u2663': sb.append("&clubs;"); break;  //BLACK CLUB SUIT
                    case '\u266A': sb.append("&sung;"); break;  //EIGHTH NOTE
                    case '\u266D': sb.append("&flat;"); break;  //FLAT
                    case '\u266E': sb.append("&natur;"); break;  //NATURAL
                    case '\u266F': sb.append("&sharp;"); break;  //SHARP
                    case '\u2713': sb.append("&check;"); break;  //CHECK MARK
                    case '\u2717': sb.append("&cross;"); break;  //BALLOT X
                    case '\u2720': sb.append("&malt;"); break;  //MALTESE CROSS
                    case '\u2726': sb.append("&lozf;"); break;  //BLACK FOUR POINTED STAR
                    case '\u2727': sb.append("&loz;"); break;  //WHITE FOUR POINTED STAR
                    case '\u2736': sb.append("&sextile;"); break;  //SIX POINTED BLACK STAR
                    default:
                        sb.append(' ');

                }
        }
        xml=sb.toString();

        //xml = perl.substitute("s/<b562>/<b562><![CDATA[/ig", xml);
        //xml = perl.substitute("s/<\\/b562>/]]><\\/b562>/ig", xml);



        return xml;

    }
    public void close() {

        System.out.println("Close..............");

        if (patentsOut != null)
            patentsOut.close();

        if (nonPatentsOut != null)
            nonPatentsOut.close();

        if (readMap != null) {
            try {
                //readMap.close();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {

        String URL = args[0];
        String User = args[1];
        String Password = args[2];
        String loadNumber = args[3];
        String rootDir = args[4];
        String patentOut = args[5];
        String nonPatentOut = args[6];
        String luceneDir = args[7];
        if (args.length==9)
        {
            weekNum = args[8];
        }
        System.out.println("Load Number:"+loadNumber);
        System.out.println("Input File:"+rootDir);
        System.out.println("Output Citation:"+ patentOut);
        System.out.println("Output Non Patent Cit:"+ nonPatentOut);
        System.out.println("Lucene Dir:"+luceneDir);
        try {

            dbMap = new OracleMap(URL,User,Password);
            dbMap.open();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        CitationXmlParser parser = new CitationXmlParser(loadNumber, patentOut, nonPatentOut, luceneDir);
        try {

            System.out.println("Begin Parsing...");
            StringBuffer xml = new StringBuffer("<patdoc>");
            parser.parseXml(loadNumber, new File(rootDir), patentOut, nonPatentOut, xml, luceneDir);

            System.out.println("End Parsing");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            parser.close();
            try {
                dbMap.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}
