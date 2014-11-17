package org.ei.data.books.tools;

import java.util.Iterator;
import java.util.Stack;


public class ChapterStartEndVisitor extends BookVisitor {

//    private Bookmark lastChild = null;
    private Stack endchildren = new Stack();
    
    public void visit(Bookmark mark) {
        mark.getChildren().accept(this);
    }

    public void visit(PDF_FileInfo pdffile) {
        pdffile.getBookmarks().accept(this);
        setLastChildrenEndpage(pdffile.getPageCount());
    }

    public void visit(Bookmarks marks) {
        Iterator itrbkmks = marks.iterator();
        Bookmark bkmk = null;
        Bookmark prevmark = null;

        while (itrbkmks.hasNext()) {
            bkmk = (Bookmark) itrbkmks.next();
            if(prevmark != null) {
                prevmark.setEndpage(bkmk.getPage());
                setLastChildrenEndpage(bkmk.getPage());
            }
            bkmk.accept(this);
            prevmark = bkmk;
        }
        if(bkmk != null) {
            endchildren.push(bkmk);
        }

    }

    private void setLastChildrenEndpage(long page) {
        // TODO Auto-generated method stub
        while (!endchildren.empty ()) { 
            Bookmark childmark = (Bookmark) endchildren.pop();
            childmark.setEndpage(page);
        }

    }

}
