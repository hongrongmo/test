package org.ei.struts.backoffice.territory;


public class USState extends Location
{
    private String m_name;
    private String m_abbreviations;
    private static final Location parentCountry = Country.US;
    
    public USState(String name, String abbreviations)
    {
        m_label = name;
        m_value = name;

        m_abbreviations = abbreviations;
    }

    public String getAbbreviations()
    {
        return m_abbreviations;
    }    
    
    public String toQueryString()
    {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" (");
        queryString.append(parentCountry.toQueryString());
        queryString.append(" AND ");
        queryString.append(" LOWER(TRANSLATE(STATE,'0123456789*/-.-()[],''','0')) IN ").append(getAbbreviations());
        queryString.append(" )");
        
        return queryString.toString();
    }

}