<!--
	This Page expects the following params ,
	adds the folder for the user and redirect to the view folders page.
	* @param java.lang.String.FolderName.
	* @param java.lang.String.Database.
-->
<%@page import="org.engvillage.biz.controller.UserSession"%>
<%@ page  import=" org.ei.domain.personalization.*"%>
<%@ page  import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page  import="org.engvillage.biz.controller.UserSession"%>
<%@ page session="false" %>


<%@ page errorPage="/error/errorPage.jsp"%>

<%
	// declare variable to update the session.
	ControllerClient client = null;
	// delcare variable to hold user id
	String nUserId  = null;
	// declare the variable to get the user id from the session.
	String sUserId = null;
	// declare the variable to get the session id from the session.
	String sSessionId = null;
	// declare the variable to hold folder name.
	String sFolderName  = null;
	// declare variable to hold the flag value while adding the folder.
	boolean addFlag = false;
	// declare variable to hold the url .
	String urlString = null;
	// declare variable to hold database.
	String database = null;

	// check for folder name parameter
	if( request.getParameter("foldername") != null)
	{
			sFolderName = request.getParameter("foldername");
	}
	// check for database parameter.
	if( request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}

	// get the session id and user id from the session.
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();
	sSessionId = ussession.getSessionid();
	sUserId = ussession.getUserid();
	//client.updateUserSession(ussession);

	// check for existance of user id.
	if(sUserId != null)
	{
		nUserId = sUserId;
	}

	// if the user id available add the folder for the user.
	if( nUserId != null)
	{
		SavedRecords sr = new SavedRecords(nUserId);
		Folder newFolder = new Folder(sFolderName);
		addFlag = sr.addFolder(newFolder);
	}
	// if the adding is success construct the redirect url
	if(addFlag)
	{
		urlString = "/controller/servlet/Controller?CID=viewPersonalFolders&database="+database;
	}
	// redirect the page to view folders.
	if(urlString!=null)
	{
	    	client.setRedirectURL(urlString);
        	client.setRemoteControl();
	}
%>
