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

	private String tocpath = null;
	private String docview_url = null;
	private String wobl_url = null;

	public boolean isBook()
	{
		return true;
	}

	public String getDocviewUrl() {
	    return docview_url;
	}

	public void setDocviewUrl() {
	  RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
    docview_url = eiProps.getProperty("FastDocviewBaseUrl");
	}

	public String getTocPath() {
	    return tocpath;
	}
	public void setTocPath() {
    RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
    tocpath = eiProps.getProperty("ReferexTocBasePath");
	}

	public String getWoblUrl() {
	    return wobl_url;
	}

	public void setWoblUrl() {
	  RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
    wobl_url = eiProps.getProperty("WholeBookDownloadBaseUrl");
	}

	public String getView() {
		if(getPageNum() == 0)
			return "/engvillage/views/customer/SearchResultsBookDetailFormat.xsl";
		else
			return "/engvillage/views/customer/SearchResultsDetailedFormat.xsl";
	}

  private void init() {
    setTocPath();
    setDocviewUrl();
    setWoblUrl();
  }

	public BookDocument(DocID docID, ElementDataMap mapDocument, String format) {
    super(docID, mapDocument, format);
    init();
  }

  public BookDocument(DocID docId) {
    super(docId);
    init();
  }

    public String getISBN13() {
      ISBN isbn = (ISBN) getElementDataMap().get(Keys.ISBN13);
      String isbnstr = isbn.withoutDash();

      return isbnstr;
    }

    public String getISBN10() {
      ISBN isbn = (ISBN) getElementDataMap().get(Keys.ISBN);
      String isbnstr = isbn.withoutDash();
      return isbnstr;
    }

    public String getChapterPii() {
      String pii = null;
      if(getElementDataMap().containsKey(Keys.BOOK_CHAPTER_PII)) {
        ElementData piidata = getElementDataMap().get(Keys.BOOK_CHAPTER_PII);
      pii = (piidata.getElementData())[0];
      }
      return pii;
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

      public static String getReadChapterLink(String baseUrl, String isbn, String pii, String custid) {

        String viewStr = "";

        try {
          AdmitOneTicketer ticketer = AdmitOneTicketer.getInstance();
          // custid is sent in empty - we will concat onto end of URL in XSL
          // getBookTicketedURL() only does that itself.
          viewStr = ticketer.getChapterTicketedURL(baseUrl, isbn, pii, custid, System.currentTimeMillis());
        }
        catch(BookException e) {
          //e.printStackTrace();
          System.out.println(e.getMessage());
        }

        return viewStr;
      }

      public static String getReadBookLink(String baseUrl, String isbn, String custid) {

        String viewStr = "";
        if(isbn != null) {
          if((isbn.length() >= 9) && (isbn.length() <= 11)) {
            String isbnSuffix = null;
            if(isbn.length() == 11) {
              isbnSuffix = isbn.substring(10,11);
            }
            String isbnroot = "978"+ isbn.substring(0, 9);
            isbn = (isbnroot + getISBN13CheckDigit(isbnroot) + ((isbnSuffix != null) ? isbnSuffix : ""));
          }
          try {
            AdmitOneTicketer ticketer = AdmitOneTicketer.getInstance();
            // custid is sent in empty - we will concat onto end of URL in XSL
            // getBookTicketedURL() only does that itself.
            viewStr = ticketer.getBookTicketedURL(baseUrl, isbn, custid, System.currentTimeMillis());
          }
          catch(BookException e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
          }
        }
        return viewStr;
      }

      private static char getISBN13CheckDigit(final String isbn) {
        int len = isbn.length();
        int digitSum = 0;
        int calcValue = 0;

        // length of passed in isbn must be 12 or
        // we will get a nullpointer here
        for (int i = 0; i < 12; i++) {
            int val = Integer.parseInt((isbn.substring(i, i + 1)));
            if (i % 2 == 1) {
                digitSum += val * 3;
            } else {
                digitSum += val;
            }
        }

        calcValue = (10 - (digitSum % 10)) % 10;

        return (char) (calcValue + '0');
    }


    public void toXML(Writer out) throws IOException {
      super.toXML(out);

      out.write("<BOOKIMGS>");
    	out.write(getWoblUrl());
      out.write("</BOOKIMGS>");

      out.write("<WOBLSERVER>");
    	out.write(getWoblUrl());
      out.write("</WOBLSERVER>");

      if (getPageNum() == 0 && getDeep()) {

        out.write("<CLOUD>");
        out.write("<![CDATA[");
        getTagCloud(out);
        out.write("]]>");
        out.write("</CLOUD>");

      	out.write("<TOC>");
        out.write("<![CDATA[");
        getTOC(out);
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
    	"0750672439",
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
        "9780750672436",
        "9780750615471a",
        "9780750615471b",
        "9780750615471c",
        "9780122896767",
        "9780121189303",
        "9780124518315",
        "9780126912951",
        "9780750670623",
        "9780750643986",
        "9780750694834",
        "9780750648851", // Still Bad - both S300 and WOBL
        "9780121746513", // Bad WOBL - no S300
        "9780750646420", // Bad WOBL - no S300
        "9780750674355" // Bad WOBL - no S300
        };

    static {
      Arrays.sort(badbooks);
    }

    public static boolean isHitHighlightable(String isbn) {
        // check to see this is not a 'bad' isbn
        return (Arrays.binarySearch(badbooks, isbn) < 0);
    }

    private void readFileToWriter(Writer out, String filepath) {

      BufferedReader rdr = null;

      File infile = new File(filepath);
      if(infile.exists())
      {
        try {
          rdr = new BufferedReader(new FileReader(infile));
          if(rdr != null)
          {
            while(rdr.ready())
            {
              out.write(rdr.readLine());
            }
          }
        }
        catch(IOException e) {}
        finally {
          try {
          rdr.close();
          }
          catch(IOException e) {}
        }
      }
      return;
    }

    public void getTagCloud(Writer out) {

      String cloudPath = getTocPath() + System.getProperty("file.separator") + getISBN13() + System.getProperty("file.separator") + getISBN13() + "_cloud.html";
      readFileToWriter(out, cloudPath);
      return ;
    }

    public void getTOC(Writer out) {

      String tocPath = getTocPath() + System.getProperty("file.separator") + getISBN13() + System.getProperty("file.separator") + getISBN13() + "_toc.html";
      readFileToWriter(out, tocPath);
      return;
    }


/* JAM - no longer in use.  TOC is always read from filesystem.
    private String getTOCHttp() {

      String strtoc = null;
      URL url = null;
      HttpURLConnection ucon = null;

      try {

        String tocUrl =  getDocviewUrl() + "/" + getISBN13() + "/" + getISBN13() + "_toc.html";
        url = new URL(tocUrl);
        ucon = (HttpURLConnection) url.openConnection();
        ucon.setDoOutput(true);
        ucon.setRequestMethod("GET");
        ucon.setUseCaches(false);

        strtoc = readTOC(new InputStreamReader(ucon.getInputStream(),"UTF-8"));

      }
      catch(FileNotFoundException e)
      {
//          e.printStackTrace();
          System.out.println(e.getMessage());
      }
      catch(Exception e)
      {
//          e.printStackTrace();
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
    }*/
}