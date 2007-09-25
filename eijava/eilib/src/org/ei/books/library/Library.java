package org.ei.books.library;

import java.util.ArrayList;
import java.util.HashMap;

public class Library extends LibraryComponent  {

	private static Library uniqueInstance;
	ArrayList libraryComponents = new ArrayList();
	public static HashMap isbnTitleMap = new HashMap();
	static String[] collectionNames = {"CHE","CIV","COM","ELE","MAT","SEC"};
	private Library() { }

	public static synchronized Library getInstance() throws Exception
	{
		if(uniqueInstance == null)
		{
			uniqueInstance = new Library();

			for(int i = 0; i < collectionNames.length;i++)
			{
				BookCollection bookCollection = new BookCollection(collectionNames[i]);
				bookCollection.populateBooks();
				uniqueInstance.add(bookCollection);
			}
		}

		return uniqueInstance;
	}
	public void add(LibraryComponent libraryComponent)
	{
		libraryComponents.add(libraryComponent);
	}

	public void remove(LibraryComponent libraryComponent)
	{
		libraryComponent.remove(libraryComponent);
	}

	public LibraryComponent getChild(int i)
	{
		return (LibraryComponent)libraryComponents.get(i);
	}

	public int getCollectionCount()
	{
		return libraryComponents.size();
	}
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
