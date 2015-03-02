package org.ei.stripes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.FlashScope;
import net.sourceforge.stripes.exception.SourcePageNotFoundException;
import net.sourceforge.stripes.validation.ValidationErrors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.controller.DataRequest;
import org.ei.controller.DataResponse;
import org.ei.controller.DataResponseBroker;
import org.ei.controller.OutputPrinter;
import org.ei.controller.content.ContentConfig;
import org.ei.controller.content.ContentDescriptor;
import org.ei.controller.logging.LogEntry;
import org.ei.exception.InfrastructureException;
import org.ei.exception.ServiceException;
import org.ei.exception.SessionException;
import org.ei.service.cars.CARSStringConstants;
import org.ei.service.cars.Impl.CARSResponse;
import org.ei.session.SessionManager;
import org.ei.session.UserSession;
import org.ei.stripes.action.SystemMessage;
import org.ei.web.cookie.CookieHandler;
import org.ei.web.cookie.EISessionCookie;

/**
 * This class extends the Stripes ActionBeanContext class to provide
 * app-specific functionality
 */
public class EVActionBeanContext extends ActionBeanContext {
    public static String REDIR_PAGE_INVALID_CID = "invalidCID";
    public static String REDIR_TOKEN_EXPIRED = "tokenExpired";
    public static String REDIR_TOKEN_CONCURRENT = "tokenConcurrent";
    public static String REDIR_PAGE_GENERAL_EXCEPTION = "generalException";
    public static String HOME_CID = "home";
    public static String XML_CID = "openXML";
    public static String RSS_CID = "openRSS";
    public static String REDIR_PAGE_SESSION_EXPIRED = "endSession";

	private static Logger log4j = Logger.getLogger(EVActionBeanContext.class);
	private static DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private String timestamp = fmt.format(new Date());
	private CARSResponse carsResponse;
	public CARSResponse getCarsResponse() {
		return carsResponse;
	}
	public void setCarsResponse(CARSResponse carsResponse) {
		this.carsResponse = carsResponse;
	}

	/**
	 * The ContentConfig object.  This should be initialized during request
	 * processing by reading from ControllerConfig object
	 */
    private ContentConfig contentConfig;
	public ContentConfig getContentConfig() {
		return contentConfig;
	}
	public void setContentConfig(ContentConfig contentConfig) {
		this.contentConfig = contentConfig;
	}

	/**
	 * The RuntimeProperties object.  This should be initialized during request
	 * processing by reading from ControllerConfig object
	 */
    private ApplicationProperties runtimeProperties;
	public ApplicationProperties getApplicationProperties() {
		return runtimeProperties;
	}
	public void setApplicationProperties(ApplicationProperties runtimeProperties) {
		this.runtimeProperties = runtimeProperties;
	}

	/**
	 * Convenience method to get UserSession
	 * @throws SessionException
	 */
	UserSession usersession;
	public void setUserSession(UserSession usersession) {
		this.usersession = usersession;
	}
	public UserSession getUserSession() {
		if (usersession == null) {
			SessionManager sessionmanager = new SessionManager(this.getRequest(), this.getResponse());
			return sessionmanager.buildNewUserSession();
		}
		return usersession;
	}
	public UserSession updateUserSession(UserSession usersession) throws SessionException {
		SessionManager sessionmanager = new SessionManager(getRequest(), getResponse());
		return sessionmanager.updateUserSession(usersession, false, false);
	}

    // The LogEntry object and getter
    private LogEntry logentry = new LogEntry();
    public LogEntry getLogEntry() {
        return logentry;
    }

	/**
	 * Return the account string for Google Analytics.  Set per environment (dev/cert/prod)
	 * @return
	 */
	public String getGoogleAnalyticsAccount() {
		return EVProperties.getProperty(EVProperties.GOOGLE_ANALYTICS_ACCOUNT);
	}

    public void setSessionCookie() {
        HttpServletRequest request = this.getRequest();
        HttpServletResponse response = this.getResponse();
        UserSession usersession = this.getUserSession();
        if (usersession != null) {
        	if(null != usersession.getSessionID()){
        	    response.addCookie(new EISessionCookie(usersession.getSessionID()));
        	}
            String entryToken = request.getParameter("SYSTEM_ENTRY_TOKEN");
            if (entryToken != null) {
                Cookie dpCookie = new Cookie("DAYPASS", "true");
                dpCookie.setMaxAge(86400);
                dpCookie.setPath("/");
                response.addCookie(dpCookie);
            }

            String encodedACWRequestParm = getEncodeACWRequestParameter();
            if (StringUtils.isNotBlank(encodedACWRequestParm)) {
                Cookie acwCookie = new Cookie(CARSStringConstants.ACW.value(), encodedACWRequestParm);
                acwCookie.setMaxAge(-1);
                acwCookie.setPath("/");
                acwCookie.setDomain(".engineeringvillage.com");
                response.addCookie(acwCookie);

            }

        }
    }



    /**
     * Return the encoded ACW request parms
     * @param request
     * @return
     */
    protected String getEncodeACWRequestParameter() {
        try {
            if (!GenericValidator.isBlankOrNull(this.getRequest().getParameter(CARSStringConstants.ACW.value()))) {
                return URLEncoder.encode(this.getRequest().getParameter(CARSStringConstants.ACW.value()), CARSStringConstants.URL_ENCODE.value());
            }
        } catch (UnsupportedEncodingException e) {
            log4j.warn("Problem in decoding the SSO Key using " + CARSStringConstants.URL_ENCODE.value() + e.getMessage());
        }
        return "";
    }

/**
 * Builds the ContentDescriptor object from
 * @param usess
 * @param request
 * @return
 * @throws Exception
 */
	public ContentDescriptor buildContentDescriptor(UserSession usess, HttpServletRequest request) throws Exception {
		log4j.info("Starting buildContentDescriptor...");
		if (usess == null || usess.getUser() == null) {
			log4j.warn("No user session or user obect!");
			return null;
		}

		ContentConfig contentConfig = EVProperties.getContentConfig();

		IEVWebUser u = usess.getUser();
		String contentID = request.getParameter("CID");
		if (contentID == null) {
			log4j.warn("No CID found on request, returning HOME_CID!");
			contentID = HOME_CID;
		}


		/*
		 * Look for customized content.
		 */
		 log4j.info("Building ContentDescriptor object, customer ID = "
			+ u.getCustomerID()
			+ ", content ID = "
			+ (contentID == null ? "null" : contentID));
		ContentDescriptor cd = contentConfig.getContentDescriptor(u.getCustomerID(), contentID);
		if (cd == null) {
			cd = contentConfig.getContentDescriptor("world_"+contentID);
		}

		return cd;
	}

	/**
	 * Returns a DataRequest object representing the current request
	 *
	 * @param usess
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public DataRequest getDataRequest(UserSession usess,
			HttpServletRequest request) throws Exception {

			log4j.info("Starting getDataRequest...");
		// Building ContentDescriptor
		ContentDescriptor cd = buildContentDescriptor(usess, request);
		if (cd == null) {
			log4j.warn("Unable to build ContentDescriptor!");
			return null;
		}

		/*
		 * We have the ContentDescriptor, now lets get the RequestParameters.
		 */

		Enumeration<String> en = request.getParameterNames();
		Properties requestParams = new Properties();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			if (key.indexOf("SYSTEM") != 0) {
				requestParams.put(key, request.getParameterValues(key));
			}
		}

		return new DataRequest(usess, requestParams, cd);
	}

	/**
	 * Retrieves the DataResponse for the current request.
	 *
	 * @param request Current HTTP request
	 * @param dataCacheDir Data cache directory string
	 * @param printer OutputPrinter object (used by DataResponseBroker for writing direct response)
	 * @param dataRequest DataRequest object
	 * @return
	 * @throws Exception
	 */
	public DataResponse getDataResponse(
			HttpServletRequest request,
			String dataCacheDir,
			OutputPrinter printer,
			DataRequest dataRequest) throws Exception {

		// Use the DataResponseBroker to retrieve the response
		DataResponse dataResponse = null;
		DataResponseBroker resBroker = DataResponseBroker.getInstance(dataCacheDir);
		dataResponse = resBroker.getDataResponse(printer, dataRequest);
		return dataResponse;
	}


	public DataResponse getDataResponseForActionBean(
			HttpServletRequest request,
			String dataCacheDir,
			OutputPrinter printer,
			DataRequest dataRequest, String xmlUrl) throws ServiceException, InfrastructureException  {

		// Use the DataResponseBroker to retrieve the response
		DataResponse dataResponse = null;
		DataResponseBroker resBroker = DataResponseBroker.getInstance(dataCacheDir);
		dataResponse = resBroker.getDataResponseForActionBean(printer, dataRequest, xmlUrl);
		return dataResponse;
	}


	/**
	 * Convenience method for determining if user is individually authenticated.
	 * Same thing can be accomplished with:
	 *
	 * context.getUserSession().getUser().isIndividuallyAuthenticated();
	 *
	 * @return boolean true if user is individually authenticated, false
	 *         otherwise.
	 */
	public boolean isUserLoggedIn() {
		try {
			UserSession usersession = getUserSession();
			if (usersession == null){
				return false;
			}
			IEVWebUser user = usersession.getUser();
			if (user == null){
				return false;
			}
			return user.isIndividuallyAuthenticated();
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the current user id. NOTE that this will be null or empty when the
	 * user is NOT logged in!
	 *
	 * @return Userid
	 */
	public String getUserid() {
		try {
			UserSession usersession = getUserSession();
			if (usersession == null)
				return null;
			if (usersession.getUser() == null)
				return null;
			return usersession.getUser().getUserId();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return timestamp
	 * @return
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Set timestamp
	 * @param timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


    /**
     * Builds a new OutputPrinter object.  This is used to write a streamed
     * response instead of using a Stripes Action/JSP to service a request
     *
     * @param request
     * @param response
     * @param appendSession
     * @return
     * @throws Exception
     */
	public OutputPrinter buildPrinter(HttpServletRequest request,HttpServletResponse response,boolean appendSession) throws Exception {

		OutputPrinter printer = new OutputPrinter(response,appendSession,getServerName(request));

		// Use the session ID from the cookie if available
        if(CookieHandler.getSessionID(request) == null)
        {
            printer.setAppendSession(true);
        }
        else
        {
            printer.setAppendSession(false);
        }

        // Get the entry token from the request if available
        String entryToken = request.getParameter("SYSTEM_ENTRY_TOKEN");
        if(entryToken != null)
        {
			printer.setEntryToken(entryToken);
		}

        // Get the cache from the request
		if(request.getAttribute("cache") != null)
		{
			printer.setCacheID((String)request.getAttribute("cache"));
		}

		return printer;
	}

	/**
	 * The DataRequest object representing the current request into EV.
	 */
	private DataRequest datarequest;
	public DataRequest getDataRequest() {
		return datarequest;
	}
	public void setDataRequest(DataRequest datarequest) {
		this.datarequest = datarequest;
	}

	/**
	 * Builds the DataRequest object for the current request
	 * @param usess
	 * @param request
	 * @return
	 * @throws UnknownHostException
	 * @throws Exception
	 */
    public DataRequest buildDataRequest(UserSession usess, HttpServletRequest request) throws UnknownHostException
    {

    	usess.getUser();
        Enumeration<?> en = request.getParameterNames();
        Properties requestParams = new Properties();
        while(en.hasMoreElements())
        {
            String key = (String)en.nextElement();
            if(key.indexOf("SYSTEM") != 0) {
                requestParams.put(key, request.getParameterValues(key));
            }
        }


        return new DataRequest(usess, requestParams);
    }

	/**
	 * Return the server name including port (if not 80)
	 *
	 * @param request
	 * @return
	 */
	public String getServerName(HttpServletRequest request) {
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		if(serverPort != 80)
		{
			serverName = serverName+":"+Integer.toString(serverPort);
		}
		return serverName;
	}


	/**
	 * Return the HelpUrl  from web.xml
	 *
	 * @return
	 * @throws Exception
	 */
	public String getHelpUrl() {
		return EVProperties.getProperty(EVProperties.HELP_URL);
	}
	public HttpSession getExistingSession() {
		return getRequest().getSession(false);
	}

    /* (non-Javadoc)
     * @see net.sourceforge.stripes.action.ActionBeanContext#getSourcePageResolution()
     */
    @Override
    public Resolution getSourcePageResolution() throws SourcePageNotFoundException {
        //
        // This method is being overridden to handle the following use case:
        // 1) An Action has one or more validated fields that have FAILE validation
        // 2) The validation failure happened OUTSIDE of a form submit so there is NO source page
        // 3) User needs to be sent to error page
        //
        ValidationErrors errors = super.getValidationErrors();
        if (errors != null && errors.size() > 0) {
            if (super.getSourcePage() == null) {
                FlashScope scope = FlashScope.getCurrent(this.getRequest(), true);
                scope.put(ErrorMessageInterceptor.CTX_KEY, errors);
                return SystemMessage.SYSTEM_ERROR_RESOLUTION;
            }
        }
        return super.getSourcePageResolution();
    }
    

}
