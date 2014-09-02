package org.ei.struts.backoffice.territory;


public class Country extends Location
{
    public static final Country CANADA = new Country("Canada", new String[]{"CA", "Canada"});
    public static final Country US = new Country("United States", new String[]{"United States", "US", "USA", "U.S."});

    private String[] m_abbreviations;
    
    public Country(String name, String[] abbreviations)
    {
        m_label = name;
        m_abbreviations = abbreviations;
    }

    public String getAbbreviations()
    {
        StringBuffer joinedAbbrev = new StringBuffer();
        for (int x=0; x < m_abbreviations.length; x++)
        {
            joinedAbbrev.append("'").append(m_abbreviations[x].toLowerCase()).append("'");
            if(x != m_abbreviations.length-1)
            {
                joinedAbbrev.append(",");
            }
        }

        return joinedAbbrev.toString();
    }
    
    public String toQueryString()
    {
        StringBuffer queryString = new StringBuffer(" LOWER(COUNTRY) IN (");
        queryString.append(getAbbreviations());
        queryString.append(") ").toString();

        return queryString.toString();
    }

}