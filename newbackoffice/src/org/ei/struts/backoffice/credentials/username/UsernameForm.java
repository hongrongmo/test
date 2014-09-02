package org.ei.struts.backoffice.credentials.username;

import javax.servlet.http.HttpServletRequest;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.credentials.access.AccessForm;

public class UsernameForm extends AccessForm  {

	public UsernameForm() { 
		setAction(Constants.ACTION_CREATE);
		setCredentials(new Username());
	}

	// -- ValidatorForm methods 
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		setAction(Constants.ACTION_CREATE);
		this.setCredentials(new Username());
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

	    MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );
	    ActionErrors errors = super.validate(mapping, request);

		Perl5Util perl = new Perl5Util();

		Username username = (Username) getCredentials(); 
		String strUsername = username.getUsername();
		String strPassword = username.getPassword();

		if((strUsername == null)  || strUsername.equals(Constants.EMPTY_STRING)) {
		  errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", resources.getMessage("prompt.username")));
		}

		if((strPassword == null)  || strPassword.equals(Constants.EMPTY_STRING)) {
		  errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required", resources.getMessage("prompt.password")));
		}

		if((strPassword != null) && (strUsername != null) && (strUsername.equals(strPassword))) {
		  errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.cannotmatch"));
		}
        
	    return errors;
	}
}
