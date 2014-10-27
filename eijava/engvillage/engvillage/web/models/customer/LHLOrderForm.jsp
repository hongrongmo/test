<!--
 * This page takes the below params as input and generates XML output.
 * This JSP is used to authenticate the user ,take the docId and
 * customer id and build the eidocument,build the lhl user iformation and
 * pass it to the oredrform.xsl
 * @param java.lang.String.database
 * @param java.lang.String.docid
 * @param java.lang.String.customerid
 -->

<%@ page language="java" %>

<%@ page session="false"%>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.backoffice.BackofficeClient"%>
<%@ page import="org.ei.domain.LhlUserInfo"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*" %>

<%@ page errorPage="/error/errorPage.jsp" %>

<!--setting page buffer size to 20kb-->
<%@ page buffer="20kb"%>

<%

	//This variable is for holding sessionId
	String sessionId = null;
	//This variable is for holding xmlString
	StringBuffer xmlString=null;
	// Variable used to hold the reference to ControllerClient object
	ControllerClient client = null;
	// Variable used to hold the productId to be checked
	long productId=0;
	//Variable used to databse got from request parameter
	String database=null;
	//variable to hold docid
	String docid=null;
	//Variable to hold DOCID object
	DocID docID=null;
	//variable for docid
	String docId=null;
	//String for userName
	String userName=null;
	//variable for linda hall object
	//LindaHall lindaHall=null;
	//variable used to determine
	boolean flag=false;
	//variable for password
	String password=null;
	//boolean auth
	boolean auth=false;
	//String url String
	String urlString=null;
	//String for ip
	String ip=null;

%>

<%
	RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
	productId = Long.parseLong(eiProps.getProperty("LHLPRODUCTID"));

	// Create a session object using the controllerclient.java
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	//client.updateUserSession(ussession);
	sessionId = ussession.getID();
	User user=ussession.getUser();
	userName=user.getUsername();

	String strContractId=user.getContractID().trim();
	String strCustomerId=user.getCustomerID().trim();

	// getting database parameter from request object.
	if(request.getParameter("database") != null)
	{
		database=request.getParameter("database").trim();
	}


	// getting DOCID parameter from request object.
	if(request.getParameter("docid") != null)
	{
		docId = request.getParameter("docid").trim();

	}


	// lindaHall=new LindaHall(contractId,custId,productId);
	// prod ID is harcoded in lhlUserInfo as 9004 for now
	BackofficeClient boc = BackofficeClient.getInstance((String) ussession.getProperty("ENV_LHLURL"));
	LhlUserInfo lhlUserInfo = boc.getLhlUserInfo(strCustomerId,strContractId);

	if(request.getParameter("pword")!= null)
	{
		password = request.getParameter("pword").trim();
		flag=true;
		// jam 10/15/2002
		// changed to use customerID instead of username
		// username for other products may not match EngVillage Username
		auth = boc.authenticateByPassword(strCustomerId, password, strContractId);
	}


	if(!auth && ( request.getParameter("ip")!= null) )
	{
		ip = request.getParameter("ip").trim();
		auth = (Boolean.valueOf(ip)).booleanValue();
	}

	if(auth)
	{
		//Building the DocId object and getting the documents from the builder
		DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
		List l = new ArrayList();

		Database databaseObj = databaseConfig.getDatabase(database);
		docID=new DocID(docId,databaseObj);
		l.add(docID);

    MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
    List builtList = builder.buildPage(l, Abstract.ABSTRACT_FORMAT);

		PageEntryBuilder eBuilder = new PageEntryBuilder(sessionId);
		List pList = eBuilder.buildPageEntryList(builtList);
		Page page1 = new Page();
		page1.addAll(pList);

		out.write("<LINDA-HALL>");
		if(page1 != null)
		{
			page1.toXML(out);
		}
		xmlString = new StringBuffer();
		xmlString.append(lhlUserInfo.toXMLString());
		xmlString.append("<DATABASE>").append(database).append("</DATABASE>");
		xmlString.append("<DOC-ID>").append(docId).append("</DOC-ID>");
		xmlString.append("<MATCH>ORDER-FORM</MATCH>");
		xmlString.append("<SESSION-ID>").append(sessionId).append("</SESSION-ID>");
		xmlString.append("</LINDA-HALL>");
		out.write(xmlString.toString());
	} else {
		xmlString = new StringBuffer("<LINDA-HALL>");
		xmlString.append("<DOC-ID>").append(docId).append("</DOC-ID>");
		xmlString.append("<SESSION-ID>").append(sessionId).append("</SESSION-ID>");
		xmlString.append("<DATABASE>").append(database).append("</DATABASE>");
		xmlString.append("<CONTRACT-ID>").append(strContractId).append("</CONTRACT-ID>");
		xmlString.append("<MATCH>").append(flag).append("</MATCH>");
		xmlString.append("</LINDA-HALL>");
		out.write(xmlString.toString());
	}
%>
