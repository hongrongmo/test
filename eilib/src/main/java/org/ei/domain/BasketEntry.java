package org.ei.domain;

/** project specific imports*/

import java.io.IOException;
import java.io.Writer;

/**
 * HitIndexs DocumentBasket related tasks
 * 
 * @author Syed S. Hussain
 * @version 1.0
 */

public class BasketEntry implements Comparable<Object>, XMLSerializable {
    private DocID docID = null;
    private Query query = null;
    private EIDoc eiDoc = null;
    private int documentBasketHitIndex = 0;
    LocalHolding localHolding = null;

    /**
     * No argument constructor
     */

    public BasketEntry() {

    }

    /**
     * This constructor takes Doc Id ,Qeury objects as arguments
     */

    public BasketEntry(DocID oDocId, Query oQuery) throws InvalidArgumentException {
        setDocID(oDocId);
        setQuery(oQuery);

    }

    /**
     * This constructor takes DocID,Query and BasketHitIndex as arguments
     **/

    public BasketEntry(DocID oDocId, Query oQuery, int iDBasketHitIndex) throws InvalidArgumentException {
        setDocID(oDocId);
        setQuery(oQuery);
        setDocumentBasketHitIndex(iDBasketHitIndex);
    }

    /**
     * This constructor takes DocID,Query ,EIDoc and BasketHitIndex as arguments
     **/

    public BasketEntry(DocID oDocId, Query oQuery, EIDoc oEIDoc, int iDBasketHitIndex) throws InvalidArgumentException {
        setDocID(oDocId);
        setQuery(oQuery);
        setEIDoc(oEIDoc);
        setDocumentBasketHitIndex(iDBasketHitIndex);
    }

    public void setDocID(DocID oDocId) throws InvalidArgumentException {
        if (oDocId == null) {
            throw new InvalidArgumentException("DocID object cannot be null ");
        } else {
            docID = oDocId;
        }
    }

    public void setQuery(Query oQuery) throws InvalidArgumentException {
        if ((oQuery == null) || (oQuery.getDisplayQuery() == null) || (oQuery.getRecordCount() == null)) {
            throw new InvalidArgumentException("Query object contains insufficient data");
        } else {
            query = oQuery;
        }
    }

    public void setEIDoc(EIDoc oEIDoc) throws InvalidArgumentException {
        if (oEIDoc == null) {
            throw new InvalidArgumentException("EIDoc cannot be null");
        } else {
            eiDoc = oEIDoc;
        }
    }

    public void setDocumentBasketHitIndex(int iDBasketHitIndex) throws InvalidArgumentException {
        if (iDBasketHitIndex < 0) {
            throw new InvalidArgumentException(" DocumentBasketHitIndex cannot be a negative number");
        } else {
            documentBasketHitIndex = iDBasketHitIndex;
        }
    }

    public EIDoc getEIDoc() {
        return eiDoc;
    }

    public int getDocumentBasketHitIndex() {
        return documentBasketHitIndex;
    }

    public DocID getDocID() {
        return docID;
    }

    public Query getQuery() {
        return query;
    }

    public String toString() {

        String xmlString = null;
        StringBuffer xmlBuffer = new StringBuffer();
        if (!(docID == null)) {
            xmlBuffer.append(docID.toString());
        }

        if (!(query == null)) {
            xmlBuffer.append(query.getDisplayQuery()).append(query.getRecordCount());

        }

        if (!(eiDoc == null)) {
            xmlBuffer.append(eiDoc.toString());
        }

        xmlBuffer.append(getDocumentBasketHitIndex() + "");
        xmlString = xmlBuffer.toString();
        return xmlString;
    }

    public void toXML(Writer out) throws IOException {
        out.write("<!--BR--><LINK>false</LINK><PAGE-ENTRY>");

        if (localHolding != null) {
            out.write(localHolding.getLocalHoldingsXML(eiDoc));
        }

        if (!(docID == null)) {
            out.write(docID.toXMLString());
        } else {
            out.write("<DOC></DOC>");
        }

        if (!((query.getDisplayQuery() == null) && (query.getRecordCount() == null))) {
            out.write("<QUERY>");
            out.write("<SEARCH-QUERY>");
            out.write("<![CDATA[");
            out.write(query.getDisplayQuery());
            out.write("]]>");
            out.write("</SEARCH-QUERY>");
            out.write("<RESULTS-COUNT>");
            out.write(query.getRecordCount());
            out.write("</RESULTS-COUNT>");
            out.write("</QUERY>");
        } else {
            out.write("<QUERY>");
            out.write("<SEARCH-QUERY>");
            out.write("</SEARCH-QUERY>");
            out.write("<RESULTS-COUNT>");
            out.write("</RESULTS-COUNT>");
            out.write("</QUERY>");
        }

        if (!(eiDoc == null)) {
            eiDoc.toXML(out);
        } else {
            out.write("<EIDOCUMENT></EIDOCUMENT>");
        }

        if (documentBasketHitIndex < 0) {
            out.write("<DOCUMENTBASKETHITINDEX>");
            out.write("</DOCUMENTBASKETHITINDEX>");
        } else {
            out.write("<DOCUMENTBASKETHITINDEX>" + documentBasketHitIndex + "</DOCUMENTBASKETHITINDEX>");
        }

        out.write("</PAGE-ENTRY><!--ER-->");
    }

    public boolean equals(Object obj) {
        BasketEntry paramBEntry = (BasketEntry) obj;
        String paramString = paramBEntry.toString();
        String localString = this.toString();
        if (localString.equals(paramString)) {
            return true;
        } else {
            return false;
        }
    }

    // This method will compare two objects

    public int compareTo(Object obj) throws ClassCastException {
        BasketEntry paramBEntry = (BasketEntry) obj;
        Integer paramBHitIndex = new Integer(paramBEntry.getDocumentBasketHitIndex());
        Integer localBHitIndex = new Integer(this.getDocumentBasketHitIndex());
        return localBHitIndex.compareTo(paramBHitIndex);
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
