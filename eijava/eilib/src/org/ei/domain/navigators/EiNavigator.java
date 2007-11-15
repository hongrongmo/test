/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.domain.navigators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.domain.DatabaseConfig;
import org.ei.parser.base.Field;
import org.ei.xml.*;
import org.ei.util.StringUtil;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EiNavigator
{
	protected static Log log = LogFactory.getLog(EiNavigator.class);


  private static final int MODIFIER_LIMIT = 64;
  private static final int NAV_STRING_LIMIT = 6500;

    // since patents release - database is now a navigator
  public static final String DB = "dbnav";

  public static final String PN = "pnnav";
  public static final String FL = "flnav";

  public static final String YR = "yrnav";
  public static final String CV = "cvnav";
  public static final String CL = "clnav";
  public static final String ST = "stnav";
  public static final String AU = "aunav";
  public static final String AF = "afnav";
  public static final String DT = "dtnav";
  public static final String LA = "lanav";
  public static final String CO = "conav";
  //upt
  public static final String PK = "pknav";
  public static final String PAC = "pacnav";
  public static final String PCI = "pcinav";
  public static final String PEC = "pecnav";
  public static final String PID = "pidnav";
  public static final String PUC = "pucnav";

  public static final String KY = "kynav";
  public static final String BKT = "bktnav";
  public static final String BKS = "bksnav";


  // for search within search
  public static final String ALL = "all";


  protected static Map displayNames = new HashMap();
    static {
        displayNames.put(EiNavigator.DB,"Database");
        displayNames.put(EiNavigator.YR,"Year");
        displayNames.put(EiNavigator.CV,"Controlled Vocabulary");
        displayNames.put(EiNavigator.CL,"Classification Code");
        displayNames.put(EiNavigator.FL,"Species Terms");
        displayNames.put(EiNavigator.ST,"Serial Title");
        displayNames.put(EiNavigator.PN,"Publisher");
        displayNames.put(EiNavigator.AU,"Author");
        displayNames.put(EiNavigator.AF,"Author Affiliation");
        displayNames.put(EiNavigator.DT,"Document Type");
        displayNames.put(EiNavigator.LA,"Language");
        displayNames.put(EiNavigator.CO,"Country");
        displayNames.put(EiNavigator.PK,"Patent Kind");
        displayNames.put(EiNavigator.PAC,"Patent Authority Code");
        displayNames.put(EiNavigator.PCI,"Patent - Cited by Index");
        displayNames.put(EiNavigator.PEC,"ECLA Code");
        displayNames.put(EiNavigator.PID,"IPC Code");
        displayNames.put(EiNavigator.PUC,"US Classification");
        displayNames.put(EiNavigator.KY,"Keyword");
        displayNames.put(EiNavigator.BKT,"Book Title");
        displayNames.put(EiNavigator.BKS,"Book Section");

   }

    // The default order of appearance is driven off the order of this list.
    // Commenting a navigator here removes it from the default set
    // This is the master list which is used to determine list of active navigators.
    private static List navigatorNames = new ArrayList();
    static
    {
        navigatorNames.add(EiNavigator.DB);
        navigatorNames.add(EiNavigator.AU);
        navigatorNames.add(EiNavigator.AF);
        navigatorNames.add(EiNavigator.CV);
        navigatorNames.add(EiNavigator.CL);
        navigatorNames.add(EiNavigator.CO);
        navigatorNames.add(EiNavigator.DT);
        navigatorNames.add(EiNavigator.LA);
        navigatorNames.add(EiNavigator.YR);
        navigatorNames.add(EiNavigator.PUC);
        navigatorNames.add(EiNavigator.PEC);
        navigatorNames.add(EiNavigator.PID);
        navigatorNames.add(EiNavigator.PK);
        navigatorNames.add(EiNavigator.PAC);
        navigatorNames.add(EiNavigator.FL);
        navigatorNames.add(EiNavigator.ST);
        navigatorNames.add(EiNavigator.PN);
        navigatorNames.add(EiNavigator.KY);
        navigatorNames.add(EiNavigator.BKT);
        navigatorNames.add(EiNavigator.BKS);
    }
    public static List getNavigatorNames() { return navigatorNames; }

    // Navigator masks are no longer used and have been replaced by logic coded into preprocessing method in FastClient()

    protected static Map fieldNames = new HashMap();
    static {
        fieldNames.put(EiNavigator.DB,"db");
        fieldNames.put(EiNavigator.YR,"yr");
        fieldNames.put(EiNavigator.CV,"cv");
        fieldNames.put(EiNavigator.CL,"cl");
        fieldNames.put(EiNavigator.FL,"fl");
        fieldNames.put(EiNavigator.ST,"st");
        fieldNames.put(EiNavigator.PN,"pn");
        fieldNames.put(EiNavigator.AU,"au");
        fieldNames.put(EiNavigator.AF,"af");
        fieldNames.put(EiNavigator.DT,"dt");
        fieldNames.put(EiNavigator.LA,"la");
        fieldNames.put(EiNavigator.CO,"co");

        fieldNames.put(EiNavigator.PK,"pk");
        fieldNames.put(EiNavigator.PEC,"pec");
        fieldNames.put(EiNavigator.PAC,"pac");
        fieldNames.put(EiNavigator.PUC,"puc");
        fieldNames.put(EiNavigator.PCI,"pci");
        fieldNames.put(EiNavigator.PID,"pid");

        fieldNames.put(EiNavigator.KY,"ky");
        fieldNames.put(EiNavigator.BKT,"bkt");
        fieldNames.put(EiNavigator.BKS,"bks");
    }

    private String name = StringUtil.EMPTY_STRING;
    private String displayname = StringUtil.EMPTY_STRING;
    private String fieldname = StringUtil.EMPTY_STRING;
    private List modifiers = new ArrayList();
    private boolean includeall = true;

    public void setIncludeExcludeAll(String includeorexclude)
    {
      setIncludeExcludeAll("true".equalsIgnoreCase(includeorexclude));
    }

    public void setIncludeExcludeAll(boolean includeorexclude)
    {
      includeall = includeorexclude;
    }

    public boolean getIncludeExcludeAll()
    {
      return includeall;
    }

    public static EiNavigator createNavigator(Field field)
    {
      return EiNavigator.createNavigator(field.getValue().toLowerCase().trim() + "nav");
    }

    public static EiNavigator createNavigator(String name)
    {
      EiNavigator ei = null;
      if((YR.equalsIgnoreCase(name)) || (YR.substring(0,2).equalsIgnoreCase(name)))
      {
        ei = new YearNavigator();
      }
      else if(ALL.equalsIgnoreCase(name))
      {
        ei = new AllNavigator();
      }
      else if(DB.equalsIgnoreCase(name))
      {
        ei = new DBNavigator(name);
      }
      else if(PUC.equalsIgnoreCase(name))
      {
        ei = new PUCNavigator(name);
      }
      else if(PEC.equalsIgnoreCase(name))
      {
        ei = new PECNavigator(name);
      }
      else if(PID.equalsIgnoreCase(name))
      {
        ei = new PIDNavigator(name);
      }
      else if(BKT.equalsIgnoreCase(name) || BKS.equalsIgnoreCase(name))
      {
        ei = new BookNavigator(name);
      }
      else
      {
        ei = new EiNavigator(name);
      }
      return ei;
    }

    public EiNavigator(String navname)
    {
      if(navname != null)
      {
        this.name = navname;
        setFieldname((String) fieldNames.get(navname));
        setDisplayname((String) displayNames.get(navname));
      }
    }


    public void addModifiers(EiModifier mods)
    {
      this.modifiers.add(mods);
    }

    public List getModifiers()
    {
      return modifiers;
    }

    public void setModifiers(List mods)
    {
      this.modifiers = mods;
    }
    /**
     * @return
     */
    public String getDisplayname()
    {
      return displayname;
    }

    /**
     * @return
     */
    public String getFieldname()
    {
      return fieldname.replaceAll("clean", StringUtil.EMPTY_STRING);
    }

    /**
     * @param string
     */
    public void setDisplayname(String string)
    {
      displayname = string;
    }

    /**
     * @param string
     */
    public void setFieldname(String string)
    {
      fieldname = string;
    }

    /**
     * @return
     */
    public String getName()
    {
      return name;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
      name = string;
    }

    public EiModifier createModifier(int i, String slable, String svalue)
    {
      return new EiModifier(i, slable, svalue);
    }

    public static final String NAVS_DELIM = "<>";
    public static final String NAV_DELIM = "@";

    public String toXML(int modifiercount)
    {
      StringBuffer sb = new StringBuffer();
      sb.append("<NAVIGATOR ")
        .append(" NAME=\"").append(this.getName()).append("\"")
        .append(" LABEL=\"").append(this.getDisplayname()).append("\"");

      sb.append(" INCLUDEALL=\"").append(this.getIncludeExcludeAll() ? "plus" : "minus").append("\"");
      sb.append(" FIELD=\"").append(this.getFieldname()).append("\">");

      Iterator itrmods = (this.getModifiers()).iterator();
      for(int mindex = 0;itrmods.hasNext();mindex++)
      {
        if(mindex == modifiercount) { break; }

        EiModifier modifier = (EiModifier) itrmods.next();
        if(modifier != null)
        {
          sb.append(modifier.toXML());
        }
      }
      sb.append(ResultsState.getPagers(getModifiers().size(), modifiercount,this.getFieldname()));
      sb.append("</NAVIGATOR>");
      return sb.toString();
    }

    public String toXML()
    {
      return toXML(getModifiers().size());
    }


    public String toCSV()
    {
      StringBuffer sb = new StringBuffer();

      Iterator itrmods = (this.getModifiers()).iterator();
      while(itrmods.hasNext())
      {
        EiModifier modifier = (EiModifier) itrmods.next();
        if(modifier != null)
        {
          sb.append(modifier.toCSV());
        }
      }

      return sb.toString();
    }

    public String toString()
    {
      return toString(EiNavigator.MODIFIER_LIMIT);
    }

    public String toString(int showlimit)
    {
      StringBuffer sb = new StringBuffer();
      sb.append((String) this.getName())
        .append(NAV_DELIM)
        .append((String) this.getDisplayname())
        .append(NAV_DELIM)
        .append((String) this.getFieldname())
        .append(NAV_DELIM)
        .append((String) String.valueOf(this.getIncludeExcludeAll()))
        .append(NAV_DELIM);

        // jam 5/24/2005 - Bug Fix added if()/else
        // add a Mods Delimiter to Navigator with empty Modifiers list
        // gets parsed correctly when pulled from cache later on
        // (see parseNavigator())
      if(this.getModifiers().size() >= 1)
      {
        Iterator itrmods = (this.getModifiers()).iterator();
        for(int limit = 0; itrmods.hasNext(); limit++)
        {
          if(limit >= showlimit)
          {
            break;
          }
          EiModifier modifier = (EiModifier) itrmods.next();
          if(modifier != null)
          {
            String modstring = modifier.toString().concat(EiModifier.MODS_DELIM);
            if((sb.length() + modstring.length()) > EiNavigator.NAV_STRING_LIMIT)
            {
              break;
            }
            sb.append(modstring);
          }
        }
      }
      else
      {
        sb.append(EiModifier.MODS_DELIM);
      }
      return sb.toString();
    }

    public static EiNavigator parseNavigator(String nav)
    {
      EiNavigator navigator = null;

      String[] navfields = nav.split(EiNavigator.NAV_DELIM);


      // Previous method toString() added empty Modifier list
      // for Navigators with no modifiers (i.e DB sometimes)
      // to ensure proper number of strings from split call
      if(navfields.length == 5)
      {
        navigator = EiNavigator.createNavigator(navfields[0]);
        navigator.setDisplayname(navfields[1]);
        navigator.setFieldname(navfields[2]);
        navigator.setIncludeExcludeAll(navfields[3]);
        navigator.setModifiers(navigator.parseModifiers(navfields[4]));
      }
      else if(navfields.length == 4)
      {
        navigator = EiNavigator.createNavigator(navfields[0]);
        navigator.setDisplayname(navfields[1]);
        navigator.setFieldname(navfields[2]);
        navigator.setModifiers(navigator.parseModifiers(navfields[3]));
      }

      if((navigator != null) && ((navigator.getName().equals(EiNavigator.BKS)) || (navigator.getName().equals(EiNavigator.BKT))))
      {
        navigator = BookNavigator.createBookNavigator(navigator);
      }
      return navigator;
    }

    public List parseModifiers(String modsstring)
    {
      List modifiers = new ArrayList();

      String[] mods = modsstring.split(EiModifier.MODS_DELIM);
      for(int i=0; i < mods.length; i++)
      {
          modifiers.add(EiModifier.parseModifier(mods[i]));
      }

      return modifiers;
    }

    // These are used to create modifiers from breadcrumb navigation
    public void setModifierValue(String mod)
    {
      setModifierValues(new String[] {mod});
    }

    public void setModifierValues(String[] mods)
    {
      for(int i = 0; i < mods.length; i++)
      {
        EiModifier mod = EiModifier.parseModifier(Entity.prepareString(mods[i]));
        modifiers.add(mod);
      }
    }

    public String getQueryString()
    {
      StringBuffer sb = new StringBuffer();
      int modsize = this.getModifiers().size();

      if(modsize > 1)
      {
        sb.append("(");
      }

      Iterator itrmods = (this.getModifiers()).iterator();
      while(itrmods.hasNext())
      {
        EiModifier modifier = (EiModifier) itrmods.next();
        if(modifier != null)
        {
          String svalue = (String) modifier.getValue();
          svalue = svalue.replaceAll("[\"\\^\\$]", StringUtil.EMPTY_STRING);
          sb.append("{");
          sb.append(svalue);
          sb.append("}");
          if(itrmods.hasNext())
          {
            sb.append(" OR ");
          }
        }
      }
      if(modsize > 1)
      {
        sb.append(")");
      }
      sb.append(" WN ");
      sb.append(fieldname.toUpperCase());

      return sb.toString();
    }

}
