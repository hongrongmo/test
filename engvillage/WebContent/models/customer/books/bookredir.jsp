<%@ page language="java" %><%@ page session="false" %>
<%
	org.ei.controller.ControllerClient client = new org.ei.controller.ControllerClient(request, response);
	client.setRedirectURL("/controller/servlet/Controller?CID=ebookSearch&database=131072");
	client.setRemoteControl();
%>