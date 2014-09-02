package org.ei.struts.backoffice.territory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public abstract class Item implements Expression
{
    protected String m_label;
    protected String m_value;
        
    public String getLabel()
    {
    	return m_label;
    }
    
    public String getValue()
    {
    	return m_value;
    }
		
}