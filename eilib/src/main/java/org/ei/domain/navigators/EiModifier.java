/*
 * Created on Aug 11, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.ei.domain.DatabaseConfig;
import org.ei.util.StringUtil;

/**
 * @author JMoschet
 * 
 *         To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EiModifier implements Comparable<Object> {
    // protected static Log log = LogFactory.getLog(EiModifier.class);
    public static final String MODS_DELIM = "QqQ";
    public static final String MOD_DELIM = "~";

    // NOTE: This is an immutable class!!
    private final int count;
    private final String value;
    private final String label;

    public static final EiModifier US_GRANTS = new EiModifier(0, "US Grants", "ug");
    public static final EiModifier US_APPLICATIONS = new EiModifier(0, "US Applications", "ua");
    public static final EiModifier EU_GRANTS = new EiModifier(0, "European  Grants", "eg");
    public static final EiModifier EU_APPLICATIONS = new EiModifier(0, "European  Applications", "ea");
    public static final EiModifier PATENT = new EiModifier(0, "Patent", "pa");

    public static final EiModifier DT_BOOK = new EiModifier(0, "BOOK", "book");
    public static final EiModifier DT_PAGE = new EiModifier(0, "PAGE", "page");

    public static final EiModifier MOD_CPX = new EiModifier(0, "Compendex", Integer.toString(DatabaseConfig.CPX_MASK));
    public static final EiModifier MOD_INS = new EiModifier(0, "Inspec", Integer.toString(DatabaseConfig.INS_MASK));
    public static final EiModifier MOD_NTI = new EiModifier(0, "NTIS", Integer.toString(DatabaseConfig.NTI_MASK));
    public static final EiModifier MOD_UPA = new EiModifier(0, "US Patents", Integer.toString(DatabaseConfig.UPA_MASK));
    public static final EiModifier MOD_EUP = new EiModifier(0, "EP Patents", Integer.toString(DatabaseConfig.EUP_MASK));
    public static final EiModifier MOD_GEO = new EiModifier(0, "Geobase", Integer.toString(DatabaseConfig.GEO_MASK));
    public static final EiModifier MOD_PAG = new EiModifier(0, "Referex", Integer.toString(DatabaseConfig.PAG_MASK));
    public static final EiModifier MOD_CBF = new EiModifier(0, "Ei Backfile", Integer.toString(DatabaseConfig.CBF_MASK));
    public static final EiModifier MOD_IBS = new EiModifier(0, "Inspec Archive", Integer.toString(DatabaseConfig.IBS_MASK));
    public static final EiModifier MOD_GRF = new EiModifier(0, "Georef", Integer.toString(DatabaseConfig.GRF_MASK));

    public static final EiModifier MOD_PCH = new EiModifier(0, "PaperChem", Integer.toString(DatabaseConfig.PCH_MASK));
    public static final EiModifier MOD_CBN = new EiModifier(0, "CBNB", Integer.toString(DatabaseConfig.CBN_MASK));
    public static final EiModifier MOD_CHM = new EiModifier(0, "Chimica", Integer.toString(DatabaseConfig.CHM_MASK));
    public static final EiModifier MOD_EPT = new EiModifier(0, "EnCompassPat", Integer.toString(DatabaseConfig.EPT_MASK));
    public static final EiModifier MOD_ELT = new EiModifier(0, "EnCompassLit", Integer.toString(DatabaseConfig.ELT_MASK));

    public static final EiModifier[] DEDUPABLE_MODS = { MOD_CHM, MOD_CPX, MOD_ELT, MOD_GEO, MOD_GRF, MOD_INS, MOD_PCH };

    public EiModifier(int i, String slable, String svalue) {
        count = i;
        label = slable;
        value = svalue;
    }

    /**
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return
     */
    public String getValue() {
        return value;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(String.valueOf(this.getCount())).append(MOD_DELIM).append(this.getValue()).append(MOD_DELIM).append(this.getLabel());

        return sb.toString();
    }

    public String toCSV() {
        StringBuffer sb = new StringBuffer();

        sb.append(this.getLabel());
        sb.append("\t");
        sb.append(this.getCount());
        sb.append("\n");

        return sb.toString();
    }

    public String toXML() {
        StringBuffer sb = new StringBuffer();

        sb.append("<MODIFIER COUNT=\"").append(this.getCount()).append("\">");

        if (!StringUtil.EMPTY_STRING.equals(this.getValue())) {
            sb.append("<VALUE><![CDATA[").append(this.getValue()).append("]]></VALUE>");
        }

        sb.append("<LABEL><![CDATA[").append(this.getLabel()).append("]]></LABEL>").append("</MODIFIER>");

        return sb.toString();
    }

    /**
     * Return a json representation of a modifier. {count,value,label}
     * 
     * @return
     * @throws JSONException
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject mod = new JSONObject();
        mod.put("count", this.getCount());

        if (!StringUtil.EMPTY_STRING.equals(this.getValue())) {
            mod.put("value", this.getValue());

        }
        mod.put("label", this.getLabel());

        return mod;
    }

    public static EiModifier parseModifier(String nav) {
        int modcount = 0;
        String svalue = StringUtil.EMPTY_STRING;
        String slabel = StringUtil.EMPTY_STRING;

        String[] modfields = nav.split(EiModifier.MOD_DELIM);
        if (modfields != null) {
            if (modfields.length == 3) {
                try {
                    modcount = Integer.parseInt(modfields[0]);
                } catch (NumberFormatException nfe) {
                    modcount = 0;
                }
                svalue = modfields[1];

                slabel = modfields[2];

            }
            if (modfields.length == 1) {
                modcount = 0;
                svalue = nav;
                slabel = nav;
            }
        }

        return new EiModifier(modcount, slabel, svalue);
    }

    public int compareTo(Object anobject) {
        EiModifier eimod = (EiModifier) anobject;

        if (this.getLabel().equalsIgnoreCase(eimod.getLabel())) {
            return 0;
        } else {
            if (this.getValue().equalsIgnoreCase(eimod.getValue())) {
                return 0;
            }

            return 1;
        }

    }

    public boolean equals(Object eimod) {
        return this.compareTo(eimod) == 0;
    }
}
