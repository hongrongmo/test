package org.ei.stripes.action.results;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.BasketEntry;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBasket;
import org.ei.session.UserSession;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.action.search.BaseSearchAction;

/**
 * This is the base class for the selected records results handling.
 * 
 * PLEASE NOTE
 * It can be invoked but ***ONLY*** for zero results searches!
 * @see processModelXML method below for how to handle this.
 * 
 * @author harovetm
 *
 */
@UrlBinding("/selected/delete.url")
public class SelectedRecordsDeleteAction extends BaseSearchAction {

	private final static Logger log4j = Logger.getLogger(SelectedRecordsDeleteAction.class);

	// Doc id(s) being deleted.  Can also be "all" to remove all
	protected String docid;

	// Doc handle
	protected int handle;
	
	// The CID value to re-run the search
	protected String reruncid;
	
    // These variables to hold search words and search options
	protected int resultscount;
	protected int resultsperpage;
    protected String selectoption;
    
    protected String searchtype="";
    protected int compmask;

    // These links are passed in from a results page  
    protected String searchresults;
    protected String newsearch;
    
	//Index to go back to Search Results
	protected int backindex;
	
	// Basket size
	protected int basketsize;
	
	/**
	 * Delete all current records
	 * 
	 * @return Resolution
	 * @throws UnsupportedEncodingException 
	 */
	@DefaultHandler
	@DontValidate
	public Resolution delete() throws UnsupportedEncodingException {
		int pageIndex = -1;
		
		// Ensure we have a user session
		UserSession usersession = context.getUserSession();
		if (usersession == null || usersession.getSessionID() == null) {
			log4j.warn("No user session available!");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}
		
		// Ensure we have valid request parameters
		if (GenericValidator.isBlankOrNull(docid)) {
			log4j.warn("Missing request parameters");
			return new RedirectResolution("/system/error.url");
		}
		
		String redirect;
		// Remove all records from basket
		DocumentBasket documentBasket;
		try {
			String sessionid = usersession.getID().toString();
			documentBasket = new DocumentBasket(sessionid);
			//List docids = documentBasket.getAllDocIDs();
			if (docid.equals("all")) {
				documentBasket.removeAll();
				redirect = "/selected/deleteall.url?CID=errorSelectedSet";
			} else {
				DocID doc = new DocID(handle, docid.trim(),
						(DatabaseConfig.getInstance()).getDatabase(docid
								.substring(0, 3)));
				BasketEntry basEntry = new BasketEntry();
				basEntry.setDocID(doc);

				List<BasketEntry> listOfDocBasHandles = new ArrayList<BasketEntry>();
				listOfDocBasHandles.add(basEntry);
				documentBasket.removeSelected(listOfDocBasHandles);

				if (basketsize == 0) {
					pageIndex = 1;
				}else{
					if ((handle % basketsize) == 0) {
						pageIndex = (handle / basketsize);
					} else {
						pageIndex = (handle / basketsize) + 1;
					}
				}

				int basketCount = documentBasket.getBasketSize();

				/*if (basketCount != 1) {
					basketCount--;
				}*/

				int totalPages = -1;
				if (basketsize == 0) {
					totalPages = 1;
				}else{
					if ((basketCount % basketsize) == 0) {
						totalPages = (basketCount / basketsize);
					} else {
						totalPages = (basketCount / basketsize) + 1;
					}
				}

				if (pageIndex > totalPages) {
					pageIndex = totalPages;
				}
				
				if(reruncid!=null && reruncid.contains("abstract")){
					redirect = 	"/selected/abstract.url";
				}else if(reruncid!=null && reruncid.contains("citation")){
					redirect = 	"/selected/citation.url";
				}else{
					redirect = 	"/selected/detailed.url";
				}
				
				redirect = redirect + "?CID=" + reruncid;
				System.out.println("redirect "+ redirect);
			}
		} catch (Exception e) {
			log4j.error("Unable to retrieve document basket", e);
			return new RedirectResolution("/system/error.url");
		}
		
		// Redirect to display page
		redirect += "&DATABASEID=" + database
				+ "&COUNT=" + resultscount
				+ "&backindex=" + backindex;
		if (pageIndex > 0) {
			redirect += "&BASKETCOUNT=" + pageIndex;
		}
		if (!GenericValidator.isBlankOrNull(searchid)) {
			redirect += "&SEARCHID=" + searchid;
		}
		if (!GenericValidator.isBlankOrNull(searchtype)) {
			redirect += "&SEARCHTYPE=" + searchtype;
		}
		if (!GenericValidator.isBlankOrNull(newsearch)) {
			redirect += "&newsearch=" + URLEncoder.encode(newsearch,"UTF-8");
		}
		if (!GenericValidator.isBlankOrNull(searchresults)) {
			redirect += "&searchresults=" + URLEncoder.encode(searchresults,"UTF-8");
		}
	
		if(reruncid==null && !docid.equals("all")){
			return null;
		}else{
			return new RedirectResolution(redirect);
		}
		
	}


	//
	//
	// GETTERS/SETTERS
	//
	//
	
	public String getDocid() {
		return docid;
	}

	public void setDocid(String docid) {
		this.docid = docid;
	}

	public String getReruncid() {
		return reruncid;
	}

	public void setReruncid(String reruncid) {
		this.reruncid = reruncid;
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

	public String getSearchtype() {
		return searchtype;
	}

	public void setSearchtype(String searchtype) {
		this.searchtype = searchtype;
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

	public int getBackindex() {
		return backindex;
	}

	public void setBackindex(int backindex) {
		this.backindex = backindex;
	}


	public int getHandle() {
		return handle;
	}


	public void setHandle(int handle) {
		this.handle = handle;
	}


	public int getBasketsize() {
		return basketsize;
	}


	public void setBasketsize(int basketsize) {
		this.basketsize = basketsize;
	}
	
}
