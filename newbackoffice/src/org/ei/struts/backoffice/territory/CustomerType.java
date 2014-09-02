package org.ei.struts.backoffice.territory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomerType extends Item
{
    public static final CustomerType ACADEMIC           = new CustomerType("Academic");
    public static final CustomerType ADMIN              = new CustomerType("Admin");
    public static final CustomerType CORPORATE          = new CustomerType("Corporate");
    public static final CustomerType GOVERNMENT         = new CustomerType("Government");
    public static final CustomerType GROUP_ACADEMIC     = new CustomerType("Group Academic");
    public static final CustomerType GROUP_INDUSTRIAL   = new CustomerType("Group Industrial");
    public static final CustomerType INDIVIDUAL         = new CustomerType("Individual");
    public static final CustomerType LIBRARY            = new CustomerType("Library");
    public static final CustomerType HOSPITAL           = new CustomerType("Hospital");

    public CustomerType(String label)
    {
        m_label = label;
        m_value = label;
    }
    
    public String toQueryString()
    {
        StringBuffer queryString = new StringBuffer(" LOWER(");
        queryString.append("CUSTOMER_TYPE");
        queryString.append(") IN( '");
        queryString.append(getValue());
        queryString.append("') ");

        return queryString.toString();
    }

}