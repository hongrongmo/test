
package org.ei.data.upt.runtime;

import org.ei.domain.EIDoc;
import org.ei.fulldoc.*;


public class EUPLinkingStrategy implements LinkingStrategy
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

       // String dbid = eid.getDatabase();

        if (authcd != null
                && "EP".equalsIgnoreCase(authcd))
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

    public String hasLink(EIDoc eid)
    {
        String patNum =  eid.getPatNumber();
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
        String patNum =  eidoc.getPatNumber();
        String authcd =  eidoc.getAuthcdNumber();

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
