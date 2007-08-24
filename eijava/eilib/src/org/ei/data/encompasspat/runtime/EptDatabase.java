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

import java.util.*;
import org.ei.domain.*;
import org.ei.fulldoc.LinkingStrategy;
import org.ei.data.compendex.runtime.CPXLinkingStrategy;

public class EptDatabase extends Database {

    private LinkingStrategy EptLinkingStrategy = new CPXLinkingStrategy();

    private static Map searchfield = new HashMap();

    static {
        searchfield.put("ALL", "Y");
        searchfield.put("TI", "Y");
        searchfield.put("AU", "Y");
        searchfield.put("AF", "Y");
        searchfield.put("AB", "Y");
        searchfield.put("PC", "Y");
        searchfield.put("PM", "Y");
        searchfield.put("CV", "Y");
        searchfield.put("CR", "Y");
        searchfield.put("IP", "Y");

    }

    public int getStartYear(boolean hasBackFile) {
        return 1990;
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

    public String getName() {
        return "EnCompassPAT";
    }

    public String getIndexName() {
        return "ept";
    }

    public String getShortName() {
        return "EnCompassPAT";
    }

    public String getDisplayAbrevName()
    {
        return "P";
    }

    public String getSingleCharName()
    {
        return "P";
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

    public boolean hasField(SearchField searchField) {
        return searchfield.containsKey(searchField.getID());
    }

}
