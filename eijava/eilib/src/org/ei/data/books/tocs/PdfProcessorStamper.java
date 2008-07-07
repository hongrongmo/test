package org.ei.data.books.tocs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

public abstract class PdfProcessorStamper extends ReferexBaseProcessor {

  public static final String WHOLE_PDFS = "V:\\EW\\whole_pdfs\\";


  public void stampPDF(File xmlFile, String isbn) throws IOException {
    String isbnpdf = WHOLE_PDFS + isbn + ".pdf";
    String stampedpdf = WHOLE_PDFS  + isbn + "_stamped.pdf";

    try {
      PdfReader reader = new PdfReader(isbnpdf);
      PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(stampedpdf));

      HashMap info = reader.getInfo();
      log.info(" Info " + info);
      info.put("Producer", "Elsevier Engineering Information");
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

  public void createPDF(File xmlFile, String isbn,
      Iterator<IncludeItem> piis) throws IOException {
    // TODO Auto-generated method stub
    Document document = null;
    PdfCopy writer = null;
    int pageOffset = 0;
    List master = new ArrayList();
    boolean firstLoop = true;
    String isbnpdf = WHOLE_PDFS + isbn + ".pdf"; 
    
    while (piis.hasNext()) {
      IncludeItem pii = piis.next();
      String foldername = pii.getPii().replaceAll("\\p{Punct}", "");

      String[] locs = { "FRONT", "BODY", "REAR" };
      File floc = null;
      for (int l = 0; l < locs.length; l++) {
        floc = new File(xmlFile.getParent() + FILE_SEP
            + locs[l] + FILE_SEP + foldername
            + FILE_SEP + "main.pdf");
        if (floc.exists()) {
          log.debug(locs[l] + " file. " + floc.getAbsolutePath());
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
      else 
      {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("Title", pii.getTitle());
        map.put("Action", "GoTo");
        map.put("Page", (pageOffset + 1) + " Fit");
        master.add(map);
      }
      
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
