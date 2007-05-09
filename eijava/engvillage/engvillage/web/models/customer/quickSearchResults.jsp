<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>

<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.books.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.query.limiter.*"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.util.StringUtil"%>
<%
    String currentPage=null;

    SearchResult result=null;
    Query queryObject = null;

    Page oPage=null;
    ControllerClient client = new ControllerClient(request, response);
    LocalHolding localHolding = null;

    String sessionId = null;
    SessionID sessionIdObj = null;
    String pUserId = "";
    boolean personalization = false;
    String navigator=null;

    String location=null;
    String searchID=null;

    boolean flag=false;
    boolean astem = false;
    int index=0;
    int nTotalDocs=0;
    int jumpIndex = -1;
    int cacheAheadNumber = 2;
    String format = StringUtil.EMPTY_STRING;

    String term1 = "";
    String term2 = "";
    String term3 = "";
    String field1 = "";
    String field2 = "";
    String field3 = "";
    String dbName="";

    String limitDocument="";
    String limitTreatment="";
    String limitLanguage="";
    String yearSelect="";
    String bYear = "";
    String eYear = "";
    String update = "";

    String bool1=null;
    String bool2=null;
    String autoStem=null;

    String limitDiscipline=null;
    String deDupFlag = null;
    String dbToDeDup = null;
    boolean deDupable = false;

    String Expand="false";
    boolean updateCurrentQuery=true;
    String DBCITATIONFORMAT="";

    ClientCustomizer clientCustomizer=null;
    boolean isPersonalizationPresent=true;
    boolean isFullTextPresent=true;
    boolean isGraphDownloadPresent=false;
    boolean isEmailAlertsPresent=true;
    boolean isCitLocalHoldingsPresent=false;
    boolean isRssLinkPresent=false;
    String defaultdbmask = null;
    String customizedStartYear="1969";
    String customizedLogo="";

    // jam - added for 'factoring' dbs from combined searches
    // when rerun =
    String rerun="";

    int pagesize=0;
    int dedupsetsize=0;
    int dedupsetsize1=0;
    SearchControl sc = new FastSearchControl();

    pagesize=Integer.parseInt(pageSize.trim());
    dedupsetsize=Integer.parseInt(dedupSetSize.trim());
    dedupsetsize1=dedupsetsize + 1;
    UserSession ussession=(UserSession)client.getUserSession();
    sessionId=ussession.getID();
    sessionIdObj = ussession.getSessionID();
    pUserId = ussession.getProperty("P_USER_ID");
    if((pUserId != null) && (pUserId.trim().length() != 0))
    {
        personalization=true;
    }

    User user = ussession.getUser();
    String customerId=user.getCustomerID().trim();

    clientCustomizer=new ClientCustomizer(ussession);
    // moved localHolding object creation to if statement in outgoing XML section
    isFullTextPresent=clientCustomizer.checkFullText("citationResults");
    //isRssLinkPresent=clientCustomizer.checkRssLink("true");
    isRssLinkPresent=clientCustomizer.checkRssLink();
    isGraphDownloadPresent=clientCustomizer.checkGraphDownload();
    isCitLocalHoldingsPresent=clientCustomizer.checkLocalHolding("citationResults");

    isPersonalizationPresent=clientCustomizer.checkPersonalization();
    isEmailAlertsPresent=clientCustomizer.checkEmailAlert();
    customizedLogo=clientCustomizer.getLogo();
    customizedStartYear = clientCustomizer.getSYear();
    if (request.getParameter("defaultdbmask") != null )
    {
        defaultdbmask = request.getParameter("defaultdbmask").trim();
    }
    if (request.getParameter("alldb") != null )
    {
        dbName = request.getParameter("alldb").trim();
    }
    else
    {
        int sumDb = 0;
        String[] dbs = request.getParameterValues("database");
        if(dbs != null)
        {
            for (int i=0; i<dbs.length; i++)
            {
                sumDb += Integer.parseInt(dbs[i]);
            }
            dbName = String.valueOf(sumDb);
        }
    }
    // jam - taken from Mexico build - revision 1.2
    navigator = (String) request.getParameter("navigator");

    location = (String) request.getParameter("location");

    //Getting the COUNT parameter from the request
    currentPage = request.getParameter("COUNT");
    if(currentPage != null && currentPage.trim().equals(""))
    {
        currentPage = null;
    }

    int intDbMask = Integer.parseInt(dbName);
    String[] credentials = user.getCartridge();

    if(!UserCredentials.hasCredentials(intDbMask, databaseConfig.getMask(credentials)))
    {
        throw new SecurityException("<DISPLAY>You do not have access to the databases requested</DISPLAY>");
    }

    boolean initialSearch = false;
    boolean clearBasket = false;

    DocumentBasket documentBasket = new DocumentBasket(sessionId);

    try
    {
        String sortBy = request.getParameter("sort");
        String sortDir = request.getParameter("sortdir");
        searchID = request.getParameter("SEARCHID");

        // jam added for ebookSearch
        String searchtype = request.getParameter("searchtype");

        if((searchID == null) || searchID.equals(StringUtil.EMPTY_STRING))
        {
            currentPage = "1";
            initialSearch = true;

            rerun = request.getParameter("RERUN");
            if((rerun == null) || rerun.equals(StringUtil.EMPTY_STRING))
            {

            		if(documentBasket.getClearOnNewSearch())
            		{
            			clearBasket = true;
            			sc.checkBasket(false);
            		}

                queryObject = new Query(databaseConfig, credentials);
                queryObject.setDataBase(intDbMask);

                searchID = (new GUID()).toString();
                queryObject.setID(searchID);

                if((searchtype != null) && searchtype.equals(Query.TYPE_BOOK))
                {
                  queryObject.setSearchType(Query.TYPE_BOOK);

                  if(request.getParameterValues("allcol") == null)
                  {
                    if(request.getParameterValues("col") != null)
                    {
                      queryObject.setReferexCollections(new ReferexLimiter(request.getParameterValues("col")));
                    }
                  }
                }
                else
                {
                  queryObject.setSearchType(Query.TYPE_QUICK);
                }

                term1=request.getParameter("searchWord1");

                field1=request.getParameter("section1");

                term2=request.getParameter("searchWord2");

                field2=request.getParameter("section2");

                term3=request.getParameter("searchWord3");

                field3=request.getParameter("section3");

                bool1 = request.getParameter("boolean1");

                bool2 = request.getParameter("boolean2");

                queryObject.setSearchPhrase(term1,field1,bool1,term2,field2,bool2,term3,field3);

                limitDocument = request.getParameter("doctype");
                queryObject.setDocumentType(limitDocument);

                limitTreatment = request.getParameter("treatmentType");
                queryObject.setTreatmentType(limitTreatment);

                limitDiscipline = request.getParameter("disciplinetype");
                queryObject.setDisciplineType(limitDiscipline);

                limitLanguage = request.getParameter("language");
                queryObject.setLanguage(limitLanguage);

                yearSelect = request.getParameter("yearselect");

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
                    /* NO need to repeat same code from above
                    bYear = request.getParameter("startYear");
                    eYear = request.getParameter("endYear");
                    queryObject.setStartYear(bYear);
                    queryObject.setEndYear(eYear);
                    */
                    update = request.getParameter("updatesNo");
                    queryObject.setLastUpdatesCount(update);
                }

                // Flipped request logic!! - no more hidden variables
                // if request parameter is 'on' - the autostemming
                // off checkbox was checked!
                autoStem = request.getParameter("autostem");

                if("on".equalsIgnoreCase(autoStem))
                {
                    queryObject.setAutoStemming("off");
                }
                else
                {
                    queryObject.setAutoStemming("on");
                }

            }  // not a RERUN query
            else
            { // RERUN from SubCount Links or Sort By

                //log(" RERUN " + rerun);

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

            // add quicksearch query as single term to show as breadcrumb
            if(refinements.size() == 0)
            {
                // check to see if this is a pcinav query from a cited by link
                // and use that navigator data create the initial refinement
                if(request.getParameter("pcinav") != null)
                {
                  EiNavigator easynav = EiNavigator.createNavigator(EiNavigator.PCI);
                  easynav.setModifierValue(request.getParameter("pcinav"));
                  refinements.addRefinement(easynav);
                }
                else
                {
                  refinements.addInitialRefinementStep(queryObject.getIntermediateQuery(), queryObject.getDisplayQuery());
                }
            }

            client.addComment("Fast Query:"+queryObject.getSearchQuery());
            client.addComment("EI Query"+queryObject.getPhysicalQuery());

            long beginQuery = System.currentTimeMillis();
            result = sc.openSearch(queryObject,
                   sessionId,
                   pagesize,
                   false);

            long searchTime = result.getResponseTime();
            long endQuery = System.currentTimeMillis();
            long totalQuery = endQuery - beginQuery;
            client.addComment("Fast Search Response Time:"+Long.toString(searchTime));
            client.addComment("Fast Network Time:"+Long.toString(totalQuery-searchTime));
            client.addComment("Fast Total Response Time:"+Long.toString(totalQuery));

            queryObject.setRecordCount(Integer.toString(result.getHitCount()));

            // save query moved from here to after out.flush call

        }   // new query
        else
        {   // SearchID is not null
            // run from history - Saved Search, Email Alert, Session History
            //log(" HISTORY " + searchID);
            // set these to false - email alert Alert or run from Saved Searches will set as needed
            updateCurrentQuery = false;
            initialSearch = false;

            searchID = request.getParameter("SEARCHID");

            // retreive query object and set properties
            if((location != null) && ("ALERT".equalsIgnoreCase(location) || "SAVEDSEARCH".equalsIgnoreCase(location)))
            {
                // get Query from SAVED searches
                queryObject = SavedSearches.getSearch(searchID);

                // create and store new searchid so we show in Session History
                searchID = new GUID().toString();
                queryObject.setID(searchID);
                queryObject.setVisible(Query.ON);
                queryObject.setSavedSearch(Query.OFF);
                queryObject.setEmailAlert(Query.OFF);

                // set search to show up in current session history
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
                queryObject = Searches.getSearch(searchID);
            }

            queryObject.setSearchQueryWriter(new FastQueryWriter());
            queryObject.setDatabaseConfig(databaseConfig);
            queryObject.setCredentials(credentials);


            // add quicksearch query as single term to show as breadcrumb
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

            result = sc.openSearch(queryObject,
                                    sessionId,
                                    pagesize,
                                    !initialSearch);

            if(initialSearch)
            {
                queryObject.setRecordCount(Integer.toString(result.getHitCount()));
            }
        }

        if(personalization == true)
        {
            queryObject.setUserID(pUserId);
        }
        queryObject.setSessionID(sessionId);

        nTotalDocs = Integer.parseInt(queryObject.getRecordCount());

        //getting request params for deDupping
        deDupFlag = (String) request.getParameter("DupFlag");
        if (deDupFlag != null)
        {
            queryObject.setDeDup(deDupFlag);
            dbToDeDup = (String)request.getParameter("DupDB");
            if (dbToDeDup != null)
            {
                queryObject.setDeDupDB(dbToDeDup);
            }
            // we have to update here since this will
            // not be done when initialsearch=true
            // or updateCurrentQuery=true
            Searches.updateSearchDeDup(queryObject);
        }

        String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

        /**
          * Checking for the total results is greater than zero.
          * then out put the xml with results.
          */
        if(nTotalDocs>0)
        {
            if(currentPage == null)
            {
                currentPage = "1";
            }
            index = Integer.parseInt(currentPage.trim());

            oPage = result.pageAt(index,
            			  Citation.CITATION_FORMAT);
            String log_action = "document";
            if (initialSearch)
            {
                log_action = "search";
            }

            /**
            * Log Functionality
            **/
            client.log("search_id", queryObject.getID());
            client.log("query_string", queryObject.getPhysicalQuery());
            client.log("bookcreds", BookCredentials.toString(credentials));
            client.log("sort_by", queryObject.getSortOption().getSortField());
            client.log("sort_direction", queryObject.getSortOption().getSortDirection());
            client.log("suppress_stem", queryObject.getAutoStemming());
            client.log("context", "search");
            client.log("action", log_action);
            client.log("type", queryObject.getSearchType());
            client.log("db_name", dbName);
            client.log("page_size", pageSize);
            client.log("format", "citation");
            client.log("num_recs", Integer.toString(oPage.docCount()));
            client.log("doc_index", Integer.toString(index));
            client.log("hits", Integer.toString(nTotalDocs));
            client.setRemoteControl();

            if(jumpIndex == -1)
            {
                //Writing out XML

                //FileWriter out1 = new FileWriter("C://LHfalse.xml");
                // isCitLocalHoldingsPresent = false;

                out.write("<PAGE>");

                StringBuffer backurl = new StringBuffer();
                backurl.append("CID=quickSearchCitationFormat").append("&");
                backurl.append("SEARCHID=").append(searchID).append("&");
                backurl.append("COUNT=").append(index).append("&");
                backurl.append("database=").append(dbName);
				String encodedBackURL = URLEncoder.encode(backurl.toString());
				String newSearchURL = URLEncoder.encode(getnsURL(dbName, queryObject.getSearchType()));
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
                out.write("<DBMASK>");
                out.write(dbName);
                out.write("</DBMASK>");
                out.write("<SEARCH-TYPE>"+queryObject.getSearchType()+"</SEARCH-TYPE>");

                out.write(strGlobalLinksXML);

                out.write("<FOOTER/>");
                out.write("<SESSION-TABLE/>");
                out.write("<SEARCH/>");
                out.write("<NAVIGATION-BAR/>");
                out.write("<TOP-RESULTS-MANAGER/>");
                out.write("<BOTTOM-RESULTS-MANAGER/>");
                if (queryObject.isDeDup())
                {
                    out.write("<DEDUP>true</DEDUP>");
                    out.write("<DUPDB>"+queryObject.getDeDupDB()+"</DUPDB>");
                    int dmask = Integer.parseInt(queryObject.getDeDupDB());
                    Database[] ddb = databaseConfig.getDatabases(dmask);
                    out.write("<DUPDBFULL>"+ddb[0].getName()+"</DUPDBFULL>");
                }
                else
                {
                    out.write("<DEDUP>false</DEDUP>");
                }

                out.write("<CLEAR-ON-VALUE>"+documentBasket.getClearOnNewSearch()+"</CLEAR-ON-VALUE>");
                out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
                out.write("<CUSTOMIZED-STARTYEAR>"+customizedStartYear+"</CUSTOMIZED-STARTYEAR>");
                out.write("<CUSTOMIZED-ENDYEAR>"+customizedEndYear+"</CUSTOMIZED-ENDYEAR>");
                out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
                out.write("<RESULTS-PER-PAGE>"+pagesize+"</RESULTS-PER-PAGE>");
                out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
                out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
                out.write("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>");
                out.write("<RSSLINK>"+isRssLinkPresent+"</RSSLINK>");
                out.write("<NAVCHRT>"+isGraphDownloadPresent+"</NAVCHRT>");
                out.write("<LOCALHOLDINGS-CITATION>"+ isCitLocalHoldingsPresent+"</LOCALHOLDINGS-CITATION>");
                out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
                out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
                out.write("<PERSON-USER-ID>"+pUserId+"</PERSON-USER-ID>");
                out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
                out.write("<RESULTS-COUNT>"+nTotalDocs+"</RESULTS-COUNT>");
                out.write("<PREV-PAGE-ID>"+(index-pagesize)+"</PREV-PAGE-ID>");
                out.write("<CURR-PAGE-ID>"+index+"</CURR-PAGE-ID>");
                out.write("<NEXT-PAGE-ID>"+(index+pagesize)+"</NEXT-PAGE-ID>");
                out.write("<DEFAULT-DB-MASK>"+defaultdbmask+"</DEFAULT-DB-MASK>");
                out.write("<DEDUPSETSIZE>" + dedupsetsize  + "</DEDUPSETSIZE>");

                queryObject.setDisplay(true);
                out.write(queryObject.toXMLString());

                // NO LONGER output databases based on credentials
                // this was used only for start and end years
                // links do not send in years anymore! Only forms
                // databaseConfig.toXML(credentials, out);

                // jam - From Joel on 12/9/2004 only get and set Local holdings
                // if isCitLocalHoldingsPresent!
                // This lessens size of outgoing page XML
                if(isCitLocalHoldingsPresent)
                {
                    localHolding = new LocalHolding(ussession,2);
                    oPage.setlocalHolding(localHolding);
                }
                oPage.toXML(out);
                //oPage.toXML(out1);

                // jam - temporary - maybe make this an attribute of
                // the fields in qeryForm? (<FIELD ... SORTABLE='true' MASK=...>)
                // jam - Sort XML output
                //
                databaseConfig.sortableToXML(credentials, out);

                // jam - taken from Mexico build - revision 1.2
                // Zhun 02/28/05
                // add back navigators

//              commented if((sc != null)) out - sc (FastSearchControl) is used
//              on w/o checking for null
//
                ResultNavigator nav = sc.getNavigator();
                out.write(nav.toXML(queryObject.getResultsState()));

                deDupable = nav.isDeDupable();
                out.write("<DEDUPABLE>" + deDupable + "</DEDUPABLE>");

                out.write("</PAGE>");
                out.println("<!--END-->");
                out.flush();

                //out1.close();
            }

            if(updateCurrentQuery)
            {
                //log(" saveQuery: updateCurrentQuery == true ");
                Searches.saveSearch(queryObject);
            }

            if(initialSearch)
            {
            	if(clearBasket)
            	{
            		documentBasket.removeAll();
            	}

                //log("initialSearch DEDUP " + queryObject.isDeDup());
                // jam - taken from Mexico build - revision 1.2
                sc.maintainNavigatorCache();

                sc.maintainCache(index,cacheAheadNumber);
                // forward from search to specific document index in email alert
                if(jumpIndex > -1)
                {
                    client.setRedirectURL("/controller/servlet/Controller?CID="+format+"&SEARCHID="+searchID+"&DOCINDEX="+jumpIndex+"&PAGEINDEX=1&database="+dbName+"&format="+format);
                    client.setRemoteControl();
                    out.println("<!--END-->");
                }
            }
            else
            {
                sc.maintainCache(index,0);
            }
        } // if(nTotalDocs>0)
        else
        { // zero results

            if(updateCurrentQuery)
            {
                //log(" saveQuery: zero results ");
                Searches.saveSearch(queryObject);
            }

            //if total results is zero then forward this page to xsl that displays zero results found for this search
            queryObject.setDisplay(true);
            if( (request.getParameter("FORWARD")!=null) &&(request.getParameter("FORWARD").equals("true")) )
            {
                String errorCode = "0";
                if(request.getParameter("errorCode") != null)
                {
                    errorCode = request.getParameter("errorCode");
                }

                client.log("search_id", queryObject.getID());
                client.log("query_string", queryObject.getPhysicalQuery());
                client.log("sort_by", queryObject.getSortOption().getSortField());
                client.log("sort_direction", queryObject.getSortOption().getSortDirection());
                client.log("suppress_stem", queryObject.getAutoStemming());
                client.log("context", "search");
                client.log("action", "search");
                client.log("type", queryObject.getSearchType());
                client.log("db_name", dbName);
                client.log("page_size", pageSize);
                client.log("format", "citation");
                client.log("doc_id", " ");
				client.log("num_recs", "0");
				client.log("doc_index", "0");
				client.log("hits", "0");

                client.setRemoteControl();
%>
                <!-- This xml is generated when there are no results for the given query  -->
                <PAGE>
                <HEADER/>
                <%= strGlobalLinksXML %>
                <FOOTER/>
                <DBMASK><%=dbName%></DBMASK>
                <SESSION-TABLE/>;
                <SEARCH/>
                <NAVIGATION-BAR/>
                <CUSTOMIZED-LOGO><%=customizedLogo%></CUSTOMIZED-LOGO>
                <CUSTOMER-ID><%=customerId%></CUSTOMER-ID>
                <CUSTOMIZED-STARTYEAR><%=customizedStartYear%></CUSTOMIZED-STARTYEAR>
                <SESSION-ID><%=sessionIdObj.toString()%></SESSION-ID>
                <PERSONALIZATION><%=personalization%></PERSONALIZATION>
                <PERSON-USER-ID><%=pUserId%></PERSON-USER-ID>
                <SEARCH-ID><%=searchID%></SEARCH-ID>
                <RESULTS-COUNT>0</RESULTS-COUNT>
                <ERROR-CODE><%=errorCode%></ERROR-CODE>
                <PERSONALIZATION-PRESENT><%=isPersonalizationPresent%></PERSONALIZATION-PRESENT>
                <FULLTEXT><%=isFullTextPresent%></FULLTEXT>
                <RSSLINK><%=isRssLinkPresent%></RSSLINK>
                <NAVCHRT><%=isGraphDownloadPresent%></NAVCHRT>
                <LOCALHOLDINGS-CITATION><%=isCitLocalHoldingsPresent%></LOCALHOLDINGS-CITATION>
                <EMAILALERTS-PRESENT><%=isEmailAlertsPresent%></EMAILALERTS-PRESENT>
                <DEFAULT-DB-MASK>><%=defaultdbmask%></DEFAULT-DB-MASK>
			          <CREDS><%= user.getCartridgeString() %></CREDS>

                <%=queryObject.toXMLString()%>
                <%@ include file="database.jsp"%>
                <%@ include file="queryForm.jsp"%>
                </PAGE>
                <!--END-->
<%
            }
            else
            {
                flag=true;
            }
        }

        /**
        * Forwarding the page when number of results is zero.
        */
        if(flag==true)
        {
            String urlString = "/controller/servlet/Controller?CID=errorQuickSearchResult&SEARCHID="+searchID+"&COUNT=1&FORWARD=true&database="+URLEncoder.encode(dbName)+"&Expand="+Expand;
            if(sc.hasError())
            {
                urlString = urlString.concat("&errorCode=").concat(sc.getErrorCode());
            }
            client.setRedirectURL(urlString);
            client.setRemoteControl();
            return;
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

    int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);
    String pageSize=null;
    String dedupSetSize=null;
    DatabaseConfig databaseConfig = null;

    public void jspInit()
    {
        try
        {
            RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
            pageSize = eiProps.getProperty("PAGESIZE");
            dedupSetSize = eiProps.getProperty("DEDUPSETSIZE");
            databaseConfig = DatabaseConfig.getInstance();

            // jam Y2K3
            customizedEndYear = Integer.parseInt(eiProps.getProperty("SYSTEM_ENDYEAR"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String getnsURL(String database,
    						String searchType)
    {
		StringBuffer buf = new StringBuffer();
		if(searchType.equals(Query.TYPE_BOOK))
		{
			buf.append("CID=ebookSearch");
		}
		else
		{
			buf.append("CID=quickSearch&database=");
			buf.append(database);
		}
		return buf.toString();
	}
%>