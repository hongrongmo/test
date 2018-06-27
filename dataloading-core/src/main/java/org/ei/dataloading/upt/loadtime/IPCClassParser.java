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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.domain.ClassNode;
import org.ei.util.DiskMap;
import org.ei.xml.Entity;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;

/**
 * @author KFokuo
 *
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IPCClassParser {

    String code = null;
    static PrintWriter out = null;
    static boolean init = false;
    StringBuffer row = new StringBuffer();
    private Perl5Util perl = new Perl5Util();

    StringBuffer text = new StringBuffer();

    String root;
    String sectionId;
    String classId;
    String subClassId;
    String title;
    ClassNode rootNode = null;
    ClassNode classNode = null;
    ClassNode sectionNode = null;
    ClassNode subClassNode = null;
    boolean startSection = false;
    boolean startSubClass = false;
    boolean startClass = false;
    boolean startTitle = false;

    Map<String, ClassNode> nodeMapper = null;

    static Hashtable<String, String> rootIpcNodes = new Hashtable<String, String>();

    static {
        rootIpcNodes.put("a.xml", "");
        rootIpcNodes.put("b.xml", "");
        rootIpcNodes.put("c.xml", "");
        rootIpcNodes.put("d.xml", "");
        rootIpcNodes.put("e.xml", "");
        rootIpcNodes.put("f.xml", "");
        rootIpcNodes.put("g.xml", "");
        rootIpcNodes.put("h.xml", "");
    }

    public IPCClassParser(ClassNode rootNode, Map<String, ClassNode> nodeMapper) {
        this.rootNode = rootNode;
        this.nodeMapper = nodeMapper;
    }

    public IPCClassParser() {

    }

    public void startSECTION(Attributes al) {

        sectionId = al.getValue("ID");

        sectionId = sectionId.trim();

        sectionNode = new ClassNode(sectionId, nodeMapper);
        rootNode.addChild(sectionNode);

        startSection = true;
        startClass = false;
        startSubClass = false;

    }

    public void startCLASS(Attributes al) {

        classId = al.getValue("ID");

        classId = classId.trim();

        classNode = new ClassNode(classId, nodeMapper);
        sectionNode.addChild(classNode);

        startSection = false;
        startClass = true;
        startSubClass = false;

    }

    public void startSUBCLASS(Attributes al) {

        try {
            subClassId = al.getValue("ID");

            subClassId = subClassId.trim();

            subClassNode = new ClassNode(subClassId, nodeMapper);
            classNode.addChild(subClassNode);

            StringBuffer file = new StringBuffer();

            file.append(subClassId).append(".xml");

            SubClassParser parser = new SubClassParser(subClassNode, nodeMapper);
            parser.parseXml(root, file.toString(), parser);

            startSection = false;
            startClass = false;
            startSubClass = true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void startTITLE() {
        startTitle = true;
    }

    public void endTITLE() {

        if (startSection) {
            sectionNode.setTitle(removeXmlTags(text.substring(0, text.length())));
        } else if (startClass) {
            classNode.setTitle((removeXmlTags(text.substring(0, text.length()))));
        } else if (startSubClass) {
            subClassNode.setTitle((removeXmlTags(text.substring(0, text.length()))));
        }

        text.setLength(0);

        startTitle = false;
        startSection = false;
        startClass = false;
        startSubClass = false;
    }

    public void textOfTITLE(String stext) {

        text.append(stext);
    }

    private String substituteChars(String xml) {

        Perl5Util perl = new Perl5Util();

        xml = perl.substitute("s/<TITLE>/<TITLE><![CDATA[/ig", xml);
        xml = perl.substitute("s/<\\/TITLE>/]]><\\/TITLE>/ig", xml);

        return xml;

    }

    public String removeXmlTags(String text) {

        if (text == null)
            return "";

        text = perl.substitute("s/<[^<^>]*>/ /ig", text);

        text = perl.substitute("s/\\s+/ /g", text);

        text = text.trim();

        return text;

    }

    class SubClassParser {

        ClassNode parentNode;
        String file;
        Map<String, ClassNode> nodeMapper;
        ClassNode mainGroupNode = null;
        boolean startMainGroup = false;
        boolean startSUBGROUP = false;
        ClassNode subGroup = null;
        String root = null;
        String subClassId;

        public SubClassParser(ClassNode parentNode, Map<String, ClassNode> nodeMapper) {
            super();
            this.parentNode = parentNode;
            this.nodeMapper = nodeMapper;
        }

        public void textOfTITLE(String stext) {

            text.append(stext);

        }

        public void startTITLE() {
            startTitle = true;
        }

        public void endTITLE() {

            if (startMainGroup)
                mainGroupNode.setTitle(removeXmlTags(text.substring(0, text.length())));
            else if (startSUBGROUP)
                subGroup.setTitle(removeXmlTags(text.substring(0, text.length())));

            startTitle = false;
            startMainGroup = false;
            startSUBGROUP = false;

            text.setLength(0);
        }

        public void startSUBCLASS(Attributes al) {

            subClassId = al.getValue("ID");

        }

        public void startMAINGROUP(Attributes al) {

            String id = al.getValue("ID");

            try {

                if (id != null)
                    id = id.trim();

                String parsedId = perl.substitute("s/" + subClassId + "//i", id);

                StringBuffer newId = new StringBuffer();

                newId.append(subClassId).append(removeLeadingZeros(parsedId));

                id = newId.toString();

                startMainGroup = true;
                startSUBGROUP = false;

                mainGroupNode = new ClassNode(id, nodeMapper);
                parentNode.addChild(mainGroupNode);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Start Main Group=" + id);
                e.printStackTrace();
                System.exit(0);
            }
        }

        public void startSUBGROUP(Attributes al) {

            String id = al.getValue("ID");
            String level = al.getValue("DOT");

            int iLevel = Integer.parseInt(level);

            try {

                startSUBGROUP = true;
                startMainGroup = false;
                String parsedId = perl.substitute("s/" + subClassId + "//i", id);

                id = removeLeadingZeros(parsedId);

                StringBuffer newId = new StringBuffer();

                newId.append(subClassId).append(id);

                id = newId.toString();

                subGroup = new ClassNode(id, nodeMapper);

                if (iLevel == 1) {
                    mainGroupNode.addChild(subGroup);
                } else if (iLevel > 1) {
                    ClassNode currNode = mainGroupNode.getCurrentChildNode();
                    currNode.addChild(subGroup);
                }
            } catch (Exception e) {
                System.out.println("Start Sub Group=" + id + " Level=" + level);
                e.printStackTrace();
                System.exit(0);
            }
        }

        public String removeLeadingZeros(String sVal) {

            if (sVal == null) {
                return sVal;
            }

            char[] schars = sVal.toCharArray();
            int index = 0;
            for (; index < sVal.length(); index++) {

                if (schars[index] != '0') {
                    break;
                }
            }

            return (index == 0) ? sVal : sVal.substring(index);
        }

        public boolean validTag(String line) {

            boolean goodTag = false;

            String goodTags[] = { "<TITLE>", "</TITLE>", "<MAINGROUP ID", "</MAINGROUP>", "<SUBGROUP ID", "</SUBGROUP>", "<SUBCLASS", "</SUBCLASS>" };

            line = line.trim();
            for (int i = 0; i < goodTags.length; i++) {
                if (line.startsWith(goodTags[i])) {
                    goodTag = true;
                    break;
                }

            }

            return goodTag;
        }

        public boolean isBadTag(String line) {

            boolean badTag = false;

            String badTags[] = { "ipc_toc.dtd", "<?xml-stylesheet", "ipc_subclass.dtd" };

            line = line.trim();
            for (int i = 0; i < badTags.length; i++) {
                if (line.indexOf(badTags[i]) != -1) {
                    badTag = true;
                    break;
                }

            }

            return badTag;
        }

        public void parseXml(String root, String file, Object oObject) {

            try {
                StringBuffer xml = new StringBuffer();
                this.root = root;
                StringBuffer fullPath = new StringBuffer();
                fullPath.append(root).append(File.separator).append(file);

                FileInputStream fin = new FileInputStream(fullPath.toString());

                BufferedReader in = new BufferedReader(new InputStreamReader(fin, "ISO-8859-1"));

                String line = null;
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

                SAXParser parser = saxParserFactory.newSAXParser();
                xml.append("<xml>");
                xml.append("<ipc>");
                while ((line = in.readLine()) != null) {

                    if (!isBadTag(line) && validTag(line))
                        xml.append(line).append("\n");

                    if (line.indexOf("</SUBCLASS>") != -1) {
                        xml.append("</ipc>");
                        xml.append("</xml>");
                        InputSource insrc = new InputSource(new StringReader(substituteChars(Entity.prepareString(xml.toString()))));
                        SaxBaseHandler saxParser = new SaxBaseHandler(oObject);

                        try {
                            parser.parse(insrc, saxParser);
                        } catch (Exception e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                            System.out.println(xml);
                            System.exit(0);
                        }
                    }
                }
                if (in != null) {
                    in.close();
                }
            }

            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void parseXml(String root, String file, Object oObject) {

        try {
            StringBuffer xml = new StringBuffer();

            this.root = root;

            StringBuffer fullPath = new StringBuffer();
            fullPath.append(root).append(File.separator).append(file);

            FileInputStream fin = new FileInputStream(fullPath.toString());

            BufferedReader in = new BufferedReader(new InputStreamReader(fin, "ISO-8859-1"));

            String line = null;
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

            SAXParser parser = saxParserFactory.newSAXParser();
            xml.append("<xml>");

            while ((line = in.readLine()) != null) {

                if (!isBadTag(line))
                    xml.append(line).append("\n");

                if (line.indexOf("</IPC>") != -1) {

                    xml.append("</xml>");

                    InputSource insrc = new InputSource(new StringReader(substituteChars(Entity.prepareString(xml.toString()))));
                    SaxBaseHandler saxParser = new SaxBaseHandler(oObject);

                    try {
                        parser.parse(insrc, saxParser);
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                        System.out.println(xml);
                        System.exit(0);
                    }
                }
            }

            if (in != null) {
                in.close();
            }
        }

        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void loadIPCCodes(File root) {

        Map<String, ClassNode> nodeMapper = new HashMap<String, ClassNode>();

        ClassNode node = null;

        try {
            String dirlist[] = root.list();

            node = new ClassNode("root", "root", nodeMapper);

            for (int i = 0; i < dirlist.length; i++) {

                String file = dirlist[i].toLowerCase();

                if (rootIpcNodes.containsKey(file)) {
                    IPCClassParser ipcParser = new IPCClassParser(node, nodeMapper);
                    ipcParser.parseXml(root.getPath(), dirlist[i], ipcParser);

                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void buildDisplay(ClassNode node) {
        ClassNode parentNode = null;

        if (node != null) {

            parentNode = node.getParent();

            if (parentNode != null)
                buildDisplay(parentNode);

        }

    }

    public boolean isBadTag(String line) {

        boolean badTag = false;

        String badTags[] = { "ipc_toc.dtd", "<?xml version", "<?xml-stylesheet", "ipc_subclass.dtd", "<?cocoon-process", "<!DOCTYPE IPC" };

        line = line.trim();
        for (int i = 0; i < badTags.length; i++) {
            if (line.indexOf(badTags[i]) != -1) {
                badTag = true;
                break;
            }

        }

        return badTag;
    }

    public void export(ClassNode node) throws Exception {

        DiskMap map = new DiskMap();
        map.openWrite("ipc");

        try {

            node.export(map);

        } finally {
            map.optimize();
            map.close();
        }
    }

    public void parse(File root) {

        Map<String, ClassNode> nodeMapper = new HashMap<String, ClassNode>();
        ClassNode node = null;

        try {
            String dirlist[] = root.list();

            node = new ClassNode("root", "root", nodeMapper);

            for (int i = 0; i < dirlist.length; i++) {

                String file = dirlist[i].toLowerCase();

                if (rootIpcNodes.containsKey(file)) {
                    System.out.println("File=" + file);
                    IPCClassParser ipcParser = new IPCClassParser(node, nodeMapper);
                    ipcParser.parseXml(root.getPath(), dirlist[i], ipcParser);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            IPCClassParser ipcParser = new IPCClassParser();
            ipcParser.export(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //Map<String, ClassNode> seekMap = new HashMap<String, ClassNode>();
        IPCClassParser parser = new IPCClassParser();
        parser.parse(new File(args[0]));
        // DiskMap dm = new DiskMap();
        //
        // try {
        // dm.openRead("ipc", false);
        // String val = dm.get("C07C6995");
        //
        // System.out.println("Val="+val);
        // }
        // catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

    }

}
