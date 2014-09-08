/*
* $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contact/EditContactAction.java-arc   1.0   Jan 14 2008 17:10:26   johna  $
* $Revision:   1.0  $
* $Date:   Jan 14 2008 17:10:26  $
*
* ====================================================================
*/


package org.ei.struts.backoffice.contact;


import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;

/**
* Implementation of <strong>Action</strong> that populates an instance of
* <code>ContactForm</code> from the profile of the currently logged on
* User (if any).
*
* @author $Author:   johna  $
* @version $Revision:   1.0  $ $Date:   Jan 14 2008 17:10:26  $
*/

public final class EditContactAction extends BackOfficeBaseAction {


    // ----------------------------------------------------- Instance Variables


    /**
    * The <code>Log</code> instance for this application.
    */
    private static Log log = LogFactory.getLog("EditContactAction");


    // --------------------------------------------------------- Public Methods


    /**
    * Process the specified HTTP request, and create the corresponding HTTP
    * response (or forward to another web component that will create it).
    * Return an <code>ActionForward</code> instance describing where and how
    * control should be forwarded, or <code>null</code> if the response has
    * already been completed.
    *
    * @param mapping The ActionMapping used to select this instance
    * @param actionForm The optional ActionForm bean for this request (if any)
    * @param request The HTTP request we are processing
    * @param response The HTTP response we are creating
    *
    * @exception Exception if the application business logic throws
    *  an exception
    */
    public ActionForward executeAction(ActionMapping mapping,
    ActionForm form,
    HttpServletRequest request,
    HttpServletResponse response)
    throws Exception {

        // Extract attributes we will need
        Locale locale = getLocale(request);
        MessageResources messages = getResources(request);
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        if (action == null) {
            action = Constants.ACTION_CREATE;
        }
        log.info(" Creating new ContactForm bean under key "+ mapping.getAttribute());

        log.info("EditContactAction:  Processing " + action + " action");

        // Populate the contact form
        if (form == null) {
            if (log.isTraceEnabled()) {
                log.trace(" Creating new ContactForm bean under key "+ mapping.getAttribute());
            }
            form = new ContactForm();
            if ("request".equals(mapping.getScope())) {
                request.setAttribute(mapping.getName(), form);
            }
            else {
                session.setAttribute(mapping.getName(), form);
            }
        }
        ContactForm cntctform = (ContactForm) form;
        cntctform.setAction(action);

        ActionErrors errors = new ActionErrors();

        Contact contact = new Contact();
        if(Constants.ACTION_CREATE.equals(action)) {

            if(request.getParameter(Tokens.CUSTOMERID) != null) {
                log.info(" Creating new Contact for CustomerID=" + request.getParameter(Tokens.CUSTOMERID));
                contact = new Contact();
                contact.setCustomerID(request.getParameter(Tokens.CUSTOMERID));
            }

        }

        if (Constants.ACTION_EDIT.equals(action)) {

            if(request.getParameter("copyContactID") != null) {
                if(!Constants.EMPTY_STRING.equals(request.getParameter("copyContactID"))) {
                    log.info( " COPYING CONTACT " + request.getParameter("copyContactID"));
                    Contact copyContact = (new ContactDatabase()).findContact(request.getParameter("copyContactID"));
                    contact = Contact.copy(copyContact);
                }
                log.info( " SETTING ACTION TO CREATE");
                action = Constants.ACTION_CREATE;
                cntctform.setAction(action);
                cntctform.setCopyContactID(Constants.EMPTY_STRING);

            }
            else if(request.getParameter(Tokens.CONTACTID) != null) {

                log.info(" Finding Contact: " + request.getParameter(Tokens.CONTACTID));
                contact = (new ContactDatabase()).findContact(request.getParameter(Tokens.CONTACTID));
            }
            else {
                // if no contract is specified, change action to list
                action = Constants.ACTION_LIST;
                cntctform.setAction(action);
            }
        }

        if(Constants.ACTION_LIST.equals(action)) {

            int offset;
            int length = 10;
            String pageOffset = request.getParameter(Tokens.OFFSET);
            if (pageOffset == null || pageOffset.equals(Constants.EMPTY_STRING)) {
                offset = 0;
            } else {
                offset = Integer.parseInt(pageOffset);
            }

            request.setAttribute(Tokens.NEXT, new Integer(offset+length));
            request.setAttribute(Tokens.PREV, new Integer(offset-length));
            request.setAttribute(Tokens.OFFSET, new Integer(offset));
            request.setAttribute(Tokens.LENGTH, new Integer(length));

            return (mapping.findForward("success"));
        }

        log.info(" Populating ContactForm from " + contact);
        if(contact != null) {
            try {
                PropertyUtils.copyProperties(cntctform.getContact() , contact);
            }
            catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                if (t == null) {
                    t = e;
                }
                log.error("populate", t);
                throw new ServletException("populate", t);
            }
            catch (Throwable t) {
                log.error("Form.populate", t);
                throw new ServletException("populate", t);
            }
        }


        // Set a transactional control token to prevent double posting
        if (log.isTraceEnabled()) {
            log.trace(" Setting transactional control token");
        }
        saveToken(request);

        // Forward control to the edit user contact page
        if (log.isTraceEnabled()) {
            log.trace(" Forwarding to 'success' page");
        }

        return (mapping.findForward(Tokens.SUCCESS));

    }


}
