package org.ei.fulldoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;

import com.lowagie.text.Document;
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
    private static Logger log4j = Logger.getLogger(Pat2PdfCreator.class);

    private static final String USPTO_PAT_ISSUED_SITE_URL = "http://patft.uspto.gov";
    private static final String USPTO_PAT_ISSUED_SEARCH_URL = "/netacgi/nph-Parser?patentnumber=";

    private static final String USPTO_PAT_APPLICATION_SITE_URL = "http://appft.uspto.gov";
    private static final String USPTO_PAT_APPLICATION_SEARCH_URL = "/netacgi/nph-Parser?Sect1=PTO1&Sect2=HITOFF&d=PG01&p=1&u=%2Fnetahtml%2FPTO%2Fsrchnum.html&r=0&f=S&l=50&TERM1=";

    private static final String PAT2PDF_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; MS-RTC LM 8)";

    private HttpClient client = null;

    public void init() {
        createClient();
        log4j.info("java.io.tmpdir is set to: " + System.getProperty("java.io.tmpdir"));
    }

    private void createClient() {
        System.getProperties().setProperty("httpclient.useragent", Pat2PdfCreator.PAT2PDF_USER_AGENT);
        client = new HttpClient();
    }

    public String getPatentPdfFilename(String pat_no) {
        return System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "Pat" + pat_no + ".pdf";
    }

    /**
     * Call the USPTO patent search and return the response. Patents can be of 2 types: Application Issued. Issued patents will be of length 7 and Application
     * will be of length 11.
     * 
     * This method attempts to return the URL to the appropriate USPTO patent page. It may also return the customer to USPTO search page if response does not
     * contain necessary info.
     * 
     * @param pat_no
     * @return
     * @throws ServiceException
     */
    public String getPatApplicationPDFUrl(String pat_no) throws ServiceException {
        //
        //
        String response = null;
        Matcher matcher = null;
        Pattern pattern = null;

        if (pat_no.length() <= 7) {

            log4j.info("Search Issued patents for patent number: " + pat_no);

            String pat_searchurl = Pat2PdfCreator.USPTO_PAT_ISSUED_SITE_URL + Pat2PdfCreator.USPTO_PAT_ISSUED_SEARCH_URL + pat_no;
            response = getUrlAsString(pat_searchurl);
            if (GenericValidator.isBlankOrNull(response)) {
                // No response - should not happen but if it does, throw Exception
                throw new ServiceException(SystemErrorCodes.PAT_PDF_ERROR, "No response looking for USPTO Issued patents META tag!");
            }
            // First search will return META tag with redirect info
            log4j.info("Looking for META tag...");
            pattern = Pattern.compile("CONTENT=\\\"[\\d];URL=(.*?)\\\">", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(response);
            if (matcher.find()) {
                // Extract the search URL and get *that* response
                response = getUrlAsString(Pat2PdfCreator.USPTO_PAT_ISSUED_SITE_URL + matcher.group(1));
                if (GenericValidator.isBlankOrNull(response)) {
                    throw new ServiceException(SystemErrorCodes.PAT_PDF_ERROR, "No response from USPTO Issued patents search!");
                }
                // Look for the PDF image link
                pattern = Pattern.compile("<a\\s+href=[\\\"]{0,1}(http://pdfpiw.uspto.gov/.piw.*?)[\\\"]{0,1}\\s*>", Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(response);
                if (matcher.find()) {
                    return matcher.group(1);
                } else {
                    return pat_searchurl;
                }
            }

        } else if (pat_no.length() == 11) {

            log4j.info("Search Application patents for patent number: " + pat_no);

            String pat_searchurl = Pat2PdfCreator.USPTO_PAT_APPLICATION_SITE_URL + Pat2PdfCreator.USPTO_PAT_APPLICATION_SEARCH_URL + pat_no;
            response = getUrlAsString(pat_searchurl);
            if (GenericValidator.isBlankOrNull(response)) {
                // No response - should not happen but if it does, throw Exception
                throw new ServiceException(SystemErrorCodes.PAT_PDF_ERROR, "No response for USPTO Application patents search, patent num: " + pat_no + "!");
            }

            log4j.info("Looking for Patent link...");
            pattern = Pattern.compile("a\\s+href=[\\\"]{0,1}(/netacgi/nph-Parser.*?)[\\\"]{0,1}\\s*>", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(response);
            if (matcher.find()) {
                response = getUrlAsString(Pat2PdfCreator.USPTO_PAT_APPLICATION_SITE_URL + matcher.group(1));
                if (GenericValidator.isBlankOrNull(response)) {
                    throw new ServiceException(SystemErrorCodes.PAT_PDF_ERROR, "No response from USPTO Issued patents search!");
                }
                log4j.info("Looking for PDF link...");
                pattern = Pattern.compile("<a\\s+href=[\\\"]{0,1}(http://pdfaiw.uspto.gov/.aiw.*?)[\\\"]{0,1}\\s*>", Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(response);
                if (matcher.find()) {
                    return matcher.group(1);
                } else {
                    return pat_searchurl;
                }
            } else {
                return pat_searchurl;
            }

        } else {
            log4j.error("Unkown patent number format. Patent No: " + pat_no);
        }

        return null;

    }

    /**
     * Create a PDF from the USPTO patent search.
     * 
     * @param pat_no
     * @param baos
     * @return
     */
    @Deprecated
    public boolean createPatentPdf(String pat_no, OutputStream baos) {

        boolean result = false;
        String response = null;
        String image_server = null;
        String img_docurl = null;

        if (pat_no == null) {
            log4j.error("Patent number cannot be null.");
            return result;
        }

        if (pat_no.length() == 7) {
            // patent

            log4j.debug("...fetching search results");
            String pat_searchurl = Pat2PdfCreator.USPTO_PAT_ISSUED_SITE_URL + Pat2PdfCreator.USPTO_PAT_ISSUED_SEARCH_URL + pat_no;
            response = getUrlAsString(pat_searchurl);
            // String response =
            // me.getFileAsString(System.getProperty("java.io.tmpdir") +
            // System.getProperty("file.separator") + "Srch" + pat_no + ".htm");
            // log4j.info(response);

            if (response != null && response.length() != 0) {
                // look for <META HTTP-EQUIV="REFRESH" CONTENT="1;URL=/....">
                // and get URL
                log4j.debug("...parsing search results");
                int url_start = response.indexOf(";URL=");
                int url_end = response.indexOf("\"></HEAD>");
                if ((url_start < 0) || (url_end < 0)) {
                    log4j.error("No patent found. Pat No: " + pat_no);
                    return false;
                }
                String pat_url = response.substring(url_start + 5, url_end);
                String pat_docurl = Pat2PdfCreator.USPTO_PAT_ISSUED_SITE_URL + pat_url;

                log4j.debug("...fetching patent page");
                response = getUrlAsString(pat_docurl);
                // response =
                // me.getFileAsString(System.getProperty("java.io.tmpdir") +
                // System.getProperty("file.separator")+ "Pat" + pat_no +
                // ".htm");
                // log4j.info(response);

                if (response != null && response.length() != 0) {
                    log4j.debug("...parsing patent image URL");
                    url_start = response.indexOf("a href=http://patimg");
                    url_end = response.indexOf(">", url_start);
                    if ((url_start < 0) || (url_end < 0)) {
                        log4j.error("No patent image URL found in page. Pat No: " + pat_no);
                        return false;
                    }
                    img_docurl = response.substring(url_start + 7, url_end);
                    // log4j.info(img_docurl);

                    log4j.debug("...parsing patent image server");
                    url_start = img_docurl.indexOf("http://");
                    url_end = img_docurl.indexOf("/.piw?", 1);
                    image_server = img_docurl.substring(url_start, url_end);
                    // log4j.info(image_server);
                }
            }
        } else if (pat_no.length() == 11) {
            // patent application

            log4j.debug("...fetching search results");
            String pat_searchurl = Pat2PdfCreator.USPTO_PAT_APPLICATION_SITE_URL + Pat2PdfCreator.USPTO_PAT_APPLICATION_SEARCH_URL + pat_no;
            // String response =
            // me.getFileAsString(System.getProperty("java.io.tmpdir") +
            // System.getProperty("file.separator") + "Srch" + pat_no + ".htm");
            response = getUrlAsString(pat_searchurl);
            // log4j.info(response);

            if (response != null && response.length() != 0) {
                log4j.debug("...parsing search results");
                int url_start = response.indexOf("HREF=/netacgi/nph-Parser");
                int url_end = response.indexOf(">", url_start);
                String pat_url = response.substring(url_start + 5, url_end);
                if ((url_start < 0) || (url_end < 0)) {
                    log4j.error("No patent application found. Pat No: " + pat_no);
                    return false;
                }
                String pat_docurl = Pat2PdfCreator.USPTO_PAT_APPLICATION_SITE_URL + pat_url;
                // log4j.info(pat_docurl);

                log4j.debug("...fetching patent page");
                response = getUrlAsString(pat_docurl);
                // response =
                // getFileAsString(System.getProperty("java.io.tmpdir") +
                // System.getProperty("file.separator") + "Pat" + pat_no +
                // ".htm");
                // log4j.info(response);

                if (response != null && response.length() != 0) {
                    log4j.debug("...parsing patent image URL");
                    url_start = response.indexOf("a href=http://aiw");
                    url_end = response.indexOf(">", url_start);
                    if ((url_start < 0) || (url_end < 0)) {
                        log4j.error("No patent image URL found in page. Pat No: " + pat_no);
                        return false;
                    }
                    img_docurl = response.substring(url_start + 7, url_end);
                    // log4j.info(img_docurl);

                    log4j.debug("...parsing patent image server");
                    url_start = img_docurl.indexOf("http://");
                    url_end = img_docurl.indexOf("/.aiw?", 1);
                    image_server = img_docurl.substring(url_start, url_end);
                    // log4j.info(image_server);
                }
            }
        } else {
            log4j.error("Unkown patent number format. Patent No: " + pat_no);
        }

        // generic patent code starts here
        // to continue from here we need img_docurl and image_server
        if (img_docurl != null && image_server != null) {
            log4j.debug("...fetching images page");
            response = getUrlAsString(img_docurl);
            // response = getFileAsString(System.getProperty("java.io.tmpdir") +
            // System.getProperty("file.separator") + "Img" + pat_no + ".htm");

            log4j.debug("...parsing number of pages in patent");
            int pages_start = response.indexOf("-- NumPages=");
            int pages_end = response.indexOf(" --", pages_start);
            String num_pages = response.substring(pages_start + 12, pages_end);
            int page_count = 0;
            try {
                page_count = Integer.parseInt(num_pages);
            } catch (NumberFormatException ne) {
                log4j.error("Cannot parse page count! Patent No: " + pat_no);
                page_count = 0;
            }
            log4j.debug("[" + page_count + "]");

            log4j.debug("...parsing path to first pages of patent");
            int url_start = response.indexOf("<embed src=\"/.DImg?");
            int url_end = response.indexOf("\" width", url_start);
            if ((url_start < 0) || (url_end < 0)) {
                log4j.error("No patent TIF found in page. Pat No: " + pat_no);
                return result;
            }
            String tiff_docurl = response.substring(url_start + 12, url_end);
            log4j.info(tiff_docurl);

            if (page_count != 0) {
                List<String> tiffs = new ArrayList<String>();
                try {
                    log4j.debug("...fetching patent image pages");
                    for (int tiff_index = 1; tiff_index <= page_count; tiff_index++) {
                        String tif_imgurl = image_server + tiff_docurl.replaceAll("PageNum=\\d+", "PageNum=" + tiff_index);
                        File tif_file = File.createTempFile("pat", ".tif");
                        log4j.info(tif_imgurl + ", " + tif_file.getPath());
                        if (saveUrlAsFile(tif_imgurl, tif_file)) {
                            tiffs.add(tif_file.getPath());
                        }
                    }
                } catch (IOException e) {
                    result = false;
                    tiffs.clear();
                }

                log4j.debug("...combining patent image pages to pdf");
                if (tiffs.size() != 0) {
                    try {
                        combineTif2Pdf((String[]) tiffs.toArray(new String[0]), baos);
                        result = true;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        log4j.error(e);
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

        BufferedReader inbuf = null;
        try {
            client.executeMethod(get);
            if (get.getStatusCode() == HttpStatus.SC_OK) {
                InputStream in = get.getResponseBodyAsStream();
                inbuf = new BufferedReader(new InputStreamReader(in));
                String line = null;
                while ((line = inbuf.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (Exception e) {
            log4j.error(e);
        } finally {
            if (inbuf != null) {
                try {
                    inbuf.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (get != null) {
                try {
                    get.releaseConnection();
                } catch (Exception e) {
                }
            }
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
            if (get.getStatusCode() == HttpStatus.SC_OK) {
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
            log4j.error(e);
            result = false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (get != null) {
                try {
                    get.releaseConnection();
                } catch (Exception e) {
                }
            }
        }

        return result;
    }

    public boolean combineTif2Pdf(String[] tiffs, OutputStream baos) throws Exception {
        boolean result = false;

        if ((tiffs != null) && tiffs.length > 0) {

            RandomAccessFileOrArray tiffile = null;
            Document document = null;
            PdfContentByte cb = null;
            try {

                try {
                    // open first file to get image size and set document size
                    tiffile = new RandomAccessFileOrArray(tiffs[0]);
                    Image img = TiffImage.getTiffImage(tiffile, 1);
                    document = new Document(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
                    PdfWriter writer = PdfWriter.getInstance(document, baos);
                    document.open();
                    cb = writer.getDirectContent();

                    // add first image to document here to avoid opening it
                    // again later

                    document.newPage();
                    img.setAbsolutePosition(0, 0);
                    cb.addImage(img);
                } finally {
                    tiffile.close();
                }

                int count = tiffs.length;
                // start loop at second position in array
                for (int file_index = 1; file_index < count; file_index++) {
                    try {
                        tiffile = new RandomAccessFileOrArray(tiffs[file_index]);
                        Image img = TiffImage.getTiffImage(tiffile, 1);
                        document.newPage();
                        img.setAbsolutePosition(0, 0);
                        cb.addImage(img);
                    } finally {
                        if (tiffile != null) {
                            try {
                                tiffile.close();
                            } catch (Exception e) {
                            }
                        }
                    }
                }

                result = true;
            } catch (Exception e) {
                throw e;
            } finally {
                if (document != null) {
                    try {
                        document.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
        return result;
    }

    // Helper method to read in 'cached' http responses
    // to limit the traffic to USPTO
    // during testing
    /*
     * private String getFileAsString(String file) { StringBuffer response = new StringBuffer(); BufferedReader rdr = null; try { rdr = new BufferedReader(new
     * FileReader(file)); if(rdr != null) { while(rdr.ready()) { response.append(rdr.readLine()); } } else { log4j.error("Response is null"); } }
     * catch(IOException e) { log4j.error(e); } finally { try { if(rdr != null) { rdr.close(); } } catch(IOException e) { log4j.error(e); } } return
     * response.toString(); }
     */

}