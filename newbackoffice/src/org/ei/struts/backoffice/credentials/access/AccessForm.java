/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/access/AccessForm.java-arc   1.0   Jan 14 2008 17:10:34   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:34  $
 *
 * ====================================================================
 */
package org.ei.struts.backoffice.credentials.access;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.ValidatorForm;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.credentials.Credentials;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:34  $
 */

public class AccessForm extends ValidatorForm  {

	// ----------------------------------------------------- Instance Variables
	/**
	 * The maintenance action we are performing (Create or Edit).
	 */
	private String action = Constants.ACTION_CREATE;
	/**
	 * Return the maintenance action.
	 * Set the maintenance action.
	 */
	public String getAction() { return (this.action); }
	public void setAction(String action) { this.action = action; }

	private Credentials m_creds;
	public Credentials getCredentials() { return this.m_creds; }
	public void setCredentials(Credentials creds) { this.m_creds = creds; }

	// --------------------------------------------------------- Public Methods


  /**
   * Reset all properties to their default values.
   *
   * @param mapping The mapping used to select this instance
   * @param request The servlet request we are processing
   */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
	  this.action = Constants.ACTION_CREATE;
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

    // Perform validator framework validations
    ActionErrors errors = super.validate(mapping, request);
    MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );

    return errors;
  }


}

