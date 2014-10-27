package org.ei.stripes.view.thesaurus;

/**
 * Represents a page navigation object for thesaurus search results (term search)
 * 
 * @author harovetm
 *
 */
public class TermPageNavigation {
	private int pagecount;
	private int current;
	public int getPagecount() {
		return pagecount;
	}
	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
	// Convienence method for JSTL - used with forEach tag
	public String[] getPages() {
		String [] pages = new String[pagecount];
		for (int i=1; i<=pagecount; i++) pages[i-1] = Integer.toString(i);
		return pages;
	}
	
}
