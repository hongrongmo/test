package org.ei.data.pag.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.books.BookDocument;
import org.ei.books.IBookPartFetcher;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.common.AuthorStream;
import org.ei.common.DataCleaner;
import org.ei.domain.Abstract;
import org.ei.domain.Citation;
import org.ei.domain.Contributor;
import org.ei.domain.Contributors;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.DocumentBuilderException;
import org.ei.domain.EIDoc;
import org.ei.domain.ElementData;
import org.ei.domain.ElementDataMap;
import org.ei.domain.FullDoc;
import org.ei.domain.ISBN;
import org.ei.domain.ISSN;
import org.ei.domain.Key;
import org.ei.domain.Keys;
import org.ei.domain.RIS;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.exception.SystemErrorCodes;
import org.ei.query.base.Highlights;
import org.ei.util.StringUtil;

public class PAGDocBuilder implements DocumentBuilder {

    private static Pattern nonAscii = Pattern.compile("[\\x00-\\x1F\\x80-\\xFF]");

    public static String TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER_TEXT = "Elsevier";

    public Perl5Util perl = new Perl5Util();

    // This order is the order in which the fields will show up
    // in the Detailed.xml stylesheet (Page Details)
    public static final Key[] DETAILED_KEYS = { Keys.BOOK_TITLE, Keys.BOOK_PAGE, Keys.BOOK_SERIES_TITLE, Keys.BOOK_DESCRIPTION, Keys.AUTHORS, Keys.EDITORS,
        Keys.AUTHOR_AFFS, Keys.EDITOR_AFFS, Keys.ISSN, Keys.ISBN, Keys.ISBN13, Keys.BOOK_CHAPTER_TITLE, Keys.BOOK_SECTION_TITLE, Keys.BOOK_CHAPTER_PII,
        Keys.BOOK_CHAPTER_START, Keys.BOOK_PAGE_COUNT, Keys.BOOK_YEAR, Keys.COVER_IMAGE, Keys.BOOK_PUBLISHER, Keys.ACCESSION_NUMBER, Keys.TITLE_TRANSLATION,
        Keys.VOLUME, Keys.MONOGRAPH_TITLE, Keys.LANGUAGE, Keys.DOC_TYPE, Keys.ABSTRACT, Keys.ABSTRACT_TYPE, Keys.BOOK_PAGE_KEYWORDS, Keys.CONTROLLED_TERMS,
        Keys.UNCONTROLLED_TERMS, Keys.CLASS_CODES, Keys.COLLECTION, Keys.TREATMENTS, Keys.DOI, Keys.DOCID, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.PROVIDER,
        Keys.NO_SO, Keys.PAGE_THUMBS };

    private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_LA, Keys.RIS_N1, Keys.RIS_TI, Keys.RIS_T1, Keys.RIS_BT, Keys.RIS_JO, Keys.RIS_T3,
        Keys.RIS_AUS, Keys.RIS_AD, Keys.RIS_EDS, Keys.RIS_VL, Keys.RIS_IS, Keys.RIS_PY, Keys.RIS_AN, Keys.RIS_SP, Keys.RIS_EP, Keys.RIS_SN, Keys.RIS_S1,
        Keys.RIS_MD, Keys.RIS_CY, Keys.RIS_PB, Keys.RIS_N2, Keys.RIS_KW, Keys.RIS_CVS, Keys.RIS_FLS, Keys.RIS_DO };
    private static final Key[] XML_KEYS = { Keys.TITLE, Keys.BOOK_PAGE, Keys.BOOK_SERIES_TITLE, Keys.BOOK_DESCRIPTION, Keys.AUTHORS, Keys.EDITORS,
        Keys.AUTHOR_AFFS, Keys.EDITOR_AFFS, Keys.ISSN, Keys.ISBN, Keys.ISBN13, Keys.BOOK_CHAPTER_TITLE, Keys.BOOK_SECTION_TITLE, Keys.BOOK_PAGE_COUNT,
        Keys.BOOK_YEAR, Keys.COVER_IMAGE, Keys.BOOK_PUBLISHER, Keys.ACCESSION_NUMBER, Keys.TITLE_TRANSLATION, Keys.VOLUME, Keys.MONOGRAPH_TITLE, Keys.LANGUAGE,
        Keys.DOC_TYPE, Keys.ABSTRACT, Keys.ABSTRACT_TYPE, Keys.BOOK_PAGE_KEYWORDS, Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.CLASS_CODES,
        Keys.COLLECTION, Keys.TREATMENTS, Keys.DOI, Keys.DOCID, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.PROVIDER, Keys.NO_SO, Keys.PAGE_THUMBS };

    /*
     * private static String queryXMLCitation =
     * "SELECT PAGE_KEYWORDS, DOCID, BN, BN13, PII, PAGE_NUM, PAGE_LABEL, SECTION_TITLE, SECTION_START, CHAPTER_START, CHAPTER_TITLE, PAGE_TXT, PAGE_TOTAL, CVS, AB, ST, BN, PP, YR, AUS, TI, PN, VO, ISS, SUB FROM PAGES_ALL WHERE DOCID IN "
     * ;
     */
    private static String queryDocument = "SELECT PAGE_KEYWORDS, DOCID, BN, BN13, PII, PAGE_NUM, PAGE_LABEL, SECTION_TITLE, SECTION_START, CHAPTER_START, CHAPTER_TITLE, PAGE_TXT, PAGE_TOTAL, CVS, AB, ST, BN, PP, YR, AUS, TI, PN, VO, ISS, SUB FROM PAGES_ALL WHERE DOCID IN "; // ('pag_0080426794_131')

    private static String queryPreview = "select DOCID, PAGE_TXT FROM PAGES_ALL WHERE DOCID IN ";

    private IBookPartFetcher fetcher;

    public IBookPartFetcher getFetcher() {
		return fetcher;
	}

	public void setFetcher(IBookPartFetcher fetcher) {
		this.fetcher = fetcher;
	}

	public DocumentBuilder newInstance(Database database) {
        return new PAGDocBuilder(database);
    }

    public PAGDocBuilder() {
    }

    public PAGDocBuilder(Database database) {
    }

    /**
     * This method takes a list of DocID objects and dataFormat and returns a List of EIDoc Objects based on a particular dataformat @ param listOfDocIDs @
     * param dataFormat @ return List --list of EIDoc's @ exception DocumentBuilderException
     */
    public List<EIDoc> buildPage(List<DocID> listOfDocIDs, String dataFormat) throws DocumentBuilderException {
        List<EIDoc> l = null;
        if (dataFormat == null) {
            dataFormat = Abstract.ABSTRACT_FORMAT;
        }
        if (dataFormat.equals(Citation.CITATION_FORMAT) || dataFormat.equals(Abstract.ABSTRACT_FORMAT) || dataFormat.equals(FullDoc.FULLDOC_FORMAT)) {
            l = loadDocument(dataFormat, listOfDocIDs);
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
        Map<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

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
                String abs = getStringFromClob(rset.getClob("PAGE_TXT"));

                if (StringUtils.isNotBlank(abs)) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

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

    /**
     * This method basically takes list Of DocIDs as Parameter This list of Docids use buildINString() method to build the required IN clause String.This is
     * appended to sql String The resultSet so obtained by executing the sql,is iterated, to build Detailed EIDoc objects,which are then added to EIdocumentList
     *
     * @param listOfDocIDs
     * @return EIDocumentList
     * @exception Exception
     */
    private List<EIDoc> loadDocument(String docformat, List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Perl5Util perl = new Perl5Util();
        Map<String, DocID> oidTable = getDocIDTable(listOfDocIDs);

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

            rset = stmt.executeQuery(queryDocument + INString);
            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("DOCID"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, TEXT_COPYRIGHT));

                if (rset.getString("PAGE_KEYWORDS") != null) {
                    ht.put(Keys.BOOK_PAGE_KEYWORDS, new XMLMultiWrapper(Keys.BOOK_PAGE_KEYWORDS, rset.getString("PAGE_KEYWORDS").split("\\|")));
                }

                if (rset.getString("AUS") != null) {
                    String authorstr = rset.getString("AUS");
                    authorstr = nonAscii.matcher(authorstr).replaceAll(" ");
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(authorstr, Keys.AUTHORS));

                    ht.put(Keys.AUTHORS, authors);

                }

                if (rset.getString("TI") != null) {
                    String title = rset.getString("TI");
                    title = nonAscii.matcher(title).replaceAll(" ");
                    ht.put(Keys.BOOK_TITLE, new XMLWrapper(Keys.BOOK_TITLE, title));
                }

                if (rset.getString("ST") != null) {
                    String stitle = rset.getString("ST");
                    stitle = nonAscii.matcher(stitle).replaceAll(" ");
                    ht.put(Keys.BOOK_SERIES_TITLE, new XMLWrapper(Keys.BOOK_SERIES_TITLE, stitle));
                }

                String chapterTitle = rset.getString("CHAPTER_TITLE");
                String chapterStart = rset.getString("CHAPTER_START");

                // PII
                String pii = rset.getString("PII");
                if (pii != null) {
                    // do not include chapter information unless there is a PII - chapter 'Chunk' PDF
                    ht.put(Keys.BOOK_CHAPTER_PII, new XMLWrapper(Keys.BOOK_CHAPTER_PII, pii));

                    if (chapterTitle != null) {
                        ht.put(Keys.BOOK_CHAPTER_TITLE, new XMLWrapper(Keys.BOOK_CHAPTER_TITLE, chapterTitle));
                    }
                }
                // we always need chapter start for loading the Navigation frame
                if (chapterStart != null) {
                    ht.put(Keys.BOOK_CHAPTER_START, new XMLWrapper(Keys.BOOK_CHAPTER_START, chapterStart));
                }

                // if chapter start is null use section start for loading the Navigation frame
                String sectionStart = rset.getString("SECTION_START");
                if ((sectionStart != null) && (chapterStart == null)) {
                    ht.put(Keys.BOOK_CHAPTER_START, new XMLWrapper(Keys.BOOK_CHAPTER_START, sectionStart));
                }

                String sectTitle = rset.getString("SECTION_TITLE");
                if ((sectTitle != null) && (!sectTitle.equalsIgnoreCase("Book"))) {
                    // sectitle will not be null here - it doesn't matter if chapterTitle is
                    if (!sectTitle.equals(chapterTitle)) {
                        ht.put(Keys.BOOK_SECTION_TITLE, new XMLWrapper(Keys.BOOK_SECTION_TITLE, sectTitle));
                    }
                }

                if (rset.getString("YR") != null) {
                    ht.put(Keys.BOOK_YEAR, new Year(Keys.BOOK_YEAR, rset.getString("YR"), perl));
                }

                // PP
                String strPages = rset.getString("PAGE_TOTAL");
                if (strPages != null) {
                    ht.put(Keys.BOOK_PAGE_COUNT, new XMLWrapper(Keys.BOOK_PAGE_COUNT, "Total Pages", strPages));
                }

                String pageNum = rset.getString("PAGE_NUM");
                if (pageNum != null) {
                    ht.put(Keys.BOOK_PAGE, new XMLWrapper(Keys.BOOK_PAGE, pageNum));
                }
                try {
                    if (!docformat.equalsIgnoreCase(Citation.CITATION_FORMAT)) {
                        if (Integer.parseInt(pageNum) == 0) {
                            String bkdesc = getStringFromClob(rset.getClob("AB"));
                            bkdesc = nonAscii.matcher(bkdesc).replaceAll(" ");
                            ht.put(Keys.BOOK_DESCRIPTION, new XMLWrapper(Keys.BOOK_DESCRIPTION, bkdesc));
                        }
                    }
                } catch (NumberFormatException e) {
                }

                // BN
                if (rset.getString("BN") != null) {
                    ht.put(Keys.ISBN, new ISBN(Keys.ISBN, rset.getString("BN")));
                }

                // ISSN
                if (rset.getString("ISS") != null) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("ISS")));
                }

                // BN13
                if (rset.getString("BN13") != null) {
                    ht.put(Keys.ISBN13, new ISBN(Keys.ISBN13, rset.getString("BN13")));
                    // ht.put(Keys.COVER_IMAGE,new XMLWrapper(Keys.COVER_IMAGE,"/static/images/bn13/"+rset.getString("BN13")+"small.jpg"));
                }

                // PN
                // suppress source label
                // if(docformat.equals(Citation.CITATION_FORMAT)) {
                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                // }

                List<String> lstTokens = new ArrayList<String>();
                if (rset.getString("PN") != null) {
                    lstTokens.add((String) rset.getString("PN"));
                }
                if (lstTokens.size() > 0) {
                    ht.put(Keys.BOOK_PUBLISHER, new XMLWrapper(Keys.BOOK_PUBLISHER, StringUtil.join(lstTokens, ", ")));
                }
                lstTokens = null;

                if (!docformat.equalsIgnoreCase(Citation.CITATION_FORMAT)) {
                    ElementData descr = null;
                    String strabs = null;
                    strabs = getStringFromClob(rset.getClob("PAGE_TXT"));
                    if ((strabs != null) && !strabs.equals(StringUtil.EMPTY_STRING)) {
                        strabs = nonAscii.matcher(strabs).replaceAll(" ");

                        descr = new Highlights(Keys.ABSTRACT, "Page Highlights", strabs, 4, 6);
                        descr.exportLabels(true);
                        ht.put(Keys.ABSTRACT, descr);
                    }
                }
                // CVS
                if ((rset.getString("CVS") != null)) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(new Key("CVS", "Subject terms"), setElementData(rset.getString("CVS"))));
                }

                // COLLECTION
                String strVol = rset.getString("VO");
                if (strVol != null) {
                    ElementData col = new XMLWrapper(Keys.COLLECTION, "Referex");
                    col = new XMLWrapper(Keys.COLLECTION, (new PAGDatabase()).getDataDictionary().getClassCodeTitle(strVol));
                    ht.put(Keys.COLLECTION, col);
                    String sub = rset.getString("SUB");
                    if (sub != null) {
                        did.setCollection(strVol + sub);
                    } else {
                        did.setCollection(strVol);
                    }
                }

                EIDoc eiDoc = new BookDocument(did, ht, docformat, this.fetcher);
                eiDoc.exportLabels(true);
                eiDoc.setLoadNumber(0);
                eiDoc.setOutputKeys(DETAILED_KEYS);
                list.add(eiDoc);
                count++;
            } // while

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

    // ???? BT - RIS_MT and in RIS_BT

    private List<EIDoc> loadRIS(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Map<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();

        List<EIDoc> list = new ArrayList<EIDoc>();
        int count = 0;
        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;
        ConnectionBroker broker = null;
        String INString = buildINString(listOfDocIDs);
        // N2 BT
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryDocument + INString);

            String risDocType = "BOOK";
            String strBookLA = "English";
            String bibDocType = "article";

            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("DOCID"));

                // default field
                ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, PAGDocBuilder.TEXT_COPYRIGHT));

                // always BOOK
                ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, risDocType));

                // currently books have no language field - all are in same lang
                ht.put(Keys.RIS_LA, new XMLWrapper(Keys.RIS_LA, strBookLA));

                // use BT field for Book Title
                if (rset.getString("TI") != null) {
                    ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, rset.getString("TI")));
                }

                // use T2 for Book Volume
                if (rset.getString("ST") != null) {
                    ht.put(Keys.RIS_T2, new XMLWrapper(Keys.RIS_T2, rset.getString("ST")));
                }

                // Put Book Collection in T3 "Title Series"
                ht.put(Keys.RIS_T3, new XMLWrapper(Keys.RIS_T3, (new PAGDatabase()).getDataDictionary().getClassCodeTitle(rset.getString("VO"))));

                if (rset.getString("AUS") != null) {

                    Contributors authors = new Contributors(Keys.RIS_AUS, getContributors(rset.getString("AUS"), Keys.RIS_AUS));

                    ht.put(Keys.RIS_AUS, authors);

                } else {
                    if (rset.getString("ED") != null) {
                        String strED = StringUtil.replaceNullWithEmptyString(rset.getString("ED"));

                        if (perl.match("/(Ed[.]\\s*)/", strED)) {
                            strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                        }

                        Contributors editors = new Contributors(Keys.RIS_EDS, getContributors(strED, Keys.RIS_EDS));

                        ht.put(Keys.RIS_EDS, editors);

                    }

                }

                if (rset.getString("YR") != null) {
                    ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, rset.getString("YR"), perl));
                }

                // Book Pages, not PDF PAges
                String strPages = rset.getString("PP");
                ht.put(Keys.RIS_SP, new XMLWrapper(Keys.RIS_SP, "1"));
                ht.put(Keys.RIS_EP, new XMLWrapper(Keys.RIS_EP, strPages));

                // ISBN
                if (rset.getString("BN") != null) {
                    ht.put(Keys.RIS_SN, new XMLWrapper(Keys.RIS_SN, rset.getString("BN")));
                }

                // Publisher
                if (rset.getString("PN") != null) {
                    ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, rset.getString("PN")));
                }

                // Abstract/Book Description
                String abs = getStringFromClob(rset.getClob("AB"));
                if ((abs != null) && !abs.equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, abs));
                }

                // CVS
                ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(rset.getString("CVS"))));

                // Keywords
                if (rset.getString("PAGE_KEYWORDS") != null) {
                    String pagekeywords = perl.substitute("s/[|]+/;/g", rset.getString("PAGE_KEYWORDS"));
                    ht.put(Keys.RIS_KW, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(pagekeywords)));
                }

                EIDoc eiDoc = new EIDoc(did, ht, RIS.RIS_FORMAT);
                eiDoc.setLoadNumber(0);
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
    private List<EIDoc> loadXMLCitations(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Map<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
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
            rset = stmt.executeQuery(queryDocument + INString);

            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("DOCID"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, TEXT_COPYRIGHT));

                if (rset.getString("PAGE_KEYWORDS") != null) {
                    ht.put(Keys.BOOK_PAGE_KEYWORDS, new XMLMultiWrapper(Keys.BOOK_PAGE_KEYWORDS, rset.getString("PAGE_KEYWORDS").split("\\|")));
                }

                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUS"), Keys.AUTHORS));

                    ht.put(Keys.AUTHORS, authors);

                }

                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("TI")));
                }

                String sectTitle = rset.getString("CHAPTER_TITLE");
                if ((sectTitle != null) && (!sectTitle.equalsIgnoreCase("Book"))) {
                    ht.put(Keys.BOOK_CHAPTER_TITLE, new XMLWrapper(Keys.BOOK_CHAPTER_TITLE, "Chapter/Section title", sectTitle));
                }

                if (rset.getString("YR") != null) {
                    ht.put(Keys.BOOK_YEAR, new Year(Keys.BOOK_YEAR, rset.getString("YR"), perl));
                }

                // PP
                String strPages = rset.getString("PAGE_TOTAL");
                if (strPages != null) {
                    ht.put(Keys.BOOK_PAGE_COUNT, new XMLWrapper(Keys.BOOK_PAGE_COUNT, "Total Pages", strPages));
                }

                String pageNum = rset.getString("PAGE_NUM");
                if (pageNum != null) {
                    ht.put(Keys.BOOK_PAGE, new XMLWrapper(Keys.BOOK_PAGE, pageNum));
                }
                // BN
                if (rset.getString("BN") != null) {
                    ht.put(Keys.ISBN, new ISBN(rset.getString("BN")));
                }
                if (rset.getString("BN13") != null) {
                    ht.put(Keys.ISBN13, new ISBN(rset.getString("BN13")));
                }

                // PN
                // suppress source label
                // if(docformat.equals(Citation.CITATION_FORMAT)) {
                ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                // }

                List<String> lstTokens = new ArrayList<String>();
                if (rset.getString("PN") != null) {
                    lstTokens.add((String) rset.getString("PN"));
                }
                if (lstTokens.size() > 0) {
                    ht.put(Keys.BOOK_PUBLISHER, new XMLWrapper(Keys.BOOK_PUBLISHER, StringUtil.join(lstTokens, ", ")));
                }
                lstTokens = null;

                // CVS
                if ((rset.getString("CVS") != null)) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(new Key("CVS", "Subject terms"), setElementData(rset.getString("CVS"))));
                }

                // COLLECTION
                String strVol = rset.getString("VO");
                if (strVol != null) {
                    ElementData col = new XMLWrapper(Keys.COLLECTION, "Referex");
                    col = new XMLWrapper(Keys.COLLECTION, (new PAGDatabase()).getDataDictionary().getClassCodeTitle(strVol));
                    ht.put(Keys.COLLECTION, col);
                    String sub = rset.getString("SUB");
                    if (sub != null) {
                        did.setCollection(strVol + sub);
                    } else {
                        did.setCollection(strVol);
                    }
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.XMLCITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(0);
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

    public List<Contributor> getContributors(String strAuthors, Key key) {

        List<Contributor> list = new ArrayList<Contributor>();
        AuthorStream aStream = null;
        DataCleaner dataCleaner = new DataCleaner();
        strAuthors = dataCleaner.cleanEntitiesForDisplay(strAuthors);
        try {
            aStream = new AuthorStream(new ByteArrayInputStream(strAuthors.getBytes()));
            // aStream.setDelimited30(';');
            String strToken = null;
            while ((strToken = aStream.readAuthor()) != null) {
                list.add(new Contributor(key, strToken));
            }
        } catch (IOException ioe) {
            System.out.println("IOE " + ioe.getMessage());
        } finally {
            if (aStream != null)
                try {
                    aStream.close();
                    aStream = null;
                } catch (IOException ioe) {
                }
        }
        return list;
    }

    /*
     * This method builds the IN String from list of docId objects. The select query will get the result set in a reverse way So in order to get in correct
     * order we are doing a reverse example of return String--(23,22,1,12...so on); @param listOfDocIDs @return String
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

    public String getStringFromClob(Clob clob) {
        String temp = "";
        if (clob != null) {
            try {
                temp = clob.getSubString(1, (int) clob.length());
            } catch (SQLException sqle) {
                temp = "";
            }
        }
        return temp;
    }

    private Map<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Map<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }

        return h;
    }

    public String[] setElementData(String elementVal) {
        ArrayList<String> list = new ArrayList<String>();
        AuthorStream aStream = null;
        String strToken = null;
        try {
            if (elementVal != null) {
                aStream = new AuthorStream(new ByteArrayInputStream(elementVal.getBytes()));
                // aStream.setDelimited30(';');
                strToken = null;
                while ((strToken = aStream.readAuthor()) != null) {

                    list.add(strToken.trim());
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (aStream != null) {
                try {
                    aStream.close();
                    aStream = null;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    @Override
    public Key[] getCitationKeys() {
        return DETAILED_KEYS;
    }

    @Override
    public Key[] getAbstractKeys() {
        return DETAILED_KEYS;
    }

    @Override
    public Key[] getDetailedKeys() {
        return DETAILED_KEYS;
    }

}
