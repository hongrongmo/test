package org.ei.domain.personalization;
/*
    UI Helper class

    Used to centralize XML production for User Interface elements
    which are customizeable.

    Default UI elements are harcoded

    Optional Items are passed in in a list (which may be empty!)
*/
import org.ei.books.collections.ReferexCollection;

public class GlobalLinks {

  public static String toXML(String[] customizedFeatures)
  {

    StringBuffer strBufGlobalLinksXML = new StringBuffer();

    boolean hasInsCpx = false;
    boolean hasThes = false;
    boolean hasBooks = false;
    boolean hasRef = true;
    boolean hasEasy = true;
    boolean hasTag = true;

    String ppd = null;


    for(int i=0; i<customizedFeatures.length; ++i)
    {
        if(customizedFeatures[i].equalsIgnoreCase("CPX") ||
           customizedFeatures[i].equalsIgnoreCase("INS"))
        {
            hasInsCpx = true;
        }

        if(customizedFeatures[i].equalsIgnoreCase("THS"))
        {
            hasThes = true;
        }

        ReferexCollection[] allcols = ReferexCollection.allcolls;
        for(int idx = 0; idx < allcols.length; idx++)
        {
          if(customizedFeatures[i].equalsIgnoreCase(allcols[idx].getAbbrev()))
          {
               hasBooks = true;
               break;
          }
        }

        if(customizedFeatures[i].equalsIgnoreCase("REF"))
        {
  	      hasRef = false;
        }

  			if(customizedFeatures[i].equalsIgnoreCase("EZY"))
  			{
  				hasEasy = false;
  			}

  			if(customizedFeatures[i].equalsIgnoreCase("TAG"))
  			{
  				hasTag = false;
  			}

  			if(customizedFeatures[i].indexOf("PPD") > -1)
  			{
  				ppd = customizedFeatures[i];
  			}
    }

		if(ppd != null)
		{
  			strBufGlobalLinksXML.append("<GLOBAL-MESSAGE>");
  			strBufGlobalLinksXML.append(ppd);
  			strBufGlobalLinksXML.append("</GLOBAL-MESSAGE>");
		}
		strBufGlobalLinksXML.append("<GLOBAL-LINKS>");
		// UI features dependent on user settings
		if(hasTag)
		{
      	strBufGlobalLinksXML.append("<TAGGROUPS/>");
		}
		// UI features dependent on user settings
		if(hasEasy)
		{
			strBufGlobalLinksXML.append("<EASY/>");
		}

		// UI features that are hardcoded defaults
		strBufGlobalLinksXML.append("<QUICK/>");
		strBufGlobalLinksXML.append("<EXPERT/>");

		// jam -- ebook and thesaurus have switched spots
		// jam 11-4-2004 - ebook and thesaurus have switched BACK
		if((hasInsCpx) && (hasThes))
		{
			strBufGlobalLinksXML.append("<THESAURUS/>");
		}

    // UI features dependent on user settings
    if(hasBooks)
    {
        strBufGlobalLinksXML.append("<BOOK/>");
    }

    if(hasRef)
    {
	    strBufGlobalLinksXML.append("<REFERENCE/>");
    }

    // rest of UI features are hardcoded defaults
    strBufGlobalLinksXML.append("<HELP/>");
    strBufGlobalLinksXML.append("</GLOBAL-LINKS>");

    return strBufGlobalLinksXML.toString();
  }

}

