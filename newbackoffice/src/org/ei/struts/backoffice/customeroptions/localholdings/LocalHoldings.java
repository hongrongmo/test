/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/localholdings/LocalHoldings.java-arc   1.0   Jan 14 2008 17:10:52   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:52  $
 *
 */
package org.ei.struts.backoffice.customeroptions.localholdings;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.contract.item.Item;

public final class LocalHoldings {

	private static Log log = LogFactory.getLog("LocalHoldings");

	public LocalHoldings() {
	}

	private Item m_item = new Item();

    private String indexid = null;
    private String customerid = null;

    private String linkLabel = null;
    private String dynamicUrl = null;
    private String defaultUrl = null;
	private String futureUrl = null;

	public Item getItem() { return m_item; }
	public void setItem(Item item) { m_item = item; }

    public String getLocalHoldingsID(){return indexid;}
    public void setLocalHoldingsID(String sid){this.indexid = sid;}

    public String getLinkLabel() { return linkLabel; }
    public void setLinkLabel(String label) { this.linkLabel = label; }

    public String getDynamicUrl(){return dynamicUrl;}
    public void setDynamicUrl (String dynamicurl){this.dynamicUrl = ((dynamicurl != null) ? dynamicurl.trim() : null);}

    public String getDefaultUrl(){return defaultUrl;}
    public void setDefaultUrl(String defUrl){this.defaultUrl = ((defUrl != null) ? defUrl.trim() : null);}

	public String getDisplayString() {
		
		StringBuffer sb = new StringBuffer();
		sb.append(linkLabel);

		return sb.toString();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" LocalHolding [ id:").append(this.getLocalHoldingsID());
		sb.append(", label:").append(this.getLinkLabel());
		sb.append(", dyn URL:").append(this.getDynamicUrl());
		sb.append(", def URL:").append(this.getDefaultUrl());
		sb.append(", img URL:").append(this.getFutureUrl()).append(" ]");
		
		return sb.toString();
	}
	public Map getEditLocalHoldingsParams() {

	  Map params = new HashMap();
	  params.put(Tokens.CUSTOMERID, m_item.getContract().getCustomerID());
	  params.put(Tokens.ITEMID, m_item.getItemID());
	  params.put(Tokens.CONTRACTID, m_item.getContract().getContractID());
	  params.put(Tokens.INDEXID, indexid);

	  return params;

	}

	//	public method used in place of implementing 'clone'
	public static LocalHoldings newInstance(LocalHoldings localholdings) throws Exception {

		LocalHoldings lhCopy = (new LocalHoldingsDatabase()).createLocalHoldings();
	
		lhCopy.setDefaultUrl(localholdings.getDefaultUrl());
		lhCopy.setDynamicUrl(localholdings.getDynamicUrl());
		lhCopy.setLinkLabel(localholdings.getLinkLabel());
	
		return lhCopy;
	 }

	/**
	 * @return
	 */
	public String getFutureUrl() {
		return futureUrl;
	}

	/**
	 * @param string
	 */
	public void setFutureUrl(String string) {
		futureUrl = ((string != null) ? string.trim() : null);
	}

}