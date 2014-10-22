/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/customer/EditCustomerAction.java-arc   1.0   Jan 14 2008 17:10:40   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:40  $
 *
 */
package org.ei.struts.backoffice.customer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
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
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.action.DynaActionFormClass;
import org.apache.struts.config.FormBeanConfig;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;

/**

 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:40  $
 */

public final class EditCustomerAction extends BackOfficeBaseAction {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("EditCustomerAction");

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

		// Extract attributes we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();
		String action = request.getParameter(Tokens.ACTION);
		if (action == null) {
			action = Constants.ACTION_CREATE;
		}

		// Validate the transactional control token
		ActionErrors errors = new ActionErrors();

		// Populate the form
		if (form == null) {
			log.info(" Creating new customerForm bean under key " + mapping.getName());

			form = new CustomerForm();
			if (Tokens.REQUEST.equals(mapping.getScope())) {
				request.setAttribute(mapping.getName(), form);
			} else {
				session.setAttribute(mapping.getName(), form);
			}
		}

		// Cast ActionForm into a customerForm
		CustomerForm cstform = (CustomerForm) form;
		cstform.setAction(action);

		Customer customer = new Customer();
		if (Constants.ACTION_CREATE.equals(action)) {
			log.info(" Creating new Customer ");
			customer = (new CustomerDatabase()).createCustomer();
		}

		log.info(" action =  " + action);

		if (Constants.ACTION_EDIT.equals(action)) {

			if (request.getParameter(Tokens.CONTACTS) != null) {
				cstform.setContacts(Boolean.valueOf(request.getParameter(Tokens.CONTACTS)).booleanValue());
				log.info(" setting contacts : " + cstform.getContacts());
			}

			if (request.getParameter(Tokens.CONSORTIUMS) != null) {
				cstform.setConsortiums(Boolean.valueOf(request.getParameter(Tokens.CONSORTIUMS)).booleanValue());
				log.info(" setting consortium = : " + cstform.getConsortiums());
			}
			String custid = request.getParameter(Tokens.CUSTOMERID);
			if (custid != null) {
				log.info(" Finding Customer: " + custid);
				customer = (new CustomerDatabase()).findCustomer(custid);
			} else if ((cstform.getCustomer() != null)	&& (cstform.getCustomer().getCustomerID() != null)) {
				custid = cstform.getCustomer().getCustomerID();
				log.info(" Finding Customer from Customer FORM: " + custid);
				customer = (new CustomerDatabase()).findCustomer(custid);
			} else {
				// if no customer is specified, change action to list
				action = Constants.ACTION_LIST;
				cstform.setAction(action);
			}
			if(customer == null) {
				errors.add(
					ActionErrors.GLOBAL_ERROR,
					new ActionError("error.customer.doesntexist",custid));	 
			}
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			saveToken(request);
			return (mapping.getInputForward());
		}

		if (Constants.ACTION_LIST.equals(action)) {

			int offset;
			int length = Constants.LIST_COUNT;
			String pageOffset = request.getParameter(Tokens.OFFSET);
			if (pageOffset == null	|| pageOffset.equals(Constants.EMPTY_STRING)) {
				offset = 0;
			} else {
				offset = Integer.parseInt(pageOffset);
			}

			if(cstform.getConsortiums() != true) {
				request.setAttribute(Tokens.NEXT, new Integer(offset + length));
				request.setAttribute(Tokens.PREV, new Integer(offset - length));
				request.setAttribute(Tokens.OFFSET, new Integer(offset));
				request.setAttribute(Tokens.LENGTH, new Integer(length));
			} 
			else {
				request.setAttribute(Tokens.NEXT, new Integer(-1));
				request.setAttribute(Tokens.PREV, new Integer(-1));
			}
			cstform.setMin(offset + 1);
			cstform.setMax(offset + length);

			if (request.getParameter(Tokens.FORWARD) != null) {

				FormBeanConfig fbc = (mapping.getModuleConfig()).findFormBeanConfig("navForm");
				DynaActionFormClass dynaClass =	DynaActionFormClass.createDynaActionFormClass(fbc);

				Map params = new HashMap();
				DynaActionForm daf = (DynaActionForm) dynaClass.newInstance();

				params.put(Tokens.OFFSET, Integer.toString(offset + length));
				params.put(Tokens.CUSTOMERID, request.getParameter(Tokens.CUSTOMERID));
				daf.set("params", params);
				request.setAttribute("navFormNext", daf);

				params = new HashMap();
				daf = (DynaActionForm) dynaClass.newInstance();

				params.put(Tokens.OFFSET, Integer.toString(offset - length));
				params.put(Tokens.CUSTOMERID, request.getParameter(Tokens.CUSTOMERID));
				daf.set("params", params);
				request.setAttribute("navFormPrev", daf);

				return (mapping.findForward(request.getParameter(Tokens.FORWARD)));
			}
			return (mapping.findForward(Tokens.SUCCESS));
		}

		if (customer != null) {
			log.info(" Populating CustomerForm from " + customer);
			try {
				PropertyUtils.copyProperties(cstform.getCustomer(), customer);
			} catch (InvocationTargetException e) {
				Throwable t = e.getTargetException();
				if (t == null) {
					t = e;
				}
				log.error("populate", t);
				throw new ServletException("populate", t);
			} catch (Throwable t) {
				log.error("Form.populate", t);
				throw new ServletException("populate", t);
			}
		}

		// Set a transactional control token to prevent double posting
		saveToken(request);

		return (mapping.findForward(Tokens.SUCCESS));

	}

}
