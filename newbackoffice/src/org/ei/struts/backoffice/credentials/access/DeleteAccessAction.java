/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/access/DeleteAccessAction.java-arc   1.0   Jan 14 2008 17:10:34   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:34  $
 *
 * ====================================================================
 */

package org.ei.struts.backoffice.credentials.access;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.credentials.Credentials;
import org.ei.struts.backoffice.credentials.CredentialsDatabase;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:34  $
 */

public final class DeleteAccessAction extends BackOfficeBaseAction {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("DeleteAccessAction");

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
		HttpSession session = request.getSession();

		log.info("hello");

		// Was this transaction cancelled?
		if (isCancelled(request)) {
			if (log.isTraceEnabled()) {
				log.trace(" Transaction was cancelled");
			}
			return (mapping.findForward("success"));
		}

		log.info("hell2");

		// Validate the transactional control token
		ActionErrors errors = new ActionErrors();

		// Validate the request parameters specified by the user
		if (log.isTraceEnabled()) {
			log.trace(" Performing extra validations");
		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		log.info("hell 33");

		Credentials creds = null;
		try {
			if (request.getParameter(Tokens.ACCESS) != null) {
				String strAccess = request.getParameter(Tokens.ACCESS);
				String strIndexID = request.getParameter(Tokens.INDEXID);

				creds = (new CredentialsDatabase()).getCredentialsData(strAccess, strIndexID);
				(new CredentialsDatabase()).deleteCredentials(strAccess, strIndexID);
			}
		} catch (Exception e) {
			log.error("Database delete", e);
		}

		// Remove the obsolete form bean
		if (mapping.getAttribute() != null) {
			if (Tokens.REQUEST.equals(mapping.getScope())) {
				request.removeAttribute(mapping.getAttribute());
			} else {
				session.removeAttribute(mapping.getAttribute());
			}
		}

		// Forward control to the specified success URI
		if (log.isTraceEnabled()) {
			log.trace(" Forwarding to success page");
		}

		StringBuffer path = new StringBuffer();

		String strContractID =  creds.getAccess().getContractID();
		String strItemID =  creds.getAccess().getItemID();
		log.info(" strContractID " + strContractID);
		log.info(" strItemID " + strItemID);
		
		if ((strItemID != null) && strContractID != null) {
			path.append(mapping.findForward("editItem").getPath());
			path.append("&").append(Tokens.CONTRACTID).append("=");
			path.append(strContractID);
			path.append("&").append(Tokens.ITEMID).append("=");
			path.append(strItemID);
		}

		return new ActionForward(path.toString());
	}

}
