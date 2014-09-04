/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/login/LoginAction.java-arc   1.1   Feb 26 2009 13:09:02   johna  $
 * $Revision:   1.1  $
 * $Date:   Feb 26 2009 13:09:02  $
 *
 */
package org.ei.struts.backoffice.login;

import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.user.User;
import org.ei.struts.backoffice.user.UserDatabase;
import org.ei.struts.backoffice.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;


/**
 * This Action is called by the ActionServlet when a login attempt
 * is made by the user. The ActionForm should be an instance of
 * a LoginForm and contain the credentials needed by the SecurityService.
 */
public class LoginAction extends Action {

  public ActionForward execute( ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response )
    throws Exception {

      // The ActionForward to return when completed
      User user = null;

      // Get the credentials from the LoginForm
      String username = ((LoginForm)form).getUsername();
      String password = ((LoginForm)form).getPassword();

      // Attempt to log in
      user = (new UserDatabase()).findUserByUsername(username);
      if((user != null)  && user.getPassword().equals(password) && (user.getIsEnabled() == Constants.ENABLED)) {
        // do nothing
        // login was successful
      }
      else {
        ((LoginForm) form).setPassword(Constants.EMPTY_STRING);
        ActionErrors errors = new ActionErrors();
        if(user != null &&  (user.getIsEnabled() != Constants.ENABLED)) {
          errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.accountdisabled"));
        }
        else {
          errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.password.mismatch"));
        }
        saveErrors(request, errors);
        return (mapping.getInputForward());
      }

      // Invalidate existing session if it exists
      HttpSession session = request.getSession(false);
      if(session != null) {
        session.invalidate(  );
      }

      // Create a new session for this user
      session = request.getSession(true);

      // Store the UserView into the session and return
      session.setAttribute(Constants.USER_KEY, user);

      return mapping.findForward(Tokens.SUCCESS);
  }

}