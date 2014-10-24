<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  import="org.ei.session.*"%>
<%@ page  import="org.ei.domain.*"%>
<%@ page session="false" %>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
	// declare variable to update the session.
	ControllerClient client = null;
	String sessionId = null;
	boolean removeFlag = false;

	// get the session id and user id from the session.
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();

	sessionId = ussession.getID();

	//deletes all the records related to session history,document
	//basket and cached page which are cached for the session
	PageCache cache = new PageCache(sessionId);
	cache.cleanCache();

	ussession.setProperty("VALID","FALSE");
	client.updateUserSession(ussession);
	client.setRemoteControl();

	String urlString = "/controller/servlet/Controller?CID=home";

	if(urlString!=null)
	{
	   	client.setRedirectURL(urlString);
	   	client.setRemoteControl();
	}
%>


