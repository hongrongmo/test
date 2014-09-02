/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/Contract.java-arc   1.0   Jan 14 2008 17:10:28   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:28  $
 *
 */
package org.ei.struts.backoffice.contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.contract.item.Item;
import org.ei.struts.backoffice.contract.item.ItemDatabase;
import org.ei.struts.backoffice.customer.Customer;
import org.ei.struts.backoffice.customer.CustomerDatabase;
import org.ei.struts.backoffice.util.date.BODate;

public final class Contract {

	private static Log log = LogFactory.getLog("Contract");

	private String m_strContractID = null;
	private String m_strRenewalRef= "0";

	private String m_strCustomerID = null;

	private String m_strStatus = null;
	private String m_strAccessType = null;
	private String m_strContractType = null;
	private String m_strSiteLicense = null;

	private String[] m_strItemIDs = new String[]{};

	private int m_intEnabled = Constants.ENABLED;

	private BODate m_dteStartDate = new BODate();
	private BODate m_dteEndDate = new BODate();
	private BODate m_dteContractStartDate = new BODate();
	private BODate m_dteContractEndDate = new BODate();
	
	private String[] m_strItemFilter = new String[]{};
	
	public String[] getItemFilter() { return m_strItemFilter; }
	public void setItemFilter(String[] filter) { 
		if(filter != null && filter.length == 1) {
			if(filter[0].equals(Constants.EMPTY_STRING)) {
				log.info("skipping empty filter");
			}
			else {
				m_strItemFilter = filter;
			}
		}
		else {
			m_strItemFilter = filter;
		} 
	}

	// ----------------------------------------------------------- Properties
	public Contract() {
		Calendar calendar = Calendar.getInstance();
	
		// set the start date of the to today
		m_dteStartDate.setDate(calendar);
	
		// set the end date to today plus xxx days
		calendar.add(Calendar.DAY_OF_YEAR, Constants.CONTRACT_LENGTH);
		m_dteEndDate.setDate(calendar);
	
	}
	
	public int getEnabled() { return m_intEnabled; }
	public void setEnabled(int enabled) { this.m_intEnabled = enabled; }

	public String getContractID() { return m_strContractID; }
	public void setContractID(String contractid) { m_strContractID = contractid; }
	
	public String getRenewalRefID() { return ((m_strRenewalRef == null) ? "0" : m_strRenewalRef); }
	public void setRenewalRefID(String contractid) { m_strRenewalRef = contractid; }
	
	public String getCustomerID() { return m_strCustomerID; }
	public void setCustomerID(String customerid) { m_strCustomerID = customerid; }
	
	public String getAccessType() { return m_strAccessType; }
	public void setAccessType (String type) { this.m_strAccessType = type; }
	
	public String getContractType() { return m_strContractType; }
	public void setContractType (String type) { this.m_strContractType = type; }

	public String getStatus() { return m_strStatus; }
	public void setStatus(String status) { this.m_strStatus = status; }
	
	public String getSiteLicense() { return m_strSiteLicense; }
	public void setSiteLicense (String  SiteLicense) { this.m_strSiteLicense = SiteLicense; }
	
	public String[] getItemIDs() { return m_strItemIDs; }
	public void setItemIDs(String[] items) { this.m_strItemIDs = items; }


	public BODate getStartdate() { return m_dteStartDate; }
	public void setStartdate(BODate bodate) { m_dteStartDate = bodate; }
	public void setStartdate(String strdate) { m_dteStartDate.setDate(strdate);	}

	public BODate getEnddate() { return m_dteEndDate; }
	public void setEnddate(BODate bodate) { m_dteEndDate = bodate; }
	public void setEnddate(String strdate) { m_dteEndDate.setDate(strdate);	}

	public BODate getContractStartdate() { return m_dteContractStartDate; }
	public void setContractStartdate(BODate bodate) { m_dteContractStartDate = bodate; }
	public void setContractStartdate(String strdate) { m_dteContractStartDate.setDate(strdate); }

	public BODate getContractEnddate() { return m_dteContractEndDate; }
	public void setContractEnddate(BODate bodate) { m_dteContractEndDate = bodate; }
	public void setContractEnddate(String strdate) { m_dteContractEndDate.setDate(strdate); }

	// -----------------------------------------------------------

	public boolean getIsEnabled() { return (m_intEnabled == Constants.ENABLED); }

	public boolean getIsActive() {

	  Calendar endPeriod = Calendar.getInstance();
	  endPeriod.setTime(m_dteEndDate.getDate());

	  return (Calendar.getInstance().getTime().before(endPeriod.getTime()));
	}

	public boolean getIsInGracePeriod() {

		// only standard contracts (purchased products) have
		// grace periods!
		Calendar gracePeriod = Calendar.getInstance();
		gracePeriod.setTime(m_dteEndDate.getDate());
		gracePeriod.add(Calendar.DAY_OF_YEAR, Constants.GRACE_PERIOD);

		return (Calendar.getInstance().getTime().before(gracePeriod.getTime()));
	}

	public boolean getIsArchived() {

	  Calendar archivePeriod = Calendar.getInstance();
	  archivePeriod.setTime(m_dteEndDate.getDate());
	  archivePeriod.add(Calendar.DAY_OF_YEAR, Constants.ARCHIVE_PERIOD);

	  return (Calendar.getInstance().getTime().after(archivePeriod.getTime()));
	}

	public boolean getIsRenewable() {
    	return true;
	}

	// -----------------------------------------------------------

	public Collection getAllItems() {
		if(m_strItemFilter == null || m_strItemFilter.length == 0) {
			return (new ItemDatabase()).getItems(m_strContractID);
		}
		else {
			Collection filter = Arrays.asList(m_strItemFilter);
			Collection filtered = new ArrayList();
			Collection allItems = (new ItemDatabase()).getItems(m_strContractID);
			Iterator itrItems = allItems.iterator();
			while(itrItems.hasNext()) {
				Item anItem = ((Item) itrItems.next());
				String pid = anItem.getProductID();
				if(filter.contains(pid)) {
					filtered.add(anItem);
				}
			}
			return filtered;
		}
	}


	public Customer getCustomer() {
		return (new CustomerDatabase()).findCustomer(m_strCustomerID);
	}

	public Item getContractItem(String productid) { 

		Iterator itr = getAllItems().iterator();
		Item matchedItem = null;
		
		while(itr.hasNext()) {
			Item item = (Item) itr.next();
			if(item.getProductID().equals(productid)) {
				matchedItem = item;
			}
		}
		return matchedItem;
	}

	// -----------------------------------------------------------

	// public method used in place of implementing 'clone'
	public static Contract newInstance(Contract contract) throws Exception {

	    Contract contractCopy = (new ContractDatabase()).createContract();
	
	    contractCopy.setCustomerID(contract.getCustomerID());
		contractCopy.setItemIDs(contract.getItemIDs());

	    contractCopy.setAccessType (contract.getAccessType());
		contractCopy.setStatus(contract.getStatus());
	    contractCopy.setSiteLicense (contract.getSiteLicense());
	
		contractCopy.setEnabled(contract.getEnabled());
		
	    return contractCopy;
	}

	public String toString() {

		StringBuffer strBufObjectValue = new StringBuffer();

	    strBufObjectValue.append("[ id: ").append(m_strContractID);
	    strBufObjectValue.append(", renewed From: ").append(m_strRenewalRef);
	    strBufObjectValue.append(", customer: ").append(m_strCustomerID);
	    strBufObjectValue.append(", start date: ").append(m_dteStartDate.getDisplaydate());
	    strBufObjectValue.append(", end date: ").append(m_dteEndDate.getDisplaydate());
		strBufObjectValue.append(", ctrct start: ").append(m_dteContractStartDate.getDisplaydate());
		strBufObjectValue.append(", ctrct end: ").append(m_dteContractEndDate.getDisplaydate());
	    strBufObjectValue.append(", access: ").append(m_strAccessType);
	    strBufObjectValue.append(", license: ").append(m_strSiteLicense);
	    strBufObjectValue.append("] ");

		return strBufObjectValue.toString();
	}

}