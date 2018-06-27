package org.ei.data.geobase.runtime;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.common.Country;
import org.ei.common.Language;
import org.ei.common.georef.*;
import org.ei.domain.Abstract;
import org.ei.domain.Affiliation;
import org.ei.domain.Affiliations;
import org.ei.domain.Citation;
import org.ei.domain.Classifications;
import org.ei.domain.Contributor;
import org.ei.domain.Contributors;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Detail;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.DocumentBuilderException;
import org.ei.domain.DocumentTypeConverter;
import org.ei.domain.EIDoc;
import org.ei.domain.ElementDataMap;
import org.ei.domain.FullDoc;
import org.ei.domain.ISBN;
import org.ei.domain.ISSN;
import org.ei.domain.Issue;
import org.ei.domain.Key;
import org.ei.domain.Keys;
import org.ei.domain.PageRange;
import org.ei.domain.RIS;
import org.ei.domain.Volume;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.util.StringUtil;

public class GEODocBuilder implements DocumentBuilder {
    public static String GEO_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String GEO_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER_TEXT = "Ei";
    private static final Key E_ISSN = new Key(Keys.E_ISSN, "Electronic ISSN");
    private static final Key GEO_CLASS_CODES = new Key(Keys.CLASS_CODES, "Classification codes");
    private static final Key GEO_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Index terms");
    public static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.AUTHOR_AFFS, Keys.SOURCE, Keys.MONOGRAPH_TITLE,
        Keys.PAGE_RANGE, Keys.VOLISSUE, Keys.PUBLICATION_YEAR, Keys.PUBLISHER, Keys.ISSUE_DATE, Keys.ISSN, Keys.DOC_TYPE, Keys.LANGUAGE, Keys.NO_SO, Keys.COPYRIGHT,
        Keys.COPYRIGHT_TEXT };
    public static final Key[] ABSTRACT_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.EDITOR_AFFS, Keys.AUTHOR_AFFS, Keys.VOLISSUE,
        Keys.SOURCE, Keys.PUBLICATION_YEAR, Keys.ISSUE_DATE, Keys.MONOGRAPH_TITLE, Keys.PAGE_RANGE, Keys.ARTICLE_NUMBER, Keys.CONFERENCE_NAME, Keys.ISSN,
        Keys.E_ISSN, Keys.ISBN, Keys.DOC_TYPE, Keys.CODEN, Keys.PUBLISHER, Keys.I_PUBLISHER, Keys.CONF_DATE, Keys.SPONSOR, Keys.PROVIDER, Keys.LANGUAGE, Keys.MAIN_HEADING,
        Keys.INDEX_TERM, Keys.SPECIES_TERMS, Keys.REGION_CONTROLLED_TERMS, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS, Keys.ABSTRACT, Keys.CLASS_CODES,
        Keys.NUMBER_OF_REFERENCES, Keys.NO_SO, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    public static final Key[] DETAILED_KEYS = { Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.TITLE_TRANSLATION, Keys.AUTHORS, Keys.EDITORS, Keys.AUTHOR_AFFS,
        Keys.EDITOR_AFFS, Keys.CORRESPONDENCE_PERSON, Keys.SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.VOLUME, Keys.ISSUE, Keys.MONOGRAPH_TITLE,
        Keys.ISSUE_DATE, Keys.PUBLICATION_YEAR, Keys.PAGE_RANGE, Keys.PAGE_COUNT, Keys.ARTICLE_NUMBER, Keys.LANGUAGE, Keys.ISSN, Keys.E_ISSN, Keys.CODEN,
        Keys.ISBN, Keys.DOC_TYPE, Keys.SOURCE_COUNTRY, Keys.CONFERENCE_NAME, Keys.CONF_DATE, Keys.MEETING_LOCATION, Keys.CONF_CODE, Keys.SPONSOR,
        Keys.PUBLISHER, Keys.ABSTRACT, Keys.ABSTRACT_TYPE, Keys.NUMBER_OF_REFERENCES, Keys.MAIN_HEADING, Keys.CONTROLLED_TERMS, Keys.SPECIES_TERMS,
        Keys.REGION_CONTROLLED_TERMS, Keys.CLASS_CODES, Keys.TREATMENTS, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS, Keys.DOI, Keys.DOCID, Keys.COPYRIGHT,
        Keys.COPYRIGHT_TEXT, Keys.PROVIDER };
    private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_LA, Keys.RIS_N1, Keys.RIS_TI, Keys.RIS_T1, Keys.RIS_BT, Keys.RIS_JO, Keys.RIS_T3,
        Keys.RIS_AUS, Keys.RIS_AD, Keys.RIS_EDS, Keys.RIS_VL, Keys.RIS_IS, Keys.RIS_PY, Keys.RIS_AN, Keys.RIS_SP, Keys.RIS_EP, Keys.RIS_SN, Keys.RIS_S1,
        Keys.RIS_MD, Keys.RIS_CY, Keys.RIS_PB, Keys.RIS_N2, Keys.RIS_KW, Keys.RIS_CVS, Keys.RIS_FLS, Keys.RIS_DO, Keys.BIB_TY };
    private static final Key[] XML_KEYS = { Keys.ISSN, Keys.E_ISSN, Keys.MAIN_HEADING, Keys.NO_SO, Keys.MONOGRAPH_TITLE, Keys.PUBLICATION_YEAR,
        Keys.VOLUME_TITLE, Keys.CONTROLLED_TERM, Keys.ISBN, Keys.AUTHORS, Keys.DOCID, Keys.SOURCE, Keys.NUMVOL, Keys.EDITOR_AFFS, Keys.EDITORS, Keys.PUBLISHER,
        Keys.VOLUME, Keys.AUTHOR_AFFS, Keys.PROVIDER, Keys.ISSUE_DATE, Keys.COPYRIGHT_TEXT, Keys.DOI, Keys.PAGE_COUNT, Keys.ARTICLE_NUMBER,
        Keys.PUBLICATION_DATE, Keys.TITLE, Keys.LANGUAGE, Keys.PAGE_RANGE, Keys.PAPER_NUMBER, Keys.COPYRIGHT, Keys.ISSUE, Keys.ACCESSION_NUMBER,
        Keys.CONTROLLED_TERMS };
    public static final String AUDELIMITER = new String(new char[] { 30 });
    public static final String IDDELIMITER = new String(new char[] { 29 });
    public static final String GROUPDELIMITER = new String(new char[] { 02 });
    private Database database;
    private static String queryCitation = "select COPYRIGHT,COPYRIGHT_TYPE,M_ID,SOURCE_TYPE,CITATION_TITLE,TRANSLATED_TITLE,AUTHORS,AUTHOR2,AUTHOR_AFFILIATION,AUTHOR_ADDRESS_PART,AUTHOR_AFFILIATION_CITY,AUTHOR_AFFILIATION_STATE,AUTHOR_AFFILIATION_COUNTRY,CONFERENCE_NAME,SOURCE_EDITOR,SOURCE_TITLE,ABBR_SOURCETITLE,SOURCE_VOLUME,SOURCE_ISSUE,SOURCE_PUBLICATIONDATE,ISSUE_TITLE,VOLUME_TITLE,PUBLISHER_NAME,SOURCE_PUBLICATIONYEAR,SOURCE_PUBLICATIONDATE,CREATED_DATE,SORT_DATE,REPORT_NUMBER,PAGES,PAGECOUNT,SOURCE_PAGERANGE,ARTICLE_NUMBER,CITATION_LANGUAGE,ABSTRACT_LANGUAGE,TITLETEXT_LANGUAGE,ISSN,E_ISSN,DOI,CODEN,ISBN,ACCESSION_NUMBER,CORRESPONDENCE_PERSON,CORRESPONDENCE_AFFILIATION,LOAD_NUMBER from geo_master where M_ID IN ";
    private static String queryXMLCitation = "select COPYRIGHT,COPYRIGHT_TYPE,M_ID,SOURCE_TYPE,CITATION_TITLE,TRANSLATED_TITLE,AUTHORS,AUTHOR2,AUTHOR_AFFILIATION,AUTHOR_ADDRESS_PART,AUTHOR_AFFILIATION_CITY,AUTHOR_AFFILIATION_STATE,AUTHOR_AFFILIATION_COUNTRY,SOURCE_EDITOR,SOURCE_TITLE,ABBR_SOURCETITLE,SOURCE_VOLUME,SOURCE_ISSUE,SOURCE_PUBLICATIONDATE,ISSUE_TITLE,VOLUME_TITLE,PUBLISHER_NAME,SOURCE_PUBLICATIONYEAR,SOURCE_PUBLICATIONDATE,CREATED_DATE,SORT_DATE,REPORT_NUMBER,PAGES,PAGECOUNT,SOURCE_PAGERANGE,ARTICLE_NUMBER,CITATION_LANGUAGE,ABSTRACT_LANGUAGE,TITLETEXT_LANGUAGE,ISSN, E_ISSN,DOI,ISBN,LOAD_NUMBER,ACCESSION_NUMBER,DESCRIPTOR_MAINTERM_GDE,DESCRIPTOR_MAINTERM_RGI,DESCRIPTOR_MAINTERM_SPC,DESCRIPTOR_MAINTERM_SPC2,CORRESPONDENCE_PERSON,CORRESPONDENCE_AFFILIATION from geo_master where M_ID IN ";
    private static String queryAbstracts = "select COPYRIGHT,COPYRIGHT_TYPE,M_ID,ACCESSION_NUMBER,SOURCE_TYPE,CITATION_TITLE,TRANSLATED_TITLE,AUTHORS,AUTHOR2,AUTHOR_AFFILIATION,AUTHOR_ADDRESS_PART,AUTHOR_AFFILIATION_CITY,AUTHOR_AFFILIATION_STATE,AUTHOR_AFFILIATION_COUNTRY,SOURCE_EDITOR,SOURCE_TITLE,ABBR_SOURCETITLE,SOURCE_VOLUME,SOURCE_ISSUE,SOURCE_PUBLICATIONDATE,ISSUE_TITLE,VOLUME_TITLE,PUBLISHER_NAME,SOURCE_PUBLICATIONYEAR,SOURCE_PUBLICATIONDATE,CREATED_DATE,SORT_DATE,REPORT_NUMBER,PAGES,PAGECOUNT,SOURCE_PAGERANGE,ARTICLE_NUMBER,CITATION_LANGUAGE,ABSTRACT_LANGUAGE,TITLETEXT_LANGUAGE,ISSN, E_ISSN,CODEN,ISBN,CONFERENCE_NAME,CONFERENCE_CITY,CONFERENCE_STATE,CONFERENCE_COUNTRY,PUBLISHER_NAME,PUBLISHER_CITY,PUBLSHER_STATE,PUBLISHER_COUNTRY,ABSTRACT,REFFERENT_COUNT,ABSTRACT_SOURCE,DESCRIPTOR_MAINTERM_GDE,DESCRIPTOR_MAINTERM_RGI,DESCRIPTOR_MAINTERM_SPC,DESCRIPTOR_MAINTERM_SPC2,LOAD_NUMBER,DOI,CORRESPONDENCE_PERSON,CORRESPONDENCE_AFFILIATION,CLASSIFICATION_SUBJECT,CLASSIFICATION from geo_master where   M_ID IN ";
    private static String queryDetailed = "select COPYRIGHT,COPYRIGHT_TYPE,M_ID,ACCESSION_NUMBER,CITATION_TITLE,TRANSLATED_TITLE,AUTHORS,AUTHOR2,AUTHOR_AFFILIATION,AUTHOR_ADDRESS_PART,AUTHOR_AFFILIATION_CITY,AUTHOR_AFFILIATION_STATE,AUTHOR_AFFILIATION_COUNTRY,SOURCE_EDITOR,SOURCE_TITLE,ABBR_SOURCETITLE,SOURCE_VOLUME,SOURCE_ISSUE,SOURCE_COUNTRY,SOURCE_PUBLICATIONDATE,ISSUE_TITLE,VOLUME_TITLE,PUBLISHER_NAME,SOURCE_PUBLICATIONYEAR,SOURCE_PUBLICATIONDATE,CREATED_DATE,SORT_DATE,REPORT_NUMBER,PAGES,ARTICLE_NUMBER,PAGECOUNT,SOURCE_PAGERANGE,CITATION_LANGUAGE,ABSTRACT_LANGUAGE,TITLETEXT_LANGUAGE,ISSN,E_ISSN,CODEN,ISBN,CONFERENCE_NAME,CONFERENCE_CITY,CONFERENCE_STATE,CONFERENCE_COUNTRY,CONFERENCE_SPONSORS,PUBLISHER_CITY,PUBLSHER_STATE,PUBLISHER_COUNTRY,ABSTRACT,REFFERENT_COUNT,ABSTRACT_SOURCE,DESCRIPTOR_MAINTERM_GDE,DESCRIPTOR_MAINTERM_RGI,DESCRIPTOR_MAINTERM_SPC,DESCRIPTOR_MAINTERM_SPC2,CONFERENCE_CODE,CLASSIFICATION,CLASSIFICATION_DESCRIPTION,CORRESPONDENCE_PERSON,CORRESPONDENCE_AFFILIATION,CLASSIFICATION_SUBJECT,LOAD_NUMBER,DOI,SOURCE_TYPE,CONFERENCE_DATE,CONFERENCE_LOCATION,CORRESPONDENCE_PERSON,CORRESPONDENCE_AFFILIATION,CORRESPONDENCE_ADDRESSPART,CORRESPONDENCE_AFF_CITY,CORRESPONDENCE_AFF_STATE,CORRESPONDENCE_AFF_COUNTRY,CORRESPONDENCE_EMAIL from geo_master where M_ID IN ";

    private static String queryPreview = "select M_ID, ABSTRACT from geo_master where M_ID IN ";

    public DocumentBuilder newInstance(Database database) {
        return new GEODocBuilder(database);
    }

    public GEODocBuilder() {
    }

    public GEODocBuilder(Database database) {
        this.database = database;
    }

    /**
     * This method takes a list of DocID objects and dataFormat and returns a List of EIDoc Objects based on a particular dataformat @ param listOfDocIDs @
     * param dataFormat @ return List --list of EIDoc's @ exception DocumentBuilderException
     */
    public List<EIDoc> buildPage(List<DocID> listOfDocIDs, String dataFormat) throws DocumentBuilderException {
        List<EIDoc> l = null;
        if (dataFormat.equals(Citation.CITATION_FORMAT)) {
            l = loadCitations(listOfDocIDs);
        } else if (dataFormat.equals(Abstract.ABSTRACT_FORMAT)) {
            l = loadAbstracts(listOfDocIDs);
        } else if (dataFormat.equals(FullDoc.FULLDOC_FORMAT)) {
            l = loadDetailed(listOfDocIDs);
        } else if (dataFormat.equalsIgnoreCase("RIS")) {
            l = loadRIS(listOfDocIDs);
        } else if (dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT)) {
            l = loadXMLCitations(listOfDocIDs);
        } else if (Citation.CITATION_PREVIEW.equals(dataFormat)) {
            l = loadPreviewData(listOfDocIDs);
        }

        return l;
    }

    private List<EIDoc> loadPreviewData(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        // Perl5Util perl = new Perl5Util();
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

        List<EIDoc> list = new ArrayList<EIDoc>();
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;

        try {
            String INString = buildINString(listOfDocIDs);
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryPreview + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_PREVIEW);

                ht.put(Keys.DOCID, (DocID) oidTable.get(rset.getString("M_ID")));
                ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.getStringFromClob(rset.getClob("ABSTRACT"))));

                list.add(eiDoc);

            }
        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {

            if (rset != null) {
                if (rset != null) {
                    try {
                        rset.close();
                    } catch (Exception e1) {
                        throw new DocumentBuilderException("database connection pool problem", e1);
                    }
                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (Exception sqle) {
                        throw new DocumentBuilderException("database connection pool problem", sqle);
                    }
                }

                if (con != null) {
                    try {
                        broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                    } catch (Exception cpe) {
                        throw new DocumentBuilderException("database connection pool problem", cpe);
                    }
                }
            }
        }
        return list;
    }

    private List<EIDoc> loadAbstracts(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Perl5Util perl = new Perl5Util();
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

        List<EIDoc> list = new ArrayList<EIDoc>();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildINString(listOfDocIDs);

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryAbstracts + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();

                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));

                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, GEO_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, GEO_TEXT_COPYRIGHT));

                // EX
                if (rset.getString("ACCESSION_NUMBER") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ACCESSION_NUMBER")));
                }

                // ST
                String sourceTitleString = rset.getString("SOURCE_TITLE");
                if (sourceTitleString != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, changeInfToSub(sourceTitleString)));
                }

                if (rset.getString("ABBR_SOURCETITLE") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, changeInfToSub(rset.getString("ABBR_SOURCETITLE"))));
                }

                String year = getYear(rset.getString("SOURCE_PUBLICATIONYEAR"), rset.getString("SOURCE_PUBLICATIONDATE"), rset.getString("CREATED_DATE"),
                    rset.getString("SORT_DATE"));

                if (year != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(year, perl));
                }

                String strPages = getPage(rset.getString("SOURCE_PAGERANGE"), rset.getString("PAGES"), rset.getString("PAGECOUNT"));

                if (strPages != null && strPages.length() > 0) {
                    if (strPages.indexOf("p") < 0 && strPages.indexOf("P") < 0) {
                        strPages = "p " + strPages.trim();
                    }
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));
                }

                if (rset.getString("PAGECOUNT") != null) {
                    String pageCount = rset.getString("PAGECOUNT").replaceAll("p", " ");
                    ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, pageCount));
                }

                if (rset.getString("ARTICLE_NUMBER") != null) {
                    ht.put(Keys.ARTICLE_NUMBER, new XMLWrapper(Keys.ARTICLE_NUMBER, rset.getString("ARTICLE_NUMBER")));
                }

                String strTitle = rset.getString("TRANSLATED_TITLE");
                String strCTitle = rset.getString("CITATION_TITLE");
                if (strTitle != null && strCTitle != null) {
                    strCTitle = strCTitle.trim();
                    if (strCTitle.charAt(0) != '(') {
                        strTitle = strTitle.concat(" (").concat(strCTitle).concat(")");
                    } else {
                        strTitle = strTitle.concat(" ").concat(strCTitle);
                    }
                } else if (strCTitle != null) {
                    strTitle = strCTitle;
                }

                if (strTitle != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, changeInfToSub(strTitle)));
                }

                if (rset.getString("AUTHORS") != null) {
                    String authorString = rset.getString("AUTHORS");
                    if (rset.getString("AUTHOR2") != null) {
                        authorString = authorString + rset.getString("AUTHOR2");
                    }
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(authorString, Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);

                    if (rset.getString("AUTHOR_AFFILIATION") != null) {
                        String affiliation = rset.getString("AUTHOR_AFFILIATION");
                        if (affiliation != null && affiliation.indexOf(",") > -1) {
                            affiliation = affiliation.replaceAll(",\\s*", ", ");
                        }

                        String affiliationAddress = rset.getString("AUTHOR_ADDRESS_PART");
                        String affiliationCity = rset.getString("AUTHOR_AFFILIATION_CITY");
                        String affiliationState = rset.getString("AUTHOR_AFFILIATION_STATE");
                        String affiliationCountry = rset.getString("AUTHOR_AFFILIATION_COUNTRY");

                        List<Affiliation> affiliationList = getContributorsAff(Keys.AUTHOR_AFFS, affiliation, affiliationAddress, affiliationCity,
                            affiliationState, affiliationCountry);
                        Affiliation affil = (Affiliation) affiliationList.get(0);
                        authors.setFirstAffiliation(affil);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affiliationList));
                    }

                } else {
                    if (rset.getString("SOURCE_EDITOR") != null) {
                        String strED = rset.getString("SOURCE_EDITOR");
                        Contributors editors = new Contributors(Keys.EDITORS, getEditors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);
                    }
                }

                if (rset.getString("SOURCE_TITLE") != null || rset.getString("ABBR_SOURCETITLE") != null || rset.getString("ISSUE_TITLE") != null
                    || rset.getString("PUBLISHER_NAME") != null) {

                    // VO
                    String strVolIss = StringUtil.EMPTY_STRING;
                    if (rset.getString("SOURCE_VOLUME") != null) {
                        strVolIss = strVolIss.concat(rset.getString("SOURCE_VOLUME"));
                        ht.put(Keys.VOLUME, new Volume(rset.getString("SOURCE_VOLUME"), perl));
                    }

                    String sourceIssue = rset.getString("SOURCE_ISSUE");

                    if (sourceIssue != null && (sourceIssue.trim()).length() > 0) {
                        ht.put(Keys.ISSUE, new Issue(sourceIssue, perl));

                        if (strVolIss != null && strVolIss.length() > 0) {
                            strVolIss = strVolIss.concat(", ").concat(sourceIssue);
                        } else {
                            strVolIss = replaceIssueNullWithEmptyString(sourceIssue);
                        }
                    }

                    if (strVolIss != null && strVolIss.length() > 0) {
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                    }

                    if (rset.getString("SOURCE_TITLE") != null || rset.getString("ABBR_SOURCETITLE") != null) {

                        if (rset.getString("SOURCE_TITLE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, changeInfToSub(rset.getString("SOURCE_TITLE"))));
                        } else if (rset.getString("ABBR_SOURCETITLE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, changeInfToSub(rset.getString("ABBR_SOURCETITLE"))));
                        }

                        if (rset.getString("ISSUE_TITLE") != null) {
                            ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, changeInfToSub(rset.getString("ISSUE_TITLE"))));
                        }

                        if (rset.getString("VOLUME_TITLE") != null) {
                            ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, changeInfToSub(rset.getString("VOLUME_TITLE"))));
                        }
                    } else if (rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null && rset.getString("ISSUE_TITLE") != null) {

                        if (rset.getString("ISSUE_TITLE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, changeInfToSub(rset.getString("ISSUE_TITLE"))));
                        }

                    } else if (rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null && rset.getString("ISSUE_TITLE") == null)

                    {
                        if (rset.getString("PUBLISHER_NAME") != null) {
                            ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PUBLISHER_NAME")));
                        }
                    }
                }

                if (rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null && rset.getString("ISSUE_TITLE") == null
                    && rset.getString("PUBLISHER_NAME") == null) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                if (rset.getString("PUBLISHER_NAME") != null) {

                    ht.put(Keys.I_PUBLISHER, new XMLWrapper(Keys.I_PUBLISHER, rset.getString("PUBLISHER_NAME")));
                }

                String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SOURCE_PUBLICATIONDATE"));

                if ((rset.getString("SOURCE_TITLE") != null || rset.getString("ABBR_SOURCETITLE") != null || rset.getString("ISSUE_TITLE") != null || rset
                    .getString("PUBLISHER_NAME") != null) && (!strSD.equals(StringUtil.EMPTY_STRING))) {
                    ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));

                } else if (rset.getString("SOURCE_PUBLICATIONYEAR") != null) {
                    String strYR = rset.getString("SOURCE_PUBLICATIONYEAR");
                    if (rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null && rset.getString("ISSUE_TITLE") == null
                        && rset.getString("PUBLISHER_NAME") == null) {
                        // if ALL 4 are null, use the label 'Publication Date'
                        ht.put(Keys.PUBLICATION_DATE, new Year(rset.getString("SOURCE_PUBLICATIONYEAR"), perl));
                    } else {
                        // else just store the date
                        ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("SOURCE_PUBLICATIONYEAR"), perl));
                    }
                }

                if (rset.getString("REPORT_NUMBER") != null) {
                    ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, rset.getString("REPORT_NUMBER")));
                }

                if (rset.getString("VOLUME_TITLE") != null) {
                    ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, changeInfToSub(rset.getString("VOLUME_TITLE"))));
                }

                String language = rset.getString("ABSTRACT_LANGUAGE");
                if (language == null) {
                    language = rset.getString("CITATION_LANGUAGE");
                }
                if (language == null) {
                    language = rset.getString("TITLETEXT_LANGUAGE");
                }

                if (language != null) {
                    language = getLanguage(language);
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, language));
                }

                if (rset.getString("CONFERENCE_NAME") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("CONFERENCE_NAME")));
                }

                if ((rset.getString("CONFERENCE_NAME") != null) || (rset.getString("CONFERENCE_CITY") != null)) {

                    List<String> lstCF = new ArrayList<String>();
                    if (rset.getString("CONFERENCE_DATE") != null) {
                        lstCF.add(rset.getString("CONFERENCE_DATE"));
                    }

                    if (rset.getString("CONFERENCE_CITY") != null) {
                        lstCF.add(rset.getString("CONFERENCE_CITY"));
                    }

                    if (rset.getString("CONFERENCE_STATE") != null) {
                        lstCF.add(rset.getString("CONFERENCE_STATE"));
                    }

                    if (rset.getString("CONFERENCE_COUNTRY") != null) {
                        lstCF.add(getCountry(rset.getString("CONFERENCE_COUNTRY")));
                    }

                    ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, StringUtil.join(lstCF, ", ")));
                    lstCF = null;

                }

                // PL - PUBLISHER LOCATION
                if (rset.getString("PUBLISHER_NAME") != null) {
                    List<String> lstPL = new ArrayList<String>();
                    if (rset.getString("PUBLISHER_CITY") != null) {
                        lstPL.add(rset.getString("PUBLISHER_CITY"));
                    }

                    if (rset.getString("PUBLSHER_STATE") != null) {
                        lstPL.add(rset.getString("PUBLSHER_STATE"));
                    }

                    if (rset.getString("PUBLISHER_COUNTRY") != null) {
                        lstPL.add(getCountry(rset.getString("PUBLISHER_COUNTRY")));
                    }

                    ht.put(Keys.PUB_LOCATION, new XMLWrapper(Keys.PUB_LOCATION, StringUtil.join(lstPL, ", ")));
                }

                // CVS
                if ((rset.getString("DESCRIPTOR_MAINTERM_GDE") != null)) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(GEO_CONTROLLED_TERMS, setElementData(rset.getString("DESCRIPTOR_MAINTERM_GDE"))));
                }

                // RGI
                if ((rset.getString("DESCRIPTOR_MAINTERM_RGI") != null)) {
                    ht.put(Keys.REGION_CONTROLLED_TERMS,
                        new XMLMultiWrapper(Keys.REGION_CONTROLLED_TERMS, setElementData(rset.getString("DESCRIPTOR_MAINTERM_RGI"))));

                    // List mapcoords = getMappingCoordinates(rset.getString("DESCRIPTOR_MAINTERM_RGI"));
                    // ht.put(GRFDocBuilder.COORDINATES, new RectangleCoordinates((String[])mapcoords.toArray(new String[]{})));

                }

                // FLS
                if (rset.getString("DESCRIPTOR_MAINTERM_SPC") != null) {
                    String descriptorMaintermSpec = rset.getString("DESCRIPTOR_MAINTERM_SPC");
                    if (rset.getString("DESCRIPTOR_MAINTERM_SPC2") != null) {
                        descriptorMaintermSpec = descriptorMaintermSpec + rset.getString("DESCRIPTOR_MAINTERM_SPC2");
                    }
                    ht.put(Keys.SPECIES_TERMS, new XMLMultiWrapper(Keys.SPECIES_TERMS, setElementData(descriptorMaintermSpec)));
                }

                // NR
                if (rset.getString("REFFERENT_COUNT") != null) {
                    String strREFs = rset.getString("REFFERENT_COUNT");

                    if (perl.match("/(\\d+)/", strREFs)) {
                        ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, perl.group(0).toString()));
                    }
                }

                // CN
                if (rset.getString("CODEN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, rset.getString("CODEN")));
                }

                // BN
                if (rset.getString("ISBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(firstElement(rset.getString("ISBN"))));
                }

                // SN
                if (rset.getString("ISSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(getIssnWithDash(rset.getString("ISSN"))));
                }

                // E-ISSN
                if (rset.getString("E_ISSN") != null) {
                    ht.put(E_ISSN, new ISSN(E_ISSN, getIssnWithDash(rset.getString("E_ISSN"))));
                }

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                // AT
                if (rset.getString("ABSTRACT_SOURCE") != null) {
                    ht.put(Keys.ABSTRACT_TYPE, new XMLWrapper(Keys.ABSTRACT_TYPE, rset.getString("ABSTRACT_SOURCE")));
                }

                // CLS
                if (rset.getString("CLASSIFICATION") != null) {
                    ht.put(Keys.CLASS_CODES, new Classifications(GEO_CLASS_CODES, setElementData(rset.getString("CLASSIFICATION")), this.database));
                }

                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Abstract.ABSTRACT_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(true);
                eiDoc.setOutputKeys(ABSTRACT_KEYS);
                list.add(eiDoc);
                count++;
            }
        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {

            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        }

        return list;
    }

    /**
     * This method basically takes list Of DocIDs as Parameter This list of Docids use buildINString() method to build the required IN clause String.This is
     * appended to sql String The resultSet so obtained by executing the sql,is iterated, to build Detailed EIDoc objects,which are then added to EIdocumentList
     *
     * @param listOfDocIDs
     * @return EIDocumentList
     * @exception Exception
     */
    private List<EIDoc> loadDetailed(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Perl5Util perl = new Perl5Util();
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

        List<EIDoc> list = new ArrayList<EIDoc>();
        List<String> emailList = null;
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildINString(listOfDocIDs);
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryDetailed + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, GEO_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, GEO_TEXT_COPYRIGHT));

                if (rset.getString("ACCESSION_NUMBER") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ACCESSION_NUMBER")));
                }

                if (rset.getString("CITATION_TITLE") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("CITATION_TITLE")));
                }

                if (rset.getString("TRANSLATED_TITLE") != null) {
                    ht.put(Keys.TITLE_TRANSLATION, new XMLWrapper(Keys.TITLE_TRANSLATION, rset.getString("TRANSLATED_TITLE")));
                }

                // Author and Author affiliation
                if (rset.getString("AUTHORS") != null) {
                    String authorString = rset.getString("AUTHORS");
                    if (rset.getString("AUTHOR2") != null) {
                        authorString = authorString + rset.getString("AUTHOR2");
                    }
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(authorString, Keys.AUTHORS));

                    ht.put(Keys.AUTHORS, authors);

                    if (rset.getString("AUTHOR_AFFILIATION") != null) {
                        String affiliation = rset.getString("AUTHOR_AFFILIATION");
                        if (affiliation != null && affiliation.indexOf(",") > -1) {
                            affiliation = affiliation.replaceAll(",\\s*", ", ");
                        }
                        String affiliationAddress = rset.getString("AUTHOR_ADDRESS_PART");
                        String affiliationCity = rset.getString("AUTHOR_AFFILIATION_CITY");
                        String affiliationState = rset.getString("AUTHOR_AFFILIATION_STATE");
                        String affiliationCountry = rset.getString("AUTHOR_AFFILIATION_COUNTRY");

                        List<Affiliation> affiliationList = getContributorsAff(Keys.AUTHOR_AFFS, affiliation, affiliationAddress, affiliationCity,
                            affiliationState, affiliationCountry);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affiliationList));
                    }

                } else {
                    if (rset.getString("SOURCE_EDITOR") != null) {
                        String strED = rset.getString("SOURCE_EDITOR");
                        Contributors editors = new Contributors(Keys.EDITORS, getEditors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);
                    }
                }

                // CAUS

                if (rset.getString("CORRESPONDENCE_PERSON") != null) {
                    String personString = rset.getString("CORRESPONDENCE_PERSON");
                    Contributors persons = new Contributors(Keys.CORRESPONDENCE_PERSON, getContributors(personString, Keys.CORRESPONDENCE_PERSON));

                    ht.put(Keys.CORRESPONDENCE_PERSON, persons);

                    if (rset.getString("CORRESPONDENCE_EMAIL") != null) {
                        emailList = getEmailList(rset.getString("CORRESPONDENCE_EMAIL"));
                        persons.setEmails(emailList);
                    }
                }

                // ST
                if (rset.getString("SOURCE_TITLE") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("SOURCE_TITLE")));
                }

                // SOURCE_COUNTRY
                if (rset.getString("SOURCE_COUNTRY") != null) {
                    ht.put(Keys.SOURCE_COUNTRY, new XMLWrapper(Keys.SOURCE_COUNTRY, getCountry(rset.getString("SOURCE_COUNTRY"))));
                }

                // SE
                if (rset.getString("ABBR_SOURCETITLE") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("ABBR_SOURCETITLE")));
                }

                // VO
                if (rset.getString("SOURCE_VOLUME") != null) {
                    String strVol = rset.getString("SOURCE_VOLUME");
                    ht.put(Keys.VOLUME, new Volume(strVol, perl));
                }

                // ISS
                if (rset.getString("SOURCE_ISSUE") != null) {
                    String strIss = rset.getString("SOURCE_ISSUE");
                    ht.put(Keys.ISSUE, new Issue(strIss, perl));
                }

                // SD
                if (rset.getString("SOURCE_PUBLICATIONDATE") != null) {
                    ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("SOURCE_PUBLICATIONDATE")));
                }

                // MT
                if (rset.getString("ISSUE_TITLE") != null) {
                    ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, rset.getString("ISSUE_TITLE")));
                }

                // VT
                if (rset.getString("VOLUME_TITLE") != null) {
                    ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VOLUME_TITLE")));
                }

                String year = getYear(rset.getString("SOURCE_PUBLICATIONYEAR"), rset.getString("SOURCE_PUBLICATIONDATE"), rset.getString("CREATED_DATE"),
                    rset.getString("SORT_DATE"));

                if (year != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(year, perl));
                }

                // PA
                if (rset.getString("REPORT_NUMBER") != null) {
                    // REPORT_NUMBER will cause Report Number label in XSL
                    ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, rset.getString("REPORT_NUMBER")));
                }

                // PP
                String strPages = getPage(rset.getString("SOURCE_PAGERANGE"), rset.getString("PAGES"), rset.getString("PAGECOUNT"));
                if (strPages != null && strPages.length() > 0) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));
                }

                // LA
                String language = rset.getString("CITATION_LANGUAGE");
                if (language == null) {
                    language = rset.getString("ABSTRACT_LANGUAGE");
                }
                if (language == null) {
                    language = rset.getString("TITLETEXT_LANGUAGE");
                }
                if (language != null) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, getLanguage(language)));
                }

                // ISSN
                if (rset.getString("ISSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(getIssnWithDash(rset.getString("ISSN"))));
                }

                // E-ISSN
                if (rset.getString("E_ISSN") != null) {
                    ht.put(E_ISSN, new ISSN(E_ISSN, getIssnWithDash(rset.getString("E_ISSN"))));
                }

                // CN
                if (rset.getString("CODEN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, rset.getString("CODEN")));
                }

                // BN
                if (rset.getString("ISBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(firstElement(rset.getString("ISBN"))));
                }

                // DT
                if (rset.getString("SOURCE_TYPE") != null) {
                    ht.put(Keys.DOC_TYPE,
                        new XMLWrapper(Keys.DOC_TYPE, replaceDTNullWithEmptyString(DocumentTypeConverter.getGeoDocumentType(rset.getString("SOURCE_TYPE")))));
                }

                // CF
                if (rset.getString("CONFERENCE_NAME") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("CONFERENCE_NAME")));
                }

                // MD
                if (rset.getString("CONFERENCE_DATE") != null) {
                    ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, rset.getString("CONFERENCE_DATE")));
                }

                // ML
                List<String> lstMeetLoc = new ArrayList<String>();
                if (rset.getString("CONFERENCE_CITY") != null) {
                    lstMeetLoc.add(rset.getString("CONFERENCE_CITY"));
                }
                if (rset.getString("CONFERENCE_STATE") != null) {
                    lstMeetLoc.add(rset.getString("CONFERENCE_STATE"));
                }

                if (rset.getString("CONFERENCE_COUNTRY") != null) {
                    lstMeetLoc.add(getCountry(rset.getString("CONFERENCE_COUNTRY")));
                }

                if (lstMeetLoc.size() > 0) {
                    ht.put(Keys.MEETING_LOCATION, new XMLWrapper(Keys.MEETING_LOCATION, StringUtil.join(lstMeetLoc, ", ")));
                }

                lstMeetLoc = null;
                // CC
                if (rset.getString("CONFERENCE_CODE") != null) {
                    ht.put(Keys.CONF_CODE, new XMLWrapper(Keys.CONF_CODE, rset.getString("CONFERENCE_CODE")));
                }

                // SP
                if (rset.getString("CONFERENCE_SPONSORS") != null) {
                    ht.put(Keys.SPONSOR, new XMLWrapper(Keys.SPONSOR, rset.getString("CONFERENCE_SPONSORS")));
                }

                // PN, PL
                List<String> lstTokens = new ArrayList<String>();
                if (rset.getString("PUBLISHER_NAME") != null) {
                    lstTokens.add((String) rset.getString("PUBLISHER_NAME"));
                }
                if (rset.getString("PUBLISHER_CITY") != null) {
                    lstTokens.add((String) rset.getString("PUBLISHER_CITY"));
                }
                if (rset.getString("PUBLSHER_STATE") != null) {
                    lstTokens.add((String) rset.getString("PUBLSHER_STATE"));
                }

                if (rset.getString("PUBLISHER_COUNTRY") != null) {
                    lstTokens.add(getCountry((String) rset.getString("PUBLISHER_COUNTRY")));
                }

                if (lstTokens.size() > 0) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.join(lstTokens, ", ")));
                }
                lstTokens = null;

                // AB

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                // AT
                if (rset.getString("ABSTRACT_SOURCE") != null) {
                    ht.put(Keys.ABSTRACT_TYPE, new XMLWrapper(Keys.ABSTRACT_TYPE, rset.getString("ABSTRACT_SOURCE")));
                }

                // NR
                if (rset.getString("REFFERENT_COUNT") != null) {
                    String strREFs = rset.getString("REFFERENT_COUNT");
                    if (perl.match("/(\\d+)/", strREFs)) {
                        ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, perl.group(0).toString()));
                    }
                }

                // CLASSIFICATION_SUBJECT
                String classificationSubject = rset.getString("CLASSIFICATION_SUBJECT");

                if (classificationSubject != null) {
                    ht.put(Keys.CLASSIFICATION_SUBJECT, new XMLWrapper(Keys.CLASSIFICATION_SUBJECT, classificationSubject));
                }

                // CVS
                if ((rset.getString("DESCRIPTOR_MAINTERM_GDE") != null)) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(Keys.INDEX_TERM, setElementData(rset.getString("DESCRIPTOR_MAINTERM_GDE"))));
                }

                // FLS
                if (rset.getString("DESCRIPTOR_MAINTERM_SPC") != null) {
                    String descriptorMaintermSpec = rset.getString("DESCRIPTOR_MAINTERM_SPC");
                    if (rset.getString("DESCRIPTOR_MAINTERM_SPC2") != null) {
                        descriptorMaintermSpec = descriptorMaintermSpec + rset.getString("DESCRIPTOR_MAINTERM_SPC2");
                    }
                    ht.put(Keys.SPECIES_TERMS, new XMLMultiWrapper(Keys.SPECIES_TERMS, setElementData(descriptorMaintermSpec)));
                }

                // RGI
                if ((rset.getString("DESCRIPTOR_MAINTERM_RGI") != null)) {
                    ht.put(Keys.REGION_CONTROLLED_TERMS,
                        new XMLMultiWrapper(Keys.REGION_CONTROLLED_TERMS, setElementData(rset.getString("DESCRIPTOR_MAINTERM_RGI"))));

                    // List mapcoords = getMappingCoordinates(rset.getString("DESCRIPTOR_MAINTERM_RGI"));
                    // ht.put(GRFDocBuilder.COORDINATES, new RectangleCoordinates((String[])mapcoords.toArray(new String[]{})));
                }

                // CLS
                if (rset.getString("CLASSIFICATION") != null) {
                    ht.put(Keys.CLASS_CODES, new Classifications(GEO_CLASS_CODES, setElementData(rset.getString("CLASSIFICATION")), this.database));
                }

                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
                }

                // Article Number
                if (rset.getString("ARTICLE_NUMBER") != null) {
                    ht.put(Keys.ARTICLE_NUMBER, new XMLWrapper(Keys.ARTICLE_NUMBER, rset.getString("ARTICLE_NUMBER")));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Detail.FULLDOC_FORMAT);
                eiDoc.exportLabels(true);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(DETAILED_KEYS);
                list.add(eiDoc);
                count++;
            }

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        }

        return list;
    }

    private List<EIDoc> loadRIS(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();

        List<EIDoc> list = new ArrayList<EIDoc>();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildINString(listOfDocIDs);

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryDetailed + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                String strDocType = null;

                if (rset.getString("SOURCE_TYPE") != null) {
                    strDocType = replaceDTNullWithEmptyString(DocumentTypeConverter.getGeoDocumentType(rset.getString("SOURCE_TYPE")));
                }

                ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, replaceTYwithRIScode(strDocType)));
                ht.put(Keys.BIB_TY, new XMLWrapper(Keys.BIB_TY, replaceTYwithBIBcode(strDocType)));
                // LA
                String language = rset.getString("CITATION_LANGUAGE");
                if (language == null) {
                    language = rset.getString("ABSTRACT_LANGUAGE");
                }
                if (language == null) {
                    language = rset.getString("TITLETEXT_LANGUAGE");
                }

                if (language != null) {
                    ht.put(Keys.RIS_LA, new XMLWrapper(Keys.RIS_LA, getLanguage(language)));
                }

                // COPYRIGHT
                ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, rset.getString("COPYRIGHT")));

                // TI
                if (rset.getString("CITATION_TITLE") != null) {
                    ht.put(Keys.RIS_TI, new XMLWrapper(Keys.RIS_TI, rset.getString("CITATION_TITLE")));
                }

                // RIS_T1
                if (rset.getString("TRANSLATED_TITLE") != null) {
                    ht.put(Keys.RIS_T1, new XMLWrapper(Keys.RIS_T1, rset.getString("TRANSLATED_TITLE")));
                }

                // RIS_BT
                if (rset.getString("ISSUE_TITLE") != null) {
                    ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, rset.getString("ISSUE_TITLE")));
                }

                // RIS_JO or RIS_T3
                if (strDocType.indexOf("JA") > -1) {
                    if (rset.getString("SOURCE_TITLE") != null) {
                        ht.put(Keys.RIS_JO, new XMLWrapper(Keys.RIS_JO, rset.getString("SOURCE_TITLE")));
                    }
                } else {
                    if (rset.getString("SOURCE_TITLE") != null) {
                        ht.put(Keys.RIS_T3, new XMLWrapper(Keys.RIS_T3, rset.getString("SOURCE_TITLE")));
                    }
                }

                if (rset.getString("AUTHORS") != null) {
                    String authorString = rset.getString("AUTHORS");
                    if (rset.getString("AUTHOR2") != null) {
                        authorString = authorString + rset.getString("AUTHOR2");
                    }
                    Contributors authors = new Contributors(Keys.RIS_AUS, getContributors(authorString, Keys.RIS_AUS));

                    ht.put(Keys.RIS_AUS, authors);
                    if (rset.getString("AUTHOR_AFFILIATION") != null) {
                        String affiliation = rset.getString("AUTHOR_AFFILIATION");
                        if (affiliation != null && affiliation.indexOf(",") > -1) {
                            affiliation = affiliation.replaceAll(",\\s*", ", ");
                        }
                        String affiliationAddress = rset.getString("AUTHOR_ADDRESS_PART");
                        String affiliationCity = rset.getString("AUTHOR_AFFILIATION_CITY");
                        String affiliationState = rset.getString("AUTHOR_AFFILIATION_STATE");
                        String affiliationCountry = rset.getString("AUTHOR_AFFILIATION_COUNTRY");
                        List<Affiliation> affiliationList = getContributorsAff(Keys.RIS_AD, affiliation, affiliationAddress, affiliationCity, affiliationState,
                            affiliationCountry);
                        ht.put(Keys.RIS_AD, new Affiliations(Keys.RIS_AD, affiliationList));
                    }

                } else {
                    if (rset.getString("SOURCE_EDITOR") != null) {
                        String strED = rset.getString("SOURCE_EDITOR");
                        Contributors editors = new Contributors(Keys.RIS_EDS, getEditors(strED, Keys.RIS_EDS));
                        ht.put(Keys.RIS_EDS, editors);
                    }
                }

                if (rset.getString("SOURCE_VOLUME") != null) {
                    String strVol = rset.getString("SOURCE_VOLUME");
                    if (strVol == null || strVol.equals("QQ")) {
                        strVol = "";
                    }
                    ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, strVol));
                }

                if (rset.getString("SOURCE_ISSUE") != null) {
                    String strIss = rset.getString("SOURCE_ISSUE");
                    if (strIss == null || strIss.equals("QQ")) {
                        strIss = "";
                    }
                    ht.put(Keys.RIS_IS, new Issue(strIss, perl));
                }

                String year = getYear(rset.getString("SOURCE_PUBLICATIONYEAR"), rset.getString("SOURCE_PUBLICATIONDATE"), rset.getString("CREATED_DATE"),
                    rset.getString("SORT_DATE"));
                if (year != null) {
                    ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, year, perl));
                }

                if (rset.getString("ACCESSION_NUMBER") != null) {
                    ht.put(Keys.RIS_AN, new XMLWrapper(Keys.RIS_AN, rset.getString("ACCESSION_NUMBER")));
                }

                // PP
                String strPages = getPage(rset.getString("SOURCE_PAGERANGE"), rset.getString("PAGES"), rset.getString("PAGECOUNT"));

                if (strPages != null && strPages.length() > 0) {
                    if (strPages.indexOf("p") < 0 && strPages.indexOf("P") < 0) {
                        strPages = "p " + strPages.trim();
                    }
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));
                }

                // Strip out and store the start page and end page
                if (strPages != null) {
                    if (perl.match("/(\\d+)[^\\d](\\d+)/", strPages)) {
                        if (perl.match("/(\\d+)/", strPages)) {
                            ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, perl.group(0).toString()));
                            if (perl.match("/(\\d+)/", perl.postMatch())) {
                                ht.put(Keys.RIS_EP, new XMLWrapper(Keys.RIS_EP, perl.group(0).toString()));
                            }
                        }
                    } else {
                        strPages = perl.substitute("s/[^\\d-,]//g", strPages);
                        ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, strPages));
                    }
                }

                if (rset.getString("ISSN") != null) {
                    ht.put(Keys.RIS_SN, new ISSN(rset.getString("ISSN")));
                }

                if (rset.getString("ISBN") != null) {
                    ht.put(Keys.RIS_S1, new ISBN(firstElement(rset.getString("ISBN"))));
                }

                // CF
                if (rset.getString("CONFERENCE_NAME") != null) {
                    ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, rset.getString("CONFERENCE_NAME")));
                }
                // MD
                if (rset.getString("CONFERENCE_DATE") != null) {
                    ht.put(Keys.RIS_MD, new XMLWrapper(Keys.RIS_MD, rset.getString("CONFERENCE_DATE")));
                }
                // ML

                List<String> lstMeetLoc = new ArrayList<String>();
                if (rset.getString("CONFERENCE_LOCATION") != null) {
                    lstMeetLoc.add(rset.getString("CONFERENCE_LOCATION"));
                } else {
                    if (rset.getString("CONFERENCE_CITY") != null) {
                        lstMeetLoc.add(rset.getString("CONFERENCE_CITY"));
                    }

                    if (rset.getString("CONFERENCE_STATE") != null) {
                        lstMeetLoc.add(rset.getString("CONFERENCE_STATE"));
                    }

                    if (rset.getString("CONFERENCE_COUNTRY") != null) {
                        lstMeetLoc.add(getCountry(rset.getString("CONFERENCE_COUNTRY")));
                    }
                }
                if (lstMeetLoc.size() > 0) {
                    ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, StringUtil.join(lstMeetLoc, ", ")));
                }

                // PN, PL
                List<String> lstTokens = new ArrayList<String>();
                if (rset.getString("PUBLISHER_NAME") != null) {
                    lstTokens.add((String) rset.getString("PUBLISHER_NAME"));
                }

                if (rset.getString("PUBLISHER_CITY") != null) {
                    lstTokens.add((String) rset.getString("PUBLISHER_CITY"));
                }

                if (rset.getString("PUBLSHER_STATE") != null) {
                    lstTokens.add((String) rset.getString("PUBLSHER_STATE"));
                }

                if (rset.getString("PUBLISHER_COUNTRY") != null) {
                    lstTokens.add(getCountry((String) rset.getString("PUBLISHER_COUNTRY")));
                }

                if (lstTokens.size() > 0) {
                    ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, StringUtil.join(lstTokens, ", ")));
                    lstTokens = null;
                }

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, abs));
                }

                // CVS
                if (rset.getString("DESCRIPTOR_MAINTERM_GDE") != null) {
                    ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(rset.getString("DESCRIPTOR_MAINTERM_GDE"))));
                }

                // FLS
                if (rset.getString("DESCRIPTOR_MAINTERM_SPC") != null) {
                    String descriptorMaintermSpec = rset.getString("DESCRIPTOR_MAINTERM_SPC");
                    if (rset.getString("DESCRIPTOR_MAINTERM_SPC2") != null) {
                        descriptorMaintermSpec = descriptorMaintermSpec + rset.getString("DESCRIPTOR_MAINTERM_SPC2");
                    }
                    ht.put(Keys.RIS_FLS, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(descriptorMaintermSpec)));
                }

                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.RIS_DO, new XMLWrapper(Keys.RIS_DO, rset.getString("DOI")));
                }

                EIDoc eiDoc = new EIDoc(did, ht, RIS.RIS_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(RIS_KEYS);
                list.add(eiDoc);
                count++;
            }

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        }

        return list;
    } // loadRIS

    /**
     * This method basically takes list Of DocIDs as Parameter This list of Docids use buildINString() method to build the required IN clause String.This is
     * appended to sql String The resultSet so obtained by executing the sql,is iterated, to build EIDoc objects,which are then added to EIdocumentList
     *
     * @param listOfDocIDs
     * @return EIDocumentList
     * @exception Exception
     */
    private List<EIDoc> loadCitations(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();
        List<EIDoc> list = new ArrayList<EIDoc>();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildINString(listOfDocIDs);
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryCitation + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, GEO_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, GEO_TEXT_COPYRIGHT));

                // PP
                String strPages = getPage(rset.getString("SOURCE_PAGERANGE"), rset.getString("PAGES"), rset.getString("PAGECOUNT"));

                if (strPages != null && strPages.length() > 0) {
                    if (strPages.indexOf("p") < 0 && strPages.indexOf("P") < 0) {
                        strPages = "p " + strPages.trim();
                    }

                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));
                }
                if (rset.getString("ARTICLE_NUMBER") != null) {
                    ht.put(Keys.ARTICLE_NUMBER, new XMLWrapper(Keys.ARTICLE_NUMBER, rset.getString("ARTICLE_NUMBER")));
                }

                if (rset.getString("ISSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("ISSN")));
                }

                // E-ISSN
                if (rset.getString("E_ISSN") != null) {
                    ht.put(Keys.E_ISSN, new ISSN(getIssnWithDash(rset.getString("E_ISSN"))));
                }

                String strTitle = rset.getString("TRANSLATED_TITLE");
                String strCTitle = rset.getString("CITATION_TITLE");
                if (strTitle != null && strCTitle != null) {
                    strCTitle = strCTitle.trim();
                    if (strCTitle.charAt(0) != '(') {
                        strTitle = strTitle.concat(" (").concat(strCTitle).concat(")");
                    } else {
                        strTitle = strTitle.concat(" ").concat(strCTitle);
                    }
                } else if (strCTitle != null) {
                    strTitle = strCTitle;
                }

                if (strTitle != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, changeInfToSub(strTitle)));
                }

                // AUS or EDS

                if (rset.getString("AUTHORS") != null) {
                    String authorString = rset.getString("AUTHORS");
                    if (rset.getString("AUTHOR2") != null) {
                        authorString = authorString + rset.getString("AUTHOR2");
                    }
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(authorString, Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);
                    if (rset.getString("AUTHOR_AFFILIATION") != null) {
                        String affiliation = rset.getString("AUTHOR_AFFILIATION");
                        if (affiliation != null && affiliation.indexOf(",") > -1) {
                            affiliation = affiliation.replaceAll(",\\s*", ", ");
                        }

                        List<Affiliation> affiliationList = getContributorsAff(Keys.AUTHOR_AFFS, affiliation, null, null, null, null);
                        Affiliation affil = (Affiliation) affiliationList.get(0);
                        authors.setFirstAffiliation(affil);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }

                } else {
                    if (rset.getString("SOURCE_EDITOR") != null) {
                        String strED = rset.getString("SOURCE_EDITOR");
                        Contributors editors = new Contributors(Keys.EDITORS, getEditors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);
                    }

                }

                // SO
                if (rset.getString("SOURCE_TITLE") != null || rset.getString("ABBR_SOURCETITLE") != null || rset.getString("ISSUE_TITLE") != null
                    || rset.getString("PUBLISHER_NAME") != null) {
                    String strVolIss = StringUtil.EMPTY_STRING;

                    // VO - VOL and ISSUE Combined by ', '
                    if (rset.getString("SOURCE_VOLUME") != null) {
                        strVolIss = strVolIss.concat(rset.getString("SOURCE_VOLUME"));
                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, rset.getString("SOURCE_VOLUME"), perl));
                    }

                    if (rset.getString("SOURCE_ISSUE") != null) {
                        ht.put(Keys.ISSUE, new Issue(rset.getString("SOURCE_ISSUE"), perl));

                        if (strVolIss != null && strVolIss.length() > 0) {
                            strVolIss = strVolIss.concat(", ").concat(rset.getString("SOURCE_ISSUE"));
                        } else {
                            strVolIss = rset.getString("SOURCE_ISSUE");
                        }
                    }

                    if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                    }

                    if (rset.getString("SOURCE_TITLE") != null || rset.getString("ABBR_SOURCETITLE") != null) {
                        if (rset.getString("SOURCE_TITLE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("SOURCE_TITLE")));
                        } else if (rset.getString("ABBR_SOURCETITLE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("ABBR_SOURCETITLE")));
                        }

                        // an MT or VT can accompany the ST (or SE)
                        if (rset.getString("ISSUE_TITLE") != null) {
                            ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, rset.getString("ISSUE_TITLE")));
                        }

                        if (rset.getString("VOLUME_TITLE") != null) {
                            ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VOLUME_TITLE")));
                        }
                    } else if ((rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null)
                        && (rset.getString("ISSUE_TITLE") != null)) {

                        if (rset.getString("ISSUE_TITLE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("ISSUE_TITLE")));
                        }
                    } else if (rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null && rset.getString("ISSUE_TITLE") == null) {
                        if (rset.getString("PUBLISHER_NAME") != null) {
                            ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PUBLISHER_NAME")));
                        }
                    } else {

                    }
                }

                if (rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null && rset.getString("ISSUE_TITLE") == null
                    && rset.getString("PUBLISHER_NAME") == null) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                // SD
                String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SOURCE_PUBLICATIONDATE"));
                if ((rset.getString("SOURCE_TITLE") != null || rset.getString("ABBR_SOURCETITLE") != null || rset.getString("ISSUE_TITLE") != null || rset
                    .getString("PUBLISHER_NAME") != null) && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                    String year = getYear(rset.getString("SOURCE_PUBLICATIONYEAR"), rset.getString("SOURCE_PUBLICATIONDATE"), rset.getString("CREATED_DATE"),
                        rset.getString("SORT_DATE"));

                    if (year != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("SOURCE_PUBLICATIONYEAR"));
                        strSD = perl.substitute("s/".concat(strYR).concat("$//"), strSD);

                        if (strSD != null && strSD.length() > 0) {
                            strSD = strSD.trim().concat(", ").concat(strYR);
                        } else {
                            strSD = strYR;
                        }
                    }

                    ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));

                } else if (rset.getString("SOURCE_PUBLICATIONYEAR") != null) {
                    String strYR = rset.getString("SOURCE_PUBLICATIONYEAR");
                    if (rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null && rset.getString("ISSUE_TITLE") == null
                        && rset.getString("PUBLISHER_NAME") == null) {
                        // if ALL 4 are null, use the label 'Publication Date'
                        ht.put(Keys.PUBLICATION_DATE, new Year(rset.getString("SOURCE_PUBLICATIONYEAR"), perl));
                    } else {
                        // else just store the date
                        ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("SOURCE_PUBLICATIONYEAR"), perl));
                    }
                }

                // PA
                if (rset.getString("REPORT_NUMBER") != null) {
                    ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, rset.getString("REPORT_NUMBER")));
                }

                // VT
                if (rset.getString("VOLUME_TITLE") != null) {
                    ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VOLUME_TITLE")));
                }

                // LA
                String language = rset.getString("CITATION_LANGUAGE");
                if (language == null) {
                    language = rset.getString("ABSTRACT_LANGUAGE");
                }
                if (language == null) {
                    language = rset.getString("TITLETEXT_LANGUAGE");
                }
                if (language != null) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, getLanguage(language)));
                }

                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
                }

                // YR

                if (rset.getString("SOURCE_PUBLICATIONYEAR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("SOURCE_PUBLICATIONYEAR"), perl));
                }

                // CN
                if (rset.getString("CODEN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, rset.getString("CODEN")));
                }
                // ST

                if (rset.getString("SOURCE_TITLE") != null) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("SOURCE_TITLE")));
                }
                // SE

                if (rset.getString("ABBR_SOURCETITLE") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("ABBR_SOURCETITLE")));
                }

                // ST
                if (rset.getString("SOURCE_TITLE") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("SOURCE_TITLE")));
                }
                // BN
                if (rset.getString("ISBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(firstElement(rset.getString("ISBN"))));
                }

                // CF
                if (rset.getString("CONFERENCE_NAME") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("CONFERENCE_NAME")));
                }

                // EX
                if (rset.getString("ACCESSION_NUMBER") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ACCESSION_NUMBER")));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(CITATION_KEYS);
                list.add(eiDoc);
                count++;
            }
        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                    rset = null;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        }

        return list;
    }

    /**
     * This method basically takes list Of DocIDs as Parameter This list of Docids use buildINString() method to build the required IN clause String.This is
     * appended to sql String The resultSet so obtained by executing the sql,is iterated, to build EIDoc objects,which are then added to EIdocumentList
     *
     * @param listOfDocIDs
     * @return EIDocumentList
     * @exception Exception
     */
    private List<EIDoc> loadXMLCitations(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();
        List<EIDoc> list = new ArrayList<EIDoc>();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildINString(listOfDocIDs);
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryXMLCitation + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, GEO_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, GEO_TEXT_COPYRIGHT));

                // Always needed for IVIP
                if (rset.getString("ISSN") != null) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("ISSN")));
                }

                String strTitle = rset.getString("TRANSLATED_TITLE");
                String strCTitle = rset.getString("CITATION_TITLE");
                if (strTitle != null && strCTitle != null) {
                    strCTitle = strCTitle.trim();
                    if (strCTitle.charAt(0) != '(') {
                        strTitle = strTitle.concat(" (").concat(strCTitle).concat(")");
                    } else {
                        strTitle = strTitle.concat(" ").concat(strCTitle);
                    }
                } else if (strCTitle != null) {
                    strTitle = strCTitle;
                }

                if (strTitle != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, changeInfToSub(strTitle)));
                }

                // ACCESSION NUMBER
                if (rset.getString("ACCESSION_NUMBER") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("ACCESSION_NUMBER")));
                }

                // AUS or EDS
                if (rset.getString("AUTHORS") != null) {
                    String authorString = rset.getString("AUTHORS");
                    if (rset.getString("AUTHOR2") != null) {
                        authorString = authorString + rset.getString("AUTHOR2");
                    }
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(authorString, Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);
                    if (rset.getString("AUTHOR_AFFILIATION") != null) {
                        String affiliation = rset.getString("AUTHOR_AFFILIATION");
                        if (affiliation != null && affiliation.indexOf(",") > -1) {
                            affiliation = affiliation.replaceAll(",\\s*", ", ");
                        }
                        String affiliationAddress = rset.getString("AUTHOR_ADDRESS_PART");
                        String affiliationCity = rset.getString("AUTHOR_AFFILIATION_CITY");
                        String affiliationState = rset.getString("AUTHOR_AFFILIATION_STATE");
                        String affiliationCountry = rset.getString("AUTHOR_AFFILIATION_COUNTRY");

                        List<Affiliation> affiliationList = getContributorsAff(Keys.AUTHOR_AFFS, affiliation, affiliationAddress, affiliationCity,
                            affiliationState, affiliationCountry);
                        Affiliation affil = (Affiliation) affiliationList.get(0);
                        authors.setFirstAffiliation(affil);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }

                } else {
                    if (rset.getString("SOURCE_EDITOR") != null) {
                        String strED = rset.getString("SOURCE_EDITOR");
                        Contributors editors = new Contributors(Keys.EDITORS, getEditors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);
                    }
                }

                // SO
                if (rset.getString("SOURCE_TITLE") != null || rset.getString("ABBR_SOURCETITLE") != null || rset.getString("ISSUE_TITLE") != null
                    || rset.getString("PUBLISHER_NAME") != null) {

                    // VO - VOL and ISSUE Combined by ', '
                    // add 'v' or 'n'

                    if (rset.getString("SOURCE_VOLUME") != null) {
                        ht.put(Keys.VOLUME, new Volume(rset.getString("SOURCE_VOLUME").replace('n', ' ').replace('v', ' '), perl));
                    }

                    if (rset.getString("SOURCE_ISSUE") != null) {
                        ht.put(Keys.ISSUE, new Issue(rset.getString("SOURCE_ISSUE").replace('n', ' '), perl));
                    }

                    if (rset.getString("SOURCE_TITLE") != null || rset.getString("ABBR_SOURCETITLE") != null) {
                        if (rset.getString("SOURCE_TITLE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("SOURCE_TITLE")));
                        } else if (rset.getString("ABBR_SOURCETITLE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("ABBR_SOURCETITLE")));
                        }
                        // an MT or VT can accompany the ST (or SE)
                        if (rset.getString("ISSUE_TITLE") != null) {
                            ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, rset.getString("ISSUE_TITLE")));
                        }
                        if (rset.getString("VOLUME_TITLE") != null) {
                            ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VOLUME_TITLE")));
                        }
                    } else if ((rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null) && rset.getString("ISSUE_TITLE") != null)

                    {
                        if (rset.getString("ISSUE_TITLE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("ISSUE_TITLE")));
                        }

                    } else if (rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null && rset.getString("ISSUE_TITLE") == null)

                    {
                        if (rset.getString("PUBLISHER_NAME") != null) {
                            ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PUBLISHER_NAME")));
                        }
                    } else {
                    }
                }
                if (rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null && rset.getString("ISSUE_TITLE") == null
                    && rset.getString("PUBLISHER_NAME") == null) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                // SD
                String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SOURCE_PUBLICATIONDATE"));
                if ((rset.getString("SOURCE_TITLE") != null || rset.getString("ABBR_SOURCETITLE") != null || rset.getString("ISSUE_TITLE") != null || rset
                    .getString("PUBLISHER_NAME") != null) && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                    if (rset.getString("SOURCE_PUBLICATIONYEAR") != null) {
                        String strYR = rset.getString("SOURCE_PUBLICATIONYEAR");
                        strSD = perl.substitute("s/".concat(strYR).concat("$//"), strSD);
                        strSD = strSD.trim().concat(", ").concat(strYR);
                    }
                    ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));

                } else if (rset.getString("SOURCE_PUBLICATIONYEAR") != null) {
                    String strYR = rset.getString("SOURCE_PUBLICATIONYEAR");
                    if (rset.getString("SOURCE_TITLE") == null && rset.getString("ABBR_SOURCETITLE") == null && rset.getString("ISSUE_TITLE") == null
                        && rset.getString("PUBLISHER_NAME") == null) {
                        // if ALL 4 are null, use the label 'Publication Date'
                        ht.put(Keys.PUBLICATION_DATE, new Year(strYR, perl));
                    } else {
                        // else just store the date
                        ht.put(Keys.PUBLICATION_YEAR, new Year(strYR, perl));
                    }
                }

                // PA
                if (rset.getString("REPORT_NUMBER") != null) {
                    ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, (rset.getString("REPORT_NUMBER"))));
                }

                // Page
                String strPages = getPage(rset.getString("SOURCE_PAGERANGE"), rset.getString("PAGES"), rset.getString("PAGECOUNT"));

                if (strPages != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));
                }

                if (rset.getString("PAGECOUNT") != null) {
                    String pageCount = rset.getString("PAGECOUNT");
                    ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, pageCount));
                }

                if (rset.getString("ARTICLE_NUMBER") != null) {
                    ht.put(Keys.ARTICLE_NUMBER, new XMLWrapper(Keys.ARTICLE_NUMBER, rset.getString("ARTICLE_NUMBER")));
                }
                // VT
                if (rset.getString("VOLUME_TITLE") != null) {
                    ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VOLUME_TITLE")));
                }

                // CVS
                if ((rset.getString("DESCRIPTOR_MAINTERM_GDE") != null)) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(Keys.INDEX_TERM, setElementData(rset.getString("DESCRIPTOR_MAINTERM_GDE"))));
                }

                // LA
                String language = rset.getString("CITATION_LANGUAGE");
                if (language == null) {
                    language = rset.getString("ABSTRACT_LANGUAGE");
                }
                if (language == null) {
                    language = rset.getString("TITLETEXT_LANGUAGE");
                }
                if (language != null) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, getLanguage(language)));
                }

                // DO
                if (rset.getString("DOI") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DOI")));
                }

                // BN
                if (rset.getString("ISBN") != null) {
                    ht.put(Keys.ISBN, new ISBN(firstElement(rset.getString("ISBN"))));
                }

                if (rset.getString("PUBLISHER_NAME") != null) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PUBLISHER_NAME")));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.XMLCITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(XML_KEYS);
                list.add(eiDoc);
                count++;
            }

        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } finally {
            if (rset != null) {
                try {
                    rset.close();
                    rset = null;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                    stmt = null;
                } catch (Exception sqle) {
                    sqle.printStackTrace();
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        }

        return list;
    }

    private String getCountry(String code) {

        String countryName = Country.formatCountry(code);
        StringBuffer countryBuffer = new StringBuffer();
        if (countryName != null && countryName.length() > 0) {
            String[] countryArray = null;
            if (countryName.indexOf(" ") > 0) {
                countryArray = countryName.split(" ");
            } else {
                countryArray = new String[1];
                countryArray[0] = countryName;
            }
            for (int i = 0; i < countryArray.length; i++) {
                countryBuffer.append(" " + countryArray[i].substring(0, 1).toUpperCase() + countryArray[i].substring(1));
            }
        }
        return countryBuffer.toString().trim();

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
                    languageName.append(lName);
                else
                    languageName.append(sCode);

                if (tokens.hasMoreTokens()) {
                    languageName.append(", ");
                }

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

    private String removeID(String inputString) {
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

    public List<Contributor> getContributors(String strAuthors, Key key) {

        List<Contributor> list = new ArrayList<Contributor>();
        String[] authorGroup = null;
        if (strAuthors.indexOf(GROUPDELIMITER) > 0) {
            authorGroup = strAuthors.split(GROUPDELIMITER);
        } else {
            authorGroup = new String[1];
            authorGroup[0] = strAuthors;
        }
        for (int k = 0; k < authorGroup.length; k++) {
            if (authorGroup[k].indexOf(IDDELIMITER) > -1) {
                int i = authorGroup[k].indexOf(IDDELIMITER);
                String auContent = authorGroup[k].substring(i + 1);

                StringTokenizer strToken = new StringTokenizer(auContent, AUDELIMITER);
                String au;
                while (strToken.hasMoreTokens()) {
                    au = strToken.nextToken().trim();
                    list.add(new Contributor(key, au));

                }
            } else {
                list.add(new Contributor(key, authorGroup[k]));
            }
        }

        return list;
    }

    public List<Contributor> getEditors(String strAuthors, Key key) {
        List<Contributor> list = new ArrayList<Contributor>();
        StringTokenizer strToken = new StringTokenizer(strAuthors, AUDELIMITER);
        String au;
        while (strToken.hasMoreTokens()) {
            au = strToken.nextToken().trim();
            list.add(new Contributor(key, au));

        }

        return list;
    }

    private Hashtable<String, String> parseGroupData(String inputData) {
        String[] group = null;
        Hashtable<String, String> outputTable = new Hashtable<String, String>();
        if (inputData != null && inputData.length() > 0) {
            if (inputData.indexOf(GROUPDELIMITER) > -1) {
                group = inputData.split(GROUPDELIMITER);
            } else {
                group = new String[1];
                group[0] = inputData;
            }

            for (int i = 0; i < group.length; i++) {
                String groupedString = group[i];
                int idIndex = group[i].indexOf(IDDELIMITER);
                String id = null;
                String content = null;
                if (idIndex > -1) {
                    id = groupedString.substring(0, idIndex);
                    content = groupedString.substring(idIndex + 1);
                    outputTable.put(id, content);
                }
            }
        }

        return outputTable;
    }

    public List<String> getEmailList(String emailString) {
        String[] emailArray = null;
        if (emailString != null) {
            String email = removeID(emailString);
            if (email.indexOf(AUDELIMITER) > -1) {
                emailArray = email.split(AUDELIMITER);
            } else {
                emailArray = new String[1];
                emailArray[0] = email;
            }
            return java.util.Arrays.asList(emailArray);
        } else {
            return null;
        }

    }

    public List<Affiliation> getContributorsAff(Key key, String affiliation, String affiliationAddress, String affiliationCity, String affiliationState,
        String affiliationCountry) {

        List<Affiliation> list = new ArrayList<Affiliation>();
        Hashtable<String, String> affTable = parseGroupData(affiliation);
        Hashtable<String, String> addressTable = parseGroupData(affiliationAddress);
        Hashtable<String, String> cityTable = parseGroupData(affiliationCity);
        Hashtable<String, String> stateTable = parseGroupData(affiliationState);
        Hashtable<String, String> countryTable = parseGroupData(affiliationCountry);

        Enumeration<String> idSet = affTable.keys();
        while (idSet.hasMoreElements()) {
            String id = (String) idSet.nextElement();
            Affiliation affObject = null;
            StringBuffer affStringBuffer = new StringBuffer();
            String affString = (String) affTable.get(id);
            if (affString != null) {
                affStringBuffer.append(affString);
                String addressString = (String) addressTable.get(id);
                String cityString = (String) cityTable.get(id);
                String stateString = (String) stateTable.get(id);
                String countryString = (String) countryTable.get(id);

                if (addressString != null && addressString.length() > 0) {
                    affStringBuffer.append(",  " + addressString);
                }
                if (cityString != null && cityString.length() > 0) {
                    affStringBuffer.append(",  " + cityString);
                }
                if (stateString != null && stateString.length() > 0) {
                    affStringBuffer.append(",  " + stateString);
                }
                if (countryString != null && (countryString.length() > 0)) {
                    affStringBuffer.append(",  " + getCountry(countryString));
                }

                affObject = new Affiliation(key, affStringBuffer.toString());

            }
            list.add(affObject);
        }

        return list;
    }

    /*
     * This method builds the IN String from list of docId objects. The select query will get the result set in a reverse way So in order to get in correct
     * order we are doing a reverse example of return String--(23,22,1,12...so on);
     *
     * @param listOfDocIDs
     *
     * @return String
     */
    private String buildINString(List<DocID> listOfDocIDs) {
        StringBuffer sQuery = new StringBuffer();
        sQuery.append("(");
        for (int k = listOfDocIDs.size(); k > 0; k--) {
            DocID doc = (DocID) listOfDocIDs.get(k - 1);
            String docID = doc.getDocID();
            if ((k - 1) == 0) {
                sQuery.append("'" + docID + "'");
            } else {
                sQuery.append("'" + docID + "'").append(",");
            }
        }// end of for
        sQuery.append(")");
        return sQuery.toString();
    }

    /* TS if volume is null and str is not null print n */
    private String replaceVolumeNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = "";
        }

        if (!str.equals("") && ((str.indexOf("v", 0) < 0) && (str.indexOf("V", 0) < 0))) {
            str = "v ".concat(str);
        }
        return str;
    }

    /* TS if number is null and str is not null print n */
    private String replaceIssueNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = "";
        }

        if (!str.equals("") && ((str.indexOf("n") < 0) && (str.indexOf("N") < 0))) {
            str = "n ".concat(str);
        }
        return str;
    }

    private String hasAbstract(ResultSet rs) throws SQLException {
        String abs = null;
        Clob clob = rs.getClob("ABSTRACT");
        if (clob != null) {
            abs = StringUtil.getStringFromClob(clob);
        }

        if (abs == null || abs.length() < 100) {
            return null;
        } else {
            return changeInfToSub(abs);
        }
    }

    private String changeInfToSub(String input) {
        if (input.indexOf("<inf>") > -1 || input.indexOf("</inf>") > -1) {
            input = input.replaceAll("<inf>", "<sub>");
            input = input.replaceAll("<\\/inf>", "</sub>");
        }

        return input;
    }

    private String getPage(String pageRange, String pages, String pageCount)

    {
        String strPage = StringUtil.EMPTY_STRING;
        if (pageRange != null && pageRange.length() > 0) {
            strPage = pageRange;
        } else if (pages != null && pages.length() > 0) {
            strPage = pages;
        } else if (pageCount != null && pageCount.length() > 0) {
            strPage = pageCount;
        }

        return strPage;

    }

    private String replaceTYwithRIScode(String str) {
        if (str == null || str.equals("QQ")) {
            str = StringUtil.EMPTY_STRING;
        }

        if (!str.equals(StringUtil.EMPTY_STRING)) {
            if (str.equals("JA")) {
                str = "JOUR";
            } else if (str.equals("CA")) {
                str = "CONF";
            } else if (str.equals("CP")) {
                str = "CONF";
            } else if (str.equals("MC")) {
                str = "CHAP";
            } else if (str.equals("MR")) {
                str = "BOOK";
            } else if (str.equals("RC")) {
                str = "RPRT";
            } else if (str.equals("RR")) {
                str = "RPRT";
            } else if (str.equals("DS")) {
                str = "THES";
            } else if (str.equals("UP")) {
                str = "UNPB";
            } else {
                str = "JOUR";
            }
        } else {
            str = "JOUR";
        }
        return str;
    }

    // jam XML document mapping, conversion to TY values
    // for BIB format - only called from loadBIB
    private String replaceTYwithBIBcode(String str) {
        if (str == null || str.equals("QQ")) {
            str = StringUtil.EMPTY_STRING;
        }

        if (!str.equals(StringUtil.EMPTY_STRING)) {
            if (str.equals("JA")) {
                str = "article";
            } else if (str.equals("CA")) {
                str = "inproceedings";
            } else if (str.equals("CP")) {
                str = "proceedings";
            } else if (str.equals("MC")) {
                str = "inbook";
            } else if (str.equals("MR")) {
                str = "book";
            } else if (str.equals("RC")) {
                str = "article";
            } else if (str.equals("RR")) {
                str = "article";
            } else if (str.equals("DS")) {
                str = "article";
            } else if (str.equals("UP")) {
                str = "unpublished";
            } else {
                str = "article";
            }
        } else {
            str = "article";
        }
        return str;
    }

    // TS XML document mapping, conversion to dt values 02/10/03

    private String replaceDTNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = StringUtil.EMPTY_STRING;
        }

        if (!str.equals(StringUtil.EMPTY_STRING)) {
            if (str.equalsIgnoreCase("ab")) {
                str = "Abstract Report";
            } else if (str.equalsIgnoreCase("ar")) {
                str = "Article";
            } else if (str.equalsIgnoreCase("bk")) {
                str = "Book";
            } else if (str.equalsIgnoreCase("br")) {
                str = "Book Review";
            } else if (str.equalsIgnoreCase("bz")) {
                str = "Business Article";
            } else if (str.equalsIgnoreCase("ch")) {
                str = "Chapter";
            } else if (str.equalsIgnoreCase("cp")) {
                str = "Conference Paper";
            } else if (str.equalsIgnoreCase("cr")) {
                str = "Conference Review";
            } else if (str.equalsIgnoreCase("di")) {
                str = "Dissertation";
            } else if (str.equalsIgnoreCase("ed")) {
                str = "Editorial";
            } else if (str.equalsIgnoreCase("er")) {
                str = "Erratum";
            } else if (str.equalsIgnoreCase("ip")) {
                str = "Article in Press";
            } else if (str.equalsIgnoreCase("gi")) {
                str = "In Process";
            } else if (str.equalsIgnoreCase("le")) {
                str = "Letter";
            } else if (str.equalsIgnoreCase("no")) {
                str = "Note";
            } else if (str.equalsIgnoreCase("pa")) {
                str = "Patent";
            } else if (str.equalsIgnoreCase("pr")) {
                str = "Press Release";
            } else if (str.equalsIgnoreCase("re")) {
                str = "Review";
            } else if (str.equalsIgnoreCase("rp")) {
                str = "Report";
            } else if (str.equalsIgnoreCase("sh")) {
                str = "Short Survey";
            } else if (str.equalsIgnoreCase("wp")) {
                str = "Working Pape";
            } else if (str.equalsIgnoreCase("JA")) {
                str = "Journal article (JA)";
            } else if (str.equalsIgnoreCase("MC")) {
                str = "Monograph chapter (MC)";
            } else if (str.equalsIgnoreCase("MR")) {
                str = "Monograph review (MR)";
            } else if (str.equalsIgnoreCase("CA")) {
                str = "Conference article (CA)";
            }
        }
        return str;
    }

    private String getSourceType(String str) {
        if (str == null || str.equals("QQ")) {
            str = StringUtil.EMPTY_STRING;
        }

        if (!str.equals(StringUtil.EMPTY_STRING)) {
            if (str.equalsIgnoreCase("b")) {
                str = "Book";
            } else if (str.equalsIgnoreCase("d")) {
                str = "Trade Journal";
            } else if (str.equalsIgnoreCase("j")) {
                str = "Journal";
            } else if (str.equalsIgnoreCase("k")) {
                str = "Book Series";
            } else if (str.equalsIgnoreCase("m")) {
                str = "Multi-volume Reference Works";
            } else if (str.equalsIgnoreCase("p")) {
                str = "Conference Proceeding";
            } else if (str.equalsIgnoreCase("r")) {
                str = "Report";
            }
        }
        return str;
    }

    private Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }

        return h;
    }

    private String firstElement(String data) {
        if (data == null) {
            return null;
        }

        String rdata = null;
        int index = data.indexOf(AUDELIMITER);
        if (index > -1) {
            rdata = data.substring(0, index);
        } else {
            rdata = data;
        }

        return rdata;
    }

    public String[] setElementData(String elementVal) {
        ArrayList<String> list = new ArrayList<String>();
        if (elementVal != null) {
            elementVal = changeInfToSub(elementVal);
        }
        StringTokenizer st = new StringTokenizer(elementVal, AUDELIMITER);
        String strToken = null;

        while (st.hasMoreTokens()) {
            strToken = st.nextToken().trim();
            if (strToken.length() > 0) {
                if (strToken.length() > 1) {
                    strToken = strToken.substring(0, 1).toUpperCase() + strToken.substring(1);
                }
                list.add(strToken);
            }

        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    public String getIssnWithDash(String issn) {
        if (issn.length() == 9) {
            return issn;
        } else if (issn.length() == 8) {
            return issn.substring(0, 4) + "-" + issn.substring(4, 8);
        }

        return null;
    }

    public String getYear(String publicationYear, String publicationDate, String createdDate, String sortDate) {
        String year = publicationYear;

        if (year == null) {
            year = publicationDate;
            if (year != null && year.indexOf("-") > -1) {
                year = year.substring(year.lastIndexOf("-"));
            }
        }

        if (year == null) {
            year = createdDate;
            if (year != null && year.indexOf("-") > -1) {
                year = year.substring(year.lastIndexOf("-"));
            }
        }

        if (year == null) {
            year = sortDate;
            if (year != null && year.indexOf("-") > -1) {
                year = year.substring(year.lastIndexOf("-"));
            }
        }

        return year;
    }

    /*
     * private List getMappingCoordinates(String strMaintermsRegion) { String[] geobasemaintermsrgi = strMaintermsRegion.split(AUDELIMITER);
     *
     * List mapcoords = new ArrayList(); GeoRefCoordinateMap coords = GeoRefCoordinateMap.getInstance(); for(int termindex = 0; termindex <
     * geobasemaintermsrgi.length; termindex++) { String term = geobasemaintermsrgi[termindex]; String georefcoords =
     * coords.lookupGeoBaseTermRawCoordinates(term); if(georefcoords != null) { //System.out.println(" Found coords " + georefcoords + " for " +
     * geobasemaintermsrgi[termindex]); term = term.concat(GRFDocBuilder.IDDELIMITER).concat(georefcoords); mapcoords.add(term); } } return mapcoords; }
     */
    @Override
    public Key[] getCitationKeys() {
        return CITATION_KEYS;
    }

    @Override
    public Key[] getAbstractKeys() {
        return ABSTRACT_KEYS;
    }

    @Override
    public Key[] getDetailedKeys() {
        return DETAILED_KEYS;
    }

}
