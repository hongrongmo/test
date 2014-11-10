/**
 *
 */
package org.ei.service;

import org.ei.config.EVProperties;

/**
 * @author harovetm
 *
 */
public abstract class BaseServiceConstants {
    public static final String CLONE_HOST_NAME = "EV_USER";
    public static final String NOT_AVAILABLE = "N/A";

    public static final String CSAS_BASE_URL = "CSAS_BASE_URL";
    public static final String CONSUMER_APP="CONSUMER_APP";
    public static final String CONSUMER_CLIENT="CONSUMER_CLIENT";
    public static final String WEBSERVICE_LOG_LEVEL="WEBSERVICE_LOG_LEVEL";
    public static final String WEBSERVICE_VERSION="WEBSERVICE_VERSION";
    public static final String PLATFORM_CODE="PLATFORM_CODE";
    public static final String SITE_IDENTIFIER="SITE_IDENTIFIER";

    public static String getServicesBaseURL() {
        return EVProperties.getProperty(CSAS_BASE_URL);
    }

    public static String getConsumerApp() {
        return EVProperties.getProperty(ANEServiceConstants.CONSUMER_APP);
    }

    public static String getConsumerClient() {
        return EVProperties.getProperty(ANEServiceConstants.CONSUMER_CLIENT);
    }

    public static String getWebserviceLogLevel() {
        return EVProperties.getProperty(ANEServiceConstants.WEBSERVICE_LOG_LEVEL);
    }

    public static String getWebserviceVersion() {
        return EVProperties.getProperty(ANEServiceConstants.WEBSERVICE_VERSION);
    }

    public static String getPlatformCode() {
        return EVProperties.getProperty(ANEServiceConstants.PLATFORM_CODE);
    }

    public static String getSiteIdentifier() {
        return EVProperties.getProperty(ANEServiceConstants.SITE_IDENTIFIER);
    }

}
