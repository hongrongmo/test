package org.ei.data.encompasslit.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ei.data.compendex.runtime.CPXLinkingStrategy;
import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;
import org.ei.fulldoc.LinkingStrategy;

public class EltDatabase extends Database {
    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE, SortField.PUBLISHER });
    }

    private LinkingStrategy EltLinkingStrategy = new CPXLinkingStrategy();
    private DataDictionary dataDictionary = new EltDataDictionary();

    private static Map<String, String> searchfield = new HashMap<String, String>();

    static {
        searchfield.put("ALL", "Y");
        searchfield.put("AB", "Y");
        searchfield.put("AF", "Y");
        searchfield.put("All", "Y");
        searchfield.put("AN", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("BN", "Y");
        searchfield.put("CF", "Y");
        searchfield.put("CC", "Y");
        searchfield.put("CL", "Y");
        searchfield.put("CO", "Y");
        searchfield.put("CR", "Y");
        searchfield.put("CV", "Y");
        searchfield.put("CVA", "Y");
        searchfield.put("CVM", "Y");
        searchfield.put("CVMA", "Y");
        searchfield.put("CVMN", "Y");
        searchfield.put("CVMP", "Y");
        searchfield.put("CVN", "Y");
        searchfield.put("CVP", "Y");
        searchfield.put("DT", "Y");
        searchfield.put("FL", "Y");
        searchfield.put("KY", "Y");
        searchfield.put("LA", "Y");
        searchfield.put("LT", "Y");
        searchfield.put("PN", "Y");
        searchfield.put("SN", "Y");
        searchfield.put("SO", "Y");
        searchfield.put("ST", "Y");
        searchfield.put("TI", "Y");
        searchfield.put("YR", "Y");
        searchfield.put("VO", "Y");
        searchfield.put("SU", "Y");

    }

    public int getStartYear(boolean hasBackFile) {
        return 1962;
    }

    protected String getBaseTableHook() {
        return "elt_master";
    }

    public DocumentBuilder newBuilderInstance() {
        return new EltDocBuilder(this);
    }

    public SearchControl newSearchControlInstance() {
        return new FastSearchControl();
    }

    public String getID() {
        return "elt";
    }

    public String getLegendID() {
        return "el";
    }

    public String getName() {
        return "EnCompassLIT";
    }

    public String getIndexName() {
        return "elt";
    }

    public String getShortName() {
        return "EnCompassLIT";
    }

    public int getMask() {
        return 1024;
    }

    public boolean hasChildren() {
        return false;
    }

    public boolean linkLocalHoldings(String linklabel) {
        if (linklabel.indexOf("NTIS") > -1) {
            return false;
        } else {
            return true;
        }
    }

    public int getSortValue() {
        return getMask();
    }

    public LinkingStrategy getLinkingStrategy() {
        return EltLinkingStrategy;
    }

    public boolean hasField(SearchField searchField, int mask) {
        if (searchfield.containsKey(searchField.getID())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasField(SearchField searchField) {
        if (searchfield.containsKey(searchField.getID())) {
            return true;
        } else {
            return false;
        }
    }

    public DataDictionary getDataDictionary() {
        return dataDictionary;
    }

    public String getSingleCharName() {
        return "L";
    }

    public String getDisplayAbrevName() {
        return "L";
    }

}