package org.ei.data.books.tocs;

import java.util.LinkedList;
import java.util.List;

public class Issue implements Comparable {

  private String issn = null;

  private String isbn = null;

  private List<IncludeItem> includeItems = null;

  public Issue() {
    includeItems = new LinkedList<IncludeItem>();
  }

  public void addIncludeItem(IncludeItem i) {
    includeItems.add(i);
  }

  public List<IncludeItem> getIncludeItems() {
    return includeItems;
  }

  public void setIncludeItems(List<IncludeItem> includeItems) {
    this.includeItems = includeItems;
  }

  public String getIsbn() {
    return isbn.replaceAll("-","");
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public String getIssn() {
    return issn;
  }

  public void setIssn(String issn) {
    this.issn = issn;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append(getClass().getName()).append("@").append(
        Integer.toHexString(hashCode())).append(" [");
    builder.append("issn").append("='").append(getIssn()).append("' ");
    builder.append("isbn").append("='").append(getIsbn()).append("' ");
    builder.append("includeitems").append("='").append(getIncludeItems()).append("' ");
    builder.append("]");

    return builder.toString();
  }

  public int compareTo(Object o) {
    int result = 1;
    if (this.equals(o)) {
      result = 0;
    }
    return result;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + ((isbn == null) ? 0 : isbn.hashCode());
    result = PRIME * result + ((issn == null) ? 0 : issn.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final Issue other = (Issue) obj;
    if (isbn == null) {
      if (other.isbn != null)
        return false;
    } else if (!isbn.equals(other.isbn))
      return false;
    if (issn == null) {
      if (other.issn != null)
        return false;
    } else if (!issn.equals(other.issn))
      return false;
    return true;
  }


}