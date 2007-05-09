<%--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.databaseName
 --%>
<%@ page language="java" %>
<%@ page session="false" %>
<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<!--import statements of ei packages.-->
<%@ page import="javax.servlet.jsp.*" %>
<%@ page import="org.ei.connectionpool.*" %>
<%@ page import="org.ei.controller.ControllerClient" %>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
  /**
   * This page contains Saved Searche records xml for that user.
   */

	// This variable for sessionId
	// jam 9/24/2002 - Changed from String to SessionID object
	//String sessionId = null;
    	SessionID sessionId=null;

	// This variable for Database name
	String dbName=null;
	// This variable for userId
	String userId=null;
	//This variable for instance of SvaedSearches

	//This variables are used for customization.
	ClientCustomizer clientCustomizer=null;
	boolean isPersonalizationPresent=true;
	boolean isEmailAlertsPresent=true;
	boolean isCcEmailAlertsPresent=false;
	
	String customizedLogo="";
    String show=null;

	// This variable is used to hold ControllerClient instance
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	//client.updateUserSession(ussession);
	//sessionId=ussession.getID();
	// jam 9/24/2002 - Changed from String to SessionID object
	sessionId=ussession.getSessionID();

	userId = ussession.getProperty("P_USER_ID");

	User user=ussession.getUser();
	String customerId=user.getCustomerID().trim();
	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
		isEmailAlertsPresent=clientCustomizer.checkEmailAlert();
		isCcEmailAlertsPresent=clientCustomizer.checkEmailccList();
		customizedLogo=clientCustomizer.getLogo();
	}
    //Getting database name as request parameter.
	if(request.getParameter("database")!=null)
	{
		dbName = request.getParameter("database");
	}

	if(request.getParameter("show") != null)
	{
		show = request.getParameter("show");
	}

	if(userId!=null && userId.trim().length()>0)
  	{

    	// Getting savedSearches object for sessionId and userId
    	// jam 9/24/2002 - added to String on new SessionID object
    	// for correct session ID with version number prepended
    	
    
        //Writting out xml for that sessionId
    	String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

		out.write("<PAGE>");
		out.write("<HEADER/>");
		out.write("<FOOTER/>");
		out.write(strGlobalLinksXML);
		out.write("<NAVIGATION-BAR/>");
		out.write("<PERSONALIZATION>true</PERSONALIZATION>");
		// jam 9/24/2002 - added to String on new SessionID object
		// for correct session ID with version number prepended
		out.write("<SESSION-ID>"+sessionId.toString()+"</SESSION-ID>");
		out.write("<DBMASK>"+dbName+"</DBMASK>");
		out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
		out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
  		out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
  		out.write("<CCEMAILALERTS-PRESENT>"+isCcEmailAlertsPresent+"</CCEMAILALERTS-PRESENT>");
		//out.write(Searches.getUserSavedSearchesXML(userId));

		if((show != null) && show.equalsIgnoreCase("alerts"))
		{
    		out.write("<SHOW>alerts</SHOW>");
		    Searches.getUserAlertsXML(userId,out);
		}
		else
		{
    		out.write("<SHOW>all</SHOW>");
		    Searches.getUserSavedSearchesXML(userId,out);
		}
		

        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        String[] credentials = user.getCartridge();
        databaseConfig.sortableToXML(credentials, out);

		out.write("</PAGE>");
		
	}
	else
	{
     	String  urlString = "/controller/servlet/Controller?CID=quickSearch&database="+dbName;
	  	client.setRedirectURL(urlString);
  		client.setRemoteControl();
	}

%>