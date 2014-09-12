<%--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.database
 * @param java.lang.String.CID
 * @param java.lang.String.SEARCHID
  * @param java.lang.String.SEARCHQUERY
 --%>
<%@page import="org.engvillage.config.RuntimeProperties"%>
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page errorPage="/error/errorPage.jsp" %>
<%@ page buffer="20kb"%>
<%@ page session="false" %>
<%@ page import=" java.util.*"%>
<%@ page import=" java.net.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession" %>
<%@ page import="org.ei.config.*"%>
<%
 	// Variable to hold the no.of documents in the document basket
	int docBasketPageSize = 0 ;

	// variable to hold the Session ID w/o the prefix
	// or could be called the Session Key
	String strSessionKey = null;
	// Variable to hold the default CID for the document Basket.
	// This CID will direct the user to Citationbasket.xsl for all the links that lead to Document basket
	String cid="citationBasket";
	// Variable to hold the URL to which the control is redirected when there are no documents in the Document Basket
	String urlString=null;


	String documentFormat=null;

	int pagesize = 0;
	int numpages = 0;

	ClientCustomizer clientCustomizer=null;
	String customizedLogo="";
    // Object reference to BasketPage object
	BasketPage basketPage = null;
    // Object reference to DocumentBasket object
	DocumentBasket basket = null;

	// Variable used to hold the reference to ControllerClient object
	ControllerClient client = null;
%>


<%!
	/**
	* This jsp init method is called only once, when request comes for the first time.
	* It reads the value of the number of documents to be displyed per page of Docuemnt Basket.
	* This value is read from the Runtime.properties file.
	*/

    // Varibale to hold the size of each page of Document basket
	String pageSize=null;
    int docBasketPageSize=0;
	public void jspInit()
	{
		try
		{
			RuntimeProperties runtimeProps= RuntimeProperties.getInstance();
			pageSize = runtimeProps.getProperty("BASKETPAGESIZE");
			docBasketPageSize=Integer.parseInt(pageSize.trim());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

%>

<%
	// Create a session object using the Controllerclient object
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();

	//client.updateUserSession(ussession);

	strSessionKey = ussession.getSessionid();
	String customerId=ussession.getCustomerid().trim();
	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		customizedLogo=clientCustomizer.getLogo();
	}

	// Retrieve all the request parameters
	if(request.getParameter("CID") != null)
	{
	    cid=request.getParameter("CID").trim();
    }

	basket = new DocumentBasket(strSessionKey);
	int numPages = basket.countPages();

	/*
	 *  Gets the results for the current page
	 */
	if( (cid!=null) && (cid.equals("printCitationSelectedSet")) )
	{
		documentFormat=Citation.CITATION_FORMAT;
	}
	else if( (cid!=null) && (cid.equals("printAbstractSelectedSet")) )
	{
		documentFormat=Abstract.ABSTRACT_FORMAT;
	}
	else if( (cid!=null) && (cid.equals("printDetailedSelectedSet")) )
	{
		documentFormat=FullDoc.FULLDOC_FORMAT;
	}


%>


<%
	if(numPages>0) {

		// This xml is generated when there are documents in the document basket
		StringBuffer  basketContentStringBuffer  = new StringBuffer();
		basketContentStringBuffer.append("<PAGE>")
		.append("<!--BH-->")
		.append("<HEADER>")
		.append("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>")
		.append("</HEADER>")
		.append("<!--EH-->");

		out.write(basketContentStringBuffer.toString());

		for(int z = 1; z<=numPages; z++)
		{
			basketPage = basket.pageAt(z, documentFormat);
			basketPage.toXML(out);
		}

		//Signal the end of records section
		out.write("<!--*-->");
		//Signale footer section
		out.write("<!--BF--><FOOTER/><!--EF-->");
		out.write("</PAGE>");

		//System.out.println(" contents of Selected Set" + basketContentStringBuffer.toString());

		out.flush();

	} else {

		//forward when the no of documents is zero
		if((cid != null) && (!cid.equals("printErrorSelectedSet"))) {
			urlString = "/controller/servlet/Controller?CID=printErrorSelectedSet";
			client.setRedirectURL(urlString);
			client.setRemoteControl();

		} else {

			StringBuffer  basketContentStringBuffer  = new StringBuffer();
			basketContentStringBuffer.append("<PAGE>")
			.append("<!--BH-->")
			.append("<HEADER/>")
			.append("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>")
			.append("<!--EH-->");

			out.write(basketContentStringBuffer.toString());
			//Signal the end of records section
			out.write("<!--*-->");
			//Signale footer section
			out.write("<!--BF--><FOOTER/><!--EF-->");
			out.write("</PAGE>");
			out.flush();

		}
	}

%>
