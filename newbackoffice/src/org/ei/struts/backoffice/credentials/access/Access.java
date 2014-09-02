/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/access/Access.java-arc   1.0   Jan 14 2008 17:10:34   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:34  $
 *
 * ====================================================================
 */

package org.ei.struts.backoffice.credentials.access;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.contract.Contract;
import org.ei.struts.backoffice.contract.ContractDatabase;
import org.ei.struts.backoffice.contract.item.Item;
import org.ei.struts.backoffice.contract.item.ItemDatabase;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:34  $
 */
public class Access {

	private static Log log = LogFactory.getLog("Access");

	private String m_strItemID = null;
	private String m_strContractID = null;

	public String getContractID() {
		return (m_strContractID == null) ? "0" : m_strContractID;
	}
	public void setContractID(String entitlementid) {
		m_strContractID = entitlementid;
	}

	public String getItemID() {
		return (m_strItemID == null) ? "0" : m_strItemID;
	}
	public void setItemID(String itemid) {
		m_strItemID = itemid;
	}

	public Contract getContract() {
		return (new ContractDatabase()).findContract(m_strContractID);
	}
	public Item getItem() {
		return (new ItemDatabase()).findItem(m_strContractID, m_strItemID);
	}

	public String toString() {
		StringBuffer strBufObjectValue = new StringBuffer();

		strBufObjectValue.append(" Contract ID:").append(m_strContractID);
		strBufObjectValue.append(getContract());
		strBufObjectValue.append(" Item ID:").append(m_strItemID);
		strBufObjectValue.append(getItem());
		return strBufObjectValue.toString();
	}

}
