package org.ei.stripes.action.results;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.config.JSPPathProperties;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Query;
import org.ei.domain.Searches;
import org.ei.exception.ErrorXml;
import org.ei.exception.InfrastructureException;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVPathUrl;
import org.ei.stripes.adapter.GenericAdapter;
import org.ei.stripes.adapter.IBizBean;
import org.ei.stripes.adapter.IBizXmlAdapter;
import org.ei.stripes.view.Author;
import org.ei.stripes.view.DedupSummary;
import org.ei.tags.TagBubble;

/**
 * This is the base class for the search results abstract/detailed page handling.
 *
 * @author harovetm
 *
 */
public abstract class SearchResultsFormatAction extends SearchResultsAction implements IBizBean {

    private final static Logger log4j = Logger.getLogger(SearchResultsFormatAction.class);

    private TagBubble tagbubble;
    private boolean ckhighlighting = true;
    private String view = "abstract";
    private String prevqs;
    private String nextqs;
    private String resultsqs;
    private String newsearchqs;
    private String dedupnavqs;
    private String absnavqs;
    private String detnavqs;
    private String bookdetnavqs;
    private String displaydb;
    protected DedupSummary dedupsummary;
    private String displayPagination;
    protected boolean bloglink;
    private String displaySearchType;
    private String pageType;

    @After(stages = LifecycleStage.BindingAndValidation)
    public void init() {
        HttpServletRequest request = context.getRequest();
        String qs = request.getQueryString();
        backurl = request.getRequestURI() + (qs == null ? "" : "?" + qs);
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getBacktoresultsLink() throws InfrastructureException {

        if (GenericValidator.isBlankOrNull(this.searchid)) {
            log4j.warn("No search ID present!");
            return EVPathUrl.EV_HOME.value();
        }

        Query query = Searches.getSearch(this.searchid);
        if (query == null) {
            log4j.warn("No Query object found!");
            return EVPathUrl.EV_HOME.value();
        }

        StringBuffer backtoresults = new StringBuffer("/search/results/");
        if (GenericValidator.isBlankOrNull(this.searchtype) || "Quick".equals(searchtype) || "Book".equals(searchtype) || "Easy".equals(searchtype)) {
            backtoresults.append("quick.url?");
        } else if ("Expert".equals(searchtype) || "Combined".equals(searchtype)) {
            backtoresults.append("expert.url?");
        } else if ("Thesaurus".equals(searchtype)) {
            backtoresults.append("thes.url?");
        } else if ("Tags".equals(searchtype)) {
            backtoresults.append("tags.url?");
        }
        backtoresults.append("SEARCHID=" + this.searchid);
        backtoresults.append("&database=" + query.getDataBase());
        return backtoresults.toString();
    }

    /**
     * Return the XML adapter for quick search results display! (executed from org.ei.stripes.AuthInterceptor)
     *
     * @throws ServletException
     */
    public void processModelXml(InputStream instream) throws InfrastructureException {
        IBizXmlAdapter adapter = new GenericAdapter();

        adapter.processXml(this, instream, getXSLPath());

        // Only show maps (beta) under the following conditions:
        // * HAS COMPMASK == 8192 (geobase), 2097152 (georef) or 2105344 (both)
        // * HAS a navigator called 'geonav'
        // * NOT in a china build!
        if (((this.compmask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK || (this.compmask & DatabaseConfig.GRF_MASK) == DatabaseConfig.GRF_MASK)) {
            showmap = true;
        }

        // Attempt to set the highlighting from the user's session
        UserSession usersession = context.getUserSession();
        if (usersession == null) {
            log4j.warn("Unable to set highlighting preference - UserSession object not available!");
        } else {
            String hl = usersession.getProperty(UserSession.HIGHLIGHT_STATE);
            // @see UpdaetUsersSession.java action class. The highlight session variable
            // is ONLY put into session when it's OFF.
            if (!GenericValidator.isBlankOrNull(hl)) {
                ckhighlighting = false;
            }
        }

        setTextzones(usersession.getUserTextZones());

    }

    public String getXMLPath() {

        if (isSearchTypeQuickSearch() || isSearchTypeExpertSearch()) {
            if ("patentrefabstract".equals(context.getEventName()) || "patentrefdetailed".equals(context.getEventName())) {
                return EVProperties.getJSPPath(JSPPathProperties.PATENT_REF_ABSTRACT_DETAILED_PATH);
            } else {
                return EVProperties.getJSPPath(JSPPathProperties.ABSTRACT_DETAILED_RECORD_PATH);
            }
        } else if (isSearchTypeTagSearch()) {
            return EVProperties.getJSPPath(JSPPathProperties.TAG_RECORD_FORWARD_PATH);
        } else if (isSearchTypeDedupSearch()) {
            return EVProperties.getJSPPath(JSPPathProperties.DEDUP_RECORD_FORWARD_PATH);
        } else {
            return EVProperties.getJSPPath(JSPPathProperties.ABSTRACT_DETAILED_RECORD_PATH);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.ei.stripes.action.results.SearchResultsAction#handleException(org.ei.exception.ErrorXml)
     */
    @Override
    public Resolution handleException(ErrorXml errorXml) {

        if ("quickSearch".equals(pageType)) {
            return new RedirectResolution("/search/quick.url?CID=quickSearch&searchID=" + getSearchid() + "&database=" + getDatabase()
                + "&error=validationError&errorCode=" + errorXml.getErrorCode());
        } else if ("expertSearch".equals(pageType)) {
            return new RedirectResolution("/search/expert.url?CID=quickSearch&searchID=" + getSearchid() + "&database=" + getDatabase()
                + "&error=validationError&errorCode=" + errorXml.getErrorCode());
        } else {
            log4j.warn("Unable to handle ErrorXml object, code = " + errorXml.getErrorCode() + "; message = '" + errorXml.getErrorMessage() + "'");
            context.getRequest().setAttribute("errorXml", errorXml);
            return new ForwardResolution("/WEB-INF/pages/world/systemerror.jsp");
        }

    }

    protected boolean isSearchTypeQuickSearch() {

        return "quickSearch".equalsIgnoreCase(getPageTypeFromRequest());
    }

    protected boolean isSearchTypeExpertSearch() {
        return "expertSearch".equalsIgnoreCase(getPageTypeFromRequest());
    }

    protected boolean isSearchTypeTagSearch() {
        return "tagSearch".equalsIgnoreCase(getPageTypeFromRequest());
    }

    protected boolean isSearchTypeDedupSearch() {
        return "dedupSearch".equalsIgnoreCase(getPageTypeFromRequest());
    }

    protected String getPageTypeFromRequest() {
        return context.getRequest().getParameter("pageType");
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getPrevqs() {
        return prevqs;
    }

    public void setPrevqs(String prevqs) {
        this.prevqs = prevqs;
    }

    public String getNextqs() {
        return nextqs;
    }

    public void setNextqs(String nextqs) {
        this.nextqs = nextqs;
    }

    public String getResultsqs() {
        return resultsqs;
    }

    public void setResultsqs(String resultsqs) {
        this.resultsqs = resultsqs;
    }

    public String getNewsearchqs() {
        return newsearchqs;
    }

    public void setNewsearchqs(String newsearchqs) {
        this.newsearchqs = newsearchqs;
    }

    public String getDedupnavqs() {
        return dedupnavqs;
    }

    public void setDedupnavqs(String dedupnavqs) {
        this.dedupnavqs = dedupnavqs;
    }

    public String getAbsnavqs() {
        return absnavqs;
    }

    public void setAbsnavqs(String absnavqs) {
        this.absnavqs = absnavqs;
    }

    public String getDetnavqs() {
        return detnavqs;
    }

    public void setDetnavqs(String detnavqs) {
        this.detnavqs = detnavqs;
    }

    public String getBookdetnavqs() {
        return bookdetnavqs;
    }

    public void setBookdetnavqs(String bookdetnavqs) {
        this.bookdetnavqs = bookdetnavqs;
    }

    public String getDisplaydb() {
        return displaydb;
    }

    public void setDisplaydb(String displaydb) {
        this.displaydb = displaydb;
    }

    public String getEncodedquery() {
        try {
            return URLEncoder.encode(displayquery, "UTF-8");
        } catch (Exception e) {
            log4j.error("Unable to encode first author!");
            return null;
        }
    }

    public String getEncodedfirstauthor() {
        try {
            if (results.size() > 0) {
                List<Author> authors = results.get(0).getAuthors();
                if (authors != null && authors.size() > 0)
                    return URLEncoder.encode(authors.get(0).getName(), "UTF-8");
            }
        } catch (Exception e) {
            log4j.error("Unable to encode first author!", e);
        }
        return null;
    }

    public boolean isCkhighlighting() {
        return ckhighlighting;
    }

    public void setCkhighlighting(boolean ckhighlighting) {
        this.ckhighlighting = ckhighlighting;
    }

    public TagBubble getTagbubble() {
        return tagbubble;
    }

    public void setTagbubble(TagBubble tagbubble) {
        this.tagbubble = tagbubble;
    }

    public String getDisplayPagination() {
        return displayPagination;
    }

    public void setDisplayPagination(String displayPagination) {
        this.displayPagination = displayPagination;
    }

    public boolean isBloglink() {
        return bloglink;
    }

    public void setBloglink(boolean bloglink) {
        this.bloglink = bloglink;
    }

    public String getDisplaySearchType() {
        return displaySearchType;
    }

    public void setDisplaySearchType(String displaySearchType) {
        this.displaySearchType = displaySearchType;
    }

}
