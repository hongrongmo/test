package org.ei.util;

import java.util.regex.Pattern;

public class URLPlucker {
    /* Mastering Regualr Expressions 3rd Edition, Chapter 5, page 209 */
    /* Practical Regex Techniques, Extended Examples: Building Up a Regex Through Variables in Java */

    private static String SubDomain  = "(?i:[a-z0-9]|[a-z0-9][-a-z0-9]*[a-z0-9])";
    private static String TopDomains = "(?x-i:com\\b     \n" +
                                      " |edu\\b         \n" +
                                      " |biz\\b         \n" +
                                      " |in(?:t|fo)\\b  \n" +
                                      " |mil\\b         \n" +
                                      " |net\\b         \n" +
                                      " |org\\b         \n" +
                                      " |[a-z][a-z]\\b  \n" + // country codes
                                      ")                \n";

    private static String Hostname   = "(?:" + SubDomain + "\\.)+" + TopDomains;
    private static String NOTRIN     = ";\"’<>()\\[\\]\\{\\}\\s\\x7F-\\xFF";
    private static String NOTREND    = "!.,?";
    private static String ANYWHERE   = "[ˆ" + NOTRIN + NOTREND + "]";
    private static String EMBEDDED   = "[" + NOTREND + "]";
    private static String UrlPath    = "/"+ANYWHERE + "*("+EMBEDDED+"+"+ANYWHERE+"+)*";
    private static String Url =
                "(?x:                                             \n"+
                " \\b                                             \n"+
                " ## match the hostname part                      \n"+
                " (                                               \n"+
                " (?: ftp | http s? ): // [-\\w]+(\\.\\w[-\\w]*)+ \n"+
                " |                                               \n"+
                " " + Hostname + "                                \n"+
                " )                                               \n"+
                " # allow optional port                           \n"+
                " (?: \\d+ )?                                    \n"+
                "                                                 \n"+
                " # rest of url is optional, and begins with /    \n"+
                " (?: " + UrlPath + ")?                           \n"+
                ")";

    // Now convert string we’ve built up into a real regex object
    public static final Pattern UrlRegex = Pattern.compile(Url);

    private URLPlucker() {
      // do nothing
    }
}