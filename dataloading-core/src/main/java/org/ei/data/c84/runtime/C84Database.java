package org.ei.data.c84.runtime;

import org.ei.domain.*;

import java.util.Map;
import java.util.Hashtable;

import org.ei.fulldoc.LinkingStrategy;
import org.ei.domain.sort.SortField;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SearchException;

import java.util.List;
import java.util.Arrays;

public class C84Database extends Database {

    public int getStartYear(boolean hasBackFile) {
        return 1884;
    }

    private LinkingStrategy linkingStrategy = new C84LinkingStrategy();

    protected String getBaseTableHook() {
        return "c84_master";
    }

    public List getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE, SortField.PUBLISHER });
    }

    /*
     * Because this a backfile overide getLastFourUpdates so that never returns a hit from FAST.
     */

    public String getUpdates(int num) throws InfrastructureException {
        return "13000-13001";
    }

    public DocumentBuilder newBuilderInstance() {
        return new C84DocBuilder(this);
    }

    public SearchControl newSearchControlInstance() {
        return new FastSearchControl();
    }

    public String getID() {
        return "c84";
    }

    public String getLegendID() {
        return "c";
    }

    public String getName() {
        return "Compendex";
    }

    public boolean isBackfile() {
        return true;
    }

    public String getIndexName() {
        return "c84";
    }

    public String getShortName() {
        return "Compendex";
    }

    public int getMask() {
        return 32;
    }

    public LinkingStrategy getLinkingStrategy() {
        return linkingStrategy;
    }

    public boolean hasField(SearchField searchField) {
        return false;
    }

    public int getSortValue() {
        return 1;
    }

    public Map getTreatments() {
        Map mp = new Hashtable();
        return mp;
    }

    public String getSingleCharName() {
        return "Z";
    }

    public boolean linkLocalHoldings(String linklabel) {
        if (linklabel.indexOf("NTIS") > -1) {
            return false;
        } else {
            return true;
        }
    }

    public DataDictionary getDataDictionary() {
        return null;
    }
}