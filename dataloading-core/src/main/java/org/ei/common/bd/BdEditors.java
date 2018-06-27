/*
 * Created on Jun 20, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.common.bd;
import java.util.*;
//import org.ei.data.bd.loadtime.*;


/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BdEditors
{
    private BdAuthors au ;
    private static ArrayList  edElements = new ArrayList();

    static
    {
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

    public BdEditors(String bdEditors){ au = new BdAuthors(bdEditors,edElements);}
    public String[] getSearchValue(){ return au.getSearchValue();}
    public List getEditors(){ return au.getAuthors();}

}
