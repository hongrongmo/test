package org.ei.stripes.view;

import org.apache.log4j.Logger;

/**
 * Represents page navigation for various EV pages.  Note that some parts 
 * of EV use record index navigation (search results page) and some use
 * page navigation (selected records).  This class will convert from either
 * format to give the caller access to both methods
 * 
 * @author harovetm
 *
 */
public class PageNavigation {
	private final static Logger log4j = Logger.getLogger(PageNavigation.class);
	
	public static int RESULTS_PER_PAGE = 25;
	
	/**
	 * Normal constructor
	 */
	public PageNavigation() {
		this.resultsperpage = RESULTS_PER_PAGE;
	}
	
	private int resultscount;
	private int resultsperpage = RESULTS_PER_PAGE;
	// Some of EV uses page navigation and some uses index (below)
	private int currentpage;
	private int prevpage;
	private int nextpage;
	// These are for thesaurus browse page navigation ONLY!
	// It uses an index to mark the prev/next transitions
	private int currentindex;
	private int previndex;
	private int nextindex;
	
	public int getResultsperpage() {
		return resultsperpage;
	}
	public void setResultsperpage(int offset) {
		if (offset == 0) {
			log4j.warn("Attempt to set resultsperpage to 0 - must be positive value!");
			return; 
		}
		this.resultsperpage = offset;
	}
	
	public int getPagecount() {
		double x = ((double)resultscount) / ((double)resultsperpage);
		return (int) Math.ceil(x);
	}

	public int getResultscount() {
		return resultscount;
	}

	public void setResultscount(int resultscount) {
		this.resultscount = resultscount;
	}

	public int getCurrentpage() {
		// If the currentpage is 0 we only have an record index.  Do
		// the conversion one time
		if (currentpage == 0) {
			currentpage = (currentindex / resultsperpage);
			if (currentindex % resultsperpage > 0) currentpage++;
		}
		return currentpage;
	}
	public void setCurrentpage(int current) {
		this.currentpage = current;
	}
	
	public int getPrevindex() {
		// If the previndex is 0 we are doing page-based navigation.  Do
		// the conversion to index one time
		if (previndex == 0) {
			previndex = ((prevpage * resultsperpage) + 1);
		}
		return previndex;
	}

	public void setPrevindex(int previndex) {
		this.previndex = previndex;
	}

	public int getNextindex() {
		// If the currentindex is 0 we are doing page-based navigation.  Do
		// the conversion to index one time
		if (nextindex == 0) {
			nextindex = ((nextpage * resultsperpage) + 1);
		}
		return nextindex;
	}

	public void setNextindex(int nextindex) {
		this.nextindex = nextindex;
	}

	public int getPrevpage() {
		// If the prevpage is 0 we only have an record index.  Do
		// the conversion one time
		if (prevpage == 0) {
			if (previndex == 1) prevpage = 1;
			else prevpage = (previndex / resultsperpage);
		}
		return prevpage;
	}

	public void setPrevpage(int prevpage) {
		this.prevpage = prevpage;
	}

	public int getNextpage() {
		// If the currentpage is 0 we only have an record index.  Do
		// the conversion one time
		if (nextpage == 0) {
			if (nextindex == 1) nextpage = 1;
			else nextpage = (nextindex / resultsperpage);
		}
		return nextpage;
	}

	public void setNextpage(int nextpage) {
		this.nextpage = nextpage;
	}

	public int getCurrentindex() {
		// If the currentindex is 0 we are doing page-based navigation.  Do
		// the conversion to index one time
		if (currentindex == 0) {
			currentindex = ((currentpage * resultsperpage) + 1);
		}
		return currentindex;
	}

	public void setCurrentindex(int currentindex) {
		this.currentindex = currentindex;
	}

}
