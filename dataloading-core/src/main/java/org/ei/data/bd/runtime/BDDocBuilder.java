package org.ei.data.bd.runtime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.data.CITEDBY;
import org.ei.common.Language;
import org.ei.common.bd.BdAffiliation;
import org.ei.common.bd.BdAffiliations;
import org.ei.common.bd.BdAuthor;
import org.ei.common.bd.BdAuthors;
import org.ei.common.bd.BdCitationTitle;
import org.ei.common.bd.BdCoden;
import org.ei.common.bd.BdConfLocations;
import org.ei.common.bd.BdCorrespAffiliation;
import org.ei.common.bd.BdCorrespAffiliations;
import org.ei.common.bd.BdDocumentType;
import org.ei.common.bd.BdEditors;
import org.ei.common.bd.BdIsbn;
import org.ei.common.bd.BdPageCount;
import org.ei.common.bd.CVTerms;
import org.ei.common.Constants;
import org.ei.xml.Entity;
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
import org.ei.domain.EIDoc;
import org.ei.domain.ElementData;
import org.ei.domain.ElementDataMap;
import org.ei.domain.FullDoc;
import org.ei.domain.ISSN;
import org.ei.domain.Issue;
import org.ei.domain.Key;
import org.ei.domain.KeyValuePair;
import org.ei.domain.Keys;
import org.ei.domain.PageRange;
import org.ei.domain.RIS;
import org.ei.domain.Volume;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLMultiWrapper2;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.util.StringUtil;

public class BDDocBuilder implements DocumentBuilder {
    public static String CPX_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String CPX_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER_TEXT = "Ei";
    private static Map<String, String> issnARFix = new HashMap<String, String>();
    private static final Key ELT_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Controlled terms");
    private static final Key ELT_CLASS_CODES = new Key(Keys.CLASS_CODES_MULTI, "Class codes");
    private static final Key ELT_MAJOR_TERMS = new Key(Keys.MAJOR_TERMS, "Major terms");
    private static final Key CPX_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Ei controlled terms");
    private static final Key CPX_CLASS_CODES = new Key(Keys.CLASS_CODES, "Ei classification codes");
    private static final Key CPX_MAIN_HEADING = new Key(Keys.MAIN_HEADING, "Ei main heading");
    private static final String LT_MSG = "Please click here to view all linked terms";
    public static final Key[] CITATION_KEYS = { Keys.DOC_TYPE, Keys.CITEDBY, Keys.PI, Keys.ACCESSION_NUMBER, Keys.DOCID, Keys.TITLE, Keys.TITLE_TRANSLATION,
        Keys.EDITORS, Keys.AUTHORS, Keys.AUTHOR_AFFS, Keys.SOURCE, Keys.MONOGRAPH_TITLE, Keys.PAGE_RANGE, Keys.ARTICLE_NUMBER, Keys.VOLISSUE,
        Keys.PUBLICATION_YEAR, Keys.PUBLISHER, Keys.ISSUE_DATE, Keys.ISSN, Keys.LANGUAGE, Keys.NO_SO, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.DOI,
        Keys.PATAPPNUM, Keys.PATNUM, Keys.PATASSIGN, Keys.PATENT_ISSUE_DATE };
    public static final Key[] ABSTRACT_KEYS = { Keys.CITEDBY, Keys.PI, Keys.ACCESSION_NUMBER, Keys.DOCID, Keys.TITLE, Keys.TITLE_TRANSLATION, Keys.EDITORS,
        Keys.AUTHORS, Keys.EDITOR_AFFS, Keys.AUTHOR_AFFS, Keys.VOLISSUE, Keys.SOURCE, Keys.PUBLICATION_YEAR, Keys.ISSUE_DATE, Keys.MONOGRAPH_TITLE,
        Keys.PAGE_RANGE, Keys.ARTICLE_NUMBER, Keys.CONFERENCE_NAME, Keys.ISSN, Keys.ISBN, Keys.PUBLISHER, Keys.I_PUBLISHER, Keys.CONF_DATE, Keys.SPONSOR,
        Keys.PROVIDER, Keys.LANGUAGE, Keys.MAIN_HEADING, ELT_MAJOR_TERMS, CPX_CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS,
        Keys.ABSTRACT, Keys.NUMBER_OF_REFERENCES, Keys.NO_SO, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.CLASS_CODES, Keys.CAS_REGISTRY_CODES, Keys.TREATMENTS,
        Keys.DOI, Keys.PATAPPNUM, Keys.PATNUM, Keys.PATASSIGN, Keys.REPORT_NUMBER_PAPER, Keys.PATENT_ISSUE_DATE, Keys.ISBN13, Keys.E_ISSN, Keys.DOC_TYPE };
    public static final Key[] DETAILED_KEYS = { Keys.CITEDBY, Keys.PI, Keys.ACCESSION_NUMBER, Keys.PATAPPNUM, Keys.PRIORITY_INFORMATION, Keys.PATNUM,
        Keys.PATASSIGN, Keys.TITLE, Keys.TITLE_TRANSLATION, Keys.AUTHORS, Keys.AUTHOR_AFFS, Keys.CORRESPONDING_EMAIL, Keys.CORRESPONDING_AUTHORS,
        Keys.CORRESPONDING_AUTHORS_AFF, Keys.EDITORS, Keys.EDITOR_AFFS, Keys.SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.VOLUME, Keys.ISSUE, Keys.SOURCE,
        Keys.MONOGRAPH_TITLE, Keys.VOLUME_TITLE, Keys.ISSUE_DATE, Keys.PUBLICATION_YEAR, Keys.REPORT_NUMBER_PAPER, Keys.PAPER_NUMBER, Keys.PAGE_RANGE,
        Keys.ARTICLE_NUMBER, Keys.SECONDARY_SOURCE, Keys.LANGUAGE, Keys.ISSN, Keys.E_ISSN, Keys.CODEN, Keys.ISBN, Keys.ISBN13, Keys.DOC_TYPE,
        Keys.CONFERENCE_NAME, Keys.CONF_DATE, Keys.MEETING_LOCATION, Keys.CONF_CODE, Keys.SPONSOR, Keys.PUBLISHER, Keys.ABSTRACT, Keys.ABSTRACT_TYPE,
        Keys.NUMBER_OF_CLAIMS, Keys.NUMBER_OF_TABLES, Keys.SPECIFIC_NAMES, Keys.NUMBER_OF_REFERENCES, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS, Keys.PROVIDER,
        Keys.SUPPL, Keys.PI, Keys.PAGE_COUNT, Keys.MAIN_HEADING, ELT_MAJOR_TERMS, Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.REGION_CONTROLLED_TERMS,
        Keys.CAS_REGISTRY_CODES, Keys.CLASS_CODES, Keys.CLASS_CODES_MULTI, Keys.INDEXING_TEMPLATE, Keys.MANUAL_LINKED_TERMS, Keys.LINKED_TERMS,
        Keys.LINKED_TERMS_HOLDER, Keys.TREATMENTS, Keys.DOI, Keys.DOCID, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.PATENT_ISSUE_DATE };
    private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_LA, Keys.RIS_N1, Keys.RIS_M1, Keys.RIS_TI, Keys.RIS_T1, Keys.RIS_BT, Keys.RIS_JO,
        Keys.RIS_T3, Keys.RIS_AUS, Keys.RIS_AD, Keys.RIS_EDS, Keys.RIS_VL, Keys.RIS_IS, Keys.RIS_PY, Keys.RIS_AN, Keys.RIS_SP, Keys.RIS_EP, Keys.RIS_SN,
        Keys.RIS_BN, Keys.RIS_MD, Keys.RIS_CY, Keys.RIS_PB, Keys.RIS_N2, Keys.RIS_KW, Keys.RIS_CVMS, Keys.RIS_CVS, Keys.RIS_FLS, Keys.RIS_DO };
    private static final Key[] XML_KEYS = { Keys.ISSN, Keys.MAIN_HEADING, Keys.NO_SO, Keys.MONOGRAPH_TITLE, Keys.PUBLICATION_YEAR, Keys.VOLUME_TITLE,
        Keys.CONTROLLED_TERM, Keys.ISBN, Keys.ISBN13, Keys.AUTHORS, Keys.DOCID, Keys.SOURCE, Keys.NUMVOL, Keys.EDITOR_AFFS, Keys.EDITORS, Keys.PUBLISHER,
        Keys.VOLUME, Keys.AUTHOR_AFFS, Keys.PROVIDER, Keys.ISSUE_DATE, Keys.COPYRIGHT_TEXT, Keys.DOI, Keys.PAGE_COUNT, Keys.PUBLICATION_DATE, Keys.TITLE,
        Keys.TITLE_TRANSLATION, Keys.LANGUAGE, Keys.PAGE_RANGE, Keys.PAPER_NUMBER, Keys.COPYRIGHT, Keys.ISSUE, Keys.ACCESSION_NUMBER, Keys.CONTROLLED_TERMS,
        Keys.PATENT_ISSUE_DATE };
    public static final String DELIMITER = ",";
    static { // ISSNs with AR field problem
        issnARFix.put("00913286", "");
        issnARFix.put("10833668", "");
        issnARFix.put("10179909", "");
        issnARFix.put("15393755", "");
        issnARFix.put("00319007", "");
        issnARFix.put("10502947", "");
        issnARFix.put("00036951", "");
        issnARFix.put("00218979", "");
        issnARFix.put("00219606", "");
        issnARFix.put("00346748", "");
        issnARFix.put("1070664X", "");
        issnARFix.put("10706631", "");
        issnARFix.put("00948276", "");
        issnARFix.put("00431397", "");
    }

    private Database database;

    private static String queryBD = "select * from bd_master where M_ID IN  ";

    private static String previewQueryBD = "select M_ID, ABSTRACTDATA from bd_master where M_ID IN  ";

    public DocumentBuilder newInstance(Database database) {
        return new BDDocBuilder(database);
    }

    public BDDocBuilder() {
    }

    public BDDocBuilder(Database database) {
        this.database = database;
    }

    /**
     * This method takes a list of DocID objects and dataFormat and returns a List of EIDoc Objects based on a particular dataformat @ param listOfDocIDs @
     * param dataFormat @ return List --list of EIDoc's @ exception DocumentBuilderException
     */
    public List<?> buildPage(List<DocID> listOfDocIDs, String dataFormat) throws DocumentBuilderException {
        List<EIDoc> l = null;
        if (Citation.CITATION_PREVIEW.equalsIgnoreCase(dataFormat)) {
            l = loadPreviewData(listOfDocIDs);
        } else {
            l = loadDocument(listOfDocIDs, dataFormat);
        }

        return l;
    }

    private List<EIDoc> loadDocument(List<DocID> listOfDocIDs, String dataFormat) throws DocumentBuilderException {
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
            rset = stmt.executeQuery(queryBD + INString);
            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                EIDoc eiDoc = newEIDoc(did, dataFormat, ht);
                database = did.getDatabase();
                buildField(Keys.DOCID, (DocID) oidTable.get(rset.getString("M_ID")), ht);
                formatRIS(buildField(Keys.ACCESSION_NUMBER, rset.getString("ACCESSNUMBER"), ht), dataFormat, Keys.ACCESSION_NUMBER, Keys.RIS_AN);
                formatRIS(buildField(Keys.DOI, rset.getString("DOI"), ht), dataFormat, Keys.DOI, Keys.RIS_DO);
                buildField(Keys.COPYRIGHT, CPX_HTML_COPYRIGHT, ht);
                formatRIS(buildField(Keys.COPYRIGHT_TEXT, CPX_TEXT_COPYRIGHT, ht), dataFormat, Keys.COPYRIGHT_TEXT, Keys.RIS_N1);

                Key issuedateKey = Keys.ISSUE_DATE;
                String strDocType = rset.getString("CITTYPE");
                if (("PA").equalsIgnoreCase(strDocType)) {
                    issuedateKey = Keys.PATENT_ISSUE_DATE;
                }

                String pyr = rset.getString("PUBLICATIONYEAR");
                String pyd = reFormatDate(rset.getString("PUBLICATIONDATE"));

                if (pyd != null && pyr != null && !pyd.matches(".*[1-9][0-9][0-9][0-9].*")) {
                    pyd = pyd + " " + pyr;
                }

                buildField(issuedateKey, pyd, ht);

                // added for CITEDBY

                if (rset.getString("DATABASE") != null &&
                    (rset.getString("DATABASE")).equalsIgnoreCase(DatabaseConfig.CPX_PREF) || (rset.getString("DATABASE")).equalsIgnoreCase(DatabaseConfig.GEO_PREF)) {
                    CITEDBY citedby = new CITEDBY();
                    citedby.setKey(Keys.CITEDBY);

                    if (rset.getString("DOI") != null) {
                        citedby.setDoi(URLEncoder.encode((rset.getString("DOI")).trim(), "UTF-8"));
                    }

                    if (rset.getString("PII") != null) {
                        citedby.setPii((rset.getString("PII")).trim());
                    }

                    if (rset.getString("VOLUME") != null) {
                        citedby.setVolume((rset.getString("VOLUME")).trim());
                    }

                    if (rset.getString("ISSUE") != null) {
                        citedby.setIssue((rset.getString("ISSUE")).trim());
                    }

                    if (rset.getString("ISSN") != null) {
                        citedby.setIssn((rset.getString("ISSN")).trim());
                    }

                    if (rset.getString("PAGE") != null) {
                        citedby.setPage((rset.getString("PAGE")).trim());
                    }

                    if (rset.getString("ACCESSNUMBER") != null) {
                        citedby.setAccessionNumber(rset.getString("ACCESSNUMBER"));
                    }

                    if (rset.getString("ISBN") != null) {
                        citedby.setIsbn(getIsbn(rset.getString("ISBN"), 10));
                        citedby.setIsbn13(getIsbn(rset.getString("ISBN"), 13));
                    }

                    buildField(Keys.CITEDBY, citedby, ht);
                }

                // end citedby

                formatRIS(buildField(Keys.MONOGRAPH_TITLE, rset.getString("ISSUETITLE"), ht), dataFormat, Keys.MONOGRAPH_TITLE, Keys.RIS_BT);
                formatRIS(buildField(Keys.VOLUME, getVolume(rset.getString("VOLUME"), perl), ht), dataFormat, Keys.VOLUME, Keys.RIS_VL);
                formatRIS(buildField(Keys.ISSUE, getIssue(rset.getString("ISSUE"), perl), ht), dataFormat, Keys.ISSUE, Keys.RIS_IS);
                formatRISSerialTitle(buildField(Keys.SERIAL_TITLE, rset.getString("SOURCETITLE"), ht), dataFormat, Keys.SERIAL_TITLE,
                    getDocumentType(rset.getString("CITTYPE"), rset.getString("CONFCODE")));
                if (rset.getString("CODEN") != null) {
                    buildField(Keys.CODEN, BdCoden.convert(rset.getString("CODEN")), ht);
                }
                // do not call build field, getIssn will return the
                // ElementDataMap
                formatRIS(getIssn(rset.getString("ISSN"), rset.getString("EISSN"), ht, dataFormat), dataFormat, Keys.ISSN, Keys.RIS_SN);

                buildField(Keys.ABBRV_SERIAL_TITLE, rset.getString("SOURCETITLEABBREV"), ht);

                formatRIS(buildField(Keys.RIS_M1, database.getName(), ht), dataFormat, Keys.RIS_M1, Keys.RIS_M1);

                buildField(Keys.VOLISSUE, getVolumeIssue(rset.getString("VOLUME"), rset.getString("ISSUE")), ht);

                if (database.getMask() == 1024 && rset.getString("SOURC") != null && dataFormat.equals(FullDoc.FULLDOC_FORMAT)) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.substituteChars(rset.getString("SOURC"))));
                } else if (dataFormat.equals(Citation.CITATION_FORMAT) || dataFormat.equals(Abstract.ABSTRACT_FORMAT)
                    || dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT)) {
                    buildField(
                        Keys.SOURCE,
                        getSource(database.getMask(), rset.getString("SOURC"), rset.getString("SOURCETITLE"), rset.getString("SOURCETITLEABBREV"),
                            rset.getString("ISSUETITLE"), rset.getString("PUBLISHERNAME")), ht);

                    buildField(
                        Keys.NO_SO,
                        getNoSource(database.getMask(), rset.getString("SOURC"), rset.getString("SOURCETITLE"), rset.getString("SOURCETITLEABBREV"),
                            rset.getString("ISSUETITLE"), rset.getString("PUBLISHERNAME")), ht);

                }

                String strTitle = getCitationTitle(rset.getString("CITATIONTITLE"));
                if (strTitle == null) {
                    formatRIS(buildField(Keys.TITLE, getTranslatedCitationTitle(rset.getString("CITATIONTITLE")), ht), dataFormat, Keys.TITLE, Keys.RIS_TI);
                } else {
                    formatRIS(buildField(Keys.TITLE, strTitle, ht), dataFormat, Keys.TITLE, Keys.RIS_TI);
                    formatRIS(buildField(Keys.TITLE_TRANSLATION, getTranslatedCitationTitle(rset.getString("CITATIONTITLE")), ht), dataFormat,
                        Keys.TITLE_TRANSLATION, Keys.RIS_T1);
                }

                formatRIS(buildField(Keys.ISBN, getIsbn(rset.getString("ISBN"), 10), ht), dataFormat, Keys.ISBN, Keys.RIS_BN);
                formatRIS(buildField(Keys.ISBN13, getIsbn(rset.getString("ISBN"), 13), ht), dataFormat, Keys.ISBN, Keys.RIS_BN);

                if (!("PA").equalsIgnoreCase(strDocType) || ("PA").equalsIgnoreCase(strDocType)
                    && !(dataFormat.equals(Abstract.ABSTRACT_FORMAT) || dataFormat.equals(Citation.CITATION_FORMAT))) {
                    buildField(Keys.PAGE_COUNT, getPageCount(rset.getString("PAGECOUNT")), ht);
                    formatRIS(buildField(Keys.PUBLICATION_YEAR, getYear(rset.getString("PUBLICATIONYEAR"), perl), ht), dataFormat, Keys.PUBLICATION_YEAR,
                        Keys.RIS_PY);
                    buildField(
                        Keys.PAGE_RANGE,
                        getPageRange(dataFormat, rset.getString("PAGE"), rset.getString("ARTICLENUMBER"), rset.getString("ISSN"), rset.getString("EISSN"), perl),
                        ht);
                }

                buildField(Keys.ARTICLE_NUMBER, rset.getString("ARTICLENUMBER"), ht);

                Key publisherKey = Keys.PUBLISHER;

                if (dataFormat.equals(Abstract.ABSTRACT_FORMAT)) {
                    publisherKey = Keys.I_PUBLISHER;
                }

                formatRIS(buildField(publisherKey, getPublisher(rset.getString("PUBLISHERNAME"), rset.getString("PUBLISHERADDRESS"), dataFormat, ht), ht),
                    dataFormat, Keys.PUBLISHER, Keys.RIS_PB);
                formatRIS(buildField(Keys.LANGUAGE, getLanguage(rset.getString("CITATIONLANGUAGE"), dataFormat), ht), dataFormat, Keys.LANGUAGE, Keys.RIS_LA);
                formatRIS(buildField(Keys.AUTHORS, getAuthors(Keys.AUTHORS, rset.getString("AUTHOR"), rset.getString("AUTHOR_1"), dataFormat), ht), dataFormat,
                    Keys.AUTHORS, Keys.RIS_AUS);

                formatRIS(
                    buildField(Keys.AUTHOR_AFFS,
                        getAuthorsAffiliation(Keys.AUTHOR_AFFS, rset.getString("AFFILIATION"), rset.getString("AFFILIATION_1"), dataFormat), ht), dataFormat,
                    Keys.AUTHOR_AFFS, Keys.RIS_AD);

                buildField(Keys.PROVIDER, PROVIDER_TEXT, ht);

                formatRISDocType(buildField(Keys.DOC_TYPE, getDocumentType(rset.getString("CITTYPE"), rset.getString("CONFCODE")), ht), dataFormat,
                    Keys.DOC_TYPE, Keys.RIS_TY);
                formatRIS(
                    buildField(Keys.EDITORS, getEditors(Keys.EDITORS, rset.getString("AUTHOR"), rset.getString("EDITORS"), rset.getString("CONFERENCEEDITOR")),
                        ht), dataFormat, Keys.EDITORS, Keys.RIS_EDS);
                buildField(Keys.VOLUME_TITLE, rset.getString("VOLUMETITLE"), ht);
                // buildField(Keys.PAPER_NUMBER,rset.getString("REPORTNUMBER"),ht);
                // chem
                if (database.getMask() != 128 && database.getMask() != 1024) {
                    formatRIS(buildField(Keys.CONTROLLED_TERMS, setElementData(rset.getString("CONTROLLEDTERM")), ht), dataFormat, Keys.CONTROLLED_TERMS,
                        Keys.RIS_CVS);
                } else if (database.getMask() != 1024) {
                    formatRIS(buildField(Keys.CONTROLLED_TERMS, setElementData(rset.getString("CHEMICALTERM")), ht), dataFormat, Keys.CONTROLLED_TERMS,
                        Keys.RIS_CVS);
                }

                buildField(Keys.PATNUM, rset.getString("PATNO"), ht);
                buildField(Keys.PATAPPNUM, rset.getString("APPLN"), ht);
                buildField(Keys.PATASSIGN, rset.getString("ASSIG"), ht);

                if (!dataFormat.equals(Citation.CITATION_FORMAT) && !dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT)) {
                    buildField(Keys.CONF_CODE, rset.getString("CONFCODE"), ht);
                    buildField(Keys.NUMBER_OF_REFERENCES, rset.getString("REFCOUNT"), ht);
                    buildField(Keys.TREATMENTS, setTreatments(rset.getString("TREATMENTCODE"), database), ht);

                    if (database.getMask() != 1024) {
                        formatRIS(buildField(Keys.ABSTRACT, getAbstract(rset), ht), dataFormat, Keys.ABSTRACT, Keys.RIS_N2);
                    } else {
                        String secondarySrc = StringUtil.substituteChars(rset.getString("SECSOURCE"));
                        String secondaryTitle = StringUtil.substituteChars(rset.getString("SECSOURCETITLE"));
                        String cc = StringUtil.substituteChars(rset.getString("CLASSIFICATIONDESC"));

                        if (!perl.match("/Chemical Abstracts/i", secondarySrc) && !perl.match("/Chemical Abstracts/i", secondaryTitle)) {
                            formatRIS(buildField(Keys.ABSTRACT, getAbstract(rset), ht), dataFormat, Keys.ABSTRACT, Keys.RIS_N2);
                        } else {
                            if (perl.match("/Oil Field Chemicals/i", cc)) {
                                formatRIS(buildField(Keys.ABSTRACT, getAbstract(rset), ht), dataFormat, Keys.ABSTRACT, Keys.RIS_N2);
                            }
                        }
                    }
                    buildField(Keys.SPONSOR, setSponsorData(rset.getString("CONFSPONSORS")), ht);
                    formatRIS(buildField(Keys.START_PAGE, getStartPage(rset.getString("PAGE")), ht), dataFormat, Keys.START_PAGE, Keys.RIS_SP);
                    formatRIS(buildField(Keys.END_PAGE, getEndPage(rset.getString("PAGE")), ht), dataFormat, Keys.END_PAGE, Keys.RIS_EP);

                    // formatRISDocType(buildField(Keys.DOC_TYPE,getDocumentType(rset.getString("CITTYPE"),rset.getString("CONFCODE")),ht),dataFormat,Keys.DOC_TYPE,Keys.RIS_TY);
                    // display doc type for all format
                    formatRIS(buildField(Keys.CONFERENCE_NAME, rset.getString("CONFNAME"), ht), dataFormat, Keys.CONFERENCE_NAME, Keys.RIS_BT);
                    formatRIS(buildField(Keys.CONF_DATE, getConferenceDate(rset.getString("CONFDATE")), ht), dataFormat, Keys.CONF_DATE, Keys.RIS_MD);
                    formatRIS(buildField(Keys.MEETING_LOCATION, getConferenceLocation(rset.getString("CONFLOCATION")), ht), dataFormat, Keys.MEETING_LOCATION,
                        Keys.RIS_CY);
                    buildField(Keys.REGION_CONTROLLED_TERMS, setElementData(rset.getString("REGIONALTERM")), ht);
                    buildField(new Key(Keys.UNCONTROLLED_TERMS, "Species terms"), setElementData(rset.getString("SPECIESTERM")), ht);
                    // chem
                    if (database.getMask() != 128 && database.getMask() != 1024) {
                        buildField(Keys.CLASS_CODES, getClassification(Keys.CLASS_CODES, rset.getString("CLASSIFICATIONCODE"), database), ht);
                        formatRIS(buildField(Keys.MAIN_HEADING, getMainTerm(rset.getString("MAINHEADING")), ht), dataFormat, Keys.MAIN_HEADING, Keys.RIS_KW);
                    } else if (database.getMask() == 1024) {
                        buildField(Keys.CLASS_CODES_MULTI, new XMLMultiWrapper(ELT_CLASS_CODES, setElementData(rset.getString("CLASSIFICATIONDESC"))), ht);
                        // formatRIS(buildField(Keys.MAIN_HEADING,rset.getString("MAINHEADING"),ht),
                        // dataFormat,Keys.MAIN_HEADING, Keys.RIS_KW);

                    }

                    String apict = rset.getString("APICT");
                    String apict1 = rset.getString("APICT1");

                    CVTerms cvterms = null;
                    if (apict != null && !apict.trim().equals("")) {
                        if (apict1 != null && !apict1.trim().equals("")) {
                            apict = apict.concat(apict1);
                        }
                        cvterms = new CVTerms(apict);
                        cvterms.parse();
                    }

                    if (cvterms != null) {
                        String cvtstr = cvterms.getCvexpandstr();
                        String cvtmjr = cvterms.getCvmexpandstr();
                        if (cvtstr != null && cvtstr.length() > 0) {
                            ht.put(ELT_CONTROLLED_TERMS, new XMLMultiWrapper2(ELT_CONTROLLED_TERMS, setCVS(StringUtil.substituteChars(cvtstr))));
                            formatRIS(ht, dataFormat, Keys.CONTROLLED_TERMS, Keys.RIS_CVS);
                        }
                        if (cvtmjr != null && cvtmjr.length() > 0) {
                            ht.put(ELT_MAJOR_TERMS, new XMLMultiWrapper2(ELT_MAJOR_TERMS, setCVS(StringUtil.substituteChars(cvtmjr))));
                            formatRIS(ht, dataFormat, ELT_MAJOR_TERMS, Keys.RIS_CVMS);
                        }

                    }

                    if (rset.getString("APIATM") != null) {
                        ht.put(Keys.INDEXING_TEMPLATE,
                            new XMLWrapper(Keys.INDEXING_TEMPLATE, replaceBar(formatATM(StringUtil.substituteChars(rset.getString("APIATM"))))));
                    }

                    String linkedTerms = StringUtil.replaceNullWithEmptyString(rset.getString("APILT"));
                    if (!linkedTerms.equals("") && !linkedTerms.equalsIgnoreCase("QQ")) {
                        String apiltowerflow = rset.getString("APILT1");
                        if (apiltowerflow != null && !apiltowerflow.trim().equals("")) {
                            linkedTerms = linkedTerms.concat(StringUtil.replaceNullWithEmptyString(apiltowerflow));
                        }
                        ht.put(Keys.LINKED_TERMS, new XMLWrapper(Keys.LINKED_TERMS, StringUtil.substituteChars(removePoundSign(replaceDelim(linkedTerms)))));
                    }

                    if (rset.getString("APIATM") != null) {

                        if (rset.getString("APILTM") != null) {
                            String ltm = rset.getString("APILTM");

                            if (!ltm.equalsIgnoreCase("QQ")) {
                                ht.put(Keys.MANUAL_LINKED_TERMS, new XMLWrapper(Keys.MANUAL_LINKED_TERMS, (StringUtil.substituteChars(replaceDelim(ltm)))));
                            }
                        }
                    } // AN

                    formatRIS(buildField(Keys.UNCONTROLLED_TERMS, setElementData(rset.getString("UNCONTROLLEDTERM")), ht), dataFormat, Keys.UNCONTROLLED_TERMS,
                        Keys.RIS_FLS);
                    buildField(Keys.ABSTRACT_TYPE, getAbstractType(rset.getString("ABSTRACTORIGINAL")), ht);
                    buildField(Keys.MEDIA, rset.getString("MEDIA"), ht);
                    buildField(Keys.CSESS, rset.getString("CSESS"), ht);
                    buildField(Keys.PATNUM, rset.getString("PATNO"), ht);
                    buildField(Keys.PATAPPNUM, rset.getString("APPLN"), ht);
                    buildField(Keys.PLING, rset.getString("PLING"), ht);
                    buildField(Keys.PRIORITY_INFORMATION, rset.getString("PRIOR_NUM"), ht);
                    buildField(Keys.PCODE, rset.getString("PCODE"), ht);
                    buildField(Keys.NUMBER_OF_CLAIMS, rset.getString("CLAIM"), ht);
                    // pch
                    if (database.getMask() == 64) {
                        buildField(Keys.SECONDARY_SOURCE, rset.getString("SOURC"), ht);
                    } else if (database.getMask() == 1024) {

                        if (rset.getString("SECSOURCETITLE") != null) {
                            StringBuffer strSecSour = new StringBuffer();
                            strSecSour.append(rset.getString("SECSOURCETITLE"));
                            if (rset.getString("SECVOLUME") != null) {
                                strSecSour.append(" ").append(formatVolume(rset.getString("SECVOLUME")));
                            }
                            if (rset.getString("SECISSUE") != null) {
                                strSecSour.append("(").append(formatIssue(rset.getString("SECISSUE"))).append(")");
                            }
                            if (rset.getString("SEC") != null) {
                                strSecSour.append(" ").append(formatAbstrNumber(rset.getString("SEC")));
                            }
                            if (rset.getString("SECPUBDATE") != null) {
                                strSecSour.append(" (").append(formatJournalSourcePub(rset.getString("SECPUBDATE"))).append(")");
                            }
                            buildField(new Key(Keys.SECONDARY_SOURCE, "Secondary source"), strSecSour.toString(), ht);

                            // remove source
                        }

                        else {
                            buildField(new Key(Keys.SECONDARY_SOURCE, "Secondary source"), rset.getString("SECSOURCE"), ht);
                        }

                    }
                    buildField(Keys.NUMBER_OF_FIGURES, rset.getString("NOFIG"), ht);
                    buildField(Keys.NUMBER_OF_TABLES, rset.getString("NOTAB"), ht);
                    buildField(Keys.SUB_INDEX, rset.getString("SUB_INDEX"), ht);
                    buildField(Keys.SPECIFIC_NAMES, rset.getString("SPECN"), ht);
                    buildField(Keys.SUPPL, rset.getString("SUPPL"), ht);
                    buildField(Keys.PDFIX, rset.getString("PDFIX"), ht);
                    buildField(Keys.REPORT_NUMBER_PAPER, rset.getString("REPORTNUMBER"), ht);

                    buildField(Keys.CAS_REGISTRY_CODES, new CRNumStrategy(rset.getString("CASREGISTRYNUMBER")).getCRN(database.getMask()), ht);
                }

                // buildField(Keys.PI,rset.getString("PII"),ht);

                if (rset.getString("CORRESPONDENCENAME") != null || rset.getString("CORRESPONDENCEEADDRESS") != null) {
                    buildField(Keys.CORRESPONDING_AUTHORS,
                        getCorAuthors(Keys.CORRESPONDING_AUTHORS, rset.getString("CORRESPONDENCENAME"), rset.getString("CORRESPONDENCEEADDRESS")), ht);
                }

                if (rset.getString("AFFILIATION") == null) {
                    buildField(Keys.CORRESPONDING_AUTHORS_AFF, getCorrespondingAuAff(rset.getString("CORRESPONDENCEAFFILIATION")), ht);
                }

                if (database.getMask() == 1024 && rset.getString("CORRESPONDENCEAFFILIATION") != null) {
                    buildField(Keys.CORRESPONDING_AUTHORS_AFF, getCorrespondingAuAff(rset.getString("CORRESPONDENCEAFFILIATION")), ht);
                }

                list.add(eiDoc);
                count++;

            }
        } catch (SQLException e) {
            throw new DocumentBuilderException(e);
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException(e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException(e);
        } catch (UnsupportedEncodingException e) {
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
            rset = stmt.executeQuery(previewQueryBD + INString);
            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_PREVIEW);

                ht.put(Keys.DOCID, (DocID) oidTable.get(rset.getString("M_ID")));
                ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, getAbstract(rset)));
                list.add(eiDoc);

            }
        } catch (ConnectionPoolException e) {
            throw new DocumentBuilderException("database connection pool problem", e);
        } catch (NoConnectionAvailableException e) {
            throw new DocumentBuilderException("database connection pool avilable problem", e);
        } catch (SQLException e) {
            throw new DocumentBuilderException("database fetch problem", e);
        } catch (Exception e) {
            throw new DocumentBuilderException("database fetch problem", e);
        } finally {

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
        return list;
    }

    public String replaceBar(String str) {
        String result = perl.substitute("s/\\|/<\\/br>/g", str);
        return result;
    }

    public String formatATM(String str) {

        if (str == null)
            return "";

        String template = perl.substitute("s/A\\$\\$/TEMPLATE TITLE: /g", str);
        template = perl.substitute("s/P\\$\\$/TEMPLATE TITLE: /g", template);
        template = perl.substitute("s/\\$\\$/TEMPLATE TITLE: /g", template);
        template = perl.substitute("s/##/NUMBER OF TEMPLATE-GENERATED LINK TERMS: /g", template);
        template = perl.substitute("s/0//g", template);
        StringBuffer sbTemplate = new StringBuffer();

        List<String> lstTokens = new ArrayList<String>();

        perl.split(lstTokens, "/\\|/", template);

        int numOfTplts = 0;

        for (int i = 0; i < lstTokens.size(); i++) {
            String term = (String) lstTokens.get(i);

            if (perl.match("/TEMPLATE TITLE:/i", term))
                ++numOfTplts;
        }

        if (numOfTplts >= 1) {
            for (int i = 0; i < lstTokens.size(); i++) {
                String term = (String) lstTokens.get(i);

                if (perl.match("/TEMPLATE TITLE:/i", term)) {

                    if (i == 0)
                        sbTemplate.append(term);
                    else
                        sbTemplate.append("<br/>").append(term);

                } else {

                    if (!perl.match("/NUMBER OF TEMPLATE-GENERATED LINK TERMS:/", term) && !perl.match("/V[0-9]/i", term) && !term.startsWith(">")
                        && !perl.match("/S[0-9]/i", term)) {
                        // sbTemplate.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").
                        sbTemplate.append(term);

                    } else {
                        sbTemplate.append(term);
                    }

                }
                if (i > 0)
                    sbTemplate.append("|");
            }

            String result = sbTemplate.toString();
            result = result.replaceAll("</br>", "<br>");
            result = result.replaceAll("<br/>", "<br>");
            return result;
        } else {
            template = template.replaceAll("</br>", "<br>");
            template = template.replaceAll("<br/>", "<br>");
            return template;
        }
    }

    public static String removePoundSign(String ct) {

        Perl5Util perl = new Perl5Util();

        if (ct == null)
            return "";
        String parsedCT = perl.substitute("s/\\#//g", ct);
        String gd = new String(Constants.GROUPDELIMITER);
        String id = new String(Constants.IDDELIMITER);
        parsedCT = perl.substitute("s/" + gd + "/|/g", parsedCT);
        parsedCT = perl.substitute("s/" + id + "/;/g", parsedCT);

        // parsedCT = perl.substitute("s/IDDELIMITER/;/g", parsedCT);

        return parsedCT;

    }

    // BdCoden.convert

    @SuppressWarnings("unused")
    private String getEmail(String email) {
        if (email != null && email.indexOf(Constants.IDDELIMITER) > -1) {
            email = email.substring(email.indexOf(Constants.IDDELIMITER) + 1);
        }
        return email;
    }

    private String getAbstractType(String abstractType) {
        if (abstractType != null && abstractType.equalsIgnoreCase("n")) {
            return "(Edited Abstract)";
        }
        return null;
    }

    private String getLanguage(String languageString, String dataFormat) {

        if (languageString != null) {
            if ((dataFormat.equals(Citation.CITATION_FORMAT) || dataFormat.equals(Abstract.ABSTRACT_FORMAT))
                && (languageString.equals("en") || languageString.equals("eng") || languageString.equalsIgnoreCase("english"))) {
                return null;
            }

            return Language.getIso639Language(languageString);
        }

        return null;

    }

    private String getVolumeIssue(String volume, String issue)  {
        String strVolIss = StringUtil.EMPTY_STRING;
        ;
        if (volume != null) {
            strVolIss = strVolIss.concat(replaceVolumeNullWithEmptyString(volume));
        }

        if (issue != null) {
            if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(issue));
            } else {
                strVolIss = replaceIssueNullWithEmptyString(issue);
            }
        }

        if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
            return strVolIss;
        }
        return null;
    }

    private String getSource(int BitMaskNumber, String sourc, String sourceTitle, String sourceTitleAbbrev, String issueTitle, String publisherName) {
        String source = null;
        if (BitMaskNumber == 1024 && sourc != null) {
            return sourc;
        } else if (sourceTitle != null || sourceTitleAbbrev != null || issueTitle != null || publisherName != null) {
            if (sourceTitle != null) {
                source = sourceTitle;
            } else if (sourceTitleAbbrev != null) {
                source = sourceTitleAbbrev;
            } else if (issueTitle != null) {
                source = issueTitle;
            } else if (publisherName != null) {
                source = publisherName;
            }
        }
        return source;
    }

    private String getNoSource(int BitMaskNumber, String sourc, String sourceTitle, String sourceTitleAbbrev, String issueTitle, String publisherName) {
        if (BitMaskNumber == 1024 && sourc == null && sourceTitle == null && sourceTitleAbbrev == null && issueTitle == null && publisherName == null) {
            return "NO_SO";
        } else if (BitMaskNumber != 1024 && sourceTitle == null && sourceTitleAbbrev == null && issueTitle == null && publisherName == null) {
            return "NO_SO";
        }
        return null;
    }

    private ElementData getYear(String year, Perl5Util perl) {
        if (year != null) {
            Year yearObject = new Year(year.trim(), perl);
            return yearObject;
        }
        return null;
    }

    private static final Pattern pubdateregex = Pattern.compile("(\\w+) (\\d{2}),(\\d{4})");

    private String getConferenceDate(String cdate) {
        if (cdate != null && !cdate.trim().equals("")) {
            String[] confDates = cdate.split("-");
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < confDates.length; i++) {
                result.append(reFormatDate(confDates[i]));
                if (i < confDates.length - 1) {
                    result.append(" - ");
                }
            }
            return result.toString();
        }
        return cdate;
    }

    private String reFormatDate(String pubdate) {
        if (pubdate != null) {
            Matcher m = pubdateregex.matcher(pubdate);
            if (m.find() && m.groupCount() == 3) {
                String days = m.group(2);
                // check for days which are formatted like July 01
                if (days.startsWith("0")) {
                    // trim off the leading zero to change to July 1
                    days = days.substring(1);
                }
                pubdate = m.group(1).concat(" ").concat(days).concat(", ").concat(m.group(3));

            }
        }
        return pubdate;
    }

    private ElementData getVolume(String volume, Perl5Util perl) {
        if (volume != null) {
            Volume volumeObject = new Volume(volume.trim(), perl);
            return volumeObject;
        }
        return null;
    }

    private ElementData getIssue(String issue, Perl5Util perl) {
        if (issue != null) {
            Issue issueObject = new Issue(issue.trim(), perl);
            return issueObject;
        }
        return null;
    }

    private EIDoc newEIDoc(DocID did, String dataFormat, ElementDataMap ht) {
        EIDoc eiDoc = null;
        if (dataFormat.equals(Citation.CITATION_FORMAT)) {
            eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
            eiDoc.exportLabels(false);
            eiDoc.setOutputKeys(CITATION_KEYS);
        } else if (dataFormat.equals(Abstract.ABSTRACT_FORMAT)) {
            eiDoc = new EIDoc(did, ht, Abstract.ABSTRACT_FORMAT);
            eiDoc.exportLabels(true);
            eiDoc.setOutputKeys(ABSTRACT_KEYS);
        } else if (dataFormat.equals(FullDoc.FULLDOC_FORMAT)) {
            eiDoc = new EIDoc(did, ht, Detail.FULLDOC_FORMAT);
            eiDoc.exportLabels(true);
            eiDoc.setOutputKeys(DETAILED_KEYS);
        } else if (dataFormat.equalsIgnoreCase("RIS")) {
            eiDoc = new EIDoc(did, ht, RIS.RIS_FORMAT);
            eiDoc.exportLabels(false);
            eiDoc.setOutputKeys(RIS_KEYS);
        } else if (dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT)) {
            eiDoc = new EIDoc(did, ht, Citation.XMLCITATION_FORMAT);
            eiDoc.exportLabels(false);
            eiDoc.setOutputKeys(XML_KEYS);
        }
        return eiDoc;
    }

    private ElementDataMap buildField(Key key, ElementData data, ElementDataMap ht)  {
        if (data != null && data.getElementData() != null) {
            ht.put(key, data);
        }

        return ht;
    }

    private ElementDataMap buildField(Key key, String data, ElementDataMap ht)  {
        if (data != null && (data.trim()).length() > 0) {
            ht.put(key, new XMLWrapper(key, data.trim()));
        }
        return ht;
    }

    private ElementDataMap buildField(Key key, String[] data, ElementDataMap ht)  {
        if (data != null && data.length > 0) {
            ht.put(key, new XMLMultiWrapper(key, data));
        }
        return ht;

    }

    private String getFirstPublisher(String pubName) {
        if (pubName != null) {
            int i = pubName.indexOf(Constants.AUDELIMITER);
            if (i > -1) {
                return pubName.substring(0, i);
            } else {
                return pubName;
            }
        }

        return null;
    }

    private String getPublisher(String name, String address, String dataFormat, ElementDataMap ht)  {
        String outputString = null;
        name = getFirstPublisher(name);
        StringBuffer addressBuffer = new StringBuffer();
        if (dataFormat.equals(FullDoc.FULLDOC_FORMAT) && address != null) {
            if (address.indexOf(Constants.IDDELIMITER) > -1) {
                BdConfLocations aff = new BdConfLocations(address);
                List<BdAffiliation> aList = aff.getAffiliations();
                for (int i = 0; i < aList.size(); i++) {
                    BdAffiliation bdaff = (BdAffiliation) aList.get(i);
                    addressBuffer.append(bdaff.getDisplayValue());
                }
                address = addressBuffer.toString();
            } else if (address.indexOf(Constants.AUDELIMITER) > -1) {
                address.replaceAll(Constants.AUDELIMITER, ", ");
            }

            if (name != null && name.length() > 0 && address != null && address.length() > 0) {
                outputString = name + ", " + address;
            } else if (name != null && name.length() > 0) {
                outputString = name;
            } else if (address != null && address.length() > 0) {
                outputString = address;
            }

            return outputString;
        } else if (dataFormat.equals("RIS")) {
            if (address != null) {
                if (address.indexOf(Constants.IDDELIMITER) > -1) {
                    BdConfLocations aff = new BdConfLocations(address);
                    List<BdAffiliation> aList = aff.getAffiliations();
                    for (int i = 0; i < aList.size(); i++) {
                        BdAffiliation bdaff = (BdAffiliation) aList.get(i);
                        addressBuffer.append(bdaff.getDisplayValue());
                    }
                    address = addressBuffer.toString();
                } else if (address.indexOf(Constants.AUDELIMITER) > -1) {
                    address.replaceAll(Constants.AUDELIMITER, ", ");
                }

                ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, address));
            }

            return name;
        } else {
            return name;
        }
    }

    private String getCitationTitle(String titleString)  {
        String title = null;

        if (titleString != null && titleString.length() > 0) {
            BdCitationTitle bdTitle = new BdCitationTitle(titleString);
            List<BdCitationTitle> bdTitleList = bdTitle.getCitationTitle();
            if (bdTitleList != null) {
                for (int i = 0; i < bdTitleList.size(); i++) {
                    BdCitationTitle titleObject = (BdCitationTitle) bdTitleList.get(i);
                    if (titleObject.getTitle() != null) {
                        title = titleObject.getTitle();
                        break;
                    }
                }
            }
        }

        title = cleanBadCharacters(title);

        return title;
    }

    private String getDocumentType(String ct, String confCode)  {
        boolean confCodeFlag = false;
        if (confCode != null) {
            confCodeFlag = true;
        }
        if (ct != null) {
            return replaceDTNullWithEmptyString(BdDocumentType.getDocType(ct, confCodeFlag));
        } else {
            return null;
        }
    }

    private String getStartPage(String page) {
        String strPage = StringUtil.EMPTY_STRING;

        if (page != null) {
            String[] pages = setElementData(page);
            if (pages[0] != null && pages[0].length() > 0) {
                strPage = pages[0];
                if (strPage.indexOf("-") > 0) {
                    strPage = strPage.substring(0, strPage.indexOf("-"));
                }
            } else {
                if (pages[1] != null) {
                    strPage = pages[1];
                }
            }
        }
        return strPage.trim();
    }

    private String getEndPage(String page) {
        String strPage = StringUtil.EMPTY_STRING;

        if (page != null) {
            String[] pages = setElementData(page);

            if (pages[0] != null && pages[0].length() > 0) {
                strPage = pages[0];
                if (strPage.indexOf("-") > -1) {
                    strPage = strPage.substring(strPage.indexOf("-") + 1);
                }
            } else {
                if (pages[2] != null) {
                    strPage = pages[2];
                }
            }

        }
        return strPage.trim();
    }

    private PageRange getPageRange(String dataFormat, String page, String articleNumber, String issn, String eissn, Perl5Util perl)  {
        String strPage = null;

        if (page != null) {
            String[] pages = setElementData(page);

            if (pages[0] != null && pages[0].length() > 0) {
                strPage = pages[0];
            } else {
                if ((pages[1] != null && pages[1].trim().length() > 0) && (pages[2] != null && pages[2].trim().length() > 0)) {
                    strPage = (pages[1] + "-" + pages[2]);
                } else if (pages[1] != null && pages[1].trim().length() > 0) {
                    strPage = pages[1];
                } else if (pages[2] != null && pages[2].trim().length() > 0) {
                    strPage = pages[2];
                }
            }
        }

        if (strPage != null) {
            if (dataFormat.equals(Citation.CITATION_FORMAT) || dataFormat.equals(Abstract.ABSTRACT_FORMAT)) {
                if (strPage.indexOf("p") == -1) {
                    strPage = "p " + strPage;
                }
            } else {
                if (strPage.indexOf("p") == 0) {
                    strPage = strPage.substring(1).trim();
                }
            }

            PageRange pageRange = new PageRange(strPage, perl);
            return pageRange;
        }

        return null;
    }

    private String getPageCount(String pageCountString) {
        if (pageCountString != null) {
            BdPageCount pageCount = new BdPageCount(pageCountString);
            return pageCount.getDisplayValue();
        }
        return null;
    }

    private ElementDataMap getIssn(String issn, String eissn, ElementDataMap ht, String dataFormat)  {
        if (dataFormat.equals(FullDoc.FULLDOC_FORMAT) || dataFormat.equals(Abstract.ABSTRACT_FORMAT)) {
            // if either exist include both ISSN and EISSN under separate keys
            // Separate keys are used so both EISSN and ISSN display with
            // separate labels
            // and so both EISSN and ISSN are output toXML. Both keys are listed
            // in DETAILED_KEYS

            if (issn != null) {
                ht.put(Keys.ISSN, new ISSN(issn));
            }
            if (eissn != null) {
                ht.put(Keys.E_ISSN, new ISSN(Keys.E_ISSN, eissn));
            }
        } else {
            // include the ISSN or EISSN under the ISSN key
            // since only the ISSN key is listed for all other formats
            // the Key used in creating the ISSN object will determine the label
            // used when output
            if (issn != null) {
                ht.put(Keys.ISSN, new ISSN(issn));
            } else if (eissn != null) {
                ht.put(Keys.ISSN, new ISSN(Keys.E_ISSN, eissn));
            }
        }
        return ht;
    }

    private String getIsbn(String isbn, int type)  {
        BdIsbn bdisbn = new BdIsbn(isbn);
        String isbnString = null;
        if (type == 10) {
            isbnString = bdisbn.getISBN10();
        } else if (type == 13) {
            isbnString = bdisbn.getISBN13();
        }

        return isbnString;
    }

    private String getTranslatedCitationTitle(String titleString) {
        String title = null;

        if (titleString != null && titleString.length() > 0) {
            BdCitationTitle bdTitle = new BdCitationTitle(titleString);
            List<BdCitationTitle> bdTitleList = bdTitle.getTranslatedCitationTitle();
            if (bdTitleList != null) {
                for (int i = 0; i < bdTitleList.size(); i++) {
                    BdCitationTitle titleObject = (BdCitationTitle) bdTitleList.get(i);
                    if (titleObject.getTitle() != null) {
                        title = titleObject.getTitle();
                        break;
                    }
                }
            }

        }

        title = cleanBadCharacters(title);

        return title;
    }

    private String getConferenceLocation(String confLocation) {
        if (confLocation != null) {
            BdConfLocations bdc = new BdConfLocations(confLocation);
            StringBuffer result = new StringBuffer();
            String[] cfl = bdc.getDisplayValue().split(",");
            for (int i = 0; i < cfl.length; i++) {
                String cf = cfl[i].trim();
                // jam - This code appears to be uppercasing the first character
                // and then appending the remaining characters
                // previously it was causing a String index out of range
                // exception
                // Test here to make sure we have more than a single character
                if ((cf != null) && (cf.length() > 1)) {
                    result.append(cf.substring(0, 1).toUpperCase());
                    result.append(cf.substring(1));
                    if (i < cfl.length - 1) {
                        result.append(", ");
                    }
                }
            }
            return result.toString();
        }
        return null;
    }

    private String getCorrespondingAuAff(String coAuAff) {
        if (coAuAff != null) {
            BdCorrespAffiliations aff = new BdCorrespAffiliations(coAuAff);
            List<BdCorrespAffiliation> l = aff.getAffiliations();

            if (l == null || l.size() == 0) {
                return null;
            } else {
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < l.size(); i++) {
                    BdCorrespAffiliation a = (BdCorrespAffiliation) l.get(i);
                    buf.append(a.getDisplayValue()).append(" ");
                }
                return buf.toString();
            }
        }

        return null;
    }

    private static Map<String, String> badCharacterMap = new HashMap<String, String>();
    static {
        // &die is the same as an &uml
        badCharacterMap.put("a&die;", "&#228;");
        badCharacterMap.put("e&die;", "&#235;");
        badCharacterMap.put("i&die;", "&#239;");
        badCharacterMap.put("o&die;", "&#246;");
        badCharacterMap.put("u&die;", "&#252;");

        badCharacterMap.put("A&grave;", "&#192;");
        badCharacterMap.put("A&acute;", "&#193;");
        badCharacterMap.put("A&circ;", "&#194;");
        badCharacterMap.put("A&tilde;", "&#195;");
        badCharacterMap.put("A&uml;", "&#196;");
        badCharacterMap.put("A&ring;", "&#197;");
        badCharacterMap.put("C&cedil;", "&#199;");
        badCharacterMap.put("E&grave;", "&#200;");
        badCharacterMap.put("E&acute;", "&#201;");
        badCharacterMap.put("E&circ;", "&#202;");
        badCharacterMap.put("E&uml;", "&#203;");
        badCharacterMap.put("I&grave;", "&#204;");
        badCharacterMap.put("I&acute;", "&#205;");
        badCharacterMap.put("I&circ;", "&#206;");
        badCharacterMap.put("I&uml;", "&#207;");
        badCharacterMap.put("N&tilde;", "&#209;");
        badCharacterMap.put("O&grave;", "&#210;");
        badCharacterMap.put("O&acute;", "&#211;");
        badCharacterMap.put("O&circ;", "&#212;");
        badCharacterMap.put("O&tilde;", "&#213;");
        badCharacterMap.put("O&uml;", "&#214;");
        badCharacterMap.put("O&slash;", "&#216;");
        badCharacterMap.put("S&caron;", "&#352;");
        badCharacterMap.put("U&grave;", "&#217;");
        badCharacterMap.put("U&acute;", "&#218;");
        badCharacterMap.put("U&circ;", "&#219;");
        badCharacterMap.put("U&uml;", "&#220;");
        badCharacterMap.put("Y&acute;", "&#221;");
        badCharacterMap.put("Y&uml;", "&#376;");
        badCharacterMap.put("a&grave;", "&#224;");
        badCharacterMap.put("a&acute;", "&#225;");
        badCharacterMap.put("a&circ;", "&#226;");
        badCharacterMap.put("a&tilde;", "&#227;");
        badCharacterMap.put("a&uml;", "&#228;");
        badCharacterMap.put("a&ring;", "&#229;");
        badCharacterMap.put("c&cedil;", "&#231;");
        badCharacterMap.put("e&grave;", "&#232;");
        badCharacterMap.put("e&acute;", "&#233;");
        badCharacterMap.put("e&circ;", "&#234;");
        badCharacterMap.put("e&uml;", "&#235;");
        badCharacterMap.put("i&grave;", "&#236;");
        badCharacterMap.put("i&acute;", "&#237;");
        badCharacterMap.put("i&circ;", "&#238;");
        badCharacterMap.put("i&uml;", "&#239;");
        badCharacterMap.put("n&tilde;", "&#241;");
        badCharacterMap.put("o&grave;", "&#242;");
        badCharacterMap.put("o&acute;", "&#243;");
        badCharacterMap.put("o&circ;", "&#244;");
        badCharacterMap.put("o&tilde;", "&#245;");
        badCharacterMap.put("o&uml;", "&#246;");
        badCharacterMap.put("o&slash;", "&#248;");
        badCharacterMap.put("s&caron;", "&#353;");
        badCharacterMap.put("u&grave;", "&#249;");
        badCharacterMap.put("u&acute;", "&#250;");
        badCharacterMap.put("u&circ;", "&#251;");
        badCharacterMap.put("u&uml;", "&#252;");
        badCharacterMap.put("y&acute;", "&#253;");
        badCharacterMap.put("y&uml;", "&#255;");
    }

    /*
     * see http://java.sun.com/developer/technicalArticles/releases/1.4regex/ for a full explanation of Simple Word Replacement
     */
    private static final Pattern accentregex = Pattern.compile("(\\w)(&\\w+;)");

    public String cleanBadCharacters(String strValue) {

       if(strValue != null)
       {
           Matcher m = accentregex.matcher(strValue);
           StringBuffer sb = new StringBuffer();
           boolean result = m.find();

           while(result)
           {
             String strMatch = m.group();
             String strReplace = (String) badCharacterMap.get(strMatch);
             if(strReplace == null)
             {
   			Pattern accentregex1 = Pattern.compile("(&\\w+;)");
   			Matcher m1 = accentregex1.matcher(strMatch);
   			boolean result1 = m1.find();
   			while(result1)
   			{
             		String strMatch1 = m1.group();
   				//System.out.println("MATCH1= " +Entity.prepareString(strMatch1));
   				if(Entity.prepareString(strMatch1)!=null && Entity.prepareString(strMatch1).length()>0)
   				{
   				  //good entity, do nothing
   				   strReplace  =strMatch;
   				}
   				else
   			    {
   					//bad Entity
   					// if there is no map, just replace with the intial character
   					// i.e. a,S,Z without the trailing entity so it will at least look cleaner
   					strReplace = m.group(1);
   					// do not strip off trailing &, < or > !  i.e. A&amp; will just remain A&amp;
   					if(m.group(2).equals("&amp;") || m.group(2).equals("&gt;") || m.group(2).equals("&lt;")) {
   					  strReplace = m.group();
   					}
   				}
   				result1 = m1.find();
   			}
             }
              // The appendReplacement method appends everything up to the next match and the replacement for that match.
             m.appendReplacement(sb, strReplace);
             result = m.find();
           }
           // The appendTail appends the strings at the end, after the last match.
           m.appendTail(sb);
           strValue = sb.toString();
       }

       return strValue;
  }

    public Contributors getAuthors(Key key, String authorString, String authorString1, String dataFormat)

     {
        StringBuffer auString = new StringBuffer();

        if (authorString != null) {
            auString.append(authorString);
        }
        if (authorString1 != null) {
            auString.append(authorString1);
        }

        if (auString.length() > 0) {
            List<Contributor> authorNames = new ArrayList<Contributor>();
            BdAuthors authors = new BdAuthors(auString.toString());
            List<BdAuthor> authorList = authors.getAuthors();
            for (int i = 0; i < authorList.size(); i++) {

                BdAuthor author = (BdAuthor) authorList.get(i);

                String auDisplayName = cleanBadCharacters(author.getDisplayName());
                String email = author.getEaddress();
                if (dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT) || dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT)) {
                    authorNames.add(new Contributor(key, auDisplayName));
                } else {
                    Contributor persons = new Contributor(key, auDisplayName, author.getAffIdList());
                    if (email != null && !email.equals("")) {
                        persons.setEmail(email);
                    }
                    authorNames.add(persons);
                }

            }
            return (new Contributors(Keys.AUTHORS, authorNames));
        }
        return null;
    }

    private ElementData getEditors(Key key, String authorString, String editorsString, String confEditor)  {
        BdEditors editors = new BdEditors(editorsString);

        List<BdAuthor> editorList = editors.getEditors();
        List<Contributor> editorNames = new ArrayList<Contributor>();
        if (authorString == null && editorsString != null) {
            for (int i = 0; i < editorList.size(); i++) {
                BdAuthor author = (BdAuthor) editorList.get(i);
                String auDisplayName = cleanBadCharacters(author.getDisplayName());
                editorNames.add(new Contributor(key, auDisplayName));
            }
            return new Contributors(Keys.EDITORS, editorNames);
        } else if (authorString == null && editorsString == null && confEditor != null) {
            editors = new BdEditors(confEditor);
            editorList = editors.getEditors();

            for (int i = 0; i < editorList.size(); i++) {
                BdAuthor author = (BdAuthor) editorList.get(i);
                String auDisplayName = cleanBadCharacters(author.getDisplayName());
                editorNames.add(new Contributor(key, auDisplayName));
            }
            return new Contributors(key, editorNames);

        } else {
            return null;
        }
    }

    private ElementData getCorAuthors(Key key, String authorString, String emailString)  {
        BdEditors editors = new BdEditors(authorString);
        List<BdAuthor> editorList = editors.getEditors();
        List<Contributor> editorNames = new ArrayList<Contributor>();

        for (int i = 0; i < editorList.size(); i++) {
            BdAuthor author = (BdAuthor) editorList.get(i);
            String auDisplayName = cleanBadCharacters(author.getDisplayName());
            Contributor persons = new Contributor(key, auDisplayName);

            if (emailString != null) {
                if (emailString.indexOf("email" + Constants.IDDELIMITER) > -1) {
                    emailString = emailString.substring(emailString.indexOf(Constants.IDDELIMITER) + 1);
                }

                persons.setEmail(emailString);
            }
            editorNames.add(persons);
        }
        return new Contributors(key, editorNames);

    }

    private ElementData getClassification(Key key, String classCode, Database database)  {
        if (classCode != null) {
            return new Classifications(key, setElementData(classCode), database);
        } else {
            return null;
        }
    }

    private Affiliations getAuthorsAffiliation(Key key, String affString, String affString1, String dataFormat)  {
        StringBuffer affBuffer = new StringBuffer();

        if (affString != null) {
            affBuffer.append(affString);
        }
        if (affString1 != null) {
            affBuffer.append(affString1);
        }
        if (affBuffer.length() > 0) {
            List<Affiliation> affList = new ArrayList<Affiliation>();
            BdAffiliations aff = new BdAffiliations(affBuffer.toString());
            List<BdAffiliation> aList = aff.getAffiliations();
            for (int i = 0; i < aList.size(); i++) {
                BdAffiliation bdaff = (BdAffiliation) aList.get(i);
                String strAffDisplay = cleanBadCharacters(bdaff.getDisplayValue());

                if (i == 0 && dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT)) {
                    // affList.add(new Affiliation(key, strAffDisplay)); //original
                    // return (new Affiliations(Keys.RIS_AD, affList)); //original
                    affList.add(new Affiliation(key, strAffDisplay, bdaff.getIdDislpayValue()));
                } else if (dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT) && i == 0) {
                    // affList.add(new Affiliation(key, strAffDisplay)); //original
                    // return (new Affiliations(Keys.AUTHOR_AFFS, affList)); //original
                    affList.add(new Affiliation(key, strAffDisplay, bdaff.getIdDislpayValue()));
                } else {
                    affList.add(new Affiliation(key, strAffDisplay, bdaff.getIdDislpayValue()));
                }
            }
            if (dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT)) {
                return (new Affiliations(Keys.RIS_AD, affList));
            } else {
                return (new Affiliations(Keys.AUTHOR_AFFS, affList));
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    private String hasAbstract(ResultSet rs) throws SQLException  {
        String abs = null;
        Clob clob = rs.getClob("AB");
        if (clob != null) {
            abs = StringUtil.getStringFromClob(clob);
        }

        if (abs == null || abs.length() < 100) {
            return null;
        } else {
            return abs;
        }
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
    private String replaceVolumeNullWithEmptyString(String str)  {
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

    private String getAbstract(ResultSet rs) throws SQLException  {
        String abs = null;
        Clob clob = rs.getClob("ABSTRACTDATA");
        if (clob != null) {
            abs = StringUtil.getStringFromClob(clob);

        }
        return abs;

    }

    @SuppressWarnings("unused")
    private static boolean hasARFix(String strISSN) // Checks ISSNs with AR
                                                    // field problems
    {
        if (issnARFix.containsKey(strISSN.replaceAll("-", ""))) {
            return true;
        } else {
            return false;
        }
    }

    private static final Map<String, String> mapDoctypeToRIScode = new HashMap<String, String>();
    {
        mapDoctypeToRIScode.put("Journal article (JA)", "JOUR");
        mapDoctypeToRIScode.put("Conference article (CA)", "CONF");
        mapDoctypeToRIScode.put("Conference proceeding (CP)", "CONF");
        mapDoctypeToRIScode.put("Monograph chapter (MC)", "CHAP");
        mapDoctypeToRIScode.put("Monograph review (MR)", "BOOK");
        mapDoctypeToRIScode.put("Report chapter (RC)", "RPRT");
        mapDoctypeToRIScode.put("Report review (RR)", "RPRT");
        mapDoctypeToRIScode.put("Dissertation (DS)", "THES");
        mapDoctypeToRIScode.put("Unpublished paper (UP)", "UNPB");
        mapDoctypeToRIScode.put("Patent (PA)", "PAT");
    }

    // jam XML document mapping, conversion to TY values
    // for RIS format - only called from loadRIS
    private String replaceTYwithRIScode(String str) {
        if (str == null || str.equals("QQ")) {
            str = StringUtil.EMPTY_STRING;
        }

        if (!str.equals(StringUtil.EMPTY_STRING) && mapDoctypeToRIScode.containsKey(str)) {
            str = (String) mapDoctypeToRIScode.get(str);
        } else {
            System.out.println("Missing RIS Code mapping for " + str);
            str = "JOUR";
        }
        return str;
    }

    private static final Map<String, String> mapAbbrevToDoctype = new HashMap<String, String>();
    {
        mapAbbrevToDoctype.put("JA", "Journal article (JA)");
        mapAbbrevToDoctype.put("CA", "Conference article (CA)");
        mapAbbrevToDoctype.put("CP", "Conference proceeding (CP)");
        mapAbbrevToDoctype.put("MC", "Monograph chapter (MC)");
        mapAbbrevToDoctype.put("MR", "Monograph review (MR)");
        mapAbbrevToDoctype.put("RC", "Report chapter (RC)");
        mapAbbrevToDoctype.put("RR", "Report review (RR)");
        mapAbbrevToDoctype.put("DS", "Dissertation (DS)");
        mapAbbrevToDoctype.put("UP", "Unpublished paper (UP)");
        mapAbbrevToDoctype.put("PA", "Patent (PA)");
        mapAbbrevToDoctype.put("IP", "Article in Press");
    }

    // TS XML document mapping, conversion to dt values 02/10/03
    private String replaceDTNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = StringUtil.EMPTY_STRING;
        }

        if (!str.equals(StringUtil.EMPTY_STRING) && mapAbbrevToDoctype.containsKey(str)) {
            str = (String) mapAbbrevToDoctype.get(str);
        } else {
            System.out.println("Missing DOCTYPE mapping for " + str);
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

    public String setSponsorData(String elementVal) {
        StringBuffer buf = new StringBuffer();
        String[] array = null;
        String result = null;
        if (elementVal != null && elementVal.trim().length() > 0) {
            if (elementVal.indexOf(Constants.AUDELIMITER) > -1) {
                array = elementVal.split(Constants.AUDELIMITER, -1);
            } else {
                array = new String[1];
                array[0] = elementVal;
            }
        }
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                buf.append(array[i]).append("; ");
            }
            result = buf.toString();
            if (result.length() > 2) {
                result = result.substring(0, result.length() - 2);
            }
        }
        return result;
    }

    private Perl5Util perl = new Perl5Util();

    public String replaceDelim(String str) {
        if (str.indexOf(Constants.IDDELIMITER) > -1 || str.indexOf(Constants.GROUPDELIMITER) > -1 || str.indexOf(Constants.AUDELIMITER) > -1) {
            String gd = new String(Constants.GROUPDELIMITER);
            String id = new String(Constants.IDDELIMITER);
            String au = new String(Constants.AUDELIMITER);
            str = perl.substitute("s/" + gd + "/;/g", str);
            str = perl.substitute("s/" + id + "/;/g", str);
            str = perl.substitute("s/" + au + "/;/g", str);
            str = perl.substitute("s/;;/|/g", str);
        }

        return str;
    }

    public String[] setEltTermsElementData(String elementVal, ArrayList<String> mcv) {
        String[] array = null;
        ArrayList<String> cv = new ArrayList<String>();

        if (elementVal != null && elementVal.trim().length() > 0) {
            if (elementVal.indexOf(Constants.IDDELIMITER) > -1) {
                array = elementVal.split(Constants.IDDELIMITER, -1);
            } else {
                array = new String[1];
                array[0] = elementVal;
            }
        }

        for (int i = 0; i < array.length; i++) {
            if (perl.match("/[*]/", array[i])) {
                mcv.add(array[i]);
            } else {
                cv.add(array[i]);
            }
        }
        return (String[]) cv.toArray(new String[0]);
    }

    public String[] setElementData(String elementVal) {
        String[] array = null;
        if (elementVal != null && elementVal.trim().length() > 0) {
            if (elementVal.indexOf(Constants.AUDELIMITER) > -1) {
                array = elementVal.split(Constants.AUDELIMITER, -1);
            } else {
                array = new String[1];
                array[0] = elementVal;
            }
        }
        return array;
    }

    public String[] setTreatments(String treatments, Database db) {
        String[] array = null;
        ArrayList<String> result = new ArrayList<String>();
        if (treatments != null && treatments.trim().length() > 0) {
            if (db != null && db.getDataDictionary() != null) {
                array = treatments.split(Constants.AUDELIMITER, -1);

                for (int j = 0; j < array.length; j++) {
                    result.add((String) db.getDataDictionary().getTreatmentTitle(array[j]));
                }
            }
            return (String[]) result.toArray(new String[0]);
        }
        return null;
    }

    public String getMainTerm(String mainTerm) {
        if (mainTerm == null) {
            return null;
        }
        String term = mainTerm;
        if (mainTerm.indexOf(Constants.AUDELIMITER) > -1) {
            term = mainTerm.substring(0, mainTerm.indexOf(Constants.AUDELIMITER));

        }

        return term;

    }

    public void formatRIS(ElementDataMap map, String dataFormat, Key ORIGINAL_KEY, Key NEW_KEY) {
        if (dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT)) {
            if (map != null) {
                ElementData ed = map.get(ORIGINAL_KEY);
                if (ed != null) {
                    ed.setKey(NEW_KEY);
                    map.put(NEW_KEY, ed);
                }
            }
        }
    }

    public void formatRISDocType(ElementDataMap map, String dataFormat, Key ORIGINAL_KEY, Key NEW_KEY) {
        if (dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT)) {
            ElementData ed = map.get(ORIGINAL_KEY);
            if (ed != null) {
                String[] elementDataArray = ed.getElementData();

                for (int i = 0; i < elementDataArray.length; i++) {
                    String strDocType = StringUtil.replaceNullWithEmptyString(elementDataArray[i]);
                    String risDocType = replaceTYwithRIScode(strDocType);
                    elementDataArray[i] = risDocType;
                }

                ed.setKey(NEW_KEY);
                ed.setElementData(elementDataArray);
                map.put(NEW_KEY, ed);
            } else {
                map.put(NEW_KEY, new XMLWrapper(NEW_KEY, "JOUR"));
            }
        }
    }

    public void formatRISSerialTitle(ElementDataMap map, String dataFormat, Key ORIGINAL_KEY, String docType) {
        if (dataFormat.equalsIgnoreCase(RIS.RIS_FORMAT) && map != null) {
            ElementData ed = map.get(ORIGINAL_KEY);

            if (ed != null) {
                // jam - If doctype is null, treat the article as a Journal
                // Article
                if ((docType == null) || docType.equals("Journal article (JA)")) {
                    ed.setKey(Keys.RIS_JO);
                    map.put(Keys.RIS_JO, ed);
                } else {
                    ed.setKey(Keys.RIS_T3);
                    map.put(Keys.RIS_T3, ed);
                }
            }

        }
    }

    private KeyValuePair[] setCVS(String cvs) {
        List<KeyValuePair> list = new ArrayList<KeyValuePair>();
        StringTokenizer st = new StringTokenizer(cvs, ";", false);
        String strToken = null;

        while (st.hasMoreTokens()) {
            strToken = st.nextToken().trim();
            if (strToken.length() > 0) {
                strToken = perl.substitute("s/\\#//g", strToken);
                KeyValuePair k = new KeyValuePair(getTermField(strToken), strToken);
                list.add(k);
            }
        }

        return (KeyValuePair[]) list.toArray(new KeyValuePair[list.size()]);

    }

    public Key getTermField(String cv) {

        Key field = null;

        if (cv.startsWith("*")) {
            if (cv.endsWith("-A"))
                field = Keys.MAJOR_REAGENT_TERM;
            else if (cv.endsWith("-P"))
                field = Keys.MAJOR_PRODUCT_TERM;
            else if (cv.endsWith("-N"))
                field = Keys.MAJOR_NOROLE_TERM;
            else
                field = Keys.MAJOR_TERM;
        } else {
            if (cv.endsWith("-A"))
                field = Keys.REAGENT_TERM;
            else if (cv.endsWith("-P"))
                field = Keys.PRODUCT_TERM;
            else if (cv.endsWith("-N"))
                field = Keys.NOROLE_TERM;
            else
                field = Keys.CONTROLLED_TERM;
        }

        return field;

    }

    public String formatJournalSourcePub(String jspub) {
        if (jspub != null && jspub.indexOf(Constants.IDDELIMITER) > -1) {
            List<String> parms = new ArrayList<String>();
            perl.split(parms, "/" + Constants.IDDELIMITER + "/", jspub);
            if (parms.size() > 1) {
                jspub = (String) parms.get(0);
            }
        }
        return jspub;
    }

    public String formatAbstrNumber(String sVal) {
        StringBuffer sbSan = new StringBuffer();
        sVal = perl.substitute("s/;//g", sVal);
        if (sVal.indexOf(":") > -1) {
            List<String> parms = new ArrayList<String>();
            perl.split(parms, "/:/", sVal);
            if (parms.size() == 2)
                sVal = (String) parms.get(1);
        }
        sbSan.append("abstract no. ").append(sVal);
        return sbSan.toString();
    }

    private String formatVolume(String str) {
        String volume = "";
        if (str == null) {
            return "";
        }
        volume = perl.substitute("s/[v,V,\\.]//g", str);
        volume = perl.substitute("s/\\^/-/g", volume);
        return volume;
    }

    private String formatIssue(String str) {
        String issue = new String();
        if (str == null) {
            return "";
        }
        issue = perl.substitute("s/[n,N,\\.]//g", str);
        return issue.toString();
    }

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
