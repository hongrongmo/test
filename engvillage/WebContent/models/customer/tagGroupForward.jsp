<%@ page language="java"%><%@ page session="false"%><%@ page
	import="java.util.*"%><%@ page import="org.ei.tags.*"%><%@ page
	errorPage="/error/errorPage.jsp"%>
<%
    String groupID = null;

    if (request.getParameter("groupID") != null) {
        groupID = request.getParameter("groupID");
    }
    TagGroupBroker groupBroker = new TagGroupBroker();
    if (groupID != null) {
        groupBroker.removeGroup(groupID);
    }
    pageContext.forward("tagGroups.jsp");
%>