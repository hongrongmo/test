package org.ei.stripes.action.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.access.AccessException;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Query;
import org.ei.domain.SearchForm;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.exception.SessionException;
import org.ei.gui.ListBoxOption;
import org.ei.parser.base.BooleanQuery;
import org.ei.query.base.ExactTermGatherer;
import org.ei.query.base.FastQueryWriter;
import org.ei.query.base.ThesaurusCVTermGatherer;
import org.ei.session.UserCredentials;
import org.ei.session.UserPreferences;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.action.WebAnalyticsEventProperties;
import org.ei.stripes.exception.EVExceptionHandler;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

@UrlBinding("/search/thesHome.url")
public class ThesaurusSearchAction extends SearchDisplayAction { // implements IBizBean {

    private final static Logger log4j = Logger.getLogger(ThesaurusSearchAction.class);

    public static final int THES_PAGE_SIZE = 10;

    // Thesaurus database flags
    protected boolean inspec;
    protected boolean compendex;
    protected boolean geobase;
    protected boolean georef;
    protected boolean encompasspat;
    protected boolean encompasslit;

    // Database IDs for help link
    private String helpdbids = "cpx,ins,grf,geo,ept,elt";

    // These variable to hold search words and search options
    protected String term = "";
    protected int termsearchcount;
    // Step number for term path
    private int snum = -1;

    private String clip;
    private List<ListBoxOption> clipoptions = new ArrayList<ListBoxOption>();
    private String combine = "or";  // Default combine to "OR"

    // Message indicator
    @Validate(trim=true,mask="zero")
    private String message;

    @Override
    protected void populateSearchFormFields(Query query) {
        if (query == null) return;

        this.sort = context.getUserSession().getUser().getUserPrefs().getSort();

        // Set the clipboard contents
        query.setSearchQueryWriter(new FastQueryWriter());
        query.setDatabaseConfig(DatabaseConfig.getInstance());
        query.setCredentials(context.getUserSession().getUser().getCartridge());

        // creating Thesaurus XML for output
        BooleanQuery bq;
        try {
            bq = query.getParseTree();
            ExactTermGatherer etg = new ThesaurusCVTermGatherer();
            List lstThesTerms = etg.gatherExactTerms(bq);
            Iterator itrThesTerms = lstThesTerms.iterator();
            combine = etg.getFirstConnector();

            String strThesTerm = "";
            while (itrThesTerms.hasNext()) {
                strThesTerm = (String) itrThesTerms.next();
                ListBoxOption option = new ListBoxOption(null, strThesTerm, strThesTerm);
                clipoptions.add(option);
            }

        } catch (Exception e) {
            EVExceptionHandler.logException("Unable to parse clipboard contents!", e, context.getRequest(), log4j);
        }

        super.populateSearchFormFields(query);
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
     * Search submit - calls parent class validate method
     *
     * @return Resolution
     * @throws InfrastructureException
     * @throws SessionException
     * @throws ServletException
     * @throws HistoryException
     * @throws IOException
     * @throws SearchException
     */
    @HandlesEvent("submit")
    public Resolution validate() throws InfrastructureException, SessionException {

    	return super.validate();
    }

    /**
     * Thesaurus search display - main display of the page. All others are Ajax displays
     *
     * @return Resolution
     * @throws InfrastructureException
     * @throws SessionException
     * @throws AccessException
     */
    @DefaultHandler
    public Resolution thesHome() throws InfrastructureException, SessionException {
        HttpServletRequest request = context.getRequest();
        setRoom(ROOM.search);

        // create web analytics event and add it to the request.
        createWebEvent(WebAnalyticsEventProperties.CAT_SEARCH_FORM, WebAnalyticsEventProperties.ACTION_THES_SEARCH,
            WebAnalyticsEventProperties.LABEL_NEW_SEARCH);

        // Ensure user has access to thes form.
        IEVWebUser user = context.getUserSession().getUser();
        if (!user.getUserPreferences().getBoolean(UserPreferences.FENCE_THESAURUS) || !isThesaurusEnabled()) {
            return SystemMessage.FEATURE_DISABLED_RESOLUTION;
        }

        // Save credentials for Thesaurus search
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        this.compendex = UserCredentials.hasCredentials(DatabaseConfig.CPX_MASK, user.getUsermask());
        this.inspec = UserCredentials.hasCredentials(DatabaseConfig.INS_MASK, user.getUsermask());
        this.geobase = UserCredentials.hasCredentials(DatabaseConfig.GEO_MASK, user.getUsermask());
        this.georef = UserCredentials.hasCredentials(DatabaseConfig.GRF_MASK, user.getUsermask());
        this.encompasspat = UserCredentials.hasCredentials(DatabaseConfig.EPT_MASK, user.getUsermask());
        this.encompasslit = UserCredentials.hasCredentials(DatabaseConfig.ELT_MASK, user.getUsermask());

        // Trim (if appropriate) the help ids
        if (!isGeobase())
            helpdbids = helpdbids.replaceAll("geo", "");
        if (!isGeoref())
            helpdbids = helpdbids.replaceFirst("grf,", "");
        if (!isInspec())
            helpdbids = helpdbids.replaceAll("ins,", "");
        if (!isCompendex())
            helpdbids = helpdbids.replaceAll("cpx,", "");
        if (!isEncompasspat())
            helpdbids = helpdbids.replaceAll("ept,", "");
        if (!isEncompasslit())
            helpdbids = helpdbids.replaceAll("elt,", "");

        // The BackOffice can set the start page the Thesaurus but
        // the default DB may not be one of cpx, ins, grf or geo. We
        // need to set the DB appropriately and redirect in that case.
        // Set up to get form information
        int db = initDatabase();
        if (db != DatabaseConfig.CPX_MASK && db != DatabaseConfig.INS_MASK && db != DatabaseConfig.GEO_MASK && db != DatabaseConfig.EPT_MASK
            && db != DatabaseConfig.ELT_MASK && db != DatabaseConfig.EPT_MASK + DatabaseConfig.ELT_MASK && db != DatabaseConfig.GRF_MASK) {
            db = defaultDatabase();
            return new RedirectResolution("/search/thesHome.url?database=" + db + "#init");
        }

        // Call preprocess to setup form values
        preprocess();

        // Get the doc type options (LIMIT BY).
        doctypeopts = SearchForm.getOptions(doctype, db, "doctype");

        // Get the treatment type options (LIMIT BY). This is only set for
        // specific database values!
        if ((db & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK || (db & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
            treatmenttypeopts = SearchForm.getOptions(treatmentType, db, "treattype");
        }

        // Get the discipline type options (LIMIT BY). This is only set for
        // specific database values!
        if ((db & DatabaseConfig.IBS_MASK) == DatabaseConfig.IBS_MASK || (db & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
            disciplinetypeopts = SearchForm.getOptions(disciplinetype, db, "discipline");
        }

        // Always output language options (LIMIT BY)
        languageopts = SearchForm.getOptions(language, db, "language");

        // Get year options
        startyearopts = SearchForm.getYears(db, startYear, stringYear, "startYear");
        endyearopts = SearchForm.getYears(db, Integer.toString(SearchForm.calEndYear(db)), stringYear, "endYear");
        if (GenericValidator.isBlankOrNull(endYear)) {
            endYear = Integer.toString(SearchForm.calEndYear(db));
        }

        // Display!
        return new ForwardResolution("/WEB-INF/pages/customer/search/thesHome.jsp");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.stripes.validation.ValidationErrorHandler#handleValidationErrors(net.sourceforge.stripes.validation.ValidationErrors)
     */
    @Override
    public Resolution handleValidationErrors(ValidationErrors errors) throws InfrastructureException, SessionException {
        if (errors != null) {
            return thesHome();
        }
        return null;
    }

    //
    //
    // GETTERS/SETTERS
    //
    //

    public boolean isInspec() {
        return inspec;
    }

    public void setInspec(boolean inspec) {
        this.inspec = inspec;
    }

    public boolean isCompendex() {
        return compendex;
    }

    public void setCompendex(boolean compendex) {
        this.compendex = compendex;
    }

    public boolean isGeobase() {
        return geobase;
    }

    public void setGeobase(boolean geobase) {
        this.geobase = geobase;
    }

    public boolean isGeoref() {
        return georef;
    }

    public void setGeoref(boolean georef) {
        this.georef = georef;
    }

    public boolean isEncompasspat() {
        return encompasspat;
    }

    public void setEncompasspat(boolean encompasspat) {
        this.encompasspat = encompasspat;
    }

    public boolean isEncompasslit() {
        return encompasslit;
    }

    public void setEncompasslit(boolean encompasslit) {
        this.encompasslit = encompasslit;
    }

    public String getHelpdbids() {
        return helpdbids;
    }

    public void setHelpdbids(String helpdbids) {
        this.helpdbids = helpdbids;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getYearRange() {
        return yearRange;
    }

    public void setYearRange(String yearRange) {
        this.yearRange = yearRange;
    }

    public int defaultDatabase() {
        int db = Integer.parseInt(database);
        // See if it's already at an appropriate value
        if (db == DatabaseConfig.CPX_MASK || db == DatabaseConfig.INS_MASK || db == DatabaseConfig.GEO_MASK || db == DatabaseConfig.GRF_MASK
            || db == DatabaseConfig.EPT_MASK) {
            return db;
        }
        // Check entitlements IN ORDER!!!
        if (isCompendex())
            database = Integer.toString(DatabaseConfig.CPX_MASK);
        else if (isInspec())
            database = Integer.toString(DatabaseConfig.INS_MASK);
        else if (isGeobase())
            database = Integer.toString(DatabaseConfig.GEO_MASK);
        else if (isGeoref())
            database = Integer.toString(DatabaseConfig.GRF_MASK);
        else if (isEncompasslit())
            database = Integer.toString(DatabaseConfig.ELT_MASK);
        else if (isEncompasspat())
            database = Integer.toString(DatabaseConfig.EPT_MASK);
        return Integer.parseInt(database);
    }

    public List<ListBoxOption> getClipoptions() {
        return clipoptions;
    }

    public String getCombine() {
        return combine;
    }

    public void setCombine(String combine) {
        this.combine = combine;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSnum() {
        return snum;
    }

    public void setSnum(int snum) {
        this.snum = snum;
    }

    public String getClip() {
        return clip;
    }

    public void setClip(String clip) {
        this.clip = clip;
    }

}
