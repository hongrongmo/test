package org.ei.service.cars;

/**
 * Enum class to hold the constants name with value as string
 *
 * @author naikn1
 * @version 1.0
 *
 */
public enum CARSStringConstants {
	/**
	 * Constant to hold the jsessionid value retrieved from REST call
	 *
	 */

	REQUEST_PARAMS_HOLDER("requestParams"),
	FILE_PARAMS("fileParams"),
	PROFILE_IMAGE("profileImage"),
	MIME_SEPARATOR("--simple boundary"),
	LESS_THAN("<"),
	SESSION_AFFINITY_KEY("JSESSIONID"),
	EQUAL("="),
	SEMICOLON(";"),
	MULTIPART_MIXED("multipart/mixed"),
	APPLICATION_CONTENT_TYPE("application/x-www-form-urlencoded"),
	APPLICATION_XML("application/xml"),
	XPATH_EXR("...XPath Expression = "),
	XPATH_EXR_VAL("...XPath Expression Value = "),
	XPATH_COMPILE_ERROR("Xpath compile error: "),
	XPATH_COMMON_ERROR("Xpath related error: "),
	CARS_TEXT("CARS_TEXT"),
	XSL_PATH("/common/cars/xslt/AppParams.xsl"),
	LEFT_OPEN_TAG("<apps:"),
	LEFT_CLOSE_TAG("</apps:"),
	RIGHT_CLOSE_TAG(">"),
	LESS_THAN_ENCODE("&lt;"),
	GREATER_THAN_ENCODE("&gt;"),
	GREATER_THAN(">"),
	GTR_THAN_UTF8("&amp;gt;"),
	AMPERSAND_UTF8("&amp;"),
	AMPERSAND("&"),
	XML_END_PARAM("?>"),
	YES("Y"),
	NO("N"),
	PATH_SEPARATOR("/"),
	USER_LOGGED_OUT_BY_SSO("USER_LOGGED_OUT_BY_SSO"),
	TRUE("true"),
	UNDERSCORE("_"),
	SETTER("set"),
	SET_COOKIE("Set-Cookie"),
	URL_ENCODE("UTF-8"),
	HEADER_CONTENT("HEADER_CONTENT"),
	CARS_CONTENT("CARS_CONTENT"),
	PAGE_TITLE("pagetitle"),
	SETTINGS_LINK_OPEN_DIV("<div id=\"settingsTopLinks\"><ul>"),
	SETTINGS_LINK_CLOSE_DIV("</ul></div>"),
	PROFILE_PHOTO("profile_photo"),
	CONFIRMATION_EMAIL_XSL_PATH("/common/cars/xslt/ConfirmationEmail.xsl"),
	COOKIE("Cookie"),
	ACW("acw"),
	ACCEPT_ENCODING("Accept-Encoding");

	/*
	SESSION_AFFINITY_KEY("JSESSIONID"),
	*//**
	 * Constant to hold the value of media type mutipart mixed
	 *//*
	MULTIPART_MIXED("multipart/mixed"),
	*//**
	 * Constant to hold the value of media type application xml
	 *//*
	APPLICATION_XML("application/xml"),
	*//**
	 * Constant to hold the value of media type application url encode
	 *//*
	APPLICATION_CONTENT_TYPE("application/x-www-form-urlencoded"),
	*//**
	 * Constant name to hold the request parameters from request
	 *//*
	REQUEST_PARAMS_HOLDER("requestParams"),
	*//**
	 * Constant name to hold the value of cookie values in response header
	 *//*
	SET_COOKIE("Set-Cookie"),
	*//**
	 * Constant name to hold the empty value
	 *//*
	EMPTY(""),
	*//**
	 * Constant name to hold the value of equal operator
	 *//*
	EQUAL("="),
	*//**
	 * Constant name to hold the value of semicolon
	 *//*
	SEMICOLON(";"),
	*//**
	 * Constant name to hold the value of question mark
	 *//*
	QUESTION_MARK("?"),
	*//**
	 * Constant name to hold the value of path separator
	 *//*
	PATH_SEPARATOR("/"),
	*//**
	 * Constant name to hold the value of end tag used in xml declaration
	 *//*
	XML_END_PARAM("?>"),
	*//**
	 * Constant name to hold the value of XSL path used for creating XML
	 *//*
	XSL_PATH("/main/resources/stylesheets/xml/AppParams.xsl"),
	*//**
	 * XSL path for email confirmation
	 *//*
	CONFIRMATION_EMAIL_XSL_PATH("/main/resources/stylesheets/email/ConfirmationEmail.xsl"),
	*//**
	 * Constant to show value Y
	 *//*
	YES("Y"),
	*//**
	 * Constant to show value N
	 *//*
	NO("N"),
	*//**
	 * error message to be sent when mail adress is invalid
	 *//*
	INVALID_MAIL_ADDR("Invalid address found while sending e-mail!"),
	*//**
	 * error message to be sent when mail is undelivered to receiver
	 *//*
	UNDELIVERABLE_MAIL("E-mail not delivered to the specified recipient!"),
	*//**
	 * error message to be sent when smpt client is not able to send mail
	 *//*
	SMTP_SERVER_ERROR(
			"SmtpClient is not able to complete e-mail sending operation!"),
	*//**
	 * Error message to be sent when sending mail fails
	 *//*
	SEND_MAIL_ERROR("Error occured while trying to send mail"),
	*//**
	 * Error message to be sent when exceptions occurs during parsing stream to
	 * string
	 *//*
	STREAM_PARSE_ERROR("Exception encountered parsing stream to string"),
	*//**
	 * message to show jsessionid
	 *//*
	JSESSIONID_AVAIL_MSG("JSESSIONID from CARS response is : "),
	*//**
	 * message to be shown when jsession id is not available
	 *//*
	JSESSIONID_NOT_AVAIL_MSG("JSESSIONID is not available"),
	*//**
	 * The message for showing cars rest url
	 *//*
	CARS_REST_URI("CARS Target URI for invoking REST call : "),
	*//**
	 * Debug message to be shown at the start of invokeMultiPart method
	 *//*
	MULTIPART_START_METHOD("******** Start invokeMultiPart() method ********"),
	*//**
	 * Debug message to be shown at the end of invokeMultiPart method
	 *//*
	MULTIPART_END_METHOD("******** Start invokeMultiPart() method ********"),
	*//**
	 * Debug message to be shown at the start of invokeCommonRequest method
	 *//*
	INVOKE_START_METHOD("******** Start invokeCommonRequest() method ********"),
	*//**
	 * Debug message to be shown at the end of invokeCommonRequest method
	 *//*
	INVOKE_END_METHOD("******** End invokeCommonRequest() method ********"),
	*//**
	 * Debug message to be shown for cars rest url
	 *//*
	CARS_POST_URL("CARS POST Request URL : "),
	*//**
	 * Debug message to be shown before rest call in POST method
	 *//*
	MULTIPART_POST_BEFORE("Inside Http POST Method: Before Rest Call"),
	*//**
	 * Debug message to be shown after rest call in POST method
	 *//*
	MULTIPART_POST_AFTER("Inside Http POST Method: After Rest Call"),
	*//**
	 * Debug message to be shown before rest call in PUT method
	 *//*
	MULTIPART_PUT_BEFORE("Inside Http PUT Method: Before Rest Call"),
	*//**
	 * Debug message to be shown after rest call in PUT method
	 *//*
	MULTIPART_PUT_AFTER("Inside Http PUT Method: After Rest Call"),
	*//**
	 * Represents separator for MIME response
	 *//*
	MIME_SEPARATOR("--simple boundary"),
	*//**
	 * Less than constant
	 *//*
	LESS_THAN("<"),
	*//**
	 * class name to be shown while cutting CPM Timer
	 *//*
	CPM_REST_CLASS("CARSRestRequestProcessor"),
	*//**
	 * method name to be shown while cutting CPM Timer
	 *//*
	CPM_REST_METHOD("invoke"),
	*//**
	 * Identifier to be shown while cutting CPM Timer
	 *//*
	CPM_REST_IDENTIFIER("RESTFul_Service_CALL"),
	*//**
	 * Message to be displayed for client web exception
	 *//*
	CLIENT_WEB_ERROR("ClientWebException in CARS call : "),
	*//**
	 * Message to be displayed for any exception occurs in rest call
	 *//*
	CLIENT_COMMON_ERROR("Exception in CARS call : "),
	*//**
	 * error to be shown when not able to load xsl file in given path
	 *//*
	XSL_FILE_NOT_FOUND("Failed to load XSL file in given path."),
	*//**
	 * error to be shown while transforming xsl
	 *//*
	XSL_TRANSFORM_ERROR("Error occured during xsl transformation."),
	*//**
	 * Messge to print xml to be transformed
	 *//*
	XML_TRANSFORM_DATA("XML that has to be transformed : "),
	*//**
	 * Represents message to show xpath expression
	 *//*
	XPATH_EXR("...XPath Expression = "),
	*//**
	 * Represents message to show xpath expression value
	 *//*
	XPATH_EXR_VAL("...XPath Expression Value = "),
	*//**
	 * Represents message to show for xpath compile error
	 *//*
	XPATH_COMPILE_ERROR("Xpath compile error: "),
	*//**
	 * Represents message to show for any Xpath errors
	 *//*
	XPATH_COMMON_ERROR("Xpath related error: "),
	*//**
	 * String representing sso logout session attribute
	 *//*
	USER_LOGGED_OUT_BY_SSO("USER_LOGGED_OUT_BY_SSO"),
	*//**
	 * String representing true
	 *//*
	TRUE("true"),
	*//**
	 * String value for under score(_)
	 *//*
	UNDERSCORE("_"),
	*//**
	 * info message to be shown error method
	 *//*
	CARS_ERROR_METHOD("Inside Error Block"),
	*//**
	 * error message to be shown when any error occurs during cars call
	 *//*
	CARS_ERROR_MSG("Status - Error Code : "),
	*//**
	 * Message to show that the request object is null
	 *//*
	REQUEST_EMPTY_MSG("Incoming CARS Request is null or empty."),
	*//**
	 * Message to show that the request object is invalid
	 *//*
	REQUEST_INVALID_MSG("CARSRequest object contains invalid parameters."),
	*//**
	 * Message to show that the response from cars is empty
	 *//*
	CARS_RESP_EMPTY("Response from CARS service is empty"),
	*//**
	 * Message to show that it is inside logout action
	 *//*
	LOGOUT_METHOD("Inside CARS Logout Action"),
	*//**
	 * String to represent class name while cutting cpm timer for xml transform
	 *//*
	CPM_XMLTRANSFORM_CLASS("CARSProcessor"),
	*//**
	 * String to represent method name while cutting cpm timer for xml transform
	 *//*
	CPM_XMLTRANSFORM_METHOD("transform"),
	*//**
	 * String to represent id name while cutting cpm timer for xml transform
	 *//*
	CPM_XMLTRANSFORM_ID("XML_TRANSFORMATION"),
	*//**
	 * Transformer configuration exception message
	 *//*
	XML_CONFIG_ERROR("TransformerFactory Error: "),
	*//**
	 * Transformer exception message
	 *//*
	XML_TRANSFORM_ERROR("Transformer transform Error: "),
	*//**
	 * Common transformer exception
	 *//*
	XML_COMMON_ERROR("Transformer Error New: "),
	*//**
	 * Template name for forgot password email
	 *//*
	FORGOT_PWD_EMAIL("CARS_FORGOTTEN_PASSWORD_EMAIL"),
	*//**
	 * Info Message before initializing CARS Template Cache
	 *//*
	CARS_TEMPLATE_CACHE_START_MESSAGE(
			"Initializing CARS Template Cache---->Start"),
	*//**
	 * Info Message after initializing CARS Template Cache
	 *//*
	CARS_TEMPLATE_CACHE_END_MESSAGE("Initializing CARS Template ---->End"),
	*//**
	 * Error Message if not able to load the CARS Template Cache
	 *//*
	CARS_TEMPLATE_CACHE_ERROR_MESSAGE("Couldn't load Template Cache data"),
	*//**
	 * Date format
	 *//*
	DATE_FORMAT("ddMMyyyyHHmmss"),
	*//**
	 * Symbol for greater than
	 *//*
	GREATER_THAN(">"),
	*//**
	 * Symbol for less than in html encoded format
	 *//*
	LESS_THAN_ENCODE("&lt;"),
	*//**
	 * Symbol for greater than in html encoded format
	 *//*
	GREATER_THAN_ENCODE("&gt;"),
	*//**
	 * UTF-8 decoding - SSOCookie
	 *//*
	URL_ENCODE("UTF-8"),
	*//**
	 * identifier for .url
	 *//*
	URL_EXTN(".url"),
	*//**
	 * This value should be used in a call to
	 * getFeatureConstraints ( ).getBoolean ( ) to
	 * determine if the user has administrative privileges.
	 *//*
	IS_ADMIN_USER("isAdminUser"),
	*//**
	 * This value should be used in a call to
	 * getFeatureConstraints ( ).getBoolean ( ) to determine if the user has
	 * data privileges (can see the raw data in xml format).
	 *//*
	RAW_XML_VIEW_AVAILABLE("enableRawXmlView"),
	*//**
	 * This value should be used in a call to
	 * getFeatureConstraints ( ).getBoolean ( ) to determine if the user
	 * has the external source entitlements enabled
	 *//*
	IS_ENABLED_XTRNL_SUBSCRIBED_ENTITLEMENTS("isEnableXtrnlSubscribedEntitlements"),
	*//**
	 * Message to be shown when SITE_IDENTIFIER is not confugured in web
	 *//*
	SITEIDENTIFIER_NOT_CONFIGURED("SITE_IDENTIFIER is not confugured in web"),
	*//**
	 * Message to be shown when PLATFORM_CODE is not confugured in web
	 *//*
	PLATFORMCODE_NOT_CONFIGURED("PLATFORM_CODE is not confugured in web"),
	*//**
	 * Constant for CARS Header Content
	 *//*
	HEADER_CONTENT("HEADER_CONTENT"),
	*//**
	 * Variable to hold CARS HTML content
	 *//*
	CARS_CONTENT("CARS_CONTENT"),
	*//**
	 * Variable to hold login full page zone data in right side
	 *//*
	LOGIN_FULL_REG_TZ("LOGIN_FULL_REG_TZ"),
	*//**
	 * Variable to hold CARS PAGE TITLE
	 *//*
	 PAGE_TITLE("pagetitle"),
	 *//**
	  * The cars error type to be set in order to forward the control to corresponding JSPs
	  *//*
	 CARS_ERROR_TYPE("CARS_ERROR_TYPE"),
	 *//**
	  * Forward page path when any error occurs during rest call
	  *//*
	 CARS_ERROR_PAGE("CARSErrorPage"),
	 *//**
	  * Forwarding the request to marketing home page when ip authentication fails
	  *//*
	 MARKETING_HOME_PAGE("MarketingHomePage"),
	 //CARS4
	 *//**
	  * Variable to hold setter constant
	  *//*
	 SETTER("set"),
	 *//**
	  * Variable to hold nceTypeName for CARS_TEXT
	  * textzone category
	  *//*
	 CARS_TEXT("CARS_TEXT"),
	 *//**
	  * Open tag
	  *//*
	 LEFT_OPEN_TAG("<apps:"),
	 *//**
	  * Left close tag
	  *//*
	 LEFT_CLOSE_TAG("</apps:"),
	 *//**
	  * Close tag
	  *//*
	 RIGHT_CLOSE_TAG(">"),
	 *//**
	  * Variable to hold CARS Registration MuliPart File field Params
	 *//*
	 FILE_PARAMS("fileParams"),
	 *//**
	  * Constant to hold the author claim fence value
	  *//*
	 AUTHCLAIM_AVAIL_FENCE("AUTHCLAIM_AVAIL"),
	 *//**
	  * Constant to hold the fence name for manage applications
	  *//*
	 MANAGE_APP_FENCE("Enable_SciVerse_App_Marketplace_Link"),
	 *//**
	  * Constant to hold the fence name for admin user
	  *//*
	 ADMIN_USER_FENCE("isAdminUser"),
	 *//**
	  *
	  *//*
	 GTR_THAN_UTF8("&amp;gt;"),
	 *//**
	  *
	  *//*
	 AMPERSAND_UTF8("&amp;"),
	 *//**
	  *
	  *//*
	 AMPERSAND("&"),
	 *//**
	  *
	  *//*
	 SETTINGS_LINK_OPEN_DIV("<div id=\"scopusTopLinks\"><ul>"),
	 *//**
	  *
	  *//*
	 SETTINGS_LINK_CLOSE_DIV("</ul></div>"),
	 *//**
	  *
	  *//*
	 LOGIN_FULL_TEXTZONE("ScopusLoginPageMarketingZone"),
	 *//**
	  * Constant to hold the input file type form name
	  *
	  *//*
	 PROFILE_IMAGE("profileImage"),
	 *//**
	 * Constant to hold photo upload content
	 *//*
	 PROFILE_PHOTO("profile_photo");*/


	/** The attribute to hold the constant name */
	private final String m_value;

	/**
	 * Constructor
	 *
	 * @param str the constant value
	 */
	private CARSStringConstants(String str) {
		this.m_value = str;
	}

	/**
	 * Method to get the constant value
	 *
	 * @return m_value
	 */
	public String value() {
		return m_value;
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
