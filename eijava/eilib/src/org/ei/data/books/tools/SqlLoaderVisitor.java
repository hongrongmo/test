package org.ei.data.books.tools;

import java.io.IOException;
import java.io.Writer;

public class SqlLoaderVisitor extends BookVisitor {

    private Writer wrtr = null;
    final static String FIELD_DELIMITER = "\t";    
    
    public SqlLoaderVisitor(Writer awriter) {
        wrtr = awriter;
    }
    
    public void visit(PDF_FileInfo pdffile) {
        for (int curpage = 1; curpage <= pdffile.getPageCount(); curpage++) {

            PDF_Page referexpage = pdffile.getPage(curpage);
        
            long page_size = referexpage.getTextSizeBytes();
            try {
                wrtr.write("pag_" + pdffile.getIsbn().toLowerCase()+"_"+Long.toString(curpage));

                wrtr.write(FIELD_DELIMITER);
                wrtr.write(pdffile.getIsbn().toLowerCase());
                wrtr.write(FIELD_DELIMITER);
                wrtr.write(pdffile.getIsbn13().toLowerCase());
                wrtr.write(FIELD_DELIMITER);
                wrtr.write(Long.toString(curpage));
                wrtr.write(FIELD_DELIMITER);
                if(referexpage.getChapter() != null) {
                    wrtr.write(referexpage.getChapter().getTitle());
                }
                wrtr.write(FIELD_DELIMITER);
                if(referexpage.getChapter() != null) {
                    wrtr.write(Long.toString(referexpage.getChapter().getPage()));
                }
                wrtr.write(FIELD_DELIMITER);
                wrtr.write(Long.toString(page_size));
                wrtr.write(FIELD_DELIMITER);
                wrtr.write(referexpage.getTextFilePath());
                wrtr.write(FIELD_DELIMITER);
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
    
}
