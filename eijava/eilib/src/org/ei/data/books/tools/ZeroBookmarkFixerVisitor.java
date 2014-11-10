package org.ei.data.books.tools;

import java.util.Iterator;
import java.util.Stack;


public class ZeroBookmarkFixerVisitor extends BookVisitor {

    private Stack needstart = new Stack();
    private long lastStart = 0;
    
    public void visit(Bookmark mark) {
        if(mark.getPage() == 0) {
            needstart.push(mark);
        }
        mark.getChildren().accept(this);
        setStartPages(lastStart);
        
    }

    public void visit(PDF_FileInfo pdffile) {
        pdffile.getBookmarks().accept(this);

        while (!needstart.empty ()) { 
            Bookmark zeromark = (Bookmark) needstart.pop();
            log.info("Unfixed " + zeromark);
        }
     }

    public void visit(Bookmarks marks) {
        Iterator itrbkmks = marks.iterator();
        Bookmark bkmk = null;

        while (itrbkmks.hasNext()) {
            bkmk = (Bookmark) itrbkmks.next();
            bkmk.accept(this);
            lastStart = bkmk.getPage();
        }
    }

    private void setStartPages(long page) {
        // TODO Auto-generated method stub
        if(lastStart != 0) {
//            log.info("lastStart " + lastStart);
            while (!needstart.empty ()) { 
                Bookmark zeromark = (Bookmark) needstart.pop();
                zeromark.setPage(page);
//                log.info("Fixing " + zeromark);
            }
        }
        lastStart = 0;
    }
}
