/*
 * $Author:   johna  $
 * $Revision:   1.17  $
 * $Date:   Apr 03 2006 15:20:22  $
 *
*/


package org.ei.struts.emetrics.actions;


import java.util.Locale;
import java.util.Stack;

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


public final class CustomerSwitchAction extends FrameworkBaseAction {


    // ----------------------------------------------------- Instance Variables


  /**
  * Commons Logging instance.
  */
	protected static Log log = LogFactory.getLog(CustomerSwitchAction.class);


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

		// Extract attributes we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession(false);

		ActionErrors errors = new ActionErrors();

		// Populate the form
		if (form == null) {
			log.info(" Creating new CustomerSwitch bean under key "	+ mapping.getName());
			form = new CustomerSwitchForm();
			request.setAttribute(mapping.getName(), form);
		}

		// Cast form into a customerSwitchForm
		CustomerSwitchForm cstform = (CustomerSwitchForm) form;

		if(!cstform.getAction().equalsIgnoreCase(Constants.ACTION_RETURN)) {

			// Get application service
			EmetricsServiceImpl serviceImpl = (EmetricsServiceImpl) getFrameworkService();

			UserContainer userContainer = getUserContainer(request);
			UserView userView = (UserView) userContainer.getUserView();

            // if user ids are the same, back button was used.
            if(userView.getCustomerId() == cstform.getCustomerId())
            {
    			return (mapping.findForward("reports"));
            }

			UserView switchView = serviceImpl.switchView(userView, cstform.getCustomerId());
			if(switchView == null){

				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.switch.denied", new Integer(cstform.getCustomerId())));
				saveErrors(request, errors);
				return (mapping.findForward("error"));
				//response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}

            // jam - hacked in parent only cosortium setting
            if(request.getParameter("parentonly") != null)
            {
                String ponly = request.getParameter("parentonly");
                if("true".equalsIgnoreCase(ponly))
                {
                    switchView.setParentonly(true);
                }
            }

			userContainer.setUserView(switchView);
			session.setAttribute(Constants.USER_CONTAINER_KEY, userContainer);

			Stack views = (Stack) session.getAttribute(Constants.SWITCH_VIEW_KEY);
			if(views == null) {
				views = new Stack();
			}
			views.push(userView);
			session.setAttribute(Constants.SWITCH_VIEW_KEY, views);

			return (mapping.findForward("reports"));
		}
		else {

			UserContainer userContainer = getUserContainer(request);

			Stack views = (Stack) session.getAttribute(Constants.SWITCH_VIEW_KEY);
			if(views == null) {
				log.error("Switch with no stack!!!");
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.switch.nostack"));
				saveErrors(request, errors);
				return (mapping.findForward("error"));
			}
			UserView userView = (UserView) views.pop();

			userContainer.setUserView(userView);
			session.setAttribute(Constants.USER_CONTAINER_KEY, userContainer);

			if(views.isEmpty()) {
				session.removeAttribute(Constants.SWITCH_VIEW_KEY);
			}
			else {
				session.setAttribute(Constants.SWITCH_VIEW_KEY, views);
			}

            if(session.getAttribute("PreviousSearchResults") != null) {
    			log.info("Putting CustomerForm from session object on request forward" + session.getAttribute("PreviousSearchResults") );
                request.setAttribute("PreviousSearchResults", session.getAttribute("PreviousSearchResults"));
            }


            // clean out report data from session when switching
            // No more caching report data in user's session object
            /*
            String lasthashid = (String) session.getAttribute(Constants.LAST_REPORT_ID_KEY);
            if(lasthashid != null) {
                session.removeAttribute(Constants.LAST_REPORT_ID_KEY);
                session.removeAttribute(lasthashid);
                session.removeAttribute(Constants.REPORT_DATA_KEY.concat(lasthashid));
            }
            */

			return (mapping.findForward("customers"));
		}

    }


}
