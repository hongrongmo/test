/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/user/UserForm.java-arc   1.1   Feb 26 2009 11:20:06   johna  $
 * $Revision:   1.1  $
 * $Date:   Feb 26 2009 11:20:06  $
 *
 */
package org.ei.struts.backoffice.user;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.ResultList;
import org.ei.struts.backoffice.ResultListBase;
import org.ei.struts.backoffice.region.RegionDatabase;
import org.ei.struts.backoffice.role.RoleDatabase;
import org.apache.struts.util.LabelValueBean;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author $Author:   johna  $
 * @version $Revision:   1.1  $ $Date:   Feb 26 2009 11:20:06  $
 */
public final class UserForm extends ValidatorForm  {

	// ----------------------------------------------------- Instance Variables

  private String action = Constants.ACTION_CREATE;
  private User m_user = new User();
  // ----------------------------------------------------------- Properties

  public String getAction() { return (this.action); }
  public void setAction(String action) { this.action = action; }

  public User getUser() { return (this.m_user); }
  public void setUser(User user) { this.m_user = user; }

  // ---------------------------------------------------------

	public ResultList getAllRegions() {
		return new ResultListBase((new RegionDatabase()).getRegions());
	}

	public ResultList getAllRoles() {
		return new ResultListBase((new RoleDatabase()).getRoles());
	}

	public ResultList getAllUsers() {
		return new ResultListBase((new UserDatabase()).getUsers());
	}

  // ---------------------------------------------------------

  public Collection getAccessOptions() {

      Collection colAllStatus = new ArrayList();

      colAllStatus.add(new LabelValueBean("Yes", String.valueOf(Constants.ENABLED)));
      colAllStatus.add(new LabelValueBean("No", String.valueOf(Constants.DISABLED)));

      return (colAllStatus);
  }

  /**
   * Reset all properties to their default values.
   *
   * @param mapping The mapping used to select this instance
   * @param request The servlet request we are processing
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {

    this.action = Constants.ACTION_CREATE;
    this.m_user = new User();
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
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

    // Perform validator framework validations
    ActionErrors errors = super.validate(mapping, request);

    // Only need crossfield validations here
    //if (!m_strPassword.equals(m_strPassword)) {
        //errors.add("password2", new ActionError("error.password.match"));
    //}
    return errors;

  }


}
