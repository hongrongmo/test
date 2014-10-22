package org.ei.service.cars;

import org.apache.commons.lang.StringUtils;
import org.ei.config.EVProperties;

/**
 * Enum class to hold the constant names which will be 
 * retrieved from configuration file 
 * (CARS related config varibles defined in web. 
 * The name here should be same as in config file)
 * 
 * @author naikn1
 * @version 1.0
 * 
 */
public enum CARSConfigVariables {
	/**
	 * Constant to get the CARS end point url which will be used for REST call
	 */
	CARS_END_POINT,
	/**
	 * Constant name to retrieve service timeout period (in ms) for REST call
	 */
	CARS_SERVICE_TIMEOUT,
	/**
	 * Constant name to get the header name which will be set to rest client
	 */
	X_ELS_AUTHENTICATION,
	/**
	 * Constant name to get the header value which will be set to rest client
	 */
	X_ELS_AUTHENTICATION_VALUE,
	/**
	 * Constant name to get the flag(true/false) for multiple platform
	 */
	GET_ALL_PRODUCTS_FOR_PLATFORM,
	/**
	 * Constant name to get the CARS template time format which will be used for
	 * comparing with the mime template update date
	 */
	CARS_TEMPLATE_TIME_FORMAT,
	/**
	 * The constant name for getting smpt server addr which will be used for
	 * sending mail
	 */
	SMTP_SERVER_ADDRESS,
	/**
	 * Constants name to get the mail sender mail address while sending mail
	 */
	SENDER_EMAIL_ADDRESS,
	/**
	 * Constants name to get the product name which will be used for building
	 * XML
	 */
	APP_PRODUCT_NAME,
	/**
	 * Constants name to get the footer info which will be used for constructing
	 * mail content
	 */
	CARS_MAIL_FOOTER,
	/**
	 * Constant name to get the html content which will hold product specific
	 * links or data
	 */
	PRODUCT_SPECIFIC_LINKS,
	/**
	 * DATA Cache Rebuild time from SMAPI for CARS Template
	 */
	DATA_CACHE_REBUILD_TIME,
	/**
	 * Flag whether SSO Authentication is enabled or not (True or False).
	 */
	DISABLE_SSO_AUTH,
	/**
	 * Flag whether pessimistic authentication is turned on or not.
	 */
	SSO_REDIRECT_AUTH,
	/**
	 * platform code
	 */
	PLATFORM_CODE,
	/**
	 * site identifier
	 */
	SITE_IDENTIFIER,
	/**
	 * SSO revalidation time in minutes defined in web config file
	 * which is 10 minutes
	 */
	SSO_REVALIDATION_TIME,
	/**
	 * Flag to check if the request is from scopus
	 * so that we will be appending .url for each submit actions
	 * (not required for other clusters)
	 */
	REQUEST_FROM_SCOPUS,
    /**
     * The url extension as ".url" for appending in Scopus URLS
     */
    APP_URL_EXTN,
    /**
     * Application specific home url to be returned
     */
    APP_HOME_URI,
    /**
     * Application specific page to handle login full cancel link
     */
    LOGIN_FULL_CANCEL_URI,
	/**
	 * Constants to get the cars base uri "/customer"
	 */
	CARS_BASE_URI,
	/**
	 * This is the config variable name to hold url for initial authentication
	 * i.e "/customer/authenticate"
	 */
	INITIAL_AUTHENTICATE_URI,
    /**
     * This is the config variable name to hold url for getting login full template 
     * from CARS i.e "/customer/authenticate/loginfull"
     */
    LOGIN_FULL_URI,
	/**
	 * This is the config variable name to hold url for getting feature
	 * inaccessible template from CARS i.e "/customer/inaccessible"
	 */
	FEATURE_INACCESSIBLE_URI,
	/**
	 * This is the config variable name to hold url for getting activate 
	 * personalization template from CARS i.e "/customer/profile"
	 */
	ACTIVATE_PERSONALIZATION_URI,
    /**
     * production uri to be used for forgot password 
     */
	EMAIL_REMINDER_URI,
    /**
     * production uri to be used for forgot password confirmation mail
     */
    EMAIL_REMINDER_CONFIRM_URI,
    /**
     * Athens/Institution login link
     */
    INSTITUTION_CHOICE_URI,
    /**
     * Display profile URI 
     */
    PROFILE_DISPLAY_URI,
    /**
     * Terminate session / logout URI 
     */
    TERMINATE_URI,
    /**
     * Settings page URI 
     */
    SETTINGS_URI,
    /**
     * Change password page URI 
     */
    CHANGE_PASSWORD_URI,
    /**
     * Expand image for login header
     */
    LOGIN_EXPAND_IMG,
    /**
     * Collapse image for login header
     */
    LOGIN_COLLAPSE_IMG,
    /**
     * Config variable to hold the privacy policy link 
     * to be displayed in register page
     */
    PRIVACY_POLICY_URL,
    /**
     * Config variable to hold the user agreement url 
     * to be displayed in register page
     */
    USER_AGREEMENT_URL,
    /**
     * Constant to get the login links in top right corner
     */
    LOGIN_TOP_LINKS,
    /**
     * Constant to get the application specific logout links in top right corner
     */
    LOGOUT_TOP_LINKS,
	/**
	 * Constant for CARS Cookie Expiration
	 */
	CARS_COOKIE_EXPIRATION,
	/**
	 * Constant for Auth Token Cookie Expiration
	 */
	AUTH_TOKEN_COOKIE_EXPIRATION,
    /**
     * Url of the SSO Master domain
     */
    SSO_MASTER_DOMAIN,
	/**
	 * Constant to get the settings page links in top of the CARS links
	 */
	SETTINGS_TOP_LINKS,
	/**
	 * Constant to get the settings claimed author page link
	 */
	SETTINGS_CLAIMED_AUTHORS_LINK,
	/**
	 * Constant to get the settings page links in bottom of the CARS links
	 */
	SETTINGS_LINKS_BOTTOM,
	/**
	 * Constant to hold the registration confirmation mail navigation bar color
	 */
	REGISTRATION_CONFIRMATION_EMAIL_BAR_COLOR,
	/**
	 * Constant to hold the registration confirmation mail text bar color
	 */
	REGISTRATION_CONFIRMATION_EMAIL_TXT_COLOR,
	/**
	 * Constant to hold the registration confirmation mail message list in body
	 */
	REGISTRATION_CONFIRMATION_EMAIL_MESSAGE_LIST,
    /**
     * Config variable to hold the default profile image path to be 
     * displayed in registration page
     */
	APP_DEFAULT_IMAGE,
	/**
	 * Config variable to hold the show image path
	 */
	APP_HIDE_IMAGE,
	/**
	 * Config variable to hold the hide image path
	 */
	APP_SHOW_IMAGE,
	/**
	 * The config parameter for holding the bulk activation url
	 */
	CARS_BULK_ACTIVATION_URI,
    /**
     * The application domain name
     */
    APP_DOMAIN,
	/**
	 * The cancel url value in registration association page
	 */
	REG_ID_ASSOC_CANCEL,
	/**
	 * Config variable to hold the enable author claim flag value
	 */
	ENABLE_AUTHOR_CLAIMING,
	/**
	 * Config variable to hold the enable refworks flag value
	 */
	ENABLE_REFWORKS,
	/**
	 * RefWorks Settings link to be displayed in Settings page
	 */
	REFWORKS_SETTINGS_LINK,
	/**
	 * Manage Application link to be displayed in Settings page
	 */
	MANAGE_APP_LINK,
	/**
	 * Admin Tool link to be displayed in Settings page
	 */
	ADMIN_TOOL_LINK,
	/**
	 * Config variable for holding path choice text
	 */
	PATH_CHOICE_NOTE_TEXT,
	
	/**
	 * Config variable for Saved Serachses and alert link on settings page
	 */
	SAVED_SEARCHES_ALERTS_LINK,
	
	/**
	 * Config variable for View update folder link on settings page
	 */
	VIEW_UPDATE_FOLDER_LINK,
	/**
	 * Config variable for the View my prefs popup on the settings page
	 */
	VIEW_MY_PREFS_LINK,
    /**
     * Config variable for text zone on top of self-manra page
     */
    CARS_EMAIL_SETUP_TOP_TZ,
    
    /**
     * Config variable for text zone at bottom of self-manra page
     */
    CARS_EMAIL_SETUP_BOT_TZ,
    
    /**
     * Config variable for text zone from CARS_REG_ID_ASSOCIATION template for Registration id association 
     */
    CARS_REG_ID_ASSOCIATION_REGISTER_TZ,
    
    /**
     * Config variable for APP_LEARN_MORE_URL used on Shib choice page (help "learn more" link)
     */
    APP_LEARN_MORE_URL,
    
    CARS_NOBODY_LOGIN_FULL_TZ,
    /**
     * Config variable for text zone on login full page
     */
    LOGIN_FULL_TEXT;
    
	/**
	 * This method is used for getting the string value of cars config variables
	 * 
	 * @param constantName
	 *            - the config variable name
	 * @return String - the value of config variable
	 */
	public static String getConstantAsString(CARSConfigVariables constantName) {
		String returnVal = null;
		if (null != constantName) {
			returnVal = EVProperties.getProperty(constantName.name());
		}
		if (StringUtils.isBlank(returnVal)) {
			returnVal = "";
		}
		return returnVal;
	}

	/**
	 * This method is used for getting the integer value of cars config
	 * variables
	 * 
	 * @param constantName - the config variable name
	 * @return int - the value of config variable
	 */
	public static int getConstantAsInt(CARSConfigVariables constantName) {
		int returnVal = 0;
		if (null != constantName) {
			 returnVal = Integer.parseInt(EVProperties.getProperty(constantName.name()));
		}
		
		return returnVal;
	}

	/**
	 * This method is used for getting the boolean value of cars config
	 * variables
	 * 
	 * @param constantName - the config variable name
	 * @return boolean - the value of config variable
	 */
	public static boolean getConstantAsBoolean(CARSConfigVariables constantName) {
		boolean returnVal = false;
		if (null != constantName) {
			returnVal = Boolean.parseBoolean(EVProperties.getProperty(constantName.name()));
		}
		return returnVal;
	}
}

/*******************************************************************************
 * 
 * ELSEVIER CONFIDENTIAL
 * 
 * This document is the property of Elsevier, and its contents are proprietary
 * to Elsevier. Reproduction in any form by anyone of the materials contained
 * herein without the permission of Elsevier is prohibited. Finders are asked to
 * return this document to the following Elsevier location.
 * 
 * Elsevier 360 Park Avenue South, New York, NY 10010-1710
 * 
 * Copyright (c) 2013 by Elsevier, A member of the Reed Elsevier plc group.
 * 
 * All Rights Reserved.
 * 
 ******************************************************************************/