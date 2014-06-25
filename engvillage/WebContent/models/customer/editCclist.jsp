<!--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.databaseName
 -->
<%@ page language="java" %>
<%@ page session="false" %>
<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>

<!--import statements of ei packages.-->
<%@ page import="javax.servlet.jsp.*" %>
<%@ page import="org.ei.connectionpool.*" %>
<%@ page import="org.ei.controller.ControllerClient" %>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.util.StringUtil" %>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
  /**
   * This page contains SavedSearches records xml for that user.
   */

	// This variable for sessionId
	// jam 9/24/2002 - Changed from String to SessionID object
	//String sessionId = null;
  	SessionID sessionId=null;

	// This variable for Database name
	String dbName=null;
	// This variable for userId
	String userId=null;
	//This variables are used for customization.
	ClientCustomizer clientCustomizer=null;
	boolean isPersonalizationPresent=true;
	boolean isEmailAlertsPresent=true;
	boolean isEmailccListPresent=false;
	String customizedLogo="";
	
	// This variable is used to hold ControllerClient instance
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	//client.updateUserSession(ussession);
	//sessionId=ussession.getID();
	// jam 9/24/2002 - Changed from String to SessionID object
	sessionId = ussession.getSessionID();

	userId = ussession.getUserIDFromSession();

	 IEVWebUser user = ussession.getUser();
	String customerId=user.getCustomerID().trim();
	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		isEmailAlertsPresent=clientCustomizer.checkEmailAlert();
		isEmailccListPresent=clientCustomizer.checkEmailccList();
	}
  // Getting database name as request parameter.
	if(request.getParameter("database")!=null)
	{
		dbName = request.getParameter("database").trim();
	}

	String savedsearchid = request.getParameter("savedsearchid");

	if(userId != null && userId.trim().length()>0)
  	{

		String action = request.getParameter("action");
		if(action != null)
		{
			String list = request.getParameter("list");
			if("deletecclist".equalsIgnoreCase(action))
			{
			  list = StringUtil.EMPTY_STRING;
			}
			SavedSearches.storeSavedSearchCCList(userId, savedsearchid, list);

			// jam - output is used for pop-up window after saving
			// no need to output any other data than this here and then stop
			out.write("<PAGE>");
			out.write("<SESSION-ID>"+sessionId.toString()+"</SESSION-ID>");
			out.write("<USERID>"+userId+"</USERID>");
			out.write("<DBMASK>"+dbName+"</DBMASK>");
			out.write("<CONFIRM>"+action+"</CONFIRM>");
			out.write("<SAVEDSEARCH-ID>"+savedsearchid+"</SAVEDSEARCH-ID>");
			out.write("</PAGE>");
			out.println("<!--END-->");
			out.flush();
			return;
		}

		//FileWriter out1 = new FileWriter("/temp/test.xml");

		//Writting out xml for that sessionId
		String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

		out.write("<PAGE>");
		out.write("<HEADER/>");
		out.write("<FOOTER/>");
		out.write("<USERID>"+userId+"</USERID>");
		out.write("<SESSION-ID>"+sessionId.toString()+"</SESSION-ID>");
		out.write("<DBMASK>"+dbName+"</DBMASK>");

		// send in writer instead of returning string
		SavedSearches.getXMLCcList(savedsearchid, out);

		out.write("</PAGE>");

		//out1.close();

	}
	else
	{
     	String  urlString = "/controller/servlet/Controller";
	  	client.setRedirectURL(urlString);
  		client.setRemoteControl();
	}

%>