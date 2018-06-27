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
import java.util.Stack;

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
public class ECLAClassParser {

    static PrintWriter out = null;
    static boolean init = false;
    StringBuffer row = new StringBuffer();
    private Perl5Util perl = new Perl5Util();
    boolean startClass = false;
    StringBuffer text = new StringBuffer();

    String root;
    ClassNode rootNode = null;
    Map<String, ClassNode> nodeMapper;
    ClassNode level2Node = null;
    ClassNode level3Node = null;
    ClassNode level4Node = null;
    String branch = null;
    boolean add2Root = false;
    String level;
    Map<String, Stack<ClassNode>> nodeMap = new HashMap<String, Stack<ClassNode>>();
    Map<String, ClassNode> seekMap = new HashMap<String, ClassNode>();
    static Hashtable<String, String> rootEclaNodes = new Hashtable<String, String>();

    static {

        rootEclaNodes.put("ecla-a.xml", "");
        rootEclaNodes.put("ecla-b.xml", "");
        rootEclaNodes.put("ecla-c.xml", "");
        rootEclaNodes.put("ecla-d.xml", "");
        rootEclaNodes.put("ecla-e.xml", "");
        rootEclaNodes.put("ecla-f.xml", "");
        rootEclaNodes.put("ecla-g.xml", "");
        rootEclaNodes.put("ecla-h.xml", "");
        rootEclaNodes.put("ecla-k.xml", "");
        rootEclaNodes.put("ecla-l.xml", "");
        rootEclaNodes.put("ecla-m.xml", "");
        rootEclaNodes.put("ecla-n.xml", "");
        rootEclaNodes.put("ecla-p.xml", "");
        rootEclaNodes.put("ecla-r.xml", "");
        rootEclaNodes.put("ecla-s.xml", "");
        rootEclaNodes.put("ecla-t.xml", "");
        rootEclaNodes.put("ecla-y.xml", "");
    }

    public ECLAClassParser(ClassNode rootNode, Map<String, ClassNode> nodeMapper) {
        this.rootNode = rootNode;
        this.nodeMapper = nodeMapper;
    }

    public ECLAClassParser() {

    }

    public void startCLASS(Attributes al) {

        String id = al.getValue("symbol");
        branch = al.getValue("branch");
        level = al.getValue("level");

        if (id != null)
            id = id.trim();

        if (level != null && level.equals("2")) {
            level2Node = new ClassNode(id, nodeMapper);
            rootNode.addChild(level2Node);

            Stack<ClassNode> st = (Stack<ClassNode>) nodeMap.get(String.valueOf(level));

            if (st == null) {
                st = new Stack<ClassNode>();
                st.push(level2Node);
                nodeMap.put(level, st);
            } else {
                st.push(level2Node);
            }

        } else if (level != null && level.equals("4")) {
            level4Node = new ClassNode(id, nodeMapper);
            Stack<ClassNode> st = (Stack<ClassNode>) nodeMap.get(String.valueOf("2"));
            level3Node = (ClassNode) st.peek();
            level3Node.addChild(level4Node);

            st = (Stack<ClassNode>) nodeMap.get(String.valueOf(level));

            if (st == null) {
                st = new Stack<ClassNode>();
                st.push(level4Node);
                nodeMap.put(level, st);
            } else {
                st.push(level4Node);
            }

        } else if (level != null && level.equals("5")) {
            if (branch != null) {
                Stack<?> st = (Stack<?>) nodeMap.get(String.valueOf("4"));
                level4Node = (ClassNode) st.peek();
                SubClassParser parser = new SubClassParser(level4Node, nodeMapper);
                parser.parseXml(root, branch, parser);
            }
        }
    }

    public void endTITLE() {

        if (level != null && level.equals("2"))
            level2Node.setTitle(removeXmlTags(text.substring(0, text.length())));
        else if (level != null && level.equals("4"))
            level4Node.setTitle(removeXmlTags(text.substring(0, text.length())));

        text.setLength(0);
    }

    public void textOfTITLE(String stext) {

        text.append(stext);

    }

    class SubClassParser {

        ClassNode parentNode;
        String file;
        Map<String, ClassNode> nodeMapper;
        ClassNode c = null;
        boolean startMainGroup = false;
        boolean startSUBGROUP = false;
        ClassNode childClassNode = null;
        ClassNode level5Node = null;
        ClassNode level7Node = null;
        String root = null;
        String level;
        StringBuffer text = new StringBuffer();
        int iLevel = 0;
        Map<String, Stack<ClassNode>> classMap = new HashMap<String, Stack<ClassNode>>();

        public SubClassParser(ClassNode parentNode, Map<String, ClassNode> nodeMapper) {
            super();
            this.parentNode = parentNode;
            this.nodeMapper = nodeMapper;
        }

        public void endTITLE() {

            if (level.equals("5"))
                level5Node.setTitle(removeXmlTags(text.substring(0, text.length())));
            else if (level.equals("7"))
                level7Node.setTitle(removeXmlTags(text.substring(0, text.length())));
            else if (iLevel > 7) {
                childClassNode.setTitle(removeXmlTags(text.substring(0, text.length())));
            }

            text.setLength(0);

        }

        public void textOfTITLE(String stext) {

            try {

                text.append(stext);
            } catch (Exception e) {
                System.out.println("Text of Title=" + text);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void startCLASS(Attributes al) {

            String id = al.getValue("symbol");

            level = al.getValue("level");

            try {

                iLevel = Integer.parseInt(level);

                startMainGroup = true;
                startSUBGROUP = false;

                if (id != null)
                    id = id.trim();

                if (level.equals("5")) {
                    level5Node = new ClassNode(id, nodeMapper);
                    parentNode.addChild(level5Node);

                    Stack<ClassNode> st = (Stack<ClassNode>) classMap.get(String.valueOf(level));

                    if (st == null) {
                        st = new Stack<ClassNode>();
                        st.push(level5Node);
                        classMap.put(level, st);
                    } else {
                        st.push(level5Node);
                    }
                } else if (level.equals("7")) {
                    level7Node = new ClassNode(id, nodeMapper);
                    level5Node.addChild(level7Node);

                    Stack<ClassNode> st = (Stack<ClassNode>) classMap.get(String.valueOf(level));

                    if (st == null) {
                        st = new Stack<ClassNode>();
                        st.push(level7Node);
                        classMap.put(level, st);
                    } else {
                        st.push(level7Node);
                    }
                } else if (iLevel > 7) {
                    childClassNode = new ClassNode(id, nodeMapper);

                    Stack<ClassNode> st = (Stack<ClassNode>) classMap.get(String.valueOf(level));

                    if (st == null) {
                        st = new Stack<ClassNode>();
                        st.push(childClassNode);
                        classMap.put(level, st);
                    } else {
                        st.push(childClassNode);
                    }

                    int depth = iLevel - 1;

                    st = (Stack<ClassNode>) classMap.get(String.valueOf(depth));

                    ClassNode parent = (ClassNode) st.peek();

                    parent.addChild(childClassNode);

                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Start Class=" + id);
                e.printStackTrace();
                System.exit(0);
            }

        }

        public boolean validTag(String line) {

            boolean goodTag = false;

            String goodTags[] = { "<title>", "</title>", "<class", "</class>" };

            line = line.trim();
            for (int i = 0; i < goodTags.length; i++) {
                if (line.indexOf(goodTags[i]) != -1) {
                    goodTag = true;
                    break;
                }

            }

            return goodTag;
        }

        public boolean isBadTag(String line) {

            boolean badTag = false;

            String badTags[] = { "<?xml", "<!DOCTYPE" };

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

                while ((line = in.readLine()) != null) {

                    if (!isBadTag(line))
                        xml.append(line).append("\n");

                    if (line.indexOf("</classification-scheme>") != -1) {

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

    public String removeXmlTags(String text) {

        if (text == null)
            return "";

        text = perl.substitute("s/<upd>/ \\[/ig", text);
        text = perl.substitute("s/<\\/upd>/\\]/ig", text);
        text = perl.substitute("s/<([^<^>]*)>/ /ig", text);
        text = perl.substitute("s/\\s+/ /g", text);

        text = text.trim();

        return text;

    }

    private String substituteChars(String xml) {

        Perl5Util perl = new Perl5Util();

        xml = perl.substitute("s/<title>/<title><![CDATA[/ig", xml);
        xml = perl.substitute("s/<\\/title>/]]><\\/title>/ig", xml);

        return xml;

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

                if (line.indexOf("</classification-scheme>") != -1) {

                    xml.append("</xml>");
                    InputSource insrc = new InputSource(new StringReader(substituteChars(Entity.prepareString(xml.toString()))));
                    SaxBaseHandler saxParser = new SaxBaseHandler(oObject);
                    // System.out.println(xml);
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

    public void parse(File root) {

        Map<String, ClassNode> nodeMapper = new HashMap<String, ClassNode>();
        ClassNode node = null;

        try {
            String dirlist[] = root.list();

            node = new ClassNode("root", "root", nodeMapper);

            for (int i = 0; i < dirlist.length; i++) {

                String file = dirlist[i].toLowerCase();

                if (rootEclaNodes.containsKey(file)) {
                    System.out.println("File=" + file);
                    ECLAClassParser eclaParser = new ECLAClassParser(node, nodeMapper);
                    eclaParser.parseXml(root.getPath(), dirlist[i], eclaParser);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            ECLAClassParser eclaParser = new ECLAClassParser();
            eclaParser.export(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void export(ClassNode node) throws Exception {

        DiskMap map = new DiskMap();
        map.openWrite("ecla");

        try {
            node.export(map);
        } finally {
            map.optimize();
            map.close();
        }
    }

    public void buildDisplay(ClassNode node) {
        ClassNode parentNode = null;

        if (node != null) {

            parentNode = node.getParent();

            if (parentNode != null)
                buildDisplay(parentNode);

        }

        if (node.getTitle() != null) {
            String title = node.getTitle().trim();
            if (!title.equals(""))
                System.out.println("** " + node.getTitle() + " - " + node.getID());
        }
    }

    public boolean isBadTag(String line) {

        boolean badTag = false;

        String badTags[] = { "<?xml version", "class_gen.dtd", "<!DOCTYPE classification" };

        line = line.trim();
        for (int i = 0; i < badTags.length; i++) {
            if (line.indexOf(badTags[i]) != -1) {
                badTag = true;
                break;
            }

        }

        return badTag;

    }

    public static void main(String[] args) {

        ECLAClassParser parser = new ECLAClassParser();
        parser.parse(new File(args[0]));

    }
}
