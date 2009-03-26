/*
 * Created on May 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.bulletins;

import java.util.*;

import org.apache.oro.text.perl.MalformedPerl5PatternException;
import org.apache.oro.text.perl.Perl5Util;
import org.ei.gui.ListBox;

/**
 * @author KFokuo
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BulletinGUI
{

    private static Properties mappings = new Properties();

    static {

        mappings.put("A", "Automotive;automotive");
        mappings.put("CZP", "Catalysts/Zeolites;catalysys-zeolites:catalysts-zeolites");
        mappings.put("CZL", "Catalysts/Zeolites;catalysys-zeolites:catalysts-zeolites");
        mappings.put("CP", "Chemical Products;chemical_products");
        mappings.put("ETS", "Environment, Transport and Storage;environment_transport_storage");
        mappings.put("FRP", "Fuel Reformulation;fuel_reformation:fuel_reformulation");
        mappings.put("FRL", "Fuel Reformulation;fuel_reformation:fuel_reformulation");
        mappings.put("HE", "Health and Environment;health_environment");
        mappings.put("NG", "Natural Gas;natural_gas");
        mappings.put("OCP", "Oilfield Chemicals;oilfield_chemicals");
        mappings.put("OCL", "Oilfield Chemicals;oilfield_chemicals");
        mappings.put("PP", "Petroleum Processes;petroleum_processes");
        mappings.put("PRP", "Petroleum Refining and Petrochemicals ;petroleum_refining_petrochemicals");
        mappings.put("PSP", "Petroleum and Specialty Products;petroleum_speciality_products");
        mappings.put("PS_L", "Petroleum Substitutes;petroleum_substitutes");
        mappings.put("PS_P", "Petroleum Substitutes;petroleum_substitutes");
        mappings.put("POL", "Polymers;polymers");
        mappings.put("TS", "Transportation and Storage;transportation_storage");
        mappings.put("TP", "Tribology;tribology");
        mappings.put("TL", "Tribology;tribology");
    }
    /**
     * @return
     */
    public static String createCategoryLb(String cartridges, String queryString)
    {

        if (queryString != null && !queryString.trim().equals(""))
        {
            BulletinQuery query = new BulletinQuery();

            query.setQuery(queryString);

            String category = query.getCategory();
            String db = query.getDatabase();

            return buildCategoryLb(cartridges, category, db);
        }
        else
        {
            return buildCategoryLb(cartridges);
        }
    }
    public static String buildCategoryLb(String cartridges, String category, String db)
    {

        ListBox lbCategories = new ListBox();
        try
        {

            lbCategories.setName("cy");

            Enumeration keys = mappings.keys();

            List arrDisValues = new Vector();
            List arrDisOptValues = new Vector();
            StringTokenizer tokens = null;
            String newCartridges = "";

            if (db.equals("1"))
                newCartridges = getLITCartridges(cartridges);
            else
                newCartridges = getPATCartridges(cartridges);

            StringTokenizer stCartridges = new StringTokenizer(newCartridges, ";", false);

            while (stCartridges.hasMoreElements())
            {

                String cartridge = stCartridges.nextToken();

                if (cartridge != null)
                {

                    String lstBoxValue = mappings.getProperty(cartridge);

                    if (lstBoxValue != null)
                    {

                        tokens = new StringTokenizer(lstBoxValue, ";", false);

                        while (tokens.hasMoreTokens())
                        {
                            String displayValue = (String) tokens.nextToken();
                            String value = (String) tokens.nextToken();
                            arrDisValues.add(displayValue);
                            arrDisOptValues.add(value);
                        }
                    }

                }
            }

            String[] disValues = new String[arrDisValues.size()];
            String[] disOptValues = new String[arrDisOptValues.size()];

            int i = 0;

            for (Iterator iter = arrDisValues.iterator(); iter.hasNext();)
            {
                String element = (String) iter.next();
                disValues[i] = element;
                i++;

            }

            i = 0;

            for (Iterator iter = arrDisOptValues.iterator(); iter.hasNext();)
            {
                String element = (String) iter.next();
                disOptValues[i] = element;
                i++;

            }

            lbCategories.setOptions(disValues);
            lbCategories.setValues(disOptValues);

            if (category != null && !category.equals(""))
            {

                lbCategories.setDefaultChoice(category);
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        lbCategories.setJavaScript("refreshYears()");
        return lbCategories.render();

    }

    public static String createYearLb(String queryString)
    {

        if (queryString != null && !queryString.equals(""))
        {

            BulletinQuery query = new BulletinQuery();

            query.setQuery(queryString);

            String yr = query.getYr();
            String category = query.getCategory();

            return buildYearLb(yr, category);

        }
        else
        {
            return buildYearLb();
        }
    }
    public static String buildYearLb(String yr, String category)
    {

        ListBox lbYear = new ListBox();

        lbYear.setName("yr");

        if (category != null && !category.equalsIgnoreCase("polymers"))
        {

            lbYear.setOptions(new String[] {"2009","2008", "2007", "2006", "2005", "2004", "2003","2002","2001" });
            lbYear.setValues(new String[] {"2009","2008", "2007", "2006", "2005", "2004", "2003","2002", "2001" });

        }
        else
        {
            lbYear.setOptions(new String[] { "2002", "2001" });
            lbYear.setValues(new String[] { "2002", "2001" });

        }

        if (yr != null && !yr.equals(""))
            lbYear.setDefaultChoice(yr);

        return lbYear.render();

    }
    /**
     * @return
     */
    public static String createDbOpt1(String cartridges, String queryString)
    {

        if (queryString != null && !queryString.equals(""))
        {

            BulletinQuery query = new BulletinQuery();

            query.setQuery(queryString);
            String db = query.getDatabase();

            return buildDbOpt1(db, cartridges);

        }
        else
        {
            return buildDbOpt1(cartridges);
        }
    }
    public static String buildDbOpt1(String db, String cartridges)
    {

        StringBuffer sbHtml = new StringBuffer();
        String isChecked = "";

        if (cartridges.toUpperCase().indexOf("LIT_HTM") > -1 || cartridges.toUpperCase().indexOf("LIT_PDF") > -1)
        {

            if (db.equals("1"))
                isChecked = "checked";

            sbHtml.append("<td width=\"6\"><img src=\"{$RESOURCE-PATH}/i/s.gif\" width=\"4\"></img></td>");
            sbHtml.append("<td valign=\"top\">&nbsp; &nbsp;");
            sbHtml.append("<input type=\"radio\" name=\"").append("db\" onclick=\"refreshCategories()\" ").append(isChecked).append(" value=\"").append("1").append("\">");
            sbHtml.append(" <a class=\"S\">EnCompassLIT</a> &nbsp; ");
			sbHtml.append("</td>");
        }
        return sbHtml.toString();
    }
    public static String buildDbOpt1(String cartridges)
    {

        StringBuffer sbHtml = new StringBuffer();

        String isChecked = "checked";

        if (cartridges.toUpperCase().indexOf("LIT_HTM") > -1 || cartridges.toUpperCase().indexOf("LIT_PDF") > -1)
        {
            sbHtml.append("<td width=\"6\"><img src=\"{$RESOURCE-PATH}/i/s.gif\" width=\"4\"></img></td>");
            sbHtml.append("<td valign=\"top\">&nbsp; &nbsp;");
            sbHtml.append("<input type=\"radio\" name=\"").append("db\" onclick=\"refreshCategories()\" ").append(isChecked).append(" value=\"").append("1").append("\">");
            sbHtml.append(" <a class=\"S\">EnCompassLIT</a> &nbsp; ");
            sbHtml.append("</td>");
        }
        return sbHtml.toString();
    }
    /**
       * @return
       */
    public static String createDbOpt2(String cartridges, String queryString)
    {

        if (queryString != null && !queryString.equals(""))
        {

            BulletinQuery query = new BulletinQuery();

            query.setQuery(queryString);
            String db = query.getDatabase();

            return buildDbOpt2(db, cartridges);

        }
        else
        {
            return buildDbOpt2(cartridges);
        }
    }
    public static String buildDbOpt2(String db, String cartridges)
    {

        StringBuffer sbHtml = new StringBuffer();

        String isChecked = "";

        if (cartridges.toUpperCase().indexOf("PAT_HTM") > -1 || cartridges.toUpperCase().indexOf("PAT_PDF") > -1)
        {

            if (db.equals("2"))
                isChecked = "checked";
            sbHtml.append("<td width=\"6\"><img src=\"{$RESOURCE-PATH}/i/s.gif\" width=\"4\"></img></td>");
            sbHtml.append("<td valign=\"top\">&nbsp; &nbsp;");
            sbHtml.append("<input type=\"radio\" name=\"").append("db\" onclick=\"refreshCategories()\" ").append(isChecked).append(" value=\"").append("2").append("\">");
            sbHtml.append(" <a class=\"S\">EnCompassPAT</a> &nbsp; ");
			sbHtml.append("</td>");
        }
        return sbHtml.toString();
    }
    public static String buildDbOpt2(String cartridges)
    {

        StringBuffer sbHtml = new StringBuffer();

        String isChecked = "";
        if (cartridges.toUpperCase().indexOf("PAT_HTM") > -1 || cartridges.toUpperCase().indexOf("PAT_PDF") > -1)
        {
            sbHtml.append("<td width=\"6\"><img src=\"{$RESOURCE-PATH}/i/s.gif\" width=\"4\"></img></td>");
            sbHtml.append("<td valign=\"top\">&nbsp; &nbsp;");
            sbHtml.append("<input type=\"radio\" name=\"").append("db\" onclick=\"refreshCategories()\" ").append(isChecked).append(" value=\"").append("2").append("\">");
            sbHtml.append(" <a class=\"S\">EnCompassPAT</a> &nbsp; ");
			sbHtml.append("</td>");
        }
        return sbHtml.toString();
    }

    public static String buildCategoryLb(String cartridges)
    {

        ListBox lbCategories = new ListBox();
        StringTokenizer tokens = null;

        lbCategories.setName("cy");

        List arrDisOptValues = new Vector();
        List arrDisValues = new Vector();

        String newCartridges = "";

        StringTokenizer stCartridges = new StringTokenizer(cartridges, ";", false);

        while (stCartridges.hasMoreElements())
        {

            String cartridge = stCartridges.nextToken();

            if (cartridge != null)
            {

                String lstBoxValue = mappings.getProperty(cartridge);

                if (lstBoxValue != null)
                {
                    tokens = new StringTokenizer(lstBoxValue, ";", false);

                    while (tokens.hasMoreTokens())
                    {
                        String displayValue = (String) tokens.nextToken();
                        String value = (String) tokens.nextToken();
                        arrDisValues.add(displayValue);
                        arrDisOptValues.add(value);
                    }
                }
            }
        }

        String[] disValues = new String[arrDisValues.size()];
        String[] disOptValues = new String[arrDisOptValues.size()];

        int i = 0;

        for (Iterator iter = arrDisValues.iterator(); iter.hasNext();)
        {
            String element = (String) iter.next();
            disValues[i] = element;
            i++;

        }

        i = 0;

        for (Iterator iter = arrDisOptValues.iterator(); iter.hasNext();)
        {
            String element = (String) iter.next();
            disOptValues[i] = element;
            i++;

        }

        lbCategories.setOptions(disValues);
        lbCategories.setValues(disOptValues);
        lbCategories.setJavaScript("refreshYears()");

        return lbCategories.render();

    }
    /**
    		* @return
    		*/
    public static String createYearLb()
    {

        BulletinQuery query = new BulletinQuery();

        return buildYearLb();
    }
    public static String buildYearLb()
    {

        ListBox lbYear = new ListBox();

        lbYear.setName("yr");

        lbYear.setOptions(new String[] {"2009","2008", "2007", "2006", "2005", "2004", "2003","2002", "2001"});
        lbYear.setValues(new String[] {"2009","2008", "2007", "2006", "2005", "2004", "2003","2002", "2001"});
        lbYear.setDefaultChoice("2009");
        return lbYear.render();

    }
    public boolean validCartridge(String database, String currCartridge)
    {

        String validCartridges = " LIT_PDF;LIT_HTM;PAT_PDF;PAT_HTM;A;CZL;FRL;HE;NG;OCL;PRP;PS_L;TS;TL;CZP;CP;ETS;FRP;OCP;PP;PSP;PS_P;POL;TP";

        boolean isValid = false;

        try
        {
            Perl5Util perl = new Perl5Util();
            List parms = new ArrayList();

            perl.split(parms, "/;/", validCartridges);
            for (int i = 0; i < parms.size(); i++)
            {
                String sCartridge = ((String) parms.get(i)).trim();
                if (sCartridge.equalsIgnoreCase(currCartridge))
                {
                    isValid = true;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return isValid;
    }
    public boolean isLITCartridge(String cartridge)
    {

        String litCartridges = "A;CZL;FRL;HE;NG;OCL;PRP;PS_L;TS;TL";
        Perl5Util perl = new Perl5Util();

        boolean isValid = false;
        try
        {

            List parms = new ArrayList();

            perl.split(parms, "/;/", litCartridges);

            for (int i = 0; i < parms.size(); i++)
            {
                String sCartridge = ((String) parms.get(i)).trim();
                if (sCartridge.equalsIgnoreCase(cartridge))
                {
                    isValid = true;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return isValid;
    }
    public static String getLITCartridges(String cartridges)
    {

        String litCartridges[] = { "A", "CZL", "FRL", "HE", "NG", "OCL", "PRP", "PS_L", "TS", "TL" };
        Perl5Util perl = new Perl5Util();
        StringBuffer sbResult = new StringBuffer();

        try
        {

            List parms = new ArrayList();

            perl.split(parms, "/;/", cartridges);

            for (int i = 0; i < parms.size(); i++)
            {
                String sCartridge = ((String) parms.get(i)).trim();
                for (int j = 0; j < litCartridges.length; j++)
                {
                    if (litCartridges[j].equalsIgnoreCase(sCartridge))
                    {
                        sbResult.append(sCartridge);
                    }
                }

                if (i >= 0)
                    sbResult.append(";");
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sbResult.toString();
    }
    public static String getPATCartridges(String cartridges)
    {

        String patCartridges[] = { "CZP", "CP", "ETS", "FRP", "OCP", "PP", "PSP", "PS_P", "POL", "TP" };
        Perl5Util perl = new Perl5Util();
        StringBuffer sbResult = new StringBuffer();

        try
        {

            List parms = new ArrayList();

            perl.split(parms, "/;/", cartridges);

            for (int i = 0; i < parms.size(); i++)
            {
                String sCartridge = ((String) parms.get(i)).trim();
                for (int j = 0; j < patCartridges.length; j++)
                {
                    if (patCartridges[j].equalsIgnoreCase(sCartridge))
                    {
                        sbResult.append(sCartridge);
                    }
                    if (i >= 0)
                        sbResult.append(";");
                }
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sbResult.toString();
    }
    public boolean isPATCartridge(String cartridge)
    {

        String patCartridges = "CZP;CP;ETS;FRP;OCP;PP;PSP;PS_P;POL;TP";

        Perl5Util perl = new Perl5Util();

        boolean isValid = false;
        try
        {

            List parms = new ArrayList();

            perl.split(parms, "/;/", patCartridges);

            for (int i = 0; i < parms.size(); i++)
            {
                String sCartridge = ((String) parms.get(i)).trim();
                if (sCartridge.equalsIgnoreCase(cartridge))
                {
                    isValid = true;
                    break;
                }
            }
        }
        catch (MalformedPerl5PatternException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return isValid;
    }

}
