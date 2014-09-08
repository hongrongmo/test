package org.ei.struts.backoffice.territory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class Territories
{
    private static Map allterritories = new LinkedHashMap();
 
    static 
    {
        Territory t = new Territory("Region 1 - Academic & Hospital Accounts");
        t.addLocation(Location.CANADA);
        t.addLocation(Location.CONNECTICUT);
        t.addLocation(Location.DELAWARE);
        t.addLocation(Location.DC);
        t.addLocation(Location.MAINE);
        t.addLocation(Location.MARYLAND);
        t.addLocation(Location.MASSACHUSETTS);
        t.addLocation(Location.NEW_HAMPSHIRE);
        t.addLocation(Location.NEW_JERSEY);
        t.addLocation(Location.NEW_YORK);
        t.addLocation(Location.PENNSYLVANIA);
        t.addLocation(Location.RHODE_ISLAND);
        t.addLocation(Location.VERMONT);
        t.addLocation(Location.WEST_VIRGINIA);
        t.addCustomerType(CustomerType.ACADEMIC);
        t.addCustomerType(CustomerType.LIBRARY);
        t.addCustomerType(CustomerType.HOSPITAL);
	    allterritories.put(t.getValue(),t);

        t = new Territory("Region 2 - Academic & Hospital Accounts");
        t.addLocation(Location.ALABAMA);
        t.addLocation(Location.ARKANSAS);
        t.addLocation(Location.FLORIDA);
        t.addLocation(Location.GEORGIA);
        t.addLocation(Location.ILLINOIS);
        t.addLocation(Location.INDIANA);
        t.addLocation(Location.IOWA);
        t.addLocation(Location.KENTUCKY);
        t.addLocation(Location.LOUISIANA);
        t.addLocation(Location.MICHIGAN);
        t.addLocation(Location.MINNESOTA);
        t.addLocation(Location.MISSISSIPPI);
        t.addLocation(Location.MISSOURI);
        t.addLocation(Location.NORTH_CAROLINA);
        t.addLocation(Location.OHIO);
        t.addLocation(Location.SOUTH_CAROLINA);
        t.addLocation(Location.TENNESSEE);
        t.addLocation(Location.VIRGINIA);
        t.addLocation(Location.WISCONSIN);
        t.addCustomerType(CustomerType.ACADEMIC);
        t.addCustomerType(CustomerType.LIBRARY);
        t.addCustomerType(CustomerType.HOSPITAL);
	    allterritories.put(t.getValue(),t);

        t = new Territory("Region 3 - Academic & Hospital Accounts");
        t.addLocation(Location.ALASKA);
        t.addLocation(Location.ARIZONA);
        t.addLocation(Location.CALIFORNIA);
        t.addLocation(Location.COLORADO);
        t.addLocation(Location.HAWAII);
        t.addLocation(Location.IDAHO);
        t.addLocation(Location.KANSAS);
        t.addLocation(Location.MONTANA);
        t.addLocation(Location.NEBRASKA);
        t.addLocation(Location.NEVADA);
        t.addLocation(Location.NEW_MEXICO);
        t.addLocation(Location.NORTH_DAKOTA);
        t.addLocation(Location.OKLAHOMA);
        t.addLocation(Location.OREGON);
        t.addLocation(Location.SOUTH_DAKOTA);
        t.addLocation(Location.TEXAS);
        t.addLocation(Location.UTAH);
        t.addLocation(Location.WASHINGTON);
        t.addLocation(Location.WYOMING);
        t.addCustomerType(CustomerType.ACADEMIC);
        t.addCustomerType(CustomerType.LIBRARY);
        t.addCustomerType(CustomerType.HOSPITAL);
	    allterritories.put(t.getValue(),t);
	    
        t = new Territory("US & Canada Government Accounts");
        t.addLocation(Location.CANADA);
        t.addLocation(Location.US);
        t.addCustomerType(CustomerType.GOVERNMENT);
 
        t = new Territory("US & Canada - Corporate Accounts 1");
        t.addLocation(Location.CANADA);
        t.addLocation(Location.US);
        t.addIndustry(Industry.OIL_AND_GAS);
        t.addIndustry(Industry.AUTOMOTIVE);
        t.addIndustry(Industry.TRANSPORTATION);
	    allterritories.put(t.getValue(),t);

        t = new Territory("US & Canada - Corporate Accounts 2");
        t.addLocation(Location.CANADA);
        t.addLocation(Location.US);
        t.addIndustry(Industry.CHEMICAL);
        t.addIndustry(Industry.PHARMACEUTICAL_LARGE);
        t.addIndustry(Industry.CHEMICAL_PHARMA);
	    allterritories.put(t.getValue(),t);

        t = new Territory("US & Canada - Corporate Accounts 3");
        t.addLocation(Location.CANADA);
        t.addLocation(Location.US);
        t.addIndustry(Industry.ELECTRONICS);
        t.addIndustry(Industry.IT_COMPUTERS_SOFTWARE);
        t.addIndustry(Industry.POWER_UTILITIES);
        t.addIndustry(Industry.TELECOM);
	    allterritories.put(t.getValue(),t);

        t = new Territory("US & Canada - Corporate Accounts 4");
        t.addLocation(Location.CANADA);
        t.addLocation(Location.US);
        t.addIndustry(Industry.AEROSPACE_DEFENSE);
        t.addIndustry(Industry.FOOD_HOME_PERSONAL_CARE);
        t.addIndustry(Industry.INSURANCE);
        t.addIndustry(Industry.MINING);
        t.addIndustry(Industry.METALS);
        t.addIndustry(Industry.MANUFACTURING);
	    allterritories.put(t.getValue(),t);

        t = new Territory("US & Canada - Corporate Accounts 5");
        t.addLocation(Location.CANADA);
        t.addLocation(Location.US);
        t.addIndustry(Industry.BIOTECHNOLOGY);
        t.addIndustry(Industry.AGRICULTURAL_FORESTRY);
	    allterritories.put(t.getValue(),t);

        t = new Territory("US & Canada - Corporate Accounts 6");
        t.addLocation(Location.CANADA);
        t.addLocation(Location.US);
        t.addIndustry(Industry.PHARMACEUTICAL_SMALL);
        t.addIndustry(Industry.RESEARCH_LIFE_SCIENCES);
        t.addIndustry(Industry.LIFE_SCIENCE_MISC);
	    allterritories.put(t.getValue(),t);

        t = new Territory("US & Canada - Corporate Accounts 7");
        t.addLocation(Location.CANADA);
        t.addLocation(Location.US);
        t.addIndustry(Industry.ENG_AND_CONST);
        t.addIndustry(Industry.PULP_AND_PAPER);
        t.addIndustry(Industry.BANKING_FINANCE);
        t.addIndustry(Industry.CORPORATE_SALES_LEADS);
	    allterritories.put(t.getValue(),t);

        t = new Territory("US & Canada - Special Accounts");
        t.addLocation(Location.CANADA);
        t.addLocation(Location.US);
        t.addCustomerType(CustomerType.GROUP_ACADEMIC);
        t.addCustomerType(CustomerType.GROUP_INDUSTRIAL);
	    allterritories.put(t.getValue(),t);
    }


	public Collection getTerritories() {
	    
	    return allterritories.values();
	    
	}

	public static Territory getTerritory(String id) {
	    
        Territory t = null;
        
        if(allterritories.containsKey(id))
        {
            t = (Territory) allterritories.get(id);    
        }

	    return t;
	}

}