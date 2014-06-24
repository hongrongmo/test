/*package com.elsevier.els.app.cars.rest;

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
import java.util.zip.GZIPInputStream;

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
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.internal.handlers.GzipHandler;
import org.apache.wink.common.RestConstants;
import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.apache.wink.common.model.multipart.BufferedInMultiPart;
import org.apache.wink.common.model.multipart.InPart;

import com.elsevier.edit.common.logging.extendedlogger.CommonLogger;
import com.elsevier.els.app.biz.utils.GenericValidator;
import com.elsevier.els.app.cars.ICARSRequest;
import com.elsevier.els.app.cars.constants.CARSConfigVariables;
import com.elsevier.els.app.cars.constants.CARSRequestURL;
import com.elsevier.els.app.cars.constants.CARSStringConstants;
import com.elsevier.els.app.cars.constants.HttpMethod;
import com.elsevier.els.app.cars.constants.RESTRequestParameters;
import com.elsevier.els.app.cars.exception.CARSErrorEventID;
import com.elsevier.els.app.cars.exception.CARSServiceException;
import com.elsevier.els.app.cars.util.CARSCommonUtil;
import com.elsevier.els.app.cars.util.MessageResourceUtil;
//import org.apache.http.impl.client.ContentEncodingHttpClient;

*//**
 * This is the main class which is used for invoking CARS
 * through RESTFul services call and fetching the list of MIME responses.
 *
 * @author naikn1
 * @version 1.0
 *
 *//*
public final class CARSRestRequestProcessor {
	*//**  static reference for rest client *//*
	private static RestClient restClient = null;

	*//** *//*
	private static final String GZIP = "gzip,deflate";
	*//**
	 * static block to intialize rest client
	 *//*
	static {
		ClientConfig config = new ClientConfig();
		int serviceTimeout = CARSConfigVariables
				.getConstantAsInt(CARSConfigVariables.CARS_SERVICE_TIMEOUT);
		config.acceptHeaderAutoSet(true);
		config.connectTimeout(serviceTimeout);
		config.followRedirects(true);
		config.readTimeout(serviceTimeout);
		config.handlers(new GzipHandler());
		restClient = new RestClient(config);
	}

	*//** Avoid to create instance *//*
	private CARSRestRequestProcessor() {	}


	*//**
	 * This method is used for invoking the RESTFul service call and
	 * getting the LIST of MIME response and String
	 *
	 * @param carsRequest the request object holding required param values
	 * @return List of MIME response
	 * @throws CARSServiceException if any service fails during REST call
	 *//*
	public static List<String> invoke(ICARSRequest carsRequest,HttpServletRequest request)
	throws CARSServiceException {
		StringBuilder strBuilder = new StringBuilder(128);
		List<String> mimeRespList = null;
		String cpmClassName = CARSStringConstants.CPM_REST_CLASS.value();
		String cpmMethodName = CARSStringConstants.CPM_REST_METHOD.value();
		String cpmIdName = CARSStringConstants.CPM_REST_IDENTIFIER.value();
		try {
			//start CPM Timer for rest call
			CommonLogger.startTimer(cpmClassName, cpmMethodName, cpmIdName);

			strBuilder.append(CARSConfigVariables
					.getConstantAsString(CARSConfigVariables.CARS_END_POINT));
			strBuilder.append(carsRequest.getRequestURI());

			boolean isMultiPartReq = isMultiPartReq(carsRequest);
			if (isMultiPartReq) {//invoke multipart request
				mimeRespList = invokeMultiPartRequest(
						strBuilder.toString(), carsRequest,request);
			} else {//invok common request
				mimeRespList = invokeCommonRequest(
						strBuilder.toString(),carsRequest, request);
			}
		} catch (ClientWebException exp) {
			throw new CARSServiceException(
					CARSErrorEventID.CARS_SERVICE_FAILURE_ERROR,
					CARSStringConstants.CLIENT_WEB_ERROR.value(), exp);
		} catch (Exception exp) {
			throw new CARSServiceException(
					CARSErrorEventID.CARS_SERVICE_FAILURE_ERROR,
					CARSStringConstants.CLIENT_COMMON_ERROR.value(), exp);
		} finally {
			//stop CPM Timer for rest call
			CommonLogger.stopTimer(cpmClassName, cpmMethodName, cpmIdName);
		}
		return mimeRespList;
	}

	*//**
	 * Method to check if the request is of type multi part
	 *
	 * @param carsRequest the request obj coming from web
	 * @return true if it is multipart else false
	 *//*
	public static boolean isMultiPartReq(ICARSRequest carsRequest) {
		HttpMethod method = carsRequest.getHTTPMethod();
		if (CARSRequestURL.UPDATE_PROFILE_URL.getUrl().
				equals(carsRequest.getRequestURI()) &&
				(HttpMethod.POST.equals(method) ||
						HttpMethod.PUT.equals(method))) {
			return true;
		}
		return false;
	}

	*//**
	 * This method is used for invoking common rest call and
	 * returns list of mime response
	 *
	 * @param carsURI the cars target uri to be invoked
	 * @param carsRequest the request object with all required param values
	 * @return List<String> the list of mime response as string
	 *//*
	public static List<String> invokeCommonRequest(String carsURI,
			ICARSRequest carsRequest, HttpServletRequest httpReq) {
		ClientResponse clientResponse = null;
		BufferedInMultiPart bufInMultiPart = null;
		List<String> respList = null;
		if (CommonLogger.isDebugEnabled()) {
			CommonLogger.debug(
					CARSStringConstants.INVOKE_START_METHOD.value());
		}
		Resource resource = restClient.resource(carsURI);

		if (null != carsRequest.getRestRequestParams()) {
			for (Entry<RESTRequestParameters, Object> pair : carsRequest
					.getRestRequestParams().entrySet()) {
				if (null != pair && null != pair.getKey()) {
					if (null != pair.getKey().getReqParam()) {
						if (CARSStringConstants.REQUEST_PARAMS_HOLDER.value()
								.equalsIgnoreCase(pair.getKey().getReqParam())) {
							resource.queryParams(convertToMultiValuedMap(pair));
						} else {
							// sometimes we get param with null as value like
							// platform_code=null
							// so have done a null check to avoid any exception
							if (null != pair.getValue()) {
							    String value = null;
							  //UTF-8 Encode all CARS param value 
                                try {                                       
                                    value = URLEncoder.encode(pair.getValue().toString(), "UTF-8");                                      
                                } catch (UnsupportedEncodingException e) {
                                    if (CommonLogger.isInfoEnabled()) {
                                        CommonLogger.info(
                                                "Error occured while encoding" + pair.getKey().getReqParam());
                                    }
                                }   
								resource.queryParam(pair.getKey().getReqParam(),
								        value);
							}
						}
					}
				}
			}
		}

		resource.header(
					CARSConfigVariables.getConstantAsString(
							CARSConfigVariables.X_ELS_AUTHENTICATION),
					CARSConfigVariables.getConstantAsString(
							CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE));
		resource.header(CARSStringConstants.ACCEPT_ENCODING.value(), GZIP);
		CommonLogger.info("Adding ACCEPT_ENCODING in the Header : gzip,deflate");
		resource.header(
				CARSConfigVariables.getConstantAsString(
						CARSConfigVariables.X_CARS_NAMESPACE),
				CARSConfigVariables.getConstantAsString(
						CARSConfigVariables.NAMESPACE_VERSION));
		
		//Set Accept-Language to the header for localization
		//Currently only Scopus will have localization. so if check is needed here
		if (CARSCommonUtil.isFromScopus()) {
			String locale = MessageResourceUtil.getLocaleFromSession(httpReq).toString();
			locale = CARSCommonUtil.convertLanguageToHyphen(locale);
			resource.acceptLanguage(locale);
		}

		String sessionAffinity = carsRequest.getSessionAffinityKey();
		if (null != sessionAffinity) {
			resource.cookie(CARSStringConstants.SESSION_AFFINITY_KEY.value()
					+ CARSStringConstants.EQUAL.value() + sessionAffinity
					+ CARSStringConstants.SEMICOLON.value());
			resource.header(CARSStringConstants.SESSION_AFFINITY_KEY.value(),
					sessionAffinity);
		}
		if (CommonLogger.isInfoEnabled()) {
			CommonLogger.info(CARSStringConstants.CARS_REST_URI.value()
					+ resource.getUriBuilder().build());
		}
		String deleteResponse =null;
		switch (carsRequest.getHTTPMethod()) {
		case POST:
			resource.accept(CARSStringConstants.MULTIPART_MIXED.value());
			bufInMultiPart = resource.post(BufferedInMultiPart.class,
					RestConstants.CHARACTER_ENCODING_UTF_8);
			break;
		case PUT:
			resource.contentType(CARSStringConstants.APPLICATION_CONTENT_TYPE
					.value());
			resource.accept(CARSStringConstants.MULTIPART_MIXED.value());
			bufInMultiPart = resource.put(BufferedInMultiPart.class,
					RestConstants.CHARACTER_ENCODING_UTF_8);
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
			setSessionAffinityCookie(clientResponse);
			break;
		}
		if (respList == null) {
			if (null != clientResponse) {
				CommonLogger.info("Content-Encoding from response InPart : " +clientResponse.getHeaders().getFirst("Content-Encoding"));
				bufInMultiPart = clientResponse
						.getEntity(BufferedInMultiPart.class);
			}
			if (null != bufInMultiPart) {
				
				respList = fetchMIMERespList(bufInMultiPart.getParts());
			}
		}

		if (null != clientResponse) {
			CommonLogger.info("Content-Encoding from response for RestClient CARS call : " +clientResponse.getHeaders().getFirst("Content-Encoding"));
			bufInMultiPart = clientResponse.getEntity(
					BufferedInMultiPart.class);
			setSessionAffinityCookie(clientResponse);
		}

		if (null != bufInMultiPart) {
			respList = fetchMIMERespList(bufInMultiPart.getParts());
		}
		if (CommonLogger.isDebugEnabled()) {
			CommonLogger.debug(
					CARSStringConstants.INVOKE_END_METHOD.value());
		}
		return respList;
	}

	*//**
	 * This method is used for getting the list of mime
	 * response as string
	 *
	 * @param parts the list of InPart
	 * @return list of mime response list as string
	 *//*
	private static List<String> fetchMIMERespList(List<InPart> parts) {
		List<String> mimeRespList = null;
		if (null != parts && parts.size() > 0) {
			mimeRespList = new ArrayList<String>(parts.size());
			for (InPart inPart : parts) {
				if (null != inPart) {					
					// Convert Stream to String and add to list
					mimeRespList.add(convertStreamToString(
							inPart.getInputStream()));
				}
			}
		}
		return mimeRespList;
	}

	*//**
	 * This method is used for invoking rest when the http method is POST/PUT
	 * and the request uri is for registration
	 *
	 * @param carsURI  - the cars request uri
	 * @param carsRequest - the request object from web
	 * @return List - the mime response lists
	 * @throws HttpException - if any http exception occurs
	 * @throws IOException - if any input/output exception occurs
	 *//*
	public static List<String> invokeMultiPartRequest(String carsURI,
			ICARSRequest carsRequest,HttpServletRequest request) throws HttpException, IOException {
		InputStream respStream = null;
		List<Part> partsList = new ArrayList<Part>();
		String contentEncoding = new String();
		List<String> respList = null;
		Part[] parts = null;
		PostMethod postMethod = null;
		PutMethod putMethod = null;
		HttpClient httpClient = new HttpClient();
		//DecompressingHttpClient sh = new DecompressingHttpClient(httpClient);
		
		if (CommonLogger.isDebugEnabled()) {
			CommonLogger.debug(CARSStringConstants.MULTIPART_START_METHOD.value());
			CommonLogger.debug(CARSStringConstants.CARS_POST_URL.value() + carsURI);
		}
		if (null != carsRequest.getRestRequestParams()) {
			//add all request params and parametermap from request
			for (Entry<RESTRequestParameters, Object> pair : carsRequest
					.getRestRequestParams().entrySet()) {
				if (null != pair && null != pair.getKey()) {
					if (CARSStringConstants.REQUEST_PARAMS_HOLDER.value()
							.equalsIgnoreCase(pair.getKey().getReqParam())) {
						for (Entry<Object, Object> paramValue :((Map<Object, Object>) pair
								.getValue()).entrySet()) {
							String[] arrayValue = (String[]) paramValue.getValue();
							for (String value : arrayValue) {
								partsList.add(new StringPart(paramValue.getKey().toString(), value, "UTF-8"));
							}
						}
					}
					if (pair.getValue() != null) {
						partsList.add(new StringPart(pair.getKey()
								.getReqParam(), pair.getValue().toString(), "UTF-8"));
					}
				}
			}
			//add all the file upload content  from the request
			if (request.getAttribute(CARSStringConstants.FILE_PARAMS.value()) != null) {
				Part part;
				Map<String, Object> fileParamsMap = (Map<String, Object>) request
						.getAttribute(CARSStringConstants.FILE_PARAMS.value());
				for (Entry<String, Object> params : fileParamsMap.entrySet()) {
					String key = params.getKey();
					if(key.equals(CARSStringConstants.PROFILE_IMAGE.value())){
						key=CARSStringConstants.PROFILE_PHOTO.value();
					}
					if (params.getValue() != null) {
						part = new FilePart(key, new ByteArrayPartSource(key,
								(byte[]) params.getValue()));
						partsList.add(part);
					}
				}
			}
			parts = partsList.toArray(new Part[partsList.size()]);
		}
		if (null != carsURI && null != parts && parts.length > 0) {
			if (HttpMethod.POST.equals(carsRequest.getHTTPMethod())) {
				postMethod = new PostMethod(carsURI);
				postMethod.setRequestHeader(
						CARSConfigVariables.getConstantAsString(
								CARSConfigVariables.X_ELS_AUTHENTICATION),
						CARSConfigVariables.getConstantAsString(
								CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE));
				postMethod.setRequestHeader(
						CARSConfigVariables.getConstantAsString(
								CARSConfigVariables.X_CARS_NAMESPACE),
						CARSConfigVariables.getConstantAsString(
								CARSConfigVariables.NAMESPACE_VERSION));
				postMethod.setRequestHeader(CARSStringConstants.ACCEPT_ENCODING.value(), GZIP);
				CommonLogger.info("Adding ACCEPT_ENCODING in the Header : gzip,deflate");
				if (CARSCommonUtil.isFromScopus()) {
					String locale = MessageResourceUtil.getLocaleFromSession(request).toString();
					locale = CARSCommonUtil.convertLanguageToHyphen(locale);
					postMethod.setRequestHeader(CARSConfigVariables.getConstantAsString(
						CARSConfigVariables.ACCEPT_LANGUAGE), locale);
				}
				postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
				if (CommonLogger.isDebugEnabled()) {
					CommonLogger.debug(
							CARSStringConstants.MULTIPART_POST_BEFORE.value());
				}
				httpClient.executeMethod(postMethod);
				if (CommonLogger.isDebugEnabled()) {
					CommonLogger.debug(
							CARSStringConstants.MULTIPART_POST_AFTER.value());
				}
				
				if(postMethod != null){
					contentEncoding = postMethod.getResponseHeader("Content-Encoding").getValue();	
					CommonLogger.info("Content-Encoding from response for mutilpart POSTMethod of CARS call : " + contentEncoding);
				}
				if(contentEncoding.indexOf("gzip") != -1)					
					respStream = new GZIPInputStream(postMethod.getResponseBodyAsStream());
				else
					respStream = postMethod.getResponseBodyAsStream();
							
				//respStream = postMethod.getResponseBodyAsStream();
			} else if (HttpMethod.PUT.equals(carsRequest.getHTTPMethod())) {
				putMethod = new PutMethod(carsURI);
				putMethod.setRequestHeader(
						CARSConfigVariables.getConstantAsString(
								CARSConfigVariables.X_ELS_AUTHENTICATION),
						CARSConfigVariables.getConstantAsString(
								CARSConfigVariables.X_ELS_AUTHENTICATION_VALUE));
				putMethod.setRequestHeader(
						CARSConfigVariables.getConstantAsString(
								CARSConfigVariables.X_CARS_NAMESPACE),
						CARSConfigVariables.getConstantAsString(
								CARSConfigVariables.NAMESPACE_VERSION));
				putMethod.setRequestHeader(CARSStringConstants.ACCEPT_ENCODING.value(), GZIP);
				CommonLogger.info("Adding ACCEPT_ENCODING in the Header : gzip,deflate");
				if (CARSCommonUtil.isFromScopus()) {
					String locale = MessageResourceUtil.getLocaleFromSession(request).toString();
					locale = CARSCommonUtil.convertLanguageToHyphen(locale);
					putMethod.setRequestHeader(CARSConfigVariables.getConstantAsString(
						CARSConfigVariables.ACCEPT_LANGUAGE), locale);
				}
				putMethod.setRequestEntity(new MultipartRequestEntity(parts,
						putMethod.getParams()));
				if (CommonLogger.isDebugEnabled()) {
					CommonLogger.debug(
							CARSStringConstants.MULTIPART_PUT_BEFORE.value());
				}
				httpClient.executeMethod(putMethod);
				
			
				if(putMethod != null){
					contentEncoding = putMethod.getResponseHeader("Content-Encoding").getValue();	
					CommonLogger.info("Content-Encoding from response for mutilpart PUTMethod of CARS call : " + contentEncoding);
				}
				
				if (CommonLogger.isDebugEnabled()) {
					CommonLogger.debug(
							CARSStringConstants.MULTIPART_PUT_AFTER.value());
				}
				
				if(contentEncoding.indexOf("gzip") != -1)					
					respStream = new GZIPInputStream(putMethod.getResponseBodyAsStream());
				else
					respStream = putMethod.getResponseBodyAsStream();
				
					//putMethod.getResponseBodyAsStream();
			}
		}
		respList = fetchMIMERespList(respStream);
		if (CommonLogger.isDebugEnabled()) {
			CommonLogger.debug(CARSStringConstants.MULTIPART_END_METHOD.value());
		}
		return respList;
	}

	*//**
	 * Method to convert the cars InputStream to String and
	 * return response list as string
	 *
	 * @param respStream - the InputStream containing MIME responses
	 * @return List<String> - the converted list of mime response
	 *//*
	private static List<String> fetchMIMERespList(InputStream respStream) {
		String respAsString = null;
		List<String> respList = null;
		if (null != respStream) {
			respAsString = convertStreamToString(respStream);
			if (null != respAsString) {
				String[] respStrArr = respAsString.split(
						CARSStringConstants.MIME_SEPARATOR.value());
	        	if (null != respStrArr && respStrArr.length > 0) {
					respList = new ArrayList<String>(respStrArr.length);
					for (String mimeResponse : respStrArr) {
						if (null != mimeResponse) {
							int index = mimeResponse.indexOf(
										CARSStringConstants.LESS_THAN.value());
							if (index != -1) {
								mimeResponse = mimeResponse.substring(
										index, mimeResponse.length());
								respList.add(mimeResponse);
							}
						}
					}
				}
			}
		}
		return respList;
	}

	*//**
	 * This method is used for setting the JSESSIONID in session so that it wil
	 * be used in subsequent CARS and SOAP calls
	 *
	 * @param clientResponse the REST response
	 *//*
	private static void setSessionAffinityCookie(ClientResponse clientResponse) {
		MultivaluedMap<String, String> headerMap = clientResponse.getHeaders();
		String sessionAffinityKey = null;
		String cookieVal = null;
		if (null != headerMap) {
			List<String> cookieValues = headerMap
					.get(CARSStringConstants.SET_COOKIE.value());
			if (null != cookieValues && cookieValues.size() > 0) {
				cookieVal = cookieValues.get(0);
				if (!GenericValidator.isBlankOrNull(cookieVal)) {
					sessionAffinityKey = getJSessionId(cookieVal);
				}
			}
			if (null != sessionAffinityKey) {
				if (CommonLogger.isInfoEnabled()) {
					CommonLogger.info(
							CARSStringConstants.JSESSIONID_AVAIL_MSG.value()
							+ sessionAffinityKey);
				}
				CARSCommonUtil.setValueInSession(
						CARSStringConstants.SESSION_AFFINITY_KEY.value(),
						sessionAffinityKey);
			} else {
				if (CommonLogger.isInfoEnabled()) {
					CommonLogger.info(CARSStringConstants.JSESSIONID_NOT_AVAIL_MSG.value());
				}
			}
		}
	}

	*//**
	 * This method is used for getting the JSESSIONID value from CARS response
	 * header cookie
	 *
	 * @param input the string containing JSESSIONID key and value
	 * @return the JSESSIONID value
	 *//*
	private static String getJSessionId(String input) {
		String jSessionId = null;
		String[] keyValPair = input
				.split(CARSStringConstants.SEMICOLON.value());
		for (String strVal : keyValPair) {
			if (!GenericValidator.isBlankOrNull(strVal)) {
				if (strVal.contains(
						CARSStringConstants.SESSION_AFFINITY_KEY.value())) {
					String[] strArray = strVal.split(
							CARSStringConstants.EQUAL.value());
					if (null != strArray && strArray.length > 0) {
						jSessionId = strArray[1];
						break;
					}
				}
			}
		}
		return jSessionId;
	}

	*//**
	 * This method is used for converting input stream to String
	 *
	 * @param inputStream the input stream to be converted to string
	 * @return xml as string
	 *//*
	private static String convertStreamToString(InputStream inputStream) {
		String xmlAsStr = CARSStringConstants.EMPTY.value();
		if (null != inputStream) {
			StringBuilder strBuilder = new StringBuilder(128);
			InputStreamReader isr = null;
			try {
				isr = new InputStreamReader(inputStream,
						RestConstants.CHARACTER_ENCODING_UTF_8);
				BufferedReader bufReader = new BufferedReader(isr);
				int ch = -1;
				while ((ch = bufReader.read()) > -1) {
					strBuilder.append((char) ch);
				}
			} catch (IOException ioExp) {
				CommonLogger.error(
					CARSErrorEventID.CARS_STREAM_PARSE_ERROR,
					CARSStringConstants.STREAM_PARSE_ERROR.value(),
					ioExp);
			} finally {
				try {
					if (null != isr)
						isr.close();
					if (null != inputStream)
						inputStream.close();
				} catch (IOException ioExp) {
					CommonLogger.error(
							CARSErrorEventID.CARS_STREAM_PARSE_ERROR,
							CARSStringConstants.STREAM_PARSE_ERROR.value(),
							ioExp);
				}
			}
			xmlAsStr = strBuilder.toString();
		}
		return xmlAsStr;
	}

	*//**
	 * This method is used to convert the incoming requestParameter map to
	 * MultivaluedMap which wink supports
	 * Since the Request-parameter-map is Map<String, String[]>, whereas
	 * MultivaluedMap for WINK-supported RESTful call accepts
	 * Map<String, String>, so making a duplicate data copy
	 *
	 * @param Map of type Entry which holds request parameters
	 * @return MultivaluedMap
	 *//*
	@SuppressWarnings("unchecked")
	private static MultivaluedMap<String, String> convertToMultiValuedMap(
			Entry<RESTRequestParameters, Object> pair) {
		MultivaluedMap<String, String> map = new MultivaluedMapImpl<String, String>();
		if (null != pair.getValue()) {
			for (Entry<Object, Object> paramValue : ((Map<Object, Object>) pair
					.getValue()).entrySet()) {
				if (null != paramValue && null != paramValue.getValue()) {
					String[] arrayValue = (String[]) paramValue.getValue();
					if (null != arrayValue && arrayValue.length > 0) {
						for (String value : arrayValue) {
							if (null != paramValue.getKey()) {
								//UTF-8 Encode all CARS param value 
									try {									    
										value = URLEncoder.encode(value, "UTF-8");										
									} catch (UnsupportedEncodingException e) {
										if (CommonLogger.isInfoEnabled()) {
											CommonLogger.info(
													"Error occured while encoding" +paramValue.getKey().toString());
										}
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
}

*//*****************************************************************************

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

   Copyright (c) 2012 by Elsevier, A member of the Reed Elsevier plc
   group.

   All Rights Reserved.

*****************************************************************************/