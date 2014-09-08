package org.ei.struts.backoffice.credentials.access;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.credentials.Credentials;
import org.ei.struts.backoffice.credentials.CredentialsDatabase;
import org.ei.struts.backoffice.credentials.gateway.GatewayForm;
import org.ei.struts.backoffice.credentials.ip.IpForm;
import org.ei.struts.backoffice.credentials.username.UsernameForm;


public final class EditAccessAction extends BackOfficeBaseAction {

	// ----------------------------------------------------- Instance Variables
	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("EditAccessAction");

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

		if(Constants.ACTION_DELETE.equals(action)) {
			String accessid = request.getParameter(Tokens.ACCESS);
			String indexid = request.getParameter(Tokens.INDEXID);
			(new CredentialsDatabase()).deleteCredentials(accessid, indexid);
			// the ONLY reson this works is beacuse
			// the request with the proper parameters on it  
			// is sent to the following forward 
			return (mapping.findForward("editItem"));
		}

		log.info("Form action is " + action);

		Credentials creds = null;
		String strAccess = request.getParameter(Tokens.ACCESS);

		// Populate the form
		if (form == null) {
			log.info(" Creating new form under key " + mapping.getName());
			
			if(Constants.IP.equalsIgnoreCase(strAccess)) {
				form = new IpForm();
			}
			else if(Constants.GATEWAY.equalsIgnoreCase(strAccess)) {
				form = new GatewayForm();
			}
			else if(Constants.USERNAME.equalsIgnoreCase(strAccess)) {
				form = new UsernameForm();
			}
			
			if (Tokens.REQUEST.equals(mapping.getScope())) {
				request.setAttribute(mapping.getName(), form);
			} else {
				session.setAttribute(mapping.getName(), form);
			}
		}

		// Cast ActionForm into a OptionsForm
		AccessForm accessform = (AccessForm) form;
		accessform.setAction(action);

		if (Constants.ACTION_EDIT.equals(action)) {
			String strIndexID = request.getParameter(Tokens.INDEXID);
			creds = (new CredentialsDatabase()).getCredentialsData(strAccess, strIndexID);
		}
	    if(Constants.ACTION_CREATE.equals(action)) {
			creds = (new CredentialsDatabase()).createCredentials(strAccess);
			String strContractID = request.getParameter(Tokens.CONTRACTID);
			String strItemID = request.getParameter(Tokens.ITEMID);
			Access access = new Access();
			access.setContractID(strContractID);
			access.setItemID(strItemID);
			creds.setAccess(access);
		}
	
		if (creds != null) {
			log.info("Populating Form from " + creds);
			try {
				PropertyUtils.copyProperties(accessform.getCredentials(), creds);
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

		// Forward control to the edit customer page
		if (log.isTraceEnabled()) {
			log.trace(" Forwarding to 'success' page");
		}

		return (mapping.findForward(strAccess.toLowerCase()));

	}

}
