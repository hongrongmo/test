package org.ei.dataloading.georef.loadtime;

import java.io.*;
import java.util.*;
import org.jdom2.*;     //map svn jdom into recent jdom2
import org.jdom2.input.*;

import org.apache.oro.text.perl.*;
import org.ei.util.GUID;

public class GeorefReader {
    private Hashtable record = null;

    private List articles = null;

    private Document doc = null;

    private Iterator rec = null;

    private Perl5Util perl = new Perl5Util();

    private boolean inabstract = false;

    private HashSet entity = null;

    public String loadNumber = null;

    private static String[] elementNames = new String[] { "M_ID", "ISSN",
            "EISSN", "A02", "A03", "A05", "A06", "A07", "A08", "A09", "A10",
            "A11", "A12", "A13", "ALT_SPELL", "AUTH_AFF", "AUTH_ADD",
            "AUTH_AFF_CO", "AUTH_EMAIL", "A17", "A18", "A19", "A20", "A28",
            "A29", "A21", "A22", "A23", "A24", "A25_1", "A25_2", "A26", "A27",
            "A30", "A31", "A32", "A39", "A41", "A42", "A43", "A45", "A46",
            "DOI", "COPYRIGHT", "Z01", "Z03", "Z04", "Z05", "Z15", "Z24",
            "Z32", "Z33", "Z34", "Z35", "Z36", "Z37", "Z38", "Z39", "Z44",
            "INDEX_TERMS", "UNCONTROLLED_TERMS", "Z60", "Z61", "Z62", "Z63" };

    private static int count = 0;

    /*
     * If there is field delimiter that is 2 or more values for one field eg,
     * A;B;C, use 'AUDELIMITER' between A, B and C If there is subfields in one
     * value of the field use 'IDDELIMITER' , for eg. A:1;B:2;C:3 USE
     * AUDELIMITER between A, B and C To separate fields use 'IDDELIMITER
     * between A and 1, B and 2 and C and 3.
     */
    public static final String AUDELIMITER = new String(new char[] { 30 });

    public static final String IDDELIMITER = new String(new char[] { 31 });

    BufferedWriter out = null;

    public static void main(String[] args) throws Exception {
        File inFile = new File(args[0]);
        File outFile = new File(args[1]);
        Hashtable rec;
        GeorefReader g = new GeorefReader(inFile.toURI().toURL().getPath(),
                outFile.toURI().toURL().getPath());
        g.loadNumber = args[2];
        while ((rec = g.getRecord()) != null) {

        }

        g.close();
    }

    public GeorefReader() throws Exception {

    }

    public GeorefReader(String fileName, String outfile) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        builder.setExpandEntities(false);
        this.doc = builder.build(fileName);
        Element georefRoot = doc.getRootElement();
        this.articles = georefRoot.getChildren("Reference");
        this.rec = articles.iterator();
        out = new BufferedWriter(new FileWriter(outfile));
    }

    public GeorefReader(String fileName, String outfile, String action) throws Exception {
            SAXBuilder builder = new SAXBuilder();
            builder.setExpandEntities(false);
            this.doc = builder.build(fileName);
            Element georefRoot = doc.getRootElement();
            System.out.println("in GeorefReader");
            if(action.equals("delete"))
            {
                this.articles = georefRoot.getChildren();
                System.out.println("SIZE= "+this.articles.size());
            }
            else
            {
                this.articles = georefRoot.getChildren("Reference");
            }
            this.rec = articles.iterator();
            out = new BufferedWriter(new FileWriter(outfile));
    }

    public GeorefReader(Reader r, String outfile) throws Exception {

        //super(r);
        SAXBuilder builder = new SAXBuilder();
        builder.setExpandEntities(false);
        this.doc = builder.build(r);
        Element georefRoot = doc.getRootElement();
        this.articles = georefRoot.getChildren("Reference");
        this.rec=articles.iterator();
        out = new BufferedWriter(new FileWriter(outfile));

    }

    public void close() throws Exception {
        out.close();
    }

    public int getRecordCount() {
        return articles.size();
    }

    public Hashtable getRecord() throws Exception {
        entity = new HashSet();
        String idNumber = null;

        if (rec.hasNext()) {
            Element article = (Element) rec.next();
            record = new Hashtable();
            //System.out.println("NAME= "+article.getName());

            // ISSN
            if (article.getChild("A01") != null) {
                Element issn = article.getChild("A01");
                if (issn.getChild("A01_2") != null) {
                    record.put("ISSN", concatISSN(article, "P"));
                    record.put("EISSN", concatISSN(article, "E"));
                }
            }

            // CODEN
            if (article.getChild("A02") != null) {
                record.put("A02", concatRepeatable(article, "A02"));
            }

            //TITLE_OF_SERIAL
            if (article.getChild("A03") != null) {
                record.put("A03", new StringBuffer(article.getChild("A03")
                        .getTextTrim()));

            }

            // VOLUME IDENTIFICATION DATA (FIRST ORDER DESIGNATION)
            if (article.getChild("A05") != null) {
                // record.put("A05",concatSubElements(article,"A05",2));
                record.put("A05", new StringBuffer(article.getChild("A05")
                        .getTextTrim()));
            }

            // ISSUE IDENTIFICATION DATA (SECOND ORDER DESIGNATION)
            if (article.getChild("A06") != null) {
                // record.put("A06",concatSubElements(article,"A06",2));
                record.put("A06", new StringBuffer(article.getChild("A06")
                        .getTextTrim()));
            }

            // OTHER IDENTIFICATION OF SERIAL ISSUE
            if (article.getChild("A07") != null) {
                record.put("A07", new StringBuffer(article.getChild("A07")
                        .getTextTrim()));
            }

            // TITLE
            if (article.getChild("A08") != null) {
                if (article.getChild("A08") != null) {
                    record.put("A08", getTitle(article, "A08"));
                }
            }

            if (article.getChild("A09") != null) {
                record.put("A09", getTitle(article, "A09"));

            }
            if (article.getChild("A10") != null) {
                record.put("A10", getTitle(article, "A10"));

            }

            // PERSON_ANALYTIC,ALTERNATE_AUTHOR,AUTHOR_EMAIL
            if (article.getChild("A11") != null) {
                //record.put("A11", concatSubElements(article, "A11", 1));
            	record.put("A11", checkColumnWidth(3500,"PERSON_ANALYTIC",concatSubElements(article, "A11", 1).toString()));
                record.put("AUTH_EMAIL", getAuthorEmail(article));

                Element title = article.getChild("A11");
                if (title.getChild("A11_3") != null) {
                    if (record.get("ALT_SPELL") != null) {
                        String altSpell = concatSubElements(article, "A11", 3)
                                .toString();
                        altSpell = record.get("ALT_SPELL") + AUDELIMITER
                                + altSpell.toString();
                        record.put("ALT_SPELL", altSpell);
                    } else
                        record.put("ALT_SPELL", concatSubElements(article,
                                "A11", 3));
                }

            }
            if (article.getChild("A12") != null) {
                record.put("A12", concatSubElements(article, "A12", 1));
                Element title = article.getChild("A12");
                if (title.getChild("A12_3") != null) {
                    if (record.get("ALT_SPELL") != null) {
                        String altSpell = concatSubElements(article, "A12", 3)
                                .toString();
                        altSpell = record.get("ALT_SPELL") + AUDELIMITER
                                + altSpell.toString();
                        record.put("ALT_SPELL", altSpell);
                    } else
                        record.put("ALT_SPELL", concatSubElements(article,
                                "A12", 3));
                }
            }
            if (article.getChild("A13") != null) {

                record.put("A13", concatSubElements(article, "A13", 1));
                Element title = article.getChild("A13");
                if (title.getChild("A13_3") != null) {
                    if (record.get("ALT_SPELL") != null) {
                        String altSpell = concatSubElements(article, "A13", 3)
                                .toString();
                        altSpell = record.get("ALT_SPELL") + AUDELIMITER
                                + altSpell.toString();
                        record.put("ALT_SPELL", altSpell);
                    } else
                        record.put("ALT_SPELL", concatSubElements(article,
                                "A13", 3));
                }
            }

            // AUTHOR_AFFILIATION,AUTHOR_AFFILIATION_ADDRESS,AUTHOR_AFFILIATION_COUNTRY
            StringBuffer aff = new StringBuffer();
            if (article.getChild("A14") != null) {
                record.put("AUTH_AFF", concatSubElements(article, "A14", 1));
            }

            if (article.getChild("A14") != null) {

                if (article.getChild("A14") != null) {
                    Element title = article.getChild("A14");
                    StringBuffer affAdd = new StringBuffer();

                    if (title.getChild("A14_2") != null) {
                        affAdd.append(title.getChild("A14_2").getTextTrim());
                    }
                    if (title.getChild("A14_4") != null) {
                        record.put("AUTH_AFF_CO", new StringBuffer(title
                                .getChild("A14_4").getTextTrim()));
                    }

                    record.put("AUTH_ADD", affAdd.toString());
                }
            }

            // CORPORATE BODY ASSOCIATED WITH
            if (article.getChild("A17") != null) {
                record.put("A17", concatCorporateBody(article, "A17"));
            }
            if (article.getChild("A18") != null) {
                record.put("A18", concatCorporateBody(article, "A18"));
            }
            if (article.getChild("A19") != null) {
                record.put("A19", concatCorporateBody(article, "A19"));
            }

            // COLLATION
            if (article.getChild("A20") != null) {

                record.put("A20", concatSubElements(article, "A20", 1));

            }
            if (article.getChild("A28") != null) {

                record.put("A28", concatSubElements(article, "A28", 1));

            }
            if (article.getChild("A29") != null) {

                record.put("A29", concatSubElements(article, "A29", 1));

            }
            // DATE OF PUBLICATION
            if (article.getChild("A21") != null) {
                record.put("A21", new StringBuffer(article.getChild("A21")
                        .getTextTrim()));

            }

            // LANGUAGE TEXT
            if (article.getChild("A23") != null) {
                record.put("A23", concatSubElements(article, "A23", 1));

            }

            // LANGUAGE OF SUMMARY
            if (article.getChild("A24") != null) {
                record.put("A24", concatSubElements(article, "A24", 1));

            }

            // PUBLISHER,PUBLISHER_ADDRESS
            if (article.getChild("A25") != null) {
                Element publisher = article.getChild("A25");

                if (publisher.getChild("A25_1") != null) {
                    record.put("A25_1", concatSubElements(article, "A25", 1));
                }

            }

            // PUBLISHER ADDRESSES
            if (article.getChild("A25") != null) {
                Element publisher = article.getChild("A25");
                if (publisher.getChild("A25_2") != null) {

                    record.put("A25_2", concatPublisherAddress(article, "A25"));
                }

            }

            // ISBN
            if (article.getChild("A26") != null) {
                record.put("A26", getISBNs(article));
            }

            // EDITION
            if (article.getChild("A27") != null) {
                record.put("A27", new StringBuffer(article.getChild("A27")
                        .getTextTrim()));
            }

            // NAME OF MEETING
            if (article.getChild("A30") != null) {
                record.put("A30", new StringBuffer(article.getChild("A30")
                        .getTextTrim()));
            }

            // LOCATION OF MEETING
            if (article.getChild("A31") != null) {
                Element location = article.getChild("A31");
                if (location.getChild("A31_1") != null)
                    record.put("A31", concatLocationAddress(article, "A31"));
            }

            // DATE OF MEETING
            if (article.getChild("A32") != null) {
                Element dateOfMeeting = article.getChild("A32");
                if (dateOfMeeting.getChild("A32_1") != null) {
                    record.put("A32", new StringBuffer(dateOfMeeting.getChild(
                            "A32_1").getTextTrim()));
                }
            }

            // REPORT NUMBER
            if (article.getChild("A39") != null) {
                record.put("A39", getReportNumbers(article));
            }

            // UNIVERSITY OR OTHER EDUCATIONAL INSTITUTION
            if (article.getChild("A41") != null) {
                Element university = article.getChild("A41");
                if(university.getChild("A41_1")!=null){
                    record.put("A41", new StringBuffer(university.getChild("A41_1").getTextTrim()));
                }
            }

            // TYPE OF DEGREE
            if (article.getChild("A42") != null) {
                record.put("A42", new StringBuffer(article.getChild("A42")
                        .getTextTrim()));
            }

            // AVAILABILITY OF DOCUMENT
            if (article.getChild("A43") != null) {
                record.put("A43", concatSubElements(article, "A43", 1));
            }

            // NUMBER OF REFERENCES
            if (article.getChild("A45") != null) {
                record.put("A45", new StringBuffer(article.getChild("A45")
                        .getTextTrim()));
            }

            // SUMMARY ONLY NOTE
            if (article.getChild("A46") != null) {
                record.put("A46", new StringBuffer(article.getChild("A46")
                        .getTextTrim()));
            }

            // IDENTIFICATION NUMBER
            if (article.getChild("Z01") != null) {
                record.put("Z01", new StringBuffer(article.getChild("Z01")
                        .getTextTrim()));
                idNumber = new String(article.getChild("Z01").getTextTrim());
                //System.out.println("CHILD--ID_NUMBER= "+idNumber);
            }
            else if(article.getName().equals("Z01")) {
                record.put("Z01", new StringBuffer(article.getTextTrim()));
                idNumber = new String(article.getTextTrim());
                //System.out.println("NAME--ID_NUMBER= "+idNumber);
            }

            // CATEGORY CODE
            if (article.getChild("Z03") != null) {
                record.put("Z03", concatSubElements(article, "Z03", 1));
            }

            // DOCUMENT TYPE
            if (article.getChild("Z04") != null) {
                record.put("Z04", new StringBuffer(article.getChild("Z04")
                        .getTextTrim()));
            }

            // BIOGRAPHIC LEVEL CODE
            if (article.getChild("Z05") != null) {
                record.put("Z05", new StringBuffer(article.getChild("Z05")
                        .getTextTrim()));
            }

            // ABSTRACT
            if (article.getChild("Z15") != null) {
                record.put("Z15", new StringBuffer(article.getChild("Z15")
                        .getTextTrim()));
            }

            // ANNOTATION
            if (article.getChild("Z24") != null) {
                record.put("Z24", new StringBuffer(article.getChild("Z24")
                        .getTextTrim()));
            }

            // ILLUSTRATION
            if (article.getChild("Z32") != null) {
                record.put("Z32", new StringBuffer(article.getChild("Z32")
                        .getTextTrim()));
            }

            // MAP SCALE
            if (article.getChild("Z33") != null) {
                record.put("Z33", getRepeatable(article, "Z33"));

            }

            // MAP TYPE
            if (article.getChild("Z34") != null) {
                record.put("Z34", getRepeatable(article, "Z34"));

            }

            // MEDIUM OF SOURCE
            if (article.getChild("Z35") != null) {
                record.put("Z35", getRepeatable(article, "Z35"));

            }

            // COORDINATES
            if (article.getChild("Z36") != null) {
                record.put("Z36", getCoordinates(article));

            }

            // AFFILIATION, SECONDARY
            if (article.getChild("Z37") != null) {
                record.put("Z37", checkColumnWidth(3500,"AFFILIATION_SECONDARY",getSecondAff(article, "Z37").toString()));
            }

            // SOURCE NOTE
            if (article.getChild("Z38") != null) {
                record.put("Z38", new StringBuffer(article.getChild("Z38")
                        .getTextTrim()));
            }

            // COUNTRY OF PUBLICATION
            if (article.getChild("Z39") != null) {
                Element country = article.getChild("Z39");
                record.put("Z39", new StringBuffer(country.getChild("Z39_1")
                        .getTextTrim()));
            }

            // REFERENCE SOURCE
            if (article.getChild("Z43") != null) {
                record.put("COPYRIGHT", getRepeatable(article, "Z43"));

            }

            // UPDATE CODE
            if (article.getChild("Z44") != null) {
                record.put("Z44", new StringBuffer(article.getChild("Z44")
                        .getTextTrim()));
                //loadNumber = new String(article.getChild("Z44").getTextTrim());
            }

            // INDEX TERMS,UNCONTROLLED_TERMS
            if (article.getChild("Z50") != null) {
                //record.put("INDEX_TERMS", getIndexTerms(article));
            	record.put("INDEX_TERMS", checkColumnWidth(3500,"INDEX_TERMS",getIndexTerms(article).toString()));
                //record.put("UNCONTROLLED_TERMS", getUncontrolledTerms(article));
                record.put("UNCONTROLLED_TERMS", checkColumnWidth(3500,"UNCONTROLLED_TERMS",getUncontrolledTerms(article).toString()));
            }

            // RESEARCH PROGRAM
            if (article.getChild("Z60") != null) {
                record.put("Z60", concatResearchProgram(article));

            }

            // HOLDING LIBRARY
            if (article.getChild("Z61") != null) {
                record.put("Z61", concatSubElements(article, "Z61", 1));
            }

            // URL
            if (article.getChild("Z62") != null) {
                record.put("Z62", getURL(article, "Z62"));
            }

            // TARGET AUDIENCE
            if (article.getChild("Z63") != null) {
                record.put("Z63", concatRepeatable(article, "Z63"));
            }

            // DIGITAL OBJECT IDENTIFIER
            if (article.getChild("DOI") != null) {
                record.put("DOI", new StringBuffer(article.getChild("DOI")
                        .getTextTrim()));
            }

            record.put("M_ID", new StringBuffer("grf_"
                    + (new GUID()).toString()));
            String m_id = "grf_" + idNumber.replaceAll("-", "");

            for (int i = 0; i < elementNames.length; i++) {
                if (elementNames[i].equals("DOI")) {
                    out.write("" + loadNumber + "|");
                }

                if (record.get(elementNames[i]) != null) {

                    out.write("" + record.get(elementNames[i]) + "|");

                } else {

                    out.write("|");

                }

            }

            out.write("\n");

            count++;
            return record;
        }

        return null;
    }

    private StringBuffer getMixData(List l, StringBuffer b) {
        Iterator it = l.iterator();

        while (it.hasNext()) {
            Object o = it.next();

            if (o instanceof Text) {

                String text = ((Text) o).getText();
                text = perl.substitute("s/&/&amp;/g", text);
                text = perl.substitute("s/</&lt;/g", text);
                text = perl.substitute("s/>/&gt;/g", text);

                b.append(text);

            } else if (o instanceof EntityRef) {
                if (inabstract)
                    entity.add(((EntityRef) o).getName());

                b.append("&").append(((EntityRef) o).getName()).append(";");
            } else if (o instanceof Element) {
                Element e = (Element) o;
                b.append("<").append(e.getName());
                List ats = e.getAttributes();
                if (!ats.isEmpty()) {
                    Iterator at = ats.iterator();
                    while (at.hasNext()) {
                        Attribute a = (Attribute) at.next();
                        b.append(" ").append(a.getName()).append("=\"").append(
                                a.getValue()).append("\"");
                    }
                }
                b.append(">");
                getMixData(e.getContent(), b);
                b.append("</").append(e.getName()).append(">");
            }
        }

        return b;
    }

    private StringBuffer getIndexTerms(Element e) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals("Z50")) {
                if (t.getAttribute("att1").getValue().equals("1")
                        || t.getAttribute("att1").getValue().equals("2")
                        || t.getAttribute("att1").getValue().equals("3")) {
                    if (t.getAttribute("att2") != null) {
                        field.append(t.getAttribute("att2").getValue());
                        if (t.getAttribute("att3") != null)
                            field.append(t.getAttribute("att3").getValue());
                        field.append(IDDELIMITER);
                    }

                    getMixData(t.getContent(), field);
                    field.append(AUDELIMITER);
                }

            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    private StringBuffer getUncontrolledTerms(Element e) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals("Z50")) {
                if (t.getAttribute("att1").getValue().equals("0")) {
                    getMixData(t.getContent(), field);
                    field.append(AUDELIMITER);
                }

            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    public StringBuffer getTitle(Element e, String elemName) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals(elemName)) {
                String elemSubName = elemName + "_1";

                if (t.getChild(elemSubName) != null) {
                    getMixData(t.getChild(elemSubName).getContent(), field);
                    field.append(IDDELIMITER);
                }

                elemSubName = elemName + "_2";

                if (t.getChild(elemSubName) != null) {
                    getMixData(t.getChild(elemSubName).getContent(), field);
                    ;
                }

                field.append(AUDELIMITER);

            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }

    }
    
    private String checkColumnWidth(int columnWidth,
			String columnName,
			String data) throws Exception
	{
		int cutOffPosition = 0;
		
		if(columnWidth > 0  && data!= null)
		{
			//if(data.length()>columnWidth)
			if(data.length()>columnWidth) 
			{
			//System.out.println("1="+data);
			//System.out.println("normal length="+data.length()+" codelength="+lengthCodepoints(data));
			//System.out.println("Problem: "+getDatabase()+" record "+getAccessionNumber()+"'s data for column "+columnName+" is too big. data length is "+data.length());
			//added to output oversize data for later use by hmo at 5/10/2019				
			//this.oversizeFieldOut.println(getAccessionNumber()+"\t"+getPui()+"\t"+columnName+"\t"+getUpdatenumber()+"\t"+getDatabase()+"\t"+this.filename+"\t"+data);
			
			data = data.substring(0,columnWidth);
			cutOffPosition = data.lastIndexOf(this.AUDELIMITER);
			if(cutOffPosition<data.lastIndexOf(this.IDDELIMITER))
			{
				cutOffPosition = data.lastIndexOf(this.IDDELIMITER);
			}
			if(cutOffPosition>0)
			{
				data = data.substring(0,cutOffPosition);
			}
			if(data.length()>columnWidth)
			{
				data = data.substring(0,columnWidth);
			}
			//System.out.println("2="+data);
			
			}
		}
		//this.oversizeFieldOut.flush();
		return data;
	}

    public StringBuffer getSecondAff(Element e, String elemName) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals(elemName)) {
                String elemSubName = elemName + "_1";

                if (t.getChild(elemSubName) != null) {
                    getMixData(t.getChild(elemSubName).getContent(), field);
                    field.append(IDDELIMITER);
                    // field.append(",");
                }

                elemSubName = elemName + "_2";

                if (t.getChild(elemSubName) != null) {
                    getMixData(t.getChild(elemSubName).getContent(), field);
                    field.append(IDDELIMITER);
                    // field.append(",");
                }

                elemSubName = elemName + "_4";

                if (t.getChild(elemSubName) != null) {
                    getMixData(t.getChild(elemSubName).getContent(), field);
                    ;
                }

                field.append(AUDELIMITER);
            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }

    }

    public StringBuffer getURL(Element e, String elemName) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals(elemName)) {
                String elemSubName = elemName + "_1";

                if (t.getChild(elemSubName) != null) {
                    getMixData(t.getChild(elemSubName).getContent(), field);
                    field.append(",");
                }

                elemSubName = elemName + "_2";

                if (t.getChild(elemSubName) != null) {
                    getMixData(t.getChild(elemSubName).getContent(), field);
                }

                field.append(AUDELIMITER);
            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }

    }

    private StringBuffer getISBNs(Element e) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals("A26")) {
                getMixData(t.getContent(), field);
                field.append(AUDELIMITER);
            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    private StringBuffer getReportNumbers(Element e) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals("A39")) {
                getMixData(t.getContent(), field);
                field.append(AUDELIMITER);
            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    public StringBuffer concatSubElements(Element e, String elemName, int subInt) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals(elemName)) {
                String elemSubName = elemName + "_" + subInt;

                if (t.getChild(elemSubName) != null) {
                    getMixData(t.getChild(elemSubName).getContent(), field);
                    ;
                }

                field.append(AUDELIMITER);

            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    public StringBuffer concatISSN(Element e, String issnType) {
        String elemName = "A01";
        int subInt = 2;

        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals(elemName)) {
                String elemSubName = elemName + "_" + subInt;
                String elemSubType = elemName + "_1";

                if (t.getChild(elemSubName) != null
                        && t.getChild(elemSubType).getText().equals(issnType)) {
                    getMixData(t.getChild(elemSubName).getContent(), field);
                    field.append(AUDELIMITER);
                }
            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    private StringBuffer getRepeatable(Element e, String name) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals(name)) {
                getMixData(t.getContent(), field);
                field.append(AUDELIMITER);
            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    public StringBuffer concatRepeatable(Element e, String elemName) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals(elemName)) {
                getMixData(t.getContent(), field);

                field.append(AUDELIMITER);
            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    public StringBuffer concatPublisherAddress(Element e, String elemName) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals(elemName)) {
                String elemSubNameOne = elemName + "_2";
                String elemSubNameFour = elemName + "_4";

                if (t.getChild(elemSubNameOne) != null) {
                    field.append(t.getChild(elemSubNameOne).getText());
                    if (t.getChild(elemSubNameFour) != null) {
                        field.append(", ");
                        field.append(t.getChild(elemSubNameFour).getText());
                    }
                }

                field.append(AUDELIMITER);

            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    public StringBuffer concatLocationAddress(Element e, String elemName) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals(elemName)) {
                String elemSubNameOne = elemName + "_1";
                String elemSubNameThree = elemName + "_3";

                if (t.getChild(elemSubNameOne) != null) {
                    field.append(t.getChild(elemSubNameOne).getText());
                    if (t.getChild(elemSubNameThree) != null) {
                        field.append(", ");
                        field.append(t.getChild(elemSubNameThree).getText());
                    }
                }

                field.append(AUDELIMITER);

            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    public StringBuffer getCoordinates(Element e) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals("Z36")) {
                if (t.getAttribute("att7") != null) {
                    field.append(t.getAttribute("att7").getValue());
                    field.append(IDDELIMITER);
                }

                getMixData(t.getContent(), field);
                field.append(AUDELIMITER);

            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    public StringBuffer getAuthorEmail(Element e) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals("A11")) {
                if (t.getAttribute("att3") != null) {
                    field.append(t.getAttribute("att3").getValue());
                }

            }
        }

        return field;
    }

    public StringBuffer concatCorporateBody(Element e, String elemName) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals(elemName)) {
                if (t.getChild("A17_1") != null) {
                    field.append(t.getChild("A17_1").getText());

                    if (t.getChild("A17_4") != null) {
                        field.append(", ");
                        field.append(t.getChild("A17_4").getText());
                    }
                } else if (t.getChild("A18_1") != null) {
                    field.append(t.getChild("A18_1").getText());

                    if (t.getChild("A18_4") != null) {
                        field.append(", ");
                        field.append(t.getChild("A18_4").getText());
                    }
                } else if (t.getChild("A19_1") != null) {
                    field.append(t.getChild("A19_1").getText());

                    if (t.getChild("A19_4") != null) {
                        field.append(", ");
                        field.append(t.getChild("A19_4").getText());
                    }
                }

                field.append(AUDELIMITER);

            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }

    public StringBuffer concatResearchProgram(Element e) {
        StringBuffer field = new StringBuffer();
        List lt = e.getChildren();

        for (int i = 0; i < lt.size(); i++) {
            Element t = (Element) lt.get(i);

            if (t.getName().equals("Z60")) {
                if (t.getChild("Z60_2") != null)
                    field.append(t.getChild("Z60_2").getText());
                if (t.getChild("Z60_1") != null) {
                    if (t.getChild("Z60_2") != null)
                        field.append(", ");
                    field.append(t.getChild("Z60_1").getText());
                }

                field.append(AUDELIMITER);

            }
        }

        if (field.lastIndexOf(AUDELIMITER) != -1) {
            return field.delete(field.lastIndexOf(AUDELIMITER), field.length());
        } else {
            return field;
        }
    }
}
