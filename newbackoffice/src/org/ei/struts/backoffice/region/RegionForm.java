/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/region/RegionForm.java-arc   1.0   Jan 14 2008 17:11:04   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:11:04  $
 *
 */
package org.ei.struts.backoffice.region;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

/**
 * Form bean for the user registration page.  This form has the following
 * fields, with default values in square brackets:
 * <ul>
 * <li><b>action</b> - The maintenance action we are performing (Create,
 *     Delete, or Edit).
 * <li><b>fromAddress</b> - The EMAIL address of the sender, to be included
 *     on sent messages.  [REQUIRED]
 * <li><b>fullName</b> - The full name of the sender, to be included on
 *     sent messages.  [REQUIRED]
 * <li><b>password</b> - The password used by this user to log on.
 * <li><b>password2</b> - The confirmation password, which must match
 *     the password when changing or setting.
 * <li><b>replyToAddress</b> - The "Reply-To" address to be included on
 *     sent messages.  [Same as from address]
 * <li><b>username</b> - The registered username, which must be unique.
 *     [REQUIRED]
 * </ul>
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:11:04  $
 */

public final class RegionForm extends ValidatorForm  {


    // ----------------------------------------------------- Instance Variables


	public RegionForm() {
	}

    /**
     * The maintenance action we are performing (Create or Edit).
     */
    private String action = "Create";

    private Region m_Region = null;

    // ----------------------------------------------------------- Properties

    /**
     * Return the maintenance action.
     */
    public String getAction() { return (this.action); }
    /**
     * Set the maintenance action.
     *
     * @param action The new maintenance action.
     */
    public void setAction(String action) { this.action = action; }


    public Region getRegion () { return m_Region; }
    public void setRegionName (Region region) { this.m_Region = region; }

    public Collection getRegions() { return (new RegionDatabase()).getRegions(); }

    // --------------------------------------------------------- Public Methods


    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        this.action = "Create";
		this.m_Region = new Region();
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

        return errors;

    }


}

