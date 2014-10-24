package org.ei.stripes.action.results;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.gui.ListBoxOption;
import org.ei.stripes.adapter.IBizBean;
import org.ei.tags.Scope;

/**
 * This is the base class for the search results handling.
 * 
 * PLEASE NOTE
 * It can be invoked but ***ONLY*** for zero results searches!
 * @see processModelXML method below for how to handle this.
 * 
 * @author harovetm
 *
 */
@UrlBinding("/search/results/tags.url")
public class TagSearchResultsAction extends SearchResultsAction implements IBizBean, ICitationAction {

	private final static Logger log4j = Logger.getLogger(TagSearchResultsAction.class);

	private String scope;
	private List<ListBoxOption> scopes;
	private String searchgrouptags;
	private String searchtags;

	
	@DefaultHandler
	@DontValidate
	public Resolution tags() throws Exception {
		setRoom(ROOM.search);
		setBasketCount(getUserBasketCount());

		// Get the scope for the tags
		Scope sc = new Scope(context.getUserid(), this.scope);
		this.scopes = sc.getScopeOptions();
		
		// Disable some query toolbar buttons
		this.removeduplicates = false;
		this.showmap = false;
		this.searchtype = "TagSearch";

		//
		// Set some parms not covered by XSLT
		//
		if (GenericValidator.isBlankOrNull(this.searchid)) {
		    this.searchid = context.getRequest().getParameter("SEARCHID");
		}
		this.sessionid = context.getUserSession().getSessionID().toString();
		this.reruncid = this.CID;
		this.displayquery = this.searchid;
		
		//
		// If "error" is present, an error has been added from the
		// model layer!
		//
    	String error = context.getRequest().getParameter("error");
    	if (!GenericValidator.isBlankOrNull(error)) {
    		context.getValidationErrors().addGlobalError(new LocalizableError(
    			"org.ei.stripes.action.search.BaseSearchAction.syntaxerror",
    			context.getHelpUrl()));
    	}
		
		// Forward on
		return new ForwardResolution("/WEB-INF/pages/customer/results/tagresults.jsp");
	}
	
	
	public String getScope() {
		return scope;
	}


	public void setScope(String scope) {
		this.scope = scope;
	}


	public String getSearchgrouptags() {
		return searchgrouptags;
	}


	public void setSearchgrouptags(String searchgrouptags) {
		this.searchgrouptags = searchgrouptags;
	}


	public String getSearchtags() {
		return searchtags;
	}


	public void setSearchtags(String searchtags) {
		this.searchtags = searchtags;
	}


	public List<ListBoxOption> getScopes() {
		return scopes;
	}


	public void setScopes(List<ListBoxOption> scopes) {
		this.scopes = scopes;
	}

}
