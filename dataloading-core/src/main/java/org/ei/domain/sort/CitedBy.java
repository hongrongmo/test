package org.ei.domain.sort;

import org.ei.domain.Sort;

public class CitedBy extends SortField {

	public String getDisplayName() {
		return "Cited by Patents";
	}

	public String getSearchIndexKey() {
		return "pcb";
	}

  public String getDefaultSortDirection() { return Sort.DOWN; }

  public int getDisplayOrderValue() { return 6; }

}