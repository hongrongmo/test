/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/dds/DDSForm.java-arc   1.0   Jan 14 2008 17:10:50   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:50  $
 *
 */
package org.ei.struts.backoffice.customeroptions.dds;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.OptionsForm;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $
 */

public final class DDSForm extends OptionsForm {

	private static Log log = LogFactory.getLog(DDSForm.class);

	/**
	 * The maintenance action we are performing (Create or Edit).
	 */
	public DDSForm() { 
		setAction(Constants.ACTION_CREATE);
		setOptions(new DDS());
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
		setOptions(new DDS());
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

		log.debug("JM" + ( (DDS) this.getOptions()).getCompany());
		if(
			( (DDS) this.getOptions()).getCompany().indexOf("&") > -1
			|| ( (DDS) this.getOptions()).getAddress1().indexOf("&") > -1
			|| ( (DDS) this.getOptions()).getAddress2().indexOf("&") > -1
		) 
		
		{
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.ampersand"));
		}

		return errors;

	}

}
