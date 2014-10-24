package org.ei.stripes.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;

/**
 * This class is used with the More Search Sources to redirect links to 
 * @author harovetm
 *
 */
@UrlBinding("/redirsearch.url")
public class RedirectSearchAction extends EVActionBean {

    private String searchType;
    
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }
    
    @DefaultHandler
    public Resolution redirsearch() {
        String urlString;
        if ("expert".equals(searchType)) {
             urlString = EVPathUrl.EV_EXPERT_SEARCH.value() + "?" + context.getRequest().getQueryString();
        }
        else if ("combined".equals(searchType)) {
             urlString = "/controller/servlet/Controller?CID=combinedSearch&"+context.getRequest().getQueryString();
        }
        else {
             urlString = EVPathUrl.EV_QUICK_SEARCH.value() + "?" + context.getRequest().getQueryString();
        }
        
        return new RedirectResolution(urlString);
    }

    //
    // GETTERS/SETTERS
    //
    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }
    
}
