/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/login/LoginForm.java-arc   1.0   Jan 14 2008 17:11:00   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:11:00  $
 *
 */
package org.ei.struts.backoffice.login;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.ValidatorForm;
import org.ei.struts.backoffice.Constants;

/**
 * This ActionForm is used by the online banking appliation to validate
 * that the user has entered an accessNumber and a pinNumber. If one or
 * both of the fields are empty when validate( ) is called by the
 * ActionServlet, error messages are created.
 */
public class LoginForm extends ValidatorForm {

  public LoginForm() {
    super();
  }

  private String m_strUsername = null;
  private String m_strPassword = null;

  public void setUsername(String username) { this.m_strUsername = username; }
  public String getUsername() { return m_strUsername; }

  public void setPassword(String password) { this.m_strPassword= password; }
  public String getPassword() { return m_strPassword; }



  /**
   * Called by the framework to validate the user has entered values in the
   * accessNumber and pinNumber fields.
   */
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request ){
    ActionErrors errors = new ActionErrors();

    // Get access to the message resources for this application.
    // There's no easy way to access the resources from an ActionForm.
    MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );

    // Check and see if the access number is missing.
    if(getUsername() == null || getUsername().equals(Constants.EMPTY_STRING)) {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", resources.getMessage("prompt.username")));
    }

    // Check and see if the password number is missing.
    if(getPassword() == null || getPassword().equals(Constants.EMPTY_STRING)) {
      errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", resources.getMessage("prompt.password")));
    }

    // Return the ActionErrors, if any.
    return errors;
  }

  /**
   * Called by the framework to reset the fields back to their default values.
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    // Clear out the fields.
    this.m_strUsername = null;
    this.m_strPassword= null;
  }


}
