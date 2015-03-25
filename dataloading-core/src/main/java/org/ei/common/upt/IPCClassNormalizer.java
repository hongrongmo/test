package org.ei.common.upt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.util.DiskMap;

public class IPCClassNormalizer {

    static char DELIM = (char) 30;
    static Perl5Util perl = new Perl5Util();

    public static void main(String args[]) {

        String test1 = "C07C69/76";

        DiskMap diskMap = new DiskMap();

        try {

            String key = IPCClassNormalizer.normalize(test1);

            diskMap.openRead("C:\\elsevier\\univentio\\ipc", false);

            String val = diskMap.get(key);

            // System.out.println(val);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                diskMap.close();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    public static String[] trimLeadingZeroFromSubClass(String codes) {

        List<?> lstCodes = new ArrayList<Object>();

        perl.split(lstCodes, "/\\|/", codes);

        LinkedList<String> ipcVals = new LinkedList<String>();

        for (int i = 0; i < lstCodes.size(); i++) {

            String ipcCode = (String) lstCodes.get(i);

            StringBuffer buff = new StringBuffer();

            List<?> ipcSubCodes = new ArrayList<Object>();

            perl.split(ipcSubCodes, "/-/", ipcCode);

            if (ipcSubCodes.size() > 1) {
                String subCode = (String) ipcSubCodes.get(1);
                subCode = removeLeadingZeros(subCode);
                buff.append(ipcSubCodes.get(0)).append(subCode);
                ipcVals.addLast(buff.toString());
            } else {
                buff.append(ipcCode);
                ipcVals.addLast(buff.toString());
            }
        }

        String[] arrVals = (String[]) ipcVals.toArray(new String[ipcVals.size()]);

        return arrVals;
    }

    public static String removeLeadingZeros(String sVal) {

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

    public static String trimLeadingZeroFromSubClass(String code, String[] subs) {

        code = code.replaceAll(" ", "");
        if (subs == null) {
            return code;
        }

        for (int i = 0; i < subs.length; i++) {
            String sub = subs[i];
            if (sub.indexOf("0") == 0) {
                // System.out.println("Checking:" + code);
                if (code.indexOf(sub) > -1) {
                    // System.out.println("Removing zero for:" + code);
                    String subNoLeadingZero = sub.substring(1, sub.length());
                    code = code.replaceFirst(sub, subNoLeadingZero);
                    return code;
                }
            }
        }

        return code;
    }

    public static String normalize(String code)  {

        if (code == null)
            return "";

        code = perl.substitute("s/\\///ig", code);

        return code;
    }

    public static List<?> convertString2List(String sList) {

        if (sList == null)
            return new ArrayList<Object>();

        List<?> lstVals = new ArrayList<Object>();

        perl.split(lstVals, "/" + DELIM + "/", sList);

        return lstVals;
    }
}
