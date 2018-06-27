package org.ei.domain.sort;

import org.ei.domain.Sort;

public class Year extends SortField
{
  public String getDisplayName(){ return "Date"; }
  public String getSearchIndexKey(){ return "yr"; }
  public int getDisplayOrderValue() { return 2; }

  public String getDefaultSortDirection() { return Sort.DOWN; }

}


