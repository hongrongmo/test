package org.ei.stripes.view.thesaurus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a set of full record results from a thesaurus search
 *
 * @author harovetm
 *
 */
public class FullRecResults {
	private TermSearchResult main;
	private TermSearchResult use;
	private boolean scopenote;
	private List<TermSearchResult> broader = new ArrayList<TermSearchResult>();
	private List<TermSearchResult> related = new ArrayList<TermSearchResult>();
	private List<TermSearchResult> narrower = new ArrayList<TermSearchResult>();
	private List<TermSearchResult> leadin = new ArrayList<TermSearchResult>();
	public TermSearchResult getMain() {
		return main;
	}
	public void setMain(TermSearchResult main) {
		this.main = main;
	}
	public TermSearchResult getUse() {
		return use;
	}
	public void setUse(TermSearchResult use) {
		this.use = use;
	}
	public boolean isScopenote() {
		return scopenote;
	}
	public void setScopenote(boolean scopenote) {
		this.scopenote = scopenote;
	}
	public List<TermSearchResult> getBroader() {
		if(broader!=null && broader.size()>1)
			Collections.sort(broader,new ThesaurusTermComp());
		return broader;
	}
	public void setBroader(List<TermSearchResult> broader) {
		this.broader = broader;
	}
	public void addBroader(TermSearchResult term) {
		this.broader.add(term);
	}
	public List<TermSearchResult> getRelated() {
		if(related != null && related.size()>1)
			Collections.sort(related,new ThesaurusTermComp());
		return related;
	}
	public void setRelated(List<TermSearchResult> related) {
		this.related = related;
	}
	public void addRelated(TermSearchResult term) {
		this.related.add(term);
	}
	public List<TermSearchResult> getNarrower() {
		if(narrower!=null & narrower.size()>1)
			Collections.sort(narrower,new ThesaurusTermComp());
		return narrower;
	}
	public void setNarrower(List<TermSearchResult> narrower) {
		this.narrower = narrower;
	}
	public void addNarrower(TermSearchResult term) {
		this.narrower.add(term);
	}
	public List<TermSearchResult> getLeadin() {
		if(leadin!=null && leadin.size()>1)
			Collections.sort(leadin,new ThesaurusTermComp());
		return leadin;
	}
	public void setLeadin(List<TermSearchResult> leadin) {
		this.leadin = leadin;
	}
	public void addLeadin(TermSearchResult term) {
		this.leadin.add(term);
	}



}
