package org.ei.struts.backoffice.customeroptions.localholdings;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.apache.struts.validator.ValidatorForm;
import org.ei.struts.backoffice.Constants;

public class LocalHoldingsForm extends ValidatorForm  {

	private String action = Constants.ACTION_CREATE;
	private LocalHoldings m_localholdings = new LocalHoldings();

	public LocalHoldings getLocalHoldings() { return m_localholdings; }
	public void setLocalHoldings(LocalHoldings localholdings) { m_localholdings = localholdings; }

	public String getAction() { return (this.action); }
	public void setAction(String action) { this.action = action; }

	// -- ValidatorForm methods
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		this.action = Constants.ACTION_CREATE;
		this.m_localholdings = new LocalHoldings();
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

	    MessageResources resources = (MessageResources) request.getAttribute( Globals.MESSAGES_KEY );
	    ActionErrors errors = super.validate(mapping, request);

	    if((getLocalHoldings().getLinkLabel() == null)  || (getLocalHoldings().getLinkLabel().equals(Constants.EMPTY_STRING)))
	    {
			errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required",resources.getMessage("prompt.linkLabel")));
		}

		if(
			((getLocalHoldings().getDynamicUrl() == null)  || (getLocalHoldings().getDynamicUrl().equals(Constants.EMPTY_STRING)))
		    &&
		    ((getLocalHoldings().getDefaultUrl() == null)  || (getLocalHoldings().getDefaultUrl().equals(Constants.EMPTY_STRING)))
		  )
		{
		  	errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.required",resources.getMessage("prompt.customerOptionURL")));
   		}

   		if(getLocalHoldings().getDynamicUrl().length()>500)
		{
		 	errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.customerOptionLength",resources.getMessage("prompt.dynamicUrl")));
		}

		if(getLocalHoldings().getDefaultUrl().length()>500)
		{
		 	errors.add( ActionErrors.GLOBAL_ERROR, new ActionError("errors.customerOptionLength",resources.getMessage("prompt.defaultUrl")));
		}
	    return errors;
	}
}
