package org.ei.data.compendex.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
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

public class CPXDatabase extends Database {
    private LinkingStrategy CPXLinkingStrategy = new CPXLinkingStrategy();
    private static Map<String, String> searchfield = new HashMap<String, String>();
    static {
        searchfield.put("ALL", "Y");
        searchfield.put("AB", "Y");
        searchfield.put("AN", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("AF", "Y");
        searchfield.put("CL", "Y");
        searchfield.put("CN", "Y");
        searchfield.put("CC", "Y");
        searchfield.put("CF", "Y");
        searchfield.put("CV", "Y");
        searchfield.put("PU", "backfile");
        searchfield.put("DT", "Y");
        searchfield.put("PA", "backfile");
        searchfield.put("BN", "Y");
        searchfield.put("SN", "Y");
        searchfield.put("LA", "Y");
        searchfield.put("MH", "Y");
        searchfield.put("PI", "backfile");
        searchfield.put("PM", "backfile");
        searchfield.put("PN", "Y");
        searchfield.put("ST", "Y");
        searchfield.put("KY", "Y");
        searchfield.put("TI", "Y");
        searchfield.put("TR", "Y");
        searchfield.put("FL", "Y");
        searchfield.put("CO", "Y");
        searchfield.put("VO", "Y");
        searchfield.put("SU", "Y");
    }

    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE, SortField.PUBLISHER });
    }

    private DataDictionary dataDictionary = new CPXDataDictionary();

    public int getStartYear(boolean hasBackFile) {
        return ((hasBackFile == true) ? 1884 : 1969);
    }

    private Database backfile = new org.ei.data.c84.runtime.C84Database();

    protected String getBaseTableHook() {
        return "cpx_master";
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
        return "cpx";
    }

    public String getLegendID() {
        return "c";
    }

    public String getName() {
        return "Compendex";
    }

    public String getIndexName() {
        return "cpx";
    }

    public Database getBackfile() {
        return backfile;
    }

    public String getShortName() {
        return "Compendex";
    }

    public int getMask() {
        return 1;
    }

    public int getSortValue() {
        return getMask();
    }

    public Map<String, String> getTreatments() {

        Map<String, String> mp = new Hashtable<String, String>();

        mp.put("A", "Applications (APP)");
        mp.put("B", "Biographical (BIO)");
        mp.put("E", "Economic (ECO)");
        mp.put("X", "Experimental (EXP)");
        mp.put("G", "General review (GEN)");
        mp.put("H", "Historical (HIS)");
        mp.put("L", "Literature review (LIT)");
        mp.put("M", "Management aspects (MAN)");
        mp.put("N", "Numerical (NUM)");
        mp.put("T", "Theoretical (THR)");

        return mp;
    }

    public boolean hasField(SearchField searchField, int mask) {
        if (searchfield.containsKey(searchField.getID())) {
            String s = (String) searchfield.get(searchField.getID());
            if (s.equals("backfile")) {
                if ((32 & mask) == 32) {
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

    public LinkingStrategy getLinkingStrategy() {
        return CPXLinkingStrategy;
    }

    public boolean linkLocalHoldings(String linklabel) {
        if (linklabel.indexOf("NTIS") > -1) {
            return false;
        } else {
            return true;
        }
    }

    public String getSingleCharName() {
        return "C";
    }

}