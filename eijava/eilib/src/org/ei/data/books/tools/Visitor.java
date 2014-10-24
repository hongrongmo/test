package org.ei.data.books.tools;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface Visitor {
    public static Log log = LogFactory.getLog(Visitor.class);

    void visit(Bookmark mark);
    void visit(Bookmarks marks);
    void visit(PDF_FileInfo pdffile);
    void visit(PDF_Page pdfpage);

}
