package org.ei.struts.backoffice.territory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Industries extends Items
{
    public Collection getAllIndustries()
    {
        List allIndustries = new ArrayList();
        allIndustries.add(Industry.AEROSPACE_DEFENSE);       
        allIndustries.add(Industry.AGRICULTURAL_FORESTRY);
        allIndustries.add(Industry.AUTOMOTIVE);   
        allIndustries.add(Industry.BANKING_FINANCE);
        allIndustries.add(Industry.BIOTECHNOLOGY);
        allIndustries.add(Industry.CHEMICAL);
        allIndustries.add(Industry.CHEMICAL_PHARMA);
        allIndustries.add(Industry.CORPORATE_SALES_LEADS);
        allIndustries.add(Industry.ELECTRONICS);   
        allIndustries.add(Industry.ENG_AND_CONST);
        allIndustries.add(Industry.FOOD_HOME_PERSONAL_CARE);
        allIndustries.add(Industry.IT_COMPUTERS_SOFTWARE);
        allIndustries.add(Industry.INSURANCE);
        allIndustries.add(Industry.LIFE_SCIENCE_MISC);
        allIndustries.add(Industry.MANUFACTURING);
        allIndustries.add(Industry.METALS);
        allIndustries.add(Industry.MINING);
        allIndustries.add(Industry.OIL_AND_GAS);
        allIndustries.add(Industry.PHARMACEUTICAL_LARGE);
        allIndustries.add(Industry.PHARMACEUTICAL_SMALL);
        allIndustries.add(Industry.POWER_UTILITIES);
        allIndustries.add(Industry.PULP_AND_PAPER);
        allIndustries.add(Industry.RESEARCH_LIFE_SCIENCES);
        allIndustries.add(Industry.TELECOM);
        allIndustries.add(Industry.TRANSPORTATION);
        return allIndustries;
    }

}