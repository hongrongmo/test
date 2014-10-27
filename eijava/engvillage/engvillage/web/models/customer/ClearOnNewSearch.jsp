<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  import="org.ei.session.*"%>
<%@ page  import="org.ei.domain.*"%>
<%@ page session="false" %>


<%@ page errorPage="/error/errorPage.jsp"%>

<%
	// declare variable to update the session.
	ControllerClient client = null;
	String sessionId = null;
	String clearOnValue = null;
	// get the session id and user id from the session.

	if( request.getParameter("clearonvalue") != null)
	{
		clearOnValue = request.getParameter("clearonvalue");
	}

	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();

	sessionId = ussession.getID();
	
	DocumentBasket documentBasket = new DocumentBasket(sessionId);
	documentBasket.updateClearOnNewSearch(("true").equalsIgnoreCase(clearOnValue));
	
	//ussession.setProperty("CLEAR_SELECTED_SET",clearOnValue);
	//client.updateUserSession(ussession);
	client.setRemoteControl();
%>
<test/>


