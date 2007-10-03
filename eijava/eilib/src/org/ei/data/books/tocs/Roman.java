package org.ei.data.books.tocs;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Roman {
    static private final String[] strRoman = { "I", "V", "X", "L", "C", "D", "M" };

    static private final int[] iArabic = { 1, 5, 10, 50, 100, 500, 1000 };

    static private HashMap roman2arabic = new HashMap();
    static {
        for (int i = 0; i < strRoman.length; ++i) {
            roman2arabic.put(strRoman[i], new Integer(iArabic[i]));
        }
    }

    static private final String[] a1000 = { "M", "MMMMM" };

    static private final String[] a100 = { "C", "D" };

    static private final String[] a10 = { "X", "L" };

    static private final String[] a1 = { "I", "V" };

    static private HashMap roman_digit = new HashMap();
    static {
        roman_digit.put(new Integer(1000), new String[] { "M", "MMMMM" });
        roman_digit.put(new Integer(100), new String[] { "C", "D" });
        roman_digit.put(new Integer(10), new String[] { "X", "L" });
        roman_digit.put(new Integer(1), new String[] { "I", "V" });
    }

    static private final int[] digs = { 1, 10, 100, 1000 };

    static private final int[] figure = { 1000, 100, 10, 1 };

    static private final String[] figs = { "IV", "XL", "CD", "MMMMMM" };

    static private final Pattern p = Pattern
            .compile(
                    "^(?:M{0,3})(?:D?C{0,3}|C[DM])(?:L?X{0,4}|X[LC])(?:V?I{0,3}|I[VX])$",
                    Pattern.CASE_INSENSITIVE);

    static public boolean isRoman(String strRoman) {
        return (strRoman != null && !strRoman.equals("") && p.matcher(strRoman).matches());
    }

    static public int arabic(String strRoman) throws IllegalArgumentException {
        if (!isRoman(strRoman)) { // should have tested this first!
            throw new IllegalArgumentException();
        }
        strRoman = strRoman.toUpperCase();
        int last_digit = 1000;
        int arabic = 0;
        for (int i = 0; i < strRoman.length(); ++i) { // for each roman
                                                        // character
            int digit = ((Integer) (roman2arabic.get(strRoman.substring(i,
                    i + 1)))).intValue();
            if (last_digit < digit) {
                arabic -= 2 * last_digit;
            }
            last_digit = digit;
            arabic += digit;
        }
        return arabic;
    }

    static public String Roman(int arg) throws IllegalArgumentException {
        if (arg <= 0 || arg >= 4000) {
            throw new IllegalArgumentException();
        }
        String strx = "";
        String strRoman = "";
        for (int i = 0; i < figure.length; ++i) {
            int dol = figure[i]; // 1000, 100, 10, 1
            int digit = arg / dol; // (integer division)
            String[] strarray = (String[]) roman_digit.get(new Integer(dol));
            String strI = strarray[0];
            String strV = strarray[1];
            if (1 <= digit && digit <= 3) {
                strRoman += x(strI, digit);
            } else if (digit == 4) {
                strRoman += strI + strV;
            } else if (digit == 5) {
                strRoman += strV;
            } else if (6 <= digit && digit <= 8) {
                strRoman += strV + x(strI, digit - 5);
            } else if (digit == 9) {
                strRoman += strI + strx;
            }
            arg -= digit * dol;
            strx = strI;
        }
        return strRoman;
    }

    static public String roman(int arg) throws IllegalArgumentException {
        String strRoman = Roman(arg); // returns all upper case
        return strRoman.toLowerCase();
    }

    static private String x(String s, int count) {
        // arguments are guaranteed valid
        String ret = "";
        for (int i = 0; i < count; ++i) {
            ret += s;
        }
        return ret;
    }
}
