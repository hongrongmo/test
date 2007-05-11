package org.ei.data.upt.runtime;

import org.ei.domain.*;
//import org.ei.ev.driver.upt.UPTDocBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ei.fulldoc.LinkingStrategy;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;


public class UPADatabase extends Database {

	private static Map searchfield = new HashMap();
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
		searchfield.put("PUC", "Y");
		searchfield.put("TI", "Y");
		searchfield.put("PAC", "Y");
		searchfield.put("PAM", "Y");
		searchfield.put("PAN", "Y");
		searchfield.put("PCI", "Y");
		searchfield.put("PCO", "Y");
		searchfield.put("PE", "Y");
		searchfield.put("PI", "Y");
		searchfield.put("PA", "Y");
		searchfield.put("PFD", "Y");
		searchfield.put("CO", "Y");


	}

  public List getSortableFields() {
    return Arrays.asList(new SortField[]{SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.CITEDBY});
  }

	public int getStartYear(boolean hasBackFile) {
		return 1790;
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
		return "upa";
	}

	public String getName() {
		return "US Patents";
	}

	public String getIndexName() {
		return "upa";
	}

	public String getShortName() {
		return "US Patents";
	}

	public boolean linkLocalHoldings(String linklabel)
	{
		return false;
	}

	public int getMask() {
		return 32768;
	}

	// in order to get the US to come before EP, even though
	// the masks are in the opposite order
	// So we will use a value lower than the EP mask
	// the Database.compareTo() is based on getSortValue
	// and used in sorting in DatabaseConfig getDatabases()
	public int getSortValue() {
		return 16384-1;
	}


    public LinkingStrategy getLinkingStrategy()
    {
        return new UPALinkingStrategy();
    }

    public boolean hasField(SearchField searchField,
    						int userMaskMax)
    {
 	  return searchfield.containsKey(searchField.getID());
    }

}