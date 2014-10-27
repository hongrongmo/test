package org.ei.domain.sort;

public class Publisher extends SortField {

	public String getDisplayName() {
		return "Publisher";
	}

	public String getSearchIndexKey() {
		return "pnsort";
	}
	public int getDisplayOrderValue() { return 5; }

}
