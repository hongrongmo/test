package org.ei.stripes.action.search;

import java.io.UnsupportedEncodingException;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.Searches;
import org.ei.exception.InfrastructureException;
import org.ei.stripes.action.BackUrlAction;
import org.ei.stripes.action.BackUrlAction.BackURLByFeature;
import org.ei.stripes.action.EVActionBean;

@UrlBinding("/search/history/{$event}.url")
public class SearchHistoryAction extends EVActionBean {

	private final static Logger log4j = Logger.getLogger(SearchHistoryAction.class);

	// If present, indicates that only 1 item should be cleared
	String searchid;

	/**
	 * Clears current search history - either by session or by search ID
	 *
	 * @return Resolution
	 * @throws InfrastructureException
	 * @throws UnsupportedEncodingException
	 */
	@HandlesEvent("clear")
	@DontValidate
	public Resolution clear() throws InfrastructureException {
		String redir = "";

		// If searchid is present thn just clear single item in history
		if (!GenericValidator.isBlankOrNull(searchid)) {
			Searches.removeSingleSearch(searchid);
		} else {
			Searches.removeSessionSearches(context.getUserSession().getSessionID().getID());
		}

		// If nexturl is present use it for redirect.
		return new RedirectResolution(BackUrlAction.getStoredURLByFeature(context.getRequest(), BackURLByFeature.SEARCHHISTORY, true));
	}

	//
	//
	// GETTERS/SETTERS
	//
	//

	public String getSearchid() {
		return searchid;
	}

	public void setSearchid(String searchid) {
		this.searchid = searchid;
	}

}
