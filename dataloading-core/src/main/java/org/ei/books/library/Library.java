package org.ei.books.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Library extends LibraryComponent {

    private static Library uniqueInstance;
    private ArrayList<LibraryComponent> libraryComponents = new ArrayList<LibraryComponent>();
    private Map<String, Book> isbnTitleMap;
    private static String[] collectionNames = { "CHE", "CIV", "COM", "ELE", "MAT", "SEC", "TNFCHE", "TNFCIV", "TNFCOM", "TNFELE", "TNFMAT", "TNFSEC" };

    private Library() throws Exception {
        this.isbnTitleMap = new HashMap<String, Book>();
        for (int i = 0; i < collectionNames.length; i++) {
            BookCollection bookCollection = new BookCollection(collectionNames[i], this.isbnTitleMap);
            add(bookCollection);
        }
    }

    public static synchronized Library getInstance() throws Exception {
        if (uniqueInstance == null) {
            uniqueInstance = new Library();
        }

        return uniqueInstance;
    }

    public Book getBook(String key) {
        return (Book) this.isbnTitleMap.get(key);
    }

    public void add(LibraryComponent libraryComponent) {
        libraryComponents.add(libraryComponent);
    }

    public void remove(LibraryComponent libraryComponent) {
        libraryComponent.remove(libraryComponent);
    }

    public LibraryComponent getChild(int i) {
        return (LibraryComponent) libraryComponents.get(i);
    }

    public int getCollectionCount() {
        return libraryComponents.size();
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
