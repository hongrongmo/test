/*
 * Created on Jun 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.data.encompasspat.runtime;

/**
 * @author Tsolovye
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

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

public class EptDatabase extends Database {

    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR });
    }

    private LinkingStrategy EptLinkingStrategy = new CPXLinkingStrategy();
    private DataDictionary dataDictionary = new EptDataDictionary();

    private static Map<String, String> searchfield = new HashMap<String, String>();

    static {
        searchfield.put("AB", "Y");
        searchfield.put("AC", "Y");
        searchfield.put("AD", "Y");
        searchfield.put("AF", "Y");
        searchfield.put("AJ", "Y");
        searchfield.put("All", "Y");
        searchfield.put("AN", "Y");
        searchfield.put("AP", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("CL", "Y");
        searchfield.put("CR", "Y");
        searchfield.put("CV", "Y");
        searchfield.put("CVA", "Y");
        searchfield.put("CVM", "Y");
        searchfield.put("CVMA", "Y");
        searchfield.put("CVMN", "Y");
        searchfield.put("CVMP", "Y");
        searchfield.put("CVN", "Y");
        searchfield.put("CVP", "Y");
        searchfield.put("DPID", "Y");
        searchfield.put("RO", "Y");
        searchfield.put("DG", "Y");
        searchfield.put("FL", "Y");
        searchfield.put("PID", "Y");
        searchfield.put("KY", "Y");
        searchfield.put("LA", "Y");
        searchfield.put("LT", "Y");
        searchfield.put("PA", "Y");
        searchfield.put("PAC", "Y");
        searchfield.put("PAM", "Y");
        searchfield.put("PC", "Y");
        searchfield.put("PCO", "Y");
        searchfield.put("PM", "Y");
        searchfield.put("PRC", "Y");
        searchfield.put("PRD", "Y");
        searchfield.put("PRN", "Y");
        searchfield.put("TI", "Y");
        searchfield.put("YR", "Y");

    }

    public DataDictionary getDataDictionary() {
        return dataDictionary;
    }

    public int getStartYear(boolean hasBackFile) {
        // jam - changed for Baja build
        return 1963;
    }

    protected String getBaseTableHook() {
        return "ept_master";
    }

    public DocumentBuilder newBuilderInstance() {
        return new EptDocBuilder(this);
    }

    public SearchControl newSearchControlInstance() {
        return new FastSearchControl();
    }

    public String getID() {
        return "ept";
    }

    public String getLegendID() {
        return "ep";
    }

    public String getName() {
        return "EnCompassPAT";
    }

    public String getIndexName() {
        return "ept";
    }

    public String getShortName() {
        return "EnCompassPAT";
    }

    public String getDisplayAbrevName() {
        return "P";
    }

    public String getSingleCharName() {
        // JM CHanged 'P' to 'M' - Referex uses P and was part of EV2 first
        return "M";
    }

    public int getMask() {
        return 2048;
    }

    public boolean hasChildren() {
        return false;
    }

    public boolean linkLocalHoldings(String linklabel) {
        return false;
    }

    public int getSortValue() {
        return getMask();
    }

    public LinkingStrategy getLinkingStrategy() {
        return EptLinkingStrategy;
    }

    public boolean hasField(SearchField searchField, int mask) {
        if (searchfield.containsKey(searchField.getID())) {
            return true;
        } else {
            return false;
        }
    }
}
