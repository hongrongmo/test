package org.ei.service.cars;

/**
 * Enum to hold the constant name and value for all XPath to be 
 * retrieved from MIME response and build response/user object
 * 
 * XPath explaination: "*" in xpath actually tell to look this element in whole XML. 
 * this needs to be done to support both kind of elements one which has name space with it and one which does not.
 * Since we are using nameSpace unaware setting for XPath evaluation. 
 *   
 * @author Mahendra
 * @version 1.0
 *
 */
public enum XPathEnum {
	/** Xpath for retrieving status code */
	STATUS_CODE("*//statusCode"),
    /** Xpath for retrieving full user info */
    USER_INFO("*//userInfo"),
    /** Xpath for retrieving auth token */
    AUTH_TOKEN("*//authToken"),
	/** Xpath for retrieving user first name */
	FIRST_NAME("*//firstName"),
	/** Xpath for retrieving user last name */
	LAST_NAME("*//lastName"),
	/** Xpath for retrieving user id */
	USERID("*//userId"),
	/** Xpath for retrieving web user id */
	WEB_USER_ID("*//webUserId"),
	/** Xpath for retrieving department id */
	DEPARTMENT_ID("*//departmentId"),
	/** Xpath for retrieving department name */
	DEPARTMENT_NAME("*//departmentName"),
	/** Xpath for retrieving account id */
	ACCOUNT_ID("*//accountId"),
	/** Xpath for retrieving account name */
	ACCOUNT_NAME("*//accountName"),
	/** Xpath for retrieving account name */
	ACCOUNT_NUMBER("*//accountNumber"),
	/** Xpath for retrieving cross fire access enabled flag */
	CROSSFIRE_ACCESS_ENABLED("*//crossFireAccessEnabled"),
	/** Xpath for retrieving discovery fate access enabled */
	DISCOVERY_GATE_ACCESS_ENABLED("*//discoveryGateAccessEnabled"),
	/** Xpath for getting refworks data */
	RFW_DATA("*//refworksData"),
	/** Xpath for getting refworks high level group id */
	RFW_HIGHLEVEL_GRP_ID("*//refworksHigherLvlGroupId"),
	/** Xpath for getting refworks server settings */
	RFW_SRV_SETTING("*//refworksSrvSetting"),
	/** Xpath for getting refworks regional server settings */
	RFW_RGNL_SRV_SETTING("*//refworksRgnlSrvSetting"),
	/** Xpath for getting refworks allow view group id */
	RFW_ALLOW_VIEW_GRP_ID("*//refworksAllowViewGroupId"),
	/** Xpath for getting refworks user data */
	RFW_USER_DATA("*//refworksUserData"),
	/** Xpath for getting refworks user level group id */
	RFW_USR_LVL_GRP_ID("*//refworksUsrLvlGroupId"),
	/** Xpath for getting refworks user name */
	RFW_USER_NAME("*//refworksUserName"),
	/** Xpath for getting refworks user password */
	RFW_USER_PASSWORD("*//refworksUserPassword"),
	/** Xpath for getting refworks auth type */
	RFW_AUTH_TYPE("*//refworksAuthType"),
	/** Xpath for retrieving profile photo url */
	PROFILE_PHOTO_URL("*//profilePhotoUrl"),
	/** Xpath for retrieving sso url */
	SSO_URL("*//ssoURLs/url"),
	/** Xpath for retrieving sso key */
	SSO_KEY("*//ssoKey"),
	/** Xpath for retrieving allowed registration types */
	ALLOWED_REG_TYPE("*//allowedRegType"),
	/** Xpath for retrieving email address */
	EMAIL("*//email"),
	/** Xpath for retrieving user anonymity */
	USER_ANONYMITY("*//userAnonymity"),
	/** Xpath for retrieving user password reset */
	USER_PASSWORD_RESET("*//userPasswordReset"),
	/** Xpath for retrieving user agreement accepted flag */
	USER_AGRMNT_ACCEPTED("*//userAgreementAccepted"),
	/** Xpath for retrieving association type */
	ASSOCIATION_TYPE("*//associationType"),
	/** Xpath for retrieving template name */
	TEMPLATE_NS2("*//template"),
	/** Xpath for retrieving cars template name */
	TEMPLATE_CARS("*//template"),
	/** Xpath for retrieving template update date */
	TEMPLATE_UPDATE_DATE("*//template_update_date"),
	/** Xpath for retrieving cars cookie */
	CARS_COOKIE("*//cars_cookie"),
	/** Xpath for retrieving force flag */
	FORCE_TOKEN("*//force"),
	/** Xpath for retrieving flow complete flag */
	FLOW_COMPLETE_TOKEN("*//flow_complete"),
	/** Xpath for retrieving page type */
	PAGE_TYPE("*//page"),
	/** Xpath for retrieving usage path info */
	USAGE_PATH_INFO("*//usagePathInfo"),
	/** Xpath for retrieving web prod admin flag */
	WEB_PROD_ADMIN("*//webProdAdmin"),
	/** Xpath for retrieving error type */
	ERROR_TYPE("*//statusDetail/errorType"),
	/** Xpath for retrieving error text */
	ERROR_TEXT("*//statusDetail/errorText"),
	/** Xpath for retrieving error field name */
	ERROR_FIELD_NAME  ("*//statusDetail/fieldName"),
	/** Xpath for retrieving fence ids */
	FENCE_IDS("*//fenceIDs/id"),
	/** Xpath for retrieving customer name */
	CUSTOMER_NAME("*//customer_name"),
	/** Xpath for retrieving data priviledges */
	DATA_PRIVILEGE("*//dataPrivileges"),
	/** Xpath for retrieving admin priviledges */
	ADMIN_PRIVILEGE("*//adminPrivileges"),
	/** Xpath for retrieving developer flag */
	DEVELOPER_FLAG("*//developerFlag"),
	/** Xpath for retrieving next request to be sent */
	NEXT_REQUEST_URI("*//next_call"),
	/** Xpath for retrieving product id */
	PRODUCT_ID("*//productId"),
	/** Xpath for retrieving all list Id */
	ALL_LIST_ID("*//allListId"),
	/** Xpath for retrieving all list update date */
	ALL_LIST_UPDATE_DATE("*//allListUpdateDate"),
	/** Xpath for retrieving user athens enabled flag */
	ATHENS_ENABLED_FLAG("*//athensEnabled"),
	/** Xpath for retrieving page title */
	PAGE_TITLE("*//title"),
    /** Xpath for retrieving redirect url */
    REDIRECT_URL("*//redirect_URL"),
    /** Xpath for retrieving shibboleth url */
    SHIBBOLETH_URL("*//shibboleth_URL"),
	/** Xpath for retrieving athens url */
	ATHENS_URL("*//athens_URL"),
	/** Xpath for retrieving for cars authenticate user response */
	CARS_AUTHENTICATE_USER_RESPONSE("*//carsAuthenticateUserResponse"),
	/** Xpath for retrieving for cars authenticate user response type */
	CARS_AUTHENTICATE_USER_RESPONSE_TYPE("*//authenticateUserResponseType"),
	/** Xpath for retrieving athen fence value */
	ATHENS_FENCE("*//athens_fence"),
	/** Xpath for retrieving shibboleth fence value */
	SHIBBOLETH_FENCE("*//shibboleth_fence"),
	/** Xpath for retrieving path choice exists flag */
	PATH_CHOICE_EXISTS("*//pathChoicesExist"),
	/** Xpath for retrieving path choice */
	PATH_CHOICE("*//path_choice"),
	/** Xpath for retrieving remember path flag */
	REMEMBER_PATH_FLAG("*//remember_path_flag"),	
	/** Xpath for retrieving credential type */
	CRED_TYPE("*//cred_type"),
	/** Xpath for retrieving recipient email address */
	MAIL_RECIPIENT ("*//email_address"),
	/** Xpath for retrieving mail subject */
	MAIL_SUBJECT ("*//email_subject"),
	/** Xpath for retrieving user platform info */
	USR_PLATFORM_INFO("*//userPlatformInfo"),
	/** Xpath for retrieving user scopus info */
	USR_SCOPUS_INFO("*//userScopusInfo"),
	/** Xpath for retrieving enable external subscibed entitlements flag */
	EXTERNAL_SUBSCRIBED_ENT("*//enableXtrnlSubscribedEntitlements"),
	/** Xpath for retrieving mark sub unsub journals */
	MARK_SUB_UNSUB_JOURNAL("*//markSubUnsubJournals"),
	/** Xpath for retrieving quicklink info */
	QUICK_LINK_INFO("*//quickLinkInfo"),
	/** Xpath for retrieving quick link info link label */
	LINK_LABEL("*//quickLinkInfo/linkLabel"),
	/** Xpath for retrieving quick link info mouseover text */
	LINK_MOUSEOVER_TEXT("*//quickLinkInfo/linkMouseOverText"),
	/** Xpath for retrieving quicklink info link url */
	LINK_URL("*//quickLinkInfo/linkURL"),
	/** Xpath for retrieving scirus source info */
	SCIRUS_SOURCE_INFO("*//scirusSourceInfo"),
	/** Xpath for retrieving tab text of scirus source */
	TAB_TEXT("*//scirusSourceInfo/tabText"),
	/** Xpath for retrieving abbreviation of scirus source */
	ABBREVIATION("*//scirusSourceInfo/abbreviation"),
	/** Xpath for getting the opaque info */
	OPAQUE_INFO("*//opaqueInfo"),
	/** Xpath for getting the opaque info name */
	OPAQUE_INFO_NAME("*//opaqueInfo/name"),
	/** Xpath for getting the opaque info value */
	OPAQUE_INFO_VALUE("*//opaqueInfo/value"),
	/** Xpath for getting the path choice info */
	PATH_CHOICE_INFO("*//pathChoiceInfo"),
	/** Xpath for getting the path choice number */
	PATH_CHOICE_NUMBER("*//pathChoiceInfo/pathChoiceNumber"),
	/** Xpath for getting the path choice description */
	PATH_CHOICE_DESC("*//pathChoiceInfo/description"),
	//CARS4
	/** Xpath to get the key event recodr id from CARS mime */
	RECORD_ID("*//key_event/record_id"),
	/** Xpath to get the key event name from CARS mime */
	KEYEVENT_NAME("*//key_event/key_event_field/name"),
	/** Xpath to get the key event value from CARS mime */
	KEYEVENT_VALUE("*//key_event/key_event_field/value"),
	/** Xpath to get the text zone name from CARS mime  */
	TEXTZONE_NAME ("*//text_zone/text_zone_item/name"),
	/** Xpath to get the text zone var from CARS mime  */
	TEXTZONE_VAR ("*//text_zone/text_zone_item/var"),
	DOC_DELIVERY_EMAIL("*//docDeliveryEmail"),
	DOC_DELIVERY_MESSAGE("*//docDeliveryMessage");

	
	/** The attribute to hold the xpath name */
	private final String m_value;
	
	/**
	 * Constructor 
	 * 
	 * @param str - the constant value
	 */
	XPathEnum(String str) {
		this.m_value = str;
	}
	
	/**
	 * Method to get the xpath value
	 * 
	 * @return String - the xpath value
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