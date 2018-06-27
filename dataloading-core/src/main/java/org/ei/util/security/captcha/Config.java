package org.ei.util.security.captcha;

import java.util.Properties;


public class Config {

    static public final String MAX_NUMBER = "6";
    static public final String LTR_WIDTH = "33";
    static public final String IMAGE_HEIGHT = "40";
    static public final String PASS_CODE = "n2f3r01-782108h-324908";
    static public final String ALGORITHM = "PBEWithMD5AndDES";
    static public final String SKEW = "1";
    static public final String DRAW_LINES = "3";
    static public final String DRAW_BOXES = "300";
    static public final String SKEW_PROCESSOR_CLASS = "org.ei.security.captcha.SkewImageProba:org.ei.security.captcha.SkewImageSimple";

    static Properties _properties = null;
    static boolean isDebugOn = true;


    public static String getProperty(String propName) {
        return propName;
    }

    public static int getPropertyInt(String propName) {
        return new Long(propName).intValue();
    }

    public static long getPropertyLong(String propName) {
        return new Long(propName).longValue();
    }

    public static double getPropertyDouble(String propName) {
        return new Double(propName).doubleValue();
    }

    public static boolean getPropertyBoolean(String propName) {
        return new Boolean(propName).booleanValue();
    }
}
