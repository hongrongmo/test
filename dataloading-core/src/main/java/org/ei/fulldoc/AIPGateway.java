package org.ei.fulldoc;

import java.util.Hashtable;

public class AIPGateway {
    private Hashtable<String, AIPJournal> issnTable = new Hashtable<String, AIPJournal>();
    private String linkbase = "http://link.aip.org/link/?";
    private static AIPGateway instance;

    public static synchronized AIPGateway getInstance() {
        if (instance == null) {
            instance = new AIPGateway();
        }

        return instance;
    }

    private AIPGateway() {
        // AIP(American Institute of Physics) Publisher
        issnTable.put("0094243X", new AIPJournal("APC", 24));
        issnTable.put("00036951", new AIPJournal("APL", 12));
        issnTable.put("10541500", new AIPJournal("CHA", 1));
        issnTable.put("08941866", new AIPJournal("CIP", 11));
        issnTable.put("15219615", new AIPJournal("CSX", 1));
        issnTable.put("00218979", new AIPJournal("JAP", 39));
        issnTable.put("00219606", new AIPJournal("JCP", 48));
        issnTable.put("00222488", new AIPJournal("JMP", 9));
        issnTable.put("00472689", new AIPJournal("JPR", 29));
        issnTable.put("1063777X", new AIPJournal("LTP", 23));
        issnTable.put("10706631", new AIPJournal("PHF", 6));
        issnTable.put("1070664X", new AIPJournal("PHP", 1));
        issnTable.put("00346748", new AIPJournal("RSI", 39));

        // MAIK Publisher
        issnTable.put("10637710", new AIPJournal("ACP", 46));
        issnTable.put("10637737", new AIPJournal("ASL", 26));
        issnTable.put("10637729", new AIPJournal("ASR", 44));
        issnTable.put("10637745", new AIPJournal("CRY", 45));
        issnTable.put("10637761", new AIPJournal("JET", 84));
        issnTable.put("00213640", new AIPJournal("JTP", 63));
        issnTable.put("0030400X", new AIPJournal("OPS", 88));
        issnTable.put("10637788", new AIPJournal("ATN", 63));
        issnTable.put("10637796", new AIPJournal("PAN", 28));
        issnTable.put("10637834", new AIPJournal("PSS", 39));
        issnTable.put("1063780X", new AIPJournal("PPR", 26));
        issnTable.put("10637826", new AIPJournal("SEM", 31));
        issnTable.put("10637842", new AIPJournal("TPH", 42));
        issnTable.put("10637850", new AIPJournal("TPL", 23));

        // ASCE(American Society of Civil Engineers) Publisher
        issnTable.put("15323641", new AIPJournal("QGM", 1));
        issnTable.put("08931321", new AIPJournal("QAS", 8));
        issnTable.put("10840702", new AIPJournal("QBE", 1));
        issnTable.put("0887381X", new AIPJournal("QCR", 9));
        issnTable.put("10900268", new AIPJournal("QCC", 1));
        issnTable.put("08873801", new AIPJournal("QCP", 9));
        issnTable.put("07339364", new AIPJournal("QCO", 121));
        issnTable.put("07339402", new AIPJournal("QEY", 121));
        issnTable.put("07339372", new AIPJournal("QEM", 121));
        issnTable.put("07339364", new AIPJournal("QEE", 121));
        issnTable.put("10900241", new AIPJournal("QGT", 123));
        issnTable.put("07339429", new AIPJournal("QHY", 121));
        issnTable.put("10840699", new AIPJournal("QHE", 1));
        issnTable.put("10760342", new AIPJournal("QIS", 1));
        issnTable.put("07339437", new AIPJournal("QIR", 121));
        issnTable.put("0742597X", new AIPJournal("QME", 11));
        issnTable.put("08991561", new AIPJournal("QMT", 7));
        issnTable.put("08873828", new AIPJournal("QCF", 9));
        issnTable.put("10523928", new AIPJournal("QPI", 121));
        issnTable.put("07339445", new AIPJournal("QST", 121));
        issnTable.put("07339453", new AIPJournal("QSU", 121));
        issnTable.put("0733947X", new AIPJournal("QTE", 121));
        issnTable.put("07339488", new AIPJournal("QUP", 121));
        issnTable.put("07339496", new AIPJournal("QWR", 121));
        issnTable.put("0733950X", new AIPJournal("QWW", 121));
        issnTable.put("15326748", new AIPJournal("LME", 1));
        issnTable.put("15276988", new AIPJournal("QNH", 1));
        issnTable.put("1090025X", new AIPJournal("QHZ", 1));
        issnTable.put("10840680", new AIPJournal("QSC", 1));

        // ASME(American Society of Mechanical Engineers) Publisher
        issnTable.put("00036900", new AIPJournal("AMR", 54));
        issnTable.put("00218936", new AIPJournal("AMJ", 67));
        issnTable.put("01480731", new AIPJournal("JBY", 122));
        issnTable.put("15309827", new AIPJournal("CIS", 1));
        issnTable.put("00220434", new AIPJournal("JDS", 122));
        issnTable.put("10437398", new AIPJournal("JEP", 122));
        issnTable.put("01950738", new AIPJournal("JRG", 122));
        issnTable.put("07424795", new AIPJournal("GTP", 122));
        issnTable.put("00944289", new AIPJournal("JYT", 122));
        issnTable.put("00982202", new AIPJournal("JFG", 122));
        issnTable.put("00221481", new AIPJournal("JHR", 122));
        issnTable.put("10871357", new AIPJournal("MAE", 122));
        issnTable.put("10500472", new AIPJournal("JMD", 122));
        issnTable.put("08927219", new AIPJournal("JOM", 122));
        issnTable.put("00949930", new AIPJournal("JPV", 122));
        issnTable.put("01996231", new AIPJournal("SLE", 122));
        issnTable.put("07424787", new AIPJournal("JTQ", 122));
        issnTable.put("0889504X", new AIPJournal("JTM", 122));
        issnTable.put("07393717", new AIPJournal("VAJ", 122));

    }

    public boolean hasLink(String ISSN, String volString, String page) {
        if ((ISSN == null || ISSN.length() == 0) || (volString == null || volString.length() == 0) || (page == null || page.length() == 0)) {
            return false;
        }

        if (issnTable.containsKey(ISSN)) {
            AIPJournal journal = (AIPJournal) issnTable.get(ISSN);

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
        AIPJournal journal = (AIPJournal) issnTable.get(ISSN);

        if (hasLink(ISSN, volString, page)) {
            link = linkbase + journal.getLinkCode() + "/" + volString + "/" + page;
        }
        System.out.println("AIP link:" + link);
        return link;
    }

    class AIPJournal {
        private String linkCode;
        private int volBegin;

        public AIPJournal(String _linkCode, int _volBegin) {
            this.linkCode = _linkCode;
            this.volBegin = _volBegin;
        }

        public int getVolBegin() {
            return this.volBegin;
        }

        public String getLinkCode() {
            return this.linkCode;
        }
    }
}