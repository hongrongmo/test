package org.ei.dataloading.xmlDataLoading;

public class Isbn extends BaseElement
{
	String isbn;
	String isbn_type;

    public void setIsbn(String isbn)
	{
		this.isbn = isbn;
	}

	public String getIsbn()
	{
		return this.isbn;
	}

	public void setIsbn_type(String isbn_type)
	{
		this.isbn_type = isbn_type;
	}

	public String getIsbn_type()
	{
		return this.isbn_type;
	}
}
