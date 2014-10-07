/**
 *
 */
package org.ei.stripes.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.stripes.util.HttpRequestUtil;

/**
 * @author harovetm
 *
 */
@UrlBinding("/back/{$event}.url")
public class BackUrlAction extends EVActionBean {
    private static final Logger log4j = Logger.getLogger(BackUrlAction.class);

    public static final String SESSION_FEATURE_EXTENSION = ".backurl";

    @Validate(mask = "SAVETOFOLDER|SELECTEDRECORDS|SAVEDSEARCH|SEARCHHISTORY")
    private String feature;

    public enum BackURLByFeature {
        SAVETOFOLDER, SELECTEDRECORDS, SAVEDSEARCH, SEARCHHISTORY;
        public static BackURLByFeature value(String value) {
            try {
                return BackURLByFeature.valueOf(value);
            } catch (Throwable t) {
                return null;
            }
        }
    }

    /**
     * Displays the save Folders page -
     *
     * @return Resolution
     */
    @HandlesEvent("service")
    @DefaultHandler
    @Validate
    public Resolution service() {
        return new RedirectResolution(restore(context.getRequest()));
    }

    /**
     * Attempt to restore back URL from session
     *
     * @param request
     * @return
     */
    private String restore(HttpServletRequest request) {
        // Get the session but do NOT create one if it doesn't exist (shouldn't happen)
        HttpSession session = request.getSession(false);
        if (session == null) {
            log4j.warn("No session object!");
            return EVPathUrl.EV_HOME.value();
        }

        // See if the feature parm is available on request
        if (GenericValidator.isBlankOrNull(this.feature)) {
            log4j.warn("No feature value set!");
            return EVPathUrl.EV_HOME.value();
        }
        BackURLByFeature featureenum = BackURLByFeature.valueOf(this.feature);
        if (feature == null) {
            log4j.warn("Feature not indicated or is invalid for backurl: " + this.feature + "!");
            return EVPathUrl.EV_HOME.value();
        }

        // Attempt to retrieve back URL from session
        String backurl = (String) session.getAttribute(featureenum.name() + SESSION_FEATURE_EXTENSION);
        if (GenericValidator.isBlankOrNull(backurl)) {
            log4j.warn("No backurl value found in session!");
            return EVPathUrl.EV_HOME.value();
        }
        session.removeAttribute(backurl);
        return backurl;

    }

    /**
     * Scan ActionBean for valid backurl. This should be called AFTER binding and validation!
     *
     * @param request
     */
    public static void scan(HttpServletRequest request) {
        String backurlfromrequest = request.getParameter("backurl");
        if (GenericValidator.isBlankOrNull(backurlfromrequest)) {
            log4j.info("No backurl value found in request...");
            return;
        }

        BackURLByFeature feature = BackURLByFeature.value(backurlfromrequest);
        if (feature == null) {
            log4j.info("Feature not indicated or is invalid for backurl: " + backurlfromrequest + "!");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            log4j.warn("No session object!");
            return;
        }

        String referer = HttpRequestUtil.getReferer(request);
        switch (feature) {
        case SELECTEDRECORDS:
        case SAVEDSEARCH:
        case SAVETOFOLDER:
        case SEARCHHISTORY:
            if (GenericValidator.isBlankOrNull(referer)) {
                log4j.info("No referer available, NOT storing!");
                return;
            }
            String stored = (String) session.getAttribute(feature.name() + BackUrlAction.SESSION_FEATURE_EXTENSION);
            if (!GenericValidator.isBlankOrNull(stored)) {
                if (referer.contains("/customer")) {
                    // Do not store /customer/* URLs - most likely an authentication flow...
                    log4j.warn("Referer is a 'customer' URL, do NOT save!");
                    return;
                } else if (stored.trim().equals(referer.trim())) {
                    // Compare stored to current. If the paths match, do NOT store. Probably just a refresh.
                    log4j.warn("Referer path is same as stored path for " + feature.name() + ", NOT storing!");
                    return;
                }
            }
            session.setAttribute(feature.name() + BackUrlAction.SESSION_FEATURE_EXTENSION, referer);
            break;
        default:
            break;

        }
    }

    /**
     * Retrieve "back" URL from session by feature
     *
     * @param request
     * @param feature
     * @return
     */
    public static String getStoredURLByFeature(HttpServletRequest request, BackURLByFeature feature) {
        return getStoredURLByFeature(request, feature, false);
    }

    /**
     * Retrieve "back" URL from session by feature
     *
     * @param request
     * @param feature
     * @return
     */
    public static String getStoredURLByFeature(HttpServletRequest request, BackURLByFeature feature, boolean remove) {
        // Get the session but do NOT create one if it doesn't exist (shouldn't happen)
        HttpSession session = request.getSession(false);
        if (session == null) {
            log4j.warn("No session object!");
            return EVPathUrl.EV_HOME.value();
        }

        // See if the feature parm is available on request
        if (feature == null || GenericValidator.isBlankOrNull(feature.name())) {
            log4j.warn("No feature value set!");
            return EVPathUrl.EV_HOME.value();
        }

        // Attempt to retrieve back URL from session
        String backurl = (String) session.getAttribute(feature.name() + SESSION_FEATURE_EXTENSION);
        if (GenericValidator.isBlankOrNull(backurl)) {
            log4j.warn("No backurl value found in session!");
            return EVPathUrl.EV_HOME.value();
        }

        if (remove) {
            session.removeAttribute(feature.name() + SESSION_FEATURE_EXTENSION);
        }
        return backurl;
    }

    /**
     * Remove "back" URL from session by feature
     *
     * @param request
     * @param feature
     */
    public static void removeStoredURLByFeature(HttpServletRequest request, BackURLByFeature feature) {
        // Get the session but do NOT create one if it doesn't exist (shouldn't happen)
        HttpSession session = request.getSession(false);
        if (session == null) {
            log4j.warn("No session object!");
            return;
        }

        // See if the feature parm is available on request
        if (feature == null || GenericValidator.isBlankOrNull(feature.name())) {
            log4j.warn("No feature value set!");
            return;
        }

        session.removeAttribute(feature.name() + SESSION_FEATURE_EXTENSION);
    }

    /**
     * @return the feature
     */
    public String getFeature() {
        return feature;
    }

    /**
     * @param feature
     *            the feature to set
     */
    public void setFeature(String feature) {
        this.feature = feature;
    }

}
