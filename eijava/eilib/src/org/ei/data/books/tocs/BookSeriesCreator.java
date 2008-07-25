package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class BookSeriesCreator extends ReferexBaseProcessor {
  private static final File xsltFile = new File("xsl\\BooKSeriesTOC.xsl");

  public boolean process(String xmlpath) throws IOException {
    
    boolean result = false;
    File xmlFile = new File(xmlpath + FILE_SEP + "issue.xml");
    Issue anissue = IssueLoader.getIssue(xmlFile);
    if (anissue != null) {
      String issn = cleanIdentifier(anissue.getIssn());
      String isbn = cleanIdentifier(anissue.getIsbn());
      if ((isbn != null)) {
        if (isbn.length() == 10) {
          isbn = convertToIsbn13(isbn);
        }
        if(checkIsbn(isbn)) {
          log.info(isbn + "/" + issn + ": " + xmlFile);

          HashMap bookInfo = getBookInfo(isbn);

          List<IncludeItem> issuearticles = anissue.getIncludeItems();
          Iterator<IncludeItem> includeitems = issuearticles.iterator();
  
          String bookpdf = WHOLE_PDFS + isbn + ".pdf";
          String stampedbookpdf = WHOLE_PDFS + isbn + "_stamped.pdf";

          if(isCreateToc()) {
            createToc(xmlFile, isbn);
          }
          
          if(isCreateWholePdf()) {
            pdfprocessor.createPDF(xmlFile, bookpdf, includeitems);
          }
          if(isBurstWholePdf()) {
            pdfprocessor.burstPDF(new File(bookpdf), BURST_AND_EXTRACTED + FILE_SEP + isbn);
          }

          if(isStampWholePdf())
          {
            pdfprocessor.stampPDF(new File(bookpdf),
                new File(stampedbookpdf),
                bookInfo);
  
            if(new File(bookpdf).delete())
            {
              new File(stampedbookpdf).renameTo(new File(bookpdf));
            }
          }

          if(isCopyChapters())
          {
            // reset iterator
            includeitems = issuearticles.iterator();
  
            while (includeitems.hasNext()) {
              IncludeItem includeitem = includeitems.next();
              String foldername = cleanIdentifier(includeitem.getPii());
  
              pdfprocessor.copyChapter(xmlFile, isbn, foldername, BURST_AND_EXTRACTED);
    
              String chapterpdf = BURST_AND_EXTRACTED + isbn + FILE_SEP + foldername + ".pdf";
              String stampedchapterpdf = BURST_AND_EXTRACTED + isbn + FILE_SEP + foldername + "_stamped.pdf";
              pdfprocessor.stampPDF(new File(chapterpdf),
                  new File(stampedchapterpdf),
                  bookInfo);
    
                if(new File(chapterpdf).delete())
                {
                  new File(stampedchapterpdf).renameTo(new File(chapterpdf));
                }
            } // while
          }
          result = true;
        }
      }
    }
    return result;
  }
  private boolean createToc(File xmlFile, String isbn) {
    boolean result = false;
  
    Issue iss = IssueLoader.getIssue(xmlFile);
    if(iss != null) {    
      try {
        File burst = new File(BURST_AND_EXTRACTED + isbn); 
        if(!burst.exists()) {
          burst.mkdir();
        }
        if(burst.isDirectory()) 
        {
          Result tresult = new StreamResult(new FileOutputStream(BURST_AND_EXTRACTED + isbn + FILE_SEP + isbn + "_toc.html"));
          log.info(BURST_AND_EXTRACTED + isbn + FILE_SEP + isbn + "_toc.html");              
          if(toctransformer.runTransform(xmlFile,xsltFile,tresult)) {
            result = true;
          }
        }
      } catch(IOException e) {
          log.error(e);
      }
    }    
    return result;
  }  
  
}