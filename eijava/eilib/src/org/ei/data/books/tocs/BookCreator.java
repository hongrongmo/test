package org.ei.data.books.tocs;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.data.books.tools.PDF_FileInfo;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.SimpleBookmark;
import com.lowagie.text.xml.xmp.DublinCoreSchema;
import com.lowagie.text.xml.xmp.XmpArray;
import com.lowagie.text.xml.xmp.XmpSchema;
import com.lowagie.text.xml.xmp.XmpWriter;

public class BookCreator
{
  protected static Log log = LogFactory.getLog(BookCreator.class);
  private static List isbnList = new ArrayList();

  private static boolean checkIsbn(String isbn) {
    boolean result = false;
    if((isbn == null) || (!isbnList.isEmpty() && !isbnList.contains(isbn))) {
        log.debug("Unused ISBN: " + isbn);
    }
    else {
        log.debug("found isbn " + isbn);
        result = true;
    }    
    return result;
  }
  private static String convertToIsbn13(String isbn) {
    String newisbn;
    String isbnroot = PDF_FileInfo.BN13_PREFIX + isbn.substring(0, 9);
    newisbn = isbnroot + PDF_FileInfo.getISBN13CheckDigit(isbnroot);
    return newisbn;
  }

  public static void main(String args[])
		//throws Exception
	{

    try {
      BufferedReader rdr = new BufferedReader(new FileReader("isbnList.txt"));
      while (rdr.ready()) {
    	  String aline = rdr.readLine();
    	  isbnList.add(aline);
    	}
    	rdr.close();
    }
    catch(IOException ioe) {

    }
    
    isbnList = new ArrayList();
    isbnList.add("9780750680691");

    File[] archvies = BookTocTransform.getBookArchvieDirectoryList(BookTocTransform.ARCHIVE_ROOT);
    // skip loop - change arch < archvies.length
    for (int arch = 0; arch < 0; arch++) {
      String archive = archvies[arch].getName();
      log.info((arch) + ". archive: " + archive);
      File[] files = BookTocTransform.getFileList(BookTocTransform.ARCHIVE_ROOT + archive);
      for (int i = 0; i < files.length; i++) {
        File xmlFile;
        try {
          xmlFile = new File(files[i].getCanonicalPath() + BookTocTransform.FILE_SEP + "main.xml");
          String isbn =  new File(xmlFile.getParent()).getName();
          if(isbn != null) {
            if(isbn.length() == 10) {
              isbn = convertToIsbn13(isbn);
            }
            if(checkIsbn(isbn)) {
              log.info((i) + ". " + isbn + ": " + xmlFile);
  
              List<IncludeItem> includeitems = IncludeItemLoader.getIncludeItems(xmlFile);
              Iterator<IncludeItem> piis = includeitems.iterator();
              
              createPDF(xmlFile, isbn, piis);
              stampPDF(xmlFile, isbn);
            }
          }
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

        xmlFile = null;
      }
    }
    
    File[] seriesarchive = BookTocTransform.getBookSeriesFileList(BookTocTransform.ARCHIVE_ROOT);
    for(int seriesidx = 0; seriesidx < seriesarchive.length; seriesidx++) {
      log.debug((seriesidx) + ". series: " + seriesarchive[seriesidx]);
//      if(!"EVIBS02I".equalsIgnoreCase(seriesarchive[seriesidx].getName()))
//      {
//        continue;
//      }
      File[] files = BookTocTransform.getFileList(seriesarchive[seriesidx].getAbsolutePath());
      for (int i = 0; i < files.length; i++) {
        log.debug((i) + ". files: " + files[i]);
        try {
          File[] issues = BookTocTransform.getFileList(files[i].getCanonicalPath());
          for (int j = 0; j < issues.length; j++) {
            File xmlFile = new File(issues[j].getCanonicalPath() + BookTocTransform.FILE_SEP + "issue.xml");
            Issue anissue = IssueLoader.getIssue(xmlFile);
            if(anissue != null)
            {
              String issn = anissue.getIssn().replaceAll("\\p{Punct}","");
              List<IncludeItem> serialissues = IncludeItemLoader.getIncludeItems(xmlFile);
              Iterator<IncludeItem> includeitems = serialissues.iterator();
              String isbn = anissue.getIsbn().replaceAll("\\p{Punct}","");
              if(isbn != null) {
                if(isbn.length() == 10) {
                  isbn = convertToIsbn13(isbn);
                }
                log.info((j) + ". " + isbn + ": " + issues[j]);
    
                while(includeitems.hasNext()) {
                  IncludeItem includeitem = includeitems.next();
                  String pii = includeitem.getPii().replaceAll("\\p{Punct}", "");
                  String mainxmlfolder = pii.replace("S" + issn, "");
                  // article file is in folder which is article pii without the "S<issn>" as the prefix
                  File mainxmlFile = new File(issues[j].getCanonicalPath() + BookTocTransform.FILE_SEP + mainxmlfolder + BookTocTransform.FILE_SEP + "main.xml");
                  if(mainxmlFile.exists()) {
                    IncludeItem article = ArticleLoader.getIncludeItem(mainxmlFile);
                    if(article == null) {
                      log.info(mainxmlFile);
                    } else {
                      log.info("\t\t" + article.getTitle());
                    }
                  } else {
                    log.info("\t Cannot find " + mainxmlFile + "!");
                  }
                  
                } // while
              }
            }
          }
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    
  }
  
  private static void stampPDF(File xmlFile, String isbn) throws IOException {
    String isbnpdf = xmlFile.getCanonicalPath() + BookTocTransform.FILE_SEP + isbn + ".pdf";
    String stampedpdf = xmlFile.getCanonicalPath() + BookTocTransform.FILE_SEP + isbn + "_stamped.pdf";

    try {
      PdfReader reader = new PdfReader(isbnpdf);
      PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(stampedpdf));
      
      HashMap info = reader.getInfo();
      log.info(" Info " + info);
      info.put("Producer","Elsevier Engineering Information");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      XmpWriter xmp = new XmpWriter(baos);
      XmpSchema dc = new DublinCoreSchema();
      XmpArray subject = new XmpArray(XmpArray.UNORDERED);
      subject.add("Referex");
      dc.setProperty(DublinCoreSchema.SUBJECT, subject);
      xmp.addRdfDescription(dc);
      xmp.close();
      stamper.setXmpMetadata(baos.toByteArray());
      stamper.setMoreInfo(info);
      stamper.close();
      reader.close();
    } catch (DocumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  private static void createPDF(File xmlFile, String isbn, Iterator<IncludeItem> piis) throws IOException {
    // TODO Auto-generated method stub
    Document document = null;
    PdfCopy writer = null;
    int pageOffset = 0;
    List master = new ArrayList();
    boolean firstLoop = true;
    String isbnpdf = xmlFile.getCanonicalPath() + BookTocTransform.FILE_SEP + isbn + ".pdf";

    while(piis.hasNext()) {
      IncludeItem pii = piis.next();
      String foldername = pii.getPii().replaceAll("\\p{Punct}", "");

      String[] locs = {"FRONT", "BODY","REAR"};
      File floc = null;
      for(int l = 0; l < locs.length; l++) {
        floc = new File(xmlFile.getCanonicalPath() + BookTocTransform.FILE_SEP + locs[l] + BookTocTransform.FILE_SEP + foldername + BookTocTransform.FILE_SEP + "main.pdf"); 
        if(floc.exists()) {
          log.info(locs[l] + " file. " + floc.getAbsolutePath());
          break;
        }
      }

      PdfReader reader = new PdfReader(floc.getAbsolutePath());
      reader.consolidateNamedDestinations();
      // we retrieve the total number of pages
      int n = reader.getNumberOfPages();
      List bookmarks = SimpleBookmark.getBookmark(reader);
      if (bookmarks != null) {
          if (pageOffset != 0) {
              SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
          }
          master.addAll(bookmarks);
      }

      HashMap map = new HashMap();
      map.put("Title", pii.getTitle());
      map.put("Action", "GoTo");
      map.put("Page", (pageOffset + 1) + " Fit");
      master.add(map);

      
      pageOffset += n;

      if (firstLoop) {
        // step 1: creation of a document-object
        document = new Document(reader.getPageSizeWithRotation(1));
        // step 2: we create a writer that listens to the document
        try {
          writer = new PdfCopy(document, new FileOutputStream(isbnpdf));
        } catch (DocumentException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        // step 3: we open the document
        document.open();
        firstLoop = false;
      }
      
      PdfImportedPage page;
      for (int m = 0; m < n;) {
          ++m;
          page = writer.getImportedPage(reader, m);
          try {
            writer.addPage(page);
          } catch (BadPdfFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
      }
      reader.close();
    } // while 
    if (!master.isEmpty()) {
      writer.setOutlines(master);
    }
    // step 5: we close the document
    document.close();

  }
}