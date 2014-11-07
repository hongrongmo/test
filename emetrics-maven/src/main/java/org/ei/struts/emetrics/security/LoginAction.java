package org.ei.struts.emetrics.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.customer.view.UserContainer;
import org.ei.struts.emetrics.customer.view.UserView;
import org.ei.struts.emetrics.service.EmetricsServiceImpl;
import org.ei.struts.framework.exceptions.DatastoreException;
import org.ei.struts.framework.service.IFrameworkService;
import org.ei.struts.framework.service.IFrameworkServiceFactory;

/**
 * Implements the logic to authenticate a user for the storefront application.
 */
public class LoginAction extends Action {

	protected IFrameworkService getFrameworkService()
		throws DatastoreException {
		IFrameworkServiceFactory factory =
			(IFrameworkServiceFactory) servlet
				.getServletContext()
				.getAttribute(
				Constants.SERVICE_FACTORY_KEY);
		return factory.createService();
	}
	/**
	* Commons Logging instance.
	*/
	protected static Log log = LogFactory.getLog("LoginAction");

	/**
	 * Called by the controller when the a user attempts to login to the
	 * storefront application.
	 */

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		// Get the user's login name and password. They should have already
		// validated by the ActionForm.
		String username = ((LoginForm) form).getUsername();
		String password = ((LoginForm) form).getPassword();

		// Login through the security service
		EmetricsServiceImpl serviceImpl =
			(EmetricsServiceImpl) getFrameworkService();
		UserView userView = null;

		try {
			// Attempt to log in
			userView = (UserView) serviceImpl.authenticate(username, password);
		} catch (Exception e) {
			((LoginForm) form).setPassword(Constants.EMPTY_STRING);
			ActionErrors errors = new ActionErrors();
			errors.add(
				ActionErrors.GLOBAL_ERROR,
				new ActionError("error.password.mismatch"));
			saveErrors(request, errors);
			return (mapping.getInputForward());
		}

		// Invalidate existing session if it exists
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		// Create a new session for this user
		session = request.getSession(true);

		// Store the UserView into the session and return
		UserContainer userContainer = new UserContainer();
		userContainer.setUserView(userView);
		userContainer.setLocale(request.getLocale());

		session.setAttribute(Constants.USER_CONTAINER_KEY, userContainer);

		log.info(" LOGGED IN! " + Constants.SUCCESS_KEY);
		log.info(" UserView " + userView);

		return mapping.findForward(Constants.SUCCESS_KEY);
	}
}