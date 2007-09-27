package org.ei.domain;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.*;
import java.net.*;
import org.ei.domain.DocID;
import org.ei.query.base.*;
import org.ei.util.StringUtil;
import org.ei.fulldoc.*;

public class EIDoc implements Highlightable, XMLSerializable, LocalHoldingLinker, Keys {
    private int loadNumber = -1;
    private Key[] outputKeys;
    private boolean labels = false;
    private boolean deep = false;
    private String format;
    private String view;

    public static final String eiRootDocumentTag = "EI-DOCUMENT";

    private ElementDataMap mapDocument;
    private DocID docID;

	public String getView()
	{
		return this.view;
	}

	public void setView(String view)
	{
		this.view = view;
	}

	public boolean getDeep()
	{
		return this.deep;
	}

	public void setDeep(boolean deep)
	{
		this.deep = deep;
	}

	public boolean isBook()
	{
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
                if(xml != null)
                {
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

    private String getIssueNo() {
        String value = null;
        if (mapDocument.containsKey(Keys.ISSUE)) {
            Issue issue = (Issue) mapDocument.get(Keys.ISSUE);
            value = issue.getIssueNumber();
        }

        return value;
    }

    private String getStartPage() {
        String value = null;
        if (mapDocument.containsKey(Keys.PAGE_RANGE)) {
            PageRange pageRange = (PageRange) mapDocument.get(Keys.PAGE_RANGE);
            value = pageRange.getStartPage();
        }

        return value;
    }

    private String getDOI() {
        String value = null;
        if (mapDocument.containsKey(Keys.DOI)) {
            ElementData doi = (ElementData) mapDocument.get(Keys.DOI);
            String[] edata = doi.getElementData();
            value = edata[0];
        }
        return value;
    }

    public String getFT()
    {
        String ft = null;
        LinkingStrategy ls = this.docID.getDatabase().getLinkingStrategy();

        try
        {
            ft = ls.hasLink(this);
        }
        catch(Exception e)
        {
            ft = LinkingStrategy.HAS_LINK_NO;
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

        if (mapDocument.containsKey(Keys.ISSN))
        {
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
            URL = sUtil.replace(URL, LocalHoldingLinker.localHoldingFields[i], getDataForLocalHolding(LocalHoldingLinker.localHoldingFields[i]), StringUtil.REPLACE_GLOBAL, StringUtil.MATCH_CASE_SENSITIVE);

        }

        return URL;
    }

    private String getDataForLocalHolding(String field) {

        String value = null;

        if (field.equals(LocalHoldingLinker.AULAST)) {
            if (mapDocument.containsKey(Keys.AUTHORS)) {
                Contributors authors = (Contributors) mapDocument.get(Keys.AUTHORS);
                Contributor author = authors.getFirstContributor();
                value = author.getLastName();
            }
        }
        else if (field.equals(LocalHoldingLinker.AUFIRST)) {
            if (mapDocument.containsKey(Keys.AUTHORS)) {
                Contributors authors = (Contributors) mapDocument.get(Keys.AUTHORS);
                Contributor author = authors.getFirstContributor();
                value = author.getFirstName();
            }
        }
        else if (field.equals(LocalHoldingLinker.ANUMBER)) {
            if (mapDocument.containsKey(ACCESSION_NUMBER)) {
                ElementData elementData = (ElementData) mapDocument.get(ACCESSION_NUMBER);
                String[] edata = elementData.getElementData();
                value = edata[0];
            }
        }
        else if (field.equals(LocalHoldingLinker.AUFULL)) {
            if (mapDocument.containsKey(Keys.AUTHORS)) {
                Contributors authors = (Contributors) mapDocument.get(Keys.AUTHORS);
                Contributor author = authors.getFirstContributor();
                value = author.getName();
            }
        }
        else if (field.equals(LocalHoldingLinker.ISSN)) {
            value = getISSN2();
        }
        else if (field.equals(LocalHoldingLinker.ISSN9)) {
            if (mapDocument.containsKey(Keys.ISSN)) {
                ISSN issn = (ISSN) mapDocument.get(Keys.ISSN);
                value = issn.withDash();
            }
        }
        else if (field.equals(LocalHoldingLinker.EISSN)) {
            if (mapDocument.containsKey(Keys.E_ISSN)) {
                ISSN issn = (ISSN) mapDocument.get(Keys.E_ISSN);//Electronic ISSN
                value = issn.withoutDash();

            }
        }
        else if (field.equals(LocalHoldingLinker.ISBN1)) {
            if (mapDocument.containsKey(Keys.ISBN)) {
                ISBN isbn = (ISBN) mapDocument.get(Keys.ISBN);
                value = isbn.withoutDash();
            }
        }
        else if (field.equals(LocalHoldingLinker.ISBN2)) {
            if (mapDocument.containsKey(Keys.ISBN)) {
                ISBN isbn = (ISBN) mapDocument.get(Keys.ISBN);
                value = isbn.withDash();
            }
        }
        else if (field.equals(LocalHoldingLinker.CODEN)) {
            if (mapDocument.containsKey(Keys.CODEN)) {
                ElementData coden = (ElementData) mapDocument.get(Keys.CODEN);
                String[] edata = coden.getElementData();
                value = edata[0];
            }
        }
        else if (field.equals(LocalHoldingLinker.TITLE)) {
            if (mapDocument.containsKey(Keys.SERIAL_TITLE)) {
                ElementData stitle = (ElementData) mapDocument.get(Keys.SERIAL_TITLE);
                String[] edata = stitle.getElementData();
                value = edata[0];
            }
        }
        else if (field.equals(LocalHoldingLinker.STITLE)) {
            if (mapDocument.containsKey(Keys.ABBRV_SERIAL_TITLE)) {
                ElementData atitle = (ElementData) mapDocument.get(Keys.ABBRV_SERIAL_TITLE);
                String[] edata = atitle.getElementData();
                value = edata[0];
            }
            else if (mapDocument.containsKey(Keys.SOURCE)) {
                ElementData atitle = (ElementData) mapDocument.get(Keys.SOURCE);
                String[] edata = atitle.getElementData();
                value = edata[0];
            }
        }
        else if (field.equals(LocalHoldingLinker.ATITLE)) {
            if (mapDocument.containsKey(Keys.TITLE)) {
                ElementData atitle = (ElementData) mapDocument.get(Keys.TITLE);
                String[] edata = atitle.getElementData();
                value = edata[0];
            }
        }
        else if (field.equals(LocalHoldingLinker.CTITLE)) {
            if (mapDocument.containsKey(Keys.CONFERENCE_NAME)) {
                ElementData ctitle = (ElementData) mapDocument.get(Keys.CONFERENCE_NAME);
                String[] edata = ctitle.getElementData();
                value = edata[0];
            }
        }
        else if (field.equals(LocalHoldingLinker.VOLUME)) {
            value = getVolumeNo();
        }
        else if (field.equals(LocalHoldingLinker.ISSUE)) {
            value = getIssueNo();
        }
        else if (field.equals(LocalHoldingLinker.SPAGE)) {
            value = getStartPage();
        }
        else if (field.equals(LocalHoldingLinker.EPAGE)) {
            if (mapDocument.containsKey(Keys.PAGE_RANGE)) {
                PageRange pageRange = (PageRange) mapDocument.get(Keys.PAGE_RANGE);
                value = pageRange.getEndPage();
            }
        }
        else if (field.equals(LocalHoldingLinker.PAGES)) {
            if (mapDocument.containsKey(Keys.PAGE_RANGE)) {
                PageRange pageRange = (PageRange) mapDocument.get(Keys.PAGE_RANGE);
                value = pageRange.getPageRange();
            }
        }
        else if (field.equals(LocalHoldingLinker.ARTNUM)) {
            if (mapDocument.containsKey(Keys.ARTICLE_NUMBER)) {
                ElementData articleNum = (ElementData) mapDocument.get(Keys.ARTICLE_NUMBER);
                String[] artNum = articleNum.getElementData();
                value = artNum[0];

            }
        }
        else if (field.equals(LocalHoldingLinker.YEAR))
        {
            Year year = null;
            if ((year = (Year)mapDocument.get(Keys.PUBLICATION_YEAR)) != null)
            {
                value = year.getYYYY();
            }
            else if((year = (Year) mapDocument.get(Keys.LINKING_YEAR)) != null)
            {
                value = year.getYYYY();
            }
        }
        else if (field.equals(LocalHoldingLinker.DOI))
        {
            value = getDOI();
        }

        if (value != null)
        {
            value = URLEncoder.encode(value.trim());
        }
        else
        {
            return "";
        }

        return value;
    }

    public Map getHighlightData(String field) {

        if (field.equalsIgnoreCase("all")) {
            return getHighlightMap(ALL_HIGHLIGHT_KEYS);
        }
        else if (field.equalsIgnoreCase("ky")) {
            return getHighlightMap(KY_HIGHLIGHT_KEYS);
        }
        else if (field.equalsIgnoreCase("af")) {
            return getHighlightMap(AF_HIGHLIGHT_KEYS);
        }
        else if (field.equalsIgnoreCase("cf")) {
            return getHighlightMap(CF_HIGHLIGHT_KEYS);
        }

        else if (field.equalsIgnoreCase("fl")) {
            return getHighlightMap(FL_HIGHLIGHT_KEYS);

        }
        else if (field.equalsIgnoreCase("st")) {
            return getHighlightMap(ST_HIGHLIGHT_KEYS);
        }
        else if (field.equalsIgnoreCase("nt")) {
            return getHighlightMap(NT_HIGHLIGHT_KEYS);
        }

        else if (field.equalsIgnoreCase("pn")) {
            return getHighlightMap(PN_HIGHLIGHT_KEYS);

        }
        else if (field.equalsIgnoreCase("cv")) {

            return getHighlightMap(CV_HIGHLIGHT_KEYS);

        }
        else if (field.equalsIgnoreCase("mh")) {
            return getHighlightMap(MH_HIGHLIGHT_KEYS);

        }
        else if (field.equalsIgnoreCase("av")) {
            return getHighlightMap(AV_HIGHLIGHT_KEYS);

        }
        else if (field.equalsIgnoreCase("co")) {
            return getHighlightMap(CO_HIGHLIGHT_KEYS);

        }
        else if (field.equalsIgnoreCase("ti")) {
            return getHighlightMap(TI_HIGHLIGHT_KEYS);

        }
        else if (field.equalsIgnoreCase("ab")) {

            return getHighlightMap(AB_HIGHLIGHT_KEYS);
        }
        else if (field.equalsIgnoreCase("rgi")) {

			return getHighlightMap(RGI_HIGHLIGHT_KEYS);
	    }


        return null;
    }

    public Map getHighlightMap(Key[] keys) {
        Map m = new HashMap();
        for (int i = 0; i < keys.length; i++) {
            Key key = keys[i];
            if (mapDocument.get(key) != null) {
                m.put(key, ((ElementData) mapDocument.get(key)).getElementData());
            }
        }

        return m;
    }

    public void setHighlightData(Map h) {

        Set keys = h.keySet();
        Iterator it = keys.iterator();
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



    public String getType()
    {
        String result = null;
        if (this.mapDocument.containsKey(Keys.DOC_TYPE))
        {

            ElementData doctypes = this.mapDocument.get(Keys.DOC_TYPE);
            String[] doctype = doctypes.getElementData();
            result = doctype[0];
        }
        return result;
    }

    public String getDatabase() {
        String result = null;
        if (this.mapDocument.containsKey(Keys.DOCID)) {
            ElementData years = this.mapDocument.get(Keys.DOCID);
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

}
