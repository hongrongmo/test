package org.ei.domain.sort;

import org.ei.domain.Sort;

public class Relevance extends SortField
{
  public String getDisplayName(){ return "Relevance"; }
  public String getSearchIndexKey(){ return "relevance"; }
  public int getDisplayOrderValue() { return 1; }
  public String getDefaultSortDirection() { return Sort.DOWN; }
  public String[] getAvailableSortDirections() { return new String[]{Sort.DOWN}; }
}


