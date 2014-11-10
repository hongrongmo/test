package org.ei.domain;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * This class has all the methods required for the Basketpage, get the documents in the basket from the particular page,
 * size of the page which contain how many documents,xml string of the page and adding of the document to the page.
 * 
 **/


public class BasketPage implements XMLSerializable {
    
    List<BasketEntry> basketList     = new ArrayList<BasketEntry>();
    private int       m_intPageIndex = 0;
    private int       m_intPageSize  = 0;
    LocalHolding      localHolding   = null;
    
    /**
     * Default Constructor.
     */
    
    public BasketPage() {
    }
    
    /**
     * This method retuns the no of documents available in the page. @ return : int (no of documents).
     */
    
    public int docCount() {
        if (basketList == null) {
            return 0;
        } else {
            return basketList.size();
        }
    }
    
    /**
     * This method returns the document at the particular index in the page. @ param : document index in the page. @
     * return : EIDoc object.
     */
    
    public BasketEntry docAt(int docIndex) {
        BasketEntry doc = (BasketEntry) basketList.get(docIndex);
        return doc;
    }
    
    /**
     * This methods adds the document to the page. @ param : BasketEntry object. @ return : BasketEntry object.
     */
    
    public BasketEntry add(BasketEntry basEntry) {
        basketList.add(basEntry);
        return basEntry;
    }
    
    /**
     * This method takes list of BasketEntry and adds each BasketEntry to the List. @ param : List of BasketEntry.
     */
    
    public void addAll(List<BasketEntry> basketEntryList) throws IOException {
        for (int i = 0; i < basketEntryList.size(); i++) {
            BasketEntry bas = (BasketEntry) basketEntryList.get(i);
            if (bas != null) {
                basketEntryList.add(bas);
            }
        }
    }
    
    /**
     * jam - 9/25/2002 Added int member m_intPageIndex. This value is set by DocumentBasket.pageAt() when the page is
     * created and filled with BasketEntries This will ensure correct numbering of BasketEntries when in bulk mode.
     * 
     * When not in bulk mode, basket entries are numbered using their positions Removing and then adding entries changes
     * the DocumentBasketHitIndex property and we will reset it when looping through a page. See toXML()
     */
    public int getPageIndex() {
        return m_intPageIndex;
    }
    
    public void setPageIndex(int value) {
        if (value > 0) {
            m_intPageIndex = value;
        }
    }
    
    public int getPageSize() {
        return m_intPageSize;
    }
    
    public void setPageSize(int value) {
        if (value > 0) {
            m_intPageSize = value;
        }
    }
    
    
    public void toXML(Writer out) throws IOException {
        
        out.write("<PAGE-RESULTS>");
        try {
            for (int i = 0; i < basketList.size(); i++) {
                // ((FolderEntry)folderList.get(i)).toXML(out);
                BasketEntry aBasketEntry = (BasketEntry) basketList.get(i);
                // list is zero based - display is 1 based
                aBasketEntry.setDocumentBasketHitIndex(((m_intPageIndex - 1) * m_intPageSize) + (i + 1));
                
                if (localHolding != null) {
                    aBasketEntry.setlocalHolding(localHolding);
                }
                
                aBasketEntry.toXML(out);
            }
        } catch (InvalidArgumentException iae) {
            
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
