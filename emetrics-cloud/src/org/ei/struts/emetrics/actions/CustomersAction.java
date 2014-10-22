/*
 * $Author:   Zhun  $
 * $Revision:   1.11  $
 * $Date:   Mar 17 2005 12:21:40  $
 *
*/


package org.ei.struts.emetrics.actions;


import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.customer.view.UserContainer;
import org.ei.struts.emetrics.customer.view.UserView;
import org.ei.struts.emetrics.service.EmetricsServiceImpl;
import org.ei.struts.framework.FrameworkBaseAction;


public final class CustomersAction extends FrameworkBaseAction {


    // ----------------------------------------------------- Instance Variables
	/**
	 * Commons Logging instance.
	 */
	protected static Log log = LogFactory.getLog(CustomersAction.class);

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
     * @exception Exception if business logic throws an exception
     */
	public ActionForward executeAction(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
	throws Exception {

		//		Extract attributes we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();

		// Populate the form
		if (form == null) {
			log.info(" Creating new CustomersForm bean under key "	+ mapping.getName());
			form = new CustomersForm();
			if ("request".equals(mapping.getScope())) {
				request.setAttribute(mapping.getName(), form);
			} else {
				session.setAttribute(mapping.getName(), form);
			}
		}
        else
        {
            log.info(" ================ Form is not null " + form);
        }
        
		// Cast form into a CustomersForm
		CustomersForm cstform = (CustomersForm) form;

		if(request.getAttribute("PreviousSearchResults") != null) {
			cstform.setValues( (Map) request.getAttribute("PreviousSearchResults"));
		}

    	// Get application service
    	EmetricsServiceImpl serviceImpl = (EmetricsServiceImpl) getFrameworkService();

		// Validate the request parameters specified by the user
		ActionErrors errors = new ActionErrors();
//		try
//		{
			UserContainer userContainer = getUserContainer(request);
			UserView user = (UserView) userContainer.getUserView();

//		}
//		catch(Exception e)
//		{
//			log.info(" Failed to get user");
//			e.printStackTrace();
//			errors.add("loginError", new ActionError("error.password.mismatch"));
//		}

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
		    saveErrors(request, errors);
			return (mapping.findForward("error.noreportid"));
		}

//		int custId = cstform.getCustid();
//		String name = cstform.getName();
//		Map filter = cstform.getCustomerFilter();
		Map filter = cstform.getValues();
		if(!filter.isEmpty()) {

			Collection customers = serviceImpl.getCustomers(filter);
			if(customers.size() == 0) {
    		    errors.add("searchError", new ActionError("error.zero.results"));
    		    saveErrors(request, errors);
			}

			cstform.setCustomers(customers);

            session.setAttribute("PreviousSearchResults", cstform.getValues());
            log.info(" Put customersForm (search results) form on session "  + cstform.getValues() );
		}
		else
		{
            log.info(" customersForm filter is EMPTY ");
		}


//	    if (name == null && (firstchar != null && firstchar.length() != 0) ) {
//			name = firstchar+"%";
//		} else {
//			name = "%"+ name +"%";
//		}
//
//		if ((custId > 0) || (name != null && name.length() != 0) ) {
//	    	customers = serviceImpl.getCustomers(custId,name);
//
//			Iterator custIt = customers.iterator();
//			CustomerBO customer = null;
//			while (custIt.hasNext()) {
//				customer = (CustomerBO)custIt.next();
//				log.info("Customer: "+ customer.getCust_id() + "/"+customer.getName());
//			}
//		}

		// Forward control to the specified success URI
		return (mapping.findForward(Constants.SUCCESS_KEY));
    }

    // ------------------------------------------------------ Protected Methods
}
