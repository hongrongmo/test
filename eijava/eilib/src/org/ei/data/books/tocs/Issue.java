package org.ei.data.books.tocs;


public class Issue implements Comparable  {

    private String issn = null;
    private String isbn = null;
	
	public Issue() {
	}

    public int compareTo(Object o) {
        Issue anIssue = (Issue) o;
        // TODO Auto-generated method stub
        return 0;
    }

    public String getIsbn() {
        return isbn;
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
        // TODO Auto-generated method stub
        return "(" + getIssn() + ", " + getIsbn() + ")";
    }

}