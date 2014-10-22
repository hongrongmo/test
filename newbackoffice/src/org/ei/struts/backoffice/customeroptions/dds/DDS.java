/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/dds/DDS.java-arc   1.0   Jan 14 2008 17:10:50   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:50  $
 *
 */
package org.ei.struts.backoffice.customeroptions.dds;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.Options;

public final class DDS extends Options {

	private Log log = LogFactory.getLog("DDS");

	public String getProduct() {
		return Constants.DDS;
	}

	private String m_strFirstName = null;
	private String m_strLastName = null;
	private String m_strEmail = null;
	private String m_strCompany = null;
	private String m_strAddress1 = null;
	private String m_strAddress2 = null;
	private String m_strCity = null;
	private String m_strState = null;
	private String m_strZip = null;
	private String m_strCountry = null;
	private String m_strPhone = null;
	private String m_strFax = null;
	private String m_strAccount = null;
	private String m_strShipping = null;

	// ----------------------------------------------------------- Properties

	public String getFirstName() {
		return m_strFirstName;
	}
	public void setFirstName(String firstname) {
		this.m_strFirstName = firstname;
	}

	public String getLastName() {
		return m_strLastName;
	}
	public void setLastName(String lastname) {
		this.m_strLastName = lastname;
	}

	public String getEmail() {
		return m_strEmail;
	}
	public void setEmail(String email) {
		this.m_strEmail = email;
	}

	public String getCompany() {
		return m_strCompany;
	}
	public void setCompany(String company) {
		this.m_strCompany = company;
	}

	public String getAddress1() {
		return m_strAddress1;
	}
	public void setAddress1(String address1) {
		this.m_strAddress1 = address1;
	}

	public String getAddress2() {
		return m_strAddress2;
	}
	public void setAddress2(String address2) {
		this.m_strAddress2 = address2;
	}

	public String getCity() {
		return m_strCity;
	}
	public void setCity(String city) {
		this.m_strCity = city;
	}

	public String getState() {
		return m_strState;
	}
	public void setState(String state) {
		this.m_strState = state;
	}

	public String getZip() {
		return m_strZip;
	}
	public void setZip(String zip) {
		this.m_strZip = zip;
	}

	public String getCountry() {
		return m_strCountry;
	}
	public void setCountry(String country) {
		this.m_strCountry = country;
	}

	public String getPhone() {
		return m_strPhone;
	}
	public void setPhone(String phone) {
		this.m_strPhone = phone;
	}

	public String getFax() {
		return m_strFax;
	}
	public void setFax(String fax) {
		this.m_strFax = fax;
	}

	public String getAccountNumber() {
		return m_strAccount;
	}
	public void setAccountNumber(String account) {
		this.m_strAccount = account;
	}

	public String getShipping() {
		return m_strShipping;
	}
	public void setShipping(String shipping) {
		this.m_strShipping = shipping;
	}

	// ---------------------------------------------------------

	public String toString() {
		StringBuffer strBufObjectValue = new StringBuffer();

		strBufObjectValue.append(super.toString());
		
		strBufObjectValue.append(" DDS Data [ first").append(m_strFirstName);
		strBufObjectValue.append(", last: ").append(m_strLastName);
		strBufObjectValue.append(", email: ").append(m_strEmail);
		strBufObjectValue.append(", co.: ").append(m_strCompany);
		strBufObjectValue.append(", addr1: ").append(m_strAddress1);
		strBufObjectValue.append(", addr2: ").append(m_strAddress2);
		strBufObjectValue.append(", city: ").append(m_strCity);
		strBufObjectValue.append(", state: ").append(m_strState);
		strBufObjectValue.append(", zip: ").append(m_strZip);
		strBufObjectValue.append(", ctry: ").append(m_strCountry);
		strBufObjectValue.append(", ph: ").append(m_strPhone);
		strBufObjectValue.append(", fax: ").append(m_strFax);
		strBufObjectValue.append(", ship: ").append(getShipping());
		strBufObjectValue.append("]");
		
		return strBufObjectValue.toString();

	}
}