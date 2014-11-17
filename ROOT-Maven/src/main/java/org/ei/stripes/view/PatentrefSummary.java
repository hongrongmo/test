package org.ei.stripes.view;

import java.util.ArrayList;
import java.util.List;

public class PatentrefSummary{
	
	protected String patrefsummsg;
	SearchResult psr = new SearchResult();
	protected List<SearchResult> presults = null;
	
	public String getPatrefsummsg() {
		return patrefsummsg;
	}

	public void setPatrefsummsg(String patrefsummsg) {
		this.patrefsummsg = patrefsummsg;
	}
	
	public List<SearchResult> getPresults() {
		return presults;
	}

	public void setPresults(List<SearchResult> presults) {
		this.presults = presults;
	}
	
	public void addSearchPresult(SearchResult psr) {
		if (presults == null) {
			presults = new ArrayList<SearchResult>();
		}
		presults.add(psr);
	}



	
}