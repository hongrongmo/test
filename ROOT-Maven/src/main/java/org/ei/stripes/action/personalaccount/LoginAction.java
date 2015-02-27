package org.ei.stripes.action.personalaccount;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationError;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.personalization.UserPrefs;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.config.EVProperties;
import org.ei.controller.IPBlocker;
import org.ei.controller.IPBlocker.COUNTER;
import org.ei.exception.SessionException;
import org.ei.service.cars.CARSResponseStatus;
import org.ei.service.cars.CARSTemplateNames;
import org.ei.service.cars.PageType;
import org.ei.service.cars.RESTRequestParameters;
import org.ei.service.cars.Impl.CARSResponse;
import org.ei.session.SessionManager;
import org.ei.session.UserSession;
import org.ei.stripes.action.WebAnalyticsEventProperties;
import org.ei.stripes.action.lindahall.LindaHallAuthAction;
import org.ei.stripes.util.HttpRequestUtil;
import org.ei.web.analytics.GoogleWebAnalyticsEvent;

import com.elsevier.webservices.schemas.csas.constants.types.v7.AllowedRegistrationType;

@UrlBinding("/customer/authenticate.url")
public class LoginAction extends CARSActionBean {
    private static Logger log4j = Logger.getLogger(LoginAction.class);

    public static final ValidationError OUTSIDE_IP_ERROR = new SimpleError("You are currently outside of your institution&#39;s IP address range, and your username and password are not valid for remote login.  Please contact your administrator if you require remote access to Engineering Village.");
    public static final ValidationError TICURL_INVALID_ERROR = new SimpleError("You cannot access Engineering Village because you are using a trusted partner authentication that is not recognized. Please contact your librarian or system administrator for assistance.");
    public static final ValidationError INVALID_UNPW_ERROR = new SimpleError("You have entered an invalid username/password combination. Please re-enter your username and password.");
    public static final ValidationError SHIB_INSTITUTION_NOT_FOUND_ERROR = new SimpleError("Your log on credentials do not allow full access to Engineering Village. Please contact your institution's librarian or information specialist for assistance.");
    public static final ValidationError OTHER_ERROR = new SimpleError("An error has occurred.");

    /**
     * Override for the ISecuredAction interface.  This ActionBean does NOT
     * require authentication!
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    /**
     * Default handler for the Login action.  These are the use cases for access to
     * the Login action:
     *
     * 1. User has FAILED authentication and does NOT have any access
     * 2. User has FAILED authentication but has IP (anonymous) access
     * 3. User has specifically requested Login page.
     *
     * @return
     * @throws ServletException
     * @throws UnsupportedEncodingException
     * @throws SessionException
     */
    @DefaultHandler
    public Resolution authenticate() throws ServletException, UnsupportedEncodingException, SessionException {
        UserSession usersession = context.getUserSession();
        SessionManager sessionmanager = new SessionManager(context.getRequest(), context.getResponse());
        CARSResponse carsresponse = null; //context.getCarsResponse();
        GoogleWebAnalyticsEvent loginEvent = null; 
        String auth_type = context.getRequest().getParameter("auth_type");
        if(GenericValidator.isBlankOrNull(auth_type)){
        	auth_type = "NO AUTH TYPE";
        }
        // Call CARS with current authentication info.  Remember that this
        // can be ID/PW, self-manra, path choice, etc.
        try {
            log4j.info("Authenticating user...");

            usersession = sessionmanager.authenticateUser(context);
            carsresponse = context.getCarsResponse();
        } catch (Exception e) {
            throw new ServletException("Unable to authenticate user!", e);
        }
        
        //
        // If there is an authentication error and the type is NO_ACTIVE_PATHS
        // This is a special case that happens when a user is OUTSIDE of their
        // regular IP range and does NOT have remote access!
        //
        if (CARSResponseStatus.STATUS_CODE.AUTHENTICATION_ERROR.equals(carsresponse.getResponseStatus().getStatuscode())) {
        	log4j.warn("Authentication Error response from CARS");
        	if (EVProperties.isLogCarsResponseErrors()) log4j.warn(carsresponse.toString());
        	if(usersession.getUser().isCustomer()){
        		String ipaddress = HttpRequestUtil.getIP(context.getRequest());
            	IPBlocker.getInstance().increment(ipaddress, COUNTER.AUTHFAIL);
        	}
        	if (AllowedRegistrationType.NONE.toString().equals(usersession.getUser().getAllowedRegType())) {
                // Process legacy credentials.  This is only needed while we are migrating users!
                processLegacyInstitutionCredentials();
            }

        }

        if (carsresponse.isPathChoice()) {
            log4j.info("Processing path choice...");
            // If redirect is present, save it as next URL
            if (!GenericValidator.isBlankOrNull(carsresponse.getRedirectURL())) {
                context.getUserSession().setNextUrl(carsresponse.getRedirectURL());
            }
        	loginEvent = new GoogleWebAnalyticsEvent(WebAnalyticsEventProperties.CAT_LOGIN,auth_type,WebAnalyticsEventProperties.ACTION_LOGIN_PATHCHOICE);
        	loginEvent.recordRemoteEvent(context);
            // Just process this request if it's path choice.  Man how I hate this screen!!!
            return super.getResolution();
        }

        // If there is a webUserId present, use it to load preferences
        IEVWebUser user = usersession.getUser();
        if (user != null && user.isIndividuallyAuthenticated()) {
            UserPrefs userprefs = UserPrefs.load(user.getWebUserId());
            if (userprefs == null) {
                userprefs = new UserPrefs(user.getWebUserId());
                userprefs.save();
            }
            usersession.getUser().setUserPrefs(userprefs);
            context.updateUserSession(usersession);
           	loginEvent = new GoogleWebAnalyticsEvent(WebAnalyticsEventProperties.CAT_LOGIN,auth_type,usersession.getUser().getAccount().getAccountName());
           	loginEvent.recordRemoteEvent(context);
            
        }

        // Redirects can happen for 3rd party auth systems
        if(null != carsresponse.getPageType() && carsresponse.getPageType().equals(PageType.REDIRECT)) {
            log4j.info("Processing CARS redirect, shib URL: '" + carsresponse.getShibbolethURL() + ", redirect URL: '" + carsresponse.getRedirectURL() + "'");
            log4j.info(carsresponse.toString());
            if (StringUtils.isNotBlank(carsresponse.getShibbolethURL())){
                return new RedirectResolution(carsresponse.getShibbolethURL());
            } else if (StringUtils.isNotBlank(carsresponse.getRedirectURL())){
                return new RedirectResolution(URLDecoder.decode(carsresponse.getRedirectURL(), "utf-8"));
            } else {
                log4j.warn("CARS has indicated REDIRECT in response but there is no URL to go to!");
                return HomeAction.HOME_RESOLUTION;
            }
        }

        // Show the welcome page if user has no authentication AND we're not completing
        // self-manra or bulk registration
        if (!usersession.getUser().isCustomer() && StringUtils.isBlank(context.getRequest().getParameter("self_managed_token")) && StringUtils.isBlank(context.getRequest().getParameter("bulk_id"))) {
            carsresponse.setHeader(false);

            // CARS returns plain CARS_LOGIN page when user attempts to login without remote access.
            // We must put user on FULL login page and add a message
            if (CARSResponseStatus.ERROR_TYPE.NO_ACTIVE_PATHS.equals(carsresponse.getResponseStatus().getErrortype())) {
                log4j.warn("NO_ACTIVE_PATHS error has occurred!");
                if (EVProperties.isLogCarsResponseErrors()) log4j.warn(carsresponse.toString());
                context.getValidationErrors().addGlobalError(OUTSIDE_IP_ERROR);
            } else if (CARSResponseStatus.ERROR_TYPE.INVALID.equals(carsresponse.getResponseStatus().getErrortype())) {
                log4j.warn("INVALID error type returned from CARS!");
                if (EVProperties.isLogCarsResponseErrors()) log4j.warn(carsresponse.toString());
                if ("TICURL_QUERY_STRING".equals(carsresponse.getResponseStatus().getErrorfieldname()) ||
                    "TICURL_MD5_HASH".equals(carsresponse.getResponseStatus().getErrorfieldname())) {
                    context.getValidationErrors().addGlobalError(TICURL_INVALID_ERROR);
                } else {
                    context.getValidationErrors().addGlobalError(OTHER_ERROR);
                    // Clear cookies for this case
                    LogoutAction.clearCarsCookies(context.getResponse(), null);
                }
            } else if (CARSResponseStatus.ERROR_TYPE.TICURL_INSTITUTION_NOT_FOUND.equals(carsresponse.getResponseStatus().getErrortype())) {
                log4j.warn("TICURL_INSTITUTION_NOT_FOUND Error response from CARS");
                if (EVProperties.isLogCarsResponseErrors()) log4j.warn(carsresponse.toString());
                context.getValidationErrors().addGlobalError(TICURL_INVALID_ERROR);
            } else if (CARSResponseStatus.ERROR_TYPE.LOGIN_NO_MATCH.equals(carsresponse.getResponseStatus().getErrortype())) {
                log4j.warn("LOGIN_NO_MATCH Error response from CARS");
                if (EVProperties.isLogCarsResponseErrors()) log4j.warn(carsresponse.toString());
                context.getValidationErrors().addGlobalError(INVALID_UNPW_ERROR);
            } else if (CARSResponseStatus.ERROR_TYPE.CRED_NO_MATCH.equals(carsresponse.getResponseStatus().getErrortype())) {
                log4j.warn("INVALID_UNPW_ERROR Error response from CARS");
                if (EVProperties.isLogCarsResponseErrors()) log4j.warn(carsresponse.toString());
                context.getValidationErrors().addGlobalError(INVALID_UNPW_ERROR);
            } else if (CARSResponseStatus.ERROR_TYPE.LOGIN_NO_MATCH.equals(carsresponse.getResponseStatus().getErrortype())) {
                log4j.warn("INVALID_UNPW_ERROR Error response from CARS");
                if (EVProperties.isLogCarsResponseErrors()) log4j.warn(carsresponse.toString());
                context.getValidationErrors().addGlobalError(INVALID_UNPW_ERROR);
            } else if (CARSResponseStatus.ERROR_TYPE.SHIB_INSTITUTION_NOT_FOUND.equals(carsresponse.getResponseStatus().getErrortype())) {
                log4j.warn("SHIB_INSTITUTION_NOT_FOUND Error response from CARS");
                if (EVProperties.isLogCarsResponseErrors()) log4j.warn(carsresponse.toString());
                context.getValidationErrors().addGlobalError(SHIB_INSTITUTION_NOT_FOUND_ERROR);
            } else {
                log4j.warn("Unknown Error response from CARS");
                if (EVProperties.isLogCarsResponseErrors()) log4j.warn(carsresponse.toString());
                context.getValidationErrors().addGlobalError(OTHER_ERROR);
                // Clear cookies for this case
                LogoutAction.clearCarsCookies(context.getResponse(), null);
            }

            HomeAction.createLoginFullCARSResponse(context);
            return new ForwardResolution("/WEB-INF/pages/world/welcome.jsp");
        }

        // Set Help link for special settings case
        if (CARSTemplateNames.CARS_SETTINGS.toString().equals(carsresponse.getTemplateName())) {
            setHelpcontext("help.context.customer.settings");
        }

    	// Clear some session items if successful login
        context.getRequest().getSession(false).removeAttribute(LindaHallAuthAction.LHL_SESSION_USERINFO);

    	return super.getResolution();

    }


    /**
     * Create a CARS response for LOGIN_FULL template
     * @param context
     * @return
     */



    /**
     * This method is meant to be called AFTER a failed CARS authentication
     * to see if the user is entering legacy institution credentials (e.g.
     * "fast/fast").
     *
     * It also sets the isLegacyInstitutionCredentials value.
     *
     * @param username
     * @param password
     */
    public void processLegacyInstitutionCredentials() {

        // If user is already authenticated, return
        if (context.getUserSession().getUser().isCustomer()) {
            log4j.warn("User is already authenticated!");
            return;
        }

        // Make sure carsresponse is not empty
        CARSResponse carsresponse = context.getCarsResponse();
        if (carsresponse == null) {
            log4j.warn("CARS response object is empty!");
            return;
        }

        // Do not process if no authentication error is present
        if (!CARSResponseStatus.STATUS_CODE.AUTHENTICATION_ERROR.equals(carsresponse.getResponseStatus().getStatuscode())) {
            return;
        }

        // Now get the username and password from the request
        HttpServletRequest request = context.getRequest();
        String username = request.getParameter(RESTRequestParameters.USER_NAME.getReqParam());
        String password = request.getParameter(RESTRequestParameters.PASSWORD.getReqParam());
        String product = EVProperties.getProperty(EVProperties.APP_NAME);
        if (GenericValidator.isBlankOrNull(product) || GenericValidator.isBlankOrNull(username) || GenericValidator.isBlankOrNull(password)) {
            return;
        }

    }



}
