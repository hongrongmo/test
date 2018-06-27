package org.ei.gui;


public class InternalResolver
{
	public static String resolveSearchType(String newSearchURL)
	{
//		System.out.println("New search URL:"+newSearchURL);

		if(newSearchURL == null)
		{
			return "unknown";
		}
		else if(newSearchURL.indexOf("quickSearch") > -1)
		{
			return "Quick";
		}
		else if(newSearchURL.indexOf("expertSearch") > -1)
		{
			return "Expert";
		}
		else if(newSearchURL.indexOf("easySearch") > -1)
		{
			return "Easy";
		}
		else if(newSearchURL.indexOf("tags") > -1)
		{
			return "Tags";
		}
		else if(newSearchURL.indexOf("thesHome") > -1)
		{
			return "Thesaurus";
		}

		return "unknown";
	}
}