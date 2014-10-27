<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="org.ei.tags.*"%><%@ page import="org.ei.domain.*"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.session.*"%><%@ page import="org.ei.config.*"%><%@ page  errorPage="/error/errorPage.jsp"%><%


	SessionID sessionIdObj = null;
	ControllerClient client = null;
	String database = null;

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionIdObj = ussession.getSessionID();
	String pUserId = ussession.getProperty("P_USER_ID");
	ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
	database = clientCustomizer.getDefaultDB();
	TagGroupBroker groupBroker = new TagGroupBroker();
	TagGroup group = groupBroker.getGroup(request.getParameter("groupid"), false);

	out.write("<PAGE>");
	group.toXML(out);
	out.write("<FOOTER/>");
	out.write("<SESSION-ID>");
	out.write(sessionIdObj.toString());
	out.write("</SESSION-ID>");
	out.write("<DBMASK>");
	out.write(database);
	out.write("</DBMASK>");
	out.write("</PAGE>");
%>