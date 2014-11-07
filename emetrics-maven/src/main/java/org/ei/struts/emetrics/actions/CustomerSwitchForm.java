package org.ei.struts.emetrics.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

/**
 * Form bean for the user signon page.
 */
public class CustomerSwitchForm extends ActionForm {

	private int customerid = 0;
	private String action = "";
	
	/**
	 * Public accessors and mutators
	 */
	public int getCustomerId() {
		return (this.customerid);
	}

	public void setCustomerId(int acustomerid) {
		this.customerid = acustomerid;
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
		ActionErrors errors = new ActionErrors();

		// Get access to the message resources for this application
		// There's not an easy way to access the resources from an ActionForm
		MessageResources resources =
			(MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

		return errors;
	}

	/**
	 * Reset all properties to their default values.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		this.customerid = 0;
		this.action = "";
	}

	/**
	 * @return
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param string
	 */
	public void setAction(String string) {
		action = string;
	}

}