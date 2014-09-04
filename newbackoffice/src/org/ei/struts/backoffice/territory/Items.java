package org.ei.struts.backoffice.territory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Items implements Expression
{
    Map m_map = new HashMap();
    
    public void add(Item o)
    {
    	m_map.put(o.getValue(),o);
    }
    
    public boolean isEmpty()
    {
    	return m_map.isEmpty();
    }	
    
    protected Collection getCollection()
    {
    	return m_map.values();
    }
    
    protected Collection getKeys()
    {
    	return m_map.keySet();
    }
		
	public String toQueryString()
	{
        StringBuffer joinedItems = new StringBuffer();
        Iterator itrItems = getCollection().iterator();
        while(itrItems.hasNext())
        {
            Item anitem = (Item) itrItems.next();
            joinedItems.append(anitem.toQueryString());
            if(itrItems.hasNext())
            {
                joinedItems.append(" OR ");
            }
        }
			
		return joinedItems.toString();
	}
}