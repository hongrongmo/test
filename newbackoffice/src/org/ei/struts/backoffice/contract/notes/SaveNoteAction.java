/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/contract/notes/SaveNoteAction.java-arc   1.0   Mar 25 2009 11:23:20   johna  $
 * $Revision:   1.0  $
 * $Date:   Mar 25 2009 11:23:20  $
 *
 */
package org.ei.struts.backoffice.contract.notes;

import java.util.Locale;

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
import org.ei.struts.backoffice.contract.Contract;
import org.ei.struts.backoffice.contract.ContractDatabase;
import org.ei.struts.backoffice.contract.item.ItemForm;
/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.0  $ $Date:   Mar 25 2009 11:23:20  $
 */

public final class SaveNoteAction extends BackOfficeBaseAction {

	// ----------------------------------------------------- Instance Variables

	/**
	 * The <code>Log</code> instance for this application.
	 */
	private static Log log = LogFactory.getLog("SaveNoteAction");


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

		// Extract attributes and parameters we will need
		Locale locale = getLocale(request);
		MessageResources messages = getResources(request);
		HttpSession session = request.getSession();
		ItemForm noteform = (ItemForm) form;
		String action = noteform.getAction();
		if (action == null) {
			action = Constants.ACTION_CREATE;
		}

		log.info("Processing " + action + " action");

		// Was this transaction cancelled?
		if (isCancelled(request)) {
			log.info(" Transaction '" + action + "' was cancelled");
			return (mapping.findForward("success"));
		}

		// Validate the transactional control token
		ActionErrors errors = new ActionErrors();

		if (log.isInfoEnabled()) {
			log.info(" Checking transactional control token");
		}
		if (!isTokenValid(request)) {
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.transaction.token"));
		}
		resetToken(request);

		// Report any errors we have discovered and return back to the original form
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			saveToken(request);
			log.error(" ERROR transactional control token");

			return (mapping.getInputForward());
		}

		Note note = null;
		try {
			NoteDatabase iDatabase = new NoteDatabase();
      if(Constants.ACTION_CREATE.equals(action)) {
        note = iDatabase.createNote();
      }
			String noteID = note.getNoteID();
      PropertyUtils.copyProperties(note, noteform.getNote());
			if(noteID != null) {
				note.setNoteID(noteID);
			}
			iDatabase.saveNote(note);
			log.info("note saved " + note);

		} catch (Exception e) {
			log.error("Database save", e);
		}

		// Remove the obsolete form bean
	  if (mapping.getName() != null) {
			if (Tokens.REQUEST.equals(mapping.getScope())) {
        log.info("request.removeAttribute " + mapping.getName());
				request.removeAttribute(mapping.getName());
			} else {
        log.info("session.removeAttribute " + mapping.getName());
				session.removeAttribute(mapping.getName());
			}
		}
		else {
      log.info("mapping.getName(): " + mapping.getName());
		}

		StringBuffer path = new StringBuffer();
		path.append(mapping.findForward("editItem").getPath());
		path.append("&").append(Tokens.CONTRACTID).append("=");
		path.append(note.getContractID());
		path.append("&").append(Tokens.ITEMID).append("=");
		path.append(note.getItemID());

		log.info(path.toString());

		// Return a new forward based on stub+value
    return new ActionForward(path.toString());
	}

}
