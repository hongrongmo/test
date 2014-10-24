<!--
 * This page basically does the responsiblity of Confirmation Order Form
 * From this page the user has option to edit or submit request.
-->
<%@ page language="java" %>

<%@ page session="false"%>

<!-- import statements of Java packages-->
<%@ page import=" java.util.*"%>

<!--import statements of ei packages.-->
<%@ page import ="org.ei.domain.*"%>
<%@ page import ="org.ei.config.*"%>
<%@ page import ="org.ei.controller.ControllerClient"%>
<%@ page import ="org.ei.session.*" %>

<%@ page errorPage="/error/errorPage.jsp" %>

<!--setting page buffer size to 20kb-->

<!--This Jsp generates XML for lhl_Orderforn_Complete.xsl -->
<%
    //variable to hold methodName(Email,Ariel,Fax etx)
	String methodName=null;
	//variable to hold service level(Regular,Rush,Super Rush,Drop everything)
	String service=null;
	//variable to hold title
	String title=null;
	//variable to hold authors
	String authors=null;
	//variable to hold source
	String source=null;
	//variable to hold issn
	String issn=null;
	//variable to hold coden
	String coden=null;
	//variable to hold accountNo(Fed ex account)
	String accountNo=null;
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
	//variable to hold shipping(Senders Name)
    String attention=null;
    //variable to hold fax
 	String fax=null;
 	//variable to hold fedex
 	String fedex=null;

 	String deliveryEmail=null;
 	String confirmationEmail=null;

 	//variable to hold ariel
 	String ariel=null;
 	//variable to hold shippingValue(decentalised or centralized)
 	String shippingValue=null;

// mvb
	//the sessionid
	SessionID sessionIdObj = null;

 	//variable to hold sessionId
 	String sessionId=null;
 	//variable to hold client
 	ControllerClient client = null;
    //variable to hold database
 	String database=null;
    //variable to hold docId
 	String docId=null;
    //variable to hold docID Object
 	DocID docID=null;

%>


<%
    // Create a session object using the controllerclient.java
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	//client.updateUserSession(ussession);
	sessionId = ussession.getID();


	sessionIdObj = ussession.getSessionID();



    // centralized or decentralized
	if(request.getParameter("shipping_value")!=null)
	{
		shippingValue=request.getParameter("shipping_value");
	}

	if(request.getParameter("database") != null)
	{

		database=request.getParameter("database").trim();
	}

	// getting DOCID parameter from request object.
	if(request.getParameter("docid") != null)
	{

		docId = request.getParameter("docid").trim();
	}



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

    // method name(Email,ariel,Fax)

    if(request.getParameter("method")!=null)
    {
		methodName=request.getParameter("method").trim();
	}

	if(request.getParameter("deliveryEmail")!=null)
	{
	 	deliveryEmail=request.getParameter("deliveryEmail").trim();;
	}
	if(request.getParameter("confirmationEmail")!=null)
	{
	 	confirmationEmail=request.getParameter("confirmationEmail").trim();;
	}

    // method value(ariel address)
	if(request.getParameter("ariel")!=null)
	{
	 	ariel=request.getParameter("ariel").trim();;
	}

    // fedex account number
	if(request.getParameter("fedex")!=null)
	{
	 	fedex=request.getParameter("fedex");
	}
	// service type
	if(request.getParameter("service")!=null)
	{
		service=request.getParameter("service").trim();
	}


    //senders name
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

	if(page1 != null)
	{
		page1.toXML(out);
	}

	StringBuffer sb=new StringBuffer();
	sb.append("<SHIPPING-VALUE>");
	sb.append(shippingValue);
	sb.append("</SHIPPING-VALUE>");

	sb.append("<SESSION-ID>");
	sb.append(sessionIdObj.toString());
	sb.append("</SESSION-ID>");

	sb.append("<DOC-ID>");
	sb.append(docId);
	sb.append("</DOC-ID>");

	sb.append("<DATABASE>");
	sb.append(database);
	sb.append("</DATABASE>");

	sb.append("<DELIVERY-METHOD-NAME>");
	sb.append(methodName);
	sb.append("</DELIVERY-METHOD-NAME>");

	if(methodName.equals("E-mail")){
		sb.append("<DELIVERY-METHOD-VALUE>");
		sb.append(deliveryEmail);
		sb.append("</DELIVERY-METHOD-VALUE>");

	}else if(methodName.equals("Ariel")){
		sb.append("<DELIVERY-METHOD-VALUE>");
		sb.append(ariel);
		sb.append("</DELIVERY-METHOD-VALUE>");
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
	sb.append("<TELEPHONE>");
	sb.append(phone);
	sb.append("</TELEPHONE>");
	sb.append("<FAX>");
	sb.append(fax);
	sb.append("</FAX>");
	sb.append("<EMAIL>");
	sb.append(confirmationEmail);
	sb.append("</EMAIL>");
	sb.append("</CONTACT-INFO>");

	sb.append("</LINDA-HALL>");

    out.write(sb.toString());
%>




