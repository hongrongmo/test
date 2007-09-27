<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.books.BookQueryWriter"%>
<%@ page import="org.ei.books.LemQueryWriter"%>
<%@ page import="org.ei.books.BookDocument"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>
<%!
    String docview_url = "";
    public void jspInit()
    {
    	  try {
      	  RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
          docview_url = eiProps.getProperty("FastDocviewBaseUrl");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
%>
<%
  // Get the request parameters
  String cid = request.getParameter("CID");
  String docindex = request.getParameter("DOCINDEX");
  String searchID = request.getParameter("SEARCHID");
  String database = request.getParameter("database");
  String docid = request.getParameter("docid");

  out.write("<ROOT>");

  StringBuffer navFrameUrl = new StringBuffer();
  navFrameUrl.append("CID=").append("bookNav").append("&");
  navFrameUrl.append("SEARCHID=").append(searchID).append("&");
  navFrameUrl.append("DOCINDEX=").append(docindex).append("&");
  navFrameUrl.append("database=").append(database).append("&");
  navFrameUrl.append("docid=").append(docid);

  StringBuffer tocurl = new StringBuffer();
  tocurl.append("CID=").append("bookToc").append("&");
  tocurl.append("SEARCHID=").append(searchID).append("&");
  tocurl.append("DOCINDEX=").append(docindex).append("&");
  tocurl.append("database=").append(database).append("&");
  tocurl.append("docid=").append(docid);

  String isbn = "";
  String pdfpage = "";
  String strDocviewURL = docview_url;

  if(docid != null)
  {
    docid = docid.toLowerCase();
    String[] bookid = docid.split("_",3);
    if(bookid != null && bookid.length == 3)
    {
      isbn = bookid[1];
      pdfpage = bookid[2];
      if(Long.parseLong(pdfpage) == 0)
      {
        pdfpage = "1";
      }
    }
  }

	DecimalFormat df = new DecimalFormat("0000");
  df.setMinimumIntegerDigits(4);
  pdfpage = df.format(Long.parseLong(pdfpage));
  navFrameUrl.append("&").append("page=").append(pdfpage);

  out.write("<NAVURL><![CDATA[");
  out.write(navFrameUrl.toString());
  out.write("]]></NAVURL>");

  out.write("<TOCURL><![CDATA[");
  out.write(tocurl.toString());
  out.write("]]></TOCURL>");

  out.write("<DOCVIEW><![CDATA[");
  out.write(strDocviewURL);
  out.write("]]></DOCVIEW>");

  String strQuery = "";
  if(searchID != null)
  {
    Query tQuery = Searches.getSearch(searchID);
    if(tQuery == null)
    {
      tQuery = SavedSearches.getSearch(searchID);
    }

    if(tQuery != null)
    {
      LemQueryWriter searchQueryWriter = new LemQueryWriter();
      BaseParser parser = new BaseParser();
      BooleanQuery queryTree = (BooleanQuery) parser.parse(tQuery.getPhysicalQuery());
      if(BookDocument.isHitHighlightable(isbn))
      {
        strQuery = searchQueryWriter.getQuery(queryTree);
        if(strQuery != null)
        {
          strQuery = strQuery.replaceFirst(";$","");
        }
      }
    }
  }

  // Getting customerId
  ControllerClient client = new ControllerClient(request, response);
  UserSession ussession = (UserSession) client.getUserSession();
  User user = ussession.getUser();
  String customerId = user.getCustomerID();
  String sessionId = ussession.getID();
  String strTicket = BookDocument.getReadPageTicket(isbn, customerId);

  StringBuffer pdfurl = new StringBuffer();
  pdfurl.append(strDocviewURL + "/" + isbn + "/pg_" + pdfpage + ".pdf").append("?TICKET=").append(strTicket);
  pdfurl.append("&DOCVIEWSESSION=$SESSIONID");
  pdfurl.append("#xml=").append(strDocviewURL).append("/").append(isbn).append("/pg_").append(pdfpage).append(".pdf.offsetinfo");
  pdfurl.append("?hilite=");
  if((strQuery != null) && !strQuery.equals(""))
  {
    pdfurl.append(java.net.URLEncoder.encode(strQuery,"UTF-8"));
  }
  pdfurl.append(BookDocument.PDF_ANCHOR);

  out.write("<PAGEURL><![CDATA[");
  out.write(pdfurl.toString());
  out.write("]]></PAGEURL>");

  out.write("</ROOT>");

%>
