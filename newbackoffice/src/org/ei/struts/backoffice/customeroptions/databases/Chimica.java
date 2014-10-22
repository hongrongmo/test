/*
 * Created on Nov 23, 2004
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
public class Chimica extends Database {

	public Chimica()
	{
		this.NAME = OptionConstants.CHM_Label; //"Chimica";
	}

    public int getMask() { return 128; }

	private String BACKFILE_CART = null;
	public String getBackfileCartridge()
	{
		return BACKFILE_CART;
	}
  public String getCartridge()
  {
    return OptionConstants.CHM_Cartridge;
  }

	public int getStartYear()
	{
		return 1970;
	}

	public String getCharID()
	{
		return "H";
	}

}
