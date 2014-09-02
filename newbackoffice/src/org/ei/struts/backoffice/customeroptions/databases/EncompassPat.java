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
public class EncompassPat extends Database {

	public EncompassPat()
	{
		this.NAME = OptionConstants.EPT_Label; // "EncompassPat";
	}

    public int getMask() { return 2048; }

	private String BACKFILE_CART = null;
	public String getBackfileCartridge()
	{
		return BACKFILE_CART;
	}
  public String getCartridge()
  {
    return OptionConstants.EPT_Cartridge;
  }
	public int getStartYear()
	{
		return 1963;
	}

	public String getCharID()
	{
		return "M";
	}

}
