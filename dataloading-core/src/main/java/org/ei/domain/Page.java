package org.ei.domain;

// general imports
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.ei.query.base.HitHighlighter;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * This class has all the methods required for the page, like get the document from the particular page, size of the page which contain how many documents,xml
 * string of the page and adding of the document to the page.
 */
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Page implements XMLSerializable {

    // List docList = new ArrayList();
    private List<PageEntry> entryList = new ArrayList<PageEntry>();
    private HitHighlighter highlighter;
    LocalHolding localHolding = null;

    private BitSet dupSet = null;
    private boolean skipDups = false;
    private String dbToSkip = null;

    private int pageIndex = 1;

    public String getDbToSkip() {
        return this.dbToSkip;
    }

    public void setDbToSkip(String inStr) {
        this.dbToSkip = inStr;
    }

    public BitSet getDupSet() {
        return this.dupSet;
    }

    public void setDupSet(BitSet inSet) {
        this.dupSet = inSet;
    }

    public boolean isSkipDups() {
        return this.skipDups;
    }

    public void setSkipDups(boolean skip) {
        this.skipDups = skip;
    }

    public void setHitHighlighter(HitHighlighter h) {
        this.highlighter = h;
    }

    /**
     * Default Constructor.
     */

    public Page() {

    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public void setPageIndex(int inInt) {
        this.pageIndex = inInt;
    }

    /**
     * This method retuns the no of documents available in the page.
     * 
     * @return : int (no of documents).
     */

    public int docCount() {
        return entryList.size();
    }

    /**
     * This method returns the document at the particular index in the page.
     * 
     * @param : document index in the page.
     * @return : EIDoc object.
     */

    public EIDoc docAt(int docIndex) {
        PageEntry entry = (PageEntry) entryList.get(docIndex);
        return entry.getDoc();
    }

    public PageEntry entryAt(int docIndex) {
        return (PageEntry) entryList.get(docIndex);
    }

    /**
     * This methods adds the document to the page.
     * 
     * @param : EIDoc object.
     * @return : EIDoc object.
     */

    public PageEntry add(PageEntry pageEntry) {
        entryList.add(pageEntry);
        return pageEntry;
    }

    /**
     * This method takes list of documents and adds each document to the List.
     * 
     * @param : List of EIDocuments.
     */

    public void addAll(List<PageEntry> pageEntryList) {
        entryList.addAll(pageEntryList);
    }

    public void toXML(Writer out) throws IOException {
        int length = entryList.size();

        out.write("<PAGE-RESULTS>");

        for (int i = 0; i < length; i++) {
            PageEntry entry = (PageEntry) entryList.get(i);
            entry.setHitHighlighter(this.highlighter);
            if (localHolding != null) {
                entry.setlocalHolding(localHolding);
            }
            entry.toXML(out);
        }

        out.write("</PAGE-RESULTS>");
    }

    /**
     * set localHolding to instance variable. @ param : localHolding.
     */
    public void setlocalHolding(LocalHolding localHolding) {
        this.localHolding = localHolding;
    }

    /**
     * get the value of localHolding. @ return : LocalHolding
     */
    public LocalHolding getlocalHolding() {
        return localHolding;
    }
}
