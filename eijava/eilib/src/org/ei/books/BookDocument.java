package org.ei.books;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.ei.config.ConfigService;
import org.ei.config.RuntimeProperties;
import org.ei.domain.DocID;
import org.ei.domain.EIDoc;
import org.ei.domain.ElementData;
import org.ei.domain.ElementDataMap;
import org.ei.domain.ISBN;
import org.ei.domain.Keys;

public class BookDocument extends EIDoc {

	public static String PDF_ANCHOR = "&original_content_type=application%2Fpdf&toolbar=1&statusbar=0&messages=0&navpanes=0";


	public static String getDocviewUrl() {
		String docview_url = null;

		RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
	    docview_url = eiProps.getProperty("FastDocviewBaseUrl");
	    if(docview_url == null)
	    {
	    	docview_url = "http://localhost/docview";
	    }
	    return docview_url;
	}

	public static String getTocPath() {
		String tocpath = null;

		  RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
	    tocpath = eiProps.getProperty("ReferexTocBasePath");

	    return tocpath;
	}

	public String getView() {
		if(getPageNum() == 0)
			return "/engvillage/views/customer/SearchResultsBookDetailFormat.xsl";
		else
			return "/engvillage/views/customer/SearchResultsDetailedFormat.xsl";
	}

	public BookDocument(DocID docID, ElementDataMap mapDocument, String format) {
        super(docID, mapDocument, format);
    }

    public BookDocument(DocID docId) {
        super(docId);
    }

    public String getISBN() {
      ISBN isbn = (ISBN) getElementDataMap().get(Keys.ISBN);
      if(isbn != null) {
        return isbn.withoutDash();
      }
      else {
        return null;
      }
    }

    public int getPageNum() {

        ElementData pageData = getElementDataMap().get(Keys.BOOK_PAGE);
        String[] page = pageData.getElementData();
        int pagenum = 0;

        if((page != null) && (page.length == 1))
        {
          try {
            pagenum = Integer.parseInt(page[0]);
          }
          catch(NumberFormatException e)
          {
            pagenum = 0;
          }
        }
        return pagenum;
    }

    public String getBookLink() {

      String viewStr = "";

      try {
        AdmitOneTicketer ticketer = AdmitOneTicketer.getInstance();
        viewStr = viewStr.concat("<URL><![CDATA[");
        // custid is sent in empty - we will concat onto end of URL in XSL
        // getBookTicketedURL() only does that itself.
        viewStr = viewStr.concat(ticketer.getBookTicketedURL(this.getISBN(),"", System.currentTimeMillis()));
        viewStr = viewStr.concat("]]></URL>");
      } catch(BookException e) {}

      return viewStr;
    }

    public static String getReadPageTicket(String isbn, String custid) {

        String viewStr = "";

        try {
          AdmitOneTicketer ticketer = AdmitOneTicketer.getInstance();
          // custid is sent in empty - we will concat onto end of URL in XSL
          // getBookTicketedURL() only does that itself.
          viewStr = ticketer.getPageTicket(isbn, custid, System.currentTimeMillis());
        } catch(BookException e) {}

        return viewStr;
      }

    public static String getReadBookLink(String isbn, String custid) {

        String viewStr = "";

        try {
          AdmitOneTicketer ticketer = AdmitOneTicketer.getInstance();
          // custid is sent in empty - we will concat onto end of URL in XSL
          // getBookTicketedURL() only does that itself.
          viewStr = ticketer.getBookTicketedURL(isbn, custid, System.currentTimeMillis());
        } catch(BookException e) {}

        return viewStr;
      }

    public void toXML(Writer out) throws IOException {
      super.toXML(out);

      if (getPageNum() == 0 && getDeep()) {

    	out.write("<TOC>");
        out.write("<![CDATA[");

        out.write(getTOC());

        out.write("]]>");
        out.write("</TOC>");
      }
      return;
    }
/*    public String getTOC() {

      Writer wrtr = new StringWriter();

      try {
        org.ei.data.books.BookPart part = null;
        BookPartBuilder builder = new BookPartBuilder();
        part = builder.buildBookPart(this.getISBN());
        part.setIndex(0);
        BookPartXMLVisitor xmlVisitor = new BookPartXMLVisitor(wrtr,
                                          "923358",
                                          new String[]{"BPE","ELE","MAT","CHE"});

        part.accept(xmlVisitor);
      } catch(Exception e) {}

      return wrtr.toString();
    }
*/
    public boolean hasFulltextandLocalHoldingsLinks() {
        // Full-text, linda hall, local holdings
        // not supported by books
        return false;
    }

    // this array will have to be Sorted so the
    // Arrays.binarySearch method to work correctly
    private static String[] badbooks = new String[] {
    	"0750672803",
    	"0884151646",
    	"0884152731",
    	"0884152898",
    	"0884154114",
    	"0884156575",
    	"0884158225",
    	"0884159485",
    	"0750650508",
    	"0750650559",
    	"0750650761",
    	"0750651318",
    	"0750655208",
    	"0750672439",
    	"0750649321",
    	"0750639962",
    	"0750615478a",
    	"0750615478b",
    	"0750615478c",
    	"0122896769",
    	"0121189309",
    	"0124518311",
    	"0126912955",
    	"0750670622",
    	"0750643986",
    	"0750694831",
        "9780750672801",
        "9780884151647",
        "9780884152736",
        "9780884152897",
        "9780884154112",
        "9780884156574",
        "9780884158226",
        "9780884159483",
        "9780750650502",
        "9780750650557",
        "9780750650762",
        "9780750651318",
        "9780750655200",
        "9780750672436",
        "9780750649322",
        "9780750639965",
        "9780750615471a",
        "9780750615471b",
        "9780750615471c",
        "9780122896767",
        "9780121189303",
        "9780124518315",
        "9780126912951",
        "9780750670623",
        "9780750643986",
        "9780750694834"};
    static {
      Arrays.sort(badbooks);
    }

    public static boolean isHitHighlightable(String isbn) {
        // check to see this is not a 'bad' isbn
        return (Arrays.binarySearch(badbooks, isbn) < 0);
    }

    public String getTOC() {
      if(getTocPath() != null)
        return getTOCFs();
      else
        return getTOCHttp();
    }

    private String readTOC(Reader rdrin) {

      Writer wrtr = new StringWriter();

      BufferedReader rdr = new BufferedReader(rdrin);
      try {
       if(rdr != null)
        {
          while(rdr.ready())
          {
            String aline = rdr.readLine();
            wrtr.write(aline);
          }
        }
        else
        {
          System.out.println("Response is null");
        }
      }
      catch(IOException e) {}
      finally {
        try {
        rdr.close();
        }
        catch(IOException e) {}
      }

      return wrtr.toString();
    }

    private String getTOCFs() {

      String strtoc = null;

      try {

        String tocPath =  getTocPath() + System.getProperty("file.separator") + getISBN() + System.getProperty("file.separator") + getISBN() + "_toc.html";

        File doc_data = new File(tocPath); //filepathname + System.getProperty("file.separator") + "doc_data.txt");
        strtoc = readTOC(new FileReader(doc_data));
      }
      catch(FileNotFoundException e)
      {
          e.printStackTrace();
          System.out.println(e.getMessage());
      }
      finally
      {
      }
      return strtoc;
    }

    private String getTOCHttp() {

      String strtoc = null;
      URL url = null;
      HttpURLConnection ucon = null;

      try {

        String tocUrl =  getDocviewUrl() + "/pdfs/" + getISBN() + "/" + getISBN() + "_toc.html";
        url = new URL(tocUrl);
        ucon = (HttpURLConnection) url.openConnection();
        ucon.setDoOutput(true);
        ucon.setRequestMethod("GET");
        ucon.setUseCaches(false);

        strtoc = readTOC(new InputStreamReader(ucon.getInputStream(),"UTF-8"));

      }
      catch(FileNotFoundException e)
      {
          e.printStackTrace();
          System.out.println(e.getMessage());
      }
      catch(Exception e)
      {
          e.printStackTrace();
          System.out.println(e.getMessage());
      }
      finally
      {

          if(ucon != null)
          {
           ucon.disconnect();
          }
      }
      return strtoc;
    }
}