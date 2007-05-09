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
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page  import=" java.util.*"%>
<%@ page  import=" java.net.*"%>

<!--import statements of ei packages.-->
<%@ page import ="org.ei.domain.*"%>
<%@ page import ="org.ei.config.*"%>
<%@ page import ="org.ei.controller.ControllerClient"%>
<%@ page import ="org.ei.session.*" %>

<%@ page  import="org.ei.backoffice.BackofficeClient"%>
<%@ page  import="org.ei.domain.LhlUserInfo"%>


<%@ page errorPage="/error/errorPage.jsp" %>

<!--setting page buffer size to 20kb-->
<%@ page buffer="20kb"%>

<%

	//This variable is for holding sessionId
	String sessionId = null;
	//This variable is for holding documentFormat( eg:Citation.CITATION_FORMAT).
	String documentFormat=null;
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

	// Variables to hold contract id and customer id
	String strContractId;
	String strCustomerId;

	//variable for linda hall object
	//LindaHall lindaHall=null;
	//variable used to determine
	boolean flag=false;
	//variable for password
	String password=null;
	//variable for auth
	boolean auth=false;
	//var for url
	String urlString=null;
	//var for ip address
	String ipAddress=null;
	//String match
	String match=null;
%>

<%
	RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
	productId = Long.parseLong(eiProps.getProperty("LHLPRODUCTID"));

	// Create a session object using the controllerclient.java
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	
	sessionId = ussession.getID();
	User user = ussession.getUser();
	
	ipAddress = ussession.getProperty("ENV_IPADDRESS");
	userName = user.getUsername();
	strContractId = user.getContractID().trim();
	strCustomerId = user.getCustomerID().trim();

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

	if(lhlUserInfo != null)  {
		String accessType = lhlUserInfo.getAccessType();

		if(accessType.equals("IPA")) {
			auth=boc.authenticateByIp(ipAddress,strContractId);
		}
	}

	//If authentication is true  it redirects to viewOrderForm cid
	if(auth==true) {
		urlString = "/controller/servlet/Controller?CID=lhlViewOrderForm"+"&database="+database+"&docid="+docId+"&ip=true";
	} else {

		//XML output..based on the flag value of Match the form that has to be displayed is displayed
		xmlString = new StringBuffer("<LINDA-HALL>");
		xmlString.append("<DOC-ID>"+docId+"</DOC-ID>");
		xmlString.append("<SESSION-ID>"+sessionId+"</SESSION-ID>");
		xmlString.append("<DATABASE>"+database+"</DATABASE>");
		xmlString.append("<CONTRACT-ID>"+strContractId+"</CONTRACT-ID>");
		xmlString.append("<MATCH>"+flag+"</MATCH>");
		xmlString.append("</LINDA-HALL>");
		out.write(xmlString.toString());
		out.flush();
	}

	if(urlString!=null)
	{
		client.setRedirectURL(urlString);
		client.setRemoteControl();
	}
%>