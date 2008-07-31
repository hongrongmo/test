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
  private ArchiveMapper mapper;

  public BookSeriesCreator() {
    mapper = new BookSeriesArchiveMapper();
  }
  
  public boolean process(String isbn) throws IOException {
    
    boolean result = false;
    String xmlpath = mapper.getArchviePath(isbn);

    File xmlFile = new File(xmlpath + FILE_SEP + "issue.xml");
    Issue anissue = IssueLoader.getIssue(xmlFile);
    HashMap bookInfo = getBookInfo(isbn);
    String bookpdf = WHOLE_PDFS + isbn + ".pdf";

    if (anissue != null) {
      String issn = mapper.cleanIdentifier(anissue.getIssn());
      log.info(isbn + "/" + issn + ": " + xmlFile);


      List<IncludeItem> issuearticles = anissue.getIncludeItems();
      Iterator<IncludeItem> includeitems = issuearticles.iterator();

      if(isCreateToc()) {
        createToc(xmlFile, isbn);
      }
      
      if(isCreateWholePdf()) {
        pdfprocessor.createPDF(xmlFile, bookpdf, includeitems);
      }

      if(isCopyChapters())
      {
        // reset iterator
        includeitems = issuearticles.iterator();

        while (includeitems.hasNext()) {
          IncludeItem includeitem = includeitems.next();
          String foldername = mapper.cleanIdentifier(includeitem.getPii());

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

    if(isBurstWholePdf()) {
      File bookpdf_file = new File(bookpdf);
      if(bookpdf_file.exists()) {
        pdfprocessor.burstPDF(new File(bookpdf), BURST_AND_EXTRACTED + FILE_SEP + isbn);
      }
    }

    if(isStampWholePdf())
    {
      String stampedbookpdf = WHOLE_PDFS + isbn + "_stamped.pdf";
      File bookpdf_file = new File(bookpdf);
      if(bookpdf_file.exists()) {
        pdfprocessor.stampPDF(new File(bookpdf),
            new File(stampedbookpdf),
            bookInfo);
  
        if(new File(bookpdf).delete())
        {
          new File(stampedbookpdf).renameTo(new File(bookpdf));
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