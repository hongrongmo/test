package org.ei.struts.backoffice.credentials.gateway;

import javax.servlet.http.HttpServletRequest;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.credentials.access.AccessForm;

public class GatewayForm extends AccessForm  {

	// -- ValidatorForm methods 
	public GatewayForm() { 
		setAction(Constants.ACTION_CREATE);
		setCredentials(new Gateway());
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		setAction(Constants.ACTION_CREATE);
		setCredentials(new Gateway());
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

	    MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );
	    ActionErrors errors = super.validate(mapping, request);

		Perl5Util perl = new Perl5Util();

		Gateway gateway = (Gateway) getCredentials();
		String strGatewayUrl = gateway.getGatewayUrl();
		
		// DO NOT test for valid URL - wildcards are allowed, i.e. '%'
		if((strGatewayUrl == null)  || strGatewayUrl.equals(Constants.EMPTY_STRING)) {
		  errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", resources.getMessage("prompt.gateway")));
		}
	    return errors;
	}
}
