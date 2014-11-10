package org.ei.fulldoc;

import java.util.Hashtable;

public class BlackwellGateway {
    private Hashtable<String, BlackwellJournal> issnTable = new Hashtable<String, BlackwellJournal>();
    private String linkbase = "http://www.blackwell-synergy.com/openurl?genre=article&";
    private static BlackwellGateway instance;
    int vol = -1;

    public static synchronized BlackwellGateway getInstance() {
        if (instance == null) {
            instance = new BlackwellGateway();
        }

        return instance;
    }

    private BlackwellGateway() {
        issnTable.put("0365-5954", new BlackwellJournal(41));
        issnTable.put("0272-4332", new BlackwellJournal(20));
        issnTable.put("0007-1013", new BlackwellJournal(28));
        issnTable.put("0167-7055", new BlackwellJournal(10));
        issnTable.put("0284-1851", new BlackwellJournal(41));
        issnTable.put("0960-7412", new BlackwellJournal(1));
        issnTable.put("0031-9317", new BlackwellJournal(78));
        issnTable.put("0016-8025", new BlackwellJournal(46));
        issnTable.put("1093-9687", new BlackwellJournal(12));
        // issnTable.put("0165-0203", new BlackwellJournal(26));
        issnTable.put("0824-7935", new BlackwellJournal(13));
        issnTable.put("8756-758X", new BlackwellJournal(21));
        issnTable.put("0371-0459", new BlackwellJournal(30));
        issnTable.put("0266-4720", new BlackwellJournal(14));
        issnTable.put("0016-8025", new BlackwellJournal(46));
        issnTable.put("1350-1917", new BlackwellJournal(8));
        issnTable.put("0969-6016", new BlackwellJournal(8));
        issnTable.put("0266-4909", new BlackwellJournal(14));
        issnTable.put("0022-2720", new BlackwellJournal(189));
        issnTable.put("0268-1064", new BlackwellJournal(12));
        issnTable.put("0268-1072", new BlackwellJournal(12));
        issnTable.put("0275-5408", new BlackwellJournal(15));
        issnTable.put("0033-6807", new BlackwellJournal(27));
        issnTable.put("0035-8711", new BlackwellJournal(293));
        issnTable.put("0956-540X", new BlackwellJournal(132));
        issnTable.put("0959-2954", new BlackwellJournal(8));
        // issnTable.put("0284-1851", new BlackwellJournal(68));
        issnTable.put("0002-9092", new BlackwellJournal(82));
        issnTable.put("0003-813X", new BlackwellJournal(43));
        issnTable.put("0812-0099", new BlackwellJournal(46));
        issnTable.put("0950-091X", new BlackwellJournal(10));
        issnTable.put("0969-9988", new BlackwellJournal(5));
        issnTable.put("0014-2956", new BlackwellJournal(258));
        issnTable.put("0263-5046", new BlackwellJournal(16));
        issnTable.put("0266-6979", new BlackwellJournal(14));
        issnTable.put("0142-5463", new BlackwellJournal(19));
        issnTable.put("0950-5423", new BlackwellJournal(33));
        issnTable.put("0021-8901", new BlackwellJournal(35));
        issnTable.put("0022-2380", new BlackwellJournal(34));
        issnTable.put("0263-4929", new BlackwellJournal(17));
        issnTable.put("0035-9254", new BlackwellJournal(46));
        issnTable.put("0143-9782", new BlackwellJournal(18));
        issnTable.put("0033-3298", new BlackwellJournal(75));
        issnTable.put("0033-4545", new BlackwellJournal(77));
        issnTable.put("0037-0746", new BlackwellJournal(45));
        issnTable.put("0039-2103", new BlackwellJournal(38));
        issnTable.put("0022-2526", new BlackwellJournal(98));
        issnTable.put("0280-6495", new BlackwellJournal(42));
        issnTable.put("0280-6509", new BlackwellJournal(42));
        issnTable.put("0954-4879", new BlackwellJournal(10));
        // issnTable.put("0734-242X", new BlackwellJournal(17));

    }

    private String addDash(String i) {
        String part1 = i.substring(0, 4);
        String part2 = i.substring(4, i.length());
        return part1 + "-" + part2;
    }

    public boolean hasLink(String ISSN, String volString, String issue, String page) {
        if ((ISSN == null || ISSN.length() == 0) || (volString == null || volString.length() == 0) || (page == null || page.length() == 0)
            || (issue == null || issue.length() == 0)) {
            return false;
        }

        ISSN = addDash(ISSN);

        if (issnTable.containsKey(ISSN)) {
            BlackwellJournal journal = (BlackwellJournal) issnTable.get(ISSN);

            try {
                vol = Integer.parseInt(volString);
            } catch (Exception e) {
                return false;
            }

            if (vol >= journal.getVolBegin()) {
                return true;
            }
        }

        return false;
    }

    public String getLink(String ISSN, String volString, String issue, String page) {
        String link = null;

        if (hasLink(ISSN, volString, issue, page)) {
            link = linkbase + "issn=" + addDash(ISSN) + "&volume=" + vol + "&spage=" + page + "&issue=" + issue;
        }

        return link;
    }

    class BlackwellJournal {
        private int volBegin;

        public BlackwellJournal(int _volBegin) {
            this.volBegin = _volBegin;
        }

        public int getVolBegin() {
            return this.volBegin;
        }

    }
}