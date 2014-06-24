package org.ei.stripes.util;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.validator.GenericValidator;
import org.ei.controller.CookieHandler;
import org.ei.stripes.action.ApplicationStatus;

public class HttpRequestUtil {
    private static org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger(HttpRequestUtil.class);

    /**
     * This method returns the current IP for the user.
     *
     * @param request
     * @return
     */
    public static String getIP(HttpServletRequest request) {
        // First get IP from request
        String ip = request.getHeader("x-forwarded-for");
        if (GenericValidator.isBlankOrNull(ip)){
            ip = request.getRemoteAddr();
            log4j.info("Retrieved IP from request.getRemoteaddr(): " + ip);
        } else {
            log4j.info("Determining correct IP from x-forwarded-for header: '" + ip +"'");
        	// TMH Fix for AWS - it can return a list of IPs in this header
        	// where the first one is the client's real IP and the following ones
        	// are proxy servers and ELB IPs.
        	String[] iparr = ip.split(",");
    	    // Parse list from RIGHT-TO-LEFT!  Eliminate private IPs
            ArrayUtils.reverse(iparr);
        	boolean found = false;
    	    for (String temp : iparr) {
    	        temp = temp.trim();
    	        if (isValidIP(temp) && !isPrivateIP(temp)) {
    	            ip = temp;
    	            found = true;
    	            break;
    	        }
    	    }
    	    if (!found) {
    	        log4j.error("No valid ip address could be found from x-forwarded-for: '" + ip + "'");
                ip = request.getRemoteAddr();
    	    } else {
    	        log4j.info("Retrieved IP from x-forwarded-for: " + ip);
    	    }
        }

        // Next see if there's a special override available.  See the
        // org.ei.stripes.action.ApplicationStatus action bean for more details
        // but basically this allows us to simulate different IPs for testing
        // purposes
        Map<String, Cookie> cookiemap = CookieHandler.getCookieMap(request);
        if (cookiemap != null && !cookiemap.isEmpty() && cookiemap.containsKey("SIMULATEDIP")) {
            String simulatedip = cookiemap.get("SIMULATEDIP").getValue();
            if (!GenericValidator.isBlankOrNull(simulatedip)) {
            	simulatedip = ApplicationStatus.isValidSimIp(simulatedip);
            	if(simulatedip != null){
            	    log4j.info("Returning simulated ip: " + simulatedip);
            		return simulatedip;
            	}
            }
        }

        return ip;
    }

    private static final String validip = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static Pattern ippattern =Pattern.compile(validip);
    private static Matcher ipmatcher;

    /**
     * Utility method to determine if IP is valid
     * @param ipaddress
     * @return
     */
    public static boolean isValidIP(String ipaddress) {
        ipaddress = ipaddress.trim();
        if (GenericValidator.isBlankOrNull(ipaddress)) {
            return false;
        }
        ipmatcher = ippattern.matcher(ipaddress);
        if (!ipmatcher.matches()) {
            log4j.error("Invalid IP format: '" + ipaddress + "'!");
            return false;
        }
        return true;
    }

    /**
     * Utility method to determine if IP is private
     * @param ipaddress
     * @return
     */
    public static boolean isPrivateIP(String ipaddress) {
        ipaddress = ipaddress.trim();
        if (!isValidIP(ipaddress)) {
            return false;
        }
        try {
            InetAddress inetaddress = InetAddress.getByName(ipaddress);
            return inetaddress.isSiteLocalAddress();
        } catch (Exception e) {
            log4j.error("Unable to convert '" + ipaddress + "' to InetAddress!", e);
            return false;
        }
    }

    /**
     * Get the domain (with port) from the current request
     *
     * @param request
     * @return
     */
    public static String getDomain(HttpServletRequest request) {
    	if (request == null) {
    		log4j.warn("Request is empty!");
    		return null;
    	}

    	String domain = "";
    	try {
			URI uri = new URI(request.getRequestURL().toString());
	    	domain = uri.getHost();
		} catch (URISyntaxException e) {
			log4j.error("Unable to parse request URL");
		}
    	return domain;
    }

    /**
     * Normalizes the specified URL.
     *
     * @param url
     *            an input URL.
     * @return the normalized URL.
     * @see #normalizeUrl(java.net.URL)
     */
    public static URL normalizeUrl(String url) {
        try {
            return normalizeUrl(new URL(url));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }

    /**
     * Normalizes the specified URL by:
     * <ol>
     * <li>Converting the host name and protocol name to lower-case.</li>
     * <li>Dropping the port number in case it is a default port number for the
     * specified protocol.</li>
     * <li>If the input URL's file path is set to &quot;/&quot;, then it is
     * removed.</li>
     * <li>The input URL's ref path is disregarded.</li>
     * <li>The input URL's user info path is disregarded.</li>
     * </ol>
     *
     * @param url
     *            an input URL.
     * @return the normalized URL.
     */
    public static URL normalizeUrl(URL url) {
        String protocol = url.getProtocol().toLowerCase();
        String host = url.getHost().toLowerCase();

        // if a standard port is used, set port to -1
        int port = url.getPort();
        if ((port == 80 && "http"
                .equals(protocol))
                || (port == 443 && "https"
                        .equals(protocol)))
            port = -1;

        String file = url.getFile();
        if ("/".equals(file))
            file = "";

        try {
            return new URL(protocol, host, port, file);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error normalizing URL: "
                    + url, e);
        }
    }

    /**
     * Pretty print an HTTP request
     *
     * @param request
     * @return
     */
    public static String prettyPrintRequest(HttpServletRequest request, String newline) {

    	// Add request URI with parameters
    	StringBuffer sb = new StringBuffer("    +    URI:        " + request.getRequestURI());
    	if (request.getParameterNames() != null) {
            Enumeration<String> en = request.getParameterNames();

            String key;
            String[] value;

            if (en.hasMoreElements()) {
            	sb.append("?");
            }
            while (en.hasMoreElements()) {
                key = (String) en.nextElement();
                value = request.getParameterValues(key);
                sb.append(key + "=");
                for (int t = 0; t < value.length; t++) {
                	if (t > 0) sb.append(",");
                    sb.append(value[t]);
                }
                if (en.hasMoreElements()) sb.append("&");
            }

    	}
    	sb.append(newline);

    	// Add IP, Session ID and User-Agent
    	sb.append("    +    IP:         " + getIP(request) + newline);
    	HttpSession session = request.getSession(false);
    	if (session != null) {
    		sb.append("    +    SessionID:  " + session.getId() + " (" + session.isNew() + ")" + newline);
    	}
    	sb.append("    +    User-Agent: " + request.getHeader("User-Agent") + newline);

    	return sb.toString();
    }

    /**
     * Return the server name including port (if not 80)
     *
     * @param request
     * @return
     */
    public static String getServerName(HttpServletRequest request) {
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        if (serverPort != 80) {
            serverName = serverName + ":" + Integer.toString(serverPort);
        }
        return serverName;
    }

}
