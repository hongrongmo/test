/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/EditContractAction.java-arc   1.2   Apr 16 2009 11:47:26   johna  $
 * $Revision:   1.2  $
 * $Date:   Apr 16 2009 11:47:26  $
 *
 */
package org.ei.struts.backoffice.contract;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletException;
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
import org.ei.struts.backoffice.BackOfficeBaseAction;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.contract.item.Item;
import org.ei.struts.backoffice.contract.item.ItemDatabase;
import org.ei.struts.backoffice.credentials.Credentials;
import org.ei.struts.backoffice.credentials.CredentialsDatabase;
import org.ei.struts.backoffice.customeroptions.Options;
import org.ei.struts.backoffice.customeroptions.OptionsDatabase;
import org.ei.struts.backoffice.customeroptions.localholdings.LocalHoldings;
import org.ei.struts.backoffice.customeroptions.localholdings.LocalHoldingsDatabase;


/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.2  $ $Date:   Apr 16 2009 11:47:26  $
 */

/*
  4/16/2009

  The parameter sent to this action was incorrect so the ActionForm which is sent
  in as a parameter was empty.  The parameters need to match the syntax used on
  the ContractForm object which was contract.proprerty - so the set/get method could be
  used.  i.e. cotnract.contractID.

  It also prevents us from doing all kinds of weird getPath() & setting parameters on the URL
  when forwarding from an action! DUH.  How f'ing long did it take for me to realize this.

*/

public final class EditContractAction extends BackOfficeBaseAction {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("EditContractAction");

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
	public ActionForward executeAction(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		// Extract attributes we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();
		String action = request.getParameter(Tokens.ACTION);
		if (action == null) {
			action = Constants.ACTION_CREATE;
		}

		log.info(" action=" + action);

		// Validate the transactional control token
		ActionErrors errors = new ActionErrors();

		if (log.isTraceEnabled()) {
			log.trace(" Checking transactional control token");
		}
		if (!isTokenValid(request)) {
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.transaction.token"));
		}
		resetToken(request);

		if (form == null) {
			log.info(
				" Creating new form bean under key "
					+ mapping.getName()
					+ " in scope "
					+ mapping.getScope());

			form = new ContractForm();
			if (Tokens.REQUEST.equals(mapping.getScope())) {
				request.setAttribute(mapping.getName(), form);
			} else {
				session.setAttribute(mapping.getName(), form);
			}
		}
		else {
			log.info(
				" Using form bean under key "
					+ mapping.getName());
		}

		// Cast ActionForm into a contractForm
		ContractForm cntrctform = (ContractForm) form;
		cntrctform.setAction(action);

    Contract contract = new Contract();
    if (Constants.ACTION_CREATE.equals(action)) {

      if (request.getParameter(Tokens.CUSTOMERID) != null) {
        log.info(" Creating new Contract for CustomerID=" + request.getParameter(Tokens.CUSTOMERID));
        contract = new Contract();
        contract.setCustomerID(request.getParameter(Tokens.CUSTOMERID));
      } else {
        errors.add(
          Tokens.CUSTOMERID,
          new ActionError("error.username.unique", "blech"));
      }
      if (request.getParameter(Tokens.TYPEID) != null) {
        contract.setAccessType(request.getParameter(Tokens.TYPEID));
      }
    }

    if (Constants.ACTION_EDIT.equals(action)) {

      String strContractID = request.getParameter(Tokens.CONTRACTID);
      if(strContractID == null) {
        log.info( " Using ContractID from form ");
        strContractID = cntrctform.getContract().getContractID();
      }
      if (strContractID != null) {
        log.info( " Finding Contract: " + strContractID);
        contract = (new ContractDatabase()).findContract(strContractID);
      } else {
        // if no contract is specified, change action to list
        action = Constants.ACTION_LIST;
        cntrctform.setAction(action);
      }
    }

		if (Constants.ACTION_RENEW.equals(action)) {

			if (request.getParameter(Tokens.CONTRACTID) != null) {
				log.info(
					" RENEWING Contract: "
						+ request.getParameter(Tokens.CONTRACTID));

				Contract oldcontract =
					(new ContractDatabase()).findContract(
						request.getParameter(Tokens.CONTRACTID));

				contract = Contract.newInstance(oldcontract);
				contract.setStatus(Constants.STATUS_RENEWAL);
				contract.setRenewalRefID(oldcontract.getContractID());

				ItemDatabase itemdatabase = new ItemDatabase();
				ContractDatabase contractdatabase = new ContractDatabase();
				LocalHoldingsDatabase lhDatabase = new LocalHoldingsDatabase();
				CredentialsDatabase credsDatabase = new CredentialsDatabase();

				contractdatabase.saveContract(contract);

				log.info(" SAVED RENEWED contract " + contract);

				String[] itemids = oldcontract.getItemIDs();

				for(int x = 0; x < itemids.length; x++) {

					Item olditem = itemdatabase.findItem(oldcontract.getContractID(), itemids[x]);
					log.info(" COPYING item " + itemids[x]);

					Item item = Item.newInstance(olditem);
					item.setItemID(itemids[x]);
					item.setContract(contract);
					item.setNotes(olditem.getNotes());

					log.info(" SAVING item " + item);
					itemdatabase.saveItem(item);

					// Localholdings linked to this contract/item(product)
					Collection holdings = lhDatabase.getAllLocalHoldings(oldcontract.getContractID(), oldcontract.getCustomerID(), olditem);
					Iterator itrHoldings = holdings.iterator();
					while(itrHoldings.hasNext()) {
						LocalHoldings localholdings = (LocalHoldings) itrHoldings.next();
						LocalHoldings newholding = LocalHoldings.newInstance(localholdings);
						newholding.setItem(item);
						log.info(" COPIED localholding " + newholding);

						lhDatabase.saveLocalHoldings(newholding);
					}


					// Credentials/access methods linked to this contract/item(product)
					Collection creds = credsDatabase.getAllCredentialsData(oldcontract.getContractID(), oldcontract.getCustomerID(), olditem.getProductID());
					Iterator itrCreds = creds.iterator();
					while(itrCreds.hasNext()) {
						Credentials credentials = (Credentials) itrCreds.next();
						// delete old access record
						credsDatabase.deleteCredentials(credentials.getType(), credentials.getIndexID());
						// repopulate
						credentials.getAccess().setContractID(contract.getContractID());
						credentials.getAccess().setItemID(item.getItemID());
						credentials.setIndexID("-1");
						credsDatabase.saveCredentials(credentials);
						log.info(" COPYING credentials " + credentials);
					}

					// Product Options
					Options options = (new OptionsDatabase()).getOptionsData(oldcontract.getContractID(), oldcontract.getCustomerID(), olditem.getProductID());
					if (options != null) {
						log.info(" COPYING options " + options);
						options.setContractID(contract.getContractID());
						options.setCustomerID(contract.getCustomerID());
						(new OptionsDatabase()).saveOptions(options);
					}
				} // for


				oldcontract.setEnabled(Constants.DISABLED);
				oldcontract.setStatus(Constants.STATUS_CONVERTED);
				contractdatabase.saveContract(oldcontract);

				// update all the old items with the
				// old contract properties
				Iterator itr = oldcontract.getAllItems().iterator();
				while (itr.hasNext()) {
					Item item = (Item) itr.next();
					item.setContract(oldcontract);
					itemdatabase.saveItem(item);
				}


				// after renewing - fall through to action edit
				action = Constants.ACTION_EDIT;
				cntrctform.setAction(action);
			}

		}

		if (Constants.ACTION_LIST.equals(action)) {

			int offset;
			int length = 10;
			String pageOffset = request.getParameter(Tokens.OFFSET);
			if (pageOffset == null || pageOffset.equals("")) {
				offset = 0;
			} else {
				offset = Integer.parseInt(pageOffset);
			}

			request.setAttribute(Tokens.NEXT, new Integer(offset + length));
			request.setAttribute(Tokens.PREV, new Integer(offset - length));
			request.setAttribute(Tokens.OFFSET, new Integer(offset));
			request.setAttribute(Tokens.LENGTH, new Integer(length));

			return (mapping.findForward(Tokens.SUCCESS));
		}

		if (contract != null) {
			try {
				PropertyUtils.copyProperties(
					cntrctform.getItem(),
					(new Item()));

				PropertyUtils.copyProperties(
					cntrctform.getContract(),
					contract);
			} catch (InvocationTargetException e) {
				Throwable t = e.getTargetException();
				if (t == null) {
					t = e;
				}
				log.error("populate", t);
				throw new ServletException("populate", t);
			} catch (Throwable t) {
				log.error("populate", t);
				throw new ServletException("populate", t);
			}

		}

		// Set a transactional control token to prevent double posting
		if (log.isTraceEnabled()) {
			log.trace(" Setting transactional control token");
		}
		saveToken(request);

		// Forward control to the edit contract page
		if (log.isTraceEnabled()) {
			log.trace(" Forwarding to 'success' page");
		}

		if (request.getParameter(Tokens.FORWARD) != null) {
			return (mapping.findForward(Tokens.ASSIGN));
		}

		return (mapping.findForward(Tokens.SUCCESS));

	}

}
