package org.ei.dataloading.compendex.loadtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.ei.util.StringUtil;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

//import org.apache.oro.text.perl.Perl5Util;

public class CompendexThesaurusXMLReader extends FilterReader {

    private Document doc = null;
    private List records = null;
    private Hashtable record = null;
    private Iterator reciterator = null;
    private Hashtable leadinTable = new Hashtable();
    // private Perl5Util perl = new Perl5Util();
    private StringUtil util = new StringUtil();
    private static ArrayList<String> sqlsequence = new ArrayList<String>();
    private static Hashtable<String, String> childelement = new Hashtable<String, String>();
    public static final String DELIM = ";";
    public static final String L = "L";
    public static final String LEADIN_TERMS = "UF";
    public static final String MAIN_TERM = "T";
    public static final String NEW_LINE = "\n";
    public static final String ROOT = "TermInfo";
    public static final String SEARCH_TERM = "searchterm";
    public static final String STATUS = "status";
    public static final String SQLLOADDELIM = "	";
    public static final String TOP_TERMS = "TT";
    public static final String HISTORY_SCOPE_NOTES = "hist";
    public static final String PRIOR_TERMS = "PT";
    public static final String CLASS_CODES = "CL";
    public static final String USE_TERMS = "USE";
    public static final String USEOR_TERMS = "USEOR";
    public static final String USEAND_TERMS = "USEAND";

    static {
        childelement.put("T", "T");
        childelement.put("DT", "DT");
        childelement.put("SN", "SN");
        childelement.put("UF", "UF");
        childelement.put("BT", "BT");
        childelement.put("RT", "RT");
        childelement.put("NT", "NT");
        childelement.put("CL", "CL");
        childelement.put("USE", "USE");
        childelement.put("USEOR", "USE");
        childelement.put("USEAND", "USE");

        sqlsequence.add("T");// "MAIN_TERM_DISPLAY");
        sqlsequence.add("searchterm");// low case "MAIN_TERM_SEARCH");
        sqlsequence.add("status");// "STATUS");
        sqlsequence.add("SN");// "SCOPE_NOTES");
        sqlsequence.add("hist");// "HISTORY_SCOPE_NOTES");
        sqlsequence.add("USE");// "USE_TERMS");
        sqlsequence.add("UF");// "LEADIN_TERMS");
        sqlsequence.add("NT");// "NARROWER_TERMS");
        sqlsequence.add("BT");// "BROADER_TERMS");
        sqlsequence.add("RT");// "RELATED_TERMS");
        sqlsequence.add("TT");// "TOP_TERMS");
        sqlsequence.add("CL");// "CLASS_CODES");
        sqlsequence.add("PT");// "PRIOR_TERMS");
        sqlsequence.add("DT");// "DATE_OF_INTRO");
    }

    public CompendexThesaurusXMLReader(Reader r) throws Exception {
        super(r);
        SAXBuilder builder = new SAXBuilder();
        builder.setExpandEntities(false);
        this.doc = builder.build(this);
        Element compendexRoot = doc.getRootElement();
        this.records = compendexRoot.getChildren(ROOT);
        this.reciterator = records.iterator();
    }

    public static void main(String[] args) throws Exception {
        String infile = args[0];
        String outfile = args[1];
        try {
            File fileout = new File(outfile);
            File filein = new File(infile);
            List list = readTRfile(filein);
            list = addData(list);
            writeTRfile(list, fileout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List readTRfile(File fin) {
        BufferedReader bufin = null;
        ArrayList tlist = new ArrayList();
        int i = 0;
        try {
            bufin = new BufferedReader(new FileReader(fin));
            CompendexThesaurusXMLReader xin = new CompendexThesaurusXMLReader(bufin);
            while (xin.reciterator.hasNext()) {
                Hashtable rec = xin.getRecord();
                tlist.add(rec);
            }
            Collection c = xin.leadinTable.values();
            tlist.addAll(c);
            Collections.sort(tlist, new SortAlgo());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufin != null) {
                    bufin.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tlist;
    }

    public static List addData(List l) {

        List list = new ArrayList();

        for (int i = 0; i < l.size(); ++i) {
            Hashtable record = (Hashtable) l.get(i);

            // Determine Status

            if (record.get(USE_TERMS) != null) {
                if (record.get(STATUS) == null) {
                    record.put(STATUS, "L");
                }
            } else {
                if (record.get(STATUS) == null) {
                    record.put(STATUS, "C");
                }
            }

            list.add(record);
        }

        return list;
    }

    private static void writeTRfile(List list, File fileout) {
        StringBuffer buf = new StringBuffer();
        FileWriter out = null;
        int id = 1;
        try {
            out = new FileWriter(fileout);
            int len = list.size();
            ;
            for (int i = 0; i < len; i++) {
                Hashtable resrec = (Hashtable) list.get(i);
                buf = new StringBuffer(NEW_LINE);
                buf.append(id++).append(SQLLOADDELIM);
                for (int a = 0; a < sqlsequence.size(); a++) {
                    if (resrec.get(sqlsequence.get(a)) != null) {
                        buf.append(resrec.get(sqlsequence.get(a)));
                    }
                    buf.append(SQLLOADDELIM);
                }
                out.write(buf.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Hashtable<String, String> getRecord() {
        int i = 0;
        if (reciterator.hasNext()) {
            Element el = (Element) reciterator.next();
            record = new Hashtable<String, String>();
            List<Element> ellist = el.getChildren();
            int size = ellist.size();

            for (int m = 0; m < ellist.size(); m++) {
                Element ch = (Element) ellist.get(m);
                String name = (String) ch.getName().trim();
                if (childelement.get(name) != null) {
                    StringBuffer databuf = new StringBuffer();
                    databuf.append(ch.getTextTrim());
                    if (!addMainTerm(record, name, databuf)) {
                        if (name.equals(CLASS_CODES)) {
                            cleanCL(record, databuf);
                        } else if (name.equals(LEADIN_TERMS)) {
                            createLTerms(record, databuf);
                        } else if ((name.equals(USEOR_TERMS)) || (name.equals(USEAND_TERMS))) {
                            record.put(USE_TERMS, formatField(removeStar(databuf.toString()), (String) record.get(USE_TERMS)));
                        } else {
                            record.put(name, formatField(removeStar(databuf.toString()), (String) record.get(name)));
                        }
                    }

                }

            }
            return record;
        }
        return null;
    }

    public void cleanCL(Hashtable<String, String> record, StringBuffer strbuf) {
        String s = strbuf.toString().trim();
        s = util.replace(s, ",", "", StringUtil.REPLACE_GLOBAL, StringUtil.MATCH_CASE_INSENSITIVE);

        StringTokenizer tokens = new StringTokenizer(s);
        StringBuffer buf = new StringBuffer();
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            buf.append(token);
            if (tokens.hasMoreTokens()) {
                buf.append(";");
            }
        }
        record.put(CLASS_CODES, buf.toString());

    }

    public String removeStar(String s) {
        return util.replace(s, "*", "", StringUtil.REPLACE_FIRST, StringUtil.MATCH_CASE_INSENSITIVE);
    }

    public String formatField(String data, String fielddata) {

        if (fielddata != null) {
            StringBuffer strbuf = new StringBuffer();
            strbuf.append(fielddata);
            strbuf.append(";");
            strbuf.append(data);
            return strbuf.toString();
        } else {
            return data;
        }
    }

    private boolean addMainTerm(Hashtable<String, String> record, String name, StringBuffer mainTerm) {
        if (name.equals(MAIN_TERM)) {
            record.put(MAIN_TERM, removeStar(mainTerm.toString()));
            record.put(SEARCH_TERM, removeStar(mainTerm.toString().toLowerCase()));
            if (mainTerm.toString().indexOf("*") > -1) {
                record.put(HISTORY_SCOPE_NOTES, "Former Ei Vocabulary term");
                record.put(STATUS, "D1993");
            }
            return true;
        } else {
            return false;
        }
    }

    private void createLTerms(Hashtable<String, String> record, StringBuffer mainTerm) {

        if (mainTerm.toString().indexOf("*") > -1) {
            record.put(PRIOR_TERMS, formatField(removeStar(mainTerm.toString()), (String) record.get(PRIOR_TERMS)));
        } else {
            record.put(LEADIN_TERMS, formatField(removeStar(mainTerm.toString()), (String) record.get(LEADIN_TERMS)));
        }
    }

    public static class SortAlgo implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            Hashtable<String, String> t1 = (Hashtable<String, String>) o1;
            Hashtable<String, String> t2 = (Hashtable<String, String>) o2;
            String term1 = (String) t1.get(SEARCH_TERM);
            String term2 = (String) t2.get(SEARCH_TERM);
            return term1.compareTo(term2);
        }
    }

}
