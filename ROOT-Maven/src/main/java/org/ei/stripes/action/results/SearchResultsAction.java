package org.ei.stripes.action.results;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.LocalizableError;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.config.JSPPathProperties;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocumentBasket;
import org.ei.domain.Query;
import org.ei.domain.Searches;
import org.ei.domain.Sort;
import org.ei.domain.navigators.state.ResultNavigatorStateHelper;
import org.ei.exception.EVBaseException;
import org.ei.exception.ErrorXml;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.UserSession;
import org.ei.stripes.action.ControllerRequestHelper;
import org.ei.stripes.action.EVPathUrl;
import org.ei.stripes.action.WebAnalyticsEventProperties;
import org.ei.stripes.adapter.GenericAdapter;
import org.ei.stripes.adapter.IBizBean;
import org.ei.stripes.adapter.IBizXmlAdapter;
import org.ei.stripes.view.SearchResult;
import org.ei.stripes.view.SearchResultNavigator;
import org.ei.tags.TagBubble;
import org.ei.web.analytics.GoogleWebAnalyticsEvent;


/**
 * This is the base class for the search results handling.
 *
 * PLEASE NOTE It can be invoked but ***ONLY*** for zero results searches!
 *
 * @see processModelXML method below for how to handle this.
 *
 * @author harovetm
 *
 */
@UrlBinding("/search/results/{$event}.url")
public class SearchResultsAction extends AbstractSearchResultsAction implements IBizBean, ICitationAction {

	private final static Logger log4j = Logger.getLogger(SearchResultsAction.class);

	public final static String RESULTS_ADAPTER = "/transform/results/SearchResultsAdapter.xsl";
	public final static String DEDUP_RESULTS_ADAPTER = "/transform/results/DedupSearchResultsAdapter.xsl";
	public final static String PATENTREF_RESULTS_ADAPTER = "/transform/results/PatentrefSearchResultsAdapter.xsl";

	private String topappend;
	private String lastRefineStep;
	private GoogleWebAnalyticsEvent webEvent = new GoogleWebAnalyticsEvent();
	private boolean searchWidget = false;
	private String swReferrer = "";
	
	private String location = "";
	private String emailweek = "";
	private String format = "";


	/**
	 * Return the XML adapter for quick search results display! (executed from
	 * org.ei.stripes.AuthInterceptor)
	 *
	 * @throws InfrastructureException
	 * @throws EVBaseException
	 * @throws ServletException
	 */
	public void processModelXml(InputStream instream) throws InfrastructureException {
		IBizXmlAdapter adapter = new GenericAdapter();
		// try {
		adapter.processXml(this, instream, getXSLPath());

		// Get the state of the special "append" navigator
		// For the first 2 navigators, show them as open if they are unitialized
		// Also, look for 'geo' navigator to see if we should turn on map
		// function (below)
		ResultNavigatorStateHelper navstatehelper = new ResultNavigatorStateHelper(context.getUserSession());
		appendopen = navstatehelper.isAppendopen();
		navstatehelper.UpdatedNavigatorsFromSessionData(navigators);

		boolean hasgeo = false;
		for (int i = 0; i < navigators.size(); i++) {
			SearchResultNavigator nav = navigators.get(i);
			if ("geo".equals(nav.getField())) {
				hasgeo = true;
				break;
			}
		}

		// Only show maps (beta) under the following conditions:
		// * HAS COMPMASK == 8192 (geobase), 2097152 (georef) or 2105344 (both)
		// * HAS a navigator called 'geonav'
		// * NOT in a china build!
		if (((compmask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK || (compmask & DatabaseConfig.GRF_MASK) == DatabaseConfig.GRF_MASK) && hasgeo
				&& !china) {
			showmap = true;
		}

		setTextzones(context.getUserSession().getUserTextZones());

		// Set the new search link appropriately
		String newsearchlink = EVPathUrl.EV_QUICK_SEARCH.value() + "?CID=quickSearch&database=" + database;
		if ("Book".equals(searchtype)) {
			newsearchlink = EVPathUrl.EV_EBOOK_SEARCH.value() + "?CID=ebookSearch&database=" + database;
		}
		if ("Expert".equals(searchtype)) {
			newsearchlink = EVPathUrl.EV_EXPERT_SEARCH.value() + "?CID=expertSearch&database=" + database;
		}
		if ("Thesaurus".equals(searchtype)) {
			newsearchlink = EVPathUrl.EV_THES_SEARCH.value() + "?CID=thesHome&database=" + database + "#init";
		}
		context.getRequest().setAttribute("searchlink", newsearchlink);

		/*
		 * } catch (Exception e) { log4j.error("Unable to process XML!", e);
		 * throw new RuntimeException(e); }
		 */
	}


	@After(stages = LifecycleStage.EventHandling)
	private void stopPerformance() {
		webEvent.recordRemoteEvent(context);
	}

	@After(stages = LifecycleStage.RequestComplete)
	private void cleanup() {
		if (this.results != null && this.results.size() > 0) {
			for (SearchResult r : results) {
				r.clear();
				r = null;
			}
			this.results.clear();
		}
		this.results = null;
	}

	/**
	 * SearchResults init method
	 *
	 * - Update the session before any search results run. This allows us to get
	 * the correct value for the results-per-page - Build the backurl
	 *
	 * @throws SessionException
	 * @throws EVBaseException
	 * @throws IOException
	 * @throws NumberFormatException
	 * @throws ParseException
	 *
	 */
	@Before
	public void init() throws EVBaseException, NumberFormatException, IOException, ParseException {
		// Set the database value
		if (StringUtils.isNotBlank(database) && (Integer.parseInt(database) & DatabaseConfig.PAG_MASK)== DatabaseConfig.PAG_MASK && (Integer.parseInt(database) != DatabaseConfig.PAG_MASK) && EVProperties.getApplicationProperties().isItTime(ApplicationProperties.REFEREX_MASK_DATE)) {
				database = Integer.toString(Integer.parseInt(database)-DatabaseConfig.PAG_MASK);
		}

		// Set search ID if not present.  This is due to case-sensitivity on the request URL.  Legacy code
		// sometimes uses "SEARCHID" and new code sometimes uses "searchid".
		Query qObj = new Query();
		if (StringUtils.isBlank(getSearchid())) {
			setSearchid(getRequest().getParameter("SEARCHID"));
			qObj = Searches.getSearch(getRequest().getParameter("SEARCHID"));
			if (qObj != null) {
				createQueryEventList(qObj);
			}
		}
		
        // Set the records-per-page
		if (null != getRequest().getParameter("pageSizeVal")) {
			UserSession usersession = context.getUserSession();
			if (usersession == null) {
				log4j.warn("UserSession object not available!");
			} else {
				try {
					usersession.setRecordsPerPage(getRequest().getParameter("pageSizeVal"));
					context.updateUserSession(usersession);
				} catch (SessionException e) {
					log4j.error("************ Unable to update navigator state: " + e.getMessage());
					throw new InfrastructureException(SystemErrorCodes.SESSION_UPDATE_FOR_RECORD_PAGE_FAILED, e);
				}
			}

		}

		// Get the values for the dropdown
		String pageSizeOptions = EVProperties.getProperty(ApplicationProperties.PAGESIZE_OPTIONS);
		StringTokenizer st = new StringTokenizer(pageSizeOptions, ",");
		if (null != st) {
			while (st.hasMoreTokens()) {
				String value = st.nextToken();
				if (!value.isEmpty()) {
					addPageCountOption(value);
				}
			}
		}

		// Build the backurl and nexturl from the request parameters
		if (GenericValidator.isBlankOrNull(backurl)) {
			try {
				HttpServletRequest request = context.getRequest();
				StringBuffer backurlbuff = new StringBuffer();
				Enumeration<String> paramnames = request.getParameterNames();

				while (paramnames.hasMoreElements()) {
					String name = paramnames.nextElement();
					String value = request.getParameter(name);
					// Only add if value is not empty and
					// name is not 'view' (routing param)
					if (!name.equals("view") && !GenericValidator.isBlankOrNull(value)) {
						backurlbuff.append(name + "=" + value);
						if (paramnames.hasMoreElements())
							backurlbuff.append("&");
					}
				}
				backurl = URLEncoder.encode(request.getRequestURI() + "?" + backurlbuff.toString(), "UTF-8");
				nexturl = backurl; // For login from header
			} catch (UnsupportedEncodingException e) {
				log4j.warn("Unable to build backurl/nexturl: " + e.getMessage());
			}
		} else {
			if (GenericValidator.isBlankOrNull(nexturl)) {
				nexturl = backurl; // For login from header
			}
		}

        // Process the request to create error messages from error codes
    	ControllerRequestHelper.processErrorCode(this);
    	
		if (getLimitError() != null) {
			context.getValidationErrors().add("limitError",
					new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.limitError", getSavedSeachesAndAlertsLimit()));
		}

		if (context.getUserSession().getProperty(UserSession.SHOW_NEWSEARCH_ALERT) != null) {
			shownewsrchalert = context.getUserSession().getProperty(UserSession.SHOW_NEWSEARCH_ALERT);
		}

		if (context.getUserSession().getProperty(UserSession.SHOW_MAX_ALERT) != null) {
			showmaxalert = context.getUserSession().getProperty(UserSession.SHOW_MAX_ALERT);
		}

		if (context.getUserSession().getProperty(UserSession.SHOW_MAX_ALERTCLEAR) != null) {
			showmaxalertclear = context.getUserSession().getProperty(UserSession.SHOW_MAX_ALERTCLEAR);
		}
	}

	/**
	 * Create Google Analytics events to send to page
	 */
    @After(stages = LifecycleStage.EventHandling)
    protected void doGoogleAnalytics() {
        try {
            // Add Google Analytics web events
            webEvent.setCategory(WebAnalyticsEventProperties.CAT_SEARCH_RESULT);
            webEvent.setLabel(getFoundin());
            if(searchWidget){
                addWebEvent(new GoogleWebAnalyticsEvent(WebAnalyticsEventProperties.CAT_SEARCH_WIDGET, getFoundin(),swReferrer));
            }

            String action = this.context.getLogEntry().getLogProperties().getProperty("action");
            if ("search".equals(action)) {
                if ("expert".equalsIgnoreCase(this.searchtype)) {
                    addWebEvent(new GoogleWebAnalyticsEvent(WebAnalyticsEventProperties.CAT_SEARCH_SUBMIT, WebAnalyticsEventProperties.ACTION_EXPERT_SEARCH,
                        getFoundin()));
                } else if ("thesaurus".equalsIgnoreCase(this.searchtype)) {
                    addWebEvent(new GoogleWebAnalyticsEvent(WebAnalyticsEventProperties.CAT_SEARCH_SUBMIT, WebAnalyticsEventProperties.ACTION_THES_SEARCH,
                        getFoundin()));
                } else {
                    addWebEvent(new GoogleWebAnalyticsEvent(WebAnalyticsEventProperties.CAT_SEARCH_SUBMIT, WebAnalyticsEventProperties.ACTION_QUICK_SEARCH,
                        getFoundin()));
                }
            }
    		//if this is from an email alert lets track it
    		if(StringUtils.isNotBlank(emailweek) && "alert".equals(location.toLowerCase())){
    			createWebEvent("Email Alert", emailweek, format);
    		}
        } catch (Throwable t) {
            log4j.warn("Unable to record Google Analytics!", t);
        }
    }

	/**
	 * Add Google Analytics web events to a list from a Query object
	 * @param query
	 * @return
	 */
	protected void createQueryEventList(Query query) {
	    if (query == null) {

	    }
        
        GoogleWebAnalyticsEvent theEvent = null;

        String sDocType = query.getDocumentType();
        if (!GenericValidator.isBlankOrNull(sDocType) && !(sDocType.equals("NO-LIMIT"))) {
            theEvent = new GoogleWebAnalyticsEvent();
            theEvent.setCategory(WebAnalyticsEventProperties.CAT_LIMIT_TO);
            theEvent.setAction(WebAnalyticsEventProperties.ACTION_DOC_TYPE);
            theEvent.setLabel(sDocType);
            theEvent.recordRemoteEvent(context);
        }
        String sTreatmentType = query.getTreatmentType();
        if (!GenericValidator.isBlankOrNull(sTreatmentType) && !(sTreatmentType.equals("NO-LIMIT"))) {
            theEvent = new GoogleWebAnalyticsEvent();
            theEvent.setCategory(WebAnalyticsEventProperties.CAT_LIMIT_TO);
            theEvent.setAction(WebAnalyticsEventProperties.ACTION_TREAT_TYPE);
            theEvent.setLabel(sTreatmentType);
            theEvent.recordRemoteEvent(context);
        }
        String sDisciplineType = query.getDisciplineType();
        if (!GenericValidator.isBlankOrNull(sDisciplineType) && !(sDisciplineType.equals("NO-LIMIT"))) {
            theEvent = new GoogleWebAnalyticsEvent();
            theEvent.setCategory(WebAnalyticsEventProperties.CAT_LIMIT_TO);
            theEvent.setAction(WebAnalyticsEventProperties.ACTION_DISC_TYPE);
            theEvent.setLabel(sDisciplineType);
            theEvent.recordRemoteEvent(context);

        }
        String sLanguage = query.getLanguage();
        if (!GenericValidator.isBlankOrNull(sLanguage) && (!sLanguage.equals("NO-LIMIT"))) {
            theEvent = new GoogleWebAnalyticsEvent();
            theEvent.setCategory(WebAnalyticsEventProperties.CAT_LIMIT_TO);
            theEvent.setAction(WebAnalyticsEventProperties.ACTION_LANG_TYPE);
            theEvent.setLabel(sLanguage);
            theEvent.recordRemoteEvent(context);

        }
        String sLastFourUpdates = query.getLastFourUpdates();
        if (!GenericValidator.isBlankOrNull(sLastFourUpdates)) {
            theEvent = new GoogleWebAnalyticsEvent();
            theEvent.setCategory(WebAnalyticsEventProperties.CAT_LIMIT_TO);
            theEvent.setAction(WebAnalyticsEventProperties.ACTION_UPDATES);
            theEvent.setLabel(sLastFourUpdates);
            theEvent.recordRemoteEvent(context);
        }
        if (Sort.PUB_YEAR_FIELD.equals(query.getSortOption().getSortField())) {
            theEvent = new GoogleWebAnalyticsEvent();
            theEvent.setCategory(WebAnalyticsEventProperties.CAT_SORT_BY);
            theEvent.setAction(WebAnalyticsEventProperties.ACTION_PUB_YEAR);
            theEvent.recordRemoteEvent(context);
        }
        String sAutoStemming = query.getAutoStemming();
        if (!GenericValidator.isBlankOrNull(sAutoStemming) && sAutoStemming.equalsIgnoreCase(Query.OFF)) {
            theEvent = new GoogleWebAnalyticsEvent();
            theEvent.setCategory(WebAnalyticsEventProperties.CAT_SORT_BY);
            theEvent.setAction(WebAnalyticsEventProperties.ACTION_AUTO_STEM_OFF);
            theEvent.recordRemoteEvent(context);
        }

    }

	/**
	 * Quick search results handler
	 *
	 * @return Resolution
	 * @throws EVBaseException
	 *
	 */
	@HandlesEvent("quick")
	@DontValidate
	public Resolution quick() throws EVBaseException {
        // Check for RERUN request param.
        Resolution redir = redirectOnRerun();
        if (redir != null) return redir;

		setRoom(ROOM.search);
		setBasketCount(getUserBasketCount());
		webEvent.setCategory(WebAnalyticsEventProperties.CAT_SEARCH_RESULT);
		webEvent.setAction(WebAnalyticsEventProperties.ACTION_QUICK_SEARCH_RES);
		//
		// If "error" is present, an error has been added from the
		// model layer!
		//
		String error = context.getRequest().getParameter("error");
		if (!GenericValidator.isBlankOrNull(error)) {
			context.getValidationErrors().add("validationError",
					new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.syntaxerror", context.getHelpUrl()));

		}

		// Check for zero results
		if (this.resultscount == 0) {
			return new ForwardResolution("/WEB-INF/pages/customer/results/noresults.jsp");
		}

		// Forward on
		return new ForwardResolution("/WEB-INF/pages/customer/results/results.jsp");
	}

	/**
	 * Quick search results handler
	 *
	 * @return Resolution
	 *
	 */
	@HandlesEvent("expert")
	@DontValidate
	public Resolution expert() throws EVBaseException {
        // Check for RERUN request param.
        Resolution redir = redirectOnRerun();
        if (redir != null) return redir;

        setRoom(ROOM.search);
		setBasketCount(getUserBasketCount());
		webEvent.setCategory(WebAnalyticsEventProperties.CAT_SEARCH_RESULT);
		webEvent.setAction(WebAnalyticsEventProperties.ACTION_EXPERT_SEARCH_RES);
		//
		// If "error" is present, an error has been added from the
		// model layer!
		//
		String error = context.getRequest().getParameter("error");
		if (!GenericValidator.isBlankOrNull(error)) {
			context.getValidationErrors().add("validationError",
					new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.syntaxerror", context.getHelpUrl()));
		}

		// Check for zero results
		if (this.resultscount == 0) {
			return new ForwardResolution("/WEB-INF/pages/customer/results/noresults.jsp");
		}

		// Forward on
		return new ForwardResolution("/WEB-INF/pages/customer/results/results.jsp");
	}

	/**
	 * Thesaurus search results handler
	 *
	 * @return Resolution
	 *
	 */
	@HandlesEvent("thes")
	@DontValidate
	public Resolution thes() throws EVBaseException {
        // Check for RERUN request param.
	    Resolution redir = redirectOnRerun();
	    if (redir != null) return redir;

        setRoom(ROOM.search);
		setBasketCount(getUserBasketCount());
		webEvent.setAction(WebAnalyticsEventProperties.ACTION_THES_SEARCH_RES);
		//
		// If "error" is present, an error has been added from the
		// model layer!
		//
		String error = context.getRequest().getParameter("error");
		if (!GenericValidator.isBlankOrNull(error)) {
			context.getValidationErrors().add("validationError",
					new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.syntaxerror", context.getHelpUrl()));
		}

		// Check for zero results
		if (this.resultscount == 0) {
			return new ForwardResolution("/WEB-INF/pages/customer/results/noresults.jsp");
		}

		// Forward on
		return new ForwardResolution("/WEB-INF/pages/customer/results/results.jsp");
	}

	/**
	 * Dedup search results handler
	 *
	 * @return Resolution
	 *
	 */
	@HandlesEvent("dedup")
	@DontValidate
	public Resolution dedup() throws EVBaseException {
		setRoom(ROOM.search);
		setBasketCount(getUserBasketCount());
		webEvent.setAction(WebAnalyticsEventProperties.ACTION_DEDUP_SEARCH_RES);
		// Check for zero results
		if (this.resultscount == 0) {
			return new ForwardResolution("/WEB-INF/pages/customer/results/noresults.jsp");
		}

		// Forward on
		return new ForwardResolution("/WEB-INF/pages/customer/results/results.jsp");
	}

	/**
	 * Combined search results handler (from search history)
	 *
	 * @return Resolution
	 */
	@HandlesEvent("combine")
	@DontValidate
	public Resolution combine() throws EVBaseException {
		setRoom(ROOM.search);
		setBasketCount(getUserBasketCount());
		webEvent.setAction(WebAnalyticsEventProperties.ACTION_COMBINE_SEARCH_RES);
		// Check for zero results
		if (this.resultscount == 0) {
			return new ForwardResolution("/WEB-INF/pages/customer/results/noresults.jsp");
		}

		// Forward on
		return new ForwardResolution("/WEB-INF/pages/customer/results/results.jsp");
	}

	/**
	 * patentref search results handler
	 *
	 * @return Resolution
	 * @throws UnsupportedEncodingException
	 */
	@HandlesEvent("patentref")
	@DontValidate
	public Resolution patentref() throws EVBaseException {
		setRoom(ROOM.search);
		setBasketCount(getUserBasketCount());
		webEvent.setAction(WebAnalyticsEventProperties.ACTION_PATREF_SEARCH_RES);
		if (patentrefsummary.getPresults().size() > 0) {
			SearchResult patentsr = patentrefsummary.getPresults().get(0);
			setDbname(patentsr.getDoc().getDbname());
			setDocid(patentsr.getDoc().getDocid());

		}
		if (results.size() > 1) {

			results.remove(results.size() - 1);
			for (SearchResult sr : results) {
				try {
					sr.setAbstractlink("<a class=\"externallink\" href=\"/search/doc/abstract.url?patentrefabstract=true&pageType="
							+ this.searchtype.toLowerCase() + "Search&searchtype=" + this.searchtype + "&SEARCHID=" + this.searchid + "&DOCINDEX="
							+ sr.getDoc().getHitindex() + "&database=" + sr.getDoc().getDbid() + "&format=" + this.searchtype.toLowerCase() + "&searchnav="
							+ URLEncoder.encode(context.getRequest().getQueryString(), "UTF-8") + "SearchAbstractFormat&displayPagination=yes&docid="
							+ sr.getDoc().getDocid() + "\">Abstract</a>");

					sr.setDetailedlink("<a class=\"externallink\" href=\"/search/doc/detailed.url?patentrefdetailed=true&pageType="
							+ this.searchtype.toLowerCase() + "Search&searchtype=" + this.searchtype + "&SEARCHID=" + this.searchid + "&DOCINDEX="
							+ sr.getDoc().getHitindex() + "&database=" + sr.getDoc().getDbid() + "&format=" + this.searchtype.toLowerCase() + "&searchnav="
							+ URLEncoder.encode(context.getRequest().getQueryString(), "UTF-8") + "SearchAbstractFormat&displayPagination=yes&docid="
							+ sr.getDoc().getDocid() + "\">Detailed</a>");
				} catch (UnsupportedEncodingException e) {
					throw new SearchException(SystemErrorCodes.PATENT_REF_URL_CREATION_FAILED, "", e);
				}
			}
		}

		//
		// If "error" is present, an error has been added from the
		// model layer!
		//
		String error = context.getRequest().getParameter("error");
		if (!GenericValidator.isBlankOrNull(error)) {
			context.getValidationErrors().add("validationError",
					new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.syntaxerror", context.getHelpUrl()));
		}

		// Forward on
		return new ForwardResolution("/WEB-INF/pages/customer/results/results.jsp");
	}

	/**
	 * otherref search results handler
	 *
	 * @return Resolution
	 */
	@HandlesEvent("otherref")
	@DontValidate
	public Resolution otherref() throws EVBaseException {
		setRoom(ROOM.search);
		setBasketCount(getUserBasketCount());
		webEvent.setAction(WebAnalyticsEventProperties.ACTION_OTHERREF_SEARCH_RES);
		if (patentrefsummary.getPresults().size() > 0) {
			SearchResult patentsr = patentrefsummary.getPresults().get(0);
			setDbname(patentsr.getDoc().getDbname());
			setDocid(patentsr.getDoc().getDocid());
		}

		if (results.size() > 1) {
			results.remove(results.size() - 1);
		}
		//
		// If "error" is present, an error has been added from the
		// model layer!
		//
		String error = context.getRequest().getParameter("error");
		if (!GenericValidator.isBlankOrNull(error)) {
			context.getValidationErrors().add("validationError",
					new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.syntaxerror", context.getHelpUrl()));
		}

		// Forward on
		return new ForwardResolution("/WEB-INF/pages/customer/results/otherrefresults.jsp");
	}

	/**
	 * When the "RERUN" parameter is present, user is doing a navigator action.  Redirect
	 * the request so they aren't left on a form POST!
	 * 
	 * @return RedirectResolution 
	 */
	protected Resolution redirectOnRerun() {
        if (!GenericValidator.isBlankOrNull(context.getRequest().getParameter("RERUN")) &&
            !GenericValidator.isBlankOrNull(getSearchtype()) &&
            !GenericValidator.isBlankOrNull(getSearchid())) {
            return new RedirectResolution(
                "/search/results/" + getSearchtype().toLowerCase() + ".url" +
                "?SEARCHID=" + getSearchid() + 
                "&CID=" + getSearchtype().toLowerCase() + "SearchCitationFormat" +
                "&database=" + getDatabase());
        }
        return null;
	}
	
	protected int getUserBasketCount() throws InfrastructureException {
		DocumentBasket basket;
		if (null != getContext().getUserSession().getID()) {
			basket = new DocumentBasket(getContext().getUserSession().getID());
			return basket.getBasketSize();
		}
		return 0;
	}

	/*
	 * private int getUserPrevSrchBasketCount(String searchid) throws
	 * InfrastructureException{ DocumentBasket basket; if(null !=
	 * getContext().getUserSession().getID()){ basket = new
	 * DocumentBasket(getContext().getUserSession().getID(),searchid); return
	 * basket.getOtherSearchBasketSize();
	 *
	 * } return 0; }
	 */

	public String getXSLPath() {

		if (isEventNoResults() || isEventCombine() || isEventThes() || isEventExpert() || isEventQuick() || isEventTags()) {
			return this.getClass().getResource(RESULTS_ADAPTER).toExternalForm();
		} else if (isEventDedup()) {
			return this.getClass().getResource(DEDUP_RESULTS_ADAPTER).toExternalForm();
		} else if (isEventPatenref() || isEventOtherRef()) {
			return this.getClass().getResource(PATENTREF_RESULTS_ADAPTER).toExternalForm();
		}
		return null;
	}

	public String getXMLPath() {

		if (isEventCombine()) {
			return EVProperties.getJSPPath(JSPPathProperties.COMBINE_SEARCH_RESULTS);
		} else if (isEventThes()) {
			return EVProperties.getJSPPath(JSPPathProperties.THES_SEARCH_RESULTS);
		} else if (isEventExpert()) {
			return EVProperties.getJSPPath(JSPPathProperties.EXPERT_SEARCH_RESULTS);
		} else if (isEventQuick()) {
			return EVProperties.getJSPPath(JSPPathProperties.QUICK_SEARCH_RESULTS);
		} else if (isEventDedup()) {
			return EVProperties.getJSPPath(JSPPathProperties.DEDUP_SEARCH_RESULTS);
		} else if (isEventPatenref()) {
			return EVProperties.getJSPPath(JSPPathProperties.PATENREF_SEARCH_RESULTS);
		} else if (isEventOtherRef()) {
			return EVProperties.getJSPPath(JSPPathProperties.OTHERREF_SEARCH_RESULTS);
		} else if (isEventTags()) {
			return EVProperties.getJSPPath(JSPPathProperties.TAG_SEARCH_RESULTS);
		}
		return "";
	}

	private boolean isEventOtherRef() {
		return "otherref".equals(this.context.getEventName());
	}

	private boolean isEventPatenref() {
		return "patentref".equals(this.context.getEventName());
	}

	private boolean isEventDedup() {
		return "dedup".equals(this.context.getEventName());
	}

	private boolean isEventQuick() {
		return "quick".equals(this.context.getEventName());
	}

	private boolean isEventExpert() {
		return "expert".equals(this.context.getEventName());
	}

	private boolean isEventThes() {
		return "thes".equals(this.context.getEventName());
	}

	private boolean isEventCombine() {
		return "combine".equals(this.context.getEventName());
	}

	private boolean isEventNoResults() {
		return "noresults".equals(this.context.getEventName());
	}

	private boolean isEventTags() {
		return "tags".equals(this.context.getEventName());
	}

	@Override
	public void setTagbubble(TagBubble tagbubble) {
		// No tag bubbles on citation results
		return;
	}

	@Override
	public Resolution handleException(ErrorXml errorXml) {
		if (isEventExpert()) {
			StringBuffer redirect = new StringBuffer();
			if(errorXml.getErrorCode() == SystemErrorCodes.EBOOK_REMOVED) {
				redirect.append("/search/expert.url?CID=expertSearch&database=" + getDatabase() + "&searchid=" + getSearchid() + "&errorCode="+errorXml.getErrorCode());
			} else {
				if (!GenericValidator.isBlankOrNull(getRequest().getParameter("RERUN"))) {
					if (getLastRefineStep() != null && getLastRefineStep().equalsIgnoreCase("0")) {
						redirect.append("/search/results/quick.url?CID=quickSearchCitationFormat&SEARCHID=" + getRequest().getParameter("RERUN"));
						redirect.append("&database=" + getDatabase());
						redirect.append("&errorCode=" + errorXml.getErrorCode());
						if (!GenericValidator.isBlankOrNull(getAppendstr())) redirect.append("&appendstr=" + URLEncoder.encode(getAppendstr()));
					} else {
						redirect.append("/search/results/expert.url?CID=expertSearchCitationFormat&SEARCHID=" + getRequest().getParameter("RERUN"));
						redirect.append("&database=" + getDatabase());
						redirect.append("&STEP="+ getLastRefineStep());
						redirect.append("&errorCode=" + errorXml.getErrorCode());
						if (!GenericValidator.isBlankOrNull(getAppendstr())) redirect.append("&appendstr=" + URLEncoder.encode(getAppendstr()));
					}
				} else {
					redirect.append("/search/expert.url?CID=expertSearch&database=" + getDatabase() + "&searchid=" + getSearchid() + "&errorCode="+errorXml.getErrorCode());
				}
			}
			
			return new RedirectResolution(redirect.toString());
		}
		if (isEventQuick()) {
			if ("Book".equals(searchtype)) {
				return new RedirectResolution("/search/ebook.url?CID=ebookSearch&searchID=" + getSearchid() + "&database=" + getDatabase() + "&errorCode="+errorXml.getErrorCode());
			}else if(errorXml.getErrorCode() == SystemErrorCodes.EBOOK_REMOVED){
				return new RedirectResolution("/search/quick.url?CID=quickSearch&searchID=" + getSearchid() + "&database=" + getDatabase() + "&eerrorCode="+errorXml.getErrorCode());
			}else {
				return new RedirectResolution("/search/quick.url?CID=quickSearch&searchID=" + getSearchid() + "&database=" + getDatabase() + "&errorCode="+errorXml.getErrorCode());
			}
		}

		if (isEventCombine()) {
			String referer = context.getRequest().getHeader("Referer");
			referer = referer.replaceAll("[&\\?]history=t", "").replaceAll("[&\\?]errorCode=[0-9]*", "");
			String qs = "history=t";
			if (errorXml.getErrorCode() == SystemErrorCodes.COMBINE_HISTORY_UNIQUE_DATABASE_ERROR) {
				qs += "&errorCode=" + SystemErrorCodes.COMBINE_HISTORY_UNIQUE_DATABASE_ERROR;
			} else {
				qs += "&errorCode=" + SystemErrorCodes.SEARCH_HISTORY_ERROR;
			}
			if (GenericValidator.isBlankOrNull(referer)) {
				log4j.warn("Error has occurred but Referer request header is not set!  Returning to quick search page...");
				return new RedirectResolution("/search/quick.url?" + qs);
			} else {
				return new RedirectResolution(referer + (referer.contains("?") ? "&" : "?") + qs);
			}
		} else {
			log4j.warn("Unable to handle ErrorXml object, code = " + errorXml.getErrorCode() + "; message = '" + errorXml.getErrorMessage() + "'");
			context.getRequest().setAttribute("errorXml", errorXml);
			return new ForwardResolution("/system/error.url");
		}

		// return "";
	}

	public String getTopappend() {
		return topappend;
	}

	public void setTopappend(String topappend) {
		this.topappend = topappend;
	}

	public String getLastRefineStep() {
		return lastRefineStep;
	}

	public void setLastRefineStep(String lastRefineStep) {
		this.lastRefineStep = lastRefineStep;
	}


	public boolean isSearchWidget() {
		return searchWidget;
	}
	public void setSearchwidget(String searchWidget) {
		if(StringUtils.isNotBlank(searchWidget)){
			this.searchWidget = Boolean.parseBoolean(searchWidget);
		}
	}

	public void setSearchWidget(boolean searchWidget) {
		this.searchWidget = searchWidget;
	}

	public String getSwReferrer() {
		return swReferrer;
	}

	public void setSwReferrer(String swReferrer) {
		this.swReferrer = swReferrer;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEmailweek() {
		return emailweek;
	}

	public void setEmailweek(String emailweek) {
		this.emailweek = emailweek;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
