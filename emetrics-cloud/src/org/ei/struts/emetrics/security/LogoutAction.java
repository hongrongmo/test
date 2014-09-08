package org.ei.struts.emetrics.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.customer.view.UserView;
import org.ei.struts.emetrics.service.EmetricsServiceImpl;
import org.ei.struts.framework.FrameworkBaseAction;



/**
 * This action is called when a user checks out of the emetrics
 * application.
 */
public class LogoutAction extends FrameworkBaseAction {
  public ActionForward executeAction( ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response )
    throws Exception {

    HttpSession session = request.getSession(false);
    // Make sure the user has a valid session
    if(session != null) {
      if(getUserContainer(request) != null) {
        // Login through the security service
        EmetricsServiceImpl serviceImpl = (EmetricsServiceImpl)this.getFrameworkService();
        serviceImpl.logout( ((UserView)(getUserContainer(request).getUserView())));

       	// Set authentication principal
        //getUserContainer(request).cleanUp();
        session.invalidate();
      }
    }
    return mapping.findForward( Constants.SUCCESS_KEY );
  }
}
