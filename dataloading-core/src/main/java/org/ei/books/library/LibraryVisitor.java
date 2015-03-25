package org.ei.books.library;

import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.ei.books.collections.ReferexCollection;

public class LibraryVisitor implements Visitor {

    private HashMap<String, Integer> collectionMap = new HashMap<String, Integer>();

    private TreeMap<String, Integer> colCountMap = new TreeMap<String, Integer>();

    private String[] creds;

    private boolean perpetual = true;

    public LibraryVisitor(String[] creds, boolean perpetual) {
        this.creds = creds;
        this.perpetual = perpetual;
    }

    public void visit(LibraryComponent libraryComponent) {
        System.out.println("LIBRARYCOMPONENT");
    }

    private boolean checkCredentials(Book book, String curCollectionName) {
        int subCol = book.getSubCollection();
        String colTest = curCollectionName;

        if (subCol > 0) {
            colTest = curCollectionName + Integer.toString(subCol);
        }

        if (perpetual) {
            for (int i = 0; i < creds.length; i++) {
                String curColTest = colTest.toLowerCase();
                String curCred = creds[i].toLowerCase();
                if (curColTest.equals(curCred))
                    return true;
            }
        } else {
            for (int i = 0; i < creds.length; i++) {

                String curColTest = colTest.substring(0, 3).toLowerCase();
                int k = 3;
                if ((creds[i] != null) && (creds[i].length() < 3)) {
                    k = creds[i].length();
                }
                String curCred = creds[i].substring(0, k).toLowerCase();
                if (curColTest.equals(curCred))
                    return true;
            }

        }

        return false;
    }

    public boolean isColInCreds(String collectionName) {
        boolean inCreds = false;

        for (int i = 0; i < creds.length; i++) {
            if ((creds[i] != null) && (creds[i].length() >= 3)) {
                String curColName = collectionName.substring(0, 2).toLowerCase();
                String curCred = creds[i].substring(0, 2).toLowerCase();
                if (curColName.equals(curCred)) {
                    return true;
                }
            }
        }

        return inCreds;

    }

    public void visit(BookCollection bookCollection) {
        int bookCount = 0;
        String curCollectionName = bookCollection.getName();

        if (isColInCreds(curCollectionName)) {
            for (int i = 0; i < bookCollection.getBookCount(); i++) {
                Book book = (Book) bookCollection.getBook(i);

                if (checkCredentials(book, curCollectionName)) {

                    bookCount++;
                    if (book.getCVS() != null) {
                        String cName = curCollectionName;
                        String[] cvs = book.getCVS().split(";");

                        for (int j = 0; j < cvs.length; j++) {
                            if (cvs[j] != null && cvs[j].length() > 1) {

                                if (colCountMap.containsKey(cName + ":" + cvs[j])) {
                                    Integer prevCount = (Integer) (colCountMap.get(cName + ":" + cvs[j]));
                                    Integer curCount = new Integer(prevCount.intValue() + 1);
                                    colCountMap.put(cName + ":" + cvs[j], curCount);
                                } else {
                                    colCountMap.put(cName + ":" + cvs[j], new Integer(1));
                                }

                            }
                        }
                    }
                }
            }

            colCountMap = combinedTNFBook(colCountMap);
            collectionMap.put(curCollectionName, new Integer(bookCount));
        }

        // add for combined TNF records count
        if (collectionMap.containsKey("TNFELE")) {
            Integer tnfCount = (Integer) collectionMap.get("TNFELE");
            if (collectionMap.containsKey("ELE")) {
                Integer count = (Integer) collectionMap.get("ELE");
                collectionMap.put("ELE", new Integer(tnfCount.intValue() + count.intValue()));
            } else {
                collectionMap.put("ELE", new Integer(tnfCount.intValue()));
            }

            collectionMap.remove("TNFELE");
        }

        if (collectionMap.containsKey("TNFMAT")) {
            Integer tnfCount = (Integer) collectionMap.get("TNFMAT");
            if (collectionMap.containsKey("MAT")) {
                Integer count = (Integer) collectionMap.get("MAT");
                collectionMap.put("MAT", new Integer(tnfCount.intValue() + count.intValue()));
            } else {
                collectionMap.put("MAT", new Integer(tnfCount.intValue()));
            }

            collectionMap.remove("TNFMAT");
        }

        if (collectionMap.containsKey("TNFCHE")) {
            Integer tnfCount = (Integer) collectionMap.get("TNFCHE");
            if (collectionMap.containsKey("CHE")) {
                Integer count = (Integer) collectionMap.get("CHE");
                collectionMap.put("CHE", new Integer(tnfCount.intValue() + count.intValue()));
            } else {
                collectionMap.put("CHE", new Integer(tnfCount.intValue()));
            }

            collectionMap.remove("TNFCHE");
        }

        if (collectionMap.containsKey("TNFCIV")) {
            Integer tnfCount = (Integer) collectionMap.get("TNFCIV");
            if (collectionMap.containsKey("CIV")) {
                Integer count = (Integer) collectionMap.get("CIV");
                collectionMap.put("CIV", new Integer(tnfCount.intValue() + count.intValue()));
            } else {
                collectionMap.put("CIV", new Integer(tnfCount.intValue()));
            }

            collectionMap.remove("TNFCIV");
        }

        if (collectionMap.containsKey("TNFCOM")) {
            Integer tnfCount = (Integer) collectionMap.get("TNFCOM");
            if (collectionMap.containsKey("COM")) {
                Integer count = (Integer) collectionMap.get("COM");
                collectionMap.put("COM", new Integer(tnfCount.intValue() + count.intValue()));
            } else {
                collectionMap.put("COM", new Integer(tnfCount.intValue()));
            }

            collectionMap.remove("TNFCOM");
        }

        if (collectionMap.containsKey("TNFSEC")) {
            Integer tnfCount = (Integer) collectionMap.get("TNFSEC");
            if (collectionMap.containsKey("SEC")) {
                Integer count = (Integer) collectionMap.get("SEC");
                collectionMap.put("SEC", new Integer(tnfCount.intValue() + count.intValue()));
            } else {
                collectionMap.put("SEC", new Integer(tnfCount.intValue()));
            }

            collectionMap.remove("TNFSEC");
        }

    }

    public void toXML(Writer out, String sbstate) {
        try {
            int bstate = 0;
            if (sbstate != null) {
                try {
                    bstate = Integer.parseInt(sbstate);
                    if ((bstate > 127) || (bstate < 0)) {
                        bstate = 0;
                    }
                } catch (NumberFormatException e) {
                    bstate = 0;
                }

            }
            ReferexCollection[] allcolls = ReferexCollection.allcolls;
            LibraryFilters libraryFilters = LibraryFilters.getInstance();
            String curCollectionName = null;
            List<String> existsCollectionName = new ArrayList<String>();

            for (int i = 0; i < allcolls.length; i++) {
                for (int j = 0; j < creds.length; j++) {
                    if (creds[j].length() >= 3) {
                        curCollectionName = creds[j].substring(0, 3);

                        if (curCollectionName.equals(allcolls[i].getAbbrev()) && !existsCollectionName.contains(curCollectionName)) {
                            String sub = creds[j + 1].substring(0, creds[j + 1].length() > 2 ? 3 : creds[j + 1].length());
                            if (j == creds.length - 1 || ((j + 1) < creds.length && !curCollectionName.equals(sub)) || (!perpetual && creds[j].length() == 3)) {
                                Integer subCount = (Integer) collectionMap.get(allcolls[i].getAbbrev());
                                out.write("<" + allcolls[i].getAbbrev() + ">");

                                // THM add fields for UI refresh

                                out.write("<SHORTNAME><![CDATA[" + allcolls[i].getShortname() + "*]]></SHORTNAME>");
                                out.write("<DISPLAYNAME><![CDATA[" + allcolls[i].getDisplayName() + "]]></DISPLAYNAME>");
                                out.write("<SUBCOUNT>" + subCount + "</SUBCOUNT>");

                                int colmask = allcolls[i].getColMask();

                                out.write("<BSTATE><![CDATA[<a class=\"SpLink\" href=\"/search/ebook.url?CID=ebookSearch&database=131072&bstate="
                                    + String.valueOf(((bstate & colmask) == colmask) ? (bstate - colmask) : (bstate + colmask)) + "\">");
                                out.write(((bstate & colmask) == colmask) ? "...less" : "more...");
                                out.write("</a>]]></BSTATE>");

                                out.write("<HEAD><![CDATA[<a class=\"SmWhiteText\" href=\"/search/results/quick.url?CID=quickSearchCitationFormat&database=131072&sortdir=up&sort=stsort&yearselect=yearrange&searchtype=Book&col="
                                    + URLEncoder.encode(allcolls[i].getShortname()) + "\"><b>" + "All" // allcolls[i].getDisplayName()
                                    + " (" + subCount + ")</b></a> <a class=\"SmWhiteText\"></a>]]></HEAD>");
                                out.write("<CVS>");

                                TreeSet<Map.Entry<String, Integer>> treeSet = getColTreeSet();
                                int cvscount = 0;
                                int cvscountshown = ((bstate & colmask) == colmask) ? treeSet.size() : 6;

                                for (Iterator<?> iter = treeSet.iterator(); iter.hasNext();) {
                                    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iter.next();
                                    String key = (String) entry.getKey();

                                    String curEntry = key.substring(0, 3);
                                    String curCVS = key.substring(4, key.length());
                                    if (curEntry.equals(curCollectionName)) {
                                        Integer val = (Integer) entry.getValue();

                                        java.lang.reflect.Field[] fields = libraryFilters.getClass().getDeclaredFields();
                                        for (int k = 0; k < fields.length; k++) {
                                            if (fields[k].getName().equals(curCollectionName)) {
                                                String[] filter = (String[]) fields[k].get(libraryFilters);
                                                Arrays.sort(filter);

                                                if (Arrays.binarySearch(filter, curCVS) >= 0) {
                                                    String tnfCollectionName = curCollectionName;
                                                    out.write("<CV><![CDATA[<a class=\"SpLink\" href=\"/search/results/quick.url?CID=quickSearchCitationFormat&database=131072&sortdir=up&sort=stsort&yearselect=yearrange&searchtype=Book&searchWord1={"
                                                        + URLEncoder.encode(curCVS, "UTF-8")
                                                        + "}&section1=CV&boolean1=AND&col="
                                                        + URLEncoder.encode(tnfCollectionName, "UTF-8") + "\">" + curCVS + " (" + val + ")</a><br/>]]></CV>");
                                                    cvscount++;
                                                }
                                            }
                                        }

                                    }
                                    // break out of loop depending on count which
                                    // was
                                    // set according to state of more/less links
                                    // bstate
                                    if (cvscount >= cvscountshown) {
                                        break;
                                    }

                                }

                                out.write("</CVS>");
                                out.write("</" + allcolls[i].getAbbrev() + ">");

                                existsCollectionName.add(curCollectionName);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public TreeMap<String, Integer> combinedTNFBook(TreeMap<String, Integer> colMap) {
        if (colMap != null) {
            Object[] keyArray = colMap.keySet().toArray();

            for (int j = 0; j < keyArray.length; j++) {
                if (keyArray[j] != null) {
                    String keyString = (String) keyArray[j];

                    if (keyString.length() > 6 && keyString.indexOf("TNF") == 0) {

                        if (colMap.containsKey(keyString.substring(3))) {
                            Integer prevCount = (Integer) (colMap.get(keyString.substring(3)));
                            Integer tnfCount = (Integer) (colMap.get(keyString));
                            colMap.put(keyString.substring(3), prevCount.intValue() + tnfCount.intValue());
                            colMap.remove(keyString);
                        }

                    }
                }
            }
        }
        return colMap;
    }

    public HashMap<String, Integer> getCollectionMap() {
        return collectionMap;
    }

    public TreeMap<String, Integer> getColCountMap() {
        return colCountMap;
    }

    public TreeSet<Map.Entry<String, Integer>> getColTreeSet() {

        TreeSet<Map.Entry<String, Integer>> tset = new TreeSet<Map.Entry<String, Integer>>(new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                int returnVal = -1;
                Map.Entry<String, Integer> e1 = (Map.Entry<String, Integer>) o1;
                Map.Entry<String, Integer> e2 = (Map.Entry<String, Integer>) o2;

                Integer d1 = (Integer) e1.getValue();
                Integer d2 = (Integer) e2.getValue();

                if (d1.equals(d2)) {
                    returnVal = -1;
                } else
                    returnVal = d2.compareTo(d1);

                return returnVal;
            }
        });

        tset.addAll(colCountMap.entrySet());

        return tset;
    }

    public void visit(Library library) {
        for (int i = 0; i < library.getCollectionCount(); i++) {
            BookCollection bookCollection = (BookCollection) library.getChild(i);

            bookCollection.accept(this);
        }
    }
}
