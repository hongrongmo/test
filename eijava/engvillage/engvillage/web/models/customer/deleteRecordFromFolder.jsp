<!--
 * This file deletes all the  records from the Folder
 * @param java.lang.String.folderId
 * @param java.lang.String.docId

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
   	String database = null;
   	Folder folder=null;
   	List listOfFolderEntries = null;
   	String cid=null;
   	String format=null;
	client = new ControllerClient(request,response);

	String sessionid=null;

	UserSession ussession=(UserSession)client.getUserSession();
	sessionid = ussession.getID();


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

	if(request.getParameter("database") != null)
	{
	    database = request.getParameter("database").trim();
	}

	if(request.getParameter("docid") !=null)
	{
	    docid = request.getParameter("docid").trim();
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

	String strFolderId = folderId;
	String fName = savedRecords.getFolderName(strFolderId);
	folder = new Folder(strFolderId,fName);

	listOfFolderEntries = new ArrayList();

    	docId = new DocID(docid,
    			  databaseConfig.getDatabase(docid.substring(0,3)));
	FolderEntry fe = new FolderEntry(docId);
	listOfFolderEntries.add(fe);


	// Remove all folder entries from the SavedRecords object
	savedRecords.removeSelected(listOfFolderEntries,folder);

	if(cid!=null)
	{
	  urlString = "/controller/servlet/Controller?CID="+cid+"&database="+database+"&folderid="+folderId;
	}

	if(urlString!=null)
	{
		client.setRedirectURL(urlString);
		client.setRemoteControl();
	}

 %>




