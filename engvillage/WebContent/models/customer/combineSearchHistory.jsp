<%@page import="org.ei.config.ApplicationProperties"%>
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@page import="org.ei.exception.SearchException"%>
<%@page import="org.ei.exception.InfrastructureException"%>
<%@page import="org.ei.exception.SystemErrorCodes"%>
<%@page import="org.ei.exception.EVBaseException"%>
<%@ page language="java"%>
<%@ page session="false"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="org.apache.oro.text.perl.Perl5Util"%>
<!--import statements of ei packages.-->
<%@ page import="org.ei.config.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.util.*"%>

<%
	String currentPage = null;
	SearchControl sc = new FastSearchControl();

	SearchResult result = null;
	Query queryObject = null;

	Page oPage = null;
	ControllerClient client = new ControllerClient(request, response);
	LocalHolding localHolding = null;

	String sessionId = null;
	String pUserId = "";
	boolean personalization = false;
	int pagesize = 0;

	String navigator = null;
	String location = null;
	String searchID = null;
	boolean flag = false;
	boolean astem = false;
	int index = 0;
	int nTotalDocs = 0;
	int jumpIndex = -1;
	String format = StringUtil.EMPTY_STRING;
	String dbName = null;

	String datab = null;
	if (request.getParameter("alldb") != null) {
		datab = request.getParameter("alldb").trim();
	} else {
		int sumDb = 0;
		String[] dbs = request.getParameterValues("database");
		if (dbs != null) {
			for (int i = 0; i < dbs.length; i++) {
				sumDb += Integer.parseInt(dbs[i]);
			}
			datab = String.valueOf(sumDb);
		}
	}

	String term1 = "";
	StringBuffer query = null;
	Database databaseObj = null;
	boolean updateCurrentQuery = true;
	String sortBy = null;
	String combineSearch = "";
	char[] combineCharArray = null;
	List databaseList = new ArrayList();

	ClientCustomizer clientCustomizer = null;
	boolean isPersonalizationPresent = true;
	boolean isFullTextPresent = true;
	boolean isGraphDownloadPresent = false;
	boolean isRssLinkPresent = false;
	boolean isCitLocalHoldingsPresent = false;
	boolean isEmailAlertsPresent = true;

	Perl5Util perl = new Perl5Util();

	String customizedLogo = "";

	// jam 12/30/2002
	// After making chage in 1.2 for expert and quick, needed to
	// set page cacheahead size to same number, 4 (was 7)
	// also added as a parameter
	int cacheAheadNumber = 0;

	String rerun = "";
	boolean initialSearch = false;
	boolean clearBasket = false;

	String deDupFlag = null;
	String dbToDeDup = null;
	boolean deDupable = false;
	String errorBackUrl = null;
	String defaultdb = null;

	int dedupsetsize = 0;
	int dedupsetsize1 = 0;
%>

<%!String dedupSetSize = null;

	int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);

	String pageSize = null;
	DatabaseConfig databaseConfig = null;

	public void jspInit() {
		try {
			ApplicationProperties eiProps = ApplicationProperties.getInstance();
			pageSize = eiProps.getProperty("DISPLAYPAGESIZE");
			databaseConfig = DatabaseConfig.getInstance();
			dedupSetSize = eiProps.getProperty("DEDUPSETSIZE");
			// jam Y2K3
			customizedEndYear = Integer.parseInt(eiProps.getProperty("SYSTEM_ENDYEAR"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}%>

<%
	pagesize = Integer.parseInt(pageSize.trim());
	dedupsetsize = Integer.parseInt(dedupSetSize.trim());
	dedupsetsize1 = dedupsetsize + 1;

	UserSession ussession = (UserSession) client.getUserSession();
	sessionId = ussession.getSessionid();
	pUserId = ussession.getUserid();
	if ((pUserId != null) && (pUserId.trim().length() != 0)) {
		personalization = true;
	}

	String customerId = ussession.getCustomerid().trim();
	clientCustomizer = new ClientCustomizer(ussession);
	isFullTextPresent = clientCustomizer.checkFullText("citationResults");
	//isRssLinkPresent=clientCustomizer.checkRssLink("true");
	isGraphDownloadPresent = clientCustomizer.checkGraphDownload();
	isRssLinkPresent = clientCustomizer.checkRssLink();
	isCitLocalHoldingsPresent = clientCustomizer.checkLocalHolding("citationResults");

	isPersonalizationPresent = clientCustomizer.checkPersonalization();
	isEmailAlertsPresent = clientCustomizer.checkEmailAlert();
	customizedLogo = clientCustomizer.getLogo();

	navigator = (String) request.getParameter("navigator");

	location = (String) request.getParameter("location");

	currentPage = request.getParameter("COUNT");

	if (null != request.getParameter("pageSizeVal")) {
		pagesize = Integer.parseInt(request.getParameter("pageSizeVal"));
	} else {
		if (null != ussession.getRecordsPerPage()) {
			pagesize = Integer.parseInt(ussession.getRecordsPerPage());
		}
	}

	if (currentPage != null && currentPage.trim().equals("")) {
		currentPage = null;
	}

	//TMH - UI refresh, get the "PAGE" parameter from the request
	//when the COUNT param is empty.  Indicates page navigation
	if (currentPage == null) {
		currentPage = request.getParameter("PAGE");
		if (currentPage != null && currentPage.trim().equals("")) {
			currentPage = null;
		} else if (currentPage != null) {
			// convert to index
			currentPage = Integer.toString(Integer.parseInt(currentPage) * pagesize);
		}
	}

	int dbMask = -1;

	if (datab != null) {
		dbMask = Integer.parseInt(datab);
	}

	if ((request.getParameter("txtcombine") != null)) {
		combineSearch = request.getParameter("txtcombine").trim();
	}

	if ((request.getParameter("errorbackurl") != null)) {
		errorBackUrl = request.getParameter("errorbackurl").trim();
	}

	if ((request.getParameter("defaultdb") != null)) {
		defaultdb = request.getParameter("defaultdb").trim();
	}

	String results = "";
	if ((request.getParameter("RESULTS") != null)) {
		results = request.getParameter("RESULTS");
	}

	StringBuffer search = null;
	StringBuffer queryString = new StringBuffer();
	String selectedDatabase = null;
	boolean uniqueDatabase = true;

	combineSearch = perl.substitute("s/\\s+/ /g", combineSearch);
	combineCharArray = combineSearch.toCharArray();

	// get list of all searches in session
	List sessionsearches = Searches.getListSessionSearches(sessionId);

	for (int i = 0; i < combineCharArray.length; i++) {
		//If char arry contains # , then we read char array that contains number after #
		if (combineCharArray[i] == '#') {
			search = new StringBuffer();
			for (int j = (i + 1); j < (combineCharArray.length); j++) {
				//checking for number
				if (Character.isDigit(combineCharArray[j])) {
					search.append(combineCharArray[j]);
					i = (j + 1);
				} else {
					//If not number we break the loop and continue from the current char index
					i = (j - 1);
					break;
				}
			}

			if (search.toString().length() > 0) {
				int sno = Integer.parseInt(search.toString()) - 1;
				if (sno < sessionsearches.size()) {
					// grab session object by index from list
					Query aquery = (Query) sessionsearches.get(sno);
					if (aquery != null) {
						databaseList.add(Integer.toString(aquery.getDataBase()));
						queryString.append("(").append(aquery.getPhysicalQuery()).append(")");
					}
				} else {

					if(request.getParameter("searchTypeName") != null){
						errorBackUrl = request.getParameter("searchTypeName");
					}

					String urlString = "/controller/servlet/Controller?CID=" + errorBackUrl + "&database=" + defaultdb
							+ "&history=t&errorCode=" + SystemErrorCodes.SEARCH_HISTORY_NOIDEXISTS;
					client.setRedirectURL(urlString);
					client.setRemoteControl();
					return;
				}

			}
		} else {
			queryString.append(combineCharArray[i]);
		}
	}

	if (dbMask == -1) //Coming from saved search
	{
		if (databaseList.size() == 1) {
			dbName = (String) databaseList.get(0);
			uniqueDatabase = true;
		} else if (databaseList.size() > 1) {
			dbName = (String) databaseList.get(0);

			for (int i = 1; i < databaseList.size(); i++) {
				if (!(dbName.equals((String) databaseList.get(i)))) {
					uniqueDatabase = false;
				}
			}
		}

		dbMask = Integer.parseInt(dbName);
	}

	String[] credentials = ussession.getCartridge();

	if (uniqueDatabase && (dbMask != 8 && dbMask != 16)) {
		DocumentBasket documentBasket = new DocumentBasket(sessionId);

		try {
			String sort = request.getParameter("sort");
			String sortDir = request.getParameter("sortdir");

			searchID = request.getParameter("SEARCHID");
			if ((searchID == null) || searchID.equals(StringUtil.EMPTY_STRING)) {
				currentPage = "1";
				initialSearch = true;

				rerun = request.getParameter("RERUN");
				if ((rerun == null) || rerun.equals(StringUtil.EMPTY_STRING)) {

					queryObject = new Query(databaseConfig, credentials);
					queryObject.setDataBase(dbMask);

					// jam 12/20/2004 - fix clear on new search bug
					// code was not working in combined history searches
					if (documentBasket.getClearOnNewSearch()) {
						clearBasket = true;
						sc.checkBasket(false);
					}

					searchID = new GUID().toString();
					queryObject.setID(searchID);

					queryObject.setSearchType("Combined");

					queryObject.setSearchPhrase(queryString.toString(), "", "", "", "", "", "", "");
					queryObject.setAutoStemming("");
				} else {

					// rerun
					//log(" RERUN " + rerun);

					// retreive query object and individually set properties
					queryObject = Searches.getSearch(rerun);
					queryObject.setDatabaseConfig(databaseConfig);
					queryObject.setCredentials(credentials);

					searchID = new GUID().toString();
					queryObject.setDataBase(dbMask);
					queryObject.setID(searchID);
					queryObject.clearDupSet();
					queryObject.setDeDup(false);

				}

				queryObject.setSortOption(new Sort(sort, sortDir));

				queryObject.setSearchQueryWriter(new FastQueryWriter());
				queryObject.compile();

				// NOTE: changes to the refinements object will be reflected in queryObject (This is by (poor?) design)
				Refinements refinements = queryObject.getRefinements();
				// add combined query as single term to show as breadcrumb
				if (refinements.size() == 0) {
					refinements.addInitialRefinementStep(queryObject.getIntermediateQuery(), queryObject.getDisplayQuery());
				}

				client.addComment("Fast Query:" + queryObject.getSearchQuery());
				client.addComment("EI Query" + queryObject.getPhysicalQuery());

				long beginQuery = System.currentTimeMillis();

				result = sc.openSearch(queryObject, sessionId, pagesize, false);

				long searchTime = result.getResponseTime();
				long endQuery = System.currentTimeMillis();
				long totalQuery = endQuery - beginQuery;
				client.addComment("Fast Search Response Time:" + Long.toString(searchTime));
				client.addComment("Fast Network Time:" + Long.toString(totalQuery - searchTime));
				client.addComment("Fast Total Response Time:" + Long.toString(totalQuery));

				queryObject.setRecordCount(Integer.toString(result.getHitCount()));

				// save query moved from here to after out.flush call
			} else { // searchID is not null
				// run from session history or saved searches
				//log(" HISTORY " + searchID);
				updateCurrentQuery = false;
				initialSearch = false;

				// retreive query object and individually set properties
				if ((location != null) && ("ALERT".equalsIgnoreCase(location) || "SAVEDSEARCH".equalsIgnoreCase(location))) {
					// get Query from SAVED searches
					queryObject = SavedSearches.getSearch(searchID);

					searchID = new GUID().toString();
					// reset
					queryObject.setID(searchID);
					queryObject.setVisible(Query.ON);
					queryObject.setSavedSearch(Query.OFF);
					queryObject.setEmailAlert(Query.OFF);
					queryObject.setDeDup(false);
					updateCurrentQuery = true;
					// set as initial search - default is false
					initialSearch = true;

					if ("ALERT".equalsIgnoreCase(location)) {
						String emailweek = (String) request.getParameter("emailweek");
						queryObject.setEmailAlertWeek(emailweek);
					}

					if (request.getParameter("DOCINDEX") != null) {
						jumpIndex = Integer.parseInt(request.getParameter("DOCINDEX"));
						format = request.getParameter("format");
						// no need to cache ahead - single document view
						cacheAheadNumber = 0;
					}
				} else {
					// get Query from current session history
					queryObject = Searches.getSearch(searchID);
				}

				queryObject.setSearchQueryWriter(new FastQueryWriter());
				queryObject.setDatabaseConfig(databaseConfig);
				queryObject.setCredentials(credentials);

				// add combined query as single term to show as breadcrumb
				Refinements refinements = queryObject.getRefinements();
				if (refinements.size() == 0) {
					refinements.addInitialRefinementStep(queryObject.getIntermediateQuery(), queryObject.getDisplayQuery());
				}

				if ((navigator != null) && ("MORE".equalsIgnoreCase(navigator))) {
					ResultsState rs = queryObject.getResultsState();
					rs.modifyState(request.getParameter("FIELD"));
					Searches.updateSearchRefineState(queryObject);
				}

				sc = new FastSearchControl();

				result = sc.openSearch(queryObject, sessionId, pagesize, !initialSearch);

				if (initialSearch) {
					// update count on query objects retreived as saved searches
					queryObject.setRecordCount(Integer.toString(result.getHitCount()));
				}
			}

			if (personalization == true) {
				queryObject.setUserID(pUserId);
			}
			queryObject.setSessionID(sessionId);

			nTotalDocs = Integer.parseInt(queryObject.getRecordCount());

			//getting request params for deDupping

			deDupFlag = (String) request.getParameter("DupFlag");
			if (deDupFlag != null) {
				queryObject.setDeDup(deDupFlag);
				dbToDeDup = (String) request.getParameter("DupDB");
				if (dbToDeDup != null) {
					queryObject.setDeDupDB(dbToDeDup);
				}
				// we have to update here since this will
				// not be done when initialsearch=true
				// or updateCurrentQuery=true
				Searches.updateSearchDeDup(queryObject);
			}

			String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());

			if (nTotalDocs > 0) {
				if (currentPage == null) {
					currentPage = "1";
				}
				index = Integer.parseInt(currentPage);

				oPage = result.pageAt(index, Citation.CITATION_FORMAT);
				String log_action = "document";
				if (initialSearch) {
					log_action = "search";
				}

				/**
				 * Log Functionality
				 **/
				client.log("search_id", queryObject.getID());
				client.log("query_string", queryObject.getPhysicalQuery());
				client.log("sort_by", queryObject.getSortOption().getSortField());
				client.log("sort_direction", queryObject.getSortOption().getSortDirection());
				client.log("suppress_stem", queryObject.getAutoStemming());
				client.log("context", "search");
				client.log("action", log_action);
				client.log("type", "expert");
				client.log("db_name", Integer.toString(queryObject.getDataBase()));
				client.log("page_size", pageSize);
				client.log("format", "citation");
				client.log("doc_id", " ");
				client.log("num_recs", Integer.toString(oPage.docCount()));
				client.log("doc_index", Integer.toString(index));
				client.log("hits", Integer.toString(nTotalDocs));
				client.setRemoteControl();

				//Writing out XML
				if (jumpIndex == -1) {
					out.write("<PAGE>");

					StringBuffer backurl = new StringBuffer();
					backurl.append("CID=combineSearchHistory").append("&");
					backurl.append("SEARCHID=").append(searchID).append("&");
					backurl.append("COUNT=").append(index).append("&");
					backurl.append("database=").append(dbMask);

					out.write("<BACKURL>");
					out.write(URLEncoder.encode(backurl.toString()));
					out.write("</BACKURL>");
					out.write("<RESULTS>");
					out.write(results);
					out.write("</RESULTS>");

					out.write("<HEADER/>");
					out.write("<DBMASK>");
					out.write(Integer.toString(queryObject.getDataBase()));
					out.write("</DBMASK>");
					out.write("<SEARCH-TYPE>" + queryObject.getSearchType() + "</SEARCH-TYPE>");
					out.write(strGlobalLinksXML);
					out.write("<NAVIGATION-BAR/>");
					out.write("<TOP-RESULTS-MANAGER/>");
					out.write("<BOTTOM-RESULTS-MANAGER/>");
					if (queryObject.isDeDup()) {
						out.write("<DEDUP>true</DEDUP>");
						out.write("<DUPDB>" + queryObject.getDeDupDB() + "</DUPDB>");
						int dmask = Integer.parseInt(queryObject.getDeDupDB());
						Database[] ddb = databaseConfig.getDatabases(dmask);
						out.write("<DUPDBFULL>" + ddb[0].getName() + "</DUPDBFULL>");
					} else {
						out.write("<DEDUP>false</DEDUP>");
					}

					out.write("<FOOTER/>");
					out.write("<SESSION-TABLE/>");
					out.write("<COMBINED-REFINE-SEARCH/>");
					out.write("<CLEAR-ON-VALUE>" + documentBasket.getClearOnNewSearch() + "</CLEAR-ON-VALUE>");
					out.write("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
					out.write("<PERSONALIZATION-PRESENT>" + isPersonalizationPresent + "</PERSONALIZATION-PRESENT>");
					out.write("<FULLTEXT>" + isFullTextPresent + "</FULLTEXT>");
					out.write("<RSSLINK>" + isRssLinkPresent + "</RSSLINK>");
					out.write("<NAVCHRT>" + isGraphDownloadPresent + "</NAVCHRT>");
					out.write("<LOCALHOLDINGS-CITATION>" + isCitLocalHoldingsPresent + "</LOCALHOLDINGS-CITATION>");
					out.write("<EMAILALERTS-PRESENT>" + isEmailAlertsPresent + "</EMAILALERTS-PRESENT>");
					out.write("<CUSTOMER-ID>" + customerId + "</CUSTOMER-ID>");
					out.write("<RESULTS-PER-PAGE>" + pagesize + "</RESULTS-PER-PAGE>");
					out.write("<SESSION-ID>" + sessionId + "</SESSION-ID>");
					out.write("<PERSONALIZATION>" + personalization + "</PERSONALIZATION>");
					out.write("<PERSON-USER-ID>" + pUserId + "</PERSON-USER-ID>");
					out.write("<SEARCH-ID>" + searchID + "</SEARCH-ID>");
					out.write("<RESULTS-COUNT>" + nTotalDocs + "</RESULTS-COUNT>");
					out.write("<PREV-PAGE-ID>" + (index - pagesize) + "</PREV-PAGE-ID>");
					out.write("<CURR-PAGE-ID>" + index + "</CURR-PAGE-ID>");
					out.write("<NEXT-PAGE-ID>" + (index + pagesize) + "</NEXT-PAGE-ID>");

					out.write("<DEDUPSETSIZE>" + dedupsetsize + "</DEDUPSETSIZE>");

					// output databases based on credentials
					databaseConfig.toXML(credentials, out);

					// jam - From Joel on 12/9/2004 only get and set Local holdings
					// if isCitLocalHoldingsPresent!
					// This lessens size of outgoing page XML
					if (isCitLocalHoldingsPresent) {
				        localHolding=new LocalHolding(ussession.getProperty(UserSession.LOCAL_HOLDING_KEY),2);
						oPage.setlocalHolding(localHolding);
					}

					oPage.toXML(out);
					queryObject.setDisplay(true);
					out.write(queryObject.toXMLString());

					// jam - Sort XML output
					//
					databaseConfig.sortableToXML(credentials, out);

					//                  commented if((sc != null)) out - sc (FastSearchControl) is used
					//                  on w/o checking for null
					//
					ResultNavigator nav = sc.getNavigator();
					out.write(nav.toXML(queryObject.getResultsState()));

					deDupable = nav.isDeDupable();
					out.write("<DEDUPABLE>" + deDupable + "</DEDUPABLE>");
%>
<%@ include file="database.jsp"%>
<%@ include file="queryForm.jsp"%>
<%
	out.write("</PAGE>");
					out.println("<!--END-->");
					out.flush();
				}

				if (updateCurrentQuery) {
					//log(" saveQuery: updateCurrentQuery == true ");
					Searches.saveSearch(queryObject);
				}

				if (initialSearch) {
					if (clearBasket) {
						documentBasket.removeAll();
					}

					sc.maintainNavigatorCache();
					sc.maintainCache(index, cacheAheadNumber);
					if (jumpIndex > -1) {
						client.setRedirectURL("/controller/servlet/Controller?CID=" + format + "&SEARCHID=" + queryObject.getID() + "&DOCINDEX="
								+ jumpIndex + "&PAGEINDEX=1&database=" + defaultdb + "&format=" + format + "&pageType=" + queryObject.getSearchType()
								+ "Search");
						client.setRemoteControl();
						out.println("<!--END-->");
					}

					//                   if(deDupable)
					//                    {
					//                        long beginDeDup = System.currentTimeMillis();

					//                        queryObject.setDupSet(sc.getDupSet(dedupsetsize));

					//log(" updateSearch DupSet: (initialSearch == true)");
					//                        Searches.updateSearch(queryObject);
					//                    }
				} else {
					sc.maintainCache(index, 0);
				}
			} else { // total docs is not greater than 0

				if (updateCurrentQuery) {
					//log(" saveQuery: zero results ");
					Searches.saveSearch(queryObject);
				}

				if ((request.getParameter("FORWARD") != null) && (request.getParameter("FORWARD").equals("true"))) {
					String errorCode = "0";
					if (request.getParameter("errorCode") != null) {
						errorCode = request.getParameter("errorCode");
					}

					out.write("<PAGE>");
					out.write("<HEADER/>");
					out.write("<DBMASK>");
					out.write(datab);
					out.write("</DBMASK>");
					out.write(strGlobalLinksXML);
					out.write("<NAVIGATION-BAR/>");
					out.write("<RESULTS-MANAGER/>");
					out.write("<FOOTER/>");
					out.write("<SESSION-TABLE/>");
					out.write("<COMBINED-REFINE-SEARCH/>");
					out.write("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
					out.write("<PERSONALIZATION-PRESENT>" + isPersonalizationPresent + "</PERSONALIZATION-PRESENT>");
					out.write("<FULLTEXT>" + isFullTextPresent + "</FULLTEXT>");
					out.write("<RSSLINK>" + isRssLinkPresent + "</RSSLINK>");
					out.write("<NAVCHRT>" + isGraphDownloadPresent + "</NAVCHRT>");
					out.write("<LOCALHOLDINGS-CITATION>" + isCitLocalHoldingsPresent + "</LOCALHOLDINGS-CITATION>");
					out.write("<EMAILALERTS-PRESENT>" + isEmailAlertsPresent + "</EMAILALERTS-PRESENT>");
					out.write("<CUSTOMER-ID>" + customerId + "</CUSTOMER-ID>");
					out.write("<SESSION-ID>" + sessionId + "</SESSION-ID>");
					out.write("<PERSONALIZATION>" + personalization + "</PERSONALIZATION>");
					out.write("<PERSON-USER-ID>" + pUserId + "</PERSON-USER-ID>");
					out.write("<SEARCH-ID>" + searchID + "</SEARCH-ID>");
					out.write("<RESULTS-COUNT>0</RESULTS-COUNT>");
					out.write("<ERROR-CODE>" + errorCode + "</ERROR-CODE>");
					queryObject.setDisplay(true);
					out.write(queryObject.toXMLString());
%>
<%@ include file="database.jsp"%>
<%@ include file="queryForm.jsp"%>
<%
	out.write("</PAGE>");
					out.print("<!--END-->");

				}
				else {
					flag = true;
				}
			}

			//Forward the page when no of results are zero.
			if (flag == true) {
				String urlString = "/controller/servlet/Controller?CID=errorCombineSearchResult&SEARCHID=" + searchID
						+ "&COUNT=1&FORWARD=true&database=" + Integer.toString(queryObject.getDataBase()) + "&history=t";
				if (sc.hasError()) {
					urlString = urlString.concat("&errorCode=").concat(sc.getErrorCode());
				}
				client.setRedirectURL(urlString);
				client.setRemoteControl();
				return;
			}
		}  catch (Exception e) {
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
	    } finally {
			if (sc != null) {
				sc.closeSearch();
			}
		}
	} else {
		InfrastructureException e = new InfrastructureException(SystemErrorCodes.COMBINE_HISTORY_UNIQUE_DATABASE_ERROR, "Only searches from the same database can be combined.");
        client.setException("true");
        client.setRemoteControl();
        out.write(e.toXML());
/*
		String dbErr = null;
		if (!uniqueDatabase) {
			dbErr = "mismatch";
		}
		String urlString = "/controller/servlet/Controller?CID=" + errorBackUrl + "&database=" + defaultdb + "&dberr=" + dbErr + "&history=t";
		client.setRedirectURL(urlString);
		client.setRemoteControl();
		*/
		return;
	}
%>