<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>

<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>

<%
    String sessionId = null;
    SessionID sessionIdObj = null;

	String dups = null;
	Page oPage = null;
	DedupBroker dedupBroker;
	List dupList;

    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession = (UserSession)client.getUserSession();
    sessionId = ussession.getID();

    try
    {

		dups = request.getParameter("dups");
		dedupBroker = new DedupBroker();
		if(dups != null)
		{
			dupList = dedupBroker.getDupList(dups);
			oPage = dedupBroker.buildPage(dupList, FullDoc.FULLDOC_FORMAT, sessionId);
			out.write("<PAGE><LINK>false</LINK>");
			oPage.toXML(out);
			out.write("</PAGE>");
			out.flush();
		}
    }
    catch(Exception e)
    {
		e.printStackTrace();
	}

%>