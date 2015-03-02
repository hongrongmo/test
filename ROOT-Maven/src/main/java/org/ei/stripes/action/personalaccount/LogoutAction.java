package org.ei.stripes.action.personalaccount;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSRequestProcessor;
import org.ei.service.cars.CARSRequestType;
import org.ei.service.cars.Impl.CARSResponse;
import org.ei.service.cars.rest.CARSRequestFactory;
import org.ei.service.cars.rest.request.CARSRequest;
import org.ei.service.cars.util.CARSCommonUtil;
import org.ei.session.UserSession;
import org.ei.stripes.action.EVPathUrl;
import org.ei.stripes.action.WebAnalyticsEventProperties;
import org.ei.stripes.exception.EVExceptionHandler;
import org.ei.web.analytics.GoogleWebAnalyticsEvent;
import org.ei.web.cookie.CookieHandler;
import org.ei.web.cookie.EISessionCookie;

/**
 * This class services the logout or terminate action. It clears out all cookies and calls CARS to terminate user session.
 *
 * @author harovetm
 *
 */
@UrlBinding("/customer/terminate.url")
public class LogoutAction extends CARSActionBean {

    private final static Logger log4j = Logger.getLogger(LogoutAction.class);

    /**
     * Override for the ISecuredAction interface. This ActionBean does NOT require authentication!
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    /**
     * Default handler for logout
     *
     * @return Resolution
     */
    @DefaultHandler
    @DontValidate
    public Resolution process() {

        HttpServletResponse response = context.getResponse();
        setRoom(ROOM.blank);
        GoogleWebAnalyticsEvent logoutEvent = new GoogleWebAnalyticsEvent(WebAnalyticsEventProperties.CAT_LOGOUT, 
        											WebAnalyticsEventProperties.CAT_LOGOUT, "End Session");
        logoutEvent.setEndSession(true);
        logoutEvent.recordRemoteEvent(context);
        // Terminate the CARS session
        CARSResponse carsResponse = terminateSession(context.getRequest(), context.getResponse(), context.getUserSession());

        // Add custom header for Ajax calls that need to determine if there was
        // a redirect!
        response.addHeader("EV_END_SESSION", "true");

        // Clear cookies - the EISESSION cookie will be cleared as part of the
        // SessionManager logout below
        if (carsResponse != null) {
            clearCarsCookies(response, carsResponse.getCarsCookie());
            // after logout CARS send a new cars cookie
            CARSCommonUtil.handleCarsCookie(context.getRequest(), context.getResponse(), carsResponse.getCarsCookie());
        }

        clearClientCookies(response);

        // Clear user session
        HttpSession session = context.getExistingSession();
        if (session != null && !session.isNew()) {
            session.invalidate();
        }

        return new RedirectResolution(EVPathUrl.EV_HOME.value());

    }

    public static void clearClientCookies(HttpServletResponse response) {
        response.addCookie(CookieHandler.clearCookie("JSESSIONID"));
        response.addCookie(CookieHandler.clearCookie("SECUREID"));
        response.addCookie(CookieHandler.clearCookie(EISessionCookie.EISESSION_COOKIE_NAME));
    }

    /**
     * Clean all EV cookies from response
     *
     * @param response
     */
    public static void clearCarsCookies(HttpServletResponse response, String newCarsCookie) {
        if (response == null) {
            log4j.warn("Response is null!");
            return;
        }
        response.addCookie(CookieHandler.clearCookie("RXSESSION"));
        // if new cars cookie is blank then get rid of the old one
        if (StringUtils.isBlank(newCarsCookie)) {
            response.addCookie(CookieHandler.clearCookie("CARS_COOKIE"));
        }
        response.addCookie(CookieHandler.clearACWCookie("acw"));

    }

    /**
     * Public method to terminate a CARS session
     *
     * @param request
     * @return
     */
    public static CARSResponse terminateSession(HttpServletRequest request, HttpServletResponse response, UserSession usersession) {
        /*
         * if (usersession == null || usersession.getUser() == null) { if (GenericValidator.isBlankOrNull(BaseCookie.getAuthTokenCookie(request))) {
         * log4j.info("No usersession or user object AND no auth token cookie! Do NOT call CARS terminate!"); return null; } } else if
         * (GenericValidator.isBlankOrNull(usersession.getUser().getAuthToken()) && GenericValidator.isBlankOrNull(BaseCookie.getAuthTokenCookie(request))) {
         * log4j.info("No authtoken present - do NOT call CARS terminate!"); return null; }
         */
        CARSResponse carsResponse = null;
        CARSRequestProcessor carsrequestprocessor = null;

        // Call CARS terminate action
        try {
            CARSRequest carsReq = CARSRequestFactory.buildCARSRequest(CARSRequestType.TERMINATE, request, usersession);
            carsrequestprocessor = new CARSRequestProcessor();
            carsResponse = carsrequestprocessor.process(carsReq, request, response, usersession);
        } catch (ServiceException e) {
            EVExceptionHandler.logException("CARS terminate action failed", e, request, log4j);
        }

        return carsResponse;
    }

}
