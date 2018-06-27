package org.ei.books.library;

public class LibraryComponent implements Visitable{

	public void add(LibraryComponent libraryComponent)
	{
		throw new UnsupportedOperationException();
	}
	
	public void remove(LibraryComponent libraryComponent)
	{
		throw new UnsupportedOperationException(); 
	}
	
	public LibraryComponent getChild(LibraryComponent libraryComponent)
	{
		throw new UnsupportedOperationException();
	}
	
	public String getIsbn()
	{
		throw new UnsupportedOperationException();
	}
	
	public int getSubjectCode()
	{
		throw new UnsupportedOperationException();
	}

	public String getTitle()
	{
		throw new UnsupportedOperationException();
	}
	
	public String getCitation()
	{
		throw new UnsupportedOperationException();
	}

	public int getBookCount()
	{
		throw new UnsupportedOperationException();
	}
	
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
