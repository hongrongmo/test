package org.ei.stripes.action.results;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.exception.ErrorXml;
import org.ei.stripes.action.WebAnalyticsEventProperties;

/**
 * This is the base class for the search results abstract/detailed page handling.
 * 
 * @author harovetm
 *
 */
@UrlBinding("/search/doc/detailed.url")
public class SearchResultsDetailAction extends SearchResultsFormatAction {

	private final static Logger log4j = Logger.getLogger(SearchResultsDetailAction.class);

		
	public String getXSLPath() {
			return this.getClass().getResource("/transform/results/SearchResultsDetailedAdapter.xsl").toExternalForm();
	}
	
	
	@DefaultHandler
	@DontValidate
	public Resolution handler() {
		createWebEvent(WebAnalyticsEventProperties.CAT_RECORD,WebAnalyticsEventProperties.ACTION_DETAIL_RECORD,"");
		setRoom(ROOM.search);
		setPageTitle();
		setView("detailed");
		return new ForwardResolution("/WEB-INF/pages/customer/record/abstract.jsp");
	}

	@HandlesEvent("patentrefdetailed")
    @DontValidate
    public Resolution patentrefdetailed() {
		createWebEvent(WebAnalyticsEventProperties.CAT_RECORD,WebAnalyticsEventProperties.ACTION_PATDET_RECORD,"");
        setRoom(ROOM.search);
        setPageTitle();
        setView("detailed");
        return new ForwardResolution("/WEB-INF/pages/customer/record/abstract.jsp");
    }
	
	private void setPageTitle() {
		if(isSearchTypeQuickSearch() || isSearchTypeDedupSearch()){
			setDisplaySearchType("Quick Search");
		}else{
			setDisplaySearchType("Expert Search");
		}
		
		context.getRequest().setAttribute("pageTitle", "Engineering Village - "+ getDisplaySearchType() + " Detailed Format");
	}
	
	@Override
	public String getCID() {
		
		return getPageTypeFromRequest()+"DetailedFormat";
	}
	
}
