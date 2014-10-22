/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customer/CustomerForm.java-arc   1.0   Jan 14 2008 17:10:40   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:40  $
 *
 */
package org.ei.struts.backoffice.customer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.ValidatorForm;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.ResultList;
import org.ei.struts.backoffice.ResultListBase;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.customeroptions.OptionConstants;
import org.ei.struts.backoffice.region.RegionDatabase;
import org.ei.struts.backoffice.type.TypeDatabase;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $
 */

public class CustomerForm extends ValidatorForm {

	/**
	 * The maintenance action we are performing
	 */
	private String action = Constants.ACTION_CREATE;
	private Customer m_Customer = new Customer();

	private boolean contacts = true;
	private boolean consortiums = false;

	private int m_intMin = -1;
	private int m_intMax = -1;

	// ----------------------------------------------------------- Properties

	/**
	 * Return the maintenance action.
	 */
	public String getAction() {
		return (this.action);
	}
	/**
	 * Set the maintenance action.
	 *
	 * @param action The new maintenance action.
	 */
	public void setAction(String action) {
		this.action = action;
	}

	public boolean getContacts() {
		return (this.contacts);
	}
	public void setContacts(boolean contacts) {
		this.contacts = contacts;
	}

	public boolean getConsortiums() {
		return (this.consortiums);
	}
	public void setConsortiums(boolean consortiums) {
		this.consortiums = consortiums;
	}

	public Customer getCustomer() {
		return m_Customer;
	}
	public void setCustomer(Customer contact) {
		m_Customer = contact;
	}

	public int getMin() {
		return m_intMin;
	}
	public void setMin(int min) {
		m_intMin = min;
	}

	public int getMax() {
		return m_intMax;
	}
	public void setMax(int max) {
		m_intMax = max;
	}

	// --------------------------------------------------------- Form Helper Methods

	public Collection getAllMonths() {
		return Constants.getMonths();
	}

	public Collection getAllDays() {
		return Constants.getDays();
	}

	public Collection getAllYears() {
		return Constants.getYears();
	}

	private Map makeParams() {
		Map params = new HashMap();
		params.put(Tokens.CUSTOMERID, getCustomer().getCustomerID());
		return params;
	}
	public Map getCreateContract() {
		Map params = makeParams();
		return params;
	}

	// --------------------------------------------------------- Form Helper Methods

	public Collection getAllTypes() {
		return (new TypeDatabase()).getTypes();
	}

	public Collection getAllRegions() {
		return (new RegionDatabase()).getRegions();
	}

	public Collection getAllCountries() {
		return Constants.getCountries();
	}

	public Collection getYesNoOption() {
		return OptionConstants.getYesNoOption();
	}

	public ResultList getCustomerContacts() {
		return new ResultListBase(getCustomer().getCustomerContacts());
	}

	public ResultList getAllCustomers() {
		if (consortiums == true) {
			return getAllConsortiums();
		}
		if (getMax() < 0) {
			return new ResultListBase((new CustomerDatabase()).getCustomers());
		} else {
			return new ResultListBase(
				(new CustomerDatabase()).getCustomers(getMin(), getMax()));
		}
	}

	public ResultListBase getAllConsortiums() {
		return new ResultListBase((new CustomerDatabase()).getConsortiums());
	}

	public ResultListBase getConsortiumMembers() {
		return new ResultListBase((this.getCustomer()).getConsortiumMembers());
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
		;
		this.contacts = true;
		this.m_Customer = new Customer();
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
	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {

		MessageResources resources =
			(MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

		// Perform validator framework validations
		ActionErrors errors = super.validate(mapping, request);

		if (!getAction().equals(Constants.ACTION_ADD)) {
			if ((getCustomer().getName() == null)
				|| (getCustomer().getName().equals(Constants.EMPTY_STRING))) {
				errors.add(
					ActionErrors.GLOBAL_ERROR,
					new ActionError(
						"errors.required",
						resources.getMessage("prompt.name")));
			}
		}
		return errors;
	}

}
