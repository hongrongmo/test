package org.ei.dataloading.paper.loadtime;

import java.io.ByteArrayInputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.common.Constants;
import org.ei.common.CountryFormatter;
import org.ei.common.AuthorStream;
import org.ei.dataloading.CombinedWriter;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.Combiner;
import org.ei.dataloading.EVCombinedRec;
import org.ei.dataloading.XMLWriterCommon;
import org.ei.util.GUID;

public class PaperChemCombiner extends Combiner {

    Perl5Util perl = new Perl5Util();

    // private int exitNumber;

    private static String tablename;
    private static String where;
    // private static Hashtable<?, ?> cvnoise = new Hashtable<Object, Object>();

    public static void main(String args[]) throws Exception {

        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
        int loadNumber = Integer.parseInt(args[4]);
        int recsPerfile = Integer.parseInt(args[5]);
        int exitAt = Integer.parseInt(args[6]);
        tablename = args[7];
        where = args[8];

        Combiner.TABLENAME = tablename;
        Combiner.EXITNUMBER = exitAt;
        CombinedWriter writer = new CombinedXMLWriter(recsPerfile, loadNumber, "pch");
        System.out.println("write year" + loadNumber);

        PaperChemCombiner c = new PaperChemCombiner(writer);

        if (loadNumber > 3000 || loadNumber < 1000) {
            c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
        } else {
            c.writeCombinedByYear(url, driver, username, password, loadNumber);
        }
        System.out.println("finished loadnumber " + loadNumber);
    }

    public PaperChemCombiner(CombinedWriter writer) {
        super(writer);
    }
    
    public void writeCombinedByTableHook(Connection con)
    		throws Exception
    		{
    			Statement stmt = null;
    			ResultSet rs = null;
    			try
    			{
    			
    				stmt = con.createStatement();
    				System.out.println("Running the query...");
    				String sqlQuery = "select * from " + Combiner.TABLENAME +" where database='" + Combiner.CURRENTDB + "'";
    				System.out.println(sqlQuery);
    				rs = stmt.executeQuery(sqlQuery);
    				
    				System.out.println("Got records ...from table::"+Combiner.TABLENAME);
    				writeRecs(rs);
    				System.out.println("Wrote records.");
    				this.writer.end();
    				this.writer.flush();
    			
    			}
    			finally
    			{
    			
    				if (rs != null)
    				{
    					try
    					{
    						rs.close();
    					}
    					catch (Exception e)
    					{
    						e.printStackTrace();
    					}
    				}
    				
    				if (stmt != null)
    				{
    					try
    					{
    						stmt.close();
    					}
    					catch (Exception e)
    					{
    						e.printStackTrace();
    					}
    				}
    			}
    		}


    public void writeCombinedByYearHook(Connection con, int year) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;
        try {

            this.writer.begin();
            stmt = con.createStatement();
            if (year != 3000) {
                rs = stmt
                    .executeQuery("select M_ID, id, an, ex, ig, su, ab, au, cp, em, af, ac, asp, ay, ed, ef, ec, es, ey, mt, st, se, cf, bn, sn, cn, vo, iss, sd, SUBSTR(yr,1,4) yr, md, m1, m2, ml, mc, ms, my, sp, pa, pn, pc, ps, py, pp, xp, vx, la, dt, cc, at, nr, ti, tt, tr, cls, fl, pt, pi, do, rn, media, csess, patno, pling, appln, prior_num, assig, pcode, claim, sourc, nofig, notab, sub_index, specn, suppl, pdfix, LOAD_NUMBER from "
                        + Combiner.TABLENAME + " where SUBSTR(yr,1,4) ='" + year + "' AND load_number < 1000000");
            } else {
                rs = stmt
                    .executeQuery("select M_ID, id, an, ex, ig, su, ab, au, cp, em, af, ac, asp, ay, ed, ef, ec, es, ey, mt, st, se, cf, bn, sn, cn, vo, iss, sd, SUBSTR(yr,1,4) yr, md, m1, m2, ml, mc, ms, my, sp, pa, pn, pc, ps, py, pp, xp, vx, la, dt, cc, at, nr, ti, tt, tr, cls, fl, pt, pi, do, rn, media, csess, patno, pling, appln, prior_num, assig, pcode, claim, sourc, nofig, notab, sub_index, specn, suppl, pdfix, LOAD_NUMBER from "
                        + Combiner.TABLENAME + " where " + where);
            }
            System.out.println("Got records ...");
            writeRecs(rs);
            System.out.println("Wrote records1.");
            this.writer.end();

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
        int i = 0;
        while (rs.next()) {

            EVCombinedRec rec = new EVCombinedRec();

            ++i;

            if (Combiner.EXITNUMBER != 0 && i > Combiner.EXITNUMBER) {
                break;
            }

            String abString = getStringFromClob(rs.getClob("ab"));

            if (validYear(rs.getString("yr"))) {

                if (rs.getString("au") != null) {
                    rec.put(EVCombinedRec.AUTHOR, prepareAuthor(rs.getString("au")));
                } else if (rs.getString("ed") != null) {
                    rec.put(EVCombinedRec.EDITOR, prepareAuthor(rs.getString("ed")));
                }

                String st = rs.getString("st");
                if (st == null) {
                    st = rs.getString("se");
                }

                if (st != null) {
                    rec.put(EVCombinedRec.SERIAL_TITLE, st);
                }

                if (rs.getString("af") != null) {
                    rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareMulti(formatAffiliation(rs.getString("af"), st)));
                } else if (rs.getString("ef") != null) {
                    rec.put(EVCombinedRec.EDITOR_AFFILIATION, prepareMulti(formatAffiliation(rs.getString("ef"), st)));
                }

                if (rs.getString("ti") != null) {
                    rec.put(EVCombinedRec.TITLE, rs.getString("ti"));
                }

                if (rs.getString("tt") != null) {
                    rec.put(EVCombinedRec.TRANSLATED_TITLE, rs.getString("tt"));
                }

                if (abString != null && abString.length() > 99) {
                    rec.put(EVCombinedRec.ABSTRACT, abString);
                }

                if (rs.getString("pt") != null) {
                    rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("pt")));
                }

                if (rs.getString("fl") != null) {
                    rec.put(EVCombinedRec.UNCONTROLLED_TERMS, prepareMulti(rs.getString("fl")));
                }

                if (rs.getString("sn") != null) {
                    rec.put(EVCombinedRec.ISSN, rs.getString("sn"));
                }

                if (rs.getString("cn") != null) {
                    rec.put(EVCombinedRec.CODEN, rs.getString("cn"));
                }

                if (rs.getString("bn") != null) {
                    rec.put(EVCombinedRec.ISBN, rs.getString("bn"));
                }

                if (rs.getString("pn") != null) {
                    rec.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("pn"));
                }

                if (rs.getString("tr") != null) {
                    rec.put(EVCombinedRec.TREATMENT_CODE, prepareMulti(getTreatmentCode(rs.getString("tr"))));
                }

                String la = rs.getString("la");
                if (la != null) {
                    rec.put(EVCombinedRec.LANGUAGE, prepareMulti(la));
                }

                String docType = rs.getString("dt");
                if (docType == null) {
                    docType = "";
                }

                if (rec.containsKey(EVCombinedRec.CONTROLLED_TERMS)) {
                    docType = docType + " CORE";
                }

                rec.put(EVCombinedRec.DOCTYPE, docType);

                if (rs.getString("cls") != null) {
                    rec.put(EVCombinedRec.CLASSIFICATION_CODE, prepareMulti(XMLWriterCommon.formatClassCodes(rs.getString("cls"))));
                }

                if (rs.getString("cc") != null) {
                    rec.put(EVCombinedRec.CONFERENCE_CODE, rs.getString("cc"));
                }

                if (rs.getString("cf") != null) {
                    rec.put(EVCombinedRec.CONFERENCE_NAME, rs.getString("cf"));
                }

                String cl = formatConferenceLoc(rs.getString("ms"), rs.getString("mc"), rs.getString("my"));

                if (cl.length() > 2) {
                    rec.put(EVCombinedRec.CONFERENCE_LOCATION, cl);
                }

                if (rs.getString("m2") != null) {
                    rec.put(EVCombinedRec.MEETING_DATE, rs.getString("m2"));
                }

                if (rs.getString("sp") != null) {
                    rec.put(EVCombinedRec.SPONSOR_NAME, rs.getString("sp"));
                }

                if (rs.getString("mt") != null) {
                    rec.put(EVCombinedRec.MONOGRAPH_TITLE, rs.getString("mt"));
                }
                if (rs.getString("assig") != null) {
                    rec.put(EVCombinedRec.COMPANIES, prepareMulti(rs.getString("assig")));
                }

                if (rs.getString("patno") != null) {
                    rec.put(EVCombinedRec.PATENT_NUMBER, rs.getString("patno"));
                }

                String patinfo = formatPatentInfo(rs.getString("appln"), rs.getString("prior_num"), rs.getString("pling"), rs.getString("pcode"));

                if (patinfo.length() > 2) {
                    rec.put(EVCombinedRec.PATENTAPPDATE, prepareMulti(patinfo));
                }

                if (rs.getString("sd") != null && rs.getString("patno") != null) {
                    rec.put(EVCombinedRec.PATENTISSUEDATE, rs.getString("sd"));
                }

                if (rs.getString("py") != null) {
                    rec.put(EVCombinedRec.COUNTRY, prepareMulti(CountryFormatter.formatCountry(rs.getString("py"))));

                }

                rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));

                rec.put(EVCombinedRec.DATABASE, "pch");
                rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));

                if (rs.getString("yr") != null) {
                    rec.put(EVCombinedRec.PUB_YEAR, rs.getString("yr"));
                }

                rec.put(EVCombinedRec.DEDUPKEY,
                    getDedupKey(rec.get(EVCombinedRec.ISSN), rec.get(EVCombinedRec.CODEN), rs.getString("vo"), rs.getString("iss"), rs.getString("xp")));

                rec.put(EVCombinedRec.VOLUME, getFirstNumber(rs.getString("vo")));
                rec.put(EVCombinedRec.ISSUE, getFirstNumber(rs.getString("iss")));
                rec.put(EVCombinedRec.STARTPAGE, getFirstNumber(rs.getString("xp")));

                rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("an"));

                if (rs.getString("do") != null) {
                    rec.put(EVCombinedRec.DOI, rs.getString("do"));
                }

                this.writer.writeRec(rec);
            }

        }
    }

    private String stripAnon(String line) {
        line = perl.substitute("s/\\banon\\b/ /gi", line);
        line = perl.substitute("s/\\(ed\\.\\)/ /gi", line);
        return line;
    }

    private String[] prepareAuthor(String aString) throws Exception {
        StringBuffer buf = new StringBuffer();
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

    private String getDedupKey(String issn, String coden, String volume, String issue, String page) throws Exception {

        String firstVolume = getFirstNumber(volume);
        String firstIssue = getFirstNumber(issue);
        String firstPage = getFirstNumber(page);

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

    private String getTreatmentCode(String tc) {
        StringBuffer tbuff = new StringBuffer();

        for (int i = 0; i < tc.length(); ++i) {
            char c = tc.charAt(i);
            if (i > 0) {
                tbuff.append(";");
            }

            if (c == 'A') {
                tbuff.append("APP");
            } else if (c == 'B') {
                tbuff.append("BIO");
            } else if (c == 'E') {
                tbuff.append("ECO");
            } else if (c == 'X') {
                tbuff.append("EXP");
            } else if (c == 'G') {
                tbuff.append("GEN");
            } else if (c == 'H') {
                tbuff.append("HIS");
            } else if (c == 'L') {
                tbuff.append("LIT");
            } else if (c == 'M') {
                tbuff.append("MAN");
            } else if (c == 'N') {
                tbuff.append("NUM");
            } else if (c == 'T') {
                tbuff.append("THR");
            } else {
                tbuff.append("NA");
            }
        }

        return tbuff.toString();
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

    private String formatConferenceLoc(String ms, String mc, String my) {
        StringBuffer b = new StringBuffer(" ");
        if (ms != null) {
            b.append(ms + ", ");
        }

        if (mc != null) {
            b.append(mc + ", ");
        }

        if (my != null) {
            b.append(my);
        }

        return b.toString();

    }

    private String formatPatentInfo(String app, String prn, String pln, String pcd) {
        StringBuffer b = new StringBuffer(" ");
        if (app != null) {
            b.append(app + ";");
        }
        if (prn != null) {
            b.append(prn + ";");
        }
        if (pln != null) {
            b.append(pln + ";");
        }
        if (pcd != null) {
            b.append(pcd);
        }
        return b.toString();
    }

    private String getStringFromClob(Clob clob) throws Exception {
        String temp = null;
        if (clob != null) {
            temp = clob.getSubString(1, (int) clob.length());
        }

        return temp;
    }

    // writeCombinedByWeekNumber

    public void writeCombinedByWeekHook(Connection con, int weekNumber) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            this.writer.begin();
            stmt = con.createStatement();

            rs = stmt
                .executeQuery("select M_ID, id, an, ex, ig, su, ab, au, cp, em, af, ac, asp, ay, ed, ef, ec, es, ey, mt, st, se, cf, bn, sn, cn, vo, iss, sd, substr(yr,1,4) as yr, md, m1, m2, ml, mc, ms, my, sp, pa, pn, pc, ps, py, pp, xp, vx, la, dt, cc, at, nr, ti, tt, tr, cls, fl, pt, pi, do, rn, media, csess, patno, pling, appln, prior_num, assig, pcode, claim, sourc, nofig, notab, sub_index, specn, suppl, pdfix , LOAD_NUMBER from "
                    + tablename + " where load_number = '" + weekNumber + "'");

            writeRecs(rs);
            this.writer.end();

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
                    list.add(s);
                }

            }
            return (String[]) list.toArray(new String[1]);
        } else {
            String[] str = new String[] { "" };
            return str;

        }
    }

    // added method calls for Author Affiliation filed to remove 'Anon', 'et al'
    // if author affilation is the same as serial title - remove aff filed from extract
    // replace '&' with 'and'
    // remove following serial title from aff
    /*
     * Graphic Arts Monthly International Paperboard Industry Paper, London Allgemeine Papier-Rundschau PaperAge Papier, Carton & Cellulose European Papermaker
     * PIMA's North American Papermaker
     */

    private static Hashtable<String, String> removeaff = new Hashtable<String, String>();

    static {
        removeaff.put("s/\\banon\\b/ /gi", "s/\\banon\\b/ /gi");
        removeaff.put("s/\\bet al\\b/ /gi", "s/\\bet al\\b/ /gi");
        removeaff.put("s/\\s+&\\s+/ and /gi", "s/\\s+&\\s+/ and /gi");
        removeaff.put("s/\\bGraphic Arts Monthly\\b/ /gi", "s/\\bGraphic Arts Monthly\\b/ /gi");
        removeaff.put("s/\\bInternational Paperboard Industry\\b/ /gi", "s/\\bInternational Paperboard Industry\\b/ /gi");
        removeaff.put("s/\\bPaper, London\\b/ /gi", "s/\\bPaper, London\\b/ /gi");
        removeaff.put("s/\\bAllgemeine Papier-Rundschau\\b/ /gi", "s/\\bAllgemeine Papier-Rundschau\\b/ /gi");
        removeaff.put("s/\\bPaperAge\\b/ /gi", "s/\\bPaperAge\\b/ /gi");
        removeaff.put("s/\\bPapier, Carton & Cellulose\\b/ /gi", "s/\\bPapier, Carton & Cellulose\\b/ /gi");
        removeaff.put("s/\\bEuropean Papermaker\\b/ /gi", "s/\\bEuropean Papermaker\\b/ /gi");
        removeaff.put("s/\\bPIMA\'s North American Papermaker\\b/ /gi", "s/\\bPIMA\'s North American Papermaker\\b/ /gi");
    }

    private String formatAffiliation(String result, String stitle) {
        Enumeration<String> en = removeaff.keys();
        if (result != null && !result.trim().equals("")) {
            while (en.hasMoreElements()) {
                result = perl.substitute((String) en.nextElement(), result);
            }
            if (stitle != null && !stitle.trim().equals("") && stitle.equalsIgnoreCase(result)) {
                result = "";
            }
        }
        return result;
    }

}
