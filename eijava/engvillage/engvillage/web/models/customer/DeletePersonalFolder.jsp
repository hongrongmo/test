<!--
	This page expects the following params
	* @param java.lang.String.database.
	* @param java.lang.long.folderid.
	and deletes the folder from the system then
	redirects to the view folders page.
-->
<%@ page session="false" %>

<%@ page  import=" org.ei.domain.personalization.*"%>
<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  import="org.ei.session.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>


<%
	//declare variable to hold the client session object.
	ControllerClient client = null;
	// declare variable to hold the user id from the session.
	String sUserId = null;
	// declare variable to hold session id.
	String sSessionId = null;
	// declare variable to hold user id.
	String nUserId  = null;
	// declare variable to hold folder id.
	String strFolderId  = "";

	// declare variable to hold delete flag.
	boolean deleteFlag = false;
	// declare variable to hold url.
	String urlString = null;
	// declare variable to hold database value.
	String database = null;

	// get the database param value.
	if( request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}
	// get the folder id param value.
	if( request.getParameter("folderid") != null)
	{
		strFolderId  = request.getParameter("folderid");
	}
	// get the client session and from that session get the session id and user id.
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();
	sSessionId = ussession.getID();
	SessionID sessionObj = ussession.getSessionID();

	sUserId = ussession.getProperty("P_USER_ID");
	//client.updateUserSession(ussession);

	// check for user id existance.
	if( sUserId != null)
	{
		nUserId = sUserId;
	}
	// delete folder from the system.
	if( nUserId != null)
	{
		SavedRecords sr = new SavedRecords(nUserId);

		// this is idiotic
		// get the folder name, create a folder object
		// set the folder id, then call delete
		String strFolderName = sr.getFolderName(strFolderId);
		Folder newFolder = new Folder(strFolderName);
		newFolder.setFolderID(strFolderId);

		deleteFlag = sr.removeFolder(newFolder);
	}
	// construct url
	if(deleteFlag)
	{
		long milli = System.currentTimeMillis();
		urlString = "/controller/servlet/Controller?EISESSION="+sessionObj.toString()+"&CID=viewPersonalFolders&database="+database+"&time="+milli;
	}
	//redirect to the view folders page.
	if(urlString!=null)
	{
	    	client.setRedirectURL(urlString);
        	client.setRemoteControl();
	}

%>
