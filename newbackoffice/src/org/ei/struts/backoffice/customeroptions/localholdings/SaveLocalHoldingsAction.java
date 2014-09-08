/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customeroptions/localholdings/SaveLocalHoldingsAction.java-arc   1.0   Jan 14 2008 17:10:52   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:52  $
 *
 *
 */
package org.ei.struts.backoffice.customeroptions.localholdings;

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
import org.ei.struts.backoffice.contract.item.Item;
import org.ei.struts.backoffice.contract.item.ItemDatabase;

/**
 * Implementation of <strong>Action</strong> that validates and creates or
 * updates the user LocalHoldings information entered by the user.  If a new
 * LocalHoldings is created, the user is also implicitly logged on.
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:52  $
 */

public final class SaveLocalHoldingsAction extends BackOfficeBaseAction {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("SaveLocalHoldingsAction");

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
		LocalHoldingsForm lhform = (LocalHoldingsForm) form;
		String action = lhform.getAction();
		if (action == null) {
			action = Constants.ACTION_CREATE;
		}
		LocalHoldingsDatabase database = new LocalHoldingsDatabase();
		if (log.isDebugEnabled()) {
			log.debug(
				"SaveLocalHoldingsAction:  Processing " + action + " action");
		}

		// Was this transaction cancelled?
		if (isCancelled(request)) {
			if (log.isTraceEnabled()) {
				log.trace(" Transaction '" + action + "' was cancelled");
			}
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

		// Validate the request parameters specified by the user
		if (log.isTraceEnabled()) {
			log.trace(" Performing extra validations");
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			saveToken(request);
			return (mapping.getInputForward());
		}

		LocalHoldings localholdings = null;
		try {
			if (Constants.ACTION_CREATE.equals(action)) {
				localholdings = database.createLocalHoldings();
			} else {
				localholdings =	database.findLocalHoldings(lhform.getLocalHoldings().getLocalHoldingsID());
			}
			Item item = (new ItemDatabase()).findItem(request.getParameter(Tokens.CONTRACTID), request.getParameter(Tokens.ITEMID));
			localholdings.setItem(item);

			log.info("Database LocalHoldings object" + localholdings);

			String strLocalHoldingsID = localholdings.getLocalHoldingsID();
			PropertyUtils.copyProperties(localholdings,	lhform.getLocalHoldings());
			if (strLocalHoldingsID != null) {
				localholdings.setLocalHoldingsID(strLocalHoldingsID);
			}
			log.info("LocalHoldingsDatabase save" + localholdings);
			database.saveLocalHoldings(localholdings);
		} catch (Exception e) {
			log.error("Database save", e);
		}

		// Remove the obsolete form bean
		if (mapping.getAttribute() != null) {
			if ("request".equals(mapping.getScope()))
				request.removeAttribute(mapping.getName());
			else
				session.removeAttribute(mapping.getName());
		}

		// Forward control to the specified success URI
		if (log.isTraceEnabled()) {
			log.trace(" Forwarding to success page");
		}

		StringBuffer path = new StringBuffer();
		// Get stub URI from mapping (/do/whatever?paramName=)
		path.append(mapping.findForward("editItem").getPath());
		path.append("&customerid=");
		path.append((String) localholdings.getItem().getContract().getCustomerID());
		path.append("&contractid=");
		path.append((String) localholdings.getItem().getContract().getContractID());
		path.append("&itemid=");
		path.append((String) localholdings.getItem().getItemID());
		
		// Return a new forward based on stub+value
		return new ActionForward(path.toString());

	}

}
