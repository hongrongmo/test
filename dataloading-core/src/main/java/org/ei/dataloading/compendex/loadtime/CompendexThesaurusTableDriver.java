package org.ei.dataloading.compendex.loadtime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.ei.dataloading.ThesaurusTableDriver;
import org.ei.util.StringUtil;

public class CompendexThesaurusTableDriver extends ThesaurusTableDriver {

    private StringUtil util = new StringUtil();
    private String infile;
    private String outfile;

    public static void main(String args[]) throws Exception {
        String infile = args[0];
        String outfile = args[1];
        CompendexThesaurusTableDriver driver = new CompendexThesaurusTableDriver(infile, outfile);
        driver.createData();
    }

    public CompendexThesaurusTableDriver(String in, String out) {
        this.infile = in;
        this.outfile = out;
    }

    public void createData() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        TRecord record = null;
        List<TRecord> list = new ArrayList<TRecord>();
        String state = null;
        String fieldName = null;

        try {
            reader = new BufferedReader(new FileReader(this.infile));
            writer = new PrintWriter(new FileWriter(this.outfile));
            String line = null;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {

                // System.out.println(lineNumber);
                // lineNumber++;

                if (line.length() > 0) {
                    char c = line.charAt(0);

                    int i = (int) c;

                    if (c == '*') {
                        state = "endRecord";
                    } else {
                        state = "beginField";
                    }

                    if (state.equals("beginField")) {
                        if (line.length() < 2) {
                            System.out.println(line);
                        } else {
                            fieldName = line.substring(0, 2);
                            fieldName = fieldName.toUpperCase();

                            String data = line.substring(3, line.length());

                            if (fieldName.equals(CLASS_CODES)) {
                                data = cleanCL(data);
                            }

                            if (fieldName.equals(MAIN_TERM) && data.indexOf("#") > -1) {
                                record.addFieldValue(HISTORY_SCOPE_NOTES, "Former Ei Vocabulary term");
                                record.addFieldValue(STATUS, "D1993");
                                record.addFieldValue(fieldName, removeHash(data.trim()));
                            } else {
                                record.addFieldValue(fieldName, data.trim());
                            }
                        }
                    }

                    if (state.equals("endRecord")) {
                        if (record != null) {

                            list.add(record);

                        }

                        record = new TRecord();
                    }
                }
            }

            if (record != null && record.size() > 0) {
                list.add(record);
            }

            Collections.sort(list, new SortAlgo());
            System.out.println("Size 1:" + list.size());
            list = addData(list);
            System.out.println("Size 2:" + list.size());
            writeRecords(list, writer, "cpx");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public String removeHash(String s) {
        return util.replace(s, "#", "", StringUtil.REPLACE_FIRST, StringUtil.MATCH_CASE_INSENSITIVE);
    }

    public String cleanCL(String s) {
        s = util.replace(s, ",", "", StringUtil.REPLACE_GLOBAL, StringUtil.MATCH_CASE_INSENSITIVE);

        StringTokenizer tokens = new StringTokenizer(s);
        StringBuffer buf = new StringBuffer();
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            buf.append(token);
            if (tokens.hasMoreTokens()) {
                buf.append(";");
            }
        }

        return buf.toString();

    }

    public List<TRecord> addData(List<TRecord> l) {

        List<TRecord> list = new ArrayList<TRecord>();

        for (int i = 0; i < l.size(); ++i) {
            TRecord record = (TRecord) l.get(i);

            // Determine Status

            if (record.getFieldValues(USE_TERMS) != null) {
                if (record.getFieldValues(STATUS) == null) {
                    record.addFieldValue(STATUS, "L");
                }
            } else {
                if (record.getFieldValues(STATUS) == null) {
                    record.addFieldValue(STATUS, "C");
                }
            }

            // Determine Prior or Leadin

            String ufs = record.getFieldValues("UF");
            if (ufs != null) {
                StringTokenizer tokens = new StringTokenizer(ufs, ";");
                while (tokens.hasMoreTokens()) {
                    String tok = tokens.nextToken();
                    if (tok.indexOf("#") > -1) {
                        record.addFieldValue(PRIOR_TERMS, removeHash(tok));
                    } else {
                        record.addFieldValue(LEADIN_TERMS, tok);
                    }
                }
            }

            list.add(record);
        }

        return list;
    }

}
