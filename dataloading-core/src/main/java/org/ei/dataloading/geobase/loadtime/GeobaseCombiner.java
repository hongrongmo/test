package org.ei.dataloading.geobase.loadtime;

import java.io.ByteArrayInputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.common.AuthorStream;
import org.ei.dataloading.CombinedWriter;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.Combiner;
import org.ei.common.Country;
import org.ei.dataloading.EVCombinedRec;
import org.ei.common.Language;
import org.ei.dataloading.LoadLookup;
import org.ei.dataloading.XMLWriterCommon;
import org.ei.dataloading.georef.loadtime.GeobaseToGeorefMap;
import org.ei.util.GUID;

public class GeobaseCombiner extends Combiner {

    public static final String AUDELIMITER = new String(new char[] { 30 });
    public static final String IDDELIMITER = new String(new char[] { 29 });
    public static final String GROUPDELIMITER = new String(new char[] { 02 });

    Perl5Util perl = new Perl5Util();

    private int exitNumber;

    private static String tablename;

    public static void main(String args[]) throws Exception {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@206.137.75.51:1521:EI";
        String username = "AP_EV_SEARCH";
        String password = "ei3it";
        url = args[0];
        driver = args[1];
        username = args[2];
        password = args[3];
        int loadNumber = Integer.parseInt(args[4]);
        int recsPerfile = Integer.parseInt(args[5]);
        int exitAt = Integer.parseInt(args[6]);
        tablename = args[7];

        Combiner.TABLENAME = tablename;
        Combiner.EXITNUMBER = exitAt;

        CombinedWriter writer = new CombinedXMLWriter(recsPerfile, loadNumber, "geo");

        GeobaseCombiner c = new GeobaseCombiner(writer);
        if (loadNumber > 3000 || loadNumber < 1000) {
            c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
        } else {
            c.writeCombinedByYear(url, driver, username, password, loadNumber);
        }
    }

    public GeobaseCombiner(CombinedWriter writer) {
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


    public void writeCombinedByWeekHook(Connection con, int weekNumber) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            this.writer.begin();
            stmt = con.createStatement();
            String sqlQuery = "select DESCRIPTOR_MAINTERM_SPC2,AUTHOR2,AUTHOR_AFFILIATION_STATE,ARTICLE_NUMBER,AUTHOR_AFFILIATION_COUNTRY,AUTHOR_AFFILIATION_CITY,ABSTRACT, m_id, doi, SOURCE_VOLUME, SOURCE_ISSUE, ACCESSION_NUMBER,AUTHOR_AFFILIATION, SOURCE_PAGERANGE, AUTHORS, ISBN, CLASSIFICATION, CONFERENCE_CITY, CONFERENCE_CODE,CONFERENCE_NAME, CODEN, DESCRIPTOR_MAINTERM_GDE, DESCRIPTOR_MAINTERM_RGI,DOCUMENT_TYPE, SOURCE_EDITOR, DESCRIPTOR_MAINTERM_SPC, CITATION_LANGUAGE, ABSTRACT_LANGUAGE, TITLETEXT_LANGUAGE, CONFERENCE_CITY, CLASSIFICATION_SUBJECT, CONFERENCE_STATE, ISSUE_TITLE, CONFERENCE_COUNTRY, CONFERENCE_DATE, PAGES, PAGECOUNT, PUBLISHER_NAME, ABBR_SOURCETITLE, ISSN,E_ISSN, CONFERENCE_SPONSORS, SOURCE_TITLE, CITATION_TITLE, TRANSLATED_TITLE, SOURCE_TYPE, VOLUME_TITLE, SOURCE_PUBLICATIONYEAR, SOURCE_PUBLICATIONDATE, load_number, CREATED_DATE, SORT_DATE from "
                + Combiner.TABLENAME + " where load_number ='" + weekNumber + "' AND load_number != 0 and load_number < 1000000";
            // use for update translated_title
            // String sqlQuery =
            // "select DESCRIPTOR_MAINTERM_SPC2,AUTHOR2,AUTHOR_AFFILIATION_STATE,ARTICLE_NUMBER,AUTHOR_AFFILIATION_COUNTRY,AUTHOR_AFFILIATION_CITY,ABSTRACT, m_id, doi, SOURCE_VOLUME, SOURCE_ISSUE, ACCESSION_NUMBER,AUTHOR_AFFILIATION, SOURCE_PAGERANGE, AUTHORS, ISBN, CLASSIFICATION, CONFERENCE_CITY, CONFERENCE_CODE,CONFERENCE_NAME, CODEN, DESCRIPTOR_MAINTERM_GDE, DESCRIPTOR_MAINTERM_RGI,DOCUMENT_TYPE, SOURCE_EDITOR, DESCRIPTOR_MAINTERM_SPC, CITATION_LANGUAGE, ABSTRACT_LANGUAGE, TITLETEXT_LANGUAGE, CONFERENCE_CITY, CLASSIFICATION_SUBJECT, CONFERENCE_STATE, ISSUE_TITLE, CONFERENCE_COUNTRY, CONFERENCE_DATE, PAGES, PAGECOUNT, PUBLISHER_NAME, ABBR_SOURCETITLE, ISSN,E_ISSN, CONFERENCE_SPONSORS, SOURCE_TITLE, CITATION_TITLE, TRANSLATED_TITLE, SOURCE_TYPE, VOLUME_TITLE, SOURCE_PUBLICATIONYEAR, SOURCE_PUBLICATIONDATE, load_number, CREATED_DATE, SORT_DATE from "
            // + Combiner.TABLENAME + " where translated_title is not null AND load_number != 0 and load_number < 1000000";
            // System.out.println("sqlQuery= "+sqlQuery);
            rs = stmt.executeQuery(sqlQuery);
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

    public void writeCombinedByYearHook(Connection con, int year) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;
        try {

            this.writer.begin();
            stmt = con.createStatement();
            if (year == 1000) {
                // return all records which do not have source_publicationyear and source_publicationdate
                rs = stmt
                    .executeQuery("select AUTHOR_AFFILIATION_STATE,ARTICLE_NUMBER,AUTHOR_AFFILIATION_COUNTRY,AUTHOR_AFFILIATION_CITY,ABSTRACT, m_id, doi, SOURCE_VOLUME, SOURCE_ISSUE, ACCESSION_NUMBER,AUTHOR_AFFILIATION, SOURCE_PAGERANGE, AUTHORS, AUTHOR2, ISBN, CLASSIFICATION, CONFERENCE_CODE,CONFERENCE_CITY, CONFERENCE_NAME, CODEN, DESCRIPTOR_MAINTERM_GDE, DESCRIPTOR_MAINTERM_RGI, DOCUMENT_TYPE, SOURCE_EDITOR, DESCRIPTOR_MAINTERM_SPC, DESCRIPTOR_MAINTERM_SPC2, CITATION_LANGUAGE, ABSTRACT_LANGUAGE, TITLETEXT_LANGUAGE, CONFERENCE_CITY, CLASSIFICATION_SUBJECT, CONFERENCE_STATE, ISSUE_TITLE, CONFERENCE_COUNTRY, CONFERENCE_DATE, PAGES, PAGECOUNT, PUBLISHER_NAME, ABBR_SOURCETITLE, ISSN,E_ISSN, CONFERENCE_SPONSORS, SOURCE_TITLE, CITATION_TITLE, TRANSLATED_TITLE, SOURCE_TYPE, VOLUME_TITLE, SOURCE_PUBLICATIONYEAR, SOURCE_PUBLICATIONDATE,PAGECOUNT,PAGES,load_number, CREATED_DATE, SORT_DATE from "
                        + Combiner.TABLENAME
                        + " where SOURCE_PUBLICATIONYEAR is null and SOURCE_PUBLICATIONDATE is null AND load_number != 0 and load_number < 1000000");
            } else {
                // use for update translated_title
                // String sqlQuery =
                // "select AUTHOR_AFFILIATION_STATE,ARTICLE_NUMBER,AUTHOR_AFFILIATION_COUNTRY,AUTHOR_AFFILIATION_CITY,ABSTRACT, m_id, doi, SOURCE_VOLUME, SOURCE_ISSUE, ACCESSION_NUMBER,AUTHOR_AFFILIATION, SOURCE_PAGERANGE, AUTHORS, AUTHOR2, ISBN, CLASSIFICATION, CONFERENCE_CODE,CONFERENCE_CITY, CONFERENCE_NAME, CODEN, DESCRIPTOR_MAINTERM_GDE, DESCRIPTOR_MAINTERM_RGI, DOCUMENT_TYPE, SOURCE_EDITOR, DESCRIPTOR_MAINTERM_SPC, DESCRIPTOR_MAINTERM_SPC2, CITATION_LANGUAGE, ABSTRACT_LANGUAGE, TITLETEXT_LANGUAGE, CONFERENCE_CITY, CLASSIFICATION_SUBJECT, CONFERENCE_STATE, ISSUE_TITLE, CONFERENCE_COUNTRY, CONFERENCE_DATE, PAGES, PAGECOUNT, PUBLISHER_NAME, ABBR_SOURCETITLE, ISSN,E_ISSN, CONFERENCE_SPONSORS, SOURCE_TITLE, CITATION_TITLE, TRANSLATED_TITLE, SOURCE_TYPE, VOLUME_TITLE, SOURCE_PUBLICATIONYEAR, SOURCE_PUBLICATIONDATE,PAGECOUNT,PAGES,load_number, CREATED_DATE, SORT_DATE from "
                // + Combiner.TABLENAME + " where TRANSLATED_TITLE is not null";
                rs = stmt
                    .executeQuery("select AUTHOR_AFFILIATION_STATE,ARTICLE_NUMBER,AUTHOR_AFFILIATION_COUNTRY,AUTHOR_AFFILIATION_CITY,ABSTRACT, m_id, doi, SOURCE_VOLUME, SOURCE_ISSUE, ACCESSION_NUMBER,AUTHOR_AFFILIATION, SOURCE_PAGERANGE, AUTHORS, AUTHOR2, ISBN, CLASSIFICATION, CONFERENCE_CODE,CONFERENCE_CITY, CONFERENCE_NAME, CODEN, DESCRIPTOR_MAINTERM_GDE, DESCRIPTOR_MAINTERM_RGI, DOCUMENT_TYPE, SOURCE_EDITOR, DESCRIPTOR_MAINTERM_SPC, DESCRIPTOR_MAINTERM_SPC2, CITATION_LANGUAGE, ABSTRACT_LANGUAGE, TITLETEXT_LANGUAGE, CONFERENCE_CITY, CLASSIFICATION_SUBJECT, CONFERENCE_STATE, ISSUE_TITLE, CONFERENCE_COUNTRY, CONFERENCE_DATE, PAGES, PAGECOUNT, PUBLISHER_NAME, ABBR_SOURCETITLE, ISSN,E_ISSN, CONFERENCE_SPONSORS, SOURCE_TITLE, CITATION_TITLE, TRANSLATED_TITLE, SOURCE_TYPE, VOLUME_TITLE, SOURCE_PUBLICATIONYEAR, SOURCE_PUBLICATIONDATE,PAGECOUNT,PAGES,load_number, CREATED_DATE, SORT_DATE from "
                        + Combiner.TABLENAME
                        + " where (SOURCE_PUBLICATIONYEAR ='"
                        + year
                        + "' or SOURCE_PUBLICATIONDATE like '%"
                        + year
                        + "%') AND load_number != 0 and load_number < 1000000");
            }
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

    private void writeRecs(ResultSet rs) throws Exception {
        try {

            int i = 0;
            String mid = null;
            while (rs.next()) {
                mid = rs.getString("M_ID");
                EVCombinedRec rec = new EVCombinedRec();
                ++i;

                if (Combiner.EXITNUMBER != 0 && i > Combiner.EXITNUMBER) {
                    break;
                }

                String abString = getStringFromClob(rs.getClob("ABSTRACT"));
                String year = rs.getString("SOURCE_PUBLICATIONYEAR");
                if (year == null || year.equals("null") || year.length() < 1) {
                    year = rs.getString("SOURCE_PUBLICATIONDATE");
                    if (year != null && year.indexOf("-") > -1) {
                        year = year.substring(year.lastIndexOf("-") + 1);
                    }
                }

                if (year == null || year.equals("null") || year.length() < 1) {
                    year = rs.getString("SORT_DATE");
                    if (year != null && year.indexOf("-") > -1) {
                        year = year.substring(year.lastIndexOf("-") + 1);
                    }
                }

                if (year == null || year.equals("null") || year.length() < 1) {
                    year = rs.getString("CREATED_DATE");
                    if (year != null && year.indexOf("-") > -1) {
                        year = year.substring(year.lastIndexOf("-") + 1);
                    }
                }

                if (validYear(year)) {
                    if (rs.getString("AUTHORS") != null) {
                        String authors = rs.getString("AUTHORS");
                        if (rs.getString("AUTHOR2") != null) {
                            authors = authors + rs.getString("AUTHOR2");
                        }
                        authors = LoadLookup.removeSpecialCharacter(authors);
                        authors = removeID(authors, "authors");
                        rec.put(EVCombinedRec.AUTHOR, prepareAuthor(authors));
                        if (rs.getString("AUTHOR_AFFILIATION") != null) {
                            String authorAffiliation = rs.getString("AUTHOR_AFFILIATION");
                            authorAffiliation = LoadLookup.removeSpecialCharacter(authorAffiliation);
                            authorAffiliation = removeID(authorAffiliation, "affiliation");
                            rec.put(EVCombinedRec.AUTHOR_AFFILIATION, authorAffiliation);

                            StringBuffer affilLoc = new StringBuffer();
                            if (rs.getString("AUTHOR_AFFILIATION_COUNTRY") != null) {
                                String country = rs.getString("AUTHOR_AFFILIATION_COUNTRY");
                                country = removeID(country, "country");
                                String countryFormatted = null;

                                if (country != null) {
                                    countryFormatted = Country.formatCountry(country.toLowerCase());
                                }

                                if (countryFormatted != null) {
                                    affilLoc.append(countryFormatted);
                                    rec.put(EVCombinedRec.COUNTRY, countryFormatted.split(AUDELIMITER));
                                }
                            }

                            if (rs.getString("AUTHOR_AFFILIATION_STATE") != null) {
                                String state = rs.getString("AUTHOR_AFFILIATION_STATE");
                                state = removeID(state, "state");
                                affilLoc.append(" ");
                                affilLoc.append(state);
                            }

                            if (rs.getString("AUTHOR_AFFILIATION_CITY") != null) {
                                String city = rs.getString("AUTHOR_AFFILIATION_CITY");
                                city = removeID(city, "city");
                                affilLoc.append(" ");
                                affilLoc.append(city);
                            }

                            if (affilLoc.length() > 0) {
                                rec.put(EVCombinedRec.AFFILIATION_LOCATION, (affilLoc.toString()).split(AUDELIMITER));
                            }
                        }
                    } else if (rs.getString("SOURCE_EDITOR") != null) {
                        rec.put(EVCombinedRec.EDITOR, prepareAuthor(rs.getString("SOURCE_EDITOR")));
                    }

                    if (rs.getString("DOI") != null) {
                        rec.put(EVCombinedRec.DOI, rs.getString("DOI"));
                    }

                    if (rs.getString("CITATION_TITLE") != null) {
                        rec.put(EVCombinedRec.TITLE, rs.getString("CITATION_TITLE"));
                    }

                    if (rs.getString("TRANSLATED_TITLE") != null) {
                        rec.put(EVCombinedRec.TRANSLATED_TITLE, rs.getString("TRANSLATED_TITLE"));
                    }

                    if (rs.getString("VOLUME_TITLE") != null) {
                        rec.put(EVCombinedRec.VOLUME_TITLE, rs.getString("VOLUME_TITLE"));
                    }

                    if (abString != null && abString.length() > 0) {
                        rec.put(EVCombinedRec.ABSTRACT, abString);
                    }

                    String cvs = rs.getString("DESCRIPTOR_MAINTERM_GDE");
                    if (cvs != null) {
                        cvs = LoadLookup.removeSpecialCharacter(cvs);
                        rec.put(EVCombinedRec.CONTROLLED_TERMS, (rs.getString("DESCRIPTOR_MAINTERM_GDE").split(AUDELIMITER)));
                    }

                    if (rs.getString("DESCRIPTOR_MAINTERM_SPC") != null) {
                        String sTerm = rs.getString("DESCRIPTOR_MAINTERM_SPC");
                        if (rs.getString("DESCRIPTOR_MAINTERM_SPC2") != null) {
                            sTerm = sTerm + rs.getString("DESCRIPTOR_MAINTERM_SPC2");
                        }
                        rec.put(EVCombinedRec.UNCONTROLLED_TERMS, (sTerm).split(AUDELIMITER));
                    }

                    if (rs.getString("ISSN") != null || rs.getString("E_ISSN") != null) {
                        String issn = rs.getString("ISSN");
                        String e_issn = rs.getString("E_ISSN");
                        StringBuffer issnString = new StringBuffer();

                        if (issn != null && issn.length() > 0) {
                            issnString.append(issn);
                        }

                        if (issn != null && e_issn != null && issn.length() > 0 && e_issn.length() > 0) {
                            issnString.append(AUDELIMITER);
                        }

                        if (e_issn != null && e_issn.length() > 0) {
                            issnString.append(e_issn);
                        }

                        rec.put(EVCombinedRec.ISSN, (issnString.toString()).split(AUDELIMITER));
                    }

                    if (rs.getString("CODEN") != null) {
                        rec.put(EVCombinedRec.CODEN, rs.getString("CODEN"));
                    }

                    if (rs.getString("ISBN") != null) {
                        rec.put(EVCombinedRec.ISBN, (rs.getString("ISBN")).split(AUDELIMITER));
                    }

                    String st = rs.getString("SOURCE_TITLE");
                    st = LoadLookup.removeSpecialCharacter(st);
                    if (st == null) {
                        st = rs.getString("ABBR_SOURCETITLE");
                    }

                    if (st != null) {
                        rec.put(EVCombinedRec.SERIAL_TITLE, st);
                    }

                    String publisherName = rs.getString("PUBLISHER_NAME");
                    if (publisherName != null) {
                        publisherName = LoadLookup.removeSpecialCharacter(publisherName);
                        rec.put(EVCombinedRec.PUBLISHER_NAME, publisherName.split(AUDELIMITER));
                    }

                    String la = rs.getString("CITATION_LANGUAGE");
                    if (la == null) {
                        la = rs.getString("ABSTRACT_LANGUAGE");
                    }

                    if (la == null) {
                        la = rs.getString("TITLETEXT_LANGUAGE");
                    }

                    if (la != null) {
                        rec.put(EVCombinedRec.LANGUAGE, getLanguage(la).split(AUDELIMITER));
                    }

                    String docType = getGeoDocumentType(rs.getString("SOURCE_TYPE"));

                    /*
                     * if (rec.containsKey(EVCombinedRec.CONTROLLED_TERMS)) { docType = docType + " CORE"; }
                     */

                    rec.put(EVCombinedRec.DOCTYPE, docType);

                    if (rs.getString("CLASSIFICATION") != null) {
                        rec.put(EVCombinedRec.CLASSIFICATION_CODE, (XMLWriterCommon.formatClassCodes(rs.getString("CLASSIFICATION"))).split(AUDELIMITER));
                    }

                    if (rs.getString("CONFERENCE_CODE") != null) {
                        rec.put(EVCombinedRec.CONFERENCE_CODE, rs.getString("CONFERENCE_CODE"));
                    }

                    if (rs.getString("CONFERENCE_NAME") != null) {
                        rec.put(EVCombinedRec.CONFERENCE_NAME, rs.getString("CONFERENCE_NAME"));
                    }

                    String cl = formatConferenceLoc(rs.getString("CONFERENCE_STATE"), rs.getString("CONFERENCE_CITY"), rs.getString("CONFERENCE_COUNTRY"));

                    if (cl.length() > 2) {
                        rec.put(EVCombinedRec.CONFERENCE_LOCATION, cl);
                    }

                    if (rs.getString("CONFERENCE_DATE") != null) {
                        rec.put(EVCombinedRec.MEETING_DATE, rs.getString("CONFERENCE_DATE"));
                    }

                    if (rs.getString("CONFERENCE_SPONSORS") != null) {
                        rec.put(EVCombinedRec.SPONSOR_NAME, rs.getString("CONFERENCE_SPONSORS"));
                    }

                    if (rs.getString("ISSUE_TITLE") != null) {
                        rec.put(EVCombinedRec.MONOGRAPH_TITLE, rs.getString("ISSUE_TITLE"));
                    }

                    rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));

                    rec.put(EVCombinedRec.DATABASE, "geo");
                    rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));

                    if (year != null) {
                        rec.put(EVCombinedRec.PUB_YEAR, year);
                    }

                    String pages = rs.getString("SOURCE_PAGERANGE");

                    if (pages == null) {
                        pages = rs.getString("PAGES");
                    }

                    if (pages == null) {
                        pages = rs.getString("PAGECOUNT");
                    }

                    rec.put(
                        EVCombinedRec.DEDUPKEY,
                        getDedupKey(rec.getString(EVCombinedRec.ISSN), rec.getString(EVCombinedRec.CODEN), rs.getString("SOURCE_VOLUME"),
                            rs.getString("SOURCE_ISSUE"), pages));

                    rec.put(EVCombinedRec.VOLUME, getFirstNumber(rs.getString("SOURCE_VOLUME")));
                    rec.put(EVCombinedRec.ISSUE, getFirstNumber(rs.getString("SOURCE_ISSUE")));
                    rec.put(EVCombinedRec.STARTPAGE, getFirstPage(pages));
                    String notes = null;
                    if (rs.getString("CLASSIFICATION_SUBJECT") != null) {
                        notes = rs.getString("CLASSIFICATION_SUBJECT");
                    }

                    if (rs.getString("CLASSIFICATION_SUBJECT") != null) {
                        notes = notes + AUDELIMITER;
                    }

                    if (notes != null) {
                        rec.put(EVCombinedRec.NOTES, notes.split(AUDELIMITER));
                    }

                    if (rs.getString("DESCRIPTOR_MAINTERM_RGI") != null) {
                        String[] geobasemaintermsrgi = rs.getString("DESCRIPTOR_MAINTERM_RGI").split(AUDELIMITER);
                        rec.put(EVCombinedRec.CHEMICALTERMS, geobasemaintermsrgi);

                        // jam - Added lookup of DESCRIPTOR_MAINTERM_RGI for creation of INT_PATENT_CLASSIFICATION Navigator
                        // This Navigator will be used also for mappable GeoRef Index Terms
                        List<String> navigatorterms = new ArrayList<String>();
                        GeobaseToGeorefMap lookup = GeobaseToGeorefMap.getInstance();
                        for (int termindex = 0; termindex < geobasemaintermsrgi.length; termindex++) {
                            String georefterm = lookup.lookupGeobaseTerm(geobasemaintermsrgi[termindex]);
                            if (georefterm != null) {
                                // System.out.println(" Found " + georefterm + " for " + geobasemaintermsrgi[termindex]);
                                navigatorterms.add(georefterm);
                            }
                        }
                        if (!navigatorterms.isEmpty()) {
                            rec.putIfNotNull(EVCombinedRec.INT_PATENT_CLASSIFICATION, (String[]) navigatorterms.toArray(new String[] {}));
                        }
                    }

                    rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("ACCESSION_NUMBER"));

                    rec.put(EVCombinedRec.PUB_SORT, Integer.toString(i));

                    try {
                        this.writer.writeRec(rec);
                    } catch (Exception e) {
                        System.out.println("MID= " + mid);
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLanguage(String code) {
        StringBuffer languageName = new StringBuffer();
        String lName = null;
        if (code.indexOf(AUDELIMITER) > -1) {
            StringTokenizer tokens = new StringTokenizer(code, AUDELIMITER);
            while (tokens.hasMoreTokens()) {
                String sCode = tokens.nextToken().trim();
                lName = Language.getIso639Language(sCode);
                if (lName != null)
                    languageName.append(lName + AUDELIMITER);
                else
                    languageName.append(sCode + AUDELIMITER);
            }
        } else {
            lName = Language.getIso639Language(code);
            if (lName != null)
                languageName.append(lName);
            else
                languageName.append(code);
        }
        return languageName.toString();
    }

    private String[] prepareMulti(String multiString) throws Exception {
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

    }

    private String[] prepareAuthor(String aString) throws Exception {
        String[] authorArray = null;
        if (aString.indexOf(AUDELIMITER) > -1) {
            authorArray = aString.split(AUDELIMITER);
        } else {
            authorArray = new String[1];
            authorArray[0] = aString;
        }

        return authorArray;
    }

    private String stripAnon(String line) {
        line = perl.substitute("s/\\banon\\b/ /gi", line);
        line = perl.substitute("s/\\(ed\\.\\)/ /gi", line);
        return line;
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
        } else if (coden != null) {
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

    private String formatConferenceLoc(String ms, String mc, String my) {
        StringBuffer b = new StringBuffer(" ");
        if (ms != null) {
            b.append(ms + ", ");
        }

        if (mc != null) {
            b.append(mc + ", ");
        }

        if (my != null) {
            b.append(Country.formatCountry(my.toLowerCase()));
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

    private String removeID(String inputString, String format) throws Exception {
        String[] inputGroupArray = null;
        String[] authorArray = null;
        StringBuffer outputString = new StringBuffer();
        if (inputString != null && inputString.indexOf(GROUPDELIMITER) > -1) {
            inputGroupArray = inputString.split(GROUPDELIMITER);
        } else {
            inputGroupArray = new String[1];
            inputGroupArray[0] = inputString;
        }

        for (int i = 0; i < inputGroupArray.length; i++) {
            String inputGroupString = inputGroupArray[i];
            if (inputGroupString.indexOf(IDDELIMITER) > -1) {
                inputGroupString = inputGroupString.substring(inputGroupString.indexOf(IDDELIMITER) + 1);
            }
            outputString.append(inputGroupString);
            if (i < inputGroupArray.length - 1) {
                outputString.append(AUDELIMITER);
            }
        }
        return outputString.toString();
    }

    public static String getGeoDocumentType(String sourceType) {

        if (sourceType != null) {
            if (sourceType.equalsIgnoreCase("B"))
                return "MR";
            else if (sourceType.equalsIgnoreCase("D") || sourceType.equalsIgnoreCase("J"))
                return "JA";
            else if (sourceType.equalsIgnoreCase("P"))
                return "CA";
        }

        return sourceType;
    }

}
