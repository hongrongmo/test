<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="java.net.URLEncoder"%><%@ page import="org.ei.domain.*"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.session.*"%><%@ page import="org.ei.config.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.*"%><%@ page import="org.ei.domain.personalization.GlobalLinks"%><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page  errorPage="/error/errorPage.jsp"%><%
	SessionID sessionIdObj = null;
	Database databaseObj = null;
	ControllerClient client = null;
	String sessionId  = null;
	String database = null;
	String dedupSet = null;
	String customizedLogo="";
	String refEmail = "";
	boolean  isPersonalizationPresent=true;
	boolean personalization = false;
	List docIDList = null;
	String groupid = null;
%><%!
    int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);
    int pagesize = 0;
    int dedupSetSize = 0;
    DatabaseConfig databaseConfig = null;

    public void jspInit()
    {
        try
        {
            RuntimeProperties runtimeProps = ConfigService.getRuntimeProperties();
            pagesize = Integer.parseInt(runtimeProps.getProperty("PAGESIZE"));
            dedupSetSize = Integer.parseInt(runtimeProps.getProperty("DEDUPSETSIZE"));

            databaseConfig = DatabaseConfig.getInstance();

            // jam Y2K3
            customizedEndYear = Integer.parseInt(runtimeProps.getProperty("SYSTEM_ENDYEAR"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
%><%

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionIdObj = ussession.getSessionID();
	sessionId = ussession.getID();
	User user=ussession.getUser();
	String[] credentials = user.getCartridge();
	String edit = null;

	String pUserId = ussession.getProperty("P_USER_ID");

	ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
	database = clientCustomizer.getDefaultDB();

	if(clientCustomizer.getRefEmail() != null &&
        clientCustomizer.getRefEmail().length()>0)
    {
        refEmail = clientCustomizer.getRefEmail();
    }

    if(clientCustomizer.isCustomized())
    {
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
        customizedLogo=clientCustomizer.getLogo();
    }

	if((pUserId != null) && (pUserId.trim().length() != 0))
	{
		personalization=true;
	}

	if(request.getParameter("groupid") != null)
	{
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
    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

	out.write("<PAGE>");
	out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
	out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
	out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
	out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
	out.write("<REFEMAIL>"+refEmail+"</REFEMAIL>");
	out.write("<DBMASK>"+database+"</DBMASK>");
	out.write("<HEADER/>");
	out.write(strGlobalLinksXML);
	out.write("<FOOTER/>");
	out.write("<DBMASK>"+database+"</DBMASK>");
	out.write("<ADDTAGSELECTRANGE-NAVIGATION-BAR/>");
	out.write("<GROUPS-NAVIGATION-BAR/>");

	Color.getInstance().toXML(out);

	out.write("</PAGE>");
	out.write("<!--END-->");
	out.flush();
%>
