package org.ei.dataloading.encompasslit.loadtime;

import java.io.ByteArrayInputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.common.AuthorStream;
import org.ei.common.CVSTermBuilder;
import org.ei.common.Constants;
import org.ei.common.CountryFormatter;
import org.ei.common.EltAusFormatter;
import org.ei.common.encompasslit.EltDocTypes;

/*
import org.ei.dataloading.QualifierFacet;
import org.ei.dataloading.EVCombinedRec;
import org.ei.dataloading.CombinedWriter;
import org.ei.datloadinga.CombinedXMLWriter;
import org.ei.dataloading.Combiner;
import org.ei.dataloading.XMLWriterCommon;
*/

import org.ei.dataloading.*;
import org.ei.util.GUID;
import org.ei.util.StringUtil;

/*
 * Created on Apr 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class EltCombiner extends Combiner {

    private EltDocTypes eltDocTypes = new EltDocTypes();
    Perl5Util perl = new Perl5Util();

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
        System.out.println("Table Name=" + Combiner.TABLENAME);
        System.out.println("LoadNumber=" + loadNumber);
        System.out.println("RecsPerFile=" + recsPerbatch);
        System.out.println(Combiner.TABLENAME);

        CombinedWriter writer = new CombinedXMLWriter(recsPerbatch, loadNumber, "elt");
        writer.setOperation(operation);
        EltCombiner c = new EltCombiner(writer);

        System.out.println("write year");
        if (loadNumber > 3000 || (loadNumber < 1000 && loadNumber > 0)) {
            c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
        }
        // extract the whole thing
        else if (loadNumber == 0) {
            for (int yearIndex = 1955; yearIndex <= 2012; yearIndex++) {
                System.out.println("Processing year " + yearIndex + "...");
                // create a new writer so we can see the loadNumber/yearNumber in the filename
                c = new EltCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex, "elt", environment));
                c.writeCombinedByYear(url, driver, username, password, yearIndex);
            }
        } else {
            c.writeCombinedByYear(url, driver, username, password, loadNumber);
        }

        System.out.println("end of loadnumber " + loadNumber);
    }

    public EltCombiner(CombinedWriter writer) {
        super(writer);
    }

    public void writeCombinedByYearHook(Connection con, int year) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;
        try {

            stmt = con.createStatement();
            System.out.println("Running the query...");

            rs = stmt
                .executeQuery("select M_ID,VLN,ISN, PAG, substr(pyr,1,4) as pyr , STI,CIP , AUT , TIE , TIF,MOT, SPD, SPT, AAF, LNA, ABS, STY, APICT, APICT1, APILT ,APILT1 , RNR, ISSN, PUB, ISBN,CNFNAM, CNFSTD, CNFEND, CNFVEN, CNFCTY, CNFCNY, CNFEDN, CNFEDO, CNFEDA, CNFCAT, CNFCOD, CNFPAG, CNFPCT, CNFPRT, CNFSPO , APIUT ,APICC ,APILTM,APIALC, APIATM, APICRN, APIAMS, APIAPC, APIANC, APIAT, APICT, APILT , SUBSTR(pyr,1,4) yr, load_number,so,secsti,oab,apiatm,apiltm,apiams,cna,sta,seciss,cnfstd,cnfend,cnfven,cnfcty,cnfcny,cnfpag,cnfpct,cnfprt,cf,sec,dt,cprs, DOI, seq_num from elt_master where seq_num is not null and substr(pyr,1,4) ='"
                    + year + "'");
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
        }
    }

    private void writeRecs(ResultSet rs) throws Exception {
        CVSTermBuilder termBuilder = new CVSTermBuilder();

        while (rs.next()) {

            EVCombinedRec rec = new EVCombinedRec();
            QualifierFacet qfacet = new QualifierFacet();
            String abString = getStringFromClob(rs.getClob("abs"));

            if (validYear(rs.getString("pyr"))) {

                if (rs.getString("m_id") != null) {
                    rec.put(EVCombinedRec.DOCID, rs.getString("m_id"));
                }

                if (rs.getString("cip") != null) {
                    rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("cip"));
                }

                String apict = StringUtil.replaceNonAscii(replaceNull(rs.getString("apict")));
                String apict1 = StringUtil.replaceNonAscii(replaceNull(rs.getString("apict1")));

                StringBuffer cvs = new StringBuffer();

                if (!apict1.equals(""))
                    cvs.append(apict).append(";").append(apict1);
                else
                    cvs.append(apict);

                String cv = termBuilder.getNonMajorTerms(cvs.toString());
                String mh = termBuilder.getMajorTerms(cvs.toString());
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

                String norole = termBuilder.getNoRoleTerms(parsedCV);
                qfacet.setNorole(norole);
                rec.put(EVCombinedRec.NOROLE_TERMS, prepareMulti(norole));

                String reagent = termBuilder.getReagentTerms(parsedCV);
                qfacet.setReagent(reagent);
                rec.put(EVCombinedRec.REAGENT_TERMS, prepareMulti(reagent));

                String product = termBuilder.getProductTerms(parsedCV);
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
                if (rs.getString("apiut") != null) {
                    rec.put(EVCombinedRec.UNCONTROLLED_TERMS,
                        prepareMulti(CVSTermBuilder.formatCT(StringUtil.replaceNonAscii(replaceNull(rs.getString("apiut"))))));
                }

                if (rs.getString("tie") != null) {
                    rec.put(EVCombinedRec.TITLE, StringUtil.replaceNonAscii(replaceNull(rs.getString("tie"))));
                }

                if (rs.getString("tif") != null) {
                    rec.put(EVCombinedRec.TRANSLATED_TITLE, StringUtil.replaceNonAscii(replaceNull(rs.getString("tif"))));
                }

                if (rs.getString("mot") != null) {
                    rec.put(EVCombinedRec.MONOGRAPH_TITLE, rs.getString("mot"));
                }

                if (rs.getString("aut") != null) {

                    StringBuffer authors = new StringBuffer();

                    EltAusFormatter ausFormatter = new EltAusFormatter();

                    int loadNumber = rs.getInt("LOAD_NUMBER");

                    if (loadNumber != 0 && loadNumber >= 200000) {
                        authors.append(ausFormatter.formatAuthors(replaceNull(rs.getString("aut"))));

                    } else {
                        authors.append(replaceNull(rs.getString("aut")));
                    }

                    rec.put(EVCombinedRec.AUTHOR, prepareAuthor(stripDelim(StringUtil.replaceNonAscii(authors.toString()))));
                }

                if (rs.getString("aaf") != null) {

                    rec.put(
                        EVCombinedRec.AUTHOR_AFFILIATION,
                        prepareMulti(StringUtil.replaceNonAscii(EltAusFormatter.formatAffiliation(stripDelim(replaceNull(rs.getString("aaf")), ";"))),
                            Constants.AFF));
                }

                if (rs.getString("apicc") != null) {
                    rec.put(EVCombinedRec.CLASSIFICATION_CODE,
                        prepareMulti(StringUtil.replaceNonAscii(replaceNull(XMLWriterCommon.formatClassCodes(rs.getString("apicc"))))));
                }
                StringBuffer issn = new StringBuffer();

                if (rs.getString("issn") != null) {
                    issn.append(rs.getString("issn")).append(";");
                }

                if (rs.getString("seciss") != null) {
                    issn.append(rs.getString("seciss"));
                }

                if ((issn != null) && (issn.length() > 0)) {
                    rec.put(EVCombinedRec.ISSN, prepareMulti(issn.toString()));

                }

                if (rs.getString("dt") != null) {
                    rec.put(EVCombinedRec.DOCTYPE, prepareMulti(rs.getString("dt"), Constants.DT));
                }
                if (rs.getString("pub") != null) {
                    rec.put(EVCombinedRec.PUBLISHER_NAME, StringUtil.replaceNonAscii(replaceNull(rs.getString("pub"))));
                }
                if (rs.getString("isbn") != null) {
                    rec.put(EVCombinedRec.ISBN, rs.getString("isbn"));
                }
                if (rs.getString("pyr") != null) {
                    rec.put(EVCombinedRec.PUB_YEAR, rs.getString("pyr"));
                }

                if (rs.getString("lna") != null) {
                    rec.put(EVCombinedRec.LANGUAGE, prepareMulti(StringUtil.replaceNonAscii(replaceNull(rs.getString("lna")))));
                }

                // 11/29/07 TS by new specs "oab" is appended to "ab" to make searchable
                String oabsract = StringUtil.replaceNonAscii(replaceNull(rs.getString("oab")));
                if (abString != null) {
                    if (oabsract != null) {
                        abString = abString.concat(" ").concat(oabsract);
                    }

                    rec.put(EVCombinedRec.ABSTRACT, StringUtil.replaceNonAscii(replaceNull(abString)));
                } else if (oabsract != null) {
                    rec.put(EVCombinedRec.ABSTRACT, StringUtil.replaceNonAscii(replaceNull(oabsract)));
                }

                if (rs.getString("cnfnam") != null) {
                    rec.put(EVCombinedRec.CONFERENCE_NAME, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfnam"))));
                }

                if (rs.getString("cnfcty") != null) {
                    rec.put(EVCombinedRec.CONFERENCE_LOCATION, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfcty"))));
                }

                if (rs.getString("cnfcod") != null) {
                    rec.put(EVCombinedRec.CONFERENCE_CODE, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfcod"))));
                }
                if (rs.getString("cnfedo") != null) {

                    rec.put(EVCombinedRec.CONFERENCEAFFILIATIONS, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfedo"))));
                }
                if (rs.getString("cnfedn") != null) {

                    rec.put(EVCombinedRec.CONFERENCEEDITORS, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfedn"))));
                }

                if (rs.getString("cnfstd") != null) {

                    rec.put(EVCombinedRec.CONFERENCESTARTDATE, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfstd"))));
                }
                if (rs.getString("cnfend") != null) {

                    rec.put(EVCombinedRec.CONFERENCEENDDATE, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfend"))));
                }

                if (rs.getString("cnfven") != null) {

                    rec.put(EVCombinedRec.CONFERENCEVENUESITE, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfven"))));
                }
                if (rs.getString("cnfcty") != null) {

                    rec.put(EVCombinedRec.CONFERENCECITY, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfcty"))));
                }

                if (rs.getString("cnfcny") != null) {

                    rec.put(EVCombinedRec.CONFERENCECOUNTRYCODE, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfcny"))));
                }
                if (rs.getString("cnfpag") != null) {

                    rec.put(EVCombinedRec.CONFERENCEPAGERANGE, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfpag"))));
                }

                if (rs.getString("cnfpct") != null) {

                    rec.put(EVCombinedRec.CONFERENCENUMBERPAGES, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfpct"))));
                }

                if (rs.getString("cnfprt") != null) {

                    rec.put(EVCombinedRec.CONFERENCEPARTNUMBER, StringUtil.replaceNonAscii(replaceNull(rs.getString("cnfprt"))));
                }

                if (rs.getString("cf") != null) {

                    rec.put(EVCombinedRec.STN_CONFERENCE, StringUtil.replaceNonAscii(replaceNull(rs.getString("cf"))));
                }

                if (rs.getString("sec") != null) {

                    rec.put(EVCombinedRec.STN_SECONDARY_CONFERENCE, StringUtil.replaceNonAscii(replaceNull(rs.getString("sec"))));
                }

                if (rs.getString("sti") != null) {
                    rec.put(EVCombinedRec.SERIAL_TITLE, StringUtil.replaceNonAscii(replaceNull(rs.getString("sti"))));
                }

                String apilt = StringUtil.replaceNonAscii(replaceNull(getStringFromClob(rs.getClob("apilt"))));

                rec.put(EVCombinedRec.LINKED_TERMS, prepareMultiLinkedTerm(CVSTermBuilder.formatCT(apilt)));

                if (rs.getString("apicrn") != null) {
                    rec.put(EVCombinedRec.CASREGISTRYNUMBER, prepareMulti(rs.getString("apicrn")));
                }

                rec.put(EVCombinedRec.DEDUPKEY, getDedupKey(rec.get(EVCombinedRec.ISSN), null, rs.getString("vln"), rs.getString("isn"), rs.getString("pag")));

                rec.put(EVCombinedRec.VOLUME, getFirstNumber(rs.getString("vln")));
                rec.put(EVCombinedRec.ISSUE, getFirstNumber(rs.getString("isn")));
                rec.put(EVCombinedRec.STARTPAGE, getFirstNumber(rs.getString("pag")));

                rec.put(EVCombinedRec.DATABASE, "elt");
                rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("load_number"));
                rec.put(EVCombinedRec.SOURCE, StringUtil.replaceNonAscii(replaceNull(rs.getString("so"))));
                rec.put(EVCombinedRec.SECONDARY_SRC_TITLE, StringUtil.replaceNonAscii(replaceNull(rs.getString("secsti"))));
                // 11/29/07 TS by new specs "oab" is appended to "ab" to make searchable
                // rec.put(EVCombinedRec.OTHER_ABSTRACT, StringUtil.replaceNonAscii(replaceNull(rs.getString("oab"))));
                rec.put(EVCombinedRec.MAIN_TERM, prepareMulti(StringUtil.replaceNonAscii(replaceNull(rs.getString("apiams"))), Constants.CVS));
                // rec.put(EVCombinedRec.EDITOR_AFFILIATION, StringUtil.replaceNonAscii(replaceNull(rs.getString("cna"))));
                rec.put(EVCombinedRec.ABBRV_SRC_TITLE, StringUtil.replaceNonAscii(replaceNull(rs.getString("sta"))));
                rec.put(EVCombinedRec.COUNTRY, prepareMulti(stripDelim(replaceNull(rs.getString("cna")), ";"), Constants.CO));
                if (rs.getString("doi") != null) {
                    rec.put(EVCombinedRec.DOI, rs.getString("doi"));
                }
                if (rs.getString("seq_num") != null) {
                    rec.put(EVCombinedRec.PARENT_ID, rs.getString("seq_num"));
                }
                this.writer.writeRec(rec);
            }
        }
    }

    public String replaceNull(String sVal) {

        if (sVal == null)
            sVal = "";

        return sVal;
    }

    private String getFirstNumber(String v) {

        MatchResult mResult = null;
        if (v == null) {
            return null;
        }

        if (perl.match("/[1-9][0-9]*/", v)) {
            mResult = perl.getMatch();
        } else {
            return null;
        }

        return mResult.toString();
    }

    private String getFirstPage(String v) {

        MatchResult mResult = null;
        if (v == null) {
            return null;
        }

        if (perl.match("/[A-Z]?[0-9][0-9]*/", v)) {
            mResult = perl.getMatch();
        } else {
            return null;
        }

        return mResult.toString();
    }

    private String getDedupKey(String issn, String coden, String volume, String issue, String page) throws Exception {

        String firstVolume = getFirstNumber(volume);
        String firstIssue = getFirstNumber(issue);
        String firstPage = getFirstPage(page);

        if ((issn == null && coden == null) || firstVolume == null || firstIssue == null || firstPage == null) {
            return (new GUID()).toString();
        }

        StringBuffer buf = new StringBuffer();

        if (issn != null) {
            buf.append(perl.substitute("s/-//g", issn));
        } else {
            buf.append(coden);
        }

        buf.append("vol" + firstVolume);
        buf.append("is" + firstIssue);
        buf.append("pa" + firstPage);

        return buf.toString().toLowerCase();

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

    private String getStringFromClob(Clob clob) throws Exception {
        String temp = null;
        if (clob != null) {
            temp = clob.getSubString(1, (int) clob.length());
        }

        return temp;
    }

    public void writeCombinedByWeekHook(Connection con, int weekNumber) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {

            stmt = con.createStatement();
            System.out.println("Getting weeks records ...");
            rs = stmt
                .executeQuery("select M_ID,VLN,ISN, PAG, substr(PYR,1,4) as PYR , STI,CIP , AUT , TIE , TIF,MOT, SPD, SPT, AAF, LNA, ABS, STY, APICT, APICT1, APILT ,APILT1 , RNR, ISSN, PUB, ISBN,CNFNAM, CNFSTD, CNFEND, CNFVEN, CNFCTY, CNFCNY, CNFEDN, CNFEDO, CNFEDA, CNFCAT, CNFCOD, CNFPAG, CNFPCT, CNFPRT, CNFSPO , APIUT ,APICC ,APILTM,APIALC, APIATM, APICRN, APIAMS, APIAPC, APIANC, APIAT, APICT, APILT , SUBSTR(pyr,1,4) yr, load_number,so,secsti,oab,apiatm,apiltm,apiams,cna,sta,seciss,cnfstd,cnfend,cnfven,cnfcty,cnfcny,cnfpag,cnfpct,cnfprt,cf,sec,dt,cprs, DOI,seq_num from elt_master where seq_num is not null and load_number="
                    + weekNumber);

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
        }

    }

    private String[] prepareAuthor(String aString) throws Exception {

        AuthorStream astream = new AuthorStream(new ByteArrayInputStream(aString.getBytes()));
        String s = null;
        ArrayList<String> list = new ArrayList<String>();

        while ((s = astream.readAuthor()) != null) {
            s = s.trim();
            if (s.length() > 0) {
                s = stripAnon(s);
                s = s.trim();

                list.add(s);
            }
        }

        return (String[]) list.toArray(new String[1]);

    }

    private String stripAnon(String line) {
        line = perl.substitute("s/\\banon\\b/ /gi", line);
        line = perl.substitute("s/\\(ed\\.\\)/ /gi", line);
        return line;
    }

    /*
     * stripDelim overloaded method is used to remove delimiters from fields au, aff second param String newDelim equal to ";" is provided for aff, cv fields
     * for delim substitution
     */

    private String stripDelim(String line) {
        return stripDelim(line, "");

    }

    private String stripDelim(String line, String newDelim) {
        line = perl.substitute("s/\\|+\\d+\\:+/" + newDelim + "/gi", line);
        if (line.indexOf(";") == 0) {
            line = line.substring(1);
        }
        return line;
    }

    /*
     * stripAsterics method is used to remove asterisk from fields cv and mh
     */

    private String stripAsterics(String line) {
        line = perl.substitute("s/\\*+//gi", line);
        return line;
    }

    /*
     * prepareMulti method is overloaded to achieve additional format functioality: - for cv it calls stripAsterics method to strip prefix asterisk - for
     * docTypes it calles eltDocTypes.getMappedDocType(s) to retrieve additional doc types values for cross-searches with ev2 data sources
     */

    private String[] prepareMulti(String multiString) throws Exception {
        return prepareMulti(multiString, null);
    }

    private String[] prepareMulti(String multiString, Constants constant) throws Exception {
        if (multiString != null) {

            AuthorStream astream = new AuthorStream(new ByteArrayInputStream(multiString.getBytes()));
            String s = null;

            ArrayList<String> list = new ArrayList<String>();
            while ((s = astream.readAuthor()) != null) {
                s = s.trim();
                if (s.length() > 0) {
                    if (constant == null) {
                        list.add(s);
                    } else if (constant.equals(Constants.CVS)) {
                        list.add(stripAsterics(s));
                    } else if (constant.equals(Constants.DT)) {
                        list.add(eltDocTypes.getMappedDocType(s));
                    } else if (constant.equals(Constants.AFF)) {
                        list.add(formatAffiliation(s));
                    } else if (constant.equals(Constants.CO)) {
                        list.add(CountryFormatter.formatCountry(s));
                    }
                }
            }
            return (String[]) list.toArray(new String[1]);
        } else {
            String[] str = new String[] { "" };
            return str;

        }
    }

    private String[] prepareMultiLinkedTerm(String multiString) throws Exception {
        if (multiString != null) {

            AuthorStream astream = new AuthorStream(new ByteArrayInputStream(multiString.getBytes()));
            String s = null;
            ArrayList<String> list = new ArrayList<String>();
            while ((s = astream.readAuthor()) != null) {
                s = s.trim();

                if (perl.match("/\\|/", s)) {
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

    private String formatAffiliation(String result) {

        if ((result.length() == 1) && result.equals(".")) {
            result = "";
        } else if (result.equalsIgnoreCase("University")) {
            result = "";
        }

        return result;
    }

}
