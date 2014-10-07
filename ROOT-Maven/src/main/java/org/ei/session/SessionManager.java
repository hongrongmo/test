package org.ei.session;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.ane.entitlements.EntitlementsService;
import org.ei.ane.entitlements.EntitlementsServiceImpl;
import org.ei.ane.entitlements.UserEntitlement;
import org.ei.ane.entitlements.UserEntitlement.ENTITLEMENT_TYPE;
import org.ei.ane.textzones.TextZonesConstants;
import org.ei.ane.textzones.TextZonesService;
import org.ei.ane.textzones.TextZonesServiceImpl;
import org.ei.biz.personalization.EVWebUser;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.personalization.PersonalAccount;
import org.ei.biz.personalization.UserProfile;
import org.ei.books.collections.ReferexCollection;
import org.ei.bulletins.BulletinGUI;
import org.ei.config.EVProperties;
import org.ei.controller.IPBlocker;
import org.ei.controller.IPBlocker.COUNTER;
import org.ei.domain.InvalidArgumentException;
import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.cars.CARSRequestProcessor;
import org.ei.service.cars.CARSRequestType;
import org.ei.service.cars.CARSStringConstants;
import org.ei.service.cars.Impl.CARSResponse;
import org.ei.service.cars.rest.CARSRequestFactory;
import org.ei.service.cars.rest.request.CARSRequest;
import org.ei.service.cars.util.CARSCommonUtil;
import org.ei.stripes.EVActionBeanContext;
import org.ei.stripes.util.HttpRequestUtil;
import org.ei.util.SyncTokenFIFOQueue;
import org.ei.web.cookie.CookieHandler;
import org.ei.web.cookie.EISessionCookie;

public class SessionManager {

    private static Logger log4j = Logger.getLogger(SessionManager.class);

    public static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";
    public static final String REQUEST_USE_SESSION_PARAM = "SYSTEM_USE_SESSION_PARAM";
    public static final String REQUEST_NEWSESSION = "SYSTEM_NEWSESSION";
    public static final String REQUEST_ENTRY_TOKEN = "SYSTEM_ENTRY_TOKEN";
    public static final String REQUEST_USERNAME = "SYSTEM_USERNAME";
    public static final String REQUEST_PASSWORD = "SYSTEM_PASSWORD";
    public static final String REQUEST_LOGOUT = "SYSTEM_LOGOUT";
    public static final String REQUEST_PT = "SYSTEM_PT";

    private static SessionBroker sessionBroker;

    /**
     * Static init method - should be called at application startup!
     *
     * @throws Exception
     * @throws NumberFormatException
     */
    public static void init() throws NumberFormatException {
        long sessiontimeout = 0;
        sessiontimeout = Long.parseLong(EVProperties.getProperty(EVProperties.SESSION_TIMEOUT));

        sessionBroker = SessionBroker.getInstance(sessiontimeout);
    }

    private HttpServletRequest request; 	// Current request object
    private HttpServletResponse response; 	// Current response object

    /**
     * Constructor must take in current request/response pair
     *
     * @param request
     * @param repsonse
     */
    public SessionManager(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public UserSession authenticateUser(EVActionBeanContext evcontext) throws SessionException, ServiceException, InfrastructureException {
        return authenticateUser(evcontext, false);
    }

    /**
     * Authenticate the user. Uses incoming request to determine auth method (IP, un/pw, etc.)
     *
     * @return UserSession object
     * @throws SessionException
     * @throws SessionException
     * @throws SessionException
     * @throws ServiceException
     * @throws IOException
     * @throws HttpException
     * @throws InfrastructureException
     * @throws InvalidArgumentException
     */
    public UserSession authenticateUser(EVActionBeanContext evcontext, boolean ipauth) throws SessionException, ServiceException, InfrastructureException {
        log4j.info("Session Manager authenticateUser START");
        HttpServletRequest request = evcontext.getRequest();
        HttpServletResponse response = evcontext.getResponse();
        CARSRequestProcessor carsrequestprocessor = null;

        UserSession userSession = evcontext.getUserSession();
        if (userSession == null) {
            throw new SessionException(SystemErrorCodes.SESSION_CREATE_ERROR, "Unable to authenticate User: empty UserSession object!");
        }
        CARSResponse carsresponse = null;
        CARSRequest carsReq = null;
        if (ipauth) {
            carsReq = CARSRequestFactory.buildCARSRequest(CARSRequestType.IPAUTHENTICATE, request, userSession);
        } else {
            carsReq = CARSRequestFactory.buildCARSRequest(CARSRequestType.AUTHENTICATE, request, userSession);
        }
        carsrequestprocessor = new CARSRequestProcessor();
        carsresponse = carsrequestprocessor.process(carsReq, request, response, userSession);
        evcontext.setCarsResponse(carsresponse);

        // Always add Text Zones to pick up platform defaults
        if (StringUtils.isNotBlank(getUserAuthToken(userSession))) {
            setUserTextZonesInUserSession(userSession);
        }

        // If there is a "userInfo" element present in CARS response
        // then parse the response for user information
        if (StringUtils.isNotBlank(userSession.getUser().getWebUserId())) {
            setUserEntitlementsInUserSession(userSession);
            setUserCartridgeInUserSession(userSession);

            setUserStartPageInEvWebUser(userSession);
            setUserCustomerIdInUserSession(userSession);
            setLocalHoldingsInUserSession(userSession);
            setBulletinEntitlements(userSession);

            try {
                StringBuffer out = new StringBuffer();
                out.append("********* User authenticated (" + ipauth + ") **********");
                out.append(EVProperties.NEWLINE + "    +    User:       " + userSession.getUser().getWebUserId() + " - " + userSession.getUser().getFirstName() + " " + userSession.getUser().getLastName() + ", " + userSession.getUser().getEmail());
                out.append(EVProperties.NEWLINE + "    +    Cartridge:  " + userSession.getUser().getCartridgeString());
                out.append(EVProperties.NEWLINE + "    +    CustomerID: " + userSession.getUser().getCustomerID());
                out.append(EVProperties.NEWLINE + "    +    IP address: " + HttpRequestUtil.getIP(evcontext.getRequest()));
                if (userSession.getUser().getAccount() != null) {
                    out.append(EVProperties.NEWLINE + "    +    Account:    " + userSession.getUser().getAccount().getAccountNumber() + " - " + userSession.getUser().getAccount().getAccountName() + "/" + userSession.getUser().getAccount().getDepartmentName());
                }
                log4j.warn(out.toString());
            } catch (Throwable t) {
                log4j.error("Unable to print authentication info: " + t);
            }
        }


        PersonalAccount pa = new PersonalAccount();
        IEVWebUser user = userSession.getUser();
        if (user == null) {
            throw new SessionException(SystemErrorCodes.SESSION_CREATE_ERROR, "User object is null!");
        }
        String userType = user.getUserAnonymity();

        if (user.isIndividuallyAuthenticated()) {
            UserProfile p = pa.getUserProfileByWebUserId(user.getWebUserId());
            if (p == null) {
                p = addNewPersonalAccount(userSession, "Y");
            } else {
                touchPersonalAccount(p.getUserId());
            }
            user.setUserInfo(p);
        }

        // Set the IP address for the User
        user.setIpAddress(HttpRequestUtil.getIP(request));
        CARSCommonUtil.handleCarsCookie(request, response, carsresponse.getCarsCookie());
        // Set auth token cookies as RXSESSION
        CARSCommonUtil.handleAuthTokenCookie(request, response, user.getAuthToken());
        userSession.setAuthtoken(user.getAuthToken());
        // Write SSO cookie
        writeSSOCookieSinceValueChangedAfterLogin(evcontext);

        // If we have a valid customer, update the session with user info
        // NOTE: we do NOT update session otherwise because we need to avoid creating
        //       session info on every request!
        if (user.isCustomer() || (carsresponse != null && carsresponse.isPathChoice())) {
            userSession = updateUserSession(userSession, false, true);
        }else{
        	String ipaddress = HttpRequestUtil.getIP(request);
        	IPBlocker.getInstance().increment(ipaddress, COUNTER.AUTHFAIL);
        }

        return userSession;
    }

    private void writeSSOCookieSinceValueChangedAfterLogin(EVActionBeanContext context) {
        if(SSOHelper.isStateChanged(context.getRequest(), getBrowserSSOKey(context))){
            Cookie[] cookies = context.getRequest().getCookies();

            if(cookies != null ) {
                for( int i=0; i<cookies.length; i++ ) {
                Cookie cookie = cookies[i];
                    if(CARSStringConstants.ACW.value().equals(cookie.getName())) {
                        cookie.setValue(getUpdatedCookieFromCARSRequest(context));
                        cookie.setMaxAge(-1);
                        cookie.setPath("/");
                        cookie.setDomain(".engineeringvillage.com");
                        context.getResponse().addCookie(cookie);
                        break;
                    }
                }
            }
        }
    }

    private String getUpdatedCookieFromCARSRequest(EVActionBeanContext context)  {
        try {
            return URLEncoder.encode(getBrowserSSOKey(context), CARSStringConstants.URL_ENCODE.value());
        } catch (UnsupportedEncodingException e) {
            log4j.warn( "Problem in decoding the SSO Key using " + CARSStringConstants.URL_ENCODE.value() + e.getMessage());
        }
        return "";
    }

    private String getBrowserSSOKey(EVActionBeanContext context) {
        return context.getUserSession().getBrowserSSOKey();
    }




    /**
     * create a new personal account for a user so we can track personalization
     *
     * @param userSession
     * @return the userprofile created
     * @throws InfrastructureException
     * @throws InvalidArgumentException
     * @throws InfrastructureException
     */
    public UserProfile addNewPersonalAccount(UserSession userSession, String merged) throws InfrastructureException {

        String custId = userSession.getUser().getCustomerID();
        if (GenericValidator.isBlankOrNull(userSession.getUser().getWebUserId())) {
            return null;
        }

        // needs to be unique for profile table. If customer id is null we should use the
        // CS account number
        if (GenericValidator.isBlankOrNull(custId)) {
            custId = userSession.getUser().getAccount().getAccountNumber();
        }
        UserProfile user;
		try {
			user = new UserProfile();
			user.setsWebUserId(userSession.getUser().getWebUserId());
			user.setEmail(userSession.getUser().getEmail());
			user.setFirstName(userSession.getUser().getFirstName());
			user.setLastName(userSession.getUser().getLastName());
			user.setPassword("dummypswd");
			user.setContractId(userSession.getUser().getWebUserId());
			user.setCustomerId(custId);
			user.setMerged(merged);

			PersonalAccount pa = new PersonalAccount();
			user = pa.createUserProfile(user);
			userSession.getUser().setUserInfo(user);
		} catch (InvalidArgumentException e) {
			throw new InfrastructureException(SystemErrorCodes.PERSONAL_PROFILE_ERROR, e);
		}
        return user;

    }

    private void touchPersonalAccount(String userid) throws InfrastructureException {
        PersonalAccount pa = new PersonalAccount();
        pa.touchUserProfile(userid);
    }

    private void setUserStartPageInEvWebUser(UserSession userSession) {
        getUserFromHttpSession(userSession).setStartPage(getStartPageFromTextZones(userSession));
    }

    private String getStartPageFromTextZones(UserSession userSession) {
        return userSession.getUserTextZones().get(TextZonesConstants.START_PAGE);
    }

    private void setUserCartridgeInUserSession(UserSession userSession) throws SessionException {
            List<String> cartridge = CartridgeBuilder.buildUserCartridge(userSession);
            // Special case - add referex entitlement if any fence is found!
            if (ReferexCollection.ALLCOLS_PATTERN.matcher(cartridge.toString()).find()) {
                userSession.getUserEntitlements().add(new UserEntitlement(ENTITLEMENT_TYPE.REFEREX, "PAG"));
            }
            userSession.getUser().setCartridge(cartridge.toArray(new String[cartridge.size()]));
    }

    public void setUserEntitlementsInUserSession(UserSession userSession) throws SessionException, ServiceException {
		EntitlementsService entitlementsService = new EntitlementsServiceImpl();
		userSession.getUserEntitlements().clear();
		Set<UserEntitlement> userEntitlementList = entitlementsService.getUserEntitlements(getUserAuthToken(userSession), getUserProductAllListId(userSession),
				getCarsSessionid(userSession));
		userSession.addUserEntitlements(userEntitlementList);
    }

    private String getCarsSessionid(UserSession userSession) {
        return userSession.getUser().getCarsJSessionId();
    }

    private String getUserProductAllListId(UserSession userSession) {
        return getUserFromHttpSession(userSession).getProductInfo().getAllListId();
    }

    private String getUserAuthToken(UserSession userSession) {
        return getUserFromHttpSession(userSession).getAuthToken();
    }

    private void setUserTextZonesInUserSession(UserSession userSession) throws ServiceException {
        TextZonesService textZonesService = new TextZonesServiceImpl();

        userSession.getUserTextZones().clear();
        userSession.addUserTextZones(textZonesService.getUserTextZones(getUserAuthToken(userSession), getCarsSessionid(userSession)));
    }

    private IEVWebUser getUserFromHttpSession(UserSession session) {
        if (null != session.getUser()) {
            return session.getUser();
        }

        return null;
    }

    /**
     * Build a new SessionID object from the current request
     *
     * @return
     * @throws SessionException
     */
    public SessionID buildSessionID() throws SessionException {
        SessionID sessionidObj = null;

        // Attempt to get current sessionid string - first
        // look in EISESSION cookie
        String sessionid = new EISessionCookie(this.request).getSessionIdWithVersion();

        // Now check incoming request parameter if indicated
        String useSessionParam = "";
        if (request.getParameter(REQUEST_USE_SESSION_PARAM) != null) {
            useSessionParam = request.getParameter(REQUEST_USE_SESSION_PARAM);
        }
        if (GenericValidator.isBlankOrNull(sessionid) || "true".equals(useSessionParam)) {
            sessionid = request.getParameter(EISessionCookie.EISESSION_COOKIE_NAME);
        }

        // If sessionid string is not null and not "0", use it!
        if (!GenericValidator.isBlankOrNull(sessionid) && !"0".equals(sessionid)) {
            sessionidObj = new SessionID(sessionid);
        }

        // Some requests explicitly require new session, check
        // for that here and null out session id obj if true
        if (!GenericValidator.isBlankOrNull(request.getParameter(REQUEST_NEWSESSION))) {
            sessionidObj = null;
        }

        // Create new session ID if empty
        if (sessionidObj == null || GenericValidator.isBlankOrNull(sessionidObj.getID())) {
            sessionidObj = new SessionID(EISessionCookie.buildNewSessionID(), 0);
        }
        return sessionidObj;
    }

    /**
     * Retrieve the current UserSession. Will create a new one if none can be found.
     *
     * @param session
     * @param sessionidObj
     * @return
     * @throws SessionException
     */
    public UserSession getUserSession() throws SessionException {
        log4j.info("Retrieving current UserSession...");

        // Retrieve a SessionID object. This call will look in cookies
        // and on the request to see if the user has an existing session ID
        SessionID sessionidObj = buildSessionID();

        // Attempt to get the session but do NOT create one if it doesn't
        // exist! This is so we can tell if the old session expired...
        HttpSession session = this.request.getSession(false);
        UserSession usersession = null;
        if (session == null) {
            usersession = buildNewUserSession();
            Cookie jsessioncookie = CookieHandler.getCookie(this.request, JSESSIONID_COOKIE_NAME);
            if (jsessioncookie != null) usersession.setStatus(SessionStatus.NEW);
            else usersession.setStatus(SessionStatus.NEW_HAD_EXPIRED);
        } else {
            usersession = (UserSession) session.getAttribute(sessionidObj.getID());
            if (usersession == null) {
                usersession = buildNewUserSession();
                usersession.setStatus(SessionStatus.NEW_HAD_EXPIRED);
            } else {
                usersession.setStatus(SessionStatus.OLD_FROM_CACHE);
            }
        }

        // Create a "dummy" user
        if (usersession.getUser() == null) {
            IEVWebUser user = new EVWebUser();
            if (!GenericValidator.isBlankOrNull(usersession.getAuthtoken())) {
                user.setAuthToken(usersession.getAuthtoken());
            }
            usersession.setUser(user);
        }

        usersession.setSessionID(sessionidObj);
        return usersession;
    }

    /**
     * Build a new UserSession object
     *
     * @return
     * @throws SessionException
     */
    public UserSession buildNewUserSession() {
        log4j.info("Building new UserSession...");

        UserSession userSession = new UserSession();
        userSession.setUser(new EVWebUser());
        userSession.setProperties(new Properties());
        userSession.setFifoQueue(new SyncTokenFIFOQueue());
        addRequestMetadata(userSession);

        return userSession;
    }

    /**
     * Touch the UserSession to keep it active
     *
     * @param usersession
     * @return
     * @throws SessionException
     */
    /*
     * public UserSession doTouch(UserSession usersession) throws SessionException { log4j.info("Starting touch...");
     *
     * if (usersession == null) { log4j.warn("No UserSession object for doTouch()!"); HttpSession session = this.request.getSession(true); usersession =
     * buildNewUserSession(session); usersession.setStatus(SessionStatus.NEW); return usersession; }
     *
     * // Update local session copy first HttpSession session = request.getSession(); SessionID sessionID = usersession.getSessionID(); UserSession cached =
     * (UserSession) session.getAttribute(sessionID.getID()); long time = System.currentTimeMillis(); if (cached != null) { usersession = cached;
     * usersession.setLastTouched(time); session.setAttribute(sessionID.getID(), usersession); }
     *
     * // Update database sessionBroker.touch(usersession.getSessionID().getID());
     *
     * // Return UserSession return usersession; }
     */

    /**
     * Determine if current session has expired by comparing incoming SessionID object to built UserSession object
     *
     * @param sessionidObj
     *            Incoming SessionID object
     * @param usersession
     *            Current UserSession object
     * @return
     */
    public boolean isExpired(SessionID sessionidObj, UserSession usersession) {
        log4j.info("Checking if current UserSession is expired....");
        // If UserSession is null, return false!
        if (usersession == null)
            return false;

        // Get the SessionID object from the current UserSession
        int version = sessionidObj.getVersionNumber();
        long currentTime = System.currentTimeMillis();

        int version2 = (usersession.getSessionID()).getVersionNumber();
        long lastTouched = usersession.getLastTouched();
        long expireTime = usersession.getExpireIn();

        if (version == version2 && (currentTime - lastTouched) < expireTime) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * Convience method to update UserSession - DOES NOT INCREMENT THE VERSION!
     *
     * @param session
     * @param response
     * @param usersession
     * @return
     * @throws SessionException
     */
    public UserSession updateUserSession(UserSession usersession) throws SessionException {
        return updateUserSession(usersession, false, false);
    }

    /**
     * Update the current UserSession object. This should be called after any request that modifies the session.
     *
     * @param usersession
     * @param incrementversion
     * @return
     * @throws SessionException
     */
    public UserSession updateUserSession(UserSession usersession, boolean incrementversion, boolean isPathChoiceExists) throws SessionException {
        log4j.info("Updating UserSession...");

        if (usersession == null) {
            log4j.warn("Attempt to call updateUserSession with empty UserSession object");
            return null;
        }
        if (request.getParameter(SessionManager.REQUEST_PT) == null) {
        	// Only create a new session if this is a valid customer!
        	if (usersession.getUser().isCustomer() || isPathChoiceExists) {
	            HttpSession session = this.request.getSession(true);
	            SessionID sessionidObj = usersession.getSessionID();
	            if (sessionidObj == null) {
	                log4j.warn("Session ID was null - should be created earlier!");
	                sessionidObj = new SessionID(EISessionCookie.buildNewSessionID(), 1);
	            } else {
	                // Increment version on session ID
	                sessionidObj = new SessionID(usersession.getSessionID().getID(), usersession.getSessionID().getVersionNumber() + (incrementversion ? 1 : 0));
	            }
	            usersession.setSessionID(sessionidObj);
	            // Write new EISESSION cookie to response
	            writeSessionCookie(sessionidObj);

	            if (session.isNew()) {
	            	log4j.info("New session created!  Incrementing session counter...");
	                IPBlocker.getInstance().increment(HttpRequestUtil.getIP(request), IPBlocker.COUNTER.SESSION);
	            }

	            // Update into local container session
	            session.setAttribute(usersession.getSessionID().getID(), usersession);
	            // Update database
	            // sessionBroker.updateSession(usersession);

        	}
        } else {
            log4j.warn("NO new session created due to SYSTEM_PT parameter!");
            try {
                usersession.setSessionID(new SessionID(new org.ei.util.GUID().toString(), 0));
            } catch (Exception e) {
                throw new SessionException(SystemErrorCodes.UNKNOWN_SESSION_ERROR, "Unable to create SYSTEM_PT session!", e);
            }
        }
        return usersession;
    }

    /**
     * Write a new EISESSION cookie to the response
     *
     * @param response
     * @param sessionID
     * @param entryToken
     */
    public void writeSessionCookie(SessionID sessionidObj) {
        log4j.info("Writing EISESSION Cookie...");
        EISessionCookie cookie = new EISessionCookie(sessionidObj);
        // A negative value means that the cookie is not stored persistently
        // and will be deleted when the Web browser exits. A zero value
        // causes the cookie to be deleted.
        // http://java.sun.com/j2ee/sdk_1.2.1/techdocs/api/javax/servlet/http/Cookie.html#setMaxAge(int)
        String qs = request.getQueryString();
        if (qs != null && qs.contains("CID=openXML")) {
            cookie.setMaxAge(0);	// Clear cookie for openXML requests
        } else {
            cookie.setMaxAge(-1);	// All other requests write this
        }
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    /**
     * Add request metadata (IP address, server name, etc.) to the current UserSession.
     *
     * @param usersession
     */
    private void addRequestMetadata(UserSession usersession) {
        log4j.info("Adding request metadata...");

        // String referrerURL = request.getHeader("Referer");
        String ipAddress = HttpRequestUtil.getIP(request);

        usersession.setProperty(UserSession.ENV_IPADDRESS, ipAddress);
        usersession.setProperty(UserSession.ENV_BASEADDRESS, getServerName());
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            usersession.setProperty(UserSession.ENV_UAGENT, userAgent);
        }

    }

    /**
     * Return the server name including port (if not 80)
     *
     * @param request
     * @return
     */
    private String getServerName() {
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        if (serverPort != 80) {
            serverName = serverName + ":" + Integer.toString(serverPort);
        }
        return serverName;
    }

    private void setUserCustomerIdInUserSession(UserSession userSession) {

        Map<String, String> textzones = userSession.getUserTextZones();
        String strTzCustomerId = textzones.get(UserPreferences.TZ_CUSTOMER_ID);

        if (strTzCustomerId != null && !strTzCustomerId.isEmpty()) {
            log4j.info("strTzCustomerId" + strTzCustomerId);
            userSession.getUser().setCustomerID(strTzCustomerId);
        } else {
            log4j.warn("***************Fence CUSTOMER_ID (EV - Legacy Back Office Information - Customer ID) is empty for the Customer Number : "
                + userSession.getUser().getCustomerID() + "*******************");
        }
    }

    private void setLocalHoldingsInUserSession(UserSession userSession) {

        Map<String, String> textzones = userSession.getUserTextZones();
        StringBuffer locHoldKeybuff = new StringBuffer();
        String strTmpLbl = null;
        String strTmpDefURL = null;
        String strTmpDynURL = null;
        String strTmpImgURL = null;

        for (int i = 0; i < UserPreferences.TZ_LINK_LABELS.length; i++) {

            strTmpLbl = textzones.get(UserPreferences.TZ_LINK_LABELS[i]);
            strTmpDynURL = textzones.get(UserPreferences.TZ_DYNAMIC_URLS[i]);

            if (strTmpLbl != null && !strTmpLbl.isEmpty() && strTmpDynURL != null && !strTmpDynURL.isEmpty()) {

                strTmpDefURL = textzones.get(UserPreferences.TZ_DEFAULT_URLS[i]);
                strTmpImgURL = textzones.get(UserPreferences.TZ_IMAGE_URLS[i]);

                locHoldKeybuff.append(UserPreferences.TZ_LINK_LABELS[i]);
                locHoldKeybuff.append(",");

                locHoldKeybuff.append(strTmpLbl);
                locHoldKeybuff.append(",");
                strTmpLbl = null;

                locHoldKeybuff.append(strTmpDynURL);
                locHoldKeybuff.append(",");
                strTmpDynURL = null;

                locHoldKeybuff.append(strTmpDefURL == null ? "" : strTmpDefURL);
                locHoldKeybuff.append(",");
                strTmpDefURL = null;

                locHoldKeybuff.append(strTmpImgURL == null ? "" : strTmpImgURL);
                locHoldKeybuff.append("|");
                strTmpImgURL = null;
            }

        }

       log4j.info("localHoldingKey" + locHoldKeybuff.toString());
       userSession.setLocalHoldingKey(locHoldKeybuff.toString());


    }

    /**
     * display Bulletin link if user has bulletin entitlements
     */

    protected void setBulletinEntitlements(UserSession userSession) {

        if (userSession != null) {
            UserPreferences userprefs = userSession.getUser().getUserPreferences();
            if (userprefs != null && userprefs.isBulletin()) {

                boolean lit = false;
                boolean pat = false;

                lit = !GenericValidator.isBlankOrNull(BulletinGUI.getLITCartridges(userSession.getUser().getCartridgeString()));
                pat = !GenericValidator.isBlankOrNull(BulletinGUI.getPATCartridges(userSession.getUser().getCartridgeString()));

                if (!lit && !pat) {
                    userprefs.setBulletinEnt(false);

                } else {
                    userprefs.setBulletinEnt(true);
                }
                userSession.getUser().setUserPreferences(userprefs);
            }
        }

    }
}
