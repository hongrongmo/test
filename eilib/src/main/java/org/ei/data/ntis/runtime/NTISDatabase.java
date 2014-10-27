package org.ei.data.ntis.runtime;

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

public class NTISDatabase extends Database {

    private static Map<String, String> searchfield = new HashMap<String, String>();
    static {
        searchfield.put("ALL", "Y");
        searchfield.put("AB", "Y");
        searchfield.put("AN", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("AF", "Y");
        searchfield.put("AV", "Y");
        searchfield.put("CL", "Y");
        searchfield.put("CT", "Y");
        searchfield.put("CV", "Y");
        searchfield.put("CO", "Y");
        searchfield.put("DT", "Y");
        searchfield.put("PA", "Y");
        searchfield.put("LA", "Y");
        searchfield.put("AG", "Y");
        searchfield.put("NT", "Y");
        searchfield.put("PI", "Y");
        searchfield.put("RN", "Y");
        searchfield.put("KY", "Y");
        searchfield.put("TI", "Y");
        searchfield.put("FL", "Y");
    }

    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR });
    }

    private DataDictionary dataDictionary = new NTISDataDictionary();

    private LinkingStrategy NTISLinkingStrategy = new NTISLinkingStrategy();

    public int getStartYear(boolean hasBackFile) {
        return 1899;
    }

    protected String getBaseTableHook() {
        return "ntis_master";
    }

    public DataDictionary getDataDictionary() {
        return this.dataDictionary;
    }

    public DocumentBuilder newBuilderInstance() {
        return new NTISDocBuilder(this);
    }

    public SearchControl newSearchControlInstance() {
        return new FastSearchControl();
    }

    public String getID() {
        return "nti";
    }

    public String getLegendID() {
        return "n";
    }

    public String getName() {
        return "NTIS";
    }

    public String getIndexName() {
        return "ntis";
    }

    public String getShortName() {
        return "NTIS";
    }

    public boolean hasField(SearchField searchField, int userMaskMax) {
        return searchfield.containsKey(searchField.getID());
    }

    public boolean linkLocalHoldings(String linklabel) {
        if (linklabel.indexOf("NTIS") > -1) {
            return true;
        } else {
            return false;
        }
    }

    public int getMask() {
        return 4;
    }

    public int getSortValue() {
        return getMask();
    }

    public LinkingStrategy getLinkingStrategy() {
        return this.NTISLinkingStrategy;
    }

    public String getSingleCharName() {
        return "N";
    }

}