package org.ei.stripes.action;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.biz.access.AccessException;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.personalization.UserProfile;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.IndividualAuthRequiredAccessControl;
import org.ei.domain.Searches;
import org.ei.domain.personalization.SavedSearches;
import org.ei.domain.personalization.SavedSearchesAndAlerts;
import org.ei.domain.personalization.SearchHistory;
import org.ei.exception.InfrastructureException;
import org.ei.session.UserPreferences;
import org.ei.session.UserSession;
import org.ei.stripes.action.personalaccount.IPersonalLogin;

@UrlBinding("/personal/savesearch/{$event}.url")
public class SavedSearchesAndAlertsAction extends EVActionBean implements IPersonalLogin {

	private final static Logger log4j = Logger.getLogger(SavedSearchesAndAlertsAction.class);

	public static final String SAVESEARCH_DISPLAY_URL = "/personal/savesearch/display.url";
	public static final String SAVESEARCH_DELETE_URL = "/personal/savesearch/delete.url";
	public static final String SAVESEARCH_ADD_URL = "/personal/savesearch/add.url";

	private String searchid;
	private boolean ajax;
	private String[] alertcbx;

	private List<SearchHistory> savedSearches = new ArrayList<SearchHistory>();
	private List<SearchHistory> savedAlerts = new ArrayList<SearchHistory>();
	private boolean emailAlertsPresent;
	private boolean ccEmailAlertsPresent;
	private int savedSeachesCount;
	private int savedAlertsCount;

	private UserSession usersession = null;

	/**
	 * Override for the ISecuredAction interface. This ActionBean requires
	 * individual authentication!
	 */
	@Override
	public IAccessControl getAccessControl() {
		return new IndividualAuthRequiredAccessControl();
	}

	/**
	 * Do all event validation here.
	 *
	 * @return
	 */
	@Before
	public Resolution validate() {
		usersession = context.getUserSession();
		if (usersession == null) {
			log4j.warn("No UserSession object available!");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}
		return null;
	}

	/**
	 * Displays the main viewSavedSearchesAndAlert page -
	 *
	 * @return Resolution
	 * @throws InfrastructureException
	 * @throws AccessException
	 */
	@DefaultHandler
	@DontValidate
	public Resolution main() throws InfrastructureException, AccessException {

		log4j.info("Starting saved searches display...");
		setRoom(ROOM.mysettings);
		UserProfile user = usersession.getUser().getUserInfo();

		String userId = "";
		if (user != null) {
			userId = user.getUserId();
		}

		setUserEmailAlertAndCCemailAlert(usersession.getUser());
		getUserSavedSearchesAndAlerts(userId);

		return new ForwardResolution("/WEB-INF/pages/customer/settings/savedSeachesAndAlerts.jsp");
	}

	/**
	 * Handles deleting all searches from saved search/alert page.
	 *
	 * @return
	 * @throws ClientCustomizerException
	 * @throws InfrastructureException
	 * @throws AccessException
	 */
	@HandlesEvent("DeleteAllSearches")
	@DontValidate
	public Resolution deleteallsearches() throws InfrastructureException {
		log4j.info("Starting deleteallsearches event...");
		setRoom(ROOM.mysettings);

		UserProfile user = usersession.getUser().getUserInfo();

		String userId = "";
		if (user != null) {
			userId = user.getUserId();
		}

		// Retrieve saved searches and delete one at a time
		SavedSearchesAndAlerts savedSearchesAndAlerts = getUserSearchesAndAlerts(userId);
		for (SearchHistory searchHistory : savedSearchesAndAlerts.getSavedSearchList()) {
			Searches.removeSavedSearch(searchHistory.getQueryid(), userId);
		}

		// Go back to main display
		return new RedirectResolution(SAVESEARCH_DISPLAY_URL);
	}

	/**
	 * Handles adding an alert from the saved searches/alert page
	 *
	 * @return
	 * @throws InfrastructureException
	 * @throws AccessException
	 */
	@HandlesEvent("AddAlerts")
	@DontValidate
	public Resolution addalerts() throws InfrastructureException, AccessException {
		log4j.info("Starting saved searches addalerts event...");
		setRoom(ROOM.mysettings);

		UserProfile user = usersession.getUser().getUserInfo();

		String userId = "";
		if (user != null) {
			userId = user.getUserId();
		}

		for (String searchId : alertcbx) {
			Searches.addEmailAlertSearch(searchId, userId);
		}

		// Go back to main display
		return new RedirectResolution(SAVESEARCH_DISPLAY_URL);
	}

	/**
	 * Deletes all alerts
	 *
	 * @return
	 * @throws InfrastructureException
	 * @throws AccessException
	 */
	@HandlesEvent("DeleteAllAlerts")
	@DontValidate
	public Resolution deleteallalerts() throws InfrastructureException, AccessException {
		log4j.info("Starting saved searches deleteallalerts event...");
		setRoom(ROOM.mysettings);

		UserProfile user = usersession.getUser().getUserInfo();

		String userId = "";
		if (user != null) {
			userId = user.getUserId();
		}

		// Retrieve saved searches and delete one at a time
		SavedSearchesAndAlerts savedSearchesAndAlerts = getUserSearchesAndAlerts(userId);
		for (SearchHistory searchHistory : savedSearchesAndAlerts.getSavedAlertList()) {
			Searches.removeSavedSearch(searchHistory.getQueryid(), userId);
		}

		// Go back to main display
		return new RedirectResolution(SAVESEARCH_DISPLAY_URL);
	}

	/**
	 * Reverts current alerts back to saved searches.
	 *
	 * @return
	 * @throws InfrastructureException
	 * @throws AccessException
	 */
	@HandlesEvent("ClearAlerts")
	@DontValidate
	public Resolution clearalerts() throws InfrastructureException, AccessException {
		log4j.info("Starting saved searches clearalerts event...");
		setRoom(ROOM.mysettings);

		UserProfile user = usersession.getUser().getUserInfo();

		String userId = "";
		if (user != null) {
			userId = user.getUserId();
		}
		// Get searches to make into alerts from emailcbx parm
		for (String searchId : alertcbx) {
			Searches.removeEmailAlertSearch(searchId, userId);
		}

		// Go back to main display
		return new RedirectResolution(SAVESEARCH_DISPLAY_URL);
	}

	/**
	 * Delete all saved searches or alerts
	 *
	 * @return
	 * @throws ClientCustomizerException
	 * @throws InfrastructureException
	 * @throws AccessException
	 */
	@HandlesEvent("DeleteSearch")
	@DontValidate
	public Resolution deletesearch() throws InfrastructureException, AccessException {

		log4j.info("Starting deletesearch event...");
		setRoom(ROOM.mysettings);

		UserProfile user = usersession.getUser().getUserInfo();

		String userId = "";
		if (user != null) {
			userId = user.getUserId();
		}

		Searches.removeSavedSearch(searchid, userId);

		return new RedirectResolution(SAVESEARCH_DISPLAY_URL);
	}

	/**
	 * Common method to set the Email Alerts flags
	 *
	 * @param user
	 * @throws AccessException
	 */
	private void setUserEmailAlertAndCCemailAlert(IEVWebUser user) throws AccessException {
		UserPreferences prefs = (UserPreferences) user.getUserPreferences();
		emailAlertsPresent = true; // TODO is this right??? -
									// prefs.getBoolean(UserPreferences.FENCE_EMAIL_ALERTS);
		ccEmailAlertsPresent = prefs.getBoolean(UserPreferences.FENCE_EMAIL_ALERTS_CC);
	}

	/**
	 * Common method to retrieve the user's saved searches and alerts
	 *
	 * @param userId
	 * @throws InfrastructureException
	 */
	private void getUserSavedSearchesAndAlerts(String userId) throws InfrastructureException {
		SavedSearchesAndAlerts savedSearchesAndAlerts = getUserSearchesAndAlerts(userId);
		setSavedSearches(savedSearchesAndAlerts.getSavedSearchList());
		setSavedAlerts(savedSearchesAndAlerts.getSavedAlertList());
		setSavedSeachesCount(savedSearchesAndAlerts.getSavedSearchCount());
		setSavedAlertsCount(savedSearchesAndAlerts.getSavedAlertCount());
	}

	/**
	 * Returns a Resolution object for the login page
	 */
	@Override
	public String getLoginNextUrl() {
		return SAVESEARCH_DISPLAY_URL;
	}

	@Override
	public String getLoginCancelUrl() {
		return getBackurl();
	}

	private SavedSearchesAndAlerts getUserSearchesAndAlerts(String userId) throws InfrastructureException {
		return SavedSearches.getUserSavedSearchesAndAlerts(userId);
	}

	public List<SearchHistory> getSavedSearches() {
		return savedSearches;
	}

	public void setSavedSearches(List<SearchHistory> savedSearches) {
		this.savedSearches = savedSearches;
	}

	public boolean getEmailAlertsPresent() {
		return emailAlertsPresent;
	}

	public void setEmailAlertsPresent(boolean emailAlertsPresent) {
		this.emailAlertsPresent = emailAlertsPresent;
	}

	public boolean getCcEmailAlertsPresent() {
		return ccEmailAlertsPresent;
	}

	public void setCcEmailAlertsPresent(boolean ccEmailAlertsPresent) {
		this.ccEmailAlertsPresent = ccEmailAlertsPresent;
	}

	public int getSavedSeachesCount() {
		return savedSeachesCount;
	}

	public void setSavedSeachesCount(int savedSeachesCount) {
		this.savedSeachesCount = savedSeachesCount;
	}

	public int getSavedAlertsCount() {
		return savedAlertsCount;
	}

	public void setSavedAlertsCount(int savedAlertsCount) {
		this.savedAlertsCount = savedAlertsCount;
	}

	public List<SearchHistory> getSavedAlerts() {
		return savedAlerts;
	}

	public void setSavedAlerts(List<SearchHistory> savedAlerts) {
		this.savedAlerts = savedAlerts;
	}

	public String getSearchid() {
		return searchid;
	}

	public void setSearchid(String searchid) {
		this.searchid = searchid;
	}

	public boolean isAjax() {
		return ajax;
	}

	public void setAjax(boolean ajax) {
		this.ajax = ajax;
	}

	public String[] getAlertcbx() {
		return alertcbx;
	}

	public void setAlertcbx(String[] alertcbx) {
		this.alertcbx = alertcbx;
	}

}
