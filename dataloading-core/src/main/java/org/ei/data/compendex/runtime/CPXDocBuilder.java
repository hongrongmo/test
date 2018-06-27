package org.ei.data.compendex.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

public class CPXDocBuilder implements DocumentBuilder {
    public static String CPX_TEXT_COPYRIGHT = Database.DEFAULT_ELSEVIER_TEXT_COPYRIGHT;
    public static String CPX_HTML_COPYRIGHT = Database.DEFAULT_ELSEVIER_HTML_COPYRIGHT;
    public static String PROVIDER_TEXT = "Ei";
    private static Map<String, String> issnARFix = new HashMap<String, String>();
    private static final Key CPX_CONTROLLED_TERMS = new Key(Keys.CONTROLLED_TERMS, "Ei controlled terms");
    private static final Key CPX_CLASS_CODES = new Key(Keys.CLASS_CODES, "Ei classification codes");
    private static final Key CPX_MAIN_HEADING = new Key(Keys.MAIN_HEADING, "Ei main heading");
    public static final Key[] CITATION_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.AUTHOR_AFFS, Keys.SOURCE, Keys.MONOGRAPH_TITLE,
        Keys.PAGE_RANGE, Keys.VOLISSUE, Keys.PUBLICATION_YEAR, Keys.PUBLISHER, Keys.ISSUE_DATE, Keys.ISSN, Keys.LANGUAGE, Keys.NO_SO, Keys.COPYRIGHT,
        Keys.COPYRIGHT_TEXT, Keys.DOC_TYPE, Keys.DOI };
    public static final Key[] ABSTRACT_KEYS = { Keys.DOCID, Keys.DOC_TYPE, Keys.TITLE, Keys.EDITORS, Keys.AUTHORS, Keys.EDITOR_AFFS, Keys.AUTHOR_AFFS, Keys.VOLISSUE,
        Keys.SOURCE, Keys.PUBLICATION_YEAR, Keys.ISSUE_DATE, Keys.MONOGRAPH_TITLE, Keys.PAGE_RANGE, Keys.CONFERENCE_NAME, Keys.ISSN, Keys.ISBN, Keys.CODEN,
        Keys.PUBLISHER, Keys.I_PUBLISHER, Keys.CONF_DATE, Keys.SPONSOR, Keys.PROVIDER, Keys.LANGUAGE, Keys.MAIN_HEADING, CPX_CONTROLLED_TERMS,
        Keys.UNCONTROLLED_TERMS, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS, Keys.ABSTRACT, Keys.NUMBER_OF_REFERENCES, Keys.NO_SO, Keys.COPYRIGHT,
        Keys.COPYRIGHT_TEXT, Keys.CLASS_CODES, Keys.DOC_TYPE, Keys.DOI };
    public static final Key[] DETAILED_KEYS = { Keys.ACCESSION_NUMBER, Keys.TITLE, Keys.TITLE_TRANSLATION, Keys.AUTHORS, Keys.EDITORS, Keys.AUTHOR_AFFS,
        Keys.EDITOR_AFFS, Keys.SERIAL_TITLE, Keys.ABBRV_SERIAL_TITLE, Keys.VOLUME, Keys.ISSUE, Keys.MONOGRAPH_TITLE, Keys.ISSUE_DATE, Keys.PUBLICATION_YEAR,
        Keys.PAPER_NUMBER, Keys.PAGE_RANGE, Keys.LANGUAGE, Keys.ISSN, Keys.CODEN, Keys.ISBN, Keys.DOC_TYPE, Keys.CONFERENCE_NAME, Keys.CONF_DATE,
        Keys.MEETING_LOCATION, Keys.CONF_CODE, Keys.SPONSOR, Keys.PUBLISHER, Keys.ABSTRACT, Keys.ABSTRACT_TYPE, Keys.NUMBER_OF_REFERENCES, Keys.MAIN_HEADING,
        Keys.CONTROLLED_TERMS, Keys.UNCONTROLLED_TERMS, Keys.CLASS_CODES, Keys.TREATMENTS, Keys.GLOBAL_TAGS, Keys.PRIVATE_TAGS, Keys.DOI, Keys.DOCID,
        Keys.COPYRIGHT, Keys.COPYRIGHT_TEXT, Keys.PROVIDER };
    private static final Key[] RIS_KEYS = { Keys.RIS_TY, Keys.RIS_LA, Keys.RIS_N1, Keys.RIS_TI, Keys.RIS_T1, Keys.RIS_BT, Keys.RIS_JO, Keys.RIS_T3,
        Keys.RIS_AUS, Keys.RIS_AD, Keys.RIS_EDS, Keys.RIS_VL, Keys.RIS_IS, Keys.RIS_PY, Keys.RIS_AN, Keys.RIS_SP, Keys.RIS_EP, Keys.RIS_SN, Keys.RIS_S1,
        Keys.RIS_MD, Keys.RIS_CY, Keys.RIS_PB, Keys.RIS_N2, Keys.RIS_KW, Keys.RIS_CVS, Keys.RIS_FLS, Keys.RIS_DO };
    private static final Key[] XML_KEYS = { Keys.ISSN, Keys.MAIN_HEADING, Keys.NO_SO, Keys.MONOGRAPH_TITLE, Keys.PUBLICATION_YEAR, Keys.VOLUME_TITLE,
        Keys.CONTROLLED_TERM, Keys.ISBN, Keys.AUTHORS, Keys.DOCID, Keys.SOURCE, Keys.NUMVOL, Keys.EDITOR_AFFS, Keys.EDITORS, Keys.PUBLISHER, Keys.VOLUME,
        Keys.AUTHOR_AFFS, Keys.PROVIDER, Keys.ISSUE_DATE, Keys.COPYRIGHT_TEXT, Keys.DOI, Keys.PAGE_COUNT, Keys.PUBLICATION_DATE, Keys.TITLE, Keys.LANGUAGE,
        Keys.PAGE_RANGE, Keys.PAPER_NUMBER, Keys.COPYRIGHT, Keys.ISSUE, Keys.ACCESSION_NUMBER, Keys.CONTROLLED_TERMS };

    static {  // ISSNs with AR field problem
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

    private static String queryCitation = "select M_ID,DT,TI,TT,AUS,AF,AM,AC,ASS,AV,AY,CF,ED,EF,EM,EC,ES,EV,EY,ST,SE,VO,ISS,SD,MT,VT,PN,YR,NV,PA,XP,AR,PP,LA,ME,SN,DO,YR,CN,ST,SE,BN,EX,LOAD_NUMBER from cpx_master where M_ID IN ";
    private static String queryXMLCitation = "select  M_ID,DT,TI,TT,AUS,AF,AM,AC,ASS,AV,AY,ED,EF,EM,EC,ES,EV,EY,ST,SE,VO,ISS,SD,MT,VT,PN,YR,NV,PA,XP,AR,PP,LA,ME,SN,DO,BN,LOAD_NUMBER,EX,CVS from cpx_master where M_ID IN ";
    // private static String
    // queryAbstracts="select M_ID,DT,TI,TT,AUS,AF,AM,AC,ASS,AV,AY,ED,EF,EM,EC,ES,EV,EY,ST,SE,VO,ISS,SD,MT,ME,VT,PN,YR,NV,PA,XP,AR,PP,LA,SN,CN,BN,CF,M2,MC,MS,MV,MY,SP,SC,SS,SV,SY,PN,PC,PY,PO,AB,NR,AT,MH,SH,CVS from cpx_master where M_ID IN ";
    private static String queryAbstracts = "select M_ID,EX,DT,TI,TT,AUS,AF,AM,AC,ASS,AV,AY,ED,EF,EM,EC,ES,EV,EY,ST,SE,VO,ISS,SD,MT,ME,VT,PN,YR,NV,PA,XP,AR,PP,LA,SN,CN,BN,CF,M2,MC,MS,MV,MY,SP,SC,SS,SV,SY,PN,PC,PY,PO,AB,NR,AT,MH,SH,CVS,FLS,PS,PV,CLS,LOAD_NUMBER, DO from cpx_master where   M_ID IN ";

    // jam 12/30/2002
    // New Index - field change from AN to EX
    private static String queryDetailed = "select M_ID,EX,TI,TT,AUS,AF,AM,AC,ASS,AV,AY,ED,EF,EM,EC,ES,EV,EY,ST,SE,VO,ISS,SD,MT,ME,VT,PN,YR,NV,PA,XP,AR,PP,LA,SN,CN,BN,CF,M2,MC,MS,MV,MY,SP,SC,SS,SY,PC,PY,PO,AB,NR,AT,MH,SH,CVS,DT,CC,SV,FLS,CAL,CLS,TR,PS,PV,LOAD_NUMBER, DO from cpx_master where M_ID IN ";

    private static String querypreview = "select M_ID, AB from cpx_master where M_ID IN ";

    public DocumentBuilder newInstance(Database database) {
        return new CPXDocBuilder(database);
    }

    public CPXDocBuilder() {
    }

    public CPXDocBuilder(Database database) {
        this.database = database;
    }

    /**
     * This method takes a list of DocID objects and dataFormat and returns a List of EIDoc Objects based on a particular dataformat @ param listOfDocIDs @
     * param dataFormat @ return List --list of EIDoc's @ exception DocumentBuilderException
     */
    public List<EIDoc> buildPage(List<DocID> listOfDocIDs, String dataFormat) throws DocumentBuilderException {
        List<EIDoc> l = null;
        try {
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
            } else if (Citation.CITATION_PREVIEW.equalsIgnoreCase(dataFormat)) {
                l = loadPreviewData(listOfDocIDs);
            }

        } catch (Exception e) {
            throw new DocumentBuilderException(e);
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
            rset = stmt.executeQuery(querypreview + INString);
            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));
                EIDoc eiDoc = new EIDoc(did, ht, Citation.CITATION_PREVIEW);

                ht.put(Keys.DOCID, (DocID) oidTable.get(rset.getString("M_ID")));
                ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, StringUtil.getStringFromClob(rset.getClob("AB"))));
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

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, CPX_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, CPX_TEXT_COPYRIGHT));

                if (rset.getString("EX") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("EX")));
                }

                if (rset.getString("ST") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("ST")));
                }

                if (rset.getString("SE") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("SE")));
                }

                if (rset.getString("YR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("YR"), perl));
                }

                String strPages = getPage(rset.getString("XP"), rset.getString("PP"), rset.getString("AR"), rset.getString("SN"), rset.getString("DO"),
                    rset.getString("YR"));

                if (strPages != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));
                }

                if (rset.getString("PP") != null) {
                    String pageCount = rset.getString("PP").replaceAll("p", " ");
                    ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, pageCount));
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

                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));

                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUS"), Keys.AUTHORS));

                    if (rset.getString("AF") != null) {
                        Affiliation affil = new Affiliation(Keys.AUTHOR_AFFS, rset.getString("AF"));
                        authors.setFirstAffiliation(affil);
                        ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, affil));
                    }

                    ht.put(Keys.AUTHORS, authors);

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

                if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null
                    || rset.getString("PN") != null) {
                    String strVolIss = StringUtil.EMPTY_STRING;
                    if (rset.getString("VO") != null) {
                        strVolIss = strVolIss.concat(replaceVolumeNullWithEmptyString(rset.getString("VO")));
                        ht.put(Keys.VOLUME, new Volume(rset.getString("VO"), perl));
                    }

                    if (rset.getString("ISS") != null) {
                        ht.put(Keys.ISSUE, new Issue(rset.getString("ISS"), perl));

                        if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                            strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(rset.getString("ISS")));
                        } else {
                            strVolIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                        }
                    }

                    if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                    }

                    if (rset.getString("ST") != null || rset.getString("SE") != null) {

                        if (rset.getString("ST") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("ST")));
                        } else if (rset.getString("SE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("SE")));
                        }

                        if (rset.getString("MT") != null) {
                            ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, rset.getString("MT")));
                        }

                        if (rset.getString("VT") != null) {
                            ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VT")));
                        }
                    } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") != null || rset.getString("ME") != null)) {

                        if (rset.getString("MT") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("MT")));
                        } else if (rset.getString("ME") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("ME")));
                        }
                    } else if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null) {
                        if (rset.getString("PN") != null) {
                            ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PN")));
                        }
                    } else {

                    }
                }

                if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null
                    && rset.getString("PN") == null) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                if (rset.getString("PN") != null) {
                    ht.put(Keys.I_PUBLISHER, new XMLWrapper(Keys.I_PUBLISHER, rset.getString("PN")));
                }

                String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));

                if ((rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null || rset
                    .getString("PN") != null) && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                    if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        strSD = perl.substitute("s/".concat(strYR).concat("$//"), strSD);
                        strSD = strSD.trim().concat(", ").concat(strYR);
                    }
                    ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));

                } else if (rset.getString("YR") != null) {
                    String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                    if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("PN") == null
                        && rset.getString("ME") == null) {
                        // if ALL 4 are null, use the label 'Publication Date'
                        ht.put(Keys.PUBLICATION_DATE, new Year(rset.getString("YR"), perl));
                    } else {
                        // else just store the date
                        ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("YR"), perl));
                    }
                }

                if (rset.getString("VT") != null) {
                    ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VT")));
                }

                if ((rset.getString("LA") != null) && (!rset.getString("LA").equalsIgnoreCase("ENGLISH"))) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, rset.getString("LA")));

                }

                if (rset.getString("CF") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("CF")));
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
                    } else {
                        if (rset.getString("MV") != null) {
                            lstCF.add(rset.getString("MV"));
                        }
                    }
                    if (rset.getString("MY") != null) {
                        lstCF.add(rset.getString("MY"));
                    }

                    ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, StringUtil.join(lstCF, ", ")));
                    lstCF = null;

                }

                // SP - SPONSOR'S INFO
                if (rset.getString("SP") != null) {

                    List<String> lstSponsor = new ArrayList<String>();
                    lstSponsor.add(rset.getString("SP"));

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
                    ht.put(Keys.SPONSOR, new XMLWrapper(Keys.SPONSOR, StringUtil.join(lstSponsor, ", ")));
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
                    ht.put(Keys.PUB_LOCATION, new XMLWrapper(Keys.PUB_LOCATION, StringUtil.join(lstPL, ", ")));
                }

                String mh = rset.getString("MH");
                String sh = rset.getString("SH");
                String mainHeading = null;
                if (mh != null && sh != null) {
                    mainHeading = mh + " -- " + sh;
                } else if (mh != null && sh == null) {
                    mainHeading = mh;
                } else if (mh == null && sh != null) {
                    mainHeading = sh;
                }

                // FLS - added in Baja to ABS view
                if (rset.getString("FLS") != null) {
                    ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(rset.getString("FLS"))));
                }

                if ((rset.getString("CVS") != null) || (rset.getString("MH") != null)) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper2(CPX_CONTROLLED_TERMS, setCVS(mainHeading, rset.getString("CVS"))));
                }

                // NR
                if (rset.getString("NR") != null) {
                    String strREFs = rset.getString("NR");
                    if (perl.match("/(\\d+)/", strREFs)) {
                        ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, perl.group(0).toString()));
                    }
                }

                // CN
                if (rset.getString("CN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, rset.getString("CN")));
                }
                // BN
                // /isbn
                if (rset.getString("BN") != null) {
                    ht.put(Keys.ISBN, new ISBN(rset.getString("BN")));
                }
                // SN
                if (rset.getString("SN") != null) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("SN")));
                }

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                // CLS
                if (rset.getString("CLS") != null) {
                    ht.put(Keys.CLASS_CODES, new Classifications(CPX_CLASS_CODES, setElementData(rset.getString("CLS")), this.database));
                }

                // AT
                if (rset.getString("AT") != null) {
                    ht.put(Keys.ABSTRACT_TYPE, new XMLWrapper(Keys.ABSTRACT_TYPE, rset.getString("AT")));
                }

                // DO
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

                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, CPX_HTML_COPYRIGHT));

                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, CPX_TEXT_COPYRIGHT));

                if (rset.getString("EX") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("EX")));
                }

                if (rset.getString("TI") != null) {
                    ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, rset.getString("TI")));
                }

                if (rset.getString("TT") != null) {
                    ht.put(Keys.TITLE_TRANSLATION, new XMLWrapper(Keys.TITLE_TRANSLATION, rset.getString("TT")));
                }

                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.AUTHORS, getContributors(rset.getString("AUS"), Keys.AUTHORS));

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
                            ht.put(Keys.EDITOR_AFFS, new Affiliations(Keys.EDITOR_AFFS, eaffil));
                        }
                    }
                }

                if ((rset.getString("ST") != null || rset.getString("SE") != null)) {

                    if (rset.getString("ST") != null) {
                        ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("ST")));
                    }

                    if (rset.getString("SE") != null) {
                        ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("SE")));
                    }

                    if (rset.getString("VO") != null) {
                        String strVol = replaceVolumeNullWithEmptyString(rset.getString("VO"));
                        if (strVol != null) {
                            ht.put(Keys.VOLUME, new Volume(strVol, perl));
                        }
                    }

                    if (rset.getString("ISS") != null) {
                        String strIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                        ht.put(Keys.ISSUE, new Issue(strIss, perl));
                    }

                    // SD
                    if (rset.getString("SD") != null) {
                        ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, rset.getString("SD")));
                    }

                    // MT
                    if (rset.getString("MT") != null) {
                        ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, rset.getString("MT")));
                    }
                    // ME
                    if (rset.getString("ME") != null) {
                        ht.put(Keys.ABBRV_MON_MONOGRAPH_TITLE, new XMLWrapper(Keys.ABBRV_MON_MONOGRAPH_TITLE, rset.getString("ME")));
                    }
                    // VT
                    if (rset.getString("VT") != null) {
                        ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VT")));
                    }
                } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") != null || rset.getString("ME") != null)) {
                    // MT
                    if (rset.getString("MT") != null) {
                        ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, rset.getString("MT")));
                    }
                    // ME
                    if (rset.getString("ME") != null) {
                        ht.put(Keys.ABBRV_MON_MONOGRAPH_TITLE, new XMLWrapper(Keys.ABBRV_MON_MONOGRAPH_TITLE, rset.getString("ME")));
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
                        ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VT")));
                    }
                }

                if (rset.getString("YR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("YR"), perl));
                }

                // NV
                if (rset.getString("NV") != null) {
                    ht.put(Keys.NUMVOL, new XMLWrapper(Keys.NUMVOL, rset.getString("NV")));
                }

                // PA
                if (rset.getString("PA") != null) {
                    // PAPER_NUMBER will cause Paper Number label in XSL
                    ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, rset.getString("PA")));
                }

                // PP
                String strPages = getPage(rset.getString("XP"), rset.getString("PP"), rset.getString("AR"), rset.getString("SN"), rset.getString("DO"),
                    rset.getString("YR"));
                if (strPages != null) {
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));
                }

                // Strip out and store the start page and end page

                // FIX BUG BELLOW -- NULL POINTER ON strPages

                if (strPages != null) {
                    if (perl.match("/(\\d+)/", strPages)) {
                        ht.put(Keys.START_PAGE, new XMLWrapper(Keys.START_PAGE, perl.group(0).toString()));
                        if (perl.match("/(\\d+)/", perl.postMatch())) {
                            ht.put(Keys.END_PAGE, new XMLWrapper(Keys.END_PAGE, perl.group(0).toString()));
                        }
                    }
                }

                // LA
                if (rset.getString("LA") != null) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, rset.getString("LA")));
                }
                // SN
                if (rset.getString("SN") != null) {
                    ht.put(Keys.ISSN, new ISSN(rset.getString("SN")));
                }
                // CN
                if (rset.getString("CN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, rset.getString("CN")));
                }
                // BN
                if (rset.getString("BN") != null) {
                    ht.put(Keys.ISBN, new ISBN(rset.getString("BN")));
                }

                // DT
                if (rset.getString("DT") != null) {
                    ht.put(Keys.DOC_TYPE, new XMLWrapper(Keys.DOC_TYPE, replaceDTNullWithEmptyString(rset.getString("DT"))));
                }

                // CF
                if (rset.getString("CF") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("CF")));
                }
                // MD
                if (rset.getString("M2") != null) {
                    ht.put(Keys.CONF_DATE, new XMLWrapper(Keys.CONF_DATE, rset.getString("M2")));
                }
                // ML
                List<String> lstMeetLoc = new ArrayList<String>();
                if (rset.getString("MC") != null) {
                    lstMeetLoc.add(rset.getString("MC"));
                }
                if (rset.getString("MS") != null)

                {
                    lstMeetLoc.add(rset.getString("MS"));
                }
                if (rset.getString("MV") != null) {
                    lstMeetLoc.add(rset.getString("MV"));
                }
                if (rset.getString("MY") != null) {
                    lstMeetLoc.add(rset.getString("MY"));
                }
                if (lstMeetLoc.size() > 0) {
                    ht.put(Keys.MEETING_LOCATION, new XMLWrapper(Keys.MEETING_LOCATION, StringUtil.join(lstMeetLoc, ", ")));
                }
                lstMeetLoc = null;
                // CC
                if (rset.getString("CC") != null) {
                    ht.put(Keys.CONF_CODE, new XMLWrapper(Keys.CONF_CODE, rset.getString("CC")));
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
                    ht.put(Keys.SPONSOR, new XMLWrapper(Keys.SPONSOR, StringUtil.join(lstSponsor, ", ")));
                }

                lstSponsor = null;

                // PN, PL
                List<String> lstTokens = new ArrayList<String>();
                if (rset.getString("PN") != null) {
                    lstTokens.add((String) rset.getString("PN"));
                }
                if (rset.getString("PC") != null) {
                    lstTokens.add((String) rset.getString("PC"));
                }
                if (rset.getString("PS") != null) {
                    lstTokens.add((String) rset.getString("PS"));
                }
                if (rset.getString("PV") != null) {
                    lstTokens.add((String) rset.getString("PV"));
                }
                if (rset.getString("PY") != null) {
                    lstTokens.add((String) rset.getString("PY"));
                }

                if (lstTokens.size() > 0) {
                    ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.join(lstTokens, ", ")));
                }
                lstTokens = null;

                // PO
                if (rset.getString("PO") != null) {
                    ht.put(Keys.PUBLICATION_ORDER, new XMLWrapper(Keys.PUBLICATION_ORDER, rset.getString("PO")));
                }
                // AB

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.ABSTRACT, new XMLWrapper(Keys.ABSTRACT, abs));
                }

                // AT
                if (rset.getString("AT") != null) {
                    ht.put(Keys.ABSTRACT_TYPE, new XMLWrapper(Keys.ABSTRACT_TYPE, rset.getString("AT")));
                }

                // NR
                if (rset.getString("NR") != null) {
                    String strREFs = rset.getString("NR");
                    if (perl.match("/(\\d+)/", strREFs)) {
                        ht.put(Keys.NUMBER_OF_REFERENCES, new XMLWrapper(Keys.NUMBER_OF_REFERENCES, perl.group(0).toString()));
                    }
                }

                // MH
                String mh = rset.getString("MH");
                String sh = rset.getString("SH");
                String mainHeading = null;
                if (mh != null && sh != null) {
                    mainHeading = mh.concat("   -- ").concat(sh);
                } else if (mh != null && sh == null) {
                    mainHeading = mh;
                } else if (mh == null && sh != null) {
                    mainHeading = sh;
                }
                if (rset.getString("MH") != null) {
                    ht.put(Keys.MAIN_HEADING, new XMLWrapper(CPX_MAIN_HEADING, mainHeading));

                }

                // CVS
                if ((rset.getString("CVS") != null)) {
                    ht.put(Keys.CONTROLLED_TERMS, new XMLMultiWrapper(CPX_CONTROLLED_TERMS, setElementData(rset.getString("CVS"))));
                }

                // FLS
                if (rset.getString("FLS") != null) {
                    ht.put(Keys.UNCONTROLLED_TERMS, new XMLMultiWrapper(Keys.UNCONTROLLED_TERMS, setElementData(rset.getString("FLS"))));
                }

                // CLS
                if (rset.getString("CLS") != null) {
                    ht.put(Keys.CLASS_CODES, new Classifications(CPX_CLASS_CODES, setElementData(rset.getString("CLS")), this.database));
                }

                // TR
                if (rset.getString("TR") != null) {
                    ht.put(Keys.TREATMENTS, new Treatments(setTreatments(rset.getString("TR")), this.database));
                }

                // DO
                if (rset.getString("DO") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DO")));
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

    // ???? BT - RIS_MT and in RIS_BT

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
        // N2 BT
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);
            stmt = con.createStatement();
            rset = stmt.executeQuery(queryDetailed + INString);

            while (rset.next()) {
                ElementDataMap ht = new ElementDataMap();
                DocID did = (DocID) oidTable.get(rset.getString("M_ID"));

                // ht.put(EIDoc.DOCID, did.getDocID());
                // ht.put(EIDoc.HANDLE, Integer.toString(did.getHitIndex()));
                // ht.put(EIDoc.DATABASE_ID, did.getDatabase().getID());
                // ht.put(EIDoc.DATABASE,"Compendex");
                // ht.put(EIDoc.PROVIDER,this.PROVIDER);
                String strDocType = StringUtil.replaceNullWithEmptyString(rset.getString("DT"));
                String risDocType = replaceTYwithRIScode(strDocType);

                ht.put(Keys.RIS_TY, new XMLWrapper(Keys.RIS_TY, risDocType));

                if (rset.getString("LA") != null) {
                    ht.put(Keys.RIS_LA, new XMLWrapper(Keys.RIS_LA, rset.getString("LA")));
                }

                ht.put(Keys.RIS_N1, new XMLWrapper(Keys.RIS_N1, CPXDocBuilder.CPX_TEXT_COPYRIGHT));

                if (rset.getString("TI") != null) {
                    ht.put(Keys.RIS_TI, new XMLWrapper(Keys.RIS_TI, rset.getString("TI")));
                }
                if (rset.getString("TT") != null) {
                    ht.put(Keys.RIS_T1, new XMLWrapper(Keys.RIS_T1, rset.getString("TT")));
                }

                if (rset.getString("MT") != null) {
                    ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, rset.getString("MT")));
                }

                if (risDocType.equalsIgnoreCase("JOUR")) {
                    if (rset.getString("ST") != null) {
                        ht.put(Keys.RIS_JO, new XMLWrapper(Keys.RIS_JO, rset.getString("ST")));
                    }
                } else {
                    if (rset.getString("ST") != null) {
                        ht.put(Keys.RIS_T3, new XMLWrapper(Keys.RIS_T3, rset.getString("ST")));
                    }
                }

                if (rset.getString("AUS") != null) {
                    Contributors authors = new Contributors(Keys.RIS_AUS, getContributors(rset.getString("AUS"), Keys.RIS_AUS));

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

                        Affiliation aff = new Affiliation(Keys.RIS_AD, StringUtil.join(lstAFF, ", "));
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
                            Affiliation eff = new Affiliation(Keys.RIS_AD, rset.getString("EF"));
                            ht.put(Keys.RIS_AD, new Affiliations(Keys.RIS_AD, eff));
                        }
                    }
                }

                if (rset.getString("VO") != null) {
                    String strVol = replaceVolumeNullWithEmptyString(rset.getString("VO"));
                    strVol = perl.substitute("s/[vol\\.]//gi", strVol);
                    ht.put(Keys.RIS_VL, new XMLWrapper(Keys.RIS_VL, strVol));
                }

                if (rset.getString("ISS") != null) {
                    String strIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                    strIss = perl.substitute("s/[no\\.]//gi", strIss);

                    ht.put(Keys.RIS_IS, new Issue(strIss, perl));
                }

                if (rset.getString("YR") != null) {
                    ht.put(Keys.RIS_PY, new Year(Keys.RIS_PY, rset.getString("YR"), perl));
                }

                if (rset.getString("EX") != null) {
                    ht.put(Keys.RIS_AN, new XMLWrapper(Keys.RIS_AN, rset.getString("EX")));
                }

                // PP
                String strPages = getPage(rset.getString("XP"), rset.getString("PP"), rset.getString("AR"), rset.getString("SN"), rset.getString("DO"),
                    rset.getString("YR"));
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
                    ht.put(Keys.RIS_BT, new XMLWrapper(Keys.RIS_BT, rset.getString("CF")));
                }
                // MD
                if (rset.getString("M2") != null) {
                    ht.put(Keys.RIS_MD, new XMLWrapper(Keys.RIS_MD, rset.getString("M2")));
                }
                // ML

                /*
                 * JM 10/22/2008 Stop putting Conference Location in CY field. CY should be City of publication. Code was left since we may find another RIS
                 * field to put it in
                 */
                /*
                 * List lstMeetLoc = new ArrayList(); if (rset.getString("MC") !=null) { lstMeetLoc.add(rset.getString("MC")); } if (rset.getString("MS") !=
                 * null) { lstMeetLoc.add(rset.getString("MS")); } if (rset.getString("MV") != null) { lstMeetLoc.add(rset.getString("MV")); } if
                 * (rset.getString("MY") != null) { lstMeetLoc.add(rset.getString("MY")); } if (lstMeetLoc.size() > 0) { ht.put(Keys.RIS_CY, new
                 * XMLWrapper(Keys.RIS_CY, StringUtil.replaceNullWithEmptyString(StringUtil.join(lstMeetLoc,", ")))); }
                 */
                /*
                 * JM 10/23/2008 Separated Publisher from location/address to put publisher location in RIS CY field. See ANUM='083911581212'
                 */
                // PN
                if (rset.getString("PN") != null) {
                    ht.put(Keys.RIS_PB, new XMLWrapper(Keys.RIS_PB, rset.getString("PN")));
                }
                // PL
                List<String> lstTokens = new ArrayList<String>();
                if (rset.getString("PC") != null) {
                    lstTokens.add((String) rset.getString("PC"));
                }
                if (rset.getString("PS") != null) {
                    lstTokens.add((String) rset.getString("PS"));
                }
                if (rset.getString("PV") != null) {
                    lstTokens.add((String) rset.getString("PV"));
                }
                if (rset.getString("PY") != null) {
                    lstTokens.add((String) rset.getString("PY"));
                }
                if (lstTokens.size() > 0) {
                    ht.put(Keys.RIS_CY, new XMLWrapper(Keys.RIS_CY, StringUtil.join(lstTokens, ", ")));
                }
                lstTokens = null;

                String abs = null;
                if ((abs = hasAbstract(rset)) != null) {
                    ht.put(Keys.RIS_N2, new XMLWrapper(Keys.RIS_N2, abs));
                }

                // MH
                String mh = rset.getString("MH");
                String sh = rset.getString("SH");
                String mainHeading = null;
                if (mh != null && sh != null) {
                    mainHeading = mh.concat("   -- ").concat(sh);
                } else if (mh != null && sh == null) {
                    mainHeading = mh;
                } else if (mh == null && sh != null) {
                    mainHeading = sh;
                }

                if (mainHeading != null) {
                    ht.put(Keys.RIS_KW, new XMLWrapper(Keys.RIS_KW, mainHeading));
                }

                // CVS
                ht.put(Keys.RIS_CVS, new XMLMultiWrapper(Keys.RIS_CVS, setElementData(rset.getString("CVS"))));
                // FLS

                ht.put(Keys.RIS_FLS, new XMLMultiWrapper(Keys.RIS_FLS, setElementData(rset.getString("FLS"))));

                // DO
                if (rset.getString("DO") != null) {
                    ht.put(Keys.RIS_DO, new XMLWrapper(Keys.RIS_DO, rset.getString("DO")));
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
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, CPX_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, CPX_TEXT_COPYRIGHT));

                String strPages = getPage(rset.getString("XP"), rset.getString("PP"), rset.getString("AR"), rset.getString("SN"), rset.getString("DO"),
                    rset.getString("YR"));

                ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));

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

                ht.put(Keys.TITLE, new XMLWrapper(Keys.TITLE, strTitle));

                // AUS or EDS
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

                        Contributors editors = new Contributors(Keys.EDITORS, getContributors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);

                        if (rset.getString("EF") != null) {
                            Affiliation eaffil = new Affiliation(Keys.EDITOR_AFFS, rset.getString("EF"));
                            editors.setFirstAffiliation(eaffil);
                            ht.put(Keys.AUTHOR_AFFS, new Affiliations(Keys.AUTHOR_AFFS, eaffil));
                        }
                    }
                }

                // SO
                if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null
                    || rset.getString("PN") != null) {
                    String strVolIss = StringUtil.EMPTY_STRING;

                    // VO - VOL and ISSUE Combined by ', '
                    // add 'v' or 'n'
                    if (rset.getString("VO") != null) {
                        strVolIss = strVolIss.concat(replaceVolumeNullWithEmptyString(rset.getString("VO")));
                        ht.put(Keys.VOLUME, new Volume(Keys.VOLUME, rset.getString("VO"), perl));
                    }

                    if (rset.getString("ISS") != null) {

                        ht.put(Keys.ISSUE, new Issue(rset.getString("ISS"), perl));

                        if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                            strVolIss = strVolIss.concat(", ").concat(replaceIssueNullWithEmptyString(rset.getString("ISS")));
                        } else {
                            strVolIss = replaceIssueNullWithEmptyString(rset.getString("ISS"));
                        }
                    }

                    if (strVolIss != null && !strVolIss.equals(StringUtil.EMPTY_STRING)) {
                        ht.put(Keys.VOLISSUE, new XMLWrapper(Keys.VOLISSUE, strVolIss));
                    }

                    if (rset.getString("ST") != null || rset.getString("SE") != null) {
                        if (rset.getString("ST") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("ST")));
                        } else if (rset.getString("SE") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("SE")));
                        }

                        // an MT or VT can accompany the ST (or SE)
                        if (rset.getString("MT") != null) {
                            ht.put(Keys.MONOGRAPH_TITLE, new XMLWrapper(Keys.MONOGRAPH_TITLE, rset.getString("MT")));
                        }

                        if (rset.getString("VT") != null) {
                            ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VT")));
                        }
                    } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") != null || rset.getString("ME") != null)) {

                        if (rset.getString("MT") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("MT")));
                        } else if (rset.getString("ME") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("ME")));
                        }
                    } else if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null) {
                        if (rset.getString("PN") != null) {
                            ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, rset.getString("PN")));
                        }
                    } else {

                    }
                }

                if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null
                    && rset.getString("PN") == null) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                // SD
                String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));
                if ((rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null || rset
                    .getString("PN") != null) && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                    if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        strSD = perl.substitute("s/".concat(strYR).concat("$//"), strSD);
                        strSD = strSD.trim().concat(", ").concat(strYR);
                    }

                    ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));

                } else if (rset.getString("YR") != null) {
                    String strYR = rset.getString("YR");
                    if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("PN") == null
                        && rset.getString("ME") == null) {
                        // if ALL 4 are null, use the label 'Publication Date'
                        ht.put(Keys.PUBLICATION_DATE, new Year(rset.getString("YR"), perl));
                    } else {
                        // else just store the date
                        ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("YR"), perl));
                    }
                }
                // NV
                if (rset.getString("NV") != null) {
                    ht.put(Keys.NUMVOL, new XMLWrapper(Keys.NUMVOL, rset.getString("NV")));
                }

                // VT
                if (rset.getString("VT") != null) {
                    ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, rset.getString("VT")));
                }

                // LA
                if ((rset.getString("LA") != null) && !rset.getString("LA").equalsIgnoreCase("ENGLISH")) {
                    ht.put(Keys.LANGUAGE, new XMLWrapper(Keys.LANGUAGE, rset.getString("LA")));
                }

                // DO
                if (rset.getString("DO") != null) {
                    ht.put(Keys.DOI, new XMLWrapper(Keys.DOI, rset.getString("DO")));
                }

                // YR

                if (rset.getString("YR") != null) {
                    ht.put(Keys.PUBLICATION_YEAR, new Year(rset.getString("YR"), perl));
                }
                // CN
                if (rset.getString("CN") != null) {
                    ht.put(Keys.CODEN, new XMLWrapper(Keys.CODEN, rset.getString("CN")));
                }
                // ST

                if (rset.getString("ST") != null) {
                    ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, rset.getString("ST")));
                }
                // SE

                if (rset.getString("SE") != null) {
                    ht.put(Keys.ABBRV_SERIAL_TITLE, new XMLWrapper(Keys.ABBRV_SERIAL_TITLE, rset.getString("SE")));
                }

                // ST
                if (rset.getString("ST") != null) {
                    ht.put(Keys.SERIAL_TITLE, new XMLWrapper(Keys.SERIAL_TITLE, rset.getString("ST")));
                }
                // BN
                if (rset.getString("BN") != null) {
                    ht.put(Keys.ISBN, new ISBN(rset.getString("BN")));
                }

                // CF
                if (rset.getString("CF") != null) {
                    ht.put(Keys.CONFERENCE_NAME, new XMLWrapper(Keys.CONFERENCE_NAME, rset.getString("CF")));
                }

                // EX
                if (rset.getString("EX") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, rset.getString("EX")));
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
                ht.put(Keys.COPYRIGHT, new XMLWrapper(Keys.COPYRIGHT, CPXDocBuilder.CPX_HTML_COPYRIGHT));
                ht.put(Keys.COPYRIGHT_TEXT, new XMLWrapper(Keys.COPYRIGHT_TEXT, CPXDocBuilder.CPX_TEXT_COPYRIGHT));

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
                if (rset.getString("EX") != null) {
                    ht.put(Keys.ACCESSION_NUMBER, new XMLWrapper(Keys.ACCESSION_NUMBER, StringUtil.replaceNullWithEmptyString(rset.getString("EX"))));
                }

                // AUS or EDS

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
                        Contributors editors = new Contributors(Keys.EDITORS, getContributors(strED, Keys.EDITORS));
                        ht.put(Keys.EDITORS, editors);

                        if (rset.getString("EF") != null) {
                            Affiliation eaffil = new Affiliation(Keys.EDITOR_AFFS, StringUtil.replaceNullWithEmptyString(rset.getString("EF")));
                            ht.put(Keys.EDITOR_AFFS, new Affiliations(Keys.EDITOR_AFFS, eaffil));
                        }
                    }
                }
                // SO
                if (rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null
                    || rset.getString("PN") != null) {

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
                        if (rset.getString("VT") != null) {
                            ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("VT"))));
                        }
                    } else if ((rset.getString("ST") == null && rset.getString("SE") == null) && (rset.getString("MT") != null || rset.getString("ME") != null)) {
                        if (rset.getString("MT") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("MT"))));
                        } else if (rset.getString("ME") != null) {
                            ht.put(Keys.SOURCE, new XMLWrapper(Keys.SOURCE, StringUtil.replaceNullWithEmptyString(rset.getString("ME"))));
                        }
                    } else if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null) {
                        if (rset.getString("PN") != null) {
                            ht.put(Keys.PUBLISHER, new XMLWrapper(Keys.PUBLISHER, StringUtil.replaceNullWithEmptyString(rset.getString("PN"))));
                        }
                    } else {
                    }
                }
                if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("ME") == null
                    && rset.getString("PN") == null) {
                    ht.put(Keys.NO_SO, new XMLWrapper(Keys.NO_SO, "NO_SO"));
                }

                // RN - INSPEC ONLY

                // SD
                String strSD = StringUtil.replaceNullWithEmptyString(rset.getString("SD"));
                if ((rset.getString("ST") != null || rset.getString("SE") != null || rset.getString("MT") != null || rset.getString("ME") != null || rset
                    .getString("PN") != null) && (!strSD.equals(StringUtil.EMPTY_STRING))) {

                    if (rset.getString("YR") != null) {
                        String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                        strSD = perl.substitute("s/".concat(strYR).concat("$//"), strSD);
                        strSD = strSD.trim().concat(", ").concat(strYR);
                    }
                    ht.put(Keys.ISSUE_DATE, new XMLWrapper(Keys.ISSUE_DATE, strSD));

                } else if (rset.getString("YR") != null) {
                    String strYR = StringUtil.replaceNullWithEmptyString(rset.getString("YR"));
                    if (rset.getString("ST") == null && rset.getString("SE") == null && rset.getString("MT") == null && rset.getString("PN") == null
                        && rset.getString("ME") == null) {
                        // if ALL 4 are null, use the label 'Publication Date'
                        ht.put(Keys.PUBLICATION_DATE, new Year(strYR, perl));
                    } else {
                        // else just store the date

                        ht.put(Keys.PUBLICATION_YEAR, new Year(strYR, perl));
                    }
                }
                // NV
                if (rset.getString("NV") != null) {
                    ht.put(Keys.NUMVOL, new XMLWrapper(Keys.NUMVOL, StringUtil.replaceNullWithEmptyString(rset.getString("NV"))));
                }
                // PA
                if (rset.getString("PA") != null) {
                    ht.put(Keys.PAPER_NUMBER, new XMLWrapper(Keys.PAPER_NUMBER, StringUtil.replaceNullWithEmptyString((rset.getString("PA")))));
                }

                String strPages = getPage(rset.getString("XP"), rset.getString("PP"), rset.getString("AR"), rset.getString("SN"), rset.getString("DO"),
                    rset.getString("YR"));

                if (strPages != null) {
                    strPages = strPages.replaceAll("p", " ");
                    ht.put(Keys.PAGE_RANGE, new PageRange(strPages, perl));
                }

                if (rset.getString("PP") != null) {
                    String pageCount = rset.getString("PP").replaceAll("p", " ");
                    ht.put(Keys.PAGE_COUNT, new XMLWrapper(Keys.PAGE_COUNT, pageCount));
                }

                // VT
                if (rset.getString("VT") != null) {
                    ht.put(Keys.VOLUME_TITLE, new XMLWrapper(Keys.VOLUME_TITLE, StringUtil.replaceNullWithEmptyString(rset.getString("VT"))));
                }
                // CVS
                if (rset.getString("CVS") != null) {
                    ht.put(Keys.CONTROLLED_TERMS,
                        new XMLMultiWrapper(CPX_CONTROLLED_TERMS, setElementData(StringUtil.replaceNullWithEmptyString(rset.getString("CVS")))));
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
                // STT - INSPEC ONLY

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
                // aStream.setDelimited30(';');
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

    private String getPage(String strPageRange, String strArticleNum, String strISSN, String strDOI, String strYear) {
        return getPage(strPageRange, null, strArticleNum, strISSN, strDOI, strYear);
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
            } else if (strPage.equals(StringUtil.EMPTY_STRING)) {
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

    // jam XML document mapping, conversion to TY values
    // for RIS format - only called from loadRIS
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
