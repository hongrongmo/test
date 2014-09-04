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
public class CBNB extends Database {

	public CBNB()
	{
		this.NAME = OptionConstants.CBN_Label;
	}

    public int getMask() { return 256; }

	private String BACKFILE_CART = null;
	public String getBackfileCartridge()
	{
		return BACKFILE_CART;
	}
  public String getCartridge()
  {
    return OptionConstants.CBN_Cartridge;
  }

	public int getStartYear()
	{
		return 1985;
	}

	public String getCharID()
	{
		return "B";
	}

}
