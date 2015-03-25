package org.ei.books.library;

public class TitleVisitor extends BookResultVisitor {
    private String m_isbn;

    public TitleVisitor(String isbn) {
        m_isbn = isbn;
    }

    public void visit(BookCollection bookCollection) {
        for (int i = 0; i < bookCollection.getBookCount(); i++) {
            Book book = (Book) bookCollection.getBook(i);
            if (book.getIsbn().equals(m_isbn)) {
                setResult(book.getTitle());
                break;
            }
        }
    }

}
