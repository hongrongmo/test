package org.ei.data.upt.runtime;

import java.util.HashMap;
import java.util.Map;

import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FakeSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.fulldoc.LinkingStrategy;

public class UPTRefDatabase extends Database {
    private LinkingStrategy UPTRefLinkingStrategy = new UPTLinkingStrategy();

    public int getStartYear(boolean hasBackFile) {
        return ((hasBackFile == true) ? 1884 : 1969);
    }

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
        searchfield.put("CO", "Y");

    }

    protected String getBaseTableHook() {
        return "upt_refs";
    }

    public DocumentBuilder newBuilderInstance() {
        return new UPTRefDocBuilder(this);
    }

    public SearchControl newSearchControlInstance() {
        // return new FastSearchControl();
        return new FakeSearchControl();
    }

    public String getID() {
        return "ref";
    }

    public String getLegendID() {
        return "r";
    }

    public String getName() {
        return "Patent References";
    }

    public String getIndexName() {
        return "ref";
    }

    public String getShortName() {
        return "Patents References";
    }

    public int getMask() {
        return 65536;
    }

    public int getSortValue() {
        return getMask();
    }

    public boolean linkLocalHoldings(String linkLabel) {
        return false;
    }

    public LinkingStrategy getLinkingStrategy() {

        return UPTRefLinkingStrategy;
    }

    public boolean hasField(SearchField searchField) {
        return searchfield.containsKey(searchField.getID());
    }

    public String getSingleCharName() {
        return "D";
    }

    public DataDictionary getDataDictionary() {
        return null;
    }

}