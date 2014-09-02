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
public class EUPatents extends PatentsBase {

	private String NAME = OptionConstants.EUP_Label; // "EP Patents";

	public String getName()
	{
		return NAME;
	}
  public String getCartridge()
  {
    return OptionConstants.EUP_Cartridge;
  }
  public int getMask() { return 16384; }

	public String getCharID()
	{
		return "E";
	}

}
