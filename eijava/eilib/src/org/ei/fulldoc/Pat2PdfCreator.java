package org.ei.fulldoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;

/*
  See: http://tothink.com/pat2pdf/
*/
public class Pat2PdfCreator {
  private static Log log = LogFactory.getLog(Pat2PdfCreator.class);

  private static final String pat_Issued_Search_URL = "/netacgi/nph-Parser?patentnumber=";
  private static final String pat_Application_Search_URL = "/netacgi/nph-Parser?Sect1=PTO2&Sect2=HITOFF&u=%2Fnetahtml%2FPTO%2Fsearch-adv.html&r=0&p=1&f=S&l=50&d=PG01&Query=DN%2F";
  private static final String pat_Issued_Site_URL = "http://patft.uspto.gov";
  private static final String pat_Application_Site_URL = "http://appft1.uspto.gov";
  private static final String PAT2PDF_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; MS-RTC LM 8)";

  private HttpClient client = null;

  public void init() {
    createClient();
    log.info("java.io.tmpdir is set to: " + System.getProperty("java.io.tmpdir"));
  }

  private void createClient() {
    System.getProperties().setProperty("httpclient.useragent", Pat2PdfCreator.PAT2PDF_USER_AGENT);
    client = new HttpClient();
  }

  public static void main(String[] args) {
    Pat2PdfCreator me = new Pat2PdfCreator();
    me.init();
    me.createPatentPdf("6421675"); //20010000044
  }

  public String getPatentPdfFilename(String pat_no) {
    return System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Pat" + pat_no + ".pdf";
  }

  public boolean createPatentPdf(String pat_no) {

    boolean result = false;
    String response = null;
    String image_server = null;
    String img_docurl = null;

    if(pat_no == null)
    {
      log.error("Patent number cannot be null.");
      return result;
    }

    if(pat_no.length() == 7) {
      // patent

      log.info("...fetching search results");
      String pat_searchurl = Pat2PdfCreator.pat_Issued_Site_URL + Pat2PdfCreator.pat_Issued_Search_URL + pat_no;
      response = getUrlAsString(pat_searchurl);
      //String response = me.getFileAsString(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Srch" + pat_no + ".htm");
      //log.info(response);

      if(response != null && response.length() != 0)
      {
        // look for <META HTTP-EQUIV="REFRESH" CONTENT="1;URL=/....">
        // and get URL
        log.info("...parsing search results");
        int url_start = response.indexOf(";URL=");
        int url_end = response.indexOf("\"></HEAD>");
        if((url_start < 0) || (url_end < 0)) {
          log.error("No patent found. Pat No: " + pat_no);
          return false;
        }
        String pat_url = response.substring(url_start + 5, url_end);
        String pat_docurl = Pat2PdfCreator.pat_Issued_Site_URL + pat_url;

        log.info("...fetching patent page");
        response = getUrlAsString(pat_docurl);
        //response = me.getFileAsString(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+ "Pat" + pat_no + ".htm");
        //log.info(response);

        if(response != null && response.length() != 0)
        {
          log.info("...parsing patent image URL");
          url_start = response.indexOf("a href=http://patimg");
          url_end = response.indexOf(">",url_start);
          if((url_start < 0) || (url_end < 0)) {
            log.error("No patent image URL found in page. Pat No: " + pat_no);
            return false;
          }
          img_docurl = response.substring(url_start + 7, url_end);
          log.info(img_docurl);

          log.info("...parsing patent image server");
          url_start = img_docurl.indexOf("http://");
          url_end = img_docurl.indexOf("/.piw?",1);
          image_server = img_docurl.substring(url_start, url_end);
          log.info(image_server);
        }
      }
    }
    else if (pat_no.length() == 11) {
      // patent application

      log.info("...fetching search results");
      String pat_searchurl = Pat2PdfCreator.pat_Application_Site_URL + Pat2PdfCreator.pat_Application_Search_URL + pat_no;
      //String response = me.getFileAsString(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Srch" + pat_no + ".htm");
      response = getUrlAsString(pat_searchurl);
      //log.info(response);

      if(response != null && response.length() != 0)
      {
        log.info("...parsing search results");
        int url_start = response.indexOf("HREF=/netacgi/nph-Parser");
        int url_end = response.indexOf(">",url_start);
        String pat_url = response.substring(url_start + 5, url_end);
        if((url_start < 0) || (url_end < 0)) {
          log.error("No patent application found. Pat No: " + pat_no);
          return false;
        }
        String pat_docurl = Pat2PdfCreator.pat_Application_Site_URL + pat_url;
        log.info(pat_docurl);

        log.info("...fetching patent page");
        response = getUrlAsString(pat_docurl);
        //response = getFileAsString(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Pat" + pat_no + ".htm");
        //log.info(response);

        if(response != null && response.length() != 0)
        {
          log.info("...parsing patent image URL");
          url_start = response.indexOf("a href=http://aiw");
          url_end = response.indexOf(">",url_start);
          if((url_start < 0) || (url_end < 0)) {
            log.error("No patent image URL found in page. Pat No: " + pat_no);
            return false;
          }
          img_docurl = response.substring(url_start + 7, url_end);
          log.info(img_docurl);

          log.info("...parsing patent image server");
          url_start = img_docurl.indexOf("http://");
          url_end = img_docurl.indexOf("/.aiw?",1);
          image_server = img_docurl.substring(url_start, url_end);
          log.info(image_server);
        }
      }
    }
    else {
      log.error("Unkown patent number format. Patent No: " + pat_no);
    }

    // generic patent code starts here
    // to continue from here we need img_docurl and image_server
    if(img_docurl != null && image_server != null) {
      log.info("...fetching images page");
      response = getUrlAsString(img_docurl);
      //response = getFileAsString(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Img" + pat_no + ".htm");

      log.info("...parsing number of pages in patent");
      int pages_start = response.indexOf("-- NumPages=");
      int pages_end = response.indexOf(" --",pages_start);
      String num_pages = response.substring(pages_start + 12, pages_end);
      int page_count = 0;
      try {
        page_count = Integer.parseInt(num_pages);
      } catch(NumberFormatException ne) {
        log.error("Cannot parse page count! Patent No: " + pat_no);
        page_count = 0;
      }
      log.info("[" + page_count + "]");

      log.info("...parsing path to first pages of patent");
      int url_start = response.indexOf("<embed src=\"/.DImg?");
      int url_end = response.indexOf("\" width",url_start);
      if((url_start < 0) || (url_end < 0)) {
        log.error("No patent TIF found in page. Pat No: " + pat_no);
        return result;
      }
      String tiff_docurl = response.substring(url_start + 12, url_end);
      log.info(tiff_docurl);

      if(page_count != 0)
      {
        log.info("...fetching patent image pages");
        List tiffs = new ArrayList();
        for(int tiff_index=1; tiff_index <= page_count; tiff_index++) {
          String tiff_imgurl = image_server + tiff_docurl.replaceAll("PageNum=\\d+", "PageNum=" + tiff_index);
          String tiff_file_path = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Pat" + pat_no + "Page" + tiff_index + ".tif";
          log.info(tiff_imgurl + ", " + tiff_file_path);

          if(saveUrlAsFile(tiff_imgurl, new File(tiff_file_path))) {
            tiffs.add(tiff_file_path);
          }
        }

        log.info("...combining patent image pages to pdf");
        if(tiffs.size() != 0) {
          try {
            String pdfpath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Pat" + pat_no + ".pdf";
            combineTif2Pdf((String[]) tiffs.toArray(new String[0]), new File(pdfpath));
            result = true;
          } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error(e);
          }
        }
      }
    }
    return result;
  }

  private String getUrlAsString(String url) {
    StringBuffer response = new StringBuffer();

    GetMethod get = new GetMethod(url);
    get.setFollowRedirects(false);

    try {
      client.executeMethod(get);
      if(get.getStatusCode() == HttpStatus.SC_OK)
      {
        InputStream in = get.getResponseBodyAsStream();
        BufferedReader inbuf = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while((line = inbuf.readLine()) != null) {
          response.append(line);
          //log.info(line);
        }
        inbuf.close();
      }
    } catch (Exception e) {
      log.error(e);
    } finally {
      get.releaseConnection();
    }
    return response.toString();
  }

  private boolean saveUrlAsFile(String url, File file) {
    boolean result = false;

    GetMethod get = new GetMethod(url);
    get.setFollowRedirects(false);

    FileOutputStream out = null;
    InputStream in = null;

    try {
      client.executeMethod(get);
      if(get.getStatusCode() == HttpStatus.SC_OK)
      {
        in = get.getResponseBodyAsStream();
        out = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count = -1;
        while ((count = in.read(buffer)) != -1) {
          out.write(buffer, 0, count);
        }
        out.flush();
        result = true;
      }
    } catch (Exception e) {
      log.error(e);
      result = false;
    } finally {
      if(in != null) {
        try {
          in.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      if(out != null) {
        try {
          out.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      get.releaseConnection();
    }
    return result;
  }

  public boolean combineTif2Pdf(String[] tiffs, File pdffile) throws Exception {
    boolean result = false;

    if((tiffs != null) && tiffs.length > 0)
    {
      RandomAccessFileOrArray tiffile;
      Document document = null;
      try {
        // open first file to get image size and set document size
        tiffile = new RandomAccessFileOrArray(tiffs[0]);
        Image img = TiffImage.getTiffImage(tiffile, 1);
        document = new Document(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdffile));
        document.open();
        PdfContentByte cb = writer.getDirectContent();

        // add first image to document here to avoid opening it again later
        document.newPage();
        img.setAbsolutePosition(0, 0);
        cb.addImage(img);
        tiffile.close();

        int count = tiffs.length;
        // start loop at second position in array
        for (int file_index = 1; file_index < count; file_index++) {
          tiffile = new RandomAccessFileOrArray(tiffs[file_index]);
          img = TiffImage.getTiffImage(tiffile, 1);
          document.newPage();
          img.setAbsolutePosition(0, 0);
          cb.addImage(img);
          tiffile.close();
        }
        result = true;
      } catch (IOException e) {
        log.error(e);
      } catch (DocumentException e) {
        log.error(e);
      }
      finally {
        if(document != null) {
          document.close();
        }
      }
    }
    if(!result) {
      pdffile.delete();
    }
    return result;
  }

  // Helper method to read in 'cached' http responses
  // to limit the traffic to USPTO
  // during testing
  private String getFileAsString(String file) {
    StringBuffer response = new StringBuffer();
    BufferedReader rdr  = null;
    try {
     rdr = new BufferedReader(new FileReader(file));
     if(rdr != null)
      {
        while(rdr.ready())
        {
          response.append(rdr.readLine());
        }
      }
      else
      {
        log.error("Response is null");
      }
    }
    catch(IOException e) {
      log.error(e);
    }
    finally {
      try {
        if(rdr != null) {
          rdr.close();
        }
      }
      catch(IOException e) {
        log.error(e);
      }
    }
    return response.toString();
  }

}