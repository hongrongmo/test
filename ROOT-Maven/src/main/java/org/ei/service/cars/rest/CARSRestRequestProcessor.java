package org.ei.service.cars.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.RestConstants;
import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.apache.wink.common.model.multipart.BufferedInMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.ei.exception.ServiceException;
import org.ei.exception.SystemErrorCodes;
import org.ei.service.BaseServiceConstants;
import org.ei.service.cars.CARSConfigVariables;
import org.ei.service.cars.CARSStringConstants;
import org.ei.service.cars.HttpMethod;
import org.ei.service.cars.RESTRequestParameters;
import org.ei.service.cars.rest.request.CARSRequest;

public final class CARSRestRequestProcessor {
	private static RestClient restClient = null;
	private final static Logger log4j = Logger.getLogger(CARSRestRequestProcessor.class);

	static {
		ClientConfig config = new ClientConfig();
		int serviceTimeout = CARSConfigVariables.getConstantAsInt(CARSConfigVariables.CARS_SERVICE_TIMEOUT);
		config.acceptHeaderAutoSet(true);
		config.connectTimeout(serviceTimeout);
		config.followRedirects(true);
		config.readTimeout(serviceTimeout);
		restClient = new RestClient(config);
	}

	private CARSRestRequestProcessor() {
	}

	public static List<String> invoke(CARSRequest carsRequest, HttpServletRequest request) throws ServiceException {
		StringBuilder strBuilder = new StringBuilder();
		List<String> mimeRespList = null;

		try {

			strBuilder.append(BaseServiceConstants.getServicesBaseURL() + CARSConfigVariables.getConstantAsString(CARSConfigVariables.CARS_END_POINT));
			strBuilder.append(carsRequest.getRequestURI());

			if (carsRequest.isMultiPartReq()) {
				mimeRespList = invokeMultiPartRequest(strBuilder.toString(), carsRequest, request);
			} else {
				mimeRespList = invokeCommonRequest(strBuilder.toString(), carsRequest);
			}

		} catch (ClientWebException exp) {
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "CARS_SERVICE_FAILURE_ERROR", exp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new ServiceException(SystemErrorCodes.CARS_RESPONSE_PROCESSING_ERROR, "CLIENT_COMMON_ERROR", exp);
		}
		return mimeRespList;
	}

	/**
	 * This method is used for invoking common rest call and returns list of
	 * mime response
	 *
	 * @param carsURI
	 *            the cars target uri to be invoked
	 * @param carsRequest
	 *            the request object with all required param values
	 * @return List<String> the list of mime response as string
	 * @throws Exception
	 */
	public static List<String> invokeCommonRequest(String carsURI, CARSRequest carsRequest) throws Exception {
		ClientResponse clientResponse = null;
		BufferedInMultiPart bufInMultiPart = null;
		List<String> respList = null;

		Resource resource = restClient.resource(carsURI);

		if (null != carsRequest.getRestRequestParams()) {
			for (Entry<RESTRequestParameters, Object> pair : carsRequest.getRestRequestParams().entrySet()) {
				if (null != pair && null != pair.getKey()) {
					if (null != pair.getKey().getReqParam()) {
						if (CARSStringConstants.REQUEST_PARAMS_HOLDER.value().equalsIgnoreCase(pair.getKey().getReqParam())) {
							resource.queryParams(convertToMultiValuedMap(pair));
						} else {

							if (null != pair.getValue()) {
								String value = null;
								try {
									value = URLEncoder.encode(pair.getValue().toString(), "UTF-8");
								} catch (UnsupportedEncodingException e) {
									log4j.error("Error occured while encoding" + pair.getKey().getReqParam());
								}
								resource.queryParam(pair.getKey().getReqParam(), value);
							}
						}
					}
				}
			}
		}

		resource.header(CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION),
				CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE));

		String sessionAffinity = carsRequest.getSessionAffinity();
		if (null != sessionAffinity) {
			resource.cookie(CARSStringConstants.SESSION_AFFINITY_KEY.value() + CARSStringConstants.EQUAL.value() + sessionAffinity
					+ CARSStringConstants.SEMICOLON.value());
			resource.header(CARSStringConstants.SESSION_AFFINITY_KEY.value(), sessionAffinity);
		}

		String deleteResponse = null;
		switch (carsRequest.getHTTPMethod()) {
		case POST:
			resource.accept(CARSStringConstants.MULTIPART_MIXED.value());
			bufInMultiPart = resource.post(BufferedInMultiPart.class, RestConstants.CHARACTER_ENCODING_UTF_8);
			break;
		case PUT:
			resource.contentType(CARSStringConstants.APPLICATION_CONTENT_TYPE.value());
			resource.accept(CARSStringConstants.MULTIPART_MIXED.value());
			bufInMultiPart = resource.put(BufferedInMultiPart.class, RestConstants.CHARACTER_ENCODING_UTF_8);
			break;
		case DELETE:
			resource.accept(CARSStringConstants.APPLICATION_XML.value());
			deleteResponse = resource.delete(String.class);
			respList = new ArrayList<String>(1);
			respList.add(deleteResponse);
			break;
		case GET:
		default:
			resource.accept(CARSStringConstants.MULTIPART_MIXED.value());
			clientResponse = resource.get();
			// setSessionAffinityCookie(clientResponse);
			break;
		}
		try {
			if (null != clientResponse) {
				bufInMultiPart = clientResponse.getEntity(BufferedInMultiPart.class);
				setSessionAffinityCookie(clientResponse);
			}

			if (null != bufInMultiPart) {
				respList = fetchMIMERespList(bufInMultiPart.getParts());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("", e);
		}
		return respList;
	}

	private static List<String> fetchMIMERespList(List<InPart> parts) {
		List<String> mimeRespList = null;
		if (null != parts && parts.size() > 0) {
			mimeRespList = new ArrayList<String>(parts.size());
			for (InPart inPart : parts) {
				if (null != inPart) {
					mimeRespList.add(convertStreamToString(inPart.getInputStream()));
				}
			}
		}
		return mimeRespList;
	}

	/**
	 * This method is used for invoking rest when the http method is POST/PUT
	 * and the request uri is for registration
	 *
	 * @param carsURI
	 *            - the cars request uri
	 * @param carsRequest
	 *            - the request object from web
	 * @return List - the mime response lists
	 * @throws HttpException
	 *             - if any http exception occurs
	 * @throws IOException
	 *             - if any input/output exception occurs
	 */
	@SuppressWarnings("unchecked")
	public static List<String> invokeMultiPartRequest(String carsURI, CARSRequest carsRequest, HttpServletRequest request) throws HttpException, IOException {
		InputStream respStream = null;
		List<Part> partsList = new ArrayList<Part>();
		List<String> respList = null;
		Part[] parts = null;
		PostMethod postMethod = null;
		PutMethod putMethod = null;
		HttpClient httpClient = new HttpClient();

		if (null != carsRequest.getRestRequestParams()) {

			for (Entry<RESTRequestParameters, Object> pair : carsRequest.getRestRequestParams().entrySet()) {

				if (null != pair && null != pair.getKey()) {

					if (CARSStringConstants.REQUEST_PARAMS_HOLDER.value().equalsIgnoreCase(pair.getKey().getReqParam())) {
						for (Entry<Object, Object> paramValue : ((Map<Object, Object>) pair.getValue()).entrySet()) {
							String[] arrayValue = (String[]) paramValue.getValue();
							for (String value : arrayValue) {
								partsList.add(new StringPart(paramValue.getKey().toString(), value));
							}
						}
					}

					if (pair.getValue() != null) {
						partsList.add(new StringPart(pair.getKey().getReqParam(), pair.getValue().toString()));
					}
				}
			}

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
			}
			parts = partsList.toArray(new Part[partsList.size()]);
		}

		if (null != carsURI && null != parts && parts.length > 0) {
			if (HttpMethod.POST.equals(carsRequest.getHTTPMethod())) {
				postMethod = new PostMethod(carsURI);
				postMethod.setRequestHeader(CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION),
						CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE));

				postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
				httpClient.executeMethod(postMethod);
				respStream = postMethod.getResponseBodyAsStream();

			} else if (HttpMethod.PUT.equals(carsRequest.getHTTPMethod())) {
				putMethod = new PutMethod(carsURI);
				putMethod.setRequestHeader(CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION),
						CARSConfigVariables.getConstantAsString(CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE));

				putMethod.setRequestEntity(new MultipartRequestEntity(parts, putMethod.getParams()));
				httpClient.executeMethod(putMethod);
				respStream = putMethod.getResponseBodyAsStream();
			}
		}
		respList = fetchMIMERespList(respStream);

		return respList;
	}

	private static List<String> fetchMIMERespList(InputStream respStream) {
		String respAsString = null;
		List<String> respList = null;
		if (null != respStream) {
			respAsString = convertStreamToString(respStream);

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
	 * This method is used for setting the JSESSIONID in session so that it wil
	 * be used in subsequent CARS and SOAP calls
	 *
	 * @param clientResponse
	 *            the REST response
	 */
	private static void setSessionAffinityCookie(ClientResponse clientResponse) {
		MultivaluedMap<String, String> headerMap = clientResponse.getHeaders();
		String sessionAffinityKey = null;
		String cookieVal = null;

		if (null != headerMap) {
			List<String> cookieValues = headerMap.get(CARSStringConstants.SET_COOKIE.value());
			if (null != cookieValues && cookieValues.size() > 0) {
				cookieVal = cookieValues.get(0);
				if (null != cookieVal && !"".equals(cookieVal)) {
					sessionAffinityKey = getJSessionId(cookieVal);
				}
			}
			/*
			 * if (null != sessionAffinityKey) {
			 * //log4j.info("JSESSIONID is not available : "+
			 * sessionAffinityKey);
			 * setValueInSession(CARSStringConstants.SESSION_AFFINITY_KEY
			 * .value(),sessionAffinityKey); } else {
			 * log4j.info("JSESSIONID is not available"); }
			 */}
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

	private static String convertStreamToString(InputStream inputStream) {
		String xmlAsStr = "";

		if (null != inputStream) {
			StringBuilder strBuilder = new StringBuilder();
			InputStreamReader isr = null;
			try {
				isr = new InputStreamReader(inputStream, RestConstants.CHARACTER_ENCODING_UTF_8);
				BufferedReader bufReader = new BufferedReader(isr);
				int ch = -1;
				while ((ch = bufReader.read()) > -1) {
					strBuilder.append((char) ch);
				}
			} catch (IOException ioExp) {
				log4j.error("CARS_STREAM_PARSE_ERROR", ioExp);
			} finally {
				try {
					if (null != isr)
						isr.close();
					if (null != inputStream)
						inputStream.close();
				} catch (IOException ioExp) {
					log4j.error("CARS_STREAM_PARSE_ERROR", ioExp);
				}
			}
			xmlAsStr = strBuilder.toString();
		}
		return xmlAsStr;
	}

	@SuppressWarnings("unchecked")
	private static MultivaluedMap<String, String> convertToMultiValuedMap(Entry<RESTRequestParameters, Object> pair) {
		MultivaluedMap<String, String> map = new MultivaluedMapImpl<String, String>();

		if (null != pair.getValue()) {
			for (Entry<Object, Object> paramValue : ((Map<Object, Object>) pair.getValue()).entrySet()) {
				if (null != paramValue && null != paramValue.getValue()) {
					String[] arrayValue = (String[]) paramValue.getValue();
					if (null != arrayValue && arrayValue.length > 0) {
						for (String value : arrayValue) {
							if (null != paramValue.getKey()) {
								try {
									value = URLEncoder.encode(value, "UTF-8");
								} catch (UnsupportedEncodingException e) {
									log4j.error("Error occured while encoding" + paramValue.getKey().toString());
								}
								map.add(paramValue.getKey().toString(), value);
							}
						}
					}
				}
			}
		}
		return map;
	}

	private String getShibbolethFenceFromRequest(HttpServletRequest httpReq) {
		return httpReq.getParameter(RESTRequestParameters.SHIBBOLETH_FENCE.getReqParam());
	}

}
