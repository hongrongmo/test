package org.ei.stripes.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.IndividualAuthRequiredAccessControl;
import org.ei.domain.Searches;
import org.ei.domain.personalization.SavedSearchesAndAlerts;
import org.ei.domain.personalization.SearchHistory;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.UserSession;
import org.ei.stripes.action.BackUrlAction.BackURLByFeature;
import org.ei.stripes.action.personalaccount.IPersonalLogin;


@UrlBinding("/personal/savedsearch/adddelete.url")
public class AddDeleteSavedSearchAction extends EVActionBean implements
		IPersonalLogin {

	private final static Logger log4j = Logger.getLogger(AddDeleteSavedSearchAction.class);
    private String searchid;
    private String option;
    private String selectvalue;
	private boolean ajax;
	private String history;

	/**
	 * Override for the ISecuredAction interface.  This ActionBean
	 * requires individual authentication!
	 */
	@Override
	public IAccessControl getAccessControl() {
		return new IndividualAuthRequiredAccessControl();
	}


	/**
	 * Default handler to add to list of saved searches/alerts
	 *
	 * @return
	 * @throws InfrastructureException
	 * @throws HistoryException
	 * @throws UnsupportedEncodingException
	 */
	@DefaultHandler
	@DontValidate
	public Resolution addDeleteSavedSearch() throws InfrastructureException  {

		log4j.info("Starting add Delete Saved Search display...");
		setRoom(ROOM.mysettings);

		// Search ID parameter is required
		if(GenericValidator.isBlankOrNull(searchid)) {
			log4j.warn("No SearchID value");
			if (!ajax) {
				return new ForwardResolution(SystemMessage.SYSTEM_ERROR_URL + "?message=Search ID is required.");
			} else {
				return new StreamingResolution("application/json","{\"status\":\"error\",\"message\":\"Search ID is required.\"}");
			}
		}

		// Get the user session
		UserSession ussession = context.getUserSession();
		if (ussession == null) {
			log4j.warn("No UserSession object available!");
			if (!ajax) {
				return SystemMessage.SYSTEM_ERROR_RESOLUTION;
			} else {
				return new StreamingResolution("application/json","{\"status\":\"error\",\"message\":\"Unable to process request.\"}");
			}
		}


		// Build the SavedSearchesAndAlerts object
		String userId = ussession.getUser().getUserInfo().getUserId();
		SavedSearchesAndAlerts savedSearchesAndAlerts = SavedSearchesAndAlerts.build(userId);
		if (savedSearchesAndAlerts == null) {
			log4j.warn("No saved searches or alerts could be found for user ID: " + userId + ".");
			if (!ajax) {
				return SystemMessage.SYSTEM_ERROR_RESOLUTION;
			} else {
				return new StreamingResolution("application/json","{\"status\":\"error\",\"message\":\"Unable to process request.\"}");
			}
		}

		// Process the request - mark or unmark
		int limit = savedSearchesAndAlerts.getLimit();
		if (isUNMarkRequest()) {
			if (isSavedSearchRequest()) {
				Searches.removeSavedSearch(getSearchid(), userId);
			} else if (isEmailAlertRequest()) {
				Searches.removeEmailAlertSearch(getSearchid(), userId);
			}
		} else {
			try {
				// Ensure we are below the limit
				if (!(isSearchAlreadyExistsAsSavedSearch(savedSearchesAndAlerts, getSearchid()) &&
					 isEmailAlertRequest())) {

					if (savedSearchesAndAlerts.getTotalCount() >= limit) {
						log4j.warn("Attempt to add alert above limit (" + savedSearchesAndAlerts.getLimit() + ").");
						if (!GenericValidator.isBlankOrNull(getBackurl())) {
							String backurlWithLimitError = getBackurl() + "&limitError=true";
							if (!ajax) {
								return new RedirectResolution(
										URLDecoder.decode(backurlWithLimitError,
														"UTF-8"), false);
							} else {
								return new StreamingResolution("application/json","{\"status\":\"limit\"}");
							}
						} else {
							if (!ajax) {
								return new RedirectResolution(SystemMessage.SYSTEM_ERROR_URL + "?message=" +
										URLEncoder.encode("Unable to add your Saved Search or Alert.", "utf-8"));
							} else {
								return new StreamingResolution("application/json","{\"status\":\"limit\"}");
							}
						}
					}
				}
			} catch (UnsupportedEncodingException e) {
				throw new InfrastructureException(SystemErrorCodes.SAVED_SEARCH_ERROR, e);
			}

			// If we make it here, it's OK to add saved search/alert
			if (isEmailAlertRequest()) {
				Searches.addEmailAlertSearch(getSearchid(), userId);
			} else if (isSavedSearchRequest()) {
				Searches.addSavedSearch(getSearchid(), userId);
			}
		}

		// Return appropriately
		if (!ajax) {
		    return new RedirectResolution(BackUrlAction.getStoredURLByFeature(context.getRequest(), BackURLByFeature.SAVEDSEARCH, true), false);
		} else {
			return new StreamingResolution("application/json","{\"status\":\"success\"}");
		}

	}

	/**
	 * Determine if request is for Email Alert
	 * @return
	 */
	private boolean isEmailAlertRequest() {
		return SavedSearchesAndAlerts.CREATE_ALERT_REQUEST.equals(getOption());
	}

	/**
	 * Determine if request is for Saved Search
	 * @return
	 */
	private boolean isSavedSearchRequest() {
		return SavedSearchesAndAlerts.SAVE_SEARCH_REQUEST.equals(getOption());
	}


	/**
	 * Determine if request is to unmark item
	 * @return
	 */
	private boolean isUNMarkRequest() {
		return !GenericValidator.isBlankOrNull(getSelectvalue()) && SavedSearchesAndAlerts.UNMARK.equals(getSelectvalue());
	}

	/**
	 * See if search already exists in database
	 *
	 * @param savedSearchesAndAlerts
	 * @param searchId
	 * @return
	 */
	private boolean isSearchAlreadyExistsAsSavedSearch(SavedSearchesAndAlerts savedSearchesAndAlerts, String searchId) {
		if(!savedSearchesAndAlerts.getSavedSearchList().isEmpty()){
			for(SearchHistory savedSearch: savedSearchesAndAlerts.getSavedSearchList()){
				if(searchId.equalsIgnoreCase(savedSearch.getQueryid())){
					return true;
				}
			}
		}

		 return false;
	}


	/**
	 * Returns a Resolution object for the login page
	 */
	@Override
	public String getLoginNextUrl() {

		// This method gets called BEFORE binding and validation happens
		// in Stripes so we have to get the request parms
		HttpServletRequest request = context.getRequest();
		backurl = request.getParameter("backurl");
		database = request.getParameter("database");
		option = request.getParameter("option");
		selectvalue = request.getParameter("selectvalue");
		searchid = request.getParameter("searchid");
		history = request.getParameter("history");

		return "/personal/savedsearch/adddelete.url?database=" + database +
					"&searchid=" + searchid +
					"&option=" + option +
					"&selectvalue=" + selectvalue;
	}

    @Override
    public String getLoginCancelUrl() {
    	return getBackurl();
    }

	//
	//
	// GETTERS / SETTERS
	//
	//

	public String getSelectvalue() {
		return selectvalue;
	}

	public void setSelectvalue(String selectvalue) {
		this.selectvalue = selectvalue;
	}

	public String getSearchid() {
		return searchid;
	}

	public void setSearchid(String searchid) {
		this.searchid = searchid;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public boolean isAjax() {
		return ajax;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

}
