<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java"%><%@ page session="false"%><%@ page
	import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page
	import="org.ei.domain.*"%><%@ page
	import="org.engvillage.biz.controller.ControllerClient"%><%@ page
	import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page
	import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page
	import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.*"%><%@ page
	import="org.ei.domain.personalization.GlobalLinks"%><%@ page
	import="org.ei.domain.personalization.SavedSearches"%><%@ page
	errorPage="/error/errorPage.jsp"%>
<%
    ControllerClient client = null;
    String database = null;
    String customizedLogo = "";
    boolean isPersonalizationPresent = true;
    boolean personalization = false;
    String oldtag = null;
    String newtag = null;
    client = new ControllerClient(request, response);
    UserSession ussession = (UserSession) client.getUserSession();

    String pUserId = ussession.getUserid();

    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);

    if (clientCustomizer.isCustomized()) {
        isPersonalizationPresent = clientCustomizer.checkPersonalization();
        customizedLogo = clientCustomizer.getLogo();
    }

    if ((pUserId != null) && (pUserId.trim().length() != 0)) {
        personalization = true;
    } else {
        /*
         *	No personal account so redirect to the login screen.
         */
        client
            .setRedirectURL("/controller/servlet/Controller?CID=personalLoginForm&displaylogin=true&database=27&nexturl=CID%3DrenameTag&backurl=CID%3DrenameTag");
        client.setRemoteControl();
        return;
    }

    database = clientCustomizer.getDefaultDB();

    if (request.getParameter("oldtag") != null) {
        oldtag = request.getParameter("oldtag");
    }
    if (request.getParameter("newtag") != null) {
        newtag = request.getParameter("newtag");
    }

    String customerId = ussession.getCustomerid();
    TagBroker tagBroker = new TagBroker();
    int scopeint = 1;
    String groupID = null;
    String scope = request.getParameter("scope");
    if (scope != null) {
        if (!scope.equals("1") && !scope.equals("2") && !scope.equals("4")) {
            scopeint = 3;
            groupID = scope;
        } else {
            scopeint = Integer.parseInt(scope);
        }
    }

    if (newtag != null && oldtag != null) {
        tagBroker.updateTagName(oldtag, newtag, pUserId, customerId, groupID, scopeint);
    }

    String[] taglabels = tagBroker.getUserTagNames(scopeint, pUserId, customerId, groupID);

    String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());

    out.write("<PAGE>");

    if ((pUserId != null) && (pUserId.trim().length() != 0)) {
        TagGroupBroker groupBroker = new TagGroupBroker();
        TagGroup[] tgroups = groupBroker.getGroups(pUserId, false);
        if (tgroups != null && tgroups.length > 0) {
            out.write("<TGROUPS>");
            for (int i = 0; i < tgroups.length; i++) {
                tgroups[i].toXML(out);
            }
            out.write("</TGROUPS>");
        }
    }

    out.write("<SCOPE>");

    if (scope == null || scope.trim().equals("")) {
        scope = "1";
    }
    out.write(scope);
    out.write("</SCOPE>");
    out.write("<SESSION-ID>" + ussession.getSessionid() + "</SESSION-ID>");
    out.write("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
    out.write("<PERSONALIZATION-PRESENT>" + isPersonalizationPresent + "</PERSONALIZATION-PRESENT>");
    out.write("<PERSONALIZATION>" + personalization + "</PERSONALIZATION>");
    out.write("<HEADER/>");
    out.write(strGlobalLinksXML);
    out.write("<FOOTER/>");
    out.write("<DBMASK>" + database + "</DBMASK>");
    out.write("<ADDTAGSELECTRANGE-NAVIGATION-BAR/>");
    out.write("<GROUPS-NAVIGATION-BAR/>");
    out.write("<TAGS>");
    for (int i = 0; i < taglabels.length; i++) {
        out.write("<TAG><![CDATA[");
        out.write(taglabels[i]);
        out.write("]]></TAG>");
    }
    out.write("</TAGS>");
    out.write("</PAGE>");
    out.write("<!--END-->");
    out.flush();
%>