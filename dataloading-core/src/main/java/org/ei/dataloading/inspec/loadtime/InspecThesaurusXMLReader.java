/*
 * Created on 02.08.2006
 *
 */
package org.ei.dataloading.inspec.loadtime;

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

import org.apache.oro.text.perl.Perl5Util;
import org.ei.xml.Entity;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;

/**
 * @author Tsolovye
 *
 */
public class InspecThesaurusXMLReader extends FilterReader {
    private Document doc = null;
    private List<?> records = null;
    private Hashtable<String, String> record = null;
    private Iterator<?> reciterator = null;
    private Hashtable<String, Hashtable<String, String>> leadinTable = new Hashtable<String, Hashtable<String, String>>();
    private Perl5Util perl = new Perl5Util();
    private static ArrayList<String> sqlsequence = new ArrayList<String>();
    private static Hashtable<String, String> childelement = new Hashtable<String, String>();
    private static Hashtable<String, String> childmultielement = new Hashtable<String, String>();
    public static final String DELIM = ";";
    public static final String L = "L";
    public static final String LEADIN_TERMS = "uf";
    public static final String MAIN_TERM = "term";
    public static final String NEW_LINE = "\n";
    public static final String ROOT = "rec";
    public static final String SEARCH_TERM = "searchterm";
    public static final String STATUS = "status";
    public static final String SQLLOADDELIM = "	";
    public static final String USE_TERMS = "useterms";

    static {
        childelement.put("term", "term");
        childelement.put("status", "status");
        childelement.put("di", "di");
        childelement.put("scope", "scope");

        childmultielement.put("bt", "bt");
        childmultielement.put("tt", "tt");
        childmultielement.put("cc", "cc");
        childmultielement.put("uf", "uf");
        childmultielement.put("rt", "rt");
        childmultielement.put("pt", "pt");
        childmultielement.put("nt", "nt");

        sqlsequence.add("term");// "MAIN_TERM_DISPLAY");
        sqlsequence.add("searchterm");// low case "MAIN_TERM_SEARCH");
        sqlsequence.add("status");// "STATUS");
        sqlsequence.add("scope");// "SCOPE_NOTES");
        sqlsequence.add("hist");// "HISTORY_SCOPE_NOTES");
        sqlsequence.add("useterms");// "USE_TERMS");
        sqlsequence.add("uf");// "LEADIG_TERMS");
        sqlsequence.add("nt");// "NARROWER_TERMS");
        sqlsequence.add("bt");// "BROADER_TERMS");
        sqlsequence.add("rt");// "RELATED_TERMS");
        sqlsequence.add("tt");// "TOP_TERMS");
        sqlsequence.add("cc");// "CLASS_CODES");
        sqlsequence.add("pt");// "PRIOR_TERMS");
        sqlsequence.add("di");// "DATE_OF_INTRO");
    }

    public InspecThesaurusXMLReader(Reader r) throws Exception {
        super(r);
        SAXBuilder builder = new SAXBuilder();
        builder.setExpandEntities(false);
        this.doc = builder.build(this);
        Element inspecRoot = doc.getRootElement();
        this.records = inspecRoot.getChildren(ROOT);
        this.reciterator = records.iterator();
    }

    public static void main(String[] args) throws Exception {
        // String infile = args[0];
        // String outfile = args[1];
        String infile = "C:/inspecthesaurus/Inspec_Thes_2007-001.xml";
        String outfile = "C:/inspecthesaurus/thesaurus.dat";
        try {
            File fileout = new File(outfile);
            File filein = new File(infile);
            List<Hashtable<String, String>> list = readTRfile(filein);
            writeTRfile(list, fileout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Hashtable<String, String>> readTRfile(File fin) {
        BufferedReader bufin = null;
        ArrayList<Hashtable<String, String>> tlist = new ArrayList<Hashtable<String, String>>();
        int i = 0;
        try {
            bufin = new BufferedReader(new FileReader(fin));
            InspecThesaurusXMLReader xin = new InspecThesaurusXMLReader(bufin);
            while (xin.reciterator.hasNext()) {
                Hashtable<String, String> rec = xin.getRecord();
                tlist.add(rec);
            }
            Collection<Hashtable<String, String>> c = xin.leadinTable.values();
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

    private static void writeTRfile(List<Hashtable<String, String>> list, File fileout) {
        StringBuffer buf = new StringBuffer();
        FileWriter out = null;
        int id = 1;
        try {
            out = new FileWriter(fileout);
            int len = list.size();
            ;
            for (int i = 0; i < len; i++) {
                Hashtable<?, ?> resrec = (Hashtable<?, ?>) list.get(i);
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
            List<?> ellist = el.getChildren();
            int size = ellist.size();
            for (int m = 0; m < ellist.size(); m++) {
                Element ch = (Element) ellist.get(m);
                String name = (String) ch.getName().toLowerCase().trim();
                if (childelement.get(name) != null) {
                    if (!addMainTerm(record, name, getMixData(el.getChild(name).getContent(), new StringBuffer()))) {
                        record.put(name, Entity.prepareString(getMixData(el.getChild(name).getContent(), new StringBuffer()).toString()));
                    }
                } else if (childmultielement.get(name) != null) {
                    record.put(name, Entity.prepareString(getMultiField(ch.getChildren())));
                    if (name.equals(LEADIN_TERMS)) {
                        createLTerms(ch.getChildren(), Entity.prepareString(getMixData(el.getChild(MAIN_TERM).getContent(), new StringBuffer()).toString()));
                    }
                }
            }
            return record;
        }
        return null;
    }

    private void createLTerms(List<?> ch, String mainTerm) {
        try {
            if (mainTerm != null) {
                int len = ch.size();
                for (int j = 0; j < len; j++) {
                    Element el = (Element) ch.get(j);
                    String u = Entity.prepareString(getMixData(el.getContent(), new StringBuffer()).toString());
                    if (leadinTable.containsKey(u)) {
                        Hashtable<String, String> tht = (Hashtable<String, String>) leadinTable.get(u);
                        String useterms = (String) tht.get(USE_TERMS);
                        useterms = useterms.concat(DELIM).concat(mainTerm);
                        tht.put(USE_TERMS, useterms);
                        leadinTable.put(u, tht);
                    } else {
                        Hashtable<String, String> tht = new Hashtable<String, String>();
                        addMainTerm(tht, MAIN_TERM, new StringBuffer(u));
                        tht.put(USE_TERMS, mainTerm);
                        tht.put(STATUS, L);
                        leadinTable.put(u, tht);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(mainTerm);
            e.printStackTrace();
        }
    }

    private boolean addMainTerm(Hashtable<String, String> record, String name, StringBuffer mainTerm) {
        if (name.equals(MAIN_TERM)) {
            record.put(MAIN_TERM, Entity.prepareString(mainTerm.toString()));
            record.put(SEARCH_TERM, Entity.prepareString(mainTerm.toString().toLowerCase()));
            return true;
        } else {
            return false;
        }
    }

    private String getMultiField(List<?> ch) {
        int len = ch.size();
        StringBuffer multifield = new StringBuffer();
        for (int i = 0; i < len; i++) {
            Element el = (Element) ch.get(i);
            multifield.append(getMixData(el.getContent(), new StringBuffer()));
            if (i != len - 1) {
                multifield.append(DELIM);
            }
        }
        return multifield.toString();
    }

    public static class SortAlgo implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            Hashtable<?, ?> t1 = (Hashtable<?, ?>) o1;
            Hashtable<?, ?> t2 = (Hashtable<?, ?>) o2;
            String term1 = (String) t1.get(SEARCH_TERM);
            String term2 = (String) t2.get(SEARCH_TERM);
            return term1.compareTo(term2);
        }
    }

    private StringBuffer getMixData(List<?> l, StringBuffer b) {
        Iterator<?> it = l.iterator();
        while (it.hasNext()) {
            Object o = it.next();

            if (o instanceof Text) {
                String text = ((Text) o).getText();
                text = perl.substitute("s/&/&amp;/g", text);
                text = perl.substitute("s/</&lt;/g", text);
                text = perl.substitute("s/>/&gt;/g", text);
                text = perl.substitute("s/\n//g", text);
                text = perl.substitute("s/\r//g", text);
                b.append(text);
            } else if (o instanceof EntityRef) {
                b.append("&").append(((EntityRef) o).getName()).append(DELIM);
            } else if (o instanceof Element) {
                Element e = (Element) o;
                b.append("<").append(e.getName());
                List<?> ats = e.getAttributes();
                if (!ats.isEmpty()) {
                    Iterator<?> at = ats.iterator();
                    while (at.hasNext()) {
                        Attribute a = (Attribute) at.next();
                        b.append(" ").append(a.getName()).append("=\"").append(a.getValue()).append("\"");
                    }
                }
                b.append(">");
                getMixData(e.getContent(), b);
                b.append("</").append(e.getName()).append(">");
            }
        }
        return b;
    }

    /*
     * inspec thesaurus 2006 entities
     *
     * mu infin nu ges eta les alpha
     */
}
