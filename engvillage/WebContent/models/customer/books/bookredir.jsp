<%@ page language="java" %><%@ page session="false" %>
<%
	org.engvillage.biz.controller.ControllerClient client = new org.engvillage.biz.controller.ControllerClient(request, response);
	client.setRedirectURL("/controller/servlet/Controller?CID=ebookSearch&database=131072");
	client.setRemoteControl();
%>