<!--
 * This file deletes all the  records from the Folder
 * @param java.lang.String.folderId
 -->
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page  import=" java.util.*"%>
<%@ page  import=" java.net.*"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>

<%@ page  errorPage="/error/errorPage.jsp"%>

<%
	DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
	DocID  docId=null;
   	ControllerClient client = null;
   	SavedRecords savedRecords=null;
   	String urlString=null;
   	String folderId = null;
   	String docid=null;
   	Folder folder=null;
   	List listOfFolderEntries = null;
   	String cid=null;
   	String format=null;
   	String database = request.getParameter("database");
   	String sessionid=null;

	client = new ControllerClient(request,response);


	UserSession ussession=(UserSession)client.getUserSession();

	sessionid = ussession.getID();


	// Varaible to hold the current User id
	String userId = null;
	String sUserId=null;

	sUserId=ussession.getProperty("P_USER_ID");

	if( sUserId != null)
	{
		userId = sUserId;
	}
	if(request.getParameter("folderid") != null)
	{
	     folderId = request.getParameter("folderid").trim();
	}

	if(request.getParameter("format")!=null)
	{
		format = request.getParameter("format").trim();
	}



	//comparing format variable and setting it to cid

    if( (format!=null) &&  (format.equals("citation"))  )
	{
		cid="viewCitationSavedRecords";

	 }
	 else if( (format!=null) &&  (format.equals("abstract")) )
	 {
		 cid="viewAbstractSavedRecords";
	 }
	 else if( (format!=null) &&  (format.equals("detailed"))  )
	 {
		 cid="viewDetailedSavedRecords";
	 }


	savedRecords = new SavedRecords(userId);
	String strNewFolderId  = folderId;
	String fName=savedRecords.getFolderName(strNewFolderId);
	folder=new Folder(strNewFolderId,fName);

	// Remove all folder entries from the SavedRecords object
	savedRecords.removeAllInFolder(folder);

	urlString = "/controller/servlet/Controller?CID=viewCitationSavedRecords&database="+database+"&folderid="+folderId;

	if(urlString!=null)
	{
		client.setRedirectURL(urlString);
		client.setRemoteControl();
	}

 %>





