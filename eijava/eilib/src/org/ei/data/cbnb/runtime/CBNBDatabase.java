package org.ei.data.cbnb.runtime;

import org.ei.domain.*;
import org.ei.util.StringUtil;
import org.ei.domain.SearchField;
import org.ei.domain.SearchFields;
import java.util.Map;
import java.util.Hashtable;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.ei.fulldoc.LinkingStrategy;


public class CBNBDatabase extends Database
{

	private static Map searchfield = new HashMap();
	static
	{

		searchfield.put("ALL", "Y");
		searchfield.put("AB", "Y");
		searchfield.put("CR", "Y");
		searchfield.put("CM", "Y");
		searchfield.put("CN", "Y");
		searchfield.put("PE", "Y");
		searchfield.put("DT", "Y");
		searchfield.put("CV", "Y");
		searchfield.put("BN", "Y");
		searchfield.put("SN", "Y");
		searchfield.put("GD", "Y");
		searchfield.put("CL", "Y");
		searchfield.put("LA", "Y");
		searchfield.put("CO", "Y");
		searchfield.put("SC", "Y");
		searchfield.put("ST", "Y");
		searchfield.put("IC", "Y");
		searchfield.put("TI", "Y");
		searchfield.put("KY", "Y");

	}

    private DataDictionary dataDictionary = new CBNBDataDictionary();

	public int getStartYear(boolean hasBackFile)
	{ return (1990); }

	protected String getBaseTableHook()
	{
		return "cbn_master";
	}

    public DataDictionary getDataDictionary()
    {
        return dataDictionary;
    }
	public DocumentBuilder newBuilderInstance()
	{
		return new CBNBDocBuilder(this);
	}

	public SearchControl newSearchControlInstance()
	{
		return new FastSearchControl();
	}

	public String getID()
	{
		return "cbn";
	}
	
    public String getLegendID()
    {
        return "cb";
    }

	public String getName()
	{
		return "CBNB";
	}

    public String getDisplayAbrevName()
    {
        return "Cb";
    }

    public String getSingleCharName()
    {
        return "B";
    }

	public String getIndexName()
	{
		return "bbn";
	}

/*	public Database getBackfile()
	{
		return backfile;
	} */

	public String getShortName()
	{
		return "CBNB";
	}

	public int getMask()
	{
		return 256;
	}
	public int getSortValue(){ return getMask(); }

	public boolean linkLocalHoldings(String linklabel)
	{
		return false;
	}
	public boolean hasField(SearchField searchField,
							int userMaskMax)
	{

		return searchfield.containsKey(searchField.getID());
	}
	public LinkingStrategy getLinkingStrategy()
	{
		return null;
    }
}