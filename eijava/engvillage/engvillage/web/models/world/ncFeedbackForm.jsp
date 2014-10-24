<!--
  This page basically updates the session id and this is used in Feedback Component
  @param java.lang.String
-->
<%@ page language="java" %>
<%@ page session="false" %>

<%@ page contentType="text/xml"%>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<!--import statements of ei packages.-->

<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>

<%

   // Variable to hold sessionId
    String sessionId = null;

	ControllerClient client = new ControllerClient(request, response);

	if((request.getHeaderNames()!=null)&&(request.getHeader("SESSION.USER")!=null)){
		UserSession ussession=(UserSession)client.getUserSession();

		//client.updateUserSession(ussession);
		sessionId=ussession.getID();
    }
	client.setRemoteControl();

%>


<root>

<SESSION-ID><%=sessionId%></SESSION-ID>

<FOOTER/>

</root>


