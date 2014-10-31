
//
//
// This class should be refactored into EiNavigator
// EiNavigator could implement the generic
// with specific EiNavigator subclasses overriding the default behavior when necessary
//
//

package org.ei.domain.navigators;

import org.ei.domain.DatabaseConfig;
import org.apache.oro.text.perl.*;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class ConvertModifierTitle
{
    // aunavs longer than this will return ResultNavigator.REMOVE_MODIFIER
    // indicating ResultNavigator to remove this modifier
    private final static int MAX_AUNAV_LENGTH = 50;

	//protected static Log log = LogFactory.getLog(ConvertModifierTitle.class);

	public String getTitle(String code, String navName, DatabaseConfig dbConfig)
	{
    	String title = null;
		Perl5Util p5 = new Perl5Util();

		if((code != null) && (code.length() > 0))
		{
			if(navName.equalsIgnoreCase(EiNavigator.CL))
			{
				title = dbConfig.getClassTitle(code);
				if((title == null) || (title.equals(code)))
				{
					// if the code and title are the same
					// try uppercasing (CL is used for Book Collections)
	                title = p5.substitute("s/(\\w+)/\\u$1/g",code);
				}
			}
			else if(navName.equalsIgnoreCase(EiNavigator.DT))
			{
				title = dbConfig.getDtTitle(code.toUpperCase());
				if(title == null)
				{
				    title = ResultNavigator.REMOVE_MODIFIER;
				}
			}
			else if(navName.equalsIgnoreCase(EiNavigator.PAC))
			{
				title = dbConfig.getAuthorityCode(code.toUpperCase());
				if(title == null)
				{
				    title = ResultNavigator.REMOVE_MODIFIER;
				}
			}
			// Roles are stored in the PUC navigator
			else if(navName.equalsIgnoreCase(EiNavigator.RO))
			{
        // RO title Convertion Code is in RONavigator class
        // RONavigator is a virtual navigator for the PUC Navifgator
        // The titles are converted before the RO Nvaigator is created
        // from the PUC navigator - so we convert them inside the RONavigator class
        // instead
			}
			else if(navName.equalsIgnoreCase(EiNavigator.AU))
			{

        // look a string, ed*, surrounded by () or []
				if(p5.match("m/(\\[|\\()ed[^\\1].*(\\]|\\))/i",code))
				{
				    //log.info(" REMOVING " + code);
				    title = ResultNavigator.REMOVE_MODIFIER;
				    return title;
				}
                // look for exact matches of a sinlge word/words
				if(p5.match("m/^(\\W)?conf$/i",code))
                {
				    //log.info(" REMOVING " + code);
				    title = ResultNavigator.REMOVE_MODIFIER;
				    return title;
                }
                // look for a list of specific strings between word breaks
				if(p5.match("m/\\b(collaboration|editors view|various|the editors|co.?authors)\\b/i",code))
				{
				    //log.info(" REMOVING " + code);
				    title = ResultNavigator.REMOVE_MODIFIER;
				    return title;
				}

                // max length for Author name
				if(code.length() > ConvertModifierTitle.MAX_AUNAV_LENGTH)
				{
				    title = ResultNavigator.REMOVE_MODIFIER;
				    return title;
				}

				// translate first char of every word
				// to Uppercase. Only available in Perl/ORO
				// (\\u not supported in java 1.4 regexp pkg)
				title = p5.substitute("s/(\\w+)/\\u$1/g",code);
				//log.debug("["+title+"]");
				// this will only match a single word
				// single name authors (like "Bono" get no comma !
//				if(!p5.match("m/^(\\w+)$/",title))
//				{
					// insert a comma after the first word
//					title = p5.substitute("s/^(\\w+)/$1,/",title);
//				}
				// Or this does the same thing
				// match any word that is followed by at least one character
				// that is not the end of the string
				// insert a comma after the first word
				title = p5.substitute("s/^(\\w+)(\\b[^$].{1,})$/$1,$2/",title);
				// insert a period after any single character
				// surrounded by word breaks
				title = p5.substitute("s/\\b(\\w)\\b/$1\\./g",title);

				title = p5.substitute("s#C\\.\\/O\\.#c\\/o#",title);
			}
			else if(navName.equalsIgnoreCase(EiNavigator.PID) || navName.equalsIgnoreCase(EiNavigator.PEC))
      {
   				title = code.toUpperCase();
      }
			else if(navName.equalsIgnoreCase(EiNavigator.PN)
					|| navName.equalsIgnoreCase(EiNavigator.ST)
					|| navName.equalsIgnoreCase(EiNavigator.FL)
					|| navName.equalsIgnoreCase(EiNavigator.PN)
					|| navName.equalsIgnoreCase(EiNavigator.AF)
					|| navName.equalsIgnoreCase(EiNavigator.CV)
					|| navName.equalsIgnoreCase(EiNavigator.CO)
					|| navName.equalsIgnoreCase(EiNavigator.LA)
					|| navName.equalsIgnoreCase(EiNavigator.PK)
					|| navName.equalsIgnoreCase(EiNavigator.PCI)
					|| navName.equalsIgnoreCase(EiNavigator.PUC))
			{
                // translate first char of every word
                // to Uppercase. Only available in Perl/ORO
                // (\\u not supported in java 1.4 regexp pkg)
                title = p5.substitute("s/(\\w+)/\\u$1/g",code);

      }
			else
			{
				title = code;
			}
		}

		return title;
	}

	public String getValue(String value, String navName)
	{
    	String title = value;

        Perl5Util p5 = new Perl5Util();

        if(navName.equalsIgnoreCase(EiNavigator.CL))
        {
            // look a string, ed*, surrounded by () or []
			title = p5.substitute("s/DQD/\\./gi",value);
        }
        else if(navName.equalsIgnoreCase(EiNavigator.CV))
        {
            title = p5.substitute("s/(\\w)(\\()(\\w)/$1 $2$3/g",title);
        }
		return title;
	}

}
