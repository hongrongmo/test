/*
 * Created on Apr 21, 2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.backoffice.customeroptions.databases;
import org.ei.struts.backoffice.customeroptions.OptionConstants;
/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GeoRef extends Database {

  public GeoRef()
  {
    this.NAME = OptionConstants.GRF_Label;
  }

  public int getMask() { return 2097152; }

  private String BACKFILE_CART = null;
  public String getBackfileCartridge()
  {
    return BACKFILE_CART;
  }
  public String getCartridge()
  {
    return OptionConstants.GRF_Cartridge;
  }
  public int getStartYear()
  {
    return 1785;
  }

  public String getCharID()
  {
    return "X";
  }

}
