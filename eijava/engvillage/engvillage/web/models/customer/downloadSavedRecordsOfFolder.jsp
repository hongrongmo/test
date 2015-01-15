<%--
 * jsp file that is required to view records in a folder
 * to the savetofolder.xsl */
 * @param java.lang.String.folderid
 * @param java.lang.String.cid
 * @param java.lang.String.databse
--%>
<%@ page language="java" %>
<%@ page session="false" %>

<%-- import statements of Java packages--%>
<%@ page  import=" java.util.*"%>
<%@ page  import=" java.net.*"%>
<%--import statements of ei packages.--%>
<%@ page import ="org.ei.domain.personalization.*"%>
<%@ page import ="org.ei.config.*"%>
<%@ page import ="org.ei.domain.*"%>
<%@ page import ="org.ei.controller.ControllerClient"%>
<%@ page import ="org.ei.session.*" %>
<%@ page errorPage="/error/errorPage.jsp" %>
<%--setting page buffer size to 20kb--%>
<%@ page buffer="20kb"%>
<%

	int folderSize = 0 ;
	// Variable to hold the default CID for the Saved Record.
	String cid=null;
	// Variable to hold the database name for which the search results were obtained.
	// eg. Compendex Chemistry,
	String database=null;
	// Variable to hold the URL to which the control is redirected when there are no documents in the Document Basket
    	String urlString=null;
    	// Variable that holds the booelan value to decide if the control is to be redirected to the error document basket page
	boolean flag = false ;
	// Variable to hold the Personalizaton status
	boolean personalization = false;
	//The document format
	String documentFormat=null;

	//the sessionid
	SessionID sessionIdObj = null;
	String sessionid=null;
	// Object reference to FolderPage object
	FolderPage folderPage = null;
	//Object reference to Folder object
	Folder folder=null;
	//the folderid got from request
	String folderId="";
	// Object reference to SavedRecords object
	SavedRecords savedRecords = null;
	// Variable used to hold the reference to ControllerClient object
	ControllerClient client = null;
	//for redirection
	String redirect=null;
	//Client Customization variables
	ClientCustomizer clientCustomizer=null;

	//LocalHolding localHolding=null;

	boolean isPersonalizationPresent=true;
	boolean isLHLPresent=true;
	boolean isFullTextPresent=true;
	boolean isLocalHolidinsPresent=true;
	String customizedLogo="";
	//Database form to be displayed
	String customizedDB=null;
	String customizedStartYear="";
	String customizedEndYear="";

	//Default database search form whose search form is displayed for the user who does not
	// have a specified default form
	String defaultDatabase="Compendex";

	// Create a session object using the Controllerclient object
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();

	sessionid = ussession.getID();
	sessionIdObj = ussession.getSessionID();
	User user=ussession.getUser();

	String customerId=user.getCustomerID().trim();
	String contractId=user.getContractID().trim();

	//localHolding=new LocalHolding(customerId,contractId);

	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
		isLHLPresent=clientCustomizer.checkDDS();
		isFullTextPresent=clientCustomizer.checkFullText();
//		isLocalHolidinsPresent=clientCustomizer.checkLocalHolding();
		customizedLogo=clientCustomizer.getLogo();

	}
	// Varaible to hold the current User id
	String userId = null;
	String sUserId=null;

	sUserId=ussession.getProperty("P_USER_ID");
	if((sUserId != null) && (sUserId.trim().length() != 0)){
			personalization=true;
	}


	if( sUserId != null)
	{
		userId = sUserId;
	}

	// Retrieve all the request parameters
	if(request.getParameter("redirect") != null)
	{
	redirect=request.getParameter("redirect").trim();
	}

	if(request.getParameter("CID") != null)
	{
	cid=request.getParameter("CID").trim();
	}

	if(request.getParameter("folderid") != null)
	{

	folderId=request.getParameter("folderid").trim();
	}


	if(request.getParameter("database") != null)
	{
	database=request.getParameter("database").trim();
	}

	if(("Compendex").equalsIgnoreCase(database))
		{
			if(clientCustomizer.getStartYear()!=-1)
			{
				customizedStartYear=Integer.toString(clientCustomizer.getStartYear());
			}
			if(clientCustomizer.getEndYear()!=-1)
			{
				customizedEndYear=Integer.toString(clientCustomizer.getEndYear());
			}
	}

	savedRecords = new SavedRecords(userId);
	String strFolderId = folderId;
	String fName=savedRecords.getFolderName(strFolderId);
	folder=new Folder(strFolderId,fName);

	folderSize = savedRecords.getSizeOfFolder(folder);


	String strDownloadFormat = request.getParameter("format");
	String strDisplayFormat = "";

	/*
	 *  Gets the results for the current page
	 */
	if((strDownloadFormat != null) && (strDownloadFormat.equalsIgnoreCase("ASCII") )) {
		// if ASCII then DisplayFormat matters (i.e. Citation,Abstract,fullDoc))
		strDisplayFormat = request.getParameter("displayformat");
		if( (strDisplayFormat!=null) && (strDisplayFormat.equalsIgnoreCase("citation")) ) {
			documentFormat=Citation.CITATION_FORMAT;
		} else if( (strDisplayFormat!=null) && (strDisplayFormat.equalsIgnoreCase("abstract")) ) {
			documentFormat=Abstract.ABSTRACT_FORMAT;
		} else if( (strDisplayFormat!=null) && (strDisplayFormat.equalsIgnoreCase("fulldoc")) ) {
			documentFormat=FullDoc.FULLDOC_FORMAT;
		}
	} else {
		// if RIS - use fulldoc to build XML
		// Stylesheet will build RIS format from FULL_DOC 'records'
		documentFormat = "RIS";
	}


	if(folderSize > 0)
	{
		// Create the basketPage object with the documents
		folderPage = (FolderPage) savedRecords.viewRecordsInFolder(folder,
									   documentFormat);
	}



	if( (folderSize>0) && (redirect==null))
	{
		// jam 10/1/2002
		// Added code for setting filename
		// Client.setContentDispositionFilenameTimestamp()
		// method will timestamp and encode filename
		StringBuffer strbFilename = new StringBuffer();
		strbFilename.append(documentFormat);
		strbFilename.append("_");
		strbFilename.append(strDownloadFormat);
		if(strDownloadFormat.equalsIgnoreCase("ASCII")) {
			strbFilename.append("_.txt");
		} else if(strDownloadFormat.equalsIgnoreCase("bib")) {
			strbFilename.append("_.bib");
		} else {
			strbFilename.append("_.ris");
		}
    // do not set disposition or filename - refworks needs only mime type
    if(!strDownloadFormat.equalsIgnoreCase("refworks"))
    {
      client.setContentDispositionFilenameTimestamp(strbFilename.toString());
    }
		client.setRemoteControl();


		// and printXXXXXXXXX is the CID
		// THIS CID OPERATES IN BULK MODE
		// THIS CID OPERATES IN BULK MODE
		// This is done so that the print function
		// mimics the print function of PrintSelectedRecords.jsp
		// and uses the XSL, PrintXXXXXXFormat.xsl
		StringBuffer  folderContentStringBuffer  = new StringBuffer();

		folderContentStringBuffer.append("<PAGE><!--BH-->");
	/*	folderContentStringBuffer.append("<HEADER>");
		if(strDownloadFormat.equalsIgnoreCase("riscpx"))
			folderContentStringBuffer.append("<DB>Compendex</DB>");
		else if(strDownloadFormat.equalsIgnoreCase("risinspec"))
			folderContentStringBuffer.append("<DB>INSPEC</DB>");
		folderContentStringBuffer.append("</HEADER>"); */
		folderContentStringBuffer.append("<!--EH-->");


	   	//Writing out XML
		out.write(folderContentStringBuffer.toString());


		// if the download format is not ASCII
		if(!strDownloadFormat.equalsIgnoreCase("ASCII")) {
			int folderPageSize = folderPage.docCount();

			for(int x = 0; x< folderPageSize; x++) {

					// walk folder selecting only records that match
					// the db chosen for RIS
					FolderEntry entryDoc = folderPage.docAt(x);
					//String db = entryDoc.getEIDoc().getDocID().getDatabase().getID();

					if(strDownloadFormat.equalsIgnoreCase("ris") || strDownloadFormat.equalsIgnoreCase("bib") ||strDownloadFormat.equalsIgnoreCase("refworks"))
					{
						entryDoc.toXML(out);
					}
			} // for
		} else {
			folderPage.toXML(out);
		} // if-else


		out.write("<!--*-->");
		//Signale footer section
		out.write("<!--BF--><FOOTER/><!--EF-->");
		out.write("</PAGE>");
		out.println("<!--END-->");
		out.flush();
	}
	else
	{

		// This "should" never happen because downloadForm.jsp (DownloadForm.xsl)
		// which is called before this does the same check, preventing
		// the display of the download form when the folder is empty
		//
		// forward when the no of documents in basket is zero
		if((cid != null) && (!cid.equals("printErrorSelectedSet")))
		{
			urlString = "/controller/servlet/Controller?CID=printErrorSelectedSet";
			client.setRedirectURL(urlString);
			client.setRemoteControl();
		}
	}




%>
