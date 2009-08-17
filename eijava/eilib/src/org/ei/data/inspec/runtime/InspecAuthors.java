/*
 * Created on Jun 20, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ei.data.inspec.runtime;
import java.util.*;

import org.ei.data.bd.BdAuthors;
import org.ei.data.bd.loadtime.*;


/**
 * @author solovyevat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InspecAuthors
{
    private BdAuthors au ;
    private static ArrayList  auElements = new ArrayList();

    static
    {      
        auElements.add("indexedName");
        auElements.add("affidStr");
        //auElements.add("foreNames"); for 2010 addition
       // auElements.add("initials");
       // auElements.add("suffix");
       //auElements.add("eAddress"); for 2010 addition
       //auElements.add("id");for 2010 addition
    }

    public InspecAuthors(String bdAuthors)
    { 
    	au = new BdAuthors(bdAuthors,auElements);
    }
    
    public String[] getSearchValue()
    { 
    	return au.getSearchValue();
    }
    public List getInspecAuthors()
    { 
    	return au.getAuthors();
    }

}
