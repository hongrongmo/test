<!--
 * This file adds a set of records to the SavedRecords.
 * @param java.lang.String.database
 * @param java.lang.String.docids
 * @param java.lang.String.folderId

-->

<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.URLEncoder"%>

<!--import statements of ei packages.-->

<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.DatabaseConfig"%>
<%@ page import="org.ei.session.*"%>
<%@ page import ="org.ei.config.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>

<%
   	 // Object references
	DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
	//DocID object which is a part of Folder entry
	DocID  docId=null;
	//Controller Client
   	ControllerClient client = null;
   	//This variable for Folder object
   	Folder folder=null;

	//This variable for document id
   	String docid = null;
   	//This variable for database name
   	String database= null;

   	//This variable for Folder Id
   	String folderId=null;
   	//For folder name
   	String folderName="";
   	//This variable for the list of document Ids
   	List listOfDocIds = null;
   	//Session id variable
   	String sessionId=null;
   	SessionID sessionIdObj = null;
   	//used for return variable by the component
   	boolean addFlag=false;
   	String docFormat=null;

    boolean personalization = false;
    boolean isPersonalizationPresent=true;

	// Create a session object using the controllerclient

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();

	sessionId = ussession.getID();
	sessionIdObj = ussession.getSessionID();

	// Varaible to hold the current User id

	String userId = null;
	String sUserId=null;

	sUserId=ussession.getProperty("P_USER_ID");
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


    //  Get all the request parameters
	if(request.getParameter("database") != null)
	{
		database = request.getParameter("database").trim();
	}

	if(request.getParameter("docid") !=null)
	{
		docid = request.getParameter("docid");
	}

	if(request.getParameter("folderid") != null)
	{
		folderId = request.getParameter("folderid").trim();
	}

	if(request.getParameter("foldername") != null)
	{
		folderName = request.getParameter("foldername").trim();
	}


	SavedRecords sr = new SavedRecords(userId);

	//System.out.println(" add to savedRecords.jsp");
	if((folderName.length()!=0) && (folderName!=null))
	{

		folder = new Folder(folderName);
		addFlag = sr.addFolder(folder);
		String  strNewFolderId =sr.getFolderId(folderName);
		folder.setFolderID(strNewFolderId);
	}

	else
	{
		String strNewFolderId = folderId;
		String fName=sr.getFolderName(strNewFolderId);
		folder=new Folder(strNewFolderId,fName);

	}

	if(docid != null)
	{
		listOfDocIds = new ArrayList();
        listOfDocIds.add(docid);

		List DocIDList=new ArrayList();
		String d = null;

		for(int i = 0; i < listOfDocIds.size() ; i++)
		{
			String adocid =  (String) listOfDocIds.get(i);
            docId = new DocID(adocid.trim(),databaseConfig.getDatabase(adocid.substring(0,3)));
			DocIDList.add(docId);
		}

		ArrayList listOfFolderEntries = new ArrayList();
		int count=listOfDocIds.size();

		// Create FolderEntry objects and then create a list of FolderEntries.
		for(int j=0;j<count;j++)
		{
			docId= (DocID) DocIDList.get(j);

			FolderEntry fe = new FolderEntry(docId);
			listOfFolderEntries.add(fe);
		}


		// jam 9/26/2002
		// send in the whole list at once
		// if there is not enough room, none will get added
		// and false will be returned
		addFlag = sr.addSelectedRecords(listOfFolderEntries, folder);

	} //if docid not equal to null


	StringBuffer sbuffer=new StringBuffer();
	sbuffer.append("<PAGE>");
    
    // NOV 2004
    // jam added when pop-up login removed 
    User user=ussession.getUser();
    
    sbuffer.append("<HEADER/>");
    sbuffer.append(GlobalLinks.toXML(user.getCartridge()));
    sbuffer.append("<FOOTER/>");

    String backurl = request.getParameter("backurl");
    if(backurl != null)
    {
	    sbuffer.append("<BACKURL>");
	    sbuffer.append(URLEncoder.encode(backurl));
		sbuffer.append("</BACKURL>");
	}

    sbuffer.append("<CUSTOMIZED-LOGO>").append(customizedLogo ).append("</CUSTOMIZED-LOGO>");
    sbuffer.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
    sbuffer.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");

	sbuffer.append("<FOLDER-NAME>").append(folder.getFolderName()).append("</FOLDER-NAME>");
	sbuffer.append("<FOLDER-ID>").append(folder.getFolderID()).append("</FOLDER-ID>");
	sbuffer.append("<DATABASE-ID>").append(database).append("</DATABASE-ID>");
	sbuffer.append("<SESSION-ID>").append(sessionIdObj.toString()).append("</SESSION-ID>");
	sbuffer.append("</PAGE>");
	out.write(sbuffer.toString());
	out.println("<!--END-->");

	out.flush();




 %>





