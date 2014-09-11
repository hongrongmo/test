/*
 * Created on Jun 20, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.data.bd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author solovyevat
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BdEditors {
    private BdAuthors au;
    private static ArrayList<String> edElements = new ArrayList<String>();

    static {
        edElements.add("id");
        edElements.add("initials");
        edElements.add("indexedName");
        edElements.add("degrees");
        edElements.add("surname");
        edElements.add("givenName");
        edElements.add("suffix");
        edElements.add("nametext");
        edElements.add("text");
    }

    public BdEditors(String bdEditors) {
        au = new BdAuthors(bdEditors, edElements);
    }

    public String[] getSearchValue() {
        return au.getSearchValue();
    }

    public List<BdAuthor> getEditors() {
        return au.getAuthors();
    }

}
