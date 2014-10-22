/**
 *
 */
package org.ei.web.cookie;

import javax.servlet.http.Cookie;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

/**
 * @author harovetm
 *
 */
public class SimulatedIPCookie extends Cookie {

    private static final Logger log4j = Logger.getLogger(SimulatedIPCookie.class);
    private static final String SIM_IP_SALT = "saltyipsaregreat";

    public static final String SIMULATED_IP_COOKIE_NAME = "SIMULATEDIP";

    /**
     * Override for super
     * @param name
     * @param value
     */
    public SimulatedIPCookie(String name, String value) {
        super(SIMULATED_IP_COOKIE_NAME, value);
        this.setMaxAge(-1);
    }

    /**
     * Build new SimulatedIPCookie from request cookie
     * @param simulatedipcookie
     */
    public SimulatedIPCookie(Cookie simulatedipcookie) {
        super(SIMULATED_IP_COOKIE_NAME, "");
        if (simulatedipcookie == null)
            return;
        if (simulatedipcookie.getValue() != null) this.setValue(simulatedipcookie.getValue());
        if (simulatedipcookie.getDomain() != null) this.setDomain(simulatedipcookie.getDomain());
        if (simulatedipcookie.getPath() != null) this.setPath(simulatedipcookie.getPath());
        if (simulatedipcookie.getComment() != null) this.setComment(simulatedipcookie.getComment());
        this.setSecure(simulatedipcookie.getSecure());
        this.setHttpOnly(simulatedipcookie.isHttpOnly());
        this.setMaxAge(-1);
    }

    /**
     * Build new SimulatedIPCookie from simulated IP value
     * @param ip
     */
    public SimulatedIPCookie(String ip) {
        super(SIMULATED_IP_COOKIE_NAME, "");
        this.setMaxAge(-1);
        this.setSimulatedIP(ip);
    }

    /**
     * Set simulated IP address
     * @param ip
     */
    public void setSimulatedIP(String ip) {
        if (GenericValidator.isBlankOrNull(ip)) return;

        // Hash IP value
        try {
            this.setValue(ip + "_" + DigestUtils.md5Hex(ip + SIM_IP_SALT));
        } catch (Exception e) {
            log4j.error("Unable to simulate IP: " + ip, e);
        }
    }

    /**
     * Check if the cookie value is a valid ip overrid generated from our system
     *
     * @param cookieValue
     * @return if it is return the ip
     */
    public String getSimulatedIP() {
        String value = this.getValue();
        if (GenericValidator.isBlankOrNull(value)) {
            return null;
        }
        try {
            String ip = value.substring(0, value.indexOf("_"));
            String md5 = value.substring(value.indexOf("_") + 1);
            String check = "";
            check = DigestUtils.md5Hex(ip + SIM_IP_SALT);
            if (check.equals(md5)) {
                return ip;
            }
        } catch (Exception e) {
            log4j.error("Unable to hash cookie value!", e);
        }

        return null;
    }

}
