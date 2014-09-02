/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/login/LogoutAction.java-arc   1.0   Jan 14 2008 17:11:00   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:11:00  $
 *
 */


package org.ei.struts.backoffice.login;


import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.user.User;


public final class LogoutAction extends Action {


  private static Log log = LogFactory.getLog("LogoffAction");


  public ActionForward execute(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest request,
				 HttpServletResponse response)
  	  throws Exception {

  	// Extract attributes we will need
  	Locale locale = getLocale(request);
  	MessageResources messages = getResources(request);
  	HttpSession session = request.getSession();
  	User user = (User) session.getAttribute(Constants.USER_KEY);

  	// Process this user logoff
  	if (user != null) {
      if (log.isDebugEnabled()) {
         log.debug("LogoffAction: User '" + user.getUsername() + "' logged off in session " + session.getId());
      }
  	} else {
      if (log.isDebugEnabled()) {
          log.debug("LogoffActon: User logged off in session " + session.getId());
      }
  	}
  	session.removeAttribute(Constants.USER_KEY);
  	session.invalidate();

  	// Forward control to the specified success URI
  	return (mapping.findForward("success"));

  }


}
