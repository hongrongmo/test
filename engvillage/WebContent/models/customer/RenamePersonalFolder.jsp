<!--
	This page expects the following params based on the request
	* @param java.lang.String.database.
	* @param java.lang.String.folderedit.
	* @param java.lang.String.foldername.
	* @param java.lang.String.folderid.
	and rename the old forname with the new one
	and redirects to the view folders page.
-->
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>

<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>

<%@ page import="java.util.List"%>

<%@ page errorPage="/error/errorPage.jsp"%>


<%
    boolean personalization = false;
    boolean isPersonalizationPresent=true;
	ControllerClient client = null;
	// declare variable to hold user id from session.
	String sUserId = null;
	// declare variable to hold session id.
	String sSessionId = null;
	// declare variable to hold user id.

	String nUserId  = null;
	// declare variable to hold folder name.
	String sFolderName  = null;

	String strFolderId = "";

	// declare variable to hold rename flag.
	boolean renameFlag = false;
	// declare variable to hold redirect string.
	String urlString = null;
	// declare variable to hold folder edit param value.
	String folderEdit = null;
	// declare variable to hold xml string.
	StringBuffer sb = new StringBuffer();
	// declare variable to hold list of folders.
	List folderList = null;
	// declare variable to hold database param value.
	String database = null;

	ClientCustomizer clientCustomizer=null;
	// Stores the source attribute of the customized logo image
	String customizedLogo="";

	if( request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}
	//get the client session object and from that get the session id and user id.
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();
	sSessionId = ussession.getSessionid();

	sUserId = ussession.getUserid();
	//client.updateUserSession(ussession);

	//check for user id existance.
	if( sUserId != null)
	{
		nUserId = sUserId;
		personalization=true;
	}

	String customerId=ussession.getCustomerid().trim();
	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		customizedLogo=clientCustomizer.getLogo();
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
	}

	if( request.getParameter("folderedit") != null)
	{
		folderEdit = request.getParameter("folderedit");
	}
	// display the edit form to rename the folder name
	if( folderEdit != null)
	{
    	if( request.getParameter("oldfoldername") != null)
    	{
    		sFolderName = request.getParameter("oldfoldername");
    	}
    	if( request.getParameter("folderid") != null)
    	{
    		strFolderId = request.getParameter("folderid");
    	}
    	//build the xml to display
    	sb.append("<PAGE>");
    	sb.append("<HEADER/>");
    	sb.append(GlobalLinks.toXML(ussession.getCartridge()));
    	sb.append("<FOOTER/>");

    	sb.append("<SESSION-ID>"+sSessionId+"</SESSION-ID>");

    	if( nUserId != null)
    	{
			SavedRecords sr = new SavedRecords(nUserId);
			sb.append("<FOLDERS>");
			folderList = sr.viewListOfFolders();
			int nListSize = folderList.size();

			for(int i=0; i < nListSize; i++)
			{
				Folder folder = (Folder)folderList.get(i);
				sb.append(folder.toXMLString());
			}
			sb.append("</FOLDERS>");
    	}
    	sb.append("<DATABASE>"+database+"</DATABASE>");
    	sb.append("<FOLDER>");
    	sb.append("<FOLDER-NAME>"+sFolderName+"</FOLDER-NAME>");
    	sb.append("<FOLDER-ID>"+strFolderId+"</FOLDER-ID>");
    	sb.append("</FOLDER>");
    	sb.append("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
    	sb.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
    	sb.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");
    	sb.append("</PAGE>");
    	out.println(sb.toString());
	}
	else
	{
		if( request.getParameter("newfoldername") != null)
		{
			sFolderName = request.getParameter("newfoldername");
		}
		if( request.getParameter("folderid") != null)
		{
			strFolderId = request.getParameter("folderid");
		}
		//update folder information.
		SavedRecords sr = new SavedRecords(nUserId);
		Folder folder = new Folder(strFolderId,sFolderName);
		renameFlag = sr.renameFolder(folder,folder);
		if(renameFlag)
		{
			urlString = "/controller/servlet/Controller?CID=viewPersonalFolders&database="+database;
		}
		// redirect to the view folders page.
		if(urlString!=null)
		{
			client.setRedirectURL(urlString);
			client.setRemoteControl();
		}
	}

%>
