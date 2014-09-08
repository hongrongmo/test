package org.ei.struts.backoffice.territory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomerTypes extends Items
{
    public Collection getAllCustomerTypes()
    {
        List allTypes = new ArrayList();
        allTypes.add(CustomerType.ACADEMIC);
        allTypes.add(CustomerType.ADMIN);        
        allTypes.add(CustomerType.CORPORATE);
        allTypes.add(CustomerType.GOVERNMENT);     
        allTypes.add(CustomerType.GROUP_ACADEMIC);
        allTypes.add(CustomerType.GROUP_INDUSTRIAL);
        allTypes.add(CustomerType.INDIVIDUAL);
        allTypes.add(CustomerType.LIBRARY);    
        allTypes.add(CustomerType.HOSPITAL);       

        return allTypes;
    }

}