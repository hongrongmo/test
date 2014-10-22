<%@ page language="java"%><%@ page session="false"%><%@ page
	import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page
	import="org.ei.domain.*"%><%@ page
	import="org.engvillage.biz.controller.ControllerClient"%><%@ page
	import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page
	import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page
	import="org.ei.domain.Searches"%><%@ page
	import="org.ei.tags.TagBroker"%><%@ page import="org.ei.tags.Tag"%><%@ page
	import="org.ei.domain.personalization.GlobalLinks"%><%@ page
	import="org.ei.domain.personalization.SavedSearches"%><%@ page
	errorPage="/error/errorPage.jsp"%>
<%
    ControllerClient client = null;
    String atag = null;
    String tagresult = null;

    if (request.getParameter("tag") != null) {
        atag = request.getParameter("tag");
    } else {
        atag = "a";
    }

    String scopegroup = null;
    if (request.getParameter("scope") != null) {
        scopegroup = request.getParameter("scope");
    }

    int scope = -1;
    String groupID = null;

    if (request.getParameter("scope") != null) {
        String scope_group = request.getParameter("scope");
        if (scope_group.equals("1") || scope_group.equals("2") || scope_group.equals("4")) {
            scope = Integer.parseInt(scope_group);
        } else {
            String[] parts = scope_group.split(":");
            scope = Integer.parseInt(parts[0]);
            groupID = parts[1];
        }
    }

    client = new ControllerClient(request, response);
    UserSession ussession = (UserSession) client.getUserSession();

    String customerId = ussession.getCustomerid().trim();
    String pUserId = ussession.getUserid();

    TagBroker tagBroker = new TagBroker();

    if (atag != null && scopegroup == null) {
        tagresult = tagBroker.autocompleteTags(atag);
    }
    if (atag != null && scopegroup != null) {
        tagresult = tagBroker.autocompleteTags(atag, scope, groupID, customerId, pUserId);
    }
    if (tagresult == null) {
        tagresult = "tagA;tagB";
    }

    out.write("<PAGE>");
    out.write("<HEADER/>");
    out.write("<FOOTER/>");
    out.write("<TAG><![CDATA[" + tagresult + "]]></TAG>");
    out.write("</PAGE>");
    out.write("<!--END-->");
    out.close();
%>