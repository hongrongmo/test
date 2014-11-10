package org.ei.fulldoc;

import java.util.Hashtable;

public class APSGateway {
    private Hashtable<String, APSJournal> issnTable = new Hashtable<String, APSJournal>();
    private String linkbase = "http://link.aps.org/abstract/";
    private static APSGateway instance;

    public static synchronized APSGateway getInstance() {
        if (instance == null) {
            instance = new APSGateway();
        }

        return instance;
    }

    private APSGateway() {
        // Physics Review A
        issnTable.put("10502947", new APSJournal("PRA", 1, 61));

        // Physics Review B
        issnTable.put("01631829", new APSJournal("PRB", 1, 63));

        // Physics Review C
        issnTable.put("05562813", new APSJournal("PRC", 1, 60));

        // Physics Review D
        issnTable.put("05562821", new APSJournal("PRD", 1, 58));

        // Physics Review E
        issnTable.put("1063651X", new APSJournal("PRE", 47, 63));
        issnTable.put("15393755", new APSJournal("PRE", 47, 63));

        // Physics Review Letters
        issnTable.put("00319007", new APSJournal("PRL", 1, 87));

    }

    public boolean hasLink(String ISSN, String volString, String page) {
        if ((ISSN == null || ISSN.length() == 0) || (volString == null || volString.length() == 0) || (page == null || page.length() == 0)) {
            return false;
        }

        if (issnTable.containsKey(ISSN)) {
            APSJournal journal = (APSJournal) issnTable.get(ISSN);

            int vol = -1;

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

    public String getLink(String ISSN, String volString, String page) {
        String link = null;
        APSJournal journal = (APSJournal) issnTable.get(ISSN);

        if (hasLink(ISSN, volString, page)) {
            int vol = Integer.parseInt(volString);
            if (vol >= journal.getVolNewPage()) {
                while (page.length() < 6) {
                    page = "0" + page;
                }

            }

            link = linkbase + journal.getLinkCode() + "/v" + volString + "/p" + page;
        }

        return link;
    }

    class APSJournal {
        private String linkCode;
        private int volBegin;
        private int volNewPage;

        public APSJournal(String _linkCode, int _volBegin, int _volNewPage) {
            this.linkCode = _linkCode;
            this.volBegin = _volBegin;
            this.volNewPage = _volNewPage;
        }

        public int getVolNewPage() {
            return this.volNewPage;
        }

        public int getVolBegin() {
            return this.volBegin;
        }

        public String getLinkCode() {
            return this.linkCode;
        }
    }
}