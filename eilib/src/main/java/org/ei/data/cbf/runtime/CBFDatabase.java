package org.ei.data.cbf.runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ei.data.c84.runtime.C84Database;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.domain.SearchField;
import org.ei.domain.sort.SortField;

public class CBFDatabase extends C84Database {
    private static Map<String, String> searchfield = new HashMap<String, String>();

    public static String C84_PREF = "c84_";
    public static int CBF_MASK = 262144;

    static {
        // verify fields

        searchfield.put("ALL", "Y");
        searchfield.put("AB", "Y");
        searchfield.put("AN", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("AF", "Y");
        searchfield.put("CL", "Y");
        searchfield.put("CN", "Y");
        searchfield.put("CF", "Y");
        searchfield.put("CV", "Y");
        searchfield.put("PU", "backfile");
        searchfield.put("DT", "Y");
        searchfield.put("PA", "backfile");
        searchfield.put("BN", "Y");
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
    }

    public List<SortField> getSortableFields() {
        return Arrays.asList(new SortField[] { SortField.RELEVANCE, SortField.AUTHOR, SortField.YEAR, SortField.SOURCE, SortField.PUBLISHER });
    }

    public int getStartYear(boolean hasBackFile) {
        return 1884;
    }

    public int getEndYear() {
        return 1969;
    }

    public DocumentBuilder newBuilderInstance() {
        return new CBFDocBuilder();
    }

    public String getID() {
        return "zbf";
    }

    public String getLegendID() {
        return "c";
    }

    public String getName() {
        return "Ei Backfile";
    }

    public String getIndexName() {
        return "c84";
    }

    public String getShortName() {
        return "Ei Backfile";
    }

    public int getMask() {
        return 262144;
    }

    public boolean hasField(SearchField searchField, int userMaskMax) {

        return searchfield.containsKey(searchField.getID());
    }

    public boolean isBackfile() {
        return false;
    }

    public int getSortValue() {
        return 1;
    }

    public String getSingleCharName() {
        return "Y";
    }

}