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
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.SimpleBookmark;

public class PdfProcessorStamper  {

  public static final String FILE_SEP = System.getProperty("file.separator");

  protected static Log log = LogFactory.getLog(PdfProcessorStamper.class);

  public void stampPDF(File srcfile, File destfile, HashMap bookInfo) {

    if(!srcfile.exists())
    {
      log.error("File does niot exist! " + srcfile);
      return;
    }

    PdfReader reader = null;
    OutputStream stampedfile = null;
    PdfStamper stamper = null;

    try {
      stampedfile = new FileOutputStream(destfile);

      //reader = new PdfReader(srcfile.getAbsolutePath());
      reader = new PdfReader(new RandomAccessFileOrArray(srcfile.getAbsolutePath()), null);

      stamper = new PdfStamper(reader, stampedfile);

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
      reader.close();
      stamper.close();
      stampedfile.close();

    } catch (DocumentException e) {
      log.error(e);
    } catch (IOException e) {
      log.error(e);
    }
    finally {
    }
  }

  public void burstPDF(File srcpdf, String destinationFolder)
  {
    PdfReader reader = null;
    Document document = null;
    try {
      log.debug("Counting...");
      reader = new PdfReader(new RandomAccessFileOrArray(srcpdf.getAbsolutePath()), null);
      // we retrieve the total number of pages
      int n = reader.getNumberOfPages();
      log.info("There are " + n + " pages in the original file.");
      int pagenumber;
      String filename;
      NumberFormat df = new DecimalFormat("0000");
      df.setMinimumIntegerDigits(4);

      for (int i = 0; i < n; i++) {
        pagenumber = i + 1;
        filename = df.format(pagenumber);

        // step 1: creation of a document-object
        document = new Document(reader.getPageSizeWithRotation(pagenumber));
        //document = new Document();
        // step 2: we create a writer that listens to the document
        PdfWriter writer = PdfWriter.getInstance(document,
            new FileOutputStream(new File(destinationFolder, "pg_" + filename + ".pdf")));
        // step 3: we open the document
        document.open();

        PdfContentByte cb = writer.getDirectContent();
        PdfImportedPage page = writer.getImportedPage(reader, pagenumber);
        int rotation = reader.getPageRotation(pagenumber);
        if (rotation == 90 || rotation == 270) {
          //log.info("rotated");
          cb.addTemplate(page, 0, -1f, 1f, 0, 0, reader.getPageSizeWithRotation(pagenumber).getHeight());
        }
        else {
          cb.addTemplate(page, 1f, 0, 0, 1f, 75, 100);
        }
        // step 5: we close the document
        document.close();
        writer.close();
      }
    } catch(DocumentException e) {
      log.error(e);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      log.error(e);
    }
    finally {
      if(document != null && document.isOpen()) {
        document.close();
      }

      if(reader!=null) {
        reader.close();
      }
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

      //PdfReader reader = new PdfReader(floc.getAbsolutePath());
      PdfReader reader = new PdfReader(new RandomAccessFileOrArray(floc.getAbsolutePath()), null);
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
      String piiname, String destinationFolder) throws IOException {

      File floc = getIncludeItem(xmlFile, piiname);
      if(!floc.exists()) {
        log.error("Missing chapter " + floc);
      }
      try {
        copy(floc, new File(destinationFolder + isbn + FILE_SEP + piiname + ".pdf"));
      }
      catch(java.io.FileNotFoundException e) {
        log.error(e);
      }
  }

  public int countPages(File xmlFile, IncludeItem pii) throws IOException {

    String foldername = pii.getPii().replaceAll("\\p{Punct}", "");
    File floc = getIncludeItem(xmlFile, foldername);
//    PdfReader reader = new PdfReader(floc.getAbsolutePath());
    PdfReader reader = new PdfReader(new RandomAccessFileOrArray(floc.getAbsolutePath()), null);
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
    System.out.println("foldername= "+foldername);
    if(foldername.startsWith("A"))
    {
      // article file is in folder which is article pii without the
      // "S<issn>" as the prefix so strip the first 9 characters
      foldername = foldername.substring(13);
      foldername = foldername.substring(0,1)+"."+foldername.substring(1);
      floc = new File(xmlFile.getParent()
        + FILE_SEP + foldername
        + FILE_SEP + "main.pdf");
        System.out.println("FILEPATH= "+floc.getAbsolutePath());
    }
    else if( foldername.startsWith("S"))
	{
	  // article file is in folder which is article pii without the
	  // "S<issn>" as the prefix so strip the first 9 characters
	  foldername = foldername.substring(9);
	  floc = new File(xmlFile.getParent()
		+ FILE_SEP + foldername
		+ FILE_SEP + "main.pdf");
		System.out.println("FILEPATH1= "+floc.getAbsolutePath());
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
