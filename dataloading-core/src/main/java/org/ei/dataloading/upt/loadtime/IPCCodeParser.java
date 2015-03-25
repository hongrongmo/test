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
public class IPCCodeParser {

    String code = null;
    static PrintWriter out = null;
    static boolean init = false;
    StringBuffer row = new StringBuffer();
    private Perl5Util perl = new Perl5Util();
    boolean startClass = false;
    StringBuffer sbtext = new StringBuffer();

    public IPCCodeParser() {
    }

    public IPCCodeParser(String outFile) {

        if (!init) {
            init = true;
            init(outFile);
        }
    }

    public void startSUBGROUP(Attributes al) {

        code = al.getValue("ID");

        if (code != null)
            code = code.trim();

    }

    public void endTITLE() {

        if (code != null && sbtext.length() > 1 && !sbtext.toString().equals("")) {
            out.println(code + "\t" + removeXmlTags(sbtext.toString()) + "\t" + "upt");
        }
        sbtext.setLength(0);
        code = null;

    }

    public void textOfTITLE(String text) {

        if (text != null && !text.equals("")) {
            text = perl.substitute("s/\\s+/ /", text);

            sbtext.append(text);

        }

    }

    public String removeXmlTags(String text) {

        if (text == null)
            return "";

        text = perl.substitute("s/\\<\\/?[a-z0-9A-Z\\s\"=]+\\/?>/ /ig", text);
        text = perl.substitute("s/\\<SREF\\s?TARGET=[a-z0-9A-Z\\s\"=\\/]+\\>/ /ig", text);
        text = perl.substitute("s/\\<SREF TYPE=[a-z0-9A-Z\\s\"=\\/]+\\>/ /ig", text);
        text = perl.substitute("s/\\<MREF START=[a-z0-9A-Z\\s\"=\\/]+\\>/ /ig", text);
        text = perl.substitute("s/\\<MREF TYPE=[a-z0-9A-Z\\s\"=\\/]+\\>/ /ig", text);
        text = perl.substitute("s/\\<FIG FILE=[\\\\\\.a-z0-9A-Z\\s\"=\\/]+\\>/ /ig", text);

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
        xml = perl.substitute("s/://ig", xml);

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
                        if (line.indexOf("</IPC>") >= 0) {
                            // System.out.println(line);

                            if (line.indexOf("ipc_subclass.dtd") == -1 && line.indexOf("ipc_toc.dtd") == -1 && validTag(line)) {
                                xml.append(line).append("\n");
                                // System.out.println(line);
                            }

                            xml.append("</xml>");

                            IPCCodeParser patentParser = new IPCCodeParser(patentOut);

                            SaxBaseHandler saxParser = new SaxBaseHandler(patentParser);
                            // String xmlFile = Entity.prepareString(substituteChars(xml.toString()));
                            // xmlFile = perl.substitute("s/\\s+/ /g", xmlFile);
                            String str = xml.substring(0, xml.length());
                            str = substituteChars(str);

                            // System.out.println(str);

                            // System.out.println(str);
                            //
                            // System.out.println("====================");

                            // System.out.println(str);

                            System.out.println("=================================================================");
                            InputSource insrc = new InputSource(new StringReader(new String(str)));
                            parser.parse(insrc, saxParser);

                            // patentParser.parseXml(xml.substring(0, xml.length()));
                            // System.out.println(xml);
                            // System.out.println("=====================");
                            xml.setLength(0);
                            xml.append("<xml>").append("\n");
                        } else {
                            if (line.indexOf("ipc_subclass.dtd") == -1 && line.indexOf("ipc_toc.dtd") == -1 && validTag(line)) {

                                xml.append(line).append("\n");
                                // System.out.println(line);
                            }

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

        String goodTags[] = { "<SUBGROUP", "<TITLE>", "</TITLE>", "</SUBGROUP>" };
        line = line.trim();
        for (int i = 0; i < goodTags.length; i++) {
            if (line.startsWith(goodTags[i])) {
                goodTag = true;
                break;
            }

        }

        return goodTag;
    }

    public static void main(String[] args) {

        IPCCodeParser ipc = new IPCCodeParser("c:\\ipc.dat");
        // String test = ipc.removeXmlTags("<FIG FILE=\"fig603.gif\" />");
        // System.out.println(test);
        ipc.parseXml(new File("C:\\elsevier\\patent_codes\\ipc\\data"), "c:\\ipc.dat", new StringBuffer("<xml>"));
    }

}
