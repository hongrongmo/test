package org.ei.controller.logging;

import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.ei.controller.DataResponse;
import org.ei.session.UserSession;
import org.ei.stripes.util.HttpRequestUtil;

public class LogEntry {
    private static final org.apache.log4j.Logger log4j         = org.apache.log4j.Logger.getLogger(LogEntry.class);
    private static final Logger                  logger        = Logger.getInstance();

    private Properties                           logProperties = new Properties();
    private long                                 requestTime   = System.currentTimeMillis();
    private long                                 responseTime;
    private String                               IPAddress;
    private String                               sessionID;
    private String                               contentID     = "NO_CONTENT_ID";
    private String                               customerID;
    private String                               refURL;
    private String                               userAgent;
    private String                               appName;
    private Integer                              transNum;
    private String                               username;
    private String                               requestID;
    private String                               URLStem;
    private String                               URLQuery;

    /**
     * Enqueue the LogEntry to the logging service.
     *
     * @param logentry
     */
    public void enqueue() {
        // Enqueue to Logger!
        log4j.warn("********Logging usage entry '" + this.getContentID() + "', Session ID: " + this.getSessionID() + "', customer ID: " + this.getCustomerID());
        logger.enqueue(this);
    }

    /**
     * Build a LogEntry object from the DataResponse and EVActionBeanContext objects
     *
     * @param response
     * @param context
     * @return
     */
    public LogEntry addDataResponse(DataResponse dataResponse) {
        if (dataResponse == null) return this;

        // Add any logging properties ("LOG.*" entries from data layer)
        if (this.logProperties == null) this.logProperties = new Properties();
        if (dataResponse.getLogProperties() != null) {
            this.logProperties.putAll(dataResponse.getLogProperties());
        }

        if (null != dataResponse.getDataRequest().getContentDescriptor()) {
            this.setContentID(((dataResponse.getDataRequest()).getContentDescriptor()).getContentID());
        }

        // Only set request time if 0 - EVActionBean should have already set
        // it during LifecycleStage RequestInit.
        if (this.getRequestTime() == 0) {
            this.setRequestTime((dataResponse.getDataRequest()).getRequestTime());
        }
        this.setRequestID((dataResponse.getDataRequest()).getRequestID());
        this.setResponseTime(dataResponse.getResponseTime());

        return this;

    }

    /**
     * Add data from the current UserSession
     *
     * @param logentry
     * @param request
     */
    public LogEntry addUserSession(UserSession usersession) {
        if (usersession == null || usersession.getSessionID() == null) return this;

        // Set the request ID
        this.setRequestID(usersession.getSessionID().getID());

        // Get the logging properties object. Create if null;
        if (this.logProperties == null) this.logProperties = new Properties();

        // Get contract ID and entry token from usersession
        String contractID = usersession.getUser().getContractID();
        if (contractID != null) {
            this.logProperties.put("contract", contractID);
        }

        String entryToken = usersession.getProperty("ENTRY_TOKEN");
        if (entryToken != null) {
            this.logProperties.put("etoken", entryToken);
        }

        // Get some customer information
        this.setCustomerID((usersession.getUser()).getCustomerID());
        this.setUsername((usersession.getUser()).getUsername());
        this.setSessionID((usersession.getSessionID()).toString());

        return this;
    }

    /**
     * Add data from an HTTP request
     *
     * @param logentry
     * @param request
     */
    public LogEntry addHttpData(HttpServletRequest request) {
        if (request == null) return this;

        // Add UserAgent and referring URL
        this.setUserAgent(request.getHeader("User-Agent"));
        this.setRefURL(request.getHeader("referer"));
        this.setURLStem(request.getRequestURI());
        this.setURLQuery(request.getQueryString());

        // Add IP address
        this.setIPAddress(HttpRequestUtil.getIP(request));

        // Some accounts to department-based logging. Check the
        // cookies to see if this is needed.
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Properties props = this.getLogProperties();
            for (int i = 0; i < cookies.length; ++i) {
                Cookie cookie = cookies[i];
                if (cookie.getName().equals("DEPARTMENT")) {
                    if (props == null) props = new Properties();
                    props.put("department", cookie.getValue());
                    this.setLogProperties(props);
                } else if (cookie.getName().equals("CLIENTID")) {
                    String clientID = cookie.getValue();
                    props.put("clientid", clientID);
                }

            }
        }

        return this;
    }

    //
    //
    // GETTERS/SETTERS
    //
    //
    public String getURLQuery() {
        return this.URLQuery;
    }

    public void setURLQuery(String URLQuery) {
        this.URLQuery = URLQuery;
    }

    public String getURLStem() {
        return URLStem;
    }

    public void setURLStem(String URLStem) {
        this.URLStem = URLStem;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestID() {
        return this.requestID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setTransNum(Integer i) {
        this.transNum = i;
    }

    public Integer getTransNum() {
        return transNum;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRefURL() {
        return refURL;
    }

    public void setRefURL(String refURL) {
        this.refURL = refURL;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getRequestTime() {
        return this.requestTime;
    }

    public void setRequestTime(long beginTime) {
        this.requestTime = beginTime;
    }

    public long getResponseTime() {
        return this.responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public String getIPAddress() {
        return this.IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public String getSessionID() {
        return this.sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setContentID(String contentID) {
        this.contentID = contentID;
    }

    public String getContentID() {
        return this.contentID;
    }

    public String getCustomerID() {
        return this.customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public Properties getLogProperties() {
        return this.logProperties;
    }

    public void setLogProperties(Properties logProperties) {
        this.logProperties = logProperties;
    }

}
