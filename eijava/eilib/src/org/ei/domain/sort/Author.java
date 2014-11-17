package org.ei.domain.sort;

public class Author extends SortField
{
  public String getDisplayName(){ return "Author"; }
  public String getSearchIndexKey(){ return "ausort"; }
  public int getDisplayOrderValue() { return 3; }

}

