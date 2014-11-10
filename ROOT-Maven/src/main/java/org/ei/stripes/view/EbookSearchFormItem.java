package org.ei.stripes.view;

import java.util.ArrayList;
import java.util.List;

public class EbookSearchFormItem {
	private String shortname;
	private String displayname;
	private int subcount;
	private List<String> searchlinks = new ArrayList<String>();

	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public int getSubcount() {
		return subcount;
	}
	public void setSubcount(int subcount) {
		this.subcount = subcount;
	}
	public List<String> getSearchlinks() {
		return searchlinks;
	}
	public void setSearchlinks(List<String> searchlinks) {
		this.searchlinks = searchlinks;
	}
	public void addSearchlink(String searchlink) {
		this.searchlinks.add(searchlink);
	}
}
