package org.ei.books.library;

import java.util.*;

public class Library extends LibraryComponent  {

	private static Library uniqueInstance;
	private ArrayList libraryComponents = new ArrayList();
	private Map isbnTitleMap;
	private static String[] collectionNames = {"CHE","CIV","COM","ELE","MAT","SEC"};




	private Library()
		throws Exception
	{
		this.isbnTitleMap = new HashMap();
		for(int i = 0; i < collectionNames.length;i++)
		{
			BookCollection bookCollection = new BookCollection(collectionNames[i],
			                                                   this.isbnTitleMap);
			add(bookCollection);
		}
	}

	public static synchronized Library getInstance() throws Exception
	{
		if(uniqueInstance == null)
		{
			uniqueInstance = new Library();
		}

		return uniqueInstance;
	}

	public Book getBook(String key)
	{
		return (Book) this.isbnTitleMap.get(key);
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
