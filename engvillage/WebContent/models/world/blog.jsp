
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page contentType="text/xml"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>

<%
    String mid = request.getParameter("MID");
    String database = request.getParameter("DATABASE");
    String title = request.getParameter("TITLE");
    ControllerClient client = new ControllerClient(request, response);   
    UserSession ussession=(UserSession)client.getUserSession();
    String serverName= ussession.getEnvBaseAddress();  
%>

<root>
<DOC-ID><%=mid%></DOC-ID>
<DATABASE><%=database%></DATABASE>
<SERVER-NAME><%=serverName%></SERVER-NAME>
<TITLE><![CDATA[<%=title%>]]></TITLE>
<FOOTER/>
</root>
