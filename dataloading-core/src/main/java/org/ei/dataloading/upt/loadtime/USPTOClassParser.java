package org.ei.dataloading.upt.loadtime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.domain.ClassNode;
import org.ei.util.DiskMap;

public class USPTOClassParser {
    private ClassNode[] levels;
    private Map<String, ClassNode> seekMap;
    private Perl5Util perl = new Perl5Util();
    private ClassNode topNode;
    private ClassNode rootNode;
    private ArrayList<String> allcodes = new ArrayList<String>();

    public static void main(String args[]) throws Exception {
        HashMap<String, ClassNode> seekMap = new HashMap<String, ClassNode>();
        USPTOClassParser parser = new USPTOClassParser(seekMap);
        parser.parse();
        parser.export();
    }

    public void export() throws Exception {
        DiskMap map = new DiskMap();
        map.openWrite("uspto");
        try {
            rootNode.export(map);
        } finally {
            map.optimize();
            map.close();
        }
    }

    private String getFlattenedTree(String code) {
        ClassNode currentNode = (ClassNode) seekMap.get(code);
        LinkedList<ClassNode> ll = new LinkedList<ClassNode>();
        ll.addLast(currentNode);
        while ((currentNode = currentNode.getParent()) != null) {
            ll.addLast(currentNode);
        }

        StringBuffer buf = new StringBuffer();

        while (ll.size() > 0) {
            ClassNode cn = (ClassNode) ll.removeLast();
            if (!cn.getTitle().equals("root")) {
                buf.append(cn.getTitle());
                if (ll.size() > 0) {
                    buf.append(" ; ");
                }
            }
        }

        return buf.toString();
    }

    public USPTOClassParser(Map<String, ClassNode> seekMap) {
        this.seekMap = seekMap;
        rootNode = new ClassNode("root", "root", seekMap);
    }

    public void walk(ClassNode cn, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print(".");
        }

        level++;
        System.out.println(cn.getID() + ":" + cn.getTitle());
        Iterator<?> it = (cn.getChildren()).iterator();
        while (it.hasNext()) {
            ClassNode cl = (ClassNode) it.next();
            walk(cl, level);
        }
    }

    public void parse() throws Exception {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < topClasses.length; i++) {
            String filename = getFileName(topClasses[i]);
            // System.out.println("Parseing file:"+filename);
            levels = new ClassNode[10];
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(filename));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    String topTitle = null;
                    if ((topTitle = topTitle(line)) != null) {
                        // System.out.println("Top title:"+topTitle);

                        topNode = new ClassNode(topClasses[i], topTitle, seekMap);
                        rootNode.addChild(topNode);

                    } else {
                        if (doLine(line)) {
                            String code = topClasses[i] + "/" + getCode(line);
                            String label = getLabel(line);
                            int depth = getDepth(line);
                            // System.out.println(code+":"+Integer.toString(depth)+":"+label);

                            allcodes.add(code);
                            ClassNode cn = new ClassNode(code, label, this.seekMap);

                            levels[depth] = cn;
                            if (depth > 0) {
                                levels[depth - 1].addChild(cn);
                            } else {
                                topNode.addChild(cn);
                            }

                        }
                    }
                }

            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Load time:" + Long.toString(end - begin));

        // walk(rootNode,0);

    }

    public ClassNode seek(String code) {
        return (ClassNode) seekMap.get(code);
    }

    private String getFileName(String code) {
        return "sched" + code + ".htm";
    }

    private boolean doLine(String line) {

        return perl.match("#^<tr valign=\"top\"><td width=\"2%\" valign=\"top\">#", line);
    }

    private String getCode(String line) {
        if (perl.match("/(List of Patents for class |Link to Class Definition for class )\\w+ subclass ([\\w|\\.]+)/", line)) {
            return perl.group(2).toString();
        }

        return "QQ";
    }

    private String getLabel(String line) {
        if (perl.match("#</big>([^<].*)</td></tr></table>#", line)) {
            String s = perl.group(1).toString();
            s = perl.substitute("s#</b>##", s);
            return s.trim();
        }

        return null;
    }

    private String topTitle(String line) {
        if (perl.match("/<\\/td><td valign=\"top\" class=\"ClassTitle\">([^<].*)<\\/td><\\/tr>/", line)) {
            return perl.group(1).toString();
        }

        return null;
    }

    private int getDepth(String line) {
        if (perl.match("/([1-9])_dots\\.gif/", line)) {
            return Integer.parseInt(perl.group(1).toString());
        }

        return 0;
    }

    private static String[] topClasses = { "002", "004", "005", "007", "008", "012", "014", "015", "016", "019", "023", "024", "026", "027", "028", "029",
        "030", "033", "034", "036", "037", "038", "040", "042", "043", "044", "047", "048", "049", "051", "052", "053", "054", "055", "056", "057", "059",
        "060", "062", "063", "065", "066", "068", "069", "070", "071", "072", "073", "074", "075", "076", "079", "081", "082", "083", "084", "086", "087",
        "089", "091", "092", "095", "096", "099", "100", "101", "102", "104", "105", "106", "108", "109", "110", "111", "112", "114", "116", "117", "118",
        "119", "122", "123", "124", "125", "126", "127", "128", "131", "132", "134", "135", "136", "137", "138", "139", "140", "141", "142", "144", "147",
        "148", "149", "150", "152", "156", "157", "159", "160", "162", "163", "164", "165", "166", "168", "169", "171", "172", "173", "174", "175", "177",
        "178", "180", "181", "182", "184", "185", "186", "187", "188", "190", "191", "192", "193", "194", "196", "198", "199", "200", "201", "202", "203",
        "204", "205", "206", "208", "209", "210", "211", "212", "213", "215", "216", "217", "218", "219", "220", "221", "222", "223", "224", "225", "226",
        "227", "228", "229", "231", "232", "234", "235", "236", "237", "238", "239", "241", "242", "244", "245", "246", "248", "249", "250", "251", "252",
        "254", "256", "257", "258", "260", "261", "264", "266", "267", "269", "270", "271", "273", "276", "277", "278", "279", "280", "281", "283", "285",
        "289", "290", "291", "292", "293", "294", "295", "296", "297", "298", "299", "300", "301", "303", "305", "307", "310", "312", "313", "314", "315",
        "318", "320", "322", "323", "324", "326", "327", "329", "330", "331", "332", "333", "334", "335", "336", "337", "338", "340", "341", "342", "343",
        "345", "346", "347", "348", "349", "351", "352", "353", "355", "356", "358", "359", "360", "361", "362", "363", "365", "366", "367", "368", "369",
        "370", "372", "373", "374", "375", "376", "377", "378", "379", "380", "381", "382", "383", "384", "385", "386", "388", "392", "396", "398", "399",
        "400", "401", "402", "403", "404", "405", "406", "407", "408", "409", "410", "411", "412", "413", "414", "415", "416", "417", "418", "419", "420",
        "422", "423", "424", "425", "426", "427", "428", "429", "430", "431", "432", "433", "434", "435", "436", "438", "439", "440", "441", "442", "445",
        "446", "449", "450", "451", "452", "453", "454", "455", "460", "462", "463", "464", "470", "472", "473", "474", "475", "476", "477", "482", "483",
        "492", "493", "494", "501", "502", "503", "504", "505", "507", "508", "510", "512", "514", "516", "518", "520", "521", "522", "523", "524", "525",
        "526", "527", "528", "530", "532", "534", "536", "540", "544", "546", "548", "549", "552", "554", "556", "558", "560", "562", "564", "568", "570",
        "585", "588", "600", "601", "602", "604", "606", "607", "623", "700", "701", "702", "703", "704", "705", "706", "707", "708", "709", "710", "711",
        "712", "713", "714", "715", "716", "717", "718", "719", "720", "725", "726", "800", "901", "902", "903", "930", "968", "976", "977", "984", "987",
        "D01", "D02", "D03", "D04", "D05", "D06", "D07", "D08", "D09", "D10", "D11", "D12", "D13", "D14", "D15", "D16", "D17", "D18", "D19", "D20", "D21",
        "D22", "D23", "D24", "D25", "D26", "D27", "D28", "D29", "D30", "D32", "D34", "D99", "PLT" };

}
