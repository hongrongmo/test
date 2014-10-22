<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java"%><%@ page session="false"%><%@ page
	import="java.util.*"%><%@ page import="org.ei.tags.*"%><%@ page
	import="org.ei.domain.*"%><%@ page
	import="org.engvillage.biz.controller.ControllerClient"%><%@ page
	import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page
	import="org.ei.config.*"%><%@ page errorPage="/error/errorPage.jsp"%>
<%
    ControllerClient client = null;
    String database = null;

    client = new ControllerClient(request, response);
    UserSession ussession = (UserSession) client.getUserSession();
    String pUserId = ussession.getUserid();
    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);
    database = clientCustomizer.getDefaultDB();
    TagGroupBroker groupBroker = new TagGroupBroker();
    TagGroup group = groupBroker.getGroup(request.getParameter("groupid"), false);

    out.write("<PAGE>");
    group.toXML(out);
    out.write("<FOOTER/>");
    out.write("<SESSION-ID>");
    out.write(ussession.getSessionid());
    out.write("</SESSION-ID>");
    out.write("<DBMASK>");
    out.write(database);
    out.write("</DBMASK>");
    out.write("</PAGE>");
%>