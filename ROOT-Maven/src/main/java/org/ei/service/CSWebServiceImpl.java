package org.ei.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;
import org.ei.exception.ServiceException;
import org.ei.service.cars.CARSStringConstants;

import com.elsevier.webservices.schemas.csas.constants.types.v7.AuthenticationType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserReqPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserResponseType;
import com.elsevier.webservices.schemas.csas.types.v13.AuthenticateUserType;
import com.elsevier.webservices.schemas.csas.types.v13.ConnectionInfoType;
import com.elsevier.webservices.schemas.csas.types.v13.ContentEntitlementsMethodLevelReqType;
import com.elsevier.webservices.schemas.csas.types.v13.GetContentEntitlementsReqPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetContentEntitlementsRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetContentEntitlementsResponseType;
import com.elsevier.webservices.schemas.csas.types.v13.GetContentEntitlementsType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValueDefaultsRequestType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValueDefaultsRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValueDefaultsResponseType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValuesForSessionRequestType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValuesForSessionResponseType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValuesForSitesRequestType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValuesForSitesRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetNonCombEntValuesForSitesResponseType;
import com.elsevier.webservices.schemas.csas.types.v13.GetPasswordReminderResponseType;
import com.elsevier.webservices.schemas.csas.types.v13.GetPasswordReminderType;
import com.elsevier.webservices.schemas.csas.types.v13.GetPlatformFenceInfoReqPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetPlatformFenceInfoRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetPlatformFenceInfoResponseType;
import com.elsevier.webservices.schemas.csas.types.v13.GetPlatformFenceInfoType;
import com.elsevier.webservices.schemas.csas.types.v13.GetProfileReqPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.GetProfileType;
import com.elsevier.webservices.schemas.csas.types.v13.LoginPasswordCredentialType;
import com.elsevier.webservices.schemas.csas.types.v13.NonCombEntValueDefaultsRequestPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.NonCombEntValuesForSessionRequestPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.NonCombEntValuesForSessionRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.NonCombEntValuesForSitesRequestPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.PasswordReminderReqPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.PasswordReminderRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.TerminateUserSessionReqPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.TerminateUserSessionResponseType;
import com.elsevier.webservices.schemas.csas.types.v13.TerminateUserSessionType;
import com.elsevier.webservices.schemas.csas.types.v13.UserCredentialsType;
import com.elsevier.webservices.schemas.csas.types.v13.UserProfileRespPayloadType;
import com.elsevier.webservices.schemas.csas.types.v13.UserProfileResponseType;
import com.elsevier.webservices.schemas.easi.headers.types.v1.RequestHeaderType;
import com.elsevier.webservices.schemas.easi.headers.types.v1.ResponseHeaderType;
import com.elsevier.wsdls.csas.service.v13.CSApplicationServicePortTypeV13;

/**
 * This class holds the proxy pool used for csws access. This class provides
 * access to the pool as a singleton.
 */
public class CSWebServiceImpl implements CSWebService {

	private final static Logger log4j = Logger.getLogger(CSWebServiceImpl.class);

	public PasswordReminderRespPayloadType getPasswordReminder(String email, String ipaddress) throws ServiceException {
		CSApplicationServicePortTypeV13 port = null;

		final PasswordReminderReqPayloadType payload = new PasswordReminderReqPayloadType();
		payload.setPlatformInfo(ANEServiceHelper.getEVPlatformInfo());
		payload.setEmail(email);
		payload.setIpAddress(ipaddress);

		GetPasswordReminderType passwordreminderType = new GetPasswordReminderType();
		passwordreminderType.setPasswordReminderReqPayload(payload);

		Holder<GetPasswordReminderResponseType> getpasswordResponseHolder = new Holder<GetPasswordReminderResponseType>();

		try {
			RequestHeaderType reqHeader = ANEServiceHelper.getRequestHeaderHolder();
			Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
			log4j.debug("Retrieving CSWS proxy...");
			port = ANEServiceHelper.getANEService();
			port.getPasswordReminder(passwordreminderType, reqHeader, getpasswordResponseHolder, respHeaderHolder);
			log4j.debug("User info are fateched successfully using Customer System web service....");
		} finally {
			ANEServiceHelper.releasePort(port);
		}

		return getpasswordResponseHolder.value.getPasswordReminderRespPayload();
	}

	public AuthenticateUserRespPayloadType authenticateByIP(String IP) throws ServiceException {
        CSApplicationServicePortTypeV13 port = null;

        ConnectionInfoType connectioninfo = new ConnectionInfoType();
        connectioninfo.setBrowserType("N/A");
        connectioninfo.setIpAddress(IP);

        final AuthenticateUserReqPayloadType payload = new AuthenticateUserReqPayloadType();
        payload.setPathChoiceNumber(1);
        payload.setPlatformInfo(ANEServiceHelper.getEVPlatformInfo());
        payload.setAuthenticationType(AuthenticationType.IP);
        payload.setConnectionInfo(connectioninfo);
        payload.setIsSSODisabled(true);

        AuthenticateUserType authenticateuser = new AuthenticateUserType();
        authenticateuser.setAuthenticateUserReqPayload(payload);

        Holder<AuthenticateUserResponseType> authenticateResponseHolder = new Holder<AuthenticateUserResponseType>();

        try {
            RequestHeaderType reqHeader = ANEServiceHelper.getRequestHeaderHolder();
            Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
            log4j.debug("Retrieving CSWS proxy...");
            port = ANEServiceHelper.getANEService();
            port.authenticateUser(authenticateuser, reqHeader, authenticateResponseHolder, respHeaderHolder);
            log4j.debug("User info are fetched successfully using Customer System web service....");
        } finally {
            ANEServiceHelper.releasePort(port);
        }

        return authenticateResponseHolder.value.getAuthenticateUserRespPayload();
	}

	public AuthenticateUserRespPayloadType authenticateLindaHall(String username, String password) throws ServiceException {
		CSApplicationServicePortTypeV13 port = null;

		UserCredentialsType usercredentials = new UserCredentialsType();
		LoginPasswordCredentialType loginpassword = new LoginPasswordCredentialType();
		loginpassword.setUserId(username);
		loginpassword.setPassword(password);
		usercredentials.setLoginPasswordCredential(loginpassword);

		ConnectionInfoType connectioninfo = new ConnectionInfoType();
		connectioninfo.setBrowserType("N/A");
		connectioninfo.setIpAddress("0.0.0.0");

		final AuthenticateUserReqPayloadType payload = new AuthenticateUserReqPayloadType();
		payload.setPathChoiceNumber(1);
		payload.setPlatformInfo(ANEServiceHelper.getEVPlatformInfo());
		payload.setUserCredentials(usercredentials);
		payload.setAuthenticationType(AuthenticationType.LOGIN_PASSWORD);
		payload.setConnectionInfo(connectioninfo);
		payload.setIsSSODisabled(true);

		AuthenticateUserType authenticateuser = new AuthenticateUserType();
		authenticateuser.setAuthenticateUserReqPayload(payload);

		Holder<AuthenticateUserResponseType> authenticateResponseHolder = new Holder<AuthenticateUserResponseType>();

		try {
			RequestHeaderType reqHeader = ANEServiceHelper.getRequestHeaderHolder();
			Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
			log4j.debug("Retrieving CSWS proxy...");
			port = ANEServiceHelper.getANEService();
			port.authenticateUser(authenticateuser, reqHeader, authenticateResponseHolder, respHeaderHolder);
			log4j.debug("User info are fetched successfully using Customer System web service....");
		} finally {
			ANEServiceHelper.releasePort(port);
		}

		authenticateResponseHolder.value.getAuthenticateUserRespPayload().getStatus();
		authenticateResponseHolder.value.getAuthenticateUserRespPayload().getUserInfo();

		return authenticateResponseHolder.value.getAuthenticateUserRespPayload();
	}

	public void terminate(String authtoken) throws ServiceException {
		CSApplicationServicePortTypeV13 port = null;

		TerminateUserSessionReqPayloadType terminationpayload = new TerminateUserSessionReqPayloadType();
		terminationpayload.setAuthToken(authtoken);
		terminationpayload.setPlatformInfo(ANEServiceHelper.getEVPlatformInfo());

		TerminateUserSessionType termination = new TerminateUserSessionType();
		termination.setTerminateUserSessionReqPayload(terminationpayload);
		Holder<TerminateUserSessionResponseType> terminationResponseHolder = new Holder<TerminateUserSessionResponseType>();

		try {
			RequestHeaderType reqHeader = ANEServiceHelper.getRequestHeaderHolder();
			Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
			log4j.debug("Retrieving CSWS proxy...");
			port = ANEServiceHelper.getANEService();
			port.terminateUserSession(termination, reqHeader, terminationResponseHolder, respHeaderHolder);
			log4j.debug("User info are fateched successfully using Customer System web service....");
		} finally {
			ANEServiceHelper.releasePort(port);
		}
	}

	public UserProfileRespPayloadType getProfile(int webUserID) throws ServiceException {
		CSApplicationServicePortTypeV13 port = null;

		GetProfileType getProfile = new GetProfileType();
		GetProfileReqPayloadType getProfileReqPayload = new GetProfileReqPayloadType();
		getProfileReqPayload.setPlatformInfo(ANEServiceHelper.getEVPlatformInfo());
		getProfileReqPayload.setWebUserId(Integer.toString(webUserID));
		getProfile.setGetProfileReqPayload(getProfileReqPayload);
		Holder<UserProfileResponseType> profileResponseHolder = new Holder<UserProfileResponseType>();

		try {
			RequestHeaderType reqHeader = ANEServiceHelper.getRequestHeaderHolder();
			Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
			log4j.debug("Retrieving CSWS proxy...");
			port = ANEServiceHelper.getANEService();
			port.getProfile(getProfile, reqHeader, profileResponseHolder, respHeaderHolder);
			log4j.debug("User info are fateched successfully using Customer System web service....");
		} finally {
			ANEServiceHelper.releasePort(port);
		}

		profileResponseHolder.value.getUserProfileRespPayload().getStatus();
		profileResponseHolder.value.getUserProfileRespPayload().getUserData();

		return profileResponseHolder.value.getUserProfileRespPayload();
	}

	public GetNonCombEntValueDefaultsRespPayloadType getNonCombEntValueDefaults() throws ServiceException {
		CSApplicationServicePortTypeV13 port = null;

		GetNonCombEntValueDefaultsRequestType getNonCombEntValueDefaultsRequestType = new GetNonCombEntValueDefaultsRequestType();
		NonCombEntValueDefaultsRequestPayloadType nonCombEntValueDefaultsRequestPayloadType = new NonCombEntValueDefaultsRequestPayloadType();
		nonCombEntValueDefaultsRequestPayloadType.setPlatformInfo(ANEServiceHelper.getEVPlatformInfo());

		/*
		 * if() {
		 * nonCombEntValueDefaultsRequestPayloadType.setReturnAllProductsOfPlatform
		 * (true); } else {
		 * nonCombEntValueDefaultsRequestPayloadType.setProductId(productId);
		 * nonCombEntValueDefaultsRequestPayloadType
		 * .setReturnAllProductsOfPlatform(false); }
		 */
		// nonCombEntValueDefaultsRequestPayloadType.setProductId(getANEServiceConfigurationProperty(ANEServiceConstants.PRODUCT_ID));
		nonCombEntValueDefaultsRequestPayloadType.setReturnAllProductsOfPlatform(true);
		getNonCombEntValueDefaultsRequestType.setNonCombEntValueDefaultsRequestPayloadType(nonCombEntValueDefaultsRequestPayloadType);
		Holder<GetNonCombEntValueDefaultsResponseType> getNonCombEntValueDefaultsResponseTypHolder = new Holder<GetNonCombEntValueDefaultsResponseType>();

		try {
			RequestHeaderType reqHeader = ANEServiceHelper.getRequestHeaderHolder();
			Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
			log4j.debug("Retrieving CSWS proxy...");
			port = ANEServiceHelper.getANEService();
			port.getNonCombEntValueDefaults(getNonCombEntValueDefaultsRequestType, reqHeader, getNonCombEntValueDefaultsResponseTypHolder, respHeaderHolder);

			log4j.debug("templates are fateched successfully using Customer System web service....");
		} finally {
			ANEServiceHelper.releasePort(port);
		}

		return getNonCombEntValueDefaultsResponseTypHolder.value.getGetNonCombEntValueDefaultsRespPayloadType();
	}

	public NonCombEntValuesForSessionRespPayloadType getNonCombEntValueForSession(String authToken, String carsSessionId) throws ServiceException {

		log4j.debug("Retrieving NonCombEntData per session value..-Begin");
		CSApplicationServicePortTypeV13 port = null;

		GetNonCombEntValuesForSessionRequestType getNonCombEntValuesForSessionRequestType = new GetNonCombEntValuesForSessionRequestType();

		NonCombEntValuesForSessionRequestPayloadType nonCombEntValuesForSessionRequestPayloadType = new NonCombEntValuesForSessionRequestPayloadType();
		nonCombEntValuesForSessionRequestPayloadType.setPlatformInfo(ANEServiceHelper.getEVPlatformInfo());
		nonCombEntValuesForSessionRequestPayloadType.setAuthToken(authToken);
		getNonCombEntValuesForSessionRequestType.setNonCombEntValuesForSessionRequestPayloadType(nonCombEntValuesForSessionRequestPayloadType);

		Holder<GetNonCombEntValuesForSessionResponseType> GetNonCombEntValuesForSessionResponseTypeHolder = new Holder<GetNonCombEntValuesForSessionResponseType>();

		try {
			RequestHeaderType reqHeader = ANEServiceHelper.getRequestHeaderHolder();
			Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
			log4j.debug("Retrieving CSWS proxy...");

			port = ANEServiceHelper.getANEService();

			setCARSJSessionIdInSoapCall(port, carsSessionId);
			port.getNonCombEntValuesForSession(getNonCombEntValuesForSessionRequestType, reqHeader, GetNonCombEntValuesForSessionResponseTypeHolder,
					respHeaderHolder);

			log4j.info("***********************************user call response server id for seesion id=" + carsSessionId
					+ ":*********************************** " + respHeaderHolder.value.getServerId());
			log4j.debug("templates are fateched successfully using Customer System web service....");
		} finally {
			ANEServiceHelper.releasePort(port);
		}
		log4j.debug("Retrieving NonCombEntData per session value..-End");
		return GetNonCombEntValuesForSessionResponseTypeHolder.value.getNonCombEntValuesForSessionResponsePayloadType();
	}

	public GetNonCombEntValuesForSitesRespPayloadType getNonCombEntValuesForSites(String nceTypeName) throws ServiceException {
		CSApplicationServicePortTypeV13 port = null;

		GetNonCombEntValuesForSitesRequestType getNonCombEntValuesForSitesRequestType = new GetNonCombEntValuesForSitesRequestType();
		NonCombEntValuesForSitesRequestPayloadType getNonComEntValuesForSitesReqPayloadType = new NonCombEntValuesForSitesRequestPayloadType();
		getNonComEntValuesForSitesReqPayloadType.setPlatformInfo(ANEServiceHelper.getEVPlatformInfo());
		getNonComEntValuesForSitesReqPayloadType.getNceTypeName().add(nceTypeName);
		getNonComEntValuesForSitesReqPayloadType.setReturnAllSitesOfPlatform(true);
		getNonCombEntValuesForSitesRequestType.setNonCombEntValuesForSitesRequestPayloadType(getNonComEntValuesForSitesReqPayloadType);
		Holder<GetNonCombEntValuesForSitesResponseType> getNonCombEntValuesForSitesResponseHolderType = new Holder<GetNonCombEntValuesForSitesResponseType>();

		try {
			RequestHeaderType reqHeader = ANEServiceHelper.getRequestHeaderHolder();
			Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
			log4j.debug("Retrieving CSWS proxy...");
			port = ANEServiceHelper.getANEService();
			port.getNonCombEntValuesForSites(getNonCombEntValuesForSitesRequestType, reqHeader, getNonCombEntValuesForSitesResponseHolderType, respHeaderHolder);
			log4j.debug("templates are fateched successfully using Customer System web service....");
		} finally {
			ANEServiceHelper.releasePort(port);
		}

		return getNonCombEntValuesForSitesResponseHolderType.value.getGetNonCombEntValuesForSitesRespPayloadType();
	}

	public GetPlatformFenceInfoRespPayloadType getPlatformFenceInfo() throws ServiceException {

		CSApplicationServicePortTypeV13 port = null;

		GetPlatformFenceInfoType platformFenceInfo = new GetPlatformFenceInfoType();
		GetPlatformFenceInfoReqPayloadType platformFenceInfoReqPayload = new GetPlatformFenceInfoReqPayloadType();

		platformFenceInfoReqPayload.setPlatformInfo(ANEServiceHelper.getEVPlatformInfo());
		platformFenceInfo.setGetPlatformFenceInfoReqPayload(platformFenceInfoReqPayload);
		Holder<GetPlatformFenceInfoResponseType> platformFenceInfoResponseHolder = new Holder<GetPlatformFenceInfoResponseType>();

		try {
			RequestHeaderType reqHeader = ANEServiceHelper.getRequestHeaderHolder();
			Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
			log4j.debug("Retrieving CSWS proxy...");
			port = ANEServiceHelper.getANEService();
			port.getPlatformFenceInfo(platformFenceInfo, reqHeader, platformFenceInfoResponseHolder, respHeaderHolder);
			log4j.debug("Getting all the platform fence info using Customer System web service...");
		} finally {
			ANEServiceHelper.releasePort(port);
		}

		return platformFenceInfoResponseHolder.value.getGetPlatformFenceInfoRespPayload();
	}

	private String createRequestLevelValuesString(String authToken) {
		StringBuffer requestLevelValue = new StringBuffer();
		requestLevelValue.append("authToken=");
		requestLevelValue.append(authToken);
		requestLevelValue.append("|");
		requestLevelValue.append("platformCode=");
		requestLevelValue.append(ANEServiceHelper.getEVPlatformInfo().getPlatformCode());
		requestLevelValue.append("|");
		requestLevelValue.append("siteIdentifier=");
		requestLevelValue.append(ANEServiceHelper.getEVPlatformInfo().getSiteIdentifier());

		return requestLevelValue.toString();
	}

	public GetContentEntitlementsRespPayloadType getUserEntitelments(String authToken, String carsSessionId) throws ServiceException {

		CSApplicationServicePortTypeV13 port = null;

		GetContentEntitlementsType contentEntitlementsType = new GetContentEntitlementsType();
		GetContentEntitlementsReqPayloadType entitlementsReqPayloadType = new GetContentEntitlementsReqPayloadType();

		// Format the data need to be set on for this call.
		// authToken=Q79f7-d02a64e3a3186d8ca66b570b52323eab5d5|platformCode=EV|siteIdentifier=engvil
		// isContentLevel=Y|keyName=databaseid

		entitlementsReqPayloadType.setRequestLevelValues(createRequestLevelValuesString(authToken));
		ContentEntitlementsMethodLevelReqType contentEntitlementsMethodLevelReqTypeForDataBaseContentType = new ContentEntitlementsMethodLevelReqType();
		contentEntitlementsMethodLevelReqTypeForDataBaseContentType.setMethodLevelValues(createMethodLevelValuesStringForDataBaseContentType());
		ContentEntitlementsMethodLevelReqType contentEntitlementsMethodLevelReqTypeForBulletinContentType = new ContentEntitlementsMethodLevelReqType();
		contentEntitlementsMethodLevelReqTypeForBulletinContentType.setMethodLevelValues(createMethodLevelValuesStringForBulletinContentType());

		entitlementsReqPayloadType.getEntitlementsMethod().add(contentEntitlementsMethodLevelReqTypeForDataBaseContentType);
		entitlementsReqPayloadType.getEntitlementsMethod().add(contentEntitlementsMethodLevelReqTypeForBulletinContentType);
		contentEntitlementsType.setGetContentEntitlementsReqPayload(entitlementsReqPayloadType);

		Holder<GetContentEntitlementsResponseType> contentEntitlementsResponseHolder = new Holder<GetContentEntitlementsResponseType>();

		try {
			Holder<ResponseHeaderType> respHeaderHolder = new Holder<ResponseHeaderType>();
			port = ANEServiceHelper.getANEService();

			setCARSJSessionIdInSoapCall(port, carsSessionId);
			port.getContentEntitlements(contentEntitlementsType, ANEServiceHelper.getRequestHeaderHolder(), contentEntitlementsResponseHolder, respHeaderHolder);

			log4j.info("***********************************user call response server id for seesion id=" + carsSessionId
					+ ":*********************************** " + respHeaderHolder.value.getServerId());
		} finally {
			ANEServiceHelper.releasePort(port);
		}

		return contentEntitlementsResponseHolder.value.getGetContentEntitlementsRespPayload();
	}

	private void setCARSJSessionIdInSoapCall(CSApplicationServicePortTypeV13 port, String jSessionId) {

		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> cookies = new ArrayList<String>();
		cookies.add(CARSStringConstants.SESSION_AFFINITY_KEY.value() + "=" + jSessionId);
		map.put(CARSStringConstants.COOKIE.value(), cookies);
		((BindingProvider) port).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, map);
	}

	private String createMethodLevelValuesStringForDataBaseContentType() {
		StringBuffer methodLevelValues = new StringBuffer();
		methodLevelValues.append("contentTypeCode=DB");
		methodLevelValues.append("|");
		methodLevelValues.append("isContentLevel=Y");
		methodLevelValues.append("|");
		methodLevelValues.append("keyName=databaseid");

		return methodLevelValues.toString();
	}

	private String createMethodLevelValuesStringForBulletinContentType() {
		StringBuffer methodLevelValues = new StringBuffer();
		methodLevelValues.append("contentTypeCode=BLT");
		methodLevelValues.append("|");
		methodLevelValues.append("isContentLevel=Y");
		methodLevelValues.append("|");
		methodLevelValues.append("keyName=bltid");

		return methodLevelValues.toString();
	}

}
