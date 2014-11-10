package org.ei.data.upt.runtime;

import java.net.URLEncoder;

import org.ei.domain.EIDoc;
import org.ei.fulldoc.LinkInfo;
import org.ei.fulldoc.LinkingStrategy;
import org.ei.util.security.SecureID;

public class UPALinkingStrategy implements LinkingStrategy
{

    public LinkInfo buildLink( EIDoc eid) throws Exception
    {
        LinkInfo linkInfo = new LinkInfo();
        String pnum = eid.getPatNumber();
        String pkind = eid.getPatKind();
        String authcd = eid.getAuthcdNumber();
        String doctype = eid.getType();

        if (pnum != null && authcd != null && "US".equalsIgnoreCase(authcd))
        {
            pnum = pnum.trim();
            StringBuffer redirect = new StringBuffer();
            if ((doctype != null) && (doctype.equalsIgnoreCase("UA")))
            {
                // create redirect for patent application
                redirect.append("http://appft1.uspto.gov/netacgi/nph-Parser?Sect1=PTO1&Sect2=HITOFF&d=PG01&p=1&u=/netahtml/PTO/srchnum.html&r=1&f=G&l=50&s1=")
                  .append(pnum)
                  .append(".PGNR.&OS=DN/")
                  .append(pnum)
                  .append("&RS=DN/")
                  .append(pnum);
            }
            else
            {
                // create redirect for issued patent
                redirect.append("http://patft.uspto.gov/netacgi/nph-Parser?patentnumber=")
                  .append(pnum);
            }
            // create link to patent servlet
            StringBuffer buf = new StringBuffer();
            buf.append("/controller/servlet/Patent.pdf?").append("ac=").append(authcd).append("&pn=").append(pnum).append("&kc=").append(pkind).append("&type=PDF").append("&rurl=").append(URLEncoder.encode(redirect.toString(),"UTF-8"));
            buf.append("&secureID=").append(SecureID.getSecureID(LinkingStrategy.PATENT_LINK_EXPIRES));
            linkInfo.url = buf.toString();
        }
        return linkInfo;
    }

    public String hasLink(EIDoc eid)
    {
        String patNum =  eid.getPatNumber();
        String authcd =  eid.getAuthcdNumber();
        if((patNum != null) &&(authcd != null))
        {
            return HAS_LINK_ALWAYS;
        }
        else
        {
            return HAS_LINK_NO;
        }
    }
}
