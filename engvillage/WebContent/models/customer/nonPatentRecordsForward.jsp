<%@ page language="java" %><%@ page session="false" %><%@ page errorPage="/error/errorPage.jsp"%><%@ page buffer="20kb"%><%@ page import="java.net.URLEncoder"%><%

   String database = request.getParameter("database");
   String docid = request.getParameter("docid");
   String searchnav = request.getParameter("searchnav");
   String encodedSearchNav = URLEncoder.encode(searchnav);

   StringBuffer absurl = getabsURL(docid, database, encodedSearchNav);
   StringBuffer deturl = getdetURL(docid, database, encodedSearchNav);
   StringBuffer patrefurl = getpatrefURL(docid, database, encodedSearchNav);

   request.setAttribute("ABS_URL", absurl);
   request.setAttribute("DET_URL", deturl);
   request.setAttribute("SEARCH_RESULTS_URL", new StringBuffer(searchnav));
   request.setAttribute("NEW_SEARCH_URL", new StringBuffer());
   request.setAttribute("NONPATREF_URL", new StringBuffer());
   request.setAttribute("PATREF_URL", patrefurl);
   pageContext.forward("nonPatentRecords.jsp");

%><%!
	StringBuffer getabsURL(String docID,
						   String database,
						   String encodedSearchNav)
	{
		StringBuffer absurlbuf = new StringBuffer();
		absurlbuf.append("CID=referenceAbstractFormat&amp;format=referenceAbstractFormat&amp;");
		absurlbuf.append("database=").append(database).append("&amp;");
		absurlbuf.append("docid=").append(docID).append("&amp;");
	    absurlbuf.append("searchnav=").append(encodedSearchNav);
		return absurlbuf;
	}

	StringBuffer getdetURL(String docID,
						   String database,
						   String encodedSearchNav)
	{
		StringBuffer absurlbuf = new StringBuffer();
		absurlbuf.append("CID=referenceDetailedFormat&amp;format=referenceDetailedFormat&amp;");
		absurlbuf.append("database=").append(database).append("&amp;");
		absurlbuf.append("docid=").append(docID).append("&amp;");
	    absurlbuf.append("searchnav=").append(encodedSearchNav);
		return absurlbuf;
	}

	StringBuffer getpatrefURL(String docID,
							  String database,
						      String encodedSearchNav)
	{
		StringBuffer refurlbuf = new StringBuffer();
		refurlbuf.append("CID=referenceReferencesFormat&amp;format=patentReferencesFormat&amp;");
		refurlbuf.append("database=").append(database).append("&amp;");
		refurlbuf.append("docid=").append(docID).append("&amp;");
	    refurlbuf.append("searchnav=").append(encodedSearchNav);
		return refurlbuf;
	}
%>