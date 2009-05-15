<!--
 * This page takes the below params as input and generates XML output.
 * This JSP is used to Email the order details to the customer and Lhl
 * @param java.lang.String.database
 * @param java.lang.String.docid
 -->

<%@ page language="java" %>
<%@ page session="false"%>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.text.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="javax.mail.internet.*"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.util.StringUtil" %>
<%@ page import="org.ei.email.*"%>
<%@ page import="org.ei.backoffice.BackofficeClient"%>
<%@ page import="org.ei.domain.LhlUserInfo"%>
<%@ page import="org.apache.oro.text.perl.*"%>

<%@ page import="javax.xml.transform.TransformerFactory"%>
<%@ page import="javax.xml.transform.Transformer"%>
<%@ page import="javax.xml.transform.TransformerException"%>
<%@ page import="javax.xml.transform.TransformerConfigurationException"%>
<%@ page import="javax.xml.transform.stream.StreamSource"%>
<%@ page import="javax.xml.transform.stream.StreamResult"%>
<%@ page import="javax.xml.transform.stream.StreamSource"%>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
    //This variable is for holding sessionId
	String database = null;
    //This variable is for holding sessionId
	String sessionId = null;
	//This variable is for holding documentFormat
	String documentFormat=null;
	//This variable is for holding xmlString
	StringBuffer messageString=null;
	//This variable is for holding controller client object
	ControllerClient client = null;

	//Var for conmtactInfo
	ContactInfo cInfo=null;

	String strContractId;
	String strCustomerId;

	//Var for customer id
	//  FROM  parameter
	String from =null;
	// getting TO  parameter
	String to=null;
	// getting message  parameter
	String message=null;
	// getting SUBJECT  parameter
	String subject="";

	//Variables that are used for the message to be sent..got from request parameters
	String title=null;
	String authedit=null;
	String source=null;
	String issn=null;
	String coden=null;
	String isbn=null;
	String firstName=null;
	String lastName=null;
	String companyName=null;
	String address1=null;
	String address2=null;
	String city=null;
	String state=null;
	String country=null;
	String zip=null;
	String phone=null;
	String fax=null;
	String deliveryEmail=null;
	String confirmationEmail=null;

	String methodName=null;
	String methodValue=null;
	String serviceLevel=null;
	String accountNumber=null;
	String attention=null;
	String aeFlag=null;
	String publicationDate=null;


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
	User user=ussession.getUser();


	strContractId=user.getContractID().trim();
	strCustomerId=user.getCustomerID().trim();

	cInfo=new ContactInfo();
	BackofficeClient boc = BackofficeClient.getInstance((String) ussession.getProperty("ENV_LHLURL"));
	LhlUserInfo lhlUserInfo = boc.getLhlUserInfo(strCustomerId,strContractId);

	//Document Details
	// getting DOCID parameter from request object.
	if(request.getParameter("docid") != null)
	{
		docId = request.getParameter("docid").trim();
	}

	//Shipping address
	if(request.getParameter("firstname") != null)
	{
		firstName = request.getParameter("firstname").trim();
		cInfo.setUserFirstName(firstName);
	}
	if(request.getParameter("lastname") != null)
	{
		lastName = request.getParameter("lastname").trim();
		cInfo.setUserLastName(lastName);
	}
	if(request.getParameter("companyname") != null)
	{
		companyName = request.getParameter("companyname").trim();
		cInfo.setCompany(companyName);
	}
	if(request.getParameter("address1") != null)
	{
		address1 = request.getParameter("address1").trim();
		cInfo.setAddress1(address1);
	}
	if(request.getParameter("address2") != null)
	{
		address2 = request.getParameter("address2").trim();
		cInfo.setAddress2(address2);
	}
	if(request.getParameter("city") != null)
	{
		city = request.getParameter("city").trim();
		cInfo.setCity(city);
	}
	if(request.getParameter("state") != null)
	{
		state = request.getParameter("state").trim();
		cInfo.setState(state);
	}
	if(request.getParameter("country") != null)
	{
		country = request.getParameter("country").trim();
		cInfo.setCountry(firstName);
	}
	if(request.getParameter("zip") != null)
	{
		zip = request.getParameter("zip").trim();
		cInfo.setZip(zip);
	}
	if(request.getParameter("phone") != null)
	{
		phone = request.getParameter("phone").trim();
		cInfo.setPhoneNumber(phone);
	}
	if(request.getParameter("fax") != null)
	{
		fax = request.getParameter("fax").trim();
		cInfo.setFaxNumber(fax);
	}

	if(request.getParameter("deliveryEmail") != null)
	{
		deliveryEmail = request.getParameter("deliveryEmail").trim();
	}
	if(request.getParameter("confirmationEmail") != null)
	{
		confirmationEmail = request.getParameter("confirmationEmail").trim();
	}
	if(request.getParameter("attention") != null)
	{
		attention = request.getParameter("attention").trim();
	}
	if(request.getParameter("methodname") != null)
	{
		methodName = request.getParameter("methodname").trim();
	}
	if(request.getParameter("methodvalue") != null)
	{
		methodValue = request.getParameter("methodvalue").trim();
	}
	if(request.getParameter("servicelevel") != null)
	{
		serviceLevel = request.getParameter("servicelevel").trim();
	}
	if(request.getParameter("accountnumber") != null)
	{
		accountNumber = request.getParameter("accountnumber").trim();
		cInfo.setAccountNumber(accountNumber);
	}

	if(request.getParameter("database") != null)
	{
		database=request.getParameter("database").trim();
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

	StringBuffer sBuffer = null;

	if(page1 != null) {

		StringWriter sw = new StringWriter();
		page1.toXML(sw);
		sBuffer = new StringBuffer(sw.toString());
	}

	try
	{

		// Code for updating the order_data table
		// Use the (possibly) updated contact information
		lhlUserInfo.setContactInfo(cInfo);

		Date today = new Date();
		String format = "yyyyMMdd";
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String orderNumber = Long.toString(System.currentTimeMillis());

		// Create Email message
		messageString=new StringBuffer();
		messageString.append("The following order ").append("(Order number: ").append(orderNumber).append(")").append(" has been sent to Linda Hall Library. A Linda Hall Library representative will contact you for confirmation of your order and for billing arrangements. To contact Linda Hall Library regarding your order, please call 1 (800) 662-1545 or e-mail ei@lindahall.org").append("\n\n");

		/* Transform XML document into display form for inclusion in Email and Database */
		TransformerFactory tfactory = TransformerFactory.newInstance();
		ServletContext context = getServletContext();
		java.net.URL xslURL = context.getResource("/views/customer/LindaHallAsciiEmail.xsl");
		Transformer transformer = tfactory.newTransformer(new StreamSource(xslURL.toString() ));

		StringReader sr=new StringReader(sBuffer.toString());
		StringWriter sw=new StringWriter();
		transformer.transform(new StreamSource(sr),new StreamResult(sw));
		messageString.append(sw.toString());

		// send order information to database
		boc.sendOrderInfo(lhlUserInfo, sw.toString(), attention, methodName, methodValue, serviceLevel, confirmationEmail, orderNumber);

		messageString.append("\nDelivery method: ").append(methodName).append(", ( ").append( methodValue ).append( " )");
		messageString.append("\nService level: ").append(serviceLevel).append("\n");

		if((accountNumber != null) && !(accountNumber.equals(""))) {
			messageString.append("\nFedEx account: ").append(accountNumber).append("\n");
		}

		messageString.append("Attention: ").append(attention).append("\n");
		messageString.append("E-mail: ").append(deliveryEmail).append("\n");
		messageString.append("Ship to: \n");
		messageString.append(firstName).append(" ").append(lastName).append("\n");
		messageString.append(companyName).append("\n");
		messageString.append(address1).append("\n");
		if(address2 != null &&  !(address2.equals(""))) {
			messageString.append("").append(address2).append("\n");
		}
		messageString.append(city).append(", ").append(state).append(" ").append(country).append(" ").append(zip).append("\n");
		messageString.append(fax).append("\n");
		messageString.append(phone).append("\n");
		messageString.append(confirmationEmail).append("\n");
		messageString.append("\nOrder submitted: ").append(StringUtil.getFormattedDate("EEE, dd MMM yyyy 'at' HH:mm:ss (z)")).append("\n");


		List toAddress=new ArrayList();
		List ccAddress=new ArrayList();

		// send email to Linday Hall Library reference
		toAddress.add("ei@lindahall.org");
//		toAddress.add("j.moschetto@elsevier.com");

		// add ei customer support to (CC) receipients
		ccAddress.add("eicustomersupport@elsevier.com");

		// jam - 5/12/2003 - Ariel "Address" is not an email
		// so do not send to supplied address
		if(!"Ariel".equalsIgnoreCase(methodName)) {
			// send email to delivery address, if email delivery is specified
			if(deliveryEmail != null && !deliveryEmail.trim().equals("")) {
				toAddress.add(deliveryEmail);
			}
		}

		// ALWAYS send to confirmation email address!!
		// send to given confirmation address
		if(confirmationEmail != null && !confirmationEmail.trim().equals("")) {
			toAddress.add(confirmationEmail);
		}

		from="eicustomersupport@elsevier.com";
		subject ="EV2 Linda Hall Library Document Request";

		long lo=System.currentTimeMillis();
		java.util.Date d=new java.util.Date(lo);

		// create an instance of EMail Object for sending email
		EMail email=EMail.getInstance();
		// create an instance of eimessage and call the respective set methods
		EIMessage eimessage = new EIMessage();
    //setSender no longer sets from address - use setFrom for From and setSender for Sender
    //eimessage.setSender(from);
    eimessage.setSender(EIMessage.DEFAULT_SENDER);
    eimessage.setFrom(from);

		eimessage.addTORecepients(toAddress);
		eimessage.addCCRecepients(ccAddress);
		eimessage.setSentDate(d);
		eimessage.setSubject(subject);

		eimessage.setMessageBody(messageString.toString());
		email.sendMessage(eimessage);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}

%>
<root>
</root>
