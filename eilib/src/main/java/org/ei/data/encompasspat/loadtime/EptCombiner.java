package org.ei.data.encompasspat.loadtime;

import java.io.ByteArrayInputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.data.*;
import org.ei.util.StringUtil;

public class EptCombiner extends Combiner {

    Perl5Util perl = new Perl5Util();

    public EptCombiner(CombinedWriter writer) {
        super(writer);
    }

    public void writeCombinedByYearHook(Connection con, int year) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {

            stmt = con.createStatement();

            if (year == 9999) {
                rs = stmt
                    .executeQuery("select dn,m_id,pat_in,ti,aj,ap,ad,ac,pn, substr(load_number,1,4) as py,pc,ic,cs,cc,la,lt,ab,ct,cr,ut,ey,crn,load_number,ll,dt,ds,seq_num from ept_master where py = '19' or py is null and seq_num is not null");
            } else {
                rs = stmt
                    .executeQuery("select dn,m_id,pat_in,ti,aj,ap,ad,ac,pn,py,pc,ic,cs,cc,la,lt,ab,ct,cr,ut,ey,crn,load_number,ll,dt,ds,seq_num from ept_master where py = '"
                        + year + "' and seq_num is not null");
            }
            writeRecs(rs);

            this.writer.end();
            this.writer.flush();

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void writeCombinedByWeekHook(Connection con, int week) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;

        try {

            stmt = con.createStatement();
            rs = stmt
                .executeQuery("select dn,m_id,pat_in,ti,aj,ap,ad,ac,pn,py,pc,ic,cs,cc,la,lt,ab,ct,cr,ut,ey,crn,load_number,ll,dt,ds,seq_num from ept_master where load_number = "
                    + week + " and seq_num is not null");
            writeRecs(rs);
            this.writer.end();
            this.writer.flush();

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getStringFromClob(Clob clob) throws Exception {
        String temp = null;
        if (clob != null) {
            temp = clob.getSubString(1, (int) clob.length());
        }

        return temp;
    }

    public void writeRecs(ResultSet rs) throws Exception {

        int i = 0;
        CVSTermBuilder termBuilder = new CVSTermBuilder();

        while (rs.next()) {
            ++i;
            QualifierFacet qfacet = new QualifierFacet();
            EVCombinedRec rec = new EVCombinedRec();
            if (validYear(rs.getString("py"))) {

                String abs = replaceNull(getStringFromClob(rs.getClob("ab")));
                String lt = replaceNull(getStringFromClob(rs.getClob("lt")));

                String accessionNumber = rs.getString("dn");

                rec.put(EVCombinedRec.DOCID, rs.getString("m_id"));
                rec.put(EVCombinedRec.PARENT_ID, rs.getString("seq_num"));
                rec.put(EVCombinedRec.DEDUPKEY, accessionNumber);
                rec.put(EVCombinedRec.ACCESSION_NUMBER, accessionNumber);
                rec.put(EVCombinedRec.AUTHOR, prepareMulti(StringUtil.replaceNonAscii(replaceNull(rs.getString("pat_in")))));
                rec.put(EVCombinedRec.DATABASE, "ept");
                rec.put(EVCombinedRec.TITLE, StringUtil.replaceNonAscii(replaceNull(rs.getString("ti"))));

                String derwentAccession = rs.getString("aj");

                rec.put(EVCombinedRec.DERWENT_ACCESSION_NUMBER, derwentAccession);
                String ll = replaceNull(rs.getString("ll"));

                rec.put(EVCombinedRec.APPLICATION_NUMBER, prepareMulti(StringUtil.replaceNonAscii(getApplicationNumber(replaceNull(ll)))));
                rec.put(EVCombinedRec.APPLICATION_COUNTRY, prepareMulti(StringUtil.replaceNonAscii(getApplicationCountry(replaceNull(ll)))));
                rec.put(EVCombinedRec.PATENTAPPDATE, prepareMulti(StringUtil.replaceNonAscii(getApplicationDate(replaceNull(ll)))));

                rec.put(EVCombinedRec.PATENT_NUMBER, formatPn(rs.getString("pn"), rs.getString("pc")));
                rec.put(EVCombinedRec.PUB_YEAR, rs.getString("py"));
                rec.put(EVCombinedRec.AUTHORITY_CODE, rs.getString("pc"));

                // this field is used to generate ipc navigator for EPT database when ept is only one db in application
                // when it is combined with some other db - use navigator - rec.INT_PATENT_CLASSIFICATION
                rec.put(EVCombinedRec.PATENT_KIND, prepareMulti(termBuilder.removeBar(replaceNull(rs.getString("ic")))));

                rec.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, prepareMulti(termBuilder.removeBar(replaceNull(rs.getString("ic"))), Constants.IPC));

                rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareMulti(StringUtil.replaceNonAscii(replaceNull(rs.getString("cs")))));
                rec.put(EVCombinedRec.CLASSIFICATION_CODE, prepareMulti(XMLWriterCommon.formatClassCodes(rs.getString("cc"))));
                rec.put(EVCombinedRec.LANGUAGE, prepareMulti(rs.getString("la"), Constants.LA));
                lt = StringUtil.replaceNonAscii(replaceNull(lt));

                rec.put(EVCombinedRec.LINKED_TERMS, prepareMultiLinkedTerm(CVSTermBuilder.formatCT(lt)));
                rec.put(EVCombinedRec.ABSTRACT, StringUtil.replaceNonAscii(replaceNull(abs)));

                String ct = replaceNull(rs.getString("ct"));

                String cv = termBuilder.getNonMajorTerms(ct);
                String mh = termBuilder.getMajorTerms(ct);
                StringBuffer cvsBuffer = new StringBuffer();

                String expandedMajorTerms = termBuilder.expandMajorTerms(mh);
                String expandedMH = termBuilder.getMajorTerms(expandedMajorTerms);
                String expandedCV1 = termBuilder.expandNonMajorTerms(cv);
                String expandedCV2 = termBuilder.getNonMajorTerms(expandedMajorTerms);

                if (!expandedCV2.equals(""))
                    cvsBuffer.append(expandedCV1).append(";").append(expandedCV2);
                else
                    cvsBuffer.append(expandedCV1);

                String parsedCV = CVSTermBuilder.formatCT(cvsBuffer.toString());

                rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(termBuilder.getStandardTerms(parsedCV), Constants.CVS));

                String parsedMH = CVSTermBuilder.formatCT(expandedMH);

                rec.put(EVCombinedRec.MAIN_HEADING, prepareMulti(StringUtil.replaceNonAscii(termBuilder.removeRoleTerms(parsedMH)), Constants.CVS));
                // this field is added to generate navigators for Major terms
                rec.put(EVCombinedRec.ECLA_CODES, prepareMulti(StringUtil.replaceNonAscii(termBuilder.removeRoleTerms(parsedMH)), Constants.CVS));

                String norole = StringUtil.replaceNonAscii(replaceNull(termBuilder.getNoRoleTerms(parsedCV)));

                qfacet.setNorole(norole);
                rec.put(EVCombinedRec.NOROLE_TERMS, prepareMulti(norole));

                String reagent = StringUtil.replaceNonAscii(replaceNull(termBuilder.getReagentTerms(parsedCV)));

                qfacet.setReagent(reagent);
                rec.put(EVCombinedRec.REAGENT_TERMS, prepareMulti(reagent));

                String product = StringUtil.replaceNonAscii(replaceNull(termBuilder.getProductTerms(parsedCV)));
                qfacet.setProduct(product);
                rec.put(EVCombinedRec.PRODUCT_TERMS, prepareMulti(product));

                String mnorole = termBuilder.getMajorNoRoleTerms(parsedMH);
                qfacet.setNorole(mnorole);
                rec.put(EVCombinedRec.MAJORNOROLE_TERMS, prepareMulti(mnorole));

                String mreagent = termBuilder.getMajorReagentTerms(parsedMH);
                qfacet.setReagent(mreagent);
                rec.put(EVCombinedRec.MAJORREAGENT_TERMS, prepareMulti(mreagent));

                String mproduct = termBuilder.getMajorProductTerms(parsedMH);
                qfacet.setProduct(mproduct);
                rec.put(EVCombinedRec.MAJORPRODUCT_TERMS, prepareMulti(mproduct));

                // rec.put(rec.UNCONTROLLED_TERMS, prepareMulti(qfacet.getValue()));
                // 11/29/07 TS by new specs q facet mapped to uspto code navigator field
                rec.put(EVCombinedRec.USPTOCODE, prepareMulti(qfacet.getValue()));

                // added Free language field
                rec.put(EVCombinedRec.UNCONTROLLED_TERMS, prepareMulti(CVSTermBuilder.formatCT(StringUtil.replaceNonAscii(replaceNull(rs.getString("ut"))))));
                rec.put(EVCombinedRec.CASREGISTRYNUMBER, prepareMulti(rs.getString("crn")));

                rec.put(EVCombinedRec.ENTRY_YEAR, rs.getString("ey"));

                rec.put(EVCombinedRec.PRIORITY_NUMBER, prepareMulti(StringUtil.replaceNonAscii(rs.getString("ap"))));
                rec.put(EVCombinedRec.PRIORITY_COUNTRY, prepareMulti(StringUtil.replaceNonAscii(rs.getString("ac"))));
                rec.put(EVCombinedRec.PRIORITY_DATE, prepareMulti(StringUtil.replaceNonAscii(rs.getString("ad"))));

                rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("load_number"));
                // ad us_patents and us_apps to doctypes - only for us - make it multyfields
                // and remove it if not usp combines display

                rec.put(EVCombinedRec.DOCTYPE, prepareMulti(formatDt(rs.getString("dt"), rs.getString("pc"), rs.getString("pn"))));

                rec.put(EVCombinedRec.DESIGNATED_STATES, prepareMulti(rs.getString("ds")));

                this.writer.writeRec(rec);
            }

        }
    }

    // this method is fixing US application numbers
    private String formatPn(String pn, String pc) {
        if (pc != null && pn != null && pc.equalsIgnoreCase(Constants.US.getValue()) && pn.trim().length() == 10) {
            StringBuffer uspn = new StringBuffer(pn.substring(0, 4));
            uspn.append(Constants.PN_FIX.getValue()).append(pn.substring(4));
            return uspn.toString();
        } else {
            return pn;
        }

    }

    private boolean validYear(String year) {
        if (year == null) {
            return false;
        }

        if (year.length() != 4) {
            return false;
        }

        return perl.match("/[1-9][0-9][0-9][0-9]/", year);
    }

    public String getApplicationNumber(String ll) {

        StringBuffer bufAppNumbers = new StringBuffer();

        if (ll != null && !ll.equals("")) {

            String sVal = perl.substitute("s/\\s+/ /g", ll);

            MatchResult mRes = null;

            StringTokenizer st = new StringTokenizer(sVal, ";", false);

            bufAppNumbers = new StringBuffer();

            while (st.hasMoreTokens()) {
                String prc = st.nextToken().trim();

                if (prc != null) {
                    List<String> lstTokens = new ArrayList<String>();

                    perl.split(lstTokens, "/\\s/", prc);

                    String token = "";

                    if (lstTokens.size() > 1) {

                        token = (String) lstTokens.get(1);

                        bufAppNumbers.append(token);
                    }

                    if (st.hasMoreTokens())
                        bufAppNumbers.append(";");

                    lstTokens.clear();
                }

            }
        }
        return bufAppNumbers.toString();
    }

    public String getApplicationCountry(String ll) {

        StringBuffer buffCountries = new StringBuffer();

        if (ll != null && !ll.equals("")) {

            String sVal = perl.substitute("s/\\s+/ /g", ll);

            MatchResult mRes = null;
            String token = "";

            StringTokenizer st = new StringTokenizer(sVal, ";");
            String prc = "";
            buffCountries = new StringBuffer();

            while (st.hasMoreTokens()) {
                prc = st.nextToken().trim();

                if (prc != null) {
                    List<String> lstTokens = new ArrayList<String>();

                    perl.split(lstTokens, "/\\s/", prc);

                    if (lstTokens.size() > 0) {
                        token = (String) lstTokens.get(0);
                        buffCountries.append(token);
                    }

                    if (st.hasMoreTokens())
                        buffCountries.append(";");

                    lstTokens.clear();
                }

            }
        }
        return buffCountries.toString();
    }

    public String getApplicationDate(String ll) {

        StringBuffer bufDates = new StringBuffer();

        if (ll != null && !ll.equals("")) {

            String sVal = perl.substitute("s/\\s+/ /g", ll);

            MatchResult mRes = null;
            String token = "";

            StringTokenizer st = new StringTokenizer(sVal, ";");
            String prc = "";
            bufDates = new StringBuffer();

            while (st.hasMoreTokens()) {
                prc = st.nextToken().trim();

                if (prc != null) {
                    List<String> lstTokens = new ArrayList<String>();

                    perl.split(lstTokens, "/\\s/", prc);

                    if (lstTokens.size() > 2) {
                        token = (String) lstTokens.get(2);

                        bufDates.append(token);
                    }

                    if (st.hasMoreTokens())
                        bufDates.append(";");

                    lstTokens.clear();
                }
            }
        }
        return bufDates.toString();
    }

    public String replaceNull(String sVal) {

        if (sVal == null)
            sVal = "";

        return sVal;
    }

    /*
     * stripAsterics method is used to remove asterisk from fields cv and mh
     */

    private String stripAsterics(String line) {
        line = perl.substitute("s/\\*+//gi", line);
        return line;
    }

    public String[] prepareMulti(String multiString) throws Exception {
        return prepareMulti(multiString, null);
    }

    public String[] prepareMulti(String multiString, Constants constant) throws Exception {
        if (multiString != null) {

            AuthorStream astream = new AuthorStream(new ByteArrayInputStream(multiString.getBytes()));
            String s = null;
            ArrayList<String> list = new ArrayList<String>();
            while ((s = astream.readAuthor()) != null) {
                s = s.trim();
                if (constant == null) {
                    list.add(s);
                } else if (constant.equals(Constants.CVS)) {
                    list.add(stripAsterics(s));
                } else if (constant.equals(Constants.IPC)) {
                    list.add(removeSpaces(formatIpc(s)));
                } else if (constant.equals(Constants.LA)) {
                    list.add(convertLanguages(s));
                }
            }
            return (String[]) list.toArray(new String[1]);
        } else {
            String[] str = new String[] { "" };
            return str;

        }
    }

    // this method is generating doc types for us patents ("ug") and us applications ("ua")

    private String formatDt(String dt, String pc, String pn) {
        StringBuffer dtbuf = new StringBuffer(dt);
        if (pc != null && pn != null && dt != null && pc.equals(Constants.US.getValue())) {
            if (pn.length() == 10) {
                dtbuf.append(Constants.MULTIFIELD_DELIM.getValue()).append(Constants.DT_UA.getValue());
            } else {
                dtbuf.append(Constants.MULTIFIELD_DELIM.getValue()).append(Constants.DT_UG.getValue());
            }
            return dtbuf.toString().toLowerCase();
        } else {
            return dt.toLowerCase();
        }
    }

    private String formatIpc(String line) {
        StringTokenizer toc = new StringTokenizer(line, "-");
        StringBuffer ipc = new StringBuffer(toc.nextToken());

        if (toc.hasMoreTokens()) {
            String toc2 = toc.nextToken();
            if (toc2.indexOf("/") > -1) {
                toc2 = perl.substitute("s/^[\\-+0]*//gi", toc2);
            }
            ipc.append(toc2);
        }

        return ipc.toString();
    }

    private String[] prepareMultiLinkedTerm(String multiString) throws Exception {
        if (multiString != null) {

            AuthorStream astream = new AuthorStream(new ByteArrayInputStream(multiString.getBytes()));
            String s = null;
            ArrayList<String> list = new ArrayList<String>();
            while ((s = astream.readAuthor()) != null) {
                s = s.trim();

                if (perl.match("/\\|/", s)) {
                    StringBuffer buf = new StringBuffer();
                    ArrayList<String> strings = new ArrayList<String>();
                    perl.split(strings, "/\\|/", s);

                    for (int i = 0; i < strings.size(); ++i) {
                        String s1 = (String) strings.get(i);
                        s1 = s1.trim();
                        if (s1.length() > 0)
                            list.add(s1);
                    }
                } else {
                    if (s.length() > 0) {
                        list.add(s);
                    }
                }
            }
            return (String[]) list.toArray(new String[1]);
        } else {
            String[] str = new String[] { "" };
            return str;
        }
    }

    public static void main(String args[]) throws Exception {

        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
        int loadNumber = Integer.parseInt(args[4]);
        int recsPerbatch = Integer.parseInt(args[5]);
        String operation = args[6];
        Combiner.TABLENAME = args[7];
        String environment = args[8].toLowerCase();
        System.out.println("Table Name=" + args[7]);
        System.out.println("Table Name=" + args[7]);
        System.out.println("LoadNumber=" + loadNumber);
        System.out.println("RecsPerBatch=" + recsPerbatch);

        CombinedWriter writer = new CombinedXMLWriter(recsPerbatch, loadNumber, "ept");
        writer.setOperation(operation);
        EptCombiner c = new EptCombiner(writer);

        // extract the whole thing
        if (loadNumber == 0) {
            for (int yearIndex = 1919; yearIndex <= 2012; yearIndex++) {
                System.out.println("Processing year " + yearIndex + "...");
                c = new EptCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex, "ept", environment));
                c.writeCombinedByYear(url, driver, username, password, yearIndex);
            }
            int yearIndex = 9999;
            System.out.println("Processing year " + yearIndex + "...");
            c = new EptCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex, "ept", environment));
            c.writeCombinedByYear(url, driver, username, password, yearIndex);
        } else if (loadNumber > 3000 || loadNumber < 1000) {
            c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
        } else {

            c.writeCombinedByYear(url, driver, username, password, loadNumber);
        }

    }

    // 11/29/07 TS by new specs this method is taken from UPO combiner to sync format
    // modification of method signature - param and return value from String[] modifyed to String
    public String removeSpaces(String code) {
        code = perl.substitute("s/\\s+//", code);

        if (perl.match("/\\/\\//", code)) {
            code = perl.substitute("s/\\/\\//\\//", code);
        }

        if (perl.match("/\\./", code)) {
            code = perl.substitute("s/\\./PERIOD/ig", code);
        }

        if (perl.match("/\\//", code)) {
            code = perl.substitute("s/\\//SLASH/ig", code);
        }

        return code;
    }

    // 11/29/07 TS by new specs EnCompassPAT production problem fix
    public String convertLanguages(String lang) {
        if (lang == null) {
            return null;
        }
        lang = lang.trim().toUpperCase();
        if (lang.length() == 1) {
            if (lang.equals("E")) {
                lang = perl.substitute("s/E/English/gi", lang);
            } else if (lang.equals("J")) {
                lang = perl.substitute("s/J/Japanese/gi", lang);
            } else if (lang.equals("G")) {
                lang = perl.substitute("s/G/German/gi", lang);
            } else if (lang.equals("F")) {
                lang = perl.substitute("s/F/French/gi", lang);
            } else if (lang.equals("R")) {
                lang = perl.substitute("s/R/Russian/gi", lang);
            } else if (lang.equals("S")) {
                lang = perl.substitute("s/S/Spanish/gi", lang);
            } else if (lang.equals("C")) {
                lang = perl.substitute("s/C/Chinese/gi", lang);
            }
        }

        return lang;

    }
}