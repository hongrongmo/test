<%--
 * This page the follwing params as input and generates XML output.
 * This JSP is used when some of the records in a page of a particular type of display
 * (citation,detailed or abstract) are to be printed
 *
 * @param java.lang.String.searchid
 * @param java.lang.String.pagesize
 * @param java.lang.String.database
 * @param java.lang.String.displayformat
 * @param java.lang.String.docidlist
 * @param java.lang.String.handlelist
--%>
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>

<%-- import statements of Java packages--%>
<%@ page  import=" java.util.*"%>
<%@ page  import=" java.net.*"%>
<%-- import statements of ei packages.--%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page  errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>
<%

	ControllerClient client = null;
	// This variable is used to hold SearchComponent instance
	 //Setting the object refernce.
	DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

	DocID  docID=null;
	SearchControl sc=null;
	Page page1 = null;
	//This variable for searchResults instance
	SearchResult result=null;
	//This variable for sessionId
	String sessionId  = null;
	String searchid  = null;
	//The document format
	String documentFormat=null;

	int pageSize = 0;
	//This variable for Document id.
	String docids= null;
	//This variable for handle
	String  handles = null;
	//This variable for database name
	String database = null;
	List DocIDList = null;
	List docIdList = null;
	List handleList = null;
	//variable to hold personalization feature
	boolean  isPersonalizationPresent=false;
	String customizedLogo="";


%>
<%

	// Create a session object using the controllerclient.java
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();

	sessionId = ussession.getSessionid();

	ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
	isPersonalizationPresent=clientCustomizer.checkPersonalization();
	customizedLogo=clientCustomizer.getLogo();

	if(request.getParameter("searchid") != null)
	{
		searchid = request.getParameter("searchid");
	}


	if(request.getParameter("pagesize") != null)
	{
		pageSize = Integer.parseInt(request.getParameter("pagesize"));
	}


	if(request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}


	if(request.getParameter("docidlist") !=null)
	{
		docids = request.getParameter("docidlist");
	}

	if(request.getParameter("handlelist")!=null)
	{
		handles = request.getParameter("handlelist");
	}

	StringTokenizer st = new StringTokenizer(docids,",");
	docIdList = new ArrayList();

	while(st.hasMoreTokens())
	{
		String docid = st.nextToken();
		if(!docid.trim().equals(""))
		{
			docIdList.add(docid);
		}
	}

	StringTokenizer st1 = new StringTokenizer(handles,",");
	handleList = new ArrayList();

	while(st1.hasMoreTokens())
	{
		String handle  = st1.nextToken();
		if(!handle.trim().equals(""))
		{
			//System.out.println(" adding handle " + handle.toString());

			handleList.add(handle);
		}
	}
	//  Check if the no. of docid and handles are same.  If they are same, then construct
	//  DocId, BasketEntry objects and then create a list of BasketEntries.

	DocIDList = new ArrayList();

	int docIdSize = docIdList.size();
	int handleSize =handleList.size();

	if( docIdSize  == handleSize )
	{
		int handle = 0;
		String docid = null;
		for(int i = 0; i < docIdSize ; i++)
		{
			//System.out.println(" current handle " + (String) handleList.get(i));

			handle =  Integer.parseInt((String) handleList.get(i));


			docid =  (String) docIdList.get(i);


			docID = new DocID(handle,
					  docid.trim(),
					  databaseConfig.getDatabase(docid.substring(0,3)));
			DocIDList.add(docID);
		}
	}


	MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
	List bList = null;

	String strDownloadFormat = request.getParameter("format");
	String strDisplayFormat = "";

	if((strDownloadFormat != null) && (strDownloadFormat.equalsIgnoreCase("ASCII") )) {

		// if ASCII then DisplayFormat matters
		strDisplayFormat = request.getParameter("displayformat");
		if(strDisplayFormat.equalsIgnoreCase("citation"))
		{
			documentFormat = Citation.CITATION_FORMAT;
		}
		else if(strDisplayFormat.equalsIgnoreCase("abstract"))
		{
			documentFormat = Abstract.ABSTRACT_FORMAT;
		}
		else if(strDisplayFormat.equalsIgnoreCase("detailed") || strDisplayFormat.equalsIgnoreCase("fullDoc"))
		{
			documentFormat = FullDoc.FULLDOC_FORMAT;
		}
	} else {
		// if RIS - use fulldoc to build XML
		// Stylesheet will build RIS format from FULL_DOC 'records'
		documentFormat = "RIS";
	}

	bList = builder.buildPage(DocIDList, documentFormat);

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

	PageEntryBuilder eBuilder = new PageEntryBuilder(sessionId);
	List pList = eBuilder.buildPageEntryList(bList);
	page1 = new Page();
	page1.addAll(pList);

	StringBuffer  basketContentStringBuffer  = new StringBuffer();

	basketContentStringBuffer.append("<PAGE><!--BH-->");
	basketContentStringBuffer.append("<!--EH-->");
	out.write(basketContentStringBuffer.toString());


	for(int i=0; i<page1.docCount();i++)
	{
		EIDoc doc = page1.docAt(i);
		out.write("<!--BR--><PAGE-ENTRY>");
		doc.toXML(out);
		out.write("</PAGE-ENTRY><!--ER-->");

	}

	out.write("<!--*-->");
	//Signale footer section
	out.write("<!--BF--><FOOTER/><!--EF-->");
	out.write("</PAGE>");
	out.flush();

%>