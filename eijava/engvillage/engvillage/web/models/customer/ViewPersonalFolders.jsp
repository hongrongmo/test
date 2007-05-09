<!--
	This page expects the following param
	* @param java.lang.String.database.
	and gets all the folder for the user and
	generates xml for viewing the folders.
-->
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import=" org.ei.domain.personalization.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="java.util.List"%>
<%@ page import="org.ei.domain.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
	// declare variable to hold client session.
	ControllerClient client = null;
	// declare variable to hold xml string.
	StringBuffer sb = null;
	// declare variable to hold user id from session.
	String sUserId = null;
	// declare variable to hold session id.
	String sSessionId = null;
	SessionID sessionIdObj = null;
	// declare variable to hold user id.
	String nUserId  = null;
	// declare variable to hold no of folders.
	int nFolderCount = 0;
	// declare variable to hold list of folders.
	List folderList = null;
	// declare variable to hold database param value.
	String database = null;
	ClientCustomizer clientCustomizer=null;
	// Stores the source attribute of the customized logo image
	String customizedLogo="";
    boolean personalization = false;
    boolean isPersonalizationPresent=true;

	if( request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}

    sb = new StringBuffer("<PAGE>");	
	sb.append("<DATABASE>"+database+"</DATABASE>");

	// get the client session object from that get session id and user id.
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();
	sSessionId = ussession.getID();
	sessionIdObj = ussession.getSessionID();
	sUserId = ussession.getProperty("P_USER_ID");
	
    if((sUserId != null) && (sUserId.length() != 0))
    {
        personalization=true;
    }

	User user=ussession.getUser();

	String customerId=user.getCustomerID().trim();
	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		customizedLogo=clientCustomizer.getLogo();
        isPersonalizationPresent=clientCustomizer.checkPersonalization();		
	}

	// check for user id existance.
	if( sUserId != null)
	{
		nUserId = sUserId;
	}

    // NOV 2004
    // jam added when pop-up login removed 
    sb.append("<HEADER/>");
    sb.append(GlobalLinks.toXML(user.getCartridge()));
    sb.append("<FOOTER/>");

	sb.append("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");

	// if the user exists get all the folders from the system and build xml.
	if( nUserId != null)
	{
		SavedRecords sr = new SavedRecords(nUserId);
		nFolderCount = sr.getFolderCount();
		sb.append("<FOLDERS><NUMBER-OF-FOLDERS>"+nFolderCount+"</NUMBER-OF-FOLDERS>");
		folderList = sr.viewListOfFolders();
		int nListSize = folderList.size();

		for(int i=0; i < nListSize; i++)
		{
			Folder folder = (Folder)folderList.get(i);
			sb.append(folder.toXMLString());
		}
		sb.append("</FOLDERS>");
	}
	sb.append("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
    sb.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
    sb.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");

	sb.append("</PAGE>");

	out.println(sb.toString());
%>
