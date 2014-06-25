<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page import="org.ei.domain.*"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.*"%><%@ page import="org.ei.domain.personalization.GlobalLinks"%><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page  errorPage="/error/errorPage.jsp"%><%
	
	SessionID sessionIdObj = null;
	ControllerClient client = null;
	String sessionId  = null;
	String tagname = null;
	String customizedLogo="";
	boolean  isPersonalizationPresent=true;
	boolean personalization = false;
	String groupid = null;
	String database = null;
%><%

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionIdObj = ussession.getSessionID();
	sessionId = ussession.getID();
	 IEVWebUser user = ussession.getUser();
	String[] credentials = user.getCartridge();
	String pUserId = ussession.getUserIDFromSession();
	ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
	database = clientCustomizer.getDefaultDB();

    if(clientCustomizer.isCustomized())
    {
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
        customizedLogo=clientCustomizer.getLogo();
    }
	if((pUserId != null) && (pUserId.trim().length() != 0))
	{
		personalization=true;
	}
	if(request.getParameter("tagname") != null)
	{
    	tagname = request.getParameter("tagname");
	}
	
	if(request.getParameter("groupid") != null)
	{
		groupid = request.getParameter("groupid");
	}
	TagGroupBroker groupBroker = new TagGroupBroker();
	TagGroup group = groupBroker.getGroup(groupid, true);

/*
        client.log("SEARCH_ID", searchID);
        client.log("QUERY_STRING", tQuery.getDisplayQuery());
        client.log("DB_NAME", Integer.toString(did.getDatabase().getMask()));
        client.log("PAGE_SIZE", Integer.toString(pagesize));
        client.log("HIT_COUNT", totalDocCount);
        client.log("NUM_RECS", "1");
        client.log("context", "search");
        client.log("DOC_INDEX", (new Integer(index)).toString());
        client.log("DOC_ID", did.getDocID());
        client.log("FORMAT", dataFormat);
        client.log("ACTION", "document");
        client.setRemoteControl();
*/
    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

	out.write("<PAGE>");
	out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
	out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
	out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
	out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
	out.write("<HEADER/>");
	out.write("<DBMASK>"+database+"</DBMASK>");
	out.write(strGlobalLinksXML);
	out.write("<FOOTER/>");
	out.write("<ADDTAGSELECTRANGE-NAVIGATION-BAR/>");
	out.write("<GROUPS-NAVIGATION-BAR/>");
	group.toXML(out);
	Color color = Color.getInstance();
	color.toXML(out);
	out.write("</PAGE>");
	out.write("<!--END-->");
	out.flush();
	%>
