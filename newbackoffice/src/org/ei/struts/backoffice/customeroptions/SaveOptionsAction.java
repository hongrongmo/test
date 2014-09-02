/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/SaveOptionsAction.java-arc   1.0   Jan 14 2008 17:10:44   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:44  $
 *
 * ====================================================================
 */

package org.ei.struts.backoffice.customeroptions;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.contract.Contract;
import org.ei.struts.backoffice.contract.item.Item;

/**
 * Implementation of <strong>Action</strong> that validates and creates or
 * updates the user access information entered by the user.  If a new
 * access is created, the user is also implicitly logged on.
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:44  $
 */

public final class SaveOptionsAction extends BackOfficeBaseAction {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("SaveAccessAction");

	// --------------------------------------------------------- Public Methods

	/**
	 * Process the specified HTTP request, and create the corresponding HTTP
	 * response (or forward to another web component that will create it).
	 * Return an <code>ActionForward</code> instance describing where and how
	 * control should be forwarded, or <code>null</code> if the response has
	 * already been completed.
	 *
	 * @param mapping The ActionMapping used to select this instance
	 * @param actionForm The optional ActionForm bean for this request (if any)
	 * @param request The HTTP request we are processing
	 * @param response The HTTP response we are creating
	 *
	 * @exception Exception if the application business logic throws
	 *  an exception
	 */
	public ActionForward executeAction(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		// Extract attributes and parameters we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();
		OptionsForm optionsform = (OptionsForm) form;

		String action = optionsform.getAction();
		if (action == null) {
			action = Constants.ACTION_CREATE;
		}
		log.info("Form action is " + action);

		// Was this transaction cancelled?
		if (isCancelled(request)) {
			log.info(" Transaction '" + action + "' was cancelled");
			return (mapping.findForward("success"));
		}

		// Validate the transactional control token
		ActionErrors errors = new ActionErrors();

		if (log.isTraceEnabled()) {
			log.trace(" Checking transactional control token");
		}
		if (!isTokenValid(request)) {
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.transaction.token"));
		}
		resetToken(request);

		OptionsDatabase database = new OptionsDatabase();
		log.trace(" Performing extra validations");

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			saveToken(request);
			return (mapping.getInputForward());
		}

		try {
			Options opts = database.createOptions(optionsform.getOptions().getProduct());
			PropertyUtils.copyProperties(opts, optionsform.getOptions());
			database.saveOptions(opts);
			log.info("saved " + optionsform.getOptions());
		} catch (Exception e) {
			log.error("Database save", e);
		}

		// Remove the obsolete form bean
		if (mapping.getAttribute() != null) {
			if (Tokens.REQUEST.equals(mapping.getScope())) {
				request.removeAttribute(mapping.getName());
			} else {
				session.removeAttribute(mapping.getName());
			}
		}

		StringBuffer path = new StringBuffer();

		Options options = optionsform.getOptions();
		Contract contract = options.getContract();
		Item item = contract.getContractItem(options.getProduct());
		 
		if ((options != null)) {
			path.append(mapping.findForward("editItem").getPath());
			path.append("&").append(Tokens.CONTRACTID).append("=");
			path.append(options.getContractID());
			path.append("&").append(Tokens.ITEMID).append("=");
			path.append(item.getItemID());
			path.append("&").append(Tokens.CUSTOMERID).append("=");
			path.append(options.getCustomerID());
		}

		return new ActionForward(path.toString());
	}

}
