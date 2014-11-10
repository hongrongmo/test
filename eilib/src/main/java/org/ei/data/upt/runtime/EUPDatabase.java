package org.ei.data.upt.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;
import org.ei.fulldoc.LinkingStrategy;


public class EUPDatabase extends Database {

	private static Map<String, String> searchfield = new HashMap<String, String>();
	static
	{
		searchfield.put("ALL", "Y");
		searchfield.put("AB", "Y");
		searchfield.put("AU", "Y");
		searchfield.put("AF", "Y");
		searchfield.put("KY", "Y");
		searchfield.put("PD", "Y");
		searchfield.put("PM", "Y");
		searchfield.put("PID", "Y");
		searchfield.put("PRN", "Y");
		searchfield.put("TI", "Y");
		searchfield.put("YR", "Y");
		searchfield.put("PEC", "Y");
		searchfield.put("PAC", "Y");
		searchfield.put("PAM", "Y");
		searchfield.put("PAN", "Y");
		searchfield.put("PCI", "Y");
		searchfield.put("PE", "Y");
		searchfield.put("PI", "Y");
		searchfield.put("PCO", "Y");
		searchfield.put("PA", "Y");
		searchfield.put("PFD", "Y");
		searchfield.put("CO", "Y");
	}

  public List<SortField> getSortableFields() {
    return Arrays.asList(new SortField[]{SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.CITEDBY});
  }


	public int getStartYear(boolean hasBackFile)
	{
		return 1978;
	}

	protected String getBaseTableHook() {
		return "upt_master";
	}

	public DocumentBuilder newBuilderInstance() {
		return new UPTDocBuilder(this);
	}

	public SearchControl newSearchControlInstance() {
		return new FastSearchControl();
	}

	public String getID() {
		return "eup";
	}

    public String getLegendID()
    {
        return "e";
    }

	public String getName() {
		return "EP Patents";
	}

	public boolean linkLocalHoldings(String linklabel)
	{
		return false;
	}

	public String getIndexName() {
		return "eup";
	}

	public String getShortName() {
		return "EP Patents";
	}

	public int getMask() {
		return 16384;
	}

	public int getSortValue() {
		return getMask();
	}

    public LinkingStrategy getLinkingStrategy()
    {
        return new EUPLinkingStrategy();
    }

	public boolean hasField(SearchField searchField,
					  		int userMaskMax)
	{
		return searchfield.containsKey(searchField.getID());
	}

	public String getSingleCharName()
	{
		return "E";
	}

}