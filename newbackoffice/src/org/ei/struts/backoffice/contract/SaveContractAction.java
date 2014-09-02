/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/SaveContractAction.java-arc   1.1   Apr 16 2009 11:48:32   johna  $
 * $Revision:   1.1  $
 * $Date:   Apr 16 2009 11:48:32  $
 *
 * ====================================================================
 */

package org.ei.struts.backoffice.contract;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

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
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.contract.item.Item;
import org.ei.struts.backoffice.contract.item.ItemDatabase;
import org.ei.struts.backoffice.customeroptions.Options;
import org.ei.struts.backoffice.customeroptions.OptionsDatabase;
import org.ei.struts.backoffice.customeroptions.village.Village;
import org.ei.struts.backoffice.product.Product;
import org.ei.struts.backoffice.customeroptions.OptionConstants;

/**
 * Implementation of <strong>Action</strong> that validates and creates or
 * updates the user access information entered by the user.  If a new
 * access is created, the user is also implicitly logged on.
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.1  $ $Date:   Apr 16 2009 11:48:32  $
 */

public final class SaveContractAction
	extends BackOfficeBaseLookupDispatchAction {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("SaveContractAction");

	// --------------------------------------------------------- Public Methods
	protected Map getKeyMethodMap() {
		Map map = new HashMap();
		// (Application.Properties key, methodname)
		map.put("action.disable", "disable");
		map.put("action.enable", "enable");
		map.put("action.create", "create");
		map.put("action.edit", "edit");
		map.put("action.add", "add");
		return map;
	}

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
	public ActionForward disable(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		return flipEnabled(mapping, form, request, response);

	}
	public ActionForward enable(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		return flipEnabled(mapping, form, request, response);

	}

	private ActionForward flipEnabled(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		ContractForm contractform = (ContractForm) form;
		String action = contractform.getAction();

		ContractDatabase database = new ContractDatabase();
		String strcontractID = contractform.getContract().getContractID();
		Contract contract =	database.findContract(strcontractID);

		if (contract.getIsEnabled()) {

      Options opts = (new OptionsDatabase()).getOptionsData(strcontractID,contractform.getContract().getCustomerID(),Product.PROD_EV.getProductID());
      if(opts != null)
      {
        List optionslist = Arrays.asList(((Village)opts).getSelectedOptions());
        log.info(optionslist);
        if(optionslist.contains(OptionConstants.ZBF_Cartridge) ||
            optionslist.contains(OptionConstants.IBS_Cartridge) ||
            optionslist.contains(OptionConstants.BPE_Cartridge))
          {
            // cannot disable contract with Perpetual option error
        		// Validate the transactional control token
        		ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.contract.perpetual"));
            saveErrors(request, errors);
			      return (mapping.findForward("editContract"));
          }
      }

			contract.setEnabled(Constants.DISABLED);
		} else {
			contract.setEnabled(Constants.ENABLED);
		}
		database.saveContract(contract);

		//StringBuffer path = new StringBuffer();
		//path.append(mapping.findForward("editContract").getPath());
		//path.append("&").append(Tokens.CONTRACTID).append("=");
		//path.append(contract.getContractID());
		// Return a new forward based on stub+value
		//return new ActionForward(path.toString());
    return (mapping.findForward("editContract"));
  }

	public ActionForward edit(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		// Extract attributes and parameters we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();
		ContractForm contractform = (ContractForm) form;
		String action = contractform.getAction();
		if (action == null) {
			action = Constants.ACTION_CREATE;
		}
		ContractDatabase database = new ContractDatabase();
		log.debug("SaveContractAction:  Processing " + action + " action");

		// Was this transaction cancelled?
		if (isCancelled(request)) {
			log.trace(" Transaction '" + action + "' was cancelled");
			return (mapping.findForward("success"));
		}

		// Validate the transactional control token
		ActionErrors errors = new ActionErrors();

		log.trace(" Checking transactional control token");
		if (!isTokenValid(request)) {
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.transaction.token"));
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

		Contract contract = null;
		try {
			contract =
				database.findContract(
					contractform.getContract().getContractID());

			// update all items with values from
			// contract form
			// only end up here on EDIT actions
			PropertyUtils.copyProperties(contract, contractform.getContract());

			ItemDatabase itemDatabase = new ItemDatabase();
			Iterator itr = contract.getAllItems().iterator();
			while (itr.hasNext()) {
				Item item = (Item) itr.next();
				item.setContract(contract);
				itemDatabase.saveItem(item);
			}
		} catch (Exception e) {
			log.error("Database save", e);
		}

		// Remove the obsolete form bean
		if (mapping.getName() != null) {
			if ("request".equals(mapping.getScope())) {
				request.removeAttribute(mapping.getName());
			} else {
				session.removeAttribute(mapping.getName());
			}
		}

		// Forward control to the specified success URI
		if (log.isTraceEnabled()) {
			log.trace(" Forwarding to success page");
		}

		StringBuffer path = new StringBuffer();
		// Get stub URI from mapping (/do/whatever?paramName=)
		path.append(mapping.findForward("editContract").getPath());
		path.append("&contractid=");
		path.append((String) contract.getContractID());

		// Return a new forward based on stub+value
		return new ActionForward(path.toString());
	}

	public ActionForward create(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		// Extract attributes and parameters we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();
		ContractForm contractform = (ContractForm) form;
		String action = contractform.getAction();
		if (action == null) {
			action = Constants.ACTION_CREATE;
		}
		ContractDatabase database = new ContractDatabase();
		log.debug("SaveContractAction:  Processing " + action + " action");

		// Was this transaction cancelled?
		if (isCancelled(request)) {
			log.trace(" Transaction '" + action + "' was cancelled");
			return (mapping.findForward("success"));
		}

		// Validate the transactional control token
		ActionErrors errors = new ActionErrors();

		log.trace(" Checking transactional control token");
		if (!isTokenValid(request)) {
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.transaction.token"));
		}
		resetToken(request);

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			saveToken(request);
			return (mapping.getInputForward());
		}

		Contract contract = null;
		try {
			contract = database.createContract();
			PropertyUtils.copyProperties(contract, contractform.getContract());
			log.info("save contract " + contract);

			contract = database.saveContract(contract);
			if (contract == null) {
				log.error("Contract save");
			}

			ItemDatabase iDatabase = new ItemDatabase();
			Item item = iDatabase.createItem(contract.getContractID());
			item.setContract(contract);
			item.setProductID(contractform.getItem().getProductID());
			item.setNotes(contractform.getItem().getNotes());

			Options options =
				(new OptionsDatabase()).createOptions(item.getProductID());
			log.info(" SETTING DEFAULT OPTIONS " + options);
			if (options != null) {
				options.setContractID(contract.getContractID());
				options.setCustomerID(contract.getCustomerID());
				(new OptionsDatabase()).saveOptions(options);
			}

			iDatabase.saveItem(item);
		} catch (Exception e) {
			log.error("Database save", e);
		}

		// Remove the obsolete form bean
		if (mapping.getName() != null) {
			if ("request".equals(mapping.getScope())) {
				request.removeAttribute(mapping.getName());
			} else {
				session.removeAttribute(mapping.getName());
			}
		}

		// Forward control to the specified success URI
		if (log.isTraceEnabled()) {
			log.trace(" Forwarding to success page");
		}

		StringBuffer path = new StringBuffer();
		// Get stub URI from mapping (/do/whatever?paramName=)
		path.append(mapping.findForward("editContract").getPath());
		path.append("&").append(Tokens.CONTRACTID).append("=");
		path.append((String) contract.getContractID());

		// Return a new forward based on stub+value
		return new ActionForward(path.toString());
	}

	public ActionForward add(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		// Extract attributes and parameters we will need
		Locale locale = getLocale(request);
		HttpSession session = request.getSession();
		ContractForm contractform = (ContractForm) form;
		String action = contractform.getAction();

		// Was this transaction cancelled?
		if (isCancelled(request)) {
			log.trace(" Transaction '" + action + "' was cancelled");
			return (mapping.findForward("success"));
		}

		// Validate the transactional control token
		ActionErrors errors = new ActionErrors();

		log.trace(" Checking transactional control token");
		if (!isTokenValid(request)) {
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.transaction.token"));
		}
		resetToken(request);

		// Report any errors we have discovered back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			saveToken(request);
			return (mapping.getInputForward());
		}

		Item item = null;
		Contract contract = new Contract();
		try {
			ContractDatabase cDatabase = new ContractDatabase();
			ItemDatabase iDatabase = new ItemDatabase();

			contract =
				cDatabase.findContract(
					contractform.getContract().getContractID());

			// Validate the request parameters specified by the user
			if (log.isTraceEnabled()) {
				log.trace(" Performing extra validations");
			}

			log.info(
				"checking dupe item " + contractform.getItem().getProductID());
			if (contract.getContractItem(contractform.getItem().getProductID())
				!= null) {
				log.info(" DUPE = !!");
				errors.add(
					ActionErrors.GLOBAL_ERROR,
					new ActionError(
						"errors.contract.duplicate",
						contractform.getItem().getProduct().getName()));
			}

			// Report any errors we have discovered and return back to the original form
			if (!errors.isEmpty()) {
				saveErrors(request, errors);
				saveToken(request);

				StringBuffer path = new StringBuffer();
				// Get stub URI from mapping (/do/whatever?paramName=)
				path.append(mapping.findForward("editContract").getPath());
				path.append("&").append(Tokens.CONTRACTID).append("=");
				path.append((String) contract.getContractID());

				// Return a new forward based on stub+value
				return (new ActionForward(path.toString()));
			}

			item = iDatabase.createItem(contract.getContractID());
			item.setContract(contract);
			item.setProductID(contractform.getItem().getProductID());
			item.setNotes(contractform.getItem().getNotes());

			Options options =
				(new OptionsDatabase()).createOptions(item.getProductID());
			log.info(" SETTING DEFAULT OPTIONS " + options);
			if (options != null) {
				options.setContractID(contract.getContractID());
				options.setCustomerID(contract.getCustomerID());
				(new OptionsDatabase()).saveOptions(options);
			}

			iDatabase.saveItem(item);
			log.info("item saved " + item);
		} catch (Exception e) {
			log.error("Database save", e);
		}

		// Remove the obsolete form bean
		if (mapping.getName() != null) {
			if ("request".equals(mapping.getScope())) {
				request.removeAttribute(mapping.getName());
			} else {
				session.removeAttribute(mapping.getName());
			}
		}

		// Forward control to the specified success URI
		if (log.isTraceEnabled()) {
			log.trace(" Forwarding to success page");
		}

		StringBuffer path = new StringBuffer();
		// Get stub URI from mapping (/do/whatever?paramName=)
		path.append(mapping.findForward("editContract").getPath());
		path.append("&").append(Tokens.CONTRACTID).append("=");
		path.append((String) contract.getContractID());

		// Return a new forward based on stub+value
		return new ActionForward(path.toString());
	}
}
