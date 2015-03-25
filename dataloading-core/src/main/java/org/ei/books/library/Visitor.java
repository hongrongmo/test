package org.ei.books.library;

public interface Visitor {

	public void visit(LibraryComponent libraryComponent);
	public void visit(Library library);
	public void visit(BookCollection bookCollection);
}
