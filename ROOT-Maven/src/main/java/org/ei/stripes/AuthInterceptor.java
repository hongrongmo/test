package org.ei.stripes;

import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.ei.biz.security.AuthorizationFailure;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.ISecuredAction;
import org.ei.biz.security.WorldAccessControl;
import org.ei.config.EVProperties;
import org.ei.controller.IPBlocker;
import org.ei.controller.IPBlocker.COUNTER;
import org.ei.service.ANEServiceConstants;
import org.ei.service.cars.CARSConstants;
import org.ei.service.cars.CARSStringConstants;
import org.ei.service.cars.Impl.CARSResponse;
import org.ei.session.SSOHelper;
import org.ei.session.SessionManager;
import org.ei.session.UserSession;
import org.ei.stripes.action.ApplicationStatus;
import org.ei.stripes.action.CaptchaAction;
import org.ei.stripes.action.ControllerAction;
import org.ei.stripes.action.EVActionBean;
import org.ei.stripes.action.EVPathUrl;
import org.ei.stripes.action.SavedSearchesAndAlertsAction;
import org.ei.stripes.action.SystemMessage;
import org.ei.stripes.action.folders.PersonalFoldersAction;
import org.ei.stripes.action.personalaccount.CARSActionBean;
import org.ei.stripes.action.personalaccount.IPersonalLogin;
import org.ei.stripes.action.personalaccount.LoginAction;
import org.ei.stripes.exception.EVExceptionHandler;
import org.ei.stripes.util.HttpRequestUtil;
import org.ei.stripes.view.CustomizedLogo;
import org.ei.web.cookie.CookieHandler;
import org.perf4j.log4j.Log4JStopWatch;

@Intercepts(LifecycleStage.BindingAndValidation)
public class AuthInterceptor implements Interceptor {

    private static org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger(AuthInterceptor.class);

    @Override
    public Resolution intercept(ExecutionContext context) throws Exception {
		Log4JStopWatch stopwatch = new Log4JStopWatch("Interceptor.Auth");

		EVActionBeanContext evcontext = (EVActionBeanContext) context.getActionBeanContext();
        EVActionBean actionbean = (EVActionBean) context.getActionBean();
        HttpServletRequest request = evcontext.getRequest();
        HttpServletResponse response = evcontext.getResponse();
        IAccessControl accesscontrol = null;

        // *************************************************************
        // AuthInterceptor processing is not required for page which is open to world
        // and IP authentication is not required
        // *************************************************************
        if (actionbean instanceof ISecuredAction) {
            log4j.info("Getting access control object (ISecuredAction)");
            accesscontrol = ((ISecuredAction) actionbean).getAccessControl();
            if (accesscontrol instanceof WorldAccessControl) {
                return context.proceed();
            }
        }

        // *************************************************************
        // If this is a call to get the status page then just proceed!
        // *************************************************************
        if (actionbean != null && actionbean instanceof ApplicationStatus) {
            log4j.warn("Skipping auth interceptor!");
            return context.proceed();
        }

        // ************************************************************
        // Clear out the next URL if we are NOT doing a CARS action
        // ************************************************************
        UserSession userSession = evcontext.getUserSession();
        if ((!(actionbean instanceof CARSActionBean)) && StringUtils.isNotBlank(userSession.getNextUrl())) {
            userSession.setNextUrl(null);
        }

        // Check for SSO credentials. If no cookies are present, redirect to SSO
        // core server to set the cookies
        if (isSSORedirectRequired(request, userSession) && !(actionbean instanceof LoginAction)) {
            return new RedirectResolution(ANEServiceConstants.getSSOCoreRedirectURL()
                + URLEncoder.encode(getRedirectUrlForSSOCore(request), "UTF-8"), false);
        }

        // *****************************************************
        // Attempt to authenticate the user if they have NO
        // current authentication.
        // *****************************************************
        SessionManager sessionmanager = new SessionManager(request, response);
        if (((!userSession.getUser().isCustomer()) || isSSOReAuthenticationRequired(request, userSession)) && !(actionbean instanceof LoginAction)) {
            // || isLoginActionRequest(context)
            userSession = sessionmanager.authenticateUser(evcontext, true);
            CARSResponse carsresponse = evcontext.getCarsResponse();
            if (carsresponse == null) {
                log4j.error("No CARS response object returned!");
                return SystemMessage.SYSTEM_ERROR_RESOLUTION;
            }
        }


        if(!userSession.getUser().isCustomer()){
        	String ipaddress = HttpRequestUtil.getIP(request);
        	IPBlocker.getInstance().increment(ipaddress, COUNTER.NONCUSTOMER_REQUEST);
        }

        // *****************************************************
        // See if CookieHandler is handling this request.
        // Currently this is just for checking the department-
        // level usage tracking.
        // TODO - forward to JSP handled by Stripes??
        // *****************************************************
        log4j.info("Checking for special usage by dept");
        Resolution deptresolution = CookieHandler.handleRequest(request, response, userSession);
        if (deptresolution != null) {
            return deptresolution;
        }

        // *****************************************************
        // Check the request for certain searches that would
        // trigger a captcha check
        // *****************************************************
        // Avoid infinite loop - see if we're already processing captcha
        // request!
        if (!(actionbean instanceof CaptchaAction)) {
            log4j.info("Checking for captcha");
            Resolution captcharesolution = CaptchaAction.handleCaptcha(request, userSession);
            if (captcharesolution != null)
                return captcharesolution;
        } else {
            log4j.info("Skipping to captcha handling");
            return context.proceed();
        }

        // *****************************************************
        // See if action bean associated with the request
        // requires Personal login
        // *****************************************************
        try {
            if (null != accesscontrol) {
                boolean access = accesscontrol.isAccessAllowed(userSession.getUser());
                if (!access) {
                    AuthorizationFailure authfailure = accesscontrol.getAuthorizationFailure();
                    log4j.warn("Authorization denied via AccessControl object:  " + authfailure.getName());
                    if (AuthorizationFailure.AUTHENTICATION_REQUIRED == authfailure) {
                        log4j.info("Redirecting user to world home page (authentication required)");
                        userSession.setNextUrl(URLEncoder.encode(request.getRequestURI() + "?" + request.getQueryString(), "UTF-8"));
                        sessionmanager.updateUserSession(userSession);
                        return new RedirectResolution(EVPathUrl.EV_HOME.value() + "?redir=t", false);
                    } else if (AuthorizationFailure.INDIVIDUAL_AUTHENTICATION_REQUIRED == authfailure) {

                        if (userSession != null && userSession.getUser() != null
                            && "ANON_SHIBBOLETH".equalsIgnoreCase(userSession.getUser().getUserAnonymity())
                            && ((actionbean instanceof PersonalFoldersAction) || (actionbean instanceof SavedSearchesAndAlertsAction))) {
                            log4j.info("Redirecting user to activate personalization required page since user type is ANON_SHIBBOLETH");
                            return new RedirectResolution(CARSConstants.getLocalActivatePersonalizationURI());
                        }

                        log4j.info("Redirecting user to personal login page (individual authentication required)");
                        if (!(actionbean instanceof IPersonalLogin)) {
                            throw new ServletException("Request requires individual authentication but ActionBean does not implement IPersonalLogin!");
                        } else {
                            log4j.info("Redirecting to login form via IPersonalLogin association!");
                            userSession.setBackUrl(((IPersonalLogin) actionbean).getLoginCancelUrl());
                            userSession.setNextUrl(((IPersonalLogin) actionbean).getLoginNextUrl());
                            return new RedirectResolution(CARSConstants.getLocalLoginFullURI());
                        }
                    } else if (AuthorizationFailure.FEATURE_DISABLED == authfailure) {
                        log4j.info("Feature is disabled for user - redirecting to error page");
                        return new RedirectResolution(SystemMessage.SYSTEM_ERROR_URL + "?message="
                            + URLEncoder.encode("You do not currently have access to this feature.", "UTF-8"));
                    }
                }
            }
            // ********************************************************
            // Initialize the GlobalDisplay links and the custom logo.
            // We only do this when NOT going to the ControllerAction
            // since any request serviced by that action will have
            // these items rendered via the model/view XML/XSL
            // transformation
            // ********************************************************
            if (!(actionbean instanceof ControllerAction)) {
                log4j.info("Setting Custom logo...");
                actionbean.setCustomlogo(CustomizedLogo.build(evcontext));
            }

            // *****************************************************
            // Proceed with the request!
            // *****************************************************
            return context.proceed();

        } catch (Exception e) {
            // ******************************************************************
            // Deal with Exception from code above
            // ******************************************************************
            EVExceptionHandler.logException("Error has occurred processing request", e, request);

            // Already trying to get the exception page??
            if (actionbean instanceof SystemMessage) {
                return new StreamingResolution("text/html", "The website is currently unavailable.");
            }
            // Not already in redirect, try to redirect to it
            throw e;

        } finally {
            stopwatch.stop();
        }
    }

    /**
     * This method attempts to determine if an SSO redirect is required.
     *
     * @param request
     * @param userSession
     * @return
     */
    private boolean isSSORedirectRequired(HttpServletRequest request, UserSession userSession)  {
        if (request == null || userSession == null) {
            throw new IllegalArgumentException("Invalid parameters passed to isSSORedirectRequired!");
        }

        // Certain parameters on the URL should disable SSO redirect
        if (!GenericValidator.isBlankOrNull(request.getParameter(SessionManager.REQUEST_PT))
            || !GenericValidator.isBlankOrNull(request.getParameter(SessionManager.REQUEST_NEWSESSION))
            || !GenericValidator.isBlankOrNull(request.getParameter(SessionManager.REQUEST_USERNAME))
            || !GenericValidator.isBlankOrNull(request.getParameter(SessionManager.REQUEST_PASSWORD))) {
            log4j.info("SSO Redirect NOT required - request parameter indicator!");
            return false;
        }

        // Certain "world" content IDs do not require SSO
        String cid = request.getParameter("CID");
        if ("openXML".equals(cid) || "openRSS".equals(cid) || "openRSSP".equals(cid) || "openATOM".equals(cid) || "blogDocument".equals(cid)) {
            log4j.info("SSO Redirect NOT required - user is requesting CID=" + cid + "!");
            return false;
        }

        // Check to see if SSO is disabled in properties
        if (Boolean.valueOf(EVProperties.getProperty(EVProperties.DISABLE_SSO_AUTH))) {
            log4j.info("SSO Redirect NOT required - DISABLE_SSO_AUTH set in EVProperties!");
            return false;
        }

        // Check to see if SSO is disabled in database runtime properties
        String ssoCoreRedirectFlag = EVProperties.getProperty(EVProperties.SSO_CORE_REDIRECT_FLAG);
        if (StringUtils.isBlank(ssoCoreRedirectFlag) || !Boolean.valueOf(ssoCoreRedirectFlag)) {
            log4j.info("SSO Redirect NOT required -SSO disabled in database!");
            return false;
        }

        // Lastly, have we already checked?
        if (StringUtils.isBlank(userSession.getBrowserSSOKey()) &&
            StringUtils.isBlank(CookieHandler.getACWCookie(request).getValue()) &&
            null == request.getParameter(CARSStringConstants.ACW.value())) {
            log4j.info("SSO Redirect IS required - No ACW cookie or parameter on URL!");
            return true;
        }

        // If we make it here, return false, SSO is either disabled or has
        // already been run
        return false;
    }

    private String getRedirectUrlForLoginPage(HttpServletRequest request) {
        StringBuffer userRequestUrl = new StringBuffer();
        userRequestUrl.append(request.getRequestURI());
        if (StringUtils.isNotBlank(request.getQueryString())) {
            userRequestUrl.append("?" + request.getQueryString());
        }

        return userRequestUrl.toString();
    }

    private String getRedirectUrlForSSOCore(HttpServletRequest request) {
        StringBuffer userRequestUrl = new StringBuffer();
        userRequestUrl.append(request.getRequestURL());
        if (StringUtils.isNotBlank(request.getQueryString())) {
            userRequestUrl.append("?" + request.getQueryString());
        }

        return userRequestUrl.toString();
    }

    private boolean isSSOReAuthenticationRequired(HttpServletRequest request, UserSession userSession) {
        return isSSOCookieChanged(request, userSession) || isSSOCookieValidationExpried(userSession);
    }

    private boolean isSSOCookieValidationExpried(UserSession userSession) {
        return SSOHelper.isSSOTimeIntervalPassed(SSOHelper.getSSOLastAccessTime(userSession));
    }

    private boolean isSSOCookieChanged(HttpServletRequest request, UserSession userSession) {
        return SSOHelper.isStateChanged(request, userSession.getBrowserSSOKey());
    }

}
