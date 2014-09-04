package org.ei.struts.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.ei.struts.emetrics.Constants;
import org.ei.struts.emetrics.customer.view.*;
import org.ei.struts.framework.exceptions.DatastoreException;
import org.ei.struts.framework.service.IFrameworkService;
import org.ei.struts.framework.service.IFrameworkServiceFactory;
/**
 * An abstract Action class that all store front action classes should
 * extend.
 */
abstract public class FrameworkBaseAction extends Action {

	protected static Log log = LogFactory.getLog(FrameworkBaseAction.class);


	protected IFrameworkService getFrameworkService()
		throws DatastoreException {
		IFrameworkServiceFactory factory =
			(IFrameworkServiceFactory) getApplicationObject(Constants
				.SERVICE_FACTORY_KEY);
		return factory.createService();
	}

	/**
	 * Retrieve a session object based on the request and the attribute name.
	 */
	private Object getSessionObject(HttpServletRequest request, String attrName) {
		Object sessionObj = null;

		// Don't create a session if one isn't already present
		HttpSession session = request.getSession(false);
		if(session != null) {
			sessionObj = session.getAttribute(attrName);
		}
		
		return sessionObj;
	}


	/**
	 * Retrieve the UserContainer for the user tier to the request.
	 */
	protected UserContainer getUserContainer(HttpServletRequest request) {
		UserContainer userContainer =
			(UserContainer) getSessionObject(request,
				Constants.USER_CONTAINER_KEY);

//		// Create a UserContainer for the user if it doesn't exist already
//		if (userContainer == null) {
//			userContainer = new UserContainer();
//			userContainer.setLocale(request.getLocale());
//			HttpSession session = request.getSession();
//			session.setAttribute(Constants.USER_CONTAINER_KEY, userContainer);
//		}
		return userContainer;
	}

	/**
	 * Retrieve an object from the application scope by its name. This is
	 * a convience method.
	 */
	protected Object getApplicationObject(String attrName) {
		return servlet.getServletContext().getAttribute(attrName);
	}

	/* methods added by jm */
	/* every action will pass through here first - 
	 * this is where we check for login!
	 * 
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		log.info(" Checking login before forwarding request.");
		if (isLoggedIn(request)) {
			// It just calls a worker method that contains the real execute logic
			return executeAction(mapping, form, request, response);
		} else {
			return (mapping.findForward("login"));
		}
	}

	/* ALL SUBCLASSES implement this method */
	abstract public ActionForward executeAction(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception;

	public boolean isLoggedIn(HttpServletRequest request) {

		UserContainer userContainer = null;
		HttpSession session = request.getSession(true);

		if (session != null) {
			userContainer =
				(UserContainer) session.getAttribute(
					Constants.USER_CONTAINER_KEY);
		}

		log.info(
			" isLoggedIn "
				+ ((userContainer != null)
					? (userContainer.getUserView() != null)
					: false));

		return (
			(userContainer != null)
				? (userContainer.getUserView() != null)
				: false);
	}

}
