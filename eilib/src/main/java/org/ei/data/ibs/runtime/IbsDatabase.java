package org.ei.data.ibs.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ei.data.insback.runtime.IBFLinkingStrategy;
import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.fulldoc.LinkingStrategy;

public class IbsDatabase extends Database {
    private static Map<String, String> searchfield = new HashMap<String, String>();
    static {
        searchfield.put("ALL", "Y");
        searchfield.put("AB", "Y");
        searchfield.put("AN", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("CL", "Y");
        searchfield.put("OC", "Y");
        searchfield.put("CF", "Y");
        searchfield.put("CV", "Y");
        searchfield.put("DI", "Y");
        searchfield.put("DT", "Y");
        searchfield.put("LA", "Y");
        searchfield.put("PN", "Y");
        searchfield.put("ST", "Y");
        searchfield.put("KY", "Y");
        searchfield.put("TI", "Y");
        searchfield.put("FL", "Y");
    }
    
    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE, SortField.PUBLISHER });
    }
    
  //HH 01/16/2015
    private DataDictionary dataDictionary =  org.ei.data.inspec.runtime.InspecDataDictionary.getInstance();
    
    public int getStartYear(boolean hasBackFile) {
        return 1896;
    }
    
    public int getEndYear() {
        return 1969;
    }
    
    private LinkingStrategy linkingStrategy = new IBFLinkingStrategy();
    
    protected String getBaseTableHook() {
        return "ibf_master";
    }
    
    
    public DocumentBuilder newBuilderInstance() {
        return new IbsDocBuilder(this);
    }
    
    public SearchControl newSearchControlInstance() {
        return new FastSearchControl();
    }
    
    /*
     * Because this a backfile overide getLastFourUpdates so that never returns a hit from FAST.
     */
    
    public String getUpdates(int num) throws InfrastructureException {
        return "13000-13001";
    }
    
    public DataDictionary getDataDictionary() {
        return dataDictionary;
    }
    
    public String getID() {
        return "ibs";
    }
    
    public String getLegendID() {
        return "ia";
    }
    
    public String getName() {
        return "Inspec Archive";
    }
    
    public String getIndexName() {
        return "ibf";
    }
    
    public boolean isBackfile() {
        // JM This is not the INS Backfile - it is a standalone database
        return false;
    }
    
    public String getShortName() {
        return "Inspec Archive";
    }
    
    public boolean hasField(SearchField searchField) {
        return false;
    }
    
    public boolean hasField(SearchField searchField, int mask) {
        if (searchfield.containsKey(searchField.getID())) {
            return true;
        } else {
            return false;
        }
    }
    
    
    public LinkingStrategy getLinkingStrategy() {
        return linkingStrategy;
    }
    
    public int getMask() {
        return 1048576;
    }
    
    public int getSortValue() {
        return 2;
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
    
    public boolean linkLocalHoldings(String linklabel) {
        if (linklabel.indexOf("NTIS") > -1) {
            return false;
        } else {
            return true;
        }
    }
    
    public String getSingleCharName() {
        return "F";
    }
    
    
}