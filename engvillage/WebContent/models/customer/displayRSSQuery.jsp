<%@ page session="false" %>
<%@ page  import="org.ei.util.GUID"%>
<%@ page  import="org.ei.xmlio.RSS"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.ei.config.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<ROOT>
<%
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	String serverName = ussession.getProperty(UserSession.ENV_BASEADDRESS);
   	String customerID=ussession.getCustomerid().trim();
	String term1=request.getParameter("term1");
	String database=request.getParameter("database");
	String queryID = new GUID().toString();
	RSS.setQuery(queryID,term1,database,customerID);
	out.write("<SERVERNAME>"+serverName+"</SERVERNAME>");
	out.write("<QUERYID>"+queryID+"</QUERYID>");
%>
</ROOT>