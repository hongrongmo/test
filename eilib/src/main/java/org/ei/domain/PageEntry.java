package org.ei.domain;

//project specific imports

// general imports
import java.io.IOException;
import java.io.Writer;

import org.ei.query.base.HitHighlighter;

/////////////////////////////////////////////////////////////////////////////////////
/**
 * Handles Page related tasks
 */
// ///////////////////////////////////////////////////////////////////////////////////

public class PageEntry implements Comparable<Object>, XMLSerializable {

    private EIDoc doc;
    private boolean isSelected;
    private String basketSearchid = "";
    private HitHighlighter highlighter;
    LocalHolding localHolding = null;

    private boolean skip;
    private boolean isDup;
    private String dupIds;

    public void setSkip(boolean inBool) {
        this.skip = inBool;
    }

    public void setIsDup(boolean isDup) {
        this.isDup = isDup;
    }

    public void setDupIds(String dupIds) {
        this.dupIds = dupIds;
    }

    public void setHitHighlighter(HitHighlighter h) {
        this.highlighter = h;
    }

    /**
     * No argument constructor
     */

    public PageEntry() {

    }

    /**
     * This constructor takes Doc Id ,Qeury objects as arguments
     * 
     * @exception : InvalidArgumentException
     */

    public PageEntry(EIDoc doc, boolean isSelected) throws InvalidArgumentException {
        setDoc(doc);
        setSelected(isSelected);
    }

    // Implementations of all the set methods used in the above constructors

    /**
     * This method takes document object and assign it to the global value .
     * 
     * @param : EI Document.
     * @exception : InvalidArgumentException
     */

    public void setDoc(EIDoc aDoc) throws InvalidArgumentException {

        if (aDoc == null) {
            throw new InvalidArgumentException("Doc object cannot be null ");
        } else {
            doc = aDoc;
        }
    }

    /**
     * This method takes selected value whether the document is already in the document basket or not
     * 
     * @param : selected value true/false.
     * @exception : InvalidArgumentException
     */

    public void setSelected(boolean selected) throws InvalidArgumentException {
        this.isSelected = selected;
    }

    /**
     * This method returns the document.
     * 
     * @return : document.
     */

    public EIDoc getDoc() {
        return doc;
    }

    /**
     * This method returns the selected value.
     * 
     * @return : true - if the document is already in the document basket false - if the document is not present in the document basket.
     */

    public boolean getSelected() {
        return isSelected;
    }

    /**
     * This method returns the string representation of the object.
     * 
     * @return : string.
     */

    public String toString() {

        String xmlString = null;
        StringBuffer xmlBuffer = new StringBuffer();
        if (!(doc == null)) {
            xmlBuffer.append(doc.toString());
        }
        xmlBuffer.append(".");
        xmlBuffer.append(isSelected ? "Selected." : "Not Selected.");
        xmlString = xmlBuffer.toString();
        return xmlString;
    }

    public void toXML(Writer out) throws IOException {
        if (isDup) {
            out.write("<PAGE-ENTRY DUP=\"true\">");
            out.write("<DUPIDS>" + dupIds + "</DUPIDS>");
        } else {
            out.write("<PAGE-ENTRY DUP=\"false\">");
        }
        if (doc == null) {
            out.write("<DOC></DOC>");
        } else {
            if (highlighter != null) {
                doc = (EIDoc) highlighter.highlight(doc);
            }

            doc.toXML(out);
            if (localHolding != null) {
                out.write(localHolding.getLocalHoldingsXML(doc));
            }
        }
        out.write("<IS-SELECTED>");
        out.write(new Boolean(isSelected).toString());
        out.write("</IS-SELECTED>");
        if (isSelected) {
            out.write("<BASKET-SEARCHID>");
            out.write(basketSearchid);
            out.write("</BASKET-SEARCHID>");
        }
        out.write("</PAGE-ENTRY>");
    }

    /**
     * This method returns the boolean value by comparing the objects.
     * 
     * @return : true - if both the objects are equal, false - otherwise.
     */

    public boolean equals(Object obj) {
        PageEntry paramBEntry = (PageEntry) obj;
        String paramString = paramBEntry.toString();
        String localString = this.toString();
        return localString.equals(paramString);

    }

    /**
     * Compares the Object with the specified object for order Method is implemented to get Sorted PageEntry list.
     * 
     * @exception : ClassCastException
     * @return : -1 if null or if less upon comparison
     */

    public int compareTo(Object obj) throws ClassCastException {
        PageEntry paramBEntry = (PageEntry) obj;
        return this.getDoc().toString().compareTo(paramBEntry.getDoc().toString());
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

    public String getBasketSearchid() {
        return basketSearchid;
    }

    public void setBasketSearchid(String basketSearchid) {
        this.basketSearchid = basketSearchid;
    }
}
