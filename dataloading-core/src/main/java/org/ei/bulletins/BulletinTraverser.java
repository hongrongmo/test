package org.ei.bulletins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.oro.text.perl.Perl5Util;

public class BulletinTraverser {

    private Perl5Util perl = new Perl5Util();
    PrintWriter writer = null;
    PrintWriter error = null;
    private static final String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
        "December" };
    List<Bulletin> lstBulletins = new Vector<Bulletin>();

    public BulletinTraverser() {

        init();
    }

    public void init() {

        try {
            writer = new PrintWriter(new FileWriter("c:\\elsevier\\code\\bulletin.dat"));
            error = new PrintWriter(new FileWriter("c:\\elsevier\\code\\bulletin.bad"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String parseYear(String pubDate, String year, String fileName, String category, String db) {

        String sYear = null;

        try {

            if (perl.match("/[0-9][0-9][0-9][0-9]/", pubDate)) {
                sYear = perl.getMatch().toString();
            }
        } catch (RuntimeException ex) {
            error.println("Error[" + year + "]: Missing Publication Date ---> DB: " + db + " File Name: " + fileName + " " + "Category: " + category);
        }

        return sYear;
    }

    public String getPubDt(String sDate) {

        String month = "";
        String day = "";
        String year = "";

        for (int i = 0; i < months.length; i++) {
            if (perl.match("/" + months[i] + "/", sDate)) {
                month = perl.getMatch().toString();
            }
        }

        if (perl.match("/\\s+[0-9]+,/", sDate)) {
            String day1 = perl.getMatch().toString();
            day = perl.substitute("s/,//g", day1);
        }
        if (perl.match("/[0-9][0-9][0-9][0-9]/", sDate)) {
            year = perl.getMatch().toString();
        }

        StringBuffer dt = new StringBuffer();

        if (day != null && day.trim().length() == 1)
            dt.append("0").append(day.trim());
        else
            dt.append(day);

        if (!day.equals(""))
            dt.append("-");

        dt.append(month).append("-").append(year);

        return dt.toString();

    }

    private void walkHtmlDirTree(File root) {
        String dirlist[];
        File current_file;
        dirlist = root.list();

        String path1 = null;

        for (int i = 0; i < dirlist.length; i++) {
            String path = root.getPath() + File.separator + dirlist[i];

            int index = path.indexOf("api");

            System.out.println("Path=" + path);

            if (index != -1)
                path1 = path.substring(index);

            current_file = new File(path);

            if (current_file.isDirectory()) {
                walkHtmlDirTree(current_file);
            } else if (current_file.isFile()) {
                if (path1.endsWith(".htm")) {

                    // System.out.println(path1);
                    List<?> dirsTokens = new Vector<Object>();

                    path1 = path1.replace('\\', ';');

                    perl.split(dirsTokens, "/;/", path1);

                    String db = ((String) dirsTokens.get(1)).trim();

                    String year1 = ((String) dirsTokens.get(2)).trim();
                    String category = ((String) dirsTokens.get(3)).trim();
                    String fileName = ((String) dirsTokens.get(5)).trim();
                    int index1 = -1;

                    if (fileName.indexOf(".htm") != -1) {
                        index1 = fileName.indexOf(".htm");

                    }

                    fileName = fileName.substring(0, index1);

                    String pubDt = parsePubDt(path);
                    String year = parseYear(pubDt, year1, fileName, category, db);

                    Bulletin bt = new Bulletin();
                    bt.setDatabase(db);
                    bt.setYear(year);
                    bt.setPublishedDt(pubDt);
                    bt.setCategory(category);
                    bt.setFileName(fileName);

                    year = removeSpaces(year);
                    year1 = removeSpaces(year1);

                    if (year != null && !year.equals(year1))
                        error.println("Error[" + year1 + "]: Publication Year Mismatch ---> /api/" + db + "/" + year1 + "/" + category + "/" + "html/"
                            + fileName + ".htm");

                    if (year != null)
                        lstBulletins.add(bt);

                    dirsTokens.clear();
                }
            }
        }
    }

    private void walkZipDirTree(File root) {
        String dirlist[];
        File current_file;
        dirlist = root.list();

        String path1 = null;

        for (int i = 0; i < dirlist.length; i++) {
            String path = root.getPath() + File.separator + dirlist[i];

            int index = path.indexOf("api");

            if (index != -1)
                path1 = path.substring(index);

            current_file = new File(path);

            if (current_file.isDirectory()) {
                walkZipDirTree(current_file);
            } else if (current_file.isFile()) {
                if (path1.endsWith(".zip")) {

                    List<?> dirsTokens = new Vector<Object>();

                    path1 = path1.replace('\\', ';');

                    perl.split(dirsTokens, "/;/", path1);

                    String db = ((String) dirsTokens.get(1)).trim();

                    String year = ((String) dirsTokens.get(2)).trim();
                    String category = ((String) dirsTokens.get(3)).trim();
                    String fileName = ((String) dirsTokens.get(5)).trim();
                    int index1 = -1;
                    @SuppressWarnings("unused")
                    boolean isHtml = false;
                    boolean isZip = false;

                    if (fileName.indexOf(".htm") != -1) {
                        index1 = fileName.indexOf(".htm");
                        isHtml = true;
                    }

                    if (fileName.indexOf(".zip") != -1) {
                        index1 = fileName.indexOf(".zip");
                        isZip = true;
                    }

                    fileName = fileName.substring(0, index1);

                    if (isZip) {
                        for (Iterator<Bulletin> iter = lstBulletins.iterator(); iter.hasNext();) {
                            Bulletin element = (Bulletin) iter.next();
                            String zipFileName = fileName;

                            String db2 = element.getDatabase();
                            String yr2 = element.getYear();
                            String cy2 = element.getCategory();
                            String fl3 = element.getFileName();

                            String key = db + year + category;

                            String currKey = db2 + yr2 + cy2;

                            String htmlSuffix = "";
                            String zipSuffix = "";

                            if (key.equals(currKey)) {

                                if (perl.match("/[0-9][0-9]/", fl3)) {
                                    htmlSuffix = perl.getMatch().toString();
                                }

                                if (perl.match("/[0-9][0-9]/", zipFileName)) {
                                    zipSuffix = perl.getMatch().toString();
                                }

                                if (htmlSuffix.equals(zipSuffix)) {
                                    element.setZipFileName(zipFileName);
                                }

                            }
                        }

                        dirsTokens.clear();
                    }
                }
            }
        }
    }

    public void write() {

        try {
            for (Iterator<Bulletin> iter = lstBulletins.iterator(); iter.hasNext();) {

                Bulletin element = (Bulletin) iter.next();

                String db = removeSpaces(element.getDatabase());
                String year = removeSpaces(element.getYear());
                String category = removeSpaces(element.getCategory());
                String fileName = removeSpaces(element.getFileName());
                String zp = removeSpaces(element.getZipFileName());

                if (zp == null)
                    zp = "";

                int month = 0;
                String pubDt = removeSpaces(element.getPublishedDt());
                for (int i = 0; i < months.length; i++) {
                    if (perl.match("/" + months[i] + "/", pubDt)) {
                        month = i;
                        break;
                    }
                }
                String day = "";
                if (perl.match("/[0-9][0-9]-/", pubDt)) {
                    day = perl.getMatch().toString();
                    day = perl.substitute("s/-//g", day);
                }
                month++;

                writer.println(getDbId(db) + "\t" + category + "\t" + year + "\t" + pubDt + "\t" + fileName + "\t" + zp + "\t" + month + "\t" + day);
                writer.flush();

            }
        } catch (Exception ex) {
        } finally {
            close(writer);
            close(error);
        }
    }

    public void close(PrintWriter writer) {

        if (writer != null)
            writer.close();
    }

    public String getDbId(String sDbId) {

        String id = "";

        if (sDbId.equalsIgnoreCase("lit"))
            id = "1";
        else if (sDbId.equalsIgnoreCase("patent"))
            id = "2";

        return id;

    }

    public String removeSpaces(String sVal) {

        if (sVal != null)
            sVal = perl.substitute("s/ //g", sVal);

        return sVal;
    }

    public String parsePubDt(String path) {

        FileReader in = null;
        BufferedReader bufRdr = null;
        boolean found = false;
        String pubdt = null;

        try {
            in = new FileReader(path);
            bufRdr = new BufferedReader(in);

            String line = null;

            while ((line = bufRdr.readLine()) != null) {
                for (int i = 0; i < months.length; i++) {
                    if (line.indexOf(months[i]) != -1) {
                        pubdt = getPubDt(line);
                        found = true;
                        break;
                    }
                }
                if (found)
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufRdr != null) {

                try {
                    bufRdr.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return pubdt;
    }

    public static void main(String args[]) {

        BulletinTraverser traverser = new BulletinTraverser();
        System.out.println("Traversing Bulletin File System...");
        traverser.walkHtmlDirTree(new File("C:\\elsevier\\docs\\release\\encompassweb\\bulletin\\api"));
        traverser.walkZipDirTree(new File("C:\\elsevier\\docs\\release\\encompassweb\\bulletin\\api"));
        traverser.write();
        System.out.println("Finished");
    }
}