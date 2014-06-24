<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="application/xml; charset=UTF-8"%>
<%@page import="org.apache.commons.validator.GenericValidator"%>
<%@page import="org.ei.domain.navigators.state.ResultNavigatorStateHelper"%>
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>

<!-- import statements of Java packages-->
<%@page import="org.ei.exception.EVBaseException"%>
<%@page import="org.ei.exception.SystemErrorCodes"%>
<%@page import="org.ei.exception.SearchException"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<!--import statements of ei packages.-->
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.util.StringUtil"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>
<%@ page import="org.ei.domain.Searches"%>
<%
	String update = "";
    String currentPage = null;
    SearchControl sc = new FastSearchControl();
    SearchResult result=null;
    Query queryObject = null;

    Page oPage=null;
    ControllerClient client = new ControllerClient(request, response);
    LocalHolding localHolding = null;
    String sessionId = null;
    SessionID sessionIdObj = null;
    String pUserId = "";
    boolean personalization = false;
    int pagesize=0;
    String recentXmlQueryString=null;
    String navigator=null;
    String location=null;
    String searchID=null;
    boolean flag=false;
    boolean astem = false;
    int index=0;
    int nTotalDocs=0;

    String term1 = "";
    String dbName="";
    String sortBy="";
    String sortDir="";
    String yearSelect="";
    String bYear = "";
    String eYear = "";
    String Expand="false";
    boolean updateCurrentQuery=true;
    String searchType=null;
    ClientCustomizer clientCustomizer=null;
    boolean isPersonalizationPresent=true;
    boolean isFullTextPresent=true;
    boolean isGraphDownloadPresent=false;
    boolean isCitLocalHoldingsPresent=false;
    boolean isEmailAlertsPresent=true;
    boolean isRssLinkPresent=false;
    int customizedStartYear=1970;
    String customizedLogo="";
    String intialSearchReqParam= null;

    String rerun="";

    int jumpIndex = -1;
    int cacheAheadNumber = 0;
    String format = StringUtil.EMPTY_STRING;

    try
    {
        pagesize=Integer.parseInt(pageSize.trim());
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }

    UserSession ussession=(UserSession)client.getUserSession();

    sessionId = ussession.getID();
    sessionIdObj = ussession.getSessionID();
    pUserId = ussession.getUserIDFromSession();

    if((pUserId != null) && (pUserId.trim().length() != 0))
    {
        personalization=true;
    }

     IEVWebUser user = ussession.getUser();
    String[] credentials = user.getCartridge();

    String customerId=user.getCustomerID().trim();

    long bTime = System.currentTimeMillis();
    clientCustomizer=new ClientCustomizer(ussession);
    isRssLinkPresent=clientCustomizer.checkRssLink();
    isGraphDownloadPresent=clientCustomizer.checkGraphDownload();
    isFullTextPresent=clientCustomizer.checkFullText("citationResults");
    isCitLocalHoldingsPresent=clientCustomizer.checkLocalHolding("citationResults");

    isPersonalizationPresent=clientCustomizer.checkPersonalization();
    isEmailAlertsPresent=clientCustomizer.checkEmailAlert();
    customizedLogo=clientCustomizer.getLogo();

    if (null != request.getParameter("pageSizeVal"))
    {
    	pagesize = Integer.parseInt(request.getParameter("pageSizeVal"));
    }else{
    	 if(null != ussession.getRecordsPerPage()){
    	   pagesize = Integer.parseInt(ussession.getRecordsPerPage());
    	 }
    }

    if(request.getParameter("database")!=null)
    {
        dbName = request.getParameter("database");
        if(dbName.equals("3072") || dbName.equals("2048") || dbName.equals("1024"))
        {
        	boolean hasEPT = UserCredentials.hasCredentials(2048, databaseConfig.getMask(user.getCartridge()));
            boolean hasELT = UserCredentials.hasCredentials(1024, databaseConfig.getMask(user.getCartridge()));
            if(hasEPT && hasELT)
            {
            	dbName = "3072";
            }
            else if(hasEPT && !hasELT)
            {
            	dbName = "2048";
            }
            else if(!hasEPT && hasELT)
            {
            	dbName = "1024";
            }
            else if(!hasEPT && !hasELT)
            {
            	throw new SecurityException("<DISPLAY>You do not have access to the databases requested</DISPLAY>");
            }
        }



    }

    //System.out.println("dbName==> "+dbName);
    navigator =(String)request.getParameter("navigator");

    location =(String)request.getParameter("location");

    currentPage=request.getParameter("COUNT");
    if(currentPage != null && currentPage.trim().equals(""))
    {
        currentPage = null;
    }

    //TMH - UI refresh, get the "PAGE" parameter from the request
    //when the COUNT param is empty.  Indicates page navigation
    if(currentPage == null)
    {
        currentPage = request.getParameter("PAGE");
        if (currentPage != null && currentPage.trim().equals("")) {
        	currentPage = null;
        } else if (currentPage != null) {
        	// convert to index
        	currentPage = Integer.toString(Integer.parseInt(currentPage) * pagesize);
        }
    }

    if (request.getParameter("intialSearch") != null )
    {
        intialSearchReqParam = request.getParameter("intialSearch").trim();
    }

    if(request.getParameter("SESSIONHISTORY")!=null)
    {
        updateCurrentQuery = false;
    }

    if(request.getParameter("SEARCHTYPE")!=null)
    {
        searchType="COMBINED-REFINE-SEARCH";
    }
    else
    {
        searchType="SEARCH";
    }

    if(request.getParameter("Expand")!=null)
    {
        Expand=request.getParameter("Expand");
    }

    int intDbMask = Integer.parseInt(dbName);


    boolean initialSearch = false;
    boolean clearBasket = false;

    DocumentBasket documentBasket = new DocumentBasket(sessionId);

    try
    {
    /**
    * Checking if the currentPage is null,that means its not been navigating.then its constructs the query
    * if currentPage is not null means ,that it been navigating
    * The control enters this if loop only when the searchresultspage.jsp is called for the first time
    */
    sortBy = request.getParameter("sort");
    sortDir = request.getParameter("sortdir");
    searchID = request.getParameter("SEARCHID");


    if((searchID == null) || searchID.equals(StringUtil.EMPTY_STRING))
    {
        currentPage = "1";
        initialSearch = true;

        rerun = request.getParameter("RERUN");
        if((rerun == null) || rerun.equals(StringUtil.EMPTY_STRING))
        {
            queryObject = new Query(databaseConfig, credentials);

            searchID = new GUID().toString();
            queryObject.setID(searchID);

            queryObject.setSearchType(Query.TYPE_THESAURUS);

            queryObject.setDataBase(intDbMask);

            if(documentBasket.getClearOnNewSearch())
            {
            if(documentBasket.getClearOnNewSearch())
            {
                clearBasket = true;
                sc.checkBasket(false);
            }
                }

                if(request.getParameter("searchWord1")!=null)
                {
                    term1=request.getParameter("searchWord1").trim();
                }

                queryObject.setSearchPhrase(term1,"","","","","","","");

                String limitDocument = request.getParameter("doctype");
                queryObject.setDocumentType(limitDocument);

                String limitTreatment = request.getParameter("treatmentType");
                queryObject.setTreatmentType(limitTreatment);

                String limitDiscipline = request.getParameter("disciplinetype");
                queryObject.setDisciplineType(limitDiscipline);

                String limitLanguage = request.getParameter("language");
                queryObject.setLanguage(limitLanguage);

                yearSelect=request.getParameter("yearselect");

                // common year handling code
                // was repeated inside two clauses of
                // same if/else statement
                bYear = request.getParameter("startYear");
                eYear = request.getParameter("endYear");

                // get the years based on the DB mask from the link
                // for Search Results, Folders and Selected Sets, the XSL will correctly
                // set the "database=" request param so we need not worry where it came from
                // we will use intDbMask & user credentials
                if ((bYear == null) || (eYear == null))
                {
                  Map startEndYears = databaseConfig.getStartEndYears(credentials, intDbMask);
                  bYear = (bYear == null) ? (String) startEndYears.get(DatabaseConfig.STARTYEAR) : bYear;
                  eYear = (eYear == null) ? (String) startEndYears.get(DatabaseConfig.ENDYEAR) : eYear;
                }
                queryObject.setStartYear(bYear);
                queryObject.setEndYear(eYear);

                if(!yearSelect.equals("yearrange"))
                {
                    update = request.getParameter("updatesNo");
                    queryObject.setLastUpdatesCount(update);
                }

                queryObject.setAutoStemming("off");
            }  // not a RERUN query
            else
            { // RERUN from SubCount Links or Sort By
               // log(" RERUN " + rerun);

                queryObject = Searches.getSearch(rerun);
                queryObject.setDatabaseConfig(databaseConfig);
                queryObject.setCredentials(credentials);

                searchID = new GUID().toString();
                queryObject.setID(searchID);
                queryObject.setDataBase(intDbMask);
                queryObject.clearDupSet();
                queryObject.setDeDup(false);

            }

            queryObject.setSortOption(new Sort(sortBy,sortDir));

            queryObject.setSearchQueryWriter(new FastQueryWriter());
            queryObject.compile();

            // NOTE: changes to the refinements object will be reflected in queryObject (This is by (poor?) design)
            Refinements refinements = queryObject.getRefinements();
            // add thesaurus query as single term to show as breadcrumb
            if(refinements.size() == 0)
            {
                refinements.addInitialRefinementStep(queryObject.getIntermediateQuery(), queryObject.getDisplayQuery());
            }
            //System.out.println("FAST Query:"+queryObject.getSearchQuery()+" **  EI Query: "+queryObject.getPhysicalQuery());
            client.addComment("FAST Query:"+queryObject.getSearchQuery());
            client.addComment("EI Query"+queryObject.getPhysicalQuery());
        	HitHighlighter highlighter = new HitHighlighter(queryObject.getParseTree());
        	sc.setHitHighlighter(highlighter);
            result = sc.openSearch(queryObject,
                sessionId,
                pagesize,
                false);

            queryObject.setRecordCount(Integer.toString(result.getHitCount()));

            if(personalization == true)
            {
                queryObject.setUserID(pUserId);
            }
            queryObject.setSessionID(sessionId);

            // save query moved from here to after out.flush call
        }
        else
        {
            // log(" HISTORY " + searchID);
            updateCurrentQuery = false;
            initialSearch = false;

            if(null != intialSearchReqParam){
                initialSearch = true;
                updateCurrentQuery = true;

                if(documentBasket.getClearOnNewSearch()){
                        clearBasket = true;
                        sc.checkBasket(false);
                }
            }

            searchID=request.getParameter("SEARCHID");

            if((location != null) && ("ALERT".equalsIgnoreCase(location) || "SAVEDSEARCH".equalsIgnoreCase(location)))
            {
                // get Query from SAVED searches
                queryObject = SavedSearches.getSearch(searchID);
                if (queryObject == null) {
                    throw new SearchException(SystemErrorCodes.SAVED_SEARCH_NOT_FOUND, "Unable to find saved search / alert by ID: '" + searchID + "'");
                }

                searchID = new GUID().toString();
                // reset
                queryObject.setID(searchID);
                queryObject.setVisible(Query.ON);
                queryObject.setSavedSearch(Query.OFF);
                queryObject.setEmailAlert(Query.OFF);
                queryObject.setSessionID(sessionIdObj.getID());
                updateCurrentQuery = true;
                // set as initial search - default is false
                initialSearch = true;

                if("ALERT".equalsIgnoreCase(location))
                {
                    String emailweek = (String) request.getParameter("emailweek");
                    queryObject.setEmailAlertWeek(emailweek);
                }

                if(request.getParameter("DOCINDEX") != null)
                {
                    jumpIndex = Integer.parseInt(request.getParameter("DOCINDEX"));
                    format = request.getParameter("format");
                    // no need to cache ahead - single document view
                    cacheAheadNumber = 0;
                }
            }
            else
            {
                // get Query from current session history
                queryObject = Searches.getSearch(searchID);
            }
            if (queryObject == null) {
                throw new SearchException(SystemErrorCodes.SEARCH_NOT_FOUND, "Unable to find search by ID: '" + searchID + "'");
            }

            queryObject.setSearchQueryWriter(new FastQueryWriter());
            queryObject.setDatabaseConfig(databaseConfig);
            queryObject.setCredentials(credentials);

            // add thes query as single term to show as breadcrumb
            Refinements refinements = queryObject.getRefinements();
            if(refinements.size() == 0)
            {
                refinements.addInitialRefinementStep(queryObject.getIntermediateQuery(), queryObject.getDisplayQuery());
            }

            if((navigator != null) && ("MORE".equalsIgnoreCase(navigator)))
            {
                 ResultsState rs = queryObject.getResultsState();
                 rs.modifyState(request.getParameter("FIELD"));
                 Searches.updateSearchRefineState(queryObject);
            }

            long beginQuery = System.currentTimeMillis();
        	HitHighlighter highlighter = new HitHighlighter(queryObject.getParseTree());
        	sc.setHitHighlighter(highlighter);
            result = sc.openSearch(queryObject,
                                   sessionId,
                                   pagesize,
                                   !initialSearch);

            if(initialSearch)
            {
                queryObject.setRecordCount(Integer.toString(result.getHitCount()));
                client.addComment("Fast Query: "+queryObject.getSearchQuery());
                client.addComment("EI Query: "+queryObject.getPhysicalQuery());
                long searchTime = result.getResponseTime();
                long endQuery = System.currentTimeMillis();
                long totalQuery = endQuery - beginQuery;
                client.addComment("Fast Search Response Time: "+Long.toString(searchTime));
                client.addComment("Fast Network Time: "+Long.toString(totalQuery-searchTime));
                client.addComment("Fast Total Response Time: "+Long.toString(totalQuery));
            }
        }


        recentXmlQueryString = queryObject.toXMLString();

        if(personalization == true)
        {
            queryObject.setUserID(pUserId);
        }
        queryObject.setSessionID(sessionId);

        /*
        *  Gets the results for the current page
        */

        nTotalDocs = Integer.parseInt(queryObject.getRecordCount());

        String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

        // creating Thesaurus XML for output
        BooleanQuery bq = queryObject.getParseTree();
        String strThesTerm = "";
        StringBuffer strBufThesXML = new StringBuffer();
        ExactTermGatherer etg = new ExactTermGatherer();

        List lstThesTerms = etg.gatherExactTerms(bq);
        String firstCon = etg.getFirstConnector();

        Iterator itrThesTerms = lstThesTerms.iterator();

        strBufThesXML.append("<CVS CONNECTOR='"+firstCon+"'>");

        while(itrThesTerms.hasNext())
        {
            strThesTerm = (String) itrThesTerms.next();

            strBufThesXML.append("<CV>");
            strBufThesXML.append(strThesTerm);
            strBufThesXML.append("</CV>");
        }
        strBufThesXML.append("</CVS>");

        if(nTotalDocs!=0)
        {
            if(currentPage == null)
            {
                currentPage = "1";
            }
            index=Integer.parseInt(currentPage);

            oPage = result.pageAt(index, Citation.CITATION_FORMAT);

            // Search boolean
            String log_action = "document";
            if (navigator == null)
            {
                log_action = "search";
            }

            /**
            *   Log Functionality
            */

            client.log("search_id", searchID);
            client.log("query_string", queryObject.getPhysicalQuery());
            client.log("sort_by", queryObject.getSortOption().getSortField());
            client.log("sort_direction", queryObject.getSortOption().getSortDirection());
            client.log("suppress_stem", queryObject.getAutoStemming());
            client.log("context", "thesaurus");
            client.log("action", log_action);
            client.log("type", "expert");
            client.log("db_name", dbName);
            client.log("page_size", pageSize);
            client.log("format", "CIT");
            client.log("doc_id", " ");
            if(nTotalDocs>0)
            {
                client.log("num_recs", Integer.toString(oPage.docCount()));
                client.log("doc_index", Integer.toString(index));
                client.log("hits", Integer.toString(nTotalDocs));
            }
            else
            {
                client.log("num_recs", "0");
                client.log("doc_index", "0");
                client.log("hits", "0");
            }
            client.setRemoteControl();

            //Writing out XML
            if(jumpIndex == -1)
            {
                out.write("<PAGE>");

                StringBuffer backurl = new StringBuffer();
                backurl.append("CID=thesSearchCitationFormat").append("&");
                backurl.append("SEARCHID=").append(searchID).append("&");
                backurl.append("COUNT=").append(index).append("&");
                backurl.append("database=").append(dbName);
		String encodedBackURL = URLEncoder.encode(backurl.toString());
		String newSearchURL = URLEncoder.encode(getnsURL(dbName));
		out.write("<BACKURL>");
		out.write(encodedBackURL);
		out.write("</BACKURL>");
		out.write("<PAGE-NAV>");
		out.write("<RESULTS-NAV>");
		out.write(encodedBackURL);
		out.write("</RESULTS-NAV>");
		out.write("<NEWSEARCH-NAV>");
		out.write(newSearchURL);
		out.write("</NEWSEARCH-NAV>");
		out.write("</PAGE-NAV>");


                out.write("<HEADER/>");
                out.write("<SEARCH/>");
                out.write("<DBMASK>");
                out.write(dbName);
                out.write("</DBMASK>");
                out.write(strGlobalLinksXML);
                out.write("<NAVIGATION-BAR/>");
                out.write("<SEARCH-TYPE>"+queryObject.getSearchType()+"</SEARCH-TYPE>");
                out.write("<TOP-RESULTS-MANAGER/>");
                out.write("<BOTTOM-RESULTS-MANAGER/>");
                out.write("<DEDUP>false</DEDUP>");
                out.write("<CLEAR-ON-VALUE>"+documentBasket.getClearOnNewSearch()+"</CLEAR-ON-VALUE>");
                out.write("<FOOTER/>");
                out.write("<SESSION-TABLE/>");
                out.write("<CUSTOMIZED-STARTYEAR>"+customizedStartYear+"</CUSTOMIZED-STARTYEAR>");
                out.write("<CUSTOMIZED-ENDYEAR>"+customizedEndYear+"</CUSTOMIZED-ENDYEAR>");
                out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
                out.write("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>");
                out.write("<RSSLINK>"+isRssLinkPresent+"</RSSLINK>");
                out.write("<NAVCHRT>"+isGraphDownloadPresent+"</NAVCHRT>");
                out.write("<LOCALHOLDINGS-CITATION>"+ isCitLocalHoldingsPresent+"</LOCALHOLDINGS-CITATION>");
                out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
                out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
                out.write("<RESULTS-PER-PAGE>"+pagesize+"</RESULTS-PER-PAGE>");
                out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
                out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
                out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
                out.write("<PERSON-USER-ID>"+pUserId+"</PERSON-USER-ID>");
                out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
                out.write("<RESULTS-COUNT>"+nTotalDocs+"</RESULTS-COUNT>");
                out.write("<PREV-PAGE-ID>"+(index-pagesize)+"</PREV-PAGE-ID>");
                out.write("<CURR-PAGE-ID>"+index+"</CURR-PAGE-ID>");
                out.write("<NEXT-PAGE-ID>"+(index+pagesize)+"</NEXT-PAGE-ID>");

                // jam - From Joel on 12/9/2004 only get and set Local holdings
                // if isCitLocalHoldingsPresent!
                // This lessens size of outgoing page XML
                if(isCitLocalHoldingsPresent)
                {
                    localHolding = new LocalHolding(ussession,2);
                    oPage.setlocalHolding(localHolding);
                }
                oPage.toXML(out);
%>
                <%@ include file="database.jsp"%>
                <%@ include file="queryForm.jsp"%>
                <%
                	out.write(strBufThesXML.toString());

                                out.write(recentXmlQueryString);

                                databaseConfig.sortableToXML(credentials, out);

                                // jam taken from Mexico build revision 1.3 for Faceted Browsing / Navigators
                                if((sc != null))
                                {
                                    ResultNavigator nav = sc.getNavigator();
                                    out.write(nav.toXML(queryObject.getResultsState()));
                                }

                                out.write("</PAGE>");
                                out.println("<!--END-->");
                                out.flush();

                            }

                            if(updateCurrentQuery)
                            {
                                //log(" saveQuery: updateCurrentQuery == true ");
                                //Searches.saveSearch(queryObject);
                                Searches.updateExistingSearch(queryObject);
                            }

                            if(initialSearch)
                            {
                                if(clearBasket)
                        {
                            documentBasket.removeAll();
                                }

                                sc.maintainNavigatorCache();
                                sc.maintainCache(index,cacheAheadNumber);
                                if(jumpIndex > -1)
                                {
                                    client.setRedirectURL("/controller/servlet/Controller?CID="+format+"&SEARCHID="+queryObject.getID()+"&DOCINDEX="+jumpIndex+"&PAGEINDEX=1&database="+dbName+"&format="+format+"&pageType="+queryObject.getSearchType()+"Search");
                                    client.setRemoteControl();
                                    out.println("<!--END-->");
                                }
                            }
                            else
                            {
                                sc.maintainCache(index,0);
                            }
                        }
                        else
                        {  // zero results


                            if(updateCurrentQuery)
                            {
                               // log(" saveQuery: zero results ");
                                Searches.updateExistingSearch(queryObject);
                            }

                            if( (request.getParameter("FORWARD")!=null) &&(request.getParameter("FORWARD").equals("true")) )
                            {
                                // Logging
                                client.log("SEARCH_ID", searchID);
                                client.log("QUERY_STRING", queryObject.getPhysicalQuery());
                                client.log("sort_by", queryObject.getSortOption().getSortField());
                                client.log("sort_direction", queryObject.getSortOption().getSortDirection());

                                client.log("ACTION", "search");
                                client.log("TYPE", "expert");
                                client.log("context", "thesaurus");

                                client.log("DOC_ID", " ");
                                client.log("FORMAT", "CIT");
                                client.log("DB_NAME", dbName);
                                client.log("PAGE_SIZE", pageSize);
                                client.log("HIT_COUNT", "0");
                                client.log("DOC_INDEX", "0");
                                client.log("NUM_RECS", "0");
                                client.setRemoteControl();

                                out.write("<PAGE>");
                                out.write("<HEADER/>");
                                out.write("<SEARCH/>");
                                out.write("<DBMASK>");
                                out.write(dbName);
                                out.write("</DBMASK>");
                                out.write(strGlobalLinksXML);
                                out.write("<NAVIGATION-BAR/>");
                                out.write("<RESULTS-MANAGER/>");
                                out.write("<CONTEXT>S</CONTEXT>");
                                out.write("<FOOTER/>");
                                out.write("<SESSION-TABLE/>");
                                out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
                                out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
                                out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
                                out.write("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>");
                                out.write("<RSSLINK>"+isRssLinkPresent+"</RSSLINK>");
                                out.write("<NAVCHRT>"+isGraphDownloadPresent+"</NAVCHRT>");
                                out.write("<LOCALHOLDINGS-CITATION>"+ isCitLocalHoldingsPresent+"</LOCALHOLDINGS-CITATION>");
                                out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
                                out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
                                out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
                                out.write("<PERSON-USER-ID>"+pUserId+"</PERSON-USER-ID>");
                                out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
                                out.write("<RESULTS-COUNT>0</RESULTS-COUNT>");
                                out.write(strBufThesXML.toString());
                                out.write(recentXmlQueryString);
                %>
                <%@ include file="database.jsp"%>
                <%@ include file="queryForm.jsp"%>
                <%
                	out.write("</PAGE>");
                                out.println("<!--END-->");
                            }
                            else
                            {
                                flag=true;
                            }
                        }
                        //Forward the page when no of results are zero.
                        if(flag==true)
                        {
                            StringBuffer urlString = new StringBuffer("/controller/servlet/Controller?CID=thesHome");
                            urlString.append("&searchid="+searchID+"&message=zero");
                            if (!GenericValidator.isBlankOrNull(dbName)) urlString.append("&database="+URLEncoder.encode(dbName,"UTF-8"));
                            if (!GenericValidator.isBlankOrNull(request.getParameter("thesterm"))) urlString.append("&term="+request.getParameter("thesterm"));
                            urlString.append("#init");
                            client.setRedirectURL(urlString.toString());
                            client.setRemoteControl();
                            return;
                        }
                    }catch (Exception e) {
                        EVBaseException be = null;
                        if (e instanceof EVBaseException) {
                            be = (EVBaseException) e;
                        } else {
                            be = new SearchException(SystemErrorCodes.UNKNOWN_SEARCH_ERROR, e);
                        }
                        client.setException("true");
                        client.setRemoteControl();
                        out.write(be.toXML());
                        out.flush();

                        return;
                    }
                    finally
                    {
                        if(sc != null)
                        {
                            sc.closeSearch();
                        }
                    }
                %><%!/**
    *   This jsp init method is called only once when request comes for the first time.
    *   At that it should read the size documents that to be displayed per page.
    *   This size is read from the Eiproperties file.
    */
    String pageSize=null;

    DatabaseConfig databaseConfig = null;
    int customizedEndYear=0;

    public void jspInit()
    {
        try
        {
            RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
            pageSize = eiProps.getProperty("DISPLAYPAGESIZE");
            databaseConfig = DatabaseConfig.getInstance();

            // jam Y2K3
            customizedEndYear = Integer.parseInt(eiProps.getProperty("SYSTEM_ENDYEAR"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log("Errorrr:"+e.getMessage());
        }
    }

	private String getnsURL(String database)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("CID=thesSearch&database=");
		buf.append(database);
		return buf.toString();
	}%>