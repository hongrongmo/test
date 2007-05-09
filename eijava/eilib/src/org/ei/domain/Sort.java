/*
 * Created on Jul 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Sort
{
	protected static Log log = LogFactory.getLog(Sort.class);
	
    private static Sort defaultSort = new Sort(Sort.DEFAULT_FIELD, Sort.DOWN);
    public static Sort getDefaultSortOption()
    {
        return defaultSort;
    }

	public static final String DISPLAY_UP = "Ascending";
	public static final String DISPLAY_DOWN = "Descending";

    public static final String UP = "up";
    public static final String DOWN = "dw";

	public static final String PUB_YEAR_FIELD = "yr";
	public static final String RELEVANCE_FIELD = "relevance";
	
    public static final String DEFAULT_FIELD = RELEVANCE_FIELD;

    private static final String ASC = "+";
    private static final String DESC = "-";

    private String sortField;
    private String sortDirection;

    public Sort(String field, String dir)
    {
        if (field != null)
        {
            setSortField(field);
        }
        else
        {
			setSortField(DEFAULT_FIELD);
        }

        if (dir != null)
        {
            setSortDirection(dir);
        }
        else
        {
            setSortDirection(DOWN);
        }

    }

    /**
     * @return
     */
    public String getSortDirection()
    {
        return sortDirection;
    }

    /**
     * @return
     */
    public String getSortField()
    {
        return sortField;
    }

    /**
     * @param string
     */
    public void setSortDirection(String string)
    {
        sortDirection = string;
    }

    /**
     * @param string
     */
    public void setSortField(String string)
    {
        sortField = string;
    }

	public static String displayDir(String dir)
	{
		if (UP.equalsIgnoreCase(dir))
		{
			return DISPLAY_UP;
		}
		else
		{
			return DISPLAY_DOWN;
		}
	}
    public static String not(String dir)
    {
        if (UP.equalsIgnoreCase(dir))
        {
            return DOWN;
        }
        else
        {
            return UP;
        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append(this.sortField);

        return sb.toString();
    }

    public String toXML()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("<SORT-OPTION>").append(this.sortField).append("</SORT-OPTION>");
        sb.append("<SORT-DIRECTION>").append(this.sortDirection).append("</SORT-DIRECTION>");

        return sb.toString();
    }

}
