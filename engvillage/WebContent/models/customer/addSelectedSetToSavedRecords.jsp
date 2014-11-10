<!--
 *  jsp file that is required to add docids from the selected set
 * takes the basket count parameter talks to the basket gets the docId's
 * and adds to the savedrecords
 * @param java.lang.String.folderid
 * @param java.lang.String.basketcount
 * @param java.lang.String.foldername

-->
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>

<%@ page import="java.util.* "%>
<%@ page import="java.net.URLEncoder "%>

<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>

<%@ page errorPage="/error/errorPage.jsp" %>

<% //Basket Page variable that is returned by the PageAt method
	BasketPage basketPage=null;
	//Folder objecet
	Folder folder=null;
	//Variable for basket count
	String basketCount=null;
	//String folder Id
	String folderId=null;
	//String folder Name
	String folderName="";
	//Var for sessionid
	String sessionId=null;
	//flag
	boolean addFlag=false;
	//Folder entry list
	List listOfFolderEntries=null;
	//string for document format
	String database=null;

    boolean personalization = false;
    boolean isPersonalizationPresent=true;

	if(request.getParameter("folderid") != null)
	{
		folderId = request.getParameter("folderid").trim();
	}

	if(request.getParameter("foldername") != null)
	{
		folderName = request.getParameter("foldername").trim();
	}

	if(request.getParameter("database") != null)
	{
		database = request.getParameter("database").trim();
	}
   // Create a session object using the controllerclient.java
	ControllerClient client = new ControllerClient(request, response);

	UserSession ussession=(UserSession)client.getUserSession();
	sessionId = ussession.getSessionid();

	// Varaible to hold the current User id

	String userId = null;
	String sUserId=null;

	sUserId=ussession.getUserid();

    if((sUserId != null) && (sUserId.length() != 0))
    {
        personalization=true;
    }

    ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
    String customizedLogo="";
    if(clientCustomizer.isCustomized())
    {
        customizedLogo=clientCustomizer.getLogo();
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
    }



	if( sUserId != null)
	{
		userId = sUserId;
	}

	SavedRecords sr = new SavedRecords(userId);


	if((folderName!=null) && (folderName.length()!=0) )
	{
		folder = new Folder(folderName);
		addFlag = sr.addFolder(folder);
		String strNewFolderId =sr.getFolderId(folderName);
		folder.setFolderID(strNewFolderId);
	}

	else
	{
		String strNewFolderId = folderId;
		String fName=sr.getFolderName(strNewFolderId);
		folder=new Folder(strNewFolderId,fName);

	}



	DocumentBasket basket=new DocumentBasket(sessionId);
	List docIDs =basket.getAllDocIDs();
	int count=docIDs.size();

	// jam 9/30/2002
	// Changed to use Add All
	// Create FolderEntry objects and add to
	// list of FolderEntries.

	listOfFolderEntries = new ArrayList();

	for(int j=0;j<count;j++)
	{
		DocID docId=(DocID)docIDs.get(j);
		FolderEntry fe = new FolderEntry(docId);
		listOfFolderEntries.add(fe);
	}

	if(listOfFolderEntries!=null)
	{
		addFlag=sr.addSelectedRecords(listOfFolderEntries,folder);
	}


	StringBuffer sbuffer=new StringBuffer();
	sbuffer.append("<PAGE>");

    // NOV 2004
    // jam added when pop-up login removed
    sbuffer.append("<HEADER/>");
    sbuffer.append(GlobalLinks.toXML(ussession.getCartridge()));
    sbuffer.append("<FOOTER/>");

    String backurl = request.getParameter("backurl");
    if(backurl != null)
    {
	    sbuffer.append("<BACKURL>");
	    sbuffer.append(URLEncoder.encode(backurl,"UTF-8"));
		sbuffer.append("</BACKURL>");
	}

    sbuffer.append("<CUSTOMIZED-LOGO>").append(customizedLogo ).append("</CUSTOMIZED-LOGO>");
    sbuffer.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
    sbuffer.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");

	sbuffer.append("<FOLDER-NAME>").append(folder.getFolderName()).append("</FOLDER-NAME>");
	sbuffer.append("<FOLDER-ID>").append(folder.getFolderID()).append("</FOLDER-ID>");
	sbuffer.append("<DATABASE-ID>").append(database).append("</DATABASE-ID>");
	sbuffer.append("<SESSION-ID>").append(sessionId).append("</SESSION-ID>");
	sbuffer.append("</PAGE>");
	out.write(sbuffer.toString());
	out.println("<!--END-->");

	out.flush();


%>



