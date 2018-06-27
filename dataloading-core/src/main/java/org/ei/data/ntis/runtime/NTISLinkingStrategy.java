
package org.ei.data.ntis.runtime;

import org.ei.domain.EIDoc;
import org.ei.fulldoc.LinkInfo;
import org.ei.fulldoc.LinkingStrategy;


public class NTISLinkingStrategy implements LinkingStrategy
{

    public LinkInfo buildLink( EIDoc eid) throws Exception
    {
    	LinkInfo linkInfo = new LinkInfo();
    	String accnum = eid.getAccNumber();
    	StringBuffer buf = new StringBuffer();
    	buf.append("http://www.ntis.gov/Search/Home/titleDetail?abbr=");
    	buf.append(accnum);
    	linkInfo.url = buf.toString();
        return linkInfo;
    }

    public String hasLink(EIDoc eid)
	{
        String acnum = eid.getAccNumber();
        String date = eid.getYear();
        int dateint = 0;
        if(date != null)
        {
			try
			{
            	dateint = Integer.parseInt(date);
			}
			catch(Exception e)
			{
				return HAS_LINK_NO;
			}
        }
        if(dateint >= 1990
           && acnum != null)
        {
            return HAS_LINK_YES;
        }
		else
		{
		    return HAS_LINK_NO;
		}
	}


	public String getFT(EIDoc eidoc)
	{
        String acnum = eidoc.getAccNumber();
        String date = eidoc.getYear();
        int dateint = 0;
        if (date != null)
        {
			try
			{
            	dateint = Integer.parseInt(date);
			}
			catch(Exception e)
			{
				return "N";
			}
        }

        if(dateint >= 1990 &&
           acnum != null)
		{
			return "Y";
		}
		else
		{
			return "N";
		}
	}
}







