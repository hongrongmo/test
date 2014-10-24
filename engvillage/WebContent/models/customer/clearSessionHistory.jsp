<%--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String
 --%>
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages -->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*" %>

<!--import statements of ei packages -->

<%@ page import="org.ei.connectionpool.*" %>
<%@ page import="org.engvillage.biz.controller.ControllerClient" %>
<%@ page import="org.engvillage.biz.controller.UserSession" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.Searches" %>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
    // Variable to hold the url to whch the request has to be redirected from the JSP
   	String urlString = null;
    // Total no of resutls of the last executed search query.Used for displaying the results after clearing the session history
    String totalDocCount=null;
	// The search results page number. Used for displaying the results after clearing the session history
	String currentPage=null;
	// The database of the last executed search query. Used for displaying the results after clearing the session history
	String database=null;
	// The search type of the search query . Used for displaying the results after clearing the session history
	String searchType=null;
    // The search id of the search query . Used for displaying the results after clearing the session history
	String searchID=null;
	// The searchResultsFormat of the last executed search query. Used for displaying the results after clearing the session history
	String searchResultsFormat=null;

	// Get the session id from the ControllerClient object
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	String sessionId=ussession.getSessionid();

    // Get the request object parameters

	if(request.getParameter("COUNT")!=null)
	{
	    currentPage=request.getParameter("COUNT").trim();
    }

    if(request.getParameter("database")!=null)
    {
		database=request.getParameter("database").trim();
	}

	if(request.getParameter("SEARCHTYPE")!=null)
	{
		searchType=request.getParameter("SEARCHTYPE").trim();
    }

	if(request.getParameter("SEARCHID")!=null)
	{
		searchID=request.getParameter("SEARCHID").trim();
	}

	if(request.getParameter("searchResultsFormat")!=null)
	{
		searchResultsFormat=request.getParameter("searchResultsFormat").trim();
	}


	// Clear the session searches using sessionId
	Searches.removeSessionSearches(sessionId);

    // Redirect the request to the appropriate place
	if(request.getParameter("nexturl")!=null) {
		urlString = "/controller/servlet/Controller?" + URLDecoder.decode(request.getParameter("nexturl"),"UTF-8"); 
	} else {
    	urlString="/controller/servlet/Controller?CID=viewCompleteSearchHistory&COUNT="+currentPage+"&SEARCHID="+searchID+"&database="+database+"&SEARCHTYPE="+searchType+"&searchResultsFormat="+searchResultsFormat+"&searchHistoryEmpty=true";
	}

	client.setRedirectURL(urlString);
	client.setRemoteControl();
%>
