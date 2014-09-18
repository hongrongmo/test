
package org.ei.data.compendex.runtime;

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



public class CPXLinkingStrategy implements LinkingStrategy
{



    public LinkInfo buildLink( EIDoc eid) throws Exception
    {
    	AlgoLinker algo = new AlgoLinker();
    	LinkInfo linkInfo;
    	linkInfo = algo.buildLink(getISSN2(eid),
            						   getVolumeNo(eid),
            						   getIssueNo(eid),
            						   getStartPage(eid),
            						   getDOI(eid));

        return linkInfo;
    }

    public String hasLink(EIDoc eid)
	{

		AlgoLinker algo = new AlgoLinker();
		//System.out.println("DOI-LINK= "+getDOI(eid)+" ISSN2= "+getISSN2(eid)+" volume= "+getVolumeNo(eid)+" issueNo= "+getIssueNo(eid)+" startpage= "+getStartPage(eid));
		if(algo.hasLink(getISSN2(eid),
						getVolumeNo(eid),
						getIssueNo(eid),
						getStartPage(eid),
						getDOI(eid)))
		{
			//System.out.println("HAS_LINK_YES= "+HAS_LINK_YES);
			return HAS_LINK_YES;
		}
		else
		{
			//System.out.println("HAS_LINK_NO= "+HAS_LINK_NO);
			return HAS_LINK_NO;
		}
	}


	public String getISSN2(EIDoc eidoc)
	{
		String value = null;
		if(eidoc.getElementDataMap().containsKey(Keys.ISSN))
		{
			ISSN issn = (ISSN)eidoc.getElementDataMap().get(Keys.ISSN);
			value = issn.withoutDash();
		}

		return value;
	}


	private String getVolumeNo(EIDoc eidoc)
	{
		String value = null;
		if(eidoc.getElementDataMap().containsKey(Keys.VOLUME))
		{
			Volume volume = (Volume) eidoc.getElementDataMap().get(Keys.VOLUME);
			value = volume.getVolumeNumber();
		}
		return value;

	}

	private String getIssueNo(EIDoc eidoc)
	{
		String value = null;
		if(eidoc.getElementDataMap().containsKey(Keys.ISSUE))
		{
			Issue issue = (Issue)eidoc.getElementDataMap().get(Keys.ISSUE);
			value = issue.getIssueNumber();
		}

		return value;
	}

	private String  getStartPage(EIDoc eidoc)
	{
		String value = null;
		if(eidoc.getElementDataMap().containsKey(Keys.PAGE_RANGE))
		{
			PageRange pageRange = (PageRange)eidoc.getElementDataMap().get(Keys.PAGE_RANGE);
			value = pageRange.getStartPage();
		}

		return value;
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


    public String getLink(EIDoc eidoc)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
