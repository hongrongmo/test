
package org.ei.data.upt.runtime;

import org.ei.domain.EIDoc;
import org.ei.fulldoc.LinkInfo;
import org.ei.fulldoc.LinkingStrategy;
import org.ei.fulldoc.UniventioPDFGateway;


public class UPTLinkingStrategy implements LinkingStrategy
{

    public LinkInfo buildLink(EIDoc eid)
        throws Exception
    {
        LinkInfo linkInfo = new LinkInfo();
        String pnum = eid.getCitationPatNum();
        String ppub = eid.getPatpubNumber();
        String authcd = eid.getAuthcdNumber();
        String pkind = eid.getPatKind();
        StringBuffer buf = new StringBuffer();
        StringBuffer sbRURL = new StringBuffer();


        String dbid = eid.getDatabase();
        if ((dbid != null) &&
            (dbid.equalsIgnoreCase("ref")))
        {
           sbRURL.append("http://v3.espacenet.com/textdoc?DB=EPODOC&IDX=");
           sbRURL.append(padppub(ppub.trim()));
           buf.append(UniventioPDFGateway.getPatentLink(authcd,pnum,pkind,sbRURL.toString()));
           linkInfo.url = buf.toString();
        }
        else if ("US".equalsIgnoreCase(authcd))
        {
            sbRURL.append("http://patft.uspto.gov/netacgi/nph-Parser?patentnumber=");
            sbRURL.append(pnum.trim());
            buf.append(UniventioPDFGateway.getPatentLink(authcd,pnum,pkind,sbRURL.toString()));
            linkInfo.url = buf.toString();
        }
        else if ("EP".equalsIgnoreCase(authcd))
        {

            sbRURL.append("http://v3.espacenet.com/textdoc?DB=EPODOC&IDX=");
            sbRURL.append(authcd.trim());
            sbRURL.append(pad(pnum.trim()));
            buf.append(UniventioPDFGateway.getPatentLink(authcd,pnum,pkind,sbRURL.toString()));
            linkInfo.url = buf.toString();
        }

        return linkInfo;
    }

    public String pad(String num)
    {
        while(num.length() < 7)
        {
            num = "0"+num;
        }

        return num;
    }

    public String padppub(String ppub)
    {
        String authcode = ppub.substring(0,2);
        String num = pad(ppub.substring(2));
        return authcode+num;
    }

    public String hasLink(EIDoc eid)
    {
        String patNum = eid.getPatNumber();
        String authcd = eid.getAuthcdNumber();

        if((patNum != null) &&
           (authcd != null))
        {
            return HAS_LINK_ALWAYS;
        }
        else
        {
            return HAS_LINK_NO;
        }
    }
}
