package org.ei.service.cars;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum class to hold all request parameters which are 
 * required to make REST call
 * 
 * @author naikn1
 * @version 1.0
 *
 */
public enum RESTRequestParameters {
	/**
	 * Constant to hold the request param for auth type
	 */
	AUTH_TYPE("auth_type"), 
	/**
	 * Constant to hold the request param for auth toke
	 */
	AUTH_TOKEN("auth_token"), 
	/**
	 * Constant to hold the request param for sso key
	 */
	SSO_KEY("sso_key"), 
	/**
	 * Constant to hold the request param for remember key 
	 */
	REMEMBER_KEY("remember_key"), 
	/**
	 * Constant to hold the request param for username
	 */
	USER_NAME("username"), 
	/**
	 * Constant to hold the request param for password
	 */
	PASSWORD("password"), 
	/**
	 * Constant to hold the request param for remember flag
	 */
	REMEMBER_FLAG("remember_flag"),
	/**
	 * Constant to hold the request param for remember flag path choice
	 */
	REMEMBER_PATH_CHOICE("remember_flag_pathchoice"),
	/**
	 * Constant to hold the request param for cars cookie
	 */
	CARS_COOKIE("cars_cookie"),
	/**
	 * Constant to hold the request param for sso disabled flag
	 */
	SSO_DISABLED("sso_disabled"),
	/**
	 * Constant to hold the request param for path choice
	 */
	PATH_CHOICE("path_choice"),
    /**
     * Constant to hold the request param for platform code + site combo (terminate action)
     */
    PLATFORM_SITE("platSite"),
    /**
     * Constant to hold the request param for platform code
     */
    PLATFORM_CODE("plat_code"),
	/**
	 * Constant to hold the request param for site identifier
	 */
	SITE_ID("site_id"),
	/**
	 * Constant to hold the request param for client ip address
	 */
	CLIENT_IP("client_ip"), 
	/**
	 * Constant to hold the request param for browser type
	 */
	BROWSER_TYPE("client_browser"), 
	/**
	 * Constant to hold the request param for machine address
	 */
	MACHINE_ADDRESS("client_machine"), 
	/**
	 * Constant to hold the request param for profile id
	 */
	PROFILE_ID("prof_id"), 
	/**
	 * Constant to hold the request param for domain name
	 */
	DOMAIN_NAME("domain_name"),
	/**
	 * Constant to hold the request param for anon type
	 */
	ANON_TYPE("anon_type"),
	/**
	 * Constant to hold the request param for association type
	 */
	ASSOCIATION_TYPE("assoc_type"),
	/**
	 * Constant to hold the request param for athens fence
	 */
	ATHENS_FENCE("athens_fence"), 
	/**
	 * Constant to hold the request param for shibboleth fence
	 */
	SHIBBOLETH_FENCE("shibboleth_fence"), 
	/**
	 * Constant to hold the request param for credential type
	 */
	CRED_TYPE("cred_type"), 
	/**
	 * Constant to hold the request param for path choice exists flag
	 */
	PATH_CHOICE_EXISTS("path_choice_exists"), 
	/**
	 * Constant to hold the request param for account name
	 */
	ACCOUNT_NAME("acct_name"), 
	/**
	 * Constant to hold the request param for department name
	 */
	DEPARTMENT_NAME("dept_name"), 
	/**
	 * Constant to hold the request param for registration type
	 */
	REGISTRATION_TYPE("reg_type"), 
	/**
	 * Constant to hold the request param for remember path flag
	 */
	REMEMEBER_PATH_FLAG("remember_path_flag"), 
    /**
     * LOGIN_FULL cancel link
     */
    LOGIN_FULL_CANCEL_URI("login_full_cancel_uri"),
	/**
	 * Constant to hold the request param for requestParams
	 */
	REQUEST_PARAMS("requestParams"), 
	// CARS4
	/**
	 * Constant to hold the request param for anonymous shibboleth flag
	 */
	ANON_SHIBBOLETH("SHIBBOLETH"),
	/**
	 * Constant to hold the request param for dynamic url
	 */
	DYNAMIC_REQUEST_URI("dynamicURI"),
	/**
	 * Constant to hold the request param for origin
	 */
	ORIGIN("origin"),
	/**
	 * Constant to hold the request param for zone
	 */
	ZONE("zone");
    
	/**
	 * The static map, to store the entries;
	 */
	private static Map<String, RESTRequestParameters> localEntries = 
		new HashMap<String, RESTRequestParameters>(10, 0.05f);

	static {
		for (RESTRequestParameters entry : RESTRequestParameters.values()) {
			localEntries.put(entry.getReqParam(), entry);
		}
	}

	/** This field specifies the value of m_reqParam. */
	private final String m_reqParam;

	/**
	 * Constructor.
	 * 
	 * @param str - the request param value
	 */
	private RESTRequestParameters(String str) {
		this.m_reqParam = str;
	}

	/**
	 * This method gets the value of m_reqParam.
	 * 
	 * @return String - the request param value
	 */
	public String getReqParam() {
		return m_reqParam;
	}

	/**
	 * This method is to judge if any enum-type matches with the input-param
	 * 
	 * @param param the request param
	 * @return boolean matching enum-type
	 */
	public static boolean isEnumObject(String param) {
		return localEntries.get(param) != null ? true : false;
	}
}

/*****************************************************************************

                               ELSEVIER
                             CONFIDENTIAL

   This document is the property of Elsevier, and its contents are
   proprietary to Elsevier.   Reproduction in any form by anyone of the
   materials contained  herein  without  the  permission  of Elsevier is
   prohibited.  Finders are  asked  to  return  this  document  to the
   following Elsevier location.

       Elsevier
       360 Park Avenue South,
       New York, NY 10010-1710

   Copyright (c) 2013 by Elsevier, A member of the Reed Elsevier plc
   group.

   All Rights Reserved.

*****************************************************************************/