/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contact/Contact.java-arc   1.0   Jan 14 2008 17:10:26   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:26  $
 *
 */
package org.ei.struts.backoffice.contact;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.customer.Customer;
import org.ei.struts.backoffice.customer.CustomerDatabase;

public final class Contact {

    private static Log log = LogFactory.getLog("Contact");

    private String m_strContactID = null;
    private String m_strTitle = null;
    private String m_strFirstName = null;
    private String m_strLastName = null;
    private String m_strEmail = null;

    private String m_strADDRESS = null;
    private String m_strADDRESS2 = null;
    private String m_strCITY = null;
    private String m_strSTATE = null;
    private String m_strZIP = null;
    private String m_strCOUNTRY = null;
    private String m_strPHONE = null;
    private String m_strFAX = null;

	private String m_strCustomerID = null;

    // -----------------------------------------------------------

    public String getContactID() { return m_strContactID; }
    public void setContactID(String contactid) { m_strContactID = contactid; }

    // -----------------------------------------------------------

    public String getCustomerID() { return m_strCustomerID; }
    public void setCustomerID(String customerid) { m_strCustomerID = customerid; }

    // -----------------------------------------------------------

    public String getFirstName () { return m_strFirstName; }
    public void setFirstName (String firstname) { this.m_strFirstName = firstname; }

    public String getLastName () { return m_strLastName; }
    public void setLastName (String lastname) { this.m_strLastName = lastname; }

    public String getTitle () { return m_strTitle; }
    public void setTitle (String title) { this.m_strTitle = title; }

    public String getEmail() { return m_strEmail; }
    public void setEmail(String email) { this.m_strEmail = email; }

    // -----------------------------------------------------------

    public String getADDRESS() { return m_strADDRESS; }
    public void setADDRESS (String ADDRESS) { this.m_strADDRESS = ADDRESS; }

    public String getADDRESS2() { return m_strADDRESS2; }
    public void setADDRESS2 (String ADDRESS2) { this.m_strADDRESS2 = ADDRESS2; }

    public String getCITY() { return m_strCITY; }
    public void setCITY (String CITY) { this.m_strCITY = CITY; }

    public String getSTATE() { return m_strSTATE; }
    public void setSTATE (String STATE) { this.m_strSTATE = STATE; }

    public String getZIP() { return m_strZIP; }
    public void setZIP (String ZIP) { this.m_strZIP = ZIP; }

    public String getCOUNTRY()
    {
		if(m_strCOUNTRY!=null)
			m_strCOUNTRY=m_strCOUNTRY.toUpperCase();

		return m_strCOUNTRY;
	}
    public void setCOUNTRY (String COUNTRY) { this.m_strCOUNTRY = COUNTRY; }

    public String getPHONE() { return m_strPHONE; }
    public void setPHONE (String PHONE) { this.m_strPHONE = PHONE; }

    public String getFAX() { return m_strFAX; }
    public void setFAX (String FAX) { this.m_strFAX = FAX; }

    // -----------------------------------------------------------

    public String getDisplayName() {

        StringBuffer strBufDisplayName = new StringBuffer();
        if(m_strTitle != null) {
            strBufDisplayName.append(m_strTitle);
            strBufDisplayName.append(" ");
        }
        strBufDisplayName.append(m_strFirstName);
        strBufDisplayName.append(" ");
        strBufDisplayName.append(m_strLastName);

        return strBufDisplayName.toString();
    }

    public Customer getCustomer() {
        return (new CustomerDatabase()).findCustomer(m_strCustomerID);
    }

    public static Contact copy(Contact contact) throws Exception {

        Contact contactCopy = (new ContactDatabase()).createContact();

        contactCopy.setCustomerID( contact.getCustomerID());

        contactCopy.setADDRESS (contact.getADDRESS());
        contactCopy.setADDRESS2 (contact.getADDRESS2());
        contactCopy.setCITY (contact.getCITY());
        contactCopy.setSTATE (contact.getSTATE());
        contactCopy.setZIP (contact.getZIP());
        contactCopy.setCOUNTRY (contact.getCOUNTRY());
        contactCopy.setPHONE (contact.getPHONE());
        contactCopy.setFAX (contact.getFAX());

        return contactCopy;
    }

    public String toString() {
        StringBuffer strBufObjectValue = new StringBuffer();

        strBufObjectValue.append(" Contact [id: ").append(m_strContactID);
        strBufObjectValue.append(", customer : ").append(m_strCustomerID);
        strBufObjectValue.append(", first: ").append(m_strFirstName);
        strBufObjectValue.append(", last: ").append(m_strLastName);
        strBufObjectValue.append(", title: ").append(m_strTitle);
		strBufObjectValue.append("]");
		
        return strBufObjectValue.toString();
    }
}