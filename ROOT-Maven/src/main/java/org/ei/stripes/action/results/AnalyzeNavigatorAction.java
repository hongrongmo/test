package org.ei.stripes.action.results;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.Query;
import org.ei.domain.Searches;
import org.ei.domain.navigators.EiNavigator;
import org.ei.domain.navigators.NavigatorCache;
import org.ei.domain.navigators.ResultNavigator;
import org.ei.domain.navigators.ResultsState;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.SystemMessage;
import org.ei.util.StringUtil;

@UrlBinding("/search/results/analyzenav.url")
public class AnalyzeNavigatorAction extends EVActionBean {

	private final static Logger log4j = Logger
			.getLogger(AnalyzeNavigatorAction.class);

	// Request parameters
	private String field;
	private String searchid;

	// Data needed on JSP (getters only)
	private String analyzedata;
	private String query;
	private int count;

	/**
	 * Handler for the Charts display from navigators/facets
	 * 
	 * @return Resolution
	 */
	@DefaultHandler
	@DontValidate
	public Resolution display() {
		setRoom(ROOM.blank);

		// Ensure searchid is present 
		if (GenericValidator.isBlankOrNull(searchid)) {
			log4j.error("'searchid' request parameter is required!");
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}
		
		// Set some local variables
		UserSession usersession = context.getUserSession();
		String sessionId = usersession.getID();
		Query queryObject = null;
		if (field == null) {
			field = "db";
		}
		String navfield = field.concat("nav");

		try {

			// Build a query object from the search ID
			queryObject = Searches.getSearch(searchid);
			query = queryObject.getDisplayQuery();

			// Get navigator info from cache
			NavigatorCache navcache = new NavigatorCache(sessionId);
			ResultNavigator navigators = navcache.getFromCache(searchid);
			EiNavigator anav = navigators.getNavigatorByName(navfield);
			if (anav != null) {
				int count = ResultsState.DEFAULT_STATE_COUNT;
				ResultsState resultsState = queryObject.getResultsState();
				Map rs = resultsState.getStateMap();
				if ((rs != null) && (rs.get(field) != null)) {
					count = ((Integer) rs.get(field)).intValue();
				} else {
					log4j.warn("No state map present!");
				}

				String navstring = anav.toString(count);
				analyzedata = StringUtil.zipText(navstring);
			} else {
				log4j.warn("No navigators retrieved from cache!");
			}

		} catch (Exception e) {
			log4j.error("Exception has occurred: " + e.getMessage());
			return SystemMessage.SYSTEM_ERROR_RESOLUTION;
		}

		return new ForwardResolution("/WEB-INF/pages/customer/results/analyzenav.jsp");
	}

	//
	//
	// GETTERS/SETTERS
	//
	//

	public String getAnalyzedata() {
		return analyzedata;
	}

	public String getAnalyzedataencoded() throws UnsupportedEncodingException {
		return URLEncoder.encode(analyzedata, "UTF-8");
	}

	public String getQuery() {
		return query;
	}

	public int getCount() {
		return count;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getSearchid() {
		return searchid;
	}

	public void setSearchid(String searchid) {
		this.searchid = searchid;
	}

}
