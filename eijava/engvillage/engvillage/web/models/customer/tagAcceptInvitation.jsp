<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page import="org.ei.domain.*"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.session.*"%><%@ page import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.*"%><%@ page import="org.ei.domain.personalization.GlobalLinks"%><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page  errorPage="/error/errorPage.jsp"%><%

	SessionID sessionIdObj = null;
	ControllerClient client = null;
	String sessionId  = null;
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionIdObj = ussession.getSessionID();
	String pUserId = ussession.getProperty("P_USER_ID");
	ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
	String database = clientCustomizer.getDefaultDB();

	String fullName = null;

	String groupid = null;
	TagGroup cg = null;
	if(request.getParameter("groupid") != null)
	{
		groupid = request.getParameter("groupid");
		TagGroupBroker broker = new TagGroupBroker();
		cg = broker.getGroup(groupid, true);
		fullName = cg.getMember(cg.getOwnerid()).getFullName();
	}

	out.write("<PAGE>");
	out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
	if (pUserId != null)
	{
		out.write("<PUSERID>"+pUserId+"</PUSERID>");
	}
	if (groupid != null)
	{
		out.write("<GROUPID>"+groupid+"</GROUPID>");
	}
	out.write("<HEADER/>");
	out.write("<FOOTER/>");
	out.write("<DBMASK>");
	out.write(database);
	out.write("</DBMASK>");
	out.write("<FULL-NAME>");
	out.write(fullName);

	out.write("</FULL-NAME>");
	out.write("<SESSION-ID>");
	out.write(sessionIdObj.toString());
	out.write("</SESSION-ID>");
	out.write("<ADDTAGSELECTRANGE-NAVIGATION-BAR/>");
	out.write("<GROUPS-NAVIGATION-BAR/>");
	out.write("<NEXTURL><![CDATA[");
	out.write("CID=tagAcceptInvitationFinal&groupid=");
	out.write(groupid);
	out.write("]]></NEXTURL>");
	out.write("<BACKURL><![CDATA[");
	out.write("CID=tagAcceptInvitation&groupid=");
	out.write(groupid);
	out.write("]]></BACKURL>");
	if (cg != null)
	{
		cg.toXML(out);
	}
	out.write("</PAGE>");
	out.write("<!--END-->");
	out.flush();
%>