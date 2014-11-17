package org.ei.struts.emetrics.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.service.EmetricsServiceImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Form bean for the user signon page.
 */
public class CustomersForm extends ActionForm {

	protected static Log log = LogFactory.getLog(CustomersForm.class);

	/* filter fields*/
	private String[] products = new String[]{};
	private Map values = new HashMap();
	private Collection customers = new ArrayList();
	
	public void setValue(String key, Object value) {
		if(!(Constants.EMPTY_STRING.equals(value.toString().trim()))) { 
			values.put(key, value);
		}
	}

	public String[] getProducts()
	{
		return products;
	}
	public void setProducts(String[] prod)
	{
		products = prod;
		if(products != null) {
			setValue("products", products);
		}		
	}

	public Object getValue(String key) {
		return values.get(key);
	}
 
	public Map getValues() {
		return values;
	}

	public void setValues(Map aMap) {
		values.putAll(aMap);
	}

//	public Collection getCharlist() {
//		return EmetricsServiceImpl.getCharlist();
//	}

	public Collection getSalesRegions() {
		return EmetricsServiceImpl.getSalesRegions();
	}

	public Collection getStatuses() {
		return EmetricsServiceImpl.getStatuses();
	}

	public Collection getAllProducts() {
		return EmetricsServiceImpl.getProducts();
	}

	public Collection getAllAccess() {
		return EmetricsServiceImpl.getYesNoOption();
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
//	public ActionErrors validate(
//		ActionMapping mapping,
//		HttpServletRequest request) {
//		ActionErrors errors = new ActionErrors();
//
//		// Get access to the message resources for this application
//		// There's not an easy way to access the resources from an ActionForm
//		MessageResources resources =
//			(MessageResources) request.getAttribute(Globals.MESSAGES_KEY);
//
//		return errors;
//	}

	/**
	 * Reset all properties to their default values.
	 *
	 * @param mapping The mapping used to select this instance
	 * @param request The servlet request we are processing
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request) {
//		super.reset(mapping, request);

		this.products = new String[]{};
		this.customers = new ArrayList();
		this.values = new HashMap();
	}

	/**
	 * @return
	 */
	public Collection getCustomers() {
		return customers;
	}

	/**
	 * @param collection
	 */
	public void setCustomers(Collection collection) {
		customers = collection;
	}

}