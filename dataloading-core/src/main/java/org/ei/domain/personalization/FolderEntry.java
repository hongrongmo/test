package org.ei.domain.personalization;

/** project specific imports*/

import java.io.IOException;
import java.io.Writer;

import org.ei.domain.DocID;
import org.ei.domain.EIDoc;
import org.ei.domain.InvalidArgumentException;
import org.ei.domain.LocalHolding;
import org.ei.domain.XMLSerializable;

/**
 * SavedRecords related tasks
 * 
 * @author Kavitha.D
 * @version 1.0
 */

public class FolderEntry implements Comparable<Object>, XMLSerializable {
    private DocID docID = null;
    private EIDoc eiDoc = null;
    private String folderEntryHitIndex = null;
    private String remoteXmlSource = null;
    private int m_intDisplayIndex = 0;
    LocalHolding localHolding = null;

    /**
     * No argument constructor
     */

    public FolderEntry() {
    }

    /**
     * This constructor takes DocId objects as argument
     */

    public FolderEntry(DocID oDocId) throws InvalidArgumentException {
        setDocID(oDocId);

    }

    /**
     * This constructor takes DocID and FolderHitIndex as arguments
     **/

    public FolderEntry(DocID oDocId, String sFolderEntryHitIndex) throws InvalidArgumentException {
        setDocID(oDocId);
        setFolderEntrytHitIndex(sFolderEntryHitIndex);
    }

    /**
     * This constructor takes DocID ,EIDoc and FolderHitIndex as arguments
     **/

    public FolderEntry(DocID oDocId, EIDoc oEIDoc, String sFolderEntryHitIndex) throws InvalidArgumentException {
        setDocID(oDocId);
        setEIDoc(oEIDoc);
        setFolderEntrytHitIndex(sFolderEntryHitIndex);
    }

    /**
     * jam - 9/24/2002 Added new constructor which also takes display index as argument
     */

    /**
     * This constructor takes DocID ,EIDoc, FolderHitIndex and DisplayIndex as arguments
     */
    public FolderEntry(DocID oDocId, EIDoc oEIDoc, String sFolderEntryHitIndex, int intDisplayIndex) throws InvalidArgumentException {
        setDocID(oDocId);
        setEIDoc(oEIDoc);
        setFolderEntrytHitIndex(sFolderEntryHitIndex);
        setDisplayIndex(intDisplayIndex);

    }

    /**
     * jam - 9/24/2002 Added int member m_intDisplayIndex. This value can be set by outer loop while walking the contents of a Folder Page to ensure correct
     * sequential numbering.
     * 
     * FolderEntry.getFolderEntryHitIndex() is not proper for number folder contents in result set
     */
    public int getDisplayIndex() {
        return m_intDisplayIndex;
    }

    public void setDisplayIndex(int value) {
        if (value > 0) {
            m_intDisplayIndex = value;
        }
    }

    /**
     * Setting the DocID object
     */

    public void setDocID(DocID oDocId) throws InvalidArgumentException {
        if (oDocId == null) {
            throw new InvalidArgumentException("DocID object cannot be null ");
        } else {
            docID = oDocId;
        }
    }

    /**
     * Setting the EIDOC object
     */

    public void setEIDoc(EIDoc oEIDoc) throws InvalidArgumentException {
        if (oEIDoc == null) {
            throw new InvalidArgumentException("EIDoc cannot be null");
        } else {
            eiDoc = oEIDoc;
        }
    }

    /**
     * Setting the remte XML source
     */

    public void setRemoteDoc(String xmlData) throws InvalidArgumentException {
        if (xmlData == null) {
            throw new InvalidArgumentException("xmlData cannot be null");
        } else {
            remoteXmlSource = xmlData;
        }
    }

    /**
     * Setting the FolderEntryHitIndex object
     */

    public void setFolderEntrytHitIndex(String sFolderHitIndex) throws InvalidArgumentException {
        folderEntryHitIndex = sFolderHitIndex;
    }

    /**
     * Returns the EIDoc object
     */

    public EIDoc getEIDoc() {
        return eiDoc;
    }

    /**
     * Returns the FolderEntryHitIndex
     */
    public String getFolderEntryHitIndex() {
        return folderEntryHitIndex;
    }

    /**
     * Returns the DocID object
     */
    public DocID getDocID() {
        return docID;
    }

    public String getRemoteDoc() {
        return remoteXmlSource;
    }

    /*** Returns the to String */

    public String toString() {

        String xmlString = null;
        StringBuffer xmlBuffer = new StringBuffer();
        if (!(docID == null)) {
            xmlBuffer.append(docID.toString());
        }

        if (!(eiDoc == null)) {
            xmlBuffer.append(eiDoc.toString());
        }
        if (!(remoteXmlSource == null)) {
            xmlBuffer.append(remoteXmlSource.toString());
        }

        xmlBuffer.append(getFolderEntryHitIndex() + "");
        xmlString = xmlBuffer.toString();
        return xmlString;
    }

    /**
     * Returns The XMLString
     */
    /**
     * jam added comment tags for BR and ER for Bulk mode
     */
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

        if (!(eiDoc == null)) {
            eiDoc.toXML(out);
        }

        if (remoteXmlSource != null) {
            out.write(remoteXmlSource);
        }
        if ((eiDoc == null) && (remoteXmlSource != null)) {
            out.write("<EIDOCUMENT></EIDOCUMENT>");
        }

        if (folderEntryHitIndex == null) {
            out.write("<FOLDER-ENTRY-HIT-INDEX>");
            out.write("</FOLDER-ENTRY-HIT-INDEX>");
        } else {
            out.write("<FOLDER-ENTRY-HIT-INDEX>" + folderEntryHitIndex + "</FOLDER-ENTRY-HIT-INDEX>");
            out.write("<DOCUMENTBASKETHITINDEX>" + m_intDisplayIndex + "</DOCUMENTBASKETHITINDEX>");
        }

        out.write("</PAGE-ENTRY><!--ER-->");
    }

    /**
     * The equals method which compares two FolderEntry objects based on comparable method implementation
     */

    public boolean equals(Object obj) {
        FolderEntry paramBEntry = (FolderEntry) obj;
        String paramString = paramBEntry.toString();
        String localString = this.toString();
        if (localString.equals(paramString)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method will compare two objects of FolderEntry on basis of HitIndex
     */

    public int compareTo(Object obj) throws ClassCastException {
        FolderEntry paramBEntry = (FolderEntry) obj;
        Integer paramBHitIndex = Integer.parseInt(paramBEntry.getFolderEntryHitIndex());
        Integer localBHitIndex = Integer.parseInt(this.getFolderEntryHitIndex());
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

// End of FolderEntry.java
