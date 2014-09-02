/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/user/SaveUserAction.java-arc   1.0   Jan 14 2008 17:11:14   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:11:14  $
 *
 *
 */
package org.ei.struts.backoffice.user;


import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;


/**
 * Implementation of <strong>Action</strong> that validates and creates or
 * updates the user contact information entered by the user.  If a new
 * contact is created, the user is also implicitly logged on.
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:11:14  $
 */

public final class SaveUserAction extends BackOfficeBaseAction {


  // ----------------------------------------------------- Instance Variables


  /**
   * The <code>Log</code> instance for this application.
   */
  private static Log log = LogFactory.getLog("SaveUserAction");


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

    // Extract attributes and parameters we will need
    Locale locale = getLocale(request);
    MessageResources messages = getResources(request);
    HttpSession session = request.getSession();
    UserForm usrform = (UserForm) form;
    String action = usrform.getAction();
    if (action == null) {
        action = Constants.ACTION_CREATE;
    }

    UserDatabase database = new UserDatabase();
    if (log.isDebugEnabled()) {
      log.debug(" SaveUserAction:  Processing " + action + " action");
    }

    // Was this transaction cancelled?
    if (isCancelled(request)) {
      if (log.isTraceEnabled()) {
        log.trace(" Transaction '" + action + "' was cancelled");
      }
      return (mapping.getInputForward());
    }

    // Validate the transactional control token
    ActionErrors errors = new ActionErrors();

    if (log.isTraceEnabled()) {
      log.trace(" Checking transactional control token");
    }
    if (!isTokenValid(request)) {
      errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.transaction.token"));
    }
    resetToken(request);

    // Validate the request parameters specified by the user
    if (log.isTraceEnabled()) {
      log.trace(" Performing extra validations");
    }

    String value = null;
    value = usrform.getUser().getUsername();

    if ((Constants.ACTION_CREATE.equals(action)) && (database.findUserByUsername(value) != null)) {
      errors.add(Tokens.USERNAME, new ActionError("error.username.unique", usrform.getUser().getUsername()));
    }
    if (Constants.ACTION_CREATE.equals(action)) {
      value = usrform.getUser().getPassword();
      if ((value == null) || (value.length() <1)) {
        errors.add(Tokens.PASSWORD, new ActionError("error.password.required"));
      }
    }

    // Report any errors we have discovered back to the original form
    if (!errors.isEmpty()) {
      saveErrors(request, errors);
      saveToken(request);
      return (mapping.getInputForward());
    }

    try {
      User user = null;
      if (Constants.ACTION_CREATE.equals(action)) {
          user = database.createUser();
      }
      else {
        user = database.findUser(usrform.getUser().getSalesRepId());
      }
      String oldPassword = user.getPassword();
      PropertyUtils.copyProperties(user, usrform.getUser());
      if ((usrform.getUser().getPassword() == null) || (usrform.getUser().getPassword().length() < 1)) {
          user.setPassword(oldPassword);
      }

      log.info("Database save" + user.toString());
      database.saveUser(user);
    }
    catch (Exception e) {
        log.error("Database save", e);
    }

    // Remove the obsolete form bean
    if (mapping.getAttribute() != null) {
      if ("request".equals(mapping.getScope())) {
         request.removeAttribute(mapping.getAttribute());
      }
      else {
         session.removeAttribute(mapping.getAttribute());
      }
    }

    // Forward control to the specified success URI
    if (log.isTraceEnabled()) {
      log.trace(" Forwarding to success page");
    }

    return (mapping.findForward(Tokens.SUCCESS));

  }


}
