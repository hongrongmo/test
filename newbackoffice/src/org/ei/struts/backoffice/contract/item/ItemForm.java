/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/item/ItemForm.java-arc   1.2   Mar 25 2009 14:48:44   johna  $
 * $Revision:   1.2  $
 * $Date:   Mar 25 2009 14:48:44  $
 *
 */
package org.ei.struts.backoffice.contract.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.ImageButtonBean;
import org.apache.struts.validator.ValidatorForm;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.contract.status.StatusDatabase;
import org.ei.struts.backoffice.product.ProductDatabase;
import org.ei.struts.backoffice.contract.notes.Note;
/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.2  $
 */

public final class ItemForm extends ValidatorForm {

	/**
	  * The maintenance action we are performing (Create or Edit).
	  */
	private String action = null; // = Constants.ACTION_CREATE;
	private Item m_Item = new Item();
	private Note m_note = new Note();

	private ImageButtonBean saveAndReturn = new ImageButtonBean();
	private ImageButtonBean saveAndGotoOptions = new ImageButtonBean();

	// ----------------------------------------------------------- Properties

	public void setSaveAndReturn(ImageButtonBean button) {
		this.saveAndReturn = button;
	}
	public ImageButtonBean getSaveAndReturn() {
		return this.saveAndReturn;
	}

	public void setSaveAndGotoOptions(ImageButtonBean button) {
		this.saveAndGotoOptions = button;
	}
	public ImageButtonBean getSaveAndGotoOptions() {
		return this.saveAndGotoOptions;
	}

	/**
	 * Return the maintenance action.
	 */
	//public String getAction() { return (this.action); }
	//public String getSelected() {
	public String getAction() {
		return action;
	}

	/**
	* Set the maintenance action.
	*
	* @param action The new maintenance action.
	*/
	public void setAction(String action) {
		this.action = action;
	}

	// ----------------------------------------------------------- Properties

	public Item getItem() {
		return m_Item;
	}
	public void setItem(Item item) {
		m_Item = item;
	}

	public Note getNote() {
		return m_note;
	}
	public void setNote(Note note) {
		m_note = note;
	}

  public String getAbbrevNotes() {
    String notes = m_Item.getNotes();
    if((notes != null) && notes.length() > 160) {
      notes = notes.substring(0,160) + "...";
    }
    return notes;
  }

	// -----------------------------------------------------------
	private Map makeParams() {

		Map params = new HashMap();
		params.put(Tokens.ITEMID, getItem().getItemID());
		params.put(Tokens.CONTRACTID, getItem().getContract().getContractID());
		return params;

	}

	public Map getCreateLocalHoldings() {

		Map params = makeParams();
		params.put(Tokens.CUSTOMERID, getItem().getContract().getCustomerID());
		return params;
	}

	public Map getCreateIpAccess() {

		Map params = makeParams();
		params.put(Tokens.ACCESS, Constants.IP);
		return params;
	}
	public Map getCreateUsernameAccess() {

		Map params = makeParams();
		params.put(Tokens.ACCESS, Constants.USERNAME);
		return params;
	}
	public Map getCreateGatewayAccess() {

		Map params = makeParams();
		params.put(Tokens.ACCESS, Constants.GATEWAY);
		return params;
	}

	// -----------------------------------------------------------

	public Collection getAllRestrictions() {
		return (new StatusDatabase()).getStatus();
	}

	public Collection getAllProducts() {
		return (new ProductDatabase()).getProducts();
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Reset all properties to their default values.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		//this.action = Constants.ACTION_CREATE;
		this.action = null;
		this.m_Item = new Item();
    this.m_note = new Note();
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
	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {

		// Perform validator framework validations
		ActionErrors errors = super.validate(mapping, request);

		// Only need crossfield validations here
		//if (!m_strPassword.equals(m_strPassword)) {
		//errors.add("password2", new ActionError("error.password.match"));
		//}
		return errors;

	}

}
