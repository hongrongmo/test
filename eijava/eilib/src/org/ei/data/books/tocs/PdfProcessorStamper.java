package org.ei.data.books.tocs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SimpleBookmark;

public class PdfProcessorStamper  {

  public static final String FILE_SEP = System.getProperty("file.separator");

  protected static Log log = LogFactory.getLog(PdfProcessorStamper.class);

  public void stampPDF(File srcfile, File destfile, HashMap bookInfo) throws IOException {

    try {
      PdfReader reader = new PdfReader(srcfile.getAbsolutePath());
      PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(destfile));

      log.debug(" Stamping Book Information " + bookInfo);
      
      /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
      XmpWriter xmp = new XmpWriter(baos);
      XmpSchema dc = new DublinCoreSchema();
      XmpArray subject = new XmpArray(XmpArray.UNORDERED);
      subject.add("Referex");
      dc.setProperty(DublinCoreSchema.SUBJECT, subject);
      xmp.addRdfDescription(dc);
      xmp.close();
      stamper.setXmpMetadata(baos.toByteArray()); */

      stamper.setMoreInfo(bookInfo);
      stamper.close();
      reader.close();
    } catch (DocumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void burstPDF(File srcpdf, String destinationFolder)
  {
  
    try {
      PdfReader reader = new PdfReader(srcpdf.getAbsolutePath());
      // we retrieve the total number of pages
      int n = reader.getNumberOfPages();
      log.info("There are " + n + " pages in the original file.");
      Document document;
      int pagenumber;
      String filename;
      NumberFormat df = new DecimalFormat("0000");
      df.setMinimumIntegerDigits(4);

      for (int i = 0; i < n; i++) {
        pagenumber = i + 1;
        filename = df.format(pagenumber);

        // step 1: creation of a document-object
        document = new Document(reader.getPageSizeWithRotation(pagenumber));
        // step 2: we create a writer that listens to the document
        PdfWriter writer = PdfWriter.getInstance(document, 
            new FileOutputStream(new File(destinationFolder, "pg_" + filename + ".pdf")));
        // step 3: we open the document
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        PdfImportedPage page = writer.getImportedPage(reader, pagenumber);
        int rotation = reader.getPageRotation(pagenumber);
        if (rotation == 90 || rotation == 270) {
          cb.addTemplate(page, 0, -1f, 1f, 0, 0, reader.getPageSizeWithRotation(pagenumber).getHeight());
        }
        else {
          cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
        }
        // step 5: we close the document
        document.close();
      }
      reader.close();
    } catch(DocumentException e) {
      log.error(e);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      log.error(e);
    }
  }
  
  public void createPDF(File xmlFile, String isbnpdf, Iterator<IncludeItem> piis) throws IOException {
    // TODO Auto-generated method stub
    Document document = null;
    PdfCopy writer = null;
    int pageOffset = 0;
    List master = new ArrayList();
    boolean firstLoop = true;
    
    while (piis.hasNext()) {
      IncludeItem pii = piis.next();
      String foldername = pii.getPii().replaceAll("\\p{Punct}", "");

      File floc = getIncludeItem(xmlFile, foldername);

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
      writer.freeReader(reader);
    } // while
    if (!master.isEmpty()) {
      writer.setOutlines(master);
    }
    // step 5: we close the document
    document.close();

  }

  public void copyChapter(File xmlFile, String isbn,
      String foldername, String destinationFolder) throws IOException {

      File floc = getIncludeItem(xmlFile, foldername);
      copy(floc, new File(destinationFolder + isbn + FILE_SEP + foldername + ".pdf"));
  }
  
  public int countPages(File xmlFile, IncludeItem pii) throws IOException {
    
    String foldername = pii.getPii().replaceAll("\\p{Punct}", "");
    File floc = getIncludeItem(xmlFile, foldername);
    PdfReader reader = new PdfReader(floc.getAbsolutePath());
    // we retrieve the total number of pages
    int n = reader.getNumberOfPages();
    reader.close();

    return n;
  }  
 
  // Copies src file to dst file.
  // If the dst file does not exist, it is created
  private void copy(File src, File dst) throws IOException {
      InputStream in = new FileInputStream(src);
      OutputStream out = new FileOutputStream(dst);
  
      // Transfer bytes from in to out
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
      }
      in.close();
      out.close();
  }

  private File getIncludeItem(File xmlFile, String foldername) throws IOException {
    File floc = null;
    // book series
    if(foldername.startsWith("S"))
    {
      // article file is in folder which is article pii without the
      // "S<issn>" as the prefix so strip the first 9 characters
      foldername = foldername.substring(9);
      floc = new File(xmlFile.getParent()
        + FILE_SEP + foldername
        + FILE_SEP + "main.pdf");      
    }
    else
    {
      String[] locs = { "FRONT", "BODY", "REAR" };
      for (int l = 0; l < locs.length; l++) {
        floc = new File(xmlFile.getParent() + FILE_SEP
            + locs[l] + FILE_SEP + foldername
            + FILE_SEP + "main.pdf");
        if (floc.exists()) {
          log.debug(locs[l] + " file. " + floc.getAbsolutePath());
          break;
        }
        else {
          floc = null;
        }
      }
    }
    if(floc == null) {
      log.error("File missing. " + xmlFile.getParent() + " " + foldername);
    }
    else {
      log.debug(floc.getAbsolutePath());
    }
    return floc;
  }

}
