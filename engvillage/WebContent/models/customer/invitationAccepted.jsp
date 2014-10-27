<%@ page language="java" %><%@ page session="false" %><%@ page import="org.engvillage.biz.controller.ControllerClient"%><%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page import="org.ei.tags.*"%><%@ page  errorPage="/error/errorPage.jsp"%><%

	ControllerClient client = null;
	String groupid = null;
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	String pUserId = ussession.getUserid();
	if(request.getParameter("groupid") != null)
	{
		groupid = request.getParameter("groupid");
	}
	if(groupid != null)
	{
		TagGroupBroker broker = new TagGroupBroker();
		broker.addMember(groupid,pUserId);
	}

	client.setRedirectURL("/controller/servlet/Controller?CID=tagGroups");
	client.setRemoteControl();
%>