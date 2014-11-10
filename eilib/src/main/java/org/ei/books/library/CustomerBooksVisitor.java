package org.ei.books.library;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomerBooksVisitor implements Visitor {
    private List<Book> owned_books = new ArrayList<Book>();
    private String[] creds;
    private boolean perpetual = true;

    public CustomerBooksVisitor(String[] creds, boolean perpetual) {
        this.creds = creds;
        this.perpetual = perpetual;
    }

    private boolean checkCredentials(Book book, String curCollectionName) {
        int subCol = book.getSubCollection();
        String colTest = curCollectionName;

        if (subCol > 0) {
            colTest = curCollectionName + Integer.toString(subCol);
        }

        if (perpetual) {
            for (int i = 0; i < creds.length; i++) {
                String curColTest = colTest.toLowerCase();
                String curCred = creds[i].toLowerCase();
                if (curColTest.equals(curCred))
                    return true;
            }
        } else {
            for (int i = 0; i < creds.length; i++) {
                // substring(0,3) to get three characters - not substring(0,2)
                // length of the substring is endIndex-beginIndex.
                String curColTest = colTest.substring(0, 3).toLowerCase();
                String curCred = creds[i].substring(0, 3).toLowerCase();
                if (curColTest.equals(curCred))
                    return true;
            }
        }
        return false;
    }

    public boolean isColInCreds(String collectionName) {
        boolean inCreds = false;

        for (int i = 0; i < creds.length; i++) {
            if ((creds[i] != null) && (creds[i].length() >= 3)) {
                String curColName = collectionName.substring(0, 3).toLowerCase();
                String curCred = creds[i].substring(0, 3).toLowerCase();
                if (curColName.equals(curCred)) {
                    return true;
                }
            }
        }
        return inCreds;
    }

    public void toXML(Writer out) {
        try {
            out.write("<BOOKS COUNT=\"");
            out.write(String.valueOf(owned_books.size()));
            out.write("\">");
            Iterator<Book> itrBooks = owned_books.iterator();
            while (itrBooks.hasNext()) {
                Book abook = (Book) itrBooks.next();

                out.write("<BOOK>");
                out.write("<COLLECTION>");
                out.write(abook.getCollection() + (abook.getSubCollection() != 0 ? String.valueOf(abook.getSubCollection()) : ""));
                out.write("</COLLECTION>");
                out.write("<ISBN>");
                out.write(abook.getIsbn());
                out.write("</ISBN>");
                out.write("</BOOK>");
            }
            out.write("</BOOKS>");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void visit(LibraryComponent libraryComponent) {
        // Do nothing
        return;
    }

    public void visit(BookCollection bookCollection) {
        String curCollectionName = bookCollection.getName();

        if (isColInCreds(curCollectionName)) {
            for (int i = 0; i < bookCollection.getBookCount(); i++) {
                Book book = (Book) bookCollection.getBook(i);

                if (checkCredentials(book, curCollectionName)) {
                    owned_books.add(book);
                }
            }
        }
    }

    public void visit(Library library) {
        for (int i = 0; i < library.getCollectionCount(); i++) {
            BookCollection bookCollection = (BookCollection) library.getChild(i);
            bookCollection.accept(this);
        }
    }
}
