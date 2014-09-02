/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customer/SaveCustomerAction.java-arc   1.0   Jan 14 2008 17:10:40   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:40  $
 *
 */
package org.ei.struts.backoffice.customer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
/**
 * Implementation of <strong>Action</strong> that validates and creates or
 * updates the user contact information entered by the user.  If a new
 * contact is created, the user is also implicitly logged on.
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:40  $
 */

public final class SaveCustomerAction extends BackOfficeBaseAction {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("SaveCustomerAction");

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
		CustomerForm cstform = (CustomerForm) form;
		String action = cstform.getAction();
		if (action == null) {
			action = Constants.ACTION_CREATE;
		}

		CustomerDatabase database = new CustomerDatabase();
		if (log.isDebugEnabled()) {
			log.debug(" SaveCustomerAction:  Processing " + action + " action");
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
		if ((Constants.ACTION_EDIT.equals(action))) {

			// prevent adding customer to itself as parent
			if(cstform.getCustomer().getCustomerID().equals(cstform.getCustomer().getParentID())) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.customer.equalsParent", cstform.getCustomer().getParentID()));
				cstform.getCustomer().setParentID("0");
			}

			// prevent adding non-existent parent id
			if(!cstform.getCustomer().getParentID().equals("0") && database.findCustomer(cstform.getCustomer().getParentID()) == null) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.customer.notExists", cstform.getCustomer().getParentID()));
				cstform.getCustomer().setParentID("0");
			}

		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			saveToken(request);
			return (mapping.getInputForward());
		}

		try {
			Customer customer = null;
			if (Constants.ACTION_CREATE.equals(action)) {
				customer = database.createCustomer();
			} else if (Constants.ACTION_EDIT.equals(action)) {
				customer = database.findCustomer(cstform.getCustomer().getCustomerID());
			} else if (Constants.ACTION_REMOVE.equals(action)) {

				customer = database.findCustomer(cstform.getCustomer().getCustomerID());

				log.info(
						" Removing member "
							+ customer.getCustomerID()
							+ " from Consortium "
							+ customer.getParentID());

				customer.setParentID("0");

				database.saveCustomer(customer);

				// when done, redirect (back) to edit customer
				action = Constants.ACTION_EDIT;
				cstform.setAction(action);
				return (mapping.findForward("editCustomer"));

			} else if (Constants.ACTION_ADD.equals(action)) {
				log.info(
					" Adding customer "
						+ request.getParameter(Tokens.CUSTOMERID)
						+ " to Consortium "
						+ request.getParameter(Tokens.CONSORTIUMID));

				// do whatever to add customer to consortium
				Customer consortium = null;
				CustomerDatabase cDatabase = new CustomerDatabase();
				consortium = cDatabase.findCustomer(request.getParameter(Tokens.CONSORTIUMID));

				//String[] added = request.getParameterValues(Tokens.CUSTOMERID);
				String[] added = new String[]{cstform.getCustomer().getCustomerID()};
				String[] exists = consortium.getMemberIDs();
				if (added != null) {
					List newmembers = Arrays.asList(added);
					List members = new ArrayList(Arrays.asList(exists));

					Iterator itrNewMembers = newmembers.iterator();
					String strMemberID = null;
					while (itrNewMembers.hasNext()) {
						strMemberID = (String) itrNewMembers.next();
						if (!members.contains(strMemberID)) {
							log.info("ADDING Consortium member " + strMemberID);
							members.add(strMemberID);
						} else {
							log.info("SKIPPING already a member " + strMemberID);
						}
					}
					consortium.setMemberIDs((String[]) members.toArray(new String[] {}));
					cDatabase.saveConsortium(consortium);
				}

				// when done, redirect (back) to edit customer
				action = Constants.ACTION_EDIT;
				cstform.setAction(action);
				return (mapping.findForward("editCustomer"));

			} // if action == ADD

			// only end up here on EDIT and CREATE actions
			PropertyUtils.copyProperties(customer, cstform.getCustomer());
			log.info("Database save" + customer.toString());
			database.saveCustomer(customer);
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

		if (Constants.ACTION_CREATE.equals(action)) {
			action = Constants.ACTION_EDIT;
			cstform.setAction(action);
			return (mapping.findForward("editCustomer"));
		}

		log.info(" Forwarding to success page !");
		return (mapping.findForward("success"));

	}

}
