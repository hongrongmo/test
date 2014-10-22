package org.ei.stripes.action.results;

import java.util.List;

import org.ei.stripes.view.SearchResult;
import org.ei.tags.TagBubble;

/**
 * This interfaced allows us to use different Stripes actions in the same 
 * XSL stylesheets (CitationResults.xsl)
 * 
 * @author harovetm
 *
 */
public interface ICitationAction {
	public void addSearchResult(SearchResult sr);

	public List<SearchResult> getResults() ;
	public void setResults(List<SearchResult> results) ;

	public void setTagbubble(TagBubble tagbubble);
}
