/*
 * Created on Nov 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.backoffice.customeroptions.databases;

import java.util.Collection;

import org.ei.struts.backoffice.customeroptions.OptionConstants;

/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class Database implements Comparable {

  protected Log log = LogFactory.getLog(Database.class);

  private String year;
  private boolean backfile = false;

  protected String NAME = null;
  public String getName()
  {
    return NAME;
  }

  abstract public int getMask();
  abstract public int getStartYear();
  abstract public String getBackfileCartridge();
  abstract public String getCartridge();
  abstract public String getCharID();

  public int getYearAsInt()
  {
    try
    {
      return Integer.parseInt(getYear());
    }
    catch(NumberFormatException nfe)
    {
      return 0;
    }
  }

  public Collection getAllYearOptions()
  {
    return OptionConstants.getYearRange(getStartYear());
  }

  /**
   * @return
   */
  public boolean isBackfile() {
    return backfile;
  }

  /**
   * @param b
   */
  public void setBackfile(boolean b) {
    backfile = b;
  }

  /**
   * @return
   */
  public String getYear() {
    return year;
  }

  /**
   * @param string
   */
  public void setYear(String string) {
    year = string;
  }

    public int compareTo(Object obj)
    {
        Database d = (Database) obj;
        if(d != null)
        {
          if(getMask() < d.getMask())
          {
              return -1;
          }
          else if(getMask() > d.getMask())
          {
              return 1;
          }
          else if(getMask() == d.getMask())
          {
              return 0;
          }
          else
          {
              return 0;
          }
      }
      else
      {
        return 0;
      }
    }
}
