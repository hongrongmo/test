package org.ei.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.config.ApplicationProperties;
import org.ei.session.BlockedIPEvent;
import org.ei.stripes.util.HttpRequestUtil;

public class RateLimiter {
    private final static Logger log4j = Logger.getLogger(RateLimiter.class);

    public static final String SESSION_RATE_LIMITOR_KEY = RateLimiter.class.getName() + ".ratelimitor";

    private boolean enabled = false;	// Default
    private double maxrate = 2.00;		// Default
    private int minrequests = 300;		// Default
    private long reset = 300; // Default
    private String emailto = "harover@elsevier.com";
    private String sender = "eicustomersupport@elsevier.com";

    private static RateLimiter instance;

    /**
     * Singleton instance
     *
     * @return
     * @throws IOException
     */
    public static synchronized RateLimiter getInstance() throws IOException {
        if (instance == null) {
            instance = new RateLimiter();
        }
        return instance;
    }

    /**
     * Constructor
     *
     * @throws IOException
     */
    private RateLimiter() {
        ApplicationProperties runtimeproperties = EVProperties.getApplicationProperties();

        try {
            this.enabled = Boolean.parseBoolean((String) runtimeproperties.getProperty(EVProperties.RATELIMITER_ENABLED));
            log4j.info("RateLimiter enabled = " + this.enabled);
        } catch (Exception e) {
            log4j.error("Unable to read '" + EVProperties.RATELIMITER_ENABLED + "' from runtime properties!");
        }

        try {
            this.maxrate = Double.parseDouble((String) runtimeproperties.getProperty(EVProperties.RATELIMITER_MAX_RATE));
            log4j.info("RateLimiter maxrate = " + this.maxrate);
        } catch (Exception e) {
            log4j.error("Unable to read '" + EVProperties.RATELIMITER_MAX_RATE + "' from runtime properties!");
        }

        try {
            this.minrequests = Integer.parseInt((String) runtimeproperties.getProperty(EVProperties.RATELIMITER_MIN_REQUESTS));
            log4j.info("RateLimiter minrequests = " + this.minrequests);
        } catch (Exception e) {
            log4j.error("Unable to read '" + EVProperties.RATELIMITER_MIN_REQUESTS + "' from runtime properties!");
        }

        try {
            this.reset = Integer.parseInt((String) runtimeproperties.getProperty(EVProperties.RATELIMITER_RESET));
            log4j.info("RateLimiter reset = " + this.reset);
        } catch (Exception e) {
            log4j.error("Unable to read '" + EVProperties.RATELIMITER_RESET + "' from runtime properties!");
        }

        try {
            this.emailto = runtimeproperties.getProperty(EVProperties.RATELIMITER_EMAIL_TO);
            if (GenericValidator.isBlankOrNull(this.emailto)) {
                this.emailto = "harover@elsevier.com";
            }
            log4j.info("RateLimiter emailto = " + this.emailto);
        } catch (Exception e) {
            log4j.error("Unable to read '" + EVProperties.RATELIMITER_EMAIL_TO + "' from runtime properties!");
        }

        try {
            this.sender = runtimeproperties.getProperty(ApplicationProperties.TO_RECEPIENTS);
            log4j.info("RateLimiter sender = " + this.sender);
        } catch (Exception e) {
            log4j.error("Unable to read '" + ApplicationProperties.TO_RECEPIENTS + "' from runtime properties!");
        }
    }

    /**
     * Determine if user is blocked. Uses combination of static list from the "blocked_ip.txt" file list and rate check to determine block status.
     *
     * @param request
     * @return
     */
    public boolean block(HttpServletRequest request) {

        // Allow openXML and openRSS requests through without check
        String CID = request.getParameter("CID");
        if (!GenericValidator.isBlankOrNull(CID)) {
            if (CID.contains("openXML") || CID.contains("openRSS")) {
                return false;
            }
        }

        // Get RateLimiter from session. Do NOT create session if not present! This must be
        // done in the SessionBuilderInterceptor!
        HttpSession session = request.getSession(false);
        if (session == null)
            return false;

        // Initialize if not present
        SessionRate sessionrate = (SessionRate) session.getAttribute(SESSION_RATE_LIMITOR_KEY);
        if (sessionrate == null) {
            sessionrate = new SessionRate();
            session.setAttribute(SESSION_RATE_LIMITOR_KEY, sessionrate);
        } else {
            sessionrate.incrementRaterequests();
        }
        // Bump total requests
        sessionrate.incrementTotalrequests();

        // Requests not at minimum?
        int raterequests = sessionrate.getRaterequests();
        if (raterequests <= this.minrequests)
            return false;

        synchronized (session) {
            // Already blocked?
            if (sessionrate.isSessionblock())
                return true;

            // Calculate rate...
            long seconds = (new Date().getTime() - sessionrate.getFirstrequest()) / 1000;
            double rate = ((double) raterequests) / ((double) seconds);
            log4j.info("Checking rate of '" + rate + "' against RateLimiter max of '" + maxrate + "'");
            if (rate > this.maxrate) {
                // Create BlockedIPEvent
                String ip = HttpRequestUtil.getIP(request);
                String message = "Customer SESSION has been blocked due to high activity.  IP: " + ip + ", requests: " + raterequests + ", rate: " + rate;
                BlockedIPEvent ipevent = new BlockedIPEvent(ip);
                ipevent.setEvent(BlockedIPEvent.EVENT_REQUESTRATE_LIMIT);
                ipevent.setBucket(ip + "_" + session.getId() + "_REQUESTRATE");
                ipevent.setMessage(message);

                IPBlocker.notifyEmail(ipevent, IPBlocker.insertBlockedIPsTable(ipevent));

                // Save block for next request
                sessionrate.setSessionblock(true);
                session.setAttribute(SESSION_RATE_LIMITOR_KEY, sessionrate);
                log4j.warn(message);
                return true;
            }

            // Every 5 minutes, reset the RateLimitor. This way if some customer starts slow but then
            // ramps up requests we don't have to wait for RateLimitor to catch up
            if (seconds > reset) {
                sessionrate.reset();
            }

        }

        return false;
    }

    /**
     * Inner class to track request rate.
     *
     * @author harovetm
     *
     */
    public static class SessionRate {
        private int totalrequests;
        private int raterequests = 1;
        private long firstrequest = new Date().getTime();
        private boolean sessionblock = false;

        public void reset() {
            firstrequest = new Date().getTime();
            raterequests = 1;
            sessionblock = false;
        }

        public long getFirstrequest() {
            return firstrequest;
        }

        public boolean isSessionblock() {
            return sessionblock;
        }

        public void setSessionblock(boolean sessionblock) {
            this.sessionblock = sessionblock;
        }

        public int getRaterequests() {
            return raterequests;
        }

        public int incrementRaterequests() {
            return ++raterequests;
        }

        public void setRaterequests(int raterequests) {
            this.raterequests = raterequests;
        }

        public int getTotalrequests() {
            return totalrequests;
        }

        public int incrementTotalrequests() {
            return ++totalrequests;
        }

        public void setTotalrequests(int totalrequests) {
            this.totalrequests = totalrequests;
        }
    }
}