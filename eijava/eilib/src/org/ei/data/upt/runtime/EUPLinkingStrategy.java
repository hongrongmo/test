package org.ei.data.upt.runtime;

import org.ei.domain.EIDoc;
import org.ei.fulldoc.LinkInfo;
import org.ei.fulldoc.LinkingStrategy;
import java.net.URLEncoder;

public class EUPLinkingStrategy implements LinkingStrategy
{

    public LinkInfo buildLink( EIDoc eid) throws Exception
    {
        LinkInfo linkInfo = new LinkInfo();
        String pnum = eid.getPatNumber();
        String authcd = eid.getAuthcdNumber();
        String pkind = eid.getPatKind();

        if (pnum != null && authcd != null && "EP".equalsIgnoreCase(authcd))
        {
            StringBuffer redirect = new StringBuffer();
            redirect.append("http://v3.espacenet.com/textdoc?DB=EPODOC&IDX=")
              .append(authcd.trim())
              .append(pad(pnum.trim()));

            StringBuffer buf = new StringBuffer();
            buf.append("/controller/servlet/Patent.pdf?").append("ac=").append(authcd).append("&pn=").append(pnum).append("&kc=").append(pkind).append("&type=PDF").append("&rurl=").append(URLEncoder.encode(redirect.toString(),"UTF-8"));
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

    public String hasLink(EIDoc eid)
    {
        String patNum = eid.getPatNumber();
        String authcd = eid.getAuthcdNumber();

        if((patNum != null)
            &&(authcd != null))
        {
            return HAS_LINK_ALWAYS;
        }
        else
        {
            return HAS_LINK_NO;
        }

    }

    public String getFT(EIDoc eidoc)
    {
        String patNum = eidoc.getPatNumber();
        String authcd = eidoc.getAuthcdNumber();

        if((patNum != null)
                &&(authcd != null))
        {
            return "Y";
        }
        else
        {
            return "N";
        }
    }
}
