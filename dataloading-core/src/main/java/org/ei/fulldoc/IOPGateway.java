
package org.ei.fulldoc;

import java.util.Hashtable;

public class IOPGateway
{
    private Hashtable<String, String> issnTable = new Hashtable<String, String>();
    private String linkbase = "http://stacks.iop.org";
    private static IOPGateway instance;

    public static synchronized IOPGateway getInstance()
    {
        if(instance == null)
        {
            instance = new IOPGateway();
        }

        return instance;
    }

    private IOPGateway()
    {

        issnTable.put("00319155", "yes");
        issnTable.put("09524746","yes");
        issnTable.put("09570233","yes");
        issnTable.put("09673334","yes");
        issnTable.put("13647830","yes");
        issnTable.put("02665611","yes");
        issnTable.put("09601317","yes");
        issnTable.put("09534075","yes");
        issnTable.put("09538984","yes");
        issnTable.put("00223727","yes");
        issnTable.put("09650393","yes");
        issnTable.put("09574484","yes");
        issnTable.put("07413335","yes");
        issnTable.put("09630252","yes");
        issnTable.put("14644266","yes");
        issnTable.put("02681242","yes");
        issnTable.put("09641726","yes");
        issnTable.put("09532048","yes");
        issnTable.put("09597174","yes");
        issnTable.put("00261394","yes");
        issnTable.put("00344885","yes");
        issnTable.put("14685248","yes");
        issnTable.put("02649381","yes");
        issnTable.put("03054470","yes");
        issnTable.put("09517715","yes");
        issnTable.put("09543899","yes");
        issnTable.put("0954898X","yes");
        issnTable.put("10298479","yes");
        issnTable.put("14697688","yes");
        issnTable.put("14757516","yes");



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

        if(issnTable.containsKey(ISSN))
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
            link = linkbase+"/"+addDash(ISSN)+"/"+volString+"/"+page;
        }
        System.out.println("IOP link:"+ link);
        return link;
    }

    private String addDash(String i)
    {
        String part1 = i.substring(0,4);
        String part2 = i.substring(4,i.length());
        return part1+"-"+part2;
    }

}