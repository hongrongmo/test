package org.ei.data.ENGnetBASE.runtime;

import org.ei.domain.*;
import org.ei.fulldoc.LinkingStrategy;
public class ENGnetBASEDatabase extends Database
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
		return "crc";
	}
    public String getLegendID()
    {
        return "cr";
    }

	public String getName()
	{
		return "CRC ENGnetBASE";
	}

	public String getIndexName()
	{
		return getID();
	}

	public String getShortName()
	{
		return "CRC";
	}

	public int getMask()
	{
		return 16;
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
		return "R";
	}
	
	 public DataDictionary getDataDictionary()
	    {
	        return null;
	    }

}