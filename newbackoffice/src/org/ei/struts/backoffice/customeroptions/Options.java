package org.ei.struts.backoffice.customeroptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.contract.Contract;
import org.ei.struts.backoffice.contract.ContractDatabase;
import org.ei.struts.backoffice.contract.item.Item;

public abstract class Options {

	private static Log log = LogFactory.getLog("Options");

	private String m_strContractID = null;
	private String m_strCustomerID = null;

	public abstract String getProduct();

	/**
	 * @return
	 */
	public String getContractID() {	return m_strContractID; }
	public void setContractID(String string) { m_strContractID = string; }

	public String getCustomerID() { return m_strCustomerID; }
	public void setCustomerID(String string) { m_strCustomerID = string; }


	public Contract getContract() {
		return (new ContractDatabase()).findContract(m_strContractID);
	}

	public Item getContractItem() {
		return getContract().getContractItem(getProduct());
	}

	/**
	 * @param string
	 */
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" Options [ product: ").append(getProduct());
		sb.append(", contract id: ").append(getContractID());
		sb.append(", customer id: ").append(getCustomerID());
		sb.append("]");
		return sb.toString();
	}

}
