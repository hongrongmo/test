package org.ei.service.cars.rest.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.controller.StripesRequestWrapper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.config.EVProperties;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.ANEServiceConstants;
import org.ei.service.cars.CARSAuthenticationConstants;
import org.ei.service.cars.CARSRequestType;
import org.ei.service.cars.CARSStringConstants;
import org.ei.service.cars.HttpMethod;
import org.ei.service.cars.RESTRequestParameters;
import org.ei.session.BaseCookie;
import org.ei.session.SSOHelper;
import org.ei.session.UserPreferences;
import org.ei.session.UserSession;
import org.ei.stripes.util.HttpRequestUtil;

import com.elsevier.webservices.schemas.csas.constants.types.v7.AuthenticationType;

public abstract class CARSRequestBase implements CARSRequest {
	private final static Logger log4j = Logger.getLogger(CARSRequestBase.class);

	protected HttpServletRequest httprequest;
	protected IEVWebUser webUser;
	protected String requestURI;
	protected HttpMethod httpMethod = HttpMethod.GET;
	protected Map<RESTRequestParameters, Object> requestParamMap = new EnumMap<RESTRequestParameters, Object>(RESTRequestParameters.class);
	protected String sessionAffinityVal;
	protected CARSRequestType requestType;
	protected boolean isSSOAuthenticationReq = false;
	protected UserSession userSession;

	protected CARSRequestBase() {
	};

	/**
	 * CARSRequest* objects must be constructed with the current HTTP request
	 * object!
	 */
	public CARSRequestBase(HttpServletRequest httprequest, UserSession userSession) throws ServiceException {
		this.httprequest = httprequest;
		this.userSession = userSession;
		this.webUser = userSession.getUser();
		this.setSessionAffinity(userSession.getUser().getCarsJSessionId());

		//
		// Add common CARS request parameters
		//
		appendRESTRequestParams();

		//
		// Add context-based CARS params
		//
		appendContextualParams();

		//
		// Add user info to request
		//
		appendUserInfoInToRESTrequestParams();

		// Add the SHIB default fence value. NOTE that this comes back with
		// authenticate
		// by IP requests so it *should* already be set to it's default value in
		// the
		// user object
		if (userSession != null && userSession.getUser() != null) {
			this.addRestRequestParameter(RESTRequestParameters.SHIBBOLETH_FENCE,
					userSession.getUser().getUserPreferences().getBoolean(UserPreferences.FENCE_INSTITUTIONAL_SHIB_LOGIN_LINK));
		}

	}

	/**
	 * Special method that should be overridden by derived classes to add any
	 * CARS parameters specific to the current request. E.g., the current
	 * terminate request requires different params for platform code and site
	 * identifier than other CARS requests.
	 *
	 * By default this does nothing...
	 */
	protected void appendContextualParams() {
		return;
	}

	/**
	 * Add common request params
	 *
	 * @param carsrequest
	 * @param request
	 */
	protected void appendRESTRequestCommonParams() {
		// Add IP address, browser type and machine address
		String ipAddr = HttpRequestUtil.getIP(this.httprequest);
		// "138.12.195.211"
		this.addRestRequestParameter(RESTRequestParameters.CLIENT_IP, ipAddr);
		this.addRestRequestParameter(RESTRequestParameters.BROWSER_TYPE, this.httprequest.getHeader("user-agent"));
		this.addRestRequestParameter(RESTRequestParameters.MACHINE_ADDRESS, ipAddr);

		// Add Platform code, Site ID and SSO_DISABLED flag
		this.addRestRequestParameter(RESTRequestParameters.PLATFORM_CODE, ANEServiceConstants.getPlatformCode());
		this.addRestRequestParameter(RESTRequestParameters.SITE_ID, ANEServiceConstants.getSiteIdentifier());
		this.addRestRequestParameter(RESTRequestParameters.SSO_DISABLED, ANEServiceConstants.getDisableSSOAuth());
	}

	/**
	 * Append HTTP-based parameters to CARS request
	 *
	 * @param carsrequest
	 * @param request
	 * @throws ServiceException
	 */
	public void appendRESTRequestParams() throws ServiceException {
		boolean ssoDisabledFlag = Boolean.valueOf(EVProperties.getProperty(EVProperties.DISABLE_SSO_AUTH));

		//
		// Ensure httprequest is present
		//
		if (this.httprequest == null) {
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "HTTP request is required!");
		}

		//
		// Always append common parameters
		//
		appendRESTRequestCommonParams();

		// Add CARS_COOKIE if present
		String carsCookie = BaseCookie.getCookieValue(this.httprequest, "CARS_COOKIE");
		if (!GenericValidator.isBlankOrNull(carsCookie)) {
			this.addRestRequestParameter(RESTRequestParameters.CARS_COOKIE, carsCookie);
		}

		// Add SSO disabled flag
		this.addRestRequestParameter(RESTRequestParameters.SSO_DISABLED, ssoDisabledFlag);

		//
		// Check request type - if this is IP authentication we only need a
		// few parameters
		//
		if (this instanceof CARSRequestIpAuthenticate) {
			this.addRestRequestParameter(RESTRequestParameters.AUTH_TYPE, AuthenticationType.IP.toString());
			if (!ssoDisabledFlag) {
				appendSSORequestParams();
			}
			return;
		}

		//
		// If *NOT* IP authentication, add remaining request parameters
		//
		Map<String, String[]> req_param_map = this.httprequest.getParameterMap();
		String httpMethod = HttpMethod.GET.toString(); // Set default

		// Get the http_method_name from request
		if (isMultiPartRequest()) {

			parseMultiPartFormRequest(req_param_map);

			if (req_param_map != null && req_param_map.size() > 0) {
				String methodName = "http_method_name";
				if (req_param_map.get(methodName) != null && req_param_map.get(methodName).length > 0) {
					httpMethod = req_param_map.get(methodName)[0];
				}
			}
		} else if (null != this.httprequest.getParameter("http_method_name")) {
			httpMethod = this.httprequest.getParameter("http_method_name");
		}
		this.setHTTPMethod(HttpMethod.valueOf(httpMethod));

		// Get the auth_type from the request
		String tempAuthType = null;
		String defaultAuthType = AuthenticationType.IP.toString();
		boolean authTypeAvail = false;

		if (req_param_map != null && req_param_map.size() > 0) {
			if (req_param_map.get("auth_type") != null && req_param_map.get("auth_type").length > 0) {
				tempAuthType = req_param_map.get("auth_type")[0];
				if (null != tempAuthType) {
					authTypeAvail = true;
					this.addRestRequestParameter(RESTRequestParameters.AUTH_TYPE, tempAuthType);
				}
			}

			this.addRestRequestParameter(RESTRequestParameters.REQUEST_PARAMS, req_param_map);
		}

		if (!authTypeAvail) {
			if (!ssoDisabledFlag) {
				appendSSORequestParams();
				authTypeAvail = this.getRestRequestParams().containsKey(RESTRequestParameters.AUTH_TYPE);
			}
		}
		if (!authTypeAvail) {
			this.addRestRequestParameter(RESTRequestParameters.AUTH_TYPE, defaultAuthType);
		}

	}

	/**
	 * Append User information from existing CARS User object
	 *
	 * @param carsrequest
	 * @param webUser
	 * @param request
	 */
	public void appendUserInfoInToRESTrequestParams() {
		if (webUser == null)
			return;

		// Add authtoken when present
		String authtoken = webUser.getAuthToken();

		if (!GenericValidator.isBlankOrNull(authtoken)) {
			this.addRestRequestParameter(RESTRequestParameters.AUTH_TOKEN, authtoken);
		}

		String athens_fence = this.httprequest.getParameter(RESTRequestParameters.ATHENS_FENCE.getReqParam());
		if (null != athens_fence && athens_fence.length() > 0) {
			this.addRestRequestParameter(RESTRequestParameters.ATHENS_FENCE, Boolean.valueOf(Boolean.parseBoolean(athens_fence)));
		} else {
			this.addRestRequestParameter(RESTRequestParameters.ATHENS_FENCE, Boolean.valueOf(webUser.isAthensFenceEnabled()));
		}

		String shibboleth_fence = getShibbolethFenceFromRequest();
		if (null != shibboleth_fence && shibboleth_fence.length() > 0) {
			this.addRestRequestParameter(RESTRequestParameters.SHIBBOLETH_FENCE, Boolean.valueOf(Boolean.parseBoolean(getShibbolethFenceFromRequest())));
		} else {
			this.addRestRequestParameter(RESTRequestParameters.SHIBBOLETH_FENCE, Boolean.valueOf(webUser.isAthensFenceEnabled()));
		}

		String path_choice_exists = this.httprequest.getParameter(RESTRequestParameters.PATH_CHOICE_EXISTS.getReqParam());
		if (null != path_choice_exists) {
			this.addRestRequestParameter(RESTRequestParameters.PATH_CHOICE_EXISTS, Boolean.valueOf(Boolean.parseBoolean(path_choice_exists.trim())));
		} else {
			this.addRestRequestParameter(RESTRequestParameters.PATH_CHOICE_EXISTS, Boolean.valueOf(webUser.isPathChoiceExists()));
		}

		String pathChoice = this.httprequest.getParameter(RESTRequestParameters.PATH_CHOICE.getReqParam());
		String tempAuthType = (String) this.getRestRequestParams().get(RESTRequestParameters.AUTH_TYPE);
		if (null == pathChoice) {
			if (!(null != tempAuthType && tempAuthType.equals(CARSAuthenticationConstants.CANDIDATE_PATH.value()))) {
				this.addRestRequestParameter(RESTRequestParameters.PATH_CHOICE, webUser.getPathChoice());
			}
		} else {
			this.addRestRequestParameter(RESTRequestParameters.PATH_CHOICE, pathChoice);
		}

		String remember_parameter_flag = this.httprequest.getParameter(RESTRequestParameters.REMEMEBER_PATH_FLAG.getReqParam());
		if (null != remember_parameter_flag) {
			this.addRestRequestParameter(RESTRequestParameters.REMEMEBER_PATH_FLAG, Boolean.valueOf(Boolean.parseBoolean(remember_parameter_flag.trim())));
		} else {
			if (!(null != tempAuthType && tempAuthType.equals(CARSAuthenticationConstants.CANDIDATE_PATH.value()))) {
				this.addRestRequestParameter(RESTRequestParameters.REMEMEBER_PATH_FLAG, Boolean.valueOf(webUser.isRememberPathFlagEnabled()));
			}
		}

		if (null != webUser.getAssociationType()) {
			this.addRestRequestParameter(RESTRequestParameters.ASSOCIATION_TYPE, webUser.getAssociationType());
		}

		if (null != webUser.getUserAnonymity()) {
			this.addRestRequestParameter(RESTRequestParameters.ANON_TYPE, webUser.getUserAnonymity());
		}

		if (null != webUser.getAllowedRegType()) {
			this.addRestRequestParameter(RESTRequestParameters.REGISTRATION_TYPE, webUser.getAllowedRegType());
		}

		if (webUser.getCredType() != null) {
			this.addRestRequestParameter(RESTRequestParameters.CRED_TYPE, webUser.getCredType());
		}

		if (null != webUser.getUserAnonymity()
				&& (webUser.getUserAnonymity().equals(CARSAuthenticationConstants.INDIVIDUAL.name()) || webUser.getUserAnonymity().equals(
						CARSAuthenticationConstants.ANON_SHIBBOLETH.name()))) {

			if (null != webUser.getWebUserId()) {
				this.addRestRequestParameter(RESTRequestParameters.PROFILE_ID, webUser.getWebUserId());
			}
		}

		if (null != webUser.getAccount() && StringUtils.isNotBlank(webUser.getAccount().getAccountName())
				&& StringUtils.isNotBlank(webUser.getAccount().getDepartmentName())) {
			this.addRestRequestParameter(RESTRequestParameters.ACCOUNT_NAME, webUser.getAccount().getAccountName());
			this.addRestRequestParameter(RESTRequestParameters.DEPARTMENT_NAME, webUser.getAccount().getDepartmentName());
		} else {
			// there is scenario when the account isn't null but its params are
			// but there are valid params in the http request.
			// so copy them here.
			if (StringUtils.isNotBlank(this.httprequest.getParameter(RESTRequestParameters.ACCOUNT_NAME.getReqParam()))) {
				this.addRestRequestParameter(RESTRequestParameters.ACCOUNT_NAME,
						this.httprequest.getParameter(RESTRequestParameters.ACCOUNT_NAME.getReqParam()));
			}
			if (StringUtils.isNotBlank(this.httprequest.getParameter(RESTRequestParameters.DEPARTMENT_NAME.getReqParam()))) {
				this.addRestRequestParameter(RESTRequestParameters.DEPARTMENT_NAME,
						this.httprequest.getParameter(RESTRequestParameters.DEPARTMENT_NAME.getReqParam()));
			}
		}
	}

	/**
	 * Append SSO-based params from HTTP request
	 *
	 * @param carsReq
	 * @param httpReq
	 * @throws UnsupportedEncodingException
	 */
	protected void appendSSORequestParams() {

		// CARSRequestImpl carsRequest = (CARSRequestImpl) carsReq;
		String decodeSSOBrowserCookie = null;

		if (StringUtils.isNotBlank(getACWvalueFromRequestParameter(httprequest))) {
			String acwParameter = getDecodeACWRequestParameter();
			this.addRestRequestParameter(RESTRequestParameters.SSO_KEY, acwParameter);
			userSession.setBrowserSSOKey(acwParameter);
		} else if (StringUtils.isNotBlank(SSOHelper.getSSOCookieValue(httprequest))) {
			decodeSSOBrowserCookie = SSOHelper.decodeSSOCookieKey(httprequest);
			this.addRestRequestParameter(RESTRequestParameters.SSO_KEY, decodeSSOBrowserCookie);
			userSession.setBrowserSSOKey(decodeSSOBrowserCookie);
		}

		if (SSOHelper.isStateChanged(httprequest, getBrowserSSOKeyFromSession())) {
			addRestRequestParameter(RESTRequestParameters.AUTH_TYPE, CARSAuthenticationConstants.AUTH_TOKEN.value());
		} else if (SSOHelper.isSSOTimeIntervalPassed(getSSOLastAccessTime(userSession))) {

			if (webUser != null && "ANON_IP".equals(webUser.getUserAnonymity())) {
				this.addRestRequestParameter(RESTRequestParameters.AUTH_TYPE, CARSAuthenticationConstants.REVALIDATE_IP.value());
			} else {
				this.addRestRequestParameter(RESTRequestParameters.AUTH_TYPE, CARSAuthenticationConstants.AUTH_TOKEN.value());
			}
		}

	}

	private Long getSSOLastAccessTime(UserSession userSession) {
		return SSOHelper.getSSOLastAccessTime(userSession);
	}

	private String getDecodeACWRequestParameter() {
		try {
			String acwValue = URLEncoder.encode(getACWvalueFromRequestParameter(httprequest), CARSStringConstants.URL_ENCODE.value());
			return URLDecoder.decode(acwValue, CARSStringConstants.URL_ENCODE.value());
		} catch (UnsupportedEncodingException e) {
			log4j.warn("Problem in decoding the SSO Key using " + CARSStringConstants.URL_ENCODE.value() + e.getMessage());
		}
		return "";
	}

	private String getACWvalueFromRequestParameter(HttpServletRequest request) {
		return (String) request.getParameter(CARSStringConstants.ACW.value());
	}

	private String getBrowserSSOKeyFromSession() {
		return userSession.getBrowserSSOKey();
	}

	/**
	 * Convienence method to determine if request is multi-part
	 *
	 * @param request
	 * @return
	 */
	protected boolean isMultiPartRequest() {
		return ServletFileUpload.isMultipartContent(this.httprequest);
	}

	/**
	 * Process multi-part request item as regular form field and file fields.
	 * The name and value of each regular form field will be added to the given
	 * parameterMap and file fields in a seperate map.
	 *
	 * @param request
	 *            The HttpServletRequest Object
	 * @param parameterMap
	 *            The parameterMap to be used for the HttpServletRequest.
	 */
	protected void parseMultiPartFormRequest(Map<String, String[]> parameterMap) throws ServiceException {
		try {

			StripesRequestWrapper stripesreqwrap = (StripesRequestWrapper) this.httprequest;
			Enumeration<String> fileControlNames = stripesreqwrap.getFileParameterNames();

			while (fileControlNames.hasMoreElements()) {
				String fileControl = fileControlNames.nextElement();
				FileBean filebean = stripesreqwrap.getFileParameterValue(fileControl);
				if (filebean != null) {
					Map<String, Object> fileParamMap = new HashMap<String, Object>();
					if (filebean.getFileName().length() <= 0) {
						fileParamMap.put(fileControl, null);
					} else {
						processMultiPartFileFieldRequest(filebean.getInputStream(), fileParamMap, fileControl);
					}
					this.httprequest.setAttribute("fileParams", fileParamMap);
				}
			}
		} catch (Exception e) {
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "Invalid request", e);
		}
	}

	/**
	 * Process multi-part request item for form file upload fields. The name and
	 * value of form file upload fields will be added to the map.
	 *
	 * @param multipartItem
	 *            The multiple parts of all form field name and values
	 * @param fileParamMap
	 *            The fileParamMap to be used for the form file upload name and
	 *            value.
	 */
	protected void processMultiPartFileFieldRequest(InputStream inputStream, Map<String, Object> fileParamMap, String fileControl) throws ServiceException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			byte b[] = new byte[4096];
			while (inputStream.read(b) != -1) {
				outputStream.write(b);
			}
			if (outputStream != null) {
				fileParamMap.put(fileControl, outputStream.toByteArray());
			}
		} catch (IOException ioException) {
			log4j.error("Exception encoutered while parsing the input stream", ioException);
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "Exception encoutered while parsing the input stream");
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				outputStream.close();
			} catch (IOException e) {
				log4j.error("Exception encountered while closing the input stream");
			}
		}
	}

	/**
	 * Process multipart request item as regular form field. The name and value
	 * of each regular form field will be added to the given parameterMap.
	 *
	 * @param formField
	 *            The form field to be processed.
	 * @param parameterMap
	 *            The parameterMap to be used for the HttpServletRequest.
	 */

	protected void processMultiPartFormFieldRequest(String[] formField, Map<String, String[]> parameterMap) {
		String name = formField[0];// formField.getFieldName();
		System.out.println("name" + name);
		String value = formField[1];// formField.getString();
		System.out.println("value" + value);
		String[] values = parameterMap.get(name);
		// System.out.println("values"+values.length);

		/*
		 * if (values == null) { System.out.println("name"+name);
		 * parameterMap.put(name, new String[] { value });
		 * System.out.println("name"+name); } else { String[] newValues = new
		 * String[values.length + 1]; System.arraycopy(values, 0, newValues, 0,
		 * values.length); newValues[values.length] = value;
		 * parameterMap.put(name, newValues);
		 *
		 * }
		 */
	}

	protected String getShibbolethFenceFromRequest() {
		return this.httprequest.getParameter(RESTRequestParameters.SHIBBOLETH_FENCE.getReqParam());
	}

	//
	//
	// Overrides for CARSRequest interface
	//
	//

	@Override
	public HttpMethod getHTTPMethod() {
		return this.httpMethod;
	}

	public void setHTTPMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	@Override
	public String getRequestURI() {
		return this.requestURI;
	}

	@Override
	public boolean isMultiPartReq() {
		return (HttpMethod.POST.equals(httpMethod) || HttpMethod.PUT.equals(httpMethod));
	}

	@Override
	public boolean isEmptyRequest() {
		return (requestParamMap == null || requestParamMap.isEmpty());
	}

	@Override
	public CARSRequestType getRequestType() {
		return this.requestType;
	}

	public void setRequestType(CARSRequestType requestType) {
		this.requestType = requestType;
	}

	@Override
	public boolean isSSOAuthenticationReq() {
		return isSSOAuthenticationReq;
	}

	public void setSSOAuthenticationReq(boolean ssoAuthReq) {
		isSSOAuthenticationReq = ssoAuthReq;
	}

	@Override
	public boolean isSiteIdentifierPresent() {
		if (null == this.requestParamMap)
			return false;
		return this.requestParamMap.containsKey(RESTRequestParameters.SITE_ID) || this.requestParamMap.containsKey(RESTRequestParameters.PLATFORM_SITE);
	}

	@Override
	public boolean isPlatformCodePresent() {
		if (null == this.requestParamMap)
			return false;
		return this.requestParamMap.containsKey(RESTRequestParameters.PLATFORM_CODE) || this.requestParamMap.containsKey(RESTRequestParameters.PLATFORM_SITE);
	}

	@Override
	public String getSessionAffinity() {
		return this.sessionAffinityVal;
	}

	public void setSessionAffinity(String affinity) {
		sessionAffinityVal = affinity;
	}

	@Override
	public Map<RESTRequestParameters, Object> getRestRequestParams() {
		return this.requestParamMap;
	}

	@Override
	public void addRestRequestParameter(RESTRequestParameters paramName, Object paramValue) {
		this.requestParamMap.put(paramName, paramValue);
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void setUserSession(UserSession userSession) {
		this.userSession = userSession;
	}

	// Generally mark NOT to run POST as GET
	@Override
	public boolean isPostAsGet() {
		return false;
	}

}
