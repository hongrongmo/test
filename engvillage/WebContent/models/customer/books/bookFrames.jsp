<%@ page session="false" %>
<%@ page language="java" %>
<%@ page import="java.util.*"%>
<%@page import="org.ei.service.amazon.s3.AmazonS3ServiceImpl"%>
<%@page import="org.ei.service.amazon.s3.AmazonS3Service"%>
<%@ page import="java.io.*"%>
<%@ page import="org.ei.service.*"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
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
    String wobl_url = "";
    public void jspInit()
    {
    	  try {
      	  RuntimeProperties eiProps = RuntimeProperties.getInstance();
      	  wobl_url = eiProps.getProperty(RuntimeProperties.WHOLE_BOOK_DOWNLOAD_BASE_URL);
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

	if (docid != null) {
		docid = docid.toLowerCase();
		String[] bookid = docid.split("_", 3);
		if (bookid != null && bookid.length == 3) {
			isbn = bookid[1];
			pdfpage = bookid[2];
			if (Long.parseLong(pdfpage) == 0) {
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
    out.write(wobl_url);
    out.write("]]></DOCVIEW>");

    out.write("<BOOKIMGS-URL><![CDATA[");
    out.write(wobl_url + "/images/" + isbn + "/" + isbn + "small.jpg");
    out.write("]]></BOOKIMGS-URL>");

	String strQuery = "";
	if (searchID != null) {
		Query tQuery = Searches.getSearch(searchID);
		if (tQuery == null) {
			tQuery = SavedSearches.getSearch(searchID);
		}

		if (tQuery != null) {
			LemQueryWriter searchQueryWriter = new LemQueryWriter();
			BaseParser parser = new BaseParser();
			BooleanQuery queryTree = (BooleanQuery) parser.parse(tQuery.getPhysicalQuery());
			if (BookDocument.isHitHighlightable(isbn)) {
				strQuery = searchQueryWriter.getQuery(queryTree);
				if (strQuery != null) {
					strQuery = strQuery.replaceFirst(";$", "");
				}
			}
		}
	}

	// Getting customerId
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession = (UserSession) client.getUserSession();
	IEVWebUser user = ussession.getUser();
	String customerId = ussession.getCustomerid();
	String sessionId = ussession.getSessionid();
	String strTicket = BookDocument.getReadPageTicket(isbn, customerId);

	StringBuffer pdfurl = new StringBuffer();
	pdfurl.append(wobl_url + "/" + isbn + "/pg_" + pdfpage + ".pdf");
	/*
	pdfurl.append("?TICKET=").append(strTicket);
	pdfurl.append("&DOCVIEWSESSION=$SESSIONID");
	pdfurl.append("#xml=").append(strDocviewURL).append("/").append(isbn).append("/pg_").append(pdfpage).append(".pdf.offsetinfo");
	pdfurl.append("?hilite=");
	if ((strQuery != null) && !strQuery.equals("")) {
		pdfurl.append(java.net.URLEncoder.encode(strQuery, "UTF-8"));
	}
	pdfurl.append(BookDocument.PDF_ANCHOR);
    */
	out.write("<PAGEURL><![CDATA[");
	out.write(pdfurl.toString());
	out.write("]]></PAGEURL>");

	out.write("</ROOT>");
%>
