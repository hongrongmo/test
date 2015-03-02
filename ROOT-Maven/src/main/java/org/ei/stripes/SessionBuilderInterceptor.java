package org.ei.stripes;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.controller.IPBlocker;
import org.ei.controller.IPBlocker.COUNTER;
import org.ei.session.SessionManager;
import org.ei.session.UserSession;
import org.ei.stripes.action.BackUrlAction;
import org.ei.stripes.action.CaptchaAction;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.WebAnalyticsEventProperties;
import org.ei.stripes.action.personalaccount.LogoutAction;
import org.ei.stripes.exception.EVExceptionHandler;
import org.ei.stripes.util.HttpRequestUtil;
import org.ei.web.analytics.GoogleWebAnalyticsEvent;
import org.ei.web.util.RequestDumper;
import org.perf4j.log4j.Log4JStopWatch;

/**
 * This class is a Stripes Interceptor that is supposed to
 * build a session instance for the current request.  It
 * MUST run before the session is needed (ActionBeans or
 * other interecptors).
 *
 * For the reason above, this Interceptor should NOT issue
 * redirects but should forward directly to JSPs to avoid
 * ActionBeans that would require a session.
 *
 * @author harovetm
 *
 */
@Intercepts(LifecycleStage.BindingAndValidation)
public class SessionBuilderInterceptor implements Interceptor {

	private Logger log4j = Logger.getLogger(SessionBuilderInterceptor.class);

	public static final String REQUEST_ERROR_TITLE = "errorTitle";
	public static final String REQUEST_ERROR_TEXT = "errorText";

	@Override
	public Resolution intercept(ExecutionContext executioncontext) throws Exception {

		Log4JStopWatch stopwatch = new Log4JStopWatch("Interceptor.SessionBuilder");

		EVActionBeanContext context = (EVActionBeanContext) executioncontext.getActionBeanContext();
		HttpServletRequest request = context.getRequest();
		HttpServletResponse response = context.getResponse();
		EVActionBean actionbean = (EVActionBean) executioncontext.getActionBean();
		IAccessControl accesscontrol= null;

		String ipaddress = HttpRequestUtil.getIP(request);
		log4j.info("[" + ipaddress + "] Starting intercept...");

        // *****************************************************
        // Check for maintenance mode
        // *****************************************************
		String maintenancemode = EVProperties.getProperty(EVProperties.MAINTENANCE_MODE);
        if (!GenericValidator.isBlankOrNull(maintenancemode) && Boolean.parseBoolean(maintenancemode)) {
            log4j.warn("Application is in MAINTENANCE MODE!");
            return new ForwardResolution("/WEB-INF/pages/world/maintenance.jsp");
        }

        // *****************************************************
        // Ensure the application has been initialized
        // *****************************************************
        if (!EVInitializationListener.isInitialized()) {
            log4j.warn("[" + ipaddress + "] Application is NOT initialized, going to maintenance page!");
            return new ForwardResolution("/WEB-INF/pages/world/maintenance.jsp");
        }

		if (actionbean instanceof ISecuredAction) {
			accesscontrol = ((ISecuredAction) actionbean).getAccessControl();
			if (accesscontrol instanceof WorldAccessControl) {
				return executioncontext.proceed();
			}
		}

		// Add the release version number to the request for JSPs
        request.setAttribute("releaseversion", EVProperties.getApplicationProperties().getProperty(ApplicationProperties.RELEASE_VERSION));
        // Add the contact us link to the request for all JSPs
        request.setAttribute("contactuslink", EVProperties.getApplicationProperties().getProperty(ApplicationProperties.CONTACT_US_LINK));

		// *****************************************************
		// Check for IP and Session Blocks
		// *****************************************************
		try {
			IPBlocker ipBlocker = IPBlocker.getInstance();
			if (ipBlocker.isBlocked(ipaddress)) {
				request.setAttribute(REQUEST_ERROR_TITLE, "IP Address Blocked");
				request.setAttribute(REQUEST_ERROR_TEXT, "Your IP address, '" + ipaddress + "' has been blocked from accessing Engineering Village.  Please contact your institution for more details.");
				return new ErrorResolution(HttpServletResponse.SC_BAD_REQUEST);
			}else if(ipBlocker.isRequestPerSessionRateExceeded(request)){
				if (!(actionbean instanceof CaptchaAction)) {
					return new ForwardResolution("/captcha/display.url");
				}else{
					log4j.info("Skipping to captcha handling");
		            return executioncontext.proceed();
				}
			}
		} catch (Exception e) {
			EVExceptionHandler.logException("Unable to verify IP: ", e, request);
			throw new RuntimeException("PreAuth failed during IPBlocker check!", e);
		}

		// *****************************************************
		// Update request counter into memcache
		// *****************************************************
		IPBlocker.getInstance().increment(ipaddress, COUNTER.REQUEST);


		if (actionbean instanceof ISecuredAction) {
			accesscontrol = ((ISecuredAction) actionbean).getAccessControl();
			if (accesscontrol instanceof WorldAccessControl) {
				return executioncontext.proceed();
			}
		}

        // *****************************************************
		// Check the User-Agent. If "PEAR", end request
		// *****************************************************
		String uagent = request.getHeader("User-Agent");
		if (uagent != null && uagent.indexOf("PEAR") > -1) {
			log4j.warn("[" + ipaddress + "] User-Agent is: '" + uagent + "', ending request");
			request.setAttribute(REQUEST_ERROR_TITLE, "Invalid User Agent");
			request.setAttribute(REQUEST_ERROR_TEXT, "The User-Agent value, '" + uagent + "' is invalid.");
			return new ErrorResolution(HttpServletResponse.SC_BAD_REQUEST);
		}

		// *****************************************************
        // Special handling for logout - no need to continue if user
        // is requesting LogOut!
		// *****************************************************

		if (actionbean == null) {
			throw new RuntimeException("No ActionBean is attached to request!");
		}

		// ****************************************************
		// Now attempt to build a session!
		// ****************************************************
		SessionManager sessionmanager = new SessionManager(request, response);
		UserSession usersession = sessionmanager.getUserSession();

        context.setUserSession(usersession);
        if (actionbean instanceof LogoutAction) {
            return ((LogoutAction)actionbean).process();
        }
		// See if the session has expired
//		if (usersession.getStatus().equals(SessionStatus.NEW_HAD_EXPIRED)) {
//            String path = request.getRequestURI();
//			log4j.info("[" + ipaddress + "] Session marked as NEW_HAD_EXPIRED, path='" + path + "'");
//			if (request.getParameter(SessionManager.REQUEST_PT) != null ||
//				GenericValidator.isBlankOrNull(path) ||
//				path.contains("home.url") ||
//				path.contains("CID=home")) {
//				usersession.setProperty(UserSession.SESSION_END_REDIR, "no");
//			} else {
//				// let the request process and authInterceptor will take care the right landing page logic
//	            log4j.info("[" + ipaddress + "] Redirecting to Home page");
//				usersession.setProperty(UserSession.SESSION_END_REDIR, "yes");
//				return new RedirectResolution(EVPathUrl.EV_HOME.value() + "?redir=t", false);
//			}
//
//		}

		// *****************************************************
		// Print out request information
		// *****************************************************
		log4j.warn("********* Servicing request **********" + EVProperties.NEWLINE + HttpRequestUtil.prettyPrintRequest(request, EVProperties.NEWLINE));
		if (log4j.isInfoEnabled()) {
			RequestDumper.dump(request, "params,headers");
			log4j.info("client IP for this request is==================================="
					+ request.getRemoteAddr()
					+ "=======Host======="
					+ request.getRemoteHost());
		}

        // Scan request for "backurl"
        BackUrlAction.scan(request);

		stopwatch.stop();

		// Continue on with request
		return executioncontext.proceed();
	}


}
