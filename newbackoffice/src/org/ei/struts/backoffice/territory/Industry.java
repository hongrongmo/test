package org.ei.struts.backoffice.territory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Industry extends Item
{
    public static final Industry AEROSPACE_DEFENSE       = new Industry("Aerospace / Defense","1");
    public static final Industry AGRICULTURAL_FORESTRY   = new Industry("Agricultural / Forestry","2");
    public static final Industry AUTOMOTIVE              = new Industry("Automotive","3");
    public static final Industry BANKING_FINANCE         = new Industry("Banking / Finance","4");
    public static final Industry BIOTECHNOLOGY           = new Industry("Biotechnology","5");
    public static final Industry CHEMICAL                = new Industry("Chemical","6");
    public static final Industry CHEMICAL_PHARMA         = new Industry("Chemical/Pharma Div.","7");
    public static final Industry CORPORATE_SALES_LEADS   = new Industry("Corporate Sales Leads","8");
    public static final Industry ELECTRONICS             = new Industry("Electronics","9");
    public static final Industry ENG_AND_CONST           = new Industry("Eng. & Const. Firms","10");
    public static final Industry FOOD_HOME_PERSONAL_CARE = new Industry("Food / Home / Personal Care","11");
    public static final Industry IT_COMPUTERS_SOFTWARE   = new Industry("IT – Computers/Software","12");
    public static final Industry INSURANCE               = new Industry("Insurance","13");
    public static final Industry LIFE_SCIENCE_MISC       = new Industry("Life Science Misc.","14");
    public static final Industry MANUFACTURING           = new Industry("Manufacturing","15");
    public static final Industry METALS                  = new Industry("Metals","16");
    public static final Industry MINING                  = new Industry("Mining","17");
    public static final Industry OIL_AND_GAS             = new Industry("Oil & Gas","18");
    public static final Industry PHARMACEUTICAL_LARGE    = new Industry("Pharmaceutical (Large Accounts)","19");
    public static final Industry PHARMACEUTICAL_SMALL    = new Industry("Pharmaceutical (Small Accounts)","20");
    public static final Industry POWER_UTILITIES         = new Industry("Power/Utilities","21");
    public static final Industry PULP_AND_PAPER          = new Industry("Pulp & Paper","22");
    public static final Industry RESEARCH_LIFE_SCIENCES  = new Industry("Research Life Sciences","23");
    public static final Industry TELECOM                 = new Industry("Telecom","24");
    public static final Industry TRANSPORTATION          = new Industry("Transportation","25");

    public Industry(String label, String value)
    {
        m_label = label;
        m_value = value;
    }
    
    
    public String toQueryString()
    {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" TECH_CONTACT");
        queryString.append(" IN( '");
        queryString.append(getValue());
        queryString.append("') ");

        return queryString.toString();
    }

}