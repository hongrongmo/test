<%@ page language="java" %>
<%@ page session="false" %>
<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<!--import statements of ei packages.-->
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.session.*" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%
    /**
    *This is used to update email alerts of savedsearches.
    *
    */
    // Object references
    ControllerClient client = null;
    ServletContext context = config.getServletContext();
    
    // Variable to hold the session id
    String sessionID  = null;
    // Variable to hold the database id of the saved search.
    String databaseID = null;
    // Variable to hold the option of the saved search(i.e either saved search or emailalert).
    String option = null;
    // this vraible for mark or unmark
    String markOption = null;
    
    // This for searchid
    String searchid = null;
    // variable for userId
    String userId = null;
%>
<%
    // Create a session object using the controllerclient.java
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionID = ussession.getID();
	userId = ussession.getUserIDFromSession();


    //Get all the parameters from the request
	if(request.getParameter("databaseid")!= null)
	{
		databaseID = request.getParameter("databaseid").trim();
	}

	if(request.getParameter("option")!= null)
	{
		option = request.getParameter("option").trim();
	}

	if(request.getParameter("selectvalue")!= null)
	{
        markOption = request.getParameter("selectvalue").trim();
	}

	if(request.getParameter("searchid") != null)
	{
		searchid = request.getParameter("searchid").trim();
	}

    try
    {
        if(option!=null && (option.equals("EmailAlert") || option.equals("EmailAlertSearch")))
        {
            if(markOption!=null && markOption.equals("unmark"))
            {
                Searches.removeEmailAlertSearch(searchid,userId);
            }
            else
            {
                Searches.addEmailAlertSearch(searchid,userId);
            }
        }
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }

%>
<test/>