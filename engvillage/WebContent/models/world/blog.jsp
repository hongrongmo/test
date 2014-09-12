
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page contentType="text/xml"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>

<%
    String mid = request.getParameter("MID");
    String database = request.getParameter("DATABASE");
    String title = request.getParameter("TITLE");
    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession=(UserSession)client.getUserSession();
    String serverName = ussession.getProperty(UserSession.ENV_BASEADDRESS);
%>

<root>
<DOC-ID><%=mid%></DOC-ID>
<DATABASE><%=database%></DATABASE>
<SERVER-NAME><%=serverName%></SERVER-NAME>
<TITLE><![CDATA[<%=title%>]]></TITLE>
<FOOTER/>
</root>
