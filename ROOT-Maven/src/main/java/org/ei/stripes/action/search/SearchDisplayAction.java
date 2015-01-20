package org.ei.stripes.action.search;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StrictBinding;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrorHandler;
import net.sourceforge.stripes.validation.ValidationErrors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.ane.entitlements.UserEntitlement;
import org.ei.ane.entitlements.UserEntitlement.ENTITLEMENT_TYPE;
import org.ei.biz.access.AccessException;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.books.collections.ReferexCollection;
import org.ei.controller.logging.LogEntry;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseConfigException;
import org.ei.domain.HelpLinksCache;
import org.ei.domain.Query;
import org.ei.domain.SearchForm;
import org.ei.domain.SearchValidator;
import org.ei.domain.SearchValidator.SearchValidatorStatus;
import org.ei.domain.SearchValidatorRequest;
import org.ei.domain.Searches;
import org.ei.domain.personalization.SavedSearches;
import org.ei.domain.personalization.SearchHistory;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.gui.ListBoxOption;
import org.ei.query.limiter.ReferexLimiter;
import org.ei.session.SessionID;
import org.ei.session.UserCredentials;
import org.ei.session.UserPreferences;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVPathUrl;
import org.ei.stripes.action.GoogleWebAnalyticsEvent;
import org.ei.stripes.action.WebAnalyticsEventProperties;
import org.ei.stripes.exception.EVExceptionHandler;
import org.ei.stripes.util.DatabaseSelector;
import org.ei.stripes.view.EbookSearchFormItem;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import org.xml.sax.SAXException;

/**
 * This class has a dual purpose. It serves to render the Quick, Expert and eBook search forms and it also handles search submissions from these forms. This is
 * necessary since the Stripes framework use of form objects needs an action bean tied
 *
 * @author harovetm
 *
 */
@UrlBinding("/search/{$event}.url")
@StrictBinding
public class SearchDisplayAction extends BaseSearchAction implements ValidationErrorHandler { // implements
                                                            // IBizBean {

    private final static Logger log4j = Logger.getLogger(SearchDisplayAction.class);

    // Options for the "doctype" sections
    protected List<ListBoxOption> doctypeopts;
    // Options for the "treatmenttype" sections
    protected List<ListBoxOption> treatmenttypeopts;
    // Options for the "disciplinetype" sections
    protected List<ListBoxOption> disciplinetypeopts;
    // Options for the "languagetype" sections
    protected List<ListBoxOption> languageopts;

    // Options for the "search-in" sections
    protected List<ListBoxOption> section1opts;
    protected List<ListBoxOption> section2opts;
    protected List<ListBoxOption> section3opts;

    // Year values string (from user cartridge)
    protected String stringYear = "";

    // Year option strings
    protected List<ListBoxOption> startyearopts;
    protected List<ListBoxOption> endyearopts;

    // eBooks only
    protected String creds = "";
    protected List<String> selcols = new ArrayList<String>();
    protected List<EbookSearchFormItem> ebooklist = new ArrayList<EbookSearchFormItem>();

    // Datbase checkbox list
    protected List<DatabaseSelector.DatabaseCheckbox> databasecheckboxes;
    // Referex collections checkbox list
    protected List<DatabaseSelector.ReferexCheckbox> referexcheckboxes;

    // The history variable
    protected List<SearchHistory> searchhistorylist = new ArrayList<SearchHistory>();
    protected int savedcount;
    protected int alertcount;
    protected List<String> moresourceslinks = null;
    MoreSearchSources moresearchsources = new MoreSearchSources();

    @Validate(trim=true,mask="true|false")
    protected String dberr;
    @Validate(trim=true,mask="hisidnotexists")
    protected String hisiderr;
    @Validate(trim=true,mask="true|false")
    protected String searchHisErr;

    private String swReferrer = "";
    protected GoogleWebAnalyticsEvent webEvent = new GoogleWebAnalyticsEvent();

    /**
     * Preprocess to setup some values @
     *
     * @throws SessionException
     *
     * @throws Exception
     */
    @Before(on = { "quick", "expert", "ebook" })
    protected void preprocess() throws InfrastructureException, SessionException {
        IEVWebUser user = context.getUserSession().getUser();
        HttpServletRequest request = getContext().getRequest();
        UserSession userSession = context.getUserSession();

        webEvent.setCategory(WebAnalyticsEventProperties.CAT_SEARCH_FORM);
        // default to new. PopulateSearchId will override it if need be
        webEvent.setLabel(WebAnalyticsEventProperties.LABEL_NEW_SEARCH);

        if (context.getEventName().equalsIgnoreCase("quick")) {
            webEvent.setAction(WebAnalyticsEventProperties.ACTION_QUICK_SEARCH);
        } else if (context.getEventName().equalsIgnoreCase("expert")) {
            webEvent.setAction(WebAnalyticsEventProperties.ACTION_EXPERT_SEARCH);
        } else if (context.getEventName().equalsIgnoreCase("ebook")) {
            webEvent.setAction(WebAnalyticsEventProperties.ACTION_EBOOK_SEARCH);
        }

        // Get the user's database entitlements as a mask
        usermask = user.getUsermask();

        // Get the user's default database as a mask
        if (GenericValidator.isBlankOrNull(database)) {
            database = Integer.toString(userSession.getUser().getDefaultDB(usermask));
        }

        // TODO change this!
        String[] cartridgearr = context.getUserSession().getUser().getCartridge();
        stringYear = SearchForm.getClientStartYears(context.getUserSession().getUser().getCartridgeString(), cartridgearr);
        //
        // First populate form from Fence values
        //
        populateFromUserPrefs();

        //
        // Initialize form from searchid if available (edit search)
        //
        populateFromSearchID();

        //
        // Add search history items
        //
        SessionID sessionId = context.getUserSession().getSessionID();
        if ((sessionId != null) && (!GenericValidator.isBlankOrNull(sessionId.getID()))) {
            List<Query> queries = Searches.getSessionSearches(sessionId.getID());
            int serialnumber = queries.size();
            for (Query query : queries) {
                SearchHistory searchhistory = new SearchHistory(query);
                searchhistory.setSerialnumber(Integer.toString(serialnumber--));
                searchhistorylist.add(searchhistory);
            }
        }

        //
        // Add alert and saved search counts
        //
        String userId = user.getUserId();
        Map counts = SavedSearches.getCounts(userId);
        if (!GenericValidator.isBlankOrNull((String) counts.get("ECOUNT"))) {
            this.alertcount = Integer.parseInt((String) counts.get("ECOUNT"));
        }
        if (!GenericValidator.isBlankOrNull((String) counts.get("SCOUNT"))) {
            this.savedcount = Integer.parseInt((String) counts.get("SCOUNT"));
        }

        //
        // If "errorquery" is present, the SearchSubmitAction bean should
        // have added a Query object into the session that represents form
        // state for the error. Attempt to retrieve it and use to
        // re-display search criteria
        //
        HttpSession session = context.getRequest().getSession(false);
        Query query = (Query) session.getAttribute("errorquery");
        if (query != null) {
            populateSearchFormFields(query);
            session.removeAttribute("errorquery");
            context.getValidationErrors().add("validationError",
                new LocalizableError("org.ei.stripes.action.search.BaseSearchAction.syntaxerror", context.getHelpUrl()));

        }

        setSearchHistoryErrors();
        // reset results per page.
        userSession.setRecordsPerPage(Integer.toString(userSession.getUser().getUserPrefs().getResultsPerPage()));
        context.updateUserSession(userSession);
        // set the web analytics event on the request for the jsp to injest.
        addWebEvent(webEvent);
        // Common to all search forms - set the More Search Sources contents
        moresourceslinks = moresearchsources.getMoreSearchSources(user.getCartridge(), sessionid, context);

    }

    private StopWatch beanstopwatch = null;

    @Before(on = "submit")
    private void startPerformance() {
        this.beanstopwatch = new Log4JStopWatch();
    }

    @After(on = "submit")
    private void stopPerformance() {
        if (this.beanstopwatch != null && this.context.getEventName() != null) {
            this.beanstopwatch.stop("SEARCH_SUBMIT");
        }
    }

    /**
     * Try to initialize action bean from searchid on request @
     *
     * @throws InfrastructureException
     */
    protected void populateFromSearchID() throws InfrastructureException {
        if (!GenericValidator.isBlankOrNull(searchid)) {
            Query queryObject;
            queryObject = Searches.getSearch(searchid);
            if (queryObject != null) {
                populateSearchFormFields(queryObject);
                // change the webevent to an edit type instead of new
                webEvent.setLabel(WebAnalyticsEventProperties.LABEL_EDIT_SEARCH);
            }
        }

    }

    /**
     * Try to initialize action bean from fence values (A&E)
     */
    protected void populateFromFences() {
        // Set sort and autostemming from fence values
        UserPreferences userprefs = context.getUserSession().getUser().getUserPreferences();
        if (userprefs.isRelevanceSort()) {
            this.sort = "relevance";
        } else {
            this.sort = "yr";
        }

    }

    protected void populateFromUserPrefs() {
        this.sort = context.getUserSession().getUser().getUserPrefs().getSort();
    }

    /**
     * Try to initialize the database value. Order is:
     *
     * 1) Set to "Referex" id if on eBook 2) If "alldb" is on the request, collect all db IDs
     */
    protected int initDatabase() {
        HttpServletRequest request = context.getRequest();
        IEVWebUser user = context.getUserSession().getUser();
        int idatabase = 0; // user.getUserPreferences().getDefaultDB();

        // Try to initialize default database from request
        // String event = context.getEventName();
        if (!GenericValidator.isBlankOrNull(alldb)) {
            try {
                idatabase = Integer.parseInt(alldb);
            } catch (NumberFormatException e) {
                log4j.warn("Invalid database format (alldb): " + alldb);
            }
        } else if (!GenericValidator.isBlankOrNull(database)) {
            try {
                idatabase = Integer.parseInt(database);
            } catch (NumberFormatException e) {
                log4j.warn("Invalid database format (database): " + database);
            }
            if (idatabase == 0) {
                String[] dbs = request.getParameterValues("database");
                for (int i = 0; dbs != null && i < dbs.length; i++) {
                    if ("Compendex".equalsIgnoreCase(dbs[i])) {
                        idatabase += 1;
                    } else if ("Inspec".equalsIgnoreCase(dbs[i])) {
                        idatabase += 2;
                    } else if ("Com".equalsIgnoreCase(dbs[i]) || "Combined".equalsIgnoreCase(dbs[i])) {
                        idatabase += 3;
                    }
                }
            }
        }

        DatabaseConfig dbconfig = DatabaseConfig.getInstance();
        // If default database still 0, init from fences. NOTE: only add when
        // fence is
        // ON *and* user is entitled!
        if (idatabase == 0) {
            Set<UserEntitlement> entitlements = context.getUserSession().filterUserEntitlements(ENTITLEMENT_TYPE.DATABASE);
            if (entitlements.size() == 1
                || (entitlements.size() == 2 && entitlements.contains(UserEntitlement.CPX_ENTITLEMENT) && entitlements
                    .contains(UserEntitlement.C84_ENTITLEMENT))
                || (entitlements.size() == 2 && entitlements.contains(UserEntitlement.INS_ENTITLEMENT) && entitlements
                    .contains(UserEntitlement.IBF_ENTITLEMENT))) {
                idatabase = ((UserEntitlement) entitlements.toArray()[0]).getMask();
            }
        }

        database = String.valueOf(idatabase);

        return idatabase;
    }


    /**
     * Search submit
     *
     * @return Resolution
     * @throws SessionException
     * @throws InfrastructureException
     * @throws Exception
     * @throws ServletException
     * @throws HistoryException
     * @throws IOException
     * @throws SearchException
     */
    @HandlesEvent("submit")
    public Resolution validate() throws InfrastructureException, SessionException {

    	HttpServletRequest request = context.getRequest();
        UserSession usersession = context.getUserSession();
        Query queryObject = null;

        //
        // Stripes will remove any empty text values from the form submission
        // but we do NOT want this behavior!! Get them directly from the
        // request.
        //
        searchWords = request.getParameterValues("searchWords");

        //
        // Parm 'searchtype' is required!
        //
        if (GenericValidator.isBlankOrNull(searchtype)) {
            log4j.error("'searchtype' not set!");
            throw new InfrastructureException(SystemErrorCodes.INVALID_ARGUMENT_SET_ERROR, "'searchtype' must be present!");
        }

        //
        // Get the database value by combining selected checkboxes
        //
        if (!GenericValidator.isBlankOrNull(alldb)) {
            database = alldb;
        } else {
            int sumDb = 0;
            String[] dbs = request.getParameterValues("database");
            if (dbs == null) {
                throw new InfrastructureException(SystemErrorCodes.INVALID_ARGUMENT_SET_ERROR, "No database selected!");
            } else {
                for (String db : dbs) {
                    sumDb += Integer.parseInt(db);
                }
                database = String.valueOf(sumDb);

                for (int i = 0; i < dbs.length; i++) {
                    if (Integer.parseInt(dbs[i].trim()) != DatabaseConfig.UPA_MASK && Integer.parseInt(dbs[i].trim()) != DatabaseConfig.EUP_MASK
                        && Integer.parseInt(dbs[i].trim()) != DatabaseConfig.EPT_MASK) {
                        showpatentshelp = false;
                        break;
                    }
                    if (Integer.parseInt(dbs[i].trim()) == DatabaseConfig.UPA_MASK || Integer.parseInt(dbs[i].trim()) == DatabaseConfig.EUP_MASK
                        || Integer.parseInt(dbs[i].trim()) == DatabaseConfig.EPT_MASK) {
                        showpatentshelp = true;
                    }
                }
            }
        }

        //
        // Create a return URL - default quick search
        //

        //
        // Use SearchValidator to validate incoming search
        //
        SearchValidatorRequest validatorrequest = new SearchValidatorRequest();
        validatorrequest.setCartridge(usersession.getCartridge());
        validatorrequest.setDatabasemask(Integer.parseInt(database));
        validatorrequest.setSearchform(this);
        validatorrequest.setSearchtype(this.searchtype);
        validatorrequest.setSessionid(usersession.getSessionid());
        validatorrequest.setUserid(usersession.getUserid());

        SearchValidator searchvalidator = new SearchValidator();
        SearchValidatorStatus status = searchvalidator.validate(validatorrequest);

        if (status != SearchValidatorStatus.SUCCESS) {
            String searchformURL = EVPathUrl.EV_QUICK_SEARCH.value() + "?CID=quickSearch&database=" + database + "&showpatentshelp=" + showpatentshelp;
            if (Query.TYPE_BOOK.equals(searchtype)) {
                searchformURL = EVPathUrl.EV_EBOOK_SEARCH.value() + "?CID=ebookSearch&database=" + database + "&showpatentshelp=" + showpatentshelp;
            } else if (Query.TYPE_EXPERT.equals(searchtype)) {
                searchformURL = EVPathUrl.EV_EXPERT_SEARCH.value() + "?CID=expertSearch&database=" + database + "&showpatentshelp=" + showpatentshelp;
            } else if (Query.TYPE_THESAURUS.equals(searchtype)) {
                searchformURL = EVPathUrl.EV_THES_SEARCH.value() + "?CID=thesHome&database=" + database + "&showpatentshelp=" + showpatentshelp + "#init";
            }
            // Add query object to session to re-populate search form
            queryObject = searchvalidator.getQueryObject();
            // Add the Query object to session to be used by the
            // SearchDisplayAction
            if (queryObject != null) {
                context.getRequest().getSession(false).setAttribute("errorquery", queryObject);
            }
            return (new RedirectResolution(searchformURL));
        }

        //
        // Validate the search. NOTE: the actual search is run either
        // quickSearchResults.jsp or expertSearchResults.jsp
        //
        queryObject = searchvalidator.getQueryObject();
        Searches.saveSearch(queryObject);

        //
        // For the @After code, we need to set the searchid
        //
        this.searchid = queryObject.getID();

        //
        // Create a results URL - default quick search
        //
        String resultsURL = "/search/results/quick.url?CID=quickSearchCitationFormat" + "&database=" + database + "&SEARCHID=" + queryObject.getID()
            + "&intialSearch=true" + "&showpatentshelp=" + showpatentshelp;
        if (Query.TYPE_EXPERT.equals(searchtype)) {
            resultsURL = "/search/results/expert.url?CID=expertSearchCitationFormat" + "&database=" + database + "&SEARCHID=" + queryObject.getID()
                + "&intialSearch=true" + "&showpatentshelp=" + showpatentshelp;
        } else if (Query.TYPE_THESAURUS.equals(searchtype)) {
            resultsURL = "/search/results/thes.url?CID=thesSearchCitationFormat" + "&database=" + database + "&SEARCHID=" + queryObject.getID()
                + "&intialSearch=true" + "&showpatentshelp=" + showpatentshelp;
        }

        // All good! Redirect to results page
        return new RedirectResolution(resultsURL);
    }

    /**
     * Widget Search submit
     *
     * @return Resolution
     * @throws ServletException
     * @throws HistoryException
     * @throws IOException
     * @throws SearchException
     */
    @HandlesEvent("widgetSubmit")
    public Resolution widgetSearchSubmit() throws InfrastructureException {

        HttpServletRequest request = context.getRequest();
        UserSession usersession = context.getUserSession();
        Query queryObject = null;

        usermask = context.getUserSession().getUser().getUsermask();

        populateFromUserPrefs();
        //
        // Stripes will remove any empty text values from the form submission
        // but we do NOT want this behavior!! Get them directly from the
        // request.
        //
        searchWords = request.getParameterValues("searchWords");

        //
        // Parm 'searchtype' is required!
        //
        if (GenericValidator.isBlankOrNull(searchtype)) {
            log4j.error("'searchtype' not set!");
            throw new InfrastructureException(SystemErrorCodes.INVALID_ARGUMENT_SET_ERROR, "'searchtype' must be present!");
        }

        if (GenericValidator.isBlankOrNull(database)) {
            int sumDb = 0;
            String[] dbs = null;

            List<String> defaultDB = getDefaultDBAsList();
            if (!defaultDB.isEmpty()) {
                dbs = defaultDB.toArray(new String[defaultDB.size()]);
            } else {
                dbs = buildDatabaselistOnEntitlements(usersession);
            }
            if (dbs != null && dbs.length != 0) {
                for (String db : dbs) {
                    sumDb += Integer.parseInt(db);
                }
                database = String.valueOf(sumDb);
            }
        }

        if (GenericValidator.isBlankOrNull(database)) {
            throw new InfrastructureException(SystemErrorCodes.INVALID_ARGUMENT_SET_ERROR, "No database selected!");
        } else {

            Database[] dbArray = DatabaseConfig.getInstance().getDatabases(Integer.parseInt(database));

            for (int i = 0; i < dbArray.length; i++) {
                if (dbArray[i].getMask() != DatabaseConfig.UPA_MASK && dbArray[i].getMask() != DatabaseConfig.EUP_MASK
                    && dbArray[i].getMask() != DatabaseConfig.EPT_MASK) {
                    showpatentshelp = false;
                    break;
                }
                if (dbArray[i].getMask() == DatabaseConfig.UPA_MASK || dbArray[i].getMask() == DatabaseConfig.EUP_MASK
                    || dbArray[i].getMask() == DatabaseConfig.EPT_MASK) {
                    showpatentshelp = true;
                }
            }
        }

        //
        // Create a return URL - default quick search
        //

        //
        // Use SearchValidator to validate incoming search
        //
        SearchValidatorRequest validatorrequest = new SearchValidatorRequest();
        validatorrequest.setCartridge(usersession.getCartridge());
        validatorrequest.setDatabasemask(Integer.parseInt(database));
        validatorrequest.setSearchform(this);
        validatorrequest.setSearchtype(this.searchtype);
        validatorrequest.setSessionid(usersession.getSessionid());
        validatorrequest.setUserid(usersession.getUserid());

        SearchValidator searchvalidator = new SearchValidator();
        SearchValidatorStatus status = searchvalidator.validate(validatorrequest);

        if (status != SearchValidatorStatus.SUCCESS) {
            String searchformURL = EVPathUrl.EV_QUICK_SEARCH.value() + "?CID=quickSearch&database=" + database + "&showpatentshelp=" + showpatentshelp;
            if (Query.TYPE_BOOK.equals(searchtype)) {
                searchformURL = EVPathUrl.EV_EBOOK_SEARCH.value() + "?CID=ebookSearch&database=" + database + "&showpatentshelp=" + showpatentshelp;
            } else if (Query.TYPE_EXPERT.equals(searchtype)) {
                searchformURL = EVPathUrl.EV_EXPERT_SEARCH.value() + "?CID=expertSearch&database=" + database + "&showpatentshelp=" + showpatentshelp;
            } else if (Query.TYPE_THESAURUS.equals(searchtype)) {
                searchformURL = EVPathUrl.EV_THES_SEARCH.value() + "?CID=thesHome&database=" + database + "&showpatentshelp=" + showpatentshelp + "#init";
            }
            // Add query object to session to re-populate search form
            queryObject = searchvalidator.getQueryObject();
            // Add the Query object to session to be used by the
            // SearchDisplayAction
            if (queryObject != null) {
                context.getRequest().getSession(false).setAttribute("errorquery", queryObject);
            }
            return (new RedirectResolution(searchformURL));
        }

        //
        // Validate the search. NOTE: the actual search is run either
        // quickSearchResults.jsp or expertSearchResults.jsp
        //
        queryObject = searchvalidator.getQueryObject();
        Searches.saveSearch(queryObject);

        //
        // For the @After code, we need to set the searchid
        //
        this.searchid = queryObject.getID();

        //
        // Create a results URL - default quick search
        //
        String resultsURL = "/search/results/quick.url?CID=quickSearchCitationFormat" + "&database=" + database + "&SEARCHID=" + queryObject.getID()
            + "&intialSearch=true" + "&showpatentshelp=" + showpatentshelp + "&searchwidget=true&swReferrer=" + swReferrer;
        if (Query.TYPE_EXPERT.equals(searchtype)) {
            resultsURL = "/search/results/expert.url?CID=expertSearchCitationFormat" + "&database=" + database + "&SEARCHID=" + queryObject.getID()
                + "&intialSearch=true" + "&showpatentshelp=" + showpatentshelp + "&searchwidget=true&swReferrer=" + swReferrer;
        } else if (Query.TYPE_THESAURUS.equals(searchtype)) {
            resultsURL = "/search/results/thes.url?CID=thesSearchCitationFormat" + "&database=" + database + "&SEARCHID=" + queryObject.getID()
                + "&intialSearch=true" + "&showpatentshelp=" + showpatentshelp + "&searchwidget=true&swReferrer=" + swReferrer;
        }

        // All good! Redirect to results page
        return new RedirectResolution(resultsURL);
    }

    private List<String> getDatabasesfromRequestForm(HttpServletRequest request) {
        String[] databases = request.getParameterValues("database");
        List<String> databaseList = new ArrayList<String>();
        if (null != databases) {
            for (String database : databases) {
                databaseList.add(Integer.toString(DatabaseConfig.getInstance().getMask(database)));

            }
        }
        return databaseList;
    }

    private List<String> getDefaultDBAsList() {
        return context.getUserSession().getUser().getDefaultDBAsList(usermask);
    }

    private String[] buildDatabaselistOnEntitlements(UserSession usersession) {
        String[] dbs;
        List<String> databases = new ArrayList<String>();

        for (UserEntitlement entitlement : usersession.getUserEntitlements()) {
            if (ENTITLEMENT_TYPE.DATABASE == entitlement.getEntitlementType()) {
                databases.add(String.valueOf(entitlement.getMask()));
            }
        }

        if (usersession.getUser().getUserPreferences().getBoolean(UserPreferences.FENCE_EBOOK)) {
            String keys = usersession.getUser().getCartridgeString();
            if (ReferexCollection.ALLCOLS_PATTERN.matcher(keys).find()) {
                databases.add(String.valueOf(DatabaseConfig.PAG_MASK));
            }
        }
        dbs = databases.toArray(new String[databases.size()]);
        return dbs;
    }

    /**
     * Quick search display TODO get customized values!!!! (see SearchParameters.jsp)
     *
     * @return Resolution
     * @throws ServletException
     * @throws HistoryException
     * @throws IOException
     */
    @HandlesEvent("quick")
    public Resolution quicksearch() throws InfrastructureException {
        setRoom(ROOM.search);

        // Set up to get form information
        int db = initDatabase();

        // Get the database selection checkboxes
        DatabaseSelector dbselect = new DatabaseSelector();
        try {
            databasecheckboxes = dbselect.getDatabaseCheckboxes(usermask, db);
        } catch (DatabaseConfigException e) {
            log4j.error("Unable to get checkboxes!!", e);
        }

        // Get the search-in options
        section1opts = SearchForm.getOptions(section1, db, "section");
        section2opts = SearchForm.getOptions(section2, db, "section");
        section3opts = SearchForm.getOptions(section3, db, "section");

        // Get the doc type options (LIMIT BY). This is only set for specific
        // database values!
        if ((usermask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK || (usermask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK
            || (usermask & DatabaseConfig.EUP_MASK) == DatabaseConfig.EUP_MASK || (usermask & DatabaseConfig.UPA_MASK) == DatabaseConfig.UPA_MASK
            || (usermask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK || (usermask & DatabaseConfig.IBS_MASK) == DatabaseConfig.IBS_MASK) {
            doctypeopts = SearchForm.getOptions(doctype, db, "doctype");
        }

        // Get the treatment type options (LIMIT BY). This is only set for
        // specific
        // database values!
        if ((usermask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK || (usermask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
            treatmenttypeopts = SearchForm.getOptions(treatmentType, db, "treattype");
        }

        // Get the discipline type options (LIMIT BY). This is only set for
        // specific
        // database values!
        if ((usermask & DatabaseConfig.IBS_MASK) == DatabaseConfig.IBS_MASK || (usermask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
            disciplinetypeopts = SearchForm.getOptions(disciplinetype, db, "discipline");
        }

        // Always output language options (LIMIT BY)
        languageopts = SearchForm.getOptions(language, db, "language");

        // Get year options
        startyearopts = SearchForm.getYears(db, startYear, stringYear, "startYear");
        if (GenericValidator.isBlankOrNull(endYear)) {
            endYear = Integer.toString(SearchForm.calEndYear(db));
        }
        endyearopts = SearchForm.getYears(db, endYear, stringYear, "endYear");

        // TMH - change per Product management! Autostem fence applies ONLY to
        // quick search!
        if (GenericValidator.isBlankOrNull(this.autostem)) {
            if (context.getUserSession().getUser().getUserPreferences().isAutostemming()) {
                this.autostem = "false";
            } else {
                this.autostem = "true";
            }
        }

        // Forward on
        return new ForwardResolution("/WEB-INF/pages/customer/search/quickSearch.jsp");
    }

    /**
     * Expert search display
     *
     * @return Resolution
     * @throws SAXException
     * @throws IOException
     * @throws ServletException
     * @throws HistoryException
     */
    @HandlesEvent("expert")
    @DontValidate
    public Resolution expertsearch() throws InfrastructureException {
        setRoom(ROOM.search);

        // Get the database selection checkboxes
        int db = initDatabase();
        DatabaseSelector dbselect = new DatabaseSelector();
        try {
            databasecheckboxes = dbselect.getDatabaseCheckboxes(usermask, db);
        } catch (DatabaseConfigException e) {
            log4j.error("Unable to get checkboxes!!", e);
        }

        // Get year options
        startyearopts = SearchForm.getYears(db, startYear, stringYear, "startYear");
        if (GenericValidator.isBlankOrNull(this.endYear)) {
            this.endYear = Integer.toString(SearchForm.calEndYear(db));
        }
        endyearopts = SearchForm.getYears(db, this.endYear, stringYear, "endYear");

        // TMH - change per Product management! Autostem fence applies ONLY to
        // quick search!
        if (GenericValidator.isBlankOrNull(this.autostem)) {
            this.autostem = "true";
        }

        return new ForwardResolution("/WEB-INF/pages/customer/search/expertSearch.jsp");
    }

    /**
     * eBook search display
     *
     * @return Resolution
     * @throws HistoryException
     * @throws AccessException
     * @throws ServletException
     */
    @HandlesEvent("ebook")
    @DontValidate
    public Resolution ebooksearch() throws InfrastructureException {

        // Ensure user has access to ebook form. Normally this
        // would be done in an IAccessControl implementation but
        // the events are co-mingled here making it difficult
        IEVWebUser user = context.getUserSession().getUser();
        if (!isEbookEnabled()) {
            log4j.warn("Attempt to access eBook after removal!");
            context.getValidationErrors().add("validationError", new LocalizableError("org.ei.stripes.action.search.SearchDisplayAction.ebookremoval"));
            return quicksearch();
        }

        setRoom(ROOM.search);
        // This must be set for the HelpLinksCache lookup in the header!
        if (GenericValidator.isBlankOrNull(this.CID)) {
            this.CID = HelpLinksCache.KEYS.ebookSearch.toString();
        }

        // Get Referex selected columns if present
        Query query = Searches.getSearch(searchid);
        if (query != null && query.getReferexCollections() != null) {
            ReferexLimiter limiter = (ReferexLimiter) query.getReferexCollections();
            for (ReferexCollection col : limiter.getSelectedCollections()) {
                addSelcol(col.getAbbrev());
            }
        }

        // Get the database selection checkboxes
        DatabaseSelector dbselect = new DatabaseSelector();
        referexcheckboxes = dbselect.getReferexCheckboxes(user.getCartridgeString(), selcols.toString());

        // Set the database to Referex
        setDatabase(Integer.toString(DatabaseConfig.PAG_MASK));

        // Get the BROWSE collection
        populateReferexBrowse("119", user.getCartridge());

        // Get the search-in options
        int database = Integer.parseInt(getDatabase());
        section1opts = SearchForm.getOptions(section1, database, "section");
        section3opts = SearchForm.getOptions(section3, database, "section");
        section2opts = SearchForm.getOptions(section2, database, "section");

        return new ForwardResolution("/WEB-INF/pages/customer/search/ebookSearch.jsp");
    }

    /**
     * Parse the Referex browse categories
     */
    private void populateReferexBrowse(String bstate, String[] creds) {
        // Sort the credentials and search form perpetual setting
        Arrays.sort(creds);
        boolean perpetual = (Arrays.binarySearch(creds, "BPE") >= 0);

        //
        // Create some XML and transform it. This really should be converted
        // to a non-XML solution but the LibraryVisitor code for Referex is
        // pretty intense and I'm pretty lazy... :) TMH
        //
        StringWriter output = new StringWriter();
        org.ei.books.library.Library library;
        try {
            // Create XML into output StringWriter
            library = org.ei.books.library.Library.getInstance();
            org.ei.books.library.LibraryVisitor libraryVisitor = null;
            libraryVisitor = new org.ei.books.library.LibraryVisitor(creds, perpetual);
            library.accept(libraryVisitor);
            libraryVisitor.toXML(output, bstate);

            // Transform!
            SAXBuilder builder = new SAXBuilder();
            String xml = "<E-BOOK>" + output.toString() + "</E-BOOK>";
            Document doc = builder.build(new ByteArrayInputStream(xml.getBytes()));
            Element rootnode = doc.getRootElement();
            List<Element> children = rootnode.getChildren();
            for (Element ele : children) {
                EbookSearchFormItem ebookitem = new EbookSearchFormItem();
                String tmp = ele.getChildText("DISPLAYNAME");
                if (!GenericValidator.isBlankOrNull(tmp))
                    ebookitem.setDisplayname(tmp);
                tmp = ele.getChildText("SHORTNAME");
                if (!GenericValidator.isBlankOrNull(tmp))
                    ebookitem.setShortname(tmp);
                tmp = ele.getChildText("SUBCOUNT");
                if (!GenericValidator.isBlankOrNull(tmp))
                    ebookitem.setSubcount(Integer.parseInt(tmp));
                for (Element link : ((List<Element>) ele.getChild("CVS").getChildren("CV"))) {
                    ebookitem.addSearchlink(link.getText());
                }
                addEbookitem(ebookitem);
            }
        } catch (Exception e) {
            EVExceptionHandler.logException("Unable to parse Referex Browse items!", e, null, log4j);
            return;
        } finally {
            try {
                output.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Look for Search History errors on request and output appropriate message
     */
    protected void setSearchHistoryErrors() {

        if (null != getDberr()) {
            context.getValidationErrors().add("searchHistoryError",
                new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.dbMismatchSearchHistoryError"));
        }
        if (null != getHisiderr()) {
            context.getValidationErrors().add("searchHistoryError",
                new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.HisIdNotExistsError", getSearchHistoryList().size()));
        }
        if (null != getSearchHisErr()) {
            context.getValidationErrors().add("searchHistoryError", new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.seachHistError"));
        }
        String errorCode = getErrorCode();
        if (!GenericValidator.isBlankOrNull(errorCode)) {
            if (errorCode.equalsIgnoreCase(Integer.toString(SystemErrorCodes.EBOOK_REMOVED))) {
                context.getValidationErrors().add("validationError", new LocalizableError("org.ei.stripes.action.search.SearchDisplayAction.ebookremoval"));
            } else if (errorCode.equalsIgnoreCase(Integer.toString(SystemErrorCodes.SEARCH_NOT_FOUND))) {
                context.getValidationErrors().add("validationError", new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.SearchNotFound"));
            } else if (errorCode.equalsIgnoreCase(Integer.toString(SystemErrorCodes.SAVED_SEARCH_NOT_FOUND))) {
                context.getValidationErrors().add("validationError", new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.SavedSearchNotFound"));
            } else {
                context.getValidationErrors().add("validationError", new LocalizableError("org.ei.stripes.action.search.SearchResultsAction.unknownerror"));
            }
        }
    }

    /**
     * Generic check to see if Thesaurus entitlements are enabled
     *
     * @return
     */
    public boolean isThesaurusEnabled() {
        IEVWebUser user = context.getUserSession().getUser();

        // Check for correct credentials to access Thesaurus search
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        if (!UserCredentials.hasCredentials(DatabaseConfig.CPX_MASK, user.getUsermask())
            && !UserCredentials.hasCredentials(DatabaseConfig.INS_MASK, user.getUsermask())
            && !UserCredentials.hasCredentials(DatabaseConfig.GEO_MASK, user.getUsermask())
            && !UserCredentials.hasCredentials(DatabaseConfig.GRF_MASK, user.getUsermask())
            && !UserCredentials.hasCredentials(DatabaseConfig.EPT_MASK, user.getUsermask())) {
            return false;
        }
        return true;
    }

    /**
     * Generic check to see if Thesaurus entitlements are enabled
     *
     * @return
     */
    public boolean isEbookEnabled() {
        IEVWebUser user = context.getUserSession().getUser();

        // Check for correct credentials to access Thesaurus search
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        if (!UserCredentials.hasCredentials(DatabaseConfig.PAG_MASK, user.getUsermask())) {
            return false;
        }
        return true;
    }

    /**
     * Log search form usage metadata
     */
    @After
    private void doUsageLogging() {
        LogEntry logentry = context.getLogEntry();
        if (StringUtils.isNotBlank(this.searchid)) {
            if (logentry == null) {
                throw new IllegalStateException("LogEntry object is empty!");
            }
            Properties logproperties = logentry.getLogProperties();
            logproperties.put("QUERY_STRING", " ");
            logproperties.put("CONTEXT", "search");
            logproperties.put("ACTION", "prepare");
            if ("expert".equalsIgnoreCase(context.getEventName())) {
                logproperties.put("TYPE", "expert_search");
            } else {
                logproperties.put("TYPE", "basic_search");
            }

            try {
                logproperties.put("DB_NAME", this.database == null ? "" : this.database);
            } catch (Throwable t) {
                log4j.error("Unable to log database for SearchDisplayAction!");
            }

            logproperties.put("SORT_BY", this.sort == null ? "" : this.sort);
            logproperties.put("LIMIT_DOCUMENT", this.doctype == null ? "" : this.doctype);
            logproperties.put("LIMIT_TREATMENT", this.treatmentType == null ? "" : this.treatmentType);
            logproperties.put("LIMIT_LANGUAGE", this.language == null ? "" : this.language);
            logproperties.put("LIMIT_YEAR", this.yearselect == null ? "" : this.yearselect);
            logproperties.put("START_YEAR", this.endYear == null ? "" : this.endYear);
            logproperties.put("END_YEAR", this.endYear == null ? "" : this.endYear);
            logproperties.put("NUM_RECS", "0");
            logproperties.put("DOC_INDEX", "0");
            logproperties.put("HIT_COUNT", "0");
            logproperties.put("USE_TYPE", getUseType());
            logentry.setLogProperties(logproperties);
        }
    }

    //
    //
    // GETTERS/SETTERS
    //
    //

    public List<DatabaseSelector.DatabaseCheckbox> getDatabaseCheckboxes() {
        return databasecheckboxes;
    }

    public List<DatabaseSelector.ReferexCheckbox> getReferexCheckboxes() {
        return referexcheckboxes;
    }

    public List<ListBoxOption> getSection1opts() {
        return section1opts;
    }

    public List<ListBoxOption> getSection2opts() {
        return section2opts;
    }

    public List<ListBoxOption> getSection3opts() {
        return section3opts;
    }

    public List<EbookSearchFormItem> getEbooklist() {
        return ebooklist;
    }

    public void setEbooklist(List<EbookSearchFormItem> ebooklist) {
        this.ebooklist = ebooklist;
    }

    public void addEbookitem(EbookSearchFormItem item) {
        this.ebooklist.add(item);
    }

    public int getAlldbvalue() {
        return getScrubbedMask(usermask);
    }

    public List<ListBoxOption> getDoctypeopts() {
        return doctypeopts;
    }

    public List<ListBoxOption> getTreatmenttypeopts() {
        return treatmenttypeopts;
    }

    public List<ListBoxOption> getDisciplinetypeopts() {
        return disciplinetypeopts;
    }

    public List<ListBoxOption> getLanguageopts() {
        return languageopts;
    }

    public List<ListBoxOption> getStartyearopts() {
        return startyearopts;
    }

    public void setStartyearopts(List<ListBoxOption> startyearopts) {
        this.startyearopts = startyearopts;
    }

    public List<ListBoxOption> getEndyearopts() {
        return endyearopts;
    }

    public List<SearchHistory> getSearchHistoryList() {
        return searchhistorylist;
    }

    public String getCreds() {
        return creds;
    }

    public void setCreds(String creds) {
        this.creds = creds;
    }

    public List<String> getSelcols() {
        return selcols;
    }

    public void addSelcol(String selcol) {
        if (this.selcols == null)
            this.selcols = new ArrayList<String>();
        this.selcols.add(selcol);
    }

    public String getStringYear() {
        return stringYear;
    }

    public void setStringYear(String stryear) {
        this.stringYear = stryear;
    }

    public int getusermask() {
        return usermask;
    }

    public void setusermask(int usermask) {
        this.usermask = usermask;
    }

    public int getSavedcount() {
        return savedcount;
    }

    public void setSavedcount(int savedcount) {
        this.savedcount = savedcount;
    }

    public int getAlertcount() {
        return alertcount;
    }

    public void setAlertcount(int alertcount) {
        this.alertcount = alertcount;
    }

    public List<String> getMoresourceslinks() {
        return moresourceslinks;
    }

    public void setMoresourceslinks(List<String> moresourceslinks) {
        this.moresourceslinks = moresourceslinks;
    }

    public String getDberr() {
        return dberr;
    }

    public void setDberr(String dberr) {
        this.dberr = dberr;
    }

    public String getHisiderr() {
        return hisiderr;
    }

    public void setHisiderr(String hisiderr) {
        this.hisiderr = hisiderr;
    }

    public String getSearchHisErr() {
        return searchHisErr;
    }

    public void setSearchHisErr(String searchHisErr) {
        this.searchHisErr = searchHisErr;
    }

    public String getSwReferrer() {
        return swReferrer;
    }

    public void setSwReferrer(String swReferrer) {
        this.swReferrer = swReferrer;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.stripes.validation.ValidationErrorHandler#handleValidationErrors(net.sourceforge.stripes.validation.ValidationErrors)
     */
    @Override
    public Resolution handleValidationErrors(ValidationErrors errors) throws InfrastructureException, SessionException  {
        if (errors != null) {
            preprocess();
            if ("Quick".equals(this.searchtype)) {
                return quicksearch();
            } else if ("Expert".equals(this.searchtype)) {
                return expertsearch();
            } else {
                return quicksearch();
            }
        }
        return null;
    }

}
