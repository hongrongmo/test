/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/village/ev2/Ev2.java-arc   1.4   Feb 25 2009 17:08:26   johna  $
 * $Revision:   1.4  $
 * $Date:   Feb 25 2009 17:08:26  $
 *
 */
package org.ei.struts.backoffice.customeroptions.village.ev2;

import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.OptionConstants;
import org.ei.struts.backoffice.customeroptions.databases.*;
import org.ei.struts.backoffice.customeroptions.village.Village;

public final class Ev2 extends Village {

  private Log log = LogFactory.getLog(Ev2.class);

  private final String SORT_KEY = "SRT";
  private final String SELECTED_STARTYEAR_KEY = "SY";
  private final String AUTOSTEM_KEY = "STM";

  private String startCID = null;
  private String referenceServicesLink = null;
  private String defaultDatabase = null;

  private String sortBy = null;
  private String autostem = null;

  private Database[] dbs = null;

  public Ev2()
  {
    super();

    dbs = new Database[15];
    dbs[0] = new Compendex();
    dbs[1] = new Inspec();
    dbs[2] = new NTIS();
    dbs[3] = new USPatents();
    dbs[4] = new EUPatents();
    dbs[5] = new CBNB();
    dbs[6] = new GEOBASE();
    dbs[7] = new PaperChem();
    dbs[8] = new Referex();
    dbs[9] = new CompendexBackfile();
    dbs[10] = new Chimica();
    dbs[11] = new EncompassPat();
    dbs[12] = new EncompassLit();
    dbs[13] = new InspecArchive();
    dbs[14] = new GeoRef();
  }
  // used to return db from array inside indexed properties iterator
  public Object getDatabase(int index) {
    return dbs[index];
  }
    // used for indexed properties iterator
  public Collection getDatabaseArray()
  {
    return Arrays.asList(dbs);
  }


  private String[] litbulletins = new String[] {};
  public String[] getLitbulletins() {
    return (this.litbulletins);
  }
  public void setLitbulletins(String[] litbulletins) {
    this.litbulletins = litbulletins;
  }

  private String[] patbulletins = new String[] {};
  public String[] getPatbulletins() {
    return (this.patbulletins);
  }
  public void setPatbulletins(String[] patbulletins) {
    this.patbulletins = patbulletins;
  }

  // used for setting value from array of Cartridges
  public void setSortBy(String[] carts)
  {
    if(carts != null)
    {
      setSortBy(OptionConstants.getKeyValueFromCartridges(SORT_KEY, carts));
    }
  }


  // used for setting value from array of Cartridges
  public void setAutostem(String[] carts)
  {
    if(carts != null)
    {
      setAutostem(OptionConstants.getKeyValueFromCartridges(AUTOSTEM_KEY, carts));
    }
  }

  // used for setting value from array of Cartridges
  public void setDatabaseArray(String[] carts)
  {
    if(carts != null)
    {
      for(int i = 0; i < dbs.length; i++)
      {
        if(dbs[i] != null)
        {
          String strkey = dbs[i].getCharID().concat(SELECTED_STARTYEAR_KEY);
          String value = OptionConstants.getKeyValueFromCartridges(strkey, carts);

          if(dbs[i].getBackfileCartridge() != null)
          {
            if(OptionConstants.getKeyValueFromCartridges(dbs[i].getBackfileCartridge(),getSelectedOptions()) != null)
            {
              dbs[i].setBackfile(true);
            }
          }

          dbs[i].setYear(value);
        }
      }
    }
  }

  public String getProduct() {
    return Constants.EV2;
  }

  /**
   * @return
   */
  public Database[] getDbs()
  {
    return dbs;
  }
  /**
   * @param DB[]
   */
  public void setDbs(Database[] dbarray)
  {
    dbs = dbarray;
  }

  /**
   * @return
   */
  public String getStartCID() {
    return startCID;
  }

  /**
   * @param string
   */
  public void setStartCID(String string) {
    startCID = string;
  }

  /**
   * @return
   */
  public String getReferenceServicesLink() {
    return referenceServicesLink;
  }

  /**
   * @param string
   */
  public void setReferenceServicesLink(String string) {
    referenceServicesLink = string;
  }

  /**
   * @return
   */
  public void setDefaultDatabase(String dbmask) {
    defaultDatabase = dbmask;

    // this turns mask into array of separate mask values
    // so the individual checkboxes can be set on the Ev2 form
    // each default DB checkbox value is the mask value

    List dblist = new ArrayList();

    if(dbmask != null)
    {
        int mask = Integer.parseInt(dbmask);
        for(int i = 0; mask != 0; i++)
        {
            int thisbit = (int) Math.pow(2,i);

            if((mask & thisbit) == thisbit)
            {
                dblist.add(Integer.toString(thisbit));
                mask &= ~thisbit;
            }
        }
    }

    // set array of masks for displaying default database checkboxes on options form
    setSelectedDefaultDatabases((String[]) dblist.toArray(new String[0]));
  }

  public String getDefaultDatabase() {

    int sum = 0;
    String strsum = null;
    for(int i = 0; i < getSelectedDefaultDatabases().length; i++)
    {
      sum += Integer.parseInt(getSelectedDefaultDatabases()[i]);
    }

    if(sum > 0)
    {
      strsum = Integer.toString(sum);
    }

    return strsum;
  }

  /**
   * @return
   */
  public String getSortBy()
  {
    return sortBy;
  }

  /**
   * @param string
   */
  public void setSortBy(String string)
  {
    sortBy = string;
  }

  /**
   * @return
   */
  public String getAutostem()
  {
      return autostem;
  }

  /**
   * @param string
   */
  public void setAutostem(String string)
  {
    autostem = string;
  }

  public String joinOptions()
  {
    StringBuffer opts = new StringBuffer();
    opts.append(super.joinOptions());

    for(int i = 0; i < dbs.length; i++)
    {
      if(dbs[i] != null)
      {
        if((dbs[i].getYear() != null )&& !dbs[i].getYear().equals(Constants.EMPTY_STRING))
        {
          opts.append(OPTION_SEPARATOR);
          opts.append(dbs[i].getCharID());
          opts.append(SELECTED_STARTYEAR_KEY);
          opts.append(dbs[i].getYear());
        }
      }
    }

    if((getSortBy() != null) && !getSortBy().equals(Constants.EMPTY_STRING))
    {
      opts.append(OPTION_SEPARATOR);
      opts.append(SORT_KEY);
      opts.append(getSortBy());
    }

    if((getAutostem() != null) && !getAutostem().equals(Constants.EMPTY_STRING))
    {
      opts.append(OPTION_SEPARATOR);
      opts.append(AUTOSTEM_KEY);
      opts.append(getAutostem());
    }

    String[] strArray = getLitbulletins();
    if(strArray != null && strArray.length > 0)
    {
      if(opts.length() != 0)
      {
        opts.append(OPTION_SEPARATOR);
      }
      for(int x = 0; x < strArray.length; x++) {
        if(x > 0) {
          opts.append(OPTION_SEPARATOR);
        }
        opts.append(strArray[x]);
      }
    }

    strArray = getPatbulletins();
    if(strArray != null && strArray.length > 0)
    {
      if(opts.length() != 0)
      {
        opts.append(OPTION_SEPARATOR);
      }
      for(int x = 0; x < strArray.length; x++) {
        if(x > 0) {
          opts.append(OPTION_SEPARATOR);
        }
        opts.append(strArray[x]);
      }
    }

    return opts.toString();
  }

  public String toString() {
    StringBuffer strBufObjectValue = new StringBuffer();
    strBufObjectValue.append(super.toString());
    strBufObjectValue.append(", Lit Bulletins: ");
    strBufObjectValue.append(Arrays.asList(getLitbulletins()));
    strBufObjectValue.append(", Pat Bulletins: ");
    strBufObjectValue.append(Arrays.asList(getPatbulletins()));
    return strBufObjectValue.toString();
  }

}