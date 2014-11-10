package org.ei.data.pag.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ei.data.compendex.runtime.CPXLinkingStrategy;
import org.ei.domain.DataDictionary;
import org.ei.domain.Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.FastSearchControl;
import org.ei.domain.LimitGroups;
import org.ei.domain.SearchControl;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;
import org.ei.fulldoc.LinkingStrategy;

public class PAGDatabase extends Database {
    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.PUBLISHER, SortField.SOURCE, SortField.YEAR });
    }

    private LinkingStrategy linkingStrategy = new CPXLinkingStrategy();
    private static Map<String, String> searchfield = new HashMap<String, String>();
    private String PERPETUAL = "BPE";

    // Expert search codes legend is determined from this Map
    static {
        // searchfield.put("ALL", "Y");
        // searchfield.put("DT", "Y");
        searchfield.put("KY", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("BN", "Y");
        searchfield.put("TI", "Y");
        searchfield.put("PN", "Y");
        searchfield.put("CV", "Y");

    }

    public int getStartYear(boolean hasBackFile) {
        return getStartYear();
    }

    public int getStartYear() {
        return 1987;
    }

    public LimitGroups limitSearch(String credentials[], String fields[]) {
        boolean perpetual = isPerpetual(credentials);
        String cred = null;
        LimitGroups lg = new LimitGroups();

        for (int i = 0; i < credentials.length; i++) {
            if (credentials[i].toLowerCase().indexOf("ele") == 0 || credentials[i].toLowerCase().indexOf("che") == 0
                || credentials[i].toLowerCase().indexOf("mat") == 0 || credentials[i].toLowerCase().indexOf("com") == 0
                || credentials[i].toLowerCase().indexOf("civ") == 0 || credentials[i].toLowerCase().indexOf("sec") == 0
                || credentials[i].toLowerCase().indexOf("tnfele") == 0 || credentials[i].toLowerCase().indexOf("tnfche") == 0
                || credentials[i].toLowerCase().indexOf("tnfmat") == 0 || credentials[i].toLowerCase().indexOf("tnfcom") == 0
                || credentials[i].toLowerCase().indexOf("tnfciv") == 0 || credentials[i].toLowerCase().indexOf("tnfsec") == 0) {
                cred = credentials[i];
                if (!perpetual) {
                    cred = cred + "*";
                }

                lg.addLimiter("cl", cred);
            }
        }

        if (onlyPages(fields)) {
            lg.addLimiter("dt", "PAGE");
        } else {
            lg.addLimiter("dt", "BOOK");
        }

        if (lg.size() > 0) {
            return lg;
        }

        return null;
    }

    private boolean onlyPages(String[] fields) {
        for (int i = 0; i < fields.length; i++) {
            // System.out.println("field:"+fields[i]);
            if (fields[i].equalsIgnoreCase("ALL") || fields[i].equalsIgnoreCase("KY")) {
                return true;
            }
        }

        return false;
    }

    private boolean isPerpetual(String[] credentials) {
        int len = credentials.length;
        for (int i = 0; i < len; i++) {
            if (PERPETUAL.equals(credentials[i].toUpperCase())) {
                return true;
            }
        }

        return false;
    }

    public boolean hasField(SearchField searchField, int userMaskMax) {
        return searchfield.containsKey(searchField.getID());
    }

    protected String getBaseTableHook() {
        return "book_master";
    }

    private DataDictionary dataDictionary = new PAGDataDictionary();

    public DataDictionary getDataDictionary() {
        return dataDictionary;
    }

    public DocumentBuilder newBuilderInstance() {
        return new PAGDocBuilder(this);
    }

    public SearchControl newSearchControlInstance() {
        return new FastSearchControl();
    }

    public String getID() {
        return "pag";
    }

    public String getName() {
        return "Referex";
    }

    public String getLegendID() {
        return "pa";
    }

    public String getIndexName() {
        return "pag";
    }

    public String getShortName() {
        return "Referex";
    }

    public int getMask() {
        return 131072;
    }

    public String getUpdates(int num) throws InfrastructureException {
        return "13000-13001";
    }

    public int getSortValue() {
        return getMask();
    }

    public Map<?, ?> getTreatments() {

        Map<?, ?> mp = new Hashtable<Object, Object>();

        // mp.put("A","Applications (APP)");

        return mp;
    }

    public LinkingStrategy getLinkingStrategy() {
        return linkingStrategy;
    }

    public boolean linkLocalHoldings(String linklabel) {
        return false;
    }

    public String getSingleCharName() {
        return "P";
    }
}