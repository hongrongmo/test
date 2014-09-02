/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/product/Product.java-arc   1.1   Apr 16 2009 11:23:54   johna  $
 * $Revision:   1.1  $
 * $Date:   Apr 16 2009 11:23:54  $
 *
 */
package org.ei.struts.backoffice.product;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Product {

  public static final Product PROD_EV = new Product();
  static {
    PROD_EV.setProductID("4000");
    PROD_EV.setName("EngVillage2");
    PROD_EV.setDescription("EngVillage2");
  }

  private static Log log = LogFactory.getLog("Product");

	public Product() {
	}

    private String m_strProductID = null;
    private String m_strName = null;
	private String m_strDescription = null;

    // ----------------------------------------------------------- Properties

    public String getProductID() { return m_strProductID; }
    public void setProductID(String entitlementid) { m_strProductID = entitlementid; }

    public String getName() { return m_strName; }
    public void setName(String name) { m_strName = name; }

    public String getDescription() { return m_strDescription; }
    public void setDescription(String description) { m_strDescription = description; }


	public String toString() {
		StringBuffer strBufObjectValue = new StringBuffer();

	    strBufObjectValue.append(m_strProductID);
	    strBufObjectValue.append(m_strName);
	    strBufObjectValue.append(m_strDescription);

		return strBufObjectValue.toString();
	}
}