package org.ei.data.chem.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;
import org.ei.fulldoc.LinkingStrategy;

public class ChemDatabase extends Database {
    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE });
    }

    private static Map<String, String> searchfield = new HashMap<String, String>();
    static {
        searchfield.put("ALL", "Y");
        searchfield.put("AB", "Y");
        searchfield.put("AF", "Y");
        searchfield.put("CR", "Y");
        searchfield.put("CN", "Y");
        searchfield.put("DT", "Y");
        searchfield.put("CV", "Y");
        searchfield.put("SN", "Y");
        searchfield.put("CL", "Y");
        searchfield.put("LA", "Y");
        searchfield.put("CO", "Y");
        searchfield.put("ST", "Y");
        searchfield.put("TI", "Y");
        searchfield.put("KY", "Y");
        searchfield.put("PN", "Y");
        searchfield.put("VO", "Y");
        searchfield.put("SU", "Y");
    }

    private LinkingStrategy ChemLinkingStrategy = new org.ei.data.compendex.runtime.CPXLinkingStrategy();

    private DataDictionary dataDictionary = new ChemDataDictionary();

    public int getStartYear(boolean hasBackFile) {
        // jam - changed for Baja build
        return 1970;
    }

    protected String getBaseTableHook() {
        return "chm_master";
    }

    public DataDictionary getDataDictionary() {
        return dataDictionary;
    }

    public DocumentBuilder newBuilderInstance() {
        return new MultiDatabaseDocBuilder();
    }

    public SearchControl newSearchControlInstance() {
        return new FastSearchControl();
    }

    public String getID() {
        return "chm";
    }

    public String getLegendID() {
        return "cm";
    }

    public String getName() {
        return "Chimica";
    }

    public String getIndexName() {
        return "chm";
    }

    /*
     * public Database getBackfile() { return backfile; }
     */

    public String getShortName() {
        return "Chimica";
    }

    public String getDisplayAbrevName() {
        return "Ch";
    }

    public String getSingleCharName() {
        return "H";
    }

    public int getMask() {
        return 128;
    }

    public int getSortValue() {
        return getMask();
    }

    public boolean linkLocalHoldings(String linklabel) {
        if (linklabel.indexOf("NTIS") > -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean hasField(SearchField searchField, int userMaskMax) {
        return searchfield.containsKey(searchField.getID());
    }

    public LinkingStrategy getLinkingStrategy() {
        return ChemLinkingStrategy;
    }

}