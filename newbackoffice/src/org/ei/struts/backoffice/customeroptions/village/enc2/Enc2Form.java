/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/village/enc2/Enc2Form.java-arc   1.0   Jan 14 2008 17:10:58   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:58  $
 *
 */
package org.ei.struts.backoffice.customeroptions.village.enc2;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.customeroptions.OptionConstants;
import org.ei.struts.backoffice.customeroptions.village.Village;
import org.ei.struts.backoffice.customeroptions.village.VillageForm;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $
 */

public final class Enc2Form extends VillageForm {

	private static Log log = LogFactory.getLog(Enc2Form.class);

	public Enc2Form() {
		setAction(Constants.ACTION_CREATE);
		setOptions(new Enc2());
	}

	public Collection getAllStartPages() { return OptionConstants.getDefaultStartPageOptions( getOptions().getProduct() ); }

	public Collection getAllDatabaseOptions() { return OptionConstants.getDefaultDatabaseOptions( getOptions().getProduct() ); }

	public Collection getAllLitbulletins() { return OptionConstants.getAllLitbulletins( getOptions().getProduct() ); }

	public Collection getAllPatbulletins() { return OptionConstants.getAllPatbulletins( getOptions().getProduct() ); }
		
	
	// --------------------------------------------------------- Public Methods

	/**
	 * Reset all properties to their default values.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		setAction(Constants.ACTION_CREATE);
		setOptions(new Enc2());
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

		MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );
		ActionErrors errors = super.validate(mapping, request);

		Enc2 opt = (Enc2) getOptions();
		
		if((opt.getSelectedOptions() == null) && (getAllPatbulletins() == null) && (getAllLitbulletins() == null)) {
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.cartridge"));
		}
		else if((opt.getSelectedOptions().length == 0) && (opt.getPatbulletins().length == 0) && (opt.getLitbulletins().length == 0)) {
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.cartridge"));
		}
		return errors;

	}

}
