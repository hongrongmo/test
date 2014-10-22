<%@ page language="java" %><%@ page session="false" %><%@ page errorPage="/error/errorPage.jsp"%><%@ page buffer="20kb"%><%@ page import="java.net.URLEncoder"%>
<%

	String database = request.getParameter("database");
	String docid = request.getParameter("docid");
	String searchnav = request.getParameter("searchnav");
	int offset = 0;
	if(request.getParameter("offset") != null)
	{
	   offset = Integer.parseInt(request.getParameter("offset"));
	}
	String encodedSearchNav = URLEncoder.encode(searchnav);
	StringBuffer absurl = getabsURL(docid, database, encodedSearchNav);
	StringBuffer deturl = getdetURL(docid, database, encodedSearchNav);
	StringBuffer nonpatrefurl = getnonpatrefURL(docid, database, encodedSearchNav);
	StringBuffer backurl = getbackURL(docid, database, encodedSearchNav);
	StringBuffer nextrefurl =  new StringBuffer(backurl.toString());
	nextrefurl.append("&offset=");
	nextrefurl.append(Integer.toString(offset+25));
	request.setAttribute("NEXTREF_URL", nextrefurl);
	StringBuffer prevrefurl =  new StringBuffer(backurl.toString());
	prevrefurl.append("&offset=");
	prevrefurl.append(Integer.toString(offset-25));
	request.setAttribute("PREVREF_URL", prevrefurl);
	request.setAttribute("BACK_URL", backurl);
	request.setAttribute("ABS_URL", absurl);
	request.setAttribute("DET_URL", deturl);
	request.setAttribute("SEARCH_RESULTS_URL", new StringBuffer(searchnav));
	request.setAttribute("NEW_SEARCH_URL", new StringBuffer());
	request.setAttribute("NONPATREF_URL", nonpatrefurl);
	pageContext.forward("patentReferences.jsp");

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

	StringBuffer getnonpatrefURL(String docID,
								 String database,
								 String encodedSearchNav)
	{
		StringBuffer nonpatrefurlbuf = new StringBuffer();
		nonpatrefurlbuf.append("CID=referenceNonPatentReferencesFormat&amp;format=ReferencesFormat&amp;");
		nonpatrefurlbuf.append("database=").append(database).append("&amp;");
		nonpatrefurlbuf.append("docid=").append(docID).append("&amp;");
	    nonpatrefurlbuf.append("searchnav=").append(encodedSearchNav);
		return nonpatrefurlbuf;
	}

	StringBuffer getbackURL(String docID,
							String database,
							String encodedSearchNav)
	{
		StringBuffer refurlbuf = new StringBuffer();
		refurlbuf.append("CID=referenceReferencesFormat&format=patentReferencesFormat&");
		refurlbuf.append("database=").append(database).append("&");
		refurlbuf.append("docid=").append(docID).append("&");
		refurlbuf.append("searchnav=").append(encodedSearchNav);
		return refurlbuf;
	}



%>