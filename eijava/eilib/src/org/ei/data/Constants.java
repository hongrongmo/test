package org.ei.data;


public class Constants
{
    private String constant;
    public static final Constants CVS = new Constants("CVS");
    public static final Constants DT = new Constants("DT");
    public static final Constants DT_UA = new Constants("ua");
    public static final Constants DT_UG = new Constants("ug");
    public static final Constants IPC = new Constants("IP");
    public static final Constants MULTIFIELD_DELIM = new Constants(";");
    public static final Constants PN = new Constants("PN");
    public static final Constants PN_FIX = new Constants("0");
    public static final Constants US = new Constants("US");
    

    private Constants(String constant)
    {
        this.constant = constant;
    }

    public String toString()
    {
        return this.constant;
    }

    public String getValue()
    {
        return this.constant;
    }

    public boolean equals(Object o)
    {
    	Constants f = (Constants)o;

        if(f.constant.equals(this.constant))
        {
            return true;
        }
        return false;
    }
}
