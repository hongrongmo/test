package org.ei.fulldoc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;

public class DOISifter {
    private String infile;
    private BufferedReader in;
    private PrintWriter good;
    private PrintWriter bad;
    private List<String> doiGrouping = new ArrayList<String>();
    private String lastpubid = "";
    private int commitCount = 0;

    private Perl5Util perl = new Perl5Util();

    public static void main(String args[]) throws Exception {
        String infile = args[0];
        DOISifter sifter = new DOISifter(infile);
        sifter.sift();
    }

    public DOISifter(String infile) {
        this.infile = infile;
    }

    public void commit(PrintWriter w) throws Exception {
        for (int i = 0; i < doiGrouping.size(); i++) {
            commitCount++;
            String line = (String) doiGrouping.get(i);
            w.println(line);
        }
        doiGrouping.clear();
    }

    public void sift() throws Exception {

        try {
            in = new BufferedReader(new FileReader(this.infile));
            good = new PrintWriter(new FileWriter(this.infile + ".good"));
            bad = new PrintWriter(new FileWriter(this.infile + ".bad"));
            String line = null;
            int count = 0;
            while ((line = in.readLine()) != null) {
                count++;
                String pubid = null;
                // System.out.println(line);
                if (perl.match("#http://dx.doi.org/([^/]*)/(\\S\\S\\S)#", line)) {
                    pubid = perl.group(1);
                    pubid = pubid + "/" + perl.group(2);
                }

                System.out.println(count + " " + pubid);

                if ((lastpubid.equals(pubid) || doiGrouping.size() == 0)) {
                    doiGrouping.add(line);
                } else {
                    if (doiGrouping.size() > 1) {
                        commit(good);
                    } else {
                        commit(bad);
                    }

                    doiGrouping.add(line);
                }

                lastpubid = pubid;
            }

            if (doiGrouping.size() > 1) {
                commit(good);
            } else {
                commit(bad);
            }
            System.out.println("Commit count:" + commitCount);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (good != null) {
                try {
                    good.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bad != null) {
                try {
                    bad.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}