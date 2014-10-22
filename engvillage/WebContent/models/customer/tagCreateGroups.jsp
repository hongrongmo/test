<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@page import="org.ei.config.ApplicationProperties"%>
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
    Database databaseObj = null;
    ControllerClient client = null;
    String sessionId = null;
    String database = null;
    String dedupSet = null;
    String customizedLogo = "";
    String refEmail = "";
    boolean isPersonalizationPresent = true;
    boolean personalization = false;
    List docIDList = null;
    String groupid = null;
%><%!int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);
    int pagesize = 0;
    int dedupSetSize = 0;
    DatabaseConfig databaseConfig = null;

    public void jspInit() {
        try {
            ApplicationProperties runtimeProps = ApplicationProperties.getInstance();
            pagesize = Integer.parseInt(runtimeProps.getProperty("PAGESIZE"));
            dedupSetSize = Integer.parseInt(runtimeProps.getProperty("DEDUPSETSIZE"));

            databaseConfig = DatabaseConfig.getInstance();

            // jam Y2K3
            customizedEndYear = Integer.parseInt(runtimeProps.getProperty("SYSTEM_ENDYEAR"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }%>
<%
    client = new ControllerClient(request, response);
    UserSession ussession = (UserSession) client.getUserSession();
    sessionId = ussession.getSessionid();
    String[] credentials = ussession.getCartridge();
    String edit = null;

    String pUserId = ussession.getUserid();

    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);
    database = clientCustomizer.getDefaultDB();

    if (clientCustomizer.getRefEmail() != null && clientCustomizer.getRefEmail().length() > 0) {
        refEmail = clientCustomizer.getRefEmail();
    }

    if (clientCustomizer.isCustomized()) {
        isPersonalizationPresent = clientCustomizer.checkPersonalization();
        customizedLogo = clientCustomizer.getLogo();
    }

    if ((pUserId != null) && (pUserId.trim().length() != 0)) {
        personalization = true;
    }

    if (request.getParameter("groupid") != null) {
        groupid = request.getParameter("groupid");
    }

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
    String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());

    out.write("<PAGE>");
    out.write("<SESSION-ID>" + sessionId + "</SESSION-ID>");
    out.write("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
    out.write("<PERSONALIZATION-PRESENT>" + isPersonalizationPresent + "</PERSONALIZATION-PRESENT>");
    out.write("<PERSONALIZATION>" + personalization + "</PERSONALIZATION>");
    out.write("<REFEMAIL>" + refEmail + "</REFEMAIL>");
    out.write("<DBMASK>" + database + "</DBMASK>");
    out.write("<HEADER/>");
    out.write(strGlobalLinksXML);
    out.write("<FOOTER/>");
    out.write("<DBMASK>" + database + "</DBMASK>");
    out.write("<ADDTAGSELECTRANGE-NAVIGATION-BAR/>");
    out.write("<GROUPS-NAVIGATION-BAR/>");

    Color.getInstance().toXML(out);

    out.write("</PAGE>");
    out.write("<!--END-->");
    out.flush();
%>
