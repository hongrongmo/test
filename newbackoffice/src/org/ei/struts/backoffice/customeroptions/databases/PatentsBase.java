/*
 * Created on Nov 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.ei.struts.backoffice.customeroptions.databases;


/**
 * @author JMoschet
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class PatentsBase extends Database {

	public int getStartYear()
	{
		return 1978;
	}

	private String BACKFILE_CART = null;
	public String getBackfileCartridge()
	{
		return BACKFILE_CART;
	}

}
