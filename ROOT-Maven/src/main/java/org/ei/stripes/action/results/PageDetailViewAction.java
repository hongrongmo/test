package org.ei.stripes.action.results;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.books.BookDocument;
import org.ei.config.EVProperties;
import org.ei.config.JSPPathProperties;
import org.ei.domain.DatabaseConfig;
import org.ei.exception.EVBaseException;
import org.ei.exception.InfrastructureException;
import org.ei.session.UserSession;
import org.ei.stripes.adapter.GenericAdapter;
import org.ei.stripes.adapter.IBizBean;
import org.ei.stripes.adapter.IBizXmlAdapter;
import org.ei.stripes.view.Author;
import org.ei.stripes.view.DedupSummary;
import org.ei.tags.TagBubble;

/**
 * This is the base class for the search results abstract/detailed page handling.
 * 
 * @author bhanu
 *
 */
@UrlBinding("/search/book/{$event}.url")

public class PageDetailViewAction extends SearchResultsAction implements IBizBean {

	private final static Logger log4j = Logger.getLogger(PageDetailViewAction.class);
	
		private TagBubble tagbubble;
		private boolean ckhighlighting=true;
		private String view = "page";
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
		private String pagedetnavqs;
		private String currentpage;
		private String toc; 
		private String internalsearch;
		private String cloud; 
		protected boolean bloglink;
		private String displaySearchType;
		private String pageType;
		private boolean tagsearch;
		private int scope;
		
		public String getPageType() {
			return pageType;
		}

		public void setPageType(String pageType) {
			this.pageType = pageType;
		}
		

	/**
	 * Return the XML adapter for quick search results display!
	 * (executed from org.ei.stripes.AuthInterceptor)
	 * @throws InfrastructureException 
	 * @throws EVBaseException 
	 * @throws ServletException 
	 */
	public void processModelXml(InputStream instream) throws InfrastructureException  {
		IBizXmlAdapter adapter = new GenericAdapter();
			
			adapter.processXml(this, instream, getXSLPath());

			// Only show maps (beta) under the following conditions:
			// * HAS COMPMASK == 8192 (geobase), 2097152 (georef) or 2105344 (both)
			// * HAS a navigator called 'geonav'
			// * NOT in a china build!
			if (((this.compmask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK || 
				 (this.compmask & DatabaseConfig.GRF_MASK) == DatabaseConfig.GRF_MASK)) {
				showmap = true;
			}
			
			// Attempt to set the highlighting from the user's session
			UserSession usersession = context.getUserSession();
			if (usersession == null) {
				log4j.warn("Unable to set highlighting preference - UserSession object not available!");
			} else {
				String hl = usersession.getHighlightState();
				// @see UpdaetUsersSession.java action class.  The highlight session variable
				// is ONLY put into session when it's OFF.  
				if (!GenericValidator.isBlankOrNull(hl)) {
					ckhighlighting = false;
				}
			}

	}
	
	
	public String getXSLPath() {
		return this.getClass().getResource("/transform/results/PageDetailedAdapter.xsl").toExternalForm();
	}

	
	public String getXMLPath() {
		
		if(isPageDetailPage()){
			return EVProperties.getJSPPath(JSPPathProperties.ABSTRACT_DETAILED_RECORD_PATH);
		}else if(isBookSummaryPage()){
			return EVProperties.getJSPPath(JSPPathProperties.BOOK_SUMMARY_PATH);
		}
		return "";
	}

	private boolean isBookSummaryPage() {
		return "book".equals(getPageType());
	}

	private boolean isPageDetailPage() {
		return "page".equals(getPageType());
	}


	/**
	 * Page/Book Detail View results handler
	 * 
	 * @return Resolution
	 */
	@HandlesEvent("detailed")
	@DontValidate
	public Resolution handler() {
		setRoom(ROOM.search);
		
		setPageTitle();
		return new ForwardResolution("/WEB-INF/pages/customer/record/pagedetails.jsp");
	}
    
	@HandlesEvent("bookdetailed")
	@DontValidate
	public Resolution bookdetailed() {
		setRoom(ROOM.search);
		
		setPageTitle();
		if ("TagSearch".equals(searchtype)) {
		    this.resultsqs = "SEARCHID=" + this.searchid + "&COUNT=" + this.resultscount + "&scope=" + this.scope;
		}
		return new ForwardResolution("/WEB-INF/pages/customer/record/pagedetails.jsp");
	}
	
	private void setPageTitle() {
		if(isPageDetailPage()){
			setDisplaySearchType("Page Details");
			setView("page");
		}else{
			setDisplaySearchType("Book Details");
			setView("book");
		}
		
		context.getRequest().setAttribute("pageTitle", "Engineering Village - "+ getDisplaySearchType() + " Format");
	}
	
	
	@Override
	public String getCID() {
		if("book".equals(getPageType())){
				return "bookSummary";
		}
		return "pageDetailedFormat";
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
				if (authors != null && authors.size() > 0) return URLEncoder.encode(authors.get(0).getName(), "UTF-8");
			}
		} catch (Exception e) {
			log4j.error("Unable to encode first author!",e);
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
	
	public String getPagedetnavqs() {
		return pagedetnavqs;
	}

	public void setPagedetnavqs(String pagedetnavqs) {
		this.pagedetnavqs = pagedetnavqs;
	}

	public String getCurrentpage() {
		return currentpage;
	}

	public void setCurrentpage(String currentpage) {
		this.currentpage = currentpage;
	}
	
	public String getToc() {
		try {
			if (this.results == null || this.results.size() == 0) {
				log4j.warn("No results object attached!");
				return "";
			}
			return BookDocument.getBookPartFromS3(this.results.get(0).getIsbn13(), "_toc.html");
		} catch (Exception e) {
			log4j.error("Unable to get tag cloud!", e);
			return "";
		}
	}

	@Deprecated
	public void setToc(String toc) {
		this.toc = toc;
	}

	public String getInternalsearch() {
		return internalsearch;
	}

	public void setInternalsearch(String internalsearch) {
		this.internalsearch = internalsearch;
	}
	
	public String getCloud() {
		try {
			if (this.results == null || this.results.size() == 0) {
				log4j.warn("No results object attached!");
				return "";
			}
			return BookDocument.getBookPartFromS3(this.results.get(0).getIsbn13(), "_cloud.html");
		} catch (Exception e) {
			log4j.error("Unable to get tag cloud!", e);
			return "";
		}
	}

	@Deprecated
	public void setCloud(String cloud) {
		this.cloud = cloud;
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

    public boolean isTagsearch() {
        return tagsearch;
    }

    public void setTagsearch(boolean tagsearch) {
        this.tagsearch = tagsearch;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

}

