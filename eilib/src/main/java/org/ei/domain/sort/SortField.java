package org.ei.domain.sort;

import org.ei.domain.Sort;

public abstract class SortField implements Comparable<Object> {
    public final static SortField RELEVANCE = new Relevance();
    public final static SortField AUTHOR = new Author();
    public final static SortField YEAR = new Year();
    public final static SortField PUBLISHER = new Publisher();
    public final static SortField SOURCE = new Source();
    public final static SortField CITEDBY = new CitedBy();

    public abstract String getDisplayName();

    public abstract String getSearchIndexKey();

    public abstract int getDisplayOrderValue();

    public String getDefaultSortDirection() {
        return Sort.UP;
    }

    public String[] getAvailableSortDirections() {
        return new String[] { Sort.UP, Sort.DOWN };
    }

    public int compareTo(Object obj) {
        SortField d = (SortField) obj;
        if (d != null) {
            if (getDisplayOrderValue() < d.getDisplayOrderValue()) {
                return -1;
            } else if (getDisplayOrderValue() > d.getDisplayOrderValue()) {
                return 1;
            } else if (getDisplayOrderValue() == d.getDisplayOrderValue()) {
                return 0;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

}
