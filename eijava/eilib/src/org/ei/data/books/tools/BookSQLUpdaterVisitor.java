package org.ei.data.books.tools;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class BookSQLUpdaterVisitor extends BookVisitor {

    private Writer wrtr = null;
    final static String FIELD_DELIMITER = ",";    
    
    public BookSQLUpdaterVisitor(Writer awriter) {
        wrtr = awriter;
    }
    
    public void visit(PDF_FileInfo pdffile) {
        // Write Page 0 - "Book Record"
        int pageZero = 0;
        

        // loop through all PDF pages
        for (int curpage = 1; curpage <= pdffile.getPageCount(); curpage++) {

            PDF_Page referexpage = pdffile.getPage(curpage);
        
            long page_size = 0; //referexpage.getTextSizeBytes();
            
            try {
                if((referexpage.getChapter() != null) || referexpage.getSection() != null)
                {
                    wrtr.write("UPDATE BOOK_PAGES_WOBL SET ");
    
                    // PII - Only if this page is part of a Chapter/Downloadable "CHUNK"
                    if(referexpage.getChapter() != null) {
                        wrtr.write("PII='chp_" + pdffile.getIsbn13().toLowerCase()+"_"+PDF_FileInfo.formatPageNumber(referexpage.getChapter().getPage()) + "'");
                        wrtr.write(FIELD_DELIMITER);
                    }
                    else {
                        wrtr.write("PII=NULL ");
                        wrtr.write(FIELD_DELIMITER);
                    }
                    // SECTION_TITLE
                    if(referexpage.getSection() != null) {
                        String sectionT = referexpage.getSection().getTitle();
                        sectionT = sectionT.replaceAll("'","''");
                        sectionT = sectionT.replaceAll("&","&'||'");
                        wrtr.write("SECTION_TITLE='" + sectionT + "' ");
                        wrtr.write(FIELD_DELIMITER);
                    }
    
                    // SECTION_START
                    if(referexpage.getSection() != null) {
                        wrtr.write("SECTION_START=" + referexpage.getSection().getPage() + " ");
                    }
    
                    if(referexpage.getChapter() != null) {
                        wrtr.write(FIELD_DELIMITER);
                    }
    
                    // CHAPTER_TITLE - Only if this page is part of a Chapter/Downloadable "CHUNK"
                    if(referexpage.getChapter() != null) {
                        String chapterT = referexpage.getChapter().getTitle();
                        chapterT = chapterT.replaceAll("'","''");
                        chapterT = chapterT.replaceAll("&","&'||'");
                        wrtr.write("CHAPTER_TITLE='" + chapterT + "' ");
                        wrtr.write(FIELD_DELIMITER);
                    }
                    
                    // CHAPTER_START - Only if this page is part of a Chapter/Downloadable "CHUNK"
                    if(referexpage.getChapter() != null) {
                        wrtr.write("CHAPTER_START=" + referexpage.getChapter().getPage() + " ");
                    }
    
                    // DOCID
                    wrtr.write(" WHERE DOCID='pag_" + pdffile.getIsbn13().toLowerCase()+"_"+Long.toString(curpage) + "';");
                   
                   
                    wrtr.write(System.getProperty("line.separator"));
                    
                }
            } catch (IOException e) {
                // TODO: handle exception
                log.error(e);
            }
        } // for
        try {
            wrtr.write("COMMIT;");
            wrtr.write(System.getProperty("line.separator"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void visit(PDF_Page pdfpage) {
        // The insides of the loop for the visit(PDF_FileInfo) method
        // could be put here and replaced with a
        // referexpage.accept(this) call
    }
    
//    public void visit(Bookmark mark) {
//        
//    }
//    
//    public void visit(Bookmarks marks) {
//        Iterator itrbkmks = marks.iterator();
//        Bookmark bkmk = null;
//
//        while (itrbkmks.hasNext()) {
//            bkmk = (Bookmark) itrbkmks.next();
//            bkmk.accept(this);
//        }
//    }
    
}
