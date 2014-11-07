package org.ei.stripes.action.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.Database;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.InvalidArgumentException;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.view.PageNavigation;
import org.ei.stripes.view.thesaurus.ThesaurusTermComp;
import org.ei.stripes.view.thesaurus.ThesaurusTermInfoComp;
import org.ei.thesaurus.ThesSearchNavigator;
import org.ei.thesaurus.ThesaurusAction;
import org.ei.thesaurus.ThesaurusException;
import org.ei.thesaurus.ThesaurusPage;
import org.ei.thesaurus.ThesaurusPath;
import org.ei.thesaurus.ThesaurusQuery;
import org.ei.thesaurus.ThesaurusRecord;
import org.ei.thesaurus.ThesaurusRecordBroker;
import org.ei.thesaurus.ThesaurusRecordID;
import org.ei.thesaurus.ThesaurusSearchControl;
import org.ei.thesaurus.ThesaurusSearchResult;
import org.ei.thesaurus.ThesaurusStep;
import org.ei.xml.Entity;
import org.xml.sax.SAXException;

@UrlBinding("/search/thes/{$event}.url")
public class ThesaurusAjaxAction extends EVActionBean {

	private final static Logger log4j = Logger.getLogger(ThesaurusAjaxAction.class);

	public static final String THES_TERMSEARCH_URL = "/search/thes/termsearch.url";
	public static final String THES_FULLREC_URL = "/search/thes/fullrec.url";
	public static final String THES_BROWSE_URL = "/search/thes/browse.url";

	public static final int THES_PAGE_SIZE = 10;
	public static final String THES_PATH_SESSION_ID = "THESAURUS_PATH";

	// These variable to hold search words and search options
	protected String term = "";
	protected int tid = -1;
	protected int termsearchcount;
	// Step number for term path
	private int snum = -1;
	// Page number (optional)
	private int pageNumber = 0;
	// Index number (Browse only)
	private int index = -1;
	private int previndex = 0;
	private int nextindex = 0;
	// Page size (optional)
	private int pageSize = THES_PAGE_SIZE;
	// Form submit boolean
	private boolean formSubmit;

	// Results-based objects - used with term searches, record retrieval and
	// browse
	protected List<ThesaurusStep> steps;
	private List<ThesaurusRecord> termsearchresults;
	private List<ThesaurusRecord> termsearchsuggests;
	private ThesaurusRecord fullrecresult;
	private List<ThesaurusRecord> fullrecresults;
	private List<ThesaurusRecord> browseresults;
	private PageNavigation pagenav;

	/**
	 * Retrieve database by name
	 *
	 * @param dbName
	 * @return
	 */
	private String getDbName(String dbName) {
		if (dbName.equals("1")) {
			return "cpx";
		} else if (dbName.equals("2")) {
			return "ins";
		} else if (dbName.equals("3072")) {
			return "ept";
		} else if (dbName.equals("8192")) {
			return "geo";
		} else if (dbName.equals("2097152")) {
			return "grf";
		}
		return "cpx";
	}

	/**
	 * Retrieve the ThesaurusPath object from session
	 *
	 * @return
	 */
	private ThesaurusPath getPathFromSession() {
		ThesaurusPath path = (ThesaurusPath) context.getRequest().getSession(false).getAttribute(THES_PATH_SESSION_ID);
		if (path == null) {
			path = new ThesaurusPath(THES_TERMSEARCH_URL + "?", THES_BROWSE_URL + "?", THES_FULLREC_URL + "?");
			context.getRequest().getSession(false).setAttribute(THES_PATH_SESSION_ID, path);
		}
		return path;
	}

	/**
	 * Thesaurus search results display (AJAX)
	 *
	 * @return Resolution
	 * @throws ServletException
	 * @throws HistoryException
	 * @throws ClientCustomizerException
	 * @throws IOException
	 */
	@HandlesEvent("fullrec")
	@DontValidate
	public Resolution thesFullRec() throws Exception {


		if (GenericValidator.isBlankOrNull(term)) {
			throw new InvalidArgumentException("'term' must be present in request!");
		}

		//
		// Build the term path (breadcrumb)
		//
		ThesaurusPath thespath = getPathFromSession();
		Properties props = new Properties();
		Enumeration<String> anenum = context.getRequest().getParameterNames();
		while (anenum.hasMoreElements()) {
			String key = (String) anenum.nextElement();
			String value = context.getRequest().getParameter(key);
			props.setProperty(key, value);
		}
		props.remove("formSubmit");

		// See if form was submitted (if not, request was from a link click)
		if (formSubmit) {
			thespath.clearPathFrom(0);
			thespath.addStep(ThesaurusAction.FULL, term, props);
		} else {
			if (snum == -1 || !thespath.hasIndex(snum)) {
				thespath.addStep(ThesaurusAction.FULL, term, props);
			} else {
				thespath.clearPathFrom(snum);
				thespath.addStep(ThesaurusAction.FULL, term, props);
			}
		}
		steps = thespath.getSteps();

		// Change quoted and wildcard term searches
		term = Entity.prepareString(term.trim().replaceAll("^[\"\']", "").replaceAll("[\"\']$", "").replaceAll("\\*$", ""));

		//
		// Build the results from exact term search
		//
		DatabaseConfig dConfig = DatabaseConfig.getInstance();
		Database database = dConfig.getDatabase(getDbName(this.database));
		ThesaurusRecordBroker broker = new ThesaurusRecordBroker(database);
		ThesaurusRecordID recID = null;
		if (tid != -1) {
			recID = new ThesaurusRecordID(tid, term, database);
		} else {
			recID = new ThesaurusRecordID(term, database);
		}
		ThesaurusRecordID[] recIDs = new ThesaurusRecordID[1];
		recIDs[0] = recID;
		ThesaurusPage fullrecpage = broker.buildPage(recIDs, 0, 0);

		//
		// Check the count - if zero, look for suggestions
		//
		if (fullrecpage.get(0) == null) {
			ThesaurusPage thessuggest = broker.getSuggestions(term.toLowerCase(), pageSize);
			if (thessuggest.size() > 0) {
				// Build ThesaurusRecord list
				for (int idx = 0; idx < thessuggest.size(); idx++) {
					addTermsearchsuggest(thessuggest.get(idx));
				}
			}
			fullrecresult = null;
		} else {
			fullrecresults = fullrecpage.getRecords();
		}

		// Return HTML
		return new ForwardResolution("/WEB-INF/pages/customer/search/ajax/thesFullRec.jsp");
	}

	/**
	 * Thesaurus term search display (AJAX). NOTE that this does NOT catch
	 * exceptions. See the EVExceptionHandler class for exception handling
	 *
	 * @return Resolution
	 * @throws InfrastructureException
	 * @throws SessionException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ThesaurusException
	 * @throws ParseException
	 * @throws InvalidArgumentException
	 * @throws ServletException
	 * @throws HistoryException
	 * @throws ClientCustomizerException
	 */
	@HandlesEvent("termsearch")
	@DontValidate
	public Resolution thesTermSearch() throws InfrastructureException, SessionException {

		if (GenericValidator.isBlankOrNull(term)) {
			throw new InfrastructureException(SystemErrorCodes.INVALID_ARGUMENT_SET_ERROR, "'term' must be present in request!");
		}

		context.getUserSession().getID();

		try {
			//
			// Build the term path (breadcrumb)
			//
			ThesaurusPath thespath = getPathFromSession();
			Properties props = new Properties();
			Enumeration<String> anenum = context.getRequest().getParameterNames();
			while (anenum.hasMoreElements()) {
				String key = (String) anenum.nextElement();
				String value = context.getRequest().getParameter(key);
				props.setProperty(key, value);
			}

			if (pageNumber == 0) {
				thespath.clearPathFrom(0);
				thespath.addStep(ThesaurusAction.SEARCH, term, props);
				pageNumber = 1; // Now set to 1 for display and calculations
			} else {

				if (snum != -1 && thespath.hasIndex(snum)) {
					thespath.clearPathFrom(snum);
				}
			}

			steps = thespath.getSteps();

			//
			// Build and run thesaurus search
			//
			term = Entity.prepareString(term.trim().replaceAll("^[\"\']", "").replaceAll("[\"\']$", ""));
			DatabaseConfig dConfig = DatabaseConfig.getInstance();
			Database database = dConfig.getDatabase(getDbName(this.database));
			ThesaurusQuery query = new ThesaurusQuery(database, term);
			query.compile();
			ThesaurusSearchControl sc = new ThesaurusSearchControl();
			ThesaurusSearchResult sr = sc.search(query, pageSize);
			int offset = ((pageNumber - 1) * pageSize) + 1;
			ThesaurusPage thesresults = sr.pageAt(offset);
			termsearchcount = sr.getHitCount();

			//
			// Get navigator and build page navigation
			//
			ThesSearchNavigator searchNavigator = new ThesSearchNavigator(pageSize, termsearchcount, pageNumber);
			pagenav = new PageNavigation();
			pagenav.setResultscount(searchNavigator.getDocCount());
			pagenav.setCurrentpage(pageNumber);
			pagenav.setResultsperpage(pageSize);

			//
			// Check the count - if zero, look for suggestions
			//
			if (termsearchcount == 0) {
				ThesaurusRecordBroker broker = new ThesaurusRecordBroker(database);
				ThesaurusPage thessuggest = broker.getSuggestions(term.toLowerCase(), pageSize);
				if (thessuggest.size() > 0) {
					// Build ThesaurusRecord list

					for (int idx = 0; idx < thessuggest.size(); idx++) {
						addTermsearchsuggest(thessuggest.get(idx));
					}
				}
			} else {
				// Build ThesaurusRecord list
				for (int idx = 0; idx < thesresults.size(); idx++) {
					addTermsearchresult(thesresults.get(idx));
				}
			}
		} catch (ThesaurusException e) {
			throw new InfrastructureException(SystemErrorCodes.THES_PROCESSING_ERROR, e);
		}

		// Return HTML
		return new ForwardResolution("/WEB-INF/pages/customer/search/ajax/thesTermSearch.jsp");
	}

	/**
	 * Thesaurus browse display (AJAX)
	 *
	 * @return Resolution
	 * @throws InfrastructureException
	 * @throws SessionException
	 * @throws ThesaurusException
	 * @throws InvalidArgumentException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ServletException
	 * @throws HistoryException
	 * @throws ClientCustomizerException
	 */
	@HandlesEvent("browse")
	@DontValidate
	public Resolution thesBrowse() throws InfrastructureException, SessionException {

		if (GenericValidator.isBlankOrNull(term)) {
			throw new InfrastructureException(SystemErrorCodes.INVALID_ARGUMENT_SET_ERROR, "'term' must be present in request!");
		}

		context.getUserSession().getID();

		ThesaurusPage browsepage;
		try {
			//
			// Build the term path (breadcrumb)
			//
			ThesaurusPath thespath = getPathFromSession();
			Properties props = new Properties();
			Enumeration<String> anenum = context.getRequest().getParameterNames();
			while (anenum.hasMoreElements()) {
				String key = (String) anenum.nextElement();
				String value = context.getRequest().getParameter(key);
				props.setProperty(key, value);
			}
			thespath.clearPathFrom(0);
			thespath.addStep(ThesaurusAction.BROWSE, term, props);
			steps = thespath.getSteps();

			//
			// Build the results
			//
			term = Entity.prepareString(term.trim().replaceAll("^[\"\']", "").replaceAll("[\"\']$", ""));
			DatabaseConfig dConfig = DatabaseConfig.getInstance();
			Database database = dConfig.getDatabase(getDbName(this.database));
			ThesaurusRecordBroker broker = new ThesaurusRecordBroker(database);
			browsepage = null;

			if (index == -1) { // initial request!

				ThesaurusRecordID recID = new ThesaurusRecordID(term, database);
				ThesaurusRecordID[] recIDs = new ThesaurusRecordID[1];
				recIDs[0] = recID;

				ThesaurusPage tpage = broker.buildPage(recIDs, 0, 0);
				if (tpage.get(0) == null) {
					tpage = broker.getAlphaStartingPoint(term.toLowerCase());
				}

				if (tpage.get(0) != null) {
					ThesaurusRecord trec = tpage.get(0);
					int recNumber = trec.getRecID().getRecordID();
					if (recNumber > 2) {
						nextindex = recNumber - 3;
					} else {
						nextindex = 0;
					}

					ThesaurusRecordID[] ids = new ThesaurusRecordID[pageSize];
					for (int i = 0; i < pageSize; i++) {
						ids[i] = new ThesaurusRecordID(nextindex, database);
						nextindex++;
					}
					browsepage = broker.buildPage(ids, 0, pageSize - 1);
				}

			} else { // Next/Prev index
				ThesaurusRecordID[] ids = new ThesaurusRecordID[pageSize];
				nextindex = index;
				for (int i = 0; i < pageSize; i++) {
					ids[i] = new ThesaurusRecordID(nextindex, database);
					nextindex++;
				}
				browsepage = broker.buildPage(ids, 0, pageSize - 1);

			}
		} catch (ThesaurusException e) {
			throw new InfrastructureException(SystemErrorCodes.THES_PROCESSING_ERROR, e);
		}

		//
		// Build page navigation
		//
		if ((nextindex - pageSize) == 0) {
			previndex = -1;
		} else {
			previndex = nextindex - (pageSize * 2);
			if (previndex < 0) {
				previndex = 0;
			}
		}

		//
		// Shortcut to the results
		//
		if (browsepage != null) {
			browseresults = browsepage.getRecords();
		}

		return new ForwardResolution("/WEB-INF/pages/customer/search/ajax/thesBrowse.jsp");
	}

	//
	//
	// GETTERS/SETTERS
	//
	//
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getTermsearchcount() {
		return termsearchcount;
	}

	public void setTermsearchcount(int termsearchcount) {
		this.termsearchcount = termsearchcount;
	}

	public List<ThesaurusStep> getSteps() {
		return steps;
	}

	public List<ThesaurusRecord> getTermsearchresults() {

		if (termsearchresults != null && termsearchresults.size() > 0)
			Collections.sort(termsearchresults, new ThesaurusTermComp());

		return termsearchresults;
	}

	public void addTermsearchresult(ThesaurusRecord record) {
		if (termsearchresults == null)
			termsearchresults = new ArrayList<ThesaurusRecord>();
		termsearchresults.add(record);
	}

	public List<ThesaurusRecord> getTermsearchsuggests() {
		if (termsearchsuggests != null && termsearchsuggests.size() > 0)
			Collections.sort(termsearchsuggests, new ThesaurusTermComp());

		return termsearchsuggests;
	}

	public void addTermsearchsuggest(ThesaurusRecord record) {
		if (termsearchsuggests == null)
			termsearchsuggests = new ArrayList<ThesaurusRecord>();
		termsearchsuggests.add(record);
	}

	public ThesaurusRecord getFullrecresult() {
		return fullrecresult;
	}

	public List<ThesaurusRecord> getBrowseresults() {
		if (browseresults != null && browseresults.size() > 0)
			// Collections.sort(browseresults,new ThesaurusTermComp());
			// //original

			// Hanan, fix terms with "*" come in order Jan 29, 2013
			Collections.sort(browseresults, new ThesaurusTermInfoComp());
		return browseresults;
	}

	public List<ThesaurusRecord> getFullrecresults() {
		if (fullrecresults != null && fullrecresults.size() > 0)
			Collections.sort(fullrecresults, new ThesaurusTermComp());
		return fullrecresults;
	}

	public PageNavigation getPagenav() {
		return pagenav;
	}

	public void setPagenav(PageNavigation pagenav) {
		this.pagenav = pagenav;
	}

	public int getSnum() {
		return snum;
	}

	public void setSnum(int snum) {
		this.snum = snum;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public boolean isFormSubmit() {
		return formSubmit;
	}

	public void setFormSubmit(boolean formSubmit) {
		this.formSubmit = formSubmit;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getPrevindex() {
		return previndex;
	}

	public int getNextindex() {
		return nextindex;
	}

}
