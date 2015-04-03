<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.URLDecoder"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*" %>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
	Query query = null;
	ControllerClient client = null;
	ServletContext context = config.getServletContext();

	String sessionID  = null;
	String database = null;
	
	String count=null;
	String resultsFormat=null;
	String option=null;
	String searchID=null;
	
	String markOption=null;
	String userId=null;

	boolean remoteDB=false;

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionID = ussession.getID();
	userId = ussession.getProperty("P_USER_ID");

	if(request.getParameter("database")!=null)
	{
        database = request.getParameter("database");
	}

	if(request.getParameter("resultsformat")!=null)
	{
		resultsFormat = request.getParameter("resultsformat");
	}

	if(resultsFormat!=null && resultsFormat.equals("remoteRedirect"))
    {
	    remoteDB=true;
	}

	if(request.getParameter("count")!=null)
	{
		count = request.getParameter("count");
	}

	if(request.getParameter("searchid") != null)
	{
		searchID = request.getParameter("searchid");
	}

	if(request.getParameter("option")!=null)
	{
		option = request.getParameter("option");
	}

    if(request.getParameter("selectvalue")!=null)
	{
		markOption = request.getParameter("selectvalue");
	}

	if(markOption!=null && markOption.equals("unmark"))
	{
        //delete search from savedsearches.
		if(option!=null && option.equals("SavedSearch"))
		{   
            Searches.removeSavedSearch(searchID,userId);
		}
		else if(option!=null && option.equals("EmailAlert"))
        {
            Searches.removeEmailAlertSearch(searchID,userId);
        }
	}
	else
	{
	    // The user may not have been logged in (personalization) when the search
	    // was originally run (only time Searchh USER_ID is set is on INSERT)
	    // so we have to pass in USER_ID along with SearchID for Udating
		if(option!=null && option.equals("EmailAlert"))
		{
            Searches.addEmailAlertSearch(searchID,userId);
		}
		else if(option!=null &&  option.equals("SavedSearch"))
		{
			Searches.addSavedSearch(searchID,userId);
		}
	}
    String backurl = request.getParameter("backurl");
	if(backurl != null)
	{
	    client.setRedirectURL("/controller/servlet/Controller?" + URLDecoder.decode(backurl));
  		client.setRemoteControl();
  		out.write("<!--END-->");
        out.flush();  		
    }
    
//	if(resultsFormat!=null)
//	{
//	    String urlString=null;
//        if(remoteDB==true)
//        {
//    		urlString = "/controller/servlet/Controller?CID="+resultsFormat+"&SEARCHID="+searchID+"&COUNT="+count+"&database="+database+"&remoteDB="+remoteDB+"&searchType="+query.getSearchType()+"&"+query.getPhysicalQuery().trim();
//    	}
//    	else
//    	{
//            urlString = "/controller/servlet/Controller?CID="+resultsFormat+"&SEARCHID="+searchID+"&COUNT="+count+"&database="+database+"&remoteDB="+remoteDB;
//    	}
//        client.setRedirectURL(urlString);
//  		client.setRemoteControl();
//    }
 %>
 <test/>