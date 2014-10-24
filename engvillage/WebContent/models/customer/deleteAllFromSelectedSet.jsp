<!--
 * This file deletes all records for the Selected Set for that session
 * and generates the No records selected page
 * @param java.lang.String
 -->

<%@ page language="java"%>
<%@ page session="false"%>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<!--import statements of ei packages.-->
<%@  page import="org.ei.domain.*"%>
<%@  page import="org.ei.config.*"%>
<%@  page import="org.engvillage.biz.controller.ControllerClient"%>
<%@  page import="org.engvillage.biz.controller.UserSession"%>

<%@ page errorPage="/error/errorPage.jsp"%>


<%
	// Object references
	ControllerClient client = null;
	//DocumentBasket object
	DocumentBasket documentBasket = null;

	// Variable to hold the sesion id for the current search
	String sessionid = null;

	// Variable to hold the search type of the last executed search query
	String searchType = null;
	// Variable to hold the search id of the last executed search query
	String searchID = null;
	// Variable to hold the count odf the results page of the last executed search query
	String count = null;
	// Variable to hold the database name of the last executed search query
	String databaseType = null;
	// Variable to hold the database ID
	String databaseID = null;
	// Variable to hold the URL to which the request is to be forwarded after this JSP functionalty has been completed
	String urlString = null;
	// Link back to search results
	String searchresults = null;

	String selectOption = null;

	// Obtain all the regeust parameters

	if (request.getParameter("selectoption") != null) {
		selectOption = request.getParameter("selectoption");
	} else {

		searchType = request.getParameter("SEARCHTYPE").trim();

		searchID = request.getParameter("SEARCHID").trim();

		count = request.getParameter("COUNT").trim();

		if (request.getParameter("DATABASETYPE") != null) {
			databaseType = request.getParameter("DATABASETYPE").trim();
		}
		if (request.getParameter("DATABASEID") != null) {
			databaseID = request.getParameter("DATABASEID").trim();
		}
		if (request.getParameter("searchresults") != null) {
			searchresults = request.getParameter("searchresults").trim();
		}
	}

	// Create a session object using the controllerclient.java
	client = new ControllerClient(request, response);
	UserSession ussession = (UserSession) client.getUserSession();

	sessionid = ussession.getSessionid();

	// Create the Document Basket object.
	documentBasket = new DocumentBasket(sessionid);
	// Use removeAll () of Documentbasket toi remove all the documents from the Document Basket
	documentBasket.removeAll();

	if (databaseType != null) {
		urlString = "/controller/servlet/Controller?CID=errorSelectedSet&SEARCHID="
				+ searchID
				+ "&SEARCHTYPE="
				+ searchType
				+ "&COUNT="
				+ count;
		if (databaseType != null) {
			urlString += "&DATABASETYPE=" + URLEncoder.encode(databaseType);
		}
		if (databaseID != null) {
			urlString += "&DATABASEID=" + databaseID;
		}
		if (searchresults != null) {
			urlString += "&searchresults=" + searchresults;
		}
				
	}

	if (selectOption == null) {
		client.setRedirectURL(urlString);
		client.setRemoteControl();
	} else {
%>
<test />
<%
	}
%>
