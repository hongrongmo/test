package org.ei.data.upt.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;
import org.ei.fulldoc.LinkingStrategy;

public class UPTDatabase extends Database {

    private LinkingStrategy UPTLinkingStrategy = new UPTLinkingStrategy();
    private Database[] children = { new UPADatabase(), new EUPDatabase() };

    private static Map<String, String> searchfield = new HashMap<String, String>();
    static {
        searchfield.put("ALL", "Y");
        searchfield.put("AB", "Y");
        searchfield.put("AN", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("PE", "Y");
        searchfield.put("KY", "Y");
        searchfield.put("PD", "Y");
        searchfield.put("PM", "Y");
        searchfield.put("PID", "Y");
        searchfield.put("PRN", "Y");
        searchfield.put("PUC", "Y");
        searchfield.put("TI", "Y");

    }

    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.CITEDBY });
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
        return "upt";
    }

    public String getLegendID() {
        return "u";
    }

    public String getName() {
        return "Patents";
    }

    public String getIndexName() {
        return "upt";
    }

    public String getShortName() {
        return "Patents";
    }

    public int getMask() {
        return 524288;
    }

    public boolean hasChildren() {
        return true;
    }

    public boolean linkLocalHoldings(String linklabel) {
        return false;
    }

    public Database[] getChildren() {
        return children;
    }

    public int getSortValue() {
        return getMask();
    }

    public LinkingStrategy getLinkingStrategy() {
        return UPTLinkingStrategy;
    }

    public boolean hasField(SearchField searchField) {
        return searchfield.containsKey(searchField.getID());
    }

    public String getSingleCharName() {
        return "T";
    }

    public DataDictionary getDataDictionary() {
        return null;
    }
}