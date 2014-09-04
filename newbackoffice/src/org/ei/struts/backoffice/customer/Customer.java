/*
* $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customer/Customer.java-arc   1.0   Jan 14 2008 17:10:38   johna  $
* $Revision:   1.0  $
* $Date:   Jan 14 2008 17:10:38  $
*
*/
package org.ei.struts.backoffice.customer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.ResultList;
import org.ei.struts.backoffice.ResultListBase;
import org.ei.struts.backoffice.contact.ContactDatabase;
import org.ei.struts.backoffice.contract.ContractDatabase;
import org.ei.struts.backoffice.region.Region;
import org.ei.struts.backoffice.region.RegionDatabase;

public class Customer {

    protected static Log log = LogFactory.getLog("Customer");

    public Customer() {
    }
    private String m_strParentID = null;
	private String[] m_strMemberIDs = new String[]{};
	private String m_strConsortium = Constants.NO;

    private String m_strCustomerID = null;
    private String m_strName = null;
    private String m_strNotes = null;
    private int m_intRegion = 0;
    private Region m_objRegion = null;
    private int m_intIndustry = 0;
    
	private String m_strType = Constants.EMPTY_STRING;
	private String m_otherID = null;
	
	private String m_strAddress1 = null;
	private String m_strAddress2 = null;
	private String m_strCity = null;
	private String m_strState = null;
	private String m_strZip = null;
	private String m_strCountry = null;
	private String m_strPhone = null;
	private String m_strFax = null;

	private String m_strStartMonth = null;
	private String m_strStartDay = null;
	private String m_strStartYear = null;

    // ----------------------------------------------------------- Properties

    public String getCustomerID() { return m_strCustomerID; }
    public void setCustomerID(String customerid) { m_strCustomerID = customerid; }

    public String getName () { return m_strName; }
    public void setName (String name) { this.m_strName = name; }

    public String getType() { return m_strType; }
    public void setType (String type) { this.m_strType = type; }

    public String getConsortium () { return m_strConsortium ; }
    public void setConsortium (String isConsortium ) { this.m_strConsortium = isConsortium; }

    public int getRegion() { return m_intRegion; }
    public void setRegion (int region) { this.m_intRegion = region; }

    public int getIndustry() { return m_intIndustry; }
    public void setIndustry(int industry) { this.m_intIndustry = industry; }

    public String getNotes() { return m_strNotes; }
    public void setNotes (String notes) { this.m_strNotes = notes; }

    public String getParentID(){ return (m_strParentID == null) ? "0" : m_strParentID; }
    public void setParentID(String companyid){ m_strParentID = companyid; }

	public String[] getMemberIDs(){ return m_strMemberIDs; }
	public void setMemberIDs(String[] memberids){ m_strMemberIDs = memberids; }

    // ---------------------------------------------------------

    public ResultList getAllContracts() {

        Collection allContracts = (new ContractDatabase()).getContracts(m_strCustomerID);

        return new ResultListBase(allContracts);
    }

    public void setSalesRegion(Region sr) {

        m_objRegion = sr;
    }

    public Region getSalesRegion() {

        if(m_objRegion == null)
        {
            m_objRegion = (new RegionDatabase()).findRegion(String.valueOf(m_intRegion));
        }
        return m_objRegion;
    }

	// ---------------------------------------------------------

    public Customer getParentCompany() {

        Customer parent=null;
		parent = (new CustomerDatabase()).findCustomer(this.getParentID());
        return parent;
    }

	public Collection getConsortiumMembers() {
			Collection members = new ArrayList();

			String[] memberids = this.getMemberIDs();
			Customer customer=null;
			for(int x=0; x < memberids.length; x++) {
				customer = (new CustomerDatabase()).findCustomer(memberids[x]);
				members.add(customer);
			}
			return members;
  	}
    // ---------------------------------------------------------

	public Collection getCustomerContacts() {
		return (new ContactDatabase()).getContacts(m_strCustomerID);
	}

    // ---------------------------------------------------------

    public String toString() {
        StringBuffer strBufObjectValue = new StringBuffer();

        strBufObjectValue.append("Customer [id ").append(m_strCustomerID).append(", ");
		if(m_otherID != null) {
			strBufObjectValue.append(", otherID: ").append(m_otherID);
		}
        if(m_strName != null) {
            strBufObjectValue.append(", name: ").append(m_strName );
        }
        if(m_strConsortium != null) {
            strBufObjectValue.append(", consortium?:").append(m_strConsortium );
        }
        if(m_strNotes != null) {
            strBufObjectValue.append(", notes: ").append(m_strNotes);
        }
		strBufObjectValue.append("]");
        return strBufObjectValue.toString();
    }
	/**
	 * @return
	 */
	public String getFax() {
		return m_strFax;
	}

	/**
	 * @param string
	 */
	public void setFax(String string) {
		m_strFax = string;
	}

	/**
	 * @return
	 */
	public String getAddress1() {
		return m_strAddress1;
	}

	/**
	 * @return
	 */
	public String getAddress2() {
		return m_strAddress2;
	}

	/**
	 * @return
	 */
	public String getCity() {
		return m_strCity;
	}

	/**
	 * @return
	 */
	public String getCountry() {
		return m_strCountry;
	}

	/**
	 * @return
	 */
	public String getPhone() {
		return m_strPhone;
	}

	/**
	 * @return
	 */
	public String getState() {
		return m_strState;
	}

	/**
	 * @return
	 */
	public String getZip() {
		return m_strZip;
	}


	/**
	 * @param string
	 */
	public void setAddress1(String string) {
		m_strAddress1 = string;
	}

	/**
	 * @param string
	 */
	public void setAddress2(String string) {
		m_strAddress2 = string;
	}

	/**
	 * @param string
	 */
	public void setCity(String string) {
		m_strCity = string;
	}

	/**
	 * @param string
	 */
	public void setCountry(String string) {
		m_strCountry = string;
	}

	/**
	 * @param string
	 */
	public void setPhone(String string) {
		m_strPhone = string;
	}

	/**
	 * @param string
	 */
	public void setState(String string) {
		m_strState = string;
	}

	/**
	 * @param string
	 */
	public void setZip(String string) {
		m_strZip = string;
	}

	// ---------------------
	// DATE Helper Methods

	public String getDisplayStartDate() 
	{ 
		Date aDate = null; 
		aDate = getStartDate();
		if(aDate != null) {
			return Constants.formatDate(getStartDate().getTime());
		}
		else {
			return "N/A";
		}
	 
	}

	public Date getStartDate() {

	  Calendar calendar = Calendar.getInstance();
	  try {
		calendar.set(Integer.parseInt(m_strStartYear), Integer.parseInt(m_strStartMonth), Integer.parseInt(m_strStartDay));
	  }
	  catch (NumberFormatException nfe) {
		return null;	  	
	  }
	  return (calendar.getTime());

	}
	public void setStartDate (String startdate) {

	  Calendar calendar = Calendar.getInstance();
	  calendar.setTime(Constants.createDate(startdate));

	  m_strStartMonth = String.valueOf(calendar.get(Calendar.MONTH));
	  m_strStartDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
	  m_strStartYear = String.valueOf(calendar.get(Calendar.YEAR));
	}

	public String getStartMonth() { return m_strStartMonth; }
	public void setStartMonth(String month) { m_strStartMonth = month; }

	public String getStartDay() { return m_strStartDay; }
	public void setStartDay(String day) { m_strStartDay = day; }

	public String getStartYear() { return m_strStartYear; }
	public void setStartYear(String year) { m_strStartYear = year; }
	
	/**
	 * @return
	 */
	public String getOtherID() {
		return m_otherID;
	}

	/**
	 * @param string
	 */
	public void setOtherID(String string) {
		m_otherID = string;
	}

}