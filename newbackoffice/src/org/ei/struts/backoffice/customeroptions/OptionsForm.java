package org.ei.struts.backoffice.customeroptions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.ValidatorForm;
import org.ei.struts.backoffice.Constants;

public class OptionsForm extends ValidatorForm  {

	private String action = Constants.ACTION_CREATE;
	private Options m_options;
	
	public String getAction() { return (this.action); }
	public void setAction(String action) { this.action = action; }

	public Options getOptions() { return m_options; }
	public void setOptions(Options options) { m_options = options; }

	// -- ValidatorForm methods 
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		this.action = Constants.ACTION_CREATE;
	}
	
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

	    MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );
	    ActionErrors errors = super.validate(mapping, request);

	    return errors;
	}
}
