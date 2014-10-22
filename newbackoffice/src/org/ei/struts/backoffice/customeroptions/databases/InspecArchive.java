/*
 * Created on Jan 14, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.backoffice.customeroptions.databases;
import org.ei.struts.backoffice.customeroptions.OptionConstants;/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InspecArchive extends Database {

  public InspecArchive()
  {
    this.NAME = OptionConstants.IBS_Label; // "Inspec Archive";
  }

  public int getMask() { return 1048576; }

  public String getBackfileCartridge()
  {
    return null;
  }
  public String getCartridge()
  {
    return OptionConstants.IBS_Cartridge;
  }
  public int getStartYear()
  {
    return 1896;
  }

  public String getCharID()
  {
    return "F";
  }

}
