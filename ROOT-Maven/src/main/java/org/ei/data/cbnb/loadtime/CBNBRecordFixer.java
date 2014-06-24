package org.ei.data.cbnb.loadtime;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.oro.text.perl.Perl5Util;

public class CBNBRecordFixer {

    private Perl5Util perl = new Perl5Util();

    private String currentID;
    private String pubyear = "";

    public String fixRecord(String record) throws IOException {
        StringBuffer buf = new StringBuffer();
        ArrayList<String> al = new ArrayList<String>();
        perl.split(al, "/\t/", record.trim());
        for (int i = 0; i < al.size(); ++i) {
            if (i > 0) {
                buf.append("	");
            }

            String fs = (String) al.get(i);
            if (fs != null) {
                buf.append(fixField(i, fs));
            }
        }

        return buf.toString();
    }

    private String fixField(int index, String data) throws IOException {

        String fieldName = CBNBBaseTableRecord.baseTableFields[index];

        if (data == null && !fieldName.equals("ABS")) {
            return "";
        }
        if (fieldName.equalsIgnoreCase("ABS")) {
            if (data == null || data.trim().length() < 1) {
                return "No abstract available";
            }
        }

        if (fieldName.equalsIgnoreCase("PYR")) {
            if (data == null || data.trim().length() < 1) {
                return pubyear;
            }
        }

        if (fieldName.equalsIgnoreCase("PBD") && data != null && data.trim().length() > 1) {
            pubyear = data.substring(data.length() - 4);
            return data;
        }

        if (fieldName.equalsIgnoreCase("CDN")) {
            if (data.equalsIgnoreCase("QQQQQQ")) {
                return "";
            }

        }

        return data;
    }

}