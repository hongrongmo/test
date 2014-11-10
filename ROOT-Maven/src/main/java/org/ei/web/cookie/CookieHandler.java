package org.ei.web.cookie;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import org.apache.log4j.Logger;
import org.ei.controller.Customer;
import org.ei.controller.Department;
import org.ei.controller.DepartmentConfigHandler;
import org.ei.security.utils.SecureID;
import org.ei.service.cars.CARSStringConstants;
import org.ei.session.UserSession;
import org.ei.util.GUID;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class CookieHandler {

    private static final Logger log4j = Logger.getLogger(CookieHandler.class);

    private static Map<String, Customer> departmentMap;

    /**
     * Handle request for the department map contents
     *
     * @param request
     * @param response
     * @param ses
     * @return
     * @throws Exception
     */
    public static Resolution handleRequest(HttpServletRequest request, HttpServletResponse response, UserSession ses) throws Exception {
        if (null == request || null == response) {
            throw new IllegalArgumentException("Request and/or response are not available!");
        }

        log4j.info("Handling cookies for response...");
        Map<String, Cookie> cookiemap = getCookieMap(request);

        /*
         * This block handles clientID cookies
         */
        if (!cookiemap.containsKey("CLIENTID")) {
            addClientIDCookie(response);
        }

        /*
         * This block handles secureID;
         */
        if (!cookiemap.containsKey("SECUREID")) {
            addSecureIDCookie(response);
        }

        Map<String, Customer> dmap = getDepartmentMap(request);
        if (dmap != null) {

            String sessionID = null;
            String customerID = ses.getUser().getCustomerID();
            if (null != ses.getSessionID()) {
                sessionID = ses.getSessionID().toString();
            }
            /*
             * This block handles department cookies.
             */
            String CID = request.getParameter("CID");
            if ((CID == null || CID.equals("home") || CID.equals("login")) && !cookiemap.containsKey("DEPARTMENT")
                && (dmap != null && dmap.containsKey(customerID)) && (request.getParameter("SYSTEM_DEPARTMENT") == null)) {
                log4j.warn("Return department form!");
                printDepartmentForm(request, response, sessionID, customerID, dmap);
                return new ForwardResolution("/WEB-INF/pages/customer/departmenttracking.jsp");
            } else if (request.getParameter("SYSTEM_DEPARTMENT") != null) {
                log4j.warn("Adding department cookie as indicated by request parameter!");
                addDepartmentCookie(request, response);
            } else if (request.getParameter("SYSTEM_CLEAR_DEP_COOKIE") != null) {
                log4j.warn("Clearing department cookie as indicated by request parameter!");
                clearDepartmentCookie(response);
            } else if (request.getParameter("SYSTEM_DEPARTMENT_UPDATE") != null) {
                log4j.warn("Resetting department cookie as indicated by request parameter!");
                resetDepartment();
            }
        }
        return null;
    }

    /**
     * This method is meant to be used to transfer legacy EV cookies from the "/controller/servlet" path to root path "/".
     *
     * @param request
     * @param response
     */
    public void handleLegacyCookies(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Cookie> cookiemap = getCookieMap(request);
        // This should only be called when path is '/controller/servlet/Controller'
        // and we need to try to transfer cookies if they are present
        if (cookiemap != null) {
            for (Cookie c : cookiemap.values()) {
                // Clone legacy cookie unless it's JSESSION
                if (!"JSESSIONID".equalsIgnoreCase(c.getName())) {
                    log4j.warn("Cloning cookie: " + c.getName());
                    Cookie refactorcookie = (Cookie) c.clone();
                    refactorcookie.setPath("/");
                    response.addCookie(refactorcookie);
                }
                // Expire legacy cookie
                c.setMaxAge(0);
                response.addCookie(c);
            }
        }
    }

    /**
     * Check for "DAYPASS" cookie
     *
     * @param request
     * @return
     */
    public static boolean isDayPass(HttpServletRequest request) {
        Map<String, Cookie> cookiemap = getCookieMap(request);
        if (cookiemap.containsKey("DAYPASS"))
            return true;
        return false;
    }

    /**
     * Get Session ID from cookie
     *
     * NOTE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Cookies for EV controller are scoped to /controller/servlet! Very important that all URLs start with this! This
     * method has been deprecated - please use EISessionCookie.getSessionID() instead.
     *
     * @param request
     * @return
     */
    @Deprecated
    public static String getSessionID(HttpServletRequest request) {
        Map<String, Cookie> cookiemap = getCookieMap(request);
        if (cookiemap.containsKey(EISessionCookie.EISESSION_COOKIE_NAME)) {
            Cookie sessioncookie = cookiemap.get(EISessionCookie.EISESSION_COOKIE_NAME);
            return sessioncookie.getValue();
        }
        return null;
    }

    /**
     * Get map of Cookie values for request
     *
     * @param request
     * @return
     */
    public static Map<String, Cookie> getCookieMap(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request object is not present!");
        }

        // Build Cookie map from incoming request
        Map<String, Cookie> map = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                Cookie cookie = cookies[i];
                map.put(cookie.getName(), cookie);
            }
        }

        return map;
    }

    /**
     * Return the ACW cookie value
     *
     * @param request
     * @return
     */
    public static Cookie getACWCookie(HttpServletRequest request) {
        Cookie acwcookie = getCookieMap(request).get(CARSStringConstants.ACW.value());
        if (acwcookie == null) return new Cookie(CARSStringConstants.ACW.value(), "");
        else return acwcookie;
    }

    /**
     * Get the EISESSION cookie
     *
     * @return
     */
    public static EISessionCookie getEISessionCookie(HttpServletRequest request) {
        return new EISessionCookie(getCookieMap(request).get(EISessionCookie.EISESSION_COOKIE_NAME));
    }

    /**
     * Parse the department map. Looked up as resource from WEB-INF directory.
     *
     * @param request
     * @return
     */
    public static Map<String, Customer> getDepartmentMap(HttpServletRequest request) {
        try {
            if (departmentMap == null) {
                InputStream departmentconfig = request.getServletContext().getResourceAsStream("/WEB-INF/department-config.xml");
                if (departmentconfig == null) {
                    Logger.getLogger(CookieHandler.class).warn("Unable to read department-config.xml!  Be sure it is present in the WEB-INF directory!");
                    return null;
                }
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                XMLReader parser;
                parser = saxParser.getXMLReader();
                DepartmentConfigHandler handler = new DepartmentConfigHandler();
                parser.setContentHandler(handler);
                parser.parse(new InputSource(departmentconfig));
                departmentMap = handler.getDepartmentMap();
            }
            return departmentMap;
        } catch (Exception e) {
            Logger.getLogger(CookieHandler.class).error("Unable to parse department-config.xml!", e);
            return null;
        }
    }

    /**
     * Reset the department - will cause the department-config.xml class to be read again!
     */
    private static synchronized void resetDepartment() {
        departmentMap = null;
    }

    /**
     * Print the department form page. This is returned only to customers with entries in department-config.xml!
     *
     * @param request
     * @param response
     * @param sessionID
     * @param customerID
     * @param dmap
     * @throws Exception
     */
    private static void printDepartmentForm(HttpServletRequest request, HttpServletResponse response, String sessionID, String customerID, Map<String, Customer> dmap)
        throws Exception {
        Customer cust = (Customer) dmap.get(customerID);
        Department[] department = cust.getDepartments();
        request.setAttribute("department", department);
        request.setAttribute("cust", cust);
        request.setAttribute("sessionid", sessionID);
    }

    /**
     * Add a DEPARTMENT cookie
     *
     * @param request
     * @param response
     */
    private static void addDepartmentCookie(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Adding department cookie.");
        String departmentID = request.getParameter("SYSTEM_DEPARTMENT");
        if (departmentID != null) {
            Cookie depCookie = new Cookie("DEPARTMENT", departmentID);
            depCookie.setMaxAge(63072000);
            depCookie.setPath("/");
            response.addCookie(depCookie);
        }
    }

    /**
     * Clears the DEPARTMENT cookie
     *
     * @param response
     * @throws Exception
     */
    private static void clearDepartmentCookie(HttpServletResponse response) throws Exception {
        Cookie depCookie = new Cookie("DEPARTMENT", "");
        depCookie.setMaxAge(0);
        depCookie.setPath("/");
        depCookie.setValue(null);
        response.addCookie(depCookie);
    }

    /**
     * Adds the SECUREID cookie
     *
     * @param response
     * @throws Exception
     */
    private static void addSecureIDCookie(HttpServletResponse response) throws Exception {
        Cookie secureIDCookie = new Cookie("SECUREID", SecureID.getSecureID(-1));
        secureIDCookie.setMaxAge(63072000);
        secureIDCookie.setPath("/");
        response.addCookie(secureIDCookie);
    }

    /**
     * Adds the CLIENTID cookie for usage
     *
     * @param response
     * @throws Exception
     */
    private static void addClientIDCookie(HttpServletResponse response) throws Exception {
        Cookie clientIDCookie = new Cookie("CLIENTID", new GUID().toString());
        clientIDCookie.setMaxAge(63072000);
        response.addCookie(clientIDCookie);
    }

    //
    //
    // Generic cookie methods
    //
    //

    public static Cookie getCookie(HttpServletRequest request, String name) {
        return getCookieMap(request).get(name);
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, Cookie cookie) {
        String contextRoot = request.getContextPath();
        if (contextRoot != null && contextRoot.length() > 0) {
            cookie.setPath(contextRoot);
        } else {
            cookie.setPath("/");
        }
        response.addCookie(cookie);
    }

    public static Cookie clearCookie(String cookiename) {
        Cookie cookie = new Cookie(cookiename, "");
        // Set Max Age to 0 to clear the cookie
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setValue("");
        return cookie;
    }

    public static Cookie clearCookie(Cookie cookie) {
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setValue("");
        return cookie;
    }

    public static Cookie clearACWCookie(String cookiename) {
        Cookie cookie = new Cookie(cookiename, "");
        // Set Max Age to 0 to clear the cookie
        cookie.setMaxAge(0);
        cookie.setDomain(".engineeringvillage.com");
        cookie.setPath("/");
        cookie.setValue("");
        return cookie;
    }

}