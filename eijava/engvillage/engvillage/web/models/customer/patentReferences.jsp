<%@ page language="java" %>
<%@ page session="false" %>
<!--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.database
 * @param java.lang.String.totalDocCount
 * @param java.lang.String.sessionId
 * @param java.lang.String.searchID
 -->

<!-- import statements of Java packages-->

<%@ page import="java.util.*"%>
<%@ page import="java.io.FileWriter"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="java.net.URLEncoder"%>

<!--import statements of ei packages.-->

<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.util.*"%>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.data.upt.runtime.*"%>


<%@ page errorPage="/error/errorPage.jsp"%>

<%@ page buffer="20kb"%>

<%
    SearchControl sc = null;
    EIDoc curDoc =null;

    SearchResult result=null;
    Query queryObject = null;
    int offset = 0;
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    String sessionId=null;
    SessionID sessionIdObj = null;

    String pUserId = null;
    boolean personalization = false;

    String searchID=null;
    String database=null;
    String format=null;
    String cid=null;
    int pageSize=0;

    String currentRecord=null;
    String resultsPage=null;
    String navigator=null;

    ClientCustomizer clientCustomizer=null;
    boolean isPersonalizationPresent=true;
    boolean isLHLPresent=true;
    boolean isFullTextPresent=true;
    boolean isBlogLinkPresent=false;
    boolean isLocalHolidinsPresent=true;
    String customizedLogo="";

    String customizedStartYear="";
    String customizedEndYear="";

%>

<%

    try
    {
        // Get the value of the number of documents to be displayed in a search results page form Runtime.properties file
        RuntimeProperties runtimeProps = ConfigService.getRuntimeProperties();
        pageSize = Integer.parseInt(runtimeProps.getProperty("PAGESIZE"));
        String endYear = runtimeProps.getProperty("SYSTEM_ENDYEAR");


        //Getting sessionid from controllerClient
        ControllerClient client = new ControllerClient(request, response);
        UserSession ussession=(UserSession)client.getUserSession();
        sessionId=ussession.getID();
        sessionIdObj = ussession.getSessionID();
        pUserId = ussession.getProperty("P_USER_ID");
        if((pUserId != null) && (pUserId.trim().length() != 0))
        {
            personalization=true;
        }

        User user=ussession.getUser();
        String[] credentials = user.getCartridge();
        String customerId=user.getCustomerID().trim();

        clientCustomizer=new ClientCustomizer(ussession);
        isFullTextPresent=clientCustomizer.checkFullText();
        isBlogLinkPresent=clientCustomizer.checkBlogLink();

        if(clientCustomizer.isCustomized())
        {
            isPersonalizationPresent=clientCustomizer.checkPersonalization();
            isLHLPresent=clientCustomizer.checkDDS();
            isLocalHolidinsPresent=clientCustomizer.checkLocalHolding();
            customizedLogo=clientCustomizer.getLogo();
        }

        // Get the request parameters
        currentRecord=request.getParameter("DOCINDEX");
        resultsPage=request.getParameter("PAGEINDEX");
        searchID=request.getParameter("SEARCHID");


        queryObject = Searches.getSearch(searchID);

        // Get the CID to which the request is to be sent
        // This is used in displaying search results in the appropriate format

        if(request.getParameter("format")!=null)
        {
            format=request.getParameter("format").trim();
            cid = format.trim();
        }

        if(request.getParameter("CID")!=null)
        {
            cid=request.getParameter("CID").trim();
        }

        if(request.getParameter("offset")!=null)
        {
	    	offset=Integer.parseInt(request.getParameter("offset"));;
        }

        if(request.getParameter("database")!=null)
        {
            database=request.getParameter("database").trim();
        }

        String docid = null;

        if(request.getParameter("docid")!=null)
        {
            docid = request.getParameter("docid").trim();
        }

        navigator=request.getParameter("navigator");
        if (navigator == null)
        {
            navigator="NEXT";
        }

        if(currentRecord == null )
        {
            currentRecord = "1";
        }

        int index=Integer.parseInt(currentRecord);


        StringBuffer backurl = new StringBuffer();
		if(request.getAttribute("BACK_URL") == null)
		{
			backurl.append("CID=").append(request.getParameter("CID")).append("&");
			backurl.append("SEARCHID=").append(request.getParameter("SEARCHID")).append("&");
			backurl.append("DOCINDEX=").append(request.getParameter("DOCINDEX")).append("&");
			backurl.append("PAGEINDEX=").append(request.getParameter("PAGEINDEX")).append("&");
			backurl.append("database=").append(request.getParameter("database")).append("&");
			backurl.append("format=").append(request.getParameter("format")).append("&");
			backurl.append("docid=").append(docid);
		}
		else
		{
			backurl = (StringBuffer)request.getAttribute("BACK_URL");
		}


		StringBuffer nextrefurl = new StringBuffer();
		if(request.getAttribute("NEXTREF_URL") == null)
		{
			nextrefurl = new StringBuffer(backurl.toString());
			nextrefurl.append("&offset=");
			nextrefurl.append(Integer.toString(offset+25));
		}
		else
		{
			nextrefurl = (StringBuffer)request.getAttribute("NEXTREF_URL");

		}

		StringBuffer prevrefurl = new StringBuffer();
		if(request.getAttribute("PREVREF_URL") == null)
		{
			prevrefurl = new StringBuffer(backurl.toString());
			prevrefurl.append("&offset=");
			prevrefurl.append(Integer.toString(offset-25));
		}
		else
		{
			prevrefurl = (StringBuffer)request.getAttribute("PREVREF_URL");
		}
		backurl.append("&offset=").append(Integer.toString(offset));




		StringBuffer srurl = new StringBuffer();

		if(request.getAttribute("SEARCH_RESULTS_URL") == null)
		{
			srurl = getsrURL(index,
							 searchID,
							 database,
							 queryObject);
		}
		else
		{
			srurl = (StringBuffer)request.getAttribute("SEARCH_RESULTS_URL");
		}

		StringBuffer dedupurl = null;
		if(request.getAttribute("DEDUP_RESULTS_URL") != null)
		{
			dedupurl = (StringBuffer)request.getAttribute("DEDUP_RESULTS_URL");
		}


		StringBuffer nsurl = new StringBuffer();
		if(request.getAttribute("NEW_SEARCH_URL") == null)
		{
			nsurl = getnsURL(database,
							 queryObject);
		}
		else
		{
			nsurl = (StringBuffer)request.getAttribute("NEW_SEARCH_URL");
		}

		StringBuffer absurl = new StringBuffer();
		if(request.getAttribute("ABS_URL") == null)
		{
			absurl = getabsURL(queryObject,
							   index,
							   searchID,
							   database);
		}
		else
		{
			absurl = (StringBuffer)request.getAttribute("ABS_URL");
		}

		StringBuffer nonpatrefurl = new StringBuffer();
		if(request.getAttribute("NONPATREF_URL") == null)
		{
			nonpatrefurl = getnonpatrefURL(queryObject,index,searchID,database,docid);
		}
		else
		{
			nonpatrefurl = (StringBuffer)request.getAttribute("NONPATREF_URL");
		}

		StringBuffer deturl = new StringBuffer();
		if(request.getAttribute("DET_URL") == null)
		{
			deturl = getdetURL(queryObject,
							   index,
							   searchID,
							   database);
		}
		else
		{
			deturl = (StringBuffer)request.getAttribute("DET_URL");
		}





        sc = new FastSearchControl();

        UPTDocBuilder docBuilder = new UPTDocBuilder();

        List list = new ArrayList();
        Database upt = databaseConfig.getDatabase("upt");
        list.add(new DocID(docid,upt));
        List eiDocs = docBuilder.buildPage(list,Citation.CITATION_FORMAT);
        curDoc = (EIDoc)eiDocs.get(0);


        UPTRefDocBuilder refBuilder = new UPTRefDocBuilder(new UPTRefDatabase());
        RefPager pager = refBuilder.getRefPager(docid,
        										25,
        										sessionId);
        RefPage refPage = pager.getPage(offset);


        String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

        out.write("<PAGE>");
        out.write("<PAGE-INDEX>"+index+"</PAGE-INDEX>");
		out.write("<PAGE-NAV>");
		out.write("<ABS-NAV>");
		out.write(absurl.toString());
		out.write("</ABS-NAV>");
		out.write("<DET-NAV>");
		out.write(deturl.toString());
		out.write("</DET-NAV>");
		out.write("<NONPATREF-NAV>");
		out.write(nonpatrefurl.toString());
		out.write("</NONPATREF-NAV>");
		out.write("<RESULTS-NAV><![CDATA[");
		out.write(srurl.toString());
		out.write("]]></RESULTS-NAV>");
		out.write("<NEWSEARCH-NAV>");
		out.write(nsurl.toString());
		out.write("</NEWSEARCH-NAV>");
		if(dedupurl != null)
		{
			out.write("<DEDUP-RESULTS-NAV>");
			out.write(dedupurl.toString());
			out.write("</DEDUP-RESULTS-NAV>");
		}
		out.write("<PREVREF-NAV><![CDATA[");
		out.write(prevrefurl.toString());
    	out.write("]]></PREVREF-NAV>");
    	out.write("<NEXTREF-NAV><![CDATA[");
		out.write(nextrefurl.toString());
    	out.write("]]></NEXTREF-NAV>");
		out.write("</PAGE-NAV>");
		out.write("<BACKURL-DECODED><![CDATA[");
		out.write(backurl.toString());
        out.write("]]></BACKURL-DECODED>");
        out.write("<BACKURL>");
        out.write(URLEncoder.encode(backurl.toString()));
        out.write("</BACKURL>");
        out.write("<HEADER/>");
        out.write("<ABSTRACT-DETAILED-NAVIGATION-BAR/>");
        out.write("<DBMASK>");
        out.write(database);
        out.write("</DBMASK>");
        out.write("<FOOTER/>");
        out.write(strGlobalLinksXML);
        out.write("<DEDUP>false</DEDUP>");
        out.write("<ABSTRACT-DETAILED-RESULTS-MANAGER/>");
        out.write("<SEARCH/>");
        out.write("<SESSION-TABLE/>");
        out.write("<CUSTOMIZED-STARTYEAR>"+customizedStartYear+"</CUSTOMIZED-STARTYEAR>");
        out.write("<CUSTOMIZED-ENDYEAR>"+customizedEndYear+"</CUSTOMIZED-ENDYEAR>");
        out.write("<CID>"+cid+"</CID>");
        out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
        out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
        out.write("<LHL>"+isLHLPresent+"</LHL>");
        out.write("<BLOGLINK>"+isBlogLinkPresent+"</BLOGLINK>");
        out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
        out.write("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>");
        out.write("<LOCALHOLDINGS>"+isLocalHolidinsPresent+"</LOCALHOLDINGS>");
        out.write("<DATABASE>"+database+"</DATABASE>");
        out.write("<RESULTS-COUNT>1</RESULTS-COUNT>");
        out.write("<SEL-COUNT>"+pager.getSetSize()+"</SEL-COUNT>");
        out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
        out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
        out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
        out.write("<PAGE-INDEX>"+resultsPage+"</PAGE-INDEX>");
        out.write("<PREV-PAGE-ID>"+(index-1)+"</PREV-PAGE-ID>");
        out.write("<CURR-PAGE-ID>"+index+"</CURR-PAGE-ID>");
        out.write("<NEXT-PAGE-ID>"+(index+1)+"</NEXT-PAGE-ID>");
        out.write("<OFFSET>"+offset+"</OFFSET>");
        out.write("<PAGE-RESULTS>");
        databaseConfig.toXML(credentials, out);
      	out.write("<UPT-DOC>");
      	curDoc.toXML(out);
        refPage.toXML(out);
        out.write("</UPT-DOC>");
        out.write("</PAGE-RESULTS>");

      	String sRefQuery = pager.getQuery();
      	Query tQuery = null;
      	if(sRefQuery != null)
      	{
      		String newSearchID = new GUID().toString();
      		tQuery = new Query(databaseConfig, credentials);
      		tQuery.setSearchPhrase(sRefQuery,"","","","","","","");
      		tQuery.setID(newSearchID);
      		tQuery.setDataBase(49152);
      		tQuery.setDeDup(false);
      		tQuery.setSearchType(Query.TYPE_EXPERT);
      		tQuery.setSessionID(sessionId);
      		tQuery.setStartYear("1790");
      		tQuery.setEndYear(endYear);
      		tQuery.setAutoStemming("on");
      		tQuery.setSortOption(new Sort(Sort.PUB_YEAR_FIELD,Sort.DOWN));
      		tQuery.setSearchQueryWriter(new FastQueryWriter());
      		tQuery.compile();
      		System.out.println(tQuery.getSearchQuery());

      		if((navigator != null) && (navigator.equalsIgnoreCase("MORE")))
      		{
      		     ResultsState rs = tQuery.getResultsState();
      		     rs.modifyState(request.getParameter("FIELD"));
      		}

      		result = sc.openSearch(tQuery,
      				      sessionId,
      				      pageSize,
      				      false);
      		ResultNavigator nav = sc.getNavigator();
      		out.write(nav.toXML(tQuery.getResultsState()));
    	  }

        out.write("<HIDE-NAV/>");
    		if(queryObject != null)
    		{
        	out.write(queryObject.toXMLString());
    		}
        else
        {
          out.write("<SESSION-DATA><SEARCH-TYPE>Tags</SEARCH-TYPE></SESSION-DATA>");
        }
        out.write("</PAGE>");
        out.write("<!--END-->");
        out.flush();
    }
    finally
    {
        if(sc != null)
        {
            sc.closeSearch();
        }
    }
%>
<%!
	StringBuffer getsrURL(int recnum,
						  String searchID,
						  String database,
						  Query tQuery)
	{
		StringBuffer srurlbuf = new StringBuffer();
		srurlbuf.append("CID=").append(XSLCIDHelper.searchResultsCid(tQuery.getSearchType())).append("&");
		srurlbuf.append("SEARCHID=").append(searchID).append("&");
		srurlbuf.append("COUNT=").append(Integer.toString((recnum))).append("&");
		srurlbuf.append("database=").append(database);
		return srurlbuf;
	}

	StringBuffer getnsURL(String database,Query tQuery)
	{
		StringBuffer nsurlbuf = new StringBuffer();
		nsurlbuf.append("CID=").append(XSLCIDHelper.newSearchCid(tQuery.getSearchType())).append("&amp;");
		nsurlbuf.append("database=").append(database);
		return nsurlbuf;
	}



	private StringBuffer getabsURL(Query tQuery,
								   int recnum,
								   String searchID,
								   String database)
	{
		StringBuffer absurlbuf = new StringBuffer();
		absurlbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"AbstractFormat").append("&amp;");
		absurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		absurlbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		absurlbuf.append("database=").append(database).append("&amp;");
		absurlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"AbstractFormat");
		return absurlbuf;
	}

	private StringBuffer getnonpatrefURL(Query tQuery,
										 int recnum,
										 String searchID,
										 String database,
										 String docID)
	{
		StringBuffer patrefbuf = new StringBuffer();
		patrefbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"NonPatentReferencesFormat").append("&amp;");
		patrefbuf.append("SEARCHID=").append(searchID).append("&amp;");
		patrefbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		patrefbuf.append("database=").append(database).append("&amp;");
		patrefbuf.append("docid=").append(docID).append("&amp;");
		patrefbuf.append("format=").append("ReferencesFormat");
		return patrefbuf;
	}


	private StringBuffer getdetURL(Query tQuery,
								   int recnum,
								   String searchID,
								   String database)
	{
		StringBuffer deturlbuf = new StringBuffer();
		deturlbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"DetailedFormat").append("&amp;");
		deturlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		deturlbuf.append("DOCINDEX=").append(Integer.toString(recnum)).append("&amp;");
		deturlbuf.append("database=").append(database).append("&amp;");
		deturlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"DetailedFormat");
		return deturlbuf;
	}
%>