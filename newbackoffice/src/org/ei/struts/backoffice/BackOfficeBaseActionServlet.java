/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/BackOfficeBaseActionServlet.java-arc   1.0   Jan 14 2008 17:10:22   johna  $
 * $Revision:   1.0  $
 * $Date:   Jan 14 2008 17:10:22  $
 *
 */
package org.ei.struts.backoffice;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionServlet;
import org.ei.struts.backoffice.contact.Contact;
import org.ei.struts.backoffice.contact.ContactDatabase;
import org.ei.struts.backoffice.contract.Contract;
import org.ei.struts.backoffice.contract.ContractDatabase;
import org.ei.struts.backoffice.customer.Customer;
import org.ei.struts.backoffice.customer.CustomerDatabase;
import org.ei.struts.backoffice.util.uid.UID;

/**
 * Extend the Struts ActionServlet to perform your own special
 * initialization.
 */
public class BackOfficeBaseActionServlet extends ActionServlet {

	private static Log log = LogFactory.getLog("BackOfficeBaseActionServlet");

	public void init() throws ServletException {

		log.info("Initializing...");
		// Make sure to always call the super's init(  ) first
		super.init();

		// Initialize the Environment

		try {

			// Test the Environment - try to create objects
			ContactDatabase contactDB = new ContactDatabase();
			Contact ctct = contactDB.createContact();
			ctct = null;

			ContractDatabase contractDB = new ContractDatabase();
			Contract ctrct = contractDB.createContract();
			ctrct = null;

			CustomerDatabase customerDB = new CustomerDatabase();
			Customer cust = customerDB.createCustomer();
			cust = null;

			// test generic sequence - UID class
			if(UID.getNextId() <= UID.getHighestId())
			{
    			throw new UnavailableException("GENERIC SEQUENCE OUT OF ORDER");
			}

		} catch (Exception ex) {

			// If there's a problem, disable the web app
			log.error("init() ", ex);

			throw new UnavailableException(ex.getMessage());

		}
		// store start time in milliseconds - for use in pools/uptime calculations
		getServletContext().setAttribute(
			Constants.SERVLET_STARTTIME_KEY,
			new Long(System.currentTimeMillis()));
		log.info("Done: " + new Date());

	}
}
