package org.ei.books.library;

public class BookResultVisitor implements Visitor {
  private String m_result = null;

	public void visit(LibraryComponent libraryComponent) {
	}


	public void visit(BookCollection bookCollection) {
	}

  protected void setResult(String result) {
    m_result = result;
  }

  public String getResult() {
    return m_result;
  }

	public void visit(Library library)
	{
		for (int i = 0; i < library.getCollectionCount(); i++)
		{
			BookCollection bookCollection = (BookCollection) library.getChild(i);
      if(getResult() == null) {
  			bookCollection.accept(this);
  		}
  		else {
  		  break;
  		}
		}
	}
}
