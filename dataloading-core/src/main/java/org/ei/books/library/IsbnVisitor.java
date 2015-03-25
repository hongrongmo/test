package org.ei.books.library;

public class IsbnVisitor extends BookResultVisitor {
    private String m_title;

    public IsbnVisitor(String title) {
        m_title = title;
    }

    public void visit(BookCollection bookCollection) {
        for (int i = 0; i < bookCollection.getBookCount(); i++) {
            Book book = (Book) bookCollection.getBook(i);
            if (book.getTitle().toLowerCase().equals(m_title)) {
                setResult(book.getIsbn());
                break;
            }
        }
    }

}
