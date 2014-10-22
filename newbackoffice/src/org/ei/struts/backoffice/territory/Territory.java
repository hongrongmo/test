package org.ei.struts.backoffice.territory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Territory 
{
    private String m_label;
    private Items m_locations = new Locations();
    private Items m_industries =  new Industries();
    private Items m_customertypes =  new CustomerTypes();
    
    public Territory(String label)
    {
        m_label = label;
    }

    public String getLabel()
    {
        return m_label;
    }

    public String getValue()
    {
        return m_label;
    }

    public void addLocation(Location l)
    {
        m_locations.add(l);
    }

    public void addIndustry(Industry i)
    {
        m_industries.add(i);
    }

    public void addCustomerType(CustomerType c)
    {
        m_customertypes.add(c);
    }

    public String[] getCustomerTypes()
    {
        return (String[]) (m_customertypes.getKeys()).toArray(new String[]{});
    }
    public String[] getIndustries()
    {
        return (String[]) (m_industries.getKeys()).toArray(new String[]{});
    }


    public String toQueryString()
    {
        StringBuffer territoryQueryString = new StringBuffer();
        territoryQueryString.append("SELECT SALES_REGION.REGION, CUSTOMER_MASTER.* FROM CUSTOMER_MASTER, SALES_REGION  WHERE ");


        List whereClause = new ArrayList();
    
        if(!m_customertypes.isEmpty())
        {
            whereClause.add(m_customertypes.toQueryString());
        }

        if(!m_locations.isEmpty())
        {
            whereClause.add(m_locations.toQueryString());
        }

        if(!m_industries.isEmpty())
        {
            whereClause.add(m_industries.toQueryString());
        }

        Iterator itrWhere = whereClause.iterator();
        while(itrWhere.hasNext())
        {
            territoryQueryString.append(" ( ").append((String) itrWhere.next()).append(" ) ");
            if(itrWhere.hasNext())
            {
                territoryQueryString.append(" AND ");
            }
                   
        }
 
        territoryQueryString.append(" AND CUSTOMER_MASTER.REGION_CODE=SALES_REGION.REGION_CODE ");
        territoryQueryString.append(" ORDER BY CUSTOMER_MASTER.COUNTRY ");
    
        return territoryQueryString.toString();
    }

    public static void main(String[] args)
    {
       
    }
}