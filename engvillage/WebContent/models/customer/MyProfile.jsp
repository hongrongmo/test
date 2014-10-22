<!--
This page expects the following params
* @param java.lang.String.displaylogin.
* @param java.lang.String.displayform.
* @param java.lang.String.database.
and
1. displays the login form if the displaylogin param value is not null.
2. displays the login form with error message if the user id or password is invalid.
3. redirects to the appropriate page based on the displayform value
if the authentication is successfull.
-->
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>

<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>

<%

    ControllerClient client = null;
    String displayLogin  = null;
    String displayForm = null;
    String database = null;
    String searchid = null;
    String count = null;
    String docids = null;
	String CID = null;
	String searchType = null;
    String optionValue = null;
    String queryId = null;
    String resultsFormat = null;
    String source = null;

    // Variable to hold the Personalizaton status
    String pUserId = null;
    boolean personalization = false;
    boolean isPersonalizationPresent=true;

    ClientCustomizer clientCustomizer=null;
    // Stores the source attribute of the customized logo image
    String customizedLogo="";

    // get all the parameters required

    if( request.getParameter("displaylogin") != null)
    {
        displayLogin = request.getParameter("displaylogin");
    }
    if( request.getParameter("displayform") != null)
    {
        displayForm = request.getParameter("displayform");
    }
    if( request.getParameter("database") != null)
    {
        database = request.getParameter("database");
    }
    if( request.getParameter("count") != null)
    {
        count = request.getParameter("count");
    }
    if( request.getParameter("searchid") != null)
    {
        searchid = request.getParameter("searchid");
    }
    if( request.getParameter("docidlist") != null)
    {
        docids = request.getParameter("docidlist");
    }
    if( request.getParameter("option") != null)
    {
        optionValue = request.getParameter("option");
    }
    if( request.getParameter("queryid") != null)
    {
        queryId = request.getParameter("queryid");
    }
    if( request.getParameter("resultsformat") != null)
    {
        resultsFormat = request.getParameter("resultsformat");
    }
    if( request.getParameter("source") != null)
    {
        source = request.getParameter("source");
    }

	Query queryObject = null;
	queryObject=Searches.getSearch(searchid);

	if(queryObject!=null)
	{
		searchType = queryObject.getSearchType();
		if(searchType!=null) {
			if(searchType.equalsIgnoreCase(Query.TYPE_QUICK))
			{
				CID="quickSearchCitationFormat";
			}
			else if(searchType.equalsIgnoreCase(Query.TYPE_EASY))
			{
				CID="expertSearchCitationFormat";
			}
			else if(searchType.equalsIgnoreCase(Query.TYPE_EXPERT))
			{
				CID="expertSearchCitationFormat";
			}
			else if(searchType.equalsIgnoreCase(Query.TYPE_THESAURUS))
			{
				CID="thesSearchCitationFormat";
			}
			else if(searchType.equalsIgnoreCase(Query.TYPE_COMBINED_PAST))
			{
				CID="combineSearchHistory";
			}
		}
	}

	out.write("<PAGE>");
  	if( CID != null)
  	{
  		out.write("<CID>");
  		out.write(CID);
  		out.write("</CID>");
  	}

    if( docids != null )
    {
        out.write("<DOCIDS>");
        out.write(docids);
        out.write("</DOCIDS>");
    }
    if( optionValue != null )
    {
        out.write("<OPTION-VALUE>");
        out.write(optionValue);
        out.write("</OPTION-VALUE>");
    }
    if( queryId != null )
    {
        out.write("<QUERY-ID>");
        out.write(queryId);
        out.write("</QUERY-ID>");
    }

    if(source != null)
    {
        out.write("<SOURCE>");
        out.write(source);
        out.write("</SOURCE>");
    }

    if(queryId != null )
    {
        out.write("<RESULTS-FORMAT>");
        out.write(resultsFormat);
        out.write("</RESULTS-FORMAT>");
    }
    if(database != null)
    {
        out.write("<DATABASE>");
        out.write(database);
        out.write("</DATABASE>");
    }

    if(count != null)
    {
        out.write("<COUNT>");
        out.write(count);
        out.write("</COUNT>");
    }

    if(searchid != null)
    {
        out.write("<SEARCH-ID>");
        out.write(searchid);
        out.write("</SEARCH-ID>");
    }

    if(searchType != null)
    {
        out.write("<SEARCH-TYPE>");
        out.write(searchType);
        out.write("</SEARCH-TYPE>");
    }

    //get the client session object from that get the session id.
    client = new ControllerClient(request,response);
    UserSession ussession=(UserSession)client.getUserSession();
    String sSessionId = ussession.getSessionid();

    pUserId = ussession.getUserid();
    if((pUserId != null) && (pUserId.length() != 0))
    {
        personalization=true;
    }

    // NOV 2004
    // jam added when pop-up login removed
    out.write("<HEADER/>");
    out.write(GlobalLinks.toXML(ussession.getCartridge()));
    out.write("<FOOTER/>");

    String customerId=ussession.getCustomerid().trim();
    String strContractId = ussession.getContractid().trim();

    clientCustomizer=new ClientCustomizer(ussession);
    if(clientCustomizer.isCustomized())
    {
        customizedLogo=clientCustomizer.getLogo();
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
    }
	out.write("<PAGE-NAV>");
	if(request.getParameter("searchresults") != null)
	{
		out.write("<RESULTS-NAV>");
		out.write(java.net.URLEncoder.encode(request.getParameter("searchresults")));
		out.write("</RESULTS-NAV>");
	}
	if(request.getParameter("newsearch") != null)
	{
		out.write("<NEWSEARCH-NAV>");
		out.write(java.net.URLEncoder.encode(request.getParameter("newsearch")));
		out.write("</NEWSEARCH-NAV>");
	}
	out.write("</PAGE-NAV>");
    out.write("<SESSION-ID>");
    out.write(ussession.getSessionid());
    out.write("</SESSION-ID>");
    out.write("<CUSTOMIZED-LOGO>");
    out.write(customizedLogo );
    out.write("</CUSTOMIZED-LOGO>");
    out.write("<PERSONALIZATION-PRESENT>");
    out.write(Boolean.toString(isPersonalizationPresent));
    out.write("</PERSONALIZATION-PRESENT>");
    out.write("<PERSONALIZATION>");
    out.write(Boolean.toString(personalization));
    out.write("</PERSONALIZATION>");
    out.write("</PAGE>");
%>


