package org.ei.domain.personalization;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.ei.config.ApplicationProperties;
import org.ei.exception.InfrastructureException;

/**
 * The saved searches and alerts object.
 *
 * @author harovetm
 *
 */
public class SavedSearchesAndAlerts {

	public static String SAVE_SEARCH_REQUEST = "SavedSearch";
	public static String CREATE_ALERT_REQUEST = "EmailAlert";
	public static String MARK = "mark";
	public static String UNMARK = "unmark";

	private int savedSearchCount;
	private int savedAlertCount;
	List<SearchHistory> savedSearchList = new ArrayList<SearchHistory>();
	List<SearchHistory> savedAlertList = new ArrayList<SearchHistory>();

	/**
	 * Build a SavedSearchesAndAlerts object using userId
	 *
	 * @return SavedSearchesAndAlerts object
	 * @throws InfrastructureException
	 */
	public static SavedSearchesAndAlerts build(String userId) throws InfrastructureException {
		if (GenericValidator.isBlankOrNull(userId))
			return null;
		SavedSearchesAndAlerts ssaa = SavedSearches.getUserSavedSearchesAndAlerts(userId);
		return ssaa;
	}

	/**
	 * Get the limit for Saved Searches and Alerts
	 *
	 * @return
	 */
	public int getLimit() {
		return Integer.parseInt(ApplicationProperties.getInstance().getProperty(ApplicationProperties.SAVED_SERCHES_ALERTS_LIMIT));
	}

	/**
	 * Used by XSLT transforms to add history object to alert list
	 *
	 * @param searchHistory
	 */
	public void addSavedAlert(SearchHistory searchHistory) {
		getSavedAlertList().add(searchHistory);
	}

	/**
	 * Used by XSLT transforms to add history object to saved searc list
	 *
	 * @param searchHistory
	 */
	public void addSavedsearch(SearchHistory searchHistory) {
		getSavedSearchList().add(searchHistory);
	}

	/**
	 * Return total count of saved searches and alerts
	 *
	 * @return
	 */
	public int getTotalCount() {
		return (getSavedSearchCount() + getSavedAlertCount());
	}

	//
	//
	// GETTERS / SETTERS
	//
	//

	public int getSavedSearchCount() {
		return savedSearchCount;
	}

	public void setSavedSearchCount(int savedSearchCount) {
		this.savedSearchCount = savedSearchCount;
	}

	public int getSavedAlertCount() {
		return savedAlertCount;
	}

	public void setSavedAlertCount(int savedAlertCount) {
		this.savedAlertCount = savedAlertCount;
	}

	public List<SearchHistory> getSavedSearchList() {
		return savedSearchList;
	}

	public void setSavedSearchList(List<SearchHistory> savedsearchList) {
		this.savedSearchList = savedsearchList;
	}

	public List<SearchHistory> getSavedAlertList() {
		return savedAlertList;
	}

	public void setSavedAlertList(List<SearchHistory> savedAlertList) {
		this.savedAlertList = savedAlertList;
	}

}
