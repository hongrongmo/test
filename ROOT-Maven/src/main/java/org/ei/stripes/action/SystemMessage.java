package org.ei.stripes.action;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.biz.security.IAccessControl;
import org.ei.biz.security.NoAuthAccessControl;
import org.ei.domain.DatabaseConfig;
import org.ei.exception.ErrorXml;
import org.ei.exception.ExceptionWriter;
import org.ei.session.AWSInfo;
import org.ei.session.UserSession;
import org.ei.stripes.exception.EVExceptionHandler;
import org.ei.stripes.util.HttpRequestUtil;
import org.ei.web.cookie.CookieHandler;
import org.w3c.dom.Document;

@UrlBinding("/system/{$event}.url")
public class SystemMessage extends EVActionBean {
    private final static Logger log4j = Logger.getLogger(SystemMessage.class);

    public static String SYSTEM_ENDSESSION_URL = "/system/endsession.url";
    public static String SYSTEM_ERROR_URL = "/system/error.url";
    public static String SYSTEM_TOKEN_URL = "/system/token.url";

    public static Resolution FEATURE_DISABLED_RESOLUTION;
    public static Resolution SYSTEM_ERROR_RESOLUTION;
    public static Resolution SYSTEM_ENDSESSION_RESOLUTION;
    public static Resolution SYSTEM_LOGOUT_RESOLUTION;
    public static Resolution SYSTEM_TOKEN_RESOLUTION;

    static {
        try {
            FEATURE_DISABLED_RESOLUTION = new RedirectResolution(SYSTEM_ERROR_URL + "?message="
                + URLEncoder.encode("You do not currently have access to this feature.", "UTF-8"));
            SYSTEM_ERROR_RESOLUTION = new RedirectResolution(SYSTEM_ERROR_URL + "?message=" + URLEncoder.encode("A system error has occurred.", "UTF-8"));

            // Legacy Token (Day Pass) operations
            SYSTEM_TOKEN_RESOLUTION = new RedirectResolution(SYSTEM_TOKEN_URL);

            SYSTEM_ENDSESSION_RESOLUTION = new RedirectResolution(SYSTEM_ENDSESSION_URL);
            SYSTEM_LOGOUT_RESOLUTION = new RedirectResolution(SYSTEM_ENDSESSION_URL + "?SYSTEM_LOGOUT=true;");
        } catch (Exception e) {
            throw new RuntimeException("Unable to build Resolution objects for SystemMessage!");
        }
    }

    // Request parameters
    private boolean invalidcid;
    private boolean endsession;
    private boolean error;
    private String message;
    private boolean daypass;

    /**
     * Override for the ISecuredAction interface. This ActionBean does NOT require authentication!
     */
    @Override
    public IAccessControl getAccessControl() {
        return new NoAuthAccessControl();
    }

    /**
     * Process the error from the request
     */
    public void processErrorXml() {
        String exception = (String) this.context.getRequest().getAttribute("exception");
        if (GenericValidator.isBlankOrNull(exception)) {
            ErrorXml errorXml= (ErrorXml) this.context.getRequest().getAttribute("errorXml");
            if (errorXml == null) {
                return;
            }
            exception = ExceptionWriter.toXml(errorXml);
            this.context.getRequest().setAttribute("exception", exception);
        }

        // This is small XML - just parse in place (no adapter)
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // never forget this!
        DocumentBuilder dBuilder;
        try {
            dBuilder = factory.newDocumentBuilder();
            Document doc = dBuilder.parse(new ByteArrayInputStream(exception.getBytes()));
            doc.getDocumentElement().normalize();

            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath xpath = xpathfactory.newXPath();
            message = (String) xpath.evaluate("DISPLAY", doc, XPathConstants.STRING);

        } catch (Exception e) {
            log4j.error("Unable to parse error page XML: " + e.getMessage());
            return;
        }

    }

    /**
     * Handles the legacy help URL - redirects to Quick Search
     *
     * @return Resolution
     */
    @HandlesEvent("help")
    @DontValidate
    public Resolution help() {
        return new RedirectResolution("/quick.url?CID=quickSearch");
    }

    /**
     * Invalid CID handler
     *
     * @return Resolution
     */
    @HandlesEvent("invalidcid")
    @DontValidate
    public Resolution invalidcid() {
        setRoom(ROOM.blank);
        getContext().getRequest().setAttribute("pageTitle", "Engineering Village - System Error");
        invalidcid = true;

        return new ForwardResolution("/WEB-INF/pages/world/systemmessage.jsp");
    }

    /**
     * End session handler
     *
     * @return Resolution
     */
    @HandlesEvent("endsession")
    @DontValidate
    @Deprecated
    public Resolution endsession() {
        HttpServletResponse response = context.getResponse();

        setRoom(ROOM.blank);
        context.getRequest().setAttribute("pageTitle", "Engineering Village - Session Ended");
        context.getRequest().getSession(true);
        endsession = true;
        daypass = (context.getUserSession().getEnvDayPass() != null);

        // Add custom header for Ajax calls that need to determine if there was a redirect!
        response.addHeader("EV_END_SESSION", "true");

        // Clear cookies - the EISESSION cookie will be cleared as part of the SessionManager logout below
        response.addCookie(CookieHandler.clearCookie("SECUREID"));
        response.addCookie(CookieHandler.clearCookie("CARS_COOKIE"));

        // Clear user session
        context.getRequest().getSession(false).setAttribute(context.getUserSession().getSessionID().getID(), null);
        context.getRequest().getSession(false).invalidate();

        return new ForwardResolution("/WEB-INF/pages/world/systemmessage.jsp");
    }

    /**
     * Error page handler
     *
     * @return Resolution
     */
    @HandlesEvent("error")
    @DontValidate
    public Resolution systemerror() {
        setRoom(ROOM.blank);
        getContext().getRequest().setAttribute("pageTitle", "Engineering Village - System Error");
        error = true;

        // Process any messages on the URL
        processErrorXml();

        // Add a google analytics event
        context.getRequest().setAttribute("usersession", context.getUserSession());
        createWebEvent(EVExceptionHandler.GA_EXCEPTION_CATEGORY, "SystemErrorPage", (this.message == null ? "No message" : this.message) +  " (Instance ID: " + new AWSInfo().getEc2Id() + ")");


        return new ForwardResolution("/WEB-INF/pages/world/systemerror.jsp");
    }

    /**
     * Token handler (legacy code for Day Pass handling)
     *
     * @return Resolution
     */
    @HandlesEvent("token")
    @DontValidate
    public Resolution token() {
        setRoom(ROOM.blank);
        getContext().getRequest().setAttribute("pageTitle", "Engineering Village - System Error");
        error = true;
        message = "This feature is no longer available.";

        return new ForwardResolution("/WEB-INF/pages/world/systemmessage.jsp");
    }

    /**
     * Information about current user account
     *
     * @return Resolution
     */
    @HandlesEvent("whoami")
    @DontValidate
    public Resolution whoami() {
        setRoom(ROOM.blank);
        getContext().getRequest().setAttribute("pageTitle", "Engineering Village - whoami");

        HttpServletRequest request = context.getRequest();

        StringBuffer buff = new StringBuffer();
        buff.append("Remote address: " + request.getRemoteAddr());
        buff.append("<br/>");
        buff.append("Remote Forwarded address: " + HttpRequestUtil.getIP(request) + " (raw: " + request.getHeader("x-forwarded-for") + ")");
        buff.append("<br/>");

        UserSession usersession = (UserSession) context.getUserSession();
        if (usersession != null && usersession.getUser() != null) {
            IEVWebUser user = usersession.getUser();
            DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

            String customerId = user.getCustomerID();
            String contractId = user.getContractID();
            String username = user.getUsername();
            String[] credentials = user.getCartridge();

            if (GenericValidator.isBlankOrNull(user.getWebUserId())) {
                buff.append("You are NOT logged in");
                buff.append("<br/>");
            } else {
                buff.append("You are logged in as custid: " + customerId + " (" + contractId + ")");
                buff.append("<br/>");
            }
            if (username != null) {
                buff.append("username: " + user.getUsername());
                buff.append("<br/>");
            }
            if (credentials != null) {
                buff.append("You credentials are: " + Arrays.asList(credentials));
                buff.append("<br/>");
                buff.append("Your default mask is: " + databaseConfig.getMask(credentials));
                buff.append("<br/>");
            }
            if (usersession.getSessionID() != null) {
                buff.append("Your session id is: " + usersession.getSessionID().getID());
                buff.append("<br/>");
            }
        }
        message = buff.toString();

        return new ForwardResolution("/WEB-INF/pages/world/whoami.jsp");
    }

    //
    //
    // GETTERS/SETTERS
    //
    //

    public boolean isInvalidcid() {
        return invalidcid;
    }

    public void setInvalidcid(boolean invalidcid) {
        this.invalidcid = invalidcid;
    }

    public boolean isEndsession() {
        return endsession;
    }

    public void setEndsession(boolean endsession) {
        this.endsession = endsession;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDaypass() {
        return daypass;
    }

}
