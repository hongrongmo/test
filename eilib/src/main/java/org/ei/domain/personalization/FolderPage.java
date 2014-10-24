package org.ei.domain.personalization;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.ei.domain.LocalHolding;
import org.ei.domain.XMLSerializable;

public class FolderPage implements XMLSerializable {
    List<FolderEntry> folderList = new ArrayList<FolderEntry>();
    LocalHolding localHolding = null;

    /**
     * Default Constructor.
     */

    public FolderPage() {

    }

    /**
     * This method retuns the no of documents available in the page. @ return : int (no of documents).
     */

    public int docCount() {
        if (folderList == null) {
            return 0;
        } else {
            return folderList.size();
        }
    }

    /**
     * This method returns the document at the particular index in the page. @ param : document index in the page. @ return : EIDoc object.
     */

    public FolderEntry docAt(int docIndex) {
        FolderEntry doc = (FolderEntry) folderList.get(docIndex);
        return doc;
    }

    /**
     * This methods adds the document to the page. @ param : FolderEntry object. @ return : FolderEntry object.
     */

    public FolderEntry add(FolderEntry aFolderEntry) {
        folderList.add(aFolderEntry);
        return aFolderEntry;
    }

    /**
     * This method takes list of FolderEntry and adds each FolderEntry to the List. @ param : List of FolderEntry.
     */

    public void addAll(List<FolderEntry> aFolderEntryList) throws IOException {
        for (int i = 0; i < aFolderEntryList.size(); i++) {
            FolderEntry fEntry = (FolderEntry) aFolderEntryList.get(i);
            if (fEntry != null) {
                aFolderEntryList.add(fEntry);
            }
        }
    }

    /**
     * jam - 9/24/2002 modified class to set the Display Index as each folder entry is retreived for proper sequential numbering when displaying contents in
     * bulk mode.
     * 
     */
    public void toXML(Writer out) throws IOException {
        String xmlString = null;
        out.write("<PAGE-RESULTS>");
        for (int i = 0; i < folderList.size(); i++) {
            // ((FolderEntry)folderList.get(i)).toXML(out);
            FolderEntry aFolderEntry = (FolderEntry) folderList.get(i);
            // list is zero based - display is 1 based
            aFolderEntry.setDisplayIndex(i + 1);

            if (localHolding != null) {
                aFolderEntry.setlocalHolding(localHolding);
            }

            aFolderEntry.toXML(out);
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
