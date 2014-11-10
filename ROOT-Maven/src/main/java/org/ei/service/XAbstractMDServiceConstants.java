package org.ei.service;

import org.ei.config.EVProperties;

public class XAbstractMDServiceConstants extends BaseServiceConstants {
	public static final String MAX_NUMBER_POOL_CONNECTION = "MAX_POOL_XABSMDWS_PROXIES";
	public static final String XABS_MD_WEB_SERVICE_END_POINT = "XABS_MD_WEB_SERVICE_END_POINT";
	public static final String XABS_MD_WSDL_PATH = "XABS_MD_WSDL_PATH";
	public static final String WEBSERVICE_LOG_LEVEL="XABS_MD_WEBSERVICE_LOG_LEVEL";
	public static final String WEBSERVICE_VERSION="XABS_MD_WEBSERVICE_VERSION";

    public static String getMaxNumberPoolConnections() {
        return EVProperties.getProperty(MAX_NUMBER_POOL_CONNECTION);
    }

    public static String getXAbstractMDWebServiceEndPoint() {
        return getServicesBaseURL() + EVProperties.getProperty(XABS_MD_WEB_SERVICE_END_POINT);
    }

    public static String getXAbstractMDWSDLPath() {
        return getServicesBaseURL() + EVProperties.getProperty(XABS_MD_WSDL_PATH);
    }

    public static String getWebserviceLogLevel() {
        return EVProperties.getProperty(WEBSERVICE_LOG_LEVEL);
    }

    public static String getWebserviceVersion() {
        return EVProperties.getProperty(WEBSERVICE_VERSION);
    }
}
