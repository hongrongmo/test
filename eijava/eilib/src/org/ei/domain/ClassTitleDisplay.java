<<<<<<< HEAD
package org.ei.domain;

public class ClassTitleDisplay
{
    private final static String NO_DATA_MSG = "No data available";

	public static String getDisplayTitle(String title)
	{
		//System.out.println(" in getDisplayTitle ");
		if((title == null) || title.equals(""))
		{
		    title = NO_DATA_MSG;
        }

		StringBuffer buf = new StringBuffer();
		String[] strings = title.split("\\(:\\)");
		buf.append("<UL>");
		for(int i=0; i<strings.length;i++)
		{
		    if((strings != null) && (strings[i].trim().length() != 0))
		    {
    			buf.append("<li>");
    			if(i==strings.length-1)
    			{
    				buf.append("<b>");
    			}

    			int j = 200;

    			if(strings[i].length() > j)
    			{
    				while(strings[i].charAt(j) != ' ')
    				{
    					j--;
    				}
    				strings[i] = strings[i].substring(0,j);
    				buf.append(strings[i]);
    				buf.append("...");
    			}
    			else
    			{
    				buf.append(strings[i]);
    			}

    			if(i==strings.length-1)
    			{
    				buf.append("</b>");
    			}

    			buf.append("</li>");
            }
		}
		buf.append("</UL>");

		return buf.toString().replaceAll("'"," ");
	}

	public static String getDisplayTitle2(String title)
	{
		System.out.println(" in getDisplayTitle2 ");
		if((title == null) || title.equals(""))
		{
		    title = NO_DATA_MSG;
        }
		StringBuffer buf = new StringBuffer();
		String[] strings = title.split("\\(:\\)");
		buf.append("<ul type='circle'>");
		for(int i=0; i<strings.length;i++)
		{
		    if((strings != null) && (strings[i].trim().length() != 0))
		    {
    			buf.append("<li>");
    			buf.append(strings[i]);
    			buf.append("</li>");
            }
		}
		buf.append("</ul>");

		return buf.toString();
	}

	public static String getDisplayTitle3(String title)
	{

		if((title == null) || title.equals(""))
		{
			title = NO_DATA_MSG;
		}
		StringBuffer buf = new StringBuffer();
		String[] strings = title.split("\\(:\\)");

		for(int i=0; i<strings.length;i++)
		{
			if((strings != null) && (strings[i].trim().length() != 0))
			{
				buf.append(strings[i]);
			}
		}

		return buf.toString();
	}
=======
package org.ei.domain;

public class ClassTitleDisplay
{
    private final static String NO_DATA_MSG = "No data available";
    
	public static String getDisplayTitle(String title)
	{
		if((title == null) || title.equals(""))
		{
		    title = NO_DATA_MSG;
        }    

		StringBuffer buf = new StringBuffer();
		String[] strings = title.split("\\(:\\)");
		buf.append("<UL>");
		for(int i=0; i<strings.length;i++)
		{
		    if((strings != null) && (strings[i].trim().length() != 0))
		    {
    			buf.append("<li>");
    			if(i==strings.length-1)
    			{
    				buf.append("<b>");
    			}
    
    			int j = 200;
    
    			if(strings[i].length() > j)
    			{
    				while(strings[i].charAt(j) != ' ')
    				{
    					j--;
    				}
    				strings[i] = strings[i].substring(0,j);
    				buf.append(strings[i]);
    				buf.append("...");
    			}
    			else
    			{
    				buf.append(strings[i]);
    			}
    
    			if(i==strings.length-1)
    			{
    				buf.append("</b>");
    			}
    
    			buf.append("</li>");
            }
		}
		buf.append("</UL>");

		return buf.toString().replaceAll("'"," ");
	}

	public static String getDisplayTitle2(String title)
	{

		if((title == null) || title.equals(""))
		{
		    title = NO_DATA_MSG;
        }    
		StringBuffer buf = new StringBuffer();
		String[] strings = title.split("\\(:\\)");
		buf.append("<ul type='circle'>");
		for(int i=0; i<strings.length;i++)
		{
		    if((strings != null) && (strings[i].trim().length() != 0))
		    {
    			buf.append("<li>");
    			buf.append(strings[i]);
    			buf.append("</li>");
            }
		}
		buf.append("</ul>");

		return buf.toString();
	}
>>>>>>> 15dca715b02cb03c5fa8fd5c16635f7e802f1eae
}