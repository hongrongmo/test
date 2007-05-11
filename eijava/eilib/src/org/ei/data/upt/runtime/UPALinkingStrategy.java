
package org.ei.data.upt.runtime;

import org.ei.domain.EIDoc;
import org.ei.fulldoc.LinkInfo;
import org.ei.fulldoc.*;


public class UPALinkingStrategy implements LinkingStrategy
{

    public LinkInfo buildLink( EIDoc eid) throws Exception
    {
        LinkInfo linkInfo = new LinkInfo();
        String pnum = eid.getPatNumber();
        //String ppub = eid.getPatpubNumber();
        String authcd = eid.getAuthcdNumber();
        String pkind = eid.getPatKind();
        StringBuffer buf = new StringBuffer();
        StringBuffer sbRURL = new StringBuffer();
        String doctype = eid.getType();

        //String dbid = eid.getDatabase();
        if (authcd != null &&
            "US".equalsIgnoreCase(authcd))
        {
            if ((doctype != null)
                    && (doctype.equalsIgnoreCase("UA")))
            {
                sbRURL.append("http://appft1.uspto.gov/netacgi/nph-Parser?Sect1=PTO1&Sect2=HITOFF&d=PG01&p=1&u=/netahtml/PTO/srchnum.html&r=1&f=G&l=50&s1=")
                      .append(pnum.trim())
                      .append(".PGNR.&OS=DN/")
                      .append(pnum.trim())
                      .append("&RS=DN/")
                      .append(pnum.trim());

                buf.append(UniventioPDFGateway.getPatentLink(authcd,pnum,pkind,sbRURL.toString()));
                linkInfo.url = buf.toString();
            }
            else
            {
                sbRURL.append("http://patft.uspto.gov/netacgi/nph-Parser?patentnumber=");
                sbRURL.append(pnum.trim());
                buf.append(UniventioPDFGateway.getPatentLink(authcd,pnum,pkind,sbRURL.toString()));
                linkInfo.url = buf.toString();
            }
        }


        return linkInfo;
    }

    public String hasLink(EIDoc eid)
    {
        String patNum =  eid.getPatNumber();
        String authcd =  eid.getAuthcdNumber();
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
}
