<!--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.database
 * @param java.lang.String.CID
 * @param java.lang.String.SEARCHID
 * @param java.lang.String.SEARCHTYPE
 * @param java.lang.String.DATABASETYPE
 * @param java.lang.String.DATABASEID
 * @param java.lang.String.BASKETCOUNT
 * @param java.lang.String.SEARCHQUERY
 -->
<%@page import="org.ei.config.ApplicationProperties"%>
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false"%>
<%@ page errorPage="/error/errorPage.jsp" %>
<%@ page buffer="20kb"%>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<!--import statements of ei packages.-->
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession" %>
<%@ page import="org.ei.config.*"%>
<%
 	// Variable to hold the no.of documents in the document basket
	int docBasketPageSize = 0 ;
	// Varaible to hold the current session id
	String sessionid = null;
	// Variable to hold the Personalization userid
	String pUserId = null;
	// Variable to hold the count of the page of the search results to display,
	// i.e the search results page to which the user is returned when the search results button is clicked document basket pages
	String currentPage = null;
    // Variable that is used to calculate the Docuemnt Basket page that is to be shown when the user clicks either the View Selected Records/Record basket link
	int index = 0;
	// Variable to hold the no. of documents available in the Document Basket
	int basketSize = 0;
	// Variable to hold the database name for which the search results were obtained.
	// eg. Compendex Chemistry, CBNB, Beisltein Astracts,
	// Used in the Search Results link
	String databaseType=null;
	// Variable to hold the default CID for the document Basket.
	// This CID will direct the user to Citationbasket.xsl for all the links that lead to Document basket
	String cid="citationBasket";
	// Variable to hold the database id used in the DatabaseConfig object,
	// used to retrieve the databases information for a particular data container eg. cpxchem, beilstein,cbnb.
	// Used in the Search Results link
	String databaseID=null;
	// Variable to hold the URL to which the control is redirected when there are no documents in the Document Basket
	String urlString=null;
	// Variable to hold the page of the basket currently being displayed.
	String basketCount=null;
	// Variable that hold the search type of the search executed - Quick or Expert.
	// Used in the Search Results link
	String searchType=null;
	// Variable to hodl the navigation details
	String navigation=null;
	// Variable that holds the booelan value to decide if the control is to be redirected to the error document basket page
	boolean flag = false ;
	String navigator=null;

	String documentFormat=null;
	List docBasketDatabase = null;
	int databaseCount = 0;

	String clearOnValue = null;
	int pagesize = 0;
	int numpages = 0;

	ClientCustomizer clientCustomizer=null;
	String customizedLogo="";

    // Object reference to BasketPage object
    BasketPage basketPage = null;
    // Object reference to DocumentBasket object
    DocumentBasket basket = null;
    //int  value which represents documents from single search returns 0 and multiple search,the return value is 1
    boolean hasMultipleSearchIds=false;
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
			ApplicationProperties runtimeProps= ApplicationProperties.getInstance();
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
	sessionid = ussession.getSessionid();
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

	String strDownloadFormat = request.getParameter("format");
	String strDisplayFormat = "";

	basket = new DocumentBasket(sessionid);
	int numPages = basket.countPages();


	/*
	 *  Gets the results for the current page
	 */
	if((strDownloadFormat != null) && (strDownloadFormat.equalsIgnoreCase("ASCII") )) {
		// if ASCII then DisplayFormat matters (i.e. Citation,Abstract,fullDoc))
		strDisplayFormat = request.getParameter("displayformat");

		if( (strDisplayFormat!=null) && (strDisplayFormat.equalsIgnoreCase("citation")) ) {
			documentFormat=Citation.CITATION_FORMAT;
		} else if( (strDisplayFormat!=null) && (strDisplayFormat.equalsIgnoreCase("abstract")) ) {
			documentFormat=Abstract.ABSTRACT_FORMAT;
		} else if( (strDisplayFormat!=null) && (strDisplayFormat.equalsIgnoreCase("fulldoc")) ) {
			documentFormat=FullDoc.FULLDOC_FORMAT;
		}
	} else {
		// if RIS - use fulldoc to build XML
		// Stylesheet will build RIS format from FULL_DOC 'records'
		documentFormat = "RIS";
	}

	if(numPages>0)
	{
		// This xml is generated when there are documents in the document basket


		// jam 10/1/2002
		// Added code for setting filename
		// Client.setContentDispositionFilenameTimestamp()
		// method will timestamp and encode filename
		StringBuffer strbFilename = new StringBuffer();
		strbFilename.append(documentFormat);
		strbFilename.append("_");
		strbFilename.append(strDownloadFormat);
		if(strDownloadFormat.equalsIgnoreCase("ASCII"))
		{
			strbFilename.append("_.txt");
		}
		else if (strDownloadFormat.equalsIgnoreCase("bib"))
    {
			strbFilename.append("_.bib");
		}
		else
    {
			strbFilename.append("_.ris");
		}
    // do not set disposition or filename - refworks needs only mime type
    if(!strDownloadFormat.equalsIgnoreCase("refworks"))
    {
      client.setContentDispositionFilenameTimestamp(strbFilename.toString());
    }
		client.setRemoteControl();

		StringBuffer  basketContentStringBuffer  = new StringBuffer();

		basketContentStringBuffer.append("<PAGE><!--BH-->");
		basketContentStringBuffer.append("<!--EH-->");

		out.write(basketContentStringBuffer.toString());
		for(int z = 1; z<=numPages; ++z)
		{
			basketPage = basket.pageAt(z,documentFormat);

			// if the download format is not ASCII
			if(!strDownloadFormat.equalsIgnoreCase("ASCII"))
			{
				int basketPageSize = basketPage.docCount();

				for(int x = 0; x< basketPageSize; x++)
				{
						// walk basket selecting only records that match
						// the db chosen for RIS
						BasketEntry be = basketPage.docAt(x);
						//String db= be.getEIDoc().getDocID().getDatabase().getID();

						if(strDownloadFormat.equalsIgnoreCase("ris") || strDownloadFormat.equalsIgnoreCase("bib") ||strDownloadFormat.equalsIgnoreCase("refworks"))
						{
							be.toXML(out);
						}
				} // for
			}
			else
			{
				basketPage.toXML(out);
			} // if-else

		} // for

		//Signal the end of records section
		out.write("<!--*-->");
		//Signal footer section
		out.write("<!--BF--><FOOTER/><!--EF-->");
		out.write("</PAGE>");
		out.flush();

	}
	else
	{

		// This "should" never happen because downloadForm.jsp (DownloadForm.xsl)
		// which is called before this does the same check, preventing
		// the display of the download form when the basket is empty
		//
		// forward when the no of documents in basket is zero
		if((cid != null) && (!cid.equals("printErrorSelectedSet")))
		{
			urlString = "/controller/servlet/Controller?CID=printErrorSelectedSet";
			client.setRedirectURL(urlString);
			client.setRemoteControl();
		}
	}

%>
