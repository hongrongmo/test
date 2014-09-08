/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/region/EditRegionAction.java-arc   1.0   Jan 14 2008 17:11:04   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:11:04  $
 *
 * ====================================================================
 */


package org.ei.struts.backoffice.region;


import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.BackOfficeBaseAction;

/**
 * Implementation of <strong>Action</strong> that populates an instance of
 * <code>ContactForm</code> from the profile of the currently logged on
 * User (if any).
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:11:04  $
 */

public final class EditRegionAction extends BackOfficeBaseAction {


    // ----------------------------------------------------- Instance Variables


    /**
     * The <code>Log</code> instance for this application.
     */
  private static Log log = LogFactory.getLog("EditRegionAction");


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
    public ActionForward executeAction(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
	throws Exception {

		// Extract attributes we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();
		String action = request.getParameter("action");
		if (action == null) {
			action = "Create";
		}
        log.info(" Creating new RegionForm bean under key "+ mapping.getAttribute());

        if (log.isDebugEnabled()) {
            log.debug("RegionAction:  Processing " + action + " action");
        }

		// Populate the user region form
		if (form == null) {
            if (log.isTraceEnabled()) {
                log.trace(" Creating new RegionForm bean under key "+ mapping.getAttribute());
            }
	    	form = new RegionForm();
            if ("request".equals(mapping.getScope()))
                request.setAttribute(mapping.getAttribute(), form);
            else
                session.setAttribute(mapping.getAttribute(), form);
		}

		/*
		if (user != null) {
            if (log.isTraceEnabled()) {
                log.trace(" Populating form from " + user);
            }
            try {
                PropertyUtils.copyProperties(regform, user);
                regform.setAction(action);
                regform.setPassword(null);
                regform.setPassword2(null);
            } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                if (t == null)
                    t = e;
                log.error("ContactForm.populate", t);
                throw new ServletException("ContactForm.populate", t);
            } catch (Throwable t) {
                log.error("ContactForm.populate", t);
                throw new ServletException("ContactForm.populate", t);
            }
		}
		*/

        // Set a transactional control token to prevent double posting
        if (log.isTraceEnabled()) {
            log.trace(" Setting transactional control token");
        }
        saveToken(request);

		// Forward control to the edit user contact page
        if (log.isTraceEnabled()) {
            log.trace(" Forwarding to 'success' page");
        }

		return (mapping.findForward("success"));

    }


}
