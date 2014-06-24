package org.ei.stripes.action.search;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.Searches;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.EVPathUrl;

@UrlBinding("/search/history/{$event}.url")
public class SearchHistoryAction extends EVActionBean {

	private final static Logger log4j = Logger.getLogger(SearchHistoryAction.class);

	// If present, holds the parameters for the next URL to go to
	String nexturl;

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
		try {
			if (!GenericValidator.isBlankOrNull(nexturl)) {
				redir = URLDecoder.decode(nexturl, "UTF-8");
			} else {
				redir = EVPathUrl.EV_HOME.value();
			}
		} catch (UnsupportedEncodingException e) {
			throw new InfrastructureException(SystemErrorCodes.UNKNOWN_INFRASTRUCTURE_ERROR, e);
		}

		return new RedirectResolution(redir);
	}

	//
	//
	// GETTERS/SETTERS
	//
	//

	public String getNexturl() {
		return nexturl;
	}

	public void setNexturl(String nexturl) {
		this.nexturl = nexturl;
	}

	public String getSearchid() {
		return searchid;
	}

	public void setSearchid(String searchid) {
		this.searchid = searchid;
	}

}
