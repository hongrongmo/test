/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/item/EditItemAction.java-arc   1.1   Mar 25 2009 14:48:44   johna  $
 * $Revision:   1.1  $
 * $Date:   Mar 25 2009 14:48:44  $
 *
 */
package org.ei.struts.backoffice.contract.item;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.contract.notes.Note;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.1  $ $Date:   Mar 25 2009 14:48:44  $
 */

public final class EditItemAction extends BackOfficeBaseAction {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("EditItemAction");

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

		log.info(" action=" + action);

//		Validate the transactional control token
		 ActionErrors errors = new ActionErrors();

/*		 if (log.isTraceEnabled()) {
			 log.trace(" Checking transactional control token");
		 }
		 if (!isTokenValid(request)) {
			 errors.add(
				 ActionErrors.GLOBAL_ERROR,
				 new ActionError("error.transaction.token"));
		 }
		 resetToken(request);
*/
		// Populate the form
		if (form == null) {
			log.info(" Creating new entitlementForm bean under key " + mapping.getName());
			log.info(" in scope " + mapping.getScope());

			form = new ItemForm();
			if (Tokens.REQUEST.equals(mapping.getScope())) {
				request.setAttribute(mapping.getName(), form);
			} else {
				session.setAttribute(mapping.getName(), form);
			}
		}

		// Cast ActionForm into a contractForm
		ItemForm itemform = (ItemForm) form;
		itemform.setAction(action);

		Item item = null;
		if (Constants.ACTION_CREATE.equals(action)) {
			item = new Item();
		}

		if (Constants.ACTION_EDIT.equals(action)) {

			if (request.getParameter(Tokens.ITEMID) != null) {

				String strContractid = request.getParameter(Tokens.CONTRACTID);
				String strItemid = request.getParameter(Tokens.ITEMID);

				log.info(" Finding Item from request parameters : " + strContractid + ", " + strItemid );
				item = (new ItemDatabase()).findItem(strContractid, strItemid);
			}
			else
			{
			    // get the information from the itemForm on request
				String strContractid = itemform.getItem().getContract().getContractID();
				String strItemid = itemform.getItem().getItemID();

				log.info(" Finding Item from FORM parameters : " + strContractid + ", " + strItemid );
				item = (new ItemDatabase()).findItem(strContractid, strItemid);
			}
		}

		log.info(" Populating form from " + item);
		if (item != null) {
			try {
				PropertyUtils.copyProperties(itemform.getItem(), item);
				itemform.setNote(new Note());
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
		if (log.isTraceEnabled()) {
			log.trace(" Setting transactional control token");
		}
		saveToken(request);

		// Forward control to the edit contract page
		if (log.isTraceEnabled()) {
			log.trace(" Forwarding to 'success' page");
		}

		return (mapping.findForward(Tokens.SUCCESS));

	}

}
