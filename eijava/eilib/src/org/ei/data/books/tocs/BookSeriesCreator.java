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

  public BookSeriesCreator(ArchiveMapper archivemapper) {
    mapper = archivemapper;
  }

  public boolean process(String isbn) throws IOException {

    boolean result = false;
    String xmlpath = mapper.getArchviePath(isbn);
    System.out.println("ISBN xmlpath= "+xmlpath);
    if(xmlpath == null) {
      return false;
    }
    File xmlFile = new File(xmlpath + FILE_SEP + "issue.xml");
    System.out.println("PATH10= "+xmlpath + FILE_SEP);
    Issue anissue = IssueLoader.getIssue(xmlFile);
    System.out.println("ISBN= "+anissue.getIsbn()+" ISBN2= "+isbn);
    HashMap bookInfo = getBookInfo(isbn);
    String bookpdf = getWhole_pdfs() + isbn + ".pdf";
    System.out.println("Whole_pdfs= "+bookpdf);

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
        File destinationfolder = new File(getBurst_extract() + isbn);
        if(!destinationfolder.exists()) {
          destinationfolder.mkdir();
        }
        while (includeitems.hasNext()) {
          IncludeItem includeitem = includeitems.next();
          String foldername = mapper.cleanIdentifier(includeitem.getPii());

          pdfprocessor.copyChapter(xmlFile, isbn, foldername, getBurst_extract());

          String chapterpdf = getBurst_extract() + isbn + FILE_SEP + foldername + ".pdf";
          String stampedchapterpdf = getBurst_extract() + isbn + FILE_SEP + foldername + "_stamped.pdf";
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
        File burstdir = new File(getPages() + FILE_SEP + isbn);
        if(!burstdir.exists()) {
          burstdir.mkdir();
        }
        if(burstdir.isDirectory())
        {
          pdfprocessor.burstPDF(new File(bookpdf), getPages() + FILE_SEP + isbn);
        }
      }
    }
    if(isStampWholePdf())
    {
      String stampedbookpdf = getWhole_pdfs() + isbn + "_stamped.pdf";
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
        File burst = new File(getBurst_extract() + isbn);
        if(!burst.exists()) {
          burst.mkdir();
        }
        if(burst.isDirectory())
        {
          Result tresult = new StreamResult(new FileOutputStream(getBurst_extract() + isbn + FILE_SEP + isbn + "_toc.html"));
          log.info(getBurst_extract()+ isbn + FILE_SEP + isbn + "_toc.html");
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