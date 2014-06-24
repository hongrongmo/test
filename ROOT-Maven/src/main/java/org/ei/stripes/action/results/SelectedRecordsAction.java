package org.ei.stripes.action.results;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.config.JSPPathProperties;
import org.ei.config.RuntimeProperties;
import org.ei.domain.DatabaseDisplayHelper;
import org.ei.exception.ErrorXml;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SessionException;
import org.ei.session.UserSession;
import org.ei.stripes.action.search.BaseSearchAction;
import org.ei.stripes.adapter.GenericAdapter;
import org.ei.stripes.adapter.IBizBean;
import org.ei.stripes.adapter.IBizXmlAdapter;
import org.ei.stripes.view.PageNavigation;
import org.ei.stripes.view.ResultsNavigatorItem;
import org.ei.stripes.view.SearchResult;
import org.ei.stripes.view.SearchResultNavigator;
import org.ei.stripes.view.SortOption;
import org.ei.tags.TagBubble;

/**
 * This is the base class for the selected records results handling.
 * 
 * PLEASE NOTE It can be invoked but ***ONLY*** for zero results searches!
 * 
 * @see processModelXML method below for how to handle this.
 * 
 * @author harovetm
 * 
 */
@UrlBinding("/selected/{$event}.url")
public class SelectedRecordsAction extends BaseSearchAction implements IBizBean, ICitationAction {
    
    private final static Logger log4j = Logger.getLogger(SelectedRecordsAction.class);
    
    @Override
    public String getXSLPath() {
        
        if ("citation".equals(this.context.getEventName()) || "deleteall".equals(this.context.getEventName())
            || "citationfolder".equals(this.context.getEventName()) || "deleteallfolder".equals(this.context.getEventName())) {
            return this.getClass().getResource("/transform/results/SelectedRecordsAdapter.xsl").toExternalForm();
        } else if ("abstract".equals(this.context.getEventName()) || "abstractfolder".equals(this.context.getEventName())) {
            return this.getClass().getResource("/transform/results/SelectedRecordsAbstractAdapter.xsl").toExternalForm();
        } else if ("detailed".equals(this.context.getEventName()) || "detailedfolder".equals(this.context.getEventName())) { return this.getClass()
            .getResource("/transform/results/SelectedRecordsDetailedAdapter.xsl").toExternalForm(); }
        return "";
    }
    
    @Override
    public String getXMLPath() {
        if ("citationfolder".equalsIgnoreCase(context.getEventName()) || "abstractfolder".equals(this.context.getEventName())
            || "detailedfolder".equals(this.context.getEventName())) {
            return EVProperties.getJSPPath(JSPPathProperties.VIEW_SAVED_RECORDS);
        } else if ("deleteallfolder".equals(this.context.getEventName())) {
            return EVProperties.getJSPPath(JSPPathProperties.DELETE_ALL_FROM_FOLDER);
        } else if ("deletefolder".equals(this.context.getEventName())) {
            return EVProperties.getJSPPath(JSPPathProperties.DELETE_FROM_SAVED_RECORDS);
        } else if ("citation".equalsIgnoreCase(context.getEventName()) || "abstract".equals(this.context.getEventName())
            || "detailed".equals(this.context.getEventName()) || "deleteall".equals(this.context.getEventName())) { return EVProperties
            .getJSPPath(JSPPathProperties.SELECTED_SET); }
        return "";
    }
    
    /**
     * Return the XML adapter for quick search results display! (executed from org.ei.stripes.AuthInterceptor)
     * 
     * @throws ServletException
     */
    @SuppressWarnings("deprecation")
    public void processModelXml(InputStream instream) throws InfrastructureException {
        IBizXmlAdapter adapter = new GenericAdapter();
            adapter.processXml(this, instream, getXSLPath());
            
            if (getNewsearch() != null)
            {
                setNewsearchencoded(URLEncoder.encode(getNewsearch()));
            }
            if (getSearchresults() == null || getSearchresults().isEmpty()) {
                setNewsearch("CID=quickSearch&database=" + this.database);
            } else {
                setSearchresults(URLDecoder.decode(getSearchresults()));
                setSearchresultsencoded(URLEncoder.encode(getSearchresults()));
            }
            
      
    }
    
    // The CID value to re-run the search
    protected String reruncid;
    
    // These variables to hold search words and search options
    protected int resultscount;
    protected int resultsperpage;
    protected String selectoption;
    
    protected List<SortOption> sortoptions = new ArrayList<SortOption>();
    protected boolean autostemming;
    
    protected String displayquery = "";
    protected List<ResultsNavigatorItem> refinequery = new ArrayList<ResultsNavigatorItem>();
    
    protected String sessionid = "";
    protected String queryid = "";
    protected String searchtype = "";
    protected int compmask;
    
    // These links are passed in from a results page
    protected String searchresults;
    protected String newsearch;
    
    // Record tools
    protected String emaillink;
    protected String downloadlink;
    protected String printlink;
    protected String savetofolderlink;
    protected String viewselectedlink;
    protected String removealllink;
    
    protected boolean fulltextenabled;
    protected boolean clearonnewsearch;
    
    protected PageNavigation pagenav = new PageNavigation();
    protected List<SearchResultNavigator> navigators = new ArrayList<SearchResultNavigator>();
    protected List<SearchResult> results = new ArrayList<SearchResult>();
    
    protected int appendstate;
    
    protected HashMap<String, Integer> authors = new HashMap<String, Integer>();
    protected HashMap<String, Integer> documentType = new HashMap<String, Integer>();
    protected HashMap<String, Integer> affiliations = new HashMap<String, Integer>();
    // The view type to be loaded - "citation" by default.
    private String view = "citation";
    // The CID value for pagination
    protected String cid;
    // These links are passed in from a results page in encoded format
    protected String searchresultsencoded;
    protected String newsearchencoded;
    protected boolean lindahall;
    // Index to go back to Search Results
    protected int backindex;
    protected String folderSize = "";
    protected String docindex;
    protected String format;
    // Highlighting
    private boolean ckhighlighting = true;
    
    @Before
    public void updateSession() throws SessionException {
        
        if (null != getRequest().getParameter("pageSizeVal")) {
            UserSession usersession = context.getUserSession();
            if (usersession == null) {
                log4j.warn("UserSession object not available!");
            } else {
                usersession.setRecordsPerPage(getRequest().getParameter("pageSizeVal"));
                try {
                    context.updateUserSession(usersession);
                } catch (SessionException e) {
                    log4j.error("************ Unable to update navigator state: " + e.getMessage());
                }
                context.updateUserSession(usersession);
            }
            
        }
        
        // String pageSizeOptions= context.getRuntimeProperties().getProperty("PAGESIZEOPTIONS");
        String pageSizeOptions = EVProperties.getRuntimeProperty(RuntimeProperties.PAGESIZE_OPTIONS);
        StringTokenizer st = new StringTokenizer(pageSizeOptions, ",");
        if (null != st) {
            while (st.hasMoreTokens()) {
                String value = st.nextToken();
                if (!value.isEmpty()) {
                    addPageCountOption(value);
                }
            }
        }
    }
    
    /**
     * Citation format handler
     * 
     * @return Resolution
     */
    @HandlesEvent("deleteall")
    @DontValidate
    public Resolution deleteall() {
        setRoom(ROOM.selectedrecords);
        getContext().getRequest().setAttribute("pageTitle", "Selected Set");
        
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/customer/results/selected.jsp");
    }
    
    /**
     * Citation format handler
     * 
     * @return Resolution
     */
    @HandlesEvent("citation")
    @DontValidate
    public Resolution displaycitation() {
        setRoom(ROOM.selectedrecords);
        getContext().getRequest().setAttribute("pageTitle", "Selected Set - Citation Format");
        setView("citation");
        setSelectoption("citation");
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/customer/results/selected.jsp");
    }
    
    /**
     * Abstract format handler
     * 
     * @return Resolution
     */
    @HandlesEvent("abstract")
    @DontValidate
    public Resolution displayabstract() {
        setRoom(ROOM.search);
        getContext().getRequest().setAttribute("pageTitle", "Selected Set - Abstract Format");
        if (getSearchresults() == null) {
            setNewsearch("CID=quickSearch&database=" + this.database);
        }
        setView("abstract");
        setSelectoption("abstract");
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/customer/results/selected.jsp");
    }
    
    /**
     * Detailed format handler
     * 
     * @return Resolution
     */
    @HandlesEvent("detailed")
    @DontValidate
    public Resolution displaydetailed() {
        setRoom(ROOM.search);
        getContext().getRequest().setAttribute("pageTitle", "Selected Set - Detailed Format");
        if (getSearchresults() == null) {
            setNewsearch("CID=quickSearch&database=" + this.database);
        }
        setView("detailed");
        setSelectoption("detailed");
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/customer/results/selected.jsp");
    }
    
    /**
     * folder Citation format handler
     * 
     * @return Resolution
     */
    @HandlesEvent("citationfolder")
    @DontValidate
    public Resolution citationfolder() {
        setRoom(ROOM.mysettings);
        getContext().getRequest().setAttribute("pageTitle", "My Folders:" + folderName);
        setView("citation");
        setSelectoption("citation");
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/customer/folders/viewSavedRecordsFolder.jsp");
    }
    
    /**
     * folder Abstract format handler
     * 
     * @return Resolution
     */
    @HandlesEvent("abstractfolder")
    @DontValidate
    public Resolution abstractfolder() {
        setRoom(ROOM.mysettings);
        getContext().getRequest().setAttribute("pageTitle", "My Folders:" + folderName);
        setView("abstract");
        setSelectoption("abstract");
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/customer/folders/viewSavedRecordsFolder.jsp");
    }
    
    /**
     * folder Detailed format handler
     * 
     * @return Resolution
     */
    @HandlesEvent("detailedfolder")
    @DontValidate
    public Resolution detailedfolder() {
        setRoom(ROOM.mysettings);
        getContext().getRequest().setAttribute("pageTitle", "My Folders:" + folderName);
        setView("detailed");
        setSelectoption("detailed");
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/customer/folders/viewSavedRecordsFolder.jsp");
    }
    
    /**
     * folder Citation format handler
     * 
     * @return Resolution
     */
    @HandlesEvent("deleteallfolder")
    @DontValidate
    public Resolution deleteallfolder() {
        setRoom(ROOM.mysettings);
        getContext().getRequest().setAttribute("pageTitle", "My Folders:" + folderName);
        
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/customer/folders/viewSavedRecordsFolder.jsp");
    }
    
    /**
     * folder Citation format handler
     * 
     * @return Resolution
     */
    @HandlesEvent("deletefolder")
    @DontValidate
    public Resolution deletefolder() {
        setRoom(ROOM.mysettings);
        getContext().getRequest().setAttribute("pageTitle", "My Folders:" + folderName);
        
        // Forward on
        return new ForwardResolution("/WEB-INF/pages/customer/folders/viewSavedRecordsFolder.jsp");
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
    
    public int getAffiliationSize() {
        return (affiliations.size());
    }
    
    public void addAffiliation(String affiliation) {
        if (affiliations.get(affiliation) == null) {
            affiliations.put(affiliation, new Integer(1));
        }
        else {
            Integer i = (Integer) affiliations.get(affiliation);
            i++;
            affiliations.put(affiliation, i);
        }
    }
    
    public int getAuthorsSize() {
        if (authors == null) { return (0); }
        return authors.size();
    }
    
    public int getDocumentTypeSize() {
        if (documentType == null) { return (0); }
        return documentType.size();
    }
    
    public HashMap<String, Integer> getAuthors() {
        return authors;
    }
    
    public void addDocumentType(String doctype) {
        Object obj = authors.get(doctype);
        
        if (obj == null) {
            documentType.put(doctype, new Integer(1));
        }
        else {
            Integer i = (Integer) documentType.get(doctype);
            i++;
            documentType.put(doctype, i);
        }
    }
    
    public void addAuthor(String author) {
        Object obj = authors.get(author);
        
        if (obj == null) {
            authors.put(author, new Integer(1));
        }
        else {
            Integer i = (Integer) authors.get(author);
            i++;
            authors.put(author, i);
        }
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
        return URLEncoder.encode(displayquery, "UTF-8");
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
    
    public boolean isFulltextenabled() {
        return fulltextenabled;
    }
    
    public void setFulltextenabled(boolean fulltextenabled) {
        this.fulltextenabled = fulltextenabled;
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
        return DatabaseDisplayHelper.getDisplayName(compmask);
    }
    
    public int getAppendstate() {
        return appendstate;
    }
    
    public String getSearchresults() {
        return searchresults;
    }
    
    public void setSearchresults(String searchresults) {
        this.searchresults = searchresults;
    }
    
    public String getNewsearch() {
        return newsearch;
    }
    
    public void setNewsearch(String newsearch) {
        this.newsearch = newsearch;
    }
    
    public String getView() {
        return view;
    }
    
    public void setView(String view) {
        this.view = view;
    }
    
    public String getCid() {
        return cid;
    }
    
    public void setCid(String cid) {
        this.cid = cid;
    }
    
    public String getSearchresultsencoded() {
        return searchresultsencoded;
    }
    
    public void setSearchresultsencoded(String searchresultsencoded) {
        this.searchresultsencoded = searchresultsencoded;
    }
    
    public String getNewsearchencoded() {
        return newsearchencoded;
    }
    
    public void setNewsearchencoded(String newsearchencoded) {
        this.newsearchencoded = newsearchencoded;
    }
    
    @Override
    public void setTagbubble(TagBubble tagbubble) {
        // No tag bubbles on citation results
        return;
    }
    
    public boolean isLindahall() {
        return lindahall;
    }
    
    public void setLindahall(boolean lindahall) {
        this.lindahall = lindahall;
    }
    
    public int getBackindex() {
        return backindex;
    }
    
    public void setBackindex(int backindex) {
        this.backindex = backindex;
    }
    
    public String getDocindex() {
        return docindex;
    }
    
    public void setDocindex(String docindex) {
        this.docindex = docindex;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public boolean isCkhighlighting() {
        return ckhighlighting;
    }
    
    public void setCkhighlighting(boolean ckhighlighting) {
        this.ckhighlighting = ckhighlighting;
    }

    /**
     * Default handling for exceptions from data service layer
     * @param errorXml
     * @return
     */
	public Resolution handleException(ErrorXml errorXml) {
		context.getRequest().setAttribute("errorXml", errorXml);
		return new ForwardResolution("/WEB-INF/pages/world/systemerror.jsp");
	}
	
    
}
