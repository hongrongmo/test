package org.ei.data.books.tools;

import java.util.Iterator;


public class LogInfoVisitor extends BookVisitor {

    public void visit(Bookmark mark) {
        log.info("bookmark: " + mark.toString());
        mark.getChildren().accept(this);
    }

    public void visit(PDF_FileInfo pdffile) {
        log.info("pdffile: " + pdffile.getIsbn());
        pdffile.getBookmarks().accept(this);
    }

    public void visit(Bookmarks marks) {
        //log.info("Visiting list of bookmarks: ");
        Iterator itrbkmks = marks.iterator();
        while (itrbkmks.hasNext()) {
            Bookmark bkmk = (Bookmark) itrbkmks.next();
            bkmk.accept(this);
        }
        //log.info("done!");
    }

    public void visit(PDF_Page pdfpage) {
        // TODO Auto-generated method stub
        // this visitor does not 'Visit' pages - so do nothng
    }
}
