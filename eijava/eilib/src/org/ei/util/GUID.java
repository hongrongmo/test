package org.ei.util;

import java.net.InetAddress;
import java.rmi.server.UID;

/**
*   This class produces a Globally Unique Identifier.
**/


public class GUID
{
    private String guid;
    private StringUtil util = new StringUtil();

    public GUID() throws Exception
    {
        UID uid = new UID();


        String vmid = System.getProperty("vmid");

        if(vmid == null)
        {
            InetAddress i = InetAddress.getLocalHost();
            guid = uid.toString()+i.getHostAddress();
        }
        else
        {
            guid = uid.toString()+vmid;
        }

        guid = util.replace(guid,
                    ".",
                    "",
                        StringUtil.REPLACE_GLOBAL,
                    StringUtil.MATCH_CASE_INSENSITIVE);

        guid = util.replace(guid,
                    ":",
                    "",
                    StringUtil.REPLACE_GLOBAL,
                    StringUtil.MATCH_CASE_INSENSITIVE);

        guid = util.replace(guid,
                    "-",
                    "M",
                    StringUtil.REPLACE_GLOBAL,
                    StringUtil.MATCH_CASE_INSENSITIVE);

    }



    public String toString()
    {
        return guid;
    }

}
