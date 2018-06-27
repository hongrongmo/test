package org.ei.data.geobase.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;
import org.ei.domain.MultiDatabaseDocBuilder;

import org.ei.data.compendex.runtime.CPXLinkingStrategy;
import org.ei.fulldoc.LinkingStrategy;

public class GEODatabase extends Database {
    private LinkingStrategy GEOLinkingStrategy = new CPXLinkingStrategy();
    private static Map<String, String> searchfield = new HashMap<String, String>();
    static {
        searchfield.put("ALL", "Y");
        searchfield.put("AB", "Y");
        searchfield.put("AN", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("AF", "Y");
        searchfield.put("CL", "Y");
        searchfield.put("CV", "Y");
        searchfield.put("DT", "Y");
        searchfield.put("BN", "Y");
        searchfield.put("SN", "Y");
        searchfield.put("LA", "Y");
        searchfield.put("PN", "Y");
        searchfield.put("ST", "Y");
        searchfield.put("KY", "Y");
        searchfield.put("TI", "Y");
        searchfield.put("FL", "Y");
        searchfield.put("RGI", "Y");
        searchfield.put("CO", "Y");
        searchfield.put("VO", "Y");
        searchfield.put("SU", "Y");
    }

    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE });
    }

    private DataDictionary dataDictionary = new GEODataDictionary();

    public int getStartYear(boolean hasBackFile) {
        return 1973;
    }

    protected String getBaseTableHook() {
        return "geo_master";
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
        return "geo";
    }

    public String getLegendID() {
        return "g";
    }

    public String getName() {
        return "GEOBASE";
    }

    public String getIndexName() {
        return "geo";
    }

    public String getShortName() {
        return getName();
    }

    public int getMask() {
        return 8192;
    }

    public int getSortValue() {
        return getMask();
    }

    public Map<?, ?> getTreatments() {

        Map<?, ?> mp = new Hashtable<Object, Object>();

        return mp;
    }

    public boolean hasField(SearchField searchField, int userMaskMax) {
        // System.out.println("ID= "+searchField.getID());
        // System.out.println("hasField= "+searchfield.containsKey(searchField.getID()));
        // System.out.println("*****************************");
        return searchfield.containsKey(searchField.getID());
    }

    public LinkingStrategy getLinkingStrategy() {
        return GEOLinkingStrategy;
    }

    public boolean linkLocalHoldings(String linklabel) {
        if (linklabel.indexOf("NTIS") > -1) {
            return false;
        } else {
            return true;
        }
    }

    public String getSingleCharName() {
        return "G";
    }
}