package org.ei.stripes.action.results;



import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.log4j.Logger;
import org.ei.stripes.action.WebAnalyticsEventProperties;



@UrlBinding("/search/doc/abstract.url")
public class SearchResultsAbstractAction extends SearchResultsFormatAction  {

	private final static Logger log4j = Logger.getLogger(SearchResultsAbstractAction.class);
	
		
	public String getXSLPath() {
			return this.getClass().getResource("/transform/results/SearchResultsAbstractAdapter.xsl").toExternalForm();
	}
	
    @DefaultHandler
    @DontValidate
    public Resolution handler() {
    	createWebEvent(WebAnalyticsEventProperties.CAT_RECORD,WebAnalyticsEventProperties.ACTION_ABS_RECORD,"");
    	setRoom(ROOM.search);
        setPageTitle();
        setView("abstract");
        return new ForwardResolution("/WEB-INF/pages/customer/record/abstract.jsp");
    }
    
    @HandlesEvent("patentrefabstract")
    @DontValidate
    public Resolution patentrefabstract() {
    	createWebEvent(WebAnalyticsEventProperties.CAT_RECORD,WebAnalyticsEventProperties.ACTION_PATABS_RECORD,"");
        setRoom(ROOM.search);
        setPageTitle();
        setView("abstract");
        return new ForwardResolution("/WEB-INF/pages/customer/record/abstract.jsp");
    }
    
	private void setPageTitle() {
		if(isSearchTypeQuickSearch() || isSearchTypeDedupSearch()){
			setDisplaySearchType("Quick Search");
		}else{
			setDisplaySearchType("Expert Search");
		}
		
		context.getRequest().setAttribute("pageTitle", "Engineering Village - "+ getDisplaySearchType() + " Abstract Format");
	}
	
	@Override
	public String getCID() {
		
		return getPageTypeFromRequest()+"AbstractFormat";
	}
	
}
