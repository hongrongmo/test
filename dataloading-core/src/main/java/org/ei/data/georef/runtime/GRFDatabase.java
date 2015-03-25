package org.ei.data.georef.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ei.domain.DataDictionary;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;
import org.ei.data.compendex.runtime.CPXLinkingStrategy;
import org.ei.fulldoc.LinkingStrategy;
import org.ei.domain.SearchFields;
import org.ei.data.georef.runtime.GRFDocBuilder;

public class GRFDatabase extends Database {
    private LinkingStrategy linkingStrategy = new CPXLinkingStrategy();
    private static Map<String, String> searchfield = new HashMap<String, String>();
    static {
        searchfield.put(SearchFields.ALL.getID(), "Y");
        searchfield.put(SearchFields.AB.getID(), "Y");
        searchfield.put(SearchFields.AN.getID(), "Y");
        searchfield.put(SearchFields.AU.getID(), "Y");
        searchfield.put(SearchFields.AF.getID(), "Y");
        searchfield.put(SearchFields.CL.getID(), "Y");
        searchfield.put(SearchFields.CV.getID(), "Y");
        searchfield.put(SearchFields.DT.getID(), "Y");
        searchfield.put(SearchFields.BN.getID(), "Y");
        searchfield.put(SearchFields.SN.getID(), "Y");
        searchfield.put(SearchFields.CN.getID(), "Y");
        searchfield.put(SearchFields.LA.getID(), "Y");
        searchfield.put(SearchFields.PN.getID(), "Y");
        searchfield.put(SearchFields.ST.getID(), "Y");
        searchfield.put(SearchFields.KY.getID(), "Y");
        searchfield.put(SearchFields.TI.getID(), "Y");
        searchfield.put(SearchFields.FL.getID(), "Y");
        searchfield.put(SearchFields.CO.getID(), "Y");
        searchfield.put(SearchFields.AV.getID(), "Y");
        searchfield.put(SearchFields.YR.getID(), "Y");
        searchfield.put(SearchFields.RN.getID(), "Y");
        searchfield.put(SearchFields.CF.getID(), "Y");
        searchfield.put(SearchFields.VO.getID(), "Y");
        searchfield.put(SearchFields.SU.getID(), "Y");

    }

    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE, SortField.PUBLISHER });
    }

    private DataDictionary dataDictionary = GRFDataDictionary.getInstance();

    public int getStartYear(boolean hasBackFile) {
        return 1785;
    }

    protected String getBaseTableHook() {
        return "GEOREF_MASTER";
    }

    public DataDictionary getDataDictionary() {
        return dataDictionary;
    }

    public DocumentBuilder newBuilderInstance() {
        return new GRFDocBuilder(this);
    }

    public SearchControl newSearchControlInstance() {
        return new FastSearchControl();
    }

    public String getID() {
        return "grf";
    }

    public String getLegendID() {
        return "f";
    }

    public String getName() {
        return "GeoRef";
    }

    public String getIndexName() {
        return "grf";
    }

    public String getShortName() {
        return "GeoRef";
    }

    public int getMask() {
        return 2097152;
    }

    public int getSortValue() {
        return (DatabaseConfig.GEO_MASK + 1);
    }

    public Map<?, ?> getTreatments() {

        Map<?, ?> mp = new Hashtable<Object, Object>();

        return mp;
    }

    public boolean hasField(SearchField searchField, int userMaskMax) {
        return searchfield.containsKey(searchField.getID());
    }

    public LinkingStrategy getLinkingStrategy() {
        return linkingStrategy;
    }

    public boolean linkLocalHoldings(String linklabel) {
        if (linklabel.indexOf("NTIS") > -1) {
            return false;
        } else {
            return true;
        }
    }

    public String getSingleCharName() {
        return "X";
    }
}
