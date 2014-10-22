<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="org.ei.tags.*"%><%@ page errorPage="/error/errorPage.jsp"%><%

	String groupID = request.getParameter("groupID");
	String memberID = request.getParameter("memberID");
	TagGroupBroker groupBroker = new TagGroupBroker();
	if (groupID != null && memberID != null)
	{
		String[] id = {memberID};
		groupBroker.removeMembers(groupID,id);
	}
	pageContext.forward("tagGroups.jsp");
%>