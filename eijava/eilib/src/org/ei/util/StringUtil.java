package org.ei.util;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import sun.misc.BASE64Encoder;

public class StringUtil {

    public static final int REPLACE_GLOBAL = 1;
    public static final int REPLACE_FIRST = 2;
    public static final int MATCH_CASE_SENSITIVE = 3;
    public static final int MATCH_CASE_INSENSITIVE = 4;
    public static final String EMPTY_STRING = "";
    public static final String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";

    /** Format todays date with the default format */
    public static String getFormattedDate() {
        return getFormattedDate(DEFAULT_DATE_FORMAT);
    }
    /** Format today's date with a specified format */
    public static String getFormattedDate(String strPattern) {
        Calendar cal = Calendar.getInstance();
        java.util.Date dteTodaysDate = cal.getTime();
        return getFormattedDate(strPattern, dteTodaysDate);
    }
    /** Format a date with the default format */
    public static String getFormattedDate(Date dteDate) {
        return getFormattedDate(DEFAULT_DATE_FORMAT, dteDate);
    }
    public static String replaceNonAscii(String sVal) {

        if (sVal == null)
            return "";

        StringBuffer buffer = new StringBuffer();

        char[] sChar = sVal.toCharArray();

        int i = 0;

        for (int j = 0; j < sChar.length; j++) {
            i = (int) sChar[j];

            if (i >= 32 && i <= 126) {
                buffer.append(sChar[j]);
            }
            else {
                buffer.append(" ");
            }
        }
        return buffer.toString();
    }
    /** Format a date with the a sapecified format */
    public static String getFormattedDate(String strPattern, Date dteDate) {
        DateFormat dateFormat = new SimpleDateFormat(strPattern);
        String formattedDate = dateFormat.format(dteDate);
        return formattedDate;
    }

    /** Return a date parsed from a string using the default format */
    public static Date getDateFromString(String strDate) {
        return getDateFromString(DEFAULT_DATE_FORMAT, strDate);
    }
    /** Return a date parsed from a string using a specified format */
    public static Date getDateFromString(String strPattern, String strDate) {
        try {
            return (new SimpleDateFormat(strPattern)).parse(strDate);
        }
        catch (ParseException pe) {
            return null;
        }
    }

    public static String replaceSingleQuotes(String s) {
        return s.replace('\'', ' ');
    }

    public static String validate(Object obj) {
        String str = (String) obj;
        if (str == null) {
            str = "";
        }
        return str;
    }

    public static String notNull(String s) {
        if (s == null) {
            return "";
        }

        return s;
    }

    public String replace(String mainString, String replace, String replaceWith, int global, int caseTest) {
        int shift = 0;
        String mainStringTest = null;
        String replaceTest = null;
        if (caseTest == StringUtil.MATCH_CASE_INSENSITIVE) {
            mainStringTest = mainString.toUpperCase();
            replaceTest = replace.toUpperCase();
        }
        else {
            mainStringTest = mainString;
            replaceTest = replace;
        }

        while (mainStringTest.substring(shift, mainStringTest.length()).indexOf(replaceTest) >= 0) {
            String shifted = mainString.substring(0, shift);
            String workingTest = mainStringTest.substring(shift, mainStringTest.length());
            String working = mainString.substring(shift, mainString.length());
            int beginIndex = workingTest.indexOf(replaceTest);
            String part1 = working.substring(0, beginIndex);
            String part2 = working.substring(beginIndex + replace.length(), mainString.length() - shift);
            StringBuffer sb = new StringBuffer();
            sb.append(part1);
            sb.append(replaceWith);
            shift = shift + sb.length();
            sb.append(part2);
            mainString = shifted + sb.toString();
            if (global == StringUtil.REPLACE_FIRST) {
                break;
            }
            else {
                if (caseTest == StringUtil.MATCH_CASE_INSENSITIVE) {
                    mainStringTest = mainString.toUpperCase();
                }
                else {
                    mainStringTest = mainString;
                }
            }
        }
        return mainString;
    }

    public String computeUniqueHash(String hashThis) throws StringUtilException {
        BASE64Encoder en = new BASE64Encoder();
        String theHash = null;
        try {
            byte[] p = hashThis.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            theHash = en.encode(md.digest(p));

        }
        catch (Exception e) {
            throw new StringUtilException(e);
        }

        return theHash;
    }

    /**
    *	This method	takes a	clob object	and	builds a
    *	String out of it.
    *	@param clob
    *	@return	String
    *	@exception InvalidEIDocException
    **/
    public static String getStringFromClob(java.sql.Clob clob) throws Exception {
        String temp = StringUtil.EMPTY_STRING;
        if (clob != null) {
            temp = clob.getSubString(1, (int) clob.length());
        }
        return temp;
    }

    public static String join(Collection coll, String strToken) {

        StringBuffer strBuffer = new StringBuffer();

        try {
            Iterator itrColl = coll.iterator();
            while (itrColl.hasNext()) {
                strBuffer.append((String) itrColl.next());
                if (itrColl.hasNext()) {
                    strBuffer.append(strToken);
                }
            }

        }
        catch (ClassCastException cce) {

        }
        return strBuffer.toString();
    }

    public static String lowerCase(String string) {
        return string.toLowerCase();
    }

    /* This	method checks whether the String is	null or	not
     * If null it returns a	 Empty String .
     */
    public static String replaceNullWithEmptyString(String str) {
        if (str == null || str.equals("QQ")) {
            str = StringUtil.EMPTY_STRING;
        }
        return str.trim();
    }


    public static String teaser(String s)
    {
		if(s == null)
		{
			return null;
		}

		int length = s.length();
		int teaserLength = (int)(length * .65);
		return s.substring(0,teaserLength);
	}
}
