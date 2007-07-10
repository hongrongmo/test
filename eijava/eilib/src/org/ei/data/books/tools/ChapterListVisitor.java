package org.ei.data.books.tools;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChapterListVisitor extends BookVisitor {

    final static String FIELD_DELIMITER = "\t";    

    private Writer wrtr = null;
    private PDF_FileInfo pdffile = null;
    private Map chap_fileList = new HashMap();
    
    public ChapterListVisitor(Writer awriter) {
        wrtr = awriter;
    }

    public void visit(Bookmark mark) {
        try {
            if(mark.isChapter()) {
                String strPii = "chp_" + pdffile.getIsbn13() + "_" + PDF_FileInfo.formatPageNumber(mark.getPage());
                if(!chap_fileList.containsKey(strPii)) {
                    wrtr.write("pdftk " + pdffile.getIsbn13() + ".pdf cat " + mark.getPage() + "-" + mark.getEndpage() + " output " + OutputPageData.PATH_PREFIX + System.getProperty("file.separator") + "BurstAndExtracted" + System.getProperty("file.separator") + pdffile.getIsbn13() + "\\" + strPii + ".pdf dont_ask");
                    wrtr.write(System.getProperty("line.separator"));
                    chap_fileList.put(strPii,new Boolean(true));
                }
            }
        }
        catch(IOException e) {
            log.error(e);
        }
        mark.getChildren().accept(this);
    }
    
    public void visit(PDF_FileInfo pdffile) {
        this.pdffile = pdffile;
        pdffile.getBookmarks().accept(this);
    }

    public void visit(Bookmarks marks) {
        Iterator itrbkmks = marks.iterator();
        Bookmark bkmk = null;

        while (itrbkmks.hasNext()) {
            bkmk = (Bookmark) itrbkmks.next();
            bkmk.accept(this);
        }
    }
    
}
