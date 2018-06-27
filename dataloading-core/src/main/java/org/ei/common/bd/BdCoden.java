package org.ei.common.bd;

import java.util.HashMap;
import java.util.Map;

public class BdCoden {
    private static Map<String, Integer> codenMap = new HashMap<String, Integer>();
    private static Map<String, String> reverseCodenMap = new HashMap<String, String>();
    private static Map<String, Integer> remainderMap = new HashMap<String, Integer>();
    private static int[] nums = { 11, 7, 5, 3, 1 };

    static {
        char letter = 'A';
        int x = (int) letter;

        for (int i = 0; i < 26; i++) {
            String a = "" + (char) (x + i);
            String b = "" + (x + i - 64);

            codenMap.put(a, new Integer(b));
            reverseCodenMap.put(b, a);
        }

        for (int i = 1; i < 10; i++) {
            String a = "" + i;
            String b = "" + (i + 26);

            codenMap.put(a, new Integer(b));
            reverseCodenMap.put(b, a);
        }

        codenMap.put("0", new Integer(36));
        reverseCodenMap.put("36", "0");

        for (int i = 27; i < 34; i++) {
            String a = "" + i;
            String b = "" + (i - 25);

            remainderMap.put(a, new Integer(b));
        }

        remainderMap.put("0", new Integer(9));

    }

    public static String convert(String fiveDigitCoden)  {
        String returnVal = null;
        if (fiveDigitCoden != null && ((fiveDigitCoden.length() > 5) || (fiveDigitCoden.indexOf(" ") != -1))) {
            return fiveDigitCoden;
        } else if (fiveDigitCoden != null) {

            Integer cint = (Integer) codenMap.get(fiveDigitCoden.substring(4, 5));
            int checkDigit = 0;

            if (fiveDigitCoden.length() < 5 || cint == null)
                return null;

            for (int i = 0; i < 5; i++) {
                Integer m = (Integer) codenMap.get(fiveDigitCoden.substring(i, i + 1));
                checkDigit += m.intValue() * nums[i];
            }

            checkDigit = checkDigit % 34;
            if (checkDigit > 0 && checkDigit < 27) {
                returnVal = "" + reverseCodenMap.get("" + checkDigit);
            } else {
                returnVal = "" + (Integer) remainderMap.get("" + checkDigit);
            }

        }
        return fiveDigitCoden + returnVal;

    }

    public static void main(String[] args) throws Exception {
        char letter = 'A';
        int x = (int) letter;
        System.out.println(BdCoden.convert("48THA"));
    }

}
