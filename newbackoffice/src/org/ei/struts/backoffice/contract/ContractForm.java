/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/ContractForm.java-arc   1.0   Jan 14 2008 17:10:28   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:28  $
 *
 */
package org.ei.struts.backoffice.contract;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.ResultList;
import org.ei.struts.backoffice.ResultListBase;
import org.ei.struts.backoffice.contract.item.Item;
import org.ei.struts.backoffice.customer.Customer;
import org.ei.struts.backoffice.product.ProductDatabase;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $
 */

public final class ContractForm extends ValidatorForm  {


    /**
     * The maintenance action we are performing (Create or Edit).
     */
    private String action = Constants.ACTION_CREATE;

    private Contract m_Contract = new Contract();
	
	private Item m_item = new Item();
	
	
    // ----------------------------------------------------------- Properties

    /**
     * Return the maintenance action.
     */
    public String getAction() { return (this.action); }
    /**
     * Set the maintenance action.
     *
     * @param action The new maintenance action.
     */
    public void setAction(String action) { this.action = action; }

	public ResultList getCustomerContacts() {
    	Customer customer = getContract().getCustomer();
		return new ResultListBase(customer.getCustomerContacts());
    }


    // ----------------------------------------------------------- Properties

    public Contract getContract() { return m_Contract; }
    public void setContract(Contract contract) { m_Contract = contract; }

	public Item getItem() { return m_item; }
	public void setItem(Item item ) { m_item = item; }

    // -----------------------------------------------------------

	public Collection getAllProducts() {
		return (new ProductDatabase()).getProducts();
	}

	public Collection getAllStatus() {
		return Constants.getAllStatus();
	}

	public Collection getAllAccessType() {
		return Constants.getAllAccessType();
	}

	public Collection getAllContractType() {
		return Constants.getAllContractType();
	}

	public Collection getAllSiteLicense() {
		return Constants.getSiteLicenses();
	}

	public Collection getAllMonths() {
		return Constants.getMonths();
	}

	public Collection getAllDays() {
		return Constants.getDays();
	}
	
	public Collection getAllYears() {
		return Constants.getYears();
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
    	this.m_Contract = new Contract();
    	this.m_item = new Item();
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
