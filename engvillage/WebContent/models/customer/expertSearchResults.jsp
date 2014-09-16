<?xml version="1.0" encoding="UTF-8"?>
<%@page import="org.ei.config.ApplicationProperties"%>
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page contentType="application/xml; charset=UTF-8"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="org.ei.exception.SearchException"%>
<%@page import="org.ei.exception.SystemErrorCodes"%>
<%@ page import="org.ei.exception.EVBaseException"%>
<%@page import="org.apache.commons.validator.GenericValidator"%>
<%@ page language="java"%>
<%@ page session="false"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%--  import statements of ei packages. --%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.ei.data.DataCleaner"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.books.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.util.StringUtil"%>
<%@ page import="org.ei.parser.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%-- Increasing the buffer size to 20kb --%>
<%@ page buffer="20kb"%>
<%
    Logger log4j = Logger.getLogger("quickSearchResults.jsp");
	String currentPage = null;
	SearchControl sc = new FastSearchControl();
	sc.setUseNavigators(true);

	Refinements parsedrefinements = new Refinements();

	SearchResult result = null;
	Query queryObject = null;

	Page oPage = null;
	ControllerClient client = new ControllerClient(request, response);
	LocalHolding localHolding = null;
	String sessionId = null;
	String pUserId = "";
	boolean personalization = false;
	int pagesize = 0;
	int dedupsetsize = 0;
	int dedupsetsize1 = 0;

	String navigator = null;
	String location = null;
	String searchID = null;
	boolean flag = false;
	boolean astem = false;
	int index = 0;
	int nTotalDocs = 0;
	boolean cacheFlag = false;
	String term1 = "";
	String dbName = "";

	String limitLanguage = "";
	String yearSelect = "";
	String bYear = "";
	String eYear = "";
	String update = "";

	String bool1 = null;
	String bool2 = null;
	String autoStem = null;
	String deDupFlag = null;
	String dbToDeDup = null;
	boolean deDupable = false;
	String lastRefineStep = null;
	String appendstr = null;
	String intialSearchReqParam = null;

	Database databaseObj = null;
	String Expand = "false";
	boolean updateCurrentQuery = true;
	ClientCustomizer clientCustomizer = null;
	boolean isPersonalizationPresent = true;
	boolean isRssLinkPresent = false;
	boolean isFullTextPresent = true;
	boolean isGraphDownloadPresent = false;
	boolean isEmailAlertsPresent = true;
	boolean isCitLocalHoldingsPresent = false;
	String showpatentshelp = "false";

	String customizedStartYear = "1969";
	String customizedLogo = "";
	int jumpIndex = -1;
	int cacheAheadNumber = 0;
	String format = StringUtil.EMPTY_STRING;
	String rerun = StringUtil.EMPTY_STRING;

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
	isRssLinkPresent = clientCustomizer.checkRssLink();
	isGraphDownloadPresent = clientCustomizer.checkGraphDownload();
	isCitLocalHoldingsPresent = clientCustomizer.checkLocalHolding("citationResults");

	isPersonalizationPresent = clientCustomizer.checkPersonalization();
	isEmailAlertsPresent = clientCustomizer.checkEmailAlert();
	customizedLogo = clientCustomizer.getLogo();
	customizedStartYear = clientCustomizer.getSYear();

	if (request.getParameter("intialSearch") != null) {
		intialSearchReqParam = request.getParameter("intialSearch").trim();
	}

	if (null != request.getParameter("pageSizeVal")) {
		pagesize = Integer.parseInt(request.getParameter("pageSizeVal"));
	} else {
		if (null != ussession.getRecordsPerPage()) {
			pagesize = Integer.parseInt(ussession.getRecordsPerPage());
		}
	}

	String[] credentials = ussession.getCartridge();

	if (request.getParameter("lastRefineStep") != null) {
		lastRefineStep = request.getParameter("lastRefineStep");
	}
	try {

	if (request.getParameter("alldb") != null) {
		dbName = request.getParameter("alldb").trim();
	} else {
		int sumDb = 0;
		if (request.getParameterValues("database") != null) {
			String[] dbs = request.getParameterValues("database");
			for (int i = 0; i < dbs.length; i++) {
				sumDb += Integer.parseInt(dbs[i]);
			}
			//remove the referex db but continue the search
            if(((sumDb & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK) && ApplicationProperties.getInstance().isItTime(ApplicationProperties.REFEREX_MASK_DATE)){
	            if((sumDb & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK && sumDb != DatabaseConfig.PAG_MASK){
	            	sumDb -= DatabaseConfig.PAG_MASK;
	            }else if (sumDb == DatabaseConfig.PAG_MASK){
	            	throw new SearchException(SystemErrorCodes.EBOOK_REMOVED, "Referex DB no longer available");
	            }
            }
			dbName = String.valueOf(sumDb);
		} else {
			// there is no db checkbox on easy search form
			dbName = String.valueOf(databaseConfig.getScrubbedMask(credentials));
		}
	}

	if (request.getParameter("showpatentshelp") != null) {
		showpatentshelp = request.getParameter("showpatentshelp").trim();
	}

	int intDbMask = Integer.parseInt(dbName);

	navigator = (String) request.getParameter("navigator");

	location = (String) request.getParameter("location");

	currentPage = request.getParameter("COUNT");
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

	boolean initialSearch = false;
	boolean clearBasket = false;
	String inclexcl = request.getParameter("exclude");

	DocumentBasket documentBasket = new DocumentBasket(sessionId);


		if (!UserSession.hasCredentials(intDbMask, databaseConfig.getMask(credentials))) {
			throw new SecurityException("<DISPLAY>You do not have access to the databases requested</DISPLAY>");

		}

		String sort = request.getParameter("sort");
		String sortDir = request.getParameter("sortdir");
		searchID = request.getParameter("SEARCHID");
		String searchtype = request.getParameter("searchtype");

		//change for RSS link
		if (request.getParameter("DOCINDEX") != null) {
			jumpIndex = Integer.parseInt(request.getParameter("DOCINDEX"));
			format = request.getParameter("format");
			// jam 11-25-2002
			// no need to cache ahead - single document view
			cacheAheadNumber = 0;
			//log("docindex= "+jumpIndex);
		}

		if ((searchID == null) || searchID.equals(StringUtil.EMPTY_STRING)) {

			currentPage = "1";
			initialSearch = true;

			// NEW SEARCH
			rerun = request.getParameter("RERUN");
			if ((rerun == null) || rerun.equals(StringUtil.EMPTY_STRING)) {

				queryObject = new Query(databaseConfig, credentials);
				queryObject.setDataBase(intDbMask);

				searchID = (new GUID()).toString();
				queryObject.setID(searchID);

				if (documentBasket.getClearOnNewSearch()) {
					clearBasket = true;
					sc.checkBasket(false);
				}

				if ((searchtype != null) && Query.TYPE_EASY.equalsIgnoreCase(searchtype)) {
					queryObject.setSearchType(Query.TYPE_EASY);
					queryObject.setDataBase(intDbMask);

					Map startEndYears = databaseConfig.getStartEndYears(credentials, intDbMask);
					bYear = (String) startEndYears.get(DatabaseConfig.STARTYEAR);
					eYear = (String) startEndYears.get(DatabaseConfig.ENDYEAR);

					// jam 1/3/2006 Year change
					queryObject.setStartYear(bYear);
					queryObject.setEndYear(eYear);

					// jam 11/8/2004 - Autostemming always "ON" for Easy Search
					queryObject.setAutoStemming("on");
				} else {
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
					if ((bYear == null) || (eYear == null)) {
						Map startEndYears = databaseConfig.getStartEndYears(credentials, intDbMask);
						bYear = (bYear == null) ? (String) startEndYears.get(DatabaseConfig.STARTYEAR) : bYear;
						eYear = (eYear == null) ? (String) startEndYears.get(DatabaseConfig.ENDYEAR) : eYear;
					}
					queryObject.setStartYear(bYear);
					queryObject.setEndYear(eYear);

					if (!yearSelect.equals("yearrange")) {
						update = request.getParameter("updatesNo");
						queryObject.setLastUpdatesCount(update);
					}
					queryObject.setSearchType(Query.TYPE_EXPERT);

				}

				// adding the initial search as a single refinement so it will appear as a breadcrumb
				Refinements refinements = queryObject.getRefinements();
				if (request.getParameter("searchWord1") != null) {
					EiNavigator easynav = EiNavigator.createNavigator(EiNavigator.ALL);

					// check to see if this is a pcinav query from a cited by link
					// and use that navigator data create the initial refinement
					if (request.getParameter("pcinav") != null) {
						easynav = EiNavigator.createNavigator(EiNavigator.PCI);
						easynav.setModifierValue(request.getParameter("pcinav"));
					} else {
						easynav.setModifierValues(request.getParameterValues("searchWord1"));
					}
					refinements.addRefinement(easynav);
				}

				if (request.getParameter("searchWord1") != null) {
					term1 = request.getParameter("searchWord1");
				}

				// jam 5/16/2005 - taken from quickSearchResults.jsp
				//
				// Flipped request logic!! - no more hidden variables
				// if request parameter is 'on' - the autostemming
				// off checkbox was checked!
				autoStem = request.getParameter("autostem");

				if ("on".equalsIgnoreCase(autoStem)) {
					queryObject.setAutoStemming("off");
				} else {
					queryObject.setAutoStemming("on");
				}

				queryObject.setSearchPhrase(term1, "", "", "", "", "", "", "");
			} else {

				// rerun from 'breadcrumb' or refinement
				//log(" RERUN " + rerun);

				// retreive query object and individually set properties
				queryObject = Searches.getSearch(rerun);

				// If the sort parameter is not being changed by by this request
				// keep the sort the same as the current query.
				if (sort == null) {
					sort = queryObject.getSortOption().getSortField();
					sortDir = queryObject.getSortOption().getSortDirection();
				}

				if (queryObject == null) {
					log(" ERROR = CANNOT FIND SEARCH RERUN: " + rerun);
				}
				queryObject.setDatabaseConfig(databaseConfig);
				queryObject.setCredentials(credentials);

				searchID = new GUID().toString();
				queryObject.setID(searchID);
				queryObject.setDataBase(intDbMask);
				queryObject.clearDupSet();
				queryObject.setDeDup(false);

				// get refinements object from queryObject for modification
				// NOTE: changes to refinements object will be reflected in queryObject (This is by (poor?) design)
				Refinements refinements = queryObject.getRefinements();

				// Easy Search Breadcrumb Navigation
				if (queryObject.getSearchType().equals(Query.TYPE_EASY)) {

					// adding a step - append=mathmatical+tests&section=clnav
					if (request.getParameter("append") != null) {
						EiNavigator easynav = EiNavigator.createNavigator(request.getParameter("section"));
						easynav.setModifierValues(request.getParameterValues("append"));
						refinements.addRefinement(easynav);
					}
				}
				// chopping back to step - STEP=2
				if (request.getParameter("STEP") != null) {
					refinements.setRefinementStep(request.getParameter("STEP"));
				}

				// removing specific step - REMOVE=1
				if (request.getParameter("REMOVE") != null) {
					refinements.removeRefinementStep(request.getParameter("REMOVE"));
				}

				if (!queryObject.getSearchType().equals(Query.TYPE_EASY)) {
					// Faceted Browsing on Expert results
					// Quick, Expert, Thes. SEARCH REFINED

					// adding refinements in current request to a temporary refinements object
					Refinements newrefinements = new Refinements();

					// loop williterate through all possible navigators and look for values
					// in request object
					Iterator itrNames = EiNavigator.getNavigatorNames().iterator();
					while (itrNames.hasNext()) {
						String navfield = (String) itrNames.next();

						if (request.getParameterValues(navfield) != null) {
							String[] allvals = request.getParameterValues(navfield);

							EiNavigator einavigator = EiNavigator.createNavigator(navfield);
							einavigator.setModifierValues(allvals);
							einavigator.setIncludeExcludeAll((inclexcl != null));

							newrefinements.addRefinement(einavigator);
						}
					}

					// "add a term" textbox (was checkbox and text) form element
					appendstr = request.getParameter("topappend");
					if ((appendstr == null) || (appendstr.length() == 0)) {
						appendstr = request.getParameter("btmappend");
					}
					if ((appendstr != null) && (appendstr.length() != 0)) {
						EiNavigator addaterm = EiNavigator.createNavigator(EiNavigator.ALL);
						addaterm.setModifierValue(appendstr);
						newrefinements.addRefinement(addaterm);
					}

					if (newrefinements.size() > 0) {
						// get all of the new refinements as single query
						String refinementPhrase = newrefinements.getRefinementQueryString();

						//System.out.println(" refinementPhrase ==> " + refinementPhrase);

						// add single refinementPhrase as a single 'ALL' refinement to the queryobject
						EiNavigator singleterm = EiNavigator.createNavigator(EiNavigator.ALL);
						singleterm.setModifierValue(refinementPhrase);

						// preserve if this was a "NOT/EXCLUDE"
						singleterm.setIncludeExcludeAll((inclexcl == null));

						// here we will check for "resultsorall" parameter
						// if "results" - continue as usual - concatentate refinements to original query
						// if "all" - do not concatenate Intermediate Query
						//          - run refinements w/o leading " AND "
						String resultsorall = request.getParameter("resultsorall");
						if (resultsorall != null) {
							// if "all" - newrefinements become query
							// other refinements are ignored - this is a new search
							if (EiNavigator.ALL.equalsIgnoreCase(resultsorall)) {
								// clear all other refinements
								refinements = new Refinements();
							}
						}

						refinements.addRefinement(singleterm);
					}

					// NEW - Build query completely out of the refinements
					String newphrase = refinements.connectRefinements();

					searchID = new GUID().toString();
					Query newqueryObject = new Query(databaseConfig, credentials);
					newqueryObject.setID(searchID);
					newqueryObject.setDataBase(intDbMask);
					newqueryObject.setDeDup(false);
					newqueryObject.setSearchType(Query.TYPE_EXPERT);

					// copy some data from the old query object
					newqueryObject.setStartYear(queryObject.getStartYear());
					newqueryObject.setEndYear(queryObject.getEndYear());
					newqueryObject.setLastFourUpdates(queryObject.getLastFourUpdates());
					newqueryObject.setEmailAlertWeek(queryObject.getEmailAlertWeek());

					// jam - 5/16/05 Turkey build - bug found by Rafael
					// even though all searches end up as Experts - keep stemming values
					// from original search
					newqueryObject.setAutoStemming(queryObject.getAutoStemming());

					// sort arguments are hidden in refine form
					// so we do not have to copy them

					newqueryObject.setSearchPhrase(newphrase, "", "", "", "", "", "", "");

					queryObject = newqueryObject;
					updateCurrentQuery = true;

					queryObject.setRefinements(refinements);

					//log(" =:> newphrase: " + newphrase);
					//log(" EXPERT =:> refinements: " + refinements.toXML());

				} // if EXPRT
			}

			queryObject.setSortOption(new Sort(sort, sortDir));
			queryObject.setSearchQueryWriter(new FastQueryWriter());
			queryObject.compile();

			/*
			This code was introduced to handle WN PCI requests
			in the refinements object - now we handle them
			when adding the initial search as a single refinement (see above)
			if(queryObject.getSearchType().equals(Query.TYPE_EASY))
			{
			  Refinements refinements = queryObject.getRefinements();
			  if(refinements.size() == 1)
			  {
			    refinements = new Refinements();
			    refinements.addInitialRefinementStep(queryObject.getIntermediateQuery(), queryObject.getDisplayQuery());
			    queryObject.setRefinements(refinements);
			  }
			}*/

			/*
			    JAM TURKEY CODE to parse navigators out of expert search query
			 */
			BooleanQuery bq = queryObject.getParseTree();
			RefinementFieldGatherer fg = new RefinementFieldGatherer();
			Map mapFields = fg.gatherExactFields(bq);
			Iterator itr = mapFields.keySet().iterator();

			while (itr.hasNext()) {
				Field field = (Field) itr.next();
				EiNavigator einavigator = EiNavigator.createNavigator(field);

				if (!EiNavigator.DB.equals(einavigator.getName())) {
					List trmMods = new ArrayList();

					// create modifiers from the list of terms and add them
					// to a navigator for this field
					List trmList = (List) mapFields.get(field);
					Iterator itrTerms = trmList.iterator();
					while (itrTerms.hasNext()) {
						String term = (String) itrTerms.next();
						// This is messy.  Could be somewhere else?
						if (EiNavigator.AU.equals(einavigator.getName())) {
							// remove 'spacer' inserted into Author names by DataCleaner
							term = DataCleaner.restoreAuthor(term);
						}

						trmMods.add(EiModifier.parseModifier(term));
					}

					einavigator.setModifiers(trmMods);
					parsedrefinements.addRefinement(einavigator);
				}
			}

			client.addComment("FAST Query:" + queryObject.getSearchQuery());
			client.addComment("EI Query" + queryObject.getPhysicalQuery());

			long beginQuery = System.currentTimeMillis();
        	HitHighlighter highlighter = new HitHighlighter(queryObject.getParseTree());
        	sc.setHitHighlighter(highlighter);
			result = sc.openSearch(queryObject, sessionId, pagesize, false);

			long searchTime = result.getResponseTime();
			long endQuery = System.currentTimeMillis();
			long totalQuery = endQuery - beginQuery;
			client.addComment("Fast Search Response Time:" + Long.toString(searchTime));
			client.addComment("Fast Network Time:" + Long.toString(totalQuery - searchTime));
			client.addComment("Fast Total Response Time:" + Long.toString(totalQuery));

			//getting the results count for this query
			queryObject.setRecordCount(Integer.toString(result.getHitCount()));

			// save query moved from here to after out.flush call

		} // new query - No searchID
		else { // SearchID is not null
				// run from history - Saved Search, Email Alert, Session History
				//log(" HISTORY " + searchID);
				// set these to false - email alert Alert or run from Saved Searches will set as needed
			updateCurrentQuery = false;
			initialSearch = false;

			if (null != intialSearchReqParam) {
				initialSearch = true;
				updateCurrentQuery = true;

				if (documentBasket.getClearOnNewSearch()) {
					clearBasket = true;
					sc.checkBasket(false);
				}
			}

			searchID = request.getParameter("SEARCHID");

			// retreive query object and set properties
			if ((location != null) && ("ALERT".equalsIgnoreCase(location) || "SAVEDSEARCH".equalsIgnoreCase(location))) {
				// get Query from SAVED searches
				queryObject = SavedSearches.getSearch(searchID);
			    if (queryObject == null) {
			        throw new SearchException(SystemErrorCodes.SAVED_SEARCH_NOT_FOUND, "Unable to find saved search / alert by ID: '" + searchID + "'");
			    }
				// create and store new searchid so we show in Session History
				searchID = new GUID().toString();
				queryObject.setID(searchID);
				queryObject.setVisible(Query.ON);
				queryObject.setSavedSearch(Query.OFF);
				queryObject.setEmailAlert(Query.OFF);
                queryObject.setSessionID(sessionId);
				// bug fixed after code review - saved searches most likely have wrong dupset saved
				// so make sure this option is reset to false
				queryObject.setDeDup(false);

				// set search to show up in current session history
				updateCurrentQuery = true;

				// set as initial search - default is false
				initialSearch = true;

				if ("ALERT".equalsIgnoreCase(location)) {
					String emailweek = (String) request.getParameter("emailweek");
					queryObject.setEmailAlertWeek(emailweek);
				}
			} else {
				// get Query from current session history
				queryObject = Searches.getSearch(searchID);
			}

            if (queryObject == null) {
                throw new SearchException(SystemErrorCodes.SEARCH_NOT_FOUND, "Unable to find search by ID: '" + searchID + "'");
            }
			queryObject.setSearchQueryWriter(new FastQueryWriter());
			queryObject.setDatabaseConfig(databaseConfig);
			queryObject.setCredentials(credentials);

			// add expert query as single term to show as breadcrumb
			Refinements refinements = queryObject.getRefinements();
			if (refinements.size() == 0) {
				refinements.addInitialRefinementStep(queryObject.getIntermediateQuery(), queryObject.getDisplayQuery());
			}

			if ((navigator != null) && ("MORE".equalsIgnoreCase(navigator))) {
				ResultsState rs = queryObject.getResultsState();
				rs.modifyState(request.getParameter("FIELD"));
				Searches.updateSearchRefineState(queryObject);
			}

			long beginQuery = System.currentTimeMillis();
        	HitHighlighter highlighter = new HitHighlighter(queryObject.getParseTree());
        	sc.setHitHighlighter(highlighter);
			result = sc.openSearch(queryObject, sessionId, pagesize, !initialSearch);

			if (initialSearch) {
				queryObject.setRecordCount(Integer.toString(result.getHitCount()));
				client.addComment("Fast Query: " + queryObject.getSearchQuery());
				client.addComment("EI Query: " + queryObject.getPhysicalQuery());
				long searchTime = result.getResponseTime();
				long endQuery = System.currentTimeMillis();
				long totalQuery = endQuery - beginQuery;
				client.addComment("Fast Search Response Time: " + Long.toString(searchTime));
				client.addComment("Fast Network Time: " + Long.toString(totalQuery - searchTime));
				client.addComment("Fast Total Response Time: " + Long.toString(totalQuery));
			}
		}

		if (personalization == true) {
			queryObject.setUserID(pUserId);
		}
		queryObject.setSessionID(sessionId);

		nTotalDocs = Integer.parseInt(queryObject.getRecordCount());

		// getting request params for deDupping
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
				// Faceted Browsing on Expert results
				navigator = "NEXT";
				log_action = "search";
			}

			/**
			 * Log Functionality
			 **/
			client.log("search_id", searchID);
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
			client.log("doc_id", " ");
			client.log("num_recs", Integer.toString(oPage.docCount()));
			client.log("doc_index", Integer.toString(index));
			client.log("hits", Integer.toString(nTotalDocs));
			if (request.getParameter("mapevent") != null) {
				client.log("mapevent", request.getParameter("mapevent"));
			}
			client.setRemoteControl();

			//Writing out XML
			if (jumpIndex == -1) {
				out.write("<PAGE>");

				StringBuffer backurl = new StringBuffer();
				backurl.append("CID=expertSearchCitationFormat").append("&");
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
				out.write("<BACKURL>");
				out.write(URLEncoder.encode(backurl.toString()));
				out.write("</BACKURL>");

				out.write("<HEADER/>");
				out.write(strGlobalLinksXML);
				out.write("<NAVIGATION-BAR/>");
				out.write("<DBMASK>");
				out.write(dbName);
				out.write("</DBMASK>");
				out.write("<SHOW-PATENTS-HELP>");
				out.write(showpatentshelp);
				out.write("</SHOW-PATENTS-HELP>");
				out.write("<SEARCH-TYPE>" + queryObject.getSearchType() + "</SEARCH-TYPE>");
				out.write("<SEARCH/>");
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
				out.write("<CLEAR-ON-VALUE>" + documentBasket.getClearOnNewSearch() + "</CLEAR-ON-VALUE>");
				out.write("<FOOTER/>");
				out.write("<SESSION-TABLE/>");
				out.write("<CUSTOMIZED-STARTYEAR>" + customizedStartYear + "</CUSTOMIZED-STARTYEAR>");
				out.write("<CUSTOMIZED-ENDYEAR>" + customizedEndYear + "</CUSTOMIZED-ENDYEAR>");
				out.write("<PERSONALIZATION-PRESENT>" + isPersonalizationPresent + "</PERSONALIZATION-PRESENT>");
				out.write("<FULLTEXT>" + isFullTextPresent + "</FULLTEXT>");
				out.write("<RSSLINK>" + isRssLinkPresent + "</RSSLINK>");
				out.write("<NAVCHRT>" + isGraphDownloadPresent + "</NAVCHRT>");
				out.write("<LOCALHOLDINGS-CITATION>" + isCitLocalHoldingsPresent + "</LOCALHOLDINGS-CITATION>");
				out.write("<EMAILALERTS-PRESENT>" + isEmailAlertsPresent + "</EMAILALERTS-PRESENT>");
				out.write("<CUSTOMER-ID>" + customerId + "</CUSTOMER-ID>");
				out.write("<RESULTS-PER-PAGE>" + pagesize + "</RESULTS-PER-PAGE>");
				out.write("<SESSION-ID>" + sessionId + "</SESSION-ID>");
				out.write("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
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

				// jam - Sort XML output
				//
				databaseConfig.sortableToXML(credentials, out);
%>
<%@ include file="database.jsp"%>
<%@ include file="queryForm.jsp"%>
<%
	//              commented if((sc != null)) out - sc (FastSearchControl) is used
				//              on w/o checking for null
				//
				ResultNavigator nav = null;
				nav = sc.getNavigator();

				if (nav != null) {
					deDupable = nav.isDeDupable();
				} else {
					deDupable = queryObject.isDeDup();
				}

				// This is new 'flag' used in XSL - Previously the
				// XSL just checked the dbmask
				out.write("<DEDUPABLE>" + deDupable + "</DEDUPABLE>");

				if (queryObject.getSearchType().equals(Query.TYPE_EASY)) {
					if ((nav != null) && (queryObject.getRefinements() != null)) {
						nav.removeRefinements(queryObject.getRefinements());
					}
				} else {
					if ((nav != null) && (parsedrefinements.size() != 0)) {
						// Removing Refinements from an Expert search
						nav.removeRefinements(parsedrefinements);
					}
				}

				if (nav != null) {
					out.write(nav.toXML(queryObject.getResultsState()));
				}

				queryObject.setDisplay(true);

				out.write(queryObject.toXMLString());
				out.write("</PAGE>");
				out.println("<!--END-->");
				out.flush();
			}

			if (updateCurrentQuery) {
				//log(" saveQuery: updateCurrentQuery == true ");
				Searches.updateExistingSearch(queryObject);
			}

			if (initialSearch) {
				//log("initialSearch DEDUP " + queryObject.isDeDup());

				// this was checking "searchtype" param
				// but when query is run from Alert, SavedSearch or History - searchtype is null
				// so check queryObject.getSearchType() which is sest to "searchtype" when query is new

				if (clearBasket) {
					documentBasket.removeAll();
				}
				sc.maintainNavigatorCache();
				sc.maintainCache(index, cacheAheadNumber);
				if (jumpIndex > -1) {
					client.setRedirectURL("/controller/servlet/Controller?CID=" + format + "&SEARCHID=" + queryObject.getID() + "&DOCINDEX="
							+ jumpIndex + "&PAGEINDEX=1&database=" + dbName + "&format=" + format + "&pageType=" + queryObject.getSearchType()
							+ "Search");
					client.setRemoteControl();
					out.println("<!--END-->");
				}
			} else {
				sc.maintainCache(index, 0);
			}

		} // nTotalDocs>0
		else { // zero results

			if (updateCurrentQuery) {
				//log(" saveQuery: zero results ");
				Searches.updateExistingSearch(queryObject);
			}

			String errorCode = "0";
			if (request.getParameter("errorCode") != null) {
				errorCode = request.getParameter("errorCode");
			}

			client.addComment("Fast Query:" + queryObject.getSearchQuery());
			client.addComment("EI Query" + queryObject.getPhysicalQuery());

			// Logging
			client.log("SEARCH_ID", searchID);
			client.log("QUERY_STRING", queryObject.getPhysicalQuery());
			client.log("bookcreds", BookCredentials.toString(credentials));
			client.log("sort_by", queryObject.getSortOption().getSortField());
			client.log("sort_direction", queryObject.getSortOption().getSortDirection());
			client.log("suppress_stem", queryObject.getAutoStemming());
			client.log("ACTION", "search");
			client.log("TYPE", "expert");
			client.log("context", "search");
			client.log("doc_id", " ");

			client.log("FORMAT", "CIT");
			client.log("DB_NAME", dbName);
			client.log("PAGE_SIZE", pageSize);
			client.log("HIT_COUNT", "0");
			client.log("DOC_INDEX", "0");
			client.log("NUM_RECS", "0");
			client.setRemoteControl();

			out.write("<PAGE>");
			out.write("<HEADER/>");
			out.write("<DBMASK>");
			out.write(dbName);
			out.write("</DBMASK>");
			out.write(strGlobalLinksXML);
			out.write("<NAVIGATION-BAR/>");
			out.write("<RESULTS-MANAGER/>");
			out.write("<FOOTER/>");
			out.write("<SESSION-TABLE/>");
			out.write("<SEARCH/>");
			out.write("<CUSTOMER-ID>" + customerId + "</CUSTOMER-ID>");
			out.write("<CUSTOMIZED-STARTYEAR>" + customizedStartYear + "</CUSTOMIZED-STARTYEAR>");
			out.write("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
			out.write("<PERSONALIZATION-PRESENT>" + isPersonalizationPresent + "</PERSONALIZATION-PRESENT>");
			out.write("<FULLTEXT>" + isFullTextPresent + "</FULLTEXT>");
			out.write("<RSSLINK>" + isRssLinkPresent + "</RSSLINK>");
			out.write("<NAVCHRT>" + isGraphDownloadPresent + "</NAVCHRT>");
			out.write("<LOCALHOLDINGS-CITATION>" + isCitLocalHoldingsPresent + "</LOCALHOLDINGS-CITATION>");
			out.write("<EMAILALERTS-PRESENT>" + isEmailAlertsPresent + "</EMAILALERTS-PRESENT>");
			out.write("<SESSION-ID>" + sessionId + "</SESSION-ID>");
			out.write("<PERSONALIZATION>" + personalization + "</PERSONALIZATION>");
			out.write("<PERSON-USER-ID>" + pUserId + "</PERSON-USER-ID>");
			out.write("<SEARCH-ID>" + searchID + "</SEARCH-ID>");
			out.write("<RESULTS-COUNT>0</RESULTS-COUNT>");
			out.write("<ERROR-CODE>" + errorCode + "</ERROR-CODE>");
			out.write("<SEARCH-TYPE>" + queryObject.getSearchType() + "</SEARCH-TYPE>");

			queryObject.setDisplay(true);
			out.write(queryObject.toXMLString());
%>
<%@ include file="database.jsp"%>
<%@ include file="queryForm.jsp"%>
<%
            	out.write("</PAGE>");
            			out.println("<!--END-->");
            		}

    } catch (Exception e) {
        log4j.error("Unable to process Expert search!", e);
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
    }	finally {
            		if (sc != null) {
            			sc.closeSearch();
            		}
            	}
            %>
<%!private String getnsURL(String database, String searchType) {
		StringBuffer buf = new StringBuffer();
		if (searchType.equals(Query.TYPE_EXPERT)) {
			buf.append("CID=expertSearch&database=");
		} else {
			buf.append("CID=easySearch&database=");
		}

		buf.append(database);
		return buf.toString();
	}

	String pageSize = null;
	String dedupSetSize = null;
	DatabaseConfig databaseConfig = null;
	int customizedEndYear = 0;

	public void jspInit() {
		try {
			ApplicationProperties eiProps = ApplicationProperties.getInstance();
			pageSize = eiProps.getProperty("DISPLAYPAGESIZE");
			dedupSetSize = eiProps.getProperty("DEDUPSETSIZE");
			databaseConfig = DatabaseConfig.getInstance();

			// jam Y2K3
			customizedEndYear = Integer.parseInt(eiProps.getProperty("SYSTEM_ENDYEAR"));
		} catch (Exception e) {
			e.printStackTrace();
			log("Errorrr:" + e.getMessage());
		}
	}%>
