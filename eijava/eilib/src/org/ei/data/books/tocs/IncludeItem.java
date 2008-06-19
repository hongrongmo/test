package org.ei.data.books.tocs;

import java.util.ArrayList;
import java.util.List;

public class IncludeItem {

    private String pii = null;
    private String title = "";
    
    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

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

    public String toString() {
      StringBuffer buffer = new StringBuffer();

      buffer.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode())).append(" [");
      buffer.append("title").append("='").append(getTitle()).append("' ");
      buffer.append("pii").append("='").append(getPii()).append("' ");
      buffer.append("ranges").append("='").append(getRanges()).append("' ");
      buffer.append("]");

      return buffer.toString();
    }
}
