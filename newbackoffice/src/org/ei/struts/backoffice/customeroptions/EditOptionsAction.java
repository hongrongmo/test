package org.ei.struts.backoffice.customeroptions;

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
import org.ei.struts.backoffice.customeroptions.dds.DDSForm;
import org.ei.struts.backoffice.customeroptions.village.cv.CvForm;
import org.ei.struts.backoffice.customeroptions.village.enc2.Enc2Form;
import org.ei.struts.backoffice.customeroptions.village.ev2.Ev2Form;
import org.ei.struts.backoffice.customeroptions.village.pv2.Pv2Form;

public final class EditOptionsAction extends BackOfficeBaseAction {

	// ----------------------------------------------------- Instance Variables
	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("EditOptionsAction");

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
		log.info("Form action is " + action);

		Options options = null;
		String productid = request.getParameter(Tokens.PRODUCTID);
		
		// Populate the form
		if (form == null) {
			log.info(" Creating new form under key " + mapping.getName());

			if(Constants.DDS.equalsIgnoreCase(productid)) {
				form = new DDSForm();
			}
			else if(Constants.EV2.equalsIgnoreCase(productid)) {
				form = new Ev2Form();
			}
			else if(Constants.CV.equalsIgnoreCase(productid)) {
				form = new CvForm();
			}
			else if(Constants.PV2.equalsIgnoreCase(productid)) {
				form = new Pv2Form();
			}
			else if(Constants.ENC2.equalsIgnoreCase(productid)) {
				form = new Enc2Form();
			}
					
			if (Tokens.REQUEST.equals(mapping.getScope())) {
				request.setAttribute(mapping.getName(), form);
			} else {
				session.setAttribute(mapping.getName(), form);
			}
		}

		// Cast ActionForm into a OptionsForm
		OptionsForm optionsform = (OptionsForm) form;
		optionsform.setAction(action);

		String contractid  = request.getParameter(Tokens.CONTRACTID);
		String customerid = request.getParameter(Tokens.CUSTOMERID);

		if (Constants.ACTION_EDIT.equals(action)) {
			options = (new OptionsDatabase()).getOptionsData(contractid, customerid, productid);

			if(options == null) {
				action = Constants.ACTION_CREATE; 
			}
		}
		if(Constants.ACTION_CREATE.equals(action)) {
			options = (new OptionsDatabase()).createOptions(productid);

			if(options != null){
				options.setContractID(contractid);
				options.setCustomerID(customerid);
				(new OptionsDatabase()).saveOptions(options);
			}
			
		}

		if (options != null) {
			log.info("Populating Form from " + options);
			try {
				PropertyUtils.copyProperties(optionsform.getOptions(), options);
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

		return (mapping.findForward(productid.toLowerCase()));

	}

}