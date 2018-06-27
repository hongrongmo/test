package org.ei.common.upt;



public class USPTOClassNormalizer
{

    public static void main(String args[])
    {
        String test1 = "002/003.000";
        String test2 = "2/030.10";
        String test3 = "510/3";
        String test4 = "511/2.1";

        System.out.println(normalize(test1));
        System.out.println(normalize(test2));
        System.out.println(normalize(test3));
        System.out.println(normalize(test4));
    }


    public static String normalize(String code)
    {
        StringBuffer buf = new StringBuffer();
        String cl = null;
        String subcl = null;
        String[] parts = code.split("/");
        if(parts.length == 0)
        {
            return "";
        }

        cl = normalizeClass(parts[0]);

        if(parts.length > 1)
        {
            subcl = normalizeSubClass(parts[1]);
        }

        buf.append(cl);
        if(subcl != null)
        {
            buf.append("/");
            buf.append(subcl);
        }

        return buf.toString().toUpperCase();
    }

    public static String normalizeClass(String cl)
    {
        if(cl.startsWith("D"))
            return "D"+padLeadingZeros(cl.substring(1), 2);
        return padLeadingZeros(cl, 3);
    }

    public static String normalizeSubClass(String scl)
    {
        String decimal = null;

        if(scl.indexOf(".") > -1)
        {
            String parts[] = scl.split("\\.");
            scl = stripLeadingZeros(parts[0]);
            if(parts.length > 1)
            {
                decimal = stripTrailingZeros(parts[1]);
            }
        }
        else
        {
            scl = stripLeadingZeros(scl);
        }

        StringBuffer buf = new StringBuffer();
        if(scl != null)
        {
            buf.append(scl);
        }

        if(decimal != null)
        {
            buf.append(".");
            buf.append(decimal);
        }

        return buf.toString();

    }

    public static String stripLeadingZeros(String s)
    {
        while((s.indexOf("0") == 0))
        {
            if(s.length() == 1)
            {
                return null;
            }
            s = s.substring(1,s.length());
        }

        return s;
    }

    public static String stripTrailingZeros(String s)
    {
        while(s.charAt(s.length()-1) == '0')
        {
            if(s.length() == 1)
            {
                return null;
            }

            s = s.substring(0, s.length()-1);
        }

        return s;
    }

    public static String padLeadingZeros(String s, int size)
    {
        while(s.length() < size)
        {
            s = "0"+s;
        }

        return s;
    }
}
