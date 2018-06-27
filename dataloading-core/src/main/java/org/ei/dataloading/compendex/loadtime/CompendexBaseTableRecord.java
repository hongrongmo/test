package org.ei.dataloading.compendex.loadtime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CompendexBaseTableRecord {
    public static String[] baseTableFields = { "M_ID", "ID", "AN", "IG", "CN", "ISS", "LA", "AUS", "AV", "AF", "AC", "AFS", "AY", "AB", "AT", "BN", "BR", "CF",
        "CC", "CL", "CLS", "CVS", "DS", "DT", "ED", "EF", "EM", "EC", "ES", "EV", "EY", "FLS", "MT", "ME", "MD", "M1", "M2", "MC", "MS", "MV", "MY", "MH",
        "MN", "NR", "NV", "OA", "PA", "PN", "PC", "PS", "PV", "PY", "PP", "SD", "ST", "SE", "SN", "SP", "SU", "TG", "TI", "TR", "TT", "UP", "VO", "VT", "XP",
        "YN", "YR", "RN", "SH", "WT", "AM", "CAL", "SC", "SS", "SV", "SY", "PO", "VN", "VM", "VC", "VS", "VV", "VY", "VX", "EX", "LF", "UR", "YP", "ML", "ASS",
        "OD", "RT", "LOAD_NUMBER", "PI", "DO", "EN", "AR", "IT", "BX", "BE", "BV", "CP", "TY", "UPDATE_NUMBER" };

    public static String[] propReader(String mapfile) throws IOException {

        String value;
        ArrayList<String> listtag = new ArrayList<String>(10);
        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(mapfile));

            while ((line = in.readLine()) != null) {
                if (line.indexOf('=') > 0) {
                    if ((line.indexOf('=') < line.indexOf('#')) || (line.indexOf('#') == -1)) {
                        value = line.substring(0, line.indexOf('=')).trim();
                        // System.out.println(value);
                        listtag.add(value);
                    }

                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File Not Found:[" + mapfile + "]");
            System.exit(1);
        } finally {
            if (in != null)
                in.close();
        }
        return (String[]) listtag.toArray(new String[listtag.size()]);
    }
}
