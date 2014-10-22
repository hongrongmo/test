package org.ei.struts.backoffice.territory;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LocationGroup extends Location
{
    public static final Country BELGIUM = new Country("Belgium", new String[]{"Belgium", "bel"});
    public static final Country LUXEMBOURG = new Country("Luxembourg", new String[]{"Luxembourg", "lux"});
    public static final Country NETHERLANDS = new Country("Netherlands", new String[]{"Netherlands", "neth"});
    public static final Country UK = new Country("United Kingdom", new String[]{"United Kingdom", "england", "great britain", "britain", "UK"});
    
    public static final Country GERMANY = new Country("Germany", new String[]{"Germany", "ger"});
    public static final Country AUSTRIA = new Country("Austria", new String[]{"Austria", "aus"});
    public static final Country SWITZERLAND = new Country("Switzerland", new String[]{"Switzerland", "swi", "switz"});
    public static final Country RUSSIA = new Country("Russia", new String[]{"Russia", "rus", "USSR"});
    public static final Country FRANCE = new Country("France", new String[]{"France", "fr"});
    public static final Country SPAIN = new Country("Spain", new String[]{"Spain", "sp", "esp"});
    public static final Country ITALY = new Country("Italy", new String[]{"Italy", "it"});
    public static final Country PORTUGAL = new Country("Portugal", new String[]{"Portugal"});
    public static final Country DENMARK = new Country("Denmark", new String[]{"Denmark", "dn"});
    public static final Country NORWAY = new Country("Norway", new String[]{"Norway", "nor"});
    public static final Country SWEDEN = new Country("Sweden", new String[]{"Sweden", "swe"});

    public static final Country CHINA = new Country("China", new String[]{"China", "ch"});
    public static final Country INDIA = new Country("India", new String[]{"India", "in"});
 
/*
Czech Republic
Slovak Republic
Hungary
Slovenia
Croatia
Serbia
Montenegro
Bosnia
Herzegovina
Macedonia
Romania
Bulgaria
Lithuania
Latvia
Estonia
*/
 

    public static final LocationGroup BENELUX = new LocationGroup("Benelux", new Country[]{BELGIUM, NETHERLANDS, LUXEMBOURG});
    public static final LocationGroup SCANDINAVIA = new LocationGroup("Scandinavia", new Country[]{DENMARK, NORWAY, SWEDEN});
    public static final LocationGroup EASTERN_EUROPE = new LocationGroup("Eastern Europe", new Country[]{});

    private List m_group;
    
    public LocationGroup(String name, Location[] locations)
    {
        m_label = name;
        m_group = Arrays.asList(locations);
    }

    public String toQueryString()
    {
        StringBuffer joinedLocations = new StringBuffer();
        Iterator itrCountries = m_group.iterator();
        while(itrCountries.hasNext())
        {
            Location alocation = (Location) itrCountries.next();
            joinedLocations.append(alocation.toQueryString());
            if(itrCountries.hasNext())
            {
                joinedLocations.append(" OR ");
            }
        }
			
		return joinedLocations.toString();
			
    }

}