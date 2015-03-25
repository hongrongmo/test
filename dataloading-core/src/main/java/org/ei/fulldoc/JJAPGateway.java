
package org.ei.fulldoc;

import java.util.Hashtable;

public class JJAPGateway
{
    private Hashtable issnTable = new Hashtable();
    private String linkbase = "http://jjap.ipap.jp/link?JJAP";
    private static JJAPGateway instance;

    public static synchronized JJAPGateway getInstance()
    {
        if(instance == null)
        {
            instance = new JJAPGateway();
        }

        return instance;
    }

    private JJAPGateway()
    {
        issnTable.put("00214922", "yes");
    }

    public boolean hasLink(String ISSN,
                           String volString,
                           String page)
    {
        if((ISSN == null || ISSN.length() == 0)
            || (volString == null || volString.length() == 0)
            || (page == null || page.length() == 0))
        {
            return false;
        }

        int vol = -1;

        try
        {
            vol = Integer.parseInt(volString);
        }
        catch(Exception e)
        {

        }

        if(issnTable.containsKey(ISSN) && vol >= 21)
        {
            return true;
        }

        return false;
    }

    public String getLink(String ISSN,
                          String volString,
                          String page)
    {
        String link = null;

        if(hasLink(ISSN,
                   volString,
                   page))
        {
            link = linkbase+"/"+volString+"/"+page+"/";
        }
        System.out.println("JJAP link:"+ link);
        return link;
    }



}