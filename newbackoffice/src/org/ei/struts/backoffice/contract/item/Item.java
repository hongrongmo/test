/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/item/Item.java-arc   1.4   Apr 01 2009 11:55:10   johna  $
 * $Revision:   1.4  $
 * $Date:   Apr 01 2009 11:55:10  $
 *
 */
package org.ei.struts.backoffice.contract.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.contract.Contract;
import org.ei.struts.backoffice.credentials.CredentialsDatabase;
import org.ei.struts.backoffice.customeroptions.localholdings.LocalHoldingsDatabase;
import org.ei.struts.backoffice.product.Product;
import org.ei.struts.backoffice.product.ProductDatabase;
import org.ei.struts.backoffice.contract.notes.NoteDatabase;

public final class Item {

	private static Log log = LogFactory.getLog("Item");

	private String m_strItemID = null;
	private String m_strProductID = null;
	private String m_strNotes = null;

	private Contract m_contractParent = new Contract();

	// ----------------------------------------------------------- Properties

	public String getItemID() {
		return m_strItemID;
	}
	public void setItemID(String entitlementid) {
		m_strItemID = entitlementid;
	}

	public String getProductID() {
		return m_strProductID;
	}
	public void setProductID(String productid) {
		m_strProductID = productid;
	}

	public String getNotes() {
		return m_strNotes;
	}

	public void setNotes(String notes) {
		this.m_strNotes = (notes != null) ? notes.trim() : null;
	}

	public Contract getContract() {
		return m_contractParent;
	}
	public void setContract(Contract contract) {
		this.m_contractParent = contract;
	}

	public Product getProduct() {
		return (new ProductDatabase()).findProduct(m_strProductID);
	}

	// -----------------------------------------------------------

  public Collection getNoteHistory() {
    return (new NoteDatabase()).getNotes(m_contractParent.getContractID(),getItemID());
  }

	public boolean getHasOptions() {
		return Constants.getProductsWithOptions().contains(getProductID());
	}
	public boolean getHasLocalHoldings() {
		return Constants.getProductsWithLocalHoldings().contains(getProductID());
	}


	public Collection getIpAccess() {
		return
			(new CredentialsDatabase()).getIpData(
				m_contractParent.getContractID(),
				m_contractParent.getCustomerID(),
				m_strProductID);
	}
	public Collection getGatewayAccess() {
		return
			(new CredentialsDatabase()).getGatewayData(
				m_contractParent.getContractID(),
				m_contractParent.getCustomerID(),
				m_strProductID);
	}
	public Collection getUsernameAccess() {
		return
			(new CredentialsDatabase()).getUsernameData(
				m_contractParent.getContractID(),
				m_contractParent.getCustomerID(),
				m_strProductID);
	}
	public Collection getLocalHoldings() {
		return
			(new LocalHoldingsDatabase()).getAllLocalHoldings(
				m_contractParent.getContractID(),
				m_contractParent.getCustomerID(),
				this);
	}
	// -----------------------------------------------------------

	public Map getLinkParams() {

		Map params = new HashMap();
		params.put(Tokens.CUSTOMERID, getContract().getCustomerID());
		params.put(Tokens.ITEMID, getItemID());
		params.put(Tokens.CONTRACTID, getContract().getContractID());
		params.put(Tokens.PRODUCTID, getProductID());
		return params;

	}

	// -----------------------------------------------------------

	// public method used in place of implementing 'clone'
	public static Item newInstance(Item item) {

		Item itemCopy = (new ItemDatabase()).createItem(item.getContract().getContractID());

		itemCopy.setContract(item.getContract());
		itemCopy.setProductID(item.getProductID());

		return itemCopy;
	}

	public String toString() {
		StringBuffer strBufObjectValue = new StringBuffer();

		strBufObjectValue.append(" Item [ id: ").append(m_strItemID);
		strBufObjectValue.append(", product: ").append(m_strProductID);
		strBufObjectValue.append(", Contract: ").append(getContract());
		strBufObjectValue.append(", notes: ").append(m_strNotes);
		strBufObjectValue.append(" ]");

		return strBufObjectValue.toString();
	}

}
