<!--
 * This page basically does the responsiblity of Editing Order Form
 * Its also responsible for providing the XML for Complete Order Confirmation Form
-->
<%@ page language="java" %>

<%@ page session="false"%>

<!-- import statements of Java packages-->
<%@ page import=" java.util.*"%>
<%@ page import=" java.net.*"%>

<!--import statements of ei packages.-->
<%@ page import ="org.ei.domain.*"%>
<%@ page import ="org.ei.config.*"%>
<%@ page import ="org.ei.controller.ControllerClient"%>
<%@ page import ="org.ei.session.*" %>

<%@ page errorPage="/error/errorPage.jsp" %>

<!--setting page buffer size to 20kb-->
<%@ page buffer="20kb"%>

<%
   	//This variable is for holding sessionId
	String sessionId = null;
	// Variable used to hold the reference to ControllerClient object
	ControllerClient client = null;
	// Variable used to hold the productId to be checked
	long productId=0;

    //variable to hold methodName
	String methodName=null;
	//variable to hold methodValue
	String methodValue=null;
	//variable to hold service level(Regular,Rush,Super Rush,Drop Everything)
	String service=null;
	//variable to hold firstName
	String firstName=null;
	//variable to hold lastName
	String lastName=null;
	//variable to hold companyName
	String companyName=null;
	//variable to hold address2
	String address2=null;
	//variable to hold address1
	String address1=null;
	//variable to hold city
	String city=null;
	//variable to hold state
	String state=null;
	//variable to hold country
	String country=null;
	//variable to hold zip
	String zip=null;
	//variable to hold phone
	String phone=null;
	//variable to hold shipping(senders Name)
    String attention=null;

    //variable to hold confirmation email
    String confirmationEmail=null;
 	//variable to hold delivery email
 	String deliveryEmail=null;

    //variable to hold fax
 	String fax=null;
 	//variable to hold fedex account No
 	String fedex=null;
 	//variable to hold shippingValue(Centralised,Decentralised)
 	String shippingValue=null;
    //variable to hold database
	String database=null;
    //variable to hold docId
	String docId=null;
    //variable to hold docID object
 	DocID docID=null;


    if(request.getParameter("shippingvalue")!=null)
    {
		shippingValue=request.getParameter("shippingvalue").trim();
	}
	if(request.getParameter("docid")!=null)
	{
		docId=request.getParameter("docid");
	}

	if(request.getParameter("database")!=null)
	{
	    database=request.getParameter("database");
	}


	RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
	productId = Long.parseLong(eiProps.getProperty("LHLPRODUCTID"));

	// Create a session object using the controllerclient.java
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionId = ussession.getID();


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

    if(request.getParameter("methodname")!=null)
    {
		methodName=request.getParameter("methodname").trim();
	}

    if(request.getParameter("methodvalue")!=null)
    {
		methodValue=request.getParameter("methodvalue");
		deliveryEmail = methodValue;
	}

	if(request.getParameter("confirmationEmail") != null)
	{
		confirmationEmail = request.getParameter("confirmationEmail").trim();
	}

	if(request.getParameter("servicelevel")!=null)
	{
		service=request.getParameter("servicelevel").trim();
	}

	if(request.getParameter("accountnumber")!=null)
	{
	 	fedex=request.getParameter("accountnumber");
	}

	if(request.getParameter("attention")!=null)
	{
     	attention=request.getParameter("attention");
	    int k=attention.indexOf("&");
	    if(k>0){
		   String st=attention.substring(0,k);
		   String finalstr=st.concat("&#38;").concat(attention.substring(k+1,attention.length()));
		   attention=finalstr;
		}
	}

	if(request.getParameter("firstname")!=null)
	{
	 	firstName=request.getParameter("firstname");
	}

	if(request.getParameter("lastname")!=null)
	{
	 	lastName=request.getParameter("lastname");
	}

	if(request.getParameter("companyname")!=null)
	{
	 	companyName=request.getParameter("companyname");
	}

	if(request.getParameter("address1")!=null)
	{
	 	address1=request.getParameter("address1");
	}

	if(request.getParameter("address2")!=null)
	{
	 	address2=request.getParameter("address2");
	}

	if(request.getParameter("city")!=null)
	{
	 	city=request.getParameter("city");
	}

	if(request.getParameter("state")!=null)
	{
	 	state=request.getParameter("state");
	}

	if(request.getParameter("country")!=null)
	{
	 	country=request.getParameter("country");
	}

	if(request.getParameter("zip")!=null)
	{
	 	zip=request.getParameter("zip");
	}

	if(request.getParameter("phone")!=null)
	{
	 	phone=request.getParameter("phone");
	}

	if(request.getParameter("fax")!=null)
	{
		fax=request.getParameter("fax");
	}


	out.write("<LINDA-HALL>");

	if(page1!= null)
	{
		page1.toXML(out);
	}


	StringBuffer sb=new StringBuffer();

	sb.append("<DATABASE>").append(database).append("</DATABASE>");
	sb.append("<DOC-ID>").append(docId).append("</DOC-ID>");
	sb.append("<MATCH>ORDER-FORM</MATCH>");
	sb.append("<SESSION-ID>").append(sessionId).append("</SESSION-ID>");

	sb.append("<LHL-USER-INFO>");
    // jam 12/9/2003 - XML structure did not match LHLUserInfo toXML() method!
    // caused problem in XSL LHLOrderForm.xsl.
	sb.append("<SHIPPING>");
	sb.append(shippingValue);
	sb.append("</SHIPPING>");

	sb.append("<DELIVERY-METHOD-NAME>");
	sb.append(methodName);
	sb.append("</DELIVERY-METHOD-NAME>");
    if(methodName!=null){
		if(methodName.equals("E-mail")){
			sb.append("<DELIVERY-METHOD-VALUE>");
			sb.append(deliveryEmail);
			sb.append("</DELIVERY-METHOD-VALUE>");

		}else if(methodName.equals("Ariel")){
			sb.append("<DELIVERY-METHOD-VALUE>");
			sb.append(methodValue);
			sb.append("</DELIVERY-METHOD-VALUE>");
		}
	}

	sb.append("<SERVICE-LEVEL>");
	sb.append(service);
	sb.append("</SERVICE-LEVEL>");

	sb.append("<ACCOUNT-NUMBER>");
	sb.append(fedex);
	sb.append("</ACCOUNT-NUMBER>");

	sb.append("<ATTENTION>");
	sb.append(attention);
	sb.append("</ATTENTION>");

	sb.append("<EMAIL-CONFIRM>");
	sb.append(confirmationEmail);
	sb.append("</EMAIL-CONFIRM>");

	sb.append("<CONTACT-INFO>");
	sb.append("<FIRST-NAME>");
	sb.append(firstName);
	sb.append("</FIRST-NAME>");
	sb.append("<LAST-NAME>");
	sb.append(lastName);
	sb.append("</LAST-NAME>");
	sb.append("<COMPANY-NAME><![CDATA[");
	sb.append(companyName);
	sb.append("]]></COMPANY-NAME>");
	sb.append("<ADDRESS1><![CDATA[");
	sb.append(address1);
	sb.append("]]></ADDRESS1>");
	sb.append("<ADDRESS2>");
	sb.append(address2);
	sb.append("</ADDRESS2>");
	sb.append("<CITY>");
	sb.append(city);
	sb.append("</CITY>");
	sb.append("<STATE>");
	sb.append(state);
	sb.append("</STATE>");
	sb.append("<COUNTRY>");
	sb.append(country);
	sb.append("</COUNTRY>");
	sb.append("<ZIP>");
	sb.append(zip);
	sb.append("</ZIP>");
	sb.append("<PHONE>");
	sb.append(phone);
	sb.append("</PHONE>");
	sb.append("<FAX>");
	sb.append(fax);
	sb.append("</FAX>");
	sb.append("<EMAIL>");
	sb.append(confirmationEmail);
	sb.append("</EMAIL>");
	sb.append("</CONTACT-INFO>");
	sb.append("</LHL-USER-INFO>");
	sb.append("</LINDA-HALL>");

    out.write(sb.toString());
%>





