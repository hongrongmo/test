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
public class USPatents extends PatentsBase {

	public int getStartYear()
	{
		return 1959;
	}

	public USPatents()
	{
		this.NAME = OptionConstants.UPA_Label; //"US Patents";
	}
  public String getCartridge()
  {
    return OptionConstants.UPA_Cartridge;
  }
  public int getMask() { return 32768; }

	public String getCharID()
	{
		return "U";
	}



}
