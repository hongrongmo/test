package org.ei.service;

import org.ei.config.EVProperties;
import org.ei.config.RuntimeProperties;


public abstract class ANEServiceConstants extends BaseServiceConstants {
	public static final String MAX_NUMBER_POOL_CONNECTION = "MAX_POOL_CSWS_PROXIES";
	public static final String CUSTOMER_SYSTEM_WEB_SERVICE_END_POINT = "CUSTOMER_SYSTEM_WEB_SERVICE_END_POINT";
	public static final String CUSTOMER_SERVICE_WSDL_PATH = "CUSTOMER_SERVICE_WSDL_PATH";
    public static final String WEBSERVICE_LOG_LEVEL="WEBSERVICE_LOG_LEVEL";
    public static final String WEBSERVICE_VERSION="WEBSERVICE_VERSION";
    public static final String SSO_CORE_REDIRECT_URL="SSO_CORE_REDIRECT_URL";
    public static final String DISABLE_SSO_AUTH="disable_sso_auth";

    public static String getDisableSSOAuth() {
        return  EVProperties.getRuntimeProperty(DISABLE_SSO_AUTH);
    }

    public static String getSSOCoreRedirectURL() {
        return  EVProperties.getRuntimeProperty(SSO_CORE_REDIRECT_URL);
    }

	public static String getMaxNumberPoolConnections() {
	    return EVProperties.getRuntimeProperty(MAX_NUMBER_POOL_CONNECTION);
	}

    public static String getCustomerSystemWebServiceEndPoint() {
        return getServicesBaseURL() + EVProperties.getRuntimeProperty(CUSTOMER_SYSTEM_WEB_SERVICE_END_POINT);
    }

    public static String getCustomerServiceWSDLPath() {
        return getServicesBaseURL() + EVProperties.getRuntimeProperty(CUSTOMER_SERVICE_WSDL_PATH);
    }

    public static String getWebserviceLogLevel() {
        return EVProperties.getRuntimeProperty(WEBSERVICE_LOG_LEVEL);
    }

    public static String getWebserviceVersion() {
        return EVProperties.getRuntimeProperty(WEBSERVICE_VERSION);
    }

}
