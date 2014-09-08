/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contact/ContactForm.java-arc   1.0   Jan 14 2008 17:10:26   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:26  $
 *
 */
package org.ei.struts.backoffice.contact;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.ResultList;
import org.ei.struts.backoffice.ResultListBase;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $
 */

public final class ContactForm extends ValidatorForm  {


  // ----------------------------------------------------------- Properties

  /**
   * The maintenance action we are performing (Create or Edit).
   */
  private String action = Constants.ACTION_CREATE;
  private Contact m_contact = new Contact();
  private String m_strCopyContactID = null;

  /**
   * Maintenance action.
   *
   * @param action The new maintenance action.
   */
  public String getAction() { return (this.action); }
  public void setAction(String action) { this.action = action; }

  public String getCopyContactID() { return (this.m_strCopyContactID); }
  public void setCopyContactID(String copyContactID) { this.m_strCopyContactID = copyContactID; }

  // --------------------------------------------------------- Public Methods

  public Contact getContact() { return m_contact; }
  public void setContact(Contact contact){ m_contact = contact; }

  // --------------------------------------------------------- Public Methods

	  public Collection getAllCountries() {
		  return Constants.getCountries();
	  }

    public ResultList getCustomerContacts() {
    	return new ResultListBase(getContact().getCustomer().getCustomerContacts());
    }

    public ResultList getAllContacts() {
        return new ResultListBase((new ContactDatabase()).getAllContacts());
    }

    public ResultList getTitles() {
        return new ResultListBase(Constants.getTitles());
    }

  // --------------------------------------------------------- Public Methods

  /**
   * Reset all properties to their default values.
   *
   * @param mapping The mapping used to select this instance
   * @param request The servlet request we are processing
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {

    this.action = Constants.ACTION_CREATE;
    this.m_strCopyContactID = null;
    this.m_contact = new Contact();
  }


  /**
   * Validate the properties that have been set from this HTTP request,
   * and return an <code>ActionErrors</code> object that encapsulates any
   * validation errors that have been found.  If no errors are found, return
   * <code>null</code> or an <code>ActionErrors</code> object with no
   * recorded error messages.
   *
   * @param mapping The mapping used to select this instance
   * @param request The servlet request we are processing
   */
  public ActionErrors validate(ActionMapping mapping,
                               HttpServletRequest request) {

      // Perform validator framework validations
      ActionErrors errors = super.validate(mapping, request);

      // Only need crossfield validations here
      //if (!m_strPassword.equals(m_strPassword)) {
          //errors.add("password2", new ActionError("error.password.match"));
      //}
      return errors;

  }

}
