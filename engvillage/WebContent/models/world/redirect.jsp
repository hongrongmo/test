<%@ page language="java" %>
<%@ page session="false"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page errorPage="/error/errorPage.jsp"%>

<%
	ControllerClient client = new ControllerClient(request, response);
	String urlString = request.getParameter("URL");
    client.setRedirectURL(urlString);
    client.setRemoteControl();
   	return;

%>