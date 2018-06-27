/*
 * Created on Oct 7, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.dataloading.upt.loadtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.oro.text.perl.Perl5Util;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;

/**
 * @author KFokuo
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ECLACodeParser {

    String code = null;
    static PrintWriter out = null;
    static boolean init = false;
    StringBuffer row = new StringBuffer();
    private Perl5Util perl = new Perl5Util();
    boolean startClass = false;
    StringBuffer sbtext = new StringBuffer();

    public ECLACodeParser() {
    }

    public ECLACodeParser(String outFile) {

        if (!init) {
            init = true;
            init(outFile);
        }
    }

    public void startCLASS(Attributes al) {

        code = al.getValue("symbol");

        if (code != null)
            code = code.trim();

        System.out.println("Code=" + code);

    }

    public void endTITLE() {

        out.println(code + "\t" + removeXmlTags(sbtext.toString()) + "\t" + "upt");
        sbtext.setLength(0);

    }

    public void textOfTITLE(String text) {

        if (text != null && !text.equals("")) {
            text = perl.substitute("s/\\s+/ /", text);
            text = text.trim();
            sbtext.append(text);

        }

    }

    public String removeXmlTags(String text) {

        if (text == null)
            return "";

        text = perl.substitute("s/\\<\\/?[a-z0-9A-Z\\s\"=]+\\/?>/ /ig", text);
        text = perl.substitute("s/\\<ref scheme=\"ecla\"\\squalifier=\"[\\/a-z0-9A-Z\\s\"=]+\"\\>/ /ig", text);
        text = perl.substitute("s/\\<fig identifier=\"[\\\\\\.a-z0-9A-Z\\s\"=]+\"\\>/ /ig", text);
        text = perl.substitute("s/\\<fig identifier=\"images[\\/\\.a-z0-9A-Z\\s\"=]+\">/ /ig", text);

        text = perl.substitute("s/\\s+/ /", text);

        text = text.trim();

        return text;

    }

    private void init(String outFile) {

        try {
            out = new PrintWriter(new FileWriter(outFile), true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String substituteChars(String xml) {

        Perl5Util perl = new Perl5Util();

        xml = perl.substitute("s/<title>/<title><![CDATA[/ig", xml);
        xml = perl.substitute("s/<\\/title>/]]><\\/title>/ig", xml);

        return xml;

    }

    public void parseXml(File root, String patentOut, StringBuffer xml) {

        BufferedReader in = null;
        new Perl5Util();

        try {

            System.currentTimeMillis();

            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            SAXParser parser = saxParserFactory.newSAXParser();

            String dirlist[] = root.list();

            File current_file = null;

            for (int i = 0; i < dirlist.length; i++) {
                StringBuffer sbPath = new StringBuffer();

                sbPath.append(root.getPath()).append(File.separator).append(dirlist[i]);

                String path = sbPath.substring(0, sbPath.length());

                path.indexOf("api");

                current_file = new File(path);

                if (current_file.isDirectory()) {
                    if (!path.endsWith(".xml")) {
                        System.out.println(path);
                    }
                    parseXml(current_file, patentOut, xml);
                } else if (current_file.isFile() && path.endsWith(".xml")) {
                    FileInputStream xmlStream = new FileInputStream(new File(path));
                    in = new BufferedReader(new InputStreamReader(xmlStream, "UTF-8"));
                    System.out.println(path);
                    String line = null;

                    while ((line = in.readLine()) != null) {
                        if (line.indexOf("</classification-scheme>") >= 0) {
                            // System.out.println(line);

                            if (line.indexOf("class_gen.dtd") == -1) {
                                xml.append(line).append("\n");

                            }
                            ECLACodeParser patentParser = new ECLACodeParser(patentOut);

                            SaxBaseHandler saxParser = new SaxBaseHandler(patentParser);
                            // String xmlFile = Entity.prepareString(substituteChars(xml.toString()));
                            // xmlFile = perl.substitute("s/\\s+/ /g", xmlFile);
                            String str = xml.substring(0, xml.length());
                            str = substituteChars(str);

                            // System.out.println(str);
                            //
                            // System.out.println("====================");

                            // System.out.println(str);
                            InputSource insrc = new InputSource(new StringReader(new String(str)));
                            parser.parse(insrc, saxParser);

                            // patentParser.parseXml(xml.substring(0, xml.length()));
                            // System.out.println(xml);
                            // System.out.println("=====================");
                            xml.setLength(0);
                            // xml.append("<xml>").append("\n");
                        } else {
                            if (line.indexOf("class_gen.dtd") == -1)
                                xml.append(line).append("\n");

                            // System.out.println(line);

                        }
                    }

                    if (in != null) {
                        in.close();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean validTag(String line) {

        boolean goodTag = false;

        String goodTags[] = { "<class", "</class" };

        for (int i = 0; i < goodTags.length; i++) {
            if (line.indexOf(goodTags[i]) != -1) {
                goodTag = true;
                break;
            }

        }

        return goodTag;
    }

    public static void main(String[] args) {

        ECLACodeParser ecla = new ECLACodeParser("c:\\ecla.dat");
        // String test =
        // ecla.removeXmlTags("<comment>Switches therefor <explanation><ref scheme=\"ecla\">B60Q1/14M</ref>, <ref scheme=\"ecla\">B60Q1/38A</ref>, <ref scheme=\"ecla\">B60Q1/40</ref> take precedence, arrangements or fitting of control knobs on dashboard <ref scheme=\"ecla\" qualifier=\"B60K37/06\">B60K37/06</ref></explanation></comment> <upd>N0012</upd> <upd>C0302</upd></title>");
        // System.out.println(test);
        ecla.parseXml(new File("C:\\elsevier\\patent_codes\\ecla\\codes"), "c:\\ecla.dat", new StringBuffer());
    }

}
