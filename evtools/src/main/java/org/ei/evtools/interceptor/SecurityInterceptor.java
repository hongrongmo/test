package org.ei.evtools.interceptor;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {
	
	private static Logger logger = Logger.getLogger(SecurityInterceptor.class);
	
	@Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
		if(!validateIP(request)){
			ModelAndView mv = new ModelAndView("403"); 
			ModelAndViewDefiningException mvde = new ModelAndViewDefiningException(mv); 
			throw mvde; 
		}
		return true;
    }
	
	public boolean validateIP(HttpServletRequest request) {
        // Fetch the requesting (client) IP address for this request
        String clientIP = request.getHeader("x-forwarded-for");
        if (clientIP == null)
            clientIP = request.getRemoteAddr();

        // Assume invalid until proven otherwise
        boolean validIP = false;

        // If IP is null, we're done. Otherwise attempt to authenitcate.
        if (StringUtils.isNotBlank(clientIP)) {
            // Put IP string into IP object and attempt to authenticate
            Inet4Address testIP = null;
            try {
                testIP = (Inet4Address) InetAddress.getByName(clientIP);
                clientIP = testIP.toString();

                byte[] octets = testIP.getAddress();

                // Allow addresses in the range 138.12.x.x (DEV)
                if ((octets[0] == (byte) 138) && (octets[1] == (byte) 12)) {
                    validIP = true;
                }
                // Allow addresses in the range 127.0.0.1 (LOCALHOST)
                else if ((octets[0] == (byte) 127) && (octets[1] == (byte) 0) && (octets[2] == (byte) 0) && (octets[3] == (byte) 1)) {
                    validIP = true;
                }
                // Allow addresses in the range 198.185.23.x (CERT)
                else if ((octets[0] == (byte) 198) && (octets[1] == (byte) 185) && (octets[2] == (byte) 23)) {
                    validIP = true;
                }
                // Allow addresses in the range 10.178.x.x (CERT)
                else if ((octets[0] == (byte) 10) && (octets[1] == (byte) 178)) {
                    validIP = true;
                }
                // Allow addresses in the range 198.185.25.x (PROD)
                else if ((octets[0] == (byte) 198) && (octets[1] == (byte) 185)) {
                    validIP = true;
                }
                // Allow VPN access
                else if ((octets[0] == (byte) 172) && (octets[1] == (byte) 29)) {
                    validIP = true;
                } else {
                	logger.warn("IP address '" + clientIP + "' not in the range 138.12.x.x, " + "198.185.23.x, 198.185.x.x, or 172.29.x.x");
                }
            } catch (UnknownHostException e) {
            	logger.warn("Caught exception while validating IP: " + clientIP, e);
                validIP = false;
            }
        }
        return validIP;
    }
}
