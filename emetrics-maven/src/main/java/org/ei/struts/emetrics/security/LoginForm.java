package org.ei.struts.emetrics.security;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

/**
 * Form bean for the user signon page.
 */
public class LoginForm extends ActionForm {
  private String password = "";
  private String username = "";
  private String requestpage = "";

    /**
     * The request attribute under which the path information is stored for
     * processing during a RequestDispatcher.include() call.
     */
    public static final String INCLUDE_PATH_INFO =
        "javax.servlet.include.path_info";


    /**
     * The request attribute under which the servlet path information is stored
     * for processing during a RequestDispatcher.include() call.
     */
    public static final String INCLUDE_SERVLET_PATH =
        "javax.servlet.include.servlet_path";



  /**
   * Public accessors and mutators
   */
  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return (this.username);
  }

  public String getPassword() {
    return (this.password);
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRequestpage() {
    return (this.requestpage);
  }

  public void setRequestpage(String requestpage) {
    this.requestpage = requestpage;
  }

  /**
   * Validate the properties that have been set from this HTTP request,
   * and return an <code>ActionErrors</code> object that encapsulates any
   * validation errors that have been found.  If no errors are found, return
   * <code>null</code> or an <code>ActionErrors</code> object with no
   * recorded error messages.
   *
   * @param mapping The mapping used to select this instance
   * @param request The servlet request we are processing
   */
  public ActionErrors validate(ActionMapping mapping,
                               HttpServletRequest request) {
    ActionErrors errors = new ActionErrors();

    // Get access to the message resources for this application
    // There's not an easy way to access the resources from an ActionForm
	MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );
	  
    if(getUsername() == null || getUsername().length() < 1) {
      errors.add( ActionErrors.GLOBAL_ERROR,
                  new ActionError("global.required", resources.getMessage( "label.username" ) ));
    }
    if(getPassword() == null || getPassword().length() < 1) {
      errors.add( ActionErrors.GLOBAL_ERROR,
                  new ActionError("global.required", resources.getMessage( "label.password" ) ));
    }
    if (requestpage == null || requestpage.length() == 0) {
    	// Identify the path component we will use to select a mapping
    	//String path = processPath(request);
    	this.requestpage = request.getServletPath();
	}
    return errors;
  }

  /**
   * Reset all properties to their default values.
   *
   * @param mapping The mapping used to select this instance
   * @param request The servlet request we are processing
   */
  public void reset(ActionMapping mapping,
                    HttpServletRequest request) {
    this.password = "";
    this.username = "";
	this.requestpage = "";
  }


}