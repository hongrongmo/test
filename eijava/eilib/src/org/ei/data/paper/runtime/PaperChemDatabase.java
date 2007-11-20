package org.ei.data.paper.runtime;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.ei.data.compendex.runtime.CPXLinkingStrategy;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.fulldoc.LinkingStrategy;

public class PaperChemDatabase extends Database {

    private LinkingStrategy PCHLinkingStrategy = new CPXLinkingStrategy();
    private static Map searchfield = new HashMap();
	static
	{
		searchfield.put("ALL", "Y");
		searchfield.put("AB", "Y");
		searchfield.put("AN", "Y");
		searchfield.put("AU", "Y");
		searchfield.put("AF", "Y");
		searchfield.put("CN", "Y");
		searchfield.put("CF", "Y");
		searchfield.put("CV", "Y");
		searchfield.put("PU", "Y");
		searchfield.put("DT", "Y");
		searchfield.put("PA", "Y");
		searchfield.put("BN", "Y");
		searchfield.put("SN", "Y");
		searchfield.put("LA", "Y");
		searchfield.put("PI", "Y");
		searchfield.put("PM", "Y");
		searchfield.put("PN", "Y");
		searchfield.put("ST", "Y");
		searchfield.put("KY", "Y");
		searchfield.put("TI", "Y");
		searchfield.put("FL", "Y");
	}

    public int getStartYear(boolean hasBackFile) {
        return (1990);
    }

    protected String getBaseTableHook() {
        return "paper_master";
    }

    public DocumentBuilder newBuilderInstance() {
        return new PaperChemDocBuilder(this);
    }

    public SearchControl newSearchControlInstance() {
        return new FastSearchControl();
    }

    public String getID() {
        return "pch";
    }

    public String getLegendID()
    {
        return "pc";
    }

    public String getName() {
        return "PaperChem";
    }

    public String getIndexName() {
        return "pch";
    }

    public String getShortName() {
        return "PaperChem";
    }

    public String getDisplayAbrevName()
    {
        return "PC";
    }

    public String getSingleCharName()
    {
        return "A";
    }

    public int getMask() {
        return 64;
    }

	public int getSortValue() {
		return getMask();
	}

    public Map getTreatments() {
        Map mp = new Hashtable();
        mp.put("A", "Applications (APP)");
        mp.put("B", "Biographical (BIO)");
        mp.put("E", "Economic (ECO)");
        mp.put("X", "Experimental (EXP)");
        mp.put("G", "General review (GEN)");
        mp.put("H", "Historical (HIS)");
        mp.put("L", "Literature review (LIT)");
        mp.put("M", "Management aspects (MAN)");
        mp.put("N", "Numerical (NUM)");
        mp.put("T", "Theoretical (THR)");
        return mp;
    }

    public LinkingStrategy getLinkingStrategy()
	{
        return PCHLinkingStrategy;
	}

	public boolean hasField(SearchField searchField,
			int mask)
	{
	    if(searchfield.containsKey(searchField.getID()))
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }
	}

    public boolean hasField(SearchField searchField)
    {
	    if(searchfield.containsKey(searchField.getID()))
	    {
	        return true;
	    }
	    else
	    {
	        return false;
	    }
    }
}