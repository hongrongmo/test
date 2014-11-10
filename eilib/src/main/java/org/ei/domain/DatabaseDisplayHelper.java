package org.ei.domain;

import java.util.*;

import org.ei.domain.DatabaseConfig;
import org.ei.util.StringUtil;

public class DatabaseDisplayHelper {

    private static StringBuffer jsVars = new StringBuffer("<script language=\"javascript\">");
    private static StringBuffer jsInitVars = new StringBuffer();
    private static StringBuffer jsRedrawCell = new StringBuffer();
    private static Hashtable<String, String> maskConversion = new Hashtable<String, String>();
    private static Hashtable<String, String> dbConversion = new Hashtable<String, String>();
    private static int htmlraw = 0;
    private static boolean isTwoDb = false;

    static {
        maskConversion.put("1", "cpx");
        maskConversion.put("2", "ins");
        maskConversion.put("4", "nti");
        maskConversion.put("262144", "c84");
        maskConversion.put("64", "pch");
        maskConversion.put("128", "chm");
        maskConversion.put("256", "cbn");
        maskConversion.put("1024", "elt");
        maskConversion.put("2048", "ept");
        maskConversion.put("8192", "geo");
        maskConversion.put("16384", "eup");
        maskConversion.put("32768", "upa");
        maskConversion.put("131072", "pag");
        maskConversion.put("65536", "ref");
        maskConversion.put("1048576", "ibs");
        maskConversion.put("2097152", "grf");

        dbConversion.put("cpx", "1");
        dbConversion.put("ins", "2");
        dbConversion.put("nti", "4");
        dbConversion.put("c84", "262144");
        dbConversion.put("pch", "64");
        dbConversion.put("chm", "128");
        dbConversion.put("cbn", "256");
        dbConversion.put("elt", "1024");
        dbConversion.put("ept", "2048");
        dbConversion.put("geo", "8192");
        dbConversion.put("eup", "16384");
        dbConversion.put("pag", "131072");
        dbConversion.put("upa", "32768");
        dbConversion.put("ref", "65536");
        dbConversion.put("ibs", "1048576");
        dbConversion.put("grf", "2097152");

    }

    private static boolean setDBCount(int mask) {
        int i = 0;
        Iterator<String> itr = maskConversion.keySet().iterator();
        boolean isPatentsSet = false;
        while (itr.hasNext()) {
            String db = (String) itr.next();
            int dbmask = Integer.parseInt(db);
            if ((mask & dbmask) == dbmask) {
                if (dbmask != DatabaseConfig.EUP_MASK && dbmask != DatabaseConfig.UPA_MASK) {
                    i++;
                } else if ((dbmask == DatabaseConfig.EUP_MASK || dbmask == DatabaseConfig.UPA_MASK) && !isPatentsSet) {
                    isPatentsSet = true;
                    i++;
                }
            }
        }

        if (i < 3) {
            return true;
        }
        return false;
    }

    public static String getNewsText(int mask) {
        htmlraw = 0;
        StringBuffer result = new StringBuffer();
        String strText = StringUtil.EMPTY_STRING;
        jsVars = new StringBuffer("<script language=\"javascript\">");
        jsInitVars = new StringBuffer("\nfunction initVars(termtype)\n{");
        jsRedrawCell = new StringBuffer("\n\nfunction redrawCell(db)\n{ \n var cel ;");

        isTwoDb = setDBCount(mask);

        StringBuffer html = new StringBuffer();
        if ((mask & 8) == 8) {
            // System.out.println("Removing CRC");
            mask = mask - 8;
        }

        if ((mask & 16) == 16) {
            // System.out.println("Removing uspto");
            mask = mask - 16;
        }

        if ((mask & DatabaseConfig.CBN_MASK) == DatabaseConfig.CBN_MASK) {
            jsWriter(DatabaseConfig.CBN_MASK);
            html.append(htmlWriter(DatabaseConfig.CBN_MASK));
        }
        if ((mask & DatabaseConfig.CHM_MASK) == DatabaseConfig.CHM_MASK) {
            jsWriter(DatabaseConfig.CHM_MASK);
            html.append(htmlWriter(DatabaseConfig.CHM_MASK));
        }

        if ((mask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) {
            jsWriter(DatabaseConfig.CPX_MASK);
            html.append(htmlWriter(DatabaseConfig.CPX_MASK));
        }
        if ((mask & DatabaseConfig.CBF_MASK) == DatabaseConfig.CBF_MASK && (mask & DatabaseConfig.CPX_MASK) != DatabaseConfig.CPX_MASK) {
            jsWriter(DatabaseConfig.CBF_MASK);
            html.append(htmlWriter(DatabaseConfig.CBF_MASK));
        }

        if ((mask & DatabaseConfig.ELT_MASK) == DatabaseConfig.ELT_MASK) {
            jsWriter(DatabaseConfig.ELT_MASK);
            html.append(htmlWriter(DatabaseConfig.ELT_MASK));
        }

        if ((mask & DatabaseConfig.EPT_MASK) == DatabaseConfig.EPT_MASK) {
            jsWriter(DatabaseConfig.EPT_MASK);
            html.append(htmlWriter(DatabaseConfig.EPT_MASK));
        }

        boolean isPatentsSet = false;
        if ((mask & DatabaseConfig.EUP_MASK) == DatabaseConfig.EUP_MASK) {
            isPatentsSet = true;
            jsWriter(DatabaseConfig.EUP_MASK);
            html.append(htmlWriter(DatabaseConfig.EUP_MASK));
        }

        if (((mask & DatabaseConfig.UPA_MASK) == DatabaseConfig.UPA_MASK) && !isPatentsSet) {
            jsWriter(DatabaseConfig.UPA_MASK);
            html.append(htmlWriter(DatabaseConfig.UPA_MASK));
        }

        if ((mask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK) {
            jsWriter(DatabaseConfig.GEO_MASK);
            html.append(htmlWriter(DatabaseConfig.GEO_MASK));
        }

        if ((mask & DatabaseConfig.GRF_MASK) == DatabaseConfig.GRF_MASK) {
            jsWriter(DatabaseConfig.GRF_MASK);
            html.append(htmlWriter(DatabaseConfig.GRF_MASK));
        }

        if ((mask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
            jsWriter(DatabaseConfig.INS_MASK);
            html.append(htmlWriter(DatabaseConfig.INS_MASK));
        }
        if ((mask & DatabaseConfig.IBS_MASK) == DatabaseConfig.IBS_MASK && (mask & DatabaseConfig.INS_MASK) != DatabaseConfig.INS_MASK) {
            jsWriter(DatabaseConfig.IBS_MASK);
            html.append(htmlWriter(DatabaseConfig.IBS_MASK));
        }
        if ((mask & DatabaseConfig.NTI_MASK) == DatabaseConfig.NTI_MASK) {
            jsWriter(DatabaseConfig.NTI_MASK);
            html.append(htmlWriter(DatabaseConfig.NTI_MASK));
        }
        if ((mask & DatabaseConfig.PCH_MASK) == DatabaseConfig.PCH_MASK) {
            jsWriter(DatabaseConfig.PCH_MASK);
            html.append(htmlWriter(DatabaseConfig.PCH_MASK));
        }

        if ((mask & DatabaseConfig.PAG_MASK) == DatabaseConfig.PAG_MASK) {
            jsWriter(DatabaseConfig.PAG_MASK);
            html.append(htmlWriter(DatabaseConfig.PAG_MASK));
        }

        jsInitVars.append("}");
        jsRedrawCell.append("\n return false;}");
        jsVars.append(jsInitVars).append(jsRedrawCell).append("</script>");

        result.append(jsVars);
        result.append(html);

        return result.toString();

    }

    private static void jsWriter(int mask)

    {
        String m = String.valueOf(mask);
        jsVars.append(new jsFragment((String) maskConversion.get(m)).writeJSfragment());
        jsRedrawCell.append(new jsFragment((String) maskConversion.get(m)).writeRedrawCell());
    }

    private static StringBuffer htmlWriter(int mask) {
        String m = String.valueOf(mask);
        StringBuffer buf = new StringBuffer();
        buf = new htmlFragment((String) maskConversion.get(m)).writeHTMLfragment();
        return buf;
    }

    private static class htmlFragment {
        private String tableBody;
        private String table;
        private String div;
        private String inputField;
        private String dbDispName;
        private String text;
        private String textNoMinus;
        private String shortText;
        private String img;
        private int dbmask;

        private String terms;
        private String flag;
        private String dbcode;
        boolean isTwodb;

        private htmlFragment(String dbcode) {
            this.table = dbcode.concat("Table");
            this.tableBody = dbcode.concat("TableBody");
            this.div = dbcode.concat("Div");
            this.flag = dbcode.concat("Flag");
            this.terms = dbcode.concat("Terms");
            this.inputField = dbcode.concat("inputField");

            this.dbDispName = DbDisplayConstants.getDbname(dbcode);
            this.img = DbDisplayConstants.getImgPlus(dbcode);
            this.text = DbDisplayConstants.getDisplayText(dbcode);
            this.textNoMinus = DbDisplayConstants.getDisplayTextNoMinus(dbcode);
            this.shortText = DbDisplayConstants.getDisplayShortText(dbcode);
            this.dbcode = dbcode;
            String mask = (String) dbConversion.get(dbcode);
            this.dbmask = Integer.parseInt(mask);
            this.isTwodb = isTwoDb;
        }

        private StringBuffer writeHTMLfragment() {
            StringBuffer html = new StringBuffer();
            html.append("<tr><td valign=\"top\" width=\"14\">"
                + "<img src=\"/static/images/s.gif\" width=\"2\" height=\"3\"/></br><img src=\"/static/images/s.gif\" width=\"2\"/>");
            if (!this.isTwodb) {
                html.append(this.img);
            }
            html.append("<img src=\"/static/images/s.gif\" width=\"2\"/></td><td valign=\"top\">");

            if (!this.isTwodb) {
                html.append(this.shortText);
            } else {
                html.append(this.textNoMinus);
            }

            html.append("</td><td valign=\"top\" width=\"2\"><img src=\"/static/images/s.gif\" width=\"2\"/></td></tr>");

            return html;
        }

    }

    private static class jsFragment {
        private String tableBody;
        private String table;
        private String div;

        private String terms;
        private String flag;
        private String inputField;
        private String dbDispName;
        private String dbDispText;
        private String dbDispTextNoMinus;
        private String dbShortText;
        private String dbcode;
        private int dbmask;
        private String img;
        private String imgMinus;
        private String imgPlus;

        private jsFragment(String dbcode) {
            this.table = dbcode.concat("Table");
            this.tableBody = dbcode.concat("TableBody");
            this.div = dbcode.concat("Div");
            this.flag = dbcode.concat("Flag");
            this.terms = dbcode.concat("Terms");
            this.inputField = dbcode.concat("InputField");

            this.dbDispName = DbDisplayConstants.getDbname(dbcode);
            this.dbDispText = DbDisplayConstants.getDisplayText(dbcode);
            this.dbDispTextNoMinus = DbDisplayConstants.getDisplayTextNoMinus(dbcode);
            this.dbShortText = DbDisplayConstants.getDisplayShortText(dbcode);
            this.dbcode = dbcode;
            String mask = (String) dbConversion.get(dbcode);

            this.dbmask = Integer.parseInt(mask);

            this.img = DbDisplayConstants.getImgPlus(dbcode);

            this.imgPlus = "<img src=\"/static/images/s.gif\" width=\"2\" height=\"3\"/></br><img src=\"/static/images/s.gif\" width=\"2\"/>"
                + DbDisplayConstants.getImgPlus(dbcode) + "<img src=\"/static/images/s.gif\" width=\"2\"/>";

            this.imgMinus = "<img src=\"/static/images/s.gif\" width=\"2\" height=\"3\"/></br><img src=\"/static/images/s.gif\" width=\"2\"/>"
                + DbDisplayConstants.getImgMinus(dbcode) + "<img src=\"/static/images/s.gif\" width=\"2\"/>";

        }

        private StringBuffer writeRedrawCell() {

            StringBuffer jso = new StringBuffer("\n if(db == " + this.dbmask + ")\n {  \n if (" + this.flag + " == 1 )\n{"
                + "\n cel=document.getElementById(\'newsTable\').rows[" + htmlraw + "].cells;" +

                "\n cel[0].innerHTML = " + this.dbcode + "imgMinus" + "; \n" +

                "\n cel[1].innerHTML = " + this.dbcode + "DispText" + "; \n" +

                this.flag + " = 2; \n" + "} else if (" + this.flag + " == 2 )\n{" + "\n cel=document.getElementById(\'newsTable\').rows[" + htmlraw
                + "].cells;" +

                "\n cel[0].innerHTML = " + this.dbcode + "imgPlus" + "; \n" +

                "\n cel[1].innerHTML = " + this.dbcode + "ShortText" + "; \n" +

                this.flag + " = 1; \n" + "} \n}");

            htmlraw++;
            return jso;
        }

        private StringBuffer writeInitVars() {

            StringBuffer jsiv = new StringBuffer("\n var " + this.dbcode + "DispText" + " = \'" + this.dbDispText + "\';\n" +

            "var " + this.dbcode + "ShortText" + " = \'" + this.dbShortText + "\';\n" +

            "var " + this.dbcode + "imgPlus" + " = \'" + this.imgPlus + "\';\n" +

            "var " + this.dbcode + "imgMinus" + " = \'" + this.imgMinus + "\';\n" +

            "var " + this.dbcode + "Flag" + " = 1 ;\n"

            );

            return jsiv;
        }

        private StringBuffer writeJSfragment() {

            StringBuffer js = new StringBuffer("\n var " + this.dbcode + "DispText" + " = \'" + this.dbDispText + "\';\n" + "var " + this.dbcode + "ShortText"
                + " = \'" + this.dbShortText + "\';\n" +

                "var " + this.dbcode + "imgPlus" + " = \'" + this.imgPlus + "\';\n" +

                "var " + this.dbcode + "imgMinus" + " = \'" + this.imgMinus + "\';\n" +

                "var " + this.dbcode + "Flag" + " = 1 ;\n");
            return js;
        }

    }

    public static String getDisplayName(int mask) {

        StringBuffer buf = new StringBuffer();
        try {
            Map<?, ?> h = DriverConfig.getDriverTable();
            DatabaseConfig dconfig = DatabaseConfig.getInstance(h);
            Database[] databases = dconfig.getDatabases(mask);
            for (int i = 0; i < databases.length; i++) {
                if (i > 0) {
                    if ((databases.length - i) == 1) {
                        buf.append(" & ");
                    } else {
                        buf.append(", ");
                    }
                }

                buf.append(databases[i].getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Yes, I meant to sit on this. - Joel
        }
        return buf.toString();
    }

    public static String getIndexName(int mask) {

        StringBuffer buf = new StringBuffer();
        try {
            Map<?, ?> h = DriverConfig.getDriverTable();
            DatabaseConfig dconfig = DatabaseConfig.getInstance(h);
            Database[] databases = dconfig.getDatabases(mask);
            if (databases != null) {
                int i = 0;
                if (databases[i] != null) {
                    buf = new StringBuffer(databases[i].getIndexName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Yes, I meant to sit on this. - Joel
        }

        return buf.toString();
    }

    public static String getDisplayInits(int mask) {
        return getSearchCodesLegend(mask, false);
    }

    public static String getSearchCodesLegend(int mask) {
        return getSearchCodesLegend(mask, true);
    }

    private static String getSearchCodesLegend(int mask, boolean legend) {
        if ((legend == true) && DatabaseDisplayHelper.singleDatabase(mask)) {
            return StringUtil.EMPTY_STRING;
        } else {
            StringBuffer buf = new StringBuffer();
            try {
                Map<?, ?> h = DriverConfig.getDriverTable();
                DatabaseConfig dconfig = DatabaseConfig.getInstance(h);
                Database[] databases = dconfig.getDatabases(mask);

                for (int i = 0; i < databases.length; i++) {
                    if (legend) {
                        buf.append("<span class=\"BoldBlueText\">");
                    }
                    if ((!legend) && (i != 0)) {
                        buf.append(", ");
                    }
                    buf.append((databases[i].getLegendID()));
                    if (legend) {
                        buf.append("</span>");
                        buf.append(" ");
                        buf.append(databases[i].getName());
                        buf.append(" ");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Yes, I meant to sit on this. - Joel
            }
            return buf.toString();
        }
    }

    // only works with scrubbed mask
    public static boolean singleDatabase(int mask) {
        if ((mask == 1) || (mask == 2) || (mask == 4) || (mask == 128) || (mask == 8192) || // geobase
            (mask == 16384) || (mask == 32768)) {
            return true;
        } else {
            return false;
        }

    }

    public static String isDeDupable(int mask) {
        return String.valueOf(((mask == 3) || (mask == 7)));
    }

}
