package org.ei.service;

import org.ei.config.EVProperties;

public class FastSearchServiceConstants extends BaseServiceConstants {
    public static final String MAX_NUMBER_POOL_CONNECTION = "MAX_POOL_FSWS_PROXIES";
	public static final String FAST_SEARCH_WEB_SERVICE_END_POINT = "FAST_SEARCH_WEB_SERVICE_END_POINT";
	public static final String FAST_SEARCH_WSDL_PATH = "FAST_SEARCH_WSDL_PATH";
	public static final String WEBSERVICE_LOG_LEVEL="FAST_WEBSERVICE_LOG_LEVEL";
	public static final String WEBSERVICE_VERSION="FAST_WEBSERVICE_VERSION";

    public static String getMaxNumberPoolConnections() {
        return EVProperties.getProperty(MAX_NUMBER_POOL_CONNECTION);
    }

    public static String getFastSearchWebServiceEndPoint() {
        return getServicesBaseURL() + EVProperties.getProperty(FAST_SEARCH_WEB_SERVICE_END_POINT);
    }

    public static String getFastSearchWSDLPath() {
        return getServicesBaseURL() + EVProperties.getProperty(FAST_SEARCH_WSDL_PATH);
    }

    public static String getWebserviceLogLevel() {
        return EVProperties.getProperty(WEBSERVICE_LOG_LEVEL);
    }

    public static String getWebserviceVersion() {
        return EVProperties.getProperty(WEBSERVICE_VERSION);
    }
}
