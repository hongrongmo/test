
package org.ei.fulldoc;

import java.util.Hashtable;

public class SPIEGateway
{
    private Hashtable issnTable = new Hashtable();
    private String linkbase = "http://spiedl.aip.org/getabs/servlet/GetabsServlet?prog=normal&idtype=cvips&gifs=Yes&id=";
    private String linkbase2 = "http://spiedl.aip.org/dbt/dbt.jsp?KEY=PSISDG&Issue=1&Volume=";
    private static SPIEGateway instance;



    public static synchronized SPIEGateway getInstance()
    {
        if(instance == null)
        {
            instance = new SPIEGateway();
        }

        return instance;
    }

    private SPIEGateway()
    {

        issnTable.put("0277786X", new SPIEJournal("PSISDG",1700));

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
            SPIEJournal journal = (SPIEJournal)issnTable.get(ISSN);

            int vol = -1;

            try
            {
                vol = Integer.parseInt(volString);
            }
            catch(Exception e)
            {
                return false;
            }
            if(vol >= journal.getVolBegin())
            {
                return true;
            }
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
            if(page != null && page.length() != 0)
            {
                link = linkbase+getID(volString,page);
            }
            else
            {
                link = linkbase2+volString;
            }
        }
        System.out.println("SPIE link:"+ link);
        return link;
    }

    private String getID(String vol,
                         String page)
    {
        StringBuffer buf = new StringBuffer("PSISDG");
        buf.append(pad(vol));
        buf.append("000001");
        buf.append(pad(page));
        buf.append("000001");
        return buf.toString();
    }

    private String pad(String p)
    {
        while(p.length() < 6)
        {
            p = "0"+p;
        }

        return p;
    }

    class SPIEJournal
    {
        private String linkCode;
        private int volBegin;

        public SPIEJournal(String _linkCode,
                          int _volBegin)
        {
            this.linkCode = _linkCode;
            this.volBegin = _volBegin;
        }


        public int getVolBegin()
        {
            return this.volBegin;
        }

        public String getLinkCode()
        {
            return this.linkCode;
        }
    }
}