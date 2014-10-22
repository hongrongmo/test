<!--
 * This file adds a set of records to the Selected Set.
 * @param java.lang.String.database
 * @param java.lang.String.docids
 * @param java.lang.String.handles
 * @param java.lang.String.documentType
 * @param java.lang.String.oracleQuery
-->

<%@page import="org.engvillage.biz.controller.UserSession"%>
<%@page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page  import=" java.util.*"%>
<%@ page  import=" java.net.URLEncoder"%>

<!--import statements of ei packages.-->

<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>

<%@ page  errorPage="/error/errorPage.jsp"%>

<%
	DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
	DocID  docId=null;
	Query query = null;
   	ControllerClient client = null;
	String sessionid  = null;
   	String docids = null;
   	String  handles = null;
   	String database = null;
   	String documentType=null;
   	String  oracleQuery = null;
   	String searchId=null;
   	List listOfDocIds = null;
  	List listOfHandles = null;
   	List listOfBasketEntries = null;


	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionid = ussession.getSessionid();


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


	//***adding search id
	if(request.getParameter("searchid") != null)
	{
		searchId = request.getParameter("searchid");
	}


    	// Create a new Query object
	query = new Query();
	// Set the query object with the oracle query retrieved from the request object
	query.setDisplayQuery(oracleQuery);
	query.setID(searchId);


    	// From the arrays of docid and handles, individual lists of docid and handles are formed
    	// i.e. Convert arrays into lists.

	listOfDocIds = new ArrayList();

	listOfHandles = new ArrayList();


	StringTokenizer st = new StringTokenizer(docids,",");
	while(st.hasMoreTokens())
	{
       		String docid = st.nextToken();
       		if(!docid.trim().equals(""))
		{
	   		listOfDocIds.add(docid);
		}
   	}

	StringTokenizer st1 = new StringTokenizer(handles,",");

     	while(st1.hasMoreTokens())
	{
		String handle  = st1.nextToken();
         	if(!handle.trim().equals(""))
		{
  	   		listOfHandles.add(handle);
		}
   	}


    	//  Check if the number of docid and handles are same.  If they are same, construct
    	//  DocId objects. Create BasketEntry objects and then create a list of BasketEntries.



	listOfBasketEntries = new ArrayList();
	if(   listOfDocIds.size() == listOfHandles.size()    )
	{
		for(int i = 0; i < listOfDocIds.size() ; i++)
		{
			int handle =  Integer.parseInt((String) listOfHandles.get(i));
			//long docid =  Long.parseLong((String) listOfDocIds.get(i));
			String docid =  (String) listOfDocIds.get(i);


			docId = new DocID(handle,docid.trim(),databaseConfig.getDatabase(docid.substring(0,3)));
			BasketEntry be = new BasketEntry(docId,query);
 			listOfBasketEntries.add(be);
		}
 	}


    	// Create an object of DocumentBasket with a constructor that takes session id
	DocumentBasket basket = new DocumentBasket(sessionid);

	// Add list of basket entries to the Document basket object
	basket.addAll(listOfBasketEntries);

 %>
 <test/>




