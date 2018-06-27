package org.ei.common;


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
    public static final Constants LA = new Constants("LA");
    public static final Constants AFF = new Constants("AFF");
    public static final Constants CO = new Constants("CO");
	public static final String AUDELIMITER = new String(new char[] {30});
	public static final String IDDELIMITER = new String(new char[] {31});
	public static final String GROUPDELIMITER = new String(new char[] {29});
	public static final String REFERENCEDELIMITER = new String(new char[] {28});
	public static String GRF_TEXT_COPYRIGHT = "GeoRef, Copyright 2014, American Geological Institute.";
	public static String GRF_HTML_COPYRIGHT = "GeoRef, Copyright &copy; 2014, American Geological Institute.";
	public static String PROVIDER_TEXT = "American Geological Institute";
	public static final int MINIMUM_ABSTRACT_LENGTH = 100;


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
