package org.ei.data.inspec.runtime;

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
import org.ei.data.compendex.runtime.CPXLinkingStrategy;
import org.ei.fulldoc.LinkingStrategy;

public class InspecDatabase extends Database {
    private static Map<String, String> searchfield = new HashMap<String, String>();
    static {
        searchfield.put("ALL", "Y");
        searchfield.put("AB", "Y");
        searchfield.put("AN", "Y");
        searchfield.put("AI", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("AF", "Y");
        searchfield.put("CI", "Y");
        searchfield.put("CL", "Y");
        searchfield.put("OC", "backfile");
        searchfield.put("CN", "Y");
        searchfield.put("CF", "Y");
        searchfield.put("CV", "Y");
        searchfield.put("DI", "Y");
        searchfield.put("DT", "Y");
        searchfield.put("BN", "Y");
        searchfield.put("SN", "Y");
        searchfield.put("LA", "Y");
        searchfield.put("MI", "Y");
        searchfield.put("NI", "Y");
        searchfield.put("PN", "Y");
        searchfield.put("ST", "Y");
        searchfield.put("KY", "Y");
        searchfield.put("TI", "Y");
        searchfield.put("TR", "Y");
        searchfield.put("FL", "Y");
        searchfield.put("CO", "Y");
        searchfield.put("VO", "Y");
        searchfield.put("SU", "Y");
        searchfield.put("PID", "Y");
    }

    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE, SortField.PUBLISHER });
    }

    public int getStartYear(boolean hasBackFile) {
        return ((hasBackFile == true) ? 1896 : 1969);
    }

    private LinkingStrategy InspecLinkingStrategy = new CPXLinkingStrategy();

    private DataDictionary dataDictionary = new InspecDataDictionary();

    private Database backfile = new org.ei.data.insback.runtime.InsBackDatabase();

    protected String getBaseTableHook() {
        return "new_ins_master";
    }

    public DataDictionary getDataDictionary() {
        return dataDictionary;
    }

    public Database getBackfile() {
        return backfile;
    }

    public boolean hasField(SearchField searchField, int mask) {
        if (searchfield.containsKey(searchField.getID())) {
            String s = (String) searchfield.get(searchField.getID());
            if (s.equals("backfile")) {
                if ((4096 & mask) == 4096) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public DocumentBuilder newBuilderInstance() {
        return new InspecDocBuilder(this);
    }

    public SearchControl newSearchControlInstance() {
        return new FastSearchControl();
    }

    public String getID() {
        return "ins";
    }

    public String getLegendID() {
        return "i";
    }

    public String getName() {
        return "Inspec";
    }

    public String getIndexName() {
        return "ins";
    }

    public String getShortName() {
        return "Inspec";
    }

    public int getMask() {
        return 2;
    }

    public int getSortValue() {
        return getMask();
    }

    public Map<String, String> getTreatments() {

        Map<String, String> mp = new Hashtable<String, String>();

        mp.put("A", "Applications (APP)");
        mp.put("B", "Bibliography (BIB)");
        mp.put("E", "Economic (ECO)");
        mp.put("G", "General review (GEN)");
        mp.put("N", "New development (NEW)");
        mp.put("P", "Practical (PRA)");
        mp.put("R", "Product review (PRO)");
        mp.put("T", "Theoretical (THR)");
        mp.put("X", "Experimental (EXP)");

        return mp;
    }

    public LinkingStrategy getLinkingStrategy() {
        return InspecLinkingStrategy;
    }

    public boolean linkLocalHoldings(String linklabel) {
        if (linklabel.indexOf("NTIS") > -1) {
            return false;
        } else {
            return true;
        }
    }

    public String getSingleCharName() {
        return "I";
    }

}