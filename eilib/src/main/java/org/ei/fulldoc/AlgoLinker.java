package org.ei.fulldoc;



public class AlgoLinker
{

    APSGateway aps          = APSGateway.getInstance();
    AIPGateway aip          = AIPGateway.getInstance();
    IOPGateway iop          = IOPGateway.getInstance();
    SpringerGateway spr     = SpringerGateway.getInstance();
    BlackwellGateway blk    = BlackwellGateway.getInstance();
    TaylorFrancisGateway tfr = TaylorFrancisGateway.getInstance();
    //SDGateway sdGateway     = SDGateway.getInstance();
    //SPIEGateway spie     = SPIEGateway.getInstance();
    JJAPGateway jjap     = JJAPGateway.getInstance();

    public boolean hasLink(String issn,
                           String volume,
                           String issue,
                           String page,
                           String doi)
    {

        if(doi != null && doi.length() > 0)
        {
            return true;
        }
        else
        {


            if(iop.hasLink(issn,
                           volume,
                           page))
            {
                return true;
            }
            else if(jjap.hasLink(issn,
                         volume,
                         page))
            {
                return true;
            }
            else if(aps.hasLink(issn,
                           volume,
                           page))
            {
                return true;
            }
            else if(aip.hasLink(issn,
                           volume,
                           page))
            {
                return true;
            }
            else if(spr.hasLink(issn,
                                volume,
                                issue,
                                page))
            {
                return true;
            }
            else if(blk.hasLink(issn,
                                volume,
                                issue,
                                page))
            {
                return true;
            }
            else if(tfr.hasLink(issn,
                                volume,
                                issue,
                                page))
            {
                return true;
            }

        }

        return false;
    }


    public LinkInfo buildLink(String issn,
                              String volume,
                              String issue,
                              String page,
                              String doi)
        throws Exception
    {

        LinkInfo linkInfo = new LinkInfo();

		linkInfo.url = tfr.getLink(issn,
								   volume,
								   issue,
								   page);


		if(linkInfo.url != null)
		{
			linkInfo.pubid = "TAY";
			return linkInfo;
		}


		if(doi != null && doi.length() >0)
		{
			linkInfo.url = "http://dx.doi.org/" + doi;
			return linkInfo;
		}


		linkInfo.url  = aps.getLink(issn,
									volume,
									page);

		if(linkInfo.url != null)
		{
			linkInfo.pubid = "APS";
			return linkInfo;
		}

		linkInfo.url  = aip.getLink(issn,
									volume,
									page);

		if(linkInfo.url != null)
		{
			linkInfo.pubid = "AIP";
			return linkInfo;
		}

		linkInfo.url  = jjap.getLink(issn,
								volume,
								page);

		if(linkInfo.url != null)
		{
			linkInfo.pubid = "JJAP";
			return linkInfo;
		}


		linkInfo.url = spr.getLink(issn,
								   volume,
								   issue,
								   page);

		if(linkInfo.url != null)
		{
			linkInfo.pubid = "SPR";
			return linkInfo;
		}

		linkInfo.url  = blk.getLink(issn,
									volume,
									issue,
									page);

		if(linkInfo.url != null)
		{
			linkInfo.pubid = "BLA";
			return linkInfo;
		}


		linkInfo.url = iop.getLink(issn,
								   volume,
								   page);


		if(linkInfo.url != null)
		{
			linkInfo.pubid = "IOP";
			return linkInfo;
		}


        return null;
    }
}