package org.ei.data.books.tocs;

import java.util.ArrayList;
import java.util.List;

public class IncludeItem {

    private String pii = null;
    
    public String getPii() {
        return pii;
    }

    public void setPii(String pii) {
        this.pii = pii;
    }

    private List<PageRange> ranges = new ArrayList<PageRange>();
    
    
    public List<PageRange> getRanges() { return ranges; }
    
    public void addRange(String start, String end) {
        PageRange pr = new PageRange();
        pr.setStart(start);
        pr.setEnd(end);
        ranges.add(pr);
    }
}
