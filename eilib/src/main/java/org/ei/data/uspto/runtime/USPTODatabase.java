package org.ei.data.uspto.runtime;

import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.fulldoc.LinkingStrategy;

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

	public String getLegendID()
	{
		return "up";
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

	public String getSingleCharName()
	{
		return "S";
	}
	
	public DataDictionary getDataDictionary()
	{
		return null;
	}
}