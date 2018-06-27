package org.ei.data.c84.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.common.AuthorStream;
import org.ei.common.DataCleaner;
import org.ei.domain.Abstract;
import org.ei.domain.Affiliation;
import org.ei.domain.Affiliations;
import org.ei.domain.Citation;
import org.ei.domain.Contributor;
import org.ei.domain.Contributors;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.DocumentBuilderException;
import org.ei.domain.EIDoc;
import org.ei.domain.ElementDataMap;
import org.ei.domain.FullDoc;
import org.ei.domain.ISBN;
import org.ei.domain.ISSN;
import org.ei.domain.Issue;
import org.ei.domain.Key;
import org.ei.domain.KeyValuePair;
import org.ei.domain.Keys;
import org.ei.domain.PageRange;
import org.ei.domain.RIS;
import org.ei.domain.Treatments;
import org.ei.domain.Volume;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLMultiWrapper2;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.util.StringUtil;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class C84DocBuilder implements DocumentBuilder {
    // private static Log log = LogFactory.getLog("Constants");

    public static String C84_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String C84_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER_TEXT = "Ei";

    public static final Key CPX_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Ei controlled terms");
    public static final Key CPX_CLASS_CODES = new Key(Keys.CLASS_CODES, "Ei classification codes");
    public static final Key CPX_MAIN_HEADING = new Key(Keys.MAIN_HEADING, "Ei main heading");
    public static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.DOI, Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.SOURCE, Keys.PAGE_RANGE, Keys.VOLISSUE,
        Keys.PUBLICATION_YEAR, Keys.ISSUE_DATE, Keys.ISSN, Keys.PROVIDER, Keys.PATASSIGN, Keys.PATNUM, Keys.PATFILDATE, Keys.PATENT_ISSUE_DATE,
        Keys.PATCOUNTRY, Keys.NO_SO, Keys.LANGUAGE, Keys.MONOGRAPH_TITLE, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    public static final Key[] ABSTRACT_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.DOI, Keys.COPYRIGHT_TEXT, Keys.COPYRIGHT, Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.AUTHORS,
        Keys.AUTHOR_AFFS, Keys.EDITORS, Keys.EDITOR_AFFS, Keys.MAIN_HEADING, Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.GLOBAL_TAGS,
        Keys.PRIVATE_TAGS, Keys.ABSTRACT, Keys.NUMBER_OF_REFERENCES, Keys.ABSTRACT_TYPE, Keys.LANGUAGE, Keys.NO_SO, Keys.VOLUME, Keys.ISSUE, Keys.VOLISSUE,
        Keys.SOURCE, Keys.MONOGRAPH_TITLE, Keys.VOLUME_TITLE, Keys.PUBLISHER, Keys.I_PUBLISHER, Keys.ISSUE_DATE, Keys.PUBLICATION_DATE, Keys.PUBLICATION_YEAR,
        Keys.PAPER_NUMBER, Keys.PAGE_RANGE, Keys.VOLUME_TITLE, Keys.CONFERENCE_NAME, Keys.CONF_DATE, Keys.SPONSOR, Keys.PUB_LOCATION, Keys.CODEN, Keys.ISBN,
        Keys.ISSN, Keys.PROVIDER, Keys.REPORT_NUMBER, Keys.PATENT_ISSUE_DATE, Keys.PATASSIGN, Keys.PATNUM, Keys.PATFILDATE, Keys.PATCOUNTRY, Keys.COPYRIGHT,
        Keys.COPYRIGHT_TEXT };
    public static final Key[] DETAILED_KEYS = { Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.TITLE_TRANSLATION, Keys.AUTHORS, Keys.EDITORS, Keys.INVENTORS,
        Keys.SALUTATION, Keys.PATASSIGN, Keys.PATNUM, Keys.PATFILDATE, Keys.PATENT_ISSUE_DATE, Keys.AUTHOR_AFFS, Keys.EDITOR_AFFS, Keys.SOURCE,
        Keys.SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.VOLISSUE, Keys.VOLUME, Keys.ISSUE, Keys.MONOGRAPH_TITLE, Keys.ISSUING_ORG, Keys.REPORT_NUMBER,
        Keys.ISSUE_DATE, Keys.PUBLICATION_DATE, Keys.PUBLICATION_YEAR, Keys.PAPER_NUMBER, Keys.PAGE_RANGE, Keys.NUMBER_OF_FIGURES,
        Keys.LANGUAGE, Keys.ISSN, Keys.CODEN, Keys.ISBN, Keys.DOC_TYPE, Keys.PATCOUNTRY, Keys.CONFERENCE_NAME, Keys.CONF_DATE, Keys.MEETING_LOCATION,
        Keys.CONF_CODE, Keys.SPONSOR, Keys.PUBLISHER, Keys.PUB_PLACE, Keys.PUB_LOCATION, Keys.TRANSLATION_SERIAL_TITLE,
        Keys.TRANSLATION_ABBREVIATED_SERIAL_TITLE, Keys.TRANSLATION_VOLUME, Keys.TRANSLATION_ISSUE, Keys.TRANSLATION_PUBLICATION_DATE, Keys.TRANSLATION_PAGES,
        Keys.TRANSLATION_ISSN, Keys.TRANSLATION_CODEN, Keys.TRANSLATION_COUNTRY_OF_PUB, Keys.ACRONYM_DEFINITION, Keys.ABSTRACT, Keys.ABSTRACT_TYPE,
        Keys.NUMBER_OF_REFERENCES, Keys.MAIN_HEADING, Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.CLASS_CODES, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS,
        Keys.NUMERICAL_DATA_INDEXING, Keys.ASTRONOMICAL_OBJECT_INDEXING, Keys.CHEMICAL_DATA_INDEX, Keys.TREATMENTS, Keys.DISCIPLINES, Keys.DOI, Keys.DOCID,
        Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.PROVIDER };
    public static final Key[] RIS_KEYS = { Keys.DOCID, Keys.RIS_TY, Keys.RIS_LA, Keys.RIS_N1, Keys.RIS_TI, Keys.RIS_T1, Keys.RIS_BT, Keys.RIS_JO, Keys.RIS_T3,
        Keys.RIS_DO, Keys.RIS_AUS, Keys.RIS_AD, Keys.RIS_EDS, Keys.RIS_PY, Keys.RIS_PE, Keys.RIS_PB, Keys.RIS_Y1, Keys.RIS_Y2, Keys.RIS_CY, Keys.RIS_VL,
        Keys.RIS_IS, Keys.RIS_SP, Keys.RIS_EP, Keys.RIS_SN, Keys.RIS_S1, Keys.RIS_MD, Keys.RIS_AN, Keys.RIS_N2, Keys.RIS_KW, Keys.RIS_CVS, Keys.RIS_FLS,
        Keys.BIB_TY };
    public static final Key[] XML_KEYS = { Keys.ISSN, Keys.PATENT_ISSUE_DATE, Keys.NO_SO, Keys.MONOGRAPH_TITLE, Keys.PUBLICATION_YEAR, Keys.VOLUME_TITLE,
        Keys.ISBN, Keys.AUTHORS, Keys.DOCID, Keys.SOURCE, Keys.NUMVOL, Keys.EDITOR_AFFS, Keys.EDITORS, Keys.PUBLISHER, Keys.VOLUME, Keys.AUTHOR_AFFS,
        Keys.PROVIDER, Keys.ISSUE_DATE, Keys.COPYRIGHT_TEXT, Keys.DOI, Keys.PAGE_COUNT, Keys.PUBLICATION_DATE, Keys.TITLE, Keys.LANGUAGE, Keys.PAGE_RANGE,
        Keys.PAPER_NUMBER, Keys.PATASSIGN, Keys.PATCOUNTRY, Keys.ISSUE, Keys.COPYRIGHT, Keys.ACCESSION_NUMBER, Keys.PATNUM, Keys.CONTROLLED_TERMS,
        Keys.PATFILDATE };

    // AO TL") ???

    // document builder interface methods which are called by EIDoc classes
    // for building detailed XML views of documents

    private Perl5Util perl = new Perl5Util();
    private Database database;

    // jam added PE,PM,AD,PD,PU (patent fields) for 1884
    private static String queryXMLCitation = "select M_ID,EX,DT,TI,TT,AUS,AF,AM,AC,ASS,AV,AY,ED,EF,EM,EC,ES,EV,EY,ST,SE,VO,ISS,SD,MT,VT,PN,YR,NV,PA,XP,PP,LA,ME,SN,BN,LOAD_NUMBER, PE,PM,AD,PD,PU,CVS,DO from c84_master where M_ID IN ";
    private static String queryCitation = "select M_ID,DT,TI,TT,AUS,AF,CF,M2,MC,AM,AC,ASS,AV,AY,ED,EF,EM,EC,ES,EV,EY,ST,SE,VO,ISS,SD,MT,VT,PN,YR,NV,PA,XP,PP,LA,ME, SN, LOAD_NUMBER, PE,PM,AD,PD,PU,EX,BN,CN,DO from c84_master where M_ID IN ";
    private static String queryAbstracts = "select M_ID,EX,DT,TI,TT,AUS,AF,AM,AC,ASS,AV,AY,ED,EF,EM,EC,ES,EV,EY,ST,SE,VO,ISS,SD,MT,ME,VT,PN,YR,NV,PA,XP,PP,LA,SN,CN,BN,CF,M2,MC,MS,MV,MY,SP,SC,SS,SV,SY,PN,PC,PY,PO,AB,NR,AT,MH,SH,CVS,FLS,PS,PV, PE,PM,AD,PD,PU,DO  from c84_master where M_ID IN ";
    // added TL for 1884 detailed
    private static String queryDetailed = "select M_ID,EX,TI,TT,AUS,AF,AM,AC,ASS,AV,AY,ED,EF,EM,EC,ES,EV,EY,ST,SE,VO,ISS,SD,MT,ME,VT,PN,YR,NV,PA,XP,PP,LA,SN,CN,BN,CF,M2,MC,MS,MV,MY,SP,SC,SS,SY,PC,PY,PO,AB,NR,AT,MH,SH,CVS,DT,CC,SV,FLS,CAL,CLS,TR,PS,PV,PE,PM,AD,PD,PU,FG,TL,AO,DO  from c84_master where M_ID IN ";

    private static String queryPreview = "select M_ID, AB from c84_master where M_ID IN";

    public DocumentBuilder newInstance(Database database) {
        return new C84DocBuilder(database);
    }

    public C84DocBuilder() {
    }

    public C84DocBuilder(Database database) {
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
                ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, hasAbstract(rset)));
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
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, C84DocBuilder.C84_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, C84DocBuilder.C84_TEXT_COPYRIGHT));

                // Always needed for IVIP
                if (rset.getString("SN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("SN"))));
                }

                // Title always needed for Citation
                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("TT") != null) && (rset.getString("TI") != null)) {
                    // 1884 Special Note
                    // swap the TT and TI if they both exist - 1884 only
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TT"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TI"))).concat(")");
                } else if (rset.getString("TI") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI"));
                } else if (rset.getString("TT") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TT"));
                }

                if (strTitle != null && !StringUtil.EMPTY_STRING.equals(strTitle)) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));
                }
                strTitle = null;

                if (rset.getString("DO") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, StringUtil.replaceNullWithEmptyString(rset.getString("DO"))));
                }

                // AUS or EDS - always needed for Citation (AUS is Inventor(s) in the case of a Patent)

                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.replaceNullWithEmptyString(rset.getString("AUS")),
                        Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);

                    if (rset.getString("AF") != null) {
                        Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, StringUtil.replaceNullWithEmptyString(rset.getString("AF")));
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }

                } else {
                    if (rset.getString("ED") != null) {
                        String strED = StringUtil.replaceNullWithEmptyString(rset.getString("ED"));
                        if (perl.match("/(Ed[.]\\s*)/", strED)) {
                            strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                        }
                        if (strED != null && !strED.trim().equals(StringUtil.EMPTY_STRING)) {
                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(strED, Keys.EDITORS));
                            ht.put(Keys.EDITORS, editors);
                        }

                        if (rset.getString("EF") != null) {
                            Affiliation eaffil = new Affiliation(Keys.EDITOR_AFFS, StringUtil.replaceNullWithEmptyString(rset.getString("EF")));
                            ht.put(Keys.EDITOR_AFFS, new Affiliations(Keys.EDITOR_AFFS, eaffil));
                        }
                    }
                }

                // 1884 Patent Records
                String strDocType = StringUtil.replaceNullWithEmptyString(rset.getString("DT"));
                if ("PA".equalsIgnoreCase(strDocType)) {

                    // suppress 'Source:' label
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    if ((rset.getString("PE") != null)) {

                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, StringUtil.replaceNullWithEmptyString(rset.getString("PE"))));
                    }
                    if ((rset.getString("PM") != null)) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, StringUtil.replaceNullWithEmptyString(rset.getString("PM"))));

                    }
                    if ((rset.getString("AD") != null)) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, StringUtil.replaceNullWithEmptyString(rset.getString("AD"))));
                    }
                    if ((rset.getString("PD") != null)) {
                        ht.put(Keys.PATENT_ISSUE_DATE, new XMLWrapper(Keys.PATENT_ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PD"))));
                    }
                    if ((rset.getString("PU") != null)) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, StringUtil.replaceNullWithEmptyString(rset.getString("PU"))));
                    }

                } // "PA".equalsIgnoreCase(strDocType)
                else { // All other citations (same as CPX)

                    // SO
                    if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null
                        || rset.getString("PN") != null) {

                        // VO - VOL and ISSUE Combined by ', '
                        // add 'v' or 'n'
                        if (rset.getString("VO") != null) {
                            ht.put(Keys.VOLUME, new Volume(StringUtil.replaceNullWithEmptyString(rset.getString("VO").replace('n', ' ').replace('v', ' ')),
                                perl));
                        }
                        if (rset.getString("ISS") != null) {

                            ht.put(Keys.ISSUE, new Issue(StringUtil.replaceNullWithEmptyString(rset.getString("ISS").replace('n', ' ')), perl));

                        }

                        if (rset.getString("ST") != null || rset.getString("SE") != null) {
                            if (rset.getString("ST") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("ST"))));
                            } else if (rset.getString("SE") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("SE"))));
                            }
                            // an MT or VT can accompany the ST (or SE)
                            if (rset.getString("MT") != null) {
                                ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                            }
                            if (rset.getString("VT") != null) {
                                ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("VT"))));
                            }
                        } else if ((rset.getString("ST") == null && rset.getString("SE") == null)
                            && (rset.getString("MT") != null || rset.getString("ME") != null)) {
                            if (rset.getString("MT") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                            } else if (rset.getString("ME") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("ME"))));
                            }
                        } else if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null) {
                            if (rset.getString("PN") != null) {
                                ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PN"))));
                            }
                        }
                    }
                    if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null
                        && rset.getString("PN") == null) {
                        ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                    }

                    // jam 11/24/2003 - request from Mark T. - Suppress Issue Date if Month is
                    // missing from Issue Date value
                    String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));
                    if (perl.match("#(\\w+)\\W+(\\d+)#", strSD)) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));
                    }

                    if ((rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null || rset
                        .getString("PN") != null) && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                        if (rset.getString("YR") != null) {
                            // remove Year from SD and reformat as Month, Year
                            String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));

                            strSD = perl.substitute("s/".concat(strYR).concat("$//"), strSD);

                            if (!strSD.equals(StringUtil.EMPTY_STRING)) {
                                strSD = strSD.trim().concat(", ").concat(strYR);
                            } else {
                                strSD = strSD.concat(strYR);
                            }
                        }
                        if (strSD != null && !strSD.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));
                        }

                    } else if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("PN") == null
                            && rset.getString("ME") == null && !strYR.equals(StringUtil.EMPTY_STRING)) {

                            // if ALL 4 are null, use the label 'Publication Date'
                            ht.put(Keys.PUBLICATION_DATE, new Year(Keys.PUBLICATION_DATE, strYR, perl));

                        } else if (!strYR.equals(StringUtil.EMPTY_STRING)) {
                            // else just store the date
                            ht.put(Keys.PUBLICATION_YEAR, new Year(Keys.PUBLICATION_YEAR, strYR, perl));
                        }
                    }

                    // NV
                    if (rset.getString("NV") != null) {
                        ht.put(Keys.NUMVOL, new XMLWrapper(Keys.NUMVOL, StringUtil.replaceNullWithEmptyString(rset.getString("NV"))));
                    }
                    // PA
                    if (rset.getString("PA") != null) {

                        ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("PA"))));
                    }

                    // PP
                    if (rset.getString("XP") != null) {
                        String pageRange = rset.getString("XP").replaceAll("p", " ");
                        ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(pageRange), perl));
                    } else if (rset.getString("PP") != null) {
                        String pageCount = rset.getString("PP").replaceAll("p", " ");
                        ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, StringUtil.replaceNullWithEmptyString(pageCount)));
                    }

                    // VT
                    if (rset.getString("VT") != null) {
                        ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("VT"))));
                    }

                    // LA
                    if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LA"))));
                    }
                } // "PA".equalsIgnoreCase(strDocType)

                // CVS - common for all doctypes
                if (rset.getString("CVS") != null) {
                    ht.put(Keys.CONTROLLED_TERMS,
                        new XMLMultiWrapper(CPX_CONTROLLED_TERMS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("CVS")))));
                }

                // BN
                if (rset.getString("BN") != null) {
                    ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("BN"))));
                }

                // AN
                if (rset.getString("EX") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("EX"))));
                }

                if (rset.getString("PN") != null) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PN"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Citation.XMLCITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(XML_KEYS);
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
    }// loadXMLCitation

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
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, C84_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, C84_TEXT_COPYRIGHT));

                if (rset.getString("SN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("SN"))));
                }

                if (rset.getString("DO") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DO")));
                }

                // Title always needed for Citation
                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("TT") != null) && (rset.getString("TI") != null)) {
                    // 1884 Special Note
                    // swap the TT and TI if they both exist - 1884 only
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TT"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TI"))).concat(")");
                } else if (rset.getString("TI") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI"));
                } else if (rset.getString("TT") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TT"));
                }

                if (strTitle != null && !strTitle.equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));
                }

                strTitle = null;

                // AUS or EDS - always needed for Citation (AUS is Inventor(s) in the case of a Patent)
                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.replaceNullWithEmptyString(rset.getString("AUS")),
                        Keys.AUTHORS));
                    ht.put(Keys.AUTHORS, authors);

                    if (rset.getString("AF") != null) {
                        Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, StringUtil.replaceNullWithEmptyString(rset.getString("AF")));
                        authors.setFirstAffiliation(affil);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }
                } else {
                    if (rset.getString("ED") != null) {
                        String strED = StringUtil.replaceNullWithEmptyString(rset.getString("ED"));
                        if (perl.match("/(Ed[.]\\s*)/", strED)) {
                            strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                        }
                        Contributors editors = new Contributors(Keys.EDITORS, getContributors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);

                        if (rset.getString("EF") != null) {
                            Affiliation eaffil = new Affiliation(Keys.EDITOR_AFFS, StringUtil.replaceNullWithEmptyString(rset.getString("EF")));
                            editors.setFirstAffiliation(eaffil);
                            ht.put(Keys.EDITOR_AFFS, new Affiliations(Keys.EDITOR_AFFS, eaffil));
                        }
                    }
                }

                // 1884 Patent Records
                String strDocType = StringUtil.replaceNullWithEmptyString(rset.getString("DT"));
                if ("PA".equalsIgnoreCase(strDocType)) {

                    // suppress 'Source:' label
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    if ((rset.getString("PE") != null)) {

                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, StringUtil.replaceNullWithEmptyString(rset.getString("PE"))));
                    }
                    if ((rset.getString("PM") != null)) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, StringUtil.replaceNullWithEmptyString(rset.getString("PM"))));

                    }
                    if ((rset.getString("AD") != null)) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, StringUtil.replaceNullWithEmptyString(rset.getString("AD"))));
                    }
                    if ((rset.getString("PD") != null)) {
                        ht.put(Keys.PATENT_ISSUE_DATE, new XMLWrapper(Keys.PATENT_ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PD"))));
                    }
                    if ((rset.getString("PU") != null)) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, StringUtil.replaceNullWithEmptyString(rset.getString("PU"))));
                    }

                } // "PA".equalsIgnoreCase(strDocType)
                else { // All other citations (same as CPX)

                    // SO
                    if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null
                        || rset.getString("PN") != null) {
                        String strVolIss = StringUtil.EMPTY_STRING;

                        // VO - VOL and ISSUE Combined by ', '
                        // add 'v' or 'n'
                        if (rset.getString("VO") != null) {
                            strVolIss = strVolIss.concat(replaceVolumeNullWithEmptyString(rset.getString("VO")));

                            if (!StringUtil.EMPTY_STRING.equals(strVolIss)) {
                                ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, strVolIss, perl));
                            }

                        }
                        if (rset.getString("ISS") != null) {
                            ht.put(Keys.ISSUE, new Issue(replaceVolumeNullWithEmptyString(rset.getString("ISS")), perl));

                            if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                                strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(rset.getString("ISS")));
                            } else {
                                strVolIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                            }
                        }
                        if (!StringUtil.EMPTY_STRING.equals(strVolIss)) {
                            ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                        }

                        if (rset.getString("ST") != null || rset.getString("SE") != null) {
                            if (rset.getString("ST") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("ST"))));
                            } else if (rset.getString("SE") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("SE"))));
                            }

                            // an MT or VT can accompany the ST (or SE)
                            if (rset.getString("MT") != null) {
                                ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                            }

                            if (rset.getString("VT") != null) {
                                ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("VT"))));
                            }
                        } else if ((rset.getString("ST") == null && rset.getString("SE") == null)
                            && (rset.getString("MT") != null || rset.getString("ME") != null)) {
                            if (rset.getString("MT") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                            } else if (rset.getString("ME") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("ME"))));
                            }
                        }

                        else if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null) {
                            if (rset.getString("PN") != null) {
                                ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PN"))));
                            }
                        }
                    }
                    if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null
                        && rset.getString("PN") == null) {
                        ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                    }

                    // jam 11/24/2003 - request from Mark T. - Suppress Issue Date if Month is
                    // missing from Issue Date value
                    String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));

                    if (strSD != null && !strSD.trim().equals("") && perl.match("#(\\w+)\\W+(\\d+)#", strSD)) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));
                    }

                    if ((rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null || rset
                        .getString("PN") != null) && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                        if (rset.getString("YR") != null) {
                            // remove Year from SD and reformat as Month, Year
                            String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                            strSD = perl.substitute("s/".concat(strYR).concat("$//"), strSD);

                            if (!strSD.equals(StringUtil.EMPTY_STRING)) {
                                strSD = strSD.trim().concat(", ").concat(strYR);
                            } else {
                                strSD = strSD.concat(strYR);
                            }
                        }
                        // Year??
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));
                    } else if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("PN") == null
                            && rset.getString("ME") == null) {
                            // if ALL 4 are null, use the label 'Publication Date'
                            ht.put(Keys.PUBLICATION_DATE, new Year(Keys.PUBLICATION_DATE, strYR, perl));
                        } else {
                            // else just store the date
                            ht.put(Keys.PUBLICATION_YEAR, new Year(Keys.PUBLICATION_YEAR, strYR, perl));
                        }
                    }

                    // NV
                    if (rset.getString("NV") != null) {
                        ht.put(Keys.NUMVOL, new XMLWrapper(Keys.NUMVOL, StringUtil.replaceNullWithEmptyString(rset.getString("NV"))));
                    }
                    // PA
                    if (rset.getString("PA") != null) {
                        ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("PA"))));
                    }

                    // PP
                    if (rset.getString("XP") != null) {
                        ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(rset.getString("XP")), perl));

                    } else if (rset.getString("PP") != null) {
                        ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(rset.getString("PP")), perl));
                    }

                    // VT
                    if (rset.getString("VT") != null) {
                        ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("VT"))));
                    }

                    // LA
                    if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LA"))));
                    }

                    // AN
                    if (rset.getString("EX") != null) {
                        ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("EX"))));
                    }
                    // BN
                    if (rset.getString("BN") != null) {
                        ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("BN"))));
                    }
                    // CF
                    if ((rset.getString("CF") != null) || (rset.getString("M2") != null) || (rset.getString("MC") != null)) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("CF"))));
                    }

                    // CN
                    if (rset.getString("CN") != null) {
                        ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("CN"))));
                    }

                    if (rset.getString("ST") != null) {
                        ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("ST"))));
                    }
                    // SE
                    if (rset.getString("SE") != null) {
                        ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("SE"))));
                    }

                    if (rset.getString("YR") != null) {
                        ht.put(Keys.LINKING_YEAR, new Year(Keys.LINKING_YEAR, rset.getString("YR"), perl));
                    }

                } // "PA".equalsIgnoreCase(strDocType)

                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.setOutputKeys(CITATION_KEYS);
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

    private List<EIDoc> loadAbstracts(List<DocID> listOfDocIDs) throws DocumentBuilderException {
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();
        List<EIDoc> list = new ArrayList<EIDoc>();
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

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, C84_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, C84_TEXT_COPYRIGHT));

                if (rset.getString("EX") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("EX"))));
                }
                if (rset.getString("DO") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, StringUtil.replaceNullWithEmptyString(rset.getString("DO"))));
                }

                // TI/TT - common for all doctypes
                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("TT") != null) && (rset.getString("TI") != null)) {
                    // 1884 Special Note
                    // swap the TT and TI if they both exist - 1884 only
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TT"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TI"))).concat(")");
                } else if (rset.getString("TI") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI"));
                } else if (rset.getString("TT") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TT"));
                }
                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));
                strTitle = null;

                // AUS or EDS - common for all doctypes

                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUS"), Keys.AUTHORS));

                    ht.put(Keys.AUTHORS, authors);

                    if (rset.getString("AF") != null) {
                        Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, rset.getString("AF"));
                        authors.setFirstAffiliation(affil);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }
                } else {
                    if (rset.getString("ED") != null) {
                        String strED = StringUtil.replaceNullWithEmptyString(rset.getString("ED"));
                        if (perl.match("/(Ed[.]\\s*)/", strED)) {
                            strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                        }

                        Contributors editors = new Contributors(Keys.EDITORS, getContributors(rset.getString("ED"), Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);

                        if (rset.getString("EF") != null) {
                            Affiliation eaffil = new Affiliation(Keys.EDITOR_AFFS, StringUtil.replaceNullWithEmptyString(rset.getString("EF")));
                            editors.setFirstAffiliation(eaffil);
                            ht.put(Keys.EDITOR_AFFS, new Affiliations(Keys.EDITOR_AFFS, eaffil));
                        }
                    }
                }

                // MH - common for all doctypes
                String mainHeading = StringUtil.EMPTY_STRING;
                if ((rset.getString("MH") != null) && (rset.getString("SH") != null)) {
                    mainHeading = rset.getString("MH").concat(" -- ").concat(rset.getString("SH"));
                } else if (rset.getString("MH") != null) {
                    mainHeading = rset.getString("MH");
                } else if (rset.getString("SH") != null) {
                    mainHeading = rset.getString("SH");
                }
                if (mainHeading != null) {
                    ht.put(Keys.MAIN_HEADING, new XMLWrapper(CPX_MAIN_HEADING, StringUtil.replaceNullWithEmptyString(mainHeading)));
                }

                // CVS - common for all doctypes

                if ((rset.getString("CVS") != null) || (rset.getString("MH") != null)) {
                    ht.put(Keys.CONTROLLED_TERMS,
                        new XMLMultiWrapper2(CPX_CONTROLLED_TERMS, setCVS(mainHeading, StringUtil.replaceNullWithEmptyString(rset.getString("CVS")))));
                }

                // FLS - added in Baja to ABS view
                if (rset.getString("FLS") != null) {
                    ht.put(Keys.UNCONTROLLED_TERMS,
                        new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("FLS")))));

                }

                mainHeading = null;

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                // NR - common for all doctypes
                if (rset.getString("NR") != null) {
                    String strREFs = rset.getString("NR");
                    if (perl.match("/(\\d+)/", strREFs)) {
                        ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, perl.group(0).toString()));
                    }
                }

                // AT - common for all doctypes
                if (rset.getString("AT") != null) {
                    ht.put(Keys.ABSTRACT_TYPE, new XMLWrapper(Keys.ABSTRACT_TYPE, StringUtil.replaceNullWithEmptyString(rset.getString("AT"))));
                }

                // LA - common for all doctypes
                if ((rset.getString("LA") != null) && (!rset.getString("LA").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LA"))));
                }

                // 1884 Patent Records
                String strDocType = StringUtil.replaceNullWithEmptyString(rset.getString("DT"));
                if ("PA".equalsIgnoreCase(strDocType)) {

                    // suppress 'Source:' label
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    if ((rset.getString("PE") != null)) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, StringUtil.replaceNullWithEmptyString(rset.getString("PE"))));
                    }
                    if ((rset.getString("PM") != null)) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, StringUtil.replaceNullWithEmptyString(rset.getString("PM"))));

                    }
                    if ((rset.getString("AD") != null)) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, StringUtil.replaceNullWithEmptyString(rset.getString("AD"))));
                    }
                    if ((rset.getString("PD") != null)) {
                        ht.put(Keys.PATENT_ISSUE_DATE, new XMLWrapper(Keys.PATENT_ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PD"))));
                    }
                    if ((rset.getString("PU") != null)) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, StringUtil.replaceNullWithEmptyString(rset.getString("PU"))));
                    }

                } // "PA".equalsIgnoreCase(strDocType)
                else { // All other doctypes (same as CPX)

                    if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null
                        || rset.getString("PN") != null) {

                        String strVolIss = StringUtil.EMPTY_STRING;
                        // VO
                        if (rset.getString("VO") != null) {
                            strVolIss = strVolIss.concat(rset.getString("VO"));
                            ht.put(Keys.VOLUME, new Volume(StringUtil.replaceNullWithEmptyString(strVolIss), perl));
                        }
                        if (rset.getString("ISS") != null) {
                            ht.put(Keys.ISSUE, new Issue(StringUtil.replaceNullWithEmptyString(rset.getString("ISS")), perl));

                            if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                                strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(rset.getString("ISS")));
                            } else {
                                strVolIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                            }
                        }

                        if ((strVolIss != null) && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, StringUtil.replaceNullWithEmptyString(strVolIss)));
                        }

                        if (rset.getString("ST") != null || rset.getString("SE") != null) {

                            if (rset.getString("ST") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("ST"))));
                            } else if (rset.getString("SE") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("SE"))));
                            }
                            // an MT or VT can accompany the ST (or SE)
                            if (rset.getString("MT") != null) {
                                ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                            }
                            if (rset.getString("VT") != null) {
                                ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("VT"))));
                            }
                        } else if ((rset.getString("ST") == null && rset.getString("SE") == null)
                            && (rset.getString("MT") != null || rset.getString("ME") != null)) {

                            if (rset.getString("MT") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                            } else if (rset.getString("ME") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("ME"))));
                            }
                        } else if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null) {
                            if (rset.getString("PN") != null) {
                                ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PN"))));
                            }
                        }
                    }

                    if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null
                        && rset.getString("PN") == null) {
                        ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                    }

                    // PN
                    if (rset.getString("PN") != null) {
                        ht.put(Keys.I_PUBLISHER, new XMLWrapper(Keys.I_PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PN"))));
                    }

                    // jam 11/24/2003 - request from Mark T. - Suppress Issue Date if Month is
                    // missing from Issue Date value
                    String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));
                    if (perl.match("#(\\w+)\\W+(\\d+)#", strSD)) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, StringUtil.replaceNullWithEmptyString(strSD)));
                    }

                    if ((rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null || rset
                        .getString("PN") != null) && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                        if (rset.getString("YR") != null) {
                            // remove Year from SD and reformat as Month, Year
                            String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                            strSD = perl.substitute("s/".concat(strYR).concat("$//"), strSD);

                            if (!strSD.equals(StringUtil.EMPTY_STRING)) {
                                strSD = strSD.trim().concat(", ").concat(strYR);
                            } else {
                                strSD = strSD.concat(strYR);
                            }
                        }
                        if (strSD != null) {
                            ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));
                        }
                    } else if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("PN") == null
                            && rset.getString("ME") == null) {
                            // if ALL 4 are null, use the label 'Publication Date'
                            ht.put(Keys.PUBLICATION_DATE, new Year(Keys.PUBLICATION_DATE, StringUtil.replaceNullWithEmptyString(strYR), perl));
                        } else {
                            // else just store the date
                            ht.put(Keys.PUBLICATION_YEAR, new Year(Keys.PUBLICATION_YEAR, StringUtil.replaceNullWithEmptyString(strYR), perl));
                        }
                    }

                    // PA
                    if (rset.getString("PA") != null) {
                        ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("PA"))));
                    }

                    // PP
                    if (rset.getString("XP") != null) {
                        ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(rset.getString("XP")), perl));

                    } else if (rset.getString("PP") != null) {
                        ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(rset.getString("PP")), perl));
                    }

                    // VT
                    if (rset.getString("VT") != null) {
                        ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("VT"))));
                    }

                    // CF
                    if ((rset.getString("CF") != null) || (rset.getString("M2") != null) || (rset.getString("MC") != null)) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("CF"))));
                    }

                    // CONF INFORMATION
                    if ((rset.getString("CF") != null) || (rset.getString("M2") != null) || (rset.getString("MC") != null)) {

                        List<String> lstCF = new ArrayList<String>();
                        if (rset.getString("M2") != null) {
                            lstCF.add(rset.getString("M2"));
                        }
                        if (rset.getString("MC") != null) {
                            lstCF.add(rset.getString("MC"));
                        }
                        if (rset.getString("MS") != null) {
                            lstCF.add(rset.getString("MS"));
                        } else {
                            if (rset.getString("MV") != null) {
                                lstCF.add(rset.getString("MV"));
                            }
                        }
                        if (rset.getString("MY") != null) {
                            lstCF.add(rset.getString("MY"));
                        }

                        String confresult = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstCF, ", "));

                        if (confresult != null && !confresult.equals("")) {
                            ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, confresult));
                        }

                        lstCF = null;
                    }

                    // SP - SPONSOR'S INFO
                    if (rset.getString("SP") != null) {
                        List<String> lstSponsor = new ArrayList<String>();
                        lstSponsor.add(rset.getString("SP"));
                        // There is no SM field!!
                        // if (rset.getString("SM") != null){
                        // lstSponsor.add(rset.getString("SM"));
                        // }
                        if (rset.getString("SC") != null) {
                            lstSponsor.add(rset.getString("SC"));
                        }
                        if (rset.getString("SS") != null) {
                            lstSponsor.add(rset.getString("SS"));
                        }
                        if (rset.getString("SV") != null) {
                            lstSponsor.add(rset.getString("SV"));
                        }
                        if (rset.getString("SY") != null) {
                            lstSponsor.add(rset.getString("SY"));
                        }

                        String sponsor = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstSponsor, ", "));

                        if (sponsor != null && !sponsor.equals("")) {
                            ht.put(Keys.SPONSOR, new XMLWrapper(Keys.SPONSOR, sponsor));
                        }

                        lstSponsor = null;
                    }

                    // PL - PUBLISHER LOCATION
                    if (rset.getString("PN") != null) {
                        List<String> lstPL = new ArrayList<String>();
                        if (rset.getString("PC") != null) {
                            lstPL.add(rset.getString("PC"));
                        }
                        if (rset.getString("PS") != null) {
                            lstPL.add(rset.getString("PS"));
                        } else {
                            if (rset.getString("PV") != null) {
                                lstPL.add(rset.getString("PV"));
                            }
                        }
                        if (rset.getString("PY") != null) {
                            lstPL.add(rset.getString("PY"));
                        }
                        if (rset.getString("PO") != null) {
                            lstPL.add(rset.getString("PO"));
                        }

                        String publocation = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstPL, ", "));

                        if (publocation != null && !publocation.equals("")) {
                            ht.put(Keys.PUB_LOCATION, new XMLWrapper(Keys.PUB_LOCATION, publocation));
                        }

                        lstPL = null;
                    }

                    // CN
                    if (rset.getString("CN") != null) {
                        ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("CN"))));
                    }
                    // BN
                    if (rset.getString("BN") != null) {
                        ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("BN"))));
                    }
                    // SN
                    if (rset.getString("SN") != null) {
                        ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("SN"))));
                    }

                    if (rset.getString("ST") != null) {
                        ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("ST"))));
                    }
                    // SE
                    if (rset.getString("SE") != null) {
                        ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("SE"))));
                    }

                    if (rset.getString("YR") != null) {
                        ht.put(Keys.LINKING_YEAR, new Year(Keys.LINKING_YEAR, rset.getString("YR"), perl));
                    }

                } // else All other doctypes (same as CPX)

                EIDoc eiDoc = new EIDoc(did, ht, Abstract.ABSTRACT_FORMAT);
                eiDoc.exportLabels(true);
                eiDoc.setOutputKeys(ABSTRACT_KEYS);
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
        Hashtable<String, DocID> oidTable = getDocIDTable(listOfDocIDs);
        Perl5Util perl = new Perl5Util();
        List<EIDoc> list = new ArrayList<EIDoc>();
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

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, C84_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, C84_TEXT_COPYRIGHT));

                // AN
                if (rset.getString("EX") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("EX"))));
                }
                if (rset.getString("DO") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, StringUtil.replaceNullWithEmptyString(rset.getString("DO"))));
                }

                // TI/TT - common for all doctypes
                // 1884 Special Note
                // swap the TT and TI if they both exist - 1884 only
                if ((rset.getString("TT") != null) && (rset.getString("TI") != null)) {
                    // 1884 Special Note
                    // swap the TT and TI if they both exist - 1884 only
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("TT"))));

                    ht.put(Keys.TITLE_TRANSLATION, new XMLWrapper(Keys.TITLE_TRANSLATION, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                } else if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                } else if (rset.getString("TT") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("TT"))));
                }

                String strDocType = StringUtil.replaceNullWithEmptyString(rset.getString("DT"));

                // AUS - common to all Doctypes
                if (rset.getString("AUS") != null) {
                    if ("PA".equalsIgnoreCase(strDocType)) {
                        Contributors authors = new Contributors(EIDoc.INVENTORS, getContributors(StringUtil.replaceNullWithEmptyString(rset.getString("AUS")),
                            EIDoc.INVENTORS));

                        ht.put(EIDoc.INVENTORS, authors);
                    } else {
                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.replaceNullWithEmptyString(rset.getString("AUS")),
                            Keys.AUTHORS));

                        ht.put(Keys.AUTHORS, authors);

                        if (rset.getString("AF") != null) {
                            List<String> lstAFF = new ArrayList<String>();

                            lstAFF.add(rset.getString("AF"));
                            if (rset.getString("AM") != null) {
                                lstAFF.add(rset.getString("AM"));
                            }
                            if (rset.getString("AC") != null) {
                                lstAFF.add(rset.getString("AC"));
                            }
                            if (rset.getString("ASS") != null) {
                                lstAFF.add(rset.getString("ASS"));
                            }
                            if (rset.getString("AV") != null) {
                                lstAFF.add(rset.getString("AV"));
                            }
                            if (rset.getString("AY") != null) {
                                lstAFF.add(rset.getString("AY"));
                            }
                            Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, StringUtil.join(lstAFF, ", "));
                            ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                            lstAFF = null;
                        }
                    }
                } else {

                    if (rset.getString("ED") != null) {
                        String strED = StringUtil.replaceNullWithEmptyString(rset.getString("ED"));
                        if (perl.match("/(Ed[.]\\s*)/", strED)) {
                            strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                        }
                        Contributors editors = new Contributors(Keys.EDITORS, getContributors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);

                        if (rset.getString("EF") != null) {
                            Affiliation eaffil = new Affiliation(Keys.EDITOR_AFFS, StringUtil.replaceNullWithEmptyString(rset.getString("EF")));
                            ht.put(Keys.EDITOR_AFFS, new Affiliations(Keys.EDITOR_AFFS, eaffil));
                        }
                    }
                }

                // 1884 Patent Records
                if ("PA".equalsIgnoreCase(strDocType)) {
                    // YR is always "Publication date" in Detailed View 1884 Patent
                    if ((rset.getString("YR") != null)) {
                        ht.put(Keys.PUBLICATION_DATE, new Year(Keys.PUBLICATION_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("YR")), perl));
                    }

                    // suppress 'Source:' label
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    if ((rset.getString("PE") != null)) {
                        ht.put(Keys.PATASSIGN, new XMLWrapper(Keys.PATASSIGN, StringUtil.replaceNullWithEmptyString(rset.getString("PE"))));
                    }
                    if ((rset.getString("PM") != null)) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, StringUtil.replaceNullWithEmptyString(rset.getString("PM"))));

                    }
                    if ((rset.getString("AD") != null)) {
                        ht.put(Keys.PATFILDATE, new XMLWrapper(Keys.PATFILDATE, StringUtil.replaceNullWithEmptyString(rset.getString("AD"))));
                    }
                    if ((rset.getString("PD") != null)) {
                        ht.put(Keys.PATENT_ISSUE_DATE, new XMLWrapper(Keys.PATENT_ISSUE_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("PD"))));
                    }
                    if ((rset.getString("PU") != null)) {
                        ht.put(Keys.PATCOUNTRY, new XMLWrapper(Keys.PATCOUNTRY, StringUtil.replaceNullWithEmptyString(rset.getString("PU"))));
                    }
                } // "PA".equalsIgnoreCase(strDocType)
                else { // all other doctypes (just like CPX)
                    if ((rset.getString("ST") != null || rset.getString("SE") != null)) {
                        // ST
                        if (rset.getString("ST") != null) {
                            ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("ST"))));
                        }
                        // SE
                        if (rset.getString("SE") != null) {

                            ht.put(Keys.ABBRV_SERIAL_TITLE,
                                new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("SE"))));
                        }
                        // VO
                        if (rset.getString("VO") != null) {
                            String strVol = replaceVolumeNullWithEmptyString(rset.getString("VO"));
                            ht.put(Keys.VOLUME, new Volume(strVol, perl));
                        }
                        // ISS
                        if (rset.getString("ISS") != null) {
                            replaceIssueNullWithEmptyString(rset.getString("ISS"));
                            ht.put(Keys.ISSUE, new Issue(rset.getString("ISS"), perl));
                        }

                        // SD
                        if (rset.getString("SD") != null) {
                            // jam 11/24/2003 - request from Mark T. - Suppress Issue Date if Month is
                            // missing from Issue Date value
                            String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));
                            if (perl.match("#(\\w+)\\W+(\\d+)#", strSD)) {
                                ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));
                            }
                        }

                        // MT
                        if (rset.getString("MT") != null) {
                            ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                        }

                        // ME
                        if (rset.getString("ME") != null) {
                            ht.put(Keys.ABBRV_MON_MONOGRAPH_TITLE,
                                new XMLWrapper(Keys.ABBRV_MON_MONOGRAPH_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("ME"))));
                        }
                        // VT
                        if (rset.getString("VT") != null) {
                            ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("VT"))));
                        }
                    } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") != null || rset.getString("ME") != null)) {
                        // MT
                        if (rset.getString("MT") != null) {

                            ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));

                        }
                        // ME
                        if (rset.getString("ME") != null) {
                            ht.put(Keys.ABBRV_MON_MONOGRAPH_TITLE,
                                new XMLWrapper(Keys.ABBRV_MON_MONOGRAPH_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("ME"))));

                        }
                        // VO
                        if (rset.getString("VO") != null) {
                            String strVol = replaceVolumeNullWithEmptyString(rset.getString("VO"));
                            ht.put(Keys.VOLUME, new Volume(strVol, perl));
                        }
                        // ISS
                        if (rset.getString("ISS") != null) {
                            String strIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                            ht.put(Keys.ISSUE, new Issue(strIss, perl));
                        }

                        // VT
                        if (rset.getString("VT") != null) {
                            ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("VT"))));
                        }
                    }

                    if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        ht.put(Keys.PUBLICATION_YEAR, new Year(Keys.PUBLICATION_YEAR, strYR, perl));
                    }

                    // NV
                    if (rset.getString("NV") != null) {
                        ht.put(Keys.NUMVOL, new XMLWrapper(Keys.NUMVOL, StringUtil.replaceNullWithEmptyString(rset.getString("NV"))));
                    }

                    // PA
                    if (rset.getString("PA") != null) {
                        // PAPER_NUMBER will cause Paper Number label in XSL
                        ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("PA"))));
                    }

                    // PP
                    String strPages = StringUtil.EMPTY_STRING;
                    if (rset.getString("XP") != null) {
                        strPages = rset.getString("XP");
                    } else if (rset.getString("PP") != null) {
                        strPages = rset.getString("PP");
                    }

                    if (!strPages.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(strPages), perl));

                    }

                    // Non-patent 1884 Records
                    if ((rset.getString("TL") != null)) {
                        ht.put(Keys.SALUTATION, new XMLWrapper(Keys.SALUTATION, StringUtil.replaceNullWithEmptyString(rset.getString("TL"))));
                    }
                    if (rset.getString("FG") != null) {
                        ht.put(Keys.NUMBER_OF_FIGURES, new XMLWrapper(Keys.NUMBER_OF_FIGURES, StringUtil.replaceNullWithEmptyString(rset.getString("FG"))));
                    }
                    if (rset.getString("AO") != null) {
                        ht.put(Keys.ACRONYM_DEFINITION, new XMLWrapper(Keys.ACRONYM_DEFINITION, StringUtil.replaceNullWithEmptyString(rset.getString("AO"))));
                    }

                    // Strip out and store the start page and end page
                    // BUG FIX BELOW -- NULL POINTER ON strPages
                    if (strPages != null) {
                        if (perl.match("/(\\d+)/", strPages)) {
                            ht.put(Keys.START_PAGE, new XMLWrapper(Keys.START_PAGE, perl.group(0).toString()));
                            if (perl.match("/(\\d+)/", perl.postMatch())) {
                                ht.put(Keys.END_PAGE, new XMLWrapper(Keys.END_PAGE, perl.group(0).toString()));
                            }
                        }
                    }

                    // SN
                    if (rset.getString("SN") != null) {
                        ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("SN"))));
                    }
                    // CN
                    if (rset.getString("CN") != null) {
                        ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, StringUtil.replaceNullWithEmptyString(rset.getString("CN"))));
                    }
                    // BN
                    if (rset.getString("BN") != null) {
                        ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("BN"))));
                    }
                    // CF
                    if (rset.getString("CF") != null) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, StringUtil.replaceNullWithEmptyString(rset.getString("CF"))));
                    }
                    // MD
                    if (rset.getString("M2") != null) {
                        ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, StringUtil.replaceNullWithEmptyString(rset.getString("M2"))));
                    }
                    // ML
                    List<String> lstMeetLoc = new ArrayList<String>();
                    if (rset.getString("MC") != null) {
                        lstMeetLoc.add(rset.getString("MC"));
                    }
                    if (rset.getString("MS") != null) {
                        lstMeetLoc.add(rset.getString("MS"));
                    }
                    if (rset.getString("MV") != null) {
                        lstMeetLoc.add(rset.getString("MV"));
                    }
                    if (rset.getString("MY") != null) {
                        lstMeetLoc.add(rset.getString("MY"));
                    }
                    if (lstMeetLoc.size() > 0) {
                        String meetingloc = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstMeetLoc, ", "));

                        if (!meetingloc.equals("")) {
                            ht.put(Keys.MEETING_LOCATION, new XMLWrapper(Keys.MEETING_LOCATION, meetingloc));
                        }

                    }
                    lstMeetLoc = null;

                    // CC
                    if (rset.getString("CC") != null) {

                        ht.put(Keys.CONF_CODE, new XMLWrapper(Keys.CONF_CODE, StringUtil.replaceNullWithEmptyString(rset.getString("CC"))));
                    }

                    // SP
                    List<String> lstSponsor = new ArrayList<String>();
                    if (rset.getString("SP") != null) {
                        lstSponsor.add(rset.getString("SP"));
                    }
                    if (rset.getString("SC") != null) {
                        lstSponsor.add(rset.getString("SC"));
                    }
                    if (rset.getString("SS") != null) {
                        lstSponsor.add(rset.getString("SS"));
                    }
                    if (rset.getString("SV") != null) {
                        lstSponsor.add(rset.getString("SV"));
                    }
                    if (rset.getString("SY") != null) {
                        lstSponsor.add(rset.getString("SY"));
                    }
                    if (lstSponsor.size() > 0) {

                        String sponsors = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstSponsor, ", "));

                        if (!sponsors.equals("")) {
                            ht.put(Keys.SPONSOR, new XMLWrapper(Keys.SPONSOR, sponsors));
                        }

                    }
                    lstSponsor = null;

                    // jam NTIS - Bug #81 - suppress all Pub info if publisher name is blank
                    // PN, PL
                    if (rset.getString("PN") != null) {
                        List<String> lstTokens = new ArrayList<String>();
                        lstTokens.add((String) rset.getString("PN"));
                        if (rset.getString("PC") != null) {
                            lstTokens.add((String) rset.getString("PC"));
                        }

                        if (lstTokens.size() > 0) {
                            String publisher = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstTokens, ", "));

                            if (!publisher.equals("")) {
                                ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, publisher));
                            }

                        }
                        lstTokens = null;
                    }

                    // PO
                    if (rset.getString("PO") != null) {
                        ht.put(Keys.PUBLICATION_ORDER, new XMLWrapper(Keys.PUBLICATION_ORDER, StringUtil.replaceNullWithEmptyString(rset.getString("PO"))));
                    }
                    // FLS
                    if (rset.getString("FLS") != null) {
                        ht.put(Keys.UNCONTROLLED_TERMS,
                            new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("FLS")))));

                    }

                    // CLS
                    if (rset.getString("CLS") != null) {
                        ht.put(Keys.CLASS_CODES,
                            new XMLMultiWrapper(CPX_CONTROLLED_TERMS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("CLS")))));
                    }

                    if ((rset.getString("CVS") != null)) {
                        ht.put(Keys.CONTROLLED_TERMS,
                            new XMLMultiWrapper(CPX_CONTROLLED_TERMS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("CVS")))));
                    }
                    // TR

                    if (rset.getString("TR") != null) {
                        ht.put(Keys.TREATMENTS, new Treatments(setTreatments(StringUtil.replaceNullWithEmptyString(rset.getString("TR"))), this.database));
                    }
                }  // "PA".equalsIgnoreCase(strDocType)

                // DT
                if (rset.getString("DT") != null) {
                    ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, replaceDTNullWithEmptyString(rset.getString("DT"))));

                }

                // AB - common for all doctypes
                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                // AT - common for all doctypes
                if (rset.getString("AT") != null) {
                    ht.put(Keys.ABSTRACT_TYPE, new XMLWrapper(Keys.ABSTRACT_TYPE, StringUtil.replaceNullWithEmptyString(rset.getString("AT"))));
                }

                // NR - common for all doctypes

                if (rset.getString("NR") != null) {
                    String strREFs = rset.getString("NR");
                    if (perl.match("/(\\d+)/", strREFs)) {
                        ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, perl.group(0).toString()));
                    }
                }

                // MH - common for all doctypes
                String mainHeading = StringUtil.EMPTY_STRING;
                if ((rset.getString("MH") != null) && (rset.getString("SH") != null)) {
                    mainHeading = rset.getString("MH").concat(" -- ").concat(rset.getString("SH"));
                } else if (rset.getString("MH") != null) {
                    mainHeading = rset.getString("MH");
                } else if (rset.getString("SH") != null) {
                    mainHeading = rset.getString("SH");
                }

                if (rset.getString("MH") != null) {
                    mainHeading = StringUtil.replaceNullWithEmptyString(mainHeading);
                    if (!mainHeading.equals("")) {
                        ht.put(Keys.MAIN_HEADING, new XMLWrapper(CPX_MAIN_HEADING, mainHeading));
                    }
                }
                mainHeading = null;

                // CVS - common for all doctypes

                if ((rset.getString("CVS") != null)) {
                    ht.put(Keys.CONTROLLED_TERMS,
                        new XMLMultiWrapper(CPX_CONTROLLED_TERMS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("CVS")))));
                }

                // LA - common for all doctypes
                if (rset.getString("LA") != null) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LA"))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, FullDoc.FULLDOC_FORMAT);
                eiDoc.exportLabels(true);
                eiDoc.setOutputKeys(DETAILED_KEYS);
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

                String strDocType = StringUtil.replaceNullWithEmptyString(rset.getString("DT"));
                String risDocType = replaceTYwithRIScode(strDocType);
                String bibDocType = replaceTYwithBIBcode(strDocType);

                if (rset.getString("DT") != null) {
                    ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, risDocType));
                    ht.put(Keys.BIB_TY, new XMLWrapper(Keys.BIB_TY, bibDocType));
                }

                if (rset.getString("LA") != null) {
                    ht.put(Keys.RIS_LA, new XMLWrapper(Keys.RIS_LA, StringUtil.replaceNullWithEmptyString(rset.getString("LA"))));
                }

                ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, C84DocBuilder.C84_TEXT_COPYRIGHT));

                if (rset.getString("TI") != null) {
                    ht.put(Keys.RIS_TI, new XMLWrapper(Keys.RIS_TI, StringUtil.replaceNullWithEmptyString(rset.getString("TI"))));
                }
                if (rset.getString("TT") != null) {
                    ht.put(Keys.RIS_T1, new XMLWrapper(Keys.RIS_T1, StringUtil.replaceNullWithEmptyString(rset.getString("TT"))));
                }
                if (rset.getString("MT") != null) {
                    ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                }

                if (risDocType.equalsIgnoreCase("JOUR")) {
                    if (rset.getString("ST") != null) {
                        ht.put(Keys.RIS_JO, new XMLWrapper(Keys.RIS_JO, StringUtil.replaceNullWithEmptyString(rset.getString("ST"))));
                    }
                } else {
                    if (rset.getString("ST") != null) {
                        ht.put(Keys.RIS_T3, new XMLWrapper(Keys.RIS_T3, StringUtil.replaceNullWithEmptyString(rset.getString("ST"))));
                    }
                }

                // DO
                if (rset.getString("DO") != null) {
                    ht.put(Keys.RIS_DO, new XMLWrapper(Keys.RIS_DO, StringUtil.replaceNullWithEmptyString(rset.getString("DO"))));
                }

                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.RIS_AUS, getContributors(StringUtil.replaceNullWithEmptyString(rset.getString("AUS")),
                        Keys.RIS_AUS));

                    ht.put(Keys.RIS_AUS, authors);

                    if (rset.getString("AF") != null) {
                        List<String> lstAFF = new ArrayList<String>();

                        lstAFF.add(rset.getString("AF"));
                        if (rset.getString("AM") != null) {
                            lstAFF.add(rset.getString("AM"));
                        }
                        if (rset.getString("AC") != null) {
                            lstAFF.add(rset.getString("AC"));
                        }
                        if (rset.getString("ASS") != null) {
                            lstAFF.add(rset.getString("ASS"));
                        }
                        if (rset.getString("AV") != null) {
                            lstAFF.add(rset.getString("AV"));
                        }
                        if (rset.getString("AY") != null) {
                            lstAFF.add(rset.getString("AY"));
                        }

                        Affiliation aff = new Affiliation(Keys.RIS_AD, StringUtil.replaceNullWithEmptyString(StringUtil.join(lstAFF, ", ")));
                        ht.put(Keys.RIS_AD, new Affiliations(Keys.RIS_AD, aff));
                        lstAFF = null;
                    }
                } else {
                    if (rset.getString("ED") != null) {
                        String strED = StringUtil.replaceNullWithEmptyString(rset.getString("ED"));
                        if (perl.match("/(Ed[.]\\s*)/", strED)) {
                            strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                        }
                        Contributors editors = new Contributors(Keys.RIS_EDS, getContributors(strED, Keys.RIS_EDS));

                        ht.put(Keys.RIS_EDS, editors);

                        if (rset.getString("EF") != null) {
                            Affiliation eff = new Affiliation(Keys.RIS_AD, StringUtil.replaceNullWithEmptyString(rset.getString("EF")));
                            ht.put(Keys.RIS_AD, new Affiliations(Keys.RIS_AD, eff));

                        }
                    }
                }

                // 1884 Patent Records
                if ("PA".equalsIgnoreCase(strDocType)) {

                    // YR is always "Publication date" in Detailed View 1884 Patent
                    if ((rset.getString("YR") != null)) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        ht.put(Keys.RIS_PY, new Year(strYR, perl));
                    }
                    if ((rset.getString("PE") != null)) {
                        ht.put(Keys.RIS_PE, new XMLWrapper(Keys.RIS_PE, StringUtil.replaceNullWithEmptyString(rset.getString("PE"))));
                    }
                    if ((rset.getString("PM") != null)) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, StringUtil.replaceNullWithEmptyString(rset.getString("PM"))));
                    }
                    // Pat Issue/Assign
                    if ((rset.getString("PD") != null)) {
                        ht.put(Keys.RIS_Y1, new XMLWrapper(Keys.RIS_Y1, this.formatRISDate(StringUtil.replaceNullWithEmptyString(rset.getString("PD")))));
                    }
                    // Pat Filing
                    if ((rset.getString("AD") != null)) {
                        ht.put(Keys.RIS_Y2, new XMLWrapper(Keys.RIS_Y2, this.formatRISDate(StringUtil.replaceNullWithEmptyString(rset.getString("AD")))));
                    }
                    if ((rset.getString("PU") != null)) {
                        ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, StringUtil.replaceNullWithEmptyString(rset.getString("PU"))));
                    }
                } // "PA".equalsIgnoreCase(strDocType)
                else {
                    if (rset.getString("VO") != null) {
                        String strVol = replaceVolumeNullWithEmptyString(rset.getString("VO"));
                        strVol = perl.substitute("s/[vol\\.]//gi", strVol);
                        if (strVol != null) {
                            ht.put(Keys.RIS_VL, new Volume(Keys.RIS_VL, strVol, perl));
                        }

                    }

                    if (rset.getString("ISS") != null) {
                        String strIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                        strIss = perl.substitute("s/[no\\.]//gi", strIss);
                        if (strIss != null) {
                            ht.put(Keys.RIS_IS, new Issue(Keys.RIS_IS, strIss, perl));
                        }
                    }

                    if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, strYR, perl));
                    }

                    // PP
                    String strPages = StringUtil.EMPTY_STRING;
                    if (rset.getString("XP") != null) {
                        strPages = rset.getString("XP");
                    } else {
                        strPages = rset.getString("PP");
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

                    if (rset.getString("SN") != null) {
                        ht.put(Keys.RIS_SN, new ISSN(Keys.RIS_SN, StringUtil.replaceNullWithEmptyString(rset.getString("SN"))));
                    }
                    if (rset.getString("BN") != null) {
                        ht.put(Keys.RIS_S1, new ISBN(Keys.RIS_S1, StringUtil.replaceNullWithEmptyString(rset.getString("BN"))));
                    }

                    // CF
                    if (rset.getString("CF") != null) {
                        ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, StringUtil.replaceNullWithEmptyString(rset.getString("CF"))));
                    }
                    // MD
                    if (rset.getString("M2") != null) {
                        ht.put(Keys.RIS_MD, new XMLWrapper(Keys.RIS_MD, StringUtil.replaceNullWithEmptyString(rset.getString("M2"))));
                    }
                    // ML
                    /*
                     * JM 10/22/2008 Stop putting Conference Location in CY field. CY should be City of publication. Code was left since we may find another RIS
                     * field to put it in
                     */
                    /*
                     * List lstMeetLoc = new ArrayList(); if (rset.getString("MC") !=null){ lstMeetLoc.add(rset.getString("MC")); } if (rset.getString("MS") !=
                     * null){ lstMeetLoc.add(rset.getString("MS")); } if (rset.getString("MV") != null){ lstMeetLoc.add(rset.getString("MV")); } if
                     * (rset.getString("MY") != null){ lstMeetLoc.add(rset.getString("MY")); } if ((lstMeetLoc != null)&& (lstMeetLoc.size() > 0)) {
                     * ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, StringUtil.replaceNullWithEmptyString(StringUtil.join(lstMeetLoc,", ")))); } lstMeetLoc =
                     * null;
                     */

                    // PN, PL
                    // jam NTIS - Bug #81 - suppress all Pub info if publisher name is blank
                    // PN, PL
                    if (rset.getString("PN") != null) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, rset.getString("PN")));

                        if (rset.getString("PC") != null) {
                            ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, rset.getString("PC")));
                        }
                        /*
                         * NTIS/1884 - Bug #86 - do not duplicate Publisher information same as in loadDetailed() if (rset.getString("PS") != null){
                         * lstTokens.add((String) rset.getString("PS")); } if (rset.getString("PV") != null){ lstTokens.add((String) rset.getString("PV")); } if
                         * (rset.getString("PY") != null){ lstTokens.add((String) rset.getString("PY")); }
                         */
                    }

                }

                if (rset.getString("EX") != null) {
                    String strEX = StringUtil.replaceNullWithEmptyString(rset.getString("EX"));
                    ht.put(Keys.RIS_AN, new XMLWrapper(Keys.RIS_AN, strEX));
                }

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, abs));
                }
                // MH
                String mh = rset.getString("MH");
                String sh = rset.getString("SH");
                String mainHeading = null;
                if (mh != null && sh != null) {
                    mainHeading = mh.concat(" -- ").concat(sh);
                } else if (mh != null) {
                    mainHeading = mh;
                } else if (sh != null) {
                    mainHeading = sh;
                }
                if (mainHeading != null) {
                    ht.put(Keys.RIS_KW, new XMLWrapper(Keys.RIS_KW, StringUtil.replaceNullWithEmptyString(mainHeading)));
                }

                // CVS
                if (rset.getString("CVS") != null) {
                    ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("CVS")))));
                }
                // FLS
                if (rset.getString("FLS") != null) {
                    ht.put(Keys.RIS_FLS, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("FLS")))));
                }

                EIDoc eiDoc = new EIDoc(did, ht, RIS.RIS_FORMAT);
                eiDoc.exportLabels(false);
                eiDoc.setOutputKeys(RIS_KEYS);
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

    /*
     * PRIVATE UTILITY METHODS /
     *
     * /* This method builds the IN String from list of docId objects. The select query will get the result set in a reverse way So in order to get in correct
     * order we are doing a reverse example of return String--(23,22,1,12...so on);
     *
     * @param listOfDocIDs
     *
     * @return String
     */
    protected String buildINString(List<DocID> listOfDocIDs) {
        StringBuffer sQuery = new StringBuffer();
        sQuery.append("(");
        for (int k = listOfDocIDs.size(); k > 0; k--) {
            DocID doc = (DocID) listOfDocIDs.get(k - 1);
            String docID = doc.getDocID();
            if ((k - 1) == 0) {
                sQuery.append("'").append(docID).append("'");
            } else {
                sQuery.append("'").append(docID).append("'").append(",");
            }
        } // end of for
        sQuery.append(")");
        return sQuery.toString();
    }

    /* TS if volume is null and str is not null print n */
    private String replaceVolumeNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = StringUtil.EMPTY_STRING;
        }

        if (!str.equals(StringUtil.EMPTY_STRING) && ((str.indexOf("v", 0) < 0) && (str.indexOf("V", 0) < 0))) {
            str = "v ".concat(str);
        }
        return str;
    }

    /* TS if number is null and str is not null print n */
    private String replaceIssueNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = StringUtil.EMPTY_STRING;
        }

        if (!str.equals(StringUtil.EMPTY_STRING) && ((str.indexOf("n") < 0) && (str.indexOf("N") < 0))) {
            str = "n ".concat(str);
        }
        return str;
    }

    /* TS , end */

    // jam XML document mapping, conversion to TY values
    // for RIS format - only called from loadDetailed
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
            } else if (str.equals("PA")) {
                str = "PAT";
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
            } else if (str.equals("PA")) {
                str = "article";
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
            if (str.equals("JA")) {
                str = "Journal article (JA)";
            } else if (str.equals("CA")) {
                str = "Conference article (CA)";
            } else if (str.equals("CP")) {
                str = "Conference proceeding (CP)";
            } else if (str.equals("MC")) {
                str = "Monograph chapter (MC)";
            } else if (str.equals("MR")) {
                str = "Monograph review (MR)";
            } else if (str.equals("RC")) {
                str = "Report chapter (RC)";
            } else if (str.equals("RR")) {
                str = "Report review (RR)";
            } else if (str.equals("DS")) {
                str = "Dissertation (DS)";
            } else if (str.equals("UP")) {
                str = "Unpublished paper (UP)";
            } else if (str.equals("PA")) {
                str = "Patent (PA)";
            }
        }
        return str;
    }

    protected Hashtable<String, DocID> getDocIDTable(List<DocID> listOfDocIDs) {
        Hashtable<String, DocID> h = new Hashtable<String, DocID>();

        for (int i = 0; i < listOfDocIDs.size(); ++i) {
            DocID d = (DocID) listOfDocIDs.get(i);
            h.put(d.getDocID(), d);
        }

        return h;
    }

    private String formatRISDate(String strInDate) {
        String strDate = strInDate;

        final String DEFAULT_RIS_FORMAT = "yyyy/MM/dd";
        final String DEFAULT_INDATE_FORMAT = "yyyy/MMM/dd";

        String strOutputPattern = DEFAULT_RIS_FORMAT;
        String strInputPattern = DEFAULT_INDATE_FORMAT;

        if (perl.match("/(\\w+)\\W+(\\d+)\\W+(\\d+)/", strDate)) {
            strInputPattern = DEFAULT_INDATE_FORMAT;
            strOutputPattern = DEFAULT_RIS_FORMAT;
            strDate = (perl.group(3).toString()).concat("/").concat(perl.group(1).toString()).concat("/").concat(perl.group(2).toString());
        } else if (perl.match("/(\\d+)\\W+(\\w+)\\W+(\\d+)/", strDate)) {
            strInputPattern = DEFAULT_INDATE_FORMAT;
            strOutputPattern = DEFAULT_RIS_FORMAT;
            strDate = (perl.group(3).toString()).concat("/").concat(perl.group(2).toString()).concat("/").concat(perl.group(1).toString());
        } else if (perl.match("/(\\w+)\\W+(\\w+)\\W+(\\d+)/", strDate)) {
            strInputPattern = "yyyy/MMM/";
            strOutputPattern = "yyyy/MM/";
            strDate = (perl.group(3).toString()).concat("/").concat(perl.group(1).toString()).concat("/");
        } else if (perl.match("/(\\w+)\\W+(\\d+)/", strDate)) {
            strInputPattern = "yyyy/MMM/";
            strOutputPattern = "yyyy/MM/";
            strDate = (perl.group(2).toString()).concat("/").concat(perl.group(1).toString()).concat("/");
        } else if (perl.match("/(\\d+)/", strDate)) {
            strInputPattern = "yyyy//";
            strOutputPattern = "yyyy//";
            strDate = (perl.group(1).toString()).concat("//");
        }

        SimpleDateFormat formatter = new SimpleDateFormat(strInputPattern);

        // Parse the resulting string into a Date object
        // MMM is used to parse the input (Month name, this will include long, short and abbreviated month names)
        // MM is used to format the output (Month number)
        // Format the Date object back into a String
        try {
            java.util.Date currentTime = formatter.parse((String) strDate);
            formatter = new SimpleDateFormat(strOutputPattern);
            strDate = formatter.format(currentTime);
        } catch (ParseException pe) {
            // just in case we failed miserably, use the raw data from the DB
            strDate = strInDate;
        }

        return strDate;
    }

    public String[] setElementData(String elementVal) {
        ArrayList<String> list = new ArrayList<String>();
        AuthorStream aStream = null;
        String strToken = null;
        try {
            if (elementVal != null) {
                aStream = new AuthorStream(new ByteArrayInputStream(elementVal.getBytes()));
                strToken = null;
                while ((strToken = aStream.readAuthor()) != null) {
                    list.add(strToken);
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

    public String[] setTreatments(String treatments) {
        ArrayList<String> result = new ArrayList<String>();
        int len = 0;
        int trlen = treatments.length();
        String ch = "";
        if (trlen >= 0 && trlen < 10 && trlen > 0) {
            for (int i = 0; i < trlen; i++) {
                if (len < trlen) {
                    ch = treatments.substring(len, len + 1);
                } else {
                    ch = treatments.substring(len);
                }

                if (!ch.equals("") && ch != null && !ch.equals("QQ") && !ch.equals(";")) {
                    result.add(ch);
                }
                len++;
                ch = "";
            }
        }

        return (String[]) result.toArray(new String[1]);
    }

    public List<Contributor> getContributors(String strAuthors, Key key) {

        List<Contributor> list = new ArrayList<Contributor>();
        AuthorStream aStream = null;
        DataCleaner dataCleaner = new DataCleaner();
        strAuthors = dataCleaner.cleanEntitiesForDisplay(strAuthors);
        try {
            aStream = new AuthorStream(new ByteArrayInputStream(strAuthors.getBytes()));
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

    private String hasAbstract(ResultSet rs) throws SQLException  {
        String abs = null;
        Clob clob = rs.getClob("AB");
        if (clob != null) {
            abs = StringUtil.getStringFromClob(clob);
        }

        if (abs == null || abs.indexOf("No abstract") == 0) {
            return null;
        } else {
            return abs;
        }
    }

    private KeyValuePair[] setCVS(String mainHeading, String cvs) {
        ArrayList<KeyValuePair> list = new ArrayList<KeyValuePair>();
        if (mainHeading != null) {
            list.add(new KeyValuePair(Keys.MAIN_HEADING, mainHeading));
        }

        AuthorStream aStream = null;
        String strToken = null;
        try {
            if (cvs != null) {
                aStream = new AuthorStream(new ByteArrayInputStream(cvs.getBytes()));
                strToken = null;
                while ((strToken = aStream.readAuthor()) != null) {
                    KeyValuePair k = new KeyValuePair(Keys.CONTROLLED_TERM, strToken);
                    list.add(k);
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

        return (KeyValuePair[]) list.toArray(new KeyValuePair[list.size()]);
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

// End Of C84DocBuilder
