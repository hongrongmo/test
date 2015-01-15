package org.ei.data.c84.runtime;

import org.ei.domain.EIDoc;
import org.ei.domain.ElementData;
import org.ei.domain.ISSN;
import org.ei.domain.Issue;
import org.ei.domain.Keys;
import org.ei.fulldoc.LinkingStrategy;
import org.ei.domain.PageRange;
import org.ei.domain.Volume;
import org.ei.fulldoc.AlgoLinker;
import org.ei.fulldoc.LinkInfo;


public class C84LinkingStrategy
	implements LinkingStrategy
{

    public LinkInfo buildLink(EIDoc eid)
    	throws Exception
    {
    	LinkInfo linkInfo = new LinkInfo();
    	linkInfo.url = "http://dx.doi.org/"+getDOI(eid);
        return linkInfo;
    }

    public String hasLink(EIDoc eid)
	{
		if(getDOI(eid) != null)
		{
			return HAS_LINK_YES;
		}
		else
		{
			return HAS_LINK_NO;
		}
	}

	private String getDOI(EIDoc eidoc)
	{
		String value = null;
		if(eidoc.getElementDataMap().containsKey(Keys.DOI))
		{
			ElementData doi = (ElementData)eidoc.getElementDataMap().get(Keys.DOI);
			String[] edata = doi.getElementData();
			value = edata[0];
		}
		return value;
	}
}
