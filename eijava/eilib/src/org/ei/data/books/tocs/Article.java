package org.ei.data.books.tocs;


public class Article {

  private String title = null;
 
  public Article() {
    this.title = "";
  }
  
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append(getClass().getName()).append("@").append(
        Integer.toHexString(hashCode())).append(" [");
    builder.append("title").append("='").append(getTitle()).append("' ");
    builder.append("]");

    return builder.toString();
  }
}