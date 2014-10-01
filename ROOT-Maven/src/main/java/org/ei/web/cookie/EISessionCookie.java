/**
 *
 */
package org.ei.web.cookie;

import java.rmi.server.UID;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.session.AWSInfo;
import org.ei.session.SessionID;
import org.ei.util.GUID;

/**
 * @author HaroveTM
 *
 */
public class EISessionCookie extends Cookie {
    private static final Logger log4j = Logger.getLogger(EISessionCookie.class);
    private static final long serialVersionUID = -8039633887817148201L;

    public static final String EISESSION_COOKIE_NAME = "EISESSION";
    private static final String EISESSION_SECRET = "h$a5$jmp4BKluup1V7Sw^HSo1pwH62pe";
    private static final long EXPIRES_IN = 30 * 60 * 1000;  // 30 minute expiration KEEP IN SYNC WITH JSESSION EXPIRATION!!
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");

    private int version;
    private String sessionid = "";
    private Date timestamp = new Date();

    public EISessionCookie(HttpServletRequest request) {
        super(EISESSION_COOKIE_NAME, "");
        this.setPath("/");
        this.setMaxAge(-1);
        Map<String, Cookie> cookiemap = CookieHandler.getCookieMap(request);
        if (cookiemap.containsKey(EISESSION_COOKIE_NAME)) {
            this.processCookie(cookiemap.get(EISESSION_COOKIE_NAME));
        }
        if (isExpired()) reset();
    }

    public EISessionCookie(Cookie eisessioncookie) {
        super(EISESSION_COOKIE_NAME, "");
        this.setPath("/");
        this.setMaxAge(-1);
        if (eisessioncookie != null) {
            this.processCookie(eisessioncookie);
        }
        if (isExpired()) reset();
    }

    public EISessionCookie(SessionID osessionid) {
        super(EISESSION_COOKIE_NAME, "");
        this.setPath("/");
        this.setMaxAge(-1);
        this.version = osessionid.getVersionNumber();
        this.sessionid = osessionid.getID();
    }

    public String getSessionIdWithVersion() {
        return Integer.toString(this.version) + "_" + this.sessionid;
    }

    public String getSessionid() {
        return this.sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getTimestamp() {
        return this.sessionid;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - this.timestamp.getTime() > EXPIRES_IN;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String getValue() {
        String tohash = Integer.toString(this.version) + "_" + this.sessionid + "_" + formatter.format(this.timestamp);
        String hashed = DigestUtils.md5Hex(tohash + EISESSION_SECRET);
        return tohash + "_" + hashed;
    }

    /**
     * Method to build new session IDs
     * @return
     */
    public static String buildNewSessionID() {
        return GUID.normalize(new UID().toString()) + ":" + new AWSInfo().getEc2Id();
    }

    private void processCookie(Cookie eisessioncookie) {
        if (eisessioncookie == null) {
            this.setValue("");
            reset();
            return;
        } else {
            String value = eisessioncookie.getValue();
            if (GenericValidator.isBlankOrNull(value)) {
                log4j.error("Cannot process empty value!");
                reset();
                return;
            }

            try {
                this.setValue(value);

                // Split on "_"
                // Old format will just be <version>_<ID>
                // New version will be <version>_<ID>_<timestamp>_<hash>
                String check = "";
                String splitter[] = value.split("_");
                if (splitter.length > 0)
                    this.version = Integer.parseInt(splitter[0]);
                if (splitter.length > 1)
                    this.sessionid = splitter[1];
                if (splitter.length > 2) {
                    this.timestamp = formatter.parse(splitter[2]);
                    if (splitter.length > 3) {
                        check = splitter[3];
                    }
                    if (!check.equals(DigestUtils.md5Hex(splitter[0] + "_" + splitter[1] + "_" + splitter[2] + EISESSION_SECRET))) {
                        log4j.error("Hash check has failed for cookie value!");
                        reset();
                    }

                }
            } catch (Throwable t) {
                log4j.error("Unable to process EISESSION cookie!", t);
                reset();
            }
        }
    }

    private void reset() {
        // Build new session ID
        this.setValue("");
        this.setSessionid(buildNewSessionID());
        this.setTimestamp(new Date());
        this.setVersion(0);
        this.setPath("/");
        // A negative value means that the cookie is not stored persistently
        // and will be deleted when the Web browser exits. A zero value
        // causes the cookie to be deleted.
        // http://java.sun.com/j2ee/sdk_1.2.1/techdocs/api/javax/servlet/http/Cookie.html#setMaxAge(int)
        this.setMaxAge(-1);
    }
}
