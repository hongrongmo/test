package org.ei.data.ibs.runtime;

import org.ei.domain.*;
import org.ei.data.insback.runtime.*;
import java.util.Map;
import java.util.Hashtable;
import org.ei.data.compendex.runtime.CPXLinkingStrategy;
import org.ei.fulldoc.LinkingStrategy;
public class IbsDatabase extends Database
{
    private DataDictionary dataDictionary = new org.ei.data.inspec.runtime.InspecDataDictionary();
    public int getStartYear(boolean hasBackFile) { return 1896; }

    private LinkingStrategy linkingStrategy = new IBFLinkingStrategy();
    protected String getBaseTableHook()
    {
        return "ibf_master";
    }


    public DocumentBuilder newBuilderInstance()
    {
        return new IbsDocBuilder(this);
    }

    public SearchControl newSearchControlInstance()
    {
        return new FastSearchControl();
    }

    /*
    * Because this a backfile overide getLastFourUpdates so that
    * never returns a hit from FAST.
    */

    public String getUpdates(int num)
        throws SearchException
    {
        return "13000-13001";
    }

    public DataDictionary getDataDictionary()
    {
        return dataDictionary;
    }

    public String getID()
    {
        return "ibs";
    }

    public String getLegendID()
    {
        return "ib";
    }

    public String getName()
    {
        return "Inspec Archive";
    }

    public String getIndexName()
    {
        return "ibf";
    }
    public boolean isBackfile()
    {
        return true;
    }

    public String getShortName()
    {
        return "Inspec Archive";
    }

 	public boolean hasField(SearchField searchField)
  	{
	 	return false;
  	}

	public LinkingStrategy getLinkingStrategy()
	{
	    return linkingStrategy;
  	}

    public int getMask()
    {
        return 1048576;
    }
    public int getSortValue(){ return 2; }

  public Map getTreatments() {

    Map mp = new Hashtable();

    mp.put("A", "Applications (APP)");
        mp.put("B", "Bibliography (BIB)");
        mp.put("E", "Economic (ECO)");
        mp.put("G", "General review (GEN)");
        mp.put("N", "New development (NEW)");
        mp.put("P", "Practical (PRA)");
        mp.put("R", "Product review (PRO)");
        mp.put("T", "Theoretical (THR)");
        mp.put("X", "Experimental (EXP)");

    return mp;
  }

	public boolean linkLocalHoldings(String linklabel)
	{
		if(linklabel.indexOf("NTIS") > -1)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public String getSingleCharName()
	{
		return "F";
	}


}