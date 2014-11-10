package org.ei.domain;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * This is basic type which holds the hitIndex of the document, document id , document type and database .
 */
// ///////////////////////////////////////////////////////////////////////////////////////////////////////

public class DocID implements ElementData {

    private int hitIndex;
    private String docID;
    private Database database;
    protected boolean labels;
    protected Key key;

    private String doi;
    private String dedupKey;
    private String dedupOption;
    private int dmask;
    private String dupIndex;
    private boolean isDup;
    private String dupIds;
    private String tagDate;
    private String collection;
    private List<DocID> dupObjs = new ArrayList<DocID>();

    public void setKey(Key akey) {
        this.key = akey;
    }

    public Key getKey() {
        return this.key;
    }

    public DocID(String nDocID, Database sDatabase) {
        this.key = Keys.DOCID;
        this.docID = nDocID;
        this.database = sDatabase;
    }

    /**
     * Constructor which takes hitIndex,document id and database as arguments.
     */

    public DocID(int nHitIndex, String nDocID, Database sDatabase) {
        this.key = Keys.DOCID;
        this.hitIndex = nHitIndex;

        this.docID = nDocID;
        this.database = sDatabase;
    }

    public DocID(int nHitIndex, String nDocID, Database sDatabase, String dedupKey, String doi, int dmask) {
        this.key = Keys.DOCID;
        this.hitIndex = nHitIndex;
        this.docID = nDocID;
        this.database = sDatabase;
        this.dedupKey = dedupKey;
        this.dmask = dmask;
        this.doi = doi;
    }

    /**
     * set method to set the hitIndex for the document.
     * 
     * @param : int (hitIndex ).
     */

    public void setTagDate(String tagDate) {
        this.tagDate = tagDate;
    }

    public String getTagDate() {
        return this.tagDate;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getCollection() {
        return this.collection;
    }

    public void setHitIndex(int nHitIndex) {
        this.hitIndex = nHitIndex;
    }

    public void setDupIds(String dupIds) {
        this.dupIds = dupIds;
    }

    public void addDupObj(DocID dup) {
        this.dupObjs.add(dup);
    }

    public void setDedupIndex(int dedupIndex) {
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public void setDedupKey(String dedupKey) {
        this.dedupKey = dedupKey;
    }

    public void setDmask(int dmask) {
        this.dmask = dmask;
    }

    public void setIsDup(boolean isDup) {
        this.isDup = isDup;
    }

    public void setDedupOption(String dedupOption) {
        this.dedupOption = dedupOption;
    }

    public void setDupIndex(String dupIndex) {
        this.dupIndex = dupIndex;
    }

    /**
     * set method to set the id to the document.
     * 
     * @param : int(document id).
     */

    public void setDocID(String nDocID) {
        this.docID = nDocID;
    }

    /**
     * set the database in which the document contain.
     * 
     * @param : Database(database).
     */

    public void setDatabase(Database sDatabase) {
        this.database = sDatabase;
    }

    /**
     * gets the hitIndex of the document.
     * 
     * @return : int (hitIndex).
     */

    public int getHitIndex() {
        return hitIndex;
    }

    /**
     * gets the id of the document.
     * 
     * @return : int( document id).
     */

    public String getDocID() {
        return docID;
    }

    /**
     * gets the database of the document.
     * 
     * @return : database.
     */

    public Database getDatabase() {
        return database;
    }

    public String getDoi() {
        return doi;
    }

    public String getDedupKey() {
        return dedupKey;
    }

    public int getDmask() {
        return dmask;
    }

    public boolean isDup() {
        return this.isDup;
    }

    public String getDupIndex() {
        return dupIndex;
    }

    public String getDupIds() {
        return this.dupIds;
    }

    public List<DocID> getDupObjs() {
        return this.dupObjs;
    }

    public String getDedupOption() {
        return this.dedupOption;
    }

    /**
     * this method compares the two objects based on the doc id and database.
     * 
     * @return : true , if the two objects are equal : false , if the two objects are not equal.
     */
    public boolean equals(Object object) throws ClassCastException {
        if (object == null) {
            return false;
        } else {
            DocID docObj = (DocID) object;
            if ((this.getDocID().equals(docObj.getDocID())) && ((this.database.getID()).equals(docObj.getDatabase().getID()))) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Compares the Object with the specified object for order Method is implemented to get Sorted DocID list.
     * 
     * @exception : ClassCastException
     * @return : -1 if null or if less upon comparison
     */
    public int compareTo(Object object) throws ClassCastException {
        if (object == null) {
            return -1;
        } else {
            DocID docObj = (DocID) object;
            String sObjString = docObj.toString();
            String sThisString = this.toString();
            return sThisString.compareTo(sObjString);
        }
    }

    /**
     * This method returns the hashcode value of the string.
     * 
     * @return : int.
     */
    public int hashCode() {
        String hashCode = this.docID;
        return hashCode.hashCode();
    }

    /**
     * This method returns the string format of the object.
     * 
     * @return : String.
     */
    public String toString() {
        StringBuffer docString = new StringBuffer();
        docString.append("hitIndex:" + hitIndex);
        docString.append("\n doc id :" + docID);
        docString.append("\n database :" + database.getID());
        return docString.toString();
    }

    /**
     * This method returns the xml formatted string of the object.
     * 
     * @return : String.
     */
    public String toXMLString() {
        StringBuffer xmlString = new StringBuffer("<DOC>");
        xmlString.append("<HITINDEX>").append(hitIndex).append("</HITINDEX>");
        xmlString.append("<DOCID>").append(docID).append("</DOCID>");
        xmlString.append("<DATABASE>");
        xmlString.append(database.getID());
        xmlString.append("</DATABASE>");
        xmlString.append("</DOC>");
        return xmlString.toString();
    }

    public void toXML(Writer out) throws IOException {
        out.write("<DOC>");
        out.write("<HITINDEX>");
        out.write(Integer.toString(hitIndex));
        out.write("</HITINDEX>");
        if (this.tagDate != null) {
            out.write("<TAGDATE>");
            out.write(this.tagDate);
            out.write("</TAGDATE>");
        }
        out.write("<");
        out.write(this.key.getKey());
        out.write(">");
        out.write(docID);
        out.write("</");
        out.write(this.key.getKey());
        out.write(">");
        database.toXML(out);
        out.write("</DOC>");
    }

    public String[] getElementData() {
        String s[] = { this.docID };
        return s;
    }

    public void setElementData(String[] elementData) {
        this.docID = elementData[0];
    }

    public void exportLabels(boolean labels) {
        this.labels = labels;
    }
}
