package org.ei.data.books.tools;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class SqlLoaderVisitor extends BookVisitor {

    private Writer wrtr = null;
    final static String FIELD_DELIMITER = ",";    
    
    public SqlLoaderVisitor(Writer awriter) {
        wrtr = awriter;
    }
    
    public void visit(PDF_FileInfo pdffile) {
        // Write Page 0 - "Book Record"
        int pageZero = 0;
        
        try {
            // DOCID
            wrtr.write("pag_" + pdffile.getIsbn13().toLowerCase()+"_"+String.valueOf(pageZero));
            wrtr.write(FIELD_DELIMITER);
            // BN 
            wrtr.write(pdffile.getIsbn().toLowerCase());
            wrtr.write(FIELD_DELIMITER);
            // BN13
            wrtr.write(pdffile.getIsbn13().toLowerCase());
            wrtr.write(FIELD_DELIMITER);

            // PII - Only if this page is part of a Chapter/Downloadable "CHUNK"
            wrtr.write(FIELD_DELIMITER);
            // PAGE_NUM
            wrtr.write(String.valueOf(pageZero));
            wrtr.write(FIELD_DELIMITER);
            // PAGE_LABEL
            wrtr.write(String.valueOf(pageZero));
            wrtr.write(FIELD_DELIMITER);

            // SECTION_TITLE
            wrtr.write("\"Book\"");
            wrtr.write(FIELD_DELIMITER);
            // SECTION_START
            wrtr.write(String.valueOf(pageZero));
            wrtr.write(FIELD_DELIMITER);

            // CHAPTER_TITLE - Only if this page is part of a Chapter/Downloadable "CHUNK"
            wrtr.write("\"Book\"");
            wrtr.write(FIELD_DELIMITER);
            
            // CHAPTER_START - Only if this page is part of a Chapter/Downloadable "CHUNK"
            wrtr.write(String.valueOf(pageZero));
            wrtr.write(FIELD_DELIMITER);

            // PAGE_BYTES
            wrtr.write(Long.toString(0));
            wrtr.write(FIELD_DELIMITER);

            // PAGE_TEXT
            wrtr.write("");
            wrtr.write(FIELD_DELIMITER);
            
            // PAGE_TOTAL
            wrtr.write(String.valueOf(pdffile.getPageCount()));
            wrtr.write(FIELD_DELIMITER);
            
            // PAGE_KEYWORDS
            wrtr.write(FIELD_DELIMITER);
            
            wrtr.write(FIELD_DELIMITER); // PP
            wrtr.write(FIELD_DELIMITER); // YR
            wrtr.write(FIELD_DELIMITER); // TI
            wrtr.write(FIELD_DELIMITER); // AUS
            wrtr.write(FIELD_DELIMITER); // CVS
            wrtr.write(FIELD_DELIMITER); // AF
            wrtr.write(FIELD_DELIMITER); // PN
            wrtr.write(FIELD_DELIMITER); // NT
            wrtr.write(FIELD_DELIMITER); // ST
            wrtr.write(FIELD_DELIMITER); // SP
            wrtr.write(FIELD_DELIMITER); // ISS
            wrtr.write(FIELD_DELIMITER); // VO
            wrtr.write(FIELD_DELIMITER); // AB
            wrtr.write(FIELD_DELIMITER); // SUB

            //PDFPP
            wrtr.write(String.valueOf(pdffile.getPageCount()));
          
            
            wrtr.write(System.getProperty("line.separator"));
        } catch (IOException e) {
            // TODO: handle exception
        }
        
        // loop through all PDF pages
        for (int curpage = 1; curpage <= pdffile.getPageCount(); curpage++) {

            PDF_Page referexpage = pdffile.getPage(curpage);
        
            long page_size = 0; //referexpage.getTextSizeBytes();
            
            try {
                // DOCID
                wrtr.write("pag_" + pdffile.getIsbn13().toLowerCase()+"_"+Long.toString(curpage));
                wrtr.write(FIELD_DELIMITER);
                // BN 
                wrtr.write(pdffile.getIsbn().toLowerCase());
                wrtr.write(FIELD_DELIMITER);
                // BN13
                wrtr.write(pdffile.getIsbn13().toLowerCase());
                wrtr.write(FIELD_DELIMITER);

                // PII - Only if this page is part of a Chapter/Downloadable "CHUNK"
                if(referexpage.getChapter() != null) {
                    wrtr.write("chp_" + pdffile.getIsbn13().toLowerCase()+"_"+PDF_FileInfo.formatPageNumber(referexpage.getChapter().getPage()));
                }
                wrtr.write(FIELD_DELIMITER);
                // PAGE_NUM
                wrtr.write(Long.toString(curpage));
                wrtr.write(FIELD_DELIMITER);
                // PAGE_LABEL
                wrtr.write(Long.toString(curpage));
                wrtr.write(FIELD_DELIMITER);

                // SECTION_TITLE
                if(referexpage.getSection() != null) {
                    wrtr.write("\"" + referexpage.getSection().getTitle() + "\"");
                }
                wrtr.write(FIELD_DELIMITER);
                // SECTION_START
                if(referexpage.getSection() != null) {
                    wrtr.write(Long.toString(referexpage.getSection().getPage()));
                }
                wrtr.write(FIELD_DELIMITER);

                // CHAPTER_TITLE - Only if this page is part of a Chapter/Downloadable "CHUNK"
                if(referexpage.getChapter() != null) {
                    wrtr.write("\"" + referexpage.getChapter().getTitle() + "\"");
                }
                wrtr.write(FIELD_DELIMITER);
                
                // CHAPTER_START - Only if this page is part of a Chapter/Downloadable "CHUNK"
                if(referexpage.getChapter() != null) {
                    wrtr.write(Long.toString(referexpage.getChapter().getPage()));
                }
                wrtr.write(FIELD_DELIMITER);

                // PAGE_BYTES
                wrtr.write(Long.toString(page_size));
                wrtr.write(FIELD_DELIMITER);

                // PAGE_TEXT
                wrtr.write(referexpage.getTextFilePath());
                wrtr.write(FIELD_DELIMITER);
                
                // PAGE_TOTAL
                wrtr.write(String.valueOf(pdffile.getPageCount()));
                wrtr.write(FIELD_DELIMITER);
                
                // PAGE_KEYWORDS
                wrtr.write(FIELD_DELIMITER);
                
                wrtr.write(FIELD_DELIMITER); // PP
                wrtr.write(FIELD_DELIMITER); // YR
                wrtr.write(FIELD_DELIMITER); // TI
                wrtr.write(FIELD_DELIMITER); // AUS
                wrtr.write(FIELD_DELIMITER); // CVS
                wrtr.write(FIELD_DELIMITER); // AF
                wrtr.write(FIELD_DELIMITER); // PN
                wrtr.write(FIELD_DELIMITER); // NT
                wrtr.write(FIELD_DELIMITER); // ST
                wrtr.write(FIELD_DELIMITER); // SP
                wrtr.write(FIELD_DELIMITER); // ISS
                wrtr.write(FIELD_DELIMITER); // VO
                wrtr.write(FIELD_DELIMITER); // AB
                wrtr.write(FIELD_DELIMITER); // SUB

                //PDFPP
                wrtr.write(String.valueOf(pdffile.getPageCount()));
               
                
                wrtr.write(System.getProperty("line.separator"));
            } catch (IOException e) {
                // TODO: handle exception
            }
        
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
