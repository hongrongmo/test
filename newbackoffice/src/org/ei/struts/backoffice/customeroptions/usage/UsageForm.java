/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/usage/UsageForm.java-arc   1.0   Jan 14 2008 17:10:54   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:54  $
 *
 */
package org.ei.struts.backoffice.customeroptions.usage;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.OptionsForm;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $
 */

public final class UsageForm extends OptionsForm {

	private static Log log = LogFactory.getLog("DDSForm");

	/**
	 * The maintenance action we are performing (Create or Edit).
	 */
	public UsageForm() { 
		setAction(Constants.ACTION_CREATE);
		setOptions(new Usage());
	}

	public Collection getAllCountries() {
		return Constants.getCountries();
	}
	
	// ----------------------------------------------------------- Properties


	// --------------------------------------------------------- Public Methods

	/**
	 * Reset all properties to their default values.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		setAction(Constants.ACTION_CREATE);
		setOptions(new Usage());
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

		// Perform validator framework validations
		ActionErrors errors = super.validate(mapping, request);

		return errors;

	}

}
