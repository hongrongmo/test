package org.ei.books.library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import org.ei.domain.DatabaseConfig;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.xml.Entity;

public class BookCollection extends LibraryComponent {

    private ArrayList<LibraryComponent> libraryComponents = new ArrayList<LibraryComponent>();
    private String name;

    BookCollection(String name, Map<String, Book> bookMap) throws Exception {
        this.name = name;
        populateBooks(bookMap);
    }

    private void populateBooks(Map<String, Book> bookMap) throws Exception {
        Connection con = null;
        ConnectionBroker broker = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SEARCH_POOL);

            pstmt1 = con.prepareStatement("select * from pages_all where page_num=0 and vo=?");

            pstmt1.setString(1, name);
            rs = pstmt1.executeQuery();

            while (rs.next()) {
                String bn13 = rs.getString("bn13");
                String ti = rs.getString("ti");
                String cvs = rs.getString("cvs");
                String vo = rs.getString("vo");
                int sub = rs.getInt("sub");
                String authors = rs.getString("AUS");
                String publisher = rs.getString("PN");
                String year = rs.getString("YR");

                if (vo != null && vo.equalsIgnoreCase(name)) {
                    Book curBook = new Book(bn13, ti, sub, cvs);
                    curBook.setAuthors(authors);
                    curBook.setPublisher(publisher);
                    curBook.setPubyear(year);
                    curBook.setCollection(vo);
                    libraryComponents.add(curBook);
                    bookMap.put(bn13, curBook);
                    bookMap.put(Entity.prepareString(ti.toLowerCase()), curBook);
                }
            }

        } finally {
            if (rs != null) {
                close(rs);
            }

            if (pstmt1 != null) {
                close(pstmt1);
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SEARCH_POOL);
                } catch (Exception cpe) {
                    cpe.printStackTrace();
                }
            }
        }
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

    public String getName() {
        return name;
    }

    public int getBookCount() {
        return libraryComponents.size();
    }

    public Book getBook(int i) {
        return (Book) libraryComponents.get(i);
    }

    private void close(PreparedStatement ps) {
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
