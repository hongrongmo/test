package org.ei.session;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.biz.personalization.EVWebUser;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.util.StringUtil;
import org.ei.web.util.RequestDumper;

public class SessionService extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private StringUtil sUtil = new StringUtil();
    UserBroker uBroker = new UserBroker();
    SessionBroker sessionBroker;

    public static final String EISESSION_COOKIE = "EISESSION";
    public static final String USE_SESSION_PARAM = "SYSTEM_USE_SESSION_PARAM";
    public static final String NEWSESSION = "SYSTEM_NEWSESSION";

    public static final String REQUEST_GET = "g";
    public static final String REQUEST_NOAUTH = "x";
    public static final String REQUEST_UPDATE = "s";
    public static final String REQUEST_TOUCH = "t";
    public static final String REQUEST_ACTIVE = "c";
    public static final String REQUEST_LOGOUT = "l";
    public static final String REQUEST_VALIDATEIP = "r";

    /**
     * Initialize the service
     */
    public void init() throws ServletException {
        ServletConfig config = getServletConfig();
        sessionBroker = SessionBroker.getInstance(Long.parseLong(config.getInitParameter("expireIn")));
    }

    /**
     * Servlet to retrieve a User's session.
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        UserSession userSession = null;

        RequestDumper.dump(request, "params");

        String prodname = request.getParameter("ap");
        String username = request.getParameter("un");
        String password = request.getParameter("pa");
        String ipAddress = request.getParameter("ip");
        String refUrl = request.getParameter("rf");
        String entryToken = request.getParameter("et");
        String sessionID = request.getParameter("si");
        String sessionVersion = request.getParameter("sv");
        String requestType = request.getParameter("rt");

        // If entry token or username are present we need to authenticate
        // so remove the current session ID
        if (entryToken != null || username != null) {
            sessionID = null;
        }

        try {
            ServletOutputStream out = response.getOutputStream();

            //
            // Request is to retrieve user session - can come from authentication
            // procedure or from database
            //
            if (requestType.equals(REQUEST_GET)) {
                if (sessionID == null) {
                    // Retrieve user information by username/password, IP, referrer URL or token
                    IEVWebUser u = uBroker.getUser(prodname, username, password, ipAddress, refUrl, entryToken);

                    userSession = sessionBroker.createSession(u, null);
                    userSession.setStatus(SessionStatus.NEW);
                    userSession.setProperty(UserSession.RECORDS_PER_PAGE, "25");

                } else {
                    // Attempt to retrieve from database store first
                    userSession = sessionBroker.getUserSession(new SessionID(sessionID, Integer.parseInt(sessionVersion)));
                    if (userSession == null) {
                        // Retrieve user information by username/password, IP, referrer URL or token
                        IEVWebUser u = uBroker.getUser(prodname, username, password, ipAddress, refUrl, entryToken);

                        userSession = sessionBroker.createSession(u, null);
                        userSession.setStatus(SessionStatus.NEW_HAD_EXPIRED);
                        userSession.setProperty(UserSession.RECORDS_PER_PAGE, "25");
                    } else {
                        userSession.setStatus(SessionStatus.OLD_FROM_DATABASE);
                    }
                }

                if (userSession.getProperty("ENTRY_TOKEN") == null && userSession.getUser().isCustomer()) {
                    /*
                     * It is a customer so load the custom properties.
                     */
                    CustomProperties cprops = new CustomProperties(userSession);
                    userSession = cprops.loadCustom();
                }

                Properties props = userSession.unloadToProperties();
                Enumeration<Object> en = props.keys();
                while (en.hasMoreElements()) {
                    String key = (String) en.nextElement();
                    response.setHeader(key, props.getProperty(key));
                }

                out.print("good");
            }

            //
            // Request is to update current session
            //
            else if (requestType.equals(REQUEST_NOAUTH)) {
                userSession = sessionBroker.getUserSession(new SessionID(sessionID, Integer.parseInt(sessionVersion)));
                if (userSession == null) {
                    // Create "empty" user and insert temporarily
                    IEVWebUser u = new EVWebUser();
                    u.setUsername(User.USERNAME_AUTH_FAIL);
                    userSession = sessionBroker.createSession(u, null);
                }
            }
            //
            // Request is to update current session
            //
            else if (requestType.equals(REQUEST_UPDATE)) {
                UserSession uSession = new UserSession();
                Enumeration<?> en = request.getHeaderNames();
                Properties nProps = new Properties();
                while (en.hasMoreElements()) {
                    String hkey = ((String) en.nextElement()).toUpperCase();
                    if (hkey.indexOf("SESSION") > -1) {
                        nProps.setProperty(hkey, request.getHeader(hkey));
                    }
                }

                uSession.loadFromProperties(nProps);
                sessionBroker.updateSession(uSession);
                out.println("Good");
            }

            //
            // Request is to "touch" current session - resets expiration time
            //
            else if (requestType.equals(REQUEST_TOUCH)) {
                sessionBroker.touch(sessionID);
            }

            //
            // Request is to logout current user
            //
            else if (requestType.equals(REQUEST_LOGOUT)) {
                sessionBroker.logout(sessionID);
            }

            //
            // Request is to retrieve current session using token
            //
            else if (requestType.equals(REQUEST_ACTIVE)) {
                String sid = sessionBroker.getActiveSession(entryToken);
                if (sid != null) {
                    response.setHeader("SESSIONID", sid);
                }
                out.print("good");
            }

            //
            // Request is to retrieve current session using IP address
            //
            else if (requestType.equals(REQUEST_VALIDATEIP)) {
                String customerID = uBroker.validateCustomerIP(prodname, ipAddress);
                response.setHeader("CUSTOMERID", customerID);
            }
        } catch (Exception e) {
            log("Error:", e);
        }
    }

}
