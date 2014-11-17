package org.ei.stripes.view.thesaurus;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of term search results
 * 
 * @author harovetm
 *
 */
public class TermSearchResults {
	private String mode;
	private List<TermSearchResult> results = new ArrayList<TermSearchResult>();
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<TermSearchResult> getResults() {
		return results;
	}
	public void setResults(List<TermSearchResult> results) {
		this.results = results;
	}
}
