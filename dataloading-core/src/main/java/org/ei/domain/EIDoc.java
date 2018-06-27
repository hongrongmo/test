package org.ei.domain;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.data.DocTypeToGenreConverter;
import org.ei.fulldoc.LinkInfo;
import org.ei.fulldoc.LinkingStrategy;
import org.ei.query.base.Highlightable;
import org.ei.util.StringUtil;

public class EIDoc implements Highlightable, XMLSerializable, LocalHoldingLinker, Keys {
    private final static Logger log4j = Logger.getLogger(EIDoc.class);

    private int loadNumber = -1;
    private Key[] outputKeys;
    private boolean labels = false;
    private boolean deep = false;
    private String format;
    private String view;

    public static final String eiRootDocumentTag = "EI-DOCUMENT";

    private ElementDataMap mapDocument;
    private DocID docID;

    /**
     * Build an EI Doc object
     *
     * @param docid
     * @return
     */
    public static EIDoc buildDoc(String docid) {
        List<?> eiDocs = null;
        EIDoc doc = null;
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        DocumentBuilder builder = new MultiDatabaseDocBuilder();

        if (!GenericValidator.isBlankOrNull(docid)) {
            List<DocID> docIds = new ArrayList<DocID>();
            DocID did = new DocID(docid, databaseConfig.getDatabase(docid.substring(0, 3)));
            docIds.add(did);
            try {
                eiDocs = builder.buildPage(docIds, Abstract.ABSTRACT_FORMAT);
                doc = (EIDoc) eiDocs.get(0);
            } catch (DocumentBuilderException e) {
                log4j.error("Unable to retrieve EIDoc with id: " + docid);
            }
        } else {
            log4j.error("Unable to retrieve EIDoc with null id");
        }

        return doc;
    }

    public String getView() {
        return this.view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public boolean getDeep() {
        return this.deep;
    }

    public void setDeep(boolean deep) {
        this.deep = deep;
    }

    public boolean isBook() {
        return false;
    }

    public ElementDataMap getElementDataMap() {
        return this.mapDocument;
    }

    public void toXML(Writer out) throws IOException {
        out.write("<");
        out.write(eiRootDocumentTag);
        out.write(" VIEW=\"");
        out.write(this.format);
        out.write("\">");
        out.write(buildFTTag());

        for (int i = 0; i < this.outputKeys.length; i++) {
            Key key = this.outputKeys[i];
            if (mapDocument.containsKey(key)) {
                ElementData xml = mapDocument.get(key);
                if (xml != null) {
                    xml.exportLabels(this.labels);
                    xml.toXML(out);
                }
            }

        }

        out.write("</");
        out.write(eiRootDocumentTag);
        out.write(">");
    }

    public EIDoc(DocID docID, ElementDataMap mapDocument, String format) {
        this.docID = docID;
        this.mapDocument = mapDocument;
        this.format = format;
    }

    public EIDoc(DocID docId) {
        this.docID = docId;
    }

    public String getFormat() {
        return this.format;
    }

    public DocID getDocID() {
        return this.docID;
    }

    public void setOutputKeys(Key[] outputKeys) {
        this.outputKeys = outputKeys;
    }

    public int getLoadNumber() {
        return this.loadNumber;
    }

    public void setLoadNumber(int loadNumber) {
        this.loadNumber = loadNumber;
    }

    public String getLongTerms() {
        String longTerms = null;
        if (mapDocument.containsKey(Keys.LINKED_TERMS)) {
            ElementData doi = (ElementData) mapDocument.get(Keys.LINKED_TERMS);
            String[] edata = doi.getElementData();
            longTerms = edata[0];
        }
        return longTerms;
    }

    public void exportLabels(boolean labels) {
        this.labels = labels;
    }

    public String getISSN2() {
        String value = null;
        if (mapDocument.containsKey(Keys.ISSN)) {
            ISSN issn = (ISSN) mapDocument.get(Keys.ISSN);
            value = issn.withoutDash();
        }

        return value;
    }

    public String getVolumeNo() {
        String value = null;
        if (mapDocument.containsKey(Keys.VOLUME)) {
            Volume volume = (Volume) mapDocument.get(Keys.VOLUME);
            value = volume.getVolumeNumber();
        }
        return value;

    }

    public String getVolume() {
        String value = null;
        if (mapDocument.containsKey(Keys.VOLUME)) {
            Volume volume = (Volume) mapDocument.get(Keys.VOLUME);
            value = volume.getVolume();
        }
        return value;

    }

    public String getIssueNo() {
        String value = null;
        if (mapDocument.containsKey(Keys.ISSUE)) {
            Issue issue = (Issue) mapDocument.get(Keys.ISSUE);
            value = issue.getIssueNumber();
        }

        return value;
    }

    public String getIssue() {
        String value = null;
        if (mapDocument.containsKey(Keys.ISSUE)) {
            Issue issue = (Issue) mapDocument.get(Keys.ISSUE);
            value = issue.getIssue();
        }

        return value;
    }

    public String getStartPage() {
        String value = null;
        if (mapDocument.containsKey(Keys.PAGE_RANGE)) {
            PageRange pageRange = (PageRange) mapDocument.get(Keys.PAGE_RANGE);
            value = pageRange.getStartPage();
        }

        return value;
    }

    public String getDOI() {
        String value = null;
        if (mapDocument.containsKey(Keys.DOI)) {
            ElementData doi = (ElementData) mapDocument.get(Keys.DOI);
            String[] edata = doi.getElementData();
            value = edata[0];
        }
        return value;
    }

    public String getDocType() {
        String value = null;
        if (mapDocument.containsKey(Keys.DOC_TYPE)) {
            ElementData dt = (ElementData) mapDocument.get(Keys.DOC_TYPE);
            String[] edata = dt.getElementData();
            value = edata[0];
        }
        return value;
    }

    public String getMapDataElement(Key keyname) {
        String value = null;
        if (mapDocument.containsKey(keyname)) {
            ElementData eldata = (ElementData) mapDocument.get(keyname);
            String[] edata = eldata.getElementData();
            if ((edata != null) && (edata.length >= 1)) {
                value = edata[0];
            }
        }
        return value;
    }

    public String getFT() {
        String ft = null;
        LinkingStrategy ls = this.docID.getDatabase().getLinkingStrategy();

        try {
            ft = ls.hasLink(this);
        } catch (Exception e) {
            ft = LinkingStrategy.HAS_LINK_NO;
            // e.printStackTrace();
        }

        return ft;
    }

    public String buildFTTag() {
        StringBuffer tempFT = new StringBuffer();
        tempFT.append("<FT ");
        tempFT.append(" FTLINK=").append("\"").append(getFT()).append("\"");
        tempFT.append("/>");
        return tempFT.toString();
    }

    public LinkInfo buildFT() throws Exception {
        LinkingStrategy ls = this.docID.getDatabase().getLinkingStrategy();
        LinkInfo linkInfo = ls.buildLink(this);
        return linkInfo;
    }

    public String getISSN() {

        if (mapDocument.containsKey(Keys.ISSN)) {
            ISSN issn = (ISSN) mapDocument.get(Keys.ISSN);
            return issn.getIssn();
        }

        return null;
    }

    public String getLocalHoldingLink(String URL) {
        StringUtil sUtil = new StringUtil();

        if (URL == null) {
            return null;
        }

        for (int i = 0; i < LocalHoldingLinker.localHoldingFields.length; ++i) {
            try {
                URL = sUtil.replace(URL, LocalHoldingLinker.localHoldingFields[i], getDataForLocalHolding(LocalHoldingLinker.localHoldingFields[i]),
                    StringUtil.REPLACE_GLOBAL, StringUtil.MATCH_CASE_SENSITIVE);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }

        }
        return URL;
    }

    private String getDataForLocalHolding(String field) throws UnsupportedEncodingException {

        String value = null;

        if (field.equals(LocalHoldingLinker.AULAST)) {
            if (mapDocument.containsKey(Keys.AUTHORS)) {
                Contributors authors = (Contributors) mapDocument.get(Keys.AUTHORS);
                Contributor author = authors.getFirstContributor();
                value = author.getLastName();
            }
        } else if (field.equals(LocalHoldingLinker.AUFIRST)) {
            if (mapDocument.containsKey(Keys.AUTHORS)) {
                Contributors authors = (Contributors) mapDocument.get(Keys.AUTHORS);
                Contributor author = authors.getFirstContributor();
                value = author.getFirstName();
            }
        } else if (field.equals(LocalHoldingLinker.ANUMBER)) {
            if (mapDocument.containsKey(ACCESSION_NUMBER)) {
                ElementData elementData = (ElementData) mapDocument.get(ACCESSION_NUMBER);
                String[] edata = elementData.getElementData();
                value = edata[0];
            }
        } else if (field.equals(LocalHoldingLinker.AUFULL)) {
            if (mapDocument.containsKey(Keys.AUTHORS)) {
                Contributors authors = (Contributors) mapDocument.get(Keys.AUTHORS);
                Contributor author = authors.getFirstContributor();
                value = author.getName();
            }
        } else if (field.equals(LocalHoldingLinker.ISSN)) {
            if (mapDocument.containsKey(Keys.ISSN)) {
                ISSN issn = (ISSN) mapDocument.get(Keys.ISSN);

                if (issn.getKey().getKey().equals(Keys.E_ISSN.getKey())) {
                    value = null;
                } else {
                    value = issn.withoutDash();
                }
            }
        } else if (field.equals(LocalHoldingLinker.ISSN9)) {
            if (mapDocument.containsKey(Keys.ISSN)) {
                ISSN issn = (ISSN) mapDocument.get(Keys.ISSN);
                if (issn.getKey().getKey().equals(Keys.E_ISSN.getKey())) {
                    value = null;
                } else {
                    value = issn.withDash();
                }
            }
        } else if (field.equals(LocalHoldingLinker.EISSN)) {
            if (mapDocument.containsKey(Keys.E_ISSN)) {
                ISSN issn = (ISSN) mapDocument.get(Keys.E_ISSN);// Electronic ISSN
                value = issn.withoutDash();
            } else {
                if (mapDocument.containsKey(Keys.ISSN)) {
                    ISSN issn = (ISSN) mapDocument.get(Keys.ISSN);
                    if (issn.getKey().getKey().equals(Keys.E_ISSN.getKey())) {
                        value = issn.withoutDash();
                    }
                }
            }
        } else if (field.equals(LocalHoldingLinker.ISBN1)) {
            if (mapDocument.containsKey(Keys.ISBN13)) {
                ElementData isbn = (ElementData) mapDocument.get(Keys.ISBN13);
                String[] values = isbn.getElementData();
                if ((values != null) && (values.length >= 1)) {
                    value = values[0].replaceAll("\\W", "");
                }
            } else if (mapDocument.containsKey(Keys.ISBN)) {
                ElementData isbn = (ElementData) mapDocument.get(Keys.ISBN);
                String[] values = isbn.getElementData();
                if ((values != null) && (values.length >= 1)) {
                    value = values[0].replaceAll("\\W", "");
                }
            }
        } else if (field.equals(LocalHoldingLinker.ISBN2)) {
            if (mapDocument.containsKey(Keys.ISBN13)) {
                ElementData isbn = (ElementData) mapDocument.get(Keys.ISBN13);
                String[] values = isbn.getElementData();
                if ((values != null) && (values.length >= 1)) {
                    value = values[0].replaceAll("\\W", "");
                }
            } else if (mapDocument.containsKey(Keys.ISBN)) {
                ElementData isbn = (ElementData) mapDocument.get(Keys.ISBN);
                String[] values = isbn.getElementData();
                if ((values != null) && (values.length >= 1)) {
                    value = values[0].replaceAll("\\W", "");
                }
            }
        } else if (field.equals(LocalHoldingLinker.CODEN)) {
            if (mapDocument.containsKey(Keys.CODEN)) {
                ElementData coden = (ElementData) mapDocument.get(Keys.CODEN);
                String[] edata = coden.getElementData();
                value = edata[0];
            }
        } else if (field.equals(LocalHoldingLinker.TITLE)) {
            if (mapDocument.containsKey(Keys.SERIAL_TITLE)) {
                ElementData stitle = (ElementData) mapDocument.get(Keys.SERIAL_TITLE);
                String[] edata = stitle.getElementData();
                value = edata[0];
            }
            // TMH - 03/2014 Univ of Nevada Reno support
            // Put in Monograph title if available
            else if (mapDocument.containsKey(Keys.MONOGRAPH_TITLE)) {
                ElementData ctitle = (ElementData) mapDocument.get(Keys.MONOGRAPH_TITLE);
                String[] edata = ctitle.getElementData();
                value = edata[0];
            }
            // JAM - 12/2008 "Reindeer Games" release
            // put in Conference title if Serial title is not present
            else if (mapDocument.containsKey(Keys.CONFERENCE_NAME)) {
                ElementData ctitle = (ElementData) mapDocument.get(Keys.CONFERENCE_NAME);
                String[] edata = ctitle.getElementData();
                value = edata[0];
            }
        } else if (field.equals(LocalHoldingLinker.STITLE)) {
            if (mapDocument.containsKey(Keys.ABBRV_SERIAL_TITLE)) {
                ElementData atitle = (ElementData) mapDocument.get(Keys.ABBRV_SERIAL_TITLE);
                String[] edata = atitle.getElementData();
                value = edata[0];
            } else if (mapDocument.containsKey(Keys.SOURCE)) {
                ElementData atitle = (ElementData) mapDocument.get(Keys.SOURCE);
                String[] edata = atitle.getElementData();
                value = edata[0];
            }
        } else if (field.equals(LocalHoldingLinker.ATITLE)) {
            if (mapDocument.containsKey(Keys.TITLE)) {
                ElementData atitle = (ElementData) mapDocument.get(Keys.TITLE);
                String[] edata = atitle.getElementData();
                value = edata[0];
            }
        } else if (field.equals(LocalHoldingLinker.CTITLE)) {
            // JAM - 12/2008 "Reindeer Games" release
            // do nothing - there is no mapping for this field
            // see LocalHoldingLinker.TITLE above
        } else if (field.equals(LocalHoldingLinker.VOLUME)) {
            value = getVolumeNo();
        } else if (field.equals(LocalHoldingLinker.VOLUME2)) {
            value = getVolume();
        } else if (field.equals(LocalHoldingLinker.ISSUE)) {
            value = getIssueNo();
        } else if (field.equals(LocalHoldingLinker.ISSUE2)) {
            value = getIssue();
        } else if (field.equals(LocalHoldingLinker.SPAGE)) {
            value = getStartPage();
        } else if (field.equals(LocalHoldingLinker.EPAGE)) {
            if (mapDocument.containsKey(Keys.PAGE_RANGE)) {
                PageRange pageRange = (PageRange) mapDocument.get(Keys.PAGE_RANGE);
                value = pageRange.getEndPage();
            }
        } else if (field.equals(LocalHoldingLinker.PAGES)) {
            if (mapDocument.containsKey(Keys.PAGE_RANGE)) {
                PageRange pageRange = (PageRange) mapDocument.get(Keys.PAGE_RANGE);
                value = pageRange.getPageRange();
            }
        } else if (field.equals(LocalHoldingLinker.ARTNUM)) {
            if (mapDocument.containsKey(Keys.ARTICLE_NUMBER)) {
                ElementData articleNum = (ElementData) mapDocument.get(Keys.ARTICLE_NUMBER);
                String[] artNum = articleNum.getElementData();
                value = artNum[0];

            }
        } else if (field.equals(LocalHoldingLinker.YEAR)) {
            Year year = null;
            if ((year = (Year) mapDocument.get(Keys.PUBLICATION_YEAR)) != null) {
                value = year.getYYYY();
            } else if ((year = (Year) mapDocument.get(Keys.LINKING_YEAR)) != null) {
                value = year.getYYYY();
            }
        } else if (field.equals(LocalHoldingLinker.DOI)) {
            value = getDOI();
        } else if (field.equals(LocalHoldingLinker.GENRE)) {
            // Use the doctype to output "genre"
            value = DocTypeToGenreConverter.normalize(getDocType());
        }

        if (value != null) {
            value = URLEncoder.encode(value.trim(), "UTF-8");
        } else {
            return "";
        }

        return value;
    }

    public Map<Key, String[]> getHighlightData(String field) {

        if (field.equalsIgnoreCase("all")) {
            return getHighlightMap(ALL_HIGHLIGHT_KEYS);
        } else if (field.equalsIgnoreCase("ky")) {
            return getHighlightMap(KY_HIGHLIGHT_KEYS);
        } else if (field.equalsIgnoreCase("af")) {
            return getHighlightMap(AF_HIGHLIGHT_KEYS);
        } else if (field.equalsIgnoreCase("cf")) {
            return getHighlightMap(CF_HIGHLIGHT_KEYS);
        }

        else if (field.equalsIgnoreCase("fl")) {
            return getHighlightMap(FL_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("st")) {
            return getHighlightMap(ST_HIGHLIGHT_KEYS);
        } else if (field.equalsIgnoreCase("nt")) {
            return getHighlightMap(NT_HIGHLIGHT_KEYS);
        }

        else if (field.equalsIgnoreCase("pn")) {
            return getHighlightMap(PN_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("cv")) {

            return getHighlightMap(CV_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("cvp")) {

            return getHighlightMap(CV_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("cvn")) {

            return getHighlightMap(CV_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("cva")) {

            return getHighlightMap(CV_HIGHLIGHT_KEYS);

        }

        else if (field.equalsIgnoreCase("mh")) {
            return getHighlightMap(MH_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("av")) {
            return getHighlightMap(AV_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("co")) {
            return getHighlightMap(CO_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("ti")) {
            return getHighlightMap(TI_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("ab")) {

            return getHighlightMap(AB_HIGHLIGHT_KEYS);
        }

        else if (field.equalsIgnoreCase("cvm")) {

            return getHighlightMap(MJS_HIGHLIGHT_KEYS);
        } else if (field.equalsIgnoreCase("cvmp")) {

            return getHighlightMap(MJS_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("cvmn")) {

            return getHighlightMap(MJS_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("cvma")) {

            return getHighlightMap(MJS_HIGHLIGHT_KEYS);

        } else if (field.equalsIgnoreCase("lt")) {

            return getHighlightMap(LT_HIGHLIGHT_KEYS);
        } else if (field.equalsIgnoreCase("atm")) {

            return getHighlightMap(TEMPLATE_HIGHLIGHT_KEYS);
        } else if (field.equalsIgnoreCase("mlt")) {

            return getHighlightMap(MLT_HIGHLIGHT_KEYS);
        } else if (field.equalsIgnoreCase("rgi")) {

            return getHighlightMap(RGI_HIGHLIGHT_KEYS);
        }

        return null;
    }

    public Map<Key, String[]> getHighlightMap(Key[] keys) {
        Map<Key, String[]> m = new HashMap<Key, String[]>();
        for (int i = 0; i < keys.length; i++) {
            Key key = keys[i];
            if (mapDocument.get(key) != null) {
                String[] existsElementsData = ((ElementData) mapDocument.get(key)).getElementData();
                if (existsElementsData != null && existsElementsData.length > 0) {
                    m.put(key, ((ElementData) mapDocument.get(key)).getElementData());
                }
            }
        }

        return m;
    }

    public void setHighlightData(Map<Key, String[]> h) {

        Set<Key> keys = h.keySet();
        Iterator<Key> it = keys.iterator();
        while (it.hasNext()) {
            Key key = (Key) it.next();
            String[] values = (String[]) h.get(key);
            ElementData ed = (ElementData) mapDocument.get(key);
            ed.setElementData(values);
            mapDocument.put(key, ed);
        }
    }

    public String getPatNumber() {
        String result = null;
        if (this.mapDocument.containsKey(Keys.PATNUM)) {
            ElementData patns = this.mapDocument.get(Keys.PATNUM);
            String[] patnum = patns.getElementData();
            result = patnum[0];
        }
        return result;
    }

    public String getPatpubNumber() {
        String result = null;
        if (this.mapDocument.containsKey(Keys.PATENT_NUMBER1)) {
            ElementData patpubns = this.mapDocument.get(Keys.PATENT_NUMBER1);
            String[] patpubnum = patpubns.getElementData();
            result = patpubnum[0];
        }
        return result;
    }

    public String getPatKind() {
        String result = null;
        if (this.mapDocument.containsKey(Keys.KIND_CODE)) {
            ElementData patpubns = this.mapDocument.get(Keys.KIND_CODE);
            String[] patkind = patpubns.getElementData();
            result = patkind[0];
        }
        return result;
    }

    public String getCitationPatNum() {
        String result = null;
        if (this.mapDocument.containsKey(Keys.PATENT_NUMBER)) {
            ElementData patpubns = this.mapDocument.get(Keys.PATENT_NUMBER);
            String[] patpubnum = patpubns.getElementData();
            result = patpubnum[0];
        }
        return result;
    }

    public String getAccNumber() {
        String result = null;
        if (this.mapDocument.containsKey(Keys.ACCESSION_NUMBER)) {
            ElementData accnum = this.mapDocument.get(Keys.ACCESSION_NUMBER);
            String[] an = accnum.getElementData();
            result = an[0];
        }
        return result;

    }

    public String getAuthcdNumber() {
        String result = null;
        if (this.mapDocument.containsKey(Keys.AUTH_CODE)) {
            ElementData auths = this.mapDocument.get(Keys.AUTH_CODE);
            String[] authcd = auths.getElementData();
            result = authcd[0];
        }
        return result;
    }

    public String getYear() {
        String result = null;
        if (this.mapDocument.containsKey(Keys.PUBLICATION_YEAR)) {
            ElementData years = this.mapDocument.get(Keys.PUBLICATION_YEAR);
            Year yr = (Year) years;
            result = yr.getYYYY();
        }
        return result;
    }

    public String getType() {
        String result = null;
        if (this.mapDocument.containsKey(Keys.DOC_TYPE)) {

            ElementData doctypes = this.mapDocument.get(Keys.DOC_TYPE);
            String[] doctype = doctypes.getElementData();
            result = doctype[0];
        }
        return result;
    }

    public String getDatabase() {
        String result = null;
        if (this.mapDocument.containsKey(Keys.DOCID)) {
            DocID docid = (DocID) this.mapDocument.get(Keys.DOCID);
            result = docid.getDatabase().getID();
        }
        return result;
    }

    public boolean hasFulltextandLocalHoldingsLinks() {
        // Full-text, linda hall, local holdings
        // always true except if overridden
        return true;
    }

    // This method maps from the standard Ei/EV Document types
    // to the RIS document types for the TY element which starts each RIS record
    // It also exitst as a private method in org.ei.data.compendex.runtime.CPXDocBuilder.java
    // but it was needed for GeoRef RIS so I made it a public method here
    public String replaceTYwithRIScode(String str) {
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
            } else if (str.equals("MP")) {
                str = "MAP";
            } else {
                str = "JOUR";
            }
        } else {
            str = "JOUR";
        }
        return str;
    }
}
