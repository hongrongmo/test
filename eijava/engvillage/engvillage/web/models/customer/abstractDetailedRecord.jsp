<%@ page language="java" %><%@ page session="false" %><%@ page import="java.util.*"%><%@ page import="java.io.FileWriter"%><%@ page import="java.net.URLDecoder"%><%@ page import="java.net.URLEncoder"%><%@ page import="org.ei.controller.ControllerClient"%><%@ page import="org.ei.session.*"%><%@ page import="org.ei.domain.*" %><%@ page import="org.ei.config.*"%><%@ page import="org.ei.parser.base.*"%><%@ page import="org.ei.query.base.*"%><%@ page import="org.ei.domain.personalization.GlobalLinks"%><%@ page import="org.ei.domain.personalization.SavedSearches"%><%@ page import="org.ei.domain.Searches"%><%@ page import="org.ei.tags.*"%><%@ page import="org.ei.config.*"%><%@ page import="org.ei.books.BookDocument"%><%@ page import="org.ei.util.*"%><%@ page import="java.util.*"%><%@ page import="java.net.*"%><%@ page import="java.io.*"%><%@ page errorPage="/error/errorPage.jsp"%><%@ page buffer="20kb"%><%
    FastSearchControl sc = null;
    PageEntry entry =null;
    EIDoc curDoc =null;
    SearchResult result=null;
    String totalDocCount=null;
    String sessionId=null;
    SessionID sessionIdObj = null;

    String pUserId = null;
    boolean personalization = false;

    String searchID=null;
    String database=null;
    String format=null;
    String cid=null;
    String searchtype=null;
    String currentRecord=null;
    String resultsPage=null;
    Query tQuery = null;
    String serverName = null;
    String dataFormat=null;
    ClientCustomizer clientCustomizer=null;
    boolean isPersonalizationPresent=true;
    boolean isLHLPresent=true;
    boolean isFullTextPresent=true;
    boolean isBlogLinkPresent=false;
    boolean isLocalHolidinsPresent=true;
    boolean maintainCache = false;
    String customizedLogo="";
    String customizedStartYear="";
    String customizedEndYear="";
    DocID did = null;

    LocalHolding localHolding=null;
%><%!
    DatabaseConfig databaseConfig = null;
    int pagesize = 0;
    int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);

    public void jspInit()
    {
        try
        {
            // Get the value of the number of documents to be displayed in a search results page form Runtime.properties file
            RuntimeProperties runtimeProps = ConfigService.getRuntimeProperties();
            pagesize = Integer.parseInt(runtimeProps.getProperty("PAGESIZE"));

            databaseConfig = DatabaseConfig.getInstance();

            // jam Y2K3
            customizedEndYear = Integer.parseInt(runtimeProps.getProperty("SYSTEM_ENDYEAR"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
%><%
    try
    {
        //Getting sessionid from controllerClient
        ControllerClient client =  null;

        if(request.getAttribute("CONTROLLER_CLIENT") != null)
        {
        	client = (ControllerClient)request.getAttribute("CONTROLLER_CLIENT");
    	}
    	else
    	{
        	client = new ControllerClient(request, response);
    	}

        UserSession ussession=(UserSession)client.getUserSession();
        sessionId=ussession.getID();
        sessionIdObj = ussession.getSessionID();
        serverName= ussession.getProperty("ENV_BASEADDRESS");

        pUserId = ussession.getProperty("P_USER_ID");
        if((pUserId != null) && (pUserId.trim().length() != 0))
        {
          personalization=true;
        }

        User user=ussession.getUser();
        String[] credentials = user.getCartridge();
        String customerId=user.getCustomerID().trim();
        localHolding=new LocalHolding(ussession);

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


        // This is used in displaying search results in the appropriate format
        if(request.getParameter("format")!=null)
        {
          format=request.getParameter("format").trim();
          //cid=format;
        }

    	if(request.getParameter("RETURNCID") != null)
    	{
    		cid = request.getParameter("RETURNCID");
    	}
    	else
    	{
    		cid = request.getParameter("CID");
    	}


        if(request.getParameter("database")!=null)
        {
         	database=request.getParameter("database").trim();
        }
        if(request.getParameter("searchtype")!=null)
        {
         	searchtype=request.getParameter("searchtype").trim();
        }

        if(request.getParameter("database")!=null)
        {
          database=request.getParameter("database").trim();
        }

        if(currentRecord == null )
        {
          currentRecord = "1";
        }
        int index=Integer.parseInt(currentRecord);

        if(format!=null)
        {
        	if(format.endsWith("AbstractFormat"))
        	{
          	  dataFormat = Abstract.ABSTRACT_FORMAT;
          	}
          	else if(format.endsWith("TocFormat"))
          	{
          	  dataFormat = Abstract.ABSTRACT_FORMAT;
          	}
          	else if(format.endsWith("DetailedFormat"))
          	{
          	  dataFormat = FullDoc.FULLDOC_FORMAT;
          	}
        }
        else
        {
        	format = Abstract.ABSTRACT_FORMAT;
        	dataFormat = Abstract.ABSTRACT_FORMAT;
        }

    	StringBuffer backurl = new StringBuffer();
    	backurl = getbackURL(cid,searchID,currentRecord,resultsPage,database,format);

    	if(request.getAttribute("BACK_URL") != null)
    	{
    		backurl.append((StringBuffer)request.getAttribute("BACK_URL"));
    	}

    	String backURLString = backurl.toString();

        String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

    	int recnum = -1;
    	int totalcount = -1;

        if(request.getAttribute("PAGE_ENTRY") != null)
        {
    		entry = (PageEntry)request.getAttribute("PAGE_ENTRY");
    		tQuery = (Query)request.getAttribute("QUERY");
	}
        else
        {

    		tQuery = Searches.getSearch(searchID);
    		if(tQuery == null)
    		{
    	        tQuery = SavedSearches.getSearch(searchID);
    		}
    		tQuery.setSearchQueryWriter(new FastQueryWriter());
    		tQuery.setDatabaseConfig(databaseConfig);
    		tQuery.setCredentials(user.getCartridge());

		/*
		*   Handle the Hit Highlighting
		*/
		BooleanQuery booleanQuery = tQuery.getParseTree();
		HitHighlighter highlighter = new HitHighlighter(booleanQuery);

		sc = new FastSearchControl();
            	sc.setHitHighlighter(highlighter);
            	result = sc.openSearch(tQuery,
                        	       sessionId,
                        	       pagesize,
                        	       true);

            	entry = result.entryAt(index, dataFormat);
            	maintainCache = true;
            	totalDocCount = tQuery.getRecordCount();
            	recnum = Integer.parseInt(currentRecord);
		totalcount = Integer.parseInt(totalDocCount);
        }

        curDoc = entry.getDoc();
        curDoc.setDeep(true);
        client.setView(curDoc.getView());
        did = curDoc.getDocID();


		StringBuffer prevurl = new StringBuffer();
		if(request.getAttribute("PREV_URL") == null)
		{
			if(recnum > 1)
			{
				prevurl = getprevURL(cid,recnum,searchID,database,format);
			}
		}
		else
		{
			prevurl = (StringBuffer)request.getAttribute("PREV_URL");
		}

		StringBuffer nexturl = new StringBuffer();
		if(request.getAttribute("NEXT_URL") == null)
		{
			if(recnum < totalcount)
			{
				nexturl = getnextURL(cid,recnum,searchID,database,format);
			}
		}
		else
		{
			nexturl = (StringBuffer)request.getAttribute("NEXT_URL");
		}

		StringBuffer srurl = new StringBuffer();
		if(request.getAttribute("SEARCH_RESULTS_URL") == null)
		{
			srurl = getsrURL(recnum,searchID,database,tQuery);
		}
		else
		{
			srurl = (StringBuffer)request.getAttribute("SEARCH_RESULTS_URL");
		}

		StringBuffer dedupurl = new StringBuffer();
		if(request.getAttribute("DEDUP_RESULTS_URL") != null)
		{
			dedupurl = (StringBuffer)request.getAttribute("DEDUP_RESULTS_URL");
		}

		StringBuffer nsurl = new StringBuffer();
		if(request.getAttribute("NEW_SEARCH_URL") == null)
		{
			nsurl = getnsURL(database,tQuery);
		}
		else
		{
			nsurl = (StringBuffer)request.getAttribute("NEW_SEARCH_URL");
		}

		StringBuffer absurl = new StringBuffer();
		if(request.getAttribute("ABS_URL") == null)
		{
			absurl = getabsURL(tQuery,recnum,searchID,database);
		}
		else
		{
			absurl = (StringBuffer)request.getAttribute("ABS_URL");
		}


		StringBuffer deturl = new StringBuffer();
		if(request.getAttribute("DET_URL") == null)
		{
			deturl = getdetURL(tQuery,recnum,searchID,database);
		}
		else
		{
			deturl = (StringBuffer)request.getAttribute("DET_URL");
		}


		StringBuffer patrefurl = new StringBuffer();
		if(request.getAttribute("PATREF_URL") == null)
		{
			patrefurl = getpatrefURL(tQuery,recnum,searchID,database,did);
		}
		else
		{
			patrefurl = (StringBuffer)request.getAttribute("PATREF_URL");
		}

		StringBuffer nonpatrefurl = new StringBuffer();
		if(request.getAttribute("NONPATREF_URL") == null)
		{
			nonpatrefurl = getnonpatrefURL(tQuery,recnum,searchID,database,did);
		}
		else
		{
			nonpatrefurl = (StringBuffer)request.getAttribute("NONPATREF_URL");
		}


		StringBuffer addTagURL = new StringBuffer();
		if(request.getAttribute("ADD_TAG_URL") == null)
		{
			addTagURL = getaddURL(backURLString, database);
		}
		else
		{
			addTagURL = (StringBuffer)request.getAttribute("ADD_TAG_URL");
		}

		StringBuffer editTagURL = new StringBuffer();
		if(request.getAttribute("EDIT_TAG_URL") == null)
		{
			editTagURL = geteditURL(backURLString);
		}
		else
		{
			editTagURL = (StringBuffer)request.getAttribute("EDIT_TAG_URL");
		}

		StringBuffer bookdeturl = new StringBuffer();

		if(request.getAttribute("BOOKDET_URL") == null)
		{
			String pii = null;
			if(curDoc.isBook())
			{
				pii = ((BookDocument) curDoc).getChapterPii();
			}
			bookdeturl = getbookdetURL(tQuery,recnum,searchID,database,did,pii);
		}
		else
		{
			bookdeturl = (StringBuffer) request.getAttribute("BOOKDET_URL");
		}


		String searchContext = "default";
		if(request.getAttribute("SEARCH_CONTEXT") != null)
		{
			searchContext = (String)request.getAttribute("SEARCH_CONTEXT");
		}

        /**
        *   Log Functionality
        */
        client.log("DB_NAME", Integer.toString(did.getDatabase().getMask()));
        client.log("NUM_RECS", "1");
        client.log("context", "search");
        client.log("DOC_INDEX", Integer.toString(index));
        client.log("DOC_ID", did.getDocID());
        client.log("FORMAT", dataFormat);
        client.log("ACTION", "document");
        if(curDoc.getISSN()!=null)
        {
        	client.log("ISSN", curDoc.getISSN());
        }
        client.setRemoteControl();
        out.write("<PAGE>");
        out.write("<SEARCH-CONTEXT>");
        out.write(searchContext);
        out.write("</SEARCH-CONTEXT>");
        if(tQuery != null)
        {
          out.write(tQuery.toXMLString());
        }
        else
        {
          String altDisplayQuery = (String)request.getAttribute("ALT_DISPLAY_QUERY");
          out.write("<ALT-DISPLAY-QUERY><![CDATA[");
          out.write(altDisplayQuery);
          out.write("]]></ALT-DISPLAY-QUERY>");
          out.write("<SESSION-DATA><SEARCH-TYPE>Tags</SEARCH-TYPE></SESSION-DATA>");
        }

        out.write("<BACKURL>");
        out.write(URLEncoder.encode(backURLString));
        out.write("</BACKURL>");
        out.write("<PAGE-NAV>");
        out.write("<RESULTS-NAV>");
        out.write(URLEncoder.encode(srurl.toString()));
        out.write("</RESULTS-NAV>");
        out.write("<NEWSEARCH-NAV>");
        out.write(URLEncoder.encode(nsurl.toString()));
        out.write("</NEWSEARCH-NAV>");

        if(dedupurl.length() > 0)
        {
    	    out.write("<DEDUP-RESULTS-NAV>");
    	    out.write(dedupurl.toString());
          out.write("</DEDUP-RESULTS-NAV>");
        }
        out.write("<ABS-NAV>");
        out.write(absurl.toString());
        out.write("</ABS-NAV>");
        out.write("<DET-NAV>");
        out.write(deturl.toString());
        out.write("</DET-NAV>");
        out.write("<BOOKDET-NAV>");
        out.write(bookdeturl.toString());
        out.write("</BOOKDET-NAV>");
        out.write("<PATREF-NAV>");
        out.write(patrefurl.toString());
        out.write("</PATREF-NAV>");
        out.write("<NONPATREF-NAV>");
        out.write(nonpatrefurl.toString());
        out.write("</NONPATREF-NAV>");

        if(prevurl.length() > 0)
        {
    	    out.write("<PREV>");
    	    out.write(prevurl.toString());
    	    out.write("</PREV>");
      	}
      	if(nexturl.length() > 0)
      	{
          out.write("<NEXT>");
      		out.write(nexturl.toString());
          out.write("</NEXT>");
        }
        out.write("</PAGE-NAV>");
        TagBubble bubble = new TagBubble(addTagURL.toString(),
        								 editTagURL.toString(),
										    "nextURL",
        								 pUserId,
        								 customerId,
        								 did,
        								 new ScopeComp());
        // here are the tags:

        EditGrouper grouper = new EditGrouper(pUserId,
        									  customerId,
        									  did.getDocID());

        bubble.toXML(out);
        grouper.editXML(bubble.getTags(), out);

        out.write("<HEADER/>");
        out.write("<ABSTRACT-DETAILED-NAVIGATION-BAR/>");
        out.write("<DBMASK>");
        out.write(database);
        out.write("</DBMASK>");
        out.write("<FOOTER/>");
        out.write(strGlobalLinksXML);
        out.write("<ABSTRACT-DETAILED-RESULTS-MANAGER/>");
        out.write("<SEARCH/>");
        out.write("<SESSION-TABLE/>");
        out.write("<CUSTOMIZED-STARTYEAR>"+customizedStartYear+"</CUSTOMIZED-STARTYEAR>");
        out.write("<CUSTOMIZED-ENDYEAR>"+customizedEndYear+"</CUSTOMIZED-ENDYEAR>");
        out.write(localHolding.getLocalHoldingsXML(curDoc));
        out.write("<CID>"+cid+"</CID>");
        // jam - added format which had been ignored since output format is hardcoded in the CIDs
        out.write("<FORMAT>"+format+"</FORMAT>");
        out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
        out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
        if(curDoc.hasFulltextandLocalHoldingsLinks())
        {
          out.write("<LHL>"+isLHLPresent+"</LHL>");
          out.write("<LOCALHOLDINGS>"+isLocalHolidinsPresent+"</LOCALHOLDINGS>");
          out.write("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>");
        }
        out.write("<BLOGLINK>"+isBlogLinkPresent+"</BLOGLINK>");
        out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
        out.write("<RESULTS-COUNT>"+totalDocCount+"</RESULTS-COUNT>");
        out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
        out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
        out.write("<DATABASE-MASK>");
        out.write(database);
        out.write("</DATABASE-MASK>");
        out.write("<SEARCH-ID><![CDATA["+searchID+"]]></SEARCH-ID>");
        out.write("<SERVER-NAME>"+serverName+"</SERVER-NAME>");
        out.write("<PAGE-INDEX>"+resultsPage+"</PAGE-INDEX>");
        out.write("<PREV-PAGE-ID>"+(index-1)+"</PREV-PAGE-ID>");
        out.write("<CURR-PAGE-ID>"+index+"</CURR-PAGE-ID>");
        out.write("<NEXT-PAGE-ID>"+(index+1)+"</NEXT-PAGE-ID>");
        out.write("<SEARCH-TYPE-TAG>"+searchtype+"</SEARCH-TYPE-TAG>");
        out.write("<PAGE-RESULTS>");
        databaseConfig.toXML(credentials, out);
        entry.toXML(out);

        out.write("</PAGE-RESULTS>");
        out.write("</PAGE>");
        out.write("<!--END-->");
        out.flush();
        if(maintainCache)
        {
        	sc.maintainCache(index,0);
        }
    }
    finally
    {
        if(sc != null)
        {
            sc.closeSearch();
        }
    }
%><%!

	StringBuffer getbackURL(String cid,String searchID,String currentRecord,String resultsPage, String database,String format)
	{
		StringBuffer backurlbuf = new StringBuffer();
		backurlbuf.append("CID=").append(cid).append("&");
		backurlbuf.append("SEARCHID=").append(searchID).append("&");
		backurlbuf.append("DOCINDEX=").append(currentRecord).append("&");
		backurlbuf.append("PAGEINDEX=").append(resultsPage).append("&");
		backurlbuf.append("database=").append(database).append("&");
		backurlbuf.append("format=").append(format);
		return backurlbuf;
	}



	StringBuffer getprevURL(String cid,int recnum,String searchID,String database,String format)
	{
		StringBuffer prevurlbuf = new StringBuffer();
		prevurlbuf.append("CID=").append(cid).append("&amp;");
		prevurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		prevurlbuf.append("DOCINDEX=").append(Integer.toString((recnum-1))).append("&amp;");
		prevurlbuf.append("database=").append(database).append("&amp;");
		prevurlbuf.append("format=").append(format);
		return prevurlbuf;
	}

	StringBuffer getnextURL(String cid,int recnum,String searchID,String database,String format)
	{
		StringBuffer nexturlbuf = new StringBuffer();
		nexturlbuf.append("CID=").append(cid).append("&amp;");
		nexturlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		nexturlbuf.append("DOCINDEX=").append(Integer.toString((recnum+1))).append("&amp;");
		nexturlbuf.append("database=").append(database).append("&amp;");
		nexturlbuf.append("format=").append(format);
		return nexturlbuf;
	}

	StringBuffer getsrURL(int recnum,String searchID,String database,Query tQuery)
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
		nsurlbuf.append("CID=").append(XSLCIDHelper.newSearchCid(tQuery.getSearchType())).append("&");
		nsurlbuf.append("database=").append(database);
		return nsurlbuf;
	}

	StringBuffer getabsURL(Query tQuery,int recnum,String searchID,String database)
	{
		StringBuffer absurlbuf = new StringBuffer();
		absurlbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"AbstractFormat").append("&amp;");
		absurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		absurlbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		absurlbuf.append("database=").append(database).append("&amp;");
		absurlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"AbstractFormat");
		return absurlbuf;
	}

	StringBuffer getpatrefURL(Query tQuery, int recnum,String searchID,String database, DocID docID)
	{
		StringBuffer patrefbuf = new StringBuffer();
		patrefbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"ReferencesFormat").append("&amp;");
		patrefbuf.append("SEARCHID=").append(searchID).append("&amp;");
		patrefbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		patrefbuf.append("database=").append(database).append("&amp;");
		patrefbuf.append("docid=").append(docID.getDocID()).append("&amp;");
		patrefbuf.append("format=").append("ReferencesFormat");
		return patrefbuf;
	}

	StringBuffer getnonpatrefURL(Query tQuery, int recnum,String searchID,String database, DocID docID)
	{
		StringBuffer patrefbuf = new StringBuffer();
		patrefbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"NonPatentReferencesFormat").append("&amp;");
		patrefbuf.append("SEARCHID=").append(searchID).append("&amp;");
		patrefbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		patrefbuf.append("database=").append(database).append("&amp;");
		patrefbuf.append("docid=").append(docID.getDocID()).append("&amp;");
		patrefbuf.append("format=").append("ReferencesFormat");
		return patrefbuf;
	}


	StringBuffer getdetURL(Query tQuery,int recnum,String searchID,String database)
	{
		StringBuffer deturlbuf = new StringBuffer();
		deturlbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"DetailedFormat").append("&amp;");
		deturlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		deturlbuf.append("DOCINDEX=").append(Integer.toString(recnum)).append("&amp;");
		deturlbuf.append("database=").append(database).append("&amp;");
		deturlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"DetailedFormat");
		return deturlbuf;
	}


	private StringBuffer getaddURL(String backURL, String database)
	{
		StringBuffer addurlbuf = new StringBuffer();
		addurlbuf.append("CID=").append("addTagForward").append("&amp;");
		addurlbuf.append("database=").append(database).append("&amp;");
		addurlbuf.append("RETURNURL=").append(URLEncoder.encode(backURL));

		return addurlbuf;
	}


	private StringBuffer geteditURL(String backURL)
	{
		StringBuffer addurlbuf = new StringBuffer();
		addurlbuf.append("CID=").append("editForward").append("&amp;");
		addurlbuf.append("RETURNURL=").append(URLEncoder.encode(backURL));
		return addurlbuf;
	}

	private StringBuffer getbookdetURL(Query tQuery,int recnum,String searchID,String database, DocID docID, String pii)
	{
		StringBuffer bookdeturlbuf = new StringBuffer();
		bookdeturlbuf.append("CID=bookSummary").append("&amp;");
		bookdeturlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		bookdeturlbuf.append("DOCINDEX=").append(Integer.toString(recnum)).append("&amp;");
		bookdeturlbuf.append("database=").append(database).append("&amp;");
		if(pii != null)
		{
  			bookdeturlbuf.append("pii=").append(pii).append("&amp;");
    	}
		bookdeturlbuf.append("docid=").append(docID.getDocID());
		return bookdeturlbuf;
	}



%>
