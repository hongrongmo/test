<%--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.database
 * @param java.lang.String.savedSearchId
 * @param java.lang.String.option
 --%>
<%@ page language="java" %>
<%@ page session="false" %>
<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="javax.servlet.jsp.*" %>
<!--import statements of ei packages.-->
<%@ page import="org.ei.connectionpool.*" %>
<%@ page import="org.ei.controller.ControllerClient" %>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.Searches"%>

<%@ page errorPage="/error/errorPage.jsp"%>


<%
   /**
    *This page is used to remove single and delete all saved seaches for this user.
    */

    // This variable for sessionId
	String sessionId = null;
	// This variable for savedSearchID
	String savedSearchID=null;
	// This variable for deleteoption(i.e either single or clear all)
	String deleteOption=null;
	//this variable for usedid
	String userId=null;
    //this variable for databaseName
	String dbName=null;
    String show=null;

	// This variable is used to hold ControllerClient instance
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();

	sessionId = ussession.getID();
	userId = ussession.getUserIDFromSession();

    //getting parameters through request
	if(request.getParameter("database")!=null)
	{
		dbName = request.getParameter("database").trim();
	}

	if(request.getParameter("show")!=null)
	{
		show = request.getParameter("show");
	}

	// this is the same as query-id now
	if(request.getParameter("savedsearchid")!=null)
	{
		savedSearchID = request.getParameter("savedsearchid").trim();
	}

	if(request.getParameter("option")!=null)
	{
		deleteOption = request.getParameter("option").trim();
	}

    if((savedSearchID!=null) && (deleteOption!=null && deleteOption.trim().equals("single")))
    {
		// to remove single record - but maintain in session
		Searches.removeSavedSearch(savedSearchID, userId);
	}
	else
	{
        if("alerts".equalsIgnoreCase(show))
        {
    		// to clear email alerts for the user
    		Searches.removeUserEmailAlerts(userId);
        }
        else
        {
    		// to clear saved searches for the user
    		// this will maintain any current searches still in session
    		Searches.removeUserSavedSearches(userId);
        }
	}

	String urlString="/controller/servlet/Controller?CID=viewSavedSearches&database="+dbName;
	if(show!=null)
	{
	    urlString = urlString.concat("&show=").concat(show);
	}
 	client.setRedirectURL(urlString);
  	client.setRemoteControl();

  	return;
%>