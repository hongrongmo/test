package org.ei.stripes.action.results;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import org.apache.log4j.Logger;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DatabaseDisplayHelper;
import org.ei.exception.ErrorXml;
import org.ei.session.UserPreferences;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.action.search.BaseSearchAction;
import org.ei.stripes.view.DedupSummary;
import org.ei.stripes.view.PageNavigation;
import org.ei.stripes.view.PatentrefSummary;
import org.ei.stripes.view.ResultsNavigatorItem;
import org.ei.stripes.view.SearchResult;
import org.ei.stripes.view.SearchResultNavigator;
import org.ei.stripes.view.SortOption;

/**
 * This is an abstract class that represents a search results action. Any ActionBean that needs to create search results should extend this class.
 * 
 * @author harovetm
 * 
 */
public abstract class AbstractSearchResultsAction extends BaseSearchAction {
	private static final Logger log4j = Logger.getLogger(AbstractSearchResultsAction.class);
	
    // Error key
    private String error;

    // China build indicator
    protected boolean china;

    // The CID value to re-run the search
    protected String reruncid;

    // These variables to hold search words and search options
    protected int resultscount;
    protected int resultsperpage;
    protected String selectoption;

    protected List<SortOption> sortoptions = new ArrayList<SortOption>();
    protected boolean autostemming;

    protected String displayquery = "";
    protected String physicalquery = "";
    protected List<ResultsNavigatorItem> refinequery = new ArrayList<ResultsNavigatorItem>();
    protected boolean dedup;
    protected String dedupdb;

    protected String sessionid = "";
    protected String queryid = "";
    protected String searchtype = "";
    protected int compmask;

    protected boolean lindahall;

    // Flags for saved searches / alerts
    protected String savedsearchflag;
    protected String createalertflag;

    // These links are built in the stylesheet
    protected String rsslink;
    protected String emaillink;
    protected String downloadlink;
    protected String printlink;
    protected String savetofolderlink;
    protected String viewselectedlink;
    protected String removealllink;

    protected boolean fulltextenabled;
    protected boolean showmap;
    protected boolean clearonnewsearch;

    protected PageNavigation pagenav = new PageNavigation();
    protected List<SearchResultNavigator> navigators = new ArrayList<SearchResultNavigator>();
    protected List<SearchResult> results = new ArrayList<SearchResult>();

    protected boolean appendopen;
    protected String linkResultCount = "";
    protected String dedupResultCount;
    protected String appendstr;
    private int basketCount;
    // patents and other references
    protected boolean patentref;
    protected PatentrefSummary patentrefsummary;
    protected String dbname;
    protected String docid;
    protected String docindex;
    protected String citedbyflag;

    
    
    //
    // Dedup only fields
    //
    protected DedupSummary dedupsummary;

    // Basket flags
    protected String shownewsrchalert = "true";
    protected String showmaxalert = "true";
    protected String showmaxalertclear = "true";
    // protected String tracksearchid;
    private int prevSrchBasketCount;
    public Map<String, String> textzones;

    /**
     * Default handling for exceptions from data service layer
     * @param errorXml
     * @return
     */
	public Resolution handleException(ErrorXml errorXml) {
		context.getRequest().setAttribute("errorXml", errorXml);
		//return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		return new ForwardResolution("/WEB-INF/pages/world/systemerror.jsp");
	}
	
    //
    //
    // GETTERS/SETTERS
    //
    //

    public String getReruncid() {
        return reruncid;
    }

    public void setReruncid(String reruncid) {
        this.reruncid = reruncid;
    }

    public void addSearchResult(SearchResult sr) {
        if (results == null) {
            results = new ArrayList<SearchResult>();
        }
        results.add(sr);
    }

    public int getResultscount() {
        return resultscount;
    }

    public void setResultscount(int resultscount) {
        this.resultscount = resultscount;
    }

    public int getResultsperpage() {
        return resultsperpage;
    }

    public void setResultsperpage(int resultsperpage) {
        this.resultsperpage = resultsperpage;
    }

    public String getSelectoption() {
        return selectoption;
    }

    public void setSelectoption(String selectoption) {
        this.selectoption = selectoption;
    }

    public String getSearchWord1() {
        return searchWord1;
    }

    public void setSearchWord1(String searchWord1) {
        this.searchWord1 = searchWord1;
    }

    public String getSearchWord2() {
        return searchWord2;
    }

    public void setSearchWord2(String searchWord2) {
        this.searchWord2 = searchWord2;
    }

    public String getSearchWord3() {
        return searchWord3;
    }

    public void setSearchWord3(String searchWord3) {
        this.searchWord3 = searchWord3;
    }

    public String getBoolean1() {
        return boolean1;
    }

    public void setBoolean1(String boolean1) {
        this.boolean1 = boolean1;
    }

    public String getBoolean2() {
        return boolean2;
    }

    public void setBoolean2(String boolean2) {
        this.boolean2 = boolean2;
    }

    public String getSection1() {
        return section1;
    }

    public void setSection1(String section1) {
        this.section1 = section1;
    }

    public String getSection2() {
        return section2;
    }

    public void setSection2(String section2) {
        this.section2 = section2;
    }

    public String getSection3() {
        return section3;
    }

    public void setSection3(String section3) {
        this.section3 = section3;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getDisciplinetype() {
        return disciplinetype;
    }

    public void setDisciplinetype(String disciplinetype) {
        this.disciplinetype = disciplinetype;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(String treatmentType) {
        this.treatmentType = treatmentType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<SortOption> getSortoptions() {
        return sortoptions;
    }

    public void addSortoption(SortOption sortoption) {
        this.sortoptions.add(sortoption);
    }

    public boolean isAutostemming() {
        return autostemming;
    }

    public void setAutostemming(boolean autostemming) {
        this.autostemming = autostemming;
    }

    public String getEncodeddisplayquery() throws UnsupportedEncodingException {
        if (displayquery == null)
            return "";
        else
            return URLEncoder.encode(displayquery, "UTF-8");
    }
    public String getPhysicalquery() {
        return physicalquery;
    }

    public void setPhysicalquery(String physicalquery) {
        this.physicalquery = physicalquery;
    }
    public String getDisplayquery() {
        return displayquery;
    }

    public void setDisplayquery(String displayquery) {
        this.displayquery = displayquery;
    }

    public List<ResultsNavigatorItem> getRefinequery() {
        return refinequery;
    }

    public void addRefinequery(ResultsNavigatorItem refinequery) {
        this.refinequery.add(refinequery);
    }

    public boolean isDedup() {
        return dedup;
    }

    public void setDedup(boolean dedup) {
        this.dedup = dedup;
    }

    public String getDedupdb() {
        return dedupdb;
    }

    public void setDedupdb(String dedupdb) {
        this.dedupdb = dedupdb;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getQueryid() {
        return queryid;
    }

    public void setQueryid(String queryid) {
        this.queryid = queryid;
    }

    public String getSearchtype() {
        return searchtype;
    }

    public void setSearchtype(String searchtype) {
        this.searchtype = searchtype;
    }

    public String getRsslink() {
        return rsslink;
    }

    public void setRsslink(String rsslink) {
        this.rsslink = rsslink;
    }

    public boolean isFulltextenabled() {
        return fulltextenabled;
    }

    public void setFulltextenabled(boolean fulltextenabled) {
        this.fulltextenabled = fulltextenabled;
    }

    public boolean isShowmap() {
        return showmap;
    }

    public void setShowmap(boolean showmap) {
        this.showmap = showmap;
    }

    public PageNavigation getPagenav() {
        return pagenav;
    }

    public void setPagenav(PageNavigation pagenav) {
        this.pagenav = pagenav;
    }

    public List<SearchResultNavigator> getNavigators() {
        return navigators;
    }

    public void setNavigators(List<SearchResultNavigator> navigators) {
        this.navigators = navigators;
    }

    public void addNavigator(SearchResultNavigator srn) {
        if (navigators == null) {
            navigators = new ArrayList<SearchResultNavigator>();
        }
        navigators.add(srn);
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(List<SearchResult> results) {
        this.results = results;
    }

    public String getEmaillink() {
        return emaillink;
    }

    public void setEmaillink(String emaillink) {
        this.emaillink = emaillink;
    }

    public String getDownloadlink() {
        return downloadlink;
    }

    public void setDownloadlink(String downloadlink) {
        this.downloadlink = downloadlink;
    }

    public String getPrintlink() {
        return printlink;
    }

    public void setPrintlink(String printlink) {
        this.printlink = printlink;
    }

    public String getSavetofolderlink() {
        return savetofolderlink;
    }

    public void setSavetofolderlink(String savetofolderlink) {
        this.savetofolderlink = savetofolderlink;
    }

    public DedupSummary getDedupsummary() {
        return dedupsummary;
    }

    public void setDedupsummary(DedupSummary dedupsummary) {
        this.dedupsummary = dedupsummary;
    }

    public String getViewselectedlink() {
        return viewselectedlink;
    }

    public void setViewselectedlink(String viewselectedlink) {
        this.viewselectedlink = viewselectedlink;
    }

    public String getRemovealllink() {
        return removealllink;
    }

    public void setRemovealllink(String removealllink) {
        this.removealllink = removealllink;
    }

    public boolean isClearonnewsearch() {
        return clearonnewsearch;
    }

    public void setClearonnewsearch(boolean clearonnewsearch) {
        this.clearonnewsearch = clearonnewsearch;
    }

    public int getCompmask() {
        return compmask;
    }

    public void setCompmask(int compmask) {
        this.compmask = compmask;
    }

    public String getFoundin() {
        if (compmask <= 0) {
        	log4j.info("Setting compmask from database: " + database);
        	try {
            compmask = Integer.parseInt(database);
        	} catch (Exception e) {
            	log4j.error("Unable to convert datbase value '" + database + "'");
            	compmask = DatabaseConfig.CPX_MASK;
        	}
        }
        return DatabaseDisplayHelper.getDisplayName(compmask);
    }

    public boolean isChina() {
        return china;
    }

    public void setChina(boolean china) {
        this.china = china;
    }

    public boolean isAppendopen() {
        return appendopen;
    }

    public boolean isLindahall() {
        return context.getUserSession().getUser().getPreference(UserPreferences.FENCE_DDS_SERVICE_LHL);
    }

    public void setLindahall(boolean lindahall) {
        this.lindahall = lindahall;
    }

    public String getLinkResultCount() {
        return linkResultCount;
    }

    public void setLinkResultCount(String linkResultCount) {
        this.linkResultCount = linkResultCount;
    }

    public String getDedupResultCount() {
        return dedupResultCount;
    }

    public void setDedupResultCount(String dedupResultCount) {
        this.dedupResultCount = dedupResultCount;
    }

    public String getAppendstr() {
        return appendstr;
    }

    public void setAppendstr(String appendstr) {
        this.appendstr = appendstr;
    }

    public int getBasketCount() {
        return basketCount;
    }

    public void setBasketCount(int basketCount) {
        this.basketCount = basketCount;
    }

    public String getSavedsearchflag() {
        return savedsearchflag;
    }

    public void setSavedsearchflag(String savedsearchflag) {
        this.savedsearchflag = savedsearchflag;
    }

    public String getCreatealertflag() {
        return createalertflag;
    }

    public void setCreatealertflag(String createalertflag) {
        this.createalertflag = createalertflag;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isPatentref() {
        return patentref;
    }

    public void setPatentref(boolean patentref) {
        this.patentref = patentref;
    }

    public PatentrefSummary getPatentrefsummary() {
        return patentrefsummary;
    }

    public void setPatentrefsummary(PatentrefSummary patentrefsummary) {
        this.patentrefsummary = patentrefsummary;
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getDocindex() {
        return docindex;
    }

    public void setDocindex(String docindex) {
        this.docindex = docindex;
    }

    public String getCitedbyflag() {
        return citedbyflag;
    }

    public void setCitedbyflag(String citedbyflag) {
        this.citedbyflag = citedbyflag;
    }

    public String getShownewsrchalert() {
        return shownewsrchalert;
    }

    public void setShownewsrchalert(String shownewsrchalert) {
        this.shownewsrchalert = shownewsrchalert;
    }

    public String getShowmaxalert() {
        return showmaxalert;
    }

    public void setShowmaxalert(String showmaxalert) {
        this.showmaxalert = showmaxalert;
    }

    public String getShowmaxalertclear() {
        return showmaxalertclear;
    }

    public void setShowmaxalertclear(String showmaxalertclear) {
        this.showmaxalertclear = showmaxalertclear;
    }

    public int getPrevSrchBasketCount() {
        return prevSrchBasketCount;
    }

    public void setPrevSrchBasketCount(int prevSrchBasketCount) {
        this.prevSrchBasketCount = prevSrchBasketCount;
    }

    public Map<String, String> getTextzones() {
        return textzones;
    }

    public void setTextzones(Map<String, String> textzones) {
        this.textzones = textzones;
    }

}
