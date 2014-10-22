/*
 * Created on Nov 23, 2004
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
public class Compendex extends Database {

	public Compendex()
	{
		this.NAME = OptionConstants.CPX_Label;
	}

  public int getMask() { return 1; }

	private String BACKFILE_CART = OptionConstants.C84_Cartridge;
  public String getCartridge()
  {
    return OptionConstants.CPX_Cartridge;
  }
	public String getBackfileCartridge()
	{
		return BACKFILE_CART;
	}

	public int getStartYear()
	{
		if(isBackfile())
		{
			return 1884;
		}
		else
		{
			return 1969;
		}
	}

	public String getCharID()
	{
		return "C";
	}

}
