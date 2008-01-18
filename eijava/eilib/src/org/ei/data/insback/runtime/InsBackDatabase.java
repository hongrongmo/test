package org.ei.data.insback.runtime;

import org.ei.domain.*;
import java.util.Map;
import java.util.Hashtable;
import org.ei.data.compendex.runtime.CPXLinkingStrategy;
import org.ei.fulldoc.LinkingStrategy;

import java.util.List;
import java.util.Arrays;
import org.ei.domain.sort.SortField;

public class InsBackDatabase extends Database
{
    private DataDictionary dataDictionary = new org.ei.data.inspec.runtime.InspecDataDictionary();
    public int getStartYear(boolean hasBackFile) { return 1896; }

    private LinkingStrategy linkingStrategy = new IBFLinkingStrategy();
    protected String getBaseTableHook()
    {
        return "ibf_master";
    }

    public List getSortableFields() {
      return Arrays.asList(new SortField[]{SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE, SortField.PUBLISHER});
    }

    public DocumentBuilder newBuilderInstance()
    {
        return new InsBackDocBuilder(this);
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
        return "ibf";
    }

    public String getLegendID()
    {
        return "ib";
    }

    public String getName()
    {
        return "Inspec";
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
        return "Inspec";
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
        return 4096;
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