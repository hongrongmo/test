<%@ page session="false" %>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<PAGE>

<%
	ControllerClient client = new ControllerClient(request, response);
        UserSession ussession=(UserSession)client.getUserSession();
        if(ussession.getEnvDayPass() != null)
        {
        	out.print("<DAYPASS>true</DAYPASS>");
        }
%>

<END-SESSION></END-SESSION>
<FOOTER></FOOTER>
</PAGE>


