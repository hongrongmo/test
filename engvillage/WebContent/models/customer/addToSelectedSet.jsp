<!--
 * This file adds a single document to the selecyted Set.
 * @param java.lang.String.sessionid
 * @param java.lang.String.docid
 * @param java.lang.String.handle
 * @param java.lang.String.database
 * @param java.lang.String.searchQuery
 * @param java.lang.String.documentType
 -->
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page  import=" java.util.*"%>
<%@ page  import=" java.net.URLEncoder"%>
<!--import statements of ei packages.-->

<%@ page import="org.ei.domain.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession" %>

<%@ page errorPage="/error/errorPage.jsp"%>

<%

    // Object references
	Query query = null;
   	BasketEntry be = null;
   	ControllerClient client = null;
   	DocID odocId=null;
   	ServletContext context = config.getServletContext();
	DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    // Variable to hold the session id
   	String sessionid  = null;
   	// Variable to hold the docid of the selected document. This value is obtained from the http request
   	String docid = null;
   	//Variable to hold the handle of of the selected document. This value is obtained from the http request
   	String handle = null;
   	// Variable to hold the database of the selected document. This value is obtained from the http request
   	String database = null;
   	// Variable to hold the search query which resulted in the selected document. This value is obtained from the http request
   	String searchQuery = null;
   	// Variable to hold the document type of  the selected document.  THis is available only when the selected document is from CBNB database
   	//This value is obtained from the http request
   	String documentType=null;

	//***added for ei***This for searchid
	String searchid=null;




%>

<%

    // Create a session object using the controllerclient.java

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	//client.updateUserSession(ussession);
	sessionid = ussession.getSessionid();


    //Get all the parameters from the request


	if(request.getParameter("database")!=null)
	{
		database = request.getParameter("database").trim();
	}

	if(request.getParameter("docid")!=null)
	{
		docid = request.getParameter("docid");
	}

	if(request.getParameter("handle")!=null)
	{
		handle = request.getParameter("handle");
	}

	if(request.getParameter("searchquery")!=null)
	{
		searchQuery = request.getParameter("searchquery");
	}

	if(request.getParameter("documenttype")!=null)
	{
		documentType= request.getParameter("documenttype").trim();
	}
	//***adding search id
	if(request.getParameter("searchid") != null)
	{
		searchid = request.getParameter("searchid").trim();
	}

	query = new Query();
	query.setSearchQuery(searchQuery);
	query.setDisplayQuery(searchQuery);
	query.setID(searchid);

  //  Check if number of docid and handles are same.  If they are same, then  construct
  //  DocId objects, then create BasketEntry objects and create a list of BasketEntries.

	int ihandle = Integer.parseInt(handle);
	//long idocid = Long.parseLong(docid);

	odocId = new DocID(ihandle,docid.trim(),databaseConfig.getDatabase(database));

	be = new BasketEntry(odocId,query);

   //  Creating an object of Document Basket with a constructor that takes session id
	DocumentBasket basket = new DocumentBasket(sessionid);

	// Add the basket entry list to the Doucment Basket object
	basket.add(be);
 %>
 <test/>




