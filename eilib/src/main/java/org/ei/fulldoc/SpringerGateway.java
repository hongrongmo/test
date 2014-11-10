package org.ei.fulldoc;

import java.util.Hashtable;

public class SpringerGateway
{
    private Hashtable issnTable = new Hashtable();
    private String linkbase = "http://www.springerlink.com/openurl.asp?genre=article&";
    private static SpringerGateway instance;

    int vol = -1;

    public static synchronized SpringerGateway getInstance()
    {
        if(instance == null)
        {
            instance = new SpringerGateway();
        }

        return instance;
    }

    private SpringerGateway()
    {
        issnTable.put("13528661", new SpringerJournal(16));
        issnTable.put("00015903", new SpringerJournal(33));
        issnTable.put("00015970", new SpringerJournal(160));
        issnTable.put("08949166", new SpringerJournal(16));
        issnTable.put("07246145", new SpringerJournal(63));
        issnTable.put("00653195", new SpringerJournal(138));
        issnTable.put("09515666", new SpringerJournal(16));
        issnTable.put("01784617", new SpringerJournal(15));
        issnTable.put("16182642", new SpringerJournal(372));
        issnTable.put("14240637", new SpringerJournal(1));
        issnTable.put("09381279", new SpringerJournal(8));
        issnTable.put("00954616", new SpringerJournal(33));
        issnTable.put("09478396", new SpringerJournal(62));
        issnTable.put("09462171", new SpringerJournal(63));
        issnTable.put("09335846", new SpringerJournal(35));
        issnTable.put("00039527", new SpringerJournal(134));
        issnTable.put("09391533", new SpringerJournal(67));
        issnTable.put("00904341", new SpringerJournal(30));
        issnTable.put("09354956", new SpringerJournal(7));
        issnTable.put("03401200", new SpringerJournal(75));
        issnTable.put("14359529", new SpringerJournal(57));
        issnTable.put("00074861", new SpringerJournal(56));
        issnTable.put("00080624", new SpringerJournal(35));
        issnTable.put("0278081X", new SpringerJournal(21));
        issnTable.put("09307575", new SpringerJournal(12));
        issnTable.put("14355558", new SpringerJournal(1));
        issnTable.put("0303402X", new SpringerJournal(275));
        issnTable.put("02099683", new SpringerJournal(18));
        issnTable.put("00103616", new SpringerJournal(182));
        issnTable.put("10163328", new SpringerJournal(7));
        issnTable.put("01787675", new SpringerJournal(19));
        issnTable.put("0010485X", new SpringerJournal(62));
        issnTable.put("14329360", new SpringerJournal(1));
        issnTable.put("09351175", new SpringerJournal(8));
        issnTable.put("01795376", new SpringerJournal(15));
        issnTable.put("01782770", new SpringerJournal(10));
        issnTable.put("09487921", new SpringerJournal(79));
        issnTable.put("01770667", new SpringerJournal(15));
        issnTable.put("09430105", new SpringerJournal(28));
        issnTable.put("0364152X", new SpringerJournal(20));
        issnTable.put("01757571", new SpringerJournal(25));
        issnTable.put("14346001", new SpringerJournal(1));
        issnTable.put("14346028", new SpringerJournal(1));
        issnTable.put("14346044", new SpringerJournal(1));
        issnTable.put("14346060", new SpringerJournal(1));
        issnTable.put("12928941", new SpringerJournal(1));
        issnTable.put("07234864", new SpringerJournal(22));
        issnTable.put("01777963", new SpringerJournal(18));
        issnTable.put("09345043", new SpringerJournal(10));
        issnTable.put("00157899", new SpringerJournal(63));
        issnTable.put("16153375", new SpringerJournal(1));
        issnTable.put("09370633", new SpringerJournal(354));
        issnTable.put("02760460", new SpringerJournal(17));
        issnTable.put("00167835", new SpringerJournal(85));
        issnTable.put("09110119", new SpringerJournal(14));
        issnTable.put("09477411", new SpringerJournal(31));
        issnTable.put("00183768", new SpringerJournal(53));
        issnTable.put("01783564", new SpringerJournal(10));
        issnTable.put("01706012", new SpringerJournal(19));
        issnTable.put("02683768", new SpringerJournal(15));
        issnTable.put("00207276", new SpringerJournal(26));
        issnTable.put("16155262", new SpringerJournal(1));
        issnTable.put("14325012", new SpringerJournal(1));
        issnTable.put("14332833", new SpringerJournal(1));
        issnTable.put("14332779", new SpringerJournal(1));
        issnTable.put("03427188", new SpringerJournal(16));
        issnTable.put("09332790", new SpringerJournal(9));
        issnTable.put("08971889", new SpringerJournal(15));
        issnTable.put("13416979", new SpringerJournal(8));
        issnTable.put("09497714", new SpringerJournal(71));
        issnTable.put("03036812", new SpringerJournal(34));
        issnTable.put("00222844", new SpringerJournal(42));
        issnTable.put("09388974", new SpringerJournal(6));
        issnTable.put("07217595", new SpringerJournal(16));
        issnTable.put("14328488", new SpringerJournal(1));
        issnTable.put("02191377", new SpringerJournal(2));
        issnTable.put("02688921", new SpringerJournal(13));
        issnTable.put("03029743", new SpringerJournal(149));
        issnTable.put("01708643", new SpringerJournal(269));
        issnTable.put("00758434", new SpringerJournal(1000));
        issnTable.put("09328092", new SpringerJournal(9));
        issnTable.put("14328917", new SpringerJournal(1));
        issnTable.put("14322994", new SpringerJournal(46));
        issnTable.put("00255610", new SpringerJournal(84));
        issnTable.put("09324194", new SpringerJournal(12));
        issnTable.put("01777971", new SpringerJournal(70));
        issnTable.put("09467076", new SpringerJournal(2));
        issnTable.put("00263672", new SpringerJournal(131));
        issnTable.put("00264598", new SpringerJournal(32));
        issnTable.put("09300708", new SpringerJournal(68));
        issnTable.put("00269247", new SpringerJournal(129));
        issnTable.put("09424962", new SpringerJournal(4));
        issnTable.put("00281042", new SpringerJournal(81));
        issnTable.put("09410643", new SpringerJournal(8));
        issnTable.put("0029599X", new SpringerJournal(67));
        issnTable.put("01716468", new SpringerJournal(20));
        issnTable.put("14337541", new SpringerJournal(2));
        issnTable.put("16174909", new SpringerJournal(5));
        issnTable.put("09492054", new SpringerJournal(4));
        issnTable.put("03421791", new SpringerJournal(23));
        issnTable.put("14226944", new SpringerJournal(1));
        issnTable.put("07217714", new SpringerJournal(16));
        issnTable.put("00320935", new SpringerJournal(200));
        issnTable.put("01788051", new SpringerJournal(106));
        issnTable.put("01700839", new SpringerJournal(39));
        issnTable.put("0340255X", new SpringerJournal(112));
        issnTable.put("00334553", new SpringerJournal(149));
        issnTable.put("0301634X", new SpringerJournal(35));
        issnTable.put("09473602", new SpringerJournal(3));
        issnTable.put("09349839", new SpringerJournal(10));
        issnTable.put("09349847", new SpringerJournal(8));
        issnTable.put("00354511", new SpringerJournal(36));
        issnTable.put("07232632", new SpringerJournal(31));
        issnTable.put("09381287", new SpringerJournal(6));
        issnTable.put("14327643", new SpringerJournal(1));
        issnTable.put("09458115", new SpringerJournal(1));
        issnTable.put("1615147X", new SpringerJournal(16));
        issnTable.put("0177798X", new SpringerJournal(59));
        issnTable.put("00405752", new SpringerJournal(93));
        issnTable.put("09354964", new SpringerJournal(9));
        issnTable.put("1432881X", new SpringerJournal(95));
        issnTable.put("14324350", new SpringerJournal(29));
        issnTable.put("09311890", new SpringerJournal(10));
        issnTable.put("16155289", new SpringerJournal(1));
        issnTable.put("13594338", new SpringerJournal(6));
        issnTable.put("01782789", new SpringerJournal(12));
        issnTable.put("10668888", new SpringerJournal(5));
        issnTable.put("00437719", new SpringerJournal(31));
        issnTable.put("00442275", new SpringerJournal(48));
        issnTable.put("09397922", new SpringerJournal(354));
        issnTable.put("07223277", new SpringerJournal(99));
        issnTable.put("01709739", new SpringerJournal(69));
        issnTable.put("01787683", new SpringerJournal(37));
    }

    public boolean hasLink( String ISSN,
                            String volString,
                            String issue,
                            String page )
    {
        if((ISSN == null || ISSN.length() == 0 )
            || (volString == null || volString.length() == 0 )
            || (issue == null || issue.length() == 0 )
            || (page == null || page.length() == 0 ))
        {
            return false;
        }

        if(issnTable.containsKey(ISSN))
        {
            SpringerJournal journal = (SpringerJournal)issnTable.get(ISSN);

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



    public String getLink( String ISSN,
                           String volString,
                           String issue,
                           String page )
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

    class SpringerJournal
    {
        private int volBegin;

        public SpringerJournal(int _volBegin)
        {
            this.volBegin = _volBegin;
        }

        public int getVolBegin()
        {
            return this.volBegin;
        }

    }
}