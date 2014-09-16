<!--
 * Intermediate jsp file that is required to carry on the docids from the serach results
 * to the savetofolder.xsl */
 * @param java.lang.String.searchid
 * @param java.lang.String.docids
 * @param java.lang.String.databaseid
-->
<%@page import="org.ei.config.ApplicationProperties"%>
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page buffer="20kb"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>

<!--import statements of ei packages.-->
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>

<%@ page errorPage="/error/errorPage.jsp" %>
<%
	//Used for the session
	String sessionId="";
	//used for the database
	String database="";
	//the docid got from xsl
	String docids="";
	//the string buffer that is used for the xml
	StringBuffer xmlString=new StringBuffer();
	//To hold the list of folders
	List folderList=new ArrayList();
	//For the basket count
	String source=null;
	//string count
	String count=null;
	//flag
	boolean selectedSet=false;

	// This variable is used to hold ControllerClient instance
	 ControllerClient client = new ControllerClient(request, response);

    /**
     *  Getting the UserSession object from the Controller client .
     *  Getting the session id from the usersession.
     */
	UserSession ussession=(UserSession)client.getUserSession();
	//client.updateUserSession(ussession);
	sessionId=ussession.getSessionid();

	// Varaible to hold the current User id
	String userId = null;
	String sUserId=null;
    boolean personalization = false;
    boolean isPersonalizationPresent=true;

    ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
    String customizedLogo="";
    if(clientCustomizer.isCustomized())
    {
        customizedLogo=clientCustomizer.getLogo();
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
    }

	sUserId=ussession.getUserid();
    if((sUserId != null) && (sUserId.length() != 0))
    {
        personalization=true;
    }

	//client.updateUserSession(ussession);
	DocumentBasket basket=new DocumentBasket(sessionId);
	int basketSize=0;

	if( sUserId != null)
	{
		userId = sUserId;
	}

	if(request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}

	String backurl = request.getParameter("backurl");
	String docid = request.getParameter("docid");

	if(request.getParameter("source") != null)
	{
		source = request.getParameter("source");
		if(source.equals("selectedset"))
		{
			selectedSet=true;
	 		basketSize=basket.getBasketSize();
		}
	}
	if(request.getParameter("count") != null)
	{
		count = request.getParameter("count");
	}

	ApplicationProperties eiProps = ApplicationProperties.getInstance();
	int maxFolderSize=0;
	maxFolderSize = Integer.parseInt(eiProps.getProperty("MAXFOLDERSIZE"));

	SavedRecords sr=new SavedRecords(userId);
	folderList=sr.viewListOfFolders();

	xmlString.append("<PAGE>");

    if(backurl != null)
    {
	    xmlString.append("<BACKURL>");
	    xmlString.append(URLEncoder.encode(backurl));
		xmlString.append("</BACKURL>");
	}

    // NOV 2004
    // jam added when pop-up login removed

    xmlString.append("<HEADER/>");
    xmlString.append(GlobalLinks.toXML(ussession.getCartridge()));
    xmlString.append("<FOOTER/>");

	xmlString.append("<DATABASE>").append(database).append("</DATABASE>");
	xmlString.append("<SESSION-ID>").append(sessionId).append("</SESSION-ID>");
	xmlString.append("<USER-ID>").append(userId).append("</USER-ID>");
	xmlString.append("<MAX-FOLDER-SIZE>").append(maxFolderSize).append("</MAX-FOLDER-SIZE>");

	if(folderList != null)
	{
    	xmlString.append("<FOLDERS>");
		Iterator itr = folderList.iterator();
		while(itr.hasNext())
		{
            Folder tempFolder = (Folder) itr.next();
            xmlString.append(tempFolder.toXMLString());
		}
	    xmlString.append("</FOLDERS>");
    	xmlString.append("<NUMBER-OF-FOLDERS>").append(folderList.size()).append("</NUMBER-OF-FOLDERS>");
	}

    if(docid != null)
    {
	    xmlString.append("<DOCID>").append(docid).append("</DOCID>");
	}
	xmlString.append("<COUNT>").append(count).append("</COUNT>");
	xmlString.append("<SELECTED-SET>").append(selectedSet).append("</SELECTED-SET>");
	xmlString.append("<BASKET-SIZE>").append(basketSize).append("</BASKET-SIZE>");
    xmlString.append("<CUSTOMIZED-LOGO>").append(customizedLogo ).append("</CUSTOMIZED-LOGO>");
    xmlString.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
    xmlString.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");

	xmlString.append("</PAGE>");

	out.write(xmlString.toString());
	//out.println("<!--END-->");
%>
