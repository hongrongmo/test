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
public class Inspec extends Database {

	public Inspec()
	{
		this.NAME = OptionConstants.INS_Label; //"Inspec";
	}

    public int getMask() { return 2; }

	private String BACKFILE_CART = OptionConstants.IBF_Cartridge;
	public String getBackfileCartridge()
	{
		return BACKFILE_CART;
  }
  public String getCartridge()
  {
    return OptionConstants.INS_Cartridge;
  }

	public int getStartYear()
	{
		if(isBackfile())
		{
			return 1896;
		}
		else
		{
			return 1969;
		}
	}

//	public void setYear(String[] carts)
//	{
//		if(OptionConstants.getKeyValueFromCartridges(BACKFILE_CART, carts) != null)
//		{
//			setYear("1896");
//		}
//		else
//		{
//			setYear("1969");
//		}
//	}

	public String getCharID()
	{
		return "I";
	}

}
