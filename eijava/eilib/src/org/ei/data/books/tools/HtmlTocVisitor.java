package org.ei.data.books.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

public class HtmlTocVisitor extends BookVisitor {

    Writer wrtr = null;
    String isbn = null;
    
    public HtmlTocVisitor() {
        
    }
    
    public void visit(PDF_FileInfo pdffile) {
        // TODO Auto-generated method stub
        String filename = pdffile.getFilePathname() + System.getProperty("file.separator") + pdffile.getIsbn() + "_toc.html";
        log.info(filename);
        try {
            wrtr = new PrintWriter(new FileOutputStream(filename));
            isbn = pdffile.getIsbn();
            pdffile.getBookmarks().accept(this);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if(wrtr != null) {
                try {
                    wrtr.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            wrtr = null;
        }
    }

    public void visit(Bookmarks marks) {
        try {
            wrtr.write("\r\n<ul>\r\n");
            Iterator itrbkmks = marks.iterator();
            while (itrbkmks.hasNext()) {
                Bookmark bkmk = (Bookmark) itrbkmks.next();
                wrtr.write("<li>");
                bkmk.accept(this);
                wrtr.write("</li>");
            }
            wrtr.write("</ul>\r\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
    public void visit(Bookmark mark) {
        // TODO Auto-generated method stub
        //log.info("Visiting bookmark: " + mark.toString());

        try {
            wrtr.write("<a ");
            wrtr.write(" title=\"");
            wrtr.write(mark.getTitle());
            wrtr.write("\"");
            wrtr.write(" href=\"javascript:loadFromToc('");
            wrtr.write(isbn);
            wrtr.write("',");
            wrtr.write(String.valueOf(mark.getPage()));
            wrtr.write(");void('');\">");
            wrtr.write(mark.getTitle());
            wrtr.write("</a>");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        mark.getChildren().accept(this);
    }

   
}
