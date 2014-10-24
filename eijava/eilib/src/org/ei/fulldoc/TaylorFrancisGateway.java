package org.ei.fulldoc;

import java.util.Hashtable;

public class TaylorFrancisGateway
{
    private Hashtable issnTable = new Hashtable();
    private String linkbase = "http://taylorandfrancis.metapress.com/openurl.asp?genre=article&";
    private static TaylorFrancisGateway instance;
    int vol = -1;

    public static synchronized TaylorFrancisGateway getInstance()
    {
        if(instance == null)
        {
            instance = new TaylorFrancisGateway();
        }

        return instance;
    }

    private TaylorFrancisGateway()
    {
        issnTable.put("0284186X", new TaylorFrancisJournal(37));
        issnTable.put("08827516", new TaylorFrancisJournal(25));
        issnTable.put("00018732", new TaylorFrancisJournal(47));
        issnTable.put("02786826", new TaylorFrancisJournal(30));
        issnTable.put("00033790", new TaylorFrancisJournal(56));
        //vol is high, 59 right now
        //issnTable.put("0003-6811", new TaylorFrancisJournal(80));
        issnTable.put("08839514", new TaylorFrancisJournal(10));
        //vol is too high like 8210 orlow the vol; 0 hits
        //issnTable.put("0890-3069", new TaylorFrancisJournal(13));
        issnTable.put("10556796", new TaylorFrancisJournal(21));
        issnTable.put("07434618", new TaylorFrancisJournal(19));
        issnTable.put("0144929X", new TaylorFrancisJournal(15));
        issnTable.put("10242422", new TaylorFrancisJournal(20));
        issnTable.put("09613218", new TaylorFrancisJournal(25));
        //vol upto 188 right now
        issnTable.put("00986445", new TaylorFrancisJournal(189));
        //vol is 18 right now
        issnTable.put("10286608", new TaylorFrancisJournal(19));
        //vol is 21 right now
        issnTable.put("07349343", new TaylorFrancisJournal(22));
        issnTable.put("08920753", new TaylorFrancisJournal(27));
        //vol is 164 right now
        //issnTable.put("0010-2202", new TaylorFrancisJournal(174));
        issnTable.put("09540091", new TaylorFrancisJournal(7));
        issnTable.put("01446193", new TaylorFrancisJournal(14));
        issnTable.put("00107514", new TaylorFrancisJournal(38));
        //vol is 7 right now
        issnTable.put("0889311X", new TaylorFrancisJournal(8));
        issnTable.put("01969722", new TaylorFrancisJournal(27));
        issnTable.put("14689367", new TaylorFrancisJournal(16));
        issnTable.put("02681110", new TaylorFrancisJournal(14));
        issnTable.put("09523987", new TaylorFrancisJournal(37));
        issnTable.put("0731356X", new TaylorFrancisJournal(27));
        issnTable.put("15325008", new TaylorFrancisJournal(29));
        issnTable.put("02726343", new TaylorFrancisJournal(20));
        issnTable.put("10196781", new TaylorFrancisJournal(9));
        issnTable.put("00908312", new TaylorFrancisJournal(21));
        issnTable.put("0305215X", new TaylorFrancisJournal(34));
        issnTable.put("00140139", new TaylorFrancisJournal(40));
        issnTable.put("03043797", new TaylorFrancisJournal(25));
        issnTable.put("08916152", new TaylorFrancisJournal(12));
        issnTable.put("00150193", new TaylorFrancisJournal(266));
        issnTable.put("07315171", new TaylorFrancisJournal(29));
        issnTable.put("01468030", new TaylorFrancisJournal(17));
        //vol is 94 right now
        issnTable.put("03091929", new TaylorFrancisJournal(96));
        issnTable.put("01457632", new TaylorFrancisJournal(20));
        issnTable.put("0740817X", new TaylorFrancisJournal(35));
        issnTable.put("01972243", new TaylorFrancisJournal(12));
        issnTable.put("1369118X", new TaylorFrancisJournal(2));
        issnTable.put("10584587", new TaylorFrancisJournal(42));
        //vol is 6 right now
        issnTable.put("10248072", new TaylorFrancisJournal(7));
        issnTable.put("0951192X", new TaylorFrancisJournal(9));
        //vol is 77 right now
        issnTable.put("00207160", new TaylorFrancisJournal(79));
        issnTable.put("00207179", new TaylorFrancisJournal(66));
        issnTable.put("00207217", new TaylorFrancisJournal(80));
        issnTable.put("03067319", new TaylorFrancisJournal(82));
        issnTable.put("00207233", new TaylorFrancisJournal(59));
        issnTable.put("03081079", new TaylorFrancisJournal(31));
        issnTable.put("13658816", new TaylorFrancisJournal(11));
        //vol is 15 right now
        //issnTable.put("0895-7959", new TaylorFrancisJournal(22));
        issnTable.put("02656736", new TaylorFrancisJournal(15));
        //vol is 29 right now
        issnTable.put("0020739X", new TaylorFrancisJournal(30));
        //issn is different
        //issnTable.put("0091-4037", new TaylorFrancisJournal(51));
        issnTable.put("00207543", new TaylorFrancisJournal(35));
        issnTable.put("09553002", new TaylorFrancisJournal(1));
        issnTable.put("01431161", new TaylorFrancisJournal(18));
        //vol is 20 right now
        issnTable.put("09500693", new TaylorFrancisJournal(21));
        issnTable.put("10255818", new TaylorFrancisJournal(4));
        issnTable.put("01425919", new TaylorFrancisJournal(22));
        issnTable.put("00207721", new TaylorFrancisJournal(30));
        issnTable.put("07900627", new TaylorFrancisJournal(11));
        issnTable.put("0144235X", new TaylorFrancisJournal(16));
        issnTable.put("00218464", new TaylorFrancisJournal(78));
        issnTable.put("14639246", new TaylorFrancisJournal(21));
        //vol is 42 right now
        //issnTable.put("0095-8972", new TaylorFrancisJournal(55));
        issnTable.put("07370652", new TaylorFrancisJournal(21));
        issnTable.put("09544828", new TaylorFrancisJournal(9));
        issnTable.put("10655131", new TaylorFrancisJournal(9));
        issnTable.put("0952813X", new TaylorFrancisJournal(8));
        issnTable.put("02683962", new TaylorFrancisJournal(11));
        //vol is 6 right now
        //issnTable.put("0022-250X", new TaylorFrancisJournal(26));
        issnTable.put("03091902", new TaylorFrancisJournal(23));
        issnTable.put("09500340", new TaylorFrancisJournal(1));
        //vol is 56 right now
        //issnTable.put("0094-9655", new TaylorFrancisJournal(72));
        issnTable.put("01495739", new TaylorFrancisJournal(22));
        issnTable.put("01690965", new TaylorFrancisJournal(11));
        //vol is 18 right now
        issnTable.put("02786273", new TaylorFrancisJournal(20));
        //vol is 9 right now
        issnTable.put("08981507", new TaylorFrancisJournal(12));
        issnTable.put("02678292", new TaylorFrancisJournal(22));
        issnTable.put("1358314X", new TaylorFrancisJournal(9));
        //vol is 10 right now
        issnTable.put("10556915", new TaylorFrancisJournal(11));
        //vol is 17 right now
        //issnTable.put("0149-0419", new TaylorFrancisJournal(22));
        issnTable.put("1064119X", new TaylorFrancisJournal(17));
        issnTable.put("15376494", new TaylorFrancisJournal(9));
        issnTable.put("10759417", new TaylorFrancisJournal(6));
        issnTable.put("14639238", new TaylorFrancisJournal(24));
        issnTable.put("10893954", new TaylorFrancisJournal(1));
        issnTable.put("08827508", new TaylorFrancisJournal(23));
        //need to remove issue is null
        issnTable.put("1058725X", new TaylorFrancisJournal(372));
        issnTable.put("00268976", new TaylorFrancisJournal(87));
        issnTable.put("08927022", new TaylorFrancisJournal(28));
        //vol is 17 right now
        issnTable.put("10589759", new TaylorFrancisJournal(18));
        issnTable.put("10587268", new TaylorFrancisJournal(28));
        issnTable.put("10407782", new TaylorFrancisJournal(35));
        issnTable.put("10407790", new TaylorFrancisJournal(35));
        //vol is 38 right now
        //issnTable.put("0233-1934", new TaylorFrancisJournal(51));
        issnTable.put("10556788", new TaylorFrancisJournal(17));
        issnTable.put("02726351", new TaylorFrancisJournal(19));
        issnTable.put("01411594", new TaylorFrancisJournal(75));
        issnTable.put("14786435", new TaylorFrancisJournal(83));
        issnTable.put("01418610", new TaylorFrancisJournal(77));
        issnTable.put("13642812", new TaylorFrancisJournal(77));
        issnTable.put("09500839", new TaylorFrancisJournal(73));
        //vol is 122 right now
        //issnTable.put("1042-6507", new TaylorFrancisJournal(177));
        issnTable.put("00319104", new TaylorFrancisJournal(40));
        //vol is 8 right now
        //issnTable.put("1051-9998", new TaylorFrancisJournal(10));
        issnTable.put("09537287", new TaylorFrancisJournal(8));
        issnTable.put("08109028", new TaylorFrancisJournal(18));
        issnTable.put("10420150", new TaylorFrancisJournal(157));
        issnTable.put("02827581", new TaylorFrancisJournal(14));
        //vol is 37 right now
        //issnTable.put("1045-1129", new TaylorFrancisJournal(72));
        //vol is 21 right now
        //issnTable.put("0278-6117", new TaylorFrancisJournal(25));
        //vol is 15 right now
        issnTable.put("01422413", new TaylorFrancisJournal(17));
        //vol is 40 right now
        issnTable.put("02329298", new TaylorFrancisJournal(42));
        //vol is 27 right now
        //issnTable.put("0730-3300", new TaylorFrancisJournal(35));
        issnTable.put("13546783", new TaylorFrancisJournal(2));
        issnTable.put("02772248", new TaylorFrancisJournal(82));
        issnTable.put("03081060", new TaylorFrancisJournal(25));
        issnTable.put("13506285", new TaylorFrancisJournal(3));
        issnTable.put("1065514X", new TaylorFrancisJournal(14));
    }

    public boolean hasLink(String ISSN,
                           String volString,
                           String issue,
                           String page)
    {
        if((ISSN == null || ISSN.length() == 0)
            || (volString == null || volString.length() == 0)
            || (page == null || page.length() == 0)
            || (issue == null || issue.length() == 0))
        {
            return false;
        }

        if(issnTable.containsKey(ISSN))
        {
            TaylorFrancisJournal journal = (TaylorFrancisJournal)issnTable.get(ISSN);

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
                          String issue,
                          String page)
    {
        String link = null;

        if(hasLink(ISSN,
                   volString,
                   issue,
                   page))
        {
            link = linkbase+"issn="+ISSN +"&volume="+vol+"&issue="+issue+"&spage="+page;
        }

        return link;
    }

    class TaylorFrancisJournal
    {
        private int volBegin;

        public TaylorFrancisJournal(int _volBegin)
        {
            this.volBegin = _volBegin;
        }

        public int getVolBegin()
        {
            return this.volBegin;
        }

    }
}