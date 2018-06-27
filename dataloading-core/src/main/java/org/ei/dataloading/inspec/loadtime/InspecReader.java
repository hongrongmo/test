package org.ei.dataloading.inspec.loadtime;

import java.io.FilterReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Text;
import org.jdom2.input.SAXBuilder;
import org.ei.common.*;

public class InspecReader extends FilterReader {
    private Hashtable<String, StringBuffer> record = null;
    private List<?> articles = null;
    private Document doc = null;
    private Iterator<?> rec = null;
    private Perl5Util perl = new Perl5Util();
    private boolean inabstract = false;
    private HashSet<String> entity = null;

    public static void main(String[] args) throws Exception {

        String filename = args[0];
        /*
         * Hashtable rec; InspecReader1 r = new InspecReader1(filename); while((rec=r.getRecord())!=null) { System.out.println(rec.toString()); }
         */

    }

    public InspecReader(Reader r) throws Exception {
        super(r);
        SAXBuilder builder = new SAXBuilder();
        builder.setExpandEntities(false);
        this.doc = builder.build(this);
        Element inspecRoot = doc.getRootElement();
        this.articles = inspecRoot.getChildren("article");
        this.rec = articles.iterator();

    }

    public int getRecordCount() {
        return articles.size();
    }

    public void close() {
        System.out.println("Closed");
    }

    public Hashtable<String, StringBuffer> getRecord() {
        entity = new HashSet<String>();
        if (rec.hasNext()) {
            Element article = (Element) rec.next();
            record = new Hashtable<String, StringBuffer>();

            /*
             * Control Group Elements
             */

            Element controlGroup = article.getChild("contg");
            // AN Number
            record.put("ANUM", new StringBuffer(controlGroup.getChild("uid").getTextTrim()));

            // Year and Issue No.
            record.put("SU", new StringBuffer(controlGroup.getChild("saiss").getTextTrim()));

            // Copyright name
            if (controlGroup.getChild("crt") != null)
                record.put("CRT", getCRT(controlGroup.getChild("crt")));

            record.put("ABNUM", getABNUM(controlGroup));

            // Record type
            record.put("RTYPE", new StringBuffer(controlGroup.getChild("rtyp").getTextTrim()));

            /*
             *
             * Biblio Group Elements
             */
            Element bibGroup = article.getChild("bibliog");

            // Title
            record.put("TI", getMixData(bibGroup.getChild("ti").getContent(), new StringBuffer()));

            // Authors
            if (bibGroup.getChild("aug") != null)
                record.put("AUS", getName(bibGroup.getChild("aug")));

            // Journal
            if (bibGroup.getChild("jrefg") != null) {
                if (bibGroup.getChild("jrefg").getChild("jrag") != null) {
                    getJournalData(bibGroup.getChild("jrefg").getChild("jrag"));
                    List<?> j = bibGroup.getChild("jrefg").getChildren("jrag");
                    if (j.size() > 1)
                        record.put("SOURCE", getSource(j));
                }
                if (bibGroup.getChild("jrefg").getChild("jrtpg") != null) {
                    getJournalData(bibGroup.getChild("jrefg").getChild("jrtpg"));
                    List<?> j = bibGroup.getChild("jrefg").getChildren("jrtpg");
                    if (j.size() > 1)
                        record.put("TSOURCE", getSource(j));
                }

            }
            // Book Group
            if (bibGroup.getChild("bookg") != null)
                getBookData(bibGroup.getChild("bookg"));

            // Report Group
            if (bibGroup.getChild("rptg") != null)
                getBookData(bibGroup.getChild("rptg"));

            // Dissertation Group
            if (bibGroup.getChild("dssg") != null)
                getBookData(bibGroup.getChild("dssg"));

            // Patent Group
            if (bibGroup.getChild("patg") != null)
                getPatentData(bibGroup.getChild("patg"));
            // Conference Group
            if (bibGroup.getChild("cng") != null)
                getConferenceData(bibGroup.getChild("cng"));

            // Language
            if (bibGroup.getChild("lng") != null)
                record.put("LA", getFields(bibGroup.getChild("lng")));

            // Other Info
            if (bibGroup.getChild("othinfo") != null)
                record.put("OINFO", getAbstract(bibGroup.getChild("othinfo").getContent(), new StringBuffer()));

            // Abstract Group
            if (bibGroup.getChild("absg") != null) {
                // Abstract
                if (bibGroup.getChild("absg").getChild("abs") != null)

                    record.put("AB", getAbstract(bibGroup.getChild("absg").getChild("abs").getContent(), new StringBuffer()));

                // Image
                if (bibGroup.getChild("absg").getChild("fig") != null)
                    record.put("FIG", getImage(bibGroup.getChild("absg")));
            }

            /*
             *
             * Indexing Group
             */
            Element idxGroup = article.getChild("indexg");
            if (idxGroup.getChild("cindg") != null)
                record.put("CVS", getIndexing(idxGroup.getChild("cindg"), "term"));
            if (idxGroup.getChild("origind") != null)
                record.put("OCVS", getOrgIndexing(idxGroup.getChild("origind"), "termg"));
            if (idxGroup.getChild("ccg") != null)
                record.put("CLS", getIndexing(idxGroup.getChild("ccg"), "cc"));
            if (idxGroup.getChild("origcc") != null)
                record.put("OCLS", getOrgIndexing(idxGroup.getChild("origcc"), "cc"));
            if (idxGroup.getChild("ucindg") != null)
                record.put("FLS", getIndexing(idxGroup.getChild("ucindg"), "term"));
            if (idxGroup.getChild("udcg") != null)
                record.put("UDC", getIndexing(idxGroup.getChild("udcg"), "udc"));

            // System.out.println("Record:"+record.toString());
            return record;
        }
        return null;
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
                if (inabstract)
                    entity.add(((EntityRef) o).getName());

                b.append("&").append(((EntityRef) o).getName()).append(";");
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

    private StringBuffer getAbstract(List<?> l, StringBuffer b) {
        inabstract = true;
        b = getMixData(l, b);
        inabstract = false;
        return getDocType().append("<ABS>").append(b).append("</ABS>");
    }

    private StringBuffer getName(Element e) {
        StringBuffer name = new StringBuffer();
        List<?> pname = e.getChildren("pname");

        for (int i = 0; i < pname.size(); i++) {
            Element n = (Element) pname.get(i);

            getMixData(n.getChild("snm").getContent(), name);

            if (n.getChild("init") != null) {
                name.append(", ");
                getMixData(n.getChild("init").getContent(), name);
            }
            if (n.getChild("sfix") != null) {
                name.append(", ");
                getMixData(n.getChild("sfix").getContent(), name);
            }
            name.append("; ");
        }

        return name.delete(name.lastIndexOf("; "), name.length());
    }

    private StringBuffer getImage(Element e) {
        StringBuffer image = new StringBuffer();
        List<?> images = e.getChildren("fig");

        for (int i = 0; i < images.size(); i++) {
            Element n = (Element) images.get(i);

            image.append(n.getChild("image").getAttributeValue("img").trim());

            if (n.getChildTextTrim("image") != null) {
                image.append(" ");
                image.append(n.getChildTextTrim("image"));
            }

            if (n.getChild("caption") != null) {
                image.append(" [");
                getMixData(n.getChild("caption").getContent(), image);
                image.append("]");
            }

            image.append("; ");
        }

        return image.delete(image.lastIndexOf("; "), image.length());
    }

    private StringBuffer getABNUM(Element e) {
        StringBuffer abno = new StringBuffer();
        List<?> abnums = e.getChildren("abng");

        for (int i = 0; i < abnums.size(); i++) {
            Element n = (Element) abnums.get(i);

            abno.append(n.getChild("abno").getAttributeValue("sec"));
            if (n.getChild("yr") != null) {

                abno.append(n.getChildTextTrim("yr"));
                abno.append("-");
            }

            if (n.getChild("vol") != null) {
                abno.append(n.getChildTextTrim("vol"));
                abno.append("/");
            }
            if (n.getChild("iss") != null) {
                abno.append(n.getChildTextTrim("iss"));
                abno.append(":");
            }
            abno.append(n.getChildTextTrim("abno"));
            if (n.getChild("uidlink") != null) {
                abno.append("|").append(n.getChildTextTrim("uidlink"));
            }
            abno.append("; ");
        }

        return abno.delete(abno.lastIndexOf("; "), abno.length());
    }

    private StringBuffer getCRT(Element e) {
        StringBuffer crt = new StringBuffer();
        crt.append("Copyright ");
        crt.append(e.getChildTextTrim("yr"));
        List<?> crn = e.getChildren("crn");

        for (int i = 0; i < crn.size(); i++) {
            Element n = (Element) crn.get(i);

            crt.append(", ");
            crt.append(n.getTextTrim());
        }

        return crt;
    }

    private StringBuffer getDate(Element e) {
        StringBuffer date = new StringBuffer();

        if (e.getChild("day") != null) {
            date.append(e.getChildTextTrim("day"));
            date.append(" ");
        }
        if (e.getChild("mo") != null) {
            date.append(e.getChildTextTrim("mo"));
            date.append(" ");
        }

        date.append(e.getChildTextTrim("yr"));

        return date;
    }

    private void getJournalData(Element e) {
        String keyprfx = "";
        if (e.getName().equals("jrtpg")) {
            keyprfx = "T";
        }
        // Full Journal Title
        if (e.getChild("jt") != null)
            record.put(keyprfx + "FJT", getMixData(e.getChild("jt").getContent(), new StringBuffer()));
        // Modern Abbreviated. Title
        if (e.getChild("ajt") != null)
            record.put(keyprfx + "AJT", getMixData(e.getChild("ajt").getContent(), new StringBuffer()));
        // Original Abbreviated. Title
        record.put(keyprfx + "OJT", getMixData(e.getChild("origajt").getContent(), new StringBuffer()));

        // VID
        if (e.getChild("vid") != null) {
            if (e.getChild("vid").getChild("vol") != null)
                record.put(keyprfx + "VOL", new StringBuffer(e.getChild("vid").getChildTextTrim("vol")));
            if (e.getChild("vid").getChild("ino") != null)
                record.put(keyprfx + "ISS", new StringBuffer(e.getChild("vid").getChildTextTrim("ino")));
            if (e.getChild("vid").getChild("other") != null)
                record.put(keyprfx + "VOLISS", getMixData(e.getChild("vid").getChild("other").getContent(), new StringBuffer()));
        }

        // Page
        if (e.getChild("pgn") != null)
            record.put(keyprfx + "IPN", new StringBuffer(e.getChildTextTrim("pgn")));

        // Publication Date
        if (e.getChild("pdt") != null)
            getPubDate(e.getChild("pdt"), keyprfx);

        // Publication Country
        if (e.getChild("cntry") != null)
            record.put(keyprfx + "CPUB", getMixData(e.getChild("cntry").getContent(), new StringBuffer()));

        // DOI
        if (e.getChild("doi") != null)
            record.put(keyprfx + "DOI", new StringBuffer(e.getChildTextTrim("doi")));

    }

    private void getBookData(Element e)

    {
        if (e.getName().equals("bookg")) {
            if (e.getChild("part") != null)
                record.put("PARTNO", new StringBuffer(e.getChildTextTrim("part")));
            if (e.getChild("section") != null)
                record.put("SEC", new StringBuffer(e.getChildTextTrim("section")));

        }

        if (e.getName().equals("bookg") || e.getName().equals("rptg")) {
            if (e.getChild("pubti") != null)
                record.put("THLP", getMixData(e.getChild("pubti").getContent(), new StringBuffer()));

            if (e.getChild("editg") != null)
                record.put("EDS", getName(e.getChild("editg")));
            if (e.getChild("pgn") != null)
                record.put("IPN", new StringBuffer(e.getChildTextTrim("pgn")));
        }
        if (e.getName().equals("rptg")) {
            if (e.getChild("repno") != null)
                record.put("RNUM", new StringBuffer(e.getChildTextTrim("repno")));
        }
        if (e.getName().equals("rptg") || e.getName().equals("dssg")) {
            if (e.getChild("issorg") != null) {
                record.put("IORG", getMixData(e.getChild("issorg").getChild("orgn").getContent(), new StringBuffer()));
                if (e.getChild("issorg").getChild("cntry") != null)
                    record.put("CIORG", getMixData(e.getChild("issorg").getChild("cntry").getContent(), new StringBuffer()));
            }
        }
        if (e.getName().equals("dssg")) {
            if (e.getChild("subdt") != null) {
                if (e.getChild("subdt").getChild("sdate") != null)
                    record.put("FDATE", getDate(e.getChild("subdt").getChild("sdate")));
                if (e.getChild("subdt").getChild("odate") != null)
                    record.put("FODATE", getMixData(e.getChild("subdt").getChild("odate").getContent(), new StringBuffer()));
            }
        }
        if (e.getChild("pug") != null)
            getPub(e.getChild("pug"));
    }

    private void getConferenceData(Element e) {
        if (e.getChild("ct") != null)
            record.put("TC", getMixData(e.getChild("ct").getContent(), new StringBuffer()));
        if (e.getChild("cndt") != null)
            getPubDate(e.getChild("cndt"), "");
        if (e.getChild("loc") != null)
            record.put("CLOC", getMixData(e.getChild("loc").getContent(), new StringBuffer()));
        if (e.getChild("cntry") != null)
            record.put("CCNF", getMixData(e.getChild("cntry").getContent(), new StringBuffer()));
        if (e.getChild("cnsg") != null)
            record.put("SORG", getFields(e.getChild("cnsg")));

    }

    private StringBuffer getFields(Element e) {
        StringBuffer field = new StringBuffer();
        List<?> lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);
            getMixData(t.getContent(), field);
            field.append("; ");
        }
        return field.delete(field.lastIndexOf("; "), field.length());
    }

    private void getPatentData(Element e) {
        if (e.getChild("pdg") != null) {
            if (e.getChild("pdg").getChildTextTrim("patno") != null)
                record.put("PNUM", new StringBuffer(e.getChild("pdg").getChildTextTrim("patno")));
            if (e.getChild("pdg").getChild("cntry") != null)
                record.put("CPAT", getMixData(e.getChild("pdg").getChild("cntry").getContent(), new StringBuffer()));
            if (e.getChild("pdg").getChild("subdt") != null) {
                if (e.getChild("pdg").getChild("subdt").getChild("sdate") != null)
                    record.put("FDATE", getDate(e.getChild("pdg").getChild("subdt").getChild("sdate")));
                if (e.getChild("pdg").getChild("subdt").getChild("odate") != null)
                    record.put("FODATE", getMixData(e.getChild("pdg").getChild("subdt").getChild("odate").getContent(), new StringBuffer()));
            }
            if (e.getChild("pdg").getChild("assg") != null)
                record.put("PAS", getFields(e.getChild("pdg").getChild("assg")));
        }
        if (e.getChild("pdt") != null)
            getPubDate(e.getChild("pdt"), "");
        if (e.getChild("cntry") != null)
            record.put("CPUB", getMixData(e.getChild("cntry").getContent(), new StringBuffer()));
        if (e.getChild("pp") != null)
            record.put("NPL1", new StringBuffer(e.getChildTextTrim("pp")));
    }

    private void getPubDate(Element e, String keyprfx) {
        // Publication Date or Conference Date
        if (e.getName().equals("pdt")) {
            keyprfx = keyprfx + "P";
            getPubYear(e);
        } else if (e.getName().equals("cndt"))
            keyprfx = keyprfx + "C";

        if (e.getChild("sdate") != null)
            record.put(keyprfx + "DATE", getDate(e.getChild("sdate")));
        if (e.getChild("edate") != null)
            record.put(keyprfx + "EDATE", getDate(e.getChild("edate")));
        if (e.getChild("odate") != null)
            record.put(keyprfx + "ODATE", getMixData(e.getChild("odate").getContent(), new StringBuffer()));

    }

    private void getPubYear(Element e) {
        if (e.getChild("sdate") != null)
            record.put("PYR", new StringBuffer(e.getChild("sdate").getChildTextTrim("yr")));
        else if (e.getChild("edate") != null) {
            record.put("PYR", new StringBuffer(e.getChild("edate").getChildTextTrim("yr")));
        } else if (e.getChild("odate") != null) {
            String str = e.getChildTextTrim("odate");
            if (str.substring(str.length() - 4).matches("[1][8-9][0-9][0-9]"))
                record.put("PYR", new StringBuffer(str.substring(str.length() - 4)));
        }
    }

    private void getPub(Element e) {
        // Publication Info
        if (e.getChild("pdt") != null)
            getPubDate(e.getChild("pdt"), "");
        if (e.getChild("pnm") != null)
            record.put("PUB", getMixData(e.getChild("pnm").getContent(), new StringBuffer()));
        if (e.getChild("loc") != null)
            record.put("PPUB", getMixData(e.getChild("loc").getContent(), new StringBuffer()));
        if (e.getChild("pp") != null)
            record.put("NPL1", new StringBuffer(e.getChildTextTrim("pp")));
        if (e.getChild("doi") != null)
            record.put("DOI", new StringBuffer(e.getChildTextTrim("doi")));

    }

    private StringBuffer getSource(List<?> l) {
        StringBuffer source = new StringBuffer();

        for (int i = 1; i < l.size(); i++) {
            Element e = (Element) l.get(i);
            if (e.getChild("jt") != null)
                getMixData(e.getChild("jt").getContent(), source);
            source.append("|");
            if (e.getChild("ajt") != null)
                getMixData(e.getChild("ajt").getContent(), source);
            source.append("|");
            getMixData(e.getChild("origajt").getContent(), source);
            source.append("|");

            if (e.getChild("vid") != null) {
                if (e.getChild("vid").getChild("vol") != null)
                    source.append(e.getChild("vid").getChildTextTrim("vol"));
                source.append("|");
                if (e.getChild("vid").getChild("ino") != null)
                    source.append(e.getChild("vid").getChildTextTrim("ino"));
                source.append("|");
                if (e.getChild("vid").getChild("other") != null)
                    getMixData(e.getChild("vid").getChild("other").getContent(), source);
                source.append("|");
            } else
                source.append("|||");

            // Page
            if (e.getChild("pgn") != null)
                source.append(e.getChildTextTrim("pgn"));
            source.append("|");
            // Publication Date
            if (e.getChild("pdt") != null) {
                if (e.getChild("pdt").getChild("sdate") != null)
                    source.append(getDate(e.getChild("pdt").getChild("sdate")));
                source.append("|");
                if (e.getChild("pdt").getChild("edate") != null)
                    source.append(getDate(e.getChild("pdt").getChild("edate")));
                source.append("|");
                if (e.getChild("pdt").getChild("odate") != null)
                    getMixData(e.getChild("pdt").getChild("odate").getContent(), source);
                source.append("|");
            } else
                source.append("|||");

            // Publication Country
            if (e.getChild("cntry") != null)
                getMixData(e.getChild("cntry").getContent(), source);
            source.append("|");
            // DOI
            if (e.getChild("doi") != null)
                source.append(e.getChildTextTrim("doi"));
            source.append("~ ");
        }
        return source.delete(source.lastIndexOf("~ "), source.length());
    }

    // Indexing Methods

    private StringBuffer getIndexing(Element e, String type) {
        StringBuffer terms = new StringBuffer();
        List<?> lt = e.getChildren(type);

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);
            if (t.getName().equals("cc")) {

                if (t.getAttributeValue("type") != null && (t.getAttributeValue("type")).equals("prime")) {
                    terms.append("*");
                }
                terms.append(t.getChildTextTrim("code"));
                terms.append("; ");
            } else {
                getMixData(t.getContent(), terms);
                terms.append("; ");
            }
        }
        return terms.delete(terms.lastIndexOf("; "), terms.length());
    }

    private StringBuffer getOrgIndexing(Element e, String type) {
        StringBuffer terms = new StringBuffer();
        List<?> lt = e.getChildren(type);

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);
            if (t.getName().equals("cc")) {

                if (t.getAttributeValue("type") != null) {
                    terms.append(t.getAttributeValue("type"));
                }
                terms.append("|");

                if ((t.getChild("code")) != null) {
                    terms.append(t.getChildTextTrim("code"));
                }
                terms.append("|");

                if (t.getChild("cct") != null) {
                    getMixData(t.getChild("cct").getContent(), terms);
                }
                terms.append("|");

                if (t.getChild("cct").getAttributeValue("cmh") != null) {
                    terms.append(t.getChild("cct").getAttributeValue("cmh"));
                }
            } else {
                if (t.getChild("mterm") != null)
                    getMixData(t.getChild("mterm").getContent(), terms);
                terms.append("|");
                if (t.getChild("sterm") != null) {
                    getMixData(t.getChild("sterm").getContent(), terms);
                }
                terms.append("|");
                if (t.getChild("ssterm") != null) {
                    getMixData(t.getChild("ssterm").getContent(), terms);
                }

                terms.append("|");
                getMixData(t.getChild("mod").getContent(), terms);
            }
            terms.append("~ ");
        }
        return terms.delete(terms.lastIndexOf("~ "), terms.length());
    }

    public StringBuffer getDocType() {

        StringBuffer buf = new StringBuffer();
        if (entity.size() == 0) {
            return buf;
        }
        Iterator<String> ent = entity.iterator();

        buf.append("<?xml version=\"1.0\"?><!DOCTYPE ABS [");
        while (ent.hasNext()) {
            String e = (String) ent.next();
            buf.append("<!ENTITY ").append(e).append(" \"").append(e).append("\" > ");
        }
        buf.append("]>");
        return buf;
    }

}
