package org.ei.service.cars;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.common.model.multipart.BufferedInMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.ei.biz.personalization.EVWebUser;
import org.ei.biz.personalization.IEVWebUser;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.RESTResourceBuilder;
import org.ei.service.cars.Impl.CARSResponse;
import org.ei.service.cars.rest.CARSRequestFactory;
import org.ei.service.cars.rest.request.CARSRequest;
import org.ei.service.cars.rest.util.XMLUtil;
import org.ei.service.cars.util.UserDataProcessor;
import org.ei.session.CARSMetadata;
import org.ei.session.UserSession;
import org.ei.stripes.exception.EVExceptionHandler;
import org.ei.util.StringUtil;
import org.perf4j.log4j.Log4JStopWatch;

public class CARSRequestProcessor {
	private final static Logger log4j = Logger.getLogger(CARSRequestProcessor.class);

	/*
	 * private CARSRequest carsrequest; private HttpServletRequest httprequest;
	 * private UserSession usersession;
	 */

	private String errorLog = "";

	/**
	 * Process an incoming CARS request and formulate a response
	 *
	 * @param carsrequest
	 * @param httprequest
	 * @return
	 * @throws ServiceException
	 * @throws ServiceException
	 * @throws Exception
	 */
	public CARSResponse process(CARSRequest carsrequest, HttpServletRequest httprequest, HttpServletResponse httpresponse, UserSession usersession)
			throws ServiceException {

		Log4JStopWatch stopwatch = new Log4JStopWatch("CARS." + carsrequest.getRequestType());

		// Response object
		CARSResponse carsresponse = null;

		//
		// Validate incoming request
		//
		if (null == carsrequest || carsrequest.isEmptyRequest() || GenericValidator.isBlankOrNull(carsrequest.getRequestURI())) {
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "CARS_INVALID_REQUEST_ERROR");
		}
		if (!carsrequest.isSiteIdentifierPresent()) {
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "CARS_SITEIDENTIFIER_NOT_PRESENT");
		}
		if (!carsrequest.isPlatformCodePresent()) {
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "CARS_PLATFORMCODE_NOT_PRESENT");
		}

		//
		// Create the response
		//
		carsresponse = buildResponse(carsrequest, httprequest);
		stopwatch.stop();

		List<String> mimelist = carsresponse.getMimeList();
		if (mimelist == null || mimelist.size() == 0) {
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "No mime list returned from CARS call!");
		} else if (usersession != null) {
			usersession.setCarsLastSucessAccessTime(Long.valueOf(System.currentTimeMillis()));
		}

		//
		// Iterate over mime list to add more info into CARSResponse. NOTE that
		// this logic assumes the mime list comes in pairs!
		//
		for (int i = 0; i < mimelist.size(); i += 2) {

			// Get 1st mime and see if current request is TERMINATE (only has
			// one mime type returned
			String mime1 = mimelist.get(i);
			if (StringUtils.isBlank(mime1)) {
				log4j.error("1st mime response from CARS is empty!");
				continue;
			}
			if (mimelist.size() == 1) {
				// Add display info
				processDisplayModel(carsresponse, mime1);
				break;
			}

			// All other requests should return pairs of responses
			String mime2 = mimelist.get(i + 1);
			if (StringUtils.isBlank(mime1) || StringUtils.isBlank(mime2)) {
				log4j.error("2nd mime response from CARS is empty!");
				continue;
			}

			//
			// Save the CARS response status
			//
			carsresponse.setResponseStatus(new CARSResponseStatus(mime1));

			// Fetch the user info
			String mimeRespToProcessUserAndDisplayInfo = CARSResponseHelper.fetchUserInfo(Arrays.asList(new String[] { mime1, mime2 }));

			// Process the first mime and get the PageType
			PageType pageType = processDisplayModel(carsresponse, mimeRespToProcessUserAndDisplayInfo);
			if (pageType == null) {
				continue;
				// throw new
				// ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR,
				// "No page type in first mime!");
			}
			//
			// Process mime responses based on page type
			//
			switch (pageType) {
			case LOGIN:
			case GENERIC:
			case POPUP:
				// Path choice?
				carsresponse.setPathChoice(CARSTemplateNames.CARS_PATH_CHOICE.toString().equals(carsresponse.getTemplateName()));

				// If status code is OK, build user data
				buildUserDataFromCarsResponse(usersession, carsresponse, mimeRespToProcessUserAndDisplayInfo);
				// Add display info

				if (PageType.LOGIN == pageType) {
					// Add header content to metadata object
					CARSMetadata carsmetadata = usersession.getCarsMetaData();
					if (carsmetadata == null) {
						carsmetadata = new CARSMetadata();
					}
					carsmetadata.setHeaderContent(CARSResponseHelper.transformResponse(carsresponse, mime2, usersession.getUser()));
					usersession.setCarsMetaData(carsmetadata);
				} else {
					carsresponse.setPageContent(CARSResponseHelper.transformResponse(carsresponse, mime2, usersession.getUser()));
					carsresponse.setPageType(pageType);
				}

				break;
			case EMAIL:
				// Parse email info into CARSResponse object and send
				String emailContent = CARSResponseHelper.transformResponse(carsresponse, mime2, usersession.getUser());
				String userEmail = XMLUtil.fetchXPathValAsString(mime2, XPathEnum.MAIL_RECIPIENT.value());
				String emailSubject = XMLUtil.fetchXPathValAsString(mime2, XPathEnum.MAIL_SUBJECT.value());
				CARSResponseHelper.sendConfirmationMail(carsresponse, userEmail, emailSubject, emailContent);

				break;
			case REDIRECT:
				// Set possible redirect URLs
				// Special case for TICURL - it will return CARS_PATH_CHOICE in
				// mimes 1/2 and THEN a redirect
				// in mimes 3/4 when two or more accounts use the same TICURL.
				if (!carsresponse.isPathChoice()) {
					carsresponse.setPageType(pageType);
				}
				carsresponse.setRedirectURL(XMLUtil.fetchXPathValAsString(mime2, XPathEnum.REDIRECT_URL.value()));
				carsresponse.setShibbolethURL(XMLUtil.fetchXPathValAsString(mime2, XPathEnum.SHIBBOLETH_URL.value()));

				break;
			default:
				log4j.warn("Unknown page type!");
				break;
			}

			// forget user name-password link on home page CARs response has
			// next_call url where below logic applies
			String nextRequestUri = carsresponse.getNextRequestURI();
			if (StringUtils.isNotBlank(nextRequestUri)) {
				CARSRequest nextCARSRequest = CARSRequestFactory.buildCARSRequest(httprequest, nextRequestUri, usersession);
				nextCARSRequest.addRestRequestParameter(RESTRequestParameters.DYNAMIC_REQUEST_URI, nextRequestUri);
				CARSRequestProcessor carsrequestprocessor = new CARSRequestProcessor();
				carsresponse = carsrequestprocessor.process(nextCARSRequest, httprequest, httpresponse, usersession);
			}

		}

		stopwatch.stop();
		return carsresponse;
	}

	/**
	 * Build user data from the mime response from CARS call. Update the
	 * UserSession with resulting info.
	 *
	 * @param userSession
	 * @param carsresponse
	 * @param mimeResp
	 * @throws ServiceException
	 */
	private void buildUserDataFromCarsResponse(UserSession userSession, CARSResponse carsresponse, String mimeResp) throws ServiceException {
		//
		// Create new User if empty
		//
		IEVWebUser currentuser = userSession.getUser();
		if (currentuser == null) {
			currentuser = new EVWebUser();
		}

		if (StringUtils.isNotBlank(carsresponse.getSessionAffinity())) {
			currentuser.setCarsJSessionId(carsresponse.getSessionAffinity());
		}
		//
		// Update the current user
		//
		UserDataProcessor.processUserData((EVWebUser) currentuser, mimeResp, getSSOKeyFromSession(userSession));

		// update SSO cookie in session if the CARS response cookie does not
		// match with session cookie
		if (StringUtils.isNotBlank(getSSOKeyFromSession(userSession))) {
			if (StringUtils.isNotBlank(currentuser.getSsoKey()) && !getSSOKeyFromSession(userSession).equals(currentuser.getSsoKey())) {
				userSession.setBrowserSSOKey(currentuser.getSsoKey());
			}
		} else {
			userSession.setBrowserSSOKey(currentuser.getSsoKey());
		}

		userSession.setUser(currentuser);
	}

	private String getSSOKeyFromSession(UserSession userSession) {
		return userSession.getBrowserSSOKey();
	}

	/**
	 * Builds the CARSResponse object.
	 *
	 * @param resource
	 *            The REST Resource object to connect with
	 * @param carsRequest
	 *            The CARS request object - assumed to be already validated!
	 * @return CARS response object
	 * @throws ServiceException
	 * @throws Exception
	 */
	private CARSResponse buildResponse(CARSRequest carsRequest, HttpServletRequest httprequest) throws ServiceException {

		CARSResponse carsresponse = new CARSResponse();
		BufferedInMultiPart bufInMultiPart = null;
		List<String> respList = null;

		errorLog = "";

		//
		// Build Resource object for REST call
		//
		Resource resource = RESTResourceBuilder.build(carsRequest, httprequest, this);
		if (resource == null) {
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "Unable to build REST client Resource object!");
		}

		HttpClient httpClient = new HttpClient();
		InputStream respStream = null;
		try {
			//
			// Make the REST call based on the HTTP method
			//
			switch (carsRequest.getHTTPMethod()) {
			case POST:
				errorLog = "";
				PostMethod postMethod = RESTResourceBuilder.buildPostMethod(carsRequest, httprequest, this);
				postMethod.setRequestHeader(CARSStringConstants.ACCEPT_ENCODING.value(), CARSConstants.GZIP);
				Part[] partsForPostMethod = RESTResourceBuilder.fetchParts(carsRequest, httprequest);
				postMethod.setRequestEntity(new MultipartRequestEntity(partsForPostMethod, postMethod.getParams()));
				errorLog = errorLog + "Header 2: key=" + CARSStringConstants.ACCEPT_ENCODING.value() + ", value=" + CARSConstants.GZIP + "\n";
				httpClient.executeMethod(postMethod);

				if (postMethod != null && postMethod.getResponseHeader("Content-Encoding").getValue().indexOf("gzip") != -1) {
					respStream = new GZIPInputStream(postMethod.getResponseBodyAsStream());
				} else {
					respStream = postMethod.getResponseBodyAsStream();
				}
				respList = fetchMIMERespList(respStream);

				break;
			case PUT:
				// Currently the change password request can NOT handle
				// multipart PUT request. Sigh.
				if (carsRequest.getRequestURI().contains(
						CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_BASE_URI)
								+ CARSConfigVariables.getConstantAsString(CARSConfigVariables.CHANGE_PASSWORD_URI))) {
					resource.accept(CARSStringConstants.MULTIPART_MIXED.value());
					ClientResponse clientresponse = resource.put(null);
					if (clientresponse == null) {
						logCarsErrors(errorLog, "Empty ClientResponse from CARS!", null, carsRequest, httprequest);
						throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "Empty ClientResponse from CARS!");
					} else if (clientresponse.getStatusCode() != 200) {
						logCarsErrors(errorLog,"ClientResponse status code: " + clientresponse.getStatusCode() + ", message: "
								+ clientresponse.getMessage(),null,carsRequest,httprequest);
						throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "ClientResponse status code: " + clientresponse.getStatusCode() + ", message: "
								+ clientresponse.getMessage());
					}
					bufInMultiPart = clientresponse.getEntity(BufferedInMultiPart.class);
					if (null != bufInMultiPart) {
						respList = fetchMIMERespList(bufInMultiPart.getParts());
					}
				} else {
					errorLog = "";
					PutMethod putMethod = RESTResourceBuilder.buildPutMethod(carsRequest, httprequest, this);
					putMethod.setRequestHeader(CARSStringConstants.ACCEPT_ENCODING.value(), CARSConstants.GZIP);
					Part[] partsForPutMethod = RESTResourceBuilder.fetchParts(carsRequest, httprequest);
					putMethod.setRequestEntity(new MultipartRequestEntity(partsForPutMethod, putMethod.getParams()));
					errorLog = errorLog + "Header 2: key=" + CARSStringConstants.ACCEPT_ENCODING.value() + ", value=" + CARSConstants.GZIP + "\n";
					httpClient.executeMethod(putMethod);

					if (putMethod != null && putMethod.getResponseHeader("Content-Encoding").getValue().indexOf("gzip") != -1) {
						respStream = new GZIPInputStream(putMethod.getResponseBodyAsStream());
					} else {
						respStream = putMethod.getResponseBodyAsStream();
					}
					respList = fetchMIMERespList(respStream);
				}

				break;
			case DELETE:
				resource.accept(CARSStringConstants.APPLICATION_XML.value());
				String deleteresponse = resource.delete(String.class);
				respList = new ArrayList<String>(1);
				respList.add(deleteresponse);
				break;
			case GET:
			default:
				resource.accept(CARSStringConstants.MULTIPART_MIXED.value());
				ClientResponse clientresponse = resource.get();
				if (clientresponse == null) {
					logCarsErrors(errorLog, "Empty ClientResponse from CARS!", null, carsRequest, httprequest);
					throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "Empty ClientResponse from CARS!");
				} else if (clientresponse.getStatusCode() != 200) {
					logCarsErrors(errorLog,"ClientResponse status code: " + clientresponse.getStatusCode() + ", message: "
							+ clientresponse.getMessage(),null,carsRequest,httprequest);
					throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "ClientResponse status code: " + clientresponse.getStatusCode()
							+ ", message: " + clientresponse.getMessage());
				}
				bufInMultiPart = clientresponse.getEntity(BufferedInMultiPart.class);
				if (null != bufInMultiPart) {
					respList = fetchMIMERespList(bufInMultiPart.getParts());
				}

				// Update the session affinity value
				String buildSessionAffinity = buildSessionAffinity(clientresponse);
				if (StringUtils.isNotBlank(buildSessionAffinity)) {
					carsresponse.setSessionAffinity(buildSessionAffinity);
				}
				break;
			}

		} catch (Exception e) {
			logCarsErrors(errorLog,"Unable to build Response!  Exception: " + e.getClass() + ", message: " + e.getMessage(),respList,carsRequest,httprequest);
			EVExceptionHandler.logException("Unable to build Response!  Exception: " + e.getClass() + ", message: " + e.getMessage(), e, null, log4j);
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, e);
		} finally {
			if (respStream != null) {
				try {
					respStream.close();
				} catch (Throwable t) {
				}
			}
		}

		// Add to response
		carsresponse.setMimeList(respList);

		return carsresponse;
	}

	/**
	 * Add display info to response
	 *
	 * @param carsresponse
	 * @return
	 * @throws ServiceException
	 */
	public PageType processDisplayModel(CARSResponse carsresponse, String mimeResp) throws ServiceException {
		String pageTitle = null;
		String platformName = null;

		if (GenericValidator.isBlankOrNull(mimeResp)) {
			throw new IllegalArgumentException("No user info in CARS response object!");
		}

		carsresponse.setTemplateName(XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.TEMPLATE_NS2.value()));
		platformName = CARSConfigVariables.getConstantAsString(CARSConfigVariables.APP_PRODUCT_NAME);
		pageTitle = XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.PAGE_TITLE.value());

		if (StringUtils.isNotBlank(pageTitle)) {
			if (null != platformName && !"".equals(platformName.trim())) {
				pageTitle = platformName + " - " + pageTitle;
			}
		}
		carsresponse.setPageTitle(pageTitle);
		carsresponse.setTemplateUpdateDate(XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.TEMPLATE_UPDATE_DATE.value()));
		carsresponse.setFlowComplete(XMLUtil.fetchXPathValAsBoolean(mimeResp, XPathEnum.FLOW_COMPLETE_TOKEN.value()));
		carsresponse.setCarsCookie(XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.CARS_COOKIE.value()));
		carsresponse.setNextRequestURI(XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.NEXT_REQUEST_URI.value()));
		carsresponse.setForce(XMLUtil.fetchXPathValAsBoolean(mimeResp, XPathEnum.FORCE_TOKEN.value()));
		carsresponse.setProductId(XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.PRODUCT_ID.value()));

		// Get the page type from mime 1
		PageType pagetype = null;
		String pagetypestr = XMLUtil.fetchXPathValAsString(mimeResp, XPathEnum.PAGE_TYPE.value());
		try {
			if (StringUtils.isNotBlank(pagetypestr)) {
				pagetype = PageType.valueOf(pagetypestr);
			}
		} catch (Exception e) {
			log4j.error("No page type found for: " + pagetypestr);
		}
		return pagetype;
	}

	/**
	 * Build MIME list from InPart List
	 *
	 * @param parts
	 * @return
	 */
	private List<String> fetchMIMERespList(List<InPart> parts) {
		List<String> mimeRespList = null;
		if (null != parts && parts.size() > 0) {
			mimeRespList = new ArrayList<String>(parts.size());
			for (InPart inPart : parts) {
				if (null != inPart) {
					mimeRespList.add(StringUtil.convertStreamToString(inPart.getInputStream()));
				}
			}
		}
		return mimeRespList;
	}

	/**
	 * Build MIME list from InputStream.
	 *
	 * @param respStream
	 * @return
	 */
	private List<String> fetchMIMERespList(InputStream respStream) {
		String respAsString = null;
		List<String> respList = null;
		if (null != respStream) {
			respAsString = StringUtil.convertStreamToString(respStream);

			if (null != respAsString) {
				String[] respStrArr = respAsString.split(CARSStringConstants.MIME_SEPARATOR.value());
				if (null != respStrArr && respStrArr.length > 0) {
					respList = new ArrayList<String>(respStrArr.length);
					for (String mimeResponse : respStrArr) {
						if (null != mimeResponse) {
							int index = mimeResponse.indexOf(CARSStringConstants.LESS_THAN.value());
							if (index != -1) {
								mimeResponse = mimeResponse.substring(index, mimeResponse.length());
								respList.add(mimeResponse);
							}
						}
					}
				}
			}
		}
		return respList;
	}

	/**
	 * This method is used for fetching the CARS UserInfo MIME response to
	 * create/update the User(WebUser) object if available otherwise fetches the
	 * first MIME response
	 *
	 * @param mimeList
	 *            - the list of MIME responses
	 * @return String - the MIME response containing UserInfo
	 * @throws ServiceException
	 * @throws ServiceException
	 */
	private String fetchSingleValue(String xpathexp, List<String> mimeList) throws ServiceException {
		String value = null;

		if (mimeList == null || mimeList.size() == 0)
			return null;

		for (int i = 0; i < mimeList.size(); i++) {
			if (GenericValidator.isBlankOrNull(mimeList.get(i)))
				continue;
			String responseMime = mimeList.get(i);
			value = XMLUtil.fetchXPathValAsString(responseMime, xpathexp);
			if (StringUtils.isNotBlank(value)) {
				return value;
			}
		}
		return value;
	}

	/**
	 * Fetch Part object from incoming Multipart request.
	 *
	 * @param carsrequest
	 * @param request
	 * @return Array of Part objects or null
	 */
	@SuppressWarnings("unchecked")
	public static Part[] fetchParts(CARSRequest carsrequest, HttpServletRequest request) {
		Part[] parts = null;
		if (null != carsrequest.getRestRequestParams()) {
			List<Part> partsList = new ArrayList<Part>();
			// add all the file upload content from the request
			if (request.getAttribute(CARSStringConstants.FILE_PARAMS.value()) != null) {
				Part part;
				Map<String, Object> fileParamsMap = (Map<String, Object>) request.getAttribute(CARSStringConstants.FILE_PARAMS.value());
				for (Entry<String, Object> params : fileParamsMap.entrySet()) {
					String key = params.getKey();
					if (key.equals(CARSStringConstants.PROFILE_IMAGE.value())) {
						key = CARSStringConstants.PROFILE_PHOTO.value();
					}
					if (params.getValue() != null) {
						part = new FilePart(key, new ByteArrayPartSource(key, (byte[]) params.getValue()));
						partsList.add(part);
					}
				}
				parts = partsList.toArray(new Part[partsList.size()]);
			}
		}
		return parts;
	}

	/**
	 * This method is used for setting the JSESSIONID in session so that it wil
	 * be used in subsequent CARS and SOAP calls
	 *
	 * @param clientResponse
	 *            the REST response
	 */
	private static String buildSessionAffinity(ClientResponse clientresponse) {
		MultivaluedMap<String, String> headerMap = clientresponse.getHeaders();
		String sessionAffinity = null;
		String cookieVal = null;

		if (null != headerMap) {
			List<String> cookieValues = headerMap.get(CARSStringConstants.SET_COOKIE.value());
			if (null != cookieValues && cookieValues.size() > 0) {
				cookieVal = cookieValues.get(0);
				if (null != cookieVal && !"".equals(cookieVal)) {
					sessionAffinity = getJSessionId(cookieVal);
				}
			}
		}

		return sessionAffinity;
	}

	private static String getJSessionId(String input) {
		String jSessionId = null;
		String[] keyValPair = input.split(CARSStringConstants.SEMICOLON.value());

		for (String strVal : keyValPair) {
			if (StringUtils.isNotBlank(strVal)) {
				if (strVal.contains(CARSStringConstants.SESSION_AFFINITY_KEY.value())) {
					String[] strArray = strVal.split(CARSStringConstants.EQUAL.value());
					if (null != strArray && strArray.length > 0) {
						jSessionId = strArray[1];
						break;
					}
				}
			}
		}
		return jSessionId;
	}

	private void logCarsErrors(String resourceStr, String errorMessage, List<String> respList, CARSRequest carsRequest, HttpServletRequest httprequest) {

		StringBuffer errorlog = new StringBuffer();
		errorlog.append("\n***************************************************CARS ERROR LOG START***********************************************************\n");
		errorlog.append("Exception Message : " + errorMessage + "\n");
		errorlog.append("Request details :" + resourceStr + "\n");

		if (carsRequest.getHTTPMethod().equals(HttpMethod.POST) || carsRequest.getHTTPMethod().equals(HttpMethod.PUT)) {
			if (null != carsRequest.getRestRequestParams()) {
				for (Entry<RESTRequestParameters, Object> pair : carsRequest.getRestRequestParams().entrySet()) {
					if (null != pair && null != pair.getKey()) {
						if (CARSStringConstants.REQUEST_PARAMS_HOLDER.value().equalsIgnoreCase(pair.getKey().getReqParam())) {
							for (Entry<Object, Object> paramValue : ((Map<Object, Object>) pair.getValue()).entrySet()) {
								String[] arrayValue = (String[]) paramValue.getValue();
								for (String value : arrayValue) {
									errorlog.append("Parts : key=" + paramValue.getKey().toString() + ", value=" + value + "\n");
								}
							}
						}
						if (pair.getValue() != null) {
							errorlog.append("Parts : key=" + pair.getKey().getReqParam() + ", value=" + pair.getValue().toString() + "\n");

						}
					}
				}
				// add all the file upload content from the request
				if (httprequest.getAttribute(CARSStringConstants.FILE_PARAMS.value()) != null) {
					Map<String, Object> fileParamsMap = (Map<String, Object>) httprequest.getAttribute(CARSStringConstants.FILE_PARAMS.value());
					for (Entry<String, Object> params : fileParamsMap.entrySet()) {
						String key = params.getKey();
						if (key.equals(CARSStringConstants.PROFILE_IMAGE.value())) {
							key = CARSStringConstants.PROFILE_PHOTO.value();
						}
						if (params.getValue() != null) {
							errorlog.append("Parts : key=" + key + ", value=" + params.getValue() + "\n");
						}
					}
				}
			}
		}

		if (respList == null) {
			errorlog.append("Response List :" + respList + "\n");
		} else if (respList.size() == 0) {
			errorlog.append("Response List size:" + respList.size() + "\n");
		} else {
			errorlog.append("Response List :" + "\n");
			for (String resp : respList) {
				errorlog.append(resp + "\n");
			}
		}

		errorlog.append("***************************************************CARS ERROR LOG END***********************************************************");
		try {
			Logger carsErrorLog4j = Logger.getLogger("CarsErrorLogger");
			carsErrorLog4j.info(errorlog.toString());
		} catch (Exception e) {
			log4j.warn("Writing cars error into carslogger is failed, writing the error in root logger.");
			log4j.warn(errorlog.toString());
		}
		errorlog = null;
	}

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

}
