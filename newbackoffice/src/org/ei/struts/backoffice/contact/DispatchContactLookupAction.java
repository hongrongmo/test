package org.ei.struts.backoffice.contact;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.BackOfficeBaseLookupDispatchAction;
import org.ei.struts.backoffice.Constants;


/**
 * Implementation of <strong>Action</strong> that validates and creates or
 * updates the user contact information entered by the user.  If a new
 * contact is created, the user is also implicitly logged on.
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:26  $
 */

public final class DispatchContactLookupAction extends BackOfficeBaseLookupDispatchAction {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    private static Log log = LogFactory.getLog("DispatchContactLookupAction");


    protected Map getKeyMethodMap() {
        Map map = new HashMap();
        map.put("action.save", "save");
        map.put("action.delete", "delete");
        return map;
    }

/*
    public ActionForward execute(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response)
        throws Exception {
            
        return super.execute(mapping, form, request, response);        
        
    }
*/                      
                      
    public ActionForward save(ActionMapping mapping,
                            ActionForm form,
                            HttpServletRequest request,
                            HttpServletResponse response)
      throws Exception {

        log.info("SAVE");

		// Extract attributes and parameters we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();
		ContactForm cntctform = (ContactForm) form;
		String action = cntctform.getAction();
		if (action == null) {
		  action = Constants.ACTION_CREATE;
		}
		ContactDatabase database = new ContactDatabase() ;
        if (log.isDebugEnabled()) 
        {
	    	log.debug("SaveContactAction:  Processing " + action + " action");
		}

		// Was this transaction cancelled?
		if (isCancelled(request)) 
		{
			if (log.isTraceEnabled()) 
			{
	            log.trace(" Transaction '" + action + "' was cancelled");
            }
		    return (mapping.findForward("success"));
		}

		// Validate the transactional control token
		ActionErrors errors = new ActionErrors();

        if (log.isTraceEnabled()) 
        {
          log.trace(" Checking transactional control token");
        }

        if (!isTokenValid(request)) 
        {
          errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.transaction.token"));
        }
        resetToken(request);

		// Validate the request parameters specified by the user
	    if (log.isTraceEnabled()) 
	    {
	        log.trace(" Performing extra validations");
        }

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
    	    saveErrors(request, errors);
    	    saveToken(request);
    	    return (mapping.getInputForward());
        }

        Contact contact = null;
        try 
        {
          if (Constants.ACTION_CREATE.equals(action)) 
          {
            contact = database.createContact();
          }
          else 
          {
    		contact = database.findContact(cntctform.getContact().getContactID());
          }
    
            log.info("Database Contact object" + contact);

			String strContactID = contact.getContactID();
            PropertyUtils.copyProperties(contact, cntctform.getContact());
			if(strContactID != null) 
			{
				contact.setContactID(strContactID);
			}
			log.info("ContactDatabase save" + contact);
	        database.saveContact(contact);
        }
        catch (Exception e) 
        {
	        log.error("Database save", e);
	    }

		// Remove the obsolete form bean
		if (mapping.getAttribute() != null) 
		{
	        if ("request".equals(mapping.getScope()))
	            request.removeAttribute(mapping.getName());
            else
	            session.removeAttribute(mapping.getName());
        }

		// Forward control to the specified success URI
	    if (log.isTraceEnabled()) {
	        log.trace(" Forwarding to success page");
        }

		StringBuffer path = new StringBuffer();
		// Get stub URI from mapping (/do/whatever?paramName=)
		path.append(mapping.findForward("editCustomer").getPath());
		path.append("&customerid=");
		path.append((String) contact.getCustomerID());

		// Return a new forward based on stub+value
		return new ActionForward(path.toString());

    }
    
    public ActionForward delete(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
        throws Exception {

        log.info("DELETE");

        // Extract attributes and parameters we will need
        Locale locale = getLocale(request);
        MessageResources messages = getResources(request);
        HttpSession session = request.getSession();
        ContactForm cntctform = (ContactForm) form;
        String action = cntctform.getAction();
        if (action == null) {
            action = Constants.ACTION_DELETE;
        }
        ContactDatabase database = new ContactDatabase() ;
        if (log.isDebugEnabled()) {
            log.debug("Processing " + action + " action");
        }

        // Was this transaction cancelled?
        if (isCancelled(request)) {
            if (log.isTraceEnabled()) {
                log.trace(" Transaction '" + action + "' was cancelled");
            }
            return (mapping.findForward("success"));
        }

        // Validate the transactional control token
        ActionErrors errors = new ActionErrors();

        if (log.isTraceEnabled()) {
            log.trace(" Checking transactional control token");
        }
        if (!isTokenValid(request)) {
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.transaction.token"));
        }
        resetToken(request);

        // Validate the request parameters specified by the user
        if (log.isTraceEnabled()) {
            log.trace(" Performing extra validations");
        }

        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            saveToken(request);
            return (mapping.getInputForward());
        }

        Contact contact = null;
        try {
            contact = cntctform.getContact();

            log.info("Deleting Contact object: " + contact);
            boolean result = database.deleteContact(contact);
            log.info("Result = " + result);
        }
        catch (Exception e) {
            log.error("Database delete", e);
        }

        // Remove the obsolete form bean
        if (mapping.getAttribute() != null) {
            if ("request".equals(mapping.getScope()))
                request.removeAttribute(mapping.getName());
            else
                session.removeAttribute(mapping.getName());
        }

        // Forward control to the specified success URI
        if (log.isTraceEnabled()) {
            log.trace(" Forwarding to success page");
        }

        StringBuffer path = new StringBuffer();
        // Get stub URI from mapping (/do/whatever?paramName=)
        path.append(mapping.findForward("editCustomer").getPath());
        path.append("&customerid=");
        path.append((String) contact.getCustomerID());

        // Return a new forward based on stub+value
		return new ActionForward(path.toString());

    }

}
