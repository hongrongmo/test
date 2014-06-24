package org.ei.stripes.action.results;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.BasketEntry;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBasket;
import org.ei.domain.FastSearchControl;
import org.ei.domain.InvalidArgumentException;
import org.ei.domain.Query;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchResult;
import org.ei.domain.Searches;
import org.ei.domain.personalization.IEVWebUser;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.query.base.FastQueryWriter;
import org.ei.session.UserSession;
import org.ei.stripes.action.search.BaseSearchAction;

/**
 * This class handles Ajax requests for Basket operations (add, delete)
 * 
 * @author harovetm
 * 
 */

@UrlBinding("/basket/{$event}.url")
public class BasketAjaxAction extends BaseSearchAction {

	private final static Logger log4j = Logger
			.getLogger(BasketAjaxAction.class);

	private boolean max;				// Flag for max doc selection
	private boolean all;				// Flag for clear entire basket (unmark ONLY)
	private String searchquery = null;	// The search query string
	private String[] docid;				// CSV list of DocIDs (single & page ONLY)
	private String[] handle;				// CSV list of handles (single & page ONLY)
	private int pagesize = 25;			// Current results page size
	
	private String alertid;				// ID of alert to be hidden

	private DocumentBasket basket;		// DocumentBasket object - NOT from request!

	/**
	 * Get the current basket count
	 * 
	 * @return Resolution
	 * @throws SessionException 
	 * @throws IOException 
	 * @throws InfrastructureException 
	 */
	@DontValidate
	@HandlesEvent("count")
	public Resolution count() throws SessionException, IOException, InfrastructureException {
		// Attempt to get the user's session
		UserSession usersession = context.getUserSession();
		if (usersession == null) {
			context.getResponse().sendError(500);
			return new StreamingResolution("application/json",
					"{\"status\":\"error\"}");
		}
		
		basket = new DocumentBasket(usersession.getID());
		return new StreamingResolution("application/json","{\"status\":\"success\",\"count\":\"" + basket.getBasketSize() + "\"}");
	}
	
	/**
	 * Clear the warning for deleting a basket
	 * 
	 * @return Resolution
	 * @throws SessionException 
	 * @throws IOException 
	 */
	@DontValidate
	@HandlesEvent("clearalert")
	public Resolution clearalert() throws SessionException, IOException {
		// Attempt to get the user's session
		UserSession usersession = context.getUserSession();
		if (usersession == null) {
			context.getResponse().sendError(500);
			return new StreamingResolution("application/json",
					"{\"status\":\"error\"}");
		}
		// Check for various flags being set:
		// "showmaxalert" - flag for showing message with Max, basket empty
		// "showmaxalertclear" - flag for showing message with Max, basket with items
		// "shownewsrchalert" - flag for showing message with delete all records button
		if ("showmaxalert".equals(alertid)) {
			usersession.setProperty(UserSession.SHOW_MAX_ALERT,"false");
			context.updateUserSession(usersession);
		} else if ("showmaxalertclear".equals(alertid)) {
				usersession.setProperty(UserSession.SHOW_MAX_ALERTCLEAR,"false");
				context.updateUserSession(usersession);
		} else if ("shownewsrchalert".equals(alertid)) {
			usersession.setProperty(UserSession.SHOW_NEWSEARCH_ALERT,"false");
			context.updateUserSession(usersession);
		}
		return new StreamingResolution("application/json","{\"status\":\"success\"}");
	}
	

	/**
	 * Add records to the current basket.  There are 3 types of adds:
	 * 
	 * Single - a single entry will be present in the docid/handle arrays
	 * Page - multiple entries will be present in the docid/handle arrays
	 * Max - clear entire basket and add up to 500 results from current search
	 * 
	 * @return Resolution
	 * @throws IOException 
	 */
	@DontValidate
	@HandlesEvent("mark")
	public Resolution mark() throws IOException {
		
		List<BasketEntry> listOfBasketEntries = new ArrayList<BasketEntry>();
		DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
		
		UserSession usersession = context.getUserSession();
		if (usersession == null || usersession.getSessionID() == null) {
			log4j.error("No user session available!");
			context.getResponse().sendError(500);
			return new StreamingResolution("application/json",
					"{\"status\":\"error\"}");
		}
		IEVWebUser user = usersession.getUser();

		// Ensure we have valid request parameters
		if (GenericValidator.isBlankOrNull(searchid)
				|| GenericValidator.isBlankOrNull(usersession.getID())
				|| GenericValidator.isBlankOrNull(searchquery)) {
			// TODO handle with Stripes validation??
			log4j.warn("Missing request parameters");
			context.getResponse().sendError(500);
			return new StreamingResolution("application/json",
					"{\"status\":\"error\"}");
		}

		log4j.info("Mark request, session ID: " + usersession.getID() + ", searchid: " + searchid + ", max=" + max + ", docid=" + docid);

		String message = null;
		try {

			basket = new DocumentBasket(usersession.getID(), searchid);

			// Build a Query object
			Query query = new Query();
			query.setSearchQuery(searchquery);
			query.setDisplayQuery(searchquery);
			query.setID(searchid);

			if (max) {
				// First, clear out ALL basket entries from current search and get
				// basket size
				basket.removeAll();
				basket.getBasketSize();

				// Now see how many doc IDs we need to fetch to fill up basket
				List<String> listOfHandles = new ArrayList<String>();
				for (int i = 1; i <= DocumentBasket.maxSize; i++) {
					listOfHandles.add(String.valueOf(i));
				}

				// Run the search
				SearchResult result = null;
				SearchControl sc = new FastSearchControl();
				query = Searches.getSearch(searchid);
				query.setSearchQueryWriter(new FastQueryWriter());
				query.setDatabaseConfig(databaseConfig);
				query.setCredentials(user.getCartridge());

				result = sc.openSearch(query, usersession.getID(), DocumentBasket.maxSize, true);

				// Add returned DocIDs to list
				List<DocID> listOfDocIds = result.getDocIDRange(1, DocumentBasket.maxSize);
				for (DocID d : listOfDocIds) {
					listOfBasketEntries.add(new BasketEntry(d, query));
				}

				// Fill the basket!
				basket.addAll(listOfBasketEntries);
			} else {
				// Sometimes the page may not have the correct basket count.
				int basketsize = basket.getBasketSize();
				if (basketsize == DocumentBasket.maxSize) {
					String err = "<p>You have already selected a maximum of " + DocumentBasket.maxSize + " records.  You can view all currently selected records by clicking \'Selected Records\'.</p>";
					log4j.warn("Basket count is already at max!");
					return new StreamingResolution("application/json",
							"{\"status\":\"error\", \"count\":\"" + basketsize + "\", \"message\":\"" + err + "\"}");
				} else if (basketsize + docid.length > DocumentBasket.maxSize) {
					String err = "<p>Selecting the current page would exceed the maximum of " + DocumentBasket.maxSize + " records, so no records will be selected.  You can view all currently selected records by clicking \'Selected Records\'.</p>";
					log4j.warn("Adding current docids will exceed basket limit!");
					return new StreamingResolution("application/json",
							"{\"status\":\"error\", \"count\":\"" + basketsize + "\", \"message\":\"" + err + "\"}");
				}
				// This is either a single doc request or page request, either
				// way we are sent a list of doc IDs
				if (docid == null || docid.length == 0 || handle == null || handle.length == 0) {
					log4j.error("Either docid or handle list is empty!");
					context.getResponse().sendError(500);
					return new StreamingResolution("application/json",
							"{\"status\":\"error\"}");
				}
				DocID docId;
				for (int i=0; i<docid.length; i++) {
					docId = new DocID(Integer.parseInt(handle[i]),docid[i].trim(),databaseConfig.getDatabase(docid[i].substring(0,3)));
					listOfBasketEntries.add(new BasketEntry(docId, query));
				}
				basket.addAll(listOfBasketEntries);
			}

			return new StreamingResolution("application/json",
					"{\"status\":\"success\"" + 
							",\"count\":\"" + basket.getBasketSize() + "\"" + 
							(message != null ? ",\"message\":\"" + message + "\"" : "") + 
					"}");

		} catch (Exception e) {
			log4j.error("Unable to create process basket mark request", e);
			context.getResponse().sendError(500);
			return new StreamingResolution("application/json",
					"{\"status\":\"error\"}");
		}

	}

	/**
	 * Remove records from the current basket.  There are 4 types:
	 * 
	 * Single - a single entry will be present in the docid/handle arrays
	 * Page - multiple entries will be present in the docid/handle arrays
	 * Max - clear basket for CURRENT search
	 * All - clear entire basket
	 * 
	 * @return Resolution
	 * @throws IOException 
	 */
	@DontValidate
	@HandlesEvent("unmark")
	public Resolution unmark() throws IOException {

		new ArrayList<BasketEntry>();
		DatabaseConfig.getInstance();

		UserSession usersession = context.getUserSession();
		if (usersession == null || usersession.getSessionID() == null) {
			log4j.error("No user session available!");
			context.getResponse().sendError(500);
			return new StreamingResolution("application/json",
					"{\"status\":\"error\"}");
		}
		usersession.getUser();

		// Ensure we have valid request parameters
		if (GenericValidator.isBlankOrNull(searchid)
				|| GenericValidator.isBlankOrNull(usersession.getID())) {
			// TODO handle with Stripes validation??
			log4j.warn("Missing request parameters");
			context.getResponse().sendError(500);
			return new StreamingResolution("application/json",
					"{\"status\":\"error\"}");
		}

		log4j.info("Unmark request, session ID: " + usersession.getID() + ", searchid: " + searchid + ", max=" + max + ", docid=" + docid);

		try {
			if (!removeFromBasket(usersession) || basket == null) {
				context.getResponse().sendError(500);
				return new StreamingResolution("application/json",
						"{\"status\":\"error\"}");
			} else {
				return new StreamingResolution("application/json",
						"{\"status\":\"success\",\"count\":\""
								+ basket.getBasketSize() + "\"}");
			}
		} catch (Exception e) {
			log4j.error("Unable to create process basket unmark request", e);
			context.getResponse().sendError(500);
			return new StreamingResolution("application/json",
					"{\"status\":\"error\"}");
		}
	}

	/**
	 * Remove one or more items from the current basket.  This is called
	 * by both the Ajax and non-Ajax handlers above
	 * 
	 * @param usersession
	 * @return true/false if remove was successful
	 * @throws InvalidArgumentException
	 * @throws InfrastructureException
	 */
	private boolean removeFromBasket(UserSession usersession) throws InvalidArgumentException, InfrastructureException {
		List<BasketEntry> listOfBasketEntries = new ArrayList<BasketEntry>();
		DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

		// Build a Query object
		Query query = new Query();
		query.setSearchQuery(searchquery);
		query.setDisplayQuery(searchquery);
		query.setID(searchid);

		boolean success = true;
		
		// Request is to remove all items in basket
		if (all) {
			basket = new DocumentBasket(usersession.getID());
			success = basket.removeAll();
		// Request is to remove all items for CURRENT search
		} else if (max) {
			basket = new DocumentBasket(usersession.getID(), searchid);
			success = basket.removeAllSearch();
		// This is either a single doc request or page request, either
		// way we are sent a list of doc IDs
		} else {
			basket = new DocumentBasket(usersession.getID());
			//String docids[] = docid.split(",");
			//String handles[] = handle.split(",");
			if (docid == null || docid.length == 0 || handle == null || handle.length == 0) {
				log4j.error("Either docid or handle list is empty!");
				throw new InfrastructureException(SystemErrorCodes.BASKET_ERROR, "Either docid or handle list is empty!");
			}
			DocID docId;
			for (int i = 0; i < docid.length; i++) {
				docId = new DocID(
						Integer.parseInt(handle[i]),
						docid[i].trim(),
						databaseConfig.getDatabase(docid[i].substring(0, 3)));
				listOfBasketEntries.add(new BasketEntry(docId, query));
			}
			return basket.removeSelected(listOfBasketEntries);

		}

		return success;
	}

	//
	//
	// GETTERS/SETTERS
	//
	//
	
	public String getsearchquery() {
		return searchquery;
	}

	public void setsearchquery(String searchquery) {
		this.searchquery = searchquery;
	}

	public String[] getDocid() {
		return docid;
	}

	public void setDocid(String[] docid) {
		this.docid = docid;
	}

	public String[] getHandle() {
		return handle;
	}

	public void setHandle(String[] handle) {
		this.handle = handle;
	}

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	public boolean isMax() {
		return max;
	}

	public void setMax(boolean max) {
		this.max = max;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}


	public String getAlertid() {
		return alertid;
	}


	public void setAlertid(String alertid) {
		this.alertid = alertid;
	}
}
