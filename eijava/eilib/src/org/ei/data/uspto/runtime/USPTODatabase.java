package org.ei.data.uspto.runtime;

import org.ei.domain.*;
import org.ei.fulldoc.LinkingStrategy;
import org.ei.util.StringUtil;
import org.ei.domain.SearchField;
import org.ei.domain.SearchFields;

public class USPTODatabase extends Database
{


	public DocumentBuilder newBuilderInstance()
	{
		return null;
	}

	public SearchControl newSearchControlInstance()
	{
		return new FastSearchControl();
	}

	public String getID()
	{
		return "usp";
	}

	public String getName()
	{
		return "USPTO";
	}

	public String getIndexName()
	{
		return getID();
	}

	public String getShortName()
	{
		return "USPTO";
	}

	public int getMask()
	{
		return 8;
	}

 	public boolean hasField(SearchField searchField)
  	{
	 	return false;
  	}
    public LinkingStrategy getLinkingStrategy()
    {
        return null;
    }
	public int getSortValue(){ return getMask(); }

}