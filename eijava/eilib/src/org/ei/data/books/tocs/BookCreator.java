package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class BookCreator extends ReferexBaseProcessor  {

  private static final File xsltFile = new File("xsl\\BookTOC.xsl");

  public boolean process(String xmlpath) throws IOException {

    boolean result = false;
    File xmlFile = new File(xmlpath + FILE_SEP + "main.xml");
    String isbn = new File(xmlFile.getParent()).getName();
    if (isbn != null) {
      if (isbn.length() == 10) {
        isbn = convertToIsbn13(isbn);
      }
      if (checkIsbn(isbn)) {
        log.info(isbn + ": " + xmlFile);
        
        HashMap bookInfo = getBookInfo(isbn);
        
        List<IncludeItem> includeitems = IssueLoader.getIssue(xmlFile).getIncludeItems();
        Iterator<IncludeItem> piis = includeitems.iterator();

        String bookpdf = WHOLE_PDFS + isbn + ".pdf";
        String stampedbookpdf = WHOLE_PDFS + isbn + "_stamped.pdf";

        if(isCheckArchive()) {
          checkBookArchives(xmlFile, isbn);
        }
        
        if(isCreateToc()) {
          createToc(xmlFile, isbn);
        }
        if(isCreateWholePdf()) {
          pdfprocessor.createPDF(xmlFile, bookpdf, piis);
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
          piis = includeitems.iterator();

          while (piis.hasNext()) {
            IncludeItem pii = piis.next();
            String foldername = pii.getPii().replaceAll("\\p{Punct}", "");
  
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
          }
        }
        result = true;
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
  
  private boolean checkBookArchives(File xmlFile, String isbn) {
    boolean flagged = false;

    List<IncludeItem> includeitems = IssueLoader.getIssue(xmlFile).getIncludeItems();
    Iterator<IncludeItem> piis = includeitems.iterator();
    int mainxmlpagetotal = 0;
    int pdfpagetotal = 0;
    while(piis.hasNext()) {
      IncludeItem item = piis.next();
      List<PageRange> pageRanges = item.getPageRanges();
      Iterator<PageRange> ranges = pageRanges.iterator();
      int xmlpages = 0;
      while(ranges.hasNext())
      {
        xmlpages += ranges.next().length(); 
        mainxmlpagetotal += xmlpages;
      }
      int pdfpages = 0;
      try {
        pdfpages = pdfprocessor.countPages(xmlFile, item);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      pdfpagetotal += pdfpages;
      if(pdfpages != xmlpages) {
        log.error(isbn + " " + item.getPii() + " main.xml length: " + xmlpages + " Chapter main.pdf pages " + pdfpages);
        flagged = true;
      }
    }
    if (flagged) {
      log.error(isbn + " " + " main.xml total: " + mainxmlpagetotal + ". Chapter main.pdf totals: " + pdfpagetotal);
    }
    
    log.info(isbn + ": passes checks successfully.");
    return flagged;
  }
  

}