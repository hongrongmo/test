<%--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String
--%>
<%@ page language="java" %>
<%@ page session="false"%>
<%@ page contentType="text/xml"%>

<%-- import statements of Java packages --%>
<%@ page import="java.util.*"%>

<%-- import statements of ei packages. --%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.navigators.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>
<%

	SessionID sessionIdObj = null;

	// Variable to hold the Personalization userid
	String pUserId = null;
	// Variable to hold the Personalizaton status
	boolean personalization = false;

	// variable to hold database name
	String database = null;
	String count = null;
	String searchId = null;
	String searchType = null;
	String resultsCount = null;

	//variable to hold personalization feature
	boolean  isPersonalizationPresent=true;
	String customizedLogo="";

	String refEmail = "";


	ControllerClient client = new ControllerClient(request, response);


	UserSession ussession=(UserSession)client.getUserSession();

	//client.updateUserSession(ussession);
	sessionIdObj = ussession.getSessionID();
	String sessionId = ussession.getID();

	pUserId = ussession.getProperty("P_USER_ID");
	if((pUserId != null) && (pUserId.trim().length() != 0)){
		personalization=true;
	}
    User user=ussession.getUser();
	ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);

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
	
	searchId = request.getParameter("SEARCHID");
	database = request.getParameter("database");
	count = request.getParameter("COUNT");
	searchType = request.getParameter("SEARCHTYPE");
	resultsCount=request.getParameter("RESULTSCOUNT");
	
	//DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
	NavigatorCache navcache = new NavigatorCache(sessionId);
	ResultNavigator nav = navcache.getFromCache(searchId);
	//List strDb =
	List dedupableDb =  nav.getDeDupableDBList();
	/*

	for(int i = 0; i < strDb.size(); i++)
	{
		System.out.println((String)strDb.get(i));
		dedupableDb.add(databaseConfig.getDatabase((String)strDb.get(i)));
	}
	*/

	client.setRemoteControl();

	String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

	

	out.write("<PAGE>");
	out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
	out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
	out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
	out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
	out.write("<REFEMAIL>"+refEmail+"</REFEMAIL>");
	out.write("<HEADER/>");
	out.write(strGlobalLinksXML);
	out.write("<FOOTER/>");
	out.write("<DBMASK>"+database+"</DBMASK>");
	out.write("<COUNT>"+count+"</COUNT>");
	out.write("<SEARCH-ID>"+searchId+"</SEARCH-ID>");
	out.write("<SEARCH-TYPE>"+searchType+"</SEARCH-TYPE>");
	out.write("<RESULTS-COUNT>"+resultsCount+"</RESULTS-COUNT>");
	out.write("<DEDUPFORM-NAVIGATION-BAR/>");
	Iterator it = dedupableDb.iterator();
	out.write("<DEDUPABLE-DB>");

	while(it.hasNext())
	{
		Database db = (Database)it.next();
		out.write("<DB ID=\""+db.getID()+"\" NAME=\""+db.getName()+"\" />");
	}
	out.write("</DEDUPABLE-DB>");
	out.write("</PAGE>");
%>
