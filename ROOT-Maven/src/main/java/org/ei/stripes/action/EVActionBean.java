package org.ei.stripes.action;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.After;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.ISecuredAction;
import org.ei.biz.security.NormalAuthRequiredAccessControl;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.controller.logging.LogEntry;
import org.ei.exception.SystemErrorCodes;
import org.ei.session.UserSession;
import org.ei.stripes.EVActionBeanContext;
import org.ei.stripes.util.HttpRequestUtil;
import org.ei.stripes.view.CustomizedLogo;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

public abstract class EVActionBean implements ActionBean, ISecuredAction {

    private final static Logger log4j = Logger.getLogger(EVActionBean.class);

    @Validate(trim = true, mask = ".*")
    protected String CID;

    /*
     * Some request parameters that are common to a lot of the EV actions
     */
    protected String sessionid;
    protected String displayLogin = "";
    @Validate(mask = "\\d*|compendex|inspec|cpx|ins|nti|usp|crc|c84|pch|chm|cbn|elt|ept|ibf|geo|eup|upa|ref|pag|zbf|upt|ibs|grf")
    protected String database;
    protected int usermask; 	// User Database mask (available DBs)
    protected String source = "";
    protected String nexturl = "";
    protected String backurl = "";
    protected boolean personalization;
    protected String modelXml;
    protected String baseaddress = "";
    // Page message
    protected String message;
    public String isActionBeanInstance;
    protected boolean showpatentshelp;
    protected CustomizedLogo customlogo;
    private String s3FigUrl = null;
    
   	private boolean showLoginBox = true;
    private StopWatch requeststopwatch = null;

    @Validate(mask = "-{0,1}\\d*")
    protected int errorCode = SystemErrorCodes.INIT;

    /**
     * Override for the ISecuredAction interface. By default all ActionBeans require either Individual or Guest auth access. Action Beans with different
     * requirements (e.g. no access control or just Individual) should override this method!
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NormalAuthRequiredAccessControl();
    }

    /**
     * Return the rulevel (usually passed as -Dcom.elsevier.env=xxxx)
     * @return
     */
    public String getRunlevel() {
    	return System.getProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL);
    }
    
    protected List<String> comments;   // Comments from biz (JSP) layer

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<String> getComments() {
        return this.comments;
    }

    /**
     * Create a timestamp for request start. This is mainly used for the Google Analaytics call in standard.jsp.
     */
    private Date starttime;

    public long getStarttime() {
        if (starttime == null) {
            log4j.warn("Timestamp not set!  Creating now...");
            starttime = new Date();
        }
        return starttime.getTime();
    }

    @Before(stages = LifecycleStage.HandlerResolution)
    private void setTimestamp() {
        starttime = new Date();
        this.requeststopwatch = new Log4JStopWatch();
    }

    @After(stages = LifecycleStage.ActionBeanResolution)
    private void init() {
        if (this.getContext() != null) {
            this.getContext().getRequest().setAttribute(EVProperties.REQUEST_ATTRIBUTE, EVProperties.getApplicationProperties());
        }
    }

    /**
     * WARNING!!!!!
     *
     * At the completion of every request re-write the session cookie. We do this in order to handle changes to the user session. If the session has been
     * updated then the version number will have changed. This signals other JVMs that their SessionCache object is out-of-date and needs to be updated.
     */
    @After(stages = LifecycleStage.EventHandling)
    public void setSessionCookie() {
        // Use method from context object. This is for unit testing - override this method
        // have it do nothing!
        context.setSessionCookie();
        if (this.requeststopwatch != null) {
            this.requeststopwatch.stop("request." + this.getClass().getSimpleName() + "." + this.getContext().getEventName());
        }
        // If SYSTEM_PT is present, delete session!
        /*
         * HttpServletRequest request = context.getRequest(); if (request != null &&
         * !GenericValidator.isBlankOrNull(request.getParameter(SessionManager.REQUEST_PT))) { try {
         * EVSessionListener.cleanSessionTables(context.getUserSession().getSessionID().getID()); } catch (Exception e) {
         * log4j.error("Unable to clean up 'fake' SYSTEM_PT session", e); } }
         */
    }

    /**
     * The Stripes context object.
     */
    protected EVActionBeanContext context;

    public EVActionBeanContext getContext() {
        return this.context;
    }

    public void setContext(ActionBeanContext arg0) {
        this.context = (EVActionBeanContext) arg0;
    }

    public CustomizedLogo getCustomlogo() {
        return customlogo;
    }

    public void setCustomlogo(CustomizedLogo customlogo) {
        this.customlogo = customlogo;
    }

    /**
     * Return the current year
     *
     * @return current year
     */
    public int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * Sets the "room" for EV - search, selected records, etc.
     */
    private ROOM room = ROOM.blank;

    public static enum ROOM {
        blank, welcome, search, selectedrecords, mysettings, tagsgroups, bulletins;
    };

    public boolean isRoomHome() {
        return this.room == ROOM.welcome;
    }

    public boolean isRoomSearch() {
        return this.room == ROOM.search;
    }

    public boolean isRoomSelectedRecords() {
        return this.room == ROOM.selectedrecords;
    }

    public boolean isRoomMySettings() {
        return this.room == ROOM.mysettings;
    }

    public boolean isRoomTagsGroups() {
        return this.room == ROOM.tagsgroups;
    }

    public boolean isRoomBulletins() {
        return this.room == ROOM.bulletins;
    }

    /**
     * Get the current room.
     *
     * @return String
     */
    public ROOM getRoom() {
        return room;
    }

    /**
     * Set the current room.
     *
     * @param room
     */
    public void setRoom(ROOM room) {
        if (room == null) {
            this.room = ROOM.blank;
        } else {
            this.room = room;
        }
    }

    private List<LocalizableError> globalerrors;
    public static String GLOBALERROR_REQUEST_KEY = "globalerrors";

    /**
     * Adds an error message to the global errors/messages list
     *
     * @param request
     * @param resourcekey
     */
    protected void addGlobalError(HttpServletRequest request, String resourcekey, Object... resourceparms) {
        if (globalerrors == null) {
            globalerrors = new ArrayList<LocalizableError>();
        }
        globalerrors.add(new LocalizableError(resourcekey, resourceparms));
        request.setAttribute(GLOBALERROR_REQUEST_KEY, globalerrors);
    }

    protected List<LocalizableError> getGlobalErrors() {
        return globalerrors;
    }

    /**
     * Go to appropriate start page, assumes NO cancel URL!
     *
     * @param userSession
     * @return
     * @throws MalformedURLException
     */
    protected Resolution gotoStartPage(UserSession userSession) {
        return gotoStartPage(userSession, false);
    }

    /**
     * Put user on appropriate start page
     *
     * @param user
     * @return
     * @throws MalformedURLException
     */
    protected Resolution gotoStartPage(UserSession userSession, boolean iscancel) {
        String startpage = userSession.getUser().getStartPage();
        log4j.info("User startpage: " + startpage);

        String nextURL = userSession.getNextUrl();
        String cancelURL = userSession.getBackUrl();
        userSession.removeProperty(UserSession.NEXT_URL);

        if (iscancel) {
            if (StringUtils.isNotBlank(cancelURL)) {
                userSession.removeProperty(UserSession.BACK_URL);
                log4j.info("Cancel URL found: '" + cancelURL + "'");
                try {
                    return new RedirectResolution(URLDecoder.decode(cancelURL, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    log4j.error("Unable to redirect to: " + cancelURL, e);
                    return redirectToUserStartPage(startpage);
                }
            } else {
                log4j.info("Cancel indicated but Cancel URL is empty!  Returning to start page...");
                return redirectToUserStartPage(startpage);
            }
        } else if (isValidNextUrl(nextURL)) {
            log4j.info("Next URL found: " + nextURL);
            try {

                // To support search widget functionality, we are forcing the url to HTTP
                String decodedNextUrl = URLDecoder.decode(nextURL, "UTF-8");
                if (decodedNextUrl.contains("/search/widgetSubmit.url")) {
                    return redirectToNonHttpPage(decodedNextUrl);
                }

                return new RedirectResolution(URLDecoder.decode(nextURL, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log4j.error("Unable to redirect to: " + nextURL, e);
                return redirectToUserStartPage(startpage);
            }
        } else {
            log4j.info("Plain start page redirect");
            return redirectToUserStartPage(startpage);
        }
    }

    private Resolution redirectToUserStartPage(String startpage) {
        String path = getStartPage();
        log4j.info("Redirecting to path: " + path);
        return new RedirectResolution(path, false);
    }

    /**
     * Redirect to non http page.
     *
     * @param url
     *            the url
     * @return the resolution
     */
    private Resolution redirectToNonHttpPage(String url) {
        HttpServletRequest request = context.getRequest();
        String port = "";
        if (!request.isSecure() && request.getServerPort() > 0 && request.getServerPort() != 80 && request.getServerPort() != 443) {
            port = ":" + Integer.toString(request.getServerPort());
        } else if (!GenericValidator.isBlankOrNull(EVProperties.getProperty(ApplicationProperties.HTTP_PORT))) {
            port = ":" + EVProperties.getProperty(ApplicationProperties.HTTP_PORT);
        }
        String path = "http://" + HttpRequestUtil.getDomain(request) + port + url;
        log4j.info("Redirecting to path: " + path);
        return new RedirectResolution(path, false);
    }

    /**
     * Return the user's start page
     *
     * @return
     */
    private String getStartPage() {
        String startpage = context.getUserSession().getUser().getStartPage();
        String path = EVPathUrl.EV_QUICK_SEARCH.value();

        if (GenericValidator.isBlankOrNull(startpage) || "-".equals(startpage) || "quickSearch".equals(startpage)) {
            path = EVPathUrl.EV_QUICK_SEARCH.value();
        } else if ("expertSearch".equals(startpage)) {
            path = EVPathUrl.EV_EXPERT_SEARCH.value();
        } else if ("thesHome".equals(startpage)) {
            path = EVPathUrl.EV_THES_INIT_SEARCH.value();
        } else if ("ebookSearch".equals(startpage)) {
            // TMH - eBook removal story, go to QUICK search as start page
            log4j.warn("User start page is eBook!  Changing to Quick...");
            path = EVPathUrl.EV_QUICK_SEARCH.value();
        } else if ("easySearch".equals(startpage)) {
            path = EVPathUrl.EV_EXPERT_SEARCH.value();
        }

        return path;
    }

    private boolean isValidNextUrl(String nextURL) {
        // False if URL is blank
        if (StringUtils.isBlank(nextURL))
            return false;

        try {
            // False if URL is one of the start page URLs
            String decode = URLDecoder.decode(nextURL, "UTF-8");
            if (decode.contains(getStartPage()) || decode.contains(EVPathUrl.EV_HOME.value())) {
                return false;
            }
            // False if self-manra URL. This happens because we automatically
            // save requests when LOGIN_FULL is displayed.
            if (decode.contains("/customer/authenticate/manra.url")) {
                return false;
            }
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        return true;
    }

    /**
     * The next 2 methods work together to produce Usage logging. The startlog() method will set the current time for the start and finalizelog() will set then
     * ending time and enqueue to the Logger.
     *
     */
    @After(stages = LifecycleStage.BindingAndValidation)
    protected void startlog() {
        LogEntry logentry = this.context.getLogEntry();
        logentry.addHttpData(this.context.getRequest());
        String appName = EVProperties.getProperty(EVProperties.APP_NAME);
        logentry.setAppName(appName);

    }

    @After(stages = LifecycleStage.RequestComplete)
    protected void finalizelog() {
        LogEntry logentry = this.context.getLogEntry();
        UserSession usersession = this.context.getUserSession();
        // Ensure usersession is not null! This can happen on redirects from
        // legacy URLs to new URLs. No need to log those!
        if (logentry != null && usersession != null) {
            // TMH - 09/16/13 only log usage when there is a valid customer. Un-authenticated
            // requests were filling up usage DB table with info
            IEVWebUser user = usersession.getUser();
            if (user != null && user.isCustomer()) {
                logentry.addUserSession(this.context.getUserSession());
                logentry.setResponseTime(System.currentTimeMillis());
                logentry.enqueue();
            }
        }
    }

    /**
     * Return the base help URL
     *
     * @return
     */
    public String getHelpUrl() {
        return EVProperties.getProperty(EVProperties.HELP_URL);
    }

    // Context for the help link. Should be set in Event handlers
    // of ActionBean instances
    protected String helpcontext = "";

    /**
     * Get the context for the help URL
     *
     * @return
     */
    public String getHelpcontext() {
        return helpcontext;
    }

    /**
     * Set the context for the help URL
     */
    public void setHelpcontext(String helpcontext) {
        this.helpcontext = helpcontext;
    }

    @Before(stages = LifecycleStage.BindingAndValidation)
    public void setHelpcontext() {
        String strRequestURI = context.getRequest().getRequestURI().toLowerCase();
        if (strRequestURI.contains(".url")) {
            strRequestURI = strRequestURI.substring(0, strRequestURI.indexOf("."));
        }
        strRequestURI = "help.context" + strRequestURI.replaceAll("/", ".");
        setHelpcontext(EVProperties.getProperty(strRequestURI));
        log4j.info("Help context set: " + helpcontext);
    }

    /**
     * Convenience method to create a filename with timestamp for output
     *
     * @param baseFilename
     * @return
     * @throws UnsupportedEncodingException
     */
    protected String getContentDispositionFilenameTimestamp(String baseFilename) throws UnsupportedEncodingException {
        java.util.Calendar calCurrentDate = java.util.GregorianCalendar.getInstance();
        StringBuffer strbFilename = new StringBuffer();
        strbFilename.append(calCurrentDate.get(java.util.Calendar.DAY_OF_MONTH));
        strbFilename.append("-");
        strbFilename.append(calCurrentDate.get(java.util.Calendar.MONTH) + 1);
        strbFilename.append("-");
        strbFilename.append(calCurrentDate.get(java.util.Calendar.YEAR));
        strbFilename.append("-");
        strbFilename.append(System.currentTimeMillis());
        strbFilename.append("_");
        strbFilename.append(baseFilename);

        return java.net.URLEncoder.encode(strbFilename.toString(), "UTF-8");

    }

    //
    //
    // GETTERS/SETTERS
    //
    //
    public String getDisplayLogin() {
        return displayLogin;
    }

    public void setDisplayLogin(String displayLogin) {
        this.displayLogin = displayLogin;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public int getUsermask() {
        return usermask;
    }

    public void setUsermask(int usermask) {
        this.usermask = usermask;
    }

    public String getModelXml() {
        return modelXml;
    }

    public void setModelXml(String modelXml) {
        this.modelXml = modelXml;
    }

    public String getNexturlencoded() throws UnsupportedEncodingException {
        if (nexturl == null)
            return null;
        return URLEncoder.encode(nexturl, "UTF-8");
    }

    public String getNexturl() {
        return nexturl;
    }

    public void setNexturl(String nexturl) {
        this.nexturl = nexturl;
    }

    public String getBackurl() {
        if (GenericValidator.isBlankOrNull(backurl)) {
            backurl = context.getRequest().getHeader("Referer");
            if (GenericValidator.isBlankOrNull(backurl)) {
                backurl = EVPathUrl.EV_HOME.value();
            }
        }
        return backurl;
    }

    public void setBackurl(String backurl) {
        this.backurl = backurl;
    }

    public boolean isPersonalization() {
        return personalization;
    }

    public void setPersonalization(boolean personalization) {
        this.personalization = personalization;
    }

    public String getSessionid() {
        if (GenericValidator.isBlankOrNull(this.sessionid)) {
            if (context.getUserSession() != null && context.getUserSession().getSessionID() != null) {
                return context.getUserSession().getSessionID().getID();
            } else {
                log4j.warn("Session ID cannot be retrieved!  UserSession is null or SessionID object is null...");
            }
        }
        return this.sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getCID() {
        return CID;
    }

    public void setCID(String cID) {
        CID = cID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpServletRequest getRequest() {
        return getContext().getRequest();
    }

    public String getIsActionBeanInstance() {
        return isActionBeanInstance;
    }

    public void setIsActionBeanInstance(String isActionBeanInstance) {
        this.isActionBeanInstance = isActionBeanInstance;
    }

    public boolean isShowpatentshelp() {
        return showpatentshelp;
    }

    public void setShowpatentshelp(boolean showpatentshelp) {
        this.showpatentshelp = showpatentshelp;
    }

    public boolean getShowLoginBox() {
        return showLoginBox;
    }

    public void setShowLoginBox(boolean showLoginBox) {
        this.showLoginBox = showLoginBox;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMaintenanceMsg(){
    	String msg = null;
    	boolean isEnabled = Boolean.parseBoolean((EVProperties.getProperty(EVProperties.DOWNTIME_MESSAGE_ENABLED)));
    	if(isEnabled){
    		String message = EVProperties.getProperty(EVProperties.DOWNTIME_MESSAGE_TEXT);
    		if(message != null){
    			msg = message;
    			String color = EVProperties.getProperty(EVProperties.DOWNTIME_MESSAGE_COLOR);
    			if(color != null){
    				msg="<span style=\"color:"+color+"\">"+msg+"</span>";
    			}
    		}
    	}
    	return msg;
    }
    
    public String getIE7Msg(){
    	String msg = null;
    	boolean isEnabled = Boolean.parseBoolean((EVProperties.getProperty(EVProperties.IE7_WARN_MSG_ENABLED)));
    	if(isEnabled){
    		msg = EVProperties.getProperty(EVProperties.IE7_WARN_MSG_TEXT);
    	}
    	return msg;
    }
    
    public String getS3FigUrl() {
    	 String s3figurl = (EVProperties.getProperty(EVProperties.S3_FIG_URL));
         if (s3figurl == null)
        	 s3figurl = "https://s3.amazonaws.com/ev-data/tmp/fig/";
         return s3figurl;
	}

    /**
     * Create a Web Event to be recorded on page load and add it to the list in the request
     *
     * @param cat
     *            - Category
     * @param action
     *            - the event action
     * @param label
     *            - the Label for the event
     * @return the event created
     */
    public GoogleWebAnalyticsEvent createWebEvent(String cat, String action, String label) {

        GoogleWebAnalyticsEvent webEvent = new GoogleWebAnalyticsEvent();

        webEvent.setCategory(cat);
        webEvent.setAction(action);
        webEvent.setLabel(label);
        addWebEvent(webEvent);

        return webEvent;

    }

    /**
     * Add a webevent to the request object
     *
     * @param webEvent
     */
    protected void addWebEvent(GoogleWebAnalyticsEvent webEvent) {
        ArrayList<GoogleWebAnalyticsEvent> eventList = (ArrayList<GoogleWebAnalyticsEvent>) context.getRequest().getAttribute(
            WebAnalyticsEventProperties.WEB_EVENT_REQUEST_NAME);

        if (eventList == null) {
            eventList = new ArrayList<GoogleWebAnalyticsEvent>();
        }

        eventList.add(webEvent);

        context.getRequest().setAttribute(WebAnalyticsEventProperties.WEB_EVENT_REQUEST_NAME, eventList);
    }

    /**
     * append a list of events to the current event list.
     *
     * @param webEvents
     */
    protected void appendWebEventList(List<GoogleWebAnalyticsEvent> webEvents) {
        List<GoogleWebAnalyticsEvent> eventList = (List<GoogleWebAnalyticsEvent>) context.getRequest().getAttribute(
            WebAnalyticsEventProperties.WEB_EVENT_REQUEST_NAME);

        if (eventList == null) {
            eventList = new ArrayList<GoogleWebAnalyticsEvent>();
        }

        eventList.addAll(webEvents);

        context.getRequest().setAttribute(WebAnalyticsEventProperties.WEB_EVENT_REQUEST_NAME, eventList);
    }

    

    public String getBaseaddress() {
        return context.getRequest().getServerName();
    }

}
