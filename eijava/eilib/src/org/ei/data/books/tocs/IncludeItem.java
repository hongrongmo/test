package org.ei.data.books.tocs;

import java.util.LinkedList;
import java.util.List;

public class IncludeItem {

    private String pii = null;
    private String title = "";
    private List<PageRange> pageranges = null;
    

    public IncludeItem() {
      pageranges = new LinkedList<PageRange>();
    }

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

    public List<PageRange> getPageRanges() { 
        return this.pageranges; 
    }
    
    public void setPageRanges(List<PageRange> pageranges) {
      this.pageranges = pageranges;
    }
    
    public void addPageRange(PageRange pr) {
      this.pageranges.add(pr);
    }

    public String toString() {
      StringBuilder builder = new StringBuilder();

      builder.append(getClass().getName()).append("@").append(Integer.toHexString(hashCode()));
      builder.append("\n[");
      builder.append("title").append("='").append(getTitle()).append("' ");
      builder.append("pii").append("='").append(getPii()).append("' ");
      builder.append("ranges").append("='").append(getPageRanges()).append("' ");
      builder.append("]");

      return builder.toString();
    }
}
