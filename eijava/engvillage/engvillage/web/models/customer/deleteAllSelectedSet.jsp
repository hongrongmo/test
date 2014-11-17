<!--
 * This file deletes a set of selected  records for the Selected Set
 * and generates XML output of the Selecetd Set Page after deleting the record
 * @param java.lang.String
 -->
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page  import=" java.util.*"%>
<%@ page  import=" java.net.*"%>
<!--import statements of ei packages.-->
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>

<%@ page  errorPage="/error/errorPage.jsp"%>

<%
   //Setting the object refernce.
	DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
	DocID  docId=null;
	Query query = null;
   	ControllerClient client = null;
   	//This variable for sessionId
   	String sessionid  = null;
   	//This variable for Document id.
   	String docids = null;
   	//This variable for handle
   	String  handles = null;
   	//This variable for database name
   	String database = null;
   	//This variable for oracle query
   	String  oracleQuery = null;
   	//This list to hold list of Document ids
   	List listOfDocIds = null;
   	//This list to hold list of handles
   	List listOfHandles = null;
    	//This variable for document type
   	String documentType=null;
   	//List of Basket Entry objects
   	List listOfBasketEntries = null;

%>

<%
  	// Create a session object using the controllerclient.java

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	//client.updateUserSession(ussession);
	sessionid = ussession.getID();


    	// Get all the parameters from the request object

	if(request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}

	if(request.getParameter("documenttype") != null)
	{
		documentType = request.getParameter("documenttype");
	}

	if(request.getParameter("docidlist") !=null)
	{
		docids = request.getParameter("docidlist");
	}

	if(request.getParameter("handlelist")!=null)
	{
		handles = request.getParameter("handlelist");
	}

	if(request.getParameter("searchquery") != null)
	{
		oracleQuery = request.getParameter("searchquery");
	}

	query = new Query();
	query.setSearchQuery(oracleQuery);

  	// From the arrays of docid and handles construct individual lists of docid and handles
  	// i.e. convert arrays into lists.

	listOfDocIds = new ArrayList();

	StringTokenizer st = new StringTokenizer(docids,",");
	listOfDocIds = new ArrayList();

   	while(st.hasMoreTokens())
	{
       		String docid = st.nextToken();
       		if(!docid.trim().equals(""))
		{
	   		listOfDocIds.add(docid);
		}
   	}

	StringTokenizer st1 = new StringTokenizer(handles,",");
	listOfHandles = new ArrayList();

	while(st1.hasMoreTokens())
	{
         	String handle  = st1.nextToken();
         	if(!handle.trim().equals(""))
		{
  	   		listOfHandles.add(handle);
		}
	}


	//  Check if the no. of docid and handles are same.  If they are same, then construct
	//  DocId, BasketEntry objects and then create a list of BasketEntries.

	listOfBasketEntries = new ArrayList();
	if(     (     listOfDocIds.size() == listOfHandles.size()    )     )
	{
 		for(int i = 0; i < listOfDocIds.size() ; i++)
		{
			int handle =  Integer.parseInt((String) listOfHandles.get(i));
			String docid =  (String) listOfDocIds.get(i);
			docId = new DocID(handle,docid.trim(),databaseConfig.getDatabase(docid.substring(0,3)));
			BasketEntry be = new BasketEntry(docId,query);
 			listOfBasketEntries.add(be);
		}
 	}


	 //  Create Document basket Object.
	DocumentBasket basket = new DocumentBasket(sessionid);

	// Add the list of basket entries to the Document Basket
	basket.removeSelected(listOfBasketEntries);

 %>
 <test/>




