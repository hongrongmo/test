<%--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.sessionId
 --%>
<%@ page language="java" %>
<%@ page session="false" %>
<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<!--import statements of ei packages.-->
<%@ page import="javax.servlet.jsp.*" %>
<%@ page import="org.ei.connectionpool.*" %>
<%@ page import="org.ei.controller.ControllerClient" %>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks" %>
<%@ page import="org.ei.domain.Searches "%>
<%@ page import="org.ei.domain.Sort" %>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
	// This variable for sessionId
	String sessionId = null;
	SessionID sessionIdObj = null;

	// Variable to hold the Personalization userid
	String pUserId = "";
	// Variable to hold the Personalizaton status
	boolean personalization = false;

	String currentPage=null;

	String searchID=null;
	String dbName=null;
	String searchType=null;
	String CID="";
    String sort="";
	Query queryObject=null;

	String searchHistoryEmpty="false";

	ClientCustomizer clientCustomizer=null;
	boolean isPersonalizationPresent=true;
	boolean isEmailAlertsPresent=true;
	String customizedLogo="";

	// This variable is used to hold ControllerClient instance
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();

	sessionId=ussession.getID();
	sessionIdObj = ussession.getSessionID();
	pUserId = ussession.getUserIDFromSession();
	if((pUserId != null) && (pUserId.trim().length() != 0))
	{
		personalization=true;
	}

	IEVWebUser user = ussession.getUser();
	String customerId=user.getCustomerID().trim();
	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
		isEmailAlertsPresent=clientCustomizer.checkEmailAlert();
		customizedLogo=clientCustomizer.getLogo();
	}

    //Getting the COUNT parameter from the request
    if(request.getParameter("COUNT")!=null)
	{
		currentPage=request.getParameter("COUNT").trim();
	}

    if(request.getParameter("searchHistoryEmpty")!=null)
	{
        searchHistoryEmpty=request.getParameter("searchHistoryEmpty");
	}


	if(request.getParameter("sort")!=null)
	{
		sort = request.getParameter("sort");
	}
	else if (clientCustomizer.getSortBy() != null)
	{
		sort = clientCustomizer.getSortBy();
	}


    /**
	*Getting new searchId for each new search
	* If searchId is excisted then using that id.
	*/
	if(request.getParameter("SEARCHID")!=null)
	{
		searchID=request.getParameter("SEARCHID").trim();
	}

    if(request.getParameter("database")!=null)
    {
		dbName = request.getParameter("database").trim();
	}

	if(request.getParameter("SEARCHTYPE")!=null)
	{
        searchType = request.getParameter("SEARCHTYPE").trim();
	}

	if(request.getParameter("searchResultsFormat")!=null)
	{
        CID = request.getParameter("searchResultsFormat").trim();
	}

    if(CID!=null && CID.trim().length()>0)
    {

	}
	else
	{
		queryObject=Searches.getSearch(searchID);

        if(queryObject!=null)
        {
			String queryType = queryObject.getSearchType();
			if(queryType!=null) {
    			if(queryType.equalsIgnoreCase(Query.TYPE_QUICK))
    			{
    				CID="quickSearchCitationFormat";
    			}
    			else if(queryType.equalsIgnoreCase(Query.TYPE_EASY))
    			{
    				CID="expertSearchCitationFormat";
    			}
    			else if(queryType.equalsIgnoreCase(Query.TYPE_EXPERT))
    			{
    				CID="expertSearchCitationFormat";
    			}
    			else if(queryType.equalsIgnoreCase(Query.TYPE_THESAURUS))
    			{
    				CID="thesSearchCitationFormat";
    			}
    			else if(queryType.equalsIgnoreCase(Query.TYPE_COMBINED_PAST))
    			{
    				CID="combineSearchHistory";
    			}
            }
		}
	}

	String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
	//Writing out xml for that sessionId
	out.write("<PAGE>");

    StringBuffer backurl = new StringBuffer();
    backurl.append("CID=").append("viewCompleteSearchHistory").append("&");
    backurl.append("COUNT=").append(currentPage).append("&");
    backurl.append("SEARCHID=").append(searchID).append("&");
    backurl.append("database=").append(dbName).append("&");
    backurl.append("SEARCHTYPE=").append(searchType);

    out.write("<BACKURL>");
	out.write(java.net.URLEncoder.encode(backurl.toString()));
	out.write("</BACKURL>");
	out.write("<PAGE-NAV>");
	if(request.getParameter("searchresults") != null)
	{
		out.write("<RESULTS-NAV>");
		out.write(URLEncoder.encode(request.getParameter("searchresults")));
		out.write("</RESULTS-NAV>");
	}
	if(request.getParameter("newsearch") != null)
	{
		out.write("<NEWSEARCH-NAV>");
		out.write(URLEncoder.encode(request.getParameter("newsearch")));
		out.write("</NEWSEARCH-NAV>");
	}
	out.write("</PAGE-NAV>");
	out.write("<HEADER/>");
	out.write("<FOOTER/>");
	out.write("<DBMASK>");
	out.write(dbName);
	out.write("</DBMASK>");
	out.write(strGlobalLinksXML);
	out.write("<NAVIGATION-BAR/>");
	out.write("<SESSION-TABLE/>");
	out.write("<CID>"+CID+"</CID>");
	out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
	out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
	out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
	out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
	out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
	out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
	out.write("<PERSON-USER-ID>"+pUserId+"</PERSON-USER-ID>");
	out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
	out.write("<SESSION-DATA>");
	out.write("<SEARCH-TYPE>"+searchType+"</SEARCH-TYPE>");
    out.write("<SORT-OPTION>"+sort+"</SORT-OPTION>");

	out.write("<DATABASE><SHORTNAME>"+dbName+"</SHORTNAME></DATABASE>");
	out.write("</SESSION-DATA>");
	out.write("<ISHISTORY-EMPTY>"+searchHistoryEmpty+"</ISHISTORY-EMPTY>");
	out.write("<CURR-PAGE-ID>"+currentPage+"</CURR-PAGE-ID>");

	Searches.getSessionXMLQuery(pUserId,sessionIdObj.getID(),out);

    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    databaseConfig.sortableToXML(user.getCartridge(), out);

	out.write("</PAGE>");



%>


