package org.ei.books.library;

public class Book extends LibraryComponent{

	private String isbn;
	private String title;
	private String cvs;
	int sub;
	private String m_authors;
	private String m_publisher;
	private String m_year;
	private String m_vo;

	public Book(String isbn, String title,int sub, String cvs)
	{
		this.isbn = isbn;
		this.title = title;
		this.cvs = cvs;
		this.sub = sub;
	}

	public String getIsbn() { return isbn; }
	public String getTitle() { return title; }
	public int getSubCollection() { return sub; }
	public String getCVS() { return cvs; }


  public void setCollection(String vo) { m_vo = vo; }
  public void setAuthors(String authors) { m_authors = authors; }
  public void setPublisher(String publisher) { m_publisher = publisher; }
  public void setPubyear(String year) { m_year = year;}

  public String getCollection() { return m_vo; }
  public String getAuthors() { return m_authors; }
  public String getPublisher() { return m_publisher; }
  public String getPubyear() { return m_year; }
}
