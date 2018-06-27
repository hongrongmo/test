package org.ei.dataloading.compendex.loadtime;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CpxCorrectionUpdateDeleteSeparator {
    private static final char CONT = 32, REC = '*', EOF = 65535;
    private static final String checkField = "HS", DELFLAG = "D";
    private static PrintWriter outputUpdate;
    private static PrintWriter outputDelete;

    public static void main(String args[]) throws Exception {

        BufferedReader in = null;
        if (args.length > 0) {
            try {
                String inFileName = args[0];
                String updateFileName = inFileName + "-update";
                String deleteFileName = inFileName + "-delete";
                in = new BufferedReader(new FileReader(new File(inFileName)));
                outputUpdate = new PrintWriter(new BufferedWriter(new FileWriter(updateFileName)));
                outputDelete = new PrintWriter(new BufferedWriter(new FileWriter(deleteFileName)));
                if (in.readLine().charAt(0) == REC) {
                    System.out.println("File OK");
                }
                CpxCorrectionUpdateDeleteSeparator cpxUD = new CpxCorrectionUpdateDeleteSeparator();
                cpxUD.readRecords(in);
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            } finally {
                if (in != null) {
                    in.close();
                }
                if (outputUpdate != null) {
                    outputUpdate.close();
                }
                if (outputDelete != null) {
                    outputDelete.close();
                }
            }
        } else {
            System.out.println("Please enter a parameter");
            System.exit(1);
        }

    }

    public void readRecords(BufferedReader in) throws Exception {
        String line = null;
        String fieldName = null;
        boolean moreHSFlag = false;
        ArrayList<String> record = new ArrayList<String>();
        char deleteFlag = '0';

        while ((line = in.readLine()) != null) {
            if (line.length() < 1) {
                continue;
            } else if (line.charAt(0) == REC) {
                doSeparate(record);
                record = new ArrayList<String>();
                moreHSFlag = false;
            }

            if (line.length() > 2) {
                fieldName = line.substring(0, 2).trim().toUpperCase();
                if (fieldName.equals(checkField)) {
                    deleteFlag = ((line.substring(3, line.length())).trim()).charAt(0);
                    if (!moreHSFlag) {
                        record.add(0, String.valueOf(deleteFlag));
                        moreHSFlag = true;
                    } else {
                        record.set(0, String.valueOf(deleteFlag));
                    }
                }
                record.add(line);

            }
        }
        doSeparate(record);
    }

    public void doSeparate(ArrayList<String> rec) throws Exception {
        String deleteFlag;
        boolean type = false;

        deleteFlag = (String) rec.get(0);
        if (deleteFlag.equals(DELFLAG)) {
            type = true;
        }

        if (type) {
            outputDelete.println(REC);
        } else {
            outputUpdate.println(REC);
        }

        for (int i = 1; i < rec.size(); i++) {
            String value = (String) rec.get(i);

            if (type) {
                outputDelete.println(value);
            } else {
                outputUpdate.println(value);
            }

        }
    }
}
