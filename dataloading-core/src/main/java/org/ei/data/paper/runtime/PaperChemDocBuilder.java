package org.ei.data.paper.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import org.apache.commons.lang.StringUtils;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.common.AuthorStream;
import org.ei.common.DataCleaner;
import org.ei.data.compendex.runtime.CPXDocBuilder;
import org.ei.domain.Abstract;
import org.ei.domain.Affiliation;
import org.ei.domain.Affiliations;
import org.ei.domain.Citation;
import org.ei.domain.Contributor;
import org.ei.domain.Contributors;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Detail;
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
import org.ei.domain.Keys;
import org.ei.domain.PageRange;
import org.ei.domain.RIS;
import org.ei.domain.Volume;
import org.ei.domain.XMLMultiWrapper;
import org.ei.domain.XMLWrapper;
import org.ei.domain.Year;
import org.ei.util.StringUtil;

/**
 * This class is the implementation of DocumentBuilder Basically this class is responsible for building a List of EIDocs from a List of DocIds.The input ie list
 * of docids come from PaperSearchControl and
 *
 */
public class PaperChemDocBuilder implements DocumentBuilder {
    public static String PAPER_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String PAPER_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER_TEXT = "Ei";

    private static final Key PAPER_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Controlled terms");
    private static final Key PAPER_SOURCE = new Key(Keys.SOURCE, "Original source field");

    // private static final Key PAPER_CLASS_CODES = new Key(Keys.CLASS_CODES, "Classification codes");

    private static Map<?, ?> issnARFix = new HashMap<Object, Object>();
    // private static final Key CPX_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Ei controlled terms");
    // private static final Key CPX_CLASS_CODES = new Key(Keys.CLASS_CODES, "Ei classification codes");
    public static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.PATAPPNUM, Keys.PATENT_NUMBER, Keys.PATNUM, Keys.PATASSIGN, Keys.PATENT_ISSUE_DATE,
        Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.AUTHOR_AFFS, Keys.SOURCE, Keys.MONOGRAPH_TITLE, Keys.PAGE_RANGE, Keys.VOLISSUE, Keys.PUBLICATION_DATE,
        Keys.PUBLISHER, Keys.ISSUE_DATE, Keys.ISSN, Keys.LANGUAGE, Keys.NO_SO, Keys.DOI, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    public static final Key[] ABSTRACT_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.PATAPPNUM, Keys.PATENT_NUMBER, Keys.PATNUM, Keys.PATASSIGN, Keys.PATENT_ISSUE_DATE,
        Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.EDITOR_AFFS, Keys.AUTHOR_AFFS, Keys.VOLISSUE, Keys.PAPER_NUMBER, Keys.SOURCE, Keys.PUBLICATION_YEAR_PAPER,
        Keys.PUBLICATION_DATE_PAPER, Keys.ISSUE_DATE, Keys.PAGE_RANGE, Keys.REPORT_NUMBER_PAPER, Keys.MONOGRAPH_TITLE, Keys.CONFERENCE_NAME, Keys.ISSN,
        Keys.ISBN, Keys.CODEN, Keys.PUBLISHER, Keys.I_PUBLISHER, Keys.CONF_DATE, Keys.SPONSOR, Keys.PROVIDER, Keys.LANGUAGE, Keys.MAIN_HEADING,
        PAPER_CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.ABSTRACT, Keys.NUMBER_OF_REFERENCES, Keys.NO_SO, Keys.DOI, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT };
    public static final Key[] DETAILED_KEYS = { Keys.ACCESSION_NUMBER, Keys.PATAPPNUM, Keys.PRIORITY_INFORMATION, Keys.PATNUM, Keys.PATASSIGN,
        Keys.PATENT_ISSUE_DATE, Keys.TITLE, Keys.TITLE_TRANSLATION, Keys.AUTHORS, Keys.EDITORS, Keys.AUTHOR_AFFS, Keys.EDITOR_AFFS, Keys.SERIAL_TITLE,
        Keys.ABBRV_SERIAL_TITLE, Keys.VOLUME, Keys.REPORT_NUMBER_PAPER, Keys.ISSUE, Keys.MONOGRAPH_TITLE, Keys.ISSUE_DATE, Keys.PUBLICATION_YEAR,
        Keys.PAGE_RANGE, Keys.SOURCE, Keys.LANGUAGE, Keys.ISSN, Keys.CODEN, Keys.ISBN, Keys.DOC_TYPE, Keys.CONFERENCE_NAME, Keys.CONF_DATE,
        Keys.MEETING_LOCATION, Keys.CONF_CODE, Keys.SESSION_NAME_NUMBER, Keys.SPONSOR, Keys.PUBLISHER, Keys.ABSTRACT, Keys.ABSTRACT_TYPE,
        Keys.NUMBER_OF_CLAIMS, Keys.NUMBER_OF_REFERENCES, Keys.NUMBER_OF_TABLES, Keys.SPECIFIC_NAMES, Keys.MAIN_HEADING, PAPER_CONTROLLED_TERMS,
        Keys.UNCONTROLLED_TERMS, Keys.CLASS_CODES, Keys.TREATMENTS, Keys.DOI, Keys.DOCID, Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.PROVIDER };
    private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_LA, Keys.RIS_N1, Keys.RIS_TI, Keys.RIS_T1, Keys.RIS_BT, Keys.RIS_JO, Keys.RIS_T3,
        Keys.RIS_AUS, Keys.RIS_AD, Keys.RIS_EDS, Keys.RIS_VL, Keys.RIS_IS, Keys.RIS_PY, Keys.RIS_AN, Keys.RIS_SP, Keys.RIS_EP, Keys.RIS_SN, Keys.RIS_S1,
        Keys.RIS_MD, Keys.RIS_CY, Keys.RIS_PB, Keys.RIS_N2, Keys.RIS_KW, Keys.RIS_CVS, Keys.RIS_FLS, Keys.RIS_DO };
    private static final Key[] XML_KEYS = { Keys.ISSN, Keys.MAIN_HEADING, Keys.NO_SO, Keys.MONOGRAPH_TITLE, Keys.PUBLICATION_YEAR, Keys.CONTROLLED_TERM,
        Keys.ISBN, Keys.AUTHORS, Keys.DOCID, Keys.SOURCE, Keys.NUMVOL, Keys.EDITOR_AFFS, Keys.EDITORS, Keys.PUBLISHER, Keys.VOLUME, Keys.AUTHOR_AFFS,
        Keys.PROVIDER, Keys.ISSUE_DATE, Keys.COPYRIGHT_TEXT, Keys.DOI, Keys.PUBLICATION_DATE, Keys.TITLE, Keys.LANGUAGE, Keys.PAGE_RANGE, Keys.PAPER_NUMBER,
        Keys.COPYRIGHT, Keys.ISSUE, Keys.ACCESSION_NUMBER, PAPER_CONTROLLED_TERMS };
    private static String DELIM;

    private Perl5Util perl = new Perl5Util();
    private Database database;

    private static String queryCitation = "select M_ID,DT,TI,TT,AU,AF,AC,ASP,AY,ED,EF,EM,EC,ES,EY,ST,SE,VO,ISS,SD,MT,PN,YR,PA,XP,PP,LA,SN,CP,SOURC,ASSIG,APPLN,PATNO,DO, LOAD_NUMBER from paper_master  where M_ID IN ";
    private static String queryXMLCitation = "select  M_ID,DT,TI,TT,AU,AF,AC,ASP,AY,ED,EF,EM,EC,ES,EY,ST,SE,VO,ISS,SD,MT,PN,YR,PA,XP,PP,LA,SN,DO,BN,LOAD_NUMBER, AN, PT , M2 from paper_master where M_ID IN ";

    private static String queryAbstracts = "select M_ID,DT,TI,TT,AU,AF,AC,ASP,AY,ED,EF,EM,EC,ES,EY,ST,SE,VO,ISS,SD,MT,VX,PN,YR,PA,XP,PP,SOURC,LA,SN,CN,BN,CF,M2,MD,MC,MS,MY,SP,PC,PY,AB,NR,AT,PT,FL,PS,CP,ASSIG,APPLN,PATNO, DO, LOAD_NUMBER from paper_master where   M_ID IN ";

    // jam 12/30/2002
    // New Index - field change from AN to EX
    private static String queryDetailed = "select M_ID,AN,TI,TT,AU,AF,AC,ASP,AY,ED,EF,EM,EC,ES,EY,ST,SE,VO,ISS,SD,MT,VX,PN,YR,PA,XP,PP,SOURC,LA,SN,CN,BN,CF,M2,MD,MC,MS,MY,SP,PC,PY,AB,CSESS,CLAIM,NOFIG,NOTAB,NR,AT,PT,FL,DT,CC,PS,SPECN,CP,PRIOR_NUM,PATNO,ASSIG,APPLN, DO , LOAD_NUMBER from paper_master where M_ID   IN ";

    private static String queryPreview = "select M_ID, AB from paper_master where M_ID IN ";

    public DocumentBuilder newInstance(Database database) {
        return new PaperChemDocBuilder(database);
    }

    public PaperChemDocBuilder() {
    }

    public PaperChemDocBuilder(Database database) {
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
        }

        else if (dataFormat.equals(FullDoc.FULLDOC_FORMAT)) {
            l = loadDetailed(listOfDocIDs);
        } else if (dataFormat.equalsIgnoreCase("RIS")) {
            l = loadRIS(listOfDocIDs);
        }

        else if (dataFormat.equalsIgnoreCase(Citation.XMLCITATION_FORMAT)) {
            l = loadXMLCitations(listOfDocIDs);
        }

        else if (Citation.CITATION_PREVIEW.equals(dataFormat)) {
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
                String abs = StringUtil.getStringFromClob(rset.getClob("AB"));

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

    private List<EIDoc> loadAbstracts(List<DocID> listOfDocIDs) throws DocumentBuilderException {
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
            rset = stmt.executeQuery(queryAbstracts + INString);

            while (rset.next()) {

                ElementDataMap ht = new ElementDataMap();

                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));

                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PROVIDER_TEXT));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, PAPER_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, PAPER_TEXT_COPYRIGHT));

                if (rset.getString("VX") != null && !rset.getString("VX").trim().equals(StringUtil.EMPTY_STRING))

                {
                    ht.put(Keys.REPORT_NUMBER_PAPER, new XMLWrapper(Keys.REPORT_NUMBER_PAPER, rset.getString("VX")));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("TT") != null) && (rset.getString("TI") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TT"))).concat(")");
                } else if (rset.getString("TI") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI")).concat(strTitle);
                } else if (rset.getString("TT") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI")).concat(strTitle);
                }

                if (strTitle != null && !strTitle.trim().equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));
                }
                strTitle = null;

                // AU or EDS
                if (rset.getString("AU") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AU"), Keys.AUTHORS));

                    if (rset.getString("AF") != null) {
                        Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, rset.getString("AF"));
                        authors.setFirstAffiliation(affil);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }

                    ht.put(Keys.AUTHORS, authors);

                    /*
                     * if(rset.getString("AF") != null) { ht.put(EIDoc.AUTHOR_AFF,StringUtil.replaceNullWithEmptyString(rset.getString("AF"))); }
                     */
                } else {
                    if (rset.getString("ED") != null) {
                        String strED = StringUtil.replaceNullWithEmptyString(rset.getString("ED"));
                        if (perl.match("/(Ed[.]\\s*)/", strED)) {
                            strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                        }

                        Contributors editors = new Contributors(Keys.EDITORS, getContributors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);

                        if (rset.getString("EF") != null) {
                            Affiliation eaffil = new Affiliation(Keys.EDITOR_AFFS, rset.getString("EF"));
                            editors.setFirstAffiliation(eaffil);
                            ht.put(Keys.EDITOR_AFFS, new Affiliations(Keys.EDITOR_AFFS, eaffil));
                        }
                    }
                }

                // Patent Records
                String strDocType = StringUtil.replaceNullWithEmptyString(rset.getString("DT"));
                if ("PA".equalsIgnoreCase(strDocType)) {
                    // suppress 'Source:' label
                    {
                        ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                    }

                    if ((rset.getString("ASSIG") != null)) {
                        List<String> lstAsg = convertString2List(StringUtil.substituteChars(StringUtil.replaceNullWithEmptyString(rset.getString("ASSIG"))));
                        if (lstAsg != null && lstAsg.size() > 0) {
                            ht.put(Keys.PATASSIGN, new XMLMultiWrapper(Keys.PATASSIGN, setAssignees(lstAsg)));
                        }
                    }
                    if ((rset.getString("APPLN") != null)) {
                        String appln = StringUtil.replaceNullWithEmptyString(rset.getString("APPLN"));
                        if (!appln.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.PATAPPNUM, new XMLWrapper(Keys.PATAPPNUM, appln));
                        }
                    }
                    if ((rset.getString("PATNO") != null)) {
                        String patno = StringUtil.replaceNullWithEmptyString(rset.getString("PATNO"));
                        if (!patno.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, patno));
                        }
                    }
                } // "PA".equalsIgnoreCase(strDocType)
                else { // All other doctypes

                    if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("SOURC") != null
                        || rset.getString("PN") != null) {

                        if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("SOURC") == null) {
                            String strVolIss = StringUtil.EMPTY_STRING;
                            // VO
                            if (rset.getString("VO") != null) {
                                strVolIss = strVolIss.concat(replaceVolumeNullWithEmptyString(rset.getString("VO")));

                                if (!strVolIss.equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.VOLUME, new Volume(StringUtil.replaceNullWithEmptyString(rset.getString("VO")), perl));
                                }
                            }
                            if (rset.getString("ISS") != null) {
                                String iss = StringUtil.replaceNullWithEmptyString(rset.getString("ISS"));

                                if (!iss.equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.ISSUE, new Issue(iss, perl));
                                }

                                if (!strVolIss.equals(StringUtil.EMPTY_STRING)) {
                                    strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(rset.getString("ISS")));
                                } else {
                                    strVolIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                                }
                            }

                            if (!strVolIss.equals(StringUtil.EMPTY_STRING)) {
                                ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                            }
                        }

                        if (rset.getString("ST") != null || rset.getString("SE") != null) {
                            if (rset.getString("ST") != null) {
                                String st = StringUtil.replaceNullWithEmptyString(rset.getString("ST"));

                                if (!st.equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, st));
                                }
                            } else if (rset.getString("SE") != null) {
                                String se = StringUtil.replaceNullWithEmptyString(rset.getString("SE"));
                                if (!se.equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, se));
                                }
                            }
                            // an MT can accompany the ST (or SE)
                            if (rset.getString("MT") != null) {
                                String mt = StringUtil.replaceNullWithEmptyString(rset.getString("MT"));
                                if (!mt.equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, mt));
                                }
                            }
                        } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") != null)) {
                            if (rset.getString("MT") != null) {
                                String mt = StringUtil.replaceNullWithEmptyString(rset.getString("MT"));

                                if (!mt.equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, mt));
                                }
                            }
                        } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") == null)
                            && (rset.getString("SOURC") != null)) {
                            if (rset.getString("SOURC") != null) {
                                String sourc = StringUtil.replaceNullWithEmptyString(rset.getString("SOURC"));

                                if (!sourc.equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, sourc));
                                }
                            }
                        } else if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null) {
                            if (rset.getString("PN") != null) {
                                String pn = StringUtil.replaceNullWithEmptyString(rset.getString("PN"));

                                if (!pn.equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, pn));
                                }
                            }
                        } else {

                        }
                    }
                }

                if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("PN") == null
                    && rset.getString("SOURC") == null) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                // PN
                if (rset.getString("PN") != null) {
                    String pn = StringUtil.replaceNullWithEmptyString(rset.getString("PN"));
                    if (!pn.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.I_PUBLISHER, new XMLWrapper(Keys.I_PUBLISHER, pn));
                    }
                }

                // SD
                if ("PA".equalsIgnoreCase(strDocType)) {
                    if (rset.getString("SD") != null) {
                        String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));

                        if (rset.getString("YR") != null) {
                            String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                            strSD = perl.substitute("s/,*\\s*\\d\\d\\d\\d//", strSD);
                            strSD = strSD.trim();
                            if (strSD.equals(StringUtil.EMPTY_STRING)) {
                                strSD = strSD.concat(strYR);
                            } else {
                                strSD = perl.substitute("s#,$##", strSD);
                                strSD = strSD.concat(", ").concat(strYR);
                            }
                        }

                        if (!strSD.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.PATENT_ISSUE_DATE, new XMLWrapper(Keys.PATENT_ISSUE_DATE, strSD));
                        }
                    } else if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        if (!strYR.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.PUBLICATION_DATE_PAPER, new Year(Keys.PUBLICATION_DATE_PAPER, strYR, perl));
                        }
                    }
                } else {
                    if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("SOURC") == null) {
                        // SD
                        String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));
                        if ((rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("PN") != null)
                            && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                            if (rset.getString("YR") != null) {
                                String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                                strSD = perl.substitute("s/,*\\s*\\d\\d\\d\\d//", strSD);
                                strSD = strSD.trim();
                                if (strSD.equals(StringUtil.EMPTY_STRING)) {
                                    strSD = strSD.concat(strYR);
                                } else {
                                    strSD = perl.substitute("s#,$##", strSD);
                                    strSD = strSD.concat(", ").concat(strYR);
                                }
                            }
                            if (!strSD.equals(StringUtil.EMPTY_STRING)) {
                                ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));
                            }

                        } else if (rset.getString("YR") != null) {
                            String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));

                            if (!strYR.equals(StringUtil.EMPTY_STRING)) {
                                if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null
                                    && rset.getString("PN") == null) {
                                    // if ALL 4 are null, use the label 'Publication Date'
                                    ht.put(Keys.PUBLICATION_DATE_PAPER, new Year(Keys.PUBLICATION_DATE_PAPER, strYR, perl));
                                } else {
                                    // else just store the date
                                    ht.put(Keys.PUBLICATION_YEAR_PAPER, new Year(Keys.PUBLICATION_YEAR_PAPER, strYR, perl));
                                }
                            }

                        }
                    }
                }

                // These are needed for local holding.
                if (rset.getString("ST") != null) {
                    String st = rset.getString("ST");
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, st));
                }

                if (rset.getString("SE") != null) {
                    String se = rset.getString("SE");
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, se));
                }

                if (rset.getString("YR") != null) {
                    String strYR = rset.getString("YR");
                    ht.put(Keys.PUBLICATION_YEAR, new Year(Keys.PUBLICATION_YEAR, strYR, perl));
                }

                if (rset.getString("PA") != null) {
                    String pa = StringUtil.replaceNullWithEmptyString(rset.getString("PA"));

                    if (!pa.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, pa));
                    }
                }

                if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("SOURC") == null) {
                    // PP
                    if (rset.getString("XP") != null) {
                        String pagerange = StringUtil.replaceNullWithEmptyString(rset.getString("XP"));

                        if (!pagerange.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.PAGE_RANGE, new PageRange(pagerange, perl));
                        }
                    } else if (rset.getString("PP") != null) {
                        String pagerange = StringUtil.replaceNullWithEmptyString(rset.getString("PP"));

                        if (!pagerange.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.PAGE_RANGE, new PageRange(pagerange, perl));
                        }
                    }
                }

                // LA
                if ((rset.getString("LA") != null) && (!rset.getString("LA").equalsIgnoreCase("ENGLISH"))) {
                    String la = StringUtil.replaceNullWithEmptyString(rset.getString("LA"));

                    if (!la.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, la));
                    }
                }
                // CF
                if ((rset.getString("CF") != null) || (rset.getString("M2") != null) || (rset.getString("MC") != null)) {
                    String cfname = StringUtil.replaceNullWithEmptyString(rset.getString("CF"));

                    if (!cfname.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, cfname));
                    }
                }

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
                    }
                    if (rset.getString("MY") != null) {
                        lstCF.add(rset.getString("MY"));
                    }
                    String cfdate = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstCF, ", "));

                    if (!cfdate.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, cfdate));
                    }

                    lstCF = null;
                }

                // SP - SPONSOR'S INFO
                if (rset.getString("SP") != null) {

                    List<String> lstSponsor = new ArrayList<String>();
                    lstSponsor.add(rset.getString("SP"));

                    String sponsor = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstSponsor, ", "));

                    if (!sponsor.equals(StringUtil.EMPTY_STRING)) {
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
                    }
                    if (rset.getString("PY") != null) {
                        lstPL.add(rset.getString("PY"));
                    }
                    String publocation = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstPL, ", "));

                    if (!publocation.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PUB_LOCATION, new XMLWrapper(Keys.PUB_LOCATION, publocation));
                    }
                    lstPL = null;
                }

                // CVS
                if (rset.getString("PT") != null) {

                    String pt = StringUtil.replaceNullWithEmptyString(rset.getString("PT"));
                    if (!pt.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(PAPER_CONTROLLED_TERMS, new XMLMultiWrapper(PAPER_CONTROLLED_TERMS, setElementData(pt)));
                    }
                }

                // FLS
                if (rset.getString("FL") != null) {

                    String fl = StringUtil.replaceNullWithEmptyString(rset.getString("FL"));

                    if (!fl.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(fl)));
                    }
                }

                // NR
                if (rset.getString("NR") != null) {
                    String strREFs = rset.getString("NR");

                    if (perl.match("/(\\d+)/", strREFs)) {
                        String refs = StringUtil.replaceNullWithEmptyString(perl.group(0).toString());

                        if (!refs.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, refs));
                        }
                    }
                }

                // CN
                if (rset.getString("CN") != null && !rset.getString("CN").trim().equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, rset.getString("CN")));
                }

                // BN
                if (rset.getString("BN") != null) {
                    String isbn = StringUtil.replaceNullWithEmptyString(rset.getString("BN"));
                    if (!isbn.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.ISBN, new ISBN(isbn));
                    }
                }
                // SN
                if (rset.getString("SN") != null) {
                    String issn = StringUtil.replaceNullWithEmptyString(rset.getString("SN"));
                    if (!issn.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.ISSN, new ISSN(issn));
                    }
                }

                // AB
                if (rset.getClob("AB") != null) {
                    String abs = checkAbstract(StringUtil.getStringFromClob(rset.getClob("AB")));
                    if (!abs.trim().equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                    }
                }

                // AT
                if (rset.getString("AT") != null) {
                    String atype = StringUtil.replaceNullWithEmptyString(rset.getString("AT"));
                    if (!atype.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.ABSTRACT_TYPE, new XMLWrapper(Keys.ABSTRACT_TYPE, atype));
                    }
                }

                if (rset.getString("DO") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DO")));
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
                } catch (ConnectionPoolException cpe) {
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
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, PAPER_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, PAPER_TEXT_COPYRIGHT));

                if (rset.getString("VX") != null && !rset.getString("VX").trim().equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.REPORT_NUMBER, new XMLWrapper(Keys.REPORT_NUMBER, rset.getString("VX")));
                }

                // AN
                if (rset.getString("AN") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("AN")));
                }

                // TI
                if (rset.getString("TI") != null) {
                    String ti = StringUtil.replaceNullWithEmptyString(rset.getString("TI"));
                    if (!ti.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, ti));
                    }
                }
                // TT

                if (rset.getString("TT") != null) {
                    String tt = StringUtil.replaceNullWithEmptyString(rset.getString("TT"));

                    if (!tt.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.TITLE_TRANSLATION, new XMLWrapper(Keys.TITLE_TRANSLATION, tt));
                    }
                }

                if (rset.getString("AU") != null) {
                    String au = StringUtil.replaceNullWithEmptyString(rset.getString("AU"));

                    if (!au.equals(StringUtil.EMPTY_STRING)) {
                        Contributors authors = new Contributors(Keys.AUTHORS, getContributors(au, Keys.AUTHORS));
                        ht.put(Keys.AUTHORS, authors);
                    }
                } else {
                    if (rset.getString("ED") != null) {
                        String strED = StringUtil.replaceNullWithEmptyString(rset.getString("ED"));
                        if (perl.match("/(Ed[.]\\s*)/", strED)) {
                            strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                        }

                        if (!strED.trim().equals(StringUtil.EMPTY_STRING)) {
                            Contributors editors = new Contributors(Keys.EDITORS, getContributors(strED, Keys.EDITORS));
                            ht.put(Keys.EDITORS, editors);
                        }

                        if (rset.getString("EF") != null) {
                            String ef = StringUtil.replaceNullWithEmptyString(rset.getString("EF"));

                            if (!ef.equals(StringUtil.EMPTY_STRING)) {
                                Affiliation eaffil = new Affiliation(Keys.EDITOR_AFFS, ef);
                                ht.put(Keys.EDITOR_AFFS, new Affiliations(Keys.EDITOR_AFFS, eaffil));
                            }
                        }
                    }
                }

                if (rset.getString("AF") != null) {
                    List<String> lstAFF = new ArrayList<String>();

                    lstAFF.add(rset.getString("AF"));
                    if (rset.getString("AC") != null) {
                        lstAFF.add(rset.getString("AC"));
                    }
                    if (rset.getString("ASP") != null) {
                        lstAFF.add(rset.getString("ASP"));
                    }
                    if (rset.getString("AY") != null) {
                        lstAFF.add(rset.getString("AY"));
                    }

                    String af = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstAFF, ", "));

                    if (!af.equals(StringUtil.EMPTY_STRING)) {
                        Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, af);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }

                    lstAFF = null;
                }

                if ((rset.getString("ST") != null || rset.getString("SE") != null)) {

                    // ST
                    if (rset.getString("ST") != null) {
                        String st = StringUtil.replaceNullWithEmptyString(rset.getString("ST"));

                        if (!st.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, st));
                        }
                    }
                    // SE
                    if (rset.getString("SE") != null) {
                        String se = StringUtil.replaceNullWithEmptyString(rset.getString("SE"));

                        if (!se.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, se));
                        }
                    }
                    // VO
                    if (rset.getString("VO") != null) {
                        String strVol = replaceVolumeNullWithEmptyString(rset.getString("VO"));

                        if (!strVol.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.VOLUME, new Volume(strVol, perl));
                        }
                    }
                    // ISS
                    if (rset.getString("ISS") != null) {
                        String strIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));

                        if (!strIss.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.ISSUE, new Issue(strIss, perl));
                        }
                    }
                    // SD
                    if (rset.getString("SD") != null) {
                        String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));

                        if (!strSD.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));
                        }
                    }

                    // MT
                    if (rset.getString("MT") != null) {
                        String mt = StringUtil.replaceNullWithEmptyString(rset.getString("MT"));

                        if (!mt.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, mt));
                        }
                    }
                } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") != null)) {
                    // MT
                    if (rset.getString("MT") != null) {
                        String mt = StringUtil.replaceNullWithEmptyString(rset.getString("MT"));

                        if (!mt.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, mt));
                        }
                    }
                    // VO
                    if (rset.getString("VO") != null) {
                        String strVol = replaceVolumeWithEmptyString(rset.getString("VO"));

                        if (!strVol.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.VOLUME, new Volume(strVol, perl));
                        }
                    }

                    // ISS
                    if (rset.getString("ISS") != null) {
                        String strIss = replaceIssueWithEmptyString(rset.getString("ISS"));

                        if (!strIss.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.ISSUE, new Issue(strIss, perl));
                        }
                    }
                }

                // PAS - Assignee
                if ((rset.getString("ASSIG") != null)) {

                    List<String> lstAsg = convertString2List(StringUtil.substituteChars(StringUtil.replaceNullWithEmptyString(rset.getString("ASSIG"))));

                    if (lstAsg != null && lstAsg.size() > 0) {
                        ht.put(Keys.PATASSIGN, new XMLMultiWrapper(Keys.PATASSIGN, setAssignees(lstAsg)));

                    }
                }

                // PAP - Application number
                if ((rset.getString("APPLN") != null)) {
                    String appln = StringUtil.replaceNullWithEmptyString(rset.getString("APPLN"));

                    if (!appln.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PATAPPNUM, new XMLWrapper(Keys.PATAPPNUM, appln));
                    }
                }

                if ((rset.getString("PATNO") != null)) {
                    ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, StringUtil.replaceNullWithEmptyString(rset.getString("PATNO"))));
                }

                // PRIORN - Priority number
                if ((rset.getString("PRIOR_NUM") != null)) {
                    ht.put(
                        Keys.PRIORITY_INFORMATION,
                        new XMLMultiWrapper(Keys.PRIORITY_INFORMATION, setElementData(((formatPriorityInfo(StringUtil.replaceNullWithEmptyString(rset
                            .getString("PRIOR_NUM"))))))));

                }

                // SD for DT - PA
                if ((rset.getString("SD") != null) && (rset.getString("DT").equalsIgnoreCase("PA"))) {

                    String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));

                    if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        strSD = perl.substitute("s/,*\\s*\\d\\d\\d\\d//", strSD);
                        strSD = strSD.trim();
                        if (strSD.equals(StringUtil.EMPTY_STRING)) {
                            strSD = strSD.concat(strYR);
                        } else {
                            strSD = perl.substitute("s#,$##", strSD);
                            strSD = strSD.concat(", ").concat(strYR);
                        }
                    }
                    if (strSD != null) {
                        ht.put(Keys.PATENT_ISSUE_DATE, new XMLWrapper(Keys.PATENT_ISSUE_DATE, strSD));
                    }
                }

                // YR
                if (rset.getString("YR") != null) {
                    String strYR = rset.getString("YR");
                    ht.put(Keys.PUBLICATION_YEAR, new Year(Keys.PUBLICATION_YEAR, strYR, perl));
                }

                // PA
                if (rset.getString("PA") != null) {

                    String pa = StringUtil.replaceNullWithEmptyString(rset.getString("PA"));
                    if (!pa.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, pa));
                    }
                }

                // PP
                String strPages = StringUtil.EMPTY_STRING;
                if (rset.getString("XP") != null) {
                    strPages = rset.getString("XP");
                    MatchResult mResult = null;
                    if (strPages != null) {
                        if ((perl.match("#\\-#", strPages)) || (perl.match("#\\+#", strPages)) || (perl.match("#\\,#", strPages))) {
                            strPages = perl.substitute("s#p##ig", strPages);
                            strPages = perl.substitute("s#\\.# #ig", strPages);
                            strPages = perl.substitute("s#pp##ig", strPages);
                        }
                    }

                } else {
                    strPages = rset.getString("PP");
                }

                if (strPages != null && !strPages.equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));
                }

                // Strip out and store the start page and end page

                // FIX BUG BELLOW -- NULL POINTER ON strPages

                if (strPages != null) {
                    // if(perl.match("/(\\d+)/", strPages)) {
                    // ht.put(EIDoc.START_PAGE,perl.group(0).toString());
                    // if(perl.match("/(\\d+)/", perl.postMatch())) {
                    // ht.put(EIDoc.END_PAGE,perl.group(0).toString());
                    // }
                    // }

                    if (perl.match("/(\\d+)/", strPages)) {
                        ht.put(Keys.START_PAGE, new XMLWrapper(Keys.START_PAGE, perl.group(0).toString()));

                        if (perl.match("/(\\d+)/", perl.postMatch())) {
                            ht.put(Keys.END_PAGE, new XMLWrapper(Keys.END_PAGE, perl.group(0).toString()));
                        }
                    }
                }

                // SO
                if (rset.getString("SOURC") != null) {
                    String sourc = StringUtil.replaceNullWithEmptyString(rset.getString("SOURC"));

                    if (!sourc.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.SOURCE, new XMLWrapper(PAPER_SOURCE, sourc));
                    }
                }

                // LA
                if (rset.getString("LA") != null) {

                    String la = StringUtil.replaceNullWithEmptyString(rset.getString("LA"));

                    if (!la.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, la));
                    }
                }
                // SN
                if (rset.getString("SN") != null) {
                    String sn = StringUtil.replaceNullWithEmptyString(rset.getString("SN"));

                    if (!sn.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.ISSN, new ISSN(sn));
                    }
                }
                // CN
                if (rset.getString("CN") != null) {
                    String cn = StringUtil.replaceNullWithEmptyString(rset.getString("CN"));

                    if (!cn.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, cn));
                    }
                }

                // BN
                if (rset.getString("BN") != null) {
                    String bn = StringUtil.replaceNullWithEmptyString(rset.getString("BN"));

                    if (!bn.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.ISBN, new ISBN(bn));
                    }
                }

                // DT
                if (rset.getString("DT") != null) {
                    String dt = replaceDTNullWithEmptyString(rset.getString("DT"));

                    if (!dt.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, dt));
                    }
                }

                // CF
                if (rset.getString("CF") != null) {
                    String cf = StringUtil.replaceNullWithEmptyString(rset.getString("CF"));

                    if (!cf.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, cf));
                    }
                }

                // MD

                if (rset.getString("M2") != null) {
                    String m2 = StringUtil.replaceNullWithEmptyString(rset.getString("M2"));
                    if (!m2.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, m2));
                    }
                } else if (rset.getString("MD") != null) {
                    // ht.put(EIDoc.CONF_DATE,StringUtil.replaceNullWithEmptyString(rset.getString("MD")));
                    String md = StringUtil.replaceNullWithEmptyString(rset.getString("MD"));

                    if (!md.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, md));
                    }
                }

                // ML
                List<String> lstMeetLoc = new ArrayList<String>();
                if (rset.getString("MC") != null) {
                    lstMeetLoc.add(rset.getString("MC"));
                }
                if (rset.getString("MS") != null) {
                    lstMeetLoc.add(rset.getString("MS"));
                }
                if (rset.getString("MY") != null) {
                    lstMeetLoc.add(rset.getString("MY"));
                }
                if (lstMeetLoc.size() > 0) {
                    String ml = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstMeetLoc, ", "));

                    if (!ml.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.MEETING_LOCATION, new XMLWrapper(Keys.MEETING_LOCATION, ml));
                    }
                }

                lstMeetLoc = null;

                // CSESS
                if (rset.getString("CSESS") != null) {
                    String csess = StringUtil.replaceNullWithEmptyString(rset.getString("CSESS"));
                    if (!csess.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.SESSION_NAME_NUMBER, new XMLWrapper(Keys.SESSION_NAME_NUMBER, csess));
                    }
                }

                // CC

                if (rset.getString("CC") != null) {
                    String cc = StringUtil.replaceNullWithEmptyString(rset.getString("CC"));
                    if (!cc.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.CONF_CODE, new XMLWrapper(Keys.CONF_CODE, cc));
                    }
                }

                // SP
                List<String> lstSponsor = new ArrayList<String>();
                if (rset.getString("SP") != null) {
                    lstSponsor.add(rset.getString("SP"));
                }
                if (lstSponsor.size() > 0) {
                    String sp = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstSponsor, ", "));
                    if (!sp.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.SPONSOR, new XMLWrapper(Keys.SPONSOR, sp));
                    }
                }

                lstSponsor = null;

                // PN, PL
                List<String> lstTokens = new ArrayList<String>();
                if (rset.getString("PN") != null) {
                    lstTokens.add((String) rset.getString("PN"));
                }
                if (rset.getString("PC") != null) {
                    lstTokens.add((String) rset.getString("PC").trim());
                }
                if (rset.getString("PS") != null) {
                    lstTokens.add((String) rset.getString("PS"));
                }
                if (rset.getString("PY") != null) {
                    lstTokens.add((String) rset.getString("PY"));
                }

                if (lstTokens.size() > 0) {
                    String pub = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstTokens, ", "));
                    if (!pub.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, pub));
                    }
                }
                lstTokens = null;

                // AB
                if (rset.getClob("AB") != null) {
                    String abs = checkAbstract(StringUtil.getStringFromClob(rset.getClob("AB")));

                    if (!abs.trim().equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                    }

                }

                // AT

                if (rset.getString("AT") != null) {
                    String aty = StringUtil.replaceNullWithEmptyString(rset.getString("AT"));
                    if (!aty.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.ABSTRACT_TYPE, new XMLWrapper(Keys.ABSTRACT_TYPE, aty));
                    }
                }

                // // CLAIM
                if (rset.getString("CLAIM") != null) {
                    String claim = StringUtil.replaceNullWithEmptyString(rset.getString("CLAIM"));

                    if (!claim.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.NUMBER_OF_CLAIMS, new XMLWrapper(Keys.NUMBER_OF_CLAIMS, claim));
                    }
                }

                // NOFIG
                if (rset.getString("NOFIG") != null) {
                    String nofig = StringUtil.replaceNullWithEmptyString(rset.getString("NOFIG"));
                    if (!nofig.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.NUMBER_OF_FIGURES, new XMLWrapper(Keys.NUMBER_OF_FIGURES, nofig));
                    }
                }

                // NOTAB
                if (rset.getString("NOTAB") != null) {
                    String notab = StringUtil.replaceNullWithEmptyString(rset.getString("NOTAB"));
                    if (!notab.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.NUMBER_OF_TABLES, new XMLWrapper(Keys.NUMBER_OF_TABLES, notab));
                    }
                }

                // NR
                if (rset.getString("NR") != null) {
                    String strREFs = rset.getString("NR");

                    if (perl.match("/(\\d+)/", strREFs)) {
                        strREFs = StringUtil.replaceNullWithEmptyString(perl.group(0).toString());

                        if (!strREFs.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, strREFs));
                        }
                    }
                }

                // CVS
                if (rset.getString("PT") != null) {
                    String pt = StringUtil.replaceNullWithEmptyString(rset.getString("PT"));
                    if (!pt.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(PAPER_CONTROLLED_TERMS, new XMLMultiWrapper(PAPER_CONTROLLED_TERMS, setElementData(pt)));
                    }
                }

                // FLS
                if (rset.getString("FL") != null) {
                    String fl = StringUtil.replaceNullWithEmptyString(rset.getString("FL"));
                    if (!fl.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(fl)));
                    }
                }

                // SPECN
                if (rset.getString("SPECN") != null) {
                    String specn = StringUtil.replaceNullWithEmptyString(rset.getString("SPECN"));
                    if (!specn.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.SPECIFIC_NAMES, new XMLWrapper(Keys.SPECIFIC_NAMES, specn));
                    }
                }

                if (rset.getString("DO") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DO")));
                }

                EIDoc eiDoc = new EIDoc(did, ht, Detail.FULLDOC_FORMAT);
                eiDoc.setLoadNumber(rset.getInt("LOAD_NUMBER"));
                eiDoc.exportLabels(true);
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
                } catch (ConnectionPoolException cpe) {
                    cpe.printStackTrace();
                }
            }
        }

        return list;
    }

    private List<EIDoc> loadRIS(List<DocID> listOfDocIDs) throws DocumentBuilderException {
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
            rset = stmt.executeQuery(queryDetailed + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));

                String strDocType = StringUtil.replaceNullWithEmptyString(rset.getString("DT"));

                String ty = replaceTYwithRIScode(rset.getString("DT"));

                if (!ty.equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, ty));
                }

                ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, PaperChemDocBuilder.PAPER_TEXT_COPYRIGHT));

                if (rset.getString("TI") != null) {
                    String ti = StringUtil.replaceNullWithEmptyString(rset.getString("TI"));

                    if (!ti.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.RIS_TI, new XMLWrapper(Keys.RIS_TI, ti));
                    }
                }
                if (rset.getString("TT") != null) {
                    String tt = StringUtil.replaceNullWithEmptyString(rset.getString("TT"));
                    if (!tt.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.RIS_T1, new XMLWrapper(Keys.RIS_T1, tt));
                    }
                }
                if (rset.getString("MT") != null) {
                    String mt = StringUtil.replaceNullWithEmptyString(rset.getString("MT"));
                    if (!mt.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, mt));
                    }
                }

                if (strDocType.equalsIgnoreCase("JA")) {
                    if (rset.getString("ST") != null) {
                        String st = StringUtil.replaceNullWithEmptyString(rset.getString("ST"));
                        if (!st.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.RIS_JO, new XMLWrapper(Keys.RIS_JO, st));
                        }
                    }
                } else {
                    if (rset.getString("ST") != null) {
                        String t3 = StringUtil.replaceNullWithEmptyString(rset.getString("ST"));

                        if (!t3.equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.RIS_T3, new XMLWrapper(Keys.RIS_T3, t3));
                        }
                    }
                }

                if (rset.getString("AU") != null)

                {
                    String au = StringUtil.replaceNullWithEmptyString(rset.getString("AU"));

                    if (!au.equals(StringUtil.EMPTY_STRING)) {
                        Contributors authors = new Contributors(Keys.RIS_AUS, getContributors(au, Keys.RIS_AUS));
                        ht.put(Keys.RIS_AUS, authors);

                    }

                    if (rset.getString("AF") != null) {
                        List<String> lstAFF = new ArrayList<String>();

                        lstAFF.add(rset.getString("AF"));
                        if (rset.getString("AC") != null) {
                            lstAFF.add(rset.getString("AC"));
                        }
                        if (rset.getString("ASP") != null) {
                            lstAFF.add(rset.getString("ASP"));
                        }
                        if (rset.getString("AY") != null) {
                            lstAFF.add(rset.getString("AY"));
                        }

                        String straf = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstAFF, ", "));

                        if (!straf.equals(StringUtil.EMPTY_STRING)) {
                            Affiliation aff = new Affiliation(Keys.RIS_AD, straf);
                            ht.put(Keys.RIS_AD, new Affiliations(Keys.RIS_AD, aff));
                        }
                        lstAFF = null;
                    }
                } else

                {
                    if (rset.getString("ED") != null) {
                        String strED = StringUtil.replaceNullWithEmptyString(rset.getString("ED"));
                        if (perl.match("/(Ed[.]\\s*)/", strED)) {
                            strED = perl.substitute("s/\\(Ed[.]\\s*\\)//gi", strED);
                        }

                        if (!strED.equals(StringUtil.EMPTY_STRING)) {
                            Contributors editors = new Contributors(Keys.RIS_EDS, getContributors(strED, Keys.RIS_EDS));

                            ht.put(Keys.RIS_EDS, editors);
                        }

                        if (rset.getString("EF") != null) {
                            String stref = StringUtil.replaceNullWithEmptyString(rset.getString("EF"));
                            if (!stref.equals(StringUtil.EMPTY_STRING)) {
                                Affiliation eff = new Affiliation(Keys.RIS_AD, stref);
                                ht.put(Keys.RIS_AD, new Affiliations(Keys.RIS_AD, eff));
                            }

                        }
                    }
                }

                if (rset.getString("VO") != null) {
                    String strVol = replaceVolumeNullWithEmptyString(rset.getString("VO"));
                    if (!strVol.equals(StringUtil.EMPTY_STRING)) {
                        strVol = perl.substitute("s/[vol\\.]//gi", strVol);
                        ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, strVol));
                    }
                }

                if (rset.getString("ISS") != null) {
                    String strIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                    strIss = perl.substitute("s/[no\\.]//gi", strIss);
                    ht.put(Keys.RIS_IS, new Issue(strIss, perl));
                }

                if (rset.getString("YR") != null) {
                    String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                    if (!strYR.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, strYR, perl));
                    }
                }

                if (rset.getString("AN") != null) {
                    String strAN = StringUtil.replaceNullWithEmptyString(rset.getString("AN"));

                    if (!strAN.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.RIS_AN, new XMLWrapper(Keys.RIS_AN, strAN));
                    }
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
                    ht.put(Keys.RIS_SN, new ISSN(rset.getString("SN")));
                }
                if (rset.getString("BN") != null) {
                    ht.put(Keys.RIS_S1, new ISBN(rset.getString("BN")));
                }

                // CF
                if (rset.getString("CF") != null) {
                    String cf = StringUtil.replaceNullWithEmptyString(rset.getString("CF"));

                    if (!cf.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, cf));
                    }
                }
                // MD
                if (rset.getString("M2") != null) {
                    String m2 = StringUtil.replaceNullWithEmptyString(rset.getString("M2"));
                    if (!m2.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.RIS_MD, new XMLWrapper(Keys.RIS_MD, m2));
                    }
                } else if (rset.getString("MD") != null) {
                    String m2 = StringUtil.replaceNullWithEmptyString(rset.getString("MD"));
                    if (!m2.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.RIS_MD, new XMLWrapper(Keys.RIS_MD, m2));
                    }
                }
                // ML
                List<String> lstMeetLoc = new ArrayList<String>();
                if (rset.getString("MC") != null) {
                    lstMeetLoc.add(rset.getString("MC"));
                }
                if (rset.getString("MS") != null) {
                    lstMeetLoc.add(rset.getString("MS"));
                }
                if (rset.getString("MY") != null) {
                    lstMeetLoc.add(rset.getString("MY"));
                }

                if (lstMeetLoc.size() > 0) {
                    ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, StringUtil.replaceNullWithEmptyString(StringUtil.join(lstMeetLoc, ", "))));
                }

                lstMeetLoc = null;

                // PN, PL
                List<String> lstTokens = new ArrayList<String>();
                if (rset.getString("PN") != null) {
                    lstTokens.add((String) rset.getString("PN"));
                }
                if (rset.getString("PC") != null) {
                    lstTokens.add((String) rset.getString("PC").trim());
                }
                if (rset.getString("PS") != null) {
                    lstTokens.add((String) rset.getString("PS"));
                }
                if (rset.getString("PY") != null) {
                    lstTokens.add((String) rset.getString("PY"));
                }

                if (lstTokens.size() > 0) {
                    String pb = StringUtil.replaceNullWithEmptyString(StringUtil.join(lstTokens, ", "));
                    if (!pb.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, pb));
                    }
                    lstTokens = null;
                }

                lstTokens = null;

                String abs = null;
                if ((abs = checkAbstract(StringUtil.getStringFromClob(rset.getClob("AB")))) != null) {
                    ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, abs));
                }
                // CVS
                String cvs = StringUtil.replaceNullWithEmptyString(rset.getString("PT"));
                if (!cvs.equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(cvs)));
                }
                // FLS
                String fls = StringUtil.replaceNullWithEmptyString(rset.getString("FL"));
                if (!fls.equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.RIS_FLS, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(fls)));
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
                } catch (ConnectionPoolException cpe) {
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

                // Common Fields
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                ht.put(Keys.DOCID, did);

                ht.put(Keys.PROVIDER, new XMLWrapper(Keys.PROVIDER, PaperChemDocBuilder.PROVIDER_TEXT));
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, PaperChemDocBuilder.PAPER_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, PaperChemDocBuilder.PAPER_TEXT_COPYRIGHT));

                // Always needed for IVIP
                if (rset.getString("SN") != null) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("SN")));
                }

                String strTitle = StringUtil.EMPTY_STRING;

                if ((rset.getString("TT") != null) && (rset.getString("TI") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TT"))).concat(")");
                } else if (rset.getString("TI") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI")).concat(strTitle);
                } else if (rset.getString("TT") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI")).concat(strTitle);
                }
                if (!strTitle.equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));
                    strTitle = null;
                }

                if (rset.getString("AU") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.replaceNullWithEmptyString(rset.getString("AU")),
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
                        Contributors editors = new Contributors(Keys.EDITORS, getContributors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);

                        if (rset.getString("EF") != null) {
                            Affiliation eaffil = new Affiliation(Keys.EDITOR_AFFS, StringUtil.replaceNullWithEmptyString(rset.getString("EF")));
                            ht.put(Keys.EDITOR_AFFS, new Affiliations(Keys.EDITOR_AFFS, eaffil));
                        }
                    }
                }

                // Patent Records
                String strDocType = StringUtil.replaceNullWithEmptyString(rset.getString("DT"));
                if ("PA".equalsIgnoreCase(strDocType)) {

                    // suppress 'Source:' label
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));

                    // PAS - Assignee
                    if ((rset.getString("ASSIG") != null)) {

                        List<String> lstAsg = convertString2List(StringUtil.substituteChars(rset.getString("ASSIG")));

                        if (lstAsg != null && lstAsg.size() > 0) {

                            ht.put(Keys.PATASSIGN, new XMLMultiWrapper(Keys.PATASSIGN, setAssignees(lstAsg)));

                        }
                    }
                    if ((rset.getString("APPLN") != null)) {
                        ht.put(Keys.PATAPPNUM, new XMLWrapper(Keys.PATAPPNUM, StringUtil.replaceNullWithEmptyString(rset.getString("APPLN"))));
                    }
                    if ((rset.getString("PATNO") != null)) {
                        ht.put(Keys.PATNUM, new XMLWrapper(Keys.PATNUM, StringUtil.replaceNullWithEmptyString(rset.getString("PATNO"))));
                    }

                } // "PA".equalsIgnoreCase(strDocType)
                else {
                    // SO
                    if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("SOURC") != null
                        || rset.getString("PN") != null) {

                        if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("SOURC") == null) {
                            String strVolIss = StringUtil.EMPTY_STRING;

                            // VO - VOL and ISSUE Combined by ', '
                            // add 'v' or 'n'

                            if (rset.getString("VO") != null) {
                                strVolIss = strVolIss.concat(replaceVolumeNullWithEmptyString(rset.getString("VO")));
                                if (strVolIss != null && !strVolIss.trim().equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.VOLUME, new Volume(StringUtil.replaceNullWithEmptyString(rset.getString("VO")), perl));
                                }
                            }
                            if (rset.getString("ISS") != null) {
                                ht.put(Keys.ISSUE, new Issue(StringUtil.replaceNullWithEmptyString(rset.getString("ISS")), perl));
                                if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                                    strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(rset.getString("ISS")));
                                } else {
                                    strVolIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                                }
                            }
                            if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                                ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                            }

                        }
                        if (rset.getString("ST") != null || rset.getString("SE") != null) {

                            if (rset.getString("ST") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("ST"))));
                            } else if (rset.getString("SE") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("SE"))));
                            }
                            // an MT can accompany the ST (or SE)
                            if (rset.getString("MT") != null) {
                                ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                            }
                        } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") != null)) {
                            if (rset.getString("MT") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                            }
                        } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") == null)
                            && (rset.getString("SOURC") != null)) {
                            if (rset.getString("SOURC") != null) {
                                ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("SOURC"))));
                            }
                        } else if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null
                            && rset.getString("SOURC") == null) {
                            if (rset.getString("PN") != null) {
                                ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PN"))));
                            }
                        } else {

                        }
                    }
                }

                if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("SOURC") == null
                    && rset.getString("PN") == null) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                // SD
                if ("PA".equalsIgnoreCase(strDocType)) {
                    if (rset.getString("SD") != null) {
                        String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));

                        if (rset.getString("YR") != null) {
                            String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                            strSD = perl.substitute("s/,*\\s*\\d\\d\\d\\d//", strSD);
                            strSD = strSD.trim();
                            if (strSD.equals(StringUtil.EMPTY_STRING)) {
                                strSD = strSD.concat(strYR);
                            } else {
                                strSD = perl.substitute("s#,$##", strSD);
                                strSD = strSD.concat(", ").concat(strYR);
                            }
                        }

                        // ht.put(EIDoc.PATENT_ISSUE_DATE, strSD);
                        // String strYR = formatDate(StringUtil.replaceNullWithEmptyString(rset.getString("SD")));

                        if (strSD != null && !strSD.trim().equals(StringUtil.EMPTY_STRING)) {
                            ht.put(Keys.PATENT_ISSUE_DATE, new XMLWrapper(Keys.PATENT_ISSUE_DATE, strSD));
                        }

                    } else if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));

                        ht.put(Keys.PUBLICATION_DATE, new Year(strYR, perl));
                    }
                } else {
                    if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("SOURC") == null) {
                        // SD
                        String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));
                        if ((rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("PN") != null)
                            && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                            if (rset.getString("YR") != null) {
                                String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                                strSD = perl.substitute("s/,*\\s*\\d\\d\\d\\d//", strSD);
                                strSD = strSD.trim();
                                if (strSD.equals(StringUtil.EMPTY_STRING)) {
                                    strSD = strSD.concat(strYR);
                                } else {
                                    strSD = perl.substitute("s#,$##", strSD);
                                    strSD = strSD.concat(", ").concat(strYR);
                                }
                            }

                            if (strSD != null && !strSD.trim().equals(StringUtil.EMPTY_STRING)) {
                                ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));
                            }

                        } else if (rset.getString("YR") != null) {
                            String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                            if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("PN") == null) {
                                // if ALL 4 are null, use the label 'Publication Date'
                                // ht.put(EIDoc.PUBLICATION_DATE,strYR);
                                if (strYR != null && !strYR.equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.PUBLICATION_DATE, new Year(strYR, perl));
                                }
                            } else {
                                // else just store the date
                                // ht.put(EIDoc.PUBLICATION_YEAR,strYR);
                                if (strYR != null && !strYR.equals(StringUtil.EMPTY_STRING)) {
                                    ht.put(Keys.PUBLICATION_DATE, new Year(strYR, perl));
                                }
                            }
                        }
                    }
                }

                // These are needed for local holding.
                if (rset.getString("ST") != null) {
                    String st = rset.getString("ST");
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, st));
                }

                if (rset.getString("SE") != null) {
                    String se = rset.getString("SE");
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, se));
                }

                if (rset.getString("YR") != null) {
                    String strYR = rset.getString("YR");
                    ht.put(Keys.PUBLICATION_YEAR, new Year(Keys.PUBLICATION_YEAR, strYR, perl));
                }

                if (rset.getString("PA") != null) {

                    // ht.put(EIDoc.PAPER_NUMBER,StringUtil.replaceNullWithEmptyString(rset.getString("PA")));
                    ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("PA"))));

                }
                if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("SOURC") == null) {
                    // PP
                    if (rset.getString("XP") != null) {
                        ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(rset.getString("XP")), perl));

                        // ht.put(EIDoc.PAGE_RANGE,StringUtil.replaceNullWithEmptyString(rset.getString("XP")));
                    } else {
                        ht.put(Keys.PAGE_RANGE, new PageRange(StringUtil.replaceNullWithEmptyString(rset.getString("PP")), perl));

                        // ht.put(EIDoc.PAGE_RANGE,StringUtil.replaceNullWithEmptyString(rset.getString("PP")));
                    }
                }
                // LA
                if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LA"))));
                }

                // DO
                if (rset.getString("DO") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DO")));
                }

                // STT - INSPEC ONLY

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
                } catch (ConnectionPoolException cpe) {
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
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, CPXDocBuilder.CPX_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, PaperChemDocBuilder.PAPER_TEXT_COPYRIGHT));

                // Always needed for IVIP
                if (rset.getString("SN") != null) {
                    ht.put(Keys.ISSN, new ISSN(StringUtil.replaceNullWithEmptyString(rset.getString("SN"))));
                }

                String strTitle = StringUtil.EMPTY_STRING;
                if ((rset.getString("TT") != null) && (rset.getString("TI") != null)) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI"));
                    strTitle = strTitle.concat(" (").concat(StringUtil.replaceNullWithEmptyString(rset.getString("TT"))).concat(")");
                } else if (rset.getString("TI") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI")).concat(strTitle);
                } else if (rset.getString("TT") != null) {
                    strTitle = StringUtil.replaceNullWithEmptyString(rset.getString("TI")).concat(strTitle);
                }

                if (!strTitle.equals(StringUtil.EMPTY_STRING)) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));
                }

                // ACCESSION NUMBER

                if (rset.getString("AN") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("AN"))));
                }

                // AUS or EDS

                if (rset.getString("AU") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(StringUtil.replaceNullWithEmptyString(rset.getString("AU")),
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
                        Contributors editors = new Contributors(Keys.EDITORS, getContributors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);

                        if (rset.getString("EF") != null) {
                            Affiliation eaffil = new Affiliation(Keys.EDITOR_AFFS, StringUtil.replaceNullWithEmptyString(rset.getString("EF")));
                            ht.put(Keys.EDITOR_AFFS, new Affiliations(Keys.EDITOR_AFFS, eaffil));
                        }
                    }
                }

                // SO
                if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("PN") != null) {

                    // VO - VOL and ISSUE Combined by ', '
                    // add 'v' or 'n'

                    if (rset.getString("VO") != null) {
                        ht.put(Keys.VOLUME, new Volume(rset.getString("VO").replace('n', ' ').replace('v', ' '), perl));
                    }
                    if (rset.getString("ISS") != null) {

                        ht.put(Keys.ISSUE, new Issue(rset.getString("ISS").replace('n', ' '), perl));

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
                    } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") != null)) {
                        if (rset.getString("MT") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                        }
                    } else if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null) {
                        if (rset.getString("PN") != null) {
                            ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PN"))));
                        }
                    } else {
                    }
                }
                if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("PN") == null) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                // RN - INSPEC ONLY

                // SD
                String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));
                if ((rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("PN") != null)
                    && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                    if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        strSD = perl.substitute("s/".concat(strYR).concat("$//"), strSD);
                        strSD = strSD.trim().concat(", ").concat(strYR);
                    }
                    ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));

                } else if (rset.getString("YR") != null) {
                    String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                    if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("PN") == null) {
                        // if ALL 4 are null, use the label 'Publication Date'
                        ht.put(Keys.PUBLICATION_DATE, new Year(strYR, perl));
                    } else {
                        // else just store the date
                        ht.put(Keys.PUBLICATION_YEAR, new Year(strYR, perl));
                    }
                }

                // PA
                if (rset.getString("PA") != null) {
                    ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, StringUtil.replaceNullWithEmptyString((rset.getString("PA")))));
                }

                String strPages = StringUtil.EMPTY_STRING;
                if (rset.getString("XP") != null) {
                    strPages = rset.getString("XP");
                    MatchResult mResult = null;
                    if (strPages != null) {
                        if ((perl.match("#\\-#", strPages)) || (perl.match("#\\+#", strPages)) || (perl.match("#\\,#", strPages))) {
                            strPages = perl.substitute("s#p##ig", strPages);
                            strPages = perl.substitute("s#\\.# #ig", strPages);
                            strPages = perl.substitute("s#pp##ig", strPages);
                        }
                    }

                } else {
                    strPages = rset.getString("PP");
                }
                strPages = StringUtil.replaceNullWithEmptyString(rset.getString("PA"));
                if (strPages != null) {
                    strPages = strPages.replaceAll("p", " ");
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));
                }

                // CVS
                if (rset.getString("PT") != null) {
                    ht.put(PAPER_CONTROLLED_TERMS,
                        new XMLMultiWrapper(PAPER_CONTROLLED_TERMS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("PT")))));
                }

                // LA
                if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, StringUtil.replaceNullWithEmptyString(rset.getString("LA"))));
                }

                // DO
                if (rset.getString("DO") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, StringUtil.replaceNullWithEmptyString(rset.getString("DO"))));
                }

                // BN
                if (rset.getString("BN") != null) {
                    ht.put(Keys.ISBN, new ISBN(StringUtil.replaceNullWithEmptyString(rset.getString("BN"))));
                }

                if (rset.getString("PN") != null) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PN"))));
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

        if (!str.equals(StringUtil.EMPTY_STRING) && ((str.indexOf("v", 0) < 0) && (str.indexOf("V", 0) < 0))) {
            str = "v ".concat(str);
        }
        return str;
    }

    private String replaceVolumeWithEmptyString(String str)

    {
        if (str == null || str.equals("QQ")) {
            str = "";
        }
        if (((str.indexOf("v", 0) >= 0) || (str.indexOf("V", 0) >= 0))) {
            str = perl.substitute("s/[^\\d-,]//s", str);
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

    private String replaceIssueWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = "";
        }
        if (((str.indexOf("n", 0) >= 0) || (str.indexOf("N", 0) >= 0))) {
            str = perl.substitute("s/[^\\d-,]//s", str);
        }
        return str;
    }

    /* TS , end */
    private String checkAbstract(String s) {
        if (s == null) {
            return "";
        }

        if (s.length() < 100) {
            return "";
        }

        return s;
    }

    // jam XML document mapping, conversion to TY values
    // for RIS format - only called from loadDetailed
    private String replaceTYwithRIScode(String str) {
        if (str == null || str.equals("QQ")) {
            str = "";
        }

        if (!str.equals("")) {
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
            }
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

    public String[] setElementData(String elementVal) {
        ArrayList<String> list = new ArrayList<String>();
        AuthorStream aStream = null;
        String strToken = null;
        try {
            if (elementVal != null) {
                aStream = new AuthorStream(new ByteArrayInputStream(elementVal.getBytes()));
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

    public List<String> convertString2List(String sList) {

        if (sList == null)
            return new ArrayList<String>();

        List<String> lstVals = new ArrayList<String>();

        perl.split(lstVals, "/;/", sList);

        return lstVals;
    }

    private String[] setAssignees(List<String> lstAsg) {
        HashMap<String, String> dupMap = new HashMap<String, String>();
        ArrayList<String> assignees = new ArrayList<String>();
        for (int i = 0; i < lstAsg.size(); i++) {

            String assignee = (String) lstAsg.get(i);
            String upAs = assignee.toUpperCase().trim();
            if (!dupMap.containsKey(upAs)) {
                assignees.add(assignee);
                dupMap.put(upAs, "");
            }

        }

        return (String[]) assignees.toArray(new String[assignees.size()]);
    }

    private String formatPriorityInfo(String priorityInfo) {

        StringBuffer pi = new StringBuffer();
        StringTokenizer st = new StringTokenizer(priorityInfo, ";");

        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (!token.equals(""))
                pi.append(token).append(";");
        }

        return priorityInfo;
    }

    public String formatDate(String date) {
        StringBuffer result = null;
        if ((date != null) && (date.length() > 5)) {
            result = new StringBuffer();
            result.append(date.substring(4, 6));
            result.append("/");
            result.append(date.substring(6));
            result.append("/");
            result.append(date.substring(0, 4));
            return result.toString();
        } else {
            return date;
        }

    }

    private String getPage(String strPageRange, String strPageCount, String strArticleNum, String strISSN, String strDOI, String strYear) {
        String strPage = StringUtil.EMPTY_STRING;

        if (strPageRange != null) {
            strPage = strPageRange;
        } else if (strPageCount != null) {
            strPage = strPageCount;
        }

        if (strArticleNum != null) // Records with AR field Fix
        {
            if (strISSN != null && hasARFix(strISSN)) // Check ISSN for AR problem
            {
                strPage = "p " + strArticleNum;
            }
        }

        return strPage;

    }

    private static boolean hasARFix(String strISSN) // Checks ISSNs with AR field problems
    {
        if (issnARFix.containsKey(strISSN.replaceAll("-", ""))) {
            return true;
        } else {
            return false;
        }
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

// End Of PaperChemDocBuilder
